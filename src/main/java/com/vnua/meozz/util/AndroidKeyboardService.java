package com.vnua.meozz.util;

import com.gluonhq.charm.down.Services;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.LifecycleEvent;

import android.graphics.Rect;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class AndroidKeyboardService 
//implements KeyboardService 
{

//    private static final float       SCALE  = FXActivity.getInstance().getResources().getDisplayMetrics().density;

//    private final InputMethodManager imm;

    private Rect                     currentBounds;
    private DoubleProperty           visibleHeight;

    private OnGlobalLayoutListener   layoutListener;

    private boolean                  keyboardVisible;

//    public AndroidKeyboardService() {
//        imm = (InputMethodManager) FXActivity.getInstance().getSystemService(FXActivity.INPUT_METHOD_SERVICE);
//        initLayoutListener();
//    }

    private void initLayoutListener() {
        double screenHeight = MobileApplication.getInstance().getScreenHeight();
        currentBounds = new Rect();    
        visibleHeight = new SimpleDoubleProperty(screenHeight);
        visibleHeight.addListener((ov, n, n1) -> onHeightChanged(n, n1));


//        layoutListener = layoutListener(visibleHeight);

//        FXActivity.getViewGroup().getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
//        Services.get(LifecycleService.class).ifPresent(l ->
//        {
//            l.addListener(LifecycleEvent.RESUME, () -> FXActivity.getViewGroup().getViewTreeObserver().addOnGlobalLayoutListener(layoutListener));
//            l.addListener(LifecycleEvent.PAUSE, () -> FXActivity.getViewGroup().getViewTreeObserver().removeOnGlobalLayoutListener(layoutListener));
//       });
    }
//
//    private OnGlobalLayoutListener layoutListener(DoubleProperty height) {
//        return () -> height.set(getCurrentHeigt());
//    }

//    private float getCurrentHeigt() {
//        FXActivity.getViewGroup().getRootView().getWindowVisibleDisplayFrame(currentBounds);
//        return currentBounds.height() / SCALE;
//    }

    private void onHeightChanged(Number oldHeight, Number newHeight) {
        double heightDelta = newHeight.doubleValue() - oldHeight.doubleValue();
        keyboardVisible = heightDelta < 0;
    }

    public boolean isKeyboardVisible() {
        return keyboardVisible;
    }

    public void show() {
//        if (!keyboardVisible) {
//            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
//        }
    }

    public void hide() {
//        if (keyboardVisible) {
//            imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
//        }
    }
}
