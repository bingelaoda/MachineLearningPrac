/*   1:    */ package org.apache.commons.io;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.IOException;
/*   5:    */ 
/*   6:    */ public class FileDeleteStrategy
/*   7:    */ {
/*   8: 41 */   public static final FileDeleteStrategy NORMAL = new FileDeleteStrategy("Normal");
/*   9: 46 */   public static final FileDeleteStrategy FORCE = new ForceFileDeleteStrategy();
/*  10:    */   private final String name;
/*  11:    */   
/*  12:    */   protected FileDeleteStrategy(String name)
/*  13:    */   {
/*  14: 58 */     this.name = name;
/*  15:    */   }
/*  16:    */   
/*  17:    */   public boolean deleteQuietly(File fileToDelete)
/*  18:    */   {
/*  19: 73 */     if ((fileToDelete == null) || (!fileToDelete.exists())) {
/*  20: 74 */       return true;
/*  21:    */     }
/*  22:    */     try
/*  23:    */     {
/*  24: 77 */       return doDelete(fileToDelete);
/*  25:    */     }
/*  26:    */     catch (IOException ex) {}
/*  27: 79 */     return false;
/*  28:    */   }
/*  29:    */   
/*  30:    */   public void delete(File fileToDelete)
/*  31:    */     throws IOException
/*  32:    */   {
/*  33: 94 */     if ((fileToDelete.exists()) && (!doDelete(fileToDelete))) {
/*  34: 95 */       throw new IOException("Deletion failed: " + fileToDelete);
/*  35:    */     }
/*  36:    */   }
/*  37:    */   
/*  38:    */   protected boolean doDelete(File fileToDelete)
/*  39:    */     throws IOException
/*  40:    */   {
/*  41:116 */     return fileToDelete.delete();
/*  42:    */   }
/*  43:    */   
/*  44:    */   public String toString()
/*  45:    */   {
/*  46:126 */     return "FileDeleteStrategy[" + this.name + "]";
/*  47:    */   }
/*  48:    */   
/*  49:    */   static class ForceFileDeleteStrategy
/*  50:    */     extends FileDeleteStrategy
/*  51:    */   {
/*  52:    */     ForceFileDeleteStrategy()
/*  53:    */     {
/*  54:136 */       super();
/*  55:    */     }
/*  56:    */     
/*  57:    */     protected boolean doDelete(File fileToDelete)
/*  58:    */       throws IOException
/*  59:    */     {
/*  60:151 */       FileUtils.forceDelete(fileToDelete);
/*  61:152 */       return true;
/*  62:    */     }
/*  63:    */   }
/*  64:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.io.FileDeleteStrategy
 * JD-Core Version:    0.7.0.1
 */