// This is a manifest file that'll be compiled into application.js, which will include all the files
// listed below.
//
// Any JavaScript/Coffee file within this directory, lib/assets/javascripts, vendor/assets/javascripts,
// or vendor/assets/javascripts of plugins, if any, can be referenced here using a relative path.
//
// It's not advisable to add code directly here, but if you do, it'll appear at the bottom of the
// the compiled file.
//
// WARNING: THE FIRST BLANK LINE MARKS THE END OF WHAT'S TO BE PROCESSED, ANY BLANK LINE SHOULD
// GO AFTER THE REQUIRES BELOW.
//
//= require jquery
//= require jquery_ujs
//= require foundation
//= require_tree .



this.marker = null;
this.map = null;
this.geocoder = null;

$(document).ready(function(){
    initialize();
});

function initialize() {
    if (this.geocoder == null)
        this.geocoder = new google.maps.Geocoder();

    var lat = $('#lat').attr('value');
    var lng = $('#lng').attr('value');
    var latlng = new google.maps.LatLng(lat, lng);

    var myOptions = {
        zoom: 8,
        center: latlng,
        mapTypeId:google.maps.MapTypeId.ROADMAP,
        streetViewControl: false,
        mapTypeControl: false
    }

    this.map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);

    this.marker = new google.maps.Marker({
        position: latlng,
        draggable:true,
        animation: google.maps.Animation.DROP,
        map: this.map
    });
    google.maps.event.addListener(this.marker, 'click', toggleBounce);
    google.maps.event.addListener(this.marker, 'dragend', markerPositionChanged);
}

function toggleBounce() {
    if (this.marker.getAnimation() != null) {
        this.marker.setAnimation(null);
    } else {
        this.marker.setAnimation(google.maps.Animation.BOUNCE);
    }
}

function markerPositionChanged() {
    var latlng = marker.getPosition();
    geocoder.geocode({ 'latLng': latlng, 'language' : 'ru' }, function (results, status) {
        if (status == google.maps.GeocoderStatus.OK) {
            this.map.setCenter(results[0].geometry.location);
            setAddress(results[0]);
        }
    });
}

function setAddress(item) {
    var new_address = composeAddress(item);
    $('#address').val(new_address);
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