/*   1:    */ package weka.classifiers.trees.j48;
/*   2:    */ 
/*   3:    */ import weka.core.Capabilities;
/*   4:    */ import weka.core.Capabilities.Capability;
/*   5:    */ import weka.core.Instances;
/*   6:    */ import weka.core.RevisionUtils;
/*   7:    */ import weka.core.Utils;
/*   8:    */ 
/*   9:    */ public class C45PruneableClassifierTree
/*  10:    */   extends ClassifierTree
/*  11:    */ {
/*  12:    */   static final long serialVersionUID = -4813820170260388194L;
/*  13: 45 */   protected boolean m_pruneTheTree = false;
/*  14: 48 */   protected boolean m_collapseTheTree = false;
/*  15: 51 */   protected float m_CF = 0.25F;
/*  16: 54 */   protected boolean m_subtreeRaising = true;
/*  17: 57 */   protected boolean m_cleanup = true;
/*  18:    */   
/*  19:    */   public C45PruneableClassifierTree(ModelSelection toSelectLocModel, boolean pruneTree, float cf, boolean raiseTree, boolean cleanup, boolean collapseTree)
/*  20:    */     throws Exception
/*  21:    */   {
/*  22: 77 */     super(toSelectLocModel);
/*  23:    */     
/*  24: 79 */     this.m_pruneTheTree = pruneTree;
/*  25: 80 */     this.m_CF = cf;
/*  26: 81 */     this.m_subtreeRaising = raiseTree;
/*  27: 82 */     this.m_cleanup = cleanup;
/*  28: 83 */     this.m_collapseTheTree = collapseTree;
/*  29:    */   }
/*  30:    */   
/*  31:    */   public Capabilities getCapabilities()
/*  32:    */   {
/*  33: 92 */     Capabilities result = super.getCapabilities();
/*  34: 93 */     result.disableAll();
/*  35:    */     
/*  36:    */ 
/*  37: 96 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  38: 97 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  39: 98 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*  40: 99 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  41:    */     
/*  42:    */ 
/*  43:102 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  44:103 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  45:    */     
/*  46:    */ 
/*  47:106 */     result.setMinimumNumberInstances(0);
/*  48:    */     
/*  49:108 */     return result;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public void buildClassifier(Instances data)
/*  53:    */     throws Exception
/*  54:    */   {
/*  55:120 */     getCapabilities().testWithFail(data);
/*  56:    */     
/*  57:    */ 
/*  58:123 */     data = new Instances(data);
/*  59:124 */     data.deleteWithMissingClass();
/*  60:    */     
/*  61:126 */     buildTree(data, (this.m_subtreeRaising) || (!this.m_cleanup));
/*  62:127 */     if (this.m_collapseTheTree) {
/*  63:128 */       collapse();
/*  64:    */     }
/*  65:130 */     if (this.m_pruneTheTree) {
/*  66:131 */       prune();
/*  67:    */     }
/*  68:133 */     if (this.m_cleanup) {
/*  69:134 */       cleanup(new Instances(data, 0));
/*  70:    */     }
/*  71:    */   }
/*  72:    */   
/*  73:    */   public final void collapse()
/*  74:    */   {
/*  75:147 */     if (!this.m_isLeaf)
/*  76:    */     {
/*  77:148 */       double errorsOfSubtree = getTrainingErrors();
/*  78:149 */       double errorsOfTree = localModel().distribution().numIncorrect();
/*  79:150 */       if (errorsOfSubtree >= errorsOfTree - 0.001D)
/*  80:    */       {
/*  81:153 */         this.m_sons = null;
/*  82:154 */         this.m_isLeaf = true;
/*  83:    */         
/*  84:    */ 
/*  85:157 */         this.m_localModel = new NoSplit(localModel().distribution());
/*  86:    */       }
/*  87:    */       else
/*  88:    */       {
/*  89:159 */         for (int i = 0; i < this.m_sons.length; i++) {
/*  90:160 */           son(i).collapse();
/*  91:    */         }
/*  92:    */       }
/*  93:    */     }
/*  94:    */   }
/*  95:    */   
/*  96:    */   public void prune()
/*  97:    */     throws Exception
/*  98:    */   {
/*  99:178 */     if (!this.m_isLeaf)
/* 100:    */     {
/* 101:181 */       for (int i = 0; i < this.m_sons.length; i++) {
/* 102:182 */         son(i).prune();
/* 103:    */       }
/* 104:185 */       int indexOfLargestBranch = localModel().distribution().maxBag();
/* 105:    */       double errorsLargestBranch;
/* 106:    */       double errorsLargestBranch;
/* 107:186 */       if (this.m_subtreeRaising) {
/* 108:187 */         errorsLargestBranch = son(indexOfLargestBranch).getEstimatedErrorsForBranch(this.m_train);
/* 109:    */       } else {
/* 110:190 */         errorsLargestBranch = 1.7976931348623157E+308D;
/* 111:    */       }
/* 112:194 */       double errorsLeaf = getEstimatedErrorsForDistribution(localModel().distribution());
/* 113:    */       
/* 114:    */ 
/* 115:    */ 
/* 116:198 */       double errorsTree = getEstimatedErrors();
/* 117:201 */       if ((Utils.smOrEq(errorsLeaf, errorsTree + 0.1D)) && (Utils.smOrEq(errorsLeaf, errorsLargestBranch + 0.1D)))
/* 118:    */       {
/* 119:205 */         this.m_sons = null;
/* 120:206 */         this.m_isLeaf = true;
/* 121:    */         
/* 122:    */ 
/* 123:209 */         this.m_localModel = new NoSplit(localModel().distribution());
/* 124:210 */         return;
/* 125:    */       }
/* 126:215 */       if (Utils.smOrEq(errorsLargestBranch, errorsTree + 0.1D))
/* 127:    */       {
/* 128:216 */         C45PruneableClassifierTree largestBranch = son(indexOfLargestBranch);
/* 129:217 */         this.m_sons = largestBranch.m_sons;
/* 130:218 */         this.m_localModel = largestBranch.localModel();
/* 131:219 */         this.m_isLeaf = largestBranch.m_isLeaf;
/* 132:220 */         newDistribution(this.m_train);
/* 133:221 */         prune();
/* 134:    */       }
/* 135:    */     }
/* 136:    */   }
/* 137:    */   
/* 138:    */   protected ClassifierTree getNewTree(Instances data)
/* 139:    */     throws Exception
/* 140:    */   {
/* 141:235 */     C45PruneableClassifierTree newTree = new C45PruneableClassifierTree(this.m_toSelectModel, this.m_pruneTheTree, this.m_CF, this.m_subtreeRaising, this.m_cleanup, this.m_collapseTheTree);
/* 142:    */     
/* 143:    */ 
/* 144:238 */     newTree.buildTree(data, (this.m_subtreeRaising) || (!this.m_cleanup));
/* 145:    */     
/* 146:240 */     return newTree;
/* 147:    */   }
/* 148:    */   
/* 149:    */   private double getEstimatedErrors()
/* 150:    */   {
/* 151:250 */     double errors = 0.0D;
/* 152:253 */     if (this.m_isLeaf) {
/* 153:254 */       return getEstimatedErrorsForDistribution(localModel().distribution());
/* 154:    */     }
/* 155:256 */     for (int i = 0; i < this.m_sons.length; i++) {
/* 156:257 */       errors += son(i).getEstimatedErrors();
/* 157:    */     }
/* 158:258 */     return errors;
/* 159:    */   }
/* 160:    */   
/* 161:    */   private double getEstimatedErrorsForBranch(Instances data)
/* 162:    */     throws Exception
/* 163:    */   {
/* 164:273 */     double errors = 0.0D;
/* 165:276 */     if (this.m_isLeaf) {
/* 166:277 */       return getEstimatedErrorsForDistribution(new Distribution(data));
/* 167:    */     }
/* 168:279 */     Distribution savedDist = localModel().m_distribution;
/* 169:280 */     localModel().resetDistribution(data);
/* 170:281 */     Instances[] localInstances = (Instances[])localModel().split(data);
/* 171:282 */     localModel().m_distribution = savedDist;
/* 172:283 */     for (int i = 0; i < this.m_sons.length; i++) {
/* 173:284 */       errors += son(i).getEstimatedErrorsForBranch(localInstances[i]);
/* 174:    */     }
/* 175:286 */     return errors;
/* 176:    */   }
/* 177:    */   
/* 178:    */   private double getEstimatedErrorsForDistribution(Distribution theDistribution)
/* 179:    */   {
/* 180:299 */     if (Utils.eq(theDistribution.total(), 0.0D)) {
/* 181:300 */       return 0.0D;
/* 182:    */     }
/* 183:302 */     return theDistribution.numIncorrect() + Stats.addErrs(theDistribution.total(), theDistribution.numIncorrect(), this.m_CF);
/* 184:    */   }
/* 185:    */   
/* 186:    */   private double getTrainingErrors()
/* 187:    */   {
/* 188:314 */     double errors = 0.0D;
/* 189:317 */     if (this.m_isLeaf) {
/* 190:318 */       return localModel().distribution().numIncorrect();
/* 191:    */     }
/* 192:320 */     for (int i = 0; i < this.m_sons.length; i++) {
/* 193:321 */       errors += son(i).getTrainingErrors();
/* 194:    */     }
/* 195:322 */     return errors;
/* 196:    */   }
/* 197:    */   
/* 198:    */   private ClassifierSplitModel localModel()
/* 199:    */   {
/* 200:333 */     return this.m_localModel;
/* 201:    */   }
/* 202:    */   
/* 203:    */   private void newDistribution(Instances data)
/* 204:    */     throws Exception
/* 205:    */   {
/* 206:347 */     localModel().resetDistribution(data);
/* 207:348 */     this.m_train = data;
/* 208:349 */     if (!this.m_isLeaf)
/* 209:    */     {
/* 210:350 */       Instances[] localInstances = (Instances[])localModel().split(data);
/* 211:352 */       for (int i = 0; i < this.m_sons.length; i++) {
/* 212:353 */         son(i).newDistribution(localInstances[i]);
/* 213:    */       }
/* 214:    */     }
/* 215:357 */     else if (!Utils.eq(data.sumOfWeights(), 0.0D))
/* 216:    */     {
/* 217:358 */       this.m_isEmpty = false;
/* 218:    */     }
/* 219:    */   }
/* 220:    */   
/* 221:    */   private C45PruneableClassifierTree son(int index)
/* 222:    */   {
/* 223:368 */     return (C45PruneableClassifierTree)this.m_sons[index];
/* 224:    */   }
/* 225:    */   
/* 226:    */   public String getRevision()
/* 227:    */   {
/* 228:377 */     return RevisionUtils.extract("$Revision: 11006 $");
/* 229:    */   }
/* 230:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.j48.C45PruneableClassifierTree
 * JD-Core Version:    0.7.0.1
 */