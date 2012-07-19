var timeout = 100;
var closetimer = 0;
var ddmenuitem = 0;

function dd_open() {  
	dd_canceltimer();
   	dd_close();
   	ddmenuitem = $(this).find('ul').css('visibility', 'visible');
}

function dd_close() {  
	if (ddmenuitem) ddmenuitem.css('visibility', 'hidden');
}

function dd_timer() {  
	closetimer = window.setTimeout(dd_close, timeout);
}

function dd_canceltimer() {  
	if(closetimer) {  
		window.clearTimeout(closetimer);
    	closetimer = null;
  	}
}

$(document).ready(function() {  
	$('#dropdown > li').bind('mouseover', dd_open)
   	$('#dropdown > li').bind('mouseout',  dd_timer)
});

document.onclick = dd_close;