package me.jamesxu.reduxlib.store;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import me.jamesxu.reduxlib.action.Action;
import me.jamesxu.reduxlib.reduce.Reduce;
import me.jamesxu.reduxlib.state.State;

/**
 * Created by mobilexu on 2/7/16.
 */
public class Store {

    private static Store defaultInstance;
    private EventBus eventBus;

    private List<Reduce> reducers;

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
        reducers = new ArrayList<>();
    }

    /**
     * 合并reduce
     *
     * @param reduceList
     */
    public void combineReducers(Reduce... reduceList) {
        for (Reduce reduce : reduceList) {
            reducers.add(reduce);
        }
    }

    /**
     * 添加单个reduce
     *
     * @param reduce
     */
    public void addReduce(Reduce reduce) {
        reducers.add(reduce);
    }

    /**
     * 移除单个Reduce
     *
     * @param reduce
     */
    public void removeReduce(Reduce reduce) {
        reducers.remove(reduce);
    }

    /**
     * 清空reducers
     */
    public void clearAllReduce() {
        reducers.clear();
    }

    /**
     * 发送一个Action
     *
     * @param action
     */
    public void dispatch(Action action) {
        for (Reduce reduce : reducers) {
            State localState = reduce.handleAction(action);
            if (localState != null) {
                eventBus.post(localState);
            }

        }
    }
}
