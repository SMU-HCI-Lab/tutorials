package domain.service

import domain.entity.ConvertedPicture
import domain.entity.PictureId
import domain.entity.PictureProperty
import domain.exception.ConversionFailureException
import domain.exception.ConvertingException
import domain.exception.DatabaseException
import domain.exception.PictureNotFoundException
import domain.repository.ConvertedPictureRepository
import domain.repository.PicturePropertyRepository
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.duration.Duration

class GetPictureServiceSpec extends PlaySpec with MockitoSugar {

  trait Setup {
    val pictureId = PictureId(123L)
    val convertedPicture = ConvertedPicture(pictureId, Array())
    val pictureProperty = PictureProperty(pictureId, PictureProperty.Value(PictureProperty.Status.Success, null, null, null, null, 0, null))

    val mockedConvertedPictureRepository = mock[ConvertedPictureRepository]
    val mockedPicturePropertyRepository = mock[PicturePropertyRepository]

    val sut = new GetPictureService(mockedConvertedPictureRepository, mockedPicturePropertyRepository, ExecutionContext.global)
  }

  "GetPictureService#get" should {
    "get ConvertedPicture and PictureProperty" in new Setup {
      when(mockedPicturePropertyRepository.find(pictureId)).thenReturn(Future.successful(pictureProperty))
      when(mockedConvertedPictureRepository.find(pictureId)).thenReturn(Future.successful(convertedPicture))

      val actual = sut.get(pictureId)
      assert(Await.result(actual, Duration.Inf) === ((convertedPicture, pictureProperty)))
      verify(mockedPicturePropertyRepository, times(1)).find(pictureId)
      verify(mockedConvertedPictureRepository, times(1)).find(pictureId)
    }

    "return Future.failed(ConversionFailureException) if PictureProperty.Status is Failure" in new Setup {
      val picturePropertyFailure = PictureProperty(pictureId, PictureProperty.Value(PictureProperty.Status.Failure, null, null, null, null, 0, null))
      when(mockedPicturePropertyRepository.find(pictureId)).thenReturn(Future.successful(picturePropertyFailure))
      val actual = sut.get(pictureId)
      intercept[ConversionFailureException] {
        Await.result(actual, Duration.Inf)
      }
      verify(mockedPicturePropertyRepository, times(1)).find(pictureId)
      verify(mockedConvertedPictureRepository, never()).find(ArgumentMatchers.any())
    }

    "return Future.failed(ConvertingException) if PictureProperty.Status is Converting" in new Setup {
      val picturePropertyFailure = PictureProperty(pictureId, PictureProperty.Value(PictureProperty.Status.Converting, null, null, null, null, 0, null))
      when(mockedPicturePropertyRepository.find(pictureId)).thenReturn(Future.successful(picturePropertyFailure))
      val actual = sut.get(pictureId)
      intercept[ConvertingException] {
        Await.result(actual, Duration.Inf)
      }
      verify(mockedPicturePropertyRepository, times(1)).find(pictureId)
      verify(mockedConvertedPictureRepository, never()).find(ArgumentMatchers.any())
    }

    "return Future.failed(PictureNotFoundException) if PictureProperty is not found" in new Setup {
      when(mockedPicturePropertyRepository.find(pictureId)).thenReturn(Future.failed(PictureNotFoundException("test")))
      val actual = sut.get(pictureId)
      intercept[PictureNotFoundException] {
        Await.result(actual, Duration.Inf)
      }
      verify(mockedPicturePropertyRepository, times(1)).find(pictureId)
      verify(mockedConvertedPictureRepository, never()).find(ArgumentMatchers.any())
    }

    "return Future.failed(DatabaseException) if ConvertedPictureRepository return Future.failed(DatabaseException)" in new Setup {
      when(mockedPicturePropertyRepository.find(pictureId)).thenReturn(Future.successful(pictureProperty))
      when(mockedConvertedPictureRepository.find(pictureId)).thenReturn(Future.failed(DatabaseException("test")))
      val actual = sut.get(pictureId)
      intercept[DatabaseException] {
        Await.result(actual, Duration.Inf)
      }
      verify(mockedPicturePropertyRepository, times(1)).find(pictureId)
      verify(mockedConvertedPictureRepository, times(1)).find(pictureId)
    }
  }
}
