package backend.services;

import java.util.ArrayList;

import backend.bean.GoodsBean;

public class GoodsManagements {

	DataAccessObject dao;
	String path;

	public GoodsManagements() {
		this.path = "C:\\Hogun\\ICIA\\hogeun_test\\workspace\\phone\\src\\data";
	}

	public ArrayList<GoodsBean> backController(int serviceCode, GoodsBean goods) {
		ArrayList<GoodsBean> gList = null;

		if (serviceCode == 1) {
			//판매자 아이디로 상품 정보 검색
			gList = this.searchSalerGoodsControl(goods);
		}else if (serviceCode == 2) {
			this.renewSaleGoodsInfoControl(goods);
		}else if (serviceCode == 3) {
			this.deleteSaleGoodsInfoControl(goods);
		}else if (serviceCode == 4) {
			this.appendSaleGoodsInfo(goods);
		}

		return gList;
	}

	private ArrayList<GoodsBean> searchSalerGoodsControl(GoodsBean goods){
		ArrayList<GoodsBean> gList = new ArrayList<GoodsBean>();

		// 판매 목록 검색
		this.searchSaleGoodsInfos(goods, gList);
		// 판매 상품 상세 검색
		this.getGoodsName(gList);

		return gList;
	}


	private void searchSaleGoodsInfos(GoodsBean goods, ArrayList<GoodsBean> gList) {
		// DAO 2
		this.dao = new DataAccessObject(2,this.path);
		this.dao.searchSaleGoodsInfos(goods, gList);
	}

	private void getGoodsName(ArrayList<GoodsBean> gList) {
		// DAO 1
		this.dao = new DataAccessObject(1,this.path);
		this.dao.getGoodsName(gList);
	}


	private void renewSaleGoodsInfoControl(GoodsBean goods) {
		ArrayList<GoodsBean> sgList = this.readAllSaleGoodsTxt();

		for (GoodsBean data : sgList) {
			if (data.getGoodsSaler().equals(goods.getGoodsSaler())) {
				if (data.getGoodsCode().equals(goods.getGoodsCode())) {
					data.setGoodsPrice(goods.getGoodsPrice());
					data.setGoodsStocks(goods.getGoodsStocks());
					break;
				}
			}
		}

		this.renewSaleGoodsInfo(sgList);
	}

	//SaleGoods.txt의 모든 정보 불러오기
	private ArrayList<GoodsBean> readAllSaleGoodsTxt(){
		//DAO 2
		this.dao = new DataAccessObject(2,this.path);
		return this.dao.readAllSaleGoods();
	}

	private void renewSaleGoodsInfo(ArrayList<GoodsBean> sgList) {
		//DAO 2
		this.dao = new DataAccessObject(2,this.path);
		this.dao.renewSaleGoodsTxt(sgList);
	}

	private void deleteSaleGoodsInfoControl(GoodsBean goods) {
		ArrayList<GoodsBean> sgList = this.readAllSaleGoodsTxt();

		for (int i = 0; i < sgList.size(); i++) {
			if (sgList.get(i).getGoodsSaler().equals(goods.getGoodsSaler())) {
				if (sgList.get(i).getGoodsCode().equals(goods.getGoodsCode())) {
					sgList.remove(i);
					break;
				}
			}
		}

		this.renewSaleGoodsInfo(sgList);
	}


	private void appendSaleGoodsInfo(GoodsBean goods) {
		//DAO 2
		this.dao = new DataAccessObject(2,this.path);
		this.dao.appendSaleGoodsInfo(goods);
	}
}
