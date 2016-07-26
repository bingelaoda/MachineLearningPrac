/*  1:   */ package weka.python;
/*  2:   */ 
/*  3:   */ import java.io.PrintStream;
/*  4:   */ 
/*  5:   */ public class SessionMutex
/*  6:   */ {
/*  7:   */   private boolean m_verbose;
/*  8:   */   private boolean m_locked;
/*  9:   */   private Thread m_lockedBy;
/* 10:   */   
/* 11:   */   public SessionMutex() {}
/* 12:   */   
/* 13:   */   public SessionMutex(boolean verbose)
/* 14:   */   {
/* 15:44 */     this.m_verbose = verbose;
/* 16:   */   }
/* 17:   */   
/* 18:   */   private synchronized void lock()
/* 19:   */   {
/* 20:48 */     while (this.m_locked)
/* 21:   */     {
/* 22:49 */       if (this.m_lockedBy == Thread.currentThread()) {
/* 23:50 */         System.err.println("INFO: Mutex detected a deadlock! The application is likely to hang indefinitely!");
/* 24:   */       }
/* 25:54 */       if (this.m_verbose) {
/* 26:55 */         System.out.println("INFO: " + toString() + " is m_locked by " + this.m_lockedBy + ", but " + Thread.currentThread() + " waits for release");
/* 27:   */       }
/* 28:   */       try
/* 29:   */       {
/* 30:59 */         wait();
/* 31:   */       }
/* 32:   */       catch (InterruptedException e)
/* 33:   */       {
/* 34:61 */         if (this.m_verbose) {
/* 35:62 */           System.out.println("INFO: " + toString() + " caught InterruptedException");
/* 36:   */         }
/* 37:   */       }
/* 38:   */     }
/* 39:66 */     this.m_locked = true;
/* 40:67 */     this.m_lockedBy = Thread.currentThread();
/* 41:68 */     if (this.m_verbose) {
/* 42:69 */       System.out.println("INFO: " + toString() + " m_locked by " + this.m_lockedBy);
/* 43:   */     }
/* 44:   */   }
/* 45:   */   
/* 46:   */   public synchronized boolean safeLock()
/* 47:   */   {
/* 48:74 */     if ((this.m_locked) && (this.m_lockedBy == Thread.currentThread()))
/* 49:   */     {
/* 50:75 */       if (this.m_verbose) {
/* 51:76 */         System.out.println("INFO: " + toString() + " unable to provide safe lock for " + Thread.currentThread());
/* 52:   */       }
/* 53:79 */       return false;
/* 54:   */     }
/* 55:81 */     lock();
/* 56:82 */     return true;
/* 57:   */   }
/* 58:   */   
/* 59:   */   public synchronized void unlock()
/* 60:   */   {
/* 61:86 */     if ((this.m_locked) && (this.m_lockedBy != Thread.currentThread())) {
/* 62:87 */       System.err.println("WARNING: Mutex was unlocked by other thread");
/* 63:   */     }
/* 64:90 */     this.m_locked = false;
/* 65:91 */     if (this.m_verbose) {
/* 66:92 */       System.out.println("INFO: " + toString() + " unlocked by " + Thread.currentThread());
/* 67:   */     }
/* 68:97 */     notify();
/* 69:   */   }
/* 70:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.python.SessionMutex
 * JD-Core Version:    0.7.0.1
 */