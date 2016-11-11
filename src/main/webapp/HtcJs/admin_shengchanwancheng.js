$(function(){
	//接收账号信息
	var phone=$.query.get("phone");
	var password=$.query.get("password");
	//工具集
	admin_shengchanwancheng_manager_tools={
			// 重新刷新数据
		refresh:function(){
				$('#admin_shengchanwancheng').datagrid('reload');
			},
		
		search:function(){
			$('#admin_shengchanwancheng').datagrid({
					loader:function(param, success, error) {
						var search = $("input[name='admin_shengchanwancheng_search']").val();
						var date_from=$("input[name='admin_shengchanwancheng_date_from']").val();
						var date_to=$("input[name='admin_shengchanwancheng_date_to']").val();
						
						var date_from_search=new Date(date_from.replace(/-/g,"/"));
						var date_to_search=new Date(date_to.replace(/-/g,"/"));
						if(date_from_search>date_to_search){
							$.messager.alert("提示","结束时间应该大于或等于开始时间","info");
							return false;
						}
						var times={"start":date_from,"end":date_to};
						var request_data = {
							"parameter" : search,
							"times":times,
						};
						var jsondata = JSON.stringify(request_data);
						$.ajax({
									url : 'productNote',
									timeout:3000,
									headers : {
										'userName' : phone,
										'hashedPassword' : password
									},
									type : "get",
									data : "jsondata=" + jsondata,
									success : function(data) {
										data=data.rows;
										for(var i=0;i<data.length;i++){
											if(data[i].productOverdue==0){
													data[i].productOverdue="逾期";
											}else if(data[i].productOverdue==1){
													data[i].productOverdue="未逾期";
													}
										}
										success(data);
										$('#admin_shengchanwancheng').datagrid('loaded');
										$('#admin_shengchanwancheng').datagrid('resize');
									},
									error : function(XMLHttpRequest, status,errorThrown) {// 请求完成最终执行的函数
										var object = JSON.parse(XMLHttpRequest.responseText);
										$.messager.alert("提示",object.data,"info");
										$.messager.progress("close");
										$('#admin_shengchanwancheng').datagrid('loaded');
										$('#admin_shengchanwancheng').datagrid('resize');
									},
								});
					},
			});	
		},
		
		add:function(){
			$('#admin_shengchanwancheng_add').dialog('open');
			
		},
		
		redo:function(){
				$('#admin_shengchanwancheng').datagrid('unselectAll');
		},
		
		update:function(){
				var rows=$('#admin_shengchanwancheng').datagrid('getSelections');
				if(rows.length>1){
					$.messager.alert("警告!","每次只能修改一条记录!","warning");
				}else if(rows.length==0){
					$.messager.alert("警告!","请至少选择一条记录!","warning");
				}else if(rows.length==1){
						if(rows[0].productOverdue=="逾期"){
							rows[0].productOverdue=0;
						}else if(rows[0].productOverdue=="未逾期"){
								rows[0].productOverdue=1;
								}
						$('#admin_shengchanwancheng_update').form('load',{
                            noteID_update:rows[0]._id,
							productBatch_update:rows[0].productBatch,
							productStartDate_update:rows[0].productStartDate,
							productEndDate_update:rows[0].productEndDate,
							productTotalQuantity_update:rows[0].productTotalQuantity,
							productUsableNumber_update:rows[0].productOverdue,
							productOverdue_update:rows[0].productOverdue,
							productOverdueExplain_update:rows[0].productOverdueExplain,
						}).dialog('open');				
					}
		},
	};
	
	$("#admin_shengchanwancheng").datagrid({
		width:'800',
		title:'用户列表',
		fit:true,
		border:false,
		columns:[[
		{
				field:'_id',
				title:'记录编号',
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
				fixed:true,
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
				field:'productStartDate',
				title:'实际生产开始时间',
				sortable:true,
				halign:'center',
				width:100,
				
			},
			{
				field:'productEndDate',
				title:'实际生产完成时间',
				sortable:true,
				halign:'center',
				width:100,
				
			},
			{
				field:'productTotalQuantity',
				title:'实际生产产品总数',
				sortable:true,
				halign:'center',
				width:100,
				
			},
			{
				field:'productUsableNumber',
				title:'合格数量',
				sortable:true,
				halign:'center',
				width:100,
				
			},
			{
				field:'productOverdue',
				title:'逾期与否',
				sortable:true,
				halign:'center',
				width:100,
				
			},
			{
				field:'productOverdueExplain',
				title:'逾期说明',
				sortable:true,
				halign:'center',
				width:100,
				
			},
			{
				field:'notePerson',
				title:'记录人',
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
		toolbar:"#admin_shengchanwancheng_manager_tool",
		loader:function(param,success,error){
			var options=$("#admin_shengchanwancheng").datagrid('options');
			var pageNumber=options.pageNumber;
			var pageSize=options.pageSize;
			var request = { "pageNow":pageNumber,"pageSize":pageSize};
			var json_data= JSON.stringify(request);
			$.ajax({
				url:'productNote',
				timeout:3000,//超时3秒
				headers:{
					'userName':phone,
					'hashedPassword':password
				},
				type:"get",
				data:"jsondata="+json_data,
				success:function(data){
					data=data.rows;
					for(var i=0;i<data.length;i++){
						if(data[i].productOverdue==0){
								data[i].productOverdue="逾期";
						}else if(data[i].productOverdue==1){
								data[i].productOverdue="未逾期";
								}
					}
					success(data);
					$('#admin_shengchanwancheng').datagrid('loaded');
					$('#admin_shengchanwancheng').datagrid('resize');
				},
				error: function(XMLHttpRequest,status,errorThrown){//请求完成最终执行的函数
					var object=JSON.parse(XMLHttpRequest.responseText);
					$.messager.alert("提示",object.data,"info");
					$('#admin_shengchanwancheng').datagrid('loaded');
					$('#admin_shengchanwancheng').datagrid('resize');
			},		
			});
		},
	});
	
	//生产批次
	$("input[name='productBatch_add']").textbox({
		required:true,
		missingMessage:"请输入生产批次",
	});
	//开始时间
	$("input[name='productStartDate_add']").datebox({
		required:true,
		editable:false,
		missingMessage:"请选择开始时间",
	});
	$("input[name='productStartDate_update']").datebox({
		required:true,
		editable:false,
		missingMessage:"请选择开始时间",
	});
	//结束时间
	$("input[name='productEndDate_add']").datebox({
		required:true,
		editable:false,
		missingMessage:"请选择结束时间",
	});
	$("input[name='productEndDate_update']").datebox({
		required:true,
		editable:false,
		missingMessage:"请选择结束时间",
	});
	//实际生产产品总数
	$("input[name='productUsableNumber_add']").numberbox({
		required:true,
		missingMessage:"请输入实际生产产品总数",
	});
	$("input[name='productUsableNumber_update']").numberbox({
		required:true,
		missingMessage:"请输入实际生产产品总数",
	});
	//合格产品总数
	$("input[name='productTotalQuantity_add']").numberbox({
		required:true,
		missingMessage:"请输入合格产品总数",
	});
	$("input[name='productTotalQuantity_update']").numberbox({
		required:true,
		missingMessage:"请输入合格产品总数",
	});
	//是否逾期
	$("input[name='productOverdue_add']").textbox({
		required:true,
		editable:false,
		missingMessage:"请输入合格产品总数",
	});
	
	$("input[name='productOverdueExplain_add']").textbox({
		required:false,
		multiline:true,
		width:180,
		height:60,
	});
	$("input[name='productOverdueExplain_update']").textbox({
		required:false,
		multiline:true,
		width:180,
		height:60,
	});
	//定义用户添加弹窗
	$('#admin_shengchanwancheng_add').dialog({
		width:480,
		title:'添加用户',
		height:420,
		modal:true,
		closed:true,
		buttons:[{
			text:'添加',
			iconCls:'icon-ok',
			handler:function(){
				if($('#admin_shengchanwancheng_add').form('validate')){
					var productBatch_add=$('input[name=productBatch_add]').val();
					var productStartDate_add=$('input[name=productStartDate_add]').val();
					var productEndDate_add=$('input[name=productEndDate_add]').val();
					var productTotalQuantity_add=$('input[name=productTotalQuantity_add]').val();
					var productUsableNumber_add=$('input[name=productUsableNumber_add]').val();
					var productOverdue_add=$('#productOverdue_add').val();
					var productOverdueExplain_add=$('input[name=productOverdueExplain_add]').val();
					
					var productStartDate_add_parse=new Date(productStartDate_add.replace(/-/g,"/"));
					var productEndDate_add_parse=new Date(productEndDate_add.replace(/-/g,"/"));
					if(productStartDate_add_parse>productEndDate_add_parse){
						$.messager.alert("提示","结束时间应该大于或等于开始时间","info");
						return false;
					}
					var requestdata = {
						"productBatch" : productBatch_add,
						"productStartDate" : productStartDate_add,
						"productEndDate" : productEndDate_add,
						"productTotalQuantity" : productTotalQuantity_add,
						"productUsableNumber" : productUsableNumber_add,
						"productOverdue" : productOverdue_add,
						"productOverdueExplain" : productOverdueExplain_add
					};
					requestdata=JSON.stringify(requestdata);
					$.ajax(
							{
								url:'productNote',
								timeout:3000,
								type:'post',
								headers:{
									'userName':phone,
									'hashedPassword':password
								},
								data:
									"jsondata="+requestdata,
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
								$('#admin_shengchanwancheng_add').dialog('close').form('reset');
								$('#admin_shengchanwancheng').datagrid('reload');
								$('#admin_shengchanwancheng').datagrid('resize');
								},
								error: function(XMLHttpRequest,status,errorThrown){//请求完成最终执行的函数
									var object=JSON.parse(XMLHttpRequest.responseText);
									$.messager.alert("提示",object.data,"info");
									$.messager.progress('close');
									$('#admin_shengchanwancheng').datagrid('loaded');
									$('#admin_shengchanwancheng').datagrid('resize');
							},		
							}
						);
					}
				
				}
			},{
			text:'取消',
			iconCls:'icon-redo',
			handler:function(){
				$('#admin_shengchanwancheng_add').dialog('close').form('reset');
			}
		}],
	});
	//定义用户修改弹窗
	$('#admin_shengchanwancheng_update').dialog({
		width:480,
		title:'修改用户',
		height:420,
		modal:true,
		closed:true,
		buttons:[{
			text:'修改',
			iconCls:'icon-ok',
			handler:function(){
				if($('#admin_shengchanwancheng_update').form('validate')){
					var noteID_update = $('input[name=noteID_update]').val();
					var productBatch_update=$('input[name=productBatch_update]').val();
					var productStartDate_update=$('input[name=productStartDate_update]').val();
					var productEndDate_update=$('input[name=productEndDate_update]').val();
					var productTotalQuantity_update=$('input[name=productTotalQuantity_update]').val();
					var productUsableNumber_update=$('input[name=productUsableNumber_update]').val();
					var productOverdue_update=$('#productOverdue_update').val();
					var productOverdueExplain_update=$('input[name=productOverdueExplain_update]').val();
					
					var productStartDate_update_parse=new Date(productStartDate_update.replace(/-/g,"/"));
					var productEndDate_update_parse=new Date(productEndDate_update.replace(/-/g,"/"));
					if(productStartDate_update_parse>productEndDate_update_parse){
						$.messager.alert("提示","结束时间应该大于或等于开始时间","info");
						return false;
					}
					var requestdata = {
						"productBatch" : productBatch_update,
						"productStartDate" : productStartDate_update,
						"productEndDate" : productEndDate_update,
						"productTotalQuantity" : productTotalQuantity_update,
						"productUsableNumber" : productUsableNumber_update,
						"productOverdue" : productOverdue_update,
						"productOverdueExplain" : productOverdueExplain_update
					};
					requestdata=JSON.stringify(requestdata);
					$.ajax({
						url:"productNote/:"+noteID_update,
						timeout:3000,
						type:'put',
						headers:{
							'userName':phone,
							'hashedPassword':password
						},
						data:"jsondata="+requestdata,
						beforeSend:function(){
							$.messager.progress({
								text:'正在修改，请稍后...',
							});
						},
						success:function(data){	
							$.messager.progress('close');
								$.messager.show({
									title:'提示',
									msg:'修改成功',
									timeout:2000,
								});
								$('#admin_shengchanwancheng_update').dialog('close').form('reset');
								$('#admin_shengchanwancheng').datagrid('reload');
								$('#admin_shengchanwancheng').datagrid('resize');
						},
						error: function(XMLHttpRequest,status,errorThrown){//请求完成最终执行的函数
							var object=JSON.parse(XMLHttpRequest.responseText);
							$.messager.alert("提示",object.data,"info");
							$.messager.progress('close');
							$('#admin_shengchanwancheng').datagrid('loaded');
							$('#admin_shengchanwancheng').datagrid('resize');
					},							
					});
			}
			}
		},{
			text:'取消',
			iconCls:'icon-redo',
			handler:function(){
				$('#admin_shengchanwancheng_update').dialog('close').form('reset');
			
			}
		}],
	});
});