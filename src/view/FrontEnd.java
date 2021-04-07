package view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import backend.bean.BasketDataBean;
import backend.bean.GoodsBean;
import backend.control.FrontController;

public class FrontEnd {
	private FrontController fc;
	private Scanner scanner;

	public FrontEnd() {
		fc = new FrontController();
		scanner = new Scanner(System.in);

		this.viewControl(this.makeTitle());

		scanner.close(); // 스캐너 닫기
	}


	/** 프로그램 제어
	 * @name private viewControl
	 * @param title 타이틀 문자열
	 */
	private void viewControl(String title) {
		// 메뉴코드 0으로 초기화
		int menuCode = 0;

		// 메뉴 2차원 배열 선언 및 할당
		String[][] menu = {	
				{"MAIN", "LogIn", "Join","Search", "Close"},
				{"LOGIN"},
				{"JOIN", "Personal", "Company", "Back"}};

		// 로그인 정보	::	null	비로그인 상태
		//			::	else	{아이디, 이름, 타입, 로그인 시간, 장바구니 갯수}
		String[] accessInfo = null;		

		// 무한 반복
		while(true) {
			this.display(title);
			// 아이디(이름:타입) 접속시간 장바구니갯수
			this.display((accessInfo == null) ? "" : "    [ " + accessInfo[0] + "(" + accessInfo[1] + ":" + accessInfo[2]+")"+
					" AccessTime : " + accessInfo[3] +" " + "Basket : " + accessInfo[4] + " ]\n");
			this.display(this.makeMenu(true, menu, menuCode));			
			menuCode = Integer.parseInt(this.userInput());

			if(menuCode == 0) {	// 0을 입력시 종료
				break;
			}else if(menuCode == 1) { // 로그인/로그아웃
				if (accessInfo == null) { // 비로그인 상태인 경우
					accessInfo = this.login(title);
					if(accessInfo != null) { // 로그인에 성공했다면
						if (accessInfo[2].equals("Company")){//사업자라면
							String[] menu2 = {"MAIN", "LogOut", "Join","Search", "Basket","MyPage","GoodsManage","Close"};
							menu[0] = menu2;
						}else { // 개인회원이라면
							String[] menu2 = {"MAIN", "LogOut", "Join","Search", "Basket","MyPage","Close"};
							menu[0] = menu2;
						}
					}
				}else { // 로그인 상태인 경우
					accessInfo = this.logout(accessInfo);
					String menu2[] = {"MAIN", "LogIn", "Join","Search", "Close"};
					menu[0] = menu2;
				}
			}else if(menuCode == 2) {	
				this.join(title, menu, menuCode);
			}else if (menuCode == 3) {
				this.searchGoods(title, accessInfo);
			}else if (menuCode == 4) {
				if (accessInfo != null) { // 로그인 중 이라면
					this.myBasket(title, accessInfo);
				}
			}else if(menuCode == 5) {
				if (accessInfo != null) { // 로그인 중 이라면
					this.myPage(title, accessInfo);
				}
			}else if(menuCode == 6) {
				if (accessInfo != null) {
					if (accessInfo[2].equals("Company")) {
						this.goodsManage(title, accessInfo);
					}
				}
			}
			// 메뉴코드 초기화
			menuCode = 0;
		}
	}


	/** 로그인 :: null이면 로그인 실패
	 * @param title 타이틀 문자열
	 * @return String[]
	 */
	private String[] login(String title) {
		String[] accessInfo = new String[2];

		// 무한 반복
		while(true) {
			this.display(title);
			this.display(this.makeInputItem(true, "ACCESS", "USER ID"));
			accessInfo[0] = this.userInput(); // 아이디 입력
			this.display(this.makeInputItem(false, null, "USER PASSWORD"));
			accessInfo[1] = this.userInput(); // 패스워드 입력
			this.display(this.makeResult(true, " CONFIRM(y/n)"));
			if (this.userInput().equals("y")) { // y면 빠져나감
				break;
			}else { // 아닐경우 입력값 초기화
				accessInfo[0] = null;
				accessInfo[1] = null;
			}
		}
		// 서버에 로그인 요청 및 성공여부 확인
		accessInfo = this.fc.memberAccess(accessInfo);

		return accessInfo;
	}

	/** 로그아웃
	 * @name private logout
	 * @param accessInfo 로그인 정보
	 * @return String[] null
	 */
	private String[] logout(String[] accessInfo) {
		return this.fc.accessOut(accessInfo);
	}

	/** 회원 가입
	 * @name private join
	 * @param title 타이틀 문자열
	 * @param menu 메뉴
	 * @param menuCode 메뉴 코드
	 */
	private void join(String title, String[][] menu, int menuCode) {
		// 회원가입 정보를 입력받을 배열 {아이디,이름,패스워드,나이,타입}
		String[] userInfo = new String[5]; 
		boolean idCheck = false;
		boolean menuCheck = true;
		int code;
		String[] joinInfo;

		this.display(title);
		while(!idCheck) {
			this.display(this.makeInputItem(menuCheck, "JOIN", "USER ID"));
			userInfo[0] = this.userInput(); // 아이디 입력
			menuCheck = false;
			// 서버 전송 :: id중복여부 체크
			idCheck = (fc.duplicateId(userInfo)[0].equals("0"))? false : true;
		}

		this.display(this.makeInputItem(menuCheck, null, "USER NAME"));
		userInfo[1] = this.userInput();
		this.display(this.makeInputItem(menuCheck, null, "USER PASSWORD"));
		userInfo[2] = this.userInput();
		this.display(this.makeInputItem(menuCheck, null, "USER AGE"));
		userInfo[3] = this.userInput();

		this.display(makeMenu(menuCheck, menu, menuCode));
		code = Integer.parseInt(this.userInput());
		if(code >= 1 && code <= menu[menuCode].length-2) {
			userInfo[4] = (code==1? "P": "C");
		}

		// 회원가입 서버전송
		// 서버로부터 가입된 회원정보를 리턴받아 화면 출력
		joinInfo = fc.joinMember(userInfo);

		// 사용자로 부터 Confirm 후 메인화면 복귀
		menuCheck = true;
		this.display(title);
		this.display(this.makeInputItem(menuCheck, "JOIN Successful", "USER ID"));
		this.display(joinInfo[0] + "\n");

		menuCheck = false;

		this.display(this.makeInputItem(menuCheck, null, "USER NAME"));
		this.display(joinInfo[1] + "\n");
		this.display(this.makeInputItem(menuCheck, null, "USER PASSWORD"));
		this.display(joinInfo[2] + "\n");
		this.display(this.makeInputItem(menuCheck, null, "USER AGE"));
		this.display(joinInfo[3] + "\n");
		this.display(this.makeInputItem(menuCheck, null, "USER TYPE"));
		this.display((joinInfo[4].equals("P")?"개인":"판매자") + "\n");

		this.display(this.makeResult(true, " PRESS ANY KEY"));
		this.userInput();
	}

	//상품 검색
	//Map Collection : frontedn<-->frontcontroller
	/** 상품 검색 및 장바구니에 담기
	 * @name private searchGoods
	 * @param title 타이틀 문자열
	 * @param accessInfo 로그인 정보
	 */
	private void searchGoods(String title, String[] accessInfo) {
		HashMap<String, String> map = new HashMap<String, String>();
		ArrayList<GoodsBean> gList;
		String menuCode;

		while(true) {
			this.display(title);
			this.display(this.makeInputItem(true, "GoodsSearch", "Word"));
			menuCode = this.userInput();
			if(menuCode.equals("0")) {// 이전화면
				break;
			}
			map.put("Word", menuCode);
			gList = fc.searchGoods(map);

			// 검색결과화면 출력
			this.display(this.makeResult(false, "-------------\n"));
			this.display("\t번호\t상품코드\t상품명\t판매금액\t판매자\t재고\n");//탭으로 공백
			this.display(this.makeResult(false, "-------------\n"));

			// 검색결과로 리턴 받은 gList 출력 :: ArrayList<GoodsBean>
			int index = 0;
			for(GoodsBean goods : gList) {
				index++;
				this.display("\t" + index + ".\t");
				this.display(goods.getGoodsCode()+"\t");
				this.display(goods.getGoodsName()+"\t");
				this.display(goods.getGoodsPrice()+"\t");
				this.display(goods.getGoodsSaler()+"\t");
				this.display(goods.getGoodsStocks()+"\n");			
			}
			this.display(this.makeResult(true, " Select"));
			menuCode = this.userInput();
			if (!menuCode.equals("0")) {
				// 구매자(userId),판매자(saler),상품코드(gCode),수량(gQty) --> HashMap<String, String>
				HashMap<String, String> basket = new HashMap<String, String>();
				// 선택한 상품정보 저장 - 코드, 판매자
				basket.put("gCode",gList.get(Integer.parseInt(menuCode)-1).getGoodsCode());
				basket.put("saler",gList.get(Integer.parseInt(menuCode)-1).getGoodsSaler());
				// 수량 입력
				this.display(this.makeInputItem(false, null, "구매수량"));
				basket.put("gQty",this.userInput());
				// 장바구니(로그인여부?)
				if (accessInfo == null) {
					this.display(this.makeInputItem(false, null, "USER ID"));
				}
				basket.put("userId", ((accessInfo == null) ? this.userInput() : accessInfo[0]));

				// 장바구니 담기 요청
				// FrontController
				// - setBasket(HashMap<String, String> basket) 
				this.fc.setBasket(basket);
			}
		}
	}


	/** 장바구니 확인 및 선택 후 구매
	 * @name private myBasket
	 * @param title 타이틀 문자열
	 * @param accessInfo 로그인 정보
	 */
	private void myBasket(String title, String[] accessInfo) {
		HashMap<String,String> map = new HashMap<String,String>();
		ArrayList<BasketDataBean> gList = null;
		map.put("userId", accessInfo[0]);

		// 타이틀 출력
		this.display(title);
		// 서브타이틀 및 항목 제목 출력
		this.display("     [ My Basket ]\n");
		this.display(this.makeResult(false, "------------\n"));
		this.display("\t번호\t상품코드\t상품명\t판매금액\t수량\t금액\t판매자\n");
		this.display(this.makeResult(false, "------------\n"));
		// ShoppingBasket.txt에 가서 해당 계정의 장바구니 정보 가져오기
		gList = this.fc.getBasketData(map);
		if(gList.size() != 0) { // 장바구니의 상품이 하나 이상일 때
			// 리턴되어진 장바구니 정보 출력
			this.displayBasketDataInfo(gList,true,false);

			this.display(this.makeResult(true, " 구매할까요?(y/n)"));
			if (this.userInput().equals("y")) {
				HashMap<String,String> sendPur = null;
				//구매번호를 기록할 boolean[] 배열 false로 초기화
				boolean[] pCheck = new boolean[gList.size()];
				for(int i = 0; i < pCheck.length;i++) {
					pCheck[i] = false;
				}

				//구매 번호 선택 : 0을 입력하기 전까지 선택
				while(true) {
					//구매번호 입력 타이틀 출력
					this.display(this.makeResult(true, " Purchase"));
					//구매번호 입력
					int check = Integer.parseInt(this.userInput());
					//구매번호가 0이면 break
					if (check == 0) {break;}
					//구매번호 기록 : true면 false로 false면 true로
					pCheck[check-1] = (!pCheck[check-1]);

					//선택한 번호 출력
					this.display(this.makeResult(true, " select"));
					int checkIndex= 0;
					for(boolean b : pCheck) {
						checkIndex++;
						if(b) {
							this.display(checkIndex + " ");
						}
					}
					this.display("\n");

				}
				//선택한 상품 전송할 HashMap 생성
				for (int i = 0; i < pCheck.length; i++) {
					//0이 아니라면(선택한 번호라면 했다면)
					if (pCheck[i]) {
						if (sendPur == null) {
							sendPur = new HashMap<String,String>();
						}
						//구매자,판매자,코드,수량
						String record = gList.get(i).getUserId() + "," +
								gList.get(i).getSaler() + "," +
								gList.get(i).getgCode() + "," +
								gList.get(i).getgQty();
						sendPur.put(i + "", record);
					}
				}

				//선택한 상품이 있다면
				if (sendPur != null) {
					//구매 요청
					//FrontController
					// - purchaseRequest(ArrayList<HashMap<String,String>> sendPur)
					fc.purchaseRequest(sendPur);
				}
			}

		}else { // 장바구니에 상품이 없는 경우
			this.display("       [장바구니에 담긴 상품이 없습니다]\n");
			this.userInput();
		}
	}


	/** 마이페이지 :: 장바구니 및 구매목록 출력
	 * @name private myPage
	 * @param title 타이틀 문자열
	 * @param accessInfo 로그인 정보
	 */
	private void myPage(String title, String[] accessInfo) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("userId", accessInfo[0]);
		//장바구니 데이터 가져오기
		ArrayList<BasketDataBean> gList = this.fc.getBasketData(map);
		//구매 목록 가져오기
		ArrayList<BasketDataBean> pList = this.fc.getPurchaseInfo(map);


		//타이틀 출력
		this.display(title);

		//장바구니 출력
		this.display("     [ My Basket ]\n");
		this.display(this.makeResult(false, "------------\n"));
		this.display("\t상품코드\t상품명\t판매금액\t수량\t금액\t판매자\n");
		this.display(this.makeResult(false, "------------\n"));
		if (gList.size() != 0) {
			this.displayBasketDataInfo(gList,false,false);
		}else {
			this.display("       [장바구니에 상품이 없습니다]\n");
		}

		this.display(this.makeResult(false, "------------\n"));

		//구매 목록 출력
		this.display("     [ My Purchase ]\n");
		this.display(this.makeResult(false, "----------------------\n"));
		this.display("\t구매시간\t\t상품코드\t상품명\t판매금액\t수량\t금액\t판매자\n");
		this.display(this.makeResult(false, "----------------------\n"));
		if (pList.size() != 0) {
			this.displayBasketDataInfo(pList, false,true);
		}else {
			this.display("       [구매한 상품이 없습니다.]\n");
		}
		this.userInput();
	}


	/** 판매자 상품 관리
	 * 
	 * @param gList
	 * @param idx
	 * @param time
	 */
	private void goodsManage(String title, String[] accessInfo) {
		int selectMenu = 0;
		int selectGoods = 0;
		String input = null;
		HashMap<String,String> map = new HashMap<String,String>();
		ArrayList<GoodsBean> gList = null;
		map.put("userId", accessInfo[0]);

		while (true) {
			// 판매자 아이디로 판매 상품 검색
			//fc -> searchSalerGoods(map)
			gList = this.fc.searchSalerGoods(map);

			//타이틀 출력
			this.display(title);

			// 판매 상품 목록 출력
			this.displayGoodsInfo(gList,true);
			this.display("\t1.상품 수정\t\t2.상품 추가\n\t3.상품 삭제\t\t0.메인으로\n");
			this.display(this.makeResult(true, " select"));
			selectMenu = Integer.parseInt(this.userInput());

			// 0을 선택하면 빠져나온다.
			if (selectMenu == 0) {break;}

			// 상품 수정
			else if (selectMenu == 1) {
				// 항목 선택 0이거나 범위 밖 이면 뒤로
				this.display(this.makeResult(true, " index"));
				selectGoods = Integer.parseInt(this.userInput());
				if (selectGoods > 0 && selectGoods <= gList.size()){
					map.put("code", gList.get(selectGoods - 1).getGoodsCode());
					map.put("price", "" + gList.get(selectGoods - 1).getGoodsPrice());
					map.put("stock","" + gList.get(selectGoods - 1).getGoodsPrice());

					// 수정가격 입력 0이면 변경 x
					this.display(this.makeResult(true, " price"));
					input = this.userInput();
					if (!input.equals("0")) {
						map.put("price", input);
					}

					// 수정재고 입력 0이면 변경 x
					this.display(this.makeResult(true, " stock"));
					input = this.userInput();
					if (!input.equals("0")) {
						map.put("stock", input);
					}
					// 상품 수정 요청
					// fc - renewSaleGoodsInfo
					this.fc.renewSaleGoodsInfo(map);
				}
			}//end 1

			// 상품 추가
			else if (selectMenu == 2) {
				ArrayList<GoodsBean> goodsList = this.fc.readAllGoodsTxt();//상품 불러오기
				for (GoodsBean data : gList) {//추가 가능한 상품 선택
					for (int i = 0; i < goodsList.size();i++) {
						if (goodsList.get(i).getGoodsCode().equals(data.getGoodsCode())) {
							goodsList.remove(i);
							break;
						}
					}
				}
				this.displayGoodsInfo(goodsList, false);
				// 항목 선택 0이거나 범위 밖 이면 뒤로
				this.display(this.makeResult(true, " index"));
				selectGoods = Integer.parseInt(this.userInput());
				if (selectGoods > 0 && selectGoods <= goodsList.size()){
					map.put("code",  goodsList.get(selectGoods - 1).getGoodsCode());
					map.put("price", "0");
					map.put("stock", "0");

					// 수정가격 입력 0이면 변경 x
					this.display(this.makeResult(true, " price"));
					input = this.userInput();
					if (!input.equals("0")) {
						map.put("price", input);
					}

					// 수정재고 입력 0이면 변경 x
					this.display(this.makeResult(true, " stock"));
					input = this.userInput();
					if (!input.equals("0")) {
						map.put("stock", input);
					}

					// 상품 추가 요청
					this.fc.appendSaleGoodsInfo(map);
				}
			}//end 2

			//상품 삭제
			else if (selectMenu == 3) {
				this.display(this.makeResult(true, " index"));
				selectGoods = Integer.parseInt(this.userInput());
				if (selectGoods > 0 && selectGoods <= gList.size()){
					map.put("code", gList.get(selectGoods - 1).getGoodsCode());
					map.put("price", "" + gList.get(selectGoods - 1).getGoodsPrice());
					map.put("stock","" + gList.get(selectGoods - 1).getGoodsPrice());
					// 상품 삭제 요청
					this.fc.deleteSaleGoodsInfo(map);
				}
			} // end 3
		}
	}

	private void displayGoodsInfo(ArrayList<GoodsBean> gList,boolean type) {
		int index = 0;
		this.display(this.makeResult(false, "----------------------\n"));
		this.display("\t번호\t코드\t이름" + (type?"\t가격\t재고":"") + "\t카테고리\n");
		this.display(this.makeResult(false, "----------------------\n"));

		for (GoodsBean data : gList) {
			index++;
			this.display("\t" + index +".");//인덱스
			this.display("\t" + data.getGoodsCode());//코드 
			this.display("\t" + data.getGoodsName());//이름 
			if (type) {
				this.display("\t" + data.getGoodsPrice());//가격 
				this.display("\t" + data.getGoodsStocks());//재고 
			}
			this.display("\t" + data.getGoodsCategory() + "\n");//카테고리
			//상세
		}
		this.display(this.makeResult(false, "----------------------\n"));
	}

	private void displayBasketDataInfo(ArrayList<BasketDataBean> gList, boolean idx, boolean time) {
		int indexNumber = 0;
		int totalAmount = 0;
		for (BasketDataBean data : gList) {
			indexNumber++;
			this.display(idx?"\t" + indexNumber + ".\t":"\t");//번호
			this.display(time ? data.getDate() + "\t" : "");//구매시간
			this.display(data.getgCode() + "\t");//상품코드
			this.display(data.getgName() + "\t");//상품명
			this.display(data.getgPrice() + "\t");//판매금액
			this.display(data.getgQty() + "\t");//수량
			this.display(data.getAmount() + "\t");//금액
			this.display(data.getSaler() + "\n");//판매자

			totalAmount += data.getAmount();
		}
		this.display(this.makeResult(false, "------------\n"));
		this.display("\t\t\t\t\ttotal : " + totalAmount + "\n");
	}


	private String userInput() {
		return this.scanner.next();
	}

	private void display(String basket) {
		System.out.print(basket);
	}

	private String makeTitle() {
		StringBuffer sb = new StringBuffer();

		sb.append("\n\n\n\n");
		sb.append("ᕦ(ò_óˇ)ᕤ ___________________________________________________________________\n");
		sb.append("   +                                                         +\n");
		sb.append("   +             Second Project :: Shopping Mall             +\n");
		sb.append("   +                                                         +\n");
		sb.append("   +                              Designed by HoonZzang      +\n");
		sb.append("   +                                                         +\n");
		sb.append("   + _______________________________________________________ +\n");
		sb.append("\n");

		return sb.toString();
	}

	private String makeMenu(boolean type, String[][] menu, int menuCode) {
		StringBuffer sb = new StringBuffer();

		sb.append(type?"     " + "[ " + menu[menuCode][0] + " ]\n":"");

		if(menu[menuCode].length > 1) {
			sb.append("     -------------------------------------------------------\n");
			for(int index=1; index < menu[menuCode].length-1; index++) {
				sb.append("       " + index + ". " + menu[menuCode][index] + ((index%2==0)? "\n":"       "));
			}
			sb.append("       0. " + menu[menuCode][menu[menuCode].length-1] + "\n");
			sb.append("     ------------------------------------------- Select : ");
		}		 

		return sb.toString();
	}

	private String makeInputItem(boolean type, String step, String item) {
		StringBuffer sb = new StringBuffer();

		sb.append(type? "     [ " + step + " ]\n": "");
		sb.append(type? "     -------------------------------------------------------\n":"");
		sb.append("      [ " + item + " ] : ");

		return sb.toString();
	}

	private String makeResult(boolean type, String result) {
		StringBuffer sb = new StringBuffer();

		sb.append("     -------------------------------------------"+ result);
		sb.append(type? " : " : "");

		return sb.toString();
	}

}








