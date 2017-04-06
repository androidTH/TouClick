package com.example.hoolai.touclick.utils;

/**
 * Created by hoolai on 17/4/5.
 */

public interface CheckState {
     public void CheckSucces(String token, String checkAddress, String sid);
     public void checkFail(boolean fail);
}
