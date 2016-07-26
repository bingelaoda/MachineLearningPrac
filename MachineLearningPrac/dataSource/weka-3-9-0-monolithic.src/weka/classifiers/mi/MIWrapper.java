/*   1:    */ package weka.classifiers.mi;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.classifiers.Classifier;
/*   8:    */ import weka.classifiers.SingleClassifierEnhancer;
/*   9:    */ import weka.core.Capabilities;
/*  10:    */ import weka.core.Capabilities.Capability;
/*  11:    */ import weka.core.Instance;
/*  12:    */ import weka.core.Instances;
/*  13:    */ import weka.core.MultiInstanceCapabilitiesHandler;
/*  14:    */ import weka.core.Option;
/*  15:    */ import weka.core.OptionHandler;
/*  16:    */ import weka.core.RevisionUtils;
/*  17:    */ import weka.core.SelectedTag;
/*  18:    */ import weka.core.Tag;
/*  19:    */ import weka.core.TechnicalInformation;
/*  20:    */ import weka.core.TechnicalInformation.Field;
/*  21:    */ import weka.core.TechnicalInformation.Type;
/*  22:    */ import weka.core.TechnicalInformationHandler;
/*  23:    */ import weka.core.Utils;
/*  24:    */ import weka.filters.Filter;
/*  25:    */ import weka.filters.unsupervised.attribute.MultiInstanceToPropositional;
/*  26:    */ 
/*  27:    */ public class MIWrapper
/*  28:    */   extends SingleClassifierEnhancer
/*  29:    */   implements MultiInstanceCapabilitiesHandler, OptionHandler, TechnicalInformationHandler
/*  30:    */ {
/*  31:    */   static final long serialVersionUID = -7707766152904315910L;
/*  32:    */   protected int m_NumClasses;
/*  33:    */   public static final int TESTMETHOD_ARITHMETIC = 1;
/*  34:    */   public static final int TESTMETHOD_GEOMETRIC = 2;
/*  35:    */   public static final int TESTMETHOD_MAXPROB = 3;
/*  36:143 */   public static final Tag[] TAGS_TESTMETHOD = { new Tag(1, "arithmetic average"), new Tag(2, "geometric average"), new Tag(3, "max probability of positive bag") };
/*  37:149 */   protected int m_Method = 2;
/*  38:152 */   protected MultiInstanceToPropositional m_ConvertToProp = new MultiInstanceToPropositional();
/*  39:155 */   protected int m_WeightMethod = 3;
/*  40:    */   
/*  41:    */   public String globalInfo()
/*  42:    */   {
/*  43:164 */     return "A simple Wrapper method for applying standard propositional learners to multi-instance data.\n\nFor more information see:\n\n" + getTechnicalInformation().toString();
/*  44:    */   }
/*  45:    */   
/*  46:    */   public TechnicalInformation getTechnicalInformation()
/*  47:    */   {
/*  48:181 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.TECHREPORT);
/*  49:182 */     result.setValue(TechnicalInformation.Field.AUTHOR, "E. T. Frank and X. Xu");
/*  50:183 */     result.setValue(TechnicalInformation.Field.TITLE, "Applying propositional learning algorithms to multi-instance data");
/*  51:    */     
/*  52:185 */     result.setValue(TechnicalInformation.Field.YEAR, "2003");
/*  53:186 */     result.setValue(TechnicalInformation.Field.MONTH, "06");
/*  54:187 */     result.setValue(TechnicalInformation.Field.INSTITUTION, "University of Waikato");
/*  55:188 */     result.setValue(TechnicalInformation.Field.ADDRESS, "Department of Computer Science, University of Waikato, Hamilton, NZ");
/*  56:    */     
/*  57:    */ 
/*  58:191 */     return result;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public Enumeration<Option> listOptions()
/*  62:    */   {
/*  63:202 */     Vector<Option> result = new Vector();
/*  64:    */     
/*  65:204 */     result.addElement(new Option("\tThe method used in testing:\n\t1.arithmetic average\n\t2.geometric average\n\t3.max probability of positive bag.\n\t(default: 1)", "P", 1, "-P [1|2|3]"));
/*  66:    */     
/*  67:    */ 
/*  68:    */ 
/*  69:    */ 
/*  70:209 */     result.addElement(new Option("\tThe type of weight setting for each single-instance:\n\t0.keep the weight to be the same as the original value;\n\t1.weight = 1.0\n\t2.weight = 1.0/Total number of single-instance in the\n\t\tcorresponding bag\n\t3. weight = Total number of single-instance / (Total\n\t\tnumber of bags * Total number of single-instance \n\t\tin the corresponding bag).\n\t(default: 3)", "A", 1, "-A [0|1|2|3]"));
/*  71:    */     
/*  72:    */ 
/*  73:    */ 
/*  74:    */ 
/*  75:    */ 
/*  76:    */ 
/*  77:    */ 
/*  78:    */ 
/*  79:    */ 
/*  80:    */ 
/*  81:220 */     result.addAll(Collections.list(super.listOptions()));
/*  82:    */     
/*  83:222 */     return result.elements();
/*  84:    */   }
/*  85:    */   
/*  86:    */   public void setOptions(String[] options)
/*  87:    */     throws Exception
/*  88:    */   {
/*  89:272 */     String methodString = Utils.getOption('P', options);
/*  90:273 */     if (methodString.length() != 0) {
/*  91:274 */       setMethod(new SelectedTag(Integer.parseInt(methodString), TAGS_TESTMETHOD));
/*  92:    */     } else {
/*  93:276 */       setMethod(new SelectedTag(1, TAGS_TESTMETHOD));
/*  94:    */     }
/*  95:279 */     String weightString = Utils.getOption('A', options);
/*  96:280 */     if (weightString.length() != 0) {
/*  97:281 */       setWeightMethod(new SelectedTag(Integer.parseInt(weightString), MultiInstanceToPropositional.TAGS_WEIGHTMETHOD));
/*  98:    */     } else {
/*  99:284 */       setWeightMethod(new SelectedTag(3, MultiInstanceToPropositional.TAGS_WEIGHTMETHOD));
/* 100:    */     }
/* 101:289 */     super.setOptions(options);
/* 102:    */     
/* 103:291 */     Utils.checkForRemainingOptions(options);
/* 104:    */   }
/* 105:    */   
/* 106:    */   public String[] getOptions()
/* 107:    */   {
/* 108:302 */     Vector<String> result = new Vector();
/* 109:    */     
/* 110:304 */     result.add("-P");
/* 111:305 */     result.add("" + this.m_Method);
/* 112:    */     
/* 113:307 */     result.add("-A");
/* 114:308 */     result.add("" + this.m_WeightMethod);
/* 115:    */     
/* 116:310 */     Collections.addAll(result, super.getOptions());
/* 117:    */     
/* 118:312 */     return (String[])result.toArray(new String[result.size()]);
/* 119:    */   }
/* 120:    */   
/* 121:    */   public String weightMethodTipText()
/* 122:    */   {
/* 123:322 */     return "The method used for weighting the instances.";
/* 124:    */   }
/* 125:    */   
/* 126:    */   public void setWeightMethod(SelectedTag method)
/* 127:    */   {
/* 128:331 */     if (method.getTags() == MultiInstanceToPropositional.TAGS_WEIGHTMETHOD) {
/* 129:332 */       this.m_WeightMethod = method.getSelectedTag().getID();
/* 130:    */     }
/* 131:    */   }
/* 132:    */   
/* 133:    */   public SelectedTag getWeightMethod()
/* 134:    */   {
/* 135:342 */     return new SelectedTag(this.m_WeightMethod, MultiInstanceToPropositional.TAGS_WEIGHTMETHOD);
/* 136:    */   }
/* 137:    */   
/* 138:    */   public String methodTipText()
/* 139:    */   {
/* 140:353 */     return "The method used for testing.";
/* 141:    */   }
/* 142:    */   
/* 143:    */   public void setMethod(SelectedTag method)
/* 144:    */   {
/* 145:362 */     if (method.getTags() == TAGS_TESTMETHOD) {
/* 146:363 */       this.m_Method = method.getSelectedTag().getID();
/* 147:    */     }
/* 148:    */   }
/* 149:    */   
/* 150:    */   public SelectedTag getMethod()
/* 151:    */   {
/* 152:373 */     return new SelectedTag(this.m_Method, TAGS_TESTMETHOD);
/* 153:    */   }
/* 154:    */   
/* 155:    */   public Capabilities getCapabilities()
/* 156:    */   {
/* 157:383 */     Capabilities result = super.getCapabilities();
/* 158:    */     
/* 159:    */ 
/* 160:386 */     result.disableAllClasses();
/* 161:387 */     result.disableAllClassDependencies();
/* 162:388 */     if (super.getCapabilities().handles(Capabilities.Capability.NOMINAL_CLASS)) {
/* 163:389 */       result.enable(Capabilities.Capability.NOMINAL_CLASS);
/* 164:    */     }
/* 165:391 */     if (super.getCapabilities().handles(Capabilities.Capability.BINARY_CLASS)) {
/* 166:392 */       result.enable(Capabilities.Capability.BINARY_CLASS);
/* 167:    */     }
/* 168:394 */     result.enable(Capabilities.Capability.RELATIONAL_ATTRIBUTES);
/* 169:395 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 170:396 */     result.disable(Capabilities.Capability.MISSING_VALUES);
/* 171:    */     
/* 172:    */ 
/* 173:399 */     result.enable(Capabilities.Capability.ONLY_MULTIINSTANCE);
/* 174:    */     
/* 175:401 */     return result;
/* 176:    */   }
/* 177:    */   
/* 178:    */   public Capabilities getMultiInstanceCapabilities()
/* 179:    */   {
/* 180:413 */     Capabilities result = super.getCapabilities();
/* 181:    */     
/* 182:    */ 
/* 183:416 */     result.disableAllClasses();
/* 184:417 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 185:    */     
/* 186:419 */     return result;
/* 187:    */   }
/* 188:    */   
/* 189:    */   public void buildClassifier(Instances data)
/* 190:    */     throws Exception
/* 191:    */   {
/* 192:433 */     getCapabilities().testWithFail(data);
/* 193:    */     
/* 194:    */ 
/* 195:436 */     Instances train = new Instances(data);
/* 196:437 */     train.deleteWithMissingClass();
/* 197:439 */     if (this.m_Classifier == null) {
/* 198:440 */       throw new Exception("A base classifier has not been specified!");
/* 199:    */     }
/* 200:443 */     if (getDebug()) {
/* 201:444 */       System.out.println("Start training ...");
/* 202:    */     }
/* 203:446 */     this.m_NumClasses = train.numClasses();
/* 204:    */     
/* 205:    */ 
/* 206:449 */     this.m_ConvertToProp.setWeightMethod(getWeightMethod());
/* 207:450 */     this.m_ConvertToProp.setInputFormat(train);
/* 208:451 */     train = Filter.useFilter(train, this.m_ConvertToProp);
/* 209:452 */     train.deleteAttributeAt(0);
/* 210:    */     
/* 211:454 */     this.m_Classifier.buildClassifier(train);
/* 212:    */   }
/* 213:    */   
/* 214:    */   public double[] distributionForInstance(Instance exmp)
/* 215:    */     throws Exception
/* 216:    */   {
/* 217:467 */     Instances testData = new Instances(exmp.dataset(), 0);
/* 218:468 */     testData.add(exmp);
/* 219:    */     
/* 220:    */ 
/* 221:471 */     this.m_ConvertToProp.setWeightMethod(new SelectedTag(0, MultiInstanceToPropositional.TAGS_WEIGHTMETHOD));
/* 222:    */     
/* 223:    */ 
/* 224:474 */     testData = Filter.useFilter(testData, this.m_ConvertToProp);
/* 225:475 */     testData.deleteAttributeAt(0);
/* 226:    */     
/* 227:    */ 
/* 228:478 */     double[] distribution = new double[this.m_NumClasses];
/* 229:479 */     double nI = testData.numInstances();
/* 230:480 */     double[] maxPr = new double[this.m_NumClasses];
/* 231:482 */     for (int i = 0; i < nI; i++)
/* 232:    */     {
/* 233:483 */       double[] dist = this.m_Classifier.distributionForInstance(testData.instance(i));
/* 234:485 */       for (int j = 0; j < this.m_NumClasses; j++) {
/* 235:487 */         switch (this.m_Method)
/* 236:    */         {
/* 237:    */         case 1: 
/* 238:489 */           distribution[j] += dist[j] / nI;
/* 239:490 */           break;
/* 240:    */         case 2: 
/* 241:493 */           if (dist[j] < 0.001D) {
/* 242:494 */             dist[j] = 0.001D;
/* 243:495 */           } else if (dist[j] > 0.999D) {
/* 244:496 */             dist[j] = 0.999D;
/* 245:    */           }
/* 246:499 */           distribution[j] += Math.log(dist[j]) / nI;
/* 247:500 */           break;
/* 248:    */         case 3: 
/* 249:502 */           if (dist[j] > maxPr[j]) {
/* 250:503 */             maxPr[j] = dist[j];
/* 251:    */           }
/* 252:    */           break;
/* 253:    */         }
/* 254:    */       }
/* 255:    */     }
/* 256:510 */     if (this.m_Method == 2) {
/* 257:511 */       for (int j = 0; j < this.m_NumClasses; j++) {
/* 258:512 */         distribution[j] = Math.exp(distribution[j]);
/* 259:    */       }
/* 260:    */     }
/* 261:516 */     if (this.m_Method == 3)
/* 262:    */     {
/* 263:517 */       distribution[1] = maxPr[1];
/* 264:518 */       distribution[0] = (1.0D - distribution[1]);
/* 265:    */     }
/* 266:521 */     if (Utils.eq(Utils.sum(distribution), 0.0D)) {
/* 267:522 */       for (int i = 0; i < distribution.length; i++) {
/* 268:523 */         distribution[i] = (1.0D / distribution.length);
/* 269:    */       }
/* 270:    */     } else {
/* 271:526 */       Utils.normalize(distribution);
/* 272:    */     }
/* 273:529 */     return distribution;
/* 274:    */   }
/* 275:    */   
/* 276:    */   public String toString()
/* 277:    */   {
/* 278:539 */     return "MIWrapper with base classifier: \n" + this.m_Classifier.toString();
/* 279:    */   }
/* 280:    */   
/* 281:    */   public String getRevision()
/* 282:    */   {
/* 283:549 */     return RevisionUtils.extract("$Revision: 10369 $");
/* 284:    */   }
/* 285:    */   
/* 286:    */   public static void main(String[] argv)
/* 287:    */   {
/* 288:559 */     runClassifier(new MIWrapper(), argv);
/* 289:    */   }
/* 290:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.mi.MIWrapper
 * JD-Core Version:    0.7.0.1
 */