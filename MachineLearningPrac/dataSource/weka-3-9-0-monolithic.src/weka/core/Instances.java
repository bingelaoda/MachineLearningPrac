/*    1:     */ package weka.core;
/*    2:     */ 
/*    3:     */ import java.io.FileReader;
/*    4:     */ import java.io.IOException;
/*    5:     */ import java.io.PrintStream;
/*    6:     */ import java.io.Reader;
/*    7:     */ import java.io.Serializable;
/*    8:     */ import java.util.AbstractList;
/*    9:     */ import java.util.ArrayList;
/*   10:     */ import java.util.Enumeration;
/*   11:     */ import java.util.HashMap;
/*   12:     */ import java.util.HashSet;
/*   13:     */ import java.util.List;
/*   14:     */ import java.util.Map.Entry;
/*   15:     */ import java.util.Random;
/*   16:     */ import weka.core.converters.ArffLoader.ArffReader;
/*   17:     */ import weka.core.converters.ConverterUtils.DataSource;
/*   18:     */ import weka.experiment.Stats;
/*   19:     */ 
/*   20:     */ public class Instances
/*   21:     */   extends AbstractList<Instance>
/*   22:     */   implements Serializable, RevisionHandler
/*   23:     */ {
/*   24:     */   static final long serialVersionUID = -19412345060742748L;
/*   25:     */   public static final String FILE_EXTENSION = ".arff";
/*   26:     */   public static final String SERIALIZED_OBJ_FILE_EXTENSION = ".bsi";
/*   27:     */   public static final String ARFF_RELATION = "@relation";
/*   28:     */   public static final String ARFF_DATA = "@data";
/*   29:     */   protected String m_RelationName;
/*   30:     */   protected ArrayList<Attribute> m_Attributes;
/*   31:     */   protected HashMap<String, Integer> m_NamesToAttributeIndices;
/*   32:     */   protected ArrayList<Instance> m_Instances;
/*   33:     */   protected int m_ClassIndex;
/*   34: 124 */   protected int m_Lines = 0;
/*   35:     */   
/*   36:     */   public Instances(Reader reader)
/*   37:     */     throws IOException
/*   38:     */   {
/*   39: 134 */     ArffLoader.ArffReader arff = new ArffLoader.ArffReader(reader, 1000, false);
/*   40: 135 */     initialize(arff.getData(), 1000);
/*   41: 136 */     arff.setRetainStringValues(true);
/*   42:     */     Instance inst;
/*   43: 138 */     while ((inst = arff.readInstance(this)) != null) {
/*   44: 139 */       this.m_Instances.add(inst);
/*   45:     */     }
/*   46: 141 */     compactify();
/*   47:     */   }
/*   48:     */   
/*   49:     */   @Deprecated
/*   50:     */   public Instances(Reader reader, int capacity)
/*   51:     */     throws IOException
/*   52:     */   {
/*   53: 165 */     ArffLoader.ArffReader arff = new ArffLoader.ArffReader(reader, 0);
/*   54: 166 */     Instances header = arff.getStructure();
/*   55: 167 */     initialize(header, capacity);
/*   56: 168 */     this.m_Lines = arff.getLineNo();
/*   57:     */   }
/*   58:     */   
/*   59:     */   public Instances(Instances dataset)
/*   60:     */   {
/*   61: 179 */     this(dataset, dataset.numInstances());
/*   62:     */     
/*   63: 181 */     dataset.copyInstances(0, this, dataset.numInstances());
/*   64:     */   }
/*   65:     */   
/*   66:     */   public Instances(Instances dataset, int capacity)
/*   67:     */   {
/*   68: 194 */     initialize(dataset, capacity);
/*   69:     */   }
/*   70:     */   
/*   71:     */   protected void initialize(Instances dataset, int capacity)
/*   72:     */   {
/*   73: 205 */     if (capacity < 0) {
/*   74: 206 */       capacity = 0;
/*   75:     */     }
/*   76: 211 */     this.m_ClassIndex = dataset.m_ClassIndex;
/*   77: 212 */     this.m_RelationName = dataset.m_RelationName;
/*   78: 213 */     this.m_Attributes = dataset.m_Attributes;
/*   79: 214 */     this.m_NamesToAttributeIndices = dataset.m_NamesToAttributeIndices;
/*   80: 215 */     this.m_Instances = new ArrayList(capacity);
/*   81:     */   }
/*   82:     */   
/*   83:     */   public Instances(Instances source, int first, int toCopy)
/*   84:     */   {
/*   85: 231 */     this(source, toCopy);
/*   86: 233 */     if ((first < 0) || (first + toCopy > source.numInstances())) {
/*   87: 234 */       throw new IllegalArgumentException("Parameters first and/or toCopy out of range");
/*   88:     */     }
/*   89: 237 */     source.copyInstances(first, this, toCopy);
/*   90:     */   }
/*   91:     */   
/*   92:     */   public Instances(String name, ArrayList<Attribute> attInfo, int capacity)
/*   93:     */   {
/*   94: 255 */     HashSet<String> names = new HashSet();
/*   95: 256 */     StringBuffer nonUniqueNames = new StringBuffer();
/*   96: 257 */     for (Attribute att : attInfo)
/*   97:     */     {
/*   98: 258 */       if (names.contains(att.name())) {
/*   99: 259 */         nonUniqueNames.append("'" + att.name() + "' ");
/*  100:     */       }
/*  101: 261 */       names.add(att.name());
/*  102:     */     }
/*  103: 263 */     if (names.size() != attInfo.size()) {
/*  104: 264 */       throw new IllegalArgumentException("Attribute names are not unique! Causes: " + nonUniqueNames.toString());
/*  105:     */     }
/*  106: 267 */     names.clear();
/*  107:     */     
/*  108: 269 */     this.m_RelationName = name;
/*  109: 270 */     this.m_ClassIndex = -1;
/*  110: 271 */     this.m_Attributes = attInfo;
/*  111: 272 */     this.m_NamesToAttributeIndices = new HashMap((int)(numAttributes() / 0.75D));
/*  112: 273 */     for (int i = 0; i < numAttributes(); i++)
/*  113:     */     {
/*  114: 274 */       attribute(i).setIndex(i);
/*  115: 275 */       this.m_NamesToAttributeIndices.put(attribute(i).name(), Integer.valueOf(i));
/*  116:     */     }
/*  117: 277 */     this.m_Instances = new ArrayList(capacity);
/*  118:     */   }
/*  119:     */   
/*  120:     */   public Instances stringFreeStructure()
/*  121:     */   {
/*  122: 290 */     ArrayList<Attribute> newAtts = new ArrayList();
/*  123: 291 */     for (Attribute att : this.m_Attributes) {
/*  124: 292 */       if (att.type() == 2) {
/*  125: 293 */         newAtts.add(new Attribute(att.name(), (List)null, att.index()));
/*  126: 294 */       } else if (att.type() == 4) {
/*  127: 295 */         newAtts.add(new Attribute(att.name(), new Instances(att.relation(), 0), att.index()));
/*  128:     */       }
/*  129:     */     }
/*  130: 299 */     if (newAtts.size() == 0) {
/*  131: 300 */       return new Instances(this, 0);
/*  132:     */     }
/*  133: 302 */     ArrayList<Attribute> atts = (ArrayList)Utils.cast(this.m_Attributes.clone());
/*  134: 303 */     for (Attribute att : newAtts) {
/*  135: 304 */       atts.set(att.index(), att);
/*  136:     */     }
/*  137: 306 */     Instances result = new Instances(this, 0);
/*  138: 307 */     result.m_Attributes = atts;
/*  139: 308 */     return result;
/*  140:     */   }
/*  141:     */   
/*  142:     */   public boolean add(Instance instance)
/*  143:     */   {
/*  144: 322 */     Instance newInstance = (Instance)instance.copy();
/*  145:     */     
/*  146: 324 */     newInstance.setDataset(this);
/*  147: 325 */     this.m_Instances.add(newInstance);
/*  148:     */     
/*  149: 327 */     return true;
/*  150:     */   }
/*  151:     */   
/*  152:     */   public void add(int index, Instance instance)
/*  153:     */   {
/*  154: 345 */     Instance newInstance = (Instance)instance.copy();
/*  155:     */     
/*  156: 347 */     newInstance.setDataset(this);
/*  157: 348 */     this.m_Instances.add(index, newInstance);
/*  158:     */   }
/*  159:     */   
/*  160:     */   public Attribute attribute(int index)
/*  161:     */   {
/*  162: 362 */     return (Attribute)this.m_Attributes.get(index);
/*  163:     */   }
/*  164:     */   
/*  165:     */   public Attribute attribute(String name)
/*  166:     */   {
/*  167: 376 */     Integer index = (Integer)this.m_NamesToAttributeIndices.get(name);
/*  168: 377 */     if (index != null) {
/*  169: 378 */       return attribute(index.intValue());
/*  170:     */     }
/*  171: 381 */     return null;
/*  172:     */   }
/*  173:     */   
/*  174:     */   public boolean checkForAttributeType(int attType)
/*  175:     */   {
/*  176: 392 */     int i = 0;
/*  177: 394 */     while (i < this.m_Attributes.size()) {
/*  178: 395 */       if (attribute(i++).type() == attType) {
/*  179: 396 */         return true;
/*  180:     */       }
/*  181:     */     }
/*  182: 399 */     return false;
/*  183:     */   }
/*  184:     */   
/*  185:     */   public boolean checkForStringAttributes()
/*  186:     */   {
/*  187: 408 */     return checkForAttributeType(2);
/*  188:     */   }
/*  189:     */   
/*  190:     */   public boolean checkInstance(Instance instance)
/*  191:     */   {
/*  192: 421 */     if (instance.numAttributes() != numAttributes()) {
/*  193: 422 */       return false;
/*  194:     */     }
/*  195: 424 */     for (int i = 0; i < numAttributes(); i++) {
/*  196: 425 */       if (!instance.isMissing(i)) {
/*  197: 427 */         if ((attribute(i).isNominal()) || (attribute(i).isString()))
/*  198:     */         {
/*  199: 428 */           if (!Utils.eq(instance.value(i), (int)instance.value(i))) {
/*  200: 429 */             return false;
/*  201:     */           }
/*  202: 430 */           if ((Utils.sm(instance.value(i), 0.0D)) || (Utils.gr(instance.value(i), attribute(i).numValues()))) {
/*  203: 432 */             return false;
/*  204:     */           }
/*  205:     */         }
/*  206:     */       }
/*  207:     */     }
/*  208: 436 */     return true;
/*  209:     */   }
/*  210:     */   
/*  211:     */   public Attribute classAttribute()
/*  212:     */   {
/*  213: 448 */     if (this.m_ClassIndex < 0) {
/*  214: 449 */       throw new UnassignedClassException("Class index is negative (not set)!");
/*  215:     */     }
/*  216: 451 */     return attribute(this.m_ClassIndex);
/*  217:     */   }
/*  218:     */   
/*  219:     */   public int classIndex()
/*  220:     */   {
/*  221: 463 */     return this.m_ClassIndex;
/*  222:     */   }
/*  223:     */   
/*  224:     */   public void compactify()
/*  225:     */   {
/*  226: 472 */     this.m_Instances.trimToSize();
/*  227:     */   }
/*  228:     */   
/*  229:     */   public void delete()
/*  230:     */   {
/*  231: 480 */     this.m_Instances = new ArrayList();
/*  232:     */   }
/*  233:     */   
/*  234:     */   public void delete(int index)
/*  235:     */   {
/*  236: 491 */     this.m_Instances.remove(index);
/*  237:     */   }
/*  238:     */   
/*  239:     */   public void deleteAttributeAt(int position)
/*  240:     */   {
/*  241: 507 */     if ((position < 0) || (position >= this.m_Attributes.size())) {
/*  242: 508 */       throw new IllegalArgumentException("Index out of range");
/*  243:     */     }
/*  244: 510 */     if (position == this.m_ClassIndex) {
/*  245: 511 */       throw new IllegalArgumentException("Can't delete class attribute");
/*  246:     */     }
/*  247: 514 */     ArrayList<Attribute> newList = new ArrayList(this.m_Attributes.size() - 1);
/*  248: 515 */     HashMap<String, Integer> newMap = new HashMap((int)((this.m_Attributes.size() - 1) / 0.75D));
/*  249: 516 */     for (int i = 0; i < position; i++)
/*  250:     */     {
/*  251: 517 */       Attribute att = (Attribute)this.m_Attributes.get(i);
/*  252: 518 */       newList.add(att);
/*  253: 519 */       newMap.put(att.name(), Integer.valueOf(i));
/*  254:     */     }
/*  255: 521 */     for (int i = position + 1; i < this.m_Attributes.size(); i++)
/*  256:     */     {
/*  257: 522 */       Attribute newAtt = (Attribute)((Attribute)this.m_Attributes.get(i)).copy();
/*  258: 523 */       newAtt.setIndex(i - 1);
/*  259: 524 */       newList.add(newAtt);
/*  260: 525 */       newMap.put(newAtt.name(), Integer.valueOf(i - 1));
/*  261:     */     }
/*  262: 527 */     this.m_Attributes = newList;
/*  263: 528 */     this.m_NamesToAttributeIndices = newMap;
/*  264: 530 */     if (this.m_ClassIndex > position) {
/*  265: 531 */       this.m_ClassIndex -= 1;
/*  266:     */     }
/*  267: 533 */     for (int i = 0; i < numInstances(); i++)
/*  268:     */     {
/*  269: 534 */       instance(i).setDataset(null);
/*  270: 535 */       instance(i).deleteAttributeAt(position);
/*  271: 536 */       instance(i).setDataset(this);
/*  272:     */     }
/*  273:     */   }
/*  274:     */   
/*  275:     */   public void deleteAttributeType(int attType)
/*  276:     */   {
/*  277: 549 */     int i = 0;
/*  278: 550 */     while (i < this.m_Attributes.size()) {
/*  279: 551 */       if (attribute(i).type() == attType) {
/*  280: 552 */         deleteAttributeAt(i);
/*  281:     */       } else {
/*  282: 554 */         i++;
/*  283:     */       }
/*  284:     */     }
/*  285:     */   }
/*  286:     */   
/*  287:     */   public void deleteStringAttributes()
/*  288:     */   {
/*  289: 569 */     deleteAttributeType(2);
/*  290:     */   }
/*  291:     */   
/*  292:     */   public void deleteWithMissing(int attIndex)
/*  293:     */   {
/*  294: 581 */     ArrayList<Instance> newInstances = new ArrayList(numInstances());
/*  295: 583 */     for (int i = 0; i < numInstances(); i++) {
/*  296: 584 */       if (!instance(i).isMissing(attIndex)) {
/*  297: 585 */         newInstances.add(instance(i));
/*  298:     */       }
/*  299:     */     }
/*  300: 588 */     this.m_Instances = newInstances;
/*  301:     */   }
/*  302:     */   
/*  303:     */   public void deleteWithMissing(Attribute att)
/*  304:     */   {
/*  305: 599 */     deleteWithMissing(att.index());
/*  306:     */   }
/*  307:     */   
/*  308:     */   public void deleteWithMissingClass()
/*  309:     */   {
/*  310: 609 */     if (this.m_ClassIndex < 0) {
/*  311: 610 */       throw new UnassignedClassException("Class index is negative (not set)!");
/*  312:     */     }
/*  313: 612 */     deleteWithMissing(this.m_ClassIndex);
/*  314:     */   }
/*  315:     */   
/*  316:     */   public Enumeration<Attribute> enumerateAttributes()
/*  317:     */   {
/*  318: 623 */     return new WekaEnumeration(this.m_Attributes, this.m_ClassIndex);
/*  319:     */   }
/*  320:     */   
/*  321:     */   public Enumeration<Instance> enumerateInstances()
/*  322:     */   {
/*  323: 633 */     return new WekaEnumeration(this.m_Instances);
/*  324:     */   }
/*  325:     */   
/*  326:     */   public String equalHeadersMsg(Instances dataset)
/*  327:     */   {
/*  328: 646 */     if (this.m_ClassIndex != dataset.m_ClassIndex) {
/*  329: 647 */       return "Class index differ: " + (this.m_ClassIndex + 1) + " != " + (dataset.m_ClassIndex + 1);
/*  330:     */     }
/*  331: 651 */     if (this.m_Attributes.size() != dataset.m_Attributes.size()) {
/*  332: 652 */       return "Different number of attributes: " + this.m_Attributes.size() + " != " + dataset.m_Attributes.size();
/*  333:     */     }
/*  334: 656 */     for (int i = 0; i < this.m_Attributes.size(); i++)
/*  335:     */     {
/*  336: 657 */       String msg = attribute(i).equalsMsg(dataset.attribute(i));
/*  337: 658 */       if (msg != null) {
/*  338: 659 */         return "Attributes differ at position " + (i + 1) + ":\n" + msg;
/*  339:     */       }
/*  340:     */     }
/*  341: 663 */     return null;
/*  342:     */   }
/*  343:     */   
/*  344:     */   public boolean equalHeaders(Instances dataset)
/*  345:     */   {
/*  346: 674 */     return equalHeadersMsg(dataset) == null;
/*  347:     */   }
/*  348:     */   
/*  349:     */   public Instance firstInstance()
/*  350:     */   {
/*  351: 685 */     return (Instance)this.m_Instances.get(0);
/*  352:     */   }
/*  353:     */   
/*  354:     */   public Random getRandomNumberGenerator(long seed)
/*  355:     */   {
/*  356: 698 */     Random r = new Random(seed);
/*  357: 699 */     r.setSeed(instance(r.nextInt(numInstances())).toStringNoWeight().hashCode() + seed);
/*  358:     */     
/*  359: 701 */     return r;
/*  360:     */   }
/*  361:     */   
/*  362:     */   public void insertAttributeAt(Attribute att, int position)
/*  363:     */   {
/*  364: 720 */     if ((position < 0) || (position > this.m_Attributes.size())) {
/*  365: 721 */       throw new IllegalArgumentException("Index out of range");
/*  366:     */     }
/*  367: 723 */     if (attribute(att.name()) != null) {
/*  368: 724 */       throw new IllegalArgumentException("Attribute name '" + att.name() + "' already in use at position #" + attribute(att.name()).index());
/*  369:     */     }
/*  370: 727 */     att = (Attribute)att.copy();
/*  371: 728 */     att.setIndex(position);
/*  372:     */     
/*  373: 730 */     ArrayList<Attribute> newList = new ArrayList(this.m_Attributes.size() + 1);
/*  374: 731 */     HashMap<String, Integer> newMap = new HashMap((int)((this.m_Attributes.size() + 1) / 0.75D));
/*  375: 732 */     for (int i = 0; i < position; i++)
/*  376:     */     {
/*  377: 733 */       Attribute oldAtt = (Attribute)this.m_Attributes.get(i);
/*  378: 734 */       newList.add(oldAtt);
/*  379: 735 */       newMap.put(oldAtt.name(), Integer.valueOf(i));
/*  380:     */     }
/*  381: 737 */     newList.add(att);
/*  382: 738 */     newMap.put(att.name(), Integer.valueOf(position));
/*  383: 739 */     for (int i = position; i < this.m_Attributes.size(); i++)
/*  384:     */     {
/*  385: 740 */       Attribute newAtt = (Attribute)((Attribute)this.m_Attributes.get(i)).copy();
/*  386: 741 */       newAtt.setIndex(i + 1);
/*  387: 742 */       newList.add(newAtt);
/*  388: 743 */       newMap.put(newAtt.name(), Integer.valueOf(i + 1));
/*  389:     */     }
/*  390: 745 */     this.m_Attributes = newList;
/*  391: 746 */     this.m_NamesToAttributeIndices = newMap;
/*  392: 748 */     for (int i = 0; i < numInstances(); i++)
/*  393:     */     {
/*  394: 749 */       instance(i).setDataset(null);
/*  395: 750 */       instance(i).insertAttributeAt(position);
/*  396: 751 */       instance(i).setDataset(this);
/*  397:     */     }
/*  398: 753 */     if (this.m_ClassIndex >= position) {
/*  399: 754 */       this.m_ClassIndex += 1;
/*  400:     */     }
/*  401:     */   }
/*  402:     */   
/*  403:     */   public Instance instance(int index)
/*  404:     */   {
/*  405: 768 */     return (Instance)this.m_Instances.get(index);
/*  406:     */   }
/*  407:     */   
/*  408:     */   public Instance get(int index)
/*  409:     */   {
/*  410: 782 */     return (Instance)this.m_Instances.get(index);
/*  411:     */   }
/*  412:     */   
/*  413:     */   public double kthSmallestValue(Attribute att, int k)
/*  414:     */   {
/*  415: 794 */     return kthSmallestValue(att.index(), k);
/*  416:     */   }
/*  417:     */   
/*  418:     */   public double kthSmallestValue(int attIndex, int k)
/*  419:     */   {
/*  420: 808 */     if (!attribute(attIndex).isNumeric()) {
/*  421: 809 */       throw new IllegalArgumentException("Instances: attribute must be numeric to compute kth-smallest value.");
/*  422:     */     }
/*  423: 813 */     if ((k < 1) || (k > numInstances())) {
/*  424: 814 */       throw new IllegalArgumentException("Instances: value for k for computing kth-smallest value too large.");
/*  425:     */     }
/*  426: 818 */     double[] vals = new double[numInstances()];
/*  427: 819 */     for (int i = 0; i < vals.length; i++)
/*  428:     */     {
/*  429: 820 */       double val = instance(i).value(attIndex);
/*  430: 821 */       if (Utils.isMissingValue(val)) {
/*  431: 822 */         vals[i] = 1.7976931348623157E+308D;
/*  432:     */       } else {
/*  433: 824 */         vals[i] = val;
/*  434:     */       }
/*  435:     */     }
/*  436: 827 */     return Utils.kthSmallestValue(vals, k);
/*  437:     */   }
/*  438:     */   
/*  439:     */   public Instance lastInstance()
/*  440:     */   {
/*  441: 838 */     return (Instance)this.m_Instances.get(this.m_Instances.size() - 1);
/*  442:     */   }
/*  443:     */   
/*  444:     */   public double meanOrMode(int attIndex)
/*  445:     */   {
/*  446: 854 */     if (attribute(attIndex).isNumeric())
/*  447:     */     {
/*  448:     */       double found;
/*  449: 855 */       double result = found = 0.0D;
/*  450: 856 */       for (int j = 0; j < numInstances(); j++) {
/*  451: 857 */         if (!instance(j).isMissing(attIndex))
/*  452:     */         {
/*  453: 858 */           found += instance(j).weight();
/*  454: 859 */           result += instance(j).weight() * instance(j).value(attIndex);
/*  455:     */         }
/*  456:     */       }
/*  457: 862 */       if (found <= 0.0D) {
/*  458: 863 */         return 0.0D;
/*  459:     */       }
/*  460: 865 */       return result / found;
/*  461:     */     }
/*  462: 867 */     if (attribute(attIndex).isNominal())
/*  463:     */     {
/*  464: 868 */       int[] counts = new int[attribute(attIndex).numValues()];
/*  465: 869 */       for (int j = 0; j < numInstances(); tmp171_170++) {
/*  466: 870 */         if (!instance(j).isMissing(attIndex))
/*  467:     */         {
/*  468: 871 */           int tmp171_170 = ((int)instance(j).value(attIndex)); int[] tmp171_156 = counts;tmp171_156[tmp171_170] = ((int)(tmp171_156[tmp171_170] + instance(tmp171_170).weight()));
/*  469:     */         }
/*  470:     */       }
/*  471: 874 */       return Utils.maxIndex(counts);
/*  472:     */     }
/*  473: 876 */     return 0.0D;
/*  474:     */   }
/*  475:     */   
/*  476:     */   public double meanOrMode(Attribute att)
/*  477:     */   {
/*  478: 890 */     return meanOrMode(att.index());
/*  479:     */   }
/*  480:     */   
/*  481:     */   public int numAttributes()
/*  482:     */   {
/*  483: 901 */     return this.m_Attributes.size();
/*  484:     */   }
/*  485:     */   
/*  486:     */   public int numClasses()
/*  487:     */   {
/*  488: 914 */     if (this.m_ClassIndex < 0) {
/*  489: 915 */       throw new UnassignedClassException("Class index is negative (not set)!");
/*  490:     */     }
/*  491: 917 */     if (!classAttribute().isNominal()) {
/*  492: 918 */       return 1;
/*  493:     */     }
/*  494: 920 */     return classAttribute().numValues();
/*  495:     */   }
/*  496:     */   
/*  497:     */   public int numDistinctValues(int attIndex)
/*  498:     */   {
/*  499: 935 */     HashSet<Double> set = new HashSet(2 * numInstances());
/*  500: 936 */     for (Instance current : this)
/*  501:     */     {
/*  502: 937 */       double key = current.value(attIndex);
/*  503: 938 */       if (!Utils.isMissingValue(key)) {
/*  504: 939 */         set.add(Double.valueOf(key));
/*  505:     */       }
/*  506:     */     }
/*  507: 942 */     return set.size();
/*  508:     */   }
/*  509:     */   
/*  510:     */   public int numDistinctValues(Attribute att)
/*  511:     */   {
/*  512: 954 */     return numDistinctValues(att.index());
/*  513:     */   }
/*  514:     */   
/*  515:     */   public int numInstances()
/*  516:     */   {
/*  517: 965 */     return this.m_Instances.size();
/*  518:     */   }
/*  519:     */   
/*  520:     */   public int size()
/*  521:     */   {
/*  522: 977 */     return this.m_Instances.size();
/*  523:     */   }
/*  524:     */   
/*  525:     */   public void randomize(Random random)
/*  526:     */   {
/*  527: 987 */     for (int j = numInstances() - 1; j > 0; j--) {
/*  528: 988 */       swap(j, random.nextInt(j + 1));
/*  529:     */     }
/*  530:     */   }
/*  531:     */   
/*  532:     */   @Deprecated
/*  533:     */   public boolean readInstance(Reader reader)
/*  534:     */     throws IOException
/*  535:     */   {
/*  536:1011 */     ArffLoader.ArffReader arff = new ArffLoader.ArffReader(reader, this, this.m_Lines, 1, new String[0]);
/*  537:1012 */     Instance inst = arff.readInstance(arff.getData(), false);
/*  538:1013 */     this.m_Lines = arff.getLineNo();
/*  539:1014 */     if (inst != null)
/*  540:     */     {
/*  541:1015 */       add(inst);
/*  542:1016 */       return true;
/*  543:     */     }
/*  544:1018 */     return false;
/*  545:     */   }
/*  546:     */   
/*  547:     */   public void replaceAttributeAt(Attribute att, int position)
/*  548:     */   {
/*  549:1037 */     if ((position < 0) || (position > this.m_Attributes.size())) {
/*  550:1038 */       throw new IllegalArgumentException("Index out of range");
/*  551:     */     }
/*  552:1042 */     if (!att.name().equals(((Attribute)this.m_Attributes.get(position)).name()))
/*  553:     */     {
/*  554:1045 */       Attribute candidate = attribute(att.name());
/*  555:1046 */       if ((candidate != null) && (position != candidate.index())) {
/*  556:1047 */         throw new IllegalArgumentException("Attribute name '" + att.name() + "' already in use at position #" + attribute(att.name()).index());
/*  557:     */       }
/*  558:     */     }
/*  559:1052 */     att = (Attribute)att.copy();
/*  560:1053 */     att.setIndex(position);
/*  561:     */     
/*  562:1055 */     ArrayList<Attribute> newList = new ArrayList(this.m_Attributes.size());
/*  563:1056 */     HashMap<String, Integer> newMap = new HashMap((int)((this.m_Attributes.size() + 1) / 0.75D));
/*  564:1057 */     for (int i = 0; i < position; i++)
/*  565:     */     {
/*  566:1058 */       Attribute oldAtt = (Attribute)this.m_Attributes.get(i);
/*  567:1059 */       newList.add(oldAtt);
/*  568:1060 */       newMap.put(oldAtt.name(), Integer.valueOf(i));
/*  569:     */     }
/*  570:1062 */     newList.add(att);
/*  571:1063 */     newMap.put(att.name(), Integer.valueOf(position));
/*  572:1064 */     for (int i = position + 1; i < this.m_Attributes.size(); i++)
/*  573:     */     {
/*  574:1065 */       Attribute newAtt = (Attribute)this.m_Attributes.get(i);
/*  575:1066 */       newList.add(newAtt);
/*  576:1067 */       newMap.put(newAtt.name(), Integer.valueOf(i));
/*  577:     */     }
/*  578:1069 */     this.m_Attributes = newList;
/*  579:1070 */     this.m_NamesToAttributeIndices = newMap;
/*  580:1072 */     for (int i = 0; i < numInstances(); i++)
/*  581:     */     {
/*  582:1073 */       instance(i).setDataset(null);
/*  583:1074 */       instance(i).setMissing(position);
/*  584:1075 */       instance(i).setDataset(this);
/*  585:     */     }
/*  586:     */   }
/*  587:     */   
/*  588:     */   public String relationName()
/*  589:     */   {
/*  590:1087 */     return this.m_RelationName;
/*  591:     */   }
/*  592:     */   
/*  593:     */   public Instance remove(int index)
/*  594:     */   {
/*  595:1101 */     return (Instance)this.m_Instances.remove(index);
/*  596:     */   }
/*  597:     */   
/*  598:     */   public void renameAttribute(int att, String name)
/*  599:     */   {
/*  600:1112 */     Attribute existingAtt = attribute(name);
/*  601:1113 */     if (existingAtt != null)
/*  602:     */     {
/*  603:1114 */       if (att == existingAtt.index()) {
/*  604:1115 */         return;
/*  605:     */       }
/*  606:1117 */       throw new IllegalArgumentException("Attribute name '" + name + "' already present at position #" + existingAtt.index());
/*  607:     */     }
/*  608:1122 */     Attribute newAtt = attribute(att).copy(name);
/*  609:1123 */     ArrayList<Attribute> newVec = new ArrayList(numAttributes());
/*  610:1124 */     HashMap<String, Integer> newMap = new HashMap((int)(numAttributes() / 0.75D));
/*  611:1125 */     for (Attribute attr : this.m_Attributes) {
/*  612:1126 */       if (attr.index() == att)
/*  613:     */       {
/*  614:1127 */         newVec.add(newAtt);
/*  615:1128 */         newMap.put(name, Integer.valueOf(att));
/*  616:     */       }
/*  617:     */       else
/*  618:     */       {
/*  619:1130 */         newVec.add(attr);
/*  620:1131 */         newMap.put(attr.name(), Integer.valueOf(attr.index()));
/*  621:     */       }
/*  622:     */     }
/*  623:1134 */     this.m_Attributes = newVec;
/*  624:1135 */     this.m_NamesToAttributeIndices = newMap;
/*  625:     */   }
/*  626:     */   
/*  627:     */   public void renameAttribute(Attribute att, String name)
/*  628:     */   {
/*  629:1146 */     renameAttribute(att.index(), name);
/*  630:     */   }
/*  631:     */   
/*  632:     */   public void renameAttributeValue(int att, int val, String name)
/*  633:     */   {
/*  634:1159 */     Attribute newAtt = (Attribute)attribute(att).copy();
/*  635:1160 */     ArrayList<Attribute> newVec = new ArrayList(numAttributes());
/*  636:     */     
/*  637:1162 */     newAtt.setValue(val, name);
/*  638:1163 */     for (Attribute attr : this.m_Attributes) {
/*  639:1164 */       if (attr.index() == att) {
/*  640:1165 */         newVec.add(newAtt);
/*  641:     */       } else {
/*  642:1167 */         newVec.add(attr);
/*  643:     */       }
/*  644:     */     }
/*  645:1170 */     this.m_Attributes = newVec;
/*  646:     */   }
/*  647:     */   
/*  648:     */   public void renameAttributeValue(Attribute att, String val, String name)
/*  649:     */   {
/*  650:1183 */     int v = att.indexOfValue(val);
/*  651:1184 */     if (v == -1) {
/*  652:1185 */       throw new IllegalArgumentException(val + " not found");
/*  653:     */     }
/*  654:1187 */     renameAttributeValue(att.index(), v, name);
/*  655:     */   }
/*  656:     */   
/*  657:     */   public Instances resample(Random random)
/*  658:     */   {
/*  659:1199 */     Instances newData = new Instances(this, numInstances());
/*  660:1200 */     while (newData.numInstances() < numInstances()) {
/*  661:1201 */       newData.add(instance(random.nextInt(numInstances())));
/*  662:     */     }
/*  663:1203 */     return newData;
/*  664:     */   }
/*  665:     */   
/*  666:     */   public Instances resampleWithWeights(Random random)
/*  667:     */   {
/*  668:1217 */     return resampleWithWeights(random, false);
/*  669:     */   }
/*  670:     */   
/*  671:     */   public Instances resampleWithWeights(Random random, boolean[] sampled)
/*  672:     */   {
/*  673:1232 */     return resampleWithWeights(random, sampled, false);
/*  674:     */   }
/*  675:     */   
/*  676:     */   public Instances resampleWithWeights(Random random, boolean representUsingWeights)
/*  677:     */   {
/*  678:1249 */     return resampleWithWeights(random, null, representUsingWeights);
/*  679:     */   }
/*  680:     */   
/*  681:     */   public Instances resampleWithWeights(Random random, boolean[] sampled, boolean representUsingWeights)
/*  682:     */   {
/*  683:1267 */     double[] weights = new double[numInstances()];
/*  684:1268 */     for (int i = 0; i < weights.length; i++) {
/*  685:1269 */       weights[i] = instance(i).weight();
/*  686:     */     }
/*  687:1271 */     return resampleWithWeights(random, weights, sampled, representUsingWeights);
/*  688:     */   }
/*  689:     */   
/*  690:     */   public Instances resampleWithWeights(Random random, double[] weights)
/*  691:     */   {
/*  692:1287 */     return resampleWithWeights(random, weights, null);
/*  693:     */   }
/*  694:     */   
/*  695:     */   public Instances resampleWithWeights(Random random, double[] weights, boolean[] sampled)
/*  696:     */   {
/*  697:1308 */     return resampleWithWeights(random, weights, sampled, false);
/*  698:     */   }
/*  699:     */   
/*  700:     */   public Instances resampleWithWeights(Random random, double[] weights, boolean[] sampled, boolean representUsingWeights)
/*  701:     */   {
/*  702:1331 */     if (weights.length != numInstances()) {
/*  703:1332 */       throw new IllegalArgumentException("weights.length != numInstances.");
/*  704:     */     }
/*  705:1335 */     Instances newData = new Instances(this, numInstances());
/*  706:1336 */     if (numInstances() == 0) {
/*  707:1337 */       return newData;
/*  708:     */     }
/*  709:1341 */     double[] P = new double[weights.length];
/*  710:1342 */     System.arraycopy(weights, 0, P, 0, weights.length);
/*  711:1343 */     Utils.normalize(P);
/*  712:1344 */     double[] Q = new double[weights.length];
/*  713:1345 */     int[] A = new int[weights.length];
/*  714:1346 */     int[] W = new int[weights.length];
/*  715:1347 */     int M = weights.length;
/*  716:1348 */     int NN = -1;
/*  717:1349 */     int NP = M;
/*  718:1350 */     for (int I = 0; I < M; I++)
/*  719:     */     {
/*  720:1351 */       if (P[I] < 0.0D) {
/*  721:1352 */         throw new IllegalArgumentException("Weights have to be positive.");
/*  722:     */       }
/*  723:1354 */       Q[I] = (M * P[I]);
/*  724:1355 */       if (Q[I] < 1.0D) {
/*  725:1356 */         W[(++NN)] = I;
/*  726:     */       } else {
/*  727:1358 */         W[(--NP)] = I;
/*  728:     */       }
/*  729:     */     }
/*  730:1361 */     if ((NN > -1) && (NP < M)) {
/*  731:1362 */       for (int S = 0; S < M - 1; S++)
/*  732:     */       {
/*  733:1363 */         int I = W[S];
/*  734:1364 */         int J = W[NP];
/*  735:1365 */         A[I] = J;
/*  736:1366 */         Q[J] += Q[I] - 1.0D;
/*  737:1367 */         if (Q[J] < 1.0D) {
/*  738:1368 */           NP++;
/*  739:     */         }
/*  740:1370 */         if (NP >= M) {
/*  741:     */           break;
/*  742:     */         }
/*  743:     */       }
/*  744:     */     }
/*  745:1377 */     for (int I = 0; I < M; I++) {
/*  746:1378 */       Q[I] += I;
/*  747:     */     }
/*  748:1382 */     int[] counts = null;
/*  749:1383 */     if (representUsingWeights) {
/*  750:1384 */       counts = new int[M];
/*  751:     */     }
/*  752:1387 */     for (int i = 0; i < numInstances(); i++)
/*  753:     */     {
/*  754:1389 */       double U = M * random.nextDouble();
/*  755:1390 */       int I = (int)U;
/*  756:     */       int ALRV;
/*  757:     */       int ALRV;
/*  758:1391 */       if (U < Q[I]) {
/*  759:1392 */         ALRV = I;
/*  760:     */       } else {
/*  761:1394 */         ALRV = A[I];
/*  762:     */       }
/*  763:1396 */       if (representUsingWeights) {
/*  764:1397 */         counts[ALRV] += 1;
/*  765:     */       } else {
/*  766:1399 */         newData.add(instance(ALRV));
/*  767:     */       }
/*  768:1401 */       if (sampled != null) {
/*  769:1402 */         sampled[ALRV] = true;
/*  770:     */       }
/*  771:1404 */       if (!representUsingWeights) {
/*  772:1405 */         newData.instance(newData.numInstances() - 1).setWeight(1.0D);
/*  773:     */       }
/*  774:     */     }
/*  775:1410 */     if (representUsingWeights) {
/*  776:1411 */       for (int i = 0; i < counts.length; i++) {
/*  777:1412 */         if (counts[i] > 0)
/*  778:     */         {
/*  779:1413 */           newData.add(instance(i));
/*  780:1414 */           newData.instance(newData.numInstances() - 1).setWeight(counts[i]);
/*  781:     */         }
/*  782:     */       }
/*  783:     */     }
/*  784:1419 */     return newData;
/*  785:     */   }
/*  786:     */   
/*  787:     */   public Instance set(int index, Instance instance)
/*  788:     */   {
/*  789:1436 */     Instance newInstance = (Instance)instance.copy();
/*  790:1437 */     Instance oldInstance = (Instance)this.m_Instances.get(index);
/*  791:     */     
/*  792:1439 */     newInstance.setDataset(this);
/*  793:1440 */     this.m_Instances.set(index, newInstance);
/*  794:     */     
/*  795:1442 */     return oldInstance;
/*  796:     */   }
/*  797:     */   
/*  798:     */   public void setClass(Attribute att)
/*  799:     */   {
/*  800:1452 */     this.m_ClassIndex = att.index();
/*  801:     */   }
/*  802:     */   
/*  803:     */   public void setClassIndex(int classIndex)
/*  804:     */   {
/*  805:1464 */     if (classIndex >= numAttributes()) {
/*  806:1465 */       throw new IllegalArgumentException("Invalid class index: " + classIndex);
/*  807:     */     }
/*  808:1467 */     this.m_ClassIndex = classIndex;
/*  809:     */   }
/*  810:     */   
/*  811:     */   public void setRelationName(String newName)
/*  812:     */   {
/*  813:1477 */     this.m_RelationName = newName;
/*  814:     */   }
/*  815:     */   
/*  816:     */   protected void sortBasedOnNominalAttribute(int attIndex)
/*  817:     */   {
/*  818:1490 */     int[] counts = new int[attribute(attIndex).numValues()];
/*  819:1491 */     Instance[] backup = new Instance[numInstances()];
/*  820:1492 */     int j = 0;
/*  821:1493 */     for (Instance inst : this)
/*  822:     */     {
/*  823:1494 */       backup[(j++)] = inst;
/*  824:1495 */       if (!inst.isMissing(attIndex)) {
/*  825:1496 */         counts[((int)inst.value(attIndex))] += 1;
/*  826:     */       }
/*  827:     */     }
/*  828:1501 */     int[] indices = new int[counts.length];
/*  829:1502 */     int start = 0;
/*  830:1503 */     for (int i = 0; i < counts.length; i++)
/*  831:     */     {
/*  832:1504 */       indices[i] = start;
/*  833:1505 */       start += counts[i];
/*  834:     */     }
/*  835:1507 */     for (Instance inst : backup) {
/*  836:1508 */       if (!inst.isMissing(attIndex))
/*  837:     */       {
/*  838:1509 */         int tmp180_179 = ((int)inst.value(attIndex)); int[] tmp180_169 = indices; int tmp182_181 = tmp180_169[tmp180_179];tmp180_169[tmp180_179] = (tmp182_181 + 1);this.m_Instances.set(tmp182_181, tmp180_179);
/*  839:     */       }
/*  840:     */       else
/*  841:     */       {
/*  842:1511 */         this.m_Instances.set(start++, tmp180_179);
/*  843:     */       }
/*  844:     */     }
/*  845:     */   }
/*  846:     */   
/*  847:     */   public void sort(int attIndex)
/*  848:     */   {
/*  849:1527 */     if (!attribute(attIndex).isNominal())
/*  850:     */     {
/*  851:1530 */       double[] vals = new double[numInstances()];
/*  852:1531 */       Instance[] backup = new Instance[vals.length];
/*  853:1532 */       for (int i = 0; i < vals.length; i++)
/*  854:     */       {
/*  855:1533 */         Instance inst = instance(i);
/*  856:1534 */         backup[i] = inst;
/*  857:1535 */         double val = inst.value(attIndex);
/*  858:1536 */         if (Utils.isMissingValue(val)) {
/*  859:1537 */           vals[i] = 1.7976931348623157E+308D;
/*  860:     */         } else {
/*  861:1539 */           vals[i] = val;
/*  862:     */         }
/*  863:     */       }
/*  864:1543 */       int[] sortOrder = Utils.sortWithNoMissingValues(vals);
/*  865:1544 */       for (int i = 0; i < vals.length; i++) {
/*  866:1545 */         this.m_Instances.set(i, backup[sortOrder[i]]);
/*  867:     */       }
/*  868:     */     }
/*  869:     */     else
/*  870:     */     {
/*  871:1548 */       sortBasedOnNominalAttribute(attIndex);
/*  872:     */     }
/*  873:     */   }
/*  874:     */   
/*  875:     */   public void sort(Attribute att)
/*  876:     */   {
/*  877:1563 */     sort(att.index());
/*  878:     */   }
/*  879:     */   
/*  880:     */   public void stableSort(int attIndex)
/*  881:     */   {
/*  882:1577 */     if (!attribute(attIndex).isNominal())
/*  883:     */     {
/*  884:1580 */       double[] vals = new double[numInstances()];
/*  885:1581 */       Instance[] backup = new Instance[vals.length];
/*  886:1582 */       for (int i = 0; i < vals.length; i++)
/*  887:     */       {
/*  888:1583 */         Instance inst = instance(i);
/*  889:1584 */         backup[i] = inst;
/*  890:1585 */         vals[i] = inst.value(attIndex);
/*  891:     */       }
/*  892:1588 */       int[] sortOrder = Utils.stableSort(vals);
/*  893:1589 */       for (int i = 0; i < vals.length; i++) {
/*  894:1590 */         this.m_Instances.set(i, backup[sortOrder[i]]);
/*  895:     */       }
/*  896:     */     }
/*  897:     */     else
/*  898:     */     {
/*  899:1593 */       sortBasedOnNominalAttribute(attIndex);
/*  900:     */     }
/*  901:     */   }
/*  902:     */   
/*  903:     */   public void stableSort(Attribute att)
/*  904:     */   {
/*  905:1608 */     stableSort(att.index());
/*  906:     */   }
/*  907:     */   
/*  908:     */   public void stratify(int numFolds)
/*  909:     */   {
/*  910:1621 */     if (numFolds <= 1) {
/*  911:1622 */       throw new IllegalArgumentException("Number of folds must be greater than 1");
/*  912:     */     }
/*  913:1625 */     if (this.m_ClassIndex < 0) {
/*  914:1626 */       throw new UnassignedClassException("Class index is negative (not set)!");
/*  915:     */     }
/*  916:1628 */     if (classAttribute().isNominal())
/*  917:     */     {
/*  918:1631 */       int index = 1;
/*  919:1632 */       while (index < numInstances())
/*  920:     */       {
/*  921:1633 */         Instance instance1 = instance(index - 1);
/*  922:1634 */         for (int j = index; j < numInstances(); j++)
/*  923:     */         {
/*  924:1635 */           Instance instance2 = instance(j);
/*  925:1636 */           if ((instance1.classValue() == instance2.classValue()) || ((instance1.classIsMissing()) && (instance2.classIsMissing())))
/*  926:     */           {
/*  927:1638 */             swap(index, j);
/*  928:1639 */             index++;
/*  929:     */           }
/*  930:     */         }
/*  931:1642 */         index++;
/*  932:     */       }
/*  933:1644 */       stratStep(numFolds);
/*  934:     */     }
/*  935:     */   }
/*  936:     */   
/*  937:     */   public double sumOfWeights()
/*  938:     */   {
/*  939:1655 */     double sum = 0.0D;
/*  940:1657 */     for (int i = 0; i < numInstances(); i++) {
/*  941:1658 */       sum += instance(i).weight();
/*  942:     */     }
/*  943:1660 */     return sum;
/*  944:     */   }
/*  945:     */   
/*  946:     */   public Instances testCV(int numFolds, int numFold)
/*  947:     */   {
/*  948:1680 */     if (numFolds < 2) {
/*  949:1681 */       throw new IllegalArgumentException("Number of folds must be at least 2!");
/*  950:     */     }
/*  951:1683 */     if (numFolds > numInstances()) {
/*  952:1684 */       throw new IllegalArgumentException("Can't have more folds than instances!");
/*  953:     */     }
/*  954:1687 */     int numInstForFold = numInstances() / numFolds;
/*  955:     */     int offset;
/*  956:     */     int offset;
/*  957:1688 */     if (numFold < numInstances() % numFolds)
/*  958:     */     {
/*  959:1689 */       numInstForFold++;
/*  960:1690 */       offset = numFold;
/*  961:     */     }
/*  962:     */     else
/*  963:     */     {
/*  964:1692 */       offset = numInstances() % numFolds;
/*  965:     */     }
/*  966:1694 */     Instances test = new Instances(this, numInstForFold);
/*  967:1695 */     int first = numFold * (numInstances() / numFolds) + offset;
/*  968:1696 */     copyInstances(first, test, numInstForFold);
/*  969:1697 */     return test;
/*  970:     */   }
/*  971:     */   
/*  972:     */   public String toString()
/*  973:     */   {
/*  974:1709 */     StringBuffer text = new StringBuffer();
/*  975:     */     
/*  976:1711 */     text.append("@relation").append(" ").append(Utils.quote(this.m_RelationName)).append("\n\n");
/*  977:1713 */     for (int i = 0; i < numAttributes(); i++) {
/*  978:1714 */       text.append(attribute(i)).append("\n");
/*  979:     */     }
/*  980:1716 */     text.append("\n").append("@data").append("\n");
/*  981:     */     
/*  982:1718 */     text.append(stringWithoutHeader());
/*  983:1719 */     return text.toString();
/*  984:     */   }
/*  985:     */   
/*  986:     */   protected String stringWithoutHeader()
/*  987:     */   {
/*  988:1731 */     StringBuffer text = new StringBuffer();
/*  989:1733 */     for (int i = 0; i < numInstances(); i++)
/*  990:     */     {
/*  991:1734 */       text.append(instance(i));
/*  992:1735 */       if (i < numInstances() - 1) {
/*  993:1736 */         text.append('\n');
/*  994:     */       }
/*  995:     */     }
/*  996:1739 */     return text.toString();
/*  997:     */   }
/*  998:     */   
/*  999:     */   public Instances trainCV(int numFolds, int numFold)
/* 1000:     */   {
/* 1001:1759 */     if (numFolds < 2) {
/* 1002:1760 */       throw new IllegalArgumentException("Number of folds must be at least 2!");
/* 1003:     */     }
/* 1004:1762 */     if (numFolds > numInstances()) {
/* 1005:1763 */       throw new IllegalArgumentException("Can't have more folds than instances!");
/* 1006:     */     }
/* 1007:1766 */     int numInstForFold = numInstances() / numFolds;
/* 1008:     */     int offset;
/* 1009:     */     int offset;
/* 1010:1767 */     if (numFold < numInstances() % numFolds)
/* 1011:     */     {
/* 1012:1768 */       numInstForFold++;
/* 1013:1769 */       offset = numFold;
/* 1014:     */     }
/* 1015:     */     else
/* 1016:     */     {
/* 1017:1771 */       offset = numInstances() % numFolds;
/* 1018:     */     }
/* 1019:1773 */     Instances train = new Instances(this, numInstances() - numInstForFold);
/* 1020:1774 */     int first = numFold * (numInstances() / numFolds) + offset;
/* 1021:1775 */     copyInstances(0, train, first);
/* 1022:1776 */     copyInstances(first + numInstForFold, train, numInstances() - first - numInstForFold);
/* 1023:     */     
/* 1024:     */ 
/* 1025:1779 */     return train;
/* 1026:     */   }
/* 1027:     */   
/* 1028:     */   public Instances trainCV(int numFolds, int numFold, Random random)
/* 1029:     */   {
/* 1030:1799 */     Instances train = trainCV(numFolds, numFold);
/* 1031:1800 */     train.randomize(random);
/* 1032:1801 */     return train;
/* 1033:     */   }
/* 1034:     */   
/* 1035:     */   public double[] variances()
/* 1036:     */   {
/* 1037:1814 */     double[] vars = new double[numAttributes()];
/* 1038:1816 */     for (int i = 0; i < numAttributes(); i++) {
/* 1039:1817 */       vars[i] = (0.0D / 0.0D);
/* 1040:     */     }
/* 1041:1819 */     double[] means = new double[numAttributes()];
/* 1042:1820 */     double[] sumWeights = new double[numAttributes()];
/* 1043:1822 */     for (int i = 0; i < numInstances(); i++)
/* 1044:     */     {
/* 1045:1823 */       double weight = instance(i).weight();
/* 1046:1824 */       for (int attIndex = 0; attIndex < numAttributes(); attIndex++) {
/* 1047:1825 */         if ((attribute(attIndex).isNumeric()) && 
/* 1048:1826 */           (!instance(i).isMissing(attIndex)))
/* 1049:     */         {
/* 1050:1827 */           double value = instance(i).value(attIndex);
/* 1051:1829 */           if (Double.isNaN(vars[attIndex]))
/* 1052:     */           {
/* 1053:1832 */             means[attIndex] = value;
/* 1054:1833 */             sumWeights[attIndex] = weight;
/* 1055:1834 */             vars[attIndex] = 0.0D;
/* 1056:     */           }
/* 1057:     */           else
/* 1058:     */           {
/* 1059:1838 */             double delta = weight * (value - means[attIndex]);
/* 1060:1839 */             sumWeights[attIndex] += weight;
/* 1061:1840 */             means[attIndex] += delta / sumWeights[attIndex];
/* 1062:1841 */             vars[attIndex] += delta * (value - means[attIndex]);
/* 1063:     */           }
/* 1064:     */         }
/* 1065:     */       }
/* 1066:     */     }
/* 1067:1847 */     for (int attIndex = 0; attIndex < numAttributes(); attIndex++) {
/* 1068:1848 */       if (attribute(attIndex).isNumeric()) {
/* 1069:1849 */         if (sumWeights[attIndex] <= 1.0D)
/* 1070:     */         {
/* 1071:1850 */           vars[attIndex] = (0.0D / 0.0D);
/* 1072:     */         }
/* 1073:     */         else
/* 1074:     */         {
/* 1075:1852 */           vars[attIndex] /= (sumWeights[attIndex] - 1.0D);
/* 1076:1853 */           if (vars[attIndex] < 0.0D) {
/* 1077:1854 */             vars[attIndex] = 0.0D;
/* 1078:     */           }
/* 1079:     */         }
/* 1080:     */       }
/* 1081:     */     }
/* 1082:1859 */     return vars;
/* 1083:     */   }
/* 1084:     */   
/* 1085:     */   public double variance(int attIndex)
/* 1086:     */   {
/* 1087:1871 */     if (!attribute(attIndex).isNumeric()) {
/* 1088:1872 */       throw new IllegalArgumentException("Can't compute variance because attribute is not numeric!");
/* 1089:     */     }
/* 1090:1876 */     double mean = 0.0D;
/* 1091:1877 */     double var = (0.0D / 0.0D);
/* 1092:1878 */     double sumWeights = 0.0D;
/* 1093:1879 */     for (int i = 0; i < numInstances(); i++) {
/* 1094:1880 */       if (!instance(i).isMissing(attIndex))
/* 1095:     */       {
/* 1096:1881 */         double weight = instance(i).weight();
/* 1097:1882 */         double value = instance(i).value(attIndex);
/* 1098:1884 */         if (Double.isNaN(var))
/* 1099:     */         {
/* 1100:1887 */           mean = value;
/* 1101:1888 */           sumWeights = weight;
/* 1102:1889 */           var = 0.0D;
/* 1103:     */         }
/* 1104:     */         else
/* 1105:     */         {
/* 1106:1893 */           double delta = weight * (value - mean);
/* 1107:1894 */           sumWeights += weight;
/* 1108:1895 */           mean += delta / sumWeights;
/* 1109:1896 */           var += delta * (value - mean);
/* 1110:     */         }
/* 1111:     */       }
/* 1112:     */     }
/* 1113:1900 */     if (sumWeights <= 1.0D) {
/* 1114:1901 */       return (0.0D / 0.0D);
/* 1115:     */     }
/* 1116:1904 */     var /= (sumWeights - 1.0D);
/* 1117:1907 */     if (var < 0.0D) {
/* 1118:1908 */       return 0.0D;
/* 1119:     */     }
/* 1120:1910 */     return var;
/* 1121:     */   }
/* 1122:     */   
/* 1123:     */   public double variance(Attribute att)
/* 1124:     */   {
/* 1125:1923 */     return variance(att.index());
/* 1126:     */   }
/* 1127:     */   
/* 1128:     */   public AttributeStats attributeStats(int index)
/* 1129:     */   {
/* 1130:1936 */     AttributeStats result = new AttributeStats();
/* 1131:1937 */     if (attribute(index).isNominal())
/* 1132:     */     {
/* 1133:1938 */       result.nominalCounts = new int[attribute(index).numValues()];
/* 1134:1939 */       result.nominalWeights = new double[attribute(index).numValues()];
/* 1135:     */     }
/* 1136:1941 */     if (attribute(index).isNumeric()) {
/* 1137:1942 */       result.numericStats = new Stats();
/* 1138:     */     }
/* 1139:1944 */     result.totalCount = numInstances();
/* 1140:     */     
/* 1141:1946 */     HashMap<Double, double[]> map = new HashMap(2 * result.totalCount);
/* 1142:1947 */     for (Instance current : this)
/* 1143:     */     {
/* 1144:1948 */       double key = current.value(index);
/* 1145:1949 */       if (Utils.isMissingValue(key))
/* 1146:     */       {
/* 1147:1950 */         result.missingCount += 1;
/* 1148:     */       }
/* 1149:     */       else
/* 1150:     */       {
/* 1151:1952 */         double[] values = (double[])map.get(Double.valueOf(key));
/* 1152:1953 */         if (values == null)
/* 1153:     */         {
/* 1154:1954 */           values = new double[2];
/* 1155:1955 */           values[0] = 1.0D;
/* 1156:1956 */           values[1] = current.weight();
/* 1157:1957 */           map.put(Double.valueOf(key), values);
/* 1158:     */         }
/* 1159:     */         else
/* 1160:     */         {
/* 1161:1959 */           values[0] += 1.0D;
/* 1162:1960 */           values[1] += current.weight();
/* 1163:     */         }
/* 1164:     */       }
/* 1165:     */     }
/* 1166:1965 */     for (Map.Entry<Double, double[]> entry : map.entrySet()) {
/* 1167:1966 */       result.addDistinct(((Double)entry.getKey()).doubleValue(), (int)((double[])entry.getValue())[0], ((double[])entry.getValue())[1]);
/* 1168:     */     }
/* 1169:1968 */     return result;
/* 1170:     */   }
/* 1171:     */   
/* 1172:     */   public double[] attributeToDoubleArray(int index)
/* 1173:     */   {
/* 1174:1983 */     double[] result = new double[numInstances()];
/* 1175:1984 */     for (int i = 0; i < result.length; i++) {
/* 1176:1985 */       result[i] = instance(i).value(index);
/* 1177:     */     }
/* 1178:1987 */     return result;
/* 1179:     */   }
/* 1180:     */   
/* 1181:     */   public String toSummaryString()
/* 1182:     */   {
/* 1183:1999 */     StringBuffer result = new StringBuffer();
/* 1184:2000 */     result.append("Relation Name:  ").append(relationName()).append('\n');
/* 1185:2001 */     result.append("Num Instances:  ").append(numInstances()).append('\n');
/* 1186:2002 */     result.append("Num Attributes: ").append(numAttributes()).append('\n');
/* 1187:2003 */     result.append('\n');
/* 1188:     */     
/* 1189:2005 */     result.append(Utils.padLeft("", 5)).append(Utils.padRight("Name", 25));
/* 1190:2006 */     result.append(Utils.padLeft("Type", 5)).append(Utils.padLeft("Nom", 5));
/* 1191:2007 */     result.append(Utils.padLeft("Int", 5)).append(Utils.padLeft("Real", 5));
/* 1192:2008 */     result.append(Utils.padLeft("Missing", 12));
/* 1193:2009 */     result.append(Utils.padLeft("Unique", 12));
/* 1194:2010 */     result.append(Utils.padLeft("Dist", 6)).append('\n');
/* 1195:     */     
/* 1196:     */ 
/* 1197:2013 */     int numDigits = (int)Math.log10(numAttributes()) + 1;
/* 1198:2015 */     for (int i = 0; i < numAttributes(); i++)
/* 1199:     */     {
/* 1200:2016 */       Attribute a = attribute(i);
/* 1201:2017 */       AttributeStats as = attributeStats(i);
/* 1202:2018 */       result.append(Utils.padLeft("" + (i + 1), numDigits)).append(' ');
/* 1203:2019 */       result.append(Utils.padRight(a.name(), 25)).append(' ');
/* 1204:2021 */       switch (a.type())
/* 1205:     */       {
/* 1206:     */       case 1: 
/* 1207:2023 */         result.append(Utils.padLeft("Nom", 4)).append(' ');
/* 1208:2024 */         percent = Math.round(100.0D * as.intCount / as.totalCount);
/* 1209:2025 */         result.append(Utils.padLeft("" + percent, 3)).append("% ");
/* 1210:2026 */         result.append(Utils.padLeft("0", 3)).append("% ");
/* 1211:2027 */         percent = Math.round(100.0D * as.realCount / as.totalCount);
/* 1212:2028 */         result.append(Utils.padLeft("" + percent, 3)).append("% ");
/* 1213:2029 */         break;
/* 1214:     */       case 0: 
/* 1215:2031 */         result.append(Utils.padLeft("Num", 4)).append(' ');
/* 1216:2032 */         result.append(Utils.padLeft("0", 3)).append("% ");
/* 1217:2033 */         percent = Math.round(100.0D * as.intCount / as.totalCount);
/* 1218:2034 */         result.append(Utils.padLeft("" + percent, 3)).append("% ");
/* 1219:2035 */         percent = Math.round(100.0D * as.realCount / as.totalCount);
/* 1220:2036 */         result.append(Utils.padLeft("" + percent, 3)).append("% ");
/* 1221:2037 */         break;
/* 1222:     */       case 3: 
/* 1223:2039 */         result.append(Utils.padLeft("Dat", 4)).append(' ');
/* 1224:2040 */         result.append(Utils.padLeft("0", 3)).append("% ");
/* 1225:2041 */         percent = Math.round(100.0D * as.intCount / as.totalCount);
/* 1226:2042 */         result.append(Utils.padLeft("" + percent, 3)).append("% ");
/* 1227:2043 */         percent = Math.round(100.0D * as.realCount / as.totalCount);
/* 1228:2044 */         result.append(Utils.padLeft("" + percent, 3)).append("% ");
/* 1229:2045 */         break;
/* 1230:     */       case 2: 
/* 1231:2047 */         result.append(Utils.padLeft("Str", 4)).append(' ');
/* 1232:2048 */         percent = Math.round(100.0D * as.intCount / as.totalCount);
/* 1233:2049 */         result.append(Utils.padLeft("" + percent, 3)).append("% ");
/* 1234:2050 */         result.append(Utils.padLeft("0", 3)).append("% ");
/* 1235:2051 */         percent = Math.round(100.0D * as.realCount / as.totalCount);
/* 1236:2052 */         result.append(Utils.padLeft("" + percent, 3)).append("% ");
/* 1237:2053 */         break;
/* 1238:     */       case 4: 
/* 1239:2055 */         result.append(Utils.padLeft("Rel", 4)).append(' ');
/* 1240:2056 */         percent = Math.round(100.0D * as.intCount / as.totalCount);
/* 1241:2057 */         result.append(Utils.padLeft("" + percent, 3)).append("% ");
/* 1242:2058 */         result.append(Utils.padLeft("0", 3)).append("% ");
/* 1243:2059 */         percent = Math.round(100.0D * as.realCount / as.totalCount);
/* 1244:2060 */         result.append(Utils.padLeft("" + percent, 3)).append("% ");
/* 1245:2061 */         break;
/* 1246:     */       default: 
/* 1247:2063 */         result.append(Utils.padLeft("???", 4)).append(' ');
/* 1248:2064 */         result.append(Utils.padLeft("0", 3)).append("% ");
/* 1249:2065 */         percent = Math.round(100.0D * as.intCount / as.totalCount);
/* 1250:2066 */         result.append(Utils.padLeft("" + percent, 3)).append("% ");
/* 1251:2067 */         percent = Math.round(100.0D * as.realCount / as.totalCount);
/* 1252:2068 */         result.append(Utils.padLeft("" + percent, 3)).append("% ");
/* 1253:     */       }
/* 1254:2071 */       result.append(Utils.padLeft("" + as.missingCount, 5)).append(" /");
/* 1255:2072 */       long percent = Math.round(100.0D * as.missingCount / as.totalCount);
/* 1256:2073 */       result.append(Utils.padLeft("" + percent, 3)).append("% ");
/* 1257:2074 */       result.append(Utils.padLeft("" + as.uniqueCount, 5)).append(" /");
/* 1258:2075 */       percent = Math.round(100.0D * as.uniqueCount / as.totalCount);
/* 1259:2076 */       result.append(Utils.padLeft("" + percent, 3)).append("% ");
/* 1260:2077 */       result.append(Utils.padLeft("" + as.distinctCount, 5)).append(' ');
/* 1261:2078 */       result.append('\n');
/* 1262:     */     }
/* 1263:2080 */     return result.toString();
/* 1264:     */   }
/* 1265:     */   
/* 1266:     */   protected void copyInstances(int from, Instances dest, int num)
/* 1267:     */   {
/* 1268:2094 */     for (int i = 0; i < num; i++) {
/* 1269:2095 */       dest.add(instance(from + i));
/* 1270:     */     }
/* 1271:     */   }
/* 1272:     */   
/* 1273:     */   protected String instancesAndWeights()
/* 1274:     */   {
/* 1275:2107 */     StringBuffer text = new StringBuffer();
/* 1276:2109 */     for (int i = 0; i < numInstances(); i++)
/* 1277:     */     {
/* 1278:2110 */       text.append(instance(i) + " " + instance(i).weight());
/* 1279:2111 */       if (i < numInstances() - 1) {
/* 1280:2112 */         text.append("\n");
/* 1281:     */       }
/* 1282:     */     }
/* 1283:2115 */     return text.toString();
/* 1284:     */   }
/* 1285:     */   
/* 1286:     */   protected void stratStep(int numFolds)
/* 1287:     */   {
/* 1288:2125 */     ArrayList<Instance> newVec = new ArrayList(this.m_Instances.size());
/* 1289:2126 */     int start = 0;
/* 1290:2129 */     while (newVec.size() < numInstances())
/* 1291:     */     {
/* 1292:2130 */       int j = start;
/* 1293:2131 */       while (j < numInstances())
/* 1294:     */       {
/* 1295:2132 */         newVec.add(instance(j));
/* 1296:2133 */         j += numFolds;
/* 1297:     */       }
/* 1298:2135 */       start++;
/* 1299:     */     }
/* 1300:2137 */     this.m_Instances = newVec;
/* 1301:     */   }
/* 1302:     */   
/* 1303:     */   public void swap(int i, int j)
/* 1304:     */   {
/* 1305:2150 */     Instance in = (Instance)this.m_Instances.get(i);
/* 1306:2151 */     this.m_Instances.set(i, this.m_Instances.get(j));
/* 1307:2152 */     this.m_Instances.set(j, in);
/* 1308:     */   }
/* 1309:     */   
/* 1310:     */   public static Instances mergeInstances(Instances first, Instances second)
/* 1311:     */   {
/* 1312:2167 */     if (first.numInstances() != second.numInstances()) {
/* 1313:2168 */       throw new IllegalArgumentException("Instance sets must be of the same size");
/* 1314:     */     }
/* 1315:2173 */     ArrayList<Attribute> newAttributes = new ArrayList(first.numAttributes() + second.numAttributes());
/* 1316:2175 */     for (Attribute att : first.m_Attributes) {
/* 1317:2176 */       newAttributes.add(att);
/* 1318:     */     }
/* 1319:2178 */     for (Attribute att : second.m_Attributes) {
/* 1320:2179 */       newAttributes.add((Attribute)att.copy());
/* 1321:     */     }
/* 1322:2183 */     Instances merged = new Instances(first.relationName() + '_' + second.relationName(), newAttributes, first.numInstances());
/* 1323:2186 */     for (int i = 0; i < first.numInstances(); i++) {
/* 1324:2187 */       merged.add(first.instance(i).mergeInstance(second.instance(i)));
/* 1325:     */     }
/* 1326:2189 */     return merged;
/* 1327:     */   }
/* 1328:     */   
/* 1329:     */   public static void test(String[] argv)
/* 1330:     */   {
/* 1331:2203 */     Random random = new Random(2L);
/* 1332:     */     try
/* 1333:     */     {
/* 1334:2211 */       if (argv.length > 1) {
/* 1335:2212 */         throw new Exception("Usage: Instances [<filename>]");
/* 1336:     */       }
/* 1337:2216 */       ArrayList<String> testVals = new ArrayList(2);
/* 1338:2217 */       testVals.add("first_value");
/* 1339:2218 */       testVals.add("second_value");
/* 1340:2219 */       ArrayList<Attribute> testAtts = new ArrayList(2);
/* 1341:2220 */       testAtts.add(new Attribute("nominal_attribute", testVals));
/* 1342:2221 */       testAtts.add(new Attribute("numeric_attribute"));
/* 1343:2222 */       Instances instances = new Instances("test_set", testAtts, 10);
/* 1344:2223 */       instances.add(new DenseInstance(instances.numAttributes()));
/* 1345:2224 */       instances.add(new DenseInstance(instances.numAttributes()));
/* 1346:2225 */       instances.add(new DenseInstance(instances.numAttributes()));
/* 1347:2226 */       instances.setClassIndex(0);
/* 1348:2227 */       System.out.println("\nSet of instances created from scratch:\n");
/* 1349:2228 */       System.out.println(instances);
/* 1350:2230 */       if (argv.length == 1)
/* 1351:     */       {
/* 1352:2231 */         String filename = argv[0];
/* 1353:2232 */         Reader reader = new FileReader(filename);
/* 1354:     */         
/* 1355:     */ 
/* 1356:2235 */         System.out.println("\nFirst five instances from file:\n");
/* 1357:2236 */         instances = new Instances(reader, 1);
/* 1358:2237 */         instances.setClassIndex(instances.numAttributes() - 1);
/* 1359:2238 */         int i = 0;
/* 1360:2239 */         while ((i < 5) && (instances.readInstance(reader))) {
/* 1361:2240 */           i++;
/* 1362:     */         }
/* 1363:2242 */         System.out.println(instances);
/* 1364:     */         
/* 1365:     */ 
/* 1366:2245 */         reader = new FileReader(filename);
/* 1367:2246 */         instances = new Instances(reader);
/* 1368:     */         
/* 1369:     */ 
/* 1370:2249 */         instances.setClassIndex(instances.numAttributes() - 1);
/* 1371:     */         
/* 1372:     */ 
/* 1373:2252 */         System.out.println("\nDataset:\n");
/* 1374:2253 */         System.out.println(instances);
/* 1375:2254 */         System.out.println("\nClass index: " + instances.classIndex());
/* 1376:     */       }
/* 1377:2258 */       System.out.println("\nClass name: " + instances.classAttribute().name());
/* 1378:2259 */       System.out.println("\nClass index: " + instances.classIndex());
/* 1379:2260 */       System.out.println("\nClass is nominal: " + instances.classAttribute().isNominal());
/* 1380:     */       
/* 1381:2262 */       System.out.println("\nClass is numeric: " + instances.classAttribute().isNumeric());
/* 1382:     */       
/* 1383:2264 */       System.out.println("\nClasses:\n");
/* 1384:2265 */       for (int i = 0; i < instances.numClasses(); i++) {
/* 1385:2266 */         System.out.println(instances.classAttribute().value(i));
/* 1386:     */       }
/* 1387:2268 */       System.out.println("\nClass values and labels of instances:\n");
/* 1388:2269 */       for (i = 0; i < instances.numInstances(); i++)
/* 1389:     */       {
/* 1390:2270 */         Instance inst = instances.instance(i);
/* 1391:2271 */         System.out.print(inst.classValue() + "\t");
/* 1392:2272 */         System.out.print(inst.toString(inst.classIndex()));
/* 1393:2273 */         if (instances.instance(i).classIsMissing()) {
/* 1394:2274 */           System.out.println("\tis missing");
/* 1395:     */         } else {
/* 1396:2276 */           System.out.println();
/* 1397:     */         }
/* 1398:     */       }
/* 1399:2281 */       System.out.println("\nCreating random weights for instances.");
/* 1400:2282 */       for (i = 0; i < instances.numInstances(); i++) {
/* 1401:2283 */         instances.instance(i).setWeight(random.nextDouble());
/* 1402:     */       }
/* 1403:2287 */       System.out.println("\nInstances and their weights:\n");
/* 1404:2288 */       System.out.println(instances.instancesAndWeights());
/* 1405:2289 */       System.out.print("\nSum of weights: ");
/* 1406:2290 */       System.out.println(instances.sumOfWeights());
/* 1407:     */       
/* 1408:     */ 
/* 1409:2293 */       Instances secondInstances = new Instances(instances);
/* 1410:2294 */       Attribute testAtt = new Attribute("Inserted");
/* 1411:2295 */       secondInstances.insertAttributeAt(testAtt, 0);
/* 1412:2296 */       System.out.println("\nSet with inserted attribute:\n");
/* 1413:2297 */       System.out.println(secondInstances);
/* 1414:2298 */       System.out.println("\nClass name: " + secondInstances.classAttribute().name());
/* 1415:     */       
/* 1416:     */ 
/* 1417:     */ 
/* 1418:2302 */       secondInstances.deleteAttributeAt(0);
/* 1419:2303 */       System.out.println("\nSet with attribute deleted:\n");
/* 1420:2304 */       System.out.println(secondInstances);
/* 1421:2305 */       System.out.println("\nClass name: " + secondInstances.classAttribute().name());
/* 1422:     */       
/* 1423:     */ 
/* 1424:     */ 
/* 1425:2309 */       System.out.println("\nHeaders equal: " + instances.equalHeaders(secondInstances) + "\n");
/* 1426:     */       
/* 1427:     */ 
/* 1428:     */ 
/* 1429:2313 */       System.out.println("\nData (internal values):\n");
/* 1430:2314 */       for (i = 0; i < instances.numInstances(); i++)
/* 1431:     */       {
/* 1432:2315 */         for (int j = 0; j < instances.numAttributes(); j++) {
/* 1433:2316 */           if (instances.instance(i).isMissing(j)) {
/* 1434:2317 */             System.out.print("? ");
/* 1435:     */           } else {
/* 1436:2319 */             System.out.print(instances.instance(i).value(j) + " ");
/* 1437:     */           }
/* 1438:     */         }
/* 1439:2322 */         System.out.println();
/* 1440:     */       }
/* 1441:2326 */       System.out.println("\nEmpty dataset:\n");
/* 1442:2327 */       Instances empty = new Instances(instances, 0);
/* 1443:2328 */       System.out.println(empty);
/* 1444:2329 */       System.out.println("\nClass name: " + empty.classAttribute().name());
/* 1445:2332 */       if (empty.classAttribute().isNominal())
/* 1446:     */       {
/* 1447:2333 */         Instances copy = new Instances(empty, 0);
/* 1448:2334 */         copy.renameAttribute(copy.classAttribute(), "new_name");
/* 1449:2335 */         copy.renameAttributeValue(copy.classAttribute(), copy.classAttribute().value(0), "new_val_name");
/* 1450:     */         
/* 1451:2337 */         System.out.println("\nDataset with names changed:\n" + copy);
/* 1452:2338 */         System.out.println("\nOriginal dataset:\n" + empty);
/* 1453:     */       }
/* 1454:2342 */       int start = instances.numInstances() / 4;
/* 1455:2343 */       int num = instances.numInstances() / 2;
/* 1456:2344 */       System.out.print("\nSubset of dataset: ");
/* 1457:2345 */       System.out.println(num + " instances from " + (start + 1) + ". instance");
/* 1458:2346 */       secondInstances = new Instances(instances, start, num);
/* 1459:2347 */       System.out.println("\nClass name: " + secondInstances.classAttribute().name());
/* 1460:     */       
/* 1461:     */ 
/* 1462:     */ 
/* 1463:2351 */       System.out.println("\nInstances and their weights:\n");
/* 1464:2352 */       System.out.println(secondInstances.instancesAndWeights());
/* 1465:2353 */       System.out.print("\nSum of weights: ");
/* 1466:2354 */       System.out.println(secondInstances.sumOfWeights());
/* 1467:     */       
/* 1468:     */ 
/* 1469:     */ 
/* 1470:2358 */       System.out.println("\nTrain and test folds for 3-fold CV:");
/* 1471:2359 */       if (instances.classAttribute().isNominal()) {
/* 1472:2360 */         instances.stratify(3);
/* 1473:     */       }
/* 1474:2362 */       for (int j = 0; j < 3; j++)
/* 1475:     */       {
/* 1476:2363 */         Instances train = instances.trainCV(3, j, new Random(1L));
/* 1477:2364 */         Instances test = instances.testCV(3, j);
/* 1478:     */         
/* 1479:     */ 
/* 1480:2367 */         System.out.println("\nTrain: ");
/* 1481:2368 */         System.out.println("\nInstances and their weights:\n");
/* 1482:2369 */         System.out.println(train.instancesAndWeights());
/* 1483:2370 */         System.out.print("\nSum of weights: ");
/* 1484:2371 */         System.out.println(train.sumOfWeights());
/* 1485:2372 */         System.out.println("\nClass name: " + train.classAttribute().name());
/* 1486:2373 */         System.out.println("\nTest: ");
/* 1487:2374 */         System.out.println("\nInstances and their weights:\n");
/* 1488:2375 */         System.out.println(test.instancesAndWeights());
/* 1489:2376 */         System.out.print("\nSum of weights: ");
/* 1490:2377 */         System.out.println(test.sumOfWeights());
/* 1491:2378 */         System.out.println("\nClass name: " + test.classAttribute().name());
/* 1492:     */       }
/* 1493:2382 */       System.out.println("\nRandomized dataset:");
/* 1494:2383 */       instances.randomize(random);
/* 1495:     */       
/* 1496:     */ 
/* 1497:2386 */       System.out.println("\nInstances and their weights:\n");
/* 1498:2387 */       System.out.println(instances.instancesAndWeights());
/* 1499:2388 */       System.out.print("\nSum of weights: ");
/* 1500:2389 */       System.out.println(instances.sumOfWeights());
/* 1501:     */       
/* 1502:     */ 
/* 1503:     */ 
/* 1504:2393 */       System.out.print("\nInstances sorted according to first attribute:\n ");
/* 1505:2394 */       instances.sort(0);
/* 1506:     */       
/* 1507:     */ 
/* 1508:2397 */       System.out.println("\nInstances and their weights:\n");
/* 1509:2398 */       System.out.println(instances.instancesAndWeights());
/* 1510:2399 */       System.out.print("\nSum of weights: ");
/* 1511:2400 */       System.out.println(instances.sumOfWeights());
/* 1512:     */     }
/* 1513:     */     catch (Exception e)
/* 1514:     */     {
/* 1515:2402 */       e.printStackTrace();
/* 1516:     */     }
/* 1517:     */   }
/* 1518:     */   
/* 1519:     */   public static void main(String[] args)
/* 1520:     */   {
/* 1521:     */     try
/* 1522:     */     {
/* 1523:2442 */       if (args.length == 0)
/* 1524:     */       {
/* 1525:2443 */         ConverterUtils.DataSource source = new ConverterUtils.DataSource(System.in);
/* 1526:2444 */         Instances i = source.getDataSet();
/* 1527:2445 */         System.out.println(i.toSummaryString());
/* 1528:     */       }
/* 1529:2448 */       else if ((args.length == 1) && (!args[0].equals("-h")) && (!args[0].equals("help")))
/* 1530:     */       {
/* 1531:2450 */         ConverterUtils.DataSource source = new ConverterUtils.DataSource(args[0]);
/* 1532:2451 */         Instances i = source.getDataSet();
/* 1533:2452 */         System.out.println(i.toSummaryString());
/* 1534:     */       }
/* 1535:2455 */       else if ((args.length == 3) && (args[0].toLowerCase().equals("merge")))
/* 1536:     */       {
/* 1537:2456 */         ConverterUtils.DataSource source1 = new ConverterUtils.DataSource(args[1]);
/* 1538:2457 */         ConverterUtils.DataSource source2 = new ConverterUtils.DataSource(args[2]);
/* 1539:2458 */         Instances i = mergeInstances(source1.getDataSet(), source2.getDataSet());
/* 1540:     */         
/* 1541:2460 */         System.out.println(i);
/* 1542:     */       }
/* 1543:2463 */       else if ((args.length == 3) && (args[0].toLowerCase().equals("append")))
/* 1544:     */       {
/* 1545:2464 */         ConverterUtils.DataSource source1 = new ConverterUtils.DataSource(args[1]);
/* 1546:2465 */         ConverterUtils.DataSource source2 = new ConverterUtils.DataSource(args[2]);
/* 1547:2466 */         String msg = source1.getStructure().equalHeadersMsg(source2.getStructure());
/* 1548:2468 */         if (msg != null) {
/* 1549:2469 */           throw new Exception("The two datasets have different headers:\n" + msg);
/* 1550:     */         }
/* 1551:2472 */         Instances structure = source1.getStructure();
/* 1552:2473 */         System.out.println(source1.getStructure());
/* 1553:2474 */         while (source1.hasMoreElements(structure)) {
/* 1554:2475 */           System.out.println(source1.nextElement(structure));
/* 1555:     */         }
/* 1556:2477 */         structure = source2.getStructure();
/* 1557:2478 */         while (source2.hasMoreElements(structure)) {
/* 1558:2479 */           System.out.println(source2.nextElement(structure));
/* 1559:     */         }
/* 1560:     */       }
/* 1561:2483 */       else if ((args.length == 3) && (args[0].toLowerCase().equals("headers")))
/* 1562:     */       {
/* 1563:2484 */         ConverterUtils.DataSource source1 = new ConverterUtils.DataSource(args[1]);
/* 1564:2485 */         ConverterUtils.DataSource source2 = new ConverterUtils.DataSource(args[2]);
/* 1565:2486 */         String msg = source1.getStructure().equalHeadersMsg(source2.getStructure());
/* 1566:2488 */         if (msg == null) {
/* 1567:2489 */           System.out.println("Headers match");
/* 1568:     */         } else {
/* 1569:2491 */           System.out.println("Headers don't match:\n" + msg);
/* 1570:     */         }
/* 1571:     */       }
/* 1572:2495 */       else if ((args.length == 3) && (args[0].toLowerCase().equals("randomize")))
/* 1573:     */       {
/* 1574:2497 */         ConverterUtils.DataSource source = new ConverterUtils.DataSource(args[2]);
/* 1575:2498 */         Instances i = source.getDataSet();
/* 1576:2499 */         i.randomize(new Random(Integer.parseInt(args[1])));
/* 1577:2500 */         System.out.println(i);
/* 1578:     */       }
/* 1579:     */       else
/* 1580:     */       {
/* 1581:2504 */         System.err.println("\nUsage:\n\tweka.core.Instances help\n\t\tPrints this help\n\tweka.core.Instances <filename>\n\t\tOutputs dataset statistics\n\tweka.core.Instances merge <filename1> <filename2>\n\t\tMerges the datasets (must have same number of rows).\n\t\tGenerated dataset gets output on stdout.\n\tweka.core.Instances append <filename1> <filename2>\n\t\tAppends the second dataset to the first (must have same number of attributes).\n\t\tGenerated dataset gets output on stdout.\n\tweka.core.Instances headers <filename1> <filename2>\n\t\tCompares the structure of the two datasets and outputs whether they\n\t\tdiffer or not.\n\tweka.core.Instances randomize <seed> <filename>\n\t\tRandomizes the dataset and outputs it on stdout.\n");
/* 1582:     */       }
/* 1583:     */     }
/* 1584:     */     catch (Exception ex)
/* 1585:     */     {
/* 1586:2529 */       ex.printStackTrace();
/* 1587:2530 */       System.err.println(ex.getMessage());
/* 1588:     */     }
/* 1589:     */   }
/* 1590:     */   
/* 1591:     */   public String getRevision()
/* 1592:     */   {
/* 1593:2541 */     return RevisionUtils.extract("$Revision: 12446 $");
/* 1594:     */   }
/* 1595:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.Instances
 * JD-Core Version:    0.7.0.1
 */