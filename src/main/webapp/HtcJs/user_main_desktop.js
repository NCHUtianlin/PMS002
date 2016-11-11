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
	
	/********************用户退出系统********************/
	
	 user_getout=function(){
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

/********************用户查询我的账户********************/
function user_addTabs_myAccount(title, url){
	if($('#tabs').tabs('exists',title)){
		$('#tabs').tabs('select',title);
	}else{
		$('#tabs').tabs('add',{
			title:title,
			closable:true,
			href:'user_myAccount.html'
		});
	}
};

/********************用户获取我的申报********************/
function user_addTabs_myApply(title, url){
	if($('#tabs').tabs('exists',title)){
		$('#tabs').tabs('select',title);
	}else{
		
		$('#tabs').tabs('add',{
			title:title,
			closable:true,
			href:'user_myApply.html'
		});
	}
};
/********************用户获取所有产品类型********************/
function user_addTabs_productType(title, url){
	if($('#tabs').tabs('exists',title)){
		$('#tabs').tabs('select',title);
	}else{
		
		$('#tabs').tabs('add',{
			title:title,
			closable:true,
			href:'user_productType.html'
		});
	}
};
/********************用户获取所有产品********************/
function user_addTabs_allProducts(title, url){
	if($('#tabs').tabs('exists',title)){
		$('#tabs').tabs('select',title);
	}else{
		$('#tabs').tabs('add',{
			title:title,
			closable:true,
			href:'user_allProducts.html'
		});
	}
};

/********************用户获取批次的统计信息********************/
function user_addTabs_Batch(title, url){
		if($('#tabs').tabs('exists',title)){
			$('#tabs').tabs('select',title);
		}else{
			
			$('#tabs').tabs('add',{
				title:title,
				closable:true,
				href:'user_statistics_batch.html'
			});
		}
};
/********************用户获取生产商的统计信息********************/
function user_addTabs_shengchanshang(title,url){
	if($('#tabs').tabs('exists',title)){
		$('#tabs').tabs('select',title);
	}else{
		
		$('#tabs').tabs('add',{
			title:title,
			closable:true,
			href:'user_statistics_producer.html'
		});
	}
};
/********************用户获取产品类型的统计信息********************/
function user_addTabs_analysisProductType(title,url){
	if($('#tabs').tabs('exists',title)){
		$('#tabs').tabs('select',title);
	}else{
		
		$('#tabs').tabs('add',{
			title:title,
			closable:true,
			href:'user_statistics_productType.html'
		});
	}
};
/********************用户获取生产完成信息********************/
function user_addTabs_shengchanwancheng(title, url){
	if($('#tabs').tabs('exists',title)){
		$('#tabs').tabs('select',title);
	}else{
		
		$('#tabs').tabs('add',{
			title:title,
			closable:true,
			href:'user_productionAnswer.html'
		});
	}
};
/********************用户获取测试信息********************/
function user_addTabs_test(title,url){
	if($('#tabs').tabs('exists',title)){
		$('#tabs').tabs('select',title);
	}else{
		$('#tabs').tabs('add',{
			title:title,
			closable:true,
			href:'user_testAnswer.html'
		});
	}
};
/********************用户导出********************/
function addTabs_toExport(title,url){
	if($('#tabs').tabs('exists',title)){
		$('#tabs').tabs('select',title);
	}else{
		$('#tabs').tabs('add',{
			title:title,
			closable:true,
			href:'user_toExport.html'
		});
	}
};


