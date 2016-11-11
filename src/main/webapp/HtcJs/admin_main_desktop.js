$(function(){
	var phone=$.query.get("phone");
	var name=$.query.get("name");
	var password=$.query.get("password");
	if(($.trim(phone)!="")&&($.trim(name)!="")&&($.trim(password)!="")){
		$('#name').text(name);
		$('#tabs').tabs({
			fit:true,
			border:false,
		});
	}else{
		$.messager.alert("提示","请先登录","info");
		location.href ="main";
	}
	/********************管理员退出系统********************/
	admin_getout=function (){
		$.messager.confirm("确认","是否确认退出？",function(flag){
				if(flag){
					$.ajax({
						url : 'userExit',
						timeout:3000,
						type : "get",
						headers : {
							'userName' : phone,
							'hashedPassword' : password
						},
						data : "",
						success : function(data) {
							location.href ="main";
						},
						error : function(XMLHttpRequest, status,errorThrown) {// 请求完成最终执行的函数
							$.messager.alert("提示","暂时不能退出，请稍后再试","info");
						},
					});
					}
			});
	};
});

/********************管理员查询我的账户********************/
function addTabs_myAccount(title, url){
	if($('#tabs').tabs('exists',title)){
		$('#tabs').tabs('select',title);
	}else{
		$('#tabs').tabs('add',{
			title:title,
			closable:true,
			href:'admin_myAccount.html'
		});
	}
};
/********************管理员查询所有用户********************/
function addTabs_allUsers(title, url){
	if($('#tabs').tabs('exists',title)){
		$('#tabs').tabs('select',title);
	}else{
		$('#tabs').tabs('add',{
			title:title,
			closable:true,
			href:'admin_allUsers.html'
		});
	}
};
/********************管理员查询所有申报********************/
function addTabs_allApply(title, url){
	if($('#tabs').tabs('exists',title)){
		$('#tabs').tabs('select',title);
	}else{
		$('#tabs').tabs('add',{
			title:title,
			closable:true,
			href:'admin_allApply.html'
		});
	}
};
/********************管理员获取我的申报********************/
function addTabs_admin_myApply(title, url){
	if($('#tabs').tabs('exists',title)){
		$('#tabs').tabs('select',title);
	}else{
		
		$('#tabs').tabs('add',{
			title:title,
			closable:true,
			href:'admin_myApply.html'
		});
	}
};
/********************管理员获取所有产品类型********************/
function addTabs_admin_productType(title, url){
	if($('#tabs').tabs('exists',title)){
		$('#tabs').tabs('select',title);
	}else{
		
		$('#tabs').tabs('add',{
			title:title,
			closable:true,
			href:'admin_productType.html'
		});
	}
};
/********************管理员获取所有产品********************/
function addTabs_allProducts(title, url){
	if($('#tabs').tabs('exists',title)){
		$('#tabs').tabs('select',title);
	}else{
		$('#tabs').tabs('add',{
			title:title,
			closable:true,
			href:'admin_allProducts.html'
		});
	}
};

/********************管理员获取批次的统计信息********************/
function addTabs_Batch(title, url){
		if($('#tabs').tabs('exists',title)){
			$('#tabs').tabs('select',title);
		}else{
			
			$('#tabs').tabs('add',{
				title:title,
				closable:true,
				href:'admin_statistics_batch.html'
			});
		}
};
/********************管理员获取生产商的统计信息********************/
function addTabs_shengchanshang(title,url){
	if($('#tabs').tabs('exists',title)){
		$('#tabs').tabs('select',title);
	}else{
		
		$('#tabs').tabs('add',{
			title:title,
			closable:true,
			href:'admin_statistics_producer.html'
		});
	}
};
/********************管理员获取产品类型的统计信息********************/
function addTabs_productType(title,url){
	if($('#tabs').tabs('exists',title)){
		$('#tabs').tabs('select',title);
	}else{
		
		$('#tabs').tabs('add',{
			title:title,
			closable:true,
			href:'admin_statistics_productType.html'
		});
	}
};
/********************管理员获取生产完成信息********************/
function addTabs_shengchanwancheng(title, url){
	if($('#tabs').tabs('exists',title)){
		$('#tabs').tabs('select',title);
	}else{
		
		$('#tabs').tabs('add',{
			title:title,
			closable:true,
			href:'admin_productionAnswer.html'
		});
	}
};
/********************管理员获取管理员获取测试信息********************/
function addTabs_test(title,url){
	if($('#tabs').tabs('exists',title)){
		$('#tabs').tabs('select',title);
	}else{
		$('#tabs').tabs('add',{
			title:title,
			closable:true,
			href:'admin_testAnswer.html'
		});
	}
};
/********************管理员产品信息导出********************/
function addTabs_toExport(title,url){
	if($('#tabs').tabs('exists',title)){
		$('#tabs').tabs('select',title);
	}else{
		$('#tabs').tabs('add',{
			title:title,
			closable:true,
			href:'admin_toExport.html'
		});
	}
};
