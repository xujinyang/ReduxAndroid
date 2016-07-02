package me.jamesxu.reduxandroid;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import me.jamesxu.reduxandroid.action.SecondAction;
import me.jamesxu.reduxandroid.reduce.SecondReduce;
import me.jamesxu.reduxandroid.state.SecondState;
import me.jamesxu.reduxlib.BaseReduxActivity;
import me.jamesxu.reduxlib.store.Store;

public class SecondActivity extends BaseReduxActivity {

    private TextView ButtonTwo;
    private SecondReduce reduce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        ButtonTwo = (TextView) findViewById(R.id.buttonTwo);
        init();
        render(null);
    }

    private void init() {
        reduce = new SecondReduce();
        Store.getInstance().addReduce(reduce);

        ButtonTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SecondAction().changeText();
            }
        });
    }

    private void render(SecondState state) {
        if (state == null)
            return;
        if (state.isLoading()) {
            ButtonTwo.setText("。。。");
        } else {
            ButtonTwo.setText(state.getText());
        }
    }

    @Override
    protected void onStateChange() {
    }

    /**
     * 接收想要监听的状态变化
     *
     * @param state
     */
    public void onEvent(SecondState state) {
        render(state);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Store.getInstance().removeReduce(reduce);
    }
}
