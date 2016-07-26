/*   1:    */ package weka.core.xml;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.FileInputStream;
/*   5:    */ import java.io.FileOutputStream;
/*   6:    */ import java.io.InputStream;
/*   7:    */ import java.io.OutputStream;
/*   8:    */ import java.lang.reflect.Constructor;
/*   9:    */ import java.lang.reflect.Method;
/*  10:    */ import weka.core.RevisionHandler;
/*  11:    */ import weka.core.RevisionUtils;
/*  12:    */ 
/*  13:    */ public class KOML
/*  14:    */   implements RevisionHandler
/*  15:    */ {
/*  16: 47 */   protected static boolean m_Present = false;
/*  17:    */   public static final String FILE_EXTENSION = ".koml";
/*  18:    */   
/*  19:    */   static
/*  20:    */   {
/*  21: 54 */     checkForKOML();
/*  22:    */   }
/*  23:    */   
/*  24:    */   private static void checkForKOML()
/*  25:    */   {
/*  26:    */     try
/*  27:    */     {
/*  28: 62 */       Class.forName("fr.dyade.koala.xml.koml.KOMLSerializer");
/*  29: 63 */       m_Present = true;
/*  30:    */     }
/*  31:    */     catch (Exception e)
/*  32:    */     {
/*  33: 65 */       m_Present = false;
/*  34:    */     }
/*  35:    */   }
/*  36:    */   
/*  37:    */   public static boolean isPresent()
/*  38:    */   {
/*  39: 76 */     return m_Present;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public static Object read(String filename)
/*  43:    */     throws Exception
/*  44:    */   {
/*  45: 87 */     return read(new FileInputStream(filename));
/*  46:    */   }
/*  47:    */   
/*  48:    */   public static Object read(File file)
/*  49:    */     throws Exception
/*  50:    */   {
/*  51: 98 */     return read(new FileInputStream(file));
/*  52:    */   }
/*  53:    */   
/*  54:    */   public static Object read(InputStream stream)
/*  55:    */     throws Exception
/*  56:    */   {
/*  57:122 */     Object result = null;
/*  58:    */     
/*  59:    */ 
/*  60:125 */     Class<?> komlClass = Class.forName("fr.dyade.koala.xml.koml.KOMLDeserializer");
/*  61:126 */     Class<?>[] komlClassArgs = new Class[2];
/*  62:127 */     komlClassArgs[0] = InputStream.class;
/*  63:128 */     komlClassArgs[1] = Boolean.TYPE;
/*  64:129 */     Object[] komlArgs = new Object[2];
/*  65:130 */     komlArgs[0] = stream;
/*  66:131 */     komlArgs[1] = new Boolean(false);
/*  67:132 */     Constructor<?> constructor = komlClass.getConstructor(komlClassArgs);
/*  68:133 */     Object koml = constructor.newInstance(komlArgs);
/*  69:134 */     Class<?>[] readArgsClasses = new Class[0];
/*  70:135 */     Method methodRead = komlClass.getMethod("readObject", readArgsClasses);
/*  71:136 */     Object[] readArgs = new Object[0];
/*  72:137 */     Class<?>[] closeArgsClasses = new Class[0];
/*  73:138 */     Method methodClose = komlClass.getMethod("close", closeArgsClasses);
/*  74:139 */     Object[] closeArgs = new Object[0];
/*  75:    */     try
/*  76:    */     {
/*  77:143 */       result = methodRead.invoke(koml, readArgs);
/*  78:    */     }
/*  79:    */     catch (Exception e)
/*  80:    */     {
/*  81:145 */       result = null;
/*  82:    */     }
/*  83:    */     finally
/*  84:    */     {
/*  85:147 */       methodClose.invoke(koml, closeArgs);
/*  86:    */     }
/*  87:150 */     return result;
/*  88:    */   }
/*  89:    */   
/*  90:    */   public static boolean write(String filename, Object o)
/*  91:    */     throws Exception
/*  92:    */   {
/*  93:162 */     return write(new FileOutputStream(filename), o);
/*  94:    */   }
/*  95:    */   
/*  96:    */   public static boolean write(File file, Object o)
/*  97:    */     throws Exception
/*  98:    */   {
/*  99:174 */     return write(new FileOutputStream(file), o);
/* 100:    */   }
/* 101:    */   
/* 102:    */   public static boolean write(OutputStream stream, Object o)
/* 103:    */     throws Exception
/* 104:    */   {
/* 105:199 */     boolean result = false;
/* 106:    */     
/* 107:    */ 
/* 108:202 */     Class<?> komlClass = Class.forName("fr.dyade.koala.xml.koml.KOMLSerializer");
/* 109:203 */     Class<?>[] komlClassArgs = new Class[2];
/* 110:204 */     komlClassArgs[0] = OutputStream.class;
/* 111:205 */     komlClassArgs[1] = Boolean.TYPE;
/* 112:206 */     Object[] komlArgs = new Object[2];
/* 113:207 */     komlArgs[0] = stream;
/* 114:208 */     komlArgs[1] = new Boolean(false);
/* 115:209 */     Constructor<?> constructor = komlClass.getConstructor(komlClassArgs);
/* 116:210 */     Object koml = constructor.newInstance(komlArgs);
/* 117:211 */     Class<?>[] addArgsClasses = new Class[1];
/* 118:212 */     addArgsClasses[0] = Object.class;
/* 119:213 */     Method methodAdd = komlClass.getMethod("addObject", addArgsClasses);
/* 120:214 */     Object[] addArgs = new Object[1];
/* 121:215 */     addArgs[0] = o;
/* 122:216 */     Class<?>[] closeArgsClasses = new Class[0];
/* 123:217 */     Method methodClose = komlClass.getMethod("close", closeArgsClasses);
/* 124:218 */     Object[] closeArgs = new Object[0];
/* 125:    */     try
/* 126:    */     {
/* 127:222 */       methodAdd.invoke(koml, addArgs);
/* 128:223 */       result = true;
/* 129:    */     }
/* 130:    */     catch (Exception e)
/* 131:    */     {
/* 132:225 */       result = false;
/* 133:    */     }
/* 134:    */     finally
/* 135:    */     {
/* 136:227 */       methodClose.invoke(koml, closeArgs);
/* 137:    */     }
/* 138:230 */     return result;
/* 139:    */   }
/* 140:    */   
/* 141:    */   public String getRevision()
/* 142:    */   {
/* 143:240 */     return RevisionUtils.extract("$Revision: 10203 $");
/* 144:    */   }
/* 145:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.xml.KOML
 * JD-Core Version:    0.7.0.1
 */