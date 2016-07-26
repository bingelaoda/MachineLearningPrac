/*   1:    */ package weka.classifiers.trees.j48;
/*   2:    */ 
/*   3:    */ import java.util.Random;
/*   4:    */ import weka.core.Capabilities;
/*   5:    */ import weka.core.Capabilities.Capability;
/*   6:    */ import weka.core.Instances;
/*   7:    */ import weka.core.RevisionUtils;
/*   8:    */ import weka.core.Utils;
/*   9:    */ 
/*  10:    */ public class PruneableClassifierTree
/*  11:    */   extends ClassifierTree
/*  12:    */ {
/*  13:    */   static final long serialVersionUID = -555775736857600201L;
/*  14: 46 */   protected boolean pruneTheTree = false;
/*  15: 49 */   protected int numSets = 3;
/*  16: 52 */   protected boolean m_cleanup = true;
/*  17: 55 */   protected int m_seed = 1;
/*  18:    */   
/*  19:    */   public PruneableClassifierTree(ModelSelection toSelectLocModel, boolean pruneTree, int num, boolean cleanup, int seed)
/*  20:    */     throws Exception
/*  21:    */   {
/*  22: 73 */     super(toSelectLocModel);
/*  23:    */     
/*  24: 75 */     this.pruneTheTree = pruneTree;
/*  25: 76 */     this.numSets = num;
/*  26: 77 */     this.m_cleanup = cleanup;
/*  27: 78 */     this.m_seed = seed;
/*  28:    */   }
/*  29:    */   
/*  30:    */   public Capabilities getCapabilities()
/*  31:    */   {
/*  32: 87 */     Capabilities result = super.getCapabilities();
/*  33: 88 */     result.disableAll();
/*  34:    */     
/*  35:    */ 
/*  36: 91 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  37: 92 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  38: 93 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*  39: 94 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  40:    */     
/*  41:    */ 
/*  42: 97 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  43: 98 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  44:    */     
/*  45:    */ 
/*  46:101 */     result.setMinimumNumberInstances(0);
/*  47:    */     
/*  48:103 */     return result;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public void buildClassifier(Instances data)
/*  52:    */     throws Exception
/*  53:    */   {
/*  54:116 */     getCapabilities().testWithFail(data);
/*  55:    */     
/*  56:    */ 
/*  57:119 */     data = new Instances(data);
/*  58:120 */     data.deleteWithMissingClass();
/*  59:    */     
/*  60:122 */     Random random = new Random(this.m_seed);
/*  61:123 */     data.stratify(this.numSets);
/*  62:124 */     buildTree(data.trainCV(this.numSets, this.numSets - 1, random), data.testCV(this.numSets, this.numSets - 1), !this.m_cleanup);
/*  63:126 */     if (this.pruneTheTree) {
/*  64:127 */       prune();
/*  65:    */     }
/*  66:129 */     if (this.m_cleanup) {
/*  67:130 */       cleanup(new Instances(data, 0));
/*  68:    */     }
/*  69:    */   }
/*  70:    */   
/*  71:    */   public void prune()
/*  72:    */     throws Exception
/*  73:    */   {
/*  74:141 */     if (!this.m_isLeaf)
/*  75:    */     {
/*  76:144 */       for (int i = 0; i < this.m_sons.length; i++) {
/*  77:145 */         son(i).prune();
/*  78:    */       }
/*  79:148 */       if (Utils.smOrEq(errorsForLeaf(), errorsForTree()))
/*  80:    */       {
/*  81:151 */         this.m_sons = null;
/*  82:152 */         this.m_isLeaf = true;
/*  83:    */         
/*  84:    */ 
/*  85:155 */         this.m_localModel = new NoSplit(localModel().distribution());
/*  86:    */       }
/*  87:    */     }
/*  88:    */   }
/*  89:    */   
/*  90:    */   protected ClassifierTree getNewTree(Instances train, Instances test)
/*  91:    */     throws Exception
/*  92:    */   {
/*  93:171 */     PruneableClassifierTree newTree = new PruneableClassifierTree(this.m_toSelectModel, this.pruneTheTree, this.numSets, this.m_cleanup, this.m_seed);
/*  94:    */     
/*  95:    */ 
/*  96:174 */     newTree.buildTree(train, test, !this.m_cleanup);
/*  97:175 */     return newTree;
/*  98:    */   }
/*  99:    */   
/* 100:    */   private double errorsForTree()
/* 101:    */     throws Exception
/* 102:    */   {
/* 103:186 */     double errors = 0.0D;
/* 104:188 */     if (this.m_isLeaf) {
/* 105:189 */       return errorsForLeaf();
/* 106:    */     }
/* 107:191 */     for (int i = 0; i < this.m_sons.length; i++) {
/* 108:192 */       if (Utils.eq(localModel().distribution().perBag(i), 0.0D)) {
/* 109:193 */         errors += this.m_test.perBag(i) - this.m_test.perClassPerBag(i, localModel().distribution().maxClass());
/* 110:    */       } else {
/* 111:197 */         errors += son(i).errorsForTree();
/* 112:    */       }
/* 113:    */     }
/* 114:199 */     return errors;
/* 115:    */   }
/* 116:    */   
/* 117:    */   private double errorsForLeaf()
/* 118:    */     throws Exception
/* 119:    */   {
/* 120:211 */     return this.m_test.total() - this.m_test.perClass(localModel().distribution().maxClass());
/* 121:    */   }
/* 122:    */   
/* 123:    */   private ClassifierSplitModel localModel()
/* 124:    */   {
/* 125:220 */     return this.m_localModel;
/* 126:    */   }
/* 127:    */   
/* 128:    */   private PruneableClassifierTree son(int index)
/* 129:    */   {
/* 130:228 */     return (PruneableClassifierTree)this.m_sons[index];
/* 131:    */   }
/* 132:    */   
/* 133:    */   public String getRevision()
/* 134:    */   {
/* 135:237 */     return RevisionUtils.extract("$Revision: 11006 $");
/* 136:    */   }
/* 137:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.j48.PruneableClassifierTree
 * JD-Core Version:    0.7.0.1
 */