'use strict';

/**
 * @ngdoc filter
 * @name musicDesktopApp.filter:youtubeDate
 * @function
 * @description
 * # youtubeDate
 * Filter in the musicDesktopApp.
 */
angular.module('musicDesktopApp')
  .filter('youtubeDate', function () {
    return function(dateul) {
      var dateString = dateul.split("PT")[1];
      dateString = dateString.split("M");
      return dateString[0] + ":" + dateString[1].split("S")[0];
    };
  });
