/*   1:    */ package weka.classifiers.rules;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import weka.classifiers.AbstractClassifier;
/*   6:    */ import weka.core.Attribute;
/*   7:    */ import weka.core.Capabilities;
/*   8:    */ import weka.core.Capabilities.Capability;
/*   9:    */ import weka.core.Instance;
/*  10:    */ import weka.core.Instances;
/*  11:    */ import weka.core.RevisionHandler;
/*  12:    */ import weka.core.RevisionUtils;
/*  13:    */ import weka.core.TechnicalInformation;
/*  14:    */ import weka.core.TechnicalInformation.Field;
/*  15:    */ import weka.core.TechnicalInformation.Type;
/*  16:    */ import weka.core.TechnicalInformationHandler;
/*  17:    */ import weka.core.Utils;
/*  18:    */ 
/*  19:    */ public class Prism
/*  20:    */   extends AbstractClassifier
/*  21:    */   implements TechnicalInformationHandler
/*  22:    */ {
/*  23:    */   static final long serialVersionUID = 1310258880025902106L;
/*  24:    */   private PrismRule m_rules;
/*  25:    */   
/*  26:    */   public String globalInfo()
/*  27:    */   {
/*  28: 96 */     return "Class for building and using a PRISM rule set for classification. Can only deal with nominal attributes. Can't deal with missing values. Doesn't do any pruning.\n\nFor more information, see \n\n" + getTechnicalInformation().toString();
/*  29:    */   }
/*  30:    */   
/*  31:    */   public TechnicalInformation getTechnicalInformation()
/*  32:    */   {
/*  33:113 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.ARTICLE);
/*  34:114 */     result.setValue(TechnicalInformation.Field.AUTHOR, "J. Cendrowska");
/*  35:115 */     result.setValue(TechnicalInformation.Field.YEAR, "1987");
/*  36:116 */     result.setValue(TechnicalInformation.Field.TITLE, "PRISM: An algorithm for inducing modular rules");
/*  37:    */     
/*  38:118 */     result.setValue(TechnicalInformation.Field.JOURNAL, "International Journal of Man-Machine Studies");
/*  39:    */     
/*  40:120 */     result.setValue(TechnicalInformation.Field.VOLUME, "27");
/*  41:121 */     result.setValue(TechnicalInformation.Field.NUMBER, "4");
/*  42:122 */     result.setValue(TechnicalInformation.Field.PAGES, "349-370");
/*  43:    */     
/*  44:124 */     return result;
/*  45:    */   }
/*  46:    */   
/*  47:    */   private class PrismRule
/*  48:    */     implements Serializable, RevisionHandler
/*  49:    */   {
/*  50:    */     static final long serialVersionUID = 4248784350656508583L;
/*  51:    */     private final int m_classification;
/*  52:    */     private Instances m_instances;
/*  53:    */     private Prism.Test m_test;
/*  54:    */     private int m_errors;
/*  55:    */     private PrismRule m_next;
/*  56:    */     
/*  57:    */     public PrismRule(Instances data, int cl)
/*  58:    */       throws Exception
/*  59:    */     {
/*  60:159 */       this.m_instances = data;
/*  61:160 */       this.m_classification = cl;
/*  62:161 */       this.m_test = null;
/*  63:162 */       this.m_next = null;
/*  64:163 */       this.m_errors = 0;
/*  65:164 */       Enumeration<Instance> enu = data.enumerateInstances();
/*  66:165 */       while (enu.hasMoreElements()) {
/*  67:166 */         if ((int)((Instance)enu.nextElement()).classValue() != cl) {
/*  68:167 */           this.m_errors += 1;
/*  69:    */         }
/*  70:    */       }
/*  71:170 */       this.m_instances = new Instances(this.m_instances, 0);
/*  72:    */     }
/*  73:    */     
/*  74:    */     public int resultRule(Instance inst)
/*  75:    */     {
/*  76:181 */       if ((this.m_test == null) || (this.m_test.satisfies(inst))) {
/*  77:182 */         return this.m_classification;
/*  78:    */       }
/*  79:184 */       return -1;
/*  80:    */     }
/*  81:    */     
/*  82:    */     public int resultRules(Instance inst)
/*  83:    */     {
/*  84:196 */       if (resultRule(inst) != -1) {
/*  85:197 */         return this.m_classification;
/*  86:    */       }
/*  87:198 */       if (this.m_next != null) {
/*  88:199 */         return this.m_next.resultRules(inst);
/*  89:    */       }
/*  90:201 */       return -1;
/*  91:    */     }
/*  92:    */     
/*  93:    */     public Instances coveredBy(Instances data)
/*  94:    */     {
/*  95:213 */       Instances r = new Instances(data, data.numInstances());
/*  96:214 */       Enumeration<Instance> enu = data.enumerateInstances();
/*  97:215 */       while (enu.hasMoreElements())
/*  98:    */       {
/*  99:216 */         Instance i = (Instance)enu.nextElement();
/* 100:217 */         if (resultRule(i) != -1) {
/* 101:218 */           r.add(i);
/* 102:    */         }
/* 103:    */       }
/* 104:221 */       r.compactify();
/* 105:222 */       return r;
/* 106:    */     }
/* 107:    */     
/* 108:    */     public Instances notCoveredBy(Instances data)
/* 109:    */     {
/* 110:233 */       Instances r = new Instances(data, data.numInstances());
/* 111:234 */       Enumeration<Instance> enu = data.enumerateInstances();
/* 112:235 */       while (enu.hasMoreElements())
/* 113:    */       {
/* 114:236 */         Instance i = (Instance)enu.nextElement();
/* 115:237 */         if (resultRule(i) == -1) {
/* 116:238 */           r.add(i);
/* 117:    */         }
/* 118:    */       }
/* 119:241 */       r.compactify();
/* 120:242 */       return r;
/* 121:    */     }
/* 122:    */     
/* 123:    */     public String toString()
/* 124:    */     {
/* 125:    */       try
/* 126:    */       {
/* 127:254 */         StringBuffer text = new StringBuffer();
/* 128:255 */         if (this.m_test != null)
/* 129:    */         {
/* 130:256 */           text.append("If ");
/* 131:257 */           for (Prism.Test t = this.m_test; t != null; t = t.m_next)
/* 132:    */           {
/* 133:258 */             if (t.m_attr == -1) {
/* 134:259 */               text.append("?");
/* 135:    */             } else {
/* 136:261 */               text.append(this.m_instances.attribute(t.m_attr).name() + " = " + this.m_instances.attribute(t.m_attr).value(t.m_val));
/* 137:    */             }
/* 138:264 */             if (t.m_next != null) {
/* 139:265 */               text.append("\n   and ");
/* 140:    */             }
/* 141:    */           }
/* 142:268 */           text.append(" then ");
/* 143:    */         }
/* 144:270 */         text.append(this.m_instances.classAttribute().value(this.m_classification) + "\n");
/* 145:272 */         if (this.m_next != null) {
/* 146:273 */           text.append(this.m_next.toString());
/* 147:    */         }
/* 148:275 */         return text.toString();
/* 149:    */       }
/* 150:    */       catch (Exception e) {}
/* 151:277 */       return "Can't print Prism classifier!";
/* 152:    */     }
/* 153:    */     
/* 154:    */     public String getRevision()
/* 155:    */     {
/* 156:288 */       return RevisionUtils.extract("$Revision: 10390 $");
/* 157:    */     }
/* 158:    */   }
/* 159:    */   
/* 160:    */   private class Test
/* 161:    */     implements Serializable, RevisionHandler
/* 162:    */   {
/* 163:    */     static final long serialVersionUID = -8925333011350280799L;
/* 164:301 */     private int m_attr = -1;
/* 165:    */     private int m_val;
/* 166:307 */     private Test m_next = null;
/* 167:    */     
/* 168:    */     private Test() {}
/* 169:    */     
/* 170:    */     private boolean satisfies(Instance inst)
/* 171:    */     {
/* 172:317 */       if ((int)inst.value(this.m_attr) == this.m_val)
/* 173:    */       {
/* 174:318 */         if (this.m_next == null) {
/* 175:319 */           return true;
/* 176:    */         }
/* 177:321 */         return this.m_next.satisfies(inst);
/* 178:    */       }
/* 179:324 */       return false;
/* 180:    */     }
/* 181:    */     
/* 182:    */     public String getRevision()
/* 183:    */     {
/* 184:334 */       return RevisionUtils.extract("$Revision: 10390 $");
/* 185:    */     }
/* 186:    */   }
/* 187:    */   
/* 188:    */   public double classifyInstance(Instance inst)
/* 189:    */   {
/* 190:350 */     int result = this.m_rules.resultRules(inst);
/* 191:351 */     if (result == -1) {
/* 192:352 */       return Utils.missingValue();
/* 193:    */     }
/* 194:354 */     return result;
/* 195:    */   }
/* 196:    */   
/* 197:    */   public Capabilities getCapabilities()
/* 198:    */   {
/* 199:365 */     Capabilities result = super.getCapabilities();
/* 200:366 */     result.disableAll();
/* 201:    */     
/* 202:    */ 
/* 203:369 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/* 204:    */     
/* 205:    */ 
/* 206:372 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/* 207:373 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 208:    */     
/* 209:375 */     return result;
/* 210:    */   }
/* 211:    */   
/* 212:    */   public void buildClassifier(Instances data)
/* 213:    */     throws Exception
/* 214:    */   {
/* 215:389 */     PrismRule rule = null;
/* 216:390 */     Test test = null;Test oldTest = null;
/* 217:    */     
/* 218:    */ 
/* 219:    */ 
/* 220:    */ 
/* 221:395 */     getCapabilities().testWithFail(data);
/* 222:    */     
/* 223:    */ 
/* 224:398 */     data = new Instances(data);
/* 225:399 */     data.deleteWithMissingClass();
/* 226:401 */     for (int cl = 0; cl < data.numClasses(); cl++)
/* 227:    */     {
/* 228:402 */       Instances E = data;
/* 229:403 */       while (contains(E, cl))
/* 230:    */       {
/* 231:404 */         rule = addRule(rule, new PrismRule(E, cl));
/* 232:405 */         Instances ruleE = E;
/* 233:406 */         while (rule.m_errors != 0)
/* 234:    */         {
/* 235:407 */           test = new Test(null);
/* 236:    */           int attUsed;
/* 237:    */           int bestCovers;
/* 238:408 */           int bestCorrect = bestCovers = attUsed = 0;
/* 239:    */           
/* 240:    */ 
/* 241:411 */           Enumeration<Attribute> enumAtt = ruleE.enumerateAttributes();
/* 242:412 */           while (enumAtt.hasMoreElements())
/* 243:    */           {
/* 244:413 */             Attribute attr = (Attribute)enumAtt.nextElement();
/* 245:414 */             if (isMentionedIn(attr, rule.m_test))
/* 246:    */             {
/* 247:415 */               attUsed++;
/* 248:    */             }
/* 249:    */             else
/* 250:    */             {
/* 251:418 */               int M = attr.numValues();
/* 252:419 */               int[] covers = new int[M];
/* 253:420 */               int[] correct = new int[M];
/* 254:421 */               for (int j = 0; j < M; j++)
/* 255:    */               {
/* 256:422 */                 int tmp185_184 = 0;correct[j] = tmp185_184;covers[j] = tmp185_184;
/* 257:    */               }
/* 258:426 */               Enumeration<Instance> enu = ruleE.enumerateInstances();
/* 259:427 */               while (enu.hasMoreElements())
/* 260:    */               {
/* 261:428 */                 Instance i = (Instance)enu.nextElement();
/* 262:429 */                 covers[((int)i.value(attr))] += 1;
/* 263:430 */                 if ((int)i.classValue() == cl) {
/* 264:431 */                   correct[((int)i.value(attr))] += 1;
/* 265:    */                 }
/* 266:    */               }
/* 267:436 */               for (int val = 0; val < M; val++)
/* 268:    */               {
/* 269:437 */                 int diff = correct[val] * bestCovers - bestCorrect * covers[val];
/* 270:440 */                 if ((test.m_attr == -1) || (diff > 0) || ((diff == 0) && (correct[val] > bestCorrect)))
/* 271:    */                 {
/* 272:444 */                   bestCorrect = correct[val];
/* 273:445 */                   bestCovers = covers[val];
/* 274:446 */                   test.m_attr = attr.index();
/* 275:447 */                   test.m_val = val;
/* 276:448 */                   rule.m_errors = (bestCovers - bestCorrect);
/* 277:    */                 }
/* 278:    */               }
/* 279:    */             }
/* 280:    */           }
/* 281:452 */           if (test.m_attr != -1)
/* 282:    */           {
/* 283:455 */             oldTest = addTest(rule, oldTest, test);
/* 284:456 */             ruleE = rule.coveredBy(ruleE);
/* 285:457 */             if (attUsed == data.numAttributes() - 1) {
/* 286:    */               break;
/* 287:    */             }
/* 288:    */           }
/* 289:    */         }
/* 290:461 */         E = rule.notCoveredBy(E);
/* 291:    */       }
/* 292:    */     }
/* 293:    */   }
/* 294:    */   
/* 295:    */   private PrismRule addRule(PrismRule lastRule, PrismRule newRule)
/* 296:    */   {
/* 297:475 */     if (lastRule == null) {
/* 298:476 */       this.m_rules = newRule;
/* 299:    */     } else {
/* 300:478 */       lastRule.m_next = newRule;
/* 301:    */     }
/* 302:480 */     return newRule;
/* 303:    */   }
/* 304:    */   
/* 305:    */   private Test addTest(PrismRule rule, Test lastTest, Test newTest)
/* 306:    */   {
/* 307:493 */     if (rule.m_test == null) {
/* 308:494 */       rule.m_test = newTest;
/* 309:    */     } else {
/* 310:496 */       lastTest.m_next = newTest;
/* 311:    */     }
/* 312:498 */     return newTest;
/* 313:    */   }
/* 314:    */   
/* 315:    */   private static boolean contains(Instances E, int C)
/* 316:    */     throws Exception
/* 317:    */   {
/* 318:511 */     Enumeration<Instance> enu = E.enumerateInstances();
/* 319:512 */     while (enu.hasMoreElements()) {
/* 320:513 */       if ((int)((Instance)enu.nextElement()).classValue() == C) {
/* 321:514 */         return true;
/* 322:    */       }
/* 323:    */     }
/* 324:517 */     return false;
/* 325:    */   }
/* 326:    */   
/* 327:    */   private static boolean isMentionedIn(Attribute attr, Test t)
/* 328:    */   {
/* 329:529 */     if (t == null) {
/* 330:530 */       return false;
/* 331:    */     }
/* 332:532 */     if (t.m_attr == attr.index()) {
/* 333:533 */       return true;
/* 334:    */     }
/* 335:535 */     return isMentionedIn(attr, t.m_next);
/* 336:    */   }
/* 337:    */   
/* 338:    */   public String toString()
/* 339:    */   {
/* 340:546 */     if (this.m_rules == null) {
/* 341:547 */       return "Prism: No model built yet.";
/* 342:    */     }
/* 343:549 */     return "Prism rules\n----------\n" + this.m_rules.toString();
/* 344:    */   }
/* 345:    */   
/* 346:    */   public String getRevision()
/* 347:    */   {
/* 348:559 */     return RevisionUtils.extract("$Revision: 10390 $");
/* 349:    */   }
/* 350:    */   
/* 351:    */   public static void main(String[] args)
/* 352:    */   {
/* 353:568 */     runClassifier(new Prism(), args);
/* 354:    */   }
/* 355:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.rules.Prism
 * JD-Core Version:    0.7.0.1
 */