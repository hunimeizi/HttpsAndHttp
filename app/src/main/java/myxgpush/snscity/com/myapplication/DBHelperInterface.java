package myxgpush.snscity.com.myapplication;

import android.provider.BaseColumns;

public class DBHelperInterface {

	public interface UserRegist extends BaseColumns {
		public static final String UserRegist_tbl = "UserRegist";
		public static final String Id = "Id";
		public static final String Status = "Status";
		public static final String Message = "Message";
		public static final String LoginUid = "LoginUid";
		public static final String PlateNumber = "PlateNumber";
		public static final String LoginPwd = "LoginPwd";
		public static final String FullName = "FullName";
		public static final String Mobile = "Mobile";
		public static final String Sex = "Sex";
	}

	/**
	 * 首页置顶
	 * 
	 * @author 马自强
	 * 
	 *         2014年5月7日 10:35:10
	 * 
	 */
	public interface PromotionsInfoHomeTopImp extends BaseColumns {

		public static final String PromotionsInfoHomeTop_tbl = "PromotionsInfoHomeTop";
		public static final String Id = "Id";
		public static final String PromotionsInfoId = "PromotionsInfoId";
		public static final String IsDel = "IsDel";
		public static final String DataOrder = "DataOrder";
		public static final String CreateTime = "CreateTime";
		public static final String LastUpdateTime = "LastUpdateTime";
		public static final String DealerId = "DealerId";
		public static final String DealerGroupId = "DealerGroupId";
	}

	/**
	 * 促销信息
	 * 
	 * @author 马自强
	 * 
	 *         2014年5月7日 10:35:14
	 * 
	 */
	public interface PromotionsInfo extends BaseColumns {
		public static final String PromotionsInfo_tbl = "PromotionsInfo";
		public static final String PromotionsInfoBeanId = "Id";// 主键ID
		public static final String PromotionsInfoCategory = "PromotionsInfoCategory";// 促销大类
																						// PromotionsInfoCategory：1团购，2活动，3优惠券,4特权，5新车，6其它
		public static final String PromotionsInfoType = "PromotionsInfoType";// 促销类型
																				// PromotionsInfoType：1团购，2活动，3优惠券
		public static final String PromotionsName = "PromotionsName";// 促销名称
		public static final String SmallPicUrl = "SmallPicUrl";// 图片
		public static final String Detail = "Detail";// 信息内容
		public static final String Address = "Address";// 地址
		public static final String OfficeTel = "OfficeTel";// 电话
		public static final String LimitNumber = "LimitNumber";// 数量限制
		public static final String PaymentMoney = "PaymentMoney";// 支付金额
		public static final String ApplyMoney = "ApplyMoney";// 使用金额
		public static final String BeginDate = "BeginDate";// 开始日期
		public static final String EndDate = "EndDate";// 结束日期
		public static final String AppVisibleDate = "AppVisibleDate";// APP可见时间
		public static final String IsOnlyOnce = "IsOnlyOnce";// 一次性购买
		public static final String IsListTop = "IsListTop";// 是否被列表置顶
															// IsListTop：1列表置顶，0取消列表置顶
		public static final String IsPublish = "IsPublish";// 发布状态
															// IsPublish：1发布，0未发布，2取消发布
		public static final String DealerId = "DealerId";// 经销商ID
		public static final String DealerUserId = "DealerUserId";// 经销商用户ID
		public static final String IsDel = "IsDel";// 删除
		public static final String DataOrder = "DataOrder";// 排序
		public static final String CreateTime = "CreateTime";// 创建时间
		public static final String LastUpdateTime = "LastUpdateTime";// 更新时间
		public static final String DealerGroupId = "DealerGroupId";// 经销商集团ID
	}

	/**
	 * 经销商客户
	 * 
	 * @author 马自强
	 * 
	 *         2014年5月7日 11:24:13
	 */

	public interface DealerCustomer extends BaseColumns {
		public static final String DealerCustomer_tbl = "DealerCustomer";
		public static final String DealerCustomerId = "Id";// 主键ID
		public static final String LoginUid = "LoginUid";// 登录名
		public static final String LoginPwd = "LoginPwd";// 登录密码
		public static final String FullName = "FullName";// 姓名
		public static final String Mobile = "Mobile";// 手机
		public static final String Sex = "Sex";// 性别 Sex：0男，1女
		public static final String Birthday = "Birthday";// 生日
		public static final String Age = "Age";// 年龄
		public static final String Email = "Email";// 邮箱
		public static final String Nickname = "Nickname";// 昵称
		public static final String HeadImage = "HeadImage";// 头像
		public static final String LoginCount = "LoginCount";// 登录次数
		public static final String GoldCount = "GoldCount";// 金币总数
		public static final String PlateNumber = "PlateNumber";// 车牌号1
		public static final String PlateNumberTwo = "PlateNumberTwo";// 车牌号2
		public static final String PlateNumberThree = "PlateNumberThree";// 车牌号3
		public static final String DealerUserId = "DealerUserId";// 经销商用户ID
		public static final String DealerId = "DealerId";// 经销商ID
		public static final String IsDel = "IsDel";// 删除
		public static final String DataOrder = "DataOrder";// 排序
		public static final String CreateTime = "CreateTime";// 创建时间
		public static final String LastUpdateTime = "LastUpdateTime";// 更新时间
		public static final String DealerGroupId = "DealerGroupId";// 经销商集团ID
		public static final String IsBlackList = "IsBlackList";// 黑名单
																// IsBlackList：1黑名单，0白名单
	}

	/**
	 * 表更新日志
	 * 
	 * @author 马自强
	 * 
	 *         2014年5月7日 17:27:55
	 * 
	 */
	public interface TableName extends BaseColumns {
		public static final String TableName_tbl = "TableUpdateLog";
		public static final String TableName = "TableName";// 表名
		public static final String LastUpdateTime = "LastUpdateTime";// 更新时间
	}

	public interface CreateTable extends BaseColumns {
		public static final String CreateTable_tbl = "CreateTable";
	}
}
