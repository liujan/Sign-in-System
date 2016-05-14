
$(function(){
	var info = $("#teacher_info").Validform({
		tiptype:2,
		label:".label",
		showAllError:true,
		ajaxPost:true,
		callback:function(data) {
			var msg = decodeURI(data.msg);
			var status = data.status;
			alert(msg);
			if (status == 200) {
				window.location.href="info";
			}
		}
	});
	
	info.addRule([
	{
		ele:"#userName",
		datatype:"s4-16",
		errormsg:"用户名长度为4到16为字符",
		nullmsg:"用户名不能为空"
	},
	{
		ele:"#teacherName",
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