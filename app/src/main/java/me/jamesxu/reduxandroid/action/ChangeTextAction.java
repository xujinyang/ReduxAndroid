package me.jamesxu.reduxandroid.action;

import android.os.Handler;

import java.util.Random;

import me.jamesxu.reduxlib.action.BaseAction;

/**
 * Created by mobilexu on 2/7/16.
 */
public class ChangeTextAction extends BaseAction {


    public void changeText() {
        dispatch(createAction(0));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                clearHashMap();
                appendHashParam("text", new Random().nextInt(10) + "文本");
                dispatch(createAction(1));
            }
        }, 3000);
    }


}
