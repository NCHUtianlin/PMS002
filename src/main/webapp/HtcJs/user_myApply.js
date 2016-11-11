$(function(){
	//接收账号信息
	var phone = $.query.get("phone");
	var password = $.query.get("password");
	
	//工具集
	user_myApply_manager_tools={
		refresh:function(){
				$('#user_myApply').datagrid('reload');
			},
		search:function(){
			$('#user_myApply').datagrid({
				loader:function(param, success, error) {
					var options = $("#user_myApply").datagrid('options');
					var pageNow = options.pageNumber;
					var page_Size = options.pageSize;
					var search = $("#user_myApply_search").val();
					var date_from=$("#user_myApply_date_from").datebox('getValue');
					var date_to=$("#user_myApply_date_to").datebox('getValue');
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
						"pageNow":pageNow,
						"page_Size":page_Size,
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
									var product_objects=data.rows;
									for(var i=0;i<product_objects.length;i++){
										if (product_objects[i].checkResult == 0) {
											product_objects[i].checkResult = "拒绝";
										} else if (product_objects[i].checkResult == 1) {
											product_objects[i].checkResult = "通过";
										} else if (product_objects[i].checkResult == 2) {
											product_objects[i].checkResult = "未审核";
										}
									}
									success(product_objects);
									$('#user_myApply').datagrid('loaded');
									$('#user_myApply').datagrid('resize');
								},
								error : function(XMLHttpRequest, status,errorThrown) {// 请求完成最终执行的函数
									var object = JSON.parse(XMLHttpRequest.responseText);
									$.messager.alert("提示",object.data,"info");
									
									$('#user_myApply').datagrid('loaded');
									$('#user_myApply').datagrid('resize');
								},
							});
				},
			});	
		},
		
		add:function(){
			$('#user_myApply_apply_add').dialog('open');
		},
		
		redo:function(){
				$('#user_myApply').datagrid('unselectAll');
		},
		
		update : function() {
			var rows = $('#user_myApply').datagrid('getSelections');
			if (rows.length > 1) {
				$.messager.alert("警告!", "每次只能修改一条记录!", "warning");
			} else if (rows.length == 0) {
				$.messager.alert("警告!", "请至少选择一条记录!", "warning");
			} else if (rows.length == 1) {
				if (rows[0].checkResult == "拒绝") {
					rows[0].checkResult = 0;
				} else if (rows[0].checkResult == "通过") {
					rows[0].checkResult = 1;
				} else if (rows[0].checkResult == "未审核") {
					rows[0].checkResult = 2;
				}
//				$("#myApply_productTypeID_update").combobox('setValue',rows[0]._id);
//				$("#myApply_productTypeID_update").combobox('setText',rows[0].productTypeName);
				$('#user_myApply_apply_update').form('load', {
					user_myApply_reportID_update : rows[0]._id,
					//这里是产品类型名称，没有产品类型ID,仅仅提供显示效果
					user_myApply_productTypeID_update:rows[0].productTypeName,
					user_myApply_reportQuantity_update : rows[0].reportQuantity,
					user_myApply_reportStartDate_update : rows[0].reportStartDate,
					user_myApply_reportEndDate_update : rows[0].reportEndDate,
					user_myApply_reportCompleteRate_update : rows[0].reportCompleteRate,
					user_myApply_producer_update : rows[0].producer,
					user_myApply_checkResult_update : rows[0].checkResult,
					user_myApply_checkExplain_update : rows[0].checkExplain,
				}).dialog('open');
			}
		},
	};
	
	$("#user_myApply").datagrid({
		width:'800',
		title:'用户列表',
		fit:true,
		border:false,
		columns:[ [ {
			field : '_id',
			title : '申报编号',
			sortable : true,
			fixed : true,
			halign : 'center',
			width : 100,
			checkbox : true,
		}, {
			field : 'productTypeName',
			title : '产品类型',
			sortable : true,
			halign : 'center',
			width : 100,

		}, {
			field : 'reportQuantity',
			title : '计划生产数量',
			sortable : true,
			halign : 'center',
			width : 100,

		}, {
			field : 'reportStartDate',
			title : '生产开始时间',
			sortable : true,
			halign : 'center',
			width : 100,

		}, {
			field : 'reportEndDate',
			title : '生产结束时间',
			sortable : true,
			halign : 'center',
			width : 100,

		}, {
			field : 'reportCompleteRate',
			title : '计划完成率',
			sortable : true,
			halign : 'center',
			width : 100,

		}, {
			field : 'producer',
			title : '生产商',
			sortable : true,
			halign : 'center',
			width : 100,

		}, {
			field : 'reportPerson',
			title : '申报人',
			sortable : true,
			halign : 'center',
			width : 100,

		}, {
			field : 'reportTime',
			title : '申报时间',
			sortable : true,
			halign : 'center',
			width : 100,

		}, {
			field : 'productBatch',
			title : '生产批次',
			sortable : true,
			fixed : true,
			halign : 'center',
			width : 100,

		}, {
			field : 'checkDate',
			title : '审核时间',
			sortable : true,
			halign : 'center',
			width : 100,

		}, {
			field : 'checkResult',
			title : '审核结果',
			sortable : true,
			halign : 'center',
			width : 100,

		}, {
			field : 'checkExplain',
			title : '审核说明',
			sortable : true,
			halign : 'center',
			width : 100,

		}, {
			field : 'checkPerson',
			title : '审核人员',
			sortable : true,
			halign : 'center',
			width : 100,

		}, 
		]],
		pagination:true,
		pageSize:10,
		pageList:[10,15,20],
		remoteSort:false,
		rownumbers:true,
		fitColumns:true,
		toolbar:"#user_myApply_manager_tool",
		loader:function(param,success,error){
			var options=$("#user_myApply").datagrid('options');
			var pageNow=options.pageNumber;
			var pageSize=options.pageSize;
			var request = {"queryTab":1, "pageNumber":pageNow,"pageSize":pageSize};
			var json_data= JSON.stringify(request);
			$.ajax({
				url:'report',
				timeout:3000,
				type:"get",	
				headers:{
					'userName':phone,
					'hashedPassword':password
				},
				data:"jsondata="+json_data,
				success:function(data){
				 var product_objects = data.rows;
					for (var i = 0; i < product_objects.length; i++) {
						if (product_objects[i].checkResult == 0) {
							product_objects[i].checkResult = "拒绝";
						} else if (product_objects[i].checkResult == 1) {
							product_objects[i].checkResult = "通过";
						} else if (product_objects[i].checkResult == 2) {
							product_objects[i].checkResult = "未审核";
						}
					}
					success(product_objects);
				},
				error : function(XMLHttpRequest, status,errorThrown) {// 请求完成最终执行的函数
					var object = JSON.parse(XMLHttpRequest.responseText);
					$.messager.alert("提示",object.data,"info");
					$('#user_myApply').datagrid('loaded');
					$('#user_myApply').datagrid('resize');
				},
			});														
		},
	});
	
	$("#user_myApply_productTypeID_add").combobox({
		required:true,
		editable:false,
		missingMessage:"请选择产品类型",
	});
	$("#user_myApply_date_from").datebox({
		editable:false,
	});
	$("#user_myApply_date_to").datebox({
		editable:false,
	});
//	$("#myApply_productTypeID_update").combobox({
//		required:true,
//		editable:false,
//		missingMessage:"请选择产品类型",
//	});
	$("#user_myApply_productTypeID_update").textbox({
		required:true,
		editable:false,
	});
	//开始时间
	$("#user_myApply_reportStartDate_add").datebox({
		required:true,
		editable:false,
		missingMessage:"请选择开始时间",
	});
	$("#user_myApply_reportStartDate_update").datebox({
		required:true,
		editable:false,
		missingMessage:"请选择开始时间",
	});
	//结束时间
	$("#user_myApply_reportEndDate_add").datebox({
		required:true,
		editable:false,
		missingMessage:"请选择结束时间",
	});
	$("#user_myApply_reportEndDate_update").datebox({
		required:true,
		editable:false,
		missingMessage:"请选择结束时间",
	});
	//产品数量检测
	$("#user_myApply_reportQuantity_add").numberbox({
		required:true,
		missingMessage:"请输入产品数量",
		});
	$("#user_myApply_reportQuantity_update").numberbox({
		required:true,
		missingMessage:"请输入产品数量",
		});
	//产品完成率输入框检测
	$("#user_myApply_reportCompleteRate_add").textbox({
		required:true,
		missingMessage:"请输入产品完成率",
	});
	$("#user_myApply_reportCompleteRate_update").textbox({
		required:true,
		missingMessage:"请输入产品完成率",
	});
	//产品生产商输入框检测
	$("#user_myApply_producer_add").textbox({
		required:true,
		missingMessage:"请输入产品生产商",
	});
	$("#user_myApply_producer_update").textbox({
		required:true,
		missingMessage:"请输入产品生产商",
	});
	//审核说明
	$("#user_myApply_checkExplain_update").textbox({
		required:false,
		multiline:true,
		width:180,
		height:60,
	});
	// 获取产品类型
	$("#user_myApply_productTypeID_add").combobox({
		valueField : '_id',
		textField : 'productTypeName',
		rquired:true,
		editable:false,
		missingMessage:"请选择产品类型",
		loader : function(param, success, error) {
			var request = {"queryTab":1};
			var json_data= JSON.stringify(request);
			$.ajax({
				url : 'productType',
				timeout:3000,
				type : "get",
				headers : {
					'userName' : phone,
					'hashedPassword' : password
				},
				data : "jsondata="+json_data,
				success : function(data) {
					data=data.rows;
					success(data);
				},
				error : function(XMLHttpRequest, status, errorThrown) {// 请求完成最终执行的函数
					var object = JSON.parse(XMLHttpRequest.responseText);
					$.messager.alert("提示",object.data,"info");
					$('#user_myApply').datagrid('loaded');
					$('#user_myApply').datagrid('resize');
				},
			});
		},
	});	
	
//	$("#myApply_productTypeID_update").combobox({
//		valueField : '_id',
//		textField : 'productTypeName',
//		rquired:true,
//		editable:false,
//		missingMessage:"请选择产品类型",
//		loader : function(param, success, error) {
//			var request = {"queryTab":1};
//			var json_data= JSON.stringify(request);
//			$.ajax({
//				url : 'productType',
//				timeout:3000,
//				type : "get",
//				headers : {
//					'userName' : phone,
//					'hashedPassword' : password
//				},
//				data : "jsondata="+json_data,
//				success : function(data) {
//					data=data.rows;
//					success(data);
//				},
//				error : function(XMLHttpRequest, status, errorThrown) {// 请求完成最终执行的函数
//					var object = JSON.parse(XMLHttpRequest.responseText);
//					$.messager.alert("提示",object.data,"info");
//					$('#user_myApply').datagrid('loaded');
//					$('#user_myApply').datagrid('resize');
//				},
//			});
//		},
//	});
	// 定义用户添加弹窗
	$('#user_myApply_apply_add').dialog({
		width : 480,
		title : '添加申报',
		height : 420,
		modal : true,
		closed : true,
		buttons : [{
					text : '添加',
					iconCls : 'icon-ok',
					handler : function() {
						if ($('#user_myApply_apply_add').form('validate')) {
							var productTypeID_add = $("#user_myApply_productTypeID_add").combobox('getValue');
							var reportQuantity_add = $('#user_myApply_reportQuantity_add').val();
							var reportStartDate_add = $('#user_myApply_reportStartDate_add').datebox('getValue');
							var reportEndDate_add = $('#user_myApply_reportEndDate_add').datebox('getValue');
							var reportCompleteRate_add = $('#user_myApply_reportCompleteRate_add').val();
							reportCompleteRate_add = parseInt(reportCompleteRate_add);
							var user_myApply_producer_add = $('#user_myApply_producer_add').val();
							var reportStartDate_add_parse=new Date(reportStartDate_add.replace(/-/g,"/"));
							var reportEndDate_add_parse=new Date(reportEndDate_add.replace(/-/g,"/"));
							var today=new Date();
							if(reportStartDate_add_parse<=today){
								$.messager.alert("提示","开始时间应该大于今天的时间","info");
								return false;
							}
							if(reportStartDate_add_parse>reportEndDate_add_parse){
								$.messager.alert("提示","结束时间应该大于或等于开始时间","info");
								return false;
							}
							var requestdata = {
								"productTypeID" : productTypeID_add,
								"reportQuantity" : reportQuantity_add,
								"reportStartDate" : reportStartDate_add,
								"reportEndDate" : reportEndDate_add,
								"reportCompleteRate" : reportCompleteRate_add,
								"producer" : user_myApply_producer_add
							};

							var json_data = JSON.stringify(requestdata);
							$.ajax({
										url : 'report',
										timeout:3000,
										type : 'post',
										headers : {
											'userName' : phone,
											'hashedPassword' : password
										},
										data : "jsondata="+ json_data,
										beforeSend : function() {
											$.messager.progress({
														text : '正在添加，请稍后...',
													});
										},
										success : function(data) {
											$.messager.progress('close');
											$.messager.show({
												title : '提示',
												msg : '添加成功！',
												timeout : 2000,
											});
											$('#user_myApply_apply_add').dialog('close').form('reset');
											$('#user_myApply').datagrid('reload');
										},
										error : function(XMLHttpRequest, status, errorThrown) {// 请求完成最终执行的函数
											var object = JSON.parse(XMLHttpRequest.responseText);
											$.messager.alert("提示",object.data,"info");
											$.messager.progress("close");
											$('#user_myApply').datagrid('loaded');
											$('#user_myApply').datagrid('resize');
										},
									});
						}

					}
				},
				{
					text : '取消',
					iconCls : 'icon-redo',
					handler : function() {
						$('#user_myApply_apply_add').dialog('close').form('reset');
					}
				} ],
	});
	//定义用户修改弹窗
	$('#user_myApply_apply_update')
	.dialog(
			{
				width : 480,
				title : '修改申报',
				height : 520,
				modal : true,
				closed : true,
				buttons : [
						{
							text : '修改',
							iconCls : 'icon-ok',
							handler : function() {
								if ($('#user_myApply_apply_update').form('validate')) {
									var reportID_update=$("#user_myApply_reportID_update").val();
									//var myApply_productTypeID_update =  $("#myApply_productTypeID_update").combobox('getValue');
									var reportQuantity_update = $('#user_myApply_reportQuantity_update').val();
									var reportStartDate_update = $('#user_myApply_reportStartDate_update').datebox('getValue');
									var reportEndDate_update = $('#user_myApply_reportEndDate_update').datebox('getValue');
									var reportCompleteRate_update = $('#user_myApply_reportCompleteRate_update').val();
									var producer_update = $('#user_myApply_producer_update').val();
									
									var reportStartDate_update_parse=new Date(reportStartDate_update.replace(/-/g,"/"));
									var reportEndDate_update_parse=new Date(reportEndDate_update.replace(/-/g,"/"));
									if(reportStartDate_update_parse>reportEndDate_update_parse){
										$.messager.alert("提示","结束时间应该大于或等于开始时间","info");
										return false;
									}
									var requestdata = {
										//"productTypeID":myApply_productTypeID_update,
										"reportQuantity" : reportQuantity_update,
										"reportStartDate" : reportStartDate_update,
										"reportEndDate" : reportEndDate_update,
										"reportCompleteRate" : reportCompleteRate_update,
										"producer" : producer_update,
									};
									var json_data = JSON.stringify(requestdata);
									$.ajax({
												url : "report/:"+ reportID_update,
												timeout : 3000,
												type : 'put',
												headers : {
													'userName' : phone,
													'hashedPassword' : password
												},
												data : "jsondata="+ json_data,
												beforeSend : function() {
													$.messager.progress({
																text : '正在修改，请稍后...',
															});
												},
												success : function(data) {
													$.messager.progress('close');
													$.messager.show({
														title : '提示',
														msg : '修改成功',
														timeout : 2000,
													});
													$('#user_myApply_apply_update').dialog('close').form('reset');
													$('#user_myApply').datagrid('reload');
												},
												error : function(XMLHttpRequest, status, errorThrown) {// 请求完成最终执行的函数
													var object = JSON.parse(XMLHttpRequest.responseText);
													$.messager.alert("提示",object.data,"info");
													$.messager.progress("close");
													$('#user_myApply').datagrid('loaded');
													$('#user_myApply').datagrid('resize');
												},
											});
								}
							}
						},
						{
							text : '取消',
							iconCls : 'icon-redo',
							handler : function() {
								$('#user_myApply_apply_update').dialog('close').form('reset');

							}
						} ],
			});
});