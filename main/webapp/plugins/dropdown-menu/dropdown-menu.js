(function($) {
    var methods = {
        action: function() {
            this.find('ul.dropdown li.headlink').hover(function() {
                $('ul', this).css('display', 'block');

            }, function() {
                $('ul', this).css('display', 'none');
            });
        },
        init: function() {
            this.find('ul.dropdown li.headlink').each(function() {
                var shift_top = $(this).offset().top + 18;
                // Смещение вниз
                $('ul', this).css('top', shift_top + 12);

                // Смещение влево
                var shift_left = $(this).offset().left + $(this).width();
                var cls = $('ul', this).attr('class');
                var width = (cls && cls === 'fix') ? $('ul', this).width() - 68 : $('ul', this).width() - 10;
                $('ul', this).css('left', shift_left - width);
            });

            this.find('ul.dropdown li').each(function() {
                if (!$(this).hasClass('noaction')) {
                    $(this).click(function() {
                        var href = $(this).find("a").attr("href");
                        if (href != "#") window.location = href;
                        return false;
                    });
                }
            });
        }
    };

    $.fn.ptmDropdownMenu = function(method) {
        if (methods[method]) {
            return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
        } else if (typeof method === 'object' || !method) {
            return methods.init.apply(this, arguments);
        } else {
            $.error('Method ' + method + ' does not exist on jQuery.ptmDropdownMenu');
        }
    };
})(jQuery);
