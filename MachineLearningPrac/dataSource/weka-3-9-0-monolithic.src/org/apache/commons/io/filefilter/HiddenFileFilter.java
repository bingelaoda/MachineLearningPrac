/*  1:   */ package org.apache.commons.io.filefilter;
/*  2:   */ 
/*  3:   */ import java.io.File;
/*  4:   */ 
/*  5:   */ public class HiddenFileFilter
/*  6:   */   extends AbstractFileFilter
/*  7:   */ {
/*  8:53 */   public static final IOFileFilter HIDDEN = new HiddenFileFilter();
/*  9:56 */   public static final IOFileFilter VISIBLE = new NotFileFilter(HIDDEN);
/* 10:   */   
/* 11:   */   public boolean accept(File file)
/* 12:   */   {
/* 13:72 */     return file.isHidden();
/* 14:   */   }
/* 15:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.io.filefilter.HiddenFileFilter
 * JD-Core Version:    0.7.0.1
 */