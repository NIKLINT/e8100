package com.tsits.tsmodel.manager;

import com.tsits.tsmodel.listener.IDataChangedListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 监听器管理
 */
public class TSListenerManager {

    private static TSListenerManager instance = new TSListenerManager();

    private List<IDataChangedListener> mDataListener;

    private TSListenerManager(){
        mDataListener = new ArrayList<>();
    }

    public static TSListenerManager getInstance(){
        return instance;
    }

    public List<IDataChangedListener> getDataListener() {
        return mDataListener;
    }

    public void setDataListener(List<IDataChangedListener> mDataListener) {
        this.mDataListener = mDataListener;
    }

    /**
     * 添加一个监听
     * @param listener 监听事件
     */
    public void addDataChangedEventListener(IDataChangedListener listener) {
        if (!this.mDataListener.contains(listener)) {
            this.mDataListener.add(listener);
        }
    }

    /**
     * 删除一个监听
     * @param e
     */
    public void removeDataChangedEventListener(IDataChangedListener e) {
        this.mDataListener.remove(e);
    }

    /**
     * 激活初始化完成事件
     */
    public void initFinishedEventListener() {
        for (IDataChangedListener listener : this.mDataListener) {
            listener.initFinished();
        }
    }
}
