package me.jamesxu.reduxlib;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import de.greenrobot.event.EventBus;

/**
 * Created by mobilexu on 30/6/16.
 */
public abstract class BaseReduxActivity extends AppCompatActivity {

    public EventBus eventBus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventBus = EventBus.getDefault();
        eventBus.register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventBus.unregister(this);
    }

    //EventBus至少有一个onEvent方法
    public void onEvent(String defaultEvent) {

    }

//    /**
//     * 接收想要监听的状态变化
//     *
//     * @param state
//     */
//    public void onEvent(T state) {
//        this.state = state;
//        onStateChange();
//    }

//    public T getState() {
//        return state;
//    }

    protected abstract void onStateChange();
}
