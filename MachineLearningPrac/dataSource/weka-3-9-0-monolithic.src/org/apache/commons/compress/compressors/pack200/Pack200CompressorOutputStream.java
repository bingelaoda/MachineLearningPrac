/*   1:    */ package org.apache.commons.compress.compressors.pack200;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.OutputStream;
/*   5:    */ import java.util.Map;
/*   6:    */ import java.util.SortedMap;
/*   7:    */ import java.util.jar.JarInputStream;
/*   8:    */ import java.util.jar.Pack200;
/*   9:    */ import java.util.jar.Pack200.Packer;
/*  10:    */ import org.apache.commons.compress.compressors.CompressorOutputStream;
/*  11:    */ import org.apache.commons.compress.utils.IOUtils;
/*  12:    */ 
/*  13:    */ public class Pack200CompressorOutputStream
/*  14:    */   extends CompressorOutputStream
/*  15:    */ {
/*  16: 38 */   private boolean finished = false;
/*  17:    */   private final OutputStream originalOutput;
/*  18:    */   private final StreamBridge streamBridge;
/*  19:    */   private final Map<String, String> properties;
/*  20:    */   
/*  21:    */   public Pack200CompressorOutputStream(OutputStream out)
/*  22:    */     throws IOException
/*  23:    */   {
/*  24: 51 */     this(out, Pack200Strategy.IN_MEMORY);
/*  25:    */   }
/*  26:    */   
/*  27:    */   public Pack200CompressorOutputStream(OutputStream out, Pack200Strategy mode)
/*  28:    */     throws IOException
/*  29:    */   {
/*  30: 64 */     this(out, mode, null);
/*  31:    */   }
/*  32:    */   
/*  33:    */   public Pack200CompressorOutputStream(OutputStream out, Map<String, String> props)
/*  34:    */     throws IOException
/*  35:    */   {
/*  36: 77 */     this(out, Pack200Strategy.IN_MEMORY, props);
/*  37:    */   }
/*  38:    */   
/*  39:    */   public Pack200CompressorOutputStream(OutputStream out, Pack200Strategy mode, Map<String, String> props)
/*  40:    */     throws IOException
/*  41:    */   {
/*  42: 92 */     this.originalOutput = out;
/*  43: 93 */     this.streamBridge = mode.newStreamBridge();
/*  44: 94 */     this.properties = props;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public void write(int b)
/*  48:    */     throws IOException
/*  49:    */   {
/*  50: 99 */     this.streamBridge.write(b);
/*  51:    */   }
/*  52:    */   
/*  53:    */   public void write(byte[] b)
/*  54:    */     throws IOException
/*  55:    */   {
/*  56:104 */     this.streamBridge.write(b);
/*  57:    */   }
/*  58:    */   
/*  59:    */   public void write(byte[] b, int from, int length)
/*  60:    */     throws IOException
/*  61:    */   {
/*  62:109 */     this.streamBridge.write(b, from, length);
/*  63:    */   }
/*  64:    */   
/*  65:    */   public void close()
/*  66:    */     throws IOException
/*  67:    */   {
/*  68:114 */     finish();
/*  69:    */     try
/*  70:    */     {
/*  71:116 */       this.streamBridge.stop();
/*  72:    */     }
/*  73:    */     finally
/*  74:    */     {
/*  75:118 */       this.originalOutput.close();
/*  76:    */     }
/*  77:    */   }
/*  78:    */   
/*  79:    */   public void finish()
/*  80:    */     throws IOException
/*  81:    */   {
/*  82:123 */     if (!this.finished)
/*  83:    */     {
/*  84:124 */       this.finished = true;
/*  85:125 */       Pack200.Packer p = Pack200.newPacker();
/*  86:126 */       if (this.properties != null) {
/*  87:127 */         p.properties().putAll(this.properties);
/*  88:    */       }
/*  89:129 */       JarInputStream ji = null;
/*  90:130 */       boolean success = false;
/*  91:    */       try
/*  92:    */       {
/*  93:132 */         p.pack(ji = new JarInputStream(this.streamBridge.getInput()), this.originalOutput);
/*  94:    */         
/*  95:134 */         success = true;
/*  96:    */       }
/*  97:    */       finally
/*  98:    */       {
/*  99:136 */         if (!success) {
/* 100:137 */           IOUtils.closeQuietly(ji);
/* 101:    */         }
/* 102:    */       }
/* 103:    */     }
/* 104:    */   }
/* 105:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.compressors.pack200.Pack200CompressorOutputStream
 * JD-Core Version:    0.7.0.1
 */