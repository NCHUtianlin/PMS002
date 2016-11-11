


		var r1 = {//"id":12,
						//"productTypeID":1,
						//"reportQuantity":1000,
						//"reportStartDate":"2017-6-1",
						//"reportEndDate":"2017-8-1",
						//"reportCompleteRate":"100",
						//"producer":"BaiDu",
						//"checkResult":1,
						"pageNow":1,
						"pageSize":8,
						//"times":{ "start":"2017-1-1","end":"2017-8-8"},
						//"parameter":"2017-6-1"
					};
		
		function getreport(){
			$.ajax({
					type:"GET",
					//contentType:"application/x-www-form-urlencoded",
					url:"./report",
					data:"jsondata="+JSON.stringify(r1),
					headers:{
						"userName":userName,
						"hashedPassword":hashedPassword
					},
					success:function(data){
							console.log(data);
							alert("OK");
					},
					error:function(data){
							console.log(data);
							alert("error");
					}
			});
		}
		
		
////////////////////////////////////////////////////////////		

var r2 = {//"id":12,
		    "reportTime": "2016-10-26 08:49:55",
		    "reportPerson": "58105e16fd56f880e8832fa4",
		    "reportCompleteRate": 100,
		    "checkResult": 1,
		    "reportQuantity": 122,
		    "productTypeID": "5810a531a82b433164995ba1",
		    "producer": "333号工厂",
		    "reportEndDate": "2016-10-29",
		    "reportStartDate": "2016-10-26",
		    "checkExplain": "",
		    "checkPerson": "58105e16fd56f880e8832fa4",
		    "checkDate": "2016-10-26 08:51:07",
		    "productBatch": "20161026085107051"
			};

function addreport(){
	$.ajax({
			type:"POST",
			//contentType:"application/x-www-form-urlencoded",
			url:"./report",
			data:"jsondata="+JSON.stringify(r2),
			headers:{
				"userName":userName,
				"hashedPassword":hashedPassword
			},
			success:function(data){
					console.log(data);
					alert("OK");
			},
			error:function(data){
					console.log(data);
					alert("error");
			}
	});
}

/////////////////////////////////////////////////////////////////////////

var r3 = {//"id":12,
		//"productTypeID":1,
		//"reportQuantity":1000,
		//"reportStartDate":"2017-6-1",
		//"reportEndDate":"2017-8-1",
		//"reportCompleteRate":"100",
		//"producer":"BaiDu",
		"checkResult":0,
		"checkExplain":"���������������г�����"
	};

function putreport(){
$.ajax({
	type:"PUT",
	//contentType:"application/x-www-form-urlencoded",
	url:"./report/:58036abb812f731b141b3adf",
	data:"jsondata="+JSON.stringify(r3),
	headers:{
		"userName":userName,
		"hashedPassword":hashedPassword
	},
	success:function(data){
			console.log(data);
			alert("OK");
	},
	error:function(data){
			console.log(data);
			alert("error");
	}
});
}

		
		
		