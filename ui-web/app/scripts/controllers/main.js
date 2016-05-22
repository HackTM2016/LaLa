'use strict';

/**
 * @ngdoc function
 * @name musicDesktopApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the musicDesktopApp
 */
angular.module('musicDesktopApp')
  .controller('MainCtrl', function ($scope, $window, $http, $interval, Youtube, socket, server) {

    socket.joinRoom("testRoom");
    var tag = document.createElement('script');
    tag.src = "http://www.youtube.com/iframe_api";
    var firstScriptTag = document.getElementsByTagName('script')[0];
    firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);
    var player;


    $scope.searchText = {
      value: ""
    };
    $scope.newPlaylist = {
    };
    $scope.sideExtended = false;
    $scope.activeTab = 'now';


    $scope.selectTab = function(tabName) {

      if(tabName !== 'now') {
        $scope.sideExtended = true;
        $scope.activeTab = tabName;
      } else {
        $scope.sideExtended = false;
      }

    };

    $scope.currentProgress = 0;
    $scope.currentSong = null;
    $scope.currentPlaylist = {
      name: "Tonight",
      id: "test",
      items: []
    };
    $scope.youtubePlaylist = [];

    //$scope.youtubePlaylist = ["oiNlK1KVSuU", "aOiDk5j9lEM", "znlFu_lemsU", "8qTFqnDpuvE", "4nWw4wYAVyc"];

    $scope.account = {
      id: "cantina@upt.com",
      name: "Cantina Politehnica"
    };

    $scope.map = {
      center: { latitude: 45.7488720, longitude: 21.2086790 },
      zoom: 2,
      markerCoords: {latitude: 45.7488720, longitude: 21.2086790},
      markerKey: "1",
      markerOptions: {
        draggable: true
      }
    };

    function getPlaylists() {
      server.getPlaylists($scope.account.id).then(function(data) {
        $scope.playlists = data.data;
        $scope.selectPlaylist(data.data[0]);
        generateYoutubeList();
      });
    }

    function generateYoutubeList() {
      $scope.youtubePlaylist = [];
      $scope.currentPlaylist.items.forEach(function(song) {
        $scope.youtubePlaylist.push(song.id);
      });
      getPlaylistInfo();
    }

    getPlaylists();

    function getPlaylistInfo() {
      var playlist = angular.copy($scope.youtubePlaylist).toString();
      console.log(playlist);

      Youtube.getVideosInfo(playlist).then(function(data) {
        $scope.currentPlaylist.items = data.data.items;
        $scope.currentPlaylist.items.forEach(function(song) {
          song.votes = 0;
        })
      });
    }

    getPlaylistInfo();


    $window.onYouTubeIframeAPIReady = function() {
      player = new YT.Player('player', {
        enablejsapi: 1,
        height: '100',
        width: '100',
        playerVars: { 'autoplay': 0, 'controls': 1 },
        events: {
          'onReady': onPlayerReady,
          'onStateChange': onPlayerStateChange
        }
      });
    };



    function newVoteForSong(songId) {
      var song = $scope.currentPlaylist.items.filter(function(song) {
        return songId == song.id;
      })[0];
      song.votes++;
      reorderPlaylist();
    }

    function onPlayerReady(event) {

      //player.stopVideo();

      //event.target.stopVideo();
    }




    function onPlayerStateChange(event) {
      console.log(event.data);/**/

      if (event.data == YT.PlayerState.UNSTARTED) {
        console.log(event.data);/**/

      }


      if (event.data == 1) {
        //playing (new song)
        var video_data = player.getVideoData();
        socket.newSong("testRoom", video_data.video_id);

        server.startSong({ company_id : $scope.account.id, song_id: video_data.video_id});


        console.log(video_data.video_id);
        Youtube.getVideoInfo(video_data.video_id).then(function(data) {
          console.log(data.data.items[0]);



          $scope.currentPlaylist.items.forEach(function(song, index) {
            if(song.id == video_data.video_id) {
              var currentPlaying = $scope.currentPlaylist.items.splice(index, 1)[0];
              console.log("currentPlaying");
              console.log(currentPlaying);
              $scope.currentPlaylist.items.push(currentPlaying);
              reorderPlaylist();
            }
          });


          $scope.currentSong = data.data.items[0];
        });
      }

      if (event.data == YT.PlayerState.ENDED) {

        player.loadPlaylist($scope.youtubePlaylist,
          0,
          0,
          "medium");

      }
      //$scope.stopVideo();
    }

    $scope.playVideo = function() {
      player.loadPlaylist($scope.youtubePlaylist,
        0,
        0,
        "medium");
      server.startPlaylist({ company_id : $scope.account.id, playlist_id: $scope.currentPlaylist.id}).then(function() {
        server.startSong({ company_id : $scope.account.id, song_id: $scope.youtubePlaylist[0]});
      });

      player.playVideo();
    };



    $scope.stopVideo = function() {
      console.log("STOP NOW");
      player.stopVideo();
      $scope.currentSong = null;
    };


    $interval(function() {
      var currentTime = player.getCurrentTime();
      var durationTime = player.getDuration();
      $scope.currentTime = currentTime;
      $scope.durationTime = durationTime;
      $scope.currentProgress = Math.floor(100 * currentTime / durationTime) + "%";
      console.log($scope.currentProgress);
    }, 1000);



    $scope.search = function () {
      var searchText = angular.copy($scope.searchText.value);
      Youtube.searchVideos(searchText).then(function(data) {
        $scope.searchResults = data.data.items;

      })
    };

    $scope.pushToList = function(songId) {
      if (songId) {
        $scope.youtubePlaylist.push(songId);
        Youtube.getVideoInfo(songId).then(function (data) {
          var newSong = angular.copy(data.data);
          $scope.currentPlaylist.items.push(newSong.items[0]);
          var playlistInList = $scope.playlists.filter(function(playlist) {
            return playlist.id == $scope.currentPlaylist.id;
          })[0];
          console.log(playlistInList);
          playlistInList.songs.push(newSong.items[0]);
          server.addSongToPlaylist({
            "companyID" : $scope.account.id,
            "playlistID": $scope.currentPlaylist.id,
            "song" : {
              "id": songId
            }
          }).then(function() {

          });
        });
      }
    };

    $scope.$on("newVote", function(event, songId) {
      console.log("vote");
      newVoteForSong(songId);
      server.voteSong({company_id: $scope.account.id, song_id: songId});
      //console.log(a,b);
    });

    $scope.testSeek = function() {
      player.seekTo($scope.durationTime - 5);

      //socket.sendVote("testRoom", "song test");
    };

    $scope.addNewPlaylist = function() {
      server.addPlaylist({
        id: "",
        name: $scope.newPlaylist.name,
        companyID: $scope.account.id,
        songs: []
      }).then(function() {
        getPlaylists();
        $scope.newPlaylist = {};
      })
    };


    $scope.selectPlaylist = function(p) {

      $scope.stopVideo();
      console.log($scope.currentPlaylist);
      server.stopPlaylist({company_id: $scope.account.id, playlist_id: $scope.currentPlaylist.id}).then(function() {
        var playlist = angular.copy(p);
        $scope.currentPlaylist.id = playlist.id;
        $scope.currentPlaylist.name = playlist.name;
        $scope.currentPlaylist.items = playlist.songs;
        generateYoutubeList();
        getPlaylistInfo();
      });


    };

    function reorderPlaylist() {
      $scope.currentPlaylist.items.sort(function(a,b) {
        return (b.votes > a.votes) ? 1 : ((a.votes > b.votes) ? -1 : 0);
      } );
      console.log($scope.youtubePlaylist);
      $scope.youtubePlaylist = [];
      $scope.currentPlaylist.items.forEach(function(song) {
        console.log(song);
        $scope.youtubePlaylist.push(song.id);
        console.log($scope.youtubePlaylist);
      })
    }


  });
