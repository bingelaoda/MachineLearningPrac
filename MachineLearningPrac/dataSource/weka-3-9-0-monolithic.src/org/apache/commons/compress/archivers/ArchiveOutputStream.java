/*   1:    */ package org.apache.commons.compress.archivers;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.IOException;
/*   5:    */ import java.io.OutputStream;
/*   6:    */ 
/*   7:    */ public abstract class ArchiveOutputStream
/*   8:    */   extends OutputStream
/*   9:    */ {
/*  10: 50 */   private final byte[] oneByte = new byte[1];
/*  11:    */   static final int BYTE_MASK = 255;
/*  12: 54 */   private long bytesWritten = 0L;
/*  13:    */   
/*  14:    */   public abstract void putArchiveEntry(ArchiveEntry paramArchiveEntry)
/*  15:    */     throws IOException;
/*  16:    */   
/*  17:    */   public abstract void closeArchiveEntry()
/*  18:    */     throws IOException;
/*  19:    */   
/*  20:    */   public abstract void finish()
/*  21:    */     throws IOException;
/*  22:    */   
/*  23:    */   public abstract ArchiveEntry createArchiveEntry(File paramFile, String paramString)
/*  24:    */     throws IOException;
/*  25:    */   
/*  26:    */   public void write(int b)
/*  27:    */     throws IOException
/*  28:    */   {
/*  29:108 */     this.oneByte[0] = ((byte)(b & 0xFF));
/*  30:109 */     write(this.oneByte, 0, 1);
/*  31:    */   }
/*  32:    */   
/*  33:    */   protected void count(int written)
/*  34:    */   {
/*  35:119 */     count(written);
/*  36:    */   }
/*  37:    */   
/*  38:    */   protected void count(long written)
/*  39:    */   {
/*  40:130 */     if (written != -1L) {
/*  41:131 */       this.bytesWritten += written;
/*  42:    */     }
/*  43:    */   }
/*  44:    */   
/*  45:    */   @Deprecated
/*  46:    */   public int getCount()
/*  47:    */   {
/*  48:143 */     return (int)this.bytesWritten;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public long getBytesWritten()
/*  52:    */   {
/*  53:152 */     return this.bytesWritten;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public boolean canWriteEntryData(ArchiveEntry archiveEntry)
/*  57:    */   {
/*  58:167 */     return true;
/*  59:    */   }
/*  60:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.ArchiveOutputStream
 * JD-Core Version:    0.7.0.1
 */