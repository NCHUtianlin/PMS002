package dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.formula.ptg.IntPtg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import service.basicService.internal.BitMac;
import service.basicService.requestService.UserService;
import dao.basic.BasicImplentsDao;
import domain.MACInfo;
import domain.Product;

public class MacDao extends BasicImplentsDao {

	private static Log log = LogFactory.getLog(MacDao.class.getName());
	
	private BitMac bitMac = new BitMac();
	@Autowired
	private static MongoTemplate mongoTemplate;
	public void setMongoTemplate(MongoTemplate mongoTemplate) {
		System.out.println("MacDao开始注入。。。。。");
		this.mongoTemplate = mongoTemplate;
		//initMacTable();
	}
	
	private void initMacTable()
	{
		List<MACInfo> list = new ArrayList<MACInfo>();
		bitMac = new BitMac(1024);
		for( int i=0 ; i < Math.pow(2, 14) ; i ++ )
		{
			MACInfo macInfo = new MACInfo( i , 1024 , bitMac.getmBits());
			list.add(macInfo);
			//System.out.println(macInfo);
			System.out.println(i+"　："+ list.get(i) );
		}
		System.out.println("list的长度："+list.size());
		
		mongoTemplate.insertAll(list);
		
	}
	//从MACTable表中获取指定的一条数据，从0开始
	public List getOne(int row)
	{
		DBCollection table = mongoTemplate.getCollection("MACTable");
		List<DBObject> list = new ArrayList<DBObject>();
		DBCursor cursor = table.find().skip(row).limit(1);
		while( cursor.hasNext() )
		{
			list.add(cursor.next());
		}
		if( list.size() == 0 )
		{
			return null;
		}
		else {
			return list;
		}
	}
	//将从数据库中获取的对象转为实体类对象
	private MACInfo toMacInfo(int row)
	{
		
		List<Map<String , Object>> list = getOne(row);
		if( list != null )
		{
			JSONObject jsonObject = JSONObject.fromObject(list.get(0).toString());
			MACInfo macInfo = (MACInfo) jsonObject.toBean(jsonObject , MACInfo.class);
			
			return macInfo;
			
		}
		
		return null;
	}
	//通过MAC地址查询MAC的使用状态
	public int getMacState(String mac)
	{
		
		//十六进制转十进制
		int macNum = Integer.parseInt(mac , 16);
		int row = (int) Math.ceil( macNum/1024f );//计算出该MAC在表中第几行数据里
		macNum = macNum - (row-1)*1024;
		System.out.println(mac+": "+macNum+"在表中row="+row);
		
		MACInfo macInfo = toMacInfo(row);
		int[] mBits = macInfo.getMAC();
		if( mBits != null )
		{
			int index = (int)Math.floor( macNum/32f );
			if( mBits[index] == (mBits[index] | 1 << ( 31 - macNum%32 ) ) )
				return 1;//不可用
			else
				return 0;//可用
		}
		else {
			log.info(mac+"不存在");
			return -1;//出错了
		}
		
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
		 *  结束 ： 成功，则返回含有mac的list； 服务器出错，申请失败，则返回一个空的list ； 没有资源，拒绝申请，返回null；
		 */
		List<String> list = new ArrayList<String>();//存放分配的mac
		int requireNum = totalQuantity * num ;//mac需求总量
		int row = 0;
		int total = 0;
		boolean flag = true;
		//判断可用MAC数量是否满足需求
		while(flag)
		{
			//无法满足需求
			if( row >= Math.pow(2, 14))
				break;
			
			if( total >= requireNum )
			{
				flag = false;
				break;
			}
			else{
				MACInfo macInfo = toMacInfo(row);
				total += macInfo.getUsableNum();
				
			}
			
			row++;
			
		}//---判断可用MAC数量是否满足需求
		//无法满足需求
		if(flag)
		{
			log.info("mac资源不足，拒绝请求");
			return null;
		}
		//可以满足需求
		else
		{
			flag = true;
			row = 0;
			//肯定是需要跨行，则将它分割: 以512个为一个单位分块
			if( requireNum > 1024 )
			{
				for( int i=0 ; i < requireNum/512 ; i++ )
				{
					List<String> mList = searchMacs(512);
					if( mList != null && mList.size() > 0 )
					{
						for( int j = 0 ; j < mList.size() ; j ++ )
						{
							list.add(mList.get(j));//统一放进一个list中
						}
					}
				}
				//requireNum可能不能整除512，剩余的少量mac还要进行分配
				int pnum = requireNum % 512;
				if( pnum != 0 )
				{
					List<String> pList = searchMacs(pnum);
					if( pList != null && pList.size() > 0 )
					{
						for( int j = 0 ; j < pList.size() ; j ++ )
						{
							list.add(pList.get(j));//统一放进一个list中
						}
					}
				}
		
				
			}//---需求量大于1024
			//需求量少于等于1024
			else {
				list = searchMacs(requireNum);
				
			}
			
			
		}//-- 可以满足需求
		
		if( list != null  )
		{
			return list;
		}
		else {
			return null;
		}
		
	
	}
 	//需求量少于等于1024,时选取mac
	private List searchMacs(int requireNum )
	{
		boolean flag = true;
		int row = 0;
		int end = 0;//当有一段连续的可用MAC地址段满足需求时，记录下该段的最后一个MAC的位置
		int lastNum = 0;//记录前一行最后的连续可用的MAC数量
		while(flag)
		{
			
			MACInfo macInfo = toMacInfo(row);
			//得到第row行中有可用MAC数目
			int usableNum = macInfo.getUsableNum();
			//得到第row行的数组
			int[] mBits = macInfo.getMAC();
			if( usableNum >= requireNum )
			{
				int len = 0;
				for( int i=0 ; i < 1024 ; i++ )
				{
					if( lastNum > 0 )
					{
						len = lastNum;//接着前一行可用MAC数量
					}
					
					int index = (int)Math.floor( i/32f );
					if( !(mBits[index] == (mBits[index] | 1 << ( 31 - i%32 ) )) )
					{
						len++;
						if( len == requireNum )
						{
							flag = false;
							end = i;//记录下最后一个MAC的位置
							break;
						}
					}
					else {
						len = 0;
					}
					if( i == 1023 )
					{
						lastNum = len;
					}
				}//--从该数组的第一个i=0开始遍历
				
				if( !flag )
				{
					break;//跳出外层while循环
				}
				
				row++;
				if( row == Math.pow(2, 14))
				{
					break;//没有找到连续可用的MAC地址段
				}
				
				
			}
			
		}//--外层while循环
		
		List<String> list = new ArrayList<String>();
		//有连续可用的MAC地址段，则将MAC地址装进list中
		if( !flag )
		{
			
			//在一行数据里
			if( end >= requireNum-1 )
			{
				
				for( int i = end; i > end - requireNum ; i-- )
				{
					String m = Integer.toHexString(row*1024 + i ).toUpperCase();
					for( int k = m.length() ; k  <= 6 ; k ++ )
					{
						m = "0" + m;//前面补0
					}
					list.add( m );
				}
				
			}
			//在两行数据里
			else {
				for( int i = end; i >= 0 ; i-- )
				{
					String m = Integer.toHexString(row*1024 + i ).toUpperCase();
					for( int k = m.length() ; k  <= 6 ; k ++ )
					{
						m = "0" + m;//前面补0
					}
					list.add( m );
				}
				row--;//前一行数据
				for( int i= 1023 ; i > 1024-(requireNum-end) ; i-- )
				{
					String m = Integer.toHexString(row*1024 + i ).toUpperCase();
					for( int k = m.length() ; k  <= 6 ; k ++ )
					{
						m = "0" + m;//前面补0
					}
					list.add( m );
				}
				
			}
			
			
			
		}
		//没有连续可用的MAC地址段，则分配零散的MAC地址
		else {
			int count = 0 ;
			
			for ( int i = 0 ; count < requireNum ; i ++ )
			{
				MACInfo macInfo = toMacInfo(i);
				if( macInfo.getUsableNum() > 0 )
				{
					int[] mBits = macInfo.getMAC();
					for( int j = 0 ; j < mBits.length ; j ++ )
					{
						if( mBits[j] == 0 )//可用
						{
							int macN = i * 1024 + j;
							String macString = Integer.toHexString(macN);
							list.add(macString);
							count++;
							if( count >= requireNum )
								break;
						}
					}
					
				}
				
			}
			
			
		}
		
		//申请MAC成功，那么需要将这些MAC的状态置为1
		int k = set1(list,row);
		//mac状态更新成功
		if( k > 0 )
		{
			return list;
		}
		//mac地址状态更新失败，返回一个空的list，表示有资源但是申请失败，重新申请
		else {
			list.clear();
			return list;
		}
		
	}
	
	//mac申请成功后，将该MAC置为1
	public int set1( List<String> list , int row )
	{
		if( list != null )
		{
			try {
				List<MACInfo> macList = new ArrayList<MACInfo>();
				int[] mBits = new int[32];
				for( int i = 0 ; i < list.size() ; i ++ )
				{
					int mac = Integer.parseInt(list.get(i) , 16);
					System.out.println(mac);
					int index = (int)Math.floor( mac/32f );
					//把该整数的第 n = 1 + 31-pos%32 位置为1,这个操作采用该整数与移位后的1进行或运算，使指定位置为1
					mBits[index] = mBits[index] | ( 1 << ( 31 - mac%32 ) );// 1左移n位，右边补足n个0，然后1和任何数相或都是1
					
				}
				
				//?????????????????????????????
				//如何更新==>> 我在表结构中加入了row字段，从0 开始计数
				MACInfo macInfo = (MACInfo) toMacInfo(row);
				int usableNumNow = macInfo.getUsableNum() - list.size();
				//MACInfo macInfo2 = new MACInfo(row , usableNumNow , mBits );
				Update update = new Update();
				update.set("usableNum", usableNumNow);
				update.set("MAC", mBits);
				mongoTemplate.updateFirst(new Query(new Criteria().where("row").is(row)), update, MACInfo.class);
			
			} catch (Exception e) {
				log.error("mac地址申请到了之后，需要将mac的状态置为1，处理过程中发生异常：" , e );
				return -1;
			}
		}
		
		return 1;
		
	}
	//产品被废弃后，MAC被回收，MAC状态置为0
	public int set0( String macString )
	{
		log.info("接收到的参数macString="+macString+"是要被置为0的mac地址");
		
		String mac = macString.substring(5);//MAC地址是12位十六进制数，前六位由IEEE组织给公司的，所以公司分配的实际是后六位
		System.out.println(mac);
		int M = Integer.parseInt(mac , 16);//将十六进制数转换为十进制
		int row = M/1024;//该MAC在表中所在行
		int Mac = M%1024;//位置
		int index = (int) Math.floor( Mac/32f );//该MAC在数组中的索引
		
		try {
			//取出现在该行的MAC数据，得到存放MAC状态的数组
			MACInfo macInfo = toMacInfo(row);
			int[] mBits = macInfo.getMAC();
			mBits[index] = mBits[index] & ~ ( 1<<(31-Mac%32) );
			
			Update update = new Update();
			update.set("MAC", mBits);
			mongoTemplate.updateFirst(new Query(new Criteria().where("row").is(row)), update, MACInfo.class);
			
			
		} catch (Exception e) {
			log.error("将MAC状态置为0的过程中发生异常：" , e );
			return -1;
		}
		
		return 1;
	}
	
	//通过MAC获取该mac的信息
	public List getByMac(String MAC)
	{
		List list = new ArrayList();
		String mac = MAC.replace(" ", "");
		//获取mac的状态
		int state = getMacState(mac);
		//从产品表中获取该mac的使用情况，对应的deviceID
		BasicDBObject dbObject = new BasicDBObject();
		dbObject.put("MAC", MAC);
		BasicDBObject cond = new BasicDBObject();
		cond.put("$all", dbObject);
		List pList = new ArrayList();
		try {
			DBCursor cursor = mongoTemplate.getCollection("productTable").find(cond);
			while( cursor.hasNext() )
			{
				pList.add(cursor.next());
			}
			
		} catch (Exception e) {
			log.error("通过mac在产品表中搜索产品信息时发生异常：" , e );
		}
		
		if( pList.size() == 0 )
		{
			return null;
		}
		
		for( int i = 0 ; i < pList.size() ; i++ )
		{
			JSONObject json = JSONObject.fromObject(pList.get(i));
			String deviceID = json.getString("deviceID");
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("deviceID", deviceID);
			map.put("mac", MAC);
			map.put("macUseState", state);
			list.add(map);
		}
		
		if( list.size() == 0 )
		{
			return null;
		}
		else {
			return list;
		}
		
		
	}
	
	//通过deviceID获取mac信息== 在产品表里搜索出mac后，在mac表中查询对应mac的使用状态
	public List getByDeviceID( String deviceID)
	{
		BasicDBObject object = new BasicDBObject();
		object.put("deviceID", deviceID);
		List<DBObject> proList = new ArrayList<DBObject>();
		try {
			DBCursor cursor = mongoTemplate.getCollection("productTable").find(object);
			
			while( cursor.hasNext() )
			{
				proList.add(cursor.next());
			}
			
		} catch (Exception e) {
			log.error("通过deviceID获取产品信息时发生异常：" , e );
		}
		
		if( proList.size() == 0 )
		{
			return null;
		}
		
		List list = new ArrayList();
		try {
			JSONObject jsonObject = JSONObject.fromObject(proList.get(0).toString());
			Product product = (Product) jsonObject.toBean(jsonObject , Product.class);
			String[] MAC = product.getMAC();
			
			
			for( int i = 0 ; i < MAC.length ; i ++ )
			{
				String Mac = MAC[i].replace(" ", "").substring(5);
				int state = getMacState(Mac);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("deviceID", deviceID);
				map.put("MAC", MAC[i]);
				map.put("macUseState", state);
				list.add(map);
			}
			
		} catch (Exception e) {
			log.error("获取mac信息时发生异常：" , e );
		}
		
		
		if( list.size() == 0 )
		{
			return null;
		}
		else {
			return list;
		}
	}

	//通过批次获取mac地址信息
	//通过批次获取mac信息=》》》 这里控制分页不大好直接控制，我这里通过控制deviceID的数量来间接控制mac的分页数量
	public List getByBatch( String batch , int pageNow , int pageSize )
	{
		
		//通过批次在产品表中获取每一个产品的deviceID，然后通过deviceID获取信息
		BasicDBObject dbObject = new BasicDBObject();
		dbObject.put("productBatch", batch);
		List<DBObject> proList = new ArrayList<DBObject>();
		try {
			DBCollection table = mongoTemplate.getCollection("productTable");
			DBCursor cursor = null;
			//分页操作
			if( pageNow > 0 )
			{
				if( pageSize <= 0 )
					pageSize = 10;
				
				cursor = table.find(dbObject).skip( (pageNow-1)*pageSize ).limit(pageSize).sort(new BasicDBObject("_id",1));
			}
			//全部查询
			else {
				cursor = table.find(dbObject);
				
			}
			while( cursor.hasNext() )
			{
				proList.add(cursor.next());
			}
			
		} catch (Exception e) {
			log.error("通过批次获取产品信息时发生异常：" , e );
		}
		
		if( proList.size() == 0 )
		{
			return null;
		}
		
		List list = new ArrayList();
		try {
			
			for( int i = 0 ; i < proList.size() ; i++ )
			{
				JSONObject json = JSONObject.fromObject(proList.get(i));
				String deviceID = json.getString("deviceID");
				List mList = getByDeviceID(deviceID);//这里获取到了一个list数组
				//将mList解开，重新装入list
				if(mList != null && mList.size() > 0 )
				{	
					for( int j = 0 ; j < mList.size() ; j ++ )
					{
						JSONObject jsonList = JSONObject.fromObject(mList.get(j));
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("deviceID", deviceID);
						map.put("MAC", jsonList.get("MAC"));
						map.put("macUseState", jsonList.get("macUseState"));
						list.add(map);
					}
				}
				
			}
			
		} catch (Exception e) {
			log.error("通过批次获取mac数据时发生异常：" , e );
		}
		
		if( list.size() == 0 )
		{
			return null;
		}
		else {
			return list;
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
