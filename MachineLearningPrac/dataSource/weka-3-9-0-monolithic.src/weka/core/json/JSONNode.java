/*   1:    */ package weka.core.json;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Container;
/*   5:    */ import java.io.PrintStream;
/*   6:    */ import java.io.Reader;
/*   7:    */ import java_cup.runtime.DefaultSymbolFactory;
/*   8:    */ import java_cup.runtime.SymbolFactory;
/*   9:    */ import javax.swing.JFrame;
/*  10:    */ import javax.swing.JScrollPane;
/*  11:    */ import javax.swing.JTree;
/*  12:    */ import javax.swing.tree.DefaultMutableTreeNode;
/*  13:    */ 
/*  14:    */ public class JSONNode
/*  15:    */   extends DefaultMutableTreeNode
/*  16:    */ {
/*  17:    */   private static final long serialVersionUID = -3047440914507883491L;
/*  18:    */   protected String m_Name;
/*  19:    */   protected Object m_Value;
/*  20:    */   protected NodeType m_NodeType;
/*  21:    */   
/*  22:    */   public static enum NodeType
/*  23:    */   {
/*  24: 53 */     PRIMITIVE,  OBJECT,  ARRAY;
/*  25:    */     
/*  26:    */     private NodeType() {}
/*  27:    */   }
/*  28:    */   
/*  29:    */   public JSONNode()
/*  30:    */   {
/*  31: 71 */     this(null, NodeType.OBJECT);
/*  32:    */   }
/*  33:    */   
/*  34:    */   public JSONNode(String name, Boolean value)
/*  35:    */   {
/*  36: 81 */     this(name, value, NodeType.PRIMITIVE);
/*  37:    */   }
/*  38:    */   
/*  39:    */   public JSONNode(String name, Integer value)
/*  40:    */   {
/*  41: 91 */     this(name, value, NodeType.PRIMITIVE);
/*  42:    */   }
/*  43:    */   
/*  44:    */   public JSONNode(String name, Double value)
/*  45:    */   {
/*  46:101 */     this(name, value, NodeType.PRIMITIVE);
/*  47:    */   }
/*  48:    */   
/*  49:    */   public JSONNode(String name, String value)
/*  50:    */   {
/*  51:111 */     this(name, value, NodeType.PRIMITIVE);
/*  52:    */   }
/*  53:    */   
/*  54:    */   protected JSONNode(String name, NodeType type)
/*  55:    */   {
/*  56:121 */     this(name, null, type);
/*  57:    */   }
/*  58:    */   
/*  59:    */   protected JSONNode(String name, Object value, NodeType type)
/*  60:    */   {
/*  61:134 */     this.m_Name = name;
/*  62:135 */     this.m_Value = value;
/*  63:136 */     this.m_NodeType = type;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public boolean isAnonymous()
/*  67:    */   {
/*  68:145 */     return this.m_Name == null;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public String getName()
/*  72:    */   {
/*  73:154 */     return this.m_Name;
/*  74:    */   }
/*  75:    */   
/*  76:    */   public Object getValue()
/*  77:    */   {
/*  78:163 */     return getValue(null);
/*  79:    */   }
/*  80:    */   
/*  81:    */   public Object getValue(Object defValue)
/*  82:    */   {
/*  83:173 */     if (this.m_Value == null) {
/*  84:174 */       return defValue;
/*  85:    */     }
/*  86:176 */     if ((this.m_Value instanceof String)) {
/*  87:177 */       return unescape(this.m_Value.toString());
/*  88:    */     }
/*  89:179 */     return this.m_Value;
/*  90:    */   }
/*  91:    */   
/*  92:    */   public boolean isPrimitive()
/*  93:    */   {
/*  94:190 */     return this.m_NodeType == NodeType.PRIMITIVE;
/*  95:    */   }
/*  96:    */   
/*  97:    */   public boolean isArray()
/*  98:    */   {
/*  99:199 */     return this.m_NodeType == NodeType.ARRAY;
/* 100:    */   }
/* 101:    */   
/* 102:    */   public boolean isObject()
/* 103:    */   {
/* 104:208 */     return this.m_NodeType == NodeType.OBJECT;
/* 105:    */   }
/* 106:    */   
/* 107:    */   public NodeType getNodeType()
/* 108:    */   {
/* 109:217 */     return this.m_NodeType;
/* 110:    */   }
/* 111:    */   
/* 112:    */   public JSONNode addNull(String name)
/* 113:    */   {
/* 114:227 */     return add(name, null, NodeType.PRIMITIVE);
/* 115:    */   }
/* 116:    */   
/* 117:    */   public JSONNode addPrimitive(String name, Boolean value)
/* 118:    */   {
/* 119:238 */     return add(name, value, NodeType.PRIMITIVE);
/* 120:    */   }
/* 121:    */   
/* 122:    */   public JSONNode addPrimitive(String name, Integer value)
/* 123:    */   {
/* 124:249 */     return add(name, value, NodeType.PRIMITIVE);
/* 125:    */   }
/* 126:    */   
/* 127:    */   public JSONNode addPrimitive(String name, Double value)
/* 128:    */   {
/* 129:260 */     return add(name, value, NodeType.PRIMITIVE);
/* 130:    */   }
/* 131:    */   
/* 132:    */   public JSONNode addPrimitive(String name, String value)
/* 133:    */   {
/* 134:271 */     return add(name, value, NodeType.PRIMITIVE);
/* 135:    */   }
/* 136:    */   
/* 137:    */   public JSONNode addArray(String name)
/* 138:    */   {
/* 139:281 */     return add(name, null, NodeType.ARRAY);
/* 140:    */   }
/* 141:    */   
/* 142:    */   public JSONNode addNullArrayElement()
/* 143:    */   {
/* 144:290 */     return add(null, null, NodeType.PRIMITIVE);
/* 145:    */   }
/* 146:    */   
/* 147:    */   public JSONNode addObjectArrayElement()
/* 148:    */   {
/* 149:299 */     return add(null, null, NodeType.OBJECT);
/* 150:    */   }
/* 151:    */   
/* 152:    */   public JSONNode addArrayElement(Object value)
/* 153:    */   {
/* 154:311 */     if (getNodeType() != NodeType.ARRAY) {
/* 155:312 */       return null;
/* 156:    */     }
/* 157:315 */     NodeType type = null;
/* 158:317 */     if (value != null) {
/* 159:318 */       if ((value instanceof Boolean)) {
/* 160:319 */         type = NodeType.PRIMITIVE;
/* 161:320 */       } else if ((value instanceof Integer)) {
/* 162:321 */         type = NodeType.PRIMITIVE;
/* 163:322 */       } else if ((value instanceof Double)) {
/* 164:323 */         type = NodeType.PRIMITIVE;
/* 165:324 */       } else if ((value instanceof String)) {
/* 166:325 */         type = NodeType.PRIMITIVE;
/* 167:326 */       } else if (value.getClass().isArray()) {
/* 168:327 */         type = NodeType.ARRAY;
/* 169:    */       } else {
/* 170:329 */         type = NodeType.OBJECT;
/* 171:    */       }
/* 172:    */     }
/* 173:333 */     return add(null, value, type);
/* 174:    */   }
/* 175:    */   
/* 176:    */   public JSONNode addObject(String name)
/* 177:    */   {
/* 178:343 */     return add(name, null, NodeType.OBJECT);
/* 179:    */   }
/* 180:    */   
/* 181:    */   protected JSONNode add(String name, Object value, NodeType type)
/* 182:    */   {
/* 183:357 */     if (isPrimitive()) {
/* 184:358 */       return null;
/* 185:    */     }
/* 186:361 */     JSONNode child = new JSONNode(name, value, type);
/* 187:362 */     add(child);
/* 188:    */     
/* 189:364 */     return child;
/* 190:    */   }
/* 191:    */   
/* 192:    */   public boolean hasChild(String name)
/* 193:    */   {
/* 194:374 */     return getChild(name) != null;
/* 195:    */   }
/* 196:    */   
/* 197:    */   public JSONNode getChild(String name)
/* 198:    */   {
/* 199:388 */     JSONNode result = null;
/* 200:390 */     for (int i = 0; i < getChildCount(); i++)
/* 201:    */     {
/* 202:391 */       JSONNode node = (JSONNode)getChildAt(i);
/* 203:392 */       if ((!node.isAnonymous()) && (node.getName().equals(name)))
/* 204:    */       {
/* 205:393 */         result = node;
/* 206:394 */         break;
/* 207:    */       }
/* 208:    */     }
/* 209:398 */     return result;
/* 210:    */   }
/* 211:    */   
/* 212:    */   protected String getIndentation(int level)
/* 213:    */   {
/* 214:411 */     StringBuffer result = new StringBuffer();
/* 215:412 */     for (int i = 0; i < level; i++) {
/* 216:413 */       result.append("\t");
/* 217:    */     }
/* 218:416 */     return result.toString();
/* 219:    */   }
/* 220:    */   
/* 221:    */   protected Object escape(Object o)
/* 222:    */   {
/* 223:426 */     if ((o instanceof String)) {
/* 224:427 */       return escape((String)o);
/* 225:    */     }
/* 226:429 */     return o;
/* 227:    */   }
/* 228:    */   
/* 229:    */   protected String escape(String s)
/* 230:    */   {
/* 231:448 */     s = s.replace("\\n", "@@-@@n").replace("\\r", "@@-@@r").replace("\\t", "@@-@@t").replace("\\b", "@@-@@b").replace("\\f", "@@-@@f");
/* 232:    */     StringBuffer result;
/* 233:    */     int i;
/* 234:452 */     if ((s.indexOf('"') > -1) || (s.indexOf('\\') > -1) || (s.indexOf('\b') > -1) || (s.indexOf('\f') > -1) || (s.indexOf('\n') > -1) || (s.indexOf('\r') > -1) || (s.indexOf('\t') > -1))
/* 235:    */     {
/* 236:456 */       result = new StringBuffer();
/* 237:457 */       for (i = 0; i < s.length();)
/* 238:    */       {
/* 239:458 */         char c = s.charAt(i);
/* 240:459 */         if (c == '"') {
/* 241:460 */           result.append("\\\"");
/* 242:461 */         } else if (c == '\\') {
/* 243:462 */           result.append("\\\\");
/* 244:463 */         } else if (c == '\b') {
/* 245:464 */           result.append("\\b");
/* 246:465 */         } else if (c == '\f') {
/* 247:466 */           result.append("\\f");
/* 248:467 */         } else if (c == '\n') {
/* 249:468 */           result.append("\\n");
/* 250:469 */         } else if (c == '\r') {
/* 251:470 */           result.append("\\r");
/* 252:471 */         } else if (c == '\t') {
/* 253:472 */           result.append("\\t");
/* 254:    */         } else {
/* 255:474 */           result.append(c);
/* 256:    */         }
/* 257:457 */         i++; continue;
/* 258:    */         
/* 259:    */ 
/* 260:    */ 
/* 261:    */ 
/* 262:    */ 
/* 263:    */ 
/* 264:    */ 
/* 265:    */ 
/* 266:    */ 
/* 267:    */ 
/* 268:    */ 
/* 269:    */ 
/* 270:    */ 
/* 271:    */ 
/* 272:    */ 
/* 273:    */ 
/* 274:    */ 
/* 275:    */ 
/* 276:    */ 
/* 277:    */ 
/* 278:478 */         result = new StringBuffer(s);
/* 279:    */       }
/* 280:    */     }
/* 281:    */     StringBuffer result;
/* 282:481 */     return result.toString();
/* 283:    */   }
/* 284:    */   
/* 285:    */   protected String unescape(String s)
/* 286:    */   {
/* 287:495 */     String[] charsFind = { "\\\\", "\\'", "\\t", "\\n", "\\r", "\\b", "\\f", "\\\"", "\\%", "\\u001E" };
/* 288:    */     
/* 289:497 */     char[] charsReplace = { '\\', '\'', '\t', '\n', '\r', '\b', '\f', '"', '%', '\036' };
/* 290:    */     
/* 291:499 */     int[] pos = new int[charsFind.length];
/* 292:    */     
/* 293:    */ 
/* 294:502 */     String str = new String(s);
/* 295:503 */     StringBuilder newStringBuffer = new StringBuilder();
/* 296:504 */     while (str.length() > 0)
/* 297:    */     {
/* 298:506 */       int curPos = str.length();
/* 299:507 */       int index = -1;
/* 300:508 */       for (int i = 0; i < pos.length; i++)
/* 301:    */       {
/* 302:509 */         pos[i] = str.indexOf(charsFind[i]);
/* 303:510 */         if ((pos[i] > -1) && (pos[i] < curPos))
/* 304:    */         {
/* 305:511 */           index = i;
/* 306:512 */           curPos = pos[i];
/* 307:    */         }
/* 308:    */       }
/* 309:517 */       if (index == -1)
/* 310:    */       {
/* 311:518 */         newStringBuffer.append(str);
/* 312:519 */         str = "";
/* 313:    */       }
/* 314:    */       else
/* 315:    */       {
/* 316:521 */         newStringBuffer.append(str.substring(0, pos[index]));
/* 317:522 */         newStringBuffer.append(charsReplace[index]);
/* 318:523 */         str = str.substring(pos[index] + charsFind[index].length());
/* 319:    */       }
/* 320:    */     }
/* 321:527 */     return newStringBuffer.toString().replace("@@-@@", "\\");
/* 322:    */   }
/* 323:    */   
/* 324:    */   public void toString(StringBuffer buffer)
/* 325:    */   {
/* 326:541 */     int level = getLevel();
/* 327:542 */     boolean isLast = getNextSibling() == null;
/* 328:543 */     String indent = getIndentation(level);
/* 329:    */     
/* 330:545 */     buffer.append(indent);
/* 331:546 */     if (this.m_Name != null)
/* 332:    */     {
/* 333:547 */       buffer.append("\"");
/* 334:548 */       buffer.append(escape(this.m_Name));
/* 335:549 */       buffer.append("\" : ");
/* 336:    */     }
/* 337:552 */     if (isObject())
/* 338:    */     {
/* 339:553 */       buffer.append("{\n");
/* 340:554 */       for (int i = 0; i < getChildCount(); i++) {
/* 341:555 */         ((JSONNode)getChildAt(i)).toString(buffer);
/* 342:    */       }
/* 343:557 */       buffer.append(indent);
/* 344:558 */       buffer.append("}");
/* 345:    */     }
/* 346:559 */     else if (isArray())
/* 347:    */     {
/* 348:560 */       buffer.append("[\n");
/* 349:561 */       for (int i = 0; i < getChildCount(); i++) {
/* 350:562 */         ((JSONNode)getChildAt(i)).toString(buffer);
/* 351:    */       }
/* 352:564 */       buffer.append(indent);
/* 353:565 */       buffer.append("]");
/* 354:    */     }
/* 355:567 */     else if (this.m_Value == null)
/* 356:    */     {
/* 357:568 */       buffer.append("null");
/* 358:    */     }
/* 359:569 */     else if ((this.m_Value instanceof String))
/* 360:    */     {
/* 361:570 */       buffer.append("\"");
/* 362:571 */       buffer.append(escape((String)this.m_Value));
/* 363:572 */       buffer.append("\"");
/* 364:    */     }
/* 365:    */     else
/* 366:    */     {
/* 367:574 */       buffer.append(this.m_Value.toString());
/* 368:    */     }
/* 369:578 */     if (!isLast) {
/* 370:579 */       buffer.append(",");
/* 371:    */     }
/* 372:581 */     buffer.append("\n");
/* 373:    */   }
/* 374:    */   
/* 375:    */   public String toString()
/* 376:    */   {
/* 377:593 */     String result = null;
/* 378:595 */     if (isObject())
/* 379:    */     {
/* 380:596 */       if (isRoot()) {
/* 381:597 */         result = "JSON";
/* 382:598 */       } else if (this.m_Name == null) {
/* 383:599 */         result = "<object>";
/* 384:    */       } else {
/* 385:601 */         result = escape(this.m_Name) + " (Object)";
/* 386:    */       }
/* 387:    */     }
/* 388:603 */     else if (isArray())
/* 389:    */     {
/* 390:604 */       if (this.m_Name == null) {
/* 391:605 */         result = "<array>";
/* 392:    */       } else {
/* 393:607 */         result = escape(this.m_Name) + " (Array)";
/* 394:    */       }
/* 395:    */     }
/* 396:610 */     else if (this.m_Name != null) {
/* 397:611 */       result = escape(this.m_Name) + ": " + escape(this.m_Value);
/* 398:    */     } else {
/* 399:613 */       result = "" + this.m_Value;
/* 400:    */     }
/* 401:617 */     return result;
/* 402:    */   }
/* 403:    */   
/* 404:    */   public static JSONNode read(Reader reader)
/* 405:    */     throws Exception
/* 406:    */   {
/* 407:632 */     SymbolFactory sf = new DefaultSymbolFactory();
/* 408:633 */     Parser parser = new Parser(new Scanner(reader, sf), sf);
/* 409:634 */     parser.parse();
/* 410:    */     
/* 411:636 */     return parser.getResult();
/* 412:    */   }
/* 413:    */   
/* 414:    */   public static void main(String[] args)
/* 415:    */     throws Exception
/* 416:    */   {
/* 417:648 */     JSONNode person = new JSONNode();
/* 418:649 */     person.addPrimitive("firstName", "John");
/* 419:650 */     person.addPrimitive("lastName", "Smith");
/* 420:651 */     JSONNode address = person.addObject("address");
/* 421:652 */     address.addPrimitive("streetAddress", "21 2nd Street");
/* 422:653 */     address.addPrimitive("city", "New York");
/* 423:654 */     address.addPrimitive("state", "NY");
/* 424:655 */     address.addPrimitive("postalCode", Integer.valueOf(10021));
/* 425:656 */     JSONNode phonenumbers = person.addArray("phoneNumbers");
/* 426:657 */     phonenumbers.addArrayElement("212 555-1234");
/* 427:658 */     phonenumbers.addArrayElement("646 555-4567");
/* 428:    */     
/* 429:    */ 
/* 430:661 */     StringBuffer buffer = new StringBuffer();
/* 431:662 */     person.toString(buffer);
/* 432:663 */     System.out.println(buffer.toString());
/* 433:    */     
/* 434:    */ 
/* 435:666 */     JTree tree = new JTree(person);
/* 436:667 */     JFrame frame = new JFrame("JSON");
/* 437:668 */     frame.setSize(800, 600);
/* 438:669 */     frame.setDefaultCloseOperation(3);
/* 439:670 */     frame.getContentPane().setLayout(new BorderLayout());
/* 440:671 */     frame.getContentPane().add(new JScrollPane(tree), "Center");
/* 441:672 */     frame.setLocationRelativeTo(null);
/* 442:673 */     frame.setVisible(true);
/* 443:    */   }
/* 444:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.json.JSONNode
 * JD-Core Version:    0.7.0.1
 */