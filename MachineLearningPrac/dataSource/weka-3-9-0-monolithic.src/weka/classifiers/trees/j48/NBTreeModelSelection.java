/*   1:    */ package weka.classifiers.trees.j48;
/*   2:    */ 
/*   3:    */ import java.util.Enumeration;
/*   4:    */ import weka.core.Attribute;
/*   5:    */ import weka.core.Instances;
/*   6:    */ import weka.core.RevisionUtils;
/*   7:    */ import weka.core.Utils;
/*   8:    */ 
/*   9:    */ public class NBTreeModelSelection
/*  10:    */   extends ModelSelection
/*  11:    */ {
/*  12:    */   private static final long serialVersionUID = 990097748931976704L;
/*  13:    */   protected final int m_minNoObj;
/*  14:    */   protected Instances m_allData;
/*  15:    */   
/*  16:    */   public NBTreeModelSelection(int minNoObj, Instances allData)
/*  17:    */   {
/*  18: 57 */     this.m_minNoObj = minNoObj;
/*  19: 58 */     this.m_allData = allData;
/*  20:    */   }
/*  21:    */   
/*  22:    */   public void cleanup()
/*  23:    */   {
/*  24: 66 */     this.m_allData = null;
/*  25:    */   }
/*  26:    */   
/*  27:    */   public final ClassifierSplitModel selectModel(Instances data)
/*  28:    */   {
/*  29: 75 */     double globalErrors = 0.0D;
/*  30:    */     
/*  31:    */ 
/*  32:    */ 
/*  33: 79 */     NBTreeSplit bestModel = null;
/*  34: 80 */     NBTreeNoSplit noSplitModel = null;
/*  35: 81 */     int validModels = 0;
/*  36:    */     try
/*  37:    */     {
/*  38: 89 */       noSplitModel = new NBTreeNoSplit();
/*  39: 90 */       noSplitModel.buildClassifier(data);
/*  40: 91 */       if (data.numInstances() < 5) {
/*  41: 92 */         return noSplitModel;
/*  42:    */       }
/*  43: 96 */       globalErrors = noSplitModel.getErrors();
/*  44: 97 */       if (globalErrors == 0.0D) {
/*  45: 98 */         return noSplitModel;
/*  46:    */       }
/*  47:103 */       Distribution checkDistribution = new Distribution(data);
/*  48:104 */       if ((Utils.sm(checkDistribution.total(), this.m_minNoObj)) || (Utils.eq(checkDistribution.total(), checkDistribution.perClass(checkDistribution.maxClass())))) {
/*  49:107 */         return noSplitModel;
/*  50:    */       }
/*  51:112 */       if (this.m_allData != null)
/*  52:    */       {
/*  53:113 */         Enumeration<Attribute> enu = data.enumerateAttributes();
/*  54:114 */         while (enu.hasMoreElements())
/*  55:    */         {
/*  56:115 */           Attribute attribute = (Attribute)enu.nextElement();
/*  57:116 */           if (!attribute.isNumeric()) {
/*  58:116 */             if (Utils.sm(attribute.numValues(), 0.3D * this.m_allData.numInstances())) {
/*  59:    */               break;
/*  60:    */             }
/*  61:    */           }
/*  62:    */         }
/*  63:    */       }
/*  64:124 */       NBTreeSplit[] currentModel = new NBTreeSplit[data.numAttributes()];
/*  65:125 */       double sumOfWeights = data.sumOfWeights();
/*  66:128 */       for (int i = 0; i < data.numAttributes(); i++) {
/*  67:131 */         if (i != data.classIndex())
/*  68:    */         {
/*  69:134 */           currentModel[i] = new NBTreeSplit(i, this.m_minNoObj, sumOfWeights);
/*  70:135 */           currentModel[i].setGlobalModel(noSplitModel);
/*  71:136 */           currentModel[i].buildClassifier(data);
/*  72:141 */           if (currentModel[i].checkModel()) {
/*  73:142 */             validModels++;
/*  74:    */           }
/*  75:    */         }
/*  76:    */         else
/*  77:    */         {
/*  78:145 */           currentModel[i] = null;
/*  79:    */         }
/*  80:    */       }
/*  81:150 */       if (validModels == 0) {
/*  82:151 */         return noSplitModel;
/*  83:    */       }
/*  84:155 */       double minResult = globalErrors;
/*  85:156 */       for (i = 0; i < data.numAttributes(); i++) {
/*  86:157 */         if ((i != data.classIndex()) && (currentModel[i].checkModel())) {
/*  87:162 */           if (currentModel[i].getErrors() < minResult)
/*  88:    */           {
/*  89:163 */             bestModel = currentModel[i];
/*  90:164 */             minResult = currentModel[i].getErrors();
/*  91:    */           }
/*  92:    */         }
/*  93:    */       }
/*  94:171 */       if ((globalErrors - minResult) / globalErrors < 0.05D) {
/*  95:172 */         return noSplitModel;
/*  96:    */       }
/*  97:183 */       return bestModel;
/*  98:    */     }
/*  99:    */     catch (Exception e)
/* 100:    */     {
/* 101:185 */       e.printStackTrace();
/* 102:    */     }
/* 103:187 */     return null;
/* 104:    */   }
/* 105:    */   
/* 106:    */   public final ClassifierSplitModel selectModel(Instances train, Instances test)
/* 107:    */   {
/* 108:196 */     return selectModel(train);
/* 109:    */   }
/* 110:    */   
/* 111:    */   public String getRevision()
/* 112:    */   {
/* 113:206 */     return RevisionUtils.extract("$Revision: 10531 $");
/* 114:    */   }
/* 115:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.j48.NBTreeModelSelection
 * JD-Core Version:    0.7.0.1
 */