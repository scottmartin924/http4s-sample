package com.example.http4ssample.config

import cats.effect.kernel.Resource
import cats.effect.IO

final case class DatabaseConfig(
    host: String,
    user: String,
    password: String,
    threadCount: Int
)
final case class ServerConfig(port: Int)

final case class ApplicationConfig(db: DatabaseConfig, server: ServerConfig)

object Config {
  def load(file: String): Resource[IO, ApplicationConfig] = {
    // FIXME Don't hardcode...just doing this since can't get pureconfig to import
    Resource.pure(
      ApplicationConfig(
        DatabaseConfig(
          host = "jdbc:h2:mem:todo;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
          user = "sa",
          password = "",
          threadCount = 8
        ),
        ServerConfig(port = 8080)
      )
    )
  }
}
