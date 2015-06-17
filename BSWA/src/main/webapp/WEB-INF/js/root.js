app.config(['$routeProvider',
  function($routeProvider) {
    $routeProvider.
      when('/create', {
        templateUrl: 'createNewPan.html',
        controller: 'createNewCtrl', public: true
      }).
      when('/running', {
        templateUrl: 'runningPan.html',
        controller: 'runningCtrl', public: true
      }).
	  when('/config', {
        templateUrl: 'networkConfigPan.html',
        controller: 'networkCtrl', public: true
      }).
      otherwise({
		templateUrl: 'homePan.html',
        controller: 'welcomeCtrl'
      });
}]);