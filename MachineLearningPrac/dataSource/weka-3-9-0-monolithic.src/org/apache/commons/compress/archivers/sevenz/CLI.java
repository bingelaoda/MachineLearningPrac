/*   1:    */ package org.apache.commons.compress.archivers.sevenz;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.FileOutputStream;
/*   5:    */ import java.io.IOException;
/*   6:    */ import java.io.PrintStream;
/*   7:    */ 
/*   8:    */ public class CLI
/*   9:    */ {
/*  10: 26 */   private static final byte[] BUF = new byte[8192];
/*  11:    */   
/*  12:    */   private static abstract enum Mode
/*  13:    */   {
/*  14: 29 */     LIST("Analysing"),  EXTRACT("Extracting");
/*  15:    */     
/*  16:    */     private final String message;
/*  17:    */     
/*  18:    */     private Mode(String message)
/*  19:    */     {
/*  20:110 */       this.message = message;
/*  21:    */     }
/*  22:    */     
/*  23:    */     public String getMessage()
/*  24:    */     {
/*  25:113 */       return this.message;
/*  26:    */     }
/*  27:    */     
/*  28:    */     public abstract void takeAction(SevenZFile paramSevenZFile, SevenZArchiveEntry paramSevenZArchiveEntry)
/*  29:    */       throws IOException;
/*  30:    */   }
/*  31:    */   
/*  32:    */   public static void main(String[] args)
/*  33:    */     throws Exception
/*  34:    */   {
/*  35:120 */     if (args.length == 0)
/*  36:    */     {
/*  37:121 */       usage();
/*  38:122 */       return;
/*  39:    */     }
/*  40:124 */     Mode mode = grabMode(args);
/*  41:125 */     System.out.println(mode.getMessage() + " " + args[0]);
/*  42:126 */     File f = new File(args[0]);
/*  43:127 */     if (!f.isFile()) {
/*  44:128 */       System.err.println(f + " doesn't exist or is a directory");
/*  45:    */     }
/*  46:130 */     SevenZFile archive = new SevenZFile(f);
/*  47:    */     try
/*  48:    */     {
/*  49:    */       SevenZArchiveEntry ae;
/*  50:133 */       while ((ae = archive.getNextEntry()) != null) {
/*  51:134 */         mode.takeAction(archive, ae);
/*  52:    */       }
/*  53:    */     }
/*  54:    */     finally
/*  55:    */     {
/*  56:137 */       archive.close();
/*  57:    */     }
/*  58:    */   }
/*  59:    */   
/*  60:    */   private static void usage()
/*  61:    */   {
/*  62:142 */     System.out.println("Parameters: archive-name [list|extract]");
/*  63:    */   }
/*  64:    */   
/*  65:    */   private static Mode grabMode(String[] args)
/*  66:    */   {
/*  67:146 */     if (args.length < 2) {
/*  68:147 */       return Mode.LIST;
/*  69:    */     }
/*  70:149 */     return (Mode)Enum.valueOf(Mode.class, args[1].toUpperCase());
/*  71:    */   }
/*  72:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.sevenz.CLI
 * JD-Core Version:    0.7.0.1
 */