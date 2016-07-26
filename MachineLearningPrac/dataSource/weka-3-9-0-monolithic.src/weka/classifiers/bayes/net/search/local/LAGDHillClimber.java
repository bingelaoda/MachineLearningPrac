/*   1:    */ package weka.classifiers.bayes.net.search.local;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.classifiers.bayes.BayesNet;
/*   8:    */ import weka.core.Instances;
/*   9:    */ import weka.core.Option;
/*  10:    */ import weka.core.RevisionUtils;
/*  11:    */ import weka.core.Utils;
/*  12:    */ 
/*  13:    */ public class LAGDHillClimber
/*  14:    */   extends HillClimber
/*  15:    */ {
/*  16:    */   static final long serialVersionUID = 7217437499439184344L;
/*  17:100 */   int m_nNrOfLookAheadSteps = 2;
/*  18:103 */   int m_nNrOfGoodOperations = 5;
/*  19:    */   
/*  20:    */   protected void search(BayesNet bayesNet, Instances instances)
/*  21:    */     throws Exception
/*  22:    */   {
/*  23:115 */     int k = this.m_nNrOfLookAheadSteps;
/*  24:116 */     int l = this.m_nNrOfGoodOperations;
/*  25:117 */     lookAheadInGoodDirectionsSearch(bayesNet, instances, k, l);
/*  26:    */   }
/*  27:    */   
/*  28:    */   protected void lookAheadInGoodDirectionsSearch(BayesNet bayesNet, Instances instances, int nrOfLookAheadSteps, int nrOfGoodOperations)
/*  29:    */     throws Exception
/*  30:    */   {
/*  31:133 */     System.out.println("Initializing Cache");
/*  32:134 */     initCache(bayesNet, instances);
/*  33:136 */     while (nrOfLookAheadSteps > 1)
/*  34:    */     {
/*  35:137 */       System.out.println("Look Ahead Depth: " + nrOfLookAheadSteps);
/*  36:138 */       boolean legalSequence = true;
/*  37:139 */       double sequenceDeltaScore = 0.0D;
/*  38:140 */       HillClimber.Operation[] bestOperation = new HillClimber.Operation[nrOfLookAheadSteps];
/*  39:    */       
/*  40:142 */       bestOperation = getOptimalOperations(bayesNet, instances, nrOfLookAheadSteps, nrOfGoodOperations);
/*  41:144 */       for (int i = 0; i < nrOfLookAheadSteps; i++) {
/*  42:145 */         if (bestOperation[i] == null) {
/*  43:146 */           legalSequence = false;
/*  44:    */         } else {
/*  45:148 */           sequenceDeltaScore += bestOperation[i].m_fDeltaScore;
/*  46:    */         }
/*  47:    */       }
/*  48:151 */       while ((legalSequence) && (sequenceDeltaScore > 0.0D))
/*  49:    */       {
/*  50:152 */         System.out.println("Next Iteration..........................");
/*  51:153 */         for (int i = 0; i < nrOfLookAheadSteps; i++) {
/*  52:154 */           performOperation(bayesNet, instances, bestOperation[i]);
/*  53:    */         }
/*  54:156 */         bestOperation = getOptimalOperations(bayesNet, instances, nrOfLookAheadSteps, nrOfGoodOperations);
/*  55:    */         
/*  56:158 */         sequenceDeltaScore = 0.0D;
/*  57:159 */         for (int i = 0; i < nrOfLookAheadSteps; i++)
/*  58:    */         {
/*  59:160 */           if (bestOperation[i] != null)
/*  60:    */           {
/*  61:161 */             System.out.println(bestOperation[i].m_nOperation + " " + bestOperation[i].m_nHead + " " + bestOperation[i].m_nTail);
/*  62:    */             
/*  63:163 */             sequenceDeltaScore += bestOperation[i].m_fDeltaScore;
/*  64:    */           }
/*  65:    */           else
/*  66:    */           {
/*  67:165 */             legalSequence = false;
/*  68:    */           }
/*  69:168 */           System.out.println("DeltaScore: " + sequenceDeltaScore);
/*  70:    */         }
/*  71:    */       }
/*  72:171 */       nrOfLookAheadSteps--;
/*  73:    */     }
/*  74:175 */     HillClimber.Operation oOperation = getOptimalOperation(bayesNet, instances);
/*  75:176 */     while ((oOperation != null) && (oOperation.m_fDeltaScore > 0.0D))
/*  76:    */     {
/*  77:177 */       performOperation(bayesNet, instances, oOperation);
/*  78:178 */       System.out.println("Performing last greedy steps");
/*  79:179 */       oOperation = getOptimalOperation(bayesNet, instances);
/*  80:    */     }
/*  81:182 */     this.m_Cache = null;
/*  82:    */   }
/*  83:    */   
/*  84:    */   protected HillClimber.Operation getAntiOperation(HillClimber.Operation oOperation)
/*  85:    */     throws Exception
/*  86:    */   {
/*  87:194 */     if (oOperation.m_nOperation == 0) {
/*  88:195 */       return new HillClimber.Operation(this, oOperation.m_nTail, oOperation.m_nHead, 1);
/*  89:    */     }
/*  90:198 */     if (oOperation.m_nOperation == 1) {
/*  91:199 */       return new HillClimber.Operation(this, oOperation.m_nTail, oOperation.m_nHead, 0);
/*  92:    */     }
/*  93:202 */     return new HillClimber.Operation(this, oOperation.m_nHead, oOperation.m_nTail, 2);
/*  94:    */   }
/*  95:    */   
/*  96:    */   protected HillClimber.Operation[] getGoodOperations(BayesNet bayesNet, Instances instances, int nrOfGoodOperations)
/*  97:    */     throws Exception
/*  98:    */   {
/*  99:220 */     HillClimber.Operation[] goodOperations = new HillClimber.Operation[nrOfGoodOperations];
/* 100:221 */     for (int i = 0; i < nrOfGoodOperations; i++)
/* 101:    */     {
/* 102:222 */       goodOperations[i] = getOptimalOperation(bayesNet, instances);
/* 103:223 */       if (goodOperations[i] != null) {
/* 104:224 */         this.m_Cache.put(goodOperations[i], -1.0E+100D);
/* 105:    */       } else {
/* 106:226 */         i = nrOfGoodOperations;
/* 107:    */       }
/* 108:    */     }
/* 109:229 */     for (int i = 0; i < nrOfGoodOperations; i++) {
/* 110:230 */       if (goodOperations[i] != null)
/* 111:    */       {
/* 112:231 */         if (goodOperations[i].m_nOperation != 2) {
/* 113:232 */           this.m_Cache.put(goodOperations[i], goodOperations[i].m_fDeltaScore);
/* 114:    */         } else {
/* 115:234 */           this.m_Cache.put(goodOperations[i], goodOperations[i].m_fDeltaScore - this.m_Cache.m_fDeltaScoreAdd[goodOperations[i].m_nHead][goodOperations[i].m_nTail]);
/* 116:    */         }
/* 117:    */       }
/* 118:    */       else {
/* 119:241 */         i = nrOfGoodOperations;
/* 120:    */       }
/* 121:    */     }
/* 122:244 */     return goodOperations;
/* 123:    */   }
/* 124:    */   
/* 125:    */   protected HillClimber.Operation[] getOptimalOperations(BayesNet bayesNet, Instances instances, int nrOfLookAheadSteps, int nrOfGoodOperations)
/* 126:    */     throws Exception
/* 127:    */   {
/* 128:262 */     if (nrOfLookAheadSteps == 1)
/* 129:    */     {
/* 130:263 */       HillClimber.Operation[] bestOperation = new HillClimber.Operation[1];
/* 131:264 */       bestOperation[0] = getOptimalOperation(bayesNet, instances);
/* 132:265 */       return bestOperation;
/* 133:    */     }
/* 134:267 */     double bestDeltaScore = 0.0D;
/* 135:268 */     double currentDeltaScore = 0.0D;
/* 136:269 */     HillClimber.Operation[] bestOperation = new HillClimber.Operation[nrOfLookAheadSteps];
/* 137:270 */     HillClimber.Operation[] goodOperations = new HillClimber.Operation[nrOfGoodOperations];
/* 138:271 */     HillClimber.Operation[] tempOperation = new HillClimber.Operation[nrOfLookAheadSteps - 1];
/* 139:272 */     goodOperations = getGoodOperations(bayesNet, instances, nrOfGoodOperations);
/* 140:274 */     for (int i = 0; i < nrOfGoodOperations; i++) {
/* 141:275 */       if (goodOperations[i] != null)
/* 142:    */       {
/* 143:276 */         performOperation(bayesNet, instances, goodOperations[i]);
/* 144:277 */         tempOperation = getOptimalOperations(bayesNet, instances, nrOfLookAheadSteps - 1, nrOfGoodOperations);
/* 145:    */         
/* 146:279 */         currentDeltaScore = goodOperations[i].m_fDeltaScore;
/* 147:280 */         for (int j = 0; j < nrOfLookAheadSteps - 1; j++) {
/* 148:281 */           if (tempOperation[j] != null) {
/* 149:282 */             currentDeltaScore += tempOperation[j].m_fDeltaScore;
/* 150:    */           }
/* 151:    */         }
/* 152:285 */         performOperation(bayesNet, instances, getAntiOperation(goodOperations[i]));
/* 153:287 */         if (currentDeltaScore > bestDeltaScore)
/* 154:    */         {
/* 155:288 */           bestDeltaScore = currentDeltaScore;
/* 156:289 */           bestOperation[0] = goodOperations[i];
/* 157:290 */           for (int j = 1; j < nrOfLookAheadSteps; j++) {
/* 158:291 */             bestOperation[j] = tempOperation[(j - 1)];
/* 159:    */           }
/* 160:    */         }
/* 161:    */       }
/* 162:    */       else
/* 163:    */       {
/* 164:295 */         i = nrOfGoodOperations;
/* 165:    */       }
/* 166:    */     }
/* 167:298 */     return bestOperation;
/* 168:    */   }
/* 169:    */   
/* 170:    */   public void setMaxNrOfParents(int nMaxNrOfParents)
/* 171:    */   {
/* 172:309 */     this.m_nMaxNrOfParents = nMaxNrOfParents;
/* 173:    */   }
/* 174:    */   
/* 175:    */   public int getMaxNrOfParents()
/* 176:    */   {
/* 177:319 */     return this.m_nMaxNrOfParents;
/* 178:    */   }
/* 179:    */   
/* 180:    */   public void setNrOfLookAheadSteps(int nNrOfLookAheadSteps)
/* 181:    */   {
/* 182:328 */     this.m_nNrOfLookAheadSteps = nNrOfLookAheadSteps;
/* 183:    */   }
/* 184:    */   
/* 185:    */   public int getNrOfLookAheadSteps()
/* 186:    */   {
/* 187:337 */     return this.m_nNrOfLookAheadSteps;
/* 188:    */   }
/* 189:    */   
/* 190:    */   public void setNrOfGoodOperations(int nNrOfGoodOperations)
/* 191:    */   {
/* 192:346 */     this.m_nNrOfGoodOperations = nNrOfGoodOperations;
/* 193:    */   }
/* 194:    */   
/* 195:    */   public int getNrOfGoodOperations()
/* 196:    */   {
/* 197:355 */     return this.m_nNrOfGoodOperations;
/* 198:    */   }
/* 199:    */   
/* 200:    */   public Enumeration<Option> listOptions()
/* 201:    */   {
/* 202:365 */     Vector<Option> newVector = new Vector(2);
/* 203:    */     
/* 204:367 */     newVector.addElement(new Option("\tLook Ahead Depth", "L", 2, "-L <nr of look ahead steps>"));
/* 205:    */     
/* 206:369 */     newVector.addElement(new Option("\tNr of Good Operations", "G", 5, "-G <nr of good operations>"));
/* 207:    */     
/* 208:    */ 
/* 209:372 */     newVector.addAll(Collections.list(super.listOptions()));
/* 210:    */     
/* 211:374 */     return newVector.elements();
/* 212:    */   }
/* 213:    */   
/* 214:    */   public void setOptions(String[] options)
/* 215:    */     throws Exception
/* 216:    */   {
/* 217:430 */     String sNrOfLookAheadSteps = Utils.getOption('L', options);
/* 218:431 */     if (sNrOfLookAheadSteps.length() != 0) {
/* 219:432 */       setNrOfLookAheadSteps(Integer.parseInt(sNrOfLookAheadSteps));
/* 220:    */     } else {
/* 221:434 */       setNrOfLookAheadSteps(2);
/* 222:    */     }
/* 223:437 */     String sNrOfGoodOperations = Utils.getOption('G', options);
/* 224:438 */     if (sNrOfGoodOperations.length() != 0) {
/* 225:439 */       setNrOfGoodOperations(Integer.parseInt(sNrOfGoodOperations));
/* 226:    */     } else {
/* 227:441 */       setNrOfGoodOperations(5);
/* 228:    */     }
/* 229:444 */     super.setOptions(options);
/* 230:    */   }
/* 231:    */   
/* 232:    */   public String[] getOptions()
/* 233:    */   {
/* 234:455 */     Vector<String> options = new Vector();
/* 235:    */     
/* 236:457 */     options.add("-L");
/* 237:458 */     options.add("" + this.m_nNrOfLookAheadSteps);
/* 238:    */     
/* 239:460 */     options.add("-G");
/* 240:461 */     options.add("" + this.m_nNrOfGoodOperations);
/* 241:    */     
/* 242:463 */     Collections.addAll(options, super.getOptions());
/* 243:    */     
/* 244:465 */     return (String[])options.toArray(new String[0]);
/* 245:    */   }
/* 246:    */   
/* 247:    */   public String globalInfo()
/* 248:    */   {
/* 249:475 */     return "This Bayes Network learning algorithm uses a Look Ahead Hill Climbing algorithm called LAGD Hill Climbing. Unlike Greedy Hill Climbing it doesn't calculate a best greedy operation (adding, deleting or reversing an arc) but a sequence of nrOfLookAheadSteps operations, which leads to a network structure whose score is most likely higher in comparison to the network obtained by performing a sequence of nrOfLookAheadSteps greedy operations. The search is not restricted by an order on the variables (unlike K2). The difference with B and B2 is that this hill climber also considers arrows part of the naive Bayes structure for deletion.";
/* 250:    */   }
/* 251:    */   
/* 252:    */   public String nrOfLookAheadStepsTipText()
/* 253:    */   {
/* 254:488 */     return "Sets the Number of Look Ahead Steps. 'nrOfLookAheadSteps = 2' means that all network structures in a distance of 2 (from the current network structure) are taken into account for the decision which arcs to add, remove or reverse. 'nrOfLookAheadSteps = 1' results in Greedy Hill Climbing.";
/* 255:    */   }
/* 256:    */   
/* 257:    */   public String nrOfGoodOperationsTipText()
/* 258:    */   {
/* 259:497 */     return "Sets the Number of Good Operations per Look Ahead Step. 'nrOfGoodOperations = 5' means that for the next Look Ahead Step only the 5 best Operations (adding, deleting or reversing an arc) are taken into account for the calculation of the best sequence consisting of nrOfLookAheadSteps operations.";
/* 260:    */   }
/* 261:    */   
/* 262:    */   public String getRevision()
/* 263:    */   {
/* 264:509 */     return RevisionUtils.extract("$Revision: 10154 $");
/* 265:    */   }
/* 266:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.bayes.net.search.local.LAGDHillClimber
 * JD-Core Version:    0.7.0.1
 */