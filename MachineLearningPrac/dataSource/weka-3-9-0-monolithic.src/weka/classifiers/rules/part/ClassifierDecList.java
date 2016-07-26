/*   1:    */ package weka.classifiers.rules.part;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import weka.classifiers.trees.j48.ClassifierSplitModel;
/*   5:    */ import weka.classifiers.trees.j48.Distribution;
/*   6:    */ import weka.classifiers.trees.j48.EntropySplitCrit;
/*   7:    */ import weka.classifiers.trees.j48.ModelSelection;
/*   8:    */ import weka.classifiers.trees.j48.NoSplit;
/*   9:    */ import weka.core.ContingencyTables;
/*  10:    */ import weka.core.Instance;
/*  11:    */ import weka.core.Instances;
/*  12:    */ import weka.core.RevisionHandler;
/*  13:    */ import weka.core.RevisionUtils;
/*  14:    */ import weka.core.Utils;
/*  15:    */ 
/*  16:    */ public class ClassifierDecList
/*  17:    */   implements Serializable, RevisionHandler
/*  18:    */ {
/*  19:    */   private static final long serialVersionUID = 7284358349711992497L;
/*  20:    */   protected int m_minNumObj;
/*  21: 53 */   protected static EntropySplitCrit m_splitCrit = new EntropySplitCrit();
/*  22:    */   protected ModelSelection m_toSelectModel;
/*  23:    */   protected ClassifierSplitModel m_localModel;
/*  24:    */   protected ClassifierDecList[] m_sons;
/*  25:    */   protected boolean m_isLeaf;
/*  26:    */   protected boolean m_isEmpty;
/*  27:    */   protected Instances m_train;
/*  28:    */   protected Distribution m_test;
/*  29:    */   protected int indeX;
/*  30:    */   
/*  31:    */   public ClassifierDecList(ModelSelection toSelectLocModel, int minNum)
/*  32:    */   {
/*  33: 84 */     this.m_toSelectModel = toSelectLocModel;
/*  34: 85 */     this.m_minNumObj = minNum;
/*  35:    */   }
/*  36:    */   
/*  37:    */   public void buildRule(Instances data)
/*  38:    */     throws Exception
/*  39:    */   {
/*  40: 95 */     buildDecList(data, false);
/*  41:    */     
/*  42: 97 */     cleanup(new Instances(data, 0));
/*  43:    */   }
/*  44:    */   
/*  45:    */   public void buildDecList(Instances data, boolean leaf)
/*  46:    */     throws Exception
/*  47:    */   {
/*  48:113 */     this.m_train = null;
/*  49:114 */     this.m_test = null;
/*  50:115 */     this.m_isLeaf = false;
/*  51:116 */     this.m_isEmpty = false;
/*  52:117 */     this.m_sons = null;
/*  53:118 */     this.indeX = 0;
/*  54:119 */     double sumOfWeights = data.sumOfWeights();
/*  55:120 */     NoSplit noSplit = new NoSplit(new Distribution(data));
/*  56:121 */     if (leaf) {
/*  57:122 */       this.m_localModel = noSplit;
/*  58:    */     } else {
/*  59:124 */       this.m_localModel = this.m_toSelectModel.selectModel(data);
/*  60:    */     }
/*  61:126 */     if (this.m_localModel.numSubsets() > 1)
/*  62:    */     {
/*  63:127 */       Instances[] localInstances = this.m_localModel.split(data);
/*  64:128 */       data = null;
/*  65:129 */       this.m_sons = new ClassifierDecList[this.m_localModel.numSubsets()];
/*  66:130 */       int i = 0;
/*  67:    */       int ind;
/*  68:    */       do
/*  69:    */       {
/*  70:132 */         i++;
/*  71:133 */         ind = chooseIndex();
/*  72:134 */         if (ind == -1)
/*  73:    */         {
/*  74:135 */           for (int j = 0; j < this.m_sons.length; j++) {
/*  75:136 */             if (this.m_sons[j] == null) {
/*  76:137 */               this.m_sons[j] = getNewDecList(localInstances[j], true);
/*  77:    */             }
/*  78:    */           }
/*  79:140 */           if (i < 2)
/*  80:    */           {
/*  81:141 */             this.m_localModel = noSplit;
/*  82:142 */             this.m_isLeaf = true;
/*  83:143 */             this.m_sons = null;
/*  84:144 */             if (Utils.eq(sumOfWeights, 0.0D)) {
/*  85:145 */               this.m_isEmpty = true;
/*  86:    */             }
/*  87:147 */             return;
/*  88:    */           }
/*  89:149 */           ind = 0;
/*  90:150 */           break;
/*  91:    */         }
/*  92:152 */         this.m_sons[ind] = getNewDecList(localInstances[ind], false);
/*  93:154 */       } while ((i < this.m_sons.length) && (this.m_sons[ind].m_isLeaf));
/*  94:157 */       this.indeX = chooseLastIndex();
/*  95:    */     }
/*  96:    */     else
/*  97:    */     {
/*  98:159 */       this.m_isLeaf = true;
/*  99:160 */       if (Utils.eq(sumOfWeights, 0.0D)) {
/* 100:161 */         this.m_isEmpty = true;
/* 101:    */       }
/* 102:    */     }
/* 103:    */   }
/* 104:    */   
/* 105:    */   public double classifyInstance(Instance instance)
/* 106:    */     throws Exception
/* 107:    */   {
/* 108:173 */     double maxProb = -1.0D;
/* 109:    */     
/* 110:175 */     int maxIndex = 0;
/* 111:178 */     for (int j = 0; j < instance.numClasses(); j++)
/* 112:    */     {
/* 113:179 */       double currentProb = getProbs(j, instance, 1.0D);
/* 114:180 */       if (Utils.gr(currentProb, maxProb))
/* 115:    */       {
/* 116:181 */         maxIndex = j;
/* 117:182 */         maxProb = currentProb;
/* 118:    */       }
/* 119:    */     }
/* 120:185 */     if (Utils.eq(maxProb, 0.0D)) {
/* 121:186 */       return -1.0D;
/* 122:    */     }
/* 123:188 */     return maxIndex;
/* 124:    */   }
/* 125:    */   
/* 126:    */   public final double[] distributionForInstance(Instance instance)
/* 127:    */     throws Exception
/* 128:    */   {
/* 129:200 */     double[] doubles = new double[instance.numClasses()];
/* 130:202 */     for (int i = 0; i < doubles.length; i++) {
/* 131:203 */       doubles[i] = getProbs(i, instance, 1.0D);
/* 132:    */     }
/* 133:206 */     return doubles;
/* 134:    */   }
/* 135:    */   
/* 136:    */   public double weight(Instance instance)
/* 137:    */     throws Exception
/* 138:    */   {
/* 139:218 */     if (this.m_isLeaf) {
/* 140:219 */       return 1.0D;
/* 141:    */     }
/* 142:221 */     int subset = this.m_localModel.whichSubset(instance);
/* 143:222 */     if (subset == -1) {
/* 144:223 */       return this.m_localModel.weights(instance)[this.indeX] * this.m_sons[this.indeX].weight(instance);
/* 145:    */     }
/* 146:226 */     if (subset == this.indeX) {
/* 147:227 */       return this.m_sons[this.indeX].weight(instance);
/* 148:    */     }
/* 149:229 */     return 0.0D;
/* 150:    */   }
/* 151:    */   
/* 152:    */   public final void cleanup(Instances justHeaderInfo)
/* 153:    */   {
/* 154:237 */     this.m_train = justHeaderInfo;
/* 155:238 */     this.m_test = null;
/* 156:239 */     if (!this.m_isLeaf) {
/* 157:240 */       for (ClassifierDecList m_son : this.m_sons) {
/* 158:241 */         if (m_son != null) {
/* 159:242 */           m_son.cleanup(justHeaderInfo);
/* 160:    */         }
/* 161:    */       }
/* 162:    */     }
/* 163:    */   }
/* 164:    */   
/* 165:    */   public String toString()
/* 166:    */   {
/* 167:    */     try
/* 168:    */     {
/* 169:257 */       StringBuffer text = new StringBuffer();
/* 170:258 */       if (this.m_isLeaf)
/* 171:    */       {
/* 172:259 */         text.append(": ");
/* 173:260 */         text.append(this.m_localModel.dumpLabel(0, this.m_train) + "\n");
/* 174:    */       }
/* 175:    */       else
/* 176:    */       {
/* 177:262 */         dumpDecList(text);
/* 178:    */       }
/* 179:265 */       return text.toString();
/* 180:    */     }
/* 181:    */     catch (Exception e) {}
/* 182:267 */     return "Can't print rule.";
/* 183:    */   }
/* 184:    */   
/* 185:    */   protected ClassifierDecList getNewDecList(Instances train, boolean leaf)
/* 186:    */     throws Exception
/* 187:    */   {
/* 188:279 */     ClassifierDecList newDecList = new ClassifierDecList(this.m_toSelectModel, this.m_minNumObj);
/* 189:    */     
/* 190:281 */     newDecList.buildDecList(train, leaf);
/* 191:    */     
/* 192:283 */     return newDecList;
/* 193:    */   }
/* 194:    */   
/* 195:    */   public final int chooseIndex()
/* 196:    */   {
/* 197:291 */     int minIndex = -1;
/* 198:292 */     double min = 1.7976931348623157E+308D;
/* 199:295 */     for (int i = 0; i < this.m_sons.length; i++) {
/* 200:296 */       if (son(i) == null)
/* 201:    */       {
/* 202:    */         double estimated;
/* 203:    */         double estimated;
/* 204:297 */         if (Utils.sm(localModel().distribution().perBag(i), this.m_minNumObj))
/* 205:    */         {
/* 206:298 */           estimated = 1.7976931348623157E+308D;
/* 207:    */         }
/* 208:    */         else
/* 209:    */         {
/* 210:300 */           estimated = 0.0D;
/* 211:301 */           for (int j = 0; j < localModel().distribution().numClasses(); j++) {
/* 212:302 */             estimated -= m_splitCrit.lnFunc(localModel().distribution().perClassPerBag(i, j));
/* 213:    */           }
/* 214:305 */           estimated += m_splitCrit.lnFunc(localModel().distribution().perBag(i));
/* 215:    */           
/* 216:307 */           estimated /= localModel().distribution().perBag(i) * ContingencyTables.log2;
/* 217:    */         }
/* 218:309 */         if (Utils.smOrEq(estimated, 0.0D)) {
/* 219:310 */           return i;
/* 220:    */         }
/* 221:312 */         if (Utils.sm(estimated, min))
/* 222:    */         {
/* 223:313 */           min = estimated;
/* 224:314 */           minIndex = i;
/* 225:    */         }
/* 226:    */       }
/* 227:    */     }
/* 228:319 */     return minIndex;
/* 229:    */   }
/* 230:    */   
/* 231:    */   public final int chooseLastIndex()
/* 232:    */   {
/* 233:327 */     int minIndex = 0;
/* 234:328 */     double min = 1.7976931348623157E+308D;
/* 235:330 */     if (!this.m_isLeaf) {
/* 236:331 */       for (int i = 0; i < this.m_sons.length; i++) {
/* 237:332 */         if ((son(i) != null) && 
/* 238:333 */           (Utils.grOrEq(localModel().distribution().perBag(i), this.m_minNumObj)))
/* 239:    */         {
/* 240:334 */           double estimated = son(i).getSizeOfBranch();
/* 241:335 */           if (Utils.sm(estimated, min))
/* 242:    */           {
/* 243:336 */             min = estimated;
/* 244:337 */             minIndex = i;
/* 245:    */           }
/* 246:    */         }
/* 247:    */       }
/* 248:    */     }
/* 249:344 */     return minIndex;
/* 250:    */   }
/* 251:    */   
/* 252:    */   protected double getSizeOfBranch()
/* 253:    */   {
/* 254:352 */     if (this.m_isLeaf) {
/* 255:353 */       return -localModel().distribution().total();
/* 256:    */     }
/* 257:355 */     return son(this.indeX).getSizeOfBranch();
/* 258:    */   }
/* 259:    */   
/* 260:    */   private void dumpDecList(StringBuffer text)
/* 261:    */     throws Exception
/* 262:    */   {
/* 263:364 */     text.append(this.m_localModel.leftSide(this.m_train));
/* 264:365 */     text.append(this.m_localModel.rightSide(this.indeX, this.m_train));
/* 265:366 */     if (this.m_sons[this.indeX].m_isLeaf)
/* 266:    */     {
/* 267:367 */       text.append(": ");
/* 268:368 */       text.append(this.m_localModel.dumpLabel(this.indeX, this.m_train) + "\n");
/* 269:    */     }
/* 270:    */     else
/* 271:    */     {
/* 272:370 */       text.append(" AND\n");
/* 273:371 */       this.m_sons[this.indeX].dumpDecList(text);
/* 274:    */     }
/* 275:    */   }
/* 276:    */   
/* 277:    */   private double getProbs(int classIndex, Instance instance, double weight)
/* 278:    */     throws Exception
/* 279:    */   {
/* 280:386 */     if (this.m_isLeaf) {
/* 281:387 */       return weight * localModel().classProb(classIndex, instance, -1);
/* 282:    */     }
/* 283:389 */     int treeIndex = localModel().whichSubset(instance);
/* 284:390 */     if (treeIndex == -1)
/* 285:    */     {
/* 286:391 */       double[] weights = localModel().weights(instance);
/* 287:392 */       return son(this.indeX).getProbs(classIndex, instance, weights[this.indeX] * weight);
/* 288:    */     }
/* 289:395 */     if (treeIndex == this.indeX) {
/* 290:396 */       return son(this.indeX).getProbs(classIndex, instance, weight);
/* 291:    */     }
/* 292:398 */     return 0.0D;
/* 293:    */   }
/* 294:    */   
/* 295:    */   protected ClassifierSplitModel localModel()
/* 296:    */   {
/* 297:409 */     return this.m_localModel;
/* 298:    */   }
/* 299:    */   
/* 300:    */   protected ClassifierDecList son(int index)
/* 301:    */   {
/* 302:417 */     return this.m_sons[index];
/* 303:    */   }
/* 304:    */   
/* 305:    */   public String getRevision()
/* 306:    */   {
/* 307:427 */     return RevisionUtils.extract("$Revision: 10153 $");
/* 308:    */   }
/* 309:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.rules.part.ClassifierDecList
 * JD-Core Version:    0.7.0.1
 */