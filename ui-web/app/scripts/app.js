'use strict';

/**
 * @ngdoc overview
 * @name musicDesktopApp
 * @description
 * # musicDesktopApp
 *
 * Main module of the application.
 */
angular
  .module('musicDesktopApp', ["uiGmapgoogle-maps"])
  .config(function(uiGmapGoogleMapApiProvider) {
    uiGmapGoogleMapApiProvider.configure({
      //    key: 'your api key',
      v: '3.20', //defaults to latest 3.X anyhow
      libraries: 'weather,geometry,visualization'
    });
  })
.run(function() {
      var socket = io("http://localhost:3000/");

    setTimeout(function() {
      socket.emit('joinRoom', "cristi");
      // $('#msg').val('');
      return false;
    }, 5000);
  });
