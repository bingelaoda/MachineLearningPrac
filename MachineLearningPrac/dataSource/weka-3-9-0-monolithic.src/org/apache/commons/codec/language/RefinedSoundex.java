/*   1:    */ package org.apache.commons.codec.language;
/*   2:    */ 
/*   3:    */ import org.apache.commons.codec.EncoderException;
/*   4:    */ import org.apache.commons.codec.StringEncoder;
/*   5:    */ 
/*   6:    */ public class RefinedSoundex
/*   7:    */   implements StringEncoder
/*   8:    */ {
/*   9: 36 */   public static final RefinedSoundex US_ENGLISH = new RefinedSoundex();
/*  10: 43 */   public static final char[] US_ENGLISH_MAPPING = "01360240043788015936020505".toCharArray();
/*  11:    */   private char[] soundexMapping;
/*  12:    */   
/*  13:    */   public RefinedSoundex()
/*  14:    */   {
/*  15: 57 */     this(US_ENGLISH_MAPPING);
/*  16:    */   }
/*  17:    */   
/*  18:    */   public RefinedSoundex(char[] mapping)
/*  19:    */   {
/*  20: 70 */     this.soundexMapping = mapping;
/*  21:    */   }
/*  22:    */   
/*  23:    */   public int difference(String s1, String s2)
/*  24:    */     throws EncoderException
/*  25:    */   {
/*  26: 96 */     return SoundexUtils.difference(this, s1, s2);
/*  27:    */   }
/*  28:    */   
/*  29:    */   public Object encode(Object pObject)
/*  30:    */     throws EncoderException
/*  31:    */   {
/*  32:113 */     if (!(pObject instanceof String)) {
/*  33:114 */       throw new EncoderException("Parameter supplied to RefinedSoundex encode is not of type java.lang.String");
/*  34:    */     }
/*  35:116 */     return soundex((String)pObject);
/*  36:    */   }
/*  37:    */   
/*  38:    */   public String encode(String pString)
/*  39:    */   {
/*  40:127 */     return soundex(pString);
/*  41:    */   }
/*  42:    */   
/*  43:    */   char getMappingCode(char c)
/*  44:    */   {
/*  45:140 */     if (!Character.isLetter(c)) {
/*  46:141 */       return '\000';
/*  47:    */     }
/*  48:143 */     return this.soundexMapping[(Character.toUpperCase(c) - 'A')];
/*  49:    */   }
/*  50:    */   
/*  51:    */   public String soundex(String str)
/*  52:    */   {
/*  53:154 */     if (str == null) {
/*  54:155 */       return null;
/*  55:    */     }
/*  56:157 */     str = SoundexUtils.clean(str);
/*  57:158 */     if (str.length() == 0) {
/*  58:159 */       return str;
/*  59:    */     }
/*  60:162 */     StringBuffer sBuf = new StringBuffer();
/*  61:163 */     sBuf.append(str.charAt(0));
/*  62:    */     
/*  63:    */ 
/*  64:166 */     char last = '*';
/*  65:168 */     for (int i = 0; i < str.length(); i++)
/*  66:    */     {
/*  67:170 */       char current = getMappingCode(str.charAt(i));
/*  68:171 */       if (current != last)
/*  69:    */       {
/*  70:173 */         if (current != 0) {
/*  71:174 */           sBuf.append(current);
/*  72:    */         }
/*  73:177 */         last = current;
/*  74:    */       }
/*  75:    */     }
/*  76:181 */     return sBuf.toString();
/*  77:    */   }
/*  78:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.codec.language.RefinedSoundex
 * JD-Core Version:    0.7.0.1
 */