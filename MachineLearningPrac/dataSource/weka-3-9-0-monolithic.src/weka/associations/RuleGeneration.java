/*   1:    */ package weka.associations;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.io.Serializable;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import java.util.Hashtable;
/*   7:    */ import java.util.TreeSet;
/*   8:    */ import weka.core.Attribute;
/*   9:    */ import weka.core.Instances;
/*  10:    */ import weka.core.RevisionHandler;
/*  11:    */ import weka.core.RevisionUtils;
/*  12:    */ import weka.core.Statistics;
/*  13:    */ import weka.core.Utils;
/*  14:    */ 
/*  15:    */ public class RuleGeneration
/*  16:    */   implements Serializable, RevisionHandler
/*  17:    */ {
/*  18:    */   private static final long serialVersionUID = -8927041669872491432L;
/*  19:    */   protected int[] m_items;
/*  20:    */   protected int m_counter;
/*  21:    */   protected int m_totalTransactions;
/*  22: 69 */   protected boolean m_change = false;
/*  23:    */   protected double m_expectation;
/*  24:    */   protected static final int MAX_N = 300;
/*  25:    */   protected int m_minRuleCount;
/*  26:    */   protected double[] m_midPoints;
/*  27:    */   protected Hashtable<Double, Double> m_priors;
/*  28:    */   protected TreeSet<RuleItem> m_best;
/*  29:    */   protected int m_count;
/*  30:    */   protected Instances m_instances;
/*  31:    */   
/*  32:    */   public RuleGeneration(ItemSet itemSet)
/*  33:    */   {
/*  34:114 */     this.m_totalTransactions = itemSet.m_totalTransactions;
/*  35:115 */     this.m_counter = itemSet.m_counter;
/*  36:116 */     this.m_items = itemSet.m_items;
/*  37:    */   }
/*  38:    */   
/*  39:    */   public static final double binomialDistribution(double accuracy, double ruleCount, double premiseCount)
/*  40:    */   {
/*  41:134 */     if (premiseCount < 300.0D) {
/*  42:135 */       return Math.pow(2.0D, Utils.log2(Math.pow(accuracy, ruleCount)) + Utils.log2(Math.pow(1.0D - accuracy, premiseCount - ruleCount)) + PriorEstimation.logbinomialCoefficient((int)premiseCount, (int)ruleCount));
/*  43:    */     }
/*  44:142 */     double mu = premiseCount * accuracy;
/*  45:143 */     double sigma = Math.sqrt(premiseCount * (1.0D - accuracy) * accuracy);
/*  46:144 */     return Statistics.normalProbability((ruleCount + 0.5D - mu) / (sigma * Math.sqrt(2.0D)));
/*  47:    */   }
/*  48:    */   
/*  49:    */   public static final double expectation(double ruleCount, int premiseCount, double[] midPoints, Hashtable<Double, Double> priors)
/*  50:    */   {
/*  51:161 */     double numerator = 0.0D;double denominator = 0.0D;
/*  52:162 */     for (double midPoint : midPoints)
/*  53:    */     {
/*  54:163 */       Double actualPrior = (Double)priors.get(new Double(midPoint));
/*  55:164 */       if ((actualPrior != null) && 
/*  56:165 */         (actualPrior.doubleValue() != 0.0D))
/*  57:    */       {
/*  58:166 */         double addend = actualPrior.doubleValue() * binomialDistribution(midPoint, ruleCount, premiseCount);
/*  59:    */         
/*  60:168 */         denominator += addend;
/*  61:169 */         numerator += addend * midPoint;
/*  62:    */       }
/*  63:    */     }
/*  64:173 */     if ((denominator <= 0.0D) || (Double.isNaN(denominator))) {
/*  65:174 */       System.out.println("RuleItem denominator: " + denominator);
/*  66:    */     }
/*  67:176 */     if ((numerator <= 0.0D) || (Double.isNaN(numerator))) {
/*  68:177 */       System.out.println("RuleItem numerator: " + numerator);
/*  69:    */     }
/*  70:179 */     return numerator / denominator;
/*  71:    */   }
/*  72:    */   
/*  73:    */   public TreeSet<RuleItem> generateRules(int numRules, double[] midPoints, Hashtable<Double, Double> priors, double expectation, Instances instances, TreeSet<RuleItem> best, int genTime)
/*  74:    */   {
/*  75:201 */     boolean redundant = false;
/*  76:202 */     ArrayList<Object> consequences = new ArrayList();ArrayList<Object> consequencesMinusOne = new ArrayList();
/*  77:    */     
/*  78:204 */     RuleItem current = null;
/*  79:    */     
/*  80:    */ 
/*  81:    */ 
/*  82:208 */     this.m_change = false;
/*  83:209 */     this.m_midPoints = midPoints;
/*  84:210 */     this.m_priors = priors;
/*  85:211 */     this.m_best = best;
/*  86:212 */     this.m_expectation = expectation;
/*  87:213 */     this.m_count = genTime;
/*  88:214 */     this.m_instances = instances;
/*  89:    */     
/*  90:    */ 
/*  91:217 */     ItemSet premise = null;
/*  92:218 */     premise = new ItemSet(this.m_totalTransactions);
/*  93:219 */     premise.m_items = new int[this.m_items.length];
/*  94:220 */     System.arraycopy(this.m_items, 0, premise.m_items, 0, this.m_items.length);
/*  95:221 */     premise.m_counter = this.m_counter;
/*  96:    */     do
/*  97:    */     {
/*  98:224 */       this.m_minRuleCount = 1;
/*  99:226 */       while (expectation(this.m_minRuleCount, premise.m_counter, this.m_midPoints, this.m_priors) <= this.m_expectation)
/* 100:    */       {
/* 101:227 */         this.m_minRuleCount += 1;
/* 102:228 */         if (this.m_minRuleCount > premise.m_counter) {
/* 103:229 */           return this.m_best;
/* 104:    */         }
/* 105:    */       }
/* 106:232 */       redundant = false;
/* 107:233 */       for (int i = 0; i < instances.numAttributes(); i++)
/* 108:    */       {
/* 109:234 */         if (i == 0)
/* 110:    */         {
/* 111:235 */           for (int j = 0; j < this.m_items.length; j++) {
/* 112:236 */             if (this.m_items[j] == -1) {
/* 113:237 */               consequences = singleConsequence(instances, j, consequences);
/* 114:    */             }
/* 115:    */           }
/* 116:240 */           if ((premise == null) || (consequences.size() == 0)) {
/* 117:241 */             return this.m_best;
/* 118:    */           }
/* 119:    */         }
/* 120:244 */         ArrayList<Object> allRuleItems = new ArrayList();
/* 121:245 */         int index = 0;
/* 122:    */         do
/* 123:    */         {
/* 124:247 */           int h = 0;
/* 125:248 */           while (h < consequences.size())
/* 126:    */           {
/* 127:249 */             RuleItem dummie = new RuleItem();
/* 128:250 */             current = dummie.generateRuleItem(premise, (ItemSet)consequences.get(h), instances, this.m_count, this.m_minRuleCount, this.m_midPoints, this.m_priors);
/* 129:253 */             if (current != null)
/* 130:    */             {
/* 131:254 */               allRuleItems.add(current);
/* 132:255 */               h++;
/* 133:    */             }
/* 134:    */             else
/* 135:    */             {
/* 136:257 */               consequences.remove(h);
/* 137:    */             }
/* 138:    */           }
/* 139:260 */           if (index == i) {
/* 140:    */             break;
/* 141:    */           }
/* 142:263 */           consequencesMinusOne = consequences;
/* 143:264 */           consequences = ItemSet.mergeAllItemSets(consequencesMinusOne, index, instances.numInstances());
/* 144:    */           
/* 145:266 */           Hashtable<ItemSet, Integer> hashtable = ItemSet.getHashtable(consequencesMinusOne, consequencesMinusOne.size());
/* 146:    */           
/* 147:268 */           consequences = ItemSet.pruneItemSets(consequences, hashtable);
/* 148:269 */           index++;
/* 149:270 */         } while (consequences.size() > 0);
/* 150:271 */         for (int h = 0; h < allRuleItems.size(); h++)
/* 151:    */         {
/* 152:272 */           current = (RuleItem)allRuleItems.get(h);
/* 153:273 */           this.m_count += 1;
/* 154:274 */           if (this.m_best.size() < numRules)
/* 155:    */           {
/* 156:275 */             this.m_change = true;
/* 157:276 */             redundant = removeRedundant(current);
/* 158:    */           }
/* 159:278 */           else if (current.accuracy() > this.m_expectation)
/* 160:    */           {
/* 161:279 */             this.m_expectation = ((RuleItem)this.m_best.first()).accuracy();
/* 162:280 */             this.m_best.remove(this.m_best.first());
/* 163:281 */             this.m_change = true;
/* 164:282 */             redundant = removeRedundant(current);
/* 165:283 */             this.m_expectation = ((RuleItem)this.m_best.first()).accuracy();
/* 166:285 */             while (expectation(this.m_minRuleCount, current.premise().m_counter, this.m_midPoints, this.m_priors) < this.m_expectation)
/* 167:    */             {
/* 168:286 */               this.m_minRuleCount += 1;
/* 169:287 */               if (this.m_minRuleCount > current.premise().m_counter) {
/* 170:    */                 break;
/* 171:    */               }
/* 172:    */             }
/* 173:    */           }
/* 174:    */         }
/* 175:    */       }
/* 176:296 */     } while (redundant);
/* 177:297 */     return this.m_best;
/* 178:    */   }
/* 179:    */   
/* 180:    */   public static boolean aSubsumesB(RuleItem a, RuleItem b)
/* 181:    */   {
/* 182:311 */     if (a.m_accuracy < b.m_accuracy) {
/* 183:312 */       return false;
/* 184:    */     }
/* 185:314 */     for (int k = 0; k < a.premise().m_items.length; k++)
/* 186:    */     {
/* 187:315 */       if ((a.premise().m_items[k] != b.premise().m_items[k]) && (
/* 188:316 */         ((a.premise().m_items[k] != -1) && (b.premise().m_items[k] != -1)) || (b.premise().m_items[k] == -1))) {
/* 189:318 */         return false;
/* 190:    */       }
/* 191:321 */       if ((a.consequence().m_items[k] != b.consequence().m_items[k]) && (
/* 192:322 */         ((a.consequence().m_items[k] != -1) && (b.consequence().m_items[k] != -1)) || (a.consequence().m_items[k] == -1))) {
/* 193:324 */         return false;
/* 194:    */       }
/* 195:    */     }
/* 196:328 */     return true;
/* 197:    */   }
/* 198:    */   
/* 199:    */   public static ArrayList<Object> singleConsequence(Instances instances, int attNum, ArrayList<Object> consequences)
/* 200:    */   {
/* 201:346 */     for (int i = 0; i < instances.numAttributes(); i++) {
/* 202:347 */       if (i == attNum) {
/* 203:348 */         for (int j = 0; j < instances.attribute(i).numValues(); j++)
/* 204:    */         {
/* 205:349 */           ItemSet consequence = new ItemSet(instances.numInstances());
/* 206:350 */           consequence.m_items = new int[instances.numAttributes()];
/* 207:351 */           for (int k = 0; k < instances.numAttributes(); k++) {
/* 208:352 */             consequence.m_items[k] = -1;
/* 209:    */           }
/* 210:354 */           consequence.m_items[i] = j;
/* 211:355 */           consequences.add(consequence);
/* 212:    */         }
/* 213:    */       }
/* 214:    */     }
/* 215:359 */     return consequences;
/* 216:    */   }
/* 217:    */   
/* 218:    */   public boolean removeRedundant(RuleItem toInsert)
/* 219:    */   {
/* 220:374 */     boolean redundant = false;boolean fSubsumesT = false;boolean tSubsumesF = false;
/* 221:    */     
/* 222:376 */     int subsumes = 0;
/* 223:377 */     Object[] best = this.m_best.toArray();
/* 224:378 */     for (Object element : best)
/* 225:    */     {
/* 226:379 */       RuleItem first = (RuleItem)element;
/* 227:380 */       fSubsumesT = aSubsumesB(first, toInsert);
/* 228:381 */       tSubsumesF = aSubsumesB(toInsert, first);
/* 229:382 */       if (fSubsumesT)
/* 230:    */       {
/* 231:383 */         subsumes = 1;
/* 232:384 */         break;
/* 233:    */       }
/* 234:386 */       if (tSubsumesF)
/* 235:    */       {
/* 236:387 */         this.m_best.remove(first);
/* 237:388 */         subsumes = 2;
/* 238:389 */         redundant = true;
/* 239:    */       }
/* 240:    */     }
/* 241:393 */     if ((subsumes == 0) || (subsumes == 2)) {
/* 242:394 */       this.m_best.add(toInsert);
/* 243:    */     }
/* 244:396 */     return redundant;
/* 245:    */   }
/* 246:    */   
/* 247:    */   public int count()
/* 248:    */   {
/* 249:406 */     return this.m_count;
/* 250:    */   }
/* 251:    */   
/* 252:    */   public boolean change()
/* 253:    */   {
/* 254:416 */     return this.m_change;
/* 255:    */   }
/* 256:    */   
/* 257:    */   public String getRevision()
/* 258:    */   {
/* 259:426 */     return RevisionUtils.extract("$Revision: 10201 $");
/* 260:    */   }
/* 261:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.associations.RuleGeneration
 * JD-Core Version:    0.7.0.1
 */