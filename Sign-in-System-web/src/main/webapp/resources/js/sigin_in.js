
$(function(){
	var info = $("#lg-form").Validform({
		tiptype:1,
		label:".label",
		showAllError:true,
		ajaxPost:true,
		callback:function(data) {
			var msg = data.msg;
			var status = data.status;
			alert(msg);
			
			if (status == 200)
				window.location.href="success.html";
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
		nullmsg:"",
		errormsg:""
	}
	]);
});