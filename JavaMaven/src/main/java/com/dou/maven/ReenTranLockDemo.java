package com.dou.maven;

/**
 * 可重入锁示例
 */
public class ReenTranLockDemo {
    private Object currentThread;
    private int lockTimes = 0;
    public synchronized void lock() throws InterruptedException {
        while (Thread.currentThread() == currentThread || currentThread == null || lockTimes > 0) {
            if (Thread.currentThread() == currentThread) {
                //当前线程重复加锁
                lockTimes++;
                System.out.println(1);
                System.out.println(currentThread);
                return;
            } else if (currentThread == null) {
                //当前线程首次加锁
                currentThread = Thread.currentThread();
                lockTimes++;
                System.out.println(2);
                System.out.println(currentThread);
                return;
            } else {
                //锁已被占用，当前线程进入等待
                wait();
                System.out.println(3);
                System.out.println(currentThread);
            }
        }
        System.out.println("lockTime:" + lockTimes);
        return;
    }
    public synchronized void unlock() {
        if (currentThread != null && Thread.currentThread() == currentThread) {
            if (1 == lockTimes) {
                currentThread = null;
                notify();
                System.out.println(4);
                System.out.println(currentThread);
            } else {
                System.out.println(5);
                System.out.println(currentThread);
            }
            lockTimes--;
        }
        System.out.println("lockTime:" + lockTimes);
    }
 
 
}