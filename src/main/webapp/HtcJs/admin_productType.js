$(function(){
	//接收账号信息
	var phone=$.query.get("phone");
	var password=$.query.get("password");
	//工具集
	admin_productType_manager_tools={
		refresh:function(){
				$('#admin_productType').datagrid('reload');
			},
		search:function(){
			$('#admin_productType').datagrid({
					loader:function(param, success, error) {
						var options=$("#admin_productType").datagrid('options');
						var pageNumber=options.pageNumber;
						var pageSize=options.pageSize;
						var search = $('#admin_productType_search').val();
						var request_data = {
							"parameter" : search,
							"pageNumber":pageNumber,
							"pageSize":pageSize,
						};
						var jsondata = JSON.stringify(request_data);
						$.ajax({
							url : 'report',
							timeout:3000,
							type : "get",
							headers : {
								'userName' : phone,
								'hashedPassword' : password
							},
							data : "jsondata=" + jsondata,
							success : function(data) {
								data=data.rows;
								for (var i = 0; i < data.length; i++) {
									if (data[i].productTypeState == 0) {
										data[i].productTypeState = "不可用";
									} else if (data[i].productTypeState == 1) {
										data[i].productTypeState = "可用";
									}else if(data[i].productTypeState==-1){
										data[i].productTypeState = "未测试";
									}
								}
								success(data);
								$('#admin_productType').datagrid('loaded');
								$('#admin_productType').datagrid('resize');
							},
							error : function(XMLHttpRequest, status,errorThrown) {// 请求完成最终执行的函数
								var object = JSON.parse(XMLHttpRequest.responseText);
								$.messager.alert("提示",object.data,"info");
								$('#admin_productType').datagrid('loaded');
								$('#admin_productType').datagrid('resize');
							},
						});
					},
					}
			);	
		},
		add:function(){
			$('#admin_productType_add').dialog('open');
		},
		
		redo:function(){
				$('#admin_productType').datagrid('unselectAll');
		},
		
		update:function(){
				var rows=$('#admin_productType').datagrid('getSelections');
				if(rows.length>1){
					$.messager.alert("警告!","每次只能修改一条记录!","warning");
				}else if(rows.length==0){
					$.messager.alert("警告!","请至少选择一条记录!","warning");
				}else if(rows.length==1){
					if(rows[0].productTypeState=="不可用"){
						rows[0].productTypeState=0;
					}else if(rows[0].productTypeState=="可用"){
						rows[0].productTypeState=1;
					}
				$('#admin_productType_update').form('load',{
					admin_productType_id_update:rows[0]._id,
					admin_productType_MAC_update:rows[0].MAC_update,
					admin_productType_productTypeName_update:rows[0].productTypeName,
					admin_productType_hardwareVersion_update:rows[0].hardwareVersion,
					admin_productType_softwareVersion_update:rows[0].softwareVersion,
					admin_productType_macNumber_update:rows[0].macNumber,
					admin_productType_productTypeState_update:rows[0].productTypeState,
				}).dialog('open');				
					}
		},
	};
	
	$("#admin_productType").datagrid({
		width:'800',
		title:'产品类型列表',
		fit:true,
		border:false,
		columns:[[
			{
				field:'_id',
				title:'产品类型编号',
				sortable:true,
				fixed:true,
				halign:'center',
				width:100,
				checkbox:true,
			},
			{
				field:'productTypeName',
				title:'产品类型名称',
				sortable:true,
				halign:'center',
				width:100,
				
			},
			{
				field:'hardwareVersion',
				title:'固件版本',
				sortable:true,
				halign:'center',
				width:100,
				
			},
			{
				field:'softwareVersion',
				title:'软件版本',
				sortable:true,
				halign:'center',
				width:100,
				
			},
			{
				field:'macNumber',
				title:'MAC数目',
				sortable:true,
				halign:'center',
				width:100,
				
			},
			{
				field:'productTypeState',
				title:'可用状态',
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
		toolbar:"#admin_productType_manager_tool",
		loader:function(param,success,error){
			var options=$("#admin_productType").datagrid('options');
			var pageNumber=options.pageNumber;
			var pageSize=options.pageSize;
			var request = { "pageNow":pageNumber,"pageSize":pageSize};
			var json_data= JSON.stringify(request);
			$.ajax({
				url:'productType',
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
						if (data[i].productTypeState == 0) {
							data[i].productTypeState = "不可用";
						} else if (data[i].productTypeState == 1) {
							data[i].productTypeState = "可用";
						}
					}
					success(data);
					$('#admin_productType').datagrid('loaded');
					$('#admin_productType').datagrid('resize');
				},
				error: function(XMLHttpRequest,status,errorThrown){//请求完成最终执行的函数
					var object=JSON.parse(XMLHttpRequest.responseText);
					$.messager.alert("提示",object.data,"info");
					$('#admin_productType').datagrid('loaded');
					$('#admin_productType').datagrid('resize');
			},
			});
		},
	});
	
	//产品类型名称输入框
	$("#admin_productType_productTypeName_add").textbox({
		required:true,
		missingMessage:"请输入产品类型名称",
	});
	//硬件版本名称输入框
	$("#admin_productType_hardwareVersion_add").textbox({
		required:true,
		missingMessage:"请输入硬件版本",
	});
	//软件版本名称输入框
	$("#admin_productType_softwareVersion_add").textbox({
		required:true,
		missingMessage:"请输入软件版本",
	});
	
	//mac数量名称输入框
	$("#admin_productType_macNumber_add").numberbox({
		required:true,
		missingMessage:"请输入mac数量",
	});
	//定义用户添加弹窗
	$('#admin_productType_add').dialog({
		width:480,
		title:'添加用户',
		height:420,
		modal:true,
		closed:true,
		buttons:[{
			text:'添加',
			iconCls:'icon-ok',
			handler:function(){
				if($('#admin_productType_add').form('validate')){
					var productTypeName_add=$('#admin_productType_productTypeName_add').val();
					var hardwareVersion_add=$('#admin_productType_hardwareVersion_add').val();
					var softwareVersion_add=$('#admin_productType_softwareVersion_add').val();
					var macNumber_add=$('#admin_productType_macNumber_add').val();
					var requestdata={
							"productTypeName":productTypeName_add,
							"hardwareVersion":hardwareVersion_add,
							"softwareVersion":softwareVersion_add,
							"macNumber":macNumber_add 
						};
					requestdata=JSON.stringify(requestdata);
					$.ajax(
							{
								url:"productType",
								timeout:3000,
								type:'post',
								headers:{
									'userName':phone,
									'hashedPassword':password
								},
								data:{
									jsondata:requestdata,
								},
								beforeSend:function(){
									$.messager.progress({
										text:'正在添加，请稍后...',
									});
								},
								success:function(data){
									$.messager.progress('close');
									$.messager.show({
										title:'提示',
										msg:'添加成功！',
										timeout:2000,
									});
								$('#admin_productType_add').dialog('close').form('reset');
								$('#admin_productType').datagrid('reload');
								},
								error: function(XMLHttpRequest,status,errorThrown){//请求完成最终执行的函数
									var object=JSON.parse(XMLHttpRequest.responseText);
									$.messager.alert("提示",object.data,"info");
									$.messager.progress('close');
									$('#admin_productType').datagrid('loaded');
									$('#admin_productType').datagrid('resize');
							},		
							}
						);
					}
				
				}
			},{
			text:'取消',
			iconCls:'icon-redo',
			handler:function(){
				$('#admin_productType_add').dialog('close').form('reset');
			}
		}],
	});
	//定义用户修改弹窗
	$('#admin_productType_update').dialog({
		width:480,
		title:'修改用户',
		height:420,
		modal:true,
		closed:true,
		buttons:[{
			text:'修改',
			iconCls:'icon-ok',
			handler:function(){
				if($('#admin_productType_update').form('validate')){
					var id_update=$('#admin_productType_id_update').val();
					var productTypeName_update=$('#admin_productType_productTypeName_update').val();
					var hardwareVersion_update=$('#admin_productType_hardwareVersion_update').val();
					var softwareVersion_update=$('#admin_productType_softwareVersion_update').val();
					var macNumber_update=$('#admin_productType_macNumber_update').val();
					var productTypeState_update=$('#admin_productType_productTypeState_update').val();
					var requestdata = {
						"productTypeID":id_update,
						"productTypeName" : productTypeName_update,
						"hardwareVersion" : hardwareVersion_update,
						"softwareVersion" : softwareVersion_update,
						"macNumber" : macNumber_update,
						"productTypeState" : productTypeState_update
					};
					requestdata=JSON.stringify(requestdata);
					$.ajax(
							{
								url:'productType/:'+id_update,
								timeout:3000,
								type:'put',
								headers:{
									'userName':phone,
									'hashedPassword':password
								},
								data: "jsondata="+requestdata,
								beforeSend:function(){
									$.messager.progress({
										text:'正在添加，请稍后...',
									});
								},
								success:function(data){
									$.messager.progress('close');
									$.messager.show({
										title:'提示',
										msg:'修改成功！',
										timeout:2000,
									});
								$('#admin_productType_update').dialog('close').form('reset');
								$('#admin_productType').datagrid('reload');
								},
								error: function(XMLHttpRequest,status,errorThrown){//请求完成最终执行的函数
									var object=JSON.parse(XMLHttpRequest.responseText);
									$.messager.alert("提示",object.data,"info");
									$.messager.progress('close');
									$('#admin_productType').datagrid('loaded');
									$('#admin_productType').datagrid('resize');
							},
							}
						);
			}
			}
		},{
			text:'取消',
			iconCls:'icon-redo',
			handler:function(){
				$('#admin_productType_update').dialog('close').form('reset');
			
			}
		}],
	});
});