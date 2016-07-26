/*   1:    */ package org.apache.commons.codec.binary;
/*   2:    */ 
/*   3:    */ import org.apache.commons.codec.BinaryDecoder;
/*   4:    */ import org.apache.commons.codec.BinaryEncoder;
/*   5:    */ import org.apache.commons.codec.DecoderException;
/*   6:    */ import org.apache.commons.codec.EncoderException;
/*   7:    */ 
/*   8:    */ public class Hex
/*   9:    */   implements BinaryEncoder, BinaryDecoder
/*  10:    */ {
/*  11: 36 */   private static final char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
/*  12:    */   
/*  13:    */   public static byte[] decodeHex(char[] data)
/*  14:    */     throws DecoderException
/*  15:    */   {
/*  16: 56 */     int len = data.length;
/*  17: 58 */     if ((len & 0x1) != 0) {
/*  18: 59 */       throw new DecoderException("Odd number of characters.");
/*  19:    */     }
/*  20: 62 */     byte[] out = new byte[len >> 1];
/*  21:    */     
/*  22:    */ 
/*  23: 65 */     int i = 0;
/*  24: 65 */     for (int j = 0; j < len; i++)
/*  25:    */     {
/*  26: 66 */       int f = toDigit(data[j], j) << 4;
/*  27: 67 */       j++;
/*  28: 68 */       f |= toDigit(data[j], j);
/*  29: 69 */       j++;
/*  30: 70 */       out[i] = ((byte)(f & 0xFF));
/*  31:    */     }
/*  32: 73 */     return out;
/*  33:    */   }
/*  34:    */   
/*  35:    */   protected static int toDigit(char ch, int index)
/*  36:    */     throws DecoderException
/*  37:    */   {
/*  38: 85 */     int digit = Character.digit(ch, 16);
/*  39: 86 */     if (digit == -1) {
/*  40: 87 */       throw new DecoderException("Illegal hexadecimal charcter " + ch + " at index " + index);
/*  41:    */     }
/*  42: 89 */     return digit;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public static char[] encodeHex(byte[] data)
/*  46:    */   {
/*  47:103 */     int l = data.length;
/*  48:    */     
/*  49:105 */     char[] out = new char[l << 1];
/*  50:    */     
/*  51:    */ 
/*  52:108 */     int i = 0;
/*  53:108 */     for (int j = 0; i < l; i++)
/*  54:    */     {
/*  55:109 */       out[(j++)] = DIGITS[((0xF0 & data[i]) >>> 4)];
/*  56:110 */       out[(j++)] = DIGITS[(0xF & data[i])];
/*  57:    */     }
/*  58:113 */     return out;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public byte[] decode(byte[] array)
/*  62:    */     throws DecoderException
/*  63:    */   {
/*  64:131 */     return decodeHex(new String(array).toCharArray());
/*  65:    */   }
/*  66:    */   
/*  67:    */   public Object decode(Object object)
/*  68:    */     throws DecoderException
/*  69:    */   {
/*  70:    */     try
/*  71:    */     {
/*  72:150 */       char[] charArray = (object instanceof String) ? ((String)object).toCharArray() : (char[])object;
/*  73:151 */       return decodeHex(charArray);
/*  74:    */     }
/*  75:    */     catch (ClassCastException e)
/*  76:    */     {
/*  77:153 */       throw new DecoderException(e.getMessage());
/*  78:    */     }
/*  79:    */   }
/*  80:    */   
/*  81:    */   public byte[] encode(byte[] array)
/*  82:    */   {
/*  83:168 */     return new String(encodeHex(array)).getBytes();
/*  84:    */   }
/*  85:    */   
/*  86:    */   public Object encode(Object object)
/*  87:    */     throws EncoderException
/*  88:    */   {
/*  89:    */     try
/*  90:    */     {
/*  91:184 */       byte[] byteArray = (object instanceof String) ? ((String)object).getBytes() : (byte[])object;
/*  92:185 */       return encodeHex(byteArray);
/*  93:    */     }
/*  94:    */     catch (ClassCastException e)
/*  95:    */     {
/*  96:187 */       throw new EncoderException(e.getMessage());
/*  97:    */     }
/*  98:    */   }
/*  99:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.codec.binary.Hex
 * JD-Core Version:    0.7.0.1
 */