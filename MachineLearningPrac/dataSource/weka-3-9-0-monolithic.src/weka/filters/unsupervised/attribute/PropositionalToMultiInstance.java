/*   1:    */ package weka.filters.unsupervised.attribute;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Random;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.core.Attribute;
/*   8:    */ import weka.core.Capabilities;
/*   9:    */ import weka.core.Capabilities.Capability;
/*  10:    */ import weka.core.DenseInstance;
/*  11:    */ import weka.core.Instance;
/*  12:    */ import weka.core.Instances;
/*  13:    */ import weka.core.Option;
/*  14:    */ import weka.core.OptionHandler;
/*  15:    */ import weka.core.RelationalLocator;
/*  16:    */ import weka.core.RevisionUtils;
/*  17:    */ import weka.core.SingleIndex;
/*  18:    */ import weka.core.SparseInstance;
/*  19:    */ import weka.core.StringLocator;
/*  20:    */ import weka.core.Utils;
/*  21:    */ import weka.filters.Filter;
/*  22:    */ import weka.filters.UnsupervisedFilter;
/*  23:    */ 
/*  24:    */ public class PropositionalToMultiInstance
/*  25:    */   extends Filter
/*  26:    */   implements OptionHandler, UnsupervisedFilter
/*  27:    */ {
/*  28:    */   private static final long serialVersionUID = 5825873573912102482L;
/*  29: 81 */   protected SingleIndex m_BagIndicator = new SingleIndex("first");
/*  30: 84 */   protected boolean m_DoNotWeightBags = false;
/*  31: 87 */   protected int m_Seed = 1;
/*  32: 90 */   protected boolean m_Randomize = false;
/*  33: 93 */   protected StringLocator m_BagStringAtts = null;
/*  34: 96 */   protected RelationalLocator m_BagRelAtts = null;
/*  35:    */   
/*  36:    */   public String globalInfo()
/*  37:    */   {
/*  38:105 */     return "Converts a propositional dataset into a multi-instance dataset (with a relational attribute). When normalizing or standardizing a multi-instance dataset, the MultiInstanceToPropositional filter can be applied first to convert the multi-instance dataset into a propositional instance dataset. After normalization or standardization, we may use this PropositionalToMultiInstance filter to convert the data back to multi-instance format.\n\nNote: the first attribute of the original propositional instance dataset must be a nominal attribute.";
/*  39:    */   }
/*  40:    */   
/*  41:    */   public Enumeration<Option> listOptions()
/*  42:    */   {
/*  43:124 */     Vector<Option> result = new Vector(3);
/*  44:    */     
/*  45:126 */     result.addElement(new Option("\tDo not weight bags by number of instances they contain.\t(default off)", "no-weights", 0, "-no-weights"));
/*  46:    */     
/*  47:    */ 
/*  48:    */ 
/*  49:130 */     result.addElement(new Option("\tThe seed for the randomization of the order of bags.\t(default 1)", "S", 1, "-S <num>"));
/*  50:    */     
/*  51:    */ 
/*  52:    */ 
/*  53:134 */     result.addElement(new Option("\tRandomizes the order of the produced bags after the generation.\t(default off)", "R", 0, "-R"));
/*  54:    */     
/*  55:    */ 
/*  56:    */ 
/*  57:138 */     result.addElement(new Option("\tThe index of the bag ID attribute.\t(default: first)", "B", 1, "-B"));
/*  58:    */     
/*  59:    */ 
/*  60:    */ 
/*  61:142 */     return result.elements();
/*  62:    */   }
/*  63:    */   
/*  64:    */   public void setOptions(String[] options)
/*  65:    */     throws Exception
/*  66:    */   {
/*  67:175 */     setDoNotWeightBags(Utils.getFlag("no-weights", options));
/*  68:    */     
/*  69:177 */     setRandomize(Utils.getFlag('R', options));
/*  70:    */     
/*  71:179 */     String tmpStr = Utils.getOption('S', options);
/*  72:180 */     if (tmpStr.length() != 0) {
/*  73:181 */       setSeed(Integer.parseInt(tmpStr));
/*  74:    */     } else {
/*  75:183 */       setSeed(1);
/*  76:    */     }
/*  77:186 */     tmpStr = Utils.getOption('B', options);
/*  78:187 */     if (tmpStr.length() != 0) {
/*  79:188 */       setBagID(tmpStr);
/*  80:    */     } else {
/*  81:190 */       setBagID("first");
/*  82:    */     }
/*  83:193 */     Utils.checkForRemainingOptions(options);
/*  84:    */   }
/*  85:    */   
/*  86:    */   public String[] getOptions()
/*  87:    */   {
/*  88:204 */     Vector<String> result = new Vector();
/*  89:    */     
/*  90:206 */     result.add("-S");
/*  91:207 */     result.add("" + getSeed());
/*  92:    */     
/*  93:209 */     result.add("-B");
/*  94:210 */     result.add("" + getBagID());
/*  95:212 */     if (this.m_Randomize) {
/*  96:213 */       result.add("-R");
/*  97:    */     }
/*  98:216 */     if (getDoNotWeightBags()) {
/*  99:217 */       result.add("-no-weights");
/* 100:    */     }
/* 101:220 */     return (String[])result.toArray(new String[result.size()]);
/* 102:    */   }
/* 103:    */   
/* 104:    */   public String seedTipText()
/* 105:    */   {
/* 106:230 */     return "The seed used by the random number generator.";
/* 107:    */   }
/* 108:    */   
/* 109:    */   public void setSeed(int value)
/* 110:    */   {
/* 111:239 */     this.m_Seed = value;
/* 112:    */   }
/* 113:    */   
/* 114:    */   public int getSeed()
/* 115:    */   {
/* 116:249 */     return this.m_Seed;
/* 117:    */   }
/* 118:    */   
/* 119:    */   public String bagIDTipText()
/* 120:    */   {
/* 121:259 */     return "The ID of the bag indicator.";
/* 122:    */   }
/* 123:    */   
/* 124:    */   public void setBagID(String value)
/* 125:    */   {
/* 126:268 */     this.m_BagIndicator.setSingleIndex(value);
/* 127:    */   }
/* 128:    */   
/* 129:    */   public String getBagID()
/* 130:    */   {
/* 131:277 */     return this.m_BagIndicator.getSingleIndex();
/* 132:    */   }
/* 133:    */   
/* 134:    */   public String randomizeTipText()
/* 135:    */   {
/* 136:287 */     return "Whether the order of the generated data is randomized.";
/* 137:    */   }
/* 138:    */   
/* 139:    */   public void setRandomize(boolean value)
/* 140:    */   {
/* 141:296 */     this.m_Randomize = value;
/* 142:    */   }
/* 143:    */   
/* 144:    */   public boolean getRandomize()
/* 145:    */   {
/* 146:305 */     return this.m_Randomize;
/* 147:    */   }
/* 148:    */   
/* 149:    */   public String doNotWeightBagsTipText()
/* 150:    */   {
/* 151:315 */     return "Whether the bags are weighted by the number of instances they contain.";
/* 152:    */   }
/* 153:    */   
/* 154:    */   public void setDoNotWeightBags(boolean value)
/* 155:    */   {
/* 156:324 */     this.m_DoNotWeightBags = value;
/* 157:    */   }
/* 158:    */   
/* 159:    */   public boolean getDoNotWeightBags()
/* 160:    */   {
/* 161:333 */     return this.m_DoNotWeightBags;
/* 162:    */   }
/* 163:    */   
/* 164:    */   public Capabilities getCapabilities()
/* 165:    */   {
/* 166:344 */     Capabilities result = super.getCapabilities();
/* 167:345 */     result.disableAll();
/* 168:    */     
/* 169:    */ 
/* 170:348 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/* 171:349 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/* 172:350 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/* 173:351 */     result.enable(Capabilities.Capability.STRING_ATTRIBUTES);
/* 174:352 */     result.enable(Capabilities.Capability.RELATIONAL_ATTRIBUTES);
/* 175:353 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 176:    */     
/* 177:    */ 
/* 178:356 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/* 179:357 */     result.enable(Capabilities.Capability.NUMERIC_CLASS);
/* 180:358 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 181:359 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 182:    */     
/* 183:361 */     return result;
/* 184:    */   }
/* 185:    */   
/* 186:    */   public boolean setInputFormat(Instances instanceInfo)
/* 187:    */     throws Exception
/* 188:    */   {
/* 189:376 */     this.m_BagIndicator.setUpper(instanceInfo.numAttributes() - 1);
/* 190:378 */     if (instanceInfo.attribute(this.m_BagIndicator.getIndex()).type() != 1) {
/* 191:379 */       throw new Exception("The bag ID attribute type of the original propositional instance dataset must be nominal!");
/* 192:    */     }
/* 193:382 */     if (this.m_BagIndicator.getIndex() == instanceInfo.classIndex()) {
/* 194:383 */       throw new Exception("The bag ID cannot be the same as the index of the class attribute!");
/* 195:    */     }
/* 196:386 */     if ((this.m_BagIndicator.getIndex() < 0) || (this.m_BagIndicator.getIndex() > instanceInfo.numAttributes())) {
/* 197:388 */       throw new IllegalArgumentException("Bag index out of range!");
/* 198:    */     }
/* 199:391 */     super.setInputFormat(instanceInfo);
/* 200:    */     
/* 201:    */ 
/* 202:394 */     Instances newData = instanceInfo.stringFreeStructure();
/* 203:395 */     Attribute attBagIndex = (Attribute)newData.attribute(this.m_BagIndicator.getIndex()).copy();
/* 204:396 */     Attribute attClass = null;
/* 205:397 */     if (newData.classIndex() >= 0) {
/* 206:398 */       attClass = (Attribute)newData.classAttribute().copy();
/* 207:    */     }
/* 208:401 */     newData.deleteAttributeAt(this.m_BagIndicator.getIndex());
/* 209:    */     
/* 210:403 */     int classIndex = newData.classIndex();
/* 211:404 */     if (classIndex >= 0)
/* 212:    */     {
/* 213:405 */       newData.setClassIndex(-1);
/* 214:406 */       newData.deleteAttributeAt(classIndex);
/* 215:    */     }
/* 216:409 */     ArrayList<Attribute> attInfo = new ArrayList(3);
/* 217:410 */     attInfo.add(attBagIndex);
/* 218:411 */     attInfo.add(new Attribute("bag", newData));
/* 219:412 */     if (classIndex >= 0) {
/* 220:413 */       attInfo.add(attClass);
/* 221:    */     }
/* 222:415 */     Instances data = new Instances(instanceInfo.relationName(), attInfo, 0);
/* 223:416 */     if (classIndex >= 0) {
/* 224:417 */       data.setClassIndex(data.numAttributes() - 1);
/* 225:    */     }
/* 226:420 */     super.setOutputFormat(data.stringFreeStructure());
/* 227:    */     
/* 228:422 */     this.m_BagStringAtts = new StringLocator(data.attribute(1).relation());
/* 229:423 */     this.m_BagRelAtts = new RelationalLocator(data.attribute(1).relation());
/* 230:    */     
/* 231:425 */     return true;
/* 232:    */   }
/* 233:    */   
/* 234:    */   protected void addBag(Instances input, Instances output, Instances bagInsts, int bagIndex, double classValue, double bagWeight)
/* 235:    */   {
/* 236:442 */     for (int i = 0; i < bagInsts.numInstances(); i++)
/* 237:    */     {
/* 238:443 */       RelationalLocator.copyRelationalValues(bagInsts.instance(i), false, input, this.m_InputRelAtts, bagInsts, this.m_BagRelAtts);
/* 239:    */       
/* 240:    */ 
/* 241:446 */       StringLocator.copyStringValues(bagInsts.instance(i), false, input, this.m_InputStringAtts, bagInsts, this.m_BagStringAtts);
/* 242:    */     }
/* 243:450 */     int value = output.attribute(1).addRelation(bagInsts);
/* 244:451 */     Instance newBag = new DenseInstance(output.numAttributes());
/* 245:452 */     newBag.setValue(0, bagIndex);
/* 246:453 */     if (input.classIndex() >= 0) {
/* 247:454 */       newBag.setValue(2, classValue);
/* 248:    */     }
/* 249:456 */     newBag.setValue(1, value);
/* 250:457 */     if (!this.m_DoNotWeightBags) {
/* 251:458 */       newBag.setWeight(bagWeight);
/* 252:    */     }
/* 253:460 */     newBag.setDataset(output);
/* 254:461 */     output.add(newBag);
/* 255:    */   }
/* 256:    */   
/* 257:    */   protected void push(Instance instance)
/* 258:    */   {
/* 259:472 */     if (instance != null) {
/* 260:473 */       super.push(instance);
/* 261:    */     }
/* 262:    */   }
/* 263:    */   
/* 264:    */   public boolean batchFinished()
/* 265:    */   {
/* 266:489 */     if (getInputFormat() == null) {
/* 267:490 */       throw new IllegalStateException("No input instance format defined");
/* 268:    */     }
/* 269:493 */     Instances input = getInputFormat();
/* 270:494 */     input.sort(this.m_BagIndicator.getIndex());
/* 271:495 */     Instances output = getOutputFormat();
/* 272:496 */     Instances bagInsts = output.attribute(1).relation().stringFreeStructure();
/* 273:    */     
/* 274:498 */     double bagIndex = input.instance(0).value(this.m_BagIndicator.getIndex());
/* 275:499 */     double classValue = -1.0D;
/* 276:500 */     if (input.classIndex() >= 0) {
/* 277:501 */       classValue = input.instance(0).classValue();
/* 278:    */     }
/* 279:503 */     double bagWeight = 0.0D;
/* 280:506 */     for (int i = 0; i < input.numInstances(); i++)
/* 281:    */     {
/* 282:507 */       double currentBagIndex = input.instance(i).value(this.m_BagIndicator.getIndex());
/* 283:    */       
/* 284:    */ 
/* 285:510 */       double[] bagInst = new double[bagInsts.numAttributes()];
/* 286:511 */       Instance inputInst = input.instance(i);
/* 287:512 */       for (int j = 0; j < inputInst.numValues(); j++)
/* 288:    */       {
/* 289:513 */         int index = inputInst.index(j);
/* 290:514 */         if ((index != input.classIndex()) && (index != this.m_BagIndicator.getIndex()))
/* 291:    */         {
/* 292:517 */           if ((input.classIndex() >= 0) && (index > input.classIndex())) {
/* 293:518 */             index--;
/* 294:    */           }
/* 295:520 */           if (index > this.m_BagIndicator.getIndex()) {
/* 296:521 */             index--;
/* 297:    */           }
/* 298:523 */           bagInst[index] = inputInst.valueSparse(j);
/* 299:    */         }
/* 300:    */       }
/* 301:526 */       Instance inst = null;
/* 302:527 */       if ((inputInst instanceof DenseInstance)) {
/* 303:528 */         inst = new DenseInstance(inputInst.weight(), bagInst);
/* 304:    */       } else {
/* 305:530 */         inst = new SparseInstance(inputInst.weight(), bagInst);
/* 306:    */       }
/* 307:532 */       inst.setDataset(bagInsts);
/* 308:535 */       if (currentBagIndex != bagIndex)
/* 309:    */       {
/* 310:538 */         addBag(input, output, bagInsts, (int)bagIndex, classValue, bagWeight);
/* 311:    */         
/* 312:    */ 
/* 313:541 */         bagInsts = bagInsts.stringFreeStructure();
/* 314:542 */         bagWeight = 0.0D;
/* 315:    */         
/* 316:    */ 
/* 317:545 */         bagIndex = currentBagIndex;
/* 318:546 */         if (input.classIndex() >= 0) {
/* 319:547 */           classValue = input.instance(i).classValue();
/* 320:    */         }
/* 321:    */       }
/* 322:552 */       bagInsts.add(inst);
/* 323:553 */       bagWeight += inst.weight();
/* 324:    */     }
/* 325:557 */     addBag(input, output, bagInsts, (int)bagIndex, classValue, bagWeight);
/* 326:559 */     if (getRandomize()) {
/* 327:560 */       output.randomize(new Random(getSeed()));
/* 328:    */     }
/* 329:563 */     for (int i = 0; i < output.numInstances(); i++) {
/* 330:564 */       push(output.instance(i));
/* 331:    */     }
/* 332:568 */     flushInput();
/* 333:    */     
/* 334:570 */     this.m_NewBatch = true;
/* 335:571 */     this.m_FirstBatchDone = true;
/* 336:    */     
/* 337:573 */     return numPendingOutput() != 0;
/* 338:    */   }
/* 339:    */   
/* 340:    */   public String getRevision()
/* 341:    */   {
/* 342:583 */     return RevisionUtils.extract("$Revision: 12135 $");
/* 343:    */   }
/* 344:    */   
/* 345:    */   public static void main(String[] args)
/* 346:    */   {
/* 347:592 */     runFilter(new PropositionalToMultiInstance(), args);
/* 348:    */   }
/* 349:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.PropositionalToMultiInstance
 * JD-Core Version:    0.7.0.1
 */