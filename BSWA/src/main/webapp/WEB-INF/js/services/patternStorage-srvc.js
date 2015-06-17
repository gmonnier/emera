services.factory('PatternStorageSrvc', ['$http', function($http) {
	
	return {
		getAllPatterns: function() {
			 var promiseOnPatterns = $http({method: 'GET', url: '/ws-resources/datastorage/extractionPatterns'}).
			then(function (response) {
			// The return value gets picked up by the then in the controller.
				return response.data;
			}, function(error) {
				return null;
			});
		   return promiseOnPatterns;
		}
	};
	
}]);