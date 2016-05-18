
$(function(){
	var info = $("#lg-form").Validform({
		tiptype:2,
		label:".label",
		showAllError:true,
		ajaxPost:true,
		callback:function(data) {
			var msg = data.msg;
			var status = data.status;
			alert(msg);
			if (status == 200)
				window.location.href="index.html";
			else{
				document.getElementById("pwd").value = "";
				document.getElementById("repw").value = "";
			}
		}
	});
	
	info.addRule([
	{
		ele:"#stuId",
		datatype:"n8-8",
		errormsg:"学号为8位数字",
		nullmsg:"学号不能为空"
	},
	{
		ele:"#name",
		datatype:"*",
		nullmsg:"姓名不能为空"
	},
	{
		ele:"#email",
		datatype:"e",
		nullmsg:"此邮箱将接收来自老师的邮件"
	},
	{
		ele:"#userPwd",
		datatype:"s4-16",
		errormsg:"密码长度为4到16位",
		nullmsg:"密码不能为空"
	},
	{
		ele:"#repw",
		recheck:"userPwd",
		datatype:"*",
		nullmsg:"请再输入一次密码！",
		errormsg:"两次密码不一致"
	}
	]);
});