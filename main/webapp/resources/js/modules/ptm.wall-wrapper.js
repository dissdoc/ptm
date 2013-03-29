(function($) {
    var methods = {
        init: function(options) {
            var settings = $.extend({map: '.map'}, options);

            this.find('li').each(function() {
                var id = $(this).attr('id');
                $(this).hover(function() {
                    $(settings['map']).ptmMap('marker_on', id);
                    return false;
                }, function() {
                    $(settings['map']).ptmMap('marker_off', id);
                    return false;
                });
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