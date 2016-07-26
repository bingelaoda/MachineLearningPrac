/*   1:    */ package weka.classifiers.meta.nestedDichotomies;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Hashtable;
/*   6:    */ import java.util.Random;
/*   7:    */ import weka.classifiers.Classifier;
/*   8:    */ import weka.classifiers.RandomizableSingleClassifierEnhancer;
/*   9:    */ import weka.classifiers.meta.FilteredClassifier;
/*  10:    */ import weka.classifiers.rules.ZeroR;
/*  11:    */ import weka.classifiers.trees.J48;
/*  12:    */ import weka.core.Capabilities;
/*  13:    */ import weka.core.Capabilities.Capability;
/*  14:    */ import weka.core.Instance;
/*  15:    */ import weka.core.Instances;
/*  16:    */ import weka.core.RevisionHandler;
/*  17:    */ import weka.core.RevisionUtils;
/*  18:    */ import weka.core.TechnicalInformation;
/*  19:    */ import weka.core.TechnicalInformation.Field;
/*  20:    */ import weka.core.TechnicalInformation.Type;
/*  21:    */ import weka.core.TechnicalInformationHandler;
/*  22:    */ import weka.filters.Filter;
/*  23:    */ import weka.filters.unsupervised.attribute.MakeIndicator;
/*  24:    */ import weka.filters.unsupervised.instance.RemoveWithValues;
/*  25:    */ 
/*  26:    */ public class ND
/*  27:    */   extends RandomizableSingleClassifierEnhancer
/*  28:    */   implements TechnicalInformationHandler
/*  29:    */ {
/*  30:    */   static final long serialVersionUID = -6355893369855683820L;
/*  31:    */   
/*  32:    */   protected class NDTree
/*  33:    */     implements Serializable, RevisionHandler
/*  34:    */   {
/*  35:    */     private static final long serialVersionUID = 4284655952754474880L;
/*  36:185 */     protected ArrayList<Integer> m_indices = null;
/*  37:188 */     protected NDTree m_parent = null;
/*  38:191 */     protected NDTree m_left = null;
/*  39:194 */     protected NDTree m_right = null;
/*  40:    */     
/*  41:    */     protected NDTree()
/*  42:    */     {
/*  43:201 */       this.m_indices = new ArrayList(1);
/*  44:202 */       this.m_indices.add(new Integer(2147483647));
/*  45:    */     }
/*  46:    */     
/*  47:    */     protected NDTree locateNode(int nodeIndex, int[] currentIndex)
/*  48:    */     {
/*  49:210 */       if (nodeIndex == currentIndex[0]) {
/*  50:211 */         return this;
/*  51:    */       }
/*  52:212 */       if (this.m_left == null) {
/*  53:213 */         return null;
/*  54:    */       }
/*  55:215 */       currentIndex[0] += 1;
/*  56:216 */       NDTree leftresult = this.m_left.locateNode(nodeIndex, currentIndex);
/*  57:217 */       if (leftresult != null) {
/*  58:218 */         return leftresult;
/*  59:    */       }
/*  60:220 */       currentIndex[0] += 1;
/*  61:221 */       return this.m_right.locateNode(nodeIndex, currentIndex);
/*  62:    */     }
/*  63:    */     
/*  64:    */     protected void insertClassIndex(int classIndex)
/*  65:    */     {
/*  66:235 */       NDTree right = new NDTree(ND.this);
/*  67:236 */       if (this.m_left != null)
/*  68:    */       {
/*  69:237 */         this.m_right.m_parent = right;
/*  70:238 */         this.m_left.m_parent = right;
/*  71:239 */         right.m_right = this.m_right;
/*  72:240 */         right.m_left = this.m_left;
/*  73:    */       }
/*  74:242 */       this.m_right = right;
/*  75:243 */       this.m_right.m_indices = ((ArrayList)this.m_indices.clone());
/*  76:244 */       this.m_right.m_parent = this;
/*  77:245 */       this.m_left = new NDTree(ND.this);
/*  78:246 */       this.m_left.insertClassIndexAtNode(classIndex);
/*  79:247 */       this.m_left.m_parent = this;
/*  80:    */       
/*  81:    */ 
/*  82:250 */       propagateClassIndex(classIndex);
/*  83:    */     }
/*  84:    */     
/*  85:    */     protected void propagateClassIndex(int classIndex)
/*  86:    */     {
/*  87:260 */       insertClassIndexAtNode(classIndex);
/*  88:261 */       if (this.m_parent != null) {
/*  89:262 */         this.m_parent.propagateClassIndex(classIndex);
/*  90:    */       }
/*  91:    */     }
/*  92:    */     
/*  93:    */     protected void insertClassIndexAtNode(int classIndex)
/*  94:    */     {
/*  95:273 */       int i = 0;
/*  96:274 */       while (classIndex > ((Integer)this.m_indices.get(i)).intValue()) {
/*  97:275 */         i++;
/*  98:    */       }
/*  99:277 */       this.m_indices.add(i, new Integer(classIndex));
/* 100:    */     }
/* 101:    */     
/* 102:    */     protected int[] getIndices()
/* 103:    */     {
/* 104:287 */       int[] ints = new int[this.m_indices.size() - 1];
/* 105:288 */       for (int i = 0; i < this.m_indices.size() - 1; i++) {
/* 106:289 */         ints[i] = ((Integer)this.m_indices.get(i)).intValue();
/* 107:    */       }
/* 108:291 */       return ints;
/* 109:    */     }
/* 110:    */     
/* 111:    */     protected boolean contains(int index)
/* 112:    */     {
/* 113:302 */       for (int i = 0; i < this.m_indices.size() - 1; i++) {
/* 114:303 */         if (index == ((Integer)this.m_indices.get(i)).intValue()) {
/* 115:304 */           return true;
/* 116:    */         }
/* 117:    */       }
/* 118:307 */       return false;
/* 119:    */     }
/* 120:    */     
/* 121:    */     protected String getString()
/* 122:    */     {
/* 123:317 */       StringBuffer string = new StringBuffer();
/* 124:318 */       for (int i = 0; i < this.m_indices.size() - 1; i++)
/* 125:    */       {
/* 126:319 */         if (i > 0) {
/* 127:320 */           string.append(',');
/* 128:    */         }
/* 129:322 */         string.append(((Integer)this.m_indices.get(i)).intValue() + 1);
/* 130:    */       }
/* 131:324 */       return string.toString();
/* 132:    */     }
/* 133:    */     
/* 134:    */     protected void unifyTree()
/* 135:    */     {
/* 136:332 */       if (this.m_left != null)
/* 137:    */       {
/* 138:333 */         if (((Integer)this.m_left.m_indices.get(0)).intValue() > ((Integer)this.m_right.m_indices.get(0)).intValue())
/* 139:    */         {
/* 140:335 */           NDTree temp = this.m_left;
/* 141:336 */           this.m_left = this.m_right;
/* 142:337 */           this.m_right = temp;
/* 143:    */         }
/* 144:339 */         this.m_left.unifyTree();
/* 145:340 */         this.m_right.unifyTree();
/* 146:    */       }
/* 147:    */     }
/* 148:    */     
/* 149:    */     protected void toString(StringBuffer text, int[] id, int level)
/* 150:    */     {
/* 151:353 */       for (int i = 0; i < level; i++) {
/* 152:354 */         text.append("   | ");
/* 153:    */       }
/* 154:356 */       text.append(id[0] + ": " + getString() + "\n");
/* 155:357 */       if (this.m_left != null)
/* 156:    */       {
/* 157:358 */         id[0] += 1;
/* 158:359 */         this.m_left.toString(text, id, level + 1);
/* 159:360 */         id[0] += 1;
/* 160:361 */         this.m_right.toString(text, id, level + 1);
/* 161:    */       }
/* 162:    */     }
/* 163:    */     
/* 164:    */     public String getRevision()
/* 165:    */     {
/* 166:372 */       return RevisionUtils.extract("$Revision: 12648 $");
/* 167:    */     }
/* 168:    */   }
/* 169:    */   
/* 170:377 */   protected NDTree m_ndtree = null;
/* 171:380 */   protected Hashtable<String, Classifier> m_classifiers = null;
/* 172:383 */   protected boolean m_hashtablegiven = false;
/* 173:    */   
/* 174:    */   public ND()
/* 175:    */   {
/* 176:390 */     this.m_Classifier = new J48();
/* 177:    */   }
/* 178:    */   
/* 179:    */   protected String defaultClassifierString()
/* 180:    */   {
/* 181:401 */     return "weka.classifiers.trees.J48";
/* 182:    */   }
/* 183:    */   
/* 184:    */   public TechnicalInformation getTechnicalInformation()
/* 185:    */   {
/* 186:416 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/* 187:417 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Lin Dong and Eibe Frank and Stefan Kramer");
/* 188:418 */     result.setValue(TechnicalInformation.Field.TITLE, "Ensembles of Balanced Nested Dichotomies for Multi-class Problems");
/* 189:    */     
/* 190:420 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "PKDD");
/* 191:421 */     result.setValue(TechnicalInformation.Field.YEAR, "2005");
/* 192:422 */     result.setValue(TechnicalInformation.Field.PAGES, "84-95");
/* 193:423 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "Springer");
/* 194:    */     
/* 195:425 */     TechnicalInformation additional = result.add(TechnicalInformation.Type.INPROCEEDINGS);
/* 196:426 */     additional.setValue(TechnicalInformation.Field.AUTHOR, "Eibe Frank and Stefan Kramer");
/* 197:427 */     additional.setValue(TechnicalInformation.Field.TITLE, "Ensembles of nested dichotomies for multi-class problems");
/* 198:    */     
/* 199:429 */     additional.setValue(TechnicalInformation.Field.BOOKTITLE, "Twenty-first International Conference on Machine Learning");
/* 200:    */     
/* 201:431 */     additional.setValue(TechnicalInformation.Field.YEAR, "2004");
/* 202:432 */     additional.setValue(TechnicalInformation.Field.PUBLISHER, "ACM");
/* 203:    */     
/* 204:434 */     return result;
/* 205:    */   }
/* 206:    */   
/* 207:    */   public void setHashtable(Hashtable<String, Classifier> table)
/* 208:    */   {
/* 209:444 */     this.m_hashtablegiven = true;
/* 210:445 */     this.m_classifiers = table;
/* 211:    */   }
/* 212:    */   
/* 213:    */   public Capabilities getCapabilities()
/* 214:    */   {
/* 215:455 */     Capabilities result = super.getCapabilities();
/* 216:    */     
/* 217:    */ 
/* 218:458 */     result.disableAllClasses();
/* 219:459 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/* 220:460 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 221:    */     
/* 222:    */ 
/* 223:463 */     result.setMinimumNumberInstances(1);
/* 224:    */     
/* 225:465 */     return result;
/* 226:    */   }
/* 227:    */   
/* 228:    */   public void buildClassifier(Instances data)
/* 229:    */     throws Exception
/* 230:    */   {
/* 231:478 */     getCapabilities().testWithFail(data);
/* 232:    */     
/* 233:    */ 
/* 234:481 */     data = new Instances(data);
/* 235:482 */     data.deleteWithMissingClass();
/* 236:    */     
/* 237:484 */     Random random = data.getRandomNumberGenerator(this.m_Seed);
/* 238:486 */     if (!this.m_hashtablegiven) {
/* 239:487 */       this.m_classifiers = new Hashtable();
/* 240:    */     }
/* 241:491 */     int[] indices = new int[data.numClasses()];
/* 242:492 */     for (int i = 0; i < indices.length; i++) {
/* 243:493 */       indices[i] = i;
/* 244:    */     }
/* 245:497 */     for (int i = indices.length - 1; i > 0; i--)
/* 246:    */     {
/* 247:498 */       int help = indices[i];
/* 248:499 */       int index = random.nextInt(i + 1);
/* 249:500 */       indices[i] = indices[index];
/* 250:501 */       indices[index] = help;
/* 251:    */     }
/* 252:505 */     this.m_ndtree = new NDTree();
/* 253:506 */     this.m_ndtree.insertClassIndexAtNode(indices[0]);
/* 254:507 */     for (int i = 1; i < indices.length; i++)
/* 255:    */     {
/* 256:508 */       int nodeIndex = random.nextInt(2 * i - 1);
/* 257:    */       
/* 258:510 */       NDTree node = this.m_ndtree.locateNode(nodeIndex, new int[1]);
/* 259:511 */       node.insertClassIndex(indices[i]);
/* 260:    */     }
/* 261:513 */     this.m_ndtree.unifyTree();
/* 262:    */     
/* 263:    */ 
/* 264:516 */     buildClassifierForNode(this.m_ndtree, data);
/* 265:    */   }
/* 266:    */   
/* 267:    */   public void buildClassifierForNode(NDTree node, Instances data)
/* 268:    */     throws Exception
/* 269:    */   {
/* 270:530 */     if (node.m_left != null)
/* 271:    */     {
/* 272:533 */       MakeIndicator filter = new MakeIndicator();
/* 273:534 */       filter.setAttributeIndex("" + (data.classIndex() + 1));
/* 274:535 */       filter.setValueIndices(node.m_right.getString());
/* 275:536 */       filter.setNumeric(false);
/* 276:537 */       filter.setInputFormat(data);
/* 277:538 */       FilteredClassifier classifier = new FilteredClassifier();
/* 278:539 */       classifier.setDoNotCheckForModifiedClassAttribute(true);
/* 279:540 */       if (data.numInstances() > 0) {
/* 280:541 */         classifier.setClassifier(weka.classifiers.AbstractClassifier.makeCopies(this.m_Classifier, 1)[0]);
/* 281:    */       } else {
/* 282:544 */         classifier.setClassifier(new ZeroR());
/* 283:    */       }
/* 284:546 */       classifier.setFilter(filter);
/* 285:548 */       if (!this.m_classifiers.containsKey(node.m_left.getString() + "|" + node.m_right.getString()))
/* 286:    */       {
/* 287:550 */         classifier.buildClassifier(data);
/* 288:551 */         this.m_classifiers.put(node.m_left.getString() + "|" + node.m_right.getString(), classifier);
/* 289:    */       }
/* 290:    */       else
/* 291:    */       {
/* 292:554 */         classifier = (FilteredClassifier)this.m_classifiers.get(node.m_left.getString() + "|" + node.m_right.getString());
/* 293:    */       }
/* 294:559 */       if (node.m_left.m_left != null)
/* 295:    */       {
/* 296:560 */         RemoveWithValues rwv = new RemoveWithValues();
/* 297:561 */         rwv.setInvertSelection(true);
/* 298:562 */         rwv.setNominalIndices(node.m_left.getString());
/* 299:563 */         rwv.setAttributeIndex("" + (data.classIndex() + 1));
/* 300:564 */         rwv.setInputFormat(data);
/* 301:565 */         Instances firstSubset = Filter.useFilter(data, rwv);
/* 302:566 */         buildClassifierForNode(node.m_left, firstSubset);
/* 303:    */       }
/* 304:568 */       if (node.m_right.m_left != null)
/* 305:    */       {
/* 306:569 */         RemoveWithValues rwv = new RemoveWithValues();
/* 307:570 */         rwv.setInvertSelection(true);
/* 308:571 */         rwv.setNominalIndices(node.m_right.getString());
/* 309:572 */         rwv.setAttributeIndex("" + (data.classIndex() + 1));
/* 310:573 */         rwv.setInputFormat(data);
/* 311:574 */         Instances secondSubset = Filter.useFilter(data, rwv);
/* 312:575 */         buildClassifierForNode(node.m_right, secondSubset);
/* 313:    */       }
/* 314:    */     }
/* 315:    */   }
/* 316:    */   
/* 317:    */   public double[] distributionForInstance(Instance inst)
/* 318:    */     throws Exception
/* 319:    */   {
/* 320:590 */     return distributionForInstance(inst, this.m_ndtree);
/* 321:    */   }
/* 322:    */   
/* 323:    */   protected double[] distributionForInstance(Instance inst, NDTree node)
/* 324:    */     throws Exception
/* 325:    */   {
/* 326:604 */     double[] newDist = new double[inst.numClasses()];
/* 327:605 */     if (node.m_left == null)
/* 328:    */     {
/* 329:606 */       newDist[node.getIndices()[0]] = 1.0D;
/* 330:607 */       return newDist;
/* 331:    */     }
/* 332:609 */     Classifier classifier = (Classifier)this.m_classifiers.get(node.m_left.getString() + "|" + node.m_right.getString());
/* 333:    */     
/* 334:611 */     double[] leftDist = distributionForInstance(inst, node.m_left);
/* 335:612 */     double[] rightDist = distributionForInstance(inst, node.m_right);
/* 336:613 */     double[] dist = classifier.distributionForInstance(inst);
/* 337:615 */     for (int i = 0; i < inst.numClasses(); i++) {
/* 338:616 */       if (node.m_right.contains(i)) {
/* 339:617 */         newDist[i] = (dist[1] * rightDist[i]);
/* 340:    */       } else {
/* 341:619 */         newDist[i] = (dist[0] * leftDist[i]);
/* 342:    */       }
/* 343:    */     }
/* 344:622 */     return newDist;
/* 345:    */   }
/* 346:    */   
/* 347:    */   public String toString()
/* 348:    */   {
/* 349:634 */     if (this.m_classifiers == null) {
/* 350:635 */       return "ND: No model built yet.";
/* 351:    */     }
/* 352:637 */     StringBuffer text = new StringBuffer();
/* 353:638 */     text.append("ND\n\n");
/* 354:639 */     this.m_ndtree.toString(text, new int[1], 0);
/* 355:    */     
/* 356:641 */     return text.toString();
/* 357:    */   }
/* 358:    */   
/* 359:    */   public String globalInfo()
/* 360:    */   {
/* 361:650 */     return "A meta classifier for handling multi-class datasets with 2-class classifiers by building a random tree structure.\n\nFor more info, check\n\n" + getTechnicalInformation().toString();
/* 362:    */   }
/* 363:    */   
/* 364:    */   public String getRevision()
/* 365:    */   {
/* 366:662 */     return RevisionUtils.extract("$Revision: 12648 $");
/* 367:    */   }
/* 368:    */   
/* 369:    */   public static void main(String[] argv)
/* 370:    */   {
/* 371:671 */     runClassifier(new ND(), argv);
/* 372:    */   }
/* 373:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.meta.nestedDichotomies.ND
 * JD-Core Version:    0.7.0.1
 */