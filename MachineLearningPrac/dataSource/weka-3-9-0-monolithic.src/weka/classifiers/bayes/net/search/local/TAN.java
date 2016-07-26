/*   1:    */ package weka.classifiers.bayes.net.search.local;
/*   2:    */ 
/*   3:    */ import java.util.Enumeration;
/*   4:    */ import weka.classifiers.bayes.BayesNet;
/*   5:    */ import weka.classifiers.bayes.net.ParentSet;
/*   6:    */ import weka.core.Instances;
/*   7:    */ import weka.core.Option;
/*   8:    */ import weka.core.RevisionUtils;
/*   9:    */ import weka.core.TechnicalInformation;
/*  10:    */ import weka.core.TechnicalInformation.Field;
/*  11:    */ import weka.core.TechnicalInformation.Type;
/*  12:    */ import weka.core.TechnicalInformationHandler;
/*  13:    */ 
/*  14:    */ public class TAN
/*  15:    */   extends LocalScoreSearchAlgorithm
/*  16:    */   implements TechnicalInformationHandler
/*  17:    */ {
/*  18:    */   static final long serialVersionUID = 965182127977228690L;
/*  19:    */   
/*  20:    */   public TechnicalInformation getTechnicalInformation()
/*  21:    */   {
/*  22:101 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.ARTICLE);
/*  23:102 */     result.setValue(TechnicalInformation.Field.AUTHOR, "N. Friedman and D. Geiger and M. Goldszmidt");
/*  24:    */     
/*  25:104 */     result.setValue(TechnicalInformation.Field.YEAR, "1997");
/*  26:105 */     result.setValue(TechnicalInformation.Field.TITLE, "Bayesian network classifiers");
/*  27:106 */     result.setValue(TechnicalInformation.Field.JOURNAL, "Machine Learning");
/*  28:107 */     result.setValue(TechnicalInformation.Field.VOLUME, "29");
/*  29:108 */     result.setValue(TechnicalInformation.Field.NUMBER, "2-3");
/*  30:109 */     result.setValue(TechnicalInformation.Field.PAGES, "131-163");
/*  31:    */     
/*  32:111 */     return result;
/*  33:    */   }
/*  34:    */   
/*  35:    */   public void buildStructure(BayesNet bayesNet, Instances instances)
/*  36:    */     throws Exception
/*  37:    */   {
/*  38:126 */     this.m_bInitAsNaiveBayes = true;
/*  39:127 */     this.m_nMaxNrOfParents = 2;
/*  40:128 */     super.buildStructure(bayesNet, instances);
/*  41:129 */     int nNrOfAtts = instances.numAttributes();
/*  42:131 */     if (nNrOfAtts <= 2) {
/*  43:132 */       return;
/*  44:    */     }
/*  45:136 */     double[] fBaseScores = new double[instances.numAttributes()];
/*  46:138 */     for (int iAttribute = 0; iAttribute < nNrOfAtts; iAttribute++) {
/*  47:139 */       fBaseScores[iAttribute] = calcNodeScore(iAttribute);
/*  48:    */     }
/*  49:143 */     double[][] fScore = new double[nNrOfAtts][nNrOfAtts];
/*  50:145 */     for (int iAttributeHead = 0; iAttributeHead < nNrOfAtts; iAttributeHead++) {
/*  51:146 */       for (int iAttributeTail = 0; iAttributeTail < nNrOfAtts; iAttributeTail++) {
/*  52:147 */         if (iAttributeHead != iAttributeTail) {
/*  53:148 */           fScore[iAttributeHead][iAttributeTail] = calcScoreWithExtraParent(iAttributeHead, iAttributeTail);
/*  54:    */         }
/*  55:    */       }
/*  56:    */     }
/*  57:159 */     int nClassNode = instances.classIndex();
/*  58:160 */     int[] link1 = new int[nNrOfAtts - 1];
/*  59:161 */     int[] link2 = new int[nNrOfAtts - 1];
/*  60:162 */     boolean[] linked = new boolean[nNrOfAtts];
/*  61:    */     
/*  62:    */ 
/*  63:165 */     int nBestLinkNode1 = -1;
/*  64:166 */     int nBestLinkNode2 = -1;
/*  65:167 */     double fBestDeltaScore = 0.0D;
/*  66:169 */     for (int iLinkNode1 = 0; iLinkNode1 < nNrOfAtts; iLinkNode1++) {
/*  67:170 */       if (iLinkNode1 != nClassNode) {
/*  68:171 */         for (int iLinkNode2 = 0; iLinkNode2 < nNrOfAtts; iLinkNode2++) {
/*  69:172 */           if ((iLinkNode1 != iLinkNode2) && (iLinkNode2 != nClassNode) && ((nBestLinkNode1 == -1) || (fScore[iLinkNode1][iLinkNode2] - fBaseScores[iLinkNode1] > fBestDeltaScore)))
/*  70:    */           {
/*  71:176 */             fBestDeltaScore = fScore[iLinkNode1][iLinkNode2] - fBaseScores[iLinkNode1];
/*  72:    */             
/*  73:178 */             nBestLinkNode1 = iLinkNode2;
/*  74:179 */             nBestLinkNode2 = iLinkNode1;
/*  75:    */           }
/*  76:    */         }
/*  77:    */       }
/*  78:    */     }
/*  79:184 */     link1[0] = nBestLinkNode1;
/*  80:185 */     link2[0] = nBestLinkNode2;
/*  81:186 */     linked[nBestLinkNode1] = true;
/*  82:187 */     linked[nBestLinkNode2] = true;
/*  83:191 */     for (int iLink = 1; iLink < nNrOfAtts - 2; iLink++)
/*  84:    */     {
/*  85:192 */       nBestLinkNode1 = -1;
/*  86:193 */       for (iLinkNode1 = 0; iLinkNode1 < nNrOfAtts; iLinkNode1++) {
/*  87:194 */         if (iLinkNode1 != nClassNode) {
/*  88:195 */           for (int iLinkNode2 = 0; iLinkNode2 < nNrOfAtts; iLinkNode2++) {
/*  89:196 */             if ((iLinkNode1 != iLinkNode2) && (iLinkNode2 != nClassNode) && ((linked[iLinkNode1] != 0) || (linked[iLinkNode2] != 0)) && ((linked[iLinkNode1] == 0) || (linked[iLinkNode2] == 0)) && ((nBestLinkNode1 == -1) || (fScore[iLinkNode1][iLinkNode2] - fBaseScores[iLinkNode1] > fBestDeltaScore)))
/*  90:    */             {
/*  91:202 */               fBestDeltaScore = fScore[iLinkNode1][iLinkNode2] - fBaseScores[iLinkNode1];
/*  92:    */               
/*  93:204 */               nBestLinkNode1 = iLinkNode2;
/*  94:205 */               nBestLinkNode2 = iLinkNode1;
/*  95:    */             }
/*  96:    */           }
/*  97:    */         }
/*  98:    */       }
/*  99:211 */       link1[iLink] = nBestLinkNode1;
/* 100:212 */       link2[iLink] = nBestLinkNode2;
/* 101:213 */       linked[nBestLinkNode1] = true;
/* 102:214 */       linked[nBestLinkNode2] = true;
/* 103:    */     }
/* 104:218 */     boolean[] hasParent = new boolean[nNrOfAtts];
/* 105:219 */     for (int iLink = 0; iLink < nNrOfAtts - 2; iLink++) {
/* 106:220 */       if (hasParent[link1[iLink]] == 0)
/* 107:    */       {
/* 108:221 */         bayesNet.getParentSet(link1[iLink]).addParent(link2[iLink], instances);
/* 109:222 */         hasParent[link1[iLink]] = true;
/* 110:    */       }
/* 111:    */       else
/* 112:    */       {
/* 113:224 */         if (hasParent[link2[iLink]] != 0) {
/* 114:225 */           throw new Exception("Bug condition found: too many arrows");
/* 115:    */         }
/* 116:227 */         bayesNet.getParentSet(link2[iLink]).addParent(link1[iLink], instances);
/* 117:228 */         hasParent[link2[iLink]] = true;
/* 118:    */       }
/* 119:    */     }
/* 120:    */   }
/* 121:    */   
/* 122:    */   public Enumeration<Option> listOptions()
/* 123:    */   {
/* 124:241 */     return super.listOptions();
/* 125:    */   }
/* 126:    */   
/* 127:    */   public void setOptions(String[] options)
/* 128:    */     throws Exception
/* 129:    */   {
/* 130:271 */     super.setOptions(options);
/* 131:    */   }
/* 132:    */   
/* 133:    */   public String[] getOptions()
/* 134:    */   {
/* 135:281 */     return super.getOptions();
/* 136:    */   }
/* 137:    */   
/* 138:    */   public String globalInfo()
/* 139:    */   {
/* 140:291 */     return "This Bayes Network learning algorithm determines the maximum weight spanning tree  and returns a Naive Bayes network augmented with a tree.\n\nFor more information see:\n\n" + getTechnicalInformation().toString();
/* 141:    */   }
/* 142:    */   
/* 143:    */   public String getRevision()
/* 144:    */   {
/* 145:303 */     return RevisionUtils.extract("$Revision: 10154 $");
/* 146:    */   }
/* 147:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.bayes.net.search.local.TAN
 * JD-Core Version:    0.7.0.1
 */