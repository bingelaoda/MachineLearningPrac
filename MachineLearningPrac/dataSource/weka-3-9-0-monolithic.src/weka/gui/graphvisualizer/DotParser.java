/*   1:    */ package weka.gui.graphvisualizer;
/*   2:    */ 
/*   3:    */ import java.io.BufferedReader;
/*   4:    */ import java.io.FileWriter;
/*   5:    */ import java.io.IOException;
/*   6:    */ import java.io.PrintStream;
/*   7:    */ import java.io.Reader;
/*   8:    */ import java.io.StreamTokenizer;
/*   9:    */ import java.util.ArrayList;
/*  10:    */ 
/*  11:    */ public class DotParser
/*  12:    */   implements GraphConstants
/*  13:    */ {
/*  14:    */   protected ArrayList<GraphNode> m_nodes;
/*  15:    */   protected ArrayList<GraphEdge> m_edges;
/*  16:    */   protected Reader m_input;
/*  17:    */   protected String m_graphName;
/*  18:    */   
/*  19:    */   public DotParser(Reader input, ArrayList<GraphNode> nodes, ArrayList<GraphEdge> edges)
/*  20:    */   {
/*  21: 68 */     this.m_nodes = nodes;
/*  22: 69 */     this.m_edges = edges;
/*  23: 70 */     this.m_input = input;
/*  24:    */   }
/*  25:    */   
/*  26:    */   public String parse()
/*  27:    */   {
/*  28: 80 */     StreamTokenizer tk = new StreamTokenizer(new BufferedReader(this.m_input));
/*  29: 81 */     setSyntax(tk);
/*  30:    */     
/*  31: 83 */     graph(tk);
/*  32:    */     
/*  33: 85 */     return this.m_graphName;
/*  34:    */   }
/*  35:    */   
/*  36:    */   protected void setSyntax(StreamTokenizer tk)
/*  37:    */   {
/*  38: 94 */     tk.resetSyntax();
/*  39: 95 */     tk.eolIsSignificant(false);
/*  40: 96 */     tk.slashStarComments(true);
/*  41: 97 */     tk.slashSlashComments(true);
/*  42: 98 */     tk.whitespaceChars(0, 32);
/*  43: 99 */     tk.wordChars(33, 255);
/*  44:100 */     tk.ordinaryChar(91);
/*  45:101 */     tk.ordinaryChar(93);
/*  46:102 */     tk.ordinaryChar(123);
/*  47:103 */     tk.ordinaryChar(125);
/*  48:104 */     tk.ordinaryChar(45);
/*  49:105 */     tk.ordinaryChar(62);
/*  50:106 */     tk.ordinaryChar(47);
/*  51:107 */     tk.ordinaryChar(42);
/*  52:108 */     tk.quoteChar(34);
/*  53:109 */     tk.whitespaceChars(59, 59);
/*  54:110 */     tk.ordinaryChar(61);
/*  55:    */   }
/*  56:    */   
/*  57:    */   protected void graph(StreamTokenizer tk)
/*  58:    */   {
/*  59:    */     try
/*  60:    */     {
/*  61:122 */       tk.nextToken();
/*  62:124 */       if (tk.ttype == -3) {
/*  63:125 */         if (tk.sval.equalsIgnoreCase("digraph"))
/*  64:    */         {
/*  65:126 */           tk.nextToken();
/*  66:127 */           if (tk.ttype == -3)
/*  67:    */           {
/*  68:128 */             this.m_graphName = tk.sval;
/*  69:129 */             tk.nextToken();
/*  70:    */           }
/*  71:132 */           while (tk.ttype != 123)
/*  72:    */           {
/*  73:133 */             System.err.println("Error at line " + tk.lineno() + " ignoring token " + tk.sval);
/*  74:    */             
/*  75:135 */             tk.nextToken();
/*  76:136 */             if (tk.ttype == -1) {
/*  77:137 */               return;
/*  78:    */             }
/*  79:    */           }
/*  80:140 */           stmtList(tk);
/*  81:    */         }
/*  82:141 */         else if (tk.sval.equalsIgnoreCase("graph"))
/*  83:    */         {
/*  84:142 */           System.err.println("Error. Undirected graphs cannot be used");
/*  85:    */         }
/*  86:    */         else
/*  87:    */         {
/*  88:144 */           System.err.println("Error. Expected graph or digraph at line " + tk.lineno());
/*  89:    */         }
/*  90:    */       }
/*  91:    */     }
/*  92:    */     catch (Exception ex)
/*  93:    */     {
/*  94:149 */       ex.printStackTrace();
/*  95:    */     }
/*  96:163 */     int[] noOfEdgesOfNode = new int[this.m_nodes.size()];
/*  97:164 */     int[] noOfPrntsOfNode = new int[this.m_nodes.size()];
/*  98:165 */     for (int i = 0; i < this.m_edges.size(); i++)
/*  99:    */     {
/* 100:166 */       GraphEdge e = (GraphEdge)this.m_edges.get(i);
/* 101:167 */       noOfEdgesOfNode[e.src] += 1;
/* 102:168 */       noOfPrntsOfNode[e.dest] += 1;
/* 103:    */     }
/* 104:170 */     for (int i = 0; i < this.m_edges.size(); i++)
/* 105:    */     {
/* 106:171 */       GraphEdge e = (GraphEdge)this.m_edges.get(i);
/* 107:172 */       GraphNode n = (GraphNode)this.m_nodes.get(e.src);
/* 108:173 */       GraphNode n2 = (GraphNode)this.m_nodes.get(e.dest);
/* 109:174 */       if (n.edges == null)
/* 110:    */       {
/* 111:175 */         n.edges = new int[noOfEdgesOfNode[e.src]][2];
/* 112:176 */         for (int k = 0; k < n.edges.length; k++) {
/* 113:177 */           n.edges[k][1] = 0;
/* 114:    */         }
/* 115:    */       }
/* 116:180 */       if (n2.prnts == null)
/* 117:    */       {
/* 118:181 */         n2.prnts = new int[noOfPrntsOfNode[e.dest]];
/* 119:182 */         for (int k = 0; k < n2.prnts.length; k++) {
/* 120:183 */           n2.prnts[k] = -1;
/* 121:    */         }
/* 122:    */       }
/* 123:186 */       int k = 0;
/* 124:187 */       while (n.edges[k][1] != 0) {
/* 125:188 */         k++;
/* 126:    */       }
/* 127:190 */       n.edges[k][0] = e.dest;
/* 128:191 */       n.edges[k][1] = e.type;
/* 129:    */       
/* 130:193 */       k = 0;
/* 131:194 */       while (n2.prnts[k] != -1) {
/* 132:195 */         k++;
/* 133:    */       }
/* 134:197 */       n2.prnts[k] = e.src;
/* 135:    */     }
/* 136:    */   }
/* 137:    */   
/* 138:    */   protected void stmtList(StreamTokenizer tk)
/* 139:    */     throws Exception
/* 140:    */   {
/* 141:202 */     tk.nextToken();
/* 142:203 */     if ((tk.ttype == 125) || (tk.ttype == -1)) {
/* 143:204 */       return;
/* 144:    */     }
/* 145:206 */     stmt(tk);
/* 146:207 */     stmtList(tk);
/* 147:    */   }
/* 148:    */   
/* 149:    */   protected void stmt(StreamTokenizer tk)
/* 150:    */   {
/* 151:214 */     if ((!tk.sval.equalsIgnoreCase("graph")) && (!tk.sval.equalsIgnoreCase("node")) && (!tk.sval.equalsIgnoreCase("edge"))) {
/* 152:    */       try
/* 153:    */       {
/* 154:219 */         nodeID(tk);
/* 155:220 */         int nodeindex = this.m_nodes.indexOf(new GraphNode(tk.sval, null));
/* 156:221 */         tk.nextToken();
/* 157:223 */         if (tk.ttype == 91) {
/* 158:224 */           nodeStmt(tk, nodeindex);
/* 159:225 */         } else if (tk.ttype == 45) {
/* 160:226 */           edgeStmt(tk, nodeindex);
/* 161:    */         } else {
/* 162:228 */           System.err.println("error at lineno " + tk.lineno() + " in stmt");
/* 163:    */         }
/* 164:    */       }
/* 165:    */       catch (Exception ex)
/* 166:    */       {
/* 167:231 */         System.err.println("error at lineno " + tk.lineno() + " in stmtException");
/* 168:    */         
/* 169:233 */         ex.printStackTrace();
/* 170:    */       }
/* 171:    */     }
/* 172:    */   }
/* 173:    */   
/* 174:    */   protected void nodeID(StreamTokenizer tk)
/* 175:    */     throws Exception
/* 176:    */   {
/* 177:240 */     if ((tk.ttype == 34) || (tk.ttype == -3) || ((tk.ttype >= 97) && (tk.ttype <= 122)) || ((tk.ttype >= 65) && (tk.ttype <= 90)))
/* 178:    */     {
/* 179:243 */       if ((this.m_nodes != null) && (!this.m_nodes.contains(new GraphNode(tk.sval, null)))) {
/* 180:244 */         this.m_nodes.add(new GraphNode(tk.sval, tk.sval));
/* 181:    */       }
/* 182:    */     }
/* 183:    */     else {
/* 184:248 */       throw new Exception();
/* 185:    */     }
/* 186:    */   }
/* 187:    */   
/* 188:    */   protected void nodeStmt(StreamTokenizer tk, int nindex)
/* 189:    */     throws Exception
/* 190:    */   {
/* 191:256 */     tk.nextToken();
/* 192:    */     
/* 193:258 */     GraphNode temp = (GraphNode)this.m_nodes.get(nindex);
/* 194:260 */     if ((tk.ttype == 93) || (tk.ttype == -1)) {
/* 195:261 */       return;
/* 196:    */     }
/* 197:262 */     if (tk.ttype == -3) {
/* 198:264 */       if (tk.sval.equalsIgnoreCase("label"))
/* 199:    */       {
/* 200:266 */         tk.nextToken();
/* 201:267 */         if (tk.ttype == 61)
/* 202:    */         {
/* 203:268 */           tk.nextToken();
/* 204:269 */           if ((tk.ttype == -3) || (tk.ttype == 34))
/* 205:    */           {
/* 206:270 */             temp.lbl = tk.sval;
/* 207:    */           }
/* 208:    */           else
/* 209:    */           {
/* 210:272 */             System.err.println("couldn't find label at line " + tk.lineno());
/* 211:273 */             tk.pushBack();
/* 212:    */           }
/* 213:    */         }
/* 214:    */         else
/* 215:    */         {
/* 216:276 */           System.err.println("couldn't find label at line " + tk.lineno());
/* 217:277 */           tk.pushBack();
/* 218:    */         }
/* 219:    */       }
/* 220:281 */       else if (tk.sval.equalsIgnoreCase("color"))
/* 221:    */       {
/* 222:283 */         tk.nextToken();
/* 223:284 */         if (tk.ttype == 61)
/* 224:    */         {
/* 225:285 */           tk.nextToken();
/* 226:286 */           if ((tk.ttype != -3) && (tk.ttype != 34))
/* 227:    */           {
/* 228:289 */             System.err.println("couldn't find color at line " + tk.lineno());
/* 229:290 */             tk.pushBack();
/* 230:    */           }
/* 231:    */         }
/* 232:    */         else
/* 233:    */         {
/* 234:293 */           System.err.println("couldn't find color at line " + tk.lineno());
/* 235:294 */           tk.pushBack();
/* 236:    */         }
/* 237:    */       }
/* 238:298 */       else if (tk.sval.equalsIgnoreCase("style"))
/* 239:    */       {
/* 240:300 */         tk.nextToken();
/* 241:301 */         if (tk.ttype == 61)
/* 242:    */         {
/* 243:302 */           tk.nextToken();
/* 244:303 */           if ((tk.ttype != -3) && (tk.ttype != 34))
/* 245:    */           {
/* 246:306 */             System.err.println("couldn't find style at line " + tk.lineno());
/* 247:307 */             tk.pushBack();
/* 248:    */           }
/* 249:    */         }
/* 250:    */         else
/* 251:    */         {
/* 252:310 */           System.err.println("couldn't find style at line " + tk.lineno());
/* 253:311 */           tk.pushBack();
/* 254:    */         }
/* 255:    */       }
/* 256:    */     }
/* 257:315 */     nodeStmt(tk, nindex);
/* 258:    */   }
/* 259:    */   
/* 260:    */   protected void edgeStmt(StreamTokenizer tk, int nindex)
/* 261:    */     throws Exception
/* 262:    */   {
/* 263:320 */     tk.nextToken();
/* 264:    */     
/* 265:322 */     GraphEdge e = null;
/* 266:323 */     if (tk.ttype == 62)
/* 267:    */     {
/* 268:324 */       tk.nextToken();
/* 269:325 */       if (tk.ttype == 123) {
/* 270:    */         for (;;)
/* 271:    */         {
/* 272:327 */           tk.nextToken();
/* 273:328 */           if (tk.ttype == 125) {
/* 274:    */             break;
/* 275:    */           }
/* 276:331 */           nodeID(tk);
/* 277:332 */           e = new GraphEdge(nindex, this.m_nodes.indexOf(new GraphNode(tk.sval, null)), 1);
/* 278:334 */           if ((this.m_edges != null) && (!this.m_edges.contains(e))) {
/* 279:335 */             this.m_edges.add(e);
/* 280:    */           }
/* 281:    */         }
/* 282:    */       }
/* 283:344 */       nodeID(tk);
/* 284:345 */       e = new GraphEdge(nindex, this.m_nodes.indexOf(new GraphNode(tk.sval, null)), 1);
/* 285:347 */       if ((this.m_edges != null) && (!this.m_edges.contains(e))) {
/* 286:348 */         this.m_edges.add(e);
/* 287:    */       }
/* 288:    */     }
/* 289:    */     else
/* 290:    */     {
/* 291:354 */       if (tk.ttype == 45)
/* 292:    */       {
/* 293:355 */         System.err.println("Error at line " + tk.lineno() + ". Cannot deal with undirected edges");
/* 294:357 */         if (tk.ttype == -3) {
/* 295:358 */           tk.pushBack();
/* 296:    */         }
/* 297:360 */         return;
/* 298:    */       }
/* 299:362 */       System.err.println("Error at line " + tk.lineno() + " in edgeStmt");
/* 300:363 */       if (tk.ttype == -3) {
/* 301:364 */         tk.pushBack();
/* 302:    */       }
/* 303:366 */       return;
/* 304:    */     }
/* 305:369 */     tk.nextToken();
/* 306:371 */     if (tk.ttype == 91) {
/* 307:372 */       edgeAttrib(tk, e);
/* 308:    */     } else {
/* 309:374 */       tk.pushBack();
/* 310:    */     }
/* 311:    */   }
/* 312:    */   
/* 313:    */   protected void edgeAttrib(StreamTokenizer tk, GraphEdge e)
/* 314:    */     throws Exception
/* 315:    */   {
/* 316:380 */     tk.nextToken();
/* 317:382 */     if ((tk.ttype == 93) || (tk.ttype == -1)) {
/* 318:383 */       return;
/* 319:    */     }
/* 320:384 */     if (tk.ttype == -3) {
/* 321:386 */       if (tk.sval.equalsIgnoreCase("label"))
/* 322:    */       {
/* 323:388 */         tk.nextToken();
/* 324:389 */         if (tk.ttype == 61)
/* 325:    */         {
/* 326:390 */           tk.nextToken();
/* 327:391 */           if ((tk.ttype == -3) || (tk.ttype == 34))
/* 328:    */           {
/* 329:392 */             System.err.println("found label " + tk.sval);
/* 330:    */           }
/* 331:    */           else
/* 332:    */           {
/* 333:394 */             System.err.println("couldn't find label at line " + tk.lineno());
/* 334:395 */             tk.pushBack();
/* 335:    */           }
/* 336:    */         }
/* 337:    */         else
/* 338:    */         {
/* 339:398 */           System.err.println("couldn't find label at line " + tk.lineno());
/* 340:399 */           tk.pushBack();
/* 341:    */         }
/* 342:    */       }
/* 343:401 */       else if (tk.sval.equalsIgnoreCase("color"))
/* 344:    */       {
/* 345:403 */         tk.nextToken();
/* 346:404 */         if (tk.ttype == 61)
/* 347:    */         {
/* 348:405 */           tk.nextToken();
/* 349:406 */           if ((tk.ttype != -3) && (tk.ttype != 34))
/* 350:    */           {
/* 351:409 */             System.err.println("couldn't find color at line " + tk.lineno());
/* 352:410 */             tk.pushBack();
/* 353:    */           }
/* 354:    */         }
/* 355:    */         else
/* 356:    */         {
/* 357:413 */           System.err.println("couldn't find color at line " + tk.lineno());
/* 358:414 */           tk.pushBack();
/* 359:    */         }
/* 360:    */       }
/* 361:418 */       else if (tk.sval.equalsIgnoreCase("style"))
/* 362:    */       {
/* 363:420 */         tk.nextToken();
/* 364:421 */         if (tk.ttype == 61)
/* 365:    */         {
/* 366:422 */           tk.nextToken();
/* 367:423 */           if ((tk.ttype != -3) && (tk.ttype != 34))
/* 368:    */           {
/* 369:426 */             System.err.println("couldn't find style at line " + tk.lineno());
/* 370:427 */             tk.pushBack();
/* 371:    */           }
/* 372:    */         }
/* 373:    */         else
/* 374:    */         {
/* 375:430 */           System.err.println("couldn't find style at line " + tk.lineno());
/* 376:431 */           tk.pushBack();
/* 377:    */         }
/* 378:    */       }
/* 379:    */     }
/* 380:435 */     edgeAttrib(tk, e);
/* 381:    */   }
/* 382:    */   
/* 383:    */   public static void writeDOT(String filename, String graphName, ArrayList<GraphNode> nodes, ArrayList<GraphEdge> edges)
/* 384:    */   {
/* 385:    */     try
/* 386:    */     {
/* 387:451 */       FileWriter os = new FileWriter(filename);
/* 388:452 */       os.write("digraph ", 0, "digraph ".length());
/* 389:453 */       if (graphName != null) {
/* 390:454 */         os.write(graphName + " ", 0, graphName.length() + 1);
/* 391:    */       }
/* 392:456 */       os.write("{\n", 0, "{\n".length());
/* 393:459 */       for (int i = 0; i < edges.size(); i++)
/* 394:    */       {
/* 395:460 */         GraphEdge e = (GraphEdge)edges.get(i);
/* 396:461 */         os.write(((GraphNode)nodes.get(e.src)).ID, 0, ((GraphNode)nodes.get(e.src)).ID.length());
/* 397:462 */         os.write("->", 0, "->".length());
/* 398:463 */         os.write(((GraphNode)nodes.get(e.dest)).ID + "\n", 0, ((GraphNode)nodes.get(e.dest)).ID.length() + 1);
/* 399:    */       }
/* 400:466 */       os.write("}\n", 0, "}\n".length());
/* 401:467 */       os.close();
/* 402:    */     }
/* 403:    */     catch (IOException ex)
/* 404:    */     {
/* 405:469 */       ex.printStackTrace();
/* 406:    */     }
/* 407:    */   }
/* 408:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.graphvisualizer.DotParser
 * JD-Core Version:    0.7.0.1
 */