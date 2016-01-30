app.controller('fileManagerController', function(uploadService, $rootScope,
		$scope, $http) {

	this.title = 'Generic file manager';
	this.localFileInputSelector = "file-input-" + this.uid;

	// The following variable is binded via an attribute within the directive
	// declaration
	// this.selectedFiles = [];

	var storedFilesURL;
	if (this.fileType === 'library') {
		this.fileUploader = uploadService.getLibsUploader();
		this.addToUploadListFunction = uploadService.addToUploadLibs;
		this.removeFromUploadListFunction = uploadService.removeFromUploadLibs;
		storedFilesURL = '/ws-resources/datastorage/libFiles';
	} else {
		this.fileUploader = uploadService.getDataUploader();
		this.addToUploadListFunction = uploadService.addToUploadData;
		this.removeFromUploadListFunction = uploadService.removeFromUploadData;
		storedFilesURL = '/ws-resources/datastorage/dataFiles';
	}

	// Init stored file list TODO BLOCKING METHOD ---> ADD THIS INO A SEPARATE
	// STORAGE SERVICE
	$scope.storedFilesList = [];
	this.fetchStoredFilesList = $http({
		method : 'GET',
		url : storedFilesURL
	}).success(function(data, status, headers, config) {
		$scope.storedFilesList = data;
	}).error(function(data, status, headers, config) {
		$scope.storedFilesList = [];
	});
	
	this.openBaseSpaceSelectionDial = function() {
		// Open dialog
		$("#basespacedialog").data('fileManager', this).dialog(
				{
					modal : true,
					width : 700,
					show : {
						effect : 'fade',
						duration : 1000
					},
					hide : {
						effect : 'fade',
						duration : 500
					},
					close : $(".accrun").accordion({
						heightStyle : "content",
						autoHeight : false,
						clearStyle : true,
						active : false,
						header : "h3",
						collapsible : true
					}),
					buttons : {
						Ok : function() {
							// emit event to ask bs-model-ctrl to add selected
							// file to
							// current model
							var fileManagerCtrl = $(this).data('fileManager');
							$rootScope.$emit('requestBaseSpaceDialogClose',
									fileManagerCtrl);
							$(this).dialog("close");
						}
					}
				});
		// clear selection and close accordions
		$('.accrun .ui-state-focus').removeClass('ui-state-focus');
	};

	this.addViewFile = angular.bind(this, function(file) {
		if (file !== null) {
			var ok = this.checkDuplicates(this.selectedFiles, file);
			if (ok) {
				this.selectedFiles.push(file);
				return true;
			} else {
				var dialogSelector = "#dialog-duplicate-warning-" + this.uid;
				this.showDialog(dialogSelector);
				return false;
			}
		}
	});

	this.addViewFileAndApply = function(file) {
		this.addViewFile(file);
		$scope.$apply();
	}

	this.fileUploader.onAfterAddingFile = angular.bind(this,
			function(fileItem) {
				var success = this.addToUploadListFunction(fileItem,
						this.addViewFile);
				if (!success) {
					this.fileUploader.removeFromQueue(fileItem);
				}
			});

	this.checkDuplicates = function(filelist, file) {
		var arrayLength = filelist.length;
		for (var i = 0; i < arrayLength; i++) {
			if (filelist[i].id === file.id) {
				return false;
			}
		}
		return true;
	};

	this.selectFile = function() {
		var elem = document.getElementById(this.localFileInputSelector);
		if (elem && document.createEvent) {
			var evt = document.createEvent("MouseEvents");
			evt.initEvent("click", true, false);
			elem.dispatchEvent(evt);
		}
	};

	this.removeSelectedFile = function(viewfile) {
		var index = this.getFileIndex(this.selectedFiles, viewfile);
		if (index != -1) {
			this.selectedFiles.splice(index, 1);
			if (viewfile.origin === 'UPLOAD') {
				var success = uploadService
						.removeFromUploadListFunction(viewfile);
				if (!success) {
					alert('impossible to remove file from upload list');
				}
			}
		}
	}

	this.isEmpty = function(list) {
		if (list !== undefined) {
			return list.length == 0;
		}
		return true;
	};

	this.getFileIndex = function(filelist, file) {
		var arrayLength = filelist.length;
		for (var i = 0; i < arrayLength; i++) {
			if (filelist[i].id === file.id) {
				return i;
			}
		}
		return -1;
	}

	this.showDialog = function(dialogid, onok) {
		$(dialogid).dialog({
			modal : true,
			width : 700,
			show : {
				effect : 'fade',
				duration : 1000
			},
			hide : {
				effect : 'fade',
				duration : 1000
			},
			buttons : {
				Ok : function() {
					$(this).dialog("close");
					if (onok) {
						onok();
					}
				}
			}
		});
	};

});