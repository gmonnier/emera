services.factory('analysisMgtService', ['$http','$rootScope','$interval','user', function($http,$rootScope,$interval,user) {
	
	var listRunningAnalysis = [];
	var listProcessedAnalysis = [];
	var netConfig = null;
	
	var sortType = "date";
	function nameCompare(analysis1,analysis2) {
	  if (analysis1.report.analyseConfig.selectedDataFiles[0].name < analysis2.report.analyseConfig.selectedDataFiles[0].name)
		 return -1;
	  if (analysis1.report.analyseConfig.selectedDataFiles[0].name > analysis2.report.analyseConfig.selectedDataFiles[0].name)
		 return 1;
	  return 0;
	}
	
	function completionDateCompare(analysis1,analysis2) {
	  if (analysis1.completionDate < analysis2.completionDate)
		 return 1;
	  if (analysis1.completionDate > analysis2.completionDate)
		 return -1;
	  return 0;
	}
	
	function updateListSort(analysisList) {
		if(sortType === "date") {
			listProcessedAnalysisTmp.sort(completionDateCompare);
		} else {
			listProcessedAnalysisTmp.sort(nameCompare);
		}
	}
	
	// Check to make sure poller doesn't already exist
    var requestinfos = function() {
		
		var urlbase = '/ws-resources/analysis/appinfos/';
		if(user.current.hasOwnProperty('user_id')) {
			urlbase += user.current.user_id;
		} else {
			urlbase += 'guest';
		}
		
		$http.get(urlbase).then(
			function(response) {
				
				listRunningAnalysisTmp = response.data.runningAnalysis;		
				listProcessedAnalysisTmp = response.data.processedAnalysis;		
				netConfig = response.data.networkConfig;
				
				updateListSort(listProcessedAnalysisTmp);
				
				var notifyRunningLengthChanged = false;
				var notifyCompleteLengthChanged = false;
				if(listRunningAnalysisTmp.length !== listRunningAnalysis.length) {
					notifyRunningLengthChanged = true;
				}
				if(listProcessedAnalysisTmp.length !== listProcessedAnalysis.length) {
					notifyCompleteLengthChanged = true;
				}
				
				listProcessedAnalysis = listProcessedAnalysisTmp;
				listRunningAnalysis = listRunningAnalysisTmp;
				if(notifyRunningLengthChanged) {
					notifyRunningLengthListeners();
				}
				if(notifyCompleteLengthChanged) {
					notifyCompleteLengthListeners();
				}
				
				// Always notify these listeners
				notifyInfoListeners();
				notifyNetConfigChanged();
			}
		);
    }
	
	// make http call at singleton initialization
	requestinfos();
	// start polling
	var pollInterval; 
	function startPolling() {
		// Don't start polling if already polling
        if ( angular.isDefined(pollInterval) ) return;
		pollInterval = $interval(requestinfos, 2000);
	}
	
	function stopPolling() {
		if (angular.isDefined(pollInterval)) {
            $interval.cancel(pollInterval);
            pollInterval = undefined;
        }
	}
	
	function notifyRunningLengthListeners() {
		$rootScope.$broadcast('runAnalysisCountChanged', listRunningAnalysis);
	}
	
	function notifyCompleteLengthListeners() {
		$rootScope.$broadcast('completeAnalysisCountChanged', listProcessedAnalysis);
	}
	
	function notifyInfoListeners() {
		$rootScope.$emit('updateRuns', listRunningAnalysis);
	}
	
	function notifyNetConfigChanged() {
		$rootScope.$emit('netConfigChanged', netConfig);
	}
	
	return {
		stopAllAnalyses: function() {
			// $http returns a promise, which has a then function, which also returns a promise
			var userID = 'guest';
			if(user.current.hasOwnProperty('user_id')) {
				userID = user.current.user_id;
			}
			
			var url = '/ws-resources/analysis/stopall?user_id=' + userID;
			var promise = $http.delete(url);
			// Return the promise to the controller
			return promise;
		},
		
		requestAnalysisStatus: function(analyseID, status) {
			var statusRequest = {analyseId : analyseID, newStatus : status};
			var request = $http({method: 'POST', 
							url: '/ws-resources/analysis/statusrequest',
							data: statusRequest});
		},
		
		getRuns: function() {
			return listRunningAnalysis;
		},
		
		getCompleted: function() {
			return listProcessedAnalysis;
		},
		
		getNetworkConfig: function() {
			return netConfig;
		},
		
		requestStartPolling: function() {
			startPolling();
		},
		
		requestStopPolling: function() {
			 stopPolling();
		},
		
		setSortType: function(newSortType) {
			if(sortType != newSortType) {
				sortType = newSortType;
				updateListSort(listProcessedAnalysis);
				notifyCompleteLengthListeners();
			}
		}
	};
	
	
}]);