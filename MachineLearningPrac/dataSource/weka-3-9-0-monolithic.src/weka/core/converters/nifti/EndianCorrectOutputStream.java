/*   1:    */ package weka.core.converters.nifti;
/*   2:    */ 
/*   3:    */ import java.io.DataOutputStream;
/*   4:    */ import java.io.FileNotFoundException;
/*   5:    */ import java.io.FileOutputStream;
/*   6:    */ import java.io.IOException;
/*   7:    */ import java.io.OutputStream;
/*   8:    */ 
/*   9:    */ public class EndianCorrectOutputStream
/*  10:    */   extends DataOutputStream
/*  11:    */ {
/*  12: 14 */   private boolean bigendian = true;
/*  13:    */   
/*  14:    */   public EndianCorrectOutputStream(String filename, boolean be)
/*  15:    */     throws FileNotFoundException, SecurityException
/*  16:    */   {
/*  17: 23 */     super(new FileOutputStream(filename));
/*  18: 24 */     this.bigendian = be;
/*  19:    */   }
/*  20:    */   
/*  21:    */   public EndianCorrectOutputStream(OutputStream os, boolean be)
/*  22:    */   {
/*  23: 32 */     super(os);
/*  24: 33 */     this.bigendian = be;
/*  25:    */   }
/*  26:    */   
/*  27:    */   public void writeShortCorrect(short val)
/*  28:    */     throws IOException
/*  29:    */   {
/*  30: 43 */     if (this.bigendian)
/*  31:    */     {
/*  32: 44 */       writeShort(val);
/*  33:    */     }
/*  34:    */     else
/*  35:    */     {
/*  36: 47 */       int byte0 = val & 0xFF;
/*  37: 48 */       int byte1 = val >> 8 & 0xFF;
/*  38:    */       
/*  39: 50 */       writeShort((short)(byte0 << 8 | byte1));
/*  40:    */     }
/*  41:    */   }
/*  42:    */   
/*  43:    */   public short flipShort(short val)
/*  44:    */   {
/*  45: 59 */     int byte0 = val & 0xFF;
/*  46: 60 */     int byte1 = val >> 8 & 0xFF;
/*  47:    */     
/*  48: 62 */     return (short)(byte0 << 8 | byte1);
/*  49:    */   }
/*  50:    */   
/*  51:    */   public void writeIntCorrect(int val)
/*  52:    */     throws IOException
/*  53:    */   {
/*  54: 71 */     if (this.bigendian)
/*  55:    */     {
/*  56: 72 */       writeInt(val);
/*  57:    */     }
/*  58:    */     else
/*  59:    */     {
/*  60: 76 */       int byte0 = val & 0xFF;
/*  61: 77 */       int byte1 = val >> 8 & 0xFF;
/*  62: 78 */       int byte2 = val >> 16 & 0xFF;
/*  63: 79 */       int byte3 = val >> 24 & 0xFF;
/*  64:    */       
/*  65: 81 */       writeInt(byte0 << 24 | byte1 << 16 | byte2 << 8 | byte3);
/*  66:    */     }
/*  67:    */   }
/*  68:    */   
/*  69:    */   public int flipInt(int val)
/*  70:    */   {
/*  71: 91 */     int byte0 = val & 0xFF;
/*  72: 92 */     int byte1 = val >> 8 & 0xFF;
/*  73: 93 */     int byte2 = val >> 16 & 0xFF;
/*  74: 94 */     int byte3 = val >> 24 & 0xFF;
/*  75:    */     
/*  76: 96 */     return byte0 << 24 | byte1 << 16 | byte2 << 8 | byte3;
/*  77:    */   }
/*  78:    */   
/*  79:    */   public void writeLongCorrect(long val)
/*  80:    */     throws IOException
/*  81:    */   {
/*  82:104 */     if (this.bigendian) {
/*  83:105 */       writeLong(val);
/*  84:    */     } else {
/*  85:109 */       writeLong(flipLong(val));
/*  86:    */     }
/*  87:    */   }
/*  88:    */   
/*  89:    */   public long flipLong(long val)
/*  90:    */   {
/*  91:119 */     long byte0 = val & 0xFF;
/*  92:120 */     long byte1 = val >> 8 & 0xFF;
/*  93:121 */     long byte2 = val >> 16 & 0xFF;
/*  94:122 */     long byte3 = val >> 24 & 0xFF;
/*  95:123 */     long byte4 = val >> 32 & 0xFF;
/*  96:124 */     long byte5 = val >> 40 & 0xFF;
/*  97:125 */     long byte6 = val >> 48 & 0xFF;
/*  98:126 */     long byte7 = val >> 56 & 0xFF;
/*  99:    */     
/* 100:128 */     return byte0 << 56 | byte1 << 48 | byte2 << 40 | byte3 << 32 | byte4 << 24 | byte5 << 16 | byte6 << 8 | byte7;
/* 101:    */   }
/* 102:    */   
/* 103:    */   public void writeFloatCorrect(float val)
/* 104:    */     throws IOException
/* 105:    */   {
/* 106:137 */     if (this.bigendian) {
/* 107:138 */       writeFloat(val);
/* 108:    */     } else {
/* 109:142 */       writeFloat(flipFloat(val));
/* 110:    */     }
/* 111:    */   }
/* 112:    */   
/* 113:    */   public float flipFloat(float val)
/* 114:    */     throws IOException
/* 115:    */   {
/* 116:152 */     int x = Float.floatToIntBits(val);
/* 117:153 */     int y = flipInt(x);
/* 118:154 */     return Float.intBitsToFloat(y);
/* 119:    */   }
/* 120:    */   
/* 121:    */   public void writeDoubleCorrect(double val)
/* 122:    */     throws IOException
/* 123:    */   {
/* 124:162 */     if (this.bigendian) {
/* 125:163 */       writeDouble(val);
/* 126:    */     } else {
/* 127:166 */       writeDouble(flipDouble(val));
/* 128:    */     }
/* 129:    */   }
/* 130:    */   
/* 131:    */   public double flipDouble(double val)
/* 132:    */   {
/* 133:176 */     long x = Double.doubleToLongBits(val);
/* 134:177 */     long y = flipLong(x);
/* 135:178 */     return Double.longBitsToDouble(y);
/* 136:    */   }
/* 137:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.converters.nifti.EndianCorrectOutputStream
 * JD-Core Version:    0.7.0.1
 */