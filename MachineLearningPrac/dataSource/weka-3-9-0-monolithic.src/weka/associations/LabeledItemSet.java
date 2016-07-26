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
/*  12:    */ import weka.core.WekaEnumeration;
/*  13:    */ 
/*  14:    */ public class LabeledItemSet
/*  15:    */   extends ItemSet
/*  16:    */   implements Serializable, RevisionHandler
/*  17:    */ {
/*  18:    */   private static final long serialVersionUID = 4158771925518299903L;
/*  19:    */   protected int m_classLabel;
/*  20:    */   protected int m_ruleSupCounter;
/*  21:    */   
/*  22:    */   public LabeledItemSet(int totalTrans, int classLabel)
/*  23:    */   {
/*  24: 68 */     super(totalTrans);
/*  25: 69 */     this.m_classLabel = classLabel;
/*  26:    */   }
/*  27:    */   
/*  28:    */   public static ArrayList<Object> deleteItemSets(ArrayList<Object> itemSets, int minSupport, int maxSupport)
/*  29:    */   {
/*  30: 84 */     ArrayList<Object> newVector = new ArrayList(itemSets.size());
/*  31: 86 */     for (int i = 0; i < itemSets.size(); i++)
/*  32:    */     {
/*  33: 87 */       LabeledItemSet current = (LabeledItemSet)itemSets.get(i);
/*  34: 88 */       if ((current.m_ruleSupCounter >= minSupport) && (current.m_ruleSupCounter <= maxSupport)) {
/*  35: 90 */         newVector.add(current);
/*  36:    */       }
/*  37:    */     }
/*  38: 93 */     return newVector;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public final boolean equals(Object itemSet)
/*  42:    */   {
/*  43:105 */     if (!equalCondset(itemSet)) {
/*  44:106 */       return false;
/*  45:    */     }
/*  46:108 */     if (this.m_classLabel != ((LabeledItemSet)itemSet).m_classLabel) {
/*  47:109 */       return false;
/*  48:    */     }
/*  49:112 */     return true;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public final boolean equalCondset(Object itemSet)
/*  53:    */   {
/*  54:123 */     if ((itemSet == null) || (!itemSet.getClass().equals(getClass()))) {
/*  55:124 */       return false;
/*  56:    */     }
/*  57:126 */     if (this.m_items.length != ((ItemSet)itemSet).items().length) {
/*  58:127 */       return false;
/*  59:    */     }
/*  60:129 */     for (int i = 0; i < this.m_items.length; i++) {
/*  61:130 */       if (this.m_items[i] != ((ItemSet)itemSet).itemAt(i)) {
/*  62:131 */         return false;
/*  63:    */       }
/*  64:    */     }
/*  65:134 */     return true;
/*  66:    */   }
/*  67:    */   
/*  68:    */   public static Hashtable<ItemSet, Integer> getHashtable(ArrayList<Object> itemSets, int initialSize)
/*  69:    */   {
/*  70:147 */     Hashtable<ItemSet, Integer> hashtable = new Hashtable(initialSize);
/*  71:149 */     for (int i = 0; i < itemSets.size(); i++)
/*  72:    */     {
/*  73:150 */       LabeledItemSet current = (LabeledItemSet)itemSets.get(i);
/*  74:151 */       hashtable.put(current, new Integer(current.m_classLabel));
/*  75:    */     }
/*  76:154 */     return hashtable;
/*  77:    */   }
/*  78:    */   
/*  79:    */   public static ArrayList<Object> mergeAllItemSets(ArrayList<Object> itemSets, int size, int totalTrans)
/*  80:    */   {
/*  81:169 */     ArrayList<Object> newVector = new ArrayList();
/*  82:    */     label322:
/*  83:173 */     for (int i = 0; i < itemSets.size(); i++)
/*  84:    */     {
/*  85:174 */       LabeledItemSet first = (LabeledItemSet)itemSets.get(i);
/*  86:175 */       for (int j = i + 1; j < itemSets.size(); j++)
/*  87:    */       {
/*  88:176 */         LabeledItemSet second = (LabeledItemSet)itemSets.get(j);
/*  89:177 */         while (first.m_classLabel != second.m_classLabel)
/*  90:    */         {
/*  91:178 */           j++;
/*  92:179 */           if (j == itemSets.size()) {
/*  93:    */             break label322;
/*  94:    */           }
/*  95:182 */           second = (LabeledItemSet)itemSets.get(j);
/*  96:    */         }
/*  97:184 */         LabeledItemSet result = new LabeledItemSet(totalTrans, first.m_classLabel);
/*  98:185 */         result.m_items = new int[first.m_items.length];
/*  99:    */         
/* 100:    */ 
/* 101:188 */         int numFound = 0;
/* 102:189 */         int k = 0;
/* 103:190 */         while (numFound < size)
/* 104:    */         {
/* 105:191 */           if (first.m_items[k] != second.m_items[k]) {
/* 106:    */             break label322;
/* 107:    */           }
/* 108:192 */           if (first.m_items[k] != -1) {
/* 109:193 */             numFound++;
/* 110:    */           }
/* 111:195 */           result.m_items[k] = first.m_items[k];
/* 112:    */           
/* 113:    */ 
/* 114:    */ 
/* 115:199 */           k++;
/* 116:    */         }
/* 117:203 */         while ((k < first.m_items.length) && (
/* 118:204 */           (first.m_items[k] == -1) || (second.m_items[k] == -1)))
/* 119:    */         {
/* 120:207 */           if (first.m_items[k] != -1) {
/* 121:208 */             result.m_items[k] = first.m_items[k];
/* 122:    */           } else {
/* 123:210 */             result.m_items[k] = second.m_items[k];
/* 124:    */           }
/* 125:213 */           k++;
/* 126:    */         }
/* 127:215 */         if (k == first.m_items.length)
/* 128:    */         {
/* 129:216 */           result.m_ruleSupCounter = 0;
/* 130:217 */           result.m_counter = 0;
/* 131:218 */           newVector.add(result);
/* 132:    */         }
/* 133:    */       }
/* 134:    */     }
/* 135:223 */     return newVector;
/* 136:    */   }
/* 137:    */   
/* 138:    */   public static Instances divide(Instances instances, boolean invert)
/* 139:    */     throws Exception
/* 140:    */   {
/* 141:241 */     Instances newInstances = new Instances(instances);
/* 142:242 */     if (instances.classIndex() < 0) {
/* 143:243 */       throw new Exception("For class association rule mining a class attribute has to be specified.");
/* 144:    */     }
/* 145:246 */     if (invert)
/* 146:    */     {
/* 147:247 */       for (int i = 0; i < newInstances.numAttributes(); i++) {
/* 148:248 */         if (i != newInstances.classIndex())
/* 149:    */         {
/* 150:249 */           newInstances.deleteAttributeAt(i);
/* 151:250 */           i--;
/* 152:    */         }
/* 153:    */       }
/* 154:253 */       return newInstances;
/* 155:    */     }
/* 156:255 */     newInstances.setClassIndex(-1);
/* 157:256 */     newInstances.deleteAttributeAt(instances.classIndex());
/* 158:257 */     return newInstances;
/* 159:    */   }
/* 160:    */   
/* 161:    */   public static ArrayList<Object> singletons(Instances instancesNoClass, Instances classes)
/* 162:    */     throws Exception
/* 163:    */   {
/* 164:275 */     ArrayList<Object> setOfItemSets = new ArrayList();
/* 165:279 */     for (int i = 0; i < instancesNoClass.numAttributes(); i++)
/* 166:    */     {
/* 167:280 */       if (instancesNoClass.attribute(i).isNumeric()) {
/* 168:281 */         throw new Exception("Can't handle numeric attributes!");
/* 169:    */       }
/* 170:283 */       for (int j = 0; j < instancesNoClass.attribute(i).numValues(); j++) {
/* 171:284 */         for (int k = 0; k < classes.attribute(0).numValues(); k++)
/* 172:    */         {
/* 173:285 */           LabeledItemSet current = new LabeledItemSet(instancesNoClass.numInstances(), k);
/* 174:286 */           current.m_items = new int[instancesNoClass.numAttributes()];
/* 175:287 */           for (int l = 0; l < instancesNoClass.numAttributes(); l++) {
/* 176:288 */             current.m_items[l] = -1;
/* 177:    */           }
/* 178:290 */           current.m_items[i] = j;
/* 179:291 */           setOfItemSets.add(current);
/* 180:    */         }
/* 181:    */       }
/* 182:    */     }
/* 183:295 */     return setOfItemSets;
/* 184:    */   }
/* 185:    */   
/* 186:    */   public static ArrayList<Object> pruneItemSets(ArrayList<Object> toPrune, Hashtable<ItemSet, Integer> kMinusOne)
/* 187:    */   {
/* 188:308 */     ArrayList<Object> newVector = new ArrayList(toPrune.size());
/* 189:311 */     for (int i = 0; i < toPrune.size(); i++)
/* 190:    */     {
/* 191:312 */       LabeledItemSet current = (LabeledItemSet)toPrune.get(i);
/* 192:314 */       for (int j = 0; j < current.m_items.length; j++) {
/* 193:315 */         if (current.m_items[j] != -1)
/* 194:    */         {
/* 195:316 */           int help = current.m_items[j];
/* 196:317 */           current.m_items[j] = -1;
/* 197:318 */           if ((kMinusOne.get(current) != null) && (current.m_classLabel == ((Integer)kMinusOne.get(current)).intValue()))
/* 198:    */           {
/* 199:320 */             current.m_items[j] = help;
/* 200:    */           }
/* 201:    */           else
/* 202:    */           {
/* 203:322 */             current.m_items[j] = help;
/* 204:323 */             break;
/* 205:    */           }
/* 206:    */         }
/* 207:    */       }
/* 208:327 */       if (j == current.m_items.length) {
/* 209:328 */         newVector.add(current);
/* 210:    */       }
/* 211:    */     }
/* 212:331 */     return newVector;
/* 213:    */   }
/* 214:    */   
/* 215:    */   public final int support()
/* 216:    */   {
/* 217:342 */     return this.m_ruleSupCounter;
/* 218:    */   }
/* 219:    */   
/* 220:    */   public final void upDateCounter(Instance instanceNoClass, Instance instanceClass)
/* 221:    */   {
/* 222:355 */     if (containedBy(instanceNoClass))
/* 223:    */     {
/* 224:356 */       this.m_counter += 1;
/* 225:357 */       if (this.m_classLabel == instanceClass.value(0)) {
/* 226:358 */         this.m_ruleSupCounter += 1;
/* 227:    */       }
/* 228:    */     }
/* 229:    */   }
/* 230:    */   
/* 231:    */   public final void upDateCounterTreatZeroAsMissing(Instance instanceNoClass, Instance instanceClass)
/* 232:    */   {
/* 233:372 */     if (containedByTreatZeroAsMissing(instanceNoClass))
/* 234:    */     {
/* 235:373 */       this.m_counter += 1;
/* 236:374 */       if (this.m_classLabel == instanceClass.value(0)) {
/* 237:375 */         this.m_ruleSupCounter += 1;
/* 238:    */       }
/* 239:    */     }
/* 240:    */   }
/* 241:    */   
/* 242:    */   public static void upDateCounters(ArrayList<Object> itemSets, Instances instancesNoClass, Instances instancesClass)
/* 243:    */   {
/* 244:391 */     for (int i = 0; i < instancesNoClass.numInstances(); i++)
/* 245:    */     {
/* 246:392 */       Enumeration<Object> enu = new WekaEnumeration(itemSets);
/* 247:393 */       while (enu.hasMoreElements()) {
/* 248:394 */         ((LabeledItemSet)enu.nextElement()).upDateCounter(instancesNoClass.instance(i), instancesClass.instance(i));
/* 249:    */       }
/* 250:    */     }
/* 251:    */   }
/* 252:    */   
/* 253:    */   public static void upDateCountersTreatZeroAsMissing(ArrayList<LabeledItemSet> itemSets, Instances instancesNoClass, Instances instancesClass)
/* 254:    */   {
/* 255:412 */     for (int i = 0; i < instancesNoClass.numInstances(); i++)
/* 256:    */     {
/* 257:413 */       Enumeration<LabeledItemSet> enu = new WekaEnumeration(itemSets);
/* 258:415 */       while (enu.hasMoreElements()) {
/* 259:416 */         ((LabeledItemSet)enu.nextElement()).upDateCounterTreatZeroAsMissing(instancesNoClass.instance(i), instancesClass.instance(i));
/* 260:    */       }
/* 261:    */     }
/* 262:    */   }
/* 263:    */   
/* 264:    */   public final ArrayList<Object>[] generateRules(double minConfidence, boolean noPrune)
/* 265:    */   {
/* 266:433 */     ArrayList<Object> premises = new ArrayList();
/* 267:434 */     ArrayList<Object> consequences = new ArrayList();
/* 268:435 */     ArrayList<Object> conf = new ArrayList();
/* 269:    */     
/* 270:437 */     ArrayList<Object>[] rules = new ArrayList[3];
/* 271:    */     
/* 272:    */ 
/* 273:    */ 
/* 274:441 */     ItemSet premise = new ItemSet(this.m_totalTransactions);
/* 275:442 */     ItemSet consequence = new ItemSet(this.m_totalTransactions);
/* 276:443 */     int[] premiseItems = new int[this.m_items.length];
/* 277:444 */     int[] consequenceItems = new int[1];
/* 278:445 */     System.arraycopy(this.m_items, 0, premiseItems, 0, this.m_items.length);
/* 279:446 */     consequence.setItem(consequenceItems);
/* 280:447 */     premise.setItem(premiseItems);
/* 281:448 */     consequence.setItemAt(this.m_classLabel, 0);
/* 282:449 */     consequence.setCounter(this.m_ruleSupCounter);
/* 283:450 */     premise.setCounter(this.m_counter);
/* 284:451 */     premises.add(premise);
/* 285:452 */     consequences.add(consequence);
/* 286:453 */     conf.add(new Double(this.m_ruleSupCounter / this.m_counter));
/* 287:    */     
/* 288:    */ 
/* 289:456 */     rules[0] = premises;
/* 290:457 */     rules[1] = consequences;
/* 291:458 */     rules[2] = conf;
/* 292:459 */     if (!noPrune) {
/* 293:460 */       pruneRules(rules, minConfidence);
/* 294:    */     }
/* 295:463 */     return rules;
/* 296:    */   }
/* 297:    */   
/* 298:    */   public String getRevision()
/* 299:    */   {
/* 300:473 */     return RevisionUtils.extract("$Revision: 10203 $");
/* 301:    */   }
/* 302:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.associations.LabeledItemSet
 * JD-Core Version:    0.7.0.1
 */