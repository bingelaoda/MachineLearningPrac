/*   1:    */ package weka.classifiers.bayes.net.search.global;
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
/*  22:    */   static final long serialVersionUID = 1176705618756672292L;
/*  23:138 */   int m_nRuns = 10;
/*  24:141 */   int m_nTabuList = 5;
/*  25:144 */   HillClimber.Operation[] m_oTabuList = null;
/*  26:    */   
/*  27:    */   public TechnicalInformation getTechnicalInformation()
/*  28:    */   {
/*  29:157 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.PHDTHESIS);
/*  30:158 */     result.setValue(TechnicalInformation.Field.AUTHOR, "R.R. Bouckaert");
/*  31:159 */     result.setValue(TechnicalInformation.Field.YEAR, "1995");
/*  32:160 */     result.setValue(TechnicalInformation.Field.TITLE, "Bayesian Belief Networks: from Construction to Inference");
/*  33:    */     
/*  34:162 */     result.setValue(TechnicalInformation.Field.INSTITUTION, "University of Utrecht");
/*  35:163 */     result.setValue(TechnicalInformation.Field.ADDRESS, "Utrecht, Netherlands");
/*  36:    */     
/*  37:165 */     return result;
/*  38:    */   }
/*  39:    */   
/*  40:    */   protected void search(BayesNet bayesNet, Instances instances)
/*  41:    */     throws Exception
/*  42:    */   {
/*  43:179 */     this.m_oTabuList = new HillClimber.Operation[this.m_nTabuList];
/*  44:180 */     int iCurrentTabuList = 0;
/*  45:    */     
/*  46:    */ 
/*  47:    */ 
/*  48:184 */     double fCurrentScore = calcScore(bayesNet);
/*  49:    */     
/*  50:    */ 
/*  51:    */ 
/*  52:    */ 
/*  53:    */ 
/*  54:190 */     double fBestScore = fCurrentScore;
/*  55:191 */     BayesNet bestBayesNet = new BayesNet();
/*  56:192 */     bestBayesNet.m_Instances = instances;
/*  57:193 */     bestBayesNet.initStructure();
/*  58:194 */     copyParentSets(bestBayesNet, bayesNet);
/*  59:197 */     for (int iRun = 0; iRun < this.m_nRuns; iRun++)
/*  60:    */     {
/*  61:198 */       HillClimber.Operation oOperation = getOptimalOperation(bayesNet, instances);
/*  62:199 */       performOperation(bayesNet, instances, oOperation);
/*  63:201 */       if (oOperation == null) {
/*  64:202 */         throw new Exception("Panic: could not find any step to make. Tabu list too long?");
/*  65:    */       }
/*  66:206 */       this.m_oTabuList[iCurrentTabuList] = oOperation;
/*  67:207 */       iCurrentTabuList = (iCurrentTabuList + 1) % this.m_nTabuList;
/*  68:    */       
/*  69:209 */       fCurrentScore += oOperation.m_fScore;
/*  70:211 */       if (fCurrentScore > fBestScore)
/*  71:    */       {
/*  72:212 */         fBestScore = fCurrentScore;
/*  73:213 */         copyParentSets(bestBayesNet, bayesNet);
/*  74:    */       }
/*  75:216 */       if (bayesNet.getDebug()) {
/*  76:217 */         printTabuList();
/*  77:    */       }
/*  78:    */     }
/*  79:222 */     copyParentSets(bayesNet, bestBayesNet);
/*  80:    */     
/*  81:    */ 
/*  82:225 */     bestBayesNet = null;
/*  83:    */   }
/*  84:    */   
/*  85:    */   void copyParentSets(BayesNet dest, BayesNet source)
/*  86:    */   {
/*  87:235 */     int nNodes = source.getNrOfNodes();
/*  88:237 */     for (int iNode = 0; iNode < nNodes; iNode++) {
/*  89:238 */       dest.getParentSet(iNode).copy(source.getParentSet(iNode));
/*  90:    */     }
/*  91:    */   }
/*  92:    */   
/*  93:    */   boolean isNotTabu(HillClimber.Operation oOperation)
/*  94:    */   {
/*  95:250 */     for (int iTabu = 0; iTabu < this.m_nTabuList; iTabu++) {
/*  96:251 */       if (oOperation.equals(this.m_oTabuList[iTabu])) {
/*  97:252 */         return false;
/*  98:    */       }
/*  99:    */     }
/* 100:255 */     return true;
/* 101:    */   }
/* 102:    */   
/* 103:    */   void printTabuList()
/* 104:    */   {
/* 105:262 */     for (int i = 0; i < this.m_nTabuList; i++)
/* 106:    */     {
/* 107:263 */       HillClimber.Operation o = this.m_oTabuList[i];
/* 108:264 */       if (o != null)
/* 109:    */       {
/* 110:265 */         if (o.m_nOperation == 0) {
/* 111:266 */           System.out.print(" +(");
/* 112:    */         } else {
/* 113:268 */           System.out.print(" -(");
/* 114:    */         }
/* 115:270 */         System.out.print(o.m_nTail + "->" + o.m_nHead + ")");
/* 116:    */       }
/* 117:    */     }
/* 118:273 */     System.out.println();
/* 119:    */   }
/* 120:    */   
/* 121:    */   public int getRuns()
/* 122:    */   {
/* 123:280 */     return this.m_nRuns;
/* 124:    */   }
/* 125:    */   
/* 126:    */   public void setRuns(int nRuns)
/* 127:    */   {
/* 128:289 */     this.m_nRuns = nRuns;
/* 129:    */   }
/* 130:    */   
/* 131:    */   public int getTabuList()
/* 132:    */   {
/* 133:296 */     return this.m_nTabuList;
/* 134:    */   }
/* 135:    */   
/* 136:    */   public void setTabuList(int nTabuList)
/* 137:    */   {
/* 138:305 */     this.m_nTabuList = nTabuList;
/* 139:    */   }
/* 140:    */   
/* 141:    */   public Enumeration<Option> listOptions()
/* 142:    */   {
/* 143:315 */     Vector<Option> newVector = new Vector(4);
/* 144:    */     
/* 145:317 */     newVector.addElement(new Option("\tTabu list length", "L", 1, "-L <integer>"));
/* 146:    */     
/* 147:319 */     newVector.addElement(new Option("\tNumber of runs", "U", 1, "-U <integer>"));
/* 148:    */     
/* 149:321 */     newVector.addElement(new Option("\tMaximum number of parents", "P", 1, "-P <nr of parents>"));
/* 150:    */     
/* 151:323 */     newVector.addElement(new Option("\tUse arc reversal operation.\n\t(default false)", "R", 0, "-R"));
/* 152:    */     
/* 153:    */ 
/* 154:326 */     newVector.addAll(Collections.list(super.listOptions()));
/* 155:    */     
/* 156:328 */     return newVector.elements();
/* 157:    */   }
/* 158:    */   
/* 159:    */   public void setOptions(String[] options)
/* 160:    */     throws Exception
/* 161:    */   {
/* 162:401 */     String sTabuList = Utils.getOption('L', options);
/* 163:402 */     if (sTabuList.length() != 0) {
/* 164:403 */       setTabuList(Integer.parseInt(sTabuList));
/* 165:    */     }
/* 166:405 */     String sRuns = Utils.getOption('U', options);
/* 167:406 */     if (sRuns.length() != 0) {
/* 168:407 */       setRuns(Integer.parseInt(sRuns));
/* 169:    */     }
/* 170:410 */     super.setOptions(options);
/* 171:    */   }
/* 172:    */   
/* 173:    */   public String[] getOptions()
/* 174:    */   {
/* 175:421 */     Vector<String> options = new Vector();
/* 176:    */     
/* 177:423 */     options.add("-L");
/* 178:424 */     options.add("" + getTabuList());
/* 179:    */     
/* 180:426 */     options.add("-U");
/* 181:427 */     options.add("" + getRuns());
/* 182:    */     
/* 183:429 */     Collections.addAll(options, super.getOptions());
/* 184:    */     
/* 185:431 */     return (String[])options.toArray(new String[0]);
/* 186:    */   }
/* 187:    */   
/* 188:    */   public String globalInfo()
/* 189:    */   {
/* 190:441 */     return "This Bayes Network learning algorithm uses tabu search for finding a well scoring Bayes network structure. Tabu search is hill climbing till an optimum is reached. The following step is the least worst possible step. The last X steps are kept in a list and none of the steps in this so called tabu list is considered in taking the next step. The best network found in this traversal is returned.\n\nFor more information see:\n\n" + getTechnicalInformation().toString();
/* 191:    */   }
/* 192:    */   
/* 193:    */   public String runsTipText()
/* 194:    */   {
/* 195:453 */     return "Sets the number of steps to be performed.";
/* 196:    */   }
/* 197:    */   
/* 198:    */   public String tabuListTipText()
/* 199:    */   {
/* 200:460 */     return "Sets the length of the tabu list.";
/* 201:    */   }
/* 202:    */   
/* 203:    */   public String getRevision()
/* 204:    */   {
/* 205:470 */     return RevisionUtils.extract("$Revision: 10154 $");
/* 206:    */   }
/* 207:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.bayes.net.search.global.TabuSearch
 * JD-Core Version:    0.7.0.1
 */