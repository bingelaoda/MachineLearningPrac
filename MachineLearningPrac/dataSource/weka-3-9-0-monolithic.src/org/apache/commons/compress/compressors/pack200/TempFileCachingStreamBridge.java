/*  1:   */ package org.apache.commons.compress.compressors.pack200;
/*  2:   */ 
/*  3:   */ import java.io.File;
/*  4:   */ import java.io.FileInputStream;
/*  5:   */ import java.io.FileOutputStream;
/*  6:   */ import java.io.IOException;
/*  7:   */ import java.io.InputStream;
/*  8:   */ import java.io.OutputStream;
/*  9:   */ 
/* 10:   */ class TempFileCachingStreamBridge
/* 11:   */   extends StreamBridge
/* 12:   */ {
/* 13:   */   private final File f;
/* 14:   */   
/* 15:   */   TempFileCachingStreamBridge()
/* 16:   */     throws IOException
/* 17:   */   {
/* 18:37 */     this.f = File.createTempFile("commons-compress", "packtemp");
/* 19:38 */     this.f.deleteOnExit();
/* 20:39 */     this.out = new FileOutputStream(this.f);
/* 21:   */   }
/* 22:   */   
/* 23:   */   InputStream getInputView()
/* 24:   */     throws IOException
/* 25:   */   {
/* 26:44 */     this.out.close();
/* 27:45 */     new FileInputStream(this.f)
/* 28:   */     {
/* 29:   */       public void close()
/* 30:   */         throws IOException
/* 31:   */       {
/* 32:48 */         super.close();
/* 33:49 */         TempFileCachingStreamBridge.this.f.delete();
/* 34:   */       }
/* 35:   */     };
/* 36:   */   }
/* 37:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.compressors.pack200.TempFileCachingStreamBridge
 * JD-Core Version:    0.7.0.1
 */