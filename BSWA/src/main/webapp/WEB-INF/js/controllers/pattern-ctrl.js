appControllers.controller('patternsCtrl', function ($scope, $http, PatternStorageSrvc) {
	
	$scope.selectedPattern = null;
	
	$scope.init = function(){
		$scope.newPatternValue = '';
		$scope.newPatternLabel = '';
	}
	$scope.init();
	
	$scope.isCreateNewValid = function() {
		return $scope.newPatternValue.length > 0;
	}
  
	var updatePatternList = function() {
		PatternStorageSrvc.getAllPatterns().then(function(patternListVar) {
			$scope.patterns = patternListVar;
		});
	};
	updatePatternList();

	$scope.select = function(pattern) {
		$scope.configuration.pattern = pattern;
		$scope.selectedPattern = pattern;
	}
	
	$scope.openPatternCreationDial = function(file) {
		
		$scope.init();
		
		// Open dialog
		$("#createPatternDialog").dialog({
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
					
					// Ask server to add selected pattern
					var request = $http({method: 'POST', url: '/ws-resources/process/addPattern', data: $scope.newPatternValue});
					request.success(function(data, status, headers, config) {
						updatePatternList();
						if(data) {
							$scope.select(data);
						}
					}).error(function(data, status, headers, config) {
						$scope.showDialog("#dialog-pattern-creation-failed" );
					});
				}
			}
		});
	}
	
	
}).directive('applyEffects', function() {
	return function(scope, element, attrs) {
		if (scope.$last){
			$( ".selectPatItem" ).click(function() {
				$( ".patternPreview" ).clearQueue();
				$( ".patternPreview" ).stop();
				$( ".patternPreview" ).hide();
				$( ".patternPreview" ).fadeIn( "slow", function() {});
			});
		}
	};
});
