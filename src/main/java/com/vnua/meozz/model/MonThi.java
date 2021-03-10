package com.vnua.meozz.model;

public class MonThi {
	private String tenMonHoc, ghepThi, toThi, soLuong, ngayThi, ngayThu, phongThi, thoiGian, tietThi;

	public MonThi(String tenMonHoc, String ghepThi, String toThi, String soLuong, String ngayThi, String ngayThu,
			String phongThi, String thoiGian, String tietThi) {
		super();
		this.tenMonHoc = tenMonHoc;
		this.ghepThi = ghepThi;
		this.toThi = toThi;
		this.soLuong = soLuong;
		this.ngayThi = ngayThi;
		this.ngayThu = ngayThu;
		this.phongThi = phongThi;
		this.thoiGian = thoiGian;
		this.tietThi = tietThi;
	}

	public String getTenMonHoc() {
		return tenMonHoc;
	}

	public void setTenMonHoc(String tenMonHoc) {
		this.tenMonHoc = tenMonHoc;
	}

	public String getGhepThi() {
		return ghepThi;
	}

	public void setGhepThi(String ghepThi) {
		this.ghepThi = ghepThi;
	}

	public String getToThi() {
		return toThi;
	}

	public void setToThi(String toThi) {
		this.toThi = toThi;
	}

	public String getSoLuong() {
		return soLuong;
	}

	public void setSoLuong(String soLuong) {
		this.soLuong = soLuong;
	}

	public String getNgayThi() {
		return ngayThi;
	}

	public void setNgayThi(String ngayThi) {
		this.ngayThi = ngayThi;
	}

	public String getNgayThu() {
		return ngayThu;
	}

	public void setNgayThu(String ngayThu) {
		this.ngayThu = ngayThu;
	}

	public String getPhongThi() {
		return phongThi;
	}

	public void setPhongThi(String phongThi) {
		this.phongThi = phongThi;
	}

	public String getThoiGian() {
		return thoiGian;
	}

	public void setThoiGian(String thoiGian) {
		this.thoiGian = thoiGian;
	}

	public String getTietThi() {
		return tietThi;
	}

	public void setTietThi(String tietThi) {
		this.tietThi = tietThi;
	}
	
	@Override
	public String toString() {
		return 	"Tên môn thi: "+tenMonHoc+"."
				+"\n\tGhép thi: "+ghepThi+"."
				+"\n\tTổ thi: "+toThi+"."
				+"\n\tSố lượng: "+soLuong+"."
				+"\n\tNgày thi: "+ngayThi+"."
				+"\n\tNgày thứ: "+ngayThu+"."
				+"\n\tPhòng thi: "+phongThi+"."
				+"\n\tThời gian: "+thoiGian+"."
				+"\n\tTiết thi: "+tietThi+".";
	}
}
