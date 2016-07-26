/*   1:    */ package weka.classifiers.trees.j48;
/*   2:    */ 
/*   3:    */ import java.util.Enumeration;
/*   4:    */ import weka.core.Attribute;
/*   5:    */ import weka.core.Instances;
/*   6:    */ import weka.core.RevisionUtils;
/*   7:    */ import weka.core.Utils;
/*   8:    */ 
/*   9:    */ public class C45ModelSelection
/*  10:    */   extends ModelSelection
/*  11:    */ {
/*  12:    */   private static final long serialVersionUID = 3372204862440821989L;
/*  13:    */   protected final int m_minNoObj;
/*  14:    */   protected final boolean m_useMDLcorrection;
/*  15:    */   protected Instances m_allData;
/*  16:    */   protected final boolean m_doNotMakeSplitPointActualValue;
/*  17:    */   
/*  18:    */   public C45ModelSelection(int minNoObj, Instances allData, boolean useMDLcorrection, boolean doNotMakeSplitPointActualValue)
/*  19:    */   {
/*  20: 68 */     this.m_minNoObj = minNoObj;
/*  21: 69 */     this.m_allData = allData;
/*  22: 70 */     this.m_useMDLcorrection = useMDLcorrection;
/*  23: 71 */     this.m_doNotMakeSplitPointActualValue = doNotMakeSplitPointActualValue;
/*  24:    */   }
/*  25:    */   
/*  26:    */   public void cleanup()
/*  27:    */   {
/*  28: 79 */     this.m_allData = null;
/*  29:    */   }
/*  30:    */   
/*  31:    */   public final ClassifierSplitModel selectModel(Instances data)
/*  32:    */   {
/*  33: 90 */     C45Split bestModel = null;
/*  34: 91 */     NoSplit noSplitModel = null;
/*  35: 92 */     double averageInfoGain = 0.0D;
/*  36: 93 */     int validModels = 0;
/*  37: 94 */     boolean multiVal = true;
/*  38:    */     try
/*  39:    */     {
/*  40:104 */       Distribution checkDistribution = new Distribution(data);
/*  41:105 */       noSplitModel = new NoSplit(checkDistribution);
/*  42:106 */       if ((Utils.sm(checkDistribution.total(), 2 * this.m_minNoObj)) || (Utils.eq(checkDistribution.total(), checkDistribution.perClass(checkDistribution.maxClass())))) {
/*  43:109 */         return noSplitModel;
/*  44:    */       }
/*  45:114 */       if (this.m_allData != null)
/*  46:    */       {
/*  47:115 */         Enumeration<Attribute> enu = data.enumerateAttributes();
/*  48:116 */         while (enu.hasMoreElements())
/*  49:    */         {
/*  50:117 */           Attribute attribute = (Attribute)enu.nextElement();
/*  51:118 */           if ((attribute.isNumeric()) || (Utils.sm(attribute.numValues(), 0.3D * this.m_allData.numInstances()))) {
/*  52:121 */             multiVal = false;
/*  53:    */           }
/*  54:    */         }
/*  55:    */       }
/*  56:127 */       C45Split[] currentModel = new C45Split[data.numAttributes()];
/*  57:128 */       double sumOfWeights = data.sumOfWeights();
/*  58:131 */       for (int i = 0; i < data.numAttributes(); i++) {
/*  59:134 */         if (i != data.classIndex())
/*  60:    */         {
/*  61:137 */           currentModel[i] = new C45Split(i, this.m_minNoObj, sumOfWeights, this.m_useMDLcorrection);
/*  62:    */           
/*  63:139 */           currentModel[i].buildClassifier(data);
/*  64:144 */           if (currentModel[i].checkModel()) {
/*  65:145 */             if (this.m_allData != null)
/*  66:    */             {
/*  67:146 */               if ((data.attribute(i).isNumeric()) || (multiVal) || (Utils.sm(data.attribute(i).numValues(), 0.3D * this.m_allData.numInstances())))
/*  68:    */               {
/*  69:149 */                 averageInfoGain += currentModel[i].infoGain();
/*  70:150 */                 validModels++;
/*  71:    */               }
/*  72:    */             }
/*  73:    */             else
/*  74:    */             {
/*  75:153 */               averageInfoGain += currentModel[i].infoGain();
/*  76:154 */               validModels++;
/*  77:    */             }
/*  78:    */           }
/*  79:    */         }
/*  80:    */         else
/*  81:    */         {
/*  82:158 */           currentModel[i] = null;
/*  83:    */         }
/*  84:    */       }
/*  85:163 */       if (validModels == 0) {
/*  86:164 */         return noSplitModel;
/*  87:    */       }
/*  88:166 */       averageInfoGain /= validModels;
/*  89:    */       
/*  90:    */ 
/*  91:169 */       double minResult = 0.0D;
/*  92:170 */       for (i = 0; i < data.numAttributes(); i++) {
/*  93:171 */         if ((i != data.classIndex()) && (currentModel[i].checkModel())) {
/*  94:174 */           if ((currentModel[i].infoGain() >= averageInfoGain - 0.001D) && (Utils.gr(currentModel[i].gainRatio(), minResult)))
/*  95:    */           {
/*  96:176 */             bestModel = currentModel[i];
/*  97:177 */             minResult = currentModel[i].gainRatio();
/*  98:    */           }
/*  99:    */         }
/* 100:    */       }
/* 101:183 */       if (Utils.eq(minResult, 0.0D)) {
/* 102:184 */         return noSplitModel;
/* 103:    */       }
/* 104:190 */       bestModel.distribution().addInstWithUnknown(data, bestModel.attIndex());
/* 105:193 */       if ((this.m_allData != null) && (!this.m_doNotMakeSplitPointActualValue)) {
/* 106:194 */         bestModel.setSplitPoint(this.m_allData);
/* 107:    */       }
/* 108:196 */       return bestModel;
/* 109:    */     }
/* 110:    */     catch (Exception e)
/* 111:    */     {
/* 112:198 */       e.printStackTrace();
/* 113:    */     }
/* 114:200 */     return null;
/* 115:    */   }
/* 116:    */   
/* 117:    */   public final ClassifierSplitModel selectModel(Instances train, Instances test)
/* 118:    */   {
/* 119:209 */     return selectModel(train);
/* 120:    */   }
/* 121:    */   
/* 122:    */   public String getRevision()
/* 123:    */   {
/* 124:219 */     return RevisionUtils.extract("$Revision: 10531 $");
/* 125:    */   }
/* 126:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.j48.C45ModelSelection
 * JD-Core Version:    0.7.0.1
 */