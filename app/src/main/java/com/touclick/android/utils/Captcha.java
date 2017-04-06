package com.touclick.android.utils;

import java.util.List;

public class Captcha  implements CaptchaSdkUtil.IHttpResponseEvent{

    private CaptchaSdkUtil captchaSdkUtil = new CaptchaSdkUtil(this);
    private String pubkey;

    public String getCt() {
        return ct;
    }

    public void setCt(String ct) {
        this.ct = ct;
    }

    private String ct;
    private ICaptchaEvent event;
    private ICaptchaImage imageHandle;
    private String sid;
    private List<byte[]> simages;
    private byte[] image;

    public Captcha(String pubkey, String ct, ICaptchaEvent event, ICaptchaImage imageHandle) {
        this.pubkey = pubkey;
        this.ct = ct;
        this.event = event;
        this.imageHandle = imageHandle;
    }

    //点击蓝色条
    public void onClick() {
        captchaSdkUtil.touclickCheck(pubkey, ct);
    }

    //点击刷新按钮
    public void onReload() {
        captchaSdkUtil.touclickCaptcha(sid, pubkey, ct);
    }

    //点击提交按钮
    public void onSubmit(String answer) {
        captchaSdkUtil.touclickVerify(answer, pubkey, ct, sid);
    }

    public String getSid() {
        return this.sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public List<byte[]> getSimages() {
        return this.simages;
    }

    public void setSimages(List<byte[]> simages) {
        this.simages = simages;
    }

    public byte[] getImage() {
        return this.image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

//    public static void main(String[] args) {
//        ICaptchaImage cap = new CaptchaImageImpl();
//        ICaptchaEvent evn = new CaptchaEventImpl();
//        Captcha captcha = new Captcha("45f5b905-4d15-41ca-ba4b-3a8612fc43cf", String.valueOf(CaptchaCTypeEnum.CLICK.getId()), evn, cap);
//        captcha.onClick();
//        captcha.onReload();
//        captcha.onSubmit("82,28,24,81");
//    }

    @Override
    public void touclickCheck(CaptchaSdkUtil.CheckModel checkModel) {
        this.sid = checkModel.getSid();
        if (checkModel.getSid() == null) {
            return;
        } else if (checkModel.getToken() != null) { //免验证
            event.onSuccess(checkModel.getToken(), "", checkModel.getSid());
        } else {
            captchaSdkUtil.touclickCaptcha(sid, pubkey, ct);
        }
    }

    @Override
    public void touclickCaptcha(ImageByteModel imageByteModel) {
        this.sid = imageByteModel.getSid();
        imageHandle.onImageLoad(imageByteModel.getImage(), imageByteModel.getSimage());
    }

    @Override
    public boolean touclickVerify(CaptchaSdkUtil.VerifyModel result) {
        if (result != null && result.getCheckAddress() != null) {
            this.sid = result.getSid();
            event.onSuccess(result.getToken(), result.getCheckAddress(), result.getSid());
            return true;
        }
        event.onCheckFail(false);
        return false;
    }
}