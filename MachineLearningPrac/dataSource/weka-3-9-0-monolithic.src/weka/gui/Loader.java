/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.io.InputStream;
/*   4:    */ import java.io.InputStreamReader;
/*   5:    */ import java.io.Reader;
/*   6:    */ import java.net.URL;
/*   7:    */ 
/*   8:    */ public class Loader
/*   9:    */ {
/*  10:    */   private String dir;
/*  11:    */   
/*  12:    */   public Loader(String dir)
/*  13:    */   {
/*  14: 45 */     this.dir = dir;
/*  15:    */   }
/*  16:    */   
/*  17:    */   public String getDir()
/*  18:    */   {
/*  19: 52 */     return this.dir;
/*  20:    */   }
/*  21:    */   
/*  22:    */   public String processFilename(String filename)
/*  23:    */   {
/*  24: 60 */     if (!filename.startsWith(getDir())) {
/*  25: 61 */       filename = getDir() + filename;
/*  26:    */     }
/*  27: 63 */     return filename;
/*  28:    */   }
/*  29:    */   
/*  30:    */   public static URL getURL(String dir, String filename)
/*  31:    */   {
/*  32: 72 */     Loader loader = new Loader(dir);
/*  33: 73 */     return loader.getURL(filename);
/*  34:    */   }
/*  35:    */   
/*  36:    */   public URL getURL(String filename)
/*  37:    */   {
/*  38: 80 */     filename = processFilename(filename);
/*  39: 81 */     return Loader.class.getClassLoader().getResource(filename);
/*  40:    */   }
/*  41:    */   
/*  42:    */   public static InputStream getInputStream(String dir, String filename)
/*  43:    */   {
/*  44: 91 */     Loader loader = new Loader(dir);
/*  45: 92 */     return loader.getInputStream(filename);
/*  46:    */   }
/*  47:    */   
/*  48:    */   public InputStream getInputStream(String filename)
/*  49:    */   {
/*  50: 99 */     filename = processFilename(filename);
/*  51:100 */     return Loader.class.getResourceAsStream(filename);
/*  52:    */   }
/*  53:    */   
/*  54:    */   public static Reader getReader(String dir, String filename)
/*  55:    */   {
/*  56:109 */     Loader loader = new Loader(dir);
/*  57:110 */     return loader.getReader(filename);
/*  58:    */   }
/*  59:    */   
/*  60:    */   public Reader getReader(String filename)
/*  61:    */   {
/*  62:119 */     InputStream in = getInputStream(filename);
/*  63:121 */     if (in == null) {
/*  64:122 */       return null;
/*  65:    */     }
/*  66:124 */     return new InputStreamReader(in);
/*  67:    */   }
/*  68:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.Loader
 * JD-Core Version:    0.7.0.1
 */