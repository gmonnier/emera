appControllers.controller('networkCtrl', function ($scope,$http, $rootScope,  analysisMgtService, user) {
	
	// init with current network config
	$scope.nodeConfig = analysisMgtService.getNetworkConfig() === null? null : analysisMgtService.getNetworkConfig().nodeConfig;
	$scope.feServer = analysisMgtService.getNetworkConfig() === null? null : analysisMgtService.getNetworkConfig().frontEndServer;
	
	analysisMgtService.requestStartPolling();

	$scope.latlong = {};
	$scope.mapData = [];
	
	$scope.usr = user;
	
	// listen for the event in the relevant $scope
	var netConfigChangedListener = $rootScope.$on('netConfigChanged', function (event, data) {
		if(data !== null) {

			$scope.netresources = data.nodeConfig.resources;
			$scope.awsInstances = data.nodeConfig.awsInstances;
			$scope.nodeServer = data.nodeConfig.nodeServer;
			$scope.feServer = data.frontEndServer;
			var newMapData = [];
			
			// add the server
			
			if(typeof data.frontEndServer !== 'undefined' && typeof data.frontEndServer.location !== 'undefined' && data.frontEndServer.location !== null && data.frontEndServer.location.latitude != 0) {
				var serverLoc = data.frontEndServer.location;
				$scope.latlong["Server"] = {"latitude": serverLoc.latitude , "longitude": serverLoc.longitude};
				newMapData[0] = {"code": "Server","name": serverLoc.cityName || serverLoc.countryName,"value": 1.5,"color": "#56ED38", "ID": data.frontEndServer.IP};
			}
			
			for (i = 0; typeof $scope.netresources !== 'undefined' && i < $scope.netresources.length ; i++) {
				var resource = $scope.netresources[i];
					if(typeof resource.location !== 'undefined' && resource.location !== null && resource.location.latitude != 0) {
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

app.directive('mapWindow',
	 function () {
	       return function (scope, element, attrs) {
	        	$('.show-earth').click(function() {
	        		$('#viewer-3d-section').fadeIn(1000);
	        		$('#viewer-3d-container').bind('click', function(event){
	        			event.stopPropagation();
	        		});
	        		
	        		$('#viewer-3d-section').bind('click', function(){
	        			$(this).hide();
	        			$(this).unbind('click');
	        		})
	        	});
	    }
	}) ;

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
