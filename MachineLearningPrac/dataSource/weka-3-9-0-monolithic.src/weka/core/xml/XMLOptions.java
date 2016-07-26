/*   1:    */ package weka.core.xml;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.InputStream;
/*   5:    */ import java.io.PrintStream;
/*   6:    */ import java.io.Reader;
/*   7:    */ import java.util.Vector;
/*   8:    */ import org.w3c.dom.Document;
/*   9:    */ import org.w3c.dom.Element;
/*  10:    */ import org.w3c.dom.Node;
/*  11:    */ import org.w3c.dom.NodeList;
/*  12:    */ import weka.core.RevisionHandler;
/*  13:    */ import weka.core.RevisionUtils;
/*  14:    */ import weka.core.Utils;
/*  15:    */ 
/*  16:    */ public class XMLOptions
/*  17:    */   implements RevisionHandler
/*  18:    */ {
/*  19:    */   public static final String TAG_OPTION = "option";
/*  20:    */   public static final String TAG_OPTIONS = "options";
/*  21:    */   public static final String ATT_NAME = "name";
/*  22:    */   public static final String ATT_TYPE = "type";
/*  23:    */   public static final String ATT_VALUE = "value";
/*  24:    */   public static final String VAL_TYPE_FLAG = "flag";
/*  25:    */   public static final String VAL_TYPE_SINGLE = "single";
/*  26:    */   public static final String VAL_TYPE_HYPHENS = "hyphens";
/*  27:    */   public static final String VAL_TYPE_QUOTES = "quotes";
/*  28:    */   public static final String VAL_TYPE_CLASSIFIER = "classifier";
/*  29:    */   public static final String VAL_TYPE_OPTIONHANDLER = "optionhandler";
/*  30:    */   public static final String ROOT_NODE = "options";
/*  31:    */   public static final String DOCTYPE = "<!DOCTYPE options\n[\n   <!ELEMENT options (option)*>\n   <!ATTLIST options type CDATA \"optionhandler\">\n   <!ATTLIST options value CDATA \"\">\n   <!ELEMENT option (#PCDATA | options)*>\n   <!ATTLIST option name CDATA #REQUIRED>\n   <!ATTLIST option type (flag | single | hyphens | quotes) \"single\">\n]\n>";
/*  32: 97 */   protected XMLDocument m_XMLDocument = null;
/*  33:    */   
/*  34:    */   public XMLOptions()
/*  35:    */     throws Exception
/*  36:    */   {
/*  37:106 */     this.m_XMLDocument = new XMLDocument();
/*  38:107 */     this.m_XMLDocument.setRootNode("options");
/*  39:108 */     this.m_XMLDocument.setDocType("<!DOCTYPE options\n[\n   <!ELEMENT options (option)*>\n   <!ATTLIST options type CDATA \"optionhandler\">\n   <!ATTLIST options value CDATA \"\">\n   <!ELEMENT option (#PCDATA | options)*>\n   <!ATTLIST option name CDATA #REQUIRED>\n   <!ATTLIST option type (flag | single | hyphens | quotes) \"single\">\n]\n>");
/*  40:109 */     setValidating(true);
/*  41:    */   }
/*  42:    */   
/*  43:    */   public XMLOptions(String xml)
/*  44:    */     throws Exception
/*  45:    */   {
/*  46:121 */     this();
/*  47:122 */     getXMLDocument().read(xml);
/*  48:    */   }
/*  49:    */   
/*  50:    */   public XMLOptions(File file)
/*  51:    */     throws Exception
/*  52:    */   {
/*  53:133 */     this();
/*  54:134 */     getXMLDocument().read(file);
/*  55:    */   }
/*  56:    */   
/*  57:    */   public XMLOptions(InputStream stream)
/*  58:    */     throws Exception
/*  59:    */   {
/*  60:145 */     this();
/*  61:146 */     getXMLDocument().read(stream);
/*  62:    */   }
/*  63:    */   
/*  64:    */   public XMLOptions(Reader reader)
/*  65:    */     throws Exception
/*  66:    */   {
/*  67:157 */     this();
/*  68:158 */     getXMLDocument().read(reader);
/*  69:    */   }
/*  70:    */   
/*  71:    */   public boolean getValidating()
/*  72:    */   {
/*  73:167 */     return this.m_XMLDocument.getValidating();
/*  74:    */   }
/*  75:    */   
/*  76:    */   public void setValidating(boolean validating)
/*  77:    */     throws Exception
/*  78:    */   {
/*  79:178 */     this.m_XMLDocument.setValidating(validating);
/*  80:    */   }
/*  81:    */   
/*  82:    */   public Document getDocument()
/*  83:    */   {
/*  84:187 */     fixHyphens();
/*  85:188 */     return this.m_XMLDocument.getDocument();
/*  86:    */   }
/*  87:    */   
/*  88:    */   public XMLDocument getXMLDocument()
/*  89:    */   {
/*  90:199 */     fixHyphens();
/*  91:200 */     return this.m_XMLDocument;
/*  92:    */   }
/*  93:    */   
/*  94:    */   protected void fixHyphens()
/*  95:    */   {
/*  96:218 */     NodeList list = this.m_XMLDocument.findNodes("//option");
/*  97:    */     
/*  98:    */ 
/*  99:221 */     Vector<Element> hyphens = new Vector();
/* 100:222 */     for (int i = 0; i < list.getLength(); i++) {
/* 101:223 */       if (((Element)list.item(i)).getAttribute("type").equals("hyphens")) {
/* 102:225 */         hyphens.add((Element)list.item(i));
/* 103:    */       }
/* 104:    */     }
/* 105:230 */     for (i = 0; i < hyphens.size(); i++)
/* 106:    */     {
/* 107:231 */       Node node = (Node)hyphens.get(i);
/* 108:    */       
/* 109:    */ 
/* 110:234 */       boolean isLast = true;
/* 111:235 */       Node tmpNode = node;
/* 112:236 */       while (tmpNode.getNextSibling() != null)
/* 113:    */       {
/* 114:238 */         if (tmpNode.getNextSibling().getNodeType() == 1)
/* 115:    */         {
/* 116:239 */           isLast = false;
/* 117:240 */           break;
/* 118:    */         }
/* 119:242 */         tmpNode = tmpNode.getNextSibling();
/* 120:    */       }
/* 121:246 */       if (!isLast)
/* 122:    */       {
/* 123:247 */         tmpNode = node.getParentNode();
/* 124:248 */         tmpNode.removeChild(node);
/* 125:249 */         tmpNode.appendChild(node);
/* 126:    */       }
/* 127:    */     }
/* 128:    */   }
/* 129:    */   
/* 130:    */   protected String toCommandLine(Element parent)
/* 131:    */   {
/* 132:270 */     Vector<String> result = new Vector();
/* 133:    */     String[] params;
/* 134:    */     int n;
/* 135:273 */     if (parent.getNodeName().equals("options"))
/* 136:    */     {
/* 137:275 */       Vector<Element> list = XMLDocument.getChildTags(parent);
/* 138:277 */       if (parent.getAttribute("type").equals("classifier"))
/* 139:    */       {
/* 140:278 */         System.err.println("Type 'classifier' is deprecated, use 'optionhandler' instead!");
/* 141:    */         
/* 142:280 */         parent.setAttribute("type", "optionhandler");
/* 143:    */       }
/* 144:283 */       if (parent.getAttribute("type").equals("optionhandler"))
/* 145:    */       {
/* 146:284 */         result.add(parent.getAttribute("value"));
/* 147:287 */         if ((list.size() > 0) && (parent.getParentNode() != null) && ((parent.getParentNode() instanceof Element)) && (((Element)parent.getParentNode()).getNodeName().equals("option")) && (((Element)parent.getParentNode()).getAttribute("type").equals("hyphens"))) {
/* 148:294 */           result.add("--");
/* 149:    */         }
/* 150:    */       }
/* 151:299 */       for (int i = 0; i < list.size(); i++)
/* 152:    */       {
/* 153:300 */         String tmpStr = toCommandLine((Element)list.get(i));
/* 154:    */         try
/* 155:    */         {
/* 156:302 */           params = Utils.splitOptions(tmpStr);
/* 157:303 */           for (n = 0; n < params.length; n++) {
/* 158:304 */             result.add(params[n]);
/* 159:    */           }
/* 160:    */         }
/* 161:    */         catch (Exception e)
/* 162:    */         {
/* 163:307 */           System.err.println("Error splitting: " + tmpStr);
/* 164:308 */           e.printStackTrace();
/* 165:    */         }
/* 166:    */       }
/* 167:    */     }
/* 168:313 */     if (parent.getNodeName().equals("option"))
/* 169:    */     {
/* 170:314 */       Vector<Element> subList = XMLDocument.getChildTags(parent);
/* 171:315 */       NodeList subNodeList = parent.getChildNodes();
/* 172:    */       
/* 173:317 */       result.add("-" + parent.getAttribute("name"));
/* 174:320 */       if (parent.getAttribute("type").equals("single"))
/* 175:    */       {
/* 176:321 */         if ((subNodeList.getLength() > 0) && (subNodeList.item(0).getNodeValue().trim().length() > 0)) {
/* 177:323 */           result.add(subNodeList.item(0).getNodeValue());
/* 178:    */         }
/* 179:    */       }
/* 180:327 */       else if (parent.getAttribute("type").equals("quotes"))
/* 181:    */       {
/* 182:328 */         result.add(toCommandLine((Element)subList.get(0)));
/* 183:    */       }
/* 184:331 */       else if (parent.getAttribute("type").equals("hyphens"))
/* 185:    */       {
/* 186:332 */         String tmpStr = toCommandLine((Element)subList.get(0));
/* 187:    */         try
/* 188:    */         {
/* 189:334 */           params = Utils.splitOptions(tmpStr);
/* 190:335 */           for (n = 0; n < params.length; n++) {
/* 191:336 */             result.add(params[n]);
/* 192:    */           }
/* 193:    */         }
/* 194:    */         catch (Exception e)
/* 195:    */         {
/* 196:339 */           System.err.println("Error splitting: " + tmpStr);
/* 197:340 */           e.printStackTrace();
/* 198:    */         }
/* 199:    */       }
/* 200:    */     }
/* 201:    */     else
/* 202:    */     {
/* 203:346 */       System.err.println("Unsupported tag '" + parent.getNodeName() + "' - skipped!");
/* 204:    */     }
/* 205:350 */     return Utils.joinOptions((String[])result.toArray(new String[result.size()]));
/* 206:    */   }
/* 207:    */   
/* 208:    */   public String toCommandLine()
/* 209:    */     throws Exception
/* 210:    */   {
/* 211:360 */     return toCommandLine(getDocument().getDocumentElement());
/* 212:    */   }
/* 213:    */   
/* 214:    */   public String[] toArray()
/* 215:    */     throws Exception
/* 216:    */   {
/* 217:370 */     return Utils.splitOptions(toCommandLine());
/* 218:    */   }
/* 219:    */   
/* 220:    */   public String toString()
/* 221:    */   {
/* 222:380 */     return getXMLDocument().toString();
/* 223:    */   }
/* 224:    */   
/* 225:    */   public String getRevision()
/* 226:    */   {
/* 227:390 */     return RevisionUtils.extract("$Revision: 10203 $");
/* 228:    */   }
/* 229:    */   
/* 230:    */   public static void main(String[] args)
/* 231:    */     throws Exception
/* 232:    */   {
/* 233:401 */     if (args.length > 0)
/* 234:    */     {
/* 235:402 */       System.out.println("\nXML:\n\n" + new XMLOptions(args[0]).toString());
/* 236:    */       
/* 237:404 */       System.out.println("\nCommandline:\n\n" + new XMLOptions(args[0]).toCommandLine());
/* 238:    */       
/* 239:    */ 
/* 240:407 */       System.out.println("\nString array:\n");
/* 241:408 */       String[] options = new XMLOptions(args[0]).toArray();
/* 242:409 */       for (String option : options) {
/* 243:410 */         System.out.println(option);
/* 244:    */       }
/* 245:    */     }
/* 246:    */   }
/* 247:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.xml.XMLOptions
 * JD-Core Version:    0.7.0.1
 */