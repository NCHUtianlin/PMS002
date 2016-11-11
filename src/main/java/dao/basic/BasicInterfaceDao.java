package dao.basic;

import java.util.List;
import java.util.Map;

import domain.User;

public interface BasicInterfaceDao {
	
	//检索账户信息：登陆、获取权限---->> 账户名是手机号码
	public User getAccount(String userName  , String hashedPassword );
			
	//通过id获取信息
	public Object getById( Class clazz , String id );
	
	//单字段查询( 表名，字段名，值 )
	public Object getByOne( Class clazz , String key , Object value );

	//获取信息，当map里面只有一个对象时，即单条件查询；否则为多条件查询
	public List getInfo(String tableName , Map<String, Object> map);
	//分页查询
	public List getInfoByPage(String tableName , Map<String, Object> map , int pageNow , int pageSize);
	//按时间段查询
	//public List getInfoByTimes(String tableName , Map<String, Object> map , Map<String, Object> mapTimes , int pageNow , int pageSize );
	//按条件查询自己的信息， 普通用户
	//public List getInfoMyself( String tableName , Map<String, Object> map , int pageNow , int pageSize );
	
	//插入用户信息，返回值大于 0  则表示插入成功
	public int insert(String tableName , Map<String , Object> map);
		
	//通过id删除信息
	//public int deleteById(String tableName , String id);
	
	//通过id修改信息
	public int updateById(Class clazz, String id, Map<String, Object> map );
	
	
	//专职模糊查询，与QueryDao对接， 只有在查询用户信息和申报信息的时候，才需要鉴别普通用户与管理员
	public List query( String tableName , Object parameter , int userAuthority , String userid , int pageNow , int pageSize );

}
