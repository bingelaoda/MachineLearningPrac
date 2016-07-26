/*   1:    */ package org.apache.commons.compress.archivers.cpio;
/*   2:    */ 
/*   3:    */ class CpioUtil
/*   4:    */ {
/*   5:    */   static long fileType(long mode)
/*   6:    */   {
/*   7: 32 */     return mode & 0xF000;
/*   8:    */   }
/*   9:    */   
/*  10:    */   static long byteArray2long(byte[] number, boolean swapHalfWord)
/*  11:    */   {
/*  12: 47 */     if (number.length % 2 != 0) {
/*  13: 48 */       throw new UnsupportedOperationException();
/*  14:    */     }
/*  15: 51 */     long ret = 0L;
/*  16: 52 */     int pos = 0;
/*  17: 53 */     byte[] tmp_number = new byte[number.length];
/*  18: 54 */     System.arraycopy(number, 0, tmp_number, 0, number.length);
/*  19: 56 */     if (!swapHalfWord)
/*  20:    */     {
/*  21: 57 */       byte tmp = 0;
/*  22: 58 */       for (pos = 0; pos < tmp_number.length; pos++)
/*  23:    */       {
/*  24: 59 */         tmp = tmp_number[pos];
/*  25: 60 */         tmp_number[(pos++)] = tmp_number[pos];
/*  26: 61 */         tmp_number[pos] = tmp;
/*  27:    */       }
/*  28:    */     }
/*  29: 65 */     ret = tmp_number[0] & 0xFF;
/*  30: 66 */     for (pos = 1; pos < tmp_number.length; pos++)
/*  31:    */     {
/*  32: 67 */       ret <<= 8;
/*  33: 68 */       ret |= tmp_number[pos] & 0xFF;
/*  34:    */     }
/*  35: 70 */     return ret;
/*  36:    */   }
/*  37:    */   
/*  38:    */   static byte[] long2byteArray(long number, int length, boolean swapHalfWord)
/*  39:    */   {
/*  40: 89 */     byte[] ret = new byte[length];
/*  41: 90 */     int pos = 0;
/*  42: 91 */     long tmp_number = 0L;
/*  43: 93 */     if ((length % 2 != 0) || (length < 2)) {
/*  44: 94 */       throw new UnsupportedOperationException();
/*  45:    */     }
/*  46: 97 */     tmp_number = number;
/*  47: 98 */     for (pos = length - 1; pos >= 0; pos--)
/*  48:    */     {
/*  49: 99 */       ret[pos] = ((byte)(int)(tmp_number & 0xFF));
/*  50:100 */       tmp_number >>= 8;
/*  51:    */     }
/*  52:103 */     if (!swapHalfWord)
/*  53:    */     {
/*  54:104 */       byte tmp = 0;
/*  55:105 */       for (pos = 0; pos < length; pos++)
/*  56:    */       {
/*  57:106 */         tmp = ret[pos];
/*  58:107 */         ret[(pos++)] = ret[pos];
/*  59:108 */         ret[pos] = tmp;
/*  60:    */       }
/*  61:    */     }
/*  62:112 */     return ret;
/*  63:    */   }
/*  64:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.cpio.CpioUtil
 * JD-Core Version:    0.7.0.1
 */