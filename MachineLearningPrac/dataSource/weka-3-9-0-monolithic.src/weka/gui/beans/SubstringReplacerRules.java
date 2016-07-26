/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.HashSet;
/*   6:    */ import java.util.List;
/*   7:    */ import java.util.Set;
/*   8:    */ import java.util.regex.Matcher;
/*   9:    */ import java.util.regex.Pattern;
/*  10:    */ import weka.core.Attribute;
/*  11:    */ import weka.core.DenseInstance;
/*  12:    */ import weka.core.Environment;
/*  13:    */ import weka.core.EnvironmentHandler;
/*  14:    */ import weka.core.Instance;
/*  15:    */ import weka.core.Instances;
/*  16:    */ import weka.core.Range;
/*  17:    */ import weka.gui.Logger;
/*  18:    */ 
/*  19:    */ public class SubstringReplacerRules
/*  20:    */   implements EnvironmentHandler, Serializable
/*  21:    */ {
/*  22:    */   private static final long serialVersionUID = -7151320452496749698L;
/*  23: 51 */   protected transient Environment m_env = Environment.getSystemWide();
/*  24:    */   protected List<SubstringReplacerMatchRule> m_matchRules;
/*  25:    */   protected Instances m_inputStructure;
/*  26:    */   protected Instances m_outputStructure;
/*  27: 62 */   protected String m_statusMessagePrefix = "";
/*  28:    */   
/*  29:    */   public void setEnvironment(Environment env)
/*  30:    */   {
/*  31: 66 */     this.m_env = env;
/*  32:    */   }
/*  33:    */   
/*  34:    */   public SubstringReplacerRules(String matchDetails, Instances inputStructure, String statusMessagePrefix, Logger log, Environment env)
/*  35:    */   {
/*  36: 82 */     this.m_matchRules = matchRulesFromInternal(matchDetails, inputStructure, statusMessagePrefix, log, env);
/*  37:    */     
/*  38:    */ 
/*  39:    */ 
/*  40: 86 */     this.m_inputStructure = new Instances(inputStructure);
/*  41: 87 */     this.m_outputStructure = new Instances(inputStructure).stringFreeStructure();
/*  42: 88 */     this.m_env = env;
/*  43: 89 */     this.m_statusMessagePrefix = statusMessagePrefix;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public SubstringReplacerRules(String matchDetails, Instances inputStructure)
/*  47:    */   {
/*  48:101 */     this(matchDetails, inputStructure, "", null, Environment.getSystemWide());
/*  49:    */   }
/*  50:    */   
/*  51:    */   public static List<SubstringReplacerMatchRule> matchRulesFromInternal(String matchReplaceDetails, Instances inputStructure, String statusMessagePrefix, Logger log, Environment env)
/*  52:    */   {
/*  53:119 */     List<SubstringReplacerMatchRule> matchRules = new ArrayList();
/*  54:    */     
/*  55:    */ 
/*  56:122 */     String[] mrParts = matchReplaceDetails.split("@@match-replace@@");
/*  57:123 */     for (String p : mrParts)
/*  58:    */     {
/*  59:124 */       SubstringReplacerMatchRule mr = new SubstringReplacerMatchRule(p.trim());
/*  60:125 */       mr.m_statusMessagePrefix = statusMessagePrefix;
/*  61:126 */       mr.m_logger = log;
/*  62:127 */       mr.init(env, inputStructure);
/*  63:128 */       matchRules.add(mr);
/*  64:    */     }
/*  65:131 */     return matchRules;
/*  66:    */   }
/*  67:    */   
/*  68:    */   public void applyRules(Instance inst)
/*  69:    */   {
/*  70:135 */     for (SubstringReplacerMatchRule mr : this.m_matchRules) {
/*  71:136 */       mr.apply(inst);
/*  72:    */     }
/*  73:    */   }
/*  74:    */   
/*  75:    */   public Instance makeOutputInstance(Instance inputI)
/*  76:    */   {
/*  77:147 */     double[] vals = new double[this.m_outputStructure.numAttributes()];
/*  78:148 */     String[] stringVals = new String[this.m_outputStructure.numAttributes()];
/*  79:149 */     for (int i = 0; i < inputI.numAttributes(); i++) {
/*  80:150 */       if ((inputI.attribute(i).isString()) && (!inputI.isMissing(i))) {
/*  81:151 */         stringVals[i] = inputI.stringValue(i);
/*  82:    */       } else {
/*  83:153 */         vals[i] = inputI.value(i);
/*  84:    */       }
/*  85:    */     }
/*  86:157 */     for (SubstringReplacerMatchRule mr : this.m_matchRules) {
/*  87:158 */       mr.apply(stringVals);
/*  88:    */     }
/*  89:161 */     for (int i = 0; i < this.m_outputStructure.numAttributes(); i++) {
/*  90:162 */       if ((this.m_outputStructure.attribute(i).isString()) && (stringVals[i] != null)) {
/*  91:163 */         this.m_outputStructure.attribute(i).setStringValue(stringVals[i]);
/*  92:    */       }
/*  93:    */     }
/*  94:167 */     Instance result = new DenseInstance(inputI.weight(), vals);
/*  95:168 */     result.setDataset(this.m_outputStructure);
/*  96:169 */     return result;
/*  97:    */   }
/*  98:    */   
/*  99:    */   public static class SubstringReplacerMatchRule
/* 100:    */     implements Serializable
/* 101:    */   {
/* 102:    */     private static final long serialVersionUID = 5792838913737819728L;
/* 103:182 */     protected String m_match = "";
/* 104:185 */     protected String m_replace = "";
/* 105:    */     protected boolean m_regex;
/* 106:    */     protected Pattern m_regexPattern;
/* 107:    */     protected boolean m_ignoreCase;
/* 108:197 */     protected String m_attsToApplyTo = "";
/* 109:    */     protected String m_matchS;
/* 110:    */     protected String m_replaceS;
/* 111:    */     protected int[] m_selectedAtts;
/* 112:    */     protected String m_statusMessagePrefix;
/* 113:    */     protected Logger m_logger;
/* 114:    */     
/* 115:    */     public SubstringReplacerMatchRule() {}
/* 116:    */     
/* 117:    */     public SubstringReplacerMatchRule(String setup)
/* 118:    */     {
/* 119:220 */       parseFromInternal(setup);
/* 120:    */     }
/* 121:    */     
/* 122:    */     public SubstringReplacerMatchRule(String match, String replace, boolean regex, boolean ignoreCase, String selectedAtts)
/* 123:    */     {
/* 124:234 */       this.m_match = match;
/* 125:235 */       this.m_replace = replace;
/* 126:236 */       this.m_regex = regex;
/* 127:237 */       this.m_ignoreCase = ignoreCase;
/* 128:238 */       this.m_attsToApplyTo = selectedAtts;
/* 129:    */     }
/* 130:    */     
/* 131:    */     protected void parseFromInternal(String setup)
/* 132:    */     {
/* 133:242 */       String[] parts = setup.split("@@MR@@");
/* 134:243 */       if ((parts.length < 4) || (parts.length > 5)) {
/* 135:244 */         throw new IllegalArgumentException("Malformed match-replace definition: " + setup);
/* 136:    */       }
/* 137:248 */       this.m_attsToApplyTo = parts[0].trim();
/* 138:249 */       this.m_regex = parts[1].trim().toLowerCase().equals("t");
/* 139:250 */       this.m_ignoreCase = parts[2].trim().toLowerCase().equals("t");
/* 140:251 */       this.m_match = parts[3].trim();
/* 141:253 */       if ((this.m_match == null) || (this.m_match.length() == 0)) {
/* 142:254 */         throw new IllegalArgumentException("Must provide something to match!");
/* 143:    */       }
/* 144:257 */       if (parts.length == 5) {
/* 145:258 */         this.m_replace = parts[4];
/* 146:    */       }
/* 147:    */     }
/* 148:    */     
/* 149:    */     public void setMatch(String match)
/* 150:    */     {
/* 151:268 */       this.m_match = match;
/* 152:    */     }
/* 153:    */     
/* 154:    */     public String getMatch()
/* 155:    */     {
/* 156:277 */       return this.m_match;
/* 157:    */     }
/* 158:    */     
/* 159:    */     public void setReplace(String replace)
/* 160:    */     {
/* 161:286 */       this.m_replace = replace;
/* 162:    */     }
/* 163:    */     
/* 164:    */     public String getReplace()
/* 165:    */     {
/* 166:295 */       return this.m_replace;
/* 167:    */     }
/* 168:    */     
/* 169:    */     public void setRegex(boolean regex)
/* 170:    */     {
/* 171:304 */       this.m_regex = regex;
/* 172:    */     }
/* 173:    */     
/* 174:    */     public boolean getRegex()
/* 175:    */     {
/* 176:313 */       return this.m_regex;
/* 177:    */     }
/* 178:    */     
/* 179:    */     public void setIgnoreCase(boolean ignore)
/* 180:    */     {
/* 181:322 */       this.m_ignoreCase = ignore;
/* 182:    */     }
/* 183:    */     
/* 184:    */     public boolean getIgnoreCase()
/* 185:    */     {
/* 186:331 */       return this.m_ignoreCase;
/* 187:    */     }
/* 188:    */     
/* 189:    */     public void setAttsToApplyTo(String a)
/* 190:    */     {
/* 191:340 */       this.m_attsToApplyTo = a;
/* 192:    */     }
/* 193:    */     
/* 194:    */     public String getAttsToApplyTo()
/* 195:    */     {
/* 196:349 */       return this.m_attsToApplyTo;
/* 197:    */     }
/* 198:    */     
/* 199:    */     public void init(Environment env, Instances structure)
/* 200:    */     {
/* 201:362 */       this.m_matchS = this.m_match;
/* 202:363 */       this.m_replaceS = this.m_replace;
/* 203:364 */       String attsToApplyToS = this.m_attsToApplyTo;
/* 204:    */       try
/* 205:    */       {
/* 206:367 */         this.m_matchS = env.substitute(this.m_matchS);
/* 207:368 */         this.m_replaceS = env.substitute(this.m_replace);
/* 208:369 */         attsToApplyToS = env.substitute(attsToApplyToS);
/* 209:    */       }
/* 210:    */       catch (Exception ex) {}
/* 211:373 */       if (this.m_regex)
/* 212:    */       {
/* 213:374 */         String match = this.m_matchS;
/* 214:375 */         if (this.m_ignoreCase) {
/* 215:376 */           match = match.toLowerCase();
/* 216:    */         }
/* 217:380 */         this.m_regexPattern = Pattern.compile(match);
/* 218:    */       }
/* 219:384 */       String tempRangeS = attsToApplyToS;
/* 220:385 */       tempRangeS = tempRangeS.replace("/first", "first").replace("/last", "last");
/* 221:    */       
/* 222:387 */       Range tempR = new Range();
/* 223:388 */       tempR.setRanges(attsToApplyToS);
/* 224:    */       try
/* 225:    */       {
/* 226:390 */         tempR.setUpper(structure.numAttributes() - 1);
/* 227:391 */         this.m_selectedAtts = tempR.getSelection();
/* 228:    */       }
/* 229:    */       catch (IllegalArgumentException ex)
/* 230:    */       {
/* 231:394 */         this.m_selectedAtts = null;
/* 232:    */       }
/* 233:    */       int c;
/* 234:397 */       if (this.m_selectedAtts == null)
/* 235:    */       {
/* 236:399 */         Set<Integer> indexes = new HashSet();
/* 237:400 */         String[] attParts = this.m_attsToApplyTo.split(",");
/* 238:401 */         for (String att : attParts)
/* 239:    */         {
/* 240:402 */           att = att.trim();
/* 241:403 */           if (att.toLowerCase().equals("/first"))
/* 242:    */           {
/* 243:404 */             indexes.add(Integer.valueOf(0));
/* 244:    */           }
/* 245:405 */           else if (att.toLowerCase().equals("/last"))
/* 246:    */           {
/* 247:406 */             indexes.add(Integer.valueOf(structure.numAttributes() - 1));
/* 248:    */           }
/* 249:409 */           else if (structure.attribute(att) != null)
/* 250:    */           {
/* 251:410 */             indexes.add(new Integer(structure.attribute(att).index()));
/* 252:    */           }
/* 253:412 */           else if (this.m_logger != null)
/* 254:    */           {
/* 255:413 */             String msg = this.m_statusMessagePrefix + "Can't find attribute '" + att + "in the incoming instances - ignoring";
/* 256:    */             
/* 257:    */ 
/* 258:416 */             this.m_logger.logMessage(msg);
/* 259:    */           }
/* 260:    */         }
/* 261:422 */         this.m_selectedAtts = new int[indexes.size()];
/* 262:423 */         c = 0;
/* 263:424 */         for (Integer i : indexes) {
/* 264:425 */           this.m_selectedAtts[(c++)] = i.intValue();
/* 265:    */         }
/* 266:    */       }
/* 267:430 */       Set<Integer> indexes = new HashSet();
/* 268:431 */       for (int m_selectedAtt : this.m_selectedAtts) {
/* 269:432 */         if (structure.attribute(m_selectedAtt).isString())
/* 270:    */         {
/* 271:433 */           indexes.add(Integer.valueOf(m_selectedAtt));
/* 272:    */         }
/* 273:435 */         else if (this.m_logger != null)
/* 274:    */         {
/* 275:436 */           String msg = this.m_statusMessagePrefix + "Attribute '" + structure.attribute(m_selectedAtt).name() + "is not a string attribute - " + "ignoring";
/* 276:    */           
/* 277:    */ 
/* 278:    */ 
/* 279:440 */           this.m_logger.logMessage(msg);
/* 280:    */         }
/* 281:    */       }
/* 282:446 */       this.m_selectedAtts = new int[indexes.size()];
/* 283:447 */       int c = 0;
/* 284:448 */       for (Integer i : indexes) {
/* 285:449 */         this.m_selectedAtts[(c++)] = i.intValue();
/* 286:    */       }
/* 287:    */     }
/* 288:    */     
/* 289:    */     public void apply(Instance inst)
/* 290:    */     {
/* 291:460 */       for (int i = 0; i < this.m_selectedAtts.length; i++)
/* 292:    */       {
/* 293:461 */         int numStringVals = inst.attribute(this.m_selectedAtts[i]).numValues();
/* 294:462 */         if (!inst.isMissing(this.m_selectedAtts[i]))
/* 295:    */         {
/* 296:463 */           String value = inst.stringValue(this.m_selectedAtts[i]);
/* 297:464 */           value = apply(value);
/* 298:465 */           inst.dataset().attribute(this.m_selectedAtts[i]).setStringValue(value);
/* 299:477 */           if (numStringVals > 1) {
/* 300:478 */             inst.setValue(this.m_selectedAtts[i], 0.0D);
/* 301:    */           }
/* 302:    */         }
/* 303:    */       }
/* 304:    */     }
/* 305:    */     
/* 306:    */     public void apply(String[] stringVals)
/* 307:    */     {
/* 308:494 */       for (int i = 0; i < this.m_selectedAtts.length; i++) {
/* 309:495 */         if (stringVals[this.m_selectedAtts[i]] != null) {
/* 310:496 */           stringVals[this.m_selectedAtts[i]] = apply(stringVals[this.m_selectedAtts[i]]);
/* 311:    */         }
/* 312:    */       }
/* 313:    */     }
/* 314:    */     
/* 315:    */     protected String apply(String source)
/* 316:    */     {
/* 317:508 */       String result = source;
/* 318:509 */       String match = this.m_matchS;
/* 319:510 */       if (this.m_ignoreCase)
/* 320:    */       {
/* 321:511 */         result = result.toLowerCase();
/* 322:512 */         match = match.toLowerCase();
/* 323:    */       }
/* 324:514 */       if ((result != null) && (result.length() > 0)) {
/* 325:515 */         if (this.m_regex) {
/* 326:517 */           result = this.m_regexPattern.matcher(result).replaceAll(this.m_replaceS);
/* 327:    */         } else {
/* 328:519 */           result = result.replace(match, this.m_replaceS);
/* 329:    */         }
/* 330:    */       }
/* 331:523 */       return result;
/* 332:    */     }
/* 333:    */     
/* 334:    */     public String toString()
/* 335:    */     {
/* 336:536 */       StringBuffer buff = new StringBuffer();
/* 337:537 */       buff.append(this.m_regex ? "Regex: " : "Substring: ");
/* 338:538 */       buff.append(this.m_match).append(" --> ").append(this.m_replace).append("  ");
/* 339:539 */       buff.append(this.m_ignoreCase ? "[ignore case]" : "").append("  ");
/* 340:540 */       buff.append("[Atts: " + this.m_attsToApplyTo + "]");
/* 341:    */       
/* 342:542 */       return buff.toString();
/* 343:    */     }
/* 344:    */     
/* 345:    */     public String toStringInternal()
/* 346:    */     {
/* 347:554 */       StringBuffer buff = new StringBuffer();
/* 348:555 */       buff.append(this.m_attsToApplyTo).append("@@MR@@");
/* 349:556 */       buff.append(this.m_regex ? "t" : "f").append("@@MR@@");
/* 350:557 */       buff.append(this.m_ignoreCase ? "t" : "f").append("@@MR@@");
/* 351:558 */       buff.append(this.m_match).append("@@MR@@");
/* 352:559 */       buff.append(this.m_replace);
/* 353:    */       
/* 354:561 */       return buff.toString();
/* 355:    */     }
/* 356:    */   }
/* 357:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.SubstringReplacerRules
 * JD-Core Version:    0.7.0.1
 */