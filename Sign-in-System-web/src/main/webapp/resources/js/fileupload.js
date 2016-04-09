/*jslint unparam: true */
/*global window, $ */
$(function () {
    'use strict';
    // Change this to the location of your server-side upload handler:
    var url = "upload_photo.html";
    jQuery('#fileupload').fileupload({
        url: url,
        dataType: 'json',
		success:function(data) {
			var msg = decodeURI(data.message);
			var status = data.status;
			if (status == -2) {
				alert(msg);
			}
			else {
				$('<p/>').text(msg).appendTo('#files');
			}
			
		},
		error:function(){
			alert("上传失败");
		},
        progressall: function (e, data) {
            var progress = parseInt(data.loaded / data.total * 100, 10);
            $('#progress .progress-bar').css(
                'width',
                progress + '%'
            );
        }
    }).prop('disabled', !jQuery.support.fileInput)
        .parent().addClass(jQuery.support.fileInput ? undefined : 'disabled');
});

function clearBar(){
	
	$('#progress .progress-bar').css(
           'width',
           0 + '%'
    );
}