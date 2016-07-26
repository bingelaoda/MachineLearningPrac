/*   1:    */ package org.boon.json.implementation;
/*   2:    */ 
/*   3:    */ import java.io.InputStream;
/*   4:    */ import java.io.InputStreamReader;
/*   5:    */ import java.io.Reader;
/*   6:    */ import java.nio.charset.Charset;
/*   7:    */ import java.nio.charset.StandardCharsets;
/*   8:    */ import java.util.concurrent.ConcurrentHashMap;
/*   9:    */ import org.boon.IO;
/*  10:    */ import org.boon.core.reflection.FastStringUtils;
/*  11:    */ import org.boon.json.JsonParser;
/*  12:    */ import org.boon.primitive.CharBuf;
/*  13:    */ 
/*  14:    */ public abstract class BaseJsonParser
/*  15:    */   implements JsonParser
/*  16:    */ {
/*  17:    */   protected static final int COLON = 58;
/*  18:    */   protected static final int COMMA = 44;
/*  19:    */   protected static final int CLOSED_CURLY = 125;
/*  20:    */   protected static final int CLOSED_BRACKET = 93;
/*  21:    */   protected static final int LETTER_E = 101;
/*  22:    */   protected static final int LETTER_BIG_E = 69;
/*  23:    */   protected static final int MINUS = 45;
/*  24:    */   protected static final int PLUS = 43;
/*  25:    */   protected static final int DECIMAL_POINT = 46;
/*  26:    */   protected static final int ALPHA_0 = 48;
/*  27:    */   protected static final int ALPHA_1 = 49;
/*  28:    */   protected static final int ALPHA_2 = 50;
/*  29:    */   protected static final int ALPHA_3 = 51;
/*  30:    */   protected static final int ALPHA_4 = 52;
/*  31:    */   protected static final int ALPHA_5 = 53;
/*  32:    */   protected static final int ALPHA_6 = 54;
/*  33:    */   protected static final int ALPHA_7 = 55;
/*  34:    */   protected static final int ALPHA_8 = 56;
/*  35:    */   protected static final int ALPHA_9 = 57;
/*  36:    */   protected static final int DOUBLE_QUOTE = 34;
/*  37:    */   protected static final int ESCAPE = 92;
/*  38: 79 */   protected static final boolean internKeys = Boolean.parseBoolean(System.getProperty("org.boon.json.implementation.internKeys", "false"));
/*  39:    */   protected static ConcurrentHashMap<String, String> internedKeysCache;
/*  40: 82 */   protected Charset charset = StandardCharsets.UTF_8;
/*  41: 85 */   protected int bufSize = 1024;
/*  42:    */   protected char[] copyBuf;
/*  43:    */   private CharBuf fileInputBuf;
/*  44:    */   
/*  45:    */   static
/*  46:    */   {
/*  47: 91 */     if (internKeys) {
/*  48: 92 */       internedKeysCache = new ConcurrentHashMap();
/*  49:    */     }
/*  50:    */   }
/*  51:    */   
/*  52:    */   protected String charDescription(int c)
/*  53:    */   {
/*  54:    */     String charString;
/*  55:100 */     if (c == 32)
/*  56:    */     {
/*  57:101 */       charString = "[SPACE]";
/*  58:    */     }
/*  59:    */     else
/*  60:    */     {
/*  61:    */       String charString;
/*  62:102 */       if (c == 9)
/*  63:    */       {
/*  64:103 */         charString = "[TAB]";
/*  65:    */       }
/*  66:    */       else
/*  67:    */       {
/*  68:    */         String charString;
/*  69:105 */         if (c == 10) {
/*  70:106 */           charString = "[NEWLINE]";
/*  71:    */         } else {
/*  72:109 */           charString = "'" + (char)c + "'";
/*  73:    */         }
/*  74:    */       }
/*  75:    */     }
/*  76:112 */     String charString = charString + " with an int value of " + c;
/*  77:113 */     return charString;
/*  78:    */   }
/*  79:    */   
/*  80:    */   public void setCharset(Charset charset)
/*  81:    */   {
/*  82:121 */     this.charset = charset;
/*  83:    */   }
/*  84:    */   
/*  85:    */   public Object parse(String jsonString)
/*  86:    */   {
/*  87:127 */     return parse(FastStringUtils.toCharArray(jsonString));
/*  88:    */   }
/*  89:    */   
/*  90:    */   public Object parse(byte[] bytes)
/*  91:    */   {
/*  92:132 */     return parse(bytes, this.charset);
/*  93:    */   }
/*  94:    */   
/*  95:    */   public Object parse(CharSequence charSequence)
/*  96:    */   {
/*  97:138 */     return parse(FastStringUtils.toCharArray(charSequence));
/*  98:    */   }
/*  99:    */   
/* 100:    */   public Object parse(Reader reader)
/* 101:    */   {
/* 102:144 */     if (this.copyBuf == null) {
/* 103:145 */       this.copyBuf = new char[this.bufSize];
/* 104:    */     }
/* 105:148 */     this.fileInputBuf = IO.read(reader, this.fileInputBuf, this.bufSize, this.copyBuf);
/* 106:149 */     return parse(this.fileInputBuf.readForRecycle());
/* 107:    */   }
/* 108:    */   
/* 109:    */   public Object parse(InputStream input)
/* 110:    */   {
/* 111:155 */     return parse(input, this.charset);
/* 112:    */   }
/* 113:    */   
/* 114:    */   public Object parse(InputStream inputStream, Charset charset)
/* 115:    */   {
/* 116:165 */     return parse(new InputStreamReader(inputStream, charset));
/* 117:    */   }
/* 118:    */   
/* 119:174 */   int[] indexHolder = new int[1];
/* 120:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.implementation.BaseJsonParser
 * JD-Core Version:    0.7.0.1
 */