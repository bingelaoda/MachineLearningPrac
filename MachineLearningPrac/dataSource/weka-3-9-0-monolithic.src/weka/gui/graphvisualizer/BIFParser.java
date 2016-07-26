/*   1:    */ package weka.gui.graphvisualizer;
/*   2:    */ 
/*   3:    */ import java.io.FileWriter;
/*   4:    */ import java.io.IOException;
/*   5:    */ import java.io.InputStream;
/*   6:    */ import java.io.StringReader;
/*   7:    */ import java.util.ArrayList;
/*   8:    */ import java.util.StringTokenizer;
/*   9:    */ import javax.xml.parsers.DocumentBuilder;
/*  10:    */ import javax.xml.parsers.DocumentBuilderFactory;
/*  11:    */ import org.w3c.dom.Document;
/*  12:    */ import org.w3c.dom.Element;
/*  13:    */ import org.w3c.dom.Node;
/*  14:    */ import org.w3c.dom.NodeList;
/*  15:    */ import org.xml.sax.InputSource;
/*  16:    */ 
/*  17:    */ public class BIFParser
/*  18:    */   implements GraphConstants
/*  19:    */ {
/*  20:    */   protected ArrayList<GraphNode> m_nodes;
/*  21:    */   protected ArrayList<GraphEdge> m_edges;
/*  22:    */   protected String graphName;
/*  23:    */   protected String inString;
/*  24:    */   protected InputStream inStream;
/*  25:    */   
/*  26:    */   public BIFParser(String input, ArrayList<GraphNode> nodes, ArrayList<GraphEdge> edges)
/*  27:    */   {
/*  28: 66 */     this.m_nodes = nodes;
/*  29: 67 */     this.m_edges = edges;
/*  30: 68 */     this.inString = input;
/*  31:    */   }
/*  32:    */   
/*  33:    */   public BIFParser(InputStream instream, ArrayList<GraphNode> nodes, ArrayList<GraphEdge> edges)
/*  34:    */   {
/*  35: 80 */     this.m_nodes = nodes;
/*  36: 81 */     this.m_edges = edges;
/*  37: 82 */     this.inStream = instream;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public String parse()
/*  41:    */     throws Exception
/*  42:    */   {
/*  43: 98 */     Document dc = null;
/*  44:    */     
/*  45:100 */     DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
/*  46:    */     
/*  47:102 */     dbf.setIgnoringElementContentWhitespace(true);
/*  48:103 */     DocumentBuilder db = dbf.newDocumentBuilder();
/*  49:105 */     if (this.inStream != null) {
/*  50:106 */       dc = db.parse(this.inStream);
/*  51:107 */     } else if (this.inString != null) {
/*  52:108 */       dc = db.parse(new InputSource(new StringReader(this.inString)));
/*  53:    */     } else {
/*  54:110 */       throw new Exception("No input given");
/*  55:    */     }
/*  56:113 */     NodeList nl = dc.getElementsByTagName("NETWORK");
/*  57:115 */     if (nl.getLength() == 0) {
/*  58:116 */       throw new BIFFormatException("NETWORK tag not found");
/*  59:    */     }
/*  60:120 */     NodeList templist = ((Element)nl.item(0)).getElementsByTagName("NAME");
/*  61:121 */     this.graphName = templist.item(0).getFirstChild().getNodeValue();
/*  62:    */     
/*  63:    */ 
/*  64:    */ 
/*  65:    */ 
/*  66:126 */     nl = dc.getElementsByTagName("VARIABLE");
/*  67:127 */     for (int i = 0; i < nl.getLength(); i++)
/*  68:    */     {
/*  69:129 */       templist = ((Element)nl.item(i)).getElementsByTagName("NAME");
/*  70:130 */       if (templist.getLength() > 1) {
/*  71:131 */         throw new BIFFormatException("More than one name tags found for variable no. " + (i + 1));
/*  72:    */       }
/*  73:135 */       String nodename = templist.item(0).getFirstChild().getNodeValue();
/*  74:136 */       GraphNode n = new GraphNode(nodename, nodename, 3);
/*  75:137 */       this.m_nodes.add(n);
/*  76:    */       
/*  77:139 */       templist = ((Element)nl.item(i)).getElementsByTagName("PROPERTY");
/*  78:140 */       for (int j = 0; j < templist.getLength(); j++) {
/*  79:141 */         if (templist.item(j).getFirstChild().getNodeValue().startsWith("position"))
/*  80:    */         {
/*  81:143 */           String xy = templist.item(j).getFirstChild().getNodeValue();
/*  82:    */           
/*  83:    */ 
/*  84:    */ 
/*  85:    */ 
/*  86:    */ 
/*  87:149 */           n.x = Integer.parseInt(xy.substring(xy.indexOf('(') + 1, xy.indexOf(',')).trim());
/*  88:    */           
/*  89:151 */           n.y = Integer.parseInt(xy.substring(xy.indexOf(',') + 1, xy.indexOf(')')).trim());
/*  90:    */           
/*  91:153 */           break;
/*  92:    */         }
/*  93:    */       }
/*  94:157 */       templist = ((Element)nl.item(i)).getElementsByTagName("OUTCOME");
/*  95:158 */       n.outcomes = new String[templist.getLength()];
/*  96:159 */       for (int j = 0; j < templist.getLength(); j++) {
/*  97:160 */         n.outcomes[j] = templist.item(j).getFirstChild().getNodeValue();
/*  98:    */       }
/*  99:    */     }
/* 100:166 */     nl = dc.getElementsByTagName("DEFINITION");
/* 101:167 */     for (int i = 0; i < nl.getLength(); i++)
/* 102:    */     {
/* 103:169 */       templist = ((Element)nl.item(i)).getElementsByTagName("FOR");
/* 104:    */       
/* 105:171 */       String nid = templist.item(0).getFirstChild().getNodeValue();
/* 106:    */       
/* 107:    */ 
/* 108:174 */       GraphNode n = (GraphNode)this.m_nodes.get(0);
/* 109:175 */       for (int j = 1; (j < this.m_nodes.size()) && (!n.ID.equals(nid)); j++) {
/* 110:176 */         n = (GraphNode)this.m_nodes.get(j);
/* 111:    */       }
/* 112:179 */       templist = ((Element)nl.item(i)).getElementsByTagName("GIVEN");
/* 113:180 */       int parntOutcomes = 1;
/* 114:182 */       for (int j = 0; j < templist.getLength(); j++)
/* 115:    */       {
/* 116:183 */         nid = templist.item(j).getFirstChild().getNodeValue();
/* 117:    */         
/* 118:185 */         GraphNode n2 = (GraphNode)this.m_nodes.get(0);
/* 119:186 */         for (int k = 1; (k < this.m_nodes.size()) && (!n2.ID.equals(nid)); k++) {
/* 120:187 */           n2 = (GraphNode)this.m_nodes.get(k);
/* 121:    */         }
/* 122:189 */         this.m_edges.add(new GraphEdge(this.m_nodes.indexOf(n2), this.m_nodes.indexOf(n), 1));
/* 123:    */         
/* 124:191 */         parntOutcomes *= n2.outcomes.length;
/* 125:    */       }
/* 126:195 */       templist = ((Element)nl.item(i)).getElementsByTagName("TABLE");
/* 127:196 */       if (templist.getLength() > 1) {
/* 128:197 */         throw new BIFFormatException("More than one Probability Table for " + n.ID);
/* 129:    */       }
/* 130:201 */       String probs = templist.item(0).getFirstChild().getNodeValue();
/* 131:202 */       StringTokenizer tk = new StringTokenizer(probs, " \n\t");
/* 132:204 */       if (parntOutcomes * n.outcomes.length > tk.countTokens()) {
/* 133:205 */         throw new BIFFormatException("Probability Table for " + n.ID + " contains more values than it should");
/* 134:    */       }
/* 135:207 */       if (parntOutcomes * n.outcomes.length < tk.countTokens()) {
/* 136:208 */         throw new BIFFormatException("Probability Table for " + n.ID + " contains less values than it should");
/* 137:    */       }
/* 138:211 */       n.probs = new double[parntOutcomes][n.outcomes.length];
/* 139:212 */       for (int r = 0; r < parntOutcomes; r++) {
/* 140:213 */         for (int c = 0; c < n.outcomes.length; c++) {
/* 141:    */           try
/* 142:    */           {
/* 143:215 */             n.probs[r][c] = Double.parseDouble(tk.nextToken());
/* 144:    */           }
/* 145:    */           catch (NumberFormatException ne)
/* 146:    */           {
/* 147:217 */             throw ne;
/* 148:    */           }
/* 149:    */         }
/* 150:    */       }
/* 151:    */     }
/* 152:235 */     int[] noOfEdgesOfNode = new int[this.m_nodes.size()];
/* 153:236 */     int[] noOfPrntsOfNode = new int[this.m_nodes.size()];
/* 154:237 */     for (int i = 0; i < this.m_edges.size(); i++)
/* 155:    */     {
/* 156:238 */       GraphEdge e = (GraphEdge)this.m_edges.get(i);
/* 157:239 */       noOfEdgesOfNode[e.src] += 1;
/* 158:240 */       noOfPrntsOfNode[e.dest] += 1;
/* 159:    */     }
/* 160:243 */     for (int i = 0; i < this.m_edges.size(); i++)
/* 161:    */     {
/* 162:244 */       GraphEdge e = (GraphEdge)this.m_edges.get(i);
/* 163:245 */       GraphNode n = (GraphNode)this.m_nodes.get(e.src);
/* 164:246 */       GraphNode n2 = (GraphNode)this.m_nodes.get(e.dest);
/* 165:247 */       if (n.edges == null)
/* 166:    */       {
/* 167:248 */         n.edges = new int[noOfEdgesOfNode[e.src]][2];
/* 168:249 */         for (int k = 0; k < n.edges.length; k++) {
/* 169:250 */           n.edges[k][0] = -1;
/* 170:    */         }
/* 171:    */       }
/* 172:253 */       if (n2.prnts == null)
/* 173:    */       {
/* 174:254 */         n2.prnts = new int[noOfPrntsOfNode[e.dest]];
/* 175:255 */         for (int k = 0; k < n2.prnts.length; k++) {
/* 176:256 */           n2.prnts[k] = -1;
/* 177:    */         }
/* 178:    */       }
/* 179:260 */       int k = 0;
/* 180:261 */       while (n.edges[k][0] != -1) {
/* 181:262 */         k++;
/* 182:    */       }
/* 183:264 */       n.edges[k][0] = e.dest;
/* 184:265 */       n.edges[k][1] = e.type;
/* 185:    */       
/* 186:267 */       k = 0;
/* 187:268 */       while (n2.prnts[k] != -1) {
/* 188:269 */         k++;
/* 189:    */       }
/* 190:271 */       n2.prnts[k] = e.src;
/* 191:    */     }
/* 192:276 */     return this.graphName;
/* 193:    */   }
/* 194:    */   
/* 195:    */   public static void writeXMLBIF03(String filename, String graphName, ArrayList<GraphNode> nodes, ArrayList<GraphEdge> edges)
/* 196:    */   {
/* 197:    */     try
/* 198:    */     {
/* 199:293 */       FileWriter outfile = new FileWriter(filename);
/* 200:    */       
/* 201:295 */       StringBuffer text = new StringBuffer();
/* 202:    */       
/* 203:297 */       text.append("<?xml version=\"1.0\"?>\n");
/* 204:298 */       text.append("<!-- DTD for the XMLBIF 0.3 format -->\n");
/* 205:299 */       text.append("<!DOCTYPE BIF [\n");
/* 206:300 */       text.append("\t<!ELEMENT BIF ( NETWORK )*>\n");
/* 207:301 */       text.append("\t      <!ATTLIST BIF VERSION CDATA #REQUIRED>\n");
/* 208:302 */       text.append("\t<!ELEMENT NETWORK ( NAME, ( PROPERTY | VARIABLE | DEFINITION )* )>\n");
/* 209:    */       
/* 210:304 */       text.append("\t<!ELEMENT NAME (#PCDATA)>\n");
/* 211:305 */       text.append("\t<!ELEMENT VARIABLE ( NAME, ( OUTCOME |  PROPERTY )* ) >\n");
/* 212:    */       
/* 213:307 */       text.append("\t      <!ATTLIST VARIABLE TYPE (nature|decision|utility) \"nature\">\n");
/* 214:    */       
/* 215:309 */       text.append("\t<!ELEMENT OUTCOME (#PCDATA)>\n");
/* 216:310 */       text.append("\t<!ELEMENT DEFINITION ( FOR | GIVEN | TABLE | PROPERTY )* >\n");
/* 217:    */       
/* 218:312 */       text.append("\t<!ELEMENT FOR (#PCDATA)>\n");
/* 219:313 */       text.append("\t<!ELEMENT GIVEN (#PCDATA)>\n");
/* 220:314 */       text.append("\t<!ELEMENT TABLE (#PCDATA)>\n");
/* 221:315 */       text.append("\t<!ELEMENT PROPERTY (#PCDATA)>\n");
/* 222:316 */       text.append("]>\n");
/* 223:317 */       text.append("\n");
/* 224:318 */       text.append("\n");
/* 225:319 */       text.append("<BIF VERSION=\"0.3\">\n");
/* 226:320 */       text.append("<NETWORK>\n");
/* 227:321 */       text.append("<NAME>" + XMLNormalize(graphName) + "</NAME>\n");
/* 228:326 */       for (int nodeidx = 0; nodeidx < nodes.size(); nodeidx++)
/* 229:    */       {
/* 230:327 */         GraphNode n = (GraphNode)nodes.get(nodeidx);
/* 231:328 */         if (n.nodeType == 3)
/* 232:    */         {
/* 233:332 */           text.append("<VARIABLE TYPE=\"nature\">\n");
/* 234:333 */           text.append("\t<NAME>" + XMLNormalize(n.ID) + "</NAME>\n");
/* 235:335 */           if (n.outcomes != null) {
/* 236:336 */             for (String outcome : n.outcomes) {
/* 237:337 */               text.append("\t<OUTCOME>" + XMLNormalize(outcome) + "</OUTCOME>\n");
/* 238:    */             }
/* 239:    */           } else {
/* 240:340 */             text.append("\t<OUTCOME>true</OUTCOME>\n");
/* 241:    */           }
/* 242:343 */           text.append("\t<PROPERTY>position = (" + n.x + "," + n.y + ")</PROPERTY>\n");
/* 243:    */           
/* 244:345 */           text.append("</VARIABLE>\n");
/* 245:    */         }
/* 246:    */       }
/* 247:351 */       for (int nodeidx = 0; nodeidx < nodes.size(); nodeidx++)
/* 248:    */       {
/* 249:352 */         GraphNode n = (GraphNode)nodes.get(nodeidx);
/* 250:353 */         if (n.nodeType == 3)
/* 251:    */         {
/* 252:357 */           text.append("<DEFINITION>\n");
/* 253:358 */           text.append("<FOR>" + XMLNormalize(n.ID) + "</FOR>\n");
/* 254:359 */           int parntOutcomes = 1;
/* 255:360 */           if (n.prnts != null) {
/* 256:361 */             for (int prnt2 : n.prnts)
/* 257:    */             {
/* 258:362 */               GraphNode prnt = (GraphNode)nodes.get(prnt2);
/* 259:363 */               text.append("\t<GIVEN>" + XMLNormalize(prnt.ID) + "</GIVEN>\n");
/* 260:364 */               if (prnt.outcomes != null) {
/* 261:365 */                 parntOutcomes *= prnt.outcomes.length;
/* 262:    */               }
/* 263:    */             }
/* 264:    */           }
/* 265:370 */           text.append("<TABLE>\n");
/* 266:371 */           for (int i = 0; i < parntOutcomes; i++)
/* 267:    */           {
/* 268:372 */             if (n.outcomes != null) {
/* 269:373 */               for (int outidx = 0; outidx < n.outcomes.length; outidx++) {
/* 270:374 */                 text.append(n.probs[i][outidx] + " ");
/* 271:    */               }
/* 272:    */             } else {
/* 273:377 */               text.append("1");
/* 274:    */             }
/* 275:379 */             text.append('\n');
/* 276:    */           }
/* 277:381 */           text.append("</TABLE>\n");
/* 278:382 */           text.append("</DEFINITION>\n");
/* 279:    */         }
/* 280:    */       }
/* 281:384 */       text.append("</NETWORK>\n");
/* 282:385 */       text.append("</BIF>\n");
/* 283:    */       
/* 284:387 */       outfile.write(text.toString());
/* 285:388 */       outfile.close();
/* 286:    */     }
/* 287:    */     catch (IOException ex)
/* 288:    */     {
/* 289:390 */       ex.printStackTrace();
/* 290:    */     }
/* 291:    */   }
/* 292:    */   
/* 293:    */   private static String XMLNormalize(String sStr)
/* 294:    */   {
/* 295:403 */     StringBuffer sStr2 = new StringBuffer();
/* 296:404 */     for (int iStr = 0; iStr < sStr.length(); iStr++)
/* 297:    */     {
/* 298:405 */       char c = sStr.charAt(iStr);
/* 299:406 */       switch (c)
/* 300:    */       {
/* 301:    */       case '&': 
/* 302:408 */         sStr2.append("&amp;");
/* 303:409 */         break;
/* 304:    */       case '\'': 
/* 305:411 */         sStr2.append("&apos;");
/* 306:412 */         break;
/* 307:    */       case '"': 
/* 308:414 */         sStr2.append("&quot;");
/* 309:415 */         break;
/* 310:    */       case '<': 
/* 311:417 */         sStr2.append("&lt;");
/* 312:418 */         break;
/* 313:    */       case '>': 
/* 314:420 */         sStr2.append("&gt;");
/* 315:421 */         break;
/* 316:    */       default: 
/* 317:423 */         sStr2.append(c);
/* 318:    */       }
/* 319:    */     }
/* 320:426 */     return sStr2.toString();
/* 321:    */   }
/* 322:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.graphvisualizer.BIFParser
 * JD-Core Version:    0.7.0.1
 */