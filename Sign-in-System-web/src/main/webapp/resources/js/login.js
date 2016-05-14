
$(function(){
	var info = $("#lg-form").Validform({
		tiptype:1,
		label:".label",
		showAllError:true,
		ajaxPost:true,
		callback:function(data) {
			var status = data.status;

			if (status != -1)
				window.location.href="home";
			else{
				var msg = decodeURI(data.message);
				alert(msg);
				document.getElementById("pwd").value = "";
			}
		}
	});
	
	info.addRule([
	{
		ele:"#stuId",
		datatype:"n8-8",
		errormsg:"",
		nullmsg:""
	},
	{
		ele:"#name",
		datatype:"*",
		nullmsg:""
	},
	{
		ele:"#pwd",
		datatype:"*",
		nullmsg:"",
		errormsg:""
	}
	]);
});