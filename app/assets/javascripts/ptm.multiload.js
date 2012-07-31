(function($) {
  $.fn.ptmMultiload = function(options) {
    var defaults = {
      text: 'No selected files'
    }, opts = $.extend(defaults, options);

    window.URL = window.URL || window.webkitURL;

    var filelist = this.find('ul.filelist');
    var fileinput = $('#fileinput');
    var fileselect = $('#fileselect');

    var selected = false;

    fileselect.click(function(e) {
      if (fileinput) fileinput.click();
      e.preventDefault();
    });

    fileinput.change(function (files) {
        var files = $(this).get(0).files;
        if (!files.length) {
            filelist.html('<li>' + opts['text'] + '</li>');
        } else {
            if (!selected) filelist.find( 'li').remove();
            selected = true;

            for (var i = 0; i < files.length; i++) {
                var li = document.createElement('li');
                filelist.append(li);

                var img = document.createElement("img");
                img.src = window.URL.createObjectURL(files[i]);
                img.onload = function(e) {
                    window.URL.revokeObjectURL(this.src);
                }
                li.appendChild(img);

                var name = document.createElement("div");
                name.className = "name"
                name.innerHTML = substrName(files[i].name);
                li.appendChild(name);

                var size_bytes = document.createElement("div");
                size_bytes.innerHTML = parseInt(files[i].size/1024) + " Kb";
                li.appendChild(size_bytes);
                $('#items').append('<option value="'+ files[i].name  +'">test</option>');
            }
        }
    }).change();

     function substrName(name) {
      name = name.substring(0, 19);
      name = name + "...";
      return name;
    }
  };
})(jQuery);
