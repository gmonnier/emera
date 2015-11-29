app.controller('fileManagerController', function(uploadService) {

	this.title = 'Generic file manager';
	this.uid = 'generic';
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

	this.selectFile = function(elemId) {
		var elem = document.getElementById(elemId);
		if (elem && document.createEvent) {
			var evt = document.createEvent("MouseEvents");
			evt.initEvent("click", true, false);
			elem.dispatchEvent(evt);
		}
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