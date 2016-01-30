appControllers.controller('createSplitCtrl', function($scope, $http,$location, uploadService, user, PatternStorageSrvc) {
	
	// init upload lists
    uploadService.clearLists();
    
    var updateSplitPatternList = function() {
		PatternStorageSrvc.getAllSplitPatterns().then(function(splitPatternListVar) {
			$scope.splitPatterns = splitPatternListVar;
		});
	};
	updateSplitPatternList();

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
	
    $scope.$on('nextStepClicked', function (evt, directiveScope) {
    	if((directiveScope.currentCreateStep+1) > directiveScope.MAX_STEP) {
			if($scope.validateParameters()) {
				$scope.enqueueProcessing();
			} else {
				$scope.showDialog("#dialog-unvalid-parameters");
			}
		}
    });
    
	$scope.toggleSelect = function(splitpattern) {
		var found = false;
		for (i = $scope.configuration.dataSplitterModels.length-1; i >= 0; i--) {
			var currentSplitter = $scope.configuration.dataSplitterModels[i];
			if(currentSplitter.regexp === splitpattern.regexp) {
				$scope.configuration.dataSplitterModels.splice(i, 1);
				found = true;
				break;
			}
		}
		if(!found) {
			$scope.configuration.dataSplitterModels.push(splitpattern);
		}
	}
    
    $scope.enqueueProcessing = function() {
		var userID = 'guest';
		if(user.current.hasOwnProperty('user_id')) {
			userID = user.current.user_id;
		}
		var urlenqueue = '/ws-resources/preprocess/enqueue?user_id=' + userID;
		
		var request = $http({method: 'POST', 
							url: urlenqueue,
							data: $scope.configuration});
		
		request.success(function(data, status, headers, config) {
			var uploadNeeded = uploadService.uploadNeeded();
			$scope.uploadmessage = uploadNeeded ? 'Upload process will start now. Please do not leave the page while transfering files to server.' : '';
			if(uploadNeeded && data) {
				// ask the upload service to start uploading files to the server - data = analyse ID
				uploadService.startUpload(data);
			} else {
				if(!data) {
					// should never occurs!
					alert('an error occured when uploading files, unable to retrieve analyse id');
				}
			}
			$scope.showDialog("#dialog-message-ok", function() {
				$scope.changeRoute('/running');
			});
		}).
		error(function(data, status, headers, config) {
			$scope.showDialog("#dialog-message-failed" );
			$scope.changeRoute('/home');
		});
	}
    
	$scope.validateParameters = function() {
		return !($scope.isEmpty(configuration.dataSplitterModels) || $scope.isEmpty(configuration.selectedDataFiles));
	}
    
    // TODO Refactor this method
	$scope.showDialog = function(dialogid, onok) {
		$(dialogid).dialog({
			modal: true,
			width: 700,
			show: {
		        effect: 'fade',
		        duration: 500
		    },
		    hide: {
		        effect: 'fade',
		        duration: 500
		    },
			buttons: {
			Ok: function() {
				$( this ).dialog( "close" );
				if(onok) {
					onok();
				}
			}
			}
		});
	}
	
	// TODO refactor this method too (common with several controllers)
	$scope.changeRoute = function(url, forceReload) {
        $scope = $scope || angular.element(document).scope();
        if(forceReload || $scope.$$phase) {
            window.location = url;
        } else {
            $location.path(url);
            $scope.$apply();
        }
    };
    
	$scope.isEmpty = function(list) {
		if(list !== undefined){
			return list.length == 0;
		}
		return true;
	};
	
	$scope.isSelected = function(splitPattern) {
		if ($scope.configuration && !$scope.isEmpty($scope.configuration.dataSplitterModels)) {
			for (var i = 0, len = $scope.configuration.dataSplitterModels.length; i < len; i++) {
				if ($scope.configuration.dataSplitterModels[i].regexp === splitPattern.regexp) {
					return true;
				}
			}
		}
		return false;
	}

}).directive('applyEffects', function() {
	return function(scope, element, attrs) {
		if (scope.$last){
			$( ".selectPatItem" ).click(function() {
				$( ".patternPreview" ).clearQueue();
				$( ".patternPreview" ).stop();
				$( ".patternPreview" ).hide();
				$( ".patternPreview" ).fadeIn( "slow", function() {});
			});
		}
	};
});
