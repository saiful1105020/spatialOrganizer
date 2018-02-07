function myMap(a, b ) {
        
          var myLatLng = {lat: a , lng: b };

          var map = new google.maps.Map(document.getElementById('map'), {
            zoom: 8,
            center: myLatLng
          });
  
          var marker = new google.maps.Marker({
            position: myLatLng,
            map: map,
            title: 'Hello World!'
          });

          
        }

 function getLat()
      {
        var myLatLng = {lat: 23.8103 , lng: 90.4125 };
         
         var map = new google.maps.Map(document.getElementById('map2'), {
            zoom: 8,
            center: myLatLng
          });
        var marker;
        google.maps.event.addListener(map, 'click', function(event) {

           marker = new google.maps.Marker({position: event.latLng, map: map});
           $.ajax({
              type: "POST",
              url: "Admin.php",
              data: { lat: event.latLng.lat, lon: event.latlng.lng }
            })
              .done(function( msg ) {
                alert( "Data Saved: " + msg );
              });

            });
        
      }

      function initMap( ) {
        //var ar = <?php json_encode($r)?>;
		
        var jArray = [];
        jArray[0] =23.8103; jArray[1] = 90.4125;
        jArray[2] = 22.3475; jArray[3] = 91.8123;
        jArray[4] = 24.9045; jArray[5] = 91.8611;
        jArray[6] = 24.3636; jArray[7] = 88.6241;
        jArray[8] = 22.7029; jArray[9] = 90.3466;
        jArray[10] = 25.7439 ; jArray[11] = 89.2572;
        jArray[12] = 22.8456; jArray[13] = 89.5403;
          var myLatLng = {lat: jArray[0] , lng: jArray[1] };

          var map = new google.maps.Map(document.getElementById('map'), {
            zoom: 8,
            center: myLatLng
          });
		//var radioArray = <?php json_encode($mapData); ?>;
		console.log(radioArray);
          //var x=document.getElementById('vehicle').checked;
        for(var i=0;i<6;i++)
        {
          myLatLng = {lat: jArray[i*2] , lng: jArray[i*2+1] };

          // var map = new google.maps.Map(document.getElementById('map'), {
          //   zoom: 10,
          //   center: myLatLng
          // });

          if(x)
          {
            var marker = new google.maps.Marker({
              position: myLatLng,
              map: map,
              title: ''
            }); 
          }
        }
      }
 