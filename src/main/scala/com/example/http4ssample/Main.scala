package com.example.http4ssample

import cats.effect.{ExitCode, IO, IOApp}

object Main extends IOApp {
  // For now removed all F[_] and just made IO, but someday we'll want to put that back
  def run(args: List[String]) =
    Http4ssampleServer.stream.compile.drain.as(ExitCode.Success)
}
