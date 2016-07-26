/*  1:   */ package org.apache.commons.compress.archivers;
/*  2:   */ 
/*  3:   */ import java.io.BufferedInputStream;
/*  4:   */ import java.io.File;
/*  5:   */ import java.io.FileInputStream;
/*  6:   */ import java.io.InputStream;
/*  7:   */ import java.io.PrintStream;
/*  8:   */ 
/*  9:   */ public final class Lister
/* 10:   */ {
/* 11:35 */   private static final ArchiveStreamFactory factory = new ArchiveStreamFactory();
/* 12:   */   
/* 13:   */   public static void main(String[] args)
/* 14:   */     throws Exception
/* 15:   */   {
/* 16:38 */     if (args.length == 0)
/* 17:   */     {
/* 18:39 */       usage();
/* 19:40 */       return;
/* 20:   */     }
/* 21:42 */     System.out.println("Analysing " + args[0]);
/* 22:43 */     File f = new File(args[0]);
/* 23:44 */     if (!f.isFile()) {
/* 24:45 */       System.err.println(f + " doesn't exist or is a directory");
/* 25:   */     }
/* 26:47 */     InputStream fis = new BufferedInputStream(new FileInputStream(f));
/* 27:   */     ArchiveInputStream ais;
/* 28:   */     ArchiveInputStream ais;
/* 29:49 */     if (args.length > 1) {
/* 30:50 */       ais = factory.createArchiveInputStream(args[1], fis);
/* 31:   */     } else {
/* 32:52 */       ais = factory.createArchiveInputStream(fis);
/* 33:   */     }
/* 34:54 */     System.out.println("Created " + ais.toString());
/* 35:   */     ArchiveEntry ae;
/* 36:56 */     while ((ae = ais.getNextEntry()) != null) {
/* 37:57 */       System.out.println(ae.getName());
/* 38:   */     }
/* 39:59 */     ais.close();
/* 40:60 */     fis.close();
/* 41:   */   }
/* 42:   */   
/* 43:   */   private static void usage()
/* 44:   */   {
/* 45:64 */     System.out.println("Parameters: archive-name [archive-type]");
/* 46:   */   }
/* 47:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.Lister
 * JD-Core Version:    0.7.0.1
 */