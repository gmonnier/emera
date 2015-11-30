appControllers.controller('bsModelCtrl', function ($scope,$rootScope, basespaceService) {
	
	$scope.selectedBaseSpaceFile = null;
	$scope.showAllRuns = false;
	$scope.maxDisplayedRuns = 5;
	$scope.userruns = []
	
	$scope.setSelectedBSResource = function(file) {
		$scope.selectedBaseSpaceFile = file;
	}
	
	$scope.toggleShowRuns = function(){
		$scope.showAllRuns = !$scope.showAllRuns;
	}
	
	$scope.getDisplayedRuns = function() {
		if($scope.showAllRuns == true) {
			return $scope.userruns;
		} else {
			// display only the first five items
			return $scope.userruns.slice(0,$scope.maxDisplayedRuns);
		}
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
	var unbind = $rootScope.$on('requestBaseSpaceDialogClose', function(){
		if($scope.selectedBaseSpaceFile !== null) {
			$rootScope.$emit('addSelectedBaseSpaceFile', $scope.selectedBaseSpaceFile);
		}
    });
	$scope.$on('$destroy', unbind);
	
});

app.directive('postRepeatDirectiveBsDial', function() {
	return function(scope, element, attrs) {
		if (scope.$last){
			//alert($(".accrun > div").length + "   " + scope.$index);
			$(".accrun").accordion({  heightStyle: "content",autoHeight: false,clearStyle: true, active: false, header: "h3", collapsible: true });
		}
	};
});

app.directive('refreshAcc', function() {
	return function(scope, element, attrs) {
		$(".accrun").accordion("refresh");
	};
});



