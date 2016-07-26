/*   1:    */ package weka.classifiers.bayes.net.search.local;
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
/*  19:    */   extends LocalScoreSearchAlgorithm
/*  20:    */   implements TechnicalInformationHandler
/*  21:    */ {
/*  22:    */   static final long serialVersionUID = 6951955606060513191L;
/*  23:113 */   double m_fTStart = 10.0D;
/*  24:116 */   double m_fDelta = 0.999D;
/*  25:119 */   int m_nRuns = 10000;
/*  26:122 */   boolean m_bUseArcReversal = false;
/*  27:125 */   int m_nSeed = 1;
/*  28:    */   Random m_random;
/*  29:    */   
/*  30:    */   public TechnicalInformation getTechnicalInformation()
/*  31:    */   {
/*  32:141 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.PHDTHESIS);
/*  33:142 */     result.setValue(TechnicalInformation.Field.AUTHOR, "R.R. Bouckaert");
/*  34:143 */     result.setValue(TechnicalInformation.Field.YEAR, "1995");
/*  35:144 */     result.setValue(TechnicalInformation.Field.TITLE, "Bayesian Belief Networks: from Construction to Inference");
/*  36:    */     
/*  37:146 */     result.setValue(TechnicalInformation.Field.INSTITUTION, "University of Utrecht");
/*  38:147 */     result.setValue(TechnicalInformation.Field.ADDRESS, "Utrecht, Netherlands");
/*  39:    */     
/*  40:149 */     return result;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public void search(BayesNet bayesNet, Instances instances)
/*  44:    */     throws Exception
/*  45:    */   {
/*  46:160 */     this.m_random = new Random(this.m_nSeed);
/*  47:    */     
/*  48:    */ 
/*  49:163 */     double[] fBaseScores = new double[instances.numAttributes()];
/*  50:164 */     double fCurrentScore = 0.0D;
/*  51:165 */     for (int iAttribute = 0; iAttribute < instances.numAttributes(); iAttribute++)
/*  52:    */     {
/*  53:166 */       fBaseScores[iAttribute] = calcNodeScore(iAttribute);
/*  54:167 */       fCurrentScore += fBaseScores[iAttribute];
/*  55:    */     }
/*  56:171 */     double fBestScore = fCurrentScore;
/*  57:172 */     BayesNet bestBayesNet = new BayesNet();
/*  58:173 */     bestBayesNet.m_Instances = instances;
/*  59:174 */     bestBayesNet.initStructure();
/*  60:175 */     copyParentSets(bestBayesNet, bayesNet);
/*  61:    */     
/*  62:177 */     double fTemp = this.m_fTStart;
/*  63:178 */     for (int iRun = 0; iRun < this.m_nRuns; iRun++)
/*  64:    */     {
/*  65:179 */       boolean bRunSucces = false;
/*  66:180 */       double fDeltaScore = 0.0D;
/*  67:181 */       while (!bRunSucces)
/*  68:    */       {
/*  69:183 */         int iTailNode = this.m_random.nextInt(instances.numAttributes());
/*  70:184 */         int iHeadNode = this.m_random.nextInt(instances.numAttributes());
/*  71:185 */         while (iTailNode == iHeadNode) {
/*  72:186 */           iHeadNode = this.m_random.nextInt(instances.numAttributes());
/*  73:    */         }
/*  74:188 */         if (isArc(bayesNet, iHeadNode, iTailNode))
/*  75:    */         {
/*  76:189 */           bRunSucces = true;
/*  77:    */           
/*  78:191 */           bayesNet.getParentSet(iHeadNode).deleteParent(iTailNode, instances);
/*  79:192 */           double fScore = calcNodeScore(iHeadNode);
/*  80:193 */           fDeltaScore = fScore - fBaseScores[iHeadNode];
/*  81:196 */           if (fTemp * Math.log(Math.abs(this.m_random.nextInt()) % 10000 / 10000.0D + 1.0E-100D) < fDeltaScore)
/*  82:    */           {
/*  83:200 */             fCurrentScore += fDeltaScore;
/*  84:201 */             fBaseScores[iHeadNode] = fScore;
/*  85:    */           }
/*  86:    */           else
/*  87:    */           {
/*  88:204 */             bayesNet.getParentSet(iHeadNode).addParent(iTailNode, instances);
/*  89:    */           }
/*  90:    */         }
/*  91:208 */         else if (addArcMakesSense(bayesNet, instances, iHeadNode, iTailNode))
/*  92:    */         {
/*  93:209 */           bRunSucces = true;
/*  94:210 */           double fScore = calcScoreWithExtraParent(iHeadNode, iTailNode);
/*  95:211 */           fDeltaScore = fScore - fBaseScores[iHeadNode];
/*  96:214 */           if (fTemp * Math.log(Math.abs(this.m_random.nextInt()) % 10000 / 10000.0D + 1.0E-100D) < fDeltaScore)
/*  97:    */           {
/*  98:218 */             bayesNet.getParentSet(iHeadNode).addParent(iTailNode, instances);
/*  99:219 */             fBaseScores[iHeadNode] = fScore;
/* 100:220 */             fCurrentScore += fDeltaScore;
/* 101:    */           }
/* 102:    */         }
/* 103:    */       }
/* 104:225 */       if (fCurrentScore > fBestScore) {
/* 105:226 */         copyParentSets(bestBayesNet, bayesNet);
/* 106:    */       }
/* 107:228 */       fTemp *= this.m_fDelta;
/* 108:    */     }
/* 109:231 */     copyParentSets(bayesNet, bestBayesNet);
/* 110:    */   }
/* 111:    */   
/* 112:    */   void copyParentSets(BayesNet dest, BayesNet source)
/* 113:    */   {
/* 114:241 */     int nNodes = source.getNrOfNodes();
/* 115:243 */     for (int iNode = 0; iNode < nNodes; iNode++) {
/* 116:244 */       dest.getParentSet(iNode).copy(source.getParentSet(iNode));
/* 117:    */     }
/* 118:    */   }
/* 119:    */   
/* 120:    */   public double getDelta()
/* 121:    */   {
/* 122:252 */     return this.m_fDelta;
/* 123:    */   }
/* 124:    */   
/* 125:    */   public double getTStart()
/* 126:    */   {
/* 127:259 */     return this.m_fTStart;
/* 128:    */   }
/* 129:    */   
/* 130:    */   public int getRuns()
/* 131:    */   {
/* 132:266 */     return this.m_nRuns;
/* 133:    */   }
/* 134:    */   
/* 135:    */   public void setDelta(double fDelta)
/* 136:    */   {
/* 137:275 */     this.m_fDelta = fDelta;
/* 138:    */   }
/* 139:    */   
/* 140:    */   public void setTStart(double fTStart)
/* 141:    */   {
/* 142:284 */     this.m_fTStart = fTStart;
/* 143:    */   }
/* 144:    */   
/* 145:    */   public void setRuns(int nRuns)
/* 146:    */   {
/* 147:293 */     this.m_nRuns = nRuns;
/* 148:    */   }
/* 149:    */   
/* 150:    */   public int getSeed()
/* 151:    */   {
/* 152:300 */     return this.m_nSeed;
/* 153:    */   }
/* 154:    */   
/* 155:    */   public void setSeed(int nSeed)
/* 156:    */   {
/* 157:309 */     this.m_nSeed = nSeed;
/* 158:    */   }
/* 159:    */   
/* 160:    */   public Enumeration<Option> listOptions()
/* 161:    */   {
/* 162:319 */     Vector<Option> newVector = new Vector(4);
/* 163:    */     
/* 164:321 */     newVector.addElement(new Option("\tStart temperature", "A", 1, "-A <float>"));
/* 165:    */     
/* 166:323 */     newVector.addElement(new Option("\tNumber of runs", "U", 1, "-U <integer>"));
/* 167:    */     
/* 168:325 */     newVector.addElement(new Option("\tDelta temperature", "D", 1, "-D <float>"));
/* 169:    */     
/* 170:327 */     newVector.addElement(new Option("\tRandom number seed", "R", 1, "-R <seed>"));
/* 171:    */     
/* 172:    */ 
/* 173:330 */     newVector.addAll(Collections.list(super.listOptions()));
/* 174:    */     
/* 175:332 */     return newVector.elements();
/* 176:    */   }
/* 177:    */   
/* 178:    */   public void setOptions(String[] options)
/* 179:    */     throws Exception
/* 180:    */   {
/* 181:382 */     String sTStart = Utils.getOption('A', options);
/* 182:383 */     if (sTStart.length() != 0) {
/* 183:384 */       setTStart(Double.parseDouble(sTStart));
/* 184:    */     }
/* 185:386 */     String sRuns = Utils.getOption('U', options);
/* 186:387 */     if (sRuns.length() != 0) {
/* 187:388 */       setRuns(Integer.parseInt(sRuns));
/* 188:    */     }
/* 189:390 */     String sDelta = Utils.getOption('D', options);
/* 190:391 */     if (sDelta.length() != 0) {
/* 191:392 */       setDelta(Double.parseDouble(sDelta));
/* 192:    */     }
/* 193:394 */     String sSeed = Utils.getOption('R', options);
/* 194:395 */     if (sSeed.length() != 0) {
/* 195:396 */       setSeed(Integer.parseInt(sSeed));
/* 196:    */     }
/* 197:398 */     super.setOptions(options);
/* 198:    */   }
/* 199:    */   
/* 200:    */   public String[] getOptions()
/* 201:    */   {
/* 202:409 */     Vector<String> options = new Vector();
/* 203:    */     
/* 204:411 */     options.add("-A");
/* 205:412 */     options.add("" + getTStart());
/* 206:    */     
/* 207:414 */     options.add("-U");
/* 208:415 */     options.add("" + getRuns());
/* 209:    */     
/* 210:417 */     options.add("-D");
/* 211:418 */     options.add("" + getDelta());
/* 212:    */     
/* 213:420 */     options.add("-R");
/* 214:421 */     options.add("" + getSeed());
/* 215:    */     
/* 216:423 */     Collections.addAll(options, super.getOptions());
/* 217:    */     
/* 218:425 */     return (String[])options.toArray(new String[0]);
/* 219:    */   }
/* 220:    */   
/* 221:    */   public String globalInfo()
/* 222:    */   {
/* 223:435 */     return "This Bayes Network learning algorithm uses the general purpose search method of simulated annealing to find a well scoring network structure.\n\nFor more information see:\n\n" + getTechnicalInformation().toString();
/* 224:    */   }
/* 225:    */   
/* 226:    */   public String TStartTipText()
/* 227:    */   {
/* 228:444 */     return "Sets the start temperature of the simulated annealing search. The start temperature determines the probability that a step in the 'wrong' direction in the search space is accepted. The higher the temperature, the higher the probability of acceptance.";
/* 229:    */   }
/* 230:    */   
/* 231:    */   public String runsTipText()
/* 232:    */   {
/* 233:453 */     return "Sets the number of iterations to be performed by the simulated annealing search.";
/* 234:    */   }
/* 235:    */   
/* 236:    */   public String deltaTipText()
/* 237:    */   {
/* 238:460 */     return "Sets the factor with which the temperature (and thus the acceptance probability of steps in the wrong direction in the search space) is decreased in each iteration.";
/* 239:    */   }
/* 240:    */   
/* 241:    */   public String seedTipText()
/* 242:    */   {
/* 243:468 */     return "Initialization value for random number generator. Setting the seed allows replicability of experiments.";
/* 244:    */   }
/* 245:    */   
/* 246:    */   public String getRevision()
/* 247:    */   {
/* 248:479 */     return RevisionUtils.extract("$Revision: 11267 $");
/* 249:    */   }
/* 250:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.bayes.net.search.local.SimulatedAnnealing
 * JD-Core Version:    0.7.0.1
 */