/*   1:    */ package org.apache.commons.io.input;
/*   2:    */ 
/*   3:    */ import java.io.DataInput;
/*   4:    */ import java.io.EOFException;
/*   5:    */ import java.io.IOException;
/*   6:    */ import java.io.InputStream;
/*   7:    */ import org.apache.commons.io.EndianUtils;
/*   8:    */ 
/*   9:    */ public class SwappedDataInputStream
/*  10:    */   extends ProxyInputStream
/*  11:    */   implements DataInput
/*  12:    */ {
/*  13:    */   public SwappedDataInputStream(InputStream input)
/*  14:    */   {
/*  15: 47 */     super(input);
/*  16:    */   }
/*  17:    */   
/*  18:    */   public boolean readBoolean()
/*  19:    */     throws IOException, EOFException
/*  20:    */   {
/*  21: 54 */     return 0 == readByte();
/*  22:    */   }
/*  23:    */   
/*  24:    */   public byte readByte()
/*  25:    */     throws IOException, EOFException
/*  26:    */   {
/*  27: 61 */     return (byte)this.in.read();
/*  28:    */   }
/*  29:    */   
/*  30:    */   public char readChar()
/*  31:    */     throws IOException, EOFException
/*  32:    */   {
/*  33: 68 */     return (char)readShort();
/*  34:    */   }
/*  35:    */   
/*  36:    */   public double readDouble()
/*  37:    */     throws IOException, EOFException
/*  38:    */   {
/*  39: 75 */     return EndianUtils.readSwappedDouble(this.in);
/*  40:    */   }
/*  41:    */   
/*  42:    */   public float readFloat()
/*  43:    */     throws IOException, EOFException
/*  44:    */   {
/*  45: 82 */     return EndianUtils.readSwappedFloat(this.in);
/*  46:    */   }
/*  47:    */   
/*  48:    */   public void readFully(byte[] data)
/*  49:    */     throws IOException, EOFException
/*  50:    */   {
/*  51: 89 */     readFully(data, 0, data.length);
/*  52:    */   }
/*  53:    */   
/*  54:    */   public void readFully(byte[] data, int offset, int length)
/*  55:    */     throws IOException, EOFException
/*  56:    */   {
/*  57: 96 */     int remaining = length;
/*  58: 98 */     while (remaining > 0)
/*  59:    */     {
/*  60:100 */       int location = offset + (length - remaining);
/*  61:101 */       int count = read(data, location, remaining);
/*  62:103 */       if (-1 == count) {
/*  63:105 */         throw new EOFException();
/*  64:    */       }
/*  65:108 */       remaining -= count;
/*  66:    */     }
/*  67:    */   }
/*  68:    */   
/*  69:    */   public int readInt()
/*  70:    */     throws IOException, EOFException
/*  71:    */   {
/*  72:116 */     return EndianUtils.readSwappedInteger(this.in);
/*  73:    */   }
/*  74:    */   
/*  75:    */   public String readLine()
/*  76:    */     throws IOException, EOFException
/*  77:    */   {
/*  78:127 */     throw new UnsupportedOperationException("Operation not supported: readLine()");
/*  79:    */   }
/*  80:    */   
/*  81:    */   public long readLong()
/*  82:    */     throws IOException, EOFException
/*  83:    */   {
/*  84:135 */     return EndianUtils.readSwappedLong(this.in);
/*  85:    */   }
/*  86:    */   
/*  87:    */   public short readShort()
/*  88:    */     throws IOException, EOFException
/*  89:    */   {
/*  90:142 */     return EndianUtils.readSwappedShort(this.in);
/*  91:    */   }
/*  92:    */   
/*  93:    */   public int readUnsignedByte()
/*  94:    */     throws IOException, EOFException
/*  95:    */   {
/*  96:149 */     return this.in.read();
/*  97:    */   }
/*  98:    */   
/*  99:    */   public int readUnsignedShort()
/* 100:    */     throws IOException, EOFException
/* 101:    */   {
/* 102:156 */     return EndianUtils.readSwappedUnsignedShort(this.in);
/* 103:    */   }
/* 104:    */   
/* 105:    */   public String readUTF()
/* 106:    */     throws IOException, EOFException
/* 107:    */   {
/* 108:167 */     throw new UnsupportedOperationException("Operation not supported: readUTF()");
/* 109:    */   }
/* 110:    */   
/* 111:    */   public int skipBytes(int count)
/* 112:    */     throws IOException, EOFException
/* 113:    */   {
/* 114:175 */     return (int)this.in.skip(count);
/* 115:    */   }
/* 116:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.io.input.SwappedDataInputStream
 * JD-Core Version:    0.7.0.1
 */