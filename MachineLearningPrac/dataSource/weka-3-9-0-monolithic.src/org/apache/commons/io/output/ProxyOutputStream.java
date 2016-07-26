/*  1:   */ package org.apache.commons.io.output;
/*  2:   */ 
/*  3:   */ import java.io.FilterOutputStream;
/*  4:   */ import java.io.IOException;
/*  5:   */ import java.io.OutputStream;
/*  6:   */ 
/*  7:   */ public class ProxyOutputStream
/*  8:   */   extends FilterOutputStream
/*  9:   */ {
/* 10:   */   public ProxyOutputStream(OutputStream proxy)
/* 11:   */   {
/* 12:40 */     super(proxy);
/* 13:   */   }
/* 14:   */   
/* 15:   */   public void write(int idx)
/* 16:   */     throws IOException
/* 17:   */   {
/* 18:46 */     this.out.write(idx);
/* 19:   */   }
/* 20:   */   
/* 21:   */   public void write(byte[] bts)
/* 22:   */     throws IOException
/* 23:   */   {
/* 24:51 */     this.out.write(bts);
/* 25:   */   }
/* 26:   */   
/* 27:   */   public void write(byte[] bts, int st, int end)
/* 28:   */     throws IOException
/* 29:   */   {
/* 30:56 */     this.out.write(bts, st, end);
/* 31:   */   }
/* 32:   */   
/* 33:   */   public void flush()
/* 34:   */     throws IOException
/* 35:   */   {
/* 36:61 */     this.out.flush();
/* 37:   */   }
/* 38:   */   
/* 39:   */   public void close()
/* 40:   */     throws IOException
/* 41:   */   {
/* 42:66 */     this.out.close();
/* 43:   */   }
/* 44:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.io.output.ProxyOutputStream
 * JD-Core Version:    0.7.0.1
 */