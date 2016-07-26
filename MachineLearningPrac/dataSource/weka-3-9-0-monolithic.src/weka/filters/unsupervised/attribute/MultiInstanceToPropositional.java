/*   1:    */ package weka.filters.unsupervised.attribute;
/*   2:    */ 
/*   3:    */ import java.util.Enumeration;
/*   4:    */ import java.util.Vector;
/*   5:    */ import weka.core.Attribute;
/*   6:    */ import weka.core.Capabilities;
/*   7:    */ import weka.core.Capabilities.Capability;
/*   8:    */ import weka.core.DenseInstance;
/*   9:    */ import weka.core.Instance;
/*  10:    */ import weka.core.Instances;
/*  11:    */ import weka.core.MultiInstanceCapabilitiesHandler;
/*  12:    */ import weka.core.Option;
/*  13:    */ import weka.core.OptionHandler;
/*  14:    */ import weka.core.RelationalLocator;
/*  15:    */ import weka.core.RevisionUtils;
/*  16:    */ import weka.core.SelectedTag;
/*  17:    */ import weka.core.StringLocator;
/*  18:    */ import weka.core.Tag;
/*  19:    */ import weka.core.Utils;
/*  20:    */ import weka.filters.Filter;
/*  21:    */ import weka.filters.UnsupervisedFilter;
/*  22:    */ 
/*  23:    */ public class MultiInstanceToPropositional
/*  24:    */   extends Filter
/*  25:    */   implements OptionHandler, UnsupervisedFilter, MultiInstanceCapabilitiesHandler
/*  26:    */ {
/*  27:    */   private static final long serialVersionUID = -4102847628883002530L;
/*  28: 85 */   protected int m_NumBags = -1;
/*  29: 88 */   protected StringLocator m_BagStringAtts = null;
/*  30: 91 */   protected RelationalLocator m_BagRelAtts = null;
/*  31:    */   protected int m_NumInstances;
/*  32:    */   public static final int WEIGHTMETHOD_ORIGINAL = 0;
/*  33:    */   public static final int WEIGHTMETHOD_1 = 1;
/*  34:    */   public static final int WEIGHTMETHOD_INVERSE1 = 2;
/*  35:    */   public static final int WEIGHTMETHOD_INVERSE2 = 3;
/*  36:108 */   public static final Tag[] TAGS_WEIGHTMETHOD = { new Tag(0, "keep the weight to be the same as the original value"), new Tag(1, "1.0"), new Tag(2, "1.0 / Total # of prop. instance in the corresp. bag"), new Tag(3, "Total # of prop. instance / (Total # of bags * Total # of prop. instance in the corresp. bag)") };
/*  37:119 */   protected int m_WeightMethod = 3;
/*  38:    */   
/*  39:    */   public Enumeration<Option> listOptions()
/*  40:    */   {
/*  41:128 */     Vector<Option> result = new Vector(1);
/*  42:    */     
/*  43:130 */     result.addElement(new Option("\tThe type of weight setting for each prop. instance:\n\t0.weight = original single bag weight /Total number of\n\tprop. instance in the corresponding bag;\n\t1.weight = 1.0;\n\t2.weight = 1.0/Total number of prop. instance in the \n\t\tcorresponding bag; \n\t3. weight = Total number of prop. instance / (Total number \n\t\tof bags * Total number of prop. instance in the \n\t\tcorresponding bag). \n\t(default:0)", "A", 1, "-A <num>"));
/*  44:    */     
/*  45:    */ 
/*  46:    */ 
/*  47:    */ 
/*  48:    */ 
/*  49:    */ 
/*  50:    */ 
/*  51:    */ 
/*  52:    */ 
/*  53:    */ 
/*  54:141 */     return result.elements();
/*  55:    */   }
/*  56:    */   
/*  57:    */   public void setOptions(String[] options)
/*  58:    */     throws Exception
/*  59:    */   {
/*  60:171 */     String weightString = Utils.getOption('A', options);
/*  61:172 */     if (weightString.length() != 0) {
/*  62:173 */       setWeightMethod(new SelectedTag(Integer.parseInt(weightString), TAGS_WEIGHTMETHOD));
/*  63:    */     } else {
/*  64:176 */       setWeightMethod(new SelectedTag(3, TAGS_WEIGHTMETHOD));
/*  65:    */     }
/*  66:179 */     Utils.checkForRemainingOptions(options);
/*  67:    */   }
/*  68:    */   
/*  69:    */   public String[] getOptions()
/*  70:    */   {
/*  71:190 */     Vector<String> result = new Vector();
/*  72:    */     
/*  73:192 */     result.add("-A");
/*  74:193 */     result.add("" + this.m_WeightMethod);
/*  75:    */     
/*  76:195 */     return (String[])result.toArray(new String[result.size()]);
/*  77:    */   }
/*  78:    */   
/*  79:    */   public String weightMethodTipText()
/*  80:    */   {
/*  81:205 */     return "The method used for weighting the instances.";
/*  82:    */   }
/*  83:    */   
/*  84:    */   public void setWeightMethod(SelectedTag method)
/*  85:    */   {
/*  86:214 */     if (method.getTags() == TAGS_WEIGHTMETHOD) {
/*  87:215 */       this.m_WeightMethod = method.getSelectedTag().getID();
/*  88:    */     }
/*  89:    */   }
/*  90:    */   
/*  91:    */   public SelectedTag getWeightMethod()
/*  92:    */   {
/*  93:225 */     return new SelectedTag(this.m_WeightMethod, TAGS_WEIGHTMETHOD);
/*  94:    */   }
/*  95:    */   
/*  96:    */   public String globalInfo()
/*  97:    */   {
/*  98:236 */     return "Converts the multi-instance dataset into single instance dataset so that the Nominalize, Standardize and other type of filters or transformation  can be applied to these data for the further preprocessing.\nNote: the first attribute of the converted dataset is a nominal attribute and refers to the bagId.";
/*  99:    */   }
/* 100:    */   
/* 101:    */   public Capabilities getCapabilities()
/* 102:    */   {
/* 103:251 */     Capabilities result = super.getCapabilities();
/* 104:252 */     result.disableAll();
/* 105:    */     
/* 106:    */ 
/* 107:255 */     result.disableAllAttributes();
/* 108:256 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/* 109:257 */     result.enable(Capabilities.Capability.RELATIONAL_ATTRIBUTES);
/* 110:258 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 111:    */     
/* 112:    */ 
/* 113:261 */     result.enableAllClasses();
/* 114:262 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 115:    */     
/* 116:    */ 
/* 117:265 */     result.enable(Capabilities.Capability.ONLY_MULTIINSTANCE);
/* 118:    */     
/* 119:267 */     return result;
/* 120:    */   }
/* 121:    */   
/* 122:    */   public Capabilities getMultiInstanceCapabilities()
/* 123:    */   {
/* 124:279 */     Capabilities result = new Capabilities(this);
/* 125:    */     
/* 126:    */ 
/* 127:282 */     result.enableAllAttributes();
/* 128:283 */     result.disable(Capabilities.Capability.RELATIONAL_ATTRIBUTES);
/* 129:284 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 130:    */     
/* 131:    */ 
/* 132:287 */     result.enableAllClasses();
/* 133:288 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 134:289 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 135:    */     
/* 136:    */ 
/* 137:292 */     result.setMinimumNumberInstances(0);
/* 138:    */     
/* 139:294 */     return result;
/* 140:    */   }
/* 141:    */   
/* 142:    */   public boolean setInputFormat(Instances instanceInfo)
/* 143:    */     throws Exception
/* 144:    */   {
/* 145:309 */     if (instanceInfo.attribute(1).type() != 4) {
/* 146:310 */       throw new Exception("Can only handle relational-valued attribute!");
/* 147:    */     }
/* 148:312 */     super.setInputFormat(instanceInfo);
/* 149:    */     
/* 150:314 */     Attribute classAttribute = (Attribute)instanceInfo.classAttribute().copy();
/* 151:315 */     Attribute bagIndex = (Attribute)instanceInfo.attribute(0).copy();
/* 152:    */     
/* 153:    */ 
/* 154:318 */     this.m_NumBags = -1;
/* 155:    */     
/* 156:    */ 
/* 157:321 */     Instances newData = instanceInfo.attribute(1).relation().stringFreeStructure();
/* 158:    */     
/* 159:323 */     newData.insertAttributeAt(bagIndex, 0);
/* 160:324 */     newData.insertAttributeAt(classAttribute, newData.numAttributes());
/* 161:325 */     newData.setClassIndex(newData.numAttributes() - 1);
/* 162:    */     
/* 163:327 */     super.setOutputFormat(newData.stringFreeStructure());
/* 164:    */     
/* 165:329 */     this.m_BagStringAtts = new StringLocator(instanceInfo.attribute(1).relation().stringFreeStructure());
/* 166:    */     
/* 167:331 */     this.m_BagRelAtts = new RelationalLocator(instanceInfo.attribute(1).relation().stringFreeStructure());
/* 168:    */     
/* 169:    */ 
/* 170:334 */     return true;
/* 171:    */   }
/* 172:    */   
/* 173:    */   public boolean input(Instance instance)
/* 174:    */   {
/* 175:348 */     if (getInputFormat() == null) {
/* 176:349 */       throw new IllegalStateException("No input instance format defined");
/* 177:    */     }
/* 178:351 */     if (this.m_NewBatch)
/* 179:    */     {
/* 180:352 */       resetQueue();
/* 181:353 */       this.m_NewBatch = false;
/* 182:    */     }
/* 183:357 */     if (this.m_NumBags != -1)
/* 184:    */     {
/* 185:358 */       convertInstance(instance);
/* 186:359 */       return true;
/* 187:    */     }
/* 188:361 */     bufferInput(instance);
/* 189:362 */     return false;
/* 190:    */   }
/* 191:    */   
/* 192:    */   public boolean batchFinished()
/* 193:    */   {
/* 194:377 */     if (getInputFormat() == null) {
/* 195:378 */       throw new IllegalStateException("No input instance format defined");
/* 196:    */     }
/* 197:382 */     if (this.m_NumBags == -1)
/* 198:    */     {
/* 199:383 */       Instances input = getInputFormat();
/* 200:384 */       this.m_NumBags = input.numInstances();
/* 201:385 */       this.m_NumInstances = 0;
/* 202:386 */       for (int i = 0; i < this.m_NumBags; i++) {
/* 203:387 */         if (input.instance(i).relationalValue(1) == null)
/* 204:    */         {
/* 205:388 */           this.m_NumInstances += 1;
/* 206:    */         }
/* 207:    */         else
/* 208:    */         {
/* 209:390 */           int nn = input.instance(i).relationalValue(1).numInstances();
/* 210:391 */           this.m_NumInstances += nn;
/* 211:    */         }
/* 212:    */       }
/* 213:396 */       for (int i = 0; i < input.numInstances(); i++) {
/* 214:397 */         convertInstance(input.instance(i));
/* 215:    */       }
/* 216:401 */       flushInput();
/* 217:    */     }
/* 218:404 */     this.m_NewBatch = true;
/* 219:405 */     return numPendingOutput() != 0;
/* 220:    */   }
/* 221:    */   
/* 222:    */   private void convertInstance(Instance bag)
/* 223:    */   {
/* 224:416 */     Instances data = bag.relationalValue(1);
/* 225:417 */     int bagSize = 1;
/* 226:418 */     if (data != null) {
/* 227:419 */       bagSize = data.numInstances();
/* 228:    */     }
/* 229:421 */     double bagIndex = bag.value(0);
/* 230:422 */     double classValue = bag.classValue();
/* 231:423 */     double weight = 0.0D;
/* 232:425 */     if (this.m_WeightMethod == 1) {
/* 233:426 */       weight = 1.0D;
/* 234:427 */     } else if (this.m_WeightMethod == 2) {
/* 235:428 */       weight = 1.0D / bagSize;
/* 236:429 */     } else if (this.m_WeightMethod == 3) {
/* 237:430 */       weight = this.m_NumInstances / (this.m_NumBags * bagSize);
/* 238:    */     } else {
/* 239:432 */       weight = bag.weight() / bagSize;
/* 240:    */     }
/* 241:436 */     Instances outputFormat = getOutputFormat().stringFreeStructure();
/* 242:438 */     for (int i = 0; i < bagSize; i++)
/* 243:    */     {
/* 244:439 */       Instance newInst = new DenseInstance(outputFormat.numAttributes());
/* 245:440 */       newInst.setDataset(outputFormat);
/* 246:441 */       newInst.setValue(0, bagIndex);
/* 247:442 */       if (!bag.classIsMissing()) {
/* 248:443 */         newInst.setClassValue(classValue);
/* 249:    */       }
/* 250:446 */       for (int j = 1; j < outputFormat.numAttributes() - 1; j++) {
/* 251:447 */         if (data == null) {
/* 252:448 */           newInst.setMissing(j);
/* 253:    */         } else {
/* 254:450 */           newInst.setValue(j, data.instance(i).value(j - 1));
/* 255:    */         }
/* 256:    */       }
/* 257:454 */       newInst.setWeight(weight);
/* 258:    */       
/* 259:    */ 
/* 260:457 */       StringLocator.copyStringValues(newInst, false, data, this.m_BagStringAtts, outputFormat, this.m_OutputStringAtts);
/* 261:    */       
/* 262:    */ 
/* 263:460 */       RelationalLocator.copyRelationalValues(newInst, false, data, this.m_BagRelAtts, outputFormat, this.m_OutputRelAtts);
/* 264:    */       
/* 265:    */ 
/* 266:463 */       push(newInst);
/* 267:    */     }
/* 268:    */   }
/* 269:    */   
/* 270:    */   public String getRevision()
/* 271:    */   {
/* 272:474 */     return RevisionUtils.extract("$Revision: 12118 $");
/* 273:    */   }
/* 274:    */   
/* 275:    */   public static void main(String[] args)
/* 276:    */   {
/* 277:483 */     runFilter(new MultiInstanceToPropositional(), args);
/* 278:    */   }
/* 279:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.MultiInstanceToPropositional
 * JD-Core Version:    0.7.0.1
 */