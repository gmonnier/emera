app.directive('fileManager', function($rootScope) {
	return {
		scope: {
			fileType : "@",
			title : "@",
			uid : "@",
			baseSpaceAllowed: "@bsallowed",
			selectedFiles: "="
		},
		templateUrl : 'html/templates/file-manager-template.html',
		controllerAs: 'ctrl',
	    controller: 'fileManagerController',
	    bindToController: true
	};
});