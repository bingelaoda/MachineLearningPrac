/*   1:    */ package weka.datagenerators.classifiers.classification;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.classifiers.bayes.net.BayesNetGenerator;
/*   7:    */ import weka.core.Instance;
/*   8:    */ import weka.core.Instances;
/*   9:    */ import weka.core.Option;
/*  10:    */ import weka.core.RevisionUtils;
/*  11:    */ import weka.core.Utils;
/*  12:    */ import weka.datagenerators.ClassificationGenerator;
/*  13:    */ 
/*  14:    */ public class BayesNet
/*  15:    */   extends ClassificationGenerator
/*  16:    */ {
/*  17:    */   static final long serialVersionUID = -796118162379901512L;
/*  18:    */   protected BayesNetGenerator m_Generator;
/*  19:    */   
/*  20:    */   public BayesNet()
/*  21:    */   {
/*  22: 94 */     setNumAttributes(defaultNumAttributes());
/*  23: 95 */     setNumArcs(defaultNumArcs());
/*  24: 96 */     setCardinality(defaultCardinality());
/*  25:    */   }
/*  26:    */   
/*  27:    */   public String globalInfo()
/*  28:    */   {
/*  29:106 */     return "Generates random instances based on a Bayes network.";
/*  30:    */   }
/*  31:    */   
/*  32:    */   public Enumeration<Option> listOptions()
/*  33:    */   {
/*  34:116 */     Vector<Option> result = enumToVector(super.listOptions());
/*  35:    */     
/*  36:118 */     result.add(new Option("\tThe number of arcs to use. (default " + defaultNumArcs() + ")", "A", 1, "-A <num>"));
/*  37:    */     
/*  38:    */ 
/*  39:121 */     result.add(new Option("\tThe number of attributes to generate. (default " + defaultNumAttributes() + ")", "N", 1, "-N <num>"));
/*  40:    */     
/*  41:    */ 
/*  42:124 */     result.add(new Option("\tThe cardinality of the attributes and the class. (default " + defaultCardinality() + ")", "C", 1, "-C <num>"));
/*  43:    */     
/*  44:    */ 
/*  45:    */ 
/*  46:128 */     return result.elements();
/*  47:    */   }
/*  48:    */   
/*  49:    */   public void setOptions(String[] options)
/*  50:    */     throws Exception
/*  51:    */   {
/*  52:176 */     super.setOptions(options);
/*  53:    */     
/*  54:178 */     Vector<String> list = new Vector();
/*  55:    */     
/*  56:180 */     list.add("-N");
/*  57:181 */     String tmpStr = Utils.getOption('N', options);
/*  58:182 */     if (tmpStr.length() != 0) {
/*  59:183 */       list.add(tmpStr);
/*  60:    */     } else {
/*  61:185 */       list.add("" + defaultNumAttributes());
/*  62:    */     }
/*  63:189 */     list.add("-M");
/*  64:190 */     list.add("" + getNumExamples());
/*  65:    */     
/*  66:192 */     list.add("-S");
/*  67:193 */     tmpStr = Utils.getOption('S', options);
/*  68:194 */     if (tmpStr.length() != 0) {
/*  69:195 */       list.add(tmpStr);
/*  70:    */     } else {
/*  71:197 */       list.add("" + defaultSeed());
/*  72:    */     }
/*  73:200 */     list.add("-A");
/*  74:201 */     tmpStr = Utils.getOption('A', options);
/*  75:202 */     if (tmpStr.length() != 0) {
/*  76:203 */       list.add(tmpStr);
/*  77:    */     } else {
/*  78:205 */       list.add("" + defaultNumArcs());
/*  79:    */     }
/*  80:208 */     list.add("-C");
/*  81:209 */     tmpStr = Utils.getOption('C', options);
/*  82:210 */     if (tmpStr.length() != 0) {
/*  83:211 */       list.add(tmpStr);
/*  84:    */     } else {
/*  85:213 */       list.add("" + defaultCardinality());
/*  86:    */     }
/*  87:216 */     setGeneratorOptions(list);
/*  88:    */   }
/*  89:    */   
/*  90:    */   public String[] getOptions()
/*  91:    */   {
/*  92:226 */     Vector<String> result = new Vector();
/*  93:    */     
/*  94:228 */     String[] options = removeBlacklist(super.getOptions());
/*  95:229 */     Collections.addAll(result, options);
/*  96:    */     
/*  97:    */ 
/*  98:232 */     options = getGenerator().getOptions();
/*  99:    */     
/* 100:234 */     result.add("-N");
/* 101:235 */     result.add("" + getNumAttributes());
/* 102:    */     
/* 103:237 */     result.add("-S");
/* 104:238 */     result.add("" + getSeed());
/* 105:    */     try
/* 106:    */     {
/* 107:241 */       result.add("-A");
/* 108:242 */       result.add(Utils.getOption('A', options));
/* 109:    */     }
/* 110:    */     catch (Exception e)
/* 111:    */     {
/* 112:244 */       e.printStackTrace();
/* 113:    */     }
/* 114:    */     try
/* 115:    */     {
/* 116:248 */       result.add("-C");
/* 117:249 */       result.add(Utils.getOption('C', options));
/* 118:    */     }
/* 119:    */     catch (Exception e)
/* 120:    */     {
/* 121:251 */       e.printStackTrace();
/* 122:    */     }
/* 123:254 */     return (String[])result.toArray(new String[result.size()]);
/* 124:    */   }
/* 125:    */   
/* 126:    */   protected void setGeneratorOptions(BayesNetGenerator generator, Vector<String> options)
/* 127:    */   {
/* 128:    */     try
/* 129:    */     {
/* 130:267 */       generator.setOptions((String[])options.toArray(new String[options.size()]));
/* 131:    */     }
/* 132:    */     catch (Exception e)
/* 133:    */     {
/* 134:269 */       e.printStackTrace();
/* 135:    */     }
/* 136:    */   }
/* 137:    */   
/* 138:    */   protected BayesNetGenerator getGenerator()
/* 139:    */   {
/* 140:279 */     if (this.m_Generator == null) {
/* 141:280 */       this.m_Generator = new BayesNetGenerator();
/* 142:    */     }
/* 143:283 */     return this.m_Generator;
/* 144:    */   }
/* 145:    */   
/* 146:    */   protected void setGeneratorOptions(Vector<String> options)
/* 147:    */   {
/* 148:292 */     setGeneratorOptions(getGenerator(), options);
/* 149:    */   }
/* 150:    */   
/* 151:    */   protected void setGeneratorOption(BayesNetGenerator generator, String option, String value)
/* 152:    */   {
/* 153:    */     try
/* 154:    */     {
/* 155:311 */       String[] options = generator.getOptions();
/* 156:312 */       Utils.getOption(option, options);
/* 157:    */       
/* 158:    */ 
/* 159:315 */       Vector<String> list = new Vector();
/* 160:316 */       for (int i = 0; i < options.length; i++) {
/* 161:317 */         if (options[i].length() != 0) {
/* 162:318 */           list.add(options[i]);
/* 163:    */         }
/* 164:    */       }
/* 165:321 */       list.add("-" + option);
/* 166:322 */       list.add(value);
/* 167:323 */       setGeneratorOptions(generator, list);
/* 168:    */     }
/* 169:    */     catch (Exception e)
/* 170:    */     {
/* 171:325 */       e.printStackTrace();
/* 172:    */     }
/* 173:    */   }
/* 174:    */   
/* 175:    */   protected void setGeneratorOption(String option, String value)
/* 176:    */   {
/* 177:336 */     setGeneratorOption(getGenerator(), option, value);
/* 178:    */   }
/* 179:    */   
/* 180:    */   protected int defaultNumAttributes()
/* 181:    */   {
/* 182:345 */     return 10;
/* 183:    */   }
/* 184:    */   
/* 185:    */   public void setNumAttributes(int numAttributes)
/* 186:    */   {
/* 187:354 */     setGeneratorOption("N", "" + numAttributes);
/* 188:    */   }
/* 189:    */   
/* 190:    */   public int getNumAttributes()
/* 191:    */   {
/* 192:365 */     int result = -1;
/* 193:    */     try
/* 194:    */     {
/* 195:367 */       result = Integer.parseInt(Utils.getOption('N', getGenerator().getOptions()));
/* 196:    */     }
/* 197:    */     catch (Exception e)
/* 198:    */     {
/* 199:370 */       e.printStackTrace();
/* 200:371 */       result = -1;
/* 201:    */     }
/* 202:374 */     return result;
/* 203:    */   }
/* 204:    */   
/* 205:    */   public String numAttributesTipText()
/* 206:    */   {
/* 207:384 */     return "The number of attributes the generated data will contain (including class attribute), ie the number of nodes in the bayesian net.";
/* 208:    */   }
/* 209:    */   
/* 210:    */   protected int defaultCardinality()
/* 211:    */   {
/* 212:393 */     return 2;
/* 213:    */   }
/* 214:    */   
/* 215:    */   public void setCardinality(int value)
/* 216:    */   {
/* 217:402 */     setGeneratorOption("C", "" + value);
/* 218:    */   }
/* 219:    */   
/* 220:    */   public int getCardinality()
/* 221:    */   {
/* 222:413 */     int result = -1;
/* 223:    */     try
/* 224:    */     {
/* 225:415 */       result = Integer.parseInt(Utils.getOption('C', getGenerator().getOptions()));
/* 226:    */     }
/* 227:    */     catch (Exception e)
/* 228:    */     {
/* 229:418 */       e.printStackTrace();
/* 230:419 */       result = -1;
/* 231:    */     }
/* 232:422 */     return result;
/* 233:    */   }
/* 234:    */   
/* 235:    */   public String cardinalityTipText()
/* 236:    */   {
/* 237:432 */     return "The cardinality of the attributes, incl the class attribute.";
/* 238:    */   }
/* 239:    */   
/* 240:    */   protected int defaultNumArcs()
/* 241:    */   {
/* 242:441 */     return 20;
/* 243:    */   }
/* 244:    */   
/* 245:    */   public void setNumArcs(int value)
/* 246:    */   {
/* 247:454 */     int nodes = getNumAttributes();
/* 248:455 */     int minArcs = nodes - 1;
/* 249:456 */     int maxArcs = nodes * (nodes - 1) / 2;
/* 250:458 */     if (value > maxArcs) {
/* 251:459 */       throw new IllegalArgumentException("Number of arcs should be at most nodes * (nodes - 1) / 2 = " + maxArcs + " instead of " + value + " (nodes = numAttributes)!");
/* 252:    */     }
/* 253:462 */     if (value < minArcs) {
/* 254:463 */       throw new IllegalArgumentException("Number of arcs should be at least (nodes - 1) = " + minArcs + " instead of " + value + " (nodes = numAttributes)!");
/* 255:    */     }
/* 256:467 */     setGeneratorOption("A", "" + value);
/* 257:    */   }
/* 258:    */   
/* 259:    */   public int getNumArcs()
/* 260:    */   {
/* 261:479 */     int result = -1;
/* 262:    */     try
/* 263:    */     {
/* 264:481 */       result = Integer.parseInt(Utils.getOption('A', getGenerator().getOptions()));
/* 265:    */     }
/* 266:    */     catch (Exception e)
/* 267:    */     {
/* 268:484 */       e.printStackTrace();
/* 269:485 */       result = -1;
/* 270:    */     }
/* 271:488 */     return result;
/* 272:    */   }
/* 273:    */   
/* 274:    */   public String numArcsTipText()
/* 275:    */   {
/* 276:498 */     return "The number of arcs in the bayesian net, at most: n * (n - 1) / 2 and at least: (n - 1); with n = numAttributes";
/* 277:    */   }
/* 278:    */   
/* 279:    */   public void setNumExamples(int numExamples)
/* 280:    */   {
/* 281:508 */     super.setNumExamples(numExamples);
/* 282:509 */     setGeneratorOption("M", "" + numExamples);
/* 283:    */   }
/* 284:    */   
/* 285:    */   public int getNumExamples()
/* 286:    */   {
/* 287:521 */     int result = -1;
/* 288:    */     try
/* 289:    */     {
/* 290:523 */       result = Integer.parseInt(Utils.getOption('M', getGenerator().getOptions()));
/* 291:    */     }
/* 292:    */     catch (Exception e)
/* 293:    */     {
/* 294:526 */       e.printStackTrace();
/* 295:527 */       result = -1;
/* 296:    */     }
/* 297:530 */     return result;
/* 298:    */   }
/* 299:    */   
/* 300:    */   public int getSeed()
/* 301:    */   {
/* 302:542 */     int result = -1;
/* 303:    */     try
/* 304:    */     {
/* 305:544 */       result = Integer.parseInt(Utils.getOption('S', getGenerator().getOptions()));
/* 306:    */     }
/* 307:    */     catch (Exception e)
/* 308:    */     {
/* 309:547 */       e.printStackTrace();
/* 310:548 */       result = -1;
/* 311:    */     }
/* 312:551 */     return result;
/* 313:    */   }
/* 314:    */   
/* 315:    */   public void setSeed(int newSeed)
/* 316:    */   {
/* 317:561 */     super.setSeed(newSeed);
/* 318:562 */     setGeneratorOption("S", "" + newSeed);
/* 319:    */   }
/* 320:    */   
/* 321:    */   public boolean getSingleModeFlag()
/* 322:    */     throws Exception
/* 323:    */   {
/* 324:574 */     return false;
/* 325:    */   }
/* 326:    */   
/* 327:    */   public Instances defineDataFormat()
/* 328:    */     throws Exception
/* 329:    */   {
/* 330:590 */     BayesNetGenerator bng = new BayesNetGenerator();
/* 331:591 */     bng.setOptions(getGenerator().getOptions());
/* 332:592 */     setGeneratorOption(bng, "M", "1");
/* 333:593 */     bng.generateRandomNetwork();
/* 334:594 */     bng.generateInstances();
/* 335:595 */     bng.m_Instances.renameAttribute(0, "class");
/* 336:596 */     bng.m_Instances.setRelationName(getRelationNameToUse());
/* 337:    */     
/* 338:598 */     return bng.m_Instances;
/* 339:    */   }
/* 340:    */   
/* 341:    */   public Instance generateExample()
/* 342:    */     throws Exception
/* 343:    */   {
/* 344:611 */     throw new Exception("Cannot generate examples one-by-one!");
/* 345:    */   }
/* 346:    */   
/* 347:    */   public Instances generateExamples()
/* 348:    */     throws Exception
/* 349:    */   {
/* 350:626 */     getGenerator().setOptions(getGenerator().getOptions());
/* 351:627 */     getGenerator().generateRandomNetwork();
/* 352:628 */     getGenerator().generateInstances();
/* 353:629 */     getGenerator().m_Instances.renameAttribute(0, "class");
/* 354:630 */     getGenerator().m_Instances.setRelationName(getRelationNameToUse());
/* 355:    */     
/* 356:632 */     return getGenerator().m_Instances;
/* 357:    */   }
/* 358:    */   
/* 359:    */   public String generateStart()
/* 360:    */   {
/* 361:644 */     return "";
/* 362:    */   }
/* 363:    */   
/* 364:    */   public String generateFinished()
/* 365:    */     throws Exception
/* 366:    */   {
/* 367:656 */     return "";
/* 368:    */   }
/* 369:    */   
/* 370:    */   public String getRevision()
/* 371:    */   {
/* 372:666 */     return RevisionUtils.extract("$Revision: 11753 $");
/* 373:    */   }
/* 374:    */   
/* 375:    */   public static void main(String[] args)
/* 376:    */   {
/* 377:675 */     runDataGenerator(new BayesNet(), args);
/* 378:    */   }
/* 379:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.datagenerators.classifiers.classification.BayesNet
 * JD-Core Version:    0.7.0.1
 */