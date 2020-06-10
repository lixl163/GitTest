package com.zsinfo.guoranhao.utils.Alipayutils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * Created by hyk on 2017/4/6.
 */

public class AlipaySubject {
    //用于保存注册的观察者对象
    private List<AlipayObserver> observers =new ArrayList<AlipayObserver>();

    //把观察者添加到观察者集合中
    public void attach(AlipayObserver observer){
        observers.add(observer);
    }
    //删除观察者集合中指定的观察者.
    public void detach(AlipayObserver observer){
        observers.remove(observer);
    }
    //通知方法,需要通知观察者列表中的所有观察者.
    protected void notifyObservers(){
        for(AlipayObserver observer : observers){
            //把当前具体被观察者对象传入到update参数中,以便在update()方法中获取被观察者的状态信息.
            observer.update(this);
        } }

}
