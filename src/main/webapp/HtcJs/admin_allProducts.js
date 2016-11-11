$(function(){
	//接收账号信息
	var phone=$.query.get("phone");
	var password=$.query.get("password");
	//工具集
	admin_allProducts_manager_tools={
		refresh:function(){
				$('#admin_allProducts').datagrid('reload');
			},
		search:function(){
			$('#admin_allProducts').datagrid({
			loader:function(param,success,error){
				var options=$("#admin_allProducts").datagrid('options');
				var pageNumber=options.pageNumber;
				var pageSize=options.pageSize;
				var search=$('#admin_allProducts_search').val();
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
							if (data[i].testingResult == 0) {
								data[i].testingResult = "失败";
							} else if (data[i].testingResult == 1) {
								data[i].testingResult = "成功";
							}
						}
						success(data);
						$('#admin_allProducts').datagrid('loaded');
						$('#admin_allProducts').datagrid('resize');
					},
					error: function(XMLHttpRequest,status,errorThrown){//请求完成最终执行的函数
						var object=JSON.parse(XMLHttpRequest.responseText);
						$.messager.alert("提示",object.data,"info");
						$('#admin_allProducts').datagrid('loaded');
						$('#admin_allProducts').datagrid('resize');
				},		
				});
			},
		});
	},
		redo:function(){
				$('#admin_allProducts').datagrid('unselectAll');
			},
		update:function(){
				var rows=$('#admin_allProducts').datagrid('getSelections');
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
						$('#admin_allProducts_update').form('load',{
							admin_allProducts_id_update:rows[0]._id,
							admin_allProducts_deviceID_update:rows[0].deviceID,
							admin_allProducts_MAC_update:rows[0].MAC,
							admin_allProducts_productTypeName_update:rows[0].productTypeName,
							admin_allProducts_productBatch_update:rows[0].productBatch,
							admin_allProducts_producer_update:rows[0].producer,
							admin_allProducts_productEndDate_update:rows[0].productEndDate,
							admin_allProducts_testResult_update:rows[0].testResult,
							admin_allProducts_productState_update:rows[0].productState,
						}).dialog('open');	
					}
		},
		
	};
	
	$("#admin_allProducts_deviceID_update").textbox({
		required:true,
		editable:false,
	});
	
	$("#admin_allProducts_MAC_update").textbox({
		required:true,
		editable:false,
	});
	$("#admin_allProducts_productTypeName_update").textbox({
		required:true,
		editable:false,
	});
	$("#admin_allProducts_productBatch_update").textbox({
		required:true,
		editable:false,
	});
	$("#admin_allProducts_producer_update").textbox({
		required:true,
		editable:false,
	});
	$("#admin_allProducts_productEndDate_update").datebox({
		required:true,
		editable:false,
		missingMessage:"请选择测试完成时间",
	});
	//测试结果
	$("#admin_allProducts_testResult_update").combobox({
		required:true,
		width:120,
		editable:false,
		missingMessage:"请选择测试结果",
	});
	//产品状态
	$("#admin_allProducts_productState_update").combobox({
		required:true,
		width:120,
		editable:false,
		missingMessage:"请选择产品状态",
	});
	$("#admin_allProducts").datagrid({
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
		toolbar:"#admin_allProducts_manager_tool",
		loader:function(param,success,error){
			var options=$("#admin_allProducts").datagrid('options');
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
					$('#admin_allProducts').datagrid('loaded');
					$('#admin_allProducts').datagrid('resize');
				},
				error: function(XMLHttpRequest,status,errorThrown){//请求完成最终执行的函数
//					var object=JSON.parse(XMLHttpRequest.responseText);
//					$.messager.alert("提示",object.data,"info");
					$('#admin_allProducts').datagrid('loaded');
					$('#admin_allProducts').datagrid('resize');
			},
			});
		},
	});
	
	
	//定义修改产品弹窗
	$('#admin_allProducts_update').dialog({
		width:480,
		title:'修改',
		height:420,
		modal:true,
		closed:true,
		buttons:[{
			text:'修改',
			iconCls:'icon-ok',
			handler:function(){
				if($('#admin_allProducts_update').form('validate')){
					var _id_update=$('#admin_allProducts_id_update').val();
					var productEndDate_update=$('#admin_allProducts_productEndDate_update').datebox('getValue');
					var testResult_update=$('#admin_allProducts_testResult_update').combobox('getValue');
					var productState_update=$('#admin_allProducts_productState_update').combobox('getValue');
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
							$('#admin_allProducts_update').dialog('close').form('reset');
							$('#admin_allProducts').datagrid('reload');
						},
						error: function(XMLHttpRequest,status,errorThrown){//请求完成最终执行的函数
							var object=JSON.parse(XMLHttpRequest.responseText);
							$.messager.alert("提示",object.data,"info");
							$.messager.progress("close");
							$('#admin_allProducts').datagrid('loaded');
						},
					});
			}
			}
		},{
			text:'取消',
			iconCls:'icon-redo',
			handler:function(){
				$('#admin_allProducts_update').dialog('close').form('reset');
			}
		}],
	});
});