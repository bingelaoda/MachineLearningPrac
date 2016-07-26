/*   1:    */ package weka.classifiers.rules.part;
/*   2:    */ 
/*   3:    */ import weka.classifiers.trees.j48.ClassifierSplitModel;
/*   4:    */ import weka.classifiers.trees.j48.Distribution;
/*   5:    */ import weka.classifiers.trees.j48.ModelSelection;
/*   6:    */ import weka.classifiers.trees.j48.NoSplit;
/*   7:    */ import weka.core.Instances;
/*   8:    */ import weka.core.RevisionUtils;
/*   9:    */ import weka.core.Utils;
/*  10:    */ 
/*  11:    */ public class PruneableDecList
/*  12:    */   extends ClassifierDecList
/*  13:    */ {
/*  14:    */   private static final long serialVersionUID = -7228103346297172921L;
/*  15:    */   
/*  16:    */   public PruneableDecList(ModelSelection toSelectLocModel, int minNum)
/*  17:    */   {
/*  18: 51 */     super(toSelectLocModel, minNum);
/*  19:    */   }
/*  20:    */   
/*  21:    */   public void buildRule(Instances train, Instances test)
/*  22:    */     throws Exception
/*  23:    */   {
/*  24: 61 */     buildDecList(train, test, false);
/*  25:    */     
/*  26: 63 */     cleanup(new Instances(train, 0));
/*  27:    */   }
/*  28:    */   
/*  29:    */   public void buildDecList(Instances train, Instances test, boolean leaf)
/*  30:    */     throws Exception
/*  31:    */   {
/*  32: 80 */     this.m_train = null;
/*  33: 81 */     this.m_isLeaf = false;
/*  34: 82 */     this.m_isEmpty = false;
/*  35: 83 */     this.m_sons = null;
/*  36: 84 */     this.indeX = 0;
/*  37: 85 */     double sumOfWeights = train.sumOfWeights();
/*  38: 86 */     NoSplit noSplit = new NoSplit(new Distribution(train));
/*  39: 87 */     if (leaf) {
/*  40: 88 */       this.m_localModel = noSplit;
/*  41:    */     } else {
/*  42: 90 */       this.m_localModel = this.m_toSelectModel.selectModel(train, test);
/*  43:    */     }
/*  44: 92 */     this.m_test = new Distribution(test, this.m_localModel);
/*  45: 93 */     if (this.m_localModel.numSubsets() > 1)
/*  46:    */     {
/*  47: 94 */       Instances[] localTrain = this.m_localModel.split(train);
/*  48: 95 */       Instances[] localTest = this.m_localModel.split(test);
/*  49: 96 */       train = null;
/*  50: 97 */       test = null;
/*  51: 98 */       this.m_sons = new ClassifierDecList[this.m_localModel.numSubsets()];
/*  52: 99 */       int i = 0;
/*  53:    */       int ind;
/*  54:    */       do
/*  55:    */       {
/*  56:101 */         i++;
/*  57:102 */         ind = chooseIndex();
/*  58:103 */         if (ind == -1)
/*  59:    */         {
/*  60:104 */           for (int j = 0; j < this.m_sons.length; j++) {
/*  61:105 */             if (this.m_sons[j] == null) {
/*  62:106 */               this.m_sons[j] = getNewDecList(localTrain[j], localTest[j], true);
/*  63:    */             }
/*  64:    */           }
/*  65:109 */           if (i < 2)
/*  66:    */           {
/*  67:110 */             this.m_localModel = noSplit;
/*  68:111 */             this.m_isLeaf = true;
/*  69:112 */             this.m_sons = null;
/*  70:113 */             if (Utils.eq(sumOfWeights, 0.0D)) {
/*  71:114 */               this.m_isEmpty = true;
/*  72:    */             }
/*  73:116 */             return;
/*  74:    */           }
/*  75:118 */           ind = 0;
/*  76:119 */           break;
/*  77:    */         }
/*  78:121 */         this.m_sons[ind] = getNewDecList(localTrain[ind], localTest[ind], false);
/*  79:123 */       } while ((i < this.m_sons.length) && (this.m_sons[ind].m_isLeaf));
/*  80:126 */       for (int j = 0; j < this.m_sons.length; j++) {
/*  81:127 */         if ((this.m_sons[j] == null) || (!this.m_sons[j].m_isLeaf)) {
/*  82:    */           break;
/*  83:    */         }
/*  84:    */       }
/*  85:131 */       if (j == this.m_sons.length)
/*  86:    */       {
/*  87:132 */         pruneEnd();
/*  88:133 */         if (!this.m_isLeaf) {
/*  89:134 */           this.indeX = chooseLastIndex();
/*  90:    */         }
/*  91:    */       }
/*  92:    */       else
/*  93:    */       {
/*  94:137 */         this.indeX = chooseLastIndex();
/*  95:    */       }
/*  96:    */     }
/*  97:    */     else
/*  98:    */     {
/*  99:140 */       this.m_isLeaf = true;
/* 100:141 */       if (Utils.eq(sumOfWeights, 0.0D)) {
/* 101:142 */         this.m_isEmpty = true;
/* 102:    */       }
/* 103:    */     }
/* 104:    */   }
/* 105:    */   
/* 106:    */   protected ClassifierDecList getNewDecList(Instances train, Instances test, boolean leaf)
/* 107:    */     throws Exception
/* 108:    */   {
/* 109:158 */     PruneableDecList newDecList = new PruneableDecList(this.m_toSelectModel, this.m_minNumObj);
/* 110:    */     
/* 111:    */ 
/* 112:161 */     newDecList.buildDecList(train, test, leaf);
/* 113:    */     
/* 114:163 */     return newDecList;
/* 115:    */   }
/* 116:    */   
/* 117:    */   protected void pruneEnd()
/* 118:    */     throws Exception
/* 119:    */   {
/* 120:173 */     double errorsTree = errorsForTree();
/* 121:174 */     double errorsLeaf = errorsForLeaf();
/* 122:175 */     if (Utils.smOrEq(errorsLeaf, errorsTree))
/* 123:    */     {
/* 124:176 */       this.m_isLeaf = true;
/* 125:177 */       this.m_sons = null;
/* 126:178 */       this.m_localModel = new NoSplit(localModel().distribution());
/* 127:    */     }
/* 128:    */   }
/* 129:    */   
/* 130:    */   private double errorsForTree()
/* 131:    */     throws Exception
/* 132:    */   {
/* 133:187 */     if (this.m_isLeaf) {
/* 134:188 */       return errorsForLeaf();
/* 135:    */     }
/* 136:190 */     double error = 0.0D;
/* 137:191 */     for (int i = 0; i < this.m_sons.length; i++) {
/* 138:192 */       if (Utils.eq(son(i).localModel().distribution().total(), 0.0D)) {
/* 139:193 */         error += this.m_test.perBag(i) - this.m_test.perClassPerBag(i, localModel().distribution().maxClass());
/* 140:    */       } else {
/* 141:196 */         error += ((PruneableDecList)son(i)).errorsForTree();
/* 142:    */       }
/* 143:    */     }
/* 144:200 */     return error;
/* 145:    */   }
/* 146:    */   
/* 147:    */   private double errorsForLeaf()
/* 148:    */     throws Exception
/* 149:    */   {
/* 150:209 */     return this.m_test.total() - this.m_test.perClass(localModel().distribution().maxClass());
/* 151:    */   }
/* 152:    */   
/* 153:    */   public String getRevision()
/* 154:    */   {
/* 155:220 */     return RevisionUtils.extract("$Revision: 10153 $");
/* 156:    */   }
/* 157:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.rules.part.PruneableDecList
 * JD-Core Version:    0.7.0.1
 */