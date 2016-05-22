'use strict';

/**
 * @ngdoc filter
 * @name musicDesktopApp.filter:secondsToDate
 * @function
 * @description
 * # secondsToDate
 * Filter in the musicDesktopApp.
 */
angular.module('musicDesktopApp')
  .filter('secondsToDate', function () {
    return function(seconds) {
      return new Date(1970, 0, 1).setSeconds(seconds);
    };
  });
