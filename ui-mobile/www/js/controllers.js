angular.module('musicapp.controllers', [])

.controller('AppCtrl', function($scope, $ionicModal, $timeout, $state, $ionicHistory) {

  // With the new view caching in Ionic, Controllers are only called
  // when they are recreated or on app start, instead of every page change.
  // To listen for when this page is active (for example, to refresh data),
  // listen for the $ionicView.enter event:
  //$scope.$on('$ionicView.enter', function(e) {
  //});



    $scope.activeState = $state.current.name;

    $scope.$on('$stateChangeStart',
      function(event, toState, toParams, fromState, fromParams){
        $scope.activeState = toState.name;
      });

    $scope.openLocations = function() {
      $ionicHistory.nextViewOptions({
        disableBack: true
      });
      $state.go("app.locations", {notify: false});
    };

    $scope.selectedLocation = {};
})

.controller('LocationsCtrl', function($scope, $state, $ionicHistory, server) {


    navigator.geolocation.getCurrentPosition(function(position) {
     console.log(position.coords.latitude);
      server.getNearest({lat: position.coords.latitude, lon: position.coords.longitude}).then(function(data) {
        //console.log(data.data);
        $scope.locations = data.data;
      });
    });

    $scope.selectLocation = function(location) {
      $ionicHistory.nextViewOptions({
        disableBack: true
      });
      $scope.selectedLocation.id = location.id;
      $scope.selectedLocation.name = location.name;
      $scope.selectedLocation.logo = location.logo;
      $scope.selectedLocation.range = location.range;
      $scope.selectedLocation.lat = location.lat;
      $scope.selectedLocation.lon = location.lon;
      $state.go("app.playlists", {notify: false});
    }

})

.controller('myMapCtrl', function($scope, $state) {

})
.controller('MapsCtrl', function($scope, $state, server) {
    var styles = [
      {
        stylers: [
          { hue: "#00defc" },
          { saturation: -5 }
        ]
      },{
        featureType: "road",
        elementType: "geometry",
        stylers: [
          { lightness: 100 },
          { visibility: "simplified" }
        ]
      },{
        featureType: "road",
        elementType: "labels",
        stylers: [
          { visibility: "off" }
        ]
      }
    ];

    var styledMap = new google.maps.StyledMapType(styles,
      {name: "Styled Map"});



    navigator.geolocation.getCurrentPosition(function(position) {
      console.log(position.coords.latitude);

      $scope.myLocation = {lat: position.coords.latitude, lng: position.coords.longitude};
      initMap();

    });


    $scope.points = [
      new google.maps.LatLng(45.748468, 21.239788),
      new google.maps.LatLng(45.748568, 21.239888),
      new google.maps.LatLng(45.748668, 21.239488)
      //{location: new google.maps.LatLng(45.748468, 21.239788),  weight: 80},
      //{location: new google.maps.LatLng(45.748982, 21.239496),  weight: 40},
      //{location: new google.maps.LatLng(45.749206, 21.240610),  weight: 100}
    ];

    var params = {

    };

    var map, heatmap,initialBounds, params;

    var gradient = [
      'rgba(0, 255, 255, 0)',
      'rgba(0, 222, 252, 1)',
      'rgba(34, 217, 242, 1)',
      'rgba(51, 215, 237, 1)',
      'rgba(84, 216, 234, 1)',
      'rgba(84, 216, 234, 1)',
      'rgba(101, 216, 234, 1)',
      'rgba(120, 216, 234, 1)',
      'rgba(113, 233, 249, 1)',
      'rgba(150, 243, 255, 1)'
    ];

    function initMap() {
      console.log($scope.myLocation);
      map = new google.maps.Map(document.getElementById('map'), {
        zoom: 18,
        center: $scope.myLocation,
        mapTypeControlOptions: {
          mapTypeIds: [google.maps.MapTypeId.ROADMAP, 'map_style']
        }

      });

      map.mapTypes.set('map_style', styledMap);
      map.setMapTypeId('map_style');

      heatmap = new google.maps.visualization.HeatmapLayer({
        data: $scope.points,
        map: map,
        radius: 90,
        gradient: gradient
      });


      google.maps.event.addListener(map, 'bounds_changed', function() {
        initialBounds = map.getBounds();
        console.log(initialBounds);
        params = {
          lat1: initialBounds.H.H,
          lon1: initialBounds.j.H,
          lat2: initialBounds.H.j,
          lon2: initialBounds.j.j
        };
        getHeatMap(params);
        //initMap();
      });

    }


    function getHeatMap (params) {
      server.getHeatMap(params).then(function(data) {
        console.log(data);
        console.log();
        $scope.points = [];

        data.data.forEach(function(point) {
          $scope.points.push({
            location: new google.maps.LatLng(point.lat, point.lon),
            weight: point.value
          });

        });
        heatmap.set('data', $scope.points);

      });
    }
    //
    //function getPoints() {
    //  return [
    //    {location: new google.maps.LatLng(45.748468, 21.239788), weight: 400},
    //    {location: new google.maps.LatLng(45.748568, 21.239888), weight: 100},
    //    {location: new google.maps.LatLng(45.748668, 21.239488), weight: 1000}
    //  ];
    //}


    function toggleHeatmap() {
      heatmap.setMap(heatmap.getMap() ? null : map);
    }



      //heatmap.set('gradient', heatmap.get('gradient') ? null : gradient);


    function changeRadius() {
      heatmap.set('radius', heatmap.get('radius') ? null : 900);
    }

    function changeOpacity() {
      heatmap.set('opacity', heatmap.get('opacity') ? null : 0.2);
    }





    //createHeatLayer($scope.heatLayer, $scope.points);


    //console.log($scope.ourData);

})

  .controller('PlayingCtrl', function($scope, Youtube, $timeout, $interval, $rootScope, socket, server) {

    var myPlaylist;

    var companyId = "kfc@email.com";
    function getCurrentPlaylist() {
      var companyid = encodeURIComponent(companyId);
      server.getCurrentPlaylist(companyid).then(function(data){
        console.log(data.data);


        $scope.currentSong = data.data.songs.filter(function(song) {
          return song.id == data.data.currentSongID;
        })[0];


        $scope.currentTime = data.data.status;
        $scope.songs = data.data.songs;
        generateYoutubeList();
        getPlaylistInfo();
      });
    }


    $interval(function() {
      var currentTime = $scope.currentTime;
      $scope.currentTime++;
      console.log($scope.currentSong);
      var durationTime = $scope.currentSong.contentDetails.duration;
console.log(durationTime);
      durationTime = durationTime.split("PT")[1];
      durationTime = durationTime.split("M");
      $scope.durationTime = durationTime[0] + ":" + durationTime[1].split("S")[0];

      durationTime = durationTime[0] * 60 + parseInt(durationTime[1].split("S")[0]);
      console.log(currentTime);
      console.log(durationTime);

      //$scope.currentTime = currentTime;
      $scope.currentProgress = Math.floor(100 * currentTime / durationTime) + "%";
      console.log($scope.currentProgress);
    }, 1000);

    getCurrentPlaylist();

    function generateYoutubeList() {
      myPlaylist = [];
      $scope.songs.forEach(function(song) {
        myPlaylist.push(song.id);
      });
    }

    $scope.songs = [];
    function getPlaylistInfo() {
      Youtube.getVideosInfo(myPlaylist.toString()).then(function(data) {
        $scope.songs = data.data.items;

        $scope.currentSong = $scope.songs.filter(function(song) {
          return song.id == $scope.currentSong.id;
        })[0];
        //$scope.currentSong = $scope.songs[2];
        $scope.songs.forEach(function(song) {
          song.votes = 0; //update from server!
        });
      });
    }



    $scope.currentSong = {};


    var msgTimeout;

    $scope.tapSong = function(songId) {

      $scope.messageId = songId;
      if(msgTimeout) {
        $timeout.cancel(msgTimeout);
      }
      msgTimeout = $timeout(function() {
        $scope.messageId = null;
      }, 1500);
      console.log('tap');
      $scope.text = "hold";

    };

    $scope.voteSong = function(song) {
      socket.sendVote("testRoom", song.id);
      newVoteForSong(song.id);
      song.voted = !song.voted;
    };

    $scope.history = [
      { title: 'H1', id: 1 },
      { title: 'Chill', id: 2 },
      { title: 'Rap', id: 3 },
      { title: 'Cowbell', id: 4 },
      { title: 'H5', id: 5 }
    ];

    $scope.options = {
      loop: false,
      pager: true,
      initialSlide: 0,
      direction: 'horizontal',
      speed: 500
    };

    socket.joinRoom("testRoom");

    $scope.$on("newVote", function(event, songId) {
      newVoteForSong(songId);
    });

    $scope.$on("newSong", function(event, songId) {
      console.log("NEW SONG");
      var toBePlayed = $scope.songs.filter(function(song) {
        return song.id == songId;
      })[0];
      $scope.currentSong = toBePlayed;



      $scope.songs.forEach(function(song, index) {
        if(song.id == songId) {
          var currentPlaying = $scope.songs.splice(index, 1)[0];
          console.log("currentPlaying");
          console.log(currentPlaying);
          $scope.songs.push(currentPlaying);
          reorderPlaylist();
        }
      });


    });


    function newVoteForSong(songId) {
      var song = $scope.songs.filter(function(song) {
        return songId == song.id;
      })[0];
      song.votes++;
      reorderPlaylist()
    }

    function reorderPlaylist() {
      console.log($scope.songs);
      $scope.songs.sort(function(a,b) {
        return (b.votes > a.votes) ? 1 : ((a.votes > b.votes) ? -1 : 0);
      } );
    }


});
