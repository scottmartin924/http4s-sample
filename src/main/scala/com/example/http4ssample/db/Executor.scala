package com.example.http4ssample.db

import doobie._
import doobie.implicits._
import doobie.util.transactor.Transactor
import cats.effect.IO
import doobie.free.connection.ConnectionIO

// In future executor here could be a typeclass w/ F
trait Executor {
  def execute[A](io: ConnectionIO[A]): IO[A]
}

class DoobieExecutor(xa: Transactor[IO]) extends Executor {
  override def execute[A](io: ConnectionIO[A]): IO[A] = io.transact(xa)
}
