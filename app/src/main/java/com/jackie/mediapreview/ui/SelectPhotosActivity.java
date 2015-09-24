package com.jackie.mediapreview.ui;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;

import com.jackie.mediapreview.R;
import com.jackie.mediapreview.entity.MediaData;
import com.jackie.mediapreview.interfaces.SelectImageUirChangeListener;
import com.jackie.mediapreview.utils.MessageUtil;
import com.jackie.mediapreview.widget.DrawerArrowDrawable;

import java.util.ArrayList;


/**
 * 选择图片的Activity
 *
 * @author Jackie
 * @see BaseActivity
 */
public class SelectPhotosActivity extends BaseActivity {

    private static final String TAG = SelectPhotosActivity.class.getSimpleName();

    public static final String EXTRA_IMAGES_STR = "images";
    public static final String EXTRA_SELECTED_PHOTOS = "selected_photos";

    public final static String EXTRA_RESIDUE = "residue";

    //    public static final String EXTRA_SELECTED_PHOTOS = "selected_photos";
    /**
     * 限制最多可选图片张数
     */
    public final static int MAX_SELECT = 10;

    private SelectPhotosFragment fragment;
    private ArrayList<MediaData> mSelectedImages = new ArrayList<>();

    /**
     * 选择多张图片标识{@value true}可以多张选择图片，{@value false}只允许选择一张图
     */
    private boolean multi;
    /**
     * 请求图片Uir用Universal Image Loader库处理标识
     */
    private boolean useUniversal;

    /**
     * 请求数据
     */
    private boolean useVideo;
    /**
     * 请求多张图片数量
     */
    //    private int residue;
    /**
     * 当前是否为浏览状态标识位
     */
    private boolean isPreview = false;
    /**
     * 当前选择的媒体数据
     */
    private MediaData currentData;

    /**
     * 选择视频询问框，提示选择视频后将删除已经选择好的图片
     */
    private AlertDialog selectVideoDialog = null;

    private SelectImageUirChangeListener listener = new SelectImageUirChangeListener() {

        PreviewFragment previewFragment;

        /**
         * 添加图片{@code mediaData}到选择列表
         *
         * @param mediaData -   需要添加的图片uri数据
         * @return -   true:   添加成功；
         * -   false:  添加失败；
         */
        @Override
        public boolean addUri(MediaData mediaData) {
            // 添加结果成功与否的返回值，默认不成功
            boolean result = false;
            if (MediaData.TYPE_VIDEO.equals(mediaData.getType())) {
                Log.i(TAG, "addUri& uri: " + mediaData.getPath());
                alertAddVideo(mediaData);
            } else {
                Log.i(TAG, "addUri& uri path: " + mediaData.getPath());
                if (multi) {
                    //                    if(mSelectedImages.size() < residue) {
                    if (mSelectedImages.size() < MAX_SELECT) {
                        // 没有超过限制的图片数量可以继续添加并返回添加结果的返回值
                        result = mSelectedImages.contains(mediaData) || mSelectedImages.add(mediaData);
                    } else {
                        // 提示用户添加的图片超过限制的数量
                        MessageUtil.showMessage(SelectPhotosActivity.this, String.format(SelectPhotosActivity.this.getString(R.string.select_too_many), MAX_SELECT));
                    }
                } else {
                    // 不是同时添加多张图片，添加完成关闭当前Activity
                    Intent intent = new Intent();
                    intent.setType(MediaData.TYPE_IMAGE);
                    if (useUniversal) {
                        intent.setData(Uri.parse(mediaData.getPath()));
                    } else {
                        intent.setData(mediaData.getContentUri());
                    }
                    setResult(RESULT_OK, intent);
                    finish();
                    result = true;
                }
            }
            return result;
        }

        /**
         * 从列表中删除图片{@code mediaData}
         *
         * @param mediaData -   需要删除的图片uri数据
         * @return -   true:   删除成功；
         * -   false:  删除失败；
         */
        @Override
        public boolean removeUri(MediaData mediaData) {
            // 返回删除结果成功与否的值
            return !mSelectedImages.contains(mediaData) || mSelectedImages.remove(mediaData);
        }

        /**
         * 打开了左侧的目录列表并设置标题栏左侧图标为{@code drawable}
         *
         * @param drawable
         */
        @Override
        public void onDrawerOpened(Drawable drawable) {
            leftButton.setImageDrawable(drawable);
        }

        /**
         * 关闭了左侧的目录列表并设置标题栏左侧图标为{@code drawable}
         *
         * @param drawable
         */
        @Override
        public void onDrawerClose(Drawable drawable) {
            leftButton.setImageDrawable(drawable);
        }

        @Override
        public void preview(MediaData mediaData) {
            currentData = mediaData;
            Log.i(TAG, "preview& mediaData: " + mediaData.toString());
            //            Bitmap bitmap = LocalImageLoader.loadBitmapFromFile(getApplicationContext(), mediaData);
            if (previewFragment == null) {
                previewFragment = PreviewFragment.newInstance("");
            }
            changeFragment(previewFragment, true);
            previewFragment.displayImage(currentData);
            isPreview = true;
            leftButton.setImageResource(R.drawable.back_normal);
        }

    };

    /**
     * 初始底部栏，没有可以不操作
     */
    @Override
    protected void initBottomBar() {
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setImageResource(R.drawable.btn_done);
        leftButton.setImageResource(R.drawable.text_title_seletor);
    }

    /**
     * 设置title
     */
    @Override
    protected void setTitle() {
        if (useVideo) {
            tvTitle.setText(R.string.select_photos_or_video);
        } else {
            tvTitle.setText(R.string.title_select_photos);
        }
    }

    @Override
    protected void titleLeftEvent() {
        if (isPreview) {
            changeFragment(fragment, false);
            isPreview = false;
        } else {
            fragment.changeDrawer();
        }
    }

    /**
     * TitilBar 右边事件
     */
    @Override
    protected void titleRightEvent() {
        if (isPreview) {
            changeFragment(fragment, false);
            if (!mSelectedImages.contains(currentData)) {
                listener.addUri(currentData);
            }
            isPreview = false;
        } else {
            Intent intent = new Intent();
            ArrayList<Uri> uriList = new ArrayList<>();
            for (MediaData mediaData : mSelectedImages) {
                if (useUniversal) {
                    uriList.add(Uri.parse(mediaData.getPath()));
                } else {
                    uriList.add(mediaData.getContentUri());
                }
            }
            intent.putParcelableArrayListExtra(EXTRA_IMAGES_STR, uriList);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    protected Fragment getFragment() {
        fragment = SelectPhotosFragment.newInstance(mSelectedImages, "");
        return fragment;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void initView() {
        Intent intent = getIntent();
        // 是否为同时添加多张图片
        multi = intent.getBooleanExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        useUniversal = intent.getBooleanExtra(MediaData.EXTRA_USE_UNIVERSAL, false);
        useVideo = intent.getBooleanExtra(MediaData.USE_VIDEO_AVAILABLE, false);
        // 总共需要添加的图片数量
        //        residue = intent.getIntExtra(EXTRA_RESIDUE, 10);
        fragment.setSelectImageUirListener(listener);
        ArrayList<Uri> uris = intent.getParcelableArrayListExtra(EXTRA_SELECTED_PHOTOS);
        mSelectedImages.clear();
        if (uris != null) {
            for (Uri uri : uris) {
                MediaData mediaData = new MediaData(uri, uri.toString(), MediaData.TYPE_IMAGE, 0);
                mSelectedImages.add(mediaData);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i(TAG, "onKeyDown& " + keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isPreview) {
                changeFragment(fragment, false);
                isPreview = false;
                Log.i(TAG, "onKeyDown& back is false");
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            Log.i(TAG, "dispatchKeyEvent& back");
            if (isPreview) {
                Log.i(TAG, "dispatchKeyEvent& back is false");
                changeFragment(fragment, false);
                isPreview = false;
                return false;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void changeFragment(Fragment f, boolean init) {
        super.changeFragment(f, init);
        if (f instanceof SelectPhotosFragment) {
            Resources resources = getResources();
            DrawerArrowDrawable drawerArrowDrawable = new DrawerArrowDrawable(resources);
            drawerArrowDrawable.setStrokeColor(resources.getColor(R.color.drawer_arrow_color));
            leftButton.setImageDrawable(drawerArrowDrawable);
        }
    }

    @Override
    public boolean dispatchKeyShortcutEvent(@NonNull KeyEvent event) {
        return super.dispatchKeyShortcutEvent(event);
    }

    /**
     * 传入为{@link MediaData#TYPE_VIDEO}弹出提示，选择了{@link MediaData#TYPE_VIDEO}将会删除已经选择好的{@link MediaData#TYPE_IMAGE}
     *
     * @param mediaData {@link MediaData#TYPE_VIDEO}类型的媒体数据
     */
    public void alertAddVideo(final MediaData mediaData) {
        //        if(!mSelectedImages.isEmpty() || residue < MAX_SELECT) {
        if (!mSelectedImages.isEmpty()) {

            if (selectVideoDialog == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.will_remove_photos);
                // 确认要选择视频
                builder.setPositiveButton(R.string.text_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        selectVideoDialog.dismiss();
                        mSelectedImages.clear();
                        resultVideo(mediaData);
                    }
                });

                // 不选择视频
                builder.setNegativeButton(R.string.text_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectVideoDialog.dismiss();
                    }
                });
                selectVideoDialog = builder.create();
            }

            selectVideoDialog.show();
        } else {
            resultVideo(mediaData);
        }
    }

    /**
     * 确定返回{@link MediaData#TYPE_VIDEO}的媒体数据
     *
     * @param mediaData 类型为{@link MediaData#TYPE_VIDEO}的{@link MediaData}
     */
    private void resultVideo(MediaData mediaData) {
        Intent intent = new Intent();
        intent.putExtra(MediaData.EXTRA_MEDIA_TYPE, MediaData.TYPE_VIDEO);
        intent.putExtra(MediaData.EXTRA_VIDEO_DURATION, mediaData.getDuration());
        intent.setData(Uri.parse(mediaData.getPath()));
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
