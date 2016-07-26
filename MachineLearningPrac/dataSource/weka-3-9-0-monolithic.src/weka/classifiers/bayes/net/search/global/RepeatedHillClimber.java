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
/*  12:    */ import weka.core.Utils;
/*  13:    */ 
/*  14:    */ public class RepeatedHillClimber
/*  15:    */   extends HillClimber
/*  16:    */ {
/*  17:    */   static final long serialVersionUID = -7359197180460703069L;
/*  18:102 */   int m_nRuns = 10;
/*  19:104 */   int m_nSeed = 1;
/*  20:    */   Random m_random;
/*  21:    */   
/*  22:    */   protected void search(BayesNet bayesNet, Instances instances)
/*  23:    */     throws Exception
/*  24:    */   {
/*  25:119 */     this.m_random = new Random(getSeed());
/*  26:    */     
/*  27:    */ 
/*  28:122 */     double fCurrentScore = calcScore(bayesNet);
/*  29:    */     
/*  30:    */ 
/*  31:    */ 
/*  32:    */ 
/*  33:    */ 
/*  34:128 */     double fBestScore = fCurrentScore;
/*  35:129 */     BayesNet bestBayesNet = new BayesNet();
/*  36:130 */     bestBayesNet.m_Instances = instances;
/*  37:131 */     bestBayesNet.initStructure();
/*  38:132 */     copyParentSets(bestBayesNet, bayesNet);
/*  39:135 */     for (int iRun = 0; iRun < this.m_nRuns; iRun++)
/*  40:    */     {
/*  41:137 */       generateRandomNet(bayesNet, instances);
/*  42:    */       
/*  43:    */ 
/*  44:140 */       super.search(bayesNet, instances);
/*  45:    */       
/*  46:    */ 
/*  47:143 */       fCurrentScore = calcScore(bayesNet);
/*  48:146 */       if (fCurrentScore > fBestScore)
/*  49:    */       {
/*  50:147 */         fBestScore = fCurrentScore;
/*  51:148 */         copyParentSets(bestBayesNet, bayesNet);
/*  52:    */       }
/*  53:    */     }
/*  54:153 */     copyParentSets(bayesNet, bestBayesNet);
/*  55:    */     
/*  56:    */ 
/*  57:156 */     bestBayesNet = null;
/*  58:    */   }
/*  59:    */   
/*  60:    */   void generateRandomNet(BayesNet bayesNet, Instances instances)
/*  61:    */   {
/*  62:165 */     int nNodes = instances.numAttributes();
/*  63:167 */     for (int iNode = 0; iNode < nNodes; iNode++)
/*  64:    */     {
/*  65:168 */       ParentSet parentSet = bayesNet.getParentSet(iNode);
/*  66:169 */       while (parentSet.getNrOfParents() > 0) {
/*  67:170 */         parentSet.deleteLastParent(instances);
/*  68:    */       }
/*  69:    */     }
/*  70:175 */     if (getInitAsNaiveBayes())
/*  71:    */     {
/*  72:176 */       int iClass = instances.classIndex();
/*  73:179 */       for (int iNode = 0; iNode < nNodes; iNode++) {
/*  74:180 */         if (iNode != iClass) {
/*  75:181 */           bayesNet.getParentSet(iNode).addParent(iClass, instances);
/*  76:    */         }
/*  77:    */       }
/*  78:    */     }
/*  79:187 */     int nNrOfAttempts = this.m_random.nextInt(nNodes * nNodes);
/*  80:188 */     for (int iAttempt = 0; iAttempt < nNrOfAttempts; iAttempt++)
/*  81:    */     {
/*  82:189 */       int iTail = this.m_random.nextInt(nNodes);
/*  83:190 */       int iHead = this.m_random.nextInt(nNodes);
/*  84:191 */       if ((bayesNet.getParentSet(iHead).getNrOfParents() < getMaxNrOfParents()) && (addArcMakesSense(bayesNet, instances, iHead, iTail))) {
/*  85:193 */         bayesNet.getParentSet(iHead).addParent(iTail, instances);
/*  86:    */       }
/*  87:    */     }
/*  88:    */   }
/*  89:    */   
/*  90:    */   void copyParentSets(BayesNet dest, BayesNet source)
/*  91:    */   {
/*  92:205 */     int nNodes = source.getNrOfNodes();
/*  93:207 */     for (int iNode = 0; iNode < nNodes; iNode++) {
/*  94:208 */       dest.getParentSet(iNode).copy(source.getParentSet(iNode));
/*  95:    */     }
/*  96:    */   }
/*  97:    */   
/*  98:    */   public int getRuns()
/*  99:    */   {
/* 100:218 */     return this.m_nRuns;
/* 101:    */   }
/* 102:    */   
/* 103:    */   public void setRuns(int nRuns)
/* 104:    */   {
/* 105:227 */     this.m_nRuns = nRuns;
/* 106:    */   }
/* 107:    */   
/* 108:    */   public int getSeed()
/* 109:    */   {
/* 110:236 */     return this.m_nSeed;
/* 111:    */   }
/* 112:    */   
/* 113:    */   public void setSeed(int nSeed)
/* 114:    */   {
/* 115:245 */     this.m_nSeed = nSeed;
/* 116:    */   }
/* 117:    */   
/* 118:    */   public Enumeration<Option> listOptions()
/* 119:    */   {
/* 120:255 */     Vector<Option> newVector = new Vector(4);
/* 121:    */     
/* 122:257 */     newVector.addElement(new Option("\tNumber of runs", "U", 1, "-U <integer>"));
/* 123:    */     
/* 124:259 */     newVector.addElement(new Option("\tRandom number seed", "A", 1, "-A <seed>"));
/* 125:    */     
/* 126:    */ 
/* 127:262 */     newVector.addAll(Collections.list(super.listOptions()));
/* 128:    */     
/* 129:264 */     return newVector.elements();
/* 130:    */   }
/* 131:    */   
/* 132:    */   public void setOptions(String[] options)
/* 133:    */     throws Exception
/* 134:    */   {
/* 135:326 */     String sRuns = Utils.getOption('U', options);
/* 136:327 */     if (sRuns.length() != 0) {
/* 137:328 */       setRuns(Integer.parseInt(sRuns));
/* 138:    */     }
/* 139:331 */     String sSeed = Utils.getOption('A', options);
/* 140:332 */     if (sSeed.length() != 0) {
/* 141:333 */       setSeed(Integer.parseInt(sSeed));
/* 142:    */     }
/* 143:336 */     super.setOptions(options);
/* 144:    */   }
/* 145:    */   
/* 146:    */   public String[] getOptions()
/* 147:    */   {
/* 148:347 */     Vector<String> options = new Vector();
/* 149:    */     
/* 150:349 */     options.add("-U");
/* 151:350 */     options.add("" + getRuns());
/* 152:    */     
/* 153:352 */     options.add("-A");
/* 154:353 */     options.add("" + getSeed());
/* 155:    */     
/* 156:355 */     Collections.addAll(options, super.getOptions());
/* 157:    */     
/* 158:357 */     return (String[])options.toArray(new String[0]);
/* 159:    */   }
/* 160:    */   
/* 161:    */   public String globalInfo()
/* 162:    */   {
/* 163:367 */     return "This Bayes Network learning algorithm repeatedly uses hill climbing starting with a randomly generated network structure and return the best structure of the various runs.";
/* 164:    */   }
/* 165:    */   
/* 166:    */   public String runsTipText()
/* 167:    */   {
/* 168:376 */     return "Sets the number of times hill climbing is performed.";
/* 169:    */   }
/* 170:    */   
/* 171:    */   public String seedTipText()
/* 172:    */   {
/* 173:383 */     return "Initialization value for random number generator. Setting the seed allows replicability of experiments.";
/* 174:    */   }
/* 175:    */   
/* 176:    */   public String getRevision()
/* 177:    */   {
/* 178:394 */     return RevisionUtils.extract("$Revision: 10154 $");
/* 179:    */   }
/* 180:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.bayes.net.search.global.RepeatedHillClimber
 * JD-Core Version:    0.7.0.1
 */