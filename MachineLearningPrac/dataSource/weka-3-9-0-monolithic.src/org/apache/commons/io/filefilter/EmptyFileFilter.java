/*  1:   */ package org.apache.commons.io.filefilter;
/*  2:   */ 
/*  3:   */ import java.io.File;
/*  4:   */ 
/*  5:   */ public class EmptyFileFilter
/*  6:   */   extends AbstractFileFilter
/*  7:   */ {
/*  8:56 */   public static final IOFileFilter EMPTY = new EmptyFileFilter();
/*  9:59 */   public static final IOFileFilter NOT_EMPTY = new NotFileFilter(EMPTY);
/* 10:   */   
/* 11:   */   public boolean accept(File file)
/* 12:   */   {
/* 13:75 */     if (file.isDirectory())
/* 14:   */     {
/* 15:76 */       File[] files = file.listFiles();
/* 16:77 */       return (files == null) || (files.length == 0);
/* 17:   */     }
/* 18:79 */     return file.length() == 0L;
/* 19:   */   }
/* 20:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.io.filefilter.EmptyFileFilter
 * JD-Core Version:    0.7.0.1
 */