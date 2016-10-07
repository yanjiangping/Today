package entity;

public class Lable {
	String lableId;
	String lableName;
	public Lable(String lableId,String lableName){
		this.lableId=lableId;
		this.lableName=lableName;
	}
	public String getLableId() {
		return lableId;
	}
	public void setLableId(String lableId) {
		this.lableId = lableId;
	}
	public String getLableName() {
		return lableName;
	}
	public void setLableName(String lableName) {
		this.lableName = lableName;
	}
	
}
