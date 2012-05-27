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
//= require_tree .

$(document).ready(function(){
    // create map
    initialize();
//    var latlng = new google.maps.LatLng(-34.397, 150.644);
//    var myOptions = {
//        zoom: 8,
//        center: latlng,
//        mapTypeId: google.maps.MapTypeId.ROADMAP
//    };
//    var map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);
    // end of create map

    $('#dropdown li.headlink').hover(
        function() { $('ul', this).css('display', 'block'); },
        function() { $('ul', this).css('display', 'none'); });

    $('.tab:not(:first)').hide();
    $('.tab:first').show();

    $('.htabs a').click(function() {
        stringref = jQuery(this).attr("href").split('#')[1];
        $('.tab:not(#'+stringref+')').hide();

        if (jQuery.browser.msie && jQuery.browser.version.substr(0,3) == "6.0")
            $('.tab#' + stringref).show();
        else
            $('.tab#' + stringref).fadeIn();

        return false;
    });
});

function initialize() {
    this.map = null;
    this.geocoder = null;

    if (this.geocoder == null)
        this.geocoder = new google.maps.Geocoder();

    var lat = $('#lat').attr('value');
    var lng = $('#lng').attr('value');
    var latlng = new google.maps.LatLng(lat, lng);
    var myOptions = {
        zoom: 8,
        center: latlng,
        mapTypeId:google.maps.MapTypeId.ROADMAP
    }
    this.map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);
}

function getZoom() {
    alert("thats all");
}

function setMapToCity(map, geocoder) {
    var address = $('#address').attr('value');
    geocoder.geocode({ 'address': address }, function (results, status) {
        if (status == google.maps.GeocoderStatus.OK) {
            map.setCenter(results[0].geometry.location);
            //установить Zoom таким образом, чтобы город был показан весь
            map.setZoom(_this.getZoom(results[0].geometry.viewport));
            //и поставить маркет для отметки адреса
            addMarker();
        } else {
            alert("Пошло что-то не так, потому что: " + status);
        }
    });
}