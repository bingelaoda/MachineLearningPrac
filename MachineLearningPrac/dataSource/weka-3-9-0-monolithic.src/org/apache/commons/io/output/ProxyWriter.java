/*  1:   */ package org.apache.commons.io.output;
/*  2:   */ 
/*  3:   */ import java.io.FilterWriter;
/*  4:   */ import java.io.IOException;
/*  5:   */ import java.io.Writer;
/*  6:   */ 
/*  7:   */ public class ProxyWriter
/*  8:   */   extends FilterWriter
/*  9:   */ {
/* 10:   */   public ProxyWriter(Writer proxy)
/* 11:   */   {
/* 12:42 */     super(proxy);
/* 13:   */   }
/* 14:   */   
/* 15:   */   public void write(int idx)
/* 16:   */     throws IOException
/* 17:   */   {
/* 18:48 */     this.out.write(idx);
/* 19:   */   }
/* 20:   */   
/* 21:   */   public void write(char[] chr)
/* 22:   */     throws IOException
/* 23:   */   {
/* 24:53 */     this.out.write(chr);
/* 25:   */   }
/* 26:   */   
/* 27:   */   public void write(char[] chr, int st, int end)
/* 28:   */     throws IOException
/* 29:   */   {
/* 30:58 */     this.out.write(chr, st, end);
/* 31:   */   }
/* 32:   */   
/* 33:   */   public void write(String str)
/* 34:   */     throws IOException
/* 35:   */   {
/* 36:63 */     this.out.write(str);
/* 37:   */   }
/* 38:   */   
/* 39:   */   public void write(String str, int st, int end)
/* 40:   */     throws IOException
/* 41:   */   {
/* 42:68 */     this.out.write(str, st, end);
/* 43:   */   }
/* 44:   */   
/* 45:   */   public void flush()
/* 46:   */     throws IOException
/* 47:   */   {
/* 48:73 */     this.out.flush();
/* 49:   */   }
/* 50:   */   
/* 51:   */   public void close()
/* 52:   */     throws IOException
/* 53:   */   {
/* 54:78 */     this.out.close();
/* 55:   */   }
/* 56:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.io.output.ProxyWriter
 * JD-Core Version:    0.7.0.1
 */