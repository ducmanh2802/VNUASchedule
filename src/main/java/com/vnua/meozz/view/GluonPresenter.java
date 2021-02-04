package com.vnua.meozz.view;

import com.gluonhq.charm.glisten.application.MobileApplication;

public abstract class GluonPresenter<T extends MobileApplication> {
	 
    private final T app = (T) MobileApplication.getInstance();
 
    protected T getApp() {
        return app;
    }
 
}
