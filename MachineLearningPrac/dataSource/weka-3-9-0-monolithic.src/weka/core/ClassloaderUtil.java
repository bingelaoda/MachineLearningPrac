/*  1:   */ package weka.core;
/*  2:   */ 
/*  3:   */ import java.io.File;
/*  4:   */ import java.io.IOException;
/*  5:   */ import java.io.PrintStream;
/*  6:   */ import java.lang.reflect.Method;
/*  7:   */ import java.net.URI;
/*  8:   */ import java.net.URL;
/*  9:   */ import java.net.URLClassLoader;
/* 10:   */ 
/* 11:   */ public class ClassloaderUtil
/* 12:   */   implements RevisionHandler
/* 13:   */ {
/* 14:39 */   private static final Class<?>[] parameters = { URL.class };
/* 15:   */   
/* 16:   */   public static void addFile(String s)
/* 17:   */     throws IOException
/* 18:   */   {
/* 19:48 */     File f = new File(s);
/* 20:49 */     addFile(f);
/* 21:   */   }
/* 22:   */   
/* 23:   */   public static void addFile(File f)
/* 24:   */     throws IOException
/* 25:   */   {
/* 26:59 */     addURL(f.toURI().toURL());
/* 27:   */   }
/* 28:   */   
/* 29:   */   public static void addURL(URL u)
/* 30:   */     throws IOException
/* 31:   */   {
/* 32:69 */     ClassloaderUtil clu = new ClassloaderUtil();
/* 33:   */     
/* 34:   */ 
/* 35:72 */     URLClassLoader sysLoader = (URLClassLoader)clu.getClass().getClassLoader();
/* 36:73 */     URL[] urls = sysLoader.getURLs();
/* 37:74 */     for (URL url : urls) {
/* 38:75 */       if (url.toString().toLowerCase().equals(u.toString().toLowerCase()))
/* 39:   */       {
/* 40:76 */         System.err.println("URL " + u + " is already in the CLASSPATH");
/* 41:77 */         return;
/* 42:   */       }
/* 43:   */     }
/* 44:80 */     Class<?> sysclass = URLClassLoader.class;
/* 45:   */     try
/* 46:   */     {
/* 47:82 */       Method method = sysclass.getDeclaredMethod("addURL", parameters);
/* 48:83 */       method.setAccessible(true);
/* 49:84 */       method.invoke(sysLoader, new Object[] { u });
/* 50:   */     }
/* 51:   */     catch (Throwable t)
/* 52:   */     {
/* 53:86 */       t.printStackTrace();
/* 54:87 */       throw new IOException("Error, could not add URL to system classloader");
/* 55:   */     }
/* 56:   */   }
/* 57:   */   
/* 58:   */   public String getRevision()
/* 59:   */   {
/* 60:98 */     return RevisionUtils.extract("$Revision: 10203 $");
/* 61:   */   }
/* 62:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.ClassloaderUtil
 * JD-Core Version:    0.7.0.1
 */