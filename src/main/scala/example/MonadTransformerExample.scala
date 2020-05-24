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
   */


  def processArgsWithoutMonadTransformerInForExpression(args: List[String]): IO[Either[String, Int]] = {
    for {
      eitherArgsCheckedSize <- Util.checkArgsSize(args) flatMap {
          case Left(e) => IO(e.asLeft)
          case Right(value) => for {
            eitherParsedArgs <- Util.parseArgs(value) flatMap {
              case Left(e) => IO(e.asLeft)
              case Right(parsedArgs) => Util.multiply1000(parsedArgs)
            }
          } yield eitherParsedArgs
      }
    } yield eitherArgsCheckedSize
  }

  def processArgsWithoutMonadTransformer(args: List[String]): IO[Either[String, Int]] = {
    Util.checkArgsSize(args) flatMap { eitherArgsCheckedSize =>
      eitherArgsCheckedSize match {
        case Left(e) => IO(e.asLeft)
        case Right(value) => Util.parseArgs(value) flatMap { eitherParsedArgs =>
          eitherParsedArgs match {
            case Left(e) => IO(e.asLeft)
            case Right(parsedArgs) => Util.multiply1000(parsedArgs)
          }
        }
      }
    }
  }

  def processArgsWithMonadTransformer(args: List[String]): IO[Either[String, Int]] = {
    val processedArgs: EitherT[IO, String, Int] = for {
      argsCheckedSize <- EitherT(Util.checkArgsSize(args))
      parsedArgs <- EitherT(Util.parseArgs(argsCheckedSize))
      multipliedArgs <- EitherT(Util.multiply1000(parsedArgs))
    } yield multipliedArgs

    processedArgs.value
  }

  def run(args: List[String]): IO[ExitCode] = {
    val valueOfProcessedArgs: IO[Either[String, Int]] = processArgsWithoutMonadTransformer(args)

    valueOfProcessedArgs.flatMap {
      case Left(error) => IO(print(s"Error Message $error")).as(ExitCode.Error)
      case Right(value) => IO(println(s"Result: $value")).as(ExitCode.Success)
    }
  }
}
