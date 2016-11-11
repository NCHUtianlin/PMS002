package dao;


import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

import dao.basic.BasicImplentsDao;


/***专职统计数据
 * 
 *   testRate 测试率，由测试数量（有成功和失败）除以实际生产数量 （ 意义在于判断工厂是否对出厂的产品全部测试）
 *   totalQuantity 实际生产总数，从生产完成记录表中获取
 *   productBatch 生产批次 / productTypeName 产品类型  / producer 生产商
 *   passRatedf 产品合格率，有生产完成记录表中的合格数量除以实际生产总数
 *   perTimedf一天的生产量
 * **/
@Repository
public class DataAnalysisDao extends BasicImplentsDao {
	
	private static Log log = LogFactory.getLog(DataAnalysisDao.class.getName());
	
	private static MongoTemplate mongoTemplate;
	private DBCollection table;
	
	@Autowired
	public void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
		//System.out.println("DataAnalysisDao开始注入。。。。");
	}

	//通过生产批次进行统计
	public List productBatch()
	{
		/***
		 *  1. 遍历生产完成记录表里的信息（里面的信息都是代表已经生产完成），获取它的生产批次、实际生产总数、合格数量、时间
		 *  2. 在每一个批次中，统计它的生产总数、合格率、测试通过率、单产时间
		 * ***/

		List<Map<String , Object>> dataList = new ArrayList<Map<String,Object>>();//存放统计数据
// 1. 检索生产完成记录表		
		List<Map<String , Object>> list = findAll("productNoteTable");
		
		if( list != null && list.size() > 0 )
		{
			
// 1.1 遍历每一个批次（表里的每一条数据）
			for(Map<String , Object> map : list )
			{
				Map<String , Object> dataMap = new HashMap<String, Object>();
// 1.2 获取该批次的实际生产总量 => totalQuantity 、 批次  => productBatch 、 合格数量 =>计算产品合格率  、 实际生产开始时间 、 生产完成时间				
				int testNum = 0;
				try {
					int totalQuantity = Integer.parseInt( map.get("productTotalQuantity").toString() );//生产总数
					String productBatch = map.get("productBatch").toString();//批次
					Double usableNumber = Double.parseDouble( map.get("productUsableNumber").toString() );//合格数量
					Object start = map.get("productStartDate");
					Object end = map.get("productEndDate");
					//提取实际生产时间,计算一天的生产量
					long days = upTime( dateToString(start) , dateToString(end) );
// 1.3 从测试表中获取该批次的产品测试数量 => 计算测试率
					//累加一个批次的测试数量
					testNum += getTestNum(productBatch);
					
					DecimalFormat df = new DecimalFormat("0.0000");//格式化小数，不足的补0

// 1.4 将一个批次的统计数据存入Map	
					if( usableNumber <= 0 || totalQuantity <= 0 )
					{
						dataMap.put("passRate", 0 );//产品合格率，有生产完成记录表中的合格数量除以实际生产总数
					}
					else {
						double passRate = ( usableNumber / totalQuantity );//合格率
						String passRatedf = df.format(passRate);//返回的是String类型的
						dataMap.put("passRate", passRatedf);//产品合格率，有生产完成记录表中的合格数量除以实际生产总数
					}
					
					if( days <= 0 )
					{
						dataMap.put("perTime", 0 );//一天的生产量
					}
					else
					{
						double perTime = ( (float)totalQuantity/days );
						dataMap.put("perTime", df.format(perTime) );//一天的生产量
					}
					
					if( testNum <= 0 || totalQuantity <= 0 )
					{
						dataMap.put( "testRate", 0 );//测试率，由测试数量（有成功和失败）除以实际生产数量
					}
					else {
						double testRate = ( (float)testNum/totalQuantity );
						dataMap.put( "testRate", df.format(testRate) );//测试率，由测试数量（有成功和失败）除以实际生产数量
					}

					dataMap.put("totalQuantity", totalQuantity);//实际生产总数，直接从生产完成记录表中获取
					dataMap.put("productBatch", productBatch);//生产批次
					
				} catch (Exception e) {
					e.printStackTrace();
					log.error("发生异常，按批次统计失败 : "+e.getMessage());
					return null;
				}
// 1.5 将所有数据打包				
				dataList.add(dataMap);
				System.out.println(dataList);
				System.out.println();
				
			}//for循环
		}//生产完成记录表里有信息
		//没有信息
		else {
			return null;
		}
// 1.6 返回最终结果		
		return dataList;
	}
	
	//通过产品类型进行统计
	public List productType()
	{
		/****
		 *  1. 在产品类型表里检索所有产品类型
		 *  2. 每一个产品类型在申报表里有几个生产批次
		 *  3. 对应的批次在生产完成记录中是否存在对应数据，存在，则进行数据统计
		 *  4. 对该类型的产品的数据进行累计，得出最终统计结果
		 * ****/
		List<Map<String , Object>> dataList = new ArrayList<Map<String,Object>>();//存放统计数据
		
// 2. 检索产品类型表，获取所有产品类型数据		
		List<Map<String , Object>> typeList = findAll("productTypeTable");
		if( typeList != null && typeList.size() > 0 )
		{
			
// 2.1. 遍历每一个产品类型
			for( Map<String , Object> map : typeList)
			{
				Map<String , Object> dataMap = new HashMap<String, Object>();
				
				//System.out.println( map.get("_id"));
// 2.2 获取该产品类型名称 和 产品类型ID
				String productTypeName = map.get("productTypeName").toString();
				String typeid = map.get("_id").toString();
				
				int totalQuantity = 0;//该类型产品生产总数
				int usableNumber = 0;//该类型产品合格总数
				int totalDays = 0;//该类型产品生产总天数
				int testNum = 0;//累计该类型的产品测试了的数量
				
				
// 2.3 通过产品类型ID检索申报表，申报必须是审核通过的才是有效申报
				Map<String , Object> reportMap = new HashMap<String, Object>();
				reportMap.put("productTypeID", typeid);
				reportMap.put("checkResult", 1);
				List<Map<String , Object>> reportList = getInfo("reportTable", reportMap);
				//该产品类型存在申报信息
				if( reportList != null && reportList.size() > 0 )
				{
					String productBatch = "";
// 2.4 遍历该类型产品的每一个批次
					for( Map<String , Object> map2 : reportList )
					{
						try {
							//JSONObject jsonObject2 = new JSONObject().fromObject(reportList.get(j));//将json字符串转换为实体类对象
							
							//获取批次
							productBatch = map2.get("productBatch").toString();
// 2.5 通过批次检索生产完成记录表，获取该批次的生产数据
							Map<String , Object> noteMap = new HashMap<String, Object>();
							noteMap.put("productBatch", productBatch);
							List<Map<String , Object>> notelList = getInfo("productNoteTable", noteMap);
							//该批次已经生产完成才有完成记录信息
							if( notelList != null && notelList.size() > 0 )
							{
// 2.6 得到给批次的各项数据，对相关数据进行累加，因为一个类型的产品有好多个批次，所以需要将它的每一个批次的数据累加
								for( Map<String , Object> m : notelList )
								{
									Object start = m.get("productStartDate");
									Object end = m.get("productEndDate");
									totalQuantity += Integer.parseInt(m.get("productTotalQuantity").toString());
									usableNumber += Integer.parseInt(m.get("productUsableNumber").toString());
									
									long days = upTime( dateToString(start) , dateToString(end) );
									totalDays += days;
								}//在生产完成记录表里得到一个批次的数据
							
								//累加一个批次的测试数量
								testNum += getTestNum(productBatch);
							}
							
						} catch (Exception e) {
							log.error("发生异常，未能完成统计处理：",e );
						}
						
						
					}//在申报表里得到该类型产品的所有批次数据

// 2.7 将该产品类型的全部数据存入Map 
					DecimalFormat df = new DecimalFormat("0.0000");//格式化小数，不足的补0
					
					if( usableNumber <= 0 || totalQuantity <= 0)
					{
						dataMap.put("passRate", 0);
					}
					else {
						double passRate = (float)usableNumber/totalQuantity;
						String passRatedf = df.format(passRate);//返回的是String类型的
						dataMap.put("passRate", passRatedf);
					}
					
					if( totalDays == 0 )
					{
						dataMap.put("perTime", 0 );
					}
					else {
						System.out.println(totalQuantity +" , "+ totalDays);
						double perTime = (float)totalQuantity/totalDays;
						dataMap.put("perTime",df.format(perTime) );
					}
					
					if( testNum <= 0 || totalQuantity <= 0 )
					{
						dataMap.put( "testRate", 0 );
					}
					else {
						//测试率
						double testRate = ( (float)testNum/totalQuantity );
						dataMap.put( "testRate", df.format(testRate) );
						
					}
					
					dataMap.put("totalQuantity", totalQuantity);
					dataMap.put("productTypeName", productTypeName);
					
//2.8 将所有数据打包		
					dataList.add(dataMap);
				}
				
			}
			
		}
		else {
			return null;
		}
// 2.9 返回最终结果
		return dataList;
		
	}
	
	//通过产品类型进行统计
	public List producer()
	{
		/****
		 *  1. 在申报表里检索每一个生产商，取出相应的批次
		 *  2. 对应的批次在生产完成记录中是否存在对应数据，存在，则进行数据统计
		 *  3. 对该生产商的数据进行累计，得出最终统计结果
		 * ****/
		List<Map<String , Object>> dataList = new ArrayList<Map<String,Object>>();//存放统计数据

// 3 检索申报表里的所有数据，因为没有专门的生产商信息表，所以不知道生产商有哪些。全表搜索，将生产商一个一个取出放进set集合里
		List<Map<String , Object>> reportList = findAll("reportTable");
		if( reportList != null && reportList.size() > 0 )
		{
			Set<String> set = new HashSet<String>();
			Map<String , Object> producerMap = new HashMap<String, Object>();
// 3.1 遍历申报表里所有数据，获取生产商信息，存放入set集合
			for( Map<String , Object> map : reportList)
			{
				//获取生产商名称
				set.add(map.get("producer").toString());
				
			}
			
// 3.2 通过遍历set中的每一个生产商，获取申报表里的批次信息
			for(String s : set)
			{
				Map<String , Object> dataMap = new HashMap<String, Object>();
				
				int totalQuantity = 0;//该类型产品生产总数
				int usableNumber = 0;//该类型产品合格总数
				int testNum = -1;//累计该生产商已经生产并测试了的产品数量
				int totalDays = 0;//该类型产品生产总天数
				
// 3.3 在申报表中获取该生产商的所有批次				
				Map<String , Object> sMap = new HashMap<String, Object>();
				sMap.put("producer", s);
				List<Map<String , Object>> slList = getInfo("reportTable", sMap);
				if( slList != null && slList.size() > 0 )
				{
					
// 3.4 遍历该生产商的每一个批次
					for( Map<String , Object> map : slList )
					{
						String productBatch = "";
						try {
							
							productBatch = map.get("productBatch").toString();
// 3.5 通过批次获取生产完成记录信息
							Map<String , Object> noteMap = new HashMap<String, Object>();
							noteMap.put("productBatch", productBatch);
							List<Map<String , Object>> noteList = getInfo("productNoteTable", noteMap);
							
							if( noteList != null && noteList.size() > 0 )
							{
								for(Map<String , Object > map2 : noteList )
								{
									Object start = map2.get("productStartDate");
									Object end = map2.get("productEndDate");
									System.out.println(start+"  "+end);
									//JSONObject jsonObject = JSONObject.fromObject(note);
									totalQuantity += Integer.parseInt( map2.get("productTotalQuantity").toString() );
									usableNumber += Integer.parseInt( map2.get("productUsableNumber").toString() );
										
									long days = upTime( dateToString(start) , dateToString(end) );
									totalDays += days;
								}
							}
							//累加一个批次的测试数量
							testNum += getTestNum(productBatch);
							
						} catch (Exception e) {
							log.error("发生异常，未能完成统计处理："+e.getMessage());
						}
						
						
					}//遍历该生产商的每一个批次

// 3.6 将该生产商的数据存入Map
					DecimalFormat df = new DecimalFormat("0.0000");//格式化小数，不足的补0
					
					if( usableNumber <= 0 || totalQuantity <= 0)
					{
						dataMap.put("passRate", 0);
					}
					else {
						double passRate = (float)usableNumber/totalQuantity;
						String passRatedf = df.format(passRate);//返回的是String类型的
						dataMap.put("passRate", passRatedf);
					}
					
					if( totalDays == 0 )
					{
						dataMap.put("perTime", 0 );
					}
					else {
						System.out.println(totalQuantity +" , "+ totalDays);
						double perTime = (float)totalQuantity/totalDays;
						dataMap.put("perTime",df.format(perTime) );
					}
					
					if( testNum <= 0 || totalQuantity <= 0 )
					{
						dataMap.put( "testRate", 0 );
					}
					else {
						//测试率
						double testRate = ( (float)testNum/totalQuantity );
						dataMap.put( "testRate", df.format(testRate) );
						
					}
					
					
					dataMap.put("totalQuantity", totalQuantity);
					dataMap.put("producer", s);

// 3.7 将所有数据打包
					dataList.add(dataMap);
					
				}
				
			}//遍历所有生产商
						
		}			

// 3.8 返回最终结果
		return dataList;
		
	}
	
	
	//通过时间段进行统计:测试的数量，测试通过的数量，测试失败的数量 testTable( deviceID, testDate, result )
	//这里的start 和 end 应该要成为一个时间间隔
	public List time( String start , String end )
	{
		/**
		 *  ???? 时间间隔中，有不同批次的产品，不同产品类型的产品，可能还有正在生产中的、生产完成的产品，有测试成功的和失败的
		 *  那么统计该以哪一个为标准
		 *  或者：就只按时间段统计产品的生产量，这个功能可以方便产生出月报、年报
		 * **/
		//List<Map<String , Object>> dataList = new ArrayList<Map<String,Object>>();//存放统计数据
		//Map<String , Object> dataMap = new HashMap<String, Object>();
		
		//return dataList;
		return null;
	}
	

	//查询一张表里的全部数据
	public List findAll( String tableName )
	{
		List<Object> list = new ArrayList<Object>();
		table = mongoTemplate.getCollection(tableName);
		
		try {
			DBCursor cursor = table.find();
			while( cursor.hasNext() )
			{
				list.add(cursor.next());
			}
			//System.out.println("findAll():"+list);
			
		} catch (Exception e) {
			log.error("发生异常，获取表"+tableName+"全部信息失败： "+e.getMessage());
		}
		
		
		return list;
	}
	
	//Date数据转字符串
	public String dateToString( Object value )
	{
		//System.out.println("被转换对象："+value);
		if( value == null )
		{
			return null;
		}
		String d = "";
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			d = dateFormat.format(value);
			//System.out.println(d);
		} catch (Exception e) {
			log.info("Date类型转String发生异常：" , e);
		}
		
		return d;
	}	
	
	//求时间差
	public long upTime(String start , String end)
	{
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		System.out.println(start +" 时间 到"+ end );
		
		long days = -1;
		try
		{
			Date d1 = df.parse(start);
			Date d2 = df.parse(end);
			long diff = d2.getTime() - d1.getTime();
			days = diff / (1000 * 60 * 60 * 24);//天数
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("发生异常，时间差计算失败 ：" + e.getMessage());
		}
		//System.out.println("生产时间差"+days);
		if( days == 0 )
		{
			days = 1;
		}
		return days;
	}
	
	//提供生产批次，对产品表进行检索，通过产品表里的deviceID找出测试表里的测试数据，将该批次的测试数量计算出来
	public int getTestNum( String productBatch )
	{
		int testNum = 0;//累计该类型的产品测试了的数量
		
		//获取测试信息，计算测试率(只要测试了的产品，成功和失败的，这个有区别于测试通过率)
		Map<String , Object> map = new HashMap<String, Object>();
		map.put("productBatch", productBatch);
		List productlList = getInfo("productTable", map);//通过生产批次查询产品表，再通过产品表里的deviceID找到对应的测试数据
		if( productlList != null )
		{
			try {
				for (int p = 0; p < productlList.size() ; p++) {
					JSONObject productObject = JSONObject.fromObject(productlList.get(p));
					String deviceID = productObject.getString("deviceID");
					Map<String , Object> tMap = new HashMap<String, Object>();
					tMap.put("deviceID", deviceID);
					List testList = getInfo("testingTable", tMap);
					if( testList != null && testList.size() > 0 )
						testNum++;//该deviceID被测试过了
					
				}
				
			} catch (Exception e) {
				log.error("发生异常，统计"+productBatch+"批次的产品测试数量未能完成: "+e.getMessage());
			}
			
			
		}
		
		//System.out.println("该批次"+productBatch+"测试数量为"+testNum);
		return testNum;
	}

}



