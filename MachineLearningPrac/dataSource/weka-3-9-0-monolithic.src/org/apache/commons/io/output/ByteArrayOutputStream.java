/*   1:    */ package org.apache.commons.io.output;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.OutputStream;
/*   5:    */ import java.io.UnsupportedEncodingException;
/*   6:    */ import java.util.ArrayList;
/*   7:    */ import java.util.List;
/*   8:    */ 
/*   9:    */ public class ByteArrayOutputStream
/*  10:    */   extends OutputStream
/*  11:    */ {
/*  12: 53 */   private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
/*  13: 56 */   private List buffers = new ArrayList();
/*  14:    */   private int currentBufferIndex;
/*  15:    */   private int filledBufferSum;
/*  16:    */   private byte[] currentBuffer;
/*  17:    */   private int count;
/*  18:    */   
/*  19:    */   public ByteArrayOutputStream()
/*  20:    */   {
/*  21: 71 */     this(1024);
/*  22:    */   }
/*  23:    */   
/*  24:    */   public ByteArrayOutputStream(int size)
/*  25:    */   {
/*  26: 82 */     if (size < 0) {
/*  27: 83 */       throw new IllegalArgumentException("Negative initial size: " + size);
/*  28:    */     }
/*  29: 86 */     needNewBuffer(size);
/*  30:    */   }
/*  31:    */   
/*  32:    */   private byte[] getBuffer(int index)
/*  33:    */   {
/*  34: 97 */     return (byte[])this.buffers.get(index);
/*  35:    */   }
/*  36:    */   
/*  37:    */   private void needNewBuffer(int newcount)
/*  38:    */   {
/*  39:107 */     if (this.currentBufferIndex < this.buffers.size() - 1)
/*  40:    */     {
/*  41:109 */       this.filledBufferSum += this.currentBuffer.length;
/*  42:    */       
/*  43:111 */       this.currentBufferIndex += 1;
/*  44:112 */       this.currentBuffer = getBuffer(this.currentBufferIndex);
/*  45:    */     }
/*  46:    */     else
/*  47:    */     {
/*  48:    */       int newBufferSize;
/*  49:116 */       if (this.currentBuffer == null)
/*  50:    */       {
/*  51:117 */         int newBufferSize = newcount;
/*  52:118 */         this.filledBufferSum = 0;
/*  53:    */       }
/*  54:    */       else
/*  55:    */       {
/*  56:120 */         newBufferSize = Math.max(this.currentBuffer.length << 1, newcount - this.filledBufferSum);
/*  57:    */         
/*  58:    */ 
/*  59:123 */         this.filledBufferSum += this.currentBuffer.length;
/*  60:    */       }
/*  61:126 */       this.currentBufferIndex += 1;
/*  62:127 */       this.currentBuffer = new byte[newBufferSize];
/*  63:128 */       this.buffers.add(this.currentBuffer);
/*  64:    */     }
/*  65:    */   }
/*  66:    */   
/*  67:    */   public void write(byte[] b, int off, int len)
/*  68:    */   {
/*  69:136 */     if ((off < 0) || (off > b.length) || (len < 0) || (off + len > b.length) || (off + len < 0)) {
/*  70:141 */       throw new IndexOutOfBoundsException();
/*  71:    */     }
/*  72:142 */     if (len == 0) {
/*  73:143 */       return;
/*  74:    */     }
/*  75:145 */     synchronized (this)
/*  76:    */     {
/*  77:146 */       int newcount = this.count + len;
/*  78:147 */       int remaining = len;
/*  79:148 */       int inBufferPos = this.count - this.filledBufferSum;
/*  80:149 */       while (remaining > 0)
/*  81:    */       {
/*  82:150 */         int part = Math.min(remaining, this.currentBuffer.length - inBufferPos);
/*  83:151 */         System.arraycopy(b, off + len - remaining, this.currentBuffer, inBufferPos, part);
/*  84:152 */         remaining -= part;
/*  85:153 */         if (remaining > 0)
/*  86:    */         {
/*  87:154 */           needNewBuffer(newcount);
/*  88:155 */           inBufferPos = 0;
/*  89:    */         }
/*  90:    */       }
/*  91:158 */       this.count = newcount;
/*  92:    */     }
/*  93:    */   }
/*  94:    */   
/*  95:    */   public synchronized void write(int b)
/*  96:    */   {
/*  97:166 */     int inBufferPos = this.count - this.filledBufferSum;
/*  98:167 */     if (inBufferPos == this.currentBuffer.length)
/*  99:    */     {
/* 100:168 */       needNewBuffer(this.count + 1);
/* 101:169 */       inBufferPos = 0;
/* 102:    */     }
/* 103:171 */     this.currentBuffer[inBufferPos] = ((byte)b);
/* 104:172 */     this.count += 1;
/* 105:    */   }
/* 106:    */   
/* 107:    */   public synchronized int size()
/* 108:    */   {
/* 109:179 */     return this.count;
/* 110:    */   }
/* 111:    */   
/* 112:    */   public void close()
/* 113:    */     throws IOException
/* 114:    */   {}
/* 115:    */   
/* 116:    */   public synchronized void reset()
/* 117:    */   {
/* 118:198 */     this.count = 0;
/* 119:199 */     this.filledBufferSum = 0;
/* 120:200 */     this.currentBufferIndex = 0;
/* 121:201 */     this.currentBuffer = getBuffer(this.currentBufferIndex);
/* 122:    */   }
/* 123:    */   
/* 124:    */   public synchronized void writeTo(OutputStream out)
/* 125:    */     throws IOException
/* 126:    */   {
/* 127:213 */     int remaining = this.count;
/* 128:214 */     for (int i = 0; i < this.buffers.size(); i++)
/* 129:    */     {
/* 130:215 */       byte[] buf = getBuffer(i);
/* 131:216 */       int c = Math.min(buf.length, remaining);
/* 132:217 */       out.write(buf, 0, c);
/* 133:218 */       remaining -= c;
/* 134:219 */       if (remaining == 0) {
/* 135:    */         break;
/* 136:    */       }
/* 137:    */     }
/* 138:    */   }
/* 139:    */   
/* 140:    */   public synchronized byte[] toByteArray()
/* 141:    */   {
/* 142:233 */     int remaining = this.count;
/* 143:234 */     if (remaining == 0) {
/* 144:235 */       return EMPTY_BYTE_ARRAY;
/* 145:    */     }
/* 146:237 */     byte[] newbuf = new byte[remaining];
/* 147:238 */     int pos = 0;
/* 148:239 */     for (int i = 0; i < this.buffers.size(); i++)
/* 149:    */     {
/* 150:240 */       byte[] buf = getBuffer(i);
/* 151:241 */       int c = Math.min(buf.length, remaining);
/* 152:242 */       System.arraycopy(buf, 0, newbuf, pos, c);
/* 153:243 */       pos += c;
/* 154:244 */       remaining -= c;
/* 155:245 */       if (remaining == 0) {
/* 156:    */         break;
/* 157:    */       }
/* 158:    */     }
/* 159:249 */     return newbuf;
/* 160:    */   }
/* 161:    */   
/* 162:    */   public String toString()
/* 163:    */   {
/* 164:258 */     return new String(toByteArray());
/* 165:    */   }
/* 166:    */   
/* 167:    */   public String toString(String enc)
/* 168:    */     throws UnsupportedEncodingException
/* 169:    */   {
/* 170:271 */     return new String(toByteArray(), enc);
/* 171:    */   }
/* 172:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.io.output.ByteArrayOutputStream
 * JD-Core Version:    0.7.0.1
 */