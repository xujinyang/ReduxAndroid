package me.jamesxu.reduxlib;

/**
 * Created by mobilexu on 2/7/16.
 */
public interface IReduce {

    void dispatch(BaseAction action);

    void onStateChange();
}
