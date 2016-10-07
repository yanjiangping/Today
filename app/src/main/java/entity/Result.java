package entity;

public class Result{
	public String stat;
	public String getStat() {
		return stat;
	}
	public void setStat(String stat) {
		this.stat = stat;
	}
	public Data[] getData() {
		return data;
	}
	public void setData(Data[] data) {
		this.data = data;
	}
	public Data[] data;
}