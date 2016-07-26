/*   1:    */ package org.apache.commons.io;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.lang.ref.PhantomReference;
/*   5:    */ import java.lang.ref.ReferenceQueue;
/*   6:    */ import java.util.Collection;
/*   7:    */ import java.util.Vector;
/*   8:    */ 
/*   9:    */ public class FileCleaningTracker
/*  10:    */ {
/*  11:    */   ReferenceQueue q;
/*  12:    */   final Collection trackers;
/*  13:    */   volatile boolean exitWhenFinished;
/*  14:    */   Thread reaper;
/*  15:    */   
/*  16:    */   public FileCleaningTracker()
/*  17:    */   {
/*  18: 47 */     this.q = new ReferenceQueue();
/*  19:    */     
/*  20:    */ 
/*  21:    */ 
/*  22: 51 */     this.trackers = new Vector();
/*  23:    */     
/*  24:    */ 
/*  25:    */ 
/*  26: 55 */     this.exitWhenFinished = false;
/*  27:    */   }
/*  28:    */   
/*  29:    */   public void track(File file, Object marker)
/*  30:    */   {
/*  31: 72 */     track(file, marker, (FileDeleteStrategy)null);
/*  32:    */   }
/*  33:    */   
/*  34:    */   public void track(File file, Object marker, FileDeleteStrategy deleteStrategy)
/*  35:    */   {
/*  36: 86 */     if (file == null) {
/*  37: 87 */       throw new NullPointerException("The file must not be null");
/*  38:    */     }
/*  39: 89 */     addTracker(file.getPath(), marker, deleteStrategy);
/*  40:    */   }
/*  41:    */   
/*  42:    */   public void track(String path, Object marker)
/*  43:    */   {
/*  44:102 */     track(path, marker, (FileDeleteStrategy)null);
/*  45:    */   }
/*  46:    */   
/*  47:    */   public void track(String path, Object marker, FileDeleteStrategy deleteStrategy)
/*  48:    */   {
/*  49:116 */     if (path == null) {
/*  50:117 */       throw new NullPointerException("The path must not be null");
/*  51:    */     }
/*  52:119 */     addTracker(path, marker, deleteStrategy);
/*  53:    */   }
/*  54:    */   
/*  55:    */   private synchronized void addTracker(String path, Object marker, FileDeleteStrategy deleteStrategy)
/*  56:    */   {
/*  57:131 */     if (this.exitWhenFinished) {
/*  58:132 */       throw new IllegalStateException("No new trackers can be added once exitWhenFinished() is called");
/*  59:    */     }
/*  60:134 */     if (this.reaper == null)
/*  61:    */     {
/*  62:135 */       this.reaper = new Reaper();
/*  63:136 */       this.reaper.start();
/*  64:    */     }
/*  65:138 */     this.trackers.add(new Tracker(path, deleteStrategy, marker, this.q));
/*  66:    */   }
/*  67:    */   
/*  68:    */   public int getTrackCount()
/*  69:    */   {
/*  70:149 */     return this.trackers.size();
/*  71:    */   }
/*  72:    */   
/*  73:    */   public synchronized void exitWhenFinished()
/*  74:    */   {
/*  75:175 */     this.exitWhenFinished = true;
/*  76:176 */     if (this.reaper != null) {
/*  77:177 */       synchronized (this.reaper)
/*  78:    */       {
/*  79:178 */         this.reaper.interrupt();
/*  80:    */       }
/*  81:    */     }
/*  82:    */   }
/*  83:    */   
/*  84:    */   private final class Reaper
/*  85:    */     extends Thread
/*  86:    */   {
/*  87:    */     Reaper()
/*  88:    */     {
/*  89:190 */       super();
/*  90:191 */       setPriority(10);
/*  91:192 */       setDaemon(true);
/*  92:    */     }
/*  93:    */     
/*  94:    */     public void run()
/*  95:    */     {
/*  96:201 */       while ((!FileCleaningTracker.this.exitWhenFinished) || (FileCleaningTracker.this.trackers.size() > 0))
/*  97:    */       {
/*  98:202 */         FileCleaningTracker.Tracker tracker = null;
/*  99:    */         try
/* 100:    */         {
/* 101:205 */           tracker = (FileCleaningTracker.Tracker)FileCleaningTracker.this.q.remove();
/* 102:    */         }
/* 103:    */         catch (Exception e) {}
/* 104:207 */         continue;
/* 105:209 */         if (tracker != null)
/* 106:    */         {
/* 107:210 */           tracker.delete();
/* 108:211 */           tracker.clear();
/* 109:212 */           FileCleaningTracker.this.trackers.remove(tracker);
/* 110:    */         }
/* 111:    */       }
/* 112:    */     }
/* 113:    */   }
/* 114:    */   
/* 115:    */   private static final class Tracker
/* 116:    */     extends PhantomReference
/* 117:    */   {
/* 118:    */     private final String path;
/* 119:    */     private final FileDeleteStrategy deleteStrategy;
/* 120:    */     
/* 121:    */     Tracker(String path, FileDeleteStrategy deleteStrategy, Object marker, ReferenceQueue queue)
/* 122:    */     {
/* 123:242 */       super(queue);
/* 124:243 */       this.path = path;
/* 125:244 */       this.deleteStrategy = (deleteStrategy == null ? FileDeleteStrategy.NORMAL : deleteStrategy);
/* 126:    */     }
/* 127:    */     
/* 128:    */     public boolean delete()
/* 129:    */     {
/* 130:254 */       return this.deleteStrategy.deleteQuietly(new File(this.path));
/* 131:    */     }
/* 132:    */   }
/* 133:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.io.FileCleaningTracker
 * JD-Core Version:    0.7.0.1
 */