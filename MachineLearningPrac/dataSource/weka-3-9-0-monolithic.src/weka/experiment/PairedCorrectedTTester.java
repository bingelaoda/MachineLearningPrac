/*   1:    */ package weka.experiment;
/*   2:    */ 
/*   3:    */ import java.io.BufferedReader;
/*   4:    */ import java.io.FileReader;
/*   5:    */ import java.io.PrintStream;
/*   6:    */ import java.util.ArrayList;
/*   7:    */ import java.util.Enumeration;
/*   8:    */ import weka.core.Attribute;
/*   9:    */ import weka.core.Instance;
/*  10:    */ import weka.core.Instances;
/*  11:    */ import weka.core.Option;
/*  12:    */ import weka.core.RevisionUtils;
/*  13:    */ import weka.core.TechnicalInformation;
/*  14:    */ import weka.core.TechnicalInformation.Field;
/*  15:    */ import weka.core.TechnicalInformation.Type;
/*  16:    */ import weka.core.TechnicalInformationHandler;
/*  17:    */ import weka.core.Utils;
/*  18:    */ 
/*  19:    */ public class PairedCorrectedTTester
/*  20:    */   extends PairedTTester
/*  21:    */   implements TechnicalInformationHandler
/*  22:    */ {
/*  23:    */   static final long serialVersionUID = -3105268939845653323L;
/*  24:    */   
/*  25:    */   public TechnicalInformation getTechnicalInformation()
/*  26:    */   {
/*  27:152 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.ARTICLE);
/*  28:153 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Claude Nadeau and Yoshua Bengio");
/*  29:154 */     result.setValue(TechnicalInformation.Field.YEAR, "2001");
/*  30:155 */     result.setValue(TechnicalInformation.Field.TITLE, "Inference for the Generalization Error");
/*  31:156 */     result.setValue(TechnicalInformation.Field.JOURNAL, "Machine Learning");
/*  32:157 */     result.setValue(TechnicalInformation.Field.PDF, "http://www.iro.umontreal.ca/~lisa/bib/pub_subject/comparative/pointeurs/nadeau_MLJ1597.pdf");
/*  33:    */     
/*  34:    */ 
/*  35:    */ 
/*  36:    */ 
/*  37:162 */     return result;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public PairedStats calculateStatistics(Instance datasetSpecifier, int resultset1Index, int resultset2Index, int comparisonColumn)
/*  41:    */     throws Exception
/*  42:    */   {
/*  43:181 */     if (this.m_Instances.attribute(comparisonColumn).type() != 0) {
/*  44:182 */       throw new Exception("Comparison column " + (comparisonColumn + 1) + " (" + this.m_Instances.attribute(comparisonColumn).name() + ") is not numeric");
/*  45:    */     }
/*  46:185 */     if (!this.m_ResultsetsValid) {
/*  47:186 */       prepareData();
/*  48:    */     }
/*  49:189 */     PairedTTester.Resultset resultset1 = (PairedTTester.Resultset)this.m_Resultsets.get(resultset1Index);
/*  50:190 */     PairedTTester.Resultset resultset2 = (PairedTTester.Resultset)this.m_Resultsets.get(resultset2Index);
/*  51:191 */     ArrayList<Instance> dataset1 = resultset1.dataset(datasetSpecifier);
/*  52:192 */     ArrayList<Instance> dataset2 = resultset2.dataset(datasetSpecifier);
/*  53:193 */     String datasetName = templateString(datasetSpecifier);
/*  54:194 */     if (dataset1 == null) {
/*  55:195 */       throw new Exception("No results for dataset=" + datasetName + " for resultset=" + resultset1.templateString());
/*  56:    */     }
/*  57:197 */     if (dataset2 == null) {
/*  58:198 */       throw new Exception("No results for dataset=" + datasetName + " for resultset=" + resultset2.templateString());
/*  59:    */     }
/*  60:200 */     if (dataset1.size() != dataset2.size()) {
/*  61:201 */       throw new Exception("Results for dataset=" + datasetName + " differ in size for resultset=" + resultset1.templateString() + " and resultset=" + resultset2.templateString());
/*  62:    */     }
/*  63:207 */     double testTrainRatio = 0.0D;
/*  64:208 */     int trainSizeIndex = -1;
/*  65:209 */     int testSizeIndex = -1;
/*  66:211 */     for (int i = 0; i < this.m_Instances.numAttributes(); i++) {
/*  67:212 */       if (this.m_Instances.attribute(i).name().toLowerCase().equals("number_of_training_instances")) {
/*  68:214 */         trainSizeIndex = i;
/*  69:215 */       } else if (this.m_Instances.attribute(i).name().toLowerCase().equals("number_of_testing_instances")) {
/*  70:217 */         testSizeIndex = i;
/*  71:    */       }
/*  72:    */     }
/*  73:220 */     if ((trainSizeIndex >= 0) && (testSizeIndex >= 0))
/*  74:    */     {
/*  75:221 */       double totalTrainSize = 0.0D;
/*  76:222 */       double totalTestSize = 0.0D;
/*  77:223 */       for (int k = 0; k < dataset1.size(); k++)
/*  78:    */       {
/*  79:224 */         Instance current = (Instance)dataset1.get(k);
/*  80:225 */         totalTrainSize += current.value(trainSizeIndex);
/*  81:226 */         totalTestSize += current.value(testSizeIndex);
/*  82:    */       }
/*  83:228 */       testTrainRatio = totalTestSize / totalTrainSize;
/*  84:    */     }
/*  85:230 */     PairedStats pairedStats = new PairedStatsCorrected(this.m_SignificanceLevel, testTrainRatio);
/*  86:233 */     for (int k = 0; k < dataset1.size(); k++)
/*  87:    */     {
/*  88:234 */       Instance current1 = (Instance)dataset1.get(k);
/*  89:235 */       Instance current2 = (Instance)dataset2.get(k);
/*  90:236 */       if (current1.isMissing(comparisonColumn))
/*  91:    */       {
/*  92:237 */         System.err.println("Instance has missing value in comparison column!\n" + current1);
/*  93:    */       }
/*  94:241 */       else if (current2.isMissing(comparisonColumn))
/*  95:    */       {
/*  96:242 */         System.err.println("Instance has missing value in comparison column!\n" + current2);
/*  97:    */       }
/*  98:    */       else
/*  99:    */       {
/* 100:246 */         if (current1.value(this.m_RunColumn) != current2.value(this.m_RunColumn)) {
/* 101:247 */           System.err.println("Run numbers do not match!\n" + current1 + current2);
/* 102:    */         }
/* 103:249 */         if ((this.m_FoldColumn != -1) && 
/* 104:250 */           (current1.value(this.m_FoldColumn) != current2.value(this.m_FoldColumn))) {
/* 105:251 */           System.err.println("Fold numbers do not match!\n" + current1 + current2);
/* 106:    */         }
/* 107:256 */         double value1 = current1.value(comparisonColumn);
/* 108:257 */         double value2 = current2.value(comparisonColumn);
/* 109:258 */         pairedStats.add(value1, value2);
/* 110:    */       }
/* 111:    */     }
/* 112:260 */     pairedStats.calculateDerived();
/* 113:261 */     return pairedStats;
/* 114:    */   }
/* 115:    */   
/* 116:    */   public static void main(String[] args)
/* 117:    */   {
/* 118:    */     try
/* 119:    */     {
/* 120:272 */       PairedCorrectedTTester tt = new PairedCorrectedTTester();
/* 121:273 */       String datasetName = Utils.getOption('t', args);
/* 122:274 */       String compareColStr = Utils.getOption('c', args);
/* 123:275 */       String baseColStr = Utils.getOption('b', args);
/* 124:276 */       boolean summaryOnly = Utils.getFlag('s', args);
/* 125:277 */       boolean rankingOnly = Utils.getFlag('r', args);
/* 126:    */       try
/* 127:    */       {
/* 128:279 */         if ((datasetName.length() == 0) || (compareColStr.length() == 0)) {
/* 129:280 */           throw new Exception("-t and -c options are required");
/* 130:    */         }
/* 131:282 */         tt.setOptions(args);
/* 132:283 */         Utils.checkForRemainingOptions(args);
/* 133:    */       }
/* 134:    */       catch (Exception ex)
/* 135:    */       {
/* 136:285 */         String result = "";
/* 137:286 */         Enumeration<Option> enu = tt.listOptions();
/* 138:287 */         while (enu.hasMoreElements())
/* 139:    */         {
/* 140:288 */           Option option = (Option)enu.nextElement();
/* 141:289 */           result = result + option.synopsis() + '\n' + option.description() + '\n';
/* 142:    */         }
/* 143:291 */         throw new Exception("Usage:\n\n-t <file>\n\tSet the dataset containing data to evaluate\n-b <index>\n\tSet the resultset to base comparisons against (optional)\n-c <index>\n\tSet the column to perform a comparison on\n-s\n\tSummarize wins over all resultset pairs\n\n-r\n\tGenerate a resultset ranking\n\n" + result);
/* 144:    */       }
/* 145:298 */       Instances data = new Instances(new BufferedReader(new FileReader(datasetName)));
/* 146:    */       
/* 147:300 */       tt.setInstances(data);
/* 148:    */       
/* 149:302 */       int compareCol = Integer.parseInt(compareColStr) - 1;
/* 150:303 */       System.out.println(tt.header(compareCol));
/* 151:304 */       if (rankingOnly)
/* 152:    */       {
/* 153:305 */         System.out.println(tt.multiResultsetRanking(compareCol));
/* 154:    */       }
/* 155:306 */       else if (summaryOnly)
/* 156:    */       {
/* 157:307 */         System.out.println(tt.multiResultsetSummary(compareCol));
/* 158:    */       }
/* 159:    */       else
/* 160:    */       {
/* 161:309 */         System.out.println(tt.resultsetKey());
/* 162:310 */         if (baseColStr.length() == 0)
/* 163:    */         {
/* 164:311 */           for (int i = 0; i < tt.getNumResultsets(); i++) {
/* 165:312 */             System.out.println(tt.multiResultsetFull(i, compareCol));
/* 166:    */           }
/* 167:    */         }
/* 168:    */         else
/* 169:    */         {
/* 170:315 */           int baseCol = Integer.parseInt(baseColStr) - 1;
/* 171:316 */           System.out.println(tt.multiResultsetFull(baseCol, compareCol));
/* 172:    */         }
/* 173:    */       }
/* 174:    */     }
/* 175:    */     catch (Exception e)
/* 176:    */     {
/* 177:320 */       e.printStackTrace();
/* 178:321 */       System.err.println(e.getMessage());
/* 179:    */     }
/* 180:    */   }
/* 181:    */   
/* 182:    */   public String getDisplayName()
/* 183:    */   {
/* 184:332 */     return "Paired T-Tester (corrected)";
/* 185:    */   }
/* 186:    */   
/* 187:    */   public String getToolTipText()
/* 188:    */   {
/* 189:343 */     return "Performs test using corrected resampled t-test statistic (Nadeau and Bengio)";
/* 190:    */   }
/* 191:    */   
/* 192:    */   public String getRevision()
/* 193:    */   {
/* 194:353 */     return RevisionUtils.extract("$Revision: 10203 $");
/* 195:    */   }
/* 196:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.experiment.PairedCorrectedTTester
 * JD-Core Version:    0.7.0.1
 */