/*  1:   */ package org.apache.commons.io.filefilter;
/*  2:   */ 
/*  3:   */ import java.io.File;
/*  4:   */ 
/*  5:   */ public class FileFileFilter
/*  6:   */   extends AbstractFileFilter
/*  7:   */ {
/*  8:41 */   public static final IOFileFilter FILE = new FileFileFilter();
/*  9:   */   
/* 10:   */   public boolean accept(File file)
/* 11:   */   {
/* 12:56 */     return file.isFile();
/* 13:   */   }
/* 14:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.io.filefilter.FileFileFilter
 * JD-Core Version:    0.7.0.1
 */