package backend.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;

import backend.bean.BasketBean;
import backend.bean.BasketDataBean;
import backend.bean.GoodsBean;
import backend.bean.MemberBean;

class DataAccessObject extends backend.dao.DataAccessObject {

	DataAccessObject(int fileIndex, String filePath) {
		super(fileIndex, filePath);
	}

	void fileRead() {
		File file = new File(this.filePath);
		try {
			fReader = new FileReader(file);
			bReader = new BufferedReader(fReader);
			String line;

			while((line = bReader.readLine()) != null) {

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { bReader.close();} catch (Exception e) {e.printStackTrace();}
		}
	}

	void fileWrite() {
		File file = new File(this.filePath);
		try {
			fWriter = new FileWriter(file, true);
			bWriter = new BufferedWriter(fWriter);
			String line;


		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { bWriter.close();} catch (Exception e) {e.printStackTrace();}
		}
	}

	//아이디를 입력받아 Member.txt 파일에 접근 같은 레코드가 있는지 파악
	//같은 아이디가 있다면 false 없으면 true
	/** 아이디 중복 체크
	 * @name duplicateCheck
	 * @param MemberBean member
	 * @return boolean :: false 아이디 중복    true 중복 없음
	 */
	boolean duplicateCheck(MemberBean member) {
		boolean check = true;
		File file = new File(this.filePath);
		try {
			fReader = new FileReader(file);
			bReader = new BufferedReader(fReader);

			String line;

			while((line = bReader.readLine()) != null) {
				String[] record = line.split(",");
				if (record[0].equals(member.getMemberId())) {
					check = false;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { bReader.close();} catch (Exception e) {e.printStackTrace();}
		}

		return check;
	}


	/** 파일에 회원 등록(Member.txt) 
	 * @name registrationMember
	 * @param MemberBean member
	 * @return boolean :: true 등록 성공    false 등록 실패
	 */
	boolean registrationMember(MemberBean member) {
		boolean isCheck = false;
		File file = new File(this.filePath);
		try {
			fWriter = new FileWriter(file, true);// true : 이어쓰기
			bWriter = new BufferedWriter(fWriter);
			String record = member.getMemberId() + "," +
					member.getMemberName() + "," +
					member.getMemberPassword() + "," +
					member.getMemberAge() + "," +
					member.getMemberType() + "\n";

			bWriter.write(record);
			//bWriter.newLine(); 줄바꿈 추가
			bWriter.flush(); //버퍼 비우기
			isCheck = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { bWriter.close();} catch (Exception e) {e.printStackTrace();}
		}

		return isCheck;
	}


	// 멤버 검색
	/** 아이디로 멤버 정보 검색, 반환
	 * @name searchMemberInfo
	 * @param MemberBean member
	 * @return MemberBean :: null 정보 없음
	 */
	MemberBean searchMemberInfo(MemberBean member) {
		MemberBean newMember = null;
		File file = new File(this.filePath);
		try {
			fReader = new FileReader(file);
			bReader = new BufferedReader(fReader);
			String line;

			while((line = bReader.readLine()) != null) {
				String[] record = line.split(",");
				if(record[0].equals(member.getMemberId())) {
					newMember = new MemberBean();
					newMember.setMemberId(record[0]);
					newMember.setMemberName(record[1]);
					newMember.setMemberPassword(record[2]);
					newMember.setMemberAge(Integer.parseInt(record[3]));
					newMember.setMemberType(record[4]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { bReader.close();} catch (Exception e) {e.printStackTrace();}
		}

		return newMember;
	}

	//멤버타입
	ArrayList<MemberBean> searchMembersInfo(MemberBean member) {
		ArrayList<MemberBean> list = new ArrayList<MemberBean>();
		File file = new File(this.filePath);
		try {
			fReader = new FileReader(file);
			bReader = new BufferedReader(fReader);
			String line;

			while((line = bReader.readLine()) != null) {
				String[] record = line.split(",");
				if(record[4].equals(member.getMemberType())) {
					MemberBean newMember = new MemberBean();
					newMember.setMemberId(record[0]);
					newMember.setMemberName(record[1]);
					newMember.setMemberPassword(record[2]);
					newMember.setMemberAge(Integer.parseInt(record[3]));
					newMember.setMemberType(record[4]);
					list.add(newMember);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { bReader.close();} catch (Exception e) {e.printStackTrace();}
		}

		return list;
	}

	boolean isAccess(MemberBean member) {
		File file = new File(this.filePath);
		String line;
		String[] record;
		boolean check = false;
		try {
			fReader = new FileReader(file);
			bReader = new BufferedReader(fReader);

			while((line = bReader.readLine()) != null) {
				//라인을 배열로
				record = line.split(",");
				//아이디 비교
				if (record[0].equals(member.getMemberId())) {
					//패스워드 비교
					if (record[2].equals(member.getMemberPassword())) {
						check = true;
					}
					break;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { bReader.close();} catch (Exception e) {e.printStackTrace();}
		}

		return check;
	}

	// History Table에 쓰기
	void writeHistory(MemberBean member) {
		File file = new File(this.filePath);
		try {
			fWriter = new FileWriter(file, true);
			bWriter = new BufferedWriter(fWriter);
			String record;

			record = member.getMemberId() + "," +
					member.getAccessTime() + "," + 
					member.getAccessType() + "\n";
			bWriter.write(record);
			bWriter.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { bWriter.close();} catch (Exception e) {e.printStackTrace();}
		}
	}

	// History Table 데이터 읽기
	ArrayList<MemberBean> readAccessInfo(MemberBean member){
		File file = new File(this.filePath);
		ArrayList<MemberBean> list = new ArrayList<MemberBean>();
		String[] record = null;

		try {
			fReader = new FileReader(file);
			bReader = new BufferedReader(fReader);

			String line;
			while((line = bReader.readLine()) != null) {
				record = line.split(",");
				if(record[0].equals(member.getMemberId())) {
					if(record[2].equals(member.getAccessType()+"")) {
						MemberBean accessData = new MemberBean();
						accessData.setAccessTime(record[1].substring(8));
						list.add(accessData);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { bReader.close();} catch (Exception e) {e.printStackTrace();}
		}

		return list;
	}


	// History Table 데이터 읽기 --> StringTokenizer
	ArrayList<MemberBean> readAccessInfo2(MemberBean member){
		File file = new File(this.filePath);
		ArrayList<MemberBean> list = new ArrayList<MemberBean>();
		String[] record = null;
		StringTokenizer tokens = null;

		try {
			fReader = new FileReader(file);
			bReader = new BufferedReader(fReader);

			String line;
			while((line = bReader.readLine()) != null) {
				tokens = new StringTokenizer(line, ",");
				record = new String[tokens.countTokens()];
				int index = -1;
				while(tokens.hasMoreTokens()) {
					index++;
					record[index] = tokens.nextToken();
				}
				if (record[0].equals(member.getMemberId())) {
					if(record[2].equals(member.getAccessType()+"")) {
						MemberBean accessInfo = new MemberBean();
						accessInfo.setAccessTime(record[1].substring(8));
						list.add(accessInfo);
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { bReader.close();} catch (Exception e) {e.printStackTrace();}
		}

		return list;
	}


	/** 로그인 로그아웃 상태 확인
	 * @name isLogin
	 * @param MemberBean member
	 * @return boolean :: true 로그인중    false 로그아웃
	 */
	boolean isLogin(MemberBean member) {
		int result = 0;//결과값 초기화
		File file = new File(this.filePath);
		try {
			fReader = new FileReader(file);
			bReader = new BufferedReader(fReader);
			String[] record = null;
			String line = null;

			while((line = bReader.readLine()) != null) {
				record = line.split(",");
				if (record[0].equals(member.getMemberId())) {
					result = result + Integer.parseInt(record[2]);
				}//액세스 타입의 합이 1이면 로그인 중이다
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { bReader.close();} catch (Exception e) {e.printStackTrace();}
		}

		return (result == 1);//로그인 중인가? (1인가?)
	}


	//Goods

	ArrayList<String> getGoodsCode(GoodsBean word) {
		ArrayList<String> gCode = new ArrayList<String>();
		File file = new File(this.filePath);

		try {
			fReader = new FileReader(file);
			bReader = new BufferedReader(fReader);

			String line;

			while((line = bReader.readLine()) != null) {
				String[] record = line.split(",");
				for(String item : record) {
					if(item.equals(word.getGoodsDetails())) {
						gCode.add(record[0]);
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { bReader.close();} catch (Exception e) {e.printStackTrace();}
		}

		return gCode;
	}


	void getSalesInfo(ArrayList<String> gCode, ArrayList<GoodsBean> gList) {
		File file = new File(this.filePath);
		try {
			fReader = new FileReader(file);
			bReader = new BufferedReader(fReader);

			String line;

			while((line = bReader.readLine()) != null) {
				String[] record = line.split(",");
				for(int index = 0; index < gCode.size(); index++) {
					if (gCode.get(index).equals(record[1])) {
						GoodsBean gInfo = new GoodsBean();
						gInfo.setGoodsSaler(record[0]);
						gInfo.setGoodsCode(record[1]);
						gInfo.setGoodsPrice(Integer.parseInt(record[2]));
						gInfo.setGoodsStocks(Integer.parseInt(record[3]));
						gList.add(gInfo);
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { bReader.close();} catch (Exception e) {e.printStackTrace();}
		}
	}


	void getGoodsInfo(ArrayList<GoodsBean> gList) {
		File file = new File(this.filePath);
		try {
			fReader = new FileReader(file);
			bReader = new BufferedReader(fReader);
			String line;

			while((line = bReader.readLine()) != null) {
				String[] record = line.split(",");
				for(int index = 0; index<gList.size(); index++) {
					if (gList.get(index).getGoodsCode().equals(record[0])) {
						gList.get(index).setGoodsName(record[1]);
						gList.get(index).setGoodsCategory(record[2]);
						gList.get(index).setGoodsDetails(record[3]);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { bReader.close();} catch (Exception e) {e.printStackTrace();}
		}
	}


	/** 장바구니 정보 쓰기
	 * @name writeBasket
	 * @param basket 장바구니에 등록할 정보
	 * @index 4
	 */
	void writeBasket(BasketBean basket) {
		File file = new File(this.filePath);
		try {
			fWriter = new FileWriter(file, true);
			bWriter = new BufferedWriter(fWriter);

			String record = basket.getUserId()	+ "," +//구매자
					basket.getSaler() + "," +//판매자
					basket.getgCode() + "," +//코드
					basket.getgQty() + "\n";//수량
			bWriter.write(record);
			bWriter.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { bWriter.close();} catch (Exception e) {e.printStackTrace();}
		}
	}

	int getBasketCount(MemberBean member) {
		int count = 0;
		File file = new File(this.filePath);
		try {
			fReader = new FileReader(file);
			bReader = new BufferedReader(fReader);

			String line;
			String[] record;

			while((line = bReader.readLine()) != null) {
				record = line.split(",");
				if (record[0].equals(member.getMemberId())) {
					count++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { bReader.close();} catch (Exception e) {e.printStackTrace();}
		}

		return count;
	}


	void getShoppingData(BasketBean basket,ArrayList<BasketDataBean> basketList) {
		File file = new File(this.filePath);
		try {
			fReader = new FileReader(file);
			bReader = new BufferedReader(fReader);

			String line;

			while((line = bReader.readLine()) != null) {
				String[] record = line.split(",");
				if (basket.getUserId().equals(record[0])) {
					BasketDataBean data = new BasketDataBean();
					data.setUserId(record[0]);//id
					data.setSaler(record[1]);//판매자
					data.setgCode(record[2]);//상품코드
					data.setgQty(Integer.parseInt(record[3]));//수량
					basketList.add(data);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { bReader.close();} catch (Exception e) {e.printStackTrace();}
		}
	}


	void getGoodsData(ArrayList<BasketDataBean> basketList) {
		File file = new File(this.filePath);
		try {
			fReader = new FileReader(file);
			bReader = new BufferedReader(fReader);
			String line;

			while((line = bReader.readLine()) != null) {
				String[] record = line.split(",");
				for(BasketDataBean data : basketList) {
					if(data.getgCode().equals(record[0])) {
						data.setgName(record[1]);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { bReader.close();} catch (Exception e) {e.printStackTrace();}
		}
	}


	void getSalesData(ArrayList<BasketDataBean> basketList) {
		File file = new File(this.filePath);
		try {
			fReader = new FileReader(file);
			bReader = new BufferedReader(fReader);

			String line;

			while((line = bReader.readLine()) != null) {
				String[] record = line.split(",");
				for (BasketDataBean data : basketList) {
					if(data.getSaler().equals(record[0])) {
						if(data.getgCode().equals(record[1])) {
							data.setgPrice(Integer.parseInt(record[2]));
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { bReader.close();} catch (Exception e) {e.printStackTrace();}
		}
	}


	void purchase(ArrayList<BasketDataBean> purList, String time) {
		File file = new File(this.filePath);
		try {
			fWriter = new FileWriter(file, true);
			bWriter = new BufferedWriter(fWriter);
			String record = purList.get(0).getUserId() + "," + 
					time + "," + "C\n";
			bWriter.write(record);
			bWriter.flush();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { bWriter.close();} catch (Exception e) {e.printStackTrace();}
		}
	}

	void purchaseDetail(ArrayList<BasketDataBean> purList, String time) {
		File file = new File(this.filePath);
		try {
			fWriter = new FileWriter(file, true);
			bWriter = new BufferedWriter(fWriter);
			String line;

			for (BasketDataBean data : purList) {
				//구매자,시간,판매자,코드,수량
				line = data.getUserId() + "," +
						time + "," + 
						data.getSaler() + "," + 
						data.getgCode() + "," + 
						data.getgQty() + "\n";
				bWriter.write(line);
				bWriter.flush();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { bWriter.close();} catch (Exception e) {e.printStackTrace();}
		}

	}


	ArrayList<BasketDataBean> getPurchaseInfo(ArrayList<BasketDataBean> userInfo) {
		ArrayList<BasketDataBean> pList = null;
		File file = new File(this.filePath);
		try {
			fReader = new FileReader(file);
			bReader = new BufferedReader(fReader);
			String line;

			while((line = bReader.readLine()) != null) {
				String[] record = line.split(",");
				if (record[0].equals(userInfo.get(0).getUserId())) {
					BasketDataBean data = new BasketDataBean();
					data.setUserId(record[0]);//아이디
					data.setDate(record[1]);//시간
					data.setSaler(record[2]);//판매자
					data.setgCode(record[3]);//코드
					data.setgQty(Integer.parseInt(record[4]));//수량
					if (pList == null) {
						pList = new ArrayList<BasketDataBean>();
					}
					pList.add(data);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { bReader.close();} catch (Exception e) {e.printStackTrace();}
		}


		return pList;
	}


	ArrayList<GoodsBean> getSaleGoodsInfos() {
		File file = new File(this.filePath);
		ArrayList<GoodsBean> saleGoodsInfos = new ArrayList<GoodsBean>();
		try {
			fReader = new FileReader(file);
			bReader = new BufferedReader(fReader);
			String line;

			while((line = bReader.readLine()) != null) {
				String[] record = line.split(",");
				GoodsBean data = new GoodsBean();
				data.setGoodsSaler(record[0]);//판매자
				data.setGoodsCode(record[1]);//코드
				data.setGoodsPrice(Integer.parseInt(record[2]));//가격
				data.setGoodsStocks(Integer.parseInt(record[3]));//재고
				saleGoodsInfos.add(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { bReader.close();} catch (Exception e) {e.printStackTrace();}
		}

		return saleGoodsInfos;
	}



	void renewSaleGoodsStock(ArrayList<GoodsBean> info) {
		File file = new File(this.filePath);
		try {
			fWriter = new FileWriter(file);
			bWriter = new BufferedWriter(fWriter);
			String line;
			for (GoodsBean data : info) {
				line = data.getGoodsSaler() + "," + 
						data.getGoodsCode() + "," + 
						data.getGoodsPrice() + ","+
						data.getGoodsStocks()+"\n";
				bWriter.write(line);
				bWriter.flush();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { bWriter.close();} catch (Exception e) {e.printStackTrace();}
		}
	}

	ArrayList<BasketBean> getBasketInfos() {
		File file = new File(this.filePath);
		ArrayList<BasketBean> bList = new ArrayList<BasketBean>();
		try {
			fReader = new FileReader(file);
			bReader = new BufferedReader(fReader);
			String line;

			while((line = bReader.readLine()) != null) {
				String[] record = line.split(",");
				BasketBean data = new BasketBean();
				data.setUserId(record[0]);
				data.setSaler(record[1]);
				data.setgCode(record[2]);
				data.setgQty(Integer.parseInt(record[3]));
				bList.add(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { bReader.close();} catch (Exception e) {e.printStackTrace();}
		}

		return bList;
	}


	void renewBasketInfos(ArrayList<BasketBean> info) {
		File file = new File(this.filePath);
		try {
			fWriter = new FileWriter(file);
			bWriter = new BufferedWriter(fWriter);
			String line;
			for (BasketBean data : info) {
				line = data.getUserId() + "," + 
						data.getSaler() + "," + 
						data.getgCode() + ","+
						data.getgQty() + "\n";
				bWriter.write(line);
				bWriter.flush();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { bWriter.close();} catch (Exception e) {e.printStackTrace();}
		}
	}

	// 판매자 아이디로 SaleGoods 정보 검색
	void searchSaleGoodsInfos(GoodsBean goods, ArrayList<GoodsBean> gList) {
		File file = new File(this.filePath);
		try {
			fReader = new FileReader(file);
			bReader = new BufferedReader(fReader);
			String line;

			while((line = bReader.readLine()) != null) {
				String[] record = line.split(",");
				if (goods.getGoodsSaler().equals(record[0])) {
					GoodsBean data = new GoodsBean();
					data.setGoodsSaler(record[0]);//판매자
					data.setGoodsCode(record[1]);//코드
					data.setGoodsPrice(Integer.parseInt(record[2]));//가격
					data.setGoodsStocks(Integer.parseInt(record[3]));//재고
					gList.add(data);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { bReader.close();} catch (Exception e) {e.printStackTrace();}
		}
	}


	void getGoodsName(ArrayList<GoodsBean> gList) {
		File file = new File(this.filePath);
		try {
			fReader = new FileReader(file);
			bReader = new BufferedReader(fReader);
			String line;

			while((line = bReader.readLine()) != null) {
				String[] record = line.split(",");
				for (GoodsBean data : gList) {
					if (data.getGoodsCode().equals(record[0])) {
						data.setGoodsName(record[1]);//이름
						data.setGoodsCategory(record[2]);//카테고리
						data.setGoodsDetails(record[3]);//디테일
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { bReader.close();} catch (Exception e) {e.printStackTrace();}
		}
	}



	ArrayList<GoodsBean> readAllSaleGoods(){
		ArrayList<GoodsBean> allRead = new ArrayList<GoodsBean>();
		File file = new File(this.filePath);
		try {
			fReader = new FileReader(file);
			bReader = new BufferedReader(fReader);
			String line;

			while((line = bReader.readLine()) != null) {
				String[] record = line.split(",");
				GoodsBean data = new GoodsBean();
				data.setGoodsSaler(record[0]);
				data.setGoodsCode(record[1]);
				data.setGoodsPrice(Integer.parseInt(record[2]));
				data.setGoodsStocks(Integer.parseInt(record[3]));
				allRead.add(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { bReader.close();} catch (Exception e) {e.printStackTrace();}
		}

		return allRead;
	}


	void renewSaleGoodsTxt(ArrayList<GoodsBean> sgList){
		File file = new File(this.filePath);
		try {
			fWriter = new FileWriter(file, false); // 덮어쓰기
			bWriter = new BufferedWriter(fWriter);
			String line;

			for (GoodsBean data : sgList) {
				line = data.getGoodsSaler()+","+
						data.getGoodsCode()+","+
						data.getGoodsPrice()+","+
						data.getGoodsStocks()+"\n";
				bWriter.write(line);
				bWriter.flush();
			}


		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { bWriter.close();} catch (Exception e) {e.printStackTrace();}
		}
	}

	ArrayList<GoodsBean> readAllGoodsTxt(){
		ArrayList<GoodsBean> gList = new ArrayList<GoodsBean>();
		File file = new File(this.filePath);
		try {
			fReader = new FileReader(file);
			bReader = new BufferedReader(fReader);
			String line;

			while((line = bReader.readLine()) != null) {
				String[] record = line.split(",");
				GoodsBean data = new GoodsBean();
				data.setGoodsCode(record[0]);
				data.setGoodsName(record[1]);
				data.setGoodsCategory(record[2]);
				data.setGoodsDetails(record[3]);
				gList.add(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { bReader.close();} catch (Exception e) {e.printStackTrace();}
		}
		return gList;
	}

	void appendSaleGoodsInfo(GoodsBean goods) {
		File file = new File(this.filePath);
		try {
			fWriter = new FileWriter(file, true);
			bWriter = new BufferedWriter(fWriter);
			String line = goods.getGoodsSaler()+","+
					goods.getGoodsCode()+","+
					goods.getGoodsPrice()+","+
					goods.getGoodsStocks()+"\n";
			
			bWriter.write(line);
			bWriter.flush();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { bWriter.close();} catch (Exception e) {e.printStackTrace();}
		}
	}
}
