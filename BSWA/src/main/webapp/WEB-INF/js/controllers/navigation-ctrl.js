appControllers.controller('navController', function($scope) {

	$scope.menuClicked = function(val) {

		$("#subSelectBar").removeClass();

		if (val === 1) {
			$("#subSelectBar").addClass("select1");
		} else if (val === 2) {
			$("#subSelectBar").addClass("select2");
		} else if (val === 3) {
			$("#subSelectBar").addClass("select3");
		} else if (val === 4) {
			$("#subSelectBar").addClass("select4");
		}
	}

});

app.directive('navDirective', function() {
	return function(scope, element, attrs) {
		$(document).ready(function() {
			var stickyNavTop = $('.nav').offset().top;

			var stickyNav = function() {
				var scrollTop = $(window).scrollTop();
				var headerHeight = $('#topToolBarLogo').height();
				if (scrollTop > 40) {
					$('.nav').css('top', '0px' );
				} else {
					$('.nav').offset({ top: 80 });
				}
			};

			stickyNav();

			$(window).scroll(function() {
				stickyNav();
			});
		});
	};
});
