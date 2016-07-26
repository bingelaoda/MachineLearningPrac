/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.beans.PropertyDescriptor;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.lang.reflect.Array;
/*   6:    */ import java.lang.reflect.Method;
/*   7:    */ import java.util.StringTokenizer;
/*   8:    */ import java.util.Vector;
/*   9:    */ 
/*  10:    */ public class PropertyPath
/*  11:    */   implements RevisionHandler
/*  12:    */ {
/*  13:    */   public static class PathElement
/*  14:    */     implements Cloneable, RevisionHandler
/*  15:    */   {
/*  16:    */     protected String m_Name;
/*  17:    */     protected int m_Index;
/*  18:    */     
/*  19:    */     public PathElement(String property)
/*  20:    */     {
/*  21: 72 */       if (property.indexOf("[") > -1)
/*  22:    */       {
/*  23: 73 */         this.m_Name = property.replaceAll("\\[.*$", "");
/*  24: 74 */         this.m_Index = Integer.parseInt(property.replaceAll(".*\\[", "").replaceAll("\\].*", ""));
/*  25:    */       }
/*  26:    */       else
/*  27:    */       {
/*  28: 78 */         this.m_Name = property;
/*  29: 79 */         this.m_Index = -1;
/*  30:    */       }
/*  31:    */     }
/*  32:    */     
/*  33:    */     public Object clone()
/*  34:    */     {
/*  35: 89 */       return new PathElement(toString());
/*  36:    */     }
/*  37:    */     
/*  38:    */     public String getName()
/*  39:    */     {
/*  40: 98 */       return this.m_Name;
/*  41:    */     }
/*  42:    */     
/*  43:    */     public boolean hasIndex()
/*  44:    */     {
/*  45:107 */       return getIndex() > -1;
/*  46:    */     }
/*  47:    */     
/*  48:    */     public int getIndex()
/*  49:    */     {
/*  50:117 */       return this.m_Index;
/*  51:    */     }
/*  52:    */     
/*  53:    */     public String toString()
/*  54:    */     {
/*  55:128 */       String result = getName();
/*  56:129 */       if (hasIndex()) {
/*  57:130 */         result = result + "[" + getIndex() + "]";
/*  58:    */       }
/*  59:132 */       return result;
/*  60:    */     }
/*  61:    */     
/*  62:    */     public String getRevision()
/*  63:    */     {
/*  64:141 */       return RevisionUtils.extract("$Revision: 8034 $");
/*  65:    */     }
/*  66:    */   }
/*  67:    */   
/*  68:    */   public static class Path
/*  69:    */     implements RevisionHandler
/*  70:    */   {
/*  71:    */     protected Vector<PropertyPath.PathElement> m_Elements;
/*  72:    */     
/*  73:    */     protected Path()
/*  74:    */     {
/*  75:163 */       this.m_Elements = new Vector();
/*  76:    */     }
/*  77:    */     
/*  78:    */     public Path(String path)
/*  79:    */     {
/*  80:172 */       this();
/*  81:    */       
/*  82:174 */       this.m_Elements = breakUp(path);
/*  83:    */     }
/*  84:    */     
/*  85:    */     public Path(Vector<PropertyPath.PathElement> elements)
/*  86:    */     {
/*  87:183 */       this();
/*  88:185 */       for (int i = 0; i < elements.size(); i++) {
/*  89:186 */         this.m_Elements.add((PropertyPath.PathElement)((PropertyPath.PathElement)elements.get(i)).clone());
/*  90:    */       }
/*  91:    */     }
/*  92:    */     
/*  93:    */     public Path(String[] elements)
/*  94:    */     {
/*  95:195 */       this();
/*  96:197 */       for (int i = 0; i < elements.length; i++) {
/*  97:198 */         this.m_Elements.add(new PropertyPath.PathElement(elements[i]));
/*  98:    */       }
/*  99:    */     }
/* 100:    */     
/* 101:    */     protected Vector<PropertyPath.PathElement> breakUp(String path)
/* 102:    */     {
/* 103:211 */       Vector<PropertyPath.PathElement> result = new Vector();
/* 104:    */       
/* 105:213 */       StringTokenizer tok = new StringTokenizer(path, ".");
/* 106:214 */       while (tok.hasMoreTokens()) {
/* 107:215 */         result.add(new PropertyPath.PathElement(tok.nextToken()));
/* 108:    */       }
/* 109:217 */       return result;
/* 110:    */     }
/* 111:    */     
/* 112:    */     public PropertyPath.PathElement get(int index)
/* 113:    */     {
/* 114:227 */       return (PropertyPath.PathElement)this.m_Elements.get(index);
/* 115:    */     }
/* 116:    */     
/* 117:    */     public int size()
/* 118:    */     {
/* 119:236 */       return this.m_Elements.size();
/* 120:    */     }
/* 121:    */     
/* 122:    */     public static Path parsePath(String path)
/* 123:    */     {
/* 124:246 */       return new Path(path);
/* 125:    */     }
/* 126:    */     
/* 127:    */     public Path subpath(int startIndex)
/* 128:    */     {
/* 129:257 */       return subpath(startIndex, size());
/* 130:    */     }
/* 131:    */     
/* 132:    */     public Path subpath(int startIndex, int endIndex)
/* 133:    */     {
/* 134:274 */       Vector<PropertyPath.PathElement> list = new Vector();
/* 135:275 */       for (int i = startIndex; i < endIndex; i++) {
/* 136:276 */         list.add(get(i));
/* 137:    */       }
/* 138:278 */       return new Path(list);
/* 139:    */     }
/* 140:    */     
/* 141:    */     public String toString()
/* 142:    */     {
/* 143:290 */       String result = "";
/* 144:292 */       for (int i = 0; i < this.m_Elements.size(); i++)
/* 145:    */       {
/* 146:293 */         if (i > 0) {
/* 147:294 */           result = result + ".";
/* 148:    */         }
/* 149:295 */         result = result + this.m_Elements.get(i);
/* 150:    */       }
/* 151:298 */       return result;
/* 152:    */     }
/* 153:    */     
/* 154:    */     public String getRevision()
/* 155:    */     {
/* 156:307 */       return RevisionUtils.extract("$Revision: 8034 $");
/* 157:    */     }
/* 158:    */   }
/* 159:    */   
/* 160:    */   protected static class PropertyContainer
/* 161:    */     implements RevisionHandler
/* 162:    */   {
/* 163:    */     protected PropertyDescriptor m_Descriptor;
/* 164:    */     protected Object m_Object;
/* 165:    */     
/* 166:    */     public PropertyContainer(PropertyDescriptor desc, Object obj)
/* 167:    */     {
/* 168:335 */       this.m_Descriptor = desc;
/* 169:336 */       this.m_Object = obj;
/* 170:    */     }
/* 171:    */     
/* 172:    */     public PropertyDescriptor getDescriptor()
/* 173:    */     {
/* 174:345 */       return this.m_Descriptor;
/* 175:    */     }
/* 176:    */     
/* 177:    */     public Object getObject()
/* 178:    */     {
/* 179:354 */       return this.m_Object;
/* 180:    */     }
/* 181:    */     
/* 182:    */     public String getRevision()
/* 183:    */     {
/* 184:363 */       return RevisionUtils.extract("$Revision: 8034 $");
/* 185:    */     }
/* 186:    */   }
/* 187:    */   
/* 188:    */   public static PropertyContainer find(Object src, Path path)
/* 189:    */   {
/* 190:384 */     PathElement part = path.get(0);
/* 191:    */     PropertyDescriptor desc;
/* 192:    */     try
/* 193:    */     {
/* 194:386 */       desc = new PropertyDescriptor(part.getName(), src.getClass());
/* 195:    */     }
/* 196:    */     catch (Exception e)
/* 197:    */     {
/* 198:389 */       desc = null;
/* 199:390 */       e.printStackTrace();
/* 200:    */     }
/* 201:394 */     if (desc == null) {
/* 202:395 */       return null;
/* 203:    */     }
/* 204:    */     PropertyContainer result;
/* 205:    */     PropertyContainer result;
/* 206:398 */     if (path.size() == 1) {
/* 207:399 */       result = new PropertyContainer(desc, src);
/* 208:    */     } else {
/* 209:    */       try
/* 210:    */       {
/* 211:404 */         Method method = desc.getReadMethod();
/* 212:405 */         Object methodResult = method.invoke(src, (Object[])null);
/* 213:    */         Object newSrc;
/* 214:    */         Object newSrc;
/* 215:406 */         if (part.hasIndex()) {
/* 216:407 */           newSrc = Array.get(methodResult, part.getIndex());
/* 217:    */         } else {
/* 218:409 */           newSrc = methodResult;
/* 219:    */         }
/* 220:410 */         result = find(newSrc, path.subpath(1));
/* 221:    */       }
/* 222:    */       catch (Exception e)
/* 223:    */       {
/* 224:413 */         result = null;
/* 225:414 */         e.printStackTrace();
/* 226:    */       }
/* 227:    */     }
/* 228:418 */     return result;
/* 229:    */   }
/* 230:    */   
/* 231:    */   public static PropertyDescriptor getPropertyDescriptor(Object src, Path path)
/* 232:    */   {
/* 233:432 */     PropertyContainer cont = find(src, path);
/* 234:434 */     if (cont == null) {
/* 235:435 */       return null;
/* 236:    */     }
/* 237:437 */     return cont.getDescriptor();
/* 238:    */   }
/* 239:    */   
/* 240:    */   public static PropertyDescriptor getPropertyDescriptor(Object src, String path)
/* 241:    */   {
/* 242:448 */     return getPropertyDescriptor(src, new Path(path));
/* 243:    */   }
/* 244:    */   
/* 245:    */   public static Object getValue(Object src, Path path)
/* 246:    */   {
/* 247:465 */     Object result = null;
/* 248:    */     
/* 249:467 */     PropertyContainer cont = find(src, path);
/* 250:469 */     if (cont == null) {
/* 251:470 */       return null;
/* 252:    */     }
/* 253:    */     try
/* 254:    */     {
/* 255:474 */       PathElement part = path.get(path.size() - 1);
/* 256:475 */       Method method = cont.getDescriptor().getReadMethod();
/* 257:476 */       Object methodResult = method.invoke(cont.getObject(), (Object[])null);
/* 258:477 */       if (part.hasIndex()) {
/* 259:478 */         result = Array.get(methodResult, part.getIndex());
/* 260:    */       } else {
/* 261:480 */         result = methodResult;
/* 262:    */       }
/* 263:    */     }
/* 264:    */     catch (Exception e)
/* 265:    */     {
/* 266:483 */       result = null;
/* 267:484 */       e.printStackTrace();
/* 268:    */     }
/* 269:487 */     return result;
/* 270:    */   }
/* 271:    */   
/* 272:    */   public static Object getValue(Object src, String path)
/* 273:    */   {
/* 274:498 */     return getValue(src, new Path(path));
/* 275:    */   }
/* 276:    */   
/* 277:    */   public static boolean setValue(Object src, Path path, Object value)
/* 278:    */   {
/* 279:517 */     boolean result = false;
/* 280:    */     
/* 281:519 */     PropertyContainer cont = find(src, path);
/* 282:521 */     if (cont == null) {
/* 283:522 */       return result;
/* 284:    */     }
/* 285:    */     try
/* 286:    */     {
/* 287:526 */       PathElement part = path.get(path.size() - 1);
/* 288:527 */       Method methodRead = cont.getDescriptor().getReadMethod();
/* 289:528 */       Method methodWrite = cont.getDescriptor().getWriteMethod();
/* 290:529 */       if (part.hasIndex())
/* 291:    */       {
/* 292:530 */         Object methodResult = methodRead.invoke(cont.getObject(), (Object[])null);
/* 293:531 */         Array.set(methodResult, part.getIndex(), value);
/* 294:532 */         methodWrite.invoke(cont.getObject(), new Object[] { methodResult });
/* 295:    */       }
/* 296:    */       else
/* 297:    */       {
/* 298:535 */         methodWrite.invoke(cont.getObject(), new Object[] { value });
/* 299:    */       }
/* 300:537 */       result = true;
/* 301:    */     }
/* 302:    */     catch (Exception e)
/* 303:    */     {
/* 304:540 */       result = false;
/* 305:541 */       e.printStackTrace();
/* 306:    */     }
/* 307:544 */     return result;
/* 308:    */   }
/* 309:    */   
/* 310:    */   public static void setValue(Object src, String path, Object value)
/* 311:    */   {
/* 312:555 */     setValue(src, new Path(path), value);
/* 313:    */   }
/* 314:    */   
/* 315:    */   public String getRevision()
/* 316:    */   {
/* 317:564 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 318:    */   }
/* 319:    */   
/* 320:    */   public static void main(String[] args)
/* 321:    */     throws Exception
/* 322:    */   {
/* 323:575 */     Path path = new Path("hello.world[2].nothing");
/* 324:576 */     System.out.println("Path: " + path);
/* 325:577 */     System.out.println(" -size: " + path.size());
/* 326:578 */     System.out.println(" -elements:");
/* 327:579 */     for (int i = 0; i < path.size(); i++) {
/* 328:580 */       System.out.println("  " + i + ". " + path.get(i).getName() + " -> " + path.get(i).getIndex());
/* 329:    */     }
/* 330:    */   }
/* 331:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.PropertyPath
 * JD-Core Version:    0.7.0.1
 */