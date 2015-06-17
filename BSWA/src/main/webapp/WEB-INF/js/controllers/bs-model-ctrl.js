appControllers.controller('bsModelCtrl', function ($scope,$rootScope, basespaceService) {
	
	$scope.selectedFile = null;
	
	$scope.setSelectedBSResource = function(file) {
		$scope.selectedFile = file;
	}
	
	basespaceService.listruns().then(function(listruns) {
		$scope.userruns = listruns;
    });
  
	basespaceService.userinfo().then(function(userinfovar) {
		$scope.user = userinfovar;
    });
	
	basespaceService.connectionstatus().then(function(connectionState) {
		$scope.connectionstatus = connectionState;
    });
	
	// Event listener when close with OK has been requested
	var unbind = $rootScope.$on('addSelectedFile', function(){
		if($scope.selectedFile !== null) {
			$scope.addDataViewFile($scope.selectedFile);
		}
    });
	$scope.$on('$destroy', unbind);
	
});

app.directive('postRepeatDirectiveBsDial', function() {
	return function(scope, element, attrs) {
		if (scope.$last){
			$(".accrun").accordion({  heightStyle: "content",autoHeight: false,clearStyle: true, active: false, header: "h3", collapsible: true });
		}
	};
});

