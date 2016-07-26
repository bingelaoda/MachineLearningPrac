/*    1:     */ package weka.classifiers.pmml.consumer;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.io.Serializable;
/*    5:     */ import java.util.ArrayList;
/*    6:     */ import org.w3c.dom.Element;
/*    7:     */ import org.w3c.dom.Node;
/*    8:     */ import org.w3c.dom.NodeList;
/*    9:     */ import weka.core.Attribute;
/*   10:     */ import weka.core.Drawable;
/*   11:     */ import weka.core.Instance;
/*   12:     */ import weka.core.Instances;
/*   13:     */ import weka.core.RevisionUtils;
/*   14:     */ import weka.core.Utils;
/*   15:     */ import weka.core.pmml.Array;
/*   16:     */ import weka.core.pmml.Array.ArrayType;
/*   17:     */ import weka.core.pmml.MappingInfo;
/*   18:     */ import weka.core.pmml.MiningSchema;
/*   19:     */ 
/*   20:     */ public class TreeModel
/*   21:     */   extends PMMLClassifier
/*   22:     */   implements Drawable
/*   23:     */ {
/*   24:     */   private static final long serialVersionUID = -2065158088298753129L;
/*   25:     */   
/*   26:     */   static class ScoreDistribution
/*   27:     */     implements Serializable
/*   28:     */   {
/*   29:     */     private static final long serialVersionUID = -123506262094299933L;
/*   30:     */     private final String m_classLabel;
/*   31:  68 */     private int m_classLabelIndex = -1;
/*   32:     */     private final double m_recordCount;
/*   33:  74 */     private double m_confidence = Utils.missingValue();
/*   34:     */     
/*   35:     */     protected ScoreDistribution(Element scoreE, MiningSchema miningSchema, double baseCount)
/*   36:     */       throws Exception
/*   37:     */     {
/*   38:  88 */       this.m_classLabel = scoreE.getAttribute("value");
/*   39:  89 */       Attribute classAtt = miningSchema.getFieldsAsInstances().classAttribute();
/*   40:  90 */       if ((classAtt == null) || (classAtt.indexOfValue(this.m_classLabel) < 0)) {
/*   41:  91 */         throw new Exception("[ScoreDistribution] class attribute not set or class value " + this.m_classLabel + " not found!");
/*   42:     */       }
/*   43:  96 */       this.m_classLabelIndex = classAtt.indexOfValue(this.m_classLabel);
/*   44:     */       
/*   45:     */ 
/*   46:  99 */       String recordC = scoreE.getAttribute("recordCount");
/*   47: 100 */       this.m_recordCount = Double.parseDouble(recordC);
/*   48:     */       
/*   49:     */ 
/*   50: 103 */       String confidence = scoreE.getAttribute("confidence");
/*   51: 104 */       if ((confidence != null) && (confidence.length() > 0)) {
/*   52: 105 */         this.m_confidence = Double.parseDouble(confidence);
/*   53: 106 */       } else if ((!Utils.isMissingValue(baseCount)) && (baseCount > 0.0D)) {
/*   54: 107 */         this.m_confidence = (this.m_recordCount / baseCount);
/*   55:     */       }
/*   56:     */     }
/*   57:     */     
/*   58:     */     void deriveConfidenceValue(double baseCount)
/*   59:     */     {
/*   60: 121 */       if ((Utils.isMissingValue(this.m_confidence)) && (!Utils.isMissingValue(baseCount)) && (baseCount > 0.0D)) {
/*   61: 123 */         this.m_confidence = (this.m_recordCount / baseCount);
/*   62:     */       }
/*   63:     */     }
/*   64:     */     
/*   65:     */     String getClassLabel()
/*   66:     */     {
/*   67: 128 */       return this.m_classLabel;
/*   68:     */     }
/*   69:     */     
/*   70:     */     int getClassLabelIndex()
/*   71:     */     {
/*   72: 132 */       return this.m_classLabelIndex;
/*   73:     */     }
/*   74:     */     
/*   75:     */     double getRecordCount()
/*   76:     */     {
/*   77: 136 */       return this.m_recordCount;
/*   78:     */     }
/*   79:     */     
/*   80:     */     double getConfidence()
/*   81:     */     {
/*   82: 140 */       return this.m_confidence;
/*   83:     */     }
/*   84:     */     
/*   85:     */     public String toString()
/*   86:     */     {
/*   87: 145 */       return this.m_classLabel + ": " + this.m_recordCount + " (" + Utils.doubleToString(this.m_confidence, 2) + ") ";
/*   88:     */     }
/*   89:     */   }
/*   90:     */   
/*   91:     */   static abstract class Predicate
/*   92:     */     implements Serializable
/*   93:     */   {
/*   94:     */     private static final long serialVersionUID = 1035344165452733887L;
/*   95:     */     abstract Eval evaluate(double[] paramArrayOfDouble);
/*   96:     */     
/*   97:     */     static enum Eval
/*   98:     */     {
/*   99: 161 */       TRUE,  FALSE,  UNKNOWN;
/*  100:     */       
/*  101:     */       private Eval() {}
/*  102:     */     }
/*  103:     */     
/*  104:     */     protected String toString(int level, boolean cr)
/*  105:     */     {
/*  106: 174 */       return toString(level);
/*  107:     */     }
/*  108:     */     
/*  109:     */     protected String toString(int level)
/*  110:     */     {
/*  111: 178 */       StringBuffer text = new StringBuffer();
/*  112: 179 */       for (int j = 0; j < level; j++) {
/*  113: 180 */         text.append("|   ");
/*  114:     */       }
/*  115: 183 */       return toString();
/*  116:     */     }
/*  117:     */     
/*  118:     */     static Eval booleanToEval(boolean missing, boolean result)
/*  119:     */     {
/*  120: 187 */       if (missing) {
/*  121: 188 */         return Eval.UNKNOWN;
/*  122:     */       }
/*  123: 189 */       if (result) {
/*  124: 190 */         return Eval.TRUE;
/*  125:     */       }
/*  126: 192 */       return Eval.FALSE;
/*  127:     */     }
/*  128:     */     
/*  129:     */     static Predicate getPredicate(Element nodeE, MiningSchema miningSchema)
/*  130:     */       throws Exception
/*  131:     */     {
/*  132: 208 */       Predicate result = null;
/*  133: 209 */       NodeList children = nodeE.getChildNodes();
/*  134: 210 */       for (int i = 0; i < children.getLength(); i++)
/*  135:     */       {
/*  136: 211 */         Node child = children.item(i);
/*  137: 212 */         if (child.getNodeType() == 1)
/*  138:     */         {
/*  139: 213 */           String tagName = ((Element)child).getTagName();
/*  140: 214 */           if (tagName.equals("True"))
/*  141:     */           {
/*  142: 215 */             result = new TreeModel.True();
/*  143: 216 */             break;
/*  144:     */           }
/*  145: 217 */           if (tagName.equals("False"))
/*  146:     */           {
/*  147: 218 */             result = new TreeModel.False();
/*  148: 219 */             break;
/*  149:     */           }
/*  150: 220 */           if (tagName.equals("SimplePredicate"))
/*  151:     */           {
/*  152: 221 */             result = new TreeModel.SimplePredicate((Element)child, miningSchema);
/*  153: 222 */             break;
/*  154:     */           }
/*  155: 223 */           if (tagName.equals("CompoundPredicate"))
/*  156:     */           {
/*  157: 224 */             result = new TreeModel.CompoundPredicate((Element)child, miningSchema);
/*  158: 225 */             break;
/*  159:     */           }
/*  160: 226 */           if (tagName.equals("SimpleSetPredicate"))
/*  161:     */           {
/*  162: 227 */             result = new TreeModel.SimpleSetPredicate((Element)child, miningSchema);
/*  163: 228 */             break;
/*  164:     */           }
/*  165:     */         }
/*  166:     */       }
/*  167: 233 */       if (result == null) {
/*  168: 234 */         throw new Exception("[Predicate] unknown or missing predicate type in node");
/*  169:     */       }
/*  170: 238 */       return result;
/*  171:     */     }
/*  172:     */   }
/*  173:     */   
/*  174:     */   static class True
/*  175:     */     extends TreeModel.Predicate
/*  176:     */   {
/*  177:     */     private static final long serialVersionUID = 1817942234610531627L;
/*  178:     */     
/*  179:     */     public TreeModel.Predicate.Eval evaluate(double[] input)
/*  180:     */     {
/*  181: 254 */       return TreeModel.Predicate.Eval.TRUE;
/*  182:     */     }
/*  183:     */     
/*  184:     */     public String toString()
/*  185:     */     {
/*  186: 259 */       return "True: ";
/*  187:     */     }
/*  188:     */   }
/*  189:     */   
/*  190:     */   static class False
/*  191:     */     extends TreeModel.Predicate
/*  192:     */   {
/*  193:     */     private static final long serialVersionUID = -3647261386442860365L;
/*  194:     */     
/*  195:     */     public TreeModel.Predicate.Eval evaluate(double[] input)
/*  196:     */     {
/*  197: 275 */       return TreeModel.Predicate.Eval.FALSE;
/*  198:     */     }
/*  199:     */     
/*  200:     */     public String toString()
/*  201:     */     {
/*  202: 280 */       return "False: ";
/*  203:     */     }
/*  204:     */   }
/*  205:     */   
/*  206:     */   static class SimplePredicate
/*  207:     */     extends TreeModel.Predicate
/*  208:     */   {
/*  209:     */     private static final long serialVersionUID = -6156684285069327400L;
/*  210:     */     
/*  211:     */     static abstract enum Operator
/*  212:     */     {
/*  213: 295 */       EQUAL("equal"),  NOTEQUAL("notEqual"),  LESSTHAN("lessThan"),  LESSOREQUAL("lessOrEqual"),  GREATERTHAN("greaterThan"),  GREATEROREQUAL("greaterOrEqual"),  ISMISSING("isMissing"),  ISNOTMISSING("isNotMissing");
/*  214:     */       
/*  215:     */       private final String m_stringVal;
/*  216:     */       
/*  217:     */       abstract TreeModel.Predicate.Eval evaluate(double[] paramArrayOfDouble, double paramDouble, int paramInt);
/*  218:     */       
/*  219:     */       abstract String shortName();
/*  220:     */       
/*  221:     */       private Operator(String name)
/*  222:     */       {
/*  223: 406 */         this.m_stringVal = name;
/*  224:     */       }
/*  225:     */       
/*  226:     */       public String toString()
/*  227:     */       {
/*  228: 411 */         return this.m_stringVal;
/*  229:     */       }
/*  230:     */     }
/*  231:     */     
/*  232: 416 */     int m_fieldIndex = -1;
/*  233:     */     String m_fieldName;
/*  234:     */     boolean m_isNominal;
/*  235:     */     String m_nominalValue;
/*  236:     */     double m_value;
/*  237:     */     Operator m_operator;
/*  238:     */     
/*  239:     */     public SimplePredicate(Element simpleP, MiningSchema miningSchema)
/*  240:     */       throws Exception
/*  241:     */     {
/*  242: 436 */       Instances totalStructure = miningSchema.getFieldsAsInstances();
/*  243:     */       
/*  244:     */ 
/*  245: 439 */       String fieldS = simpleP.getAttribute("field");
/*  246: 440 */       Attribute att = totalStructure.attribute(fieldS);
/*  247: 441 */       if (att == null) {
/*  248: 442 */         throw new Exception("[SimplePredicate] unable to find field " + fieldS + " in the incoming instance structure!");
/*  249:     */       }
/*  250: 447 */       int index = -1;
/*  251: 448 */       for (int i = 0; i < totalStructure.numAttributes(); i++) {
/*  252: 449 */         if (totalStructure.attribute(i).name().equals(fieldS))
/*  253:     */         {
/*  254: 450 */           index = i;
/*  255: 451 */           this.m_fieldName = totalStructure.attribute(i).name();
/*  256: 452 */           break;
/*  257:     */         }
/*  258:     */       }
/*  259: 455 */       this.m_fieldIndex = index;
/*  260: 456 */       if (att.isNominal()) {
/*  261: 457 */         this.m_isNominal = true;
/*  262:     */       }
/*  263: 461 */       String oppS = simpleP.getAttribute("operator");
/*  264: 462 */       for (Operator o : Operator.values()) {
/*  265: 463 */         if (o.toString().equals(oppS))
/*  266:     */         {
/*  267: 464 */           this.m_operator = o;
/*  268: 465 */           break;
/*  269:     */         }
/*  270:     */       }
/*  271: 469 */       if ((this.m_operator != Operator.ISMISSING) && (this.m_operator != Operator.ISNOTMISSING))
/*  272:     */       {
/*  273: 471 */         String valueS = simpleP.getAttribute("value");
/*  274: 472 */         if (att.isNumeric())
/*  275:     */         {
/*  276: 473 */           this.m_value = Double.parseDouble(valueS);
/*  277:     */         }
/*  278:     */         else
/*  279:     */         {
/*  280: 475 */           this.m_nominalValue = valueS;
/*  281: 476 */           this.m_value = att.indexOfValue(valueS);
/*  282: 477 */           if (this.m_value < 0.0D) {
/*  283: 478 */             throw new Exception("[SimplePredicate] can't find value " + valueS + " in nominal " + "attribute " + att.name());
/*  284:     */           }
/*  285:     */         }
/*  286:     */       }
/*  287:     */     }
/*  288:     */     
/*  289:     */     public TreeModel.Predicate.Eval evaluate(double[] input)
/*  290:     */     {
/*  291: 487 */       return this.m_operator.evaluate(input, this.m_value, this.m_fieldIndex);
/*  292:     */     }
/*  293:     */     
/*  294:     */     public String toString()
/*  295:     */     {
/*  296: 492 */       StringBuffer temp = new StringBuffer();
/*  297:     */       
/*  298: 494 */       temp.append(this.m_fieldName + " " + this.m_operator.shortName());
/*  299: 495 */       if ((this.m_operator != Operator.ISMISSING) && (this.m_operator != Operator.ISNOTMISSING)) {
/*  300: 497 */         temp.append(" " + (this.m_isNominal ? this.m_nominalValue : new StringBuilder().append("").append(this.m_value).toString()));
/*  301:     */       }
/*  302: 500 */       return temp.toString();
/*  303:     */     }
/*  304:     */   }
/*  305:     */   
/*  306:     */   static class CompoundPredicate
/*  307:     */     extends TreeModel.Predicate
/*  308:     */   {
/*  309:     */     private static final long serialVersionUID = -3332091529764559077L;
/*  310:     */     
/*  311:     */     static abstract enum BooleanOperator
/*  312:     */     {
/*  313: 515 */       OR("or"),  AND("and"),  XOR("xor"),  SURROGATE("surrogate");
/*  314:     */       
/*  315:     */       private final String m_stringVal;
/*  316:     */       
/*  317:     */       abstract TreeModel.Predicate.Eval evaluate(ArrayList<TreeModel.Predicate> paramArrayList, double[] paramArrayOfDouble);
/*  318:     */       
/*  319:     */       private BooleanOperator(String name)
/*  320:     */       {
/*  321: 598 */         this.m_stringVal = name;
/*  322:     */       }
/*  323:     */       
/*  324:     */       public String toString()
/*  325:     */       {
/*  326: 603 */         return this.m_stringVal;
/*  327:     */       }
/*  328:     */     }
/*  329:     */     
/*  330: 608 */     ArrayList<TreeModel.Predicate> m_components = new ArrayList();
/*  331:     */     BooleanOperator m_booleanOperator;
/*  332:     */     
/*  333:     */     public CompoundPredicate(Element compoundP, MiningSchema miningSchema)
/*  334:     */       throws Exception
/*  335:     */     {
/*  336: 616 */       String booleanOpp = compoundP.getAttribute("booleanOperator");
/*  337: 617 */       for (BooleanOperator b : BooleanOperator.values()) {
/*  338: 618 */         if (b.toString().equals(booleanOpp)) {
/*  339: 619 */           this.m_booleanOperator = b;
/*  340:     */         }
/*  341:     */       }
/*  342: 624 */       NodeList children = compoundP.getChildNodes();
/*  343: 625 */       for (int i = 0; i < children.getLength(); i++)
/*  344:     */       {
/*  345: 626 */         Node child = children.item(i);
/*  346: 627 */         if (child.getNodeType() == 1)
/*  347:     */         {
/*  348: 628 */           String tagName = ((Element)child).getTagName();
/*  349: 629 */           if (tagName.equals("True")) {
/*  350: 630 */             this.m_components.add(new TreeModel.True());
/*  351: 631 */           } else if (tagName.equals("False")) {
/*  352: 632 */             this.m_components.add(new TreeModel.False());
/*  353: 633 */           } else if (tagName.equals("SimplePredicate")) {
/*  354: 634 */             this.m_components.add(new TreeModel.SimplePredicate((Element)child, miningSchema));
/*  355: 636 */           } else if (tagName.equals("CompoundPredicate")) {
/*  356: 637 */             this.m_components.add(new CompoundPredicate((Element)child, miningSchema));
/*  357:     */           } else {
/*  358: 640 */             this.m_components.add(new TreeModel.SimpleSetPredicate((Element)child, miningSchema));
/*  359:     */           }
/*  360:     */         }
/*  361:     */       }
/*  362:     */     }
/*  363:     */     
/*  364:     */     public TreeModel.Predicate.Eval evaluate(double[] input)
/*  365:     */     {
/*  366: 649 */       return this.m_booleanOperator.evaluate(this.m_components, input);
/*  367:     */     }
/*  368:     */     
/*  369:     */     public String toString()
/*  370:     */     {
/*  371: 654 */       return toString(0, false);
/*  372:     */     }
/*  373:     */     
/*  374:     */     public String toString(int level, boolean cr)
/*  375:     */     {
/*  376: 659 */       StringBuffer text = new StringBuffer();
/*  377: 660 */       for (int j = 0; j < level; j++) {
/*  378: 661 */         text.append("|   ");
/*  379:     */       }
/*  380: 664 */       text.append("Compound [" + this.m_booleanOperator.toString() + "]");
/*  381: 665 */       if (cr) {
/*  382: 666 */         text.append("\\n");
/*  383:     */       } else {
/*  384: 668 */         text.append("\n");
/*  385:     */       }
/*  386: 670 */       for (int i = 0; i < this.m_components.size(); i++)
/*  387:     */       {
/*  388: 671 */         text.append(((TreeModel.Predicate)this.m_components.get(i)).toString(level, cr).replace(":", ""));
/*  389: 672 */         if (i != this.m_components.size() - 1) {
/*  390: 673 */           if (cr) {
/*  391: 674 */             text.append("\\n");
/*  392:     */           } else {
/*  393: 676 */             text.append("\n");
/*  394:     */           }
/*  395:     */         }
/*  396:     */       }
/*  397: 681 */       return text.toString();
/*  398:     */     }
/*  399:     */   }
/*  400:     */   
/*  401:     */   static class SimpleSetPredicate
/*  402:     */     extends TreeModel.Predicate
/*  403:     */   {
/*  404:     */     private static final long serialVersionUID = -2711995401345708486L;
/*  405:     */     
/*  406:     */     static abstract enum BooleanOperator
/*  407:     */     {
/*  408: 696 */       IS_IN("isIn"),  IS_NOT_IN("isNotIn");
/*  409:     */       
/*  410:     */       private final String m_stringVal;
/*  411:     */       
/*  412:     */       abstract TreeModel.Predicate.Eval evaluate(double[] paramArrayOfDouble, int paramInt, Array paramArray, Attribute paramAttribute);
/*  413:     */       
/*  414:     */       private BooleanOperator(String name)
/*  415:     */       {
/*  416: 740 */         this.m_stringVal = name;
/*  417:     */       }
/*  418:     */       
/*  419:     */       public String toString()
/*  420:     */       {
/*  421: 745 */         return this.m_stringVal;
/*  422:     */       }
/*  423:     */     }
/*  424:     */     
/*  425: 750 */     int m_fieldIndex = -1;
/*  426:     */     String m_fieldName;
/*  427: 756 */     boolean m_isNominal = false;
/*  428:     */     Attribute m_nominalLookup;
/*  429: 762 */     BooleanOperator m_operator = BooleanOperator.IS_IN;
/*  430:     */     Array m_set;
/*  431:     */     
/*  432:     */     public SimpleSetPredicate(Element setP, MiningSchema miningSchema)
/*  433:     */       throws Exception
/*  434:     */     {
/*  435: 768 */       Instances totalStructure = miningSchema.getFieldsAsInstances();
/*  436:     */       
/*  437:     */ 
/*  438: 771 */       String fieldS = setP.getAttribute("field");
/*  439: 772 */       Attribute att = totalStructure.attribute(fieldS);
/*  440: 773 */       if (att == null) {
/*  441: 774 */         throw new Exception("[SimplePredicate] unable to find field " + fieldS + " in the incoming instance structure!");
/*  442:     */       }
/*  443: 779 */       int index = -1;
/*  444: 780 */       for (int i = 0; i < totalStructure.numAttributes(); i++) {
/*  445: 781 */         if (totalStructure.attribute(i).name().equals(fieldS))
/*  446:     */         {
/*  447: 782 */           index = i;
/*  448: 783 */           this.m_fieldName = totalStructure.attribute(i).name();
/*  449: 784 */           break;
/*  450:     */         }
/*  451:     */       }
/*  452: 787 */       this.m_fieldIndex = index;
/*  453: 788 */       if (att.isNominal())
/*  454:     */       {
/*  455: 789 */         this.m_isNominal = true;
/*  456: 790 */         this.m_nominalLookup = att;
/*  457:     */       }
/*  458: 794 */       NodeList children = setP.getChildNodes();
/*  459: 795 */       for (int i = 0; i < children.getLength(); i++)
/*  460:     */       {
/*  461: 796 */         Node child = children.item(i);
/*  462: 797 */         if ((child.getNodeType() == 1) && 
/*  463: 798 */           (Array.isArray((Element)child)))
/*  464:     */         {
/*  465: 800 */           this.m_set = Array.create((Element)child);
/*  466: 801 */           break;
/*  467:     */         }
/*  468:     */       }
/*  469: 806 */       if (this.m_set == null) {
/*  470: 807 */         throw new Exception("[SimpleSetPredictate] couldn't find an array containing the set values!");
/*  471:     */       }
/*  472: 812 */       if ((this.m_set.getType() == Array.ArrayType.STRING) && (!this.m_isNominal)) {
/*  473: 813 */         throw new Exception("[SimpleSetPredicate] referenced field " + totalStructure.attribute(this.m_fieldIndex).name() + " is numeric but array type is string!");
/*  474:     */       }
/*  475: 816 */       if ((this.m_set.getType() != Array.ArrayType.STRING) && (this.m_isNominal)) {
/*  476: 817 */         throw new Exception("[SimpleSetPredicate] referenced field " + totalStructure.attribute(this.m_fieldIndex).name() + " is nominal but array type is numeric!");
/*  477:     */       }
/*  478:     */     }
/*  479:     */     
/*  480:     */     public TreeModel.Predicate.Eval evaluate(double[] input)
/*  481:     */     {
/*  482: 825 */       return this.m_operator.evaluate(input, this.m_fieldIndex, this.m_set, this.m_nominalLookup);
/*  483:     */     }
/*  484:     */     
/*  485:     */     public String toString()
/*  486:     */     {
/*  487: 830 */       StringBuffer temp = new StringBuffer();
/*  488:     */       
/*  489: 832 */       temp.append(this.m_fieldName + " " + this.m_operator.toString() + " ");
/*  490: 833 */       temp.append(this.m_set.toString());
/*  491:     */       
/*  492: 835 */       return temp.toString();
/*  493:     */     }
/*  494:     */   }
/*  495:     */   
/*  496:     */   class TreeNode
/*  497:     */     implements Serializable
/*  498:     */   {
/*  499:     */     private static final long serialVersionUID = 3011062274167063699L;
/*  500: 852 */     private String m_ID = "" + hashCode();
/*  501:     */     private String m_scoreString;
/*  502: 858 */     private int m_scoreIndex = -1;
/*  503: 861 */     private double m_scoreNumeric = Utils.missingValue();
/*  504: 864 */     private double m_recordCount = Utils.missingValue();
/*  505:     */     private String m_defaultChildID;
/*  506:     */     private TreeNode m_defaultChild;
/*  507: 873 */     private final ArrayList<TreeModel.ScoreDistribution> m_scoreDistributions = new ArrayList();
/*  508:     */     private final TreeModel.Predicate m_predicate;
/*  509: 879 */     private final ArrayList<TreeNode> m_childNodes = new ArrayList();
/*  510:     */     
/*  511:     */     protected TreeNode(Element nodeE, MiningSchema miningSchema)
/*  512:     */       throws Exception
/*  513:     */     {
/*  514: 882 */       Attribute classAtt = miningSchema.getFieldsAsInstances().classAttribute();
/*  515:     */       
/*  516:     */ 
/*  517: 885 */       String id = nodeE.getAttribute("id");
/*  518: 886 */       if ((id != null) && (id.length() > 0)) {
/*  519: 887 */         this.m_ID = id;
/*  520:     */       }
/*  521: 891 */       String scoreS = nodeE.getAttribute("score");
/*  522: 892 */       if ((scoreS != null) && (scoreS.length() > 0))
/*  523:     */       {
/*  524: 893 */         this.m_scoreString = scoreS;
/*  525: 897 */         if (classAtt.isNumeric())
/*  526:     */         {
/*  527:     */           try
/*  528:     */           {
/*  529: 899 */             this.m_scoreNumeric = Double.parseDouble(scoreS);
/*  530:     */           }
/*  531:     */           catch (NumberFormatException ex)
/*  532:     */           {
/*  533: 901 */             throw new Exception("[TreeNode] class is numeric but unable to parse score " + this.m_scoreString + " as a number!");
/*  534:     */           }
/*  535:     */         }
/*  536:     */         else
/*  537:     */         {
/*  538: 907 */           this.m_scoreIndex = classAtt.indexOfValue(this.m_scoreString);
/*  539: 909 */           if (this.m_scoreIndex < 0) {
/*  540: 910 */             throw new Exception("[TreeNode] can't find match for predicted value " + this.m_scoreString + " in class attribute!");
/*  541:     */           }
/*  542:     */         }
/*  543:     */       }
/*  544: 918 */       String recordC = nodeE.getAttribute("recordCount");
/*  545: 919 */       if ((recordC != null) && (recordC.length() > 0)) {
/*  546: 920 */         this.m_recordCount = Double.parseDouble(recordC);
/*  547:     */       }
/*  548: 924 */       String defaultC = nodeE.getAttribute("defaultChild");
/*  549: 925 */       if ((defaultC != null) && (defaultC.length() > 0)) {
/*  550: 926 */         this.m_defaultChildID = defaultC;
/*  551:     */       }
/*  552: 933 */       if (TreeModel.this.m_functionType == TreeModel.MiningFunction.CLASSIFICATION) {
/*  553: 934 */         getScoreDistributions(nodeE, miningSchema);
/*  554:     */       }
/*  555: 938 */       this.m_predicate = TreeModel.Predicate.getPredicate(nodeE, miningSchema);
/*  556:     */       
/*  557:     */ 
/*  558: 941 */       getChildNodes(nodeE, miningSchema);
/*  559: 944 */       if (this.m_defaultChildID != null) {
/*  560: 945 */         for (TreeNode t : this.m_childNodes) {
/*  561: 946 */           if (t.getID().equals(this.m_defaultChildID))
/*  562:     */           {
/*  563: 947 */             this.m_defaultChild = t;
/*  564: 948 */             break;
/*  565:     */           }
/*  566:     */         }
/*  567:     */       }
/*  568:     */     }
/*  569:     */     
/*  570:     */     private void getChildNodes(Element nodeE, MiningSchema miningSchema)
/*  571:     */       throws Exception
/*  572:     */     {
/*  573: 956 */       NodeList children = nodeE.getChildNodes();
/*  574: 958 */       for (int i = 0; i < children.getLength(); i++)
/*  575:     */       {
/*  576: 959 */         Node child = children.item(i);
/*  577: 960 */         if (child.getNodeType() == 1)
/*  578:     */         {
/*  579: 961 */           String tagName = ((Element)child).getTagName();
/*  580: 962 */           if (tagName.equals("Node"))
/*  581:     */           {
/*  582: 963 */             TreeNode tempN = new TreeNode(TreeModel.this, (Element)child, miningSchema);
/*  583: 964 */             this.m_childNodes.add(tempN);
/*  584:     */           }
/*  585:     */         }
/*  586:     */       }
/*  587:     */     }
/*  588:     */     
/*  589:     */     private void getScoreDistributions(Element nodeE, MiningSchema miningSchema)
/*  590:     */       throws Exception
/*  591:     */     {
/*  592: 973 */       NodeList scoreChildren = nodeE.getChildNodes();
/*  593: 974 */       for (int i = 0; i < scoreChildren.getLength(); i++)
/*  594:     */       {
/*  595: 975 */         Node child = scoreChildren.item(i);
/*  596: 976 */         if (child.getNodeType() == 1)
/*  597:     */         {
/*  598: 977 */           String tagName = ((Element)child).getTagName();
/*  599: 978 */           if (tagName.equals("ScoreDistribution"))
/*  600:     */           {
/*  601: 979 */             TreeModel.ScoreDistribution newDist = new TreeModel.ScoreDistribution((Element)child, miningSchema, this.m_recordCount);
/*  602:     */             
/*  603: 981 */             this.m_scoreDistributions.add(newDist);
/*  604:     */           }
/*  605:     */         }
/*  606:     */       }
/*  607:     */       double baseCount;
/*  608: 987 */       if (Utils.isMissingValue(this.m_recordCount))
/*  609:     */       {
/*  610: 988 */         baseCount = 0.0D;
/*  611: 989 */         for (TreeModel.ScoreDistribution s : this.m_scoreDistributions) {
/*  612: 990 */           baseCount += s.getRecordCount();
/*  613:     */         }
/*  614: 993 */         for (TreeModel.ScoreDistribution s : this.m_scoreDistributions) {
/*  615: 994 */           s.deriveConfidenceValue(baseCount);
/*  616:     */         }
/*  617:     */       }
/*  618:     */     }
/*  619:     */     
/*  620:     */     protected String getScore()
/*  621:     */     {
/*  622:1005 */       return this.m_scoreString;
/*  623:     */     }
/*  624:     */     
/*  625:     */     protected double getScoreNumeric()
/*  626:     */     {
/*  627:1014 */       return this.m_scoreNumeric;
/*  628:     */     }
/*  629:     */     
/*  630:     */     protected String getID()
/*  631:     */     {
/*  632:1023 */       return this.m_ID;
/*  633:     */     }
/*  634:     */     
/*  635:     */     protected TreeModel.Predicate getPredicate()
/*  636:     */     {
/*  637:1032 */       return this.m_predicate;
/*  638:     */     }
/*  639:     */     
/*  640:     */     protected double getRecordCount()
/*  641:     */     {
/*  642:1041 */       return this.m_recordCount;
/*  643:     */     }
/*  644:     */     
/*  645:     */     protected void dumpGraph(StringBuffer text)
/*  646:     */       throws Exception
/*  647:     */     {
/*  648:1045 */       text.append("N" + this.m_ID + " ");
/*  649:1046 */       if (this.m_scoreString != null) {
/*  650:1047 */         text.append("[label=\"score=" + this.m_scoreString);
/*  651:     */       }
/*  652:1050 */       if ((this.m_scoreDistributions.size() > 0) && (this.m_childNodes.size() == 0))
/*  653:     */       {
/*  654:1051 */         text.append("\\n");
/*  655:1052 */         for (TreeModel.ScoreDistribution s : this.m_scoreDistributions) {
/*  656:1053 */           text.append(s + "\\n");
/*  657:     */         }
/*  658:     */       }
/*  659:1057 */       text.append("\"");
/*  660:1059 */       if (this.m_childNodes.size() == 0) {
/*  661:1060 */         text.append(" shape=box style=filled");
/*  662:     */       }
/*  663:1064 */       text.append("]\n");
/*  664:1066 */       for (TreeNode c : this.m_childNodes)
/*  665:     */       {
/*  666:1067 */         text.append("N" + this.m_ID + "->" + "N" + c.getID());
/*  667:1068 */         text.append(" [label=\"" + c.getPredicate().toString(0, true));
/*  668:1069 */         text.append("\"]\n");
/*  669:1070 */         c.dumpGraph(text);
/*  670:     */       }
/*  671:     */     }
/*  672:     */     
/*  673:     */     public String toString()
/*  674:     */     {
/*  675:1076 */       StringBuffer text = new StringBuffer();
/*  676:     */       
/*  677:     */ 
/*  678:1079 */       dumpTree(0, text);
/*  679:     */       
/*  680:1081 */       return text.toString();
/*  681:     */     }
/*  682:     */     
/*  683:     */     protected void dumpTree(int level, StringBuffer text)
/*  684:     */     {
/*  685:1085 */       if (this.m_childNodes.size() > 0)
/*  686:     */       {
/*  687:1087 */         for (int i = 0; i < this.m_childNodes.size(); i++)
/*  688:     */         {
/*  689:1088 */           text.append("\n");
/*  690:     */           
/*  691:     */ 
/*  692:     */ 
/*  693:     */ 
/*  694:     */ 
/*  695:     */ 
/*  696:1095 */           TreeNode child = (TreeNode)this.m_childNodes.get(i);
/*  697:1096 */           text.append(child.getPredicate().toString(level, false));
/*  698:     */           
/*  699:     */ 
/*  700:1099 */           child.dumpTree(level + 1, text);
/*  701:     */         }
/*  702:     */       }
/*  703:     */       else
/*  704:     */       {
/*  705:1103 */         text.append(": ");
/*  706:1104 */         if (!Utils.isMissingValue(this.m_scoreNumeric))
/*  707:     */         {
/*  708:1105 */           text.append(this.m_scoreNumeric);
/*  709:     */         }
/*  710:     */         else
/*  711:     */         {
/*  712:1107 */           text.append(this.m_scoreString + " ");
/*  713:1108 */           if (this.m_scoreDistributions.size() > 0)
/*  714:     */           {
/*  715:1109 */             text.append("[");
/*  716:1110 */             for (TreeModel.ScoreDistribution s : this.m_scoreDistributions) {
/*  717:1111 */               text.append(s);
/*  718:     */             }
/*  719:1113 */             text.append("]");
/*  720:     */           }
/*  721:     */           else
/*  722:     */           {
/*  723:1115 */             text.append(this.m_scoreString);
/*  724:     */           }
/*  725:     */         }
/*  726:     */       }
/*  727:     */     }
/*  728:     */     
/*  729:     */     protected double[] score(double[] instance, Attribute classAtt)
/*  730:     */       throws Exception
/*  731:     */     {
/*  732:1131 */       double[] preds = null;
/*  733:1133 */       if (classAtt.isNumeric()) {
/*  734:1134 */         preds = new double[1];
/*  735:     */       } else {
/*  736:1136 */         preds = new double[classAtt.numValues()];
/*  737:     */       }
/*  738:1140 */       if (this.m_childNodes.size() == 0) {
/*  739:1141 */         doLeaf(classAtt, preds);
/*  740:     */       } else {
/*  741:1144 */         switch (TreeModel.1.$SwitchMap$weka$classifiers$pmml$consumer$TreeModel$MissingValueStrategy[TreeModel.this.m_missingValueStrategy.ordinal()])
/*  742:     */         {
/*  743:     */         case 1: 
/*  744:1146 */           preds = missingValueStrategyNone(instance, classAtt);
/*  745:1147 */           break;
/*  746:     */         case 2: 
/*  747:1149 */           preds = missingValueStrategyLastPrediction(instance, classAtt);
/*  748:1150 */           break;
/*  749:     */         case 3: 
/*  750:1152 */           preds = missingValueStrategyDefaultChild(instance, classAtt);
/*  751:1153 */           break;
/*  752:     */         default: 
/*  753:1155 */           throw new Exception("[TreeModel] not implemented!");
/*  754:     */         }
/*  755:     */       }
/*  756:1159 */       return preds;
/*  757:     */     }
/*  758:     */     
/*  759:     */     protected void doLeaf(Attribute classAtt, double[] preds)
/*  760:     */       throws Exception
/*  761:     */     {
/*  762:1170 */       if (classAtt.isNumeric()) {
/*  763:1171 */         preds[0] = this.m_scoreNumeric;
/*  764:1173 */       } else if (this.m_scoreDistributions.size() == 0) {
/*  765:1174 */         preds[this.m_scoreIndex] = 1.0D;
/*  766:     */       } else {
/*  767:1177 */         for (TreeModel.ScoreDistribution s : this.m_scoreDistributions) {
/*  768:1178 */           preds[s.getClassLabelIndex()] = s.getConfidence();
/*  769:     */         }
/*  770:     */       }
/*  771:     */     }
/*  772:     */     
/*  773:     */     protected void doNoTrueChild(Attribute classAtt, double[] preds)
/*  774:     */       throws Exception
/*  775:     */     {
/*  776:1193 */       if (TreeModel.this.m_noTrueChildStrategy == TreeModel.NoTrueChildStrategy.RETURNNULLPREDICTION) {
/*  777:1194 */         for (int i = 0; i < classAtt.numValues(); i++) {
/*  778:1195 */           preds[i] = Utils.missingValue();
/*  779:     */         }
/*  780:     */       } else {
/*  781:1199 */         doLeaf(classAtt, preds);
/*  782:     */       }
/*  783:     */     }
/*  784:     */     
/*  785:     */     protected double[] missingValueStrategyWeightedConfidence(double[] instance, Attribute classAtt)
/*  786:     */       throws Exception
/*  787:     */     {
/*  788:1216 */       if (classAtt.isNumeric()) {
/*  789:1217 */         throw new Exception("[TreeNode] missing value strategy weighted confidence, but class is numeric!");
/*  790:     */       }
/*  791:1222 */       double[] preds = null;
/*  792:1223 */       TreeNode trueNode = null;
/*  793:1224 */       boolean strategyInvoked = false;
/*  794:1225 */       int nodeCount = 0;
/*  795:1228 */       for (TreeNode c : this.m_childNodes) {
/*  796:1229 */         if (c.getPredicate().evaluate(instance) == TreeModel.Predicate.Eval.TRUE)
/*  797:     */         {
/*  798:1231 */           if (trueNode == null) {
/*  799:1232 */             trueNode = c;
/*  800:     */           }
/*  801:1234 */           nodeCount++;
/*  802:     */         }
/*  803:1235 */         else if (c.getPredicate().evaluate(instance) == TreeModel.Predicate.Eval.UNKNOWN)
/*  804:     */         {
/*  805:1236 */           strategyInvoked = true;
/*  806:1237 */           nodeCount++;
/*  807:     */         }
/*  808:     */       }
/*  809:1241 */       if (strategyInvoked)
/*  810:     */       {
/*  811:1243 */         double[][] dists = new double[nodeCount][];
/*  812:1244 */         double[] weights = new double[nodeCount];
/*  813:     */         
/*  814:     */ 
/*  815:1247 */         int count = 0;
/*  816:1248 */         for (TreeNode c : this.m_childNodes) {
/*  817:1249 */           if ((c.getPredicate().evaluate(instance) == TreeModel.Predicate.Eval.TRUE) || (c.getPredicate().evaluate(instance) == TreeModel.Predicate.Eval.UNKNOWN))
/*  818:     */           {
/*  819:1252 */             weights[count] = c.getRecordCount();
/*  820:1253 */             if (Utils.isMissingValue(weights[count])) {
/*  821:1254 */               throw new Exception("[TreeNode] weighted confidence missing value strategy invoked, but no record count defined for node " + c.getID());
/*  822:     */             }
/*  823:1259 */             dists[(count++)] = c.score(instance, classAtt);
/*  824:     */           }
/*  825:     */         }
/*  826:1264 */         preds = new double[classAtt.numValues()];
/*  827:1265 */         for (int i = 0; i < classAtt.numValues(); i++) {
/*  828:1266 */           for (int j = 0; j < nodeCount; j++) {
/*  829:1267 */             preds[i] += weights[j] / this.m_recordCount * dists[j][i];
/*  830:     */           }
/*  831:     */         }
/*  832:     */       }
/*  833:1271 */       else if (trueNode != null)
/*  834:     */       {
/*  835:1272 */         preds = trueNode.score(instance, classAtt);
/*  836:     */       }
/*  837:     */       else
/*  838:     */       {
/*  839:1274 */         doNoTrueChild(classAtt, preds);
/*  840:     */       }
/*  841:1278 */       return preds;
/*  842:     */     }
/*  843:     */     
/*  844:     */     protected double[] freqCountsForAggNodesStrategy(double[] instance, Attribute classAtt)
/*  845:     */       throws Exception
/*  846:     */     {
/*  847:1284 */       double[] counts = new double[classAtt.numValues()];
/*  848:1286 */       if (this.m_childNodes.size() > 0)
/*  849:     */       {
/*  850:1288 */         for (TreeNode c : this.m_childNodes) {
/*  851:1289 */           if ((c.getPredicate().evaluate(instance) == TreeModel.Predicate.Eval.TRUE) || (c.getPredicate().evaluate(instance) == TreeModel.Predicate.Eval.UNKNOWN))
/*  852:     */           {
/*  853:1292 */             double[] temp = c.freqCountsForAggNodesStrategy(instance, classAtt);
/*  854:1293 */             for (int i = 0; i < classAtt.numValues(); i++) {
/*  855:1294 */               counts[i] += temp[i];
/*  856:     */             }
/*  857:     */           }
/*  858:     */         }
/*  859:     */       }
/*  860:     */       else
/*  861:     */       {
/*  862:1300 */         if (this.m_scoreDistributions.size() == 0) {
/*  863:1301 */           throw new Exception("[TreeModel] missing value strategy aggregate nodes: no score distributions at leaf " + this.m_ID);
/*  864:     */         }
/*  865:1305 */         for (TreeModel.ScoreDistribution s : this.m_scoreDistributions) {
/*  866:1306 */           counts[s.getClassLabelIndex()] = s.getRecordCount();
/*  867:     */         }
/*  868:     */       }
/*  869:1310 */       return counts;
/*  870:     */     }
/*  871:     */     
/*  872:     */     protected double[] missingValueStrategyAggregateNodes(double[] instance, Attribute classAtt)
/*  873:     */       throws Exception
/*  874:     */     {
/*  875:1326 */       if (classAtt.isNumeric()) {
/*  876:1327 */         throw new Exception("[TreeNode] missing value strategy aggregate nodes, but class is numeric!");
/*  877:     */       }
/*  878:1332 */       double[] preds = null;
/*  879:1333 */       TreeNode trueNode = null;
/*  880:1334 */       boolean strategyInvoked = false;
/*  881:1336 */       for (TreeNode c : this.m_childNodes) {
/*  882:1337 */         if (c.getPredicate().evaluate(instance) == TreeModel.Predicate.Eval.TRUE)
/*  883:     */         {
/*  884:1339 */           if (trueNode == null) {
/*  885:1340 */             trueNode = c;
/*  886:     */           }
/*  887:     */         }
/*  888:1342 */         else if (c.getPredicate().evaluate(instance) == TreeModel.Predicate.Eval.UNKNOWN) {
/*  889:1343 */           strategyInvoked = true;
/*  890:     */         }
/*  891:     */       }
/*  892:1347 */       if (strategyInvoked)
/*  893:     */       {
/*  894:1348 */         double[] aggregatedCounts = freqCountsForAggNodesStrategy(instance, classAtt);
/*  895:     */         
/*  896:     */ 
/*  897:     */ 
/*  898:1352 */         Utils.normalize(aggregatedCounts);
/*  899:1353 */         preds = aggregatedCounts;
/*  900:     */       }
/*  901:1355 */       else if (trueNode != null)
/*  902:     */       {
/*  903:1356 */         preds = trueNode.score(instance, classAtt);
/*  904:     */       }
/*  905:     */       else
/*  906:     */       {
/*  907:1358 */         doNoTrueChild(classAtt, preds);
/*  908:     */       }
/*  909:1362 */       return preds;
/*  910:     */     }
/*  911:     */     
/*  912:     */     protected double[] missingValueStrategyDefaultChild(double[] instance, Attribute classAtt)
/*  913:     */       throws Exception
/*  914:     */     {
/*  915:1378 */       double[] preds = null;
/*  916:1379 */       boolean strategyInvoked = false;
/*  917:1382 */       for (TreeNode c : this.m_childNodes)
/*  918:     */       {
/*  919:1383 */         if (c.getPredicate().evaluate(instance) == TreeModel.Predicate.Eval.TRUE)
/*  920:     */         {
/*  921:1384 */           preds = c.score(instance, classAtt);
/*  922:1385 */           break;
/*  923:     */         }
/*  924:1386 */         if (c.getPredicate().evaluate(instance) == TreeModel.Predicate.Eval.UNKNOWN) {
/*  925:1387 */           strategyInvoked = true;
/*  926:     */         }
/*  927:     */       }
/*  928:1392 */       if (preds == null) {
/*  929:1393 */         if (!strategyInvoked) {
/*  930:1394 */           doNoTrueChild(classAtt, preds);
/*  931:1400 */         } else if (this.m_defaultChild != null) {
/*  932:1401 */           preds = this.m_defaultChild.score(instance, classAtt);
/*  933:     */         } else {
/*  934:1403 */           throw new Exception("[TreeNode] missing value strategy is defaultChild, but no default child has been specified in node " + this.m_ID);
/*  935:     */         }
/*  936:     */       }
/*  937:1410 */       return preds;
/*  938:     */     }
/*  939:     */     
/*  940:     */     protected double[] missingValueStrategyLastPrediction(double[] instance, Attribute classAtt)
/*  941:     */       throws Exception
/*  942:     */     {
/*  943:1426 */       double[] preds = null;
/*  944:1427 */       boolean strategyInvoked = false;
/*  945:1430 */       for (TreeNode c : this.m_childNodes)
/*  946:     */       {
/*  947:1431 */         if (c.getPredicate().evaluate(instance) == TreeModel.Predicate.Eval.TRUE)
/*  948:     */         {
/*  949:1432 */           preds = c.score(instance, classAtt);
/*  950:1433 */           break;
/*  951:     */         }
/*  952:1434 */         if (c.getPredicate().evaluate(instance) == TreeModel.Predicate.Eval.UNKNOWN) {
/*  953:1435 */           strategyInvoked = true;
/*  954:     */         }
/*  955:     */       }
/*  956:1440 */       if (preds == null)
/*  957:     */       {
/*  958:1441 */         preds = new double[classAtt.numValues()];
/*  959:1442 */         if (!strategyInvoked) {
/*  960:1444 */           doNoTrueChild(classAtt, preds);
/*  961:     */         } else {
/*  962:1447 */           doLeaf(classAtt, preds);
/*  963:     */         }
/*  964:     */       }
/*  965:1451 */       return preds;
/*  966:     */     }
/*  967:     */     
/*  968:     */     protected double[] missingValueStrategyNullPrediction(double[] instance, Attribute classAtt)
/*  969:     */       throws Exception
/*  970:     */     {
/*  971:1467 */       double[] preds = null;
/*  972:1468 */       boolean strategyInvoked = false;
/*  973:1471 */       for (TreeNode c : this.m_childNodes)
/*  974:     */       {
/*  975:1472 */         if (c.getPredicate().evaluate(instance) == TreeModel.Predicate.Eval.TRUE)
/*  976:     */         {
/*  977:1473 */           preds = c.score(instance, classAtt);
/*  978:1474 */           break;
/*  979:     */         }
/*  980:1475 */         if (c.getPredicate().evaluate(instance) == TreeModel.Predicate.Eval.UNKNOWN) {
/*  981:1476 */           strategyInvoked = true;
/*  982:     */         }
/*  983:     */       }
/*  984:1481 */       if (preds == null)
/*  985:     */       {
/*  986:1482 */         preds = new double[classAtt.numValues()];
/*  987:1483 */         if (!strategyInvoked) {
/*  988:1484 */           doNoTrueChild(classAtt, preds);
/*  989:     */         } else {
/*  990:1487 */           for (int i = 0; i < classAtt.numValues(); i++) {
/*  991:1488 */             preds[i] = Utils.missingValue();
/*  992:     */           }
/*  993:     */         }
/*  994:     */       }
/*  995:1493 */       return preds;
/*  996:     */     }
/*  997:     */     
/*  998:     */     protected double[] missingValueStrategyNone(double[] instance, Attribute classAtt)
/*  999:     */       throws Exception
/* 1000:     */     {
/* 1001:1509 */       double[] preds = null;
/* 1002:1512 */       for (TreeNode c : this.m_childNodes) {
/* 1003:1513 */         if (c.getPredicate().evaluate(instance) == TreeModel.Predicate.Eval.TRUE)
/* 1004:     */         {
/* 1005:1514 */           preds = c.score(instance, classAtt);
/* 1006:1515 */           break;
/* 1007:     */         }
/* 1008:     */       }
/* 1009:1519 */       if (preds == null)
/* 1010:     */       {
/* 1011:1520 */         preds = new double[classAtt.numValues()];
/* 1012:     */         
/* 1013:     */ 
/* 1014:1523 */         doNoTrueChild(classAtt, preds);
/* 1015:     */       }
/* 1016:1526 */       return preds;
/* 1017:     */     }
/* 1018:     */   }
/* 1019:     */   
/* 1020:     */   static enum MiningFunction
/* 1021:     */   {
/* 1022:1534 */     CLASSIFICATION,  REGRESSION;
/* 1023:     */     
/* 1024:     */     private MiningFunction() {}
/* 1025:     */   }
/* 1026:     */   
/* 1027:     */   static enum MissingValueStrategy
/* 1028:     */   {
/* 1029:1538 */     LASTPREDICTION("lastPrediction"),  NULLPREDICTION("nullPrediction"),  DEFAULTCHILD("defaultChild"),  WEIGHTEDCONFIDENCE("weightedConfidence"),  AGGREGATENODES("aggregateNodes"),  NONE("none");
/* 1030:     */     
/* 1031:     */     private final String m_stringVal;
/* 1032:     */     
/* 1033:     */     private MissingValueStrategy(String name)
/* 1034:     */     {
/* 1035:1545 */       this.m_stringVal = name;
/* 1036:     */     }
/* 1037:     */     
/* 1038:     */     public String toString()
/* 1039:     */     {
/* 1040:1550 */       return this.m_stringVal;
/* 1041:     */     }
/* 1042:     */   }
/* 1043:     */   
/* 1044:     */   static enum NoTrueChildStrategy
/* 1045:     */   {
/* 1046:1555 */     RETURNNULLPREDICTION("returnNullPrediction"),  RETURNLASTPREDICTION("returnLastPrediction");
/* 1047:     */     
/* 1048:     */     private final String m_stringVal;
/* 1049:     */     
/* 1050:     */     private NoTrueChildStrategy(String name)
/* 1051:     */     {
/* 1052:1561 */       this.m_stringVal = name;
/* 1053:     */     }
/* 1054:     */     
/* 1055:     */     public String toString()
/* 1056:     */     {
/* 1057:1566 */       return this.m_stringVal;
/* 1058:     */     }
/* 1059:     */   }
/* 1060:     */   
/* 1061:     */   static enum SplitCharacteristic
/* 1062:     */   {
/* 1063:1571 */     BINARYSPLIT("binarySplit"),  MULTISPLIT("multiSplit");
/* 1064:     */     
/* 1065:     */     private final String m_stringVal;
/* 1066:     */     
/* 1067:     */     private SplitCharacteristic(String name)
/* 1068:     */     {
/* 1069:1576 */       this.m_stringVal = name;
/* 1070:     */     }
/* 1071:     */     
/* 1072:     */     public String toString()
/* 1073:     */     {
/* 1074:1581 */       return this.m_stringVal;
/* 1075:     */     }
/* 1076:     */   }
/* 1077:     */   
/* 1078:1586 */   protected MiningFunction m_functionType = MiningFunction.CLASSIFICATION;
/* 1079:1589 */   protected MissingValueStrategy m_missingValueStrategy = MissingValueStrategy.NONE;
/* 1080:1595 */   protected double m_missingValuePenalty = Utils.missingValue();
/* 1081:1598 */   protected NoTrueChildStrategy m_noTrueChildStrategy = NoTrueChildStrategy.RETURNNULLPREDICTION;
/* 1082:1601 */   protected SplitCharacteristic m_splitCharacteristic = SplitCharacteristic.MULTISPLIT;
/* 1083:     */   protected TreeNode m_root;
/* 1084:     */   
/* 1085:     */   public TreeModel(Element model, Instances dataDictionary, MiningSchema miningSchema)
/* 1086:     */     throws Exception
/* 1087:     */   {
/* 1088:1609 */     super(dataDictionary, miningSchema);
/* 1089:1611 */     if (!getPMMLVersion().equals("3.2")) {}
/* 1090:1615 */     String fn = model.getAttribute("functionName");
/* 1091:1616 */     if (fn.equals("regression")) {
/* 1092:1617 */       this.m_functionType = MiningFunction.REGRESSION;
/* 1093:     */     }
/* 1094:1621 */     String missingVS = model.getAttribute("missingValueStrategy");
/* 1095:1622 */     if ((missingVS != null) && (missingVS.length() > 0)) {
/* 1096:1623 */       for (MissingValueStrategy m : MissingValueStrategy.values()) {
/* 1097:1624 */         if (m.toString().equals(missingVS))
/* 1098:     */         {
/* 1099:1625 */           this.m_missingValueStrategy = m;
/* 1100:1626 */           break;
/* 1101:     */         }
/* 1102:     */       }
/* 1103:     */     }
/* 1104:1632 */     String missingP = model.getAttribute("missingValuePenalty");
/* 1105:1633 */     if ((missingP != null) && (missingP.length() > 0)) {
/* 1106:     */       try
/* 1107:     */       {
/* 1108:1636 */         this.m_missingValuePenalty = Double.parseDouble(missingP);
/* 1109:     */       }
/* 1110:     */       catch (NumberFormatException ex)
/* 1111:     */       {
/* 1112:1638 */         System.err.println("[TreeModel] WARNING: couldn't parse supplied missingValuePenalty as a number");
/* 1113:     */       }
/* 1114:     */     }
/* 1115:1643 */     String splitC = model.getAttribute("splitCharacteristic");
/* 1116:1645 */     if ((splitC != null) && (splitC.length() > 0)) {
/* 1117:1646 */       for (SplitCharacteristic s : SplitCharacteristic.values()) {
/* 1118:1647 */         if (s.toString().equals(splitC))
/* 1119:     */         {
/* 1120:1648 */           this.m_splitCharacteristic = s;
/* 1121:1649 */           break;
/* 1122:     */         }
/* 1123:     */       }
/* 1124:     */     }
/* 1125:1655 */     NodeList children = model.getChildNodes();
/* 1126:1656 */     for (int i = 0; i < children.getLength(); i++)
/* 1127:     */     {
/* 1128:1657 */       Node child = children.item(i);
/* 1129:1658 */       if (child.getNodeType() == 1)
/* 1130:     */       {
/* 1131:1659 */         String tagName = ((Element)child).getTagName();
/* 1132:1660 */         if (tagName.equals("Node"))
/* 1133:     */         {
/* 1134:1661 */           this.m_root = new TreeNode((Element)child, miningSchema);
/* 1135:1662 */           break;
/* 1136:     */         }
/* 1137:     */       }
/* 1138:     */     }
/* 1139:     */   }
/* 1140:     */   
/* 1141:     */   public double[] distributionForInstance(Instance inst)
/* 1142:     */     throws Exception
/* 1143:     */   {
/* 1144:1679 */     if (!this.m_initialized) {
/* 1145:1680 */       mapToMiningSchema(inst.dataset());
/* 1146:     */     }
/* 1147:1682 */     double[] preds = null;
/* 1148:1684 */     if (this.m_miningSchema.getFieldsAsInstances().classAttribute().isNumeric()) {
/* 1149:1685 */       preds = new double[1];
/* 1150:     */     } else {
/* 1151:1687 */       preds = new double[this.m_miningSchema.getFieldsAsInstances().classAttribute().numValues()];
/* 1152:     */     }
/* 1153:1691 */     double[] incoming = this.m_fieldsMap.instanceToSchema(inst, this.m_miningSchema);
/* 1154:     */     
/* 1155:1693 */     preds = this.m_root.score(incoming, this.m_miningSchema.getFieldsAsInstances().classAttribute());
/* 1156:     */     
/* 1157:     */ 
/* 1158:1696 */     return preds;
/* 1159:     */   }
/* 1160:     */   
/* 1161:     */   public String toString()
/* 1162:     */   {
/* 1163:1701 */     StringBuffer temp = new StringBuffer();
/* 1164:     */     
/* 1165:1703 */     temp.append("PMML version " + getPMMLVersion());
/* 1166:1704 */     if (!getCreatorApplication().equals("?")) {
/* 1167:1705 */       temp.append("\nApplication: " + getCreatorApplication());
/* 1168:     */     }
/* 1169:1707 */     temp.append("\nPMML Model: TreeModel");
/* 1170:1708 */     temp.append("\n\n");
/* 1171:1709 */     temp.append(this.m_miningSchema);
/* 1172:     */     
/* 1173:1711 */     temp.append("Split-type: " + this.m_splitCharacteristic + "\n");
/* 1174:1712 */     temp.append("No true child strategy: " + this.m_noTrueChildStrategy + "\n");
/* 1175:1713 */     temp.append("Missing value strategy: " + this.m_missingValueStrategy + "\n");
/* 1176:     */     
/* 1177:1715 */     temp.append(this.m_root.toString());
/* 1178:     */     
/* 1179:1717 */     return temp.toString();
/* 1180:     */   }
/* 1181:     */   
/* 1182:     */   public String graph()
/* 1183:     */     throws Exception
/* 1184:     */   {
/* 1185:1722 */     StringBuffer text = new StringBuffer();
/* 1186:1723 */     text.append("digraph PMMTree {\n");
/* 1187:     */     
/* 1188:1725 */     this.m_root.dumpGraph(text);
/* 1189:     */     
/* 1190:1727 */     text.append("}\n");
/* 1191:     */     
/* 1192:1729 */     return text.toString();
/* 1193:     */   }
/* 1194:     */   
/* 1195:     */   public String getRevision()
/* 1196:     */   {
/* 1197:1734 */     return RevisionUtils.extract("$Revision: 10153 $");
/* 1198:     */   }
/* 1199:     */   
/* 1200:     */   public int graphType()
/* 1201:     */   {
/* 1202:1739 */     return 1;
/* 1203:     */   }
/* 1204:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.pmml.consumer.TreeModel
 * JD-Core Version:    0.7.0.1
 */