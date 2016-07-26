/*   1:    */ package weka.core.neighboursearch;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.core.DistanceFunction;
/*   7:    */ import weka.core.EuclideanDistance;
/*   8:    */ import weka.core.Instance;
/*   9:    */ import weka.core.Instances;
/*  10:    */ import weka.core.Option;
/*  11:    */ import weka.core.RevisionUtils;
/*  12:    */ import weka.core.TechnicalInformation;
/*  13:    */ import weka.core.TechnicalInformation.Field;
/*  14:    */ import weka.core.TechnicalInformation.Type;
/*  15:    */ import weka.core.TechnicalInformationHandler;
/*  16:    */ import weka.core.Utils;
/*  17:    */ import weka.core.neighboursearch.balltrees.BallNode;
/*  18:    */ import weka.core.neighboursearch.balltrees.BallTreeConstructor;
/*  19:    */ import weka.core.neighboursearch.balltrees.TopDownConstructor;
/*  20:    */ 
/*  21:    */ public class BallTree
/*  22:    */   extends NearestNeighbourSearch
/*  23:    */   implements TechnicalInformationHandler
/*  24:    */ {
/*  25:    */   private static final long serialVersionUID = 728763855952698328L;
/*  26:    */   protected int[] m_InstList;
/*  27:110 */   protected int m_MaxInstancesInLeaf = 40;
/*  28:113 */   protected TreePerformanceStats m_TreeStats = null;
/*  29:    */   protected BallNode m_Root;
/*  30:119 */   protected BallTreeConstructor m_TreeConstructor = new TopDownConstructor();
/*  31:    */   protected double[] m_Distances;
/*  32:    */   
/*  33:    */   public BallTree()
/*  34:    */   {
/*  35:131 */     if (getMeasurePerformance()) {
/*  36:132 */       this.m_Stats = (this.m_TreeStats = new TreePerformanceStats());
/*  37:    */     }
/*  38:    */   }
/*  39:    */   
/*  40:    */   public BallTree(Instances insts)
/*  41:    */   {
/*  42:142 */     super(insts);
/*  43:143 */     if (getMeasurePerformance()) {
/*  44:144 */       this.m_Stats = (this.m_TreeStats = new TreePerformanceStats());
/*  45:    */     }
/*  46:    */   }
/*  47:    */   
/*  48:    */   public String globalInfo()
/*  49:    */   {
/*  50:154 */     return "Class implementing the BallTree/Metric Tree algorithm for nearest neighbour search.\nThe connection to dataset is only a reference. For the tree structure the indexes are stored in an array.\nSee the implementing classes of different construction methods of the trees for details on its construction.\n\nFor more information see also:\n\n" + getTechnicalInformation().toString();
/*  51:    */   }
/*  52:    */   
/*  53:    */   public TechnicalInformation getTechnicalInformation()
/*  54:    */   {
/*  55:176 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.TECHREPORT);
/*  56:177 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Stephen M. Omohundro");
/*  57:178 */     result.setValue(TechnicalInformation.Field.YEAR, "1989");
/*  58:179 */     result.setValue(TechnicalInformation.Field.TITLE, "Five Balltree Construction Algorithms");
/*  59:180 */     result.setValue(TechnicalInformation.Field.MONTH, "December");
/*  60:181 */     result.setValue(TechnicalInformation.Field.NUMBER, "TR-89-063");
/*  61:182 */     result.setValue(TechnicalInformation.Field.INSTITUTION, "International Computer Science Institute");
/*  62:    */     
/*  63:184 */     TechnicalInformation additional = result.add(TechnicalInformation.Type.ARTICLE);
/*  64:185 */     additional.setValue(TechnicalInformation.Field.AUTHOR, "Jeffrey K. Uhlmann");
/*  65:186 */     additional.setValue(TechnicalInformation.Field.TITLE, "Satisfying general proximity/similarity queries with metric trees");
/*  66:187 */     additional.setValue(TechnicalInformation.Field.JOURNAL, "Information Processing Letters");
/*  67:188 */     additional.setValue(TechnicalInformation.Field.MONTH, "November");
/*  68:189 */     additional.setValue(TechnicalInformation.Field.YEAR, "1991");
/*  69:190 */     additional.setValue(TechnicalInformation.Field.NUMBER, "4");
/*  70:191 */     additional.setValue(TechnicalInformation.Field.VOLUME, "40");
/*  71:192 */     additional.setValue(TechnicalInformation.Field.PAGES, "175-179");
/*  72:    */     
/*  73:194 */     return result;
/*  74:    */   }
/*  75:    */   
/*  76:    */   protected void buildTree()
/*  77:    */     throws Exception
/*  78:    */   {
/*  79:210 */     if (this.m_Instances == null) {
/*  80:211 */       throw new Exception("No instances supplied yet. Have to call setInstances(instances) with a set of Instances first.");
/*  81:    */     }
/*  82:214 */     this.m_InstList = new int[this.m_Instances.numInstances()];
/*  83:216 */     for (int i = 0; i < this.m_InstList.length; i++) {
/*  84:217 */       this.m_InstList[i] = i;
/*  85:    */     }
/*  86:220 */     this.m_DistanceFunction.setInstances(this.m_Instances);
/*  87:221 */     this.m_TreeConstructor.setInstances(this.m_Instances);
/*  88:222 */     this.m_TreeConstructor.setInstanceList(this.m_InstList);
/*  89:223 */     this.m_TreeConstructor.setEuclideanDistanceFunction((EuclideanDistance)this.m_DistanceFunction);
/*  90:    */     
/*  91:    */ 
/*  92:226 */     this.m_Root = this.m_TreeConstructor.buildTree();
/*  93:    */   }
/*  94:    */   
/*  95:    */   public Instances kNearestNeighbours(Instance target, int k)
/*  96:    */     throws Exception
/*  97:    */   {
/*  98:243 */     NearestNeighbourSearch.MyHeap heap = new NearestNeighbourSearch.MyHeap(this, k);
/*  99:245 */     if (this.m_Stats != null) {
/* 100:246 */       this.m_Stats.searchStart();
/* 101:    */     }
/* 102:248 */     nearestNeighbours(heap, this.m_Root, target, k);
/* 103:250 */     if (this.m_Stats != null) {
/* 104:251 */       this.m_Stats.searchFinish();
/* 105:    */     }
/* 106:253 */     Instances neighbours = new Instances(this.m_Instances, heap.totalSize());
/* 107:254 */     this.m_Distances = new double[heap.totalSize()];
/* 108:255 */     int[] indices = new int[heap.totalSize()];
/* 109:256 */     int i = 1;
/* 110:257 */     while (heap.noOfKthNearest() > 0)
/* 111:    */     {
/* 112:258 */       NearestNeighbourSearch.MyHeapElement h = heap.getKthNearest();
/* 113:259 */       indices[(indices.length - i)] = h.index;
/* 114:260 */       this.m_Distances[(indices.length - i)] = h.distance;
/* 115:261 */       i++;
/* 116:    */     }
/* 117:263 */     while (heap.size() > 0)
/* 118:    */     {
/* 119:264 */       NearestNeighbourSearch.MyHeapElement h = heap.get();
/* 120:265 */       indices[(indices.length - i)] = h.index;
/* 121:266 */       this.m_Distances[(indices.length - i)] = h.distance;
/* 122:267 */       i++;
/* 123:    */     }
/* 124:270 */     this.m_DistanceFunction.postProcessDistances(this.m_Distances);
/* 125:272 */     for (i = 0; i < indices.length; i++) {
/* 126:273 */       neighbours.add(this.m_Instances.instance(indices[i]));
/* 127:    */     }
/* 128:275 */     return neighbours;
/* 129:    */   }
/* 130:    */   
/* 131:    */   protected void nearestNeighbours(NearestNeighbourSearch.MyHeap heap, BallNode node, Instance target, int k)
/* 132:    */     throws Exception
/* 133:    */   {
/* 134:293 */     double distance = (-1.0D / 0.0D);
/* 135:295 */     if (heap.totalSize() >= k) {
/* 136:296 */       distance = this.m_DistanceFunction.distance(target, node.getPivot());
/* 137:    */     }
/* 138:299 */     if ((distance > -1.0E-006D) && (Math.sqrt(heap.peek().distance) < distance - node.getRadius())) {
/* 139:301 */       return;
/* 140:    */     }
/* 141:302 */     if ((node.m_Left != null) && (node.m_Right != null))
/* 142:    */     {
/* 143:304 */       if (this.m_TreeStats != null) {
/* 144:305 */         this.m_TreeStats.incrIntNodeCount();
/* 145:    */       }
/* 146:307 */       double leftPivotDist = Math.sqrt(this.m_DistanceFunction.distance(target, node.m_Left.getPivot(), (1.0D / 0.0D)));
/* 147:    */       
/* 148:309 */       double rightPivotDist = Math.sqrt(this.m_DistanceFunction.distance(target, node.m_Right.getPivot(), (1.0D / 0.0D)));
/* 149:    */       
/* 150:311 */       double leftBallDist = leftPivotDist - node.m_Left.getRadius();
/* 151:312 */       double rightBallDist = rightPivotDist - node.m_Right.getRadius();
/* 152:314 */       if ((leftBallDist < 0.0D) && (rightBallDist < 0.0D))
/* 153:    */       {
/* 154:315 */         if (leftPivotDist < rightPivotDist)
/* 155:    */         {
/* 156:316 */           nearestNeighbours(heap, node.m_Left, target, k);
/* 157:317 */           nearestNeighbours(heap, node.m_Right, target, k);
/* 158:    */         }
/* 159:    */         else
/* 160:    */         {
/* 161:319 */           nearestNeighbours(heap, node.m_Right, target, k);
/* 162:320 */           nearestNeighbours(heap, node.m_Left, target, k);
/* 163:    */         }
/* 164:    */       }
/* 165:326 */       else if (leftBallDist < rightBallDist)
/* 166:    */       {
/* 167:327 */         nearestNeighbours(heap, node.m_Left, target, k);
/* 168:328 */         nearestNeighbours(heap, node.m_Right, target, k);
/* 169:    */       }
/* 170:    */       else
/* 171:    */       {
/* 172:330 */         nearestNeighbours(heap, node.m_Right, target, k);
/* 173:331 */         nearestNeighbours(heap, node.m_Left, target, k);
/* 174:    */       }
/* 175:    */     }
/* 176:    */     else
/* 177:    */     {
/* 178:334 */       if ((node.m_Left != null) || (node.m_Right != null)) {
/* 179:336 */         throw new Exception("Error: Only one leaf of the built ball tree is assigned. Please check code.");
/* 180:    */       }
/* 181:338 */       if ((node.m_Left == null) && (node.m_Right == null))
/* 182:    */       {
/* 183:340 */         if (this.m_TreeStats != null)
/* 184:    */         {
/* 185:341 */           this.m_TreeStats.updatePointCount(node.numInstances());
/* 186:342 */           this.m_TreeStats.incrLeafCount();
/* 187:    */         }
/* 188:344 */         for (int i = node.m_Start; i <= node.m_End; i++) {
/* 189:345 */           if (target != this.m_Instances.instance(this.m_InstList[i])) {
/* 190:347 */             if (heap.totalSize() < k)
/* 191:    */             {
/* 192:348 */               distance = this.m_DistanceFunction.distance(target, this.m_Instances.instance(this.m_InstList[i]), (1.0D / 0.0D), this.m_Stats);
/* 193:    */               
/* 194:350 */               heap.put(this.m_InstList[i], distance);
/* 195:    */             }
/* 196:    */             else
/* 197:    */             {
/* 198:352 */               NearestNeighbourSearch.MyHeapElement head = heap.peek();
/* 199:353 */               distance = this.m_DistanceFunction.distance(target, this.m_Instances.instance(this.m_InstList[i]), head.distance, this.m_Stats);
/* 200:355 */               if (distance < head.distance) {
/* 201:356 */                 heap.putBySubstitute(this.m_InstList[i], distance);
/* 202:357 */               } else if (distance == head.distance) {
/* 203:358 */                 heap.putKthNearest(this.m_InstList[i], distance);
/* 204:    */               }
/* 205:    */             }
/* 206:    */           }
/* 207:    */         }
/* 208:    */       }
/* 209:    */     }
/* 210:    */   }
/* 211:    */   
/* 212:    */   public Instance nearestNeighbour(Instance target)
/* 213:    */     throws Exception
/* 214:    */   {
/* 215:374 */     return kNearestNeighbours(target, 1).instance(0);
/* 216:    */   }
/* 217:    */   
/* 218:    */   public double[] getDistances()
/* 219:    */     throws Exception
/* 220:    */   {
/* 221:395 */     if (this.m_Distances == null) {
/* 222:396 */       throw new Exception("No distances available. Please call either kNearestNeighbours or nearestNeighbours first.");
/* 223:    */     }
/* 224:398 */     return this.m_Distances;
/* 225:    */   }
/* 226:    */   
/* 227:    */   public void update(Instance ins)
/* 228:    */     throws Exception
/* 229:    */   {
/* 230:410 */     addInstanceInfo(ins);
/* 231:411 */     this.m_InstList = this.m_TreeConstructor.addInstance(this.m_Root, ins);
/* 232:    */   }
/* 233:    */   
/* 234:    */   public void addInstanceInfo(Instance ins)
/* 235:    */   {
/* 236:423 */     if (this.m_Instances != null) {
/* 237:424 */       this.m_DistanceFunction.update(ins);
/* 238:    */     }
/* 239:    */   }
/* 240:    */   
/* 241:    */   public void setInstances(Instances insts)
/* 242:    */     throws Exception
/* 243:    */   {
/* 244:435 */     super.setInstances(insts);
/* 245:436 */     buildTree();
/* 246:    */   }
/* 247:    */   
/* 248:    */   public String ballTreeConstructorTipText()
/* 249:    */   {
/* 250:446 */     return "The tree constructor being used.";
/* 251:    */   }
/* 252:    */   
/* 253:    */   public BallTreeConstructor getBallTreeConstructor()
/* 254:    */   {
/* 255:454 */     return this.m_TreeConstructor;
/* 256:    */   }
/* 257:    */   
/* 258:    */   public void setBallTreeConstructor(BallTreeConstructor constructor)
/* 259:    */   {
/* 260:463 */     this.m_TreeConstructor = constructor;
/* 261:    */   }
/* 262:    */   
/* 263:    */   public double measureTreeSize()
/* 264:    */   {
/* 265:472 */     return this.m_TreeConstructor.getNumNodes();
/* 266:    */   }
/* 267:    */   
/* 268:    */   public double measureNumLeaves()
/* 269:    */   {
/* 270:481 */     return this.m_TreeConstructor.getNumLeaves();
/* 271:    */   }
/* 272:    */   
/* 273:    */   public double measureMaxDepth()
/* 274:    */   {
/* 275:490 */     return this.m_TreeConstructor.getMaxDepth();
/* 276:    */   }
/* 277:    */   
/* 278:    */   public Enumeration<String> enumerateMeasures()
/* 279:    */   {
/* 280:499 */     Vector<String> newVector = new Vector();
/* 281:500 */     newVector.addElement("measureTreeSize");
/* 282:501 */     newVector.addElement("measureNumLeaves");
/* 283:502 */     newVector.addElement("measureMaxDepth");
/* 284:503 */     if (this.m_Stats != null) {
/* 285:504 */       newVector.addAll(Collections.list(this.m_Stats.enumerateMeasures()));
/* 286:    */     }
/* 287:506 */     return newVector.elements();
/* 288:    */   }
/* 289:    */   
/* 290:    */   public double getMeasure(String additionalMeasureName)
/* 291:    */   {
/* 292:518 */     if (additionalMeasureName.compareToIgnoreCase("measureMaxDepth") == 0) {
/* 293:519 */       return measureMaxDepth();
/* 294:    */     }
/* 295:520 */     if (additionalMeasureName.compareToIgnoreCase("measureTreeSize") == 0) {
/* 296:521 */       return measureTreeSize();
/* 297:    */     }
/* 298:522 */     if (additionalMeasureName.compareToIgnoreCase("measureNumLeaves") == 0) {
/* 299:523 */       return measureNumLeaves();
/* 300:    */     }
/* 301:524 */     if (this.m_Stats != null) {
/* 302:525 */       return this.m_Stats.getMeasure(additionalMeasureName);
/* 303:    */     }
/* 304:527 */     throw new IllegalArgumentException(additionalMeasureName + " not supported (BallTree)");
/* 305:    */   }
/* 306:    */   
/* 307:    */   public void setMeasurePerformance(boolean measurePerformance)
/* 308:    */   {
/* 309:538 */     this.m_MeasurePerformance = measurePerformance;
/* 310:539 */     if (this.m_MeasurePerformance)
/* 311:    */     {
/* 312:540 */       if (this.m_Stats == null) {
/* 313:541 */         this.m_Stats = (this.m_TreeStats = new TreePerformanceStats());
/* 314:    */       }
/* 315:    */     }
/* 316:    */     else {
/* 317:543 */       this.m_Stats = (this.m_TreeStats = null);
/* 318:    */     }
/* 319:    */   }
/* 320:    */   
/* 321:    */   public Enumeration<Option> listOptions()
/* 322:    */   {
/* 323:552 */     Vector<Option> newVector = new Vector();
/* 324:    */     
/* 325:554 */     newVector.addElement(new Option("\tThe construction method to employ. Either TopDown or BottomUp\n\t(default: weka.core.TopDownConstructor)", "C", 1, "-C <classname and options>"));
/* 326:    */     
/* 327:    */ 
/* 328:    */ 
/* 329:    */ 
/* 330:559 */     newVector.addAll(Collections.list(super.listOptions()));
/* 331:    */     
/* 332:561 */     return newVector.elements();
/* 333:    */   }
/* 334:    */   
/* 335:    */   public void setOptions(String[] options)
/* 336:    */     throws Exception
/* 337:    */   {
/* 338:582 */     super.setOptions(options);
/* 339:    */     
/* 340:584 */     String optionString = Utils.getOption('C', options);
/* 341:585 */     if (optionString.length() != 0)
/* 342:    */     {
/* 343:586 */       String[] constructorSpec = Utils.splitOptions(optionString);
/* 344:587 */       if (constructorSpec.length == 0) {
/* 345:588 */         throw new Exception("Invalid BallTreeConstructor specification string.");
/* 346:    */       }
/* 347:590 */       String className = constructorSpec[0];
/* 348:591 */       constructorSpec[0] = "";
/* 349:    */       
/* 350:593 */       setBallTreeConstructor((BallTreeConstructor)Utils.forName(BallTreeConstructor.class, className, constructorSpec));
/* 351:    */     }
/* 352:    */     else
/* 353:    */     {
/* 354:598 */       setBallTreeConstructor(new TopDownConstructor());
/* 355:    */     }
/* 356:601 */     Utils.checkForRemainingOptions(options);
/* 357:    */   }
/* 358:    */   
/* 359:    */   public String[] getOptions()
/* 360:    */   {
/* 361:610 */     Vector<String> result = new Vector();
/* 362:    */     
/* 363:612 */     Collections.addAll(result, super.getOptions());
/* 364:    */     
/* 365:614 */     result.add("-C");
/* 366:615 */     result.add((this.m_TreeConstructor.getClass().getName() + " " + Utils.joinOptions(this.m_TreeConstructor.getOptions())).trim());
/* 367:    */     
/* 368:    */ 
/* 369:    */ 
/* 370:619 */     return (String[])result.toArray(new String[result.size()]);
/* 371:    */   }
/* 372:    */   
/* 373:    */   public String getRevision()
/* 374:    */   {
/* 375:628 */     return RevisionUtils.extract("$Revision: 10141 $");
/* 376:    */   }
/* 377:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.neighboursearch.BallTree
 * JD-Core Version:    0.7.0.1
 */