package com.vnua.meozz.model;

import java.util.function.Consumer;
import java.util.function.ToIntFunction;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class LoadingIndicatorDialog<P> {
	private Task animationWorker;
	private Task<Integer> taskWorker;

	private final ProgressIndicator progressIndicator = new ProgressIndicator(ProgressIndicator.INDETERMINATE_PROGRESS);
	private final Stage dialog = new Stage(StageStyle.TRANSPARENT);
	private final Label label = new Label();
	private final Group root = new Group();
	private Scene scene = new Scene(root, 330, 520, Color.TRANSPARENT);
	private final BorderPane mainPane = new BorderPane();
	private final VBox vbox = new VBox();

	/**
	 * Placing a listener on this list allows to get notified BY the result when the
	 * task has finished.
	 */
	public ObservableList<Integer> resultNotificationList = FXCollections.observableArrayList();

	public Integer resultValue;

	public LoadingIndicatorDialog(Window owner) {
//		if(minLengthScreen>0) {
//			scene = new Scene(root, minLengthScreen+150, 120, Color.TRANSPARENT);
//		}
		dialog.initModality(Modality.WINDOW_MODAL);
		dialog.initOwner(owner);
		dialog.setResizable(false);
		dialog.setOpacity(0.75);
		ColorAdjust adjust = new ColorAdjust();
		// Green HUE value
		adjust.setHue(-0.4);
//		progressIndicator.setEffect(adjust);
		progressIndicator.setStyle(" -fx-progress-color: #FF2424;"); // #3DB5A1, #17E7A1, #00F1B4, #FFEE1C, #FFDF1C, #FF0047
//		progressIndicator.setPadding(new Insets(30, 0, 0, 0));
	}

	/**
	 * 
	 */
	public void addTaskEndNotification(Consumer<Integer> c) {
		resultNotificationList.addListener((ListChangeListener<? super Integer>) n -> {
			resultNotificationList.clear();
			c.accept(resultValue);
		});
	}

	/**
	 *
	 */
	public void exec(P parameter, ToIntFunction func) {
		setupDialog();
		setupAnimationThread();
		setupWorkerThread(parameter, func);
	}

	/**
	 *
	 */
	private void setupDialog() {
//		mainPane.setStyle("-fx-background-color:#ADFF30");
		root.getChildren().add(mainPane);
		vbox.setSpacing(5);
		vbox.setAlignment(Pos.CENTER);
//		vbox.setStyle("-fx-background-color:#B3B3B3");
		vbox.setPadding(new Insets(100, 0, 0, 0));
//		if(minLengthScreen>0) {
//			vbox.setMinSize(minLengthScreen+150, 130);
//		} else {
			vbox.setMinSize(330, 520);
//		}
		vbox.getChildren().addAll(progressIndicator);
		mainPane.setTop(vbox);
		dialog.setScene(scene);

		dialog.setOnHiding(event -> {
			/*
			 * Gets notified when task ended, but BEFORE result value is attributed. Using
			 * the observable list above is recommended.
			 */ });

		dialog.show();
	}

	/**
	 *
	 */
	private void setupAnimationThread() {

		animationWorker = new Task() {
			@Override
			protected Object call() throws Exception {
				/*
				 * This is activated when we have a "progressing" indicator. This thread is used
				 * to advance progress every XXX milliseconds. In case of an
				 * INDETERMINATE_PROGRESS indicator, it's not of use. for (int i=1;i<=100;i++) {
				 * Thread.sleep(500); updateMessage(); updateProgress(i,100); }
				 */
				return true;
			}
		};

		progressIndicator.setProgress(0);
		progressIndicator.progressProperty().unbind();
		progressIndicator.progressProperty().bind(animationWorker.progressProperty());

		animationWorker.messageProperty().addListener((observable, oldValue, newValue) -> {
			// Do something when the animation value ticker has changed
		});

		new Thread(animationWorker).start();
	}

	/**
	 *
	 */
	private void setupWorkerThread(P parameter, ToIntFunction<P> func) {

		taskWorker = new Task<Integer>() {
			@Override
			public Integer call() {
				return func.applyAsInt(parameter);
			}
		};

		EventHandler<WorkerStateEvent> eh = event -> {
			animationWorker.cancel(true);
			progressIndicator.progressProperty().unbind();
			dialog.close();
			try {
				resultValue = taskWorker.get();
				resultNotificationList.add(resultValue);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		};

		taskWorker.setOnSucceeded(eh);
		taskWorker.setOnFailed(eh);

		new Thread(taskWorker).start();
	}

	/**
	 * For those that like beans :)
	 */
	public Integer getResultValue() {
		return resultValue;
	}

}
