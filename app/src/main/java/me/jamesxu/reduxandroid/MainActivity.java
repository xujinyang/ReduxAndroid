package me.jamesxu.reduxandroid;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import me.jamesxu.reduxlib.BaseReduxActivity;
import me.jamesxu.reduxlib.Store;

public class MainActivity extends BaseReduxActivity {

    private TextView buttonOne;
    private Store store;
    private ChangeReduce reduce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonOne = (TextView) findViewById(R.id.buttonOne);

        buttonOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventBus.post(new ChangeTextEvent());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        store.register(this);
    }

    public void onStateChange(ChangeTextState state) {
        render(state);
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
    protected void componentWillReceiveProps() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        store.unRegister(reduce);
    }
}
