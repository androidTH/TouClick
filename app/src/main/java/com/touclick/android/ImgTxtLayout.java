package com.touclick.android;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hoolai on 17/3/31.
 * 添加手指按下后的标记Tag
 */

public class ImgTxtLayout extends FrameLayout implements View.OnClickListener {

    private static String TAG = ImgTxtLayout.class.getSimpleName();
    private Context mContext;
    private FrameLayout mFrameLayoutPoints;//标记所在父布局
    private ImageView mImgBg;
    private List<String> mListPoints = new ArrayList<>();//tag的坐标("x,y")集合
    public static int offset;//手指按下后的偏移量

    public ImgTxtLayout(@NonNull Context context) {
        this(context, null);
    }

    public ImgTxtLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImgTxtLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * 初始化变量
     *
     * @param context
     */
    private void init(Context context) {
        this.mContext = context;
        offset = DispUtils.dip2px(mContext, 15);//dp2px
        View mPointLayout = inflate(mContext, R.layout.imgtxt_layout, this);
        mFrameLayoutPoints = (FrameLayout) mPointLayout.findViewById(R.id.framentpoints);
        mImgBg = (ImageView) mPointLayout.findViewById(R.id.imgbg);

        mFrameLayoutPoints.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float pX = event.getX();
                float pY = event.getY();
                setImgPoint((int) pX - offset, (int) pY - offset);
                return false;
            }
        });
    }

    public void setImgBg(Bitmap bitmap) {
        Glide.with(mContext).load(bitmap).asBitmap().diskCacheStrategy(DiskCacheStrategy.NONE).into(mImgBg);
    }

    public void setImgBg(byte[] image) {
        Glide.with(mContext).load(image).asBitmap().diskCacheStrategy(DiskCacheStrategy.NONE).into(mImgBg);
    }

    /**
     * 根据标记x,y坐标添加标记到FrameLayout上
     *
     * @param x
     * @param y
     */
    public void setImgPoint(int x, int y) {
        ImageView mImagePoint = (ImageView) LayoutInflater.from(mContext).inflate(R.layout.imgtag, this, false);
        LayoutParams params = (LayoutParams) mImagePoint.getLayoutParams();
        params.leftMargin = x;//x轴坐标
        params.topMargin = y;//y轴坐标
        mFrameLayoutPoints.addView(mImagePoint, params);//tag添加到FrameLayout中
        mFrameLayoutPoints.postInvalidate();
        Glide.with(mContext).load(R.mipmap.bubble_press).into(mImagePoint);
        mImagePoint.setOnClickListener(this);
        StringBuffer sbPoint = new StringBuffer(DispUtils.px2dip(mContext, x + offset) + "," + DispUtils.px2dip(mContext, y + offset));//片接
        mListPoints.add(sbPoint.toString());
    }

    /**
     * 点击事件
     * 点击到原来的tag需要移除这个点击的tag
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        StringBuffer sbPoint = new StringBuffer(DispUtils.px2dip(mContext, v.getLeft() + offset) + "," + DispUtils.px2dip(mContext, v.getTop() + offset));
        mListPoints.remove(sbPoint.toString());
        mFrameLayoutPoints.removeView(v);
        mFrameLayoutPoints.postInvalidate();

    }

    public List<String> getmListPoints() {
        return mListPoints;
    }

    /**
     * 清楚所有标记的tag
     */
    public void clearPoints() {
        mListPoints.clear();
        mFrameLayoutPoints.removeAllViews();
    }

    /**
     * 标记成功后的背景图片
     */
    public void success() {
        Glide.with(mContext).load(R.mipmap.ok).into(mImgBg);
    }

    /**
     * 标记失败后的背景图片
     */
    public void fail() {
        Glide.with(mContext).load(R.mipmap.error).into(mImgBg);
    }
}
