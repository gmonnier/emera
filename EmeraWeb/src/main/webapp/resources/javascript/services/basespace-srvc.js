services.factory('basespaceService', ['$http', function($http) {

	var promiseOnStatus;
	var promiseOnUser;
	var promiseOnRuns;
	
	return {
		userinfo: function() {
		   if ( !promiseOnUser ) {
			   promiseOnUser = $http({method: 'GET', url: '/ws-resources/bs-model/userinfo'}).
				then(function (response) {
					// The return value gets picked up by the then in the controller.
					return response.data;
				}, function(error) {
					return {name:"err usr", id:"err id"};
				});
		   }
		   return promiseOnUser;
		},
		
		
		connectionstatus: function() {
		    if ( !promiseOnStatus ) {
			   promiseOnStatus = $http({method: 'GET', url: '/ws-resources/bs-model/connectionstatus'}).
				then(function (response) {
					return response.data === 'true';
				}, function(error) {
					return false;
				});
		   }
		   return promiseOnStatus;
		},
		
		listruns: function() {
		    if ( !promiseOnRuns ) {
			    promiseOnRuns = $http({method: 'GET', url: '/ws-resources/bs-model/userruns'}).
				then(function (response) {
					return response.data;
				}, function(error) {
					return {};
				});
		   }
		   return promiseOnRuns;
		}
	};
	
}]);