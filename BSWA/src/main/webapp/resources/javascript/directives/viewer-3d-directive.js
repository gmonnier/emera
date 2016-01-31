app.directive('viewerMap', function($rootScope) {
	return {
		link : function(scope, element, attrs) {
			
			var renderer, camera, scene, sphere, clouds;
			
			var _scene = {
				
				buildScene: function() {
					// set the scene size
					var WIDTH = 800,
					  HEIGHT = 600;

					// set some camera attributes
					var VIEW_ANGLE = 45,
					  ASPECT = WIDTH / HEIGHT,
					  NEAR = 0.1,
					  FAR = 10000;
				    renderer = new THREE.WebGLRenderer({alpha: true, antialias: true});
				    renderer.setClearColor(0xffffff, 0);
				      
					camera =
					  new THREE.PerspectiveCamera(
					    VIEW_ANGLE,
					    ASPECT,
					    NEAR,
					    FAR);

					camera.position.z = 1.5;
					
					scene = new THREE.Scene();

					// add the camera to the scene
					scene.add(camera);
					
					//cameraLight = _scene.generateLight();
					//scene.add(cameraLight);
					scene.add(new THREE.AmbientLight(0x333333));

					var light = new THREE.DirectionalLight(0xffffff, 1);
					light.position.set(5,3,5);
					scene.add(light);
				

					sphere = new THREE.Mesh(
							  new THREE.SphereGeometry(0.5, 32, 32),
							  new THREE.MeshPhongMaterial({
							    map: THREE.ImageUtils.loadTexture('img/network/map/test.jpg'),
							    bumpMap: THREE.ImageUtils.loadTexture('img/network/map/bump.jpg'),
							    bumpScale:   0.005,
							    specularMap: THREE.ImageUtils.loadTexture('img/network/map/water.png'),
							    specular: '#333333'      })
							);
					
					clouds = new THREE.Mesh(
							  new THREE.SphereGeometry(0.503, 32, 32),
							  new THREE.MeshPhongMaterial({
							    map: THREE.ImageUtils.loadTexture('img/network/map/clouds.png'),
							    transparent: true
							  })
							);

					// add the sphere to the scene
					scene.add(sphere);
					scene.add(clouds);

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
			    	sphere.rotation.y += 0.0005;
			  		clouds.rotation.y += 0.0006;		
			  		
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