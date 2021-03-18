import org.scalatest.time.SpanSugar._
import org.scalatest.{FlatSpec, DiagrammedAssertions}
import org.scalatest.concurrent.Timeouts
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito._

class CalcSpec extends FlatSpec 
    with DiagrammedAssertions 
    with Timeouts 
    with MockitoSugar {

    val calc = new Calc

    "`sum` method" should "take a sequence of integers and return a summed value" in {
        assert(calc.sum(Seq(1, 2, 3)) === 6)
        assert(calc.sum(Seq(0)) === 0)
        assert(calc.sum(Seq(-1, 1)) === 0)
        assert(calc.sum(Seq()) === 0)
    }

    it should "overflow if the sum goes beyond the maximum value of Int" in {
        assert(calc.sum(Seq(Integer.MAX_VALUE, 1)) === Integer.MIN_VALUE)
    }

    "`div` method" should "take two integers, divide the first value by the second, and return the divided value" in {
        assert(calc.div(6, 3) === 2.0)
        assert(calc.div(1, 3) === 0.3333333333333333)
    }

    it should "throw and exception if the denominator is 0" in {
        intercept[ArithmeticException] {
            calc.div(1, 0)
        }
    }

    "`isPrime` method" should "check if the passed value is a prime number" in {
        assert(calc.isPrime(0) === false)
        assert(calc.isPrime(-1) === false)
        assert(calc.isPrime(2))
        assert(calc.isPrime(17))
    }

    it should "check if the passed value that is below 1000000 is prime within 1 second" in {
        failAfter(1000 millis) {
            assert(calc.isPrime(9999991))

        }
    }

    "Mocked Calc object" should "be able to mock the behavior of the Calc object" in {
        val mockCalc = mock[Calc]

        when(mockCalc.sum(Seq(3, 4, 5))).thenReturn(12)
        assert(mockCalc.sum(Seq(3, 4, 5)) === 12)

    }
}