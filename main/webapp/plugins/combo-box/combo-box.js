(function($) {
    var methods = {
        init: function(options) {
            var settings = $.extend({
                field: '#field',
                value: '#value'
            }, options);

            // Prepare components
            var ul = this.find('.combo ul');
            var div = this.find('.combo div');
            $(settings['value']).val('1');

            ul.css('left', div.position().left + div.width() - ul.width() - 3);
            ul.css('top', div.position().top + div.height() + 4);

            ul.find('li').bind('selected', function() {
                $(this).css('background', '#f4efff');
            }).bind('unselected', function() {
                $(this).css('background', '#fff');
            });

            ul.bind('clear', function() {
                $(this).find('li').each(function(index) {
                    $(this).trigger('unselected');
                });
            }).bind('hide', function() {
                    $(this).animate({opacity: 'hide'}, 'fast');
                }).bind('show', function() {
                    $(this).animate({opacity: 'show'}, 'fast');
                }).bind('change', function() {
                    var value = $(settings['value']).val();
                    $('li', this).each(function(index) {
                        if ($('a', this).attr('value') == value)
                            $(this).trigger('selected');
                    });
                });

            // Actions components
            $(settings['field']).focus(function() {
                ul.trigger('hide');
            });

            this.find('.combo div').click(function() {
                if (!ul.is(':visible')) {
                    ul.trigger('change');
                    ul.trigger('show');
                } else {
                    ul.trigger('change');
                    ul.trigger('hide');
                }
            });

            ul.find('li').mouseover(function() {
                ul.trigger('clear');
                $(this).trigger('selected');
            }).mouseout(function() {
                    $(this).trigger('unselected');
                }).click(function() {
                    $(settings['value']).val($(this).find('a').attr('value'));
                    ul.trigger('hide');
                });

            ul.mouseout(function() {
                $(this).trigger('change');
            });

            $(this).mouseleave(function() {
                ul.trigger('change');
                ul.trigger('hide');
            });
        }
    };

    $.fn.ptmDropdownField = function(method) {
        if (methods[method]) {
            return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
        } else if (typeof method === 'object' || !method) {
            return methods.init.apply(this, arguments);
        } else {
            $.error('Method ' + method + ' does not exist on jQuery.ptmDropdownField');
        }
    };
})(jQuery);