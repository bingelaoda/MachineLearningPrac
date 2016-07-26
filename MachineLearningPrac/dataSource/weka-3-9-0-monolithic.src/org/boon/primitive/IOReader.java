/*   1:    */ package org.boon.primitive;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.Reader;
/*   5:    */ import org.boon.Boon;
/*   6:    */ import org.boon.Logger;
/*   7:    */ 
/*   8:    */ public class IOReader
/*   9:    */   extends Reader
/*  10:    */ {
/*  11: 42 */   private static int defaultBufferSize = 100000;
/*  12:    */   private char[] buffer;
/*  13:    */   private Reader reader;
/*  14:    */   private int length;
/*  15:    */   private int position;
/*  16:    */   
/*  17:    */   public IOReader(int size)
/*  18:    */   {
/*  19: 58 */     this.reader = this.reader;
/*  20: 59 */     this.buffer = new char[size];
/*  21:    */   }
/*  22:    */   
/*  23:    */   public IOReader()
/*  24:    */   {
/*  25: 64 */     this.reader = this.reader;
/*  26: 65 */     this.buffer = new char[defaultBufferSize];
/*  27:    */   }
/*  28:    */   
/*  29:    */   public static IOReader reader(IOReader reader, int size)
/*  30:    */   {
/*  31: 72 */     if (reader == null) {
/*  32: 73 */       return new IOReader(size);
/*  33:    */     }
/*  34: 75 */     return reader;
/*  35:    */   }
/*  36:    */   
/*  37:    */   public IOReader reader(Reader in)
/*  38:    */   {
/*  39:    */     try
/*  40:    */     {
/*  41: 85 */       close();
/*  42:    */     }
/*  43:    */     catch (IOException e)
/*  44:    */     {
/*  45: 87 */       Boon.logger("IO").warn(e.getMessage(), e);
/*  46:    */     }
/*  47: 90 */     this.reader = in;
/*  48: 91 */     return this;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public int read()
/*  52:    */     throws IOException
/*  53:    */   {
/*  54: 99 */     if (this.position >= this.length)
/*  55:    */     {
/*  56:100 */       this.position = 0;
/*  57:101 */       int countRead = this.reader.read(this.buffer);
/*  58:102 */       this.length = countRead;
/*  59:103 */       if (this.length == -1) {
/*  60:104 */         return -1;
/*  61:    */       }
/*  62:    */     }
/*  63:109 */     int value = this.buffer[this.position];
/*  64:    */     
/*  65:111 */     this.position += 1;
/*  66:112 */     return value & 0xFF;
/*  67:    */   }
/*  68:    */   
/*  69:    */   public int read(char[] destination, int destinationOffset, int destinationLength)
/*  70:    */     throws IOException
/*  71:    */   {
/*  72:118 */     if (this.reader == null) {
/*  73:119 */       throw new IOException("Stream is closed");
/*  74:    */     }
/*  75:123 */     int available = this.length - this.position;
/*  76:128 */     if (available >= destinationLength)
/*  77:    */     {
/*  78:129 */       System.arraycopy(this.buffer, this.position, destination, destinationOffset, destinationLength);
/*  79:    */       
/*  80:131 */       this.position += destinationLength;
/*  81:132 */       return destinationLength;
/*  82:    */     }
/*  83:136 */     if (available > 0)
/*  84:    */     {
/*  85:139 */       System.arraycopy(this.buffer, this.position, destination, destinationOffset, available);
/*  86:    */       
/*  87:    */ 
/*  88:    */ 
/*  89:143 */       destinationLength -= available;
/*  90:144 */       destinationOffset += available;
/*  91:    */     }
/*  92:154 */     this.position = 0;
/*  93:155 */     int countRead = this.reader.read(this.buffer);
/*  94:156 */     this.length = countRead;
/*  95:157 */     if (this.length == -1) {
/*  96:158 */       return available == 0 ? -1 : available;
/*  97:    */     }
/*  98:164 */     int amountToRead = destinationLength < this.length ? destinationLength : this.length;
/*  99:165 */     System.arraycopy(this.buffer, this.position, destination, destinationOffset, amountToRead);
/* 100:    */     
/* 101:167 */     this.position = amountToRead;
/* 102:    */     
/* 103:169 */     destinationLength -= amountToRead;
/* 104:170 */     destinationOffset += amountToRead;
/* 105:172 */     if (destinationLength == 0) {
/* 106:174 */       return amountToRead + available;
/* 107:    */     }
/* 108:178 */     countRead = read(destination, destinationOffset, destinationLength);
/* 109:179 */     if (countRead == -1) {
/* 110:180 */       return amountToRead + available;
/* 111:    */     }
/* 112:182 */     return amountToRead + available + countRead;
/* 113:    */   }
/* 114:    */   
/* 115:    */   public long skip(long n)
/* 116:    */     throws IOException
/* 117:    */   {
/* 118:194 */     throw new IOException("Skip not supported");
/* 119:    */   }
/* 120:    */   
/* 121:    */   public void close()
/* 122:    */     throws IOException
/* 123:    */   {
/* 124:199 */     if (this.reader != null) {
/* 125:200 */       this.reader.close();
/* 126:    */     }
/* 127:202 */     this.reader = null;
/* 128:203 */     this.position = 0;
/* 129:204 */     this.length = 0;
/* 130:    */   }
/* 131:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.primitive.IOReader
 * JD-Core Version:    0.7.0.1
 */