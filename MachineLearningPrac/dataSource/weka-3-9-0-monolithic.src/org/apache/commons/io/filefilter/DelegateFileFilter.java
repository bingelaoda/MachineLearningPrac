/*  1:   */ package org.apache.commons.io.filefilter;
/*  2:   */ 
/*  3:   */ import java.io.File;
/*  4:   */ import java.io.FileFilter;
/*  5:   */ import java.io.FilenameFilter;
/*  6:   */ 
/*  7:   */ public class DelegateFileFilter
/*  8:   */   extends AbstractFileFilter
/*  9:   */ {
/* 10:   */   private FilenameFilter filenameFilter;
/* 11:   */   private FileFilter fileFilter;
/* 12:   */   
/* 13:   */   public DelegateFileFilter(FilenameFilter filter)
/* 14:   */   {
/* 15:44 */     if (filter == null) {
/* 16:45 */       throw new IllegalArgumentException("The FilenameFilter must not be null");
/* 17:   */     }
/* 18:47 */     this.filenameFilter = filter;
/* 19:   */   }
/* 20:   */   
/* 21:   */   public DelegateFileFilter(FileFilter filter)
/* 22:   */   {
/* 23:56 */     if (filter == null) {
/* 24:57 */       throw new IllegalArgumentException("The FileFilter must not be null");
/* 25:   */     }
/* 26:59 */     this.fileFilter = filter;
/* 27:   */   }
/* 28:   */   
/* 29:   */   public boolean accept(File file)
/* 30:   */   {
/* 31:69 */     if (this.fileFilter != null) {
/* 32:70 */       return this.fileFilter.accept(file);
/* 33:   */     }
/* 34:72 */     return super.accept(file);
/* 35:   */   }
/* 36:   */   
/* 37:   */   public boolean accept(File dir, String name)
/* 38:   */   {
/* 39:84 */     if (this.filenameFilter != null) {
/* 40:85 */       return this.filenameFilter.accept(dir, name);
/* 41:   */     }
/* 42:87 */     return super.accept(dir, name);
/* 43:   */   }
/* 44:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.io.filefilter.DelegateFileFilter
 * JD-Core Version:    0.7.0.1
 */