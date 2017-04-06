package com.example.hoolai.touclick.utils;

import java.util.List;

/**
 * Created by fanjl on 2017/3/30.
 */
public interface ICaptchaImage{
    //大图和小图
    void onImageLoad(byte[] image, List<byte[]> simages);
}
