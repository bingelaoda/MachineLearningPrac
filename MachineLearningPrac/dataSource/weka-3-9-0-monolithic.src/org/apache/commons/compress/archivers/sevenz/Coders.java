/*   1:    */ package org.apache.commons.compress.archivers.sevenz;
/*   2:    */ 
/*   3:    */ import java.io.FilterInputStream;
/*   4:    */ import java.io.FilterOutputStream;
/*   5:    */ import java.io.IOException;
/*   6:    */ import java.io.InputStream;
/*   7:    */ import java.io.OutputStream;
/*   8:    */ import java.util.Arrays;
/*   9:    */ import java.util.HashMap;
/*  10:    */ import java.util.Map;
/*  11:    */ import java.util.zip.Deflater;
/*  12:    */ import java.util.zip.DeflaterOutputStream;
/*  13:    */ import java.util.zip.Inflater;
/*  14:    */ import java.util.zip.InflaterInputStream;
/*  15:    */ import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
/*  16:    */ import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
/*  17:    */ import org.tukaani.xz.ARMOptions;
/*  18:    */ import org.tukaani.xz.ARMThumbOptions;
/*  19:    */ import org.tukaani.xz.FilterOptions;
/*  20:    */ import org.tukaani.xz.FinishableOutputStream;
/*  21:    */ import org.tukaani.xz.FinishableWrapperOutputStream;
/*  22:    */ import org.tukaani.xz.IA64Options;
/*  23:    */ import org.tukaani.xz.LZMAInputStream;
/*  24:    */ import org.tukaani.xz.PowerPCOptions;
/*  25:    */ import org.tukaani.xz.SPARCOptions;
/*  26:    */ import org.tukaani.xz.X86Options;
/*  27:    */ 
/*  28:    */ class Coders
/*  29:    */ {
/*  30: 47 */   private static final Map<SevenZMethod, CoderBase> CODER_MAP = new HashMap()
/*  31:    */   {
/*  32:    */     private static final long serialVersionUID = 1664829131806520867L;
/*  33:    */   };
/*  34:    */   
/*  35:    */   static CoderBase findByMethod(SevenZMethod method)
/*  36:    */   {
/*  37: 67 */     return (CoderBase)CODER_MAP.get(method);
/*  38:    */   }
/*  39:    */   
/*  40:    */   static InputStream addDecoder(String archiveName, InputStream is, long uncompressedLength, Coder coder, byte[] password)
/*  41:    */     throws IOException
/*  42:    */   {
/*  43: 72 */     CoderBase cb = findByMethod(SevenZMethod.byId(coder.decompressionMethodId));
/*  44: 73 */     if (cb == null) {
/*  45: 74 */       throw new IOException("Unsupported compression method " + Arrays.toString(coder.decompressionMethodId) + " used in " + archiveName);
/*  46:    */     }
/*  47: 78 */     return cb.decode(archiveName, is, uncompressedLength, coder, password);
/*  48:    */   }
/*  49:    */   
/*  50:    */   static OutputStream addEncoder(OutputStream out, SevenZMethod method, Object options)
/*  51:    */     throws IOException
/*  52:    */   {
/*  53: 83 */     CoderBase cb = findByMethod(method);
/*  54: 84 */     if (cb == null) {
/*  55: 85 */       throw new IOException("Unsupported compression method " + method);
/*  56:    */     }
/*  57: 87 */     return cb.encode(out, options);
/*  58:    */   }
/*  59:    */   
/*  60:    */   static class CopyDecoder
/*  61:    */     extends CoderBase
/*  62:    */   {
/*  63:    */     CopyDecoder()
/*  64:    */     {
/*  65: 90 */       super();
/*  66:    */     }
/*  67:    */     
/*  68:    */     InputStream decode(String archiveName, InputStream in, long uncompressedLength, Coder coder, byte[] password)
/*  69:    */       throws IOException
/*  70:    */     {
/*  71: 94 */       return in;
/*  72:    */     }
/*  73:    */     
/*  74:    */     OutputStream encode(OutputStream out, Object options)
/*  75:    */     {
/*  76: 98 */       return out;
/*  77:    */     }
/*  78:    */   }
/*  79:    */   
/*  80:    */   static class LZMADecoder
/*  81:    */     extends CoderBase
/*  82:    */   {
/*  83:    */     LZMADecoder()
/*  84:    */     {
/*  85:102 */       super();
/*  86:    */     }
/*  87:    */     
/*  88:    */     InputStream decode(String archiveName, InputStream in, long uncompressedLength, Coder coder, byte[] password)
/*  89:    */       throws IOException
/*  90:    */     {
/*  91:106 */       byte propsByte = coder.properties[0];
/*  92:107 */       long dictSize = coder.properties[1];
/*  93:108 */       for (int i = 1; i < 4; i++) {
/*  94:109 */         dictSize |= (coder.properties[(i + 1)] & 0xFF) << 8 * i;
/*  95:    */       }
/*  96:111 */       if (dictSize > 2147483632L) {
/*  97:112 */         throw new IOException("Dictionary larger than 4GiB maximum size used in " + archiveName);
/*  98:    */       }
/*  99:114 */       return new LZMAInputStream(in, uncompressedLength, propsByte, (int)dictSize);
/* 100:    */     }
/* 101:    */   }
/* 102:    */   
/* 103:    */   static class BCJDecoder
/* 104:    */     extends CoderBase
/* 105:    */   {
/* 106:    */     private final FilterOptions opts;
/* 107:    */     
/* 108:    */     BCJDecoder(FilterOptions opts)
/* 109:    */     {
/* 110:120 */       super();
/* 111:121 */       this.opts = opts;
/* 112:    */     }
/* 113:    */     
/* 114:    */     InputStream decode(String archiveName, InputStream in, long uncompressedLength, Coder coder, byte[] password)
/* 115:    */       throws IOException
/* 116:    */     {
/* 117:    */       try
/* 118:    */       {
/* 119:128 */         return this.opts.getInputStream(in);
/* 120:    */       }
/* 121:    */       catch (AssertionError e)
/* 122:    */       {
/* 123:130 */         IOException ex = new IOException("BCJ filter used in " + archiveName + " needs XZ for Java > 1.4 - see " + "http://commons.apache.org/proper/commons-compress/limitations.html#7Z");
/* 124:    */         
/* 125:    */ 
/* 126:133 */         ex.initCause(e);
/* 127:134 */         throw ex;
/* 128:    */       }
/* 129:    */     }
/* 130:    */     
/* 131:    */     OutputStream encode(OutputStream out, Object options)
/* 132:    */     {
/* 133:139 */       FinishableOutputStream fo = this.opts.getOutputStream(new FinishableWrapperOutputStream(out));
/* 134:140 */       new FilterOutputStream(fo)
/* 135:    */       {
/* 136:    */         public void flush() {}
/* 137:    */       };
/* 138:    */     }
/* 139:    */   }
/* 140:    */   
/* 141:    */   static class DeflateDecoder
/* 142:    */     extends CoderBase
/* 143:    */   {
/* 144:    */     DeflateDecoder()
/* 145:    */     {
/* 146:150 */       super();
/* 147:    */     }
/* 148:    */     
/* 149:    */     InputStream decode(String archiveName, InputStream in, long uncompressedLength, Coder coder, byte[] password)
/* 150:    */       throws IOException
/* 151:    */     {
/* 152:157 */       return new InflaterInputStream(new Coders.DummyByteAddingInputStream(in, null), new Inflater(true));
/* 153:    */     }
/* 154:    */     
/* 155:    */     OutputStream encode(OutputStream out, Object options)
/* 156:    */     {
/* 157:162 */       int level = numberOptionOrDefault(options, 9);
/* 158:163 */       return new DeflaterOutputStream(out, new Deflater(level, true));
/* 159:    */     }
/* 160:    */   }
/* 161:    */   
/* 162:    */   static class BZIP2Decoder
/* 163:    */     extends CoderBase
/* 164:    */   {
/* 165:    */     BZIP2Decoder()
/* 166:    */     {
/* 167:169 */       super();
/* 168:    */     }
/* 169:    */     
/* 170:    */     InputStream decode(String archiveName, InputStream in, long uncompressedLength, Coder coder, byte[] password)
/* 171:    */       throws IOException
/* 172:    */     {
/* 173:176 */       return new BZip2CompressorInputStream(in);
/* 174:    */     }
/* 175:    */     
/* 176:    */     OutputStream encode(OutputStream out, Object options)
/* 177:    */       throws IOException
/* 178:    */     {
/* 179:181 */       int blockSize = numberOptionOrDefault(options, 9);
/* 180:182 */       return new BZip2CompressorOutputStream(out, blockSize);
/* 181:    */     }
/* 182:    */   }
/* 183:    */   
/* 184:    */   private static class DummyByteAddingInputStream
/* 185:    */     extends FilterInputStream
/* 186:    */   {
/* 187:193 */     private boolean addDummyByte = true;
/* 188:    */     
/* 189:    */     private DummyByteAddingInputStream(InputStream in)
/* 190:    */     {
/* 191:196 */       super();
/* 192:    */     }
/* 193:    */     
/* 194:    */     public int read()
/* 195:    */       throws IOException
/* 196:    */     {
/* 197:201 */       int result = super.read();
/* 198:202 */       if ((result == -1) && (this.addDummyByte))
/* 199:    */       {
/* 200:203 */         this.addDummyByte = false;
/* 201:204 */         result = 0;
/* 202:    */       }
/* 203:206 */       return result;
/* 204:    */     }
/* 205:    */     
/* 206:    */     public int read(byte[] b, int off, int len)
/* 207:    */       throws IOException
/* 208:    */     {
/* 209:211 */       int result = super.read(b, off, len);
/* 210:212 */       if ((result == -1) && (this.addDummyByte))
/* 211:    */       {
/* 212:213 */         this.addDummyByte = false;
/* 213:214 */         b[off] = 0;
/* 214:215 */         return 1;
/* 215:    */       }
/* 216:217 */       return result;
/* 217:    */     }
/* 218:    */   }
/* 219:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.sevenz.Coders
 * JD-Core Version:    0.7.0.1
 */