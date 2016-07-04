> Redux is a predictable state container for JavaScript apps                     	Redux是 JavaScript 状态容器，提供可预测化的状态管理。

Redux 文档开头就总结了Redux的意义。当然也非常详细的解释了Redux的三大原则和各模块作用，想入门请看[Redux中文文档](http://cn.redux.js.org/index.html)。

当你已经理清楚了Action，Reducer，Store直接的关系之后，你会有这样的疑问，Action怎么传递到Reducer中的？Reducer生成的新state又是怎么触发对应页面的刷新的？这个问题我也不想讲，因为这篇文章写的非常好-[深入到源码：解读 redux 的设计思路与用法](http://div.io/topic/1309),我其实想从Android开发者的角度谈一谈直接对Redux思想的理解和Redux for Android的尝试。

# Redux for Android的尝试

说干就干，这里模拟一个网络请求，先看效果。

![](http://7o4zmy.com1.z0.glb.clouddn.com/D98F33E0-EC27-4069-8FE9-F1BD63441912.png)

再来看代码： 

BaseReduxActivity 中使用Event来桥接View和state的改动，相当于 react-redux中的   connect
```
/**
 * 
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

    /**
     * 接收想要监听的状态变化
     *
     * @param state
     */
    public void onEvent(State state) {
        onStateChange(state);
    }

    protected abstract void onStateChange(State state);
}
```

Action中提供一个type，和一个string->Object的HashMap
```
/**
 * Created by mobilexu on 2/7/16.
 */
public class Action {

    private int type;
    private HashMap<String, Object> hashMap;

    public int getType() {
        return type;
    }


    public Action(int type) {
        this.type = type;
    }

    public Action(int type, HashMap<String, Object> hashMap) {
        this.type = type;
        this.hashMap = hashMap;
    }

    public HashMap<String, Object> appendHashParam(String key, Object object) {
        if (hashMap == null) {
            hashMap = new HashMap<>();
        }
        hashMap.put(key, object);
        return hashMap;
    }

    public HashMap<String, Object> getHashMap() {
        return hashMap;
    }
}
```

ActionCreater简单的提供一下dispatch方法
```
/**
 * Created by mobilexu on 3/7/16.
 */
public class ActionCreater {

    public void dispatch(Action action) {
        Store.getInstance().dispatch(action);
    }
}

```
Reduce和State目前只是个接口
```
/**
 * Created by mobilexu on 2/7/16.
 */
public interface Reduce {
    State handleAction(Action action);
}
```

```
/**
 * Created by mobilexu on 2/7/16.
 */
public interface State {
}
```
Store中提供了Reducer的注册和dispatch的真正实现，还有就是桥接了EventBus用于桥接view和state的改变
```
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
```

最后看一下如何使用
Reducer可以在Application中统一添加，也可以在各种的页面添加。

```
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
                new SecondActionCreater().changeText();
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
    protected void onStateChange(State state) {
        if (state instanceof SecondState) {
            render((SecondState) state);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Store.getInstance().removeReduce(reduce);
    }
}
```
SecondActionCreater模拟了一次类似网络请求的操作,发送了俩个Action出去
```
public class SecondActionCreater extends ActionCreater {


    public void changeText() {
        dispatch(new Action(SecondReduce.ACTION_SECOND_SHOWLOADING));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showText();
            }
        }, 3000);
    }

    private void showText() {
        Action action = new Action(SecondReduce.ACTION_SECOND_SHOWTEXT);
        action.appendHashParam("text", new Random().nextInt(10) + "文本");
        dispatch(action);
    }


}

```
SecondReduce 收到Action之后，根据type对数据进行加工

```
public class SecondReduce implements Reduce {


    public static final int ACTION_SECOND_SHOWLOADING = 3;
    public static final int ACTION_SECOND_SHOWTEXT = 4;

    @Override
    public State handleAction(Action action) {
        switch (action.getType()) {
            case ACTION_SECOND_SHOWLOADING:
                return new SecondState(true, null);
            case ACTION_SECOND_SHOWTEXT:
                return new SecondState(false, action.getHashMap().get("text").toString());
            default:
                return null;
        }
    }
}
```
SecondState 简直就是个ViewModle

```
public class SecondState implements State {
    private boolean isLoading;
    private String text;


    public SecondState(boolean isLoading, String text) {
        this.isLoading = isLoading;
        this.text = text;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
```
整个过程，将操作都放在了SecondActionCreater 中，其实这个时候，ActionCreater就是MVP中的presenter，而Reducer就是生成ViewModle的过滤器，他不负责原始数据的采集，只是根据Action的描述，对数据进行一次加工,最后Reducer产生了一个State，通过EventBus维护的state和activity关系，进行了分发。

机智的读者会发现，redux 建议只维护一个全局 state ，而上面的框架中state是分散的，这里我有这样的考虑，原始app和web开发不太相同，一个工具app，从打开使用到最后关闭，可能会遍历几十个页面，这个时候如果维护全局一个state,那么势必会有不要的性能开销，而且这里我也可以通过监听不同的state来实现特殊情况下的需求。

```
 @Override
    protected void onStateChange(State state) {
        if (state instanceof ChangeTextState) {
            render((ChangeTextState) state);
        } else if (state instanceof SecondState) {
            renderSecond((SecondState) state);
        }
    }
```

上面简单写了一个redux for Android的框架，源码可以[这里](https://github.com/xujinyang/ReduxAndroid)看到，下面的愿景是将DataBinding结合，那个时候，ActionCreater相当于Presenter,state相当于ViewModel,那这个架构就叫PVVM。

猝不及防的吹了个牛逼，不要见怪。不过还是欢迎大家提pr。






























