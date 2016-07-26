/*  1:   */ package org.apache.commons.io.filefilter;
/*  2:   */ 
/*  3:   */ import java.io.File;
/*  4:   */ 
/*  5:   */ public class NotFileFilter
/*  6:   */   extends AbstractFileFilter
/*  7:   */ {
/*  8:   */   private IOFileFilter filter;
/*  9:   */   
/* 10:   */   public NotFileFilter(IOFileFilter filter)
/* 11:   */   {
/* 12:41 */     if (filter == null) {
/* 13:42 */       throw new IllegalArgumentException("The filter must not be null");
/* 14:   */     }
/* 15:44 */     this.filter = filter;
/* 16:   */   }
/* 17:   */   
/* 18:   */   public boolean accept(File file)
/* 19:   */   {
/* 20:54 */     return !this.filter.accept(file);
/* 21:   */   }
/* 22:   */   
/* 23:   */   public boolean accept(File file, String name)
/* 24:   */   {
/* 25:65 */     return !this.filter.accept(file, name);
/* 26:   */   }
/* 27:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.io.filefilter.NotFileFilter
 * JD-Core Version:    0.7.0.1
 */