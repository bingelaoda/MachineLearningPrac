/*   1:    */ package weka.classifiers.mi.miti;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.io.Serializable;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import java.util.Collections;
/*   7:    */ import java.util.Comparator;
/*   8:    */ import java.util.HashMap;
/*   9:    */ import java.util.List;
/*  10:    */ import java.util.Random;
/*  11:    */ import weka.core.Attribute;
/*  12:    */ import weka.core.Instance;
/*  13:    */ 
/*  14:    */ public class TreeNode
/*  15:    */   implements Serializable
/*  16:    */ {
/*  17:    */   private static final long serialVersionUID = 9050803921532593168L;
/*  18:    */   private ArrayList<Instance> instances;
/*  19:    */   private double nodeScore;
/*  20:    */   private boolean leafNode;
/*  21:    */   private boolean positiveLeaf;
/*  22: 58 */   private TreeNode parent = null;
/*  23: 61 */   private TreeNode left = null;
/*  24: 64 */   private TreeNode right = null;
/*  25: 67 */   private TreeNode[] nominalNodes = null;
/*  26:    */   public Split split;
/*  27:    */   
/*  28:    */   public TreeNode(TreeNode parent, ArrayList<Instance> instances)
/*  29:    */   {
/*  30: 76 */     this.parent = parent;
/*  31: 77 */     this.instances = instances;
/*  32:    */   }
/*  33:    */   
/*  34:    */   public double nodeScore()
/*  35:    */   {
/*  36: 84 */     return this.nodeScore;
/*  37:    */   }
/*  38:    */   
/*  39:    */   public void removeDeactivatedInstances(HashMap<Instance, Bag> instanceBags)
/*  40:    */   {
/*  41: 93 */     ArrayList<Instance> newInstances = new ArrayList();
/*  42: 94 */     for (Instance i : this.instances) {
/*  43: 95 */       if (((Bag)instanceBags.get(i)).isEnabled()) {
/*  44: 96 */         newInstances.add(i);
/*  45:    */       }
/*  46:    */     }
/*  47: 99 */     this.instances = newInstances;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public void calculateNodeScore(HashMap<Instance, Bag> instanceBags, boolean unbiasedEstimate, int kBEPPConstant, boolean bagCount, double multiplier)
/*  51:    */   {
/*  52:108 */     this.nodeScore = NextSplitHeuristic.getBepp(this.instances, instanceBags, unbiasedEstimate, kBEPPConstant, bagCount, multiplier);
/*  53:    */   }
/*  54:    */   
/*  55:    */   public boolean isLeafNode()
/*  56:    */   {
/*  57:116 */     return this.leafNode;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public boolean isPositiveLeaf()
/*  61:    */   {
/*  62:123 */     return this.positiveLeaf;
/*  63:    */   }
/*  64:    */   
/*  65:    */   public boolean isPureNegative(HashMap<Instance, Bag> instanceBags)
/*  66:    */   {
/*  67:131 */     for (Instance i : this.instances)
/*  68:    */     {
/*  69:132 */       Bag bag = (Bag)instanceBags.get(i);
/*  70:133 */       if ((bag.isEnabled()) && (bag.isPositive())) {
/*  71:134 */         return false;
/*  72:    */       }
/*  73:    */     }
/*  74:137 */     return true;
/*  75:    */   }
/*  76:    */   
/*  77:    */   public boolean isPurePositive(HashMap<Instance, Bag> instanceBags)
/*  78:    */   {
/*  79:145 */     for (Instance i : this.instances)
/*  80:    */     {
/*  81:146 */       Bag bag = (Bag)instanceBags.get(i);
/*  82:147 */       if ((bag.isEnabled()) && (!bag.isPositive())) {
/*  83:148 */         return false;
/*  84:    */       }
/*  85:    */     }
/*  86:151 */     return true;
/*  87:    */   }
/*  88:    */   
/*  89:    */   public void makeLeafNode(boolean positiveLeaf)
/*  90:    */   {
/*  91:158 */     this.leafNode = true;
/*  92:159 */     this.positiveLeaf = positiveLeaf;
/*  93:    */   }
/*  94:    */   
/*  95:    */   public TreeNode parent()
/*  96:    */   {
/*  97:166 */     return this.parent;
/*  98:    */   }
/*  99:    */   
/* 100:    */   public TreeNode left()
/* 101:    */   {
/* 102:173 */     return this.left;
/* 103:    */   }
/* 104:    */   
/* 105:    */   public TreeNode right()
/* 106:    */   {
/* 107:180 */     return this.right;
/* 108:    */   }
/* 109:    */   
/* 110:    */   public TreeNode[] nominals()
/* 111:    */   {
/* 112:187 */     return this.nominalNodes;
/* 113:    */   }
/* 114:    */   
/* 115:    */   public void deactivateRelatedInstances(HashMap<Instance, Bag> instanceBags, List<String> deactivated)
/* 116:    */   {
/* 117:196 */     for (Instance i : this.instances)
/* 118:    */     {
/* 119:197 */       Bag container = (Bag)instanceBags.get(i);
/* 120:198 */       container.disableInstances(deactivated);
/* 121:    */     }
/* 122:    */   }
/* 123:    */   
/* 124:    */   private boolean hasSplitOnAttributePreviously(Attribute a)
/* 125:    */   {
/* 126:208 */     TreeNode n = this;
/* 127:209 */     while (n != null)
/* 128:    */     {
/* 129:210 */       if ((n.split != null) && (n.split.attribute.equals(a))) {
/* 130:211 */         return true;
/* 131:    */       }
/* 132:213 */       n = n.parent;
/* 133:    */     }
/* 134:215 */     return false;
/* 135:    */   }
/* 136:    */   
/* 137:    */   public void splitInstances(HashMap<Instance, Bag> instanceBags, AlgorithmConfiguration settings, Random rand, boolean debug)
/* 138:    */   {
/* 139:226 */     ArrayList<Instance> enabled = this.instances;
/* 140:    */     
/* 141:228 */     int totalAttributes = ((Instance)this.instances.get(0)).numAttributes();
/* 142:229 */     Instance template = (Instance)this.instances.get(0);
/* 143:    */     
/* 144:    */ 
/* 145:232 */     List<Attribute> attributes = new ArrayList();
/* 146:    */     double val;
/* 147:233 */     for (int index = 0; index < totalAttributes; index++)
/* 148:    */     {
/* 149:234 */       val = template.value(index);
/* 150:235 */       for (Instance i : this.instances) {
/* 151:236 */         if (i.value(index) != val)
/* 152:    */         {
/* 153:237 */           attributes.add(template.attribute(index));
/* 154:238 */           break;
/* 155:    */         }
/* 156:    */       }
/* 157:    */     }
/* 158:244 */     int attributesToSplit = settings.attributesToSplit;
/* 159:245 */     if (settings.attributesToSplit == -1) {
/* 160:246 */       attributesToSplit = totalAttributes;
/* 161:    */     }
/* 162:248 */     if (settings.attributesToSplit == -2) {
/* 163:249 */       attributesToSplit = (int)Math.sqrt(totalAttributes) + 1;
/* 164:    */     }
/* 165:251 */     if (attributesToSplit < attributes.size())
/* 166:    */     {
/* 167:253 */       Collections.shuffle(attributes, rand);
/* 168:254 */       attributes = attributes.subList(0, attributesToSplit);
/* 169:    */     }
/* 170:258 */     ArrayList<Split> best = new ArrayList();
/* 171:260 */     for (Attribute a : attributes) {
/* 172:261 */       if ((!a.isNominal()) || (!hasSplitOnAttributePreviously(a)))
/* 173:    */       {
/* 174:265 */         Split splitPoint = Split.getBestSplitPoint(a, enabled, instanceBags, settings);
/* 175:267 */         if (splitPoint != null)
/* 176:    */         {
/* 177:271 */           if (debug) {
/* 178:272 */             System.out.println(a.name() + " scored " + splitPoint.score);
/* 179:    */           }
/* 180:275 */           best.add(splitPoint);
/* 181:    */         }
/* 182:    */       }
/* 183:    */     }
/* 184:280 */     if (best.size() == 0)
/* 185:    */     {
/* 186:281 */       makeImpureLeafNode(instanceBags, settings, debug);
/* 187:282 */       return;
/* 188:    */     }
/* 189:285 */     Collections.sort(best, new Comparator()
/* 190:    */     {
/* 191:    */       public int compare(Split o1, Split o2)
/* 192:    */       {
/* 193:288 */         return Double.compare(o2.score, o1.score);
/* 194:    */       }
/* 195:292 */     });
/* 196:293 */     int attributeSplitChoices = settings.attributeSplitChoices;
/* 197:294 */     if (settings.attributeSplitChoices == -1) {
/* 198:295 */       attributeSplitChoices = best.size();
/* 199:296 */     } else if (settings.attributeSplitChoices == -2) {
/* 200:297 */       attributeSplitChoices = (int)Math.sqrt(best.size()) + 1;
/* 201:    */     }
/* 202:299 */     int pick = rand.nextInt(Math.min(attributeSplitChoices, best.size()));
/* 203:300 */     this.split = ((Split)best.get(pick));
/* 204:302 */     if (debug) {
/* 205:303 */       System.out.println("Selected best is " + this.split.attribute.name());
/* 206:    */     }
/* 207:306 */     Attribute splittingAttribute = this.split.attribute;
/* 208:307 */     if (splittingAttribute.isNominal())
/* 209:    */     {
/* 210:310 */       int numNominalValues = splittingAttribute.numValues();
/* 211:311 */       this.nominalNodes = new TreeNode[numNominalValues];
/* 212:312 */       for (int i = 0; i < numNominalValues; i++)
/* 213:    */       {
/* 214:313 */         ArrayList<Instance> list = new ArrayList();
/* 215:314 */         for (Instance instance : enabled) {
/* 216:315 */           if (instance.value(splittingAttribute) == i) {
/* 217:316 */             list.add(instance);
/* 218:    */           }
/* 219:    */         }
/* 220:319 */         this.nominalNodes[i] = new TreeNode(this, list);
/* 221:    */       }
/* 222:    */     }
/* 223:    */     else
/* 224:    */     {
/* 225:324 */       ArrayList<Instance> left = new ArrayList();
/* 226:325 */       ArrayList<Instance> right = new ArrayList();
/* 227:327 */       for (Instance instance : enabled) {
/* 228:328 */         if (instance.value(splittingAttribute) < this.split.splitPoint) {
/* 229:329 */           left.add(instance);
/* 230:    */         } else {
/* 231:331 */           right.add(instance);
/* 232:    */         }
/* 233:    */       }
/* 234:334 */       this.left = new TreeNode(this, left);
/* 235:335 */       this.right = new TreeNode(this, right);
/* 236:336 */       if (debug) {
/* 237:337 */         System.out.println(left.size() + " went left and " + right.size() + " went right");
/* 238:    */       }
/* 239:    */     }
/* 240:    */   }
/* 241:    */   
/* 242:    */   private void makeImpureLeafNode(HashMap<Instance, Bag> instanceBags, AlgorithmConfiguration settings, boolean debug)
/* 243:    */   {
/* 244:    */     SufficientStatistics ss;
/* 245:    */     SufficientStatistics ss;
/* 246:350 */     if (!settings.useBagStatistics) {
/* 247:351 */       ss = new SufficientInstanceStatistics(this.instances, instanceBags);
/* 248:    */     } else {
/* 249:353 */       ss = new SufficientBagStatistics(this.instances, instanceBags, settings.bagCountMultiplier);
/* 250:    */     }
/* 251:356 */     double bepp = BEPP.GetBEPP(ss.totalCountRight(), ss.positiveCountRight(), settings.kBEPPConstant, settings.unbiasedEstimate);
/* 252:    */     
/* 253:358 */     makeLeafNode(ss.positiveCountRight() / ss.totalCountRight() > 0.5D);
/* 254:360 */     if (debug) {
/* 255:361 */       System.out.println(bepp > 0.5D);
/* 256:    */     }
/* 257:366 */     if (!isPositiveLeaf()) {
/* 258:367 */       return;
/* 259:    */     }
/* 260:369 */     ArrayList<String> deactivated = new ArrayList();
/* 261:370 */     deactivateRelatedInstances(instanceBags, deactivated);
/* 262:373 */     if ((deactivated.size() > 0) && (debug)) {
/* 263:374 */       Bag.printDeactivatedInstances(deactivated);
/* 264:    */     }
/* 265:    */   }
/* 266:    */   
/* 267:    */   public String render(int depth, HashMap<Instance, Bag> instanceBags)
/* 268:    */   {
/* 269:385 */     String s = "";
/* 270:    */     
/* 271:387 */     int pos = 0;
/* 272:388 */     for (Instance i : this.instances)
/* 273:    */     {
/* 274:389 */       Bag bag = (Bag)instanceBags.get(i);
/* 275:390 */       if (bag.isPositive()) {
/* 276:391 */         pos++;
/* 277:    */       }
/* 278:    */     }
/* 279:394 */     s = s + this.instances.size() + " [" + pos + " / " + (this.instances.size() - pos) + "]";
/* 280:396 */     if (isLeafNode()) {
/* 281:397 */       s = s + (isPositiveLeaf() ? " (+)" : " (-)");
/* 282:    */     }
/* 283:400 */     if ((!isLeafNode()) && (this.split != null)) {
/* 284:401 */       if (this.split.attribute.isNominal())
/* 285:    */       {
/* 286:402 */         for (int i = 0; i < this.nominalNodes.length; i++) {
/* 287:403 */           if (this.nominalNodes[i] != null)
/* 288:    */           {
/* 289:405 */             s = s + "\n";
/* 290:406 */             for (int t = 0; t < depth; t++) {
/* 291:407 */               s = s + "|\t";
/* 292:    */             }
/* 293:409 */             s = s + this.split.attribute.name() + " = " + this.split.attribute.value(i) + " : ";
/* 294:    */             
/* 295:411 */             s = s + this.nominalNodes[i].render(depth + 1, instanceBags);
/* 296:    */           }
/* 297:    */         }
/* 298:    */       }
/* 299:    */       else
/* 300:    */       {
/* 301:415 */         if (this.left != null)
/* 302:    */         {
/* 303:417 */           s = s + "\n";
/* 304:418 */           for (int i = 0; i < depth; i++) {
/* 305:419 */             s = s + "|\t";
/* 306:    */           }
/* 307:421 */           s = s + this.split.attribute.name() + " <= " + String.format("%.4g", new Object[] { Double.valueOf(this.split.splitPoint) }) + " : ";
/* 308:    */           
/* 309:423 */           s = s + this.left.render(depth + 1, instanceBags);
/* 310:    */         }
/* 311:426 */         if (this.right != null)
/* 312:    */         {
/* 313:428 */           s = s + "\n";
/* 314:429 */           for (int i = 0; i < depth; i++) {
/* 315:430 */             s = s + "|\t";
/* 316:    */           }
/* 317:432 */           s = s + this.split.attribute.name() + " > " + String.format("%.4g", new Object[] { Double.valueOf(this.split.splitPoint) }) + " : ";
/* 318:    */           
/* 319:434 */           s = s + this.right.render(depth + 1, instanceBags);
/* 320:    */         }
/* 321:    */       }
/* 322:    */     }
/* 323:438 */     return s;
/* 324:    */   }
/* 325:    */   
/* 326:    */   public boolean trimNegativeBranches()
/* 327:    */   {
/* 328:449 */     boolean positive = false;
/* 329:450 */     if (this.nominalNodes != null)
/* 330:    */     {
/* 331:453 */       for (int i = 0; i < this.nominalNodes.length; i++)
/* 332:    */       {
/* 333:454 */         TreeNode child = this.nominalNodes[i];
/* 334:455 */         if (child.isPositiveLeaf()) {
/* 335:456 */           positive = true;
/* 336:457 */         } else if (child.trimNegativeBranches()) {
/* 337:458 */           positive = true;
/* 338:    */         } else {
/* 339:460 */           this.nominalNodes[i] = null;
/* 340:    */         }
/* 341:    */       }
/* 342:    */     }
/* 343:    */     else
/* 344:    */     {
/* 345:466 */       if (this.left != null) {
/* 346:467 */         if (this.left.isPositiveLeaf()) {
/* 347:468 */           positive = true;
/* 348:469 */         } else if (this.left.trimNegativeBranches()) {
/* 349:470 */           positive = true;
/* 350:    */         } else {
/* 351:472 */           this.left = null;
/* 352:    */         }
/* 353:    */       }
/* 354:476 */       if (this.right != null) {
/* 355:477 */         if (this.right.isPositiveLeaf()) {
/* 356:478 */           positive = true;
/* 357:479 */         } else if (this.right.trimNegativeBranches()) {
/* 358:480 */           positive = true;
/* 359:    */         } else {
/* 360:482 */           this.right = null;
/* 361:    */         }
/* 362:    */       }
/* 363:    */     }
/* 364:486 */     return positive;
/* 365:    */   }
/* 366:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.mi.miti.TreeNode
 * JD-Core Version:    0.7.0.1
 */