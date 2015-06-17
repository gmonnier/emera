appControllers.controller('navController', function ($scope) {

	$scope.menuClicked = function(val) {
		
		$("#subSelectBar").removeClass();
		
		if( val === 1) {
			$("#subSelectBar").addClass("select1");
		} else if(val === 2) {
			$("#subSelectBar").addClass("select2");
		} else if(val === 3) {
			$("#subSelectBar").addClass("select3");
		} else if(val === 4) {
			$("#subSelectBar").addClass("select4");
		}
	}
	
	
});