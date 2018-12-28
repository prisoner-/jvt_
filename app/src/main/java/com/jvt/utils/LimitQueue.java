package com.jvt.utils;


import android.util.Log;

import java.util.LinkedList;

public class LimitQueue<E>{
    private int lastUmid = 0;
    private int limit; // 队列长度
    private Boolean canAdd=new Boolean(true);
    private LinkedList<E> queue = new LinkedList<E>();

    public LimitQueue(int limit){
        this.limit = limit;
    }
    public void setCanAdd(boolean canAdd){
        this.canAdd = canAdd;
    }
    /**
     * 入列：当队列大小已满时，把队头的元素poll掉
     */
    public void offer(E e,int umid){
        if(umid == lastUmid || lastUmid == 0){
            lastUmid = umid;
            if(canAdd){
                if(queue.size() >= limit){
                    queue.poll();
                }
                queue.offer(e);
            }
        }else{
            Log.i("quene","设备切换");
            queue.clear();//设备切换，先清空后添加
            lastUmid = umid;
            if(canAdd){
                if(queue.size() >= limit){
                    queue.poll();
                }
                queue.offer(e);
            }
        }
    }

    public E get(int position) {
        return queue.get(position);
    }

    public E getLast() {
        return queue.getLast();
    }

    public E getFirst() {
        return queue.getFirst();
    }

    public int getLimit() {
        return limit;
    }

    public int size() {
        return queue.size();
    }

}