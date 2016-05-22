package com.reactivespot.lala.api

import akka.actor.ActorRefFactory
import com.reactivespot.lala.domain._
import com.reactivespot.lala.service.MusicService
import spray.http.{StatusCodes, MediaTypes}
import spray.routing._

class MusicAPI(musicService: MusicService)
  (implicit actorRefFactoryParam: ActorRefFactory)
  extends HttpService
  with MusicJSONFormats {

  val route: Route =
    pathPrefix("music") {
      path("current_playlist" / "[^/]+".r) { companyID =>
        get {
          complete(musicService.getCurrentPlaylist(companyID))
        }
      } ~
      path("playlists") {
        get {
          parameters('company_id.as[String]) { companyID =>
            complete(musicService.getPlaylists(companyID))
          }
        }
      } ~
      path("playlist") {
        get {
          parameters('company_id.as[String], 'playlist_id.as[String]) { (companyID, playlistID) =>
            complete(musicService.getPlaylist(companyID, playlistID))
          }
        } ~
        post {
          entity(as[Playlist]) { playlist =>
            respondWithMediaType(MediaTypes.`application/json`) { ctx =>
              ctx.complete(SuccessMessage(musicService.addPlaylist(playlist)))
            }
          }
        }
      } ~
      path("song") {
        post {
          entity(as[AddSong]) { command =>
            respondWithMediaType(MediaTypes.`application/json`) { ctx =>
              musicService.addSong(command)
              complete("added")
            }
          }
        } ~
        post {
          entity(as[RemoveSong]) { command =>
            musicService.removeSong(command)
            complete("removed")
          }
        }
      } ~
      path("start_playlist") {
        post {
          parameters('company_id.as[String], 'playlist_id.as[String]) { (companyID, playlistID) =>
            respondWithMediaType(MediaTypes.`application/json`) { ctx =>
              musicService.startPlaylist(companyID, playlistID)
              ctx.complete(StatusCodes.OK, SuccessMessage("started"))
            }
          }
        }
      } ~
      path("stop_playlist") {
        post {
          parameters('company_id.as[String], 'playlist_id.as[String]) { (companyID, playlistID) =>
            respondWithMediaType(MediaTypes.`application/json`) { ctx =>
              musicService.stopPlaylist(companyID, playlistID)
              ctx.complete(StatusCodes.OK, SuccessMessage("stopped"))
            }
          }
        }
      } ~
      path("vote_song") {
        post {
          parameters('company_id.as[String], 'song_id.as[String]) { (companyID, songID) =>
            musicService.voteSong(companyID, songID)
            complete("")
          }
        }
      } ~
      path("start_song") {
        post {
          parameters('company_id.as[String], 'song_id.as[String]) { (companyID, songID) =>
            musicService.startSong(companyID, songID)
            complete("")
          }
        }
      }
    }

  override implicit def actorRefFactory: ActorRefFactory = actorRefFactoryParam
}
