app.directive('fileManager', function($rootScope) {
	return {
		scope: {
			fileType : "@fileType",
			title : "@title",
			uid : "@uid",
			baseSpaceAllowed: "@bsallowed",
		},
		templateUrl : '/templates/file-manager-template.html',
	    controller: 'fileManagerController',
	    controllerAs: 'ctrl',
	    bindToController: true
	};
});