/*   1:    */ package weka.classifiers.bayes.net.search;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.io.Serializable;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.classifiers.bayes.BayesNet;
/*   8:    */ import weka.classifiers.bayes.net.BIFReader;
/*   9:    */ import weka.classifiers.bayes.net.ParentSet;
/*  10:    */ import weka.core.Instances;
/*  11:    */ import weka.core.Option;
/*  12:    */ import weka.core.OptionHandler;
/*  13:    */ import weka.core.RevisionHandler;
/*  14:    */ import weka.core.RevisionUtils;
/*  15:    */ 
/*  16:    */ public class SearchAlgorithm
/*  17:    */   implements OptionHandler, Serializable, RevisionHandler
/*  18:    */ {
/*  19:    */   static final long serialVersionUID = 6164792240778525312L;
/*  20: 55 */   protected int m_nMaxNrOfParents = 1;
/*  21: 61 */   protected boolean m_bInitAsNaiveBayes = true;
/*  22: 68 */   protected boolean m_bMarkovBlanketClassifier = false;
/*  23:    */   protected String m_sInitalBIFFile;
/*  24:    */   
/*  25:    */   protected boolean addArcMakesSense(BayesNet bayesNet, Instances instances, int iAttributeHead, int iAttributeTail)
/*  26:    */   {
/*  27: 93 */     if (iAttributeHead == iAttributeTail) {
/*  28: 94 */       return false;
/*  29:    */     }
/*  30: 98 */     if (isArc(bayesNet, iAttributeHead, iAttributeTail)) {
/*  31: 99 */       return false;
/*  32:    */     }
/*  33:103 */     int nNodes = instances.numAttributes();
/*  34:104 */     boolean[] bDone = new boolean[nNodes];
/*  35:106 */     for (int iNode = 0; iNode < nNodes; iNode++) {
/*  36:107 */       bDone[iNode] = false;
/*  37:    */     }
/*  38:111 */     bayesNet.getParentSet(iAttributeHead).addParent(iAttributeTail, instances);
/*  39:113 */     for (int iNode = 0; iNode < nNodes; iNode++)
/*  40:    */     {
/*  41:116 */       boolean bFound = false;
/*  42:118 */       for (int iNode2 = 0; (!bFound) && (iNode2 < nNodes); iNode2++) {
/*  43:119 */         if (bDone[iNode2] == 0)
/*  44:    */         {
/*  45:120 */           boolean bHasNoParents = true;
/*  46:122 */           for (int iParent = 0; iParent < bayesNet.getParentSet(iNode2).getNrOfParents(); iParent++) {
/*  47:124 */             if (bDone[bayesNet.getParentSet(iNode2).getParent(iParent)] == 0) {
/*  48:125 */               bHasNoParents = false;
/*  49:    */             }
/*  50:    */           }
/*  51:129 */           if (bHasNoParents)
/*  52:    */           {
/*  53:130 */             bDone[iNode2] = true;
/*  54:131 */             bFound = true;
/*  55:    */           }
/*  56:    */         }
/*  57:    */       }
/*  58:136 */       if (!bFound)
/*  59:    */       {
/*  60:137 */         bayesNet.getParentSet(iAttributeHead).deleteLastParent(instances);
/*  61:    */         
/*  62:139 */         return false;
/*  63:    */       }
/*  64:    */     }
/*  65:143 */     bayesNet.getParentSet(iAttributeHead).deleteLastParent(instances);
/*  66:    */     
/*  67:145 */     return true;
/*  68:    */   }
/*  69:    */   
/*  70:    */   protected boolean reverseArcMakesSense(BayesNet bayesNet, Instances instances, int iAttributeHead, int iAttributeTail)
/*  71:    */   {
/*  72:162 */     if (iAttributeHead == iAttributeTail) {
/*  73:163 */       return false;
/*  74:    */     }
/*  75:167 */     if (!isArc(bayesNet, iAttributeHead, iAttributeTail)) {
/*  76:168 */       return false;
/*  77:    */     }
/*  78:172 */     int nNodes = instances.numAttributes();
/*  79:173 */     boolean[] bDone = new boolean[nNodes];
/*  80:175 */     for (int iNode = 0; iNode < nNodes; iNode++) {
/*  81:176 */       bDone[iNode] = false;
/*  82:    */     }
/*  83:180 */     bayesNet.getParentSet(iAttributeTail).addParent(iAttributeHead, instances);
/*  84:182 */     for (int iNode = 0; iNode < nNodes; iNode++)
/*  85:    */     {
/*  86:185 */       boolean bFound = false;
/*  87:187 */       for (int iNode2 = 0; (!bFound) && (iNode2 < nNodes); iNode2++) {
/*  88:188 */         if (bDone[iNode2] == 0)
/*  89:    */         {
/*  90:189 */           ParentSet parentSet = bayesNet.getParentSet(iNode2);
/*  91:190 */           boolean bHasNoParents = true;
/*  92:191 */           for (int iParent = 0; iParent < parentSet.getNrOfParents(); iParent++) {
/*  93:192 */             if (bDone[parentSet.getParent(iParent)] == 0) {
/*  94:196 */               if ((iNode2 != iAttributeHead) || (parentSet.getParent(iParent) != iAttributeTail)) {
/*  95:197 */                 bHasNoParents = false;
/*  96:    */               }
/*  97:    */             }
/*  98:    */           }
/*  99:202 */           if (bHasNoParents)
/* 100:    */           {
/* 101:203 */             bDone[iNode2] = true;
/* 102:204 */             bFound = true;
/* 103:    */           }
/* 104:    */         }
/* 105:    */       }
/* 106:209 */       if (!bFound)
/* 107:    */       {
/* 108:210 */         bayesNet.getParentSet(iAttributeTail).deleteLastParent(instances);
/* 109:211 */         return false;
/* 110:    */       }
/* 111:    */     }
/* 112:215 */     bayesNet.getParentSet(iAttributeTail).deleteLastParent(instances);
/* 113:216 */     return true;
/* 114:    */   }
/* 115:    */   
/* 116:    */   protected boolean isArc(BayesNet bayesNet, int iAttributeHead, int iAttributeTail)
/* 117:    */   {
/* 118:231 */     for (int iParent = 0; iParent < bayesNet.getParentSet(iAttributeHead).getNrOfParents(); iParent++) {
/* 119:233 */       if (bayesNet.getParentSet(iAttributeHead).getParent(iParent) == iAttributeTail) {
/* 120:234 */         return true;
/* 121:    */       }
/* 122:    */     }
/* 123:238 */     return false;
/* 124:    */   }
/* 125:    */   
/* 126:    */   public Enumeration<Option> listOptions()
/* 127:    */   {
/* 128:248 */     return new Vector(0).elements();
/* 129:    */   }
/* 130:    */   
/* 131:    */   public void setOptions(String[] options)
/* 132:    */     throws Exception
/* 133:    */   {}
/* 134:    */   
/* 135:    */   public String[] getOptions()
/* 136:    */   {
/* 137:269 */     return new String[0];
/* 138:    */   }
/* 139:    */   
/* 140:    */   public String toString()
/* 141:    */   {
/* 142:279 */     return "SearchAlgorithm\n";
/* 143:    */   }
/* 144:    */   
/* 145:    */   public void buildStructure(BayesNet bayesNet, Instances instances)
/* 146:    */     throws Exception
/* 147:    */   {
/* 148:295 */     if ((this.m_sInitalBIFFile != null) && (!this.m_sInitalBIFFile.equals("")))
/* 149:    */     {
/* 150:296 */       BIFReader initialNet = new BIFReader().processFile(this.m_sInitalBIFFile);
/* 151:297 */       for (int iAttribute = 0; iAttribute < instances.numAttributes(); iAttribute++)
/* 152:    */       {
/* 153:298 */         int iNode = initialNet.getNode(bayesNet.getNodeName(iAttribute));
/* 154:299 */         for (int iParent = 0; iParent < initialNet.getNrOfParents(iAttribute); iParent++)
/* 155:    */         {
/* 156:300 */           String sParent = initialNet.getNodeName(initialNet.getParent(iNode, iParent));
/* 157:    */           
/* 158:302 */           int nParent = 0;
/* 159:304 */           while ((nParent < bayesNet.getNrOfNodes()) && (!bayesNet.getNodeName(nParent).equals(sParent))) {
/* 160:305 */             nParent++;
/* 161:    */           }
/* 162:307 */           if (nParent < bayesNet.getNrOfNodes()) {
/* 163:308 */             bayesNet.getParentSet(iAttribute).addParent(nParent, instances);
/* 164:    */           } else {
/* 165:310 */             System.err.println("Warning: Node " + sParent + " is ignored. It is found in initial network but not in data set.");
/* 166:    */           }
/* 167:    */         }
/* 168:    */       }
/* 169:    */     }
/* 170:317 */     else if (this.m_bInitAsNaiveBayes)
/* 171:    */     {
/* 172:318 */       int iClass = instances.classIndex();
/* 173:321 */       for (int iAttribute = 0; iAttribute < instances.numAttributes(); iAttribute++) {
/* 174:322 */         if (iAttribute != iClass) {
/* 175:323 */           bayesNet.getParentSet(iAttribute).addParent(iClass, instances);
/* 176:    */         }
/* 177:    */       }
/* 178:    */     }
/* 179:327 */     search(bayesNet, instances);
/* 180:328 */     if (this.m_bMarkovBlanketClassifier) {
/* 181:329 */       doMarkovBlanketCorrection(bayesNet, instances);
/* 182:    */     }
/* 183:    */   }
/* 184:    */   
/* 185:    */   protected void search(BayesNet bayesNet, Instances instances)
/* 186:    */     throws Exception
/* 187:    */   {}
/* 188:    */   
/* 189:    */   protected void doMarkovBlanketCorrection(BayesNet bayesNet, Instances instances)
/* 190:    */   {
/* 191:355 */     int iClass = instances.classIndex();
/* 192:356 */     ParentSet ancestors = new ParentSet();
/* 193:357 */     int nOldSize = 0;
/* 194:358 */     ancestors.addParent(iClass, instances);
/* 195:359 */     while (nOldSize != ancestors.getNrOfParents())
/* 196:    */     {
/* 197:360 */       nOldSize = ancestors.getNrOfParents();
/* 198:361 */       for (int iNode = 0; iNode < nOldSize; iNode++)
/* 199:    */       {
/* 200:362 */         int iCurrent = ancestors.getParent(iNode);
/* 201:363 */         ParentSet p = bayesNet.getParentSet(iCurrent);
/* 202:364 */         for (int iParent = 0; iParent < p.getNrOfParents(); iParent++) {
/* 203:365 */           if (!ancestors.contains(p.getParent(iParent))) {
/* 204:366 */             ancestors.addParent(p.getParent(iParent), instances);
/* 205:    */           }
/* 206:    */         }
/* 207:    */       }
/* 208:    */     }
/* 209:371 */     for (int iAttribute = 0; iAttribute < instances.numAttributes(); iAttribute++)
/* 210:    */     {
/* 211:372 */       boolean bIsInMarkovBoundary = (iAttribute == iClass) || (bayesNet.getParentSet(iAttribute).contains(iClass)) || (bayesNet.getParentSet(iClass).contains(iAttribute));
/* 212:375 */       for (int iAttribute2 = 0; (!bIsInMarkovBoundary) && (iAttribute2 < instances.numAttributes()); iAttribute2++) {
/* 213:377 */         bIsInMarkovBoundary = (bayesNet.getParentSet(iAttribute2).contains(iAttribute)) && (bayesNet.getParentSet(iAttribute2).contains(iClass));
/* 214:    */       }
/* 215:381 */       if (!bIsInMarkovBoundary) {
/* 216:382 */         if (ancestors.contains(iAttribute))
/* 217:    */         {
/* 218:383 */           if (bayesNet.getParentSet(iClass).getCardinalityOfParents() < 1024) {
/* 219:384 */             bayesNet.getParentSet(iClass).addParent(iAttribute, instances);
/* 220:    */           }
/* 221:    */         }
/* 222:    */         else {
/* 223:389 */           bayesNet.getParentSet(iAttribute).addParent(iClass, instances);
/* 224:    */         }
/* 225:    */       }
/* 226:    */     }
/* 227:    */   }
/* 228:    */   
/* 229:    */   protected void setMarkovBlanketClassifier(boolean bMarkovBlanketClassifier)
/* 230:    */   {
/* 231:400 */     this.m_bMarkovBlanketClassifier = bMarkovBlanketClassifier;
/* 232:    */   }
/* 233:    */   
/* 234:    */   protected boolean getMarkovBlanketClassifier()
/* 235:    */   {
/* 236:408 */     return this.m_bMarkovBlanketClassifier;
/* 237:    */   }
/* 238:    */   
/* 239:    */   public String maxNrOfParentsTipText()
/* 240:    */   {
/* 241:415 */     return "Set the maximum number of parents a node in the Bayes net can have. When initialized as Naive Bayes, setting this parameter to 1 results in a Naive Bayes classifier. When set to 2, a Tree Augmented Bayes Network (TAN) is learned, and when set >2, a Bayes Net Augmented Bayes Network (BAN) is learned. By setting it to a value much larger than the number of nodes in the network (the default of 100000 pretty much guarantees this), no restriction on the number of parents is enforced";
/* 242:    */   }
/* 243:    */   
/* 244:    */   public String initAsNaiveBayesTipText()
/* 245:    */   {
/* 246:428 */     return "When set to true (default), the initial network used for structure learning is a Naive Bayes Network, that is, a network with an arrow from the classifier node to each other node. When set to false, an empty network is used as initial network structure";
/* 247:    */   }
/* 248:    */   
/* 249:    */   protected String markovBlanketClassifierTipText()
/* 250:    */   {
/* 251:438 */     return "When set to true (default is false), after a network structure is learned a Markov Blanket correction is applied to the network structure. This ensures that all nodes in the network are part of the Markov blanket of the classifier node.";
/* 252:    */   }
/* 253:    */   
/* 254:    */   public String getRevision()
/* 255:    */   {
/* 256:451 */     return RevisionUtils.extract("$Revision: 10153 $");
/* 257:    */   }
/* 258:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.bayes.net.search.SearchAlgorithm
 * JD-Core Version:    0.7.0.1
 */