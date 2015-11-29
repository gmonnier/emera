app.directive('fileManager', function($rootScope) {
	return {
		templateUrl : '/templates/file-manager-template.html',
	    controller: 'fileManagerController',
	    controllerAs: 'ctrl',
	};
});