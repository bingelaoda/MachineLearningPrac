/*   1:    */ package weka.classifiers.bayes.net.search.local;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.classifiers.bayes.BayesNet;
/*   8:    */ import weka.classifiers.bayes.net.ParentSet;
/*   9:    */ import weka.core.Instances;
/*  10:    */ import weka.core.Option;
/*  11:    */ import weka.core.RevisionUtils;
/*  12:    */ import weka.core.TechnicalInformation;
/*  13:    */ import weka.core.TechnicalInformation.Field;
/*  14:    */ import weka.core.TechnicalInformation.Type;
/*  15:    */ import weka.core.TechnicalInformationHandler;
/*  16:    */ import weka.core.Utils;
/*  17:    */ 
/*  18:    */ public class TabuSearch
/*  19:    */   extends HillClimber
/*  20:    */   implements TechnicalInformationHandler
/*  21:    */ {
/*  22:    */   static final long serialVersionUID = 1457344073228786447L;
/*  23:132 */   int m_nRuns = 10;
/*  24:135 */   int m_nTabuList = 5;
/*  25:138 */   HillClimber.Operation[] m_oTabuList = null;
/*  26:    */   
/*  27:    */   public TechnicalInformation getTechnicalInformation()
/*  28:    */   {
/*  29:151 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.PHDTHESIS);
/*  30:152 */     result.setValue(TechnicalInformation.Field.AUTHOR, "R.R. Bouckaert");
/*  31:153 */     result.setValue(TechnicalInformation.Field.YEAR, "1995");
/*  32:154 */     result.setValue(TechnicalInformation.Field.TITLE, "Bayesian Belief Networks: from Construction to Inference");
/*  33:    */     
/*  34:156 */     result.setValue(TechnicalInformation.Field.INSTITUTION, "University of Utrecht");
/*  35:157 */     result.setValue(TechnicalInformation.Field.ADDRESS, "Utrecht, Netherlands");
/*  36:    */     
/*  37:159 */     return result;
/*  38:    */   }
/*  39:    */   
/*  40:    */   protected void search(BayesNet bayesNet, Instances instances)
/*  41:    */     throws Exception
/*  42:    */   {
/*  43:173 */     this.m_oTabuList = new HillClimber.Operation[this.m_nTabuList];
/*  44:174 */     int iCurrentTabuList = 0;
/*  45:175 */     initCache(bayesNet, instances);
/*  46:    */     
/*  47:    */ 
/*  48:    */ 
/*  49:179 */     double fCurrentScore = 0.0D;
/*  50:180 */     for (int iAttribute = 0; iAttribute < instances.numAttributes(); iAttribute++) {
/*  51:181 */       fCurrentScore += calcNodeScore(iAttribute);
/*  52:    */     }
/*  53:188 */     double fBestScore = fCurrentScore;
/*  54:189 */     BayesNet bestBayesNet = new BayesNet();
/*  55:190 */     bestBayesNet.m_Instances = instances;
/*  56:191 */     bestBayesNet.initStructure();
/*  57:192 */     copyParentSets(bestBayesNet, bayesNet);
/*  58:195 */     for (int iRun = 0; iRun < this.m_nRuns; iRun++)
/*  59:    */     {
/*  60:196 */       HillClimber.Operation oOperation = getOptimalOperation(bayesNet, instances);
/*  61:197 */       performOperation(bayesNet, instances, oOperation);
/*  62:199 */       if (oOperation == null) {
/*  63:200 */         throw new Exception("Panic: could not find any step to make. Tabu list too long?");
/*  64:    */       }
/*  65:204 */       this.m_oTabuList[iCurrentTabuList] = oOperation;
/*  66:205 */       iCurrentTabuList = (iCurrentTabuList + 1) % this.m_nTabuList;
/*  67:    */       
/*  68:207 */       fCurrentScore += oOperation.m_fDeltaScore;
/*  69:209 */       if (fCurrentScore > fBestScore)
/*  70:    */       {
/*  71:210 */         fBestScore = fCurrentScore;
/*  72:211 */         copyParentSets(bestBayesNet, bayesNet);
/*  73:    */       }
/*  74:214 */       if (bayesNet.getDebug()) {
/*  75:215 */         printTabuList();
/*  76:    */       }
/*  77:    */     }
/*  78:220 */     copyParentSets(bayesNet, bestBayesNet);
/*  79:    */     
/*  80:    */ 
/*  81:223 */     bestBayesNet = null;
/*  82:224 */     this.m_Cache = null;
/*  83:    */   }
/*  84:    */   
/*  85:    */   void copyParentSets(BayesNet dest, BayesNet source)
/*  86:    */   {
/*  87:234 */     int nNodes = source.getNrOfNodes();
/*  88:236 */     for (int iNode = 0; iNode < nNodes; iNode++) {
/*  89:237 */       dest.getParentSet(iNode).copy(source.getParentSet(iNode));
/*  90:    */     }
/*  91:    */   }
/*  92:    */   
/*  93:    */   boolean isNotTabu(HillClimber.Operation oOperation)
/*  94:    */   {
/*  95:249 */     for (int iTabu = 0; iTabu < this.m_nTabuList; iTabu++) {
/*  96:250 */       if (oOperation.equals(this.m_oTabuList[iTabu])) {
/*  97:251 */         return false;
/*  98:    */       }
/*  99:    */     }
/* 100:254 */     return true;
/* 101:    */   }
/* 102:    */   
/* 103:    */   void printTabuList()
/* 104:    */   {
/* 105:261 */     for (int i = 0; i < this.m_nTabuList; i++)
/* 106:    */     {
/* 107:262 */       HillClimber.Operation o = this.m_oTabuList[i];
/* 108:263 */       if (o != null)
/* 109:    */       {
/* 110:264 */         if (o.m_nOperation == 0) {
/* 111:265 */           System.out.print(" +(");
/* 112:    */         } else {
/* 113:267 */           System.out.print(" -(");
/* 114:    */         }
/* 115:269 */         System.out.print(o.m_nTail + "->" + o.m_nHead + ")");
/* 116:    */       }
/* 117:    */     }
/* 118:272 */     System.out.println();
/* 119:    */   }
/* 120:    */   
/* 121:    */   public int getRuns()
/* 122:    */   {
/* 123:279 */     return this.m_nRuns;
/* 124:    */   }
/* 125:    */   
/* 126:    */   public void setRuns(int nRuns)
/* 127:    */   {
/* 128:288 */     this.m_nRuns = nRuns;
/* 129:    */   }
/* 130:    */   
/* 131:    */   public int getTabuList()
/* 132:    */   {
/* 133:295 */     return this.m_nTabuList;
/* 134:    */   }
/* 135:    */   
/* 136:    */   public void setTabuList(int nTabuList)
/* 137:    */   {
/* 138:304 */     this.m_nTabuList = nTabuList;
/* 139:    */   }
/* 140:    */   
/* 141:    */   public Enumeration<Option> listOptions()
/* 142:    */   {
/* 143:314 */     Vector<Option> newVector = new Vector(4);
/* 144:    */     
/* 145:316 */     newVector.addElement(new Option("\tTabu list length", "L", 1, "-L <integer>"));
/* 146:    */     
/* 147:318 */     newVector.addElement(new Option("\tNumber of runs", "U", 1, "-U <integer>"));
/* 148:    */     
/* 149:320 */     newVector.addElement(new Option("\tMaximum number of parents", "P", 1, "-P <nr of parents>"));
/* 150:    */     
/* 151:322 */     newVector.addElement(new Option("\tUse arc reversal operation.\n\t(default false)", "R", 0, "-R"));
/* 152:    */     
/* 153:    */ 
/* 154:325 */     newVector.addAll(Collections.list(super.listOptions()));
/* 155:    */     
/* 156:327 */     return newVector.elements();
/* 157:    */   }
/* 158:    */   
/* 159:    */   public void setOptions(String[] options)
/* 160:    */     throws Exception
/* 161:    */   {
/* 162:394 */     String sTabuList = Utils.getOption('L', options);
/* 163:395 */     if (sTabuList.length() != 0) {
/* 164:396 */       setTabuList(Integer.parseInt(sTabuList));
/* 165:    */     }
/* 166:398 */     String sRuns = Utils.getOption('U', options);
/* 167:399 */     if (sRuns.length() != 0) {
/* 168:400 */       setRuns(Integer.parseInt(sRuns));
/* 169:    */     }
/* 170:403 */     super.setOptions(options);
/* 171:    */   }
/* 172:    */   
/* 173:    */   public String[] getOptions()
/* 174:    */   {
/* 175:414 */     Vector<String> options = new Vector();
/* 176:    */     
/* 177:416 */     options.add("-L");
/* 178:417 */     options.add("" + getTabuList());
/* 179:    */     
/* 180:419 */     options.add("-U");
/* 181:420 */     options.add("" + getRuns());
/* 182:    */     
/* 183:422 */     Collections.addAll(options, super.getOptions());
/* 184:    */     
/* 185:424 */     return (String[])options.toArray(new String[0]);
/* 186:    */   }
/* 187:    */   
/* 188:    */   public String globalInfo()
/* 189:    */   {
/* 190:434 */     return "This Bayes Network learning algorithm uses tabu search for finding a well scoring Bayes network structure. Tabu search is hill climbing till an optimum is reached. The following step is the least worst possible step. The last X steps are kept in a list and none of the steps in this so called tabu list is considered in taking the next step. The best network found in this traversal is returned.\n\nFor more information see:\n\n" + getTechnicalInformation().toString();
/* 191:    */   }
/* 192:    */   
/* 193:    */   public String runsTipText()
/* 194:    */   {
/* 195:446 */     return "Sets the number of steps to be performed.";
/* 196:    */   }
/* 197:    */   
/* 198:    */   public String tabuListTipText()
/* 199:    */   {
/* 200:453 */     return "Sets the length of the tabu list.";
/* 201:    */   }
/* 202:    */   
/* 203:    */   public String getRevision()
/* 204:    */   {
/* 205:463 */     return RevisionUtils.extract("$Revision: 10154 $");
/* 206:    */   }
/* 207:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.bayes.net.search.local.TabuSearch
 * JD-Core Version:    0.7.0.1
 */