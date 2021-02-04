package com.vnua.meozz.model;

import java.util.ArrayList;

public class Tuan {
	private String maTuan, startDate, endDate;
	public String getStartDate() {
		return startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	private ArrayList<MonHoc> dsMonHoc;
	
	public Tuan(String maTuan, String startDate, String endDate) {
		this.maTuan = maTuan;
		this.startDate = startDate;
		this.endDate = endDate;
		dsMonHoc = new ArrayList<MonHoc>();
	}

	public String getMaTuan() {
		return maTuan;
	}

	public void setMaTuan(String maTuan) {
		this.maTuan = maTuan;
	}

	public ArrayList<MonHoc> getDsMonHoc() {
		return dsMonHoc;
	}

	public void setDsMonHoc(ArrayList<MonHoc> dsMonHoc) {
		this.dsMonHoc = dsMonHoc;
	}
	
	@Override
	public String toString() {
		String courseAll = "";
		for(int q = 0;q<dsMonHoc.size();q++) {
			MonHoc course = dsMonHoc.get(q);
			courseAll+="Môn hoc thứ "+(q+1)+" :\n"+course.toString()+"\n";
		}
		return "Mã tuân: "+maTuan+"."
				+"\n Ngay bắt đâu tuân hoc: "+startDate+"."
				+"\n Ngay kết thúc tuân hoc: "+endDate+"."
				+"\nDanh sách các môn hoc: \n"
				+courseAll;
	}
	
}
