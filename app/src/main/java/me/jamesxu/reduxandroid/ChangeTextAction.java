package me.jamesxu.reduxandroid;

/**
 * Created by mobilexu on 2/7/16.
 */
public class ChangeTextAction {


    public Object changeText() {
        return {
                type:types.FETCH_NOTICE_LIST,
                loading:loading,
                isRefreshing:isRefreshing
        }
    }
}
