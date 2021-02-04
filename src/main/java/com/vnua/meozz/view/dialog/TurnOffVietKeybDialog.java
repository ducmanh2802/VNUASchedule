package com.vnua.meozz.view.dialog;

import com.gluonhq.charm.glisten.control.Dialog;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class TurnOffVietKeybDialog extends Dialog {

	public TurnOffVietKeybDialog(String title) {
		setTitleText("Thông báo ");
		VBox content = new VBox();
		Label sessionTitleLabel = new Label(title);
		sessionTitleLabel.setStyle("-fx-font-family: 'Grandstander';");
		sessionTitleLabel.setWrapText(true);
		sessionTitleLabel.setMinHeight(40);
		sessionTitleLabel.setMaxHeight(80);
		StackPane stackPane = new StackPane();

		content.getChildren().addAll(sessionTitleLabel, stackPane);

		setContent(content);

		Button btnCancel = new Button("CANCEL");
		Button btnOK = new Button("OK");
		getButtons().addAll(btnCancel, btnOK);

		btnCancel.setOnAction(event ->{
			// do nothing
		});
		btnOK.setOnAction(event -> {
			hide();
		});

		content.getStyleClass().add("content");
		sessionTitleLabel.getStyleClass().add("session-title");
	}
}
