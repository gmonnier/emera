app.directive('viewerMap', function($rootScope) {
	return {
		link : function(scope, element, attrs) {
			
			var renderer, camera, scene, sphere, clouds, controls, raycaster;
			var mouse = new THREE.Vector2();
			var INTERSECTED;
			var locationMeshes = [];
			
			var WIDTH = 800,
			  HEIGHT = 600;
			
			var _utility = {
					 showAxis: function (length) {
					      var axes = new THREE.Object3D();
		
					      axes.add(_utility.buildAxis(new THREE.Vector3(0, 0, 0), new THREE.Vector3(length, 0, 0), 0xFF0000, false)); // +X
					      axes.add(_utility.buildAxis(new THREE.Vector3(0, 0, 0), new THREE.Vector3(-length, 0, 0), 0xFF0000, true)); // -X
					      axes.add(_utility.buildAxis(new THREE.Vector3(0, 0, 0), new THREE.Vector3(0, length, 0), 0x00FF00, false)); // +Y
					      axes.add(_utility.buildAxis(new THREE.Vector3(0, 0, 0), new THREE.Vector3(0, -length, 0), 0x00FF00, true)); // -Y
					      axes.add(_utility.buildAxis(new THREE.Vector3(0, 0, 0), new THREE.Vector3(0, 0, length), 0x0000FF, false)); // +Z
					      axes.add(_utility.buildAxis(new THREE.Vector3(0, 0, 0), new THREE.Vector3(0, 0, -length), 0x0000FF, true)); // -Z
		
					      return axes;
					    },
		
					    buildAxis: function (src, dst, colorHex, dashed) {
					      var geom = new THREE.Geometry(),
					        mat;
		
					      if (dashed) {
					        mat = new THREE.LineDashedMaterial({linewidth: 3, color: colorHex, dashSize: 3, gapSize: 3});
					      } else {
					        mat = new THREE.LineBasicMaterial({linewidth: 3, color: colorHex});
					      }
		
					      geom.vertices.push(src.clone());
					      geom.vertices.push(dst.clone());
					      geom.computeLineDistances(); // This one is SUPER important, otherwise dashed lines will appear as simple plain lines
		
					      var axis = new THREE.Line(geom, mat, THREE.LinePieces);
		
					      return axis;
		
					    }
				};
			
			var _scene = {
				
				buildScene: function() {

					// set some camera attributes
					var VIEW_ANGLE = 45,
					  ASPECT = WIDTH / HEIGHT,
					  NEAR = 0.1,
					  FAR = 10000;
				    renderer = new THREE.WebGLRenderer({alpha: true, antialias: true});
				    renderer.setClearColor( 0xcccccc, 0);
				      
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
					
					raycaster = new THREE.Raycaster();

					// attach the render-supplied DOM element
					element.append(renderer.domElement);
					$('canvas').mousemove( _scene.onMouseMove);
					
					controls = new THREE.OrbitControls(camera);
					controls.noPan = true;
					
					scene.add(_utility.showAxis(50));
					
					window.addEventListener( 'resize', _scene.onWindowResize, false );
					
				},
				
				onWindowResize: function() {
					camera.aspect = WIDTH / HEIGHT;
					camera.updateProjectionMatrix();

					renderer.setSize( WIDTH, HEIGHT );
				},
				
				onMouseMove: function( event ) {
					event.preventDefault();
					var parentOffset = $(this).offset(); 
					var relX = event.pageX - parentOffset.left;
					var relY = event.pageY - parentOffset.top;
					   
					mouse.x = ( relX / WIDTH ) * 2 - 1;
					mouse.y = - ( relY / HEIGHT ) * 2 + 1;
				},
				
				generateLocation: function(scene, locationData) {
					 var locmaterial = new THREE.MeshPhongMaterial({
				            "color": '#55ff55',
				            "specular": "#111111",
				            "shininess": "50",
				            "emissive": '#55ff55',
				            "shading": THREE.SmoothShading
				          });
					 
					var location = new THREE.Mesh(
							  new THREE.SphereGeometry(0.01, 15, 15),
							  locmaterial
							);
				    
				    var cartesianPosition = _scene.latLongToVector3(locationData.latitude,locationData.longitude,0.51);
				    
				    location.position.x = cartesianPosition.x;
				    location.position.y = cartesianPosition.y;
				    location.position.z = cartesianPosition.z;
				    
				    var locationLight = new THREE.PointLight(0x55ff55, 2, 100);
				    location.add(locationLight);
				    
				    location.userData = locationData;
				    locationMeshes.push(location);
				    
				    scene.add(location);
				},
	
			    render: function () {
			      if (renderer) {
			    	  
			    	//sphere.rotation.y += 0.0005;
			  		clouds.rotation.y += 0.0003;	
			  	
			  		
			  		// Detect location on hover
			  		raycaster.setFromCamera( mouse, camera );
			  		var intersects = raycaster.intersectObjects( locationMeshes );
			  		
					if ( intersects.length > 0 ) {
						if ( INTERSECTED != intersects[ 0 ].object ) {
							if ( INTERSECTED ) INTERSECTED.material.emissive.setHex( INTERSECTED.currentHex );
							INTERSECTED = intersects[ 0 ].object;
							INTERSECTED.currentHex = INTERSECTED.material.emissive.getHex();
							INTERSECTED.material.emissive.setHex( 0xff0000 );
							
							console.log(INTERSECTED.userData);
						}
					} else {
						if ( INTERSECTED ) INTERSECTED.material.emissive.setHex( INTERSECTED.currentHex );
						INTERSECTED = null;
					}
			  		
			        renderer.render(scene, camera);
	
			      }
			    },
	
			    animate: function () {
			      requestAnimationFrame(_scene.animate);
			      _scene.render();
			    },
			    
			    // convert the positions from a lat, lon to a position on a sphere.
			    latLongToVector3: function(latitude, longitude, radius) {
			    	
			    	console.log('latitude = ' + latitude);
			    	console.log('longitude = ' + longitude)
			    	
			        var latrad = (latitude) * Math.PI / 180;
			        var lonrad = (longitude) * Math.PI / 180;
			        
			        x = radius * Math.cos(latrad) * Math.cos(lonrad);
			        y = radius * Math.sin(latrad) ;
			        z = - radius * Math.cos(latrad) * Math.sin(lonrad);
			        
			        return new THREE.Vector3(x,y,z);
			    }
			  };
			
			_scene.buildScene();
			_scene.animate();
			
			
			
			scope.$watch('mapData', function(){
				
				if(scope.mapData !== null) {
					var mapData = scope.mapData;
					var latlong = scope.latlong;
					
					for (var i = 0; i < locationMeshes.length; i++) {
						scene.remove( locationMeshes[i] );
					}
					locationMeshes.length = 0;
					
					for (var i = 0; i < mapData.length; i++) {
						var dataItem = mapData[i];
						var id = dataItem.code;
						var locationData = {
							latitude: latlong[id].latitude,
							longitude: latlong[id].longitude,
							name: dataItem.name
						}
						
						_scene.generateLocation(scene,locationData);
					}
					
					for (var i = 0; i < locationMeshes.length; i++) {
						scene.add(locationMeshes[i]);
					}
				}
			});
		}
	};
});