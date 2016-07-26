/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.FilenameFilter;
/*   5:    */ import java.io.Serializable;
/*   6:    */ import javax.swing.filechooser.FileFilter;
/*   7:    */ 
/*   8:    */ public class ExtensionFileFilter
/*   9:    */   extends FileFilter
/*  10:    */   implements FilenameFilter, Serializable
/*  11:    */ {
/*  12:    */   private static final long serialVersionUID = -5960622841390665131L;
/*  13:    */   protected String m_Description;
/*  14:    */   protected String[] m_Extension;
/*  15:    */   
/*  16:    */   public ExtensionFileFilter(String extension, String description)
/*  17:    */   {
/*  18: 57 */     this.m_Extension = new String[1];
/*  19: 58 */     this.m_Extension[0] = extension;
/*  20: 59 */     this.m_Description = description;
/*  21:    */   }
/*  22:    */   
/*  23:    */   public ExtensionFileFilter(String[] extensions, String description)
/*  24:    */   {
/*  25: 70 */     this.m_Extension = extensions;
/*  26: 71 */     this.m_Description = description;
/*  27:    */   }
/*  28:    */   
/*  29:    */   public String getDescription()
/*  30:    */   {
/*  31: 82 */     return this.m_Description;
/*  32:    */   }
/*  33:    */   
/*  34:    */   public String[] getExtensions()
/*  35:    */   {
/*  36: 91 */     return (String[])this.m_Extension.clone();
/*  37:    */   }
/*  38:    */   
/*  39:    */   public boolean accept(File file)
/*  40:    */   {
/*  41:104 */     String name = file.getName().toLowerCase();
/*  42:105 */     if (file.isDirectory()) {
/*  43:106 */       return true;
/*  44:    */     }
/*  45:108 */     for (String element : this.m_Extension) {
/*  46:109 */       if (name.endsWith(element)) {
/*  47:110 */         return true;
/*  48:    */       }
/*  49:    */     }
/*  50:113 */     return false;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public boolean accept(File dir, String name)
/*  54:    */   {
/*  55:126 */     return accept(new File(dir, name));
/*  56:    */   }
/*  57:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.ExtensionFileFilter
 * JD-Core Version:    0.7.0.1
 */