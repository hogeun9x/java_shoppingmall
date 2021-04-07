package backend.bean;

public class MemberBean {
	private boolean duplicateCheck;
	private String memberId;
	private String memberName;
	private String memberPassword;
	private String memberType;
	private int memberAge;
	private String accessTime;
	private int accessType;
	private int basektCount;
	
	
	public int getBasektCount() {
		return basektCount;
	}
	public void setBasektCount(int basektCount) {
		this.basektCount = basektCount;
	}
	public String getAccessTime() {
		return accessTime;
	}
	public void setAccessTime(String accessTime) {
		this.accessTime = accessTime;
	}
	public int getAccessType() {
		return accessType;
	}
	public void setAccessType(int accessType) {
		this.accessType = accessType;
	}
	public boolean isDuplicateCheck() {
		return duplicateCheck;
	}
	public void setDuplicateCheck(boolean duplicateCheck) {
		this.duplicateCheck = duplicateCheck;
	}
	public String getMemberId() {
		return memberId;
	}
	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}
	public String getMemberName() {
		return memberName;
	}
	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}
	public String getMemberPassword() {
		return memberPassword;
	}
	public void setMemberPassword(String memberPassword) {
		this.memberPassword = memberPassword;
	}
	public String getMemberType() {
		return memberType;
	}
	public void setMemberType(String memberType) {
		this.memberType = memberType;
	}
	public int getMemberAge() {
		return memberAge;
	}
	public void setMemberAge(int memberAge) {
		this.memberAge = memberAge;
	}
}
