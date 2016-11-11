$(function(){
	//接收账号信息
	var phone=$.query.get("phone");
	var password=$.query.get("password");
	//工具集
	admin_alluser_manager_tools={
		refresh:function(){
				$('#admin_alluser').datagrid('reload');
			},
		search:function(){
			$('#admin_alluser').datagrid({
				loader:function(param,success,error){
					var queryTab=2;
					var search=$('#admin_alluser_search').val();
					var request_data={"queryTab":queryTab,"parameter":search};
					var jsondata=JSON.stringify(request_data);
					$.ajax({
						url:'user',
						timeou:3000,
						type:"get",	
						headers:{
							'userName':phone,
							'hashedPassword':password
						},
						data:"jsondata="+jsondata,
						success:function(data){
							data=data.rows;
							for(var i=0;i<data.length;i++){
								if(data[i].authority==1){
										data[i].authority="管理员";
									}else if(data[i].authority==0){
										data[i].authority="普通用户";
										}
								if(data[i].workState==1){
									data[i].workState="在职";
								}else if(data[i].workState==0){
									data[i].workState="离职";
								}
							}
							success(data);
							$('#admin_alluser').datagrid('loaded');
							$('#admin_alluser').datagrid('resize');
						},
						error: function(XMLHttpRequest,status,errorThrown){//请求完成最终执行的函数
							var object=JSON.parse(XMLHttpRequest.responseText);
							$.messager.alert("提示",object.data,"info");
							$('#admin_alluser').datagrid('loaded');
							$('#admin_alluser').datagrid('resize');
					},		
					});
				},
			});
		},
		add:function(){
			$('#admin_alluser_user_add').dialog('open');
		},
		
		redo:function(){
				$('#admin_alluser').datagrid('unselectAll');
		},
		
		update:function(){
				var rows=$('#admin_alluser').datagrid('getSelections');
				if(rows.length>1){
					$.messager.alert("警告!","每次只能修改一条记录!","warning");
				}else if(rows.length==0){
					$.messager.alert("警告!","请至少选择一条记录!","warning");
				}else if(rows.length==1){
						if (rows[0].authority == '管理员') {
							rows[0].authority = 1;
						} else if (rows[0].authority == '普通用户') {
							rows[0].authority = 0;
						}
						if(rows[0].workState=='在职'){
							rows[0].workState= 1;
						} else if (rows[0].workState == '离职') {
							rows[0].workState = 0;
						}
						$('#admin_alluser_user_update').form('load',{
							admin_alluser_userID_update:rows[0]._id,
							admin_alluser_name_update:rows[0].name,
							admin_alluser_password_update:rows[0].password,
							admin_alluser_email_update:rows[0].email,
							admin_alluser_phone_update:rows[0].phone,
							admin_alluser_authority_update:rows[0].authority,
							admin_alluser_workState_update:rows[0].workState,
						}).dialog('open');
					}
		},
		remove:function(){
			var rows=$('#admin_alluser').datagrid('getSelections');
				if(rows.length==1){
					$.messager.confirm('确认选择','您确定删除所选择的记录吗？',function(flag){
						if(flag){
							var ids=rows[0]._id;
							$.ajax({
								url:'user/:'+ids,
								timeout:3000,
								type:'delete',
								data:"",
								headers:{
									'userName':phone,
									'hashedPassword':password
								},
								success:function(data){
										$('#admin_alluser').datagrid('loaded');
										$('#admin_alluser').datagrid('reload');
										$('#admin_alluser').datagrid('unselectAll');
										$('#admin_alluser').datagrid('resize');
										$.messager.show({
											title:'消息提醒',
											msg:"用户删除成功!",
											timeout:2000,
										});
								},
								error: function(XMLHttpRequest,status,errorThrown){//请求完成最终执行的函数
									var object=JSON.parse(XMLHttpRequest.responseText);
									$.messager.alert("提示",object.data,"info");
									$('#admin_alluser').datagrid('loaded');
									$('#admin_alluser').datagrid('resize');
							},		
							});
						}
					});
				}else{
					$.messager.alert("消息提示","请选择要删除的记录！","info");
				}
		},
	};
	$("#admin_alluser").datagrid({
		width:'800',
		title:'所有用户列表',
		fit:true,
		border:false,
		columns:[[
			{
				field:'_id',
				title:'编号',
				sortable:true,
				fixed:true,
				halign:'center',
				width:100,
				checkbox:true,
			},
			{
				field:'name',
				title:'用户名',
				sortable:true,
				fixed:true,
				halign:'center',
				width:100,
				
			},
			
			{
				field:'email',
				title:'邮箱',
				sortable:true,
				halign:'center',
				width:100,
				
			},
			{
				field:'phone',
				title:'联系方式',
				sortable:true,
				halign:'center',
				width:100,
				
			},
			{
				field:'authority',
				title:'权限',
				sortable:true,
				halign:'center',
				width:100,
				
			},
			{
				field:'workState',
				title:'工作状态',
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
		toolbar:"#admin_alluser_manager_tool",
		loader:function(param,success,error){
			var options=$("#admin_alluser").datagrid('options');
			var pageNumber=options.pageNumber;
			var pageSize=options.pageSize;
			var request = { "queryTab":2,"pageNumber":pageNumber,"pageSize":pageSize};
			var json_data= JSON.stringify(request);
			$.ajax({
					url:'user',
					timeout:3000,
					type:"get",	
					headers:{
						'userName':phone,
						'hashedPassword':password
					},
					data:"jsondata="+json_data,
					success:function(data){
						data=data.rows;
						for(var i=0;i<data.length;i++){
							if(data[i].authority==1){
									data[i].authority="管理员";
								}else if(data[i].authority==0){
									data[i].authority="普通用户";
									}
							if(data[i].workState==1){
								data[i].workState="在职";
							}else if(data[i].workState==0){
								data[i].workState="离职";
							}
						}
						success(data);	
							$('#admin_alluser').datagrid('loaded');
							$('#admin_alluser').datagrid('resize');
						},
						error: function(XMLHttpRequest,status,errorThrown){//请求完成最终执行的函数
							var object=JSON.parse(XMLHttpRequest.responseText);
							$.messager.alert("提示",object.data,"info");
							$.messager.progress('close');
							$('#admin_alluser').datagrid('loaded');
							$('#admin_alluser').datagrid('resize');
					},		
					});
		},
	});
	//验证输入框是否满足条件
		$('#admin_alluser_name_add').textbox({
			required:true,
			missingMessage:"请输入用户名",
		});
		$('#admin_alluser_name_update').textbox({
			required:true,
			editable:false,
			missingMessage:"请输入用户名",
		});
		$('#admin_alluser_email_add').textbox({
			required:true,
			validType:"email",
			missingMessage:"请输入邮箱",
			invalidMessage:"请输入正确的邮箱",
		});
		$('#admin_alluser_phone_add').numberbox({
			required:true,
			validType:"length[11,11]",
			missingMessage:"请输入联系电话",
			invalidMessage:"请输入正确的电话号码",
		});
		$('#admin_alluser_authority_add').combobox({
			required:true,
			width:120,
			editable:false,
			missingMessage:"请输入权限",
		});
		//修改
		$('#admin_alluser_email_update').textbox({
			required:true,
			validType:"email",
			missingMessage:"请输入邮箱",
			invalidMessage:"请输入正确的邮箱",
		});
		$('#admin_alluser_phone_update').numberbox({
			required:true,
			validType:"length[11,11]",
			missingMessage:"请输入联系电话",
			invalidMessage:"请输入正确的电话号码",
		});
		$('#admin_alluser_authority_update').combobox({
			required:true,
			width:120,
			editable:false,
			missingMessage:"请输入权限",
		});
		$('#admin_alluser_workState_update').combobox({
			required:true,
			width:120,
			editable:false,
			missingMessage:"请输入在职状态",
		});
	//定义用户添加弹窗
	$('#admin_alluser_user_add').dialog({
		width:480,
		title:'添加用户',
		height:420,
		modal:true,
		closed:true,
		buttons:[{
			text:'添加',
			iconCls:'icon-ok',
			handler:function(){
				if($('#admin_alluser_user_add').form('validate')){
					var admin_alluser_name_add=$.trim($('#admin_alluser_name_add').val());
					var admin_alluser_password_add=$.trim($('#admin_alluser_password_add').val());
					var admin_alluser_email_add=$.trim($('#admin_alluser_email_add').val());
					var admin_alluser_phone_add=$.trim($('#admin_alluser_phone_add').val());
					var admin_alluser_authority_add=$('#admin_alluser_authority_add').val();
						admin_alluser_authority_add_parse = parseInt(admin_alluser_authority_add);
					var requestdata = {
						"name" : admin_alluser_name_add,
						"password" : admin_alluser_password_add,
						"email" : admin_alluser_email_add,
						"phone" : admin_alluser_phone_add,
						"authority" : admin_alluser_authority_add_parse
					};
					requestdata=JSON.stringify(requestdata);
					$.ajax({
								url:"user",
								type:'post',
								timeout:3000,
								headers:{
									'userName':phone,
									'hashedPassword':password
								},
								data:"jsondata="+requestdata,
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
								$('#admin_alluser_user_add').dialog('close').form('reset');
								$('#admin_alluser').datagrid('reload');
								$('#admin_alluser').datagrid('resize');
								},
								error: function(XMLHttpRequest,status,errorThrown){//请求完成最终执行的函数
									var object=JSON.parse(XMLHttpRequest.responseText);
									$.messager.alert("提示",object.data,"info");
									$.messager.progress('close');
							},		
							}
						);
					}
				
				}
			},{
			text:'取消',
			iconCls:'icon-redo',
			handler:function(){
				$('#admin_alluser_user_add').dialog('close').form('reset');
			}
		}],
	});
	//定义用户修改弹窗
	$('#admin_alluser_user_update').dialog({
		width:480,
		title:'修改用户',
		height:420,
		modal:true,
		closed:true,
		buttons:[{
			text:'修改',
			iconCls:'icon-ok',
			handler:function(){
				if($('#admin_alluser_user_update').form('validate')){
					var userID_update=$('#admin_alluser_userID_update').val();
					var name_update=$('#admin_alluser_name_update').val();
					var email_update=$('#admin_alluser_email_update').val();
					var phone_update=$('#admin_alluser_phone_update').val();
					var authority_update=$('#admin_alluser_authority_update').val();
					var workState_update=$('#admin_alluser_workState_update').val();
					authority_update = parseInt(authority_update);
					workState_update = parseInt(workState_update);
					var requestdata={
							"_id":userID_update,
							"name":name_update,
							"email":email_update,
							"phone":phone_update,
							"authority":authority_update,
							"workState":workState_update
							};
					requestdata=JSON.stringify(requestdata);
					$.ajax({
						url:'user/:'+userID_update,
						type:'put',
						timeout:3000,
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
								$('#admin_alluser_user_update').dialog('close').form('reset');
								$('#admin_alluser').datagrid('reload');
								$('#admin_alluser').datagrid('resize');
						},
						error: function(XMLHttpRequest,status,errorThrown){//请求完成最终执行的函数
							var object=JSON.parse(XMLHttpRequest.responseText);
							$.messager.alert("提示",object.data,"info");
							$.messager.progress('close');
							$('#admin_alluser').datagrid('reload');
							$('#admin_alluser').datagrid('resize');
					},				
					});
			}
			}
		},{
			text:'取消',
			iconCls:'icon-redo',
			handler:function(){
				$('#admin_alluser_user_update').dialog('close').form('reset');
			
			}
		}],
	});
});