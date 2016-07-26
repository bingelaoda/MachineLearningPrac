/*  1:   */ package org.apache.commons.io.input;
/*  2:   */ 
/*  3:   */ import java.io.FilterInputStream;
/*  4:   */ import java.io.IOException;
/*  5:   */ import java.io.InputStream;
/*  6:   */ 
/*  7:   */ public abstract class ProxyInputStream
/*  8:   */   extends FilterInputStream
/*  9:   */ {
/* 10:   */   public ProxyInputStream(InputStream proxy)
/* 11:   */   {
/* 12:43 */     super(proxy);
/* 13:   */   }
/* 14:   */   
/* 15:   */   public int read()
/* 16:   */     throws IOException
/* 17:   */   {
/* 18:49 */     return this.in.read();
/* 19:   */   }
/* 20:   */   
/* 21:   */   public int read(byte[] bts)
/* 22:   */     throws IOException
/* 23:   */   {
/* 24:54 */     return this.in.read(bts);
/* 25:   */   }
/* 26:   */   
/* 27:   */   public int read(byte[] bts, int st, int end)
/* 28:   */     throws IOException
/* 29:   */   {
/* 30:59 */     return this.in.read(bts, st, end);
/* 31:   */   }
/* 32:   */   
/* 33:   */   public long skip(long ln)
/* 34:   */     throws IOException
/* 35:   */   {
/* 36:64 */     return this.in.skip(ln);
/* 37:   */   }
/* 38:   */   
/* 39:   */   public int available()
/* 40:   */     throws IOException
/* 41:   */   {
/* 42:69 */     return this.in.available();
/* 43:   */   }
/* 44:   */   
/* 45:   */   public void close()
/* 46:   */     throws IOException
/* 47:   */   {
/* 48:74 */     this.in.close();
/* 49:   */   }
/* 50:   */   
/* 51:   */   public synchronized void mark(int idx)
/* 52:   */   {
/* 53:79 */     this.in.mark(idx);
/* 54:   */   }
/* 55:   */   
/* 56:   */   public synchronized void reset()
/* 57:   */     throws IOException
/* 58:   */   {
/* 59:84 */     this.in.reset();
/* 60:   */   }
/* 61:   */   
/* 62:   */   public boolean markSupported()
/* 63:   */   {
/* 64:89 */     return this.in.markSupported();
/* 65:   */   }
/* 66:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.io.input.ProxyInputStream
 * JD-Core Version:    0.7.0.1
 */