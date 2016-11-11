package mongoDBtest;

import java.awt.BasicStroke;  
import java.awt.Color;  
import java.awt.Graphics;  
import java.awt.Graphics2D;  
import java.awt.Image;  
import java.awt.image.BufferedImage;  
import java.io.ByteArrayOutputStream;  
import java.io.File;  
import java.io.FileOutputStream;  
import java.io.IOException;  
import java.util.Hashtable;  
  
import javax.imageio.ImageIO;  
  
import junit.framework.Assert;  
  
import org.apache.commons.codec.binary.Base64;  
import org.apache.commons.lang.StringUtils;  
  
import com.google.zxing.BarcodeFormat;  
import com.google.zxing.EncodeHintType;  
import com.google.zxing.MultiFormatWriter;  
import com.google.zxing.common.BitMatrix;  
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;  
  
  
public class BarCode {  
  
    private static final boolean NEEDCOMPRESS = false;  
  
    private static String LOGO_URL;  
    // LOGO宽度  
    private static final int WIDTH = 60;  
    // LOGO高度  
    private static final int HEIGHT = 60;  
  
    static{  
        LOGO_URL = "";// 测试图片路径地址  
    }  
  
    /** 
     * 生成二维码的工具类 
     *  
     * @param data 
     *            二维码中携带的数据 
     * @param width 
     *            二维码的宽度 
     * @param height 
     *            二维码的高度 
     * @return 返回base64格式的图片（data:image/gif;base64,xxxx） <br> 
     *         eg： <code> 
     * <img alt="" src="data:image/gif;base64,xxxx"> 
     * </code> 
     */  
    public static String createQRCode(String data, int width, int height, String isPicture) {  
  
        Assert.assertTrue("param data cannot empty.", data != null && data.trim().length() > 0);  
  
        Assert.assertTrue("param width and height must gt 0.", width > 0 && height > 0);  
  
        ByteArrayOutputStream bos = null;  
  
        MultiFormatWriter formatWriter = new MultiFormatWriter();  
  
        Hashtable<EncodeHintType, Object> param = new Hashtable<EncodeHintType, Object>();  
        param.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q);  
        param.put(EncodeHintType.CHARACTER_SET, "utf-8");  
        param.put(EncodeHintType.MARGIN, 0);  
  
        try{  
            BitMatrix bitMatrix = formatWriter.encode(data, BarcodeFormat.QR_CODE, width, height, param);  
            // 1.1去白边  
            int[] rec = bitMatrix.getEnclosingRectangle();  
            int resWidth = rec[2] + 1;  
            int resHeight = rec[3] + 1;  
            BitMatrix resMatrix = new BitMatrix(resWidth, resHeight);  
            resMatrix.clear();  
            for (int i = 0; i < resWidth; i++){  
                for (int j = 0; j < resHeight; j++){  
                    if (bitMatrix.get(i + rec[0], j + rec[1])){  
                        resMatrix.set(i, j);  
                    }  
                }  
            }  
            int width1 = resMatrix.getWidth();  
            int height1 = resMatrix.getHeight();  
            BufferedImage qrcode = new BufferedImage(width1, height1, BufferedImage.TYPE_INT_RGB);  
            for (int x = 0; x < width1; x++){  
                for (int y = 0; y < height1; y++){  
                    qrcode.setRGB(x, y, resMatrix.get(x, y) == true ? Color.BLACK.getRGB() : Color.WHITE  
                            .getRGB());  
                }  
            }  
            // 添加logo图片  
            if (StringUtils.isNotEmpty(LOGO_URL) && StringUtils.isNotEmpty(isPicture)){  
                insertImage(width1, qrcode, LOGO_URL, NEEDCOMPRESS);  
            }  
            bos = new ByteArrayOutputStream();  
            ImageIO.write(qrcode, "png", bos);  
  
            String img = StringUtils.deleteWhitespace(Base64.encodeBase64String(bos.toByteArray()));  
            return String.format("data:image/png;base64,%s", img);  
        } catch (Exception e){  
            throw new RuntimeException(e);  
        } finally{  
            if (bos != null){  
                try{  
                    bos.close();  
                } catch (IOException e){  
                }  
            }  
        }  
  
    }  
    //生成一维码
    /*public Bitmap CreateOneDCode(String content) throws WriterException {  
        // 生成一维条码,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败  
        BitMatrix matrix = new MultiFormatWriter().encode(content,  
                BarcodeFormat.CODE_128, 500, 200);  
        int width = matrix.getWidth();  
        int height = matrix.getHeight();  
        int[] pixels = new int[width * height];  
        for (int y = 0; y < height; y++) {  
            for (int x = 0; x < width; x++) {  
                if (matrix.get(x, y)) {  
                    pixels[y * width + x] = 0xff000000;  
                }  
            }  
        }  
  
        Bitmap bitmap = Bitmap.createBitmap(width, height,  
                Bitmap.Config.ARGB_8888);  
        // 通过像素数组生成bitmap,具体参考api  
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);  
        return bitmap;  
    } */ 
    
    private static void insertImage(int QRCODE_SIZE, BufferedImage source, String imgPath,  
            boolean needCompress) throws Exception {  
        File file = new File(imgPath);  
        if (!file.exists()){  
            System.err.println("" + imgPath + "   该文件不存在！");  
            return;  
        }  
        Image src = ImageIO.read(new File(imgPath));  
        int width = src.getWidth(null);  
        int height = src.getHeight(null);  
        if (needCompress){ // 压缩LOGO  
            if (width > WIDTH){  
                width = WIDTH;  
            }  
            if (height > HEIGHT){  
                height = HEIGHT;  
            }  
            Image image = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);  
            BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);  
            Graphics g = tag.getGraphics();  
            g.drawImage(image, 0, 0, null); // 绘制缩小后的图  
            g.dispose();  
            src = image;  
        }  
        // 插入LOGO  
        Graphics2D graph = source.createGraphics();  
        int x = (QRCODE_SIZE - width) / 2;  
        int y = (QRCODE_SIZE - height) / 2;  
        graph.drawImage(src, x, y, width, height, null);  
        // Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);  
        graph.setStroke(new BasicStroke(3f));  
        // graph.draw(shape); 去除图片白边  
        graph.dispose();  
    }  
  
    /** 
     * @param data 
     *            二维码携带的数据 
     * @param size 
     *            二维码的宽、高 
     * @return 
     * @see WechatValidateUtils#generateQRCode(String, int, int) 
     */  
    public static String createQRCode(String data, int size, String isPicture) {  
        return createQRCode(data, size, size, isPicture);  
    }  
  
    // 测试  
    public static void main(String[] args) throws Exception {  
        // 生成二维码  
        String url = "http://www.sina.com.cn";  
        String src = createQRCode(url, 200, "1");  
        System.out.println(url.length());  
        String html = "<img src=\"" + src + "\">";  
        FileOutputStream ops = new FileOutputStream("E:\\PMS_exportExcle\\barCode.html");  
        org.apache.commons.io.IOUtils.write(html, ops, "utf-8");  
        ops.close();  
    }  
}  