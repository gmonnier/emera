services.factory('uploadService', ['$http','FileUploader', 'analysisMgtService', '$rootScope', function($http, FileUploader , analysisMgtService, $rootScope) {
	
	var uploadDataFiles = [];
	var uploadLibraryFiles = [];
	
	var dataUploader = new FileUploader();
	var libUploader = new FileUploader();
	
	var currentUploadingFileName = "";
	var currentUploadingProgress = -1;
	var currentAnalyseID = '';
	
	function getReadableFileSizeString(fileSizeInBytes) {
		var i = -1;
		var byteUnits = [' kB', ' MB', ' GB', ' TB', 'PB', 'EB', 'ZB', 'YB'];
		do {
			fileSizeInBytes = fileSizeInBytes / 1024;
			i++;
		} while (fileSizeInBytes > 1024);

		return Math.max(fileSizeInBytes, 0.1).toFixed(2) + byteUnits[i];
	};
	
	var addToUploadList = function(fileItem, uploadList, addToViewFileListFunc) {
		if (fileItem.file) {
			// Add the view file to the controller
			var readSize = getReadableFileSizeString(fileItem.file.size);
			var viewFile = {origin:"UPLOAD", name: fileItem.file.name, dateCreated: fileItem.file.lastModifiedDate, id: fileItem.file.name, size : fileItem.file.size, readableSize : readSize}; 
			var ok = addToViewFileListFunc(viewFile);
			if(ok) {
				uploadList.push({assViewfile : viewFile, fileItem: fileItem});
				return true;
			}
		}
		return false;
	}
	
	var removeFomUploadList = function(viewFile, uploadList, uploader) {
		if(viewFile) {
			// Remove the view file from the upload list
			var arrayLength = uploadList.length;
			for (var i = 0; i < arrayLength; i++) {
				var currentFile = uploadList[i].assViewfile;
				if(currentFile === viewFile) {
					uploader. removeFromQueue(uploadList[i].fileItem);
					uploadList.splice(i, 1);
					return true;
				}
			}
		}
		// the given view file was not found in the array
		return false;
	}
	
	/*
	* Usage example:
	* upload(uploadDataFiles,'/ws-resources/datastorage/uploadDataFile')"
	*/
	var startUploadSelectedFiles = function(analyseid, uploadurl, uploader, startNextUploader) {
		
		currentAnalyseID = analyseid;
		
		analysisMgtService.requestAnalysisStatus(analyseid, 'RETRIEVE_FILES');
		
		// Associate analyse ID to this Request
		var customformData = [{analyseID: analyseid}];
		uploader.onBeforeUploadItem = function(item) {
			Array.prototype.push.apply(item.formData, customformData);
			item.url = uploadurl + '?analyseid=' + analyseid;
			currentUploadingFileName = item.file.name;
			notifyUploadFileChangedListeners(analyseid, currentUploadingFileName);
		};

		uploader.onProgressAll = function(progress) {
			if(currentUploadingProgress !== progress) {
				currentUploadingProgress = progress;
				notifyProgressListeners(analyseid, progress );
			}
		};
		uploader.onCompleteAll = function() {
			uploader.clearQueue();
			// reinit globals values
			currentUploadingFileName = "";
			currentUploadingProgress = -1;
			currentAnalyseID = '';
			
			if(startNextUploader && libUploader.queue.length > 0) {
				startUploadSelectedFiles(analyseid,'/ws-resources/datastorage/uploadLibFile', libUploader, false);
			} else {
				// Notify all downloads are completed
				notifyProgressListeners(analyseid, -1 );
			}
		};
		uploader.onErrorItem = function(fileItem, response, status, headers) {
			analysisMgtService.requestAnalysisStatus(analyseid, 'UPLOAD_ERROR');
			libUploader.cancelAll();
			dataUploader.cancelAll();
			libUploader.clearQueue();
			dataUploader.clearQueue();
			
		};
		
		uploader.uploadAll ();
	}
	
	function notifyProgressListeners(analyseId, value ) {
		var progress = {analyseId : analyseId, progressval: value};
		$rootScope.$emit('uploadProgressChanged', progress);
	}
	
	function notifyUploadFileChangedListeners(analyseId, filename ) {
		var fileInfo = {analyseId : analyseId, filename: filename};
		$rootScope.$emit('uploadingFileChanged', fileInfo);
	}
	
	return {
	  
    addToUploadData: function(file, addToViewFileListFunc) {
		return addToUploadList(file,uploadDataFiles, addToViewFileListFunc);
    },
	
	addToUploadLibs: function(file, addToViewFileListFunc) {
		return addToUploadList(file,uploadLibraryFiles, addToViewFileListFunc);
    },
	
	removeFromUploadData: function(viewFile) {
		return removeFomUploadList(viewFile,uploadDataFiles, dataUploader);
    },
	
	removeFromUploadLibs: function(viewFile) {
		return removeFomUploadList(viewFile,uploadLibraryFiles, libUploader);
    },
	
	getDataUploader: function(viewFile) {
		return dataUploader;
    },
	
	getLibsUploader: function(viewFile) {
		return libUploader;
    },
	
	getCurrentUploadingFileName: function() {
		return currentUploadingFileName;
    },
	
	getCurrentUploadingProgress: function() {
		return currentUploadingProgress;
    },
	
	getCurrentAnalyseID: function() {
		return currentAnalyseID;
    },
	
	clearLists: function() {
		uploadDataFiles = [];
		uploadLibraryFiles = [];
		libUploader.clearQueue();
		dataUploader.clearQueue();
    },
	
	uploadNeeded: function() {
		return uploadDataFiles.length > 0 || uploadLibraryFiles.length > 0;
    },
	
	showList: function() {
		alert("upload data length " + dataUploader.queue.length + "  updatafiles " + uploadDataFiles);
		alert("upload data length " + libUploader.queue.length + "  updatafiles " + uploadLibraryFiles);
	},
	
	startUpload: function(analyseid) {
		
		// Trick to start only one uploader at the same time... could be improved a lot
		if(dataUploader.queue.length > 0) {
			startUploadSelectedFiles(analyseid,'/ws-resources/datastorage/uploadDataFile', dataUploader, true);
		} else {
			startUploadSelectedFiles(analyseid,'/ws-resources/datastorage/uploadLibFile', libUploader, false);
		}
	}
  };
 
}]);