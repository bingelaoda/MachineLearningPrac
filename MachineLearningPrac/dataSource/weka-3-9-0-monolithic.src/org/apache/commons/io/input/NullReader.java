/*   1:    */ package org.apache.commons.io.input;
/*   2:    */ 
/*   3:    */ import java.io.EOFException;
/*   4:    */ import java.io.IOException;
/*   5:    */ import java.io.Reader;
/*   6:    */ 
/*   7:    */ public class NullReader
/*   8:    */   extends Reader
/*   9:    */ {
/*  10:    */   private long size;
/*  11:    */   private long position;
/*  12: 67 */   private long mark = -1L;
/*  13:    */   private long readlimit;
/*  14:    */   private boolean eof;
/*  15:    */   private boolean throwEofException;
/*  16:    */   private boolean markSupported;
/*  17:    */   
/*  18:    */   public NullReader(long size)
/*  19:    */   {
/*  20: 80 */     this(size, true, false);
/*  21:    */   }
/*  22:    */   
/*  23:    */   public NullReader(long size, boolean markSupported, boolean throwEofException)
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
/*  40:    */   public void close()
/*  41:    */     throws IOException
/*  42:    */   {
/*  43:125 */     this.eof = false;
/*  44:126 */     this.position = 0L;
/*  45:127 */     this.mark = -1L;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public synchronized void mark(int readlimit)
/*  49:    */   {
/*  50:138 */     if (!this.markSupported) {
/*  51:139 */       throw new UnsupportedOperationException("Mark not supported");
/*  52:    */     }
/*  53:141 */     this.mark = this.position;
/*  54:142 */     this.readlimit = readlimit;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public boolean markSupported()
/*  58:    */   {
/*  59:151 */     return this.markSupported;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public int read()
/*  63:    */     throws IOException
/*  64:    */   {
/*  65:165 */     if (this.eof) {
/*  66:166 */       throw new IOException("Read after end of file");
/*  67:    */     }
/*  68:168 */     if (this.position == this.size) {
/*  69:169 */       return doEndOfFile();
/*  70:    */     }
/*  71:171 */     this.position += 1L;
/*  72:172 */     return processChar();
/*  73:    */   }
/*  74:    */   
/*  75:    */   public int read(char[] chars)
/*  76:    */     throws IOException
/*  77:    */   {
/*  78:187 */     return read(chars, 0, chars.length);
/*  79:    */   }
/*  80:    */   
/*  81:    */   public int read(char[] chars, int offset, int length)
/*  82:    */     throws IOException
/*  83:    */   {
/*  84:204 */     if (this.eof) {
/*  85:205 */       throw new IOException("Read after end of file");
/*  86:    */     }
/*  87:207 */     if (this.position == this.size) {
/*  88:208 */       return doEndOfFile();
/*  89:    */     }
/*  90:210 */     this.position += length;
/*  91:211 */     int returnLength = length;
/*  92:212 */     if (this.position > this.size)
/*  93:    */     {
/*  94:213 */       returnLength = length - (int)(this.position - this.size);
/*  95:214 */       this.position = this.size;
/*  96:    */     }
/*  97:216 */     processChars(chars, offset, returnLength);
/*  98:217 */     return returnLength;
/*  99:    */   }
/* 100:    */   
/* 101:    */   public synchronized void reset()
/* 102:    */     throws IOException
/* 103:    */   {
/* 104:229 */     if (!this.markSupported) {
/* 105:230 */       throw new UnsupportedOperationException("Mark not supported");
/* 106:    */     }
/* 107:232 */     if (this.mark < 0L) {
/* 108:233 */       throw new IOException("No position has been marked");
/* 109:    */     }
/* 110:235 */     if (this.position > this.mark + this.readlimit) {
/* 111:236 */       throw new IOException("Marked position [" + this.mark + "] is no longer valid - passed the read limit [" + this.readlimit + "]");
/* 112:    */     }
/* 113:240 */     this.position = this.mark;
/* 114:241 */     this.eof = false;
/* 115:    */   }
/* 116:    */   
/* 117:    */   public long skip(long numberOfChars)
/* 118:    */     throws IOException
/* 119:    */   {
/* 120:256 */     if (this.eof) {
/* 121:257 */       throw new IOException("Skip after end of file");
/* 122:    */     }
/* 123:259 */     if (this.position == this.size) {
/* 124:260 */       return doEndOfFile();
/* 125:    */     }
/* 126:262 */     this.position += numberOfChars;
/* 127:263 */     long returnLength = numberOfChars;
/* 128:264 */     if (this.position > this.size)
/* 129:    */     {
/* 130:265 */       returnLength = numberOfChars - (this.position - this.size);
/* 131:266 */       this.position = this.size;
/* 132:    */     }
/* 133:268 */     return returnLength;
/* 134:    */   }
/* 135:    */   
/* 136:    */   protected int processChar()
/* 137:    */   {
/* 138:280 */     return 0;
/* 139:    */   }
/* 140:    */   
/* 141:    */   protected void processChars(char[] chars, int offset, int length) {}
/* 142:    */   
/* 143:    */   private int doEndOfFile()
/* 144:    */     throws EOFException
/* 145:    */   {
/* 146:306 */     this.eof = true;
/* 147:307 */     if (this.throwEofException) {
/* 148:308 */       throw new EOFException();
/* 149:    */     }
/* 150:310 */     return -1;
/* 151:    */   }
/* 152:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.io.input.NullReader
 * JD-Core Version:    0.7.0.1
 */