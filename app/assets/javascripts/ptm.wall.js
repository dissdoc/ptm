(function($) {
    $.fn.ptmWall = function(options) {
        this.find('li').each(function() {
            var image = $(this).find('img');
            var shift = (160 - image.width()) / 2;
            image.css({'position': 'relative', 'left': shift})
        });

        this.find('li').hover(
            function() {
                var pos = $(this).position();

                var left = 0;
                if (pos.left == 0) left = 0;
                else if (pos.left == 1120) left = 1065;
                else left = pos.left - 25;

                $(this).find('div').css({'top': pos.top - 25, 'left': left});
                $(this).find('div').animate({opacity: 'show'}, 'fast');
                var image = $(this).find('div img');
                var shift = ($(this).find('div').width() - image.width()) / 2;
                image.css({'position': 'relative', 'left': shift})
            },function() {
                $(this).find('div').animate({opacity: 'hide'}, 'fast');
            }
        );
    };
})(jQuery);