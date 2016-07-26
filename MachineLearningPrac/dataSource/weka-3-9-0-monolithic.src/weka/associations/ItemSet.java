/*   1:    */ package weka.associations;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Hashtable;
/*   7:    */ import weka.core.Attribute;
/*   8:    */ import weka.core.Instance;
/*   9:    */ import weka.core.Instances;
/*  10:    */ import weka.core.RevisionHandler;
/*  11:    */ import weka.core.RevisionUtils;
/*  12:    */ import weka.core.SparseInstance;
/*  13:    */ import weka.core.WekaEnumeration;
/*  14:    */ 
/*  15:    */ public class ItemSet
/*  16:    */   implements Serializable, RevisionHandler
/*  17:    */ {
/*  18:    */   private static final long serialVersionUID = 2724000045282835791L;
/*  19:    */   protected int[] m_items;
/*  20:    */   protected int m_counter;
/*  21:    */   protected int m_secondaryCounter;
/*  22:    */   protected int m_totalTransactions;
/*  23:    */   
/*  24:    */   public ItemSet(int totalTrans)
/*  25:    */   {
/*  26: 72 */     this.m_totalTransactions = totalTrans;
/*  27:    */   }
/*  28:    */   
/*  29:    */   public ItemSet(int totalTrans, int[] array)
/*  30:    */   {
/*  31: 83 */     this.m_totalTransactions = totalTrans;
/*  32: 84 */     this.m_items = array;
/*  33: 85 */     this.m_counter = 1;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public ItemSet(int[] array)
/*  37:    */   {
/*  38: 95 */     this.m_items = array;
/*  39: 96 */     this.m_counter = 0;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public boolean containedByTreatZeroAsMissing(Instance instance)
/*  43:    */   {
/*  44:    */     int numInstVals;
/*  45:    */     int p1;
/*  46:    */     int p2;
/*  47:107 */     if ((instance instanceof SparseInstance))
/*  48:    */     {
/*  49:108 */       numInstVals = instance.numValues();
/*  50:109 */       int numItemSetVals = this.m_items.length;
/*  51:    */       
/*  52:111 */       p1 = 0;
/*  53:111 */       for (p2 = 0; (p1 < numInstVals) || (p2 < numItemSetVals);)
/*  54:    */       {
/*  55:112 */         int instIndex = 2147483647;
/*  56:113 */         if (p1 < numInstVals) {
/*  57:114 */           instIndex = instance.index(p1);
/*  58:    */         }
/*  59:116 */         int itemIndex = p2;
/*  60:118 */         if (this.m_items[itemIndex] > -1)
/*  61:    */         {
/*  62:119 */           if (itemIndex != instIndex) {
/*  63:120 */             return false;
/*  64:    */           }
/*  65:122 */           if (instance.isMissingSparse(p1)) {
/*  66:123 */             return false;
/*  67:    */           }
/*  68:125 */           if (this.m_items[itemIndex] != (int)instance.valueSparse(p1)) {
/*  69:126 */             return false;
/*  70:    */           }
/*  71:130 */           p1++;
/*  72:131 */           p2++;
/*  73:    */         }
/*  74:133 */         else if (itemIndex < instIndex)
/*  75:    */         {
/*  76:134 */           p2++;
/*  77:    */         }
/*  78:135 */         else if (itemIndex == instIndex)
/*  79:    */         {
/*  80:136 */           p2++;
/*  81:137 */           p1++;
/*  82:    */         }
/*  83:    */       }
/*  84:    */     }
/*  85:    */     else
/*  86:    */     {
/*  87:142 */       for (int i = 0; i < instance.numAttributes(); i++) {
/*  88:143 */         if (this.m_items[i] > -1)
/*  89:    */         {
/*  90:144 */           if ((instance.isMissing(i)) || ((int)instance.value(i) == 0)) {
/*  91:145 */             return false;
/*  92:    */           }
/*  93:147 */           if (this.m_items[i] != (int)instance.value(i)) {
/*  94:148 */             return false;
/*  95:    */           }
/*  96:    */         }
/*  97:    */       }
/*  98:    */     }
/*  99:154 */     return true;
/* 100:    */   }
/* 101:    */   
/* 102:    */   public boolean containedBy(Instance instance)
/* 103:    */   {
/* 104:164 */     for (int i = 0; i < instance.numAttributes(); i++) {
/* 105:165 */       if (this.m_items[i] > -1)
/* 106:    */       {
/* 107:166 */         if (instance.isMissing(i)) {
/* 108:167 */           return false;
/* 109:    */         }
/* 110:169 */         if (this.m_items[i] != (int)instance.value(i)) {
/* 111:170 */           return false;
/* 112:    */         }
/* 113:    */       }
/* 114:    */     }
/* 115:175 */     return true;
/* 116:    */   }
/* 117:    */   
/* 118:    */   public static ArrayList<Object> deleteItemSets(ArrayList<Object> itemSets, int minSupport, int maxSupport)
/* 119:    */   {
/* 120:189 */     ArrayList<Object> newVector = new ArrayList(itemSets.size());
/* 121:191 */     for (int i = 0; i < itemSets.size(); i++)
/* 122:    */     {
/* 123:192 */       ItemSet current = (ItemSet)itemSets.get(i);
/* 124:193 */       if ((current.m_counter >= minSupport) && (current.m_counter <= maxSupport)) {
/* 125:195 */         newVector.add(current);
/* 126:    */       }
/* 127:    */     }
/* 128:198 */     return newVector;
/* 129:    */   }
/* 130:    */   
/* 131:    */   public boolean equals(Object itemSet)
/* 132:    */   {
/* 133:210 */     if ((itemSet == null) || (!itemSet.getClass().equals(getClass()))) {
/* 134:211 */       return false;
/* 135:    */     }
/* 136:213 */     if (this.m_items.length != ((ItemSet)itemSet).m_items.length) {
/* 137:214 */       return false;
/* 138:    */     }
/* 139:216 */     for (int i = 0; i < this.m_items.length; i++) {
/* 140:217 */       if (this.m_items[i] != ((ItemSet)itemSet).m_items[i]) {
/* 141:218 */         return false;
/* 142:    */       }
/* 143:    */     }
/* 144:221 */     return true;
/* 145:    */   }
/* 146:    */   
/* 147:    */   public static Hashtable<ItemSet, Integer> getHashtable(ArrayList<Object> itemSets, int initialSize)
/* 148:    */   {
/* 149:234 */     Hashtable<ItemSet, Integer> hashtable = new Hashtable(initialSize);
/* 150:237 */     for (int i = 0; i < itemSets.size(); i++)
/* 151:    */     {
/* 152:238 */       ItemSet current = (ItemSet)itemSets.get(i);
/* 153:239 */       hashtable.put(current, new Integer(current.m_counter));
/* 154:    */     }
/* 155:241 */     return hashtable;
/* 156:    */   }
/* 157:    */   
/* 158:    */   public int hashCode()
/* 159:    */   {
/* 160:252 */     long result = 0L;
/* 161:254 */     for (int i = this.m_items.length - 1; i >= 0; i--) {
/* 162:255 */       result += i * this.m_items[i];
/* 163:    */     }
/* 164:257 */     return (int)result;
/* 165:    */   }
/* 166:    */   
/* 167:    */   public static ArrayList<Object> mergeAllItemSets(ArrayList<Object> itemSets, int size, int totalTrans)
/* 168:    */   {
/* 169:272 */     ArrayList<Object> newVector = new ArrayList();
/* 170:    */     label269:
/* 171:276 */     for (int i = 0; i < itemSets.size(); i++)
/* 172:    */     {
/* 173:277 */       ItemSet first = (ItemSet)itemSets.get(i);
/* 174:278 */       for (int j = i + 1; j < itemSets.size(); j++)
/* 175:    */       {
/* 176:279 */         ItemSet second = (ItemSet)itemSets.get(j);
/* 177:280 */         ItemSet result = new ItemSet(totalTrans);
/* 178:281 */         result.m_items = new int[first.m_items.length];
/* 179:    */         
/* 180:    */ 
/* 181:284 */         int numFound = 0;
/* 182:285 */         int k = 0;
/* 183:286 */         while (numFound < size)
/* 184:    */         {
/* 185:287 */           if (first.m_items[k] != second.m_items[k]) {
/* 186:    */             break label269;
/* 187:    */           }
/* 188:288 */           if (first.m_items[k] != -1) {
/* 189:289 */             numFound++;
/* 190:    */           }
/* 191:291 */           result.m_items[k] = first.m_items[k];
/* 192:    */           
/* 193:    */ 
/* 194:    */ 
/* 195:295 */           k++;
/* 196:    */         }
/* 197:299 */         while ((k < first.m_items.length) && (
/* 198:300 */           (first.m_items[k] == -1) || (second.m_items[k] == -1)))
/* 199:    */         {
/* 200:303 */           if (first.m_items[k] != -1) {
/* 201:304 */             result.m_items[k] = first.m_items[k];
/* 202:    */           } else {
/* 203:306 */             result.m_items[k] = second.m_items[k];
/* 204:    */           }
/* 205:309 */           k++;
/* 206:    */         }
/* 207:311 */         if (k == first.m_items.length)
/* 208:    */         {
/* 209:312 */           result.m_counter = 0;
/* 210:    */           
/* 211:314 */           newVector.add(result);
/* 212:    */         }
/* 213:    */       }
/* 214:    */     }
/* 215:318 */     return newVector;
/* 216:    */   }
/* 217:    */   
/* 218:    */   public static ArrayList<Object> pruneItemSets(ArrayList<Object> toPrune, Hashtable<ItemSet, Integer> kMinusOne)
/* 219:    */   {
/* 220:331 */     ArrayList<Object> newVector = new ArrayList(toPrune.size());
/* 221:334 */     for (int i = 0; i < toPrune.size(); i++)
/* 222:    */     {
/* 223:335 */       ItemSet current = (ItemSet)toPrune.get(i);
/* 224:336 */       for (int j = 0; j < current.m_items.length; j++) {
/* 225:337 */         if (current.m_items[j] != -1)
/* 226:    */         {
/* 227:338 */           int help = current.m_items[j];
/* 228:339 */           current.m_items[j] = -1;
/* 229:340 */           if (kMinusOne.get(current) == null)
/* 230:    */           {
/* 231:341 */             current.m_items[j] = help;
/* 232:342 */             break;
/* 233:    */           }
/* 234:344 */           current.m_items[j] = help;
/* 235:    */         }
/* 236:    */       }
/* 237:348 */       if (j == current.m_items.length) {
/* 238:349 */         newVector.add(current);
/* 239:    */       }
/* 240:    */     }
/* 241:352 */     return newVector;
/* 242:    */   }
/* 243:    */   
/* 244:    */   public static void pruneRules(ArrayList<Object>[] rules, double minConfidence)
/* 245:    */   {
/* 246:365 */     ArrayList<Object> newPremises = new ArrayList(rules[0].size());ArrayList<Object> newConsequences = new ArrayList(rules[1].size());
/* 247:366 */     ArrayList<Object> newConf = new ArrayList(rules[2].size());
/* 248:    */     
/* 249:368 */     ArrayList<Object> newLift = null;ArrayList<Object> newLev = null;ArrayList<Object> newConv = null;
/* 250:369 */     if (rules.length > 3)
/* 251:    */     {
/* 252:370 */       newLift = new ArrayList(rules[3].size());
/* 253:371 */       newLev = new ArrayList(rules[4].size());
/* 254:372 */       newConv = new ArrayList(rules[5].size());
/* 255:    */     }
/* 256:375 */     for (int i = 0; i < rules[0].size(); i++) {
/* 257:376 */       if (((Double)rules[2].get(i)).doubleValue() >= minConfidence)
/* 258:    */       {
/* 259:377 */         newPremises.add(rules[0].get(i));
/* 260:378 */         newConsequences.add(rules[1].get(i));
/* 261:379 */         newConf.add(rules[2].get(i));
/* 262:381 */         if (rules.length > 3)
/* 263:    */         {
/* 264:382 */           newLift.add(rules[3].get(i));
/* 265:383 */           newLev.add(rules[4].get(i));
/* 266:384 */           newConv.add(rules[5].get(i));
/* 267:    */         }
/* 268:    */       }
/* 269:    */     }
/* 270:388 */     rules[0] = newPremises;
/* 271:389 */     rules[1] = newConsequences;
/* 272:390 */     rules[2] = newConf;
/* 273:392 */     if (rules.length > 3)
/* 274:    */     {
/* 275:393 */       rules[3] = newLift;
/* 276:394 */       rules[4] = newLev;
/* 277:395 */       rules[5] = newConv;
/* 278:    */     }
/* 279:    */   }
/* 280:    */   
/* 281:    */   public static ArrayList<Object> singletons(Instances instances)
/* 282:    */     throws Exception
/* 283:    */   {
/* 284:411 */     ArrayList<Object> setOfItemSets = new ArrayList();
/* 285:414 */     for (int i = 0; i < instances.numAttributes(); i++)
/* 286:    */     {
/* 287:415 */       if (instances.attribute(i).isNumeric()) {
/* 288:416 */         throw new Exception("Can't handle numeric attributes!");
/* 289:    */       }
/* 290:418 */       for (int j = 0; j < instances.attribute(i).numValues(); j++)
/* 291:    */       {
/* 292:419 */         ItemSet current = new ItemSet(instances.numInstances());
/* 293:420 */         current.m_items = new int[instances.numAttributes()];
/* 294:421 */         for (int k = 0; k < instances.numAttributes(); k++) {
/* 295:422 */           current.m_items[k] = -1;
/* 296:    */         }
/* 297:424 */         current.m_items[i] = j;
/* 298:    */         
/* 299:426 */         setOfItemSets.add(current);
/* 300:    */       }
/* 301:    */     }
/* 302:429 */     return setOfItemSets;
/* 303:    */   }
/* 304:    */   
/* 305:    */   public int support()
/* 306:    */   {
/* 307:439 */     return this.m_counter;
/* 308:    */   }
/* 309:    */   
/* 310:    */   public String toString(Instances instances)
/* 311:    */   {
/* 312:450 */     StringBuffer text = new StringBuffer();
/* 313:452 */     for (int i = 0; i < instances.numAttributes(); i++) {
/* 314:453 */       if (this.m_items[i] != -1)
/* 315:    */       {
/* 316:454 */         text.append(instances.attribute(i).name() + '=');
/* 317:455 */         text.append(instances.attribute(i).value(this.m_items[i]) + ' ');
/* 318:    */       }
/* 319:    */     }
/* 320:458 */     text.append(this.m_counter);
/* 321:459 */     return text.toString();
/* 322:    */   }
/* 323:    */   
/* 324:    */   public String toString(Instances instances, char outerDelim, char innerDelim)
/* 325:    */   {
/* 326:472 */     StringBuffer text = new StringBuffer();
/* 327:474 */     for (int i = 0; i < instances.numAttributes(); i++) {
/* 328:475 */       if (this.m_items[i] != -1) {
/* 329:476 */         text.append(instances.attribute(i).name()).append('=').append(instances.attribute(i).value(this.m_items[i])).append(innerDelim);
/* 330:    */       }
/* 331:    */     }
/* 332:481 */     int n = text.length();
/* 333:482 */     if (n > 0) {
/* 334:483 */       text.setCharAt(n - 1, outerDelim);
/* 335:485 */     } else if ((outerDelim != ' ') || (innerDelim != ' ')) {
/* 336:486 */       text.append(outerDelim);
/* 337:    */     }
/* 338:489 */     text.append(this.m_counter);
/* 339:490 */     return text.toString();
/* 340:    */   }
/* 341:    */   
/* 342:    */   public void upDateCounter(Instance instance)
/* 343:    */   {
/* 344:500 */     if (containedBy(instance)) {
/* 345:501 */       this.m_counter += 1;
/* 346:    */     }
/* 347:    */   }
/* 348:    */   
/* 349:    */   public void updateCounterTreatZeroAsMissing(Instance instance)
/* 350:    */   {
/* 351:511 */     if (containedByTreatZeroAsMissing(instance)) {
/* 352:512 */       this.m_counter += 1;
/* 353:    */     }
/* 354:    */   }
/* 355:    */   
/* 356:    */   public static void upDateCounters(ArrayList<Object> itemSets, Instances instances)
/* 357:    */   {
/* 358:525 */     for (int i = 0; i < instances.numInstances(); i++)
/* 359:    */     {
/* 360:526 */       Enumeration<Object> enu = new WekaEnumeration(itemSets);
/* 361:527 */       while (enu.hasMoreElements()) {
/* 362:528 */         ((ItemSet)enu.nextElement()).upDateCounter(instances.instance(i));
/* 363:    */       }
/* 364:    */     }
/* 365:    */   }
/* 366:    */   
/* 367:    */   public static void upDateCountersTreatZeroAsMissing(ArrayList<Object> itemSets, Instances instances)
/* 368:    */   {
/* 369:541 */     for (int i = 0; i < instances.numInstances(); i++)
/* 370:    */     {
/* 371:542 */       Enumeration<Object> enu = new WekaEnumeration(itemSets);
/* 372:543 */       while (enu.hasMoreElements()) {
/* 373:544 */         ((ItemSet)enu.nextElement()).updateCounterTreatZeroAsMissing(instances.instance(i));
/* 374:    */       }
/* 375:    */     }
/* 376:    */   }
/* 377:    */   
/* 378:    */   public int counter()
/* 379:    */   {
/* 380:557 */     return this.m_counter;
/* 381:    */   }
/* 382:    */   
/* 383:    */   public int[] items()
/* 384:    */   {
/* 385:567 */     return this.m_items;
/* 386:    */   }
/* 387:    */   
/* 388:    */   public int itemAt(int k)
/* 389:    */   {
/* 390:578 */     return this.m_items[k];
/* 391:    */   }
/* 392:    */   
/* 393:    */   public void setCounter(int count)
/* 394:    */   {
/* 395:588 */     this.m_counter = count;
/* 396:    */   }
/* 397:    */   
/* 398:    */   public void setItem(int[] items)
/* 399:    */   {
/* 400:598 */     this.m_items = items;
/* 401:    */   }
/* 402:    */   
/* 403:    */   public void setItemAt(int value, int k)
/* 404:    */   {
/* 405:609 */     this.m_items[k] = value;
/* 406:    */   }
/* 407:    */   
/* 408:    */   public String getRevision()
/* 409:    */   {
/* 410:619 */     return RevisionUtils.extract("$Revision: 12014 $");
/* 411:    */   }
/* 412:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.associations.ItemSet
 * JD-Core Version:    0.7.0.1
 */