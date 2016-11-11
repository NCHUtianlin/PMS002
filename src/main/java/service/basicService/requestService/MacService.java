package service.basicService.requestService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import dao.MacDao;
import service.interfaceService.BasicImplementService;

@Service
public class MacService extends BasicImplementService {

	private static Log log = LogFactory.getLog(MacService.class.getName());

	private static MacDao dao;
	public void setDao(MacDao dao) {
		this.dao = dao;
	}

	
	/****
	 * 更新MAC地址：有产品被废弃后，管理员手动将该产品的MAC地址回收
	 * @param request
	 * @return
	 */
	public int updateMAC( HttpServletRequest request )
	{
		log.info(request.getRemoteAddr()+" 开始进入updateMAC()方法");
		
		
		log.info(request.getRemoteAddr()+" updateMAC()方法结束");
		return 0;
	}
	
	/****
	 * 获取MAC地址信息：
	 *     1. 获取一段可用MAC地址
	 *     2. 获取某一个批次的mac地址并可以打印出来
	 *     3. 查询可用mac地址数量
	 *     4. 查询某一个mac是否可用
	 * @param request
	 * @param response
	 * @return
	 */
	public int getMAC( HttpServletRequest request , HttpServletResponse response )
	{
		log.info(request.getRemoteAddr()+" 开始进入getMAC()方法");
		
		
		log.info(request.getRemoteAddr()+" getMAC()方法结束");
		return 0;
	}
	
}
