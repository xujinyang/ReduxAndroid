package me.jamesxu.reduxandroid;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import me.jamesxu.reduxandroid.action.ChangeTextAction;
import me.jamesxu.reduxandroid.reduce.ChangeTextReduce;
import me.jamesxu.reduxandroid.state.ChangeTextState;
import me.jamesxu.reduxlib.BaseReduxActivity;
import me.jamesxu.reduxlib.store.Store;

public class MainActivity extends BaseReduxActivity<ChangeTextState> {

    private TextView buttonOne;
    private ChangeTextReduce reduce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonOne = (TextView) findViewById(R.id.buttonOne);
        init();
        render(null);
    }

    private void init() {
        reduce = new ChangeTextReduce();
        Store.getInstance().addReduce(reduce);

        buttonOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ChangeTextAction().changeText();
            }
        });
    }

    private void render(ChangeTextState state) {
        if (state == null)
            return;
        if (state.isLoading()) {
            buttonOne.setText("。。。");
        } else {
            buttonOne.setText(state.getText());
        }
    }

    @Override
    protected void onStateChange() {
        render(getState());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Store.getInstance().removeReduce(reduce);
    }
}
