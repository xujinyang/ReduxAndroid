> Redux is a predictable state container for JavaScript apps                     	Redux是 JavaScript 状态容器，提供可预测化的状态管理。

Redux 文档开头就总结了Redux的意义。当然也非常详细的解释了Redux的三大原则和各模块作用，想入门请看[Redux中文文档](http://cn.redux.js.org/index.html)。

当你已经理清楚了Action，Reducer，Store直接的关系之后，你会有这样的疑问，Action怎么传递到Reducer中的？Reducer生成的新state又是怎么触发对应页面的刷新的？这个问题我也不想讲，因为这篇文章写的非常好-[深入到源码：解读 redux 的设计思路与用法](http://div.io/topic/1309),我其实想从Android开发者的角度谈一谈直接对Redux思想的理解和Redux for Android的尝试。

# Redux 思想
  Redux是应用状态管理服务，那么什么地方缺乏状态管理尼？比如React，它的UI以组件的形式嵌套在一起，数据状态从顶层传下，同级组件不能传递数据，组件触发回调，更新数据后，依然从上层同步下来，所以React和Redux配合简直了。这里其实就简单了描述了一个思想:

	所有的UI刷新都交给数据来控制

这个思想在我有限的开发经验中占很重要的地位。举个例子:

###### 1.在一个通知列表里面，标记一个通知已读，发生已读请求后，要刷新UI，你会怎么做？

可能你会在发送一个已读请求后，直接控制当前的ViewHolder显示为已读样式，这样有问题么？没有问题！
	
###### 2.这个时候再加个需求，要更新外部一个页面当前的未读通知数目。

是不是有点懵逼，当然大神还是有方法，获取当前的未读数目，减去刚才标记已读的一个。ok 又解决了！
	
###### 3.再加一个需求，当未读通知数目少于5个的时候，显示一个清空的按钮。

这个时候，你会发现，按照上面的思路，会写出很多零碎的逻辑，随着功能的增加，复杂度激增和可维护性降低,但是又隐约的感觉到之间共通的联系，那就是：都是经过一些操作之后，需要刷新UI，而UI显示的依据是上面？那就是**数据**。

那么我们换个思路再来推理一遍。   

###### 1.在一个通知列表里面，标记一个通知已读，发生已读请求后，要刷新UI，你会怎么做？

发送已读请求之后，修改通知list中对于的item已读状态为true,然后调用listAdapter.notifyDataSetChanged（）,更具最新的UI。
	
###### 2.这个时候再加个需求，要更新外部一个页面当前的未读通知数目。

接着上面，改变list中的item已读状态之后，那么当前的未读通知数目就立马获取到了，这个时候把这个数目发送给外部的UI，让它更具这个数目刷新UI。
	
###### 3.再加一个需求，当未读通知数目少于5个的时候，显示一个清空的按钮。

同样，改变list中的item已读状态之后,如果未读数目少于5个，显示出清空按钮，这就是个if else的问题。

感受一下，复杂度是不是立马降到了线性范围，原来三个或者更多的方法，现在就变成了改变数据，刷新UI这俩步。

这和Redux有什么关系？来看一下react-redux中是如何实现上面的第一个问题的。

noticeView
```
dispatch(setRead(noticeRead, rowID, notice.noticeList));

```

notionActionCreater

```
//发送已读请求
export function setRead(notice, rowID) {
	return dispatch => {
			request(SET_READ + notice.id, 'PUT')
				.then((response) => {
					if (response.code == 0) {
                    	// 请求成功，修改list中的item的status 为已读 1
						var newDs = [];
						newDs = dataSource.slice();
						notice.status = 1;
						var changeNotice = Immutable.Map(notice);
						newDs[rowID] = changeNotice.toObject();
						dispatch(setNoticeRead(newDs));
					} else {
						ToastShort(response.message);
					}
				})
				.catch((error) => {
					if (error != null) {
						ToastShort(error.message);
					}
				});
	}
}


//ActionCreater 产生的Action 使用dispatch传给Reducers

function setNoticeRead(noticeList) {
	return {
		type: types.SET_READ,
		noticeList: noticeList
	}
}

```
noticeReducer

```

const initialState = {
	isRefreshing: false,
	loading: false,
	noticeList: []
}
//直接将copy改后的list返回，这个时候会调用view的render方法
export default function notice(state = initialState, action) {
	switch (action.type) {
		case types.SET_READ:
			return Object.assign({}, state, {
				noticeList: action.noticeList
			});
		default:
			return state;
	}
}

```
noticeView

```
renderNoticeItm(notice) {
		if (notice.status == 1) {
			return (
					已读的View
			);
		} else {
			return (
					未读的View
			);
		}
	}

```

这时候我们发现，Redux的三大部分Action、Reducer、及 Store中，
- Action负责获取原生数据，或者只是描述一种状态。
- Reducer 根据Action中的数据或者线索进行再处理，生成ViewModel。

		这里为啥说state为viewModel,是因为state中不仅有noticelsit，还是有isRefreshing这样控制UI的变量。

- Store 就是联系Action和Reducers，State的纽带

分析到这里，已经领略到了Redux架构的魅力，那么作为一名Android开发工程师，是不是可以把这个思想搬到Android上尼？

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






























