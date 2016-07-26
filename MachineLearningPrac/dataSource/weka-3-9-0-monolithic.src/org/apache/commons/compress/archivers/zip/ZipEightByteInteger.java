/*   1:    */ package org.apache.commons.compress.archivers.zip;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.math.BigInteger;
/*   5:    */ 
/*   6:    */ public final class ZipEightByteInteger
/*   7:    */   implements Serializable
/*   8:    */ {
/*   9:    */   private static final long serialVersionUID = 1L;
/*  10:    */   private static final int BYTE_1 = 1;
/*  11:    */   private static final int BYTE_1_MASK = 65280;
/*  12:    */   private static final int BYTE_1_SHIFT = 8;
/*  13:    */   private static final int BYTE_2 = 2;
/*  14:    */   private static final int BYTE_2_MASK = 16711680;
/*  15:    */   private static final int BYTE_2_SHIFT = 16;
/*  16:    */   private static final int BYTE_3 = 3;
/*  17:    */   private static final long BYTE_3_MASK = 4278190080L;
/*  18:    */   private static final int BYTE_3_SHIFT = 24;
/*  19:    */   private static final int BYTE_4 = 4;
/*  20:    */   private static final long BYTE_4_MASK = 1095216660480L;
/*  21:    */   private static final int BYTE_4_SHIFT = 32;
/*  22:    */   private static final int BYTE_5 = 5;
/*  23:    */   private static final long BYTE_5_MASK = 280375465082880L;
/*  24:    */   private static final int BYTE_5_SHIFT = 40;
/*  25:    */   private static final int BYTE_6 = 6;
/*  26:    */   private static final long BYTE_6_MASK = 71776119061217280L;
/*  27:    */   private static final int BYTE_6_SHIFT = 48;
/*  28:    */   private static final int BYTE_7 = 7;
/*  29:    */   private static final long BYTE_7_MASK = 9151314442816847872L;
/*  30:    */   private static final int BYTE_7_SHIFT = 56;
/*  31:    */   private static final int LEFTMOST_BIT_SHIFT = 63;
/*  32:    */   private static final byte LEFTMOST_BIT = -128;
/*  33:    */   private final BigInteger value;
/*  34: 68 */   public static final ZipEightByteInteger ZERO = new ZipEightByteInteger(0L);
/*  35:    */   
/*  36:    */   public ZipEightByteInteger(long value)
/*  37:    */   {
/*  38: 75 */     this(BigInteger.valueOf(value));
/*  39:    */   }
/*  40:    */   
/*  41:    */   public ZipEightByteInteger(BigInteger value)
/*  42:    */   {
/*  43: 83 */     this.value = value;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public ZipEightByteInteger(byte[] bytes)
/*  47:    */   {
/*  48: 91 */     this(bytes, 0);
/*  49:    */   }
/*  50:    */   
/*  51:    */   public ZipEightByteInteger(byte[] bytes, int offset)
/*  52:    */   {
/*  53:100 */     this.value = getValue(bytes, offset);
/*  54:    */   }
/*  55:    */   
/*  56:    */   public byte[] getBytes()
/*  57:    */   {
/*  58:108 */     return getBytes(this.value);
/*  59:    */   }
/*  60:    */   
/*  61:    */   public long getLongValue()
/*  62:    */   {
/*  63:116 */     return this.value.longValue();
/*  64:    */   }
/*  65:    */   
/*  66:    */   public BigInteger getValue()
/*  67:    */   {
/*  68:124 */     return this.value;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public static byte[] getBytes(long value)
/*  72:    */   {
/*  73:133 */     return getBytes(BigInteger.valueOf(value));
/*  74:    */   }
/*  75:    */   
/*  76:    */   public static byte[] getBytes(BigInteger value)
/*  77:    */   {
/*  78:142 */     byte[] result = new byte[8];
/*  79:143 */     long val = value.longValue();
/*  80:144 */     result[0] = ((byte)(int)(val & 0xFF));
/*  81:145 */     result[1] = ((byte)(int)((val & 0xFF00) >> 8));
/*  82:146 */     result[2] = ((byte)(int)((val & 0xFF0000) >> 16));
/*  83:147 */     result[3] = ((byte)(int)((val & 0xFF000000) >> 24));
/*  84:148 */     result[4] = ((byte)(int)((val & 0x0) >> 32));
/*  85:149 */     result[5] = ((byte)(int)((val & 0x0) >> 40));
/*  86:150 */     result[6] = ((byte)(int)((val & 0x0) >> 48));
/*  87:151 */     result[7] = ((byte)(int)((val & 0x0) >> 56));
/*  88:152 */     if (value.testBit(63))
/*  89:    */     {
/*  90:153 */       byte[] tmp125_122 = result;tmp125_122[7] = ((byte)(tmp125_122[7] | 0xFFFFFF80));
/*  91:    */     }
/*  92:155 */     return result;
/*  93:    */   }
/*  94:    */   
/*  95:    */   public static long getLongValue(byte[] bytes, int offset)
/*  96:    */   {
/*  97:166 */     return getValue(bytes, offset).longValue();
/*  98:    */   }
/*  99:    */   
/* 100:    */   public static BigInteger getValue(byte[] bytes, int offset)
/* 101:    */   {
/* 102:177 */     long value = bytes[(offset + 7)] << 56 & 0x0;
/* 103:178 */     value += (bytes[(offset + 6)] << 48 & 0x0);
/* 104:179 */     value += (bytes[(offset + 5)] << 40 & 0x0);
/* 105:180 */     value += (bytes[(offset + 4)] << 32 & 0x0);
/* 106:181 */     value += (bytes[(offset + 3)] << 24 & 0xFF000000);
/* 107:182 */     value += (bytes[(offset + 2)] << 16 & 0xFF0000);
/* 108:183 */     value += (bytes[(offset + 1)] << 8 & 0xFF00);
/* 109:184 */     value += (bytes[offset] & 0xFF);
/* 110:185 */     BigInteger val = BigInteger.valueOf(value);
/* 111:186 */     return (bytes[(offset + 7)] & 0xFFFFFF80) == -128 ? val.setBit(63) : val;
/* 112:    */   }
/* 113:    */   
/* 114:    */   public static long getLongValue(byte[] bytes)
/* 115:    */   {
/* 116:196 */     return getLongValue(bytes, 0);
/* 117:    */   }
/* 118:    */   
/* 119:    */   public static BigInteger getValue(byte[] bytes)
/* 120:    */   {
/* 121:205 */     return getValue(bytes, 0);
/* 122:    */   }
/* 123:    */   
/* 124:    */   public boolean equals(Object o)
/* 125:    */   {
/* 126:215 */     if ((o == null) || (!(o instanceof ZipEightByteInteger))) {
/* 127:216 */       return false;
/* 128:    */     }
/* 129:218 */     return this.value.equals(((ZipEightByteInteger)o).getValue());
/* 130:    */   }
/* 131:    */   
/* 132:    */   public int hashCode()
/* 133:    */   {
/* 134:227 */     return this.value.hashCode();
/* 135:    */   }
/* 136:    */   
/* 137:    */   public String toString()
/* 138:    */   {
/* 139:232 */     return "ZipEightByteInteger value: " + this.value;
/* 140:    */   }
/* 141:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.ZipEightByteInteger
 * JD-Core Version:    0.7.0.1
 */