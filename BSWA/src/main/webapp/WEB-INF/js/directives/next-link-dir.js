app.directive('initNextButton', function($rootScope) {
	return {
		scope : {},
		link : function(scope) {

			scope.currentCreateStep = 1;
			scope.nextClicked = function() {
				scope.currentCreateStep++;
				$rootScope.$broadcast('nextStepClicked');
			};

			scope.$on('$routeChangeSuccess', function() {
				scope.currentCreateStep = 1;
			});
		},
		templateUrl : '/templates/nextButton.html'
	};
});