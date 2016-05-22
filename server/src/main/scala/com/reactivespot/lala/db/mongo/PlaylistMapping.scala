package com.reactivespot.lala.db.mongo

import com.reactivespot.lala.domain.{Song, Playlist}
import reactivemongo.bson.{BSONArray, BSONDocumentReader, BSONDocument, BSONDocumentWriter}

import scala.util.{Failure, Success}

trait PlaylistMapping {

  implicit object SongWriter extends BSONDocumentWriter[Song] {
    def write(song: Song): BSONDocument =
      BSONDocument(
        "_id" -> song.id,
        "title" -> song.title,
        "author" -> song.author
      )
  }

  implicit object SongReader extends BSONDocumentReader[Song] {
    def read(doc: BSONDocument): Song = {
      Song(
        doc.getAs[String]("_id").get,
        doc.getAs[String]("title"),
        doc.getAs[String]("author")
      )
    }
  }

  implicit object PlaylistWriter extends BSONDocumentWriter[Playlist] {
    def write(playlist: Playlist): BSONDocument =
      BSONDocument(
        "_id" -> playlist.id,
        "name" -> playlist.name,
        "company_id" -> playlist.companyID,
        "songs" -> playlist.songs.map {
          song => SongWriter.write(song)
        }
      )
  }

  implicit object PlaylistReader extends BSONDocumentReader[Playlist] {
    def read(doc: BSONDocument): Playlist = {
      Playlist(
        doc.getAs[String]("_id").get,
        doc.getAs[String]("name").get,
        doc.getAs[String]("company_id").get,
        doc.getAs[BSONArray]("songs").get.iterator.toList.map { song =>
          song match {
            case Success(s) => SongReader.read(s._2.asInstanceOf[BSONDocument])
            case Failure(error) => throw new RuntimeException("Unexpected error")
          }
        }
      )
    }
  }

}
