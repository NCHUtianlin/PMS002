<?php
	sleep(1);
	$page=$_POST['page'];
	$pageSize=$_POST['rows'];
	$first=$pageSize*($page-1);
	$user='';
	$sql='';
	$date_from='';
	$date_to='';
	if(isset($_POST['user'])&&!empty($_POST['user'])){
		$user="user LIKE %{$_POST['user']}% AND";
		$sql.=$user;
	}
	if(isset($_POST['date_from'])&&!empty($_POST['date_from'])){
		$date_from="date>='{$_POST['date_from']}' AND";
		$sql.=$date_from;
	}
	if(isset($_POST['date_to'])&&!empty($_POST['date_to'])){
		$date_to="date<= '{$_POST['date_to']}' AND";
		$sql.=$date_to;
	}
	if(!empty($sql)){
		$sql=substr($sql,0,-4);
	}
	/*
		$query=mysql_query(SELECT user,email,date FROM think_user $sql ORDER BY);
		$total=mysql_num_rows(mysql_query());
	*/
	
		$json='[
			{
				"id":"1",
				"user":"张三",
				"email":"zhangsan@163.com",
				"phone":"12345",
				"date":"2016-10-12"
			},
			{
				"id":"2",
				"user":"李四",
				"email":"lisi@163.com",
				"phone":"114523",
				"date":"2016-10-13"
			},
			{
				"id":"3",
				"user":"王五",
				"email":"wangwu@163.com",
				"phone":"1535655",
				"date":"2016-10-11"
			},
			{
				"id":"4",
				"user":"赵六",
				"email":"zhaoliu@163.com",
				"phone":"145555",
				"date":"2016-10-10"
			},
			{
				"id":"5",
				"user":"钱七",
				"email":"qianqi@163.com",
				"phone":"255554",
				"date":"2016-9-11"
			}
		]';
	echo '{"total":12,"rows":'.$json.'}';
	
?>
