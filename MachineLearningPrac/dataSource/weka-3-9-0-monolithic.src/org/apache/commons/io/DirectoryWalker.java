/*   1:    */ package org.apache.commons.io;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.FileFilter;
/*   5:    */ import java.io.IOException;
/*   6:    */ import java.util.Collection;
/*   7:    */ import org.apache.commons.io.filefilter.FileFilterUtils;
/*   8:    */ import org.apache.commons.io.filefilter.IOFileFilter;
/*   9:    */ import org.apache.commons.io.filefilter.TrueFileFilter;
/*  10:    */ 
/*  11:    */ public abstract class DirectoryWalker
/*  12:    */ {
/*  13:    */   private final FileFilter filter;
/*  14:    */   private final int depthLimit;
/*  15:    */   
/*  16:    */   protected DirectoryWalker()
/*  17:    */   {
/*  18:266 */     this(null, -1);
/*  19:    */   }
/*  20:    */   
/*  21:    */   protected DirectoryWalker(FileFilter filter, int depthLimit)
/*  22:    */   {
/*  23:282 */     this.filter = filter;
/*  24:283 */     this.depthLimit = depthLimit;
/*  25:    */   }
/*  26:    */   
/*  27:    */   protected DirectoryWalker(IOFileFilter directoryFilter, IOFileFilter fileFilter, int depthLimit)
/*  28:    */   {
/*  29:301 */     if ((directoryFilter == null) && (fileFilter == null))
/*  30:    */     {
/*  31:302 */       this.filter = null;
/*  32:    */     }
/*  33:    */     else
/*  34:    */     {
/*  35:304 */       directoryFilter = directoryFilter != null ? directoryFilter : TrueFileFilter.TRUE;
/*  36:305 */       fileFilter = fileFilter != null ? fileFilter : TrueFileFilter.TRUE;
/*  37:306 */       directoryFilter = FileFilterUtils.makeDirectoryOnly(directoryFilter);
/*  38:307 */       fileFilter = FileFilterUtils.makeFileOnly(fileFilter);
/*  39:308 */       this.filter = FileFilterUtils.orFileFilter(directoryFilter, fileFilter);
/*  40:    */     }
/*  41:310 */     this.depthLimit = depthLimit;
/*  42:    */   }
/*  43:    */   
/*  44:    */   protected final void walk(File startDirectory, Collection results)
/*  45:    */     throws IOException
/*  46:    */   {
/*  47:330 */     if (startDirectory == null) {
/*  48:331 */       throw new NullPointerException("Start Directory is null");
/*  49:    */     }
/*  50:    */     try
/*  51:    */     {
/*  52:334 */       handleStart(startDirectory, results);
/*  53:335 */       walk(startDirectory, 0, results);
/*  54:336 */       handleEnd(results);
/*  55:    */     }
/*  56:    */     catch (CancelException cancel)
/*  57:    */     {
/*  58:338 */       handleCancelled(startDirectory, results, cancel);
/*  59:    */     }
/*  60:    */   }
/*  61:    */   
/*  62:    */   private void walk(File directory, int depth, Collection results)
/*  63:    */     throws IOException
/*  64:    */   {
/*  65:351 */     checkIfCancelled(directory, depth, results);
/*  66:352 */     if (handleDirectory(directory, depth, results))
/*  67:    */     {
/*  68:353 */       handleDirectoryStart(directory, depth, results);
/*  69:354 */       int childDepth = depth + 1;
/*  70:355 */       if ((this.depthLimit < 0) || (childDepth <= this.depthLimit))
/*  71:    */       {
/*  72:356 */         checkIfCancelled(directory, depth, results);
/*  73:357 */         File[] childFiles = this.filter == null ? directory.listFiles() : directory.listFiles(this.filter);
/*  74:358 */         if (childFiles == null) {
/*  75:359 */           handleRestricted(directory, childDepth, results);
/*  76:    */         } else {
/*  77:361 */           for (int i = 0; i < childFiles.length; i++)
/*  78:    */           {
/*  79:362 */             File childFile = childFiles[i];
/*  80:363 */             if (childFile.isDirectory())
/*  81:    */             {
/*  82:364 */               walk(childFile, childDepth, results);
/*  83:    */             }
/*  84:    */             else
/*  85:    */             {
/*  86:366 */               checkIfCancelled(childFile, childDepth, results);
/*  87:367 */               handleFile(childFile, childDepth, results);
/*  88:368 */               checkIfCancelled(childFile, childDepth, results);
/*  89:    */             }
/*  90:    */           }
/*  91:    */         }
/*  92:    */       }
/*  93:373 */       handleDirectoryEnd(directory, depth, results);
/*  94:    */     }
/*  95:375 */     checkIfCancelled(directory, depth, results);
/*  96:    */   }
/*  97:    */   
/*  98:    */   protected final void checkIfCancelled(File file, int depth, Collection results)
/*  99:    */     throws IOException
/* 100:    */   {
/* 101:394 */     if (handleIsCancelled(file, depth, results)) {
/* 102:395 */       throw new CancelException(file, depth);
/* 103:    */     }
/* 104:    */   }
/* 105:    */   
/* 106:    */   protected boolean handleIsCancelled(File file, int depth, Collection results)
/* 107:    */     throws IOException
/* 108:    */   {
/* 109:437 */     return false;
/* 110:    */   }
/* 111:    */   
/* 112:    */   protected void handleCancelled(File startDirectory, Collection results, CancelException cancel)
/* 113:    */     throws IOException
/* 114:    */   {
/* 115:456 */     throw cancel;
/* 116:    */   }
/* 117:    */   
/* 118:    */   protected void handleStart(File startDirectory, Collection results)
/* 119:    */     throws IOException
/* 120:    */   {}
/* 121:    */   
/* 122:    */   protected boolean handleDirectory(File directory, int depth, Collection results)
/* 123:    */     throws IOException
/* 124:    */   {
/* 125:490 */     return true;
/* 126:    */   }
/* 127:    */   
/* 128:    */   protected void handleDirectoryStart(File directory, int depth, Collection results)
/* 129:    */     throws IOException
/* 130:    */   {}
/* 131:    */   
/* 132:    */   protected void handleFile(File file, int depth, Collection results)
/* 133:    */     throws IOException
/* 134:    */   {}
/* 135:    */   
/* 136:    */   protected void handleRestricted(File directory, int depth, Collection results)
/* 137:    */     throws IOException
/* 138:    */   {}
/* 139:    */   
/* 140:    */   protected void handleDirectoryEnd(File directory, int depth, Collection results)
/* 141:    */     throws IOException
/* 142:    */   {}
/* 143:    */   
/* 144:    */   protected void handleEnd(Collection results)
/* 145:    */     throws IOException
/* 146:    */   {}
/* 147:    */   
/* 148:    */   public static class CancelException
/* 149:    */     extends IOException
/* 150:    */   {
/* 151:    */     private static final long serialVersionUID = 1347339620135041008L;
/* 152:    */     private File file;
/* 153:574 */     private int depth = -1;
/* 154:    */     
/* 155:    */     public CancelException(File file, int depth)
/* 156:    */     {
/* 157:584 */       this("Operation Cancelled", file, depth);
/* 158:    */     }
/* 159:    */     
/* 160:    */     public CancelException(String message, File file, int depth)
/* 161:    */     {
/* 162:597 */       super();
/* 163:598 */       this.file = file;
/* 164:599 */       this.depth = depth;
/* 165:    */     }
/* 166:    */     
/* 167:    */     public File getFile()
/* 168:    */     {
/* 169:608 */       return this.file;
/* 170:    */     }
/* 171:    */     
/* 172:    */     public int getDepth()
/* 173:    */     {
/* 174:617 */       return this.depth;
/* 175:    */     }
/* 176:    */   }
/* 177:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.io.DirectoryWalker
 * JD-Core Version:    0.7.0.1
 */