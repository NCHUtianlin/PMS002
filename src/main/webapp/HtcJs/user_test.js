$(function(){
	//接收账号信息
	var phone=$.query.get("phone");
	var password=$.query.get("password");
	//工具集
	user_test_manager_tools={
		refresh:function(){
				$('#user_test').datagrid('reload');
			},
		search:function(){
			$('#user_test').datagrid({
				loader:function(param,success,error){
					var options=$("#user_test").datagrid('options');
					var pageNow=options.pageNumber;
					var pageSize=options.pageSize;
					var search=$('#user_test_search').val();
					var request_data={
							"parameter":search,
							"pageNow":pageNow,
							"pageSize":pageSize
							};
					var jsondata=JSON.stringify(request_data);
					$.ajax({
						url:'testingdata',
						timeout:3000,
						type:"get",	
						headers:{
							'userName':phone,
							'hashedPassword':password
						},
						data:"jsondata="+jsondata,
						success:function(data){
							data=data.rows;
							for(var i=0;i<data.length;i++){
								if(data[i].testingResult==1){
										data[i].testingResult="成功";
									}else if(data[i].testingResult==0){
										data[i].testingResult="失败";
										}
							}
							success(data);
							$('#user_test').datagrid('loaded');
							$('#user_test').datagrid('resize');
						},
						error: function(XMLHttpRequest,status,errorThrown){//请求完成最终执行的函数
							var object=JSON.parse(XMLHttpRequest.responseText);
							$.messager.alert("提示",object.data,"info");
							$('#user_test').datagrid('loaded');
							$('#user_test').datagrid('resize');
					},		
					});
				},
			});
		},
	};
	
	$("#user_test").datagrid({
		width:'800',
		title:'测试列表',
		fit:true,
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
				title:'产品编号',
				sortable:true,
				fixed:true,
				halign:'center',
				width:100,
				
			},
			{
				field:'testDate',
				title:'测试时间',
				sortable:true,
				fixed:true,
				halign:'center',
				width:100,
				
			},
			{
				field:'result',
				title:'测试结果',
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
		toolbar:"#user_test_manager_tool",
		loader:function(param,success,error){
			var options=$("#user_test").datagrid('options');
			var pageNow=options.pageNumber;
			var pageSize=options.pageSize;
			var request = {
					"result":1,
					"pageNow":pageNow,
					"pageSize":pageSize
				};
			var json_data= JSON.stringify(request);
			$.ajax({
				url:'testingdata',
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
						if(data[i].result==1){
								data[i].result="成功";
							}else if(data[i].result==0){
								data[i].result="失败";
								}
					}
					success(data);
					$('#user_test').datagrid('loaded');
					$('#user_test').datagrid('resize');
				},
				error: function(XMLHttpRequest,status,errorThrown){//请求完成最终执行的函数
					var object=JSON.parse(XMLHttpRequest.responseText);
					$.messager.alert("提示",object.data,"info");
					$('#user_test').datagrid('loaded');
					$('#user_test').datagrid('resize');
			},
			});
		},
	});
});