/*   1:    */ package org.apache.commons.io;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ 
/*   5:    */ public class FileCleaner
/*   6:    */ {
/*   7: 43 */   static final FileCleaningTracker theInstance = new FileCleaningTracker();
/*   8:    */   
/*   9:    */   public static void track(File file, Object marker)
/*  10:    */   {
/*  11: 56 */     theInstance.track(file, marker);
/*  12:    */   }
/*  13:    */   
/*  14:    */   public static void track(File file, Object marker, FileDeleteStrategy deleteStrategy)
/*  15:    */   {
/*  16: 70 */     theInstance.track(file, marker, deleteStrategy);
/*  17:    */   }
/*  18:    */   
/*  19:    */   public static void track(String path, Object marker)
/*  20:    */   {
/*  21: 83 */     theInstance.track(path, marker);
/*  22:    */   }
/*  23:    */   
/*  24:    */   public static void track(String path, Object marker, FileDeleteStrategy deleteStrategy)
/*  25:    */   {
/*  26: 97 */     theInstance.track(path, marker, deleteStrategy);
/*  27:    */   }
/*  28:    */   
/*  29:    */   public static int getTrackCount()
/*  30:    */   {
/*  31:108 */     return theInstance.getTrackCount();
/*  32:    */   }
/*  33:    */   
/*  34:    */   public static synchronized void exitWhenFinished()
/*  35:    */   {
/*  36:133 */     theInstance.exitWhenFinished();
/*  37:    */   }
/*  38:    */   
/*  39:    */   public static FileCleaningTracker getInstance()
/*  40:    */   {
/*  41:143 */     return theInstance;
/*  42:    */   }
/*  43:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.io.FileCleaner
 * JD-Core Version:    0.7.0.1
 */