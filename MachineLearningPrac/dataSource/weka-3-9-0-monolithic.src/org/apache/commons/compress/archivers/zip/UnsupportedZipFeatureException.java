/*   1:    */ package org.apache.commons.compress.archivers.zip;
/*   2:    */ 
/*   3:    */ import java.util.zip.ZipException;
/*   4:    */ 
/*   5:    */ public class UnsupportedZipFeatureException
/*   6:    */   extends ZipException
/*   7:    */ {
/*   8:    */   private final Feature reason;
/*   9:    */   private final ZipArchiveEntry entry;
/*  10:    */   private static final long serialVersionUID = 20130101L;
/*  11:    */   
/*  12:    */   public UnsupportedZipFeatureException(Feature reason, ZipArchiveEntry entry)
/*  13:    */   {
/*  14: 41 */     super("unsupported feature " + reason + " used in entry " + entry.getName());
/*  15:    */     
/*  16: 43 */     this.reason = reason;
/*  17: 44 */     this.entry = entry;
/*  18:    */   }
/*  19:    */   
/*  20:    */   public UnsupportedZipFeatureException(ZipMethod method, ZipArchiveEntry entry)
/*  21:    */   {
/*  22: 56 */     super("unsupported feature method '" + method.name() + "' used in entry " + entry.getName());
/*  23:    */     
/*  24: 58 */     this.reason = Feature.METHOD;
/*  25: 59 */     this.entry = entry;
/*  26:    */   }
/*  27:    */   
/*  28:    */   public UnsupportedZipFeatureException(Feature reason)
/*  29:    */   {
/*  30: 70 */     super("unsupported feature " + reason + " used in archive.");
/*  31: 71 */     this.reason = reason;
/*  32: 72 */     this.entry = null;
/*  33:    */   }
/*  34:    */   
/*  35:    */   public Feature getFeature()
/*  36:    */   {
/*  37: 80 */     return this.reason;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public ZipArchiveEntry getEntry()
/*  41:    */   {
/*  42: 88 */     return this.entry;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public static class Feature
/*  46:    */   {
/*  47: 99 */     public static final Feature ENCRYPTION = new Feature("encryption");
/*  48:103 */     public static final Feature METHOD = new Feature("compression method");
/*  49:107 */     public static final Feature DATA_DESCRIPTOR = new Feature("data descriptor");
/*  50:112 */     public static final Feature SPLITTING = new Feature("splitting");
/*  51:    */     private final String name;
/*  52:    */     
/*  53:    */     private Feature(String name)
/*  54:    */     {
/*  55:117 */       this.name = name;
/*  56:    */     }
/*  57:    */     
/*  58:    */     public String toString()
/*  59:    */     {
/*  60:122 */       return this.name;
/*  61:    */     }
/*  62:    */   }
/*  63:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.UnsupportedZipFeatureException
 * JD-Core Version:    0.7.0.1
 */