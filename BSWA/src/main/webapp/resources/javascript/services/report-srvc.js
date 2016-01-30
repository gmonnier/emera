services.factory('reportSrvc', ['$http', function($http) {
	
	return {
		getReport: function(analyseID) {
			var urlvar = '/ws-resources/analysis/report/' + analyseID;
			var promiseOnReport = $http({method: 'GET', url: urlvar}).
			then(function (response) {
			// The return value gets picked up by the then in the controller.
				return response.data;
			}, function(error) {
				return null;
			});
			return promiseOnReport;
		},
		
		getPDFResult: function(analyseID) {
			var urlvar = '/ws-resources/analysis/report/' + analyseID + '/pdf' ;
			var promiseOnPDF = $http({method: 'GET', url: urlvar}).
			then(function (response) {
				window.location=urlvar;
				return response.data;
			}, function(error) {
				return null;
			});
			return promiseOnPDF;
		},
		
		getCSVResult: function(analyseID) {
			var urlvar = '/ws-resources/analysis/report/' + analyseID + '/csv' ;
			var promiseOnCSV = $http({method: 'GET', url: urlvar}).
			then(function (response) {
				return response.data;
				window.location=urlvar;
			}, function(error) {
				return null;
			});
			return promiseOnCSV;
		}
	};
	
}]);