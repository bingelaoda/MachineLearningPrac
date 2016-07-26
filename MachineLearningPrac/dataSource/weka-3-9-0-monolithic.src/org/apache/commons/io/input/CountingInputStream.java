/*   1:    */ package org.apache.commons.io.input;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.InputStream;
/*   5:    */ 
/*   6:    */ public class CountingInputStream
/*   7:    */   extends ProxyInputStream
/*   8:    */ {
/*   9:    */   private long count;
/*  10:    */   
/*  11:    */   public CountingInputStream(InputStream in)
/*  12:    */   {
/*  13: 43 */     super(in);
/*  14:    */   }
/*  15:    */   
/*  16:    */   public int read(byte[] b)
/*  17:    */     throws IOException
/*  18:    */   {
/*  19: 57 */     int found = super.read(b);
/*  20: 58 */     this.count += (found >= 0 ? found : 0L);
/*  21: 59 */     return found;
/*  22:    */   }
/*  23:    */   
/*  24:    */   public int read(byte[] b, int off, int len)
/*  25:    */     throws IOException
/*  26:    */   {
/*  27: 74 */     int found = super.read(b, off, len);
/*  28: 75 */     this.count += (found >= 0 ? found : 0L);
/*  29: 76 */     return found;
/*  30:    */   }
/*  31:    */   
/*  32:    */   public int read()
/*  33:    */     throws IOException
/*  34:    */   {
/*  35: 88 */     int found = super.read();
/*  36: 89 */     this.count += (found >= 0 ? 1L : 0L);
/*  37: 90 */     return found;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public long skip(long length)
/*  41:    */     throws IOException
/*  42:    */   {
/*  43:103 */     long skip = super.skip(length);
/*  44:104 */     this.count += skip;
/*  45:105 */     return skip;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public synchronized int getCount()
/*  49:    */   {
/*  50:120 */     long result = getByteCount();
/*  51:121 */     if (result > 2147483647L) {
/*  52:122 */       throw new ArithmeticException("The byte count " + result + " is too large to be converted to an int");
/*  53:    */     }
/*  54:124 */     return (int)result;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public synchronized int resetCount()
/*  58:    */   {
/*  59:138 */     long result = resetByteCount();
/*  60:139 */     if (result > 2147483647L) {
/*  61:140 */       throw new ArithmeticException("The byte count " + result + " is too large to be converted to an int");
/*  62:    */     }
/*  63:142 */     return (int)result;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public synchronized long getByteCount()
/*  67:    */   {
/*  68:156 */     return this.count;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public synchronized long resetByteCount()
/*  72:    */   {
/*  73:170 */     long tmp = this.count;
/*  74:171 */     this.count = 0L;
/*  75:172 */     return tmp;
/*  76:    */   }
/*  77:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.io.input.CountingInputStream
 * JD-Core Version:    0.7.0.1
 */