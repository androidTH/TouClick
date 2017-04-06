package com.example.hoolai.touclick;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.hoolai.touclick.utils.CaptchaCTypeEnum;

import java.util.List;

/**
 * Created by hoolai on 17/3/29.
 * 弹框
 */

public class ImgTxtPopWidow extends PopupWindow {

    private Context mContext;
    public static int LEAST_POINTS = 2; //最小的点击次数
    private ImageView mImgClose, mImgRefresh, mImgTemplateLeft, mImgTemplateRight;
    private ProgressBar mProgressBar; //加载进度
    private Button mBtnSend;//验证按钮
    private View mImgTxtPopLayout;
    private ImgTxtLayout mImgTxtLayout;//图文布局
    private RequestEvent mRequestEvent;//请求回调
    private int CaptChaType = CaptchaCTypeEnum.CLICK.getId();

    public ImgTxtPopWidow(Context context) {
        this(context, null);
    }

    public ImgTxtPopWidow(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImgTxtPopWidow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * 初始化变量
     *
     * @param context
     */
    public void init(Context context) {
        this.mContext = context;
        mImgTxtPopLayout = LayoutInflater.from(context).inflate(R.layout.imgtxtpop_layout, null);
        mImgTxtPopLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        setContentView(mImgTxtPopLayout);
        setWidth(mImgTxtPopLayout.getMeasuredWidth());//设置宽
        setHeight(mImgTxtPopLayout.getMeasuredHeight());//设置高
        setBackgroundDrawable(new ColorDrawable());//设置默认背景
        setFocusable(true); //触摸外面是否关闭 true 关闭 false 不关闭
        setTouchable(true);
        setOutsideTouchable(false);
        mImgClose = (ImageView) mImgTxtPopLayout.findViewById(R.id.imageclose);
        mBtnSend = (Button) mImgTxtPopLayout.findViewById(R.id.btnsend);
        mImgRefresh = (ImageView) mImgTxtPopLayout.findViewById(R.id.imagefresh);
        mImgTxtLayout = (ImgTxtLayout) mImgTxtPopLayout.findViewById(R.id.eightimagelayout);
        mImgTemplateLeft = (ImageView) mImgTxtPopLayout.findViewById(R.id.imagetemplateleft);
        mImgTemplateRight = (ImageView) mImgTxtPopLayout.findViewById(R.id.imagetemplateright);
        mProgressBar = (ProgressBar) mImgTxtPopLayout.findViewById(R.id.progress);

        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRequestEvent != null && mImgTxtLayout.getmListPoints().size() > 0) {
                    StringBuffer location = new StringBuffer();
                    if (mImgTxtLayout.getmListPoints().size() >= LEAST_POINTS) {
                        if (CaptChaType == CaptchaCTypeEnum.CLICK.getId()) {
                            location.append(mImgTxtLayout.getmListPoints().get(mImgTxtLayout.getmListPoints().size() - LEAST_POINTS) + "," + mImgTxtLayout.getmListPoints().get(mImgTxtLayout.getmListPoints().size() - 1));
                        } else {
                            for (int i = 0; i < mImgTxtLayout.getmListPoints().size(); i++) {
                                location.append(mImgTxtLayout.getmListPoints().get(i)).append(",");
                            }
                            location.replace(location.length() - 1, location.length(), "");
                        }
                    } else {
                        location.append(mImgTxtLayout.getmListPoints().get(0));
                    }
                    //31,93,214,30
                    Log.i("location", "坐标 = " + location);
                    mRequestEvent.onSubmit(location.toString());
                }
            }
        });

        mImgRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRequestEvent != null) {
                    showProgressBar();
                    mRequestEvent.onRefresh();
                }
            }
        });

        mImgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePopup();
            }
        });

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                hidePopup();
            }
        });

    }

    public void setImgTxtBg(byte[] image, List<byte[]> simages) {
        mImgTxtLayout.setImgBg(image);
        Glide.with(mContext).load(simages.get(0)).asBitmap().diskCacheStrategy(DiskCacheStrategy.NONE).into(mImgTemplateLeft);
        Glide.with(mContext).load(simages.get(1)).asBitmap().diskCacheStrategy(DiskCacheStrategy.NONE).into(mImgTemplateRight);
    }

    public void setImgTxtBg(Bitmap bitmap) {
        mImgTxtLayout.setImgBg(bitmap);
    }

    public int getCaptChaType() {
        return CaptChaType;
    }

    public void setCaptChaType(int captChaType) {
        CaptChaType = captChaType;
    }

    public void clearPoints() {
        mImgTxtLayout.clearPoints();
    }

    /**
     * 验证成功
     */
    public void ok(){
        clearPoints();
        mImgTxtLayout.success();
        mImgTxtLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                hidePopup();
            }
        }, 600);
    }

    /**
     * 验证错误
     */
    public void error(){
        clearPoints();
        mImgTxtLayout.fail();
        mImgTxtLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mRequestEvent != null) {
                    showProgressBar();
                    mRequestEvent.onRefresh();
                }
            }
        }, 400);
    }

    /**
     * 显示progressBar
     */
    public void showProgressBar(){
        mImgTxtLayout.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏progressBar
     */
    public void hideProgressBar(){
        mImgTxtLayout.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }

    /**
     * 显示PopupWidow
     * @param parent
     */
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            showBackGroudAlpha(0.4f);
            int xOffset = parent.getWidth() / 2 - mImgTxtPopLayout.getMeasuredWidth() / 2;
            this.showAsDropDown(parent, xOffset, 10);
        } else {
            this.hidePopup();
        }
        clearPoints();
    }

    public void hidePopup(){
        backgroundAlpha(1f);
        this.dismiss();
    }

    /**
     * 显示遮罩
     *
     * @param bgAlpha
     */
    public void showBackGroudAlpha(float bgAlpha){
        WindowManager.LayoutParams lp = ((Activity)mContext).getWindow()
                .getAttributes();
        lp.alpha = bgAlpha;
        ((Activity)mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        ((Activity)mContext).getWindow().setAttributes(lp);
    }

    /**
     * 遮罩消失
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = ((Activity)mContext).getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        ((Activity)mContext).getWindow().setAttributes(lp);
    }

    public void setmRequestEvent(RequestEvent mRequestEvent) {
        this.mRequestEvent = mRequestEvent;
    }

    public interface RequestEvent {
         void onRefresh();

         void onSubmit(String loaction);
    }
}
