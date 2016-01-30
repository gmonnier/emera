// List of all applications controllers
var appControllers = angular.module('appControllers', []);
// List of all applications directives
var directives = angular.module('appDirectives', []);
// List of all applications services
var services = angular.module('appServices', []);
// inject angular file upload directives and services.
var app = angular.module('app', [ 'ngRoute', 'routeStyles', 'appControllers',
		'appServices', 'ngAnimate', 'angularFileUpload', 'UserApp' ]);
app.run(function(user) {
	user.init({
		appId : '554d0317250f5'
	});
});
Offline.on('up', function() {
	// similar behavior as an HTTP redirect
	window.location.href = "/";
});
/*
 * var run = function(){ var req = new XMLHttpRequest(); req.timeout = 5000;
 * req.open('GET', 'ws-resources/connection', true); req.send(); }
 * setInterval(run, 3000);
 */
app.config([ '$routeProvider', function($routeProvider) {
	$routeProvider.when('/analysis-selection', {
		templateUrl : 'html/analysisSelection.html',
		controller : 'analysisSelectionCtrl',
		css: 'css/analysis-selection.css',
		public : true
	}).when('/create-split-analysis', {
		templateUrl : 'html/createSplitPan.html',
		controller : 'createSplitCtrl',
		css: ['css/createSplitPan.css', 'css/baseSpaceDial.css', 'css/patternCreateDial.css'],
		public : true
	}).when('/create-lookup-analysis', {
		templateUrl : 'html/createLookupPan.html',
		controller : 'createLookupCtrl',
		css: ['css/createLookupPan.css', 'css/baseSpaceDial.css'],
		public : true
	}).when('/running', {
		templateUrl : 'html/runningPan.html',
		controller : 'runningCtrl',
		css: 'css/running-pan.css',
		public : true
	}).when('/config', {
		templateUrl : 'html/networkConfigPan.html',
		controller : 'networkCtrl',
		css: ['css/configPan.css',
		      'vendor/ammap_3.13.3.free/ammap/ammap.css',
		      'vendor/Three.js'],
		public : true
	}).when('/', {
		templateUrl : 'html/homePan.html',
		css: ['css/home.css', 'vendor/slider/csss_engine1/style.css'],
		public : true
	}).otherwise({
		templateUrl : 'html/homePan.html',
		css: ['css/home.css', 'vendor/slider/csss_engine1/style.css']
	});
} ]);
(function(){
'use strict';
    angular.module('routeStyles', ['ngRoute'])
        .directive('head', ['$rootScope','$compile','$interpolate',
            function($rootScope, $compile, $interpolate){
                // this allows for support of custom interpolation symbols
                var startSym = $interpolate.startSymbol(),
                    endSym = $interpolate.endSymbol(),
                    html = ['<link rel="stylesheet" ng-repeat="(routeCtrl, cssUrl) in routeStyles" ng-href="',startSym,'cssUrl',endSym,'">'].join('');
                return {
                    restrict: 'E',
                    link: function(scope, elem){
                        elem.append($compile(html)(scope));
                        scope.routeStyles = {};
                        $rootScope.$on('$routeChangeStart', function (e, next) {
                            if(next && next.$$route && next.$$route.css){
                                if(!angular.isArray(next.$$route.css)){
                                    next.$$route.css = [next.$$route.css];
                                }
                                angular.forEach(next.$$route.css, function(sheet){
                                    scope.routeStyles[sheet] = sheet;
                                });
                            }
                        });
                        $rootScope.$on('$routeChangeSuccess', function(e, current, previous) {
                            if (previous && previous.$$route && previous.$$route.css) {
                                if (!angular.isArray(previous.$$route.css)) {
                                    previous.$$route.css = [previous.$$route.css];
                                }
                                angular.forEach(previous.$$route.css, function (sheet) {
                                    scope.routeStyles[sheet] = undefined;
                                });
                            }
                        });
                    }
                };
            }
        ]);

})();
appControllers.controller('bsModelCtrl', function ($scope,$rootScope, basespaceService) {
	
	$scope.selectedBaseSpaceFile = null;
	$scope.showAllRuns = false;
	$scope.maxDisplayedRuns = 5;
	$scope.userruns = []
	
	$scope.setSelectedBSResource = function(file) {
		$scope.selectedBaseSpaceFile = file;
	}
	
	$scope.toggleShowRuns = function(){
		$scope.showAllRuns = !$scope.showAllRuns;
	}
	
	$scope.getDisplayedRuns = function() {
		if($scope.showAllRuns == true) {
			return $scope.userruns;
		} else {
			// display only the first five items
			return $scope.userruns.slice(0,$scope.maxDisplayedRuns);
		}
	}
	
	basespaceService.listruns().then(function(listruns) {
		$scope.userruns = listruns;
    });
  
	basespaceService.userinfo().then(function(userinfovar) {
		$scope.user = userinfovar;
    });
	
	basespaceService.connectionstatus().then(function(connectionState) {
		$scope.connectionstatus = connectionState;
    });
	
	// Event listener when close with OK has been requested
	var unbind = $rootScope.$on('requestBaseSpaceDialogClose', function(event, fileManagerController){
		if($scope.selectedBaseSpaceFile !== null) {
			fileManagerController.addViewFileAndApply($scope.selectedBaseSpaceFile);
			//$rootScope.$emit('addSelectedBaseSpaceFile', $scope.selectedBaseSpaceFile);
		}
    });
	$scope.$on('$destroy', unbind);
	
});

app.directive('postRepeatDirectiveBsDial', function() {
	return function(scope, element, attrs) {
		if (scope.$last){
			//alert($(".accrun > div").length + "   " + scope.$index);
			$(".accrun").accordion({  heightStyle: "content",autoHeight: false,clearStyle: true, active: false, header: "h3", collapsible: true });
		}
	};
});

app.directive('refreshAcc', function() {
	return function(scope, element, attrs) {
		$(".accrun").accordion("refresh");
	};
});




appControllers.controller('createLookupCtrl', function ($scope,$rootScope, $http, $location, uploadService, user) {
	
	// init upload lists
    uploadService.clearLists();
    
	// Retrieve init processing configuration
	$http({method: 'GET', url: '/ws-resources/process/confinit'}).
    success(function(data, status, headers, config) {
		$scope.configuration = data;
		$scope.configuration.pattern = null;
    }).
    error(function(data, status, headers, config) {
		$scope.configuration = null;
    });
	
	$scope.isEmpty = function(list) {
		if(list !== undefined){
			return list.length == 0;
		}
		return true;
	}
	
    $scope.$on('nextStepClicked', function (evt, directiveScope) {
    	if((directiveScope.currentCreateStep+1) > directiveScope.MAX_STEP) {
			if($scope.validatePatternSelection()) {
				$scope.enqueueProcessing();
			} else {
				$scope.showDialog("#dialog-unvalid-pattern");
			}
		} else {
			if(!$scope.validateFileSelection()) {
				$scope.showDialog("#dialog-unvalid-parameters");
			} else {
				directiveScope.currentCreateStep += 1;
				$scope.stepValue = directiveScope.currentCreateStep;
			}
		}
    });
	
	$scope.validateFileSelection = function() {
		return $scope.configuration.selectedDataFiles.length > 0 && $scope.configuration.selectedLibraries.length > 0;
	}
	
	$scope.validatePatternSelection = function() {
		return $scope.configuration.pattern !== null;
	}
	
	$scope.enqueueProcessing = function() {
		
		var userID = 'guest';
		if(user.current.hasOwnProperty('user_id')) {
			userID = user.current.user_id;
		}
		var urlenqueue = '/ws-resources/process/enqueue?user_id=' + userID;
		
		var request = $http({method: 'POST', 
							url: urlenqueue,
							data: $scope.configuration});
		
		request.success(function(data, status, headers, config) {
			var uploadNeeded = uploadService.uploadNeeded();
			$scope.uploadmessage = uploadNeeded ? 'Upload process will start now. Please do not leave the page while transfering files to server.' : '';
			if(uploadNeeded && data) {
				// ask the upload service to start uploading files to the server - data = analyse ID
				uploadService.startUpload(data);
			} else {
				if(!data) {
					// should never occurs!
					alert('an error occured when uploading files, unable to retrieve analyse id');
				}
			}
			$scope.showDialog("#dialog-message-ok", function() {
				$scope.changeRoute('/running');
			});
		}).
		error(function(data, status, headers, config) {
			$scope.showDialog("#dialog-message-failed" );
			$scope.changeRoute('/home');
		});
	}
	
	$scope.showDialog = function(dialogid, onok) {
		$(dialogid).dialog({
			modal: true,
			width: 700,
			show: {
		        effect: 'fade',
		        duration: 500
		    },
		    hide: {
		        effect: 'fade',
		        duration: 500
		    },
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
	
	$scope.changeRoute = function(url, forceReload) {
        $scope = $scope || angular.element(document).scope();
        if(forceReload || $scope.$$phase) {
            window.location = url;
        } else {
            $location.path(url);
            $scope.$apply();
        }
    };

});

appControllers.controller('createSplitCtrl', function($scope, $http,$location, uploadService, user, PatternStorageSrvc) {
	
	// init upload lists
    uploadService.clearLists();
    
    var updateSplitPatternList = function() {
		PatternStorageSrvc.getAllSplitPatterns().then(function(splitPatternListVar) {
			$scope.splitPatterns = splitPatternListVar;
		});
	};
	updateSplitPatternList();

	$http({
		method : 'GET',
		url : '/ws-resources/datastorage/dataFiles'
	}).success(function(data, status, headers, config) {
		$scope.storeDataFilesList = data;
	}).error(function(data, status, headers, config) {
		$scope.storeDataFilesList = [];
	});

	// Retrieve init processing configuration
	$http({
		method : 'GET',
		url : '/ws-resources/preprocess/confinit'
	}).success(function(data, status, headers, config) {
		$scope.configuration = data;
	}).error(function(data, status, headers, config) {
		$scope.configuration = null;
	});
	
    $scope.$on('nextStepClicked', function (evt, directiveScope) {
    	if((directiveScope.currentCreateStep+1) > directiveScope.MAX_STEP) {
			if($scope.validateParameters()) {
				$scope.enqueueProcessing();
			} else {
				$scope.showDialog("#dialog-unvalid-parameters");
			}
		}
    });
    
	$scope.toggleSelect = function(splitpattern) {
		var found = false;
		for (i = $scope.configuration.dataSplitterModels.length-1; i >= 0; i--) {
			var currentSplitter = $scope.configuration.dataSplitterModels[i];
			if(currentSplitter.regexp === splitpattern.regexp) {
				$scope.configuration.dataSplitterModels.splice(i, 1);
				found = true;
				break;
			}
		}
		if(!found) {
			$scope.configuration.dataSplitterModels.push(splitpattern);
		}
	}
    
    $scope.enqueueProcessing = function() {
		var userID = 'guest';
		if(user.current.hasOwnProperty('user_id')) {
			userID = user.current.user_id;
		}
		var urlenqueue = '/ws-resources/preprocess/enqueue?user_id=' + userID;
		
		var request = $http({method: 'POST', 
							url: urlenqueue,
							data: $scope.configuration});
		
		request.success(function(data, status, headers, config) {
			var uploadNeeded = uploadService.uploadNeeded();
			$scope.uploadmessage = uploadNeeded ? 'Upload process will start now. Please do not leave the page while transfering files to server.' : '';
			if(uploadNeeded && data) {
				// ask the upload service to start uploading files to the server - data = analyse ID
				uploadService.startUpload(data);
			} else {
				if(!data) {
					// should never occurs!
					alert('an error occured when uploading files, unable to retrieve analyse id');
				}
			}
			$scope.showDialog("#dialog-message-ok", function() {
				$scope.changeRoute('/running');
			});
		}).
		error(function(data, status, headers, config) {
			$scope.showDialog("#dialog-message-failed" );
			$scope.changeRoute('/home');
		});
	}
    
	$scope.validateParameters = function() {
		return !($scope.isEmpty(configuration.dataSplitterModels) || $scope.isEmpty(configuration.selectedDataFiles));
	}
    
    // TODO Refactor this method
	$scope.showDialog = function(dialogid, onok) {
		$(dialogid).dialog({
			modal: true,
			width: 700,
			show: {
		        effect: 'fade',
		        duration: 500
		    },
		    hide: {
		        effect: 'fade',
		        duration: 500
		    },
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
	
	// TODO refactor this method too (common with several controllers)
	$scope.changeRoute = function(url, forceReload) {
        $scope = $scope || angular.element(document).scope();
        if(forceReload || $scope.$$phase) {
            window.location = url;
        } else {
            $location.path(url);
            $scope.$apply();
        }
    };
    
	$scope.isEmpty = function(list) {
		if(list !== undefined){
			return list.length == 0;
		}
		return true;
	};
	
	$scope.isSelected = function(splitPattern) {
		if ($scope.configuration && !$scope.isEmpty($scope.configuration.dataSplitterModels)) {
			for (var i = 0, len = $scope.configuration.dataSplitterModels.length; i < len; i++) {
				if ($scope.configuration.dataSplitterModels[i].regexp === splitPattern.regexp) {
					return true;
				}
			}
		}
		return false;
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

appControllers.controller('navController', function($scope) {

	$scope.menuClicked = function(val) {

		$("#subSelectBar").removeClass();

		if (val === 1) {
			$("#subSelectBar").addClass("select1");
		} else if (val === 2) {
			$("#subSelectBar").addClass("select2");
		} else if (val === 3) {
			$("#subSelectBar").addClass("select3");
		} else if (val === 4) {
			$("#subSelectBar").addClass("select4");
		}
	}

});

app.directive('navDirective', function() {
	return function(scope, element, attrs) {
		$(document).ready(function() {
			var stickyNavTop = $('.nav').offset().top;

			var stickyNav = function() {
				var scrollTop = $(window).scrollTop();
				var headerHeight = $('#topToolBarLogo').height();
				if (scrollTop > 70) {
					$('.nav').css('top', '0px' );
					$('#topToolBar').css('margin-bottom', '100px' );
				} else {
					$('.nav').offset({ top: 80 });
					$('#topToolBar').css('margin-bottom', '125px' );
				}
			};

			stickyNav();

			$(window).scroll(function() {
				stickyNav();
			});
			
			// With the element initially hidden, we can show it slowly:
			$(".toolbaraction").click(function() {
				$("#ngViewFade").clearQueue();
				$("#ngViewFade").stop();
				$("#ngViewFade").hide();
				$("#ngViewFade").fadeIn("slow", function() {
				});
			});

			$("#loginDialog").hide();
			$("#authenticationRequestDialog").hide();
		});
	};
});

appControllers.controller('networkCtrl', function ($scope,$http, $rootScope,  analysisMgtService, user) {
	
	// init with current network config
	$scope.netresources = analysisMgtService.getNetworkConfig() === null? null : analysisMgtService.getNetworkConfig().resources;
	$scope.awsInstances = analysisMgtService.getNetworkConfig() === null? null : analysisMgtService.getNetworkConfig().awsInstances;
	$scope.serverresource = analysisMgtService.getNetworkConfig() === null? null : analysisMgtService.getNetworkConfig().thisServer;
	
	analysisMgtService.requestStartPolling();

	$scope.latlong = {};
	$scope.mapData = [];
	
	$scope.usr = user;
	
	// listen for the event in the relevant $scope
	var netConfigChangedListener = $rootScope.$on('netConfigChanged', function (event, data) {
		if(data !== null) {
			$scope.netresources = data.resources;
			$scope.awsInstances = data.awsInstances;
			$scope.serverresource = data.thisServer;
			var newMapData = [];
			
			// add the server
			if(data.thisServer !== 'undefined' && data.thisServer.location !== 'undefined' && data.thisServer.location !== null && data.thisServer.location.latitude != 0) {
				$scope.latlong["Server"] = {"latitude": data.thisServer.location.latitude , "longitude": data.thisServer.location.longitude};
				newMapData[0] = {"code": "Server","name": data.thisServer.location.cityName,"value": 1.5,"color": "#56ED38", "ID": data.thisServer.IP};
			}
			
			for (i = 0; i < $scope.netresources.length; i++) {
				var resource = $scope.netresources[i];
					if(resource.location !== 'undefined' && resource.location !== null && resource.location.latitude != 0) {
					$scope.latlong["" + (i+1)] = {
						"latitude": resource.location.latitude ,
						"longitude": resource.location.longitude
					};
					newMapData[i+1] = {"code": "" + (i+1),"name": resource.location.cityName,"value": 1,"color": "#eea638", "ID": resource.IP};
				}
			}
			
			// update value only if length changed
			if(newMapData.length !== $scope.mapData.length) {
				$scope.mapData = newMapData;
			}
			newMapData = null;
		}
	
		
	});
	
	$scope.removeDistantResource = function(clientID) {
		if (!user.current.authenticated) {
			showDialog("#authenticationRequestDialog");
			return;
		}
		// Init stored Libraries files
		var urldel = "/ws-resources/netconfig/" + clientID;
		$http({method: 'DELETE', url: urldel}).
			success(function(data, status, headers, config) {
				console.log("client with ID " + clientID + " successfully removed from the distant resources list");
			}).
			error(function(data, status, headers, config) {
				console.log("client with ID " + clientID + " was not able to be removed.");
			});
	}
	
	$scope.startAllAWS = function() {
		if (!user.current.authenticated) {
			showDialog("#authenticationRequestDialog");
			return;
		}
		var urlstart = "/ws-resources/netconfig/startAllAWS";
		$http({method: 'POST', url: urlstart}).
			success(function(data, status, headers, config) {
				console.log("Successfully requested to start all AWS resources");
			}).
			error(function(data, status, headers, config) {
				console.log("Error while trying to start AWS resources");
			});
	}
	
	$scope.stopAllAWS = function() {
		if (!user.current.authenticated) {
			showDialog("#authenticationRequestDialog");
			return;
		}
		var urlstop = "/ws-resources/netconfig/stopAllAWS";
		$http({method: 'POST', url: urlstop}).
			success(function(data, status, headers, config) {
				console.log("Successfully requested to stop all AWS resources");
			}).
			error(function(data, status, headers, config) {
				console.log("Error while trying to stop AWS resources");
			});
	}
	
	$scope.deployClient = function() {
		if (!user.current.authenticated) {
			showDialog("#authenticationRequestDialog");
			return;
		}
		var urldeploy = "/ws-resources/netconfig/deployClients";
		$http({method: 'POST', url: urldeploy}).
			success(function(data, status, headers, config) {
				console.log("Successfully requested to stop all AWS resources");
			}).
			error(function(data, status, headers, config) {
				console.log("Error while trying to stop AWS resources");
			});
	}
			
	 // $scope $destroy
    $scope.$on('$destroy', function() {
		// cancel net config
		netConfigChangedListener();
		
		// cancel polling
		analysisMgtService.requestStopPolling();
	});
	
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
			Ok: function() {
				$(this).dialog("close");
				if(onok) {
					onok();
				}
			}
			}
		});
	}
	
	
}).directive('connectedResourcesAccordion', function() {
	
	return function(scope, element, attrs) {
		$(".accConnResources").accordion({  heightStyle: "content",autoHeight: false, header: "h3", collapsible: true });
	};
});

app.directive('mapElement',
 function () {
       return {
        restrict: 'E',
        replace:true,
        template: '<div id="mapdiv" style="width: 100%; height: 400px;"><div class="loadingChart shadowedText">Loading map...</div></div>',
        link: function (scope, element, attrs) {

            var map = false;

            var initMap = function() {
				if (map) map.destroy();
				
				var mapData = scope.mapData || [];
				
				AmCharts.theme = AmCharts.themes.black;

				// build map
				map = new AmCharts.AmMap();
				map.pathToImages = "vendor/ammap_3.13.3.free/ammap/images/";
				map.mouseWheelZoomEnabled = false;
				map.zoomControl.top = 70;
				map.zoomControl.buttonFillColor = "#6F9BBC";
				map.addTitle("Network resources repartition", 14);
				
				var subtitle = mapData.length + " connected";
				map.addTitle(subtitle, 11);
				
				map.areasSettings = {
					unlistedAreasColor: "#FFFFFF",
					unlistedAreasAlpha: 0.1
				};
				
				map.imagesSettings = {
					balloonText: "<span style='font-size:14px'>[[title]] [[value]]</span>",
					alpha: 0.6
				};
				

				var dataProvider = {
					mapVar: AmCharts.maps.worldLow,
					images: [],
					lines: []
				};
				
				map.dataProvider = dataProvider;
				map.write("mapdiv");
            };

			scope.$watch('mapData', function(){
				if(scope.mapData !== null) {
					if(!map) {
						initMap();
					} else {
						map.dataProvider.images.length = 0;
						map.dataProvider.lines.length = 0;
						var mapData = scope.mapData;
						var latlong = scope.latlong;
						// create circle for each distant resource
						for (var i = 0; i < mapData.length; i++) {
							var dataItem = mapData[i];
							var id = dataItem.code;

							map.dataProvider.images.push({
								type: "circle",
								width: dataItem.value * 10,
								height: dataItem.value * 10,
								longitude: latlong[id].longitude,
								latitude: latlong[id].latitude,
								title: dataItem.name,
								value: dataItem.ID,
								color: dataItem.color
							});
							
							map.dataProvider.lines.push({
								latitudes: [ latlong["Server"].latitude, latlong[id].latitude ],
								longitudes: [ latlong["Server"].longitude, latlong[id].longitude ]
							});
						}
						
						map.linesSettings = {
							color: "#eea638",
							alpha: 0.5
						};
						map.linesAboveImages = false;
				
						var subtitle = mapData.length + " connected";
						map.titles.length = 1;
						map.addTitle(subtitle, 11);
						
						// set same zoom levels to retain map position/zoom
						map.dataProvider.zoomLevel = map.zoomLevel();
						map.dataProvider.zoomLatitude = map.zoomLatitude();
						map.dataProvider.zoomLongitude = map.zoomLongitude();
	
						map.validateData();
						map.zoomTo(2);
						
					}
				} else {
					if (map) map.destroy();
				}
				
            });
                
        }//end link

    }
}) ;

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
		        duration: 500
		    },
		    hide: {
		        effect: 'fade',
		        duration: 500
		    },
			buttons: {
				Ok: function() {
					$( this ).dialog( "close" );
					
					// Ask server to add selected pattern
					var request = $http({method: 'POST', url: '/ws-resources/process/addPattern', data: {"value": $scope.newPatternValue,"alias": $scope.newPatternLabel}});
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

appControllers.controller('reportDialogCtrl', function ($scope,$http,reportSrvc) {
	
	$scope.title = "Report for ";
	$scope.graphData = [];
	$scope.chartConfig = {
        type: "serial",
        dataProvider: $scope.graphData,
		pathToImages: "http://www.amcharts.com/lib/3/images/",
        categoryField: "name",
        categoryAxis: {
                    gridAlpha: 0.15,
                    minorGridEnabled: true,
					labelRotation: 90,
                    axisColor: "#DADADA"
        },
        valueAxes: [{
                    axisAlpha: 0.2,
                    id: "v1",
        }],
		responsive: {
    enabled: true
  },
        graphs: [{
                    title: "red line",
                    id: "g1",
                    valueAxis: "v1",
                    valueField: "value",
					fillAlphas: 0.4,
                    bullet: "round",
					hideBulletsCount: 100,
                    bulletColor: "#FFFFFF",
                    bulletBorderAlpha: 1,
                    lineThickness: 2,
                    lineColor: "#b5030d",
					useLineColorForBulletBorder: true,
                    negativeLineColor: "#0352b5",
                    balloonText: "[[name]]<br><b><span style='font-size:14px;'>value: [[value]]</span></b>"
        }],
        chartCursor: {
                    fullWidth:true,
					cursorPosition: "mouse",
                    cursorAlpha:0.1
        },
        chartScrollbar: {
                    scrollbarHeight: 40,
                    color: "#FFFFFF",
                    autoGridCount: true,
                    graph: "g1"
        },

        mouseWheelZoomEnabled:true
    }
	
	if($scope.$parent.analyseDisplayedResult !== null) {
		$scope.title += " analysis started on - " + $scope.$parent.analyseDisplayedResult.launchDateFormat;
		reportSrvc.getReport($scope.$parent.analyseDisplayedResult.id).then(function(resultDataObj) {
			$scope.chartConfig.dataProvider = resultDataObj.listData;
			//$scope.chartConfig.dataProvider.length = 1000;
			$scope.graphData = resultDataObj.listData;
			//$scope.graphData.length = 1000;
		});
	} else {
		$scope.chartConfig.dataProvider = null;
		$scope.graphData = null;
	}
	
})

app.directive('chartElement',
 function () {
       return {
        restrict: 'E',
        replace:true,
        template: '<div id="chartdiv"><div class="loadingChart shadowedText">Loading chart...</div></div>',
        link: function (scope, element, attrs) {

			// Prevent css incompatibilities between map and graphs libraries
			$("link[href*='ammap.css']").prop('disabled', true);
			
			AmCharts.theme = AmCharts.themes.light;
	
            var chart = false;

            var initChart = function() {
                  if (chart) chart.destroy();
                  var config = scope.chartConfig || {};
				  
                  chart = AmCharts.makeChart("chartdiv",config);
                 /* if(config.loading) {
                    chart.showLoading();
                  }*/

						// changes cursor mode from pan to select
					function setPanSelect() {
							var chartCursor = chart.chartCursor;

							if (document.getElementById("rb1").checked) {
								chartCursor.pan = false;
								chartCursor.zoomable = true;

							} else {
								chartCursor.pan = true;
							}
							chart.validateNow();
					}
            };

			scope.$watch('graphData', function(){
				if(scope.graphData !== null && scope.graphData.length > 0) {
					initChart();
					chart.zoomToIndexes(0, 100);
				} else {
					if (chart) chart.destroy();
				}
            });
                
        }//end link

    }
}) ;
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
			show: {
		        effect: 'fade',
		        duration: 500
		    },
		    hide: {
		        effect: 'fade',
		        duration: 500
		    },
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
			show: {
		        effect: 'fade',
		        duration: 500
		    },
		    hide: {
		        effect: 'fade',
		        duration: 500
		    },
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

app.directive('runningPageLoadDirective', function() {
	return function(scope, element, attrs) {
		$("#deleteAllConfirmationDialog").hide();
		$("#additionnalAnalysisSentDialog").hide();
		$("#deletAdditionalConfirm").hide();
		$("#additionalAnalysisDialog").hide();
	};
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

appControllers.controller('runningInfosCtrl', function ($scope,$rootScope, analysisMgtService, uploadService) {
	
	$scope.init = function(analyseid)
    {
		$scope.analyseID = analyseid; 
		$scope.uploadProgress = -1;
		$scope.uploadingFileName = '';
			
		if(analyseid == uploadService.getCurrentAnalyseID()) {
			$scope.uploadProgress = uploadService.getCurrentUploadingProgress();
			$scope.uploadingFileName = uploadService.getCurrentUploadingFileName();
		}
	};
	
	// init with current runs
	$scope.runningAnalyses = analysisMgtService.getRuns();
	// Ajx call - update of running analysis info values
	$scope.updateinfos = function() {
		if($scope.runningAnalyses !== 'undefined') {
			$('.progressbar').each(function(index) {
				if($scope.runningAnalyses.length > index) {
					var progress = $scope.runningAnalyses[index].progress;
					$( this ).progressbar('value', progress);
				}
			});
		}
	};
	
	// listen for the event in the relevant $scope
	var runListener = $rootScope.$on('updateRuns', function (event, data) {
		$scope.runningAnalyses = data;
		$scope.updateinfos();
	});
	
		// listen for the event in the relevant $scope
	var uploadProgressListener = $rootScope.$on('uploadProgressChanged', function (event, data) {
		if(data.analyseId === $scope.analyseID) {
			$scope.uploadProgress = data.progressval;
			$scope.analyseId = data.analyseId;
		}
	});
	
	// listen for the event in the relevant $scope
	var uploadFileChangedListener = $rootScope.$on('uploadingFileChanged', function (event, data) {
		if(data.analyseId === $scope.analyseID) {
			$scope.uploadingFileName = data.filename;
		}
	});
	
	
	 // $scope $destroy
    $scope.$on('$destroy', function() {
		// cancel run listener
		runListener();
		// cancel uploadProgressListener
		uploadProgressListener();
		// cancel uploadFileChangedListener
		uploadFileChangedListener();
	});
});

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
app.directive('fileManager', function($rootScope) {
	return {
		scope: {
			fileType : "@",
			title : "@",
			uid : "@",
			baseSpaceAllowed: "@bsallowed",
			selectedFiles: "="
		},
		templateUrl : 'html/templates/file-manager-template.html',
		controllerAs: 'ctrl',
	    controller: 'fileManagerController',
	    bindToController: true
	};
});
app.directive('jqueryInit', function($timeout) {
	return {
		// Restrict it to be an attribute in this case
		restrict : 'A',
		link : function(element, attrs) {
			$timeout(function() {
				$('.acc').accordion({
					heightStyle : "content",
					clearStyle : true,
					header : "h3",
					collapsible : true
				});

			});
		}
	};
});
app.directive('nextStepPanel', function($rootScope) {
	return {
		scope : {
			MAX_STEP : "@steps"
		},
		link : function(scope, element, attrs) {

			scope.currentCreateStep = 1;

			scope.nextClicked = function() {
				scope.$emit('nextStepClicked', scope);
			};
		},
		templateUrl : 'html/templates/next-step-panel-template.html'
	};
});
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
		},
		
		getAllSplitPatterns: function() {
			var promiseOnSplitPatterns = $http({method: 'GET', url: '/ws-resources/datastorage/splitPatterns'}).
			then(function (response) {
			// The return value gets picked up by the then in the controller.
				return response.data;
			}, function(error) {
				return null;
			});
		   return promiseOnSplitPatterns;
		}
	};
	
}]);
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
services.factory('uploadService', ['$http','FileUploader', 'analysisMgtService', '$rootScope', function($http, FileUploader , analysisMgtService, $rootScope) {
	
	var uploadDataFiles = [];
	var uploadLibraryFiles = [];
	
	var dataUploader = new FileUploader();
	var libUploader = new FileUploader();
	
	var currentUploadingFileName = "";
	var currentUploadingProgress = -1;
	var currentAnalyseID = '';
	
	function getReadableFileSizeString(fileSizeInBytes) {
		var i = -1;
		var byteUnits = [' kB', ' MB', ' GB', ' TB', 'PB', 'EB', 'ZB', 'YB'];
		do {
			fileSizeInBytes = fileSizeInBytes / 1024;
			i++;
		} while (fileSizeInBytes > 1024);

		return Math.max(fileSizeInBytes, 0.1).toFixed(2) + byteUnits[i];
	};
	
	var addToUploadList = function(fileItem, uploadList, addToViewFileListFunc) {
		if (fileItem.file) {
			// Add the view file to the controller
			var readSize = getReadableFileSizeString(fileItem.file.size);
			var viewFile = {origin:"UPLOAD", name: fileItem.file.name, dateCreated: fileItem.file.lastModifiedDate, id: fileItem.file.name, size : fileItem.file.size, readableSize : readSize}; 
			var ok = addToViewFileListFunc(viewFile);
			if(ok) {
				uploadList.push({assViewfile : viewFile, fileItem: fileItem});
				return true;
			}
		}
		return false;
	}
	
	var removeFomUploadList = function(viewFile, uploadList, uploader) {
		if(viewFile) {
			// Remove the view file from the upload list
			var arrayLength = uploadList.length;
			for (var i = 0; i < arrayLength; i++) {
				var currentFile = uploadList[i].assViewfile;
				if(currentFile === viewFile) {
					uploader. removeFromQueue(uploadList[i].fileItem);
					uploadList.splice(i, 1);
					return true;
				}
			}
		}
		// the given view file was not found in the array
		return false;
	}
	
	/*
	* Usage example:
	* upload(uploadDataFiles,'/ws-resources/datastorage/uploadDataFile')"
	*/
	var startUploadSelectedFiles = function(analyseid, uploadurl, uploader, startNextUploader) {
		
		currentAnalyseID = analyseid;
		
		analysisMgtService.requestAnalysisStatus(analyseid, 'RETRIEVE_FILES');
		
		// Associate analyse ID to this Request
		var customformData = [{analyseID: analyseid}];
		uploader.onBeforeUploadItem = function(item) {
			Array.prototype.push.apply(item.formData, customformData);
			item.url = uploadurl + '?analyseid=' + analyseid;
			currentUploadingFileName = item.file.name;
			notifyUploadFileChangedListeners(analyseid, currentUploadingFileName);
		};

		uploader.onProgressAll = function(progress) {
			if(currentUploadingProgress !== progress) {
				currentUploadingProgress = progress;
				notifyProgressListeners(analyseid, progress );
			}
		};
		uploader.onCompleteAll = function() {
			uploader.clearQueue();
			// reinit globals values
			currentUploadingFileName = "";
			currentUploadingProgress = -1;
			currentAnalyseID = '';
			
			if(startNextUploader && libUploader.queue.length > 0) {
				startUploadSelectedFiles(analyseid,'/ws-resources/datastorage/uploadLibFile', libUploader, false);
			} else {
				// Notify all downloads are completed
				notifyProgressListeners(analyseid, -1 );
			}
		};
		uploader.onErrorItem = function(fileItem, response, status, headers) {
			analysisMgtService.requestAnalysisStatus(analyseid, 'UPLOAD_ERROR');
			libUploader.cancelAll();
			dataUploader.cancelAll();
			libUploader.clearQueue();
			dataUploader.clearQueue();
			
		};
		
		uploader.uploadAll ();
	}
	
	function notifyProgressListeners(analyseId, value ) {
		var progress = {analyseId : analyseId, progressval: value};
		$rootScope.$emit('uploadProgressChanged', progress);
	}
	
	function notifyUploadFileChangedListeners(analyseId, filename ) {
		var fileInfo = {analyseId : analyseId, filename: filename};
		$rootScope.$emit('uploadingFileChanged', fileInfo);
	}
	
	return {
	  
    addToUploadData: function(file, addToViewFileListFunc) {
		return addToUploadList(file,uploadDataFiles, addToViewFileListFunc);
    },
	
	addToUploadLibs: function(file, addToViewFileListFunc) {
		return addToUploadList(file,uploadLibraryFiles, addToViewFileListFunc);
    },
	
	removeFromUploadData: function(viewFile) {
		return removeFomUploadList(viewFile,uploadDataFiles, dataUploader);
    },
	
	removeFromUploadLibs: function(viewFile) {
		return removeFomUploadList(viewFile,uploadLibraryFiles, libUploader);
    },
	
	getDataUploader: function(viewFile) {
		return dataUploader;
    },
	
	getLibsUploader: function(viewFile) {
		return libUploader;
    },
	
	getCurrentUploadingFileName: function() {
		return currentUploadingFileName;
    },
	
	getCurrentUploadingProgress: function() {
		return currentUploadingProgress;
    },
	
	getCurrentAnalyseID: function() {
		return currentAnalyseID;
    },
	
	clearLists: function() {
		uploadDataFiles = [];
		uploadLibraryFiles = [];
		libUploader.clearQueue();
		dataUploader.clearQueue();
    },
	
	uploadNeeded: function() {
		return uploadDataFiles.length > 0 || uploadLibraryFiles.length > 0;
    },
	
	showList: function() {
		alert("upload data length " + dataUploader.queue.length + "  updatafiles " + uploadDataFiles);
		alert("upload data length " + libUploader.queue.length + "  updatafiles " + uploadLibraryFiles);
	},
	
	startUpload: function(analyseid) {
		
		// Trick to start only one uploader at the same time... could be improved a lot
		if(dataUploader.queue.length > 0) {
			startUploadSelectedFiles(analyseid,'/ws-resources/datastorage/uploadDataFile', dataUploader, true);
		} else {
			startUploadSelectedFiles(analyseid,'/ws-resources/datastorage/uploadLibFile', libUploader, false);
		}
	}
  };
 
}]);
app.controller('fileManagerController', function(uploadService, $rootScope,
		$scope, $http) {

	this.title = 'Generic file manager';
	this.localFileInputSelector = "file-input-" + this.uid;

	// The following variable is binded via an attribute within the directive
	// declaration
	// this.selectedFiles = [];

	var storedFilesURL;
	if (this.fileType === 'library') {
		this.fileUploader = uploadService.getLibsUploader();
		this.addToUploadListFunction = uploadService.addToUploadLibs;
		this.removeFromUploadListFunction = uploadService.removeFromUploadLibs;
		storedFilesURL = '/ws-resources/datastorage/libFiles';
	} else {
		this.fileUploader = uploadService.getDataUploader();
		this.addToUploadListFunction = uploadService.addToUploadData;
		this.removeFromUploadListFunction = uploadService.removeFromUploadData;
		storedFilesURL = '/ws-resources/datastorage/dataFiles';
	}

	// Init stored file list TODO BLOCKING METHOD ---> ADD THIS INO A SEPARATE
	// STORAGE SERVICE
	$scope.storedFilesList = [];
	this.fetchStoredFilesList = $http({
		method : 'GET',
		url : storedFilesURL
	}).success(function(data, status, headers, config) {
		$scope.storedFilesList = data;
	}).error(function(data, status, headers, config) {
		$scope.storedFilesList = [];
	});
	
	this.openBaseSpaceSelectionDial = function() {
		// Open dialog
		$("#basespacedialog").data('fileManager', this).dialog(
				{
					modal : true,
					width : 700,
					show : {
						effect : 'fade',
						duration : 1000
					},
					hide : {
						effect : 'fade',
						duration : 500
					},
					close : $(".accrun").accordion({
						heightStyle : "content",
						autoHeight : false,
						clearStyle : true,
						active : false,
						header : "h3",
						collapsible : true
					}),
					buttons : {
						Ok : function() {
							// emit event to ask bs-model-ctrl to add selected
							// file to
							// current model
							var fileManagerCtrl = $(this).data('fileManager');
							$rootScope.$emit('requestBaseSpaceDialogClose',
									fileManagerCtrl);
							$(this).dialog("close");
						}
					}
				});
		// clear selection and close accordions
		$('.accrun .ui-state-focus').removeClass('ui-state-focus');
	};

	this.addViewFile = angular.bind(this, function(file) {
		if (file !== null) {
			var ok = this.checkDuplicates(this.selectedFiles, file);
			if (ok) {
				this.selectedFiles.push(file);
				return true;
			} else {
				var dialogSelector = "#dialog-duplicate-warning-" + this.uid;
				this.showDialog(dialogSelector);
				return false;
			}
		}
	});

	this.addViewFileAndApply = function(file) {
		this.addViewFile(file);
		$scope.$apply();
	}

	this.fileUploader.onAfterAddingFile = angular.bind(this,
			function(fileItem) {
				var success = this.addToUploadListFunction(fileItem,
						this.addViewFile);
				if (!success) {
					this.fileUploader.removeFromQueue(fileItem);
				}
			});

	this.checkDuplicates = function(filelist, file) {
		var arrayLength = filelist.length;
		for (var i = 0; i < arrayLength; i++) {
			if (filelist[i].id === file.id) {
				return false;
			}
		}
		return true;
	};

	this.selectFile = function() {
		var elem = document.getElementById(this.localFileInputSelector);
		if (elem && document.createEvent) {
			var evt = document.createEvent("MouseEvents");
			evt.initEvent("click", true, false);
			elem.dispatchEvent(evt);
		}
	};

	this.removeSelectedFile = function(viewfile) {
		var index = this.getFileIndex(this.selectedFiles, viewfile);
		if (index != -1) {
			this.selectedFiles.splice(index, 1);
			if (viewfile.origin === 'UPLOAD') {
				var success = uploadService
						.removeFromUploadListFunction(viewfile);
				if (!success) {
					alert('impossible to remove file from upload list');
				}
			}
		}
	}

	this.isEmpty = function(list) {
		if (list !== undefined) {
			return list.length == 0;
		}
		return true;
	};

	this.getFileIndex = function(filelist, file) {
		var arrayLength = filelist.length;
		for (var i = 0; i < arrayLength; i++) {
			if (filelist[i].id === file.id) {
				return i;
			}
		}
		return -1;
	}

	this.showDialog = function(dialogid, onok) {
		$(dialogid).dialog({
			modal : true,
			width : 700,
			show : {
				effect : 'fade',
				duration : 1000
			},
			hide : {
				effect : 'fade',
				duration : 1000
			},
			buttons : {
				Ok : function() {
					$(this).dialog("close");
					if (onok) {
						onok();
					}
				}
			}
		});
	};

});