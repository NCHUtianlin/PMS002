
var note1 = {//"id":12,
		//"productBatch":"20160607",
		//"productStartDate":"2016-7-1",
		//"productEndDate":"2016-9-23",
		//"productTotalQuantity":1200,	
		//"productUsableNumber":1100,
		//"productOverdue":0,
		//"productOverdueExplain":"the weather is hoter",	
		//"notePerson":""
		};
		
		
function getNote(){
	$.ajax({
			type:"GET",
			//contentType:"application/x-www-form-urlencoded",
			url:"./productNote",
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

///////////////////////////////////////////////////////

var n2 = {//"id":12,
		"productBatch":"20160606",
		"productStartDate":"2016-07-1",
		"productEndDate":"2016-9-23",
		"productTotalQuantity":1200,	
		"productUsableNumber":1100,
		"productOverdue":0,
		"productOverdueExplain":"the weather is hoter",	
		//"notePerson":""
		};
		
		
function addNote(){
	$.ajax({
			type:"POST",
			//contentType:"application/x-www-form-urlencoded",
			url:"./productNote",
			data:"jsondata="+JSON.stringify(n2),
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


/////////////////////////////////////////////////////
var n1 = {//"id":12,
		//"productBatch":"20160606",
		//"productStartDate":"2016-07-1",
		//"productEndDate":"2016-9-23",
		"productTotalQuantity":99,	
		//"productUsableNumber":1100,
		//"productOverdue":0,
		//"productOverdueExplain":"the weather is hoter",	
		//"notePerson":""
		};
		
		
function putNote(){
	$.ajax({
			type:"PUT",
			//contentType:"application/x-www-form-urlencoded",
			url:"./productNote/:101",
			data:"jsondata="+JSON.stringify(n1),
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

