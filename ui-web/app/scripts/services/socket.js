'use strict';

/**
 * @ngdoc service
 * @name musicDesktopApp.socket
 * @description
 * # socket
 * Factory in the musicDesktopApp.
 */
angular.module('musicDesktopApp')
  .factory('socket', function ($rootScope) {
var socket;
    return {
      joinRoom: function(roomId) {
        socket = io("http://localhost:3000/");
        socket.emit('joinRoom', roomId);
          socket.on('votes', function(songId){
            $rootScope.$broadcast("newVote", songId);
          });
      },
      sendVote: function(roomId, songId) {
        socket.emit('voting', { room: roomId, vote: songId });
      },
      newSong: function(roomId, songId) {
        socket.emit('playing', { room: roomId, song: songId });
      }

    }


  });
