
$(function(){
	var info = $("#student_info").Validform({
		tiptype:2,
		label:".label",
		showAllError:true,
		ajaxPost:true,
		callback:function(data) {
			var status = data.status;
			if (status == 200) {
				alert("修改成功");
				window.location.href="my_info.html";
			}
			else
				alert("修改失败");
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