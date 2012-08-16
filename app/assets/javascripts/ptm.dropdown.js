(function($) {
    $.fn.ptmDropdown = function(options) {
        var ul_menu = this.find('ul');
        var mlink = this.find('a');
        ul_menu.css({'left': mlink.position().left + 160, 'top': mlink.position().top + 30});
        ul_menu.hide();

        mlink.click(function() {
            if (!ul_menu.is(':visible'))
                ul_menu.show();
            else
                ul_menu.hide();
        });

        this.find('ul li').click(function() {
            ul_menu.hide();
        })
    };
})(jQuery);