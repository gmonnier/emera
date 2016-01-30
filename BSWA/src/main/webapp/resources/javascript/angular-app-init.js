// List of all applications controllers
var appControllers = angular.module('appControllers', []);
// List of all applications directives
var directives = angular.module('appDirectives', []);
// List of all applications services
var services = angular.module('appServices', []);
// inject angular file upload directives and services.
var app = angular.module('app', [ 'ngRoute', 'routeStyles', 'appControllers',
		'appServices', 'ngAnimate', 'angularFileUpload', 'UserApp' ]);
app.run(function(user) {
	user.init({
		appId : '554d0317250f5'
	});
});
Offline.on('up', function() {
	// similar behavior as an HTTP redirect
	window.location.href = "/";
});
/*
 * var run = function(){ var req = new XMLHttpRequest(); req.timeout = 5000;
 * req.open('GET', 'ws-resources/connection', true); req.send(); }
 * setInterval(run, 3000);
 */