/*   1:    */ package org.apache.commons.compress.archivers.zip;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.nio.ByteBuffer;
/*   5:    */ import java.nio.CharBuffer;
/*   6:    */ import java.nio.charset.Charset;
/*   7:    */ import java.nio.charset.CharsetDecoder;
/*   8:    */ import java.nio.charset.CharsetEncoder;
/*   9:    */ import java.nio.charset.CoderResult;
/*  10:    */ import java.nio.charset.CodingErrorAction;
/*  11:    */ 
/*  12:    */ class NioZipEncoding
/*  13:    */   implements ZipEncoding
/*  14:    */ {
/*  15:    */   private final Charset charset;
/*  16:    */   
/*  17:    */   public NioZipEncoding(Charset charset)
/*  18:    */   {
/*  19: 51 */     this.charset = charset;
/*  20:    */   }
/*  21:    */   
/*  22:    */   public boolean canEncode(String name)
/*  23:    */   {
/*  24: 59 */     CharsetEncoder enc = this.charset.newEncoder();
/*  25: 60 */     enc.onMalformedInput(CodingErrorAction.REPORT);
/*  26: 61 */     enc.onUnmappableCharacter(CodingErrorAction.REPORT);
/*  27:    */     
/*  28: 63 */     return enc.canEncode(name);
/*  29:    */   }
/*  30:    */   
/*  31:    */   public ByteBuffer encode(String name)
/*  32:    */   {
/*  33: 71 */     CharsetEncoder enc = this.charset.newEncoder();
/*  34:    */     
/*  35: 73 */     enc.onMalformedInput(CodingErrorAction.REPORT);
/*  36: 74 */     enc.onUnmappableCharacter(CodingErrorAction.REPORT);
/*  37:    */     
/*  38: 76 */     CharBuffer cb = CharBuffer.wrap(name);
/*  39: 77 */     ByteBuffer out = ByteBuffer.allocate(name.length() + (name.length() + 1) / 2);
/*  40: 80 */     while (cb.remaining() > 0)
/*  41:    */     {
/*  42: 81 */       CoderResult res = enc.encode(cb, out, true);
/*  43: 83 */       if ((res.isUnmappable()) || (res.isMalformed()))
/*  44:    */       {
/*  45: 87 */         if (res.length() * 6 > out.remaining()) {
/*  46: 88 */           out = ZipEncodingHelper.growBuffer(out, out.position() + res.length() * 6);
/*  47:    */         }
/*  48: 92 */         for (int i = 0; i < res.length(); i++) {
/*  49: 93 */           ZipEncodingHelper.appendSurrogate(out, cb.get());
/*  50:    */         }
/*  51:    */       }
/*  52: 96 */       else if (res.isOverflow())
/*  53:    */       {
/*  54: 98 */         out = ZipEncodingHelper.growBuffer(out, 0);
/*  55:    */       }
/*  56:100 */       else if (res.isUnderflow())
/*  57:    */       {
/*  58:102 */         enc.flush(out);
/*  59:103 */         break;
/*  60:    */       }
/*  61:    */     }
/*  62:108 */     out.limit(out.position());
/*  63:109 */     out.rewind();
/*  64:110 */     return out;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public String decode(byte[] data)
/*  68:    */     throws IOException
/*  69:    */   {
/*  70:118 */     return this.charset.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT).decode(ByteBuffer.wrap(data)).toString();
/*  71:    */   }
/*  72:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.NioZipEncoding
 * JD-Core Version:    0.7.0.1
 */