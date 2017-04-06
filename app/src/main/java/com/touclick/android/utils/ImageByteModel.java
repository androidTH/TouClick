package com.touclick.android.utils;

import java.util.List;

/**
 * Created by fanjl on 2017/3/30.
 */
public class ImageByteModel {
    private byte[] image;
    private List<byte[]> simage;
    private String sid;

    public byte[] getImage() {
        return this.image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public List<byte[]> getSimage() {
        return this.simage;
    }

    public void setSimage(List<byte[]> simage) {
        this.simage = simage;
    }

    public String getSid() {
        return this.sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }
}
