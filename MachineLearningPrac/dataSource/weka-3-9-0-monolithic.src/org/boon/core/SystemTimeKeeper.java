/*  1:   */ package org.boon.core;
/*  2:   */ 
/*  3:   */ import java.util.concurrent.Executors;
/*  4:   */ import java.util.concurrent.ScheduledExecutorService;
/*  5:   */ import java.util.concurrent.TimeUnit;
/*  6:   */ import java.util.concurrent.atomic.AtomicBoolean;
/*  7:   */ import java.util.concurrent.atomic.AtomicLong;
/*  8:   */ import java.util.concurrent.atomic.AtomicReference;
/*  9:   */ import org.boon.core.timer.TimeKeeper;
/* 10:   */ 
/* 11:   */ public class SystemTimeKeeper
/* 12:   */   implements TimeKeeper
/* 13:   */ {
/* 14:44 */   private static final AtomicLong time = new AtomicLong();
/* 15:45 */   private static final AtomicBoolean started = new AtomicBoolean();
/* 16:   */   private static ScheduledExecutorService executorService;
/* 17:   */   
/* 18:   */   public long time()
/* 19:   */   {
/* 20:51 */     return time.get();
/* 21:   */   }
/* 22:   */   
/* 23:   */   public static void start()
/* 24:   */   {
/* 25:57 */     if (!started.getAndSet(true))
/* 26:   */     {
/* 27:58 */       executorService = Executors.newSingleThreadScheduledExecutor();
/* 28:   */       
/* 29:60 */       executorService.scheduleAtFixedRate(new Runnable()
/* 30:   */       {
/* 31:   */         public void run()
/* 32:   */         {
/* 33:63 */           SystemTimeKeeper.time.set(System.nanoTime() / 1000000L);
/* 34:   */         }
/* 35:63 */       }, 5L, 5L, TimeUnit.MILLISECONDS);
/* 36:   */     }
/* 37:68 */     Sys.timer.set(new SystemTimeKeeper());
/* 38:   */   }
/* 39:   */   
/* 40:   */   public static void shutDown()
/* 41:   */   {
/* 42:72 */     executorService.shutdown();
/* 43:   */   }
/* 44:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.SystemTimeKeeper
 * JD-Core Version:    0.7.0.1
 */