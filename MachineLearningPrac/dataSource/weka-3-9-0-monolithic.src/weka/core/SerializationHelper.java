/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.io.BufferedInputStream;
/*   4:    */ import java.io.BufferedOutputStream;
/*   5:    */ import java.io.FileInputStream;
/*   6:    */ import java.io.FileOutputStream;
/*   7:    */ import java.io.IOException;
/*   8:    */ import java.io.InputStream;
/*   9:    */ import java.io.ObjectInputStream;
/*  10:    */ import java.io.ObjectOutputStream;
/*  11:    */ import java.io.ObjectStreamClass;
/*  12:    */ import java.io.OutputStream;
/*  13:    */ import java.io.PrintStream;
/*  14:    */ import java.io.Serializable;
/*  15:    */ import java.util.Vector;
/*  16:    */ 
/*  17:    */ public class SerializationHelper
/*  18:    */   implements RevisionHandler
/*  19:    */ {
/*  20:    */   public static final String SERIAL_VERSION_UID = "serialVersionUID";
/*  21:    */   
/*  22:    */   public static boolean isSerializable(String classname)
/*  23:    */   {
/*  24:    */     boolean result;
/*  25:    */     try
/*  26:    */     {
/*  27: 51 */       result = isSerializable(Class.forName(classname));
/*  28:    */     }
/*  29:    */     catch (Exception e)
/*  30:    */     {
/*  31: 53 */       result = false;
/*  32:    */     }
/*  33: 56 */     return result;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public static boolean isSerializable(Class<?> c)
/*  37:    */   {
/*  38: 67 */     return ClassDiscovery.hasInterface(Serializable.class, c);
/*  39:    */   }
/*  40:    */   
/*  41:    */   public static boolean hasUID(String classname)
/*  42:    */   {
/*  43:    */     boolean result;
/*  44:    */     try
/*  45:    */     {
/*  46: 82 */       result = hasUID(Class.forName(classname));
/*  47:    */     }
/*  48:    */     catch (Exception e)
/*  49:    */     {
/*  50: 84 */       result = false;
/*  51:    */     }
/*  52: 87 */     return result;
/*  53:    */   }
/*  54:    */   
/*  55:    */   public static boolean hasUID(Class<?> c)
/*  56:    */   {
/*  57:100 */     boolean result = false;
/*  58:102 */     if (isSerializable(c)) {
/*  59:    */       try
/*  60:    */       {
/*  61:104 */         c.getDeclaredField("serialVersionUID");
/*  62:105 */         result = true;
/*  63:    */       }
/*  64:    */       catch (Exception e)
/*  65:    */       {
/*  66:107 */         result = false;
/*  67:    */       }
/*  68:    */     }
/*  69:111 */     return result;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public static boolean needsUID(String classname)
/*  73:    */   {
/*  74:    */     boolean result;
/*  75:    */     try
/*  76:    */     {
/*  77:127 */       result = needsUID(Class.forName(classname));
/*  78:    */     }
/*  79:    */     catch (Exception e)
/*  80:    */     {
/*  81:129 */       result = false;
/*  82:    */     }
/*  83:132 */     return result;
/*  84:    */   }
/*  85:    */   
/*  86:    */   public static boolean needsUID(Class<?> c)
/*  87:    */   {
/*  88:    */     boolean result;
/*  89:    */     boolean result;
/*  90:146 */     if (isSerializable(c)) {
/*  91:147 */       result = !hasUID(c);
/*  92:    */     } else {
/*  93:149 */       result = false;
/*  94:    */     }
/*  95:152 */     return result;
/*  96:    */   }
/*  97:    */   
/*  98:    */   public static long getUID(String classname)
/*  99:    */   {
/* 100:    */     long result;
/* 101:    */     try
/* 102:    */     {
/* 103:166 */       result = getUID(Class.forName(classname));
/* 104:    */     }
/* 105:    */     catch (Exception e)
/* 106:    */     {
/* 107:168 */       result = 0L;
/* 108:    */     }
/* 109:171 */     return result;
/* 110:    */   }
/* 111:    */   
/* 112:    */   public static long getUID(Class<?> c)
/* 113:    */   {
/* 114:181 */     return ObjectStreamClass.lookup(c).getSerialVersionUID();
/* 115:    */   }
/* 116:    */   
/* 117:    */   public static void write(String filename, Object o)
/* 118:    */     throws Exception
/* 119:    */   {
/* 120:192 */     write(new FileOutputStream(filename), o);
/* 121:    */   }
/* 122:    */   
/* 123:    */   public static void write(OutputStream stream, Object o)
/* 124:    */     throws Exception
/* 125:    */   {
/* 126:205 */     if (!(stream instanceof BufferedOutputStream)) {
/* 127:206 */       stream = new BufferedOutputStream(stream);
/* 128:    */     }
/* 129:209 */     ObjectOutputStream oos = new ObjectOutputStream(stream);
/* 130:210 */     oos.writeObject(o);
/* 131:211 */     oos.flush();
/* 132:212 */     oos.close();
/* 133:    */   }
/* 134:    */   
/* 135:    */   public static void writeAll(String filename, Object[] o)
/* 136:    */     throws Exception
/* 137:    */   {
/* 138:223 */     writeAll(new FileOutputStream(filename), o);
/* 139:    */   }
/* 140:    */   
/* 141:    */   public static void writeAll(OutputStream stream, Object[] o)
/* 142:    */     throws Exception
/* 143:    */   {
/* 144:237 */     if (!(stream instanceof BufferedOutputStream)) {
/* 145:238 */       stream = new BufferedOutputStream(stream);
/* 146:    */     }
/* 147:241 */     ObjectOutputStream oos = new ObjectOutputStream(stream);
/* 148:242 */     for (int i = 0; i < o.length; i++) {
/* 149:243 */       oos.writeObject(o[i]);
/* 150:    */     }
/* 151:245 */     oos.flush();
/* 152:246 */     oos.close();
/* 153:    */   }
/* 154:    */   
/* 155:    */   public static Object read(String filename)
/* 156:    */     throws Exception
/* 157:    */   {
/* 158:257 */     return read(new FileInputStream(filename));
/* 159:    */   }
/* 160:    */   
/* 161:    */   public static Object read(InputStream stream)
/* 162:    */     throws Exception
/* 163:    */   {
/* 164:271 */     if (!(stream instanceof BufferedInputStream)) {
/* 165:272 */       stream = new BufferedInputStream(stream);
/* 166:    */     }
/* 167:275 */     ObjectInputStream ois = new ObjectInputStream(stream);
/* 168:276 */     Object result = ois.readObject();
/* 169:277 */     ois.close();
/* 170:    */     
/* 171:279 */     return result;
/* 172:    */   }
/* 173:    */   
/* 174:    */   public static Object[] readAll(String filename)
/* 175:    */     throws Exception
/* 176:    */   {
/* 177:290 */     return readAll(new FileInputStream(filename));
/* 178:    */   }
/* 179:    */   
/* 180:    */   public static Object[] readAll(InputStream stream)
/* 181:    */     throws Exception
/* 182:    */   {
/* 183:304 */     if (!(stream instanceof BufferedInputStream)) {
/* 184:305 */       stream = new BufferedInputStream(stream);
/* 185:    */     }
/* 186:308 */     ObjectInputStream ois = new ObjectInputStream(stream);
/* 187:309 */     Vector<Object> result = new Vector();
/* 188:    */     try
/* 189:    */     {
/* 190:    */       for (;;)
/* 191:    */       {
/* 192:312 */         result.add(ois.readObject());
/* 193:    */       }
/* 194:    */     }
/* 195:    */     catch (IOException e)
/* 196:    */     {
/* 197:317 */       ois.close();
/* 198:    */     }
/* 199:319 */     return result.toArray(new Object[result.size()]);
/* 200:    */   }
/* 201:    */   
/* 202:    */   public String getRevision()
/* 203:    */   {
/* 204:329 */     return RevisionUtils.extract("$Revision: 12580 $");
/* 205:    */   }
/* 206:    */   
/* 207:    */   public static void main(String[] args)
/* 208:    */     throws Exception
/* 209:    */   {
/* 210:340 */     if (args.length == 0)
/* 211:    */     {
/* 212:341 */       System.out.println("\nUsage: " + SerializationHelper.class.getName() + " classname [classname [classname [...]]]\n");
/* 213:    */       
/* 214:343 */       System.exit(1);
/* 215:    */     }
/* 216:347 */     System.out.println();
/* 217:348 */     for (String arg : args)
/* 218:    */     {
/* 219:349 */       System.out.println(arg);
/* 220:350 */       System.out.println("- is serializable: " + isSerializable(arg));
/* 221:351 */       System.out.println("- has serialVersionUID: " + hasUID(arg));
/* 222:352 */       System.out.println("- needs serialVersionUID: " + needsUID(arg));
/* 223:    */       
/* 224:354 */       System.out.println("- serialVersionUID: private static final long serialVersionUID = " + getUID(arg) + "L;");
/* 225:    */       
/* 226:    */ 
/* 227:357 */       System.out.println();
/* 228:    */     }
/* 229:    */   }
/* 230:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.SerializationHelper
 * JD-Core Version:    0.7.0.1
 */