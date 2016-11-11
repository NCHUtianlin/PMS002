require.config({
	paths: {
		echarts: 'http://localhost:8080/PMS002/echarts/build/dist'
	}
}); 
function t(){
	alert("KAK");
}
function refash(){
	var request_data={"parameter":1};
	var jsondata=JSON.stringify(request_data);
	//ajax获取
	$.ajax({			
		url:"productAnalysis", //提交给哪个执行 
		type:'get',
		cache : false,
		headers:{
			"userName":"15180249888",
			"hashedPassword":"123456"
		},
		data:"jsondata="+jsondata,
           
		success: function(data){
			// var json = JSON.parse(data);
			var productBatch = [];
			var totalQuantity = [];
			var testRate = [];
			var passRate = [];
			var perTime = [];
			for(var i in data){
				productBatch.push(data[i].productBatch);
				totalQuantity.push(data[i].totalQuantity);
				passRate.push(data[i].passRate);
				testRate.push(data[i].testRate);
				perTime.push(data[i].perTime);
			}
			alert(data)
			/*var totalQuantity = new Array();
						for(var i in data){
							productBatch[i] = data[i].productBatch;
							totalQuantity[i] = data[i].totalQuantity;
						}

                      alert(productBatch[0]); 
                      alert(totalQuantity[0]);
                      alert([totalQuantity]);*/
			// 使用
			require(
					[
					 'echarts',
					 'echarts/chart/line' // 使用柱状图就加载bar模块，按需加载
					 ],
					 function (ec) {

						// 基于准备好的dom，初始化echarts图表
						var myChart = ec.init(document.getElementById("main")); 

						var option = {
								title : {
									text: '实际生产数量',
									subtext: ''
								},
								tooltip: {
									show: true
								},
								legend: {
									data:['实际生产数量']
								},
								xAxis : [
								         {
								        	 type : 'category',
								        	 data :productBatch,	
								        	 // data :[2,2,2,2],	
								        	 axisLabel : {
								        		 formatter : '{value}批次',
								        	 }
								         }
								         ],
								         yAxis : [
								                  {
								                	  type : 'value',
								                	  data:totalQuantity,
								                	  axisLabel : {
								                		  formatter: '{value} 数量',

								                	  }
								                  }
								                  ],
								                  series : [
								                            {
								                            	"name":"生产量",
								                            	"type":"line",
								                            	data:totalQuantity,
								                            	markPoint : {
								                            		data : [                                                                            
								                            		        {type : 'max', name: '最大值'},
								                            		        {type : 'min', name: '最小值'}
								                            		        ]
								                            	}
								                            },
								                            {
								                            	"name":"合格率",
								                            	"type":"line",
								                            	data:passRate,
								                            	
								                            },
								                            {
								                            	"name":"测试率",
								                            	"type":"line",
								                            	data:testRate,
								                            	
								                            },
								                            {
								                            	"name":"一天生产量",
								                            	"type":"line",
								                            	data:perTime,
								                            	
								                            },
								                            ]
						};

						// 为echarts对象加载数据 
						myChart.setOption(option); 
					}
			);

		}
	});
}