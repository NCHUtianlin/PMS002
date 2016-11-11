
//�� 1. ��� ��2.  ��Ʒ���� �� 3. ����̡� 4. ʱ��� ��

var data1={  "parameter":3 };
//�����ͳ��
function analysis1()
{
	$.ajax({
		type:"GET",
		//contentType:"application/x-www-form-urlencoded",
		url:"./productAnalysis",
		data:"jsondata="+JSON.stringify(data1),
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