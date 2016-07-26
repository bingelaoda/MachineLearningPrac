/*   1:    */ package org.apache.commons.compress.archivers;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.InputStream;
/*   5:    */ 
/*   6:    */ public abstract class ArchiveInputStream
/*   7:    */   extends InputStream
/*   8:    */ {
/*   9: 42 */   private final byte[] SINGLE = new byte[1];
/*  10:    */   private static final int BYTE_MASK = 255;
/*  11: 46 */   private long bytesRead = 0L;
/*  12:    */   
/*  13:    */   public abstract ArchiveEntry getNextEntry()
/*  14:    */     throws IOException;
/*  15:    */   
/*  16:    */   public int read()
/*  17:    */     throws IOException
/*  18:    */   {
/*  19: 81 */     int num = read(this.SINGLE, 0, 1);
/*  20: 82 */     return num == -1 ? -1 : this.SINGLE[0] & 0xFF;
/*  21:    */   }
/*  22:    */   
/*  23:    */   protected void count(int read)
/*  24:    */   {
/*  25: 92 */     count(read);
/*  26:    */   }
/*  27:    */   
/*  28:    */   protected void count(long read)
/*  29:    */   {
/*  30:103 */     if (read != -1L) {
/*  31:104 */       this.bytesRead += read;
/*  32:    */     }
/*  33:    */   }
/*  34:    */   
/*  35:    */   protected void pushedBackBytes(long pushedBack)
/*  36:    */   {
/*  37:115 */     this.bytesRead -= pushedBack;
/*  38:    */   }
/*  39:    */   
/*  40:    */   @Deprecated
/*  41:    */   public int getCount()
/*  42:    */   {
/*  43:126 */     return (int)this.bytesRead;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public long getBytesRead()
/*  47:    */   {
/*  48:135 */     return this.bytesRead;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public boolean canReadEntryData(ArchiveEntry archiveEntry)
/*  52:    */   {
/*  53:152 */     return true;
/*  54:    */   }
/*  55:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.ArchiveInputStream
 * JD-Core Version:    0.7.0.1
 */