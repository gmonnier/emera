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
		      'vendor/ammap_3.13.3.free/ammap/ammap.css'],
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