package com.dou.maven;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
//import java.lang.reflect.Constructor;

import org.openjdk.jol.vm.VM;
import org.openjdk.jol.info.ClassLayout;

public class Hello  extends Thread {
	String name;
	public Hello(String name) {
		this.name=name;
	}
    private static int a = 0;
    private static int b = 0;
    private static int numcount = 0;
    private static long times = 0;
    static long t1= System.currentTimeMillis();
   // private Constructor bm = null;
   // static ReenTranLockDemo reenTranLockDemo = new ReenTranLockDemo();//simple reetranlock
    static Lock reenTranLockDemo = new ReentrantLock();
    
    public static String getStackTrace(Throwable t) {
    	 StringBuilder sb = new StringBuilder(t.getClass().getName() + ": " + t.getMessage() + "\n");
         StackTraceElement[] ste = t.getStackTrace();
         for (int i = 0; i < ste.length; i++) {
             sb.append(ste[i].toString() + "\n");
         }
         return sb.toString();
    }
    public void run(){
    	long t1= System.currentTimeMillis();
    	long t2= System.currentTimeMillis();
    	//try {
         //   reenTranLockDemo.lock();
            try {
               // reenTranLockDemo.lock();
                while(t2-t1<times){
                	try {
	                	reenTranLockDemo.lock();
	                	t2= System.currentTimeMillis();
	                    a++;
	                    a--;
                	}finally {
                		reenTranLockDemo.unlock();
                	}
                }
            }catch(Throwable t){
            	System.out.println(getStackTrace(t));
            }
           // }finally {
           // 	reenTranLockDemo.unlock();
          //  }
      //  }catch(Throwable t){
       // 	System.out.println(getStackTrace(t));
        reenTranLockDemo.lock();
        b=b-1;
        reenTranLockDemo.unlock();
        while(b>0) {
        	try {
            	reenTranLockDemo.lock();
                a++;
                a--;
                //System.out.println(b);
        	}finally {
        		reenTranLockDemo.unlock();
        	}
        }
    }
 
    public static void test( int numth) throws InterruptedException {
    	Thread[] all = new Thread[10];
    	for(int i=0;i<numth;i++) {
    		all[i] = new Thread(new Hello("thread-"+i));
    		all[i].setName("thread-"+i);
    		all[i].start();
    	}
    	TimerTask timerTask = new TimerTask() {
    		public void run() {
    			long t3= System.currentTimeMillis();
    			if(t3-t1<times*numcount) {
    				t3 = System.currentTimeMillis();
    				System.out.printf("moniter lock address is %08x \n",VM.current().addressOf(reenTranLockDemo));
				}
    		}
    	};
    	Timer timer = new Timer();
    	timer.scheduleAtFixedRate(timerTask, 0, 2000);
    	for(int i=0;i<numth;i++) {
    		all[i].join();
    	}
    	timer.cancel();
    }
    
    public static void main(String[] args) throws InterruptedException{
    	String bm = "lock test doubear";
    	numcount = Integer.parseInt(args[1]);
    	int num = numcount;
    	times = Integer.parseInt(args[0]);  	
    	b = Integer.parseInt(args[2]);
    	System.out.println(bm+" threads: "+b+" counts:"+num+" times:"+times);
    	System.out.printf("source a address is %08x \n",VM.current().addressOf(a));
    	System.out.printf("1 lock address is %08x \n",VM.current().addressOf(reenTranLockDemo));
    	System.out.println("lock info is "+ClassLayout.parseInstance(reenTranLockDemo).toPrintable());
    	while(num>0) {
    		num--;
    		test(b);
    		b = Integer.parseInt(args[2]);
    	}
    	long t2= System.currentTimeMillis()-t1;
    	System.out.println("source a "+a);
    	System.out.printf("2 lock address is %08x \n",VM.current().addressOf(reenTranLockDemo));
    	System.out.println("times:"+times+" numthreads: "+ b +" total: "+t2);
    	
    }
}

