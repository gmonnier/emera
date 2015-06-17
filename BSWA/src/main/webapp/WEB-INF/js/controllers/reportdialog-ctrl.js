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
        template: '<div id="chartdiv" style="position: fixed; width: 100%; height: 100%;"><div class="loadingChart shadowedText">Loading chart...</div></div>',
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