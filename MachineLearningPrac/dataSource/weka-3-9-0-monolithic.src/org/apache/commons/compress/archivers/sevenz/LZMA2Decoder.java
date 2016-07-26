/*  1:   */ package org.apache.commons.compress.archivers.sevenz;
/*  2:   */ 
/*  3:   */ import java.io.IOException;
/*  4:   */ import java.io.InputStream;
/*  5:   */ import java.io.OutputStream;
/*  6:   */ import org.tukaani.xz.FinishableOutputStream;
/*  7:   */ import org.tukaani.xz.FinishableWrapperOutputStream;
/*  8:   */ import org.tukaani.xz.LZMA2InputStream;
/*  9:   */ import org.tukaani.xz.LZMA2Options;
/* 10:   */ 
/* 11:   */ class LZMA2Decoder
/* 12:   */   extends CoderBase
/* 13:   */ {
/* 14:   */   LZMA2Decoder()
/* 15:   */   {
/* 16:31 */     super(new Class[] { LZMA2Options.class, Number.class });
/* 17:   */   }
/* 18:   */   
/* 19:   */   InputStream decode(String archiveName, InputStream in, long uncompressedLength, Coder coder, byte[] password)
/* 20:   */     throws IOException
/* 21:   */   {
/* 22:   */     try
/* 23:   */     {
/* 24:38 */       int dictionarySize = getDictionarySize(coder);
/* 25:39 */       return new LZMA2InputStream(in, dictionarySize);
/* 26:   */     }
/* 27:   */     catch (IllegalArgumentException ex)
/* 28:   */     {
/* 29:41 */       throw new IOException(ex.getMessage());
/* 30:   */     }
/* 31:   */   }
/* 32:   */   
/* 33:   */   OutputStream encode(OutputStream out, Object opts)
/* 34:   */     throws IOException
/* 35:   */   {
/* 36:48 */     LZMA2Options options = getOptions(opts);
/* 37:49 */     FinishableOutputStream wrapped = new FinishableWrapperOutputStream(out);
/* 38:50 */     return options.getOutputStream(wrapped);
/* 39:   */   }
/* 40:   */   
/* 41:   */   byte[] getOptionsAsProperties(Object opts)
/* 42:   */   {
/* 43:55 */     int dictSize = getDictSize(opts);
/* 44:56 */     int lead = Integer.numberOfLeadingZeros(dictSize);
/* 45:57 */     int secondBit = (dictSize >>> 30 - lead) - 2;
/* 46:58 */     return new byte[] { (byte)((19 - lead) * 2 + secondBit) };
/* 47:   */   }
/* 48:   */   
/* 49:   */   Object getOptionsFromCoder(Coder coder, InputStream in)
/* 50:   */   {
/* 51:65 */     return Integer.valueOf(getDictionarySize(coder));
/* 52:   */   }
/* 53:   */   
/* 54:   */   private int getDictSize(Object opts)
/* 55:   */   {
/* 56:69 */     if ((opts instanceof LZMA2Options)) {
/* 57:70 */       return ((LZMA2Options)opts).getDictSize();
/* 58:   */     }
/* 59:72 */     return numberOptionOrDefault(opts);
/* 60:   */   }
/* 61:   */   
/* 62:   */   private int getDictionarySize(Coder coder)
/* 63:   */     throws IllegalArgumentException
/* 64:   */   {
/* 65:76 */     int dictionarySizeBits = 0xFF & coder.properties[0];
/* 66:77 */     if ((dictionarySizeBits & 0xFFFFFFC0) != 0) {
/* 67:78 */       throw new IllegalArgumentException("Unsupported LZMA2 property bits");
/* 68:   */     }
/* 69:80 */     if (dictionarySizeBits > 40) {
/* 70:81 */       throw new IllegalArgumentException("Dictionary larger than 4GiB maximum size");
/* 71:   */     }
/* 72:83 */     if (dictionarySizeBits == 40) {
/* 73:84 */       return -1;
/* 74:   */     }
/* 75:86 */     return (0x2 | dictionarySizeBits & 0x1) << dictionarySizeBits / 2 + 11;
/* 76:   */   }
/* 77:   */   
/* 78:   */   private LZMA2Options getOptions(Object opts)
/* 79:   */     throws IOException
/* 80:   */   {
/* 81:90 */     if ((opts instanceof LZMA2Options)) {
/* 82:91 */       return (LZMA2Options)opts;
/* 83:   */     }
/* 84:93 */     LZMA2Options options = new LZMA2Options();
/* 85:94 */     options.setDictSize(numberOptionOrDefault(opts));
/* 86:95 */     return options;
/* 87:   */   }
/* 88:   */   
/* 89:   */   private int numberOptionOrDefault(Object opts)
/* 90:   */   {
/* 91:99 */     return numberOptionOrDefault(opts, 8388608);
/* 92:   */   }
/* 93:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.sevenz.LZMA2Decoder
 * JD-Core Version:    0.7.0.1
 */