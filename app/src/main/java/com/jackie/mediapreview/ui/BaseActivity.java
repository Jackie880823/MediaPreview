package com.jackie.mediapreview.ui;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jackie.mediapreview.R;
import com.jackie.mediapreview.interfaces.IViewCommon;

/**
 * activity基类
 *
 * @author wing
 */
public abstract class BaseActivity extends BaseFragmentActivity implements IViewCommon{

    private static final String TAG = BaseActivity.class.getSimpleName();

    protected ImageButton leftButton;            //头部栏的左边的按钮
    protected TextView tvTitle;                          //头部栏的标题
    protected ImageView title_icon;                          //头部栏的标题
    protected ImageButton rightButton;             //头部栏的右边按钮

    Fragment fragment;
    protected Bundle mSavedInstanceState;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // 这里会影响子类返回键的监听事件，请谨慎处理
        //        Log.i(TAG, "dispatchKeyEvent& keyCode: " + event.getKeyCode() + "; Action: " + event.getAction());
        if(event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        if(event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.dispatchKeyEvent(event); // 按下其他按钮，调用父类进行默认处理
    }

    protected Fragment getFragmentInstance() {
        return fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSavedInstanceState = savedInstanceState;
        // 打开Activity隐藏软键盘；
        //        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(getLayout());

        fragment = getFragment();
        if(fragment != null) {
            if(savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
            }
        }
        initBottomBar();//要先初始
        initView();
        initTitleBar();


        //        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
        //                .findViewById(android.R.id.content)).getChildAt(0);
        //        viewGroup.addView(setWaittingView());

    }

    public int getLayout() {
        return R.layout.activity_base;
    }

    /**
     * 初始底部栏，没有可以不操作
     */
    protected abstract void initBottomBar();

    /**
     * 设置title
     */
    protected abstract void setTitle();

    /**
     * TitilBar 左边事件
     */
    protected void titleLeftEvent() {
            finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * TitilBar 右边事件
     */
    protected abstract void titleRightEvent();

    protected void titleMiddleEvent() {
    }

    protected LinearLayout titleBar;
    //    protected RelativeLayout titleBar;

    protected void initTitleBar() {
        titleBar = getViewById(R.id.title_bar);
        if(currentColor != -1) {
            titleBar.setBackgroundColor(getResources().getColor(currentColor));
        }
        leftButton = getViewById(R.id.ib_top_button_left);
        tvTitle = getViewById(R.id.tv_title);
        title_icon = getViewById(R.id.title_icon);
        rightButton = getViewById(R.id.ib_top_button_right);
        //        rightTextButton = getViewById(R.id.ib_top_text_right);
        getViewById(R.id.tv_top_title).setOnClickListener(this);
        leftButton.setOnClickListener(this);
        rightButton.setOnClickListener(this);
        setTitle();

    }

    public <V extends View> V getViewById(int id) {
        return (V) findViewById(id);
    }

    protected static int currentColor = -1;

    protected void changeTitleColor(int color) {
        if(titleBar != null) {
            currentColor = color;
            titleBar.setBackgroundColor(getResources().getColor(color));
        }
    }

    /**
     * 返回当前Action Bar的颜色值
     *
     * @return - 颜色值
     */
    public int getActionBarColor() {
        if(currentColor==-1){
            currentColor = R.color.tab_color_press1;
        }
        return currentColor;
    }

    protected abstract Fragment getFragment();

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        //		FragmentManager fragmentManager = getSupportFragmentManager();
        //		Logger.i("test", fragmentManager.getBackStackEntryCount() + "");
        //		fragmentManager.popBackStack();
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            // 进行弹出窗口//
            case R.id.ib_top_button_left:
                titleLeftEvent();
                break;
            case R.id.tv_top_title:
                titleMiddleEvent();
                break;
            case R.id.ib_top_button_right:
                //            case R.id.ib_top_text_right:
                titleRightEvent();
                break;
            default:
                //                super.onClick(v);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
