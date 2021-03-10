package com.vnua.meozz.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class CustomListCellThird extends ListCell<MonHoc> {

	private String currentDateNote = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
	private HBox hboxMainSub, hboxFirst, hboxSecond, hboxThird, hboxImView;
	private Label lblCourseName, lblPlace, lblShift, lblTime, lblPeriod, lblRoom, lblShiftNum;
	private VBox vboxSub, vBoxMain, vboxSubSub;
	private Line endLineBorder;
	private Rectangle rect;
	private ImageView timeImView;
	private int countCheck;
	
	public CustomListCellThird() {
		int insetLabel = 10;

		// draw 2 line
		endLineBorder = new Line(0, 80, 3000, 80);
		endLineBorder.setStroke(Color.rgb(161, 160, 160));
		
		rect = new Rectangle(12, 82);
		rect.setFill(Color.BLACK);
		lblCourseName = new Label("Giao duc the chat dai cuong");
	
		lblShift = new Label("7h00 - 11h30");
		lblTime = new Label("Thời gian: ");
		lblPlace = new Label("SAN04");
		lblShiftNum = new Label("1 - 5: ");
		lblPeriod = new Label("Tiết ");
		lblRoom = new Label("Phòng: ");
		
		// hbox 1
		Image timeIcon = new Image(getClass().getResourceAsStream("images/ic_clock_24.png"));
		timeImView = new ImageView(timeIcon);
		timeImView.setFitWidth(16);
		timeImView.setFitHeight(16);
		
		hboxImView = new HBox(timeImView);
		hboxImView.setPadding(new Insets(2.75, 0, 0, 0));
		
		lblShift.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
				+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #00667C");
//		lblShift.setPadding(new Insets(0, 0, 0, 20));
		lblTime.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
				+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #00667C");
		lblTime.setPadding(new Insets(0, 0, 0, 10));
		hboxFirst = new HBox(hboxImView, lblTime, lblShift);
		hboxFirst.setPrefHeight(10);
		hboxFirst.setMaxHeight(18);
		hboxFirst.setPadding(new Insets(5.25, 0, 3, 0));
		
		// hbox 2
		lblCourseName.setStyle("-fx-font-size: 17px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
				+ "  -fx-font-family: 'Bitter';" + "-fx-text-fill: #000");
//		lblCourseName.setPadding(new Insets(insetLabel, insetLabel, 0, 0));
		lblShiftNum.setStyle("-fx-font-size: 17px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
				+ "  -fx-font-family: 'Bitter';" + "-fx-text-fill: #000");
//		lblShiftNum.setPadding(new Insets(insetLabel + 20, insetLabel + 5, insetLabel + 10, insetLabel + 5));
		lblPeriod.setStyle("-fx-font-size: 17px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
				+ "  -fx-font-family: 'Bitter';" + "-fx-text-fill: #000");
//		lblPeriod.setPadding(new Insets(insetLabel + 20, insetLabel + 5, insetLabel + 10, insetLabel + 5));
		hboxSecond = new HBox(lblPeriod, lblShiftNum, lblCourseName);
		hboxSecond.setPadding(new Insets(0, 0, 3, 0));
		
		// hbox 3
		lblRoom.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
				+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #EB5E30");
//		lblPlace.setPadding(new Insets(0, insetLabel, insetLabel, 0));
		lblPlace.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
				+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #EB5E30");
//		lblPlace.setPadding(new Insets(0, insetLabel, insetLabel, 0));
		hboxThird = new HBox(lblRoom, lblPlace);
		hboxThird.setPadding(new Insets(0, 0, 3, 0));
		
		vboxSub = new VBox(hboxSecond, hboxThird);
		vboxSub.setStyle("-fx-background-color:#fff");
		vboxSub.setPadding(new Insets(0, 0, 0, 28));
		vboxSub.setStyle("-fx-background-color:#fff;" + "\r\n-fx-border-color: #D3D3D3;\r\n" + "-fx-border-width: 0;");
		
		vboxSubSub = new VBox(hboxFirst, vboxSub);
		vboxSubSub.setStyle("-fx-background-color:#fff");
//		vboxSubSub.setPadding(new Insets(insetLabel+10, insetLabel, insetLabel+10, 50));
		vboxSubSub.setStyle("-fx-background-color:#fff;" + "\r\n-fx-border-color: #D3D3D3;\r\n" + "-fx-border-width: 0;");
		
		hboxMainSub = new HBox(insetLabel, rect, vboxSubSub);
		hboxMainSub.setStyle("-fx-background-color:#fff;" + "\r\n-fx-border-color: #D3D3D3;\r\n" + "-fx-border-width: 0;");
	}

	@Override
	protected void updateItem(MonHoc course, boolean empty) {
		if (course != null && !empty) {
			lblCourseName.setText(course.getTenMonHoc().toString());
			lblPlace.setText(course.getDiaDiemHoc().toString());
			lblShift.setText(course.getStSh().toString()+" - "+course.getEnSh().toString());
			lblShiftNum.setText(course.getShNu().toString());

			// set random color (#CB67C7, #00DED1, #F2BC54, #C3604A, #1F6356, #C59586, #FFA318, #999161, #F35E13, #9155BE, #4BAC9F)
			String[] otColorList = new String[] {"#75D5FD", "#FFDB5C", "#7B61F8", "#E73F0B", "#FFDEF3", "#EBF875", "#00A9FE", "#B778BF"
//					, "#999161", "#c39797", "#9155BE", "#4BAC9F"
					};
			String[] ffColorList = new String[] {"#B76CFD", "#F8A055", "#F18D9E", "#FF9472", "#35212A", "#28CF75", "#7DA3A1", "#04BFBF"
//					, "#124559", "#333333", "#49fce0", "#5ac18e"
					};
			String[] seColorList = new String[] {"#EDAE01", "#C7DB00", "#563E20", "#556DAC", "#FAAF08", "#B76CFD", "#ED5752", "#AF4425"};
			String[] etColorList = new String[] {"#867666", "#D72C16", "#EBDF00", "#F5CA99", "#739F3D", "#FA812F", "#E2DFA2", "#C9A66B"};
			String[] ttColorList = new String[] {"#68A225", "#523634", "#BBCF4A", "#7D5E3C", "#92AAC7", "#75D5FD", "#4C3F54", "#B8D20B"};
			
			Random ranUtil = new Random();
			int randomNum = ranUtil.nextInt(6);
			
			// check start shift of course is odd or even
			int stSh = Integer.parseInt(course.getTietBatDau());
			if(stSh<4&&stSh>0) {
				rect.setStyle("-fx-fill:"+otColorList[countCheck]+";");
			} else if(stSh>=4 && stSh<6) {
				rect.setStyle("-fx-fill:"+ffColorList[countCheck]+";");
			} else if(stSh>=6 && stSh<9) {
				rect.setStyle("-fx-fill:"+seColorList[countCheck]+";");
			} else if(stSh>=9 && stSh<11) {
				rect.setStyle("-fx-fill:"+etColorList[countCheck]+";");
			} else if(stSh>=11 && stSh<13) {
				rect.setStyle("-fx-fill:"+ttColorList[countCheck]+";");
			}
			
			// index up from 0 to 14 and down to 0
//			while(
//					indexUp>=0 
////					&& 
//					) {
//				if(indexUp%2!=0) {
//					rect.setStyle("-fx-fill:"+randomColorOdd[indexUp++]+";");
//				} else {
//					rect.setStyle("-fx-fill:"+randomColorEven[indexUp++]+";");
//				}
//			}
			
			// check checked day is present day or tomorrow 
			if(course.getNgayHoc().equals(currentDateNote)) {
				// set Color : #99FFFF (Blue tone), #728360, #BBD8F2, #D6CEF2, #C4DFE6, #F1F1F2, #D0E1F9, #ACD0C0, #C9D1C8, #B9D9C3
				vboxSub.setStyle("-fx-background-color:#B9D9C3;\r\n-fx-border-color: #D3D3D3;\r\n-fx-border-width: 0;");
				hboxMainSub.setStyle("-fx-background-color:#B9D9C3;\r\n-fx-border-color: #D3D3D3;\r\n-fx-border-width: 0;");
				vboxSubSub.setStyle("-fx-background-color:#B9D9C3;\r\n-fx-border-color: #D3D3D3;\r\n-fx-border-width: 0;");
			} else {
				// tomorrow
				Calendar c = Calendar.getInstance();
				Date stDate;
				try {
					stDate = new SimpleDateFormat("dd/MM/yyyy").parse(currentDateNote);
					c.setTime(stDate);
					c.add(Calendar.DAY_OF_MONTH, 1);
					String resDay = new SimpleDateFormat("dd/MM/yyyy").format(c.getTime());
					if (resDay.equals(course.getNgayHoc())) {
						// set Color : #BFF66A (Green tone), #BFB26F, #94A2F2, #EFEBF2, #E4EA8C
						vboxSub.setStyle("-fx-background-color:#E4EAA1;\r\n-fx-border-color: #D3D3D3;\r\n-fx-border-width: 0;");
						hboxMainSub.setStyle("-fx-background-color:#E4EAA1;\r\n-fx-border-color: #D3D3D3;\r\n-fx-border-width: 0;");
						vboxSubSub.setStyle("-fx-background-color:#E4EAA1;\r\n-fx-border-color: #D3D3D3;\r\n-fx-border-width: 0;");
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			vBoxMain = new VBox(hboxMainSub, endLineBorder);
			
			setGraphic(vBoxMain);
		} else {
			setGraphic(null);
		}
		
		
	}

	public int getCountCheck() {
		return countCheck;
	}

	public void setCountCheck(int countCheck) {
		this.countCheck = countCheck;
	}

}
