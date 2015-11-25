app.directive('initNextButton', function($rootScope) {
	return {
		scope : {
			MAX_STEP: "@steps"
		},
		link : function(scope, element, attrs) {

			scope.currentCreateStep = 1;
			
			scope.nextClicked = function() {
				scope.$emit('nextStepClicked', scope.initNextButton);
			};

			scope.$on('$routeChangeSuccess', function() {
				scope.currentCreateStep = 1;
			});
		},
		templateUrl : '/templates/nextButton.html'
	};
});