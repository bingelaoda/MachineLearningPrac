/*  1:   */ package weka.core;
/*  2:   */ 
/*  3:   */ import java.io.File;
/*  4:   */ 
/*  5:   */ public class FileHelper
/*  6:   */ {
/*  7:   */   protected String m_filePath;
/*  8:   */   
/*  9:   */   public FileHelper(File file)
/* 10:   */   {
/* 11:44 */     this.m_filePath = file.toString();
/* 12:   */   }
/* 13:   */   
/* 14:   */   public FileHelper() {}
/* 15:   */   
/* 16:   */   public void setFilePath(String path)
/* 17:   */   {
/* 18:59 */     this.m_filePath = path;
/* 19:   */   }
/* 20:   */   
/* 21:   */   public String getFilePath()
/* 22:   */   {
/* 23:68 */     return this.m_filePath;
/* 24:   */   }
/* 25:   */   
/* 26:   */   public File getFile()
/* 27:   */   {
/* 28:77 */     if (this.m_filePath != null) {
/* 29:78 */       return new File(this.m_filePath);
/* 30:   */     }
/* 31:81 */     return new File(System.getProperty("user.home"));
/* 32:   */   }
/* 33:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.FileHelper
 * JD-Core Version:    0.7.0.1
 */