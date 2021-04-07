package backend.dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class DataAccessObject {
	protected String filePath;
	protected String[] fileList = 
		{"Member.txt", "Goods.txt", "SaleGoods.txt", 
				"Recent.txt","ShoppingBasket.txt", 
				"Purchase.txt", "PurchaseDetail.txt", "History.txt"};
	protected File file;
	protected FileReader fReader;
	protected FileWriter fWriter;
	protected BufferedReader bReader;
	protected BufferedWriter bWriter;

	protected DataAccessObject(int fileIndex, String filePath) {
		this.filePath = filePath + "\\" + fileList[fileIndex];
	}

}
