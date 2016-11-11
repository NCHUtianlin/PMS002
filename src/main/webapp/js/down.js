

var dataE={  "parameter":3 , "productBatch":"20161109151359013" };

function down(obj)
{
	/*$.ajax({
		type:"POST",
		//contentType:"application/x-www-form-urlencoded",
		url:"./export?type="+obj,
		//data:"jsondata="+JSON.stringify(data1),
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
});*/
	
	
	var postForm = document.getElementById("exportForm");
	postForm.action = "http://10.70.12.219:8080/PMS002/export?type=MACs";
	postForm.submit();

}

