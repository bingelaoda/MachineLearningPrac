/*   1:    */ package weka.attributeSelection;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.math.BigInteger;
/*   5:    */ import java.util.BitSet;
/*   6:    */ import java.util.Enumeration;
/*   7:    */ import java.util.Vector;
/*   8:    */ import weka.core.Instances;
/*   9:    */ import weka.core.Option;
/*  10:    */ import weka.core.OptionHandler;
/*  11:    */ import weka.core.RevisionUtils;
/*  12:    */ import weka.core.Utils;
/*  13:    */ 
/*  14:    */ public class ExhaustiveSearch
/*  15:    */   extends ASSearch
/*  16:    */   implements OptionHandler
/*  17:    */ {
/*  18:    */   static final long serialVersionUID = 5741842861142379712L;
/*  19:    */   private BitSet m_bestGroup;
/*  20:    */   private double m_bestMerit;
/*  21:    */   private boolean m_hasClass;
/*  22:    */   private int m_classIndex;
/*  23:    */   private int m_numAttribs;
/*  24:    */   private boolean m_verbose;
/*  25:    */   private int m_evaluations;
/*  26:    */   
/*  27:    */   public String globalInfo()
/*  28:    */   {
/*  29: 90 */     return "ExhaustiveSearch : \n\nPerforms an exhaustive search through the space of attribute subsets starting from the empty set of attrubutes. Reports the best subset found.";
/*  30:    */   }
/*  31:    */   
/*  32:    */   public ExhaustiveSearch()
/*  33:    */   {
/*  34: 99 */     resetOptions();
/*  35:    */   }
/*  36:    */   
/*  37:    */   public Enumeration<Option> listOptions()
/*  38:    */   {
/*  39:109 */     Vector<Option> newVector = new Vector(2);
/*  40:    */     
/*  41:111 */     newVector.addElement(new Option("\tOutput subsets as the search progresses.\n\t(default = false).", "V", 0, "-V"));
/*  42:    */     
/*  43:    */ 
/*  44:114 */     return newVector.elements();
/*  45:    */   }
/*  46:    */   
/*  47:    */   public void setOptions(String[] options)
/*  48:    */     throws Exception
/*  49:    */   {
/*  50:139 */     resetOptions();
/*  51:    */     
/*  52:141 */     setVerbose(Utils.getFlag('V', options));
/*  53:    */     
/*  54:143 */     Utils.checkForRemainingOptions(options);
/*  55:    */   }
/*  56:    */   
/*  57:    */   public String verboseTipText()
/*  58:    */   {
/*  59:153 */     return "Print progress information. Sends progress info to the terminal as the search progresses.";
/*  60:    */   }
/*  61:    */   
/*  62:    */   public void setVerbose(boolean v)
/*  63:    */   {
/*  64:163 */     this.m_verbose = v;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public boolean getVerbose()
/*  68:    */   {
/*  69:172 */     return this.m_verbose;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public String[] getOptions()
/*  73:    */   {
/*  74:183 */     Vector<String> options = new Vector();
/*  75:185 */     if (this.m_verbose) {
/*  76:186 */       options.add("-V");
/*  77:    */     }
/*  78:189 */     return (String[])options.toArray(new String[0]);
/*  79:    */   }
/*  80:    */   
/*  81:    */   public String toString()
/*  82:    */   {
/*  83:199 */     StringBuffer text = new StringBuffer();
/*  84:    */     
/*  85:201 */     text.append("\tExhaustive Search.\n\tStart set: ");
/*  86:    */     
/*  87:203 */     text.append("no attributes\n");
/*  88:    */     
/*  89:205 */     text.append("\tNumber of evaluations: " + this.m_evaluations + "\n");
/*  90:206 */     text.append("\tMerit of best subset found: " + Utils.doubleToString(Math.abs(this.m_bestMerit), 8, 3) + "\n");
/*  91:    */     
/*  92:    */ 
/*  93:209 */     return text.toString();
/*  94:    */   }
/*  95:    */   
/*  96:    */   public int[] search(ASEvaluation ASEval, Instances data)
/*  97:    */     throws Exception
/*  98:    */   {
/*  99:224 */     boolean done = false;
/* 100:    */     
/* 101:    */ 
/* 102:    */ 
/* 103:228 */     BigInteger space = BigInteger.ZERO;
/* 104:    */     
/* 105:230 */     this.m_evaluations = 0;
/* 106:231 */     this.m_numAttribs = data.numAttributes();
/* 107:232 */     this.m_bestGroup = new BitSet(this.m_numAttribs);
/* 108:234 */     if (!(ASEval instanceof SubsetEvaluator)) {
/* 109:235 */       throw new Exception(ASEval.getClass().getName() + " is not a " + "Subset evaluator!");
/* 110:    */     }
/* 111:239 */     if ((ASEval instanceof UnsupervisedSubsetEvaluator))
/* 112:    */     {
/* 113:240 */       this.m_hasClass = false;
/* 114:    */     }
/* 115:    */     else
/* 116:    */     {
/* 117:242 */       this.m_hasClass = true;
/* 118:243 */       this.m_classIndex = data.classIndex();
/* 119:    */     }
/* 120:246 */     SubsetEvaluator ASEvaluator = (SubsetEvaluator)ASEval;
/* 121:247 */     this.m_numAttribs = data.numAttributes();
/* 122:    */     
/* 123:249 */     double best_merit = ASEvaluator.evaluateSubset(this.m_bestGroup);
/* 124:250 */     this.m_evaluations += 1;
/* 125:251 */     int sizeOfBest = countFeatures(this.m_bestGroup);
/* 126:    */     
/* 127:253 */     BitSet tempGroup = new BitSet(this.m_numAttribs);
/* 128:254 */     double tempMerit = ASEvaluator.evaluateSubset(tempGroup);
/* 129:256 */     if (this.m_verbose) {
/* 130:257 */       System.out.println("Zero feature subset (" + Utils.doubleToString(Math.abs(tempMerit), 8, 5) + ")");
/* 131:    */     }
/* 132:261 */     if (tempMerit >= best_merit)
/* 133:    */     {
/* 134:262 */       int tempSize = countFeatures(tempGroup);
/* 135:263 */       if ((tempMerit > best_merit) || (tempSize < sizeOfBest))
/* 136:    */       {
/* 137:264 */         best_merit = tempMerit;
/* 138:265 */         this.m_bestGroup = ((BitSet)tempGroup.clone());
/* 139:266 */         sizeOfBest = tempSize;
/* 140:    */       }
/* 141:    */     }
/* 142:270 */     int numatts = this.m_hasClass ? this.m_numAttribs - 1 : this.m_numAttribs;
/* 143:271 */     BigInteger searchSpaceEnd = BigInteger.ONE.add(BigInteger.ONE).pow(numatts).subtract(BigInteger.ONE);
/* 144:274 */     while (!done)
/* 145:    */     {
/* 146:276 */       space = space.add(BigInteger.ONE);
/* 147:277 */       if (space.equals(searchSpaceEnd)) {
/* 148:278 */         done = true;
/* 149:    */       }
/* 150:280 */       tempGroup.clear();
/* 151:281 */       for (int i = 0; i < numatts; i++) {
/* 152:282 */         if (space.testBit(i)) {
/* 153:283 */           if (!this.m_hasClass)
/* 154:    */           {
/* 155:284 */             tempGroup.set(i);
/* 156:    */           }
/* 157:    */           else
/* 158:    */           {
/* 159:286 */             int j = i >= this.m_classIndex ? i + 1 : i;
/* 160:287 */             tempGroup.set(j);
/* 161:    */           }
/* 162:    */         }
/* 163:    */       }
/* 164:292 */       tempMerit = ASEvaluator.evaluateSubset(tempGroup);
/* 165:293 */       this.m_evaluations += 1;
/* 166:294 */       if (tempMerit >= best_merit)
/* 167:    */       {
/* 168:295 */         int tempSize = countFeatures(tempGroup);
/* 169:296 */         if ((tempMerit > best_merit) || (tempSize < sizeOfBest))
/* 170:    */         {
/* 171:297 */           best_merit = tempMerit;
/* 172:298 */           this.m_bestGroup = ((BitSet)tempGroup.clone());
/* 173:299 */           sizeOfBest = tempSize;
/* 174:300 */           if (this.m_verbose) {
/* 175:301 */             System.out.println("New best subset (" + Utils.doubleToString(Math.abs(best_merit), 8, 5) + "): " + printSubset(this.m_bestGroup));
/* 176:    */           }
/* 177:    */         }
/* 178:    */       }
/* 179:    */     }
/* 180:309 */     this.m_bestMerit = best_merit;
/* 181:    */     
/* 182:311 */     return attributeList(this.m_bestGroup);
/* 183:    */   }
/* 184:    */   
/* 185:    */   private int countFeatures(BitSet featureSet)
/* 186:    */   {
/* 187:321 */     int count = 0;
/* 188:322 */     for (int i = 0; i < this.m_numAttribs; i++) {
/* 189:323 */       if (featureSet.get(i)) {
/* 190:324 */         count++;
/* 191:    */       }
/* 192:    */     }
/* 193:327 */     return count;
/* 194:    */   }
/* 195:    */   
/* 196:    */   private String printSubset(BitSet temp)
/* 197:    */   {
/* 198:337 */     StringBuffer text = new StringBuffer();
/* 199:339 */     for (int j = 0; j < this.m_numAttribs; j++) {
/* 200:340 */       if (temp.get(j)) {
/* 201:341 */         text.append(j + 1 + " ");
/* 202:    */       }
/* 203:    */     }
/* 204:344 */     return text.toString();
/* 205:    */   }
/* 206:    */   
/* 207:    */   private int[] attributeList(BitSet group)
/* 208:    */   {
/* 209:354 */     int count = 0;
/* 210:357 */     for (int i = 0; i < this.m_numAttribs; i++) {
/* 211:358 */       if (group.get(i)) {
/* 212:359 */         count++;
/* 213:    */       }
/* 214:    */     }
/* 215:363 */     int[] list = new int[count];
/* 216:364 */     count = 0;
/* 217:366 */     for (int i = 0; i < this.m_numAttribs; i++) {
/* 218:367 */       if (group.get(i)) {
/* 219:368 */         list[(count++)] = i;
/* 220:    */       }
/* 221:    */     }
/* 222:372 */     return list;
/* 223:    */   }
/* 224:    */   
/* 225:    */   private void resetOptions()
/* 226:    */   {
/* 227:379 */     this.m_verbose = false;
/* 228:380 */     this.m_evaluations = 0;
/* 229:    */   }
/* 230:    */   
/* 231:    */   public String getRevision()
/* 232:    */   {
/* 233:390 */     return RevisionUtils.extract("$Revision: 10325 $");
/* 234:    */   }
/* 235:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.attributeSelection.ExhaustiveSearch
 * JD-Core Version:    0.7.0.1
 */