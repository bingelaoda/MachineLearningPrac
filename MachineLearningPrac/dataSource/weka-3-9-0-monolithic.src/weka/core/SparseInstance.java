/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ 
/*   7:    */ public class SparseInstance
/*   8:    */   extends AbstractInstance
/*   9:    */ {
/*  10:    */   private static final long serialVersionUID = -3579051291332630149L;
/*  11:    */   protected int[] m_Indices;
/*  12:    */   protected int m_NumAttributes;
/*  13:    */   
/*  14:    */   public SparseInstance(Instance instance)
/*  15:    */   {
/*  16: 60 */     this.m_Weight = instance.weight();
/*  17: 61 */     this.m_Dataset = null;
/*  18: 62 */     this.m_NumAttributes = instance.numAttributes();
/*  19: 63 */     if ((instance instanceof SparseInstance))
/*  20:    */     {
/*  21: 64 */       this.m_AttValues = ((SparseInstance)instance).m_AttValues;
/*  22: 65 */       this.m_Indices = ((SparseInstance)instance).m_Indices;
/*  23:    */     }
/*  24:    */     else
/*  25:    */     {
/*  26: 67 */       double[] tempValues = new double[instance.numAttributes()];
/*  27: 68 */       int[] tempIndices = new int[instance.numAttributes()];
/*  28: 69 */       int vals = 0;
/*  29: 70 */       for (int i = 0; i < instance.numAttributes(); i++) {
/*  30: 71 */         if (instance.value(i) != 0.0D)
/*  31:    */         {
/*  32: 72 */           tempValues[vals] = instance.value(i);
/*  33: 73 */           tempIndices[vals] = i;
/*  34: 74 */           vals++;
/*  35:    */         }
/*  36:    */       }
/*  37: 77 */       this.m_AttValues = new double[vals];
/*  38: 78 */       this.m_Indices = new int[vals];
/*  39: 79 */       System.arraycopy(tempValues, 0, this.m_AttValues, 0, vals);
/*  40: 80 */       System.arraycopy(tempIndices, 0, this.m_Indices, 0, vals);
/*  41:    */     }
/*  42:    */   }
/*  43:    */   
/*  44:    */   public SparseInstance(SparseInstance instance)
/*  45:    */   {
/*  46: 93 */     this.m_AttValues = instance.m_AttValues;
/*  47: 94 */     this.m_Indices = instance.m_Indices;
/*  48: 95 */     this.m_Weight = instance.m_Weight;
/*  49: 96 */     this.m_NumAttributes = instance.m_NumAttributes;
/*  50: 97 */     this.m_Dataset = null;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public SparseInstance(double weight, double[] attValues)
/*  54:    */   {
/*  55:110 */     this.m_Weight = weight;
/*  56:111 */     this.m_Dataset = null;
/*  57:112 */     this.m_NumAttributes = attValues.length;
/*  58:113 */     double[] tempValues = new double[this.m_NumAttributes];
/*  59:114 */     int[] tempIndices = new int[this.m_NumAttributes];
/*  60:115 */     int vals = 0;
/*  61:116 */     for (int i = 0; i < this.m_NumAttributes; i++) {
/*  62:117 */       if (attValues[i] != 0.0D)
/*  63:    */       {
/*  64:118 */         tempValues[vals] = attValues[i];
/*  65:119 */         tempIndices[vals] = i;
/*  66:120 */         vals++;
/*  67:    */       }
/*  68:    */     }
/*  69:123 */     this.m_AttValues = new double[vals];
/*  70:124 */     this.m_Indices = new int[vals];
/*  71:125 */     System.arraycopy(tempValues, 0, this.m_AttValues, 0, vals);
/*  72:126 */     System.arraycopy(tempIndices, 0, this.m_Indices, 0, vals);
/*  73:    */   }
/*  74:    */   
/*  75:    */   public SparseInstance(double weight, double[] attValues, int[] indices, int maxNumValues)
/*  76:    */   {
/*  77:144 */     int vals = 0;
/*  78:145 */     this.m_AttValues = new double[attValues.length];
/*  79:146 */     this.m_Indices = new int[indices.length];
/*  80:147 */     for (int i = 0; i < attValues.length; i++) {
/*  81:148 */       if (attValues[i] != 0.0D)
/*  82:    */       {
/*  83:149 */         this.m_AttValues[vals] = attValues[i];
/*  84:150 */         this.m_Indices[vals] = indices[i];
/*  85:151 */         vals++;
/*  86:    */       }
/*  87:    */     }
/*  88:154 */     if (vals != attValues.length)
/*  89:    */     {
/*  90:156 */       double[] newVals = new double[vals];
/*  91:157 */       System.arraycopy(this.m_AttValues, 0, newVals, 0, vals);
/*  92:158 */       this.m_AttValues = newVals;
/*  93:159 */       int[] newIndices = new int[vals];
/*  94:160 */       System.arraycopy(this.m_Indices, 0, newIndices, 0, vals);
/*  95:161 */       this.m_Indices = newIndices;
/*  96:    */     }
/*  97:163 */     this.m_Weight = weight;
/*  98:164 */     this.m_NumAttributes = maxNumValues;
/*  99:165 */     this.m_Dataset = null;
/* 100:    */   }
/* 101:    */   
/* 102:    */   public SparseInstance(int numAttributes)
/* 103:    */   {
/* 104:177 */     this.m_AttValues = new double[numAttributes];
/* 105:178 */     this.m_NumAttributes = numAttributes;
/* 106:179 */     this.m_Indices = new int[numAttributes];
/* 107:180 */     for (int i = 0; i < this.m_AttValues.length; i++)
/* 108:    */     {
/* 109:181 */       this.m_AttValues[i] = Utils.missingValue();
/* 110:182 */       this.m_Indices[i] = i;
/* 111:    */     }
/* 112:184 */     this.m_Weight = 1.0D;
/* 113:185 */     this.m_Dataset = null;
/* 114:    */   }
/* 115:    */   
/* 116:    */   public Object copy()
/* 117:    */   {
/* 118:198 */     SparseInstance result = new SparseInstance(this);
/* 119:199 */     result.m_Dataset = this.m_Dataset;
/* 120:200 */     return result;
/* 121:    */   }
/* 122:    */   
/* 123:    */   public Instance copy(double[] values)
/* 124:    */   {
/* 125:212 */     SparseInstance result = new SparseInstance(this.m_Weight, values);
/* 126:213 */     result.m_Dataset = this.m_Dataset;
/* 127:214 */     return result;
/* 128:    */   }
/* 129:    */   
/* 130:    */   public int index(int position)
/* 131:    */   {
/* 132:226 */     return this.m_Indices[position];
/* 133:    */   }
/* 134:    */   
/* 135:    */   public int locateIndex(int index)
/* 136:    */   {
/* 137:237 */     int min = 0;int max = this.m_Indices.length - 1;
/* 138:239 */     if (max == -1) {
/* 139:240 */       return -1;
/* 140:    */     }
/* 141:244 */     while ((this.m_Indices[min] <= index) && (this.m_Indices[max] >= index))
/* 142:    */     {
/* 143:245 */       int current = (max + min) / 2;
/* 144:246 */       if (this.m_Indices[current] > index) {
/* 145:247 */         max = current - 1;
/* 146:248 */       } else if (this.m_Indices[current] < index) {
/* 147:249 */         min = current + 1;
/* 148:    */       } else {
/* 149:251 */         return current;
/* 150:    */       }
/* 151:    */     }
/* 152:254 */     if (this.m_Indices[max] < index) {
/* 153:255 */       return max;
/* 154:    */     }
/* 155:257 */     return min - 1;
/* 156:    */   }
/* 157:    */   
/* 158:    */   public Instance mergeInstance(Instance inst)
/* 159:    */   {
/* 160:271 */     double[] values = new double[numValues() + inst.numValues()];
/* 161:272 */     int[] indices = new int[numValues() + inst.numValues()];
/* 162:    */     
/* 163:274 */     int m = 0;
/* 164:275 */     for (int j = 0; j < numValues(); m++)
/* 165:    */     {
/* 166:276 */       values[m] = valueSparse(j);
/* 167:277 */       indices[m] = index(j);j++;
/* 168:    */     }
/* 169:279 */     for (int j = 0; j < inst.numValues(); m++)
/* 170:    */     {
/* 171:280 */       values[m] = inst.valueSparse(j);
/* 172:281 */       indices[m] = (numAttributes() + inst.index(j));j++;
/* 173:    */     }
/* 174:284 */     return new SparseInstance(1.0D, values, indices, numAttributes() + inst.numAttributes());
/* 175:    */   }
/* 176:    */   
/* 177:    */   public int numAttributes()
/* 178:    */   {
/* 179:296 */     return this.m_NumAttributes;
/* 180:    */   }
/* 181:    */   
/* 182:    */   public int numValues()
/* 183:    */   {
/* 184:307 */     return this.m_Indices.length;
/* 185:    */   }
/* 186:    */   
/* 187:    */   public void replaceMissingValues(double[] array)
/* 188:    */   {
/* 189:321 */     if ((array == null) || (array.length != this.m_NumAttributes)) {
/* 190:322 */       throw new IllegalArgumentException("Unequal number of attributes!");
/* 191:    */     }
/* 192:324 */     double[] tempValues = new double[this.m_AttValues.length];
/* 193:325 */     int[] tempIndices = new int[this.m_AttValues.length];
/* 194:326 */     int vals = 0;
/* 195:327 */     for (int i = 0; i < this.m_AttValues.length; i++) {
/* 196:328 */       if (isMissingSparse(i))
/* 197:    */       {
/* 198:329 */         if (array[this.m_Indices[i]] != 0.0D)
/* 199:    */         {
/* 200:330 */           tempValues[vals] = array[this.m_Indices[i]];
/* 201:331 */           tempIndices[vals] = this.m_Indices[i];
/* 202:332 */           vals++;
/* 203:    */         }
/* 204:    */       }
/* 205:    */       else
/* 206:    */       {
/* 207:335 */         tempValues[vals] = this.m_AttValues[i];
/* 208:336 */         tempIndices[vals] = this.m_Indices[i];
/* 209:337 */         vals++;
/* 210:    */       }
/* 211:    */     }
/* 212:340 */     this.m_AttValues = new double[vals];
/* 213:341 */     this.m_Indices = new int[vals];
/* 214:342 */     System.arraycopy(tempValues, 0, this.m_AttValues, 0, vals);
/* 215:343 */     System.arraycopy(tempIndices, 0, this.m_Indices, 0, vals);
/* 216:    */   }
/* 217:    */   
/* 218:    */   public void setValue(int attIndex, double value)
/* 219:    */   {
/* 220:359 */     int index = locateIndex(attIndex);
/* 221:361 */     if ((index >= 0) && (this.m_Indices[index] == attIndex))
/* 222:    */     {
/* 223:362 */       if (value != 0.0D)
/* 224:    */       {
/* 225:363 */         double[] tempValues = new double[this.m_AttValues.length];
/* 226:364 */         System.arraycopy(this.m_AttValues, 0, tempValues, 0, this.m_AttValues.length);
/* 227:365 */         tempValues[index] = value;
/* 228:366 */         this.m_AttValues = tempValues;
/* 229:    */       }
/* 230:    */       else
/* 231:    */       {
/* 232:368 */         double[] tempValues = new double[this.m_AttValues.length - 1];
/* 233:369 */         int[] tempIndices = new int[this.m_Indices.length - 1];
/* 234:370 */         System.arraycopy(this.m_AttValues, 0, tempValues, 0, index);
/* 235:371 */         System.arraycopy(this.m_Indices, 0, tempIndices, 0, index);
/* 236:372 */         System.arraycopy(this.m_AttValues, index + 1, tempValues, index, this.m_AttValues.length - index - 1);
/* 237:    */         
/* 238:374 */         System.arraycopy(this.m_Indices, index + 1, tempIndices, index, this.m_Indices.length - index - 1);
/* 239:    */         
/* 240:376 */         this.m_AttValues = tempValues;
/* 241:377 */         this.m_Indices = tempIndices;
/* 242:    */       }
/* 243:    */     }
/* 244:380 */     else if (value != 0.0D)
/* 245:    */     {
/* 246:381 */       double[] tempValues = new double[this.m_AttValues.length + 1];
/* 247:382 */       int[] tempIndices = new int[this.m_Indices.length + 1];
/* 248:383 */       System.arraycopy(this.m_AttValues, 0, tempValues, 0, index + 1);
/* 249:384 */       System.arraycopy(this.m_Indices, 0, tempIndices, 0, index + 1);
/* 250:385 */       tempIndices[(index + 1)] = attIndex;
/* 251:386 */       tempValues[(index + 1)] = value;
/* 252:387 */       System.arraycopy(this.m_AttValues, index + 1, tempValues, index + 2, this.m_AttValues.length - index - 1);
/* 253:    */       
/* 254:389 */       System.arraycopy(this.m_Indices, index + 1, tempIndices, index + 2, this.m_Indices.length - index - 1);
/* 255:    */       
/* 256:391 */       this.m_AttValues = tempValues;
/* 257:392 */       this.m_Indices = tempIndices;
/* 258:    */     }
/* 259:    */   }
/* 260:    */   
/* 261:    */   public void setValueSparse(int indexOfIndex, double value)
/* 262:    */   {
/* 263:410 */     if (value != 0.0D)
/* 264:    */     {
/* 265:411 */       double[] tempValues = new double[this.m_AttValues.length];
/* 266:412 */       System.arraycopy(this.m_AttValues, 0, tempValues, 0, this.m_AttValues.length);
/* 267:413 */       this.m_AttValues = tempValues;
/* 268:414 */       this.m_AttValues[indexOfIndex] = value;
/* 269:    */     }
/* 270:    */     else
/* 271:    */     {
/* 272:416 */       double[] tempValues = new double[this.m_AttValues.length - 1];
/* 273:417 */       int[] tempIndices = new int[this.m_Indices.length - 1];
/* 274:418 */       System.arraycopy(this.m_AttValues, 0, tempValues, 0, indexOfIndex);
/* 275:419 */       System.arraycopy(this.m_Indices, 0, tempIndices, 0, indexOfIndex);
/* 276:420 */       System.arraycopy(this.m_AttValues, indexOfIndex + 1, tempValues, indexOfIndex, this.m_AttValues.length - indexOfIndex - 1);
/* 277:    */       
/* 278:422 */       System.arraycopy(this.m_Indices, indexOfIndex + 1, tempIndices, indexOfIndex, this.m_Indices.length - indexOfIndex - 1);
/* 279:    */       
/* 280:424 */       this.m_AttValues = tempValues;
/* 281:425 */       this.m_Indices = tempIndices;
/* 282:    */     }
/* 283:    */   }
/* 284:    */   
/* 285:    */   public double[] toDoubleArray()
/* 286:    */   {
/* 287:437 */     double[] newValues = new double[this.m_NumAttributes];
/* 288:438 */     for (int i = 0; i < this.m_AttValues.length; i++) {
/* 289:439 */       newValues[this.m_Indices[i]] = this.m_AttValues[i];
/* 290:    */     }
/* 291:441 */     return newValues;
/* 292:    */   }
/* 293:    */   
/* 294:    */   public String toStringNoWeight()
/* 295:    */   {
/* 296:453 */     return toStringNoWeight(AbstractInstance.s_numericAfterDecimalPoint);
/* 297:    */   }
/* 298:    */   
/* 299:    */   public String toStringNoWeight(int afterDecimalPoint)
/* 300:    */   {
/* 301:469 */     StringBuilder text = new StringBuilder();
/* 302:    */     
/* 303:471 */     text.append('{');
/* 304:472 */     String prefix = "";
/* 305:473 */     int sparseIndex = 0;
/* 306:474 */     for (int i = 0; i < this.m_NumAttributes; i++)
/* 307:    */     {
/* 308:477 */       if (text.length() > 1) {
/* 309:478 */         prefix = ",";
/* 310:    */       }
/* 311:481 */       double value = 0.0D;
/* 312:    */       try
/* 313:    */       {
/* 314:486 */         if ((sparseIndex < this.m_Indices.length) && (this.m_Indices[sparseIndex] == i))
/* 315:    */         {
/* 316:487 */           value = this.m_AttValues[(sparseIndex++)];
/* 317:488 */           if (Utils.isMissingValue(value))
/* 318:    */           {
/* 319:489 */             text.append(prefix).append(i).append(" ?");
/* 320:490 */             continue;
/* 321:    */           }
/* 322:    */         }
/* 323:495 */         if (this.m_Dataset == null)
/* 324:    */         {
/* 325:496 */           if (value != 0.0D) {
/* 326:497 */             text.append(prefix).append(i).append(" ").append(Utils.doubleToString(value, afterDecimalPoint));
/* 327:    */           }
/* 328:    */         }
/* 329:    */         else
/* 330:    */         {
/* 331:500 */           Attribute att = this.m_Dataset.attribute(i);
/* 332:501 */           if (att.isString()) {
/* 333:502 */             text.append(prefix).append(i).append(" ").append(Utils.quote(att.value((int)value)));
/* 334:503 */           } else if (att.isRelationValued()) {
/* 335:504 */             text.append(prefix).append(i).append(" ").append(Utils.quote(att.relation((int)value).stringWithoutHeader()));
/* 336:505 */           } else if (value != 0.0D) {
/* 337:506 */             if (att.isNominal()) {
/* 338:507 */               text.append(prefix).append(i).append(" ").append(Utils.quote(att.value((int)value)));
/* 339:508 */             } else if (att.isDate()) {
/* 340:509 */               text.append(prefix).append(i).append(" ").append(Utils.quote(att.formatDate(value)));
/* 341:    */             } else {
/* 342:511 */               text.append(prefix).append(i).append(" ").append(Utils.doubleToString(value, afterDecimalPoint));
/* 343:    */             }
/* 344:    */           }
/* 345:    */         }
/* 346:    */       }
/* 347:    */       catch (Exception e)
/* 348:    */       {
/* 349:517 */         e.printStackTrace();
/* 350:518 */         System.err.println(new Instances(this.m_Dataset, 0) + "\n" + "Att: " + i + " Val: " + value);
/* 351:519 */         throw new Error("This should never happen!");
/* 352:    */       }
/* 353:    */     }
/* 354:524 */     text.append('}');
/* 355:    */     
/* 356:526 */     return text.toString();
/* 357:    */   }
/* 358:    */   
/* 359:    */   public double value(int attIndex)
/* 360:    */   {
/* 361:540 */     int index = locateIndex(attIndex);
/* 362:541 */     if ((index >= 0) && (this.m_Indices[index] == attIndex)) {
/* 363:542 */       return this.m_AttValues[index];
/* 364:    */     }
/* 365:544 */     return 0.0D;
/* 366:    */   }
/* 367:    */   
/* 368:    */   protected void forceDeleteAttributeAt(int position)
/* 369:    */   {
/* 370:556 */     int index = locateIndex(position);
/* 371:    */     
/* 372:558 */     this.m_NumAttributes -= 1;
/* 373:559 */     if ((index >= 0) && (this.m_Indices[index] == position))
/* 374:    */     {
/* 375:560 */       int[] tempIndices = new int[this.m_Indices.length - 1];
/* 376:561 */       double[] tempValues = new double[this.m_AttValues.length - 1];
/* 377:562 */       System.arraycopy(this.m_Indices, 0, tempIndices, 0, index);
/* 378:563 */       System.arraycopy(this.m_AttValues, 0, tempValues, 0, index);
/* 379:564 */       for (int i = index; i < this.m_Indices.length - 1; i++)
/* 380:    */       {
/* 381:565 */         tempIndices[i] = (this.m_Indices[(i + 1)] - 1);
/* 382:566 */         tempValues[i] = this.m_AttValues[(i + 1)];
/* 383:    */       }
/* 384:568 */       this.m_Indices = tempIndices;
/* 385:569 */       this.m_AttValues = tempValues;
/* 386:    */     }
/* 387:    */     else
/* 388:    */     {
/* 389:571 */       int[] tempIndices = new int[this.m_Indices.length];
/* 390:572 */       double[] tempValues = new double[this.m_AttValues.length];
/* 391:573 */       System.arraycopy(this.m_Indices, 0, tempIndices, 0, index + 1);
/* 392:574 */       System.arraycopy(this.m_AttValues, 0, tempValues, 0, index + 1);
/* 393:575 */       for (int i = index + 1; i < this.m_Indices.length; i++)
/* 394:    */       {
/* 395:576 */         tempIndices[i] = (this.m_Indices[i] - 1);
/* 396:577 */         tempValues[i] = this.m_AttValues[i];
/* 397:    */       }
/* 398:579 */       this.m_Indices = tempIndices;
/* 399:580 */       this.m_AttValues = tempValues;
/* 400:    */     }
/* 401:    */   }
/* 402:    */   
/* 403:    */   protected void forceInsertAttributeAt(int position)
/* 404:    */   {
/* 405:593 */     int index = locateIndex(position);
/* 406:    */     
/* 407:595 */     this.m_NumAttributes += 1;
/* 408:596 */     if ((index >= 0) && (this.m_Indices[index] == position))
/* 409:    */     {
/* 410:597 */       int[] tempIndices = new int[this.m_Indices.length + 1];
/* 411:598 */       double[] tempValues = new double[this.m_AttValues.length + 1];
/* 412:599 */       System.arraycopy(this.m_Indices, 0, tempIndices, 0, index);
/* 413:600 */       System.arraycopy(this.m_AttValues, 0, tempValues, 0, index);
/* 414:601 */       tempIndices[index] = position;
/* 415:602 */       tempValues[index] = Utils.missingValue();
/* 416:603 */       for (int i = index; i < this.m_Indices.length; i++)
/* 417:    */       {
/* 418:604 */         tempIndices[(i + 1)] = (this.m_Indices[i] + 1);
/* 419:605 */         tempValues[(i + 1)] = this.m_AttValues[i];
/* 420:    */       }
/* 421:607 */       this.m_Indices = tempIndices;
/* 422:608 */       this.m_AttValues = tempValues;
/* 423:    */     }
/* 424:    */     else
/* 425:    */     {
/* 426:610 */       int[] tempIndices = new int[this.m_Indices.length + 1];
/* 427:611 */       double[] tempValues = new double[this.m_AttValues.length + 1];
/* 428:612 */       System.arraycopy(this.m_Indices, 0, tempIndices, 0, index + 1);
/* 429:613 */       System.arraycopy(this.m_AttValues, 0, tempValues, 0, index + 1);
/* 430:614 */       tempIndices[(index + 1)] = position;
/* 431:615 */       tempValues[(index + 1)] = Utils.missingValue();
/* 432:616 */       for (int i = index + 1; i < this.m_Indices.length; i++)
/* 433:    */       {
/* 434:617 */         tempIndices[(i + 1)] = (this.m_Indices[i] + 1);
/* 435:618 */         tempValues[(i + 1)] = this.m_AttValues[i];
/* 436:    */       }
/* 437:620 */       this.m_Indices = tempIndices;
/* 438:621 */       this.m_AttValues = tempValues;
/* 439:    */     }
/* 440:    */   }
/* 441:    */   
/* 442:    */   protected SparseInstance() {}
/* 443:    */   
/* 444:    */   public static void main(String[] options)
/* 445:    */   {
/* 446:    */     try
/* 447:    */     {
/* 448:639 */       Attribute length = new Attribute("length");
/* 449:640 */       Attribute weight = new Attribute("weight");
/* 450:    */       
/* 451:    */ 
/* 452:643 */       ArrayList<String> my_nominal_values = new ArrayList(3);
/* 453:644 */       my_nominal_values.add("first");
/* 454:645 */       my_nominal_values.add("second");
/* 455:646 */       my_nominal_values.add("third");
/* 456:    */       
/* 457:    */ 
/* 458:649 */       Attribute position = new Attribute("position", my_nominal_values);
/* 459:    */       
/* 460:    */ 
/* 461:652 */       ArrayList<Attribute> attributes = new ArrayList(3);
/* 462:653 */       attributes.add(length);
/* 463:654 */       attributes.add(weight);
/* 464:655 */       attributes.add(position);
/* 465:    */       
/* 466:    */ 
/* 467:658 */       Instances race = new Instances("race", attributes, 0);
/* 468:    */       
/* 469:    */ 
/* 470:661 */       race.setClassIndex(position.index());
/* 471:    */       
/* 472:    */ 
/* 473:664 */       SparseInstance inst = new SparseInstance(3);
/* 474:    */       
/* 475:    */ 
/* 476:    */ 
/* 477:668 */       inst.setValue(length, 5.3D);
/* 478:669 */       inst.setValue(weight, 300.0D);
/* 479:670 */       inst.setValue(position, "first");
/* 480:    */       
/* 481:    */ 
/* 482:673 */       inst.setDataset(race);
/* 483:    */       
/* 484:    */ 
/* 485:676 */       System.out.println("The instance: " + inst);
/* 486:    */       
/* 487:    */ 
/* 488:679 */       System.out.println("First attribute: " + inst.attribute(0));
/* 489:    */       
/* 490:    */ 
/* 491:682 */       System.out.println("Class attribute: " + inst.classAttribute());
/* 492:    */       
/* 493:    */ 
/* 494:685 */       System.out.println("Class index: " + inst.classIndex());
/* 495:    */       
/* 496:    */ 
/* 497:688 */       System.out.println("Class is missing: " + inst.classIsMissing());
/* 498:    */       
/* 499:    */ 
/* 500:691 */       System.out.println("Class value (internal format): " + inst.classValue());
/* 501:    */       
/* 502:    */ 
/* 503:694 */       SparseInstance copy = (SparseInstance)inst.copy();
/* 504:695 */       System.out.println("Shallow copy: " + copy);
/* 505:    */       
/* 506:    */ 
/* 507:698 */       copy.setDataset(inst.dataset());
/* 508:699 */       System.out.println("Shallow copy with dataset set: " + copy);
/* 509:    */       
/* 510:    */ 
/* 511:702 */       System.out.print("All stored values in internal format: ");
/* 512:703 */       for (int i = 0; i < inst.numValues(); i++)
/* 513:    */       {
/* 514:704 */         if (i > 0) {
/* 515:705 */           System.out.print(",");
/* 516:    */         }
/* 517:707 */         System.out.print(inst.valueSparse(i));
/* 518:    */       }
/* 519:709 */       System.out.println();
/* 520:    */       
/* 521:    */ 
/* 522:712 */       System.out.print("All values set to zero: ");
/* 523:713 */       while (inst.numValues() > 0) {
/* 524:714 */         inst.setValueSparse(0, 0.0D);
/* 525:    */       }
/* 526:716 */       for (int i = 0; i < inst.numValues(); i++)
/* 527:    */       {
/* 528:717 */         if (i > 0) {
/* 529:718 */           System.out.print(",");
/* 530:    */         }
/* 531:720 */         System.out.print(inst.valueSparse(i));
/* 532:    */       }
/* 533:722 */       System.out.println();
/* 534:    */       
/* 535:    */ 
/* 536:725 */       System.out.print("All values set to one: ");
/* 537:726 */       for (int i = 0; i < inst.numAttributes(); i++) {
/* 538:727 */         inst.setValue(i, 1.0D);
/* 539:    */       }
/* 540:729 */       for (int i = 0; i < inst.numValues(); i++)
/* 541:    */       {
/* 542:730 */         if (i > 0) {
/* 543:731 */           System.out.print(",");
/* 544:    */         }
/* 545:733 */         System.out.print(inst.valueSparse(i));
/* 546:    */       }
/* 547:735 */       System.out.println();
/* 548:    */       
/* 549:    */ 
/* 550:738 */       copy.setDataset(null);
/* 551:739 */       copy.deleteAttributeAt(0);
/* 552:740 */       copy.insertAttributeAt(0);
/* 553:741 */       copy.setDataset(inst.dataset());
/* 554:742 */       System.out.println("Copy with first attribute deleted and inserted: " + copy);
/* 555:    */       
/* 556:    */ 
/* 557:    */ 
/* 558:746 */       copy.setDataset(null);
/* 559:747 */       copy.deleteAttributeAt(1);
/* 560:748 */       copy.insertAttributeAt(1);
/* 561:749 */       copy.setDataset(inst.dataset());
/* 562:750 */       System.out.println("Copy with second attribute deleted and inserted: " + copy);
/* 563:    */       
/* 564:    */ 
/* 565:    */ 
/* 566:754 */       copy.setDataset(null);
/* 567:755 */       copy.deleteAttributeAt(2);
/* 568:756 */       copy.insertAttributeAt(2);
/* 569:757 */       copy.setDataset(inst.dataset());
/* 570:758 */       System.out.println("Copy with third attribute deleted and inserted: " + copy);
/* 571:    */       
/* 572:    */ 
/* 573:    */ 
/* 574:762 */       System.out.println("Enumerating attributes (leaving out class):");
/* 575:763 */       Enumeration<Attribute> enu = inst.enumerateAttributes();
/* 576:764 */       while (enu.hasMoreElements())
/* 577:    */       {
/* 578:765 */         Attribute att = (Attribute)enu.nextElement();
/* 579:766 */         System.out.println(att);
/* 580:    */       }
/* 581:770 */       System.out.println("Header of original and copy equivalent: " + inst.equalHeaders(copy));
/* 582:    */       
/* 583:    */ 
/* 584:    */ 
/* 585:774 */       System.out.println("Length of copy missing: " + copy.isMissing(length));
/* 586:775 */       System.out.println("Weight of copy missing: " + copy.isMissing(weight.index()));
/* 587:    */       
/* 588:777 */       System.out.println("Length of copy missing: " + Utils.isMissingValue(copy.value(length)));
/* 589:    */       
/* 590:    */ 
/* 591:    */ 
/* 592:781 */       System.out.println("Number of attributes: " + copy.numAttributes());
/* 593:782 */       System.out.println("Number of classes: " + copy.numClasses());
/* 594:    */       
/* 595:    */ 
/* 596:785 */       double[] meansAndModes = { 2.0D, 3.0D, 0.0D };
/* 597:786 */       copy.replaceMissingValues(meansAndModes);
/* 598:787 */       System.out.println("Copy with missing value replaced: " + copy);
/* 599:    */       
/* 600:    */ 
/* 601:790 */       copy.setClassMissing();
/* 602:791 */       System.out.println("Copy with missing class: " + copy);
/* 603:792 */       copy.setClassValue(0.0D);
/* 604:793 */       System.out.println("Copy with class value set to first value: " + copy);
/* 605:794 */       copy.setClassValue("third");
/* 606:795 */       System.out.println("Copy with class value set to \"third\": " + copy);
/* 607:796 */       copy.setMissing(1);
/* 608:797 */       System.out.println("Copy with second attribute set to be missing: " + copy);
/* 609:    */       
/* 610:799 */       copy.setMissing(length);
/* 611:800 */       System.out.println("Copy with length set to be missing: " + copy);
/* 612:801 */       copy.setValue(0, 0.0D);
/* 613:802 */       System.out.println("Copy with first attribute set to 0: " + copy);
/* 614:803 */       copy.setValue(weight, 1.0D);
/* 615:804 */       System.out.println("Copy with weight attribute set to 1: " + copy);
/* 616:805 */       copy.setValue(position, "second");
/* 617:806 */       System.out.println("Copy with position set to \"second\": " + copy);
/* 618:807 */       copy.setValue(2, "first");
/* 619:808 */       System.out.println("Copy with last attribute set to \"first\": " + copy);
/* 620:809 */       System.out.println("Current weight of instance copy: " + copy.weight());
/* 621:810 */       copy.setWeight(2.0D);
/* 622:811 */       System.out.println("Current weight of instance copy (set to 2): " + copy.weight());
/* 623:    */       
/* 624:813 */       System.out.println("Last value of copy: " + copy.toString(2));
/* 625:814 */       System.out.println("Value of position for copy: " + copy.toString(position));
/* 626:    */       
/* 627:816 */       System.out.println("Last value of copy (internal format): " + copy.value(2));
/* 628:    */       
/* 629:818 */       System.out.println("Value of position for copy (internal format): " + copy.value(position));
/* 630:    */     }
/* 631:    */     catch (Exception e)
/* 632:    */     {
/* 633:821 */       e.printStackTrace();
/* 634:    */     }
/* 635:    */   }
/* 636:    */   
/* 637:    */   public String getRevision()
/* 638:    */   {
/* 639:832 */     return RevisionUtils.extract("$Revision: 12472 $");
/* 640:    */   }
/* 641:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.SparseInstance
 * JD-Core Version:    0.7.0.1
 */