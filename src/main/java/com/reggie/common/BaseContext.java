package com.reggie.common;


/**
 * ThreadLocal叫做线程变量，意思是ThreadLocal中填充的变量属于当前线程，对其他线程隔离
 * **/

public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setThreadLocal(Long id){
        threadLocal.set(id);
    }

    public static Long getThreadLocal() {
        return threadLocal.get();
    }

    /**
     * 设置值
     * @param id
     */
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    /**
     * 获取值
     * @return
     */
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
