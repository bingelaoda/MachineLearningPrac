/*   1:    */ package org.boon.primitive;
/*   2:    */ 
/*   3:    */ import java.io.ByteArrayInputStream;
/*   4:    */ import java.io.IOException;
/*   5:    */ import org.boon.Exceptions;
/*   6:    */ 
/*   7:    */ public final class InMemoryInputStream
/*   8:    */   extends ByteArrayInputStream
/*   9:    */ {
/*  10: 41 */   private static final byte[] BUFFER_FOR_YOU = new byte[1];
/*  11:    */   private byte[] buffer;
/*  12:    */   private int position;
/*  13:    */   private int value;
/*  14:    */   
/*  15:    */   public InMemoryInputStream(byte[] buffer)
/*  16:    */   {
/*  17: 50 */     super(BUFFER_FOR_YOU);
/*  18:    */     
/*  19: 52 */     this.buffer = buffer;
/*  20:    */   }
/*  21:    */   
/*  22:    */   public final int read()
/*  23:    */   {
/*  24: 60 */     if (this.position >= this.buffer.length) {
/*  25: 61 */       return -1;
/*  26:    */     }
/*  27: 63 */     this.value = this.buffer[this.position];
/*  28: 64 */     this.position += 1;
/*  29: 65 */     return this.value & 0xFF;
/*  30:    */   }
/*  31:    */   
/*  32:    */   public int read(byte[] b)
/*  33:    */     throws IOException
/*  34:    */   {
/*  35: 69 */     return read(b, 0, b.length);
/*  36:    */   }
/*  37:    */   
/*  38:    */   public final int read(byte[] destination, int destinationOffset, int destinationLength)
/*  39:    */   {
/*  40: 76 */     int available = this.buffer.length - this.position;
/*  41:    */     
/*  42: 78 */     int readAmount = 0;
/*  43: 83 */     if (available >= destinationLength)
/*  44:    */     {
/*  45: 84 */       System.arraycopy(this.buffer, this.position, destination, destinationOffset, destinationLength);
/*  46:    */       
/*  47: 86 */       this.position += destinationLength;
/*  48: 87 */       readAmount = destinationLength;
/*  49:    */     }
/*  50: 89 */     else if (available > 0)
/*  51:    */     {
/*  52: 92 */       System.arraycopy(this.buffer, this.position, destination, destinationOffset, available);
/*  53:    */       
/*  54: 94 */       this.position += available;
/*  55: 95 */       readAmount = available;
/*  56:    */     }
/*  57:100 */     return readAmount <= 0 ? -1 : readAmount;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public final long skip(long n)
/*  61:    */   {
/*  62:107 */     return ((Long)Exceptions.die(Long.class, "Skip not supported")).longValue();
/*  63:    */   }
/*  64:    */   
/*  65:    */   public final int available()
/*  66:    */   {
/*  67:111 */     return this.buffer.length - this.position;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public final void mark(int limit) {}
/*  71:    */   
/*  72:    */   public final void reset()
/*  73:    */   {
/*  74:119 */     Exceptions.die("Resetting not supported");
/*  75:    */   }
/*  76:    */   
/*  77:    */   public final boolean markSupported()
/*  78:    */   {
/*  79:123 */     return false;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public final void close()
/*  83:    */     throws IOException
/*  84:    */   {
/*  85:128 */     this.position = 0;
/*  86:129 */     this.buffer = null;
/*  87:    */   }
/*  88:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.primitive.InMemoryInputStream
 * JD-Core Version:    0.7.0.1
 */