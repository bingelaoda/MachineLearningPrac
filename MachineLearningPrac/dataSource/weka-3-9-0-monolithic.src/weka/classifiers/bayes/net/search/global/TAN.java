/*   1:    */ package weka.classifiers.bayes.net.search.global;
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
/*  15:    */   extends GlobalScoreSearchAlgorithm
/*  16:    */   implements TechnicalInformationHandler
/*  17:    */ {
/*  18:    */   static final long serialVersionUID = 1715277053980895298L;
/*  19:    */   
/*  20:    */   public TechnicalInformation getTechnicalInformation()
/*  21:    */   {
/*  22:107 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.ARTICLE);
/*  23:108 */     result.setValue(TechnicalInformation.Field.AUTHOR, "N. Friedman and D. Geiger and M. Goldszmidt");
/*  24:    */     
/*  25:110 */     result.setValue(TechnicalInformation.Field.YEAR, "1997");
/*  26:111 */     result.setValue(TechnicalInformation.Field.TITLE, "Bayesian network classifiers");
/*  27:112 */     result.setValue(TechnicalInformation.Field.JOURNAL, "Machine Learning");
/*  28:113 */     result.setValue(TechnicalInformation.Field.VOLUME, "29");
/*  29:114 */     result.setValue(TechnicalInformation.Field.NUMBER, "2-3");
/*  30:115 */     result.setValue(TechnicalInformation.Field.PAGES, "131-163");
/*  31:    */     
/*  32:117 */     return result;
/*  33:    */   }
/*  34:    */   
/*  35:    */   public void buildStructure(BayesNet bayesNet, Instances instances)
/*  36:    */     throws Exception
/*  37:    */   {
/*  38:131 */     this.m_BayesNet = bayesNet;
/*  39:    */     
/*  40:133 */     this.m_bInitAsNaiveBayes = true;
/*  41:134 */     this.m_nMaxNrOfParents = 2;
/*  42:135 */     super.buildStructure(bayesNet, instances);
/*  43:136 */     int nNrOfAtts = instances.numAttributes();
/*  44:    */     
/*  45:    */ 
/*  46:    */ 
/*  47:    */ 
/*  48:    */ 
/*  49:    */ 
/*  50:143 */     int nClassNode = instances.classIndex();
/*  51:144 */     int[] link1 = new int[nNrOfAtts - 1];
/*  52:145 */     int[] link2 = new int[nNrOfAtts - 1];
/*  53:146 */     boolean[] linked = new boolean[nNrOfAtts];
/*  54:    */     
/*  55:148 */     int nBestLinkNode1 = -1;
/*  56:149 */     int nBestLinkNode2 = -1;
/*  57:150 */     double fBestDeltaScore = 0.0D;
/*  58:152 */     for (int iLinkNode1 = 0; iLinkNode1 < nNrOfAtts; iLinkNode1++) {
/*  59:153 */       if (iLinkNode1 != nClassNode) {
/*  60:154 */         for (int iLinkNode2 = 0; iLinkNode2 < nNrOfAtts; iLinkNode2++) {
/*  61:155 */           if ((iLinkNode1 != iLinkNode2) && (iLinkNode2 != nClassNode))
/*  62:    */           {
/*  63:156 */             double fScore = calcScoreWithExtraParent(iLinkNode1, iLinkNode2);
/*  64:157 */             if ((nBestLinkNode1 == -1) || (fScore > fBestDeltaScore))
/*  65:    */             {
/*  66:158 */               fBestDeltaScore = fScore;
/*  67:159 */               nBestLinkNode1 = iLinkNode2;
/*  68:160 */               nBestLinkNode2 = iLinkNode1;
/*  69:    */             }
/*  70:    */           }
/*  71:    */         }
/*  72:    */       }
/*  73:    */     }
/*  74:167 */     link1[0] = nBestLinkNode1;
/*  75:168 */     link2[0] = nBestLinkNode2;
/*  76:169 */     linked[nBestLinkNode1] = true;
/*  77:170 */     linked[nBestLinkNode2] = true;
/*  78:174 */     for (int iLink = 1; iLink < nNrOfAtts - 2; iLink++)
/*  79:    */     {
/*  80:175 */       nBestLinkNode1 = -1;
/*  81:176 */       for (iLinkNode1 = 0; iLinkNode1 < nNrOfAtts; iLinkNode1++) {
/*  82:177 */         if (iLinkNode1 != nClassNode) {
/*  83:178 */           for (int iLinkNode2 = 0; iLinkNode2 < nNrOfAtts; iLinkNode2++) {
/*  84:179 */             if ((iLinkNode1 != iLinkNode2) && (iLinkNode2 != nClassNode) && ((linked[iLinkNode1] != 0) || (linked[iLinkNode2] != 0)) && ((linked[iLinkNode1] == 0) || (linked[iLinkNode2] == 0)))
/*  85:    */             {
/*  86:182 */               double fScore = calcScoreWithExtraParent(iLinkNode1, iLinkNode2);
/*  87:184 */               if ((nBestLinkNode1 == -1) || (fScore > fBestDeltaScore))
/*  88:    */               {
/*  89:185 */                 fBestDeltaScore = fScore;
/*  90:186 */                 nBestLinkNode1 = iLinkNode2;
/*  91:187 */                 nBestLinkNode2 = iLinkNode1;
/*  92:    */               }
/*  93:    */             }
/*  94:    */           }
/*  95:    */         }
/*  96:    */       }
/*  97:193 */       link1[iLink] = nBestLinkNode1;
/*  98:194 */       link2[iLink] = nBestLinkNode2;
/*  99:195 */       linked[nBestLinkNode1] = true;
/* 100:196 */       linked[nBestLinkNode2] = true;
/* 101:    */     }
/* 102:204 */     boolean[] hasParent = new boolean[nNrOfAtts];
/* 103:205 */     for (int iLink = 0; iLink < nNrOfAtts - 2; iLink++) {
/* 104:206 */       if (hasParent[link1[iLink]] == 0)
/* 105:    */       {
/* 106:207 */         bayesNet.getParentSet(link1[iLink]).addParent(link2[iLink], instances);
/* 107:208 */         hasParent[link1[iLink]] = true;
/* 108:    */       }
/* 109:    */       else
/* 110:    */       {
/* 111:210 */         if (hasParent[link2[iLink]] != 0) {
/* 112:211 */           throw new Exception("Bug condition found: too many arrows");
/* 113:    */         }
/* 114:213 */         bayesNet.getParentSet(link2[iLink]).addParent(link1[iLink], instances);
/* 115:214 */         hasParent[link2[iLink]] = true;
/* 116:    */       }
/* 117:    */     }
/* 118:    */   }
/* 119:    */   
/* 120:    */   public Enumeration<Option> listOptions()
/* 121:    */   {
/* 122:227 */     return super.listOptions();
/* 123:    */   }
/* 124:    */   
/* 125:    */   public void setOptions(String[] options)
/* 126:    */     throws Exception
/* 127:    */   {
/* 128:263 */     super.setOptions(options);
/* 129:    */   }
/* 130:    */   
/* 131:    */   public String[] getOptions()
/* 132:    */   {
/* 133:273 */     return super.getOptions();
/* 134:    */   }
/* 135:    */   
/* 136:    */   public String globalInfo()
/* 137:    */   {
/* 138:283 */     return "This Bayes Network learning algorithm determines the maximum weight spanning tree and returns a Naive Bayes network augmented with a tree.\n\nFor more information see:\n\n" + getTechnicalInformation().toString();
/* 139:    */   }
/* 140:    */   
/* 141:    */   public String getRevision()
/* 142:    */   {
/* 143:295 */     return RevisionUtils.extract("$Revision: 10154 $");
/* 144:    */   }
/* 145:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.bayes.net.search.global.TAN
 * JD-Core Version:    0.7.0.1
 */