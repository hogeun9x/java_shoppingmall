package backend.services;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import backend.bean.MemberBean;

public class Authentication {
	DataAccessObject dao;
	String path;
	
	public Authentication() {
		this.path = "C:\\Hogun\\ICIA\\hogeun_test\\workspace\\phone\\src\\data";
	}
	
	public ArrayList<MemberBean> backController(int serviceCode, MemberBean member) {
		ArrayList<MemberBean> list = null;
		
		// 클라이언트 요청(serviceCode)에 따른 Job 분기
		switch(serviceCode) {
		case 1:
			this.duplicateCheck(member);
			break;
		case 2:
			this.regMemberControl(member);
			break;
		case 3:
			list = this.searchMembersInfo(member);
			break;
		case -1:
			this.accessOut(member);
			break;
		}
		
		return list;
	}
	
	/* 메서드 오버로딩 */
	public MemberBean backController(String serviceCode, MemberBean member) {
		MemberBean accessInfo = null;
		
		// 클라이언트 요청(serviceCode)에 따른 Job 분기
		switch(serviceCode) {
		case "A":
			accessInfo = this.accessControl(member);
		}
		
		return accessInfo;
	}
	
	/** 아이디 중복체크, !아이디 존재유무 확인
	 *  @author 	hoonzzang
	 *  @name		private  duplicateCheck
	 *  @param		MemberBean member
	 *  @return 	boolean isCheck :: 사용가능 - true :: 사용불가 - false
	 */
	private boolean duplicateCheck(MemberBean member) {
		boolean isCheck = false; // true:: 사용가능   false :: 사용불가능
		dao = new DataAccessObject(0, this.path);
		
		
		isCheck = dao.duplicateCheck(member);
		member.setDuplicateCheck(isCheck);
		
		return isCheck;
	}
	
	/** 회원등록 제어
	 * @name private regMember
	 * @param member 등록할 회원 정보
	 */
	private void regMemberControl(MemberBean member) {
	
		// 중복체크 :: 사용가능한 아이디라면
		if(this.duplicateCheck(member)) {
			// 등록 :: 회원정보 등록 요청
			if(this.regUser(member)) {
				// 정보수집 :: 등록된 회원정보를 가져옴
				member = this.searchMemberInfo(member);
			}
		}		
	}

	/** 로그인 제어 메서드
	 * @name accessControl
	 * @param MemberBean member
	 * @return void
	 */
	private MemberBean accessControl(MemberBean member) {
		MemberBean accessInfo = null;
		
		// 1.아이디 존재 유무 확인
		if (!this.duplicateCheck(member)) {
			// 2. 회원 정보 일치확인
			if (this.isAccess(member)) {
				// Append : History Table에 로그인 기록 남기기 :: id, time, 1
				member.setAccessTime(this.getNow());
				member.setAccessType(1);
				// History table 에 로그인 기록 저장
				this.writeLogInInfo(member);
				
				// 3. 회원 정보 수집 :: 아이디, 이름, 타입
				accessInfo = this.searchMemberInfo(member);
				// [ 아이디(이름:타입) AccessTime : yyyyMMddHHmmss]
				accessInfo.setAccessTime(this.searchAccessTime(member));
				// 4. 계정의 정보 중 패스워드와 나이 데이터는 삭제
				accessInfo.setMemberPassword(null);
				accessInfo.setMemberAge(0);
				// 장바구니 갯수 입력
				accessInfo.setBasektCount(this.getBasketCount(member));
			}
		}
		
		return accessInfo;
	}
	
	
	private String searchAccessTime(MemberBean member) {
		ArrayList<MemberBean> list;
		int accessTime = -1;
		boolean check = true;
		
		dao = new DataAccessObject(7,this.path);
		list = dao.readAccessInfo(member);
		
		// list에서 최대값을 갖는 데이터를 추출
		for(MemberBean data : list) {
			//accessTime = (accessTime < Integer.parseInt(data.getAccessTime()))?
			//		Integer.parseInt(data.getAccessTime()):accessTime;
			
			if (check) {
				accessTime = Integer.parseInt(data.getAccessTime());
				check = false;
			}else {
				if (accessTime < Integer.parseInt(data.getAccessTime())) {
					accessTime = Integer.parseInt(data.getAccessTime());
				}
			}
		
		}
		
		return accessTime+"";
	}
	
	
	
	/** 로그인 정보 History에 쓰기
	 * @name private writeLogInInfo
	 * @param MemberBean member
	 * @return void
	 */
	private void writeLogInInfo(MemberBean member) {
		dao = new DataAccessObject(7,this.path);
		dao.writeHistory(member);
	}
	
	/** 현재 시간 리턴 yyyyMMddHHmmss
	 * 
	 * @return String 현재 시간
	 */
	private String getNow() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		return sdf.format(date);
	}
	
	/** 로그인 정보 일치 확인 :: 아이디 패스워드
	 * @name isAccess
	 * @param MemberBean member
	 * @return boolean :: 로그인 성공 - true :: 로그인 실패 - false
	 */
	private boolean isAccess(MemberBean member) {
		this.dao = new DataAccessObject(0, this.path);
		return this.dao.isAccess(member);
	}
	
	/** 회원정보 등록
	 *  @author 	hoonzzang
	 *  @name		private regUser
	 *  @param		MemberBean member
	 *  @return		boolean isCheck
	 */
	private boolean regUser(MemberBean member) {
		dao = new DataAccessObject(0, this.path);
		
		// 등록 요청	// 등록 여부 리턴
		return dao.registrationMember(member);
	}
	
	/** 회원정보 검색
	 * @name		private  searchMemberInfo
	 * @param		String 	 memberId
	 * @return 		MemberBean memberInfo
	 */
	private MemberBean searchMemberInfo(MemberBean member) {
		dao = new DataAccessObject(0, this.path);
		
		return dao.searchMemberInfo(member);
	}	
	
	
	private ArrayList<MemberBean> searchMembersInfo(MemberBean member) {
		this.dao = new DataAccessObject(0, this.path);
		
		return dao.searchMembersInfo(member);
	}

	
	/** 로그아웃
	 * @name private accessOut
	 * @param MemberBean member
	 * @return void
	 */
	private void accessOut(MemberBean member) {
		this.dao = new DataAccessObject(7, this.path);
		
		// History Table에서 로그인 상태 여부 확인
		// :: 해당 계정의 액세스 타입을 숫자로 변환해서 누적합 -> 최종값 : 0 또는 1  <-- DAO
		// ::: 0인 경우 --> 로그아웃 상태    1--> 로그인 상태
		// 1--> History Table에 로그아웃 기록
		// 0--> History Table 작업 필요 X
		if(this.dao.isLogin(member)) {//로그인 중 이라면
			member.setAccessType(-1);//타입 -1(로그아웃)
			member.setAccessTime(this.getNow());//현재 시간
			this.writeLogInInfo(member);//로그아웃 기록 쓰기
		}/*else {아닐 경우(0인 경우) 작업 필요 X}*/
	}
	
	private int getBasketCount(MemberBean member) {
		//dao 4
		this.dao = new DataAccessObject(4, this.path);
		return this.dao.getBasketCount(member);
	}
}








