/*   1:    */ package weka.classifiers.bayes.net.search.ci;
/*   2:    */ 
/*   3:    */ import java.io.FileReader;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.util.Collections;
/*   6:    */ import java.util.Enumeration;
/*   7:    */ import java.util.Vector;
/*   8:    */ import weka.classifiers.bayes.BayesNet;
/*   9:    */ import weka.classifiers.bayes.net.ParentSet;
/*  10:    */ import weka.core.Attribute;
/*  11:    */ import weka.core.Instances;
/*  12:    */ import weka.core.Option;
/*  13:    */ import weka.core.RevisionHandler;
/*  14:    */ import weka.core.RevisionUtils;
/*  15:    */ import weka.core.Utils;
/*  16:    */ 
/*  17:    */ public class ICSSearchAlgorithm
/*  18:    */   extends CISearchAlgorithm
/*  19:    */ {
/*  20:    */   static final long serialVersionUID = -2510985917284798576L;
/*  21:    */   private int m_nMaxCardinality;
/*  22:    */   
/*  23:    */   String name(int iAttribute)
/*  24:    */   {
/*  25: 85 */     return this.m_instances.attribute(iAttribute).name();
/*  26:    */   }
/*  27:    */   
/*  28:    */   int maxn()
/*  29:    */   {
/*  30: 94 */     return this.m_instances.numAttributes();
/*  31:    */   }
/*  32:    */   
/*  33:    */   public ICSSearchAlgorithm()
/*  34:    */   {
/*  35: 98 */     this.m_nMaxCardinality = 2;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public void setMaxCardinality(int nMaxCardinality)
/*  39:    */   {
/*  40:106 */     this.m_nMaxCardinality = nMaxCardinality;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public int getMaxCardinality()
/*  44:    */   {
/*  45:115 */     return this.m_nMaxCardinality;
/*  46:    */   }
/*  47:    */   
/*  48:    */   class SeparationSet
/*  49:    */     implements RevisionHandler
/*  50:    */   {
/*  51:    */     public int[] m_set;
/*  52:    */     
/*  53:    */     public SeparationSet()
/*  54:    */     {
/*  55:126 */       this.m_set = new int[ICSSearchAlgorithm.this.getMaxCardinality() + 1];
/*  56:    */     }
/*  57:    */     
/*  58:    */     public boolean contains(int nItem)
/*  59:    */     {
/*  60:130 */       for (int iItem = 0; (iItem < ICSSearchAlgorithm.this.getMaxCardinality()) && (this.m_set[iItem] != -1); iItem++) {
/*  61:131 */         if (this.m_set[iItem] == nItem) {
/*  62:132 */           return true;
/*  63:    */         }
/*  64:    */       }
/*  65:135 */       return false;
/*  66:    */     }
/*  67:    */     
/*  68:    */     public String getRevision()
/*  69:    */     {
/*  70:145 */       return RevisionUtils.extract("$Revision: 10154 $");
/*  71:    */     }
/*  72:    */   }
/*  73:    */   
/*  74:    */   protected void search(BayesNet bayesNet, Instances instances)
/*  75:    */     throws Exception
/*  76:    */   {
/*  77:161 */     this.m_BayesNet = bayesNet;
/*  78:162 */     this.m_instances = instances;
/*  79:    */     
/*  80:164 */     boolean[][] edges = new boolean[maxn() + 1][];
/*  81:165 */     boolean[][] arrows = new boolean[maxn() + 1][];
/*  82:166 */     SeparationSet[][] sepsets = new SeparationSet[maxn() + 1][];
/*  83:167 */     for (int iNode = 0; iNode < maxn() + 1; iNode++)
/*  84:    */     {
/*  85:168 */       edges[iNode] = new boolean[maxn()];
/*  86:169 */       arrows[iNode] = new boolean[maxn()];
/*  87:170 */       sepsets[iNode] = new SeparationSet[maxn()];
/*  88:    */     }
/*  89:173 */     calcDependencyGraph(edges, sepsets);
/*  90:174 */     calcVeeNodes(edges, arrows, sepsets);
/*  91:175 */     calcArcDirections(edges, arrows);
/*  92:178 */     for (int iNode = 0; iNode < maxn(); iNode++)
/*  93:    */     {
/*  94:180 */       ParentSet oParentSet = this.m_BayesNet.getParentSet(iNode);
/*  95:181 */       while (oParentSet.getNrOfParents() > 0) {
/*  96:182 */         oParentSet.deleteLastParent(this.m_instances);
/*  97:    */       }
/*  98:184 */       for (int iParent = 0; iParent < maxn(); iParent++) {
/*  99:185 */         if (arrows[iParent][iNode] != 0) {
/* 100:186 */           oParentSet.addParent(iParent, this.m_instances);
/* 101:    */         }
/* 102:    */       }
/* 103:    */     }
/* 104:    */   }
/* 105:    */   
/* 106:    */   void calcDependencyGraph(boolean[][] edges, SeparationSet[][] sepsets)
/* 107:    */   {
/* 108:206 */     for (int iNode1 = 0; iNode1 < maxn(); iNode1++) {
/* 109:208 */       for (int iNode2 = 0; iNode2 < maxn(); iNode2++) {
/* 110:209 */         edges[iNode1][iNode2] = 1;
/* 111:    */       }
/* 112:    */     }
/* 113:212 */     for (int iNode1 = 0; iNode1 < maxn(); iNode1++) {
/* 114:213 */       edges[iNode1][iNode1] = 0;
/* 115:    */     }
/* 116:216 */     for (int iCardinality = 0; iCardinality <= getMaxCardinality(); iCardinality++)
/* 117:    */     {
/* 118:217 */       for (int iNode1 = 0; iNode1 <= maxn() - 2; iNode1++) {
/* 119:218 */         for (int iNode2 = iNode1 + 1; iNode2 < maxn(); iNode2++) {
/* 120:219 */           if (edges[iNode1][iNode2] != 0)
/* 121:    */           {
/* 122:220 */             SeparationSet oSepSet = existsSepSet(iNode1, iNode2, iCardinality, edges);
/* 123:221 */             if (oSepSet != null)
/* 124:    */             {
/* 125:222 */               edges[iNode1][iNode2] = 0;
/* 126:223 */               edges[iNode2][iNode1] = 0;
/* 127:224 */               sepsets[iNode1][iNode2] = oSepSet;
/* 128:225 */               sepsets[iNode2][iNode1] = oSepSet;
/* 129:    */               
/* 130:227 */               System.err.print("I(" + name(iNode1) + ", {");
/* 131:228 */               for (int iNode3 = 0; iNode3 < iCardinality; iNode3++) {
/* 132:229 */                 System.err.print(name(oSepSet.m_set[iNode3]) + " ");
/* 133:    */               }
/* 134:231 */               System.err.print("} ," + name(iNode2) + ")\n");
/* 135:    */             }
/* 136:    */           }
/* 137:    */         }
/* 138:    */       }
/* 139:237 */       System.err.print(iCardinality + " ");
/* 140:238 */       for (int iNode1 = 0; iNode1 < maxn(); iNode1++) {
/* 141:239 */         System.err.print(name(iNode1) + " ");
/* 142:    */       }
/* 143:241 */       System.err.print('\n');
/* 144:242 */       for (int iNode1 = 0; iNode1 < maxn(); iNode1++)
/* 145:    */       {
/* 146:243 */         for (int iNode2 = 0; iNode2 < maxn(); iNode2++) {
/* 147:244 */           if (edges[iNode1][iNode2] != 0) {
/* 148:245 */             System.err.print("X ");
/* 149:    */           } else {
/* 150:247 */             System.err.print(". ");
/* 151:    */           }
/* 152:    */         }
/* 153:250 */         System.err.print(name(iNode1) + " ");
/* 154:251 */         System.err.print('\n');
/* 155:    */       }
/* 156:    */     }
/* 157:    */   }
/* 158:    */   
/* 159:    */   SeparationSet existsSepSet(int iNode1, int iNode2, int nCardinality, boolean[][] edges)
/* 160:    */   {
/* 161:273 */     SeparationSet Z = new SeparationSet();
/* 162:274 */     Z.m_set[nCardinality] = -1;
/* 163:280 */     if (nCardinality > 0)
/* 164:    */     {
/* 165:281 */       Z.m_set[0] = next(-1, iNode1, iNode2, edges);
/* 166:282 */       int iNode3 = 1;
/* 167:283 */       while (iNode3 < nCardinality)
/* 168:    */       {
/* 169:284 */         Z.m_set[iNode3] = next(Z.m_set[(iNode3 - 1)], iNode1, iNode2, edges);
/* 170:285 */         iNode3++;
/* 171:    */       }
/* 172:    */     }
/* 173:    */     int iZ;
/* 174:    */     int iZ;
/* 175:289 */     if (nCardinality > 0) {
/* 176:290 */       iZ = maxn() - Z.m_set[(nCardinality - 1)] - 1;
/* 177:    */     } else {
/* 178:292 */       iZ = 0;
/* 179:    */     }
/* 180:    */     label298:
/* 181:295 */     while (iZ >= 0)
/* 182:    */     {
/* 183:297 */       if (isConditionalIndependent(iNode2, iNode1, Z.m_set, nCardinality)) {
/* 184:298 */         return Z;
/* 185:    */       }
/* 186:301 */       if (nCardinality > 0) {
/* 187:302 */         Z.m_set[(nCardinality - 1)] = next(Z.m_set[(nCardinality - 1)], iNode1, iNode2, edges);
/* 188:    */       }
/* 189:305 */       iZ = nCardinality - 1;
/* 190:    */       for (;;)
/* 191:    */       {
/* 192:306 */         if ((iZ < 0) || (Z.m_set[iZ] < maxn())) {
/* 193:    */           break label298;
/* 194:    */         }
/* 195:307 */         iZ = nCardinality - 1;
/* 196:308 */         while ((iZ >= 0) && (Z.m_set[iZ] >= maxn())) {
/* 197:309 */           iZ--;
/* 198:    */         }
/* 199:311 */         if (iZ < 0) {
/* 200:    */           break;
/* 201:    */         }
/* 202:314 */         Z.m_set[iZ] = next(Z.m_set[iZ], iNode1, iNode2, edges);
/* 203:315 */         for (int iNode3 = iZ + 1; iNode3 < nCardinality; iNode3++) {
/* 204:316 */           Z.m_set[iNode3] = next(Z.m_set[(iNode3 - 1)], iNode1, iNode2, edges);
/* 205:    */         }
/* 206:318 */         iZ = nCardinality - 1;
/* 207:    */       }
/* 208:    */     }
/* 209:322 */     return null;
/* 210:    */   }
/* 211:    */   
/* 212:    */   int next(int x, int iNode1, int iNode2, boolean[][] edges)
/* 213:    */   {
/* 214:    */     
/* 215:338 */     while ((x < maxn()) && ((edges[iNode1][x] == 0) || (edges[iNode2][x] == 0) || (x == iNode2))) {
/* 216:339 */       x++;
/* 217:    */     }
/* 218:341 */     return x;
/* 219:    */   }
/* 220:    */   
/* 221:    */   void calcVeeNodes(boolean[][] edges, boolean[][] arrows, SeparationSet[][] sepsets)
/* 222:    */   {
/* 223:359 */     for (int iNode1 = 0; iNode1 < maxn(); iNode1++) {
/* 224:360 */       for (int iNode2 = 0; iNode2 < maxn(); iNode2++) {
/* 225:361 */         arrows[iNode1][iNode2] = 0;
/* 226:    */       }
/* 227:    */     }
/* 228:365 */     for (int iNode1 = 0; iNode1 < maxn() - 1; iNode1++) {
/* 229:366 */       for (int iNode2 = iNode1 + 1; iNode2 < maxn(); iNode2++) {
/* 230:367 */         if (edges[iNode1][iNode2] == 0) {
/* 231:368 */           for (int iNode3 = 0; iNode3 < maxn(); iNode3++) {
/* 232:369 */             if ((((iNode3 != iNode1) && (iNode3 != iNode2) && (edges[iNode1][iNode3] != 0) && (edges[iNode2][iNode3] != 0) ? 1 : 0) & (!sepsets[iNode1][iNode2].contains(iNode3) ? 1 : 0)) != 0)
/* 233:    */             {
/* 234:371 */               arrows[iNode1][iNode3] = 1;
/* 235:372 */               arrows[iNode2][iNode3] = 1;
/* 236:    */             }
/* 237:    */           }
/* 238:    */         }
/* 239:    */       }
/* 240:    */     }
/* 241:    */   }
/* 242:    */   
/* 243:    */   void calcArcDirections(boolean[][] edges, boolean[][] arrows)
/* 244:    */   {
/* 245:    */     boolean bFound;
/* 246:    */     do
/* 247:    */     {
/* 248:398 */       bFound = false;
/* 249:402 */       for (int i = 0; i < maxn(); i++) {
/* 250:403 */         for (int j = 0; j < maxn(); j++) {
/* 251:404 */           if ((i != j) && (arrows[i][j] != 0)) {
/* 252:405 */             for (int k = 0; k < maxn(); k++) {
/* 253:406 */               if ((i != k) && (j != k) && (edges[j][k] != 0) && (edges[i][k] == 0) && (arrows[j][k] == 0) && (arrows[k][j] == 0))
/* 254:    */               {
/* 255:408 */                 arrows[j][k] = 1;
/* 256:409 */                 bFound = true;
/* 257:    */               }
/* 258:    */             }
/* 259:    */           }
/* 260:    */         }
/* 261:    */       }
/* 262:418 */       for (i = 0; i < maxn(); i++) {
/* 263:419 */         for (int j = 0; j < maxn(); j++) {
/* 264:420 */           if ((i != j) && (arrows[i][j] != 0)) {
/* 265:421 */             for (int k = 0; k < maxn(); k++) {
/* 266:422 */               if ((i != k) && (j != k) && (edges[i][k] != 0) && (arrows[j][k] != 0) && (arrows[i][k] == 0) && (arrows[k][i] == 0))
/* 267:    */               {
/* 268:424 */                 arrows[i][k] = 1;
/* 269:425 */                 bFound = true;
/* 270:    */               }
/* 271:    */             }
/* 272:    */           }
/* 273:    */         }
/* 274:    */       }
/* 275:435 */       for (i = 0; i < maxn(); i++) {
/* 276:436 */         for (int j = 0; j < maxn(); j++) {
/* 277:437 */           if ((i != j) && (arrows[i][j] != 0)) {
/* 278:438 */             for (int k = 0; k < maxn(); k++) {
/* 279:439 */               if ((k != i) && (k != j) && (arrows[k][j] != 0) && (edges[k][i] == 0)) {
/* 280:440 */                 for (int m = 0; m < maxn(); m++) {
/* 281:441 */                   if ((m != i) && (m != j) && (m != k) && (edges[m][i] != 0) && (arrows[m][i] == 0) && (arrows[i][m] == 0) && (edges[m][j] != 0) && (arrows[m][j] == 0) && (arrows[j][m] == 0) && (edges[m][k] != 0) && (arrows[m][k] == 0) && (arrows[k][m] == 0))
/* 282:    */                   {
/* 283:445 */                     arrows[m][j] = 1;
/* 284:446 */                     bFound = true;
/* 285:    */                   }
/* 286:    */                 }
/* 287:    */               }
/* 288:    */             }
/* 289:    */           }
/* 290:    */         }
/* 291:    */       }
/* 292:458 */       for (i = 0; i < maxn(); i++) {
/* 293:459 */         for (int j = 0; j < maxn(); j++) {
/* 294:460 */           if ((i != j) && (arrows[j][i] != 0)) {
/* 295:461 */             for (int k = 0; k < maxn(); k++) {
/* 296:462 */               if ((k != i) && (k != j) && (edges[k][j] != 0) && (arrows[k][j] == 0) && (arrows[j][k] == 0) && (edges[k][i] != 0) && (arrows[k][i] == 0) && (arrows[i][k] == 0)) {
/* 297:465 */                 for (int m = 0; m < maxn(); m++) {
/* 298:466 */                   if ((m != i) && (m != j) && (m != k) && (edges[m][i] != 0) && (arrows[m][i] == 0) && (arrows[i][m] == 0) && (edges[m][k] != 0) && (arrows[m][k] == 0) && (arrows[k][m] == 0))
/* 299:    */                   {
/* 300:469 */                     arrows[i][m] = 1;
/* 301:470 */                     arrows[k][m] = 1;
/* 302:471 */                     bFound = true;
/* 303:    */                   }
/* 304:    */                 }
/* 305:    */               }
/* 306:    */             }
/* 307:    */           }
/* 308:    */         }
/* 309:    */       }
/* 310:485 */       if (!bFound)
/* 311:    */       {
/* 312:486 */         i = 0;
/* 313:487 */         while ((!bFound) && (i < maxn()))
/* 314:    */         {
/* 315:488 */           int j = 0;
/* 316:489 */           while ((!bFound) && (j < maxn()))
/* 317:    */           {
/* 318:490 */             if ((edges[i][j] != 0) && (arrows[i][j] == 0) && (arrows[j][i] == 0))
/* 319:    */             {
/* 320:491 */               arrows[i][j] = 1;
/* 321:492 */               bFound = true;
/* 322:    */             }
/* 323:494 */             j++;
/* 324:    */           }
/* 325:496 */           i++;
/* 326:    */         }
/* 327:    */       }
/* 328:500 */     } while (bFound);
/* 329:    */   }
/* 330:    */   
/* 331:    */   public Enumeration<Option> listOptions()
/* 332:    */   {
/* 333:511 */     Vector<Option> result = new Vector();
/* 334:    */     
/* 335:513 */     result.addElement(new Option("\tWhen determining whether an edge exists a search is performed \n\tfor a set Z that separates the nodes. MaxCardinality determines \n\tthe maximum size of the set Z. This greatly influences the \n\tlength of the search. (default 2)", "cardinality", 1, "-cardinality <num>"));
/* 336:    */     
/* 337:    */ 
/* 338:    */ 
/* 339:    */ 
/* 340:    */ 
/* 341:    */ 
/* 342:    */ 
/* 343:521 */     result.addAll(Collections.list(super.listOptions()));
/* 344:    */     
/* 345:523 */     return result.elements();
/* 346:    */   }
/* 347:    */   
/* 348:    */   public void setOptions(String[] options)
/* 349:    */     throws Exception
/* 350:    */   {
/* 351:563 */     String tmpStr = Utils.getOption("cardinality", options);
/* 352:564 */     if (tmpStr.length() != 0) {
/* 353:565 */       setMaxCardinality(Integer.parseInt(tmpStr));
/* 354:    */     } else {
/* 355:567 */       setMaxCardinality(2);
/* 356:    */     }
/* 357:570 */     super.setOptions(options);
/* 358:    */   }
/* 359:    */   
/* 360:    */   public String[] getOptions()
/* 361:    */   {
/* 362:580 */     Vector<String> result = new Vector();
/* 363:    */     
/* 364:582 */     result.add("-cardinality");
/* 365:583 */     result.add("" + getMaxCardinality());
/* 366:    */     
/* 367:585 */     Collections.addAll(result, super.getOptions());
/* 368:    */     
/* 369:587 */     return (String[])result.toArray(new String[result.size()]);
/* 370:    */   }
/* 371:    */   
/* 372:    */   public String maxCardinalityTipText()
/* 373:    */   {
/* 374:594 */     return "When determining whether an edge exists a search is performed for a set Z that separates the nodes. MaxCardinality determines the maximum size of the set Z. This greatly influences the length of the search. Default value is 2.";
/* 375:    */   }
/* 376:    */   
/* 377:    */   public String globalInfo()
/* 378:    */   {
/* 379:606 */     return "This Bayes Network learning algorithm uses conditional independence tests to find a skeleton, finds V-nodes and applies a set of rules to find the directions of the remaining arrows.";
/* 380:    */   }
/* 381:    */   
/* 382:    */   public String getRevision()
/* 383:    */   {
/* 384:618 */     return RevisionUtils.extract("$Revision: 10154 $");
/* 385:    */   }
/* 386:    */   
/* 387:    */   public static void main(String[] argv)
/* 388:    */   {
/* 389:    */     try
/* 390:    */     {
/* 391:628 */       BayesNet b = new BayesNet();
/* 392:629 */       b.setSearchAlgorithm(new ICSSearchAlgorithm());
/* 393:630 */       Instances instances = new Instances(new FileReader("C:\\eclipse\\workspace\\weka\\data\\contact-lenses.arff"));
/* 394:    */       
/* 395:632 */       instances.setClassIndex(instances.numAttributes() - 1);
/* 396:633 */       b.buildClassifier(instances);
/* 397:634 */       System.out.println(b.toString());
/* 398:    */     }
/* 399:    */     catch (Exception e)
/* 400:    */     {
/* 401:636 */       e.printStackTrace();
/* 402:    */     }
/* 403:    */   }
/* 404:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.bayes.net.search.ci.ICSSearchAlgorithm
 * JD-Core Version:    0.7.0.1
 */