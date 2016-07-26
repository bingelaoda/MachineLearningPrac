/*   1:    */ package weka.core.xml;
/*   2:    */ 
/*   3:    */ import java.io.BufferedInputStream;
/*   4:    */ import java.io.BufferedOutputStream;
/*   5:    */ import java.io.File;
/*   6:    */ import java.io.FileInputStream;
/*   7:    */ import java.io.FileOutputStream;
/*   8:    */ import java.io.InputStream;
/*   9:    */ import java.io.OutputStream;
/*  10:    */ import java.io.Reader;
/*  11:    */ import java.io.Writer;
/*  12:    */ import java.lang.reflect.Constructor;
/*  13:    */ import java.lang.reflect.Method;
/*  14:    */ import weka.core.RevisionHandler;
/*  15:    */ import weka.core.RevisionUtils;
/*  16:    */ 
/*  17:    */ public class XStream
/*  18:    */   implements RevisionHandler
/*  19:    */ {
/*  20: 51 */   protected static boolean m_Present = false;
/*  21:    */   public static final String FILE_EXTENSION = ".xstream";
/*  22:    */   
/*  23:    */   static
/*  24:    */   {
/*  25: 58 */     checkForXStream();
/*  26:    */   }
/*  27:    */   
/*  28:    */   private static void checkForXStream()
/*  29:    */   {
/*  30:    */     try
/*  31:    */     {
/*  32: 66 */       Class.forName("com.thoughtworks.xstream.XStream");
/*  33: 67 */       m_Present = true;
/*  34:    */     }
/*  35:    */     catch (Exception e)
/*  36:    */     {
/*  37: 69 */       m_Present = false;
/*  38:    */     }
/*  39:    */   }
/*  40:    */   
/*  41:    */   public static boolean isPresent()
/*  42:    */   {
/*  43: 80 */     return m_Present;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public static String serialize(Object toSerialize)
/*  47:    */     throws Exception
/*  48:    */   {
/*  49: 94 */     Class<?>[] serializeArgsClasses = new Class[1];
/*  50: 95 */     Object[] serializeArgs = new Object[1];
/*  51:    */     
/*  52:    */ 
/*  53:    */ 
/*  54: 99 */     Class<?> xstreamClass = Class.forName("com.thoughtworks.xstream.XStream");
/*  55:100 */     Constructor<?> constructor = xstreamClass.getConstructor(new Class[0]);
/*  56:101 */     Object xstream = constructor.newInstance(new Object[0]);
/*  57:    */     
/*  58:103 */     serializeArgsClasses[0] = Object.class;
/*  59:104 */     serializeArgs[0] = toSerialize;
/*  60:105 */     Method methodSerialize = xstreamClass.getMethod("toXML", serializeArgsClasses);
/*  61:    */     String result;
/*  62:    */     try
/*  63:    */     {
/*  64:109 */       result = (String)methodSerialize.invoke(xstream, serializeArgs);
/*  65:    */     }
/*  66:    */     catch (Exception ex)
/*  67:    */     {
/*  68:111 */       result = null;
/*  69:    */     }
/*  70:114 */     return result;
/*  71:    */   }
/*  72:    */   
/*  73:    */   public static boolean write(String filename, Object o)
/*  74:    */     throws Exception
/*  75:    */   {
/*  76:126 */     return write(new File(filename), o);
/*  77:    */   }
/*  78:    */   
/*  79:    */   public static boolean write(File file, Object o)
/*  80:    */     throws Exception
/*  81:    */   {
/*  82:138 */     return write(new BufferedOutputStream(new FileOutputStream(file)), o);
/*  83:    */   }
/*  84:    */   
/*  85:    */   public static boolean write(OutputStream stream, Object o)
/*  86:    */     throws Exception
/*  87:    */   {
/*  88:153 */     Class<?>[] serializeArgsClasses = new Class[2];
/*  89:154 */     Object[] serializeArgs = new Object[2];
/*  90:    */     
/*  91:156 */     boolean result = false;
/*  92:    */     
/*  93:158 */     Class<?> xstreamClass = Class.forName("com.thoughtworks.xstream.XStream");
/*  94:159 */     Constructor<?> constructor = xstreamClass.getConstructor(new Class[0]);
/*  95:160 */     Object xstream = constructor.newInstance(new Object[0]);
/*  96:    */     
/*  97:162 */     serializeArgsClasses[0] = Object.class;
/*  98:163 */     serializeArgsClasses[1] = OutputStream.class;
/*  99:164 */     serializeArgs[0] = o;
/* 100:165 */     serializeArgs[1] = stream;
/* 101:166 */     Method methodSerialize = xstreamClass.getMethod("toXML", serializeArgsClasses);
/* 102:    */     try
/* 103:    */     {
/* 104:170 */       methodSerialize.invoke(xstream, serializeArgs);
/* 105:171 */       result = true;
/* 106:    */     }
/* 107:    */     catch (Exception ex)
/* 108:    */     {
/* 109:173 */       result = false;
/* 110:    */     }
/* 111:176 */     return result;
/* 112:    */   }
/* 113:    */   
/* 114:    */   public static boolean write(Writer writer, Object toSerialize)
/* 115:    */     throws Exception
/* 116:    */   {
/* 117:192 */     Class<?>[] serializeArgsClasses = new Class[2];
/* 118:193 */     Object[] serializeArgs = new Object[2];
/* 119:    */     
/* 120:195 */     boolean result = false;
/* 121:    */     
/* 122:197 */     Class<?> xstreamClass = Class.forName("com.thoughtworks.xstream.XStream");
/* 123:198 */     Constructor<?> constructor = xstreamClass.getConstructor(new Class[0]);
/* 124:199 */     Object xstream = constructor.newInstance(new Object[0]);
/* 125:    */     
/* 126:201 */     serializeArgsClasses[0] = Object.class;
/* 127:202 */     serializeArgsClasses[1] = Writer.class;
/* 128:203 */     serializeArgs[0] = toSerialize;
/* 129:204 */     serializeArgs[1] = writer;
/* 130:205 */     Method methodSerialize = xstreamClass.getMethod("toXML", serializeArgsClasses);
/* 131:    */     try
/* 132:    */     {
/* 133:209 */       methodSerialize.invoke(xstream, serializeArgs);
/* 134:210 */       result = true;
/* 135:    */     }
/* 136:    */     catch (Exception ex)
/* 137:    */     {
/* 138:212 */       result = false;
/* 139:    */     }
/* 140:215 */     return result;
/* 141:    */   }
/* 142:    */   
/* 143:    */   public static Object read(String filename)
/* 144:    */     throws Exception
/* 145:    */   {
/* 146:226 */     return read(new File(filename));
/* 147:    */   }
/* 148:    */   
/* 149:    */   public static Object read(File file)
/* 150:    */     throws Exception
/* 151:    */   {
/* 152:237 */     return read(new BufferedInputStream(new FileInputStream(file)));
/* 153:    */   }
/* 154:    */   
/* 155:    */   public static Object read(InputStream stream)
/* 156:    */     throws Exception
/* 157:    */   {
/* 158:251 */     Class<?>[] deSerializeArgsClasses = new Class[1];
/* 159:252 */     Object[] deSerializeArgs = new Object[1];
/* 160:    */     
/* 161:    */ 
/* 162:    */ 
/* 163:256 */     Class<?> xstreamClass = Class.forName("com.thoughtworks.xstream.XStream");
/* 164:257 */     Constructor<?> constructor = xstreamClass.getConstructor(new Class[0]);
/* 165:258 */     Object xstream = constructor.newInstance(new Object[0]);
/* 166:    */     
/* 167:260 */     deSerializeArgsClasses[0] = InputStream.class;
/* 168:261 */     deSerializeArgs[0] = stream;
/* 169:262 */     Method methodDeSerialize = xstreamClass.getMethod("fromXML", deSerializeArgsClasses);
/* 170:    */     Object result;
/* 171:    */     try
/* 172:    */     {
/* 173:267 */       result = methodDeSerialize.invoke(xstream, deSerializeArgs);
/* 174:    */     }
/* 175:    */     catch (Exception ex)
/* 176:    */     {
/* 177:269 */       ex.printStackTrace();
/* 178:270 */       result = null;
/* 179:    */     }
/* 180:273 */     return result;
/* 181:    */   }
/* 182:    */   
/* 183:    */   public static Object read(Reader r)
/* 184:    */     throws Exception
/* 185:    */   {
/* 186:287 */     Class<?>[] deSerializeArgsClasses = new Class[1];
/* 187:288 */     Object[] deSerializeArgs = new Object[1];
/* 188:    */     
/* 189:    */ 
/* 190:    */ 
/* 191:292 */     Class<?> xstreamClass = Class.forName("com.thoughtworks.xstream.XStream");
/* 192:293 */     Constructor<?> constructor = xstreamClass.getConstructor(new Class[0]);
/* 193:294 */     Object xstream = constructor.newInstance(new Object[0]);
/* 194:    */     
/* 195:296 */     deSerializeArgsClasses[0] = Reader.class;
/* 196:297 */     deSerializeArgs[0] = r;
/* 197:298 */     Method methodDeSerialize = xstreamClass.getMethod("fromXML", deSerializeArgsClasses);
/* 198:    */     Object result;
/* 199:    */     try
/* 200:    */     {
/* 201:303 */       result = methodDeSerialize.invoke(xstream, deSerializeArgs);
/* 202:    */     }
/* 203:    */     catch (Exception ex)
/* 204:    */     {
/* 205:305 */       ex.printStackTrace();
/* 206:306 */       result = null;
/* 207:    */     }
/* 208:309 */     return result;
/* 209:    */   }
/* 210:    */   
/* 211:    */   public static Object deSerialize(String xmlString)
/* 212:    */     throws Exception
/* 213:    */   {
/* 214:323 */     Class<?>[] deSerializeArgsClasses = new Class[1];
/* 215:324 */     Object[] deSerializeArgs = new Object[1];
/* 216:    */     
/* 217:    */ 
/* 218:    */ 
/* 219:328 */     Class<?> xstreamClass = Class.forName("com.thoughtworks.xstream.XStream");
/* 220:329 */     Constructor<?> constructor = xstreamClass.getConstructor(new Class[0]);
/* 221:330 */     Object xstream = constructor.newInstance(new Object[0]);
/* 222:    */     
/* 223:332 */     deSerializeArgsClasses[0] = String.class;
/* 224:333 */     deSerializeArgs[0] = xmlString;
/* 225:334 */     Method methodDeSerialize = xstreamClass.getMethod("fromXML", deSerializeArgsClasses);
/* 226:    */     Object result;
/* 227:    */     try
/* 228:    */     {
/* 229:339 */       result = methodDeSerialize.invoke(xstream, deSerializeArgs);
/* 230:    */     }
/* 231:    */     catch (Exception ex)
/* 232:    */     {
/* 233:341 */       ex.printStackTrace();
/* 234:342 */       result = null;
/* 235:    */     }
/* 236:345 */     return result;
/* 237:    */   }
/* 238:    */   
/* 239:    */   public String getRevision()
/* 240:    */   {
/* 241:355 */     return RevisionUtils.extract("$Revision: 10203 $");
/* 242:    */   }
/* 243:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.xml.XStream
 * JD-Core Version:    0.7.0.1
 */