/*   1:    */ package weka.gui.scripting;
/*   2:    */ 
/*   3:    */ import java.io.BufferedReader;
/*   4:    */ import java.io.BufferedWriter;
/*   5:    */ import java.io.File;
/*   6:    */ import java.io.FileInputStream;
/*   7:    */ import java.io.FileOutputStream;
/*   8:    */ import java.io.FileReader;
/*   9:    */ import java.io.FileWriter;
/*  10:    */ import java.io.IOException;
/*  11:    */ import java.io.InputStream;
/*  12:    */ import java.io.OutputStream;
/*  13:    */ 
/*  14:    */ public class ScriptUtils
/*  15:    */ {
/*  16:    */   protected static void copyOrMove(File sourceLocation, File targetLocation, boolean move)
/*  17:    */     throws IOException
/*  18:    */   {
/*  19: 62 */     if (sourceLocation.isDirectory())
/*  20:    */     {
/*  21: 63 */       if (!targetLocation.exists()) {
/*  22: 64 */         targetLocation.mkdir();
/*  23:    */       }
/*  24: 66 */       String[] children = sourceLocation.list();
/*  25: 67 */       for (int i = 0; i < children.length; i++) {
/*  26: 68 */         copyOrMove(new File(sourceLocation, children[i]), new File(targetLocation, children[i]), move);
/*  27:    */       }
/*  28: 74 */       if (move) {
/*  29: 75 */         sourceLocation.delete();
/*  30:    */       }
/*  31:    */     }
/*  32:    */     else
/*  33:    */     {
/*  34: 78 */       InputStream in = new FileInputStream(sourceLocation);
/*  35:    */       OutputStream out;
/*  36:    */       OutputStream out;
/*  37: 80 */       if (targetLocation.isDirectory()) {
/*  38: 81 */         out = new FileOutputStream(targetLocation.getAbsolutePath() + File.separator + sourceLocation.getName());
/*  39:    */       } else {
/*  40: 83 */         out = new FileOutputStream(targetLocation);
/*  41:    */       }
/*  42: 86 */       byte[] buf = new byte[1024];
/*  43:    */       int len;
/*  44: 87 */       while ((len = in.read(buf)) > 0) {
/*  45: 88 */         out.write(buf, 0, len);
/*  46:    */       }
/*  47: 90 */       in.close();
/*  48: 91 */       out.close();
/*  49: 93 */       if (move) {
/*  50: 94 */         sourceLocation.delete();
/*  51:    */       }
/*  52:    */     }
/*  53:    */   }
/*  54:    */   
/*  55:    */   public static void copy(File sourceLocation, File targetLocation)
/*  56:    */     throws IOException
/*  57:    */   {
/*  58:106 */     copyOrMove(sourceLocation, targetLocation, false);
/*  59:    */   }
/*  60:    */   
/*  61:    */   public static void move(File sourceLocation, File targetLocation)
/*  62:    */     throws IOException
/*  63:    */   {
/*  64:117 */     copyOrMove(sourceLocation, targetLocation, true);
/*  65:    */   }
/*  66:    */   
/*  67:    */   public static boolean save(File file, String content)
/*  68:    */   {
/*  69:131 */     BufferedWriter writer = null;
/*  70:    */     try
/*  71:    */     {
/*  72:133 */       writer = new BufferedWriter(new FileWriter(file));
/*  73:134 */       writer.write(content);
/*  74:135 */       writer.flush();
/*  75:136 */       return true;
/*  76:    */     }
/*  77:    */     catch (Exception e)
/*  78:    */     {
/*  79:139 */       e.printStackTrace();
/*  80:140 */       ??? = false;
/*  81:    */     }
/*  82:    */     finally
/*  83:    */     {
/*  84:143 */       if (writer != null) {
/*  85:    */         try
/*  86:    */         {
/*  87:145 */           writer.close();
/*  88:    */         }
/*  89:    */         catch (Exception e) {}
/*  90:    */       }
/*  91:    */     }
/*  92:    */   }
/*  93:    */   
/*  94:    */   public static String load(File file)
/*  95:    */   {
/*  96:168 */     StringBuffer result = new StringBuffer();
/*  97:169 */     String newLine = System.getProperty("line.separator");
/*  98:170 */     BufferedReader reader = null;
/*  99:    */     try
/* 100:    */     {
/* 101:173 */       reader = new BufferedReader(new FileReader(file));
/* 102:    */       String line;
/* 103:174 */       while ((line = reader.readLine()) != null)
/* 104:    */       {
/* 105:175 */         result.append(line);
/* 106:176 */         result.append(newLine);
/* 107:    */       }
/* 108:184 */       if (reader != null) {
/* 109:    */         try
/* 110:    */         {
/* 111:186 */           reader.close();
/* 112:    */         }
/* 113:    */         catch (Exception e) {}
/* 114:    */       }
/* 115:194 */       if (result == null) {
/* 116:    */         break label128;
/* 117:    */       }
/* 118:    */     }
/* 119:    */     catch (Exception e)
/* 120:    */     {
/* 121:180 */       e.printStackTrace();
/* 122:181 */       result = null;
/* 123:    */     }
/* 124:    */     finally
/* 125:    */     {
/* 126:184 */       if (reader != null) {
/* 127:    */         try
/* 128:    */         {
/* 129:186 */           reader.close();
/* 130:    */         }
/* 131:    */         catch (Exception e) {}
/* 132:    */       }
/* 133:    */     }
/* 134:194 */     tmpTernaryOp = result.toString();
/* 135:    */     label128:
/* 136:194 */     return null;
/* 137:    */   }
/* 138:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.scripting.ScriptUtils
 * JD-Core Version:    0.7.0.1
 */