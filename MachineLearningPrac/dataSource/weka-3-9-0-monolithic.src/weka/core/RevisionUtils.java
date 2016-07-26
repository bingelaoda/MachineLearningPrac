/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ 
/*   5:    */ public class RevisionUtils
/*   6:    */ {
/*   7:    */   public static enum Type
/*   8:    */   {
/*   9: 39 */     UNKNOWN,  CVS,  SUBVERSION;
/*  10:    */     
/*  11:    */     private Type() {}
/*  12:    */   }
/*  13:    */   
/*  14:    */   public static String extract(RevisionHandler handler)
/*  15:    */   {
/*  16: 53 */     return extract(handler.getRevision());
/*  17:    */   }
/*  18:    */   
/*  19:    */   public static String extract(String s)
/*  20:    */   {
/*  21: 65 */     String result = s;
/*  22: 66 */     result = result.replaceAll("\\$Revision:", "");
/*  23: 67 */     result = result.replaceAll("\\$", "");
/*  24: 68 */     result = result.replaceAll(" ", "");
/*  25:    */     
/*  26: 70 */     return result;
/*  27:    */   }
/*  28:    */   
/*  29:    */   public static Type getType(RevisionHandler handler)
/*  30:    */   {
/*  31: 81 */     return getType(extract(handler));
/*  32:    */   }
/*  33:    */   
/*  34:    */   public static Type getType(String revision)
/*  35:    */   {
/*  36: 97 */     Type result = Type.UNKNOWN;
/*  37:    */     try
/*  38:    */     {
/*  39:101 */       Integer.parseInt(revision);
/*  40:102 */       result = Type.SUBVERSION;
/*  41:    */     }
/*  42:    */     catch (Exception e) {}
/*  43:109 */     if (result == Type.UNKNOWN) {
/*  44:    */       try
/*  45:    */       {
/*  46:112 */         if (revision.indexOf('.') == -1) {
/*  47:113 */           throw new Exception("invalid CVS revision - not dots!");
/*  48:    */         }
/*  49:115 */         String[] parts = revision.split("\\.");
/*  50:118 */         if (parts.length < 2) {
/*  51:119 */           throw new Exception("invalid CVS revision - not enough parts separated by dots!");
/*  52:    */         }
/*  53:122 */         for (int i = 0; i < parts.length; i++) {
/*  54:123 */           Integer.parseInt(parts[i]);
/*  55:    */         }
/*  56:125 */         result = Type.CVS;
/*  57:    */       }
/*  58:    */       catch (Exception e) {}
/*  59:    */     }
/*  60:132 */     return result;
/*  61:    */   }
/*  62:    */   
/*  63:    */   public static void main(String[] args)
/*  64:    */     throws Exception
/*  65:    */   {
/*  66:143 */     if (args.length != 1)
/*  67:    */     {
/*  68:144 */       System.err.println("\nUsage: " + RevisionUtils.class.getName() + " <classname>\n");
/*  69:145 */       System.exit(1);
/*  70:    */     }
/*  71:148 */     RevisionHandler handler = (RevisionHandler)Class.forName(args[0]).newInstance();
/*  72:149 */     System.out.println("Type: " + getType(handler));
/*  73:150 */     System.out.println("Revision: " + extract(handler));
/*  74:    */   }
/*  75:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.RevisionUtils
 * JD-Core Version:    0.7.0.1
 */