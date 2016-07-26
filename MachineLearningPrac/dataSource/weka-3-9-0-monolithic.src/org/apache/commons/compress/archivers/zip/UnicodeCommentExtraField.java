/*  1:   */ package org.apache.commons.compress.archivers.zip;
/*  2:   */ 
/*  3:   */ public class UnicodeCommentExtraField
/*  4:   */   extends AbstractUnicodeExtraField
/*  5:   */ {
/*  6:34 */   public static final ZipShort UCOM_ID = new ZipShort(25461);
/*  7:   */   
/*  8:   */   public UnicodeCommentExtraField() {}
/*  9:   */   
/* 10:   */   public UnicodeCommentExtraField(String text, byte[] bytes, int off, int len)
/* 11:   */   {
/* 12:51 */     super(text, bytes, off, len);
/* 13:   */   }
/* 14:   */   
/* 15:   */   public UnicodeCommentExtraField(String comment, byte[] bytes)
/* 16:   */   {
/* 17:62 */     super(comment, bytes);
/* 18:   */   }
/* 19:   */   
/* 20:   */   public ZipShort getHeaderId()
/* 21:   */   {
/* 22:66 */     return UCOM_ID;
/* 23:   */   }
/* 24:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.UnicodeCommentExtraField
 * JD-Core Version:    0.7.0.1
 */