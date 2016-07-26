/*  1:   */ package org.boon.concurrent;
/*  2:   */ 
/*  3:   */ import java.util.concurrent.ExecutorService;
/*  4:   */ import java.util.concurrent.Executors;
/*  5:   */ import java.util.concurrent.ThreadFactory;
/*  6:   */ 
/*  7:   */ public class SimpleExecutors
/*  8:   */ {
/*  9:   */   public static ExecutorService threadPool(int size, final String poolName)
/* 10:   */   {
/* 11:14 */     int[] threadId = new int[1];
/* 12:15 */     threadId[0] = 0;
/* 13:16 */     Executors.newFixedThreadPool(size, new ThreadFactory()
/* 14:   */     {
/* 15:   */       public Thread newThread(Runnable runnable)
/* 16:   */       {
/* 17:20 */         int tmp10_9 = 0; int[] tmp10_6 = this.val$threadId; int tmp12_11 = tmp10_6[tmp10_9];tmp10_6[tmp10_9] = (tmp12_11 + 1);this.val$threadId[0] = tmp12_11;
/* 18:21 */         Thread thread = new Thread(runnable);
/* 19:22 */         thread.setName(poolName + " " + this.val$threadId[0]);
/* 20:23 */         return thread;
/* 21:   */       }
/* 22:   */     });
/* 23:   */   }
/* 24:   */   
/* 25:   */   public static ExecutorService threadPool(String poolName)
/* 26:   */   {
/* 27:32 */     Executors.newCachedThreadPool(new ThreadFactory()
/* 28:   */     {
/* 29:   */       public Thread newThread(Runnable runnable)
/* 30:   */       {
/* 31:36 */         Thread thread = new Thread(runnable);
/* 32:37 */         thread.setName(this.val$poolName);
/* 33:38 */         return thread;
/* 34:   */       }
/* 35:   */     });
/* 36:   */   }
/* 37:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.concurrent.SimpleExecutors
 * JD-Core Version:    0.7.0.1
 */