app.config([ '$routeProvider', function($routeProvider) {
	$routeProvider.when('/analysis-selection', {
		templateUrl : 'analysisSelection.html',
		controller : 'analysisSelectionCtrl',
		css: 'WEB-INF/css/analysis-selection.css',
		public : true
	}).when('/create-split-analysis', {
		templateUrl : 'createSplitPan.html',
		controller : 'createSplitCtrl',
		css: ['WEB-INF/css/createSplitPan.css, WEB-INF/css/baseSpaceDial.css]',
		public : true
	}).when('/create-lookup-analysis', {
		templateUrl : 'createLookupPan.html',
		controller : 'createLookupCtrl',
		css: '[WEB-INF/css/createLookupPan.css, WEB-INF/css/baseSpaceDial.css]',
		public : true
	}).when('/running', {
		templateUrl : 'runningPan.html',
		controller : 'runningCtrl',
		css: 'WEB-INF/css/running-pan.css',
		public : true
	}).when('/config', {
		templateUrl : 'networkConfigPan.html',
		controller : 'networkCtrl',
		css: ['WEB-INF/css/configPan.css','components/ammap_3.13.3.free/ammap/ammap.css'],
		public : true
	}).when('/', {
		templateUrl : 'homePan.html',
		css: ['WEB-INF/css/home.css', 'components/slider/csss_engine1/style.css'],
		public : true
	}).otherwise({
		templateUrl : 'homePan.html',
		css: ['WEB-INF/css/home.css', 'components/slider/csss_engine1/style.css']
	});
} ]);