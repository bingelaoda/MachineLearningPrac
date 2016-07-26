/*   1:    */ package weka.classifiers.trees.adtree;
/*   2:    */ 
/*   3:    */ import weka.core.Attribute;
/*   4:    */ import weka.core.Instance;
/*   5:    */ import weka.core.Instances;
/*   6:    */ import weka.core.RevisionUtils;
/*   7:    */ 
/*   8:    */ public class TwoWayNominalSplit
/*   9:    */   extends Splitter
/*  10:    */ {
/*  11:    */   private static final long serialVersionUID = -4598366190152721355L;
/*  12:    */   private final int attIndex;
/*  13:    */   private final int trueSplitValue;
/*  14:    */   private final PredictionNode[] children;
/*  15:    */   
/*  16:    */   public TwoWayNominalSplit(int _attIndex, int _trueSplitValue)
/*  17:    */   {
/*  18: 58 */     this.attIndex = _attIndex;
/*  19: 59 */     this.trueSplitValue = _trueSplitValue;
/*  20: 60 */     this.children = new PredictionNode[2];
/*  21:    */   }
/*  22:    */   
/*  23:    */   public int getNumOfBranches()
/*  24:    */   {
/*  25: 71 */     return 2;
/*  26:    */   }
/*  27:    */   
/*  28:    */   public int branchInstanceGoesDown(Instance inst)
/*  29:    */   {
/*  30: 84 */     if (inst.isMissing(this.attIndex)) {
/*  31: 85 */       return -1;
/*  32:    */     }
/*  33: 86 */     if (inst.value(this.attIndex) == this.trueSplitValue) {
/*  34: 87 */       return 0;
/*  35:    */     }
/*  36: 89 */     return 1;
/*  37:    */   }
/*  38:    */   
/*  39:    */   public ReferenceInstances instancesDownBranch(int branch, Instances instances)
/*  40:    */   {
/*  41:105 */     ReferenceInstances filteredInstances = new ReferenceInstances(instances, 1);
/*  42:106 */     if (branch == -1) {
/*  43:107 */       for (Instance instance : instances)
/*  44:    */       {
/*  45:108 */         Instance inst = instance;
/*  46:109 */         if (inst.isMissing(this.attIndex)) {
/*  47:110 */           filteredInstances.addReference(inst);
/*  48:    */         }
/*  49:    */       }
/*  50:113 */     } else if (branch == 0) {
/*  51:114 */       for (Instance instance : instances)
/*  52:    */       {
/*  53:115 */         Instance inst = instance;
/*  54:116 */         if ((!inst.isMissing(this.attIndex)) && (inst.value(this.attIndex) == this.trueSplitValue)) {
/*  55:117 */           filteredInstances.addReference(inst);
/*  56:    */         }
/*  57:    */       }
/*  58:    */     } else {
/*  59:121 */       for (Instance instance : instances)
/*  60:    */       {
/*  61:122 */         Instance inst = instance;
/*  62:123 */         if ((!inst.isMissing(this.attIndex)) && (inst.value(this.attIndex) != this.trueSplitValue)) {
/*  63:124 */           filteredInstances.addReference(inst);
/*  64:    */         }
/*  65:    */       }
/*  66:    */     }
/*  67:128 */     return filteredInstances;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public String attributeString(Instances dataset)
/*  71:    */   {
/*  72:141 */     return dataset.attribute(this.attIndex).name();
/*  73:    */   }
/*  74:    */   
/*  75:    */   public String comparisonString(int branchNum, Instances dataset)
/*  76:    */   {
/*  77:156 */     Attribute att = dataset.attribute(this.attIndex);
/*  78:157 */     if (att.numValues() != 2) {
/*  79:158 */       return (branchNum == 0 ? "= " : "!= ") + att.value(this.trueSplitValue);
/*  80:    */     }
/*  81:160 */     return "= " + (branchNum == 0 ? att.value(this.trueSplitValue) : att.value(this.trueSplitValue == 0 ? 1 : 0));
/*  82:    */   }
/*  83:    */   
/*  84:    */   public boolean equalTo(Splitter compare)
/*  85:    */   {
/*  86:174 */     if ((compare instanceof TwoWayNominalSplit))
/*  87:    */     {
/*  88:175 */       TwoWayNominalSplit compareSame = (TwoWayNominalSplit)compare;
/*  89:176 */       return (this.attIndex == compareSame.attIndex) && (this.trueSplitValue == compareSame.trueSplitValue);
/*  90:    */     }
/*  91:178 */     return false;
/*  92:    */   }
/*  93:    */   
/*  94:    */   public void setChildForBranch(int branchNum, PredictionNode childPredictor)
/*  95:    */   {
/*  96:191 */     this.children[branchNum] = childPredictor;
/*  97:    */   }
/*  98:    */   
/*  99:    */   public PredictionNode getChildForBranch(int branchNum)
/* 100:    */   {
/* 101:203 */     return this.children[branchNum];
/* 102:    */   }
/* 103:    */   
/* 104:    */   public Object clone()
/* 105:    */   {
/* 106:214 */     TwoWayNominalSplit clone = new TwoWayNominalSplit(this.attIndex, this.trueSplitValue);
/* 107:215 */     clone.orderAdded = this.orderAdded;
/* 108:216 */     if (this.children[0] != null) {
/* 109:217 */       clone.setChildForBranch(0, (PredictionNode)this.children[0].clone());
/* 110:    */     }
/* 111:219 */     if (this.children[1] != null) {
/* 112:220 */       clone.setChildForBranch(1, (PredictionNode)this.children[1].clone());
/* 113:    */     }
/* 114:222 */     return clone;
/* 115:    */   }
/* 116:    */   
/* 117:    */   public String getRevision()
/* 118:    */   {
/* 119:232 */     return RevisionUtils.extract("$Revision: 10324 $");
/* 120:    */   }
/* 121:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.adtree.TwoWayNominalSplit
 * JD-Core Version:    0.7.0.1
 */