package com.reactivespot.lala.domain

trait MusicJSONFormats extends CommonJSONFormats {
  implicit val songFormat = jsonFormat3(Song)
  implicit val playlistFormat = jsonFormat4(Playlist)

  implicit val songVFormat = jsonFormat4(SongWVotes)
  implicit val currentPlaylistFormat = jsonFormat6(CurrentPlaylist)

  implicit val addSongFormat = jsonFormat3(AddSong)
  implicit val removeSongFormat = jsonFormat3(RemoveSong)
}