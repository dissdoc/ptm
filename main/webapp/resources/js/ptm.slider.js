(function($) {
    var methods = {
        init: function(options) {
            var settings = $.extend({
                slider: '.slider',
                data: '.data',
                min: '.min',
                max: '.max',
                isaction: '.action'
            }, options);

            init_datelist($(settings['min']), $(settings['max']));

            $(settings['slider']).slider({
                min: 0,
                max: 210,
                values: [10, 200],
                range: true,
                stop: function(event, ui) {
                    if (ui.values[0] < 10) {
                        var val = $(settings['min']).val() - 50;
                        if (val < 1826) val = 1826;
                        $(settings['min']).val(val);
                        init_datelist($(settings['min']), $(settings['max']));
                    } else if (ui.values[1] > 200) {
                        var val = $(settings['max']).val() + 50;
                        if (val > 1997) val = 1997;
                        $(settings['max']).val(val);
                        init_datelist($(settings['min']), $(settings['max']));
                    } else {
                        init_date(ui.values[0] - 10, ui.values[1] - 10);
                    }

                    $(settings['slider']).slider('values', 0, 10);
                    $(settings['slider']).slider('values', 1, 200);

                    if ($('#photos').length)
                        $('#photos').ptmSearch('slider_event');
                    else if ($('#wall-wrapper').length)
                        $('#wall-wrapper').ptmSearch('slider_event');
                },
                slide: function(event, ui) {
                    $(settings['isaction']).val("true");
                }
            });

            function init_datelist(start, end) {
                $(settings['data']).find('li').each(function() {
                    $(this).remove();
                });

                start = parseInt(start.val());
                end = parseInt(end.val());

                $(settings['data']).append($('<li>', {text: start}));

                var summary = parseInt((end - start) / 17);
                if (summary > 0) {
                    for (i = 0; i < 17; i++) {
                        start += summary;
                        $(settings['data']).append($('<li>', {text: start}));
                    }
                } else {
                    var l = end - start;
                    if (l <= 1) {
                        var li = $('<li>', {html: '&nbsp;'}).css('width', 920);
                        $(settings['data']).append(li);
                    } else {
                        var diap = end - start - 1;
                        var width = parseInt(920 / diap);
                        for (i = 0; i < diap; i++) {
                            start += 1;
                            var li = $('<li>', {text: start}).css('width', width);
                            $(settings['data']).append(li);
                        }
                    }
                }

                $(settings['data']).append($('<li>', {text: end}));
            }

            function year(y, dep, data) {
                return Math.floor(y / dep * 0.9 + data);
            }

            function init_date(min, max) {
                var start = parseInt($(settings['min']).val());
                var end = parseInt($(settings['max']).val());

                if (max < 190) {
                    var val = start + ((end - start) * max / 190);
                    $(settings['max']).val(Math.floor(val));
                } else if (min > 0) {
                    var val = start + ((end - start) * min / 190);
                    $(settings['min']).val(Math.floor(val));
                }

                init_datelist($(settings['min']), $(settings['max']));
            }
        }
    };

    $.fn.ptmSlider = function(method) {
        if (methods[method]) {
            return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
        } else if (typeof method === 'object' || !method) {
            return methods.init.apply(this, arguments);
        } else {
            $.error('Method ' + method + ' does not exist on jQuery.ptmSlider');
        }
    };
})(jQuery);