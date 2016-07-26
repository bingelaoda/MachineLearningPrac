/*   1:    */ package weka.classifiers.bayes.net.search.global;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Random;
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
/*  18:    */ public class SimulatedAnnealing
/*  19:    */   extends GlobalScoreSearchAlgorithm
/*  20:    */   implements TechnicalInformationHandler
/*  21:    */ {
/*  22:    */   static final long serialVersionUID = -5482721887881010916L;
/*  23:119 */   double m_fTStart = 10.0D;
/*  24:122 */   double m_fDelta = 0.999D;
/*  25:125 */   int m_nRuns = 10000;
/*  26:128 */   boolean m_bUseArcReversal = false;
/*  27:131 */   int m_nSeed = 1;
/*  28:    */   Random m_random;
/*  29:    */   
/*  30:    */   public TechnicalInformation getTechnicalInformation()
/*  31:    */   {
/*  32:147 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.PHDTHESIS);
/*  33:148 */     result.setValue(TechnicalInformation.Field.AUTHOR, "R.R. Bouckaert");
/*  34:149 */     result.setValue(TechnicalInformation.Field.YEAR, "1995");
/*  35:150 */     result.setValue(TechnicalInformation.Field.TITLE, "Bayesian Belief Networks: from Construction to Inference");
/*  36:    */     
/*  37:152 */     result.setValue(TechnicalInformation.Field.INSTITUTION, "University of Utrecht");
/*  38:153 */     result.setValue(TechnicalInformation.Field.ADDRESS, "Utrecht, Netherlands");
/*  39:    */     
/*  40:155 */     return result;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public void search(BayesNet bayesNet, Instances instances)
/*  44:    */     throws Exception
/*  45:    */   {
/*  46:166 */     this.m_random = new Random(this.m_nSeed);
/*  47:    */     
/*  48:    */ 
/*  49:169 */     double fCurrentScore = calcScore(bayesNet);
/*  50:    */     
/*  51:    */ 
/*  52:172 */     double fBestScore = fCurrentScore;
/*  53:173 */     BayesNet bestBayesNet = new BayesNet();
/*  54:174 */     bestBayesNet.m_Instances = instances;
/*  55:175 */     bestBayesNet.initStructure();
/*  56:176 */     copyParentSets(bestBayesNet, bayesNet);
/*  57:    */     
/*  58:178 */     double fTemp = this.m_fTStart;
/*  59:179 */     for (int iRun = 0; iRun < this.m_nRuns; iRun++)
/*  60:    */     {
/*  61:180 */       boolean bRunSucces = false;
/*  62:181 */       double fDeltaScore = 0.0D;
/*  63:182 */       while (!bRunSucces)
/*  64:    */       {
/*  65:184 */         int iTailNode = this.m_random.nextInt(instances.numAttributes());
/*  66:185 */         int iHeadNode = this.m_random.nextInt(instances.numAttributes());
/*  67:186 */         while (iTailNode == iHeadNode) {
/*  68:187 */           iHeadNode = this.m_random.nextInt(instances.numAttributes());
/*  69:    */         }
/*  70:189 */         if (isArc(bayesNet, iHeadNode, iTailNode))
/*  71:    */         {
/*  72:190 */           bRunSucces = true;
/*  73:    */           
/*  74:192 */           bayesNet.getParentSet(iHeadNode).deleteParent(iTailNode, instances);
/*  75:193 */           double fScore = calcScore(bayesNet);
/*  76:194 */           fDeltaScore = fScore - fCurrentScore;
/*  77:197 */           if (fTemp * Math.log(Math.abs(this.m_random.nextInt()) % 10000 / 10000.0D + 1.0E-100D) < fDeltaScore) {
/*  78:201 */             fCurrentScore = fScore;
/*  79:    */           } else {
/*  80:204 */             bayesNet.getParentSet(iHeadNode).addParent(iTailNode, instances);
/*  81:    */           }
/*  82:    */         }
/*  83:208 */         else if (addArcMakesSense(bayesNet, instances, iHeadNode, iTailNode))
/*  84:    */         {
/*  85:209 */           bRunSucces = true;
/*  86:210 */           double fScore = calcScoreWithExtraParent(iHeadNode, iTailNode);
/*  87:211 */           fDeltaScore = fScore - fCurrentScore;
/*  88:214 */           if (fTemp * Math.log(Math.abs(this.m_random.nextInt()) % 10000 / 10000.0D + 1.0E-100D) < fDeltaScore)
/*  89:    */           {
/*  90:218 */             bayesNet.getParentSet(iHeadNode).addParent(iTailNode, instances);
/*  91:219 */             fCurrentScore = fScore;
/*  92:    */           }
/*  93:    */         }
/*  94:    */       }
/*  95:224 */       if (fCurrentScore > fBestScore) {
/*  96:225 */         copyParentSets(bestBayesNet, bayesNet);
/*  97:    */       }
/*  98:227 */       fTemp *= this.m_fDelta;
/*  99:    */     }
/* 100:230 */     copyParentSets(bayesNet, bestBayesNet);
/* 101:    */   }
/* 102:    */   
/* 103:    */   void copyParentSets(BayesNet dest, BayesNet source)
/* 104:    */   {
/* 105:240 */     int nNodes = source.getNrOfNodes();
/* 106:242 */     for (int iNode = 0; iNode < nNodes; iNode++) {
/* 107:243 */       dest.getParentSet(iNode).copy(source.getParentSet(iNode));
/* 108:    */     }
/* 109:    */   }
/* 110:    */   
/* 111:    */   public double getDelta()
/* 112:    */   {
/* 113:251 */     return this.m_fDelta;
/* 114:    */   }
/* 115:    */   
/* 116:    */   public double getTStart()
/* 117:    */   {
/* 118:258 */     return this.m_fTStart;
/* 119:    */   }
/* 120:    */   
/* 121:    */   public int getRuns()
/* 122:    */   {
/* 123:265 */     return this.m_nRuns;
/* 124:    */   }
/* 125:    */   
/* 126:    */   public void setDelta(double fDelta)
/* 127:    */   {
/* 128:274 */     this.m_fDelta = fDelta;
/* 129:    */   }
/* 130:    */   
/* 131:    */   public void setTStart(double fTStart)
/* 132:    */   {
/* 133:283 */     this.m_fTStart = fTStart;
/* 134:    */   }
/* 135:    */   
/* 136:    */   public void setRuns(int nRuns)
/* 137:    */   {
/* 138:292 */     this.m_nRuns = nRuns;
/* 139:    */   }
/* 140:    */   
/* 141:    */   public int getSeed()
/* 142:    */   {
/* 143:299 */     return this.m_nSeed;
/* 144:    */   }
/* 145:    */   
/* 146:    */   public void setSeed(int nSeed)
/* 147:    */   {
/* 148:308 */     this.m_nSeed = nSeed;
/* 149:    */   }
/* 150:    */   
/* 151:    */   public Enumeration<Option> listOptions()
/* 152:    */   {
/* 153:318 */     Vector<Option> newVector = new Vector(4);
/* 154:    */     
/* 155:320 */     newVector.addElement(new Option("\tStart temperature", "A", 1, "-A <float>"));
/* 156:    */     
/* 157:322 */     newVector.addElement(new Option("\tNumber of runs", "U", 1, "-U <integer>"));
/* 158:    */     
/* 159:324 */     newVector.addElement(new Option("\tDelta temperature", "D", 1, "-D <float>"));
/* 160:    */     
/* 161:326 */     newVector.addElement(new Option("\tRandom number seed", "R", 1, "-R <seed>"));
/* 162:    */     
/* 163:    */ 
/* 164:329 */     newVector.addAll(Collections.list(super.listOptions()));
/* 165:    */     
/* 166:331 */     return newVector.elements();
/* 167:    */   }
/* 168:    */   
/* 169:    */   public void setOptions(String[] options)
/* 170:    */     throws Exception
/* 171:    */   {
/* 172:387 */     String sTStart = Utils.getOption('A', options);
/* 173:388 */     if (sTStart.length() != 0) {
/* 174:389 */       setTStart(Double.parseDouble(sTStart));
/* 175:    */     }
/* 176:391 */     String sRuns = Utils.getOption('U', options);
/* 177:392 */     if (sRuns.length() != 0) {
/* 178:393 */       setRuns(Integer.parseInt(sRuns));
/* 179:    */     }
/* 180:395 */     String sDelta = Utils.getOption('D', options);
/* 181:396 */     if (sDelta.length() != 0) {
/* 182:397 */       setDelta(Double.parseDouble(sDelta));
/* 183:    */     }
/* 184:399 */     String sSeed = Utils.getOption('R', options);
/* 185:400 */     if (sSeed.length() != 0) {
/* 186:401 */       setSeed(Integer.parseInt(sSeed));
/* 187:    */     }
/* 188:403 */     super.setOptions(options);
/* 189:    */   }
/* 190:    */   
/* 191:    */   public String[] getOptions()
/* 192:    */   {
/* 193:414 */     Vector<String> options = new Vector();
/* 194:    */     
/* 195:416 */     options.add("-A");
/* 196:417 */     options.add("" + getTStart());
/* 197:    */     
/* 198:419 */     options.add("-U");
/* 199:420 */     options.add("" + getRuns());
/* 200:    */     
/* 201:422 */     options.add("-D");
/* 202:423 */     options.add("" + getDelta());
/* 203:    */     
/* 204:425 */     options.add("-R");
/* 205:426 */     options.add("" + getSeed());
/* 206:    */     
/* 207:428 */     Collections.addAll(options, super.getOptions());
/* 208:    */     
/* 209:430 */     return (String[])options.toArray(new String[0]);
/* 210:    */   }
/* 211:    */   
/* 212:    */   public String globalInfo()
/* 213:    */   {
/* 214:440 */     return "This Bayes Network learning algorithm uses the general purpose search method of simulated annealing to find a well scoring network structure.\n\nFor more information see:\n\n" + getTechnicalInformation().toString();
/* 215:    */   }
/* 216:    */   
/* 217:    */   public String TStartTipText()
/* 218:    */   {
/* 219:449 */     return "Sets the start temperature of the simulated annealing search. The start temperature determines the probability that a step in the 'wrong' direction in the search space is accepted. The higher the temperature, the higher the probability of acceptance.";
/* 220:    */   }
/* 221:    */   
/* 222:    */   public String runsTipText()
/* 223:    */   {
/* 224:458 */     return "Sets the number of iterations to be performed by the simulated annealing search.";
/* 225:    */   }
/* 226:    */   
/* 227:    */   public String deltaTipText()
/* 228:    */   {
/* 229:465 */     return "Sets the factor with which the temperature (and thus the acceptance probability of steps in the wrong direction in the search space) is decreased in each iteration.";
/* 230:    */   }
/* 231:    */   
/* 232:    */   public String seedTipText()
/* 233:    */   {
/* 234:473 */     return "Initialization value for random number generator. Setting the seed allows replicability of experiments.";
/* 235:    */   }
/* 236:    */   
/* 237:    */   public String getRevision()
/* 238:    */   {
/* 239:484 */     return RevisionUtils.extract("$Revision: 11267 $");
/* 240:    */   }
/* 241:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.bayes.net.search.global.SimulatedAnnealing
 * JD-Core Version:    0.7.0.1
 */