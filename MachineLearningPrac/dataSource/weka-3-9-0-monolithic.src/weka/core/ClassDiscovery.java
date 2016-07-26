/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.lang.reflect.Modifier;
/*   6:    */ import java.net.URL;
/*   7:    */ import java.util.ArrayList;
/*   8:    */ import java.util.Collections;
/*   9:    */ import java.util.Comparator;
/*  10:    */ import java.util.Enumeration;
/*  11:    */ import java.util.HashSet;
/*  12:    */ import java.util.Hashtable;
/*  13:    */ import java.util.StringTokenizer;
/*  14:    */ import java.util.Vector;
/*  15:    */ import java.util.jar.JarEntry;
/*  16:    */ import java.util.jar.JarFile;
/*  17:    */ 
/*  18:    */ public class ClassDiscovery
/*  19:    */   implements RevisionHandler
/*  20:    */ {
/*  21:    */   public static final boolean VERBOSE = false;
/*  22:    */   protected static Hashtable<String, Vector<String>> m_Cache;
/*  23:    */   protected static ClassCache m_ClassCache;
/*  24:    */   
/*  25:    */   public static boolean isSubclass(String superclass, String otherclass)
/*  26:    */   {
/*  27:    */     try
/*  28:    */     {
/*  29: 77 */       return isSubclass(Class.forName(superclass), Class.forName(otherclass));
/*  30:    */     }
/*  31:    */     catch (Exception e) {}
/*  32: 79 */     return false;
/*  33:    */   }
/*  34:    */   
/*  35:    */   public static boolean isSubclass(Class<?> superclass, Class<?> otherclass)
/*  36:    */   {
/*  37: 95 */     boolean result = false;
/*  38: 96 */     Class<?> currentclass = otherclass;
/*  39:    */     do
/*  40:    */     {
/*  41: 98 */       result = currentclass.equals(superclass);
/*  42:101 */       if (currentclass.equals(Object.class)) {
/*  43:    */         break;
/*  44:    */       }
/*  45:105 */       if (!result) {
/*  46:106 */         currentclass = currentclass.getSuperclass();
/*  47:    */       }
/*  48:108 */     } while (!result);
/*  49:110 */     return result;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public static boolean hasInterface(String intf, String cls)
/*  53:    */   {
/*  54:    */     try
/*  55:    */     {
/*  56:122 */       return hasInterface(Class.forName(intf), Class.forName(cls));
/*  57:    */     }
/*  58:    */     catch (Exception e) {}
/*  59:124 */     return false;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public static boolean hasInterface(Class<?> intf, Class<?> cls)
/*  63:    */   {
/*  64:141 */     boolean result = false;
/*  65:142 */     Class<?> currentclass = cls;
/*  66:    */     do
/*  67:    */     {
/*  68:145 */       Class<?>[] intfs = currentclass.getInterfaces();
/*  69:146 */       for (int i = 0; i < intfs.length; i++) {
/*  70:147 */         if (intfs[i].equals(intf))
/*  71:    */         {
/*  72:148 */           result = true;
/*  73:149 */           break;
/*  74:    */         }
/*  75:    */       }
/*  76:154 */       if (!result)
/*  77:    */       {
/*  78:155 */         currentclass = currentclass.getSuperclass();
/*  79:158 */         if ((currentclass == null) || (currentclass.equals(Object.class))) {
/*  80:    */           break;
/*  81:    */         }
/*  82:    */       }
/*  83:162 */     } while (!result);
/*  84:164 */     return result;
/*  85:    */   }
/*  86:    */   
/*  87:    */   protected static URL getURL(String classpathPart, String pkgname)
/*  88:    */   {
/*  89:184 */     URL result = null;
/*  90:185 */     String urlStr = null;
/*  91:    */     try
/*  92:    */     {
/*  93:188 */       File classpathFile = new File(classpathPart);
/*  94:191 */       if (classpathFile.isDirectory())
/*  95:    */       {
/*  96:193 */         File file = new File(classpathPart + pkgname);
/*  97:194 */         if (file.exists()) {
/*  98:195 */           urlStr = "file:" + classpathPart + pkgname;
/*  99:    */         }
/* 100:    */       }
/* 101:    */       else
/* 102:    */       {
/* 103:199 */         JarFile jarfile = new JarFile(classpathPart);
/* 104:200 */         Enumeration<JarEntry> enm = jarfile.entries();
/* 105:201 */         String pkgnameTmp = pkgname.substring(1);
/* 106:202 */         while (enm.hasMoreElements()) {
/* 107:203 */           if (((JarEntry)enm.nextElement()).toString().startsWith(pkgnameTmp)) {
/* 108:204 */             urlStr = "jar:file:" + classpathPart + "!" + pkgname;
/* 109:    */           }
/* 110:    */         }
/* 111:208 */         jarfile.close();
/* 112:    */       }
/* 113:    */     }
/* 114:    */     catch (Exception e) {}
/* 115:215 */     if (urlStr != null) {
/* 116:    */       try
/* 117:    */       {
/* 118:217 */         result = new URL(urlStr);
/* 119:    */       }
/* 120:    */       catch (Exception e)
/* 121:    */       {
/* 122:219 */         System.err.println("Trying to create URL from '" + urlStr + "' generates this exception:\n" + e);
/* 123:    */         
/* 124:221 */         result = null;
/* 125:    */       }
/* 126:    */     }
/* 127:225 */     return result;
/* 128:    */   }
/* 129:    */   
/* 130:    */   public static Vector<String> find(String classname, String[] pkgnames)
/* 131:    */   {
/* 132:240 */     Vector<String> result = new Vector();
/* 133:    */     try
/* 134:    */     {
/* 135:243 */       Class<?> cls = Class.forName(classname);
/* 136:244 */       result = find(cls, pkgnames);
/* 137:    */     }
/* 138:    */     catch (Exception e)
/* 139:    */     {
/* 140:246 */       e.printStackTrace();
/* 141:    */     }
/* 142:249 */     return result;
/* 143:    */   }
/* 144:    */   
/* 145:    */   public static Vector<String> find(String classname, String pkgname)
/* 146:    */   {
/* 147:264 */     Vector<String> result = new Vector();
/* 148:    */     try
/* 149:    */     {
/* 150:267 */       Class<?> cls = Class.forName(classname);
/* 151:268 */       result = find(cls, pkgname);
/* 152:    */     }
/* 153:    */     catch (Exception e)
/* 154:    */     {
/* 155:270 */       e.printStackTrace();
/* 156:    */     }
/* 157:273 */     return result;
/* 158:    */   }
/* 159:    */   
/* 160:    */   public static Vector<String> find(Class<?> cls, String[] pkgnames)
/* 161:    */   {
/* 162:289 */     Vector<String> result = new Vector();
/* 163:    */     
/* 164:291 */     HashSet<String> names = new HashSet();
/* 165:292 */     for (int i = 0; i < pkgnames.length; i++) {
/* 166:293 */       names.addAll(find(cls, pkgnames[i]));
/* 167:    */     }
/* 168:297 */     result.addAll(names);
/* 169:298 */     Collections.sort(result, new StringCompare());
/* 170:    */     
/* 171:300 */     return result;
/* 172:    */   }
/* 173:    */   
/* 174:    */   public static ArrayList<String> find(String matchText)
/* 175:    */   {
/* 176:310 */     return m_ClassCache.find(matchText);
/* 177:    */   }
/* 178:    */   
/* 179:    */   public static Vector<String> find(Class<?> cls, String pkgname)
/* 180:    */   {
/* 181:327 */     Vector<String> result = getCache(cls, pkgname);
/* 182:329 */     if (result == null)
/* 183:    */     {
/* 184:335 */       result = new Vector();
/* 185:336 */       if (m_ClassCache.getClassnames(pkgname) != null) {
/* 186:337 */         result.addAll(m_ClassCache.getClassnames(pkgname));
/* 187:    */       }
/* 188:341 */       int i = 0;
/* 189:342 */       while (i < result.size()) {
/* 190:    */         try
/* 191:    */         {
/* 192:344 */           Class<?> clsNew = Class.forName((String)result.get(i));
/* 193:347 */           if (Modifier.isAbstract(clsNew.getModifiers()))
/* 194:    */           {
/* 195:348 */             m_ClassCache.remove((String)result.get(i));
/* 196:349 */             result.remove(i);
/* 197:    */           }
/* 198:352 */           else if ((cls.isInterface()) && (!hasInterface(cls, clsNew)))
/* 199:    */           {
/* 200:353 */             result.remove(i);
/* 201:    */           }
/* 202:356 */           else if ((!cls.isInterface()) && (!isSubclass(cls, clsNew)))
/* 203:    */           {
/* 204:357 */             result.remove(i);
/* 205:    */           }
/* 206:    */           else
/* 207:    */           {
/* 208:359 */             i++;
/* 209:    */           }
/* 210:    */         }
/* 211:    */         catch (Exception e)
/* 212:    */         {
/* 213:362 */           System.out.println("Accessing class '" + (String)result.get(i) + "' resulted in error:");
/* 214:    */           
/* 215:364 */           e.printStackTrace();
/* 216:    */         }
/* 217:    */       }
/* 218:369 */       Collections.sort(result, new StringCompare());
/* 219:    */       
/* 220:    */ 
/* 221:372 */       addCache(cls, pkgname, result);
/* 222:    */     }
/* 223:375 */     return result;
/* 224:    */   }
/* 225:    */   
/* 226:    */   protected static HashSet<String> getSubDirectories(String prefix, File dir, HashSet<String> list)
/* 227:    */   {
/* 228:    */     String newPrefix;
/* 229:    */     String newPrefix;
/* 230:393 */     if (prefix == null)
/* 231:    */     {
/* 232:394 */       newPrefix = "";
/* 233:    */     }
/* 234:    */     else
/* 235:    */     {
/* 236:    */       String newPrefix;
/* 237:395 */       if (prefix.length() == 0) {
/* 238:396 */         newPrefix = dir.getName();
/* 239:    */       } else {
/* 240:398 */         newPrefix = prefix + "." + dir.getName();
/* 241:    */       }
/* 242:    */     }
/* 243:401 */     if (newPrefix.length() != 0) {
/* 244:402 */       list.add(newPrefix);
/* 245:    */     }
/* 246:406 */     File[] files = dir.listFiles();
/* 247:407 */     if (files != null) {
/* 248:408 */       for (int i = 0; i < files.length; i++) {
/* 249:409 */         if (files[i].isDirectory()) {
/* 250:410 */           list = getSubDirectories(newPrefix, files[i], list);
/* 251:    */         }
/* 252:    */       }
/* 253:    */     }
/* 254:415 */     return list;
/* 255:    */   }
/* 256:    */   
/* 257:    */   public static Vector<String> findPackages()
/* 258:    */   {
/* 259:427 */     initCache();
/* 260:    */     
/* 261:429 */     Vector<String> result = new Vector();
/* 262:430 */     Enumeration<String> packages = m_ClassCache.packages();
/* 263:431 */     while (packages.hasMoreElements()) {
/* 264:432 */       result.add(packages.nextElement());
/* 265:    */     }
/* 266:434 */     Collections.sort(result, new StringCompare());
/* 267:    */     
/* 268:436 */     return result;
/* 269:    */   }
/* 270:    */   
/* 271:    */   protected static void initCache()
/* 272:    */   {
/* 273:443 */     if (m_Cache == null) {
/* 274:444 */       m_Cache = new Hashtable();
/* 275:    */     }
/* 276:446 */     if (m_ClassCache == null) {
/* 277:447 */       m_ClassCache = new ClassCache();
/* 278:    */     }
/* 279:    */   }
/* 280:    */   
/* 281:    */   protected static void addCache(Class<?> cls, String pkgname, Vector<String> classnames)
/* 282:    */   {
/* 283:460 */     initCache();
/* 284:461 */     m_Cache.put(cls.getName() + "-" + pkgname, classnames);
/* 285:    */   }
/* 286:    */   
/* 287:    */   protected static Vector<String> getCache(Class<?> cls, String pkgname)
/* 288:    */   {
/* 289:473 */     initCache();
/* 290:474 */     return (Vector)m_Cache.get(cls.getName() + "-" + pkgname);
/* 291:    */   }
/* 292:    */   
/* 293:    */   public static void clearCache()
/* 294:    */   {
/* 295:481 */     initCache();
/* 296:482 */     m_Cache.clear();
/* 297:    */   }
/* 298:    */   
/* 299:    */   public static void clearClassCache()
/* 300:    */   {
/* 301:490 */     clearCache();
/* 302:    */     
/* 303:492 */     m_ClassCache = new ClassCache();
/* 304:    */   }
/* 305:    */   
/* 306:    */   public String getRevision()
/* 307:    */   {
/* 308:502 */     return RevisionUtils.extract("$Revision: 10203 $");
/* 309:    */   }
/* 310:    */   
/* 311:    */   public static void main(String[] args)
/* 312:    */   {
/* 313:    */     Vector<String> list;
/* 314:    */     int i;
/* 315:524 */     if ((args.length == 1) && (args[0].equals("packages")))
/* 316:    */     {
/* 317:525 */       list = findPackages();
/* 318:526 */       for (i = 0; i < list.size();)
/* 319:    */       {
/* 320:527 */         System.out.println((String)list.get(i));i++; continue;
/* 321:529 */         if (args.length == 2)
/* 322:    */         {
/* 323:531 */           Vector<String> packages = new Vector();
/* 324:532 */           StringTokenizer tok = new StringTokenizer(args[1], ",");
/* 325:533 */           while (tok.hasMoreTokens()) {
/* 326:534 */             packages.add(tok.nextToken());
/* 327:    */           }
/* 328:538 */           Vector<String> list = find(args[0], (String[])packages.toArray(new String[packages.size()]));
/* 329:    */           
/* 330:    */ 
/* 331:    */ 
/* 332:542 */           System.out.println("Searching for '" + args[0] + "' in '" + args[1] + "':\n" + "  " + list.size() + " found.");
/* 333:544 */           for (int i = 0; i < list.size(); i++) {
/* 334:545 */             System.out.println("  " + (i + 1) + ". " + (String)list.get(i));
/* 335:    */           }
/* 336:    */         }
/* 337:548 */         System.out.println("\nUsage:");
/* 338:549 */         System.out.println(ClassDiscovery.class.getName() + " packages");
/* 339:550 */         System.out.println("\tlists all packages in the classpath");
/* 340:551 */         System.out.println(ClassDiscovery.class.getName() + " <classname> <packagename(s)>");
/* 341:    */         
/* 342:553 */         System.out.println("\tlists classes derived from/implementing 'classname' that");
/* 343:    */         
/* 344:555 */         System.out.println("\tcan be found in 'packagename(s)' (comma-separated list");
/* 345:    */         
/* 346:557 */         System.out.println();
/* 347:558 */         System.exit(1);
/* 348:    */       }
/* 349:    */     }
/* 350:    */   }
/* 351:    */   
/* 352:    */   public static class StringCompare
/* 353:    */     implements Comparator<String>, RevisionHandler
/* 354:    */   {
/* 355:    */     private String fillUp(String s, int len)
/* 356:    */     {
/* 357:582 */       while (s.length() < len) {
/* 358:583 */         s = s + " ";
/* 359:    */       }
/* 360:585 */       return s;
/* 361:    */     }
/* 362:    */     
/* 363:    */     private int charGroup(char c)
/* 364:    */     {
/* 365:597 */       int result = 0;
/* 366:599 */       if ((c >= 'a') && (c <= 'z')) {
/* 367:600 */         result = 2;
/* 368:601 */       } else if ((c >= '0') && (c <= '9')) {
/* 369:602 */         result = 1;
/* 370:    */       }
/* 371:605 */       return result;
/* 372:    */     }
/* 373:    */     
/* 374:    */     public int compare(String o1, String o2)
/* 375:    */     {
/* 376:624 */       int result = 0;
/* 377:    */       
/* 378:    */ 
/* 379:627 */       String s1 = o1.toString().toLowerCase();
/* 380:628 */       String s2 = o2.toString().toLowerCase();
/* 381:    */       
/* 382:    */ 
/* 383:631 */       s1 = fillUp(s1, s2.length());
/* 384:632 */       s2 = fillUp(s2, s1.length());
/* 385:634 */       for (int i = 0; i < s1.length(); i++) {
/* 386:636 */         if (s1.charAt(i) == s2.charAt(i))
/* 387:    */         {
/* 388:637 */           result = 0;
/* 389:    */         }
/* 390:    */         else
/* 391:    */         {
/* 392:639 */           int v1 = charGroup(s1.charAt(i));
/* 393:640 */           int v2 = charGroup(s2.charAt(i));
/* 394:643 */           if (v1 != v2)
/* 395:    */           {
/* 396:644 */             if (v1 < v2)
/* 397:    */             {
/* 398:645 */               result = -1; break;
/* 399:    */             }
/* 400:647 */             result = 1; break;
/* 401:    */           }
/* 402:650 */           if (s1.charAt(i) < s2.charAt(i))
/* 403:    */           {
/* 404:651 */             result = -1; break;
/* 405:    */           }
/* 406:653 */           result = 1;
/* 407:    */           
/* 408:    */ 
/* 409:    */ 
/* 410:657 */           break;
/* 411:    */         }
/* 412:    */       }
/* 413:661 */       return result;
/* 414:    */     }
/* 415:    */     
/* 416:    */     public boolean equals(Object obj)
/* 417:    */     {
/* 418:672 */       return obj instanceof StringCompare;
/* 419:    */     }
/* 420:    */     
/* 421:    */     public String getRevision()
/* 422:    */     {
/* 423:682 */       return RevisionUtils.extract("$Revision: 10203 $");
/* 424:    */     }
/* 425:    */   }
/* 426:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.ClassDiscovery
 * JD-Core Version:    0.7.0.1
 */