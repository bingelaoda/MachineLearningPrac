/*   1:    */ package weka.core.xml;
/*   2:    */ 
/*   3:    */ import java.io.BufferedReader;
/*   4:    */ import java.io.FileInputStream;
/*   5:    */ import java.io.InputStream;
/*   6:    */ import java.io.InputStreamReader;
/*   7:    */ import java.io.PrintStream;
/*   8:    */ import java.io.Reader;
/*   9:    */ import java.io.Serializable;
/*  10:    */ import java.util.ArrayList;
/*  11:    */ import java.util.Enumeration;
/*  12:    */ import java.util.Properties;
/*  13:    */ import java.util.Vector;
/*  14:    */ import java.util.zip.GZIPInputStream;
/*  15:    */ import org.w3c.dom.Document;
/*  16:    */ import org.w3c.dom.Element;
/*  17:    */ import weka.core.Attribute;
/*  18:    */ import weka.core.DenseInstance;
/*  19:    */ import weka.core.Instance;
/*  20:    */ import weka.core.Instances;
/*  21:    */ import weka.core.ProtectedProperties;
/*  22:    */ import weka.core.RevisionUtils;
/*  23:    */ import weka.core.SparseInstance;
/*  24:    */ import weka.core.Utils;
/*  25:    */ import weka.core.Version;
/*  26:    */ 
/*  27:    */ public class XMLInstances
/*  28:    */   extends XMLDocument
/*  29:    */   implements Serializable
/*  30:    */ {
/*  31:    */   private static final long serialVersionUID = 3626821327547416099L;
/*  32: 59 */   public static String FILE_EXTENSION = ".xrff";
/*  33:    */   public static final String TAG_DATASET = "dataset";
/*  34:    */   public static final String TAG_HEADER = "header";
/*  35:    */   public static final String TAG_BODY = "body";
/*  36:    */   public static final String TAG_NOTES = "notes";
/*  37:    */   public static final String TAG_ATTRIBUTES = "attributes";
/*  38:    */   public static final String TAG_ATTRIBUTE = "attribute";
/*  39:    */   public static final String TAG_LABELS = "labels";
/*  40:    */   public static final String TAG_LABEL = "label";
/*  41:    */   public static final String TAG_METADATA = "metadata";
/*  42:    */   public static final String TAG_PROPERTY = "property";
/*  43:    */   public static final String TAG_INSTANCES = "instances";
/*  44:    */   public static final String TAG_INSTANCE = "instance";
/*  45:    */   public static final String TAG_VALUE = "value";
/*  46:    */   public static final String ATT_VERSION = "version";
/*  47:    */   public static final String ATT_TYPE = "type";
/*  48:    */   public static final String ATT_FORMAT = "format";
/*  49:    */   public static final String ATT_CLASS = "class";
/*  50:    */   public static final String ATT_INDEX = "index";
/*  51:    */   public static final String ATT_WEIGHT = "weight";
/*  52:    */   public static final String ATT_MISSING = "missing";
/*  53:    */   public static final String VAL_NUMERIC = "numeric";
/*  54:    */   public static final String VAL_DATE = "date";
/*  55:    */   public static final String VAL_NOMINAL = "nominal";
/*  56:    */   public static final String VAL_STRING = "string";
/*  57:    */   public static final String VAL_RELATIONAL = "relational";
/*  58:    */   public static final String VAL_NORMAL = "normal";
/*  59:    */   public static final String VAL_SPARSE = "sparse";
/*  60:146 */   public static final String DOCTYPE = "<!DOCTYPE dataset\n[\n   <!ELEMENT dataset (header,body)>\n   <!ATTLIST dataset name CDATA #REQUIRED>\n   <!ATTLIST dataset version CDATA \"" + Version.VERSION + "\">\n" + "\n" + "   <!" + "ELEMENT" + " " + "header" + " (" + "notes" + "?" + "," + "attributes" + ")" + ">\n" + "   <!" + "ELEMENT" + " " + "body" + " (" + "instances" + ")" + ">\n" + "   <!" + "ELEMENT" + " " + "notes" + " " + "ANY" + ">   <!--  comments, information, copyright, etc. -->\n" + "\n" + "   <!" + "ELEMENT" + " " + "attributes" + " (" + "attribute" + "+" + ")" + ">\n" + "   <!" + "ELEMENT" + " " + "attribute" + " (" + "labels" + "?" + "," + "metadata" + "?" + "," + "attributes" + "?" + ")" + ">\n" + "   <!" + "ATTLIST" + " " + "attribute" + " " + "name" + " " + "CDATA" + " " + "#REQUIRED" + ">\n" + "   <!" + "ATTLIST" + " " + "attribute" + " " + "type" + " (" + "numeric" + "|" + "date" + "|" + "nominal" + "|" + "string" + "|" + "relational" + ") " + "#REQUIRED" + ">\n" + "   <!" + "ATTLIST" + " " + "attribute" + " " + "format" + " " + "CDATA" + " " + "#IMPLIED" + ">\n" + "   <!" + "ATTLIST" + " " + "attribute" + " " + "class" + " (" + "yes" + "|" + "no" + ") \"" + "no" + "\"" + ">\n" + "   <!" + "ELEMENT" + " " + "labels" + " (" + "label" + "*" + ")" + ">   <!-- only for type \"nominal\" -->\n" + "   <!" + "ELEMENT" + " " + "label" + " " + "ANY" + ">\n" + "   <!" + "ELEMENT" + " " + "metadata" + " (" + "property" + "*" + ")" + ">\n" + "   <!" + "ELEMENT" + " " + "property" + " " + "ANY" + ">\n" + "   <!" + "ATTLIST" + " " + "property" + " " + "name" + " " + "CDATA" + " " + "#REQUIRED" + ">\n" + "\n" + "   <!" + "ELEMENT" + " " + "instances" + " (" + "instance" + "*" + ")" + ">\n" + "   <!" + "ELEMENT" + " " + "instance" + " (" + "value" + "*" + ")" + ">\n" + "   <!" + "ATTLIST" + " " + "instance" + " " + "type" + " (" + "normal" + "|" + "sparse" + ") \"" + "normal" + "\"" + ">\n" + "   <!" + "ATTLIST" + " " + "instance" + " " + "weight" + " " + "CDATA" + " " + "#IMPLIED" + ">\n" + "   <!" + "ELEMENT" + " " + "value" + " (" + "#PCDATA" + "|" + "instances" + ")" + "*" + ">\n" + "   <!" + "ATTLIST" + " " + "value" + " " + "index" + " " + "CDATA" + " " + "#IMPLIED" + ">   <!-- 1-based index (only used for instance format \"sparse\") -->\n" + "   <!" + "ATTLIST" + " " + "value" + " " + "missing" + " (" + "yes" + "|" + "no" + ") \"" + "no" + "\"" + ">\n" + "]\n" + ">";
/*  61:193 */   protected int m_Precision = 6;
/*  62:    */   protected Instances m_Instances;
/*  63:    */   
/*  64:    */   public XMLInstances()
/*  65:    */     throws Exception
/*  66:    */   {
/*  67:206 */     this.m_Instances = null;
/*  68:    */     
/*  69:208 */     setDocType(DOCTYPE);
/*  70:209 */     setRootNode("dataset");
/*  71:210 */     setValidating(true);
/*  72:    */   }
/*  73:    */   
/*  74:    */   public XMLInstances(Instances data)
/*  75:    */     throws Exception
/*  76:    */   {
/*  77:220 */     this();
/*  78:    */     
/*  79:222 */     setInstances(data);
/*  80:    */   }
/*  81:    */   
/*  82:    */   public XMLInstances(Reader reader)
/*  83:    */     throws Exception
/*  84:    */   {
/*  85:232 */     this();
/*  86:    */     
/*  87:234 */     setXML(reader);
/*  88:    */   }
/*  89:    */   
/*  90:    */   protected void addAttribute(Element parent, Attribute att)
/*  91:    */   {
/*  92:252 */     Element node = this.m_Document.createElement("attribute");
/*  93:253 */     parent.appendChild(node);
/*  94:    */     
/*  95:    */ 
/*  96:    */ 
/*  97:257 */     node.setAttribute("name", validContent(att.name()));
/*  98:260 */     switch (att.type())
/*  99:    */     {
/* 100:    */     case 0: 
/* 101:262 */       node.setAttribute("type", "numeric");
/* 102:263 */       break;
/* 103:    */     case 3: 
/* 104:266 */       node.setAttribute("type", "date");
/* 105:267 */       break;
/* 106:    */     case 1: 
/* 107:270 */       node.setAttribute("type", "nominal");
/* 108:271 */       break;
/* 109:    */     case 2: 
/* 110:274 */       node.setAttribute("type", "string");
/* 111:275 */       break;
/* 112:    */     case 4: 
/* 113:278 */       node.setAttribute("type", "relational");
/* 114:279 */       break;
/* 115:    */     default: 
/* 116:282 */       node.setAttribute("type", "???");
/* 117:    */     }
/* 118:286 */     if (att.isNominal())
/* 119:    */     {
/* 120:287 */       Element child = this.m_Document.createElement("labels");
/* 121:288 */       node.appendChild(child);
/* 122:289 */       Enumeration<?> enm = att.enumerateValues();
/* 123:290 */       while (enm.hasMoreElements())
/* 124:    */       {
/* 125:291 */         String tmpStr = enm.nextElement().toString();
/* 126:292 */         Element label = this.m_Document.createElement("label");
/* 127:293 */         child.appendChild(label);
/* 128:294 */         label.appendChild(this.m_Document.createTextNode(validContent(tmpStr)));
/* 129:    */       }
/* 130:    */     }
/* 131:299 */     if (att.isDate()) {
/* 132:300 */       node.setAttribute("format", validContent(att.getDateFormat()));
/* 133:    */     }
/* 134:304 */     if ((this.m_Instances.classIndex() > -1) && 
/* 135:305 */       (att == this.m_Instances.classAttribute())) {
/* 136:306 */       node.setAttribute("class", "yes");
/* 137:    */     }
/* 138:311 */     if ((att.getMetadata() != null) && (att.getMetadata().size() > 0))
/* 139:    */     {
/* 140:312 */       Element child = this.m_Document.createElement("metadata");
/* 141:313 */       node.appendChild(child);
/* 142:314 */       Enumeration<?> enm = att.getMetadata().propertyNames();
/* 143:315 */       while (enm.hasMoreElements())
/* 144:    */       {
/* 145:316 */         String tmpStr = enm.nextElement().toString();
/* 146:317 */         Element property = this.m_Document.createElement("property");
/* 147:318 */         child.appendChild(property);
/* 148:319 */         property.setAttribute("name", validContent(tmpStr));
/* 149:320 */         property.appendChild(this.m_Document.createTextNode(validContent(att.getMetadata().getProperty(tmpStr, ""))));
/* 150:    */       }
/* 151:    */     }
/* 152:326 */     if (att.isRelationValued())
/* 153:    */     {
/* 154:327 */       Element child = this.m_Document.createElement("attributes");
/* 155:328 */       node.appendChild(child);
/* 156:329 */       for (int i = 0; i < att.relation().numAttributes(); i++) {
/* 157:330 */         addAttribute(child, att.relation().attribute(i));
/* 158:    */       }
/* 159:    */     }
/* 160:    */   }
/* 161:    */   
/* 162:    */   protected String validContent(String content)
/* 163:    */   {
/* 164:345 */     String result = content;
/* 165:    */     
/* 166:    */ 
/* 167:    */ 
/* 168:349 */     result = result.replaceAll("&", "&amp;").replaceAll("\"", "&quot;").replaceAll("'", "&apos;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
/* 169:    */     
/* 170:    */ 
/* 171:    */ 
/* 172:353 */     result = result.replaceAll("\n", "&#10;").replaceAll("\r", "&#13;").replaceAll("\t", "&#9;");
/* 173:    */     
/* 174:    */ 
/* 175:356 */     return result;
/* 176:    */   }
/* 177:    */   
/* 178:    */   protected void addInstance(Element parent, Instance inst)
/* 179:    */   {
/* 180:374 */     Element node = this.m_Document.createElement("instance");
/* 181:375 */     parent.appendChild(node);
/* 182:    */     
/* 183:    */ 
/* 184:378 */     boolean sparse = inst instanceof SparseInstance;
/* 185:379 */     if (sparse) {
/* 186:380 */       node.setAttribute("type", "sparse");
/* 187:    */     }
/* 188:384 */     if (inst.weight() != 1.0D) {
/* 189:385 */       node.setAttribute("weight", Utils.doubleToString(inst.weight(), this.m_Precision));
/* 190:    */     }
/* 191:390 */     for (int i = 0; i < inst.numValues(); i++)
/* 192:    */     {
/* 193:391 */       int index = inst.index(i);
/* 194:    */       
/* 195:393 */       Element value = this.m_Document.createElement("value");
/* 196:394 */       node.appendChild(value);
/* 197:396 */       if (inst.isMissing(index))
/* 198:    */       {
/* 199:397 */         value.setAttribute("missing", "yes");
/* 200:    */       }
/* 201:    */       else
/* 202:    */       {
/* 203:399 */         if (inst.attribute(index).isRelationValued())
/* 204:    */         {
/* 205:400 */           Element child = this.m_Document.createElement("instances");
/* 206:401 */           value.appendChild(child);
/* 207:402 */           for (int n = 0; n < inst.relationalValue(i).numInstances(); n++) {
/* 208:403 */             addInstance(child, inst.relationalValue(i).instance(n));
/* 209:    */           }
/* 210:    */         }
/* 211:406 */         if (inst.attribute(index).type() == 0) {
/* 212:407 */           value.appendChild(this.m_Document.createTextNode(Utils.doubleToString(inst.value(index), this.m_Precision)));
/* 213:    */         } else {
/* 214:410 */           value.appendChild(this.m_Document.createTextNode(validContent(inst.stringValue(index))));
/* 215:    */         }
/* 216:    */       }
/* 217:416 */       if (sparse) {
/* 218:417 */         value.setAttribute("index", "" + (index + 1));
/* 219:    */       }
/* 220:    */     }
/* 221:    */   }
/* 222:    */   
/* 223:    */   protected void headerToXML()
/* 224:    */   {
/* 225:431 */     Element root = this.m_Document.getDocumentElement();
/* 226:432 */     root.setAttribute("name", validContent(this.m_Instances.relationName()));
/* 227:433 */     root.setAttribute("version", Version.VERSION);
/* 228:    */     
/* 229:    */ 
/* 230:436 */     Element node = this.m_Document.createElement("header");
/* 231:437 */     root.appendChild(node);
/* 232:    */     
/* 233:    */ 
/* 234:440 */     Element child = this.m_Document.createElement("attributes");
/* 235:441 */     node.appendChild(child);
/* 236:442 */     for (int i = 0; i < this.m_Instances.numAttributes(); i++) {
/* 237:443 */       addAttribute(child, this.m_Instances.attribute(i));
/* 238:    */     }
/* 239:    */   }
/* 240:    */   
/* 241:    */   protected void dataToXML()
/* 242:    */   {
/* 243:456 */     Element root = this.m_Document.getDocumentElement();
/* 244:    */     
/* 245:    */ 
/* 246:459 */     Element node = this.m_Document.createElement("body");
/* 247:460 */     root.appendChild(node);
/* 248:    */     
/* 249:    */ 
/* 250:463 */     Element child = this.m_Document.createElement("instances");
/* 251:464 */     node.appendChild(child);
/* 252:465 */     for (int i = 0; i < this.m_Instances.numInstances(); i++) {
/* 253:466 */       addInstance(child, this.m_Instances.instance(i));
/* 254:    */     }
/* 255:    */   }
/* 256:    */   
/* 257:    */   public void setInstances(Instances data)
/* 258:    */   {
/* 259:476 */     this.m_Instances = new Instances(data);
/* 260:477 */     clear();
/* 261:478 */     headerToXML();
/* 262:479 */     dataToXML();
/* 263:    */   }
/* 264:    */   
/* 265:    */   public Instances getInstances()
/* 266:    */   {
/* 267:489 */     return this.m_Instances;
/* 268:    */   }
/* 269:    */   
/* 270:    */   protected ProtectedProperties createMetadata(Element parent)
/* 271:    */     throws Exception
/* 272:    */   {
/* 273:508 */     ProtectedProperties result = null;
/* 274:    */     
/* 275:    */ 
/* 276:    */ 
/* 277:512 */     Element metanode = null;
/* 278:513 */     Vector<Element> list = getChildTags(parent, "metadata");
/* 279:514 */     if (list.size() > 0) {
/* 280:515 */       metanode = (Element)list.get(0);
/* 281:    */     }
/* 282:519 */     if (metanode != null)
/* 283:    */     {
/* 284:520 */       Properties props = new Properties();
/* 285:521 */       list = getChildTags(metanode, "property");
/* 286:522 */       for (int i = 0; i < list.size(); i++)
/* 287:    */       {
/* 288:523 */         Element node = (Element)list.get(i);
/* 289:524 */         props.setProperty(node.getAttribute("name"), getContent(node));
/* 290:    */       }
/* 291:526 */       result = new ProtectedProperties(props);
/* 292:    */     }
/* 293:529 */     return result;
/* 294:    */   }
/* 295:    */   
/* 296:    */   protected ArrayList<String> createLabels(Element parent)
/* 297:    */     throws Exception
/* 298:    */   {
/* 299:547 */     ArrayList<String> result = new ArrayList();
/* 300:    */     
/* 301:    */ 
/* 302:    */ 
/* 303:551 */     Element labelsnode = null;
/* 304:552 */     Vector<Element> list = getChildTags(parent, "labels");
/* 305:553 */     if (list.size() > 0) {
/* 306:554 */       labelsnode = (Element)list.get(0);
/* 307:    */     }
/* 308:558 */     if (labelsnode != null)
/* 309:    */     {
/* 310:559 */       list = getChildTags(labelsnode, "label");
/* 311:560 */       for (int i = 0; i < list.size(); i++)
/* 312:    */       {
/* 313:561 */         Element node = (Element)list.get(i);
/* 314:562 */         result.add(getContent(node));
/* 315:    */       }
/* 316:    */     }
/* 317:566 */     return result;
/* 318:    */   }
/* 319:    */   
/* 320:    */   protected Attribute createAttribute(Element node)
/* 321:    */     throws Exception
/* 322:    */   {
/* 323:586 */     Attribute result = null;
/* 324:    */     
/* 325:    */ 
/* 326:589 */     String name = node.getAttribute("name");
/* 327:    */     
/* 328:    */ 
/* 329:592 */     String typeStr = node.getAttribute("type");
/* 330:    */     int type;
/* 331:593 */     if (typeStr.equals("numeric"))
/* 332:    */     {
/* 333:594 */       type = 0;
/* 334:    */     }
/* 335:    */     else
/* 336:    */     {
/* 337:    */       int type;
/* 338:595 */       if (typeStr.equals("date"))
/* 339:    */       {
/* 340:596 */         type = 3;
/* 341:    */       }
/* 342:    */       else
/* 343:    */       {
/* 344:    */         int type;
/* 345:597 */         if (typeStr.equals("nominal"))
/* 346:    */         {
/* 347:598 */           type = 1;
/* 348:    */         }
/* 349:    */         else
/* 350:    */         {
/* 351:    */           int type;
/* 352:599 */           if (typeStr.equals("string"))
/* 353:    */           {
/* 354:600 */             type = 2;
/* 355:    */           }
/* 356:    */           else
/* 357:    */           {
/* 358:    */             int type;
/* 359:601 */             if (typeStr.equals("relational")) {
/* 360:602 */               type = 4;
/* 361:    */             } else {
/* 362:604 */               throw new Exception("Attribute type '" + typeStr + "' is not supported!");
/* 363:    */             }
/* 364:    */           }
/* 365:    */         }
/* 366:    */       }
/* 367:    */     }
/* 368:    */     int type;
/* 369:608 */     ProtectedProperties metadata = createMetadata(node);
/* 370:610 */     switch (type)
/* 371:    */     {
/* 372:    */     case 0: 
/* 373:612 */       if (metadata == null) {
/* 374:613 */         result = new Attribute(name);
/* 375:    */       } else {
/* 376:615 */         result = new Attribute(name, metadata);
/* 377:    */       }
/* 378:617 */       break;
/* 379:    */     case 3: 
/* 380:620 */       if (metadata == null) {
/* 381:621 */         result = new Attribute(name, node.getAttribute("format"));
/* 382:    */       } else {
/* 383:623 */         result = new Attribute(name, node.getAttribute("format"), metadata);
/* 384:    */       }
/* 385:625 */       break;
/* 386:    */     case 1: 
/* 387:628 */       ArrayList<String> values = createLabels(node);
/* 388:629 */       if (metadata == null) {
/* 389:630 */         result = new Attribute(name, values);
/* 390:    */       } else {
/* 391:632 */         result = new Attribute(name, values, metadata);
/* 392:    */       }
/* 393:634 */       break;
/* 394:    */     case 2: 
/* 395:637 */       if (metadata == null) {
/* 396:638 */         result = new Attribute(name, (ArrayList)null);
/* 397:    */       } else {
/* 398:640 */         result = new Attribute(name, (ArrayList)null, metadata);
/* 399:    */       }
/* 400:642 */       break;
/* 401:    */     case 4: 
/* 402:645 */       Vector<Element> list = getChildTags(node, "attributes");
/* 403:646 */       node = (Element)list.get(0);
/* 404:647 */       ArrayList<Attribute> atts = createAttributes(node, new int[1]);
/* 405:648 */       if (metadata == null) {
/* 406:649 */         result = new Attribute(name, new Instances(name, atts, 0));
/* 407:    */       } else {
/* 408:651 */         result = new Attribute(name, new Instances(name, atts, 0), metadata);
/* 409:    */       }
/* 410:    */       break;
/* 411:    */     }
/* 412:656 */     return result;
/* 413:    */   }
/* 414:    */   
/* 415:    */   protected ArrayList<Attribute> createAttributes(Element parent, int[] classIndex)
/* 416:    */     throws Exception
/* 417:    */   {
/* 418:675 */     ArrayList<Attribute> result = new ArrayList();
/* 419:676 */     classIndex[0] = -1;
/* 420:    */     
/* 421:678 */     Vector<Element> list = getChildTags(parent, "attribute");
/* 422:679 */     for (int i = 0; i < list.size(); i++)
/* 423:    */     {
/* 424:680 */       Element node = (Element)list.get(i);
/* 425:681 */       Attribute att = createAttribute(node);
/* 426:682 */       if (node.getAttribute("class").equals("yes")) {
/* 427:683 */         classIndex[0] = i;
/* 428:    */       }
/* 429:685 */       result.add(att);
/* 430:    */     }
/* 431:688 */     return result;
/* 432:    */   }
/* 433:    */   
/* 434:    */   protected Instance createInstance(Instances header, Element parent)
/* 435:    */     throws Exception
/* 436:    */   {
/* 437:714 */     Instance result = null;
/* 438:    */     
/* 439:    */ 
/* 440:717 */     boolean sparse = parent.getAttribute("type").equals("sparse");
/* 441:718 */     double[] values = new double[header.numAttributes()];
/* 442:    */     double weight;
/* 443:    */     double weight;
/* 444:721 */     if (parent.getAttribute("weight").length() != 0) {
/* 445:722 */       weight = Double.parseDouble(parent.getAttribute("weight"));
/* 446:    */     } else {
/* 447:724 */       weight = 1.0D;
/* 448:    */     }
/* 449:727 */     Vector<Element> list = getChildTags(parent, "value");
/* 450:728 */     for (int i = 0; i < list.size(); i++)
/* 451:    */     {
/* 452:729 */       Element node = (Element)list.get(i);
/* 453:    */       int index;
/* 454:    */       int index;
/* 455:732 */       if (sparse) {
/* 456:733 */         index = Integer.parseInt(node.getAttribute("index")) - 1;
/* 457:    */       } else {
/* 458:735 */         index = i;
/* 459:    */       }
/* 460:739 */       if (node.getAttribute("missing").equals("yes"))
/* 461:    */       {
/* 462:740 */         values[index] = Utils.missingValue();
/* 463:    */       }
/* 464:    */       else
/* 465:    */       {
/* 466:742 */         String content = getContent(node);
/* 467:743 */         switch (header.attribute(index).type())
/* 468:    */         {
/* 469:    */         case 0: 
/* 470:745 */           values[index] = Double.parseDouble(content);
/* 471:746 */           break;
/* 472:    */         case 3: 
/* 473:749 */           values[index] = header.attribute(index).parseDate(content);
/* 474:750 */           break;
/* 475:    */         case 1: 
/* 476:753 */           values[index] = header.attribute(index).indexOfValue(content);
/* 477:754 */           break;
/* 478:    */         case 2: 
/* 479:757 */           values[index] = header.attribute(index).addStringValue(content);
/* 480:758 */           break;
/* 481:    */         case 4: 
/* 482:761 */           Vector<Element> subList = getChildTags(node, "instances");
/* 483:762 */           Element child = (Element)subList.get(0);
/* 484:763 */           Instances data = createInstances(header.attribute(index).relation(), child);
/* 485:764 */           values[index] = header.attribute(index).addRelation(data);
/* 486:765 */           break;
/* 487:    */         default: 
/* 488:768 */           throw new Exception("Attribute type " + header.attribute(index).type() + " is not supported!");
/* 489:    */         }
/* 490:    */       }
/* 491:    */     }
/* 492:775 */     if (sparse) {
/* 493:776 */       result = new SparseInstance(weight, values);
/* 494:    */     } else {
/* 495:778 */       result = new DenseInstance(weight, values);
/* 496:    */     }
/* 497:781 */     return result;
/* 498:    */   }
/* 499:    */   
/* 500:    */   protected Instances createInstances(Instances header, Element parent)
/* 501:    */     throws Exception
/* 502:    */   {
/* 503:798 */     Instances result = new Instances(header, 0);
/* 504:    */     
/* 505:800 */     Vector<Element> list = getChildTags(parent, "instance");
/* 506:801 */     for (int i = 0; i < list.size(); i++) {
/* 507:802 */       result.add(createInstance(result, (Element)list.get(i)));
/* 508:    */     }
/* 509:805 */     return result;
/* 510:    */   }
/* 511:    */   
/* 512:    */   protected Instances headerFromXML()
/* 513:    */     throws Exception
/* 514:    */   {
/* 515:823 */     Element root = this.m_Document.getDocumentElement();
/* 516:    */     
/* 517:    */ 
/* 518:826 */     Version version = new Version();
/* 519:827 */     if (version.isOlder(root.getAttribute("version"))) {
/* 520:828 */       System.out.println("WARNING: loading data of version " + root.getAttribute("version") + " with version " + Version.VERSION);
/* 521:    */     }
/* 522:833 */     Vector<Element> list = getChildTags(root, "header");
/* 523:834 */     Element node = (Element)list.get(0);
/* 524:835 */     list = getChildTags(node, "attributes");
/* 525:836 */     node = (Element)list.get(0);
/* 526:837 */     int[] classIndex = new int[1];
/* 527:838 */     ArrayList<Attribute> atts = createAttributes(node, classIndex);
/* 528:    */     
/* 529:    */ 
/* 530:841 */     Instances result = new Instances(root.getAttribute("name"), atts, 0);
/* 531:842 */     result.setClassIndex(classIndex[0]);
/* 532:    */     
/* 533:844 */     return result;
/* 534:    */   }
/* 535:    */   
/* 536:    */   protected Instances dataFromXML(Instances header)
/* 537:    */     throws Exception
/* 538:    */   {
/* 539:859 */     Vector<Element> list = getChildTags(this.m_Document.getDocumentElement(), "body");
/* 540:860 */     Element node = (Element)list.get(0);
/* 541:861 */     list = getChildTags(node, "instances");
/* 542:862 */     node = (Element)list.get(0);
/* 543:863 */     Instances result = createInstances(header, node);
/* 544:    */     
/* 545:865 */     return result;
/* 546:    */   }
/* 547:    */   
/* 548:    */   public void setXML(Reader reader)
/* 549:    */     throws Exception
/* 550:    */   {
/* 551:875 */     read(reader);
/* 552:    */     
/* 553:    */ 
/* 554:878 */     this.m_Instances = dataFromXML(headerFromXML());
/* 555:    */   }
/* 556:    */   
/* 557:    */   public String getRevision()
/* 558:    */   {
/* 559:888 */     return RevisionUtils.extract("$Revision: 10203 $");
/* 560:    */   }
/* 561:    */   
/* 562:    */   public static void main(String[] args)
/* 563:    */   {
/* 564:    */     try
/* 565:    */     {
/* 566:899 */       Reader r = null;
/* 567:900 */       if (args.length != 1) {
/* 568:901 */         throw new Exception("Usage: XMLInstances <filename>");
/* 569:    */       }
/* 570:903 */       InputStream in = new FileInputStream(args[0]);
/* 571:905 */       if (args[0].endsWith(".gz")) {
/* 572:906 */         in = new GZIPInputStream(in);
/* 573:    */       }
/* 574:908 */       r = new BufferedReader(new InputStreamReader(in));
/* 575:911 */       if (args[0].endsWith(".arff"))
/* 576:    */       {
/* 577:912 */         XMLInstances i = new XMLInstances(new Instances(r));
/* 578:913 */         System.out.println(i.toString());
/* 579:    */       }
/* 580:    */       else
/* 581:    */       {
/* 582:915 */         Instances i = new XMLInstances(r).getInstances();
/* 583:916 */         System.out.println(i.toSummaryString());
/* 584:    */       }
/* 585:    */     }
/* 586:    */     catch (Exception ex)
/* 587:    */     {
/* 588:919 */       ex.printStackTrace();
/* 589:920 */       System.err.println(ex.getMessage());
/* 590:    */     }
/* 591:    */   }
/* 592:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.xml.XMLInstances
 * JD-Core Version:    0.7.0.1
 */