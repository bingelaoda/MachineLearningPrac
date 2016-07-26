/*  1:   */ package org.apache.commons.io.filefilter;
/*  2:   */ 
/*  3:   */ import java.io.File;
/*  4:   */ 
/*  5:   */ public class CanWriteFileFilter
/*  6:   */   extends AbstractFileFilter
/*  7:   */ {
/*  8:57 */   public static final IOFileFilter CAN_WRITE = new CanWriteFileFilter();
/*  9:60 */   public static final IOFileFilter CANNOT_WRITE = new NotFileFilter(CAN_WRITE);
/* 10:   */   
/* 11:   */   public boolean accept(File file)
/* 12:   */   {
/* 13:76 */     return file.canWrite();
/* 14:   */   }
/* 15:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.io.filefilter.CanWriteFileFilter
 * JD-Core Version:    0.7.0.1
 */