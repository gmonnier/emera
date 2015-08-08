appControllers.controller('runningCtrl', function ($scope,$http, analysisMgtService, reportSrvc, user) {
	
	// init with current runs
	$scope.runningAnalyses = analysisMgtService.getRuns();
	$scope.completeAnalyses = analysisMgtService.getCompleted();
	
	analysisMgtService.requestStartPolling();
	
	$scope.analyseDisplayedResult = null;
	
	$scope.stopAll = function() {
		$scope.showConfirmDialog("#deleteAllConfirmationDialog", analysisMgtService.stopAllAnalyses);
	}
	
	$scope.sortByDate = function() {
		analysisMgtService.setSortType("date");
	}
	
	$scope.sortByName = function() {
		analysisMgtService.setSortType("name");
	}
	
	$scope.expandAll = function() {
		$("h3[aria-expanded='false']").click();
	}
	
	$scope.collapseAll = function() {
		$("h3[aria-expanded='true']").click();
	}
	
	$scope.outputFileType = "PDF";
	$scope.additionalAnalysisType;
	$scope.additionalAnalysisCompareRequest = function(analysisRef, analysisComp) {
		$scope.additionalAnalysisType = "Comparison Analysis";
		$scope.showConfirmDialog("#additionalAnalysisDialog", function() { additionalAnalysisCompare(analysisRef,analysisComp) });
	}
	var additionalAnalysisCompare = function(analysisRef, analysisComp) {
		// Init stored Libraries files
		var analysisParam = {analysisID1 : analysisRef, analysisID2 : analysisComp, outputFileType : $scope.outputFileType};
		$http({method: 'POST', url: '/ws-resources/additional/analysisCompare', data: analysisParam}).
			success(function(data, status, headers, config) {
				$("#additionnalAnalysisMessage").css('color','black');
				$("#additionnalAnalysisMessage").text("Additionnal analysis succesfully requested to the server");
				$scope.showInfoDialog("#additionnalAnalysisSentDialog");
			}).
			error(function(data, status, headers, config) {
				$("#additionnalAnalysisMessage").css('color','red');
				$("#additionnalAnalysisMessage").text("Unable to generate this additionnal analysis");
				$scope.showInfoDialog("#additionnalAnalysisSentDialog");
			});
	}
	
	$scope.deleteAdditionalRequest = function(analysisID, additionalFileName) {
		$scope.showConfirmDialog("#deletAdditionalConfirm", function() { deleteAdditional(analysisID,additionalFileName) });
	}
	var deleteAdditional = function(analysisID, additionalFileName) {
		var urldelete = '/ws-resources/additional/' + analysisID + '/' + additionalFileName;
		$http({method: 'DELETE', url: urldelete});
	}
	
	$scope.downloadAdditionalAnalysis = function(analysisID, viewFile) {
		var urlvar = '/ws-resources/additional/' + analysisID + '/' + viewFile.name;
		window.location=urlvar;
	}
	
	$scope.openGraphData = function(analyse) {
		$scope.analyseDisplayedResult=analyse;
	}
	
	$scope.downloadCSV = function(analyseID) {
		var urlvar = '/ws-resources/analysis/report/' + analyseID + '/csv' ;
		
		var uid = 'guest';
		if(user.current.hasOwnProperty('user_id')) {
			uid = user.current.user_id;
		}
		urlvar += '?user_ID=' + uid;
		window.location=urlvar;
	};
	
	$scope.downloadPDF = function(analyseID) {
		var urlvar = '/ws-resources/analysis/report/' + analyseID + '/pdf' ;
		
		var uid = 'guest';
		if(user.current.hasOwnProperty('user_id')) {
			uid = user.current.user_id;
		}
		urlvar += '?user_ID=' + uid;
		window.location=urlvar;
	};
	
	// listen for the event in the relevant $scope
	var runLengthListener = $scope.$on('runAnalysisCountChanged', function (event, data) {
		$scope.runningAnalyses = data;
	});
	// listen for the event in the relevant $scope
	var completeLengthListener = $scope.$on('completeAnalysisCountChanged', function (event, data) {
		$scope.completeAnalyses = data;
	});
	
	 // $scope $destroy
    $scope.$on('$destroy', function() {
		// cancel all listeners
		runLengthListener();
		completeLengthListener();
		// stop service polling
		analysisMgtService.requestStopPolling();
	});
	
	$scope.showConfirmDialog = function(dialogid, onok ) {
		$(dialogid).dialog({
			modal: true,
			height: 200,
			width: 520,
			buttons: {
			Cancel: function() {
				$( this ).dialog( "close" );
			},
			Continue: function() {
				$( this ).dialog( "close" );
				if(onok) {
					onok();
				}
			}
			}
		});
	}
	
	$scope.showInfoDialog = function(dialogid, onok) {
		$(dialogid).dialog({
			modal: true,
			height: 170,
			width: 520,
			buttons: {
			Ok: function() {
				$( this ).dialog( "close" );
				if(onok) {
					onok();
				}
			}
			}
		});
	}
});

app.directive('postRunningRepeatDirective', function() {
	return function(scope, element, attrs) {
		if (scope.$last){
			$(".accRunning").accordion({  heightStyle: "content",autoHeight: false,clearStyle: true, header: "h3", collapsible: true });
			$('.progressbar').progressbar({});
		}
	};
});

app.directive('postCompleteRepeatDirective', function() {
	return function(scope, element, attrs) {
		if (scope.$last){
			$(".accComplete").accordion({ active: "false"
			, heightStyle: "content"
			,autoHeight: false
			,clearStyle: true
			,header: "h3"
			,collapsible: true 
			,beforeActivate: function( event, ui ) {
				if(ui.oldHeader.length > 0) {
					// This accordion has been deactivated
					$(this).find(".ui-accordion-content").toggleClass("overflow-visible");
				}
			}
			,activate: function( event, ui ) {
				if(ui.newHeader.length > 0) {
					// This accordion has been activated
					$(this).find(".ui-accordion-content").toggleClass("overflow-visible");
				}
			}});
			
			
			$( ".sortMenu" ).menu({
			  items: "> :not(.ui-widget-header)"
			}
			);
			$(".sortMenu").hide();
			$("#sortToolItem").hover(function(e){
				$(".sortMenu").width(150);
				$(".sortMenu").show();
			}, function(){
				$(".sortMenu").hide();
				$(".sortMenu").width(0);
			});
			 
			
			
			var menu = $( ".additionalAnalysisMenu" ).menu();
			$(menu).mouseleave(function () {
				menu.menu('collapseAll');
			});
		}
	};
});
