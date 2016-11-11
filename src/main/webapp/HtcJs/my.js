$(function(){
	var request_data={"parameter":1};
	var jsondata=JSON.stringify(request_data);
	//ajax获取
	$.ajax({			
		url:"productAnalysis", //提交给哪个执行 
		type:'get',
		cache : false,
		headers:{
			"userName":"15180240001",
			"hashedPassword":"123456",
		},
		data:"jsondata="+jsondata,
           
		success: function(data){
			alert(data.rows);
			alert(data.rows[0].productBatch);
			
			var productBatch = [];
			var totalQuantity = [];
			var testRate = [];
			var passRate = [];
			var perTime = [];
			for(var i in data.rows){
			
				
				productBatch.push(data.rows[i].productBatch);
				
				totalQuantity.push(data.rows[i].totalQuantity);
				passRate.push(data.rows[i].passRate * 100 );
				testRate.push(data.rows[i].testRate * 100 );
				perTime.push(data.rows[i].perTime);
			}
			
	
	
			var myChart=echarts.init(document.getElementById('main'));
					var option={
							 title: {
										text: '生产商生产产品合格率'
							 },
							 tooltip:{},
							 legend:{
								data:['生产量','测试率','单产','合格率']
							 },
							 xAxis:{
								data:productBatch,
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
	
					}
				
			}
	);
	
	});
