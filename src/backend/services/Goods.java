package backend.services;

import java.util.ArrayList;

import backend.bean.BasketBean;
import backend.bean.BasketDataBean;
import backend.bean.GoodsBean;

public class Goods {
	private DataAccessObject dao;
	private String path;

	public Goods() {
		this.path = "C:\\Hogun\\ICIA\\hogeun_test\\workspace\\phone\\src\\data";
	}

	public ArrayList<GoodsBean> backController(int serviceCode, GoodsBean word) {
		ArrayList<GoodsBean> gList = null;

		// Job을 수행할 메서드 분기 :: IF ~~
		if (serviceCode == 1) {
			gList = this.searchGoodsControl(word);
		}else if (serviceCode == 6) {
			gList = this.readAllGoodsTxt();
		}

		return gList;
	}

	public ArrayList<BasketDataBean> backController(int serviceCode, BasketBean basket) {
		ArrayList<BasketDataBean> basketList = null;
		if (serviceCode == 2) {
			this.setBasket(basket);
		}else if(serviceCode == 3){
			basketList = this.getBasketDataControl(basket);
		}

		return basketList;
	}
	
	
	public void backController(int serviceCode, ArrayList<BasketDataBean> basketList) {
		if (serviceCode == 4) {
			this.getGoodsData(basketList);
			this.getSalesGoodsData(basketList);
		}else if (serviceCode == 5) {
			this.saleGoodsStockControl(basketList);
		}
	
	}

	private ArrayList<GoodsBean> searchGoodsControl(GoodsBean word) {
		ArrayList<GoodsBean> gList = new ArrayList<GoodsBean>();
		ArrayList<String> gCode;
	
		// word --> Goods.txt Search --> goodsCode
		gCode = this.searchGoodsCode(word);
	
		// goodsCode --> SaleGoods.txt --> GoodsBean --> gList
		this.getSaleInfo(gCode, true, gList);
	
		// goodsCode--> Goods.txt --> 이름,종류,설명-->GoodsBean
		this.getSaleInfo(gCode, false, gList);
	
		return gList;
	}

	private ArrayList<String> searchGoodsCode(GoodsBean word) {
		this.dao = new DataAccessObject(1, this.path);
		return this.dao.getGoodsCode(word);
	}

	private void getSaleInfo(ArrayList<String> gCode, boolean searchType, ArrayList<GoodsBean> gList) {
		//true 2 false 1
		int fileIndex = searchType ? 2 : 1;
		this.dao = new DataAccessObject(fileIndex, this.path);		
		if (searchType) {
			this.dao.getSalesInfo(gCode, gList);
		}else {
			this.dao.getGoodsInfo(gList);
		}
	}

	private ArrayList<BasketDataBean> getBasketDataControl(BasketBean basket) {
		// 장바구니 데이터를 담을 ArrayList 생성 --> 배포
		ArrayList<BasketDataBean> basketList = new ArrayList<BasketDataBean>();
		// ShoppingBasket 장바구니 목록
		this.getShoppingBasketData(basket, basketList);
		// Goods 상품이름
		this.getGoodsData(basketList);
		// SalesGoods 상품가격
		this.getSalesGoodsData(basketList);
		
		return basketList;
	}


	private void getShoppingBasketData(BasketBean basket, ArrayList<BasketDataBean> basketList) {
		//장바구니 정보
		//DAO 4
		this.dao = new DataAccessObject(4,this.path);
		this.dao.getShoppingData(basket, basketList);
	}

	private void getGoodsData(ArrayList<BasketDataBean> basketList) {
		//GoodsData 상품명
		//DAO 1
		this.dao = new DataAccessObject(1,this.path);
		this.dao.getGoodsData(basketList);
	}

	private void getSalesGoodsData(ArrayList<BasketDataBean> basketList) {
		//SalesData 가격, amount = price * qty
		//DAO 2
		this.dao = new DataAccessObject(2,this.path);
		this.dao.getSalesData(basketList);
		//amount 계산
		for (BasketDataBean data : basketList) {
			data.setAmount(data.getgPrice() * data.getgQty());
		}
	}
	
	
	void setBasket(BasketBean basket) {
		//DAO 4 ShoppingBasket
		this.dao = new DataAccessObject(4,this.path);

		//DAO.writeBasket BasketBean
		this.dao.writeBasket(basket);
	}
	
	
	void saleGoodsStockControl(ArrayList<BasketDataBean> basketList) {

		//SaleGoods 정보 불러오기
		ArrayList<GoodsBean> saleGoodsInfo = this.getSaleGoodsInfo();
		
		//재고 데이터 갱신
		for (BasketDataBean data : basketList) {
			for (GoodsBean info : saleGoodsInfo) {
				if (data.getSaler().equals(info.getGoodsSaler())) {
					if (data.getgCode().equals(info.getGoodsCode())) {
						int stock = info.getGoodsStocks() - data.getgQty();
						info.setGoodsStocks(stock);
						
					}
				}
			}
		}
		//재고 데이터 쓰기
		this.renewSaleGoodsStock(saleGoodsInfo);
		
	}
	
	ArrayList<GoodsBean> getSaleGoodsInfo() {
		//DAO 2
		this.dao = new DataAccessObject(2,this.path);
		return this.dao.getSaleGoodsInfos();
	}
	
	void renewSaleGoodsStock(ArrayList<GoodsBean> saleGoodsInfo) {
		//DAO 2
		this.dao = new DataAccessObject(2,this.path);
		this.dao.renewSaleGoodsStock(saleGoodsInfo);
	}


	ArrayList<GoodsBean> readAllGoodsTxt(){
		//DAO 1
		this.dao = new DataAccessObject(1,this.path);
		
		return this.dao.readAllGoodsTxt();
	}
}
