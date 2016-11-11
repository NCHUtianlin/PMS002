package service.basicService.internal;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import service.basicService.requestService.UserService;
import service.interfaceService.BasicImplementService;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import dao.MacDao;
import dao.ProductDao;
import dao.basic.BasicImplentsDao;
import domain.Product;
import domain.ProductType;


/***
 *  系统内部完成的业务
 *  1. 申请deviceID
 *  2. 申请和分配MAC地址
 *  3. 批量添加产品信息
 * **/

@Service
public class OperProductData extends BasicImplementService {

	private static Log log = LogFactory.getLog(OperProductData.class.getName());
	
	//@Autowired
	private static ProductDao dao ;
	public void setDao(ProductDao dao) {
		this.dao = dao;
	}



	/**************审核通过，给该批次的产品分配MAC地址
	 * 
	 * @param productType 产品类型
	 * @param totalQuantity 计划生产数量
	 * @param num 每个产品需要的MAC数目
	 */
	public List applyMac( String productType , int totalQuantity , int num )
	{
		/*** 先判断MAC可用数量是否满足需求，否则返回null
		 *  1. 申请deviceID
		 *  2. 查询可用的mac数目是否满足需求,如果不满足，则返回null
		 *  3. 分配可用MAC地址段
		 *  4. 将MAC分配，num 个MAC对应一个deviceID 
		 *  结束
		 */
		log.info("进入applyMac()方法中申请MAC地址");
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		
		//申请deviceID
		ArrayList<String> deviceIDs = getDeviceID( productType , totalQuantity );

		//如果deviceID是连续增长的话，那么就可以先从deviceID管理表中获取最后一个deviceID，在次基础上新增deviceID
		MacDao dao = new MacDao();
		List<String> MACList = dao.applyMac(productType, totalQuantity, num);
		//申请成功，则返回含有mac的list； 服务器出错，申请失败，则返回一个空的list ； 没有资源，拒绝申请，返回null；
		if( MACList == null )
		{
			return null;
		}
		else if( MACList.size() == 0 )
		{
			return list;//返回一个空的list；
		}
		else {
			//将mac与deviceID进行整合，一个deviceID对应num个mac
			int macNo = 0;
			for( int i=0 ; i < deviceIDs.size() ; i++ )
			{
				List<String> macList = new ArrayList<String>();
				for( int j = 0 ; j < num ; j ++ )
					macList.add("DCA3AC"+ MACList.get(macNo++));//加上前六位十六进制数，该六位是有IEEE组织给出的
				
				/*//分配MAC之前，要查询当前MAC可用量是否足够
				String mac = "DCA3AC";
				for( int j = 0 ; j < num ; j ++ )
				{
					int n = Integer.parseInt(""+i+j);
					String s = Integer.toHexString(n).toUpperCase();
					macList.add(mac +""+ s);
				}*/
				
				Map<String, Object> m = new HashMap<String, Object>();
				m.put(deviceIDs.get(i), macList);
				list.add(m);
			}
			if( list.size() > 0 )
			{
				log.info("申请MAC地址成功");
			}
			
		}//--申请mac成功
		
		log.info("applyMac()方法 结束");
		return list;
		
	}
	

	/**** 申请deviceID
	 * @param totalQuantity 需要deviceID的数量
	 * @param productType 产品类型
	 * ***/
	public ArrayList<String> getDeviceID( String productType , int totalQuantity )
	{
		log.info("进入getDeviceID()方法中申请deviceID");
		
		ArrayList<String> arrayList = new ArrayList<String>();
		String type = "type";
		if( "门铃".equals(productType) )
			type = "voice";
		else if ( "门锁".equals(productType) )
			type = "lock";
		else {
			type ="other";
		}
		
		
		SimpleDateFormat df = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");//设置日期格式
		
		for( int i=0; i<totalQuantity; i++ )
		{
			Date d = new Date();
			String date = df.format(d);
			arrayList.add( "DEV_HY_"+ type +"_2016"+date+""+i );
		}
		
		log.info(" 申请deviceID完毕，getDeviceID()方法 结束");
		return arrayList;
	}
	
	/****
	 * 添加产品信息 》》》》》当审核通过的时候，系统批量添加产品信息，用户没有权限添加产品信息
	 * 1. 通过产品类型编号，获取macNumber（每个产品的mac数量）
	 * 2. 申请deviceID,分配MAC，
	 * 3. 申请成功后，批量插入产品信息
	 * @param typeid
	 * @param totalQuantity
	 * @param productBatch
	 * @return
	 * 
	 */
	public int addProduct( String userName , String productBatch )
	{
		log.info("进入addProduct()方法中批量生产产品基本信息（deviceID，MAC，生产批次，产品类型，产品状态）");
		
		//根据批次在申报表中查询出该批次的产品类型和计划生产数量
		String typeid = "" ;
		int totalQuantity = -1 ;
		try {
			Map<String , Object > rMap = new HashMap<String, Object>();
			rMap.put("productBatch", productBatch);
			List<Map<String , Object>> report = dao.getInfo( "reportTable" , rMap );
			if( report == null || report.size() == 0 )
			{
				log.info(" 批量添加产品失败，该批次("+productBatch+")在申报表中不存在，addProduct()方法 结束");
				return -1;
			}
			for( Map<String , Object> m : report )
			{
				typeid = m.get("productTypeID").toString();
				totalQuantity = Integer.parseInt( m.get("reportQuantity").toString() );
			}
				
		} catch (Exception e) {
			log.error("管理员"+userName+"审核批次为"+productBatch+"的申报时批准了，系统准备批量生成产品信息，此时获取相关申报信息发生异常：" , e );
			log.info(" 批量添加产品失败，addProduct()方法 结束");
			return -1;
		}
		
		//获取 该产品需要几个MAC , 该产品是什么类型
		ProductType ptype = (ProductType) dao.getById(ProductType.class, typeid);
		
		//System.out.println("产品类型名称"+ ptype.getProductTypeName());
		
		int macNumber = ptype.getMacNumber();
		String typeName = ptype.getProductTypeName();
		
		//申请deviceID 、 分配MAC
		List<Map<String , Object>> list = applyMac(typeName, totalQuantity, macNumber );
		
		List<DBObject> dataList = new ArrayList<DBObject>();
		
		if( list != null && list.size() > 0 )
		{
			//************  批量添加产品信息  **************
			for( Map<String, Object> m : list )
			{
				BasicDBObject dbObject = new BasicDBObject();
				
				for( String key : m.keySet() )
				{
					//System.out.println( key+" : "+ m.get(key));
					dbObject.put("deviceID", key);
					dbObject.put("MAC", m.get(key));
					dbObject.put("productTypeID", typeid);
					dbObject.put("productBatch", productBatch);
					dbObject.put("productState", -1);//生产中
					
				}
				dataList.add(dbObject);
				//System.out.println("dataList"+dataList);
			}
			
		}
		else if ( list == null ){
			log.info("MAC资源不足，无法满足需求");
			log.info(" 该批次("+productBatch+")批量添加产品失败，addProduct()方法 结束");
			return -64;
		}
		else {
			log.info("申请mac出错，请稍后重试");
			log.info(" 该批次("+productBatch+")批量添加产品失败，addProduct()方法 结束");
			return -75;
		}

		//批量插入产品信息
		int k = dao.insert(dataList);
		if( k > 0 )
		{
			log.info(" 该批次("+productBatch+")批量添加产品成功，addProduct()方法 结束");
			return 1;
		}
		else {
			log.info(" 该批次("+productBatch+")批量添加产品失败，addProduct()方法 结束");
			return -1;//批量添加信息失败
		}
		
	}
	

	/***
	 *  当该批次的产品生产完成
	 *  用户提交生产完成记录信息后，那么批量更新产品信息
	 *  更新方式：通过批次获取该批次的全部deviceID，然后在测试信息表中找出对应的测试信息
	 *  测试通过 1 ，更新产品状态为成功 1 ；测试失败，更新产品状态 为失败 0；没有测试信息的，更新产品状态未知
	 *  
	 * ***/
	public void updateProduct(String productBatch)
	{
		//根据批次从产品信息表中获取该批次的产品ID，然后通过产品ID获取测试数据
		// 1.根据批次从产品信息表中获取该批次的产品ID
		Map<String, Object> testMap = new HashMap<String, Object>();
		testMap.put("productBatch", productBatch);
		
		List<Map<String, Object>> list = dao.getInfo("productTable", testMap);
		
		if( list != null && list.size() > 0 )
		{
			for( Map<String, Object> m : list )
			{
				//Map<String, Object> map = new HashMap<String, Object>();
				String deviceID = m.get("deviceID").toString();
				//System.out.println(deviceID);
				
				Map<String, Object> dMap = new HashMap<String, Object>();
				dMap.put("deviceID", deviceID);
				// 2. 然后通过产品ID获取测试数据
				List<Map<String , Object>> testList = dao.getInfo("testingTable", dMap);
				//有该产品的测试数据
				if( testList != null && testList.size() > 0 )
				{
					for( Map<String, Object> dm : testList )
					{
						int result = Integer.parseInt(dm.get("result").toString().trim());
						
						if( result == 1 )
							dMap.put("productState", 1);
						else if( result == 0 )
							dMap.put("productState", 0);
						else
							dMap.put("productState", 2);
					}
					//通过deviceID 更新产品状态
					///System.out.println(deviceID+" : "+map);
					dao.updateById("deviceID", deviceID, dMap);
				}//-----有该产品的测试数据
				
			}//---遍历该批次的每一个产品
			
		}
		
	}
	
	

	
}
