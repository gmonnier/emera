appControllers.controller('runningInfosCtrl', function ($scope,$rootScope, analysisMgtService, uploadService) {
	
	$scope.init = function(analyseid)
    {
		$scope.analyseID = analyseid; 
		$scope.uploadProgress = -1;
		$scope.uploadingFileName = '';
			
		if(analyseid == uploadService.getCurrentAnalyseID()) {
			$scope.uploadProgress = uploadService.getCurrentUploadingProgress();
			$scope.uploadingFileName = uploadService.getCurrentUploadingFileName();
		}
	};
	
	// init with current runs
	$scope.runningAnalyses = analysisMgtService.getRuns();
	// Ajx call - update of running analysis info values
	$scope.updateinfos = function() {
		if($scope.runningAnalyses !== 'undefined') {
			$('.progressbar').each(function(index) {
				if($scope.runningAnalyses.length > index) {
					var progress = $scope.runningAnalyses[index].progress;
					$( this ).progressbar('value', progress);
				}
			});
		}
	};
	
	// listen for the event in the relevant $scope
	var runListener = $rootScope.$on('updateRuns', function (event, data) {
		$scope.runningAnalyses = data;
		$scope.updateinfos();
	});
	
		// listen for the event in the relevant $scope
	var uploadProgressListener = $rootScope.$on('uploadProgressChanged', function (event, data) {
		if(data.analyseId === $scope.analyseID) {
			$scope.uploadProgress = data.progressval;
			$scope.analyseId = data.analyseId;
		}
	});
	
	// listen for the event in the relevant $scope
	var uploadFileChangedListener = $rootScope.$on('uploadingFileChanged', function (event, data) {
		if(data.analyseId === $scope.analyseID) {
			$scope.uploadingFileName = data.filename;
		}
	});
	
	
	 // $scope $destroy
    $scope.$on('$destroy', function() {
		// cancel run listener
		runListener();
		// cancel uploadProgressListener
		uploadProgressListener();
		// cancel uploadFileChangedListener
		uploadFileChangedListener();
	});
});
