

var p1 = {//"id":12,
		//"productTypeName":"����",
		//"hardwareVersion":"02.01.01",
		//"softwareVersion":"02.03.01",
		//"macNumber":2,	
		
		};
		
		
function getPtype(){
	$.ajax({
			type:"GET",
			//contentType:"application/x-www-form-urlencoded",
			url:"./productType",
			data:"jsondata="+JSON.stringify(p1),
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

var p2 = {//"id":12,
		"productTypeName":"Voice",
		"hardwareVersion":"02.01.01",
		"softwareVersion":"02.03.01",
		"macNumber":2,	
		
		};
		
		
function addPtype(){
	$.ajax({
			type:"POST",
			//contentType:"application/x-www-form-urlencoded",
			url:"./productType",
			data:"jsondata="+JSON.stringify(p2),
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


//////////////////////////////////////////////////////

var p3 = {//"id":12,
		"productTypeName":"Lock",
		"hardwareVersion":"02.01.09",
		//"softwareVersion":"02.03.01",
		//"macNumber":2,	
		
		};
		
		
function putPtype(){
	$.ajax({
			type:"PUT",
			//contentType:"application/x-www-form-urlencoded",
			url:"./productType/:58036940812f731b141b3ade",
			data:"jsondata="+JSON.stringify(p3),
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

