/*  1:   */ package org.apache.commons.io.input;
/*  2:   */ 
/*  3:   */ import java.io.FilterReader;
/*  4:   */ import java.io.IOException;
/*  5:   */ import java.io.Reader;
/*  6:   */ 
/*  7:   */ public abstract class ProxyReader
/*  8:   */   extends FilterReader
/*  9:   */ {
/* 10:   */   public ProxyReader(Reader proxy)
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
/* 21:   */   public int read(char[] chr)
/* 22:   */     throws IOException
/* 23:   */   {
/* 24:54 */     return this.in.read(chr);
/* 25:   */   }
/* 26:   */   
/* 27:   */   public int read(char[] chr, int st, int end)
/* 28:   */     throws IOException
/* 29:   */   {
/* 30:59 */     return this.in.read(chr, st, end);
/* 31:   */   }
/* 32:   */   
/* 33:   */   public long skip(long ln)
/* 34:   */     throws IOException
/* 35:   */   {
/* 36:64 */     return this.in.skip(ln);
/* 37:   */   }
/* 38:   */   
/* 39:   */   public boolean ready()
/* 40:   */     throws IOException
/* 41:   */   {
/* 42:69 */     return this.in.ready();
/* 43:   */   }
/* 44:   */   
/* 45:   */   public void close()
/* 46:   */     throws IOException
/* 47:   */   {
/* 48:74 */     this.in.close();
/* 49:   */   }
/* 50:   */   
/* 51:   */   public synchronized void mark(int idx)
/* 52:   */     throws IOException
/* 53:   */   {
/* 54:79 */     this.in.mark(idx);
/* 55:   */   }
/* 56:   */   
/* 57:   */   public synchronized void reset()
/* 58:   */     throws IOException
/* 59:   */   {
/* 60:84 */     this.in.reset();
/* 61:   */   }
/* 62:   */   
/* 63:   */   public boolean markSupported()
/* 64:   */   {
/* 65:89 */     return this.in.markSupported();
/* 66:   */   }
/* 67:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.io.input.ProxyReader
 * JD-Core Version:    0.7.0.1
 */