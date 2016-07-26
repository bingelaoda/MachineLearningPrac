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
/*  12:    */ import weka.core.Utils;
/*  13:    */ 
/*  14:    */ public class RepeatedHillClimber
/*  15:    */   extends HillClimber
/*  16:    */ {
/*  17:    */   static final long serialVersionUID = -6574084564213041174L;
/*  18: 96 */   int m_nRuns = 10;
/*  19: 98 */   int m_nSeed = 1;
/*  20:    */   Random m_random;
/*  21:    */   
/*  22:    */   protected void search(BayesNet bayesNet, Instances instances)
/*  23:    */     throws Exception
/*  24:    */   {
/*  25:113 */     this.m_random = new Random(getSeed());
/*  26:    */     
/*  27:    */ 
/*  28:116 */     double fCurrentScore = 0.0D;
/*  29:117 */     for (int iAttribute = 0; iAttribute < instances.numAttributes(); iAttribute++) {
/*  30:118 */       fCurrentScore += calcNodeScore(iAttribute);
/*  31:    */     }
/*  32:125 */     double fBestScore = fCurrentScore;
/*  33:126 */     BayesNet bestBayesNet = new BayesNet();
/*  34:127 */     bestBayesNet.m_Instances = instances;
/*  35:128 */     bestBayesNet.initStructure();
/*  36:129 */     copyParentSets(bestBayesNet, bayesNet);
/*  37:132 */     for (int iRun = 0; iRun < this.m_nRuns; iRun++)
/*  38:    */     {
/*  39:134 */       generateRandomNet(bayesNet, instances);
/*  40:    */       
/*  41:    */ 
/*  42:137 */       super.search(bayesNet, instances);
/*  43:    */       
/*  44:    */ 
/*  45:140 */       fCurrentScore = 0.0D;
/*  46:141 */       for (int iAttribute = 0; iAttribute < instances.numAttributes(); iAttribute++) {
/*  47:142 */         fCurrentScore += calcNodeScore(iAttribute);
/*  48:    */       }
/*  49:146 */       if (fCurrentScore > fBestScore)
/*  50:    */       {
/*  51:147 */         fBestScore = fCurrentScore;
/*  52:148 */         copyParentSets(bestBayesNet, bayesNet);
/*  53:    */       }
/*  54:    */     }
/*  55:153 */     copyParentSets(bayesNet, bestBayesNet);
/*  56:    */     
/*  57:    */ 
/*  58:156 */     bestBayesNet = null;
/*  59:157 */     this.m_Cache = null;
/*  60:    */   }
/*  61:    */   
/*  62:    */   void generateRandomNet(BayesNet bayesNet, Instances instances)
/*  63:    */   {
/*  64:161 */     int nNodes = instances.numAttributes();
/*  65:163 */     for (int iNode = 0; iNode < nNodes; iNode++)
/*  66:    */     {
/*  67:164 */       ParentSet parentSet = bayesNet.getParentSet(iNode);
/*  68:165 */       while (parentSet.getNrOfParents() > 0) {
/*  69:166 */         parentSet.deleteLastParent(instances);
/*  70:    */       }
/*  71:    */     }
/*  72:171 */     if (getInitAsNaiveBayes())
/*  73:    */     {
/*  74:172 */       int iClass = instances.classIndex();
/*  75:175 */       for (int iNode = 0; iNode < nNodes; iNode++) {
/*  76:176 */         if (iNode != iClass) {
/*  77:177 */           bayesNet.getParentSet(iNode).addParent(iClass, instances);
/*  78:    */         }
/*  79:    */       }
/*  80:    */     }
/*  81:183 */     int nNrOfAttempts = this.m_random.nextInt(nNodes * nNodes);
/*  82:184 */     for (int iAttempt = 0; iAttempt < nNrOfAttempts; iAttempt++)
/*  83:    */     {
/*  84:185 */       int iTail = this.m_random.nextInt(nNodes);
/*  85:186 */       int iHead = this.m_random.nextInt(nNodes);
/*  86:187 */       if ((bayesNet.getParentSet(iHead).getNrOfParents() < getMaxNrOfParents()) && (addArcMakesSense(bayesNet, instances, iHead, iTail))) {
/*  87:189 */         bayesNet.getParentSet(iHead).addParent(iTail, instances);
/*  88:    */       }
/*  89:    */     }
/*  90:    */   }
/*  91:    */   
/*  92:    */   void copyParentSets(BayesNet dest, BayesNet source)
/*  93:    */   {
/*  94:201 */     int nNodes = source.getNrOfNodes();
/*  95:203 */     for (int iNode = 0; iNode < nNodes; iNode++) {
/*  96:204 */       dest.getParentSet(iNode).copy(source.getParentSet(iNode));
/*  97:    */     }
/*  98:    */   }
/*  99:    */   
/* 100:    */   public int getRuns()
/* 101:    */   {
/* 102:212 */     return this.m_nRuns;
/* 103:    */   }
/* 104:    */   
/* 105:    */   public void setRuns(int nRuns)
/* 106:    */   {
/* 107:221 */     this.m_nRuns = nRuns;
/* 108:    */   }
/* 109:    */   
/* 110:    */   public int getSeed()
/* 111:    */   {
/* 112:228 */     return this.m_nSeed;
/* 113:    */   }
/* 114:    */   
/* 115:    */   public void setSeed(int nSeed)
/* 116:    */   {
/* 117:237 */     this.m_nSeed = nSeed;
/* 118:    */   }
/* 119:    */   
/* 120:    */   public Enumeration<Option> listOptions()
/* 121:    */   {
/* 122:247 */     Vector<Option> newVector = new Vector(2);
/* 123:    */     
/* 124:249 */     newVector.addElement(new Option("\tNumber of runs", "U", 1, "-U <integer>"));
/* 125:    */     
/* 126:251 */     newVector.addElement(new Option("\tRandom number seed", "A", 1, "-A <seed>"));
/* 127:    */     
/* 128:    */ 
/* 129:254 */     newVector.addAll(Collections.list(super.listOptions()));
/* 130:    */     
/* 131:256 */     return newVector.elements();
/* 132:    */   }
/* 133:    */   
/* 134:    */   public void setOptions(String[] options)
/* 135:    */     throws Exception
/* 136:    */   {
/* 137:312 */     String sRuns = Utils.getOption('U', options);
/* 138:313 */     if (sRuns.length() != 0) {
/* 139:314 */       setRuns(Integer.parseInt(sRuns));
/* 140:    */     }
/* 141:317 */     String sSeed = Utils.getOption('A', options);
/* 142:318 */     if (sSeed.length() != 0) {
/* 143:319 */       setSeed(Integer.parseInt(sSeed));
/* 144:    */     }
/* 145:322 */     super.setOptions(options);
/* 146:    */   }
/* 147:    */   
/* 148:    */   public String[] getOptions()
/* 149:    */   {
/* 150:333 */     Vector<String> options = new Vector();
/* 151:    */     
/* 152:335 */     options.add("-U");
/* 153:336 */     options.add("" + getRuns());
/* 154:    */     
/* 155:338 */     options.add("-A");
/* 156:339 */     options.add("" + getSeed());
/* 157:    */     
/* 158:341 */     Collections.addAll(options, super.getOptions());
/* 159:    */     
/* 160:343 */     return (String[])options.toArray(new String[0]);
/* 161:    */   }
/* 162:    */   
/* 163:    */   public String globalInfo()
/* 164:    */   {
/* 165:353 */     return "This Bayes Network learning algorithm repeatedly uses hill climbing starting with a randomly generated network structure and return the best structure of the various runs.";
/* 166:    */   }
/* 167:    */   
/* 168:    */   public String runsTipText()
/* 169:    */   {
/* 170:362 */     return "Sets the number of times hill climbing is performed.";
/* 171:    */   }
/* 172:    */   
/* 173:    */   public String seedTipText()
/* 174:    */   {
/* 175:369 */     return "Initialization value for random number generator. Setting the seed allows replicability of experiments.";
/* 176:    */   }
/* 177:    */   
/* 178:    */   public String getRevision()
/* 179:    */   {
/* 180:380 */     return RevisionUtils.extract("$Revision: 10154 $");
/* 181:    */   }
/* 182:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.bayes.net.search.local.RepeatedHillClimber
 * JD-Core Version:    0.7.0.1
 */