/*   1:    */ package weka.filters.unsupervised.attribute;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.core.Attribute;
/*   7:    */ import weka.core.Capabilities;
/*   8:    */ import weka.core.Capabilities.Capability;
/*   9:    */ import weka.core.DenseInstance;
/*  10:    */ import weka.core.Instance;
/*  11:    */ import weka.core.Instances;
/*  12:    */ import weka.core.Option;
/*  13:    */ import weka.core.OptionHandler;
/*  14:    */ import weka.core.RevisionUtils;
/*  15:    */ import weka.core.SparseInstance;
/*  16:    */ import weka.core.Utils;
/*  17:    */ import weka.filters.Sourcable;
/*  18:    */ import weka.filters.UnsupervisedFilter;
/*  19:    */ 
/*  20:    */ public class Normalize
/*  21:    */   extends PotentialClassIgnorer
/*  22:    */   implements UnsupervisedFilter, Sourcable, OptionHandler
/*  23:    */ {
/*  24:    */   static final long serialVersionUID = -8158531150984362898L;
/*  25:    */   protected double[] m_MinArray;
/*  26:    */   protected double[] m_MaxArray;
/*  27: 91 */   protected double m_Translation = 0.0D;
/*  28: 94 */   protected double m_Scale = 1.0D;
/*  29:    */   
/*  30:    */   public String globalInfo()
/*  31:    */   {
/*  32:103 */     return "Normalizes all numeric values in the given dataset (apart from the class attribute, if set). The resulting values are by default in [0,1] for the data used to compute the normalization intervals. But with the scale and translation parameters one can change that, e.g., with scale = 2.0 and translation = -1.0 you get values in the range [-1,+1].";
/*  33:    */   }
/*  34:    */   
/*  35:    */   public Enumeration<Option> listOptions()
/*  36:    */   {
/*  37:119 */     Vector<Option> result = new Vector();
/*  38:    */     
/*  39:121 */     result.addElement(new Option("\tThe scaling factor for the output range.\n\t(default: 1.0)", "S", 1, "-S <num>"));
/*  40:    */     
/*  41:    */ 
/*  42:124 */     result.addElement(new Option("\tThe translation of the output range.\n\t(default: 0.0)", "T", 1, "-T <num>"));
/*  43:    */     
/*  44:    */ 
/*  45:127 */     result.addAll(Collections.list(super.listOptions()));
/*  46:    */     
/*  47:129 */     return result.elements();
/*  48:    */   }
/*  49:    */   
/*  50:    */   public void setOptions(String[] options)
/*  51:    */     throws Exception
/*  52:    */   {
/*  53:166 */     String tmpStr = Utils.getOption('S', options);
/*  54:167 */     if (tmpStr.length() != 0) {
/*  55:168 */       setScale(Double.parseDouble(tmpStr));
/*  56:    */     } else {
/*  57:170 */       setScale(1.0D);
/*  58:    */     }
/*  59:173 */     tmpStr = Utils.getOption('T', options);
/*  60:174 */     if (tmpStr.length() != 0) {
/*  61:175 */       setTranslation(Double.parseDouble(tmpStr));
/*  62:    */     } else {
/*  63:177 */       setTranslation(0.0D);
/*  64:    */     }
/*  65:180 */     if (getInputFormat() != null) {
/*  66:181 */       setInputFormat(getInputFormat());
/*  67:    */     }
/*  68:184 */     super.setOptions(options);
/*  69:    */     
/*  70:186 */     Utils.checkForRemainingOptions(options);
/*  71:    */   }
/*  72:    */   
/*  73:    */   public String[] getOptions()
/*  74:    */   {
/*  75:197 */     Vector<String> result = new Vector();
/*  76:    */     
/*  77:199 */     result.add("-S");
/*  78:200 */     result.add("" + getScale());
/*  79:    */     
/*  80:202 */     result.add("-T");
/*  81:203 */     result.add("" + getTranslation());
/*  82:    */     
/*  83:205 */     Collections.addAll(result, super.getOptions());
/*  84:    */     
/*  85:207 */     return (String[])result.toArray(new String[result.size()]);
/*  86:    */   }
/*  87:    */   
/*  88:    */   public Capabilities getCapabilities()
/*  89:    */   {
/*  90:218 */     Capabilities result = super.getCapabilities();
/*  91:219 */     result.disableAll();
/*  92:    */     
/*  93:    */ 
/*  94:222 */     result.enableAllAttributes();
/*  95:223 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  96:    */     
/*  97:    */ 
/*  98:226 */     result.enableAllClasses();
/*  99:227 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 100:228 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 101:    */     
/* 102:230 */     return result;
/* 103:    */   }
/* 104:    */   
/* 105:    */   public boolean setInputFormat(Instances instanceInfo)
/* 106:    */     throws Exception
/* 107:    */   {
/* 108:245 */     super.setInputFormat(instanceInfo);
/* 109:246 */     setOutputFormat(instanceInfo);
/* 110:247 */     this.m_MinArray = (this.m_MaxArray = null);
/* 111:248 */     return true;
/* 112:    */   }
/* 113:    */   
/* 114:    */   public boolean input(Instance instance)
/* 115:    */     throws Exception
/* 116:    */   {
/* 117:262 */     if (getInputFormat() == null) {
/* 118:263 */       throw new IllegalStateException("No input instance format defined");
/* 119:    */     }
/* 120:266 */     if (this.m_NewBatch)
/* 121:    */     {
/* 122:267 */       resetQueue();
/* 123:268 */       this.m_NewBatch = false;
/* 124:    */     }
/* 125:270 */     if (this.m_MinArray == null)
/* 126:    */     {
/* 127:271 */       bufferInput(instance);
/* 128:272 */       return false;
/* 129:    */     }
/* 130:274 */     convertInstance(instance);
/* 131:275 */     return true;
/* 132:    */   }
/* 133:    */   
/* 134:    */   public boolean batchFinished()
/* 135:    */     throws Exception
/* 136:    */   {
/* 137:290 */     if (getInputFormat() == null) {
/* 138:291 */       throw new IllegalStateException("No input instance format defined");
/* 139:    */     }
/* 140:294 */     if (this.m_MinArray == null)
/* 141:    */     {
/* 142:295 */       Instances input = getInputFormat();
/* 143:    */       
/* 144:297 */       this.m_MinArray = new double[input.numAttributes()];
/* 145:298 */       this.m_MaxArray = new double[input.numAttributes()];
/* 146:299 */       for (int i = 0; i < input.numAttributes(); i++) {
/* 147:300 */         this.m_MinArray[i] = (0.0D / 0.0D);
/* 148:    */       }
/* 149:303 */       for (int j = 0; j < input.numInstances(); j++)
/* 150:    */       {
/* 151:304 */         double[] value = input.instance(j).toDoubleArray();
/* 152:305 */         for (int i = 0; i < input.numAttributes(); i++) {
/* 153:306 */           if ((input.attribute(i).isNumeric()) && (input.classIndex() != i) && 
/* 154:307 */             (!Utils.isMissingValue(value[i]))) {
/* 155:308 */             if (Double.isNaN(this.m_MinArray[i]))
/* 156:    */             {
/* 157:309 */               double tmp167_166 = value[i];this.m_MaxArray[i] = tmp167_166;this.m_MinArray[i] = tmp167_166;
/* 158:    */             }
/* 159:    */             else
/* 160:    */             {
/* 161:311 */               if (value[i] < this.m_MinArray[i]) {
/* 162:312 */                 this.m_MinArray[i] = value[i];
/* 163:    */               }
/* 164:314 */               if (value[i] > this.m_MaxArray[i]) {
/* 165:315 */                 this.m_MaxArray[i] = value[i];
/* 166:    */               }
/* 167:    */             }
/* 168:    */           }
/* 169:    */         }
/* 170:    */       }
/* 171:324 */       for (int i = 0; i < input.numInstances(); i++) {
/* 172:325 */         convertInstance(input.instance(i));
/* 173:    */       }
/* 174:    */     }
/* 175:329 */     flushInput();
/* 176:    */     
/* 177:331 */     this.m_NewBatch = true;
/* 178:332 */     return numPendingOutput() != 0;
/* 179:    */   }
/* 180:    */   
/* 181:    */   protected void convertInstance(Instance instance)
/* 182:    */     throws Exception
/* 183:    */   {
/* 184:343 */     Instance inst = null;
/* 185:344 */     if ((instance instanceof SparseInstance))
/* 186:    */     {
/* 187:345 */       double[] newVals = new double[instance.numAttributes()];
/* 188:346 */       int[] newIndices = new int[instance.numAttributes()];
/* 189:347 */       double[] vals = instance.toDoubleArray();
/* 190:348 */       int ind = 0;
/* 191:349 */       for (int j = 0; j < instance.numAttributes(); j++) {
/* 192:351 */         if ((instance.attribute(j).isNumeric()) && (!Utils.isMissingValue(vals[j])) && (getInputFormat().classIndex() != j))
/* 193:    */         {
/* 194:    */           double value;
/* 195:    */           double value;
/* 196:354 */           if ((Double.isNaN(this.m_MinArray[j])) || (this.m_MaxArray[j] == this.m_MinArray[j]))
/* 197:    */           {
/* 198:355 */             value = 0.0D;
/* 199:    */           }
/* 200:    */           else
/* 201:    */           {
/* 202:357 */             value = (vals[j] - this.m_MinArray[j]) / (this.m_MaxArray[j] - this.m_MinArray[j]) * this.m_Scale + this.m_Translation;
/* 203:359 */             if (Double.isNaN(value)) {
/* 204:360 */               throw new Exception("A NaN value was generated while normalizing " + instance.attribute(j).name());
/* 205:    */             }
/* 206:    */           }
/* 207:364 */           if (value != 0.0D)
/* 208:    */           {
/* 209:365 */             newVals[ind] = value;
/* 210:366 */             newIndices[ind] = j;
/* 211:367 */             ind++;
/* 212:    */           }
/* 213:    */         }
/* 214:    */         else
/* 215:    */         {
/* 216:370 */           double value = vals[j];
/* 217:371 */           if (value != 0.0D)
/* 218:    */           {
/* 219:372 */             newVals[ind] = value;
/* 220:373 */             newIndices[ind] = j;
/* 221:374 */             ind++;
/* 222:    */           }
/* 223:    */         }
/* 224:    */       }
/* 225:378 */       double[] tempVals = new double[ind];
/* 226:379 */       int[] tempInd = new int[ind];
/* 227:380 */       System.arraycopy(newVals, 0, tempVals, 0, ind);
/* 228:381 */       System.arraycopy(newIndices, 0, tempInd, 0, ind);
/* 229:382 */       inst = new SparseInstance(instance.weight(), tempVals, tempInd, instance.numAttributes());
/* 230:    */     }
/* 231:    */     else
/* 232:    */     {
/* 233:385 */       double[] vals = instance.toDoubleArray();
/* 234:386 */       for (int j = 0; j < getInputFormat().numAttributes(); j++) {
/* 235:387 */         if ((instance.attribute(j).isNumeric()) && (!Utils.isMissingValue(vals[j])) && (getInputFormat().classIndex() != j)) {
/* 236:390 */           if ((Double.isNaN(this.m_MinArray[j])) || (this.m_MaxArray[j] == this.m_MinArray[j]))
/* 237:    */           {
/* 238:391 */             vals[j] = 0.0D;
/* 239:    */           }
/* 240:    */           else
/* 241:    */           {
/* 242:393 */             vals[j] = ((vals[j] - this.m_MinArray[j]) / (this.m_MaxArray[j] - this.m_MinArray[j]) * this.m_Scale + this.m_Translation);
/* 243:395 */             if (Double.isNaN(vals[j])) {
/* 244:396 */               throw new Exception("A NaN value was generated while normalizing " + instance.attribute(j).name());
/* 245:    */             }
/* 246:    */           }
/* 247:    */         }
/* 248:    */       }
/* 249:402 */       inst = new DenseInstance(instance.weight(), vals);
/* 250:    */     }
/* 251:404 */     inst.setDataset(instance.dataset());
/* 252:405 */     push(inst, false);
/* 253:    */   }
/* 254:    */   
/* 255:    */   public String toSource(String className, Instances data)
/* 256:    */     throws Exception
/* 257:    */   {
/* 258:437 */     StringBuffer result = new StringBuffer();
/* 259:    */     
/* 260:    */ 
/* 261:440 */     boolean[] process = new boolean[data.numAttributes()];
/* 262:441 */     for (int i = 0; i < data.numAttributes(); i++) {
/* 263:442 */       process[i] = ((data.attribute(i).isNumeric()) && (i != data.classIndex()) ? 1 : false);
/* 264:    */     }
/* 265:445 */     result.append("class " + className + " {\n");
/* 266:446 */     result.append("\n");
/* 267:447 */     result.append("  /** lists which attributes will be processed */\n");
/* 268:448 */     result.append("  protected final static boolean[] PROCESS = new boolean[]{" + Utils.arrayToString(process) + "};\n");
/* 269:    */     
/* 270:450 */     result.append("\n");
/* 271:451 */     result.append("  /** the minimum values for numeric values */\n");
/* 272:452 */     result.append("  protected final static double[] MIN = new double[]{" + Utils.arrayToString(this.m_MinArray).replaceAll("NaN", "Double.NaN") + "};\n");
/* 273:    */     
/* 274:    */ 
/* 275:455 */     result.append("\n");
/* 276:456 */     result.append("  /** the maximum values for numeric values */\n");
/* 277:457 */     result.append("  protected final static double[] MAX = new double[]{" + Utils.arrayToString(this.m_MaxArray) + "};\n");
/* 278:    */     
/* 279:459 */     result.append("\n");
/* 280:460 */     result.append("  /** the scale factor */\n");
/* 281:461 */     result.append("  protected final static double SCALE = " + this.m_Scale + ";\n");
/* 282:462 */     result.append("\n");
/* 283:463 */     result.append("  /** the translation */\n");
/* 284:464 */     result.append("  protected final static double TRANSLATION = " + this.m_Translation + ";\n");
/* 285:    */     
/* 286:466 */     result.append("\n");
/* 287:467 */     result.append("  /**\n");
/* 288:468 */     result.append("   * filters a single row\n");
/* 289:469 */     result.append("   * \n");
/* 290:470 */     result.append("   * @param i the row to process\n");
/* 291:471 */     result.append("   * @return the processed row\n");
/* 292:472 */     result.append("   */\n");
/* 293:473 */     result.append("  public static Object[] filter(Object[] i) {\n");
/* 294:474 */     result.append("    Object[] result;\n");
/* 295:475 */     result.append("\n");
/* 296:476 */     result.append("    result = new Object[i.length];\n");
/* 297:477 */     result.append("    for (int n = 0; n < i.length; n++) {\n");
/* 298:478 */     result.append("      if (PROCESS[n] && (i[n] != null)) {\n");
/* 299:479 */     result.append("        if (Double.isNaN(MIN[n]) || (MIN[n] == MAX[n]))\n");
/* 300:480 */     result.append("          result[n] = 0;\n");
/* 301:481 */     result.append("        else\n");
/* 302:482 */     result.append("          result[n] = (((Double) i[n]) - MIN[n]) / (MAX[n] - MIN[n]) * SCALE + TRANSLATION;\n");
/* 303:    */     
/* 304:484 */     result.append("      }\n");
/* 305:485 */     result.append("      else {\n");
/* 306:486 */     result.append("        result[n] = i[n];\n");
/* 307:487 */     result.append("      }\n");
/* 308:488 */     result.append("    }\n");
/* 309:489 */     result.append("\n");
/* 310:490 */     result.append("    return result;\n");
/* 311:491 */     result.append("  }\n");
/* 312:492 */     result.append("\n");
/* 313:493 */     result.append("  /**\n");
/* 314:494 */     result.append("   * filters multiple rows\n");
/* 315:495 */     result.append("   * \n");
/* 316:496 */     result.append("   * @param i the rows to process\n");
/* 317:497 */     result.append("   * @return the processed rows\n");
/* 318:498 */     result.append("   */\n");
/* 319:499 */     result.append("  public static Object[][] filter(Object[][] i) {\n");
/* 320:500 */     result.append("    Object[][] result;\n");
/* 321:501 */     result.append("\n");
/* 322:502 */     result.append("    result = new Object[i.length][];\n");
/* 323:503 */     result.append("    for (int n = 0; n < i.length; n++) {\n");
/* 324:504 */     result.append("      result[n] = filter(i[n]);\n");
/* 325:505 */     result.append("    }\n");
/* 326:506 */     result.append("\n");
/* 327:507 */     result.append("    return result;\n");
/* 328:508 */     result.append("  }\n");
/* 329:509 */     result.append("}\n");
/* 330:    */     
/* 331:511 */     return result.toString();
/* 332:    */   }
/* 333:    */   
/* 334:    */   public double[] getMinArray()
/* 335:    */   {
/* 336:520 */     return this.m_MinArray;
/* 337:    */   }
/* 338:    */   
/* 339:    */   public double[] getMaxArray()
/* 340:    */   {
/* 341:529 */     return this.m_MaxArray;
/* 342:    */   }
/* 343:    */   
/* 344:    */   public String scaleTipText()
/* 345:    */   {
/* 346:539 */     return "The factor for scaling the output range (default: 1).";
/* 347:    */   }
/* 348:    */   
/* 349:    */   public double getScale()
/* 350:    */   {
/* 351:548 */     return this.m_Scale;
/* 352:    */   }
/* 353:    */   
/* 354:    */   public void setScale(double value)
/* 355:    */   {
/* 356:557 */     this.m_Scale = value;
/* 357:    */   }
/* 358:    */   
/* 359:    */   public String translationTipText()
/* 360:    */   {
/* 361:567 */     return "The translation of the output range (default: 0).";
/* 362:    */   }
/* 363:    */   
/* 364:    */   public double getTranslation()
/* 365:    */   {
/* 366:576 */     return this.m_Translation;
/* 367:    */   }
/* 368:    */   
/* 369:    */   public void setTranslation(double value)
/* 370:    */   {
/* 371:585 */     this.m_Translation = value;
/* 372:    */   }
/* 373:    */   
/* 374:    */   public String getRevision()
/* 375:    */   {
/* 376:595 */     return RevisionUtils.extract("$Revision: 12037 $");
/* 377:    */   }
/* 378:    */   
/* 379:    */   public static void main(String[] args)
/* 380:    */   {
/* 381:604 */     runFilter(new Normalize(), args);
/* 382:    */   }
/* 383:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.Normalize
 * JD-Core Version:    0.7.0.1
 */