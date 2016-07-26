/*   1:    */ package weka.attributeSelection;
/*   2:    */ 
/*   3:    */ import java.util.Enumeration;
/*   4:    */ import java.util.Vector;
/*   5:    */ import weka.core.Instances;
/*   6:    */ import weka.core.Option;
/*   7:    */ import weka.core.OptionHandler;
/*   8:    */ import weka.core.Range;
/*   9:    */ import weka.core.RevisionUtils;
/*  10:    */ import weka.core.Utils;
/*  11:    */ 
/*  12:    */ public class Ranker
/*  13:    */   extends ASSearch
/*  14:    */   implements RankedOutputSearch, StartSetHandler, OptionHandler
/*  15:    */ {
/*  16:    */   static final long serialVersionUID = -9086714848510751934L;
/*  17:    */   private int[] m_starting;
/*  18:    */   private Range m_startRange;
/*  19:    */   private int[] m_attributeList;
/*  20:    */   private double[] m_attributeMerit;
/*  21:    */   private boolean m_hasClass;
/*  22:    */   private int m_classIndex;
/*  23:    */   private int m_numAttribs;
/*  24:    */   private double m_threshold;
/*  25:106 */   private int m_numToSelect = -1;
/*  26:109 */   private int m_calculatedNumToSelect = -1;
/*  27:    */   
/*  28:    */   public String globalInfo()
/*  29:    */   {
/*  30:118 */     return "Ranker : \n\nRanks attributes by their individual evaluations. Use in conjunction with attribute evaluators (ReliefF, GainRatio, Entropy etc).\n";
/*  31:    */   }
/*  32:    */   
/*  33:    */   public Ranker()
/*  34:    */   {
/*  35:127 */     resetOptions();
/*  36:    */   }
/*  37:    */   
/*  38:    */   public String numToSelectTipText()
/*  39:    */   {
/*  40:137 */     return "Specify the number of attributes to retain. The default value (-1) indicates that all attributes are to be retained. Use either this option or a threshold to reduce the attribute set.";
/*  41:    */   }
/*  42:    */   
/*  43:    */   public void setNumToSelect(int n)
/*  44:    */   {
/*  45:150 */     this.m_numToSelect = n;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public int getNumToSelect()
/*  49:    */   {
/*  50:160 */     return this.m_numToSelect;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public int getCalculatedNumToSelect()
/*  54:    */   {
/*  55:172 */     if (this.m_numToSelect >= 0) {
/*  56:173 */       this.m_calculatedNumToSelect = (this.m_numToSelect > this.m_attributeMerit.length ? this.m_attributeMerit.length : this.m_numToSelect);
/*  57:    */     }
/*  58:177 */     return this.m_calculatedNumToSelect;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public String thresholdTipText()
/*  62:    */   {
/*  63:187 */     return "Set threshold by which attributes can be discarded. Default value results in no attributes being discarded. Use either this option or numToSelect to reduce the attribute set.";
/*  64:    */   }
/*  65:    */   
/*  66:    */   public void setThreshold(double threshold)
/*  67:    */   {
/*  68:200 */     this.m_threshold = threshold;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public double getThreshold()
/*  72:    */   {
/*  73:209 */     return this.m_threshold;
/*  74:    */   }
/*  75:    */   
/*  76:    */   public String generateRankingTipText()
/*  77:    */   {
/*  78:219 */     return "A constant option. Ranker is only capable of generating  attribute rankings.";
/*  79:    */   }
/*  80:    */   
/*  81:    */   public void setGenerateRanking(boolean doRank) {}
/*  82:    */   
/*  83:    */   public boolean getGenerateRanking()
/*  84:    */   {
/*  85:242 */     return true;
/*  86:    */   }
/*  87:    */   
/*  88:    */   public String startSetTipText()
/*  89:    */   {
/*  90:252 */     return "Specify a set of attributes to ignore.  When generating the ranking, Ranker will not evaluate the attributes  in this list. This is specified as a comma seperated list off attribute indexes starting at 1. It can include ranges. Eg. 1,2,5-9,17.";
/*  91:    */   }
/*  92:    */   
/*  93:    */   public void setStartSet(String startSet)
/*  94:    */     throws Exception
/*  95:    */   {
/*  96:269 */     this.m_startRange.setRanges(startSet);
/*  97:    */   }
/*  98:    */   
/*  99:    */   public String getStartSet()
/* 100:    */   {
/* 101:279 */     return this.m_startRange.getRanges();
/* 102:    */   }
/* 103:    */   
/* 104:    */   public Enumeration<Option> listOptions()
/* 105:    */   {
/* 106:289 */     Vector<Option> newVector = new Vector(3);
/* 107:    */     
/* 108:291 */     newVector.addElement(new Option("\tSpecify a starting set of attributes.\n\tEg. 1,3,5-7.\n\tAny starting attributes specified are\n\tignored during the ranking.", "P", 1, "-P <start set>"));
/* 109:    */     
/* 110:    */ 
/* 111:294 */     newVector.addElement(new Option("\tSpecify a theshold by which attributes\n\tmay be discarded from the ranking.", "T", 1, "-T <threshold>"));
/* 112:    */     
/* 113:    */ 
/* 114:    */ 
/* 115:298 */     newVector.addElement(new Option("\tSpecify number of attributes to select", "N", 1, "-N <num to select>"));
/* 116:    */     
/* 117:    */ 
/* 118:301 */     return newVector.elements();
/* 119:    */   }
/* 120:    */   
/* 121:    */   public void setOptions(String[] options)
/* 122:    */     throws Exception
/* 123:    */   {
/* 124:339 */     resetOptions();
/* 125:    */     
/* 126:341 */     String optionString = Utils.getOption('P', options);
/* 127:342 */     if (optionString.length() != 0) {
/* 128:343 */       setStartSet(optionString);
/* 129:    */     }
/* 130:346 */     optionString = Utils.getOption('T', options);
/* 131:347 */     if (optionString.length() != 0)
/* 132:    */     {
/* 133:349 */       Double temp = Double.valueOf(optionString);
/* 134:350 */       setThreshold(temp.doubleValue());
/* 135:    */     }
/* 136:353 */     optionString = Utils.getOption('N', options);
/* 137:354 */     if (optionString.length() != 0) {
/* 138:355 */       setNumToSelect(Integer.parseInt(optionString));
/* 139:    */     }
/* 140:    */   }
/* 141:    */   
/* 142:    */   public String[] getOptions()
/* 143:    */   {
/* 144:367 */     Vector<String> options = new Vector();
/* 145:369 */     if (!getStartSet().equals(""))
/* 146:    */     {
/* 147:370 */       options.add("-P");
/* 148:371 */       options.add("" + startSetToString());
/* 149:    */     }
/* 150:374 */     options.add("-T");
/* 151:375 */     options.add("" + getThreshold());
/* 152:    */     
/* 153:377 */     options.add("-N");
/* 154:378 */     options.add("" + getNumToSelect());
/* 155:    */     
/* 156:380 */     return (String[])options.toArray(new String[0]);
/* 157:    */   }
/* 158:    */   
/* 159:    */   private String startSetToString()
/* 160:    */   {
/* 161:393 */     StringBuffer FString = new StringBuffer();
/* 162:396 */     if (this.m_starting == null) {
/* 163:397 */       return getStartSet();
/* 164:    */     }
/* 165:400 */     for (int i = 0; i < this.m_starting.length; i++)
/* 166:    */     {
/* 167:401 */       boolean didPrint = false;
/* 168:403 */       if ((!this.m_hasClass) || ((this.m_hasClass == true) && (i != this.m_classIndex)))
/* 169:    */       {
/* 170:404 */         FString.append(this.m_starting[i] + 1);
/* 171:405 */         didPrint = true;
/* 172:    */       }
/* 173:408 */       if (i == this.m_starting.length - 1) {
/* 174:409 */         FString.append("");
/* 175:411 */       } else if (didPrint) {
/* 176:412 */         FString.append(",");
/* 177:    */       }
/* 178:    */     }
/* 179:417 */     return FString.toString();
/* 180:    */   }
/* 181:    */   
/* 182:    */   public int[] search(ASEvaluation ASEval, Instances data)
/* 183:    */     throws Exception
/* 184:    */   {
/* 185:434 */     if (!(ASEval instanceof AttributeEvaluator)) {
/* 186:435 */       throw new Exception(ASEval.getClass().getName() + " is not a" + "Attribute evaluator!");
/* 187:    */     }
/* 188:439 */     this.m_numAttribs = data.numAttributes();
/* 189:441 */     if ((ASEval instanceof UnsupervisedAttributeEvaluator))
/* 190:    */     {
/* 191:442 */       this.m_hasClass = false;
/* 192:    */     }
/* 193:    */     else
/* 194:    */     {
/* 195:444 */       this.m_classIndex = data.classIndex();
/* 196:445 */       if (this.m_classIndex >= 0) {
/* 197:446 */         this.m_hasClass = true;
/* 198:    */       } else {
/* 199:448 */         this.m_hasClass = false;
/* 200:    */       }
/* 201:    */     }
/* 202:454 */     if ((ASEval instanceof AttributeTransformer))
/* 203:    */     {
/* 204:455 */       data = ((AttributeTransformer)ASEval).transformedHeader();
/* 205:456 */       if ((this.m_classIndex >= 0) && (data.classIndex() >= 0))
/* 206:    */       {
/* 207:457 */         this.m_classIndex = data.classIndex();
/* 208:458 */         this.m_hasClass = true;
/* 209:    */       }
/* 210:    */     }
/* 211:462 */     this.m_startRange.setUpper(this.m_numAttribs - 1);
/* 212:463 */     if (!getStartSet().equals("")) {
/* 213:464 */       this.m_starting = this.m_startRange.getSelection();
/* 214:    */     }
/* 215:467 */     int sl = 0;
/* 216:468 */     if (this.m_starting != null) {
/* 217:469 */       sl = this.m_starting.length;
/* 218:    */     }
/* 219:471 */     if ((this.m_starting != null) && (this.m_hasClass == true))
/* 220:    */     {
/* 221:473 */       boolean ok = false;
/* 222:474 */       for (int i = 0; i < sl; i++) {
/* 223:475 */         if (this.m_starting[i] == this.m_classIndex)
/* 224:    */         {
/* 225:476 */           ok = true;
/* 226:477 */           break;
/* 227:    */         }
/* 228:    */       }
/* 229:481 */       if (!ok) {
/* 230:482 */         sl++;
/* 231:    */       }
/* 232:    */     }
/* 233:485 */     else if (this.m_hasClass == true)
/* 234:    */     {
/* 235:486 */       sl++;
/* 236:    */     }
/* 237:490 */     this.m_attributeList = new int[this.m_numAttribs - sl];
/* 238:491 */     this.m_attributeMerit = new double[this.m_numAttribs - sl];
/* 239:    */     
/* 240:    */ 
/* 241:494 */     int i = 0;
/* 242:494 */     for (int j = 0; i < this.m_numAttribs; i++) {
/* 243:495 */       if (!inStarting(i)) {
/* 244:496 */         this.m_attributeList[(j++)] = i;
/* 245:    */       }
/* 246:    */     }
/* 247:500 */     AttributeEvaluator ASEvaluator = (AttributeEvaluator)ASEval;
/* 248:502 */     for (i = 0; i < this.m_attributeList.length; i++) {
/* 249:503 */       this.m_attributeMerit[i] = ASEvaluator.evaluateAttribute(this.m_attributeList[i]);
/* 250:    */     }
/* 251:506 */     double[][] tempRanked = rankedAttributes();
/* 252:507 */     int[] rankedAttributes = new int[this.m_attributeList.length];
/* 253:509 */     for (i = 0; i < this.m_attributeList.length; i++) {
/* 254:510 */       rankedAttributes[i] = ((int)tempRanked[i][0]);
/* 255:    */     }
/* 256:513 */     return rankedAttributes;
/* 257:    */   }
/* 258:    */   
/* 259:    */   public double[][] rankedAttributes()
/* 260:    */     throws Exception
/* 261:    */   {
/* 262:526 */     if ((this.m_attributeList == null) || (this.m_attributeMerit == null)) {
/* 263:527 */       throw new Exception("Search must be performed before a ranked attribute list can be obtained");
/* 264:    */     }
/* 265:531 */     int[] ranked = Utils.sort(this.m_attributeMerit);
/* 266:    */     
/* 267:533 */     double[][] bestToWorst = new double[ranked.length][2];
/* 268:    */     
/* 269:535 */     int i = ranked.length - 1;
/* 270:535 */     for (int j = 0; i >= 0; i--) {
/* 271:536 */       bestToWorst[(j++)][0] = ranked[i];
/* 272:    */     }
/* 273:540 */     for (i = 0; i < bestToWorst.length; i++)
/* 274:    */     {
/* 275:541 */       int temp = (int)bestToWorst[i][0];
/* 276:542 */       bestToWorst[i][0] = this.m_attributeList[temp];
/* 277:543 */       bestToWorst[i][1] = this.m_attributeMerit[temp];
/* 278:    */     }
/* 279:550 */     if (this.m_numToSelect <= 0) {
/* 280:551 */       if (this.m_threshold == -1.797693134862316E+308D) {
/* 281:552 */         this.m_calculatedNumToSelect = bestToWorst.length;
/* 282:    */       } else {
/* 283:554 */         determineNumToSelectFromThreshold(bestToWorst);
/* 284:    */       }
/* 285:    */     }
/* 286:561 */     return bestToWorst;
/* 287:    */   }
/* 288:    */   
/* 289:    */   private void determineNumToSelectFromThreshold(double[][] ranking)
/* 290:    */   {
/* 291:565 */     int count = 0;
/* 292:566 */     for (double[] element : ranking) {
/* 293:567 */       if (element[1] > this.m_threshold) {
/* 294:568 */         count++;
/* 295:    */       }
/* 296:    */     }
/* 297:571 */     this.m_calculatedNumToSelect = count;
/* 298:    */   }
/* 299:    */   
/* 300:    */   public String toString()
/* 301:    */   {
/* 302:581 */     StringBuffer BfString = new StringBuffer();
/* 303:582 */     BfString.append("\tAttribute ranking.\n");
/* 304:584 */     if (this.m_starting != null)
/* 305:    */     {
/* 306:585 */       BfString.append("\tIgnored attributes: ");
/* 307:    */       
/* 308:587 */       BfString.append(startSetToString());
/* 309:588 */       BfString.append("\n");
/* 310:    */     }
/* 311:591 */     if (this.m_threshold != -1.797693134862316E+308D) {
/* 312:592 */       BfString.append("\tThreshold for discarding attributes: " + Utils.doubleToString(this.m_threshold, 8, 4) + "\n");
/* 313:    */     }
/* 314:596 */     return BfString.toString();
/* 315:    */   }
/* 316:    */   
/* 317:    */   protected void resetOptions()
/* 318:    */   {
/* 319:603 */     this.m_starting = null;
/* 320:604 */     this.m_startRange = new Range();
/* 321:605 */     this.m_attributeList = null;
/* 322:606 */     this.m_attributeMerit = null;
/* 323:607 */     this.m_threshold = -1.797693134862316E+308D;
/* 324:    */   }
/* 325:    */   
/* 326:    */   private boolean inStarting(int feat)
/* 327:    */   {
/* 328:612 */     if ((this.m_hasClass == true) && (feat == this.m_classIndex)) {
/* 329:613 */       return true;
/* 330:    */     }
/* 331:616 */     if (this.m_starting == null) {
/* 332:617 */       return false;
/* 333:    */     }
/* 334:620 */     for (int element : this.m_starting) {
/* 335:621 */       if (element == feat) {
/* 336:622 */         return true;
/* 337:    */       }
/* 338:    */     }
/* 339:626 */     return false;
/* 340:    */   }
/* 341:    */   
/* 342:    */   public String getRevision()
/* 343:    */   {
/* 344:636 */     return RevisionUtils.extract("$Revision: 11213 $");
/* 345:    */   }
/* 346:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.attributeSelection.Ranker
 * JD-Core Version:    0.7.0.1
 */