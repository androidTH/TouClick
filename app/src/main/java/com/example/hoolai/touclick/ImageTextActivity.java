package com.example.hoolai.touclick;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.hoolai.touclick.utils.CheckState;

public class ImageTextActivity extends AppCompatActivity implements CheckState{

    private TextView mTvResult;
    private ImgTxtButton mImgButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_text);
        mTvResult = (TextView) findViewById(R.id.tvresult);
        mImgButton = (ImgTxtButton) findViewById(R.id.imgtxtbutton);
        mImgButton.setmCheckState(this);
    }

    @Override
    public void CheckSucces(String token, String checkAddress, String sid) {
        mTvResult.setText("token" + token + "checkAddress=" + checkAddress + "sid=" + sid);
    }

    @Override
    public void checkFail(boolean fail) {
        mTvResult.setText("失败" + fail);
    }
}
