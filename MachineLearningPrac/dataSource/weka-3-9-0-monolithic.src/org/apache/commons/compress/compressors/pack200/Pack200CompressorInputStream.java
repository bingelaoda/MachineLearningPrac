/*   1:    */ package org.apache.commons.compress.compressors.pack200;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.FilterInputStream;
/*   5:    */ import java.io.IOException;
/*   6:    */ import java.io.InputStream;
/*   7:    */ import java.util.Map;
/*   8:    */ import java.util.SortedMap;
/*   9:    */ import java.util.jar.JarOutputStream;
/*  10:    */ import java.util.jar.Pack200;
/*  11:    */ import java.util.jar.Pack200.Unpacker;
/*  12:    */ import org.apache.commons.compress.compressors.CompressorInputStream;
/*  13:    */ 
/*  14:    */ public class Pack200CompressorInputStream
/*  15:    */   extends CompressorInputStream
/*  16:    */ {
/*  17:    */   private final InputStream originalInput;
/*  18:    */   private final StreamBridge streamBridge;
/*  19:    */   
/*  20:    */   public Pack200CompressorInputStream(InputStream in)
/*  21:    */     throws IOException
/*  22:    */   {
/*  23: 58 */     this(in, Pack200Strategy.IN_MEMORY);
/*  24:    */   }
/*  25:    */   
/*  26:    */   public Pack200CompressorInputStream(InputStream in, Pack200Strategy mode)
/*  27:    */     throws IOException
/*  28:    */   {
/*  29: 74 */     this(in, null, mode, null);
/*  30:    */   }
/*  31:    */   
/*  32:    */   public Pack200CompressorInputStream(InputStream in, Map<String, String> props)
/*  33:    */     throws IOException
/*  34:    */   {
/*  35: 90 */     this(in, Pack200Strategy.IN_MEMORY, props);
/*  36:    */   }
/*  37:    */   
/*  38:    */   public Pack200CompressorInputStream(InputStream in, Pack200Strategy mode, Map<String, String> props)
/*  39:    */     throws IOException
/*  40:    */   {
/*  41:108 */     this(in, null, mode, props);
/*  42:    */   }
/*  43:    */   
/*  44:    */   public Pack200CompressorInputStream(File f)
/*  45:    */     throws IOException
/*  46:    */   {
/*  47:118 */     this(f, Pack200Strategy.IN_MEMORY);
/*  48:    */   }
/*  49:    */   
/*  50:    */   public Pack200CompressorInputStream(File f, Pack200Strategy mode)
/*  51:    */     throws IOException
/*  52:    */   {
/*  53:130 */     this(null, f, mode, null);
/*  54:    */   }
/*  55:    */   
/*  56:    */   public Pack200CompressorInputStream(File f, Map<String, String> props)
/*  57:    */     throws IOException
/*  58:    */   {
/*  59:143 */     this(f, Pack200Strategy.IN_MEMORY, props);
/*  60:    */   }
/*  61:    */   
/*  62:    */   public Pack200CompressorInputStream(File f, Pack200Strategy mode, Map<String, String> props)
/*  63:    */     throws IOException
/*  64:    */   {
/*  65:157 */     this(null, f, mode, props);
/*  66:    */   }
/*  67:    */   
/*  68:    */   private Pack200CompressorInputStream(InputStream in, File f, Pack200Strategy mode, Map<String, String> props)
/*  69:    */     throws IOException
/*  70:    */   {
/*  71:164 */     this.originalInput = in;
/*  72:165 */     this.streamBridge = mode.newStreamBridge();
/*  73:166 */     JarOutputStream jarOut = new JarOutputStream(this.streamBridge);
/*  74:167 */     Pack200.Unpacker u = Pack200.newUnpacker();
/*  75:168 */     if (props != null) {
/*  76:169 */       u.properties().putAll(props);
/*  77:    */     }
/*  78:171 */     if (f == null) {
/*  79:172 */       u.unpack(new FilterInputStream(in)
/*  80:    */       {
/*  81:    */         public void close() {}
/*  82:172 */       }, jarOut);
/*  83:    */     } else {
/*  84:181 */       u.unpack(f, jarOut);
/*  85:    */     }
/*  86:183 */     jarOut.close();
/*  87:    */   }
/*  88:    */   
/*  89:    */   public int read()
/*  90:    */     throws IOException
/*  91:    */   {
/*  92:188 */     return this.streamBridge.getInput().read();
/*  93:    */   }
/*  94:    */   
/*  95:    */   public int read(byte[] b)
/*  96:    */     throws IOException
/*  97:    */   {
/*  98:193 */     return this.streamBridge.getInput().read(b);
/*  99:    */   }
/* 100:    */   
/* 101:    */   public int read(byte[] b, int off, int count)
/* 102:    */     throws IOException
/* 103:    */   {
/* 104:198 */     return this.streamBridge.getInput().read(b, off, count);
/* 105:    */   }
/* 106:    */   
/* 107:    */   public int available()
/* 108:    */     throws IOException
/* 109:    */   {
/* 110:203 */     return this.streamBridge.getInput().available();
/* 111:    */   }
/* 112:    */   
/* 113:    */   public boolean markSupported()
/* 114:    */   {
/* 115:    */     try
/* 116:    */     {
/* 117:209 */       return this.streamBridge.getInput().markSupported();
/* 118:    */     }
/* 119:    */     catch (IOException ex) {}
/* 120:211 */     return false;
/* 121:    */   }
/* 122:    */   
/* 123:    */   public void mark(int limit)
/* 124:    */   {
/* 125:    */     try
/* 126:    */     {
/* 127:218 */       this.streamBridge.getInput().mark(limit);
/* 128:    */     }
/* 129:    */     catch (IOException ex)
/* 130:    */     {
/* 131:220 */       throw new RuntimeException(ex);
/* 132:    */     }
/* 133:    */   }
/* 134:    */   
/* 135:    */   public void reset()
/* 136:    */     throws IOException
/* 137:    */   {
/* 138:226 */     this.streamBridge.getInput().reset();
/* 139:    */   }
/* 140:    */   
/* 141:    */   public long skip(long count)
/* 142:    */     throws IOException
/* 143:    */   {
/* 144:231 */     return this.streamBridge.getInput().skip(count);
/* 145:    */   }
/* 146:    */   
/* 147:    */   public void close()
/* 148:    */     throws IOException
/* 149:    */   {
/* 150:    */     try
/* 151:    */     {
/* 152:237 */       this.streamBridge.stop();
/* 153:    */     }
/* 154:    */     finally
/* 155:    */     {
/* 156:239 */       if (this.originalInput != null) {
/* 157:240 */         this.originalInput.close();
/* 158:    */       }
/* 159:    */     }
/* 160:    */   }
/* 161:    */   
/* 162:245 */   private static final byte[] CAFE_DOOD = { -54, -2, -48, 13 };
/* 163:248 */   private static final int SIG_LENGTH = CAFE_DOOD.length;
/* 164:    */   
/* 165:    */   public static boolean matches(byte[] signature, int length)
/* 166:    */   {
/* 167:262 */     if (length < SIG_LENGTH) {
/* 168:263 */       return false;
/* 169:    */     }
/* 170:266 */     for (int i = 0; i < SIG_LENGTH; i++) {
/* 171:267 */       if (signature[i] != CAFE_DOOD[i]) {
/* 172:268 */         return false;
/* 173:    */       }
/* 174:    */     }
/* 175:272 */     return true;
/* 176:    */   }
/* 177:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.compressors.pack200.Pack200CompressorInputStream
 * JD-Core Version:    0.7.0.1
 */