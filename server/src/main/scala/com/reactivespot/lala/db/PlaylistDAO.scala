package com.reactivespot.lala.db

import com.reactivespot.lala.domain.Playlist

trait PlaylistDAO {
  def createPlayList(playlist: Playlist)

  def readCompaniesPlaylists(companyID: String): List[Playlist]

  def readPlayList(id: String): Option[Playlist]

  def updatePlayList(id: String, playList: Playlist)

  def deletePlayList(id: String)
}
