'use strict';

describe('Filter: youtubeDate', function () {

  // load the filter's module
  beforeEach(module('musicDesktopApp'));

  // initialize a new instance of the filter before each test
  var youtubeDate;
  beforeEach(inject(function ($filter) {
    youtubeDate = $filter('youtubeDate');
  }));

  it('should return the input prefixed with "youtubeDate filter:"', function () {
    var text = 'angularjs';
    expect(youtubeDate(text)).toBe('youtubeDate filter: ' + text);
  });

});
