app.directive('viewerMap', function($rootScope) {
	return {
		link : function(scope, element, attrs) {
			
			var renderer, camera, scene, particleLight, cameraLight;
			
			var _scene = {
				
				buildScene: function() {
					// set the scene size
					var WIDTH = 400,
					  HEIGHT = 300;

					// set some camera attributes
					var VIEW_ANGLE = 45,
					  ASPECT = WIDTH / HEIGHT,
					  NEAR = 0.1,
					  FAR = 10000;

					// get the DOM element to attach to
					// - assume we've got jQuery to hand
					

					// create a WebGL renderer, camera
					// and a scene
				    renderer = new THREE.WebGLRenderer({alpha: true, antialias: true});
				    renderer.setClearColor(0xffffff, 0);
				      
					camera =
					  new THREE.PerspectiveCamera(
					    VIEW_ANGLE,
					    ASPECT,
					    NEAR,
					    FAR);

					scene = new THREE.Scene();

					// add the camera to the scene
					scene.add(camera);
					
					cameraLight = _scene.generateLight();
					scene.add(cameraLight);
					
					particleLight = new THREE.Mesh(new THREE.SphereBufferGeometry(4, 8, 8), new THREE.MeshBasicMaterial({color: 0xffffff}));
				    scene.add(particleLight);
				    var pointLight = new THREE.PointLight(0x55ff55, 2, 800);
				    particleLight.add(pointLight);

					// the camera starts at 0,0,0
					// so pull it back
					camera.position.z = 300;
					
					// set up the sphere vars
					var radius = 50,
					    segments = 16,
					    rings = 16;

					var material = new THREE.MeshPhongMaterial({
			            "color": '#ffffff',
			            "specular": "#777777",
			            "shininess": "100",
			            "shading": THREE.SmoothShading,
			            metal: true
			          });
					// create a new mesh with
					// sphere geometry - we will cover
					// the sphereMaterial next!
					var sphere = new THREE.Mesh(

					  new THREE.SphereGeometry(
					    radius,
					    segments,
					    rings),

					    material);

					// add the sphere to the scene
					scene.add(sphere);

					// start the renderer
					renderer.setSize(WIDTH, HEIGHT);

					// attach the render-supplied DOM element
					element.append(renderer.domElement);
					
				    var currentControls = new THREE.OrbitControls(camera);
				    currentControls.noPan = true;
				    currentControls.addEventListener('change', _scene.render);
			      
				},
	
			    generateLight: function () {
			      // create a point light
			      var pointLight =
			        new THREE.PointLight(0xFFFFFF);
	
			      // set its position
			      pointLight.position.x = 10;
			      pointLight.position.y = 50;
			      pointLight.position.z = 130;
	
			      return pointLight;
			    },
	
			    render: function () {
			      if (renderer) {
	
			        var timer = Date.now() * 0.00025;
			        particleLight.position.x = Math.sin(timer * 7) * 300;
			        particleLight.position.y = Math.cos(timer * 5) * 400;
			        particleLight.position.z = Math.cos(timer * 3) * 300;
	
			        // Set light following camera direction
			        cameraLight.position.copy(camera.position);
			        renderer.render(scene, camera);
	
			      }
			    },
	
			    animate: function () {
			      requestAnimationFrame(_scene.animate);
			      _scene.render();
			    }
			  };
			
			_scene.buildScene();
			_scene.animate();
		}
	};
});