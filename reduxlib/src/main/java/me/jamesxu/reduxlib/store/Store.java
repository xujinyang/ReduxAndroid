package me.jamesxu.reduxlib.store;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import me.jamesxu.reduxlib.action.BaseAction;
import me.jamesxu.reduxlib.reduce.IReduce;
import me.jamesxu.reduxlib.state.State;

/**
 * Created by mobilexu on 2/7/16.
 */
public class Store {

    private static Store defaultInstance;
    private EventBus eventBus;

    private List<IReduce> iReducers;

    public static Store getInstance() {
        if (defaultInstance == null) {
            synchronized (EventBus.class) {
                if (defaultInstance == null) {
                    defaultInstance = new Store();
                }
            }
        }
        return defaultInstance;
    }

    private Store() {
        eventBus = EventBus.getDefault();
        iReducers = new ArrayList<>();
    }

    /**
     * 合并reduce
     *
     * @param iReduceList
     */
    public void combineReducers(IReduce... iReduceList) {
        for (IReduce reduce : iReduceList) {
            iReducers.add(reduce);
        }
    }

    /**
     * 添加单个reduce
     *
     * @param iReduce
     */
    public void addReduce(IReduce iReduce) {
        iReducers.add(iReduce);
    }

    /**
     * 移除单个Reduce
     *
     * @param iReduce
     */
    public void removeReduce(IReduce iReduce) {
        iReducers.remove(iReduce);
    }

    /**
     * 清空reducers
     */
    public void clearAllReduce() {
        iReducers.clear();
    }

    /**
     * 发送一个Action
     *
     * @param action
     */
    public void dispatch(BaseAction action) {
        for (IReduce iReduce : iReducers) {
            State localState = iReduce.handleAction(action);
            eventBus.post(localState);
        }
    }
}
