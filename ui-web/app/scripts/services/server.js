'use strict';

/**
 * @ngdoc service
 * @name musicDesktopApp.server
 * @description
 * # server
 * Factory in the musicDesktopApp.
 */
angular.module('musicDesktopApp')
  .factory('server', function ($http) {

    var hostname = "88.198.117.246";
    var port = "80";
    return {
      getPlaylist: function (params) { //company_id & playlist_id
        return $http.get("http://" + hostname + ":" + port + "/lala/lalaserver/music/playlist", {
          params: params
        });
      },
      getPlaylists: function (companyId) {
        return $http.get("http://" + hostname + ":" + port + "/lala/lalaserver/music/playlists", {
          params: {company_id: companyId}
        });
      },
      addPlaylist: function (data) {
        return $http.post("http://" + hostname + ":" + port + "/lala/lalaserver/music/playlist", data, {
          headers: {
            'Content-Type': 'application/json'
          }
        });
      },
      addSongToPlaylist: function (data) {
        return $http.post("http://" + hostname + ":" + port + "/lala/lalaserver/music/song", data);
      },
      removeSongFromPlaylist: function (data) {
        return $http.post("http://" + hostname + ":" + port + "/lala/lalaserver/music/song", data);
      },
      startPlaylist: function(params) {
        return $http.post("http://" + hostname + ":" + port + "/lala/lalaserver/music/start_playlist", {}, {
          params: params
        });
      },
      stopPlaylist: function(params) {
        return $http.post("http://" + hostname + ":" + port + "/lala/lalaserver/music/stop_playlist", {}, {
          params: params
        });
      },
      startSong: function(params) { //song_id, company_id
        return $http.post("http://" + hostname + ":" + port + "/lala/lalaserver/music/start_song", {}, {
          params: params
        });
      },
      voteSong: function(params) { //song_id, company_id
        return $http.post("http://" + hostname + ":" + port + "/lala/lalaserver/music/vote_song", {}, {
          params: params
        });
      }
    };
  });
