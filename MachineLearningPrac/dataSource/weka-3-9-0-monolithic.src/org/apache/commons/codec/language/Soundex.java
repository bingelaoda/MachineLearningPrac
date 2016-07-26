/*   1:    */ package org.apache.commons.codec.language;
/*   2:    */ 
/*   3:    */ import org.apache.commons.codec.EncoderException;
/*   4:    */ import org.apache.commons.codec.StringEncoder;
/*   5:    */ 
/*   6:    */ public class Soundex
/*   7:    */   implements StringEncoder
/*   8:    */ {
/*   9: 36 */   public static final Soundex US_ENGLISH = new Soundex();
/*  10:    */   public static final String US_ENGLISH_MAPPING_STRING = "01230120022455012623010202";
/*  11: 56 */   public static final char[] US_ENGLISH_MAPPING = "01230120022455012623010202".toCharArray();
/*  12:    */   
/*  13:    */   public int difference(String s1, String s2)
/*  14:    */     throws EncoderException
/*  15:    */   {
/*  16: 78 */     return SoundexUtils.difference(this, s1, s2);
/*  17:    */   }
/*  18:    */   
/*  19:    */   /**
/*  20:    */    * @deprecated
/*  21:    */    */
/*  22: 86 */   private int maxLength = 4;
/*  23:    */   private char[] soundexMapping;
/*  24:    */   
/*  25:    */   public Soundex()
/*  26:    */   {
/*  27:101 */     this(US_ENGLISH_MAPPING);
/*  28:    */   }
/*  29:    */   
/*  30:    */   public Soundex(char[] mapping)
/*  31:    */   {
/*  32:115 */     setSoundexMapping(mapping);
/*  33:    */   }
/*  34:    */   
/*  35:    */   public Object encode(Object pObject)
/*  36:    */     throws EncoderException
/*  37:    */   {
/*  38:132 */     if (!(pObject instanceof String)) {
/*  39:133 */       throw new EncoderException("Parameter supplied to Soundex encode is not of type java.lang.String");
/*  40:    */     }
/*  41:135 */     return soundex((String)pObject);
/*  42:    */   }
/*  43:    */   
/*  44:    */   public String encode(String pString)
/*  45:    */   {
/*  46:148 */     return soundex(pString);
/*  47:    */   }
/*  48:    */   
/*  49:    */   private char getMappingCode(String str, int index)
/*  50:    */   {
/*  51:165 */     char mappedChar = map(str.charAt(index));
/*  52:167 */     if ((index > 1) && (mappedChar != '0'))
/*  53:    */     {
/*  54:168 */       char hwChar = str.charAt(index - 1);
/*  55:169 */       if (('H' == hwChar) || ('W' == hwChar))
/*  56:    */       {
/*  57:170 */         char preHWChar = str.charAt(index - 2);
/*  58:171 */         char firstCode = map(preHWChar);
/*  59:172 */         if ((firstCode == mappedChar) || ('H' == preHWChar) || ('W' == preHWChar)) {
/*  60:173 */           return '\000';
/*  61:    */         }
/*  62:    */       }
/*  63:    */     }
/*  64:177 */     return mappedChar;
/*  65:    */   }
/*  66:    */   
/*  67:    */   /**
/*  68:    */    * @deprecated
/*  69:    */    */
/*  70:    */   public int getMaxLength()
/*  71:    */   {
/*  72:187 */     return this.maxLength;
/*  73:    */   }
/*  74:    */   
/*  75:    */   private char[] getSoundexMapping()
/*  76:    */   {
/*  77:196 */     return this.soundexMapping;
/*  78:    */   }
/*  79:    */   
/*  80:    */   private char map(char ch)
/*  81:    */   {
/*  82:209 */     int index = ch - 'A';
/*  83:210 */     if ((index < 0) || (index >= getSoundexMapping().length)) {
/*  84:211 */       throw new IllegalArgumentException("The character is not mapped: " + ch);
/*  85:    */     }
/*  86:213 */     return getSoundexMapping()[index];
/*  87:    */   }
/*  88:    */   
/*  89:    */   /**
/*  90:    */    * @deprecated
/*  91:    */    */
/*  92:    */   public void setMaxLength(int maxLength)
/*  93:    */   {
/*  94:224 */     this.maxLength = maxLength;
/*  95:    */   }
/*  96:    */   
/*  97:    */   private void setSoundexMapping(char[] soundexMapping)
/*  98:    */   {
/*  99:234 */     this.soundexMapping = soundexMapping;
/* 100:    */   }
/* 101:    */   
/* 102:    */   public String soundex(String str)
/* 103:    */   {
/* 104:247 */     if (str == null) {
/* 105:248 */       return null;
/* 106:    */     }
/* 107:250 */     str = SoundexUtils.clean(str);
/* 108:251 */     if (str.length() == 0) {
/* 109:252 */       return str;
/* 110:    */     }
/* 111:254 */     char[] out = { '0', '0', '0', '0' };
/* 112:    */     
/* 113:256 */     int incount = 1;int count = 1;
/* 114:257 */     out[0] = str.charAt(0);
/* 115:258 */     char last = getMappingCode(str, 0);
/* 116:259 */     while ((incount < str.length()) && (count < out.length))
/* 117:    */     {
/* 118:260 */       char mapped = getMappingCode(str, incount++);
/* 119:261 */       if (mapped != 0)
/* 120:    */       {
/* 121:262 */         if ((mapped != '0') && (mapped != last)) {
/* 122:263 */           out[(count++)] = mapped;
/* 123:    */         }
/* 124:265 */         last = mapped;
/* 125:    */       }
/* 126:    */     }
/* 127:268 */     return new String(out);
/* 128:    */   }
/* 129:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.codec.language.Soundex
 * JD-Core Version:    0.7.0.1
 */