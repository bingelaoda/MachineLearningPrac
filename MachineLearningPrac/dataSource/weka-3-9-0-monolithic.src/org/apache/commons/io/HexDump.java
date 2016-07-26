/*   1:    */ package org.apache.commons.io;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.OutputStream;
/*   5:    */ 
/*   6:    */ public class HexDump
/*   7:    */ {
/*   8:    */   public static void dump(byte[] data, long offset, OutputStream stream, int index)
/*   9:    */     throws IOException, ArrayIndexOutOfBoundsException, IllegalArgumentException
/*  10:    */   {
/*  11: 64 */     if ((index < 0) || (index >= data.length)) {
/*  12: 65 */       throw new ArrayIndexOutOfBoundsException("illegal index: " + index + " into array of length " + data.length);
/*  13:    */     }
/*  14: 69 */     if (stream == null) {
/*  15: 70 */       throw new IllegalArgumentException("cannot write to nullstream");
/*  16:    */     }
/*  17: 72 */     long display_offset = offset + index;
/*  18: 73 */     StringBuffer buffer = new StringBuffer(74);
/*  19: 75 */     for (int j = index; j < data.length; j += 16)
/*  20:    */     {
/*  21: 76 */       int chars_read = data.length - j;
/*  22: 78 */       if (chars_read > 16) {
/*  23: 79 */         chars_read = 16;
/*  24:    */       }
/*  25: 81 */       buffer.append(dump(display_offset)).append(' ');
/*  26: 82 */       for (int k = 0; k < 16; k++)
/*  27:    */       {
/*  28: 83 */         if (k < chars_read) {
/*  29: 84 */           buffer.append(dump(data[(k + j)]));
/*  30:    */         } else {
/*  31: 86 */           buffer.append("  ");
/*  32:    */         }
/*  33: 88 */         buffer.append(' ');
/*  34:    */       }
/*  35: 90 */       for (int k = 0; k < chars_read; k++) {
/*  36: 91 */         if ((data[(k + j)] >= 32) && (data[(k + j)] < 127)) {
/*  37: 92 */           buffer.append((char)data[(k + j)]);
/*  38:    */         } else {
/*  39: 94 */           buffer.append('.');
/*  40:    */         }
/*  41:    */       }
/*  42: 97 */       buffer.append(EOL);
/*  43: 98 */       stream.write(buffer.toString().getBytes());
/*  44: 99 */       stream.flush();
/*  45:100 */       buffer.setLength(0);
/*  46:101 */       display_offset += chars_read;
/*  47:    */     }
/*  48:    */   }
/*  49:    */   
/*  50:108 */   public static final String EOL = System.getProperty("line.separator");
/*  51:110 */   private static final StringBuffer _lbuffer = new StringBuffer(8);
/*  52:111 */   private static final StringBuffer _cbuffer = new StringBuffer(2);
/*  53:112 */   private static final char[] _hexcodes = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
/*  54:117 */   private static final int[] _shifts = { 28, 24, 20, 16, 12, 8, 4, 0 };
/*  55:    */   
/*  56:    */   private static StringBuffer dump(long value)
/*  57:    */   {
/*  58:129 */     _lbuffer.setLength(0);
/*  59:130 */     for (int j = 0; j < 8; j++) {
/*  60:131 */       _lbuffer.append(_hexcodes[((int)(value >> _shifts[j]) & 0xF)]);
/*  61:    */     }
/*  62:134 */     return _lbuffer;
/*  63:    */   }
/*  64:    */   
/*  65:    */   private static StringBuffer dump(byte value)
/*  66:    */   {
/*  67:144 */     _cbuffer.setLength(0);
/*  68:145 */     for (int j = 0; j < 2; j++) {
/*  69:146 */       _cbuffer.append(_hexcodes[(value >> _shifts[(j + 6)] & 0xF)]);
/*  70:    */     }
/*  71:148 */     return _cbuffer;
/*  72:    */   }
/*  73:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.io.HexDump
 * JD-Core Version:    0.7.0.1
 */