/*  1:   */ package org.apache.commons.compress.compressors.pack200;
/*  2:   */ 
/*  3:   */ import java.io.FilterOutputStream;
/*  4:   */ import java.io.IOException;
/*  5:   */ import java.io.InputStream;
/*  6:   */ import java.io.OutputStream;
/*  7:   */ 
/*  8:   */ abstract class StreamBridge
/*  9:   */   extends FilterOutputStream
/* 10:   */ {
/* 11:   */   private InputStream input;
/* 12:36 */   private final Object INPUT_LOCK = new Object();
/* 13:   */   
/* 14:   */   protected StreamBridge(OutputStream out)
/* 15:   */   {
/* 16:39 */     super(out);
/* 17:   */   }
/* 18:   */   
/* 19:   */   protected StreamBridge()
/* 20:   */   {
/* 21:43 */     this(null);
/* 22:   */   }
/* 23:   */   
/* 24:   */   InputStream getInput()
/* 25:   */     throws IOException
/* 26:   */   {
/* 27:50 */     synchronized (this.INPUT_LOCK)
/* 28:   */     {
/* 29:51 */       if (this.input == null) {
/* 30:52 */         this.input = getInputView();
/* 31:   */       }
/* 32:   */     }
/* 33:55 */     return this.input;
/* 34:   */   }
/* 35:   */   
/* 36:   */   abstract InputStream getInputView()
/* 37:   */     throws IOException;
/* 38:   */   
/* 39:   */   void stop()
/* 40:   */     throws IOException
/* 41:   */   {
/* 42:67 */     close();
/* 43:68 */     synchronized (this.INPUT_LOCK)
/* 44:   */     {
/* 45:69 */       if (this.input != null)
/* 46:   */       {
/* 47:70 */         this.input.close();
/* 48:71 */         this.input = null;
/* 49:   */       }
/* 50:   */     }
/* 51:   */   }
/* 52:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.compressors.pack200.StreamBridge
 * JD-Core Version:    0.7.0.1
 */