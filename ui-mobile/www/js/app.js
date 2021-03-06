// Ionic Starter App

// angular.module is a global place for creating, registering and retrieving Angular modules
// 'starter' is the name of this angular module example (also set in a <body> attribute in index.html)
// the 2nd parameter is an array of 'requires'
// 'starter.controllers' is found in controllers.js
angular.module('musicapp', ['ionic', 'musicapp.controllers', 'musicapp.services'])

.run(function($ionicPlatform) {
  $ionicPlatform.ready(function() {
    // Hide the accessory bar by default (remove this to show the accessory bar above the keyboard
    // for form inputs)
    if (window.cordova && window.cordova.plugins.Keyboard) {
      cordova.plugins.Keyboard.hideKeyboardAccessoryBar(true);
      cordova.plugins.Keyboard.disableScroll(true);
    }

    if (window.StatusBar) {
      // org.apache.cordova.statusbar required
      StatusBar.styleDefault();
    }
  });
})


.config( function($stateProvider, $urlRouterProvider) {




  $stateProvider
  .state('app', {
    url: '/app',
    abstract: true,
    templateUrl: 'templates/menu.html',
    controller: 'AppCtrl'
  })

  .state('app.locations', {
    url: '/locations',
    views: {
      'menuContent': {
        templateUrl: 'templates/locations.html',
        controller: 'LocationsCtrl'
      }
    }
  })

  .state('app.maps', {
    url: '/maps',
    views: {
      'menuContent': {
        templateUrl: 'templates/maps.html',
        controller: 'MapsCtrl'
      }
    }
  })

  .state('app.playlists', {
    url: '/playlists',
    views: {
      'menuContent': {
        templateUrl: 'templates/playlists.html',
        controller: 'PlayingCtrl'
      }
    }
  });

  // if none of the above states are matched, use this as the fallback
  $urlRouterProvider.otherwise('/app/locations');
});
