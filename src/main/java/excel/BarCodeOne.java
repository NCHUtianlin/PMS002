package excel;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

import javax.imageio.ImageIO;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
 
public class BarCodeOne {
    /**
     * 条形码编码
     * 
     * @param contents
     * @param width
     * @param height
     * @param imgPath
     */
    public void encode(String contents, int width, int height, String imgPath) {
        
    	
    	/*if( checkHex(contents))//判断是否为十六进制数
    	{
    		try {
				long data = hexToDouble(contents);//转为十进制
				contents = data+"";
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}*/
    	System.out.println("要生产条形码的数据："+contents);
    		
    	int codeWidth = 3 + // start guard
                (7 * 6) + // left bars
                5 + // middle guard
                (7 * 6) + // right bars
                3; // end guard
        codeWidth = Math.max(codeWidth, width);
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(contents,
                    BarcodeFormat.CODE_128, codeWidth, height, null);
 
            MatrixToImageWriter.writeToStream(bitMatrix, "png" , new FileOutputStream(imgPath));
 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
    /**
     * 解析条形码
     * 
     * @param imgPath
     * @return
     */
    public String decode(String imgPath) {
        BufferedImage image = null;
        Result result = null;
        try {
            image = ImageIO.read(new File(imgPath));
            if (image == null) {
                System.out.println("the decode image may be not exit.");
            }
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
 
            result = new MultiFormatReader().decode(bitmap, null);
            return result.getText();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
     
    //判断字符串是否为十六进制
    public boolean checkHex(String string)
    {
    	int i = 0;
    	if( string.length() > 2 )
    	{
    		if( string.charAt(0) == '0' && ( string.charAt(1) == 'X' || string.charAt(1) == 'x' ) )
    			i = 2;
    	}
    	for( ; i < string.length() ; i++ )
    	{
    		char ch = string.charAt(i);
    		if( ( ch >= '0' && ch <= '9') || ( ch >= 'a' && ch <= 'f') || ( ch >= 'A' && ch <= 'F'))
    		{
    			continue;
    		}
    		return false;
    	}
    	return true;
    	
    }
    //计算幂，这里计算十六进制()redix=16，则是根据十六进制数中的位置计算16的多少次方，位置是从0 开始计
    public static long getPower( int redix , int position) throws Exception
    {
    	long sum = 1;
    	if( position < 0 )
    	{
    		throw new Exception("位置是从0开始，不能小于0 ");
    	}
    	else if( position == 0 ){
			sum = 1;
		}
    	else {
			for( int i = 0 ; i < position ; i ++ )
				sum = sum * redix;
		}
    	
    	return sum;
    }
    //当十六进制数超过int范围内时无法进行转换，所以这里转换成double
    public static long hexToDouble( String hex ) throws Exception
    {
    	long result = 0 ;
    	if( hex != null && hex.length() > 0 )
        {
        	for( int i = 0 ; i < hex.length() ; i ++ )
        	{
        		int chValue = 0;
        		char ch = hex.charAt(i);
        		if( ch >= 'a' && ch <= 'f')
        		{
        			chValue = (int)(ch - 'a' + 10);
        		}
        		else if ( ch >= 'A' && ch <= 'F'){
        			chValue = (int)(ch - 'A' + 10);
				}
        		System.out.println("chVaue="+chValue);
        		long mi = getPower(16, hex.length()-i);//幂
        		System.out.println(i+" : "+ch+" >>> "+mi);
        		result = result + chValue * mi;
        	}
        	
        }
    	
    	
    	return result;
    }
    
    public static void main(String[] args) {  
        /*String imgPath = "E:/zxing_EAN13.png";  
        // 益达无糖口香糖的条形码  
        String contents = "6923450657713";  
   
        int width = 105, height = 50;  
        BarCodeOne handler = new BarCodeOne();  
        handler.encode(contents, width, height, imgPath);  */
   
        String mac = "1rwccccCCCBBBB";
        /*long macNum = 0;
		try {
			macNum = hexToDouble(mac);
		} catch (Exception e) {
			
			e.printStackTrace();
		}*/
        
        
       // System.out.println(macNum);
        String contents = "6923450657713"; 
		// 生成一维条形码  
		String imgPath = "E:/PMS_exportExcle/"+mac+"Bar3.png";  
        
        int width = 105, height = 50;  
        BarCodeOne handler = new BarCodeOne();  
        handler.encode(mac+"", width, height, imgPath); 
        System.out.println("Michael ,you have finished zxing EAN13 encode.");  
        
        //解析条形码
        String codeString = handler.decode(imgPath);
        System.out.println("解码："+codeString);
        
    }  
}