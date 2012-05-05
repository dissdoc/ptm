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