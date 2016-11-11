$(function(){
	//登录界面
	$("#login").dialog({
		title:'登录页',
		//width:680,
		//height:480,
		modal:true,
		buttons:"#btn",
		closable:false,
	});
	//账号密码验证
	$('#phone').numberbox({
		required:true,
		width:200,
		height:30,
		prompt:'请输入位手机号',
		missingMessage:'请输入手机号码',
		validType:'length[11,11]',
		invalidMessage:'正确手机号码为11位数字',
	});
	
	$('#password').passwordbox({
		required:true,
		width:200,
		height:30,
		prompt:'请输入密码',
		validType:'length[6,20]',
		missingMessage:'请输入密码',
		invalidMessage:'密码长度至少为6，最大为20',
	});
	//加载时先验证,光标锁定
	if(!$('#phone').numberbox('isValid')){
			$('#phone').focus();
		}else if(!$('#password').passwordbox('isValid')){
			$('#password').focus();
		}
	//单击登录
	$('#btn a').click(function(){
		if(!$('#phone').numberbox('isValid')){
			$('#phone').focus();
		}else if(!$('#password').passwordbox('isValid')){
			$('#password').focus();
		}else{
			var phone=$.trim($('#phone').val());
			var	password=$.trim($('#password').val());
			var requestdata={"queryTab":1};
			requestdata=JSON.stringify(requestdata);
			$.ajax({
				url:'login',
				cache: false,
				timeout:3000,
				Type:'get',
				dataType: "json",
				headers:{
					userName:phone,
				 	hashedPassword:password,
					},
				data:
					"jsondata="+requestdata,
				beforeSend:function(){
					$.messager.progress({
						text:'正在登录，请稍后...',
					});
				},
				success:function(data){
					$.messager.progress('close');
					if(data.authority==1){
						location.href ="admin_main.html?phone="+phone+"&name="+data.name+"&password="+password+"";
						window.close();
					}else if(data.authority==0){
						location.href ="user_main.html?phone="+phone+"&name="+data.name+"&password="+password+"";			
						window.close();	
					}
				},
				error : function(XMLHttpRequest, status, errorThrown) {// 请求错误时执行的函数
					$.messager.progress("close");
					console.log(XMLHttpRequest.responseText);
					var object = JSON.parse(XMLHttpRequest.responseText);
					$.messager.alert("提示",object.data,"info");
				},
			});
		}
	});
});

