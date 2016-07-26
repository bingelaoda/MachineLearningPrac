/*  1:   */ package org.apache.commons.io.input;
/*  2:   */ 
/*  3:   */ import java.io.IOException;
/*  4:   */ import java.io.InputStream;
/*  5:   */ 
/*  6:   */ public class DemuxInputStream
/*  7:   */   extends InputStream
/*  8:   */ {
/*  9:32 */   private InheritableThreadLocal m_streams = new InheritableThreadLocal();
/* 10:   */   
/* 11:   */   public InputStream bindStream(InputStream input)
/* 12:   */   {
/* 13:42 */     InputStream oldValue = getStream();
/* 14:43 */     this.m_streams.set(input);
/* 15:44 */     return oldValue;
/* 16:   */   }
/* 17:   */   
/* 18:   */   public void close()
/* 19:   */     throws IOException
/* 20:   */   {
/* 21:55 */     InputStream input = getStream();
/* 22:56 */     if (null != input) {
/* 23:58 */       input.close();
/* 24:   */     }
/* 25:   */   }
/* 26:   */   
/* 27:   */   public int read()
/* 28:   */     throws IOException
/* 29:   */   {
/* 30:71 */     InputStream input = getStream();
/* 31:72 */     if (null != input) {
/* 32:74 */       return input.read();
/* 33:   */     }
/* 34:78 */     return -1;
/* 35:   */   }
/* 36:   */   
/* 37:   */   private InputStream getStream()
/* 38:   */   {
/* 39:89 */     return (InputStream)this.m_streams.get();
/* 40:   */   }
/* 41:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.io.input.DemuxInputStream
 * JD-Core Version:    0.7.0.1
 */