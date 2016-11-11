$(function(){
	// 接收账号信息
	var phone = $.query.get("phone");
	var password = $.query.get("password");
	
	var request_data={"parameter":2};
	var jsondata=JSON.stringify(request_data);
	$.ajax({			
		url:"productAnalysis", 
		timeout:3000,
		type:'get',
		cache : false,
		headers:{
			"userName":phone,
			"hashedPassword":password,
		},
		data:"jsondata="+jsondata,
		success: function(data){
			var productTypeName = [];
			var totalQuantity = [];
			var testRate = [];
			var passRate = [];
			var perTime = [];
			for(var i in data.rows){
				productTypeName.push(data.rows[i].productTypeName);			
				totalQuantity.push(data.rows[i].totalQuantity);
				passRate.push(data.rows[i].passRate * 100 );
				testRate.push(data.rows[i].testRate * 100 );
				perTime.push(data.rows[i].perTime);
			}
			var myChart=echarts.init(document.getElementById('ProductType_main'));
					var option={
							 title: {
										text: '各产品类型的生产情况统计图'
							 },
							 tooltip:{},
							 legend:{
								data:['生产量','测试率','单产','合格率']
							 },
							 xAxis:{
								data:productTypeName,
							 },
							 yAxis:{},
							 series:[
							         {
										name:'生产量',
										type:'bar',
										data:totalQuantity,
									 	},
									 	{
											name:'测试率',
											type:'bar',
											data:testRate,
										 },
									 	{
											name:'单产',
											type:'bar',
											data:perTime
										 },
										 {
												name:'合格率',
												type:'bar',
												data:passRate,
										}
								 ]
							};
						myChart.setOption(option);
					},error : function(XMLHttpRequest, status,errorThrown) {// 请求完成最终执行的函数
						var object = JSON.parse(XMLHttpRequest.responseText);
						$.messager.alert("提示",object.data,"info");
						$('#admin_allApply').datagrid('loaded');
						$('#admin_allApply').datagrid('resize');
					},
				
			}
	);
	
	});
