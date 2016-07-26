/*   1:    */ package weka.classifiers.meta;
/*   2:    */ 
/*   3:    */ import java.io.BufferedReader;
/*   4:    */ import java.io.File;
/*   5:    */ import java.io.FileReader;
/*   6:    */ import java.io.StringReader;
/*   7:    */ import java.io.StringWriter;
/*   8:    */ import java.util.Collections;
/*   9:    */ import java.util.Enumeration;
/*  10:    */ import java.util.Vector;
/*  11:    */ import weka.classifiers.Classifier;
/*  12:    */ import weka.classifiers.CostMatrix;
/*  13:    */ import weka.classifiers.RandomizableSingleClassifierEnhancer;
/*  14:    */ import weka.core.Capabilities;
/*  15:    */ import weka.core.Capabilities.Capability;
/*  16:    */ import weka.core.Instance;
/*  17:    */ import weka.core.Instances;
/*  18:    */ import weka.core.Option;
/*  19:    */ import weka.core.OptionHandler;
/*  20:    */ import weka.core.RevisionUtils;
/*  21:    */ import weka.core.SelectedTag;
/*  22:    */ import weka.core.Tag;
/*  23:    */ import weka.core.TechnicalInformation;
/*  24:    */ import weka.core.TechnicalInformation.Field;
/*  25:    */ import weka.core.TechnicalInformation.Type;
/*  26:    */ import weka.core.TechnicalInformationHandler;
/*  27:    */ import weka.core.Utils;
/*  28:    */ 
/*  29:    */ public class MetaCost
/*  30:    */   extends RandomizableSingleClassifierEnhancer
/*  31:    */   implements TechnicalInformationHandler
/*  32:    */ {
/*  33:    */   static final long serialVersionUID = 1205317833344726855L;
/*  34:    */   public static final int MATRIX_ON_DEMAND = 1;
/*  35:    */   public static final int MATRIX_SUPPLIED = 2;
/*  36:167 */   public static final Tag[] TAGS_MATRIX_SOURCE = { new Tag(1, "Load cost matrix on demand"), new Tag(2, "Use explicit cost matrix") };
/*  37:172 */   protected int m_MatrixSource = 1;
/*  38:178 */   protected File m_OnDemandDirectory = new File(System.getProperty("user.dir"));
/*  39:    */   protected String m_CostFile;
/*  40:184 */   protected CostMatrix m_CostMatrix = new CostMatrix(1);
/*  41:187 */   protected int m_NumIterations = 10;
/*  42:190 */   protected int m_BagSizePercent = 100;
/*  43:    */   
/*  44:    */   public String globalInfo()
/*  45:    */   {
/*  46:200 */     return "This metaclassifier makes its base classifier cost-sensitive using the method specified in\n\n" + getTechnicalInformation().toString() + "\n\n" + "This classifier should produce similar results to one created by " + "passing the base learner to Bagging, which is in turn passed to a " + "CostSensitiveClassifier operating on minimum expected cost. The difference " + "is that MetaCost produces a single cost-sensitive classifier of the " + "base learner, giving the benefits of fast classification and interpretable " + "output (if the base learner itself is interpretable). This implementation  " + "uses all bagging iterations when reclassifying training data (the MetaCost " + "paper reports a marginal improvement when only those iterations containing " + "each training instance are used in reclassifying that instance).";
/*  47:    */   }
/*  48:    */   
/*  49:    */   public TechnicalInformation getTechnicalInformation()
/*  50:    */   {
/*  51:227 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*  52:228 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Pedro Domingos");
/*  53:229 */     result.setValue(TechnicalInformation.Field.TITLE, "MetaCost: A general method for making classifiers cost-sensitive");
/*  54:    */     
/*  55:231 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "Fifth International Conference on Knowledge Discovery and Data Mining");
/*  56:    */     
/*  57:233 */     result.setValue(TechnicalInformation.Field.YEAR, "1999");
/*  58:234 */     result.setValue(TechnicalInformation.Field.PAGES, "155-164");
/*  59:    */     
/*  60:236 */     return result;
/*  61:    */   }
/*  62:    */   
/*  63:    */   public Enumeration<Option> listOptions()
/*  64:    */   {
/*  65:247 */     Vector<Option> newVector = new Vector(5);
/*  66:    */     
/*  67:249 */     newVector.addElement(new Option("\tNumber of bagging iterations.\n\t(default 10)", "I", 1, "-I <num>"));
/*  68:    */     
/*  69:251 */     newVector.addElement(new Option("\tFile name of a cost matrix to use. If this is not supplied,\n\ta cost matrix will be loaded on demand. The name of the\n\ton-demand file is the relation name of the training data\n\tplus \".cost\", and the path to the on-demand file is\n\tspecified with the -N option.", "C", 1, "-C <cost file name>"));
/*  70:    */     
/*  71:    */ 
/*  72:    */ 
/*  73:    */ 
/*  74:    */ 
/*  75:257 */     newVector.addElement(new Option("\tName of a directory to search for cost files when loading\n\tcosts on demand (default current directory).", "N", 1, "-N <directory>"));
/*  76:    */     
/*  77:    */ 
/*  78:    */ 
/*  79:261 */     newVector.addElement(new Option("\tThe cost matrix in Matlab single line format.", "cost-matrix", 1, "-cost-matrix <matrix>"));
/*  80:    */     
/*  81:    */ 
/*  82:264 */     newVector.addElement(new Option("\tSize of each bag, as a percentage of the\n\ttraining set size. (default 100)", "P", 1, "-P"));
/*  83:    */     
/*  84:    */ 
/*  85:    */ 
/*  86:268 */     newVector.addAll(Collections.list(super.listOptions()));
/*  87:    */     
/*  88:270 */     return newVector.elements();
/*  89:    */   }
/*  90:    */   
/*  91:    */   public void setOptions(String[] options)
/*  92:    */     throws Exception
/*  93:    */   {
/*  94:351 */     String bagIterations = Utils.getOption('I', options);
/*  95:352 */     if (bagIterations.length() != 0) {
/*  96:353 */       setNumIterations(Integer.parseInt(bagIterations));
/*  97:    */     } else {
/*  98:355 */       setNumIterations(10);
/*  99:    */     }
/* 100:358 */     String bagSize = Utils.getOption('P', options);
/* 101:359 */     if (bagSize.length() != 0) {
/* 102:360 */       setBagSizePercent(Integer.parseInt(bagSize));
/* 103:    */     } else {
/* 104:362 */       setBagSizePercent(100);
/* 105:    */     }
/* 106:365 */     String costFile = Utils.getOption('C', options);
/* 107:366 */     if (costFile.length() != 0)
/* 108:    */     {
/* 109:367 */       setCostMatrix(new CostMatrix(new BufferedReader(new FileReader(costFile))));
/* 110:368 */       setCostMatrixSource(new SelectedTag(2, TAGS_MATRIX_SOURCE));
/* 111:369 */       this.m_CostFile = costFile;
/* 112:    */     }
/* 113:    */     else
/* 114:    */     {
/* 115:371 */       setCostMatrixSource(new SelectedTag(1, TAGS_MATRIX_SOURCE));
/* 116:    */     }
/* 117:374 */     String demandDir = Utils.getOption('N', options);
/* 118:375 */     if (demandDir.length() != 0) {
/* 119:376 */       setOnDemandDirectory(new File(demandDir));
/* 120:    */     }
/* 121:379 */     String cost_matrix = Utils.getOption("cost-matrix", options);
/* 122:380 */     if (cost_matrix.length() != 0)
/* 123:    */     {
/* 124:381 */       StringWriter writer = new StringWriter();
/* 125:382 */       CostMatrix.parseMatlab(cost_matrix).write(writer);
/* 126:383 */       setCostMatrix(new CostMatrix(new StringReader(writer.toString())));
/* 127:384 */       setCostMatrixSource(new SelectedTag(2, TAGS_MATRIX_SOURCE));
/* 128:    */     }
/* 129:387 */     super.setOptions(options);
/* 130:    */     
/* 131:389 */     Utils.checkForRemainingOptions(options);
/* 132:    */   }
/* 133:    */   
/* 134:    */   public String[] getOptions()
/* 135:    */   {
/* 136:400 */     Vector<String> options = new Vector();
/* 137:402 */     if (this.m_MatrixSource == 2)
/* 138:    */     {
/* 139:403 */       if (this.m_CostFile != null)
/* 140:    */       {
/* 141:404 */         options.add("-C");
/* 142:405 */         options.add("" + this.m_CostFile);
/* 143:    */       }
/* 144:    */       else
/* 145:    */       {
/* 146:407 */         options.add("-cost-matrix");
/* 147:408 */         options.add(getCostMatrix().toMatlab());
/* 148:    */       }
/* 149:    */     }
/* 150:    */     else
/* 151:    */     {
/* 152:411 */       options.add("-N");
/* 153:412 */       options.add("" + getOnDemandDirectory());
/* 154:    */     }
/* 155:414 */     options.add("-I");
/* 156:415 */     options.add("" + getNumIterations());
/* 157:416 */     options.add("-P");
/* 158:417 */     options.add("" + getBagSizePercent());
/* 159:    */     
/* 160:419 */     Collections.addAll(options, super.getOptions());
/* 161:    */     
/* 162:421 */     return (String[])options.toArray(new String[0]);
/* 163:    */   }
/* 164:    */   
/* 165:    */   public String costMatrixSourceTipText()
/* 166:    */   {
/* 167:431 */     return "Gets the source location method of the cost matrix. Will be one of MATRIX_ON_DEMAND or MATRIX_SUPPLIED.";
/* 168:    */   }
/* 169:    */   
/* 170:    */   public SelectedTag getCostMatrixSource()
/* 171:    */   {
/* 172:443 */     return new SelectedTag(this.m_MatrixSource, TAGS_MATRIX_SOURCE);
/* 173:    */   }
/* 174:    */   
/* 175:    */   public void setCostMatrixSource(SelectedTag newMethod)
/* 176:    */   {
/* 177:454 */     if (newMethod.getTags() == TAGS_MATRIX_SOURCE) {
/* 178:455 */       this.m_MatrixSource = newMethod.getSelectedTag().getID();
/* 179:    */     }
/* 180:    */   }
/* 181:    */   
/* 182:    */   public String onDemandDirectoryTipText()
/* 183:    */   {
/* 184:466 */     return "Name of directory to search for cost files when loading costs on demand.";
/* 185:    */   }
/* 186:    */   
/* 187:    */   public File getOnDemandDirectory()
/* 188:    */   {
/* 189:478 */     return this.m_OnDemandDirectory;
/* 190:    */   }
/* 191:    */   
/* 192:    */   public void setOnDemandDirectory(File newDir)
/* 193:    */   {
/* 194:489 */     if (newDir.isDirectory()) {
/* 195:490 */       this.m_OnDemandDirectory = newDir;
/* 196:    */     } else {
/* 197:492 */       this.m_OnDemandDirectory = new File(newDir.getParent());
/* 198:    */     }
/* 199:494 */     this.m_MatrixSource = 1;
/* 200:    */   }
/* 201:    */   
/* 202:    */   public String bagSizePercentTipText()
/* 203:    */   {
/* 204:504 */     return "The size of each bag, as a percentage of the training set size.";
/* 205:    */   }
/* 206:    */   
/* 207:    */   public int getBagSizePercent()
/* 208:    */   {
/* 209:515 */     return this.m_BagSizePercent;
/* 210:    */   }
/* 211:    */   
/* 212:    */   public void setBagSizePercent(int newBagSizePercent)
/* 213:    */   {
/* 214:525 */     this.m_BagSizePercent = newBagSizePercent;
/* 215:    */   }
/* 216:    */   
/* 217:    */   public String numIterationsTipText()
/* 218:    */   {
/* 219:535 */     return "The number of bagging iterations.";
/* 220:    */   }
/* 221:    */   
/* 222:    */   public void setNumIterations(int numIterations)
/* 223:    */   {
/* 224:545 */     this.m_NumIterations = numIterations;
/* 225:    */   }
/* 226:    */   
/* 227:    */   public int getNumIterations()
/* 228:    */   {
/* 229:555 */     return this.m_NumIterations;
/* 230:    */   }
/* 231:    */   
/* 232:    */   public String costMatrixTipText()
/* 233:    */   {
/* 234:565 */     return "A misclassification cost matrix.";
/* 235:    */   }
/* 236:    */   
/* 237:    */   public CostMatrix getCostMatrix()
/* 238:    */   {
/* 239:575 */     return this.m_CostMatrix;
/* 240:    */   }
/* 241:    */   
/* 242:    */   public void setCostMatrix(CostMatrix newCostMatrix)
/* 243:    */   {
/* 244:585 */     this.m_CostMatrix = newCostMatrix;
/* 245:586 */     this.m_MatrixSource = 2;
/* 246:    */   }
/* 247:    */   
/* 248:    */   public Capabilities getCapabilities()
/* 249:    */   {
/* 250:596 */     Capabilities result = super.getCapabilities();
/* 251:    */     
/* 252:    */ 
/* 253:599 */     result.disableAllClasses();
/* 254:600 */     result.disableAllClassDependencies();
/* 255:601 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/* 256:    */     
/* 257:603 */     return result;
/* 258:    */   }
/* 259:    */   
/* 260:    */   public void buildClassifier(Instances data)
/* 261:    */     throws Exception
/* 262:    */   {
/* 263:616 */     getCapabilities().testWithFail(data);
/* 264:    */     
/* 265:    */ 
/* 266:619 */     data = new Instances(data);
/* 267:620 */     data.deleteWithMissingClass();
/* 268:622 */     if (this.m_MatrixSource == 1)
/* 269:    */     {
/* 270:623 */       String costName = data.relationName() + CostMatrix.FILE_EXTENSION;
/* 271:624 */       File costFile = new File(getOnDemandDirectory(), costName);
/* 272:625 */       if (!costFile.exists()) {
/* 273:626 */         throw new Exception("On-demand cost file doesn't exist: " + costFile);
/* 274:    */       }
/* 275:628 */       setCostMatrix(new CostMatrix(new BufferedReader(new FileReader(costFile))));
/* 276:    */     }
/* 277:632 */     Bagging bagger = new Bagging();
/* 278:633 */     bagger.setClassifier(getClassifier());
/* 279:634 */     bagger.setSeed(getSeed());
/* 280:635 */     bagger.setNumIterations(getNumIterations());
/* 281:636 */     bagger.setBagSizePercent(getBagSizePercent());
/* 282:637 */     bagger.buildClassifier(data);
/* 283:    */     
/* 284:    */ 
/* 285:    */ 
/* 286:641 */     Instances newData = new Instances(data);
/* 287:642 */     for (int i = 0; i < newData.numInstances(); i++)
/* 288:    */     {
/* 289:643 */       Instance current = newData.instance(i);
/* 290:644 */       double[] pred = bagger.distributionForInstance(current);
/* 291:645 */       int minCostPred = Utils.minIndex(this.m_CostMatrix.expectedCosts(pred));
/* 292:646 */       current.setClassValue(minCostPred);
/* 293:    */     }
/* 294:650 */     this.m_Classifier.buildClassifier(newData);
/* 295:    */   }
/* 296:    */   
/* 297:    */   public double[] distributionForInstance(Instance instance)
/* 298:    */     throws Exception
/* 299:    */   {
/* 300:662 */     return this.m_Classifier.distributionForInstance(instance);
/* 301:    */   }
/* 302:    */   
/* 303:    */   protected String getClassifierSpec()
/* 304:    */   {
/* 305:674 */     Classifier c = getClassifier();
/* 306:675 */     return c.getClass().getName() + " " + Utils.joinOptions(((OptionHandler)c).getOptions());
/* 307:    */   }
/* 308:    */   
/* 309:    */   public String toString()
/* 310:    */   {
/* 311:687 */     if (this.m_Classifier == null) {
/* 312:688 */       return "MetaCost: No model built yet.";
/* 313:    */     }
/* 314:691 */     String result = "MetaCost cost sensitive classifier induction";
/* 315:692 */     result = result + "\nOptions: " + Utils.joinOptions(getOptions());
/* 316:693 */     result = result + "\nBase learner: " + getClassifierSpec() + "\n\nClassifier Model\n" + this.m_Classifier.toString() + "\n\nCost Matrix\n" + this.m_CostMatrix.toString();
/* 317:    */     
/* 318:    */ 
/* 319:    */ 
/* 320:697 */     return result;
/* 321:    */   }
/* 322:    */   
/* 323:    */   public String getRevision()
/* 324:    */   {
/* 325:707 */     return RevisionUtils.extract("$Revision: 10366 $");
/* 326:    */   }
/* 327:    */   
/* 328:    */   public static void main(String[] argv)
/* 329:    */   {
/* 330:717 */     runClassifier(new MetaCost(), argv);
/* 331:    */   }
/* 332:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.meta.MetaCost
 * JD-Core Version:    0.7.0.1
 */