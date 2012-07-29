(function($) {
  $.fn.ptmMultiload = function(options) {
    var defaults = {
      text: 'No selected files'
    }, opts = $.extend(defaults, options);

    window.URL = window.URL || window.webkitURL;

    var filelist = this.find('ul.filelist');
    var fileinput = $('#fileinput');
    var fileselect = $('#fileselect');

    
  };
})(jQuery);
