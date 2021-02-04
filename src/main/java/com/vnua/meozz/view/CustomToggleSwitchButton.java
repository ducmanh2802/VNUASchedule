package com.vnua.meozz.view;

import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Parent;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class CustomToggleSwitchButton extends Parent{
	private BooleanProperty switchedOn = new SimpleBooleanProperty(false);

    private TranslateTransition translateAnimation = new TranslateTransition(Duration.seconds(0.25));
    private FillTransition fillAnimation = new FillTransition(Duration.seconds(0.25));

    private ParallelTransition animation = new ParallelTransition(translateAnimation, fillAnimation);

    public BooleanProperty switchedOnProperty() {
        return switchedOn;
    }

    public CustomToggleSwitchButton() {
    	
        Rectangle background = new Rectangle(60, 30);
        background.setArcWidth(30);
        background.setArcHeight(30);
        background.setFill(Color.rgb(255, 219, 92));
        background.setStroke(Color.rgb(134, 118, 102));
        
        Circle trigger = new Circle(15);
        trigger.setCenterX(15);
        trigger.setCenterY(15);
        trigger.setFill(Color.rgb(183, 108, 253));
        trigger.setStroke(Color.rgb(134, 118, 102));

        DropShadow shadow = new DropShadow();
        shadow.setRadius(1);
        trigger.setEffect(shadow);

        translateAnimation.setNode(trigger);
        fillAnimation.setShape(background);

        getChildren().addAll(background, trigger);
        
        switchedOn.addListener((obs, oldState, newState) -> {
            boolean isOn = newState.booleanValue();
            translateAnimation.setToX(isOn ? 60 - 30 : 0);
            fillAnimation.setFromValue(isOn ? Color.rgb(117, 213, 253) : Color.rgb(40, 207, 117));
            fillAnimation.setToValue(isOn ? Color.rgb(40, 207, 117) : Color.rgb(117, 213, 253));

            animation.play();
        });

        setOnMouseClicked(event -> {
            switchedOn.set(!switchedOn.get());
        });
    }
}
