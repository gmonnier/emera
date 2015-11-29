app.controller('fileManagerController', function(uploadService) {

	this.title = 'Generic file manager';
	this.uid = 'generic';
	this.localFileInputSelector = "file-input-" + this.uid;
	this.storedFilesList = [];
	this.selectedFiles = [];
	this.fileUploader = uploadService.getDataUploader();
	this.baseSpaceAllowed = true;

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
					$rootScope.$emit('addSelectedFile');
					$(this).dialog("close");
					$scope.$apply();
				}
			}
		});
		// clear selection and close accordions
		$('.accrun .ui-state-focus').removeClass('ui-state-focus');
	};

	this.selectFile = function() {
		var elem = document.getElementById(this.localFileInputSelector);
		if (elem && document.createEvent) {
			var evt = document.createEvent("MouseEvents");
			evt.initEvent("click", true, false);
			elem.dispatchEvent(evt);
		}
	};
	
	this.isEmpty = function(list) {
		if(list !== undefined){
			return list.length == 0;
		}
		return true;
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
		$(".menu-load-file").menu();
	};
});