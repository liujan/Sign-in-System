
$(function(){
	var info = $("#student_info").Validform({
		tiptype:2,
		label:".label",
		showAllError:true,
		ajaxPost:true,
		callback:function(data) {
			var msg = decodeURI(data.message);
			var status = data.status;
			alert(msg);
			if (status == 1) {
				window.location.href="my_info.html";
			}
		}
	});
	
	info.addRule([
	{
		ele:"#name",
		datatype:"*",
		nullmsg:"姓名不能为空"
	},
	{
		ele:"#email",
		datatype:"e",
		nullmsg:"邮箱不能为空"
	},
	{
		ele:"#userPwd",
		datatype:"s4-16",
		errormsg:"密码长度为4到16位",
		nullmsg:"密码不能为空"
	}
	]);
});