/*  1:   */ package org.apache.commons.compress.archivers.zip;
/*  2:   */ 
/*  3:   */ import java.io.IOException;
/*  4:   */ import java.nio.ByteBuffer;
/*  5:   */ 
/*  6:   */ class FallbackZipEncoding
/*  7:   */   implements ZipEncoding
/*  8:   */ {
/*  9:   */   private final String charsetName;
/* 10:   */   
/* 11:   */   public FallbackZipEncoding()
/* 12:   */   {
/* 13:51 */     this.charsetName = null;
/* 14:   */   }
/* 15:   */   
/* 16:   */   public FallbackZipEncoding(String charsetName)
/* 17:   */   {
/* 18:61 */     this.charsetName = charsetName;
/* 19:   */   }
/* 20:   */   
/* 21:   */   public boolean canEncode(String name)
/* 22:   */   {
/* 23:69 */     return true;
/* 24:   */   }
/* 25:   */   
/* 26:   */   public ByteBuffer encode(String name)
/* 27:   */     throws IOException
/* 28:   */   {
/* 29:77 */     if (this.charsetName == null) {
/* 30:78 */       return ByteBuffer.wrap(name.getBytes());
/* 31:   */     }
/* 32:80 */     return ByteBuffer.wrap(name.getBytes(this.charsetName));
/* 33:   */   }
/* 34:   */   
/* 35:   */   public String decode(byte[] data)
/* 36:   */     throws IOException
/* 37:   */   {
/* 38:89 */     if (this.charsetName == null) {
/* 39:90 */       return new String(data);
/* 40:   */     }
/* 41:92 */     return new String(data, this.charsetName);
/* 42:   */   }
/* 43:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.FallbackZipEncoding
 * JD-Core Version:    0.7.0.1
 */