$(document).ready(function(){
	var course = $("#courseId");
	course.change(function(){
		var courseId = $('#courseId option:selected') .val();//选中的值
		var obj = document.getElementById("courseId");
		for(var i = 0; i < document.getElementById("courseId").options.length; i++) {
			var value = obj.options[i].value;
			if (value == courseId) {
				document.getElementById(value).style.display="";
			}
			else{
				document.getElementById(value).style.display="none";
			}
		}
	})
})