/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Random;
/*   6:    */ import java.util.StringTokenizer;
/*   7:    */ import java.util.Vector;
/*   8:    */ 
/*   9:    */ public abstract class CheckScheme
/*  10:    */   extends Check
/*  11:    */ {
/*  12:    */   public static class PostProcessor
/*  13:    */     implements RevisionHandler
/*  14:    */   {
/*  15:    */     public Instances process(Instances data)
/*  16:    */     {
/*  17: 53 */       return data;
/*  18:    */     }
/*  19:    */     
/*  20:    */     public String getRevision()
/*  21:    */     {
/*  22: 62 */       return RevisionUtils.extract("$Revision: 11247 $");
/*  23:    */     }
/*  24:    */   }
/*  25:    */   
/*  26: 67 */   protected int m_NumInstances = 20;
/*  27: 70 */   protected int m_NumNominal = 2;
/*  28: 73 */   protected int m_NumNumeric = 1;
/*  29: 76 */   protected int m_NumString = 1;
/*  30: 79 */   protected int m_NumDate = 1;
/*  31: 82 */   protected int m_NumRelational = 1;
/*  32: 86 */   protected int m_NumInstancesRelational = 10;
/*  33: 89 */   protected String[] m_Words = TestInstances.DEFAULT_WORDS;
/*  34: 92 */   protected String m_WordSeparators = " ";
/*  35: 95 */   protected PostProcessor m_PostProcessor = null;
/*  36: 98 */   protected boolean m_ClasspathProblems = false;
/*  37:    */   
/*  38:    */   public Enumeration<Option> listOptions()
/*  39:    */   {
/*  40:106 */     Vector<Option> result = new Vector();
/*  41:    */     
/*  42:108 */     result.addAll(Collections.list(super.listOptions()));
/*  43:    */     
/*  44:110 */     result.addElement(new Option("\tThe number of instances in the datasets (default 20).", "N", 1, "-N <num>"));
/*  45:    */     
/*  46:    */ 
/*  47:    */ 
/*  48:114 */     result.addElement(new Option("\tThe number of nominal attributes (default 2).", "nominal", 1, "-nominal <num>"));
/*  49:    */     
/*  50:    */ 
/*  51:    */ 
/*  52:118 */     result.addElement(new Option("\tThe number of values for nominal attributes (default 1).", "nominal-values", 1, "-nominal-values <num>"));
/*  53:    */     
/*  54:    */ 
/*  55:    */ 
/*  56:122 */     result.addElement(new Option("\tThe number of numeric attributes (default 1).", "numeric", 1, "-numeric <num>"));
/*  57:    */     
/*  58:    */ 
/*  59:    */ 
/*  60:126 */     result.addElement(new Option("\tThe number of string attributes (default 1).", "string", 1, "-string <num>"));
/*  61:    */     
/*  62:    */ 
/*  63:    */ 
/*  64:130 */     result.addElement(new Option("\tThe number of date attributes (default 1).", "date", 1, "-date <num>"));
/*  65:    */     
/*  66:    */ 
/*  67:    */ 
/*  68:134 */     result.addElement(new Option("\tThe number of relational attributes (default 1).", "relational", 1, "-relational <num>"));
/*  69:    */     
/*  70:    */ 
/*  71:    */ 
/*  72:138 */     result.addElement(new Option("\tThe number of instances in relational/bag attributes (default 10).", "num-instances-relational", 1, "-num-instances-relational <num>"));
/*  73:    */     
/*  74:    */ 
/*  75:    */ 
/*  76:142 */     result.addElement(new Option("\tThe words to use in string attributes.", "words", 1, "-words <comma-separated-list>"));
/*  77:    */     
/*  78:    */ 
/*  79:    */ 
/*  80:146 */     result.addElement(new Option("\tThe word separators to use in string attributes.", "word-separators", 1, "-word-separators <chars>"));
/*  81:    */     
/*  82:    */ 
/*  83:    */ 
/*  84:150 */     return result.elements();
/*  85:    */   }
/*  86:    */   
/*  87:    */   public void setOptions(String[] options)
/*  88:    */     throws Exception
/*  89:    */   {
/*  90:162 */     super.setOptions(options);
/*  91:    */     
/*  92:164 */     String tmpStr = Utils.getOption('N', options);
/*  93:165 */     if (tmpStr.length() != 0) {
/*  94:166 */       setNumInstances(Integer.parseInt(tmpStr));
/*  95:    */     } else {
/*  96:168 */       setNumInstances(20);
/*  97:    */     }
/*  98:170 */     tmpStr = Utils.getOption("nominal", options);
/*  99:171 */     if (tmpStr.length() != 0) {
/* 100:172 */       setNumNominal(Integer.parseInt(tmpStr));
/* 101:    */     } else {
/* 102:174 */       setNumNominal(2);
/* 103:    */     }
/* 104:176 */     tmpStr = Utils.getOption("numeric", options);
/* 105:177 */     if (tmpStr.length() != 0) {
/* 106:178 */       setNumNumeric(Integer.parseInt(tmpStr));
/* 107:    */     } else {
/* 108:180 */       setNumNumeric(1);
/* 109:    */     }
/* 110:182 */     tmpStr = Utils.getOption("string", options);
/* 111:183 */     if (tmpStr.length() != 0) {
/* 112:184 */       setNumString(Integer.parseInt(tmpStr));
/* 113:    */     } else {
/* 114:186 */       setNumString(1);
/* 115:    */     }
/* 116:188 */     tmpStr = Utils.getOption("date", options);
/* 117:189 */     if (tmpStr.length() != 0) {
/* 118:190 */       setNumDate(Integer.parseInt(tmpStr));
/* 119:    */     } else {
/* 120:192 */       setNumDate(1);
/* 121:    */     }
/* 122:194 */     tmpStr = Utils.getOption("relational", options);
/* 123:195 */     if (tmpStr.length() != 0) {
/* 124:196 */       setNumRelational(Integer.parseInt(tmpStr));
/* 125:    */     } else {
/* 126:198 */       setNumRelational(1);
/* 127:    */     }
/* 128:200 */     tmpStr = Utils.getOption("num-instances-relational", options);
/* 129:201 */     if (tmpStr.length() != 0) {
/* 130:202 */       setNumInstancesRelational(Integer.parseInt(tmpStr));
/* 131:    */     } else {
/* 132:204 */       setNumInstancesRelational(10);
/* 133:    */     }
/* 134:206 */     tmpStr = Utils.getOption("words", options);
/* 135:207 */     if (tmpStr.length() != 0) {
/* 136:208 */       setWords(tmpStr);
/* 137:    */     } else {
/* 138:210 */       setWords(new TestInstances().getWords());
/* 139:    */     }
/* 140:212 */     if (Utils.getOptionPos("word-separators", options) > -1)
/* 141:    */     {
/* 142:213 */       tmpStr = Utils.getOption("word-separators", options);
/* 143:214 */       setWordSeparators(tmpStr);
/* 144:    */     }
/* 145:    */     else
/* 146:    */     {
/* 147:217 */       setWordSeparators(" ");
/* 148:    */     }
/* 149:    */   }
/* 150:    */   
/* 151:    */   public String[] getOptions()
/* 152:    */   {
/* 153:231 */     Vector<String> result = new Vector();
/* 154:    */     
/* 155:233 */     String[] options = super.getOptions();
/* 156:234 */     for (int i = 0; i < options.length; i++) {
/* 157:235 */       result.add(options[i]);
/* 158:    */     }
/* 159:237 */     result.add("-N");
/* 160:238 */     result.add("" + getNumInstances());
/* 161:    */     
/* 162:240 */     result.add("-nominal");
/* 163:241 */     result.add("" + getNumNominal());
/* 164:    */     
/* 165:243 */     result.add("-numeric");
/* 166:244 */     result.add("" + getNumNumeric());
/* 167:    */     
/* 168:246 */     result.add("-string");
/* 169:247 */     result.add("" + getNumString());
/* 170:    */     
/* 171:249 */     result.add("-date");
/* 172:250 */     result.add("" + getNumDate());
/* 173:    */     
/* 174:252 */     result.add("-relational");
/* 175:253 */     result.add("" + getNumRelational());
/* 176:    */     
/* 177:255 */     result.add("-words");
/* 178:256 */     result.add("" + getWords());
/* 179:    */     
/* 180:258 */     result.add("-word-separators");
/* 181:259 */     result.add("" + getWordSeparators());
/* 182:    */     
/* 183:261 */     return (String[])result.toArray(new String[result.size()]);
/* 184:    */   }
/* 185:    */   
/* 186:    */   public void setPostProcessor(PostProcessor value)
/* 187:    */   {
/* 188:271 */     this.m_PostProcessor = value;
/* 189:    */   }
/* 190:    */   
/* 191:    */   public PostProcessor getPostProcessor()
/* 192:    */   {
/* 193:280 */     return this.m_PostProcessor;
/* 194:    */   }
/* 195:    */   
/* 196:    */   public boolean hasClasspathProblems()
/* 197:    */   {
/* 198:289 */     return this.m_ClasspathProblems;
/* 199:    */   }
/* 200:    */   
/* 201:    */   public abstract void doTests();
/* 202:    */   
/* 203:    */   public void setNumInstances(int value)
/* 204:    */   {
/* 205:304 */     this.m_NumInstances = value;
/* 206:    */   }
/* 207:    */   
/* 208:    */   public int getNumInstances()
/* 209:    */   {
/* 210:313 */     return this.m_NumInstances;
/* 211:    */   }
/* 212:    */   
/* 213:    */   public void setNumNominal(int value)
/* 214:    */   {
/* 215:322 */     this.m_NumNominal = value;
/* 216:    */   }
/* 217:    */   
/* 218:    */   public int getNumNominal()
/* 219:    */   {
/* 220:331 */     return this.m_NumNominal;
/* 221:    */   }
/* 222:    */   
/* 223:    */   public void setNumNumeric(int value)
/* 224:    */   {
/* 225:340 */     this.m_NumNumeric = value;
/* 226:    */   }
/* 227:    */   
/* 228:    */   public int getNumNumeric()
/* 229:    */   {
/* 230:349 */     return this.m_NumNumeric;
/* 231:    */   }
/* 232:    */   
/* 233:    */   public void setNumString(int value)
/* 234:    */   {
/* 235:358 */     this.m_NumString = value;
/* 236:    */   }
/* 237:    */   
/* 238:    */   public int getNumString()
/* 239:    */   {
/* 240:367 */     return this.m_NumString;
/* 241:    */   }
/* 242:    */   
/* 243:    */   public void setNumDate(int value)
/* 244:    */   {
/* 245:376 */     this.m_NumDate = value;
/* 246:    */   }
/* 247:    */   
/* 248:    */   public int getNumDate()
/* 249:    */   {
/* 250:385 */     return this.m_NumDate;
/* 251:    */   }
/* 252:    */   
/* 253:    */   public void setNumRelational(int value)
/* 254:    */   {
/* 255:394 */     this.m_NumRelational = value;
/* 256:    */   }
/* 257:    */   
/* 258:    */   public int getNumRelational()
/* 259:    */   {
/* 260:403 */     return this.m_NumRelational;
/* 261:    */   }
/* 262:    */   
/* 263:    */   public void setNumInstancesRelational(int value)
/* 264:    */   {
/* 265:412 */     this.m_NumInstancesRelational = value;
/* 266:    */   }
/* 267:    */   
/* 268:    */   public int getNumInstancesRelational()
/* 269:    */   {
/* 270:421 */     return this.m_NumInstancesRelational;
/* 271:    */   }
/* 272:    */   
/* 273:    */   protected static String[] listToArray(String value)
/* 274:    */   {
/* 275:434 */     Vector<String> list = new Vector();
/* 276:435 */     StringTokenizer tok = new StringTokenizer(value, ",");
/* 277:436 */     while (tok.hasMoreTokens()) {
/* 278:437 */       list.add(tok.nextToken());
/* 279:    */     }
/* 280:439 */     return (String[])list.toArray(new String[list.size()]);
/* 281:    */   }
/* 282:    */   
/* 283:    */   protected static String arrayToList(String[] value)
/* 284:    */   {
/* 285:452 */     String result = "";
/* 286:454 */     for (int i = 0; i < value.length; i++)
/* 287:    */     {
/* 288:455 */       if (i > 0) {
/* 289:456 */         result = result + ",";
/* 290:    */       }
/* 291:457 */       result = result + value[i];
/* 292:    */     }
/* 293:460 */     return result;
/* 294:    */   }
/* 295:    */   
/* 296:    */   public static String attributeTypeToString(int type)
/* 297:    */   {
/* 298:    */     String result;
/* 299:472 */     switch (type)
/* 300:    */     {
/* 301:    */     case 0: 
/* 302:474 */       result = "numeric";
/* 303:475 */       break;
/* 304:    */     case 1: 
/* 305:478 */       result = "nominal";
/* 306:479 */       break;
/* 307:    */     case 2: 
/* 308:482 */       result = "string";
/* 309:483 */       break;
/* 310:    */     case 3: 
/* 311:486 */       result = "date";
/* 312:487 */       break;
/* 313:    */     case 4: 
/* 314:490 */       result = "relational";
/* 315:491 */       break;
/* 316:    */     default: 
/* 317:494 */       result = "???";
/* 318:    */     }
/* 319:497 */     return result;
/* 320:    */   }
/* 321:    */   
/* 322:    */   public void setWords(String value)
/* 323:    */   {
/* 324:508 */     if (listToArray(value).length < 2) {
/* 325:509 */       throw new IllegalArgumentException("At least 2 words must be provided!");
/* 326:    */     }
/* 327:511 */     this.m_Words = listToArray(value);
/* 328:    */   }
/* 329:    */   
/* 330:    */   public String getWords()
/* 331:    */   {
/* 332:520 */     return arrayToList(this.m_Words);
/* 333:    */   }
/* 334:    */   
/* 335:    */   public void setWordSeparators(String value)
/* 336:    */   {
/* 337:529 */     this.m_WordSeparators = value;
/* 338:    */   }
/* 339:    */   
/* 340:    */   public String getWordSeparators()
/* 341:    */   {
/* 342:538 */     return this.m_WordSeparators;
/* 343:    */   }
/* 344:    */   
/* 345:    */   protected void compareDatasets(Instances data1, Instances data2)
/* 346:    */     throws Exception
/* 347:    */   {
/* 348:551 */     if (!data2.equalHeaders(data1)) {
/* 349:552 */       throw new Exception("header has been modified\n" + data2.equalHeadersMsg(data1));
/* 350:    */     }
/* 351:554 */     if (data2.numInstances() != data1.numInstances()) {
/* 352:555 */       throw new Exception("number of instances has changed");
/* 353:    */     }
/* 354:557 */     for (int i = 0; i < data2.numInstances(); i++)
/* 355:    */     {
/* 356:558 */       Instance orig = data1.instance(i);
/* 357:559 */       Instance copy = data2.instance(i);
/* 358:560 */       for (int j = 0; j < orig.numAttributes(); j++)
/* 359:    */       {
/* 360:561 */         if (orig.isMissing(j))
/* 361:    */         {
/* 362:562 */           if (!copy.isMissing(j)) {
/* 363:563 */             throw new Exception("instances have changed");
/* 364:    */           }
/* 365:    */         }
/* 366:565 */         else if (orig.value(j) != copy.value(j)) {
/* 367:566 */           throw new Exception("instances have changed");
/* 368:    */         }
/* 369:568 */         if (orig.weight() != copy.weight()) {
/* 370:569 */           throw new Exception("instance weights have changed");
/* 371:    */         }
/* 372:    */       }
/* 373:    */     }
/* 374:    */   }
/* 375:    */   
/* 376:    */   protected void addMissing(Instances data, int level, boolean predictorMissing, boolean classMissing)
/* 377:    */   {
/* 378:588 */     int classIndex = data.classIndex();
/* 379:589 */     Random random = new Random(1L);
/* 380:590 */     for (int i = 0; i < data.numInstances(); i++)
/* 381:    */     {
/* 382:591 */       Instance current = data.instance(i);
/* 383:592 */       for (int j = 0; j < data.numAttributes(); j++) {
/* 384:593 */         if (((j == classIndex) && (classMissing)) || ((j != classIndex) && (predictorMissing))) {
/* 385:595 */           if (random.nextInt(100) < level) {
/* 386:596 */             current.setMissing(j);
/* 387:    */           }
/* 388:    */         }
/* 389:    */       }
/* 390:    */     }
/* 391:    */   }
/* 392:    */   
/* 393:    */   protected Instances process(Instances data)
/* 394:    */   {
/* 395:610 */     if (getPostProcessor() == null) {
/* 396:611 */       return data;
/* 397:    */     }
/* 398:613 */     return getPostProcessor().process(data);
/* 399:    */   }
/* 400:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.CheckScheme
 * JD-Core Version:    0.7.0.1
 */