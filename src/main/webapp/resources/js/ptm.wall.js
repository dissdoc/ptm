(function($) {
    var methods = {
        init: function(options) {
            var settings = $.extend({map: '.map'}, options);

            this.find('li').each(function() {
                var id = $(this).attr('id');
                $('div.small', this).hover(function() {
                    var preview = $('li#' + id).find('div.preview');
                    var left = $(this).position().left - (preview.width() - 160)/2;
                    var top = $(this).position().top - (preview.height() - 128)/2;

                    var theImage = new Image();
                    theImage.src = preview.find('img').attr("src");
                    var width = theImage.width;
                    preview.find('p').each(function() {
                        $(this).css('width', width+"px");
                    })
                    preview.css({top: top+"px", left: left+"px"});
                    preview.animate({opacity: 'show'}, 'fast');
                    $(settings['map']).ptmMap('marker_on', id);
                    return false;
                }, function() {
                    return false;
                });
            });

            this.find('div.preview').mouseleave(function() {
                $(this).hide();
                var parent = $(this).parent().attr('id');
                $(settings['map']).ptmMap('marker_off', parent);
                return false;
            });

        }
    };

    $.fn.ptmWall = function(method) {
        if (methods[method]) {
            return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
        } else if (typeof method === 'object' || !method) {
            return methods.init.apply(this, arguments);
        } else {
            $.error('Method ' + method + ' does not exist on jQuery.ptmWall');
        }
    };
})(jQuery);