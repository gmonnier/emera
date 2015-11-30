app.controller('fileManagerController', function(uploadService, $rootScope, $scope) {

	this.title = 'Generic file manager';
	this.uid = 'generic';
	this.localFileInputSelector = "file-input-" + this.uid;
	this.storedFilesList = [];
	this.selectedFiles = [];
	this.baseSpaceAllowed = true;
	
	this.fileType = 'data';
	if (this.fileType === 'library') {
		this.fileUploader = uploadService.getDataUploader();
		this.addToUploadListFunction = uploadService.addToUploadLibs;
		this.removeFromUploadListFunction = uploadService.removeFromUploadLibs;
	} else {
		this.fileUploader = uploadService.getLibsUploader();
		this.addToUploadListFunction = uploadService.addToUploadData;
		this.removeFromUploadListFunction = uploadService.removeFromUploadData;
	}


	this.openBaseSpaceSelectionDial = function() {
		// Open dialog
		$("#basespacedialog").dialog({
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
					// emit event to ask bs-model-ctrl to add selected file to
					// current model
					$rootScope.$emit('requestBaseSpaceDialogClose');
					$(this).dialog("close");
				}
			}
		});
		// clear selection and close accordions
		$('.accrun .ui-state-focus').removeClass('ui-state-focus');
	};
	
	this.addViewFile = angular.bind(this, function(file) {
		if(file !== null) {
			var ok = this.checkDuplicates(this.selectedFiles,file);
			if(ok) {
				this.selectedFiles.push(file);
				return true;
			} else {
				var dialogSelector = "#dialog-duplicate-warning-" + this.uid;
				this.showDialog(dialogSelector);
				return false;
			}
		}
	}
	);
	
	this.fileUploader.onAfterAddingFile = angular.bind(this, function(fileItem) {
		var success = this.addToUploadListFunction(fileItem, this.addViewFile); 
		if(!success){
			this.fileUploader.removeFromQueue(fileItem);
		}
		}
	);

	this.checkDuplicates = function(filelist, file) {
		var arrayLength = filelist.length;
		for (var i = 0; i < arrayLength; i++) {
			if(filelist[i].id === file.id) {
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
		var index = this.getFileIndex(this.selectedFiles,viewfile);
		if(index != -1) {
			this.selectedFiles.splice(index, 1);
			if(viewfile.origin === 'UPLOAD') {
				// TODO Remove reference to upload DATA (too specific)
				var success = uploadService.removeFromUploadData(viewfile);
				if(!success) {
					alert('impossible to remove file from upload list');
				}
			}
		}
	}
	
	this.isEmpty = function(list) {
		if(list !== undefined){
			return list.length == 0;
		}
		return true;
	};
	
	this.getFileIndex = function (filelist, file) {
		var arrayLength = filelist.length;
		for (var i = 0; i < arrayLength; i++) {
			if(filelist[i].id === file.id) {
				return i;
			}
		}
		return -1;
	}

	
	this.showDialog = function(dialogid, onok) {
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
	};
	
	// Event listener when close with OK has been requested
	var unbind = $rootScope.$on('addSelectedBaseSpaceFile', angular.bind(this, function(event, data){
		this.addViewFile(data);
		$scope.$apply();
    }));
	$scope.$on('$destroy', unbind);
	
}).directive('initAccordions', function() {
	return function(scope, element, attrs) {
		$('.acc').accordion({
			heightStyle : "content",
			autoHeight : false,
			clearStyle : true,
			header : "h3",
			collapsible : true
		});
		$(".menu-load-file").menu();
	};
});