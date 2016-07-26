/*    1:     */ package weka.associations;
/*    2:     */ 
/*    3:     */ import java.io.Serializable;
/*    4:     */ import java.util.ArrayList;
/*    5:     */ import java.util.Collection;
/*    6:     */ import java.util.Collections;
/*    7:     */ import java.util.Enumeration;
/*    8:     */ import java.util.HashMap;
/*    9:     */ import java.util.List;
/*   10:     */ import java.util.PriorityQueue;
/*   11:     */ import java.util.Vector;
/*   12:     */ import weka.core.Attribute;
/*   13:     */ import weka.core.AttributeStats;
/*   14:     */ import weka.core.Capabilities;
/*   15:     */ import weka.core.Capabilities.Capability;
/*   16:     */ import weka.core.CapabilitiesHandler;
/*   17:     */ import weka.core.CapabilitiesIgnorer;
/*   18:     */ import weka.core.Drawable;
/*   19:     */ import weka.core.Instance;
/*   20:     */ import weka.core.Instances;
/*   21:     */ import weka.core.Option;
/*   22:     */ import weka.core.OptionHandler;
/*   23:     */ import weka.core.RevisionHandler;
/*   24:     */ import weka.core.RevisionUtils;
/*   25:     */ import weka.core.SingleIndex;
/*   26:     */ import weka.core.Tag;
/*   27:     */ import weka.core.Utils;
/*   28:     */ 
/*   29:     */ public class HotSpot
/*   30:     */   implements Associator, OptionHandler, RevisionHandler, CapabilitiesHandler, CapabilitiesIgnorer, Drawable, AssociationRulesProducer, Serializable
/*   31:     */ {
/*   32:     */   static final long serialVersionUID = 42972325096347677L;
/*   33: 120 */   protected SingleIndex m_targetSI = new SingleIndex("last");
/*   34:     */   protected int m_target;
/*   35: 124 */   protected String m_supportString = "0.33";
/*   36:     */   protected double m_support;
/*   37:     */   private int m_supportCount;
/*   38:     */   protected double m_globalTarget;
/*   39:     */   protected double m_minImprovement;
/*   40:     */   protected int m_globalSupport;
/*   41: 142 */   protected SingleIndex m_targetIndexSI = new SingleIndex("first");
/*   42:     */   protected int m_targetIndex;
/*   43:     */   protected int m_maxBranchingFactor;
/*   44: 149 */   protected int m_maxRuleLength = -1;
/*   45:     */   protected boolean m_treatZeroAsMissing;
/*   46:     */   protected int m_numInstances;
/*   47:     */   protected int m_numNonMissingTarget;
/*   48:     */   protected HotNode m_head;
/*   49:     */   protected Instances m_header;
/*   50: 170 */   protected int m_lookups = 0;
/*   51: 171 */   protected int m_insertions = 0;
/*   52: 172 */   protected int m_hits = 0;
/*   53:     */   protected boolean m_debug;
/*   54:     */   protected boolean m_minimize;
/*   55:     */   protected String m_errorMessage;
/*   56:     */   protected HashMap<HotSpotHashKey, String> m_ruleLookup;
/*   57: 186 */   protected boolean m_outputRules = false;
/*   58:     */   protected boolean m_doNotCheckCapabilities;
/*   59:     */   
/*   60:     */   public HotSpot()
/*   61:     */   {
/*   62: 195 */     resetOptions();
/*   63:     */   }
/*   64:     */   
/*   65:     */   public String globalInfo()
/*   66:     */   {
/*   67: 205 */     return "HotSpot learns a set of rules (displayed in a tree-like structure) that maximize/minimize a target variable/value of interest. With a nominal target, one might want to look for segments of the data where there is a high probability of a minority value occuring (given the constraint of a minimum support). For a numeric target, one might be interested in finding segments where this is higher on average than in the whole data set. For example, in a health insurance scenario, find which health insurance groups are at the highest risk (have the highest claim ratio), or, which groups have the highest average insurance payout.  This algorithm is similar in spirit to the PRIM bump hunting algorithm described by Friedman and Fisher (1999).";
/*   68:     */   }
/*   69:     */   
/*   70:     */   public Capabilities getCapabilities()
/*   71:     */   {
/*   72: 226 */     Capabilities result = new Capabilities(this);
/*   73: 227 */     result.disableAll();
/*   74:     */     
/*   75:     */ 
/*   76: 230 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*   77: 231 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*   78: 232 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*   79:     */     
/*   80:     */ 
/*   81: 235 */     result.enable(Capabilities.Capability.NO_CLASS);
/*   82:     */     
/*   83:     */ 
/*   84:     */ 
/*   85: 239 */     return result;
/*   86:     */   }
/*   87:     */   
/*   88:     */   protected class HotSpotHashKey
/*   89:     */     implements Serializable
/*   90:     */   {
/*   91:     */     private static final long serialVersionUID = 3962829200560373755L;
/*   92:     */     protected double[] m_splitValues;
/*   93:     */     protected byte[] m_testTypes;
/*   94: 258 */     protected boolean m_computed = false;
/*   95:     */     protected int m_key;
/*   96:     */     
/*   97:     */     public HotSpotHashKey(double[] splitValues, byte[] testTypes)
/*   98:     */     {
/*   99: 262 */       this.m_splitValues = ((double[])splitValues.clone());
/*  100: 263 */       this.m_testTypes = ((byte[])testTypes.clone());
/*  101:     */     }
/*  102:     */     
/*  103:     */     public boolean equals(Object b)
/*  104:     */     {
/*  105: 268 */       if ((b == null) || (!b.getClass().equals(getClass()))) {
/*  106: 269 */         return false;
/*  107:     */       }
/*  108: 271 */       HotSpotHashKey comp = (HotSpotHashKey)b;
/*  109: 272 */       boolean ok = true;
/*  110: 273 */       for (int i = 0; i < this.m_splitValues.length; i++) {
/*  111: 274 */         if ((this.m_splitValues[i] != comp.m_splitValues[i]) || (this.m_testTypes[i] != comp.m_testTypes[i]))
/*  112:     */         {
/*  113: 276 */           ok = false;
/*  114: 277 */           break;
/*  115:     */         }
/*  116:     */       }
/*  117: 280 */       return ok;
/*  118:     */     }
/*  119:     */     
/*  120:     */     public int hashCode()
/*  121:     */     {
/*  122: 286 */       if (this.m_computed) {
/*  123: 287 */         return this.m_key;
/*  124:     */       }
/*  125: 289 */       int hv = 0;
/*  126: 290 */       for (int i = 0; i < this.m_splitValues.length; i++)
/*  127:     */       {
/*  128: 291 */         hv = (int)(hv + this.m_splitValues[i] * 5.0D * i);
/*  129: 292 */         hv += this.m_testTypes[i] * i * 3;
/*  130:     */       }
/*  131: 294 */       this.m_computed = true;
/*  132:     */       
/*  133: 296 */       this.m_key = hv;
/*  134:     */       
/*  135: 298 */       return this.m_key;
/*  136:     */     }
/*  137:     */   }
/*  138:     */   
/*  139:     */   public void buildAssociations(Instances instances)
/*  140:     */     throws Exception
/*  141:     */   {
/*  142: 312 */     getCapabilities().testWithFail(instances);
/*  143: 314 */     if ((this.m_supportString == null) || (this.m_supportString.length() == 0)) {
/*  144: 315 */       throw new Exception("No support value provided!");
/*  145:     */     }
/*  146: 317 */     this.m_support = Double.parseDouble(this.m_supportString);
/*  147:     */     
/*  148: 319 */     this.m_errorMessage = null;
/*  149:     */     try
/*  150:     */     {
/*  151: 321 */       this.m_targetSI.setUpper(instances.numAttributes() - 1);
/*  152: 322 */       this.m_target = this.m_targetSI.getIndex();
/*  153:     */     }
/*  154:     */     catch (Exception ex)
/*  155:     */     {
/*  156: 325 */       String value = this.m_targetSI.getSingleIndex();
/*  157: 326 */       int index = -1;
/*  158: 327 */       for (int i = 0; i < instances.numAttributes(); i++) {
/*  159: 328 */         if (instances.attribute(i).name().indexOf(value) > -1)
/*  160:     */         {
/*  161: 329 */           index = i;
/*  162: 330 */           break;
/*  163:     */         }
/*  164:     */       }
/*  165: 334 */       if (index == -1) {
/*  166: 335 */         throw new Exception("Can't find an attribute containing the string " + value);
/*  167:     */       }
/*  168: 339 */       this.m_target = index;
/*  169:     */     }
/*  170: 342 */     Instances inst = new Instances(instances);
/*  171: 343 */     inst.setClassIndex(this.m_target);
/*  172: 346 */     if (inst.attribute(this.m_target).isNominal())
/*  173:     */     {
/*  174: 347 */       this.m_targetIndexSI.setUpper(inst.attribute(this.m_target).numValues() - 1);
/*  175: 348 */       this.m_targetIndex = this.m_targetIndexSI.getIndex();
/*  176:     */     }
/*  177:     */     else
/*  178:     */     {
/*  179: 350 */       this.m_targetIndexSI.setUpper(1);
/*  180:     */     }
/*  181: 353 */     if (inst.attribute(this.m_target).isNumeric())
/*  182:     */     {
/*  183: 354 */       if (this.m_supportCount > this.m_numInstances)
/*  184:     */       {
/*  185: 355 */         this.m_errorMessage = "Error: support set to more instances than there are in the data!";
/*  186:     */         
/*  187: 357 */         return;
/*  188:     */       }
/*  189: 359 */       this.m_globalTarget = inst.meanOrMode(this.m_target);
/*  190: 360 */       this.m_numNonMissingTarget = (inst.numInstances() - inst.attributeStats(this.m_target).missingCount);
/*  191:     */     }
/*  192:     */     else
/*  193:     */     {
/*  194: 363 */       double[] probs = new double[inst.attributeStats(this.m_target).nominalCounts.length];
/*  195: 365 */       for (int i = 0; i < probs.length; i++) {
/*  196: 366 */         probs[i] = inst.attributeStats(this.m_target).nominalCounts[i];
/*  197:     */       }
/*  198: 368 */       this.m_globalSupport = ((int)probs[this.m_targetIndex]);
/*  199: 370 */       if (this.m_globalSupport < this.m_supportCount) {
/*  200: 371 */         this.m_errorMessage = ("Error: minimum support " + this.m_supportCount + " is too high. Target value " + this.m_header.attribute(this.m_target).value(this.m_targetIndex) + " has support " + this.m_globalSupport + ".");
/*  201:     */       }
/*  202: 378 */       for (int i = 0; i < probs.length; i++) {
/*  203: 379 */         probs[i] /= inst.numInstances();
/*  204:     */       }
/*  205: 381 */       this.m_globalTarget = probs[this.m_targetIndex];
/*  206:     */     }
/*  207: 388 */     if (this.m_support <= 0.0D) {
/*  208: 389 */       throw new Exception("Support must be greater than zero.");
/*  209:     */     }
/*  210: 392 */     this.m_numInstances = inst.numInstances();
/*  211: 393 */     if (this.m_support >= 1.0D)
/*  212:     */     {
/*  213: 394 */       this.m_supportCount = ((int)this.m_support);
/*  214: 395 */       if (inst.attribute(this.m_target).isNumeric()) {
/*  215: 396 */         this.m_support /= this.m_numInstances;
/*  216:     */       } else {
/*  217: 399 */         this.m_support /= this.m_globalSupport;
/*  218:     */       }
/*  219:     */     }
/*  220: 402 */     this.m_supportCount = (inst.attribute(this.m_target).isNumeric() ? (int)Math.floor(this.m_support * this.m_numInstances + 0.5D) : (int)Math.floor(this.m_support * this.m_globalSupport + 0.5D));
/*  221: 406 */     if (this.m_supportCount < 1) {
/*  222: 407 */       this.m_supportCount = 1;
/*  223:     */     }
/*  224: 410 */     this.m_header = new Instances(inst, 0);
/*  225:     */     
/*  226: 412 */     this.m_ruleLookup = new HashMap();
/*  227: 413 */     double[] splitVals = new double[this.m_header.numAttributes()];
/*  228: 414 */     byte[] tests = new byte[this.m_header.numAttributes()];
/*  229:     */     
/*  230: 416 */     this.m_head = new HotNode(inst, this.m_globalTarget, splitVals, tests, 0);
/*  231:     */   }
/*  232:     */   
/*  233:     */   public String toString()
/*  234:     */   {
/*  235: 427 */     StringBuffer buff = new StringBuffer();
/*  236: 428 */     buff.append("\nHot Spot\n========");
/*  237: 429 */     if (this.m_errorMessage != null)
/*  238:     */     {
/*  239: 430 */       buff.append("\n\n" + this.m_errorMessage + "\n\n");
/*  240: 431 */       return buff.toString();
/*  241:     */     }
/*  242: 433 */     if (this.m_head == null)
/*  243:     */     {
/*  244: 434 */       buff.append("No model built!");
/*  245: 435 */       return buff.toString();
/*  246:     */     }
/*  247: 437 */     buff.append("\nTotal population: ");
/*  248: 438 */     buff.append("" + this.m_numInstances + " instances");
/*  249: 439 */     buff.append("\nTarget attribute: " + this.m_header.attribute(this.m_target).name());
/*  250: 440 */     if (this.m_header.attribute(this.m_target).isNominal())
/*  251:     */     {
/*  252: 441 */       buff.append("\nTarget value: " + this.m_header.attribute(this.m_target).value(this.m_targetIndex));
/*  253:     */       
/*  254: 443 */       buff.append(" [value count in total population: " + this.m_globalSupport + " instances (" + Utils.doubleToString(this.m_globalTarget * 100.0D, 2) + "%)]");
/*  255:     */       
/*  256:     */ 
/*  257:     */ 
/*  258: 447 */       buff.append("\nMinimum value count for segments: ");
/*  259:     */     }
/*  260:     */     else
/*  261:     */     {
/*  262: 449 */       buff.append("\nTarget average in total population: " + Utils.doubleToString(this.m_globalTarget, 3));
/*  263:     */       
/*  264: 451 */       buff.append("\nMinimum segment size: ");
/*  265:     */     }
/*  266: 453 */     buff.append("" + this.m_supportCount + " instances (" + Utils.doubleToString(this.m_support * 100.0D, 2) + "% of " + (this.m_header.attribute(this.m_target).isNominal() ? "target value " : "") + "total population)");
/*  267:     */     
/*  268:     */ 
/*  269:     */ 
/*  270:     */ 
/*  271:     */ 
/*  272: 459 */     buff.append("\nMaximum branching factor: " + this.m_maxBranchingFactor);
/*  273: 460 */     buff.append("\nMaximum rule length: " + (this.m_maxRuleLength < 0 ? "unbounded" : new StringBuilder().append("").append(this.m_maxRuleLength).toString()));
/*  274:     */     
/*  275: 462 */     buff.append("\nMinimum improvement in target: " + Utils.doubleToString(this.m_minImprovement * 100.0D, 2) + "%");
/*  276:     */     
/*  277:     */ 
/*  278: 465 */     buff.append("\n\n");
/*  279: 466 */     if (!this.m_outputRules)
/*  280:     */     {
/*  281: 467 */       buff.append(this.m_header.attribute(this.m_target).name());
/*  282: 468 */       if (this.m_header.attribute(this.m_target).isNumeric())
/*  283:     */       {
/*  284: 469 */         buff.append(" (" + Utils.doubleToString(this.m_globalTarget, 4) + ")");
/*  285:     */       }
/*  286:     */       else
/*  287:     */       {
/*  288: 471 */         buff.append("=" + this.m_header.attribute(this.m_target).value(this.m_targetIndex) + " (");
/*  289:     */         
/*  290: 473 */         buff.append("" + Utils.doubleToString(this.m_globalTarget * 100.0D, 2) + "% [");
/*  291:     */         
/*  292: 475 */         buff.append("" + this.m_globalSupport + "/" + this.m_numInstances + "])");
/*  293:     */       }
/*  294:     */     }
/*  295: 479 */     if (!this.m_outputRules)
/*  296:     */     {
/*  297: 480 */       this.m_head.dumpTree(0, buff);
/*  298:     */     }
/*  299:     */     else
/*  300:     */     {
/*  301: 482 */       List<AssociationRule> rules = new ArrayList();
/*  302:     */       try
/*  303:     */       {
/*  304: 484 */         this.m_head.getRules(rules, new ArrayList());
/*  305: 485 */         Collections.sort(rules);
/*  306: 486 */         for (AssociationRule r : rules) {
/*  307: 487 */           buff.append(r.toString() + "\n");
/*  308:     */         }
/*  309:     */       }
/*  310:     */       catch (Exception e)
/*  311:     */       {
/*  312: 490 */         e.printStackTrace();
/*  313:     */       }
/*  314:     */     }
/*  315: 493 */     buff.append("\n");
/*  316: 495 */     if (this.m_debug)
/*  317:     */     {
/*  318: 496 */       buff.append("\n=== Duplicate rule lookup hashtable stats ===\n");
/*  319: 497 */       buff.append("Insertions: " + this.m_insertions);
/*  320: 498 */       buff.append("\nLookups : " + this.m_lookups);
/*  321: 499 */       buff.append("\nHits: " + this.m_hits);
/*  322: 500 */       buff.append("\n");
/*  323:     */     }
/*  324: 503 */     return buff.toString();
/*  325:     */   }
/*  326:     */   
/*  327:     */   public String graph()
/*  328:     */     throws Exception
/*  329:     */   {
/*  330: 508 */     this.m_head.assignIDs(-1);
/*  331:     */     
/*  332: 510 */     StringBuffer text = new StringBuffer();
/*  333:     */     
/*  334: 512 */     text.append("digraph HotSpot {\n");
/*  335: 513 */     text.append("rankdir=LR;\n");
/*  336: 514 */     text.append("N0 [label=\"" + this.m_header.attribute(this.m_target).name());
/*  337: 516 */     if (this.m_header.attribute(this.m_target).isNumeric())
/*  338:     */     {
/*  339: 517 */       text.append("\\n(" + Utils.doubleToString(this.m_globalTarget, 4) + ")");
/*  340:     */     }
/*  341:     */     else
/*  342:     */     {
/*  343: 519 */       text.append("=" + this.m_header.attribute(this.m_target).value(this.m_targetIndex) + "\\n(");
/*  344:     */       
/*  345: 521 */       text.append("" + Utils.doubleToString(this.m_globalTarget * 100.0D, 2) + "% [");
/*  346:     */       
/*  347: 523 */       text.append("" + this.m_globalSupport + "/" + this.m_numInstances + "])");
/*  348:     */     }
/*  349: 525 */     text.append("\" shape=plaintext]\n");
/*  350:     */     
/*  351: 527 */     this.m_head.graphHotSpot(text);
/*  352:     */     
/*  353: 529 */     text.append("}\n");
/*  354: 530 */     return text.toString();
/*  355:     */   }
/*  356:     */   
/*  357:     */   protected class HotNode
/*  358:     */     implements Serializable
/*  359:     */   {
/*  360:     */     private static final long serialVersionUID = -4665984155566279901L;
/*  361:     */     protected Instances m_insts;
/*  362:     */     protected double m_targetValue;
/*  363:     */     protected HotNode[] m_children;
/*  364:     */     protected HotTestDetails[] m_testDetails;
/*  365:     */     public int m_id;
/*  366:     */     
/*  367:     */     protected class HotTestDetails
/*  368:     */       implements Comparable<HotTestDetails>, Serializable
/*  369:     */     {
/*  370:     */       private static final long serialVersionUID = -8403762320170624616L;
/*  371:     */       public double m_merit;
/*  372:     */       public int m_supportLevel;
/*  373:     */       public int m_subsetSize;
/*  374:     */       public int m_splitAttIndex;
/*  375:     */       public double m_splitValue;
/*  376:     */       public boolean m_lessThan;
/*  377:     */       
/*  378:     */       public HotTestDetails(int attIndex, double splitVal, boolean lessThan, int support, int subsetSize, double merit)
/*  379:     */       {
/*  380: 562 */         this.m_merit = merit;
/*  381: 563 */         this.m_splitAttIndex = attIndex;
/*  382: 564 */         this.m_splitValue = splitVal;
/*  383: 565 */         this.m_lessThan = lessThan;
/*  384: 566 */         this.m_supportLevel = support;
/*  385: 567 */         this.m_subsetSize = subsetSize;
/*  386:     */       }
/*  387:     */       
/*  388:     */       public int compareTo(HotTestDetails comp)
/*  389:     */       {
/*  390: 574 */         int result = 0;
/*  391: 575 */         if (HotSpot.this.m_minimize)
/*  392:     */         {
/*  393: 576 */           if (this.m_merit == comp.m_merit)
/*  394:     */           {
/*  395: 578 */             if (this.m_supportLevel != comp.m_supportLevel) {
/*  396: 579 */               if (this.m_supportLevel > comp.m_supportLevel) {
/*  397: 580 */                 result = -1;
/*  398:     */               } else {
/*  399: 582 */                 result = 1;
/*  400:     */               }
/*  401:     */             }
/*  402:     */           }
/*  403: 584 */           else if (this.m_merit < comp.m_merit) {
/*  404: 585 */             result = -1;
/*  405:     */           } else {
/*  406: 587 */             result = 1;
/*  407:     */           }
/*  408:     */         }
/*  409: 590 */         else if (this.m_merit == comp.m_merit)
/*  410:     */         {
/*  411: 592 */           if (this.m_supportLevel != comp.m_supportLevel) {
/*  412: 593 */             if (this.m_supportLevel > comp.m_supportLevel) {
/*  413: 594 */               result = -1;
/*  414:     */             } else {
/*  415: 596 */               result = 1;
/*  416:     */             }
/*  417:     */           }
/*  418:     */         }
/*  419: 598 */         else if (this.m_merit < comp.m_merit) {
/*  420: 599 */           result = 1;
/*  421:     */         } else {
/*  422: 601 */           result = -1;
/*  423:     */         }
/*  424: 604 */         return result;
/*  425:     */       }
/*  426:     */     }
/*  427:     */     
/*  428:     */     public HotNode(Instances insts, double targetValue, double[] splitVals, byte[] tests, int depth)
/*  429:     */     {
/*  430: 633 */       if (depth == HotSpot.this.m_maxRuleLength) {
/*  431: 634 */         return;
/*  432:     */       }
/*  433: 637 */       this.m_insts = insts;
/*  434: 638 */       this.m_targetValue = targetValue;
/*  435: 639 */       PriorityQueue<HotTestDetails> splitQueue = new PriorityQueue();
/*  436: 643 */       for (int i = 0; i < this.m_insts.numAttributes(); i++) {
/*  437: 644 */         if (i != HotSpot.this.m_target) {
/*  438: 645 */           if (this.m_insts.attribute(i).isNominal()) {
/*  439: 646 */             evaluateNominal(i, splitQueue);
/*  440:     */           } else {
/*  441: 648 */             evaluateNumeric(i, splitQueue);
/*  442:     */           }
/*  443:     */         }
/*  444:     */       }
/*  445: 653 */       if (splitQueue.size() > 0)
/*  446:     */       {
/*  447: 654 */         int queueSize = splitQueue.size();
/*  448:     */         
/*  449:     */ 
/*  450: 657 */         ArrayList<HotTestDetails> newCandidates = new ArrayList();
/*  451:     */         
/*  452: 659 */         ArrayList<HotSpot.HotSpotHashKey> keyList = new ArrayList();
/*  453: 661 */         for (int i = 0; i < queueSize; i++)
/*  454:     */         {
/*  455: 662 */           if (newCandidates.size() >= HotSpot.this.m_maxBranchingFactor) {
/*  456:     */             break;
/*  457:     */           }
/*  458: 663 */           HotTestDetails temp = (HotTestDetails)splitQueue.poll();
/*  459: 664 */           double[] newSplitVals = (double[])splitVals.clone();
/*  460: 665 */           byte[] newTests = (byte[])tests.clone();
/*  461: 666 */           newSplitVals[temp.m_splitAttIndex] = (temp.m_splitValue + 1.0D);
/*  462: 667 */           newTests[temp.m_splitAttIndex] = (temp.m_lessThan ? 1 : HotSpot.this.m_header.attribute(temp.m_splitAttIndex).isNominal() ? 2 : 3);
/*  463:     */           
/*  464:     */ 
/*  465: 670 */           HotSpot.HotSpotHashKey key = new HotSpot.HotSpotHashKey(HotSpot.this, newSplitVals, newTests);
/*  466: 671 */           HotSpot.this.m_lookups += 1;
/*  467: 672 */           if (!HotSpot.this.m_ruleLookup.containsKey(key))
/*  468:     */           {
/*  469: 674 */             HotSpot.this.m_ruleLookup.put(key, "");
/*  470: 675 */             newCandidates.add(temp);
/*  471: 676 */             keyList.add(key);
/*  472: 677 */             HotSpot.this.m_insertions += 1;
/*  473:     */           }
/*  474:     */           else
/*  475:     */           {
/*  476: 679 */             HotSpot.this.m_hits += 1;
/*  477:     */           }
/*  478:     */         }
/*  479: 686 */         this.m_children = new HotNode[newCandidates.size() < HotSpot.this.m_maxBranchingFactor ? newCandidates.size() : HotSpot.this.m_maxBranchingFactor];
/*  480:     */         
/*  481:     */ 
/*  482:     */ 
/*  483:     */ 
/*  484: 691 */         this.m_testDetails = new HotTestDetails[this.m_children.length];
/*  485: 692 */         for (int i = 0; i < this.m_children.length; i++) {
/*  486: 693 */           this.m_testDetails[i] = ((HotTestDetails)newCandidates.get(i));
/*  487:     */         }
/*  488: 697 */         splitQueue = null;
/*  489: 698 */         newCandidates = null;
/*  490: 699 */         this.m_insts = new Instances(this.m_insts, 0);
/*  491: 702 */         for (int i = 0; i < this.m_children.length; i++)
/*  492:     */         {
/*  493: 703 */           Instances subset = subset(insts, this.m_testDetails[i]);
/*  494: 704 */           HotSpot.HotSpotHashKey tempKey = (HotSpot.HotSpotHashKey)keyList.get(i);
/*  495: 705 */           this.m_children[i] = new HotNode(HotSpot.this, subset, this.m_testDetails[i].m_merit, tempKey.m_splitValues, tempKey.m_testTypes, depth + 1);
/*  496:     */         }
/*  497:     */       }
/*  498:     */     }
/*  499:     */     
/*  500:     */     private Instances subset(Instances insts, HotTestDetails test)
/*  501:     */     {
/*  502: 719 */       Instances sub = new Instances(insts, insts.numInstances());
/*  503: 720 */       for (int i = 0; i < insts.numInstances(); i++)
/*  504:     */       {
/*  505: 721 */         Instance temp = insts.instance(i);
/*  506: 722 */         if (!temp.isMissing(test.m_splitAttIndex)) {
/*  507: 723 */           if (insts.attribute(test.m_splitAttIndex).isNominal())
/*  508:     */           {
/*  509: 724 */             if (temp.value(test.m_splitAttIndex) == test.m_splitValue) {
/*  510: 725 */               sub.add(temp);
/*  511:     */             }
/*  512:     */           }
/*  513: 728 */           else if (test.m_lessThan)
/*  514:     */           {
/*  515: 729 */             if (temp.value(test.m_splitAttIndex) <= test.m_splitValue) {
/*  516: 730 */               sub.add(temp);
/*  517:     */             }
/*  518:     */           }
/*  519: 733 */           else if (temp.value(test.m_splitAttIndex) > test.m_splitValue) {
/*  520: 734 */             sub.add(temp);
/*  521:     */           }
/*  522:     */         }
/*  523:     */       }
/*  524: 740 */       sub.compactify();
/*  525: 741 */       return sub;
/*  526:     */     }
/*  527:     */     
/*  528:     */     private void evaluateNumeric(int attIndex, PriorityQueue<HotTestDetails> pq)
/*  529:     */     {
/*  530: 752 */       Instances tempInsts = this.m_insts;
/*  531: 753 */       tempInsts.sort(attIndex);
/*  532:     */       
/*  533:     */ 
/*  534: 756 */       double targetLeft = 0.0D;
/*  535: 757 */       double targetRight = 0.0D;
/*  536:     */       
/*  537: 759 */       int numMissing = 0;
/*  538: 761 */       for (int i = tempInsts.numInstances() - 1; i >= 0; i--) {
/*  539: 762 */         if (!tempInsts.instance(i).isMissing(attIndex))
/*  540:     */         {
/*  541: 763 */           if (!tempInsts.instance(i).isMissing(HotSpot.this.m_target)) {
/*  542: 764 */             targetRight += (tempInsts.attribute(HotSpot.this.m_target).isNumeric() ? tempInsts.instance(i).value(HotSpot.this.m_target) : tempInsts.instance(i).value(HotSpot.this.m_target) == HotSpot.this.m_targetIndex ? 1 : 0);
/*  543:     */           }
/*  544:     */         }
/*  545:     */         else {
/*  546: 770 */           numMissing++;
/*  547:     */         }
/*  548:     */       }
/*  549: 775 */       if (tempInsts.numInstances() - numMissing <= HotSpot.this.m_supportCount) {
/*  550: 776 */         return;
/*  551:     */       }
/*  552: 779 */       double bestMerit = 0.0D;
/*  553: 780 */       double bestSplit = 0.0D;
/*  554: 781 */       double bestSupport = 0.0D;
/*  555: 782 */       double bestSubsetSize = 0.0D;
/*  556: 783 */       boolean lessThan = true;
/*  557:     */       
/*  558:     */ 
/*  559: 786 */       double leftCount = 0.0D;
/*  560: 787 */       double rightCount = tempInsts.numInstances() - numMissing;
/*  561: 798 */       for (int i = 0; i < tempInsts.numInstances() - numMissing; i++)
/*  562:     */       {
/*  563: 799 */         Instance inst = tempInsts.instance(i);
/*  564: 801 */         if (!inst.isMissing(HotSpot.this.m_target))
/*  565:     */         {
/*  566: 802 */           if (tempInsts.attribute(HotSpot.this.m_target).isNumeric())
/*  567:     */           {
/*  568: 803 */             targetLeft += inst.value(HotSpot.this.m_target);
/*  569: 804 */             targetRight -= inst.value(HotSpot.this.m_target);
/*  570:     */           }
/*  571: 806 */           else if ((int)inst.value(HotSpot.this.m_target) == HotSpot.this.m_targetIndex)
/*  572:     */           {
/*  573: 807 */             targetLeft += 1.0D;
/*  574: 808 */             targetRight -= 1.0D;
/*  575:     */           }
/*  576: 811 */           leftCount += 1.0D;
/*  577: 812 */           rightCount -= 1.0D;
/*  578: 815 */           if ((i < tempInsts.numInstances() - 1) && (inst.value(attIndex) == tempInsts.instance(i + 1).value(attIndex))) {}
/*  579:     */         }
/*  580: 823 */         else if (tempInsts.attribute(HotSpot.this.m_target).isNominal())
/*  581:     */         {
/*  582: 824 */           if (targetLeft >= HotSpot.this.m_supportCount)
/*  583:     */           {
/*  584: 825 */             double delta = HotSpot.this.m_minimize ? bestMerit - targetLeft / leftCount : targetLeft / leftCount - bestMerit;
/*  585: 829 */             if (delta > 0.0D)
/*  586:     */             {
/*  587: 830 */               bestMerit = targetLeft / leftCount;
/*  588: 831 */               bestSplit = inst.value(attIndex);
/*  589: 832 */               bestSupport = targetLeft;
/*  590: 833 */               bestSubsetSize = leftCount;
/*  591: 834 */               lessThan = true;
/*  592:     */             }
/*  593: 836 */             else if (delta == 0.0D)
/*  594:     */             {
/*  595: 838 */               if (targetLeft > bestSupport)
/*  596:     */               {
/*  597: 839 */                 bestMerit = targetLeft / leftCount;
/*  598: 840 */                 bestSplit = inst.value(attIndex);
/*  599: 841 */                 bestSupport = targetLeft;
/*  600: 842 */                 bestSubsetSize = leftCount;
/*  601: 843 */                 lessThan = true;
/*  602:     */               }
/*  603:     */             }
/*  604:     */           }
/*  605: 848 */           if (targetRight >= HotSpot.this.m_supportCount)
/*  606:     */           {
/*  607: 849 */             double delta = HotSpot.this.m_minimize ? bestMerit - targetRight / rightCount : targetRight / rightCount - bestMerit;
/*  608: 853 */             if (delta > 0.0D)
/*  609:     */             {
/*  610: 854 */               bestMerit = targetRight / rightCount;
/*  611: 855 */               bestSplit = inst.value(attIndex);
/*  612: 856 */               bestSupport = targetRight;
/*  613: 857 */               bestSubsetSize = rightCount;
/*  614: 858 */               lessThan = false;
/*  615:     */             }
/*  616: 860 */             else if (delta == 0.0D)
/*  617:     */             {
/*  618: 862 */               if (targetRight > bestSupport)
/*  619:     */               {
/*  620: 863 */                 bestMerit = targetRight / rightCount;
/*  621: 864 */                 bestSplit = inst.value(attIndex);
/*  622: 865 */                 bestSupport = targetRight;
/*  623: 866 */                 bestSubsetSize = rightCount;
/*  624: 867 */                 lessThan = false;
/*  625:     */               }
/*  626:     */             }
/*  627:     */           }
/*  628:     */         }
/*  629:     */         else
/*  630:     */         {
/*  631: 872 */           if (leftCount >= HotSpot.this.m_supportCount)
/*  632:     */           {
/*  633: 873 */             double delta = HotSpot.this.m_minimize ? bestMerit - targetLeft / leftCount : targetLeft / leftCount - bestMerit;
/*  634: 877 */             if (delta > 0.0D)
/*  635:     */             {
/*  636: 878 */               bestMerit = targetLeft / leftCount;
/*  637: 879 */               bestSplit = inst.value(attIndex);
/*  638: 880 */               bestSupport = leftCount;
/*  639: 881 */               bestSubsetSize = leftCount;
/*  640: 882 */               lessThan = true;
/*  641:     */             }
/*  642: 884 */             else if (delta == 0.0D)
/*  643:     */             {
/*  644: 886 */               if (leftCount > bestSupport)
/*  645:     */               {
/*  646: 887 */                 bestMerit = targetLeft / leftCount;
/*  647: 888 */                 bestSplit = inst.value(attIndex);
/*  648: 889 */                 bestSupport = leftCount;
/*  649: 890 */                 bestSubsetSize = leftCount;
/*  650: 891 */                 lessThan = true;
/*  651:     */               }
/*  652:     */             }
/*  653:     */           }
/*  654: 896 */           if (rightCount >= HotSpot.this.m_supportCount)
/*  655:     */           {
/*  656: 897 */             double delta = HotSpot.this.m_minimize ? bestMerit - targetRight / rightCount : targetRight / rightCount - bestMerit;
/*  657: 901 */             if (delta > 0.0D)
/*  658:     */             {
/*  659: 902 */               bestMerit = targetRight / rightCount;
/*  660: 903 */               bestSplit = inst.value(attIndex);
/*  661: 904 */               bestSupport = rightCount;
/*  662: 905 */               bestSubsetSize = rightCount;
/*  663: 906 */               lessThan = false;
/*  664:     */             }
/*  665: 908 */             else if (delta == 0.0D)
/*  666:     */             {
/*  667: 910 */               if (rightCount > bestSupport)
/*  668:     */               {
/*  669: 911 */                 bestMerit = targetRight / rightCount;
/*  670: 912 */                 bestSplit = inst.value(attIndex);
/*  671: 913 */                 bestSupport = rightCount;
/*  672: 914 */                 bestSubsetSize = rightCount;
/*  673: 915 */                 lessThan = false;
/*  674:     */               }
/*  675:     */             }
/*  676:     */           }
/*  677:     */         }
/*  678:     */       }
/*  679: 922 */       double delta = HotSpot.this.m_minimize ? this.m_targetValue - bestMerit : bestMerit - this.m_targetValue;
/*  680: 926 */       if ((bestSupport > 0.0D) && (delta / this.m_targetValue >= HotSpot.this.m_minImprovement))
/*  681:     */       {
/*  682: 934 */         HotTestDetails newD = new HotTestDetails(attIndex, bestSplit, lessThan, (int)bestSupport, (int)bestSubsetSize, bestMerit);
/*  683:     */         
/*  684: 936 */         pq.add(newD);
/*  685:     */       }
/*  686:     */     }
/*  687:     */     
/*  688:     */     private void evaluateNominal(int attIndex, PriorityQueue<HotTestDetails> pq)
/*  689:     */     {
/*  690: 948 */       int[] counts = this.m_insts.attributeStats(attIndex).nominalCounts;
/*  691: 949 */       boolean ok = false;
/*  692:     */       
/*  693:     */ 
/*  694: 952 */       int offset = HotSpot.this.getTreatZeroAsMissing() ? 1 : 0;
/*  695: 953 */       for (int i = 0 + offset; i < this.m_insts.attribute(attIndex).numValues(); i++) {
/*  696: 954 */         if (counts[i] >= HotSpot.this.m_supportCount)
/*  697:     */         {
/*  698: 955 */           ok = true;
/*  699: 956 */           break;
/*  700:     */         }
/*  701:     */       }
/*  702: 959 */       if (ok)
/*  703:     */       {
/*  704: 960 */         double[] subsetMerit = new double[this.m_insts.attribute(attIndex).numValues()];
/*  705: 963 */         for (int i = 0; i < this.m_insts.numInstances(); i++)
/*  706:     */         {
/*  707: 964 */           Instance temp = this.m_insts.instance(i);
/*  708: 965 */           boolean missingAtt = (temp.isMissing(attIndex)) || ((HotSpot.this.getTreatZeroAsMissing()) && ((int)temp.value(attIndex) == 0));
/*  709: 969 */           if ((!missingAtt) && (!temp.isMissing(HotSpot.this.m_target)))
/*  710:     */           {
/*  711: 970 */             int attVal = (int)temp.value(attIndex);
/*  712: 971 */             if (this.m_insts.attribute(HotSpot.this.m_target).isNumeric()) {
/*  713: 972 */               subsetMerit[attVal] += temp.value(HotSpot.this.m_target);
/*  714:     */             } else {
/*  715: 974 */               subsetMerit[attVal] += ((int)temp.value(HotSpot.this.m_target) == HotSpot.this.m_targetIndex ? 1.0D : 0.0D);
/*  716:     */             }
/*  717:     */           }
/*  718:     */         }
/*  719: 983 */         for (int i = 0; i < this.m_insts.attribute(attIndex).numValues(); i++) {
/*  720: 988 */           if ((counts[i] >= HotSpot.this.m_supportCount) && ((!this.m_insts.attribute(HotSpot.this.m_target).isNominal()) || (subsetMerit[i] >= HotSpot.this.m_supportCount)))
/*  721:     */           {
/*  722: 993 */             double merit = subsetMerit[i] / counts[i];
/*  723: 994 */             double delta = HotSpot.this.m_minimize ? this.m_targetValue - merit : merit - this.m_targetValue;
/*  724: 997 */             if (delta / this.m_targetValue >= HotSpot.this.m_minImprovement)
/*  725:     */             {
/*  726: 998 */               double support = this.m_insts.attribute(HotSpot.this.m_target).isNominal() ? subsetMerit[i] : counts[i];
/*  727:     */               
/*  728:     */ 
/*  729:     */ 
/*  730:1002 */               HotTestDetails newD = new HotTestDetails(attIndex, i, false, (int)support, counts[i], merit);
/*  731:     */               
/*  732:1004 */               pq.add(newD);
/*  733:     */             }
/*  734:     */           }
/*  735:     */         }
/*  736:     */       }
/*  737:     */     }
/*  738:     */     
/*  739:     */     public int assignIDs(int lastID)
/*  740:     */     {
/*  741:1012 */       int currentLastID = lastID + 1;
/*  742:1013 */       this.m_id = currentLastID;
/*  743:1014 */       if (this.m_children != null) {
/*  744:1015 */         for (HotNode element : this.m_children) {
/*  745:1016 */           currentLastID = element.assignIDs(currentLastID);
/*  746:     */         }
/*  747:     */       }
/*  748:1019 */       return currentLastID;
/*  749:     */     }
/*  750:     */     
/*  751:     */     private void addNodeDetails(StringBuffer buff, int i, String spacer)
/*  752:     */     {
/*  753:1023 */       buff.append(HotSpot.this.m_header.attribute(this.m_testDetails[i].m_splitAttIndex).name());
/*  754:1024 */       if (HotSpot.this.m_header.attribute(this.m_testDetails[i].m_splitAttIndex).isNumeric())
/*  755:     */       {
/*  756:1025 */         if (this.m_testDetails[i].m_lessThan) {
/*  757:1026 */           buff.append(" <= ");
/*  758:     */         } else {
/*  759:1028 */           buff.append(" > ");
/*  760:     */         }
/*  761:1030 */         buff.append(Utils.doubleToString(this.m_testDetails[i].m_splitValue, 4));
/*  762:     */       }
/*  763:     */       else
/*  764:     */       {
/*  765:1032 */         buff.append(" = " + HotSpot.this.m_header.attribute(this.m_testDetails[i].m_splitAttIndex).value((int)this.m_testDetails[i].m_splitValue));
/*  766:     */       }
/*  767:1037 */       if (HotSpot.this.m_header.attribute(HotSpot.this.m_target).isNumeric()) {
/*  768:1038 */         buff.append(spacer + "(" + Utils.doubleToString(this.m_testDetails[i].m_merit, 4) + " [" + this.m_testDetails[i].m_supportLevel + "])");
/*  769:     */       } else {
/*  770:1042 */         buff.append(spacer + "(" + Utils.doubleToString(this.m_testDetails[i].m_merit * 100.0D, 2) + "% [" + this.m_testDetails[i].m_supportLevel + "/" + this.m_testDetails[i].m_subsetSize + "])");
/*  771:     */       }
/*  772:     */     }
/*  773:     */     
/*  774:     */     private void graphHotSpot(StringBuffer text)
/*  775:     */     {
/*  776:1050 */       if (this.m_children != null) {
/*  777:1051 */         for (int i = 0; i < this.m_children.length; i++)
/*  778:     */         {
/*  779:1052 */           text.append("N" + this.m_children[i].m_id);
/*  780:1053 */           text.append(" [label=\"");
/*  781:1054 */           addNodeDetails(text, i, "\\n");
/*  782:1055 */           text.append("\" shape=plaintext]\n");
/*  783:1056 */           this.m_children[i].graphHotSpot(text);
/*  784:1057 */           text.append("N" + this.m_id + "->" + "N" + this.m_children[i].m_id + "\n");
/*  785:     */         }
/*  786:     */       }
/*  787:     */     }
/*  788:     */     
/*  789:     */     protected void dumpTree(int depth, StringBuffer buff)
/*  790:     */     {
/*  791:1069 */       if (this.m_children != null) {
/*  792:1072 */         for (int i = 0; i < this.m_children.length; i++)
/*  793:     */         {
/*  794:1073 */           buff.append("\n  ");
/*  795:1074 */           for (int j = 0; j < depth; j++) {
/*  796:1075 */             buff.append("|   ");
/*  797:     */           }
/*  798:1077 */           addNodeDetails(buff, i, " ");
/*  799:     */           
/*  800:1079 */           this.m_children[i].dumpTree(depth + 1, buff);
/*  801:     */         }
/*  802:     */       }
/*  803:     */     }
/*  804:     */     
/*  805:     */     private void addTestToRule(List<Item> currentPremise, int i)
/*  806:     */       throws Exception
/*  807:     */     {
/*  808:1086 */       if (HotSpot.this.m_header.attribute(this.m_testDetails[i].m_splitAttIndex).isNumeric())
/*  809:     */       {
/*  810:1087 */         NumericItem.Comparison comp = this.m_testDetails[i].m_lessThan ? NumericItem.Comparison.LESS_THAN_OR_EQUAL_TO : NumericItem.Comparison.GREATER_THAN;
/*  811:     */         
/*  812:     */ 
/*  813:     */ 
/*  814:1091 */         NumericItem newItem = new NumericItem(HotSpot.this.m_header.attribute(this.m_testDetails[i].m_splitAttIndex), this.m_testDetails[i].m_splitValue, comp);
/*  815:     */         
/*  816:     */ 
/*  817:     */ 
/*  818:1095 */         currentPremise.add(newItem);
/*  819:     */       }
/*  820:     */       else
/*  821:     */       {
/*  822:1097 */         NominalItem newItem = new NominalItem(HotSpot.this.m_header.attribute(this.m_testDetails[i].m_splitAttIndex), (int)this.m_testDetails[i].m_splitValue);
/*  823:     */         
/*  824:     */ 
/*  825:1100 */         currentPremise.add(newItem);
/*  826:     */       }
/*  827:     */     }
/*  828:     */     
/*  829:     */     private class HotSpotNumericTargetRule
/*  830:     */       extends AssociationRule
/*  831:     */       implements Serializable
/*  832:     */     {
/*  833:     */       private static final long serialVersionUID = -1028053590504776204L;
/*  834:     */       Collection<Item> m_premise;
/*  835:     */       Collection<Item> m_consequence;
/*  836:1111 */       boolean m_numericTarget = true;
/*  837:     */       int m_totalSupport;
/*  838:     */       int m_consequenceSupport;
/*  839:     */       int m_totalTransactions;
/*  840:     */       double m_averageTarget;
/*  841:     */       DefaultAssociationRule m_delegateForDiscreteTarget;
/*  842:     */       
/*  843:     */       public HotSpotNumericTargetRule(Collection<Item> premise, int consequence, int totalSupport, int consequenceSupport, double totalTransactions)
/*  844:     */       {
/*  845:1123 */         this.m_premise = premise;
/*  846:1124 */         this.m_consequence = consequence;
/*  847:1125 */         this.m_totalSupport = totalSupport;
/*  848:1126 */         this.m_consequenceSupport = consequenceSupport;
/*  849:1127 */         this.m_totalTransactions = totalTransactions;
/*  850:1128 */         this.m_averageTarget = averageTarget;
/*  851:     */       }
/*  852:     */       
/*  853:     */       public HotSpotNumericTargetRule(Collection<Item> premise, int consequence, int premiseSupport, int consequenceSupport, int totalSupport, double totalTransactions)
/*  854:     */       {
/*  855:1136 */         this.m_numericTarget = false;
/*  856:1137 */         this.m_premise = premise;
/*  857:1138 */         this.m_consequence = consequence;
/*  858:1139 */         if (this.m_numericTarget)
/*  859:     */         {
/*  860:1140 */           this.m_totalSupport = totalSupport;
/*  861:1141 */           this.m_consequenceSupport = consequenceSupport;
/*  862:1142 */           this.m_totalTransactions = totalTransactions;
/*  863:1143 */           this.m_averageTarget = averageTarget;
/*  864:     */         }
/*  865:     */         else
/*  866:     */         {
/*  867:1145 */           this.m_delegateForDiscreteTarget = new DefaultAssociationRule(premise, consequence, DefaultAssociationRule.METRIC_TYPE.CONFIDENCE, premiseSupport, consequenceSupport, totalSupport, totalTransactions);
/*  868:     */         }
/*  869:     */       }
/*  870:     */       
/*  871:     */       public Collection<Item> getPremise()
/*  872:     */       {
/*  873:1155 */         return this.m_premise;
/*  874:     */       }
/*  875:     */       
/*  876:     */       public Collection<Item> getConsequence()
/*  877:     */       {
/*  878:1160 */         return this.m_consequence;
/*  879:     */       }
/*  880:     */       
/*  881:     */       public String getPrimaryMetricName()
/*  882:     */       {
/*  883:1165 */         return this.m_numericTarget ? "AverageTarget" : this.m_delegateForDiscreteTarget.getPrimaryMetricName();
/*  884:     */       }
/*  885:     */       
/*  886:     */       public double getPrimaryMetricValue()
/*  887:     */       {
/*  888:1171 */         return this.m_numericTarget ? this.m_averageTarget : this.m_delegateForDiscreteTarget.getPrimaryMetricValue();
/*  889:     */       }
/*  890:     */       
/*  891:     */       public double getNamedMetricValue(String metricName)
/*  892:     */         throws Exception
/*  893:     */       {
/*  894:1177 */         if (this.m_numericTarget)
/*  895:     */         {
/*  896:1178 */           if (metricName.equals("AverageTarget")) {
/*  897:1179 */             return getPrimaryMetricValue();
/*  898:     */           }
/*  899:1181 */           return Utils.missingValue();
/*  900:     */         }
/*  901:1183 */         if (metricName.equals("AverageTarget")) {
/*  902:1184 */           return Utils.missingValue();
/*  903:     */         }
/*  904:1186 */         return this.m_delegateForDiscreteTarget.getNamedMetricValue(metricName);
/*  905:     */       }
/*  906:     */       
/*  907:     */       public int getNumberOfMetricsForRule()
/*  908:     */       {
/*  909:1192 */         return DefaultAssociationRule.METRIC_TYPE.values().length + 1;
/*  910:     */       }
/*  911:     */       
/*  912:     */       public String[] getMetricNamesForRule()
/*  913:     */       {
/*  914:1197 */         String[] result = new String[getNumberOfMetricsForRule()];
/*  915:1198 */         result[0] = "AverageTarget";
/*  916:1199 */         for (int i = 0; i < DefaultAssociationRule.TAGS_SELECTION.length; i++) {
/*  917:1200 */           result[(i + 1)] = DefaultAssociationRule.TAGS_SELECTION[i].getReadable();
/*  918:     */         }
/*  919:1203 */         return result;
/*  920:     */       }
/*  921:     */       
/*  922:     */       public double[] getMetricValuesForRule()
/*  923:     */         throws Exception
/*  924:     */       {
/*  925:1208 */         double[] result = new double[getNumberOfMetricsForRule()];
/*  926:1209 */         result[0] = (this.m_numericTarget ? getPrimaryMetricValue() : Utils.missingValue());
/*  927:1212 */         for (int i = 0; i < DefaultAssociationRule.TAGS_SELECTION.length; i++) {
/*  928:1213 */           if (this.m_numericTarget) {
/*  929:1214 */             result[(i + 1)] = Utils.missingValue();
/*  930:     */           } else {
/*  931:1216 */             result[(i + 1)] = this.m_delegateForDiscreteTarget.getNamedMetricValue(DefaultAssociationRule.TAGS_SELECTION[i].getReadable());
/*  932:     */           }
/*  933:     */         }
/*  934:1222 */         return result;
/*  935:     */       }
/*  936:     */       
/*  937:     */       public int getPremiseSupport()
/*  938:     */       {
/*  939:1227 */         return this.m_numericTarget ? this.m_totalSupport : this.m_delegateForDiscreteTarget.getPremiseSupport();
/*  940:     */       }
/*  941:     */       
/*  942:     */       public int getConsequenceSupport()
/*  943:     */       {
/*  944:1233 */         return this.m_numericTarget ? this.m_consequenceSupport : this.m_delegateForDiscreteTarget.getConsequenceSupport();
/*  945:     */       }
/*  946:     */       
/*  947:     */       public int getTotalSupport()
/*  948:     */       {
/*  949:1239 */         return this.m_numericTarget ? this.m_totalSupport : this.m_delegateForDiscreteTarget.getTotalSupport();
/*  950:     */       }
/*  951:     */       
/*  952:     */       public int getTotalTransactions()
/*  953:     */       {
/*  954:1245 */         return this.m_numericTarget ? this.m_totalTransactions : this.m_delegateForDiscreteTarget.getTotalTransactions();
/*  955:     */       }
/*  956:     */       
/*  957:     */       public String toString()
/*  958:     */       {
/*  959:1251 */         StringBuffer result = new StringBuffer();
/*  960:1253 */         if (this.m_numericTarget) {
/*  961:1254 */           result.append(this.m_premise.toString() + " ==> " + this.m_consequence.toString() + ": " + this.m_totalSupport + "   ");
/*  962:     */         } else {
/*  963:1257 */           result.append(this.m_delegateForDiscreteTarget.toString());
/*  964:     */         }
/*  965:1259 */         return result.toString();
/*  966:     */       }
/*  967:     */       
/*  968:     */       public int compareTo(AssociationRule other)
/*  969:     */       {
/*  970:1264 */         int result = super.compareTo(other);
/*  971:1265 */         if (HotSpot.this.m_minimize) {
/*  972:1266 */           result = -result;
/*  973:     */         }
/*  974:1268 */         return result;
/*  975:     */       }
/*  976:     */     }
/*  977:     */     
/*  978:     */     protected void getRules(List<AssociationRule> rules, ArrayList<Item> currentPremise)
/*  979:     */       throws Exception
/*  980:     */     {
/*  981:1274 */       if (this.m_children != null) {
/*  982:1277 */         for (int i = 0; i < this.m_children.length; i++)
/*  983:     */         {
/*  984:1281 */           ArrayList<Item> newPremise = (ArrayList)currentPremise.clone();
/*  985:     */           
/*  986:     */ 
/*  987:1284 */           addTestToRule(newPremise, i);
/*  988:1286 */           if (HotSpot.this.m_header.attribute(HotSpot.this.m_target).isNominal())
/*  989:     */           {
/*  990:1287 */             NominalItem consequenceItem = new NominalItem(HotSpot.this.m_header.attribute(HotSpot.this.m_target), HotSpot.this.m_targetIndex);
/*  991:     */             
/*  992:1289 */             List<Item> consequence = new ArrayList();
/*  993:1290 */             consequence.add(consequenceItem);
/*  994:     */             
/*  995:1292 */             HotSpotNumericTargetRule newRule = new HotSpotNumericTargetRule(newPremise, consequence, this.m_testDetails[i].m_subsetSize, HotSpot.this.m_globalSupport, this.m_testDetails[i].m_supportLevel, HotSpot.this.m_numInstances, Utils.missingValue());
/*  996:     */             
/*  997:     */ 
/*  998:     */ 
/*  999:     */ 
/* 1000:     */ 
/* 1001:1298 */             rules.add(newRule);
/* 1002:     */           }
/* 1003:     */           else
/* 1004:     */           {
/* 1005:1301 */             NumericItem consequenceItem = new NumericItem(HotSpot.this.m_header.attribute(HotSpot.this.m_target), this.m_testDetails[i].m_merit, NumericItem.Comparison.NONE);
/* 1006:     */             
/* 1007:     */ 
/* 1008:1304 */             List<Item> consequence = new ArrayList();
/* 1009:1305 */             consequence.add(consequenceItem);
/* 1010:     */             
/* 1011:1307 */             HotSpotNumericTargetRule newRule = new HotSpotNumericTargetRule(newPremise, consequence, this.m_testDetails[i].m_supportLevel, HotSpot.this.m_numNonMissingTarget, HotSpot.this.m_numInstances, this.m_testDetails[i].m_merit);
/* 1012:     */             
/* 1013:     */ 
/* 1014:     */ 
/* 1015:1311 */             rules.add(newRule);
/* 1016:     */           }
/* 1017:1315 */           this.m_children[i].getRules(rules, newPremise);
/* 1018:     */         }
/* 1019:     */       }
/* 1020:     */     }
/* 1021:     */   }
/* 1022:     */   
/* 1023:     */   public String targetTipText()
/* 1024:     */   {
/* 1025:1328 */     return "The target attribute of interest (\"first\", \"last\",<index> or <attribute name> are valid values).";
/* 1026:     */   }
/* 1027:     */   
/* 1028:     */   public void setTarget(String target)
/* 1029:     */   {
/* 1030:1338 */     this.m_targetSI.setSingleIndex(target);
/* 1031:     */   }
/* 1032:     */   
/* 1033:     */   public String getTarget()
/* 1034:     */   {
/* 1035:1347 */     return this.m_targetSI.getSingleIndex();
/* 1036:     */   }
/* 1037:     */   
/* 1038:     */   public String targetIndexTipText()
/* 1039:     */   {
/* 1040:1357 */     return "The value of the target (nominal attributes only) of interest.";
/* 1041:     */   }
/* 1042:     */   
/* 1043:     */   public void setTargetIndex(String index)
/* 1044:     */   {
/* 1045:1366 */     this.m_targetIndexSI.setSingleIndex(index);
/* 1046:     */   }
/* 1047:     */   
/* 1048:     */   public String getTargetIndex()
/* 1049:     */   {
/* 1050:1375 */     return this.m_targetIndexSI.getSingleIndex();
/* 1051:     */   }
/* 1052:     */   
/* 1053:     */   public String minimizeTargetTipText()
/* 1054:     */   {
/* 1055:1385 */     return "Minimize rather than maximize the target.";
/* 1056:     */   }
/* 1057:     */   
/* 1058:     */   public void setMinimizeTarget(boolean m)
/* 1059:     */   {
/* 1060:1394 */     this.m_minimize = m;
/* 1061:     */   }
/* 1062:     */   
/* 1063:     */   public boolean getMinimizeTarget()
/* 1064:     */   {
/* 1065:1403 */     return this.m_minimize;
/* 1066:     */   }
/* 1067:     */   
/* 1068:     */   public String supportTipText()
/* 1069:     */   {
/* 1070:1413 */     return "The minimum support. Values between 0 and 1 are interpreted as a percentage of the total population; values > 1 are interpreted as an absolute number of instances";
/* 1071:     */   }
/* 1072:     */   
/* 1073:     */   public String getSupport()
/* 1074:     */   {
/* 1075:1424 */     return this.m_supportString;
/* 1076:     */   }
/* 1077:     */   
/* 1078:     */   public void setSupport(String s)
/* 1079:     */   {
/* 1080:1433 */     this.m_supportString = s;
/* 1081:     */   }
/* 1082:     */   
/* 1083:     */   public String maxBranchingFactorTipText()
/* 1084:     */   {
/* 1085:1443 */     return "Maximum branching factor. The maximum number of children to consider extending each node with.";
/* 1086:     */   }
/* 1087:     */   
/* 1088:     */   public void setMaxBranchingFactor(int b)
/* 1089:     */   {
/* 1090:1453 */     this.m_maxBranchingFactor = b;
/* 1091:     */   }
/* 1092:     */   
/* 1093:     */   public int getMaxBranchingFactor()
/* 1094:     */   {
/* 1095:1462 */     return this.m_maxBranchingFactor;
/* 1096:     */   }
/* 1097:     */   
/* 1098:     */   public String maxRuleLengthTipText()
/* 1099:     */   {
/* 1100:1472 */     return "Bound the length of a rule/path in the tree. -1 means unbounded";
/* 1101:     */   }
/* 1102:     */   
/* 1103:     */   public void setMaxRuleLength(int l)
/* 1104:     */   {
/* 1105:1482 */     this.m_maxRuleLength = l;
/* 1106:     */   }
/* 1107:     */   
/* 1108:     */   public int getMaxRuleLength()
/* 1109:     */   {
/* 1110:1491 */     return this.m_maxRuleLength;
/* 1111:     */   }
/* 1112:     */   
/* 1113:     */   public String treatZeroAsMissingTipText()
/* 1114:     */   {
/* 1115:1501 */     return "Treat zero (first value) for nominal attributes the same way as missing value (i.e. ignore). This is useful for market basket data.";
/* 1116:     */   }
/* 1117:     */   
/* 1118:     */   public void setTreatZeroAsMissing(boolean t)
/* 1119:     */   {
/* 1120:1513 */     this.m_treatZeroAsMissing = t;
/* 1121:     */   }
/* 1122:     */   
/* 1123:     */   public boolean getTreatZeroAsMissing()
/* 1124:     */   {
/* 1125:1523 */     return this.m_treatZeroAsMissing;
/* 1126:     */   }
/* 1127:     */   
/* 1128:     */   public String minImprovementTipText()
/* 1129:     */   {
/* 1130:1533 */     return "Minimum improvement in target value in order to consider adding a new branch/test";
/* 1131:     */   }
/* 1132:     */   
/* 1133:     */   public void setMinImprovement(double i)
/* 1134:     */   {
/* 1135:1543 */     this.m_minImprovement = i;
/* 1136:     */   }
/* 1137:     */   
/* 1138:     */   public double getMinImprovement()
/* 1139:     */   {
/* 1140:1552 */     return this.m_minImprovement;
/* 1141:     */   }
/* 1142:     */   
/* 1143:     */   public String debugTipText()
/* 1144:     */   {
/* 1145:1562 */     return "Output debugging info (duplicate rule lookup hash table stats).";
/* 1146:     */   }
/* 1147:     */   
/* 1148:     */   public void setDebug(boolean d)
/* 1149:     */   {
/* 1150:1571 */     this.m_debug = d;
/* 1151:     */   }
/* 1152:     */   
/* 1153:     */   public boolean getDebug()
/* 1154:     */   {
/* 1155:1580 */     return this.m_debug;
/* 1156:     */   }
/* 1157:     */   
/* 1158:     */   public void setOutputRules(boolean r)
/* 1159:     */   {
/* 1160:1584 */     this.m_outputRules = r;
/* 1161:     */   }
/* 1162:     */   
/* 1163:     */   public boolean getOutputRules()
/* 1164:     */   {
/* 1165:1588 */     return this.m_outputRules;
/* 1166:     */   }
/* 1167:     */   
/* 1168:     */   public String outputRulesTipText()
/* 1169:     */   {
/* 1170:1598 */     return "Output a rule set instead of a tree";
/* 1171:     */   }
/* 1172:     */   
/* 1173:     */   public String doNotCheckCapabilitiesTipText()
/* 1174:     */   {
/* 1175:1608 */     return "If set, associator capabilities are not checked before associator is built (Use with caution to reduce runtime).";
/* 1176:     */   }
/* 1177:     */   
/* 1178:     */   public void setDoNotCheckCapabilities(boolean doNotCheck)
/* 1179:     */   {
/* 1180:1619 */     this.m_doNotCheckCapabilities = doNotCheck;
/* 1181:     */   }
/* 1182:     */   
/* 1183:     */   public boolean getDoNotCheckCapabilities()
/* 1184:     */   {
/* 1185:1629 */     return this.m_doNotCheckCapabilities;
/* 1186:     */   }
/* 1187:     */   
/* 1188:     */   public Enumeration<Option> listOptions()
/* 1189:     */   {
/* 1190:1639 */     Vector<Option> newVector = new Vector();
/* 1191:1640 */     newVector.addElement(new Option("\tThe target index. (default = last)", "c", 1, "-c <num | first | last | attribute name>"));
/* 1192:     */     
/* 1193:1642 */     newVector.addElement(new Option("\tThe target value (nominal target only, default = first)", "V", 1, "-V <num | first | last>"));
/* 1194:     */     
/* 1195:     */ 
/* 1196:1645 */     newVector.addElement(new Option("\tMinimize rather than maximize.", "L", 0, "-L"));
/* 1197:     */     
/* 1198:1647 */     newVector.addElement(new Option("\tMinimum value count (nominal target)/segment size (numeric target).\n\tValues between 0 and 1 are \n\tinterpreted as a percentage of \n\tthe total population (numeric) or total target value\n\tpopulation size (nominal); values > 1 are \n\tinterpreted as an absolute number of \n\tinstances (default = 0.3)", "S", 1, "-S <num>"));
/* 1199:     */     
/* 1200:     */ 
/* 1201:     */ 
/* 1202:     */ 
/* 1203:     */ 
/* 1204:     */ 
/* 1205:     */ 
/* 1206:1655 */     newVector.addElement(new Option("\tMaximum branching factor (default = 2)", "M", 1, "-M <num>"));
/* 1207:     */     
/* 1208:1657 */     newVector.addElement(new Option("\tMaximum rule length (default = -1, i.e. no maximum)", "length", 1, "-length <num>"));
/* 1209:     */     
/* 1210:     */ 
/* 1211:1660 */     newVector.addElement(new Option("\tMinimum improvement in target value in order \n\tto add a new branch/test (default = 0.01 (1%))", "I", 1, "-I <num>"));
/* 1212:     */     
/* 1213:     */ 
/* 1214:     */ 
/* 1215:1664 */     newVector.addElement(new Option("\tTreat zero (first value) as missing for nominal attributes", "Z", 0, "-Z"));
/* 1216:     */     
/* 1217:     */ 
/* 1218:1667 */     newVector.addElement(new Option("\tOutput a set of rules instead of a tree structure", "R", 0, "-R"));
/* 1219:     */     
/* 1220:1669 */     newVector.addElement(new Option("\tOutput debugging info (duplicate rule lookup \n\thash table stats)", "D", 0, "-D"));
/* 1221:     */     
/* 1222:     */ 
/* 1223:1672 */     return newVector.elements();
/* 1224:     */   }
/* 1225:     */   
/* 1226:     */   public void resetOptions()
/* 1227:     */   {
/* 1228:1679 */     this.m_support = 0.33D;
/* 1229:1680 */     this.m_supportString = "0.33";
/* 1230:1681 */     this.m_minImprovement = 0.01D;
/* 1231:1682 */     this.m_maxBranchingFactor = 2;
/* 1232:1683 */     this.m_maxRuleLength = -1;
/* 1233:1684 */     this.m_minimize = false;
/* 1234:1685 */     this.m_debug = false;
/* 1235:1686 */     this.m_outputRules = false;
/* 1236:1687 */     setTarget("last");
/* 1237:1688 */     setTargetIndex("first");
/* 1238:1689 */     this.m_errorMessage = null;
/* 1239:     */   }
/* 1240:     */   
/* 1241:     */   public void setOptions(String[] options)
/* 1242:     */     throws Exception
/* 1243:     */   {
/* 1244:1748 */     resetOptions();
/* 1245:     */     
/* 1246:1750 */     String tempString = Utils.getOption('c', options);
/* 1247:1751 */     if (tempString.length() != 0) {
/* 1248:1752 */       setTarget(tempString);
/* 1249:     */     }
/* 1250:1755 */     tempString = Utils.getOption('V', options);
/* 1251:1756 */     if (tempString.length() != 0) {
/* 1252:1757 */       setTargetIndex(tempString);
/* 1253:     */     }
/* 1254:1760 */     setMinimizeTarget(Utils.getFlag('L', options));
/* 1255:     */     
/* 1256:1762 */     tempString = Utils.getOption('S', options);
/* 1257:1763 */     if (tempString.length() != 0) {
/* 1258:1764 */       setSupport(tempString);
/* 1259:     */     }
/* 1260:1767 */     tempString = Utils.getOption('M', options);
/* 1261:1768 */     if (tempString.length() != 0) {
/* 1262:1769 */       setMaxBranchingFactor(Integer.parseInt(tempString));
/* 1263:     */     }
/* 1264:1772 */     tempString = Utils.getOption("length", options);
/* 1265:1773 */     if (tempString.length() > 0) {
/* 1266:1774 */       setMaxRuleLength(Integer.parseInt(tempString));
/* 1267:     */     }
/* 1268:1777 */     tempString = Utils.getOption('I', options);
/* 1269:1778 */     if (tempString.length() != 0) {
/* 1270:1779 */       setMinImprovement(Double.parseDouble(tempString));
/* 1271:     */     }
/* 1272:1782 */     setDebug(Utils.getFlag('D', options));
/* 1273:1783 */     setOutputRules(Utils.getFlag('R', options));
/* 1274:1784 */     setTreatZeroAsMissing(Utils.getFlag('Z', options));
/* 1275:     */   }
/* 1276:     */   
/* 1277:     */   public String[] getOptions()
/* 1278:     */   {
/* 1279:1794 */     String[] options = new String[16];
/* 1280:1795 */     int current = 0;
/* 1281:     */     
/* 1282:1797 */     options[(current++)] = "-c";
/* 1283:1798 */     options[(current++)] = getTarget();
/* 1284:1799 */     options[(current++)] = "-V";
/* 1285:1800 */     options[(current++)] = getTargetIndex();
/* 1286:1801 */     if (getMinimizeTarget()) {
/* 1287:1802 */       options[(current++)] = "-L";
/* 1288:     */     }
/* 1289:1804 */     options[(current++)] = "-S";
/* 1290:1805 */     options[(current++)] = ("" + getSupport());
/* 1291:1806 */     options[(current++)] = "-M";
/* 1292:1807 */     options[(current++)] = ("" + getMaxBranchingFactor());
/* 1293:1808 */     options[(current++)] = "-length";
/* 1294:1809 */     options[(current++)] = ("" + getMaxRuleLength());
/* 1295:1810 */     options[(current++)] = "-I";
/* 1296:1811 */     options[(current++)] = ("" + getMinImprovement());
/* 1297:1812 */     if (getDebug()) {
/* 1298:1813 */       options[(current++)] = "-D";
/* 1299:     */     }
/* 1300:1816 */     if (getOutputRules()) {
/* 1301:1817 */       options[(current++)] = "-R";
/* 1302:     */     }
/* 1303:1820 */     if (getTreatZeroAsMissing()) {
/* 1304:1821 */       options[(current++)] = "-Z";
/* 1305:     */     }
/* 1306:1824 */     while (current < options.length) {
/* 1307:1825 */       options[(current++)] = "";
/* 1308:     */     }
/* 1309:1828 */     return options;
/* 1310:     */   }
/* 1311:     */   
/* 1312:     */   public String getRevision()
/* 1313:     */   {
/* 1314:1838 */     return RevisionUtils.extract("$Revision: 11008 $");
/* 1315:     */   }
/* 1316:     */   
/* 1317:     */   public int graphType()
/* 1318:     */   {
/* 1319:1848 */     return 1;
/* 1320:     */   }
/* 1321:     */   
/* 1322:     */   public AssociationRules getAssociationRules()
/* 1323:     */   {
/* 1324:1859 */     List<AssociationRule> rulesToReturn = new ArrayList();
/* 1325:     */     try
/* 1326:     */     {
/* 1327:1861 */       this.m_head.getRules(rulesToReturn, new ArrayList());
/* 1328:1862 */       Collections.sort(rulesToReturn);
/* 1329:     */     }
/* 1330:     */     catch (Exception e)
/* 1331:     */     {
/* 1332:1864 */       e.printStackTrace();
/* 1333:     */     }
/* 1334:1867 */     return new AssociationRules(rulesToReturn, this);
/* 1335:     */   }
/* 1336:     */   
/* 1337:     */   public boolean canProduceRules()
/* 1338:     */   {
/* 1339:1883 */     return true;
/* 1340:     */   }
/* 1341:     */   
/* 1342:     */   public String[] getRuleMetricNames()
/* 1343:     */   {
/* 1344:1896 */     String[] metricNames = new String[DefaultAssociationRule.TAGS_SELECTION.length + 1];
/* 1345:     */     
/* 1346:1898 */     metricNames[0] = "AverageTarget";
/* 1347:1900 */     for (int i = 0; i < DefaultAssociationRule.TAGS_SELECTION.length; i++) {
/* 1348:1901 */       metricNames[(i + 1)] = DefaultAssociationRule.TAGS_SELECTION[i].getReadable();
/* 1349:     */     }
/* 1350:1905 */     return metricNames;
/* 1351:     */   }
/* 1352:     */   
/* 1353:     */   public static void main(String[] args)
/* 1354:     */   {
/* 1355:     */     try
/* 1356:     */     {
/* 1357:1915 */       AbstractAssociator.runAssociator(new HotSpot(), args);
/* 1358:     */     }
/* 1359:     */     catch (Exception ex)
/* 1360:     */     {
/* 1361:1917 */       ex.printStackTrace();
/* 1362:     */     }
/* 1363:     */   }
/* 1364:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.associations.HotSpot
 * JD-Core Version:    0.7.0.1
 */