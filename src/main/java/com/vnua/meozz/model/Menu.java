package com.vnua.meozz.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.gluonhq.charm.glisten.control.Toast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vnua.meozz.main.Main;

public class Menu {
	private ArrayList<Tuan> listTuan;
	private ArrayList<MonThi> listMonThi;
	private static final Logger LOGGER = Logger.getLogger(Menu.class.getName());
	private String textRawHandled;
	private ArrayList<String> listTextCourseFilter, listTextCourseFilterOne, listCourseCode, listCourseName,
			listDayOfWeek, listShift, listLocation, listWeek, listFineCourseCode;
	public static final String ROOT_URL = "http://daotao.vnua.edu.vn/Default.aspx?page=thoikhoabieu&sta=1&id=";
	public static final String URL_FINAL_EXAM = "http://daotao.vnua.edu.vn/Default.aspx?page=xemlichthi&id=";
	public static final String URL_FINAL_EXAM_SDH = "http://daotao.vnua.edu.vn/Default.aspx?page=xemlichthigk&id=";
	public static final String DOWNLOAD_URL = "E:/";
	public String inputCode = "651806";// 651114, 651056, CNP09 - Tráº§n Trung Hiáº¿u,
	// CNP02 - NgÃ´ CÃ´ng Tháº¯ng, CNP03 - Ä�á»— Thá»‹ NhÃ¢m
	// CNP05 - Phan Trá»�ng Tiáº¿n, CNP07 - HoÃ ng Thá»‹ HÃ .
	// 650850, 651806
	public static final LocalDate CURRENT_DATE = LocalDate.now();
	private int currentDate = CURRENT_DATE.getDayOfMonth(), currentMonth = CURRENT_DATE.getMonthValue(),
			currentYear = CURRENT_DATE.getYear();
	private ArrayList<MonHoc> listCourseEachDay;
	private String currentDateNote = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
	private String txtNote = "", txtUpdate = "", startDate = "", recentUpdateDate = "", currentDateCheck = "",
			currentEngDayOfWeek = "", currentDOW = "";
	private int studyWeek, lengthListCourseCurrentDOW;
	private Document doc, docFinalExam;

	// Current List Course and Surround it
	private ArrayList<MonHoc> listCourseCurrentWeek, listCourseTheWeekAT, listCourseTomo, listCourseYest,
			listCourseTheWeekBY;
	private String txtCurrentWeek, txtTheWeekBB, txtTheWeekB, txtTheWeekA, txtTheWeekAA;

	// List Course Via Chosen Week
	private ArrayList<MonHoc> listCourseMondayCW, listCourseTuesdayCW, listCourseWednesdayCW, listCourseThursdayCW,
			listCourseFridayCW, listCourseSaturdayCW, listCourseSundayCW;
	private int chosenWeek;

	// Current Date, Month, Year
	private String currentMonthString = "";
	private int indexFocusCurrentDayStart, indexFocusCurrentDayEnd;
	private String[] strDays = new String[] { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday",
			"Saturday" };
	private boolean cwBoolean = true;
	private boolean CONFIRM_WRITE_FILE_FIRST = false;

	// Start shifts and end shifts
	private static final String[] START_SHIFT_ARRAY = new String[] { "7h00", "7h55", "8h50", "9h55", "10h55", "12h45",
			"13h40", "14h35", "15h40", "16h35", "18h00", "18h55", "19h50" };
	private static final String[] END_SHIFT_ARRAY = new String[] { "7h50", "8h45", "9h40", "10h45", "11h40", "13h35",
			"14h30", "15h25", "16h30", "17h25", "18h50", "19h45", "20h40" };
	private static final String SPAN_ID_LESS = "span#ctl00_ContentPlaceHolder1_ctl00_gvXem_ctl0";
	private static final String SPAN_ID_GREATER = "span#ctl00_ContentPlaceHolder1_ctl00_gvXem_ctl0";
	private boolean CONFIRM_WRITE_FILE_FIRST_FE = false;
	private String currentDevice = "";

	public Menu() {
		listTuan = new ArrayList<Tuan>();
		listMonThi = new ArrayList<MonThi>();
		listTextCourseFilter = new ArrayList<String>();// string tá»•ng
		listTextCourseFilterOne = new ArrayList<String>();// string tá»•ng 2
		listCourseCode = new ArrayList<String>(); // mÃ£ mÃ´n há»�c
		listCourseName = new ArrayList<String>(); // tÃªn mÃ´n há»�c
		listDayOfWeek = new ArrayList<String>(); // thá»©
		listShift = new ArrayList<String>(); // tiáº¿t báº¯t Ä‘áº§u - káº¿t thÃºc
		listLocation = new ArrayList<String>(); // phÃ²ng há»�c
		listWeek = new ArrayList<String>(); // tuáº§n há»�c
		listCourseEachDay = new ArrayList<MonHoc>();
		listCourseCurrentWeek = new ArrayList<MonHoc>();
		listCourseTheWeekAT = new ArrayList<MonHoc>();
		listCourseTheWeekBY = new ArrayList<MonHoc>();
		listCourseTomo = new ArrayList<MonHoc>();
		listCourseYest = new ArrayList<MonHoc>();
		listCourseMondayCW = new ArrayList<MonHoc>();
		listCourseTuesdayCW = new ArrayList<MonHoc>();
		listCourseWednesdayCW = new ArrayList<MonHoc>();
		listCourseThursdayCW = new ArrayList<MonHoc>();
		listCourseFridayCW = new ArrayList<MonHoc>();
		listCourseSaturdayCW = new ArrayList<MonHoc>();
		listCourseSundayCW = new ArrayList<MonHoc>();
		setListFineCourseCode(new ArrayList<String>()); // mÃ£ mÃ´n há»�c chuáº©n

		// pasre Month Integer to Month String
		currentMonthString = monthFromIntToString(currentMonth);
		currentEngDayOfWeek = convertDateWeek(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
		currentDateCheck = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

		Calendar calendarr = Calendar.getInstance();
		calendarr.setTime(new Date());
		String vietDOW = calendarr.get(Calendar.DAY_OF_WEEK) + "";
		currentDOW = dayOfWeekFromVietToEngInt(vietDOW);

		// set default chosen week
		chosenWeek = 1;

	}

	public void resetList() {
		listTuan = new ArrayList<Tuan>();
		listMonThi = new ArrayList<MonThi>();
		listTextCourseFilter = new ArrayList<String>();// string tá»•ng
		listTextCourseFilterOne = new ArrayList<String>();// string tá»•ng 2
		listCourseCode = new ArrayList<String>(); // mÃ£ mÃ´n há»�c
		listCourseName = new ArrayList<String>(); // tÃªn mÃ´n há»�c
		listDayOfWeek = new ArrayList<String>(); // thá»©
		listShift = new ArrayList<String>(); // tiáº¿t báº¯t Ä‘áº§u - káº¿t thÃºc
		listLocation = new ArrayList<String>(); // phÃ²ng há»�c
		listWeek = new ArrayList<String>(); // tuáº§n há»�c
		listCourseEachDay = new ArrayList<MonHoc>();
		listCourseCurrentWeek = new ArrayList<MonHoc>();
		listCourseTheWeekAT = new ArrayList<MonHoc>();
		listCourseTheWeekBY = new ArrayList<MonHoc>();
		listCourseTomo = new ArrayList<MonHoc>();
		listCourseYest = new ArrayList<MonHoc>();
		listCourseMondayCW = new ArrayList<MonHoc>();
		listCourseTuesdayCW = new ArrayList<MonHoc>();
		listCourseWednesdayCW = new ArrayList<MonHoc>();
		listCourseThursdayCW = new ArrayList<MonHoc>();
		listCourseFridayCW = new ArrayList<MonHoc>();
		listCourseSaturdayCW = new ArrayList<MonHoc>();
		listCourseSundayCW = new ArrayList<MonHoc>();
		setListFineCourseCode(new ArrayList<String>()); // mÃ£ mÃ´n há»�c chuáº©n

		// pasre Month Integer to Month String
		currentMonthString = monthFromIntToString(currentMonth);
		currentEngDayOfWeek = convertDateWeek(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
		currentDateCheck = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

		Calendar calendarr = Calendar.getInstance();
		calendarr.setTime(new Date());
		String vietDOW = calendarr.get(Calendar.DAY_OF_WEEK) + "";
		currentDOW = dayOfWeekFromVietToEngInt(vietDOW);

		// set default chosen week
		chosenWeek = 1;

	}

	public String handleJsoupAndJSON() throws IndexOutOfBoundsException {

		// handle JSoup
		try {
			doc = Jsoup.connect(ROOT_URL + inputCode).timeout(5000).get();
//			docFinalExam = Jsoup.connect(URL_FINAL_EXAM + inputCode).timeout(5000).get();
		} catch (Exception e1) {
			if (e1 instanceof IOException) {
				return "NO_INTERNET_CONNECTION";
			}
		}

		// Handle download HTML file and store it in drive E:/

		// Create URL object
//			URL url = new URl(ROOT_URL + STUDENT_CODE);
//			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
		//
//			// Enter filename in which you want to download
//			BufferedWriter writer = new BufferedWriter(new FileWriter(DOWNLOAD_URL+"Schedule-Student-"+STUDENT_CODE+".html"));
		//
//			// read each line from stream till end
//			String line;
//			while ((line = readr.readLine()) != null) {
//				writer.write(line);
//				System.out.println(line);
//			}
		//
//			readr.close();
//			writer.close();
//			System.out.println("Successfully Downloaded.");

		// Handle the content

		// Title
//		String title = doc.title();
//		System.out.println("Title: " + title);

		// Note

		Elements labels = doc.select("span.Label"), labelsFinalExam = doc.select("span.Label");
		int count = 0;

		try {
			for (Element el : labels) {
				count++;
				if (count == 12) {
					txtNote = "Lưu ý: " + el.text().substring(8, el.text().length() - 1);
					startDate = txtNote.substring(txtNote.length() - 10, txtNote.length());
					System.out.println(startDate);
				} else if (count == 14) {
					txtUpdate = "Thơi điểm gân nhất dữ liêu đươc câp nhât: "
							+ el.text().substring(2, el.text().length() - 1);
					recentUpdateDate = txtUpdate.substring(txtUpdate.length() - 10, txtUpdate.length());
				}
			}
		} catch (Exception e) {
			if (e instanceof IndexOutOfBoundsException) {
				return "WRONG_INPUT_CODE";
			}
		}

		// Validation

//		System.out.println("Ngày bắt đầu tuần học đầu: " + startDate + "\n, vÃ  ngÃ y cáº­p nháº­t gáº§n Ä‘Ã¢y nháº¥t: "
//				+ recentUpdateDate + ".");

		// Table
		Elements table = doc.select("table.body-table");
		count = 0;

		// Reg - ex
		Pattern patternNumber = Pattern.compile("[0-9]+"), patternUnicodeChar = Pattern.compile("\\p{L}+"),
				patternText = Pattern.compile("((?=.\\d*)(?=.*[A-Z])(?=[_]*).{1,20})"),
				patternOneDigit = Pattern.compile("[\\d]{1}"), patternDSSV = Pattern.compile("[DSSV]+"),
				patternDV = Pattern.compile("[^DV]");

		for (Element elem : table) {
			String content = elem.text().trim();
			System.out.println(content);
			if (content.length() > 2 && content.length() <= 10) {
			} else if (content.length() > 50) {
				textRawHandled = content.replace(" ", "*");
				listCourseCode.add(textRawHandled.substring(0, 8));

				listTextCourseFilter.add(textRawHandled.substring(8, textRawHandled.length()));
			}
		}

		// Find the quantity of "DSSV" words
		int maxAppearanceQuantity = 0, checkC = 0;
		String subStringCheck = "DSSV";
		for (int e = 0; e < listTextCourseFilter.size(); e++) {
			String st = listTextCourseFilter.get(e);
			while ((checkC = st.indexOf(subStringCheck, checkC)) != -1) {
				maxAppearanceQuantity++;
				checkC += subStringCheck.length();
			}
		}

		// Handle "DSSV" clone into double, triple or etc.
		int countChar = 0;
		boolean flagCheck = false;
		String stringClone = "", theLastFourChar;
		for (int e = 0; e < listTextCourseFilter.size(); e++) {
			stringClone = listTextCourseFilter.get(e);
			for (int f = 0; f < maxAppearanceQuantity; f++) {
				theLastFourChar = stringClone.substring(stringClone.length() - 4, stringClone.length());
				if (theLastFourChar.equals("DSSV")) {
					StringBuilder sb = new StringBuilder(stringClone);
					stringClone = sb.delete(sb.length() - 5, sb.length()).toString();
				} else {
					// check duplicate
					if (listTextCourseFilterOne.size() == 0 && countChar < maxAppearanceQuantity) {
						listTextCourseFilterOne.add(stringClone);
						flagCheck = false;
						countChar++;
					} else {
						for (int d = 0; d < listTextCourseFilterOne.size(); d++) {
							if (stringClone.equals(listTextCourseFilterOne.get(d))) {
								// do nothing
							} else {
								flagCheck = true;
							}
						}
						if (flagCheck == true && countChar < maxAppearanceQuantity && countChar == e) {
							listTextCourseFilterOne.add(stringClone);
							flagCheck = false;
							countChar++;
						}
					}
				}
			}
		}

		// handle object stored

		// Step 1: handle Start Date and kick off to count
		try {
			int currentDateOfYear = CURRENT_DATE.getDayOfYear(), koDateOfYear = dayOfYear(startDate),
					minusDateOfYear = 0, currentWeekOfYear = 0, currentWeekOfMonth = 0;

			if (CURRENT_DATE.getYear() == Integer
					.parseInt(startDate.substring(startDate.length() - 4, startDate.length()))) {
				minusDateOfYear = currentDateOfYear - koDateOfYear + 1;
				currentWeekOfYear = minusDateOfYear / 7 + 1;
				currentWeekOfMonth = currentWeekOfYear % 4;
			} else {
				// check leap year
				boolean isLeap = false;
				int allDays = 365;

				if (CURRENT_DATE.getYear() % 4 == 0) {
					if (CURRENT_DATE.getYear() % 100 == 0) {
						if (CURRENT_DATE.getYear() % 400 == 0)
							isLeap = true;
						else
							isLeap = false;
					} else
						isLeap = true;
				} else {
					isLeap = false;
				}

				if (isLeap) {
					allDays++;
				}

				int dayLastYear = allDays - koDateOfYear + 1 + currentDateOfYear;
				currentWeekOfYear = dayLastYear / 7 + 1;
				currentWeekOfMonth = currentWeekOfYear % 4;
			}
			// Handle current study week
			studyWeek = currentWeekOfYear;

		} catch (Exception e1) {
			if (e1 instanceof IOException) {
				// TODO Auto-generated catch block
//				e1.printStackTrace();
				System.out.println("Socket Error : No connect to Internet.");
			}
		}

		// list string of all courses
		System.out.println("======================================");
		try {
			for (int i = 0; i < listTextCourseFilterOne.size(); i++) {
				String[] arrStringMain = listTextCourseFilterOne.get(i).split("\\*");
				int index = 0;
				// handle courseName
				String courseName = "";
				for (int j = 0; j < arrStringMain.length; j++) {
					Matcher matcher = patternNumber.matcher(arrStringMain[j]);
					Matcher matcherOneDigit = patternOneDigit.matcher(arrStringMain[j]);
					if (matcher.matches() && !matcherOneDigit.matches()) {
						listCourseName.add(courseName.substring(0, courseName.length() - 1));
						courseName = "";
						index = j;
						break;
					}
					courseName += arrStringMain[j] + " ";
				}

				// handle dayOfWeek
				String daysOfWeek = "";
				boolean findCheck = false;

				for (int j = index + 4; j < arrStringMain.length; j++) {
					Matcher matcher = patternUnicodeChar.matcher(arrStringMain[j]);
					Matcher matcherNumber = patternNumber.matcher(arrStringMain[j]);
					Matcher matcherDSSV = patternDSSV.matcher(arrStringMain[j]);
					Matcher matcherDV = patternDV.matcher(arrStringMain[j]);
					if (matcher.matches() && daysOfWeek.equals("")) {
						daysOfWeek += arrStringMain[j];
						index = j + 1;
					} else {
						if (matcher.matches() && !daysOfWeek.equals("")) {
							daysOfWeek += ", " + arrStringMain[j];
							findCheck = true;
						}
						if (matcherNumber.matches() && findCheck) {
							index = j;
							break;
						}
					}

				}
				if (!daysOfWeek.equals("")) {
					listDayOfWeek.add(daysOfWeek);
				}

				// handle shifts

				String shifts = "";
				for (int j = index; j < arrStringMain.length; j++) {
					Matcher matcher = patternNumber.matcher(arrStringMain[j]);
					if (matcher.matches() && shifts.equals("")) {
						shifts += arrStringMain[j];
					} else {
						if (matcher.matches() && !shifts.equals("")) {
							shifts += ", " + arrStringMain[j];
						} else {
							index = j;
							break;
						}
					}

				}
				listShift.add(shifts);

				// handle locations

				String locations = "";
				for (int j = index; j < arrStringMain.length; j++) {
					Matcher matcher = patternText.matcher(arrStringMain[j]);
					if (matcher.matches() && locations.equals("")) {
						locations += arrStringMain[j];
					} else {
						if (matcher.matches() && !locations.equals("")) {
							locations += ", " + arrStringMain[j];
						} else {
							index = j;
							break;
						}
					}

				}
				listLocation.add(locations);

				// handle weeks
				String weeks = "";
				for (int j = index; j < arrStringMain.length; j++) {
					Matcher matcher = patternDSSV.matcher(arrStringMain[j]);
					if (!matcher.matches() && weeks.equals("")) {
						weeks += arrStringMain[j];
					} else {
						if (!matcher.matches() && !weeks.equals("")) {
							weeks += ", " + arrStringMain[j];
						} else {
							break;
						}
					}

				}
				listWeek.add(weeks);
			}
		} catch (Exception ex) {
			if (ex instanceof IOException) {
				// TODO Auto-generated catch block
//				e1.printStackTrace();
				System.out.println("Socket Error : No connect to Internet.");
			}
		}

		// Find final week
		int finalIndexWeek = 0, subFinalIndexWeek = 0;
		for (int k = 0; k < listWeek.size(); k++) {
			String week = listWeek.get(k);
			if (week.contains(",")) {
				String[] weekList = week.split(", ");
				for (int l = 0; l < weekList.length; l++) {
					String weeek = weekList[l];

					// check every char
					for (int t = 0; t < weeek.length(); t++) {
						char c = weeek.charAt(t);
						if (c == '-') {
							// mean not
						} else {
							subFinalIndexWeek = t + 1;
							if (subFinalIndexWeek > finalIndexWeek) {
								finalIndexWeek = subFinalIndexWeek;

							}
						}
					}
				}
			} else {

				for (int t = 0; t < week.length(); t++) {
					char c = week.charAt(t);
					if (c == '-') {
						// mean not
					} else {
						subFinalIndexWeek = t + 1;

						if (subFinalIndexWeek > finalIndexWeek) {
							finalIndexWeek = subFinalIndexWeek;
						}
					}

				}
			}
		}

		System.out.println("-------------------------FINAL WEEK: " + finalIndexWeek);
		// Handle Tuan obj
		Date stDate, chosenDate;
		Calendar calendar = Calendar.getInstance();
		String weekCode = "", dateS = startDate, dateE = "", engDateS = "", engDateE = "";
		try {
			stDate = new SimpleDateFormat("dd/MM/yyyy").parse(startDate);
			calendar.setTime(stDate);
			calendar.add(Calendar.DAY_OF_MONTH, 7);
			chosenDate = calendar.getTime();
			dateE = new SimpleDateFormat("dd/MM/yyyy").format(chosenDate);
			engDateS = dateS;
			engDateE = dateE;

			for (int q = 1; q < finalIndexWeek + 1; q++) {
				if (q < 10) {
					weekCode = "0" + q;
				} else {
					weekCode = "" + q;
				}
				Tuan week = new Tuan(weekCode, engDateS, engDateE);

				listTuan.add(week);
				stDate = new SimpleDateFormat("dd/MM/yyyy").parse(dateE);
				calendar.setTime(stDate);
				calendar.add(Calendar.DAY_OF_MONTH, 7);
				chosenDate = calendar.getTime();

				dateS = new SimpleDateFormat("dd/MM/yyyy").format(stDate);
				dateE = new SimpleDateFormat("dd/MM/yyyy").format(chosenDate);

				// convert to English date
				engDateS = dateS;
				engDateE = dateE;
			}

		} catch (Exception e1) {
			if (e1 instanceof IOException) {
				// TODO Auto-generated catch block
//				e1.printStackTrace();
				System.out.println("Socket Error : No connect to Internet.");
			}
		}

		// Handle maMonHoc error with *
		for (int k = 0; k < listCourseCode.size(); k++) {
			String courseCode = listCourseCode.get(k);
			if (courseCode.charAt(courseCode.length() - 1) == '*') {
				StringBuilder sb = new StringBuilder(courseCode);
				sb.deleteCharAt(sb.length() - 1);
				getListFineCourseCode().add(sb.toString());
			} else {
				getListFineCourseCode().add(courseCode);
			}
		}

		// confirm listTuan
//		for(int i=0;i<listTuan.size();i++){
//			System.out.println(listTuan.get(i).toString());
//		}

		// Add all MonHoc to every Tuan on listTuan
		for (int k = 0; k < listWeek.size(); k++) {
			String week = listWeek.get(k).trim();
			String[] weekList = week.split(", ");
			if (week.contains(",")) {
				weekList = week.split(", ");
				for (int l = 0; l < weekList.length; l++) {
					String weeek = weekList[l];

					// check every char
					for (int t = 0; t < weeek.length(); t++) {
						char c = weeek.charAt(t);
						if (c == '-') {
							// mean not
						} else {
							Tuan weekk = listTuan.get(t);

							// Before add, handle DOW, locations and shifts for each course (each week)
							String[] dowsSplit = listDayOfWeek.get(k).split(", "),
									locationsSplit = listLocation.get(k).split(", "),
									shiftsSplit = listShift.get(k).split(", ");

//							for(int i=0;i<dowsSplit.length;i++) {
//								System.out.print("( "+dowsSplit[i]+" ");
//							}
//							
//							for(int i=0;i<locationsSplit.length;i++) {
//								System.out.print("> "+locationsSplit[i]+" ");
//							}
//							
//							for(int i=0;i<shiftsSplit.length;i++) {
//								System.out.print("< "+shiftsSplit[i]+" ");
//							}
//								
//							for(int i=0;i<getListFineCourseCode().size();i++) {
//								System.out.print("+ "+getListFineCourseCode().get(i).toString()+" ");
//							}

							addMonHoc(weekk, getListFineCourseCode().get(k), listCourseName.get(k),
									shiftsSplit[l] + ", " + shiftsSplit[l + locationsSplit.length], locationsSplit[l],
									dowsSplit[l], "", "", "", "", "");
						}
					}
				}
			} else {
				for (int t = 0; t < week.length(); t++) {
					char c = week.charAt(t);
					if (c == '-') {
						// mean not
					} else {
						Tuan weekg = listTuan.get(t);

						// Before add, handle DOW, locations and shifts for each course (each week)
						String[] dowsSplit = listDayOfWeek.get(k).split(", "),
								locationsSplit = listLocation.get(k).split(", "),
								shiftsSplit = listShift.get(k).split(", ");

						addMonHoc(weekg, getListFineCourseCode().get(k), listCourseName.get(k), listShift.get(k),
								listLocation.get(k), listDayOfWeek.get(k), "", "", "", "", "");
					}

				}
			}
		}

//		for (int e = 0; e < listTuan.size(); e++) {
//			Tuan week = listTuan.get(e);
//			System.out.println(week.toString() + "\n----------------------------");
//		}

		// Handle Shifts, Locations and DOWs
		int countCourseWeek = 0, countCoursePerDay = 0;
		for (int e = 0; e < listTuan.size(); e++) {
			Tuan week = listTuan.get(e);
			for (int f = 0; f < week.getDsMonHoc().size(); f++) {
				countCourseWeek++;
				String courseName = week.getDsMonHoc().get(f).getTenMonHoc(),
						shifts = week.getDsMonHoc().get(f).getTietBatDau(), date = "", time = "", engDOW = "";
				// classify DOWs firstly (sort to standard from Mon to Fri)
				String[] shiftsSplitList = shifts.split(", "), engDOWList = new String[week.getDsMonHoc().size()],
						endShiftSplitList = new String[engDOWList.length],
						timeChosenList = new String[engDOWList.length],
						everyChosenDateList = new String[engDOWList.length];

				// define english day of week split list
				for (int q = 0; q < engDOWList.length; q++) {
					engDOWList[q] = dayOfWeekFromVietToEng(week.getDsMonHoc().get(f).getNgayThu());
				}

				// handle every chosen date list
				for (int q = 0; q < everyChosenDateList.length; q++) {
					everyChosenDateList[q] = convertChosenDate(week.getMaTuan(), startDate, engDOWList[q]);
				}

				// define End Shift List
				for (int q = 0; q < endShiftSplitList.length; q++) {
					endShiftSplitList[q] = ""
							+ (Integer.parseInt(shiftsSplitList[0]) + Integer.parseInt(shiftsSplitList[1]) - 1);
				}

				// define time list
				for (int q = 0; q < timeChosenList.length; q++) {
					int stSh = Integer.parseInt(shiftsSplitList[0]) - 1,
							enSh = Integer.parseInt(endShiftSplitList[q]) - 1;
					timeChosenList[q] = START_SHIFT_ARRAY[stSh] + " - " + END_SHIFT_ARRAY[enSh] + " ("
							+ shiftsSplitList[0] + "-" + endShiftSplitList[q] + ")";
					date = everyChosenDateList[q];
					time = timeChosenList[q];
					engDOW = engDOWList[q];

					// check the current date
					if (currentDateCheck.trim().equals(date.trim())) {
						countCoursePerDay++;
					} else {

					}

					week.getDsMonHoc().get(f).setStSh(START_SHIFT_ARRAY[stSh]);
					week.getDsMonHoc().get(f).setEnSh(END_SHIFT_ARRAY[enSh]);
					week.getDsMonHoc().get(f).setShNu(shiftsSplitList[0] + " - " + endShiftSplitList[q] + ": ");
					week.getDsMonHoc().get(f).setNgayHoc(date);
					week.getDsMonHoc().get(f).setThoiGianHoc(time);
					week.getDsMonHoc().get(f).setNgayThu(engDOW);

					// bonus
					int ckComma = week.getDsMonHoc().get(f).getTietBatDau().indexOf(",", 0);
					if (ckComma > 0) {
						week.getDsMonHoc().get(f)
								.setTietBatDau(week.getDsMonHoc().get(f).getTietBatDau().substring(0, ckComma));
					}
				}
			}
			convertLengthListCourseOfCurrentDOW(countCoursePerDay);
		}

		for (int e = 0; e < listTuan.size(); e++) {
			Tuan week = listTuan.get(e);
			System.out.println(week.toString() + "\n----------------------------");
		}

		// Sort up Mon - Sun and via shifts
//		// Task
//		Task<Void> sortUpShiftsAndDOWs = new Task<Void>() {
//
//			@Override
//			protected Void call() throws Exception {
//				Platform.runLater(new Runnable() {
//
//					@Override
//					public void run() {
//						sortUpShifts();
//					}
//				});
//				return null;
//			}
//		};
//
//		Thread hardWork = new Thread(sortUpShiftsAndDOWs);
//		hardWork.setDaemon(true);
//		hardWork.start();

		// Sort up via shifts (start shifts) and day of weeks
		sortUpCoursesViaDOWAndStartTime();

		// init hashTable
		Hashtable<Tuan, MonHoc> hashTable = new Hashtable<Tuan, MonHoc>();

		// add elements to Hash Table
		for (int e = 0; e < listTuan.size(); e++) {
			Tuan week = listTuan.get(e);
			for (int f = 0; f < week.getDsMonHoc().size(); f++) {
				hashTable.put(week, week.getDsMonHoc().get(f));
			}
		}
//		
//		 confirm object is fine - searched

		// define List Courses Each Week
		for (int e = 0; e < listTuan.size(); e++) {
			Tuan week = listTuan.get(e);

			// print out to check
//			System.out.println(""+week.toString());

			String weekCodde = Integer.parseInt(week.getMaTuan()) + "";
			// check if current study week
			if (weekCodde.equals(studyWeek + "")) {
				listCourseCurrentWeek = week.getDsMonHoc();

				// set txtCurrentWeek
				txtCurrentWeek = "Week " + weekCodde + " - " + week.getStartDate() + " to " + week.getEndDate();

				// handle indexFocusCurrentStart
				for (int f = 0; f < listCourseCurrentWeek.size(); f++) {
					MonHoc course = listCourseCurrentWeek.get(f);
					if (course.getNgayHoc().equals(currentDateNote)) {
						indexFocusCurrentDayStart = f;
						break;
					}
				}

				// handle indexFocusCurrentDayEnd
				for (int f = 0; f < listCourseCurrentWeek.size(); f++) {
					MonHoc course = listCourseCurrentWeek.get(f);
					if (course.getNgayHoc().equals(currentDateNote)) {
						indexFocusCurrentDayEnd = f;
					}
				}

			} else {
				// the week bb, the week b, the week current, the week a, the week aa
				String weekBBCode = (studyWeek - 2) + "", weekBCode = (studyWeek - 1) + "",
						weekACode = (studyWeek + 1) + "", weekAACode = (studyWeek + 2) + "";
				if (weekCodde.equals(weekBBCode)) {
					listCourseTheWeekBY = week.getDsMonHoc();
					txtTheWeekBB = ("Week " + weekCodde + " - " + week.getStartDate() + " to " + week.getEndDate());
				} else if (weekCodde.equals(weekBCode)) {
					listCourseYest = week.getDsMonHoc();
					txtTheWeekB = ("Week " + weekCodde + " - " + week.getStartDate() + " to " + week.getEndDate());
				} else if (weekCodde.equals(weekACode)) {
					listCourseTomo = week.getDsMonHoc();
					txtTheWeekA = ("Week " + weekCodde + " - " + week.getStartDate() + " to " + week.getEndDate());
				} else if (weekCodde.equals(weekAACode)) {
					listCourseTheWeekAT = week.getDsMonHoc();
					txtTheWeekAA = ("Week " + weekCodde + " - " + week.getStartDate() + " to " + week.getEndDate());
				}

			}

		}

		// update week 1 schedule
		updateListCourseChosenWeek(listTuan);

//		setListTuan(listTuan);

		// handle JSON object and store it in Android home system : "inputCode + .json"
		Gson gsonLib = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
//		String jsonString = gsonLib.toJson(listTuan);

		// find root directory for Android and local Desktop
		String rootDirDesktop = System.getProperty("user.dir"), rootDirAndroid = ""
//						+Environment.getRootDirectory().getAbsolutePath().toString()
		;

		String fileNameA =
//				rootDirAndroid+
				inputCode + ".json", fileNameD = inputCode + ".json", jsonStringCharset = "";

		// Using GluonConnectFileProvider => Gluon OSS
		// (https://bitbucket.org/gluon-oss/samples/src/69df8ee0a76500669427bac7e801e350da5193c0/GluonConnectFileProvider/src/main/java/com/gluonhq/samples/connect/file/Main.java)

		// overwrite JSON file
		byte[] byteJsonString = gsonLib.toJson(listTuan).getBytes(StandardCharsets.UTF_8);
		String jsonStringCharset_NOS = new String(byteJsonString, StandardCharsets.UTF_8),
				jsonStringCharset_NOS_Desktop = new String(byteJsonString, StandardCharsets.UTF_8);

		System.out.println("_______--------" + jsonStringCharset_NOS.substring(0, 100));

		try {
			if (currentDevice.equals("Desktop")) {
				// Desktop
				File publicJSON_NOS_De = new File(System.getProperty("user.dir") + "/" + inputCode + "_nos.json");
				if (!publicJSON_NOS_De.exists() && !jsonStringCharset_NOS_Desktop.isEmpty()) {
					Writer out = new BufferedWriter(
							new OutputStreamWriter(new FileOutputStream(publicJSON_NOS_De), "UTF-8"));
					try {
						out.write(jsonStringCharset_NOS_Desktop);
					} finally {
						out.close();
					}
				} else {
					Writer out = new BufferedWriter(
							new OutputStreamWriter(new FileOutputStream(publicJSON_NOS_De), "UTF-8"));
					try {
						out.write(jsonStringCharset_NOS_Desktop + ". Juss in case.");
					} finally {
						out.close();
					}
				}

			} else {
				// Android
				File publicJSON_NOS = new File(Main.publicRootDirAndroid + "/" + inputCode + "_nos.json");
				if (!publicJSON_NOS.exists() && !jsonStringCharset_NOS.isEmpty()) {
					Writer out = new BufferedWriter(
							new OutputStreamWriter(new FileOutputStream(publicJSON_NOS), "UTF-8"));
					try {
						out.write(jsonStringCharset_NOS);
					} finally {
						out.close();
					}
				} else {
					Writer out = new BufferedWriter(
							new OutputStreamWriter(new FileOutputStream(publicJSON_NOS), "UTF-8"));
					try {
						out.write(jsonStringCharset_NOS + ". Just in case.");
					} finally {
						out.close();
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// If CONFIRM_WRITE_FILE_FIRST is true

//			try (FileOutputStream fos = new FileOutputStream(Main.publicRootDirAndroid + "/" + fileNameA);
//					OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {
//				byte[] byteJsonString = gsonLib.toJson(listTuan).getBytes(StandardCharsets.UTF_8);
//				jsonStringCharset = new String(byteJsonString, StandardCharsets.UTF_8);
//				gsonLib.toJson(listTuan, osw);

		// confirm
//			System.out.println(jsonStringCharset);

//			} catch (Exception e) {
//				if (e instanceof IOException) {
		// TODO Auto-generated catch block
//				e1.printStackTrace();
//					System.out.println("Socket Error : No connect to Internet.");
//				}
//			}

		// private Storage Access
//			try {
//				File languagesFile = new File(Main.ROOT_DIR_ANDROID + "", fileNameA);
//				if (!languagesFile.exists() && !jsonStringCharset.isEmpty()) {
//					try (FileWriter writer = new FileWriter(languagesFile)) {
//						writer.write(jsonStringCharset);
//					Toast toastMessage = new Toast("WTF there are.", Toast.LENGTH_LONG);
//					toastMessage.show();
//					}
//				} else {
//					try (FileWriter writer = new FileWriter(languagesFile)) {
//						writer.write(jsonStringCharset + ". Just in case");
//					Toast toastMessage = new Toast("WTF there are not.", Toast.LENGTH_LONG);
//					toastMessage.show();
//					}
//				}
//			Toast toastMessage = new Toast(">>>>>>>>>.", Toast.LENGTH_LONG);
//			toastMessage.show();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}

//		try (FileOutputStream fos = new FileOutputStream(fileNameD);
//				OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {
////			byte[] byteJsonString = gsonLib.toJson(listTuan).getBytes(StandardCharsets.UTF_8);
////			String jsonStringCharset = new String(byteJsonString, StandardCharsets.UTF_8);
////			System.out.println(jsonStringCharset);
//			gsonLib.toJson(listTuan, osw);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
////		System.out.println(jsonString+" DONEE");

//		User user = gsonLib.fromJson(jsonString, User.class);
//		json = gson.fromJson(new InputStreamReader(is,"UTF-8"), parseType);
		// User object
//		User user = new User(inputCode, jsonString);

		// create a FileClient to the specified File
//		FileClient fileClient = FileClient.create(new File(inputCode+".json"));
//
		// create a JSON converter that converts the user object into a JSON object
//		OutputStreamOutputConverter<User> outputConverter = new JsonOutputConverter<>(User.class);
////
////		// store an object with an ObjectDataWriter created from the FileClient
//		GluonObservableObject<User> gluonUser = DataProvider.storeObject(user, fileClient.createObjectDataWriter(outputConverter));

//		String rootDir = System.getProperty("user.dir");
//		File root = Environment.getExternalStorageDirectory();
//		System.out.println("ROOOT: "+rootDir.replace("\\", "/"));
//		
//		try (OutputStream fos = Files.newOutputStream(Paths.get(rootDir+"/"+inputCode+".json"), StandardOpenOption.CREATE, StandardOpenOption.CREATE);
//				ObjectOutputStream oos = new ObjectOutputStream(fos);){
//			User user = new User(inputCode, jsonString);
//			oos.writeObject(user);
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
		return "";
	}

	// =================================FINAL====================EXAM==================================
	// ================================================================================================
	public String handleJsoupAndJSONFinalExam(boolean isNormal) throws IndexOutOfBoundsException {
		String defaultUrl = "";
		if (isNormal == true) {
			defaultUrl = URL_FINAL_EXAM;
		} else {
			defaultUrl = URL_FINAL_EXAM_SDH;
		}

		// handle JSoup
		try {
//			doc = Jsoup.connect(ROOT_URL + inputCode).timeout(5000).get();
			docFinalExam = Jsoup.connect(defaultUrl + inputCode).timeout(5000).get();
		} catch (Exception e1) {
			if (e1 instanceof IOException) {
				return "NO_INTERNET_CONNECTION";
			}
		}

		// Handle download HTML file and store it in drive E:/

		// Create URL object
//			URL url = new URl(ROOT_URL + STUDENT_CODE);
//			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
		//
//			// Enter filename in which you want to download
//			BufferedWriter writer = new BufferedWriter(new FileWriter(DOWNLOAD_URL+"Schedule-Student-"+STUDENT_CODE+".html"));
		//
//			// read each line from stream till end
//			String line;
//			while ((line = readr.readLine()) != null) {
//				writer.write(line);
//				System.out.println(line);
//			}
		//
//			readr.close();
//			writer.close();
//			System.out.println("Successfully Downloaded.");

		// Handle the content

		// Title
//		String title = doc.title();
//		System.out.println("Title: " + title);

		// Note

//		Elements labels = doc.select("span.Label"), labelsFinalExam = docFinalExam.select("span.Label");
//		int count = 0;

//		for (Element el : labelsFinalExam) {
//			count++;
////			System.out.println(el.toString());
//			if (count == 12) {
//				txtNote = "Lưu ý: " + el.text().substring(8, el.text().length() - 1);
//				startDate = txtNote.substring(txtNote.length() - 10, txtNote.length());
////				System.out.println(startDate);
////				System.out.println(txtNote);
//			} else if (count == 14) {
//				txtUpdate = "Thơi điểm gân nhất dữ liêu đươc câp nhât: "
//						+ el.text().substring(2, el.text().length() - 1);
//				recentUpdateDate = txtUpdate.substring(txtUpdate.length() - 10, txtUpdate.length());
//				
//			}
//		}

		// Validation

//		System.out.println("Ngày bắt đầu tuần học đầu: " + startDate + "\n, vÃ  ngÃ y cáº­p nháº­t gáº§n Ä‘Ã¢y nháº¥t: "
//				+ recentUpdateDate + ".");

		// init spanID
		String spanID = "";

		// Table
		Elements table = docFinalExam.select("span.Label");
		int count = 0, note = 2;

//		// Reg - ex
//		Pattern patternNumber = Pattern.compile("[0-9]+"), patternUnicodeChar = Pattern.compile("\\p{L}+"),
//				patternText = Pattern.compile("((?=.\\d*)(?=.*[A-Z])(?=[_]*).{1,20})"),
//				patternOneDigit = Pattern.compile("[\\d]{1}"), patternDSSV = Pattern.compile("[DSSV]+"),
//				patternDV = Pattern.compile("[^DV]");

		for (Element elem : table) {
//			String content = elem.text().trim();
//			System.out.println(content);
			if (note <= 9) {
				spanID = SPAN_ID_LESS;
			} else {
				spanID = SPAN_ID_GREATER;
			}

			Elements eleMaMonHoc = docFinalExam.select(spanID + note + "_lblMaMonHoc.Label"),
					eleTenMonHoc = docFinalExam.select(spanID + note + "_lblTenMonHoc.Label"),
					eleGhepThi = docFinalExam.select(spanID + note + "_lblGhepThi.Label"),
					eleToThi = docFinalExam.select(spanID + note + "_lblToThi.Label"),
					eleSoLuong = docFinalExam.select(spanID + note + "_lblSoLuong.Label"),
					eleNgayThi = docFinalExam.select(spanID + note + "_lblNgayThi.Label"),
					eleTietBatDau = docFinalExam.select(spanID + note + "_lblTietBD.Label"),
					eleSoTiet = docFinalExam.select(spanID + note + "_lblSoTiet.Label"),
					elePhong = docFinalExam.select(spanID + note + "_lblTenPhong.Label");
//			System.out.println(eleTenMonHoc.text()+" - "+eleGhepThi.text()+" - "+eleToThi.text()+" - "+eleSoLuong.text()+" - "+eleNgayThi.text()+" - "+elePhong.text()+" - "+eleTietBatDau.text()+" - "+eleSoTiet.text());
			if (!eleMaMonHoc.text().equals("") && !eleTenMonHoc.text().equals("") && !eleGhepThi.text().equals("")
					&& !eleSoLuong.text().equals("") && !eleToThi.text().equals("") && !eleNgayThi.text().equals("")
					&& !eleTietBatDau.text().equals("") && !eleSoTiet.text().equals("")
					&& !elePhong.text().equals("")) {
				String contentMaMonHoc = eleMaMonHoc.text().toString().trim(),
						contentTenMonHoc = eleTenMonHoc.text().toString(),
						contentGhepThi = eleGhepThi.text().toString().trim(),
						contentSoLuong = eleSoLuong.text().toString().trim(),
						contentToThi = eleToThi.text().toString().trim(),
						contentNgayThi = eleNgayThi.text().toString().trim(),
						contentTietBatDau = eleTietBatDau.text().toString().trim(),
						contentSoTiet = eleSoTiet.text().toString().trim(),
						contentPhong = elePhong.text().toString().trim();

				// handle Tiet like "Tiết 1 - 2"
				String tietThi = "" + contentTietBatDau + " - "
						+ (int) (Integer.parseInt(contentTietBatDau) + Integer.parseInt(contentSoTiet) - 1);

				// handle Thoi gian like "7h00 - 9h55
				String thoiGianThi = START_SHIFT_ARRAY[Integer.parseInt(contentTietBatDau) - 1] + " - "
						+ END_SHIFT_ARRAY[(int) (Integer.parseInt(contentTietBatDau) + Integer.parseInt(contentSoTiet)
								- 1) - 1];

				// handle DOW like "THỨ HAI"
				String ngayThu = "";
				Calendar c = Calendar.getInstance();
				Date stDate;
				try {
					stDate = new SimpleDateFormat("dd/MM/yyyy").parse(contentNgayThi);
					c.setTime(stDate);
					ngayThu = convertDOWEngToViet(strDays[c.get(Calendar.DAY_OF_WEEK) - 1]);
				} catch (Exception e) {
					e.printStackTrace();
				}

				// init MonThi
				MonThi monThi = new MonThi(contentTenMonHoc, contentGhepThi, contentToThi, contentSoLuong,
						contentNgayThi, ngayThu, contentPhong, thoiGianThi, tietThi);
				listMonThi.add(monThi);
//				
//				System.out.println("Môn học thứ "+(note-1)+": "
//						+monThi.toString());

			} else {
//				System.out.println("---------------"
//						+ "Tên môn học: "+eleTenMonHoc+"\n"
//						+"Ghép thi: "+eleTenMonHoc+"\n"
//						+"Tổ thi: "+eleTenMonHoc+"\n"
//						+"Số lượng: "+eleTenMonHoc+"\n"
//						+"Ngày thi: "+eleTenMonHoc+"\n"
//						+"Phòng: "+eleTenMonHoc+"\n"
//						+"Tiết bắt đầu: "+eleTenMonHoc+" - Số tiết: "+eleSoTiet+"\n"
//						);
			}
			note++;

		}

		// handle JSON object and store it in Android home system : "inputCode + .json"
		Gson gsonLib = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
//				String jsonString = gsonLib.toJson(listTuan);

		// find root directory for Android and local Desktop
		String rootDirDesktop = System.getProperty("user.dir"), rootDirAndroid = ""
//								+Environment.getRootDirectory().getAbsolutePath().toString()
		;

		String fileNameAFE =
//						rootDirAndroid+
				inputCode + "_fe.json", fileNameD = inputCode + ".json", jsonStringCharset = "";

		// Using GluonConnectFileProvider => Gluon OSS
		// (https://bitbucket.org/gluon-oss/samples/src/69df8ee0a76500669427bac7e801e350da5193c0/GluonConnectFileProvider/src/main/java/com/gluonhq/samples/connect/file/Main.java)

		// Overwrite JSON file

		byte[] byteJsonString = gsonLib.toJson(listMonThi).getBytes(StandardCharsets.UTF_8);
		String jsonStringCharset_FES = new String(byteJsonString, StandardCharsets.UTF_8),
				jsonStringCharset_FES_Desktop = new String(byteJsonString, StandardCharsets.UTF_8);

		System.out.println("_______--------" + jsonStringCharset_FES.substring(0, 100));

//		StringBuilder sb_nos = new StringBuilder(), sb_fes = new StringBuilder();
//
//		for (MonThi mt : listMonThi) {
//			sb_fes.append(mt);
//			sb_fes.append("\n---------------\n");
//		}
//
//		System.out.println(sb_fes.toString().substring(0, 20));
//
//		// first way
//		byte[] bytes = sb_fes.toString().getBytes(StandardCharsets.UTF_8);
//
//		String utf8EncodedString = new String(bytes, StandardCharsets.UTF_8),
//				jsonStringCharset_FES = "" + utf8EncodedString, jsonStringCharset_FES_Desktop = "" + utf8EncodedString;
//
//		// second way
//		ByteBuffer buffer = StandardCharsets.UTF_8.encode(sb_fes.toString());
//
//		String utf8EncodedStringSecond = StandardCharsets.UTF_8.decode(buffer).toString();
//
//		System.out.println("Way 1: " + utf8EncodedString.substring(0, 20) + ", Way 2: "
//				+ utf8EncodedStringSecond.substring(0, 20));

		try {
			if (currentDevice.equals("Desktop")) {

				// Desktop
				File publicJSON_FES_De = new File(System.getProperty("user.dir") + "/" + inputCode + "_fes.json");
				if (!publicJSON_FES_De.exists() && !jsonStringCharset_FES_Desktop.isEmpty()) {
					Writer out = new BufferedWriter(
							new OutputStreamWriter(new FileOutputStream(publicJSON_FES_De), "UTF-8"));
					try {
						out.write(jsonStringCharset_FES_Desktop);
					} finally {
						out.close();
					}
				} else {
					Writer out = new BufferedWriter(
							new OutputStreamWriter(new FileOutputStream(publicJSON_FES_De), "UTF-8"));
					try {
						out.write(jsonStringCharset_FES_Desktop + ". Just in case.");
					} finally {
						out.close();
					}
				}
			} else {

				// Android
				File publicJSON_FES = new File(Main.publicRootDirAndroid + "/" + inputCode + "_fes.json");
				if (!publicJSON_FES.exists() && !jsonStringCharset_FES.isEmpty()) {
					Writer out = new BufferedWriter(
							new OutputStreamWriter(new FileOutputStream(publicJSON_FES), "UTF-8"));
					try {
						out.write(jsonStringCharset_FES);
					} finally {
						out.close();
					}
				} else {
					Writer out = new BufferedWriter(
							new OutputStreamWriter(new FileOutputStream(publicJSON_FES), "UTF-8"));
					try {
						out.write(jsonStringCharset_FES + ". Just in case.");
					} finally {
						out.close();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Find the quantity of "DSSV" words
//		int maxAppearanceQuantity = 0, checkC = 0;
//		String subStringCheck = "DSSV";
//		for (int e = 0; e < listTextCourseFilter.size(); e++) {
//			String st = listTextCourseFilter.get(e);
//			while ((checkC = st.indexOf(subStringCheck, checkC)) != -1) {
//				maxAppearanceQuantity++;
//				checkC += subStringCheck.length();
//			}
//		}
//
//		// Handle "DSSV" clone into double, triple or etc.
//		int countChar = 0;
//		boolean flagCheck = false;
//		String stringClone = "", theLastFourChar;
//		for (int e = 0; e < listTextCourseFilter.size(); e++) {
//			stringClone = listTextCourseFilter.get(e);
//			for (int f = 0; f < maxAppearanceQuantity; f++) {
//				theLastFourChar = stringClone.substring(stringClone.length() - 4, stringClone.length());
//				if (theLastFourChar.equals("DSSV")) {
//					StringBuilder sb = new StringBuilder(stringClone);
//					stringClone = sb.delete(sb.length() - 5, sb.length()).toString();
//				} else {
//					// check duplicate
//					if (listTextCourseFilterOne.size() == 0 && countChar < maxAppearanceQuantity) {
//						listTextCourseFilterOne.add(stringClone);
//						flagCheck = false;
//						countChar++;
//					} else {
//						for (int d = 0; d < listTextCourseFilterOne.size(); d++) {
//							if (stringClone.equals(listTextCourseFilterOne.get(d))) {
//								// do nothing
//							} else {
//								flagCheck = true;
//							}
//						}
//						if (flagCheck == true && countChar < maxAppearanceQuantity && countChar == e) {
//							listTextCourseFilterOne.add(stringClone);
//							flagCheck = false;
//							countChar++;
//						}
//					}
//				}
//			}
//		}

		// handle object stored

		// Step 1: handle Start Date and kick off to count
//		try {
//			int currentDateOfYear = CURRENT_DATE.getDayOfYear(), koDateOfYear = dayOfYear(startDate),
//					minusDateOfYear = 0, currentWeekOfYear = 0, currentWeekOfMonth = 0;
//
//			if (CURRENT_DATE.getYear() == Integer
//					.parseInt(startDate.substring(startDate.length() - 4, startDate.length()))) {
//				minusDateOfYear = currentDateOfYear - koDateOfYear + 1;
//				currentWeekOfYear = minusDateOfYear / 7 + 1;
//				currentWeekOfMonth = currentWeekOfYear % 4;
//			} else {
//				// check leap year
//				boolean isLeap = false;
//				int allDays = 365;
//
//				if (CURRENT_DATE.getYear() % 4 == 0) {
//					if (CURRENT_DATE.getYear() % 100 == 0) {
//						if (CURRENT_DATE.getYear() % 400 == 0)
//							isLeap = true;
//						else
//							isLeap = false;
//					} else
//						isLeap = true;
//				} else {
//					isLeap = false;
//				}
//
//				if (isLeap) {
//					allDays++;
//				}
//
//				int dayLastYear = allDays - koDateOfYear + 1 + currentDateOfYear;
//				currentWeekOfYear = dayLastYear / 7 + 1;
//				currentWeekOfMonth = currentWeekOfYear % 4;
//			}
//			// Handle current study week
//			studyWeek = currentWeekOfYear;
//
//		} catch (Exception e1) {
//			if (e1 instanceof IOException) {
//				// TODO Auto-generated catch block
////				e1.printStackTrace();
//				System.out.println("Socket Error : No connect to Internet.");
//			} 
//		}

		// list string of all courses
		System.out.println("======================================");
//		try {
//			for (int i = 0; i < listTextCourseFilterOne.size(); i++) {
//				String[] arrStringMain = listTextCourseFilterOne.get(i).split("\\*");
//				int index = 0;
//				// handle courseName
//				String courseName = "";
//				for (int j = 0; j < arrStringMain.length; j++) {
//					Matcher matcher = patternNumber.matcher(arrStringMain[j]);
//					Matcher matcherOneDigit = patternOneDigit.matcher(arrStringMain[j]);
//					if (matcher.matches() && !matcherOneDigit.matches()) {
//						listCourseName.add(courseName.substring(0, courseName.length() - 1));
//						courseName = "";
//						index = j;
//						break;
//					}
//					courseName += arrStringMain[j] + " ";
//				}
//
//				// handle dayOfWeek
//				String daysOfWeek = "";
//				boolean findCheck = false;
//
//				for (int j = index + 4; j < arrStringMain.length; j++) {
//					Matcher matcher = patternUnicodeChar.matcher(arrStringMain[j]);
//					Matcher matcherNumber = patternNumber.matcher(arrStringMain[j]);
//					Matcher matcherDSSV = patternDSSV.matcher(arrStringMain[j]);
//					Matcher matcherDV = patternDV.matcher(arrStringMain[j]);
//					if (matcher.matches() && daysOfWeek.equals("")) {
//						daysOfWeek += arrStringMain[j];
//						index = j + 1;
//					} else {
//						if (matcher.matches() && !daysOfWeek.equals("")) {
//							daysOfWeek += ", " + arrStringMain[j];
//							findCheck = true;
//						}
//						if (matcherNumber.matches() && findCheck) {
//							index = j;
//							break;
//						}
//					}
//
//				}
//				if (!daysOfWeek.equals("")) {
//					listDayOfWeek.add(daysOfWeek);
//				}
//
//				// handle shifts
//
//				String shifts = "";
//				for (int j = index; j < arrStringMain.length; j++) {
//					Matcher matcher = patternNumber.matcher(arrStringMain[j]);
//					if (matcher.matches() && shifts.equals("")) {
//						shifts += arrStringMain[j];
//					} else {
//						if (matcher.matches() && !shifts.equals("")) {
//							shifts += ", " + arrStringMain[j];
//						} else {
//							index = j;
//							break;
//						}
//					}
//
//				}
//				listShift.add(shifts);
//
//				// handle locations
//
//				String locations = "";
//				for (int j = index; j < arrStringMain.length; j++) {
//					Matcher matcher = patternText.matcher(arrStringMain[j]);
//					if (matcher.matches() && locations.equals("")) {
//						locations += arrStringMain[j];
//					} else {
//						if (matcher.matches() && !locations.equals("")) {
//							locations += ", " + arrStringMain[j];
//						} else {
//							index = j;
//							break;
//						}
//					}
//
//				}
//				listLocation.add(locations);
//
//				// handle weeks
//				String weeks = "";
//				for (int j = index; j < arrStringMain.length; j++) {
//					Matcher matcher = patternDSSV.matcher(arrStringMain[j]);
//					if (!matcher.matches() && weeks.equals("")) {
//						weeks += arrStringMain[j];
//					} else {
//						if (!matcher.matches() && !weeks.equals("")) {
//							weeks += ", " + arrStringMain[j];
//						} else {
//							break;
//						}
//					}
//
//				}
//				listWeek.add(weeks);
//			}
//		} catch (Exception ex) {
//			if (ex instanceof IOException) {
//				// TODO Auto-generated catch block
////				e1.printStackTrace();
//				System.out.println("Socket Error : No connect to Internet.");
//			}
//		}

		// Find final week
//		int finalIndexWeek = 0, subFinalIndexWeek = 0;
//		for (int k = 0; k < listWeek.size(); k++) {
//			String week = listWeek.get(k);
//			if (week.contains(",")) {
//				String[] weekList = week.split(", ");
//				for (int l = 0; l < weekList.length; l++) {
//					String weeek = weekList[l];
//
//					// check every char
//					for (int t = 0; t < weeek.length(); t++) {
//						char c = weeek.charAt(t);
//						if (c == '-') {
//							// mean not
//						} else {
//							subFinalIndexWeek = t + 1;
//							if (subFinalIndexWeek > finalIndexWeek) {
//								finalIndexWeek = subFinalIndexWeek;
//
//							}
//						}
//					}
//				}
//			} else {
//
//				for (int t = 0; t < week.length(); t++) {
//					char c = week.charAt(t);
//					if (c == '-') {
//						// mean not
//					} else {
//						subFinalIndexWeek = t + 1;
//
//						if (subFinalIndexWeek > finalIndexWeek) {
//							finalIndexWeek = subFinalIndexWeek;
//						}
//					}
//
//				}
//			}
//		}

		// Handle Tuan obj
//		Date stDate, chosenDate;
//		Calendar calendar = Calendar.getInstance();
//		String weekCode = "", dateS = startDate, dateE = "", engDateS = "", engDateE = "";
//		try {
//			stDate = new SimpleDateFormat("dd/MM/yyyy").parse(startDate);
//			calendar.setTime(stDate);
//			calendar.add(Calendar.DAY_OF_MONTH, 7);
//			chosenDate = calendar.getTime();
//			dateE = new SimpleDateFormat("dd/MM/yyyy").format(chosenDate);
//			engDateS = convertDateWeek(dateS);
//			engDateE = convertDateWeek(dateE);
//
//			for (int q = 1; q < finalIndexWeek + 1; q++) {
//				if (q < 10) {
//					weekCode = "0" + q;
//				} else {
//					weekCode = "" + q;
//				}
//				Tuan week = new Tuan(weekCode, engDateS, engDateE);
//
//				listTuan.add(week);
//				stDate = new SimpleDateFormat("dd/MM/yyyy").parse(dateE);
//				calendar.setTime(stDate);
//				calendar.add(Calendar.DAY_OF_MONTH, 7);
//				chosenDate = calendar.getTime();
//
//				dateS = new SimpleDateFormat("dd/MM/yyyy").format(stDate);
//				dateE = new SimpleDateFormat("dd/MM/yyyy").format(chosenDate);
//
//				// convert to English date
//				engDateS = convertDateWeek(dateS);
//				engDateE = convertDateWeek(dateE);
//			}
//
//		} catch (Exception e1) {
//			if (e1 instanceof IOException) {
//				// TODO Auto-generated catch block
////				e1.printStackTrace();
//				System.out.println("Socket Error : No connect to Internet.");
//			} 
//		}

		// Handle maMonHoc error with *
//		for (int k = 0; k < listCourseCode.size(); k++) {
//			String courseCode = listCourseCode.get(k);
//			if (courseCode.charAt(courseCode.length() - 1) == '*') {
//				StringBuilder sb = new StringBuilder(courseCode);
//				sb.deleteCharAt(sb.length() - 1);
//				getListFineCourseCode().add(sb.toString());
//			} else {
//				getListFineCourseCode().add(courseCode);
//			}
//		}

		// Add all MonHoc to every Tuan on listTuan
//		for (int k = 0; k < listWeek.size(); k++) {
//			String week = listWeek.get(k).trim();
//			String[] weekList = week.split(", ");
//			if (week.contains(",")) {
//				weekList = week.split(", ");
//				for (int l = 0; l < weekList.length; l++) {
//					String weeek = weekList[l];
//
//					// check every char
//					for (int t = 0; t < weeek.length(); t++) {
//						char c = weeek.charAt(t);
//						if (c == '-') {
//							// mean not
//						} else {
//							Tuan weekk = listTuan.get(t);
//
//							// Before add, handle DOW, locations and shifts for each course (each week)
//							String[] dowsSplit = listDayOfWeek.get(k).split(", "),
//									locationsSplit = listLocation.get(k).split(", "),
//									shiftsSplit = listShift.get(k).split(", ");
//
//							addMonHoc(weekk, getListFineCourseCode().get(k), listCourseName.get(k),
//									shiftsSplit[l] + ", " + shiftsSplit[l + locationsSplit.length], locationsSplit[l],
//									dowsSplit[l], "", "", "", "", "");
//						}
//					}
//				}
//			} else {
//				for (int t = 0; t < week.length(); t++) {
//					char c = week.charAt(t);
//					if (c == '-') {
//						// mean not
//					} else {
//						Tuan weekg = listTuan.get(t);
//
//						// Before add, handle DOW, locations and shifts for each course (each week)
//						String[] dowsSplit = listDayOfWeek.get(k).split(", "),
//								locationsSplit = listLocation.get(k).split(", "),
//								shiftsSplit = listShift.get(k).split(", ");
//
//						addMonHoc(weekg, getListFineCourseCode().get(k), listCourseName.get(k), listShift.get(k),
//								listLocation.get(k), listDayOfWeek.get(k), "", "", "", "", "");
//					}
//
//				}
//			}
//		}

//		for (int e = 0; e < listTuan.size(); e++) {
//			Tuan week = listTuan.get(e);
//			System.out.println(week.toString() + "\n----------------------------");
//		}

		// Handle Shifts, Locations and DOWs
//		int countCourseWeek = 0, countCoursePerDay = 0;
//		for (int e = 0; e < listTuan.size(); e++) {
//			Tuan week = listTuan.get(e);
//			for (int f = 0; f < week.getDsMonHoc().size(); f++) {
//				countCourseWeek++;
//				String courseName = week.getDsMonHoc().get(f).getTenMonHoc(),
//						shifts = week.getDsMonHoc().get(f).getTietBatDau(), date = "", time = "", engDOW = "";
//				// classify DOWs firstly (sort to standard from Mon to Fri)
//				String[] shiftsSplitList = shifts.split(", "), engDOWList = new String[week.getDsMonHoc().size()],
//						endShiftSplitList = new String[engDOWList.length],
//						timeChosenList = new String[engDOWList.length],
//						everyChosenDateList = new String[engDOWList.length];
//
//				// define english day of week split list
//				for (int q = 0; q < engDOWList.length; q++) {
//					engDOWList[q] = dayOfWeekFromVietToEng(week.getDsMonHoc().get(f).getNgayThu());
//				}
//
//				// handle every chosen date list
//				for (int q = 0; q < everyChosenDateList.length; q++) {
//					everyChosenDateList[q] = convertChosenDate(week.getMaTuan(), startDate, engDOWList[q]);
//				}
//
//				// define End Shift List
//				for (int q = 0; q < endShiftSplitList.length; q++) {
//					endShiftSplitList[q] = ""
//							+ (Integer.parseInt(shiftsSplitList[0]) + Integer.parseInt(shiftsSplitList[1]) - 1);
//				}
//
//				// define time list
//				for (int q = 0; q < timeChosenList.length; q++) {
//					int stSh = Integer.parseInt(shiftsSplitList[0]) - 1,
//							enSh = Integer.parseInt(endShiftSplitList[q]) - 1;
//					timeChosenList[q] = START_SHIFT_ARRAY[stSh] + " - " + END_SHIFT_ARRAY[enSh] + " ("
//							+ shiftsSplitList[0] + "-" + endShiftSplitList[q] + ")";
//					date = everyChosenDateList[q];
//					time = timeChosenList[q];
//					engDOW = engDOWList[q];
//
//					// check the current date
//					if (currentDateCheck.trim().equals(date.trim())) {
//						countCoursePerDay++;
//					} else {
//
//					}
//
//					week.getDsMonHoc().get(f).setStSh(START_SHIFT_ARRAY[stSh]);
//					week.getDsMonHoc().get(f).setEnSh(END_SHIFT_ARRAY[enSh]);
//					week.getDsMonHoc().get(f).setShNu(shiftsSplitList[0] + " - " + endShiftSplitList[q] + ": ");
//					week.getDsMonHoc().get(f).setNgayHoc(date);
//					week.getDsMonHoc().get(f).setThoiGianHoc(time);
//					week.getDsMonHoc().get(f).setNgayThu(engDOW);
//
//					// bonus
//					int ckComma = week.getDsMonHoc().get(f).getTietBatDau().indexOf(",", 0);
//					if (ckComma > 0) {
//						week.getDsMonHoc().get(f)
//								.setTietBatDau(week.getDsMonHoc().get(f).getTietBatDau().substring(0, ckComma));
//					}
//				}
//			}
//			convertLengthListCourseOfCurrentDOW(countCoursePerDay);
//		}

////		for (int e = 0; e < listTuan.size(); e++) {
////			Tuan week = listTuan.get(e);
////			System.out.println(week.toString() + "\n----------------------------");
////		}

		// Sort up Mon - Sun and via shifts
//		// Task
//		Task<Void> sortUpShiftsAndDOWs = new Task<Void>() {
//
//			@Override
//			protected Void call() throws Exception {
//				Platform.runLater(new Runnable() {
//
//					@Override
//					public void run() {
//						sortUpShifts();
//					}
//				});
//				return null;
//			}
//		};
//
//		Thread hardWork = new Thread(sortUpShiftsAndDOWs);
//		hardWork.setDaemon(true);
//		hardWork.start();

		// Sort up via shifts (start shifts) and day of weeks
//		sortUpCoursesViaDOWAndStartTime();
//
//		// init hashTable
//		Hashtable<Tuan, MonHoc> hashTable = new Hashtable<Tuan, MonHoc>();
//
//		// add elements to Hash Table
//		for (int e = 0; e < listTuan.size(); e++) {
//			Tuan week = listTuan.get(e);
//			for (int f = 0; f < week.getDsMonHoc().size(); f++) {
//				hashTable.put(week, week.getDsMonHoc().get(f));
//			}
//		}
////		
////		 confirm object is fine - searched
//
//		// define List Courses Each Week
//		for (int e = 0; e < listTuan.size(); e++) {
//			Tuan week = listTuan.get(e);
//
//			// print out to check
////			System.out.println(""+week.toString());
//
//			String weekCodde = Integer.parseInt(week.getMaTuan()) + "";
//			// check if current study week
//			if (weekCodde.equals(studyWeek + "")) {
//				listCourseCurrentWeek = week.getDsMonHoc();
//
//				// set txtCurrentWeek
//				txtCurrentWeek = "Week " + weekCodde + " - " + week.getStartDate() + " to " + week.getEndDate();
//
//				// handle indexFocusCurrentStart
//				for (int f = 0; f < listCourseCurrentWeek.size(); f++) {
//					MonHoc course = listCourseCurrentWeek.get(f);
//					if (course.getNgayHoc().equals(currentDateNote)) {
//						indexFocusCurrentDayStart = f;
//						break;
//					}
//				}
//
//				// handle indexFocusCurrentDayEnd
//				for (int f = 0; f < listCourseCurrentWeek.size(); f++) {
//					MonHoc course = listCourseCurrentWeek.get(f);
//					if (course.getNgayHoc().equals(currentDateNote)) {
//						indexFocusCurrentDayEnd = f;
//					}
//				}
//
//			} else {
//				// the week bb, the week b, the week current, the week a, the week aa
//				String weekBBCode = (studyWeek - 2) + "", weekBCode = (studyWeek - 1) + "",
//						weekACode = (studyWeek + 1) + "", weekAACode = (studyWeek + 2) + "";
//				if (weekCodde.equals(weekBBCode)) {
//					listCourseTheWeekBY = week.getDsMonHoc();
//					txtTheWeekBB = ("Week " + weekCodde + " - " + week.getStartDate() + " to " + week.getEndDate());
//				} else if (weekCodde.equals(weekBCode)) {
//					listCourseYest = week.getDsMonHoc();
//					txtTheWeekB = ("Week " + weekCodde + " - " + week.getStartDate() + " to " + week.getEndDate());
//				} else if (weekCodde.equals(weekACode)) {
//					listCourseTomo = week.getDsMonHoc();
//					txtTheWeekA = ("Week " + weekCodde + " - " + week.getStartDate() + " to " + week.getEndDate());
//				} else if (weekCodde.equals(weekAACode)) {
//					listCourseTheWeekAT = week.getDsMonHoc();
//					txtTheWeekAA = ("Week " + weekCodde + " - " + week.getStartDate() + " to " + week.getEndDate());
//				}
//
//			}
//
//		}

//		setListTuan(listTuan);

//		try (FileOutputStream fos = new FileOutputStream(fileNameD);
//				OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {
////			byte[] byteJsonString = gsonLib.toJson(listTuan).getBytes(StandardCharsets.UTF_8);
////			String jsonStringCharset = new String(byteJsonString, StandardCharsets.UTF_8);
////			System.out.println(jsonStringCharset);
//			gsonLib.toJson(listTuan, osw);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
////		System.out.println(jsonString+" DONEE");

//		User user = gsonLib.fromJson(jsonString, User.class);
//		json = gson.fromJson(new InputStreamReader(is,"UTF-8"), parseType);
		// User object
//		User user = new User(inputCode, jsonString);

		// create a FileClient to the specified File
//		FileClient fileClient = FileClient.create(new File(inputCode+".json"));
//
		// create a JSON converter that converts the user object into a JSON object
//		OutputStreamOutputConverter<User> outputConverter = new JsonOutputConverter<>(User.class);
////
////		// store an object with an ObjectDataWriter created from the FileClient
//		GluonObservableObject<User> gluonUser = DataProvider.storeObject(user, fileClient.createObjectDataWriter(outputConverter));

//		String rootDir = System.getProperty("user.dir");
//		File root = Environment.getExternalStorageDirectory();
//		System.out.println("ROOOT: "+rootDir.replace("\\", "/"));
//		
//		try (OutputStream fos = Files.newOutputStream(Paths.get(rootDir+"/"+inputCode+".json"), StandardOpenOption.CREATE, StandardOpenOption.CREATE);
//				ObjectOutputStream oos = new ObjectOutputStream(fos);){
//			User user = new User(inputCode, jsonString);
//			oos.writeObject(user);
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
		return "";
	}

	private void sortUpCoursesViaDOWAndStartTime() {
		for (int e = 0; e < listTuan.size(); e++) {
			Tuan week = listTuan.get(e);
			ArrayList<MonHoc> listCourse = week.getDsMonHoc();
			Collections.sort(listCourse, new Comparator<MonHoc>() {
				@Override
				public int compare(MonHoc course1, MonHoc course2) {
					if (course1.getNgayHoc().equals(course2.getNgayHoc())
							&& course1.getNgayThu().equals(course2.getNgayThu())) {
						return Integer.parseInt(course1.getTietBatDau()) - Integer.parseInt(course2.getTietBatDau());
					} else {
						// sort via DOW
						try {
							Date firstDate = new SimpleDateFormat("dd/MM/yyyy").parse(course1.getNgayHoc()),
									secondDate = new SimpleDateFormat("dd/MM/yyyy").parse(course2.getNgayHoc());
							return firstDate.compareTo(secondDate);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
//							e.printStackTrace();
						}
					}
					return 0;
				}
			});
		}
	}

	private void convertLengthListCourseOfCurrentDOW(int countCoursePerDay) {
		this.lengthListCourseCurrentDOW = countCoursePerDay;
	}

	private String convertDateWeek(String date) {
		String rs = "", day = date.substring(0, 2), month = date.substring(3, 5);

		// add month
		switch (month) {
		case "01":
			rs += "January";
			break;
		case "02":
			rs += "February";
			break;
		case "03":
			rs += "March";
			break;
		case "04":
			rs += "April";
			break;
		case "05":
			rs += "May";
			break;
		case "06":
			rs += "June";
			break;
		case "07":
			rs += "July";
			break;
		case "08":
			rs += "August";
			break;
		case "09":
			rs += "September";
			break;
		case "10":
			rs += "October";
			break;
		case "11":
			rs += "November";
			break;
		case "12":
			rs += "December";
			break;
		default:
			break;
		}

		// add day
		if (Integer.parseInt(day) < 10) {
			day = Integer.parseInt(day) + "";
		}
		if (Integer.parseInt(day) >= 11 && Integer.parseInt(day) <= 13) {
			rs += " " + day + "th";
		} else {
			switch (Integer.parseInt(day) % 10) {
			case 1:
				rs += " " + day + "st";
				break;
			case 2:
				rs += " " + day + "nd";
				break;
			case 3:
				rs += " " + day + "rd";
				break;
			default:
				rs += " " + day + "th";
				break;
			}
		}
		return rs;
	}

	private String convertChosenDate(String maTuan, String startDate, String engDOW) {
		String res = "";
		Calendar c = Calendar.getInstance();
		try {
			int substractDate = 0;
			switch (engDOW) {
			case "Mon":
				substractDate = 7;
				break;
			case "Tue":
				substractDate = 6;
				break;
			case "Wed":
				substractDate = 5;
				break;
			case "Thur":
				substractDate = 4;
				break;
			case "Fri":
				substractDate = 3;
				break;
			case "Sat":
				substractDate = 2;
				break;
			case "Sun":
				substractDate = 1;
				break;
			default:
				break;
			}
			Date stDate = new SimpleDateFormat("dd/MM/yyyy").parse(startDate);
//					chosenDate = DateUtils.addDays(stDate, Integer.parseInt(maTuan) * 7 - substractDate);
			c.setTime(stDate);
			c.add(Calendar.DAY_OF_MONTH, Integer.parseInt(maTuan) * 7 - substractDate);
			res = new SimpleDateFormat("dd/MM/yyyy").format(c.getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		return res;
	}

	public void addMonHoc(Tuan week, String courseCode, String courseName, String shift, String location,
			String dayOfWeek, String time, String stSh, String enSh, String shNu, String day) {
//		week.getDsMonHoc().add(courses);
		// check duplicate
//		if(week.getDsMonHoc().size()==0) {
//			week.getDsMonHoc().add(courses);
		week.getDsMonHoc()
				.add(new MonHoc(courseCode, courseName, shift, location, dayOfWeek, time, stSh, enSh, shNu, day));
//		}
//		else {
//			for (int d = 0; d < week.getDsMonHoc().size(); d++) {
//				System.out.println(">>>"+week.getDsMonHoc().get(d).getMaMonHoc());
//				if (courses.equals(week.getDsMonHoc().get(d))) {
//					// do nothing
//					System.out.println("DUPPPLICATE");
//				} else {
//					week.getDsMonHoc().add(courses);
//				}
//			}
//		}
	}

	public static void show(Hashtable<Tuan, MonHoc> hashtable) {
		Enumeration<MonHoc> courseEnu = hashtable.elements();
		while (courseEnu.hasMoreElements()) {
			System.out.println(courseEnu.nextElement());
		}
	}

	public static int days[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

	// Function to return the day number
	// of the year for the given date
	public static int dayOfYear(String date) {
		// Extract the year, month and the
		// day from the date string
		int day = Integer.parseInt(date.substring(0, 2));

		int month = Integer.parseInt(date.substring(3, 5));

		int year = Integer.parseInt(date.substring(6, date.length()));
		System.out.println("day: " + day + ", month: " + month + ", year: " + year);
		// If current year is a leap year and the date
		// given is after the 28th of February then
		// it must include the 29th February
		if (month > 2 && year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) {
			++day;
		}

		// Add the days in the previous months
		while (--month > 0) {
			day = day + days[month - 1];
		}
		return day;
	}

	public String dayOfWeekFromVietToEng(String DOW) {
		String res = "";
		switch (DOW) {
		case "Hai":
			res = "Mon";
			break;
		case "Ba":
			res = "Tue";
			break;
		case "Tư":
			res = "Wed";
			break;
		case "Năm":
			res = "Thur";
			break;
		case "Sáu":
			res = "Fri";
			break;
		case "Bảy":
			res = "Sat";
			break;
		case "CN":
			res = "Sun";
			break;
		default:
			break;
		}
		return res;
	}

	public String dayOfWeekFromVietToEngInt(String DOW) {
		String res = "";
		switch (DOW) {
		case "2":
			res = "Mon";
			break;
		case "3":
			res = "Tue";
			break;
		case "4":
			res = "Wed";
			break;
		case "5":
			res = "Thur";
			break;
		case "6":
			res = "Fri";
			break;
		case "7":
			res = "Sat";
			break;
		case "8":
			res = "Sun";
			break;
		default:
			break;
		}
		return res;
	}

	public static String monthFromIntToString(int month) {
		String res = "";
		switch (month) {
		case 1:
			res = "January";
			break;
		case 2:
			res = "February";
			break;
		case 3:
			res = "March";
			break;
		case 4:
			res = "April";
			break;
		case 5:
			res = "May";
			break;
		case 6:
			res = "June";
			break;
		case 7:
			res = "July";
			break;
		case 8:
			res = "August";
			break;
		case 9:
			res = "September";
			break;
		case 10:
			res = "October";
			break;
		case 11:
			res = "November";
			break;
		case 12:
			res = "December";
			break;
		default:
			res = "Unknown";
			break;
		}
		return res;
	}

	public ArrayList<MonHoc> getListCourseCurrentDate() {
		ArrayList<MonHoc> listCourse = new ArrayList<MonHoc>();

		// search in list Tuan
		for (int q = 0; q < listTuan.size(); q++) {
			Tuan week = listTuan.get(q);
			if (week.getMaTuan().equals(studyWeek + "")) {
				// print out to check
				LOGGER.log(Level.INFO, "OK to get Study Week " + week.getMaTuan());

				// check if equal the current day
				for (MonHoc course : week.getDsMonHoc()) {
					if (course.getNgayHoc().equals(currentDateNote)) {
						listCourse.add(course);
						LOGGER.log(Level.INFO,
								"OK to get course: " + course.getTenMonHoc() + ", " + course.getNgayHoc());
					} else {
						LOGGER.log(Level.INFO, "not OK to get course: " + course.getTenMonHoc() + ", "
								+ course.getNgayHoc().toString() + ", instead: " + currentDateNote);
					}
				}

			} else {
				LOGGER.log(Level.INFO, "not OK to get Study Week " + week.getMaTuan() + ", instead :" + studyWeek);
			}
		}

		return listCourse;
	}

	public String getSTUDENT_CODE() {
		return inputCode;
	}

	public void setSTUDENT_CODE(String sTUDENT_CODE) {
		inputCode = sTUDENT_CODE;
	}

	public ArrayList<Tuan> getListTuan() {
		return listTuan;
	}

	public void setListTuan(ArrayList<Tuan> listTuan) {
		this.listTuan = listTuan;
	}

	public ArrayList<String> getListTextCourseFilter() {
		return listTextCourseFilter;
	}

	public void setListTextCourseFilter(ArrayList<String> listTextCourseFilter) {
		this.listTextCourseFilter = listTextCourseFilter;
	}

	public ArrayList<String> getListCourseCode() {
		return listCourseCode;
	}

	public void setListCourseCode(ArrayList<String> listCourseCode) {
		this.listCourseCode = listCourseCode;
	}

	public ArrayList<String> getListCourseName() {
		return listCourseName;
	}

	public void setListCourseName(ArrayList<String> listCourseName) {
		this.listCourseName = listCourseName;
	}

	public ArrayList<String> getListDayOfWeek() {
		return listDayOfWeek;
	}

	public void setListDayOfWeek(ArrayList<String> listDayOfWeek) {
		this.listDayOfWeek = listDayOfWeek;
	}

	public ArrayList<String> getListShift() {
		return listShift;
	}

	public void setListShift(ArrayList<String> listShift) {
		this.listShift = listShift;
	}

	public ArrayList<String> getListLocation() {
		return listLocation;
	}

	public void setListLocation(ArrayList<String> listLocation) {
		this.listLocation = listLocation;
	}

	public ArrayList<String> getListWeek() {
		return listWeek;
	}

	public void setListWeek(ArrayList<String> listWeek) {
		this.listWeek = listWeek;
	}

	public int getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(int currentDate) {
		this.currentDate = currentDate;
	}

	public int getCurrentMonth() {
		return currentMonth;
	}

	public void setCurrentMonth(int currentMonth) {
		this.currentMonth = currentMonth;
	}

	public int getCurrentYear() {
		return currentYear;
	}

	public void setCurrentYear(int currentYear) {
		this.currentYear = currentYear;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getRecentUpdateDate() {
		return recentUpdateDate;
	}

	public void setRecentUpdateDate(String recentUpdateDate) {
		this.recentUpdateDate = recentUpdateDate;
	}

	public ArrayList<String> getListFineCourseCode() {
		return listFineCourseCode;
	}

	public void setListFineCourseCode(ArrayList<String> listFineCourseCode) {
		this.listFineCourseCode = listFineCourseCode;
	}

	public String getInputCode() {
		return inputCode;
	}

	public void setInputCode(String inputCode) {
		this.inputCode = inputCode;
	}

	public int getStudyWeek() {
		return studyWeek;
	}

	public String getCurrentEngDayOfWeek() {
		return currentEngDayOfWeek;
	}

	public int getLengthListCourseCurrentDOW() {
		return lengthListCourseCurrentDOW;
	}

	public String getCurrentDOW() {
		return currentDOW;
	}

	public String getSAEDateViaWeek(String weekCode) {
		String saeDate = "";
		for (int e = 0; e < listTuan.size(); e++) {
			Tuan week = listTuan.get(e);
			int weekCodee = Integer.parseInt(week.getMaTuan());
			if (weekCode.equals(weekCodee + "")) {
				saeDate += week.getStartDate() + " to " + week.getEndDate();
			}
		}
		return saeDate;
	}

	public ArrayList<MonHoc> getListCourseViaWeekCode(int weekCode) {
		ArrayList<MonHoc> listCourseResult = new ArrayList<MonHoc>();
		for (int q = 0; q < listTuan.size(); q++) {
			Tuan week = listTuan.get(q);
			int weekCodee = Integer.parseInt(week.getMaTuan());
			if (weekCodee == weekCode) {
				listCourseResult = week.getDsMonHoc();
			}
		}
		return listCourseResult;
	}

	public ArrayList<MonHoc> getListCourseCurrentWeek() {
		return listCourseCurrentWeek;
	}

	public ArrayList<MonHoc> getListCourseTheWeekAT() {
		return listCourseTheWeekAT;
	}

	public ArrayList<MonHoc> getListCourseTomo() {
		return listCourseTomo;
	}

	public ArrayList<MonHoc> getListCourseYest() {
		return listCourseYest;
	}

	public ArrayList<MonHoc> getListCourseTheWeekBY() {
		return listCourseTheWeekBY;
	}

	public String getTxtCurrentWeek() {
		return txtCurrentWeek;
	}

	public String getTxtTheWeekBB() {
		return txtTheWeekBB;
	}

	public String getTxtTheWeekB() {
		return txtTheWeekB;
	}

	public String getTxtTheWeekA() {
		return txtTheWeekA;
	}

	public String getTxtTheWeekAA() {
		return txtTheWeekAA;
	}

	public int getIndexFocusCurrentDayStart() {
		return indexFocusCurrentDayStart;
	}

	public int getIndexFocusCurrentDayEnd() {
		return indexFocusCurrentDayEnd;
	}

	public ArrayList<MonHoc> getListCoursePresentDay() {
		ArrayList<MonHoc> listCourseResult = new ArrayList<MonHoc>();
		for (int q = 0; q < listTuan.size(); q++) {
			Tuan week = listTuan.get(q);
			String weekCodde = Integer.parseInt(week.getMaTuan()) + "";
			// check if current study week
			if (weekCodde.equals(studyWeek + "")) {
				// find present day
				for (int e = 0; e < week.getDsMonHoc().size(); e++) {
					String studyDate = week.getDsMonHoc().get(e).getNgayHoc().toString().trim();
					if (studyDate.equals(currentDateNote)) {
						listCourseResult.add(week.getDsMonHoc().get(e));
					}
				}
			}
		}
		return listCourseResult;
	}

	public ArrayList<MonHoc> getListCourseTomorrow() {
		ArrayList<MonHoc> listCourseResult = new ArrayList<MonHoc>();
		Calendar c = Calendar.getInstance();
		for (int q = 0; q < listTuan.size(); q++) {
			Tuan week = listTuan.get(q);
			String weekCodde = Integer.parseInt(week.getMaTuan()) + "";
			// check if current study week
			if (weekCodde.equals(studyWeek + "") || weekCodde.equals((studyWeek + 1) + "")) {
				// find tomorrow
				for (int e = 0; e < week.getDsMonHoc().size(); e++) {
					String studyDate = week.getDsMonHoc().get(e).getNgayHoc().toString().trim();
					Date stDate;
					try {
						// get tomorrow
						stDate = new SimpleDateFormat("dd/MM/yyyy").parse(currentDateNote);
						c.setTime(stDate);
						c.add(Calendar.DAY_OF_MONTH, 1);
						String resDay = new SimpleDateFormat("dd/MM/yyyy").format(c.getTime());

						// check whether or not checked day is tomorrow
						if (studyDate.equals(resDay)) {
							listCourseResult.add(week.getDsMonHoc().get(e));
						}
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}
		return listCourseResult;
	}

	public ArrayList<MonHoc> getListCourseTheDayAT() {
		ArrayList<MonHoc> listCourseResult = new ArrayList<MonHoc>();
		Calendar c = Calendar.getInstance();
		for (int q = 0; q < listTuan.size(); q++) {
			Tuan week = listTuan.get(q);
			String weekCodde = Integer.parseInt(week.getMaTuan()) + "";
			// check if current study week
			if (weekCodde.equals(studyWeek + "") || weekCodde.equals((studyWeek + 1) + "")) {
				// find the day a t
				for (int e = 0; e < week.getDsMonHoc().size(); e++) {
					String studyDate = week.getDsMonHoc().get(e).getNgayHoc().toString().trim();
					Date stDate;
					try {
						// get the day a t
						stDate = new SimpleDateFormat("dd/MM/yyyy").parse(currentDateNote);
						c.setTime(stDate);
						c.add(Calendar.DAY_OF_MONTH, 2);
						String resDay = new SimpleDateFormat("dd/MM/yyyy").format(c.getTime());
						// check whether or not checked day is the day a t
						if (studyDate.equals(resDay)) {
							listCourseResult.add(week.getDsMonHoc().get(e));
						}
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}
		return listCourseResult;
	}

	public ArrayList<MonHoc> getListCourseTheDayAAT() {
		ArrayList<MonHoc> listCourseResult = new ArrayList<MonHoc>();
		Calendar c = Calendar.getInstance();
		for (int q = 0; q < listTuan.size(); q++) {
			Tuan week = listTuan.get(q);
			String weekCodde = Integer.parseInt(week.getMaTuan()) + "";
			// check if current study week
			if (weekCodde.equals(studyWeek + "") || weekCodde.equals((studyWeek + 1) + "")) {
				// find the day a a t
				for (int e = 0; e < week.getDsMonHoc().size(); e++) {
					String studyDate = week.getDsMonHoc().get(e).getNgayHoc().toString().trim();
					Date stDate;
					try {
						// get the day a a t
						stDate = new SimpleDateFormat("dd/MM/yyyy").parse(currentDateNote);
						c.setTime(stDate);
						c.add(Calendar.DAY_OF_MONTH, 3);
						String resDay = new SimpleDateFormat("dd/MM/yyyy").format(c.getTime());

						// check whether or not checked day is the day a a t
						if (studyDate.equals(resDay)) {
							listCourseResult.add(week.getDsMonHoc().get(e));
						}
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}
		return listCourseResult;
	}

	public ArrayList<MonHoc> getListCourseTheDayAAAT() {
		ArrayList<MonHoc> listCourseResult = new ArrayList<MonHoc>();
		Calendar c = Calendar.getInstance();
		for (int q = 0; q < listTuan.size(); q++) {
			Tuan week = listTuan.get(q);
			String weekCodde = Integer.parseInt(week.getMaTuan()) + "";
			// check if current study week
			if (weekCodde.equals(studyWeek + "") || weekCodde.equals((studyWeek + 1) + "")) {
				// find the day a a a t
				for (int e = 0; e < week.getDsMonHoc().size(); e++) {
					String studyDate = week.getDsMonHoc().get(e).getNgayHoc().toString().trim();
					Date stDate;
					try {
						// get the day a a a t
						stDate = new SimpleDateFormat("dd/MM/yyyy").parse(currentDateNote);
						c.setTime(stDate);
						c.add(Calendar.DAY_OF_MONTH, 4);
						String resDay = new SimpleDateFormat("dd/MM/yyyy").format(c.getTime());

						// check whether or not checked day is the day a a a t
						if (studyDate.equals(resDay)) {
							listCourseResult.add(week.getDsMonHoc().get(e));
						}
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}
		return listCourseResult;
	}

	public ArrayList<MonHoc> getListCourseTheDayAAAAT() {
		ArrayList<MonHoc> listCourseResult = new ArrayList<MonHoc>();
		Calendar c = Calendar.getInstance();
		for (int q = 0; q < listTuan.size(); q++) {
			Tuan week = listTuan.get(q);
			String weekCodde = Integer.parseInt(week.getMaTuan()) + "";
			// check if current study week
			if (weekCodde.equals(studyWeek + "") || weekCodde.equals((studyWeek + 1) + "")) {
				// find the day a a a a t
				for (int e = 0; e < week.getDsMonHoc().size(); e++) {
					String studyDate = week.getDsMonHoc().get(e).getNgayHoc().toString().trim();
					Date stDate;
					try {
						// get the day a a t
						stDate = new SimpleDateFormat("dd/MM/yyyy").parse(currentDateNote);
						c.setTime(stDate);
						c.add(Calendar.DAY_OF_MONTH, 5);
						String resDay = new SimpleDateFormat("dd/MM/yyyy").format(c.getTime());

						// check whether or not checked day is the day a a a a t
						if (studyDate.equals(resDay)) {
							listCourseResult.add(week.getDsMonHoc().get(e));
						}
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}
		return listCourseResult;
	}

	public ArrayList<MonHoc> getListCourseTheDayAAAAAT() {
		ArrayList<MonHoc> listCourseResult = new ArrayList<MonHoc>();
		Calendar c = Calendar.getInstance();
		for (int q = 0; q < listTuan.size(); q++) {
			Tuan week = listTuan.get(q);
//			System.out.println(week.toString());
			String weekCodde = Integer.parseInt(week.getMaTuan()) + "";
			// check if current study week
			if (weekCodde.equals(studyWeek + "") || weekCodde.equals((studyWeek + 1) + "")) {
				// find the day a a a a a t
				for (int e = 0; e < week.getDsMonHoc().size(); e++) {
					String studyDate = week.getDsMonHoc().get(e).getNgayHoc().toString().trim();
					Date stDate;
					try {
						// get the day a a a a a t
						stDate = new SimpleDateFormat("dd/MM/yyyy").parse(currentDateNote);
						c.setTime(stDate);
						c.add(Calendar.DAY_OF_MONTH, 6);
						String resDay = new SimpleDateFormat("dd/MM/yyyy").format(c.getTime());

						// check whether or not checked day is the day a a a a a t
						if (studyDate.equals(resDay)) {
							listCourseResult.add(week.getDsMonHoc().get(e));
						}
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}
		return listCourseResult;
	}

	public String getTxtPresentDay() {
		String result = "";
		Calendar c = Calendar.getInstance();
		Date stDate;
		String dayAndMonth = convertDateWeek(currentDateNote);
		try {
			stDate = new SimpleDateFormat("dd/MM/yyyy").parse(currentDateNote);
			c.setTime(stDate);
			result = "Hôm nay - " + convertDOWEngToViet(strDays[c.get(Calendar.DAY_OF_WEEK) - 1]) + ", "
					+ currentDateNote;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private String convertDOWEngToViet(String string) {
		String res = "";
		switch (string) {
		case "Monday":
			res = "Thứ Hai";
			break;
		case "Tuesday":
			res = "Thứ Ba";
			break;
		case "Wednesday":
			res = "Thứ Tư";
			break;
		case "Thursday":
			res = "Thứ Năm";
			break;
		case "Friday":
			res = "Thứ Sáu";
			break;
		case "Saturday":
			res = "Thứ Bảy";
			break;
		case "Sunday":
			res = "Chủ Nhật";
			break;
		default:
			break;
		}
		return res;
	}

	public String getTxtTomorrow() {
		String result = "";
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, 1);
		String resDay = new SimpleDateFormat("dd/MM/yyyy").format(c.getTime());
		String dayAndMonth = convertDateWeek(resDay);

		Date stDate;
		try {
			stDate = new SimpleDateFormat("dd/MM/yyyy").parse(resDay);
			c.setTime(stDate);
			result = "Ngày mai - " + convertDOWEngToViet(strDays[c.get(Calendar.DAY_OF_WEEK) - 1]) + ", " + resDay;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public String getTxtTheDayAT() {
		String result = "";
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, 2);
		String resDay = new SimpleDateFormat("dd/MM/yyyy").format(c.getTime());
		String dayAndMonth = convertDateWeek(resDay);

		Date stDate;
		try {
			stDate = new SimpleDateFormat("dd/MM/yyyy").parse(resDay);
			c.setTime(stDate);
			result = convertDOWEngToViet(strDays[c.get(Calendar.DAY_OF_WEEK) - 1]) + ", " + resDay;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public String getTxtTheDayAAT() {
		String result = "";
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, 3);
		String resDay = new SimpleDateFormat("dd/MM/yyyy").format(c.getTime());
		String dayAndMonth = convertDateWeek(resDay);

		Date stDate;
		try {
			stDate = new SimpleDateFormat("dd/MM/yyyy").parse(resDay);
			c.setTime(stDate);
			result = convertDOWEngToViet(strDays[c.get(Calendar.DAY_OF_WEEK) - 1]) + ", " + resDay;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public String getTxtTheDayAAAT() {
		String result = "";
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, 4);
		String resDay = new SimpleDateFormat("dd/MM/yyyy").format(c.getTime());
		String dayAndMonth = convertDateWeek(resDay);

		Date stDate;
		try {
			stDate = new SimpleDateFormat("dd/MM/yyyy").parse(resDay);
			c.setTime(stDate);
			result = convertDOWEngToViet(strDays[c.get(Calendar.DAY_OF_WEEK) - 1]) + ", " + resDay;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public String getTxtTheDayAAAAT() {
		String result = "";
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, 5);
		String resDay = new SimpleDateFormat("dd/MM/yyyy").format(c.getTime());
		String dayAndMonth = convertDateWeek(resDay);

		Date stDate;
		try {
			stDate = new SimpleDateFormat("dd/MM/yyyy").parse(resDay);
			c.setTime(stDate);
			result = convertDOWEngToViet(strDays[c.get(Calendar.DAY_OF_WEEK) - 1]) + ", " + resDay;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public String getTxtTheDayAAAAAT() {
		String result = "";
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, 6);
		String resDay = new SimpleDateFormat("dd/MM/yyyy").format(c.getTime());
		String dayAndMonth = convertDateWeek(resDay);

		Date stDate;
		try {
			stDate = new SimpleDateFormat("dd/MM/yyyy").parse(resDay);
			c.setTime(stDate);
			result = convertDOWEngToViet(strDays[c.get(Calendar.DAY_OF_WEEK) - 1]) + ", " + resDay;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public boolean updateServerOrNot() {
		if (getListCoursePresentDay().size() == 0 && getListCourseTheDayAT().size() == 0
				&& getListCourseTheDayAAT().size() == 0 && getListCourseTheDayAAAT().size() == 0
				&& getListCourseTheDayAAAAT().size() == 0 && getListCourseTheDayAAAAAT().size() == 0
				&& getListCourseTomorrow().size() == 0) {
			return true;
		}
		return false;
	}

	public boolean checkServerUpdateOrNot() {
		if (getListCoursePresentDay().size() == 0 && getListCourseTheDayAT().size() == 0
				&& getListCourseTheDayAAT().size() == 0 && getListCourseTheDayAAAT().size() == 0
				&& getListCourseTheDayAAAAT().size() == 0 && getListCourseTheDayAAAAAT().size() == 0
				&& getListCourseTomorrow().size() == 0) {
			return false;
		}
		return true;
	}

	public ArrayList<MonThi> getListMonThi() {
		return listMonThi;
	}

	public int getListMonThiSize() {
		return listMonThi.size();
	}

	public String checkListMonThiToString() {
		String res = "";
		for (MonThi mt : listMonThi) {
			res += "" + mt.toString() + "\n----------------------";
		}
		return res;
	}

	public void updateListCourseChosenWeek(ArrayList<Tuan> weekArrayList) {
		Calendar calendar = Calendar.getInstance();
		Date mondayDate;

		try {
			for (int i = 0; i < weekArrayList.size(); i++) {
				if (i == chosenWeek - 1) {
					// chosen Week
					Tuan chosenWeek = weekArrayList.get(i);
					String mondayDateString = chosenWeek.getStartDate();

					mondayDate = new SimpleDateFormat("dd/MM/yyyy").parse(mondayDateString);

					calendar.setTime(mondayDate);
					calendar.add(Calendar.DAY_OF_MONTH, 1);
					String tuesdayDateString = new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());

					calendar.setTime(mondayDate);
					calendar.add(Calendar.DAY_OF_MONTH, 2);
					String wednesdayDateString = new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());

					calendar.setTime(mondayDate);
					calendar.add(Calendar.DAY_OF_MONTH, 3);
					String thursdayDateString = new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());

					calendar.setTime(mondayDate);
					calendar.add(Calendar.DAY_OF_MONTH, 4);
					String fridayDateString = new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());

					calendar.setTime(mondayDate);
					calendar.add(Calendar.DAY_OF_MONTH, 5);
					String saturdayDateString = new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());

					calendar.setTime(mondayDate);
					calendar.add(Calendar.DAY_OF_MONTH, 6);
					String sundayDateString = new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());

					// All LCs
					ArrayList<MonHoc> listMonHoc = chosenWeek.getDsMonHoc();
					for (int j = 0; j < listMonHoc.size(); j++) {
						MonHoc course = listMonHoc.get(j);

						// Monday LC
						if (course.getNgayHoc().equals(mondayDateString)) {
							listCourseMondayCW.add(course);
							System.out.println("Mon " + course.toString());
						}

						// Tuesday LC
						if (course.getNgayHoc().equals(tuesdayDateString)) {
							listCourseTuesdayCW.add(course);
							System.out.println("Tue " + course.toString());
						}

						// Wednesday LC
						if (course.getNgayHoc().equals(wednesdayDateString)) {
							listCourseWednesdayCW.add(course);
							System.out.println("Wed " + course.toString());
						}

						// Thursday LC
						if (course.getNgayHoc().equals(thursdayDateString)) {
							listCourseThursdayCW.add(course);
							System.out.println("Thur " + course.toString());
						}

						// Friday LC
						if (course.getNgayHoc().equals(fridayDateString)) {
							listCourseFridayCW.add(course);
							System.out.println("Fri " + course.toString());
						}

						// Saturday LC
						if (course.getNgayHoc().equals(saturdayDateString)) {
							listCourseSaturdayCW.add(course);
							System.out.println("Satur " + course.toString());
						}

						// Sunday LC
						if (course.getNgayHoc().equals(sundayDateString)) {
							listCourseSundayCW.add(course);
							System.out.println("Sun " + course.toString());
						}

					}

				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public String[] getChosenWeekListCourse(String currentChosenWeek2) {
		String[] resStringArr = new String[7];
		Calendar calendar = Calendar.getInstance();
		Date mondayDate;

		try {
			for (int i = 0; i < listTuan.size(); i++) {
				if (i == Integer.parseInt(currentChosenWeek2) - 1) {
					// chosen Week
					Tuan chosenWeek = listTuan.get(i);

					String mondayDateString = chosenWeek.getStartDate();

					// confirm new Chosen week
					System.out.println("----------------------------------------------------------");
					System.out.println("Tuần mới chọn: " + this.chosenWeek + ", SD: " + mondayDateString);

					mondayDate = new SimpleDateFormat("dd/MM/yyyy").parse(mondayDateString);

					// Monday labels
					resStringArr[0] = "Thứ Hai, " + mondayDateString;

					// Tuesday labels
					calendar.setTime(mondayDate);
					calendar.add(Calendar.DAY_OF_MONTH, 1);
					String tuesdayDateString = new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());
					resStringArr[1] = "Thứ Ba, " + tuesdayDateString;

					// Wednesday labels
					calendar.setTime(mondayDate);
					calendar.add(Calendar.DAY_OF_MONTH, 2);
					String wednesdayDateString = new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());
					resStringArr[2] = "Thứ Tư, " + wednesdayDateString;

					// Thursday labels
					calendar.setTime(mondayDate);
					calendar.add(Calendar.DAY_OF_MONTH, 3);
					String thursdayDateString = new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());
					resStringArr[3] = "Thứ Năm, " + thursdayDateString;

					// Friday labels
					calendar.setTime(mondayDate);
					calendar.add(Calendar.DAY_OF_MONTH, 4);
					String fridayDateString = new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());
					resStringArr[4] = "Thứ Sáu, " + fridayDateString;

					// Saturday labels
					calendar.setTime(mondayDate);
					calendar.add(Calendar.DAY_OF_MONTH, 5);
					String saturdayDateString = new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());
					resStringArr[5] = "Thứ Bảy, " + saturdayDateString;

					// Sunday labels
					calendar.setTime(mondayDate);
					calendar.add(Calendar.DAY_OF_MONTH, 6);
					String sundayDateString = new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());
					resStringArr[6] = "Chủ Nhật, " + sundayDateString;

					// All LCs
					ArrayList<MonHoc> listMonHoc = chosenWeek.getDsMonHoc();
					for (int j = 0; j < listMonHoc.size(); j++) {
						MonHoc course = listMonHoc.get(j);

						// Monday LC
						if (course.getNgayHoc().equals(mondayDateString)) {
							listCourseMondayCW.add(course);
							System.out.println("Mon: " + course.toString());
						}

						// Tuesday LC
						if (course.getNgayHoc().equals(tuesdayDateString)) {
							listCourseTuesdayCW.add(course);
							System.out.println("Tue: " + course.toString());
						}

						// Wednesday LC
						if (course.getNgayHoc().equals(wednesdayDateString)) {
							listCourseWednesdayCW.add(course);
							System.out.println("Wed: " + course.toString());
						}

						// Thursday LC
						if (course.getNgayHoc().equals(thursdayDateString)) {
							listCourseThursdayCW.add(course);
							System.out.println("Thur: " + course.toString());
						}

						// Friday LC
						if (course.getNgayHoc().equals(fridayDateString)) {
							listCourseFridayCW.add(course);
							System.out.println("Fri: " + course.toString());
						}

						// Saturday LC
						if (course.getNgayHoc().equals(saturdayDateString)) {
							listCourseSaturdayCW.add(course);
							System.out.println("Sat: " + course.toString());
						}

						// Sunday LC
						if (course.getNgayHoc().equals(sundayDateString)) {
							listCourseSundayCW.add(course);
							System.out.println("Sun: " + course.toString());
						}

					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return resStringArr;
	}

	public ArrayList<MonHoc> getChosenWeekListCourseBesideLabel(String curChosenWeek) {
		ArrayList<MonHoc> resListMonHoc = new ArrayList<MonHoc>();
		return resListMonHoc;
	}

	public ArrayList<MonHoc> getListCourseMondayCW() {
		return listCourseMondayCW;
	}

	public ArrayList<MonHoc> getListCourseTuesdayCW() {
		return listCourseTuesdayCW;
	}

	public ArrayList<MonHoc> getListCourseWednesdayCW() {
		return listCourseWednesdayCW;
	}

	public ArrayList<MonHoc> getListCourseThursdayCW() {
		return listCourseThursdayCW;
	}

	public ArrayList<MonHoc> getListCourseFridayCW() {
		return listCourseFridayCW;
	}

	public ArrayList<MonHoc> getListCourseSaturdayCW() {
		return listCourseSaturdayCW;
	}

	public ArrayList<MonHoc> getListCourseSundayCW() {
		return listCourseSundayCW;
	}

	public int getChosenWeek() {
		return chosenWeek;
	}

	public void setChosenWeek(int chosenWeek) {
		this.chosenWeek = chosenWeek;
	}

	public boolean getChosenWeekBool(int newChosenWeek) {
		return newChosenWeek != 1;
	}

	public void setCwBoolean(boolean cwBoolean) {
		this.cwBoolean = cwBoolean;
	}

	public boolean checkJsonFileExistOrNot(boolean isAndroid) {

//		Toast toastConfirmDevice = new Toast("Is Android, right: "+isAndroid, Toast.LENGTH_LONG);
//		toastConfirmDevice.show();

		// Check whether or not JSON file exist
		File fileNOS_Android = new File(Main.publicRootDirAndroid + "/" + inputCode + "_nos.json"),
				fileFES_Android = new File(Main.publicRootDirAndroid + "/" + inputCode + "_fes.json"),
				fileNOS_Desktop = new File(System.getProperty("user.dir") + "/" + inputCode + "_nos.json"),
				fileFES_Desktop = new File(System.getProperty("user.dir") + "/" + inputCode + "_fes.json");

//		Toast toastConfirmNOS = new Toast(""+fileNOS_Desktop.getAbsolutePath(), Toast.LENGTH_LONG);
//		toastConfirmNOS.show();
//		
//		Toast toastConfirmFES = new Toast("Content: "+fileFES_Desktop.getName(), Toast.LENGTH_LONG);
//		toastConfirmFES.show();

		// Check whether or not Desktop & Android version
		if ((fileNOS_Android.exists() == false && fileFES_Android.exists() == false && isAndroid == true)
				|| (fileNOS_Desktop.exists() == false && fileFES_Desktop.exists() == false && isAndroid == false)) {

			Toast toastConfirmWrite = new Toast("Haven't found any JSON file at all.", Toast.LENGTH_LONG);
			toastConfirmWrite.show();

			return false;
		} else {
			Toast toastConfirmWrite = new Toast("Have found JSON file and begin loading data.", Toast.LENGTH_LONG);
			toastConfirmWrite.show();
			try {
				if (isAndroid == false) {
					// Desktop Case

					// Loading TKB data
					BufferedReader buffNOS = new BufferedReader(
							new InputStreamReader(new FileInputStream(fileNOS_Desktop), "UTF-8"));

					JSONParser parserNOS = new JSONParser();

					JSONArray arrayNOS = (JSONArray) parserNOS.parse(buffNOS);

					for (Object o : arrayNOS) {
						JSONObject week = (JSONObject) o;

						String weekCode = (String) week.get("maTuan");
//						System.out.println("Mã tuần: " + weekCode);

						String startDate = (String) week.get("startDate");
//						System.out.println("Ngày bắt đầu: " + startDate);

						String endDate = (String) week.get("endDate");
//						System.out.println("Ngày kết thúc: " + endDate);

						// init Tuan
						Tuan newWeek = new Tuan(weekCode, startDate, endDate);

						// init ArrayList<MonHoc>
						ArrayList<MonHoc> listCoursePar = new ArrayList<MonHoc>();

						JSONArray listCourse = (JSONArray) week.get("dsMonHoc");

//						System.out.println("Danh sách môn học: \n");
						for (Object co : listCourse) {
							JSONObject course = (JSONObject) co;
							String maMonHoc = (String) course.get("maMonHoc"),
									tenMonHoc = (String) course.get("tenMonHoc"),
									tietBatDau = (String) course.get("tietBatDau"),
									diaDiemHoc = (String) course.get("diaDiemHoc"),
									ngayThu = (String) course.get("ngayThu"),
									thoiGianHoc = (String) course.get("thoiGianHoc"),
									ngayHoc = (String) course.get("ngayHoc");

							// handle stSh, enSh and shNu
							String stSh = thoiGianHoc.substring(0, thoiGianHoc.indexOf('-') - 1),
									enSh = thoiGianHoc.substring(thoiGianHoc.indexOf('-') + 1,
											thoiGianHoc.indexOf('(') - 1),
									shNu_first = thoiGianHoc.substring(thoiGianHoc.indexOf('('),
											thoiGianHoc.lastIndexOf('-')),
									shNu_second = thoiGianHoc.substring(thoiGianHoc.lastIndexOf('-'),
											thoiGianHoc.lastIndexOf(')')),
									shNu = shNu_first + " - " + shNu_second + ": ";

							// init MonHoc
							MonHoc coursePar = new MonHoc(maMonHoc, tenMonHoc, tietBatDau, diaDiemHoc, ngayThu,
									thoiGianHoc, stSh, enSh, shNu, ngayHoc);

							// add MonHoc to listCoursePar
							listCoursePar.add(coursePar);

							// confirm via printing
//							System.out.println("\tMã môn học: " + maMonHoc + "." + "\n\tTên môn học: " + tenMonHoc + "."
//									+ "\n\tTiết bắt đầu: " + tietBatDau + "." + "\n\tĐịa điểm học: " + diaDiemHoc + "."
//									+ "\n\tNgày thứ: " + ngayThu + "." + "\n\t Thời gian học: " + thoiGianHoc + "."
//									+ "\n\tNgày học: " + ngayHoc + "." + "\n---------------------\n");

//							Toast toastConfirmCourseName = new Toast("Tên môn học: "+tenMonHoc+".", Toast.LENGTH_LONG);
//							toastConfirmCourseName.show();
						}
						
						// set listCourse for newWeek
						newWeek.setDsMonHoc(listCoursePar);

						// add newWeek to listTuan
						listTuan.add(newWeek);
					}

					// confirm again the listTuan
					for (Tuan we : listTuan) {
						System.out.println(we.toString());
					}

					// close buff
					buffNOS.close();

//					String textNOS_Desktop = "";
//					FileInputStream finNOS_Desktop = new FileInputStream(fileNOS_Desktop);
//					byte[] buffNOS_Desktop = new byte[1024];
//					int lenNOS_Desktop = finNOS_Desktop.read(buffNOS_Desktop);
//					while (lenNOS_Desktop > 0) {
//						textNOS_Desktop += new String(buffNOS_Desktop, 0, lenNOS_Desktop);
//						lenNOS_Desktop = finNOS_Desktop.read(buffNOS_Desktop);
//					}
//
////					System.out.println("[De] Content TKB: " + textNOS_Desktop.substring(150, 450));
//					Toast toastConfirmDemoContentNOSD = new Toast(" -- R TKB f: " + textNOS_Desktop.substring(180, 200),
//							Toast.LENGTH_LONG);
//					toastConfirmDemoContentNOSD.show();
//					finNOS_Desktop.close();

					// Loading FE data

					BufferedReader buffFES = new BufferedReader(
							new InputStreamReader(new FileInputStream(fileFES_Desktop), "UTF-8"));

					JSONParser parserFES = new JSONParser();
					JSONArray arrayFES = (JSONArray) parserFES.parse(buffFES);

					for (Object o : arrayFES) {
						JSONObject monThi = (JSONObject) o;

						String tenMonThi = (String) monThi.get("tenMonHoc"), ghepThi = (String) monThi.get("ghepThi"),
								toThi = (String) monThi.get("toThi"), soLuong = (String) monThi.get("soLuong"),
								ngayThi = (String) monThi.get("ngayThi"), ngayThu = (String) monThi.get("ngayThu"),
								phongThi = (String) monThi.get("phongThi"), thoiGian = (String) monThi.get("thoiGian"),
								tietThi = (String) monThi.get("tietThi");

						// init & add MonThi to listMonThi

						MonThi mon = new MonThi(tenMonThi, ghepThi, toThi, soLuong, ngayThi, ngayThu, phongThi,
								thoiGian, tietThi);
						listMonThi.add(mon);

						// confirm via printing out
//						System.out.println("Tên môn thi: " + tenMonThi + "." + "\n\tGhép thi: " + ghepThi + "."
//								+ "\n\tTổ thi: " + toThi + "." + "\n\tSố lượng: " + soLuong + "." + "\n\tNgày thi: "
//								+ ngayThi + "." + "\n\tNgày thứ: " + ngayThu + "." + "\n\tPhòng thi: " + phongThi + "."
//								+ "\n\tThời gian: " + thoiGian + "." + "\n\tTiết thi: " + tietThi + "."
//								+ "\n--------------------------\n");

//						Toast toastConfirmCourseName = new Toast("Tên môn thi: " + tenMonThi + ".", Toast.LENGTH_LONG);
//						toastConfirmCourseName.show();
					}

					// confirm again the listMonThi
					for (MonThi mt : listMonThi) {
						System.out.println(mt.toString());
					}
					
					// close buffFES
					buffFES.close();

				} else {
					// Android Case

					// Loading TKB data

					BufferedReader buffNOS = new BufferedReader(
							new InputStreamReader(new FileInputStream(fileNOS_Android), "UTF-8"));

					JSONParser parserNOS = new JSONParser();

					JSONArray arrayNOS = (JSONArray) parserNOS.parse(buffNOS);

					for (Object o : arrayNOS) {
						JSONObject week = (JSONObject) o;

						String weekCode = (String) week.get("maTuan");
//						System.out.println("Mã tuần: " + weekCode);

						String startDate = (String) week.get("startDate");
//						System.out.println("Ngày bắt đầu: " + startDate);

						String endDate = (String) week.get("endDate");
//						System.out.println("Ngày kết thúc: " + endDate);

						// init Tuan
						Tuan newWeek = new Tuan(weekCode, startDate, endDate);

						// init ArrayList<MonHoc>
						ArrayList<MonHoc> listCoursePar = new ArrayList<MonHoc>();

						JSONArray listCourse = (JSONArray) week.get("dsMonHoc");

//						System.out.println("Danh sách môn học: \n");
						for (Object co : listCourse) {
							JSONObject course = (JSONObject) co;
							String maMonHoc = (String) course.get("maMonHoc"),
									tenMonHoc = (String) course.get("tenMonHoc"),
									tietBatDau = (String) course.get("tietBatDau"),
									diaDiemHoc = (String) course.get("diaDiemHoc"),
									ngayThu = (String) course.get("ngayThu"),
									thoiGianHoc = (String) course.get("thoiGianHoc"),
									ngayHoc = (String) course.get("ngayHoc");

							// handle stSh, enSh and shNu
							String stSh = thoiGianHoc.substring(0, thoiGianHoc.indexOf('-') - 1),
									enSh = thoiGianHoc.substring(thoiGianHoc.indexOf('-') + 1,
											thoiGianHoc.indexOf('(') - 1),
									shNu_first = thoiGianHoc.substring(thoiGianHoc.indexOf('('),
											thoiGianHoc.lastIndexOf('-')),
									shNu_second = thoiGianHoc.substring(thoiGianHoc.lastIndexOf('-'),
											thoiGianHoc.lastIndexOf(')')),
									shNu = shNu_first + " - " + shNu_second + ": ";

							// init MonHoc
							MonHoc coursePar = new MonHoc(maMonHoc, tenMonHoc, tietBatDau, diaDiemHoc, ngayThu,
									thoiGianHoc, stSh, enSh, shNu, ngayHoc);

							// add MonHoc to listCoursePar
							listCoursePar.add(coursePar);

							// confirm via printing
//							System.out.println("\tMã môn học: " + maMonHoc + "." + "\n\tTên môn học: " + tenMonHoc + "."
//									+ "\n\tTiết bắt đầu: " + tietBatDau + "." + "\n\tĐịa điểm học: " + diaDiemHoc + "."
//									+ "\n\tNgày thứ: " + ngayThu + "." + "\n\t Thời gian học: " + thoiGianHoc + "."
//									+ "\n\tNgày học: " + ngayHoc + "." + "\n---------------------\n");

//							Toast toastConfirmCourseName = new Toast("Tên môn học: "+tenMonHoc+".", Toast.LENGTH_LONG);
//							toastConfirmCourseName.show();
						}

						// set listCourse for newWeek
						newWeek.setDsMonHoc(listCoursePar);

						// add newWeek to listTuan
						listTuan.add(newWeek);
					}

					// confirm again the listTuan
//					for (Tuan we : listTuan) {
//						System.out.println(we.toString());
//					}

					// confirm listTuan via toasting
//					Toast toastConfirmListTuan = new Toast("Size: "+listTuan.size()+".", Toast.LENGTH_LONG);
//					toastConfirmListTuan.show();
					
					// close buff
					buffNOS.close();

					// Loading FE data

					BufferedReader buffFES = new BufferedReader(
							new InputStreamReader(new FileInputStream(fileFES_Android), "UTF-8"));

					JSONParser parserFES = new JSONParser();
					JSONArray arrayFES = (JSONArray) parserFES.parse(buffFES);

					for (Object o : arrayFES) {
						JSONObject monThi = (JSONObject) o;

						String tenMonThi = (String) monThi.get("tenMonHoc"), ghepThi = (String) monThi.get("ghepThi"),
								toThi = (String) monThi.get("toThi"), soLuong = (String) monThi.get("soLuong"),
								ngayThi = (String) monThi.get("ngayThi"), ngayThu = (String) monThi.get("ngayThu"),
								phongThi = (String) monThi.get("phongThi"), thoiGian = (String) monThi.get("thoiGian"),
								tietThi = (String) monThi.get("tietThi");

//						System.out.println("Tên môn thi: " + tenMonThi + "." + "\n\tGhép thi: " + ghepThi + "."
//								+ "\n\tTổ thi: " + toThi + "." + "\n\tSố lượng: " + soLuong + "." + "\n\tNgày thi: "
//								+ ngayThi + "." + "\n\tNgày thứ: " + ngayThu + "." + "\n\tPhòng thi: " + phongThi + "."
//								+ "\n\tThời gian: " + thoiGian + "." + "\n\tTiết thi: " + tietThi + "."
//								+ "\n--------------------------\n");

						// init & add MonThi to listMonThi

						MonThi mon = new MonThi(tenMonThi, ghepThi, toThi, soLuong, ngayThi, ngayThu, phongThi,
								thoiGian, tietThi);
						listMonThi.add(mon);

//						Toast toastConfirmCourseName = new Toast("Tên môn thi: "+tenMonThi+".", Toast.LENGTH_LONG);
//						toastConfirmCourseName.show();
					}

					// confirm again the listMonThi
//					for (MonThi mt : listMonThi) {
//						System.out.println(mt.toString());
//					}

					// confirm listMonThi via toasting
//					Toast toastConfirmListMonThi = new Toast("Size: "+listMonThi.size()+".", Toast.LENGTH_LONG);
//					toastConfirmListMonThi.show();
					
					// close buffFES
					buffFES.close();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

			return true;
		}

	}

	public String getCurrentDevice() {
		return currentDevice;
	}

	public void setCurrentDevice(String currentDevice) {
		this.currentDevice = currentDevice;
	}

}
