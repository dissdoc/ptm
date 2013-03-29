(function($) {
    var map = null;
    var marker = null;
    var markers = null;
    var geocoder = null;
    if (geocoder == null)
        geocoder = new google.maps.Geocoder();

    function positionChanged(latlng, field, lat, lng) {
        geocoder.geocode({'latLng': latlng}, function(results, status) {
            if (status == google.maps.GeocoderStatus.OK) {
                map.setCenter(results[0].geometry.location);
                var new_address = composeAddress(results[0]);
                field.val(new_address);
                lat.val(marker.getPosition().lat());
                lng.val(marker.getPosition().lng());
            }
        });
    }

    function composeAddress(item) {
        retAddress = "";
        $.each(item.address_components, function (i, address_item) {
            var isOk = false;
            $.each(address_item.types, function (j, typeName) {
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

    var methods = {
        init: function(options) {
            var settings = $.extend({
                width: '0px',
                height: '0px'
            }, options);

            this.css({width: settings['width'], height: settings['height']});

            var config = {
                zoom: 1,
                center: new google.maps.LatLng(18, 8),
                zoomControl: true,
                scrollwhell: true,
                mapTypeId: google.maps.MapTypeId.ROADMAP,
                streetViewControl: false,
                mapTypeControl: false
            };
            map = new google.maps.Map(document.getElementById(this.attr('id')), config);
            google.maps.event.trigger(map, 'resize');
        },
        bounds: function(options, mthd) {
            var settings = $.extend({
                ne_lat: '.ne_lat',
                ne_lng: '.ne_lng',
                sw_lat: '.sw_lat',
                sw_lng: '.sw_lng',
                mapAction: '.mapAction'
            }, options);

            google.maps.event.addListener(map, 'idle', function() {
                var currentBounds = this.getBounds();
                $(settings['ne_lng']).val(currentBounds.getNorthEast().lng());
                $(settings['ne_lat']).val(currentBounds.getNorthEast().lat());
                $(settings['sw_lng']).val(currentBounds.getSouthWest().lng());
                $(settings['sw_lat']).val(currentBounds.getSouthWest().lat());
            });

            google.maps.event.addListener(map, 'dragend', function() {
                $(settings['mapAction']).val('true');
                mthd();
            });

            google.maps.event.addListener(map, 'zoom_changed', function() {
                $(settings['mapAction']).val('true');
                mthd();
            });
        },
        marker: function(options) {
            var settings = $.extend({
                field: '#field',
                lat: '#lat',
                lng: '#lng'
            }, options);

            google.maps.event.addListener(map, 'click', function(event) {
                if (marker != null) marker.setMap(null);
                this.setCenter(event.latLng);
                positionChanged(event.latLng, $(settings['field']), $(settings['lat']), $(settings['lng']));
                marker = new google.maps.Marker({
                    position: event.latLng,
                    map: this,
                    draggable: true,
                    animation: null,
                    icon: '/resources/images/GoogleMap_Pink.png'
                });

                google.maps.event.addListener(marker, 'dragend', function(event) {
                    map.setCenter(event.latLng);
                    positionChanged(event.latLng, $(settings['field']), $(settings['lat']), $(settings['lng']));
                });
            });
        },
        field: function(options) {
            var settings = $.extend({
                field: '#field',
                lat: '#lat',
                lng: '#lng'
            }, options);

            $(settings['field']).keypress(function(event) {
                if (event.keyCode == 13) {
                    if (marker != null) marker.setMap(null);
                    var address = $(this).val();
                    geocoder.geocode({'address': address}, function(results, status) {
                        if (status == google.maps.GeocoderStatus.OK) {
                            map.setCenter(results[0].geometry.location);
                            $(settings['lat']).val(map.getCenter().lat());
                            $(settings['lng']).val(map.getCenter().lng());
                            marker = new google.maps.Marker({
                                map: map,
                                position: results[0].geometry.location,
                                draggable: true,
                                animation: null,
                                icon: '/resources/images/GoogleMap_Pink.png'
                            });

                            google.maps.event.addListener(marker, 'dragend', function(event) {
                                map.setCenter(event.latLng);
                                positionChanged(event.latLng, $(settings['field']), $(settings['lat']), $(settings['lng']));
                            });
                        } else {

                        }
                    });
                }
            });
        },
        update: function(options) {
            var settings = $.extend({
                field: '#field',
                lat: '#lat',
                lng: '#lng'
            }, options);

            var address = $(settings['field']).val();
            if (!address || address.length == 0) return;

            if (marker != null) marker.setMap(null);

            geocoder.geocode({'address': address}, function(results, status) {
                if (status == google.maps.GeocoderStatus.OK) {
                    map.setCenter(results[0].geometry.location);
                    $(settings['lat']).val(map.getCenter().lat());
                    $(settings['lng']).val(map.getCenter().lng());
                    marker = new google.maps.Marker({
                        map: map,
                        position: results[0].geometry.location,
                        draggable: true,
                        animation: null,
                        icon: '/resources/images/GoogleMap_Pink.png'
                    });

                    google.maps.event.addListener(marker, 'dragend', function(event) {
                        map.setCenter(event.latLng);
                        positionChanged(event.latLng, $(settings['field']), $(settings['lat']), $(settings['lng']));
                    });
                }
            });
        },
        set_marker:function(options) {
            if (marker != null) marker.setMap(null);

            var settings = $.extend({
                field: '.field', lat: '.lat', lng: '.lng'
            }, options);

            var lat = parseFloat($(settings['lat']).val());
            var lng = parseFloat($(settings['lng']).val());

            if (!isNaN(lat) && !isNaN(lng)) {
                var latlng = new google.maps.LatLng(lat, lng);
                map.setCenter(latlng);
                marker = new google.maps.Marker({
                    map: map,
                    position: latlng,
                    draggable: true,
                    animation: null,
                    icon: '/resources/images/GoogleMap_Pink.png'
                });

                google.maps.event.addListener(marker, 'dragend', function(event) {
                    map.setCenter(event.latLng);
                    positionChanged(event.latLng, $(settings['field']), $(settings['lat']), $(settings['lng']));
                });
            }
        },
        markers:function(data) {
            markers = new Array();
            for (var key in data) {
                var item = data[key];
                var latlng = new google.maps.LatLng(item['latitude'], item['longitude']);
                var mrk = new google.maps.Marker({
                    position: latlng,
                    map: map,
                    icon: '/resources/images/GoogleMap_Gray.png'
                });
                markers[item['id']] = mrk;
            }
        },
        marker_on:function(id) {
            var item = markers[id];
            if (!item) return false;
            item.setIcon('/resources/images/GoogleMap_Pink.png');
            item.setZIndex(google.maps.Marker.MAX_ZINDEX + 1);
        },
        marker_off:function(id) {
            var item = markers[id];
            if (!item) return false;
            item.setIcon('/resources/images/GoogleMap_Gray.png');
            item.setZIndex(google.maps.Marker.MAX_ZINDEX - 1);
        },
        delete_markers:function() {
            for (var key in markers)
                markers[key].setMap(null);
        },
        clear_all:function() {
            for (var key in markers)
                markers[key].setMap(null);
            if (marker != null) marker.setMap(null);
        }
    };

    $.fn.ptmMap = function(method) {
        if (methods[method]) {
            return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
        } else if (typeof method === 'object' || !method) {
            return methods.init.apply(this, arguments);
        } else {
            $.error('Method ' + method + ' does not exist on jQuery.ptmMap');
        }
    };
})(jQuery);