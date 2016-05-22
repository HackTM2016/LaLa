'use strict';

describe('Filter: secondsToDate', function () {

  // load the filter's module
  beforeEach(module('musicDesktopApp'));

  // initialize a new instance of the filter before each test
  var secondsToDate;
  beforeEach(inject(function ($filter) {
    secondsToDate = $filter('secondsToDate');
  }));

  it('should return the input prefixed with "secondsToDate filter:"', function () {
    var text = 'angularjs';
    expect(secondsToDate(text)).toBe('secondsToDate filter: ' + text);
  });

});
