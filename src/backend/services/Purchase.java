package backend.services;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import backend.bean.BasketBean;
import backend.bean.BasketDataBean;

public class Purchase {
	DataAccessObject dao;
	String path;
	public Purchase() {
		this.path = "C:\\Hogun\\ICIA\\hogeun_test\\workspace\\phone\\src\\data";
	}

	
	public ArrayList<BasketDataBean> backController(int serviceCode, ArrayList<BasketDataBean> purList) {
		ArrayList<BasketDataBean> send = null;
		if (serviceCode == 1) {
			this.purchaseControl(purList);
		}else if (serviceCode == 2) {
			send = this.getPurchaseInfo(purList);
		}
		
		return send;
	}
	
	
	//구매 정보 가져오기
	private ArrayList<BasketDataBean> getPurchaseInfo(ArrayList<BasketDataBean> userInfo) {
		//DAO 6
		this.dao = new DataAccessObject(6,this.path);

		return this.dao.getPurchaseInfo(userInfo);
	}

	/** 상품 구매 요청
	 * @name rivate purchaseControl
	 * @param purList 구매할 상품 목록
	 */
	private void purchaseControl(ArrayList<BasketDataBean> purList) {
		//현재시간 기록
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
		String time = sdf.format(date);
		
		//영수증 기록
		this.purchase(purList, time);
		//영수증 상세 기록
		this.purchaseDetail(purList, time);
		//장바구니 지우기
		this.deleteBasketControl(purList);
	}

	private void purchase(ArrayList<BasketDataBean> purList, String time) {
		//DAO 5
		this.dao = new DataAccessObject(5,this.path);
		this.dao.purchase(purList, time);
	}

	private void purchaseDetail(ArrayList<BasketDataBean> purList, String time) {
		//DAO 6
		this.dao = new DataAccessObject(6,this.path);
		this.dao.purchaseDetail(purList, time);
	}
	
	
	private void deleteBasketControl(ArrayList<BasketDataBean> purList) {
		//DAO 4
		this.dao = new DataAccessObject(4,this.path);
		//장바구니 정보 가져오기
		ArrayList<BasketBean> bList = this.dao.getBasketInfos();
		//장바구니 정보 갱신
		for (BasketDataBean data : purList) {
			for (int i = 0; i < bList.size(); i++) {
				if (data.getUserId().equals(bList.get(i).getUserId())) {
					if (data.getSaler().equals(bList.get(i).getSaler())) {
						if (data.getgCode().equals(bList.get(i).getgCode())) {
							if (data.getgQty() == (bList.get(i).getgQty())) {
								bList.remove(i);
								break;
							}
						}
					}
				}
			}
		}
		//장바구니 정보 다시 쓰기
		this.dao.renewBasketInfos(bList);
	}
	
	
}
