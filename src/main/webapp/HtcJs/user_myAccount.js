$(function(){
	//接收账号信息
	var phone=$.trim($.query.get("phone"));
	var password=$.trim($.query.get("password"));
	console.log(password);
	//当前用户id
	var user_ID="";
		//工具集
		user_myAccount_manager_tools={
			//刷新界面
			refresh:function(){
					$('#user_myAccount').datagrid('reload');
				},
			//撤销选中
			redo:function(){
					$('#user_myAccount').datagrid('unselectAll');
			},
			//更新选中记录
			update:function(){
					var rows=$('#user_myAccount').datagrid('getSelections');
					if(rows.length>1){
						$.messager.alert("警告!","每次只能修改一条记录!","warning");
					}else if(rows.length==0){
						$.messager.alert("警告!","请至少选择一条记录!","warning");
					}else if(rows.length==1){
						if(rows[0].authority=='管理员'){
							rows[0].authority=1;
						}else if(rows[0].authority=='普通用户'){
							rows[0].authority=0;
						}
						if(rows[0].workState=='在职'){
							rows[0].workState=1;
						}else if(rows[0].workState=='离职'){
							rows[0].workState=0;
						}
							$('#user_myAccount_account_update').form('load',{
								user_myAccount_userID_update:rows[0]._id,
								user_myAccount_name_update:rows[0].name,
								user_myAccount_email_update:rows[0].email,
								user_myAccount_phone_update:rows[0].phone,
							}).dialog('open');				
						}
			},
		};
		
		//我的账户数据载入
		$("#user_myAccount").datagrid({
			width:'800',
			title:'我的账户',
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
			//拒绝服务器端排序
			remoteSort:false,
			rownumbers:true,
			fitColumns:true,
			//工具集添加到datagrid
			toolbar:"#user_myAccount_manager_tool",
			//自定义ajax访问远程服务器加载数据
			loader:function(param,success,error){
				var request = {"queryTab":"1"};
				var json_data= JSON.stringify(request);
				$.ajax({
									type:"get",	
									headers:{
										'userName':phone,
										'hashedPassword':password
									},
									url:'user',
									timeout:3000,
									data:"jsondata="+json_data,
									success:function(data){
										data = "["+data+"]";
										data=JSON.parse(data);
										if(data[0].authority==1){
													data[0].authority="管理员";
												}else if(data[0].authority==0){
													data[0].authority="普通用户";
													}
											if(data[0].workState==1){
												data[0].workState="在职";
											}else if(data[0].workState==0){
												data[0].workState="离职";
											}
										user_ID=data[0]._id;
										success(data);
										$('#user_myAccount').datagrid('loaded');
										$('#user_myAccount').datagrid('resize');
									},
									error: function(XMLHttpRequest,status,errorThrown){//请求完成最终执行的函数
										var object=JSON.parse(XMLHttpRequest.responseText);
										$.messager.alert("提示",object.data,"info");
										$('#user_myAccount').datagrid('loaded');
										$('#user_myAccount').datagrid('resize');
								},	
								});
			},
		});
		$('#user_myAccount_name_update').textbox({
			editable:false,
		});
		//验证输入框是否满足条件
		$('#user_myAccount_email_update').textbox({
				required:true,
				validType:"email",
				missingMessage:"请输入邮箱地址",
				invalidMessage:"请输入正确的邮箱地址",
			});
		$('#user_myAccount_phone_update').numberbox({
				required:true,
				validType:"length[11,11]",
				missingMessage:"请输入联系电话",
				invalidMessage:"请输入正确的电话号码",
			});
		//定义修改弹窗
		$('#user_myAccount_account_update').dialog({
			width:480,
			title:'您是用户，即将修改您的账户',
			height:420,
			modal:true,
			closed:true,
			buttons:[{
				text:'确定',
				iconCls:'icon-ok',
				handler:function(){
					if($('#user_myAccount_account_update').form('validate')){
						var userID_update=$('#user_myAccount_userID_update').val();
						var email_update=$('#user_myAccount_email_update').val();
						var phone_update=$('#user_myAccount_phone_update').val();
						
						var requestdata = {
							"email" : email_update,
							"phone" : phone_update,
						};
						requestdata=JSON.stringify(requestdata);
						$.ajax({
							url:"user/:"+userID_update,
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
							success:function(data){
								$.messager.progress('close');
									$.messager.show({
										title:'提示',
										msg:'修改成功',
										timeout:2000,
									});
									if(phone!=phone_update){
										$.messager.alert("提示","您修改了您的电话号码,即将重新登录",'info');
										phone="";
										password="";
										setTimeout("location.href='main'",3000);
										window.opener=null;
										window.close();
										}
									else{
										$('#user_myAccount_account_update').dialog('close').form('reset');
										$('#user_myAccount').datagrid('reload');
										$('#user_myAccount').datagrid('resize');
									}
							},
							error: function(XMLHttpRequest,status,errorThrown){//请求完成最终执行的函数
								var object=JSON.parse(XMLHttpRequest.responseText);
								$.messager.alert("提示",object.data,"info");
								$.messager.progress("close");
								$('#user_myAccount').datagrid('loaded');
								$('#user_myAccount').datagrid('resize');
							},				
						});
				}
				}
			},{
				text:'取消',
				iconCls:'icon-redo',
				handler:function(){
					$('#user_myAccount_account_update').dialog('close').form('reset');
				
				}
			}],
		});
		//定义原密码输入框
		$("#user_myAccount_password_update_older").passwordbox({
			required:true,
			prompt:'请输入原始密码',
			validType:'length[6,20]',
			missingMessage:'请输入原始密码',
		});
		//定义新密码输入框
		$("#user_myAccount_password_update_newer").passwordbox({
			required:true,
			prompt:'请输入密码',
			validType:'length[6,20]',
			missingMessage:'请输入密码',
			invalidMessage:'密码长度至少为6，最大为20',
		});
		//确认密码输入框
		$("#user_myAccount_password_update_confirm").passwordbox({
			required:true,
			prompt:'确认密码',
			validType:'length[6,20]',
			missingMessage:'请再次输入密码',
			invalidMessage:'密码长度至少为6，最大为20',
		});
		//点击修改密码首先执行用户确认操作
		$("#user_myAccount_password_update_button").click(
				function(e){
					$("#user_myAccount_password_accountConfirm").dialog("open");
				}
		);
		//定义修改密码确认用户form
		$('#user_myAccount_password_accountConfirm').dialog({
			width:480,
			title:'确定用户',
			height:280,
			modal:true,
			closed:true,
			buttons:[{
				text:'确定',
				iconCls:'icon-ok',
				handler:function(){
					if($("#user_myAccount_password_accountConfirm").form("validate")){
						var password_update_older=$.trim($("#user_myAccount_password_update_older").val());
						
						var password_update_older_parse=parseInt(password_update_older);
						var password_parse=parseInt(password);
						if(password_update_older_parse == password_parse){
							$('#user_myAccount_password_accountConfirm').dialog('close').form('reset');
							$("#user_myAccount_password_update").dialog("open");
						}else{
							$.messager.alert("提示","用户验证失败，请重新输入","info");
						}
					}
				}
			},{
				text:'取消',
				iconCls:'icon-redo',
				handler:function(){
					$('#user_myAccount_password_accountConfirm').dialog('close').form('reset');
				}
			}],
		});
		//定义修改密码提交框
		$('#user_myAccount_password_update').dialog({
			width:480,
			title:'修改密码',
			height:480,
			modal:true,
			closed:true,
			buttons:[{
				text:'确定',
				iconCls:'icon-ok',
				handler:function(){
					if($("#user_myAccount_password_update").form("validate")){
						var password_update_newer= $.trim($("#user_myAccount_password_update_newer").val());
						var password_update_confirm= $.trim($("#user_myAccount_password_update_confirm").val());
						var password_update_newer_parse=parseInt(password_update_newer);
						var password_update_confirm_parse=parseInt(password_update_confirm);
						if(password_update_newer_parse==password_update_confirm_parse){//确认密码
									//修改用户自己的密码
									var requestdata = {
											"password" : password_update_newer,
										};
									requestdata=JSON.stringify(requestdata);
									$.ajax({
										url:"user/:"+user_ID,
										type:'put',
										timeout:3000,
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
										success:function(data){
											$.messager.progress('close');
												$.messager.show({
													title:'提示',
													msg:'修改成功',
													timeout:2000,
												});
													$.messager.alert("提示","您修改了您的密码,即将重新登录",'info');
													phone="";
													password="";
													setTimeout("location.href='main'",3000);
													window.opener=null;
													window.close();
										},
										error: function(XMLHttpRequest,status,errorThrown){//请求完成最终执行的函数
											var object=JSON.parse(XMLHttpRequest.responseText);
											$.messager.alert("提示",object.data,"info");
											$.messager.progress("close");
											$('#user_myAccount').datagrid('loaded');
											$('#user_myAccount').datagrid('resize');
										},		
									});
							}else{
								$.messager.alert("提示","两次密码输入不一致，请重新输入。","info");
							}
					}
				}
			},{
				text:'取消',
				iconCls:'icon-redo',
				handler:function(){
					$('#user_myAccount_password_update').dialog('close').form('reset');
				}
			}],
		});
});