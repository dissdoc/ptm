(function($){
    $.fn.ptmSearch = function(options) {
        var defaults = {
                cl: 'inactive',
                text: this.val()
            }, opts = $.extend(defaults, options);

        this.addClass(opts['cl']);
        this.val(opts['text']);

        this.focus(function() {
           if ($(this).val() == opts['text'])
               $(this).val('');
            $(this).removeClass(opts['cl']);
        });

        this.blur(function() {
            if ($(this).val() == '') {
                $(this).addClass(opts['cl']);
                $(this).val(opts['text']);
            }
        });
    };
})(jQuery);