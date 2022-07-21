package com.example.http4ssample

import cats.effect.Concurrent
import cats.implicits._
import io.circe.{Encoder, Decoder}
import io.circe.generic.semiauto._
import org.http4s._
import org.http4s.implicits._
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.circe._
import org.http4s.Method._
import io.circe.Codec
import cats.effect.IO

trait Jokes {
  def get: IO[Jokes.Joke]
}

object Jokes {
  opaque type Joke = String
  object Joke {
    given jokeDecoder: Decoder[Joke] = Decoder.decodeString
    given jokeEntityDecoder[F[_]: Concurrent]: EntityDecoder[F, Joke] =
      jsonOf
    given jokeEncoder: Encoder[Joke] = Encoder.encodeString
    given jokeEntityEncoder[F[_]]: EntityEncoder[F, Joke] =
      jsonEncoderOf
  }

  final case class JokeError(e: Throwable) extends RuntimeException

  def impl(C: Client[IO]): Jokes = new Jokes {
    val dsl = new Http4sClientDsl[IO] {}
    import dsl._
    def get: IO[Jokes.Joke] = {
      C.expect[Joke](GET(uri"https://icanhazdadjoke.com/"))
        .adaptError { case t =>
          JokeError(t)
        } // Prevent Client Json Decoding Failure Leaking
    }
  }
}
