(function($) {
    function updateImg(id, result) {
        var image = $('.window .large-img');
        image.empty();

        var element = getDataById(id, result);
        $("meta[name='description']").attr("content", element.fullDate + ", " + element.address + ". " + element.user);
        $("meta[property='og:url']").attr("content", "http://www.phototimemachine.org/photos/"+id);
        $("meta[property='og:image']").attr("content", "http://www.phototimemachine.org/data/"+id+"/small.png");
        $("meta[property='og:description']").attr("content", element.fullDate + ", " + element.address + ". " + element.user);

        if ($('#photo-date').length > 0) {
            if (element.fullDate && element.fullDate.length > 0)
                $('#photo-date').text(element.fullDate);
            else
                $('#photo-date').remove();
        } else {
            if (element.fullDate && element.fullDate.length > 0) {
                var div = $('<div>', {class: 'text', id: 'photo-date'}).text(element.fullDate);
                $('#photo-user').after(div);
            }
        }

        $('#photo-user a').attr('href', '/user/'+element.userId+'/photos');
        $('#photo-user a').text(element.user);

        var link = '';
        if (element.type && element.type_id > 0) {
            link += '?type='+element.type+'&type_id='+element.type_id;
        }

        $('#photo-name a').attr('href', '/photos/'+element.id+link);
        $('#photo-name a').text(element.name);

        var a = $('<a>', {href: '/photos/'+id+link});
        var img = $('<img>', {src: '/data/'+id+'/medium.png'});
        a.append(img);
        image.append(a);
        $('.window .thumbs-slider').ptmThumbs('clear_center');
        $('.window .thumbs-slider').ptmThumbs('center', {id: id});
    }

    function getDataById(id, result) {
        for (var i in result)
            if (result[i].id == id) return result[i];
        return null;
    }

    function generateThumbs(id, result) {
        var ul = $('.window .thumbs-slider .slider ul');

        for (var item in result) {
            var li = $('<li>', {id: 'th'+result[item].id});
            var img = $('<img>', {src: '/data/'+result[item].id+'/wall.png'});
            var a = $('<a>', {href: '#'});
            a.append(img);
            li.append(a);
            ul.append(li);
        }

        $('.window .thumbs-slider').ptmThumbs('init', {width: 602});
        $('.window .thumbs-slider').ptmThumbs('action');
        $('.window .thumbs-slider').ptmThumbs('center', {id: id});
    }

    var methods = {
        init: function() {
            var wnd = $('<div>', {class: 'modal'});
            var mask = $('<div>', {class: 'mask'});
            var close = $('<img>', {src: '/resources/images/delete.png', class: 'close'});

            mask.append(close);
            wnd.append(mask);
            this.append(wnd);
        },
        windowInit: function() {
            if ($('.window').length <= 0) {
                var wnd = $('<div>', {class: 'window'});
                $('.modal', this).append(wnd);
            }
        },
        show: function(options) {
            var settings = $.extend({
                dialog: '#dialog'
            }, options);

            $('.window').css({top: (window.innerHeight - $(settings['dialog']).height()) / 2,
                              left: (window.innerWidth - $(settings['dialog']).width()) / 2});
            $(settings['dialog']).show();
            if ($('.window '+(settings['dialog'])).length <= 0)
                $('.window').append($(settings['dialog']));
            $('.mask').fadeIn('slow');
            $('.window').fadeIn('1000');
        },
        showimg: function(file) {
            var wnd = $('<div>', {class: 'window'});
            var img = $('<div>', {class: 'large-img'});

            var picture = new Image();
            img.append(picture);
            picture.src = window.URL.createObjectURL(file);
            picture.onload = function() {
                window.URL.revokeObjectURL(this.src);
                $('.window').css('top', (window.innerHeight-picture.height)/2);
                $('.window').css('left', (window.innerWidth-picture.width)/2);
            }

            wnd.append(img);
            $('.modal', this).append(wnd);

            $('.mask').fadeIn('slow');
            $('.window').fadeIn('1000');
        },
        litemode: function(options, result) {
            var settings = $.extend({id: 0, type: '', type_id: -1}, options);
            var modal = $('.modal', this);
            var id = settings['id'];

            var element = getDataById(id, result);
            $("meta[name='description']").attr("content", element.fullDate + ", " + element.address + ". " + element.user);
            $("meta[property='og:url']").attr("content", "http://www.phototimemachine.org/photos/"+id);
            $("meta[property='og:image']").attr("content", "http://www.phototimemachine.org/data/"+id+"/small.png");
            $("meta[property='og:description']").attr("content", element.fullDate + ", " + element.address + ". " + element.user);

            var data = '';
            if (settings['type_id'] > 0) data = "?type="+settings['type']+"&type_id="+settings['type_id'];

            $.ajax({
                type: "POST",
                dataType: 'html',
                async: false,
                url: '/photos/'+settings['id']+'/modal',
                data: {params: data},
                success: function(html) {
                    $('.window').append(html);
                }
            });

            $('.window').fadeTo(0, 0, function() {
                generateThumbs(settings['id'], result);
                $('.large-img img').load(function() {
                    $('.window').css({
                        top: (window.innerHeight - $('.window').height()) / 2,
                        left: (window.innerWidth - $('.window').width()) / 2
                    });
                })
            });
            $('.mask').fadeIn('slow', function() {
                $('.window').fadeTo(0, 1);
            });

            $(document).keydown(function(evt) {
                if (evt.keyCode == 37) {
                    id = $('li#th'+id).prev().attr('id').substr(2);
                    updateImg(id, result);
                } else if (evt.keyCode == 39) {
                    id = $('li#th'+id).next().attr('id').substr(2);
                    updateImg(id, result);
                }
            });

            $('.window .thumbs-slider .slider ul').find('li').each(function() {
                $(this).click(function() {
                    id = $(this).attr('id').substr(2);
                    updateImg(id, result);
                });
            });

            if(!window.pluso){
                pluso={version:'0.9.1',url:'http://share.pluso.ru/'};
                h=document.getElementsByTagName('head')[0];
                l=document.createElement('link');
                l.href=pluso.url+'pluso.css';
                l.type='text/css';
                l.rel='stylesheet';
                s=document.createElement('script');
                s.src=pluso.url+'pluso.js';
                s.charset='UTF-8';
                h.appendChild(l);
                h.appendChild(s)
            }
        },
        destroy: function(options) {
            var settings = $.extend({dialog: '#dialog', parent: '#parent'}, options);

            $('.mask').click(function() {
                $(this).hide();
                $(settings['parent']).append($(settings['dialog']).clone().hide());
                $('.window').remove();
            });

            $('.close').click(function() {
                $('.mask').hide();
                $(settings['parent']).append($(settings['dialog']).clone().hide());
                $('.window').remove();
            });
        },
        destroy2: function(options) {
            var settings = $.extend({dialog: '#dialog', parent: '#parent'}, options);

            $('.mask').click(function() {
                $(this).hide();
                $(settings['dialog']).hide();
                $('.window').hide();
            });

            $('.close').click(function() {
                $('.mask').hide();
                $(settings['dialog']).hide()
                $('.window').hide();
            });
        },
        close: function(options) {
            var settings = $.extend({dialog: '#dialog', parent: '#parent'}, options);
            $('.mask').hide();
            $(settings['dialog']).hide();
            $('.window').hide();
        }
    };

    $.fn.ptmModal = function(method) {
        if (methods[method]) {
            return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
        } else if (typeof method === 'object' || !method) {
            return methods.init.apply(this, arguments);
        } else {
            $.error('Method ' + method + ' does not exist on jQuery.ptmModal');
        }
    };
})(jQuery);