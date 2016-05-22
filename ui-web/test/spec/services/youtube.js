'use strict';

describe('Service: Youtube', function () {

  // load the service's module
  beforeEach(module('musicDesktopApp'));

  // instantiate service
  var Youtube;
  beforeEach(inject(function (_Youtube_) {
    Youtube = _Youtube_;
  }));

  it('should do something', function () {
    expect(!!Youtube).toBe(true);
  });

});
