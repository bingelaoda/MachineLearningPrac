/*   1:    */ package org.apache.commons.io.output;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.OutputStream;
/*   5:    */ 
/*   6:    */ public class CountingOutputStream
/*   7:    */   extends ProxyOutputStream
/*   8:    */ {
/*   9:    */   private long count;
/*  10:    */   
/*  11:    */   public CountingOutputStream(OutputStream out)
/*  12:    */   {
/*  13: 42 */     super(out);
/*  14:    */   }
/*  15:    */   
/*  16:    */   public void write(byte[] b)
/*  17:    */     throws IOException
/*  18:    */   {
/*  19: 55 */     this.count += b.length;
/*  20: 56 */     super.write(b);
/*  21:    */   }
/*  22:    */   
/*  23:    */   public void write(byte[] b, int off, int len)
/*  24:    */     throws IOException
/*  25:    */   {
/*  26: 70 */     this.count += len;
/*  27: 71 */     super.write(b, off, len);
/*  28:    */   }
/*  29:    */   
/*  30:    */   public void write(int b)
/*  31:    */     throws IOException
/*  32:    */   {
/*  33: 83 */     this.count += 1L;
/*  34: 84 */     super.write(b);
/*  35:    */   }
/*  36:    */   
/*  37:    */   public synchronized int getCount()
/*  38:    */   {
/*  39: 99 */     long result = getByteCount();
/*  40:100 */     if (result > 2147483647L) {
/*  41:101 */       throw new ArithmeticException("The byte count " + result + " is too large to be converted to an int");
/*  42:    */     }
/*  43:103 */     return (int)result;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public synchronized int resetCount()
/*  47:    */   {
/*  48:117 */     long result = resetByteCount();
/*  49:118 */     if (result > 2147483647L) {
/*  50:119 */       throw new ArithmeticException("The byte count " + result + " is too large to be converted to an int");
/*  51:    */     }
/*  52:121 */     return (int)result;
/*  53:    */   }
/*  54:    */   
/*  55:    */   public synchronized long getByteCount()
/*  56:    */   {
/*  57:135 */     return this.count;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public synchronized long resetByteCount()
/*  61:    */   {
/*  62:149 */     long tmp = this.count;
/*  63:150 */     this.count = 0L;
/*  64:151 */     return tmp;
/*  65:    */   }
/*  66:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.io.output.CountingOutputStream
 * JD-Core Version:    0.7.0.1
 */