package com.example.http4ssample.db

import com.example.http4ssample.config._
import cats.effect.kernel.Resource
import cats.effect.IO
import doobie.h2._
import doobie.util.ExecutionContexts
import doobie.hikari.HikariTransactor
import doobie.util.transactor.Transactor
import org.flywaydb.core.Flyway

object Database {
  // FIXME Throughout I don't think we should have to specify h2transactor, but atm required for flyway config
  def instantiate(config: DatabaseConfig): Resource[IO, Transactor[IO]] = {
    for {
      xa <- transactor(config)
      _ <- Resource.eval(initialize(xa))
    } yield xa
  }

  def transactor(
      config: DatabaseConfig
  ): Resource[IO, H2Transactor[IO]] =
    for {
      ce <- ExecutionContexts.fixedThreadPool[IO](config.threadCount)
      xa <- H2Transactor.newH2Transactor[IO](
        config.host,
        config.user,
        config.password,
        ce
      )
    } yield xa

  def initialize(transactor: H2Transactor[IO]): IO[Unit] =
    transactor.configure { ds =>
      IO {
        val flyway = Flyway.configure().dataSource(ds).load()
        flyway.migrate()
      }.void *> IO(println("ran migrate"))
    }
}
