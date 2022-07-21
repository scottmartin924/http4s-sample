package com.example.http4ssample

import cats.effect.{Async, Resource}
import cats.syntax.all._
import com.comcast.ip4s._
import fs2.Stream
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.middleware.Logger
import cats.effect.IO
import com.example.http4ssample.routes
import com.example.http4ssample.db.Database
import com.example.http4ssample.routes.NotesRoutes
import com.example.http4ssample.config._
import com.example.http4ssample.repository.NoteRepository
import com.example.http4ssample.db._
import com.example.http4ssample.service._

object Http4ssampleServer {

  def stream: Stream[IO, Nothing] = {
    for {
      client <- Stream.resource(EmberClientBuilder.default[IO].build)
      config <- Stream.resource(Config.load("abc"))
      xa <- Stream.resource(Database.instantiate(config.db))

      repo = NoteRepository(DoobieExecutor(xa))
      helloWorldAlg = HelloWorld.impl
      jokeAlg = Jokes.impl(client)
      notesAlg = Notes.impl(repo)

      // Combine Service Routes into an HttpApp.
      // Can also be done via a Router if you
      // want to extract segments not checked
      // in the underlying routes.
      httpApp = (
        Http4ssampleRoutes.helloWorldRoutes(helloWorldAlg) <+>
          Http4ssampleRoutes.jokeRoutes(jokeAlg) <+>
          NotesRoutes.routes(notesAlg)
      ).orNotFound

      // With Middlewares in place
      finalHttpApp = Logger.httpApp(true, true)(httpApp)

      exitCode <- Stream.resource(
        EmberServerBuilder
          .default[IO]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"8080")
          .withHttpApp(finalHttpApp)
          .build >>
          Resource.eval(Async[IO].never)
      )
    } yield exitCode
  }.drain
}
