/*   1:    */ package org.apache.commons.compress.compressors.pack200;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.FileOutputStream;
/*   5:    */ import java.io.IOException;
/*   6:    */ import java.io.OutputStream;
/*   7:    */ import java.util.HashMap;
/*   8:    */ import java.util.Map;
/*   9:    */ import java.util.SortedMap;
/*  10:    */ import java.util.jar.JarFile;
/*  11:    */ import java.util.jar.JarOutputStream;
/*  12:    */ import java.util.jar.Pack200;
/*  13:    */ import java.util.jar.Pack200.Packer;
/*  14:    */ import java.util.jar.Pack200.Unpacker;
/*  15:    */ 
/*  16:    */ public class Pack200Utils
/*  17:    */ {
/*  18:    */   public static void normalize(File jar)
/*  19:    */     throws IOException
/*  20:    */   {
/*  21: 59 */     normalize(jar, jar, null);
/*  22:    */   }
/*  23:    */   
/*  24:    */   public static void normalize(File jar, Map<String, String> props)
/*  25:    */     throws IOException
/*  26:    */   {
/*  27: 79 */     normalize(jar, jar, props);
/*  28:    */   }
/*  29:    */   
/*  30:    */   public static void normalize(File from, File to)
/*  31:    */     throws IOException
/*  32:    */   {
/*  33:103 */     normalize(from, to, null);
/*  34:    */   }
/*  35:    */   
/*  36:    */   public static void normalize(File from, File to, Map<String, String> props)
/*  37:    */     throws IOException
/*  38:    */   {
/*  39:126 */     if (props == null) {
/*  40:127 */       props = new HashMap();
/*  41:    */     }
/*  42:129 */     props.put("pack.segment.limit", "-1");
/*  43:130 */     File f = File.createTempFile("commons-compress", "pack200normalize");
/*  44:131 */     f.deleteOnExit();
/*  45:    */     try
/*  46:    */     {
/*  47:133 */       OutputStream os = new FileOutputStream(f);
/*  48:134 */       JarFile j = null;
/*  49:    */       try
/*  50:    */       {
/*  51:136 */         Pack200.Packer p = Pack200.newPacker();
/*  52:137 */         p.properties().putAll(props);
/*  53:138 */         p.pack(j = new JarFile(from), os);
/*  54:139 */         j = null;
/*  55:140 */         os.close();
/*  56:141 */         os = null;
/*  57:    */         
/*  58:143 */         Pack200.Unpacker u = Pack200.newUnpacker();
/*  59:144 */         os = new JarOutputStream(new FileOutputStream(to));
/*  60:145 */         u.unpack(f, (JarOutputStream)os);
/*  61:    */       }
/*  62:    */       finally
/*  63:    */       {
/*  64:147 */         if (j != null) {
/*  65:148 */           j.close();
/*  66:    */         }
/*  67:150 */         if (os != null) {
/*  68:151 */           os.close();
/*  69:    */         }
/*  70:    */       }
/*  71:    */     }
/*  72:    */     finally
/*  73:    */     {
/*  74:155 */       f.delete();
/*  75:    */     }
/*  76:    */   }
/*  77:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.compressors.pack200.Pack200Utils
 * JD-Core Version:    0.7.0.1
 */