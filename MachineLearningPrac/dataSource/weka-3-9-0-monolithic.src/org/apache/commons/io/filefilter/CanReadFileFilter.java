/*  1:   */ package org.apache.commons.io.filefilter;
/*  2:   */ 
/*  3:   */ import java.io.File;
/*  4:   */ 
/*  5:   */ public class CanReadFileFilter
/*  6:   */   extends AbstractFileFilter
/*  7:   */ {
/*  8:65 */   public static final IOFileFilter CAN_READ = new CanReadFileFilter();
/*  9:68 */   public static final IOFileFilter CANNOT_READ = new NotFileFilter(CAN_READ);
/* 10:71 */   public static final IOFileFilter READ_ONLY = new AndFileFilter(CAN_READ, CanWriteFileFilter.CANNOT_WRITE);
/* 11:   */   
/* 12:   */   public boolean accept(File file)
/* 13:   */   {
/* 14:88 */     return file.canRead();
/* 15:   */   }
/* 16:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.io.filefilter.CanReadFileFilter
 * JD-Core Version:    0.7.0.1
 */