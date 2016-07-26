/*   1:    */ package jsyntaxpane.util;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.IOException;
/*   5:    */ import java.lang.reflect.Constructor;
/*   6:    */ import java.lang.reflect.Field;
/*   7:    */ import java.lang.reflect.InvocationTargetException;
/*   8:    */ import java.lang.reflect.Member;
/*   9:    */ import java.lang.reflect.Method;
/*  10:    */ import java.lang.reflect.Modifier;
/*  11:    */ import java.net.URL;
/*  12:    */ import java.util.ArrayList;
/*  13:    */ import java.util.Enumeration;
/*  14:    */ import java.util.HashMap;
/*  15:    */ import java.util.List;
/*  16:    */ import java.util.logging.Level;
/*  17:    */ import java.util.logging.Logger;
/*  18:    */ 
/*  19:    */ public class ReflectUtils
/*  20:    */ {
/*  21:    */   public static final List<String> DEFAULT_PACKAGES;
/*  22:    */   
/*  23:    */   public static int addMethods(Class aClass, List<Member> list)
/*  24:    */   {
/*  25: 45 */     Method[] methods = aClass.getMethods();
/*  26: 46 */     for (Method m : methods) {
/*  27: 47 */       list.add(m);
/*  28:    */     }
/*  29: 49 */     return methods.length;
/*  30:    */   }
/*  31:    */   
/*  32:    */   public static int addStaticMethods(Class aClass, List<Member> list)
/*  33:    */   {
/*  34: 59 */     Method[] methods = aClass.getMethods();
/*  35: 60 */     for (Method m : methods) {
/*  36: 61 */       if (Modifier.isStatic(m.getModifiers())) {
/*  37: 62 */         list.add(m);
/*  38:    */       }
/*  39:    */     }
/*  40: 65 */     return methods.length;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public static int addStaticFields(Class aClass, List<Member> list)
/*  44:    */   {
/*  45: 75 */     Field[] fields = aClass.getFields();
/*  46: 76 */     for (Field f : fields) {
/*  47: 77 */       if (Modifier.isStatic(f.getModifiers())) {
/*  48: 78 */         list.add(f);
/*  49:    */       }
/*  50:    */     }
/*  51: 81 */     return fields.length;
/*  52:    */   }
/*  53:    */   
/*  54:    */   public static int addFields(Class aClass, List<Member> list)
/*  55:    */   {
/*  56: 91 */     Field[] fields = aClass.getFields();
/*  57: 92 */     for (Field f : fields) {
/*  58: 93 */       list.add(f);
/*  59:    */     }
/*  60: 95 */     return fields.length;
/*  61:    */   }
/*  62:    */   
/*  63:    */   public static int addConstrcutors(Class aClass, List<Member> list)
/*  64:    */   {
/*  65:105 */     Constructor[] constructors = aClass.getConstructors();
/*  66:106 */     for (Constructor c : constructors) {
/*  67:107 */       list.add(c);
/*  68:    */     }
/*  69:109 */     return constructors.length;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public static String getJavaCallString(Constructor c)
/*  73:    */   {
/*  74:119 */     StringBuilder call = new StringBuilder();
/*  75:120 */     call.append(c.getDeclaringClass().getSimpleName());
/*  76:121 */     addParamsString(call, c.getParameterTypes());
/*  77:122 */     return call.toString();
/*  78:    */   }
/*  79:    */   
/*  80:    */   public static String getJavaCallString(Method method)
/*  81:    */   {
/*  82:132 */     StringBuilder call = new StringBuilder();
/*  83:133 */     call.append(method.getName());
/*  84:134 */     addParamsString(call, method.getParameterTypes());
/*  85:135 */     return call.toString();
/*  86:    */   }
/*  87:    */   
/*  88:    */   public static StringBuilder addParamsString(StringBuilder call, Class[] params)
/*  89:    */   {
/*  90:146 */     call.append("(");
/*  91:147 */     boolean firstArg = true;
/*  92:148 */     for (Class arg : params)
/*  93:    */     {
/*  94:149 */       if (firstArg) {
/*  95:150 */         firstArg = false;
/*  96:    */       } else {
/*  97:152 */         call.append(", ");
/*  98:    */       }
/*  99:154 */       call.append(arg.getSimpleName());
/* 100:    */     }
/* 101:156 */     call.append(")");
/* 102:157 */     return call;
/* 103:    */   }
/* 104:    */   
/* 105:    */   public static String[] getMethodCalls(Class aClass)
/* 106:    */   {
/* 107:166 */     String[] methods = new String[aClass.getMethods().length];
/* 108:167 */     int i = 0;
/* 109:168 */     for (Method method : aClass.getMethods()) {
/* 110:169 */       methods[(i++)] = getJavaCallString(method);
/* 111:    */     }
/* 112:171 */     return methods;
/* 113:    */   }
/* 114:    */   
/* 115:    */   public static String[] getConstructorCalls(Class aClass)
/* 116:    */   {
/* 117:180 */     Constructor[] constructors = aClass.getConstructors();
/* 118:181 */     String[] cons = new String[constructors.length];
/* 119:182 */     int i = 0;
/* 120:183 */     for (Constructor c : constructors) {
/* 121:184 */       cons[(i++)] = getJavaCallString(c);
/* 122:    */     }
/* 123:186 */     return cons;
/* 124:    */   }
/* 125:    */   
/* 126:    */   public static String getParamsString(Class[] params)
/* 127:    */   {
/* 128:196 */     StringBuilder sb = new StringBuilder();
/* 129:197 */     addParamsString(sb, params);
/* 130:198 */     return sb.toString();
/* 131:    */   }
/* 132:    */   
/* 133:    */   private static Class[] getClasses(String packageName)
/* 134:    */     throws ClassNotFoundException, IOException
/* 135:    */   {
/* 136:212 */     ClassLoader classLoader = ClassLoader.getSystemClassLoader();
/* 137:213 */     assert (classLoader != null);
/* 138:214 */     String path = packageName.replace('.', '/');
/* 139:215 */     Enumeration<URL> resources = classLoader.getResources(path);
/* 140:216 */     List<File> dirs = new ArrayList();
/* 141:217 */     while (resources.hasMoreElements())
/* 142:    */     {
/* 143:218 */       URL resource = (URL)resources.nextElement();
/* 144:219 */       dirs.add(new File(resource.getFile()));
/* 145:    */     }
/* 146:221 */     ArrayList<Class> classes = new ArrayList();
/* 147:222 */     for (File directory : dirs) {
/* 148:223 */       classes.addAll(findClasses(directory, packageName));
/* 149:    */     }
/* 150:225 */     return (Class[])classes.toArray(new Class[classes.size()]);
/* 151:    */   }
/* 152:    */   
/* 153:    */   private static List<Class> findClasses(File directory, String packageName)
/* 154:    */     throws ClassNotFoundException
/* 155:    */   {
/* 156:237 */     List<Class> classes = new ArrayList();
/* 157:238 */     if (!directory.exists()) {
/* 158:239 */       return classes;
/* 159:    */     }
/* 160:241 */     File[] files = directory.listFiles();
/* 161:242 */     for (File file : files) {
/* 162:243 */       if (file.isDirectory())
/* 163:    */       {
/* 164:244 */         assert (!file.getName().contains("."));
/* 165:245 */         classes.addAll(findClasses(file, packageName + "." + file.getName()));
/* 166:    */       }
/* 167:246 */       else if (file.getName().endsWith(".class"))
/* 168:    */       {
/* 169:247 */         classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
/* 170:    */       }
/* 171:    */     }
/* 172:250 */     return classes;
/* 173:    */   }
/* 174:    */   
/* 175:    */   public static Class findClass(String className, List<String> packages)
/* 176:    */   {
/* 177:    */     try
/* 178:    */     {
/* 179:262 */       return Class.forName(className);
/* 180:    */     }
/* 181:    */     catch (ClassNotFoundException ex) {}catch (NoClassDefFoundError ex) {}
/* 182:266 */     for (String pack : packages) {
/* 183:    */       try
/* 184:    */       {
/* 185:268 */         return Class.forName(pack + "." + className);
/* 186:    */       }
/* 187:    */       catch (ClassNotFoundException ex) {}catch (NoClassDefFoundError ex) {}
/* 188:    */     }
/* 189:273 */     return null;
/* 190:    */   }
/* 191:    */   
/* 192:    */   public static boolean callSetter(Object obj, String property, Object value)
/* 193:    */   {
/* 194:294 */     String key = String.format("%s.%s(%s)", new Object[] { obj.getClass().getName(), property, value.getClass().getName() });
/* 195:    */     
/* 196:296 */     Method m = null;
/* 197:297 */     boolean result = false;
/* 198:298 */     if (!SETTERS_MAP.containsKey(key))
/* 199:    */     {
/* 200:299 */       m = findMethod(obj, property, value);
/* 201:300 */       SETTERS_MAP.put(key, m);
/* 202:    */     }
/* 203:    */     else
/* 204:    */     {
/* 205:302 */       m = (Method)SETTERS_MAP.get(key);
/* 206:    */     }
/* 207:304 */     if (m != null) {
/* 208:    */       try
/* 209:    */       {
/* 210:306 */         m.invoke(obj, new Object[] { value });
/* 211:307 */         result = true;
/* 212:    */       }
/* 213:    */       catch (IllegalAccessException ex)
/* 214:    */       {
/* 215:309 */         Logger.getLogger(ReflectUtils.class.getName()).log(Level.SEVERE, null, ex);
/* 216:    */       }
/* 217:    */       catch (IllegalArgumentException ex)
/* 218:    */       {
/* 219:311 */         Logger.getLogger(ReflectUtils.class.getName()).log(Level.SEVERE, null, ex);
/* 220:    */       }
/* 221:    */       catch (InvocationTargetException ex)
/* 222:    */       {
/* 223:313 */         Logger.getLogger(ReflectUtils.class.getName()).log(Level.SEVERE, null, ex);
/* 224:    */       }
/* 225:    */     }
/* 226:316 */     return result;
/* 227:    */   }
/* 228:    */   
/* 229:    */   private static synchronized Method findMethod(Object obj, String property, Object value)
/* 230:    */   {
/* 231:321 */     Method m = null;
/* 232:322 */     Class<?> theClass = obj.getClass();
/* 233:323 */     String setter = String.format("set%C%s", new Object[] { Character.valueOf(property.charAt(0)), property.substring(1) });
/* 234:    */     
/* 235:325 */     Class paramType = value.getClass();
/* 236:326 */     while (paramType != null) {
/* 237:    */       try
/* 238:    */       {
/* 239:328 */         return theClass.getMethod(setter, new Class[] { paramType });
/* 240:    */       }
/* 241:    */       catch (NoSuchMethodException ex)
/* 242:    */       {
/* 243:332 */         for (Class iface : paramType.getInterfaces()) {
/* 244:    */           try
/* 245:    */           {
/* 246:334 */             return theClass.getMethod(setter, new Class[] { iface });
/* 247:    */           }
/* 248:    */           catch (NoSuchMethodException ex1) {}
/* 249:    */         }
/* 250:339 */         paramType = paramType.getSuperclass();
/* 251:    */       }
/* 252:    */     }
/* 253:342 */     return m;
/* 254:    */   }
/* 255:    */   
/* 256:    */   static
/* 257:    */   {
/* 258:344 */     DEFAULT_PACKAGES = new ArrayList(3);
/* 259:    */     
/* 260:    */ 
/* 261:347 */     DEFAULT_PACKAGES.add("java.lang");
/* 262:348 */     DEFAULT_PACKAGES.add("java.util");
/* 263:349 */     DEFAULT_PACKAGES.add("jsyntaxpane");
/* 264:    */   }
/* 265:    */   
/* 266:361 */   private static HashMap<String, Method> SETTERS_MAP = new HashMap();
/* 267:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.util.ReflectUtils
 * JD-Core Version:    0.7.0.1
 */