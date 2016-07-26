/*   1:    */ package org.boon.core.reflection;
/*   2:    */ 
/*   3:    */ import java.lang.reflect.Field;
/*   4:    */ import java.nio.charset.Charset;
/*   5:    */ import java.nio.charset.StandardCharsets;
/*   6:    */ import sun.misc.Unsafe;
/*   7:    */ 
/*   8:    */ public class FastStringUtils
/*   9:    */ {
/*  10: 50 */   private static final boolean WRITE_TO_FINAL_FIELDS = Boolean.parseBoolean(System.getProperty("org.boon.write.to.final.string.fields", "true"));
/*  11: 51 */   private static final boolean DISABLE = Boolean.parseBoolean(System.getProperty("org.boon.faststringutils.disable", "false"));
/*  12:    */   
/*  13:    */   private static Unsafe loadUnsafe()
/*  14:    */   {
/*  15:    */     try
/*  16:    */     {
/*  17: 55 */       Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
/*  18: 56 */       unsafeField.setAccessible(true);
/*  19: 57 */       return (Unsafe)unsafeField.get(null);
/*  20:    */     }
/*  21:    */     catch (NoSuchFieldException e)
/*  22:    */     {
/*  23: 60 */       return null;
/*  24:    */     }
/*  25:    */     catch (IllegalAccessException e) {}
/*  26: 62 */     return null;
/*  27:    */   }
/*  28:    */   
/*  29:    */   static
/*  30:    */   {
/*  31: 67 */     UNSAFE = DISABLE ? null : loadUnsafe();
/*  32: 68 */     ENABLED = UNSAFE != null;
/*  33:    */   }
/*  34:    */   
/*  35:    */   private static long getFieldOffset(String fieldName)
/*  36:    */   {
/*  37: 72 */     if (ENABLED) {
/*  38:    */       try
/*  39:    */       {
/*  40: 74 */         return UNSAFE.objectFieldOffset(String.class.getDeclaredField(fieldName));
/*  41:    */       }
/*  42:    */       catch (NoSuchFieldException e) {}
/*  43:    */     }
/*  44: 79 */     return -1L;
/*  45:    */   }
/*  46:    */   
/*  47: 83 */   public static final long STRING_VALUE_FIELD_OFFSET = getFieldOffset("value");
/*  48: 84 */   public static final long STRING_OFFSET_FIELD_OFFSET = getFieldOffset("offset");
/*  49: 85 */   public static final long STRING_COUNT_FIELD_OFFSET = getFieldOffset("count");
/*  50:    */   
/*  51:    */   private static abstract enum StringImplementation
/*  52:    */   {
/*  53: 89 */     DIRECT_CHARS,  OFFSET,  UNKNOWN;
/*  54:    */     
/*  55:    */     private StringImplementation() {}
/*  56:    */     
/*  57:    */     public abstract char[] toCharArray(String paramString);
/*  58:    */     
/*  59:    */     public abstract String noCopyStringFromChars(char[] paramArrayOfChar);
/*  60:    */   }
/*  61:    */   
/*  62:147 */   public static StringImplementation STRING_IMPLEMENTATION = computeStringImplementation();
/*  63:    */   
/*  64:    */   private static StringImplementation computeStringImplementation()
/*  65:    */   {
/*  66:151 */     if (STRING_VALUE_FIELD_OFFSET != -1L)
/*  67:    */     {
/*  68:152 */       if ((STRING_OFFSET_FIELD_OFFSET != -1L) && (STRING_COUNT_FIELD_OFFSET != -1L)) {
/*  69:153 */         return StringImplementation.OFFSET;
/*  70:    */       }
/*  71:155 */       if ((STRING_OFFSET_FIELD_OFFSET == -1L) && (STRING_COUNT_FIELD_OFFSET == -1L)) {
/*  72:156 */         return StringImplementation.DIRECT_CHARS;
/*  73:    */       }
/*  74:159 */       return StringImplementation.UNKNOWN;
/*  75:    */     }
/*  76:162 */     return StringImplementation.UNKNOWN;
/*  77:    */   }
/*  78:    */   
/*  79:    */   public static boolean hasUnsafe()
/*  80:    */   {
/*  81:167 */     return ENABLED;
/*  82:    */   }
/*  83:    */   
/*  84:170 */   public static final char[] EMPTY_CHARS = new char[0];
/*  85:    */   public static final Unsafe UNSAFE;
/*  86:    */   public static final boolean ENABLED;
/*  87:    */   public static final String EMPTY_STRING = "";
/*  88:    */   
/*  89:    */   public static char[] toCharArray(String string)
/*  90:    */   {
/*  91:174 */     if (string == null) {
/*  92:174 */       return EMPTY_CHARS;
/*  93:    */     }
/*  94:175 */     return STRING_IMPLEMENTATION.toCharArray(string);
/*  95:    */   }
/*  96:    */   
/*  97:    */   public static char[] toCharArrayNoCheck(CharSequence charSequence)
/*  98:    */   {
/*  99:180 */     return toCharArray(charSequence.toString());
/* 100:    */   }
/* 101:    */   
/* 102:    */   public static char[] toCharArray(CharSequence charSequence)
/* 103:    */   {
/* 104:184 */     if (charSequence == null) {
/* 105:184 */       return EMPTY_CHARS;
/* 106:    */     }
/* 107:185 */     return toCharArray(charSequence.toString());
/* 108:    */   }
/* 109:    */   
/* 110:    */   public static char[] toCharArrayFromBytes(byte[] bytes, Charset charset)
/* 111:    */   {
/* 112:189 */     return toCharArray(new String(bytes, charset != null ? charset : StandardCharsets.UTF_8));
/* 113:    */   }
/* 114:    */   
/* 115:    */   public static String noCopyStringFromChars(char[] chars)
/* 116:    */   {
/* 117:193 */     if (chars == null) {
/* 118:193 */       return "";
/* 119:    */     }
/* 120:194 */     return STRING_IMPLEMENTATION.noCopyStringFromChars(chars);
/* 121:    */   }
/* 122:    */   
/* 123:    */   public static String noCopyStringFromCharsNoCheck(char[] chars, int len)
/* 124:    */   {
/* 125:199 */     char[] newChars = new char[len];
/* 126:200 */     System.arraycopy(chars, 0, newChars, 0, len);
/* 127:201 */     return STRING_IMPLEMENTATION.noCopyStringFromChars(newChars);
/* 128:    */   }
/* 129:    */   
/* 130:    */   public static String noCopyStringFromCharsNoCheck(char[] chars, int start, int len)
/* 131:    */   {
/* 132:205 */     char[] newChars = new char[len];
/* 133:206 */     System.arraycopy(chars, start, newChars, 0, len);
/* 134:207 */     return STRING_IMPLEMENTATION.noCopyStringFromChars(newChars);
/* 135:    */   }
/* 136:    */   
/* 137:    */   public static String noCopyStringFromCharsNoCheck(char[] chars)
/* 138:    */   {
/* 139:213 */     return STRING_IMPLEMENTATION.noCopyStringFromChars(chars);
/* 140:    */   }
/* 141:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.reflection.FastStringUtils
 * JD-Core Version:    0.7.0.1
 */