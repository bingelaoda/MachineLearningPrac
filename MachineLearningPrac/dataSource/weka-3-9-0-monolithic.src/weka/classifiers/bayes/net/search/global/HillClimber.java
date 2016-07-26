/*   1:    */ package weka.classifiers.bayes.net.search.global;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.io.Serializable;
/*   5:    */ import java.util.Collections;
/*   6:    */ import java.util.Enumeration;
/*   7:    */ import java.util.Vector;
/*   8:    */ import weka.classifiers.bayes.BayesNet;
/*   9:    */ import weka.classifiers.bayes.net.ParentSet;
/*  10:    */ import weka.core.Instances;
/*  11:    */ import weka.core.Option;
/*  12:    */ import weka.core.RevisionHandler;
/*  13:    */ import weka.core.RevisionUtils;
/*  14:    */ import weka.core.Utils;
/*  15:    */ 
/*  16:    */ public class HillClimber
/*  17:    */   extends GlobalScoreSearchAlgorithm
/*  18:    */ {
/*  19:    */   static final long serialVersionUID = -3885042888195820149L;
/*  20:    */   
/*  21:    */   class Operation
/*  22:    */     implements Serializable, RevisionHandler
/*  23:    */   {
/*  24:    */     static final long serialVersionUID = -2934970456587374967L;
/*  25:    */     static final int OPERATION_ADD = 0;
/*  26:    */     static final int OPERATION_DEL = 1;
/*  27:    */     static final int OPERATION_REVERSE = 2;
/*  28:    */     public int m_nTail;
/*  29:    */     public int m_nHead;
/*  30:    */     public int m_nOperation;
/*  31:    */     
/*  32:    */     public Operation() {}
/*  33:    */     
/*  34:    */     public Operation(int nTail, int nHead, int nOperation)
/*  35:    */     {
/*  36:120 */       this.m_nHead = nHead;
/*  37:121 */       this.m_nTail = nTail;
/*  38:122 */       this.m_nOperation = nOperation;
/*  39:    */     }
/*  40:    */     
/*  41:    */     public boolean equals(Operation other)
/*  42:    */     {
/*  43:132 */       if (other == null) {
/*  44:133 */         return false;
/*  45:    */       }
/*  46:135 */       return (this.m_nOperation == other.m_nOperation) && (this.m_nHead == other.m_nHead) && (this.m_nTail == other.m_nTail);
/*  47:    */     }
/*  48:    */     
/*  49:146 */     public double m_fScore = -1.0E+100D;
/*  50:    */     
/*  51:    */     public String getRevision()
/*  52:    */     {
/*  53:155 */       return RevisionUtils.extract("$Revision: 10154 $");
/*  54:    */     }
/*  55:    */   }
/*  56:    */   
/*  57:160 */   boolean m_bUseArcReversal = false;
/*  58:    */   
/*  59:    */   protected void search(BayesNet bayesNet, Instances instances)
/*  60:    */     throws Exception
/*  61:    */   {
/*  62:173 */     this.m_BayesNet = bayesNet;
/*  63:174 */     double fScore = calcScore(bayesNet);
/*  64:    */     
/*  65:176 */     Operation oOperation = getOptimalOperation(bayesNet, instances);
/*  66:177 */     while ((oOperation != null) && (oOperation.m_fScore > fScore))
/*  67:    */     {
/*  68:178 */       performOperation(bayesNet, instances, oOperation);
/*  69:179 */       fScore = oOperation.m_fScore;
/*  70:180 */       oOperation = getOptimalOperation(bayesNet, instances);
/*  71:    */     }
/*  72:    */   }
/*  73:    */   
/*  74:    */   boolean isNotTabu(Operation oOperation)
/*  75:    */   {
/*  76:192 */     return true;
/*  77:    */   }
/*  78:    */   
/*  79:    */   Operation getOptimalOperation(BayesNet bayesNet, Instances instances)
/*  80:    */     throws Exception
/*  81:    */   {
/*  82:206 */     Operation oBestOperation = new Operation();
/*  83:    */     
/*  84:    */ 
/*  85:209 */     oBestOperation = findBestArcToAdd(bayesNet, instances, oBestOperation);
/*  86:    */     
/*  87:211 */     oBestOperation = findBestArcToDelete(bayesNet, instances, oBestOperation);
/*  88:213 */     if (getUseArcReversal()) {
/*  89:214 */       oBestOperation = findBestArcToReverse(bayesNet, instances, oBestOperation);
/*  90:    */     }
/*  91:218 */     if (oBestOperation.m_fScore == -1.0E+100D) {
/*  92:219 */       return null;
/*  93:    */     }
/*  94:222 */     return oBestOperation;
/*  95:    */   }
/*  96:    */   
/*  97:    */   void performOperation(BayesNet bayesNet, Instances instances, Operation oOperation)
/*  98:    */     throws Exception
/*  99:    */   {
/* 100:237 */     switch (oOperation.m_nOperation)
/* 101:    */     {
/* 102:    */     case 0: 
/* 103:239 */       applyArcAddition(bayesNet, oOperation.m_nHead, oOperation.m_nTail, instances);
/* 104:241 */       if (bayesNet.getDebug()) {
/* 105:242 */         System.out.print("Add " + oOperation.m_nHead + " -> " + oOperation.m_nTail);
/* 106:    */       }
/* 107:    */       break;
/* 108:    */     case 1: 
/* 109:247 */       applyArcDeletion(bayesNet, oOperation.m_nHead, oOperation.m_nTail, instances);
/* 110:249 */       if (bayesNet.getDebug()) {
/* 111:250 */         System.out.print("Del " + oOperation.m_nHead + " -> " + oOperation.m_nTail);
/* 112:    */       }
/* 113:    */       break;
/* 114:    */     case 2: 
/* 115:255 */       applyArcDeletion(bayesNet, oOperation.m_nHead, oOperation.m_nTail, instances);
/* 116:    */       
/* 117:257 */       applyArcAddition(bayesNet, oOperation.m_nTail, oOperation.m_nHead, instances);
/* 118:259 */       if (bayesNet.getDebug()) {
/* 119:260 */         System.out.print("Rev " + oOperation.m_nHead + " -> " + oOperation.m_nTail);
/* 120:    */       }
/* 121:    */       break;
/* 122:    */     }
/* 123:    */   }
/* 124:    */   
/* 125:    */   void applyArcAddition(BayesNet bayesNet, int iHead, int iTail, Instances instances)
/* 126:    */   {
/* 127:276 */     ParentSet bestParentSet = bayesNet.getParentSet(iHead);
/* 128:277 */     bestParentSet.addParent(iTail, instances);
/* 129:    */   }
/* 130:    */   
/* 131:    */   void applyArcDeletion(BayesNet bayesNet, int iHead, int iTail, Instances instances)
/* 132:    */   {
/* 133:289 */     ParentSet bestParentSet = bayesNet.getParentSet(iHead);
/* 134:290 */     bestParentSet.deleteParent(iTail, instances);
/* 135:    */   }
/* 136:    */   
/* 137:    */   Operation findBestArcToAdd(BayesNet bayesNet, Instances instances, Operation oBestOperation)
/* 138:    */     throws Exception
/* 139:    */   {
/* 140:306 */     int nNrOfAtts = instances.numAttributes();
/* 141:308 */     for (int iAttributeHead = 0; iAttributeHead < nNrOfAtts; iAttributeHead++) {
/* 142:309 */       if (bayesNet.getParentSet(iAttributeHead).getNrOfParents() < this.m_nMaxNrOfParents) {
/* 143:310 */         for (int iAttributeTail = 0; iAttributeTail < nNrOfAtts; iAttributeTail++) {
/* 144:311 */           if (addArcMakesSense(bayesNet, instances, iAttributeHead, iAttributeTail))
/* 145:    */           {
/* 146:313 */             Operation oOperation = new Operation(iAttributeTail, iAttributeHead, 0);
/* 147:    */             
/* 148:315 */             double fScore = calcScoreWithExtraParent(oOperation.m_nHead, oOperation.m_nTail);
/* 149:317 */             if ((fScore > oBestOperation.m_fScore) && 
/* 150:318 */               (isNotTabu(oOperation)))
/* 151:    */             {
/* 152:319 */               oBestOperation = oOperation;
/* 153:320 */               oBestOperation.m_fScore = fScore;
/* 154:    */             }
/* 155:    */           }
/* 156:    */         }
/* 157:    */       }
/* 158:    */     }
/* 159:327 */     return oBestOperation;
/* 160:    */   }
/* 161:    */   
/* 162:    */   Operation findBestArcToDelete(BayesNet bayesNet, Instances instances, Operation oBestOperation)
/* 163:    */     throws Exception
/* 164:    */   {
/* 165:342 */     int nNrOfAtts = instances.numAttributes();
/* 166:344 */     for (int iNode = 0; iNode < nNrOfAtts; iNode++)
/* 167:    */     {
/* 168:345 */       ParentSet parentSet = bayesNet.getParentSet(iNode);
/* 169:346 */       for (int iParent = 0; iParent < parentSet.getNrOfParents(); iParent++)
/* 170:    */       {
/* 171:347 */         Operation oOperation = new Operation(parentSet.getParent(iParent), iNode, 1);
/* 172:    */         
/* 173:349 */         double fScore = calcScoreWithMissingParent(oOperation.m_nHead, oOperation.m_nTail);
/* 174:351 */         if ((fScore > oBestOperation.m_fScore) && 
/* 175:352 */           (isNotTabu(oOperation)))
/* 176:    */         {
/* 177:353 */           oBestOperation = oOperation;
/* 178:354 */           oBestOperation.m_fScore = fScore;
/* 179:    */         }
/* 180:    */       }
/* 181:    */     }
/* 182:359 */     return oBestOperation;
/* 183:    */   }
/* 184:    */   
/* 185:    */   Operation findBestArcToReverse(BayesNet bayesNet, Instances instances, Operation oBestOperation)
/* 186:    */     throws Exception
/* 187:    */   {
/* 188:375 */     int nNrOfAtts = instances.numAttributes();
/* 189:377 */     for (int iNode = 0; iNode < nNrOfAtts; iNode++)
/* 190:    */     {
/* 191:378 */       ParentSet parentSet = bayesNet.getParentSet(iNode);
/* 192:379 */       for (int iParent = 0; iParent < parentSet.getNrOfParents(); iParent++)
/* 193:    */       {
/* 194:380 */         int iTail = parentSet.getParent(iParent);
/* 195:382 */         if ((reverseArcMakesSense(bayesNet, instances, iNode, iTail)) && (bayesNet.getParentSet(iTail).getNrOfParents() < this.m_nMaxNrOfParents))
/* 196:    */         {
/* 197:385 */           Operation oOperation = new Operation(parentSet.getParent(iParent), iNode, 2);
/* 198:    */           
/* 199:387 */           double fScore = calcScoreWithReversedParent(oOperation.m_nHead, oOperation.m_nTail);
/* 200:389 */           if ((fScore > oBestOperation.m_fScore) && 
/* 201:390 */             (isNotTabu(oOperation)))
/* 202:    */           {
/* 203:391 */             oBestOperation = oOperation;
/* 204:392 */             oBestOperation.m_fScore = fScore;
/* 205:    */           }
/* 206:    */         }
/* 207:    */       }
/* 208:    */     }
/* 209:398 */     return oBestOperation;
/* 210:    */   }
/* 211:    */   
/* 212:    */   public void setMaxNrOfParents(int nMaxNrOfParents)
/* 213:    */   {
/* 214:407 */     this.m_nMaxNrOfParents = nMaxNrOfParents;
/* 215:    */   }
/* 216:    */   
/* 217:    */   public int getMaxNrOfParents()
/* 218:    */   {
/* 219:416 */     return this.m_nMaxNrOfParents;
/* 220:    */   }
/* 221:    */   
/* 222:    */   public Enumeration<Option> listOptions()
/* 223:    */   {
/* 224:426 */     Vector<Option> newVector = new Vector(2);
/* 225:    */     
/* 226:428 */     newVector.addElement(new Option("\tMaximum number of parents", "P", 1, "-P <nr of parents>"));
/* 227:    */     
/* 228:430 */     newVector.addElement(new Option("\tUse arc reversal operation.\n\t(default false)", "R", 0, "-R"));
/* 229:    */     
/* 230:432 */     newVector.addElement(new Option("\tInitial structure is empty (instead of Naive Bayes)", "N", 0, "-N"));
/* 231:    */     
/* 232:    */ 
/* 233:435 */     newVector.addAll(Collections.list(super.listOptions()));
/* 234:    */     
/* 235:437 */     return newVector.elements();
/* 236:    */   }
/* 237:    */   
/* 238:    */   public void setOptions(String[] options)
/* 239:    */     throws Exception
/* 240:    */   {
/* 241:489 */     setUseArcReversal(Utils.getFlag('R', options));
/* 242:    */     
/* 243:491 */     setInitAsNaiveBayes(!Utils.getFlag('N', options));
/* 244:    */     
/* 245:493 */     String sMaxNrOfParents = Utils.getOption('P', options);
/* 246:494 */     if (sMaxNrOfParents.length() != 0) {
/* 247:495 */       setMaxNrOfParents(Integer.parseInt(sMaxNrOfParents));
/* 248:    */     } else {
/* 249:497 */       setMaxNrOfParents(100000);
/* 250:    */     }
/* 251:500 */     super.setOptions(options);
/* 252:    */   }
/* 253:    */   
/* 254:    */   public String[] getOptions()
/* 255:    */   {
/* 256:511 */     Vector<String> options = new Vector();
/* 257:513 */     if (getUseArcReversal()) {
/* 258:514 */       options.add("-R");
/* 259:    */     }
/* 260:517 */     if (!getInitAsNaiveBayes()) {
/* 261:518 */       options.add("-N");
/* 262:    */     }
/* 263:521 */     options.add("-P");
/* 264:522 */     options.add("" + this.m_nMaxNrOfParents);
/* 265:    */     
/* 266:524 */     Collections.addAll(options, super.getOptions());
/* 267:    */     
/* 268:526 */     return (String[])options.toArray(new String[0]);
/* 269:    */   }
/* 270:    */   
/* 271:    */   public void setInitAsNaiveBayes(boolean bInitAsNaiveBayes)
/* 272:    */   {
/* 273:535 */     this.m_bInitAsNaiveBayes = bInitAsNaiveBayes;
/* 274:    */   }
/* 275:    */   
/* 276:    */   public boolean getInitAsNaiveBayes()
/* 277:    */   {
/* 278:544 */     return this.m_bInitAsNaiveBayes;
/* 279:    */   }
/* 280:    */   
/* 281:    */   public boolean getUseArcReversal()
/* 282:    */   {
/* 283:553 */     return this.m_bUseArcReversal;
/* 284:    */   }
/* 285:    */   
/* 286:    */   public void setUseArcReversal(boolean bUseArcReversal)
/* 287:    */   {
/* 288:562 */     this.m_bUseArcReversal = bUseArcReversal;
/* 289:    */   }
/* 290:    */   
/* 291:    */   public String globalInfo()
/* 292:    */   {
/* 293:572 */     return "This Bayes Network learning algorithm uses a hill climbing algorithm adding, deleting and reversing arcs. The search is not restricted by an order on the variables (unlike K2). The difference with B and B2 is that this hill climber also considers arrows part of the naive Bayes structure for deletion.";
/* 294:    */   }
/* 295:    */   
/* 296:    */   public String useArcReversalTipText()
/* 297:    */   {
/* 298:582 */     return "When set to true, the arc reversal operation is used in the search.";
/* 299:    */   }
/* 300:    */   
/* 301:    */   public String getRevision()
/* 302:    */   {
/* 303:592 */     return RevisionUtils.extract("$Revision: 10154 $");
/* 304:    */   }
/* 305:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.bayes.net.search.global.HillClimber
 * JD-Core Version:    0.7.0.1
 */