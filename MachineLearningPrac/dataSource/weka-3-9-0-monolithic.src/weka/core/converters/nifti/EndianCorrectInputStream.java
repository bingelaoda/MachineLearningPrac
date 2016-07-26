/*   1:    */ package weka.core.converters.nifti;
/*   2:    */ 
/*   3:    */ import java.io.DataInputStream;
/*   4:    */ import java.io.FileInputStream;
/*   5:    */ import java.io.FileNotFoundException;
/*   6:    */ import java.io.IOException;
/*   7:    */ import java.io.InputStream;
/*   8:    */ 
/*   9:    */ public class EndianCorrectInputStream
/*  10:    */   extends DataInputStream
/*  11:    */ {
/*  12: 14 */   private boolean bigendian = true;
/*  13:    */   
/*  14:    */   public EndianCorrectInputStream(String filename, boolean be)
/*  15:    */     throws FileNotFoundException
/*  16:    */   {
/*  17: 23 */     super(new FileInputStream(filename));
/*  18: 24 */     this.bigendian = be;
/*  19:    */   }
/*  20:    */   
/*  21:    */   public EndianCorrectInputStream(InputStream is, boolean be)
/*  22:    */   {
/*  23: 33 */     super(is);
/*  24: 34 */     this.bigendian = be;
/*  25:    */   }
/*  26:    */   
/*  27:    */   public short readShortCorrect()
/*  28:    */     throws IOException
/*  29:    */   {
/*  30: 45 */     short val = readShort();
/*  31: 46 */     if (this.bigendian) {
/*  32: 47 */       return val;
/*  33:    */     }
/*  34: 50 */     int byte0 = val & 0xFF;
/*  35: 51 */     int byte1 = val >> 8 & 0xFF;
/*  36:    */     
/*  37: 53 */     return (short)(byte0 << 8 | byte1);
/*  38:    */   }
/*  39:    */   
/*  40:    */   public short flipShort(short val)
/*  41:    */   {
/*  42: 61 */     int byte0 = val & 0xFF;
/*  43: 62 */     int byte1 = val >> 8 & 0xFF;
/*  44:    */     
/*  45: 64 */     return (short)(byte0 << 8 | byte1);
/*  46:    */   }
/*  47:    */   
/*  48:    */   public int readIntCorrect()
/*  49:    */     throws IOException
/*  50:    */   {
/*  51: 74 */     int val = readInt();
/*  52: 75 */     if (this.bigendian) {
/*  53: 76 */       return val;
/*  54:    */     }
/*  55: 80 */     int byte0 = val & 0xFF;
/*  56: 81 */     int byte1 = val >> 8 & 0xFF;
/*  57: 82 */     int byte2 = val >> 16 & 0xFF;
/*  58: 83 */     int byte3 = val >> 24 & 0xFF;
/*  59:    */     
/*  60: 85 */     return byte0 << 24 | byte1 << 16 | byte2 << 8 | byte3;
/*  61:    */   }
/*  62:    */   
/*  63:    */   public int flipInt(int val)
/*  64:    */   {
/*  65: 94 */     int byte0 = val & 0xFF;
/*  66: 95 */     int byte1 = val >> 8 & 0xFF;
/*  67: 96 */     int byte2 = val >> 16 & 0xFF;
/*  68: 97 */     int byte3 = val >> 24 & 0xFF;
/*  69:    */     
/*  70: 99 */     return byte0 << 24 | byte1 << 16 | byte2 << 8 | byte3;
/*  71:    */   }
/*  72:    */   
/*  73:    */   public long readLongCorrect()
/*  74:    */     throws IOException
/*  75:    */   {
/*  76:108 */     long val = readLong();
/*  77:109 */     if (this.bigendian) {
/*  78:110 */       return val;
/*  79:    */     }
/*  80:114 */     return flipLong(val);
/*  81:    */   }
/*  82:    */   
/*  83:    */   public long flipLong(long val)
/*  84:    */   {
/*  85:123 */     long byte0 = val & 0xFF;
/*  86:124 */     long byte1 = val >> 8 & 0xFF;
/*  87:125 */     long byte2 = val >> 16 & 0xFF;
/*  88:126 */     long byte3 = val >> 24 & 0xFF;
/*  89:127 */     long byte4 = val >> 32 & 0xFF;
/*  90:128 */     long byte5 = val >> 40 & 0xFF;
/*  91:129 */     long byte6 = val >> 48 & 0xFF;
/*  92:130 */     long byte7 = val >> 56 & 0xFF;
/*  93:    */     
/*  94:132 */     return byte0 << 56 | byte1 << 48 | byte2 << 40 | byte3 << 32 | byte4 << 24 | byte5 << 16 | byte6 << 8 | byte7;
/*  95:    */   }
/*  96:    */   
/*  97:    */   public float readFloatCorrect()
/*  98:    */     throws IOException
/*  99:    */   {
/* 100:    */     float val;
/* 101:    */     float val;
/* 102:142 */     if (this.bigendian)
/* 103:    */     {
/* 104:143 */       val = readFloat();
/* 105:    */     }
/* 106:    */     else
/* 107:    */     {
/* 108:147 */       int x = readUnsignedByte();
/* 109:148 */       x |= readUnsignedByte() << 8;
/* 110:149 */       x |= readUnsignedByte() << 16;
/* 111:150 */       x |= readUnsignedByte() << 24;
/* 112:151 */       val = Float.intBitsToFloat(x);
/* 113:    */     }
/* 114:153 */     return val;
/* 115:    */   }
/* 116:    */   
/* 117:    */   public float flipFloat(float val)
/* 118:    */     throws IOException
/* 119:    */   {
/* 120:161 */     int x = Float.floatToIntBits(val);
/* 121:162 */     int y = flipInt(x);
/* 122:163 */     return Float.intBitsToFloat(y);
/* 123:    */   }
/* 124:    */   
/* 125:    */   public double readDoubleCorrect()
/* 126:    */     throws IOException
/* 127:    */   {
/* 128:    */     double val;
/* 129:    */     double val;
/* 130:171 */     if (this.bigendian)
/* 131:    */     {
/* 132:172 */       val = readDouble();
/* 133:    */     }
/* 134:    */     else
/* 135:    */     {
/* 136:175 */       long x = readUnsignedByte();
/* 137:176 */       x |= readUnsignedByte() << 8;
/* 138:177 */       x |= readUnsignedByte() << 16;
/* 139:178 */       x |= readUnsignedByte() << 24;
/* 140:179 */       x |= readUnsignedByte() << 32;
/* 141:180 */       x |= readUnsignedByte() << 40;
/* 142:181 */       x |= readUnsignedByte() << 48;
/* 143:182 */       x |= readUnsignedByte() << 56;
/* 144:183 */       val = Double.longBitsToDouble(x);
/* 145:    */     }
/* 146:185 */     return val;
/* 147:    */   }
/* 148:    */   
/* 149:    */   public double flipDouble(double val)
/* 150:    */   {
/* 151:193 */     long x = Double.doubleToLongBits(val);
/* 152:194 */     long y = flipLong(x);
/* 153:195 */     return Double.longBitsToDouble(y);
/* 154:    */   }
/* 155:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.converters.nifti.EndianCorrectInputStream
 * JD-Core Version:    0.7.0.1
 */