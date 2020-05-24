package example

import cats.implicits._
import cats.effect.IO

import scala.util.{Failure, Success, Try}

object Util {
  private[example] def checkArgsSize(args: List[String]): IO[Either[String, String]] = args match {
    case a :: Nil => IO(a.asRight)
    case _ => IO("The size of input should be 1".asLeft)
  }

  private[example] def parseArgs(args: String): IO[Either[String, Int]] =
    IO {
      Try(args.toInt) match {
        case Failure(_) => "The input should be 'numeric string'".asLeft
        case Success(value) => value.asRight
      }
    }

  private[example] def multiply1000(args: Int): IO[Either[String, Int]] =
    IO((args * 1000).asRight)

  private[example] def checkResult(result: Int): IO[Either[String, String]] =
    IO {
      if (result >= 10000000)
        s"$result is too big".asLeft
      else
        s"All calculation is done well and your result is $result".asRight
    }

}
