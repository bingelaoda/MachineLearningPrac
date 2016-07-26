/*   1:    */ package org.apache.commons.io.input;
/*   2:    */ 
/*   3:    */ import java.io.EOFException;
/*   4:    */ import java.io.IOException;
/*   5:    */ import java.io.InputStream;
/*   6:    */ 
/*   7:    */ public class NullInputStream
/*   8:    */   extends InputStream
/*   9:    */ {
/*  10:    */   private long size;
/*  11:    */   private long position;
/*  12: 67 */   private long mark = -1L;
/*  13:    */   private long readlimit;
/*  14:    */   private boolean eof;
/*  15:    */   private boolean throwEofException;
/*  16:    */   private boolean markSupported;
/*  17:    */   
/*  18:    */   public NullInputStream(long size)
/*  19:    */   {
/*  20: 80 */     this(size, true, false);
/*  21:    */   }
/*  22:    */   
/*  23:    */   public NullInputStream(long size, boolean markSupported, boolean throwEofException)
/*  24:    */   {
/*  25: 95 */     this.size = size;
/*  26: 96 */     this.markSupported = markSupported;
/*  27: 97 */     this.throwEofException = throwEofException;
/*  28:    */   }
/*  29:    */   
/*  30:    */   public long getPosition()
/*  31:    */   {
/*  32:106 */     return this.position;
/*  33:    */   }
/*  34:    */   
/*  35:    */   public long getSize()
/*  36:    */   {
/*  37:115 */     return this.size;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public int available()
/*  41:    */   {
/*  42:124 */     long avail = this.size - this.position;
/*  43:125 */     if (avail <= 0L) {
/*  44:126 */       return 0;
/*  45:    */     }
/*  46:127 */     if (avail > 2147483647L) {
/*  47:128 */       return 2147483647;
/*  48:    */     }
/*  49:130 */     return (int)avail;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public void close()
/*  53:    */     throws IOException
/*  54:    */   {
/*  55:141 */     this.eof = false;
/*  56:142 */     this.position = 0L;
/*  57:143 */     this.mark = -1L;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public synchronized void mark(int readlimit)
/*  61:    */   {
/*  62:154 */     if (!this.markSupported) {
/*  63:155 */       throw new UnsupportedOperationException("Mark not supported");
/*  64:    */     }
/*  65:157 */     this.mark = this.position;
/*  66:158 */     this.readlimit = readlimit;
/*  67:    */   }
/*  68:    */   
/*  69:    */   public boolean markSupported()
/*  70:    */   {
/*  71:167 */     return this.markSupported;
/*  72:    */   }
/*  73:    */   
/*  74:    */   public int read()
/*  75:    */     throws IOException
/*  76:    */   {
/*  77:181 */     if (this.eof) {
/*  78:182 */       throw new IOException("Read after end of file");
/*  79:    */     }
/*  80:184 */     if (this.position == this.size) {
/*  81:185 */       return doEndOfFile();
/*  82:    */     }
/*  83:187 */     this.position += 1L;
/*  84:188 */     return processByte();
/*  85:    */   }
/*  86:    */   
/*  87:    */   public int read(byte[] bytes)
/*  88:    */     throws IOException
/*  89:    */   {
/*  90:203 */     return read(bytes, 0, bytes.length);
/*  91:    */   }
/*  92:    */   
/*  93:    */   public int read(byte[] bytes, int offset, int length)
/*  94:    */     throws IOException
/*  95:    */   {
/*  96:220 */     if (this.eof) {
/*  97:221 */       throw new IOException("Read after end of file");
/*  98:    */     }
/*  99:223 */     if (this.position == this.size) {
/* 100:224 */       return doEndOfFile();
/* 101:    */     }
/* 102:226 */     this.position += length;
/* 103:227 */     int returnLength = length;
/* 104:228 */     if (this.position > this.size)
/* 105:    */     {
/* 106:229 */       returnLength = length - (int)(this.position - this.size);
/* 107:230 */       this.position = this.size;
/* 108:    */     }
/* 109:232 */     processBytes(bytes, offset, returnLength);
/* 110:233 */     return returnLength;
/* 111:    */   }
/* 112:    */   
/* 113:    */   public synchronized void reset()
/* 114:    */     throws IOException
/* 115:    */   {
/* 116:245 */     if (!this.markSupported) {
/* 117:246 */       throw new UnsupportedOperationException("Mark not supported");
/* 118:    */     }
/* 119:248 */     if (this.mark < 0L) {
/* 120:249 */       throw new IOException("No position has been marked");
/* 121:    */     }
/* 122:251 */     if (this.position > this.mark + this.readlimit) {
/* 123:252 */       throw new IOException("Marked position [" + this.mark + "] is no longer valid - passed the read limit [" + this.readlimit + "]");
/* 124:    */     }
/* 125:256 */     this.position = this.mark;
/* 126:257 */     this.eof = false;
/* 127:    */   }
/* 128:    */   
/* 129:    */   public long skip(long numberOfBytes)
/* 130:    */     throws IOException
/* 131:    */   {
/* 132:272 */     if (this.eof) {
/* 133:273 */       throw new IOException("Skip after end of file");
/* 134:    */     }
/* 135:275 */     if (this.position == this.size) {
/* 136:276 */       return doEndOfFile();
/* 137:    */     }
/* 138:278 */     this.position += numberOfBytes;
/* 139:279 */     long returnLength = numberOfBytes;
/* 140:280 */     if (this.position > this.size)
/* 141:    */     {
/* 142:281 */       returnLength = numberOfBytes - (this.position - this.size);
/* 143:282 */       this.position = this.size;
/* 144:    */     }
/* 145:284 */     return returnLength;
/* 146:    */   }
/* 147:    */   
/* 148:    */   protected int processByte()
/* 149:    */   {
/* 150:296 */     return 0;
/* 151:    */   }
/* 152:    */   
/* 153:    */   protected void processBytes(byte[] bytes, int offset, int length) {}
/* 154:    */   
/* 155:    */   private int doEndOfFile()
/* 156:    */     throws EOFException
/* 157:    */   {
/* 158:322 */     this.eof = true;
/* 159:323 */     if (this.throwEofException) {
/* 160:324 */       throw new EOFException();
/* 161:    */     }
/* 162:326 */     return -1;
/* 163:    */   }
/* 164:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.io.input.NullInputStream
 * JD-Core Version:    0.7.0.1
 */