$(function(){
	//接收账号信息
	var phone=$.query.get("phone");
	var password=$.query.get("password");
	//工具集
	user_allProducts_manager_tools={
		refresh:function(){
				$('#user_allProducts').datagrid('reload');
			},
		search:function(){
			$('#user_allProducts').datagrid({
			loader:function(param,success,error){
				var options=$("#user_allProducts").datagrid('options');
				var pageNumber=options.pageNumber;
				var pageSize=options.pageSize;
				var search=$('#user_allProducts_search').val();
				var request_data = {
						"parameter":search,
						"pageNumber":pageNumber,
						"pageSize":pageSize,
				};
				var jsondata=JSON.stringify(request_data);
				$.ajax({
					url:'product',
					timeout:3000,
					type:"get",	
					headers:{
						'userName':phone,
						'hashedPassword':password
					},
					data:"jsondata="+jsondata,											
					success:function(data){
						data=data.rows;
						for (var i = 0; i < data.length; i++) {
							switch(parseInt(data[i].productState)){
								case -1: 
									data[i].productState = "";
									break;
								case 0: 
									data[i].productState = "失败";
									break;
								case 1:
									data[i].productState = "成功";
									break;
								case 2: 
									data[i].productState = "待定";
									break;
								case 3: 
									data[i].productState = "废弃";
									break;
							}
						}
						success(data);
						$('#user_allProducts').datagrid('loaded');
						$('#user_allProducts').datagrid('resize');
					},
					error: function(XMLHttpRequest,status,errorThrown){//请求完成最终执行的函数
						var object=JSON.parse(XMLHttpRequest.responseText);
						$.messager.alert("提示",object.data,"info");
						$('#user_allProducts').datagrid('loaded');
						$('#user_allProducts').datagrid('resize');
				},		
				});
			},
		});
	},
		redo:function(){
				$('#user_allProducts').datagrid('unselectAll');
			},
		update:function(){
				var rows=$('#user_allProducts').datagrid('getSelections');
				if(rows.length>1){
					$.messager.alert("警告!","每次只能修改一条记录!","warning");
				}else if(rows.length==0){
					$.messager.alert("警告!","请至少选择一条记录!","warning");
				}else if(rows.length==1){
					if (rows[0].productState == "") {
						rows[0].productState = -1;
					}else if (rows[0].productState == "失败") {
						rows[0].productState = 0;
					} else if (rows[0].productState == "成功") {
						rows[0].productState = 1;
					}else if(rows[0].productState == "待定"){
						rows[0].productState = 2;
					}else if(rows[0].productState == "废弃"){
						rows[0].productState = 3;
					}
					if (rows[0].testResult == "失败") {
						rows[0].testResult = 0;
					} else if (rows[0].testingResult == "成功") {
						rows[0].testResult = 1;
					}
						$('#user_allProducts_update').form('load',{
							user_allProducts_id_update:rows[0]._id,
							user_allProducts_deviceID_update:rows[0].deviceID,
							user_allProducts_MAC_update:rows[0].MAC,
							user_allProducts_productTypeName_update:rows[0].productTypeName,
							user_allProducts_productBatch_update:rows[0].productBatch,
							user_allProducts_producer_update:rows[0].producer,
							user_allProducts_productEndDate_update:rows[0].productEndDate,
							user_allProducts_testResult_update:rows[0].testResult,
							user_allProducts_productState_update:rows[0].productState,
						}).dialog('open');	
					}
		},
		
	};
	
	$("#user_allProducts_deviceID_update").textbox({
		required:true,
		editable:false,
	});
	
	$("#user_allProducts_MAC_update").textbox({
		required:true,
		editable:false,
	});
	$("#user_allProducts_productTypeName_update").textbox({
		required:true,
		editable:false,
	});
	$("#user_allProducts_productBatch_update").textbox({
		required:true,
		editable:false,
	});
	$("#user_allProducts_producer_update").textbox({
		required:true,
		editable:false,
	});
	$("#user_allProducts_productEndDate_update").datebox({
		required:true,
		editable:false,
		missingMessage:"请选择测试完成时间",
	});
	//测试结果
	$("#user_allProducts_testResult_update").combobox({
		required:true,
		width:120,
		editable:false,
		missingMessage:"请选择测试结果",
	});
	//产品状态
	$("#user_allProducts_productState_update").combobox({
		required:true,
		width:120,
		editable:false,
		missingMessage:"请选择产品状态",
	});
	$("#user_allProducts").datagrid({
		width:'800',
		title:'所有产品',
		fit:true,
		cache:false,
		border:false,
		columns:[[
			{
				field:'_id',
				title:'产品编号',
				sortable:true,
				fixed:true,
				halign:'center',
				width:100,
				checkbox:true,
			},      
			{
				field:'deviceID',
				title:'deviceID',
				sortable:true,
				fixed:true,
				halign:'center',
				width:100,
			},
			{
				field:'MAC',
				title:'mac',
				sortable:true,
				fixed:true,
				halign:'center',
				width:100,
				
			},
			{
				field:'productTypeName',
				title:'产品类型名称',
				sortable:true,
				halign:'center',
				width:100,
				
			},
			{
				field:'productBatch',
				title:'生产批次',
				sortable:true,
				halign:'center',
				width:100,
				
			},
			{
				field:'producer',
				title:'生产商',
				sortable:true,
				halign:'center',
				width:100,
				
			},
			{
				field:'productEndDate',
				title:'产品完成时间',
				sortable:true,
				halign:'center',
				width:100,
				
			},
			{
				field:'testResult',
				title:'测试结果',
				sortable:true,
				halign:'center',
				width:100,
				
			},
			{
				field:'productState',
				title:'产品状态',
				sortable:true,
				halign:'center',
				width:100,
				
			},
		]],
		pagination:true,
		pageSize:10,
		pageList:[10,15,20],
		remoteSort:false,
		rownumbers:true,
		fitColumns:true,
		toolbar:"#user_allProducts_manager_tool",
		loader:function(param,success,error){
			var options=$("#user_allProducts").datagrid('options');
			var pageNumber=options.pageNumber;
			var pageSize=options.pageSize;
			var request = {
				"parameter" : "",
				"pageNow" : pageNumber,
				"pageSize" : pageSize
			};
			var json_data= JSON.stringify(request);
			$.ajax({
				url:'product',
				timeout:3000,
				type:"get",	
				headers:{
					'userName':phone,
					'hashedPassword':password
				},
				data:"jsondata="+json_data,
				success:function(data){
					data=data.rows;
					for (var i = 0; i < data.length; i++) {
						switch(parseInt(data[i].productState)){
							case -1: 
								data[i].productState = "";
								break;
							case 0: 
								data[i].productState = "失败";
								break;
							case 1:
								data[i].productState = "成功";
								break;
							case 2: 
								data[i].productState = "待定";
								break;
							case 3: 
								data[i].productState = "废弃";
								break;
						}
						if (data[i].testingResult == 0) {
							data[i].testingResult = "失败";
						} else if (data[i].testingResult == 1) {
							data[i].testingResult = "成功";
						}
					}
					success(data);
					$('#user_allProducts').datagrid('loaded');
					$('#user_allProducts').datagrid('resize');
				},
				error: function(XMLHttpRequest,status,errorThrown){//请求完成最终执行的函数
//					var object=JSON.parse(XMLHttpRequest.responseText);
//					$.messager.alert("提示",object.data,"info");
					$('#user_allProducts').datagrid('loaded');
					$('#user_allProducts').datagrid('resize');
			},
			});
		},
	});
	
	
	//定义修改产品弹窗
	$('#user_allProducts_update').dialog({
		width:480,
		title:'修改',
		height:420,
		modal:true,
		closed:true,
		buttons:[{
			text:'修改',
			iconCls:'icon-ok',
			handler:function(){
				if($('#user_allProducts_update').form('validate')){
					var _id_update=$('#user_allProducts_id_update').val();
					var productEndDate_update=$('#user_allProducts_productEndDate_update').datebox('getValue');
					var testResult_update=$('#user_allProducts_testResult_update').combobox('getValue');
					var productState_update=$('#user_allProducts_productState_update').combobox('getValue');
					var requestdata={
							"productEndDate":productEndDate_update,
							"testResult":testResult_update,
							"productState":productState_update
							};
					requestdata=JSON.stringify(requestdata);
					$.ajax({
						url:"product/:"+_id_update,
						timeout:3000,
						type:'put',
						headers:{
							'userName':phone,
							'hashedPassword':password
						},
						data:
							"jsondata="+requestdata,
					
						beforeSend:function(){
							$.messager.progress({
								text:'正在修改，请稍后...',
							});
						},
						success : function(data) {
							$.messager.progress('close');
							$.messager.show({
									title : '提示',
									msg : '修改成功',
									timeout : 2000,
							});
							$('#user_allProducts_update').dialog('close').form('reset');
							$('#user_allProducts').datagrid('reload');
						},
						error: function(XMLHttpRequest,status,errorThrown){//请求完成最终执行的函数
							var object=JSON.parse(XMLHttpRequest.responseText);
							$.messager.alert("提示",object.data,"info");
							$.messager.progress("close");
							$('#user_allProducts').datagrid('loaded');
						},
					});
			}
			}
		},{
			text:'取消',
			iconCls:'icon-redo',
			handler:function(){
				$('#user_allProducts_update').dialog('close').form('reset');
			}
		}],
	});
});