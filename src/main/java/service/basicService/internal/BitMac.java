package service.basicService.internal;

public class BitMac {
	
	private int[] mBits;//用int类型来操作位
	private int mSize;//指定有多少位
	
	
	public int[] getmBits() {
		return mBits;
	}
	public void setmBits(int[] mBits) {
		this.mBits = mBits;
	}
	
	
	public BitMac()
	{
		
	}
	public BitMac( int size )
	{
		mSize = size;
		initBits();
	}
	
	//初始化整型数组
	private void initBits()
	{
		//int为4个字节，32位，所以数组长度为 为长度除以32
		int count = (int)Math.ceil( mSize/32f );// Math.ceil( double a )返回的是大于或等于 a 的最小整数
		mBits = new int[count];
		clear();
		
	}

	//将数组全部置零
	private void clear()
	{
		for( int i=0 ; i < mBits.length ; i++ )
		{
			mBits[i] = 0;
		}
	}
	
	//将指定的位置为1，即MAC被使用
	public void set1( int pos )
	{
		//计算出pos在数组中的索引
		int index = (int)Math.floor( pos/32f );//返回小于或等于pos/32的最大整数
		System.out.println("返回小于或等于pos/32的最大整数"+ index);
		//把该整数的第 n = 1 + 31-pos%32 位置为1,这个操作采用该整数与移位后的1进行或运算，使指定位置为1
		mBits[index] = mBits[index] | ( 1 << ( 31 - pos%32 ) );// 1左移n位，右边补足n个0，然后1和任何数相或都是1
	}
	
	//将指定的位置0，即MAC可用
	public void set0( int pos )
	{
		//计算出pos在数组中的索引
		int index = (int)Math.floor( pos/32f );//返回小于或等于pos/32的最大整数
		//进行与运算，移位0和任何数相与都为0
		mBits[index] = mBits[index] & ~( 1 << ( 31 - pos%32 ) );//将1左移后，取反:与0相与为0，与1相与不变
	}
	//判断该MAC是否可用： 可用则返回false( 0 == (0|1) )，不可用则返回true( 1 == ( 1|1) )
	public boolean checkUsable( int pos )
	{
		int index = (int)Math.floor( pos/32f );
		return mBits[index] == (mBits[index] | 1 << ( 31 - pos%32 ) );
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
