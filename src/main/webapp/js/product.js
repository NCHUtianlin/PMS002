

var note1 = {//"id":12,
		//"productBatch":"20160607",
		//"productStartDate":"2016-7-1",
		"testingTimes":{
			"start":"2016-5-23",
			"end":"2016-12-23",
		}
		//"productTotalQuantity":1200,	
		//"productUsableNumber":1100,
		//"productOverdue":0,
		//"productOverdueExplain":"the weather is hoter",	
		//"notePerson":""
		};
		
		
function getproduct(){
	$.ajax({
			type:"GET",
			//contentType:"application/x-www-form-urlencoded",
			url:"./product",
			data:"jsondata="+JSON.stringify(note1),
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
