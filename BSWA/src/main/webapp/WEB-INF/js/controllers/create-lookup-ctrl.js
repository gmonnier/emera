appControllers.controller('createLookupCtrl', function ($scope,$rootScope, $http, $location, uploadService, user) {
	
	// init upload lists
    uploadService.clearLists();
    
	// Retrieve init processing configuration
	$http({method: 'GET', url: '/ws-resources/process/confinit'}).
    success(function(data, status, headers, config) {
		$scope.configuration = data;
		$scope.configuration.pattern = null;
    }).
    error(function(data, status, headers, config) {
		$scope.configuration = null;
    });
	
	$scope.isEmpty = function(list) {
		if(list !== undefined){
			return list.length == 0;
		}
		return true;
	}
	
    $scope.$on('nextStepClicked', function (evt, directiveScope) {
    	if((directiveScope.currentCreateStep+1) > directiveScope.MAX_STEP) {
			if($scope.validatePatternSelection()) {
				$scope.enqueueProcessing();
			} else {
				$scope.showDialog("#dialog-unvalid-pattern");
			}
		} else {
			if(!$scope.validateFileSelection()) {
				$scope.showDialog("#dialog-unvalid-parameters");
			} else {
				directiveScope.currentCreateStep += 1;
				$scope.stepValue = directiveScope.currentCreateStep;
			}
		}
    });
	
	$scope.validateFileSelection = function() {
		return $scope.configuration.selectedDataFiles.length > 0 && $scope.configuration.selectedLibraries.length > 0;
	}
	
	$scope.validatePatternSelection = function() {
		return $scope.configuration.pattern !== null;
	}
	
	$scope.enqueueProcessing = function() {
		
		var userID = 'guest';
		if(user.current.hasOwnProperty('user_id')) {
			userID = user.current.user_id;
		}
		var urlenqueue = '/ws-resources/process/enqueue?user_id=' + userID;
		
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
	
	$scope.showDialog = function(dialogid, onok) {
		$(dialogid).dialog({
			modal: true,
			width: 700,
			show: {
		        effect: 'fade',
		        duration: 1000
		    },
		    hide: {
		        effect: 'fade',
		        duration: 1000
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
	
	$scope.changeRoute = function(url, forceReload) {
        $scope = $scope || angular.element(document).scope();
        if(forceReload || $scope.$$phase) {
            window.location = url;
        } else {
            $location.path(url);
            $scope.$apply();
        }
    };

});
