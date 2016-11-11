package excel;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;
import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFComment;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

import service.basicService.requestService.UserService;
import excel.excelTemplate.Products;
import excel.excelTemplate.Users;


/**
 * 利用开源组件POI3.0.2动态导出EXCEL文档
 * 
 * @version v1.0
 * @param <T>
 *            应用泛型，代表任意一个符合javabean风格的类
 *            注意这里为了简单起见，boolean型的属性xxx的get器方式为getXxx(),而不是isXxx()
 *            byte[]表jpg格式的图片数据
 */
public class ExportExcel<T> {
	
	private static Log log = LogFactory.getLog(ExportExcel.class.getName());
	
	public static final String FILE_SEPARATOR = System.getProperties().getProperty("file.separator");//文件分隔符

	public void exportExcel(String title, List<Object[]> dataset, OutputStream out) {
		exportExcel( title, null, dataset, out, "yyyy-MM-dd");
	}

	public void exportExcel(String title, String[] headers , List<Object[]> dataset , OutputStream out) {
		exportExcel(title , headers, dataset, out, "yyyy-MM-dd");
	}

	public void exportExcel(String[] headers, List<Object[]> dataset, OutputStream out, String pattern) {
		exportExcel("测试POI导出EXCEL文档", headers, dataset, out, pattern);
	}

	/**
	 * 这是一个通用的方法，利用了JAVA的反射机制，可以将放置在JAVA集合中并且符号一定条件的数据以EXCEL 的形式输出到指定IO设备上
	 * 
	 * @param title
	 *            表格标题名
	 * @param headers
	 *            表格属性列名数组
	 * @param dataset
	 *            需要显示的数据集合,集合中一定要放置符合javabean风格的类的对象。此方法支持的
	 *            javabean属性的数据类型有基本数据类型及String,Date,byte[](图片数据)
	 * @param out
	 *            与输出设备关联的流对象，可以将EXCEL文档导出到本地文件或者网络中
	 * @param pattern
	 *            如果有时间数据，设定输出格式。默认为"yyy-MM-dd"
	 */
	@SuppressWarnings("unchecked")
	public void exportExcel(String title, String[] headers , List<Object[]> dataset, OutputStream out, String pattern) {
		// 声明一个工作薄
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 生成一个表格
		HSSFSheet sheet = workbook.createSheet(title);
		// 设置表格默认列宽度为20个字节
		sheet.setDefaultColumnWidth((short) 20);
		// 生成一个样式
		HSSFCellStyle style = workbook.createCellStyle();
		// 设置这些样式
		style.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style.setBorderTop(HSSFCellStyle.BORDER_THIN);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		// 生成一个字体
		HSSFFont font = workbook.createFont();
		font.setColor(HSSFColor.VIOLET.index);
		font.setFontHeightInPoints((short) 12);
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		// 把字体应用到当前的样式
		style.setFont(font);
		// 生成并设置另一个样式
		HSSFCellStyle style2 = workbook.createCellStyle();
		style2.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
		style2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style2.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style2.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style2.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style2.setBorderTop(HSSFCellStyle.BORDER_THIN);
		style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		// 生成另一个字体
		HSSFFont font2 = workbook.createFont();
		font2.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
		// 把字体应用到当前的样式
		style2.setFont(font2);
		// 声明一个画图的顶级管理器
		HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
		// 定义注释的大小和位置,详见文档
		HSSFComment comment = patriarch.createComment(new HSSFClientAnchor(0,
				0, 0, 0, (short) 4, 2, (short) 6, 5));
		// 设置注释内容
		comment.setString(new HSSFRichTextString("可以在POI中添加注释！"));
		// 设置注释作者，当鼠标移动到单元格上是可以在状态栏中看到该内容.
		comment.setAuthor("leno");
		// 产生表格标题行
		HSSFRow row = sheet.createRow(0);
		for (short i = 0; i < headers.length; i++) {
			HSSFCell cell = row.createCell(i);
			cell.setCellStyle(style);
			HSSFRichTextString text = new HSSFRichTextString(headers[i]);
			cell.setCellValue(text);
			System.out.println(headers[i]);
		}
		
		int index = 0;//第一行是标题栏
		// 遍历集合数据，产生数据行
		for( Object[] obj : dataset )
		{
			
			index++;
			row = sheet.createRow(index);
			
			for( int i = 0 ; i < obj.length ; i++ )
			{
				//System.out.println("------");
				//System.out.println(obj[i]);
				
				HSSFCell cell = row.createCell(i);
				cell.setCellStyle(style2);
				Object value = obj[i];
				String textValue = null;
				
				if (value instanceof byte[]) {
					// 有图片时，设置行高为60px;
					row.setHeightInPoints(80);
					// 设置图片所在列宽度为80px,注意这里单位的一个换算
					sheet.setColumnWidth(i, (short) (35.7 * 200));
					// sheet.autoSizeColumn(i);
					byte[] bsValue = (byte[]) value;
					//System.out.println(bsValue.length);
					
					HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0,
							1000, 240, (short) (headers.length-1), index, (short)(headers.length-1), index);
					anchor.setAnchorType(2);
					patriarch.createPicture(anchor, workbook.addPicture(
							bsValue, HSSFWorkbook.PICTURE_TYPE_JPEG));
				} 
				else {
					textValue = obj[i].toString();
				}
				
				// 如果不是图片数据，就利用正则表达式判断textValue是否全部由数字组成
				if (textValue != null) {
					Pattern p = Pattern.compile("^//d+(//.//d+)?$");
					Matcher matcher = p.matcher(textValue);
					if (matcher.matches()) {
						// 是数字当作double处理
						cell.setCellValue(Double.parseDouble(textValue));
					} else {
						HSSFRichTextString richString = new HSSFRichTextString(textValue);
						HSSFFont font3 = workbook.createFont();
						
						//System.out.println(".......");
						font3.setColor(HSSFColor.BLUE.index);
						richString.applyFont(font3);
						cell.setCellValue(richString);
					}
				}// ----textValue != null
				
					
			}//遍历Object里面的每一个元素
			
			
		}//遍历Lis<Object>

		
		try {
			workbook.write(out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
			
		
	}

	public int test( String docsPath , String fileName , List<Map<String , Object>> list , String type) {
		
		System.out.println(list);
		
		try {
			//String docsPath = System.getProperty("java.io.tmpdir");
			//System.out.println(type);
				if( "Users".equals(type))
				{
					// 用户
					String title = "生产管理系统用户信息表";
					ExportExcel<Users> ex = new ExportExcel<Users>();
					String[] headers = { "姓名", "手机号", "邮箱" , "权限" , "工作状态"  };
					List<Object[]> dataset = setEntity(list, type);
					
					System.out.println(docsPath + FILE_SEPARATOR + fileName);
					//写入文件
	
					OutputStream out = new FileOutputStream(docsPath + FILE_SEPARATOR +FILE_SEPARATOR+ fileName);
					ex.exportExcel( title ,headers, dataset, out);
					out.close();
				}
				else if( "Products".equals(type))
				{
					// 产品
					String title = "生产管理系统产品信息表";
					ExportExcel<Products> ex = new ExportExcel<Products>();
					String[] headers = { "设备ID", "产品类型", "MAC地址" };
					List<Object[]> dataset = setEntity(list, type);
					
					//写入文件
	
					OutputStream out = new FileOutputStream(docsPath + FILE_SEPARATOR +FILE_SEPARATOR+fileName);
					ex.exportExcel( title ,headers, dataset, out);
					out.close();
				}
				else if( "MACs".equals(type))
				{
					//MAC
					String title = "生产管理系统MAC信息表";
					ExportExcel<Products> ex = new ExportExcel<Products>();
					String[] headers = { "deviceID", "MAC", "一维条形码" };
					List<Object[]> dataset = setEntity(list, type);
					
					//写入文件
	
					OutputStream out = new FileOutputStream(docsPath + FILE_SEPARATOR +FILE_SEPARATOR+fileName);
					ex.exportExcel(title ,headers, dataset, out);
					out.close();
					
				}
				else if( "AnalysisBatch".equals(type))
				{
					// 批次统计
					String title = "生产管理系统批次统计表";
					ExportExcel<Products> ex = new ExportExcel<Products>();
					String[] headers = { "产品批次", "生产总数", "合格率" , "测试率" , "单产/天"  };
					List<Object[]> dataset = setEntity(list, type);
					
					//写入文件
	
					OutputStream out = new FileOutputStream(docsPath + FILE_SEPARATOR +FILE_SEPARATOR+fileName);
					ex.exportExcel(title, headers, dataset, out);
					out.close();
				}
				else if( "AnalysisProducer".equals(type))
				{
					// 生产商
					String title = "生产管理系统生产商统计表";
					ExportExcel<Products> ex = new ExportExcel<Products>();
					String[] headers = { "生产商", "生产总数", "合格率" , "测试率" , "单产/天"  };
					List<Object[]> dataset = setEntity(list, type);
					
					//写入文件
	
					OutputStream out = new FileOutputStream(docsPath + FILE_SEPARATOR +FILE_SEPARATOR+fileName);
					ex.exportExcel(title , headers, dataset, out);
					out.close();
				}
				else if( "AnalysisType".equals(type))
				{
					// 生产类型统计
					String title = "生产管理系统产品类型统计表";
					ExportExcel<Products> ex = new ExportExcel<Products>();
					String[] headers = { "产品类型", "生产总数", "合格率" , "测试率" , "单产/天"  };
					List<Object[]> dataset = setEntity(list, type);
					
					//写入文件
	
					OutputStream out = new FileOutputStream(docsPath + FILE_SEPARATOR +FILE_SEPARATOR+fileName);
					ex.exportExcel(title , headers, dataset, out);
					out.close();
				}
				
				
		} catch (IOException e) {
				log.info("从数据库导出数据时发生异常：" , e );
				return -1;
		}
		
		//JOptionPane.showMessageDialog(null, "导出成功!");
		System.out.println("excel导出成功！");
		return 1;
		
	}
	
	//将list中的数据存进excel模板类中
	public List<Object[]> setEntity( List<Map<String , Object>> list , String type )
	{
		String totalQuantity = "";
		String passRate = "";
		String testRate = "";
		String perTime = "";
		String deviceID = "";
		
		String productTypeName = "";
		
		List<Object[]> dataset = new ArrayList<Object[]>();
		for( Map<String , Object> map : list )
		{
			switch (type) {
			case "Users":
				String name = map.get("name").toString();
				String phone = map.get("phone").toString();
				String email = map.get("email").toString();
				String authority = "普通用户";
				String workState = "正常";
				int a = Integer.parseInt(map.get("authority").toString());
				int b = Integer.parseInt(map.get("workState").toString());
				if( a == 1 )
				{
					authority = "管理员";
				}
				if( b == 0 )
				{
					workState = "离职";
				}
				
				Object[] object1 = {name,phone,email,authority,workState};
				dataset.add(object1);
				
				break;
			case "Products":
				deviceID = map.get("deviceID").toString();
				productTypeName = map.get("productTypeName").toString();
				Object MAC = map.get("MAC");
				
				Object[] object2 = {deviceID,productTypeName,MAC};
				dataset.add(object2);
				
				break;
			case "MACs":
				deviceID = map.get("deviceID").toString();
				String Mac = map.get("MAC").toString();
				//读取图片
				byte[] buf = null;
				try {
					
					// 生成一维条形码  
					String imgPath = "E:/PMS_exportExcle/barCodeOne.png";  
			        
			        int width = 145, height = 30;  
			        BarCodeOne handler = new BarCodeOne();  
			        handler.encode(Mac, width, height, imgPath);  
			        
					BufferedInputStream bis = new BufferedInputStream(
							new FileInputStream("E:\\PMS_exportExcle" + FILE_SEPARATOR
									+ "barCodeOne.png"));
				
					/*BufferedInputStream bis = new BufferedInputStream(
							new FileInputStream("E:\\" + FILE_SEPARATOR
									+ "book.png"));*/
					
					buf = new byte[bis.available()];
					while ((bis.read(buf)) != -1) {
						System.out.println("条形码长度"+buf.length);
					}
					
				} catch (Exception e) {
					
				}
				
				
				
				Object[] object3 = { deviceID , Mac , buf };
				dataset.add(object3);
				
				break;
			case "AnalysisBatch":
				String productBatch = map.get("productBatch").toString();
				totalQuantity = map.get("totalQuantity").toString();
				passRate = map.get("passRate").toString();
				testRate = map.get("testRate").toString();
				perTime = map.get("perTime").toString();
				
				Object[] object4 = {productBatch,totalQuantity,passRate,testRate,perTime};
				dataset.add(object4);
				
				break;
			case "AnalysisProducer":
				String producer = map.get("producer").toString();
				totalQuantity = map.get("totalQuantity").toString();
				passRate = map.get("passRate").toString();
				testRate = map.get("testRate").toString();
				perTime = map.get("perTime").toString();
				
				Object[] object5 = {producer,totalQuantity,passRate,testRate,perTime};
				dataset.add(object5);
				
				break;
			case "AnalysisType":
				productTypeName = map.get("productTypeName").toString();
				totalQuantity = map.get("totalQuantity").toString();
				passRate = map.get("passRate").toString();
				testRate = map.get("testRate").toString();
				perTime = map.get("perTime").toString();
				
				Object[] object6 = {productTypeName,totalQuantity,passRate,testRate,perTime};
				dataset.add(object6);
				
				break;
				

			default:
				break;
			}
			
		
		}
		
		return dataset;
		
	}
	
	
	
	public int download(String path, HttpServletResponse response) {
		try {
			// path是指欲下载的文件的路径。
			File file = new File(path);
			// 取得文件名。
			String filename = file.getName();
			// 以流的形式下载文件。
			InputStream fis = new BufferedInputStream(new FileInputStream(path));
			byte[] buffer = new byte[fis.available()];
			fis.read(buffer);
			fis.close();
			// 清空response
			response.reset();
			// 设置response的Header
			response.addHeader("Content-Disposition", "attachment;filename="+ new String(filename.getBytes()));
			response.addHeader("Content-Length", "" + file.length());
			OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
			response.setContentType("application/vnd.ms-excel;charset=gb2312");
			toClient.write(buffer);
			toClient.flush();
			toClient.close();
		} catch (IOException ex) {
			log.info("将导出的文件发送给客户端时发生异常：" , ex );
			return -1;
		}
		
		return 1;
	}
}
