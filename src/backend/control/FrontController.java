package backend.control;

import java.util.ArrayList;
import java.util.HashMap;

import backend.bean.BasketBean;
import backend.bean.BasketDataBean;
import backend.bean.GoodsBean;
import backend.bean.MemberBean;
import backend.services.*;

public class FrontController {
	private Authentication auth;	
	private Goods goods;
	private GoodsManagements management;
	private Purchase purchase;

	public FrontController() {
		auth = new Authentication();
		goods = new Goods();
		management = new GoodsManagements();
		purchase = new Purchase();
	}

	/** 아이디 중복 체크 
	 * @name		public duplicateId
	 * @param 		userInfo 아이디 정보
	 * @return  	isCheck 
	 * @serviceCode 1
	 * @references  Authentication
	 */
	public String[] duplicateId(String[] userInfo) {
		String[] idCheck = new String[1];  // respons로 활용할 배열
		MemberBean member = new MemberBean();

		// 클라이언트 요청 처리를 위한 데이터 이동 : DTO : array --> bean
		member.setMemberId(userInfo[0]);
		auth.backController(1, member);

		// 클라이언트에 응답하기 위해 데이터 이동 : DTO : bean --> array
		idCheck[0] = member.isDuplicateCheck()? "1": "0";

		return idCheck;
	}

	/** 회원가입
	 * @name		public joinMember
	 * @param		String[] userInfo
	 * @return		String[] memberInfo
	 * @serviceCode 2
	 * @references 	Authentication
	 */
	public String[] joinMember(String[] userInfo) {
		String[] memberInfo = new String[5];
		MemberBean member = new MemberBean();

		// 클라이언트 요청 처리를 위한 데이터 이동 : DTO : array --> bean
		member.setMemberId(userInfo[0]);
		member.setMemberName(userInfo[1]);
		member.setMemberPassword(userInfo[2]);
		member.setMemberAge(Integer.parseInt(userInfo[3]));
		member.setMemberType(userInfo[4]);

		auth.backController(2, member);

		// 클라이언트에 응답하기 위해 데이터 이동 : DTO : bean --> array
		memberInfo[0] = member.getMemberId();
		memberInfo[1] = member.getMemberName();
		memberInfo[2] = member.getMemberPassword();
		memberInfo[3] = member.getMemberAge() + "";
		memberInfo[4] = member.getMemberType();

		return memberInfo;
	}	


	/** 멤버 정보들 검색해서 불러오기
	 * @name public searchMembersInfo
	 * @param memberType
	 * @return String[][]
	 * @serviceCode 3
	 * @references 	Authentication
	 */
	public String[][] searchMembersInfo(String[] memberType) {
		MemberBean member;
		ArrayList<MemberBean> list;

		//String[] --> MemberBean
		member = new MemberBean();
		member.setMemberType(memberType[0]);

		//멤버 정보들 검색해서 가져오기
		list = this.auth.backController(3, member);

		//ArrayList<MemberBean> --> String[][]
		String[][] memberList = new String[list.size()][5];
		for (int index = 0; index < list.size(); index++) {
			memberList[index][0] = list.get(index).getMemberId();
			memberList[index][1] = list.get(index).getMemberName();
			memberList[index][2] = list.get(index).getMemberPassword();
			memberList[index][3] = list.get(index).getMemberAge() + "";
			memberList[index][4] = list.get(index).getMemberType().equals("P") ? "개인" : "판매자";
		}


		return memberList;
	}


	/** 서버에 로그인 요청
	 * @name public memberAccess
	 * @param accessInfo 아이디, 패스워드
	 * @return String[] 로그인 회원 정보
	 * @serviceCode "A"
	 * @references 	Authentication
	 */
	public String[] memberAccess(String[] accessInfo) {
		String[] memberInfo = null;
		MemberBean member = new MemberBean();

		//String[]-->bean
		member.setMemberId(accessInfo[0]);
		member.setMemberPassword(accessInfo[1]);

		//serviceCall
		member = this.auth.backController("A", member);

		//bean-->String[]
		if (member != null) {
			memberInfo = new String[5];
			memberInfo[0] = member.getMemberId();
			memberInfo[1] = member.getMemberName();
			memberInfo[2] = (member.getMemberType().equals("P")) ? "Personal" : "Company";
			memberInfo[3] = member.getAccessTime();
			memberInfo[4] = member.getBasektCount() + "";
		}
		return memberInfo;
	}

	/** 서버에 로그아웃 요청
	 * @name public accessOut
	 * @param memberInfo 로그아웃 할 회원의 정보
	 * @return String[] null
	 * @serviceCode -1
	 * @references 	Authentication
	 */
	public String[] accessOut(String[] memberInfo) {
		MemberBean member = null;

		if (memberInfo != null) { // 회원 정보를 받았다면
			member = new MemberBean();
			member.setMemberId(memberInfo[0]);
			this.auth.backController(-1, member);
		}

		return null;
	}


	/** 판매 상품 검색
	 * @name public searchGoods
	 * @param map :: key:"word" - 검색어
	 * @return ArrayList<GoodsBean> 검색 결과
	 * @serviceCode 1
	 * @references 	Goods
	 */
	public ArrayList<GoodsBean> searchGoods(HashMap<String, String> map){
		GoodsBean goodsInfo = new GoodsBean();

		//1. words --> GoodsBean
		goodsInfo.setGoodsDetails(map.get("Word"));

		//2. Goods Call : 1, GoodsBean
		//3. ArrayList<GoodsBean> 리턴받기		
		//4. ArrayList<GoodsBean> FrontEnd로 전달
		return this.goods.backController(1, goodsInfo);
	}


	//장바구니에 담기
	public void setBasket(HashMap<String, String> basket) {
		//HashMap --> BasketBean
		BasketBean basketInfo = new BasketBean();
		basketInfo.setUserId(basket.get("userId"));
		basketInfo.setSaler(basket.get("saler"));
		basketInfo.setgCode(basket.get("gCode"));
		basketInfo.setgQty(Integer.parseInt(basket.get("gQty")));

		//Goods.backController --> serviceCode 2, BasketBean
		this.goods.backController(2, basketInfo);
	}

	public ArrayList<BasketDataBean> getBasketData(HashMap<String,String> map) {
		BasketBean basket = new BasketBean();
		// 클라이언트에서 전송된 데이터를 서버에 전송가능한 형태인 Beans로 변환
		basket.setUserId(map.get("userId"));
		// Goods Class 호출 --> serviceCode 3    BasketBean
		// Basket Data를 클라이언트로 리턴
		return this.goods.backController(3, basket);
	}


	public void purchaseRequest(HashMap<String,String> sendPur) {
		ArrayList<BasketDataBean> purList = new ArrayList<BasketDataBean>();
		//HashMap --> Bean
		for (String key : sendPur.keySet()) {
			BasketDataBean pur = new BasketDataBean();
			String[] record = sendPur.get(key).split(",");

			pur.setUserId(record[0]);
			pur.setSaler(record[1]);
			pur.setgCode(record[2]);
			pur.setgQty(Integer.parseInt(record[3]));
			purList.add(pur);
		}

		//Purchase 요청 serviceCode 1
		this.purchase.backController(1, purList);
		//구매후 재고 갱신
		this.goods.backController(5, purList);

	}


	public ArrayList<BasketDataBean> getPurchaseInfo(HashMap<String, String> map) {
		ArrayList<BasketDataBean> send = new ArrayList<BasketDataBean>();

		//HashMap --> ArrayList<BasketDataBean>
		BasketDataBean userInfo = new BasketDataBean();
		userInfo.setUserId(map.get("userId"));
		send.add(userInfo);

		//구매 정보 가져오기 ArrayList<BasketDataBean>
		send = this.purchase.backController(2, send);
		//상품 상세정보 가져오기
		this.goods.backController(4, send);

		return send;
	}

	public ArrayList<GoodsBean> searchSalerGoods(HashMap<String,String> map){
		GoodsBean goods = new GoodsBean();

		//HashMap --> Bean
		goods.setGoodsSaler(map.get("userId"));

		//GoodsManageMent :: serviceCode 1, saler
		return this.management.backController(1, goods);
	}

	public void renewSaleGoodsInfo(HashMap<String,String> map) {
		GoodsBean goods = new GoodsBean();

		//HashMap --> Bean
		goods.setGoodsSaler(map.get("userId"));
		goods.setGoodsCode(map.get("code"));
		goods.setGoodsPrice(Integer.parseInt(map.get("price")));
		goods.setGoodsStocks(Integer.parseInt(map.get("stock")));

		//GoodsManageMent :: serviceCode 2, goods
		this.management.backController(2, goods);

	}


	public void deleteSaleGoodsInfo(HashMap<String,String> map) {
		GoodsBean goods = new GoodsBean();

		//HashMap --> Bean
		goods.setGoodsSaler(map.get("userId"));
		goods.setGoodsCode(map.get("code"));
		goods.setGoodsPrice(Integer.parseInt(map.get("price")));
		goods.setGoodsStocks(Integer.parseInt(map.get("stock")));

		//GoodsManageMent :: serviceCode 3, goods
		this.management.backController(3, goods);
	}
	
	public ArrayList<GoodsBean> readAllGoodsTxt(){
		GoodsBean bean = null;
		
		return this.goods.backController(6, bean);
	}
	
	public void appendSaleGoodsInfo(HashMap<String,String> map) {
		GoodsBean goods = new GoodsBean();

		//HashMap --> Bean
		goods.setGoodsSaler(map.get("userId"));
		goods.setGoodsCode(map.get("code"));
		goods.setGoodsPrice(Integer.parseInt(map.get("price")));
		goods.setGoodsStocks(Integer.parseInt(map.get("stock")));
		
		// 상품 추가 요청 
		this.management.backController(4, goods);
	}
}










