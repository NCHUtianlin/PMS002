$(function(){
	var phone=$.query.get("phone");
	var password=$.query.get("password");
	
	//导出Mac信息
	$("#user_toExport_MAC_submit").click(function(){
		$("#user_toExport_MAC_userName").val(phone);
		$("#user_toExport_MAC_hashedPassword").val(password);
		var form=document.forms[0];
		form.action="export";
    	form.method="post";
    	form.submit();
	});
	
	//导出批次统计
	$("#user_toExport_AnalysisBatch_submit").click(function(){
		$("#user_toExport_AnalysisBatch_userName").val(phone);
		$("#user_toExport_AnalysisBatch_hashedPassword").val(password);
		var form=document.forms[1];
		form.action="export";
    	form.method="post";
    	form.submit();
	});
	
	//导出生产商统计
	$("#user_toExport_AnalysisProducer_submit").click(function(){
		$("#user_toExport_AnalysisProducer_userName").val(phone);
		$("#user_toExport_AnalysisProducer_hashedPassword").val(password);
		var form=document.forms[2];
		form.action="export";
    	form.method="post";
    	form.submit();
	});
	
	//导出产品类型统计
	$("#user_toExport_AnalysisType_submit").click(function(){
		$("#user_toExport_AnalysisType_userName").val(phone);
		$("#user_toExport_AnalysisType_hashedPassword").val(password);
		var form=document.forms[3];
		form.action="export";
    	form.method="post";
    	form.submit();
	});
	//导出产品信息
	$("#user_toExport_Products_submit").click(function(){
		if($("#user_toExport_productBatch").validatebox('validate')){
			$("#user_toExport_Products_userName").val(phone);
			$("#user_toExport_Products_hashedPassword").val(password);
			var form=document.forms[4];
			form.action="export";
	    	form.method="post";
	    	form.submit();
		}
	});
	
	
	//检测产品批次输入框
	$("#user_toExport_productBatch").validatebox({
		required:true,
		missingMessage:"请输入产品批次",
		tipPosition:'left',
	});
	
	
	
	
	
//	//工具集
//	user_toExport_tools={
//			ExportUsers:function(){
//				var request_data={"type":"Users"};
//				var jsondata=JSON.stringify(request_data);
//				$.ajax({
//					url:'export',
//					timeout:3000,
//					type:"get",	
//					headers:{
//						'userName':phone,
//						'hashedPassword':password
//					},
//					data:"jsondata="+jsondata,
//					success:function(data){
//						$.messager.show({
//							title:"提示",
//							msg:"导出成功",
//							timeout:2000,
//						});
//					},
//					error: function(XMLHttpRequest,status,errorThrown){//请求完成最终执行的函数
//						var object=JSON.parse(XMLHttpRequest.responseText);
//						$.messager.alert("提示",object.data,"info");
//				},		
//				});
//			},
//			ExportMAC:function(){
//				var request_data={"type":"MACs"};
//				var jsondata=JSON.stringify(request_data);
//				$.ajax({
//					url:'export',
//					timeout:3000,
//					type:"get",	
//					headers:{
//						'userName':phone,
//						'hashedPassword':password
//					},
//					data:"jsondata="+jsondata,
//					success:function(data){
//						$.messager.show({
//							title:"提示",
//							msg:"导出成功",
//							timeout:2000,
//						});
//					},
//					error: function(XMLHttpRequest,status,errorThrown){//请求完成最终执行的函数
//						var object=JSON.parse(XMLHttpRequest.responseText);
//						$.messager.alert("提示",object.data,"info");
//				},		
//				});
//			},
//			ExportAnalysisBatch:function(){
//				var request_data={"type":"AnalysisBatch"};
//				var jsondata=JSON.stringify(request_data);
//				$.ajax({
//					url:'export',
//					timeout:3000,
//					type:"get",	
//					headers:{
//						'userName':phone,
//						'hashedPassword':password
//					},
//					data:"jsondata="+jsondata,
//					success:function(data){
//						$.messager.show({
//							title:"提示",
//							msg:"导出成功",
//							timeout:2000,
//						});
//					},
//					error: function(XMLHttpRequest,status,errorThrown){//请求完成最终执行的函数
//						var object=JSON.parse(XMLHttpRequest.responseText);
//						$.messager.alert("提示",object.data,"info");
//				},		
//				});
//			},
//			ExportAnalysisProducer:function(){
//				var request_data={"type":"AnalysisProducer"};
//				var jsondata=JSON.stringify(request_data);
//				$.ajax({
//					url:'export',
//					timeout:3000,
//					type:"get",	
//					headers:{
//						'userName':phone,
//						'hashedPassword':password
//					},
//					data:"jsondata="+jsondata,
//					success:function(data){
//						$.messager.show({
//							title:"提示",
//							msg:"导出成功",
//							timeout:2000,
//						});
//					},
//					error: function(XMLHttpRequest,status,errorThrown){//请求完成最终执行的函数
//						var object=JSON.parse(XMLHttpRequest.responseText);
//						$.messager.alert("提示",object.data,"info");
//				},		
//				});
//			},
//			ExportAnalysisType:function(){
//				var request_data={"type":"AnalysisType"};
//				var jsondata=JSON.stringify(request_data);
//				$.ajax({
//					url:'export',
//					timeout:3000,
//					type:"get",	
//					headers:{
//						'userName':phone,
//						'hashedPassword':password
//					},
//					data:"jsondata="+jsondata,
//					success:function(data){
//						$.messager.show({
//							title:"提示",
//							msg:"导出成功",
//							timeout:2000,
//						});
//					},
//					error: function(XMLHttpRequest,status,errorThrown){//请求完成最终执行的函数
//						var object=JSON.parse(XMLHttpRequest.responseText);
//						$.messager.alert("提示",object.data,"info");
//				},		
//				});
//			},
//			ExportProducts:function(){
//				var productBatch_export=$.trim($("user_productBatch_export").val());
//				var request_data={"type":"Products","productBatch":productBatch_export};
//				var jsondata=JSON.stringify(request_data);
//				$.ajax({
//					url:'export',
//					timeout:3000,
//					type:"get",	
//					headers:{
//						'userName':phone,
//						'hashedPassword':password
//					},
//					data:"jsondata="+jsondata,
//					success:function(data){
//						$.messager.show({
//							title:"提示",
//							msg:"导出成功",
//							timeout:2000,
//						});
//					},
//					error: function(XMLHttpRequest,status,errorThrown){//请求完成最终执行的函数
//						var object=JSON.parse(XMLHttpRequest.responseText);
//						$.messager.alert("提示",object.data,"info");
//				},		
//				});
//			},
//	};
});