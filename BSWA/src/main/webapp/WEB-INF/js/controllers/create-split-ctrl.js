appControllers.controller('createSplitCtrl', function($scope, $http, uploadService) {
	
	// init upload lists
    uploadService.clearLists();

	$http({
		method : 'GET',
		url : '/ws-resources/datastorage/dataFiles'
	}).success(function(data, status, headers, config) {
		$scope.storeDataFilesList = data;
	}).error(function(data, status, headers, config) {
		$scope.storeDataFilesList = [];
	});

	// Retrieve init processing configuration
	$http({
		method : 'GET',
		url : '/ws-resources/preprocess/confinit'
	}).success(function(data, status, headers, config) {
		$scope.configuration = data;
	}).error(function(data, status, headers, config) {
		$scope.configuration = null;
	});

});
