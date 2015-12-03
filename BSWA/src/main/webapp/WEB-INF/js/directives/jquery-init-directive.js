app.directive('jqueryInit', function($timeout) {
	return {
		// Restrict it to be an attribute in this case
		restrict : 'A',
		link : function(element, attrs) {
			$timeout(function() {
				$('.acc').accordion({
					heightStyle : "content",
					autoHeight : false,
					clearStyle : true,
					header : "h3",
					collapsible : true
				});
				
			});
		}
	};
});