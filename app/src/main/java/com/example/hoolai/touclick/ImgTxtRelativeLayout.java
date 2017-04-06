package com.example.hoolai.touclick;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.hoolai.touclick.utils.Captcha;
import com.example.hoolai.touclick.utils.CaptchaCTypeEnum;
import com.example.hoolai.touclick.utils.CheckState;
import com.example.hoolai.touclick.utils.ICaptchaEvent;
import com.example.hoolai.touclick.utils.ICaptchaImage;

import java.util.List;


/**
 * Created by hoolai on 17/3/31.
 * 这个类的布局最外面包裹了RelativeLayout效率不怎么好
 * 最好使用ImgTxtButton 二个类的代码都一样
 */
public class ImgTxtRelativeLayout extends RelativeLayout implements ICaptchaEvent, ICaptchaImage, ImgTxtPopWidow.RequestEvent {

    private static String TAG = ImgTxtLayout.class.getSimpleName();

    private Context mContext;
    private Button mImgTxtBtn;
    private ImgTxtPopWidow mImgPopupWidow;
    private int mCheckBgColor;
    private int mCheckOkBgColor;
    private CharSequence mCheckTxt;//默认文字
    private CharSequence mCheckOkTxt;//验证成功的文字
    private CharSequence mCheckErrorTxt;//验证失败的文字
    private int mCheckWidth;
    private int mCheckHeight;
    private String mCheckType = String.valueOf(CaptchaCTypeEnum.CLICK.getId());//验证类型

    private Captcha captcha;
    private CheckState mCheckState;


    public ImgTxtRelativeLayout(Context context) {
        this(context, null);
    }

    public ImgTxtRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImgTxtRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        init(context);
    }

    /**
     * 初始化属性值
     * @param context
     * @param attrs
     */
    private void initAttrs(Context context, AttributeSet attrs){
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.ImgTextCheck);
        for(int i = 0; i < mTypedArray.getIndexCount(); i++){
            int attr = mTypedArray.getIndex(i);
            switch (attr){
                case R.styleable.ImgTextCheck_checkbg:
                    mCheckBgColor = mTypedArray.getResourceId(attr, R.color.color_blue_096096);
                    break;
                case R.styleable.ImgTextCheck_checkokbg:
                    mCheckOkBgColor = mTypedArray.getResourceId(attr, R.color.color_blue_81ADC8);
                    break;
                case R.styleable.ImgTextCheck_checktxt:
                    mCheckTxt = mTypedArray.getText(attr);
                    break;
                case R.styleable.ImgTextCheck_checkoktxt:
                    mCheckOkTxt = mTypedArray.getText(attr);
                    break;
                case R.styleable.ImgTextCheck_checkerrortxt:
                    mCheckErrorTxt = mTypedArray.getText(attr);
                    break;
                case R.styleable.ImgTextCheck_checktype:
                    mCheckType = mTypedArray.getString(attr);
                    break;
                case R.styleable.ImgTextCheck_checkwidth:
                    mCheckWidth = mTypedArray.getDimensionPixelSize(attr, getResources().getDimensionPixelSize(R.dimen.ImgTxtWidth));
                    break;
                case R.styleable.ImgTextCheck_checkheight:
                    mCheckHeight = mTypedArray.getDimensionPixelSize(attr, getResources().getDimensionPixelSize(R.dimen.ImgTxtHeight));
                    break;
            }
        }
        mTypedArray.recycle();
        captcha = new Captcha("45f5b905-4d15-41ca-ba4b-3a8612fc43cf", mCheckType, this, this);
    }

    /**
     * 初始化控件值
     * @param context
     */
    private void init(Context context) {
        this.mContext = context;
        mImgTxtBtn = new Button(mContext);
        addView(mImgTxtBtn);
        LayoutParams params = (LayoutParams) mImgTxtBtn.getLayoutParams();
        params.width = mCheckWidth;
        params.height = mCheckHeight;
        mImgTxtBtn.setLayoutParams(params);
        mImgTxtBtn.setBackgroundResource(mCheckBgColor);
        mImgTxtBtn.setText(mCheckTxt);
        mImgTxtBtn.setTextColor(Color.WHITE);
        mImgTxtBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mImgPopupWidow == null) {
                    mImgPopupWidow = new ImgTxtPopWidow(mContext);
                    mImgPopupWidow.setmRequestEvent(ImgTxtRelativeLayout.this);
                    mImgPopupWidow.setCaptChaType(Integer.valueOf(mCheckType));
                }

                mImgPopupWidow.showPopupWindow(v);
                mImgPopupWidow.showProgressBar();

                captcha.onClick();
            }
        });
    }

    /**
     * 验证成功
     * @param token
     * @param checkAddress
     * @param sid
     */
    @Override
    public void onSuccess(String token, String checkAddress, String sid) {
        mImgTxtBtn.setText(mCheckOkTxt);
        mImgTxtBtn.setBackgroundResource(mCheckOkBgColor);
        if (mImgPopupWidow != null && mImgPopupWidow.isShowing()) {
            mImgPopupWidow.ok();
        }
        if(mCheckState != null){
            mCheckState.CheckSucces(token, checkAddress, sid);
        }
    }

    @Override
    public void onCheckFail(boolean flag) {
        mImgTxtBtn.setText(mCheckErrorTxt);
        mImgTxtBtn.setBackgroundResource(mCheckBgColor);
        if (mImgPopupWidow != null && mImgPopupWidow.isShowing()) {
            mImgPopupWidow.error();
        }
        if(mCheckState != null){
            mCheckState.checkFail(flag);
        }
    }

    /**
     * 请求图片成功
     * @param image
     * @param simages
     */
    @Override
    public void onImageLoad(byte[] image, List<byte[]> simages) {
        if (mImgPopupWidow != null) {
            mImgTxtBtn.setText(mCheckTxt);
            mImgTxtBtn.setBackgroundResource(mCheckBgColor);
            mImgPopupWidow.clearPoints();
            mImgPopupWidow.hideProgressBar();
            mImgPopupWidow.setImgTxtBg(image, simages);
        }
    }

    /**
     * 重新加载
     */
    @Override
    public void onRefresh() {
        captcha.onReload();
    }

    @Override
    public void onSubmit(String loaction) {
        captcha.onSubmit(loaction);
    }

    public CheckState getmCheckState() {
        return mCheckState;
    }

    public void setmCheckState(CheckState mCheckState) {
        this.mCheckState = mCheckState;
    }
}
