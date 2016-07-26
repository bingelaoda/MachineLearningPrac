/*   1:    */ package org.apache.commons.compress.utils;
/*   2:    */ 
/*   3:    */ import java.io.UnsupportedEncodingException;
/*   4:    */ import org.apache.commons.compress.archivers.ArchiveEntry;
/*   5:    */ 
/*   6:    */ public class ArchiveUtils
/*   7:    */ {
/*   8:    */   public static String toString(ArchiveEntry entry)
/*   9:    */   {
/*  10: 47 */     StringBuilder sb = new StringBuilder();
/*  11: 48 */     sb.append(entry.isDirectory() ? 'd' : '-');
/*  12: 49 */     String size = Long.toString(entry.getSize());
/*  13: 50 */     sb.append(' ');
/*  14: 52 */     for (int i = 7; i > size.length(); i--) {
/*  15: 53 */       sb.append(' ');
/*  16:    */     }
/*  17: 55 */     sb.append(size);
/*  18: 56 */     sb.append(' ').append(entry.getName());
/*  19: 57 */     return sb.toString();
/*  20:    */   }
/*  21:    */   
/*  22:    */   public static boolean matchAsciiBuffer(String expected, byte[] buffer, int offset, int length)
/*  23:    */   {
/*  24:    */     byte[] buffer1;
/*  25:    */     try
/*  26:    */     {
/*  27: 73 */       buffer1 = expected.getBytes("US-ASCII");
/*  28:    */     }
/*  29:    */     catch (UnsupportedEncodingException e)
/*  30:    */     {
/*  31: 75 */       throw new RuntimeException(e);
/*  32:    */     }
/*  33: 77 */     return isEqual(buffer1, 0, buffer1.length, buffer, offset, length, false);
/*  34:    */   }
/*  35:    */   
/*  36:    */   public static boolean matchAsciiBuffer(String expected, byte[] buffer)
/*  37:    */   {
/*  38: 88 */     return matchAsciiBuffer(expected, buffer, 0, buffer.length);
/*  39:    */   }
/*  40:    */   
/*  41:    */   public static byte[] toAsciiBytes(String inputString)
/*  42:    */   {
/*  43:    */     try
/*  44:    */     {
/*  45:100 */       return inputString.getBytes("US-ASCII");
/*  46:    */     }
/*  47:    */     catch (UnsupportedEncodingException e)
/*  48:    */     {
/*  49:102 */       throw new RuntimeException(e);
/*  50:    */     }
/*  51:    */   }
/*  52:    */   
/*  53:    */   public static String toAsciiString(byte[] inputBytes)
/*  54:    */   {
/*  55:    */     try
/*  56:    */     {
/*  57:114 */       return new String(inputBytes, "US-ASCII");
/*  58:    */     }
/*  59:    */     catch (UnsupportedEncodingException e)
/*  60:    */     {
/*  61:116 */       throw new RuntimeException(e);
/*  62:    */     }
/*  63:    */   }
/*  64:    */   
/*  65:    */   public static String toAsciiString(byte[] inputBytes, int offset, int length)
/*  66:    */   {
/*  67:    */     try
/*  68:    */     {
/*  69:130 */       return new String(inputBytes, offset, length, "US-ASCII");
/*  70:    */     }
/*  71:    */     catch (UnsupportedEncodingException e)
/*  72:    */     {
/*  73:132 */       throw new RuntimeException(e);
/*  74:    */     }
/*  75:    */   }
/*  76:    */   
/*  77:    */   public static boolean isEqual(byte[] buffer1, int offset1, int length1, byte[] buffer2, int offset2, int length2, boolean ignoreTrailingNulls)
/*  78:    */   {
/*  79:152 */     int minLen = length1 < length2 ? length1 : length2;
/*  80:153 */     for (int i = 0; i < minLen; i++) {
/*  81:154 */       if (buffer1[(offset1 + i)] != buffer2[(offset2 + i)]) {
/*  82:155 */         return false;
/*  83:    */       }
/*  84:    */     }
/*  85:158 */     if (length1 == length2) {
/*  86:159 */       return true;
/*  87:    */     }
/*  88:161 */     if (ignoreTrailingNulls)
/*  89:    */     {
/*  90:162 */       if (length1 > length2) {
/*  91:163 */         for (int i = length2; i < length1; i++) {
/*  92:164 */           if (buffer1[(offset1 + i)] != 0) {
/*  93:165 */             return false;
/*  94:    */           }
/*  95:    */         }
/*  96:    */       } else {
/*  97:169 */         for (int i = length1; i < length2; i++) {
/*  98:170 */           if (buffer2[(offset2 + i)] != 0) {
/*  99:171 */             return false;
/* 100:    */           }
/* 101:    */         }
/* 102:    */       }
/* 103:175 */       return true;
/* 104:    */     }
/* 105:177 */     return false;
/* 106:    */   }
/* 107:    */   
/* 108:    */   public static boolean isEqual(byte[] buffer1, int offset1, int length1, byte[] buffer2, int offset2, int length2)
/* 109:    */   {
/* 110:194 */     return isEqual(buffer1, offset1, length1, buffer2, offset2, length2, false);
/* 111:    */   }
/* 112:    */   
/* 113:    */   public static boolean isEqual(byte[] buffer1, byte[] buffer2)
/* 114:    */   {
/* 115:205 */     return isEqual(buffer1, 0, buffer1.length, buffer2, 0, buffer2.length, false);
/* 116:    */   }
/* 117:    */   
/* 118:    */   public static boolean isEqual(byte[] buffer1, byte[] buffer2, boolean ignoreTrailingNulls)
/* 119:    */   {
/* 120:217 */     return isEqual(buffer1, 0, buffer1.length, buffer2, 0, buffer2.length, ignoreTrailingNulls);
/* 121:    */   }
/* 122:    */   
/* 123:    */   public static boolean isEqualWithNull(byte[] buffer1, int offset1, int length1, byte[] buffer2, int offset2, int length2)
/* 124:    */   {
/* 125:234 */     return isEqual(buffer1, offset1, length1, buffer2, offset2, length2, true);
/* 126:    */   }
/* 127:    */   
/* 128:    */   public static boolean isArrayZero(byte[] a, int size)
/* 129:    */   {
/* 130:247 */     for (int i = 0; i < size; i++) {
/* 131:248 */       if (a[i] != 0) {
/* 132:249 */         return false;
/* 133:    */       }
/* 134:    */     }
/* 135:252 */     return true;
/* 136:    */   }
/* 137:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.utils.ArchiveUtils
 * JD-Core Version:    0.7.0.1
 */