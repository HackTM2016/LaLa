'use strict';

/**
 * @ngdoc service
 * @name musicDesktopApp.Youtube
 * @description
 * # Youtube
 * Factory in the musicDesktopApp.
 */
angular.module('musicDesktopApp')
  .factory('Youtube', function ($http) {

    var apiKey = "AIzaSyC-y_YPMT6OUmNqqOHn1X8ozLglrAOd_Og";
    var PLAYapiKey = "AIzaSyDosN442qoW_2jOUnMKChFO6ndh_uTdBBk";


    return {

      getVideoInfo: function(videoId) {
        return $http.get("https://www.googleapis.com/youtube/v3/videos", {params: {id: [videoId], part: "snippet,contentDetails", key: apiKey} });
      },
      getVideosInfo: function(videosList) {
        return $http.get("https://www.googleapis.com/youtube/v3/videos", {params: {id: [videosList], part: "snippet,contentDetails", key: apiKey} });
      },
      searchVideos: function(searchText) {
        console.log(searchText);
        return $http.get('https://www.googleapis.com/youtube/v3/search', {
          params: {
            key: PLAYapiKey,
            type: 'video',
            maxResults: '30',
            part: 'id, snippet',
            fields: 'items/id,items/snippet/title,items/snippet/description,items/snippet/thumbnails/default,items/snippet/channelTitle',
            q: searchText
          }
        });
      }
    }
  });
