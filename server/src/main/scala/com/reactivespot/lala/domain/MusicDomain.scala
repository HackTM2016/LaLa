package com.reactivespot.lala.domain

case class Playlist(
  id: String,
  name: String,
  companyID: String,
  songs: List[Song]
)

case class Song(
  id: String,
  title: Option[String],
  author: Option[String]
)

case class CurrentPlaylist(
  id: String,
  name: String,
  companyID: String,
  songs: List[SongWVotes],
  currentSongID: String,
  status: Int
)

case class SongWVotes(
  id: String,
  title: Option[String],
  author: Option[String],
  votes: Int
)

case class AddSong(
  companyID: String,
  playlistID: String,
  song: Song
)

case class RemoveSong(
  companyID: String,
  playlistID: String,
  songID: String
)