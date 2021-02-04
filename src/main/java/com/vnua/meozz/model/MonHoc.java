package com.vnua.meozz.model;

public class MonHoc {
	private String maMonHoc, tenMonHoc, tietBatDau, diaDiemHoc, ngayThu, thoiGianHoc, stSh, enSh, shNu, ngayHoc;

	public String getMaMonHoc() {
		return maMonHoc;
	}

	public String getDiaDiemHoc() {
		return diaDiemHoc;
	}

	public String getStSh() {
		return stSh;
	}

	public String getEnSh() {
		return enSh;
	}

	public String getShNu() {
		return shNu;
	}

	public void setTietBatDau(String tietBatDau) {
		this.tietBatDau = tietBatDau;
	}

	public String getNgayThu() {
		return ngayThu;
	}

	public void setNgayThu(String ngayThu) {
		this.ngayThu = ngayThu;
	}

	public String getNgayHoc() {
		return ngayHoc;
	}

	public void setNgayHoc(String ngayHoc) {
		this.ngayHoc = ngayHoc;
	}

	public String getTenMonHoc() {
		return tenMonHoc;
	}

	public String getTietBatDau() {
		return tietBatDau;
	}

	public void setThoiGianHoc(String thoiGianHoc) {
		this.thoiGianHoc = thoiGianHoc;
	}

	public void setStSh(String stSh) {
		this.stSh = stSh;
	}

	public void setEnSh(String enSh) {
		this.enSh = enSh;
	}

	public void setShNu(String shNu) {
		this.shNu = shNu;
	}

	public MonHoc(String maMonHoc, String tenMonHoc, String tietBatDau, String diaDiemHoc, String ngayThu, String thoiGianHoc, String stSh, String enSh, String shNu, String ngayHoc) {
		this.maMonHoc = maMonHoc;
		this.tenMonHoc = tenMonHoc;
		this.tietBatDau = tietBatDau;
		this.diaDiemHoc = diaDiemHoc;
		this.ngayThu= ngayThu;
		this.thoiGianHoc = thoiGianHoc;
		this.stSh = stSh;
		this.enSh = enSh;
		this.shNu = shNu;
		this.ngayHoc = ngayHoc;
	}


	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}
		if(obj instanceof MonHoc) {
			return ((MonHoc)obj).getMaMonHoc().equals(this.maMonHoc);
		}
		return super.equals(obj);
	}
	
	@Override
	public String toString() {
		return "\tMã môn hoc: "+maMonHoc+"."
				+"\n\tTên môn hoc: "+tenMonHoc+"."
				+"\n\tTiết bắt đâu: "+tietBatDau+"."
				+"\n\tĐia điểm hoc: "+diaDiemHoc+"."
				+"\n\tNgay thứ: "+ngayThu+"."
				+"\n\t Thơi gian hoc: "+thoiGianHoc+"."
				+"\n\tNgay: "+ngayHoc+".";
	}
	
}
