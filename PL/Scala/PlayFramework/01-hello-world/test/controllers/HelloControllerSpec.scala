package controllers

import org.scalatestplus.play.PlaySpec
import play.api.test.FakeRequest
import play.api.test.Helpers._

class HelloControllerSpec extends PlaySpec {

    def controller = new HelloController(stubControllerComponents())

    "get" should {
        "show `Hello, namae!` when a name query parameter is set to `namae`" in {
            val name = "namae"
            val result = controller.get(Some(name))(FakeRequest())
            assert(status(result) === 200)
            assert(contentAsString(result) === s"Hello, $name!")
        }

        """show `Please give a name as a query parameter named "name".` if a name query parameter is absent""" in {
            val result = controller.get(None)(FakeRequest())
            assert(status(result) === 200)
            assert(contentAsString(result) === """Please give a name as a query parameter named "name".""")
        }
    }
}