package com.reactivespot.lala.logic

import java.util

import com.reactivespot.lala.db.PlaylistDAO
import com.reactivespot.lala.domain._
import com.reactivespot.lala.service.MusicService
import com.reactivespot.lala.util.Utils
import com.redis.RedisClient

import org.apache.log4j.Logger


class MusicServiceImpl(
                        playlistDAO: PlaylistDAO,
                        redisClient: RedisClient
                      )
  extends MusicService {

  val logger = Logger.getLogger(classOf[MusicServiceImpl])

  override def getCurrentPlaylist(companyID: String): Option[CurrentPlaylist] = {
    logger.info(f"Get current playlist from: $companyID")

    val currentSong = redisClient.get(companyID + "_song")
    currentSong  match {
      case None => return None
      case Some(_) => ;
    }

    redisClient.get(companyID + "_playlist") match {
      case Some(playlistID) => {
        val playlist = playlistDAO.readPlayList(playlistID).get

        val idVT: List[(String, String)] =
          (redisClient.keys(companyID + "_vote_*") match {
            case Some(keys) => Some(keys map { key =>
              val value = redisClient.get(key.get).get
              (key.get, value)
            })
            case None => None
          }).getOrElse(List())

        var pozList = scala.collection.mutable.ListBuffer[(String, (Int, Long))]()
        var negList = scala.collection.mutable.ListBuffer[(String, Long)]()

        idVT map { k =>
          val songID: String = k._1.split("_")(2)
          val tmp = k._2.split(";")
          val votes: Int = Integer.parseInt(tmp(0))
          val last: Long = tmp(1).toLong

          if(votes>0) {
            pozList += ((songID, (votes, last)))
          } else {
            negList += ((songID, last))
          }
        }

        val pozA = pozList.toArray
        val negA = negList.toArray

        scala.util.Sorting.stableSort(pozA, (e1: (String, (Int, Long)), e2: (String, (Int, Long))) =>
          if(e1._2._1 != e2._2._1) e1._2._1 > e2._2._1
          else e1._2._2 < e2._2._2
        )
        scala.util.Sorting.stableSort(negA, (e1: (String, Long), e2: (String, Long)) => e1._2 < e2._2)

        val songsByID = new util.HashMap[String, Song]()
        playlist.songs map { s =>
          songsByID.put(s.id, s)
        }
        val ids =
          playlist.songs map { s =>
            s.id
          }

        val orderedSongs = scala.collection.mutable.ListBuffer[SongWVotes]()
        pozA map { s =>
          val os = songsByID.get(s._1)
          val ns = SongWVotes(os.id, os.title, os.author, s._2._1)
          orderedSongs += ns
        }

        val unwanted1 = (pozA map {_._1}).toSet
        val unwanted2 = (negA map {_._1}).toSet
        val remaining = ids.filterNot(unwanted1).filterNot(unwanted2)

        remaining map { s =>
          val os = songsByID.get(s)
          val ns = SongWVotes(os.id, os.title, os.author, 0)
          orderedSongs += ns
        }

        negA map { s =>
          val os = songsByID.get(s._1)
          val ns = SongWVotes(os.id, os.title, os.author, 0)
          orderedSongs += ns
        }

        val csID = currentSong.get.split(";")(0)
        val csStart = currentSong.get.split(";")(1).toLong
        val diff = (System.currentTimeMillis() - csStart)/1000
        val currentPlaylist = CurrentPlaylist(playlist.id, playlist.name, playlist.companyID, orderedSongs.toList, csID, diff.toInt)

        Some(currentPlaylist)
      }
      case None => None
    }
  }

  override def addPlaylist(playlist: Playlist): String = {
    logger.info(f"Company ${playlist.companyID} adds a playlist with ${playlist.songs.size} songs")

    val id = Utils.nextSessionId()
    val playlistDB = playlist.copy(id = id)
    playlistDAO.createPlayList(playlistDB)
    id
  }

  override def getPlaylists(companyID: String): List[Playlist] = {
    logger.info(f"Company $companyID has requested all his playlist")

    playlistDAO.readCompaniesPlaylists(companyID)
  }

  override def getPlaylist(companyID: String, playlistID: String): Option[Playlist] = {
    logger.info(f"Company $companyID has requested his playlist: $playlistID")

    playlistDAO.readPlayList(playlistID) match {
      case Some(p) => if(p.companyID.equals(companyID)) Some(p) else None
      case None => None
    }
  }

  override def addSong(command: AddSong): Unit = {
    logger.info(f"Company ${command.companyID} adds a song to playlist ${command.playlistID}")

    val playlist = playlistDAO.readPlayList(command.playlistID).get
    val newSongs: List[Song] = playlist.songs :+ command.song
    val newPlaylist = playlist.copy(songs = newSongs)
    playlistDAO.updatePlayList(newPlaylist.id, newPlaylist)
  }

  override def removeSong(command: RemoveSong): Unit = {
    logger.info(f"Company ${command.companyID} removes a song to playlist ${command.playlistID}")

    val playlist = playlistDAO.readPlayList(command.playlistID).get
    val newSongs: List[Song] = playlist.songs.filter(!_.id.equalsIgnoreCase(command.songID))
    val newPlaylist = playlist.copy(songs = newSongs)
    playlistDAO.updatePlayList(newPlaylist.id, newPlaylist)
  }

  override def startPlaylist(companyID: String, playlistID: String): Unit = {
    logger.info(f"Company: $companyID has started playlist: $playlistID")
    redisClient.set(companyID + "_playlist", playlistID)
  }

  override def stopPlaylist(companyID: String, playlistID: String): Unit = {
    logger.info(f"Company: $companyID has stop playlist: $playlistID")
    redisClient.del(companyID + "_playlist")

    val keys = redisClient.keys(companyID + "_*")
    keys.get foreach {key =>
      redisClient.del(key.get)
    }
  }

  override def startSong(companyID: String, songID: String): Unit = {
    val key = companyID + "_vote_" + songID
    val now = System.currentTimeMillis()
    val value = "-1;" + now

    redisClient.set(key, value)

    val keyStart = companyID + "_song"
    redisClient.set(keyStart, songID + ";" + now)
  }

  override def voteSong(companyID: String, songID: String): Unit = {
    redisClient.get(companyID + "_playlist") match {
      case None => return;
      case Some(_) => ;
    }

    val key = companyID + "_vote_" + songID
    val now = System.currentTimeMillis()

    val value =
      redisClient.get(key) match {
        case Some(v) => {
          val oldNR = Integer.parseInt(v.split(";")(0))

          val nr = if(oldNR>0) {oldNR + 1} else {1}
          nr + ";" + now
        }
        case None => {
          1 + ";" + now
        }
      }

    redisClient.set(key, value)
  }

}
