var userName = "18270892003";
var hashedPassword = "123456";


/////////////////////  GET
var jsondata = { 		//"_id":"57f8b431bb9e4625697390ad",
						//"name":"tianlin",
						//"phone":"15180240000",
						//"password":"123",
						//"email":"tianlin@163.com",
						///"authority":1,
						//"pageNow":1,
						//"pageSize":5,
						//"workState":1,
						"queryTab":0
					};
		
		
		function getuser(){
			$.ajax({
					type:"GET",
					contentType:"application/x-www-form-urlencoded; charset=utf-8",
					url:"./user",
					data:"jsondata="+JSON.stringify(jsondata),
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
		
		
//////////////////////////////////  PUT
var jsondata2 = { 
				//"name":"tianlin",
				//"phone":"15180240000",
				//"password":"123",
				"email":"hirary@139.com",
				///"authority":1,
				//"workState":1,
			};


function putuser(){
	$.ajax({
			type:"PUT",
			contentType:"application/x-www-form-urlencoded; charset=utf-8",
			url:"./user/:58035f30812f7317e89e1770",
			data:"jsondata="+JSON.stringify(jsondata2),
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
		

////////////////////////  DELETE
function deleteuser(){
$.ajax({
	type:"DELETE",
	contentType:"application/x-www-form-urlencoded; charset=utf-8",
	url:"./user/:58035ee0812f7317e89e176f",
	data:"",
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

/////////////////////////////////  POST
var jsondata4 = { 
		"name":"Hirary",
		"phone":"15180240303",
		"password":"123",
		"email":"hi@163.com",
		"authority":0,
	};


function adduser(){
$.ajax({
	type:"POST",
	contentType:"application/x-www-form-urlencoded; charset=utf-8",
	url:"./user",
	data:"jsondata="+JSON.stringify(jsondata4),
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

		
		
		