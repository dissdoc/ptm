(function($){
    $.fn.ptmSearch = function(options) {
        var ul = this.find('.combo ul');
        this.find('#search_value').val('1');

        // Prepare for ul position
        var comboPos = this.find('.combo').position();
        // minus and plus 1 - for border 1px
        ul.css( {'top': comboPos.top + 30 + 1, 'left': ul.offset().left - 90 - 1 })
        ul.hide();

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
            if (!ul.is(':visible'))
                ul.animate({opacity: 'show'}, 'fast');
            else
                ul.animate({opacity: 'hide'}, 'fast');
        });

        this.find('.combo ul li').mouseover(function() {
            $(this).css({'background':'#eee', 'cursor': 'pointer'});
        });

        this.find('.combo ul li').mouseout(function() {
           $(this).css({'background': '#fff'});
        });

        this.find('.combo ul li').click(function() {
            $('#search_value').val( $(this).find('a').attr('value'));
            ul.animate({opacity: 'hide'}, 'fast');
        });

        $(this).mouseleave(function(){
            ul.animate({opacity: 'hide'}, 'fast');
        });
    };
})(jQuery);