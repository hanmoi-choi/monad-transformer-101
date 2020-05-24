package example

import cats.data.EitherT
import cats.effect._
import cats.implicits._

import scala.util.{Failure, Success, Try}

object MonadTransformerExample extends IOApp {
  /*
   * 1. Read User Input as Arguments
   * 2. If the size of argumnet != 1 print error message "The size of input should be 1"
   * 3. else
   *    a. the input is numeric string then * 1000
   *    b. else print error message, "The input should be 'numeric string'"
   * 4. if result >= 10000000
   *    a. then Left("$result is too big")
   *    b. else Right("All calculation is done well and your result is $result"
   */


  def processArgsWithoutMonadTransformerInForExpression(args: List[String]): IO[Either[String, String]] = {
    for {
      eitherArgsCheckedSize <- Util.checkArgsSize(args) flatMap {
          case Left(e) => IO(e.asLeft)
          case Right(value) => for {
            eitherParsedArgs <- Util.parseArgs(value) flatMap {
              case Left(e) => IO(e.asLeft)
              case Right(parsedArgs) => for {
                multipliedArgs <- Util.multiply1000(parsedArgs) flatMap {
                  case Left(e) => IO(e.asLeft)
                  case Right(multipliedArgs) => Util.checkResult(multipliedArgs)
                }
              } yield multipliedArgs
            }
          } yield eitherParsedArgs
      }
    } yield eitherArgsCheckedSize
  }

  def processArgsWithoutMonadTransformer(args: List[String]): IO[Either[String, String]] = {
    Util.checkArgsSize(args) flatMap { eitherArgsCheckedSize =>
      eitherArgsCheckedSize match {
        case Left(e) => IO(e.asLeft)
        case Right(value) => Util.parseArgs(value) flatMap {
          case Left(e) => IO(e.asLeft)
          case Right(parsedArgs) => Util.multiply1000(parsedArgs) flatMap  {
            case Left(e) => IO(e.asLeft)
            case Right(multipliedArgs) => Util.checkResult(multipliedArgs)
          }
        }
      }
    }
  }

  def processArgsWithMonadTransformer(args: List[String]): IO[Either[String, String]] = {
    val processedArgs: EitherT[IO, String, String] =
      for {
        argsCheckedSize <- EitherT(Util.checkArgsSize(args))
        parsedArgs <- EitherT(Util.parseArgs(argsCheckedSize))
        multipliedArgs <- EitherT(Util.multiply1000(parsedArgs))
        result <- EitherT(Util.checkResult(multipliedArgs))
      } yield result

    processedArgs.value
  }

  def run(args: List[String]): IO[ExitCode] = {
    val valueOfProcessedArgs: IO[Either[String, String]] = processArgsWithoutMonadTransformer(args)

    valueOfProcessedArgs.flatMap {
      case Left(error) => IO(print(s"Error Message $error")).as(ExitCode.Error)
      case Right(value) => IO(println(s"Result: $value")).as(ExitCode.Success)
    }
  }
}
