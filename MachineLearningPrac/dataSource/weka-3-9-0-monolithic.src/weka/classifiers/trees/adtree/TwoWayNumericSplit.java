/*   1:    */ package weka.classifiers.trees.adtree;
/*   2:    */ 
/*   3:    */ import weka.core.Attribute;
/*   4:    */ import weka.core.Instance;
/*   5:    */ import weka.core.Instances;
/*   6:    */ import weka.core.RevisionUtils;
/*   7:    */ import weka.core.Utils;
/*   8:    */ 
/*   9:    */ public class TwoWayNumericSplit
/*  10:    */   extends Splitter
/*  11:    */ {
/*  12:    */   private static final long serialVersionUID = 449769177903158283L;
/*  13:    */   private final int attIndex;
/*  14:    */   private final double splitPoint;
/*  15:    */   private final PredictionNode[] children;
/*  16:    */   
/*  17:    */   public TwoWayNumericSplit(int _attIndex, double _splitPoint)
/*  18:    */   {
/*  19: 58 */     this.attIndex = _attIndex;
/*  20: 59 */     this.splitPoint = _splitPoint;
/*  21: 60 */     this.children = new PredictionNode[2];
/*  22:    */   }
/*  23:    */   
/*  24:    */   public int getNumOfBranches()
/*  25:    */   {
/*  26: 71 */     return 2;
/*  27:    */   }
/*  28:    */   
/*  29:    */   public int branchInstanceGoesDown(Instance inst)
/*  30:    */   {
/*  31: 84 */     if (inst.isMissing(this.attIndex)) {
/*  32: 85 */       return -1;
/*  33:    */     }
/*  34: 86 */     if (inst.value(this.attIndex) < this.splitPoint) {
/*  35: 87 */       return 0;
/*  36:    */     }
/*  37: 89 */     return 1;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public ReferenceInstances instancesDownBranch(int branch, Instances instances)
/*  41:    */   {
/*  42:105 */     ReferenceInstances filteredInstances = new ReferenceInstances(instances, 1);
/*  43:106 */     if (branch == -1) {
/*  44:107 */       for (Instance instance : instances)
/*  45:    */       {
/*  46:108 */         Instance inst = instance;
/*  47:109 */         if (inst.isMissing(this.attIndex)) {
/*  48:110 */           filteredInstances.addReference(inst);
/*  49:    */         }
/*  50:    */       }
/*  51:113 */     } else if (branch == 0) {
/*  52:114 */       for (Instance instance : instances)
/*  53:    */       {
/*  54:115 */         Instance inst = instance;
/*  55:116 */         if ((!inst.isMissing(this.attIndex)) && (inst.value(this.attIndex) < this.splitPoint)) {
/*  56:117 */           filteredInstances.addReference(inst);
/*  57:    */         }
/*  58:    */       }
/*  59:    */     } else {
/*  60:121 */       for (Instance instance : instances)
/*  61:    */       {
/*  62:122 */         Instance inst = instance;
/*  63:123 */         if ((!inst.isMissing(this.attIndex)) && (inst.value(this.attIndex) >= this.splitPoint)) {
/*  64:124 */           filteredInstances.addReference(inst);
/*  65:    */         }
/*  66:    */       }
/*  67:    */     }
/*  68:128 */     return filteredInstances;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public String attributeString(Instances dataset)
/*  72:    */   {
/*  73:141 */     return dataset.attribute(this.attIndex).name();
/*  74:    */   }
/*  75:    */   
/*  76:    */   public String comparisonString(int branchNum, Instances dataset)
/*  77:    */   {
/*  78:156 */     return (branchNum == 0 ? "< " : ">= ") + Utils.doubleToString(this.splitPoint, 3);
/*  79:    */   }
/*  80:    */   
/*  81:    */   public boolean equalTo(Splitter compare)
/*  82:    */   {
/*  83:169 */     if ((compare instanceof TwoWayNumericSplit))
/*  84:    */     {
/*  85:170 */       TwoWayNumericSplit compareSame = (TwoWayNumericSplit)compare;
/*  86:171 */       return (this.attIndex == compareSame.attIndex) && (this.splitPoint == compareSame.splitPoint);
/*  87:    */     }
/*  88:173 */     return false;
/*  89:    */   }
/*  90:    */   
/*  91:    */   public void setChildForBranch(int branchNum, PredictionNode childPredictor)
/*  92:    */   {
/*  93:186 */     this.children[branchNum] = childPredictor;
/*  94:    */   }
/*  95:    */   
/*  96:    */   public PredictionNode getChildForBranch(int branchNum)
/*  97:    */   {
/*  98:198 */     return this.children[branchNum];
/*  99:    */   }
/* 100:    */   
/* 101:    */   public Object clone()
/* 102:    */   {
/* 103:209 */     TwoWayNumericSplit clone = new TwoWayNumericSplit(this.attIndex, this.splitPoint);
/* 104:210 */     clone.orderAdded = this.orderAdded;
/* 105:211 */     if (this.children[0] != null) {
/* 106:212 */       clone.setChildForBranch(0, (PredictionNode)this.children[0].clone());
/* 107:    */     }
/* 108:214 */     if (this.children[1] != null) {
/* 109:215 */       clone.setChildForBranch(1, (PredictionNode)this.children[1].clone());
/* 110:    */     }
/* 111:217 */     return clone;
/* 112:    */   }
/* 113:    */   
/* 114:    */   public String getRevision()
/* 115:    */   {
/* 116:227 */     return RevisionUtils.extract("$Revision: 10324 $");
/* 117:    */   }
/* 118:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.adtree.TwoWayNumericSplit
 * JD-Core Version:    0.7.0.1
 */