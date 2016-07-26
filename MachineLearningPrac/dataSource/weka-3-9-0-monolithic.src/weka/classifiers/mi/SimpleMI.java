/*   1:    */ package weka.classifiers.mi;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.classifiers.Classifier;
/*   8:    */ import weka.classifiers.SingleClassifierEnhancer;
/*   9:    */ import weka.core.Attribute;
/*  10:    */ import weka.core.Capabilities;
/*  11:    */ import weka.core.Capabilities.Capability;
/*  12:    */ import weka.core.DenseInstance;
/*  13:    */ import weka.core.Instance;
/*  14:    */ import weka.core.Instances;
/*  15:    */ import weka.core.MultiInstanceCapabilitiesHandler;
/*  16:    */ import weka.core.Option;
/*  17:    */ import weka.core.OptionHandler;
/*  18:    */ import weka.core.RevisionUtils;
/*  19:    */ import weka.core.SelectedTag;
/*  20:    */ import weka.core.Tag;
/*  21:    */ import weka.core.Utils;
/*  22:    */ 
/*  23:    */ public class SimpleMI
/*  24:    */   extends SingleClassifierEnhancer
/*  25:    */   implements OptionHandler, MultiInstanceCapabilitiesHandler
/*  26:    */ {
/*  27:    */   static final long serialVersionUID = 9137795893666592662L;
/*  28:    */   public static final int TRANSFORMMETHOD_ARITHMETIC = 1;
/*  29:    */   public static final int TRANSFORMMETHOD_GEOMETRIC = 2;
/*  30:    */   public static final int TRANSFORMMETHOD_MINIMAX = 3;
/*  31:106 */   public static final Tag[] TAGS_TRANSFORMMETHOD = { new Tag(1, "arithmetic average"), new Tag(2, "geometric average"), new Tag(3, "using minimax combined features of a bag") };
/*  32:112 */   protected int m_TransformMethod = 1;
/*  33:    */   
/*  34:    */   public String globalInfo()
/*  35:    */   {
/*  36:121 */     return "Reduces MI data into mono-instance data.";
/*  37:    */   }
/*  38:    */   
/*  39:    */   public Enumeration<Option> listOptions()
/*  40:    */   {
/*  41:132 */     Vector<Option> result = new Vector();
/*  42:    */     
/*  43:134 */     result.addElement(new Option("\tThe method used in transformation:\n\t1.arithmatic average; 2.geometric centor;\n\t3.using minimax combined features of a bag (default: 1)\n\n\tMethod 3:\n\tDefine s to be the vector of the coordinate-wise maxima\n\tand minima of X, ie., \n\ts(X)=(minx1, ..., minxm, maxx1, ...,maxxm), transform\n\tthe exemplars into mono-instance which contains attributes\n\ts(X)", "M", 1, "-M [1|2|3]"));
/*  44:    */     
/*  45:    */ 
/*  46:    */ 
/*  47:    */ 
/*  48:    */ 
/*  49:    */ 
/*  50:    */ 
/*  51:    */ 
/*  52:    */ 
/*  53:144 */     result.addAll(Collections.list(super.listOptions()));
/*  54:    */     
/*  55:146 */     return result.elements();
/*  56:    */   }
/*  57:    */   
/*  58:    */   public void setOptions(String[] options)
/*  59:    */     throws Exception
/*  60:    */   {
/*  61:188 */     String methodString = Utils.getOption('M', options);
/*  62:189 */     if (methodString.length() != 0) {
/*  63:190 */       setTransformMethod(new SelectedTag(Integer.parseInt(methodString), TAGS_TRANSFORMMETHOD));
/*  64:    */     } else {
/*  65:193 */       setTransformMethod(new SelectedTag(1, TAGS_TRANSFORMMETHOD));
/*  66:    */     }
/*  67:197 */     super.setOptions(options);
/*  68:    */     
/*  69:199 */     Utils.checkForRemainingOptions(options);
/*  70:    */   }
/*  71:    */   
/*  72:    */   public String[] getOptions()
/*  73:    */   {
/*  74:210 */     Vector<String> result = new Vector();
/*  75:    */     
/*  76:212 */     result.add("-M");
/*  77:213 */     result.add("" + this.m_TransformMethod);
/*  78:    */     
/*  79:215 */     Collections.addAll(result, super.getOptions());
/*  80:    */     
/*  81:217 */     return (String[])result.toArray(new String[result.size()]);
/*  82:    */   }
/*  83:    */   
/*  84:    */   public String transformMethodTipText()
/*  85:    */   {
/*  86:227 */     return "The method used in transformation.";
/*  87:    */   }
/*  88:    */   
/*  89:    */   public void setTransformMethod(SelectedTag newMethod)
/*  90:    */   {
/*  91:236 */     if (newMethod.getTags() == TAGS_TRANSFORMMETHOD) {
/*  92:237 */       this.m_TransformMethod = newMethod.getSelectedTag().getID();
/*  93:    */     }
/*  94:    */   }
/*  95:    */   
/*  96:    */   public SelectedTag getTransformMethod()
/*  97:    */   {
/*  98:247 */     return new SelectedTag(this.m_TransformMethod, TAGS_TRANSFORMMETHOD);
/*  99:    */   }
/* 100:    */   
/* 101:    */   public Instances transform(Instances train)
/* 102:    */     throws Exception
/* 103:    */   {
/* 104:262 */     Attribute classAttribute = (Attribute)train.classAttribute().copy();
/* 105:263 */     Attribute bagLabel = train.attribute(0);
/* 106:    */     
/* 107:    */ 
/* 108:266 */     Instances newData = train.attribute(1).relation().stringFreeStructure();
/* 109:    */     
/* 110:    */ 
/* 111:269 */     newData.insertAttributeAt(bagLabel, 0);
/* 112:    */     
/* 113:    */ 
/* 114:272 */     newData.insertAttributeAt(classAttribute, newData.numAttributes());
/* 115:273 */     newData.setClassIndex(newData.numAttributes() - 1);
/* 116:    */     
/* 117:275 */     Instances mini_data = newData.stringFreeStructure();
/* 118:276 */     Instances max_data = newData.stringFreeStructure();
/* 119:    */     
/* 120:278 */     Instance newInst = new DenseInstance(newData.numAttributes());
/* 121:279 */     Instance mini_Inst = new DenseInstance(mini_data.numAttributes());
/* 122:280 */     Instance max_Inst = new DenseInstance(max_data.numAttributes());
/* 123:281 */     newInst.setDataset(newData);
/* 124:282 */     mini_Inst.setDataset(mini_data);
/* 125:283 */     max_Inst.setDataset(max_data);
/* 126:    */     
/* 127:285 */     double N = train.numInstances();
/* 128:286 */     for (int i = 0; i < N; i++)
/* 129:    */     {
/* 130:287 */       int attIdx = 1;
/* 131:288 */       Instance bag = train.instance(i);
/* 132:289 */       double labelValue = bag.value(0);
/* 133:290 */       if (this.m_TransformMethod != 3)
/* 134:    */       {
/* 135:291 */         newInst.setValue(0, labelValue);
/* 136:    */       }
/* 137:    */       else
/* 138:    */       {
/* 139:293 */         mini_Inst.setValue(0, labelValue);
/* 140:294 */         max_Inst.setValue(0, labelValue);
/* 141:    */       }
/* 142:297 */       Instances data = bag.relationalValue(1);
/* 143:299 */       for (int j = 0; j < data.numAttributes(); j++) {
/* 144:301 */         if (this.m_TransformMethod == 1)
/* 145:    */         {
/* 146:302 */           double value = data.meanOrMode(j);
/* 147:303 */           newInst.setValue(attIdx++, value);
/* 148:    */         }
/* 149:304 */         else if (this.m_TransformMethod == 2)
/* 150:    */         {
/* 151:305 */           double[] minimax = minimax(data, j);
/* 152:306 */           double value = (minimax[0] + minimax[1]) / 2.0D;
/* 153:307 */           newInst.setValue(attIdx++, value);
/* 154:    */         }
/* 155:    */         else
/* 156:    */         {
/* 157:309 */           double[] minimax = minimax(data, j);
/* 158:310 */           mini_Inst.setValue(attIdx, minimax[0]);
/* 159:311 */           max_Inst.setValue(attIdx, minimax[1]);
/* 160:312 */           attIdx++;
/* 161:    */         }
/* 162:    */       }
/* 163:316 */       if (this.m_TransformMethod == 3)
/* 164:    */       {
/* 165:317 */         if (!bag.classIsMissing()) {
/* 166:318 */           max_Inst.setClassValue(bag.classValue());
/* 167:    */         }
/* 168:320 */         mini_data.add(mini_Inst);
/* 169:321 */         max_data.add(max_Inst);
/* 170:    */       }
/* 171:    */       else
/* 172:    */       {
/* 173:323 */         if (!bag.classIsMissing()) {
/* 174:324 */           newInst.setClassValue(bag.classValue());
/* 175:    */         }
/* 176:326 */         newData.add(newInst);
/* 177:    */       }
/* 178:    */     }
/* 179:330 */     if (this.m_TransformMethod == 3)
/* 180:    */     {
/* 181:331 */       mini_data.setClassIndex(-1);
/* 182:332 */       mini_data.deleteAttributeAt(mini_data.numAttributes() - 1);
/* 183:    */       
/* 184:    */ 
/* 185:    */ 
/* 186:    */ 
/* 187:337 */       max_data.deleteAttributeAt(0);
/* 188:    */       
/* 189:    */ 
/* 190:340 */       newData = Instances.mergeInstances(mini_data, max_data);
/* 191:    */       
/* 192:    */ 
/* 193:343 */       newData.setClassIndex(newData.numAttributes() - 1);
/* 194:    */     }
/* 195:347 */     return newData;
/* 196:    */   }
/* 197:    */   
/* 198:    */   public static double[] minimax(Instances data, int attIndex)
/* 199:    */   {
/* 200:358 */     double[] rt = { (1.0D / 0.0D), (-1.0D / 0.0D) };
/* 201:359 */     for (int i = 0; i < data.numInstances(); i++)
/* 202:    */     {
/* 203:360 */       double val = data.instance(i).value(attIndex);
/* 204:361 */       if (val > rt[1]) {
/* 205:362 */         rt[1] = val;
/* 206:    */       }
/* 207:364 */       if (val < rt[0]) {
/* 208:365 */         rt[0] = val;
/* 209:    */       }
/* 210:    */     }
/* 211:369 */     for (int j = 0; j < 2; j++) {
/* 212:370 */       if (Double.isInfinite(rt[j])) {
/* 213:371 */         rt[j] = (0.0D / 0.0D);
/* 214:    */       }
/* 215:    */     }
/* 216:375 */     return rt;
/* 217:    */   }
/* 218:    */   
/* 219:    */   public Capabilities getCapabilities()
/* 220:    */   {
/* 221:385 */     Capabilities result = super.getCapabilities();
/* 222:    */     
/* 223:    */ 
/* 224:388 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/* 225:389 */     result.enable(Capabilities.Capability.RELATIONAL_ATTRIBUTES);
/* 226:390 */     result.disable(Capabilities.Capability.MISSING_VALUES);
/* 227:    */     
/* 228:    */ 
/* 229:393 */     result.disableAllClasses();
/* 230:394 */     result.disableAllClassDependencies();
/* 231:395 */     if (super.getCapabilities().handles(Capabilities.Capability.NOMINAL_CLASS)) {
/* 232:396 */       result.enable(Capabilities.Capability.NOMINAL_CLASS);
/* 233:    */     }
/* 234:398 */     if (super.getCapabilities().handles(Capabilities.Capability.BINARY_CLASS)) {
/* 235:399 */       result.enable(Capabilities.Capability.BINARY_CLASS);
/* 236:    */     }
/* 237:401 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 238:    */     
/* 239:    */ 
/* 240:404 */     result.enable(Capabilities.Capability.ONLY_MULTIINSTANCE);
/* 241:    */     
/* 242:406 */     return result;
/* 243:    */   }
/* 244:    */   
/* 245:    */   public Capabilities getMultiInstanceCapabilities()
/* 246:    */   {
/* 247:418 */     Capabilities result = super.getCapabilities();
/* 248:    */     
/* 249:    */ 
/* 250:421 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/* 251:422 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/* 252:423 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/* 253:424 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 254:    */     
/* 255:    */ 
/* 256:427 */     result.disableAllClasses();
/* 257:428 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 258:    */     
/* 259:430 */     return result;
/* 260:    */   }
/* 261:    */   
/* 262:    */   public void buildClassifier(Instances train)
/* 263:    */     throws Exception
/* 264:    */   {
/* 265:444 */     getCapabilities().testWithFail(train);
/* 266:    */     
/* 267:    */ 
/* 268:447 */     train = new Instances(train);
/* 269:448 */     train.deleteWithMissingClass();
/* 270:450 */     if (this.m_Classifier == null) {
/* 271:451 */       throw new Exception("A base classifier has not been specified!");
/* 272:    */     }
/* 273:454 */     if (getDebug()) {
/* 274:455 */       System.out.println("Start training ...");
/* 275:    */     }
/* 276:457 */     Instances data = transform(train);
/* 277:    */     
/* 278:459 */     data.deleteAttributeAt(0);
/* 279:460 */     this.m_Classifier.buildClassifier(data);
/* 280:462 */     if (getDebug()) {
/* 281:463 */       System.out.println("Finish building model");
/* 282:    */     }
/* 283:    */   }
/* 284:    */   
/* 285:    */   public double[] distributionForInstance(Instance newBag)
/* 286:    */     throws Exception
/* 287:    */   {
/* 288:477 */     double[] distribution = new double[2];
/* 289:478 */     Instances test = new Instances(newBag.dataset(), 0);
/* 290:479 */     test.add(newBag);
/* 291:    */     
/* 292:481 */     test = transform(test);
/* 293:482 */     test.deleteAttributeAt(0);
/* 294:483 */     Instance newInst = test.firstInstance();
/* 295:    */     
/* 296:485 */     distribution = this.m_Classifier.distributionForInstance(newInst);
/* 297:    */     
/* 298:487 */     return distribution;
/* 299:    */   }
/* 300:    */   
/* 301:    */   public String toString()
/* 302:    */   {
/* 303:497 */     return "SimpleMI with base classifier: \n" + this.m_Classifier.toString();
/* 304:    */   }
/* 305:    */   
/* 306:    */   public String getRevision()
/* 307:    */   {
/* 308:507 */     return RevisionUtils.extract("$Revision: 10369 $");
/* 309:    */   }
/* 310:    */   
/* 311:    */   public static void main(String[] argv)
/* 312:    */   {
/* 313:517 */     runClassifier(new SimpleMI(), argv);
/* 314:    */   }
/* 315:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.mi.SimpleMI
 * JD-Core Version:    0.7.0.1
 */