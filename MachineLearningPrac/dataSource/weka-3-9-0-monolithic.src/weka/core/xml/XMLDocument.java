/*   1:    */ package weka.core.xml;
/*   2:    */ 
/*   3:    */ import java.io.BufferedWriter;
/*   4:    */ import java.io.ByteArrayInputStream;
/*   5:    */ import java.io.File;
/*   6:    */ import java.io.FileWriter;
/*   7:    */ import java.io.InputStream;
/*   8:    */ import java.io.OutputStream;
/*   9:    */ import java.io.PrintStream;
/*  10:    */ import java.io.Reader;
/*  11:    */ import java.io.Writer;
/*  12:    */ import java.util.Vector;
/*  13:    */ import javax.xml.namespace.QName;
/*  14:    */ import javax.xml.parsers.DocumentBuilder;
/*  15:    */ import javax.xml.parsers.DocumentBuilderFactory;
/*  16:    */ import javax.xml.xpath.XPath;
/*  17:    */ import javax.xml.xpath.XPathConstants;
/*  18:    */ import javax.xml.xpath.XPathFactory;
/*  19:    */ import org.w3c.dom.Document;
/*  20:    */ import org.w3c.dom.Element;
/*  21:    */ import org.w3c.dom.NamedNodeMap;
/*  22:    */ import org.w3c.dom.Node;
/*  23:    */ import org.w3c.dom.NodeList;
/*  24:    */ import org.xml.sax.InputSource;
/*  25:    */ import weka.core.RevisionHandler;
/*  26:    */ import weka.core.RevisionUtils;
/*  27:    */ 
/*  28:    */ public class XMLDocument
/*  29:    */   implements RevisionHandler
/*  30:    */ {
/*  31:    */   public static final String PI = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
/*  32:    */   public static final String DTD_DOCTYPE = "DOCTYPE";
/*  33:    */   public static final String DTD_ELEMENT = "ELEMENT";
/*  34:    */   public static final String DTD_ATTLIST = "ATTLIST";
/*  35:    */   public static final String DTD_OPTIONAL = "?";
/*  36:    */   public static final String DTD_AT_LEAST_ONE = "+";
/*  37:    */   public static final String DTD_ZERO_OR_MORE = "*";
/*  38:    */   public static final String DTD_SEPARATOR = "|";
/*  39:    */   public static final String DTD_CDATA = "CDATA";
/*  40:    */   public static final String DTD_ANY = "ANY";
/*  41:    */   public static final String DTD_PCDATA = "#PCDATA";
/*  42:    */   public static final String DTD_IMPLIED = "#IMPLIED";
/*  43:    */   public static final String DTD_REQUIRED = "#REQUIRED";
/*  44:    */   public static final String ATT_VERSION = "version";
/*  45:    */   public static final String ATT_NAME = "name";
/*  46:    */   public static final String VAL_YES = "yes";
/*  47:    */   public static final String VAL_NO = "no";
/*  48:119 */   protected DocumentBuilderFactory m_Factory = null;
/*  49:122 */   protected DocumentBuilder m_Builder = null;
/*  50:125 */   protected boolean m_Validating = false;
/*  51:128 */   protected Document m_Document = null;
/*  52:131 */   protected String m_DocType = null;
/*  53:134 */   protected String m_RootNode = null;
/*  54:137 */   protected XPath m_XPath = null;
/*  55:    */   
/*  56:    */   public XMLDocument()
/*  57:    */     throws Exception
/*  58:    */   {
/*  59:145 */     this.m_Factory = DocumentBuilderFactory.newInstance();
/*  60:146 */     this.m_XPath = XPathFactory.newInstance("http://java.sun.com/jaxp/xpath/dom").newXPath();
/*  61:147 */     setDocType(null);
/*  62:148 */     setRootNode(null);
/*  63:149 */     setValidating(false);
/*  64:    */   }
/*  65:    */   
/*  66:    */   public XMLDocument(String xml)
/*  67:    */     throws Exception
/*  68:    */   {
/*  69:160 */     this();
/*  70:161 */     read(xml);
/*  71:    */   }
/*  72:    */   
/*  73:    */   public XMLDocument(File file)
/*  74:    */     throws Exception
/*  75:    */   {
/*  76:172 */     this();
/*  77:173 */     read(file);
/*  78:    */   }
/*  79:    */   
/*  80:    */   public XMLDocument(InputStream stream)
/*  81:    */     throws Exception
/*  82:    */   {
/*  83:184 */     this();
/*  84:185 */     read(stream);
/*  85:    */   }
/*  86:    */   
/*  87:    */   public XMLDocument(Reader reader)
/*  88:    */     throws Exception
/*  89:    */   {
/*  90:196 */     this();
/*  91:197 */     read(reader);
/*  92:    */   }
/*  93:    */   
/*  94:    */   public DocumentBuilderFactory getFactory()
/*  95:    */   {
/*  96:206 */     return this.m_Factory;
/*  97:    */   }
/*  98:    */   
/*  99:    */   public DocumentBuilder getBuilder()
/* 100:    */   {
/* 101:215 */     return this.m_Builder;
/* 102:    */   }
/* 103:    */   
/* 104:    */   public boolean getValidating()
/* 105:    */   {
/* 106:224 */     return this.m_Validating;
/* 107:    */   }
/* 108:    */   
/* 109:    */   public void setValidating(boolean validating)
/* 110:    */     throws Exception
/* 111:    */   {
/* 112:235 */     this.m_Validating = validating;
/* 113:236 */     this.m_Factory.setValidating(validating);
/* 114:237 */     this.m_Builder = this.m_Factory.newDocumentBuilder();
/* 115:238 */     clear();
/* 116:    */   }
/* 117:    */   
/* 118:    */   public Document getDocument()
/* 119:    */   {
/* 120:247 */     return this.m_Document;
/* 121:    */   }
/* 122:    */   
/* 123:    */   public void setDocument(Document newDocument)
/* 124:    */   {
/* 125:256 */     this.m_Document = newDocument;
/* 126:    */   }
/* 127:    */   
/* 128:    */   public void setDocType(String docType)
/* 129:    */   {
/* 130:266 */     this.m_DocType = docType;
/* 131:    */   }
/* 132:    */   
/* 133:    */   public String getDocType()
/* 134:    */   {
/* 135:275 */     return this.m_DocType;
/* 136:    */   }
/* 137:    */   
/* 138:    */   public void setRootNode(String rootNode)
/* 139:    */   {
/* 140:285 */     if (rootNode == null) {
/* 141:286 */       this.m_RootNode = "root";
/* 142:    */     } else {
/* 143:288 */       this.m_RootNode = rootNode;
/* 144:    */     }
/* 145:    */   }
/* 146:    */   
/* 147:    */   public String getRootNode()
/* 148:    */   {
/* 149:297 */     return this.m_RootNode;
/* 150:    */   }
/* 151:    */   
/* 152:    */   public void clear()
/* 153:    */   {
/* 154:307 */     newDocument(getDocType(), getRootNode());
/* 155:    */   }
/* 156:    */   
/* 157:    */   public Document newDocument(String docType, String rootNode)
/* 158:    */   {
/* 159:319 */     this.m_Document = getBuilder().newDocument();
/* 160:320 */     this.m_Document.appendChild(this.m_Document.createElement(rootNode));
/* 161:321 */     setDocType(docType);
/* 162:    */     
/* 163:323 */     return getDocument();
/* 164:    */   }
/* 165:    */   
/* 166:    */   public Document read(String xml)
/* 167:    */     throws Exception
/* 168:    */   {
/* 169:335 */     if (xml.toLowerCase().indexOf("<?xml") > -1) {
/* 170:336 */       return read(new ByteArrayInputStream(xml.getBytes()));
/* 171:    */     }
/* 172:338 */     return read(new File(xml));
/* 173:    */   }
/* 174:    */   
/* 175:    */   public Document read(File file)
/* 176:    */     throws Exception
/* 177:    */   {
/* 178:349 */     this.m_Document = getBuilder().parse(file);
/* 179:350 */     return getDocument();
/* 180:    */   }
/* 181:    */   
/* 182:    */   public Document read(InputStream stream)
/* 183:    */     throws Exception
/* 184:    */   {
/* 185:361 */     this.m_Document = getBuilder().parse(stream);
/* 186:362 */     return getDocument();
/* 187:    */   }
/* 188:    */   
/* 189:    */   public Document read(Reader reader)
/* 190:    */     throws Exception
/* 191:    */   {
/* 192:373 */     this.m_Document = getBuilder().parse(new InputSource(reader));
/* 193:374 */     return getDocument();
/* 194:    */   }
/* 195:    */   
/* 196:    */   public void write(String file)
/* 197:    */     throws Exception
/* 198:    */   {
/* 199:385 */     write(new File(file));
/* 200:    */   }
/* 201:    */   
/* 202:    */   public void write(File file)
/* 203:    */     throws Exception
/* 204:    */   {
/* 205:395 */     write(new BufferedWriter(new FileWriter(file)));
/* 206:    */   }
/* 207:    */   
/* 208:    */   public void write(OutputStream stream)
/* 209:    */     throws Exception
/* 210:    */   {
/* 211:407 */     String xml = toString();
/* 212:408 */     stream.write(xml.getBytes(), 0, xml.length());
/* 213:409 */     stream.flush();
/* 214:    */   }
/* 215:    */   
/* 216:    */   public void write(Writer writer)
/* 217:    */     throws Exception
/* 218:    */   {
/* 219:419 */     writer.write(toString());
/* 220:420 */     writer.flush();
/* 221:    */   }
/* 222:    */   
/* 223:    */   public static Vector<Element> getChildTags(Node parent)
/* 224:    */   {
/* 225:430 */     return getChildTags(parent, "");
/* 226:    */   }
/* 227:    */   
/* 228:    */   public static Vector<Element> getChildTags(Node parent, String name)
/* 229:    */   {
/* 230:445 */     Vector<Element> result = new Vector();
/* 231:    */     
/* 232:447 */     NodeList list = parent.getChildNodes();
/* 233:448 */     for (int i = 0; i < list.getLength(); i++) {
/* 234:449 */       if ((list.item(i) instanceof Element)) {
/* 235:452 */         if ((name.length() == 0) || 
/* 236:453 */           (((Element)list.item(i)).getTagName().equals(name))) {
/* 237:456 */           result.add((Element)list.item(i));
/* 238:    */         }
/* 239:    */       }
/* 240:    */     }
/* 241:459 */     return result;
/* 242:    */   }
/* 243:    */   
/* 244:    */   protected Object eval(String xpath, QName type)
/* 245:    */   {
/* 246:    */     Object result;
/* 247:    */     try
/* 248:    */     {
/* 249:474 */       result = this.m_XPath.evaluate(xpath, this.m_Document, type);
/* 250:    */     }
/* 251:    */     catch (Exception e)
/* 252:    */     {
/* 253:477 */       e.printStackTrace();
/* 254:478 */       result = null;
/* 255:    */     }
/* 256:481 */     return result;
/* 257:    */   }
/* 258:    */   
/* 259:    */   public NodeList findNodes(String xpath)
/* 260:    */   {
/* 261:492 */     return (NodeList)eval(xpath, XPathConstants.NODESET);
/* 262:    */   }
/* 263:    */   
/* 264:    */   public Node getNode(String xpath)
/* 265:    */   {
/* 266:503 */     return (Node)eval(xpath, XPathConstants.NODE);
/* 267:    */   }
/* 268:    */   
/* 269:    */   public Boolean evalBoolean(String xpath)
/* 270:    */   {
/* 271:513 */     return (Boolean)eval(xpath, XPathConstants.BOOLEAN);
/* 272:    */   }
/* 273:    */   
/* 274:    */   public Double evalDouble(String xpath)
/* 275:    */   {
/* 276:524 */     return (Double)eval(xpath, XPathConstants.NUMBER);
/* 277:    */   }
/* 278:    */   
/* 279:    */   public String evalString(String xpath)
/* 280:    */   {
/* 281:534 */     return (String)eval(xpath, XPathConstants.STRING);
/* 282:    */   }
/* 283:    */   
/* 284:    */   public static String getContent(Element node)
/* 285:    */   {
/* 286:550 */     String result = "";
/* 287:551 */     NodeList list = node.getChildNodes();
/* 288:553 */     for (int i = 0; i < list.getLength(); i++)
/* 289:    */     {
/* 290:554 */       Node item = list.item(i);
/* 291:555 */       if (item.getNodeType() == 3) {
/* 292:556 */         result = result + item.getNodeValue();
/* 293:    */       }
/* 294:    */     }
/* 295:559 */     return result.trim();
/* 296:    */   }
/* 297:    */   
/* 298:    */   protected StringBuffer toString(StringBuffer buf, Node parent, int depth)
/* 299:    */   {
/* 300:579 */     String indent = "";
/* 301:580 */     for (int i = 0; i < depth; i++) {
/* 302:581 */       indent = indent + "   ";
/* 303:    */     }
/* 304:583 */     if (parent.getNodeType() == 3)
/* 305:    */     {
/* 306:584 */       if (!parent.getNodeValue().trim().equals("")) {
/* 307:585 */         buf.append(indent + parent.getNodeValue().trim() + "\n");
/* 308:    */       }
/* 309:    */     }
/* 310:588 */     else if (parent.getNodeType() == 8)
/* 311:    */     {
/* 312:589 */       buf.append(indent + "<!--" + parent.getNodeValue() + "-->\n");
/* 313:    */     }
/* 314:    */     else
/* 315:    */     {
/* 316:592 */       buf.append(indent + "<" + parent.getNodeName());
/* 317:594 */       if (parent.hasAttributes())
/* 318:    */       {
/* 319:595 */         NamedNodeMap atts = parent.getAttributes();
/* 320:596 */         for (int n = 0; n < atts.getLength(); n++)
/* 321:    */         {
/* 322:597 */           Node node = atts.item(n);
/* 323:598 */           buf.append(" " + node.getNodeName() + "=\"" + node.getNodeValue() + "\"");
/* 324:    */         }
/* 325:    */       }
/* 326:602 */       if (parent.hasChildNodes())
/* 327:    */       {
/* 328:603 */         NodeList list = parent.getChildNodes();
/* 329:605 */         if ((list.getLength() == 1) && (list.item(0).getNodeType() == 3))
/* 330:    */         {
/* 331:606 */           buf.append(">");
/* 332:607 */           buf.append(list.item(0).getNodeValue().trim());
/* 333:608 */           buf.append("</" + parent.getNodeName() + ">\n");
/* 334:    */         }
/* 335:    */         else
/* 336:    */         {
/* 337:611 */           buf.append(">\n");
/* 338:612 */           for (int n = 0; n < list.getLength(); n++)
/* 339:    */           {
/* 340:613 */             Node node = list.item(n);
/* 341:614 */             toString(buf, node, depth + 1);
/* 342:    */           }
/* 343:616 */           buf.append(indent + "</" + parent.getNodeName() + ">\n");
/* 344:    */         }
/* 345:    */       }
/* 346:    */       else
/* 347:    */       {
/* 348:620 */         buf.append("/>\n");
/* 349:    */       }
/* 350:    */     }
/* 351:624 */     return buf;
/* 352:    */   }
/* 353:    */   
/* 354:    */   public void print()
/* 355:    */   {
/* 356:631 */     System.out.println(toString());
/* 357:    */   }
/* 358:    */   
/* 359:    */   public String toString()
/* 360:    */   {
/* 361:642 */     String header = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n\n";
/* 362:643 */     if (getDocType() != null) {
/* 363:644 */       header = header + getDocType() + "\n\n";
/* 364:    */     }
/* 365:646 */     return toString(new StringBuffer(header), getDocument().getDocumentElement(), 0).toString();
/* 366:    */   }
/* 367:    */   
/* 368:    */   public String getRevision()
/* 369:    */   {
/* 370:655 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 371:    */   }
/* 372:    */   
/* 373:    */   public static void main(String[] args)
/* 374:    */     throws Exception
/* 375:    */   {
/* 376:669 */     if (args.length > 0)
/* 377:    */     {
/* 378:670 */       XMLDocument doc = new XMLDocument();
/* 379:    */       
/* 380:    */ 
/* 381:673 */       doc.read(args[0]);
/* 382:    */       
/* 383:    */ 
/* 384:676 */       doc.print();
/* 385:679 */       if (args.length > 1) {
/* 386:680 */         doc.write(args[1]);
/* 387:    */       }
/* 388:    */     }
/* 389:    */   }
/* 390:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.xml.XMLDocument
 * JD-Core Version:    0.7.0.1
 */