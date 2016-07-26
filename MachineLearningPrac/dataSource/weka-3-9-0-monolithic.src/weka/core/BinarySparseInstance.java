/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ 
/*   7:    */ public class BinarySparseInstance
/*   8:    */   extends SparseInstance
/*   9:    */ {
/*  10:    */   private static final long serialVersionUID = -5297388762342528737L;
/*  11:    */   
/*  12:    */   public BinarySparseInstance(Instance instance)
/*  13:    */   {
/*  14: 53 */     this.m_Weight = instance.weight();
/*  15: 54 */     this.m_Dataset = null;
/*  16: 55 */     this.m_NumAttributes = instance.numAttributes();
/*  17: 56 */     if ((instance instanceof SparseInstance))
/*  18:    */     {
/*  19: 57 */       this.m_AttValues = null;
/*  20: 58 */       this.m_Indices = ((SparseInstance)instance).m_Indices;
/*  21:    */     }
/*  22:    */     else
/*  23:    */     {
/*  24: 60 */       int[] tempIndices = new int[instance.numAttributes()];
/*  25: 61 */       int vals = 0;
/*  26: 62 */       for (int i = 0; i < instance.numAttributes(); i++) {
/*  27: 63 */         if (instance.value(i) != 0.0D)
/*  28:    */         {
/*  29: 64 */           tempIndices[vals] = i;
/*  30: 65 */           vals++;
/*  31:    */         }
/*  32:    */       }
/*  33: 68 */       this.m_AttValues = null;
/*  34: 69 */       this.m_Indices = new int[vals];
/*  35: 70 */       System.arraycopy(tempIndices, 0, this.m_Indices, 0, vals);
/*  36:    */     }
/*  37:    */   }
/*  38:    */   
/*  39:    */   public BinarySparseInstance(SparseInstance instance)
/*  40:    */   {
/*  41: 83 */     this.m_AttValues = null;
/*  42: 84 */     this.m_Indices = instance.m_Indices;
/*  43: 85 */     this.m_Weight = instance.m_Weight;
/*  44: 86 */     this.m_NumAttributes = instance.m_NumAttributes;
/*  45: 87 */     this.m_Dataset = null;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public BinarySparseInstance(double weight, double[] attValues)
/*  49:    */   {
/*  50:100 */     this.m_Weight = weight;
/*  51:101 */     this.m_Dataset = null;
/*  52:102 */     this.m_NumAttributes = attValues.length;
/*  53:103 */     int[] tempIndices = new int[this.m_NumAttributes];
/*  54:104 */     int vals = 0;
/*  55:105 */     for (int i = 0; i < this.m_NumAttributes; i++) {
/*  56:106 */       if (attValues[i] != 0.0D)
/*  57:    */       {
/*  58:107 */         tempIndices[vals] = i;
/*  59:108 */         vals++;
/*  60:    */       }
/*  61:    */     }
/*  62:111 */     this.m_AttValues = null;
/*  63:112 */     this.m_Indices = new int[vals];
/*  64:113 */     System.arraycopy(tempIndices, 0, this.m_Indices, 0, vals);
/*  65:    */   }
/*  66:    */   
/*  67:    */   public BinarySparseInstance(double weight, int[] indices, int maxNumValues)
/*  68:    */   {
/*  69:127 */     this.m_AttValues = null;
/*  70:128 */     this.m_Indices = indices;
/*  71:129 */     this.m_Weight = weight;
/*  72:130 */     this.m_NumAttributes = maxNumValues;
/*  73:131 */     this.m_Dataset = null;
/*  74:    */   }
/*  75:    */   
/*  76:    */   public BinarySparseInstance(int numAttributes)
/*  77:    */   {
/*  78:143 */     this.m_AttValues = null;
/*  79:144 */     this.m_NumAttributes = numAttributes;
/*  80:145 */     this.m_Indices = new int[numAttributes];
/*  81:146 */     for (int i = 0; i < this.m_Indices.length; i++) {
/*  82:147 */       this.m_Indices[i] = i;
/*  83:    */     }
/*  84:149 */     this.m_Weight = 1.0D;
/*  85:150 */     this.m_Dataset = null;
/*  86:    */   }
/*  87:    */   
/*  88:    */   public Object copy()
/*  89:    */   {
/*  90:163 */     BinarySparseInstance result = new BinarySparseInstance(this);
/*  91:164 */     result.m_Dataset = this.m_Dataset;
/*  92:165 */     return result;
/*  93:    */   }
/*  94:    */   
/*  95:    */   public Instance copy(double[] values)
/*  96:    */   {
/*  97:177 */     BinarySparseInstance result = new BinarySparseInstance(this.m_Weight, values);
/*  98:178 */     result.m_Dataset = this.m_Dataset;
/*  99:179 */     return result;
/* 100:    */   }
/* 101:    */   
/* 102:    */   public Instance mergeInstance(Instance inst)
/* 103:    */   {
/* 104:192 */     int[] indices = new int[numValues() + inst.numValues()];
/* 105:    */     
/* 106:194 */     int m = 0;
/* 107:195 */     for (int j = 0; j < numValues(); j++) {
/* 108:196 */       indices[(m++)] = index(j);
/* 109:    */     }
/* 110:198 */     for (int j = 0; j < inst.numValues(); j++) {
/* 111:199 */       if (inst.valueSparse(j) != 0.0D) {
/* 112:200 */         indices[(m++)] = (numAttributes() + inst.index(j));
/* 113:    */       }
/* 114:    */     }
/* 115:204 */     if (m != indices.length)
/* 116:    */     {
/* 117:206 */       int[] newInd = new int[m];
/* 118:207 */       System.arraycopy(indices, 0, newInd, 0, m);
/* 119:208 */       indices = newInd;
/* 120:    */     }
/* 121:210 */     return new BinarySparseInstance(1.0D, indices, numAttributes() + inst.numAttributes());
/* 122:    */   }
/* 123:    */   
/* 124:    */   public void replaceMissingValues(double[] array) {}
/* 125:    */   
/* 126:    */   public void setValue(int attIndex, double value)
/* 127:    */   {
/* 128:238 */     int index = locateIndex(attIndex);
/* 129:240 */     if ((index >= 0) && (this.m_Indices[index] == attIndex))
/* 130:    */     {
/* 131:241 */       if (value == 0.0D)
/* 132:    */       {
/* 133:242 */         int[] tempIndices = new int[this.m_Indices.length - 1];
/* 134:243 */         System.arraycopy(this.m_Indices, 0, tempIndices, 0, index);
/* 135:244 */         System.arraycopy(this.m_Indices, index + 1, tempIndices, index, this.m_Indices.length - index - 1);
/* 136:    */         
/* 137:246 */         this.m_Indices = tempIndices;
/* 138:    */       }
/* 139:    */     }
/* 140:249 */     else if (value != 0.0D)
/* 141:    */     {
/* 142:250 */       int[] tempIndices = new int[this.m_Indices.length + 1];
/* 143:251 */       System.arraycopy(this.m_Indices, 0, tempIndices, 0, index + 1);
/* 144:252 */       tempIndices[(index + 1)] = attIndex;
/* 145:253 */       System.arraycopy(this.m_Indices, index + 1, tempIndices, index + 2, this.m_Indices.length - index - 1);
/* 146:    */       
/* 147:255 */       this.m_Indices = tempIndices;
/* 148:    */     }
/* 149:    */   }
/* 150:    */   
/* 151:    */   public void setValueSparse(int indexOfIndex, double value)
/* 152:    */   {
/* 153:273 */     if (value == 0.0D)
/* 154:    */     {
/* 155:274 */       int[] tempIndices = new int[this.m_Indices.length - 1];
/* 156:275 */       System.arraycopy(this.m_Indices, 0, tempIndices, 0, indexOfIndex);
/* 157:276 */       System.arraycopy(this.m_Indices, indexOfIndex + 1, tempIndices, indexOfIndex, this.m_Indices.length - indexOfIndex - 1);
/* 158:    */       
/* 159:278 */       this.m_Indices = tempIndices;
/* 160:    */     }
/* 161:    */   }
/* 162:    */   
/* 163:    */   public double[] toDoubleArray()
/* 164:    */   {
/* 165:290 */     double[] newValues = new double[this.m_NumAttributes];
/* 166:291 */     for (int i = 0; i < this.m_Indices.length; i++) {
/* 167:292 */       newValues[this.m_Indices[i]] = 1.0D;
/* 168:    */     }
/* 169:294 */     return newValues;
/* 170:    */   }
/* 171:    */   
/* 172:    */   public String toString()
/* 173:    */   {
/* 174:307 */     StringBuffer text = new StringBuffer();
/* 175:    */     
/* 176:309 */     text.append('{');
/* 177:310 */     for (int i = 0; i < this.m_Indices.length; i++)
/* 178:    */     {
/* 179:311 */       if (i > 0) {
/* 180:312 */         text.append(",");
/* 181:    */       }
/* 182:314 */       if (this.m_Dataset == null) {
/* 183:315 */         text.append(this.m_Indices[i] + " 1");
/* 184:317 */       } else if ((this.m_Dataset.attribute(this.m_Indices[i]).isNominal()) || (this.m_Dataset.attribute(this.m_Indices[i]).isString())) {
/* 185:319 */         text.append(this.m_Indices[i] + " " + Utils.quote(this.m_Dataset.attribute(this.m_Indices[i]).value(1)));
/* 186:    */       } else {
/* 187:322 */         text.append(this.m_Indices[i] + " 1");
/* 188:    */       }
/* 189:    */     }
/* 190:326 */     text.append('}');
/* 191:327 */     if (this.m_Weight != 1.0D) {
/* 192:328 */       text.append(",{" + Utils.doubleToString(this.m_Weight, AbstractInstance.s_numericAfterDecimalPoint) + "}");
/* 193:    */     }
/* 194:332 */     return text.toString();
/* 195:    */   }
/* 196:    */   
/* 197:    */   public double value(int attIndex)
/* 198:    */   {
/* 199:346 */     int index = locateIndex(attIndex);
/* 200:347 */     if ((index >= 0) && (this.m_Indices[index] == attIndex)) {
/* 201:348 */       return 1.0D;
/* 202:    */     }
/* 203:350 */     return 0.0D;
/* 204:    */   }
/* 205:    */   
/* 206:    */   public final double valueSparse(int indexOfIndex)
/* 207:    */   {
/* 208:366 */     return 1.0D;
/* 209:    */   }
/* 210:    */   
/* 211:    */   protected void forceDeleteAttributeAt(int position)
/* 212:    */   {
/* 213:377 */     int index = locateIndex(position);
/* 214:    */     
/* 215:379 */     this.m_NumAttributes -= 1;
/* 216:380 */     if ((index >= 0) && (this.m_Indices[index] == position))
/* 217:    */     {
/* 218:381 */       int[] tempIndices = new int[this.m_Indices.length - 1];
/* 219:382 */       System.arraycopy(this.m_Indices, 0, tempIndices, 0, index);
/* 220:383 */       for (int i = index; i < this.m_Indices.length - 1; i++) {
/* 221:384 */         tempIndices[i] = (this.m_Indices[(i + 1)] - 1);
/* 222:    */       }
/* 223:386 */       this.m_Indices = tempIndices;
/* 224:    */     }
/* 225:    */     else
/* 226:    */     {
/* 227:388 */       int[] tempIndices = new int[this.m_Indices.length];
/* 228:389 */       System.arraycopy(this.m_Indices, 0, tempIndices, 0, index + 1);
/* 229:390 */       for (int i = index + 1; i < this.m_Indices.length - 1; i++) {
/* 230:391 */         tempIndices[i] = (this.m_Indices[i] - 1);
/* 231:    */       }
/* 232:393 */       this.m_Indices = tempIndices;
/* 233:    */     }
/* 234:    */   }
/* 235:    */   
/* 236:    */   protected void forceInsertAttributeAt(int position)
/* 237:    */   {
/* 238:406 */     int index = locateIndex(position);
/* 239:    */     
/* 240:408 */     this.m_NumAttributes += 1;
/* 241:409 */     if ((index >= 0) && (this.m_Indices[index] == position))
/* 242:    */     {
/* 243:410 */       int[] tempIndices = new int[this.m_Indices.length + 1];
/* 244:411 */       System.arraycopy(this.m_Indices, 0, tempIndices, 0, index);
/* 245:412 */       tempIndices[index] = position;
/* 246:413 */       for (int i = index; i < this.m_Indices.length; i++) {
/* 247:414 */         tempIndices[(i + 1)] = (this.m_Indices[i] + 1);
/* 248:    */       }
/* 249:416 */       this.m_Indices = tempIndices;
/* 250:    */     }
/* 251:    */     else
/* 252:    */     {
/* 253:418 */       int[] tempIndices = new int[this.m_Indices.length + 1];
/* 254:419 */       System.arraycopy(this.m_Indices, 0, tempIndices, 0, index + 1);
/* 255:420 */       tempIndices[(index + 1)] = position;
/* 256:421 */       for (int i = index + 1; i < this.m_Indices.length; i++) {
/* 257:422 */         tempIndices[(i + 1)] = (this.m_Indices[i] + 1);
/* 258:    */       }
/* 259:424 */       this.m_Indices = tempIndices;
/* 260:    */     }
/* 261:    */   }
/* 262:    */   
/* 263:    */   public static void main(String[] options)
/* 264:    */   {
/* 265:    */     try
/* 266:    */     {
/* 267:438 */       Attribute length = new Attribute("length");
/* 268:439 */       Attribute weight = new Attribute("weight");
/* 269:    */       
/* 270:    */ 
/* 271:442 */       ArrayList<String> my_nominal_values = new ArrayList(3);
/* 272:443 */       my_nominal_values.add("first");
/* 273:444 */       my_nominal_values.add("second");
/* 274:    */       
/* 275:    */ 
/* 276:447 */       Attribute position = new Attribute("position", my_nominal_values);
/* 277:    */       
/* 278:    */ 
/* 279:450 */       ArrayList<Attribute> attributes = new ArrayList(3);
/* 280:451 */       attributes.add(length);
/* 281:452 */       attributes.add(weight);
/* 282:453 */       attributes.add(position);
/* 283:    */       
/* 284:    */ 
/* 285:456 */       Instances race = new Instances("race", attributes, 0);
/* 286:    */       
/* 287:    */ 
/* 288:459 */       race.setClassIndex(position.index());
/* 289:    */       
/* 290:    */ 
/* 291:462 */       BinarySparseInstance inst = new BinarySparseInstance(3);
/* 292:    */       
/* 293:    */ 
/* 294:    */ 
/* 295:466 */       inst.setValue(length, 5.3D);
/* 296:467 */       inst.setValue(weight, 300.0D);
/* 297:468 */       inst.setValue(position, "first");
/* 298:    */       
/* 299:    */ 
/* 300:471 */       inst.setDataset(race);
/* 301:    */       
/* 302:    */ 
/* 303:474 */       System.out.println("The instance: " + inst);
/* 304:    */       
/* 305:    */ 
/* 306:477 */       System.out.println("First attribute: " + inst.attribute(0));
/* 307:    */       
/* 308:    */ 
/* 309:480 */       System.out.println("Class attribute: " + inst.classAttribute());
/* 310:    */       
/* 311:    */ 
/* 312:483 */       System.out.println("Class index: " + inst.classIndex());
/* 313:    */       
/* 314:    */ 
/* 315:486 */       System.out.println("Class is missing: " + inst.classIsMissing());
/* 316:    */       
/* 317:    */ 
/* 318:489 */       System.out.println("Class value (internal format): " + inst.classValue());
/* 319:    */       
/* 320:    */ 
/* 321:492 */       SparseInstance copy = (SparseInstance)inst.copy();
/* 322:493 */       System.out.println("Shallow copy: " + copy);
/* 323:    */       
/* 324:    */ 
/* 325:496 */       copy.setDataset(inst.dataset());
/* 326:497 */       System.out.println("Shallow copy with dataset set: " + copy);
/* 327:    */       
/* 328:    */ 
/* 329:500 */       System.out.print("All stored values in internal format: ");
/* 330:501 */       for (int i = 0; i < inst.numValues(); i++)
/* 331:    */       {
/* 332:502 */         if (i > 0) {
/* 333:503 */           System.out.print(",");
/* 334:    */         }
/* 335:505 */         System.out.print(inst.valueSparse(i));
/* 336:    */       }
/* 337:507 */       System.out.println();
/* 338:    */       
/* 339:    */ 
/* 340:510 */       System.out.print("All values set to zero: ");
/* 341:511 */       while (inst.numValues() > 0) {
/* 342:512 */         inst.setValueSparse(0, 0.0D);
/* 343:    */       }
/* 344:514 */       for (int i = 0; i < inst.numValues(); i++)
/* 345:    */       {
/* 346:515 */         if (i > 0) {
/* 347:516 */           System.out.print(",");
/* 348:    */         }
/* 349:518 */         System.out.print(inst.valueSparse(i));
/* 350:    */       }
/* 351:520 */       System.out.println();
/* 352:    */       
/* 353:    */ 
/* 354:523 */       System.out.print("All values set to one: ");
/* 355:524 */       for (int i = 0; i < inst.numAttributes(); i++) {
/* 356:525 */         inst.setValue(i, 1.0D);
/* 357:    */       }
/* 358:527 */       for (int i = 0; i < inst.numValues(); i++)
/* 359:    */       {
/* 360:528 */         if (i > 0) {
/* 361:529 */           System.out.print(",");
/* 362:    */         }
/* 363:531 */         System.out.print(inst.valueSparse(i));
/* 364:    */       }
/* 365:533 */       System.out.println();
/* 366:    */       
/* 367:    */ 
/* 368:536 */       copy.setDataset(null);
/* 369:537 */       copy.deleteAttributeAt(0);
/* 370:538 */       copy.insertAttributeAt(0);
/* 371:539 */       copy.setDataset(inst.dataset());
/* 372:540 */       System.out.println("Copy with first attribute deleted and inserted: " + copy);
/* 373:    */       
/* 374:    */ 
/* 375:    */ 
/* 376:544 */       copy.setDataset(null);
/* 377:545 */       copy.deleteAttributeAt(1);
/* 378:546 */       copy.insertAttributeAt(1);
/* 379:547 */       copy.setDataset(inst.dataset());
/* 380:548 */       System.out.println("Copy with second attribute deleted and inserted: " + copy);
/* 381:    */       
/* 382:    */ 
/* 383:    */ 
/* 384:552 */       copy.setDataset(null);
/* 385:553 */       copy.deleteAttributeAt(2);
/* 386:554 */       copy.insertAttributeAt(2);
/* 387:555 */       copy.setDataset(inst.dataset());
/* 388:556 */       System.out.println("Copy with third attribute deleted and inserted: " + copy);
/* 389:    */       
/* 390:    */ 
/* 391:    */ 
/* 392:560 */       System.out.println("Enumerating attributes (leaving out class):");
/* 393:561 */       Enumeration<Attribute> enu = inst.enumerateAttributes();
/* 394:562 */       while (enu.hasMoreElements())
/* 395:    */       {
/* 396:563 */         Attribute att = (Attribute)enu.nextElement();
/* 397:564 */         System.out.println(att);
/* 398:    */       }
/* 399:568 */       System.out.println("Header of original and copy equivalent: " + inst.equalHeaders(copy));
/* 400:    */       
/* 401:    */ 
/* 402:    */ 
/* 403:572 */       System.out.println("Length of copy missing: " + copy.isMissing(length));
/* 404:573 */       System.out.println("Weight of copy missing: " + copy.isMissing(weight.index()));
/* 405:    */       
/* 406:575 */       System.out.println("Length of copy missing: " + Utils.isMissingValue(copy.value(length)));
/* 407:    */       
/* 408:    */ 
/* 409:    */ 
/* 410:579 */       System.out.println("Number of attributes: " + copy.numAttributes());
/* 411:580 */       System.out.println("Number of classes: " + copy.numClasses());
/* 412:    */       
/* 413:    */ 
/* 414:583 */       double[] meansAndModes = { 2.0D, 3.0D, 0.0D };
/* 415:584 */       copy.replaceMissingValues(meansAndModes);
/* 416:585 */       System.out.println("Copy with missing value replaced: " + copy);
/* 417:    */       
/* 418:    */ 
/* 419:588 */       copy.setClassMissing();
/* 420:589 */       System.out.println("Copy with missing class: " + copy);
/* 421:590 */       copy.setClassValue(0.0D);
/* 422:591 */       System.out.println("Copy with class value set to first value: " + copy);
/* 423:592 */       copy.setClassValue("second");
/* 424:593 */       System.out.println("Copy with class value set to \"second\": " + copy);
/* 425:594 */       copy.setMissing(1);
/* 426:595 */       System.out.println("Copy with second attribute set to be missing: " + copy);
/* 427:    */       
/* 428:597 */       copy.setMissing(length);
/* 429:598 */       System.out.println("Copy with length set to be missing: " + copy);
/* 430:599 */       copy.setValue(0, 0.0D);
/* 431:600 */       System.out.println("Copy with first attribute set to 0: " + copy);
/* 432:601 */       copy.setValue(weight, 1.0D);
/* 433:602 */       System.out.println("Copy with weight attribute set to 1: " + copy);
/* 434:603 */       copy.setValue(position, "second");
/* 435:604 */       System.out.println("Copy with position set to \"second\": " + copy);
/* 436:605 */       copy.setValue(2, "first");
/* 437:606 */       System.out.println("Copy with last attribute set to \"first\": " + copy);
/* 438:607 */       System.out.println("Current weight of instance copy: " + copy.weight());
/* 439:608 */       copy.setWeight(2.0D);
/* 440:609 */       System.out.println("Current weight of instance copy (set to 2): " + copy.weight());
/* 441:    */       
/* 442:611 */       System.out.println("Last value of copy: " + copy.toString(2));
/* 443:612 */       System.out.println("Value of position for copy: " + copy.toString(position));
/* 444:    */       
/* 445:614 */       System.out.println("Last value of copy (internal format): " + copy.value(2));
/* 446:    */       
/* 447:616 */       System.out.println("Value of position for copy (internal format): " + copy.value(position));
/* 448:    */     }
/* 449:    */     catch (Exception e)
/* 450:    */     {
/* 451:619 */       e.printStackTrace();
/* 452:    */     }
/* 453:    */   }
/* 454:    */   
/* 455:    */   public String getRevision()
/* 456:    */   {
/* 457:630 */     return RevisionUtils.extract("$Revision: 12472 $");
/* 458:    */   }
/* 459:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.BinarySparseInstance
 * JD-Core Version:    0.7.0.1
 */