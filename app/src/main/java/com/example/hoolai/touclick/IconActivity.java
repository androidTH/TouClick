package com.example.hoolai.touclick;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.hoolai.touclick.utils.CheckState;

public class IconActivity extends AppCompatActivity implements CheckState {

    private TextView mTvResult;
    private ImgTxtRelativeLayout mImgButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_icon);
        mTvResult = (TextView) findViewById(R.id.tvresult);
        mImgButton = (ImgTxtRelativeLayout) findViewById(R.id.imgtxtrelativelayout);
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
