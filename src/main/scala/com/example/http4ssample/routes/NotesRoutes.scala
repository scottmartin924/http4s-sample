package com.example.http4ssample.routes

import org.http4s.HttpRoutes
import com.example.http4ssample.service.Notes
import org.http4s.dsl.Http4sDsl
import cats.effect.IO
import org.http4s.circe.CirceEntityEncoder.circeEntityEncoder

object NotesRoutes {
  def routes(notes: Notes): HttpRoutes[IO] = {
    val dsl = new Http4sDsl[IO] {}
    import dsl._
    HttpRoutes.of[IO] {
      case GET -> Root / "note" =>
        for {
          notes <- notes.get()
          resp <- Ok(notes)
        } yield resp
      case POST -> Root / "note" =>
        for {
          updates <- notes.add("my content", 3, "a thing")
          resp <- Ok(s"$updates records updated")
        } yield resp
    }
  }
}
