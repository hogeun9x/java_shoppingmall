package backend.bean;

public class BasketBean {
	private String userId;
	private String saler;
	private String gCode;
	private int gQty;
	
	public String getUserId() {
		
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getSaler() {
		return saler;
	}
	public void setSaler(String saler) {
		this.saler = saler;
	}
	public String getgCode() {
		return gCode;
	}
	public void setgCode(String gCode) {
		this.gCode = gCode;
	}
	public int getgQty() {
		return gQty;
	}
	public void setgQty(int gQty) {
		this.gQty = gQty;
	}
	
}
