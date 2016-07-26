/*   1:    */ package org.boon.primitive;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.InputStream;
/*   5:    */ import org.boon.Boon;
/*   6:    */ import org.boon.Logger;
/*   7:    */ 
/*   8:    */ public final class IOInputStream
/*   9:    */   extends InputStream
/*  10:    */ {
/*  11: 42 */   private static int defaultBufferSize = 100000;
/*  12:    */   private byte[] buffer;
/*  13:    */   private InputStream inputStream;
/*  14:    */   private int length;
/*  15:    */   private int position;
/*  16:    */   
/*  17:    */   public IOInputStream()
/*  18:    */   {
/*  19: 60 */     this.buffer = new byte[defaultBufferSize];
/*  20:    */   }
/*  21:    */   
/*  22:    */   public IOInputStream(int size)
/*  23:    */   {
/*  24: 66 */     this.buffer = new byte[size];
/*  25:    */   }
/*  26:    */   
/*  27:    */   public IOInputStream(int size, boolean autoGrowToMatch)
/*  28:    */   {
/*  29: 73 */     this.buffer = new byte[size];
/*  30:    */   }
/*  31:    */   
/*  32:    */   public static IOInputStream input(IOInputStream input)
/*  33:    */   {
/*  34: 80 */     if (input == null) {
/*  35: 81 */       return new IOInputStream();
/*  36:    */     }
/*  37: 83 */     return input;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public static IOInputStream input(IOInputStream input, int size)
/*  41:    */   {
/*  42: 91 */     if (input == null) {
/*  43: 92 */       return new IOInputStream(size);
/*  44:    */     }
/*  45: 94 */     return input;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public static IOInputStream auto(IOInputStream input, int size)
/*  49:    */   {
/*  50:102 */     if (input == null) {
/*  51:103 */       return new IOInputStream(size, true);
/*  52:    */     }
/*  53:105 */     return input;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public IOInputStream input(InputStream in)
/*  57:    */   {
/*  58:    */     try
/*  59:    */     {
/*  60:113 */       close();
/*  61:    */     }
/*  62:    */     catch (IOException e)
/*  63:    */     {
/*  64:115 */       Boon.logger("IO").warn(e.getMessage(), e);
/*  65:    */     }
/*  66:118 */     this.inputStream = in;
/*  67:119 */     return this;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public int read()
/*  71:    */     throws IOException
/*  72:    */   {
/*  73:127 */     if (this.position >= this.length)
/*  74:    */     {
/*  75:128 */       this.position = 0;
/*  76:129 */       int countRead = this.inputStream.read(this.buffer);
/*  77:130 */       this.length = countRead;
/*  78:131 */       if (this.length == -1) {
/*  79:132 */         return -1;
/*  80:    */       }
/*  81:    */     }
/*  82:137 */     int value = this.buffer[this.position];
/*  83:    */     
/*  84:139 */     this.position += 1;
/*  85:140 */     return value & 0xFF;
/*  86:    */   }
/*  87:    */   
/*  88:    */   public int read(byte[] destination, int destinationOffset, int destinationLength)
/*  89:    */     throws IOException
/*  90:    */   {
/*  91:147 */     if (this.inputStream == null) {
/*  92:148 */       throw new IOException("Stream is closed");
/*  93:    */     }
/*  94:152 */     int available = this.length - this.position;
/*  95:157 */     if (available >= destinationLength)
/*  96:    */     {
/*  97:158 */       System.arraycopy(this.buffer, this.position, destination, destinationOffset, destinationLength);
/*  98:    */       
/*  99:160 */       this.position += destinationLength;
/* 100:161 */       return destinationLength;
/* 101:    */     }
/* 102:165 */     if (available > 0)
/* 103:    */     {
/* 104:168 */       System.arraycopy(this.buffer, this.position, destination, destinationOffset, available);
/* 105:    */       
/* 106:    */ 
/* 107:    */ 
/* 108:172 */       destinationLength -= available;
/* 109:173 */       destinationOffset += available;
/* 110:    */     }
/* 111:183 */     this.position = 0;
/* 112:184 */     int countRead = this.inputStream.read(this.buffer);
/* 113:185 */     this.length = countRead;
/* 114:186 */     if (this.length == -1) {
/* 115:187 */       return available == 0 ? -1 : available;
/* 116:    */     }
/* 117:193 */     int amountToRead = destinationLength < this.length ? destinationLength : this.length;
/* 118:194 */     System.arraycopy(this.buffer, this.position, destination, destinationOffset, amountToRead);
/* 119:    */     
/* 120:196 */     this.position = amountToRead;
/* 121:    */     
/* 122:198 */     destinationLength -= amountToRead;
/* 123:199 */     destinationOffset += amountToRead;
/* 124:201 */     if (destinationLength == 0) {
/* 125:203 */       return amountToRead + available;
/* 126:    */     }
/* 127:207 */     countRead = read(destination, destinationOffset, destinationLength);
/* 128:208 */     if (countRead == -1) {
/* 129:209 */       return amountToRead + available;
/* 130:    */     }
/* 131:211 */     return amountToRead + available + countRead;
/* 132:    */   }
/* 133:    */   
/* 134:    */   public long skip(long n)
/* 135:    */     throws IOException
/* 136:    */   {
/* 137:229 */     throw new IOException("Skip not supported");
/* 138:    */   }
/* 139:    */   
/* 140:    */   public int available()
/* 141:    */     throws IOException
/* 142:    */   {
/* 143:233 */     int totalCount = this.length - this.position;
/* 144:234 */     int available = this.inputStream.available();
/* 145:235 */     return totalCount > 2147483647 - available ? 2147483647 : totalCount + available;
/* 146:    */   }
/* 147:    */   
/* 148:    */   public void mark(int limit) {}
/* 149:    */   
/* 150:    */   public void reset()
/* 151:    */     throws IOException
/* 152:    */   {
/* 153:244 */     throw new IOException("Resetting not supported");
/* 154:    */   }
/* 155:    */   
/* 156:    */   public boolean markSupported()
/* 157:    */   {
/* 158:248 */     return false;
/* 159:    */   }
/* 160:    */   
/* 161:    */   public void close()
/* 162:    */     throws IOException
/* 163:    */   {
/* 164:253 */     if (this.inputStream != null) {
/* 165:254 */       this.inputStream.close();
/* 166:    */     }
/* 167:256 */     this.inputStream = null;
/* 168:257 */     this.position = 0;
/* 169:258 */     this.length = 0;
/* 170:    */   }
/* 171:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.primitive.IOInputStream
 * JD-Core Version:    0.7.0.1
 */