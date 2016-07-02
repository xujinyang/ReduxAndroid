package me.jamesxu.reduxlib.reduce;

import me.jamesxu.reduxlib.state.State;
import me.jamesxu.reduxlib.action.Action;

/**
 * Created by mobilexu on 2/7/16.
 */
public interface Reduce {
    State handleAction(Action action);
}
