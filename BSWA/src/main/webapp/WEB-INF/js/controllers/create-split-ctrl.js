appControllers.controller('createSplitCtrl', function($scope, $http) {

	$http({method: 'GET', url: '/ws-resources/datastorage/dataFiles'}).
    success(function(data, status, headers, config) {
        $scope.storeDataFilesList = data;
    }).
    error(function(data, status, headers, config) {
		$scope.storeDataFilesList = [];
    });
	
	// Retrieve init processing configuration
	$http({method: 'GET', url: '/ws-resources/preprocess/confinit'}).
    success(function(data, status, headers, config) {
		$scope.configuration = data;
    }).
    error(function(data, status, headers, config) {
		$scope.configuration = null;
    });
	
}).directive('initAccordions', function() {
	return function(scope, element, attrs) {
		$('.acc').accordion({
			heightStyle : "content",
			autoHeight : false,
			clearStyle : true,
			header : "h3",
			collapsible : true
		});
	};
});
