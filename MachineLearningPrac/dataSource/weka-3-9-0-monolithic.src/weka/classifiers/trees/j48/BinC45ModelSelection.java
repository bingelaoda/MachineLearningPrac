/*   1:    */ package weka.classifiers.trees.j48;
/*   2:    */ 
/*   3:    */ import java.util.Enumeration;
/*   4:    */ import weka.core.Attribute;
/*   5:    */ import weka.core.Instances;
/*   6:    */ import weka.core.RevisionUtils;
/*   7:    */ import weka.core.Utils;
/*   8:    */ 
/*   9:    */ public class BinC45ModelSelection
/*  10:    */   extends ModelSelection
/*  11:    */ {
/*  12:    */   private static final long serialVersionUID = 179170923545122001L;
/*  13:    */   protected final int m_minNoObj;
/*  14:    */   protected final boolean m_useMDLcorrection;
/*  15:    */   protected Instances m_allData;
/*  16:    */   protected final boolean m_doNotMakeSplitPointActualValue;
/*  17:    */   
/*  18:    */   public BinC45ModelSelection(int minNoObj, Instances allData, boolean useMDLcorrection, boolean doNotMakeSplitPointActualValue)
/*  19:    */   {
/*  20: 66 */     this.m_minNoObj = minNoObj;
/*  21: 67 */     this.m_allData = allData;
/*  22: 68 */     this.m_useMDLcorrection = useMDLcorrection;
/*  23: 69 */     this.m_doNotMakeSplitPointActualValue = doNotMakeSplitPointActualValue;
/*  24:    */   }
/*  25:    */   
/*  26:    */   public void cleanup()
/*  27:    */   {
/*  28: 77 */     this.m_allData = null;
/*  29:    */   }
/*  30:    */   
/*  31:    */   public final ClassifierSplitModel selectModel(Instances data)
/*  32:    */   {
/*  33: 88 */     BinC45Split bestModel = null;
/*  34: 89 */     NoSplit noSplitModel = null;
/*  35: 90 */     double averageInfoGain = 0.0D;
/*  36: 91 */     int validModels = 0;
/*  37: 92 */     boolean multiVal = true;
/*  38:    */     try
/*  39:    */     {
/*  40:101 */       Distribution checkDistribution = new Distribution(data);
/*  41:102 */       noSplitModel = new NoSplit(checkDistribution);
/*  42:103 */       if ((Utils.sm(checkDistribution.total(), 2 * this.m_minNoObj)) || (Utils.eq(checkDistribution.total(), checkDistribution.perClass(checkDistribution.maxClass())))) {
/*  43:106 */         return noSplitModel;
/*  44:    */       }
/*  45:111 */       Enumeration<Attribute> enu = data.enumerateAttributes();
/*  46:112 */       while (enu.hasMoreElements())
/*  47:    */       {
/*  48:113 */         Attribute attribute = (Attribute)enu.nextElement();
/*  49:114 */         if ((attribute.isNumeric()) || (Utils.sm(attribute.numValues(), 0.3D * this.m_allData.numInstances())))
/*  50:    */         {
/*  51:116 */           multiVal = false;
/*  52:117 */           break;
/*  53:    */         }
/*  54:    */       }
/*  55:120 */       BinC45Split[] currentModel = new BinC45Split[data.numAttributes()];
/*  56:121 */       double sumOfWeights = data.sumOfWeights();
/*  57:124 */       for (int i = 0; i < data.numAttributes(); i++) {
/*  58:127 */         if (i != data.classIndex())
/*  59:    */         {
/*  60:130 */           currentModel[i] = new BinC45Split(i, this.m_minNoObj, sumOfWeights, this.m_useMDLcorrection);
/*  61:    */           
/*  62:132 */           currentModel[i].buildClassifier(data);
/*  63:137 */           if ((currentModel[i].checkModel()) && (
/*  64:138 */             (data.attribute(i).isNumeric()) || (multiVal) || (Utils.sm(data.attribute(i).numValues(), 0.3D * this.m_allData.numInstances()))))
/*  65:    */           {
/*  66:141 */             averageInfoGain += currentModel[i].infoGain();
/*  67:142 */             validModels++;
/*  68:    */           }
/*  69:    */         }
/*  70:    */         else
/*  71:    */         {
/*  72:146 */           currentModel[i] = null;
/*  73:    */         }
/*  74:    */       }
/*  75:151 */       if (validModels == 0) {
/*  76:152 */         return noSplitModel;
/*  77:    */       }
/*  78:154 */       averageInfoGain /= validModels;
/*  79:    */       
/*  80:    */ 
/*  81:157 */       double minResult = 0.0D;
/*  82:158 */       for (i = 0; i < data.numAttributes(); i++) {
/*  83:159 */         if ((i != data.classIndex()) && (currentModel[i].checkModel())) {
/*  84:162 */           if ((currentModel[i].infoGain() >= averageInfoGain - 0.001D) && (Utils.gr(currentModel[i].gainRatio(), minResult)))
/*  85:    */           {
/*  86:164 */             bestModel = currentModel[i];
/*  87:165 */             minResult = currentModel[i].gainRatio();
/*  88:    */           }
/*  89:    */         }
/*  90:    */       }
/*  91:171 */       if (Utils.eq(minResult, 0.0D)) {
/*  92:172 */         return noSplitModel;
/*  93:    */       }
/*  94:178 */       bestModel.distribution().addInstWithUnknown(data, bestModel.attIndex());
/*  95:181 */       if (!this.m_doNotMakeSplitPointActualValue) {
/*  96:182 */         bestModel.setSplitPoint(this.m_allData);
/*  97:    */       }
/*  98:184 */       return bestModel;
/*  99:    */     }
/* 100:    */     catch (Exception e)
/* 101:    */     {
/* 102:186 */       e.printStackTrace();
/* 103:    */     }
/* 104:188 */     return null;
/* 105:    */   }
/* 106:    */   
/* 107:    */   public final ClassifierSplitModel selectModel(Instances train, Instances test)
/* 108:    */   {
/* 109:197 */     return selectModel(train);
/* 110:    */   }
/* 111:    */   
/* 112:    */   public String getRevision()
/* 113:    */   {
/* 114:207 */     return RevisionUtils.extract("$Revision: 10531 $");
/* 115:    */   }
/* 116:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.j48.BinC45ModelSelection
 * JD-Core Version:    0.7.0.1
 */