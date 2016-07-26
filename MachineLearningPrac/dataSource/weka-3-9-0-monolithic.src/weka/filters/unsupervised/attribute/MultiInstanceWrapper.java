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
/*  14:    */ import weka.core.RevisionUtils;
/*  15:    */ import weka.core.Utils;
/*  16:    */ import weka.filters.Filter;
/*  17:    */ import weka.filters.MultiFilter;
/*  18:    */ import weka.filters.supervised.attribute.PartitionMembership;
/*  19:    */ 
/*  20:    */ public class MultiInstanceWrapper
/*  21:    */   extends Filter
/*  22:    */   implements OptionHandler, MultiInstanceCapabilitiesHandler
/*  23:    */ {
/*  24:    */   private static final long serialVersionUID = -3232591375578585231L;
/*  25: 76 */   protected Filter m_Filter = new PartitionMembership();
/*  26: 79 */   protected MultiInstanceToPropositional m_MItoP = null;
/*  27: 82 */   protected MultiFilter m_MF = null;
/*  28: 88 */   protected boolean m_UseAverage = false;
/*  29:    */   
/*  30:    */   public String globalInfo()
/*  31:    */   {
/*  32: 98 */     return "Applies a single-instance filter to multi-instance data by converting each bag to a collection of instances, using the filter MultiInstanceToPropositional with default parameters, where each instance is labeled with its bag's class label. Aggregates resulting data using sum/mode. The resulting data can be processed by a single-instance classifier.";
/*  33:    */   }
/*  34:    */   
/*  35:    */   public Enumeration<Option> listOptions()
/*  36:    */   {
/*  37:112 */     Vector<Option> result = new Vector();
/*  38:    */     
/*  39:114 */     result.addElement(new Option("\tThe single-instance filter to use, including all arguments.\n\t(default: weka.filters.unsupervised.attribute.PartitionMembership)", "F", 1, "-F <filter name and options>"));
/*  40:    */     
/*  41:    */ 
/*  42:    */ 
/*  43:    */ 
/*  44:    */ 
/*  45:120 */     result.addElement(new Option("\tUse average of numeric attribute values across bag instead of sum.\n", "A", 0, "-A"));
/*  46:    */     
/*  47:    */ 
/*  48:    */ 
/*  49:124 */     return result.elements();
/*  50:    */   }
/*  51:    */   
/*  52:    */   public void setOptions(String[] options)
/*  53:    */     throws Exception
/*  54:    */   {
/*  55:153 */     String filterString = Utils.getOption('F', options);
/*  56:154 */     if (filterString.length() > 0)
/*  57:    */     {
/*  58:155 */       String[] filterSpec = Utils.splitOptions(filterString);
/*  59:156 */       if (filterSpec.length == 0) {
/*  60:157 */         throw new IllegalArgumentException("Invalid filter specification string");
/*  61:    */       }
/*  62:160 */       String filterName = filterSpec[0];
/*  63:161 */       filterSpec[0] = "";
/*  64:162 */       setFilter((Filter)Utils.forName(Filter.class, filterName, filterSpec));
/*  65:    */     }
/*  66:    */     else
/*  67:    */     {
/*  68:164 */       setFilter(new PartitionMembership());
/*  69:    */     }
/*  70:167 */     setUseAverage(Utils.getFlag('A', options));
/*  71:    */     
/*  72:169 */     Utils.checkForRemainingOptions(options);
/*  73:    */   }
/*  74:    */   
/*  75:    */   public String[] getOptions()
/*  76:    */   {
/*  77:180 */     Vector<String> options = new Vector();
/*  78:    */     
/*  79:182 */     options.add("-F");
/*  80:183 */     options.add("" + getFilterSpec());
/*  81:185 */     if (getUseAverage()) {
/*  82:186 */       options.add("-A");
/*  83:    */     }
/*  84:188 */     return (String[])options.toArray(new String[0]);
/*  85:    */   }
/*  86:    */   
/*  87:    */   public String filterTipText()
/*  88:    */   {
/*  89:199 */     return "The single-instance filter to be used.";
/*  90:    */   }
/*  91:    */   
/*  92:    */   public void setFilter(Filter filter)
/*  93:    */   {
/*  94:209 */     this.m_Filter = filter;
/*  95:    */   }
/*  96:    */   
/*  97:    */   public Filter getFilter()
/*  98:    */   {
/*  99:219 */     return this.m_Filter;
/* 100:    */   }
/* 101:    */   
/* 102:    */   protected String getFilterSpec()
/* 103:    */   {
/* 104:230 */     Filter c = getFilter();
/* 105:231 */     if ((c instanceof OptionHandler)) {
/* 106:232 */       return c.getClass().getName() + " " + Utils.joinOptions(c.getOptions());
/* 107:    */     }
/* 108:235 */     return c.getClass().getName();
/* 109:    */   }
/* 110:    */   
/* 111:    */   public String useAverageTipText()
/* 112:    */   {
/* 113:246 */     return "If true, average of numeric attribute values across bag is used instead of sum.";
/* 114:    */   }
/* 115:    */   
/* 116:    */   public void setUseAverage(boolean useAverage)
/* 117:    */   {
/* 118:256 */     this.m_UseAverage = useAverage;
/* 119:    */   }
/* 120:    */   
/* 121:    */   public boolean getUseAverage()
/* 122:    */   {
/* 123:266 */     return this.m_UseAverage;
/* 124:    */   }
/* 125:    */   
/* 126:    */   public Capabilities getCapabilities()
/* 127:    */   {
/* 128:278 */     Capabilities result = super.getCapabilities();
/* 129:279 */     result.disableAll();
/* 130:    */     
/* 131:    */ 
/* 132:282 */     result.disableAllAttributes();
/* 133:283 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/* 134:284 */     result.enable(Capabilities.Capability.RELATIONAL_ATTRIBUTES);
/* 135:285 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 136:    */     
/* 137:    */ 
/* 138:288 */     result.enableAllClasses();
/* 139:289 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 140:    */     
/* 141:    */ 
/* 142:292 */     result.enable(Capabilities.Capability.ONLY_MULTIINSTANCE);
/* 143:    */     
/* 144:294 */     return result;
/* 145:    */   }
/* 146:    */   
/* 147:    */   public Capabilities getMultiInstanceCapabilities()
/* 148:    */   {
/* 149:306 */     Capabilities result = this.m_Filter.getCapabilities();
/* 150:    */     
/* 151:    */ 
/* 152:309 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 153:    */     
/* 154:    */ 
/* 155:312 */     result.setMinimumNumberInstances(0);
/* 156:    */     
/* 157:314 */     return result;
/* 158:    */   }
/* 159:    */   
/* 160:    */   public boolean setInputFormat(Instances instanceInfo)
/* 161:    */     throws Exception
/* 162:    */   {
/* 163:329 */     super.setInputFormat(instanceInfo);
/* 164:    */     
/* 165:331 */     this.m_MItoP = null;
/* 166:332 */     this.m_MF = null;
/* 167:    */     
/* 168:334 */     return false;
/* 169:    */   }
/* 170:    */   
/* 171:    */   public boolean input(Instance instance)
/* 172:    */     throws Exception
/* 173:    */   {
/* 174:348 */     if (getInputFormat() == null) {
/* 175:349 */       throw new IllegalStateException("No input instance format defined");
/* 176:    */     }
/* 177:351 */     if (this.m_NewBatch)
/* 178:    */     {
/* 179:352 */       resetQueue();
/* 180:353 */       this.m_NewBatch = false;
/* 181:    */     }
/* 182:356 */     if (this.m_MItoP != null)
/* 183:    */     {
/* 184:357 */       bufferInput(instance);
/* 185:358 */       Instances SIdata = Filter.useFilter(getInputFormat(), this.m_MItoP);
/* 186:359 */       flushInput();
/* 187:    */       
/* 188:    */ 
/* 189:362 */       Instances convertedInstances = this.m_MF.getOutputFormat();
/* 190:363 */       Instances tempInstances = new Instances(convertedInstances, convertedInstances.numInstances());
/* 191:365 */       for (Instance inst : SIdata)
/* 192:    */       {
/* 193:366 */         this.m_MF.input(inst);
/* 194:367 */         tempInstances.add(this.m_MF.output());
/* 195:    */       }
/* 196:370 */       double[] newVals = new double[tempInstances.numAttributes() + 1];
/* 197:371 */       newVals[0] = instance.value(0);
/* 198:372 */       for (int i = 1; i < newVals.length; i++) {
/* 199:373 */         if (i - 1 == tempInstances.classIndex()) {
/* 200:374 */           newVals[i] = instance.classValue();
/* 201:376 */         } else if ((!getUseAverage()) && (tempInstances.attribute(i - 1).isNumeric())) {
/* 202:377 */           for (Instance tempInst : tempInstances) {
/* 203:378 */             newVals[i] += tempInst.value(i - 1);
/* 204:    */           }
/* 205:    */         } else {
/* 206:381 */           newVals[i] = tempInstances.meanOrMode(i - 1);
/* 207:    */         }
/* 208:    */       }
/* 209:385 */       DenseInstance insT = new DenseInstance(instance.weight(), newVals);
/* 210:386 */       push(insT);
/* 211:387 */       return true;
/* 212:    */     }
/* 213:390 */     bufferInput(instance);
/* 214:391 */     return false;
/* 215:    */   }
/* 216:    */   
/* 217:    */   public boolean batchFinished()
/* 218:    */     throws Exception
/* 219:    */   {
/* 220:405 */     if (getInputFormat() == null) {
/* 221:406 */       throw new IllegalStateException("No input instance format defined");
/* 222:    */     }
/* 223:408 */     if (this.m_MItoP == null)
/* 224:    */     {
/* 225:411 */       this.m_MItoP = new MultiInstanceToPropositional();
/* 226:412 */       this.m_MItoP.setInputFormat(getInputFormat());
/* 227:413 */       Instances SIdata = Filter.useFilter(getInputFormat(), this.m_MItoP);
/* 228:    */       
/* 229:    */ 
/* 230:416 */       this.m_MF = new MultiFilter();
/* 231:417 */       Filter[] twoFilters = new Filter[2];
/* 232:418 */       twoFilters[0] = new Remove();
/* 233:419 */       ((Remove)twoFilters[0]).setAttributeIndices("1");
/* 234:420 */       twoFilters[1] = this.m_Filter;
/* 235:421 */       this.m_MF.setFilters(twoFilters);
/* 236:422 */       this.m_MF.setInputFormat(SIdata);
/* 237:424 */       for (Instance inst : SIdata) {
/* 238:425 */         this.m_MF.input(inst);
/* 239:    */       }
/* 240:427 */       this.m_MF.batchFinished();
/* 241:    */       
/* 242:429 */       Instances convertedInstances = this.m_MF.getOutputFormat();
/* 243:430 */       Instances tempInstances = new Instances(convertedInstances, convertedInstances.numInstances());
/* 244:    */       
/* 245:432 */       convertedInstances.insertAttributeAt((Attribute)getInputFormat().attribute(0).copy(), 0);
/* 246:    */       
/* 247:434 */       setOutputFormat(convertedInstances);
/* 248:    */       
/* 249:436 */       int origInstanceIndex = 0;
/* 250:437 */       int counter = 0;
/* 251:438 */       for (int index = 0; index < SIdata.numInstances(); index++)
/* 252:    */       {
/* 253:441 */         tempInstances.add(this.m_MF.output());
/* 254:    */         
/* 255:    */ 
/* 256:444 */         int bagSize = 1;
/* 257:445 */         Instances relation = getInputFormat().instance(origInstanceIndex).relationalValue(1);
/* 258:447 */         if (relation != null) {
/* 259:448 */           bagSize = relation.numInstances();
/* 260:    */         }
/* 261:452 */         counter++;
/* 262:452 */         if (counter == bagSize)
/* 263:    */         {
/* 264:453 */           double[] newVals = new double[convertedInstances.numAttributes()];
/* 265:454 */           newVals[0] = getInputFormat().instance(origInstanceIndex).value(0);
/* 266:455 */           for (int i = 1; i < newVals.length; i++) {
/* 267:456 */             if (i - 1 == tempInstances.classIndex()) {
/* 268:457 */               newVals[i] = tempInstances.instance(0).classValue();
/* 269:462 */             } else if ((!getUseAverage()) && (tempInstances.attribute(i - 1).isNumeric())) {
/* 270:464 */               for (Instance tempInst : tempInstances) {
/* 271:465 */                 newVals[i] += tempInst.value(i - 1);
/* 272:    */               }
/* 273:    */             } else {
/* 274:468 */               newVals[i] = tempInstances.meanOrMode(i - 1);
/* 275:    */             }
/* 276:    */           }
/* 277:473 */           DenseInstance insT = new DenseInstance(getInputFormat().instance(origInstanceIndex++).weight(), newVals);
/* 278:    */           
/* 279:475 */           insT.setDataset(convertedInstances);
/* 280:476 */           push(insT);
/* 281:477 */           tempInstances.delete();
/* 282:478 */           counter = 0;
/* 283:    */         }
/* 284:    */       }
/* 285:    */     }
/* 286:483 */     flushInput();
/* 287:    */     
/* 288:485 */     this.m_NewBatch = true;
/* 289:486 */     return numPendingOutput() != 0;
/* 290:    */   }
/* 291:    */   
/* 292:    */   public String getRevision()
/* 293:    */   {
/* 294:497 */     return RevisionUtils.extract("$Revision: 12118 $");
/* 295:    */   }
/* 296:    */   
/* 297:    */   public static void main(String[] args)
/* 298:    */   {
/* 299:507 */     runFilter(new MultiInstanceWrapper(), args);
/* 300:    */   }
/* 301:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.MultiInstanceWrapper
 * JD-Core Version:    0.7.0.1
 */