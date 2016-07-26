/*   1:    */ package org.apache.commons.compress.archivers.dump;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import org.apache.commons.compress.archivers.zip.ZipEncoding;
/*   5:    */ 
/*   6:    */ class DumpArchiveUtil
/*   7:    */ {
/*   8:    */   public static int calculateChecksum(byte[] buffer)
/*   9:    */   {
/*  10: 41 */     int calc = 0;
/*  11: 43 */     for (int i = 0; i < 256; i++) {
/*  12: 44 */       calc += convert32(buffer, 4 * i);
/*  13:    */     }
/*  14: 47 */     return 84446 - (calc - convert32(buffer, 28));
/*  15:    */   }
/*  16:    */   
/*  17:    */   public static final boolean verify(byte[] buffer)
/*  18:    */   {
/*  19: 58 */     int magic = convert32(buffer, 24);
/*  20: 60 */     if (magic != 60012) {
/*  21: 61 */       return false;
/*  22:    */     }
/*  23: 65 */     int checksum = convert32(buffer, 28);
/*  24: 67 */     if (checksum != calculateChecksum(buffer)) {
/*  25: 68 */       return false;
/*  26:    */     }
/*  27: 71 */     return true;
/*  28:    */   }
/*  29:    */   
/*  30:    */   public static final int getIno(byte[] buffer)
/*  31:    */   {
/*  32: 80 */     return convert32(buffer, 20);
/*  33:    */   }
/*  34:    */   
/*  35:    */   public static final long convert64(byte[] buffer, int offset)
/*  36:    */   {
/*  37: 91 */     long i = 0L;
/*  38: 92 */     i += (buffer[(offset + 7)] << 56);
/*  39: 93 */     i += (buffer[(offset + 6)] << 48 & 0x0);
/*  40: 94 */     i += (buffer[(offset + 5)] << 40 & 0x0);
/*  41: 95 */     i += (buffer[(offset + 4)] << 32 & 0x0);
/*  42: 96 */     i += (buffer[(offset + 3)] << 24 & 0xFF000000);
/*  43: 97 */     i += (buffer[(offset + 2)] << 16 & 0xFF0000);
/*  44: 98 */     i += (buffer[(offset + 1)] << 8 & 0xFF00);
/*  45: 99 */     i += (buffer[offset] & 0xFF);
/*  46:    */     
/*  47:101 */     return i;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public static final int convert32(byte[] buffer, int offset)
/*  51:    */   {
/*  52:112 */     int i = 0;
/*  53:113 */     i = buffer[(offset + 3)] << 24;
/*  54:114 */     i += (buffer[(offset + 2)] << 16 & 0xFF0000);
/*  55:115 */     i += (buffer[(offset + 1)] << 8 & 0xFF00);
/*  56:116 */     i += (buffer[offset] & 0xFF);
/*  57:    */     
/*  58:118 */     return i;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public static final int convert16(byte[] buffer, int offset)
/*  62:    */   {
/*  63:129 */     int i = 0;
/*  64:130 */     i += (buffer[(offset + 1)] << 8 & 0xFF00);
/*  65:131 */     i += (buffer[offset] & 0xFF);
/*  66:    */     
/*  67:133 */     return i;
/*  68:    */   }
/*  69:    */   
/*  70:    */   static String decode(ZipEncoding encoding, byte[] b, int offset, int len)
/*  71:    */     throws IOException
/*  72:    */   {
/*  73:141 */     byte[] copy = new byte[len];
/*  74:142 */     System.arraycopy(b, offset, copy, 0, len);
/*  75:143 */     return encoding.decode(copy);
/*  76:    */   }
/*  77:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.dump.DumpArchiveUtil
 * JD-Core Version:    0.7.0.1
 */