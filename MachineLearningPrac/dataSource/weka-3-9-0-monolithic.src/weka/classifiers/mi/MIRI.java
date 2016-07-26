/*   1:    */ package weka.classifiers.mi;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.HashMap;
/*   7:    */ import java.util.Iterator;
/*   8:    */ import java.util.Vector;
/*   9:    */ import weka.classifiers.mi.miti.Bag;
/*  10:    */ import weka.core.AdditionalMeasureProducer;
/*  11:    */ import weka.core.Capabilities;
/*  12:    */ import weka.core.Instance;
/*  13:    */ import weka.core.Instances;
/*  14:    */ import weka.core.OptionHandler;
/*  15:    */ import weka.core.TechnicalInformation;
/*  16:    */ 
/*  17:    */ public class MIRI
/*  18:    */   extends MITI
/*  19:    */   implements OptionHandler, AdditionalMeasureProducer
/*  20:    */ {
/*  21:    */   static final long serialVersionUID = -218835168397644255L;
/*  22:    */   private ArrayList<MITI.MultiInstanceDecisionTree> rules;
/*  23:    */   
/*  24:    */   public String globalInfo()
/*  25:    */   {
/*  26:131 */     return "MIRI (Multi Instance Rule Inducer): multi-instance classifier that utilizes partial MITI trees witha single positive leaf to learn and represent rules. For more information, see\n\n" + getTechnicalInformation().toString();
/*  27:    */   }
/*  28:    */   
/*  29:    */   public Enumeration enumerateMeasures()
/*  30:    */   {
/*  31:146 */     Vector newVector = new Vector(3);
/*  32:147 */     newVector.addElement("measureNumRules");
/*  33:148 */     newVector.addElement("measureNumPositiveRules");
/*  34:149 */     newVector.addElement("measureNumConditionsInPositiveRules");
/*  35:150 */     return newVector.elements();
/*  36:    */   }
/*  37:    */   
/*  38:    */   public double getMeasure(String additionalMeasureName)
/*  39:    */   {
/*  40:162 */     if (additionalMeasureName.equalsIgnoreCase("measureNumRules")) {
/*  41:163 */       return this.rules.size() + 1.0D;
/*  42:    */     }
/*  43:165 */     if (additionalMeasureName.equalsIgnoreCase("measureNumPositiveRules"))
/*  44:    */     {
/*  45:166 */       int total = 0;
/*  46:167 */       for (MITI.MultiInstanceDecisionTree rule : this.rules) {
/*  47:168 */         total += rule.numPosRulesAndNumPosConditions()[0];
/*  48:    */       }
/*  49:170 */       return total;
/*  50:    */     }
/*  51:172 */     if (additionalMeasureName.equalsIgnoreCase("measureNumConditionsInPositiveRules"))
/*  52:    */     {
/*  53:173 */       int total = 0;
/*  54:174 */       for (MITI.MultiInstanceDecisionTree rule : this.rules) {
/*  55:175 */         total += rule.numPosRulesAndNumPosConditions()[1];
/*  56:    */       }
/*  57:177 */       return total;
/*  58:    */     }
/*  59:179 */     throw new IllegalArgumentException(additionalMeasureName + " not supported (MultiInstanceTreeRuleLearner)");
/*  60:    */   }
/*  61:    */   
/*  62:    */   public void buildClassifier(Instances trainingData)
/*  63:    */     throws Exception
/*  64:    */   {
/*  65:191 */     getCapabilities().testWithFail(trainingData);
/*  66:    */     
/*  67:193 */     this.rules = new ArrayList();
/*  68:    */     
/*  69:195 */     HashMap<Instance, Bag> instanceBags = new HashMap();
/*  70:196 */     ArrayList<Instance> all = new ArrayList();
/*  71:197 */     double totalInstances = 0.0D;
/*  72:198 */     double totalBags = 0.0D;
/*  73:199 */     for (Instance i : trainingData)
/*  74:    */     {
/*  75:200 */       Bag bag = new Bag(i);
/*  76:201 */       for (Instance bagged : bag.instances())
/*  77:    */       {
/*  78:202 */         instanceBags.put(bagged, bag);
/*  79:203 */         all.add(bagged);
/*  80:    */       }
/*  81:205 */       totalBags += 1.0D;
/*  82:206 */       totalInstances += trainingData.numInstances();
/*  83:    */     }
/*  84:209 */     double b_multiplier = totalInstances / totalBags;
/*  85:210 */     if (this.m_scaleK) {
/*  86:211 */       for (Bag bag : instanceBags.values()) {
/*  87:212 */         bag.setBagWeightMultiplier(b_multiplier);
/*  88:    */       }
/*  89:    */     }
/*  90:215 */     while (all.size() > 0)
/*  91:    */     {
/*  92:216 */       MITI.MultiInstanceDecisionTree tree = new MITI.MultiInstanceDecisionTree(this, instanceBags, all, true);
/*  93:221 */       if (this.m_Debug) {
/*  94:222 */         System.out.println(tree.render());
/*  95:    */       }
/*  96:225 */       if (!tree.trimNegativeBranches()) {
/*  97:    */         break;
/*  98:    */       }
/*  99:227 */       if (this.m_Debug) {
/* 100:228 */         System.out.println(tree.render());
/* 101:    */       }
/* 102:231 */       this.rules.add(tree);
/* 103:    */       
/* 104:    */ 
/* 105:    */ 
/* 106:    */ 
/* 107:    */ 
/* 108:    */ 
/* 109:    */ 
/* 110:239 */       boolean atLeast1Positive = false;
/* 111:240 */       ArrayList<Instance> stillEnabled = new ArrayList();
/* 112:241 */       for (Instance i : all)
/* 113:    */       {
/* 114:243 */         Bag bag = (Bag)instanceBags.get(i);
/* 115:244 */         if (bag.isEnabled())
/* 116:    */         {
/* 117:246 */           stillEnabled.add(i);
/* 118:247 */           if (bag.isPositive()) {
/* 119:248 */             atLeast1Positive = true;
/* 120:    */           }
/* 121:    */         }
/* 122:    */       }
/* 123:252 */       all = stillEnabled;
/* 124:253 */       if (!atLeast1Positive) {
/* 125:    */         break;
/* 126:    */       }
/* 127:    */     }
/* 128:    */   }
/* 129:    */   
/* 130:    */   public double[] distributionForInstance(Instance newBag)
/* 131:    */     throws Exception
/* 132:    */   {
/* 133:264 */     double[] distribution = new double[2];
/* 134:265 */     Instances contents = newBag.relationalValue(1);
/* 135:266 */     boolean positive = false;
/* 136:267 */     for (Iterator i$ = contents.iterator(); i$.hasNext();)
/* 137:    */     {
/* 138:267 */       i = (Instance)i$.next();
/* 139:269 */       for (MITI.MultiInstanceDecisionTree tree : this.rules) {
/* 140:271 */         if (tree.isPositive(i))
/* 141:    */         {
/* 142:273 */           positive = true;
/* 143:274 */           break;
/* 144:    */         }
/* 145:    */       }
/* 146:    */     }
/* 147:    */     Instance i;
/* 148:279 */     distribution[1] = (positive ? 1.0D : 0.0D);
/* 149:280 */     distribution[0] = (1.0D - distribution[1]);
/* 150:    */     
/* 151:282 */     return distribution;
/* 152:    */   }
/* 153:    */   
/* 154:    */   public String toString()
/* 155:    */   {
/* 156:291 */     if (this.rules != null)
/* 157:    */     {
/* 158:292 */       String s = this.rules.size() + " rules\n";
/* 159:293 */       for (MITI.MultiInstanceDecisionTree tree : this.rules) {
/* 160:294 */         s = s + tree.render() + "\n";
/* 161:    */       }
/* 162:296 */       s = s + "\nNumber of positive rules: " + getMeasure("measureNumPositiveRules") + "\n";
/* 163:297 */       s = s + "Number of conditions in positive rules: " + getMeasure("measureNumConditionsInPositiveRules") + "\n";
/* 164:    */       
/* 165:299 */       return s;
/* 166:    */     }
/* 167:301 */     return "No model built yet!";
/* 168:    */   }
/* 169:    */   
/* 170:    */   public static void main(String[] options)
/* 171:    */   {
/* 172:309 */     runClassifier(new MIRI(), options);
/* 173:    */   }
/* 174:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.mi.MIRI
 * JD-Core Version:    0.7.0.1
 */