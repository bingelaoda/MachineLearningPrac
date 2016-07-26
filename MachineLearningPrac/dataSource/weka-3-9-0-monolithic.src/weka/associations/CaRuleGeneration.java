/*   1:    */ package weka.associations;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Hashtable;
/*   6:    */ import java.util.TreeSet;
/*   7:    */ import weka.core.Attribute;
/*   8:    */ import weka.core.Instances;
/*   9:    */ import weka.core.RevisionHandler;
/*  10:    */ import weka.core.RevisionUtils;
/*  11:    */ import weka.core.UnassignedClassException;
/*  12:    */ 
/*  13:    */ public class CaRuleGeneration
/*  14:    */   extends RuleGeneration
/*  15:    */   implements Serializable, RevisionHandler
/*  16:    */ {
/*  17:    */   private static final long serialVersionUID = 3065752149646517703L;
/*  18:    */   
/*  19:    */   public CaRuleGeneration(ItemSet itemSet)
/*  20:    */   {
/*  21: 66 */     super(itemSet);
/*  22:    */   }
/*  23:    */   
/*  24:    */   public TreeSet<RuleItem> generateRules(int numRules, double[] midPoints, Hashtable<Double, Double> priors, double expectation, Instances instances, TreeSet<RuleItem> best, int genTime)
/*  25:    */   {
/*  26: 89 */     boolean redundant = false;
/*  27: 90 */     ArrayList<Object> consequences = new ArrayList();
/*  28:    */     
/*  29: 92 */     RuleItem current = null;
/*  30:    */     
/*  31: 94 */     this.m_change = false;
/*  32: 95 */     this.m_midPoints = midPoints;
/*  33: 96 */     this.m_priors = priors;
/*  34: 97 */     this.m_best = best;
/*  35: 98 */     this.m_expectation = expectation;
/*  36: 99 */     this.m_count = genTime;
/*  37:100 */     this.m_instances = instances;
/*  38:    */     
/*  39:    */ 
/*  40:103 */     ItemSet premise = null;
/*  41:104 */     premise = new ItemSet(this.m_totalTransactions);
/*  42:105 */     int[] premiseItems = new int[this.m_items.length];
/*  43:106 */     System.arraycopy(this.m_items, 0, premiseItems, 0, this.m_items.length);
/*  44:107 */     premise.setItem(premiseItems);
/*  45:108 */     premise.setCounter(this.m_counter);
/*  46:    */     
/*  47:110 */     consequences = singleConsequence(instances);
/*  48:    */     do
/*  49:    */     {
/*  50:114 */       if ((premise == null) || (consequences.size() == 0)) {
/*  51:115 */         return this.m_best;
/*  52:    */       }
/*  53:117 */       this.m_minRuleCount = 1;
/*  54:119 */       while (expectation(this.m_minRuleCount, premise.counter(), this.m_midPoints, this.m_priors) <= this.m_expectation)
/*  55:    */       {
/*  56:120 */         this.m_minRuleCount += 1;
/*  57:121 */         if (this.m_minRuleCount > premise.counter()) {
/*  58:122 */           return this.m_best;
/*  59:    */         }
/*  60:    */       }
/*  61:125 */       redundant = false;
/*  62:    */       
/*  63:    */ 
/*  64:128 */       ArrayList<Object> allRuleItems = new ArrayList();
/*  65:129 */       int h = 0;
/*  66:130 */       while (h < consequences.size())
/*  67:    */       {
/*  68:131 */         RuleItem dummie = new RuleItem();
/*  69:132 */         this.m_count += 1;
/*  70:133 */         current = dummie.generateRuleItem(premise, (ItemSet)consequences.get(h), instances, this.m_count, this.m_minRuleCount, this.m_midPoints, this.m_priors);
/*  71:136 */         if (current != null) {
/*  72:137 */           allRuleItems.add(current);
/*  73:    */         }
/*  74:139 */         h++;
/*  75:    */       }
/*  76:143 */       for (h = 0; h < allRuleItems.size(); h++)
/*  77:    */       {
/*  78:144 */         current = (RuleItem)allRuleItems.get(h);
/*  79:145 */         if (this.m_best.size() < numRules)
/*  80:    */         {
/*  81:146 */           this.m_change = true;
/*  82:147 */           redundant = removeRedundant(current);
/*  83:    */         }
/*  84:    */         else
/*  85:    */         {
/*  86:149 */           this.m_expectation = ((RuleItem)this.m_best.first()).accuracy();
/*  87:150 */           if (current.accuracy() > this.m_expectation)
/*  88:    */           {
/*  89:151 */             this.m_best.remove(this.m_best.first());
/*  90:152 */             this.m_change = true;
/*  91:153 */             redundant = removeRedundant(current);
/*  92:154 */             this.m_expectation = ((RuleItem)this.m_best.first()).accuracy();
/*  93:156 */             while (expectation(this.m_minRuleCount, current.premise().counter(), this.m_midPoints, this.m_priors) < this.m_expectation)
/*  94:    */             {
/*  95:157 */               this.m_minRuleCount += 1;
/*  96:158 */               if (this.m_minRuleCount > current.premise().counter()) {
/*  97:    */                 break;
/*  98:    */               }
/*  99:    */             }
/* 100:    */           }
/* 101:    */         }
/* 102:    */       }
/* 103:165 */     } while (redundant);
/* 104:166 */     return this.m_best;
/* 105:    */   }
/* 106:    */   
/* 107:    */   public static boolean aSubsumesB(RuleItem a, RuleItem b)
/* 108:    */   {
/* 109:180 */     if (!a.consequence().equals(b.consequence())) {
/* 110:181 */       return false;
/* 111:    */     }
/* 112:183 */     if (a.accuracy() < b.accuracy()) {
/* 113:184 */       return false;
/* 114:    */     }
/* 115:186 */     for (int k = 0; k < a.premise().items().length; k++) {
/* 116:187 */       if ((a.premise().itemAt(k) != b.premise().itemAt(k)) && (
/* 117:188 */         ((a.premise().itemAt(k) != -1) && (b.premise().itemAt(k) != -1)) || (b.premise().itemAt(k) == -1))) {
/* 118:190 */         return false;
/* 119:    */       }
/* 120:    */     }
/* 121:199 */     return true;
/* 122:    */   }
/* 123:    */   
/* 124:    */   public static ArrayList<Object> singletons(Instances instances)
/* 125:    */     throws Exception
/* 126:    */   {
/* 127:215 */     ArrayList<Object> setOfItemSets = new ArrayList();
/* 128:218 */     if (instances.classIndex() == -1) {
/* 129:219 */       throw new UnassignedClassException("Class index is negative (not set)!");
/* 130:    */     }
/* 131:221 */     instances.classAttribute();
/* 132:222 */     for (int i = 0; i < instances.numAttributes(); i++)
/* 133:    */     {
/* 134:223 */       if (instances.attribute(i).isNumeric()) {
/* 135:224 */         throw new Exception("Can't handle numeric attributes!");
/* 136:    */       }
/* 137:226 */       if (i != instances.classIndex()) {
/* 138:227 */         for (int j = 0; j < instances.attribute(i).numValues(); j++)
/* 139:    */         {
/* 140:228 */           ItemSet current = new ItemSet(instances.numInstances());
/* 141:229 */           int[] currentItems = new int[instances.numAttributes()];
/* 142:230 */           for (int k = 0; k < instances.numAttributes(); k++) {
/* 143:231 */             currentItems[k] = -1;
/* 144:    */           }
/* 145:233 */           currentItems[i] = j;
/* 146:234 */           current.setItem(currentItems);
/* 147:235 */           setOfItemSets.add(current);
/* 148:    */         }
/* 149:    */       }
/* 150:    */     }
/* 151:239 */     return setOfItemSets;
/* 152:    */   }
/* 153:    */   
/* 154:    */   public static ArrayList<Object> singleConsequence(Instances instances)
/* 155:    */   {
/* 156:251 */     ArrayList<Object> consequences = new ArrayList();
/* 157:253 */     for (int j = 0; j < instances.classAttribute().numValues(); j++)
/* 158:    */     {
/* 159:254 */       ItemSet consequence = new ItemSet(instances.numInstances());
/* 160:255 */       int[] consequenceItems = new int[instances.numAttributes()];
/* 161:256 */       consequence.setItem(consequenceItems);
/* 162:257 */       for (int k = 0; k < instances.numAttributes(); k++) {
/* 163:258 */         consequence.setItemAt(-1, k);
/* 164:    */       }
/* 165:260 */       consequence.setItemAt(j, instances.classIndex());
/* 166:261 */       consequences.add(consequence);
/* 167:    */     }
/* 168:263 */     return consequences;
/* 169:    */   }
/* 170:    */   
/* 171:    */   public String getRevision()
/* 172:    */   {
/* 173:274 */     return RevisionUtils.extract("$Revision: 10201 $");
/* 174:    */   }
/* 175:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.associations.CaRuleGeneration
 * JD-Core Version:    0.7.0.1
 */