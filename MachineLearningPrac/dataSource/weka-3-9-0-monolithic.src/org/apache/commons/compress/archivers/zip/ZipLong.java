/*   1:    */ package org.apache.commons.compress.archivers.zip;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ 
/*   5:    */ public final class ZipLong
/*   6:    */   implements Cloneable, Serializable
/*   7:    */ {
/*   8:    */   private static final long serialVersionUID = 1L;
/*   9:    */   private static final int BYTE_1 = 1;
/*  10:    */   private static final int BYTE_1_MASK = 65280;
/*  11:    */   private static final int BYTE_1_SHIFT = 8;
/*  12:    */   private static final int BYTE_2 = 2;
/*  13:    */   private static final int BYTE_2_MASK = 16711680;
/*  14:    */   private static final int BYTE_2_SHIFT = 16;
/*  15:    */   private static final int BYTE_3 = 3;
/*  16:    */   private static final long BYTE_3_MASK = 4278190080L;
/*  17:    */   private static final int BYTE_3_SHIFT = 24;
/*  18:    */   private final long value;
/*  19: 50 */   public static final ZipLong CFH_SIG = new ZipLong(33639248L);
/*  20: 53 */   public static final ZipLong LFH_SIG = new ZipLong(67324752L);
/*  21: 63 */   public static final ZipLong DD_SIG = new ZipLong(134695760L);
/*  22: 70 */   static final ZipLong ZIP64_MAGIC = new ZipLong(4294967295L);
/*  23: 80 */   public static final ZipLong SINGLE_SEGMENT_SPLIT_MARKER = new ZipLong(808471376L);
/*  24: 87 */   public static final ZipLong AED_SIG = new ZipLong(134630224L);
/*  25:    */   
/*  26:    */   public ZipLong(long value)
/*  27:    */   {
/*  28: 94 */     this.value = value;
/*  29:    */   }
/*  30:    */   
/*  31:    */   public ZipLong(byte[] bytes)
/*  32:    */   {
/*  33:102 */     this(bytes, 0);
/*  34:    */   }
/*  35:    */   
/*  36:    */   public ZipLong(byte[] bytes, int offset)
/*  37:    */   {
/*  38:111 */     this.value = getValue(bytes, offset);
/*  39:    */   }
/*  40:    */   
/*  41:    */   public byte[] getBytes()
/*  42:    */   {
/*  43:119 */     return getBytes(this.value);
/*  44:    */   }
/*  45:    */   
/*  46:    */   public long getValue()
/*  47:    */   {
/*  48:127 */     return this.value;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public static byte[] getBytes(long value)
/*  52:    */   {
/*  53:136 */     byte[] result = new byte[4];
/*  54:137 */     putLong(value, result, 0);
/*  55:138 */     return result;
/*  56:    */   }
/*  57:    */   
/*  58:    */   public static void putLong(long value, byte[] buf, int offset)
/*  59:    */   {
/*  60:151 */     buf[(offset++)] = ((byte)(int)(value & 0xFF));
/*  61:152 */     buf[(offset++)] = ((byte)(int)((value & 0xFF00) >> 8));
/*  62:153 */     buf[(offset++)] = ((byte)(int)((value & 0xFF0000) >> 16));
/*  63:154 */     buf[offset] = ((byte)(int)((value & 0xFF000000) >> 24));
/*  64:    */   }
/*  65:    */   
/*  66:    */   public void putLong(byte[] buf, int offset)
/*  67:    */   {
/*  68:158 */     putLong(this.value, buf, offset);
/*  69:    */   }
/*  70:    */   
/*  71:    */   public static long getValue(byte[] bytes, int offset)
/*  72:    */   {
/*  73:168 */     long value = bytes[(offset + 3)] << 24 & 0xFF000000;
/*  74:169 */     value += (bytes[(offset + 2)] << 16 & 0xFF0000);
/*  75:170 */     value += (bytes[(offset + 1)] << 8 & 0xFF00);
/*  76:171 */     value += (bytes[offset] & 0xFF);
/*  77:172 */     return value;
/*  78:    */   }
/*  79:    */   
/*  80:    */   public static long getValue(byte[] bytes)
/*  81:    */   {
/*  82:181 */     return getValue(bytes, 0);
/*  83:    */   }
/*  84:    */   
/*  85:    */   public boolean equals(Object o)
/*  86:    */   {
/*  87:191 */     if ((o == null) || (!(o instanceof ZipLong))) {
/*  88:192 */       return false;
/*  89:    */     }
/*  90:194 */     return this.value == ((ZipLong)o).getValue();
/*  91:    */   }
/*  92:    */   
/*  93:    */   public int hashCode()
/*  94:    */   {
/*  95:203 */     return (int)this.value;
/*  96:    */   }
/*  97:    */   
/*  98:    */   public Object clone()
/*  99:    */   {
/* 100:    */     try
/* 101:    */     {
/* 102:209 */       return super.clone();
/* 103:    */     }
/* 104:    */     catch (CloneNotSupportedException cnfe)
/* 105:    */     {
/* 106:212 */       throw new RuntimeException(cnfe);
/* 107:    */     }
/* 108:    */   }
/* 109:    */   
/* 110:    */   public String toString()
/* 111:    */   {
/* 112:218 */     return "ZipLong value: " + this.value;
/* 113:    */   }
/* 114:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.ZipLong
 * JD-Core Version:    0.7.0.1
 */