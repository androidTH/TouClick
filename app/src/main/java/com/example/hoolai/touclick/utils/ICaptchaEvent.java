package com.example.hoolai.touclick.utils;

import java.util.List;

/**
 * Created by fanjl on 2017/3/30.
 */
public interface ICaptchaEvent{

    void onSuccess(String token, String checkAddress, String sid);

    void onCheckFail(boolean flag);

    //大图和小图
    void onImageLoad(byte[] image, List<byte[]> simages);
}
