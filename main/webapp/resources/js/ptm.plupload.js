$(function() {
    var limit_files = 0;
    var max_files = 16;

    window.URL = window.URL || window.webkitURL;

    var uploader = new plupload.Uploader({
        runtimes : 'html5',
        browse_button : 'pickfiles',
        container : 'container',
        max_file_size : '10mb',
        max_file_count  : 20,
        url : "/photos/upload",
        multipart: true,
        multipart_params: {
            '_http_accept': 'application/javascript'
        },
        filters : [
            {title : "Image files", extensions : "jpg,gif,png,tiff,tif,jpeg"}
        ]
    });

    fileList = new Array();
    address = null;
    var selectedList = new Array();

    $('#uploadfiles').click(function(e) {
        var flag = true;
        for (var id in fileList) {
            var item = fileList[id];

            $('li#'+id+' .noterror').remove();
            $('li#'+id+' .error').remove();

            var errors = 0;
            if (!isField(item['tags'])) errors += 1;
            if (!isField(item['address'])) errors += 1;
            if ((!isField(item['year']) && !isField(item['year2']) && !isField(item['month']) &&
                !isField(item['month2']) && !isField(item['day']) && !isField(item['day2']))) errors += 1;

            var message = "";
            switch(errors) {
                case 1:
                    message = "One more tag please";
                    break;
                case 2:
                    message = "Two more tags please";
                    break;
                case 3:
                    message = "At least 3 tags please";
                    break;
            }

            if (errors > 0) {
                flag = false;
                $('li#'+id).css('border', '2px solid #fff');
                var error = $('<div>', {class: 'error'}).text(message);
                $('li#'+id+' .thumb').append(error);
            } else {
                $('li#'+id).css('border', '2px solid #fff');
                var sd = $('<div>', {class: 'noterror'}).text('Ready to be uploaded!');
                $('li#'+id+' .thumb').append(sd);
            }
        }
        if (!flag) return flag;

        uploader.start();
        e.preventDefault();
    });

    uploader.init();

    uploader.bind('FilesAdded', function(up, files) {
        if ((limit_files + files.length) > max_files) return;
        limit_files += files.length;

        $('#container .panel-right .buttons').prepend(' ');
        $('#container .panel-right .buttons').prepend($('#pickfiles').text('Add more'));
        $('#container .prepare-text').text('');
        $('#container .tabpanel').css('display', 'block');
        canEdit();
        $('#map').ptmMap('init', {width: '419px', height: '218px'});
        $('#map').ptmMap('marker', {field: '#photo_place', lat: '#lat', lng: '#lng'});
        $('#map').ptmMap('field', {field: '#photo_place', lat: '#lat', lng: '#lng'});

        var ul = $('#filelist');

        $.each(files, function(i, file) {
            fileList[file.id] = { 'name': file.name, 'selected': false };
            var li = $('<li>', {id: file.id});
            var div = $('<div>', {class: 'thumb'});
            var a_zoom = $('<a>', {class: 'zoom', href: '#'});
            var a_delete = $('<a>', {class: 'delete', href: '#'});

            var img = null;
            if (file.name.substr(-4).valueOf() == '.tif'.valueOf() ||
                file.name.substr(-5).valueOf() == '.tiff'.valueOf()) {
                img = $('<span>').text(file.name)
                div.append(img);
            } else {
                img = new Image();
                img.setAttribute("class", "thumb-crop");
                img.setAttribute("className", "thumb-crop"); // for ie
                div.append(img);
                img.src = window.URL.createObjectURL(file.nativeFile);
                img.onload = function() {
                    window.URL.revokeObjectURL(this.src);
                }
            }

            a_zoom.append($('<img>', {src: '/resources/images/zoom.png'}));
            a_zoom.click(function(e) {
                $('body').ptmModal('showimg', file.nativeFile);
                $('body').ptmModal('destroy');
                return false;
            });

            a_delete.append($('<img>', {src: '/resources/images/delete.png'}));
            a_delete.click(function(e) {
                $('#' + file.id).remove();
                delete fileList[file.id];
                e.preventDefault();
                up.removeFile(file);
                limit_files--;
                canEdit();
                return false;
            });

            var panel = $('<div>', {class: 'panel'});
            panel.append(a_zoom).append(a_delete);

            div.append(panel);
            li.append(div);
            ul.prepend(li);

            li.find('.thumb-crop').load(function() {
                var padding = (157 - $(this).width()) / 2;
                $(this).css('padding-left', padding);
            });

            li.hover(function() {
                $('.panel', this).show();
                $('.panel', this).css('top', $(this).position().top + 2);
            }, function() {
                $('.panel', this).hide();
            });

            li.click(function(e) {
                var id = $(this).attr('id');

                if (e.ctrlKey || e.metaKey) {
                    if (fileList[id]['selected']) {
                        $(this).css('border', '2px solid #fff');
                        fileList[id]['selected'] = false;
                    } else {
                        $(this).css('border', '2px solid #f9bcee');
                        fileList[id]['selected'] = true;
                    }
                } else {
                    for (var key in fileList) {
                        $('li#' + key).css('border', '2px solid #fff');
                        fileList[key]['selected'] = false;
                    }

                    $(this).css('border', '2px solid #f9bcee');
                    fileList[id]['selected'] = true;
                }
                setSelectedList();
                fillFields();
                canEdit();
            });
        });

        $(document).bind('keydown', 'ctrl+a', function() {

        });

        up.refresh();
    });

    uploader.bind('UploadProgress', function(up, file) {

    });

    uploader.bind('Error', function(up, err) {
        $('#' + file.id).css('border', '2px solid red');
        up.refresh();
    });

    uploader.bind('FileUploaded', function(up, file) {
        $('#' + file.id).css('border', '2px solid green');

        if(uploader.total.uploaded == uploader.files.length)
            window.location = "/";
    });

    uploader.bind('BeforeUpload', function(up, file) {
        $.extend(up.settings.multipart_params, {
            original: file.name,
            name: fileList[file.id]['name'],
            author: fileList[file.id]['author'],
            description: fileList[file.id]['description'],
            address: fileList[file.id]['address'],
            longitude: fileList[file.id]['longitude'],
            latitude: fileList[file.id]['latitude'],
            tags: fileList[file.id]['tags'],
            marked: fileList[file.id]['marked'],
            license: fileList[file.id]['license'],
            privacy: fileList[file.id]['privacy'],
            year: fileList[file.id]['year'],
            month: fileList[file.id]['month'],
            day: fileList[file.id]['day'],
            year2: fileList[file.id]['year2'],
            month2: fileList[file.id]['month2'],
            day2: fileList[file.id]['day2']
        });
    });

    function isField(param) {
        return (param && param.length > 0 && param != "year" && param != "month" && param != "day") ? true : false;
    }

    function isSelectItem() {
        return selectedList.length > 0;
    }

    function canEdit() {
        $('#container .panel-left').find("input,select,textarea,button").prop("disabled",!isSelectItem());
    }

    function fillData(param, value) {
        if (!prepareFill(value)) return;
        if (!value || 0 === value.length) return;

        var tags = value.split(/,\s*/);
        for (var i in selectedList) {
            var item = fileList[selectedList[i]];
            if (param === 'tags') {
                if (item['tags'] && item['tags'].length > 0) {
                    var current_tags = item['tags'].split(/,\s*/);
                    for (var element in tags)
                        if ($.inArray(tags[element], current_tags) == -1) current_tags.push(tags[element]);
                    item['tags'] = current_tags.join(', ');
                } else {
                    item['tags'] = value;
                }
            } else if (param === 'year' || param === 'year2' || param === 'month' || param === 'month2' ||
                param === 'day' || param === 'day2') {
                if (selectedList.length > 1) {
                    if (value != 'year' && value != 'month' && value != 0)
                        item[param] = value;
                } else item[param] = value;
            } else item[param] = value;
        }
    }

    function setSelectedList() {
        selectedList = new Array();
        for (var id in fileList)
            if (fileList[id]['selected'] == true)
                selectedList.push(id);
    }

    function fillFields() {
        if (selectedList.length == 1) {
            var item = fileList[selectedList[0]];
            $('#photo_name').val(item['name']);
            $('#photo_author').val(item['author']);
            $('#photo_describe').val(item['description']);

            $('#photo_place').val(item['address']);
            $('#lat').val(item['latitude']);
            $('#lng').val(item['longitude']);
            $('#map').ptmMap('set_marker', {field: $('#photo_place'), lat: $('#lat'), lng: $('#lng')});

            if (item['tags'] && item['tags'].length > 0)
                $('#photo_tags').val(item['tags']);
            else
                $('#photo_tags').val('');

            if (item['marked'] && item['marked'].length > 0)
                $('input:radio[name=marked][value='+item['marked']+']').attr('checked', true);
            else
                $('input:radio[name=marked][value=0]').attr('checked', true);

            if (item['license'] && item['license'].length > 0)
                $('input:radio[name=license][value='+item['license']+']').attr('checked', true);
            else
                $('input:radio[name=license][value=0]').attr('checked', true);

            if (item['privacy'] && item['privacy'].length > 0)
                $('input:radio[name=privacy][value='+item['privacy']+']').attr('checked', true);
            else
                $('input:radio[name=privacy][value=0]').attr('checked', true);

            if (!item['year']) item['year'] = 'year';
            $('#birthYear option').filter(function() {
                return $(this).text() == item['year'];
            }).attr('selected', true);

            if (!item['month']) item['month'] = 'month';
            $('#birthMonth option').filter(function() {
                return $(this).val() == item['month'];
            }).attr('selected', true);

            if (!item['day']) item['day'] = 'day';
            $('#birthDay option').filter(function() {
                return $(this).text() == item['day'];
            }).attr('selected', true);

            if (!item['year2']) item['year2'] = 'year';
            $('#birthYear2 option').filter(function() {
                return $(this).text() == item['year2'];
            }).attr('selected', true);

            if (!item['month2']) item['month2'] = 'month';
            $('#birthMonth2 option').filter(function() {
                return $(this).val() == item['month2'];
            }).attr('selected', true);

            if (!item['day2']) item['day2'] = 'day';
            $('#birthDay2 option').filter(function() {
                return $(this).text() == item['day2'];
            }).attr('selected', true);
        } else {
            $('#photo_name').val('');
            $('#photo_author').val('');
            $('#photo_describe').val('');

            $('#photo_place').val('');
            $('#lat').val('');
            $('#lng').val('');
            $('#map').ptmMap('set_marker', {field: $('#photo_place'), lat: $('#lat'), lng: $('#lng')});

            $('#photo_tags').val('');

            $('input[name=marked]:checked').each(function() {
                $(this).prop('checked', false);
            });

            $('input[name=license]:checked').each(function() {
                $(this).prop('checked', false);
            });

            $('input[name=privacy]:checked').each(function() {
                $(this).prop('checked', false);
            });

            $('#birthYear option').filter(function() {
                return $(this).text() == 'year';
            }).attr('selected', true);

            $('#birthMonth option').filter(function() {
                return $(this).text() == 'month';
            }).attr('selected', true);

            $('#birthDay option').filter(function() {
                return $(this).text() == 'day';
            }).attr('selected', true);

            $('#birthYear2 option').filter(function() {
                return $(this).text() == 'year';
            }).attr('selected', true);

            $('#birthMonth2 option').filter(function() {
                return $(this).text() == 'month';
            }).attr('selected', true);

            $('#birthDay2 option').filter(function() {
                return $(this).text() == 'day';
            }).attr('selected', true);
        }
    }

    function prepareFill(field) {
        return (selectedList.length > 1 && field && field.trim().length <= 0) ? false : true;
    }

    $('#container .panel-left').mouseleave(function() {
        fillData('name', $('#photo_name').val());
        fillData('author', $('#photo_author').val());
        fillData('description', $('#photo_describe').val());
        $('#map').ptmMap('update', {field: '#photo_place', lat: '#lat', lng: '#lng'});
        fillData('address', $('#photo_place').val());
        fillData('latitude', $('#lat').val());
        fillData('longitude', $('#lng').val());
        fillData('tags', $('#photo_tags').val());
        fillData('marked', $('input[name=marked]:checked').val());
        fillData('license', $('input[name=license]:checked').val());
        fillData('privacy', $('input[name=privacy]:checked').val());
        fillData('year', $('#birthYear').val());
        fillData('month', $('#birthMonth').val());
        fillData('day', $('#birthDay').val());
        fillData('year2', $('#birthYear2').val());
        fillData('month2', $('#birthMonth2').val());
        fillData('day2', $('#birthDay2').val());
    });
});