/*   1:    */ package org.boon.primitive;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.StringReader;
/*   5:    */ import org.boon.Exceptions;
/*   6:    */ import org.boon.core.reflection.FastStringUtils;
/*   7:    */ 
/*   8:    */ public class InMemoryReader
/*   9:    */   extends StringReader
/*  10:    */ {
/*  11:    */   private static final String EMPTY_STRING = "";
/*  12:    */   private char[] buffer;
/*  13:    */   private int position;
/*  14:    */   private int value;
/*  15:    */   
/*  16:    */   public final void close()
/*  17:    */   {
/*  18: 53 */     this.position = 0;
/*  19: 54 */     this.buffer = null;
/*  20:    */   }
/*  21:    */   
/*  22:    */   public InMemoryReader(char[] buffer)
/*  23:    */   {
/*  24: 60 */     super("");
/*  25:    */     
/*  26: 62 */     this.buffer = buffer;
/*  27:    */   }
/*  28:    */   
/*  29:    */   public InMemoryReader(String bufferSource)
/*  30:    */   {
/*  31: 68 */     super("");
/*  32:    */     
/*  33: 70 */     this.buffer = FastStringUtils.toCharArray(bufferSource);
/*  34:    */   }
/*  35:    */   
/*  36:    */   public final int read()
/*  37:    */   {
/*  38: 79 */     if (this.position >= this.buffer.length) {
/*  39: 80 */       return -1;
/*  40:    */     }
/*  41: 82 */     this.value = this.buffer[this.position];
/*  42: 83 */     this.position += 1;
/*  43: 84 */     return this.value & 0xFF;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public int read(char[] b)
/*  47:    */     throws IOException
/*  48:    */   {
/*  49: 88 */     return read(b, 0, b.length);
/*  50:    */   }
/*  51:    */   
/*  52:    */   public int read(char[] destination, int destinationOffset, int destinationLength)
/*  53:    */   {
/*  54: 95 */     int available = this.buffer.length - this.position;
/*  55:    */     
/*  56: 97 */     int readAmount = 0;
/*  57:102 */     if (available >= destinationLength)
/*  58:    */     {
/*  59:103 */       System.arraycopy(this.buffer, this.position, destination, destinationOffset, destinationLength);
/*  60:    */       
/*  61:105 */       this.position += destinationLength;
/*  62:106 */       readAmount = destinationLength;
/*  63:    */     }
/*  64:108 */     else if (available > 0)
/*  65:    */     {
/*  66:111 */       System.arraycopy(this.buffer, this.position, destination, destinationOffset, available);
/*  67:    */       
/*  68:113 */       this.position += available;
/*  69:114 */       readAmount = available;
/*  70:    */     }
/*  71:119 */     return readAmount <= 0 ? -1 : readAmount;
/*  72:    */   }
/*  73:    */   
/*  74:    */   public final long skip(long n)
/*  75:    */   {
/*  76:126 */     return ((Long)Exceptions.die(Long.class, "Skip not supported")).longValue();
/*  77:    */   }
/*  78:    */   
/*  79:    */   public final void mark(int limit) {}
/*  80:    */   
/*  81:    */   public final void reset()
/*  82:    */   {
/*  83:134 */     Exceptions.die("Resetting not supported");
/*  84:    */   }
/*  85:    */   
/*  86:    */   public final boolean markSupported()
/*  87:    */   {
/*  88:138 */     return false;
/*  89:    */   }
/*  90:    */   
/*  91:    */   public boolean ready()
/*  92:    */   {
/*  93:142 */     return this.buffer != null;
/*  94:    */   }
/*  95:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.primitive.InMemoryReader
 * JD-Core Version:    0.7.0.1
 */