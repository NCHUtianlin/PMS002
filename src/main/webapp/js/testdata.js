



var test = {
		//"_id":12,
		//"deviceID":1,
		//"testDate":"2017-1-1",
		"result":1,		
		//"pageNow":3,
		//"pageSize";5,
		//"times":{ "start":"2017-1-1","end":"2017-8-8"}
		"parameter":"DEV"
		};
		
		
function gettest(){
	$.ajax({
			type:"GET",
			//contentType:"application/x-www-form-urlencoded",
			url:"./testingdata",
			data:"jsondata="+JSON.stringify(test),
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



		