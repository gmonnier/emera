appControllers.controller('createLookupCtrl', function ($scope,$rootScope, $http, $location, uploadService, user) {
	
	$http({method: 'GET', url: '/ws-resources/datastorage/dataFiles'}).
    success(function(data, status, headers, config) {
        $scope.storeDataFilesList = data;
    }).
    error(function(data, status, headers, config) {
		$scope.storeDataFilesList = [];
    });
	
	// Init stored Libraries files
	$http({method: 'GET', url: '/ws-resources/datastorage/libFiles'}).
    success(function(data, status, headers, config) {
        $scope.storeLibraryFilesList = data;
    }).
    error(function(data, status, headers, config) {
		$scope.storeLibraryFilesList = [];
    });
	
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
	
	$scope.addDataViewFile = function(file) {
		if(file !== null) {
			var ok = checkDuplicates($scope.configuration.selectedDataFiles,file);
			if(ok) {
				$scope.configuration.selectedDataFiles.push(file);
				return true;
			} else {
				$scope.showDialog("#dialog-duplicate-warning" );
				return false;
			}
		}
	}
	
	$scope.addLibraryViewFile = function(file) {
		if(file !== null) {
			var ok = checkDuplicates($scope.configuration.selectedLibraries,file);
			if(ok) {
				$scope.configuration.selectedLibraries.push(file);
				return true;
			} else {
				$scope.showDialog("#dialog-duplicate-warning" );
				return false;
			}
		}
	}
	
	$scope.removeDataFile = function(viewfile) {
		var index = getFileIndex($scope.configuration.selectedDataFiles,viewfile);
		if(index != -1) {
			$scope.configuration.selectedDataFiles.splice(index, 1);
			if(viewfile.origin === 'UPLOAD') {
				var success = uploadService.removeFromUploadData(viewfile);
				if(!success) {
					alert('impossible to remove file from upload list');
				}
			}
		}
	}
	
	$scope.removeLibraryFile = function(viewfile) {
		var index = getFileIndex($scope.configuration.selectedLibraries,viewfile);
		if(index != -1) {
			$scope.configuration.selectedLibraries.splice(index, 1);
			if(viewfile.origin === 'UPLOAD') {
				var success = uploadService.removeFromUploadLibs(viewfile);
				if(!success) {
					alert('impossible to remove file from upload list');
				}
			}
		}
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
	
	function checkDuplicates(filelist, file) {
		var arrayLength = filelist.length;
		for (var i = 0; i < arrayLength; i++) {
			if(filelist[i].id === file.id) {
				return false;
			}
		}
		return true;
	}
	
	function getFileIndex(filelist, file) {
		var arrayLength = filelist.length;
		for (var i = 0; i < arrayLength; i++) {
			if(filelist[i].id === file.id) {
				return i;
			}
		}
		return -1;
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
    
	
	// init upload lists
	uploadService.clearLists();
	$scope.dataUploader = uploadService.getDataUploader();
	$scope.libUploader = uploadService.getLibsUploader();
	$scope.dataUploader.onAfterAddingFile = function(fileItem) { 
		var success = uploadService.addToUploadData(fileItem, $scope.addDataViewFile); 
		if(!success){
			$scope.dataUploader.removeFromQueue(fileItem);
		}
	};
	$scope.libUploader.onAfterAddingFile = function(fileItem) { 
		var success = uploadService.addToUploadLibs(fileItem, $scope.addLibraryViewFile); 
		if(!success){
			$scope.libUploader.removeFromQueue(fileItem);
		}
	};
	

	$scope.showList = function() {
		uploadService.showList();
	}

	/*
	* Open and init base space dialog.
	*/
	var closeAccordions = function() {
		$(".accrun").accordion({  heightStyle: "content", autoHeight: false,clearStyle: true, active: false, header: "h3", collapsible: true });
	};
	
	$scope.openBaseSpaceSelectionDial = function() {
		// Open dialog
		$("#basespacedialog").dialog({
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
			close: closeAccordions(),
			buttons: {
				Ok: function() {
					// emit event to ask bs-model-ctrl to add selected file to current model
					$rootScope.$emit('addSelectedBaseSpaceFile');
					$( this ).dialog( "close" );
					$scope.$apply();
				}
			}
		});
		// clear selection and close accordions
		$('.accrun .ui-state-focus').removeClass('ui-state-focus');
	}

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
