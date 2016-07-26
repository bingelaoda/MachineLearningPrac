/*   1:    */ package weka.classifiers.rules.part;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Random;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.classifiers.trees.j48.ModelSelection;
/*   8:    */ import weka.core.Capabilities;
/*   9:    */ import weka.core.Capabilities.Capability;
/*  10:    */ import weka.core.CapabilitiesHandler;
/*  11:    */ import weka.core.Instance;
/*  12:    */ import weka.core.Instances;
/*  13:    */ import weka.core.RevisionHandler;
/*  14:    */ import weka.core.RevisionUtils;
/*  15:    */ import weka.core.Utils;
/*  16:    */ 
/*  17:    */ public class MakeDecList
/*  18:    */   implements Serializable, CapabilitiesHandler, RevisionHandler
/*  19:    */ {
/*  20:    */   private static final long serialVersionUID = -1427481323245079123L;
/*  21:    */   private Vector<ClassifierDecList> theRules;
/*  22: 55 */   private double CF = 0.25D;
/*  23:    */   private final int minNumObj;
/*  24:    */   private final ModelSelection toSelectModeL;
/*  25: 67 */   private int numSetS = 3;
/*  26: 70 */   private boolean reducedErrorPruning = false;
/*  27: 73 */   private boolean unpruned = false;
/*  28: 76 */   private int m_seed = 1;
/*  29:    */   
/*  30:    */   public MakeDecList(ModelSelection toSelectLocModel, int minNum)
/*  31:    */   {
/*  32: 83 */     this.toSelectModeL = toSelectLocModel;
/*  33: 84 */     this.reducedErrorPruning = false;
/*  34: 85 */     this.unpruned = true;
/*  35: 86 */     this.minNumObj = minNum;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public MakeDecList(ModelSelection toSelectLocModel, double cf, int minNum)
/*  39:    */   {
/*  40: 94 */     this.toSelectModeL = toSelectLocModel;
/*  41: 95 */     this.CF = cf;
/*  42: 96 */     this.reducedErrorPruning = false;
/*  43: 97 */     this.unpruned = false;
/*  44: 98 */     this.minNumObj = minNum;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public MakeDecList(ModelSelection toSelectLocModel, int num, int minNum, int seed)
/*  48:    */   {
/*  49:107 */     this.toSelectModeL = toSelectLocModel;
/*  50:108 */     this.numSetS = num;
/*  51:109 */     this.reducedErrorPruning = true;
/*  52:110 */     this.unpruned = false;
/*  53:111 */     this.minNumObj = minNum;
/*  54:112 */     this.m_seed = seed;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public Capabilities getCapabilities()
/*  58:    */   {
/*  59:122 */     Capabilities result = new Capabilities(this);
/*  60:123 */     result.disableAll();
/*  61:    */     
/*  62:    */ 
/*  63:126 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  64:127 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  65:128 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*  66:129 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  67:    */     
/*  68:    */ 
/*  69:132 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  70:133 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  71:    */     
/*  72:135 */     return result;
/*  73:    */   }
/*  74:    */   
/*  75:    */   public void buildClassifier(Instances data)
/*  76:    */     throws Exception
/*  77:    */   {
/*  78:146 */     getCapabilities().testWithFail(data);
/*  79:    */     
/*  80:    */ 
/*  81:149 */     data = new Instances(data);
/*  82:150 */     data.deleteWithMissingClass();
/*  83:    */     
/*  84:    */ 
/*  85:    */ 
/*  86:    */ 
/*  87:155 */     this.theRules = new Vector();
/*  88:    */     Instances oldPruneData;
/*  89:    */     Instances oldGrowData;
/*  90:    */     Instances oldPruneData;
/*  91:156 */     if ((this.reducedErrorPruning) && (!this.unpruned))
/*  92:    */     {
/*  93:157 */       Random random = new Random(this.m_seed);
/*  94:158 */       data.randomize(random);
/*  95:159 */       data.stratify(this.numSetS);
/*  96:160 */       Instances oldGrowData = data.trainCV(this.numSetS, this.numSetS - 1, random);
/*  97:161 */       oldPruneData = data.testCV(this.numSetS, this.numSetS - 1);
/*  98:    */     }
/*  99:    */     else
/* 100:    */     {
/* 101:163 */       oldGrowData = data;
/* 102:164 */       oldPruneData = null;
/* 103:    */     }
/* 104:167 */     while (Utils.gr(oldGrowData.numInstances(), 0.0D))
/* 105:    */     {
/* 106:    */       ClassifierDecList currentRule;
/* 107:170 */       if (this.unpruned)
/* 108:    */       {
/* 109:171 */         ClassifierDecList currentRule = new ClassifierDecList(this.toSelectModeL, this.minNumObj);
/* 110:172 */         currentRule.buildRule(oldGrowData);
/* 111:    */       }
/* 112:173 */       else if (this.reducedErrorPruning)
/* 113:    */       {
/* 114:174 */         ClassifierDecList currentRule = new PruneableDecList(this.toSelectModeL, this.minNumObj);
/* 115:175 */         ((PruneableDecList)currentRule).buildRule(oldGrowData, oldPruneData);
/* 116:    */       }
/* 117:    */       else
/* 118:    */       {
/* 119:177 */         currentRule = new C45PruneableDecList(this.toSelectModeL, this.CF, this.minNumObj);
/* 120:178 */         ((C45PruneableDecList)currentRule).buildRule(oldGrowData);
/* 121:    */       }
/* 122:181 */       Instances newGrowData = new Instances(oldGrowData, oldGrowData.numInstances());
/* 123:182 */       Enumeration<Instance> enu = oldGrowData.enumerateInstances();
/* 124:183 */       while (enu.hasMoreElements())
/* 125:    */       {
/* 126:184 */         Instance instance = (Instance)enu.nextElement();
/* 127:185 */         double currentWeight = currentRule.weight(instance);
/* 128:186 */         if (Utils.sm(currentWeight, 1.0D))
/* 129:    */         {
/* 130:187 */           instance.setWeight(instance.weight() * (1.0D - currentWeight));
/* 131:188 */           newGrowData.add(instance);
/* 132:    */         }
/* 133:    */       }
/* 134:191 */       newGrowData.compactify();
/* 135:192 */       oldGrowData = newGrowData;
/* 136:195 */       if ((this.reducedErrorPruning) && (!this.unpruned))
/* 137:    */       {
/* 138:196 */         Instances newPruneData = new Instances(oldPruneData, oldPruneData.numInstances());
/* 139:197 */         enu = oldPruneData.enumerateInstances();
/* 140:198 */         while (enu.hasMoreElements())
/* 141:    */         {
/* 142:199 */           Instance instance = (Instance)enu.nextElement();
/* 143:200 */           double currentWeight = currentRule.weight(instance);
/* 144:201 */           if (Utils.sm(currentWeight, 1.0D))
/* 145:    */           {
/* 146:202 */             instance.setWeight(instance.weight() * (1.0D - currentWeight));
/* 147:203 */             newPruneData.add(instance);
/* 148:    */           }
/* 149:    */         }
/* 150:206 */         newPruneData.compactify();
/* 151:207 */         oldPruneData = newPruneData;
/* 152:    */       }
/* 153:209 */       this.theRules.addElement(currentRule);
/* 154:    */     }
/* 155:    */   }
/* 156:    */   
/* 157:    */   public String toString()
/* 158:    */   {
/* 159:219 */     StringBuffer text = new StringBuffer();
/* 160:221 */     for (int i = 0; i < this.theRules.size(); i++) {
/* 161:222 */       text.append(this.theRules.elementAt(i) + "\n");
/* 162:    */     }
/* 163:224 */     text.append("Number of Rules  : \t" + this.theRules.size() + "\n");
/* 164:    */     
/* 165:226 */     return text.toString();
/* 166:    */   }
/* 167:    */   
/* 168:    */   public double classifyInstance(Instance instance)
/* 169:    */     throws Exception
/* 170:    */   {
/* 171:236 */     double maxProb = -1.0D;
/* 172:    */     
/* 173:238 */     int maxIndex = 0;
/* 174:    */     
/* 175:240 */     double[] sumProbs = distributionForInstance(instance);
/* 176:241 */     for (int j = 0; j < sumProbs.length; j++) {
/* 177:242 */       if (Utils.gr(sumProbs[j], maxProb))
/* 178:    */       {
/* 179:243 */         maxIndex = j;
/* 180:244 */         maxProb = sumProbs[j];
/* 181:    */       }
/* 182:    */     }
/* 183:248 */     return maxIndex;
/* 184:    */   }
/* 185:    */   
/* 186:    */   public double[] distributionForInstance(Instance instance)
/* 187:    */     throws Exception
/* 188:    */   {
/* 189:258 */     double[] currentProbs = null;
/* 190:    */     
/* 191:260 */     double weight = 1.0D;
/* 192:    */     
/* 193:    */ 
/* 194:    */ 
/* 195:264 */     double[] sumProbs = new double[instance.numClasses()];
/* 196:265 */     int i = 0;
/* 197:266 */     while (Utils.gr(weight, 0.0D))
/* 198:    */     {
/* 199:267 */       double currentWeight = ((ClassifierDecList)this.theRules.elementAt(i)).weight(instance);
/* 200:268 */       if (Utils.gr(currentWeight, 0.0D))
/* 201:    */       {
/* 202:269 */         currentProbs = ((ClassifierDecList)this.theRules.elementAt(i)).distributionForInstance(instance);
/* 203:270 */         for (int j = 0; j < sumProbs.length; j++) {
/* 204:271 */           sumProbs[j] += weight * currentProbs[j];
/* 205:    */         }
/* 206:273 */         weight *= (1.0D - currentWeight);
/* 207:    */       }
/* 208:275 */       i++;
/* 209:    */     }
/* 210:278 */     return sumProbs;
/* 211:    */   }
/* 212:    */   
/* 213:    */   public int numRules()
/* 214:    */   {
/* 215:286 */     return this.theRules.size();
/* 216:    */   }
/* 217:    */   
/* 218:    */   public String getRevision()
/* 219:    */   {
/* 220:296 */     return RevisionUtils.extract("$Revision: 11006 $");
/* 221:    */   }
/* 222:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.rules.part.MakeDecList
 * JD-Core Version:    0.7.0.1
 */