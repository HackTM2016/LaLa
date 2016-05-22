angular.module('musicapp.services', [])

  .filter('secondsToDate', function () {
    return function(seconds) {
      return new Date(1970, 0, 1).setSeconds(seconds);
    };
  })


.factory('Youtube', function($http) {

    var apiKey = "AIzaSyC-y_YPMT6OUmNqqOHn1X8ozLglrAOd_Og";

    return {

     getVideoInfo: function(videoId) {
       return $http.get("https://www.googleapis.com/youtube/v3/videos", {params: {id: [videoId], part: "snippet, contentDetails", key: apiKey} });
     },
     getVideosInfo: function(videosList) {
       return $http.get("https://www.googleapis.com/youtube/v3/videos", {params: {id: [videosList], part: "snippet, contentDetails", key: apiKey} });
     }
   }

  })

  .factory('socket', function($rootScope) {
    return {

      joinRoom: function (roomId) {
        socket = io("http://localhost:3000/");
        socket.emit('joinRoom', roomId);
        socket.on('votes', function (songId) {
          $rootScope.$broadcast("newVote", songId);
          //console.log("VOTES:", songId)
        });
        socket.on('newSong', function (songId) {
          $rootScope.$broadcast("newSong", songId);
        });
      },
      sendVote: function (roomId, songId) {
        socket.emit('voting', {room: roomId, vote: songId});
      }

    }
  })
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
            return $http.get("http://" + hostname + ":" + port + "/lala/lalaserver/music/song", data);
          },
          removeSongFromPlaylist: function (data) {
            return $http.delete("http://" + hostname + ":" + port + "/lala/lalaserver/music/song", data);
          },
          startPlaylist: function (params) {
            return $http.get("http://" + hostname + ":" + port + "/lala/lalaserver/music/start_playlist", {
              params: params
            });
          },
          stopPlaylist: function (params) {
            return $http.delete("http://" + hostname + ":" + port + "/lala/lalaserver/music/stop_playlist", {
              params: params
            });
          },
          startSong: function (params) { //song_id, company_id
            return $http.put("http://" + hostname + ":" + port + "/lala/lalaserver/music/start_song", {
              params: params
            });
          },
          voteSong: function (params) { //song_id, company_id
            return $http.put("http://" + hostname + ":" + port + "/lala/lalaserver/music/vote_song", {
              params: params
            });
          },
          getCurrentPlaylist: function (companyId) { //company_id
            return $http.get("http://" + hostname + ":" + port + "/lala/lalaserver/music/current_playlist/" + companyId);
          },
          getNearest: function (params) { //lat lon
            return $http.get("http://" + hostname + ":" + port + "/lala/lalaserver/company/nearest", {
              params: params
            });
          },
          chooseCompany: function (companyId) {
            return $http.post("http://" + hostname + ":" + port + "/lala/lalaserver/music/playlist?company_id=" + companyId);
          },
          getHeatMap: function (params) { //lat1 lon1 lat2 lon2
            console.log(params);
            return $http.get("http://" + hostname + ":" + port + "/lala/lalaserver/company/heatmap_now", {
              params: params
            });
          },
          getHeatMapHistory: function (params) { //lat1 lon1 lat2 lon2 hn
            return $http.get("http://" + hostname + ":" + port + "/lala/lalaserver/company/heatmap_history", {
              params: params
            });
          }
        };
      });

