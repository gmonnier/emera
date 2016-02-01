var appControllers=angular.module("appControllers",[]),directives=angular.module("appDirectives",[]),services=angular.module("appServices",[]),app=angular.module("app",["ngRoute","routeStyles","appControllers","appServices","ngAnimate","angularFileUpload","UserApp"]);app.run(["user",function(e){e.init({appId:"554d0317250f5"})}]),app.config(["$routeProvider",function(e){e.when("/analysis-selection",{templateUrl:"html/analysisSelection.html",controller:"analysisSelectionCtrl",css:"css/analysis-selection.css","public":!0}).when("/create-split-analysis",{templateUrl:"html/createSplitPan.html",controller:"createSplitCtrl",css:["css/createSplitPan.css","css/baseSpaceDial.css","css/patternCreateDial.css"],"public":!0}).when("/create-lookup-analysis",{templateUrl:"html/createLookupPan.html",controller:"createLookupCtrl",css:["css/createLookupPan.css","css/baseSpaceDial.css"],"public":!0}).when("/running",{templateUrl:"html/runningPan.html",controller:"runningCtrl",css:"css/running-pan.css","public":!0}).when("/config",{templateUrl:"html/networkConfigPan.html",controller:"networkCtrl",css:["css/configPan.css","vendor/ammap_3.13.3.free/ammap/ammap.css"],"public":!0}).when("/",{templateUrl:"html/homePan.html",css:["css/home.css","vendor/slider/csss_engine1/style.css"],"public":!0}).otherwise({templateUrl:"html/homePan.html",css:["css/home.css","vendor/slider/csss_engine1/style.css"]})}]),function(){"use strict";angular.module("routeStyles",["ngRoute"]).directive("head",["$rootScope","$compile","$interpolate",function(e,t,n){var o=n.startSymbol(),a=n.endSymbol(),r=['<link rel="stylesheet" ng-repeat="(routeCtrl, cssUrl) in routeStyles" ng-href="',o,"cssUrl",a,'">'].join("");return{restrict:"E",link:function(n,o){o.append(t(r)(n)),n.routeStyles={},e.$on("$routeChangeStart",function(e,t){t&&t.$$route&&t.$$route.css&&(angular.isArray(t.$$route.css)||(t.$$route.css=[t.$$route.css]),angular.forEach(t.$$route.css,function(e){n.routeStyles[e]=e}))}),e.$on("$routeChangeSuccess",function(e,t,o){o&&o.$$route&&o.$$route.css&&(angular.isArray(o.$$route.css)||(o.$$route.css=[o.$$route.css]),angular.forEach(o.$$route.css,function(e){n.routeStyles[e]=void 0}))})}}}])}(),appControllers.controller("bsModelCtrl",["$scope","$rootScope","basespaceService",function(e,t,n){e.selectedBaseSpaceFile=null,e.showAllRuns=!1,e.maxDisplayedRuns=5,e.userruns=[],e.setSelectedBSResource=function(t){e.selectedBaseSpaceFile=t},e.toggleShowRuns=function(){e.showAllRuns=!e.showAllRuns},e.getDisplayedRuns=function(){return 1==e.showAllRuns?e.userruns:e.userruns.slice(0,e.maxDisplayedRuns)},n.listruns().then(function(t){e.userruns=t}),n.userinfo().then(function(t){e.user=t}),n.connectionstatus().then(function(t){e.connectionstatus=t});var o=t.$on("requestBaseSpaceDialogClose",function(t,n){null!==e.selectedBaseSpaceFile&&n.addViewFileAndApply(e.selectedBaseSpaceFile)});e.$on("$destroy",o)}]),app.directive("postRepeatDirectiveBsDial",function(){return function(e,t,n){e.$last&&$(".accrun").accordion({heightStyle:"content",autoHeight:!1,clearStyle:!0,active:!1,header:"h3",collapsible:!0})}}),app.directive("refreshAcc",function(){return function(e,t,n){$(".accrun").accordion("refresh")}}),appControllers.controller("createLookupCtrl",["$scope","$rootScope","$http","$location","uploadService","user",function(e,t,n,o,a,r){a.clearLists(),n({method:"GET",url:"/ws-resources/process/confinit"}).success(function(t,n,o,a){e.configuration=t,e.configuration.pattern=null}).error(function(t,n,o,a){e.configuration=null}),e.isEmpty=function(e){return void 0!==e?0==e.length:!0},e.$on("nextStepClicked",function(t,n){n.currentCreateStep+1>n.MAX_STEP?e.validatePatternSelection()?e.enqueueProcessing():e.showDialog("#dialog-unvalid-pattern"):e.validateFileSelection()?(n.currentCreateStep+=1,e.stepValue=n.currentCreateStep):e.showDialog("#dialog-unvalid-parameters")}),e.validateFileSelection=function(){return e.configuration.selectedDataFiles.length>0&&e.configuration.selectedLibraries.length>0},e.validatePatternSelection=function(){return null!==e.configuration.pattern},e.enqueueProcessing=function(){var t="guest";r.current.hasOwnProperty("user_id")&&(t=r.current.user_id);var o="/ws-resources/process/enqueue?user_id="+t,i=n({method:"POST",url:o,data:e.configuration});i.success(function(t,n,o,r){var i=a.uploadNeeded();e.uploadmessage=i?"Upload process will start now. Please do not leave the page while transfering files to server.":"",i&&t?a.startUpload(t):t||alert("an error occured when uploading files, unable to retrieve analyse id"),e.showDialog("#dialog-message-ok",function(){e.changeRoute("/running")})}).error(function(t,n,o,a){e.showDialog("#dialog-message-failed"),e.changeRoute("/home")})},e.showDialog=function(e,t){$(e).dialog({modal:!0,width:700,show:{effect:"fade",duration:500},hide:{effect:"fade",duration:500},buttons:{Ok:function(){$(this).dialog("close"),t&&t()}}})},e.changeRoute=function(t,n){e=e||angular.element(document).scope(),n||e.$$phase?window.location=t:(o.path(t),e.$apply())}}]),appControllers.controller("createSplitCtrl",["$scope","$http","$location","uploadService","user","PatternStorageSrvc",function(e,t,n,o,a,r){o.clearLists();var s=function(){r.getAllSplitPatterns().then(function(t){e.splitPatterns=t})};s(),t({method:"GET",url:"/ws-resources/datastorage/dataFiles"}).success(function(t,n,o,a){e.storeDataFilesList=t}).error(function(t,n,o,a){e.storeDataFilesList=[]}),t({method:"GET",url:"/ws-resources/preprocess/confinit"}).success(function(t,n,o,a){e.configuration=t}).error(function(t,n,o,a){e.configuration=null}),e.$on("nextStepClicked",function(t,n){n.currentCreateStep+1>n.MAX_STEP&&(e.validateParameters()?e.enqueueProcessing():e.showDialog("#dialog-unvalid-parameters"))}),e.toggleSelect=function(t){var n=!1;for(i=e.configuration.dataSplitterModels.length-1;i>=0;i--){var o=e.configuration.dataSplitterModels[i];if(o.regexp===t.regexp){e.configuration.dataSplitterModels.splice(i,1),n=!0;break}}n||e.configuration.dataSplitterModels.push(t)},e.enqueueProcessing=function(){var n="guest";a.current.hasOwnProperty("user_id")&&(n=a.current.user_id);var r="/ws-resources/preprocess/enqueue?user_id="+n,i=t({method:"POST",url:r,data:e.configuration});i.success(function(t,n,a,r){var i=o.uploadNeeded();e.uploadmessage=i?"Upload process will start now. Please do not leave the page while transfering files to server.":"",i&&t?o.startUpload(t):t||alert("an error occured when uploading files, unable to retrieve analyse id"),e.showDialog("#dialog-message-ok",function(){e.changeRoute("/running")})}).error(function(t,n,o,a){e.showDialog("#dialog-message-failed"),e.changeRoute("/home")})},e.validateParameters=function(){return!(e.isEmpty(configuration.dataSplitterModels)||e.isEmpty(configuration.selectedDataFiles))},e.showDialog=function(e,t){$(e).dialog({modal:!0,width:700,show:{effect:"fade",duration:500},hide:{effect:"fade",duration:500},buttons:{Ok:function(){$(this).dialog("close"),t&&t()}}})},e.changeRoute=function(t,o){e=e||angular.element(document).scope(),o||e.$$phase?window.location=t:(n.path(t),e.$apply())},e.isEmpty=function(e){return void 0!==e?0==e.length:!0},e.isSelected=function(t){if(e.configuration&&!e.isEmpty(e.configuration.dataSplitterModels))for(var n=0,o=e.configuration.dataSplitterModels.length;o>n;n++)if(e.configuration.dataSplitterModels[n].regexp===t.regexp)return!0;return!1}}]).directive("applyEffects",function(){return function(e,t,n){e.$last&&$(".selectPatItem").click(function(){$(".patternPreview").clearQueue(),$(".patternPreview").stop(),$(".patternPreview").hide(),$(".patternPreview").fadeIn("slow",function(){})})}}),appControllers.controller("navController",["$scope",function(e){e.menuClicked=function(e){$("#subSelectBar").removeClass(),1===e?$("#subSelectBar").addClass("select1"):2===e?$("#subSelectBar").addClass("select2"):3===e?$("#subSelectBar").addClass("select3"):4===e&&$("#subSelectBar").addClass("select4")}}]),app.directive("navDirective",function(){return function(e,t,n){$(document).ready(function(){var e=($(".nav").offset().top,function(){var e=$(window).scrollTop();$("#topToolBarLogo").height();e>70?($(".nav").css("top","0px"),$("#topToolBar").css("margin-bottom","100px")):($(".nav").offset({top:80}),$("#topToolBar").css("margin-bottom","125px"))});e(),$(window).scroll(function(){e()}),$(".toolbaraction").click(function(){$("#ngViewFade").clearQueue(),$("#ngViewFade").stop(),$("#ngViewFade").hide(),$("#ngViewFade").fadeIn("slow",function(){})}),$("#loginDialog").hide(),$("#authenticationRequestDialog").hide()})}}),appControllers.controller("networkCtrl",["$scope","$http","$rootScope","analysisMgtService","user",function(e,t,n,o,a){e.netresources=null===o.getNetworkConfig()?null:o.getNetworkConfig().resources,e.awsInstances=null===o.getNetworkConfig()?null:o.getNetworkConfig().awsInstances,e.serverresource=null===o.getNetworkConfig()?null:o.getNetworkConfig().thisServer,o.requestStartPolling(),e.latlong={},e.mapData=[],e.usr=a;var r=n.$on("netConfigChanged",function(t,n){if(null!==n){e.netresources=n.resources,e.awsInstances=n.awsInstances,e.serverresource=n.thisServer;var o=[];for("undefined"!==n.thisServer&&"undefined"!==n.thisServer.location&&null!==n.thisServer.location&&0!=n.thisServer.location.latitude&&(e.latlong.Server={latitude:n.thisServer.location.latitude,longitude:n.thisServer.location.longitude},o[0]={code:"Server",name:n.thisServer.location.cityName,value:1.5,color:"#56ED38",ID:n.thisServer.IP}),i=0;i<e.netresources.length;i++){var a=e.netresources[i];"undefined"!==a.location&&null!==a.location&&0!=a.location.latitude&&(e.latlong[""+(i+1)]={latitude:a.location.latitude,longitude:a.location.longitude},o[i+1]={code:""+(i+1),name:a.location.cityName,value:1,color:"#eea638",ID:a.IP})}o.length!==e.mapData.length&&(e.mapData=o),o=null}});e.removeDistantResource=function(e){if(!a.current.authenticated)return void s("#authenticationRequestDialog");var n="/ws-resources/netconfig/"+e;t({method:"DELETE",url:n}).success(function(t,n,o,a){console.log("client with ID "+e+" successfully removed from the distant resources list")}).error(function(t,n,o,a){console.log("client with ID "+e+" was not able to be removed.")})},e.startAllAWS=function(){if(!a.current.authenticated)return void s("#authenticationRequestDialog");var e="/ws-resources/netconfig/startAllAWS";t({method:"POST",url:e}).success(function(e,t,n,o){console.log("Successfully requested to start all AWS resources")}).error(function(e,t,n,o){console.log("Error while trying to start AWS resources")})},e.stopAllAWS=function(){if(!a.current.authenticated)return void s("#authenticationRequestDialog");var e="/ws-resources/netconfig/stopAllAWS";t({method:"POST",url:e}).success(function(e,t,n,o){console.log("Successfully requested to stop all AWS resources")}).error(function(e,t,n,o){console.log("Error while trying to stop AWS resources")})},e.deployClient=function(){if(!a.current.authenticated)return void s("#authenticationRequestDialog");var e="/ws-resources/netconfig/deployClients";t({method:"POST",url:e}).success(function(e,t,n,o){console.log("Successfully requested to stop all AWS resources")}).error(function(e,t,n,o){console.log("Error while trying to stop AWS resources")})},e.$on("$destroy",function(){r(),o.requestStopPolling()});var s=function(e,t){$(e).dialog({modal:!0,resizable:!1,width:400,show:{effect:"fade",duration:500},hide:{effect:"fade",duration:500},buttons:{Ok:function(){$(this).dialog("close"),t&&t()}}})}}]).directive("connectedResourcesAccordion",function(){return function(e,t,n){$(".accConnResources").accordion({heightStyle:"content",autoHeight:!1,header:"h3",collapsible:!0})}}),app.directive("mapWindow",function(){return function(e,t,n){$(".feather-show").click(function(){$("#viewer-3d-section").fadeIn(1e3)})}}),app.directive("mapElement",function(){return{restrict:"E",replace:!0,template:'<div id="mapdiv" style="width: 100%; height: 400px;"><div class="loadingChart shadowedText">Loading map...</div></div>',link:function(e,t,n){var o=!1,a=function(){o&&o.destroy();var t=e.mapData||[];AmCharts.theme=AmCharts.themes.black,o=new AmCharts.AmMap,o.pathToImages="vendor/ammap_3.13.3.free/ammap/images/",o.mouseWheelZoomEnabled=!1,o.zoomControl.top=70,o.zoomControl.buttonFillColor="#6F9BBC",o.addTitle("Network resources repartition",14);var n=t.length+" connected";o.addTitle(n,11),o.areasSettings={unlistedAreasColor:"#FFFFFF",unlistedAreasAlpha:.1},o.imagesSettings={balloonText:"<span style='font-size:14px'>[[title]] [[value]]</span>",alpha:.6};var a={mapVar:AmCharts.maps.worldLow,images:[],lines:[]};o.dataProvider=a,o.write("mapdiv")};e.$watch("mapData",function(){if(null!==e.mapData)if(o){o.dataProvider.images.length=0,o.dataProvider.lines.length=0;for(var t=e.mapData,n=e.latlong,r=0;r<t.length;r++){var i=t[r],s=i.code;o.dataProvider.images.push({type:"circle",width:10*i.value,height:10*i.value,longitude:n[s].longitude,latitude:n[s].latitude,title:i.name,value:i.ID,color:i.color}),o.dataProvider.lines.push({latitudes:[n.Server.latitude,n[s].latitude],longitudes:[n.Server.longitude,n[s].longitude]})}o.linesSettings={color:"#eea638",alpha:.5},o.linesAboveImages=!1;var l=t.length+" connected";o.titles.length=1,o.addTitle(l,11),o.dataProvider.zoomLevel=o.zoomLevel(),o.dataProvider.zoomLatitude=o.zoomLatitude(),o.dataProvider.zoomLongitude=o.zoomLongitude(),o.validateData(),o.zoomTo(2)}else a();else o&&o.destroy()})}}}),appControllers.controller("patternsCtrl",["$scope","$http","PatternStorageSrvc",function(e,t,n){e.selectedPattern=null,e.init=function(){e.newPatternValue="",e.newPatternLabel=""},e.init(),e.isCreateNewValid=function(){return e.newPatternValue.length>0};var o=function(){n.getAllPatterns().then(function(t){e.patterns=t})};o(),e.select=function(t){e.configuration.pattern=t,e.selectedPattern=t},e.openPatternCreationDial=function(n){e.init(),$("#createPatternDialog").dialog({modal:!0,width:700,show:{effect:"fade",duration:500},hide:{effect:"fade",duration:500},buttons:{Ok:function(){$(this).dialog("close");var n=t({method:"POST",url:"/ws-resources/process/addPattern",data:{value:e.newPatternValue,alias:e.newPatternLabel}});n.success(function(t,n,a,r){o(),t&&e.select(t)}).error(function(t,n,o,a){e.showDialog("#dialog-pattern-creation-failed")})}}})}}]).directive("applyEffects",function(){return function(e,t,n){e.$last&&$(".selectPatItem").click(function(){$(".patternPreview").clearQueue(),$(".patternPreview").stop(),$(".patternPreview").hide(),$(".patternPreview").fadeIn("slow",function(){})})}}),appControllers.controller("reportDialogCtrl",["$scope","$http","reportSrvc",function(e,t,n){e.title="Report for ",e.graphData=[],e.chartConfig={type:"serial",dataProvider:e.graphData,pathToImages:"http://www.amcharts.com/lib/3/images/",categoryField:"name",categoryAxis:{gridAlpha:.15,minorGridEnabled:!0,labelRotation:90,axisColor:"#DADADA"},valueAxes:[{axisAlpha:.2,id:"v1"}],responsive:{enabled:!0},graphs:[{title:"red line",id:"g1",valueAxis:"v1",valueField:"value",fillAlphas:.4,bullet:"round",hideBulletsCount:100,bulletColor:"#FFFFFF",bulletBorderAlpha:1,lineThickness:2,lineColor:"#b5030d",useLineColorForBulletBorder:!0,negativeLineColor:"#0352b5",balloonText:"[[name]]<br><b><span style='font-size:14px;'>value: [[value]]</span></b>"}],chartCursor:{fullWidth:!0,cursorPosition:"mouse",cursorAlpha:.1},chartScrollbar:{scrollbarHeight:40,color:"#FFFFFF",autoGridCount:!0,graph:"g1"},mouseWheelZoomEnabled:!0},null!==e.$parent.analyseDisplayedResult?(e.title+=" analysis started on - "+e.$parent.analyseDisplayedResult.launchDateFormat,n.getReport(e.$parent.analyseDisplayedResult.id).then(function(t){e.chartConfig.dataProvider=t.listData,e.graphData=t.listData})):(e.chartConfig.dataProvider=null,e.graphData=null)}]),app.directive("chartElement",function(){return{restrict:"E",replace:!0,template:'<div id="chartdiv"><div class="loadingChart shadowedText">Loading chart...</div></div>',link:function(e,t,n){$("link[href*='ammap.css']").prop("disabled",!0),AmCharts.theme=AmCharts.themes.light;var o=!1,a=function(){o&&o.destroy();var t=e.chartConfig||{};o=AmCharts.makeChart("chartdiv",t)};e.$watch("graphData",function(){null!==e.graphData&&e.graphData.length>0?(a(),o.zoomToIndexes(0,100)):o&&o.destroy()})}}}),appControllers.controller("runningCtrl",["$scope","$http","analysisMgtService","reportSrvc","user",function(e,t,n,o,a){e.runningAnalyses=n.getRuns(),e.completeAnalyses=n.getCompleted(),n.requestStartPolling(),e.analyseDisplayedResult=null,e.stopAll=function(){e.showConfirmDialog("#deleteAllConfirmationDialog",n.stopAllAnalyses)},e.sortByDate=function(){n.setSortType("date")},e.sortByName=function(){n.setSortType("name")},e.expandAll=function(){$("h3[aria-expanded='false']").click()},e.collapseAll=function(){$("h3[aria-expanded='true']").click()},e.outputFileType="PDF",e.additionalAnalysisType,e.additionalAnalysisCompareRequest=function(t,n){e.additionalAnalysisType="Comparison Analysis",e.showConfirmDialog("#additionalAnalysisDialog",function(){r(t,n)})};var r=function(n,o){var a={analysisID1:n,analysisID2:o,outputFileType:e.outputFileType};t({method:"POST",url:"/ws-resources/additional/analysisCompare",data:a}).success(function(t,n,o,a){$("#additionnalAnalysisMessage").css("color","black"),$("#additionnalAnalysisMessage").text("Additionnal analysis succesfully requested to the server"),e.showInfoDialog("#additionnalAnalysisSentDialog")}).error(function(t,n,o,a){$("#additionnalAnalysisMessage").css("color","red"),$("#additionnalAnalysisMessage").text("Unable to generate this additionnal analysis"),e.showInfoDialog("#additionnalAnalysisSentDialog")})};e.deleteAdditionalRequest=function(t,n){e.showConfirmDialog("#deletAdditionalConfirm",function(){i(t,n)})};var i=function(e,n){var o="/ws-resources/additional/"+e+"/"+n;t({method:"DELETE",url:o})};e.downloadAdditionalAnalysis=function(e,t){var n="/ws-resources/additional/"+e+"/"+t.name;window.location=n},e.openGraphData=function(t){e.analyseDisplayedResult=t},e.downloadCSV=function(e){var t="/ws-resources/analysis/report/"+e+"/csv",n="guest";a.current.hasOwnProperty("user_id")&&(n=a.current.user_id),t+="?user_ID="+n,window.location=t},e.downloadPDF=function(e){var t="/ws-resources/analysis/report/"+e+"/pdf",n="guest";a.current.hasOwnProperty("user_id")&&(n=a.current.user_id),t+="?user_ID="+n,window.location=t};var s=e.$on("runAnalysisCountChanged",function(t,n){e.runningAnalyses=n}),l=e.$on("completeAnalysisCountChanged",function(t,n){e.completeAnalyses=n});e.$on("$destroy",function(){s(),l(),n.requestStopPolling()}),e.showConfirmDialog=function(e,t){$(e).dialog({modal:!0,height:200,width:520,show:{effect:"fade",duration:500},hide:{effect:"fade",duration:500},buttons:{Cancel:function(){$(this).dialog("close")},Continue:function(){$(this).dialog("close"),t&&t()}}})},e.showInfoDialog=function(e,t){$(e).dialog({modal:!0,height:170,width:520,show:{effect:"fade",duration:500},hide:{effect:"fade",duration:500},buttons:{Ok:function(){$(this).dialog("close"),t&&t()}}})}}]),app.directive("runningPageLoadDirective",function(){return function(e,t,n){$("#deleteAllConfirmationDialog").hide(),$("#additionnalAnalysisSentDialog").hide(),$("#deletAdditionalConfirm").hide(),$("#additionalAnalysisDialog").hide()}}),app.directive("postRunningRepeatDirective",function(){return function(e,t,n){e.$last&&($(".accRunning").accordion({heightStyle:"content",autoHeight:!1,clearStyle:!0,header:"h3",collapsible:!0}),$(".progressbar").progressbar({}))}}),app.directive("postCompleteRepeatDirective",function(){return function(e,t,n){if(e.$last){$(".accComplete").accordion({active:"false",heightStyle:"content",autoHeight:!1,clearStyle:!0,header:"h3",collapsible:!0,beforeActivate:function(e,t){t.oldHeader.length>0&&$(this).find(".ui-accordion-content").toggleClass("overflow-visible")},activate:function(e,t){t.newHeader.length>0&&$(this).find(".ui-accordion-content").toggleClass("overflow-visible")}}),$(".sortMenu").menu({items:"> :not(.ui-widget-header)"}),$(".sortMenu").hide(),$("#sortToolItem").hover(function(e){$(".sortMenu").width(150),$(".sortMenu").show()},function(){$(".sortMenu").hide(),$(".sortMenu").width(0)});var o=$(".additionalAnalysisMenu").menu();$(o).mouseleave(function(){o.menu("collapseAll")})}}}),appControllers.controller("runningInfosCtrl",["$scope","$rootScope","analysisMgtService","uploadService",function(e,t,n,o){e.init=function(t){e.analyseID=t,e.uploadProgress=-1,e.uploadingFileName="",t==o.getCurrentAnalyseID()&&(e.uploadProgress=o.getCurrentUploadingProgress(),e.uploadingFileName=o.getCurrentUploadingFileName())},e.runningAnalyses=n.getRuns(),e.updateinfos=function(){"undefined"!==e.runningAnalyses&&$(".progressbar").each(function(t){if(e.runningAnalyses.length>t){var n=e.runningAnalyses[t].progress;$(this).progressbar("value",n)}})};var a=t.$on("updateRuns",function(t,n){e.runningAnalyses=n,e.updateinfos()}),r=t.$on("uploadProgressChanged",function(t,n){n.analyseId===e.analyseID&&(e.uploadProgress=n.progressval,e.analyseId=n.analyseId)}),i=t.$on("uploadingFileChanged",function(t,n){n.analyseId===e.analyseID&&(e.uploadingFileName=n.filename)});e.$on("$destroy",function(){a(),r(),i()})}]),appControllers.controller("topToolBarCtrl",["$scope","$rootScope","$http","basespaceService","user",function(e,t,n,o,a){a.onAuthenticationSuccess(function(){$("#loginDialog").dialog("close")});var r=function(){$("#loginForm").submit()};e.showLoginDial=function(){i("#loginDialog",r)};var i=function(e,t){$(e).dialog({modal:!0,resizable:!1,width:400,show:{effect:"fade",duration:500},hide:{effect:"fade",duration:500},buttons:{"Log in":function(){t&&t()}}})}}]),app.directive("fileManager",["$rootScope",function(e){return{scope:{fileType:"@",title:"@",uid:"@",baseSpaceAllowed:"@bsallowed",selectedFiles:"="},templateUrl:"html/templates/file-manager-template.html",controllerAs:"ctrl",controller:"fileManagerController",bindToController:!0}}]),app.directive("jqueryInit",["$timeout",function(e){return{restrict:"A",link:function(t,n){e(function(){$(".acc").accordion({heightStyle:"content",clearStyle:!0,header:"h3",collapsible:!0})})}}}]),app.directive("nextStepPanel",["$rootScope",function(e){return{scope:{MAX_STEP:"@steps"},link:function(e,t,n){e.currentCreateStep=1,e.nextClicked=function(){e.$emit("nextStepClicked",e)}},templateUrl:"html/templates/next-step-panel-template.html"}}]),app.directive("viewerMap",["$rootScope",function(e){return{link:function(e,t,n){var o,a,r,i,s,l,u,c,d=new THREE.Vector2,p=[],f={},g=800,h=600,m={buildScene:function(){var e=45,n=g/h,i=.1,s=1e4;o=new THREE.WebGLRenderer({antialias:!0}),a=new THREE.PerspectiveCamera(e,n,i,s),a.position.z=150,l=new THREE.OrbitControls(a),l.addEventListener("change",m.render),r=new THREE.Scene,r.add(a),r.add(new THREE.AmbientLight(3355443));var c=new THREE.DirectionalLight(16777215,1);c.position.set(5,3,5),r.add(c),m.buildEarth(),o.setSize(g,h),u=new THREE.Raycaster,t.append(o.domElement),$("canvas").mousemove(m.onMouseMove),window.addEventListener("resize",m.onWindowResize,!1)},buildEarth:function(){f.maintexture=null==f.maintexture?THREE.ImageUtils.loadTexture("img/network/map/test.jpg"):f.maintexture,f.bump=null==f.bump?THREE.ImageUtils.loadTexture("img/network/map/bump.jpg"):f.bump,f.water=null==f.water?THREE.ImageUtils.loadTexture("img/network/map/water.png"):f.water,f.clouds=null==f.clouds?THREE.ImageUtils.loadTexture("img/network/map/clouds.png"):f.clouds,null==i||null==s?(i=new THREE.Mesh(new THREE.SphereGeometry(50,32,32),new THREE.MeshPhongMaterial({map:f.maintexture,bumpMap:f.bump,bumpScale:.005,specularMap:f.water,specular:"#333333"})),s=new THREE.Mesh(new THREE.SphereGeometry(51,32,32),new THREE.MeshPhongMaterial({map:f.clouds,transparent:!0})),r.add(i),r.add(s)):(i.material=new THREE.MeshPhongMaterial({map:THREE.ImageUtils.loadTexture("img/network/map/test.jpg"),bumpMap:THREE.ImageUtils.loadTexture("img/network/map/bump.jpg"),bumpScale:.5,specularMap:THREE.ImageUtils.loadTexture("img/network/map/water.png"),specular:"#333333"}),s.material=new THREE.MeshPhongMaterial({map:THREE.ImageUtils.loadTexture("img/network/map/clouds.png"),transparent:!0}))},onWindowResize:function(){a.aspect=g/h,a.updateProjectionMatrix(),o.setSize(g,h)},onMouseMove:function(e){e.preventDefault();var t=$(this).offset(),n=e.pageX-t.left,o=e.pageY-t.top;d.x=n/g*2-1,d.y=2*-(o/h)+1},generateLocation:function(e,t){var n=new THREE.MeshPhongMaterial({color:"#55ff55",shading:THREE.SmoothShading}),o=new THREE.Mesh(new THREE.SphereGeometry(1,15,15),n),a=m.latLongToVector3(t.latitude,t.longitude,51);o.position.x=a.x,o.position.y=a.y,o.position.z=a.z;var r=new THREE.PointLight(5635925,1,100);r.name="light",o.add(r),o.userData=t,p.push(o)},render:function(){if(o){i.rotation.y+=2e-4,s.rotation.y+=3e-4,u.setFromCamera(d,a);var e=u.intersectObjects(p);if(e.length>0){if(c!=e[0].object){c&&c.material.emissive.setHex(c.currentHex),c=e[0].object,c.currentHex=c.material.emissive.getHex(),c.material.emissive.setHex(16711680);var t=c.getObjectByName("light");t.color.setHex(16711680),t.intensity=2}}else{if(c){c.material.emissive.setHex(c.currentHex);var t=c.getObjectByName("light");t.color.setHex(5635925),t.intensity=1}c=null}o.render(r,a)}},animate:function(){requestAnimationFrame(m.animate),m.render()},latLongToVector3:function(e,t,n){var o=e*Math.PI/180,a=t*Math.PI/180;return x=n*Math.cos(o)*Math.cos(a),y=n*Math.sin(o),z=-n*Math.cos(o)*Math.sin(a),new THREE.Vector3(x,y,z)}};m.buildScene(),m.animate(),e.$watch("mapData",function(){if(null!==e.mapData){var t=e.mapData,n=e.latlong;m.buildEarth();for(var o=0;o<p.length;o++)r.remove(p[o]);p.length=0;for(var o=0;o<t.length;o++){var a=t[o],i=a.code,s={latitude:n[i].latitude,longitude:n[i].longitude,name:a.name};m.generateLocation(r,s)}for(var o=0;o<p.length;o++)r.add(p[o])}})}}}]),services.factory("analysisMgtService",["$http","$rootScope","$interval","user",function(e,t,n,o){function a(e,t){return e.report.analyseConfig.selectedDataFiles[0].name<t.report.analyseConfig.selectedDataFiles[0].name?-1:e.report.analyseConfig.selectedDataFiles[0].name>t.report.analyseConfig.selectedDataFiles[0].name?1:0}function r(e,t){return e.completionDate<t.completionDate?1:e.completionDate>t.completionDate?-1:0}function i(e){"date"===m?listProcessedAnalysisTmp.sort(r):listProcessedAnalysisTmp.sort(a)}function s(){angular.isDefined(w)||(w=n(v,2e3))}function l(){angular.isDefined(w)&&(n.cancel(w),w=void 0)}function u(){t.$broadcast("runAnalysisCountChanged",f)}function c(){t.$broadcast("completeAnalysisCountChanged",g)}function d(){t.$emit("updateRuns",f)}function p(){t.$emit("netConfigChanged",h)}var f=[],g=[],h=null,m="date",v=function(){var t="/ws-resources/analysis/appinfos/";t+=o.current.hasOwnProperty("user_id")?o.current.user_id:"guest",e.get(t).then(function(e){listRunningAnalysisTmp=e.data.runningAnalysis,listProcessedAnalysisTmp=e.data.processedAnalysis,h=e.data.networkConfig,i(listProcessedAnalysisTmp);var t=!1,n=!1;listRunningAnalysisTmp.length!==f.length&&(t=!0),listProcessedAnalysisTmp.length!==g.length&&(n=!0),g=listProcessedAnalysisTmp,f=listRunningAnalysisTmp,t&&u(),n&&c(),d(),p()})};v();var w;return{stopAllAnalyses:function(){var t="guest";o.current.hasOwnProperty("user_id")&&(t=o.current.user_id);var n="/ws-resources/analysis/stopall?user_id="+t,a=e["delete"](n);return a},requestAnalysisStatus:function(t,n){var o={analyseId:t,newStatus:n};e({method:"POST",url:"/ws-resources/analysis/statusrequest",data:o})},getRuns:function(){return f},getCompleted:function(){return g},getNetworkConfig:function(){return h},requestStartPolling:function(){s()},requestStopPolling:function(){l()},setSortType:function(e){m!=e&&(m=e,i(g),c())}}}]),services.factory("basespaceService",["$http",function(e){var t,n,o;return{userinfo:function(){return n||(n=e({method:"GET",url:"/ws-resources/bs-model/userinfo"}).then(function(e){return e.data},function(e){return{name:"err usr",id:"err id"}})),n},connectionstatus:function(){return t||(t=e({method:"GET",url:"/ws-resources/bs-model/connectionstatus"}).then(function(e){return"true"===e.data},function(e){return!1})),t},listruns:function(){return o||(o=e({method:"GET",url:"/ws-resources/bs-model/userruns"}).then(function(e){return e.data},function(e){return{}})),o}}}]),services.factory("PatternStorageSrvc",["$http",function(e){return{getAllPatterns:function(){var t=e({method:"GET",url:"/ws-resources/datastorage/extractionPatterns"}).then(function(e){return e.data},function(e){return null});return t},getAllSplitPatterns:function(){var t=e({method:"GET",url:"/ws-resources/datastorage/splitPatterns"}).then(function(e){return e.data},function(e){return null});return t}}}]),services.factory("reportSrvc",["$http",function(e){return{getReport:function(t){var n="/ws-resources/analysis/report/"+t,o=e({method:"GET",url:n}).then(function(e){return e.data},function(e){return null});return o},getPDFResult:function(t){var n="/ws-resources/analysis/report/"+t+"/pdf",o=e({method:"GET",url:n}).then(function(e){return window.location=n,e.data},function(e){return null});return o},getCSVResult:function(t){var n="/ws-resources/analysis/report/"+t+"/csv",o=e({method:"GET",url:n}).then(function(e){return e.data},function(e){return null});return o}}}]),services.factory("uploadService",["$http","FileUploader","analysisMgtService","$rootScope",function(e,t,n,o){function a(e){var t=-1,n=[" kB"," MB"," GB"," TB","PB","EB","ZB","YB"];do e/=1024,t++;while(e>1024);return Math.max(e,.1).toFixed(2)+n[t]}function r(e,t){var n={analyseId:e,progressval:t};o.$emit("uploadProgressChanged",n)}function i(e,t){var n={analyseId:e,filename:t};o.$emit("uploadingFileChanged",n)}var s=[],l=[],u=new t,c=new t,d="",p=-1,f="",g=function(e,t,n){if(e.file){var o=a(e.file.size),r={origin:"UPLOAD",name:e.file.name,dateCreated:e.file.lastModifiedDate,id:e.file.name,size:e.file.size,readableSize:o},i=n(r);if(i)return t.push({assViewfile:r,fileItem:e}),!0}return!1},h=function(e,t,n){if(e)for(var o=t.length,a=0;o>a;a++){var r=t[a].assViewfile;if(r===e)return n.removeFromQueue(t[a].fileItem),t.splice(a,1),!0}return!1},m=function(e,t,o,a){f=e,n.requestAnalysisStatus(e,"RETRIEVE_FILES");var s=[{analyseID:e}];o.onBeforeUploadItem=function(n){Array.prototype.push.apply(n.formData,s),n.url=t+"?analyseid="+e,d=n.file.name,i(e,d)},o.onProgressAll=function(t){p!==t&&(p=t,r(e,t))},o.onCompleteAll=function(){o.clearQueue(),d="",p=-1,f="",a&&c.queue.length>0?m(e,"/ws-resources/datastorage/uploadLibFile",c,!1):r(e,-1)},o.onErrorItem=function(t,o,a,r){n.requestAnalysisStatus(e,"UPLOAD_ERROR"),c.cancelAll(),u.cancelAll(),c.clearQueue(),u.clearQueue()},o.uploadAll()};return{addToUploadData:function(e,t){return g(e,s,t)},addToUploadLibs:function(e,t){return g(e,l,t)},removeFromUploadData:function(e){return h(e,s,u)},removeFromUploadLibs:function(e){return h(e,l,c)},getDataUploader:function(e){return u},getLibsUploader:function(e){return c},getCurrentUploadingFileName:function(){return d},getCurrentUploadingProgress:function(){return p},getCurrentAnalyseID:function(){return f},clearLists:function(){s=[],l=[],c.clearQueue(),u.clearQueue()},uploadNeeded:function(){return s.length>0||l.length>0},showList:function(){alert("upload data length "+u.queue.length+"  updatafiles "+s),alert("upload data length "+c.queue.length+"  updatafiles "+l)},startUpload:function(e){u.queue.length>0?m(e,"/ws-resources/datastorage/uploadDataFile",u,!0):m(e,"/ws-resources/datastorage/uploadLibFile",c,!1)}}}]),app.controller("fileManagerController",["uploadService","$rootScope","$scope","$http",function(e,t,n,o){this.title="Generic file manager",this.localFileInputSelector="file-input-"+this.uid;var a;"library"===this.fileType?(this.fileUploader=e.getLibsUploader(),this.addToUploadListFunction=e.addToUploadLibs,this.removeFromUploadListFunction=e.removeFromUploadLibs,a="/ws-resources/datastorage/libFiles"):(this.fileUploader=e.getDataUploader(),this.addToUploadListFunction=e.addToUploadData,this.removeFromUploadListFunction=e.removeFromUploadData,a="/ws-resources/datastorage/dataFiles"),
n.storedFilesList=[],this.fetchStoredFilesList=o({method:"GET",url:a}).success(function(e,t,o,a){n.storedFilesList=e}).error(function(e,t,o,a){n.storedFilesList=[]}),this.openBaseSpaceSelectionDial=function(){$("#basespacedialog").data("fileManager",this).dialog({modal:!0,width:700,show:{effect:"fade",duration:1e3},hide:{effect:"fade",duration:500},close:$(".accrun").accordion({heightStyle:"content",autoHeight:!1,clearStyle:!0,active:!1,header:"h3",collapsible:!0}),buttons:{Ok:function(){var e=$(this).data("fileManager");t.$emit("requestBaseSpaceDialogClose",e),$(this).dialog("close")}}}),$(".accrun .ui-state-focus").removeClass("ui-state-focus")},this.addViewFile=angular.bind(this,function(e){if(null!==e){var t=this.checkDuplicates(this.selectedFiles,e);if(t)return this.selectedFiles.push(e),!0;var n="#dialog-duplicate-warning-"+this.uid;return this.showDialog(n),!1}}),this.addViewFileAndApply=function(e){this.addViewFile(e),n.$apply()},this.fileUploader.onAfterAddingFile=angular.bind(this,function(e){var t=this.addToUploadListFunction(e,this.addViewFile);t||this.fileUploader.removeFromQueue(e)}),this.checkDuplicates=function(e,t){for(var n=e.length,o=0;n>o;o++)if(e[o].id===t.id)return!1;return!0},this.selectFile=function(){var e=document.getElementById(this.localFileInputSelector);if(e&&document.createEvent){var t=document.createEvent("MouseEvents");t.initEvent("click",!0,!1),e.dispatchEvent(t)}},this.removeSelectedFile=function(t){var n=this.getFileIndex(this.selectedFiles,t);if(-1!=n&&(this.selectedFiles.splice(n,1),"UPLOAD"===t.origin)){var o=e.removeFromUploadListFunction(t);o||alert("impossible to remove file from upload list")}},this.isEmpty=function(e){return void 0!==e?0==e.length:!0},this.getFileIndex=function(e,t){for(var n=e.length,o=0;n>o;o++)if(e[o].id===t.id)return o;return-1},this.showDialog=function(e,t){$(e).dialog({modal:!0,width:700,show:{effect:"fade",duration:1e3},hide:{effect:"fade",duration:1e3},buttons:{Ok:function(){$(this).dialog("close"),t&&t()}}})}}]);