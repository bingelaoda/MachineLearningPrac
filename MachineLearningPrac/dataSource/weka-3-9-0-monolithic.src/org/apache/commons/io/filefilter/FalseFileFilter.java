/*  1:   */ package org.apache.commons.io.filefilter;
/*  2:   */ 
/*  3:   */ import java.io.File;
/*  4:   */ 
/*  5:   */ public class FalseFileFilter
/*  6:   */   implements IOFileFilter
/*  7:   */ {
/*  8:35 */   public static final IOFileFilter FALSE = new FalseFileFilter();
/*  9:42 */   public static final IOFileFilter INSTANCE = FALSE;
/* 10:   */   
/* 11:   */   public boolean accept(File file)
/* 12:   */   {
/* 13:57 */     return false;
/* 14:   */   }
/* 15:   */   
/* 16:   */   public boolean accept(File dir, String name)
/* 17:   */   {
/* 18:68 */     return false;
/* 19:   */   }
/* 20:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.io.filefilter.FalseFileFilter
 * JD-Core Version:    0.7.0.1
 */