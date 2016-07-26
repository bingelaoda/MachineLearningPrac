/*   1:    */ package org.boon.concurrent;
/*   2:    */ 
/*   3:    */ import java.util.concurrent.Executors;
/*   4:    */ import java.util.concurrent.ScheduledExecutorService;
/*   5:    */ import java.util.concurrent.ScheduledFuture;
/*   6:    */ import java.util.concurrent.ThreadFactory;
/*   7:    */ import java.util.concurrent.TimeUnit;
/*   8:    */ import java.util.concurrent.atomic.AtomicLong;
/*   9:    */ import java.util.concurrent.atomic.AtomicReference;
/*  10:    */ import org.boon.Boon;
/*  11:    */ import org.boon.Logger;
/*  12:    */ import org.boon.core.Sys;
/*  13:    */ 
/*  14:    */ public class Timer
/*  15:    */ {
/*  16: 18 */   private Logger logger = Boon.configurableLogger(Timer.class);
/*  17: 20 */   private AtomicLong time = new AtomicLong(System.nanoTime() / 1000000L);
/*  18:    */   ScheduledExecutorService monitor;
/*  19:    */   ScheduledFuture<?> future;
/*  20: 27 */   private static AtomicReference<Timer> timeHolder = new AtomicReference();
/*  21:    */   
/*  22:    */   public static Timer timer()
/*  23:    */   {
/*  24: 30 */     if (timeHolder.get() == null) {
/*  25: 32 */       if (timeHolder.compareAndSet(timeHolder.get(), new Timer())) {
/*  26: 33 */         ((Timer)timeHolder.get()).start();
/*  27:    */       }
/*  28:    */     }
/*  29: 37 */     return (Timer)timeHolder.get();
/*  30:    */   }
/*  31:    */   
/*  32:    */   public void stop()
/*  33:    */   {
/*  34: 42 */     this.future.cancel(true);
/*  35: 43 */     this.monitor.shutdownNow();
/*  36: 44 */     this.monitor = null;
/*  37:    */   }
/*  38:    */   
/*  39:    */   private void start()
/*  40:    */   {
/*  41: 52 */     if (this.monitor == null) {
/*  42: 53 */       this.monitor = Executors.newScheduledThreadPool(1, new ThreadFactory()
/*  43:    */       {
/*  44:    */         public Thread newThread(Runnable runnable)
/*  45:    */         {
/*  46: 57 */           Thread thread = new Thread(runnable);
/*  47: 58 */           thread.setPriority(10);
/*  48: 59 */           thread.setName("Timer OutputQueue Manager");
/*  49: 60 */           return thread;
/*  50:    */         }
/*  51:    */       });
/*  52:    */     }
/*  53: 66 */     this.future = this.monitor.scheduleAtFixedRate(new Runnable()
/*  54:    */     {
/*  55:    */       public void run()
/*  56:    */       {
/*  57:    */         try
/*  58:    */         {
/*  59: 70 */           Timer.this.manageTimer();
/*  60:    */         }
/*  61:    */         catch (Exception ex)
/*  62:    */         {
/*  63: 72 */           Timer.this.logger.error(ex, new Object[] { "can't manage timeHolder" });
/*  64:    */         }
/*  65:    */       }
/*  66: 72 */     }, 50L, 50L, TimeUnit.MILLISECONDS);
/*  67:    */   }
/*  68:    */   
/*  69:    */   private void manageTimer()
/*  70:    */   {
/*  71: 84 */     int count = 0;
/*  72:    */     for (;;)
/*  73:    */     {
/*  74: 87 */       count++;
/*  75: 88 */       Sys.sleep(5L);
/*  76: 89 */       this.time.addAndGet(5L);
/*  77: 91 */       if (count > 100)
/*  78:    */       {
/*  79: 92 */         this.time.set(System.nanoTime() / 1000000L);
/*  80: 93 */         count = 0;
/*  81:    */       }
/*  82:    */     }
/*  83:    */   }
/*  84:    */   
/*  85:    */   public long time()
/*  86:    */   {
/*  87:101 */     return this.time.get();
/*  88:    */   }
/*  89:    */   
/*  90:    */   public long now()
/*  91:    */   {
/*  92:106 */     return this.time.get();
/*  93:    */   }
/*  94:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.concurrent.Timer
 * JD-Core Version:    0.7.0.1
 */