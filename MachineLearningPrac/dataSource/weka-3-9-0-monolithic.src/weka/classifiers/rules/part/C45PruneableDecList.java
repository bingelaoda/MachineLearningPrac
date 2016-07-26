/*   1:    */ package weka.classifiers.rules.part;
/*   2:    */ 
/*   3:    */ import weka.classifiers.trees.j48.ClassifierSplitModel;
/*   4:    */ import weka.classifiers.trees.j48.Distribution;
/*   5:    */ import weka.classifiers.trees.j48.ModelSelection;
/*   6:    */ import weka.classifiers.trees.j48.NoSplit;
/*   7:    */ import weka.classifiers.trees.j48.Stats;
/*   8:    */ import weka.core.Instances;
/*   9:    */ import weka.core.RevisionUtils;
/*  10:    */ import weka.core.Utils;
/*  11:    */ 
/*  12:    */ public class C45PruneableDecList
/*  13:    */   extends ClassifierDecList
/*  14:    */ {
/*  15:    */   private static final long serialVersionUID = -2757684345218324559L;
/*  16: 45 */   private double CF = 0.25D;
/*  17:    */   
/*  18:    */   public C45PruneableDecList(ModelSelection toSelectLocModel, double cf, int minNum)
/*  19:    */     throws Exception
/*  20:    */   {
/*  21: 59 */     super(toSelectLocModel, minNum);
/*  22:    */     
/*  23: 61 */     this.CF = cf;
/*  24:    */   }
/*  25:    */   
/*  26:    */   public void buildDecList(Instances data, boolean leaf)
/*  27:    */     throws Exception
/*  28:    */   {
/*  29: 78 */     this.m_train = null;
/*  30: 79 */     this.m_test = null;
/*  31: 80 */     this.m_isLeaf = false;
/*  32: 81 */     this.m_isEmpty = false;
/*  33: 82 */     this.m_sons = null;
/*  34: 83 */     this.indeX = 0;
/*  35: 84 */     double sumOfWeights = data.sumOfWeights();
/*  36: 85 */     NoSplit noSplit = new NoSplit(new Distribution(data));
/*  37: 86 */     if (leaf) {
/*  38: 87 */       this.m_localModel = noSplit;
/*  39:    */     } else {
/*  40: 89 */       this.m_localModel = this.m_toSelectModel.selectModel(data);
/*  41:    */     }
/*  42: 91 */     if (this.m_localModel.numSubsets() > 1)
/*  43:    */     {
/*  44: 92 */       Instances[] localInstances = this.m_localModel.split(data);
/*  45: 93 */       data = null;
/*  46: 94 */       this.m_sons = new ClassifierDecList[this.m_localModel.numSubsets()];
/*  47: 95 */       int i = 0;
/*  48:    */       int ind;
/*  49:    */       do
/*  50:    */       {
/*  51: 97 */         i++;
/*  52: 98 */         ind = chooseIndex();
/*  53: 99 */         if (ind == -1)
/*  54:    */         {
/*  55:100 */           for (int j = 0; j < this.m_sons.length; j++) {
/*  56:101 */             if (this.m_sons[j] == null) {
/*  57:102 */               this.m_sons[j] = getNewDecList(localInstances[j], true);
/*  58:    */             }
/*  59:    */           }
/*  60:105 */           if (i < 2)
/*  61:    */           {
/*  62:106 */             this.m_localModel = noSplit;
/*  63:107 */             this.m_isLeaf = true;
/*  64:108 */             this.m_sons = null;
/*  65:109 */             if (Utils.eq(sumOfWeights, 0.0D)) {
/*  66:110 */               this.m_isEmpty = true;
/*  67:    */             }
/*  68:112 */             return;
/*  69:    */           }
/*  70:114 */           ind = 0;
/*  71:115 */           break;
/*  72:    */         }
/*  73:117 */         this.m_sons[ind] = getNewDecList(localInstances[ind], false);
/*  74:119 */       } while ((i < this.m_sons.length) && (this.m_sons[ind].m_isLeaf));
/*  75:122 */       for (int j = 0; j < this.m_sons.length; j++) {
/*  76:123 */         if ((this.m_sons[j] == null) || (!this.m_sons[j].m_isLeaf)) {
/*  77:    */           break;
/*  78:    */         }
/*  79:    */       }
/*  80:127 */       if (j == this.m_sons.length)
/*  81:    */       {
/*  82:128 */         pruneEnd();
/*  83:129 */         if (!this.m_isLeaf) {
/*  84:130 */           this.indeX = chooseLastIndex();
/*  85:    */         }
/*  86:    */       }
/*  87:    */       else
/*  88:    */       {
/*  89:133 */         this.indeX = chooseLastIndex();
/*  90:    */       }
/*  91:    */     }
/*  92:    */     else
/*  93:    */     {
/*  94:136 */       this.m_isLeaf = true;
/*  95:137 */       if (Utils.eq(sumOfWeights, 0.0D)) {
/*  96:138 */         this.m_isEmpty = true;
/*  97:    */       }
/*  98:    */     }
/*  99:    */   }
/* 100:    */   
/* 101:    */   protected ClassifierDecList getNewDecList(Instances data, boolean leaf)
/* 102:    */     throws Exception
/* 103:    */   {
/* 104:152 */     C45PruneableDecList newDecList = new C45PruneableDecList(this.m_toSelectModel, this.CF, this.m_minNumObj);
/* 105:    */     
/* 106:    */ 
/* 107:155 */     newDecList.buildDecList(data, leaf);
/* 108:    */     
/* 109:157 */     return newDecList;
/* 110:    */   }
/* 111:    */   
/* 112:    */   protected void pruneEnd()
/* 113:    */   {
/* 114:167 */     double errorsTree = getEstimatedErrorsForTree();
/* 115:168 */     double errorsLeaf = getEstimatedErrorsForLeaf();
/* 116:169 */     if (Utils.smOrEq(errorsLeaf, errorsTree + 0.1D))
/* 117:    */     {
/* 118:170 */       this.m_isLeaf = true;
/* 119:171 */       this.m_sons = null;
/* 120:172 */       this.m_localModel = new NoSplit(localModel().distribution());
/* 121:    */     }
/* 122:    */   }
/* 123:    */   
/* 124:    */   private double getEstimatedErrorsForTree()
/* 125:    */   {
/* 126:181 */     if (this.m_isLeaf) {
/* 127:182 */       return getEstimatedErrorsForLeaf();
/* 128:    */     }
/* 129:184 */     double error = 0.0D;
/* 130:185 */     for (int i = 0; i < this.m_sons.length; i++) {
/* 131:186 */       if (!Utils.eq(son(i).localModel().distribution().total(), 0.0D)) {
/* 132:187 */         error += ((C45PruneableDecList)son(i)).getEstimatedErrorsForTree();
/* 133:    */       }
/* 134:    */     }
/* 135:190 */     return error;
/* 136:    */   }
/* 137:    */   
/* 138:    */   public double getEstimatedErrorsForLeaf()
/* 139:    */   {
/* 140:199 */     double errors = localModel().distribution().numIncorrect();
/* 141:    */     
/* 142:201 */     return errors + Stats.addErrs(localModel().distribution().total(), errors, (float)this.CF);
/* 143:    */   }
/* 144:    */   
/* 145:    */   public String getRevision()
/* 146:    */   {
/* 147:212 */     return RevisionUtils.extract("$Revision: 10153 $");
/* 148:    */   }
/* 149:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.rules.part.C45PruneableDecList
 * JD-Core Version:    0.7.0.1
 */