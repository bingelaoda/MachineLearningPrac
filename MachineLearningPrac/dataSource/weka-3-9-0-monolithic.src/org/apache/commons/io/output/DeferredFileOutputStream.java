/*   1:    */ package org.apache.commons.io.output;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.FileInputStream;
/*   5:    */ import java.io.FileOutputStream;
/*   6:    */ import java.io.IOException;
/*   7:    */ import java.io.OutputStream;
/*   8:    */ import org.apache.commons.io.IOUtils;
/*   9:    */ 
/*  10:    */ public class DeferredFileOutputStream
/*  11:    */   extends ThresholdingOutputStream
/*  12:    */ {
/*  13:    */   private ByteArrayOutputStream memoryOutputStream;
/*  14:    */   private OutputStream currentOutputStream;
/*  15:    */   private File outputFile;
/*  16: 74 */   private boolean closed = false;
/*  17:    */   
/*  18:    */   public DeferredFileOutputStream(int threshold, File outputFile)
/*  19:    */   {
/*  20: 88 */     super(threshold);
/*  21: 89 */     this.outputFile = outputFile;
/*  22:    */     
/*  23: 91 */     this.memoryOutputStream = new ByteArrayOutputStream();
/*  24: 92 */     this.currentOutputStream = this.memoryOutputStream;
/*  25:    */   }
/*  26:    */   
/*  27:    */   protected OutputStream getStream()
/*  28:    */     throws IOException
/*  29:    */   {
/*  30:109 */     return this.currentOutputStream;
/*  31:    */   }
/*  32:    */   
/*  33:    */   protected void thresholdReached()
/*  34:    */     throws IOException
/*  35:    */   {
/*  36:123 */     FileOutputStream fos = new FileOutputStream(this.outputFile);
/*  37:124 */     this.memoryOutputStream.writeTo(fos);
/*  38:125 */     this.currentOutputStream = fos;
/*  39:126 */     this.memoryOutputStream = null;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public boolean isInMemory()
/*  43:    */   {
/*  44:142 */     return !isThresholdExceeded();
/*  45:    */   }
/*  46:    */   
/*  47:    */   public byte[] getData()
/*  48:    */   {
/*  49:156 */     if (this.memoryOutputStream != null) {
/*  50:158 */       return this.memoryOutputStream.toByteArray();
/*  51:    */     }
/*  52:160 */     return null;
/*  53:    */   }
/*  54:    */   
/*  55:    */   public File getFile()
/*  56:    */   {
/*  57:173 */     return this.outputFile;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public void close()
/*  61:    */     throws IOException
/*  62:    */   {
/*  63:184 */     super.close();
/*  64:185 */     this.closed = true;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public void writeTo(OutputStream out)
/*  68:    */     throws IOException
/*  69:    */   {
/*  70:201 */     if (!this.closed) {
/*  71:203 */       throw new IOException("Stream not closed");
/*  72:    */     }
/*  73:206 */     if (isInMemory())
/*  74:    */     {
/*  75:208 */       this.memoryOutputStream.writeTo(out);
/*  76:    */     }
/*  77:    */     else
/*  78:    */     {
/*  79:212 */       FileInputStream fis = new FileInputStream(this.outputFile);
/*  80:    */       try
/*  81:    */       {
/*  82:214 */         IOUtils.copy(fis, out);
/*  83:    */       }
/*  84:    */       finally
/*  85:    */       {
/*  86:216 */         IOUtils.closeQuietly(fis);
/*  87:    */       }
/*  88:    */     }
/*  89:    */   }
/*  90:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.io.output.DeferredFileOutputStream
 * JD-Core Version:    0.7.0.1
 */