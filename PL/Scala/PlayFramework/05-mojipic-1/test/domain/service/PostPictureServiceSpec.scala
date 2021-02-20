package domain.service


import com.google.common.net.MediaType
import domain.entity.OriginalPicture
import domain.entity.PictureId
import domain.entity.PictureProperty
import domain.exception.ConversionFailureException
import domain.exception.DatabaseException
import domain.exception.InvalidContentTypeException
import domain.repository.PicturePropertyRepository
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.duration.Duration

class PostPictureServiceSpec extends PlaySpec with MockitoSugar {

  trait Setup {
    val pictureId = PictureId(123L)
    val binary = Array[Byte]()
    val mediaType = MediaType.JPEG
    val picturePropertyValue = PictureProperty.Value(PictureProperty.Status.Converting, null, null, mediaType, null, 0, null)
    val originalPicture = OriginalPicture(pictureId, binary)

    val mockedConvertPictureService = mock[ConvertPictureService]
    val mockedPicturePropertyRepository = mock[PicturePropertyRepository]
    val sut = new PostPictureService(mockedConvertPictureService, mockedPicturePropertyRepository, ExecutionContext.global)
  }

  "PostPictureService#post" should {
    "save PictureProperty and convert a picture" in new Setup {
      when(mockedPicturePropertyRepository.create(picturePropertyValue)).thenReturn(Future.successful(pictureId))
      when(mockedConvertPictureService.convert(originalPicture)).thenReturn(Future.successful(()))
      val actual = sut.post(binary, picturePropertyValue)
      assert(Await.result(actual, Duration.Inf) === (()))
      verify(mockedPicturePropertyRepository, times(1)).create(picturePropertyValue)
      verify(mockedConvertPictureService, times(1)).convert(originalPicture)
    }

    "return Future.failed(InvalidContentTypeException) if Content-Type is invalid" in new Setup {
      val invalidMediaType: MediaType = MediaType.parse("text/html")
      val actual = sut.post(binary, picturePropertyValue.copy(contentType = invalidMediaType))
      intercept[InvalidContentTypeException] {
        Await.result(actual, Duration.Inf)
      }
      verify(mockedPicturePropertyRepository, never()).create(ArgumentMatchers.any())
      verify(mockedConvertPictureService, never()).convert(ArgumentMatchers.any())
    }

    "return Future.failed(DatabaseException) if PicturePropertyRepository returns Future.failed(DatabaseException)" in new Setup {
      when(mockedPicturePropertyRepository.create(picturePropertyValue)).thenReturn(Future.failed(DatabaseException("test")))
      val actual = sut.post(binary, picturePropertyValue)
      intercept[DatabaseException] {
        Await.result(actual, Duration.Inf)
      }
      verify(mockedPicturePropertyRepository, times(1)).create(picturePropertyValue)
      verify(mockedConvertPictureService, never()).convert(ArgumentMatchers.any())
    }

    "return Future.failed(ConversionFailureException) if it failed to convert" in new Setup {
      when(mockedPicturePropertyRepository.create(picturePropertyValue)).thenReturn(Future.successful(pictureId))
      when(mockedConvertPictureService.convert(originalPicture)).thenReturn(Future.failed(ConversionFailureException("Test")))
      when(mockedPicturePropertyRepository.updateStatus(pictureId, PictureProperty.Status.Failure)).thenReturn(Future.successful(()))
      val actual = sut.post(binary, picturePropertyValue)
      intercept[ConversionFailureException] {
        Await.result(actual, Duration.Inf)
      }
      verify(mockedPicturePropertyRepository, times(1)).create(picturePropertyValue)
      verify(mockedConvertPictureService, times(1)).convert(originalPicture)
      verify(mockedPicturePropertyRepository, times(1)).updateStatus(pictureId, PictureProperty.Status.Failure)
    }
  }
}
