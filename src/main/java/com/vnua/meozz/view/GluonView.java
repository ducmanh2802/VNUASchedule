package com.vnua.meozz.view;

import com.airhacks.afterburner.views.FXMLView;
import com.gluonhq.charm.glisten.mvc.View;

public class GluonView extends FXMLView {
    private final Class presenter;
 
    public GluonView(Class<? extends GluonPresenter<?>> presenter) {
        this.presenter = presenter;
    }
 
    @Override public View getView() {
        return (View) super.getView();
    }
}
