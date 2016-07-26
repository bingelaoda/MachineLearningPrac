/*   1:    */ package org.apache.commons.compress.archivers.zip;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ 
/*   5:    */ public final class ZipShort
/*   6:    */   implements Cloneable, Serializable
/*   7:    */ {
/*   8:    */   private static final long serialVersionUID = 1L;
/*   9:    */   private static final int BYTE_1_MASK = 65280;
/*  10:    */   private static final int BYTE_1_SHIFT = 8;
/*  11:    */   private final int value;
/*  12:    */   
/*  13:    */   public ZipShort(int value)
/*  14:    */   {
/*  15: 42 */     this.value = value;
/*  16:    */   }
/*  17:    */   
/*  18:    */   public ZipShort(byte[] bytes)
/*  19:    */   {
/*  20: 50 */     this(bytes, 0);
/*  21:    */   }
/*  22:    */   
/*  23:    */   public ZipShort(byte[] bytes, int offset)
/*  24:    */   {
/*  25: 59 */     this.value = getValue(bytes, offset);
/*  26:    */   }
/*  27:    */   
/*  28:    */   public byte[] getBytes()
/*  29:    */   {
/*  30: 67 */     byte[] result = new byte[2];
/*  31: 68 */     result[0] = ((byte)(this.value & 0xFF));
/*  32: 69 */     result[1] = ((byte)((this.value & 0xFF00) >> 8));
/*  33: 70 */     return result;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public int getValue()
/*  37:    */   {
/*  38: 78 */     return this.value;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public static byte[] getBytes(int value)
/*  42:    */   {
/*  43: 87 */     byte[] result = new byte[2];
/*  44: 88 */     putShort(value, result, 0);
/*  45: 89 */     return result;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public static void putShort(int value, byte[] buf, int offset)
/*  49:    */   {
/*  50:101 */     buf[offset] = ((byte)(value & 0xFF));
/*  51:102 */     buf[(offset + 1)] = ((byte)((value & 0xFF00) >> 8));
/*  52:    */   }
/*  53:    */   
/*  54:    */   public static int getValue(byte[] bytes, int offset)
/*  55:    */   {
/*  56:112 */     int value = bytes[(offset + 1)] << 8 & 0xFF00;
/*  57:113 */     value += (bytes[offset] & 0xFF);
/*  58:114 */     return value;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public static int getValue(byte[] bytes)
/*  62:    */   {
/*  63:123 */     return getValue(bytes, 0);
/*  64:    */   }
/*  65:    */   
/*  66:    */   public boolean equals(Object o)
/*  67:    */   {
/*  68:133 */     if ((o == null) || (!(o instanceof ZipShort))) {
/*  69:134 */       return false;
/*  70:    */     }
/*  71:136 */     return this.value == ((ZipShort)o).getValue();
/*  72:    */   }
/*  73:    */   
/*  74:    */   public int hashCode()
/*  75:    */   {
/*  76:145 */     return this.value;
/*  77:    */   }
/*  78:    */   
/*  79:    */   public Object clone()
/*  80:    */   {
/*  81:    */     try
/*  82:    */     {
/*  83:151 */       return super.clone();
/*  84:    */     }
/*  85:    */     catch (CloneNotSupportedException cnfe)
/*  86:    */     {
/*  87:154 */       throw new RuntimeException(cnfe);
/*  88:    */     }
/*  89:    */   }
/*  90:    */   
/*  91:    */   public String toString()
/*  92:    */   {
/*  93:160 */     return "ZipShort value: " + this.value;
/*  94:    */   }
/*  95:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.ZipShort
 * JD-Core Version:    0.7.0.1
 */