app.controller('fileManagerController', function() {
	this.title = 'Generic file manager';
	this.storedFilesList = [];
	this.selectedFiles = [];
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