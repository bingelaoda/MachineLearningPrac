/*    1:     */ package weka.classifiers.misc;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.io.Serializable;
/*    5:     */ import java.util.Collections;
/*    6:     */ import java.util.Enumeration;
/*    7:     */ import java.util.Vector;
/*    8:     */ import weka.classifiers.Classifier;
/*    9:     */ import weka.classifiers.SingleClassifierEnhancer;
/*   10:     */ import weka.core.AdditionalMeasureProducer;
/*   11:     */ import weka.core.Attribute;
/*   12:     */ import weka.core.Capabilities;
/*   13:     */ import weka.core.Capabilities.Capability;
/*   14:     */ import weka.core.DenseInstance;
/*   15:     */ import weka.core.Drawable;
/*   16:     */ import weka.core.Environment;
/*   17:     */ import weka.core.EnvironmentHandler;
/*   18:     */ import weka.core.Instance;
/*   19:     */ import weka.core.Instances;
/*   20:     */ import weka.core.Option;
/*   21:     */ import weka.core.OptionHandler;
/*   22:     */ import weka.core.RevisionUtils;
/*   23:     */ import weka.core.SerializationHelper;
/*   24:     */ import weka.core.Utils;
/*   25:     */ import weka.core.WeightedInstancesHandler;
/*   26:     */ 
/*   27:     */ public class InputMappedClassifier
/*   28:     */   extends SingleClassifierEnhancer
/*   29:     */   implements Serializable, OptionHandler, Drawable, WeightedInstancesHandler, AdditionalMeasureProducer, EnvironmentHandler
/*   30:     */ {
/*   31:     */   private static final long serialVersionUID = 4901630631723287761L;
/*   32: 121 */   protected String m_modelPath = "";
/*   33:     */   protected transient Instances m_inputHeader;
/*   34:     */   protected Instances m_modelHeader;
/*   35:     */   protected transient Environment m_env;
/*   36:     */   protected transient int[] m_attributeMap;
/*   37:     */   protected transient int[] m_attributeStatus;
/*   38:     */   protected transient int[][] m_nominalValueMap;
/*   39: 144 */   protected boolean m_trim = true;
/*   40: 147 */   protected boolean m_ignoreCase = true;
/*   41: 150 */   protected boolean m_suppressMappingReport = false;
/*   42: 160 */   protected boolean m_initialTestStructureKnown = false;
/*   43:     */   protected double[] m_vals;
/*   44:     */   protected static final int NO_MATCH = -1;
/*   45:     */   protected static final int TYPE_MISMATCH = -2;
/*   46:     */   protected static final int OK = -3;
/*   47:     */   
/*   48:     */   public String globalInfo()
/*   49:     */   {
/*   50: 172 */     return "Wrapper classifier that addresses incompatible training and test data by building a mapping between the training data that a classifier has been built with and the incoming test instances' structure. Model attributes that are not found in the incoming instances receive missing values, so do incoming nominal attribute values that the classifier has not seen before. A new classifier can be trained or an existing one loaded from a file.";
/*   51:     */   }
/*   52:     */   
/*   53:     */   public void setEnvironment(Environment env)
/*   54:     */   {
/*   55: 188 */     this.m_env = env;
/*   56:     */   }
/*   57:     */   
/*   58:     */   public String ignoreCaseForNamesTipText()
/*   59:     */   {
/*   60: 198 */     return "Ignore case when matching attribute names and nomina values.";
/*   61:     */   }
/*   62:     */   
/*   63:     */   public void setIgnoreCaseForNames(boolean ignore)
/*   64:     */   {
/*   65: 208 */     this.m_ignoreCase = ignore;
/*   66:     */   }
/*   67:     */   
/*   68:     */   public boolean getIgnoreCaseForNames()
/*   69:     */   {
/*   70: 218 */     return this.m_ignoreCase;
/*   71:     */   }
/*   72:     */   
/*   73:     */   public String trimTipText()
/*   74:     */   {
/*   75: 228 */     return "Trim white space from each end of attribute names and nominal values before matching.";
/*   76:     */   }
/*   77:     */   
/*   78:     */   public void setTrim(boolean trim)
/*   79:     */   {
/*   80: 238 */     this.m_trim = trim;
/*   81:     */   }
/*   82:     */   
/*   83:     */   public boolean getTrim()
/*   84:     */   {
/*   85: 247 */     return this.m_trim;
/*   86:     */   }
/*   87:     */   
/*   88:     */   public String suppressMappingReportTipText()
/*   89:     */   {
/*   90: 257 */     return "Don't output a report of model-to-input mappings.";
/*   91:     */   }
/*   92:     */   
/*   93:     */   public void setSuppressMappingReport(boolean suppress)
/*   94:     */   {
/*   95: 266 */     this.m_suppressMappingReport = suppress;
/*   96:     */   }
/*   97:     */   
/*   98:     */   public boolean getSuppressMappingReport()
/*   99:     */   {
/*  100: 275 */     return this.m_suppressMappingReport;
/*  101:     */   }
/*  102:     */   
/*  103:     */   public String modelPathTipText()
/*  104:     */   {
/*  105: 285 */     return "Set the path from which to load a model. Loading occurs when the first test instance is received. Environment variables can be used in the supplied path.";
/*  106:     */   }
/*  107:     */   
/*  108:     */   public void setModelPath(String modelPath)
/*  109:     */     throws Exception
/*  110:     */   {
/*  111: 301 */     if (this.m_env == null) {
/*  112: 302 */       this.m_env = Environment.getSystemWide();
/*  113:     */     }
/*  114: 305 */     this.m_modelPath = modelPath;
/*  115:     */   }
/*  116:     */   
/*  117:     */   public String getModelPath()
/*  118:     */   {
/*  119: 316 */     return this.m_modelPath;
/*  120:     */   }
/*  121:     */   
/*  122:     */   public Capabilities getCapabilities()
/*  123:     */   {
/*  124: 326 */     Capabilities result = super.getCapabilities();
/*  125:     */     
/*  126: 328 */     result.disable(Capabilities.Capability.RELATIONAL_ATTRIBUTES);
/*  127:     */     
/*  128: 330 */     return result;
/*  129:     */   }
/*  130:     */   
/*  131:     */   public Enumeration<Option> listOptions()
/*  132:     */   {
/*  133: 390 */     Vector<Option> newVector = new Vector(4);
/*  134:     */     
/*  135: 392 */     newVector.addElement(new Option("\tIgnore case when matching attribute names and nominal values.", "I", 0, "-I"));
/*  136:     */     
/*  137: 394 */     newVector.addElement(new Option("\tSuppress the output of the mapping report.", "M", 0, "-M"));
/*  138:     */     
/*  139: 396 */     newVector.addElement(new Option("\tTrim white space from either end of names before matching.", "trim", 0, "-trim"));
/*  140:     */     
/*  141:     */ 
/*  142: 399 */     newVector.addElement(new Option("\tPath to a model to load. If set, this model\n\twill be used for prediction and any base classifier\n\tspecification will be ignored. Environment variables\n\tmay be used in the path (e.g. ${HOME}/myModel.model)", "L", 1, "-L <path to model to load>"));
/*  143:     */     
/*  144:     */ 
/*  145:     */ 
/*  146:     */ 
/*  147:     */ 
/*  148:     */ 
/*  149: 406 */     newVector.addAll(Collections.list(super.listOptions()));
/*  150:     */     
/*  151: 408 */     return newVector.elements();
/*  152:     */   }
/*  153:     */   
/*  154:     */   public void setOptions(String[] options)
/*  155:     */     throws Exception
/*  156:     */   {
/*  157: 473 */     setIgnoreCaseForNames(Utils.getFlag('I', options));
/*  158: 474 */     setSuppressMappingReport(Utils.getFlag('M', options));
/*  159: 475 */     setTrim(Utils.getFlag("trim", options));
/*  160:     */     
/*  161: 477 */     String modelPath = Utils.getOption('L', options);
/*  162: 478 */     if (modelPath.length() > 0) {
/*  163: 479 */       setModelPath(modelPath);
/*  164:     */     }
/*  165: 482 */     super.setOptions(options);
/*  166:     */   }
/*  167:     */   
/*  168:     */   public String[] getOptions()
/*  169:     */   {
/*  170: 492 */     String[] superOptions = super.getOptions();
/*  171: 493 */     String[] options = new String[superOptions.length + 5];
/*  172:     */     
/*  173: 495 */     int current = 0;
/*  174: 496 */     if (getIgnoreCaseForNames()) {
/*  175: 497 */       options[(current++)] = "-I";
/*  176:     */     }
/*  177: 499 */     if (getSuppressMappingReport()) {
/*  178: 500 */       options[(current++)] = "-M";
/*  179:     */     }
/*  180: 502 */     if (getTrim()) {
/*  181: 503 */       options[(current++)] = "-trim";
/*  182:     */     }
/*  183: 506 */     if ((getModelPath() != null) && (getModelPath().length() > 0))
/*  184:     */     {
/*  185: 507 */       options[(current++)] = "-L";
/*  186: 508 */       options[(current++)] = getModelPath();
/*  187:     */     }
/*  188: 511 */     System.arraycopy(superOptions, 0, options, current, superOptions.length);
/*  189:     */     
/*  190: 513 */     current += superOptions.length;
/*  191: 514 */     while (current < options.length) {
/*  192: 515 */       options[(current++)] = "";
/*  193:     */     }
/*  194: 517 */     return options;
/*  195:     */   }
/*  196:     */   
/*  197:     */   public void setTestStructure(Instances testStructure)
/*  198:     */   {
/*  199: 533 */     this.m_inputHeader = testStructure;
/*  200: 534 */     this.m_initialTestStructureKnown = true;
/*  201:     */   }
/*  202:     */   
/*  203:     */   public void setModelHeader(Instances modelHeader)
/*  204:     */   {
/*  205: 546 */     this.m_modelHeader = modelHeader;
/*  206:     */   }
/*  207:     */   
/*  208:     */   private void loadModel(String modelPath)
/*  209:     */     throws Exception
/*  210:     */   {
/*  211: 550 */     if ((modelPath != null) && (modelPath.length() > 0))
/*  212:     */     {
/*  213:     */       try
/*  214:     */       {
/*  215: 552 */         if (this.m_env == null) {
/*  216: 553 */           this.m_env = Environment.getSystemWide();
/*  217:     */         }
/*  218: 556 */         modelPath = this.m_env.substitute(modelPath);
/*  219:     */       }
/*  220:     */       catch (Exception ex) {}
/*  221:     */       try
/*  222:     */       {
/*  223: 562 */         Object[] modelAndHeader = SerializationHelper.readAll(modelPath);
/*  224: 564 */         if (modelAndHeader.length != 2) {
/*  225: 565 */           throw new Exception("[InputMappedClassifier] serialized model file does not seem to contain both a model and the instances header used in training it!");
/*  226:     */         }
/*  227: 569 */         setClassifier((Classifier)modelAndHeader[0]);
/*  228: 570 */         this.m_modelHeader = ((Instances)modelAndHeader[1]);
/*  229:     */       }
/*  230:     */       catch (Exception ex)
/*  231:     */       {
/*  232: 573 */         ex.printStackTrace();
/*  233:     */       }
/*  234:     */     }
/*  235:     */   }
/*  236:     */   
/*  237:     */   public void buildClassifier(Instances data)
/*  238:     */     throws Exception
/*  239:     */   {
/*  240: 587 */     if (!this.m_initialTestStructureKnown) {
/*  241: 588 */       this.m_inputHeader = new Instances(data, 0);
/*  242:     */     }
/*  243: 591 */     this.m_attributeMap = null;
/*  244: 593 */     if ((this.m_modelPath != null) && (this.m_modelPath.length() > 0)) {
/*  245: 594 */       return;
/*  246:     */     }
/*  247: 598 */     getCapabilities().testWithFail(data);
/*  248:     */     
/*  249: 600 */     this.m_Classifier.buildClassifier(data);
/*  250:     */     
/*  251: 602 */     this.m_modelHeader = new Instances(data, 0);
/*  252:     */   }
/*  253:     */   
/*  254:     */   private boolean stringMatch(String one, String two)
/*  255:     */   {
/*  256: 606 */     if (this.m_trim)
/*  257:     */     {
/*  258: 607 */       one = one.trim();
/*  259: 608 */       two = two.trim();
/*  260:     */     }
/*  261: 611 */     if (this.m_ignoreCase) {
/*  262: 612 */       return one.equalsIgnoreCase(two);
/*  263:     */     }
/*  264: 614 */     return one.equals(two);
/*  265:     */   }
/*  266:     */   
/*  267:     */   private String getFixedLengthString(String s, char pad, int len)
/*  268:     */   {
/*  269: 628 */     String padded = null;
/*  270: 629 */     if (len <= 0) {
/*  271: 630 */       return s;
/*  272:     */     }
/*  273: 633 */     if (s.length() >= len) {
/*  274: 634 */       return s.substring(0, len);
/*  275:     */     }
/*  276: 636 */     char[] buf = new char[len - s.length()];
/*  277: 637 */     for (int j = 0; j < len - s.length(); j++) {
/*  278: 638 */       buf[j] = pad;
/*  279:     */     }
/*  280: 640 */     padded = s + new String(buf);
/*  281:     */     
/*  282:     */ 
/*  283: 643 */     return padded;
/*  284:     */   }
/*  285:     */   
/*  286:     */   private StringBuffer createMappingReport()
/*  287:     */   {
/*  288: 647 */     StringBuffer result = new StringBuffer();
/*  289: 648 */     result.append("Attribute mappings:\n\n");
/*  290:     */     
/*  291: 650 */     int maxLength = 0;
/*  292: 651 */     for (int i = 0; i < this.m_modelHeader.numAttributes(); i++) {
/*  293: 652 */       if (this.m_modelHeader.attribute(i).name().length() > maxLength) {
/*  294: 653 */         maxLength = this.m_modelHeader.attribute(i).name().length();
/*  295:     */       }
/*  296:     */     }
/*  297: 656 */     maxLength += 12;
/*  298:     */     
/*  299: 658 */     int minLength = 16;
/*  300: 659 */     String headerS = "Model attributes";
/*  301: 660 */     String sep = "----------------";
/*  302: 662 */     if (maxLength < minLength) {
/*  303: 663 */       maxLength = minLength;
/*  304:     */     }
/*  305: 666 */     headerS = getFixedLengthString(headerS, ' ', maxLength);
/*  306: 667 */     sep = getFixedLengthString(sep, '-', maxLength);
/*  307: 668 */     sep = sep + "\t    ----------------\n";
/*  308: 669 */     headerS = headerS + "\t    Incoming attributes\n";
/*  309: 670 */     result.append(headerS);
/*  310: 671 */     result.append(sep);
/*  311: 673 */     for (int i = 0; i < this.m_modelHeader.numAttributes(); i++)
/*  312:     */     {
/*  313: 674 */       Attribute temp = this.m_modelHeader.attribute(i);
/*  314: 675 */       String attName = "(" + (temp.isNumeric() ? "numeric)" : "nominal)") + " " + temp.name();
/*  315:     */       
/*  316: 677 */       attName = getFixedLengthString(attName, ' ', maxLength);
/*  317: 678 */       attName = attName + "\t--> ";
/*  318: 679 */       result.append(attName);
/*  319: 680 */       String inAttNum = "";
/*  320: 681 */       if (this.m_attributeStatus[i] == -1)
/*  321:     */       {
/*  322: 682 */         inAttNum = inAttNum + "- ";
/*  323: 683 */         result.append(inAttNum + "missing (no match)\n");
/*  324:     */       }
/*  325: 684 */       else if (this.m_attributeStatus[i] == -2)
/*  326:     */       {
/*  327: 685 */         inAttNum = inAttNum + (this.m_attributeMap[i] + 1) + " ";
/*  328: 686 */         result.append(inAttNum + "missing (type mis-match)\n");
/*  329:     */       }
/*  330:     */       else
/*  331:     */       {
/*  332: 688 */         Attribute inAtt = this.m_inputHeader.attribute(this.m_attributeMap[i]);
/*  333: 689 */         String inName = "" + (this.m_attributeMap[i] + 1) + " (" + (inAtt.isNumeric() ? "numeric)" : "nominal)") + " " + inAtt.name();
/*  334:     */         
/*  335:     */ 
/*  336: 692 */         result.append(inName + "\n");
/*  337:     */       }
/*  338:     */     }
/*  339: 696 */     return result;
/*  340:     */   }
/*  341:     */   
/*  342:     */   private boolean regenerateMapping()
/*  343:     */     throws Exception
/*  344:     */   {
/*  345: 704 */     loadModel(this.m_modelPath);
/*  346: 706 */     if (this.m_modelHeader == null) {
/*  347: 707 */       return false;
/*  348:     */     }
/*  349: 710 */     this.m_attributeMap = new int[this.m_modelHeader.numAttributes()];
/*  350: 711 */     this.m_attributeStatus = new int[this.m_modelHeader.numAttributes()];
/*  351: 712 */     this.m_nominalValueMap = new int[this.m_modelHeader.numAttributes()][];
/*  352: 714 */     for (int i = 0; i < this.m_modelHeader.numAttributes(); i++)
/*  353:     */     {
/*  354: 715 */       String modelAttName = this.m_modelHeader.attribute(i).name();
/*  355: 716 */       this.m_attributeStatus[i] = -1;
/*  356: 718 */       for (int j = 0; j < this.m_inputHeader.numAttributes(); j++)
/*  357:     */       {
/*  358: 719 */         String incomingAttName = this.m_inputHeader.attribute(j).name();
/*  359: 720 */         if (stringMatch(modelAttName, incomingAttName))
/*  360:     */         {
/*  361: 721 */           this.m_attributeMap[i] = j;
/*  362: 722 */           this.m_attributeStatus[i] = -3;
/*  363:     */           
/*  364: 724 */           Attribute modelAtt = this.m_modelHeader.attribute(i);
/*  365: 725 */           Attribute incomingAtt = this.m_inputHeader.attribute(j);
/*  366: 728 */           if (modelAtt.type() != incomingAtt.type())
/*  367:     */           {
/*  368: 729 */             this.m_attributeStatus[i] = -2;
/*  369: 730 */             break;
/*  370:     */           }
/*  371: 734 */           if (modelAtt.numValues() != incomingAtt.numValues()) {
/*  372: 735 */             System.out.println("[InputMappedClassifier] Warning: incoming nominal attribute " + incomingAttName + " does not have the same " + "number of values as model attribute " + modelAttName);
/*  373:     */           }
/*  374: 742 */           if ((modelAtt.isNominal()) && (incomingAtt.isNominal()))
/*  375:     */           {
/*  376: 743 */             int[] valuesMap = new int[incomingAtt.numValues()];
/*  377: 744 */             for (int k = 0; k < incomingAtt.numValues(); k++)
/*  378:     */             {
/*  379: 745 */               String incomingNomValue = incomingAtt.value(k);
/*  380: 746 */               int indexInModel = modelAtt.indexOfValue(incomingNomValue);
/*  381: 747 */               if (indexInModel < 0) {
/*  382: 748 */                 valuesMap[k] = -1;
/*  383:     */               } else {
/*  384: 750 */                 valuesMap[k] = indexInModel;
/*  385:     */               }
/*  386:     */             }
/*  387: 753 */             this.m_nominalValueMap[i] = valuesMap;
/*  388:     */           }
/*  389:     */         }
/*  390:     */       }
/*  391:     */     }
/*  392: 759 */     return true;
/*  393:     */   }
/*  394:     */   
/*  395:     */   public Instances getModelHeader(Instances defaultH)
/*  396:     */     throws Exception
/*  397:     */   {
/*  398: 773 */     loadModel(this.m_modelPath);
/*  399:     */     
/*  400:     */ 
/*  401:     */ 
/*  402:     */ 
/*  403: 778 */     Instances toReturn = this.m_modelHeader == null ? defaultH : this.m_modelHeader;
/*  404:     */     
/*  405: 780 */     return new Instances(toReturn, 0);
/*  406:     */   }
/*  407:     */   
/*  408:     */   public int getMappedClassIndex()
/*  409:     */     throws Exception
/*  410:     */   {
/*  411: 786 */     if (this.m_modelHeader == null) {
/*  412: 787 */       throw new Exception("[InputMappedClassifier] No model available!");
/*  413:     */     }
/*  414: 790 */     if (this.m_attributeMap[this.m_modelHeader.classIndex()] == -1) {
/*  415: 791 */       return -1;
/*  416:     */     }
/*  417: 794 */     return this.m_attributeMap[this.m_modelHeader.classIndex()];
/*  418:     */   }
/*  419:     */   
/*  420:     */   public Instance constructMappedInstance(Instance incoming)
/*  421:     */     throws Exception
/*  422:     */   {
/*  423: 799 */     boolean regenerateMapping = false;
/*  424: 801 */     if (this.m_inputHeader == null)
/*  425:     */     {
/*  426: 802 */       this.m_inputHeader = incoming.dataset();
/*  427: 803 */       regenerateMapping = true;
/*  428: 804 */       this.m_initialTestStructureKnown = false;
/*  429:     */     }
/*  430: 805 */     else if (!this.m_inputHeader.equalHeaders(incoming.dataset()))
/*  431:     */     {
/*  432: 814 */       this.m_inputHeader = incoming.dataset();
/*  433:     */       
/*  434: 816 */       regenerateMapping = true;
/*  435: 817 */       this.m_initialTestStructureKnown = false;
/*  436:     */     }
/*  437: 818 */     else if (this.m_attributeMap == null)
/*  438:     */     {
/*  439: 819 */       regenerateMapping = true;
/*  440: 820 */       this.m_initialTestStructureKnown = false;
/*  441:     */     }
/*  442: 823 */     if (regenerateMapping)
/*  443:     */     {
/*  444: 824 */       regenerateMapping();
/*  445: 825 */       this.m_vals = null;
/*  446: 827 */       if (!this.m_suppressMappingReport)
/*  447:     */       {
/*  448: 828 */         StringBuffer result = createMappingReport();
/*  449: 829 */         System.out.println(result.toString());
/*  450:     */       }
/*  451:     */     }
/*  452: 833 */     this.m_vals = new double[this.m_modelHeader.numAttributes()];
/*  453: 835 */     for (int i = 0; i < this.m_modelHeader.numAttributes(); i++) {
/*  454: 836 */       if (this.m_attributeStatus[i] == -3)
/*  455:     */       {
/*  456: 837 */         Attribute modelAtt = this.m_modelHeader.attribute(i);
/*  457: 838 */         this.m_inputHeader.attribute(this.m_attributeMap[i]);
/*  458: 840 */         if (Utils.isMissingValue(incoming.value(this.m_attributeMap[i])))
/*  459:     */         {
/*  460: 841 */           this.m_vals[i] = Utils.missingValue();
/*  461:     */         }
/*  462: 845 */         else if (modelAtt.isNumeric())
/*  463:     */         {
/*  464: 846 */           this.m_vals[i] = incoming.value(this.m_attributeMap[i]);
/*  465:     */         }
/*  466: 847 */         else if (modelAtt.isNominal())
/*  467:     */         {
/*  468: 848 */           int mapVal = this.m_nominalValueMap[i][((int)incoming.value(this.m_attributeMap[i]))];
/*  469: 851 */           if (mapVal == -1) {
/*  470: 852 */             this.m_vals[i] = Utils.missingValue();
/*  471:     */           } else {
/*  472: 854 */             this.m_vals[i] = mapVal;
/*  473:     */           }
/*  474:     */         }
/*  475:     */       }
/*  476:     */       else
/*  477:     */       {
/*  478: 858 */         this.m_vals[i] = Utils.missingValue();
/*  479:     */       }
/*  480:     */     }
/*  481: 862 */     Instance newInst = new DenseInstance(incoming.weight(), this.m_vals);
/*  482: 863 */     newInst.setDataset(this.m_modelHeader);
/*  483:     */     
/*  484: 865 */     return newInst;
/*  485:     */   }
/*  486:     */   
/*  487:     */   public double classifyInstance(Instance inst)
/*  488:     */     throws Exception
/*  489:     */   {
/*  490: 870 */     Instance converted = constructMappedInstance(inst);
/*  491: 871 */     return this.m_Classifier.classifyInstance(converted);
/*  492:     */   }
/*  493:     */   
/*  494:     */   public double[] distributionForInstance(Instance inst)
/*  495:     */     throws Exception
/*  496:     */   {
/*  497: 877 */     Instance converted = constructMappedInstance(inst);
/*  498: 878 */     return this.m_Classifier.distributionForInstance(converted);
/*  499:     */   }
/*  500:     */   
/*  501:     */   public String toString()
/*  502:     */   {
/*  503: 883 */     StringBuffer buff = new StringBuffer();
/*  504:     */     
/*  505: 885 */     buff.append("InputMappedClassifier:\n\n");
/*  506:     */     try
/*  507:     */     {
/*  508: 888 */       loadModel(this.m_modelPath);
/*  509:     */     }
/*  510:     */     catch (Exception ex)
/*  511:     */     {
/*  512: 890 */       return "[InputMappedClassifier] Problem loading model.";
/*  513:     */     }
/*  514: 893 */     if ((this.m_modelPath != null) && (this.m_modelPath.length() > 0)) {
/*  515: 894 */       buff.append("Model sourced from: " + this.m_modelPath + "\n\n");
/*  516:     */     }
/*  517: 901 */     buff.append(this.m_Classifier);
/*  518: 904 */     if ((!this.m_suppressMappingReport) && (this.m_inputHeader != null))
/*  519:     */     {
/*  520:     */       try
/*  521:     */       {
/*  522: 906 */         regenerateMapping();
/*  523:     */       }
/*  524:     */       catch (Exception ex)
/*  525:     */       {
/*  526: 908 */         ex.printStackTrace();
/*  527: 909 */         return "[InputMappedClassifier] Problem loading model.";
/*  528:     */       }
/*  529: 911 */       if (this.m_attributeMap != null) {
/*  530: 912 */         buff.append("\n" + createMappingReport().toString());
/*  531:     */       }
/*  532:     */     }
/*  533: 916 */     return buff.toString();
/*  534:     */   }
/*  535:     */   
/*  536:     */   public int graphType()
/*  537:     */   {
/*  538: 927 */     if ((this.m_Classifier instanceof Drawable)) {
/*  539: 928 */       return ((Drawable)this.m_Classifier).graphType();
/*  540:     */     }
/*  541: 930 */     return 0;
/*  542:     */   }
/*  543:     */   
/*  544:     */   public Enumeration<String> enumerateMeasures()
/*  545:     */   {
/*  546: 941 */     Vector<String> newVector = new Vector();
/*  547: 943 */     if ((this.m_Classifier instanceof AdditionalMeasureProducer))
/*  548:     */     {
/*  549: 944 */       Enumeration<String> en = ((AdditionalMeasureProducer)this.m_Classifier).enumerateMeasures();
/*  550: 946 */       while (en.hasMoreElements())
/*  551:     */       {
/*  552: 947 */         String mname = (String)en.nextElement();
/*  553: 948 */         newVector.addElement(mname);
/*  554:     */       }
/*  555:     */     }
/*  556: 951 */     return newVector.elements();
/*  557:     */   }
/*  558:     */   
/*  559:     */   public double getMeasure(String additionalMeasureName)
/*  560:     */   {
/*  561: 963 */     if ((this.m_Classifier instanceof AdditionalMeasureProducer)) {
/*  562: 964 */       return ((AdditionalMeasureProducer)this.m_Classifier).getMeasure(additionalMeasureName);
/*  563:     */     }
/*  564: 967 */     throw new IllegalArgumentException(additionalMeasureName + " not supported (InputMappedClassifier)");
/*  565:     */   }
/*  566:     */   
/*  567:     */   public String graph()
/*  568:     */     throws Exception
/*  569:     */   {
/*  570: 981 */     if ((this.m_Classifier != null) && ((this.m_Classifier instanceof Drawable))) {
/*  571: 982 */       return ((Drawable)this.m_Classifier).graph();
/*  572:     */     }
/*  573: 984 */     throw new Exception("Classifier: " + getClassifierSpec() + " cannot be graphed");
/*  574:     */   }
/*  575:     */   
/*  576:     */   public String getRevision()
/*  577:     */   {
/*  578: 996 */     return RevisionUtils.extract("$Revision: 10153 $");
/*  579:     */   }
/*  580:     */   
/*  581:     */   public static void main(String[] argv)
/*  582:     */   {
/*  583:1006 */     runClassifier(new InputMappedClassifier(), argv);
/*  584:     */   }
/*  585:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.misc.InputMappedClassifier
 * JD-Core Version:    0.7.0.1
 */