/*  1:   */ package org.apache.commons.compress.archivers.sevenz;
/*  2:   */ 
/*  3:   */ import java.io.IOException;
/*  4:   */ import java.io.InputStream;
/*  5:   */ import java.io.OutputStream;
/*  6:   */ import org.tukaani.xz.DeltaOptions;
/*  7:   */ import org.tukaani.xz.FinishableWrapperOutputStream;
/*  8:   */ import org.tukaani.xz.UnsupportedOptionsException;
/*  9:   */ 
/* 10:   */ class DeltaDecoder
/* 11:   */   extends CoderBase
/* 12:   */ {
/* 13:   */   DeltaDecoder()
/* 14:   */   {
/* 15:29 */     super(new Class[] { Number.class });
/* 16:   */   }
/* 17:   */   
/* 18:   */   InputStream decode(String archiveName, InputStream in, long uncompressedLength, Coder coder, byte[] password)
/* 19:   */     throws IOException
/* 20:   */   {
/* 21:35 */     return new DeltaOptions(getOptionsFromCoder(coder)).getInputStream(in);
/* 22:   */   }
/* 23:   */   
/* 24:   */   OutputStream encode(OutputStream out, Object options)
/* 25:   */     throws IOException
/* 26:   */   {
/* 27:40 */     int distance = numberOptionOrDefault(options, 1);
/* 28:   */     try
/* 29:   */     {
/* 30:42 */       return new DeltaOptions(distance).getOutputStream(new FinishableWrapperOutputStream(out));
/* 31:   */     }
/* 32:   */     catch (UnsupportedOptionsException ex)
/* 33:   */     {
/* 34:44 */       throw new IOException(ex.getMessage());
/* 35:   */     }
/* 36:   */   }
/* 37:   */   
/* 38:   */   byte[] getOptionsAsProperties(Object options)
/* 39:   */   {
/* 40:50 */     return new byte[] { (byte)(numberOptionOrDefault(options, 1) - 1) };
/* 41:   */   }
/* 42:   */   
/* 43:   */   Object getOptionsFromCoder(Coder coder, InputStream in)
/* 44:   */   {
/* 45:57 */     return Integer.valueOf(getOptionsFromCoder(coder));
/* 46:   */   }
/* 47:   */   
/* 48:   */   private int getOptionsFromCoder(Coder coder)
/* 49:   */   {
/* 50:61 */     if ((coder.properties == null) || (coder.properties.length == 0)) {
/* 51:62 */       return 1;
/* 52:   */     }
/* 53:64 */     return (0xFF & coder.properties[0]) + 1;
/* 54:   */   }
/* 55:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.sevenz.DeltaDecoder
 * JD-Core Version:    0.7.0.1
 */