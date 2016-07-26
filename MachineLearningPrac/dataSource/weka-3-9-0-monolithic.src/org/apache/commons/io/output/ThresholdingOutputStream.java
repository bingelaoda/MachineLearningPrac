/*   1:    */ package org.apache.commons.io.output;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.OutputStream;
/*   5:    */ 
/*   6:    */ public abstract class ThresholdingOutputStream
/*   7:    */   extends OutputStream
/*   8:    */ {
/*   9:    */   private int threshold;
/*  10:    */   private long written;
/*  11:    */   private boolean thresholdExceeded;
/*  12:    */   
/*  13:    */   public ThresholdingOutputStream(int threshold)
/*  14:    */   {
/*  15: 77 */     this.threshold = threshold;
/*  16:    */   }
/*  17:    */   
/*  18:    */   public void write(int b)
/*  19:    */     throws IOException
/*  20:    */   {
/*  21: 93 */     checkThreshold(1);
/*  22: 94 */     getStream().write(b);
/*  23: 95 */     this.written += 1L;
/*  24:    */   }
/*  25:    */   
/*  26:    */   public void write(byte[] b)
/*  27:    */     throws IOException
/*  28:    */   {
/*  29:109 */     checkThreshold(b.length);
/*  30:110 */     getStream().write(b);
/*  31:111 */     this.written += b.length;
/*  32:    */   }
/*  33:    */   
/*  34:    */   public void write(byte[] b, int off, int len)
/*  35:    */     throws IOException
/*  36:    */   {
/*  37:127 */     checkThreshold(len);
/*  38:128 */     getStream().write(b, off, len);
/*  39:129 */     this.written += len;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public void flush()
/*  43:    */     throws IOException
/*  44:    */   {
/*  45:141 */     getStream().flush();
/*  46:    */   }
/*  47:    */   
/*  48:    */   public void close()
/*  49:    */     throws IOException
/*  50:    */   {
/*  51:    */     try
/*  52:    */     {
/*  53:155 */       flush();
/*  54:    */     }
/*  55:    */     catch (IOException ignored) {}
/*  56:161 */     getStream().close();
/*  57:    */   }
/*  58:    */   
/*  59:    */   public int getThreshold()
/*  60:    */   {
/*  61:175 */     return this.threshold;
/*  62:    */   }
/*  63:    */   
/*  64:    */   public long getByteCount()
/*  65:    */   {
/*  66:186 */     return this.written;
/*  67:    */   }
/*  68:    */   
/*  69:    */   public boolean isThresholdExceeded()
/*  70:    */   {
/*  71:199 */     return this.written > this.threshold;
/*  72:    */   }
/*  73:    */   
/*  74:    */   protected void checkThreshold(int count)
/*  75:    */     throws IOException
/*  76:    */   {
/*  77:218 */     if ((!this.thresholdExceeded) && (this.written + count > this.threshold))
/*  78:    */     {
/*  79:220 */       thresholdReached();
/*  80:221 */       this.thresholdExceeded = true;
/*  81:    */     }
/*  82:    */   }
/*  83:    */   
/*  84:    */   protected abstract OutputStream getStream()
/*  85:    */     throws IOException;
/*  86:    */   
/*  87:    */   protected abstract void thresholdReached()
/*  88:    */     throws IOException;
/*  89:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.io.output.ThresholdingOutputStream
 * JD-Core Version:    0.7.0.1
 */