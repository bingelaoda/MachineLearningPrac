/*  1:   */ package org.apache.commons.io.output;
/*  2:   */ 
/*  3:   */ import java.io.IOException;
/*  4:   */ import java.io.OutputStream;
/*  5:   */ 
/*  6:   */ public class TeeOutputStream
/*  7:   */   extends ProxyOutputStream
/*  8:   */ {
/*  9:   */   protected OutputStream branch;
/* 10:   */   
/* 11:   */   public TeeOutputStream(OutputStream out, OutputStream branch)
/* 12:   */   {
/* 13:40 */     super(out);
/* 14:41 */     this.branch = branch;
/* 15:   */   }
/* 16:   */   
/* 17:   */   public synchronized void write(byte[] b)
/* 18:   */     throws IOException
/* 19:   */   {
/* 20:46 */     super.write(b);
/* 21:47 */     this.branch.write(b);
/* 22:   */   }
/* 23:   */   
/* 24:   */   public synchronized void write(byte[] b, int off, int len)
/* 25:   */     throws IOException
/* 26:   */   {
/* 27:52 */     super.write(b, off, len);
/* 28:53 */     this.branch.write(b, off, len);
/* 29:   */   }
/* 30:   */   
/* 31:   */   public synchronized void write(int b)
/* 32:   */     throws IOException
/* 33:   */   {
/* 34:58 */     super.write(b);
/* 35:59 */     this.branch.write(b);
/* 36:   */   }
/* 37:   */   
/* 38:   */   public void flush()
/* 39:   */     throws IOException
/* 40:   */   {
/* 41:68 */     super.flush();
/* 42:69 */     this.branch.flush();
/* 43:   */   }
/* 44:   */   
/* 45:   */   public void close()
/* 46:   */     throws IOException
/* 47:   */   {
/* 48:78 */     super.close();
/* 49:79 */     this.branch.close();
/* 50:   */   }
/* 51:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.io.output.TeeOutputStream
 * JD-Core Version:    0.7.0.1
 */