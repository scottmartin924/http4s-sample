package com.example.http4ssample.service

import java.util.UUID
import cats.effect.IO
import com.example.http4ssample.service.Notes.Note
import com.example.http4ssample.repository.NoteRepository
import io.circe._
import io.circe.generic.semiauto._
import org.http4s._

import org.http4s.circe._

trait Notes {
  def get(): IO[List[Note]]
  def add(content: String, priority: Int, label: String): IO[Int]
}

object Notes {
  // I'm not sure where to put this...organization here is tricky for me?
  case class Note(
      id: UUID,
      content: String,
      priority: Option[Int],
      label: Option[String],
      completed: Boolean = false
  )

  object Note {
    given noteDecoder: Decoder[Note] = deriveDecoder[Note]
    given noteEntityDecoder: EntityDecoder[IO, Note] = jsonOf
    given noteEncoder: Encoder[Note] = deriveEncoder[Note]
    given noteEntityEncoder: EntityEncoder[IO, Note] = jsonEncoderOf
  }

  def impl(repo: NoteRepository): Notes = new Notes {
    // FIXME Eventually make this not just a pass through
    def get(): IO[List[Note]] = repo
      .get()
      .onError(e => IO(e.printStackTrace()))
    def add(content: String, priority: Int, label: String): IO[Int] =
      repo.add(content: String, priority: Int, label: String)
  }
}
