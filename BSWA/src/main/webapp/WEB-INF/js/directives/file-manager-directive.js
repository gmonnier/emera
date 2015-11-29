app.directive('fileManager', function($rootScope) {
	return {
		scope: {},
		templateUrl : '/templates/file-manager-template.html',
	    controller: 'fileManagerController',
	    controllerAs: 'ctrl',
	    bindToController: true
	};
});