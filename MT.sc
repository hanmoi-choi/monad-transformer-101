import cats.data.{EitherT, OptionT}
import cats.effect.IO
import cats.implicits._


// Level 1 - Single Monad
val ioString: IO[String] = IO("foo")

// Level 1.5 - Single Monad without MT
val ioEitherString: IO[Either[String, String]] = IO("right".asRight)

// Level 2 - MT with 2 Monads
val eitherTofIOString: EitherT[IO, String, String] = EitherT(IO("right".asRight))

eitherTofIOString.flatMap { _: String => ??? }


// Level 3 - MT with 3 Monads
// Really do you like to do this?
// I like to think of looking for other type classes like ZIO
type OptionTOfIO[A] = OptionT[IO, A]
type EitherTOfIO[A] = EitherT[IO, String, A]

val optionTofEitherTofIOString: OptionT[EitherTOfIO, String] =
  OptionT(EitherT(IO("right".asRight).map(_.map(_.some))))

optionTofEitherTofIOString.flatMap { _: String => ???}

val eitherTOfOptionTofIOString: EitherT[OptionTOfIO, String, String] =
  EitherT(OptionT(IO("some".some).map(_.map(_.asRight[String]))))

eitherTOfOptionTofIOString.flatMap { _: String => ??? }


// Confusing
// These are different ones from above example
val optionTofIOEitherString: OptionT[IO, Either[String, String]] =
  OptionT(IO("right".asRight.some))

optionTofIOEitherString.flatMap { _: Either[String, String] => ??? }

val eitherTOfIOOptionString: EitherT[IO, String, Option[String]] =
  EitherT(IO("right".some.asRight))

eitherTOfIOOptionString.flatMap { _: Option[String] => ??? }

