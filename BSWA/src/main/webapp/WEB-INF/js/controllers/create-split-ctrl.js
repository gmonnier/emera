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
		
		$("#menuLoadData").menu();
		$("#menuLoadData").css('position', 'absolute');
		$("#menuLoadData").css('z-index', '100');
		$("#menuLoadData").hide();

		$(".open-load-data").hover(function(e) {
			$("#menuLoadData").width(250);
			$("#menuLoadData").show();
		}, function() {
			$("#menuLoadData").hide();
			$("#menuLoadData").width(0);
		});
		
		$("#menuLoadLibrary").menu();
		$("#menuLoadLibrary").css('position', 'absolute');
		$("#menuLoadLibrary").css('z-index', '100');
		$("#menuLoadLibrary").hide();
		$(".open-load-lib").hover(function() {
			$("#menuLoadLibrary").show();
			$("#menuLoadLibrary").width(250);
		}, function() {
			$("#menuLoadLibrary").hide();
			$("#menuLoadLibrary").width(0);
		});
	};
});
