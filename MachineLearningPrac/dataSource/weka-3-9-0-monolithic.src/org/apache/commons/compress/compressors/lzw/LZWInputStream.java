/*   1:    */ package org.apache.commons.compress.compressors.lzw;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.InputStream;
/*   5:    */ import java.nio.ByteOrder;
/*   6:    */ import org.apache.commons.compress.compressors.CompressorInputStream;
/*   7:    */ import org.apache.commons.compress.utils.BitInputStream;
/*   8:    */ 
/*   9:    */ public abstract class LZWInputStream
/*  10:    */   extends CompressorInputStream
/*  11:    */ {
/*  12:    */   protected static final int DEFAULT_CODE_SIZE = 9;
/*  13:    */   protected static final int UNUSED_PREFIX = -1;
/*  14: 40 */   private final byte[] oneByte = new byte[1];
/*  15:    */   protected final BitInputStream in;
/*  16: 43 */   private int clearCode = -1;
/*  17: 44 */   private int codeSize = 9;
/*  18:    */   private byte previousCodeFirstChar;
/*  19: 46 */   private int previousCode = -1;
/*  20:    */   private int tableSize;
/*  21:    */   private int[] prefixes;
/*  22:    */   private byte[] characters;
/*  23:    */   private byte[] outputStack;
/*  24:    */   private int outputStackLocation;
/*  25:    */   
/*  26:    */   protected LZWInputStream(InputStream inputStream, ByteOrder byteOrder)
/*  27:    */   {
/*  28: 54 */     this.in = new BitInputStream(inputStream, byteOrder);
/*  29:    */   }
/*  30:    */   
/*  31:    */   public void close()
/*  32:    */     throws IOException
/*  33:    */   {
/*  34: 59 */     this.in.close();
/*  35:    */   }
/*  36:    */   
/*  37:    */   public int read()
/*  38:    */     throws IOException
/*  39:    */   {
/*  40: 64 */     int ret = read(this.oneByte);
/*  41: 65 */     if (ret < 0) {
/*  42: 66 */       return ret;
/*  43:    */     }
/*  44: 68 */     return 0xFF & this.oneByte[0];
/*  45:    */   }
/*  46:    */   
/*  47:    */   public int read(byte[] b, int off, int len)
/*  48:    */     throws IOException
/*  49:    */   {
/*  50: 73 */     int bytesRead = readFromStack(b, off, len);
/*  51: 74 */     while (len - bytesRead > 0)
/*  52:    */     {
/*  53: 75 */       int result = decompressNextSymbol();
/*  54: 76 */       if (result < 0)
/*  55:    */       {
/*  56: 77 */         if (bytesRead > 0)
/*  57:    */         {
/*  58: 78 */           count(bytesRead);
/*  59: 79 */           return bytesRead;
/*  60:    */         }
/*  61: 81 */         return result;
/*  62:    */       }
/*  63: 83 */       bytesRead += readFromStack(b, off + bytesRead, len - bytesRead);
/*  64:    */     }
/*  65: 85 */     count(bytesRead);
/*  66: 86 */     return bytesRead;
/*  67:    */   }
/*  68:    */   
/*  69:    */   protected abstract int decompressNextSymbol()
/*  70:    */     throws IOException;
/*  71:    */   
/*  72:    */   protected abstract int addEntry(int paramInt, byte paramByte)
/*  73:    */     throws IOException;
/*  74:    */   
/*  75:    */   protected void setClearCode(int codeSize)
/*  76:    */   {
/*  77:104 */     this.clearCode = (1 << codeSize - 1);
/*  78:    */   }
/*  79:    */   
/*  80:    */   protected void initializeTables(int maxCodeSize)
/*  81:    */   {
/*  82:111 */     int maxTableSize = 1 << maxCodeSize;
/*  83:112 */     this.prefixes = new int[maxTableSize];
/*  84:113 */     this.characters = new byte[maxTableSize];
/*  85:114 */     this.outputStack = new byte[maxTableSize];
/*  86:115 */     this.outputStackLocation = maxTableSize;
/*  87:116 */     int max = 256;
/*  88:117 */     for (int i = 0; i < 256; i++)
/*  89:    */     {
/*  90:118 */       this.prefixes[i] = -1;
/*  91:119 */       this.characters[i] = ((byte)i);
/*  92:    */     }
/*  93:    */   }
/*  94:    */   
/*  95:    */   protected int readNextCode()
/*  96:    */     throws IOException
/*  97:    */   {
/*  98:127 */     if (this.codeSize > 31) {
/*  99:128 */       throw new IllegalArgumentException("code size must not be bigger than 31");
/* 100:    */     }
/* 101:130 */     return (int)this.in.readBits(this.codeSize);
/* 102:    */   }
/* 103:    */   
/* 104:    */   protected int addEntry(int previousCode, byte character, int maxTableSize)
/* 105:    */   {
/* 106:138 */     if (this.tableSize < maxTableSize)
/* 107:    */     {
/* 108:139 */       this.prefixes[this.tableSize] = previousCode;
/* 109:140 */       this.characters[this.tableSize] = character;
/* 110:141 */       return this.tableSize++;
/* 111:    */     }
/* 112:143 */     return -1;
/* 113:    */   }
/* 114:    */   
/* 115:    */   protected int addRepeatOfPreviousCode()
/* 116:    */     throws IOException
/* 117:    */   {
/* 118:150 */     if (this.previousCode == -1) {
/* 119:152 */       throw new IOException("The first code can't be a reference to its preceding code");
/* 120:    */     }
/* 121:154 */     return addEntry(this.previousCode, this.previousCodeFirstChar);
/* 122:    */   }
/* 123:    */   
/* 124:    */   protected int expandCodeToOutputStack(int code, boolean addedUnfinishedEntry)
/* 125:    */     throws IOException
/* 126:    */   {
/* 127:163 */     for (int entry = code; entry >= 0; entry = this.prefixes[entry]) {
/* 128:164 */       this.outputStack[(--this.outputStackLocation)] = this.characters[entry];
/* 129:    */     }
/* 130:166 */     if ((this.previousCode != -1) && (!addedUnfinishedEntry)) {
/* 131:167 */       addEntry(this.previousCode, this.outputStack[this.outputStackLocation]);
/* 132:    */     }
/* 133:169 */     this.previousCode = code;
/* 134:170 */     this.previousCodeFirstChar = this.outputStack[this.outputStackLocation];
/* 135:171 */     return this.outputStackLocation;
/* 136:    */   }
/* 137:    */   
/* 138:    */   private int readFromStack(byte[] b, int off, int len)
/* 139:    */   {
/* 140:175 */     int remainingInStack = this.outputStack.length - this.outputStackLocation;
/* 141:176 */     if (remainingInStack > 0)
/* 142:    */     {
/* 143:177 */       int maxLength = Math.min(remainingInStack, len);
/* 144:178 */       System.arraycopy(this.outputStack, this.outputStackLocation, b, off, maxLength);
/* 145:179 */       this.outputStackLocation += maxLength;
/* 146:180 */       return maxLength;
/* 147:    */     }
/* 148:182 */     return 0;
/* 149:    */   }
/* 150:    */   
/* 151:    */   protected int getCodeSize()
/* 152:    */   {
/* 153:186 */     return this.codeSize;
/* 154:    */   }
/* 155:    */   
/* 156:    */   protected void resetCodeSize()
/* 157:    */   {
/* 158:190 */     setCodeSize(9);
/* 159:    */   }
/* 160:    */   
/* 161:    */   protected void setCodeSize(int cs)
/* 162:    */   {
/* 163:194 */     this.codeSize = cs;
/* 164:    */   }
/* 165:    */   
/* 166:    */   protected void incrementCodeSize()
/* 167:    */   {
/* 168:198 */     this.codeSize += 1;
/* 169:    */   }
/* 170:    */   
/* 171:    */   protected void resetPreviousCode()
/* 172:    */   {
/* 173:202 */     this.previousCode = -1;
/* 174:    */   }
/* 175:    */   
/* 176:    */   protected int getPrefix(int offset)
/* 177:    */   {
/* 178:206 */     return this.prefixes[offset];
/* 179:    */   }
/* 180:    */   
/* 181:    */   protected void setPrefix(int offset, int value)
/* 182:    */   {
/* 183:210 */     this.prefixes[offset] = value;
/* 184:    */   }
/* 185:    */   
/* 186:    */   protected int getPrefixesLength()
/* 187:    */   {
/* 188:214 */     return this.prefixes.length;
/* 189:    */   }
/* 190:    */   
/* 191:    */   protected int getClearCode()
/* 192:    */   {
/* 193:218 */     return this.clearCode;
/* 194:    */   }
/* 195:    */   
/* 196:    */   protected int getTableSize()
/* 197:    */   {
/* 198:222 */     return this.tableSize;
/* 199:    */   }
/* 200:    */   
/* 201:    */   protected void setTableSize(int newSize)
/* 202:    */   {
/* 203:226 */     this.tableSize = newSize;
/* 204:    */   }
/* 205:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.compressors.lzw.LZWInputStream
 * JD-Core Version:    0.7.0.1
 */