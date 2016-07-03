package me.jamesxu.reduxandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import me.jamesxu.reduxandroid.action.ChangeTextActionCreater;
import me.jamesxu.reduxandroid.reduce.ChangeTextReduce;
import me.jamesxu.reduxandroid.state.ChangeTextState;
import me.jamesxu.reduxandroid.state.SecondState;
import me.jamesxu.reduxlib.BaseReduxActivity;
import me.jamesxu.reduxlib.state.State;
import me.jamesxu.reduxlib.store.Store;

public class MainActivity extends BaseReduxActivity {

    private TextView buttonOne;
    private TextView buttonTwo;
    private ChangeTextReduce reduce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonOne = (TextView) findViewById(R.id.buttonOne);
        buttonTwo = (TextView) findViewById(R.id.buttonTwo);
        init();
        render(null);
    }

    private void init() {
        reduce = new ChangeTextReduce();
        Store.getInstance().addReduce(reduce);

        buttonOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ChangeTextActionCreater().changeText();
            }
        });

        buttonTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SecondActivity.class));
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
    protected void onStateChange(State state) {
        if (state instanceof ChangeTextState) {
            render((ChangeTextState) state);
        } else if (state instanceof SecondState) {
            renderSecond((SecondState) state);
        }
    }

    private void renderSecond(SecondState state) {
        if (state == null)
            return;
        if (state.isLoading()) {
            buttonOne.setText("。。。");
        } else {
            buttonOne.setText(state.getText());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Store.getInstance().removeReduce(reduce);
    }
}
