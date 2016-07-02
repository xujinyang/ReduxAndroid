package me.jamesxu.reduxandroid.action;

import android.os.Handler;

import java.util.Random;

import me.jamesxu.reduxandroid.reduce.SecondReduce;
import me.jamesxu.reduxlib.action.Action;

/**
 * Created by mobilexu on 2/7/16.
 */
public class SecondAction extends Action {


    public void changeText() {
        dispatch(createAction(SecondReduce.ACTION_SECOND_SHOWLOADING));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                clearHashMap();
                appendHashParam("text", new Random().nextInt(100) + "文本");
                dispatch(createAction(SecondReduce.ACTION_SECOND_SHOWTEXT));
            }
        }, 3000);
    }


}
