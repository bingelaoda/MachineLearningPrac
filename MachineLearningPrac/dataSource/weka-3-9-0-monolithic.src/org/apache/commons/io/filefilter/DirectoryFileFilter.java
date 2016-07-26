/*  1:   */ package org.apache.commons.io.filefilter;
/*  2:   */ 
/*  3:   */ import java.io.File;
/*  4:   */ 
/*  5:   */ public class DirectoryFileFilter
/*  6:   */   extends AbstractFileFilter
/*  7:   */ {
/*  8:47 */   public static final IOFileFilter DIRECTORY = new DirectoryFileFilter();
/*  9:54 */   public static final IOFileFilter INSTANCE = DIRECTORY;
/* 10:   */   
/* 11:   */   public boolean accept(File file)
/* 12:   */   {
/* 13:69 */     return file.isDirectory();
/* 14:   */   }
/* 15:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.io.filefilter.DirectoryFileFilter
 * JD-Core Version:    0.7.0.1
 */