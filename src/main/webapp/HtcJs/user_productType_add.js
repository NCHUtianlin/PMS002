$(function(){
	//工具集
	user_productType_manager_tools={
		refresh:function(){
				$('#user_productType').datagrid('reload');
			},
		
		search:function(){
			$('#user_productType').datagrid('load',{
				user:$("input[name='user']").val(),
				date_from:$("input[name='date_from']").val(),
				date_to:$("input[name='date_to']").val(),
			});	
		},
		
		add:function(){
			$('#user_productType_add').dialog('open');
			$('input[name="username"]').focus();
		},
		
		redo:function(){
				$('#user_productType').datagrid('unselectAll');
		},
		
		update:function(){
				var rows=$('#user_productType').datagrid('getSelections');
				if(rows.length>1){
					$.messager.alert("警告!","每次只能修改一条记录!","warning");
				}else if(rows.length==0){
					$.messager.alert("警告!","请至少选择一条记录!","warning");
				}else if(rows.length==1){
						$('#user_productType_update').form('load',{
							id_update:rows[0].id,
							username_update:rows[0].user,
							email_update:rows[0].email,
							phone_update:rows[0].phone,
							workState_update:'hha',
						}).dialog('open');				
					}
		},
		
		remove:function(){
			var rows=$('#user_productType').datagrid('getSelections');
				if(rows.length>0){
					$.messager.confirm('确认选择','您确定删除所选择的记录吗？',function(flag){
						if(flag){
							var ids=[];
							for(var i=0;i<rows.length;i++){
								ids.push(rows[i].id);
							}	
							console.log(ids.join(','));
							$.ajax({
								url:'content.json',
								type:'post',
								data:{
									ids:ids.join(','),
								},
								beforeSend:function(){
									$('#user_productType').datagrid('loading');
								},
								success:function(data){
									if(data){
										$('#user_productType').datagrid('loaded');
										$('#user_productType').datagrid('load');
										$('#user_productType').datagrid('unselectAll');
										$.messager.show({
											title:'消息提醒',
											msg:"用户删除成功!",
											timeout:2000,
										});
									}
								},
							});
						}
					});
					
				}else{
					$.messager.alert("消息提示","请选择要删除的记录！","info");
				}
		}
	};
	
	$("#user_productType").datagrid({
		width:'800',
		title:'用户列表',
		iconCls:'icon-search',
		url:'content.php',
		method:'post',
		fit:true,
		columns:[[
		{
				field:'id',
				title:'编号',
				sortable:true,
				fixed:true,
				halign:'center',
				width:100,
				checkbox:true,
			},
			{
				field:'user',
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
				field:'date',
				title:'注册时间',
				sortable:true,
				halign:'center',
				width:100,
				
			},
		]],
		pagination:true,
		pageSize:5,
		pageList:[5,10,15,20],
		sortName:'date',
		sortOrder:'desc',
		remoteSort:false,
		//提交额外数据
		queryParams:{
			username:"管理员",
		},
		rownumbers:true,
		fitColumns:true,
		toolbar:"#user_productType_manager_tool",
	});
	
	//定义用户添加弹窗
	$('#user_productType_add').dialog({
		width:480,
		title:'添加用户',
		height:420,
		modal:true,
		closed:true,
		buttons:[{
			text:'添加',
			iconCls:'icon-ok',
			handler:function(){
				if($('#user_productType_add').form('validate')){
					$.ajax(
							{
								url:'test.json',
								Type:'post',
								data:{
									id:$('input[name=id]').val(),
									username:$('input[name=username]').val(),
									password:$('input[name=password]').val(),
									email:$('input[name=email]').val(),
									phone:$('input[name=phone]').val(),
									workState:$('input[name=workState]').val()
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
								$('#user_productType_add').dialog('close').form('reset');
								$('#user_productType').datagrid('reload');
								},
							}
						);
					}
				
				}
			},{
			text:'取消',
			iconCls:'icon-redo',
			handler:function(){
				$('#user_productType_add').dialog('close').form('reset');
			}
		}],
	});
	//定义用户修改弹窗
	$('#user_productType_update').dialog({
		width:480,
		title:'修改用户',
		height:420,
		modal:true,
		closed:true,
		buttons:[{
			text:'修改',
			iconCls:'icon-ok',
			handler:function(){
				if($('#user_productType_update').form('validate')){
					$.ajax({
						url:"content.json",
						type:'post',
						data:{
							id:$('input[name=id_update]').val(),
							username:$('input[name=username_update]').val(),
							password:$('input[name=password_update]').val(),
							email:$('input[name=email_update]').val(),
							phone:$('input[name=phone_update]').val(),
						},
						beforeSend:function(){
							$.messager.progress({
								text:'正在修改，请稍后...',
							});
						},
						success:function(data){
							
							$.messager.progress('close');
							if(data!=''){
								$.messager.show({
									title:'提示',
									msg:'修改成功',
									timeout:2000,
								});
								$('#user_productType_update').dialog('close').form('reset');
								$('#user_productType').datagrid('reload');
							}else{
								$.messager.alert('修改失败!','未能成功修改用户信息!','warning');
							}
						}				
					});
			}
			}
		},{
			text:'取消',
			iconCls:'icon-redo',
			handler:function(){
				$('#user_productType_update').dialog('close').form('reset');
			
			}
		}],
	});
});