package com.example.http4ssample.repository

import com.example.http4ssample.db.Executor
import doobie._
import doobie.implicits._
import doobie.h2.implicits._
import java.util.UUID
import com.example.http4ssample.service.Notes.Note

class NoteRepository(executor: Executor) {
  // FIXME Not so sure about this terminology
  def get() = executor.execute(NoteRepository.getAll)
  def add(content: String, priority: Int, label: String) =
    executor.execute(NoteRepository.addNote(content, priority, label))
}

// Not sure if it's best practice, but I like keeping the SQL in the object here
// that way it can be used in other places if needed and composed into transactions...maybe
// could even have a separate TodoSql file or something...not sure what's best
object NoteRepository {
  def getAll: ConnectionIO[List[Note]] =
    fr"""select * from note""".query[Note].to[List]

  // FIXME Make this take in a note to create and return the created note
  def addNote(
      content: String,
      priority: Int,
      label: String
  ): ConnectionIO[Int] =
    sql"""insert into note(id, content, priority, label, completed)
    values(${UUID.randomUUID()}, $content, $priority, $label, false)
    """.update.run
}
