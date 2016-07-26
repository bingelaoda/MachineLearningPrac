/*  1:   */ package org.apache.commons.compress.archivers.zip;
/*  2:   */ 
/*  3:   */ public class UnicodePathExtraField
/*  4:   */   extends AbstractUnicodeExtraField
/*  5:   */ {
/*  6:34 */   public static final ZipShort UPATH_ID = new ZipShort(28789);
/*  7:   */   
/*  8:   */   public UnicodePathExtraField() {}
/*  9:   */   
/* 10:   */   public UnicodePathExtraField(String text, byte[] bytes, int off, int len)
/* 11:   */   {
/* 12:50 */     super(text, bytes, off, len);
/* 13:   */   }
/* 14:   */   
/* 15:   */   public UnicodePathExtraField(String name, byte[] bytes)
/* 16:   */   {
/* 17:61 */     super(name, bytes);
/* 18:   */   }
/* 19:   */   
/* 20:   */   public ZipShort getHeaderId()
/* 21:   */   {
/* 22:65 */     return UPATH_ID;
/* 23:   */   }
/* 24:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.UnicodePathExtraField
 * JD-Core Version:    0.7.0.1
 */