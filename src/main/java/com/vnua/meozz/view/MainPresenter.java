package com.vnua.meozz.view;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.Dialog;
import com.gluonhq.charm.glisten.control.FloatingActionButton;
import com.gluonhq.charm.glisten.control.ProgressIndicator;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.vnua.meozz.main.Main;
import com.vnua.meozz.main.ToastMessage;
import com.vnua.meozz.main.VNUAScheduler;
import com.vnua.meozz.model.CustomListCellThird;
import com.vnua.meozz.model.LoadingIndicatorDialog;
import com.vnua.meozz.model.Menu;
import com.vnua.meozz.model.MonHoc;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.util.Callback;

public class MainPresenter extends GluonPresenter<VNUAScheduler> {

	private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
	public boolean isSplashLoaded = false, isServerOK = false;
	private int[] dayCheckNoCourseAtAll = new int[7];
	private Menu menu;
	private String resultLoadMenu;

	// UI and views
	private VBox vBoxMain;
	private JFXButton btnSubmit;
	private JFXTextField tfStuAndLecCode;

	private ProgressIndicator piLoading;
	private Thread threadLoadingUpdateUIs;

	private Dialog dialogNoInCon;
	private Button btnOK, btnCancel;

	private javafx.scene.control.Menu menuMain;
	private MenuItem updateItem, logOutItem, searchItem;
	private Image scheduleIcon, vnuaLogoIcon;
	private ImageView updateImView, searchImView, logOutImView, scheduleImView, vnuaLogoImView, search24ImView,
			search36ImView, deleteAll36ImView, deleteAll24ImView;

	private String currentDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date()), currentWeek = "",
			currentDOW = "";
	private ObservableList<String> listCourseInfo = FXCollections.observableArrayList("Course name", "Time", "Place");
	private ToastMessage toast;

	private Label lblPresentDay, lblTomorrow, lblTheDayAT, lblTheDayAAT, lblTheDayAAAT, lblTheDayAAAAT, lblTheDayAAAAAT;
	private ObservableList<MonHoc> listCourseDataT, listCourseDataA, listCourseDataAA, listCourseDataAAA,
			listCourseDataAAAA, listCourseDataAAAAA, listCourseDataC;
	private ListView<MonHoc> listViewCoursesPresentDay, listViewCoursesTomorrow, listViewCoursesTheDayAT,
			listViewCoursesTheDayAAT, listViewCoursesTheDayAAAT, listViewCoursesTheDayAAAAT,
			listViewCoursesTheDayAAAAAT;
	private VBox vBoxC, vBoxT, vBoxA, vBoxAA, vBoxAAA, vBoxAAAA, vBoxAAAAA, vBoxMainSub;
	private HBox hboxSearchTextField, hboxSearchTF, hboxDeleteAllIcon;
	private Pane paneSearchTextField;
	private Rectangle rectangle;

	private ScrollPane scrollPaneMain;
	private int indexFocusCurrentDayStart, indexFocusCurrentDayEnd;

	private LoadingIndicatorDialog lid;

	@FXML
	private View main;

	
	public void initialize() {
		vBoxMain = new VBox();
		vBoxMain.setSpacing(15);

		// check Internet connectivity
		Process processCheckInCo;
//		dialogNoInCon = new Dialog();
		btnOK = new Button("OK");
		btnCancel = new Button("Cancel");
//		dialogNoInCon.setTitle(new Label("No Internet Connection"));
//		dialogNoInCon.setContent(new Label("You need to have Mobile Data or wifi to access this"));
		btnOK.setOnAction(e -> {
//			dialogNoInCon.hide();
		});
//		dialogNoInCon.getButtons().addAll(btnOK, btnCancel);
		try {
			processCheckInCo = Runtime.getRuntime().exec("ping http://daotao.vnua.edu.vn");
			int x = processCheckInCo.waitFor();

			if (x == 0) {
				// Has Internet Connection
				LOGGER.log(Level.INFO, "Good Internet Connection.");
//				dialogNoInCon.showAndWait();
			} else {
				// No Internet Connection
//				dialogNoInCon.showAndWait();

				LOGGER.log(Level.INFO, "No Internet Connection.");
			}

		} catch (IOException | InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// init menu
		menu = new Menu();

		// check whether or not Server Daotao.vnua.edu.vn UPDATE || BUSY || INSTALL
		// Confirm Code
		try {
			resultLoadMenu = menu.handleJsoupAndJSON();
			isServerOK = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		// run method
		if (isServerOK == true) {
			if (resultLoadMenu != null && resultLoadMenu.equals("")) {
				// 2 options : no Internet and Socket TimeOut

				// option 1: no Internet
				try {
					URL url = new URL("http://daotao.vnua.edu.vn");
					URLConnection connection = url.openConnection();
					connection.connect();

					LOGGER.log(Level.INFO, "Good Internet Connection.");
				} catch (SocketTimeoutException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

					LOGGER.log(Level.INFO, "Socket Time Out Exception.");

				} catch (IOException e2) {
					e2.printStackTrace();
				}

			}
		}

		// set up in4
		currentWeek = menu.getStudyWeek() + "";
		currentDOW = menu.getCurrentEngDayOfWeek();

		// Floating Search Button
		FloatingActionButton fab = new FloatingActionButton(MaterialDesignIcon.SEARCH.text, e -> {
			Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
			AppBar appBarCurrent = MobileApplication.getInstance().getAppBar();
			int screenWidth = (int) bounds.getWidth(), screenHeight = (int) bounds.getHeight();
			int halfScreenWidthPosition = screenWidth / 2, scheduleImageViewWidth = (int) scheduleIcon.getWidth(),
					titleAppBarWidth = (int) appBarCurrent.getTitle().getBoundsInParent().getWidth(),
					vnuaLogoIconWidth = (int) vnuaLogoIcon.getWidth(),
					halfTotalWidthIAL = (scheduleImageViewWidth + 5 + titleAppBarWidth) / 2,
					startPointImagePosition = halfScreenWidthPosition - halfTotalWidthIAL,
					startPointTitlePosition = startPointImagePosition + 5 + scheduleImageViewWidth,
					textFieldWidthCal = screenWidth - 423;

//									appBarCurrent.getTitle().setTranslateX(startPointTitlePosition);
//									appBarCurrent.getActionItems().get(0).setTranslateX(startPointImagePosition-screenWidth);
//									appBarCurrent.getNavIcon().setTranslateX(10);
			com.gluonhq.charm.glisten.control.Toast toastMessage = new com.gluonhq.charm.glisten.control.Toast(
					"Text field width: " + textFieldWidthCal + ", SW: " + screenWidth + ", SH: " + screenHeight,
					com.gluonhq.charm.glisten.control.Toast.LENGTH_LONG);
			toastMessage.show();

			// roll back to hBoxSearchTextField
//						scrollPaneMain.layout();
			scrollPaneMain.setVvalue(0);

			// focus on Text Field
			tfStuAndLecCode.requestFocus();
		});

		tfStuAndLecCode = new JFXTextField();
		tfStuAndLecCode.setPromptText("Student / Lecturer code");
		tfStuAndLecCode.setEditable(true);
		tfStuAndLecCode.setFocusTraversable(false);
		tfStuAndLecCode.setMinWidth(175);
		tfStuAndLecCode.setMinHeight(35);
		tfStuAndLecCode.setStyle("-fx-prompt-text-fill: #ADFF2F ;\r\n" + "-fx-text-inner-color: #ADFF2F ;\r\n"
				+ "-fx-text-fill: #ADFF2F ;\r\n" + "-fx-font-family: 'Rubik';\r\n" + "-fx-font-size: 12pt;");

//					// FX Font Icon - Ikonli Packs (ver 2.6.0) - Icomoon
		//
//					FontIcon searchFI = new FontIcon();
//					searchFI.setIconLiteral("icm-search:24:GREENYELLOW");

		// Normal Search Icon
		Image searchIcon24 = new Image(getClass().getResourceAsStream("images/ic_search_24_green.png"));
		search24ImView = new ImageView(searchIcon24);

		Image searchIcon36 = new Image(getClass().getResourceAsStream("images/ic_search_36_green.png"));
		search36ImView = new ImageView(searchIcon36);
		search36ImView.setFitWidth(24);
		search36ImView.setFitHeight(24);

		Image deleteAllIcon24 = new Image(getClass().getResourceAsStream("images/ic_delete_all_24.png"));
		deleteAll24ImView = new ImageView(deleteAllIcon24);

		Image deleteAllIcon36 = new Image(getClass().getResourceAsStream("images/ic_delete_all_36.png"));
		deleteAll36ImView = new ImageView(deleteAllIcon36);
		deleteAll36ImView.setFitWidth(24);
		deleteAll36ImView.setFitHeight(24);

		btnSubmit = new JFXButton("Submit");
		btnSubmit.setMinWidth(120);
		btnSubmit.setMinHeight(35);
		btnSubmit.setMaxWidth(140);
		btnSubmit.setMaxHeight(40);

		rectangle = new Rectangle(273, 40);
		rectangle.setFill(Color.rgb(161, 161, 160)); // B4A4C4 , (95, 108, 146)
		// set rounded
		rectangle.setArcHeight(20);
		rectangle.setArcWidth(20);

		hboxSearchTF = new HBox(tfStuAndLecCode);
		hboxSearchTF.setPadding(new Insets(3, 10, 5, 35));

		HBox hboxSearchIcon = new HBox(search36ImView);
		hboxSearchIcon.setPadding(new Insets(8, 10, 0, 8));

		hboxDeleteAllIcon = new HBox(deleteAll36ImView);
		hboxDeleteAllIcon.setPadding(new Insets(8, 10, 0, 235));
		hboxDeleteAllIcon.setVisible(false);

		// handle delete all TF content when TF on click
		deleteAll36ImView.setOnMouseClicked(e -> {
			tfStuAndLecCode.setText("");
			tfStuAndLecCode.requestFocus();
		});

		deleteAll36ImView.setOnTouchReleased(e -> {
			tfStuAndLecCode.setText("");
			tfStuAndLecCode.requestFocus();
		});

		// handle visibility of delete all icon when length of TF > 0
		Thread taskShowDeleteAllIconThread = new Thread(() -> {
			while (!Thread.currentThread().isInterrupted()) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {

						if (tfStuAndLecCode.getText().length() > 0) {
							hboxDeleteAllIcon.setVisible(true);
						} else {
							hboxDeleteAllIcon.setVisible(false);
						}
					}
				});
				try {
					Thread.sleep(10);
				} catch (InterruptedException exc) {
					break;
				}
			}
		});
		taskShowDeleteAllIconThread.setDaemon(true);
		taskShowDeleteAllIconThread.start();

		paneSearchTextField = new Pane(rectangle, hboxSearchIcon, hboxSearchTF, hboxDeleteAllIcon);

		hboxSearchTextField = new HBox(10, paneSearchTextField, btnSubmit);
		hboxSearchTextField.setMinWidth(380);
		hboxSearchTextField.setMinHeight(50);
		hboxSearchTextField.setPadding(new Insets(10, 10, 8, 10));

		// handle labels
		lblPresentDay = new Label();
		lblPresentDay.setText(menu.getTxtPresentDay());
		lblPresentDay.setStyle("-fx-font-size: 15pt;\r\n" + " -fx-font-family: 'Rubik';" + "-fx-text-fill: #ff0000;"
				+ "\r\n-fx-background-color: #E0DEDE;");

		// the container of label
		VBox lblPDContainer = new VBox(lblPresentDay);
		lblPDContainer.setPadding(new Insets(2, 0, 2, 20));

		lblTomorrow = new Label();
		lblTomorrow.setText(menu.getTxtTomorrow());
		lblTomorrow.setStyle("-fx-font-size: 15pt;\r\n" + " -fx-font-family: 'Rubik';" + "-fx-text-fill: #591212;"
				+ "\r\n-fx-background-color: #E0DEDE;");

		// the container of label
		VBox lblTContainer = new VBox(lblTomorrow);
		lblTContainer.setPadding(new Insets(2, 0, 2, 20));

		lblTheDayAT = new Label();
		lblTheDayAT.setText(menu.getTxtTheDayAT());
		lblTheDayAT.setStyle("-fx-font-size: 15pt;\r\n" + " -fx-font-family: 'Rubik';" + "-fx-text-fill: #591212;"
				+ "\r\n-fx-background-color: #E0DEDE;");

		// the container of label
		VBox lblATContainer = new VBox(lblTheDayAT);
		lblATContainer.setPadding(new Insets(2, 0, 2, 20));

		lblTheDayAAT = new Label();
		lblTheDayAAT.setText(menu.getTxtTheDayAAT());
		lblTheDayAAT.setStyle("-fx-font-size: 15pt;\r\n" + " -fx-font-family: 'Rubik';" + "-fx-text-fill: #591212;"
				+ "\r\n-fx-background-color: #E0DEDE;");

		// the container of label
		VBox lblAATContainer = new VBox(lblTheDayAAT);
		lblAATContainer.setPadding(new Insets(2, 0, 2, 20));

		lblTheDayAAAT = new Label();
		lblTheDayAAAT.setText(menu.getTxtTheDayAAAT());
		lblTheDayAAAT.setStyle("-fx-font-size: 15pt;\r\n" + " -fx-font-family: 'Rubik';" + "-fx-text-fill: #591212;"
				+ "\r\n-fx-background-color: #E0DEDE;");

		// the container of label
		VBox lblAAATContainer = new VBox(lblTheDayAAAT);
		lblAAATContainer.setPadding(new Insets(2, 0, 2, 20));

		lblTheDayAAAAT = new Label();
		lblTheDayAAAAT.setText(menu.getTxtTheDayAAAAT());
		lblTheDayAAAAT.setStyle("-fx-font-size: 15pt;\r\n" + " -fx-font-family: 'Rubik';" + "-fx-text-fill: #591212;"
				+ "\r\n-fx-background-color: #E0DEDE;");

		// the container of label
		VBox lblAAAATContainer = new VBox(lblTheDayAAAAT);
		lblAAAATContainer.setPadding(new Insets(2, 0, 2, 20));

		lblTheDayAAAAAT = new Label();
		lblTheDayAAAAAT.setText(menu.getTxtTheDayAAAAAT());
		lblTheDayAAAAAT.setStyle("-fx-font-size: 15pt;\r\n" + " -fx-font-family: 'Rubik';" + "-fx-text-fill: #591212;"
				+ "\r\n-fx-background-color: #E0DEDE;");

		// the container of label
		VBox lblAAAAATContainer = new VBox(lblTheDayAAAAAT);
		lblAAAAATContainer.setPadding(new Insets(2, 0, 2, 20));

		// check whether or not happen 'Update Server, Pls Wait 15 minutes Later"
		boolean updateOrNot = menu.updateServerOrNot();

		// init list course data
		listCourseDataC = FXCollections.observableArrayList();
		listCourseDataT = FXCollections.observableArrayList();
		listCourseDataA = FXCollections.observableArrayList();
		listCourseDataAA = FXCollections.observableArrayList();
		listCourseDataAAA = FXCollections.observableArrayList();
		listCourseDataAAAA = FXCollections.observableArrayList();
		listCourseDataAAAAA = FXCollections.observableArrayList();

		// handle index Focus Current Start and End
		indexFocusCurrentDayStart = menu.getIndexFocusCurrentDayStart();
		indexFocusCurrentDayEnd = menu.getIndexFocusCurrentDayEnd();

		// update ListView
		updateListViewC();
		updateListViewT();
		updateListViewA();
		updateListViewAA();
		updateListViewAAA();
		updateListViewAAAA();
		updateListViewAAAAA();

		// check whether or not The day has No course at all.
		if (updateOrNot == false) {
			if (listCourseDataC.size() == 0 || listCourseDataC.isEmpty()) {
				dayCheckNoCourseAtAll[0] = 1;
			} else if (listCourseDataC.size() == 1) {
				dayCheckNoCourseAtAll[0] = 2;
			}
			if (listCourseDataT.size() == 0 || listCourseDataT.isEmpty()) {
				dayCheckNoCourseAtAll[1] = 1;
			} else if (listCourseDataT.size() == 1) {
				dayCheckNoCourseAtAll[1] = 2;
			}
			if (listCourseDataA.size() == 0 || listCourseDataA.isEmpty()) {
				dayCheckNoCourseAtAll[2] = 1;
			} else if (listCourseDataA.size() == 1) {
				dayCheckNoCourseAtAll[2] = 2;
			}
			if (listCourseDataAA.size() == 0 || listCourseDataAA.isEmpty()) {
				dayCheckNoCourseAtAll[3] = 1;
			} else if (listCourseDataAA.size() == 1) {
				dayCheckNoCourseAtAll[3] = 2;
			}
			if (listCourseDataAAA.size() == 0 || listCourseDataAAA.isEmpty()) {
				dayCheckNoCourseAtAll[4] = 1;
			} else if (listCourseDataAAA.size() == 1) {
				dayCheckNoCourseAtAll[4] = 2;
			}
			if (listCourseDataAAAA.size() == 0 || listCourseDataAAAA.isEmpty()) {
				dayCheckNoCourseAtAll[5] = 1;
			} else if (listCourseDataAAAA.size() == 1) {
				dayCheckNoCourseAtAll[5] = 2;
			}
			if (listCourseDataAAAAA.size() == 0 || listCourseDataAAAAA.isEmpty()) {
				dayCheckNoCourseAtAll[6] = 1;
			} else if (listCourseDataAAAAA.size() == 1) {
				dayCheckNoCourseAtAll[6] = 2;
			}
		}

		// init list view and set list cell
		listViewCoursesPresentDay = new ListView<MonHoc>(listCourseDataC);
		listViewCoursesPresentDay.setCellFactory(new Callback<ListView<MonHoc>, ListCell<MonHoc>>() {

			public ListCell<MonHoc> call(ListView<MonHoc> listView) {
				return new CustomListCellThird();
			}

		});
		listViewCoursesTomorrow = new ListView<MonHoc>(listCourseDataT);
		listViewCoursesTomorrow.setCellFactory(new Callback<ListView<MonHoc>, ListCell<MonHoc>>() {

			public ListCell<MonHoc> call(ListView<MonHoc> listView) {
				return new CustomListCellThird();
			}

		});
		listViewCoursesTheDayAT = new ListView<MonHoc>(listCourseDataA);
		listViewCoursesTheDayAT.setCellFactory(new Callback<ListView<MonHoc>, ListCell<MonHoc>>() {

			public ListCell<MonHoc> call(ListView<MonHoc> listView) {
				return new CustomListCellThird();
			}

		});
		listViewCoursesTheDayAAT = new ListView<MonHoc>(listCourseDataAA);
		listViewCoursesTheDayAAT.setCellFactory(new Callback<ListView<MonHoc>, ListCell<MonHoc>>() {

			public ListCell<MonHoc> call(ListView<MonHoc> listView) {
				return new CustomListCellThird();
			}

		});
		listViewCoursesTheDayAAAT = new ListView<MonHoc>(listCourseDataAAA);
		listViewCoursesTheDayAAAT.setCellFactory(new Callback<ListView<MonHoc>, ListCell<MonHoc>>() {

			public ListCell<MonHoc> call(ListView<MonHoc> listView) {
				return new CustomListCellThird();
			}

		});
		listViewCoursesTheDayAAAAT = new ListView<MonHoc>(listCourseDataAAAA);
		listViewCoursesTheDayAAAAT.setCellFactory(new Callback<ListView<MonHoc>, ListCell<MonHoc>>() {

			public ListCell<MonHoc> call(ListView<MonHoc> listView) {
				return new CustomListCellThird();
			}

		});
		listViewCoursesTheDayAAAAAT = new ListView<MonHoc>(listCourseDataAAAAA);
		listViewCoursesTheDayAAAAAT.setCellFactory(new Callback<ListView<MonHoc>, ListCell<MonHoc>>() {

			public ListCell<MonHoc> call(ListView<MonHoc> listView) {
				return new CustomListCellThird();
			}

		});

		// Focus
		listViewCoursesPresentDay.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		listViewCoursesTomorrow.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		listViewCoursesTheDayAT.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		listViewCoursesTheDayAAT.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		listViewCoursesTheDayAAAT.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		listViewCoursesTheDayAAAAT.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		listViewCoursesTheDayAAAAAT.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		listViewCoursesPresentDay.setStyle("-fx-border-width: 0;" + "\r\n-fx-border-color: #fff");
		listViewCoursesPresentDay.setMinWidth(365);
		listViewCoursesPresentDay.setMaxHeight(175);

		// specially focus on The Current Day

		listViewCoursesTomorrow.setStyle("-fx-border-width: 0;" + "\r\n-fx-border-color: #fff");
		listViewCoursesTheDayAAAT.setMaxWidth(365);
		listViewCoursesTomorrow.setMaxHeight(175);

		listViewCoursesTheDayAT.setStyle("-fx-border-width: 0;" + "\r\n-fx-border-color: #fff");
		listViewCoursesTheDayAAAT.setMaxWidth(365);
		listViewCoursesTheDayAT.setMaxHeight(175);

		listViewCoursesTheDayAAT.setStyle("-fx-border-width: 0;" + "\r\n-fx-border-color: #fff");
		listViewCoursesTheDayAAAT.setMaxWidth(365);
		listViewCoursesTheDayAAT.setMaxHeight(175);

		listViewCoursesTheDayAAAT.setStyle("-fx-border-width: 0;" + "\r\n-fx-border-color: #fff");
		listViewCoursesTheDayAAAT.setMaxWidth(365);
		listViewCoursesTheDayAAAT.setMaxHeight(175);

		listViewCoursesTheDayAAAAT.setStyle("-fx-border-width: 0;" + "\r\n-fx-border-color: #fff");
		listViewCoursesTheDayAAAT.setMaxWidth(365);
		listViewCoursesTheDayAAAAT.setMaxHeight(175);

		listViewCoursesTheDayAAAAAT.setStyle("-fx-border-width: 0;" + "\r\n-fx-border-color: #fff");
		listViewCoursesTheDayAAAT.setMaxWidth(365);
		listViewCoursesTheDayAAAAAT.setMaxHeight(175);

		// handle on click for item
//					listViewCourse.setOnMouseClicked(new EventHandler<javafx.scene.input.MouseEvent>() {
		//
//						@Override
//						public void handle(javafx.scene.input.MouseEvent event) {
//							int currentChosenItem = listViewCourse.getSelectionModel().getSelectedIndex();
////									listViewCourse.getSelectionModel().select(currentChosenItem);
////									listViewCourse.scrollTo(currentChosenItem);
//							listViewCourse.getFocusModel().focus(currentChosenItem);
//						}
//					});

		// handle VBoxs
		if (dayCheckNoCourseAtAll[0] == 0) {
			vBoxC = new VBox(lblPDContainer, listViewCoursesPresentDay);
			vBoxC.setStyle(
					"\r\n-fx-border-color: #fff;" + "\r\n-fx-border-width: 0;" + "\r\n-fx-background-color: #E0DEDE;");
		} else if (dayCheckNoCourseAtAll[0] == 1) {
			// init label no courses
			Label lblNoCourses = new Label("(Không có môn học nào)");
			lblNoCourses.setStyle("-fx-font-size: 20px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
					+ "  -fx-font-family: 'Rubik';" + "-fx-text-fill: #000");
			lblNoCourses.setPadding(new Insets(10, 20, 10, 20));
			lblNoCourses.setAlignment(Pos.CENTER);

			vBoxC = new VBox(lblPDContainer, lblNoCourses);
			vBoxC.setStyle(
					"\r\n-fx-border-color: #fff;" + "\r\n-fx-border-width: 0;" + "\r\n-fx-background-color: #E0DEDE;");
		} else if (dayCheckNoCourseAtAll[0] == 2) {
			listViewCoursesPresentDay.setMaxHeight(85);
			vBoxC = new VBox(lblPDContainer, listViewCoursesPresentDay);
			vBoxC.setStyle(
					"\r\n-fx-border-color: #fff;" + "\r\n-fx-border-width: 0;" + "\r\n-fx-background-color: #E0DEDE;");
		}

		if (dayCheckNoCourseAtAll[1] == 0) {
			vBoxT = new VBox(lblTContainer, listViewCoursesTomorrow);
			vBoxT.setStyle(
					"\r\n-fx-border-color: #fff;" + "\r\n-fx-border-width: 0;" + "\r\n-fx-background-color: #E0DEDE;");
		} else if (dayCheckNoCourseAtAll[1] == 1) {
			// init label no courses
			Label lblNoCourses = new Label("(Không có môn học nào)");
			lblNoCourses.setStyle("-fx-font-size: 20px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
					+ "  -fx-font-family: 'Rubik';" + "-fx-text-fill: #000");
			lblNoCourses.setPadding(new Insets(10, 20, 10, 20));
			lblNoCourses.setAlignment(Pos.CENTER);

			vBoxT = new VBox(lblTContainer, lblNoCourses);
			vBoxT.setStyle(
					"\r\n-fx-border-color: #fff;" + "\r\n-fx-border-width: 0;" + "\r\n-fx-background-color: #E0DEDE;");
		} else if (dayCheckNoCourseAtAll[1] == 2) {
			listViewCoursesTomorrow.setMaxHeight(85);
			vBoxT = new VBox(lblTContainer, listViewCoursesTomorrow);
			vBoxT.setStyle(
					"\r\n-fx-border-color: #fff;" + "\r\n-fx-border-width: 0;" + "\r\n-fx-background-color: #E0DEDE;");
		}

		if (dayCheckNoCourseAtAll[2] == 0) {
			vBoxA = new VBox(lblATContainer, listViewCoursesTheDayAT);
			vBoxA.setStyle(
					"\r\n-fx-border-color: #fff;" + "\r\n-fx-border-width: 0;" + "\r\n-fx-background-color: #E0DEDE;");
		} else if (dayCheckNoCourseAtAll[2] == 1) {
			// init label no courses
			Label lblNoCourses = new Label("(Không có môn học nào)");
			lblNoCourses.setStyle("-fx-font-size: 20px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
					+ "  -fx-font-family: 'Rubik';" + "-fx-text-fill: #000");
			lblNoCourses.setPadding(new Insets(10, 20, 10, 20));
			lblNoCourses.setAlignment(Pos.CENTER);

			vBoxA = new VBox(lblATContainer, lblNoCourses);
			vBoxA.setStyle(
					"\r\n-fx-border-color: #fff;" + "\r\n-fx-border-width: 0;" + "\r\n-fx-background-color: #E0DEDE;");
		} else if (dayCheckNoCourseAtAll[2] == 2) {
			listViewCoursesTheDayAT.setMaxHeight(85);
			vBoxA = new VBox(lblATContainer, listViewCoursesTheDayAT);
			vBoxA.setStyle(
					"\r\n-fx-border-color: #fff;" + "\r\n-fx-border-width: 0;" + "\r\n-fx-background-color: #E0DEDE;");
		}

		if (dayCheckNoCourseAtAll[3] == 0) {
			vBoxAA = new VBox(lblAATContainer, listViewCoursesTheDayAAT);
			vBoxAA.setStyle(
					"\r\n-fx-border-color: #fff;" + "\r\n-fx-border-width: 0;" + "\r\n-fx-background-color: #E0DEDE;");
		} else if (dayCheckNoCourseAtAll[3] == 1) {
			// init label no courses
			Label lblNoCourses = new Label("(Không có môn học nào)");
			lblNoCourses.setStyle("-fx-font-size: 20px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
					+ "  -fx-font-family: 'Rubik';" + "-fx-text-fill: #000");
			lblNoCourses.setPadding(new Insets(10, 20, 10, 20));
			lblNoCourses.setAlignment(Pos.CENTER);

			vBoxAA = new VBox(lblAATContainer, lblNoCourses);
			vBoxAA.setStyle(
					"\r\n-fx-border-color: #fff;" + "\r\n-fx-border-width: 0;" + "\r\n-fx-background-color: #E0DEDE;");
		} else if (dayCheckNoCourseAtAll[3] == 2) {
			listViewCoursesTheDayAAT.setMaxHeight(85);
			vBoxAA = new VBox(lblAATContainer, listViewCoursesTheDayAAT);
			vBoxAA.setStyle(
					"\r\n-fx-border-color: #fff;" + "\r\n-fx-border-width: 0;" + "\r\n-fx-background-color: #E0DEDE;");
		}

		if (dayCheckNoCourseAtAll[4] == 0) {
			vBoxAAA = new VBox(lblAAATContainer, listViewCoursesTheDayAAAT);
			vBoxAAA.setStyle(
					"\r\n-fx-border-color: #fff;" + "\r\n-fx-border-width: 0;" + "\r\n-fx-background-color: #E0DEDE;");
		} else if (dayCheckNoCourseAtAll[4] == 1) {
			// init label no courses
			Label lblNoCourses = new Label("(Không có môn học nào)");
			lblNoCourses.setStyle("-fx-font-size: 20px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
					+ "  -fx-font-family: 'Rubik';" + "-fx-text-fill: #000");
			lblNoCourses.setPadding(new Insets(10, 20, 10, 20));
			lblNoCourses.setAlignment(Pos.CENTER);

			vBoxAAA = new VBox(lblAAATContainer, lblNoCourses);
			vBoxAAA.setStyle(
					"\r\n-fx-border-color: #fff;" + "\r\n-fx-border-width: 0;" + "\r\n-fx-background-color: #E0DEDE;");
		} else if (dayCheckNoCourseAtAll[4] == 2) {
			listViewCoursesTheDayAAAT.setMaxHeight(85);
			vBoxAAA = new VBox(lblAAATContainer, listViewCoursesTheDayAAAT);
			vBoxAAA.setStyle(
					"\r\n-fx-border-color: #fff;" + "\r\n-fx-border-width: 0;" + "\r\n-fx-background-color: #E0DEDE;");
		}

		if (dayCheckNoCourseAtAll[5] == 0) {
			vBoxAAAA = new VBox(lblAAAATContainer, listViewCoursesTheDayAAAAT);
			vBoxAAAA.setStyle(
					"\r\n-fx-border-color: #fff;" + "\r\n-fx-border-width: 0;" + "\r\n-fx-background-color: #E0DEDE;");
		} else if (dayCheckNoCourseAtAll[5] == 1) {
			// init label no courses
			Label lblNoCourses = new Label("(Không có môn học nào)");
			lblNoCourses.setStyle("-fx-font-size: 20px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
					+ "  -fx-font-family: 'Rubik';" + "-fx-text-fill: #000");
			lblNoCourses.setPadding(new Insets(10, 20, 10, 20));
			lblNoCourses.setAlignment(Pos.CENTER);

			vBoxAAAA = new VBox(lblAAAATContainer, lblNoCourses);
			vBoxAAAA.setStyle(
					"\r\n-fx-border-color: #fff;" + "\r\n-fx-border-width: 0;" + "\r\n-fx-background-color: #E0DEDE;");
		} else if (dayCheckNoCourseAtAll[5] == 2) {
			listViewCoursesTheDayAAAAT.setMaxHeight(85);
			vBoxAAAA = new VBox(lblAAAATContainer, listViewCoursesTheDayAAAAT);
			vBoxAAAA.setStyle(
					"\r\n-fx-border-color: #fff;" + "\r\n-fx-border-width: 0;" + "\r\n-fx-background-color: #E0DEDE;");
		}

		if (dayCheckNoCourseAtAll[6] == 0) {
			vBoxAAAAA = new VBox(lblAAAAATContainer, listViewCoursesTheDayAAAAAT);
			vBoxAAAAA.setStyle(
					"\r\n-fx-border-color: #fff;" + "\r\n-fx-border-width: 0;" + "\r\n-fx-background-color: #E0DEDE;");
		} else if (dayCheckNoCourseAtAll[6] == 1) {
			// init label no courses
			Label lblNoCourses = new Label("(Không có môn học nào)");
			lblNoCourses.setStyle("-fx-font-size: 20px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
					+ "  -fx-font-family: 'Rubik';" + "-fx-text-fill: #000");
			lblNoCourses.setPadding(new Insets(10, 20, 10, 20));
			lblNoCourses.setAlignment(Pos.CENTER);

			vBoxAAAAA = new VBox(lblAAAAATContainer, lblNoCourses);
			vBoxAAAAA.setStyle(
					"\r\n-fx-border-color: #fff;" + "\r\n-fx-border-width: 0;" + "\r\n-fx-background-color: #E0DEDE;");
		} else if (dayCheckNoCourseAtAll[6] == 2) {
			listViewCoursesTheDayAAAAAT.setMaxHeight(85);
			vBoxAAAAA = new VBox(lblAAAAATContainer, listViewCoursesTheDayAAAAAT);
			vBoxAAAAA.setStyle(
					"\r\n-fx-border-color: #fff;" + "\r\n-fx-border-width: 0;" + "\r\n-fx-background-color: #E0DEDE;");
		}

		// handles StackPaneContent and JFXScrollPane
		vBoxMainSub = new VBox(hboxSearchTextField, vBoxC, vBoxT, vBoxA, vBoxAA, vBoxAAA, vBoxAAAA, vBoxAAAAA);
		vBoxMainSub.setMinHeight(640);
		vBoxMainSub.setMinWidth(375);
		scrollPaneMain = new ScrollPane();

		scrollPaneMain.setContent(vBoxMainSub);

		scrollPaneMain.setMinWidth(375);
		scrollPaneMain.setMinHeight(450);
		scrollPaneMain.setMaxHeight(650);

		// set default roll position to hide hboxSearchTextField
		scrollPaneMain.setVvalue(1.735);

		vBoxMain.getChildren().addAll(
//							hboxSearchTextField,
				scrollPaneMain);

		// handle Schedule Icon and VNUA_Logo Icon
		scheduleIcon = new Image(getClass().getResourceAsStream("images/ic_schedule_48.png"));
		scheduleImView = new ImageView(scheduleIcon);
//					scheduleImView.setFitWidth(48);
//					scheduleImView.setFitHeight(48);

		vnuaLogoIcon = new Image(getClass().getResourceAsStream("images/ic_vnua_logo_40.png"));
		vnuaLogoImView = new ImageView(vnuaLogoIcon);
//					vnuaLogoImView.setFitWidth(20);
//					vnuaLogoImView.setFitHeight(20);

		// handle Menu and Menu Items
		Image updateIcon = new Image(getClass().getResourceAsStream("images/ic_update_25.png"));
		updateImView = new ImageView(updateIcon);
		updateImView.setFitWidth(20);
		updateImView.setFitHeight(20);

		Image searchIcon = new Image(getClass().getResourceAsStream("images/ic_search_30.png"));
		searchImView = new ImageView(searchIcon);
		searchImView.setFitWidth(23);
		searchImView.setFitHeight(23);

		Image logOutIcon = new Image(getClass().getResourceAsStream("images/ic_log_out_30.png"));
		logOutImView = new ImageView(logOutIcon);
		logOutImView.setFitWidth(23);
		logOutImView.setFitHeight(23);

		menuMain = new javafx.scene.control.Menu();
		updateItem = new MenuItem(" Update schedule");
		updateItem.setGraphic(updateImView);
		searchItem = new MenuItem(" Search for courses");
		searchItem.setGraphic(searchImView);
		logOutItem = new MenuItem(" Log out");
		logOutItem.setGraphic(logOutImView);

		main = new View(vBoxMain) {
			@Override
			protected void updateAppBar(AppBar appBar) {
				appBar.setTitleText("VNUA Scheduler");
				appBar.setStyle("-fx-background-color: #028D45;");
				appBar.getTitle().setStyle(
						"-fx-font-size: 22px;\r\n" + "-fx-font-family: 'Rubik';\r\n" + "-fx-text-fill: #FEF701;\r\n");
				appBar.getTitle().setTranslateX(150);
				appBar.setNavIcon(vnuaLogoImView);
				appBar.getMenuItems().addAll(updateItem, searchItem, logOutItem);
				appBar.getActionItems().addAll(scheduleImView);
				appBar.getNavIcon().setOnKeyReleased(e -> {

				});
			}
		};

		fab.showOn(main);

		main.showingProperty().addListener((obs, ov, nv) -> {
			if (nv) {
				AppBar appBar = MobileApplication.getInstance().getAppBar();
				appBar.setTitleText("VNUA Scheduler");
				appBar.setStyle("-fx-background-color: #028D45;");
				appBar.getTitle().setStyle(
						"-fx-font-size: 22px;\r\n" + "-fx-font-family: 'Rubik';\r\n" + "-fx-text-fill: #FEF701;\r\n");
				appBar.getTitle().setTranslateX(150);
				appBar.setNavIcon(vnuaLogoImView);
				appBar.getMenuItems().addAll(updateItem, searchItem, logOutItem);
				appBar.getActionItems().addAll(scheduleImView);
			}
		});
	}

	private void updateListViewC() {
		ArrayList<MonHoc> listCourseCD = menu.getListCoursePresentDay();
		this.listCourseDataC = FXCollections.observableArrayList(listCourseCD);
	}

	private void updateListViewT() {
		ArrayList<MonHoc> listCourseCD = menu.getListCourseTomorrow();
		this.listCourseDataT = FXCollections.observableArrayList(listCourseCD);
	}

	private void updateListViewA() {
		ArrayList<MonHoc> listCourseCD = menu.getListCourseTheDayAT();
		this.listCourseDataA = FXCollections.observableArrayList(listCourseCD);
	}

	private void updateListViewAA() {
		ArrayList<MonHoc> listCourseCD = menu.getListCourseTheDayAAT();
		this.listCourseDataAA = FXCollections.observableArrayList(listCourseCD);
	}

	private void updateListViewAAA() {
		ArrayList<MonHoc> listCourseCD = menu.getListCourseTheDayAAAT();
		this.listCourseDataAAA = FXCollections.observableArrayList(listCourseCD);
	}

	private void updateListViewAAAA() {
		ArrayList<MonHoc> listCourseCD = menu.getListCourseTheDayAAAAT();
		this.listCourseDataAAAA = FXCollections.observableArrayList(listCourseCD);
	}

	private void updateListViewAAAAA() {
		ArrayList<MonHoc> listCourseCD = menu.getListCourseTheDayAAAAAT();
		this.listCourseDataAAAAA = FXCollections.observableArrayList(listCourseCD);
	}
}
