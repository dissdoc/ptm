(function($) {
    var tmpl = null;
    var count = 0;
    var current_page = 0;
    var result = null;
    var SHIFT = 0;
    var mouseact = false;
    var url = '';
    var page = '';
    var type_id = -1;
    var wrapper = null;

    function fillWall(items) {
        wrapper.empty();
        $('#map').ptmMap('delete_markers');
        var data = new Array();

        for (var item in items) {
            if (items[item].address && items[item].address.length > 0) {
                var marker = new Array();
                marker['id'] = items[item].id;
                marker['latitude'] = items[item].latitude;
                marker['longitude'] = items[item].longitude;
                data.push(marker);
            }

            if (type_id > 0) {
                items[item]['type'] = page;
                items[item]['type_id'] = type_id;
            }

            var translate = $.nano(tmpl, items[item]);
            wrapper.append(translate);
            $('#'+items[item].id).hover(function() {
                var info = $('.info', this);
                info.css('top', ($(this).offset().top + $(this).height() - 3 - info.height()));
                info.show();
            }, function() {
                $('.info', this).hide();
            });

            $('#' + items[item].id + ' img').click(function() {
                $('body').ptmModal('windowInit');
                var id = $(this).closest('li').attr('id');
                $('body').ptmModal('litemode', {
                    id: id, type: page, type_id: type_id
                }, result);
                $('body').ptmModal('destroy');
                return false;
            });
        }

        $('#map').ptmMap('markers', data);
        wrapper.ptmWall('init', {map: '#map'});
    }

    function generateWall() {
        $.post(
            url,
            {
                sortby: $('#by_sorting input:radio[name=sorting]:checked').val(),
                minD: $('#dateAction').val().length > 0 ? $('#minD').val() : '',
                maxD: $('#dateAction').val().length > 0 ? $('#maxD').val() : '',
                ne_lng: $('#mapAction').val().length > 0 ? $('#ne_lng').val() : '',
                ne_lat: $('#mapAction').val().length > 0 ? $('#ne_lat').val() : '',
                sw_lng: $('#mapAction').val().length > 0 ? $('#sw_lng').val() : '',
                sw_lat: $('#mapAction').val().length > 0 ? $('#sw_lat').val() : '',
                search_field: $('#search_field').val(),
                search_value: $('#search_value').val(),
                owner_id: $('#user').val(),
                page: page,
                type_id: type_id
            },
            function(response) {
                result = response;
                count = response.length;
                current_page = 0;
                $('#results').text(count);
                fillWall(result.slice(current_page, SHIFT));
            }
        )
    }

    var methods = {
        init: function(options) {
            var settings = $.extend({
                shift: 0,
                url: '/search/photos',
                page: 'main',
                type_id: -1,
                template: '/test.tmpl',
                map_w: '0px',
                map_h: '0px'
            }, options);

            SHIFT = settings['shift'];

            tmpl =  $.ajax({
                type: "GET",
                url: settings['template'],
                async: false
            }).responseText;

            $('#map').ptmMap('init', {
                width: settings['map_w'],
                height: settings['map_h']});
            $('#map').ptmMap('bounds', {
                ne_lat: '#ne_lat',
                ne_lng: '#ne_lng',
                sw_lat: '#sw_lat',
                sw_lng: '#sw_lng',
                mapAction: '#mapAction'}, generateWall);
            $('body').ptmModal('init');
            url = settings['url'];
            page = settings['page'];
            type_id = settings['type_id'];
            wrapper = this;

            generateWall();
        },
        slider_event: function() {
            generateWall();
        },
        action: function() {
            $('#search_field').keydown(function(e) {
                if (e.which == 13) {
                    generateWall();
                }
            })

            $('#by_sorting input:radio[name=sorting]').click(function() {
                generateWall();
            });

            $('#prevbtn').click(function() {
                fillWall(result.slice(0, SHIFT));
                current_page = 0;
            });

            $('#nextbtn').click(function() {
                var c = parseInt(result.length / SHIFT);
                var ostatok = result.length - c * SHIFT;
                if (ostatok > 0) {
                    fillWall(result.slice(result.length - ostatok, result.length));
                    current_page = c + 1;
                } else {
                    fillWall(result.slice(ostatok * SHIFT, result.length));
                    current_page = c;
                }
            });

            $('#playbtn').click(function() {
                var pages = parseInt(result.length / SHIFT);
                if (result.length % SHIFT > 0) pages += 1;
                if (current_page + 1 >= pages) return;
                current_page += 1;
                var shift = (current_page + 1) * SHIFT > result.length ? result.length : (current_page + 1) * SHIFT;
                fillWall(result.slice(current_page * SHIFT, shift));
            });

            $('#forwardbtn').click(function() {
                var pages = parseInt(result.length / SHIFT);
                if (result.length % SHIFT > 0) pages += 1;
                if (current_page - 1 < 0) return;
                current_page -= 1;
                var shift = (current_page + 1) * SHIFT;
                fillWall(result.slice(current_page * SHIFT, shift));
            });

            $('body').keydown(function(e) {
                if (!$('.mask') || !$('.mask').is(":visible")) {
                    if (e.which == 39) {
                        var pages = parseInt(result.length / SHIFT);
                        if (result.length % SHIFT > 0) pages += 1;
                        if ((current_page + 1) >= pages) return;
                        current_page += 1;
                        var shift = (current_page + 1) * SHIFT > result.length ? result.length : (current_page + 1) * SHIFT;
                        fillWall(result.slice(current_page * SHIFT, shift));
                    } else if (e.which == 37) {
                        if ((current_page - 1) < 0) return;
                        current_page -= 1;
                        fillWall(result.slice(current_page * SHIFT, (current_page + 1) * SHIFT));
                    }
                }
            });
        }
    };


    $.fn.ptmSearch = function(method) {
        if (methods[method]) {
            return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
        } else if (typeof method === 'object' || !method) {
            return methods.init.apply(this, arguments);
        } else {
            $.error('Method ' + method + ' does not exist on jQuery.ptmModal');
        }
    };
})(jQuery);