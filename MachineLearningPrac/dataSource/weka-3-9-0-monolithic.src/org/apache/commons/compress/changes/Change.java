/*  1:   */ package org.apache.commons.compress.changes;
/*  2:   */ 
/*  3:   */ import java.io.InputStream;
/*  4:   */ import org.apache.commons.compress.archivers.ArchiveEntry;
/*  5:   */ 
/*  6:   */ class Change
/*  7:   */ {
/*  8:   */   private final String targetFile;
/*  9:   */   private final ArchiveEntry entry;
/* 10:   */   private final InputStream input;
/* 11:   */   private final boolean replaceMode;
/* 12:   */   private final int type;
/* 13:   */   static final int TYPE_DELETE = 1;
/* 14:   */   static final int TYPE_ADD = 2;
/* 15:   */   static final int TYPE_MOVE = 3;
/* 16:   */   static final int TYPE_DELETE_DIR = 4;
/* 17:   */   
/* 18:   */   Change(String pFilename, int type)
/* 19:   */   {
/* 20:50 */     if (pFilename == null) {
/* 21:51 */       throw new NullPointerException();
/* 22:   */     }
/* 23:53 */     this.targetFile = pFilename;
/* 24:54 */     this.type = type;
/* 25:55 */     this.input = null;
/* 26:56 */     this.entry = null;
/* 27:57 */     this.replaceMode = true;
/* 28:   */   }
/* 29:   */   
/* 30:   */   Change(ArchiveEntry pEntry, InputStream pInput, boolean replace)
/* 31:   */   {
/* 32:67 */     if ((pEntry == null) || (pInput == null)) {
/* 33:68 */       throw new NullPointerException();
/* 34:   */     }
/* 35:70 */     this.entry = pEntry;
/* 36:71 */     this.input = pInput;
/* 37:72 */     this.type = 2;
/* 38:73 */     this.targetFile = null;
/* 39:74 */     this.replaceMode = replace;
/* 40:   */   }
/* 41:   */   
/* 42:   */   ArchiveEntry getEntry()
/* 43:   */   {
/* 44:78 */     return this.entry;
/* 45:   */   }
/* 46:   */   
/* 47:   */   InputStream getInput()
/* 48:   */   {
/* 49:82 */     return this.input;
/* 50:   */   }
/* 51:   */   
/* 52:   */   String targetFile()
/* 53:   */   {
/* 54:86 */     return this.targetFile;
/* 55:   */   }
/* 56:   */   
/* 57:   */   int type()
/* 58:   */   {
/* 59:90 */     return this.type;
/* 60:   */   }
/* 61:   */   
/* 62:   */   boolean isReplaceMode()
/* 63:   */   {
/* 64:94 */     return this.replaceMode;
/* 65:   */   }
/* 66:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.changes.Change
 * JD-Core Version:    0.7.0.1
 */