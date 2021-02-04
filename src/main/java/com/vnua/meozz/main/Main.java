package com.vnua.meozz.main;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gluonhq.charm.down.ServiceFactory;
import com.gluonhq.charm.down.Services;
import com.gluonhq.charm.down.common.JavaFXPlatform;
import com.gluonhq.charm.down.plugins.ConnectivityService;
import com.gluonhq.charm.down.plugins.StorageService;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.application.ViewStackPolicy;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.Dialog;
import com.gluonhq.charm.glisten.control.FloatingActionButton;
import com.gluonhq.charm.glisten.control.ProgressIndicator;
import com.gluonhq.charm.glisten.control.TextField;
import com.gluonhq.charm.glisten.control.Toast;
import com.gluonhq.charm.glisten.control.ToggleButtonGroup;
import com.gluonhq.charm.glisten.mvc.SplashView;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.gluonhq.charm.glisten.visual.Swatch;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextField;
import com.vnua.meozz.model.CustomListCellThird;
import com.vnua.meozz.model.LoadingIndicatorDialog;
import com.vnua.meozz.model.Menu;
import com.vnua.meozz.model.MonHoc;
import com.vnua.meozz.model.MonThi;
import com.vnua.meozz.view.CustomToggleSwitchButton;
import com.vnua.meozz.view.SwitchToggleButton;
import com.vnua.meozz.view.ToggleSwitch;
import com.vnua.meozz.view.dialog.TurnOffVietKeybDialog;
import com.vnua.meozz.view.dialog.UpdateConfirmDialog;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Window;
import javafx.util.Callback;
import javafx.util.Duration;

public class Main extends MobileApplication {
	private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
	public boolean isSplashLoaded = false, isServerOK = false;
	private int[] dayCheckNoCourseAtAll = new int[7];
	private Menu menu;
	private String resultLoadMenu;

	// UI and views
	private VBox vBoxMain;
	private JFXButton btnSubmit;
	private JFXTextField tfStuAndLecCode, tfStudentInputCode;
	private TableView tableView;
	private TableColumn tblCol1, tblCol2, tblCol3;

	private ProgressIndicator piLoading;
	private Thread threadLoadingUpdateUIs;

	private Dialog dialogNoInCon;
	private Button btnOK, btnCancel;

	private javafx.scene.control.Menu menuMain;
	private MenuItem updateItem, aboutUsItem, finalExamViewItem, viewFinalExamScheduleItem;
	private Image scheduleIcon, vnuaLogoIcon, schedule32Icon;
	private ImageView updateImView, fExamImView, aboutUsImView, scheduleImView, vnuaLogoImView, search24ImView,
			search36ImView, deleteAll36ImView, deleteAll24ImView, appScreenShotImView, schedule32ImView;

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
	private VBox vBoxMainTabFE;
	private HBox hboxSearchTextField, hboxSearchTF, hboxDeleteAllIcon, hboxButtonGS, hboxButtonToggle;
	private Pane paneSearchTextField;
	private Rectangle rectangle;

	private ScrollPane scrollPaneMain;
	private StackPane stackPaneMain;
	private GridPane gridPaneTextFieldButton;
	private int indexFocusCurrentDayStart, indexFocusCurrentDayEnd;

	private LoadingIndicatorDialog lid;
	private JFXTabPane tabPane;
	private SwitchToggleButton buttonToggleSwitch;
	
	private static final String SPLASH_VIEW = "Splash";
	public static final String GET_STARTED_VIEW = "GetStarted";
	public static final String INFOR_VALID_VIEW = "InforValid";
	public static final String MAIN_VIEW = "Main";
	public static final String FINAL_EXAM_VIEW = "FinalExam";
	public static final String ABOUT_US_VIEW = "AboutUs";
	public static final String PER_CONFIRM_VIEW = "PerCon";
	public static final String PER_REQUEST_VIEW = "PerReq";
	public static final String NO_INTERNET_VIEW = "NoInternet";
	public static final String UPDATE_SERVER_CONFIRM_ONE_VIEW = "UpdateServerOne";
	public static final String UPDATE_SERVER_CONFIRM_ZERO_VIEW = "UpdateServerZero";

	private static final int widthCurrentScreen = (int) Screen.getPrimary().getVisualBounds().getWidth();
	public boolean NO_INTERNET_CON = false;

	// Main Thread of Application -> Splash -> GetStarted -> InforValid -> PerReq ->
	// PerCon -> Main -> AboutUs or ExtendedSearch.

	private ViewStackPolicy defaultViewStackPolicy;
	public boolean UPDATE_SERVER_CONFIRM = false;
	public boolean WRONG_INPUT_CODE = false;
	public boolean GO_TO_MAIN_VIEW = false;
	public String INPUT_CODE_RESTORE = "INPUT_CODE_RESTORE";
	public boolean STUDENT_NOTIFY = false;
	private String TAB_0 = "TKB";
	private String TAB_1 = "Thi CK";
	private String resultLoadMenuFE = "";
	private ScrollPane scrollPaneFinalExam;

	public static final File ROOT_DIR_ANDROID;
	static {
		ROOT_DIR_ANDROID = Services.get(StorageService.class).flatMap(StorageService::getPrivateStorage)
				.orElseThrow(() -> new RuntimeException("Error retrieving private storage"));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void init() {

//		addViewFactory(GET_STARTED_VIEW, () -> {
//			GluonView getStartedView = new GluonView(GetStartedPresenter.class);
//			getStartedView.getView().setId(GET_STARTED_VIEW);
//			return getStartedView.getView();
//		});
//
//		addViewFactory(INFOR_VALID_VIEW, () -> {
//			GluonView inforValidView = new GluonView(InforValidPresenter.class);
//			inforValidView.getView().setId(INFOR_VALID_VIEW);
//			return inforValidView.getView();
//		});

		// not follow the MVP model

		Task<Void> taskSwitchMV = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				Platform.runLater(() -> switchView(MAIN_VIEW, defaultViewStackPolicy));
				return null;
			}
		};

		Task<Void> taskSwitchIV = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				Platform.runLater(() -> {
					try {
						setUpMainView();
					} catch (IndexOutOfBoundsException e) {
						UPDATE_SERVER_CONFIRM = true;
//						LOGGER.log(Level.INFO, "Update Server Daotao.vnua.edu.vn !!!!!");
					}

					if (checkUpdateServer().equals("") && NO_INTERNET_CON==false) {
						switchView(INFOR_VALID_VIEW, defaultViewStackPolicy);
						TurnOffVietKeybDialog tovkbDialog = new TurnOffVietKeybDialog(
								"Vui lòng tắt bàn phím dành cho tiếng Việt hoặc đổi sang bàn phím mặc định tiếng Anh để trải nghiệm ứng dụng một cách tốt nhất.");
						tovkbDialog.showAndWait();
					} else if (NO_INTERNET_CON == true) {
						switchView(NO_INTERNET_VIEW, defaultViewStackPolicy);
					} else if (UPDATE_SERVER_CONFIRM == true) {
						switchView(UPDATE_SERVER_CONFIRM_ONE_VIEW, defaultViewStackPolicy);
					}
				});
				return null;
			}
		};

		Task<Void> taskSwitchPR = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				Platform.runLater(() -> switchView(PER_REQUEST_VIEW, defaultViewStackPolicy));
				return null;
			}
		};

		Task<Void> taskSwitchPC = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				Platform.runLater(() -> switchView(PER_CONFIRM_VIEW, defaultViewStackPolicy));
				return null;
			}
		};

		Task<Void> taskSwitchAU = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				Platform.runLater(() -> switchView(ABOUT_US_VIEW, defaultViewStackPolicy));
				return null;
			}
		};

		Task<Void> taskSwitchES = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				Platform.runLater(() -> switchView(FINAL_EXAM_VIEW, defaultViewStackPolicy));
				return null;
			}
		};

		Task<Void> taskSwitchNIS = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				Platform.runLater(() -> switchView(NO_INTERNET_VIEW, defaultViewStackPolicy));
				return null;
			}
		};

		addViewFactory(NO_INTERNET_VIEW, () -> {

			// Cloud image
			Image cloudIcon = new Image(getClass().getResourceAsStream("images/ic_no_internet_con_0.png"));
			ImageView noInImView = new ImageView(cloudIcon);
			noInImView.setFitWidth(350);
			noInImView.setFitHeight(263);

			// Tripple labels
			Label lblFirst = new Label("Không có kết nối mạng");
			lblFirst.setStyle(
					"-fx-font-size: 20px;\r\n" + "\r\n" + "  -fx-font-family: 'Encode Sans';" + "-fx-text-fill: #000");
			lblFirst.setWrapText(true);
			lblFirst.setAlignment(Pos.BOTTOM_CENTER);
//			lblFirst.setPadding(new Insets(5, 0, 0, 0));

			Label lblSecond = new Label("Vui lòng kiểm tra lại kết nối mạng");
			lblSecond.setStyle("-fx-font-size: 18px;\r\n" + "\r\n" + "  -fx-font-family: 'Encode Sans';"
					+ "-fx-text-fill: #B7B7B7");
			lblSecond.setWrapText(true);
			lblSecond.setAlignment(Pos.BOTTOM_CENTER);
//			lblSecond.setPadding(new Insets(5, 0, 0, 0));

			Label lblThird = new Label("NHẤN ĐỂ THỬ LẠI");
			lblThird.setStyle(
					"-fx-font-size: 18px;\r\n" + "\r\n" + "  -fx-font-family: 'Roboto';" + "-fx-text-fill: #018D44"
							+ ";\r\n" + "-fx-effect: dropshadow(one-pass-box, green, 1, 0.0, 1, 0);");
			lblThird.setWrapText(true);
			lblThird.setAlignment(Pos.BOTTOM_CENTER);
			lblThird.setPadding(new Insets(5, 0, 0, 0));
			lblThird.setFocusTraversable(true);
			lblThird.setOnMouseReleased(eventMouseReleased -> {
				loadingAndCheckConnectionAgain();

				// check GO_TO_MAIN_VIEW
				if (GO_TO_MAIN_VIEW == true) {
					MobileApplication.getInstance().switchView(MAIN_VIEW);
				}
			});
			lblThird.setOnTouchReleased(eventTouchReleased -> {
				loadingAndCheckConnectionAgain();

				// check GO_TO_MAIN_VIEW
				if (GO_TO_MAIN_VIEW == true) {
					MobileApplication.getInstance().switchView(MAIN_VIEW);
				}
			});
//			lblExplain.setPadding(new Insets(0, 0, 0, 0));

			VBox vboxMain = new VBox(20, noInImView, lblFirst, lblSecond, lblThird);
			vboxMain.setAlignment(Pos.CENTER);
			View view = new View(vboxMain) {
				@Override
				protected void updateAppBar(AppBar appBar) {
					appBar.setVisible(false);
				}
			};
			return view;
		});

		addViewFactory(UPDATE_SERVER_CONFIRM_ZERO_VIEW, () -> {

			// Image
			Image cloudIcon = new Image(getClass().getResourceAsStream("images/ic_something_went_wrong.png"));
			ImageView noInImView = new ImageView(cloudIcon);
			noInImView.setFitWidth(350);
			noInImView.setFitHeight(270.32);

			// Tripple labels
			Label lblFirst = new Label("OOPS! ĐÃ CÓ LỖI XẢY RA.");
			lblFirst.setStyle("-fx-font-size: 33px;\r\n" + "\r\n" + "  -fx-font-family: 'Oswald';"
//			+ "\r\n-fx-font-weight: bold;\r\n"
					+ ";\r\n" + "-fx-effect: dropshadow(one-pass-box, black, 1, 0.0, 1, 0);" + "-fx-text-fill: #000");
			lblFirst.setWrapText(true);
			lblFirst.setAlignment(Pos.BOTTOM_CENTER);
//			lblFirst.setPadding(new Insets(5, 0, 0, 0));

			Label lblSecond = new Label("Vui lòng thử lại sau ...");
			lblSecond.setStyle("-fx-font-size: 28px;\r\n" + "\r\n" + "  -fx-font-family: 'Grandstander';"
//					+ "\r\n-fx-font-weight: bold;\r\n"
					+ ";\r\n" + "-fx-effect: dropshadow(one-pass-box, black, 2, 0.0, 2, 0);"
					+ "-fx-text-fill: #FF2E00;");
			lblSecond.setWrapText(true);
			lblSecond.setAlignment(Pos.BOTTOM_CENTER);
//			lblSecond.setPadding(new Insets(5, 0, 0, 0));

			VBox vboxMain = new VBox(50, lblFirst, noInImView, lblSecond);
			vboxMain.setAlignment(Pos.CENTER);

			View view = new View(vboxMain) {
				@Override
				protected void updateAppBar(AppBar appBar) {
					appBar.setVisible(false);
				}
			};
			return view;
		});

		addViewFactory(UPDATE_SERVER_CONFIRM_ONE_VIEW, () -> {

			// Image
			Image cloudIcon = new Image(getClass().getResourceAsStream("images/ic_cat_error.png"));
			ImageView noInImView = new ImageView(cloudIcon);
			noInImView.setFitWidth(350);
			noInImView.setFitHeight(254.19);

			// Tripple labels
			Label lblFirst = new Label("OOPS! ĐÃ CÓ LỖI XẢY RA.");
			lblFirst.setStyle("-fx-font-size: 28px;\r\n" + "\r\n" + "  -fx-font-family: 'Sylfaen';" // Sylfaen,
																									// Plantagenet
																									// Cherokee
//						+ "\r\n-fx-font-weight: bold;\r\n" 
					+ ";\r\n" + "-fx-effect: dropshadow(one-pass-box, black, 2.5, 0.0, 2.3, 0);"
					+ "-fx-text-fill: #000");
			lblFirst.setWrapText(true);
			lblFirst.setAlignment(Pos.BOTTOM_CENTER);
//			lblFirst.setPadding(new Insets(5, 0, 0, 0));

			Label lblSecond = new Label("Vui lòng thử lại sau ...");
			lblSecond.setStyle("-fx-font-size: 30px;\r\n" + "\r\n" + "  -fx-font-family: 'Grandstander';"
//					+ "\r\n-fx-font-weight: bold;\r\n"
					+ ";\r\n" + "-fx-effect: dropshadow(one-pass-box, #94B3F2, 2.6, 0.0, 2.4, 0);"
					+ "-fx-text-fill: #A150F2;"); // 05DBF2, 7ED1F2
			lblSecond.setWrapText(true);
			lblSecond.setAlignment(Pos.BOTTOM_CENTER);
//			lblSecond.setPadding(new Insets(5, 0, 0, 0));

			VBox vboxMain = new VBox(65, lblFirst, noInImView, lblSecond);
			vboxMain.setAlignment(Pos.CENTER);
			vboxMain.setStyle("-fx-background-color: #DF9FA0;");
			View view = new View(vboxMain) {
				@Override
				protected void updateAppBar(AppBar appBar) {
					appBar.setVisible(false);
				}
			};
			return view;
		});

		addViewFactory(GET_STARTED_VIEW, () -> {

			// schedule icon
			schedule32Icon = new Image(getClass().getResourceAsStream("images/ic_schedule_96.png"));
			schedule32ImView = new ImageView(schedule32Icon);
			schedule32ImView.setFitWidth(32);
			schedule32ImView.setFitHeight(32);

			// init label title
			Label lblTitle = new Label("VNUA Scheduler");
			lblTitle.setStyle(
					"-fx-font-size: 24px;\r\n" + "\r\n" + "  -fx-font-family: 'Scada';" + "-fx-text-fill: #000");

			// init label explain
			Label lblTitleSmall = new Label("VNUA Scheduler ");
			lblTitleSmall.setStyle(
					"-fx-font-size: 17px;\r\n" + "\r\n" + "  -fx-font-family: 'Sen';" + "-fx-text-fill: #000");
			lblTitleSmall.setWrapText(true);
			lblTitleSmall.setPadding(new Insets(2, 0, 0, 0));

			Label lblExplain = new Label("cho phép người dùng ");
			lblExplain.setStyle(
					"-fx-font-size: 16px;\r\n" + "\r\n" + "  -fx-font-family: 'Bitter';" + "-fx-text-fill: #727272");
			lblExplain.setWrapText(true);
			lblExplain.setPadding(new Insets(0.35, 0, 0, 0));

			Label lblExplainZero = new Label(
					"truy cập, và kiểm tra lịch học / giảng dạy tại VNUA một cách tiện lợi, và đạt hiệu quả cao nhất, giúp người dùng có thể đưa ra kế hoạch học tập / giảng dạy tốt hơn.");
			lblExplainZero.setStyle(
					"-fx-font-size: 16px;\r\n" + "\r\n" + "  -fx-font-family: 'Bitter';" + "-fx-text-fill: #727272");
			lblExplainZero.setWrapText(true);
			lblExplainZero.setPadding(new Insets(0, 15, 0, 0));

			HBox hboxZero = new HBox(lblTitleSmall, lblExplain);
			VBox vboxZero = new VBox(hboxZero, lblExplainZero);
//			vboxZero.setMinHeight(300);
//			vboxZero.setMaxHeight(450);

			Button btnGetStarted = new JFXButton(" Truy cập ");
//			btnGetStarted.setMinWidth(150);
//			btnGetStarted.setMinHeight(40);
			btnGetStarted.setStyle("-fx-text-fill: #FEF701;\r\n" + "-fx-pref-height:40px;\r\n"
					+ "	-fx-background-color: #018D44;\r\n" + "	-fx-border-color:  #018D44;\r\n"
					+ "	-fx-border-radius: 5px;\r\n" + "	-fx-background-radius: 5px;\r\n"
					+ "	-fx-font-size: 16px;\r\n" + "	-fx-font-family: 'Doris';");
			btnGetStarted.setOnAction(action -> {
				Thread switchPR = new Thread(taskSwitchPR);
				switchPR.setDaemon(true);
				switchPR.start();
			});

			Image appScreenShot = new Image(getClass().getResourceAsStream("images/ic_schedule_96.png"));
			appScreenShotImView = new ImageView(appScreenShot);
			appScreenShotImView.setVisible(false);
			appScreenShotImView.setFitHeight(32);
			appScreenShotImView.setFitWidth(32);

			hboxButtonGS = new HBox(btnGetStarted);

			hboxButtonGS.setPadding(new Insets(10, 0, 0, lblTitle.getWidth()));

			VBox vboxMainGSV = new VBox(15, schedule32ImView, lblTitle, vboxZero, hboxButtonGS
//					, appScreenShotImView
			);
			vboxMainGSV.setPadding(new Insets(40, 10, 0, 30));

			View view = new View(vboxMainGSV) {
				@Override
				protected void updateAppBar(AppBar appBar) {
					appBar.setVisible(false);
				}
			};

			return view;
		});

		addViewFactory(INFOR_VALID_VIEW, () -> {
//			com.gluonhq.charm.glisten.control.Toast toastMessage = new com.gluonhq.charm.glisten.control.Toast(
//					""+Platform.isSupported(ConditionalFeature.VIRTUAL_KEYBOARD),
//					com.gluonhq.charm.glisten.control.Toast.LENGTH_LONG);
//			toastMessage.show();

			// Image
			Image walking24Icon = new Image(getClass().getResourceAsStream("images/ic_validate_512.png"));
			ImageView walking24ImView = new ImageView(walking24Icon);
			walking24ImView.setFitHeight(42);
			walking24ImView.setFitWidth(42);

			// init label GS
			Label lblTitle = new Label("Xác thực thông tin");
			lblTitle.setStyle(
					"-fx-font-size: 24px;\r\n" + "\r\n" + "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #000");
			lblTitle.setMinHeight(50);
			lblTitle.setMinWidth(200);

			// init label explain
			Label lblExplain = new Label("Hãy cho chúng tôi biết mã sinh viên hoặc mã giảng viên của bạn:");
			lblExplain.setStyle("-fx-font-size: 26px;\r\n" + "\r\n" + "  -fx-font-family: 'Encode Sans';"
					+ "-fx-text-fill: #565656");
			lblExplain.setMaxHeight(400);
			lblExplain.setPadding(new Insets(0, 10, 0, 0));
			lblExplain.setWrapText(true);

			TextField tfInputCode = new TextField();
			tfInputCode.setPromptText("Ví dụ: 651101");
			tfInputCode.setMinWidth(250);
			tfInputCode.setMaxHeight(40);
			tfInputCode.setFocusTraversable(false);
			tfInputCode.setStyle("-fx-prompt-text-fill: #000 ;\r\n" + "-fx-text-inner-color: #000 ;\r\n"
					+ "-fx-text-fill: #000 ;\r\n" + "-fx-font-family: 'Encode Sans';\r\n" + "\r\n"
					+ "-fx-font-size: 24px;");
			tfInputCode.setPadding(new Insets(0, 0, 0, 0));
			tfInputCode.setOnKeyReleased(new EventHandler<KeyEvent>() {

				@Override
				public void handle(KeyEvent event) {
					if (event.getCode() == KeyCode.ENTER) {
						tfInputCode.setFocusTraversable(false);

						// Validate tfInputCode content
						String tfInputCodeContentRaw = tfInputCode.getText().toString().toLowerCase().trim();
						if (tfInputCodeContentRaw.length() >= 11) {

						}

//						com.gluonhq.charm.glisten.control.Toast toastMessage = new com.gluonhq.charm.glisten.control.Toast(
//								"Touch ENTER.", com.gluonhq.charm.glisten.control.Toast.LENGTH_LONG);
//						toastMessage.show();

						// request focus on another views
						lblExplain.requestFocus();
					} else if (event.getCode() == KeyCode.CIRCUMFLEX || event.getCode() == KeyCode.DEAD_ACUTE
							|| event.getCode() == KeyCode.END || event.getCode() == KeyCode.ESCAPE
							|| event.getCode() == KeyCode.EJECT_TOGGLE || event.getCode() == KeyCode.TRACK_PREV
							|| event.getCode() == KeyCode.KATAKANA) {
						com.gluonhq.charm.glisten.control.Toast toastMessage = new com.gluonhq.charm.glisten.control.Toast(
								"Touch CLEAR.", com.gluonhq.charm.glisten.control.Toast.LENGTH_LONG);
						toastMessage.show();

						// request focus on another views
						lblExplain.requestFocus();
					}
				}

			});

			// image delete all
			Image deleteAllIcon36 = new Image(getClass().getResourceAsStream("images/ic_delete_all_48.png"));
			deleteAll36ImView = new ImageView(deleteAllIcon36);
			deleteAll36ImView.setFitWidth(36);
			deleteAll36ImView.setFitHeight(36);
			deleteAll36ImView.setVisible(false);
			HBox hboxDeleteAll36ImView = new HBox(deleteAll36ImView);
			hboxDeleteAll36ImView.setPadding(new Insets(2, 40, 0, 0));

			// init hboxSubTF
			HBox hboxSubTF = new HBox(1, tfInputCode, hboxDeleteAll36ImView);

			// handle delete all TF content when TF on click
			deleteAll36ImView.setOnMouseClicked(e -> {
				tfInputCode.setText("");
				tfInputCode.requestFocus();
			});

			deleteAll36ImView.setOnTouchReleased(e -> {
				tfInputCode.setText("");
				tfInputCode.requestFocus();
			});

			hboxDeleteAll36ImView.setOnTouchReleased(e -> {
				tfInputCode.setText("");
				tfInputCode.requestFocus();
			});

			hboxDeleteAll36ImView.setOnMouseClicked(e -> {
				tfInputCode.setText("");
				tfInputCode.requestFocus();
			});

//			setOnPressedAndHolded(deleteAll36ImView, Duration.seconds(1), e -> {
//				tfInputCode.setText(tfInputCode.getText().substring(0, tfInputCode.getText().length()-1));
//				tfInputCode.requestFocus();
//			});
//			
//			setOnPressedAndHolded(hboxDeleteAll36ImView, Duration.seconds(1), e -> {
//				tfInputCode.setText(tfInputCode.getText().substring(0, tfInputCode.getText().length()-1));
//				tfInputCode.requestFocus();
//			});

			// handle visibility of delete all icon when length of TF > 0
			Thread taskShowDeleteAllIconThread = new Thread(() -> {
				while (!Thread.currentThread().isInterrupted()) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {

							if (tfInputCode.getText().length() > 0) {
								deleteAll36ImView.setVisible(true);

//								com.gluonhq.charm.glisten.control.Toast toastMessage = new com.gluonhq.charm.glisten.control.Toast(
//										""+Platform.isSupported(ConditionalFeature.VIRTUAL_KEYBOARD),
//										com.gluonhq.charm.glisten.control.Toast.LENGTH_LONG);
//								toastMessage.show();

								// set key pressed listener
								tfInputCode.setOnKeyPressed(new EventHandler<KeyEvent>() {

									@Override
									public void handle(KeyEvent event) {
										if (event.getCode() == KeyCode.BACK_SPACE || event.getCode() == KeyCode.DELETE
												|| event.getCode() == KeyCode.CANCEL
												|| event.getCode() == KeyCode.CLEAR) {
//											com.gluonhq.charm.glisten.control.Toast toastMessage = new com.gluonhq.charm.glisten.control.Toast(
//													""+Platform.isSupported(ConditionalFeature.VIRTUAL_KEYBOARD),
//													com.gluonhq.charm.glisten.control.Toast.LENGTH_LONG);
//											toastMessage.show();
										}
									}
								});

							} else {
								deleteAll36ImView.setVisible(false);
							}
						}
					});
					try {
						Thread.sleep(2);
					} catch (InterruptedException exc) {
						break;
					}
				}
			});
			taskShowDeleteAllIconThread.setDaemon(true);
			taskShowDeleteAllIconThread.start();

			Label lblWarning = new Label("Trường thông tin trên là bắt buộc");
			lblWarning
					.setStyle("-fx-text-fill: red;" + "-fx-font-family: 'Grandstander';\r\n" + "-fx-font-size: 16px;");
			lblWarning.setPadding(new Insets(10, 0, 0, 0));
			lblWarning.setVisible(false);

			// customize fade in Animation and fade out Animation
			fadeIn.setNode(lblWarning);
			fadeIn.setFromValue(0.0);
			fadeIn.setToValue(1.0);
			fadeIn.setCycleCount(2);
			fadeIn.setAutoReverse(false);

			fadeOut.setNode(lblWarning);
			fadeOut.setFromValue(1.0);
			fadeOut.setToValue(0.0);
			fadeOut.setCycleCount(1);
			fadeOut.setAutoReverse(true);

			VBox vboxInputCode = new VBox(hboxSubTF, lblWarning);
			vboxInputCode.setPadding(new Insets(20, 0, 0, 0));

			Timeline blinker = createBlinker(lblWarning);
			blinker.setOnFinished(event -> lblWarning.setVisible(false));
			FadeTransition fader = createFader(lblWarning);

			SequentialTransition blinkThenFade = new SequentialTransition(lblWarning, blinker, fader);

			VBox vboxMainGSV = new VBox(15, walking24ImView, lblTitle, lblExplain, vboxInputCode);
			vboxMainGSV.setPadding(new Insets(40, 10, 0, 30));

			// unfocus TF by click in the background
			vboxMainGSV.setOnTouchReleased(new EventHandler<TouchEvent>() {

				@Override
				public void handle(TouchEvent event) {
//					if((!event.getTouchPoint().belongsTo(tfInputCode) || !tfInputCode.isFocused() || !tfInputCode.isFocusTraversable() ) && 
//							(event.getTouchPoint().belongsTo(lblWarning) 
//									|| event.getTouchPoint().belongsTo(lblWarning)
//									|| event.getTouchPoint().belongsTo(lblExplain)
//									|| event.getTouchPoint().belongsTo(lblTitle)
//									|| event.getTouchPoint().belongsTo(walking24ImView)
//									|| event.getTouchPoint().belongsTo(hboxDeleteAll36ImView)
//									|| event.getTouchPoint().belongsTo(deleteAll36ImView)
//									)) {
					if (tfInputCode.isFocusTraversable() || tfInputCode.isFocused()) {
//						tfInputCode.setFocusTraversable(false);
//						com.gluonhq.charm.glisten.control.Toast toastMessage = new com.gluonhq.charm.glisten.control.Toast(
//								"Touch Out the Screen.",
//								com.gluonhq.charm.glisten.control.Toast.LENGTH_LONG);
//						toastMessage.show();

						// turn off the Soft Keyboard

						// request focus on another views
//						lblExplain.requestFocus();
					}
				}
			});

			// Floating Search Button
			FloatingActionButton fab = new FloatingActionButton(MaterialDesignIcon.ARROW_FORWARD.text, e -> {

				// valid input code
				String inputCodeContent = tfInputCode.getText().toString().toUpperCase().trim();
				if (inputCodeContent == null || inputCodeContent.equals("")) {
					if (!lblWarning.isVisible()) {
						lblWarning.setVisible(true);
						fadeIn.playFromStart();
					}
				} else {

					// Valid and set new Input Code

					// Check Whether or not Student or Lecturer
					String firstChar = "" + inputCodeContent.charAt(0);

					// Regular Expression Initiation
					Pattern patternValidInputCode = Pattern.compile("[0-9a-zA-Z]+"),
							patternStudent = Pattern.compile("[0-9]+"), patternLecturer = Pattern.compile("[a-zA-Z]+");
					Matcher matcher = patternValidInputCode.matcher(inputCodeContent),
							matcherStudent = patternStudent.matcher(firstChar),
							matcherLecturer = patternLecturer.matcher(firstChar);

					if (inputCodeContent.length() >= 11) {
						// Too much character
						LOGGER.log(Level.INFO, "Special Character Check.");
						Toast toastConfirm = new Toast("Too much Character Check.", Toast.LENGTH_LONG);
						toastConfirm.show();

						// change the lblWarning content and start the Animation fading in
						if (!lblWarning.isVisible()) {
							lblWarning.setText("Mã nhập vào không đúng. Vui lòng nhập lại.");
							lblWarning.setVisible(true);
							fadeIn.playFromStart();
						} else {
							lblWarning.setVisible(false);
							lblWarning.setText("Mã nhập vào không đúng. Vui lòng nhập lại.");
							lblWarning.setVisible(true);
							fadeIn.playFromStart();
						}

						// "Your input code is not valid. Please try again!"
					} else if (!matcher.matches()) {
						// Contain Special Character
						LOGGER.log(Level.INFO, "Special Character Check.");
						Toast toastConfirm = new Toast("Special Character Check.", Toast.LENGTH_LONG);
						toastConfirm.show();

						// change the lblWarning content and start the Animation fading in
						if (!lblWarning.isVisible()) {
							lblWarning.setText("Mã nhập vào không đúng. Vui lòng nhập lại.");
							lblWarning.setVisible(true);
							fadeIn.playFromStart();
						} else {
							lblWarning.setVisible(false);
							lblWarning.setText("Mã nhập vào không đúng. Vui lòng nhập lại.");
							lblWarning.setVisible(true);
							fadeIn.playFromStart();
						}

					} else {

						// Check Wrong and Right

						// init menu
						menu = new Menu();

						// take the default input code to reverse
						String reverseDefaultInputCode = "CNP02";

						try {
							// set input code to valid
							menu.setInputCode(inputCodeContent);
							menu.resetList();
							resultLoadMenu = menu.handleJsoupAndJSON();
							resultLoadMenuFE = menu.handleJsoupAndJSONFinalExam();

							if (resultLoadMenu.equals("WRONG_INPUT_CODE")) {
								LOGGER.log(Level.INFO, "Wrong credentials. ---> Wrong input code.");

								// flagging WRONG_INPUT_CODE
								WRONG_INPUT_CODE = true;

								// reverse the default input code
								menu.setInputCode(reverseDefaultInputCode);

								// change the lblWarning content and start the Animation fading in
								if (!lblWarning.isVisible()) {
									lblWarning.setText("Mã nhập vào không đúng. Vui lòng nhập lại.");
									lblWarning.setVisible(true);
									fadeIn.playFromStart();
								} else {
									lblWarning.setVisible(false);
									lblWarning.setText("Mã nhập vào không đúng. Vui lòng nhập lại.");
									lblWarning.setVisible(true);
									fadeIn.playFromStart();
								}
							} else {
								// draw the flag WRONG_INPUT_CODE
								WRONG_INPUT_CODE = false;

								// Check and Notify to User
								if (matcherStudent.matches()) {
									LOGGER.log(Level.INFO, "Student ALERT ALERT ALERT!!!!!!");
									Toast toastConfirm = new Toast("Student !!!!.", Toast.LENGTH_LONG);
									toastConfirm.show();

									// flag STUDENT_NOTIFY
									STUDENT_NOTIFY = true;

								} else {
									LOGGER.log(Level.INFO, "Lecturer ALERT ALERT ALERT!!!!!!");
									Toast toastConfirm = new Toast("Lecturer !!!!.", Toast.LENGTH_LONG);
									toastConfirm.show();
								}
							}
						} catch (Exception ex) {
							if (ex instanceof IndexOutOfBoundsException) {
								ex.printStackTrace();

								LOGGER.log(Level.INFO, "Wrong credentials. ---> Wrong input code.");

								// flagging WRONG_INPUT_CODE
								WRONG_INPUT_CODE = true;

//								LOGGER.log(Level.INFO, "Server update recently. ---> Wait a minute.");
//
//								// flag UPDATE_SERVER_CONFIRM
//								UPDATE_SERVER_CONFIRM = true;

								// reverse the default input code
								menu.setInputCode(reverseDefaultInputCode);

								// change the lblWarning content and start the Animation fading in
								if (!lblWarning.isVisible()) {
									lblWarning.setText("Mã nhập vào không đúng. Vui lòng nhập lại.");
									lblWarning.setVisible(true);
									fadeIn.playFromStart();
								} else {
									lblWarning.setVisible(false);
									lblWarning.setText("Mã nhập vào không đúng. Vui lòng nhập lại.");
									lblWarning.setVisible(true);
									fadeIn.playFromStart();
								}
							} else if (ex instanceof IOException) {
								ex.printStackTrace();

								LOGGER.log(Level.INFO, "No Internet Connection.");

								// flag NO_INTERNET_CON
								NO_INTERNET_CON = true;

								// flag INPUT_CODE_RESTORE
								INPUT_CODE_RESTORE = inputCodeContent;
							} else if (ex instanceof RuntimeException) {
								LOGGER.log(Level.INFO, "Wrong credentials. ---> Wrong input code.");

								// flagging WRONG_INPUT_CODE
								WRONG_INPUT_CODE = true;
							}
						}

						// set up in4
						currentWeek = menu.getStudyWeek() + "";
						currentDOW = menu.getCurrentEngDayOfWeek();

						if (WRONG_INPUT_CODE == false) {
							// check flag NO_INTERNET_CON
							if (NO_INTERNET_CON == false && UPDATE_SERVER_CONFIRM == false) {
								MobileApplication.getInstance().switchView(MAIN_VIEW);
							} else if (NO_INTERNET_CON == true) {
								MobileApplication.getInstance().switchView(NO_INTERNET_VIEW);
							} else if (UPDATE_SERVER_CONFIRM == true) {
								MobileApplication.getInstance().switchView(UPDATE_SERVER_CONFIRM_ONE_VIEW);
							}
						}
					}
				}
			});

			View view = new View(vboxMainGSV) {
				@Override
				protected void updateAppBar(AppBar appBar) {
					appBar.setVisible(false);
				}
			};
			fab.showOn(view);
			return view;
		});

		addViewFactory(PER_REQUEST_VIEW, () -> {

			// Images
			Image log24Icon = new Image(getClass().getResourceAsStream("images/ic_log_100.png"));
			ImageView log24ImView = new ImageView(log24Icon);
			log24ImView.setFitHeight(28);
			log24ImView.setFitWidth(28);

			Image storage28Icon = new Image(getClass().getResourceAsStream("images/ic_storage_100.png"));
			ImageView storage28ImView = new ImageView(storage28Icon);
			storage28ImView.setFitHeight(28);
			storage28ImView.setFitWidth(28);

			Image wifi28Icon = new Image(getClass().getResourceAsStream("images/ic_wifi_100.png"));
			ImageView wifi28ImView = new ImageView(wifi28Icon);
			wifi28ImView.setFitHeight(28);
			wifi28ImView.setFitWidth(28);

			Image notification32Icon = new Image(getClass().getResourceAsStream("images/ic_notifi_100.png"));
			ImageView notification32ImView = new ImageView(notification32Icon);
			notification32ImView.setFitHeight(28);
			notification32ImView.setFitWidth(28);

			// Floating Search Button
			FloatingActionButton fab = new FloatingActionButton(MaterialDesignIcon.ARROW_FORWARD.text, e -> {
				Thread switchIV = new Thread(taskSwitchIV);
				switchIV.setDaemon(true);
				switchIV.start();
			});

			// init label GS
			Label lblTitle = new Label("Cấp quyền truy cập");
			lblTitle.setStyle(
					"-fx-font-size: 24px;\r\n" + "\r\n" + "  -fx-font-family: 'Dosis';" + "-fx-text-fill: #000");
			lblTitle.setMinHeight(50);
			lblTitle.setMinWidth(200);

			// init label explain
			Label lblExplain = new Label(
					"Ứng dụng cần được người dùng cung cấp một số quyền sau đây để có thể hoạt động một cách tốt nhất");
			lblExplain.setStyle(
					"-fx-font-size: 16px;\r\n" + "\r\n" + "  -fx-font-family: 'Quicksand';" + "-fx-text-fill: #676767");
			lblExplain.setMinHeight(30);
			lblExplain.setWrapText(true);
			lblExplain.setPadding(new Insets(0, 10, 0, 0));

			// init 2 labels and one vboxLbl
			Label lblWifi = new Label("Quyền truy cập mạng");
			lblWifi.setStyle(
					"-fx-font-size: 20px;\r\n" + "\r\n" + "  -fx-font-family: 'Encode Sans';" + "-fx-text-fill: #000");
			lblWifi.setWrapText(true);

			Label lblWifiEx = new Label("Cung cấp kết nối Internet thông qua Wifi, 3G, 4G, hoặc 5G");
			lblWifiEx.setStyle(
					"-fx-font-size: 16px;\r\n" + "\r\n" + "  -fx-font-family: 'Bitter';" + "-fx-text-fill: #676767");
			lblWifiEx.setMinHeight(20);
			lblWifiEx.setWrapText(true);

			VBox vboxLblOne = new VBox(5, lblWifi, lblWifiEx);

			Label lblStorage = new Label("Quyền truy cập bộ nhớ lưu trữ");
			lblStorage.setStyle(
					"-fx-font-size: 20px;\r\n" + "\r\n" + "  -fx-font-family: 'Encode Sans';" + "-fx-text-fill: #000");
			lblStorage.setWrapText(true);

			Label lblStorageEx = new Label("Cung cấp quyền đọc/ghi các tệp, thư mục và dữ liệu lưu trữ");
			lblStorageEx.setStyle(
					"-fx-font-size: 16px;\r\n" + "\r\n" + "  -fx-font-family: 'Bitter';" + "-fx-text-fill: #676767");
			lblStorageEx.setMinHeight(20);
			lblStorageEx.setWrapText(true);

			VBox vboxLblTwo = new VBox(5, lblStorage, lblStorageEx);

			Label lblNotification = new Label("Quyền truy cập và quản lý thông báo");
			lblNotification.setStyle(
					"-fx-font-size: 20px;\r\n" + "\r\n" + "  -fx-font-family: 'Encode Sans';" + "-fx-text-fill: #000");
			lblNotification.setWrapText(true);

			Label lblNotificationEx = new Label("Cung cấp quyền truy cập và quản lý các thông báo trong hệ thống");
			lblNotificationEx.setStyle(
					"-fx-font-size: 16px;\r\n" + "\r\n" + "  -fx-font-family: 'Bitter';" + "-fx-text-fill: #676767");
			lblNotificationEx.setMinHeight(20);
			lblNotificationEx.setWrapText(true);

			VBox vboxLblThree = new VBox(5, lblNotification, lblNotificationEx);

			// init double hbox and vboxSub
			HBox hboxOneIm = new HBox(wifi28ImView), hboxTwoIm = new HBox(storage28ImView),
					hboxThreeIm = new HBox(notification32ImView);
			hboxOneIm.setPadding(new Insets(13, 25, 13, 10));
			hboxTwoIm.setPadding(new Insets(13, 25, 13, 10));
			hboxThreeIm.setPadding(new Insets(13, 25, 13, 10));
			HBox hboxOne = new HBox(hboxOneIm, vboxLblOne), hboxTwo = new HBox(hboxTwoIm, vboxLblTwo),
					hboxThree = new HBox(hboxThreeIm, vboxLblThree);

			VBox vBoxSub = new VBox(30, hboxOne 
//					, hboxTwo
//					, hboxThree
					);
			vBoxSub.setPadding(new Insets(30, 0, 0, 0));

			VBox vboxMainGSV = new VBox(15, log24ImView, lblTitle, lblExplain, vBoxSub);
			vboxMainGSV.setPadding(new Insets(40, 10, 0, 30));

			View view = new View(vboxMainGSV) {
				@Override
				protected void updateAppBar(AppBar appBar) {
					appBar.setVisible(false);
				}
			};
			fab.showOn(view);
			return view;
		});

		addViewFactory(PER_CONFIRM_VIEW, () -> {

			// Images
			Image like24Icon = new Image(getClass().getResourceAsStream("images/ic_like_500.png"));
			ImageView like24ImView = new ImageView(like24Icon);
			like24ImView.setFitHeight(32);
			like24ImView.setFitWidth(32);

			Image setDoneIcon = new Image(getClass().getResourceAsStream("images/ic_set_done.png"));
			ImageView setDoneImView = new ImageView(setDoneIcon);

			// Floating Search Button
			FloatingActionButton fab = new FloatingActionButton(MaterialDesignIcon.ARROW_FORWARD.text, e -> {

				// check flag NO_INTERNET_CON
				if (NO_INTERNET_CON == false && UPDATE_SERVER_CONFIRM == false) {
					MobileApplication.getInstance().switchView(MAIN_VIEW);
				} else if (NO_INTERNET_CON == true) {
					MobileApplication.getInstance().switchView(NO_INTERNET_VIEW);
				} else if (UPDATE_SERVER_CONFIRM == true) {
					MobileApplication.getInstance().switchView(UPDATE_SERVER_CONFIRM_ONE_VIEW);
				}
			});

			// init label GS
			Label lblTitle = new Label("Hoàn tất!");
			lblTitle.setStyle(
					"-fx-font-size: 24px;\r\n" + "\r\n" + "  -fx-font-family: 'Manrope';" + "-fx-text-fill: #000");
			lblTitle.setMinHeight(50);
			lblTitle.setMinWidth(200);

			// init label explain
			Label lblExplain = new Label(
					"Chân thành cám ơn bạn vì đã sử dụng VNUA Scheduler. Hy vọng VNUA Scheduler sẽ làm bạn hài lòng.");
			lblExplain.setStyle("-fx-font-size: 18px;\r\n" + "\r\n" + "  -fx-font-family: 'Libre Franklin';"
					+ "-fx-text-fill: #676767");
			lblExplain.setMinHeight(60);
			lblExplain.setWrapText(true);
			lblExplain.setPadding(new Insets(0, 10, 0, 0));

			// init layout contain Set Done Image View
			HBox hboxContainImView = new HBox(setDoneImView);
			hboxContainImView.setAlignment(Pos.CENTER);
			hboxContainImView.setPadding(new Insets(0, 20, 0, 0));
			hboxContainImView.setPrefHeight(350);

			VBox vboxMainGSV = new VBox(15, like24ImView, lblTitle, lblExplain, hboxContainImView);
			vboxMainGSV.setPadding(new Insets(40, 10, 0, 30));

			View view = new View(vboxMainGSV) {
				@Override
				protected void updateAppBar(AppBar appBar) {
					appBar.setVisible(false);
				}
			};
			fab.showOn(view);
			return view;
		});

		addViewFactory(FINAL_EXAM_VIEW, () -> {
			// Floating Search Button
			FloatingActionButton fab = new FloatingActionButton(MaterialDesignIcon.ARROW_BACK.text, e -> {
				MobileApplication.getInstance().switchView(MAIN_VIEW);
			});

			JFXTextField tfCourseName = new JFXTextField();
			tfCourseName.setPromptText("Tên môn học");
			tfCourseName.setEditable(true);
			tfCourseName.setFocusTraversable(false);
			tfCourseName.setMinWidth(175);
			tfCourseName.setMinHeight(35);
			tfCourseName.setStyle("-fx-prompt-text-fill: #ADFF2F ;\r\n" + "-fx-text-inner-color: #ADFF2F ;\r\n"
					+ "-fx-text-fill: #ADFF2F ;\r\n" + "-fx-font-family: 'Libre Franklin';\r\n"
					+ "-fx-font-size: 16px;");
			tfCourseName.setPadding(new Insets(0, 0, 0, 0));
			tfCourseName.setOnKeyReleased(new EventHandler<KeyEvent>() {

				@Override
				public void handle(KeyEvent event) {
					if (event.getCode() == KeyCode.ENTER) {
						tfCourseName.setFocusTraversable(false);

						// Validate tfInputCode content
						String tfInputCodeContentRaw = tfCourseName.getText().toString().toLowerCase().trim();
						if (tfInputCodeContentRaw.length() >= 11) {

						}

//						com.gluonhq.charm.glisten.control.Toast toastMessage = new com.gluonhq.charm.glisten.control.Toast(
//								"Touch ENTER.", com.gluonhq.charm.glisten.control.Toast.LENGTH_LONG);
//						toastMessage.show();

						// request focus on another views
//						fab.requestFocus();
					} else if (event.getCode() == KeyCode.CIRCUMFLEX || event.getCode() == KeyCode.DEAD_ACUTE
							|| event.getCode() == KeyCode.END || event.getCode() == KeyCode.ESCAPE
							|| event.getCode() == KeyCode.EJECT_TOGGLE || event.getCode() == KeyCode.TRACK_PREV
							|| event.getCode() == KeyCode.KATAKANA) {
						com.gluonhq.charm.glisten.control.Toast toastMessage = new com.gluonhq.charm.glisten.control.Toast(
								"Touch CLEAR.", com.gluonhq.charm.glisten.control.Toast.LENGTH_LONG);
						toastMessage.show();

						// request focus on another views
//						lblExplain.requestFocus();
					}
				}

			});

			// image delete all
			// Normal Search Icon
			Image searchIcon24ES = new Image(getClass().getResourceAsStream("images/ic_search_24_green.png"));
			ImageView search24ImViewES = new ImageView(searchIcon24ES);

			Image searchIcon36ES = new Image(getClass().getResourceAsStream("images/ic_search_36_green.png"));
			ImageView search36ImViewES = new ImageView(searchIcon36ES);
			search36ImViewES.setFitWidth(24);
			search36ImViewES.setFitHeight(24);

			Image deleteAllIcon24ES = new Image(getClass().getResourceAsStream("images/ic_delete_all_24.png"));
			ImageView deleteAll24ImViewES = new ImageView(deleteAllIcon24ES);

			Image deleteAllIcon36ES = new Image(getClass().getResourceAsStream("images/ic_delete_all_36.png"));
			ImageView deleteAll36ImViewES = new ImageView(deleteAllIcon36ES);
			deleteAll36ImViewES.setFitWidth(24);
			deleteAll36ImViewES.setFitHeight(24);

			JFXButton btnSubmitES = new JFXButton("Xác nhận");
			btnSubmitES.setStyle("-fx-font-family: 'Doris';");
			btnSubmitES.setMinWidth(120);
			btnSubmitES.setMinHeight(35);
			btnSubmitES.setMaxWidth(140);
			btnSubmitES.setMaxHeight(40);

			Rectangle rectangleES = new Rectangle(250, 40);
			rectangleES.setFill(Color.rgb(161, 161, 160)); // B4A4C4 , (95, 108, 146)
			// set rounded
			rectangleES.setArcHeight(20);
			rectangleES.setArcWidth(20);

			HBox hboxSearchTFES = new HBox(tfCourseName);
			hboxSearchTFES.setPadding(new Insets(3, 10, 5, 35));

			HBox hboxSearchIconES = new HBox(search36ImViewES);
			hboxSearchIconES.setPadding(new Insets(8, 10, 0, 8));

			HBox hboxDeleteAllIconES = new HBox(deleteAll36ImViewES);
			hboxDeleteAllIconES.setPadding(new Insets(8, 10, 0, 213));
			hboxDeleteAllIconES.setVisible(false);

			// handle delete all TF content when TF on click
			deleteAll36ImViewES.setOnMouseClicked(e -> {
				tfCourseName.setText("");
				tfCourseName.requestFocus();
			});

			deleteAll36ImViewES.setOnTouchReleased(e -> {
				tfCourseName.setText("");
				tfCourseName.requestFocus();
			});

			hboxDeleteAllIconES.setOnTouchReleased(e -> {
				tfCourseName.setText("");
				tfCourseName.requestFocus();
			});

			hboxDeleteAllIconES.setOnMouseClicked(e -> {
				tfCourseName.setText("");
				tfCourseName.requestFocus();
			});

			// handle visibility of delete all icon when length of TF > 0
			Thread taskShowDeleteAllIconThreadES = new Thread(() -> {
				while (!Thread.currentThread().isInterrupted()) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {

							if (tfCourseName.getText().length() > 0) {
								hboxDeleteAllIconES.setVisible(true);

								// set key pressed listener
								tfCourseName.setOnKeyReleased(new EventHandler<KeyEvent>() {

									@Override
									public void handle(KeyEvent event) {
										if (event.getCode() == KeyCode.ENTER) {
											// unfocus and disable the Soft Keyboard
											rectangleES.requestFocus();
										}
									}
								});

							} else {
								hboxDeleteAllIconES.setVisible(false);
							}
						}
					});
					try {
						Thread.sleep(2);
					} catch (InterruptedException exc) {
						break;
					}
				}
			});
			taskShowDeleteAllIconThreadES.setDaemon(true);
			taskShowDeleteAllIconThreadES.start();

			Pane paneSearchTextFieldES = new Pane(rectangleES, hboxSearchIconES, hboxSearchTFES, hboxDeleteAllIconES);

			// handle delete all TF content when TF on click
			deleteAll36ImViewES.setOnMouseClicked(e -> {
				tfCourseName.setText("");
				tfCourseName.requestFocus();
			});

			deleteAll36ImViewES.setOnTouchReleased(e -> {
				tfCourseName.setText("");
				tfCourseName.requestFocus();
			});

			hboxDeleteAllIconES.setOnTouchReleased(e -> {
				tfCourseName.setText("");
				tfCourseName.requestFocus();
			});

			hboxDeleteAllIconES.setOnMouseClicked(e -> {
				tfCourseName.setText("");
				tfCourseName.requestFocus();
			});

			HBox hboxSearchTextFieldES = new HBox(10, paneSearchTextFieldES, btnSubmitES);
			hboxSearchTextFieldES.setPadding(new Insets(10, 10, 8, 10));

			Label lblWarningES = new Label("Trường thông tin trên là bắt buộc");
			lblWarningES
					.setStyle("-fx-text-fill: red;" + "-fx-font-family: 'Grandstander';\r\n" + "-fx-font-size: 16px;");
			lblWarningES.setPadding(new Insets(10, 0, 0, 0));
			lblWarningES.setVisible(false);

			// customize fade in Animation and fade out Animation
			FadeTransition fadeInES = new FadeTransition();
			fadeInES.setNode(lblWarningES);
			fadeInES.setFromValue(0.0);
			fadeInES.setToValue(1.0);
			fadeInES.setCycleCount(2);
			fadeInES.setAutoReverse(false);

			FadeTransition fadeOutES = new FadeTransition();
			fadeOutES.setNode(lblWarningES);
			fadeOutES.setFromValue(1.0);
			fadeOutES.setToValue(0.0);
			fadeOutES.setCycleCount(1);
			fadeOutES.setAutoReverse(true);

			VBox vboxInputCodeES = new VBox(hboxSearchTextFieldES, lblWarningES);
//			vboxInputCodeES.setPadding(new Insets(0, 0, 0, 0));

			Timeline blinkerES = createBlinker(lblWarningES);
			blinkerES.setOnFinished(event -> lblWarningES.setVisible(false));
			FadeTransition faderES = createFader(lblWarningES);

			SequentialTransition blinkThenFadeES = new SequentialTransition(lblWarningES, blinkerES, faderES);

			VBox vboxMainGSVES = new VBox(vboxInputCodeES);
//			vboxMainGSVES.setPadding(new Insets(0, 10, 0, 0));

			View view = new View(vboxMainGSVES) {
				@Override
				protected void updateAppBar(AppBar appBar) {
					appBar.setVisible(false);
				}
			};
			fab.showOn(view);
			return view;
		});

		addViewFactory(ABOUT_US_VIEW, () -> {
			// Floating Search Button
			FloatingActionButton fab = new FloatingActionButton(MaterialDesignIcon.ARROW_BACK.text, e -> {
				MobileApplication.getInstance().switchView(MAIN_VIEW);
			});
			// init label GS
			Label lblTitle = new Label("VNUA Scheduler");
			lblTitle.setStyle(
					"-fx-font-size: 25px;\r\n" + "\r\n" + "  -fx-font-family: 'Cabin';" + "-fx-text-fill: #E2DFA2;-fx-padding: 0 0 0 50;");

			// init label explain
			Label lblExplain = new Label(
					"Cung cấp cho người dùng giải pháp tốt hơn để tra cứu TKB và lịch thi, đặc biệt dành cho sinh viên / cán bộ giảng viên của Học viện Nông nghiệp Việt Nam.");
			lblExplain.setStyle(
					"-fx-font-size: 20px;\r\n" + "\r\n" + "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #C7DB00");
			lblExplain.setWrapText(true);

			Label lblExplainMore = new Label(
					"Chúc bạn đạt hiệu quả công việc với ứng dụng tra cứu TKB, Lịch thi.");
			lblExplainMore.setStyle(
					"-fx-font-size: 20px;\r\n" + "\r\n" + "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #C7DB00");
			lblExplainMore.setWrapText(true);

			Label lblExplainMoreMore = new Label("VNUA Scheduler được phát triển bởi nhóm tác giả:");
			lblExplainMoreMore.setStyle(
					"-fx-font-size: 20px;\r\n" + "\r\n" + "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #F5CA99");
			lblExplainMoreMore.setWrapText(true);

			Label lblExplainMoreMoreM = new Label(
					"- Nhóm sinh viên K62CNPM: Đỗ Văn Minh, Đặng Đức Mạnh, và Lê Trung Tiến Bình.");
			lblExplainMoreMoreM.setStyle(
					"-fx-font-size: 20px;\r\n" + "\r\n" + "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #FFDB5C");
			lblExplainMoreMoreM.setWrapText(true);

			Label lblExplainMoreMoreDM = new Label(
					"- Hướng dẫn bởi: GV. Trần Trung Hiếu - Bộ môn Công nghệ Phần mềm - Khoa Công nghệ thông tin - Học viện Nông nghiệp Việt Nam.");
			lblExplainMoreMoreDM.setStyle(
					"-fx-font-size: 20px;\r\n" + "\r\n" + "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #FFDB5C");
			lblExplainMoreMoreDM.setWrapText(true);

			VBox vboxMainGSV = new VBox(15, lblTitle, lblExplain, lblExplainMore, lblExplainMoreMore,
					lblExplainMoreMoreM, lblExplainMoreMoreDM);
			vboxMainGSV.setPadding(new Insets(40, 20, 0, 30));
			vboxMainGSV.setStyle("-fx-background-color:#917E60;");
			
			ScrollPane scrollPaneLabel = new ScrollPane(vboxMainGSV);
			
			View view = new View(vboxMainGSV) {
				@Override
				protected void updateAppBar(AppBar appBar) {
					appBar.setVisible(false);
				}
			};

			fab.showOn(view);
			return view;
		});

		addViewFactory(MAIN_VIEW, () -> {
			// Check Internet Connection

			// Old way
//			Process processCheckInCo;
//			try {
//				processCheckInCo = Runtime.getRuntime().exec("ping http://daotao.vnua.edu.vn");
//				int x = processCheckInCo.waitFor();
//
//				if (x == 0) {
//					// Has Internet Connection
//					LOGGER.log(Level.INFO, "Good Internet Connection.");
//
//					NO_INTERNET_CON = false;
//				} else {
//					// No Internet Connection
//
//					LOGGER.log(Level.INFO, "No Internet Connection.");
//
//					NO_INTERNET_CON = true;
//				}
//
//			} catch (IOException | InterruptedException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}

			// Gluon Services
			Services.get(ConnectivityService.class).ifPresent(service -> {
				boolean connected = service.isConnected();
				if (connected == true) {
					NO_INTERNET_CON = false;
					LOGGER.log(Level.INFO, "Good Internet Connection.");
				} else {
					NO_INTERNET_CON = true;
					LOGGER.log(Level.INFO, "No Internet Connection.");
				}
			});

			if (NO_INTERNET_CON == true) {
				MobileApplication.getInstance().switchView(NO_INTERNET_VIEW);
			} else {
				// Floating Search Button
				FloatingActionButton fab = new FloatingActionButton(MaterialDesignIcon.SEARCH.text, e -> {
					Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
					AppBar appBarCurrent = MobileApplication.getInstance().getAppBar();
					int screenWidth = (int) bounds.getWidth(), screenHeight = (int) bounds.getHeight();
					int halfScreenWidthPosition = screenWidth / 2,
							scheduleImageViewWidth = (int) scheduleIcon.getWidth(),
							titleAppBarWidth = (int) appBarCurrent.getTitle().getBoundsInParent().getWidth(),
							vnuaLogoIconWidth = (int) vnuaLogoIcon.getWidth(),
							halfTotalWidthIAL = (scheduleImageViewWidth + 5 + titleAppBarWidth) / 2,
							startPointImagePosition = halfScreenWidthPosition - halfTotalWidthIAL,
							startPointTitlePosition = startPointImagePosition + 5 + scheduleImageViewWidth,
							textFieldWidthCal = screenWidth - 423;

//									appBarCurrent.getTitle().setTranslateX(startPointTitlePosition);
//									appBarCurrent.getActionItems().get(0).setTranslateX(startPointImagePosition-screenWidth);
//									appBarCurrent.getNavIcon().setTranslateX(10);
//				com.gluonhq.charm.glisten.control.Toast toastMessage = new com.gluonhq.charm.glisten.control.Toast(
//						"Text field width: " + textFieldWidthCal + ", SW: " + screenWidth + ", SH: " + screenHeight,
//						com.gluonhq.charm.glisten.control.Toast.LENGTH_LONG);
//				toastMessage.show();

					// roll back to hBoxSearchTextField
//						scrollPaneMain.layout();
					scrollPaneMain.setVvalue(0);

					// focus on Text Field
					tfStuAndLecCode.requestFocus();
					tfStudentInputCode.requestFocus();
				});

//			    Group rootGroupTabPane = new Group();
//			    rootGroupTabPane.getChildren().addAll(tabPane);
//				
				tfStuAndLecCode = new JFXTextField();
				tfStuAndLecCode.setPromptText("Mã sinh viên / giảng viên");
				tfStuAndLecCode.setEditable(true);
				tfStuAndLecCode.setFocusTraversable(false);
				tfStuAndLecCode.setMinWidth(175);
				tfStuAndLecCode.setMinHeight(35);
				tfStuAndLecCode.setStyle("-fx-prompt-text-fill: #ADFF2F ;\r\n" + "-fx-text-inner-color: #ADFF2F ;\r\n"
						+ "-fx-text-fill: #ADFF2F ;\r\n" + "-fx-font-family: 'Libre Franklin';\r\n"
						+ "-fx-font-size: 16px;");

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

				btnSubmit = new JFXButton("Xác nhận");
				btnSubmit.setStyle("-fx-font-family: 'Doris';");
				btnSubmit.setMinWidth(120);
				btnSubmit.setMinHeight(35);
				btnSubmit.setMaxWidth(140);
				btnSubmit.setMaxHeight(40);

				rectangle = new Rectangle(250, 40);
				rectangle.setFill(Color.rgb(161, 161, 160)); // B4A4C4 , (95, 108, 146)
				// set rounded
				rectangle.setArcHeight(20);
				rectangle.setArcWidth(20);

				hboxSearchTF = new HBox(tfStuAndLecCode);
				hboxSearchTF.setPadding(new Insets(3, 10, 5, 35));

				HBox hboxSearchIcon = new HBox(search36ImView);
				hboxSearchIcon.setPadding(new Insets(8, 10, 0, 8));

				hboxDeleteAllIcon = new HBox(deleteAll36ImView);
				hboxDeleteAllIcon.setPadding(new Insets(8, 10, 0, 213));
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

				hboxDeleteAllIcon.setOnTouchReleased(e -> {

					tfStuAndLecCode.setText("");
					tfStuAndLecCode.requestFocus();
				});

				hboxDeleteAllIcon.setOnMouseClicked(e -> {

					tfStuAndLecCode.setText("");
					tfStuAndLecCode.requestFocus();
				});

//			setOnPressedAndHolded(deleteAll36ImView, Duration.seconds(2.5), e -> {
//
//				tfStuAndLecCode.setText(tfStuAndLecCode.getText().substring(0, tfStuAndLecCode.getText().length()-1));
//				tfStuAndLecCode.requestFocus();
//			});
//			
//			setOnPressedAndHolded(hboxDeleteAllIcon, Duration.seconds(2.5), e -> {
//
//				tfStuAndLecCode.setText(tfStuAndLecCode.getText().substring(0, tfStuAndLecCode.getText().length()-1));
//				tfStuAndLecCode.requestFocus();
//			});
//			

				// handle visibility of delete all icon when length of TF > 0
				Thread taskShowDeleteAllIconThread = new Thread(() -> {
					while (!Thread.currentThread().isInterrupted()) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {

								if (tfStuAndLecCode.getText().length() > 0) {
									hboxDeleteAllIcon.setVisible(true);

									// set key pressed listener
									tfStuAndLecCode.setOnKeyReleased(new EventHandler<KeyEvent>() {

										@Override
										public void handle(KeyEvent event) {
											if (event.getCode() == KeyCode.ENTER) {
												// unfocus and disable the Soft Keyboard
												lblPresentDay.requestFocus();
											}
										}
									});

								} else {
									hboxDeleteAllIcon.setVisible(false);
								}
							}
						});
						try {
							Thread.sleep(2);
						} catch (InterruptedException exc) {
							break;
						}
					}
				});
				taskShowDeleteAllIconThread.setDaemon(true);
				taskShowDeleteAllIconThread.start();

				paneSearchTextField = new Pane(rectangle, hboxSearchIcon, hboxSearchTF, hboxDeleteAllIcon);
				hboxSearchTextField = new HBox(10, paneSearchTextField, btnSubmit);
				hboxSearchTextField.setPadding(new Insets(10, 10, 8, 10));

				// handle labels
				lblPresentDay = new Label();
				lblPresentDay.setText(menu.getTxtPresentDay());
				lblPresentDay.setStyle("-fx-font-size: 15pt;\r\n" + " -fx-font-family: 'Quicksand';"
						+ "-fx-text-fill: #ff0000;" + "\r\n-fx-background-color: #E0DEDE;");

				// the container of label
				VBox lblPDContainer = new VBox(lblPresentDay);
				lblPDContainer.setPadding(new Insets(2, 0, 2, 10));

				lblTomorrow = new Label();
				lblTomorrow.setText(menu.getTxtTomorrow());
				lblTomorrow.setStyle("-fx-font-size: 15pt;\r\n" + " -fx-font-family: 'Quicksand';"
						+ "-fx-text-fill: #591212;" + "\r\n-fx-background-color: #E0DEDE;");

				// the container of label
				VBox lblTContainer = new VBox(lblTomorrow);
				lblTContainer.setPadding(new Insets(2, 0, 2, 10));

				lblTheDayAT = new Label();
				lblTheDayAT.setText(menu.getTxtTheDayAT());
				lblTheDayAT.setStyle("-fx-font-size: 15pt;\r\n" + " -fx-font-family: 'Quicksand';"
						+ "-fx-text-fill: #591212;" + "\r\n-fx-background-color: #E0DEDE;");

				// the container of label
				VBox lblATContainer = new VBox(lblTheDayAT);
				lblATContainer.setPadding(new Insets(2, 0, 2, 10));

				lblTheDayAAT = new Label();
				lblTheDayAAT.setText(menu.getTxtTheDayAAT());
				lblTheDayAAT.setStyle("-fx-font-size: 15pt;\r\n" + " -fx-font-family: 'Quicksand';"
						+ "-fx-text-fill: #591212;" + "\r\n-fx-background-color: #E0DEDE;");

				// the container of label
				VBox lblAATContainer = new VBox(lblTheDayAAT);
				lblAATContainer.setPadding(new Insets(2, 0, 2, 10));

				lblTheDayAAAT = new Label();
				lblTheDayAAAT.setText(menu.getTxtTheDayAAAT());
				lblTheDayAAAT.setStyle("-fx-font-size: 15pt;\r\n" + " -fx-font-family: 'Quicksand';"
						+ "-fx-text-fill: #591212;" + "\r\n-fx-background-color: #E0DEDE;");

				// the container of label
				VBox lblAAATContainer = new VBox(lblTheDayAAAT);
				lblAAATContainer.setPadding(new Insets(2, 0, 2, 10));

				lblTheDayAAAAT = new Label();
				lblTheDayAAAAT.setText(menu.getTxtTheDayAAAAT());
				lblTheDayAAAAT.setStyle("-fx-font-size: 15pt;\r\n" + " -fx-font-family: 'Quicksand';"
						+ "-fx-text-fill: #591212;" + "\r\n-fx-background-color: #E0DEDE;");

				// the container of label
				VBox lblAAAATContainer = new VBox(lblTheDayAAAAT);
				lblAAAATContainer.setPadding(new Insets(2, 0, 2, 10));

				lblTheDayAAAAAT = new Label();
				lblTheDayAAAAAT.setText(menu.getTxtTheDayAAAAAT());
				lblTheDayAAAAAT.setStyle("-fx-font-size: 15pt;\r\n" + " -fx-font-family: 'Quicksand';"
						+ "-fx-text-fill: #591212;" + "\r\n-fx-background-color: #E0DEDE;");

				// the container of label
				VBox lblAAAAATContainer = new VBox(lblTheDayAAAAAT);
				lblAAAAATContainer.setPadding(new Insets(2, 0, 2, 10));

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
						CustomListCellThird clct = new CustomListCellThird();
						clct.setCountCheck(1);
						return clct;
					}

				});
				listViewCoursesTomorrow = new ListView<MonHoc>(listCourseDataT);
				listViewCoursesTomorrow.setCellFactory(new Callback<ListView<MonHoc>, ListCell<MonHoc>>() {

					public ListCell<MonHoc> call(ListView<MonHoc> listView) {
						CustomListCellThird clct = new CustomListCellThird();
						clct.setCountCheck(0);
						return clct;
					}

				});
				listViewCoursesTheDayAT = new ListView<MonHoc>(listCourseDataA);
				listViewCoursesTheDayAT.setCellFactory(new Callback<ListView<MonHoc>, ListCell<MonHoc>>() {

					public ListCell<MonHoc> call(ListView<MonHoc> listView) {
						CustomListCellThird clct = new CustomListCellThird();
						clct.setCountCheck(2);
						return clct;
					}

				});
				listViewCoursesTheDayAAT = new ListView<MonHoc>(listCourseDataAA);
				listViewCoursesTheDayAAT.setCellFactory(new Callback<ListView<MonHoc>, ListCell<MonHoc>>() {

					public ListCell<MonHoc> call(ListView<MonHoc> listView) {
						CustomListCellThird clct = new CustomListCellThird();
						clct.setCountCheck(3);
						return clct;
					}

				});
				listViewCoursesTheDayAAAT = new ListView<MonHoc>(listCourseDataAAA);
				listViewCoursesTheDayAAAT.setCellFactory(new Callback<ListView<MonHoc>, ListCell<MonHoc>>() {

					public ListCell<MonHoc> call(ListView<MonHoc> listView) {
						CustomListCellThird clct = new CustomListCellThird();
						clct.setCountCheck(7);
						return clct;
					}

				});
				listViewCoursesTheDayAAAAT = new ListView<MonHoc>(listCourseDataAAAA);
				listViewCoursesTheDayAAAAT.setCellFactory(new Callback<ListView<MonHoc>, ListCell<MonHoc>>() {

					public ListCell<MonHoc> call(ListView<MonHoc> listView) {
						CustomListCellThird clct = new CustomListCellThird();
						clct.setCountCheck(5);
						return clct;
					}

				});
				listViewCoursesTheDayAAAAAT = new ListView<MonHoc>(listCourseDataAAAAA);
				listViewCoursesTheDayAAAAAT.setCellFactory(new Callback<ListView<MonHoc>, ListCell<MonHoc>>() {

					public ListCell<MonHoc> call(ListView<MonHoc> listView) {
						CustomListCellThird clct = new CustomListCellThird();
						clct.setCountCheck(6);
						return clct;
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
				listViewCoursesPresentDay.setPrefWidth(385);
//				listViewCoursesPresentDay.setMaxWidth(400);
				listViewCoursesPresentDay.setMaxHeight(165);

				// specially focus on The Current Day

				listViewCoursesTomorrow.setStyle("-fx-border-width: 0;" + "\r\n-fx-border-color: #fff");
//				listViewCoursesTheDayAAAT.setMaxWidth(400);
				listViewCoursesTomorrow.setPrefWidth(385);
				listViewCoursesTomorrow.setMaxHeight(165);

				listViewCoursesTheDayAT.setStyle("-fx-border-width: 0;" + "\r\n-fx-border-color: #fff");
//				listViewCoursesTheDayAT.setMaxWidth(400);
				listViewCoursesTheDayAT.setPrefWidth(385);
				listViewCoursesTheDayAT.setMaxHeight(165);

				listViewCoursesTheDayAAT.setStyle("-fx-border-width: 0;" + "\r\n-fx-border-color: #fff");
//				listViewCoursesTheDayAAT.setMaxWidth(400);
				listViewCoursesTheDayAAT.setPrefWidth(385);
				listViewCoursesTheDayAAT.setMaxHeight(165);

				listViewCoursesTheDayAAAT.setStyle("-fx-border-width: 0;" + "\r\n-fx-border-color: #fff");
//				listViewCoursesTheDayAAAT.setMaxWidth(400);
				listViewCoursesTheDayAAAT.setPrefWidth(385);
				listViewCoursesTheDayAAAT.setMaxHeight(165);

				listViewCoursesTheDayAAAAT.setStyle("-fx-border-width: 0;" + "\r\n-fx-border-color: #fff");
//				listViewCoursesTheDayAAAAT.setMaxWidth(400);
				listViewCoursesTheDayAAAAT.setPrefWidth(385);
				listViewCoursesTheDayAAAAT.setMaxHeight(165);

				listViewCoursesTheDayAAAAAT.setStyle("-fx-border-width: 0;" + "\r\n-fx-border-color: #fff");
//				listViewCoursesTheDayAAAAAT.setMaxWidth(400);
				listViewCoursesTheDayAAAAAT.setPrefWidth(385);
				listViewCoursesTheDayAAAAAT.setMaxHeight(165);

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
					vBoxC.setStyle("\r\n-fx-border-color: #fff;" + "\r\n-fx-border-width: 0;"
							+ "\r\n-fx-background-color: #E0DEDE;");
				} else if (dayCheckNoCourseAtAll[0] == 1) {
					// init label no courses
					Label lblNoCourses = new Label("(Không có tiết học)"); // Trống lịch
					lblNoCourses.setStyle("-fx-font-size: 20px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
							+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #000");
					lblNoCourses.setPadding(new Insets(0, 20, 10, 20));
					lblNoCourses.setAlignment(Pos.CENTER);

					// draw line
					Line endLineBorder = new Line(0, 60, 3000, 60);
					endLineBorder.setStroke(Color.rgb(161, 160, 160));
					
					vBoxC = new VBox(lblPDContainer, lblNoCourses, endLineBorder);
					vBoxC.setStyle("\r\n-fx-border-color: #fff;" + "\r\n-fx-border-width: 0;"
							+ "\r\n-fx-background-color: #E0DEDE;");
				} else if (dayCheckNoCourseAtAll[0] == 2) {
					listViewCoursesPresentDay.setMaxHeight(85);
					vBoxC = new VBox(lblPDContainer, listViewCoursesPresentDay);
					vBoxC.setStyle("\r\n-fx-border-color: #fff;" + "\r\n-fx-border-width: 0;"
							+ "\r\n-fx-background-color: #E0DEDE;");
				}

				if (dayCheckNoCourseAtAll[1] == 0) {
					vBoxT = new VBox(lblTContainer, listViewCoursesTomorrow);
					vBoxT.setStyle("\r\n-fx-border-color: #fff;" + "\r\n-fx-border-width: 0;"
							+ "\r\n-fx-background-color: #E0DEDE;");
				} else if (dayCheckNoCourseAtAll[1] == 1) {
					// init label no courses
					Label lblNoCourses = new Label("(Không có tiết học)");
					lblNoCourses.setStyle("-fx-font-size: 20px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
							+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #000");
					lblNoCourses.setPadding(new Insets(0, 20, 10, 20));
					lblNoCourses.setAlignment(Pos.CENTER);

					// draw line
					Line endLineBorder = new Line(0, 60, 3000, 60);
					endLineBorder.setStroke(Color.rgb(161, 160, 160));
					
					vBoxT = new VBox(lblTContainer, lblNoCourses, endLineBorder);
					vBoxT.setStyle("\r\n-fx-border-color: #fff;" + "\r\n-fx-border-width: 0;"
							+ "\r\n-fx-background-color: #E0DEDE;");
				} else if (dayCheckNoCourseAtAll[1] == 2) {
					listViewCoursesTomorrow.setMaxHeight(85);
					vBoxT = new VBox(lblTContainer, listViewCoursesTomorrow);
					vBoxT.setStyle("\r\n-fx-border-color: #fff;" + "\r\n-fx-border-width: 0;"
							+ "\r\n-fx-background-color: #E0DEDE;");
				}

				if (dayCheckNoCourseAtAll[2] == 0) {
					vBoxA = new VBox(lblATContainer, listViewCoursesTheDayAT);
					vBoxA.setStyle("\r\n-fx-border-color: #fff;" + "\r\n-fx-border-width: 0;"
							+ "\r\n-fx-background-color: #E0DEDE;");
				} else if (dayCheckNoCourseAtAll[2] == 1) {
					// init label no courses
					Label lblNoCourses = new Label("(Không có tiết học)");
					lblNoCourses.setStyle("-fx-font-size: 20px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
							+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #000");
					lblNoCourses.setPadding(new Insets(0, 20, 10, 20));
					lblNoCourses.setAlignment(Pos.CENTER);

					// draw line
					Line endLineBorder = new Line(0, 60, 3000, 60);
					endLineBorder.setStroke(Color.rgb(161, 160, 160));
					
					vBoxA = new VBox(lblATContainer, lblNoCourses, endLineBorder);
					vBoxA.setStyle("\r\n-fx-border-color: #fff;" + "\r\n-fx-border-width: 0;"
							+ "\r\n-fx-background-color: #E0DEDE;");
				} else if (dayCheckNoCourseAtAll[2] == 2) {
					listViewCoursesTheDayAT.setMaxHeight(85);
					vBoxA = new VBox(lblATContainer, listViewCoursesTheDayAT);
					vBoxA.setStyle("\r\n-fx-border-color: #fff;" + "\r\n-fx-border-width: 0;"
							+ "\r\n-fx-background-color: #E0DEDE;");
				}

				if (dayCheckNoCourseAtAll[3] == 0) {
					vBoxAA = new VBox(lblAATContainer, listViewCoursesTheDayAAT);
					vBoxAA.setStyle("\r\n-fx-border-color: #fff;" + "\r\n-fx-border-width: 0;"
							+ "\r\n-fx-background-color: #E0DEDE;");
				} else if (dayCheckNoCourseAtAll[3] == 1) {
					// init label no courses
					Label lblNoCourses = new Label("(Không có tiết học)");
					lblNoCourses.setStyle("-fx-font-size: 20px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
							+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #000");
					lblNoCourses.setPadding(new Insets(0, 20, 10, 20));
					lblNoCourses.setAlignment(Pos.CENTER);

					// draw line
					Line endLineBorder = new Line(0, 60, 3000, 60);
					endLineBorder.setStroke(Color.rgb(161, 160, 160));
					
					vBoxAA = new VBox(lblAATContainer, lblNoCourses, endLineBorder);
					vBoxAA.setStyle("\r\n-fx-border-color: #fff;" + "\r\n-fx-border-width: 0;"
							+ "\r\n-fx-background-color: #E0DEDE;");
				} else if (dayCheckNoCourseAtAll[3] == 2) {
					listViewCoursesTheDayAAT.setMaxHeight(85);
					vBoxAA = new VBox(lblAATContainer, listViewCoursesTheDayAAT);
					vBoxAA.setStyle("\r\n-fx-border-color: #fff;" + "\r\n-fx-border-width: 0;"
							+ "\r\n-fx-background-color: #E0DEDE;");
				}

				if (dayCheckNoCourseAtAll[4] == 0) {
					vBoxAAA = new VBox(lblAAATContainer, listViewCoursesTheDayAAAT);
					vBoxAAA.setStyle("\r\n-fx-border-color: #fff;" + "\r\n-fx-border-width: 0;"
							+ "\r\n-fx-background-color: #E0DEDE;");
				} else if (dayCheckNoCourseAtAll[4] == 1) {
					// init label no courses
					Label lblNoCourses = new Label("(Không có tiết học)");
					lblNoCourses.setStyle("-fx-font-size: 20px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
							+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #000");
					lblNoCourses.setPadding(new Insets(0, 20, 10, 20));
					lblNoCourses.setAlignment(Pos.CENTER);

					// draw line
					Line endLineBorder = new Line(0, 60, 3000, 60);
					endLineBorder.setStroke(Color.rgb(161, 160, 160));
					
					vBoxAAA = new VBox(lblAAATContainer, lblNoCourses, endLineBorder);
					vBoxAAA.setStyle("\r\n-fx-border-color: #fff;" + "\r\n-fx-border-width: 0;"
							+ "\r\n-fx-background-color: #E0DEDE;");
				} else if (dayCheckNoCourseAtAll[4] == 2) {
					listViewCoursesTheDayAAAT.setMaxHeight(85);
					vBoxAAA = new VBox(lblAAATContainer, listViewCoursesTheDayAAAT);
					vBoxAAA.setStyle("\r\n-fx-border-color: #fff;" + "\r\n-fx-border-width: 0;"
							+ "\r\n-fx-background-color: #E0DEDE;");
				}

				if (dayCheckNoCourseAtAll[5] == 0) {
					vBoxAAAA = new VBox(lblAAAATContainer, listViewCoursesTheDayAAAAT);
					vBoxAAAA.setStyle("\r\n-fx-border-color: #fff;" + "\r\n-fx-border-width: 0;"
							+ "\r\n-fx-background-color: #E0DEDE;");
				} else if (dayCheckNoCourseAtAll[5] == 1) {
					// init label no courses
					Label lblNoCourses = new Label("(Không có tiết học)");
					lblNoCourses.setStyle("-fx-font-size: 20px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
							+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #000");
					lblNoCourses.setPadding(new Insets(0, 20, 10, 20));
					lblNoCourses.setAlignment(Pos.CENTER);

					// draw line
					Line endLineBorder = new Line(0, 60, 3000, 60);
					endLineBorder.setStroke(Color.rgb(161, 160, 160));
					
					vBoxAAAA = new VBox(lblAAAATContainer, lblNoCourses, endLineBorder);
					vBoxAAAA.setStyle("\r\n-fx-border-color: #fff;" + "\r\n-fx-border-width: 0;"
							+ "\r\n-fx-background-color: #E0DEDE;");
				} else if (dayCheckNoCourseAtAll[5] == 2) {
					listViewCoursesTheDayAAAAT.setMaxHeight(85);
					vBoxAAAA = new VBox(lblAAAATContainer, listViewCoursesTheDayAAAAT);
					vBoxAAAA.setStyle("\r\n-fx-border-color: #fff;" + "\r\n-fx-border-width: 0;"
							+ "\r\n-fx-background-color: #E0DEDE;");
				}

				if (dayCheckNoCourseAtAll[6] == 0) {
					vBoxAAAAA = new VBox(lblAAAAATContainer, listViewCoursesTheDayAAAAAT);
					vBoxAAAAA.setStyle("\r\n-fx-border-color: #fff;" + "\r\n-fx-border-width: 0;"
							+ "\r\n-fx-background-color: #E0DEDE;");
				} else if (dayCheckNoCourseAtAll[6] == 1) {
					// init label no courses
					Label lblNoCourses = new Label("(Không có tiết học)");
					lblNoCourses.setStyle("-fx-font-size: 20px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
							+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #000");
					lblNoCourses.setPadding(new Insets(0, 20, 10, 20));
					lblNoCourses.setAlignment(Pos.CENTER);
					
					// draw line
					Line endLineBorder = new Line(0, 60, 3000, 60);
					endLineBorder.setStroke(Color.rgb(161, 160, 160));

					vBoxAAAAA = new VBox(lblAAAAATContainer, lblNoCourses, endLineBorder);
					vBoxAAAAA.setStyle("\r\n-fx-border-color: #fff;" + "\r\n-fx-border-width: 0;"
							+ "\r\n-fx-background-color: #E0DEDE;");
				} else if (dayCheckNoCourseAtAll[6] == 2) {
					listViewCoursesTheDayAAAAAT.setMaxHeight(85);
					vBoxAAAAA = new VBox(lblAAAAATContainer, listViewCoursesTheDayAAAAAT);
					vBoxAAAAA.setStyle("\r\n-fx-border-color: #fff;" + "\r\n-fx-border-width: 0;"
							+ "\r\n-fx-background-color: #E0DEDE;");
				}

				// handles StackPaneContent and JFXScrollPane
				vBoxMainSub = new VBox(
//					gridPaneTextFieldButton
						hboxSearchTextField, vBoxC, vBoxT, vBoxA, vBoxAA, vBoxAAA, vBoxAAAA, vBoxAAAAA);
				vBoxMainSub.setMinHeight(640);
				vBoxMainSub.setMinWidth(375);
				vBoxMainSub.setPrefWidth(385);
//				vBoxMainSub.setMaxWidth(400);

				scrollPaneMain = new ScrollPane();
				stackPaneMain = new StackPane(vBoxMainSub);

				scrollPaneMain.setContent(vBoxMainSub);

//				scrollPaneMain.setMaxWidth(400);
				scrollPaneMain.setMinWidth(375);
				scrollPaneMain.setPrefWidth(385);
				scrollPaneMain.setMinHeight(450);
				scrollPaneMain.setMaxHeight(650);

//				stackPaneMain.setMaxWidth(400);
				stackPaneMain.setPrefWidth(385);
				stackPaneMain.setMinHeight(450);
				stackPaneMain.setMaxHeight(650);
				stackPaneMain.setMinWidth(375);

//==============================================================================================//

				// set default roll position to hide hboxSearchTextField
				scrollPaneMain.setVvalue(0.25);

//==============================================================================================//

				// Define Tab 2 Final Exam

				tfStudentInputCode = new JFXTextField();
				tfStudentInputCode.setPromptText("Mã sinh viên");
				tfStudentInputCode.setEditable(true);
				tfStudentInputCode.setFocusTraversable(false);
				tfStudentInputCode.setMinWidth(175);
				tfStudentInputCode.setMinHeight(35);
				tfStudentInputCode.setStyle("-fx-prompt-text-fill: #ADFF2F ;\r\n"
						+ "-fx-text-inner-color: #ADFF2F ;\r\n" + "-fx-text-fill: #ADFF2F ;\r\n"
						+ "-fx-font-family: 'Libre Franklin';\r\n" + "-fx-font-size: 16px;");
				tfStudentInputCode.setPadding(new Insets(0, 0, 0, 0));
				tfStudentInputCode.setOnKeyReleased(new EventHandler<KeyEvent>() {

					@Override
					public void handle(KeyEvent event) {
						if (event.getCode() == KeyCode.ENTER) {
							tfStudentInputCode.setFocusTraversable(false);

							// Validate tfInputCode content
							String tfInputCodeContentRaw = tfStudentInputCode.getText().toString().toLowerCase().trim();
							if (tfInputCodeContentRaw.length() >= 11) {

							}

//							com.gluonhq.charm.glisten.control.Toast toastMessage = new com.gluonhq.charm.glisten.control.Toast(
//									"Touch ENTER.", com.gluonhq.charm.glisten.control.Toast.LENGTH_LONG);
//							toastMessage.show();

							// request focus on another views
//							fab.requestFocus();
						} else if (event.getCode() == KeyCode.CIRCUMFLEX || event.getCode() == KeyCode.DEAD_ACUTE
								|| event.getCode() == KeyCode.END || event.getCode() == KeyCode.ESCAPE
								|| event.getCode() == KeyCode.EJECT_TOGGLE || event.getCode() == KeyCode.TRACK_PREV
								|| event.getCode() == KeyCode.KATAKANA) {
							com.gluonhq.charm.glisten.control.Toast toastMessage = new com.gluonhq.charm.glisten.control.Toast(
									"Touch CLEAR.", com.gluonhq.charm.glisten.control.Toast.LENGTH_LONG);
							toastMessage.show();

							// request focus on another views
//							lblExplain.requestFocus();
						}
					}

				});

				Image searchIcon24ES = new Image(getClass().getResourceAsStream("images/ic_search_24_green.png"));
				ImageView search24ImViewES = new ImageView(searchIcon24ES);

				Image searchIcon36ES = new Image(getClass().getResourceAsStream("images/ic_search_36_green.png"));
				ImageView search36ImViewES = new ImageView(searchIcon36ES);
				search36ImViewES.setFitWidth(24);
				search36ImViewES.setFitHeight(24);

				Image deleteAllIcon24ES = new Image(getClass().getResourceAsStream("images/ic_delete_all_24.png"));
				ImageView deleteAll24ImViewES = new ImageView(deleteAllIcon24ES);

				Image deleteAllIcon36ES = new Image(getClass().getResourceAsStream("images/ic_delete_all_36.png"));
				ImageView deleteAll36ImViewES = new ImageView(deleteAllIcon36ES);
				deleteAll36ImViewES.setFitWidth(24);
				deleteAll36ImViewES.setFitHeight(24);

				JFXButton btnSubmitES = new JFXButton("Xác nhận");
				btnSubmitES.setStyle("-fx-font-family: 'Doris';");
				btnSubmitES.setMinWidth(120);
				btnSubmitES.setMinHeight(35);
				btnSubmitES.setMaxWidth(140);
				btnSubmitES.setMaxHeight(40);

				Rectangle rectangleES = new Rectangle(250, 40);
				rectangleES.setFill(Color.rgb(161, 161, 160)); // B4A4C4 , (95, 108, 146)
				// set rounded
				rectangleES.setArcHeight(20);
				rectangleES.setArcWidth(20);

				HBox hboxSearchTFES = new HBox(tfStudentInputCode);
				hboxSearchTFES.setPadding(new Insets(3, 10, 5, 35));

				HBox hboxSearchIconES = new HBox(search36ImViewES);
				hboxSearchIconES.setPadding(new Insets(8, 10, 0, 8));

				HBox hboxDeleteAllIconES = new HBox(deleteAll36ImViewES);
				hboxDeleteAllIconES.setPadding(new Insets(8, 10, 0, 213));
				hboxDeleteAllIconES.setVisible(false);

				// handle delete all TF content when TF on click
				deleteAll36ImViewES.setOnMouseClicked(e -> {
					tfStudentInputCode.setText("");
					tfStudentInputCode.requestFocus();
				});

				deleteAll36ImViewES.setOnTouchReleased(e -> {
					tfStudentInputCode.setText("");
					tfStudentInputCode.requestFocus();
				});

				hboxDeleteAllIconES.setOnTouchReleased(e -> {
					tfStudentInputCode.setText("");
					tfStudentInputCode.requestFocus();
				});

				hboxDeleteAllIconES.setOnMouseClicked(e -> {
					tfStudentInputCode.setText("");
					tfStudentInputCode.requestFocus();
				});

				// handle visibility of delete all icon when length of TF > 0
				Thread taskShowDeleteAllIconThreadES = new Thread(() -> {
					while (!Thread.currentThread().isInterrupted()) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {

								if (tfStudentInputCode.getText().length() > 0) {
									hboxDeleteAllIconES.setVisible(true);

									// set key pressed listener
									tfStudentInputCode.setOnKeyReleased(new EventHandler<KeyEvent>() {

										@Override
										public void handle(KeyEvent event) {
											if (event.getCode() == KeyCode.ENTER) {
												// unfocus and disable the Soft Keyboard
												rectangleES.requestFocus();
											}
										}
									});

								} else {
									hboxDeleteAllIconES.setVisible(false);
								}
							}
						});
						try {
							Thread.sleep(2);
						} catch (InterruptedException exc) {
							break;
						}
					}
				});
				taskShowDeleteAllIconThreadES.setDaemon(true);
				taskShowDeleteAllIconThreadES.start();

				Pane paneSearchTextFieldES = new Pane(rectangleES, hboxSearchIconES, hboxSearchTFES,
						hboxDeleteAllIconES);

				// handle delete all TF content when TF on click
				deleteAll36ImViewES.setOnMouseClicked(e -> {
					tfStudentInputCode.setText("");
					tfStudentInputCode.requestFocus();
				});

				deleteAll36ImViewES.setOnTouchReleased(e -> {
					tfStudentInputCode.setText("");
					tfStudentInputCode.requestFocus();
				});

				hboxDeleteAllIconES.setOnTouchReleased(e -> {
					tfStudentInputCode.setText("");
					tfStudentInputCode.requestFocus();
				});

				hboxDeleteAllIconES.setOnMouseClicked(e -> {
					tfStudentInputCode.setText("");
					tfStudentInputCode.requestFocus();
				});

				HBox hboxSearchTextFieldES = new HBox(10, paneSearchTextFieldES, btnSubmitES);
				hboxSearchTextFieldES.setPadding(new Insets(10, 10, 8, 10));

				VBox vboxInputCodeES = new VBox(hboxSearchTextFieldES);
//				vboxInputCodeES.setPadding(new Insets(0, 0, 0, 0));

				// 2 labels and toggle Choose Button
				buttonToggleSwitch = new SwitchToggleButton();
				ToggleButtonGroup toggleButtonGroup = new ToggleButtonGroup();

			    ToggleButton toggleButtonYes = new ToggleButton("L");
			    toggleButtonYes.minWidthProperty().bind(toggleButtonYes.prefWidthProperty());
			    toggleButtonGroup.getToggles().add(toggleButtonYes);

			    ToggleButton toggleButtonNo = new ToggleButton("R");
			    toggleButtonNo.minWidthProperty().bind(toggleButtonNo.prefWidthProperty());
			    toggleButtonGroup.getToggles().add(toggleButtonNo);
				
			    CustomToggleSwitchButton toggleButton = new CustomToggleSwitchButton();
			    HBox hboxToggleButton = new HBox(toggleButton);
			    hboxToggleButton.setPadding(new Insets(20, 5, 20, 5));
			    
		        Text text = new Text();
		        text.setStyle("-fx-text-fill:#000;-fx-font-size:18px;");
		        text.textProperty().bind(Bindings.when(toggleButton.switchedOnProperty()).then("L").otherwise("R"));
			    
				Label lblLeft = new Label("CĐ/ĐH (Cao đẳng/Đại học)"),
						lblRight = new Label("SĐH (Sau Đại học)");
				lblLeft.setWrapText(true);
				lblLeft.setPrefWidth(120);
				lblLeft.setMaxWidth(200);
				lblLeft.setStyle("-fx-font-size: 20px;\r\n" + " -fx-font-family: 'Candara';" + "-fx-text-fill: #000;"
						+ "\r\n-fx-background-color: #fff;");
				lblRight.setWrapText(true);
				lblRight.setPrefWidth(100);
				lblRight.setMaxWidth(200);
				lblRight.setStyle("-fx-font-size: 20px;\r\n" + " -fx-font-family: 'Candara';" + "-fx-text-fill: #000;"
						+ "\r\n-fx-background-color: #fff;");
				
				HBox hboxLblLeft = new HBox(lblLeft),
						hboxLblRight = new HBox(lblRight);
				hboxLblLeft.setPadding(new Insets(10, 15, 10, 15));
				hboxLblRight.setPadding(new Insets(10, 15, 10, 15));
				
				hboxButtonToggle = new HBox(10, hboxLblLeft, hboxToggleButton, hboxLblRight);
//				hboxButtonToggle.setMinWidth(tabPane.getBoundsInParent().getWidth());
				
				// Begin Loop Layout (15 epos)
				Label lblDOWFE = new Label("Thứ Hai"), lblDayFE = new Label("01/01/2021"), lblMiddle = new Label("");

				lblDOWFE.setStyle("-fx-font-size: 16pt;\r\n" + " -fx-font-family: 'Candara';" + "-fx-text-fill: #000;"
						+ "\r\n-fx-background-color: #E2DFA2;");
				lblDOWFE.setAlignment(Pos.CENTER_LEFT);
				lblDOWFE.setPadding(new Insets(0, 0, 0, 10));
				lblMiddle.setMinWidth(180);
				lblMiddle.setStyle("-fx-font-size: 16pt;\r\n" + " -fx-font-family: 'Candara';" + "-fx-text-fill: #000;"
						+ "\r\n-fx-background-color: #E2DFA2;");
				lblDayFE.setStyle("-fx-font-size: 14pt;\r\n" + " -fx-font-family: 'Ubuntu';" + "-fx-text-fill: #867666;"
						+ "\r\n-fx-background-color: #E2DFA2;");
				lblDayFE.setPadding(new Insets(2.5, 0, 0, lblDOWFE.getBoundsInParent().getWidth()));

				HBox hboxLabelFirstFE = new HBox(lblDOWFE, lblDayFE);
				hboxLabelFirstFE.setStyle("\r\n-fx-background-color: #E2DFA2;");

				int insetLabelFE = 10;

				// draw line
				Line endLineBorderFE = new Line(0, 80, 3000, 80);
				endLineBorderFE.setStroke(Color.rgb(161, 160, 160));

				Rectangle rectFE = new Rectangle(12, 110);
				rectFE.setFill(Color.rgb(237, 87, 82));
				Label lblCourseNameFE = new Label("Giao duc the chat dai cuong");

				Label lblShiftFE = new Label("7h00 - 11h30"), lblTimeFE = new Label("Thời gian: "),
						lblPlaceFE = new Label("SAN04"), lblShiftNumFE = new Label("1 - 2, "),
						lblPeriodFE = new Label("Tiết "), lblRoomFE = new Label("Phòng: "),
						lblThiMonFE = new Label("Thi môn: "), lblToThiFE = new Label("Tổ thi: "),
						lblGhepThiFE = new Label("Ghép thi: "), lblSoLuongFE = new Label("Số lượng: "),
						lblToThiFE_Rep = new Label("002, "), lblGhepThiFE_Rep = new Label("39, "),
						lblSoLuongFE_Rep = new Label("57");

				// hbox 1
				Image timeIconFE = new Image(getClass().getResourceAsStream("images/ic_clock_24_0.png"));
				ImageView timeImViewFE = new ImageView(timeIconFE);
				timeImViewFE.setFitWidth(16);
				timeImViewFE.setFitHeight(16);

				HBox hboxImViewFE = new HBox(timeImViewFE);
				hboxImViewFE.setPadding(new Insets(2.75, 0, 0, 0));

				lblShiftFE.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #C51802");
//				lblShift.setPadding(new Insets(0, 0, 0, 20));
				lblTimeFE.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #C51802");
				lblTimeFE.setPadding(new Insets(0, 0, 0, 10));
				HBox hboxFirstFE = new HBox(hboxImViewFE, lblTimeFE, lblShiftFE);
				hboxFirstFE.setPrefHeight(10);
				hboxFirstFE.setMaxHeight(18);
				hboxFirstFE.setPadding(new Insets(5.25, 0, 3, 0));

				// hbox 2
				lblCourseNameFE.setStyle("-fx-font-size: 18px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Bitter';" + "-fx-text-fill: #000");
//				lblCourseName.setPadding(new Insets(insetLabel, insetLabel, 0, 0));
				lblThiMonFE.setStyle("-fx-font-size: 18px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Bitter';" + "-fx-text-fill: #000");
//				lblShiftNum.setPadding(new Insets(insetLabel + 20, insetLabel + 5, insetLabel + 10, insetLabel + 5));
				HBox hboxSecondFE = new HBox(lblThiMonFE, lblCourseNameFE);
				hboxSecondFE.setPadding(new Insets(0, 0, 3, 0));

				// hbox 3
				lblPeriodFE.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblShiftNumFE.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblToThiFE.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblToThiFE_Rep.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblGhepThiFE.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblGhepThiFE_Rep.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblSoLuongFE.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblSoLuongFE_Rep.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				HBox hboxThirdFE = new HBox(lblPeriodFE, lblShiftNumFE, lblToThiFE, lblToThiFE_Rep, lblGhepThiFE,
						lblGhepThiFE_Rep, lblSoLuongFE, lblSoLuongFE_Rep);
				hboxThirdFE.setPadding(new Insets(0, 0, 3, 0));

				// hbox 4
				lblRoomFE.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #00667C");
//				lblPlace.setPadding(new Insets(0, insetLabel, insetLabel, 0));
				lblPlaceFE.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #00667C");
//				lblPlace.setPadding(new Insets(0, insetLabel, insetLabel, 0));
				HBox hboxFourthFE = new HBox(lblRoomFE, lblPlaceFE);
				hboxFourthFE.setPadding(new Insets(0, 0, 1, 0));

				VBox vboxSubFE = new VBox(hboxSecondFE, hboxThirdFE, hboxFourthFE);
				vboxSubFE.setStyle("-fx-background-color:#fff");
				vboxSubFE.setPadding(new Insets(0, 0, 0, 28));
				vboxSubFE.setStyle(
						"-fx-background-color:#fff;" + "\r\n-fx-border-color: #D3D3D3;\r\n" + "-fx-border-width: 0;");

				VBox vboxSubSubFE = new VBox(hboxFirstFE, vboxSubFE);
				vboxSubSubFE.setStyle("-fx-background-color:#fff");
//				vboxSubSub.setPadding(new Insets(insetLabel+10, insetLabel, insetLabel+10, 50));
				vboxSubSubFE.setStyle(
						"-fx-background-color:#fff;" + "\r\n-fx-border-color: #D3D3D3;\r\n" + "-fx-border-width: 0;");

				HBox hboxMainSubFE = new HBox(insetLabelFE, rectFE, vboxSubSubFE);
				hboxMainSubFE.setStyle(
						"-fx-background-color:#fff;" + "\r\n-fx-border-color: #D3D3D3;\r\n" + "-fx-border-width: 0;");

				// End Loop 1
				// Begin Loop 2
				Label lblDOWFE_0 = new Label("Thứ Hai"), lblDayFE_0 = new Label("01/01/2021"),
						lblMiddle_0 = new Label("");

				lblDOWFE_0.setStyle("-fx-font-size: 16pt;\r\n" + " -fx-font-family: 'Candara';" + "-fx-text-fill: #000;"
						+ "\r\n-fx-background-color: #E2DFA2;");
				lblDOWFE_0.setAlignment(Pos.CENTER_LEFT);
				lblDOWFE_0.setPadding(new Insets(0, 0, 0, 10));
				lblMiddle_0.setMinWidth(180);
				lblMiddle_0.setStyle("-fx-font-size: 16pt;\r\n" + " -fx-font-family: 'Candara';"
						+ "-fx-text-fill: #000;" + "\r\n-fx-background-color: #E2DFA2;");
				lblDayFE_0.setStyle("-fx-font-size: 14pt;\r\n" + " -fx-font-family: 'Ubuntu';"
						+ "-fx-text-fill: #867666;" + "\r\n-fx-background-color: #E2DFA2;");
				lblDayFE_0.setPadding(new Insets(2.5, 0, 0, lblDOWFE_0.getBoundsInParent().getWidth()));

				HBox hboxLabelFirstFE_0 = new HBox(lblDOWFE_0, lblDayFE_0);
				hboxLabelFirstFE_0.setStyle("\r\n-fx-background-color: #E2DFA2;");

				int insetLabelFE_0 = 10;

				// draw line
				Line endLineBorderFE_0 = new Line(0, 80, 3000, 80);
				endLineBorderFE_0.setStroke(Color.rgb(161, 160, 160));

				Rectangle rectFE_0 = new Rectangle(12, 110);
				rectFE_0.setFill(Color.rgb(146, 170, 199));
				Label lblCourseNameFE_0 = new Label("Giao duc the chat dai cuong");

				Label lblShiftFE_0 = new Label("7h00 - 11h30"), lblTimeFE_0 = new Label("Thời gian: "),
						lblPlaceFE_0 = new Label("SAN04"), lblShiftNumFE_0 = new Label("1 - 2, "),
						lblPeriodFE_0 = new Label("Tiết "), lblRoomFE_0 = new Label("Phòng: "),
						lblThiMonFE_0 = new Label("Thi môn: "), lblToThiFE_0 = new Label("Tổ thi: "),
						lblGhepThiFE_0 = new Label("Ghép thi: "), lblSoLuongFE_0 = new Label("Số lượng: "),
						lblToThiFE_Rep_0 = new Label("002, "), lblGhepThiFE_Rep_0 = new Label("39, "),
						lblSoLuongFE_Rep_0 = new Label("57");

				// hbox 1
				Image timeIconFE_0 = new Image(getClass().getResourceAsStream("images/ic_clock_24_0.png"));
				ImageView timeImViewFE_0 = new ImageView(timeIconFE_0);
				timeImViewFE_0.setFitWidth(16);
				timeImViewFE_0.setFitHeight(16);

				HBox hboxImViewFE_0 = new HBox(timeImViewFE_0);
				hboxImViewFE_0.setPadding(new Insets(2.75, 0, 0, 0));

				lblShiftFE_0.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #C51802");
//				lblShift.setPadding(new Insets(0, 0, 0, 20));
				lblTimeFE_0.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #C51802");
				lblTimeFE_0.setPadding(new Insets(0, 0, 0, 10));
				HBox hboxFirstFE_0 = new HBox(hboxImViewFE_0, lblTimeFE_0, lblShiftFE_0);
				hboxFirstFE_0.setPrefHeight(10);
				hboxFirstFE_0.setMaxHeight(18);
				hboxFirstFE_0.setPadding(new Insets(5.25, 0, 3, 0));

				// hbox 2
				lblCourseNameFE_0.setStyle("-fx-font-size: 18px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Bitter';" + "-fx-text-fill: #000");
//				lblCourseName.setPadding(new Insets(insetLabel, insetLabel, 0, 0));
				lblThiMonFE_0.setStyle("-fx-font-size: 18px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Bitter';" + "-fx-text-fill: #000");
//				lblShiftNum.setPadding(new Insets(insetLabel + 20, insetLabel + 5, insetLabel + 10, insetLabel + 5));
				HBox hboxSecondFE_0 = new HBox(lblThiMonFE_0, lblCourseNameFE_0);
				hboxSecondFE_0.setPadding(new Insets(0, 0, 3, 0));

				// hbox 3
				lblPeriodFE_0.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblShiftNumFE_0.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblToThiFE_0.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblToThiFE_Rep_0.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblGhepThiFE_0.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblGhepThiFE_Rep_0.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblSoLuongFE_0.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblSoLuongFE_Rep_0.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				HBox hboxThirdFE_0 = new HBox(lblPeriodFE_0, lblShiftNumFE_0, lblToThiFE_0, lblToThiFE_Rep_0,
						lblGhepThiFE_0, lblGhepThiFE_Rep_0, lblSoLuongFE_0, lblSoLuongFE_Rep_0);
				hboxThirdFE_0.setPadding(new Insets(0, 0, 3, 0));

				// hbox 4
				lblRoomFE_0.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #00667C");
//				lblPlace.setPadding(new Insets(0, insetLabel, insetLabel, 0));
				lblPlaceFE_0.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #00667C");
//				lblPlace.setPadding(new Insets(0, insetLabel, insetLabel, 0));
				HBox hboxFourthFE_0 = new HBox(lblRoomFE_0, lblPlaceFE_0);
				hboxFourthFE_0.setPadding(new Insets(0, 0, 1, 0));

				VBox vboxSubFE_0 = new VBox(hboxSecondFE_0, hboxThirdFE_0, hboxFourthFE_0);
				vboxSubFE_0.setStyle("-fx-background-color:#fff");
				vboxSubFE_0.setPadding(new Insets(0, 0, 0, 28));
				vboxSubFE_0.setStyle(
						"-fx-background-color:#fff;" + "\r\n-fx-border-color: #D3D3D3;\r\n" + "-fx-border-width: 0;");

				VBox vboxSubSubFE_0 = new VBox(hboxFirstFE_0, vboxSubFE_0);
				vboxSubSubFE_0.setStyle("-fx-background-color:#fff");
//				vboxSubSub.setPadding(new Insets(insetLabel+10, insetLabel, insetLabel+10, 50));
				vboxSubSubFE_0.setStyle(
						"-fx-background-color:#fff;" + "\r\n-fx-border-color: #D3D3D3;\r\n" + "-fx-border-width: 0;");

				HBox hboxMainSubFE_0 = new HBox(insetLabelFE_0, rectFE_0, vboxSubSubFE_0);
				hboxMainSubFE_0.setStyle(
						"-fx-background-color:#fff;" + "\r\n-fx-border-color: #D3D3D3;\r\n" + "-fx-border-width: 0;");

				// end Loop 2
				// begin Loop 3
				Label lblDOWFE_1 = new Label("Thứ Hai"), lblDayFE_1 = new Label("01/01/2021"),
						lblMiddle_1 = new Label("");

				lblDOWFE_1.setStyle("-fx-font-size: 16pt;\r\n" + " -fx-font-family: 'Candara';" + "-fx-text-fill: #000;"
						+ "\r\n-fx-background-color: #E2DFA2;");
				lblDOWFE_1.setAlignment(Pos.CENTER_LEFT);
				lblDOWFE_1.setPadding(new Insets(0, 0, 0, 10));
				lblMiddle_1.setMinWidth(180);
				lblMiddle_1.setStyle("-fx-font-size: 16pt;\r\n" + " -fx-font-family: 'Candara';"
						+ "-fx-text-fill: #000;" + "\r\n-fx-background-color: #E2DFA2;");
				lblDayFE_1.setStyle("-fx-font-size: 14pt;\r\n" + " -fx-font-family: 'Ubuntu';"
						+ "-fx-text-fill: #867666;" + "\r\n-fx-background-color: #E2DFA2;");
				lblDayFE_1.setPadding(new Insets(2.5, 0, 0, lblDOWFE_1.getBoundsInParent().getWidth()));

				HBox hboxLabelFirstFE_1 = new HBox(lblDOWFE_1, lblDayFE_1);
				hboxLabelFirstFE_1.setStyle("\r\n-fx-background-color: #E2DFA2;");

				int insetLabelFE_1 = 10;

				// draw line
				Line endLineBorderFE_1 = new Line(0, 80, 3000, 80);
				endLineBorderFE_1.setStroke(Color.rgb(161, 160, 160));

				Rectangle rectFE_1 = new Rectangle(12, 110);
				rectFE_1.setFill(Color.rgb(250, 129, 47));
				Label lblCourseNameFE_1 = new Label("Giao duc the chat dai cuong");

				Label lblShiftFE_1 = new Label("7h00 - 11h30"), lblTimeFE_1 = new Label("Thời gian: "),
						lblPlaceFE_1 = new Label("SAN04"), lblShiftNumFE_1 = new Label("1 - 2, "),
						lblPeriodFE_1 = new Label("Tiết "), lblRoomFE_1 = new Label("Phòng: "),
						lblThiMonFE_1 = new Label("Thi môn: "), lblToThiFE_1 = new Label("Tổ thi: "),
						lblGhepThiFE_1 = new Label("Ghép thi: "), lblSoLuongFE_1 = new Label("Số lượng: "),
						lblToThiFE_Rep_1 = new Label("002, "), lblGhepThiFE_Rep_1 = new Label("39, "),
						lblSoLuongFE_Rep_1 = new Label("57");

				// hbox 1
				Image timeIconFE_1 = new Image(getClass().getResourceAsStream("images/ic_clock_24_0.png"));
				ImageView timeImViewFE_1 = new ImageView(timeIconFE_1);
				timeImViewFE_1.setFitWidth(16);
				timeImViewFE_1.setFitHeight(16);

				HBox hboxImViewFE_1 = new HBox(timeImViewFE_1);
				hboxImViewFE_1.setPadding(new Insets(2.75, 0, 0, 0));

				lblShiftFE_1.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #C51802");
//				lblShift.setPadding(new Insets(0, 0, 0, 20));
				lblTimeFE_1.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #C51802");
				lblTimeFE_1.setPadding(new Insets(0, 0, 0, 10));
				HBox hboxFirstFE_1 = new HBox(hboxImViewFE_1, lblTimeFE_1, lblShiftFE_1);
				hboxFirstFE_1.setPrefHeight(10);
				hboxFirstFE_1.setMaxHeight(18);
				hboxFirstFE_1.setPadding(new Insets(5.25, 0, 3, 0));

				// hbox 2
				lblCourseNameFE_1.setStyle("-fx-font-size: 18px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Bitter';" + "-fx-text-fill: #000");
//				lblCourseName.setPadding(new Insets(insetLabel, insetLabel, 0, 0));
				lblThiMonFE_1.setStyle("-fx-font-size: 18px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Bitter';" + "-fx-text-fill: #000");
//				lblShiftNum.setPadding(new Insets(insetLabel + 20, insetLabel + 5, insetLabel + 10, insetLabel + 5));
				HBox hboxSecondFE_1 = new HBox(lblThiMonFE_1, lblCourseNameFE_1);
				hboxSecondFE_1.setPadding(new Insets(0, 0, 3, 0));

				// hbox 3
				lblPeriodFE_1.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblShiftNumFE_1.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblToThiFE_1.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblToThiFE_Rep_1.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblGhepThiFE_1.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblGhepThiFE_Rep_1.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblSoLuongFE_1.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblSoLuongFE_Rep_1.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				HBox hboxThirdFE_1 = new HBox(lblPeriodFE_1, lblShiftNumFE_1, lblToThiFE_1, lblToThiFE_Rep_1,
						lblGhepThiFE_1, lblGhepThiFE_Rep_1, lblSoLuongFE_1, lblSoLuongFE_Rep_1);
				hboxThirdFE_1.setPadding(new Insets(0, 0, 3, 0));

				// hbox 4
				lblRoomFE_1.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #00667C");
//				lblPlace.setPadding(new Insets(0, insetLabel, insetLabel, 0));
				lblPlaceFE_1.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #00667C");
//				lblPlace.setPadding(new Insets(0, insetLabel, insetLabel, 0));
				HBox hboxFourthFE_1 = new HBox(lblRoomFE_1, lblPlaceFE_1);
				hboxFourthFE_1.setPadding(new Insets(0, 0, 1, 0));

				VBox vboxSubFE_1 = new VBox(hboxSecondFE_1, hboxThirdFE_1, hboxFourthFE_1);
				vboxSubFE_1.setStyle("-fx-background-color:#fff");
				vboxSubFE_1.setPadding(new Insets(0, 0, 0, 28));
				vboxSubFE_1.setStyle(
						"-fx-background-color:#fff;" + "\r\n-fx-border-color: #D3D3D3;\r\n" + "-fx-border-width: 0;");

				VBox vboxSubSubFE_1 = new VBox(hboxFirstFE_1, vboxSubFE_1);
				vboxSubSubFE_1.setStyle("-fx-background-color:#fff");
//				vboxSubSub.setPadding(new Insets(insetLabel+10, insetLabel, insetLabel+10, 50));
				vboxSubSubFE_1.setStyle(
						"-fx-background-color:#fff;" + "\r\n-fx-border-color: #D3D3D3;\r\n" + "-fx-border-width: 0;");

				HBox hboxMainSubFE_1 = new HBox(insetLabelFE_1, rectFE_1, vboxSubSubFE_1);
				hboxMainSubFE_1.setStyle(
						"-fx-background-color:#fff;" + "\r\n-fx-border-color: #D3D3D3;\r\n" + "-fx-border-width: 0;");

				// end Loop 3
				// begin Loop 4
				Label lblDOWFE_2 = new Label("Thứ Hai"), lblDayFE_2 = new Label("01/01/2021"),
						lblMiddle_2 = new Label("");

				lblDOWFE_2.setStyle("-fx-font-size: 16pt;\r\n" + " -fx-font-family: 'Candara';" + "-fx-text-fill: #000;"
						+ "\r\n-fx-background-color: #E2DFA2;");
				lblDOWFE_2.setAlignment(Pos.CENTER_LEFT);
				lblDOWFE_2.setPadding(new Insets(0, 0, 0, 10));
				lblMiddle_2.setMinWidth(180);
				lblMiddle_2.setStyle("-fx-font-size: 16pt;\r\n" + " -fx-font-family: 'Candara';"
						+ "-fx-text-fill: #000;" + "\r\n-fx-background-color: #E2DFA2;");
				lblDayFE_2.setStyle("-fx-font-size: 14pt;\r\n" + " -fx-font-family: 'Ubuntu';"
						+ "-fx-text-fill: #867666;" + "\r\n-fx-background-color: #E2DFA2;");
				lblDayFE_2.setPadding(new Insets(2.5, 0, 0, lblDOWFE_2.getBoundsInParent().getWidth()));

				HBox hboxLabelFirstFE_2 = new HBox(lblDOWFE_2, lblDayFE_2);
				hboxLabelFirstFE_2.setStyle("\r\n-fx-background-color: #E2DFA2;");

				int insetLabelFE_2 = 10;

				// draw line
				Line endLineBorderFE_2 = new Line(0, 80, 3000, 80);
				endLineBorderFE_2.setStroke(Color.rgb(161, 160, 160));

				Rectangle rectFE_2 = new Rectangle(12, 110);
				rectFE_2.setFill(Color.rgb(187, 207, 74));
				Label lblCourseNameFE_2 = new Label("Giao duc the chat dai cuong");

				Label lblShiftFE_2 = new Label("7h00 - 11h30"), lblTimeFE_2 = new Label("Thời gian: "),
						lblPlaceFE_2 = new Label("SAN04"), lblShiftNumFE_2 = new Label("1 - 2, "),
						lblPeriodFE_2 = new Label("Tiết "), lblRoomFE_2 = new Label("Phòng: "),
						lblThiMonFE_2 = new Label("Thi môn: "), lblToThiFE_2 = new Label("Tổ thi: "),
						lblGhepThiFE_2 = new Label("Ghép thi: "), lblSoLuongFE_2 = new Label("Số lượng: "),
						lblToThiFE_Rep_2 = new Label("002, "), lblGhepThiFE_Rep_2 = new Label("39, "),
						lblSoLuongFE_Rep_2 = new Label("57");

				// hbox 1
				Image timeIconFE_2 = new Image(getClass().getResourceAsStream("images/ic_clock_24_0.png"));
				ImageView timeImViewFE_2 = new ImageView(timeIconFE_2);
				timeImViewFE_2.setFitWidth(16);
				timeImViewFE_2.setFitHeight(16);

				HBox hboxImViewFE_2 = new HBox(timeImViewFE_2);
				hboxImViewFE_2.setPadding(new Insets(2.75, 0, 0, 0));

				lblShiftFE_2.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #C51802");
//				lblShift.setPadding(new Insets(0, 0, 0, 20));
				lblTimeFE_2.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #C51802");
				lblTimeFE_2.setPadding(new Insets(0, 0, 0, 10));
				HBox hboxFirstFE_2 = new HBox(hboxImViewFE_2, lblTimeFE_2, lblShiftFE_2);
				hboxFirstFE_2.setPrefHeight(10);
				hboxFirstFE_2.setMaxHeight(18);
				hboxFirstFE_2.setPadding(new Insets(5.25, 0, 3, 0));

				// hbox 2
				lblCourseNameFE_2.setStyle("-fx-font-size: 18px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Bitter';" + "-fx-text-fill: #000");
//				lblCourseName.setPadding(new Insets(insetLabel, insetLabel, 0, 0));
				lblThiMonFE_2.setStyle("-fx-font-size: 18px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Bitter';" + "-fx-text-fill: #000");
//				lblShiftNum.setPadding(new Insets(insetLabel + 20, insetLabel + 5, insetLabel + 10, insetLabel + 5));
				HBox hboxSecondFE_2 = new HBox(lblThiMonFE_2, lblCourseNameFE_2);
				hboxSecondFE_2.setPadding(new Insets(0, 0, 3, 0));

				// hbox 3
				lblPeriodFE_2.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblShiftNumFE_2.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblToThiFE_2.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblToThiFE_Rep_2.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblGhepThiFE_2.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblGhepThiFE_Rep_2.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblSoLuongFE_2.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblSoLuongFE_Rep_2.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				HBox hboxThirdFE_2 = new HBox(lblPeriodFE_2, lblShiftNumFE_2, lblToThiFE_2, lblToThiFE_Rep_2,
						lblGhepThiFE_2, lblGhepThiFE_Rep_2, lblSoLuongFE_2, lblSoLuongFE_Rep_2);
				hboxThirdFE_2.setPadding(new Insets(0, 0, 3, 0));

				// hbox 4
				lblRoomFE_2.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #00667C");
//				lblPlace.setPadding(new Insets(0, insetLabel, insetLabel, 0));
				lblPlaceFE_2.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #00667C");
//				lblPlace.setPadding(new Insets(0, insetLabel, insetLabel, 0));
				HBox hboxFourthFE_2 = new HBox(lblRoomFE_2, lblPlaceFE_2);
				hboxFourthFE_2.setPadding(new Insets(0, 0, 1, 0));

				VBox vboxSubFE_2 = new VBox(hboxSecondFE_2, hboxThirdFE_2, hboxFourthFE_2);
				vboxSubFE_2.setStyle("-fx-background-color:#fff");
				vboxSubFE_2.setPadding(new Insets(0, 0, 0, 28));
				vboxSubFE_2.setStyle(
						"-fx-background-color:#fff;" + "\r\n-fx-border-color: #D3D3D3;\r\n" + "-fx-border-width: 0;");

				VBox vboxSubSubFE_2 = new VBox(hboxFirstFE_2, vboxSubFE_2);
				vboxSubSubFE_2.setStyle("-fx-background-color:#fff");
//				vboxSubSub.setPadding(new Insets(insetLabel+10, insetLabel, insetLabel+10, 50));
				vboxSubSubFE_2.setStyle(
						"-fx-background-color:#fff;" + "\r\n-fx-border-color: #D3D3D3;\r\n" + "-fx-border-width: 0;");

				HBox hboxMainSubFE_2 = new HBox(insetLabelFE_2, rectFE_2, vboxSubSubFE_2);
				hboxMainSubFE_2.setStyle(
						"-fx-background-color:#fff;" + "\r\n-fx-border-color: #D3D3D3;\r\n" + "-fx-border-width: 0;");

				// end Loop 4
				// begin Loop 5

				Label lblDOWFE_3 = new Label("Thứ Hai"), lblDayFE_3 = new Label("01/01/2021"),
						lblMiddle_3 = new Label("");

				lblDOWFE_3.setStyle("-fx-font-size: 16pt;\r\n" + " -fx-font-family: 'Candara';" + "-fx-text-fill: #000;"
						+ "\r\n-fx-background-color: #E2DFA2;");
				lblDOWFE_3.setAlignment(Pos.CENTER_LEFT);
				lblDOWFE_3.setPadding(new Insets(0, 0, 0, 10));
				lblMiddle_3.setMinWidth(180);
				lblMiddle_3.setStyle("-fx-font-size: 16pt;\r\n" + " -fx-font-family: 'Candara';"
						+ "-fx-text-fill: #000;" + "\r\n-fx-background-color: #E2DFA2;");
				lblDayFE_3.setStyle("-fx-font-size: 14pt;\r\n" + " -fx-font-family: 'Ubuntu';"
						+ "-fx-text-fill: #867666;" + "\r\n-fx-background-color: #E2DFA2;");
				lblDayFE_3.setPadding(new Insets(2.5, 0, 0, lblDOWFE_3.getBoundsInParent().getWidth()));

				HBox hboxLabelFirstFE_3 = new HBox(lblDOWFE_3, lblDayFE_3);
				hboxLabelFirstFE_3.setStyle("\r\n-fx-background-color: #E2DFA2;");

				int insetLabelFE_3 = 10;

				// draw line
				Line endLineBorderFE_3 = new Line(0, 80, 3000, 80);
				endLineBorderFE_3.setStroke(Color.rgb(161, 160, 160));

				Rectangle rectFE_3 = new Rectangle(12, 110);
				rectFE_3.setFill(Color.rgb(183, 120, 191));
				Label lblCourseNameFE_3 = new Label("Giao duc the chat dai cuong");

				Label lblShiftFE_3 = new Label("7h00 - 11h30"), lblTimeFE_3 = new Label("Thời gian: "),
						lblPlaceFE_3 = new Label("SAN04"), lblShiftNumFE_3 = new Label("1 - 2, "),
						lblPeriodFE_3 = new Label("Tiết "), lblRoomFE_3 = new Label("Phòng: "),
						lblThiMonFE_3 = new Label("Thi môn: "), lblToThiFE_3 = new Label("Tổ thi: "),
						lblGhepThiFE_3 = new Label("Ghép thi: "), lblSoLuongFE_3 = new Label("Số lượng: "),
						lblToThiFE_Rep_3 = new Label("002, "), lblGhepThiFE_Rep_3 = new Label("39, "),
						lblSoLuongFE_Rep_3 = new Label("57");

				// hbox 1
				Image timeIconFE_3 = new Image(getClass().getResourceAsStream("images/ic_clock_24_0.png"));
				ImageView timeImViewFE_3 = new ImageView(timeIconFE_3);
				timeImViewFE_3.setFitWidth(16);
				timeImViewFE_3.setFitHeight(16);

				HBox hboxImViewFE_3 = new HBox(timeImViewFE_3);
				hboxImViewFE_3.setPadding(new Insets(2.75, 0, 0, 0));

				lblShiftFE_3.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #C51802");
//				lblShift.setPadding(new Insets(0, 0, 0, 20));
				lblTimeFE_3.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #C51802");
				lblTimeFE_3.setPadding(new Insets(0, 0, 0, 10));
				HBox hboxFirstFE_3 = new HBox(hboxImViewFE_3, lblTimeFE_3, lblShiftFE_3);
				hboxFirstFE_3.setPrefHeight(10);
				hboxFirstFE_3.setMaxHeight(18);
				hboxFirstFE_3.setPadding(new Insets(5.25, 0, 3, 0));

				// hbox 2
				lblCourseNameFE_3.setStyle("-fx-font-size: 18px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Bitter';" + "-fx-text-fill: #000");
//				lblCourseName.setPadding(new Insets(insetLabel, insetLabel, 0, 0));
				lblThiMonFE_3.setStyle("-fx-font-size: 18px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Bitter';" + "-fx-text-fill: #000");
//				lblShiftNum.setPadding(new Insets(insetLabel + 20, insetLabel + 5, insetLabel + 10, insetLabel + 5));
				HBox hboxSecondFE_3 = new HBox(lblThiMonFE_3, lblCourseNameFE_3);
				hboxSecondFE_3.setPadding(new Insets(0, 0, 3, 0));

				// hbox 3
				lblPeriodFE_3.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblShiftNumFE_3.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblToThiFE_3.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblToThiFE_Rep_3.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblGhepThiFE_3.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblGhepThiFE_Rep_3.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblSoLuongFE_3.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblSoLuongFE_Rep_3.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				HBox hboxThirdFE_3 = new HBox(lblPeriodFE_3, lblShiftNumFE_3, lblToThiFE_3, lblToThiFE_Rep_3,
						lblGhepThiFE_3, lblGhepThiFE_Rep_3, lblSoLuongFE_3, lblSoLuongFE_Rep_3);
				hboxThirdFE_3.setPadding(new Insets(0, 0, 3, 0));

				// hbox 4
				lblRoomFE_3.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #00667C");
//				lblPlace.setPadding(new Insets(0, insetLabel, insetLabel, 0));
				lblPlaceFE_3.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #00667C");
//				lblPlace.setPadding(new Insets(0, insetLabel, insetLabel, 0));
				HBox hboxFourthFE_3 = new HBox(lblRoomFE_3, lblPlaceFE_3);
				hboxFourthFE_3.setPadding(new Insets(0, 0, 1, 0));

				VBox vboxSubFE_3 = new VBox(hboxSecondFE_3, hboxThirdFE_3, hboxFourthFE_3);
				vboxSubFE_3.setStyle("-fx-background-color:#fff");
				vboxSubFE_3.setPadding(new Insets(0, 0, 0, 28));
				vboxSubFE_3.setStyle(
						"-fx-background-color:#fff;" + "\r\n-fx-border-color: #D3D3D3;\r\n" + "-fx-border-width: 0;");

				VBox vboxSubSubFE_3 = new VBox(hboxFirstFE_3, vboxSubFE_3);
				vboxSubSubFE_3.setStyle("-fx-background-color:#fff");
//				vboxSubSub.setPadding(new Insets(insetLabel+10, insetLabel, insetLabel+10, 50));
				vboxSubSubFE_3.setStyle(
						"-fx-background-color:#fff;" + "\r\n-fx-border-color: #D3D3D3;\r\n" + "-fx-border-width: 0;");

				HBox hboxMainSubFE_3 = new HBox(insetLabelFE_3, rectFE_3, vboxSubSubFE_3);
				hboxMainSubFE_3.setStyle(
						"-fx-background-color:#fff;" + "\r\n-fx-border-color: #D3D3D3;\r\n" + "-fx-border-width: 0;");
				
				// end Loop 5

				// begin Loop 6
				
				Label lblDOWFE_4 = new Label("Thứ Hai"), lblDayFE_4 = new Label("01/01/2021"),
						lblMiddle_4 = new Label("");

				lblDOWFE_4.setStyle("-fx-font-size: 16pt;\r\n" + " -fx-font-family: 'Candara';" + "-fx-text-fill: #000;"
						+ "\r\n-fx-background-color: #E2DFA2;");
				lblDOWFE_4.setAlignment(Pos.CENTER_LEFT);
				lblDOWFE_4.setPadding(new Insets(0, 0, 0, 10));
				lblMiddle_4.setMinWidth(180);
				lblMiddle_4.setStyle("-fx-font-size: 16pt;\r\n" + " -fx-font-family: 'Candara';"
						+ "-fx-text-fill: #000;" + "\r\n-fx-background-color: #E2DFA2;");
				lblDayFE_4.setStyle("-fx-font-size: 14pt;\r\n" + " -fx-font-family: 'Ubuntu';"
						+ "-fx-text-fill: #867666;" + "\r\n-fx-background-color: #E2DFA2;");
				lblDayFE_4.setPadding(new Insets(2.5, 0, 0, lblDOWFE_4.getBoundsInParent().getWidth()));

				HBox hboxLabelFirstFE_4 = new HBox(lblDOWFE_4, lblDayFE_4);
				hboxLabelFirstFE_4.setStyle("\r\n-fx-background-color: #E2DFA2;");

				int insetLabelFE_4 = 10;

				// draw line
				Line endLineBorderFE_4 = new Line(0, 80, 3000, 80);
				endLineBorderFE_4.setStroke(Color.rgb(161, 160, 160));

				Rectangle rectFE_4 = new Rectangle(12, 110);
				rectFE_4.setFill(Color.rgb(4, 191, 191));
				Label lblCourseNameFE_4 = new Label("Giao duc the chat dai cuong");

				Label lblShiftFE_4 = new Label("7h00 - 11h30"), lblTimeFE_4 = new Label("Thời gian: "),
						lblPlaceFE_4 = new Label("SAN04"), lblShiftNumFE_4 = new Label("1 - 2, "),
						lblPeriodFE_4 = new Label("Tiết "), lblRoomFE_4 = new Label("Phòng: "),
						lblThiMonFE_4 = new Label("Thi môn: "), lblToThiFE_4 = new Label("Tổ thi: "),
						lblGhepThiFE_4 = new Label("Ghép thi: "), lblSoLuongFE_4 = new Label("Số lượng: "),
						lblToThiFE_Rep_4 = new Label("002, "), lblGhepThiFE_Rep_4 = new Label("39, "),
						lblSoLuongFE_Rep_4 = new Label("57");

				// hbox 1
				Image timeIconFE_4 = new Image(getClass().getResourceAsStream("images/ic_clock_24_0.png"));
				ImageView timeImViewFE_4 = new ImageView(timeIconFE_4);
				timeImViewFE_4.setFitWidth(16);
				timeImViewFE_4.setFitHeight(16);

				HBox hboxImViewFE_4 = new HBox(timeImViewFE_4);
				hboxImViewFE_4.setPadding(new Insets(2.75, 0, 0, 0));

				lblShiftFE_4.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #C51802");
//				lblShift.setPadding(new Insets(0, 0, 0, 20));
				lblTimeFE_4.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #C51802");
				lblTimeFE_4.setPadding(new Insets(0, 0, 0, 10));
				HBox hboxFirstFE_4 = new HBox(hboxImViewFE_4, lblTimeFE_4, lblShiftFE_4);
				hboxFirstFE_4.setPrefHeight(10);
				hboxFirstFE_4.setMaxHeight(18);
				hboxFirstFE_4.setPadding(new Insets(5.25, 0, 3, 0));

				// hbox 2
				lblCourseNameFE_4.setStyle("-fx-font-size: 18px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Bitter';" + "-fx-text-fill: #000");
//				lblCourseName.setPadding(new Insets(insetLabel, insetLabel, 0, 0));
				lblThiMonFE_4.setStyle("-fx-font-size: 18px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Bitter';" + "-fx-text-fill: #000");
//				lblShiftNum.setPadding(new Insets(insetLabel + 20, insetLabel + 5, insetLabel + 10, insetLabel + 5));
				HBox hboxSecondFE_4 = new HBox(lblThiMonFE_4, lblCourseNameFE_4);
				hboxSecondFE_4.setPadding(new Insets(0, 0, 3, 0));

				// hbox 3
				lblPeriodFE_4.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblShiftNumFE_4.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblToThiFE_4.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblToThiFE_Rep_4.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblGhepThiFE_4.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblGhepThiFE_Rep_4.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblSoLuongFE_4.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblSoLuongFE_Rep_4.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				HBox hboxThirdFE_4 = new HBox(lblPeriodFE_4, lblShiftNumFE_4, lblToThiFE_4, lblToThiFE_Rep_4,
						lblGhepThiFE_4, lblGhepThiFE_Rep_4, lblSoLuongFE_4, lblSoLuongFE_Rep_4);
				hboxThirdFE_4.setPadding(new Insets(0, 0, 3, 0));

				// hbox 4
				lblRoomFE_4.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #00667C");
//				lblPlace.setPadding(new Insets(0, insetLabel, insetLabel, 0));
				lblPlaceFE_4.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #00667C");
//				lblPlace.setPadding(new Insets(0, insetLabel, insetLabel, 0));
				HBox hboxFourthFE_4 = new HBox(lblRoomFE_4, lblPlaceFE_4);
				hboxFourthFE_4.setPadding(new Insets(0, 0, 1, 0));

				VBox vboxSubFE_4 = new VBox(hboxSecondFE_4, hboxThirdFE_4, hboxFourthFE_4);
				vboxSubFE_4.setStyle("-fx-background-color:#fff");
				vboxSubFE_4.setPadding(new Insets(0, 0, 0, 28));
				vboxSubFE_4.setStyle(
						"-fx-background-color:#fff;" + "\r\n-fx-border-color: #D3D3D3;\r\n" + "-fx-border-width: 0;");

				VBox vboxSubSubFE_4 = new VBox(hboxFirstFE_4, vboxSubFE_4);
				vboxSubSubFE_4.setStyle("-fx-background-color:#fff");
//				vboxSubSub.setPadding(new Insets(insetLabel+10, insetLabel, insetLabel+10, 50));
				vboxSubSubFE_4.setStyle(
						"-fx-background-color:#fff;" + "\r\n-fx-border-color: #D3D3D3;\r\n" + "-fx-border-width: 0;");

				HBox hboxMainSubFE_4 = new HBox(insetLabelFE_4, rectFE_4, vboxSubSubFE_4);
				hboxMainSubFE_4.setStyle(
						"-fx-background-color:#fff;" + "\r\n-fx-border-color: #D3D3D3;\r\n" + "-fx-border-width: 0;");
				
				// end Loop 6
				
				// begin Loop 7
				
				Label lblDOWFE_5 = new Label("Thứ Hai"), lblDayFE_5 = new Label("01/01/2021"),
						lblMiddle_5 = new Label("");

				lblDOWFE_5.setStyle("-fx-font-size: 16pt;\r\n" + " -fx-font-family: 'Candara';" + "-fx-text-fill: #000;"
						+ "\r\n-fx-background-color: #E2DFA2;");
				lblDOWFE_5.setAlignment(Pos.CENTER_LEFT);
				lblDOWFE_5.setPadding(new Insets(0, 0, 0, 10));
				lblMiddle_5.setMinWidth(180);
				lblMiddle_5.setStyle("-fx-font-size: 16pt;\r\n" + " -fx-font-family: 'Candara';"
						+ "-fx-text-fill: #000;" + "\r\n-fx-background-color: #E2DFA2;");
				lblDayFE_5.setStyle("-fx-font-size: 14pt;\r\n" + " -fx-font-family: 'Ubuntu';"
						+ "-fx-text-fill: #867666;" + "\r\n-fx-background-color: #E2DFA2;");
				lblDayFE_5.setPadding(new Insets(2.5, 0, 0, lblDOWFE_5.getBoundsInParent().getWidth()));

				HBox hboxLabelFirstFE_5 = new HBox(lblDOWFE_5, lblDayFE_5);
				hboxLabelFirstFE_5.setStyle("\r\n-fx-background-color: #E2DFA2;");

				int insetLabelFE_5 = 10;

				// draw line
				Line endLineBorderFE_5 = new Line(0, 80, 3000, 80);
				endLineBorderFE_5.setStroke(Color.rgb(161, 160, 160));

				Rectangle rectFE_5 = new Rectangle(12, 110);
				rectFE_5.setFill(Color.rgb(241, 141, 158));
				Label lblCourseNameFE_5 = new Label("Giao duc the chat dai cuong");

				Label lblShiftFE_5 = new Label("7h00 - 11h30"), lblTimeFE_5 = new Label("Thời gian: "),
						lblPlaceFE_5 = new Label("SAN04"), lblShiftNumFE_5 = new Label("1 - 2, "),
						lblPeriodFE_5 = new Label("Tiết "), lblRoomFE_5 = new Label("Phòng: "),
						lblThiMonFE_5 = new Label("Thi môn: "), lblToThiFE_5 = new Label("Tổ thi: "),
						lblGhepThiFE_5 = new Label("Ghép thi: "), lblSoLuongFE_5 = new Label("Số lượng: "),
						lblToThiFE_Rep_5 = new Label("002, "), lblGhepThiFE_Rep_5 = new Label("39, "),
						lblSoLuongFE_Rep_5 = new Label("57");

				// hbox 1
				Image timeIconFE_5 = new Image(getClass().getResourceAsStream("images/ic_clock_24_0.png"));
				ImageView timeImViewFE_5 = new ImageView(timeIconFE_5);
				timeImViewFE_5.setFitWidth(16);
				timeImViewFE_5.setFitHeight(16);

				HBox hboxImViewFE_5 = new HBox(timeImViewFE_5);
				hboxImViewFE_5.setPadding(new Insets(2.75, 0, 0, 0));

				lblShiftFE_5.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #C51802");
//				lblShift.setPadding(new Insets(0, 0, 0, 20));
				lblTimeFE_5.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #C51802");
				lblTimeFE_5.setPadding(new Insets(0, 0, 0, 10));
				HBox hboxFirstFE_5 = new HBox(hboxImViewFE_5, lblTimeFE_5, lblShiftFE_5);
				hboxFirstFE_5.setPrefHeight(10);
				hboxFirstFE_5.setMaxHeight(18);
				hboxFirstFE_5.setPadding(new Insets(5.25, 0, 3, 0));

				// hbox 2
				lblCourseNameFE_5.setStyle("-fx-font-size: 18px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Bitter';" + "-fx-text-fill: #000");
//				lblCourseName.setPadding(new Insets(insetLabel, insetLabel, 0, 0));
				lblThiMonFE_5.setStyle("-fx-font-size: 18px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Bitter';" + "-fx-text-fill: #000");
//				lblShiftNum.setPadding(new Insets(insetLabel + 20, insetLabel + 5, insetLabel + 10, insetLabel + 5));
				HBox hboxSecondFE_5 = new HBox(lblThiMonFE_5, lblCourseNameFE_5);
				hboxSecondFE_5.setPadding(new Insets(0, 0, 3, 0));

				// hbox 3
				lblPeriodFE_5.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblShiftNumFE_5.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblToThiFE_5.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblToThiFE_Rep_5.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblGhepThiFE_5.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblGhepThiFE_Rep_5.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblSoLuongFE_5.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblSoLuongFE_Rep_5.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				HBox hboxThirdFE_5 = new HBox(lblPeriodFE_5, lblShiftNumFE_5, lblToThiFE_5, lblToThiFE_Rep_5,
						lblGhepThiFE_5, lblGhepThiFE_Rep_5, lblSoLuongFE_5, lblSoLuongFE_Rep_5);
				hboxThirdFE_5.setPadding(new Insets(0, 0, 3, 0));

				// hbox 4
				lblRoomFE_5.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #00667C");
//				lblPlace.setPadding(new Insets(0, insetLabel, insetLabel, 0));
				lblPlaceFE_5.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #00667C");
//				lblPlace.setPadding(new Insets(0, insetLabel, insetLabel, 0));
				HBox hboxFourthFE_5 = new HBox(lblRoomFE_5, lblPlaceFE_5);
				hboxFourthFE_5.setPadding(new Insets(0, 0, 1, 0));

				VBox vboxSubFE_5 = new VBox(hboxSecondFE_5, hboxThirdFE_5, hboxFourthFE_5);
				vboxSubFE_5.setStyle("-fx-background-color:#fff");
				vboxSubFE_5.setPadding(new Insets(0, 0, 0, 28));
				vboxSubFE_5.setStyle(
						"-fx-background-color:#fff;" + "\r\n-fx-border-color: #D3D3D3;\r\n" + "-fx-border-width: 0;");

				VBox vboxSubSubFE_5 = new VBox(hboxFirstFE_5, vboxSubFE_5);
				vboxSubSubFE_5.setStyle("-fx-background-color:#fff");
//				vboxSubSub.setPadding(new Insets(insetLabel+10, insetLabel, insetLabel+10, 50));
				vboxSubSubFE_5.setStyle(
						"-fx-background-color:#fff;" + "\r\n-fx-border-color: #D3D3D3;\r\n" + "-fx-border-width: 0;");

				HBox hboxMainSubFE_5 = new HBox(insetLabelFE_5, rectFE_5, vboxSubSubFE_5);
				hboxMainSubFE_5.setStyle(
						"-fx-background-color:#fff;" + "\r\n-fx-border-color: #D3D3D3;\r\n" + "-fx-border-width: 0;");
				
				// end Loop 7
				
				// begin Loop 8
				
				Label lblDOWFE_6 = new Label("Thứ Hai"), lblDayFE_6 = new Label("01/01/2021"),
						lblMiddle_6 = new Label("");

				lblDOWFE_6.setStyle("-fx-font-size: 16pt;\r\n" + " -fx-font-family: 'Candara';" + "-fx-text-fill: #000;"
						+ "\r\n-fx-background-color: #E2DFA2;");
				lblDOWFE_6.setAlignment(Pos.CENTER_LEFT);
				lblDOWFE_6.setPadding(new Insets(0, 0, 0, 10));
				lblMiddle_6.setMinWidth(180);
				lblMiddle_6.setStyle("-fx-font-size: 16pt;\r\n" + " -fx-font-family: 'Candara';"
						+ "-fx-text-fill: #000;" + "\r\n-fx-background-color: #E2DFA2;");
				lblDayFE_6.setStyle("-fx-font-size: 14pt;\r\n" + " -fx-font-family: 'Ubuntu';"
						+ "-fx-text-fill: #867666;" + "\r\n-fx-background-color: #E2DFA2;");
				lblDayFE_6.setPadding(new Insets(2.5, 0, 0, lblDOWFE_6.getBoundsInParent().getWidth()));

				HBox hboxLabelFirstFE_6 = new HBox(lblDOWFE_6, lblDayFE_6);
				hboxLabelFirstFE_6.setStyle("\r\n-fx-background-color: #E2DFA2;");

				int insetLabelFE_6 = 10;

				// draw line
				Line endLineBorderFE_6 = new Line(0, 80, 3000, 80);
				endLineBorderFE_6.setStroke(Color.rgb(161, 160, 160));

				Rectangle rectFE_6 = new Rectangle(12, 110);
				rectFE_6.setFill(Color.rgb(125, 163, 161));
				Label lblCourseNameFE_6 = new Label("Giao duc the chat dai cuong");

				Label lblShiftFE_6 = new Label("7h00 - 11h30"), lblTimeFE_6 = new Label("Thời gian: "),
						lblPlaceFE_6 = new Label("SAN04"), lblShiftNumFE_6 = new Label("1 - 2, "),
						lblPeriodFE_6 = new Label("Tiết "), lblRoomFE_6 = new Label("Phòng: "),
						lblThiMonFE_6 = new Label("Thi môn: "), lblToThiFE_6 = new Label("Tổ thi: "),
						lblGhepThiFE_6 = new Label("Ghép thi: "), lblSoLuongFE_6 = new Label("Số lượng: "),
						lblToThiFE_Rep_6 = new Label("002, "), lblGhepThiFE_Rep_6 = new Label("39, "),
						lblSoLuongFE_Rep_6 = new Label("57");

				// hbox 1
				Image timeIconFE_6 = new Image(getClass().getResourceAsStream("images/ic_clock_24_0.png"));
				ImageView timeImViewFE_6 = new ImageView(timeIconFE_6);
				timeImViewFE_6.setFitWidth(16);
				timeImViewFE_6.setFitHeight(16);

				HBox hboxImViewFE_6 = new HBox(timeImViewFE_6);
				hboxImViewFE_6.setPadding(new Insets(2.75, 0, 0, 0));

				lblShiftFE_6.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #C51802");
//				lblShift.setPadding(new Insets(0, 0, 0, 20));
				lblTimeFE_6.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #C51802");
				lblTimeFE_6.setPadding(new Insets(0, 0, 0, 10));
				HBox hboxFirstFE_6 = new HBox(hboxImViewFE_6, lblTimeFE_6, lblShiftFE_6);
				hboxFirstFE_6.setPrefHeight(10);
				hboxFirstFE_6.setMaxHeight(18);
				hboxFirstFE_6.setPadding(new Insets(5.25, 0, 3, 0));

				// hbox 2
				lblCourseNameFE_6.setStyle("-fx-font-size: 18px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Bitter';" + "-fx-text-fill: #000");
//				lblCourseName.setPadding(new Insets(insetLabel, insetLabel, 0, 0));
				lblThiMonFE_6.setStyle("-fx-font-size: 18px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Bitter';" + "-fx-text-fill: #000");
//				lblShiftNum.setPadding(new Insets(insetLabel + 20, insetLabel + 5, insetLabel + 10, insetLabel + 5));
				HBox hboxSecondFE_6 = new HBox(lblThiMonFE_6, lblCourseNameFE_6);
				hboxSecondFE_6.setPadding(new Insets(0, 0, 3, 0));

				// hbox 3
				lblPeriodFE_6.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblShiftNumFE_6.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblToThiFE_6.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblToThiFE_Rep_5.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblGhepThiFE_6.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblGhepThiFE_Rep_6.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblSoLuongFE_6.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblSoLuongFE_Rep_6.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				HBox hboxThirdFE_6 = new HBox(lblPeriodFE_6, lblShiftNumFE_6, lblToThiFE_6, lblToThiFE_Rep_6,
						lblGhepThiFE_6, lblGhepThiFE_Rep_6, lblSoLuongFE_6, lblSoLuongFE_Rep_6);
				hboxThirdFE_6.setPadding(new Insets(0, 0, 3, 0));

				// hbox 4
				lblRoomFE_6.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #00667C");
//				lblPlace.setPadding(new Insets(0, insetLabel, insetLabel, 0));
				lblPlaceFE_6.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #00667C");
//				lblPlace.setPadding(new Insets(0, insetLabel, insetLabel, 0));
				HBox hboxFourthFE_6 = new HBox(lblRoomFE_6, lblPlaceFE_6);
				hboxFourthFE_6.setPadding(new Insets(0, 0, 1, 0));

				VBox vboxSubFE_6 = new VBox(hboxSecondFE_6, hboxThirdFE_6, hboxFourthFE_6);
				vboxSubFE_6.setStyle("-fx-background-color:#fff");
				vboxSubFE_6.setPadding(new Insets(0, 0, 0, 28));
				vboxSubFE_6.setStyle(
						"-fx-background-color:#fff;" + "\r\n-fx-border-color: #D3D3D3;\r\n" + "-fx-border-width: 0;");

				VBox vboxSubSubFE_6 = new VBox(hboxFirstFE_6, vboxSubFE_6);
				vboxSubSubFE_6.setStyle("-fx-background-color:#fff");
//				vboxSubSub.setPadding(new Insets(insetLabel+10, insetLabel, insetLabel+10, 50));
				vboxSubSubFE_6.setStyle(
						"-fx-background-color:#fff;" + "\r\n-fx-border-color: #D3D3D3;\r\n" + "-fx-border-width: 0;");

				HBox hboxMainSubFE_6 = new HBox(insetLabelFE_6, rectFE_6, vboxSubSubFE_6);
				hboxMainSubFE_6.setStyle(
						"-fx-background-color:#fff;" + "\r\n-fx-border-color: #D3D3D3;\r\n" + "-fx-border-width: 0;");
				
				// end Loop 8
				
				// begin Loop 9
				
				Label lblDOWFE_7 = new Label("Thứ Hai"), lblDayFE_7 = new Label("01/01/2021"),
						lblMiddle_7 = new Label("");

				lblDOWFE_7.setStyle("-fx-font-size: 16pt;\r\n" + " -fx-font-family: 'Candara';" + "-fx-text-fill: #000;"
						+ "\r\n-fx-background-color: #E2DFA2;");
				lblDOWFE_7.setAlignment(Pos.CENTER_LEFT);
				lblDOWFE_7.setPadding(new Insets(0, 0, 0, 10));
				lblMiddle_7.setMinWidth(180);
				lblMiddle_7.setStyle("-fx-font-size: 16pt;\r\n" + " -fx-font-family: 'Candara';"
						+ "-fx-text-fill: #000;" + "\r\n-fx-background-color: #E2DFA2;");
				lblDayFE_7.setStyle("-fx-font-size: 14pt;\r\n" + " -fx-font-family: 'Ubuntu';"
						+ "-fx-text-fill: #867666;" + "\r\n-fx-background-color: #E2DFA2;");
				lblDayFE_7.setPadding(new Insets(2.5, 0, 0, lblDOWFE_7.getBoundsInParent().getWidth()));

				HBox hboxLabelFirstFE_7 = new HBox(lblDOWFE_7, lblDayFE_7);
				hboxLabelFirstFE_7.setStyle("\r\n-fx-background-color: #E2DFA2;");

				int insetLabelFE_7 = 10;

				// draw line
				Line endLineBorderFE_7 = new Line(0, 80, 3000, 80);
				endLineBorderFE_7.setStroke(Color.rgb(161, 160, 160));

				Rectangle rectFE_7 = new Rectangle(12, 110);
				rectFE_7.setFill(Color.rgb(237, 174, 1));
				Label lblCourseNameFE_7 = new Label("Giao duc the chat dai cuong");

				Label lblShiftFE_7 = new Label("7h00 - 11h30"), lblTimeFE_7 = new Label("Thời gian: "),
						lblPlaceFE_7 = new Label("SAN04"), lblShiftNumFE_7 = new Label("1 - 2, "),
						lblPeriodFE_7 = new Label("Tiết "), lblRoomFE_7 = new Label("Phòng: "),
						lblThiMonFE_7 = new Label("Thi môn: "), lblToThiFE_7 = new Label("Tổ thi: "),
						lblGhepThiFE_7 = new Label("Ghép thi: "), lblSoLuongFE_7 = new Label("Số lượng: "),
						lblToThiFE_Rep_7 = new Label("002, "), lblGhepThiFE_Rep_7 = new Label("39, "),
						lblSoLuongFE_Rep_7 = new Label("57");

				// hbox 1
				Image timeIconFE_7 = new Image(getClass().getResourceAsStream("images/ic_clock_24_0.png"));
				ImageView timeImViewFE_7 = new ImageView(timeIconFE_7);
				timeImViewFE_7.setFitWidth(16);
				timeImViewFE_7.setFitHeight(16);

				HBox hboxImViewFE_7 = new HBox(timeImViewFE_7);
				hboxImViewFE_7.setPadding(new Insets(2.75, 0, 0, 0));

				lblShiftFE_7.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #C51802");
//				lblShift.setPadding(new Insets(0, 0, 0, 20));
				lblTimeFE_7.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #C51802");
				lblTimeFE_7.setPadding(new Insets(0, 0, 0, 10));
				HBox hboxFirstFE_7 = new HBox(hboxImViewFE_7, lblTimeFE_7, lblShiftFE_7);
				hboxFirstFE_7.setPrefHeight(10);
				hboxFirstFE_7.setMaxHeight(18);
				hboxFirstFE_7.setPadding(new Insets(5.25, 0, 3, 0));

				// hbox 2
				lblCourseNameFE_7.setStyle("-fx-font-size: 18px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Bitter';" + "-fx-text-fill: #000");
//				lblCourseName.setPadding(new Insets(insetLabel, insetLabel, 0, 0));
				lblThiMonFE_7.setStyle("-fx-font-size: 18px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Bitter';" + "-fx-text-fill: #000");
//				lblShiftNum.setPadding(new Insets(insetLabel + 20, insetLabel + 5, insetLabel + 10, insetLabel + 5));
				HBox hboxSecondFE_7 = new HBox(lblThiMonFE_7, lblCourseNameFE_7);
				hboxSecondFE_7.setPadding(new Insets(0, 0, 3, 0));

				// hbox 3
				lblPeriodFE_7.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblShiftNumFE_7.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblToThiFE_7.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblToThiFE_Rep_7.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblGhepThiFE_7.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblGhepThiFE_Rep_7.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblSoLuongFE_7.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblSoLuongFE_Rep_7.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				HBox hboxThirdFE_7 = new HBox(lblPeriodFE_7, lblShiftNumFE_7, lblToThiFE_7, lblToThiFE_Rep_7,
						lblGhepThiFE_7, lblGhepThiFE_Rep_7, lblSoLuongFE_7, lblSoLuongFE_Rep_7);
				hboxThirdFE_7.setPadding(new Insets(0, 0, 3, 0));

				// hbox 4
				lblRoomFE_7.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #00667C");
//				lblPlace.setPadding(new Insets(0, insetLabel, insetLabel, 0));
				lblPlaceFE_7.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #00667C");
//				lblPlace.setPadding(new Insets(0, insetLabel, insetLabel, 0));
				HBox hboxFourthFE_7 = new HBox(lblRoomFE_7, lblPlaceFE_7);
				hboxFourthFE_7.setPadding(new Insets(0, 0, 1, 0));

				VBox vboxSubFE_7 = new VBox(hboxSecondFE_7, hboxThirdFE_7, hboxFourthFE_7);
				vboxSubFE_7.setStyle("-fx-background-color:#fff");
				vboxSubFE_7.setPadding(new Insets(0, 0, 0, 28));
				vboxSubFE_7.setStyle(
						"-fx-background-color:#fff;" + "\r\n-fx-border-color: #D3D3D3;\r\n" + "-fx-border-width: 0;");

				VBox vboxSubSubFE_7 = new VBox(hboxFirstFE_7, vboxSubFE_7);
				vboxSubSubFE_7.setStyle("-fx-background-color:#fff");
//				vboxSubSub.setPadding(new Insets(insetLabel+10, insetLabel, insetLabel+10, 50));
				vboxSubSubFE_7.setStyle(
						"-fx-background-color:#fff;" + "\r\n-fx-border-color: #D3D3D3;\r\n" + "-fx-border-width: 0;");

				HBox hboxMainSubFE_7 = new HBox(insetLabelFE_7, rectFE_7, vboxSubSubFE_7);
				hboxMainSubFE_7.setStyle(
						"-fx-background-color:#fff;" + "\r\n-fx-border-color: #D3D3D3;\r\n" + "-fx-border-width: 0;");
				
				// end Loop 9
				
				// begin Loop 10
				
				Label lblDOWFE_8 = new Label("Thứ Hai"), lblDayFE_8 = new Label("01/01/2021"),
						lblMiddle_8 = new Label("");

				lblDOWFE_8.setStyle("-fx-font-size: 16pt;\r\n" + " -fx-font-family: 'Candara';" + "-fx-text-fill: #000;"
						+ "\r\n-fx-background-color: #E2DFA2;");
				lblDOWFE_8.setAlignment(Pos.CENTER_LEFT);
				lblDOWFE_8.setPadding(new Insets(0, 0, 0, 10));
				lblMiddle_8.setMinWidth(180);
				lblMiddle_8.setStyle("-fx-font-size: 16pt;\r\n" + " -fx-font-family: 'Candara';"
						+ "-fx-text-fill: #000;" + "\r\n-fx-background-color: #E2DFA2;");
				lblDayFE_8.setStyle("-fx-font-size: 14pt;\r\n" + " -fx-font-family: 'Ubuntu';"
						+ "-fx-text-fill: #867666;" + "\r\n-fx-background-color: #E2DFA2;");
				lblDayFE_8.setPadding(new Insets(2.5, 0, 0, lblDOWFE_8.getBoundsInParent().getWidth()));

				HBox hboxLabelFirstFE_8 = new HBox(lblDOWFE_8, lblDayFE_8);
				hboxLabelFirstFE_8.setStyle("\r\n-fx-background-color: #E2DFA2;");

				int insetLabelFE_8 = 10;

				// draw line
				Line endLineBorderFE_8 = new Line(0, 80, 3000, 80);
				endLineBorderFE_8.setStroke(Color.rgb(161, 160, 160));

				Rectangle rectFE_8 = new Rectangle(12, 110);
				rectFE_8.setFill(Color.rgb(0, 169, 254));
				Label lblCourseNameFE_8 = new Label("Giao duc the chat dai cuong");

				Label lblShiftFE_8 = new Label("7h00 - 11h30"), lblTimeFE_8 = new Label("Thời gian: "),
						lblPlaceFE_8 = new Label("SAN04"), lblShiftNumFE_8 = new Label("1 - 2, "),
						lblPeriodFE_8 = new Label("Tiết "), lblRoomFE_8 = new Label("Phòng: "),
						lblThiMonFE_8 = new Label("Thi môn: "), lblToThiFE_8 = new Label("Tổ thi: "),
						lblGhepThiFE_8 = new Label("Ghép thi: "), lblSoLuongFE_8 = new Label("Số lượng: "),
						lblToThiFE_Rep_8 = new Label("002, "), lblGhepThiFE_Rep_8 = new Label("39, "),
						lblSoLuongFE_Rep_8 = new Label("57");

				// hbox 1
				Image timeIconFE_8 = new Image(getClass().getResourceAsStream("images/ic_clock_24_0.png"));
				ImageView timeImViewFE_8 = new ImageView(timeIconFE_8);
				timeImViewFE_8.setFitWidth(16);
				timeImViewFE_8.setFitHeight(16);

				HBox hboxImViewFE_8 = new HBox(timeImViewFE_8);
				hboxImViewFE_8.setPadding(new Insets(2.75, 0, 0, 0));

				lblShiftFE_8.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #C51802");
//				lblShift.setPadding(new Insets(0, 0, 0, 20));
				lblTimeFE_8.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #C51802");
				lblTimeFE_8.setPadding(new Insets(0, 0, 0, 10));
				HBox hboxFirstFE_8 = new HBox(hboxImViewFE_8, lblTimeFE_8, lblShiftFE_8);
				hboxFirstFE_8.setPrefHeight(10);
				hboxFirstFE_8.setMaxHeight(18);
				hboxFirstFE_8.setPadding(new Insets(5.25, 0, 3, 0));

				// hbox 2
				lblCourseNameFE_8.setStyle("-fx-font-size: 18px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Bitter';" + "-fx-text-fill: #000");
//				lblCourseName.setPadding(new Insets(insetLabel, insetLabel, 0, 0));
				lblThiMonFE_8.setStyle("-fx-font-size: 18px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Bitter';" + "-fx-text-fill: #000");
//				lblShiftNum.setPadding(new Insets(insetLabel + 20, insetLabel + 5, insetLabel + 10, insetLabel + 5));
				HBox hboxSecondFE_8 = new HBox(lblThiMonFE_8, lblCourseNameFE_8);
				hboxSecondFE_8.setPadding(new Insets(0, 0, 3, 0));

				// hbox 3
				lblPeriodFE_8.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblShiftNumFE_8.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblToThiFE_8.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblToThiFE_Rep_8.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblGhepThiFE_8.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblGhepThiFE_Rep_8.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblSoLuongFE_8.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblSoLuongFE_Rep_8.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				HBox hboxThirdFE_8 = new HBox(lblPeriodFE_8, lblShiftNumFE_8, lblToThiFE_8, lblToThiFE_Rep_8,
						lblGhepThiFE_8, lblGhepThiFE_Rep_8, lblSoLuongFE_8, lblSoLuongFE_Rep_8);
				hboxThirdFE_8.setPadding(new Insets(0, 0, 3, 0));

				// hbox 4
				lblRoomFE_8.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #00667C");
//				lblPlace.setPadding(new Insets(0, insetLabel, insetLabel, 0));
				lblPlaceFE_8.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #00667C");
//				lblPlace.setPadding(new Insets(0, insetLabel, insetLabel, 0));
				HBox hboxFourthFE_8 = new HBox(lblRoomFE_8, lblPlaceFE_8);
				hboxFourthFE_8.setPadding(new Insets(0, 0, 1, 0));

				VBox vboxSubFE_8 = new VBox(hboxSecondFE_8, hboxThirdFE_8, hboxFourthFE_8);
				vboxSubFE_8.setStyle("-fx-background-color:#fff");
				vboxSubFE_8.setPadding(new Insets(0, 0, 0, 28));
				vboxSubFE_8.setStyle(
						"-fx-background-color:#fff;" + "\r\n-fx-border-color: #D3D3D3;\r\n" + "-fx-border-width: 0;");

				VBox vboxSubSubFE_8 = new VBox(hboxFirstFE_8, vboxSubFE_8);
				vboxSubSubFE_8.setStyle("-fx-background-color:#fff");
//				vboxSubSub.setPadding(new Insets(insetLabel+10, insetLabel, insetLabel+10, 50));
				vboxSubSubFE_8.setStyle(
						"-fx-background-color:#fff;" + "\r\n-fx-border-color: #D3D3D3;\r\n" + "-fx-border-width: 0;");

				HBox hboxMainSubFE_8 = new HBox(insetLabelFE_8, rectFE_8, vboxSubSubFE_8);
				hboxMainSubFE_8.setStyle(
						"-fx-background-color:#fff;" + "\r\n-fx-border-color: #D3D3D3;\r\n" + "-fx-border-width: 0;");
				
				// end Loop 10
				
				// begin Loop 11
				
				Label lblDOWFE_9 = new Label("Thứ Hai"), lblDayFE_9 = new Label("01/01/2021"),
						lblMiddle_9 = new Label("");

				lblDOWFE_9.setStyle("-fx-font-size: 16pt;\r\n" + " -fx-font-family: 'Candara';" + "-fx-text-fill: #000;"
						+ "\r\n-fx-background-color: #E2DFA2;");
				lblDOWFE_9.setAlignment(Pos.CENTER_LEFT);
				lblDOWFE_9.setPadding(new Insets(0, 0, 0, 10));
				lblMiddle_9.setMinWidth(180);
				lblMiddle_9.setStyle("-fx-font-size: 16pt;\r\n" + " -fx-font-family: 'Candara';"
						+ "-fx-text-fill: #000;" + "\r\n-fx-background-color: #E2DFA2;");
				lblDayFE_9.setStyle("-fx-font-size: 14pt;\r\n" + " -fx-font-family: 'Ubuntu';"
						+ "-fx-text-fill: #867666;" + "\r\n-fx-background-color: #E2DFA2;");
				lblDayFE_9.setPadding(new Insets(2.5, 0, 0, lblDOWFE_9.getBoundsInParent().getWidth()));

				HBox hboxLabelFirstFE_9 = new HBox(lblDOWFE_9, lblDayFE_9);
				hboxLabelFirstFE_9.setStyle("\r\n-fx-background-color: #E2DFA2;");

				int insetLabelFE_9 = 10;

				// draw line
				Line endLineBorderFE_9 = new Line(0, 80, 3000, 80);
				endLineBorderFE_9.setStroke(Color.rgb(161, 160, 160));

				Rectangle rectFE_9 = new Rectangle(12, 110);
				rectFE_9.setFill(Color.rgb(0, 169, 254));
				Label lblCourseNameFE_9 = new Label("Giao duc the chat dai cuong");

				Label lblShiftFE_9 = new Label("7h00 - 11h30"), lblTimeFE_9 = new Label("Thời gian: "),
						lblPlaceFE_9 = new Label("SAN04"), lblShiftNumFE_9 = new Label("1 - 2, "),
						lblPeriodFE_9 = new Label("Tiết "), lblRoomFE_9 = new Label("Phòng: "),
						lblThiMonFE_9 = new Label("Thi môn: "), lblToThiFE_9 = new Label("Tổ thi: "),
						lblGhepThiFE_9 = new Label("Ghép thi: "), lblSoLuongFE_9 = new Label("Số lượng: "),
						lblToThiFE_Rep_9 = new Label("002, "), lblGhepThiFE_Rep_9 = new Label("39, "),
						lblSoLuongFE_Rep_9 = new Label("57");

				// hbox 1
				Image timeIconFE_9 = new Image(getClass().getResourceAsStream("images/ic_clock_24_0.png"));
				ImageView timeImViewFE_9 = new ImageView(timeIconFE_9);
				timeImViewFE_9.setFitWidth(16);
				timeImViewFE_9.setFitHeight(16);

				HBox hboxImViewFE_9 = new HBox(timeImViewFE_9);
				hboxImViewFE_9.setPadding(new Insets(2.75, 0, 0, 0));

				lblShiftFE_9.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #C51802");
//				lblShift.setPadding(new Insets(0, 0, 0, 20));
				lblTimeFE_9.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #C51802");
				lblTimeFE_9.setPadding(new Insets(0, 0, 0, 10));
				HBox hboxFirstFE_9 = new HBox(hboxImViewFE_9, lblTimeFE_9, lblShiftFE_9);
				hboxFirstFE_9.setPrefHeight(10);
				hboxFirstFE_9.setMaxHeight(18);
				hboxFirstFE_9.setPadding(new Insets(5.25, 0, 3, 0));

				// hbox 2
				lblCourseNameFE_9.setStyle("-fx-font-size: 18px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Bitter';" + "-fx-text-fill: #000");
//				lblCourseName.setPadding(new Insets(insetLabel, insetLabel, 0, 0));
				lblThiMonFE_9.setStyle("-fx-font-size: 18px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Bitter';" + "-fx-text-fill: #000");
//				lblShiftNum.setPadding(new Insets(insetLabel + 20, insetLabel + 5, insetLabel + 10, insetLabel + 5));
				HBox hboxSecondFE_9 = new HBox(lblThiMonFE_9, lblCourseNameFE_9);
				hboxSecondFE_9.setPadding(new Insets(0, 0, 3, 0));

				// hbox 3
				lblPeriodFE_9.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblShiftNumFE_9.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblToThiFE_9.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblToThiFE_Rep_9.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblGhepThiFE_9.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblGhepThiFE_Rep_9.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblSoLuongFE_9.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblSoLuongFE_Rep_9.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				HBox hboxThirdFE_9 = new HBox(lblPeriodFE_9, lblShiftNumFE_9, lblToThiFE_9, lblToThiFE_Rep_9,
						lblGhepThiFE_9, lblGhepThiFE_Rep_9, lblSoLuongFE_9, lblSoLuongFE_Rep_9);
				hboxThirdFE_9.setPadding(new Insets(0, 0, 3, 0));

				// hbox 4
				lblRoomFE_9.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #00667C");
//				lblPlace.setPadding(new Insets(0, insetLabel, insetLabel, 0));
				lblPlaceFE_9.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #00667C");
//				lblPlace.setPadding(new Insets(0, insetLabel, insetLabel, 0));
				HBox hboxFourthFE_9 = new HBox(lblRoomFE_9, lblPlaceFE_9);
				hboxFourthFE_9.setPadding(new Insets(0, 0, 1, 0));

				VBox vboxSubFE_9 = new VBox(hboxSecondFE_9, hboxThirdFE_9, hboxFourthFE_9);
				vboxSubFE_9.setStyle("-fx-background-color:#fff");
				vboxSubFE_9.setPadding(new Insets(0, 0, 0, 28));
				vboxSubFE_9.setStyle(
						"-fx-background-color:#fff;" + "\r\n-fx-border-color: #D3D3D3;\r\n" + "-fx-border-width: 0;");

				VBox vboxSubSubFE_9 = new VBox(hboxFirstFE_9, vboxSubFE_9);
				vboxSubSubFE_9.setStyle("-fx-background-color:#fff");
//				vboxSubSub.setPadding(new Insets(insetLabel+10, insetLabel, insetLabel+10, 50));
				vboxSubSubFE_9.setStyle(
						"-fx-background-color:#fff;" + "\r\n-fx-border-color: #D3D3D3;\r\n" + "-fx-border-width: 0;");

				HBox hboxMainSubFE_9 = new HBox(insetLabelFE_9, rectFE_9, vboxSubSubFE_9);
				hboxMainSubFE_9.setStyle(
						"-fx-background-color:#fff;" + "\r\n-fx-border-color: #D3D3D3;\r\n" + "-fx-border-width: 0;");
				
				// end Loop 11
				
				// begin Loop 12
				
				Label lblDOWFE_10 = new Label("Thứ Hai"), lblDayFE_10 = new Label("01/01/2021"),
						lblMiddle_10 = new Label("");

				lblDOWFE_10.setStyle("-fx-font-size: 16pt;\r\n" + " -fx-font-family: 'Candara';" + "-fx-text-fill: #000;"
						+ "\r\n-fx-background-color: #E2DFA2;");
				lblDOWFE_10.setAlignment(Pos.CENTER_LEFT);
				lblDOWFE_10.setPadding(new Insets(0, 0, 0, 10));
				lblMiddle_10.setMinWidth(200);
				lblMiddle_10.setStyle("-fx-font-size: 16pt;\r\n" + " -fx-font-family: 'Candara';"
						+ "-fx-text-fill: #000;" + "\r\n-fx-background-color: #E2DFA2;");
				lblDayFE_10.setStyle("-fx-font-size: 14pt;\r\n" + " -fx-font-family: 'Ubuntu';"
						+ "-fx-text-fill: #867666;" + "\r\n-fx-background-color: #E2DFA2;");
				lblDayFE_10.setPadding(new Insets(2.5, 0, 0, lblDOWFE_10.getBoundsInParent().getWidth()));

				HBox hboxLabelFirstFE_10 = new HBox(lblDOWFE_10, lblDayFE_10);
				hboxLabelFirstFE_10.setStyle("\r\n-fx-background-color: #E2DFA2;");

				int insetLabelFE_10 = 10;

				// draw line
				Line endLineBorderFE_10 = new Line(0, 80, 3000, 80);
				endLineBorderFE_10.setStroke(Color.rgb(161, 160, 160));

				Rectangle rectFE_10 = new Rectangle(12, 110);
				rectFE_10.setFill(Color.rgb(0, 169, 254));
				Label lblCourseNameFE_10 = new Label("Giao duc the chat dai cuong");

				Label lblShiftFE_10 = new Label("7h00 - 11h30"), lblTimeFE_10 = new Label("Thời gian: "),
						lblPlaceFE_10 = new Label("SAN04"), lblShiftNumFE_10 = new Label("1 - 2, "),
						lblPeriodFE_10 = new Label("Tiết "), lblRoomFE_10 = new Label("Phòng: "),
						lblThiMonFE_10 = new Label("Thi môn: "), lblToThiFE_10 = new Label("Tổ thi: "),
						lblGhepThiFE_10 = new Label("Ghép thi: "), lblSoLuongFE_10 = new Label("Số lượng: "),
						lblToThiFE_Rep_10 = new Label("002, "), lblGhepThiFE_Rep_10 = new Label("39, "),
						lblSoLuongFE_Rep_10 = new Label("57");

				// hbox 1
				Image timeIconFE_10 = new Image(getClass().getResourceAsStream("images/ic_clock_24_0.png"));
				ImageView timeImViewFE_10 = new ImageView(timeIconFE_10);
				timeImViewFE_10.setFitWidth(16);
				timeImViewFE_10.setFitHeight(16);

				HBox hboxImViewFE_10 = new HBox(timeImViewFE_10);
				hboxImViewFE_10.setPadding(new Insets(2.75, 0, 0, 0));

				lblShiftFE_10.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #C51802");
//				lblShift.setPadding(new Insets(0, 0, 0, 20));
				lblTimeFE_10.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #C51802");
				lblTimeFE_10.setPadding(new Insets(0, 0, 0, 10));
				HBox hboxFirstFE_10 = new HBox(hboxImViewFE_10, lblTimeFE_10, lblShiftFE_10);
				hboxFirstFE_10.setPrefHeight(10);
				hboxFirstFE_10.setMaxHeight(18);
				hboxFirstFE_10.setPadding(new Insets(5.25, 0, 3, 0));

				// hbox 2
				lblCourseNameFE_10.setStyle("-fx-font-size: 18px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Bitter';" + "-fx-text-fill: #000");
//				lblCourseName.setPadding(new Insets(insetLabel, insetLabel, 0, 0));
				lblThiMonFE_10.setStyle("-fx-font-size: 18px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Bitter';" + "-fx-text-fill: #000");
//				lblShiftNum.setPadding(new Insets(insetLabel + 20, insetLabel + 5, insetLabel + 10, insetLabel + 5));
				HBox hboxSecondFE_10 = new HBox(lblThiMonFE_10, lblCourseNameFE_10);
				hboxSecondFE_10.setPadding(new Insets(0, 0, 3, 0));

				// hbox 3
				lblPeriodFE_10.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblShiftNumFE_10.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblToThiFE_10.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblToThiFE_Rep_10.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblGhepThiFE_10.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblGhepThiFE_Rep_10.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblSoLuongFE_10.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblSoLuongFE_Rep_10.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				HBox hboxThirdFE_10 = new HBox(lblPeriodFE_10, lblShiftNumFE_10, lblToThiFE_10, lblToThiFE_Rep_10,
						lblGhepThiFE_10, lblGhepThiFE_Rep_10, lblSoLuongFE_10, lblSoLuongFE_Rep_10);
				hboxThirdFE_10.setPadding(new Insets(0, 0, 3, 0));

				// hbox 4
				lblRoomFE_10.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #00667C");
//				lblPlace.setPadding(new Insets(0, insetLabel, insetLabel, 0));
				lblPlaceFE_10.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #00667C");
//				lblPlace.setPadding(new Insets(0, insetLabel, insetLabel, 0));
				HBox hboxFourthFE_10 = new HBox(lblRoomFE_10, lblPlaceFE_10);
				hboxFourthFE_10.setPadding(new Insets(0, 0, 1, 0));

				VBox vboxSubFE_10 = new VBox(hboxSecondFE_10, hboxThirdFE_10, hboxFourthFE_10);
				vboxSubFE_10.setStyle("-fx-background-color:#fff");
				vboxSubFE_10.setPadding(new Insets(0, 0, 0, 28));
				vboxSubFE_10.setStyle(
						"-fx-background-color:#fff;" + "\r\n-fx-border-color: #D3D3D3;\r\n" + "-fx-border-width: 0;");

				VBox vboxSubSubFE_10 = new VBox(hboxFirstFE_10, vboxSubFE_10);
				vboxSubSubFE_10.setStyle("-fx-background-color:#fff");
//				vboxSubSub.setPadding(new Insets(insetLabel+10, insetLabel, insetLabel+10, 50));
				vboxSubSubFE_10.setStyle(
						"-fx-background-color:#fff;" + "\r\n-fx-border-color: #D3D3D3;\r\n" + "-fx-border-width: 0;");

				HBox hboxMainSubFE_10 = new HBox(insetLabelFE_10, rectFE_10, vboxSubSubFE_10);
				hboxMainSubFE_10.setStyle(
						"-fx-background-color:#fff;" + "\r\n-fx-border-color: #D3D3D3;\r\n" + "-fx-border-width: 0;");
				
				// end Loop 12
				
				// begin Loop 13
				
				Label lblDOWFE_11 = new Label("Thứ Hai"), lblDayFE_11 = new Label("01/01/2021"),
						lblMiddle_11 = new Label("");

				lblDOWFE_11.setStyle("-fx-font-size: 16pt;\r\n" + " -fx-font-family: 'Candara';" + "-fx-text-fill: #000;"
						+ "\r\n-fx-background-color: #E2DFA2;");
				lblDOWFE_11.setAlignment(Pos.CENTER_LEFT);
				lblDOWFE_11.setPadding(new Insets(0, 0, 0, 10));
				lblMiddle_11.setMinWidth(180);
				lblMiddle_11.setStyle("-fx-font-size: 16pt;\r\n" + " -fx-font-family: 'Candara';"
						+ "-fx-text-fill: #000;" + "\r\n-fx-background-color: #E2DFA2;");
				lblDayFE_11.setStyle("-fx-font-size: 14pt;\r\n" + " -fx-font-family: 'Ubuntu';"
						+ "-fx-text-fill: #867666;" + "\r\n-fx-background-color: #E2DFA2;");
				lblDayFE_11.setPadding(new Insets(2.5, 0, 0, lblDOWFE_11.getBoundsInParent().getWidth()));

				HBox hboxLabelFirstFE_11 = new HBox(lblDOWFE_11, lblDayFE_11);
				hboxLabelFirstFE_11.setStyle("\r\n-fx-background-color: #E2DFA2;");

				int insetLabelFE_11 = 10;

				// draw line
				Line endLineBorderFE_11 = new Line(0, 80, 3000, 80);
				endLineBorderFE_11.setStroke(Color.rgb(161, 160, 160));

				Rectangle rectFE_11 = new Rectangle(12, 110);
				rectFE_11.setFill(Color.rgb(0, 169, 254));
				Label lblCourseNameFE_11 = new Label("Giao duc the chat dai cuong");

				Label lblShiftFE_11 = new Label("7h00 - 11h30"), lblTimeFE_11 = new Label("Thời gian: "),
						lblPlaceFE_11 = new Label("SAN04"), lblShiftNumFE_11 = new Label("1 - 2, "),
						lblPeriodFE_11 = new Label("Tiết "), lblRoomFE_11 = new Label("Phòng: "),
						lblThiMonFE_11 = new Label("Thi môn: "), lblToThiFE_11 = new Label("Tổ thi: "),
						lblGhepThiFE_11 = new Label("Ghép thi: "), lblSoLuongFE_11 = new Label("Số lượng: "),
						lblToThiFE_Rep_11 = new Label("002, "), lblGhepThiFE_Rep_11 = new Label("39, "),
						lblSoLuongFE_Rep_11 = new Label("57");

				// hbox 1
				Image timeIconFE_11 = new Image(getClass().getResourceAsStream("images/ic_clock_24_0.png"));
				ImageView timeImViewFE_11 = new ImageView(timeIconFE_11);
				timeImViewFE_11.setFitWidth(16);
				timeImViewFE_11.setFitHeight(16);

				HBox hboxImViewFE_11 = new HBox(timeImViewFE_11);
				hboxImViewFE_11.setPadding(new Insets(2.75, 0, 0, 0));

				lblShiftFE_11.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #C51802");
//				lblShift.setPadding(new Insets(0, 0, 0, 20));
				lblTimeFE_11.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #C51802");
				lblTimeFE_11.setPadding(new Insets(0, 0, 0, 10));
				HBox hboxFirstFE_11 = new HBox(hboxImViewFE_11, lblTimeFE_11, lblShiftFE_11);
				hboxFirstFE_11.setPrefHeight(10);
				hboxFirstFE_11.setMaxHeight(18);
				hboxFirstFE_11.setPadding(new Insets(5.25, 0, 3, 0));

				// hbox 2
				lblCourseNameFE_11.setStyle("-fx-font-size: 18px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Bitter';" + "-fx-text-fill: #000");
//				lblCourseName.setPadding(new Insets(insetLabel, insetLabel, 0, 0));
				lblThiMonFE_11.setStyle("-fx-font-size: 18px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Bitter';" + "-fx-text-fill: #000");
//				lblShiftNum.setPadding(new Insets(insetLabel + 20, insetLabel + 5, insetLabel + 10, insetLabel + 5));
				HBox hboxSecondFE_11 = new HBox(lblThiMonFE_11, lblCourseNameFE_11);
				hboxSecondFE_11.setPadding(new Insets(0, 0, 3, 0));

				// hbox 3
				lblPeriodFE_11.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblShiftNumFE_11.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblToThiFE_11.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblToThiFE_Rep_11.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblGhepThiFE_11.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblGhepThiFE_Rep_11.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblSoLuongFE_11.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblSoLuongFE_Rep_11.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				HBox hboxThirdFE_11 = new HBox(lblPeriodFE_11, lblShiftNumFE_11, lblToThiFE_11, lblToThiFE_Rep_11,
						lblGhepThiFE_11, lblGhepThiFE_Rep_11, lblSoLuongFE_11, lblSoLuongFE_Rep_11);
				hboxThirdFE_11.setPadding(new Insets(0, 0, 3, 0));

				// hbox 4
				lblRoomFE_11.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #00667C");
//				lblPlace.setPadding(new Insets(0, insetLabel, insetLabel, 0));
				lblPlaceFE_11.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #00667C");
//				lblPlace.setPadding(new Insets(0, insetLabel, insetLabel, 0));
				HBox hboxFourthFE_11 = new HBox(lblRoomFE_11, lblPlaceFE_11);
				hboxFourthFE_11.setPadding(new Insets(0, 0, 1, 0));

				VBox vboxSubFE_11 = new VBox(hboxSecondFE_11, hboxThirdFE_11, hboxFourthFE_11);
				vboxSubFE_11.setStyle("-fx-background-color:#fff");
				vboxSubFE_11.setPadding(new Insets(0, 0, 0, 28));
				vboxSubFE_11.setStyle(
						"-fx-background-color:#fff;" + "\r\n-fx-border-color: #D3D3D3;\r\n" + "-fx-border-width: 0;");

				VBox vboxSubSubFE_11 = new VBox(hboxFirstFE_11, vboxSubFE_11);
				vboxSubSubFE_11.setStyle("-fx-background-color:#fff");
//				vboxSubSub.setPadding(new Insets(insetLabel+10, insetLabel, insetLabel+10, 50));
				vboxSubSubFE_11.setStyle(
						"-fx-background-color:#fff;" + "\r\n-fx-border-color: #D3D3D3;\r\n" + "-fx-border-width: 0;");

				HBox hboxMainSubFE_11 = new HBox(insetLabelFE_11, rectFE_11, vboxSubSubFE_11);
				hboxMainSubFE_11.setStyle(
						"-fx-background-color:#fff;" + "\r\n-fx-border-color: #D3D3D3;\r\n" + "-fx-border-width: 0;");
				
				// end Loop 13
				
				// begin Loop 14
				
				Label lblDOWFE_12 = new Label("Thứ Hai"), lblDayFE_12 = new Label("01/01/2021"),
						lblMiddle_12 = new Label("");

				lblDOWFE_12.setStyle("-fx-font-size: 16pt;\r\n" + " -fx-font-family: 'Candara';" + "-fx-text-fill: #000;"
						+ "\r\n-fx-background-color: #E2DFA2;");
				lblDOWFE_12.setAlignment(Pos.CENTER_LEFT);
				lblDOWFE_12.setPadding(new Insets(0, 0, 0, 10));
				lblMiddle_12.setMinWidth(180);
				lblMiddle_12.setStyle("-fx-font-size: 16pt;\r\n" + " -fx-font-family: 'Candara';"
						+ "-fx-text-fill: #000;" + "\r\n-fx-background-color: #E2DFA2;");
				lblDayFE_12.setStyle("-fx-font-size: 14pt;\r\n" + " -fx-font-family: 'Ubuntu';"
						+ "-fx-text-fill: #867666;" + "\r\n-fx-background-color: #E2DFA2;");
				lblDayFE_12.setPadding(new Insets(2.5, 0, 0, lblDOWFE_12.getBoundsInParent().getWidth()));

				HBox hboxLabelFirstFE_12 = new HBox(lblDOWFE_12, lblDayFE_12);
				hboxLabelFirstFE_12.setStyle("\r\n-fx-background-color: #E2DFA2;");

				int insetLabelFE_12 = 10;

				// draw line
				Line endLineBorderFE_12 = new Line(0, 80, 3000, 80);
				endLineBorderFE_12.setStroke(Color.rgb(161, 160, 160));

				Rectangle rectFE_12 = new Rectangle(12, 110);
				rectFE_12.setFill(Color.rgb(0, 169, 254));
				Label lblCourseNameFE_12 = new Label("Giao duc the chat dai cuong");

				Label lblShiftFE_12 = new Label("7h00 - 11h30"), lblTimeFE_12 = new Label("Thời gian: "),
						lblPlaceFE_12 = new Label("SAN04"), lblShiftNumFE_12 = new Label("1 - 2, "),
						lblPeriodFE_12 = new Label("Tiết "), lblRoomFE_12 = new Label("Phòng: "),
						lblThiMonFE_12 = new Label("Thi môn: "), lblToThiFE_12 = new Label("Tổ thi: "),
						lblGhepThiFE_12 = new Label("Ghép thi: "), lblSoLuongFE_12 = new Label("Số lượng: "),
						lblToThiFE_Rep_12 = new Label("002, "), lblGhepThiFE_Rep_12 = new Label("39, "),
						lblSoLuongFE_Rep_12 = new Label("57");

				// hbox 1
				Image timeIconFE_12 = new Image(getClass().getResourceAsStream("images/ic_clock_24_0.png"));
				ImageView timeImViewFE_12 = new ImageView(timeIconFE_12);
				timeImViewFE_12.setFitWidth(16);
				timeImViewFE_12.setFitHeight(16);

				HBox hboxImViewFE_12 = new HBox(timeImViewFE_12);
				hboxImViewFE_12.setPadding(new Insets(2.75, 0, 0, 0));

				lblShiftFE_12.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #C51802");
//				lblShift.setPadding(new Insets(0, 0, 0, 20));
				lblTimeFE_12.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #C51802");
				lblTimeFE_12.setPadding(new Insets(0, 0, 0, 10));
				HBox hboxFirstFE_12 = new HBox(hboxImViewFE_12, lblTimeFE_12, lblShiftFE_12);
				hboxFirstFE_12.setPrefHeight(10);
				hboxFirstFE_12.setMaxHeight(18);
				hboxFirstFE_12.setPadding(new Insets(5.25, 0, 3, 0));

				// hbox 2
				lblCourseNameFE_12.setStyle("-fx-font-size: 18px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Bitter';" + "-fx-text-fill: #000");
//				lblCourseName.setPadding(new Insets(insetLabel, insetLabel, 0, 0));
				lblThiMonFE_12.setStyle("-fx-font-size: 18px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Bitter';" + "-fx-text-fill: #000");
//				lblShiftNum.setPadding(new Insets(insetLabel + 20, insetLabel + 5, insetLabel + 10, insetLabel + 5));
				HBox hboxSecondFE_12 = new HBox(lblThiMonFE_12, lblCourseNameFE_12);
				hboxSecondFE_12.setPadding(new Insets(0, 0, 3, 0));

				// hbox 3
				lblPeriodFE_12.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblShiftNumFE_12.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblToThiFE_12.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblToThiFE_Rep_12.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblGhepThiFE_12.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblGhepThiFE_Rep_12.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblSoLuongFE_12.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblSoLuongFE_Rep_12.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				HBox hboxThirdFE_12 = new HBox(lblPeriodFE_12, lblShiftNumFE_12, lblToThiFE_12, lblToThiFE_Rep_12,
						lblGhepThiFE_12, lblGhepThiFE_Rep_12, lblSoLuongFE_12, lblSoLuongFE_Rep_12);
				hboxThirdFE_12.setPadding(new Insets(0, 0, 3, 0));

				// hbox 4
				lblRoomFE_12.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #00667C");
//				lblPlace.setPadding(new Insets(0, insetLabel, insetLabel, 0));
				lblPlaceFE_12.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #00667C");
//				lblPlace.setPadding(new Insets(0, insetLabel, insetLabel, 0));
				HBox hboxFourthFE_12 = new HBox(lblRoomFE_12, lblPlaceFE_12);
				hboxFourthFE_12.setPadding(new Insets(0, 0, 1, 0));

				VBox vboxSubFE_12 = new VBox(hboxSecondFE_12, hboxThirdFE_12, hboxFourthFE_12);
				vboxSubFE_12.setStyle("-fx-background-color:#fff");
				vboxSubFE_12.setPadding(new Insets(0, 0, 0, 28));
				vboxSubFE_12.setStyle(
						"-fx-background-color:#fff;" + "\r\n-fx-border-color: #D3D3D3;\r\n" + "-fx-border-width: 0;");

				VBox vboxSubSubFE_12 = new VBox(hboxFirstFE_12, vboxSubFE_12);
				vboxSubSubFE_12.setStyle("-fx-background-color:#fff");
//				vboxSubSub.setPadding(new Insets(insetLabel+10, insetLabel, insetLabel+10, 50));
				vboxSubSubFE_12.setStyle(
						"-fx-background-color:#fff;" + "\r\n-fx-border-color: #D3D3D3;\r\n" + "-fx-border-width: 0;");

				HBox hboxMainSubFE_12 = new HBox(insetLabelFE_12, rectFE_12, vboxSubSubFE_12);
				hboxMainSubFE_12.setStyle(
						"-fx-background-color:#fff;" + "\r\n-fx-border-color: #D3D3D3;\r\n" + "-fx-border-width: 0;");
				
				// end Loop 14
				
				// begin Loop 15
				
				Label lblDOWFE_13 = new Label("Thứ Hai"), lblDayFE_13 = new Label("01/01/2021"),
						lblMiddle_13 = new Label("");

				lblDOWFE_13.setStyle("-fx-font-size: 16pt;\r\n" + " -fx-font-family: 'Candara';" + "-fx-text-fill: #000;"
						+ "\r\n-fx-background-color: #E2DFA2;");
				lblDOWFE_13.setAlignment(Pos.CENTER_LEFT);
				lblDOWFE_13.setPadding(new Insets(0, 0, 0, 10));
				lblMiddle_13.setStyle("-fx-font-size: 16pt;\r\n" + " -fx-font-family: 'Candara';"
						+ "-fx-text-fill: #000;" + "\r\n-fx-background-color: #E2DFA2;");
				lblDayFE_13.setStyle("-fx-font-size: 14pt;\r\n" + " -fx-font-family: 'Ubuntu';"
						+ "-fx-text-fill: #867666;" + "\r\n-fx-background-color: #E2DFA2;");
				lblDayFE_13.setPadding(new Insets(2.5, 0, 0, lblDOWFE_13.getBoundsInParent().getWidth()));

				HBox hboxLabelFirstFE_13 = new HBox(lblDOWFE_13, lblDayFE_13);
				hboxLabelFirstFE_13.setStyle("\r\n-fx-background-color: #E2DFA2;");

				int insetLabelFE_13 = 10;

				// draw line
				Line endLineBorderFE_13 = new Line(0, 80, 3000, 80);
				endLineBorderFE_13.setStroke(Color.rgb(161, 160, 160));

				Rectangle rectFE_13 = new Rectangle(12, 110);
				rectFE_13.setFill(Color.rgb(0, 169, 254));
				Label lblCourseNameFE_13 = new Label("Giao duc the chat dai cuong");

				Label lblShiftFE_13 = new Label("7h00 - 11h30"), lblTimeFE_13 = new Label("Thời gian: "),
						lblPlaceFE_13 = new Label("SAN04"), lblShiftNumFE_13 = new Label("1 - 2, "),
						lblPeriodFE_13 = new Label("Tiết "), lblRoomFE_13 = new Label("Phòng: "),
						lblThiMonFE_13 = new Label("Thi môn: "), lblToThiFE_13 = new Label("Tổ thi: "),
						lblGhepThiFE_13 = new Label("Ghép thi: "), lblSoLuongFE_13 = new Label("Số lượng: "),
						lblToThiFE_Rep_13 = new Label("002, "), lblGhepThiFE_Rep_13 = new Label("39, "),
						lblSoLuongFE_Rep_13 = new Label("57");

				// hbox 1
				Image timeIconFE_13 = new Image(getClass().getResourceAsStream("images/ic_clock_24_0.png"));
				ImageView timeImViewFE_13 = new ImageView(timeIconFE_13);
				timeImViewFE_13.setFitWidth(16);
				timeImViewFE_13.setFitHeight(16);

				HBox hboxImViewFE_13 = new HBox(timeImViewFE_13);
				hboxImViewFE_13.setPadding(new Insets(2.75, 0, 0, 0));

				lblShiftFE_13.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #C51802");
//				lblShift.setPadding(new Insets(0, 0, 0, 20));
				lblTimeFE_13.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #C51802");
				lblTimeFE_13.setPadding(new Insets(0, 0, 0, 10));
				HBox hboxFirstFE_13 = new HBox(hboxImViewFE_13, lblTimeFE_13, lblShiftFE_13);
				hboxFirstFE_13.setPrefHeight(10);
				hboxFirstFE_13.setMaxHeight(18);
				hboxFirstFE_13.setPadding(new Insets(5.25, 0, 3, 0));

				// hbox 2
				lblCourseNameFE_13.setStyle("-fx-font-size: 18px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Bitter';" + "-fx-text-fill: #000");
//				lblCourseName.setPadding(new Insets(insetLabel, insetLabel, 0, 0));
				lblThiMonFE_13.setStyle("-fx-font-size: 18px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Bitter';" + "-fx-text-fill: #000");
//				lblShiftNum.setPadding(new Insets(insetLabel + 20, insetLabel + 5, insetLabel + 10, insetLabel + 5));
				HBox hboxSecondFE_13 = new HBox(lblThiMonFE_13, lblCourseNameFE_13);
				hboxSecondFE_13.setPadding(new Insets(0, 0, 3, 0));

				// hbox 3
				lblPeriodFE_13.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblShiftNumFE_13.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblToThiFE_13.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblToThiFE_Rep_13.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblGhepThiFE_13.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblGhepThiFE_Rep_13.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblSoLuongFE_13.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				lblSoLuongFE_Rep_13.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #867666");
				HBox hboxThirdFE_13 = new HBox(lblPeriodFE_13, lblShiftNumFE_13, lblToThiFE_13, lblToThiFE_Rep_13,
						lblGhepThiFE_13, lblGhepThiFE_Rep_13, lblSoLuongFE_13, lblSoLuongFE_Rep_13);
				hboxThirdFE_13.setPadding(new Insets(0, 0, 3, 0));

				// hbox 4
				lblRoomFE_13.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #00667C");
//				lblPlace.setPadding(new Insets(0, insetLabel, insetLabel, 0));
				lblPlaceFE_13.setStyle("-fx-font-size: 16px;\r\n" + "-fx-text-alignment:center;" + "\r\n"
						+ "  -fx-font-family: 'Grandstander';" + "-fx-text-fill: #00667C");
//				lblPlace.setPadding(new Insets(0, insetLabel, insetLabel, 0));
				HBox hboxFourthFE_13 = new HBox(lblRoomFE_13, lblPlaceFE_13);
				hboxFourthFE_13.setPadding(new Insets(0, 0, 1, 0));

				VBox vboxSubFE_13 = new VBox(hboxSecondFE_13, hboxThirdFE_13, hboxFourthFE_13);
				vboxSubFE_13.setStyle("-fx-background-color:#fff");
				vboxSubFE_13.setPadding(new Insets(0, 0, 0, 28));
				vboxSubFE_13.setStyle(
						"-fx-background-color:#fff;" + "\r\n-fx-border-color: #D3D3D3;\r\n" + "-fx-border-width: 0;");

				VBox vboxSubSubFE_13 = new VBox(hboxFirstFE_13, vboxSubFE_13);
				vboxSubSubFE_13.setStyle("-fx-background-color:#fff");
//				vboxSubSub.setPadding(new Insets(insetLabel+10, insetLabel, insetLabel+10, 50));
				vboxSubSubFE_13.setStyle(
						"-fx-background-color:#fff;" + "\r\n-fx-border-color: #D3D3D3;\r\n" + "-fx-border-width: 0;");

				HBox hboxMainSubFE_13 = new HBox(insetLabelFE_13, rectFE_13, vboxSubSubFE_13);
				hboxMainSubFE_13.setStyle(
						"-fx-background-color:#fff;" + "\r\n-fx-border-color: #D3D3D3;\r\n" + "-fx-border-width: 0;");
				
				// end Loop 15
				
				// ============================================ UPDATE
				ArrayList<MonThi> listMonThi = menu.getListMonThi();

				if (listMonThi.size() > 0) {
					lblDOWFE.setText(listMonThi.get(0).getNgayThu());
					lblDayFE.setText(" - "+listMonThi.get(0).getNgayThi());
					lblShiftFE.setText(listMonThi.get(0).getThoiGian());
					lblCourseNameFE.setText(listMonThi.get(0).getTenMonHoc());
					lblShiftNumFE.setText(listMonThi.get(0).getTietThi()+", ");
					lblToThiFE_Rep.setText(listMonThi.get(0).getToThi()+", ");
					lblGhepThiFE_Rep.setText(listMonThi.get(0).getGhepThi()+", ");
					lblSoLuongFE_Rep.setText(listMonThi.get(0).getSoLuong());
					lblPlaceFE.setText(listMonThi.get(0).getPhongThi());
					vBoxMainTabFE = new VBox(vboxInputCodeES, hboxButtonToggle, hboxLabelFirstFE, hboxMainSubFE, endLineBorderFE);
				}
				
				if(listMonThi.size() == 2) {
					lblDOWFE.setText(listMonThi.get(0).getNgayThu());
					lblDayFE.setText(" - "+listMonThi.get(0).getNgayThi());
					lblShiftFE.setText(listMonThi.get(0).getThoiGian());
					lblCourseNameFE.setText(listMonThi.get(0).getTenMonHoc());
					lblShiftNumFE.setText(listMonThi.get(0).getTietThi()+", ");
					lblToThiFE_Rep.setText(listMonThi.get(0).getToThi()+", ");
					lblGhepThiFE_Rep.setText(listMonThi.get(0).getGhepThi()+", ");
					lblSoLuongFE_Rep.setText(listMonThi.get(0).getSoLuong());
					lblPlaceFE.setText(listMonThi.get(0).getPhongThi());
					
					lblDOWFE_0.setText(listMonThi.get(1).getNgayThu());
					lblDayFE_0.setText(" - "+listMonThi.get(1).getNgayThi());
					lblShiftFE_0.setText(listMonThi.get(1).getThoiGian());
					lblCourseNameFE_0.setText(listMonThi.get(1).getTenMonHoc());
					lblShiftNumFE_0.setText(listMonThi.get(1).getTietThi()+", ");
					lblToThiFE_Rep_0.setText(listMonThi.get(1).getToThi()+", ");
					lblGhepThiFE_Rep_0.setText(listMonThi.get(1).getGhepThi()+", ");
					lblSoLuongFE_Rep_0.setText(listMonThi.get(1).getSoLuong());
					lblPlaceFE_0.setText(listMonThi.get(1).getPhongThi());
					
					vBoxMainTabFE = new VBox(vboxInputCodeES, hboxButtonToggle, hboxLabelFirstFE, hboxMainSubFE, endLineBorderFE,
							hboxLabelFirstFE_0, hboxMainSubFE_0, endLineBorderFE_0);
				
				} else if(listMonThi.size() == 3) {
					lblDOWFE.setText(listMonThi.get(0).getNgayThu());
					lblDayFE.setText(" - "+listMonThi.get(0).getNgayThi());
					lblShiftFE.setText(listMonThi.get(0).getThoiGian());
					lblCourseNameFE.setText(listMonThi.get(0).getTenMonHoc());
					lblShiftNumFE.setText(listMonThi.get(0).getTietThi()+", ");
					lblToThiFE_Rep.setText(listMonThi.get(0).getToThi()+", ");
					lblGhepThiFE_Rep.setText(listMonThi.get(0).getGhepThi()+", ");
					lblSoLuongFE_Rep.setText(listMonThi.get(0).getSoLuong());
					lblPlaceFE.setText(listMonThi.get(0).getPhongThi());
					
					lblDOWFE_0.setText(listMonThi.get(1).getNgayThu());
					lblDayFE_0.setText(" - "+listMonThi.get(1).getNgayThi());
					lblShiftFE_0.setText(listMonThi.get(1).getThoiGian());
					lblCourseNameFE_0.setText(listMonThi.get(1).getTenMonHoc());
					lblShiftNumFE_0.setText(listMonThi.get(1).getTietThi());
					lblToThiFE_Rep_0.setText(listMonThi.get(1).getToThi()+", ");
					lblGhepThiFE_Rep_0.setText(listMonThi.get(1).getGhepThi()+", ");
					lblSoLuongFE_Rep_0.setText(listMonThi.get(1).getSoLuong()+", ");
					lblPlaceFE_0.setText(listMonThi.get(1).getPhongThi());
					
					lblDOWFE_1.setText(listMonThi.get(2).getNgayThu());
					lblDayFE_1.setText(" - "+listMonThi.get(2).getNgayThi());
					lblShiftFE_1.setText(listMonThi.get(2).getThoiGian());
					lblCourseNameFE_1.setText(listMonThi.get(2).getTenMonHoc());
					lblShiftNumFE_1.setText(listMonThi.get(2).getTietThi()+", ");
					lblToThiFE_Rep_1.setText(listMonThi.get(2).getToThi()+", ");
					lblGhepThiFE_Rep_1.setText(listMonThi.get(2).getGhepThi()+", ");
					lblSoLuongFE_Rep_1.setText(listMonThi.get(2).getSoLuong());
					lblPlaceFE_1.setText(listMonThi.get(2).getPhongThi());
					
					vBoxMainTabFE = new VBox(vboxInputCodeES, hboxButtonToggle, hboxLabelFirstFE, hboxMainSubFE, endLineBorderFE,
							hboxLabelFirstFE_0, hboxMainSubFE_0, endLineBorderFE_0, hboxLabelFirstFE_1, hboxMainSubFE_1,
							endLineBorderFE_1);
				
				} else if(listMonThi.size() == 4) {
					lblDOWFE.setText(listMonThi.get(0).getNgayThu());
					lblDayFE.setText(" - "+listMonThi.get(0).getNgayThi());
					lblShiftFE.setText(listMonThi.get(0).getThoiGian());
					lblCourseNameFE.setText(listMonThi.get(0).getTenMonHoc());
					lblShiftNumFE.setText(listMonThi.get(0).getTietThi()+", ");
					lblToThiFE_Rep.setText(listMonThi.get(0).getToThi()+", ");
					lblGhepThiFE_Rep.setText(listMonThi.get(0).getGhepThi()+", ");
					lblSoLuongFE_Rep.setText(listMonThi.get(0).getSoLuong());
					lblPlaceFE.setText(listMonThi.get(0).getPhongThi());
					
					lblDOWFE_0.setText(listMonThi.get(1).getNgayThu());
					lblDayFE_0.setText(" - "+listMonThi.get(1).getNgayThi());
					lblShiftFE_0.setText(listMonThi.get(1).getThoiGian());
					lblCourseNameFE_0.setText(listMonThi.get(1).getTenMonHoc());
					lblShiftNumFE_0.setText(listMonThi.get(1).getTietThi()+", ");
					lblToThiFE_Rep_0.setText(listMonThi.get(1).getToThi()+", ");
					lblGhepThiFE_Rep_0.setText(listMonThi.get(1).getGhepThi()+", ");
					lblSoLuongFE_Rep_0.setText(listMonThi.get(1).getSoLuong());
					lblPlaceFE_0.setText(listMonThi.get(1).getPhongThi());
					
					lblDOWFE_1.setText(listMonThi.get(2).getNgayThu());
					lblDayFE_1.setText(" - "+listMonThi.get(2).getNgayThi());
					lblShiftFE_1.setText(listMonThi.get(2).getThoiGian());
					lblCourseNameFE_1.setText(listMonThi.get(2).getTenMonHoc());
					lblShiftNumFE_1.setText(listMonThi.get(2).getTietThi());
					lblToThiFE_Rep_1.setText(listMonThi.get(2).getToThi());
					lblGhepThiFE_Rep_1.setText(listMonThi.get(2).getGhepThi());
					lblSoLuongFE_Rep_1.setText(listMonThi.get(2).getSoLuong());
					lblPlaceFE_1.setText(listMonThi.get(2).getPhongThi());
					
					lblDOWFE_2.setText(listMonThi.get(3).getNgayThu());
					lblDayFE_2.setText(" - "+listMonThi.get(3).getNgayThi());
					lblShiftFE_2.setText(listMonThi.get(3).getThoiGian());
					lblCourseNameFE_2.setText(listMonThi.get(3).getTenMonHoc());
					lblShiftNumFE_2.setText(listMonThi.get(3).getTietThi());
					lblToThiFE_Rep_2.setText(listMonThi.get(3).getToThi());
					lblGhepThiFE_Rep_2.setText(listMonThi.get(3).getGhepThi());
					lblSoLuongFE_Rep_2.setText(listMonThi.get(3).getSoLuong());
					lblPlaceFE_2.setText(listMonThi.get(3).getPhongThi());
					
					vBoxMainTabFE = new VBox(vboxInputCodeES, hboxButtonToggle, hboxLabelFirstFE, hboxMainSubFE, endLineBorderFE,
							hboxLabelFirstFE_0, hboxMainSubFE_0, endLineBorderFE_0, hboxLabelFirstFE_1, hboxMainSubFE_1,
							endLineBorderFE_1, hboxLabelFirstFE_2, hboxMainSubFE_2, endLineBorderFE_2);
				
				} else if(listMonThi.size() == 5) {
					lblDOWFE.setText(listMonThi.get(0).getNgayThu());
					lblDayFE.setText(" - "+listMonThi.get(0).getNgayThi());
					lblShiftFE.setText(listMonThi.get(0).getThoiGian());
					lblCourseNameFE.setText(listMonThi.get(0).getTenMonHoc());
					lblShiftNumFE.setText(listMonThi.get(0).getTietThi()+", ");
					lblToThiFE_Rep.setText(listMonThi.get(0).getToThi()+", ");
					lblGhepThiFE_Rep.setText(listMonThi.get(0).getGhepThi()+", ");
					lblSoLuongFE_Rep.setText(listMonThi.get(0).getSoLuong());
					lblPlaceFE.setText(listMonThi.get(0).getPhongThi());
					
					lblDOWFE_0.setText(listMonThi.get(1).getNgayThu());
					lblDayFE_0.setText(" - "+listMonThi.get(1).getNgayThi());
					lblShiftFE_0.setText(listMonThi.get(1).getThoiGian());
					lblCourseNameFE_0.setText(listMonThi.get(1).getTenMonHoc());
					lblShiftNumFE_0.setText(listMonThi.get(1).getTietThi()+", ");
					lblToThiFE_Rep_0.setText(listMonThi.get(1).getToThi()+", ");
					lblGhepThiFE_Rep_0.setText(listMonThi.get(1).getGhepThi()+", ");
					lblSoLuongFE_Rep_0.setText(listMonThi.get(1).getSoLuong());
					lblPlaceFE_0.setText(listMonThi.get(1).getPhongThi());
					
					lblDOWFE_1.setText(listMonThi.get(2).getNgayThu());
					lblDayFE_1.setText(" - "+listMonThi.get(2).getNgayThi());
					lblShiftFE_1.setText(listMonThi.get(2).getThoiGian());
					lblCourseNameFE_1.setText(listMonThi.get(2).getTenMonHoc());
					lblShiftNumFE_1.setText(listMonThi.get(2).getTietThi()+", ");
					lblToThiFE_Rep_1.setText(listMonThi.get(2).getToThi()+", ");
					lblGhepThiFE_Rep_1.setText(listMonThi.get(2).getGhepThi()+", ");
					lblSoLuongFE_Rep_1.setText(listMonThi.get(2).getSoLuong());
					lblPlaceFE_1.setText(listMonThi.get(2).getPhongThi());
					
					lblDOWFE_2.setText(listMonThi.get(3).getNgayThu());
					lblDayFE_2.setText(" - "+listMonThi.get(3).getNgayThi());
					lblShiftFE_2.setText(listMonThi.get(3).getThoiGian());
					lblCourseNameFE_2.setText(listMonThi.get(3).getTenMonHoc());
					lblShiftNumFE_2.setText(listMonThi.get(3).getTietThi()+", ");
					lblToThiFE_Rep_2.setText(listMonThi.get(3).getToThi()+", ");
					lblGhepThiFE_Rep_2.setText(listMonThi.get(3).getGhepThi()+", ");
					lblSoLuongFE_Rep_2.setText(listMonThi.get(3).getSoLuong());
					lblPlaceFE_2.setText(listMonThi.get(3).getPhongThi());
					
					lblDOWFE_3.setText(listMonThi.get(4).getNgayThu());
					lblDayFE_3.setText(" - "+listMonThi.get(4).getNgayThi());
					lblShiftFE_3.setText(listMonThi.get(4).getThoiGian());
					lblCourseNameFE_3.setText(listMonThi.get(4).getTenMonHoc());
					lblShiftNumFE_3.setText(listMonThi.get(4).getTietThi()+", ");
					lblToThiFE_Rep_3.setText(listMonThi.get(4).getToThi()+", ");
					lblGhepThiFE_Rep_3.setText(listMonThi.get(4).getGhepThi()+", ");
					lblSoLuongFE_Rep_3.setText(listMonThi.get(4).getSoLuong());
					lblPlaceFE_3.setText(listMonThi.get(4).getPhongThi());
					
					vBoxMainTabFE = new VBox(vboxInputCodeES, hboxButtonToggle, hboxLabelFirstFE, hboxMainSubFE, endLineBorderFE,
							hboxLabelFirstFE_0, hboxMainSubFE_0, endLineBorderFE_0, hboxLabelFirstFE_1, hboxMainSubFE_1,
							endLineBorderFE_1, hboxLabelFirstFE_2, hboxMainSubFE_2, endLineBorderFE_2
							, hboxLabelFirstFE_3, hboxMainSubFE_3, endLineBorderFE_3);
				
				} else if(listMonThi.size() == 6) {
					lblDOWFE.setText(listMonThi.get(0).getNgayThu());
					lblDayFE.setText(" - "+listMonThi.get(0).getNgayThi());
					lblShiftFE.setText(listMonThi.get(0).getThoiGian());
					lblCourseNameFE.setText(listMonThi.get(0).getTenMonHoc());
					lblShiftNumFE.setText(listMonThi.get(0).getTietThi()+", ");
					lblToThiFE_Rep.setText(listMonThi.get(0).getToThi()+", ");
					lblGhepThiFE_Rep.setText(listMonThi.get(0).getGhepThi()+", ");
					lblSoLuongFE_Rep.setText(listMonThi.get(0).getSoLuong());
					lblPlaceFE.setText(listMonThi.get(0).getPhongThi());
					
					lblDOWFE_0.setText(listMonThi.get(1).getNgayThu());
					lblDayFE_0.setText(" - "+listMonThi.get(1).getNgayThi());
					lblShiftFE_0.setText(listMonThi.get(1).getThoiGian());
					lblCourseNameFE_0.setText(listMonThi.get(1).getTenMonHoc());
					lblShiftNumFE_0.setText(listMonThi.get(1).getTietThi()+", ");
					lblToThiFE_Rep_0.setText(listMonThi.get(1).getToThi()+", ");
					lblGhepThiFE_Rep_0.setText(listMonThi.get(1).getGhepThi()+", ");
					lblSoLuongFE_Rep_0.setText(listMonThi.get(1).getSoLuong());
					lblPlaceFE_0.setText(listMonThi.get(1).getPhongThi());
					
					lblDOWFE_1.setText(listMonThi.get(2).getNgayThu());
					lblDayFE_1.setText(" - "+listMonThi.get(2).getNgayThi());
					lblShiftFE_1.setText(listMonThi.get(2).getThoiGian());
					lblCourseNameFE_1.setText(listMonThi.get(2).getTenMonHoc());
					lblShiftNumFE_1.setText(listMonThi.get(2).getTietThi()+", ");
					lblToThiFE_Rep_1.setText(listMonThi.get(2).getToThi()+", ");
					lblGhepThiFE_Rep_1.setText(listMonThi.get(2).getGhepThi()+", ");
					lblSoLuongFE_Rep_1.setText(listMonThi.get(2).getSoLuong());
					lblPlaceFE_1.setText(listMonThi.get(2).getPhongThi());
					
					lblDOWFE_2.setText(listMonThi.get(3).getNgayThu());
					lblDayFE_2.setText(" - "+listMonThi.get(3).getNgayThi());
					lblShiftFE_2.setText(listMonThi.get(3).getThoiGian());
					lblCourseNameFE_2.setText(listMonThi.get(3).getTenMonHoc());
					lblShiftNumFE_2.setText(listMonThi.get(3).getTietThi()+", ");
					lblToThiFE_Rep_2.setText(listMonThi.get(3).getToThi()+", ");
					lblGhepThiFE_Rep_2.setText(listMonThi.get(3).getGhepThi()+", ");
					lblSoLuongFE_Rep_2.setText(listMonThi.get(3).getSoLuong());
					lblPlaceFE_2.setText(listMonThi.get(3).getPhongThi());
					
					lblDOWFE_3.setText(listMonThi.get(4).getNgayThu());
					lblDayFE_3.setText(" - "+listMonThi.get(4).getNgayThi());
					lblShiftFE_3.setText(listMonThi.get(4).getThoiGian());
					lblCourseNameFE_3.setText(listMonThi.get(4).getTenMonHoc());
					lblShiftNumFE_3.setText(listMonThi.get(4).getTietThi()+", ");
					lblToThiFE_Rep_3.setText(listMonThi.get(4).getToThi()+", ");
					lblGhepThiFE_Rep_3.setText(listMonThi.get(4).getGhepThi()+", ");
					lblSoLuongFE_Rep_3.setText(listMonThi.get(4).getSoLuong());
					lblPlaceFE_3.setText(listMonThi.get(4).getPhongThi());
					
					lblDOWFE_4.setText(listMonThi.get(5).getNgayThu());
					lblDayFE_4.setText(" - "+listMonThi.get(5).getNgayThi());
					lblShiftFE_4.setText(listMonThi.get(5).getThoiGian());
					lblCourseNameFE_4.setText(listMonThi.get(5).getTenMonHoc());
					lblShiftNumFE_4.setText(listMonThi.get(5).getTietThi()+", ");
					lblToThiFE_Rep_4.setText(listMonThi.get(5).getToThi()+", ");
					lblGhepThiFE_Rep_4.setText(listMonThi.get(5).getGhepThi()+", ");
					lblSoLuongFE_Rep_4.setText(listMonThi.get(5).getSoLuong());
					lblPlaceFE_4.setText(listMonThi.get(5).getPhongThi());
					
					vBoxMainTabFE = new VBox(vboxInputCodeES, hboxButtonToggle, hboxLabelFirstFE, hboxMainSubFE, endLineBorderFE,
							hboxLabelFirstFE_0, hboxMainSubFE_0, endLineBorderFE_0, hboxLabelFirstFE_1, hboxMainSubFE_1,
							endLineBorderFE_1, hboxLabelFirstFE_2, hboxMainSubFE_2, endLineBorderFE_2
							, hboxLabelFirstFE_3, hboxMainSubFE_3, endLineBorderFE_3
							, hboxLabelFirstFE_4, hboxMainSubFE_4, endLineBorderFE_4);
				
				} else if(listMonThi.size() == 7) {
					lblDOWFE.setText(listMonThi.get(0).getNgayThu());
					lblDayFE.setText(" - "+listMonThi.get(0).getNgayThi());
					lblShiftFE.setText(listMonThi.get(0).getThoiGian());
					lblCourseNameFE.setText(listMonThi.get(0).getTenMonHoc());
					lblShiftNumFE.setText(listMonThi.get(0).getTietThi()+", ");
					lblToThiFE_Rep.setText(listMonThi.get(0).getToThi()+", ");
					lblGhepThiFE_Rep.setText(listMonThi.get(0).getGhepThi()+", ");
					lblSoLuongFE_Rep.setText(listMonThi.get(0).getSoLuong());
					lblPlaceFE.setText(listMonThi.get(0).getPhongThi());
					
					lblDOWFE_0.setText(listMonThi.get(1).getNgayThu());
					lblDayFE_0.setText(" - "+listMonThi.get(1).getNgayThi());
					lblShiftFE_0.setText(listMonThi.get(1).getThoiGian());
					lblCourseNameFE_0.setText(listMonThi.get(1).getTenMonHoc());
					lblShiftNumFE_0.setText(listMonThi.get(1).getTietThi()+", ");
					lblToThiFE_Rep_0.setText(listMonThi.get(1).getToThi()+", ");
					lblGhepThiFE_Rep_0.setText(listMonThi.get(1).getGhepThi()+", ");
					lblSoLuongFE_Rep_0.setText(listMonThi.get(1).getSoLuong());
					lblPlaceFE_0.setText(listMonThi.get(1).getPhongThi());
					
					lblDOWFE_1.setText(listMonThi.get(2).getNgayThu());
					lblDayFE_1.setText(" - "+listMonThi.get(2).getNgayThi());
					lblShiftFE_1.setText(listMonThi.get(2).getThoiGian());
					lblCourseNameFE_1.setText(listMonThi.get(2).getTenMonHoc());
					lblShiftNumFE_1.setText(listMonThi.get(2).getTietThi()+", ");
					lblToThiFE_Rep_1.setText(listMonThi.get(2).getToThi()+", ");
					lblGhepThiFE_Rep_1.setText(listMonThi.get(2).getGhepThi()+", ");
					lblSoLuongFE_Rep_1.setText(listMonThi.get(2).getSoLuong());
					lblPlaceFE_1.setText(listMonThi.get(2).getPhongThi());
					
					lblDOWFE_2.setText(listMonThi.get(3).getNgayThu());
					lblDayFE_2.setText(" - "+listMonThi.get(3).getNgayThi());
					lblShiftFE_2.setText(listMonThi.get(3).getThoiGian());
					lblCourseNameFE_2.setText(listMonThi.get(3).getTenMonHoc());
					lblShiftNumFE_2.setText(listMonThi.get(3).getTietThi()+", ");
					lblToThiFE_Rep_2.setText(listMonThi.get(3).getToThi()+", ");
					lblGhepThiFE_Rep_2.setText(listMonThi.get(3).getGhepThi()+", ");
					lblSoLuongFE_Rep_2.setText(listMonThi.get(3).getSoLuong());
					lblPlaceFE_2.setText(listMonThi.get(3).getPhongThi());
					
					lblDOWFE_3.setText(listMonThi.get(4).getNgayThu());
					lblDayFE_3.setText(" - "+listMonThi.get(4).getNgayThi());
					lblShiftFE_3.setText(listMonThi.get(4).getThoiGian());
					lblCourseNameFE_3.setText(listMonThi.get(4).getTenMonHoc());
					lblShiftNumFE_3.setText(listMonThi.get(4).getTietThi()+", ");
					lblToThiFE_Rep_3.setText(listMonThi.get(4).getToThi()+", ");
					lblGhepThiFE_Rep_3.setText(listMonThi.get(4).getGhepThi()+", ");
					lblSoLuongFE_Rep_3.setText(listMonThi.get(4).getSoLuong());
					lblPlaceFE_3.setText(listMonThi.get(4).getPhongThi());
					
					lblDOWFE_4.setText(listMonThi.get(5).getNgayThu());
					lblDayFE_4.setText(" - "+listMonThi.get(5).getNgayThi());
					lblShiftFE_4.setText(listMonThi.get(5).getThoiGian());
					lblCourseNameFE_4.setText(listMonThi.get(5).getTenMonHoc());
					lblShiftNumFE_4.setText(listMonThi.get(5).getTietThi()+", ");
					lblToThiFE_Rep_4.setText(listMonThi.get(5).getToThi()+", ");
					lblGhepThiFE_Rep_4.setText(listMonThi.get(5).getGhepThi()+", ");
					lblSoLuongFE_Rep_4.setText(listMonThi.get(5).getSoLuong());
					lblPlaceFE_4.setText(listMonThi.get(5).getPhongThi());
					
					lblDOWFE_5.setText(listMonThi.get(6).getNgayThu());
					lblDayFE_5.setText(" - "+listMonThi.get(6).getNgayThi());
					lblShiftFE_5.setText(listMonThi.get(6).getThoiGian());
					lblCourseNameFE_5.setText(listMonThi.get(6).getTenMonHoc());
					lblShiftNumFE_5.setText(listMonThi.get(6).getTietThi()+", ");
					lblToThiFE_Rep_5.setText(listMonThi.get(6).getToThi()+", ");
					lblGhepThiFE_Rep_5.setText(listMonThi.get(6).getGhepThi()+", ");
					lblSoLuongFE_Rep_5.setText(listMonThi.get(6).getSoLuong());
					lblPlaceFE_5.setText(listMonThi.get(6).getPhongThi());
					
					vBoxMainTabFE = new VBox(vboxInputCodeES, hboxButtonToggle, hboxLabelFirstFE, hboxMainSubFE, endLineBorderFE,
							hboxLabelFirstFE_0, hboxMainSubFE_0, endLineBorderFE_0, hboxLabelFirstFE_1, hboxMainSubFE_1,
							endLineBorderFE_1, hboxLabelFirstFE_2, hboxMainSubFE_2, endLineBorderFE_2
							, hboxLabelFirstFE_3, hboxMainSubFE_3, endLineBorderFE_3
							, hboxLabelFirstFE_4, hboxMainSubFE_4, endLineBorderFE_4
							, hboxLabelFirstFE_5, hboxMainSubFE_5, endLineBorderFE_5);
				
				} else if(listMonThi.size() == 8) {
					lblDOWFE.setText(listMonThi.get(0).getNgayThu());
					lblDayFE.setText(" - "+listMonThi.get(0).getNgayThi());
					lblShiftFE.setText(listMonThi.get(0).getThoiGian());
					lblCourseNameFE.setText(listMonThi.get(0).getTenMonHoc());
					lblShiftNumFE.setText(listMonThi.get(0).getTietThi()+", ");
					lblToThiFE_Rep.setText(listMonThi.get(0).getToThi()+", ");
					lblGhepThiFE_Rep.setText(listMonThi.get(0).getGhepThi()+", ");
					lblSoLuongFE_Rep.setText(listMonThi.get(0).getSoLuong());
					lblPlaceFE.setText(listMonThi.get(0).getPhongThi());
					
					lblDOWFE_0.setText(listMonThi.get(1).getNgayThu());
					lblDayFE_0.setText(" - "+listMonThi.get(1).getNgayThi());
					lblShiftFE_0.setText(listMonThi.get(1).getThoiGian());
					lblCourseNameFE_0.setText(listMonThi.get(1).getTenMonHoc());
					lblShiftNumFE_0.setText(listMonThi.get(1).getTietThi()+", ");
					lblToThiFE_Rep_0.setText(listMonThi.get(1).getToThi()+", ");
					lblGhepThiFE_Rep_0.setText(listMonThi.get(1).getGhepThi()+", ");
					lblSoLuongFE_Rep_0.setText(listMonThi.get(1).getSoLuong());
					lblPlaceFE_0.setText(listMonThi.get(1).getPhongThi());
					
					lblDOWFE_1.setText(listMonThi.get(2).getNgayThu());
					lblDayFE_1.setText(" - "+listMonThi.get(2).getNgayThi());
					lblShiftFE_1.setText(listMonThi.get(2).getThoiGian());
					lblCourseNameFE_1.setText(listMonThi.get(2).getTenMonHoc());
					lblShiftNumFE_1.setText(listMonThi.get(2).getTietThi()+", ");
					lblToThiFE_Rep_1.setText(listMonThi.get(2).getToThi()+", ");
					lblGhepThiFE_Rep_1.setText(listMonThi.get(2).getGhepThi()+", ");
					lblSoLuongFE_Rep_1.setText(listMonThi.get(2).getSoLuong());
					lblPlaceFE_1.setText(listMonThi.get(2).getPhongThi());
					
					lblDOWFE_2.setText(listMonThi.get(3).getNgayThu());
					lblDayFE_2.setText(" - "+listMonThi.get(3).getNgayThi());
					lblShiftFE_2.setText(listMonThi.get(3).getThoiGian());
					lblCourseNameFE_2.setText(listMonThi.get(3).getTenMonHoc());
					lblShiftNumFE_2.setText(listMonThi.get(3).getTietThi()+", ");
					lblToThiFE_Rep_2.setText(listMonThi.get(3).getToThi()+", ");
					lblGhepThiFE_Rep_2.setText(listMonThi.get(3).getGhepThi()+", ");
					lblSoLuongFE_Rep_2.setText(listMonThi.get(3).getSoLuong());
					lblPlaceFE_2.setText(listMonThi.get(3).getPhongThi());
					
					lblDOWFE_3.setText(listMonThi.get(4).getNgayThu());
					lblDayFE_3.setText(" - "+listMonThi.get(4).getNgayThi());
					lblShiftFE_3.setText(listMonThi.get(4).getThoiGian());
					lblCourseNameFE_3.setText(listMonThi.get(4).getTenMonHoc());
					lblShiftNumFE_3.setText(listMonThi.get(4).getTietThi()+", ");
					lblToThiFE_Rep_3.setText(listMonThi.get(4).getToThi()+", ");
					lblGhepThiFE_Rep_3.setText(listMonThi.get(4).getGhepThi()+", ");
					lblSoLuongFE_Rep_3.setText(listMonThi.get(4).getSoLuong());
					lblPlaceFE_3.setText(listMonThi.get(4).getPhongThi());
					
					lblDOWFE_4.setText(listMonThi.get(5).getNgayThu());
					lblDayFE_4.setText(" - "+listMonThi.get(5).getNgayThi());
					lblShiftFE_4.setText(listMonThi.get(5).getThoiGian());
					lblCourseNameFE_4.setText(listMonThi.get(5).getTenMonHoc());
					lblShiftNumFE_4.setText(listMonThi.get(5).getTietThi()+", ");
					lblToThiFE_Rep_4.setText(listMonThi.get(5).getToThi()+", ");
					lblGhepThiFE_Rep_4.setText(listMonThi.get(5).getGhepThi()+", ");
					lblSoLuongFE_Rep_4.setText(listMonThi.get(5).getSoLuong());
					lblPlaceFE_4.setText(listMonThi.get(5).getPhongThi());
					
					lblDOWFE_5.setText(listMonThi.get(6).getNgayThu());
					lblDayFE_5.setText(" - "+listMonThi.get(6).getNgayThi());
					lblShiftFE_5.setText(listMonThi.get(6).getThoiGian());
					lblCourseNameFE_5.setText(listMonThi.get(6).getTenMonHoc());
					lblShiftNumFE_5.setText(listMonThi.get(6).getTietThi()+", ");
					lblToThiFE_Rep_5.setText(listMonThi.get(6).getToThi()+", ");
					lblGhepThiFE_Rep_5.setText(listMonThi.get(6).getGhepThi()+", ");
					lblSoLuongFE_Rep_5.setText(listMonThi.get(6).getSoLuong());
					lblPlaceFE_5.setText(listMonThi.get(6).getPhongThi());
					
					lblDOWFE_6.setText(listMonThi.get(7).getNgayThu());
					lblDayFE_6.setText(" - "+listMonThi.get(7).getNgayThi());
					lblShiftFE_6.setText(listMonThi.get(7).getThoiGian());
					lblCourseNameFE_6.setText(listMonThi.get(7).getTenMonHoc());
					lblShiftNumFE_6.setText(listMonThi.get(7).getTietThi()+", ");
					lblToThiFE_Rep_6.setText(listMonThi.get(7).getToThi()+", ");
					lblGhepThiFE_Rep_6.setText(listMonThi.get(7).getGhepThi()+", ");
					lblSoLuongFE_Rep_6.setText(listMonThi.get(7).getSoLuong());
					lblPlaceFE_6.setText(listMonThi.get(7).getPhongThi());
					
					vBoxMainTabFE = new VBox(vboxInputCodeES, hboxButtonToggle,hboxLabelFirstFE, hboxMainSubFE, endLineBorderFE,
							hboxLabelFirstFE_0, hboxMainSubFE_0, endLineBorderFE_0, hboxLabelFirstFE_1, hboxMainSubFE_1,
							endLineBorderFE_1, hboxLabelFirstFE_2, hboxMainSubFE_2, endLineBorderFE_2
							, hboxLabelFirstFE_3, hboxMainSubFE_3, endLineBorderFE_3
							, hboxLabelFirstFE_4, hboxMainSubFE_4, endLineBorderFE_4
							, hboxLabelFirstFE_5, hboxMainSubFE_5, endLineBorderFE_5
							, hboxLabelFirstFE_6, hboxMainSubFE_6, endLineBorderFE_6);
				
				} else if(listMonThi.size() == 9) {
					lblDOWFE.setText(listMonThi.get(0).getNgayThu());
					lblDayFE.setText(" - "+listMonThi.get(0).getNgayThi());
					lblShiftFE.setText(listMonThi.get(0).getThoiGian());
					lblCourseNameFE.setText(listMonThi.get(0).getTenMonHoc());
					lblShiftNumFE.setText(listMonThi.get(0).getTietThi()+", ");
					lblToThiFE_Rep.setText(listMonThi.get(0).getToThi()+", ");
					lblGhepThiFE_Rep.setText(listMonThi.get(0).getGhepThi()+", ");
					lblSoLuongFE_Rep.setText(listMonThi.get(0).getSoLuong());
					lblPlaceFE.setText(listMonThi.get(0).getPhongThi());
					
					lblDOWFE_0.setText(listMonThi.get(1).getNgayThu());
					lblDayFE_0.setText(" - "+listMonThi.get(1).getNgayThi());
					lblShiftFE_0.setText(listMonThi.get(1).getThoiGian());
					lblCourseNameFE_0.setText(listMonThi.get(1).getTenMonHoc());
					lblShiftNumFE_0.setText(listMonThi.get(1).getTietThi()+", ");
					lblToThiFE_Rep_0.setText(listMonThi.get(1).getToThi()+", ");
					lblGhepThiFE_Rep_0.setText(listMonThi.get(1).getGhepThi()+", ");
					lblSoLuongFE_Rep_0.setText(listMonThi.get(1).getSoLuong());
					lblPlaceFE_0.setText(listMonThi.get(1).getPhongThi());
					
					lblDOWFE_1.setText(listMonThi.get(2).getNgayThu());
					lblDayFE_1.setText(" - "+listMonThi.get(2).getNgayThi());
					lblShiftFE_1.setText(listMonThi.get(2).getThoiGian());
					lblCourseNameFE_1.setText(listMonThi.get(2).getTenMonHoc());
					lblShiftNumFE_1.setText(listMonThi.get(2).getTietThi()+", ");
					lblToThiFE_Rep_1.setText(listMonThi.get(2).getToThi()+", ");
					lblGhepThiFE_Rep_1.setText(listMonThi.get(2).getGhepThi()+", ");
					lblSoLuongFE_Rep_1.setText(listMonThi.get(2).getSoLuong());
					lblPlaceFE_1.setText(listMonThi.get(2).getPhongThi());
					
					lblDOWFE_2.setText(listMonThi.get(3).getNgayThu());
					lblDayFE_2.setText(" - "+listMonThi.get(3).getNgayThi());
					lblShiftFE_2.setText(listMonThi.get(3).getThoiGian());
					lblCourseNameFE_2.setText(listMonThi.get(3).getTenMonHoc());
					lblShiftNumFE_2.setText(listMonThi.get(3).getTietThi()+", ");
					lblToThiFE_Rep_2.setText(listMonThi.get(3).getToThi()+", ");
					lblGhepThiFE_Rep_2.setText(listMonThi.get(3).getGhepThi()+", ");
					lblSoLuongFE_Rep_2.setText(listMonThi.get(3).getSoLuong());
					lblPlaceFE_2.setText(listMonThi.get(3).getPhongThi());
					
					lblDOWFE_3.setText(listMonThi.get(4).getNgayThu());
					lblDayFE_3.setText(" - "+listMonThi.get(4).getNgayThi());
					lblShiftFE_3.setText(listMonThi.get(4).getThoiGian());
					lblCourseNameFE_3.setText(listMonThi.get(4).getTenMonHoc());
					lblShiftNumFE_3.setText(listMonThi.get(4).getTietThi()+", ");
					lblToThiFE_Rep_3.setText(listMonThi.get(4).getToThi()+", ");
					lblGhepThiFE_Rep_3.setText(listMonThi.get(4).getGhepThi()+", ");
					lblSoLuongFE_Rep_3.setText(listMonThi.get(4).getSoLuong());
					lblPlaceFE_3.setText(listMonThi.get(4).getPhongThi());
					
					lblDOWFE_4.setText(listMonThi.get(5).getNgayThu());
					lblDayFE_4.setText(" - "+listMonThi.get(5).getNgayThi());
					lblShiftFE_4.setText(listMonThi.get(5).getThoiGian());
					lblCourseNameFE_4.setText(listMonThi.get(5).getTenMonHoc());
					lblShiftNumFE_4.setText(listMonThi.get(5).getTietThi()+", ");
					lblToThiFE_Rep_4.setText(listMonThi.get(5).getToThi()+", ");
					lblGhepThiFE_Rep_4.setText(listMonThi.get(5).getGhepThi()+", ");
					lblSoLuongFE_Rep_4.setText(listMonThi.get(5).getSoLuong());
					lblPlaceFE_4.setText(listMonThi.get(5).getPhongThi());
					
					lblDOWFE_5.setText(listMonThi.get(6).getNgayThu());
					lblDayFE_5.setText(" - "+listMonThi.get(6).getNgayThi());
					lblShiftFE_5.setText(listMonThi.get(6).getThoiGian());
					lblCourseNameFE_5.setText(listMonThi.get(6).getTenMonHoc());
					lblShiftNumFE_5.setText(listMonThi.get(6).getTietThi()+", ");
					lblToThiFE_Rep_5.setText(listMonThi.get(6).getToThi()+", ");
					lblGhepThiFE_Rep_5.setText(listMonThi.get(6).getGhepThi()+", ");
					lblSoLuongFE_Rep_5.setText(listMonThi.get(6).getSoLuong());
					lblPlaceFE_5.setText(listMonThi.get(6).getPhongThi());
					
					lblDOWFE_6.setText(listMonThi.get(7).getNgayThu());
					lblDayFE_6.setText(" - "+listMonThi.get(7).getNgayThi());
					lblShiftFE_6.setText(listMonThi.get(7).getThoiGian());
					lblCourseNameFE_6.setText(listMonThi.get(7).getTenMonHoc());
					lblShiftNumFE_6.setText(listMonThi.get(7).getTietThi()+", ");
					lblToThiFE_Rep_6.setText(listMonThi.get(7).getToThi()+", ");
					lblGhepThiFE_Rep_6.setText(listMonThi.get(7).getGhepThi()+", ");
					lblSoLuongFE_Rep_6.setText(listMonThi.get(7).getSoLuong());
					lblPlaceFE_6.setText(listMonThi.get(7).getPhongThi());
					
					lblDOWFE_7.setText(listMonThi.get(8).getNgayThu());
					lblDayFE_7.setText(" - "+listMonThi.get(8).getNgayThi());
					lblShiftFE_7.setText(listMonThi.get(8).getThoiGian());
					lblCourseNameFE_7.setText(listMonThi.get(8).getTenMonHoc());
					lblShiftNumFE_7.setText(listMonThi.get(8).getTietThi()+", ");
					lblToThiFE_Rep_7.setText(listMonThi.get(8).getToThi()+", ");
					lblGhepThiFE_Rep_7.setText(listMonThi.get(8).getGhepThi()+", ");
					lblSoLuongFE_Rep_7.setText(listMonThi.get(8).getSoLuong());
					lblPlaceFE_7.setText(listMonThi.get(8).getPhongThi());
					
					vBoxMainTabFE = new VBox(vboxInputCodeES, hboxButtonToggle, hboxLabelFirstFE, hboxMainSubFE, endLineBorderFE,
							hboxLabelFirstFE_0, hboxMainSubFE_0, endLineBorderFE_0, hboxLabelFirstFE_1, hboxMainSubFE_1,
							endLineBorderFE_1, hboxLabelFirstFE_2, hboxMainSubFE_2, endLineBorderFE_2
							, hboxLabelFirstFE_3, hboxMainSubFE_3, endLineBorderFE_3
							, hboxLabelFirstFE_4, hboxMainSubFE_4, endLineBorderFE_4
							, hboxLabelFirstFE_5, hboxMainSubFE_5, endLineBorderFE_5
							, hboxLabelFirstFE_6, hboxMainSubFE_6, endLineBorderFE_6
							, hboxLabelFirstFE_7, hboxMainSubFE_7, endLineBorderFE_7);
				
				} else if(listMonThi.size() == 10) {
					lblDOWFE.setText(listMonThi.get(0).getNgayThu());
					lblDayFE.setText(" - "+listMonThi.get(0).getNgayThi());
					lblShiftFE.setText(listMonThi.get(0).getThoiGian());
					lblCourseNameFE.setText(listMonThi.get(0).getTenMonHoc());
					lblShiftNumFE.setText(listMonThi.get(0).getTietThi()+", ");
					lblToThiFE_Rep.setText(listMonThi.get(0).getToThi()+", ");
					lblGhepThiFE_Rep.setText(listMonThi.get(0).getGhepThi()+", ");
					lblSoLuongFE_Rep.setText(listMonThi.get(0).getSoLuong());
					lblPlaceFE.setText(listMonThi.get(0).getPhongThi());
					
					lblDOWFE_0.setText(listMonThi.get(1).getNgayThu());
					lblDayFE_0.setText(" - "+listMonThi.get(1).getNgayThi());
					lblShiftFE_0.setText(listMonThi.get(1).getThoiGian());
					lblCourseNameFE_0.setText(listMonThi.get(1).getTenMonHoc());
					lblShiftNumFE_0.setText(listMonThi.get(1).getTietThi()+", ");
					lblToThiFE_Rep_0.setText(listMonThi.get(1).getToThi()+", ");
					lblGhepThiFE_Rep_0.setText(listMonThi.get(1).getGhepThi()+", ");
					lblSoLuongFE_Rep_0.setText(listMonThi.get(1).getSoLuong());
					lblPlaceFE_0.setText(listMonThi.get(1).getPhongThi());
					
					lblDOWFE_1.setText(listMonThi.get(2).getNgayThu());
					lblDayFE_1.setText(" - "+listMonThi.get(2).getNgayThi());
					lblShiftFE_1.setText(listMonThi.get(2).getThoiGian());
					lblCourseNameFE_1.setText(listMonThi.get(2).getTenMonHoc());
					lblShiftNumFE_1.setText(listMonThi.get(2).getTietThi()+", ");
					lblToThiFE_Rep_1.setText(listMonThi.get(2).getToThi()+", ");
					lblGhepThiFE_Rep_1.setText(listMonThi.get(2).getGhepThi()+", ");
					lblSoLuongFE_Rep_1.setText(listMonThi.get(2).getSoLuong());
					lblPlaceFE_1.setText(listMonThi.get(2).getPhongThi());
					
					lblDOWFE_2.setText(listMonThi.get(3).getNgayThu());
					lblDayFE_2.setText(" - "+listMonThi.get(3).getNgayThi());
					lblShiftFE_2.setText(listMonThi.get(3).getThoiGian());
					lblCourseNameFE_2.setText(listMonThi.get(3).getTenMonHoc());
					lblShiftNumFE_2.setText(listMonThi.get(3).getTietThi()+", ");
					lblToThiFE_Rep_2.setText(listMonThi.get(3).getToThi()+", ");
					lblGhepThiFE_Rep_2.setText(listMonThi.get(3).getGhepThi()+", ");
					lblSoLuongFE_Rep_2.setText(listMonThi.get(3).getSoLuong());
					lblPlaceFE_2.setText(listMonThi.get(3).getPhongThi());
					
					lblDOWFE_3.setText(listMonThi.get(4).getNgayThu());
					lblDayFE_3.setText(" - "+listMonThi.get(4).getNgayThi());
					lblShiftFE_3.setText(listMonThi.get(4).getThoiGian());
					lblCourseNameFE_3.setText(listMonThi.get(4).getTenMonHoc());
					lblShiftNumFE_3.setText(listMonThi.get(4).getTietThi()+", ");
					lblToThiFE_Rep_3.setText(listMonThi.get(4).getToThi()+", ");
					lblGhepThiFE_Rep_3.setText(listMonThi.get(4).getGhepThi()+", ");
					lblSoLuongFE_Rep_3.setText(listMonThi.get(4).getSoLuong());
					lblPlaceFE_3.setText(listMonThi.get(4).getPhongThi());
					
					lblDOWFE_4.setText(listMonThi.get(5).getNgayThu());
					lblDayFE_4.setText(" - "+listMonThi.get(5).getNgayThi());
					lblShiftFE_4.setText(listMonThi.get(5).getThoiGian());
					lblCourseNameFE_4.setText(listMonThi.get(5).getTenMonHoc());
					lblShiftNumFE_4.setText(listMonThi.get(5).getTietThi()+", ");
					lblToThiFE_Rep_4.setText(listMonThi.get(5).getToThi()+", ");
					lblGhepThiFE_Rep_4.setText(listMonThi.get(5).getGhepThi()+", ");
					lblSoLuongFE_Rep_4.setText(listMonThi.get(5).getSoLuong());
					lblPlaceFE_4.setText(listMonThi.get(5).getPhongThi());
					
					lblDOWFE_5.setText(listMonThi.get(6).getNgayThu());
					lblDayFE_5.setText(" - "+listMonThi.get(6).getNgayThi());
					lblShiftFE_5.setText(listMonThi.get(6).getThoiGian());
					lblCourseNameFE_5.setText(listMonThi.get(6).getTenMonHoc());
					lblShiftNumFE_5.setText(listMonThi.get(6).getTietThi()+", ");
					lblToThiFE_Rep_5.setText(listMonThi.get(6).getToThi()+", ");
					lblGhepThiFE_Rep_5.setText(listMonThi.get(6).getGhepThi()+", ");
					lblSoLuongFE_Rep_5.setText(listMonThi.get(6).getSoLuong());
					lblPlaceFE_5.setText(listMonThi.get(6).getPhongThi());
					
					lblDOWFE_6.setText(listMonThi.get(7).getNgayThu());
					lblDayFE_6.setText(" - "+listMonThi.get(7).getNgayThi());
					lblShiftFE_6.setText(listMonThi.get(7).getThoiGian());
					lblCourseNameFE_6.setText(listMonThi.get(7).getTenMonHoc());
					lblShiftNumFE_6.setText(listMonThi.get(7).getTietThi()+", ");
					lblToThiFE_Rep_6.setText(listMonThi.get(7).getToThi()+", ");
					lblGhepThiFE_Rep_6.setText(listMonThi.get(7).getGhepThi()+", ");
					lblSoLuongFE_Rep_6.setText(listMonThi.get(7).getSoLuong());
					lblPlaceFE_6.setText(listMonThi.get(7).getPhongThi());
					
					lblDOWFE_7.setText(listMonThi.get(8).getNgayThu());
					lblDayFE_7.setText(" - "+listMonThi.get(8).getNgayThi());
					lblShiftFE_7.setText(listMonThi.get(8).getThoiGian());
					lblCourseNameFE_7.setText(listMonThi.get(8).getTenMonHoc());
					lblShiftNumFE_7.setText(listMonThi.get(8).getTietThi()+", ");
					lblToThiFE_Rep_7.setText(listMonThi.get(8).getToThi()+", ");
					lblGhepThiFE_Rep_7.setText(listMonThi.get(8).getGhepThi()+", ");
					lblSoLuongFE_Rep_7.setText(listMonThi.get(8).getSoLuong());
					lblPlaceFE_7.setText(listMonThi.get(8).getPhongThi());
					
					lblDOWFE_8.setText(listMonThi.get(9).getNgayThu());
					lblDayFE_8.setText(" - "+listMonThi.get(9).getNgayThi());
					lblShiftFE_8.setText(listMonThi.get(9).getThoiGian());
					lblCourseNameFE_8.setText(listMonThi.get(9).getTenMonHoc());
					lblShiftNumFE_8.setText(listMonThi.get(9).getTietThi()+", ");
					lblToThiFE_Rep_8.setText(listMonThi.get(9).getToThi()+", ");
					lblGhepThiFE_Rep_8.setText(listMonThi.get(9).getGhepThi()+", ");
					lblSoLuongFE_Rep_8.setText(listMonThi.get(9).getSoLuong());
					lblPlaceFE_8.setText(listMonThi.get(9).getPhongThi());
					
					vBoxMainTabFE = new VBox(vboxInputCodeES, hboxButtonToggle, hboxLabelFirstFE, hboxMainSubFE, endLineBorderFE,
							hboxLabelFirstFE_0, hboxMainSubFE_0, endLineBorderFE_0, hboxLabelFirstFE_1, hboxMainSubFE_1,
							endLineBorderFE_1, hboxLabelFirstFE_2, hboxMainSubFE_2, endLineBorderFE_2
							, hboxLabelFirstFE_3, hboxMainSubFE_3, endLineBorderFE_3
							, hboxLabelFirstFE_4, hboxMainSubFE_4, endLineBorderFE_4
							, hboxLabelFirstFE_5, hboxMainSubFE_5, endLineBorderFE_5
							, hboxLabelFirstFE_6, hboxMainSubFE_6, endLineBorderFE_6
							, hboxLabelFirstFE_7, hboxMainSubFE_7, endLineBorderFE_7
							, hboxLabelFirstFE_8, hboxMainSubFE_8, endLineBorderFE_8);
				
				} else if(listMonThi.size() == 11) {
					lblDOWFE.setText(listMonThi.get(0).getNgayThu());
					lblDayFE.setText(" - "+listMonThi.get(0).getNgayThi());
					lblShiftFE.setText(listMonThi.get(0).getThoiGian());
					lblCourseNameFE.setText(listMonThi.get(0).getTenMonHoc());
					lblShiftNumFE.setText(listMonThi.get(0).getTietThi()+", ");
					lblToThiFE_Rep.setText(listMonThi.get(0).getToThi()+", ");
					lblGhepThiFE_Rep.setText(listMonThi.get(0).getGhepThi()+", ");
					lblSoLuongFE_Rep.setText(listMonThi.get(0).getSoLuong());
					lblPlaceFE.setText(listMonThi.get(0).getPhongThi());
					
					lblDOWFE_0.setText(listMonThi.get(1).getNgayThu());
					lblDayFE_0.setText(" - "+listMonThi.get(1).getNgayThi());
					lblShiftFE_0.setText(listMonThi.get(1).getThoiGian());
					lblCourseNameFE_0.setText(listMonThi.get(1).getTenMonHoc());
					lblShiftNumFE_0.setText(listMonThi.get(1).getTietThi()+", ");
					lblToThiFE_Rep_0.setText(listMonThi.get(1).getToThi()+", ");
					lblGhepThiFE_Rep_0.setText(listMonThi.get(1).getGhepThi()+", ");
					lblSoLuongFE_Rep_0.setText(listMonThi.get(1).getSoLuong());
					lblPlaceFE_0.setText(listMonThi.get(1).getPhongThi());
					
					lblDOWFE_1.setText(listMonThi.get(2).getNgayThu());
					lblDayFE_1.setText(" - "+listMonThi.get(2).getNgayThi());
					lblShiftFE_1.setText(listMonThi.get(2).getThoiGian());
					lblCourseNameFE_1.setText(listMonThi.get(2).getTenMonHoc());
					lblShiftNumFE_1.setText(listMonThi.get(2).getTietThi()+", ");
					lblToThiFE_Rep_1.setText(listMonThi.get(2).getToThi()+", ");
					lblGhepThiFE_Rep_1.setText(listMonThi.get(2).getGhepThi()+", ");
					lblSoLuongFE_Rep_1.setText(listMonThi.get(2).getSoLuong());
					lblPlaceFE_1.setText(listMonThi.get(2).getPhongThi());
					
					lblDOWFE_2.setText(listMonThi.get(3).getNgayThu());
					lblDayFE_2.setText(" - "+listMonThi.get(3).getNgayThi());
					lblShiftFE_2.setText(listMonThi.get(3).getThoiGian());
					lblCourseNameFE_2.setText(listMonThi.get(3).getTenMonHoc());
					lblShiftNumFE_2.setText(listMonThi.get(3).getTietThi()+", ");
					lblToThiFE_Rep_2.setText(listMonThi.get(3).getToThi()+", ");
					lblGhepThiFE_Rep_2.setText(listMonThi.get(3).getGhepThi()+", ");
					lblSoLuongFE_Rep_2.setText(listMonThi.get(3).getSoLuong());
					lblPlaceFE_2.setText(listMonThi.get(3).getPhongThi());
					
					lblDOWFE_3.setText(listMonThi.get(4).getNgayThu());
					lblDayFE_3.setText(" - "+listMonThi.get(4).getNgayThi());
					lblShiftFE_3.setText(listMonThi.get(4).getThoiGian());
					lblCourseNameFE_3.setText(listMonThi.get(4).getTenMonHoc());
					lblShiftNumFE_3.setText(listMonThi.get(4).getTietThi()+", ");
					lblToThiFE_Rep_3.setText(listMonThi.get(4).getToThi()+", ");
					lblGhepThiFE_Rep_3.setText(listMonThi.get(4).getGhepThi()+", ");
					lblSoLuongFE_Rep_3.setText(listMonThi.get(4).getSoLuong());
					lblPlaceFE_3.setText(listMonThi.get(4).getPhongThi());
					
					lblDOWFE_4.setText(listMonThi.get(5).getNgayThu());
					lblDayFE_4.setText(" - "+listMonThi.get(5).getNgayThi());
					lblShiftFE_4.setText(listMonThi.get(5).getThoiGian());
					lblCourseNameFE_4.setText(listMonThi.get(5).getTenMonHoc());
					lblShiftNumFE_4.setText(listMonThi.get(5).getTietThi()+", ");
					lblToThiFE_Rep_4.setText(listMonThi.get(5).getToThi()+", ");
					lblGhepThiFE_Rep_4.setText(listMonThi.get(5).getGhepThi()+", ");
					lblSoLuongFE_Rep_4.setText(listMonThi.get(5).getSoLuong());
					lblPlaceFE_4.setText(listMonThi.get(5).getPhongThi());
					
					lblDOWFE_5.setText(listMonThi.get(6).getNgayThu());
					lblDayFE_5.setText(" - "+listMonThi.get(6).getNgayThi());
					lblShiftFE_5.setText(listMonThi.get(6).getThoiGian());
					lblCourseNameFE_5.setText(listMonThi.get(6).getTenMonHoc());
					lblShiftNumFE_5.setText(listMonThi.get(6).getTietThi()+", ");
					lblToThiFE_Rep_5.setText(listMonThi.get(6).getToThi()+", ");
					lblGhepThiFE_Rep_5.setText(listMonThi.get(6).getGhepThi()+", ");
					lblSoLuongFE_Rep_5.setText(listMonThi.get(6).getSoLuong());
					lblPlaceFE_5.setText(listMonThi.get(6).getPhongThi());
					
					lblDOWFE_6.setText(listMonThi.get(7).getNgayThu());
					lblDayFE_6.setText(" - "+listMonThi.get(7).getNgayThi());
					lblShiftFE_6.setText(listMonThi.get(7).getThoiGian());
					lblCourseNameFE_6.setText(listMonThi.get(7).getTenMonHoc());
					lblShiftNumFE_6.setText(listMonThi.get(7).getTietThi()+", ");
					lblToThiFE_Rep_6.setText(listMonThi.get(7).getToThi()+", ");
					lblGhepThiFE_Rep_6.setText(listMonThi.get(7).getGhepThi()+", ");
					lblSoLuongFE_Rep_6.setText(listMonThi.get(7).getSoLuong());
					lblPlaceFE_6.setText(listMonThi.get(7).getPhongThi());
					
					lblDOWFE_7.setText(listMonThi.get(8).getNgayThu());
					lblDayFE_7.setText(" - "+listMonThi.get(8).getNgayThi());
					lblShiftFE_7.setText(listMonThi.get(8).getThoiGian());
					lblCourseNameFE_7.setText(listMonThi.get(8).getTenMonHoc());
					lblShiftNumFE_7.setText(listMonThi.get(8).getTietThi()+", ");
					lblToThiFE_Rep_7.setText(listMonThi.get(8).getToThi()+", ");
					lblGhepThiFE_Rep_7.setText(listMonThi.get(8).getGhepThi()+", ");
					lblSoLuongFE_Rep_7.setText(listMonThi.get(8).getSoLuong());
					lblPlaceFE_7.setText(listMonThi.get(8).getPhongThi());
					
					lblDOWFE_8.setText(listMonThi.get(9).getNgayThu());
					lblDayFE_8.setText(" - "+listMonThi.get(9).getNgayThi());
					lblShiftFE_8.setText(listMonThi.get(9).getThoiGian());
					lblCourseNameFE_8.setText(listMonThi.get(9).getTenMonHoc());
					lblShiftNumFE_8.setText(listMonThi.get(9).getTietThi()+", ");
					lblToThiFE_Rep_8.setText(listMonThi.get(9).getToThi()+", ");
					lblGhepThiFE_Rep_8.setText(listMonThi.get(9).getGhepThi()+", ");
					lblSoLuongFE_Rep_8.setText(listMonThi.get(9).getSoLuong());
					lblPlaceFE_8.setText(listMonThi.get(9).getPhongThi());
					
					lblDOWFE_9.setText(listMonThi.get(10).getNgayThu());
					lblDayFE_9.setText(" - "+listMonThi.get(10).getNgayThi());
					lblShiftFE_9.setText(listMonThi.get(10).getThoiGian());
					lblCourseNameFE_9.setText(listMonThi.get(10).getTenMonHoc());
					lblShiftNumFE_9.setText(listMonThi.get(10).getTietThi()+", ");
					lblToThiFE_Rep_9.setText(listMonThi.get(10).getToThi()+", ");
					lblGhepThiFE_Rep_9.setText(listMonThi.get(10).getGhepThi()+", ");
					lblSoLuongFE_Rep_9.setText(listMonThi.get(10).getSoLuong());
					lblPlaceFE_9.setText(listMonThi.get(10).getPhongThi());
					
					vBoxMainTabFE = new VBox(vboxInputCodeES, hboxButtonToggle, hboxLabelFirstFE, hboxMainSubFE, endLineBorderFE,
							hboxLabelFirstFE_0, hboxMainSubFE_0, endLineBorderFE_0, hboxLabelFirstFE_1, hboxMainSubFE_1,
							endLineBorderFE_1, hboxLabelFirstFE_2, hboxMainSubFE_2, endLineBorderFE_2
							, hboxLabelFirstFE_3, hboxMainSubFE_3, endLineBorderFE_3
							, hboxLabelFirstFE_4, hboxMainSubFE_4, endLineBorderFE_4
							, hboxLabelFirstFE_5, hboxMainSubFE_5, endLineBorderFE_5
							, hboxLabelFirstFE_6, hboxMainSubFE_6, endLineBorderFE_6
							, hboxLabelFirstFE_7, hboxMainSubFE_7, endLineBorderFE_7
							, hboxLabelFirstFE_8, hboxMainSubFE_8, endLineBorderFE_8
							, hboxLabelFirstFE_9, hboxMainSubFE_9, endLineBorderFE_9);
				
				} else if(listMonThi.size() == 12) {
					lblDOWFE.setText(listMonThi.get(0).getNgayThu());
					lblDayFE.setText(" - "+listMonThi.get(0).getNgayThi());
					lblShiftFE.setText(listMonThi.get(0).getThoiGian());
					lblCourseNameFE.setText(listMonThi.get(0).getTenMonHoc());
					lblShiftNumFE.setText(listMonThi.get(0).getTietThi()+", ");
					lblToThiFE_Rep.setText(listMonThi.get(0).getToThi()+", ");
					lblGhepThiFE_Rep.setText(listMonThi.get(0).getGhepThi()+", ");
					lblSoLuongFE_Rep.setText(listMonThi.get(0).getSoLuong());
					lblPlaceFE.setText(listMonThi.get(0).getPhongThi());
					
					lblDOWFE_0.setText(listMonThi.get(1).getNgayThu());
					lblDayFE_0.setText(" - "+listMonThi.get(1).getNgayThi());
					lblShiftFE_0.setText(listMonThi.get(1).getThoiGian());
					lblCourseNameFE_0.setText(listMonThi.get(1).getTenMonHoc());
					lblShiftNumFE_0.setText(listMonThi.get(1).getTietThi()+", ");
					lblToThiFE_Rep_0.setText(listMonThi.get(1).getToThi()+", ");
					lblGhepThiFE_Rep_0.setText(listMonThi.get(1).getGhepThi()+", ");
					lblSoLuongFE_Rep_0.setText(listMonThi.get(1).getSoLuong());
					lblPlaceFE_0.setText(listMonThi.get(1).getPhongThi());
					
					lblDOWFE_1.setText(listMonThi.get(2).getNgayThu());
					lblDayFE_1.setText(" - "+listMonThi.get(2).getNgayThi());
					lblShiftFE_1.setText(listMonThi.get(2).getThoiGian());
					lblCourseNameFE_1.setText(listMonThi.get(2).getTenMonHoc());
					lblShiftNumFE_1.setText(listMonThi.get(2).getTietThi()+", ");
					lblToThiFE_Rep_1.setText(listMonThi.get(2).getToThi()+", ");
					lblGhepThiFE_Rep_1.setText(listMonThi.get(2).getGhepThi()+", ");
					lblSoLuongFE_Rep_1.setText(listMonThi.get(2).getSoLuong());
					lblPlaceFE_1.setText(listMonThi.get(2).getPhongThi());
					
					lblDOWFE_2.setText(listMonThi.get(3).getNgayThu());
					lblDayFE_2.setText(" - "+listMonThi.get(3).getNgayThi());
					lblShiftFE_2.setText(listMonThi.get(3).getThoiGian());
					lblCourseNameFE_2.setText(listMonThi.get(3).getTenMonHoc());
					lblShiftNumFE_2.setText(listMonThi.get(3).getTietThi()+", ");
					lblToThiFE_Rep_2.setText(listMonThi.get(3).getToThi()+", ");
					lblGhepThiFE_Rep_2.setText(listMonThi.get(3).getGhepThi()+", ");
					lblSoLuongFE_Rep_2.setText(listMonThi.get(3).getSoLuong());
					lblPlaceFE_2.setText(listMonThi.get(3).getPhongThi());
					
					lblDOWFE_3.setText(listMonThi.get(4).getNgayThu());
					lblDayFE_3.setText(" - "+listMonThi.get(4).getNgayThi());
					lblShiftFE_3.setText(listMonThi.get(4).getThoiGian());
					lblCourseNameFE_3.setText(listMonThi.get(4).getTenMonHoc());
					lblShiftNumFE_3.setText(listMonThi.get(4).getTietThi()+", ");
					lblToThiFE_Rep_3.setText(listMonThi.get(4).getToThi()+", ");
					lblGhepThiFE_Rep_3.setText(listMonThi.get(4).getGhepThi()+", ");
					lblSoLuongFE_Rep_3.setText(listMonThi.get(4).getSoLuong());
					lblPlaceFE_3.setText(listMonThi.get(4).getPhongThi());
					
					lblDOWFE_4.setText(listMonThi.get(5).getNgayThu());
					lblDayFE_4.setText(" - "+listMonThi.get(5).getNgayThi());
					lblShiftFE_4.setText(listMonThi.get(5).getThoiGian());
					lblCourseNameFE_4.setText(listMonThi.get(5).getTenMonHoc());
					lblShiftNumFE_4.setText(listMonThi.get(5).getTietThi()+", ");
					lblToThiFE_Rep_4.setText(listMonThi.get(5).getToThi()+", ");
					lblGhepThiFE_Rep_4.setText(listMonThi.get(5).getGhepThi()+", ");
					lblSoLuongFE_Rep_4.setText(listMonThi.get(5).getSoLuong());
					lblPlaceFE_4.setText(listMonThi.get(5).getPhongThi());
					
					lblDOWFE_5.setText(listMonThi.get(6).getNgayThu());
					lblDayFE_5.setText(" - "+listMonThi.get(6).getNgayThi());
					lblShiftFE_5.setText(listMonThi.get(6).getThoiGian());
					lblCourseNameFE_5.setText(listMonThi.get(6).getTenMonHoc());
					lblShiftNumFE_5.setText(listMonThi.get(6).getTietThi()+", ");
					lblToThiFE_Rep_5.setText(listMonThi.get(6).getToThi()+", ");
					lblGhepThiFE_Rep_5.setText(listMonThi.get(6).getGhepThi()+", ");
					lblSoLuongFE_Rep_5.setText(listMonThi.get(6).getSoLuong());
					lblPlaceFE_5.setText(listMonThi.get(6).getPhongThi());
					
					lblDOWFE_6.setText(listMonThi.get(7).getNgayThu());
					lblDayFE_6.setText(" - "+listMonThi.get(7).getNgayThi());
					lblShiftFE_6.setText(listMonThi.get(7).getThoiGian());
					lblCourseNameFE_6.setText(listMonThi.get(7).getTenMonHoc());
					lblShiftNumFE_6.setText(listMonThi.get(7).getTietThi()+", ");
					lblToThiFE_Rep_6.setText(listMonThi.get(7).getToThi()+", ");
					lblGhepThiFE_Rep_6.setText(listMonThi.get(7).getGhepThi()+", ");
					lblSoLuongFE_Rep_6.setText(listMonThi.get(7).getSoLuong());
					lblPlaceFE_6.setText(listMonThi.get(7).getPhongThi());
					
					lblDOWFE_7.setText(listMonThi.get(8).getNgayThu());
					lblDayFE_7.setText(" - "+listMonThi.get(8).getNgayThi());
					lblShiftFE_7.setText(listMonThi.get(8).getThoiGian());
					lblCourseNameFE_7.setText(listMonThi.get(8).getTenMonHoc());
					lblShiftNumFE_7.setText(listMonThi.get(8).getTietThi()+", ");
					lblToThiFE_Rep_7.setText(listMonThi.get(8).getToThi()+", ");
					lblGhepThiFE_Rep_7.setText(listMonThi.get(8).getGhepThi()+", ");
					lblSoLuongFE_Rep_7.setText(listMonThi.get(8).getSoLuong());
					lblPlaceFE_7.setText(listMonThi.get(8).getPhongThi());
					
					lblDOWFE_8.setText(listMonThi.get(9).getNgayThu());
					lblDayFE_8.setText(" - "+listMonThi.get(9).getNgayThi());
					lblShiftFE_8.setText(listMonThi.get(9).getThoiGian());
					lblCourseNameFE_8.setText(listMonThi.get(9).getTenMonHoc());
					lblShiftNumFE_8.setText(listMonThi.get(9).getTietThi()+", ");
					lblToThiFE_Rep_8.setText(listMonThi.get(9).getToThi()+", ");
					lblGhepThiFE_Rep_8.setText(listMonThi.get(9).getGhepThi()+", ");
					lblSoLuongFE_Rep_8.setText(listMonThi.get(9).getSoLuong());
					lblPlaceFE_8.setText(listMonThi.get(9).getPhongThi());
					
					lblDOWFE_9.setText(listMonThi.get(10).getNgayThu());
					lblDayFE_9.setText(" - "+listMonThi.get(10).getNgayThi());
					lblShiftFE_9.setText(listMonThi.get(10).getThoiGian());
					lblCourseNameFE_9.setText(listMonThi.get(10).getTenMonHoc());
					lblShiftNumFE_9.setText(listMonThi.get(10).getTietThi()+", ");
					lblToThiFE_Rep_9.setText(listMonThi.get(10).getToThi()+", ");
					lblGhepThiFE_Rep_9.setText(listMonThi.get(10).getGhepThi()+", ");
					lblSoLuongFE_Rep_9.setText(listMonThi.get(10).getSoLuong());
					lblPlaceFE_9.setText(listMonThi.get(10).getPhongThi());
					
					lblDOWFE_10.setText(listMonThi.get(11).getNgayThu());
					lblDayFE_10.setText(" - "+listMonThi.get(11).getNgayThi());
					lblShiftFE_10.setText(listMonThi.get(11).getThoiGian());
					lblCourseNameFE_10.setText(listMonThi.get(11).getTenMonHoc());
					lblShiftNumFE_10.setText(listMonThi.get(11).getTietThi()+", ");
					lblToThiFE_Rep_10.setText(listMonThi.get(11).getToThi()+", ");
					lblGhepThiFE_Rep_10.setText(listMonThi.get(11).getGhepThi()+", ");
					lblSoLuongFE_Rep_10.setText(listMonThi.get(11).getSoLuong());
					lblPlaceFE_10.setText(listMonThi.get(11).getPhongThi());
					
					vBoxMainTabFE = new VBox(vboxInputCodeES, hboxButtonToggle, hboxLabelFirstFE, hboxMainSubFE, endLineBorderFE,
							hboxLabelFirstFE_0, hboxMainSubFE_0, endLineBorderFE_0, hboxLabelFirstFE_1, hboxMainSubFE_1,
							endLineBorderFE_1, hboxLabelFirstFE_2, hboxMainSubFE_2, endLineBorderFE_2
							, hboxLabelFirstFE_3, hboxMainSubFE_3, endLineBorderFE_3
							, hboxLabelFirstFE_4, hboxMainSubFE_4, endLineBorderFE_4
							, hboxLabelFirstFE_5, hboxMainSubFE_5, endLineBorderFE_5
							, hboxLabelFirstFE_6, hboxMainSubFE_6, endLineBorderFE_6
							, hboxLabelFirstFE_7, hboxMainSubFE_7, endLineBorderFE_7
							, hboxLabelFirstFE_8, hboxMainSubFE_8, endLineBorderFE_8
							, hboxLabelFirstFE_9, hboxMainSubFE_9, endLineBorderFE_9
							, hboxLabelFirstFE_10, hboxMainSubFE_10, endLineBorderFE_10);
				
				} else if(listMonThi.size() == 13) {
					lblDOWFE.setText(listMonThi.get(0).getNgayThu());
					lblDayFE.setText(" - "+listMonThi.get(0).getNgayThi());
					lblShiftFE.setText(listMonThi.get(0).getThoiGian());
					lblCourseNameFE.setText(listMonThi.get(0).getTenMonHoc());
					lblShiftNumFE.setText(listMonThi.get(0).getTietThi()+", ");
					lblToThiFE_Rep.setText(listMonThi.get(0).getToThi()+", ");
					lblGhepThiFE_Rep.setText(listMonThi.get(0).getGhepThi()+", ");
					lblSoLuongFE_Rep.setText(listMonThi.get(0).getSoLuong());
					lblPlaceFE.setText(listMonThi.get(0).getPhongThi());
					
					lblDOWFE_0.setText(listMonThi.get(1).getNgayThu());
					lblDayFE_0.setText(" - "+listMonThi.get(1).getNgayThi());
					lblShiftFE_0.setText(listMonThi.get(1).getThoiGian());
					lblCourseNameFE_0.setText(listMonThi.get(1).getTenMonHoc());
					lblShiftNumFE_0.setText(listMonThi.get(1).getTietThi()+", ");
					lblToThiFE_Rep_0.setText(listMonThi.get(1).getToThi()+", ");
					lblGhepThiFE_Rep_0.setText(listMonThi.get(1).getGhepThi()+", ");
					lblSoLuongFE_Rep_0.setText(listMonThi.get(1).getSoLuong());
					lblPlaceFE_0.setText(listMonThi.get(1).getPhongThi());
					
					lblDOWFE_1.setText(listMonThi.get(2).getNgayThu());
					lblDayFE_1.setText(" - "+listMonThi.get(2).getNgayThi());
					lblShiftFE_1.setText(listMonThi.get(2).getThoiGian());
					lblCourseNameFE_1.setText(listMonThi.get(2).getTenMonHoc());
					lblShiftNumFE_1.setText(listMonThi.get(2).getTietThi()+", ");
					lblToThiFE_Rep_1.setText(listMonThi.get(2).getToThi()+", ");
					lblGhepThiFE_Rep_1.setText(listMonThi.get(2).getGhepThi()+", ");
					lblSoLuongFE_Rep_1.setText(listMonThi.get(2).getSoLuong());
					lblPlaceFE_1.setText(listMonThi.get(2).getPhongThi());
					
					lblDOWFE_2.setText(listMonThi.get(3).getNgayThu());
					lblDayFE_2.setText(" - "+listMonThi.get(3).getNgayThi());
					lblShiftFE_2.setText(listMonThi.get(3).getThoiGian());
					lblCourseNameFE_2.setText(listMonThi.get(3).getTenMonHoc());
					lblShiftNumFE_2.setText(listMonThi.get(3).getTietThi()+", ");
					lblToThiFE_Rep_2.setText(listMonThi.get(3).getToThi()+", ");
					lblGhepThiFE_Rep_2.setText(listMonThi.get(3).getGhepThi()+", ");
					lblSoLuongFE_Rep_2.setText(listMonThi.get(3).getSoLuong());
					lblPlaceFE_2.setText(listMonThi.get(3).getPhongThi());
					
					lblDOWFE_3.setText(listMonThi.get(4).getNgayThu());
					lblDayFE_3.setText(" - "+listMonThi.get(4).getNgayThi());
					lblShiftFE_3.setText(listMonThi.get(4).getThoiGian());
					lblCourseNameFE_3.setText(listMonThi.get(4).getTenMonHoc());
					lblShiftNumFE_3.setText(listMonThi.get(4).getTietThi()+", ");
					lblToThiFE_Rep_3.setText(listMonThi.get(4).getToThi()+", ");
					lblGhepThiFE_Rep_3.setText(listMonThi.get(4).getGhepThi()+", ");
					lblSoLuongFE_Rep_3.setText(listMonThi.get(4).getSoLuong());
					lblPlaceFE_3.setText(listMonThi.get(4).getPhongThi());
					
					lblDOWFE_4.setText(listMonThi.get(5).getNgayThu());
					lblDayFE_4.setText(" - "+listMonThi.get(5).getNgayThi());
					lblShiftFE_4.setText(listMonThi.get(5).getThoiGian());
					lblCourseNameFE_4.setText(listMonThi.get(5).getTenMonHoc());
					lblShiftNumFE_4.setText(listMonThi.get(5).getTietThi()+", ");
					lblToThiFE_Rep_4.setText(listMonThi.get(5).getToThi()+", ");
					lblGhepThiFE_Rep_4.setText(listMonThi.get(5).getGhepThi()+", ");
					lblSoLuongFE_Rep_4.setText(listMonThi.get(5).getSoLuong());
					lblPlaceFE_4.setText(listMonThi.get(5).getPhongThi());
					
					lblDOWFE_5.setText(listMonThi.get(6).getNgayThu());
					lblDayFE_5.setText(" - "+listMonThi.get(6).getNgayThi());
					lblShiftFE_5.setText(listMonThi.get(6).getThoiGian());
					lblCourseNameFE_5.setText(listMonThi.get(6).getTenMonHoc());
					lblShiftNumFE_5.setText(listMonThi.get(6).getTietThi()+", ");
					lblToThiFE_Rep_5.setText(listMonThi.get(6).getToThi()+", ");
					lblGhepThiFE_Rep_5.setText(listMonThi.get(6).getGhepThi()+", ");
					lblSoLuongFE_Rep_5.setText(listMonThi.get(6).getSoLuong());
					lblPlaceFE_5.setText(listMonThi.get(6).getPhongThi());
					
					lblDOWFE_6.setText(listMonThi.get(7).getNgayThu());
					lblDayFE_6.setText(" - "+listMonThi.get(7).getNgayThi());
					lblShiftFE_6.setText(listMonThi.get(7).getThoiGian());
					lblCourseNameFE_6.setText(listMonThi.get(7).getTenMonHoc());
					lblShiftNumFE_6.setText(listMonThi.get(7).getTietThi()+", ");
					lblToThiFE_Rep_6.setText(listMonThi.get(7).getToThi()+", ");
					lblGhepThiFE_Rep_6.setText(listMonThi.get(7).getGhepThi()+", ");
					lblSoLuongFE_Rep_6.setText(listMonThi.get(7).getSoLuong());
					lblPlaceFE_6.setText(listMonThi.get(7).getPhongThi());
					
					lblDOWFE_7.setText(listMonThi.get(8).getNgayThu());
					lblDayFE_7.setText(" - "+listMonThi.get(8).getNgayThi());
					lblShiftFE_7.setText(listMonThi.get(8).getThoiGian());
					lblCourseNameFE_7.setText(listMonThi.get(8).getTenMonHoc());
					lblShiftNumFE_7.setText(listMonThi.get(8).getTietThi()+", ");
					lblToThiFE_Rep_7.setText(listMonThi.get(8).getToThi()+", ");
					lblGhepThiFE_Rep_7.setText(listMonThi.get(8).getGhepThi()+", ");
					lblSoLuongFE_Rep_7.setText(listMonThi.get(8).getSoLuong());
					lblPlaceFE_7.setText(listMonThi.get(8).getPhongThi());
					
					lblDOWFE_8.setText(listMonThi.get(9).getNgayThu());
					lblDayFE_8.setText(" - "+listMonThi.get(9).getNgayThi());
					lblShiftFE_8.setText(listMonThi.get(9).getThoiGian());
					lblCourseNameFE_8.setText(listMonThi.get(9).getTenMonHoc());
					lblShiftNumFE_8.setText(listMonThi.get(9).getTietThi()+", ");
					lblToThiFE_Rep_8.setText(listMonThi.get(9).getToThi()+", ");
					lblGhepThiFE_Rep_8.setText(listMonThi.get(9).getGhepThi()+", ");
					lblSoLuongFE_Rep_8.setText(listMonThi.get(9).getSoLuong());
					lblPlaceFE_8.setText(listMonThi.get(9).getPhongThi());
					
					lblDOWFE_9.setText(listMonThi.get(10).getNgayThu());
					lblDayFE_9.setText(" - "+listMonThi.get(10).getNgayThi());
					lblShiftFE_9.setText(listMonThi.get(10).getThoiGian());
					lblCourseNameFE_9.setText(listMonThi.get(10).getTenMonHoc());
					lblShiftNumFE_9.setText(listMonThi.get(10).getTietThi()+", ");
					lblToThiFE_Rep_9.setText(listMonThi.get(10).getToThi()+", ");
					lblGhepThiFE_Rep_9.setText(listMonThi.get(10).getGhepThi()+", ");
					lblSoLuongFE_Rep_9.setText(listMonThi.get(10).getSoLuong());
					lblPlaceFE_9.setText(listMonThi.get(10).getPhongThi());
					
					lblDOWFE_10.setText(listMonThi.get(11).getNgayThu());
					lblDayFE_10.setText(" - "+listMonThi.get(11).getNgayThi());
					lblShiftFE_10.setText(listMonThi.get(11).getThoiGian());
					lblCourseNameFE_10.setText(listMonThi.get(11).getTenMonHoc());
					lblShiftNumFE_10.setText(listMonThi.get(11).getTietThi()+", ");
					lblToThiFE_Rep_10.setText(listMonThi.get(11).getToThi()+", ");
					lblGhepThiFE_Rep_10.setText(listMonThi.get(11).getGhepThi()+", ");
					lblSoLuongFE_Rep_10.setText(listMonThi.get(11).getSoLuong());
					lblPlaceFE_10.setText(listMonThi.get(11).getPhongThi());
					
					lblDOWFE_11.setText(listMonThi.get(12).getNgayThu());
					lblDayFE_11.setText(" - "+listMonThi.get(12).getNgayThi());
					lblShiftFE_11.setText(listMonThi.get(12).getThoiGian());
					lblCourseNameFE_11.setText(listMonThi.get(12).getTenMonHoc());
					lblShiftNumFE_11.setText(listMonThi.get(12).getTietThi()+", ");
					lblToThiFE_Rep_11.setText(listMonThi.get(12).getToThi()+", ");
					lblGhepThiFE_Rep_11.setText(listMonThi.get(12).getGhepThi()+", ");
					lblSoLuongFE_Rep_11.setText(listMonThi.get(12).getSoLuong());
					lblPlaceFE_11.setText(listMonThi.get(12).getPhongThi());
					
					vBoxMainTabFE = new VBox(vboxInputCodeES, hboxButtonToggle, hboxLabelFirstFE, hboxMainSubFE, endLineBorderFE,
							hboxLabelFirstFE_0, hboxMainSubFE_0, endLineBorderFE_0, hboxLabelFirstFE_1, hboxMainSubFE_1,
							endLineBorderFE_1, hboxLabelFirstFE_2, hboxMainSubFE_2, endLineBorderFE_2
							, hboxLabelFirstFE_3, hboxMainSubFE_3, endLineBorderFE_3
							, hboxLabelFirstFE_4, hboxMainSubFE_4, endLineBorderFE_4
							, hboxLabelFirstFE_5, hboxMainSubFE_5, endLineBorderFE_5
							, hboxLabelFirstFE_6, hboxMainSubFE_6, endLineBorderFE_6
							, hboxLabelFirstFE_7, hboxMainSubFE_7, endLineBorderFE_7
							, hboxLabelFirstFE_8, hboxMainSubFE_8, endLineBorderFE_8
							, hboxLabelFirstFE_9, hboxMainSubFE_9, endLineBorderFE_9
							, hboxLabelFirstFE_10, hboxMainSubFE_10, endLineBorderFE_10
							, hboxLabelFirstFE_11, hboxMainSubFE_11, endLineBorderFE_11);
				
				} else if(listMonThi.size() == 14) {
					lblDOWFE.setText(listMonThi.get(0).getNgayThu());
					lblDayFE.setText(" - "+listMonThi.get(0).getNgayThi());
					lblShiftFE.setText(listMonThi.get(0).getThoiGian());
					lblCourseNameFE.setText(listMonThi.get(0).getTenMonHoc());
					lblShiftNumFE.setText(listMonThi.get(0).getTietThi()+", ");
					lblToThiFE_Rep.setText(listMonThi.get(0).getToThi()+", ");
					lblGhepThiFE_Rep.setText(listMonThi.get(0).getGhepThi()+", ");
					lblSoLuongFE_Rep.setText(listMonThi.get(0).getSoLuong());
					lblPlaceFE.setText(listMonThi.get(0).getPhongThi());
					
					lblDOWFE_0.setText(listMonThi.get(1).getNgayThu());
					lblDayFE_0.setText(" - "+listMonThi.get(1).getNgayThi());
					lblShiftFE_0.setText(listMonThi.get(1).getThoiGian());
					lblCourseNameFE_0.setText(listMonThi.get(1).getTenMonHoc());
					lblShiftNumFE_0.setText(listMonThi.get(1).getTietThi()+", ");
					lblToThiFE_Rep_0.setText(listMonThi.get(1).getToThi()+", ");
					lblGhepThiFE_Rep_0.setText(listMonThi.get(1).getGhepThi()+", ");
					lblSoLuongFE_Rep_0.setText(listMonThi.get(1).getSoLuong());
					lblPlaceFE_0.setText(listMonThi.get(1).getPhongThi());
					
					lblDOWFE_1.setText(listMonThi.get(2).getNgayThu());
					lblDayFE_1.setText(" - "+listMonThi.get(2).getNgayThi());
					lblShiftFE_1.setText(listMonThi.get(2).getThoiGian());
					lblCourseNameFE_1.setText(listMonThi.get(2).getTenMonHoc());
					lblShiftNumFE_1.setText(listMonThi.get(2).getTietThi()+", ");
					lblToThiFE_Rep_1.setText(listMonThi.get(2).getToThi()+", ");
					lblGhepThiFE_Rep_1.setText(listMonThi.get(2).getGhepThi()+", ");
					lblSoLuongFE_Rep_1.setText(listMonThi.get(2).getSoLuong());
					lblPlaceFE_1.setText(listMonThi.get(2).getPhongThi());
					
					lblDOWFE_2.setText(listMonThi.get(3).getNgayThu());
					lblDayFE_2.setText(" - "+listMonThi.get(3).getNgayThi());
					lblShiftFE_2.setText(listMonThi.get(3).getThoiGian());
					lblCourseNameFE_2.setText(listMonThi.get(3).getTenMonHoc());
					lblShiftNumFE_2.setText(listMonThi.get(3).getTietThi()+", ");
					lblToThiFE_Rep_2.setText(listMonThi.get(3).getToThi()+", ");
					lblGhepThiFE_Rep_2.setText(listMonThi.get(3).getGhepThi()+", ");
					lblSoLuongFE_Rep_2.setText(listMonThi.get(3).getSoLuong());
					lblPlaceFE_2.setText(listMonThi.get(3).getPhongThi());
					
					lblDOWFE_3.setText(listMonThi.get(4).getNgayThu());
					lblDayFE_3.setText(" - "+listMonThi.get(4).getNgayThi());
					lblShiftFE_3.setText(listMonThi.get(4).getThoiGian());
					lblCourseNameFE_3.setText(listMonThi.get(4).getTenMonHoc());
					lblShiftNumFE_3.setText(listMonThi.get(4).getTietThi()+", ");
					lblToThiFE_Rep_3.setText(listMonThi.get(4).getToThi()+", ");
					lblGhepThiFE_Rep_3.setText(listMonThi.get(4).getGhepThi()+", ");
					lblSoLuongFE_Rep_3.setText(listMonThi.get(4).getSoLuong());
					lblPlaceFE_3.setText(listMonThi.get(4).getPhongThi());
					
					lblDOWFE_4.setText(listMonThi.get(5).getNgayThu());
					lblDayFE_4.setText(" - "+listMonThi.get(5).getNgayThi());
					lblShiftFE_4.setText(listMonThi.get(5).getThoiGian());
					lblCourseNameFE_4.setText(listMonThi.get(5).getTenMonHoc());
					lblShiftNumFE_4.setText(listMonThi.get(5).getTietThi()+", ");
					lblToThiFE_Rep_4.setText(listMonThi.get(5).getToThi()+", ");
					lblGhepThiFE_Rep_4.setText(listMonThi.get(5).getGhepThi()+", ");
					lblSoLuongFE_Rep_4.setText(listMonThi.get(5).getSoLuong());
					lblPlaceFE_4.setText(listMonThi.get(5).getPhongThi());
					
					lblDOWFE_5.setText(listMonThi.get(6).getNgayThu());
					lblDayFE_5.setText(" - "+listMonThi.get(6).getNgayThi());
					lblShiftFE_5.setText(listMonThi.get(6).getThoiGian());
					lblCourseNameFE_5.setText(listMonThi.get(6).getTenMonHoc());
					lblShiftNumFE_5.setText(listMonThi.get(6).getTietThi()+", ");
					lblToThiFE_Rep_5.setText(listMonThi.get(6).getToThi()+", ");
					lblGhepThiFE_Rep_5.setText(listMonThi.get(6).getGhepThi()+", ");
					lblSoLuongFE_Rep_5.setText(listMonThi.get(6).getSoLuong());
					lblPlaceFE_5.setText(listMonThi.get(6).getPhongThi());
					
					lblDOWFE_6.setText(listMonThi.get(7).getNgayThu());
					lblDayFE_6.setText(" - "+listMonThi.get(7).getNgayThi());
					lblShiftFE_6.setText(listMonThi.get(7).getThoiGian());
					lblCourseNameFE_6.setText(listMonThi.get(7).getTenMonHoc());
					lblShiftNumFE_6.setText(listMonThi.get(7).getTietThi()+", ");
					lblToThiFE_Rep_6.setText(listMonThi.get(7).getToThi()+", ");
					lblGhepThiFE_Rep_6.setText(listMonThi.get(7).getGhepThi()+", ");
					lblSoLuongFE_Rep_6.setText(listMonThi.get(7).getSoLuong());
					lblPlaceFE_6.setText(listMonThi.get(7).getPhongThi());
					
					lblDOWFE_7.setText(listMonThi.get(8).getNgayThu());
					lblDayFE_7.setText(" - "+listMonThi.get(8).getNgayThi());
					lblShiftFE_7.setText(listMonThi.get(8).getThoiGian());
					lblCourseNameFE_7.setText(listMonThi.get(8).getTenMonHoc());
					lblShiftNumFE_7.setText(listMonThi.get(8).getTietThi()+", ");
					lblToThiFE_Rep_7.setText(listMonThi.get(8).getToThi()+", ");
					lblGhepThiFE_Rep_7.setText(listMonThi.get(8).getGhepThi()+", ");
					lblSoLuongFE_Rep_7.setText(listMonThi.get(8).getSoLuong());
					lblPlaceFE_7.setText(listMonThi.get(8).getPhongThi());
					
					lblDOWFE_8.setText(listMonThi.get(9).getNgayThu());
					lblDayFE_8.setText(" - "+listMonThi.get(9).getNgayThi());
					lblShiftFE_8.setText(listMonThi.get(9).getThoiGian());
					lblCourseNameFE_8.setText(listMonThi.get(9).getTenMonHoc());
					lblShiftNumFE_8.setText(listMonThi.get(9).getTietThi()+", ");
					lblToThiFE_Rep_8.setText(listMonThi.get(9).getToThi()+", ");
					lblGhepThiFE_Rep_8.setText(listMonThi.get(9).getGhepThi()+", ");
					lblSoLuongFE_Rep_8.setText(listMonThi.get(9).getSoLuong());
					lblPlaceFE_8.setText(listMonThi.get(9).getPhongThi());
					
					lblDOWFE_9.setText(listMonThi.get(10).getNgayThu());
					lblDayFE_9.setText(" - "+listMonThi.get(10).getNgayThi());
					lblShiftFE_9.setText(listMonThi.get(10).getThoiGian());
					lblCourseNameFE_9.setText(listMonThi.get(10).getTenMonHoc());
					lblShiftNumFE_9.setText(listMonThi.get(10).getTietThi()+", ");
					lblToThiFE_Rep_9.setText(listMonThi.get(10).getToThi()+", ");
					lblGhepThiFE_Rep_9.setText(listMonThi.get(10).getGhepThi()+", ");
					lblSoLuongFE_Rep_9.setText(listMonThi.get(10).getSoLuong());
					lblPlaceFE_9.setText(listMonThi.get(10).getPhongThi());
					
					lblDOWFE_10.setText(listMonThi.get(11).getNgayThu());
					lblDayFE_10.setText(" - "+listMonThi.get(11).getNgayThi());
					lblShiftFE_10.setText(listMonThi.get(11).getThoiGian());
					lblCourseNameFE_10.setText(listMonThi.get(11).getTenMonHoc());
					lblShiftNumFE_10.setText(listMonThi.get(11).getTietThi()+", ");
					lblToThiFE_Rep_10.setText(listMonThi.get(11).getToThi()+", ");
					lblGhepThiFE_Rep_10.setText(listMonThi.get(11).getGhepThi()+", ");
					lblSoLuongFE_Rep_10.setText(listMonThi.get(11).getSoLuong());
					lblPlaceFE_10.setText(listMonThi.get(11).getPhongThi());
					
					lblDOWFE_11.setText(listMonThi.get(12).getNgayThu());
					lblDayFE_11.setText(" - "+listMonThi.get(12).getNgayThi());
					lblShiftFE_11.setText(listMonThi.get(12).getThoiGian());
					lblCourseNameFE_11.setText(listMonThi.get(12).getTenMonHoc());
					lblShiftNumFE_11.setText(listMonThi.get(12).getTietThi()+", ");
					lblToThiFE_Rep_11.setText(listMonThi.get(12).getToThi()+", ");
					lblGhepThiFE_Rep_11.setText(listMonThi.get(12).getGhepThi()+", ");
					lblSoLuongFE_Rep_11.setText(listMonThi.get(12).getSoLuong());
					lblPlaceFE_11.setText(listMonThi.get(12).getPhongThi());
					
					lblDOWFE_12.setText(listMonThi.get(13).getNgayThu());
					lblDayFE_12.setText(" - "+listMonThi.get(13).getNgayThi());
					lblShiftFE_12.setText(listMonThi.get(13).getThoiGian());
					lblCourseNameFE_12.setText(listMonThi.get(13).getTenMonHoc());
					lblShiftNumFE_12.setText(listMonThi.get(13).getTietThi()+", ");
					lblToThiFE_Rep_12.setText(listMonThi.get(13).getToThi()+", ");
					lblGhepThiFE_Rep_12.setText(listMonThi.get(13).getGhepThi()+", ");
					lblSoLuongFE_Rep_12.setText(listMonThi.get(13).getSoLuong());
					lblPlaceFE_12.setText(listMonThi.get(13).getPhongThi());
					
					vBoxMainTabFE = new VBox(vboxInputCodeES, hboxButtonToggle, hboxLabelFirstFE, hboxMainSubFE, endLineBorderFE,
							hboxLabelFirstFE_0, hboxMainSubFE_0, endLineBorderFE_0, hboxLabelFirstFE_1, hboxMainSubFE_1,
							endLineBorderFE_1, hboxLabelFirstFE_2, hboxMainSubFE_2, endLineBorderFE_2
							, hboxLabelFirstFE_3, hboxMainSubFE_3, endLineBorderFE_3
							, hboxLabelFirstFE_4, hboxMainSubFE_4, endLineBorderFE_4
							, hboxLabelFirstFE_5, hboxMainSubFE_5, endLineBorderFE_5
							, hboxLabelFirstFE_6, hboxMainSubFE_6, endLineBorderFE_6
							, hboxLabelFirstFE_7, hboxMainSubFE_7, endLineBorderFE_7
							, hboxLabelFirstFE_8, hboxMainSubFE_8, endLineBorderFE_8
							, hboxLabelFirstFE_9, hboxMainSubFE_9, endLineBorderFE_9
							, hboxLabelFirstFE_10, hboxMainSubFE_10, endLineBorderFE_10
							, hboxLabelFirstFE_11, hboxMainSubFE_11, endLineBorderFE_11
							, hboxLabelFirstFE_12, hboxMainSubFE_12, endLineBorderFE_12);
				
				} else if(listMonThi.size() == 15) {
					lblDOWFE.setText(listMonThi.get(0).getNgayThu());
					lblDayFE.setText(" - "+listMonThi.get(0).getNgayThi());
					lblShiftFE.setText(listMonThi.get(0).getThoiGian());
					lblCourseNameFE.setText(listMonThi.get(0).getTenMonHoc());
					lblShiftNumFE.setText(listMonThi.get(0).getTietThi()+", ");
					lblToThiFE_Rep.setText(listMonThi.get(0).getToThi()+", ");
					lblGhepThiFE_Rep.setText(listMonThi.get(0).getGhepThi()+", ");
					lblSoLuongFE_Rep.setText(listMonThi.get(0).getSoLuong());
					lblPlaceFE.setText(listMonThi.get(0).getPhongThi());
					
					lblDOWFE_0.setText(listMonThi.get(1).getNgayThu());
					lblDayFE_0.setText(" - "+listMonThi.get(1).getNgayThi());
					lblShiftFE_0.setText(listMonThi.get(1).getThoiGian());
					lblCourseNameFE_0.setText(listMonThi.get(1).getTenMonHoc());
					lblShiftNumFE_0.setText(listMonThi.get(1).getTietThi()+", ");
					lblToThiFE_Rep_0.setText(listMonThi.get(1).getToThi()+", ");
					lblGhepThiFE_Rep_0.setText(listMonThi.get(1).getGhepThi()+", ");
					lblSoLuongFE_Rep_0.setText(listMonThi.get(1).getSoLuong());
					lblPlaceFE_0.setText(listMonThi.get(1).getPhongThi());
					
					lblDOWFE_1.setText(listMonThi.get(2).getNgayThu());
					lblDayFE_1.setText(" - "+listMonThi.get(2).getNgayThi());
					lblShiftFE_1.setText(listMonThi.get(2).getThoiGian());
					lblCourseNameFE_1.setText(listMonThi.get(2).getTenMonHoc());
					lblShiftNumFE_1.setText(listMonThi.get(2).getTietThi()+", ");
					lblToThiFE_Rep_1.setText(listMonThi.get(2).getToThi()+", ");
					lblGhepThiFE_Rep_1.setText(listMonThi.get(2).getGhepThi()+", ");
					lblSoLuongFE_Rep_1.setText(listMonThi.get(2).getSoLuong());
					lblPlaceFE_1.setText(listMonThi.get(2).getPhongThi());
					
					lblDOWFE_2.setText(listMonThi.get(3).getNgayThu());
					lblDayFE_2.setText(" - "+listMonThi.get(3).getNgayThi());
					lblShiftFE_2.setText(listMonThi.get(3).getThoiGian());
					lblCourseNameFE_2.setText(listMonThi.get(3).getTenMonHoc());
					lblShiftNumFE_2.setText(listMonThi.get(3).getTietThi()+", ");
					lblToThiFE_Rep_2.setText(listMonThi.get(3).getToThi()+", ");
					lblGhepThiFE_Rep_2.setText(listMonThi.get(3).getGhepThi()+", ");
					lblSoLuongFE_Rep_2.setText(listMonThi.get(3).getSoLuong());
					lblPlaceFE_2.setText(listMonThi.get(3).getPhongThi());
					
					lblDOWFE_3.setText(listMonThi.get(4).getNgayThu());
					lblDayFE_3.setText(" - "+listMonThi.get(4).getNgayThi());
					lblShiftFE_3.setText(listMonThi.get(4).getThoiGian());
					lblCourseNameFE_3.setText(listMonThi.get(4).getTenMonHoc());
					lblShiftNumFE_3.setText(listMonThi.get(4).getTietThi()+", ");
					lblToThiFE_Rep_3.setText(listMonThi.get(4).getToThi()+", ");
					lblGhepThiFE_Rep_3.setText(listMonThi.get(4).getGhepThi()+", ");
					lblSoLuongFE_Rep_3.setText(listMonThi.get(4).getSoLuong());
					lblPlaceFE_3.setText(listMonThi.get(4).getPhongThi());
					
					lblDOWFE_4.setText(listMonThi.get(5).getNgayThu());
					lblDayFE_4.setText(" - "+listMonThi.get(5).getNgayThi());
					lblShiftFE_4.setText(listMonThi.get(5).getThoiGian());
					lblCourseNameFE_4.setText(listMonThi.get(5).getTenMonHoc());
					lblShiftNumFE_4.setText(listMonThi.get(5).getTietThi()+", ");
					lblToThiFE_Rep_4.setText(listMonThi.get(5).getToThi()+", ");
					lblGhepThiFE_Rep_4.setText(listMonThi.get(5).getGhepThi()+", ");
					lblSoLuongFE_Rep_4.setText(listMonThi.get(5).getSoLuong());
					lblPlaceFE_4.setText(listMonThi.get(5).getPhongThi());
					
					lblDOWFE_5.setText(listMonThi.get(6).getNgayThu());
					lblDayFE_5.setText(" - "+listMonThi.get(6).getNgayThi());
					lblShiftFE_5.setText(listMonThi.get(6).getThoiGian());
					lblCourseNameFE_5.setText(listMonThi.get(6).getTenMonHoc());
					lblShiftNumFE_5.setText(listMonThi.get(6).getTietThi()+", ");
					lblToThiFE_Rep_5.setText(listMonThi.get(6).getToThi()+", ");
					lblGhepThiFE_Rep_5.setText(listMonThi.get(6).getGhepThi()+", ");
					lblSoLuongFE_Rep_5.setText(listMonThi.get(6).getSoLuong());
					lblPlaceFE_5.setText(listMonThi.get(6).getPhongThi());
					
					lblDOWFE_6.setText(listMonThi.get(7).getNgayThu());
					lblDayFE_6.setText(" - "+listMonThi.get(7).getNgayThi());
					lblShiftFE_6.setText(listMonThi.get(7).getThoiGian());
					lblCourseNameFE_6.setText(listMonThi.get(7).getTenMonHoc());
					lblShiftNumFE_6.setText(listMonThi.get(7).getTietThi()+", ");
					lblToThiFE_Rep_6.setText(listMonThi.get(7).getToThi()+", ");
					lblGhepThiFE_Rep_6.setText(listMonThi.get(7).getGhepThi()+", ");
					lblSoLuongFE_Rep_6.setText(listMonThi.get(7).getSoLuong());
					lblPlaceFE_6.setText(listMonThi.get(7).getPhongThi());
					
					lblDOWFE_7.setText(listMonThi.get(8).getNgayThu());
					lblDayFE_7.setText(" - "+listMonThi.get(8).getNgayThi());
					lblShiftFE_7.setText(listMonThi.get(8).getThoiGian());
					lblCourseNameFE_7.setText(listMonThi.get(8).getTenMonHoc());
					lblShiftNumFE_7.setText(listMonThi.get(8).getTietThi()+", ");
					lblToThiFE_Rep_7.setText(listMonThi.get(8).getToThi()+", ");
					lblGhepThiFE_Rep_7.setText(listMonThi.get(8).getGhepThi()+", ");
					lblSoLuongFE_Rep_7.setText(listMonThi.get(8).getSoLuong());
					lblPlaceFE_7.setText(listMonThi.get(8).getPhongThi());
					
					lblDOWFE_8.setText(listMonThi.get(9).getNgayThu());
					lblDayFE_8.setText(" - "+listMonThi.get(9).getNgayThi());
					lblShiftFE_8.setText(listMonThi.get(9).getThoiGian());
					lblCourseNameFE_8.setText(listMonThi.get(9).getTenMonHoc());
					lblShiftNumFE_8.setText(listMonThi.get(9).getTietThi()+", ");
					lblToThiFE_Rep_8.setText(listMonThi.get(9).getToThi()+", ");
					lblGhepThiFE_Rep_8.setText(listMonThi.get(9).getGhepThi()+", ");
					lblSoLuongFE_Rep_8.setText(listMonThi.get(9).getSoLuong());
					lblPlaceFE_8.setText(listMonThi.get(9).getPhongThi());
					
					lblDOWFE_9.setText(listMonThi.get(10).getNgayThu());
					lblDayFE_9.setText(" - "+listMonThi.get(10).getNgayThi());
					lblShiftFE_9.setText(listMonThi.get(10).getThoiGian());
					lblCourseNameFE_9.setText(listMonThi.get(10).getTenMonHoc());
					lblShiftNumFE_9.setText(listMonThi.get(10).getTietThi()+", ");
					lblToThiFE_Rep_9.setText(listMonThi.get(10).getToThi()+", ");
					lblGhepThiFE_Rep_9.setText(listMonThi.get(10).getGhepThi()+", ");
					lblSoLuongFE_Rep_9.setText(listMonThi.get(10).getSoLuong());
					lblPlaceFE_9.setText(listMonThi.get(10).getPhongThi());
					
					lblDOWFE_10.setText(listMonThi.get(11).getNgayThu());
					lblDayFE_10.setText(" - "+listMonThi.get(11).getNgayThi());
					lblShiftFE_10.setText(listMonThi.get(11).getThoiGian());
					lblCourseNameFE_10.setText(listMonThi.get(11).getTenMonHoc());
					lblShiftNumFE_10.setText(listMonThi.get(11).getTietThi()+", ");
					lblToThiFE_Rep_10.setText(listMonThi.get(11).getToThi()+", ");
					lblGhepThiFE_Rep_10.setText(listMonThi.get(11).getGhepThi()+", ");
					lblSoLuongFE_Rep_10.setText(listMonThi.get(11).getSoLuong());
					lblPlaceFE_10.setText(listMonThi.get(11).getPhongThi());
					
					lblDOWFE_11.setText(listMonThi.get(12).getNgayThu());
					lblDayFE_11.setText(" - "+listMonThi.get(12).getNgayThi());
					lblShiftFE_11.setText(listMonThi.get(12).getThoiGian());
					lblCourseNameFE_11.setText(listMonThi.get(12).getTenMonHoc());
					lblShiftNumFE_11.setText(listMonThi.get(12).getTietThi()+", ");
					lblToThiFE_Rep_11.setText(listMonThi.get(12).getToThi()+", ");
					lblGhepThiFE_Rep_11.setText(listMonThi.get(12).getGhepThi()+", ");
					lblSoLuongFE_Rep_11.setText(listMonThi.get(12).getSoLuong());
					lblPlaceFE_11.setText(listMonThi.get(12).getPhongThi());
					
					lblDOWFE_12.setText(listMonThi.get(13).getNgayThu());
					lblDayFE_12.setText(" - "+listMonThi.get(13).getNgayThi());
					lblShiftFE_12.setText(listMonThi.get(13).getThoiGian());
					lblCourseNameFE_12.setText(listMonThi.get(13).getTenMonHoc());
					lblShiftNumFE_12.setText(listMonThi.get(13).getTietThi()+", ");
					lblToThiFE_Rep_12.setText(listMonThi.get(13).getToThi()+", ");
					lblGhepThiFE_Rep_12.setText(listMonThi.get(13).getGhepThi()+", ");
					lblSoLuongFE_Rep_12.setText(listMonThi.get(13).getSoLuong());
					lblPlaceFE_12.setText(listMonThi.get(13).getPhongThi());
					
					lblDOWFE_13.setText(listMonThi.get(14).getNgayThu());
					lblDayFE_13.setText(" - "+listMonThi.get(14).getNgayThi());
					lblShiftFE_13.setText(listMonThi.get(14).getThoiGian());
					lblCourseNameFE_13.setText(listMonThi.get(14).getTenMonHoc());
					lblShiftNumFE_13.setText(listMonThi.get(14).getTietThi()+", ");
					lblToThiFE_Rep_13.setText(listMonThi.get(14).getToThi()+", ");
					lblGhepThiFE_Rep_13.setText(listMonThi.get(14).getGhepThi()+", ");
					lblSoLuongFE_Rep_13.setText(listMonThi.get(14).getSoLuong());
					lblPlaceFE_13.setText(listMonThi.get(14).getPhongThi());
					
					vBoxMainTabFE = new VBox(vboxInputCodeES, hboxButtonToggle, hboxLabelFirstFE, hboxMainSubFE, endLineBorderFE,
							hboxLabelFirstFE_0, hboxMainSubFE_0, endLineBorderFE_0, hboxLabelFirstFE_1, hboxMainSubFE_1,
							endLineBorderFE_1, hboxLabelFirstFE_2, hboxMainSubFE_2, endLineBorderFE_2
							, hboxLabelFirstFE_3, hboxMainSubFE_3, endLineBorderFE_3
							, hboxLabelFirstFE_4, hboxMainSubFE_4, endLineBorderFE_4
							, hboxLabelFirstFE_5, hboxMainSubFE_5, endLineBorderFE_5
							, hboxLabelFirstFE_6, hboxMainSubFE_6, endLineBorderFE_6
							, hboxLabelFirstFE_7, hboxMainSubFE_7, endLineBorderFE_7
							, hboxLabelFirstFE_8, hboxMainSubFE_8, endLineBorderFE_8
							, hboxLabelFirstFE_9, hboxMainSubFE_9, endLineBorderFE_9
							, hboxLabelFirstFE_10, hboxMainSubFE_10, endLineBorderFE_10
							, hboxLabelFirstFE_11, hboxMainSubFE_11, endLineBorderFE_11
							, hboxLabelFirstFE_12, hboxMainSubFE_12, endLineBorderFE_12
							, hboxLabelFirstFE_13, hboxMainSubFE_13, endLineBorderFE_13);
				
				}
				// end Loop 15

				// Final Exam ScrollPane
				scrollPaneFinalExam = new ScrollPane(vBoxMainTabFE);
				
				// set position
				scrollPaneFinalExam.setVvalue(0.35);
				
				tabPane = new JFXTabPane();

				Tab tab = new Tab();
				tab.setText(TAB_0);
				
				scrollPaneMain.setPadding(new Insets(0, 0, 0, 8.5));
				tab.setContent(scrollPaneMain);

				tabPane.getTabs().add(tab);

				Tab tab1 = new Tab();
				tab1.setText(TAB_1);
				tab1.setContent(scrollPaneFinalExam);

				if (STUDENT_NOTIFY == true) {
					tabPane.getTabs().add(tab1);
				}

				tabPane.setTabMinWidth(160);
//				tabPane.setPadding(new Insets(0, 0, 0, 0));

				SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
				selectionModel.select(0);

//				tabPane.setMaxSize(600, 1200);
//				tabPane.setMinWidth(1500);
//				tabPane.setMaxWidth(2000);
//				tabPane.setMinHeight(800);
//				tabPane.setMaxHeight(800);

				// Check Student or Lecturer

				vBoxMain.getChildren().addAll(
//							rootGroupTabPane,
						tabPane);

				// handle Schedule Icon and VNUA_Logo Icon
				scheduleIcon = new Image(getClass().getResourceAsStream("images/ic_schedule_96.png"));
				scheduleImView = new ImageView(scheduleIcon);
				scheduleImView.setFitWidth(48);
				scheduleImView.setFitHeight(48);

				vnuaLogoIcon = new Image(getClass().getResourceAsStream("images/ic_logo_vnua_960.png"));
				vnuaLogoImView = new ImageView(vnuaLogoIcon);
				vnuaLogoImView.setTranslateX(5);
				vnuaLogoImView.setFitWidth(40);
				vnuaLogoImView.setFitHeight(40);

				// handle Menu and Menu Items
				Image updateIcon = new Image(getClass().getResourceAsStream("images/ic_update_100_0.png")); // _0, _1
				updateImView = new ImageView(updateIcon);
				updateImView.setFitWidth(20);
				updateImView.setFitHeight(20);

				Image fExamIcon = new Image(getClass().getResourceAsStream("images/ic_schedule_100_0.png")); // 144,
																												// 250_0
				fExamImView = new ImageView(fExamIcon);
				fExamImView.setFitWidth(20);
				fExamImView.setFitHeight(20);

				Image aboutUsIcon = new Image(getClass().getResourceAsStream("images/ic_about_100_1.png")); // _1
				aboutUsImView = new ImageView(aboutUsIcon);
				aboutUsImView.setFitWidth(20);
				aboutUsImView.setFitHeight(20);

				menuMain = new javafx.scene.control.Menu();
				updateItem = new MenuItem(" Cập nhật lịch hiện tại");
				updateItem.setStyle("-fx-font-size: 18px;\r\n" + "\r\n" + " " + "\r\n-fx-text-fill: #000");
				updateItem.setGraphic(updateImView);
				finalExamViewItem = new MenuItem(" Xem lịch thi CK");
				finalExamViewItem.setStyle("-fx-font-size: 18px;\r\n" + "\r\n" + "  " + "\r\n-fx-text-fill: #000");
				finalExamViewItem.setGraphic(fExamImView);
				aboutUsItem = new MenuItem(" Thông tin ứng dụng");
				aboutUsItem.setStyle("-fx-font-size: 18px;\r\n" + "\r\n" + "  " + "\r\n-fx-text-fill: #000");
				aboutUsItem.setGraphic(aboutUsImView);

				// all Menu Item catch on Action Event
				updateItem.setOnAction(actionUpdate -> {
					// check if already updated -> print Dialog message "Update schedules
					// successfully!"

					// else -> print Dialog message "Current schedule has been up to date"
					UpdateConfirmDialog updateConDialog = new UpdateConfirmDialog("Lịch hiện tại không có thay đổi!");
					updateConDialog.showAndWait();
				});

				finalExamViewItem.setOnAction(actionSES -> {
					MobileApplication.getInstance().switchView(FINAL_EXAM_VIEW);
				});

				aboutUsItem.setOnAction(actionSAU -> {
					MobileApplication.getInstance().switchView(ABOUT_US_VIEW);
				});

				View view = new View(vBoxMain) {
					@Override
					protected void updateAppBar(AppBar appBar) {
						appBar.setTitleText("VNUA Scheduler");
						appBar.setStyle("-fx-background-color: #028D45;");
						appBar.getTitle().setStyle("-fx-font-size: 22px;\r\n" + "-fx-font-family: 'Scada';\r\n"
								+ "-fx-text-fill: #FEF701;\r\n");
						appBar.getTitle().setTranslateX(40);
						appBar.setNavIcon(vnuaLogoImView);
						appBar.getNavIcon().setTranslateX(5);
						appBar.getMenuItems().addAll(updateItem
//								, finalExamViewItem
						, aboutUsItem);
//					appBar.getActionItems().addAll(scheduleImView);
//					appBar.getActionItems().get(0).setTranslateX(-150);
//							appBar.getActionItems().addAll(MaterialDesignIcon.EXPAND_MORE.button(event -> {
//								ToastMessage toast = new ToastMessage("OK Done", 3000);
//								toast.showToast();
//							}));

					}
				};

				fab.showOn(view);

				// create a task
				Task taskS = new Task<Void>() {
					@Override
					public void run() {
						int max = 1000;
						for (int i = 1; i <= max; i++) {
							updateProgress(i, max);
						}
					}

					@Override
					protected Void call() throws Exception {
						// TODO Auto-generated method stub
						return null;
					}
				};

				btnSubmit.setOnAction(evv -> {

					// check whether or not valid input student / lecturer code
					String inputStuAndLecCode = tfStuAndLecCode.getText().trim().toLowerCase().toString();
					if (inputStuAndLecCode != null && !inputStuAndLecCode.equals("")) {
						// change the input code in the menu
						menu.setInputCode(inputStuAndLecCode);

						// update UI via show the Progress Indicator and run the menu method again
						lid = new LoadingIndicatorDialog(null);

						lid.addTaskEndNotification(result -> {
							System.out.println(result);
							lid = null; // don't keep the object, cleanup
						});

						lid.exec("123", inputParam -> {
							// load ABOUT_US_VIEW
							MobileApplication.getInstance().switchView(ABOUT_US_VIEW);

							// NO ACCESS TO UI ELEMENTS!
							for (int i = 0; i < 5; i++) {
								System.out.println("Loading data... '123' =->" + inputParam);
								try {
									Thread.sleep(200);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
							return new Integer(1);
						});

						// load MAIN_VIEW again
						MobileApplication.getInstance().switchView(MAIN_VIEW);
					} else {
						// show message via Dialog notify that wrong input information and force to
						// retype
						Toast toastMess = new Toast("Not Loading", Toast.LENGTH_LONG);
//					toastMess.show();
					}

				});

				return view;
			}
			return null;
		});

		// Splash Screen -> Splash View
		addViewFactory(MobileApplication.SPLASH_VIEW, () -> {
			Label lblMain = new Label("VNUA Scheduler");
			lblMain.setStyle("-fx-text-fill: #000 ;\r\n" + "-fx-font-family: 'Cabin';\r\n" + "-fx-font-size: 20px;");
			scheduleIcon = new Image(getClass().getResourceAsStream("images/ic_schedule_96.png"));
			ImageView scheduleImViewZero = new ImageView(scheduleIcon);
			scheduleImViewZero.setFitHeight(64);
			scheduleImViewZero.setFitWidth(64);
			HBox hboxMainM = new HBox(5, scheduleImViewZero, lblMain);
			hboxMainM.setAlignment(Pos.CENTER);
			SplashView splashView = new SplashView(PER_REQUEST_VIEW, hboxMainM);
			splashView.setOnShown(e -> {
				PauseTransition pauseOne = new PauseTransition(Duration.seconds(3.5)),
						pauseTwo = new PauseTransition(Duration.seconds(3.5)),
						pauseThree = new PauseTransition(Duration.seconds(3.5));
				pauseTwo.setOnFinished(ev -> {
					// change color of background
					splashView.setStyle("-fx-background-color: #018D44;");

					// change upper color (?)

					// hide the hboxMainM and create a new different Welcome label
//					scheduleImViewZero.setVisible(false);
					scheduleImViewZero.setFitWidth(0.00005);
					scheduleImViewZero.setFitHeight(0.00005);
					lblMain.setText("Chào mừng đến với VNUA Scheduler");
					lblMain.setStyle("-fx-text-fill: #fff ;\r\n" + "-fx-font-family: 'Grandstander';\r\n"
							+ "-fx-font-size: 20px;");

					pauseThree.play();
				});

				pauseThree.setOnFinished(ev -> {
					// close and hide the splash view
					splashView.hideSplashView();
				});

				pauseOne.setOnFinished(ev -> {

					// show another transition
					pauseTwo.play();
				});
				pauseOne.play();
			});
			return splashView;
		});

	}

	protected String checkUpdateServer() {
		menu = new Menu();
		menu.setInputCode("CNP02");

		try {

			String resultLoadMenu = menu.handleJsoupAndJSON();
			if (resultLoadMenu.equals("WRONG_INPUT_CODE")) {
				LOGGER.log(Level.INFO, "Damn it ! Update Server Currently!");

				// flag UPDATE_SERVER_CONFIRM
				UPDATE_SERVER_CONFIRM = true;

				return "UPDATE_SERVER";
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			if (exception instanceof IndexOutOfBoundsException) {

			} else if (exception instanceof IOException) {

			}
		}
		return "";
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private boolean loadingAndCheckConnectionAgain() {
		boolean res = false;
		lid = new LoadingIndicatorDialog(null);

		lid.addTaskEndNotification(result -> {
//			System.out.println(result);
			lid = null; // don't keep the object, cleanup
		});

		lid.exec("123", inputParam -> {

			// Check Internet Connection

//			Process processCheckInCo;
//			try {
//				processCheckInCo = Runtime.getRuntime().exec("ping http://daotao.vnua.edu.vn");
//				int x = processCheckInCo.waitFor();
//				Thread.currentThread().sleep(3000);
//				if (x == 0) {
//					// Has Internet Connection
//					LOGGER.log(Level.INFO, "Good Internet Connection.");
//					NO_INTERNET_CON = false;
//
//				} else {
//
//					// No Internet Connection
//					LOGGER.log(Level.INFO, "No Internet Connection.");
//					NO_INTERNET_CON = true;
//				}
//
//			} catch (IOException | InterruptedException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}

			// Gluon Services

			Services.get(ConnectivityService.class).ifPresent(service -> {
				boolean connected = service.isConnected();
				if (connected == true) {
					NO_INTERNET_CON = false;
					LOGGER.log(Level.INFO, "Good Internet Connection.");
				} else {
					NO_INTERNET_CON = true;
					LOGGER.log(Level.INFO, "No Internet Connection.");
				}
			});

			try {

				resultLoadMenu = menu.handleJsoupAndJSON();
				// check current Connectivity
				if (resultLoadMenu.equals("NO_INTERNET_CONNECTION")) {

					LOGGER.log(Level.INFO, "No Internet Connection.");

					// flag NO_INTERNET_CON
					NO_INTERNET_CON = true;

				} else {
					LOGGER.log(Level.INFO, "Good Internet Connection.");

					// flag NO_INTERNET_CON
					NO_INTERNET_CON = false;
				}
			} catch (Exception e) {

				LOGGER.log(Level.INFO, "Good Internet Connection.");

				// flag NO_INTERNET_CON
				NO_INTERNET_CON = false;
			}

			// NO ACCESS TO UI ELEMENTS!
			for (int i = 0; i < 5; i++) {
				try {
					Thread.sleep(200);
					if (NO_INTERNET_CON == false) {
						// Check Valid INPUT_CODE_RESTORE

						// If Valid =>
						// flag GO_TO_MAIN_VIEW
						GO_TO_MAIN_VIEW = true;
						LOGGER.log(Level.INFO, "Go to MAIN_VIEW.");
					} else {
						// do nothing
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return new Integer(1);
		});
		// notify user via toast up the message
		return res;
	}

	private void setOnPressedAndHolded(Node node, Duration seconds,
			EventHandler<javafx.scene.input.MouseEvent> handler) {

		class Wrapper<T> {
			T content;
		}
		Wrapper<javafx.scene.input.MouseEvent> eventWrapper = new Wrapper<>();

		PauseTransition holdTimer = new PauseTransition(seconds);
		holdTimer.setOnFinished(ev -> handler.handle(eventWrapper.content));

		node.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_PRESSED, event -> {
			eventWrapper.content = event;
			holdTimer.playFromStart();
		});
		node.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_RELEASED, event -> holdTimer.stop());
		node.addEventHandler(javafx.scene.input.MouseEvent.DRAG_DETECTED, event -> holdTimer.stop());
	}

	private void setUpMainView() {

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
//		try {
//			processCheckInCo = Runtime.getRuntime().exec("ping http://daotao.vnua.edu.vn");
//			int x = processCheckInCo.waitFor();
//			Thread.currentThread().sleep(3000);
//			if (x == 0) {
//				// Has Internet Connection
//				LOGGER.log(Level.INFO, "Good Internet Connection.");
////				dialogNoInCon.showAndWait();
//			} else {
//				// No Internet Connection
////				dialogNoInCon.showAndWait();
//
//				LOGGER.log(Level.INFO, "No Internet Connection.");
//			}
//
//		} catch (IOException | InterruptedException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}

		// Gluon Services
		Services.get(ConnectivityService.class).ifPresent(service -> {
			boolean connected = service.isConnected();
			if (connected == true) {
				NO_INTERNET_CON = false;
				LOGGER.log(Level.INFO, "Good Internet Connection.");
			} else {
				NO_INTERNET_CON = true;
				LOGGER.log(Level.INFO, "No Internet Connection.");
			}
		});

		// init menu
		menu = new Menu();

		// check whether or not Server Daotao.vnua.edu.vn UPDATE || BUSY || INSTALL NEW
		// VERSION

		try {
			menu.resetList();
			resultLoadMenu = menu.handleJsoupAndJSON();
			resultLoadMenuFE = menu.handleJsoupAndJSONFinalExam();
			isServerOK = true;

			if (resultLoadMenu.equals("WRONG_INPUT_CODE")) {
				LOGGER.log(Level.INFO, "Wrong credentials. ---> Wrong input code.");

				// flag WRONG_INPUT_CODE
				WRONG_INPUT_CODE = true;
			}

		} catch (Exception e) {

			if (e instanceof IndexOutOfBoundsException) {
				e.printStackTrace();

//				LOGGER.log(Level.INFO, "Wrong credentials. ---> Wrong input code.");
//
//				// flag WRONG_INPUT_CODE
//				WRONG_INPUT_CODE = true;

//				LOGGER.log(Level.INFO, "Server update recently. ---> Wait a minute.");
//
//				// flag UPDATE_SERVER_CONFIRM
//				UPDATE_SERVER_CONFIRM = true;

			} else if (e instanceof IOException) {
				e.printStackTrace();

				LOGGER.log(Level.INFO, "No Internet Connection.");

				// flag NO_INTERNET_CON
				NO_INTERNET_CON = true;
			}
		}

//		isServerOK = menu.checkServerUpdateOrNot();

		// Confirm Code
		try {
			menu.resetList();
			resultLoadMenu = menu.handleJsoupAndJSON();
			resultLoadMenuFE = menu.handleJsoupAndJSONFinalExam();
			isServerOK = true;

			// check resultLoadMenu
			if (resultLoadMenu.equals("NO_INTERNET_CONNECTION")) {

				LOGGER.log(Level.INFO, "No Internet Connection.");

				// flag NO_INTERNET_CON
				NO_INTERNET_CON = true;

			}

		} catch (Exception ex) {
			if (ex instanceof IndexOutOfBoundsException) {
				ex.printStackTrace();

				LOGGER.log(Level.INFO, "Wrong credentials. ---> Wrong input code.");

				// flag WRONG_INPUT_CODE
				WRONG_INPUT_CODE = true;

//				LOGGER.log(Level.INFO, "Server update recently. ---> Wait a minute.");
//
//				// flag UPDATE_SERVER_CONFIRM
//				UPDATE_SERVER_CONFIRM = true;
			} else if (ex instanceof IOException) {
				ex.printStackTrace();

				LOGGER.log(Level.INFO, "No Internet Connection.");

				// flag NO_INTERNET_CON
				NO_INTERNET_CON = true;
			}
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

	}

	@Override
	public void postInit(Scene scene) {

		Swatch.GREEN.assignTo(scene);
		scene.getStylesheets().add("http://fonts.googleapis.com/css?family=Scada");
		scene.getStylesheets().add("http://fonts.googleapis.com/css?family=Rubik");
		scene.getStylesheets().add("http://fonts.googleapis.com/css?family=Rubik:300");
		scene.getStylesheets().add("http://fonts.googleapis.com/css?family=Sen");
		scene.getStylesheets().add("http://fonts.googleapis.com/css?family=Alegreya+Sans");
		scene.getStylesheets().add("http://fonts.googleapis.com/css?family=Roboto");
		scene.getStylesheets().add("http://fonts.googleapis.com/css?family=Nunito");
		scene.getStylesheets().add("http://fonts.googleapis.com/css?family=Duru+Sans");
		scene.getStylesheets().add("http://fonts.googleapis.com/css?family=Copse");
		scene.getStylesheets().add("http://fonts.googleapis.com/css?family=Cutive");
		scene.getStylesheets().add("http://fonts.googleapis.com/css?family=Forum");

		// Vietnamese Font
		scene.getStylesheets().add("http://fonts.googleapis.com/css?family=Cabin"); // *
		scene.getStylesheets().add("http://fonts.googleapis.com/css?family=Merriweather+Sans");
		scene.getStylesheets().add("http://fonts.googleapis.com/css?family=Signika"); // *
		scene.getStylesheets().add("http://fonts.googleapis.com/css?family=Vollkorn"); // *
		scene.getStylesheets().add("http://fonts.googleapis.com/css?family=Mulish");
		scene.getStylesheets().add("http://fonts.googleapis.com/css?family=Public+Sans");
		scene.getStylesheets().add("http://fonts.googleapis.com/css?family=Markazi+Text");
		scene.getStylesheets().add("http://fonts.googleapis.com/css?family=Bitter");
		scene.getStylesheets().add("http://fonts.googleapis.com/css?family=Grandstander");
		scene.getStylesheets().add("http://fonts.googleapis.com/css?family=Libre+Franklin");
		scene.getStylesheets().add("http://fonts.googleapis.com/css?family=Roboto+Slab"); // *
		scene.getStylesheets().add("http://fonts.googleapis.com/css?family=Work+Sans"); // *
		scene.getStylesheets().add("http://fonts.googleapis.com/css?family=Quicksand");
		scene.getStylesheets().add("http://fonts.googleapis.com/css?family=Yanone+Kaffeesatz");
		scene.getStylesheets().add("http://fonts.googleapis.com/css?family=Dosis");
		scene.getStylesheets().add("http://fonts.googleapis.com/css?family=Arimo"); // *
		scene.getStylesheets().add("http://fonts.googleapis.com/css?family=Manrope");
		scene.getStylesheets().add("http://fonts.googleapis.com/css?family=Encode+Sans");
		scene.getStylesheets().add("http://fonts.googleapis.com/css?family=Oswald"); // Best
		scene.getStylesheets().add("http://fonts.googleapis.com/css?family=Ubuntu");
		scene.getStylesheets().add("http://fonts.googleapis.com/css?family=JetBrains+Mono");
		scene.getStylesheets().add("http://fonts.googleapis.com/css?family=Cuprum");

		if (JavaFXPlatform.isAndroid()) {

			AppBar appBarCurrent = MobileApplication.getInstance().getAppBar();
			scene.getStylesheets().add(Main.class.getResource("css/styleDesktopVer.css").toExternalForm());
			Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
			int screenWidth = (int) bounds.getWidth(), screenHeight = (int) bounds.getHeight();
			scene.getWindow().setWidth(screenWidth);
			scene.getWindow().setHeight(screenHeight);

//			Toast toastMess = new Toast("SW: " + screenWidth + ", SH: " + screenHeight + "Check: ", Toast.LENGTH_LONG);
//			toastMess.show();

			// handle layout position
			int halfScreenWidthPosition = screenWidth / 2,
//					titleAppBarWidth = (int) appBarCurrent.getTitle().getBoundsInParent().getWidth(),
					halfTotalWidthIAL = (253) / 2,
					startPointImagePosition = halfScreenWidthPosition - halfTotalWidthIAL,
					startPointTitlePosition = startPointImagePosition + 53,
					realImagePosition = (startPointTitlePosition - 50) + 17 - screenWidth,
					realTitlePosition = screenWidth + realImagePosition - 50 - 65;

//			toastMess = new Toast("Title: " + realTitlePosition + ", Icon: " + realImagePosition, Toast.LENGTH_SHORT);
//			toastMess.show();
//			appBarCurrent.getTitle().setTranslateX(realTitlePosition);
//			appBarCurrent.getActionItems().get(0).setTranslateX(realImagePosition);
			appBarCurrent.getNavIcon().setTranslateX(5);
//			toastMess = new Toast("Check: " + halfScreenWidthPosition + ", : " + realImagePosition,
//					Toast.LENGTH_LONG);
//			toastMess.show();

			// set Tab Pane width
			tabPane.setMinWidth(screenWidth - 50);
			hboxButtonToggle.setPrefWidth(screenWidth - 100);

			// set List View and V Box Main Sub and Scroll Pane Main
			scrollPaneMain.setMinWidth(screenWidth - 5);
			vBoxMainSub.setMinWidth(screenWidth - 5);
			scrollPaneMain.setMinHeight(screenHeight - 75);
			scrollPaneMain.setVvalue((screenHeight / 1000) + 1);

			// set textfield and submit button pref width
			tfStuAndLecCode.setMinWidth(175);
			rectangle.setWidth(225);
			hboxDeleteAllIcon.setPadding(new Insets(8, 10, 0, 215));
//			rectangle.setWidth(screenWidth-140);
//			paneSearchTextField.setMinWidth(screenWidth-140);
//			hboxSearchTextField.setMinWidth(screenWidth+60);
//			int totalSearchTFWidth = (int)btnSubmit.getWidth()+10+(int)paneSearchTextField.getWidth(),
//					halfMinusSearchTFWidthCal = (screenWidth+50-430)/2;
//			hboxSearchTextField.setPadding(new Insets(10, 10, 8, halfMinusSearchTFWidthCal));

			// set padding for hboxButtonGS
			hboxButtonGS.setPadding(new Insets(10, 0, 0, halfScreenWidthPosition));

			// handle List View Width
			listViewCoursesPresentDay.setMaxWidth(screenWidth + 50);
			listViewCoursesTheDayAT.setMaxWidth(screenWidth + 50);
			listViewCoursesTomorrow.setMaxWidth(screenWidth + 50);
			listViewCoursesTheDayAAT.setMaxWidth(screenWidth + 50);
			listViewCoursesTheDayAAAT.setMaxWidth(screenWidth + 50);
			listViewCoursesTheDayAAAAT.setMaxWidth(screenWidth + 50);
			listViewCoursesTheDayAAAAAT.setMaxWidth(screenWidth + 50);

		} else {
			// Desktop
			Toast toastMess = new Toast("Post Init Check Desktop", Toast.LENGTH_LONG);
			toastMess.show();
			scene.getWindow().setWidth(450); // 900
			scene.getWindow().setHeight(800); // 750
			scene.getStylesheets().add(Main.class.getResource("css/styleDesktopVer.css").toExternalForm());

			AppBar appBarCurrent = MobileApplication.getInstance().getAppBar();

//			appBarCurrent.getTitle().setTranslateX(65);
//			appBarCurrent.getActionItems().get(0).setTranslateX(-48);
//
//			appBarCurrent.getNavIcon().setTranslateX(10);
//			appBarCurent.getActionItems().get(0).setTranslateY(50);
			// set Icon
			Window window = scene.getWindow();
//			try {
//				((Stage) window).getIcons().add(new Image(getClass().getResourceAsStream("images/ic_schedule_32.png")));
//			} catch (Exception ex) {
//				ex.printStackTrace();
//			}

			// set Title
//			((Stage) window).setTitle("VNUA Scheduler Application - Help student to know the studying schedule Better");
		}

//		if (System.getProperty("javafx.platform") == null) {
//			PauseTransition pause = new PauseTransition(Duration.seconds(5));
//			pause.setOnFinished(f -> System.exit(0));
//			pause.play();
//		}
	}

	public static void main(String[] args) {
		System.setProperty("prism.verbose", "true");

		if (com.gluonhq.charm.down.Platform.isDesktop()) {
			System.setProperty("javafx.platform", "Desktop");
		}

		// define service for desktop
		StorageService storageService = new StorageService() {

			@Override
			public boolean isExternalStorageWritable() {
				// TODO Auto-generated method stub
				return getPrivateStorage().get().canWrite();
			}

			@Override
			public boolean isExternalStorageReadable() {
				// TODO Auto-generated method stub
				return getPrivateStorage().get().canRead();
			}

			@Override
			public Optional<File> getPublicStorage(String arg0) {
				// TODO Auto-generated method stub
				return getPrivateStorage();
			}

			@Override
			public Optional<File> getPrivateStorage() {
				// TODO Auto-generated method stub
				return Optional.of(new File(System.getProperty("user.home")));
			}
		};

		// define service factory for desktop
		ServiceFactory<StorageService> stoServiceFactory = new ServiceFactory<StorageService>() {

			@Override
			public Class<StorageService> getServiceType() {
				// TODO Auto-generated method stub
				return StorageService.class;
			}

			@Override
			public Optional<StorageService> getInstance() {
				// TODO Auto-generated method stub
				return Optional.of(storageService);
			}
		};

		// register service
//		Services.registerServiceFactory(stoServiceFactory);

		launch(args);
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

	private Timeline createBlinker(Node node) {
		Timeline blink = new Timeline(
				new KeyFrame(Duration.seconds(0), new KeyValue(node.opacityProperty(), 1, Interpolator.DISCRETE)),
				new KeyFrame(Duration.seconds(0.5), new KeyValue(node.opacityProperty(), 0, Interpolator.DISCRETE)),
				new KeyFrame(Duration.seconds(1), new KeyValue(node.opacityProperty(), 1, Interpolator.DISCRETE)));
		blink.setCycleCount(3);

		return blink;
	}

	private FadeTransition createFader(Node node) {
		FadeTransition fade = new FadeTransition(Duration.seconds(2), node);
		fade.setFromValue(1);
		fade.setToValue(0);

		return fade;
	}

	private FadeTransition fadeIn = new FadeTransition(Duration.millis(3000));

	private FadeTransition fadeOut = new FadeTransition(Duration.millis(3000));
}