/*   1:    */ package org.apache.commons.io.output;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.OutputStream;
/*   5:    */ 
/*   6:    */ public class DemuxOutputStream
/*   7:    */   extends OutputStream
/*   8:    */ {
/*   9: 32 */   private InheritableThreadLocal m_streams = new InheritableThreadLocal();
/*  10:    */   
/*  11:    */   public OutputStream bindStream(OutputStream output)
/*  12:    */   {
/*  13: 42 */     OutputStream stream = getStream();
/*  14: 43 */     this.m_streams.set(output);
/*  15: 44 */     return stream;
/*  16:    */   }
/*  17:    */   
/*  18:    */   public void close()
/*  19:    */     throws IOException
/*  20:    */   {
/*  21: 55 */     OutputStream output = getStream();
/*  22: 56 */     if (null != output) {
/*  23: 58 */       output.close();
/*  24:    */     }
/*  25:    */   }
/*  26:    */   
/*  27:    */   public void flush()
/*  28:    */     throws IOException
/*  29:    */   {
/*  30: 70 */     OutputStream output = getStream();
/*  31: 71 */     if (null != output) {
/*  32: 73 */       output.flush();
/*  33:    */     }
/*  34:    */   }
/*  35:    */   
/*  36:    */   public void write(int ch)
/*  37:    */     throws IOException
/*  38:    */   {
/*  39: 86 */     OutputStream output = getStream();
/*  40: 87 */     if (null != output) {
/*  41: 89 */       output.write(ch);
/*  42:    */     }
/*  43:    */   }
/*  44:    */   
/*  45:    */   private OutputStream getStream()
/*  46:    */   {
/*  47:100 */     return (OutputStream)this.m_streams.get();
/*  48:    */   }
/*  49:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.io.output.DemuxOutputStream
 * JD-Core Version:    0.7.0.1
 */