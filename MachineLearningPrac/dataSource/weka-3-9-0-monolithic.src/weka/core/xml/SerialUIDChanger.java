/*   1:    */ package weka.core.xml;
/*   2:    */ 
/*   3:    */ import java.io.BufferedInputStream;
/*   4:    */ import java.io.BufferedOutputStream;
/*   5:    */ import java.io.BufferedReader;
/*   6:    */ import java.io.BufferedWriter;
/*   7:    */ import java.io.File;
/*   8:    */ import java.io.FileInputStream;
/*   9:    */ import java.io.FileOutputStream;
/*  10:    */ import java.io.FileReader;
/*  11:    */ import java.io.FileWriter;
/*  12:    */ import java.io.ObjectInputStream;
/*  13:    */ import java.io.ObjectOutputStream;
/*  14:    */ import java.io.PrintStream;
/*  15:    */ import weka.core.RevisionHandler;
/*  16:    */ import weka.core.RevisionUtils;
/*  17:    */ 
/*  18:    */ public class SerialUIDChanger
/*  19:    */   implements RevisionHandler
/*  20:    */ {
/*  21:    */   protected static boolean checkKOML()
/*  22:    */     throws Exception
/*  23:    */   {
/*  24: 55 */     if (!KOML.isPresent()) {
/*  25: 56 */       throw new Exception("KOML is not present!");
/*  26:    */     }
/*  27: 58 */     return true;
/*  28:    */   }
/*  29:    */   
/*  30:    */   public static boolean isKOML(String filename)
/*  31:    */   {
/*  32: 69 */     return filename.toLowerCase().endsWith(".koml");
/*  33:    */   }
/*  34:    */   
/*  35:    */   protected static Object readBinary(String binary)
/*  36:    */     throws Exception
/*  37:    */   {
/*  38: 85 */     FileInputStream fi = new FileInputStream(binary);
/*  39: 86 */     ObjectInputStream oi = new ObjectInputStream(new BufferedInputStream(fi));
/*  40: 87 */     Object o = oi.readObject();
/*  41: 88 */     oi.close();
/*  42:    */     
/*  43: 90 */     return o;
/*  44:    */   }
/*  45:    */   
/*  46:    */   protected static void writeBinary(String binary, Object o)
/*  47:    */     throws Exception
/*  48:    */   {
/*  49:104 */     FileOutputStream fo = new FileOutputStream(binary);
/*  50:105 */     ObjectOutputStream oo = new ObjectOutputStream(new BufferedOutputStream(fo));
/*  51:106 */     oo.writeObject(o);
/*  52:107 */     oo.close();
/*  53:    */   }
/*  54:    */   
/*  55:    */   public static void binaryToKOML(String binary, String koml)
/*  56:    */     throws Exception
/*  57:    */   {
/*  58:121 */     checkKOML();
/*  59:    */     
/*  60:    */ 
/*  61:124 */     Object o = readBinary(binary);
/*  62:125 */     if (o == null) {
/*  63:126 */       throw new Exception("Failed to deserialize object from binary file '" + binary + "'!");
/*  64:    */     }
/*  65:129 */     KOML.write(koml, o);
/*  66:    */   }
/*  67:    */   
/*  68:    */   public static void komlToBinary(String koml, String binary)
/*  69:    */     throws Exception
/*  70:    */   {
/*  71:142 */     checkKOML();
/*  72:    */     
/*  73:    */ 
/*  74:145 */     Object o = KOML.read(koml);
/*  75:146 */     if (o == null) {
/*  76:147 */       throw new Exception("Failed to deserialize object from XML file '" + koml + "'!");
/*  77:    */     }
/*  78:150 */     writeBinary(binary, o);
/*  79:    */   }
/*  80:    */   
/*  81:    */   public static void changeUID(long oldUID, long newUID, String fromFile, String toFile)
/*  82:    */     throws Exception
/*  83:    */   {
/*  84:    */     String inputFile;
/*  85:174 */     if (!isKOML(fromFile))
/*  86:    */     {
/*  87:175 */       String inputFile = fromFile + ".koml";
/*  88:176 */       binaryToKOML(fromFile, inputFile);
/*  89:    */     }
/*  90:    */     else
/*  91:    */     {
/*  92:179 */       inputFile = fromFile;
/*  93:    */     }
/*  94:183 */     BufferedReader reader = new BufferedReader(new FileReader(inputFile));
/*  95:184 */     String content = "";
/*  96:    */     String line;
/*  97:185 */     while ((line = reader.readLine()) != null)
/*  98:    */     {
/*  99:186 */       if (!content.equals("")) {
/* 100:187 */         content = content + "\n";
/* 101:    */       }
/* 102:188 */       content = content + line;
/* 103:    */     }
/* 104:190 */     reader.close();
/* 105:    */     
/* 106:    */ 
/* 107:193 */     content = content.replaceAll(" uid='" + Long.toString(oldUID) + "'", " uid='" + Long.toString(newUID) + "'");
/* 108:    */     
/* 109:    */ 
/* 110:196 */     String tempFile = inputFile + ".temp";
/* 111:197 */     BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
/* 112:198 */     writer.write(content);
/* 113:199 */     writer.flush();
/* 114:200 */     writer.close();
/* 115:203 */     if (!isKOML(toFile))
/* 116:    */     {
/* 117:204 */       komlToBinary(tempFile, toFile);
/* 118:    */     }
/* 119:    */     else
/* 120:    */     {
/* 121:207 */       writer = new BufferedWriter(new FileWriter(toFile));
/* 122:208 */       writer.write(content);
/* 123:209 */       writer.flush();
/* 124:210 */       writer.close();
/* 125:    */     }
/* 126:214 */     File file = new File(tempFile);
/* 127:215 */     file.delete();
/* 128:    */   }
/* 129:    */   
/* 130:    */   public String getRevision()
/* 131:    */   {
/* 132:224 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 133:    */   }
/* 134:    */   
/* 135:    */   public static void main(String[] args)
/* 136:    */     throws Exception
/* 137:    */   {
/* 138:236 */     if (args.length != 4)
/* 139:    */     {
/* 140:237 */       System.out.println();
/* 141:238 */       System.out.println("Usage: " + SerialUIDChanger.class.getName() + " <oldUID> <newUID> <oldFilename> <newFilename>");
/* 142:239 */       System.out.println("       <oldFilename> and <newFilename> have to be different");
/* 143:240 */       System.out.println();
/* 144:    */     }
/* 145:    */     else
/* 146:    */     {
/* 147:243 */       if (args[2].equals(args[3])) {
/* 148:244 */         throw new Exception("Filenames have to be different!");
/* 149:    */       }
/* 150:246 */       changeUID(Long.parseLong(args[0]), Long.parseLong(args[1]), args[2], args[3]);
/* 151:    */     }
/* 152:    */   }
/* 153:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.xml.SerialUIDChanger
 * JD-Core Version:    0.7.0.1
 */