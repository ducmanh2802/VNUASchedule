package com.vnua.meozz.view;

import com.gluonhq.charm.glisten.animation.FadeInTransition;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.LifecycleEvent;
import com.vnua.meozz.main.VNUAScheduler;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;

public class SplashPresenter extends GluonPresenter<VNUAScheduler> {
	
	@FXML private SplashView splash;

    public void initialize(){
//        splash.setShowTransitionFactory(FadeInTransition::new);
//        splash.setOnShown((LifecycleEvent e) -> {
//            PauseTransition pause = new PauseTransition(Duration.seconds(3));
//            pause.setOnFinished((ActionEvent f) -> {
//                splash.hideSplashView();
//            });
//            pause.play();
//        });
//        Task<Void> task = new Task<Void>() {
//
//            @Override
//            protected Void call() throws Exception {
//                Platform.runLater(() -> MobileApplication.getInstance().switchView("Main"));
//                return null;
//            }
//        };
//
//        splash.setOnShown(e -> {(LifecycleEvent.SHOWN, e -> {
//            PauseTransition pause = new PauseTransition(Duration.seconds(1));
//            pause.setOnFinished(f -> {
//                access.setVisible(true);
//                new Thread(task).start();
//            });
//            pause.play();
//        });
    }
}
