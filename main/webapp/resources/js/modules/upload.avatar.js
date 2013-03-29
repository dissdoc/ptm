$(function() {
    function flushFields(c) {
        $('#w').val(c.w);
        $('#x1').val(c.x);
        $('#y1').val(c.y);
    }

    window.URL = window.URL || window.webkitURL;
    $('#modalUploadAvatar').hide();
    var jcrop_api = null;
    var width = 0;
    var height = 0;

    var uploader = new plupload.Uploader({
        runtimes : 'html5',
        browse_button : 'upload-avatar',
        container : 'modalUploadAvatar',
        max_file_size : '10mb',
        max_file_count: 1,
        url : "/user/upload/avatar",
        multi_selection: false,
        multipart: true,
        multipart_params: {
            '_http_accept': 'application/javascript'
        },
        filters : [
            {title : "Image files", extensions : "jpg,gif,png,jpeg"}
        ]
    });

    $('#save-avatar').click(function(e) {
        uploader.start();
        e.preventDefault();
    });

    uploader.init();

    uploader.bind('FilesAdded', function(up, files) {
        if (jcrop_api != null) {
            jcrop_api.destroy();
            jcrop_api = null;
        }

        $('#modalUploadAvatar .large-img img').remove();
        $('body').ptmModal('windowInit');

        $.each(files, function(i, file) {
            var img = new Image();
            $('#modalUploadAvatar .large-img').append(img);
            img.src = window.URL.createObjectURL(file.nativeFile);
            img.onload = function() {
                $('.window').css('top', (window.innerHeight-img.height)/2);
                $('.window').css('left', (window.innerWidth-img.width)/2);
                width = img.width;
                height = img.height;
            }
        });

        $('#modalUploadAvatar .large-img img').Jcrop({
            onChange: flushFields,
            onSelect: flushFields,
            aspectRatio: 1,
            setSelect: [0, 0, 30, 30]
        }, function() {
            jcrop_api = this;
        });

        $('body').ptmModal('show', {dialog: '#modalUploadAvatar'});
        $('body').ptmModal('destroy2', {dialog: '#modalUploadAvatar', parent: '#upload-avatar'});
    });

    uploader.bind('FileUploaded', function(up, file, info) {
        $('#user-avatar img').attr('src', '/avatar/'+info.response+'/icon.png');
        if (jcrop_api != null) {
            jcrop_api.destroy();
            jcrop_api = null;
        }
        $('#modalUploadAvatar .large-img img').remove();
        $('body').ptmModal('close', {dialog: '#modalUploadAvatar', parent: '#upload-avatar'});
    });

    uploader.bind('BeforeUpload', function(up, file) {
        $.extend(up.settings.multipart_params, {
            w: $('#w').val(),
            x: $('#x1').val(),
            y: $('#y1').val(),
            width: width,
            height: height
        });
    });

    $('#cancel-from-upload').click(function() {
        $('body').ptmModal('close', {dialog: '#modalUploadAvatar', parent: '#upload-avatar'});
        return false;
    });
});