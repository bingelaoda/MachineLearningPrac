/*   1:    */ package org.boon;
/*   2:    */ 
/*   3:    */ import org.boon.core.reflection.FastStringUtils;
/*   4:    */ import org.boon.primitive.CharScanner;
/*   5:    */ import org.boon.primitive.Chr;
/*   6:    */ 
/*   7:    */ public class StringScanner
/*   8:    */ {
/*   9: 39 */   private static final char[] WHITE_SPACE = { '\n', '\t', ' ', '\r' };
/*  10:    */   
/*  11:    */   public static boolean isDigits(String input)
/*  12:    */   {
/*  13: 47 */     return CharScanner.isDigits(FastStringUtils.toCharArray(input));
/*  14:    */   }
/*  15:    */   
/*  16:    */   public static String[] split(String string, char split, int limit)
/*  17:    */   {
/*  18: 62 */     char[][] comps = CharScanner.split(FastStringUtils.toCharArray(string), split, limit);
/*  19:    */     
/*  20: 64 */     return Str.fromCharArrayOfArrayToStringArray(comps);
/*  21:    */   }
/*  22:    */   
/*  23:    */   public static String[] split(String string, char split)
/*  24:    */   {
/*  25: 78 */     char[][] comps = CharScanner.split(FastStringUtils.toCharArray(string), split);
/*  26:    */     
/*  27: 80 */     return Str.fromCharArrayOfArrayToStringArray(comps);
/*  28:    */   }
/*  29:    */   
/*  30:    */   public static String[] splitByChars(String string, char... delimiters)
/*  31:    */   {
/*  32: 93 */     char[][] comps = CharScanner.splitByChars(FastStringUtils.toCharArray(string), delimiters);
/*  33:    */     
/*  34: 95 */     return Str.fromCharArrayOfArrayToStringArray(comps);
/*  35:    */   }
/*  36:    */   
/*  37:    */   public static String[] splitByCharsFromToDelims(String string, int from, int to, char... delimiters)
/*  38:    */   {
/*  39:102 */     char[][] comps = CharScanner.splitByCharsFromToDelims(FastStringUtils.toCharArray(string), from, to, delimiters);
/*  40:    */     
/*  41:104 */     return Str.fromCharArrayOfArrayToStringArray(comps);
/*  42:    */   }
/*  43:    */   
/*  44:    */   public static String[] splitByCharsFrom(String string, int from, char... delimiters)
/*  45:    */   {
/*  46:111 */     char[][] comps = CharScanner.splitByCharsFromToDelims(FastStringUtils.toCharArray(string), from, string.length(), delimiters);
/*  47:    */     
/*  48:113 */     return Str.fromCharArrayOfArrayToStringArray(comps);
/*  49:    */   }
/*  50:    */   
/*  51:    */   public static String[] splitByWhiteSpace(String string)
/*  52:    */   {
/*  53:125 */     char[][] comps = CharScanner.splitByChars(FastStringUtils.toCharArray(string), WHITE_SPACE);
/*  54:    */     
/*  55:127 */     return Str.fromCharArrayOfArrayToStringArray(comps);
/*  56:    */   }
/*  57:    */   
/*  58:    */   public static String[] splitByDelimiters(String string, String delimiters)
/*  59:    */   {
/*  60:140 */     char[][] comps = CharScanner.splitByChars(FastStringUtils.toCharArray(string), delimiters.toCharArray());
/*  61:    */     
/*  62:142 */     return Str.fromCharArrayOfArrayToStringArray(comps);
/*  63:    */   }
/*  64:    */   
/*  65:    */   public static String[] splitByCharsNoneEmpty(String string, char... delimiters)
/*  66:    */   {
/*  67:156 */     char[][] comps = CharScanner.splitByCharsNoneEmpty(FastStringUtils.toCharArray(string), delimiters);
/*  68:157 */     return Str.fromCharArrayOfArrayToStringArray(comps);
/*  69:    */   }
/*  70:    */   
/*  71:    */   public static String removeChars(String string, char... delimiters)
/*  72:    */   {
/*  73:168 */     char[][] comps = CharScanner.splitByCharsNoneEmpty(FastStringUtils.toCharArray(string), delimiters);
/*  74:169 */     return new String(Chr.add(comps));
/*  75:    */   }
/*  76:    */   
/*  77:    */   public static String[] splitByCharsNoneEmpty(String string, int start, int end, char... delimiters)
/*  78:    */   {
/*  79:181 */     Exceptions.requireNonNull(string);
/*  80:    */     
/*  81:183 */     char[][] comps = CharScanner.splitByCharsNoneEmpty(FastStringUtils.toCharArray(string), start, end, delimiters);
/*  82:184 */     return Str.fromCharArrayOfArrayToStringArray(comps);
/*  83:    */   }
/*  84:    */   
/*  85:    */   public static float parseFloat(String buffer, int from, int to)
/*  86:    */   {
/*  87:195 */     return CharScanner.parseFloat(FastStringUtils.toCharArray(buffer), from, to);
/*  88:    */   }
/*  89:    */   
/*  90:    */   public static float parseFloat(String buffer)
/*  91:    */   {
/*  92:204 */     return CharScanner.parseFloat(FastStringUtils.toCharArray(buffer));
/*  93:    */   }
/*  94:    */   
/*  95:    */   public static double parseDouble(String buffer, int from, int to)
/*  96:    */   {
/*  97:214 */     return CharScanner.parseDouble(FastStringUtils.toCharArray(buffer), from, to);
/*  98:    */   }
/*  99:    */   
/* 100:    */   public static double parseDouble(String buffer)
/* 101:    */   {
/* 102:224 */     return CharScanner.parseDouble(FastStringUtils.toCharArray(buffer));
/* 103:    */   }
/* 104:    */   
/* 105:    */   public static int parseInt(String buffer, int from, int to)
/* 106:    */   {
/* 107:235 */     return CharScanner.parseInt(FastStringUtils.toCharArray(buffer), from, to);
/* 108:    */   }
/* 109:    */   
/* 110:    */   public static int parseInt(String buffer)
/* 111:    */   {
/* 112:244 */     return CharScanner.parseInt(FastStringUtils.toCharArray(buffer));
/* 113:    */   }
/* 114:    */   
/* 115:    */   public static long parseLong(String buffer, int from, int to)
/* 116:    */   {
/* 117:254 */     return CharScanner.parseLong(FastStringUtils.toCharArray(buffer), from, to);
/* 118:    */   }
/* 119:    */   
/* 120:    */   public static long parseLong(String buffer)
/* 121:    */   {
/* 122:264 */     return CharScanner.parseLong(FastStringUtils.toCharArray(buffer));
/* 123:    */   }
/* 124:    */   
/* 125:    */   public static short parseShort(String buffer, int from, int to)
/* 126:    */   {
/* 127:270 */     return (short)CharScanner.parseInt(FastStringUtils.toCharArray(buffer), from, to);
/* 128:    */   }
/* 129:    */   
/* 130:    */   public static short parseShort(String buffer)
/* 131:    */   {
/* 132:274 */     return (short)CharScanner.parseInt(FastStringUtils.toCharArray(buffer));
/* 133:    */   }
/* 134:    */   
/* 135:    */   public static short parseByte(String buffer, int from, int to)
/* 136:    */   {
/* 137:279 */     return (short)(byte)CharScanner.parseInt(FastStringUtils.toCharArray(buffer), from, to);
/* 138:    */   }
/* 139:    */   
/* 140:    */   public static short parseByte(String buffer)
/* 141:    */   {
/* 142:283 */     return (short)(byte)CharScanner.parseInt(FastStringUtils.toCharArray(buffer));
/* 143:    */   }
/* 144:    */   
/* 145:    */   public static int findWhiteSpace(String buffer)
/* 146:    */   {
/* 147:288 */     return CharScanner.findWhiteSpace(FastStringUtils.toCharArray(buffer));
/* 148:    */   }
/* 149:    */   
/* 150:    */   public static String substringAfter(String string, String after)
/* 151:    */   {
/* 152:293 */     int index = findString(string, after);
/* 153:294 */     if (index == -1) {
/* 154:295 */       return "";
/* 155:    */     }
/* 156:297 */     return Str.slc(string, index + after.length());
/* 157:    */   }
/* 158:    */   
/* 159:    */   private static int findString(String string, String after)
/* 160:    */   {
/* 161:303 */     return CharScanner.findChars(FastStringUtils.toCharArray(after), FastStringUtils.toCharArray(string));
/* 162:    */   }
/* 163:    */   
/* 164:    */   public static String substringBefore(String string, String before)
/* 165:    */   {
/* 166:309 */     int index = findString(string, before);
/* 167:310 */     if (index == -1) {
/* 168:311 */       return "";
/* 169:    */     }
/* 170:313 */     return Str.slcEnd(string, index);
/* 171:    */   }
/* 172:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.StringScanner
 * JD-Core Version:    0.7.0.1
 */