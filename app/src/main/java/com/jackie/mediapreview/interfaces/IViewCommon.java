package com.jackie.mediapreview.interfaces;

import android.view.View;

public interface IViewCommon {

    public void initView();
    public <V extends View> V getViewById(int id);

}
