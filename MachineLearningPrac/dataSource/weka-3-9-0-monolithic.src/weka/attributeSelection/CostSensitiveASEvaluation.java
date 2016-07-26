/*   1:    */ package weka.attributeSelection;
/*   2:    */ 
/*   3:    */ import java.io.BufferedReader;
/*   4:    */ import java.io.File;
/*   5:    */ import java.io.FileReader;
/*   6:    */ import java.io.Serializable;
/*   7:    */ import java.io.StringReader;
/*   8:    */ import java.io.StringWriter;
/*   9:    */ import java.util.ArrayList;
/*  10:    */ import java.util.Collections;
/*  11:    */ import java.util.Enumeration;
/*  12:    */ import java.util.Random;
/*  13:    */ import java.util.Vector;
/*  14:    */ import weka.classifiers.CostMatrix;
/*  15:    */ import weka.core.Capabilities;
/*  16:    */ import weka.core.Capabilities.Capability;
/*  17:    */ import weka.core.Instances;
/*  18:    */ import weka.core.Option;
/*  19:    */ import weka.core.OptionHandler;
/*  20:    */ import weka.core.RevisionUtils;
/*  21:    */ import weka.core.SelectedTag;
/*  22:    */ import weka.core.Tag;
/*  23:    */ import weka.core.Utils;
/*  24:    */ import weka.core.WeightedInstancesHandler;
/*  25:    */ 
/*  26:    */ public abstract class CostSensitiveASEvaluation
/*  27:    */   extends ASEvaluation
/*  28:    */   implements OptionHandler, Serializable
/*  29:    */ {
/*  30:    */   static final long serialVersionUID = -7045833833363396977L;
/*  31:    */   public static final int MATRIX_ON_DEMAND = 1;
/*  32:    */   public static final int MATRIX_SUPPLIED = 2;
/*  33: 65 */   public static final Tag[] TAGS_MATRIX_SOURCE = { new Tag(1, "Load cost matrix on demand"), new Tag(2, "Use explicit cost matrix") };
/*  34: 70 */   protected int m_MatrixSource = 1;
/*  35: 76 */   protected File m_OnDemandDirectory = new File(System.getProperty("user.dir"));
/*  36:    */   protected String m_CostFile;
/*  37: 82 */   protected CostMatrix m_CostMatrix = new CostMatrix(1);
/*  38:    */   protected ASEvaluation m_evaluator;
/*  39: 88 */   protected int m_seed = 1;
/*  40:    */   
/*  41:    */   public Enumeration<Option> listOptions()
/*  42:    */   {
/*  43: 98 */     Vector<Option> newVector = new Vector(5);
/*  44:    */     
/*  45:100 */     newVector.addElement(new Option("\tFile name of a cost matrix to use. If this is not supplied,\n\ta cost matrix will be loaded on demand. The name of the\n\ton-demand file is the relation name of the training data\n\tplus \".cost\", and the path to the on-demand file is\n\tspecified with the -N option.", "C", 1, "-C <cost file name>"));
/*  46:    */     
/*  47:    */ 
/*  48:    */ 
/*  49:    */ 
/*  50:    */ 
/*  51:106 */     newVector.addElement(new Option("\tName of a directory to search for cost files when loading\n\tcosts on demand (default current directory).", "N", 1, "-N <directory>"));
/*  52:    */     
/*  53:    */ 
/*  54:    */ 
/*  55:110 */     newVector.addElement(new Option("\tThe cost matrix in Matlab single line format.", "cost-matrix", 1, "-cost-matrix <matrix>"));
/*  56:    */     
/*  57:    */ 
/*  58:113 */     newVector.addElement(new Option("\tThe seed to use for random number generation.", "S", 1, "-S <integer>"));
/*  59:    */     
/*  60:    */ 
/*  61:    */ 
/*  62:117 */     newVector.addElement(new Option("\tFull name of base evaluator. Options after -- are passed to the evaluator.\n\t(default: " + defaultEvaluatorString() + ")", "W", 1, "-W"));
/*  63:122 */     if ((this.m_evaluator instanceof OptionHandler))
/*  64:    */     {
/*  65:123 */       newVector.addElement(new Option("", "", 0, "\nOptions specific to evaluator " + this.m_evaluator.getClass().getName() + ":"));
/*  66:    */       
/*  67:    */ 
/*  68:    */ 
/*  69:127 */       newVector.addAll(Collections.list(((OptionHandler)this.m_evaluator).listOptions()));
/*  70:    */     }
/*  71:131 */     return newVector.elements();
/*  72:    */   }
/*  73:    */   
/*  74:    */   public void setOptions(String[] options)
/*  75:    */     throws Exception
/*  76:    */   {
/*  77:180 */     String costFile = Utils.getOption('C', options);
/*  78:181 */     if (costFile.length() != 0)
/*  79:    */     {
/*  80:    */       try
/*  81:    */       {
/*  82:183 */         setCostMatrix(new CostMatrix(new BufferedReader(new FileReader(costFile))));
/*  83:    */       }
/*  84:    */       catch (Exception ex)
/*  85:    */       {
/*  86:188 */         setCostMatrix(null);
/*  87:    */       }
/*  88:190 */       setCostMatrixSource(new SelectedTag(2, TAGS_MATRIX_SOURCE));
/*  89:191 */       this.m_CostFile = costFile;
/*  90:    */     }
/*  91:    */     else
/*  92:    */     {
/*  93:193 */       setCostMatrixSource(new SelectedTag(1, TAGS_MATRIX_SOURCE));
/*  94:    */     }
/*  95:196 */     String demandDir = Utils.getOption('N', options);
/*  96:197 */     if (demandDir.length() != 0) {
/*  97:198 */       setOnDemandDirectory(new File(demandDir));
/*  98:    */     }
/*  99:201 */     String cost_matrix = Utils.getOption("cost-matrix", options);
/* 100:202 */     if (cost_matrix.length() != 0)
/* 101:    */     {
/* 102:203 */       StringWriter writer = new StringWriter();
/* 103:204 */       CostMatrix.parseMatlab(cost_matrix).write(writer);
/* 104:205 */       setCostMatrix(new CostMatrix(new StringReader(writer.toString())));
/* 105:206 */       setCostMatrixSource(new SelectedTag(2, TAGS_MATRIX_SOURCE));
/* 106:    */     }
/* 107:209 */     String seed = Utils.getOption('S', options);
/* 108:210 */     if (seed.length() != 0) {
/* 109:211 */       setSeed(Integer.parseInt(seed));
/* 110:    */     } else {
/* 111:213 */       setSeed(1);
/* 112:    */     }
/* 113:216 */     String evaluatorName = Utils.getOption('W', options);
/* 114:218 */     if (evaluatorName.length() > 0)
/* 115:    */     {
/* 116:219 */       setEvaluator(ASEvaluation.forName(evaluatorName, null));
/* 117:220 */       setEvaluator(ASEvaluation.forName(evaluatorName, Utils.partitionOptions(options)));
/* 118:    */     }
/* 119:    */     else
/* 120:    */     {
/* 121:223 */       setEvaluator(ASEvaluation.forName(defaultEvaluatorString(), null));
/* 122:224 */       setEvaluator(ASEvaluation.forName(defaultEvaluatorString(), Utils.partitionOptions(options)));
/* 123:    */     }
/* 124:228 */     Utils.checkForRemainingOptions(options);
/* 125:    */   }
/* 126:    */   
/* 127:    */   public String[] getOptions()
/* 128:    */   {
/* 129:238 */     ArrayList<String> options = new ArrayList();
/* 130:240 */     if (this.m_MatrixSource == 2)
/* 131:    */     {
/* 132:241 */       if (this.m_CostFile != null)
/* 133:    */       {
/* 134:242 */         options.add("-C");
/* 135:243 */         options.add("" + this.m_CostFile);
/* 136:    */       }
/* 137:    */       else
/* 138:    */       {
/* 139:245 */         options.add("-cost-matrix");
/* 140:246 */         options.add(getCostMatrix().toMatlab());
/* 141:    */       }
/* 142:    */     }
/* 143:    */     else
/* 144:    */     {
/* 145:249 */       options.add("-N");
/* 146:250 */       options.add("" + getOnDemandDirectory());
/* 147:    */     }
/* 148:253 */     options.add("-S");
/* 149:254 */     options.add("" + getSeed());
/* 150:    */     
/* 151:256 */     options.add("-W");
/* 152:257 */     options.add(this.m_evaluator.getClass().getName());
/* 153:259 */     if ((this.m_evaluator instanceof OptionHandler))
/* 154:    */     {
/* 155:260 */       String[] evaluatorOptions = ((OptionHandler)this.m_evaluator).getOptions();
/* 156:261 */       if (evaluatorOptions.length > 0)
/* 157:    */       {
/* 158:262 */         options.add("--");
/* 159:263 */         Collections.addAll(options, evaluatorOptions);
/* 160:    */       }
/* 161:    */     }
/* 162:267 */     return (String[])options.toArray(new String[0]);
/* 163:    */   }
/* 164:    */   
/* 165:    */   public String globalInfo()
/* 166:    */   {
/* 167:276 */     return "A meta subset evaluator that makes its base subset evaluator cost-sensitive. ";
/* 168:    */   }
/* 169:    */   
/* 170:    */   public String defaultEvaluatorString()
/* 171:    */   {
/* 172:285 */     return "weka.attributeSelection.CfsSubsetEval";
/* 173:    */   }
/* 174:    */   
/* 175:    */   public String costMatrixSourceTipText()
/* 176:    */   {
/* 177:294 */     return "Sets where to get the cost matrix. The two options areto use the supplied explicit cost matrix (the setting of the costMatrix property), or to load a cost matrix from a file when required (this file will be loaded from the directory set by the onDemandDirectory property and will be named relation_name" + CostMatrix.FILE_EXTENSION + ").";
/* 178:    */   }
/* 179:    */   
/* 180:    */   public SelectedTag getCostMatrixSource()
/* 181:    */   {
/* 182:310 */     return new SelectedTag(this.m_MatrixSource, TAGS_MATRIX_SOURCE);
/* 183:    */   }
/* 184:    */   
/* 185:    */   public void setCostMatrixSource(SelectedTag newMethod)
/* 186:    */   {
/* 187:321 */     if (newMethod.getTags() == TAGS_MATRIX_SOURCE) {
/* 188:322 */       this.m_MatrixSource = newMethod.getSelectedTag().getID();
/* 189:    */     }
/* 190:    */   }
/* 191:    */   
/* 192:    */   public String onDemandDirectoryTipText()
/* 193:    */   {
/* 194:332 */     return "Sets the directory where cost files are loaded from. This option is used when the costMatrixSource is set to \"On Demand\".";
/* 195:    */   }
/* 196:    */   
/* 197:    */   public File getOnDemandDirectory()
/* 198:    */   {
/* 199:344 */     return this.m_OnDemandDirectory;
/* 200:    */   }
/* 201:    */   
/* 202:    */   public void setOnDemandDirectory(File newDir)
/* 203:    */   {
/* 204:355 */     if (newDir.isDirectory()) {
/* 205:356 */       this.m_OnDemandDirectory = newDir;
/* 206:    */     } else {
/* 207:358 */       this.m_OnDemandDirectory = new File(newDir.getParent());
/* 208:    */     }
/* 209:360 */     this.m_MatrixSource = 1;
/* 210:    */   }
/* 211:    */   
/* 212:    */   protected String getEvaluatorSpec()
/* 213:    */   {
/* 214:371 */     ASEvaluation ase = getEvaluator();
/* 215:372 */     if ((ase instanceof OptionHandler)) {
/* 216:373 */       return ase.getClass().getName() + " " + Utils.joinOptions(((OptionHandler)ase).getOptions());
/* 217:    */     }
/* 218:376 */     return ase.getClass().getName();
/* 219:    */   }
/* 220:    */   
/* 221:    */   public String costMatrixTipText()
/* 222:    */   {
/* 223:384 */     return "Sets the cost matrix explicitly. This matrix is used if the costMatrixSource property is set to \"Supplied\".";
/* 224:    */   }
/* 225:    */   
/* 226:    */   public CostMatrix getCostMatrix()
/* 227:    */   {
/* 228:395 */     return this.m_CostMatrix;
/* 229:    */   }
/* 230:    */   
/* 231:    */   public void setCostMatrix(CostMatrix newCostMatrix)
/* 232:    */   {
/* 233:405 */     this.m_CostMatrix = newCostMatrix;
/* 234:406 */     this.m_MatrixSource = 2;
/* 235:    */   }
/* 236:    */   
/* 237:    */   public String seedTipText()
/* 238:    */   {
/* 239:416 */     return "The random number seed to be used.";
/* 240:    */   }
/* 241:    */   
/* 242:    */   public void setSeed(int seed)
/* 243:    */   {
/* 244:426 */     this.m_seed = seed;
/* 245:    */   }
/* 246:    */   
/* 247:    */   public int getSeed()
/* 248:    */   {
/* 249:436 */     return this.m_seed;
/* 250:    */   }
/* 251:    */   
/* 252:    */   public String evaluatorTipText()
/* 253:    */   {
/* 254:446 */     return "The base evaluator to be used.";
/* 255:    */   }
/* 256:    */   
/* 257:    */   public void setEvaluator(ASEvaluation newEvaluator)
/* 258:    */     throws IllegalArgumentException
/* 259:    */   {
/* 260:458 */     this.m_evaluator = newEvaluator;
/* 261:    */   }
/* 262:    */   
/* 263:    */   public ASEvaluation getEvaluator()
/* 264:    */   {
/* 265:468 */     return this.m_evaluator;
/* 266:    */   }
/* 267:    */   
/* 268:    */   public Capabilities getCapabilities()
/* 269:    */   {
/* 270:    */     Capabilities result;
/* 271:    */     Capabilities result;
/* 272:480 */     if (getEvaluator() != null)
/* 273:    */     {
/* 274:481 */       result = getEvaluator().getCapabilities();
/* 275:    */     }
/* 276:    */     else
/* 277:    */     {
/* 278:483 */       result = new Capabilities(this);
/* 279:484 */       result.disableAll();
/* 280:    */     }
/* 281:488 */     result.disableAllClasses();
/* 282:489 */     result.disableAllClassDependencies();
/* 283:490 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/* 284:    */     
/* 285:492 */     return result;
/* 286:    */   }
/* 287:    */   
/* 288:    */   public void buildEvaluator(Instances data)
/* 289:    */     throws Exception
/* 290:    */   {
/* 291:505 */     getCapabilities().testWithFail(data);
/* 292:    */     
/* 293:    */ 
/* 294:508 */     data = new Instances(data);
/* 295:509 */     data.deleteWithMissingClass();
/* 296:511 */     if (this.m_evaluator == null) {
/* 297:512 */       throw new Exception("No base evaluator has been set!");
/* 298:    */     }
/* 299:515 */     if (this.m_MatrixSource == 1)
/* 300:    */     {
/* 301:516 */       String costName = data.relationName() + CostMatrix.FILE_EXTENSION;
/* 302:517 */       File costFile = new File(getOnDemandDirectory(), costName);
/* 303:518 */       if (!costFile.exists()) {
/* 304:519 */         throw new Exception("On-demand cost file doesn't exist: " + costFile);
/* 305:    */       }
/* 306:521 */       setCostMatrix(new CostMatrix(new BufferedReader(new FileReader(costFile))));
/* 307:    */     }
/* 308:522 */     else if (this.m_CostMatrix == null)
/* 309:    */     {
/* 310:524 */       this.m_CostMatrix = new CostMatrix(data.numClasses());
/* 311:525 */       this.m_CostMatrix.readOldFormat(new BufferedReader(new FileReader(this.m_CostFile)));
/* 312:    */     }
/* 313:529 */     Random random = null;
/* 314:530 */     if (!(this.m_evaluator instanceof WeightedInstancesHandler)) {
/* 315:531 */       random = new Random(this.m_seed);
/* 316:    */     }
/* 317:533 */     data = this.m_CostMatrix.applyCostMatrix(data, random);
/* 318:534 */     this.m_evaluator.buildEvaluator(data);
/* 319:    */   }
/* 320:    */   
/* 321:    */   public int[] postProcess(int[] attributeSet)
/* 322:    */     throws Exception
/* 323:    */   {
/* 324:547 */     return this.m_evaluator.postProcess(attributeSet);
/* 325:    */   }
/* 326:    */   
/* 327:    */   public String toString()
/* 328:    */   {
/* 329:558 */     if (this.m_evaluator == null) {
/* 330:559 */       return "CostSensitiveASEvaluation: No model built yet.";
/* 331:    */     }
/* 332:562 */     String result = (this.m_evaluator instanceof AttributeEvaluator) ? "CostSensitiveAttributeEval using " : "CostSensitiveSubsetEval using ";
/* 333:    */     
/* 334:    */ 
/* 335:565 */     result = result + "\n\n" + getEvaluatorSpec() + "\n\nEvaluator\n" + this.m_evaluator.toString() + "\n\nCost Matrix\n" + this.m_CostMatrix.toString();
/* 336:    */     
/* 337:    */ 
/* 338:568 */     return result;
/* 339:    */   }
/* 340:    */   
/* 341:    */   public String getRevision()
/* 342:    */   {
/* 343:578 */     return RevisionUtils.extract("$Revision: 10337 $");
/* 344:    */   }
/* 345:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.attributeSelection.CostSensitiveASEvaluation
 * JD-Core Version:    0.7.0.1
 */