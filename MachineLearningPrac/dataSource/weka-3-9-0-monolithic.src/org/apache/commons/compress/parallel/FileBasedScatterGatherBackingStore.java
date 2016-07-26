/*  1:   */ package org.apache.commons.compress.parallel;
/*  2:   */ 
/*  3:   */ import java.io.File;
/*  4:   */ import java.io.FileInputStream;
/*  5:   */ import java.io.FileNotFoundException;
/*  6:   */ import java.io.FileOutputStream;
/*  7:   */ import java.io.IOException;
/*  8:   */ import java.io.InputStream;
/*  9:   */ 
/* 10:   */ public class FileBasedScatterGatherBackingStore
/* 11:   */   implements ScatterGatherBackingStore
/* 12:   */ {
/* 13:   */   private final File target;
/* 14:   */   private final FileOutputStream os;
/* 15:   */   private boolean closed;
/* 16:   */   
/* 17:   */   public FileBasedScatterGatherBackingStore(File target)
/* 18:   */     throws FileNotFoundException
/* 19:   */   {
/* 20:38 */     this.target = target;
/* 21:39 */     this.os = new FileOutputStream(target);
/* 22:   */   }
/* 23:   */   
/* 24:   */   public InputStream getInputStream()
/* 25:   */     throws IOException
/* 26:   */   {
/* 27:43 */     return new FileInputStream(this.target);
/* 28:   */   }
/* 29:   */   
/* 30:   */   public void closeForWriting()
/* 31:   */     throws IOException
/* 32:   */   {
/* 33:48 */     if (!this.closed)
/* 34:   */     {
/* 35:49 */       this.os.close();
/* 36:50 */       this.closed = true;
/* 37:   */     }
/* 38:   */   }
/* 39:   */   
/* 40:   */   public void writeOut(byte[] data, int offset, int length)
/* 41:   */     throws IOException
/* 42:   */   {
/* 43:55 */     this.os.write(data, offset, length);
/* 44:   */   }
/* 45:   */   
/* 46:   */   public void close()
/* 47:   */     throws IOException
/* 48:   */   {
/* 49:59 */     closeForWriting();
/* 50:60 */     this.target.delete();
/* 51:   */   }
/* 52:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.parallel.FileBasedScatterGatherBackingStore
 * JD-Core Version:    0.7.0.1
 */