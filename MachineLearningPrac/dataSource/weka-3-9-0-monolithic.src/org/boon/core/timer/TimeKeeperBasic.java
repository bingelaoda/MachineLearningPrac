/*   1:    */ package org.boon.core.timer;
/*   2:    */ 
/*   3:    */ import java.util.concurrent.atomic.AtomicInteger;
/*   4:    */ import java.util.concurrent.atomic.AtomicLong;
/*   5:    */ import java.util.concurrent.locks.ReentrantLock;
/*   6:    */ import org.boon.core.Sys;
/*   7:    */ 
/*   8:    */ public class TimeKeeperBasic
/*   9:    */   implements TimeKeeper
/*  10:    */ {
/*  11: 50 */   private final AtomicInteger callEveryNowAndThen = new AtomicInteger();
/*  12: 51 */   private final AtomicLong time = new AtomicLong();
/*  13: 52 */   private final int TIME_KEEPER_FREQUENCY = Sys.sysProp("org.boon.timekeeper.frequency", 100);
/*  14: 54 */   private final ReentrantLock lock = new ReentrantLock();
/*  15: 56 */   private final AtomicLong lastDeltaTime = new AtomicLong();
/*  16:    */   
/*  17:    */   public final long time()
/*  18:    */   {
/*  19: 62 */     long limit = this.callEveryNowAndThen.incrementAndGet();
/*  20:    */     
/*  21: 64 */     boolean shouldGetTime = false;
/*  22: 66 */     if (limit > this.TIME_KEEPER_FREQUENCY)
/*  23:    */     {
/*  24: 67 */       this.callEveryNowAndThen.set(0);
/*  25: 68 */       shouldGetTime = true;
/*  26:    */     }
/*  27: 73 */     long time = this.time.get() + limit;
/*  28: 75 */     if ((!shouldGetTime) && (limit % 20L == 0L)) {
/*  29: 76 */       checkForDrift(time);
/*  30:    */     }
/*  31: 79 */     return time;
/*  32:    */   }
/*  33:    */   
/*  34:    */   private long checkForDrift(long time)
/*  35:    */   {
/*  36: 85 */     long delta = Math.abs(System.currentTimeMillis() - time);
/*  37: 86 */     long lastDelta = this.lastDeltaTime.getAndSet(delta);
/*  38: 87 */     if (delta > lastDelta + 200L) {
/*  39: 88 */       return getTheTime(time);
/*  40:    */     }
/*  41: 90 */     return time;
/*  42:    */   }
/*  43:    */   
/*  44:    */   private long getTheTime(long time)
/*  45:    */   {
/*  46: 94 */     boolean locked = this.lock.tryLock();
/*  47: 95 */     if (locked) {
/*  48:    */       try
/*  49:    */       {
/*  50: 98 */         time = System.nanoTime() / 1000000L;
/*  51: 99 */         this.time.set(time);
/*  52:100 */         return time;
/*  53:    */       }
/*  54:    */       finally
/*  55:    */       {
/*  56:103 */         this.lock.unlock();
/*  57:    */       }
/*  58:    */     }
/*  59:106 */     return time;
/*  60:    */   }
/*  61:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.timer.TimeKeeperBasic
 * JD-Core Version:    0.7.0.1
 */