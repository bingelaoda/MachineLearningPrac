/*  1:   */ package org.boon.concurrent;
/*  2:   */ 
/*  3:   */ import java.util.concurrent.atomic.AtomicLong;
/*  4:   */ 
/*  5:   */ public class Counter
/*  6:   */ {
/*  7:11 */   private AtomicLong count = new AtomicLong();
/*  8:12 */   long _count = 0L;
/*  9:   */   
/* 10:   */   public void increment()
/* 11:   */   {
/* 12:16 */     long __count = this._count;
/* 13:18 */     if (__count >= 100L)
/* 14:   */     {
/* 15:19 */       this.count.addAndGet(__count);
/* 16:20 */       __count = 0L;
/* 17:   */     }
/* 18:23 */     __count += 1L;
/* 19:   */     
/* 20:25 */     this._count = __count;
/* 21:   */   }
/* 22:   */   
/* 23:   */   public long incrementAndGet()
/* 24:   */   {
/* 25:29 */     long __count = this._count;
/* 26:31 */     if (__count >= 100L)
/* 27:   */     {
/* 28:32 */       this.count.addAndGet(__count);
/* 29:33 */       __count = 0L;
/* 30:   */     }
/* 31:36 */     __count += 1L;
/* 32:   */     
/* 33:38 */     this._count = __count;
/* 34:39 */     return this._count;
/* 35:   */   }
/* 36:   */   
/* 37:   */   public void add(int size)
/* 38:   */   {
/* 39:43 */     long __count = this._count;
/* 40:   */     
/* 41:   */ 
/* 42:   */ 
/* 43:47 */     __count += size;
/* 44:49 */     if (__count >= 1000L)
/* 45:   */     {
/* 46:50 */       this.count.addAndGet(__count);
/* 47:51 */       __count = 0L;
/* 48:   */     }
/* 49:54 */     this._count = __count;
/* 50:   */   }
/* 51:   */   
/* 52:   */   public long addAndGet(int size)
/* 53:   */   {
/* 54:60 */     long __count = this._count;
/* 55:   */     
/* 56:   */ 
/* 57:   */ 
/* 58:64 */     __count += size;
/* 59:66 */     if (__count >= 1000L)
/* 60:   */     {
/* 61:67 */       this.count.addAndGet(__count);
/* 62:68 */       __count = 0L;
/* 63:   */     }
/* 64:71 */     this._count = __count;
/* 65:   */     
/* 66:73 */     return this._count;
/* 67:   */   }
/* 68:   */   
/* 69:   */   public synchronized void reset()
/* 70:   */   {
/* 71:78 */     this._count = 0L;
/* 72:79 */     this.count.set(0L);
/* 73:   */   }
/* 74:   */   
/* 75:   */   public synchronized long get()
/* 76:   */   {
/* 77:85 */     this.count.addAndGet(this._count);
/* 78:86 */     this._count = 0L;
/* 79:87 */     return this.count.get();
/* 80:   */   }
/* 81:   */   
/* 82:   */   public synchronized String toString()
/* 83:   */   {
/* 84:93 */     return "" + get();
/* 85:   */   }
/* 86:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.concurrent.Counter
 * JD-Core Version:    0.7.0.1
 */