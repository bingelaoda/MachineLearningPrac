/*   1:    */ package weka.core.xml;
/*   2:    */ 
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.io.StringReader;
/*   6:    */ import java.io.StringWriter;
/*   7:    */ import java.util.Collection;
/*   8:    */ import java.util.HashMap;
/*   9:    */ import java.util.HashSet;
/*  10:    */ import java.util.Hashtable;
/*  11:    */ import java.util.Iterator;
/*  12:    */ import java.util.LinkedHashMap;
/*  13:    */ import java.util.LinkedList;
/*  14:    */ import java.util.Map;
/*  15:    */ import java.util.Properties;
/*  16:    */ import java.util.Set;
/*  17:    */ import java.util.Stack;
/*  18:    */ import java.util.TreeMap;
/*  19:    */ import java.util.TreeSet;
/*  20:    */ import java.util.Vector;
/*  21:    */ import javax.swing.DefaultListModel;
/*  22:    */ import org.w3c.dom.Element;
/*  23:    */ import weka.classifiers.CostMatrix;
/*  24:    */ import weka.core.RevisionUtils;
/*  25:    */ import weka.core.Utils;
/*  26:    */ 
/*  27:    */ public class XMLBasicSerialization
/*  28:    */   extends XMLSerialization
/*  29:    */ {
/*  30:    */   public static final String VAL_MAPPING = "mapping";
/*  31:    */   public static final String VAL_KEY = "key";
/*  32:    */   public static final String VAL_VALUE = "value";
/*  33:    */   public static final String VAL_CELLS = "cells";
/*  34:    */   
/*  35:    */   public XMLBasicSerialization()
/*  36:    */     throws Exception
/*  37:    */   {}
/*  38:    */   
/*  39:    */   public void clear()
/*  40:    */     throws Exception
/*  41:    */   {
/*  42:107 */     super.clear();
/*  43:    */     
/*  44:    */ 
/*  45:110 */     this.m_CustomMethods.register(this, DefaultListModel.class, "DefaultListModel");
/*  46:111 */     this.m_CustomMethods.register(this, HashMap.class, "Map");
/*  47:112 */     this.m_CustomMethods.register(this, HashSet.class, "Collection");
/*  48:113 */     this.m_CustomMethods.register(this, Hashtable.class, "Map");
/*  49:114 */     this.m_CustomMethods.register(this, LinkedList.class, "Collection");
/*  50:115 */     this.m_CustomMethods.register(this, Properties.class, "Map");
/*  51:116 */     this.m_CustomMethods.register(this, Stack.class, "Collection");
/*  52:117 */     this.m_CustomMethods.register(this, TreeMap.class, "Map");
/*  53:118 */     this.m_CustomMethods.register(this, LinkedHashMap.class, "Map");
/*  54:119 */     this.m_CustomMethods.register(this, TreeSet.class, "Collection");
/*  55:120 */     this.m_CustomMethods.register(this, Vector.class, "Collection");
/*  56:121 */     this.m_CustomMethods.register(this, Color.class, "Color");
/*  57:    */     
/*  58:    */ 
/*  59:124 */     this.m_CustomMethods.register(this, weka.core.matrix.Matrix.class, "Matrix");
/*  60:125 */     this.m_CustomMethods.register(this, weka.core.Matrix.class, "MatrixOld");
/*  61:126 */     this.m_CustomMethods.register(this, CostMatrix.class, "CostMatrix");
/*  62:    */   }
/*  63:    */   
/*  64:    */   public Element writeColor(Element parent, Object o, String name)
/*  65:    */     throws Exception
/*  66:    */   {
/*  67:146 */     if (DEBUG) {
/*  68:147 */       trace(new Throwable(), name);
/*  69:    */     }
/*  70:150 */     this.m_CurrentNode = parent;
/*  71:151 */     Color c = (Color)o;
/*  72:152 */     Element node = addElement(parent, name, o.getClass().getName(), false);
/*  73:    */     
/*  74:154 */     invokeWriteToXML(node, Integer.valueOf(c.getRed()), "red");
/*  75:155 */     invokeWriteToXML(node, Integer.valueOf(c.getGreen()), "green");
/*  76:156 */     invokeWriteToXML(node, Integer.valueOf(c.getBlue()), "blue");
/*  77:    */     
/*  78:158 */     return node;
/*  79:    */   }
/*  80:    */   
/*  81:    */   public Object readColor(Element node)
/*  82:    */     throws Exception
/*  83:    */   {
/*  84:173 */     if (DEBUG) {
/*  85:174 */       trace(new Throwable(), node.getAttribute("name"));
/*  86:    */     }
/*  87:177 */     Vector<Element> children = XMLDocument.getChildTags(node);
/*  88:178 */     Element redchild = (Element)children.get(0);
/*  89:179 */     Element greenchild = (Element)children.get(1);
/*  90:180 */     Element bluechild = (Element)children.get(2);
/*  91:181 */     Integer red = (Integer)readFromXML(redchild);
/*  92:182 */     Integer green = (Integer)readFromXML(greenchild);
/*  93:183 */     Integer blue = (Integer)readFromXML(bluechild);
/*  94:    */     
/*  95:185 */     return new Color(red.intValue(), green.intValue(), blue.intValue());
/*  96:    */   }
/*  97:    */   
/*  98:    */   public Element writeDefaultListModel(Element parent, Object o, String name)
/*  99:    */     throws Exception
/* 100:    */   {
/* 101:207 */     if (DEBUG) {
/* 102:208 */       trace(new Throwable(), name);
/* 103:    */     }
/* 104:211 */     this.m_CurrentNode = parent;
/* 105:    */     
/* 106:213 */     DefaultListModel model = (DefaultListModel)o;
/* 107:214 */     Element node = addElement(parent, name, o.getClass().getName(), false);
/* 108:216 */     for (int i = 0; i < model.getSize(); i++) {
/* 109:217 */       invokeWriteToXML(node, model.get(i), Integer.toString(i));
/* 110:    */     }
/* 111:220 */     return node;
/* 112:    */   }
/* 113:    */   
/* 114:    */   public Object readDefaultListModel(Element node)
/* 115:    */     throws Exception
/* 116:    */   {
/* 117:240 */     if (DEBUG) {
/* 118:241 */       trace(new Throwable(), node.getAttribute("name"));
/* 119:    */     }
/* 120:244 */     this.m_CurrentNode = node;
/* 121:    */     
/* 122:246 */     Vector<Element> children = XMLDocument.getChildTags(node);
/* 123:247 */     DefaultListModel model = new DefaultListModel();
/* 124:    */     
/* 125:    */ 
/* 126:250 */     int index = children.size() - 1;
/* 127:251 */     for (int i = 0; i < children.size(); i++)
/* 128:    */     {
/* 129:252 */       Element child = (Element)children.get(i);
/* 130:253 */       int currIndex = Integer.parseInt(child.getAttribute("name"));
/* 131:254 */       if (currIndex > index) {
/* 132:255 */         index = currIndex;
/* 133:    */       }
/* 134:    */     }
/* 135:258 */     model.setSize(index + 1);
/* 136:261 */     for (i = 0; i < children.size(); i++)
/* 137:    */     {
/* 138:262 */       Element child = (Element)children.get(i);
/* 139:263 */       model.set(Integer.parseInt(child.getAttribute("name")), invokeReadFromXML(child));
/* 140:    */     }
/* 141:267 */     return model;
/* 142:    */   }
/* 143:    */   
/* 144:    */   public Element writeCollection(Element parent, Object o, String name)
/* 145:    */     throws Exception
/* 146:    */   {
/* 147:289 */     if (DEBUG) {
/* 148:290 */       trace(new Throwable(), name);
/* 149:    */     }
/* 150:293 */     this.m_CurrentNode = parent;
/* 151:    */     
/* 152:295 */     Iterator<?> iter = ((Collection)o).iterator();
/* 153:296 */     Element node = addElement(parent, name, o.getClass().getName(), false);
/* 154:    */     
/* 155:298 */     int i = 0;
/* 156:299 */     while (iter.hasNext())
/* 157:    */     {
/* 158:300 */       invokeWriteToXML(node, iter.next(), Integer.toString(i));
/* 159:301 */       i++;
/* 160:    */     }
/* 161:304 */     return node;
/* 162:    */   }
/* 163:    */   
/* 164:    */   public Object readCollection(Element node)
/* 165:    */     throws Exception
/* 166:    */   {
/* 167:325 */     if (DEBUG) {
/* 168:326 */       trace(new Throwable(), node.getAttribute("name"));
/* 169:    */     }
/* 170:329 */     this.m_CurrentNode = node;
/* 171:    */     
/* 172:331 */     Vector<Element> children = XMLDocument.getChildTags(node);
/* 173:332 */     Vector<Object> v = new Vector();
/* 174:    */     
/* 175:    */ 
/* 176:335 */     int index = children.size() - 1;
/* 177:336 */     for (int i = 0; i < children.size(); i++)
/* 178:    */     {
/* 179:337 */       Element child = (Element)children.get(i);
/* 180:338 */       int currIndex = Integer.parseInt(child.getAttribute("name"));
/* 181:339 */       if (currIndex > index) {
/* 182:340 */         index = currIndex;
/* 183:    */       }
/* 184:    */     }
/* 185:343 */     v.setSize(index + 1);
/* 186:346 */     for (i = 0; i < children.size(); i++)
/* 187:    */     {
/* 188:347 */       Element child = (Element)children.get(i);
/* 189:348 */       v.set(Integer.parseInt(child.getAttribute("name")), invokeReadFromXML(child));
/* 190:    */     }
/* 191:353 */     Collection<Object> coll = (Collection)Utils.cast(Class.forName(node.getAttribute("class")).newInstance());
/* 192:    */     
/* 193:355 */     coll.addAll(v);
/* 194:    */     
/* 195:357 */     return coll;
/* 196:    */   }
/* 197:    */   
/* 198:    */   public Element writeMap(Element parent, Object o, String name)
/* 199:    */     throws Exception
/* 200:    */   {
/* 201:381 */     if (DEBUG) {
/* 202:382 */       trace(new Throwable(), name);
/* 203:    */     }
/* 204:385 */     this.m_CurrentNode = parent;
/* 205:    */     
/* 206:387 */     Map<?, ?> map = (Map)o;
/* 207:388 */     Iterator<?> iter = map.keySet().iterator();
/* 208:389 */     Element node = addElement(parent, name, o.getClass().getName(), false);
/* 209:391 */     while (iter.hasNext())
/* 210:    */     {
/* 211:392 */       Object key = iter.next();
/* 212:393 */       Element child = addElement(node, "mapping", Object.class.getName(), false);
/* 213:394 */       invokeWriteToXML(child, key, "key");
/* 214:395 */       invokeWriteToXML(child, map.get(key), "value");
/* 215:    */     }
/* 216:398 */     return node;
/* 217:    */   }
/* 218:    */   
/* 219:    */   public Object readMap(Element node)
/* 220:    */     throws Exception
/* 221:    */   {
/* 222:422 */     if (DEBUG) {
/* 223:423 */       trace(new Throwable(), node.getAttribute("name"));
/* 224:    */     }
/* 225:426 */     this.m_CurrentNode = node;
/* 226:    */     
/* 227:428 */     Map<Object, Object> map = (Map)Utils.cast(Class.forName(node.getAttribute("class")).newInstance());
/* 228:429 */     Vector<Element> children = XMLDocument.getChildTags(node);
/* 229:431 */     for (int i = 0; i < children.size(); i++)
/* 230:    */     {
/* 231:432 */       Element child = (Element)children.get(i);
/* 232:433 */       Vector<Element> cchildren = XMLDocument.getChildTags(child);
/* 233:434 */       Object key = null;
/* 234:435 */       Object value = null;
/* 235:437 */       for (int n = 0; n < cchildren.size(); n++)
/* 236:    */       {
/* 237:438 */         Element cchild = (Element)cchildren.get(n);
/* 238:439 */         String name = cchild.getAttribute("name");
/* 239:440 */         if (name.equals("key")) {
/* 240:441 */           key = invokeReadFromXML(cchild);
/* 241:442 */         } else if (name.equals("value")) {
/* 242:443 */           value = invokeReadFromXML(cchild);
/* 243:    */         } else {
/* 244:445 */           System.out.println("WARNING: '" + name + "' is not a recognized name for maps!");
/* 245:    */         }
/* 246:    */       }
/* 247:450 */       map.put(key, value);
/* 248:    */     }
/* 249:453 */     return map;
/* 250:    */   }
/* 251:    */   
/* 252:    */   public Element writeCostMatrix(Element parent, Object o, String name)
/* 253:    */     throws Exception
/* 254:    */   {
/* 255:469 */     CostMatrix matrix = (CostMatrix)o;
/* 256:473 */     if (DEBUG) {
/* 257:474 */       trace(new Throwable(), name);
/* 258:    */     }
/* 259:477 */     this.m_CurrentNode = parent;
/* 260:478 */     Element node = addElement(parent, name, o.getClass().getName(), false);
/* 261:479 */     Object[][] m = new Object[matrix.size()][matrix.size()];
/* 262:480 */     for (int i = 0; i < matrix.size(); i++) {
/* 263:481 */       for (int j = 0; j < matrix.size(); j++) {
/* 264:482 */         m[i][j] = matrix.getCell(i, j);
/* 265:    */       }
/* 266:    */     }
/* 267:486 */     invokeWriteToXML(node, m, "cells");
/* 268:    */     
/* 269:488 */     return node;
/* 270:    */   }
/* 271:    */   
/* 272:    */   public Object readCostMatrix(Element node)
/* 273:    */     throws Exception
/* 274:    */   {
/* 275:508 */     if (DEBUG) {
/* 276:509 */       trace(new Throwable(), node.getAttribute("name"));
/* 277:    */     }
/* 278:512 */     this.m_CurrentNode = node;
/* 279:    */     
/* 280:514 */     CostMatrix matrix = null;
/* 281:515 */     Vector<Element> children = XMLDocument.getChildTags(node);
/* 282:517 */     for (int i = 0; i < children.size(); i++)
/* 283:    */     {
/* 284:518 */       Element child = (Element)children.get(i);
/* 285:519 */       String name = child.getAttribute("name");
/* 286:521 */       if (name.equals("cells"))
/* 287:    */       {
/* 288:522 */         Object o = invokeReadFromXML(child);
/* 289:    */         
/* 290:524 */         Object[][] m = (Object[][])o;
/* 291:525 */         matrix = new CostMatrix(m.length);
/* 292:526 */         for (int j = 0; j < matrix.size(); j++) {
/* 293:527 */           for (int k = 0; k < matrix.size(); k++) {
/* 294:528 */             matrix.setCell(j, k, m[j][k]);
/* 295:    */           }
/* 296:    */         }
/* 297:    */       }
/* 298:    */     }
/* 299:534 */     return matrix;
/* 300:    */   }
/* 301:    */   
/* 302:    */   public Element writeMatrix(Element parent, Object o, String name)
/* 303:    */     throws Exception
/* 304:    */   {
/* 305:555 */     if (DEBUG) {
/* 306:556 */       trace(new Throwable(), name);
/* 307:    */     }
/* 308:559 */     this.m_CurrentNode = parent;
/* 309:    */     
/* 310:561 */     weka.core.matrix.Matrix matrix = (weka.core.matrix.Matrix)o;
/* 311:562 */     Element node = addElement(parent, name, o.getClass().getName(), false);
/* 312:    */     
/* 313:564 */     invokeWriteToXML(node, matrix.getArray(), "cells");
/* 314:    */     
/* 315:566 */     return node;
/* 316:    */   }
/* 317:    */   
/* 318:    */   public Object readMatrix(Element node)
/* 319:    */     throws Exception
/* 320:    */   {
/* 321:586 */     if (DEBUG) {
/* 322:587 */       trace(new Throwable(), node.getAttribute("name"));
/* 323:    */     }
/* 324:590 */     this.m_CurrentNode = node;
/* 325:    */     
/* 326:592 */     weka.core.matrix.Matrix matrix = null;
/* 327:593 */     Vector<Element> children = XMLDocument.getChildTags(node);
/* 328:594 */     for (int i = 0; i < children.size(); i++)
/* 329:    */     {
/* 330:595 */       Element child = (Element)children.get(i);
/* 331:596 */       String name = child.getAttribute("name");
/* 332:598 */       if (name.equals("cells"))
/* 333:    */       {
/* 334:599 */         Object o = invokeReadFromXML(child);
/* 335:600 */         matrix = new weka.core.matrix.Matrix((double[][])o);
/* 336:    */       }
/* 337:    */     }
/* 338:604 */     return matrix;
/* 339:    */   }
/* 340:    */   
/* 341:    */   public Element writeMatrixOld(Element parent, Object o, String name)
/* 342:    */     throws Exception
/* 343:    */   {
/* 344:628 */     if (DEBUG) {
/* 345:629 */       trace(new Throwable(), name);
/* 346:    */     }
/* 347:632 */     this.m_CurrentNode = parent;
/* 348:    */     
/* 349:634 */     weka.core.Matrix matrix = (weka.core.Matrix)o;
/* 350:635 */     Element node = addElement(parent, name, o.getClass().getName(), false);
/* 351:    */     
/* 352:637 */     double[][] array = new double[matrix.numRows()][];
/* 353:638 */     for (int i = 0; i < array.length; i++) {
/* 354:639 */       array[i] = matrix.getRow(i);
/* 355:    */     }
/* 356:641 */     invokeWriteToXML(node, array, "cells");
/* 357:    */     
/* 358:643 */     return node;
/* 359:    */   }
/* 360:    */   
/* 361:    */   public Object readMatrixOld(Element node)
/* 362:    */     throws Exception
/* 363:    */   {
/* 364:660 */     if (DEBUG) {
/* 365:661 */       trace(new Throwable(), node.getAttribute("name"));
/* 366:    */     }
/* 367:664 */     this.m_CurrentNode = node;
/* 368:    */     
/* 369:666 */     weka.core.matrix.Matrix matrixNew = (weka.core.matrix.Matrix)readMatrix(node);
/* 370:667 */     weka.core.Matrix matrix = new weka.core.Matrix(matrixNew.getArrayCopy());
/* 371:    */     
/* 372:669 */     return matrix;
/* 373:    */   }
/* 374:    */   
/* 375:    */   public Element writeCostMatrixOld(Element parent, Object o, String name)
/* 376:    */     throws Exception
/* 377:    */   {
/* 378:687 */     if (DEBUG) {
/* 379:688 */       trace(new Throwable(), name);
/* 380:    */     }
/* 381:691 */     this.m_CurrentNode = parent;
/* 382:    */     
/* 383:693 */     return writeMatrixOld(parent, o, name);
/* 384:    */   }
/* 385:    */   
/* 386:    */   public Object readCostMatrixOld(Element node)
/* 387:    */     throws Exception
/* 388:    */   {
/* 389:710 */     if (DEBUG) {
/* 390:711 */       trace(new Throwable(), node.getAttribute("name"));
/* 391:    */     }
/* 392:714 */     this.m_CurrentNode = node;
/* 393:    */     
/* 394:716 */     weka.core.matrix.Matrix matrixNew = (weka.core.matrix.Matrix)readMatrix(node);
/* 395:717 */     StringWriter writer = new StringWriter();
/* 396:718 */     matrixNew.write(writer);
/* 397:719 */     CostMatrix matrix = new CostMatrix(new StringReader(writer.toString()));
/* 398:    */     
/* 399:    */ 
/* 400:722 */     return matrix;
/* 401:    */   }
/* 402:    */   
/* 403:    */   public String getRevision()
/* 404:    */   {
/* 405:732 */     return RevisionUtils.extract("$Revision: 11865 $");
/* 406:    */   }
/* 407:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.xml.XMLBasicSerialization
 * JD-Core Version:    0.7.0.1
 */