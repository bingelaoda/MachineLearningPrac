/*  1:   */ package org.apache.commons.io.filefilter;
/*  2:   */ 
/*  3:   */ import java.io.File;
/*  4:   */ 
/*  5:   */ public class SizeFileFilter
/*  6:   */   extends AbstractFileFilter
/*  7:   */ {
/*  8:   */   private long size;
/*  9:   */   private boolean acceptLarger;
/* 10:   */   
/* 11:   */   public SizeFileFilter(long size)
/* 12:   */   {
/* 13:55 */     this(size, true);
/* 14:   */   }
/* 15:   */   
/* 16:   */   public SizeFileFilter(long size, boolean acceptLarger)
/* 17:   */   {
/* 18:68 */     if (size < 0L) {
/* 19:69 */       throw new IllegalArgumentException("The size must be non-negative");
/* 20:   */     }
/* 21:71 */     this.size = size;
/* 22:72 */     this.acceptLarger = acceptLarger;
/* 23:   */   }
/* 24:   */   
/* 25:   */   public boolean accept(File file)
/* 26:   */   {
/* 27:88 */     boolean smaller = file.length() < this.size;
/* 28:89 */     return this.acceptLarger ? false : !smaller ? true : smaller;
/* 29:   */   }
/* 30:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.io.filefilter.SizeFileFilter
 * JD-Core Version:    0.7.0.1
 */