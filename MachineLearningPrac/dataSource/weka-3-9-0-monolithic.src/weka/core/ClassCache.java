/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.FileFilter;
/*   5:    */ import java.io.PrintStream;
/*   6:    */ import java.net.URI;
/*   7:    */ import java.net.URISyntaxException;
/*   8:    */ import java.net.URL;
/*   9:    */ import java.net.URLClassLoader;
/*  10:    */ import java.util.ArrayList;
/*  11:    */ import java.util.Collections;
/*  12:    */ import java.util.Enumeration;
/*  13:    */ import java.util.HashSet;
/*  14:    */ import java.util.Hashtable;
/*  15:    */ import java.util.Iterator;
/*  16:    */ import java.util.jar.Attributes;
/*  17:    */ import java.util.jar.JarEntry;
/*  18:    */ import java.util.jar.JarFile;
/*  19:    */ import java.util.jar.Manifest;
/*  20:    */ 
/*  21:    */ public class ClassCache
/*  22:    */   implements RevisionHandler
/*  23:    */ {
/*  24:    */   public static final boolean VERBOSE = false;
/*  25:    */   public static final String DEFAULT_PACKAGE = "DEFAULT";
/*  26:    */   protected Hashtable<String, HashSet<String>> m_Cache;
/*  27:    */   
/*  28:    */   public static class ClassFileFilter
/*  29:    */     implements FileFilter
/*  30:    */   {
/*  31:    */     public boolean accept(File pathname)
/*  32:    */     {
/*  33: 63 */       return pathname.getName().endsWith(".class");
/*  34:    */     }
/*  35:    */   }
/*  36:    */   
/*  37:    */   public static class DirectoryFilter
/*  38:    */     implements FileFilter
/*  39:    */   {
/*  40:    */     public boolean accept(File pathname)
/*  41:    */     {
/*  42: 83 */       return pathname.isDirectory();
/*  43:    */     }
/*  44:    */   }
/*  45:    */   
/*  46:    */   public ClassCache()
/*  47:    */   {
/*  48:111 */     initialize();
/*  49:    */   }
/*  50:    */   
/*  51:    */   protected String cleanUp(String classname)
/*  52:    */   {
/*  53:123 */     String result = classname;
/*  54:125 */     if (result.indexOf("/") > -1) {
/*  55:126 */       result = result.replace("/", ".");
/*  56:    */     }
/*  57:128 */     if (result.indexOf("\\") > -1) {
/*  58:129 */       result = result.replace("\\", ".");
/*  59:    */     }
/*  60:131 */     if (result.endsWith(".class")) {
/*  61:132 */       result = result.substring(0, result.length() - 6);
/*  62:    */     }
/*  63:135 */     return result;
/*  64:    */   }
/*  65:    */   
/*  66:    */   protected String extractPackage(String classname)
/*  67:    */   {
/*  68:145 */     if (classname.indexOf(".") > -1) {
/*  69:146 */       return classname.substring(0, classname.lastIndexOf("."));
/*  70:    */     }
/*  71:148 */     return "DEFAULT";
/*  72:    */   }
/*  73:    */   
/*  74:    */   public boolean add(String classname)
/*  75:    */   {
/*  76:164 */     classname = cleanUp(classname);
/*  77:165 */     String pkgname = extractPackage(classname);
/*  78:168 */     if (!this.m_Cache.containsKey(pkgname)) {
/*  79:169 */       this.m_Cache.put(pkgname, new HashSet());
/*  80:    */     }
/*  81:171 */     HashSet<String> names = (HashSet)this.m_Cache.get(pkgname);
/*  82:172 */     return names.add(classname);
/*  83:    */   }
/*  84:    */   
/*  85:    */   public boolean remove(String classname)
/*  86:    */   {
/*  87:185 */     classname = cleanUp(classname);
/*  88:186 */     String pkgname = extractPackage(classname);
/*  89:187 */     HashSet<String> names = (HashSet)this.m_Cache.get(pkgname);
/*  90:188 */     if (names != null) {
/*  91:189 */       return names.remove(classname);
/*  92:    */     }
/*  93:191 */     return false;
/*  94:    */   }
/*  95:    */   
/*  96:    */   protected void initFromDir(String prefix, File dir)
/*  97:    */   {
/*  98:205 */     File[] files = dir.listFiles(new ClassFileFilter());
/*  99:206 */     for (File file : files) {
/* 100:207 */       if (prefix == null) {
/* 101:208 */         add(file.getName());
/* 102:    */       } else {
/* 103:210 */         add(prefix + "." + file.getName());
/* 104:    */       }
/* 105:    */     }
/* 106:215 */     files = dir.listFiles(new DirectoryFilter());
/* 107:216 */     for (File file : files) {
/* 108:217 */       if (prefix == null) {
/* 109:218 */         initFromDir(file.getName(), file);
/* 110:    */       } else {
/* 111:220 */         initFromDir(prefix + "." + file.getName(), file);
/* 112:    */       }
/* 113:    */     }
/* 114:    */   }
/* 115:    */   
/* 116:    */   protected void initFromDir(File dir)
/* 117:    */   {
/* 118:234 */     initFromDir(null, dir);
/* 119:    */   }
/* 120:    */   
/* 121:    */   protected void initFromManifest(Manifest manifest)
/* 122:    */   {
/* 123:244 */     if (manifest == null) {
/* 124:245 */       return;
/* 125:    */     }
/* 126:252 */     Attributes atts = manifest.getMainAttributes();
/* 127:253 */     String cp = atts.getValue("Class-Path");
/* 128:254 */     if (cp == null) {
/* 129:255 */       return;
/* 130:    */     }
/* 131:258 */     String[] parts = cp.split(" ");
/* 132:259 */     for (String part : parts)
/* 133:    */     {
/* 134:260 */       if (part.trim().length() == 0) {
/* 135:261 */         return;
/* 136:    */       }
/* 137:263 */       if (part.toLowerCase().endsWith(".jar")) {
/* 138:264 */         initFromClasspathPart(part);
/* 139:    */       }
/* 140:    */     }
/* 141:    */   }
/* 142:    */   
/* 143:    */   protected void initFromJar(File file)
/* 144:    */   {
/* 145:283 */     if (!file.exists())
/* 146:    */     {
/* 147:284 */       System.out.println("Jar does not exist: " + file);
/* 148:285 */       return;
/* 149:    */     }
/* 150:    */     try
/* 151:    */     {
/* 152:289 */       JarFile jar = new JarFile(file);
/* 153:290 */       Enumeration<JarEntry> enm = jar.entries();
/* 154:291 */       while (enm.hasMoreElements())
/* 155:    */       {
/* 156:292 */         JarEntry entry = (JarEntry)enm.nextElement();
/* 157:293 */         if (entry.getName().endsWith(".class")) {
/* 158:294 */           add(entry.getName());
/* 159:    */         }
/* 160:    */       }
/* 161:297 */       initFromManifest(jar.getManifest());
/* 162:    */     }
/* 163:    */     catch (Exception e)
/* 164:    */     {
/* 165:299 */       e.printStackTrace();
/* 166:    */     }
/* 167:    */   }
/* 168:    */   
/* 169:    */   public Enumeration<String> packages()
/* 170:    */   {
/* 171:309 */     return this.m_Cache.keys();
/* 172:    */   }
/* 173:    */   
/* 174:    */   public HashSet<String> getClassnames(String pkgname)
/* 175:    */   {
/* 176:319 */     if (this.m_Cache.containsKey(pkgname)) {
/* 177:320 */       return (HashSet)this.m_Cache.get(pkgname);
/* 178:    */     }
/* 179:322 */     return new HashSet();
/* 180:    */   }
/* 181:    */   
/* 182:    */   protected void initFromClasspathPart(String part)
/* 183:    */   {
/* 184:334 */     File file = null;
/* 185:335 */     if (part.startsWith("file:"))
/* 186:    */     {
/* 187:336 */       part = part.replace(" ", "%20");
/* 188:    */       try
/* 189:    */       {
/* 190:338 */         file = new File(new URI(part));
/* 191:    */       }
/* 192:    */       catch (URISyntaxException e)
/* 193:    */       {
/* 194:340 */         System.err.println("Failed to generate URI: " + part);
/* 195:341 */         e.printStackTrace();
/* 196:    */       }
/* 197:    */     }
/* 198:    */     else
/* 199:    */     {
/* 200:344 */       file = new File(part);
/* 201:    */     }
/* 202:346 */     if (file == null)
/* 203:    */     {
/* 204:347 */       System.err.println("Skipping: " + part);
/* 205:348 */       return;
/* 206:    */     }
/* 207:352 */     if (file.isDirectory()) {
/* 208:353 */       initFromDir(file);
/* 209:354 */     } else if (file.exists()) {
/* 210:355 */       initFromJar(file);
/* 211:    */     }
/* 212:    */   }
/* 213:    */   
/* 214:    */   protected void initialize()
/* 215:    */   {
/* 216:363 */     String part = "";
/* 217:    */     
/* 218:    */ 
/* 219:    */ 
/* 220:367 */     this.m_Cache = new Hashtable();
/* 221:    */     
/* 222:369 */     URLClassLoader sysLoader = (URLClassLoader)getClass().getClassLoader();
/* 223:370 */     URL[] urls = sysLoader.getURLs();
/* 224:371 */     for (URL url : urls)
/* 225:    */     {
/* 226:372 */       part = url.toString();
/* 227:    */       
/* 228:    */ 
/* 229:    */ 
/* 230:376 */       initFromClasspathPart(part);
/* 231:    */     }
/* 232:    */   }
/* 233:    */   
/* 234:    */   public ArrayList<String> find(String matchText)
/* 235:    */   {
/* 236:392 */     ArrayList<String> result = new ArrayList();
/* 237:    */     
/* 238:394 */     Enumeration<String> packages = this.m_Cache.keys();
/* 239:395 */     while (packages.hasMoreElements())
/* 240:    */     {
/* 241:396 */       Iterator<String> names = ((HashSet)this.m_Cache.get(packages.nextElement())).iterator();
/* 242:397 */       while (names.hasNext())
/* 243:    */       {
/* 244:398 */         String name = (String)names.next();
/* 245:399 */         if (name.contains(matchText)) {
/* 246:400 */           result.add(name);
/* 247:    */         }
/* 248:    */       }
/* 249:    */     }
/* 250:405 */     if (result.size() > 1) {
/* 251:406 */       Collections.sort(result);
/* 252:    */     }
/* 253:409 */     return result;
/* 254:    */   }
/* 255:    */   
/* 256:    */   public String getRevision()
/* 257:    */   {
/* 258:419 */     return RevisionUtils.extract("$Revision: 11597 $");
/* 259:    */   }
/* 260:    */   
/* 261:    */   public static void main(String[] args)
/* 262:    */   {
/* 263:428 */     ClassCache cache = new ClassCache();
/* 264:429 */     Enumeration<String> packages = cache.packages();
/* 265:430 */     while (packages.hasMoreElements())
/* 266:    */     {
/* 267:431 */       String key = (String)packages.nextElement();
/* 268:432 */       System.out.println(key + ": " + cache.getClassnames(key).size());
/* 269:    */     }
/* 270:    */   }
/* 271:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.ClassCache
 * JD-Core Version:    0.7.0.1
 */