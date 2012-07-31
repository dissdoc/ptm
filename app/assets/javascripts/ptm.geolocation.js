(function($) {
    $.fn.ptmGeolocation = function(options) {
        var defaults = {
            width: '320px',
            height_map: '240px'
        }, 	opts = $.extend(defaults, options);

        // get value ====> opts['location']


        var geocoder = null;
        if (geocoder == null)
            geocoder = new google.maps.Geocoder();

        var lat = $('#latitude').attr('value');
        var lng = $('#longitude').attr('value');
        var latlng = new google.maps.LatLng(lat, lng);
        var myOptions = {
            zoom: 3,
            center: latlng,
            mapTypeId: google.maps.MapTypeId.ROADMAP,
            streetViewControl: false,
            mapTypeControl: false
        }
        var map = new google.maps.Map(document.getElementById("geomap"), myOptions);

        var marker = new google.maps.Marker({
            position: latlng,
            draggable:true,
            animation: google.maps.Animation.DROP,
            map: map,
            icon: opts['marker']
        });
        google.maps.event.addListener(marker, 'click', toggleBounce);
        google.maps.event.addListener(marker, 'dragend', markerPositionChanged);

        function toggleBounce() {
            if (marker.getAnimation() != null) {
                marker.setAnimation(null);
            } else {
                marker.setAnimation(google.maps.Animation.BOUNCE);
            }
        }

        function markerPositionChanged() {
            var latlng = marker.getPosition();
            geocoder.geocode({ 'latLng': latlng, 'language' : 'ru' }, function (results, status) {
                if (status == google.maps.GeocoderStatus.OK) {
                    map.setCenter(results[0].geometry.location);
                    setAddress(results[0]);
                }
            });
        }

        function setAddress(item) {
            var new_address = composeAddress(item);
            $('#geofield').val(new_address);
        }

        function composeAddress(item) {
            retAddress = "";
            $.each(item.address_components, function (i, address_item) {
                var isOk = false;
                $.each(address_item.types, function (j, typeName) {
                    //не будем брать значения адреса улицы и локали (города) - город потом будет в administrative_level_2
                    if (typeName != "street_address" && typeName != "locality") {
                        isOk = true;
                    }
                });
                if (isOk) {
                    if (retAddress == "") {
                        retAddress = address_item.long_name;
                    } else {
                        retAddress = retAddress + ", " + address_item.long_name;
                    }
                }
            });
            return retAddress;
        }
    };
})(jQuery);