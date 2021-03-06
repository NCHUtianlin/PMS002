$(function() {
	// 接收账号信息
		var phone = $.query.get("phone");
		var password = $.query.get("password");
	// 工具集
	admin_allApply_manager_tools = {
		refresh : function() {
			$('#admin_allApply').datagrid('reload');
		},
		search : function() {
			$('#admin_allApply').datagrid({
				loader : function(param, success, error) {
					var options = $("#admin_allApply").datagrid('options');
					var pageNow = options.pageNumber;
					var page_Size = options.pageSize;
					var search = $('#admin_allApply_search').val();
					var date_from=$("#admin_allApply_date_from").datebox('getValue');
					var date_to=$("#admin_allApply_date_to").datebox('getValue');
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
						"pageNow" : pageNow,
						"pageSize" : page_Size
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
									$('#admin_allApply').datagrid('loaded');
									$('#admin_allApply').datagrid('resize');
								},
								error : function(XMLHttpRequest, status,errorThrown) {// 请求完成最终执行的函数
									var object = JSON.parse(XMLHttpRequest.responseText);
									$.messager.alert("提示",object.data,"info");
									$('#admin_allApply').datagrid('loaded');
									$('#admin_allApply').datagrid('resize');
								},
							});
				},
			});
		},
		add : function() {
			$('#admin_allApply_apply_add').dialog('open');
		},
		redo : function() {
			$('#admin_allApply').datagrid('unselectAll');
		},
		update : function() {
			var rows = $('#admin_allApply').datagrid('getSelections');
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
//					$("#allApply_productTypeID_update").combobox('setValue',rows[0]._id);
//					$("#allApply_productTypeID_update").combobox('setText',rows[0].productTypeName);
					//$("#allApply_productTypeID_update").combobox('select',rows[0].productTypeName);
					$('#admin_allApply_apply_update').form('load', {
						admin_allApply_reportID_update : rows[0]._id,
						//这里是产品类型名称，没有产品类型ID,仅仅提供显示效果
						admin_allApply_productTypeID_update:rows[0].productTypeName,
						admin_allApply_reportQuantity_update : rows[0].reportQuantity,
						admin_allApply_reportStartDate_update : rows[0].reportStartDate,
						admin_allApply_reportEndDate_update : rows[0].reportEndDate,
						admin_allApply_reportCompleteRate_update : rows[0].reportCompleteRate,
						admin_allApply_producer_update : rows[0].producer,
						admin_allApply_checkResult_update : rows[0].checkResult,
						admin_allApply_checkExplain_update : rows[0].checkExplain,
					}).dialog('open');
				}
		},
	};
	$("#admin_allApply").datagrid({
		width : '800',
		title : '所有申报',
		fit : true,
		cache : false,
		border:false,
		columns : [ [ {
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
		pagination : true,
		pageSize : 10,
		pageList : [10,15,20],
		remoteSort : false,
		rownumbers : true,
		fitColumns : true,
		toolbar : "#admin_allApply_manager_tool",
		loader : function(param, success, error) {
			var options = $("#admin_allApply").datagrid('options');
			var pageNow = options.pageNumber;
			var page_Size = options.pageSize;
			var request = {
				"pageNow" : pageNow,
				"pageSize" : page_Size
			};
			var json_data = JSON.stringify(request);
			$.ajax({
				url : 'report',
				timeout:3000,
				type : "get",
				headers : {
					'userName' : phone,
					'hashedPassword' : password
				},
				data : "jsondata=" + json_data,
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
					$('#admin_allApply').datagrid('loaded');
					$('#admin_allApply').datagrid('resize');
				},
				error : function(XMLHttpRequest, status, errorThrown) {// 请求完成最终执行的函数
					var object = JSON.parse(XMLHttpRequest.responseText);
					$.messager.alert("提示",object.data,"info");
					$('#admin_allApply').datagrid('loaded');
					$('#admin_allApply').datagrid('resize');
				},
			});
		},
	});
	$("#admin_allApply_productTypeID_add").combobox({
		required:true,
		editable:false,
		missingMessage:"请选择产品类型",
	});
	$("#admin_allApply_date_from").datebox({
		editable:false,
	});
	$("#admin_allApply_date_to").datebox({
		editable:false,
	});
	$("#admin_allApply_productTypeID_update").textbox({
		required:true,
		editable:false,
	});
	// 定义时间选择框
	$("#admin_allApply_reportStartDate_add").datebox({
		required:true,
		editable:false,
		missingMessage:"请选择开始时间",
	});
	$("#admin_allApply_reportEndDate_add").datebox({
		required:true,
		editable:false,
		missingMessage:"请选择结束时间",
	});
	$("#admin_allApply_reportStartDate_update").datebox({
		required:true,
		editable:false,
		missingMessage:"请选择开始时间",
	});
	$("#admin_allApply_reportEndDate_update").datebox({
		required:true,
		editable:false,
		missingMessage:"请选择结束时间",
	});
	//产品数量检测
	$("#admin_allApply_reportQuantity_add").numberbox({
		required:true,
		missingMessage:"请输入产品数量",
	});
	$("#admin_allApply_reportQuantity_update").numberbox({
		required:true,
		missingMessage:"请输入产品数量",
	});
	//产品完成率输入框检测
	$("#admin_allApply_reportCompleteRate_add").textbox({
		required:true,
		missingMessage:"请输入产品完成率",
	});
	$("#admin_allApply_reportCompleteRate_update").textbox({
		required:true,
		missingMessage:"请输入产品完成率",
	});
	//产品生产商输入框检测
	$("#admin_allApply_producer_add").textbox({
		required:true,
		missingMessage:"请输入产品生产商",
	});
	$("#admin_allApply_producer_update").textbox({
		required:true,
		missingMessage:"请输入产品生产商",
	});
	//审核说明
	$("#admin_allApply_checkExplain_update").textbox({
		required:false,
		multiline:true,
		width:180,
		height:60,
	});
	//获取产品类型
	$("#admin_allApply_productTypeID_add").combobox({
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
					$('#admin_allApply').datagrid('loaded');
					$('#admin_allApply').datagrid('resize');
				},
			});
		},
	});	
	// 定义用户添加弹窗
	$('#admin_allApply_apply_add').dialog({
		width : 480,
		title : '添加申报',
		height : 420,
		modal : true,
		closed : true,
		buttons : [{
			text : '添加',
			iconCls : 'icon-ok',
			handler : function() {
				if ($('#admin_allApply_apply_add').form('validate')) {
					var productTypeID_add =$('#admin_allApply_productTypeID_add').combobox('getValue');
					var reportQuantity_add = $('#admin_allApply_reportQuantity_add').val();
					var reportStartDate_add = $('#admin_allApply_reportStartDate_add').datebox('getValue');
					var reportEndDate_add = $('#admin_allApply_reportEndDate_add').datebox('getValue');
					var reportCompleteRate_add = $('#admin_allApply_reportCompleteRate_add').val();
					var producer_add = $('#admin_allApply_producer_add').val();
					reportCompleteRate_add = parseInt(reportCompleteRate_add);
					
					var reportStartDate_add_parse=new Date(reportStartDate_add.replace(/-/g,"/"));
					var reportEndDate_add_parse=new Date(reportEndDate_add.replace(/-/g,"/"));
					var today=new Date();
//					var todat_year=today.getFullYear().toString();
//					var today_month=(today.getMonth()+1).toString();
//					var today_day=today.getDate().toString();
//					var string_today=todat_year+"-"+today_month+"-"+today_day;
//					
//					var today_parse=new Date(string_today.replace(/-/g,"/"));
//					console.log(reportStartDate_add_parse);
//					console.log(reportEndDate_add_parse);
//					console.log(today_parse);
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
						"producer" : producer_add
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
									$('#admin_allApply_apply_add').dialog('close').form('reset');
									$('#admin_allApply').datagrid('reload');
								},
								error : function(XMLHttpRequest, status, errorThrown) {// 请求完成最终执行的函数
									var object = JSON.parse(XMLHttpRequest.responseText);
									$.messager.alert("提示",object.data,"info");
									$.messager.progress("close");
									$('#admin_allApply').datagrid('loaded');
									$('#admin_allApply').datagrid('resize');
								},
							});
				}

			}
		},
		{
			text : '取消',
			iconCls : 'icon-redo',
			handler : function() {
				$('#admin_allApply_apply_add').dialog('close').form('reset');
			}
		}],
	});
	//修改_获取产品类型
//	$("#allApply_productTypeID_update").combobox({
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
//					$('#admin_allApply').datagrid('loaded');
//					$('#admin_allApply').datagrid('resize');
//				},
//			});
//		},
//	});
	
	// 定义用户修改弹窗
	$('#admin_allApply_apply_update').dialog({
		width : 480,
		title : '修改申报',
		height : 520,
		modal : true,
		closed : true,
		buttons : [{
				text : '修改',
				iconCls : 'icon-ok',
				handler : function() {
					if ($('#admin_allApply_apply_update').form('validate')) {
						var reportID_update=$("#admin_allApply_reportID_update").val();
						//var myApply_productTypeID_update =  $("#myApply_productTypeID_update").combobox('getValue');
						var reportQuantity_update = $('#admin_allApply_reportQuantity_update').val();
						var reportStartDate_update = $('#admin_allApply_reportStartDate_update').datebox('getValue');
						var reportEndDate_update = $('#admin_allApply_reportEndDate_update').datebox('getValue');
						var reportCompleteRate_update = $('#admin_allApply_reportCompleteRate_update').val();
						var producer_update = $('#admin_allApply_producer_update').val();
						var checkResult_update = $('#admin_allApply_checkResult_update').val();
						var checkExplain_update = $('#admin_allApply_checkExplain_update').val();
						//检测时间是否是后者大于前者
						var reportStartDate_update_parse=new Date(reportStartDate_update.replace(/-/g,"/"));
						var reportEndDate_update_parse=new Date(reportEndDate_update.replace(/-/g,"/"));
//						var today=new Date();
//						if(reportStartDate_add_parse<=today){
//							$.messager.alert("提示","开始时间应该大于今天的时间","info");
//							return false;
//						}
						if(reportStartDate_update_parse>reportEndDate_update_parse){
							$.messager.alert("提示","结束时间应该大于或等于开始时间","info");
							return false;
						}
						var requestdata = {
							//"productTypeID":allApply_productTypeID_update,
							"reportQuantity" : reportQuantity_update,
							"reportStartDate" : reportStartDate_update,
							"reportEndDate" : reportEndDate_update,
							"reportCompleteRate" : reportCompleteRate_update,
							"producer" : producer_update,
							"checkResult" : checkResult_update,
							"checkExplain" : checkExplain_update
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
										$('#admin_allApply_apply_update').dialog('close').form('reset');
										$('#admin_allApply').datagrid('reload');
									},
									error : function(XMLHttpRequest, status, errorThrown) {// 请求完成最终执行的函数
										var object = JSON.parse(XMLHttpRequest.responseText);
										$.messager.alert("提示",object.data,"info");
										$.messager.progress("close");
										$('#admin_allApply').datagrid('loaded');
										$('#admin_allApply').datagrid('resize');
									},
								});
						}
					}
		},
			{
				text : '取消',
				iconCls : 'icon-redo',
				handler : function() {
					$('#admin_allApply_apply_update').dialog('close').form('reset');
				}
			}
		],
	});
});