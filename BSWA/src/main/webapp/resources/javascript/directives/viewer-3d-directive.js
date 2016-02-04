app.directive('viewerMap', function($rootScope) {
	return {
		link : function(scope, element, attrs) {
			
			var renderer, camera, scene, sphere, clouds, controls, raycaster;
			var mouse = new THREE.Vector2();
			var INTERSECTED;
			var locationMeshes = [];
			var texturesMap = {};
			
			var WIDTH = window.innerWidth - 160;
			var HEIGHT = window.innerHeight - 160;
			
			
			var _scene = {
				
				buildScene: function() {

					// set some camera attributes
					var VIEW_ANGLE = 45,
					  ASPECT = WIDTH / HEIGHT,
					  NEAR = 0.1,
					  FAR = 10000;
				    renderer = new THREE.WebGLRenderer({antialias: true});
				      
					camera = new THREE.PerspectiveCamera(
					    VIEW_ANGLE,
					    ASPECT,
					    NEAR,
					    FAR);
					camera.position.z = 150;
					
					controls = new THREE.OrbitControls(camera);
					controls.noZoom = true;
					controls.noPan = true;
					//controls.addEventListener( 'change', _scene.render );
					
					scene = new THREE.Scene();
					// add the camera to the scene
					scene.add(camera);
					
					scene.add(new THREE.AmbientLight(0x333333));

					var light = new THREE.DirectionalLight(0xffffff, 1);
					light.position.set(5,3,5);
					scene.add(light);
				

					// Build earth
					_scene.buildEarth();
					

					// start the renderer
					renderer.setSize(WIDTH, HEIGHT);
					
					raycaster = new THREE.Raycaster();

					// attach the render-supplied DOM element
					element.append(renderer.domElement);
					$('canvas').mousemove( _scene.onMouseMove);
					
					
					window.addEventListener( 'resize', _scene.onWindowResize, false );
					
				},
				
				buildEarth: function(){
					
					var loader = new THREE.TextureLoader();
					loader.crossOrigin = '';
					texturesMap['maintexture'] = texturesMap['maintexture'] == null ? loader.load('http://d2t2bnlh53lbhy.cloudfront.net/network/map/test.jpg') : texturesMap['maintexture'];
					texturesMap['bump'] = texturesMap['bump'] == null ? loader.load('http://d2t2bnlh53lbhy.cloudfront.net/network/map/bump.jpg') : texturesMap['bump'];
					texturesMap['water'] = texturesMap['water'] == null ? loader.load('http://d2t2bnlh53lbhy.cloudfront.net/network/map/water.png') : texturesMap['water'];
					texturesMap['clouds'] = texturesMap['clouds'] == null ? loader.load('http://d2t2bnlh53lbhy.cloudfront.net/network/map/clouds.png') : texturesMap['clouds'];
					
					if(sphere == null || clouds == null) {
						sphere = new THREE.Mesh(
								  new THREE.SphereGeometry(50, 64, 64),
								  new THREE.MeshPhongMaterial({
								    map: texturesMap['maintexture'],
								    bumpMap: texturesMap['bump'],
								    bumpScale:   0.005,
								    specularMap: texturesMap['water'],
								    specular: '#333333'      })
								);
						
						clouds = new THREE.Mesh(
								  new THREE.SphereGeometry(51, 64, 64),
								  new THREE.MeshPhongMaterial({
								    map: texturesMap['clouds'],
								    transparent: true
								  })
								);
	
						// add the sphere to the scene
						scene.add(sphere);
						scene.add(clouds);
					} else {
						//only update material
						sphere.material = new THREE.MeshPhongMaterial({
						    map: texturesMap['maintexture'],
						    bumpMap: texturesMap['bump'],
						    bumpScale:   0.5,
						    specularMap: texturesMap['water'],
						    specular: '#333333'      });
						clouds.material = new THREE.MeshPhongMaterial({
						    map: texturesMap['clouds'],
						    transparent: true
						  });
					}
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
				
				generateLocation: function(scene, locationData, isServer) {
					
					var color = '#55ff55';
					var size = 1;
					if(isServer) {
						size *= 1.5;
						color = '#ffff55';
					}
					var locmaterial = new THREE.MeshPhongMaterial({
				            "color": color,
				            //"shininess": "100",
				            //"emissive": '#55ff55',
				            "shading": THREE.SmoothShading
				          });
					 
					var location = new THREE.Mesh(
							  new THREE.SphereGeometry(size, 15, 15),
							  locmaterial
							);
				    
				    var cartesianPosition = _scene.latLongToVector3(locationData.latitude,locationData.longitude,51);
				    
				    location.position.x = cartesianPosition.x;
				    location.position.y = cartesianPosition.y;
				    location.position.z = cartesianPosition.z;
				   
				    var locationLight = new THREE.PointLight(color, 1.0, 100);
				    locationLight.name = 'light';
				    location.add(locationLight);
				    
				    location.userData = locationData;
				    locationMeshes.push(location);
				},
	
			    render: function () {
			      if (renderer) {
			    	  
			    	//sphere.rotation.y += 0.0002;
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
							
							var associatedLight = INTERSECTED.getObjectByName ('light');
							associatedLight.color.setHex( 0xff0000 );
							associatedLight.intensity = 2.0;
							
						}
					} else {
						if ( INTERSECTED ) {
							INTERSECTED.material.emissive.setHex( INTERSECTED.currentHex );
							var associatedLight = INTERSECTED.getObjectByName ('light');
							associatedLight.color.setHex( 0x55ff55);
							associatedLight.intensity =1.0;
						}
						
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
					
					_scene.buildEarth();
					
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
						
						_scene.generateLocation(scene,locationData, i == 0);
					}
					
					for (var i = 0; i < locationMeshes.length; i++) {
						scene.add(locationMeshes[i]);
					}
				}
			});
		}
	};
});