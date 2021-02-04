package com.vnua.meozz.main;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.net.URL;

import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.application.ViewStackPolicy;
import com.gluonhq.charm.glisten.mvc.View;
import com.vnua.meozz.view.GetStartedPresenter;
import com.vnua.meozz.view.GetStartedView;
import com.vnua.meozz.view.GluonView;
import com.vnua.meozz.view.InforValidPresenter;
import com.vnua.meozz.view.InforValidView;
import com.vnua.meozz.view.MainPresenter;
import com.vnua.meozz.view.MainView;
import com.vnua.meozz.view.SplashPresenter;
import com.vnua.meozz.view.SplashView;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class VNUAScheduler extends MobileApplication {

	private static final String SPLASH_VIEW = "Splash";
	public static final String GET_STARTED_VIEW = "GetStarted";
	public static final String INFOR_VALID_VIEW = "InforValid";
	public static final String MAIN_VIEW = "Main";
	public static final String EXT_SEARCH_VIEW = "ExtendedSearch";

//	private SplashView splashView;
//	private MainView mainView;
//	private GetStartedView getStartedView;
//	private InforValidView inforValidView;
	private ViewStackPolicy defaultViewStackPolicy;
	
	@Override
	public void init() throws Exception {
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int width = gd.getDisplayMode().getWidth();
		int height = gd.getDisplayMode().getHeight();

		
//		addViewFactory(SPLASH_VIEW, () ->{
//			splashView = new SplashView(SplashPresenter.class);
//			splashView.getView().setName(SPLASH_VIEW);
//			
//            return splashView.getView();
//		});
		addViewFactory(MAIN_VIEW, () -> {
			GluonView mainView = new GluonView(MainPresenter.class);
//			mainView.getView().setName(MAIN_VIEW);
			return mainView.getView();
		});
		addViewFactory(GET_STARTED_VIEW, () -> {
			GluonView getStartedView = new GluonView(GetStartedPresenter.class);
			getStartedView.getView().setId(GET_STARTED_VIEW);
			return getStartedView.getView();
		});
		addViewFactory(INFOR_VALID_VIEW, () -> {
			GluonView inforValidView = new GluonView(InforValidPresenter.class);
			inforValidView.getView().setId(INFOR_VALID_VIEW);
			return inforValidView.getView();
		});
		
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				Platform.runLater(() -> switchView(MAIN_VIEW, defaultViewStackPolicy));
				return null;
			}
		};

		addViewFactory(MobileApplication.SPLASH_VIEW, () -> {
			com.gluonhq.charm.glisten.mvc.SplashView splashView = new com.gluonhq.charm.glisten.mvc.SplashView(
					new Label("This is a splash"));
			splashView.setOnShown(e -> {
				PauseTransition pause = new PauseTransition(Duration.seconds(3));
				pause.setOnFinished(ev -> {
					splashView.hideSplashView();
					new Thread(task).start();
				});
				pause.play();
			});
			return splashView;
		});
	}

}
