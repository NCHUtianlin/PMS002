package dao.basic;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import dao.ProductDataDao;
import domain.ProductType;
import domain.User;


/**
 * 专职做模糊查询
 * **/
public class QueryDao extends BasicImplentsDao {
	
	private static Log log = LogFactory.getLog(QueryDao.class.getName());
	

	/**查询用户表userTable
	 * 当用户工作状态为 -1 时代表已经被管理员删除了，则不能被用户所看到
	 * */
	public BasicDBObject queryUser(Object parameter)
	{
		//String parameter = obj.toString().replace(" ", "");//将字符串中的空格去掉
		BasicDBList dbList = new BasicDBList();
		Pattern pattern = Pattern.compile("^"+parameter+".*$", Pattern.CASE_INSENSITIVE);  //左匹配
		Pattern p = Pattern.compile("[\u4e00-\u9fa5]"); // 汉字编码区间
        Matcher m = p.matcher(parameter.toString());
        
		//参数带有中文字符
		if( m.find() )
		{
			BasicDBObject obj1 = new BasicDBObject();
			BasicDBObject obj2 = new BasicDBObject();
			//匹配权限
			if(parameter.toString().contains("管理员") || parameter.toString().contains("普通"))
			{
				if(parameter.toString().contains("管理员"))
				{
					obj1.put("authority", 1);
				}
				else {
					obj1.put("authority", 0);
				}
			}
			//匹配用户姓名
			else {
				obj1.put("name", pattern); //相当于SQL语句：(1)'name like' + pattern +''
				
				Pattern patternRight = Pattern.compile("^.*"+parameter+"$", Pattern.CASE_INSENSITIVE);//右匹配
				obj2.put("name", patternRight); //相当于SQL语句：(1)'name like' + pattern +''
				
			}
			
			dbList.add(obj1);
			dbList.add(obj2);		
		}
		//匹配邮箱 ,字符串中含有 @ 
		else if( parameter.toString().indexOf("@") != -1 )
		{
			pattern = Pattern.compile("^.*"+parameter+".*$", Pattern.CASE_INSENSITIVE);   //模糊匹配
			BasicDBObject obj1 = new BasicDBObject();
			obj1.put("email", pattern); //相当于SQL语句：(2)'email like' + pattern +''
			dbList.add(obj1);
			
		}
		//数字类型，那么可以与 手机号码、用户权限 进行匹配 
		//if( StringUtils.isNumeric(parameter.toString()) )//只能校验不含负号“-”的数字
		/***
		 *  不含中文，不含@，则可以匹配手机号码、用户权限，用户姓名也可能是英文
		 * ***/
		else{
			BasicDBObject obj1 = new BasicDBObject();
			obj1.put("phone", pattern); //相当于SQL语句：(3)'phone like' + pattern +''
			BasicDBObject obj2 = new BasicDBObject();
			obj2.put("authority", pattern); //相当于SQL语句：(4)'authority like' + pattern +''
			BasicDBObject obj3 = new BasicDBObject();
			obj3.put("name", pattern); //相当于SQL语句：(5)'name like' + pattern +''
			
			dbList.add(obj1);
			dbList.add(obj2);
			dbList.add(obj3);
			
		}		
		
		//用户工作状态为 0（暂停）和 1（正常）对管理员是可见的
		BasicDBObject object1 = new BasicDBObject();
		object1.put("workState", 0); //相当于SQL语句：(6)'workState = 1'
		BasicDBObject object2 = new BasicDBObject();
		object2.put("workState", 1);//相当于SQL语句：(7)'workState = 0'
		
		BasicDBList objList = new BasicDBList();
		objList.add(object1);
		objList.add(object2);
		
		/****   整合两个条件 and   ****/
		BasicDBObject cond1 = new BasicDBObject();
		cond1.put("$or", dbList);// (1) 、 (2)、(3)(4)(5) 为三个 or 操作==== or 组合1
		BasicDBObject cond2 = new BasicDBObject();
		cond2.put("$or", objList);// (7)与(6)为 一个or 操作 ===== or组合2
		
		BasicDBList list = new BasicDBList();
		list.add(cond1);
		list.add(cond2);
		BasicDBObject cond = new BasicDBObject();
		cond.put("$and", list); // 两个 or 组合整理在一起，是 and 组合；既要满足组合1 的条件，又要满足组合2 的条件

		return cond;
	}
	
	
	/**查询申报表reportTable
	 * */
	public BasicDBObject queryReport(Object parameter , int userAuthority , String userid )
	{
		BasicDBList dbList = new BasicDBList();
		Pattern pattern = Pattern.compile("^"+parameter+".*$", Pattern.CASE_INSENSITIVE);  //左匹配
		Pattern p = Pattern.compile("[\u4e00-\u9fa5]"); // 汉字编码区间
        Matcher m = p.matcher(parameter.toString());
        Pattern pdate = Pattern.compile("(\\d{1,4}[-|\\/|年|\\.]\\d{1,2}[-|\\/|月|\\.]\\d{1,2}([日|号])?(\\s)*(\\d{1,2}([点|时])?((:)?\\d{1,2}(分)?((:)?\\d{1,2}(秒)?)?)?)?(\\s)*(PM|AM)?)", Pattern.CASE_INSENSITIVE|Pattern.MULTILINE);  
        Matcher mdate = pdate.matcher(parameter.toString());

        //含有中文，匹配生产商，也可能是申报人或者审核人的姓名,或者产品类型名称
        if( m.find() )
  		{
        	//检测该中文字符串在申报表里是否存在
        	BasicImplentsDao dao = new BasicImplentsDao();
        	User user = (User) dao.getByOne(User.class, "name", parameter);
        	if( user != null )// 表明有这样一个人存在
        	{
        		//System.out.println("存在 "+ user.get_id());
        		BasicDBObject obj1 = new BasicDBObject();
        		obj1.put("reportPerson", user.get_id());//(1)申报人 ' reportPerson = ..id '
        		BasicDBObject obj2 = new BasicDBObject();
        		obj2.put("checkPerson", user.get_id());//(2)审核人  ' checkPerson = ..id '
        		
        		dbList.add(obj1);
        		dbList.add(obj2);
        	}
        	else {
        		BasicDBObject obj2 = new BasicDBObject();
      			obj2.put("producer", pattern); //相当于SQL语句：(3)'producer like' + pattern +''
      			dbList.add(obj2);
      			
      			//匹配产品类型名称
      			BasicDBObject dObject = queryProductType(parameter);
      			List<Map<String , Object>> list = getInfo(dObject, "productTypeTable");//接收查询到的产品类型ID
      			List<Object> tList = new ArrayList<Object>();
      			try {
      				
      				if( list != null && list.size() > 0 )
      				{
      					for( Map<String,Object> map:list)
      					{
      						//for(String key : map.keySet() )
      						//{
      						//JSONObject jsonObject = JSONObject.fromObject(list.get(i));
      						//System.out.println("获取到的产品类型信息"+jsonObject);
              				//tList.add(jsonObject.getString("_id"));
              				//System.out.println("获取到一个产品类型为"+jsonObject.get("_id"));
      						tList.add(map.get("_id"));
      						//System.out.println(map.get("_id")+" : "+map.get("productTypeName"));
      						//System.out.println(key);
						//}
      					}
      					
      				}
      				
      			} catch (Exception e) {
      				log.error("发生异常，模糊查询数据时，检索产品类型信息出错： " , e);
      			}
      			
      			if( tList.size() > 0 )
      			{
      				for (int j = 0; j < tList.size(); j++) {
      					BasicDBObject obj3 = new BasicDBObject();
              			obj3.put("productTypeID", tList.get(j).toString());
              			//tList.get(j)里面存放的是ObjectId类型数据，这个数据在前台表中不作为主键，需转为字符串类型方能匹配
              			dbList.add(obj3);
              			
              			//System.out.println(tList.get(j));
					}
      				
      			}
			}
  			
  		}	
		//含有日期格式的字符串
        else if( mdate.find() )
		{
			BasicDBObject obj1 = new BasicDBObject();
			obj1.put("reportStartDate", pattern); //相当于SQL语句：(4)'reportStartDate like' + pattern +''
			BasicDBObject obj2 = new BasicDBObject();
			obj2.put("reportEndDate", pattern); //相当于SQL语句：(5)'reportEndDate like' + pattern +''
			BasicDBObject obj3 = new BasicDBObject();
			obj3.put("reportTime", pattern); //相当于SQL语句：(6)'reportTime like' + pattern +''
			
			dbList.add(obj1);
			dbList.add(obj2);
			
		} 
        //其他匹配
		else {
			pattern = Pattern.compile("^.*"+parameter+".*$", Pattern.CASE_INSENSITIVE);   //模糊匹配
			BasicDBObject obj1 = new BasicDBObject();
			obj1.put("productBatch", pattern); //相当于SQL语句：(7)'productBatch like' + pattern +''
			BasicDBObject obj2 = new BasicDBObject();
			obj2.put("productTypeName", pattern);
			dbList.add(obj1);
			dbList.add(obj2);
		}	
		
        
        BasicDBObject cond1 = new BasicDBObject();
		cond1.put("$or", dbList);// (1)(2) 、 (3) 、 (4)(5)(6) 、 (7) 为三个 or 操作==== or 组合1
		
		BasicDBList list = new BasicDBList();
        BasicDBList objList = new BasicDBList();
		//权限问题，如果为普通用户，那么只能查看自己的申报信息
        if( userAuthority == 0 )
        {
        	if( userid != null && userid.length() > 0 )
        	{
        		BasicDBObject obj1 = new BasicDBObject();
        		obj1.put("reportPerson", userid);//(8)申报人 ' reportPerson = ..id '
        		
        		objList.add(obj1);
        	}
        	else {
				log.info("你的权限是普通用户，只能查看自己的信息，但没有通过你的id，无法操作。。。");
				return null;
			}
        	//加上普通用户的查询限定条件
        	BasicDBObject cond2 = new BasicDBObject();
    		cond2.put("$or", objList);// (8)为 一个操作 ===== 组合2
    		
    		/**** 整合两个组合操作  ****/
    		list.add(cond1);
    		list.add(cond2);
        }
        else
        {
        	list.add(cond1);
        }
		
		BasicDBObject cond = new BasicDBObject();
		cond.put("$and", list); // 两个 or 组合整理在一起，是 and 组合；既要满足组合1 的条件，又要满足组合2 的条件

		return cond;
	}

	
	/**查询产品表productTable
	 * */
	public BasicDBObject queryProduct(Object parameter)
	{
		//String parameter = obj.toString().replace(" ", "");//将字符串中的空格去掉
		BasicDBList dbList = new BasicDBList();
		Pattern pattern = Pattern.compile("^"+parameter+".*$", Pattern.CASE_INSENSITIVE);  //左匹配
		Pattern p = Pattern.compile("[\u4e00-\u9fa5]"); // 汉字编码区间
        Matcher m = p.matcher(parameter.toString());
        //公司MAC地址开头 DC A3 AC
        String s = parameter.toString().replace(" ", "");
        
		//参数带有中文字符,输入的可能是产品类型名
		if( m.find() )
		{
			BasicDBObject obj1 = new BasicDBObject();
			BasicDBObject obj2 = new BasicDBObject();
			
			//匹配产品类型名称
			ProductDataDao typeDao = new ProductDataDao();
			List<Map<String, Object>> typeList = typeDao.queryProductType(parameter);
			if( typeList != null )
			{
				//遍历产品类型数据
				for( Map<String , Object> type : typeList)
				{
					BasicDBObject object = new BasicDBObject();
					object.put("productTypeID", type.get("_id").toString());
					dbList.add(object);
				}
			}
		}
		else if( s.indexOf("DCA3") != -1 )
		{
			BasicDBObject obj1 = new BasicDBObject();
			obj1.put("MAC", pattern); //相当于SQL语句：(1)'MAC like' + pattern +''
			dbList.add(obj1);
		}
		else {
			BasicDBObject obj1 = new BasicDBObject();
			obj1.put("deviceID", pattern); //相当于SQL语句：(3)'deviceID like' + pattern +''
			BasicDBObject obj2 = new BasicDBObject();
			obj2.put("productTypeID", pattern); //相当于SQL语句：(4)'productTypeID like' + pattern +''
			BasicDBObject obj3 = new BasicDBObject();
			obj3.put("productBatch", pattern); //相当于SQL语句：(5)'productBatch like' + pattern +''
			
			dbList.add(obj1);
			dbList.add(obj2);
			dbList.add(obj3);
		}
        
		
		
		BasicDBObject cond = new BasicDBObject();
		cond.put("$or", dbList);
		

		return cond;
	}
	

	/**查询测试表testTable
	 * */
	public BasicDBObject queryTest(Object parameter)
	{

		BasicDBList dbList = new BasicDBList();
		Pattern pattern = Pattern.compile("^"+parameter+".*$", Pattern.CASE_INSENSITIVE);  //左匹配
		
		//公司MAC地址开头 DC A3 AC
		String s = parameter.toString().replace(" ", "");
		if( s.indexOf("DCA3") != -1 )
		{
			BasicDBObject obj1 = new BasicDBObject();
			obj1.put("MAC", pattern); //相当于SQL语句：(1)'MAC like' + pattern +''
			dbList.add(obj1);
		}
		else {
			BasicDBObject obj1 = new BasicDBObject();
			obj1.put("productBatch", pattern); //相当于SQL语句：(3)'productBatch like' + pattern +''
			BasicDBObject obj2 = new BasicDBObject();
			obj2.put("deviceID", pattern); //相当于SQL语句：(4)'deviceID like' + pattern +''
			BasicDBObject obj3 = new BasicDBObject();
			obj3.put("testingDate", pattern); //相当于SQL语句：(5)'testingDate like' + pattern +''
			
			dbList.add(obj1);
			dbList.add(obj2);
			dbList.add(obj3);
		}
        
		
		
		BasicDBObject cond = new BasicDBObject();
		cond.put("$or", dbList);
		

		return cond;
	}
	
	/**查询产品类型表productTypeTable
	 * */
	public BasicDBObject queryProductType(Object parameter)
	{

		BasicDBList dbList = new BasicDBList();
		Pattern pattern = Pattern.compile("^"+parameter+".*$", Pattern.CASE_INSENSITIVE);  //左匹配
		
		Pattern p = Pattern.compile("[0-9]+?"); // 汉字编码区间
        Matcher m = p.matcher(parameter.toString());
		
        //参数中含有数字，可以匹配产品固件版本和软件版本，MAC数量
		if( m.find() )
		{
			BasicDBObject obj1 = new BasicDBObject();
			obj1.put("hardwareVersion", pattern); //相当于SQL语句：(1)'hardwareVersion like' + pattern +''
			BasicDBObject obj2 = new BasicDBObject();
			obj2.put("softwareVersion", pattern); 
			BasicDBObject obj3 = new BasicDBObject();
			obj3.put("macNumber", pattern);
			
			dbList.add(obj1);
			dbList.add(obj2);
			dbList.add(obj3);
		}
		//匹配产品类型名称
		else {
			BasicDBObject obj1 = new BasicDBObject();
			obj1.put("productTypeName", pattern); //相当于SQL语句：(2)'productTypeName like' + pattern +''
			
			dbList.add(obj1);
			
		}
        
		BasicDBObject cond = new BasicDBObject();
		cond.put("$or", dbList);
		
		return cond;
	}

	
}
