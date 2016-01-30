appControllers.controller('topToolBarCtrl', function ($scope,$rootScope, $http, basespaceService, user ) {
	
	user.onAuthenticationSuccess(function() {
		$( '#loginDialog' ).dialog( "close" );
	});
	
	var submitLogin = function() {
		$( "#loginForm" ).submit();
	};
	
	$scope.showLoginDial = function() {
		showDialog('#loginDialog', submitLogin);
	}
	
	var showDialog = function(dialogid, onok) {
		$(dialogid).dialog({
			modal: true,
			resizable: false,
			width: 400,
			show: {
		        effect: 'fade',
		        duration: 500
		    },
		    hide: {
		        effect: 'fade',
		        duration: 500
		    },
			buttons: {
			"Log in": function() {
			
				if(onok) {
					onok();
				}
			}
			}
		});
	}

});