package com.example.hoolai.touclick.utils;

import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Call;


/**
 * Created by fanjl on 2017/3/29.
 */
public class CaptchaSdkUtil {
    private final String _keys = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_=";
    private Gson gson = new Gson();
    private final String CheckUrl = "http://cap-5-2-0.touclick.com/public/check?";
    private final String CaptchaUrl = "http://cap-5-2-0.touclick.com/public/captcha?";
    private final String VrifyUrl = "http://ver-5-2-0.touclick.com/verifybehavior?";
    private IHttpResponseEvent mIHRepsonseEvent;

    public CaptchaSdkUtil(IHttpResponseEvent iHttpResponseEvent){
        this.mIHRepsonseEvent = iHttpResponseEvent;
    }
    /**
     * 获取图片
     */
    public String restore(Base64Model base64Model) {
        int ran_count = ((int) (Math.random() * 500)) | 0;
        int count_a = (base64Model.getCountA() - ran_count) % 4 + ran_count;
        String baseStrModel = base64Model.getBaseStr();
        for (int i = 0; i < count_a; i++) {
            baseStrModel += 'A';
        };

        for (int i = 0; i < base64Model.getCountEqual(); i++) {
            baseStrModel += '=';
        };
        return baseStrModel;

    }


    public class Base64Model {
        private int countA;
        private String baseStr;
        private String f;
        private int countEqual;

        public Base64Model() {
        }

        public int getCountA() {
            return this.countA;
        }

        public void setCountA(int countA) {
            this.countA = countA;
        }

        public int getCountEqual() {
            return this.countEqual;
        }

        public void setCountEqual(int countEqual) {
            this.countEqual = countEqual;
        }

        public String getBaseStr() {
            return this.baseStr;
        }

        public void setBaseStr(String baseStr) {
            this.baseStr = baseStr;
        }

        public String getF() {
            return this.f;
        }

        public void setF(String f) {
            this.f = f;
        }
    }

    /**
     * 处理Base64解码并写图片到指定位置
     *
     * @param base64 图片Base64数据
     * @param path   图片保存路径
     * @return
     */
    public boolean base64ToImageFile(String base64, String path) throws IOException {// 对字节数组字符串进行Base64解码并生成图片
        // 生成jpeg图片
        try {
            OutputStream out = new FileOutputStream(path);
            return base64ToImageOutput(base64, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 处理Base64解码并输出流
     *
     * @param base64
     * @param out
     * @return
     */
    public boolean base64ToImageOutput(String base64, OutputStream out) throws IOException {
        if (base64 == null) { // 图像数据为空
            return false;
        }
        try {
            // Base64解码
            byte[] bytes = android.util.Base64.decode(base64, Base64.DEFAULT);
            for (int i = 0; i < bytes.length; ++i) {
                if (bytes[i] < 0) {// 调整异常数据
                    bytes[i] += 256;
                }
            }
            // 生成jpeg图片
            out.write(bytes);
            out.flush();
            return true;
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public byte[] base64ToImageByte(String base64) throws IOException {
        // Base64解码
        byte[] bytes = android.util.Base64.decode(base64, Base64.DEFAULT);
        for (int i = 0; i < bytes.length; ++i) {
            if (bytes[i] < 0) {// 调整异常数据
                bytes[i] += 256;
            }
        }
        return bytes;
    }

    public String getChar(int code) {
        if (code < 10) {
            return code + "";
        }
        return fromCharCode(code + 55);
    }

    public String getSid() {
        String timestamp = Long.toHexString(new Date().getTime()).toUpperCase();
        int count = 32 - timestamp.length();
        while (count-- > 0) {
            timestamp += getChar((int) (Math.random() * 36) | 0);
        }
        return timestamp;
    }

    public void touclickCheck(String publicKey, String ct) {
        StringBuilder params = new StringBuilder();
        String cb = "beh" + getSid();
        params.append(CheckUrl).append("cb=").append(cb).append("&b=").append(publicKey).append("&ran=")
                .append(Math.random());
                OkHttpUtils
                        .get()
                        .url(params.toString())
                        .build()
                        .execute(new StringCallback() {

                            @Override
                            public void inProgress(float progress, long total, int id) {
                            }

                            @Override
                            public void onError(Call call, Exception e, int id) {
                                Log.i("onError", "Error" + e.toString());
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                //回调
                                String json = response.split("\\(")[1].replaceAll("\\)", "").replaceAll(";","");
                                CheckModel checkModel = gson.fromJson(json,CheckModel.class);
                                mIHRepsonseEvent.touclickCheck(checkModel);
                            }
                        });
    }

    public void touclickCaptcha(String sid, String publicKey, String ct) {
        String callback = "cb" + getSid();
        StringBuilder params = new StringBuilder();
        params.append(CaptchaUrl).append("cb=").append(callback).append("&b=").append(publicKey).append("&ct=")
                .append(ct).append("&sid=").append(sid).append("&ran=").append(Math.random());
                OkHttpUtils
                        .get()
                        .url(params.toString())
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void inProgress(float progress, long total, int id) {
                            }

                            @Override
                            public void onError(Call call, Exception e, int id) {
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                try {
                                    //回调
                                    Log.i("touclickCaptcha","jsonInfo = " +response);
                                    if(!response.isEmpty()){
                                        String json = response.split("\\(")[1].replaceAll("\\)", "");
                                        CaptchaModel captchaModel = gson.fromJson(json, CaptchaModel.class);
                                        CaptchaModel.Data datas = captchaModel.getData();
                                        String imageB64 = restore(datas.getImage());
                                        String image0B64 = restore(datas.getImage_0());
                                        String image1B64 = restore(datas.getImage_1());
                                        ImageByteModel imageByteModel = new ImageByteModel();

                                        imageByteModel.setImage(base64ToImageByte(imageB64));

                                        List<byte[]> simageBytes = new LinkedList<byte[]>();
                                        simageBytes.add(base64ToImageByte(image0B64));
                                        simageBytes.add(base64ToImageByte(image1B64));
                                        imageByteModel.setImage(base64ToImageByte(imageB64));
                                        imageByteModel.setSimage(simageBytes);
                                        imageByteModel.setSid(captchaModel.getSid());
                                        mIHRepsonseEvent.touclickCaptcha(imageByteModel);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
    }

    public void touclickVerify(String answer, String publicKey, String ct,final String sid) {
        String callback = "ve" + getSid();
        StringBuilder params = new StringBuilder();
        params.append(VrifyUrl).append("b=").append(publicKey).append("&cb=")
                .append(callback).append("&ct=").append(ct).append("&ckcode=&sid=")
                .append(sid).append("&r=").append(answer).append("&ran=")
                .append(Math.random());
            OkHttpUtils
                    .get()
                    .url(params.toString())
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            Log.i("onError", "Error" + e.toString());
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            //回调
                            String json = response.split("\\(")[1].replaceAll("\\)", "");
                            VerifyModel verifyModel = gson.fromJson(json, VerifyModel.class);
                            verifyModel.setSid(sid);
                            mIHRepsonseEvent.touclickVerify(verifyModel);
                        }
                    });
    }

    public String fromCharCode(int... codePoints) {
        return new String(codePoints, 0, codePoints.length);
    }
    public class CheckModel{
        private String sid;
        private String token;
        private String warning;

        public String getSid() {
            return this.sid;
        }

        public void setSid(String sid) {
            this.sid = sid;
        }

        public String getToken() {
            return this.token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getWarning() {
            return this.warning;
        }

        public void setWarning(String warning) {
            this.warning = warning;
        }
    }
    public class VerifyModel {
        private String token;
        private String checkAddress;
        private String retry;
        private String sid;

        public String getSid() {
            return this.sid;
        }

        public void setSid(String sid) {
            this.sid = sid;
        }

        public String getRetry() {
            return this.retry;
        }

        public void setRetry(String retry) {
            this.retry = retry;
        }

        public String getToken() {
            return this.token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getCheckAddress() {
            return this.checkAddress;
        }

        public void setCheckAddress(String checkAddress) {
            this.checkAddress = checkAddress;
        }
    }

    public class CaptchaModel {
        private int ct;
        private String sid;
        private String vt;
        private Kt kt;
        private Data data;

        public int getCt() {
            return this.ct;
        }

        public void setCt(int ct) {
            this.ct = ct;
        }

        public String getSid() {
            return this.sid;
        }

        public void setSid(String sid) {
            this.sid = sid;
        }

        public String getVt() {
            return this.vt;
        }

        public void setVt(String vt) {
            this.vt = vt;
        }

        public Kt getKt() {
            return this.kt;
        }

        public void setKt(Kt kt) {
            this.kt = kt;
        }

        public Data getData() {
            return this.data;
        }

        public void setData(Data data) {
            this.data = data;
        }

        public class Kt {
            private String[] image;
            private String[] simage;

            public String[] getImage() {
                return this.image;
            }

            public void setImage(String[] image) {
                this.image = image;
            }

            public String[] getSimage() {
                return this.simage;
            }

            public void setSimage(String[] simage) {
                this.simage = simage;
            }
        }

        public class Data {
            private Base64Model image;
            private Base64Model image_0;
            private Base64Model image_1;

            public Base64Model getImage() {
                return this.image;
            }

            public void setImage(Base64Model image) {
                this.image = image;
            }

            public Base64Model getImage_0() {
                return this.image_0;
            }

            public void setImage_0(Base64Model image_0) {
                this.image_0 = image_0;
            }

            public Base64Model getImage_1() {
                return this.image_1;
            }

            public void setImage_1(Base64Model image_1) {
                this.image_1 = image_1;
            }
        }
    }

    public interface IHttpResponseEvent{
        public void touclickCheck(CheckModel checkModel);
        public void touclickCaptcha(ImageByteModel imageByteModel);
        public boolean touclickVerify(VerifyModel verifyModel);
    }
}
