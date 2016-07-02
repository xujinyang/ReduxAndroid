package me.jamesxu.reduxandroid.action;

import android.os.Handler;

import java.util.Random;

import me.jamesxu.reduxandroid.reduce.ChangeTextReduce;
import me.jamesxu.reduxlib.action.Action;

/**
 * Created by mobilexu on 2/7/16.
 */
public class ChangeTextAction extends Action {


    public void changeText() {
        dispatch(createAction(ChangeTextReduce.ACTION_SHOWLOADING));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                clearHashMap();
                appendHashParam("text", new Random().nextInt(10) + "文本");
                dispatch(createAction(ChangeTextReduce.ACTION_SHOWTEXT));
            }
        }, 3000);
    }


}
