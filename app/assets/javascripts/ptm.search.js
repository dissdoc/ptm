(function($){
    $.fn.ptmSearch = function(options) {
        // Prepare
        var ul = this.find('.combo ul');
        this.find('#search_value').val('1');
        var comboPos = this.find('.combo').position();
        // minus and plus 1 - for border 1px
        ul.css( {'top': comboPos.top + 30 + 1, 'left': ul.offset().left - 90 - 1 })
        ul.hide();

        ul.find('li').bind('selected', function() {
            $(this).css({'background':'#eee'});
        }).bind('unselected', function() {
            $(this).css({'background':'#fff'});
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
            var value = $('#search_value').val();
            $(this).find('li').each(function(index) {
                if ($(this).find('a').attr('value') == value)
                    $(this).trigger('selected');
            });
        });

        // Actions
        $('#search_field').focus(function() {
            ul.trigger('hide');
        })

        this.find('.combo div').hover(
            function() {
                $(this).css({'cursor': 'pointer'});
                $(this).find('p').css({'color': '#999'});
            },
            function() {
                $(this).find('p').css({'color': '#666'});
            }
        );

        this.find('.combo div').click(function() {
            if (!ul.is(':visible')) {
                ul.trigger('change');
                ul.trigger('show');
            }
            else {
                ul.trigger('change');
                ul.trigger('hide');
            }
        });

        ul.find('li').mouseover(function() {
            ul.trigger('clear');
            $(this).trigger('selected').css({'cursor': 'pointer'});
        });

        ul.find('li').mouseout(function() {
            $(this).trigger('unselected');
        });

        ul.find('li').click(function() {
            $('#search_value').val( $(this).find('a').attr('value'));
            ul.trigger('hide');
        });

        ul.mouseout(function() {
            $(this).trigger('change');
        });

        $(this).mouseleave(function(){
            var value = $('#search_value').val();
            ul.trigger('change');
            ul.trigger('hide');
        });
    };
})(jQuery);