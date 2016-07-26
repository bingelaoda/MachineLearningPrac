/*   1:    */ package weka.estimators;
/*   2:    */ 
/*   3:    */ import java.io.BufferedReader;
/*   4:    */ import java.io.FileReader;
/*   5:    */ import java.io.InputStreamReader;
/*   6:    */ import java.io.PrintStream;
/*   7:    */ import java.io.Reader;
/*   8:    */ import java.io.Serializable;
/*   9:    */ import java.util.Enumeration;
/*  10:    */ import java.util.Vector;
/*  11:    */ import weka.core.Capabilities;
/*  12:    */ import weka.core.CapabilitiesHandler;
/*  13:    */ import weka.core.CapabilitiesIgnorer;
/*  14:    */ import weka.core.Instance;
/*  15:    */ import weka.core.Instances;
/*  16:    */ import weka.core.Option;
/*  17:    */ import weka.core.OptionHandler;
/*  18:    */ import weka.core.RevisionHandler;
/*  19:    */ import weka.core.RevisionUtils;
/*  20:    */ import weka.core.SerializedObject;
/*  21:    */ import weka.core.Utils;
/*  22:    */ 
/*  23:    */ public abstract class Estimator
/*  24:    */   implements Cloneable, Serializable, OptionHandler, CapabilitiesHandler, CapabilitiesIgnorer, RevisionHandler
/*  25:    */ {
/*  26:    */   static final long serialVersionUID = -5902411487362274342L;
/*  27:102 */   private boolean m_Debug = false;
/*  28:108 */   protected double m_classValueIndex = -1.0D;
/*  29:111 */   protected boolean m_noClass = true;
/*  30:    */   
/*  31:    */   private static class Builder
/*  32:    */     implements Serializable, RevisionHandler
/*  33:    */   {
/*  34:    */     private static final long serialVersionUID = -5810927990193597303L;
/*  35:122 */     Instances m_instances = null;
/*  36:125 */     int m_attrIndex = -1;
/*  37:128 */     int m_classIndex = -1;
/*  38:131 */     int m_classValueIndex = -1;
/*  39:    */     
/*  40:    */     public String getRevision()
/*  41:    */     {
/*  42:140 */       return RevisionUtils.extract("$Revision: 11006 $");
/*  43:    */     }
/*  44:    */   }
/*  45:    */   
/*  46:145 */   protected boolean m_DoNotCheckCapabilities = false;
/*  47:    */   
/*  48:    */   public String doNotCheckCapabilitiesTipText()
/*  49:    */   {
/*  50:154 */     return "If set, estimator capabilities are not checked before estimator is built (Use with caution to reduce runtime).";
/*  51:    */   }
/*  52:    */   
/*  53:    */   public void setDoNotCheckCapabilities(boolean doNotCheckCapabilities)
/*  54:    */   {
/*  55:165 */     this.m_DoNotCheckCapabilities = doNotCheckCapabilities;
/*  56:    */   }
/*  57:    */   
/*  58:    */   public boolean getDoNotCheckCapabilities()
/*  59:    */   {
/*  60:175 */     return this.m_DoNotCheckCapabilities;
/*  61:    */   }
/*  62:    */   
/*  63:    */   public void addValue(double data, double weight)
/*  64:    */   {
/*  65:    */     try
/*  66:    */     {
/*  67:186 */       throw new Exception("Method to add single value is not implemented!\nEstimator should implement IncrementalEstimator.");
/*  68:    */     }
/*  69:    */     catch (Exception ex)
/*  70:    */     {
/*  71:189 */       ex.printStackTrace();
/*  72:190 */       System.out.println(ex.getMessage());
/*  73:    */     }
/*  74:    */   }
/*  75:    */   
/*  76:    */   public void addValues(Instances data, int attrIndex)
/*  77:    */     throws Exception
/*  78:    */   {
/*  79:203 */     getCapabilities().testWithFail(data);
/*  80:    */     
/*  81:205 */     double[] minMax = new double[2];
/*  82:    */     try
/*  83:    */     {
/*  84:208 */       EstimatorUtils.getMinMax(data, attrIndex, minMax);
/*  85:    */     }
/*  86:    */     catch (Exception ex)
/*  87:    */     {
/*  88:210 */       ex.printStackTrace();
/*  89:211 */       System.out.println(ex.getMessage());
/*  90:    */     }
/*  91:214 */     double min = minMax[0];
/*  92:215 */     double max = minMax[1];
/*  93:    */     
/*  94:    */ 
/*  95:218 */     addValues(data, attrIndex, min, max, 1.0D);
/*  96:    */   }
/*  97:    */   
/*  98:    */   public void addValues(Instances data, int attrIndex, double min, double max, double factor)
/*  99:    */     throws Exception
/* 100:    */   {
/* 101:238 */     int numInst = data.numInstances();
/* 102:239 */     for (int i = 1; i < numInst; i++) {
/* 103:240 */       addValue(data.instance(i).value(attrIndex), 1.0D);
/* 104:    */     }
/* 105:    */   }
/* 106:    */   
/* 107:    */   public void addValues(Instances data, int attrIndex, int classIndex, int classValue)
/* 108:    */     throws Exception
/* 109:    */   {
/* 110:257 */     this.m_noClass = false;
/* 111:258 */     getCapabilities().testWithFail(data);
/* 112:    */     
/* 113:    */ 
/* 114:261 */     double[] minMax = new double[2];
/* 115:    */     try
/* 116:    */     {
/* 117:264 */       EstimatorUtils.getMinMax(data, attrIndex, minMax);
/* 118:    */     }
/* 119:    */     catch (Exception ex)
/* 120:    */     {
/* 121:266 */       ex.printStackTrace();
/* 122:267 */       System.out.println(ex.getMessage());
/* 123:    */     }
/* 124:270 */     double min = minMax[0];
/* 125:271 */     double max = minMax[1];
/* 126:    */     
/* 127:    */ 
/* 128:274 */     Instances workData = new Instances(data, 0);
/* 129:275 */     double factor = getInstancesFromClass(data, attrIndex, classIndex, classValue, workData);
/* 130:279 */     if (workData.numInstances() == 0) {
/* 131:280 */       return;
/* 132:    */     }
/* 133:283 */     addValues(data, attrIndex, min, max, factor);
/* 134:    */   }
/* 135:    */   
/* 136:    */   public void addValues(Instances data, int attrIndex, int classIndex, int classValue, double min, double max)
/* 137:    */     throws Exception
/* 138:    */   {
/* 139:302 */     Instances workData = new Instances(data, 0);
/* 140:303 */     double factor = getInstancesFromClass(data, attrIndex, classIndex, classValue, workData);
/* 141:307 */     if (workData.numInstances() == 0) {
/* 142:308 */       return;
/* 143:    */     }
/* 144:311 */     addValues(data, attrIndex, min, max, factor);
/* 145:    */   }
/* 146:    */   
/* 147:    */   private double getInstancesFromClass(Instances data, int attrIndex, int classIndex, double classValue, Instances workData)
/* 148:    */   {
/* 149:327 */     int num = 0;
/* 150:328 */     int numClassValue = 0;
/* 151:329 */     for (int i = 0; i < data.numInstances(); i++) {
/* 152:330 */       if (!data.instance(i).isMissing(attrIndex))
/* 153:    */       {
/* 154:331 */         num++;
/* 155:332 */         if (data.instance(i).value(classIndex) == classValue)
/* 156:    */         {
/* 157:333 */           workData.add(data.instance(i));
/* 158:334 */           numClassValue++;
/* 159:    */         }
/* 160:    */       }
/* 161:    */     }
/* 162:339 */     Double alphaFactor = new Double(numClassValue / num);
/* 163:340 */     return alphaFactor.doubleValue();
/* 164:    */   }
/* 165:    */   
/* 166:    */   public abstract double getProbability(double paramDouble);
/* 167:    */   
/* 168:    */   public static void buildEstimator(Estimator est, String[] options, boolean isIncremental)
/* 169:    */     throws Exception
/* 170:    */   {
/* 171:365 */     Builder build = new Builder(null);
/* 172:    */     try
/* 173:    */     {
/* 174:367 */       setGeneralOptions(build, est, options);
/* 175:369 */       if ((est instanceof OptionHandler)) {
/* 176:370 */         est.setOptions(options);
/* 177:    */       }
/* 178:373 */       Utils.checkForRemainingOptions(options);
/* 179:    */       
/* 180:375 */       buildEstimator(est, build.m_instances, build.m_attrIndex, build.m_classIndex, build.m_classValueIndex, isIncremental);
/* 181:    */     }
/* 182:    */     catch (Exception ex)
/* 183:    */     {
/* 184:378 */       ex.printStackTrace();
/* 185:379 */       System.out.println(ex.getMessage());
/* 186:380 */       String specificOptions = "";
/* 187:382 */       if ((est instanceof OptionHandler))
/* 188:    */       {
/* 189:383 */         specificOptions = specificOptions + "\nEstimator options:\n\n";
/* 190:384 */         Enumeration<Option> enumOptions = est.listOptions();
/* 191:385 */         while (enumOptions.hasMoreElements())
/* 192:    */         {
/* 193:386 */           Option option = (Option)enumOptions.nextElement();
/* 194:387 */           specificOptions = specificOptions + option.synopsis() + '\n' + option.description() + "\n";
/* 195:    */         }
/* 196:    */       }
/* 197:392 */       String genericOptions = "\nGeneral options:\n\n-h\n\tGet help on available options.\n-i <file>\n\tThe name of the file containing input instances.\n\tIf not supplied then instances will be read from stdin.\n-a <attribute index>\n\tThe number of the attribute the probability distribution\n\testimation is done for.\n\t\"first\" and \"last\" are also valid entries.\n\tIf not supplied then no class is assigned.\n-c <class index>\n\tIf class value index is set, this attribute is taken as class.\n\t\"first\" and \"last\" are also valid entries.\n\tIf not supplied then last is default.\n-v <class value index>\n\tIf value is different to -1, select instances of this class value.\n\t\"first\" and \"last\" are also valid entries.\n\tIf not supplied then all instances are taken.\n";
/* 198:    */       
/* 199:    */ 
/* 200:    */ 
/* 201:    */ 
/* 202:    */ 
/* 203:    */ 
/* 204:    */ 
/* 205:    */ 
/* 206:    */ 
/* 207:    */ 
/* 208:    */ 
/* 209:    */ 
/* 210:    */ 
/* 211:    */ 
/* 212:    */ 
/* 213:    */ 
/* 214:    */ 
/* 215:    */ 
/* 216:    */ 
/* 217:412 */       throw new Exception('\n' + ex.getMessage() + specificOptions + genericOptions);
/* 218:    */     }
/* 219:    */   }
/* 220:    */   
/* 221:    */   public static void buildEstimator(Estimator est, Instances instances, int attrIndex, int classIndex, int classValueIndex, boolean isIncremental)
/* 222:    */     throws Exception
/* 223:    */   {
/* 224:424 */     if (!isIncremental)
/* 225:    */     {
/* 226:426 */       if (classValueIndex == -1) {
/* 227:428 */         est.addValues(instances, attrIndex);
/* 228:    */       } else {
/* 229:431 */         est.addValues(instances, attrIndex, classIndex, classValueIndex);
/* 230:    */       }
/* 231:    */     }
/* 232:    */     else
/* 233:    */     {
/* 234:435 */       Enumeration<Instance> enumInsts = instances.enumerateInstances();
/* 235:436 */       while (enumInsts.hasMoreElements())
/* 236:    */       {
/* 237:437 */         Instance instance = (Instance)enumInsts.nextElement();
/* 238:438 */         ((IncrementalEstimator)est).addValue(instance.value(attrIndex), instance.weight());
/* 239:    */       }
/* 240:    */     }
/* 241:    */   }
/* 242:    */   
/* 243:    */   private static void setGeneralOptions(Builder build, Estimator est, String[] options)
/* 244:    */     throws Exception
/* 245:    */   {
/* 246:453 */     Reader input = null;
/* 247:    */     
/* 248:    */ 
/* 249:456 */     boolean helpRequest = Utils.getFlag('h', options);
/* 250:457 */     if (helpRequest) {
/* 251:458 */       throw new Exception("Help requested.\n");
/* 252:    */     }
/* 253:462 */     String infileName = Utils.getOption('i', options);
/* 254:463 */     if (infileName.length() != 0) {
/* 255:464 */       input = new BufferedReader(new FileReader(infileName));
/* 256:    */     } else {
/* 257:466 */       input = new BufferedReader(new InputStreamReader(System.in));
/* 258:    */     }
/* 259:469 */     build.m_instances = new Instances(input);
/* 260:    */     
/* 261:    */ 
/* 262:472 */     String attrIndex = Utils.getOption('a', options);
/* 263:474 */     if (attrIndex.length() != 0)
/* 264:    */     {
/* 265:475 */       if (attrIndex.equals("first"))
/* 266:    */       {
/* 267:476 */         build.m_attrIndex = 0;
/* 268:    */       }
/* 269:477 */       else if (attrIndex.equals("last"))
/* 270:    */       {
/* 271:478 */         build.m_attrIndex = (build.m_instances.numAttributes() - 1);
/* 272:    */       }
/* 273:    */       else
/* 274:    */       {
/* 275:480 */         int index = Integer.parseInt(attrIndex) - 1;
/* 276:481 */         if ((index < 0) || (index >= build.m_instances.numAttributes())) {
/* 277:482 */           throw new IllegalArgumentException("Option a: attribute index out of range.");
/* 278:    */         }
/* 279:485 */         build.m_attrIndex = index;
/* 280:    */       }
/* 281:    */     }
/* 282:    */     else {
/* 283:490 */       build.m_attrIndex = 0;
/* 284:    */     }
/* 285:494 */     String classIndex = Utils.getOption('c', options);
/* 286:495 */     if (classIndex.length() == 0) {
/* 287:496 */       classIndex = "last";
/* 288:    */     }
/* 289:499 */     if (classIndex.length() != 0) {
/* 290:500 */       if (classIndex.equals("first"))
/* 291:    */       {
/* 292:501 */         build.m_classIndex = 0;
/* 293:    */       }
/* 294:502 */       else if (classIndex.equals("last"))
/* 295:    */       {
/* 296:503 */         build.m_classIndex = (build.m_instances.numAttributes() - 1);
/* 297:    */       }
/* 298:    */       else
/* 299:    */       {
/* 300:505 */         int cl = Integer.parseInt(classIndex);
/* 301:506 */         if (cl == -1) {
/* 302:507 */           build.m_classIndex = (build.m_instances.numAttributes() - 1);
/* 303:    */         } else {
/* 304:509 */           build.m_classIndex = (cl - 1);
/* 305:    */         }
/* 306:    */       }
/* 307:    */     }
/* 308:515 */     String classValueIndex = Utils.getOption('v', options);
/* 309:516 */     if (classValueIndex.length() != 0) {
/* 310:517 */       if (classValueIndex.equals("first"))
/* 311:    */       {
/* 312:518 */         build.m_classValueIndex = 0;
/* 313:    */       }
/* 314:519 */       else if (classValueIndex.equals("last"))
/* 315:    */       {
/* 316:520 */         build.m_classValueIndex = (build.m_instances.numAttributes() - 1);
/* 317:    */       }
/* 318:    */       else
/* 319:    */       {
/* 320:522 */         int cl = Integer.parseInt(classValueIndex);
/* 321:523 */         if (cl == -1) {
/* 322:524 */           build.m_classValueIndex = -1;
/* 323:    */         } else {
/* 324:526 */           build.m_classValueIndex = (cl - 1);
/* 325:    */         }
/* 326:    */       }
/* 327:    */     }
/* 328:531 */     build.m_instances.setClassIndex(build.m_classIndex);
/* 329:    */   }
/* 330:    */   
/* 331:    */   public static Estimator clone(Estimator model)
/* 332:    */     throws Exception
/* 333:    */   {
/* 334:543 */     return makeCopy(model);
/* 335:    */   }
/* 336:    */   
/* 337:    */   public static Estimator makeCopy(Estimator model)
/* 338:    */     throws Exception
/* 339:    */   {
/* 340:555 */     return (Estimator)new SerializedObject(model).getObject();
/* 341:    */   }
/* 342:    */   
/* 343:    */   public static Estimator[] makeCopies(Estimator model, int num)
/* 344:    */     throws Exception
/* 345:    */   {
/* 346:570 */     if (model == null) {
/* 347:571 */       throw new Exception("No model estimator set");
/* 348:    */     }
/* 349:573 */     Estimator[] estimators = new Estimator[num];
/* 350:574 */     SerializedObject so = new SerializedObject(model);
/* 351:575 */     for (int i = 0; i < estimators.length; i++) {
/* 352:576 */       estimators[i] = ((Estimator)so.getObject());
/* 353:    */     }
/* 354:578 */     return estimators;
/* 355:    */   }
/* 356:    */   
/* 357:    */   public boolean equals(Object obj)
/* 358:    */   {
/* 359:591 */     if ((obj == null) || (!obj.getClass().equals(getClass()))) {
/* 360:592 */       return false;
/* 361:    */     }
/* 362:594 */     Estimator cmp = (Estimator)obj;
/* 363:595 */     if (this.m_Debug != cmp.m_Debug) {
/* 364:596 */       return false;
/* 365:    */     }
/* 366:598 */     if (this.m_classValueIndex != cmp.m_classValueIndex) {
/* 367:599 */       return false;
/* 368:    */     }
/* 369:601 */     if (this.m_noClass != cmp.m_noClass) {
/* 370:602 */       return false;
/* 371:    */     }
/* 372:605 */     return true;
/* 373:    */   }
/* 374:    */   
/* 375:    */   public Enumeration<Option> listOptions()
/* 376:    */   {
/* 377:616 */     Vector<Option> newVector = new Vector(1);
/* 378:    */     
/* 379:618 */     newVector.addElement(new Option("\tIf set, estimator is run in debug mode and\n\tmay output additional info to the console", "D", 0, "-D"));
/* 380:    */     
/* 381:    */ 
/* 382:621 */     return newVector.elements();
/* 383:    */   }
/* 384:    */   
/* 385:    */   public void setOptions(String[] options)
/* 386:    */     throws Exception
/* 387:    */   {
/* 388:639 */     setDebug(Utils.getFlag('D', options));
/* 389:    */   }
/* 390:    */   
/* 391:    */   public String[] getOptions()
/* 392:    */   {
/* 393:    */     String[] options;
/* 394:651 */     if (getDebug())
/* 395:    */     {
/* 396:652 */       String[] options = new String[1];
/* 397:653 */       options[0] = "-D";
/* 398:    */     }
/* 399:    */     else
/* 400:    */     {
/* 401:655 */       options = new String[0];
/* 402:    */     }
/* 403:657 */     return options;
/* 404:    */   }
/* 405:    */   
/* 406:    */   public static Estimator forName(String name, String[] options)
/* 407:    */     throws Exception
/* 408:    */   {
/* 409:676 */     return (Estimator)Utils.forName(Estimator.class, name, options);
/* 410:    */   }
/* 411:    */   
/* 412:    */   public void setDebug(boolean debug)
/* 413:    */   {
/* 414:686 */     this.m_Debug = debug;
/* 415:    */   }
/* 416:    */   
/* 417:    */   public boolean getDebug()
/* 418:    */   {
/* 419:696 */     return this.m_Debug;
/* 420:    */   }
/* 421:    */   
/* 422:    */   public String debugTipText()
/* 423:    */   {
/* 424:706 */     return "If set to true, estimator may output additional info to the console.";
/* 425:    */   }
/* 426:    */   
/* 427:    */   public Capabilities getCapabilities()
/* 428:    */   {
/* 429:719 */     Capabilities result = new Capabilities(this);
/* 430:720 */     result.enableAll();
/* 431:    */     
/* 432:    */ 
/* 433:    */ 
/* 434:    */ 
/* 435:    */ 
/* 436:    */ 
/* 437:    */ 
/* 438:728 */     return result;
/* 439:    */   }
/* 440:    */   
/* 441:    */   public String getRevision()
/* 442:    */   {
/* 443:738 */     return RevisionUtils.extract("$Revision: 11006 $");
/* 444:    */   }
/* 445:    */   
/* 446:    */   public void testCapabilities(Instances data, int attrIndex)
/* 447:    */     throws Exception
/* 448:    */   {
/* 449:749 */     getCapabilities().testWithFail(data);
/* 450:750 */     getCapabilities().testWithFail(data.attribute(attrIndex));
/* 451:    */   }
/* 452:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.estimators.Estimator
 * JD-Core Version:    0.7.0.1
 */