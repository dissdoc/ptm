(function($) {
    var width = 0;
    var height = 0;
    var shift = 0;
    var step = 82;
    var center_id = 0;

    var methods = {
        init: function(options) {
            var settings = $.extend({ width: 0 }, options);
            width = settings['width'];
            height = $('ul', this).height();
            return false;
        },
        action: function() {
            var slider = this;

            $('.sl-left', slider).click(function() {
                if (shift >= 0) return false;
                shift += step;
                $('ul', slider).css('top', shift);
                return false;
            });

            $('.sl-right', slider).click(function() {
                shift -= step;
                if ((height + shift) <= 0 ) {
                    shift += step;
                    return false;
                }
                $('ul', slider).css('top', shift);
                return false;
            });
        },
        center: function(options) {
            var settings = $.extend({ id: 0 }, options);
            center_id = settings['id'];
            var index = $('li#th'+settings['id'], this).css('background', '#0f0').index();
            shift = -parseInt(index / 5) * 82;
            $('ul', this).css('top', shift);
        },
        clear_center: function() {
            var index = $('li#th'+center_id, this).css('background', '#fff').index();
        },
        keyboard: function() {
            $('body').keydown(function(evt) {
                if (!$('input').is(':focus') && !$('textarea').is(':focus')) {
                    if (evt.keyCode == 37) {
                        var url = $('li#th' + center_id).prev().find('a').attr('href');
                        if (!url) return;
                        window.location = url;
                    } else if (evt.keyCode == 39) {
                        var url = $('li#th' + center_id).next().find('a').attr('href');
                        if (!url) return;
                        window.location = url;
                    }
                }
            });
        }
    };

    $.fn.ptmThumbs = function(method) {
        if (methods[method]) {
            return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
        } else if (typeof method === 'object' || !method) {
            return methods.init.apply(this, arguments);
        } else {
            $.error('Method ' + method + ' does not exist on jQuery.ptmThumbs');
        }
    };
})(jQuery);