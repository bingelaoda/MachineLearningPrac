/*  1:   */ package org.apache.commons.io.filefilter;
/*  2:   */ 
/*  3:   */ import java.io.File;
/*  4:   */ 
/*  5:   */ public abstract class AbstractFileFilter
/*  6:   */   implements IOFileFilter
/*  7:   */ {
/*  8:   */   public boolean accept(File file)
/*  9:   */   {
/* 10:42 */     return accept(file.getParentFile(), file.getName());
/* 11:   */   }
/* 12:   */   
/* 13:   */   public boolean accept(File dir, String name)
/* 14:   */   {
/* 15:53 */     return accept(new File(dir, name));
/* 16:   */   }
/* 17:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.io.filefilter.AbstractFileFilter
 * JD-Core Version:    0.7.0.1
 */