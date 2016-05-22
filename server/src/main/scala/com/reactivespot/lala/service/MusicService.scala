package com.reactivespot.lala.service

import com.reactivespot.lala.domain.{CurrentPlaylist, RemoveSong, AddSong, Playlist}


trait MusicService {

  def getCurrentPlaylist(companyID: String): Option[CurrentPlaylist]

  def addPlaylist(playlist: Playlist): String

  def getPlaylists(companyID: String): List[Playlist]

  def getPlaylist(companyID: String, playlistID: String): Option[Playlist]

  def addSong(addSong: AddSong)

  def removeSong(removeSong: RemoveSong)

  def startPlaylist(companyID: String, playlistID: String)

  def stopPlaylist(companyID: String, playlistID: String)

  def startSong(companyID: String, songID: String)

  def voteSong(companyID: String, songID: String)

}
