/*   1:    */ package weka.core.stemmers;
/*   2:    */ 
/*   3:    */ import java.util.HashMap;
/*   4:    */ import weka.core.RevisionUtils;
/*   5:    */ import weka.core.TechnicalInformation;
/*   6:    */ import weka.core.TechnicalInformation.Field;
/*   7:    */ import weka.core.TechnicalInformation.Type;
/*   8:    */ import weka.core.TechnicalInformationHandler;
/*   9:    */ 
/*  10:    */ public class LovinsStemmer
/*  11:    */   implements Stemmer, TechnicalInformationHandler
/*  12:    */ {
/*  13:    */   static final long serialVersionUID = -6113024782588197L;
/*  14: 67 */   private static boolean m_CompMode = false;
/*  15: 70 */   private static HashMap<String, String> m_l11 = null;
/*  16: 71 */   private static HashMap<String, String> m_l10 = null;
/*  17: 72 */   private static HashMap<String, String> m_l9 = null;
/*  18: 73 */   private static HashMap<String, String> m_l8 = null;
/*  19: 74 */   private static HashMap<String, String> m_l7 = null;
/*  20: 75 */   private static HashMap<String, String> m_l6 = null;
/*  21: 76 */   private static HashMap<String, String> m_l5 = null;
/*  22: 77 */   private static HashMap<String, String> m_l4 = null;
/*  23: 78 */   private static HashMap<String, String> m_l3 = null;
/*  24: 79 */   private static HashMap<String, String> m_l2 = null;
/*  25: 80 */   private static HashMap<String, String> m_l1 = null;
/*  26:    */   
/*  27:    */   static
/*  28:    */   {
/*  29: 84 */     m_l11 = new HashMap();
/*  30: 85 */     m_l11.put("alistically", "B");
/*  31: 86 */     m_l11.put("arizability", "A");
/*  32: 87 */     m_l11.put("izationally", "B");
/*  33: 88 */     m_l10 = new HashMap();
/*  34: 89 */     m_l10.put("antialness", "A");
/*  35: 90 */     m_l10.put("arisations", "A");
/*  36: 91 */     m_l10.put("arizations", "A");
/*  37: 92 */     m_l10.put("entialness", "A");
/*  38: 93 */     m_l9 = new HashMap();
/*  39: 94 */     m_l9.put("allically", "C");
/*  40: 95 */     m_l9.put("antaneous", "A");
/*  41: 96 */     m_l9.put("antiality", "A");
/*  42: 97 */     m_l9.put("arisation", "A");
/*  43: 98 */     m_l9.put("arization", "A");
/*  44: 99 */     m_l9.put("ationally", "B");
/*  45:100 */     m_l9.put("ativeness", "A");
/*  46:101 */     m_l9.put("eableness", "E");
/*  47:102 */     m_l9.put("entations", "A");
/*  48:103 */     m_l9.put("entiality", "A");
/*  49:104 */     m_l9.put("entialize", "A");
/*  50:105 */     m_l9.put("entiation", "A");
/*  51:106 */     m_l9.put("ionalness", "A");
/*  52:107 */     m_l9.put("istically", "A");
/*  53:108 */     m_l9.put("itousness", "A");
/*  54:109 */     m_l9.put("izability", "A");
/*  55:110 */     m_l9.put("izational", "A");
/*  56:111 */     m_l8 = new HashMap();
/*  57:112 */     m_l8.put("ableness", "A");
/*  58:113 */     m_l8.put("arizable", "A");
/*  59:114 */     m_l8.put("entation", "A");
/*  60:115 */     m_l8.put("entially", "A");
/*  61:116 */     m_l8.put("eousness", "A");
/*  62:117 */     m_l8.put("ibleness", "A");
/*  63:118 */     m_l8.put("icalness", "A");
/*  64:119 */     m_l8.put("ionalism", "A");
/*  65:120 */     m_l8.put("ionality", "A");
/*  66:121 */     m_l8.put("ionalize", "A");
/*  67:122 */     m_l8.put("iousness", "A");
/*  68:123 */     m_l8.put("izations", "A");
/*  69:124 */     m_l8.put("lessness", "A");
/*  70:125 */     m_l7 = new HashMap();
/*  71:126 */     m_l7.put("ability", "A");
/*  72:127 */     m_l7.put("aically", "A");
/*  73:128 */     m_l7.put("alistic", "B");
/*  74:129 */     m_l7.put("alities", "A");
/*  75:130 */     m_l7.put("ariness", "E");
/*  76:131 */     m_l7.put("aristic", "A");
/*  77:132 */     m_l7.put("arizing", "A");
/*  78:133 */     m_l7.put("ateness", "A");
/*  79:134 */     m_l7.put("atingly", "A");
/*  80:135 */     m_l7.put("ational", "B");
/*  81:136 */     m_l7.put("atively", "A");
/*  82:137 */     m_l7.put("ativism", "A");
/*  83:138 */     m_l7.put("elihood", "E");
/*  84:139 */     m_l7.put("encible", "A");
/*  85:140 */     m_l7.put("entally", "A");
/*  86:141 */     m_l7.put("entials", "A");
/*  87:142 */     m_l7.put("entiate", "A");
/*  88:143 */     m_l7.put("entness", "A");
/*  89:144 */     m_l7.put("fulness", "A");
/*  90:145 */     m_l7.put("ibility", "A");
/*  91:146 */     m_l7.put("icalism", "A");
/*  92:147 */     m_l7.put("icalist", "A");
/*  93:148 */     m_l7.put("icality", "A");
/*  94:149 */     m_l7.put("icalize", "A");
/*  95:150 */     m_l7.put("ication", "G");
/*  96:151 */     m_l7.put("icianry", "A");
/*  97:152 */     m_l7.put("ination", "A");
/*  98:153 */     m_l7.put("ingness", "A");
/*  99:154 */     m_l7.put("ionally", "A");
/* 100:155 */     m_l7.put("isation", "A");
/* 101:156 */     m_l7.put("ishness", "A");
/* 102:157 */     m_l7.put("istical", "A");
/* 103:158 */     m_l7.put("iteness", "A");
/* 104:159 */     m_l7.put("iveness", "A");
/* 105:160 */     m_l7.put("ivistic", "A");
/* 106:161 */     m_l7.put("ivities", "A");
/* 107:162 */     m_l7.put("ization", "F");
/* 108:163 */     m_l7.put("izement", "A");
/* 109:164 */     m_l7.put("oidally", "A");
/* 110:165 */     m_l7.put("ousness", "A");
/* 111:166 */     m_l6 = new HashMap();
/* 112:167 */     m_l6.put("aceous", "A");
/* 113:168 */     m_l6.put("acious", "B");
/* 114:169 */     m_l6.put("action", "G");
/* 115:170 */     m_l6.put("alness", "A");
/* 116:171 */     m_l6.put("ancial", "A");
/* 117:172 */     m_l6.put("ancies", "A");
/* 118:173 */     m_l6.put("ancing", "B");
/* 119:174 */     m_l6.put("ariser", "A");
/* 120:175 */     m_l6.put("arized", "A");
/* 121:176 */     m_l6.put("arizer", "A");
/* 122:177 */     m_l6.put("atable", "A");
/* 123:178 */     m_l6.put("ations", "B");
/* 124:179 */     m_l6.put("atives", "A");
/* 125:180 */     m_l6.put("eature", "Z");
/* 126:181 */     m_l6.put("efully", "A");
/* 127:182 */     m_l6.put("encies", "A");
/* 128:183 */     m_l6.put("encing", "A");
/* 129:184 */     m_l6.put("ential", "A");
/* 130:185 */     m_l6.put("enting", "C");
/* 131:186 */     m_l6.put("entist", "A");
/* 132:187 */     m_l6.put("eously", "A");
/* 133:188 */     m_l6.put("ialist", "A");
/* 134:189 */     m_l6.put("iality", "A");
/* 135:190 */     m_l6.put("ialize", "A");
/* 136:191 */     m_l6.put("ically", "A");
/* 137:192 */     m_l6.put("icance", "A");
/* 138:193 */     m_l6.put("icians", "A");
/* 139:194 */     m_l6.put("icists", "A");
/* 140:195 */     m_l6.put("ifully", "A");
/* 141:196 */     m_l6.put("ionals", "A");
/* 142:197 */     m_l6.put("ionate", "D");
/* 143:198 */     m_l6.put("ioning", "A");
/* 144:199 */     m_l6.put("ionist", "A");
/* 145:200 */     m_l6.put("iously", "A");
/* 146:201 */     m_l6.put("istics", "A");
/* 147:202 */     m_l6.put("izable", "E");
/* 148:203 */     m_l6.put("lessly", "A");
/* 149:204 */     m_l6.put("nesses", "A");
/* 150:205 */     m_l6.put("oidism", "A");
/* 151:206 */     m_l5 = new HashMap();
/* 152:207 */     m_l5.put("acies", "A");
/* 153:208 */     m_l5.put("acity", "A");
/* 154:209 */     m_l5.put("aging", "B");
/* 155:210 */     m_l5.put("aical", "A");
/* 156:211 */     if (!m_CompMode) {
/* 157:212 */       m_l5.put("alist", "A");
/* 158:    */     }
/* 159:214 */     m_l5.put("alism", "B");
/* 160:215 */     m_l5.put("ality", "A");
/* 161:216 */     m_l5.put("alize", "A");
/* 162:217 */     m_l5.put("allic", "b");
/* 163:218 */     m_l5.put("anced", "B");
/* 164:219 */     m_l5.put("ances", "B");
/* 165:220 */     m_l5.put("antic", "C");
/* 166:221 */     m_l5.put("arial", "A");
/* 167:222 */     m_l5.put("aries", "A");
/* 168:223 */     m_l5.put("arily", "A");
/* 169:224 */     m_l5.put("arity", "B");
/* 170:225 */     m_l5.put("arize", "A");
/* 171:226 */     m_l5.put("aroid", "A");
/* 172:227 */     m_l5.put("ately", "A");
/* 173:228 */     m_l5.put("ating", "I");
/* 174:229 */     m_l5.put("ation", "B");
/* 175:230 */     m_l5.put("ative", "A");
/* 176:231 */     m_l5.put("ators", "A");
/* 177:232 */     m_l5.put("atory", "A");
/* 178:233 */     m_l5.put("ature", "E");
/* 179:234 */     m_l5.put("early", "Y");
/* 180:235 */     m_l5.put("ehood", "A");
/* 181:236 */     m_l5.put("eless", "A");
/* 182:237 */     if (!m_CompMode) {
/* 183:238 */       m_l5.put("elily", "A");
/* 184:    */     } else {
/* 185:240 */       m_l5.put("elity", "A");
/* 186:    */     }
/* 187:242 */     m_l5.put("ement", "A");
/* 188:243 */     m_l5.put("enced", "A");
/* 189:244 */     m_l5.put("ences", "A");
/* 190:245 */     m_l5.put("eness", "E");
/* 191:246 */     m_l5.put("ening", "E");
/* 192:247 */     m_l5.put("ental", "A");
/* 193:248 */     m_l5.put("ented", "C");
/* 194:249 */     m_l5.put("ently", "A");
/* 195:250 */     m_l5.put("fully", "A");
/* 196:251 */     m_l5.put("ially", "A");
/* 197:252 */     m_l5.put("icant", "A");
/* 198:253 */     m_l5.put("ician", "A");
/* 199:254 */     m_l5.put("icide", "A");
/* 200:255 */     m_l5.put("icism", "A");
/* 201:256 */     m_l5.put("icist", "A");
/* 202:257 */     m_l5.put("icity", "A");
/* 203:258 */     m_l5.put("idine", "I");
/* 204:259 */     m_l5.put("iedly", "A");
/* 205:260 */     m_l5.put("ihood", "A");
/* 206:261 */     m_l5.put("inate", "A");
/* 207:262 */     m_l5.put("iness", "A");
/* 208:263 */     m_l5.put("ingly", "B");
/* 209:264 */     m_l5.put("inism", "J");
/* 210:265 */     m_l5.put("inity", "c");
/* 211:266 */     m_l5.put("ional", "A");
/* 212:267 */     m_l5.put("ioned", "A");
/* 213:268 */     m_l5.put("ished", "A");
/* 214:269 */     m_l5.put("istic", "A");
/* 215:270 */     m_l5.put("ities", "A");
/* 216:271 */     m_l5.put("itous", "A");
/* 217:272 */     m_l5.put("ively", "A");
/* 218:273 */     m_l5.put("ivity", "A");
/* 219:274 */     m_l5.put("izers", "F");
/* 220:275 */     m_l5.put("izing", "F");
/* 221:276 */     m_l5.put("oidal", "A");
/* 222:277 */     m_l5.put("oides", "A");
/* 223:278 */     m_l5.put("otide", "A");
/* 224:279 */     m_l5.put("ously", "A");
/* 225:280 */     m_l4 = new HashMap();
/* 226:281 */     m_l4.put("able", "A");
/* 227:282 */     m_l4.put("ably", "A");
/* 228:283 */     m_l4.put("ages", "B");
/* 229:284 */     m_l4.put("ally", "B");
/* 230:285 */     m_l4.put("ance", "B");
/* 231:286 */     m_l4.put("ancy", "B");
/* 232:287 */     m_l4.put("ants", "B");
/* 233:288 */     m_l4.put("aric", "A");
/* 234:289 */     m_l4.put("arly", "K");
/* 235:290 */     m_l4.put("ated", "I");
/* 236:291 */     m_l4.put("ates", "A");
/* 237:292 */     m_l4.put("atic", "B");
/* 238:293 */     m_l4.put("ator", "A");
/* 239:294 */     m_l4.put("ealy", "Y");
/* 240:295 */     m_l4.put("edly", "E");
/* 241:296 */     m_l4.put("eful", "A");
/* 242:297 */     m_l4.put("eity", "A");
/* 243:298 */     m_l4.put("ence", "A");
/* 244:299 */     m_l4.put("ency", "A");
/* 245:300 */     m_l4.put("ened", "E");
/* 246:301 */     m_l4.put("enly", "E");
/* 247:302 */     m_l4.put("eous", "A");
/* 248:303 */     m_l4.put("hood", "A");
/* 249:304 */     m_l4.put("ials", "A");
/* 250:305 */     m_l4.put("ians", "A");
/* 251:306 */     m_l4.put("ible", "A");
/* 252:307 */     m_l4.put("ibly", "A");
/* 253:308 */     m_l4.put("ical", "A");
/* 254:309 */     m_l4.put("ides", "L");
/* 255:310 */     m_l4.put("iers", "A");
/* 256:311 */     m_l4.put("iful", "A");
/* 257:312 */     m_l4.put("ines", "M");
/* 258:313 */     m_l4.put("ings", "N");
/* 259:314 */     m_l4.put("ions", "B");
/* 260:315 */     m_l4.put("ious", "A");
/* 261:316 */     m_l4.put("isms", "B");
/* 262:317 */     m_l4.put("ists", "A");
/* 263:318 */     m_l4.put("itic", "H");
/* 264:319 */     m_l4.put("ized", "F");
/* 265:320 */     m_l4.put("izer", "F");
/* 266:321 */     m_l4.put("less", "A");
/* 267:322 */     m_l4.put("lily", "A");
/* 268:323 */     m_l4.put("ness", "A");
/* 269:324 */     m_l4.put("ogen", "A");
/* 270:325 */     m_l4.put("ward", "A");
/* 271:326 */     m_l4.put("wise", "A");
/* 272:327 */     m_l4.put("ying", "B");
/* 273:328 */     m_l4.put("yish", "A");
/* 274:329 */     m_l3 = new HashMap();
/* 275:330 */     m_l3.put("acy", "A");
/* 276:331 */     m_l3.put("age", "B");
/* 277:332 */     m_l3.put("aic", "A");
/* 278:333 */     m_l3.put("als", "b");
/* 279:334 */     m_l3.put("ant", "B");
/* 280:335 */     m_l3.put("ars", "O");
/* 281:336 */     m_l3.put("ary", "F");
/* 282:337 */     m_l3.put("ata", "A");
/* 283:338 */     m_l3.put("ate", "A");
/* 284:339 */     m_l3.put("eal", "Y");
/* 285:340 */     m_l3.put("ear", "Y");
/* 286:341 */     m_l3.put("ely", "E");
/* 287:342 */     m_l3.put("ene", "E");
/* 288:343 */     m_l3.put("ent", "C");
/* 289:344 */     m_l3.put("ery", "E");
/* 290:345 */     m_l3.put("ese", "A");
/* 291:346 */     m_l3.put("ful", "A");
/* 292:347 */     m_l3.put("ial", "A");
/* 293:348 */     m_l3.put("ian", "A");
/* 294:349 */     m_l3.put("ics", "A");
/* 295:350 */     m_l3.put("ide", "L");
/* 296:351 */     m_l3.put("ied", "A");
/* 297:352 */     m_l3.put("ier", "A");
/* 298:353 */     m_l3.put("ies", "P");
/* 299:354 */     m_l3.put("ily", "A");
/* 300:355 */     m_l3.put("ine", "M");
/* 301:356 */     m_l3.put("ing", "N");
/* 302:357 */     m_l3.put("ion", "Q");
/* 303:358 */     m_l3.put("ish", "C");
/* 304:359 */     m_l3.put("ism", "B");
/* 305:360 */     m_l3.put("ist", "A");
/* 306:361 */     m_l3.put("ite", "a");
/* 307:362 */     m_l3.put("ity", "A");
/* 308:363 */     m_l3.put("ium", "A");
/* 309:364 */     m_l3.put("ive", "A");
/* 310:365 */     m_l3.put("ize", "F");
/* 311:366 */     m_l3.put("oid", "A");
/* 312:367 */     m_l3.put("one", "R");
/* 313:368 */     m_l3.put("ous", "A");
/* 314:369 */     m_l2 = new HashMap();
/* 315:370 */     m_l2.put("ae", "A");
/* 316:371 */     m_l2.put("al", "b");
/* 317:372 */     m_l2.put("ar", "X");
/* 318:373 */     m_l2.put("as", "B");
/* 319:374 */     m_l2.put("ed", "E");
/* 320:375 */     m_l2.put("en", "F");
/* 321:376 */     m_l2.put("es", "E");
/* 322:377 */     m_l2.put("ia", "A");
/* 323:378 */     m_l2.put("ic", "A");
/* 324:379 */     m_l2.put("is", "A");
/* 325:380 */     m_l2.put("ly", "B");
/* 326:381 */     m_l2.put("on", "S");
/* 327:382 */     m_l2.put("or", "T");
/* 328:383 */     m_l2.put("um", "U");
/* 329:384 */     m_l2.put("us", "V");
/* 330:385 */     m_l2.put("yl", "R");
/* 331:386 */     m_l2.put("s'", "A");
/* 332:387 */     m_l2.put("'s", "A");
/* 333:388 */     m_l1 = new HashMap();
/* 334:389 */     m_l1.put("a", "A");
/* 335:390 */     m_l1.put("e", "A");
/* 336:391 */     m_l1.put("i", "A");
/* 337:392 */     m_l1.put("o", "A");
/* 338:393 */     m_l1.put("s", "W");
/* 339:394 */     m_l1.put("y", "B");
/* 340:    */   }
/* 341:    */   
/* 342:    */   public String globalInfo()
/* 343:    */   {
/* 344:403 */     return "A stemmer based on the Lovins stemmer, described here:\n\n" + getTechnicalInformation().toString();
/* 345:    */   }
/* 346:    */   
/* 347:    */   public TechnicalInformation getTechnicalInformation()
/* 348:    */   {
/* 349:418 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.ARTICLE);
/* 350:419 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Julie Beth Lovins");
/* 351:420 */     result.setValue(TechnicalInformation.Field.YEAR, "1968");
/* 352:421 */     result.setValue(TechnicalInformation.Field.TITLE, "Development of a stemming algorithm");
/* 353:422 */     result.setValue(TechnicalInformation.Field.JOURNAL, "Mechanical Translation and Computational Linguistics");
/* 354:423 */     result.setValue(TechnicalInformation.Field.VOLUME, "11");
/* 355:424 */     result.setValue(TechnicalInformation.Field.PAGES, "22-31");
/* 356:    */     
/* 357:426 */     return result;
/* 358:    */   }
/* 359:    */   
/* 360:    */   private String removeEnding(String word)
/* 361:    */   {
/* 362:437 */     int length = word.length();
/* 363:438 */     int el = 11;
/* 364:440 */     while (el > 0)
/* 365:    */     {
/* 366:441 */       if (length - el > 1)
/* 367:    */       {
/* 368:442 */         String ending = word.substring(length - el);
/* 369:443 */         String conditionCode = null;
/* 370:444 */         switch (el)
/* 371:    */         {
/* 372:    */         case 11: 
/* 373:445 */           conditionCode = (String)m_l11.get(ending);
/* 374:446 */           break;
/* 375:    */         case 10: 
/* 376:447 */           conditionCode = (String)m_l10.get(ending);
/* 377:448 */           break;
/* 378:    */         case 9: 
/* 379:449 */           conditionCode = (String)m_l9.get(ending);
/* 380:450 */           break;
/* 381:    */         case 8: 
/* 382:451 */           conditionCode = (String)m_l8.get(ending);
/* 383:452 */           break;
/* 384:    */         case 7: 
/* 385:453 */           conditionCode = (String)m_l7.get(ending);
/* 386:454 */           break;
/* 387:    */         case 6: 
/* 388:455 */           conditionCode = (String)m_l6.get(ending);
/* 389:456 */           break;
/* 390:    */         case 5: 
/* 391:457 */           conditionCode = (String)m_l5.get(ending);
/* 392:458 */           break;
/* 393:    */         case 4: 
/* 394:459 */           conditionCode = (String)m_l4.get(ending);
/* 395:460 */           break;
/* 396:    */         case 3: 
/* 397:461 */           conditionCode = (String)m_l3.get(ending);
/* 398:462 */           break;
/* 399:    */         case 2: 
/* 400:463 */           conditionCode = (String)m_l2.get(ending);
/* 401:464 */           break;
/* 402:    */         case 1: 
/* 403:465 */           conditionCode = (String)m_l1.get(ending);
/* 404:466 */           break;
/* 405:    */         }
/* 406:469 */         if (conditionCode != null) {
/* 407:470 */           switch (conditionCode.charAt(0))
/* 408:    */           {
/* 409:    */           case 'A': 
/* 410:472 */             return word.substring(0, length - el);
/* 411:    */           case 'B': 
/* 412:474 */             if (length - el > 2) {
/* 413:475 */               return word.substring(0, length - el);
/* 414:    */             }
/* 415:    */             break;
/* 416:    */           case 'C': 
/* 417:479 */             if (length - el > 3) {
/* 418:480 */               return word.substring(0, length - el);
/* 419:    */             }
/* 420:    */             break;
/* 421:    */           case 'D': 
/* 422:484 */             if (length - el > 4) {
/* 423:485 */               return word.substring(0, length - el);
/* 424:    */             }
/* 425:    */             break;
/* 426:    */           case 'E': 
/* 427:489 */             if (word.charAt(length - el - 1) != 'e') {
/* 428:490 */               return word.substring(0, length - el);
/* 429:    */             }
/* 430:    */             break;
/* 431:    */           case 'F': 
/* 432:494 */             if ((length - el > 2) && (word.charAt(length - el - 1) != 'e')) {
/* 433:496 */               return word.substring(0, length - el);
/* 434:    */             }
/* 435:    */             break;
/* 436:    */           case 'G': 
/* 437:500 */             if ((length - el > 2) && (word.charAt(length - el - 1) == 'f')) {
/* 438:502 */               return word.substring(0, length - el);
/* 439:    */             }
/* 440:    */             break;
/* 441:    */           case 'H': 
/* 442:506 */             if ((word.charAt(length - el - 1) == 't') || ((word.charAt(length - el - 1) == 'l') && (word.charAt(length - el - 2) == 'l'))) {
/* 443:509 */               return word.substring(0, length - el);
/* 444:    */             }
/* 445:    */             break;
/* 446:    */           case 'I': 
/* 447:513 */             if ((word.charAt(length - el - 1) != 'o') && (word.charAt(length - el - 1) != 'e')) {
/* 448:515 */               return word.substring(0, length - el);
/* 449:    */             }
/* 450:    */             break;
/* 451:    */           case 'J': 
/* 452:519 */             if ((word.charAt(length - el - 1) != 'a') && (word.charAt(length - el - 1) != 'e')) {
/* 453:521 */               return word.substring(0, length - el);
/* 454:    */             }
/* 455:    */             break;
/* 456:    */           case 'K': 
/* 457:525 */             if ((length - el > 2) && ((word.charAt(length - el - 1) == 'l') || (word.charAt(length - el - 1) == 'i') || ((word.charAt(length - el - 1) == 'e') && (word.charAt(length - el - 3) == 'u')))) {
/* 458:530 */               return word.substring(0, length - el);
/* 459:    */             }
/* 460:    */             break;
/* 461:    */           case 'L': 
/* 462:534 */             if ((word.charAt(length - el - 1) != 'u') && (word.charAt(length - el - 1) != 'x') && ((word.charAt(length - el - 1) != 's') || (word.charAt(length - el - 2) == 'o'))) {
/* 463:538 */               return word.substring(0, length - el);
/* 464:    */             }
/* 465:    */             break;
/* 466:    */           case 'M': 
/* 467:542 */             if ((word.charAt(length - el - 1) != 'a') && (word.charAt(length - el - 1) != 'c') && (word.charAt(length - el - 1) != 'e') && (word.charAt(length - el - 1) != 'm')) {
/* 468:546 */               return word.substring(0, length - el);
/* 469:    */             }
/* 470:    */             break;
/* 471:    */           case 'N': 
/* 472:550 */             if ((length - el > 3) || ((length - el == 3) && (word.charAt(length - el - 3) != 's'))) {
/* 473:553 */               return word.substring(0, length - el);
/* 474:    */             }
/* 475:    */             break;
/* 476:    */           case 'O': 
/* 477:557 */             if ((word.charAt(length - el - 1) == 'l') || (word.charAt(length - el - 1) == 'i')) {
/* 478:559 */               return word.substring(0, length - el);
/* 479:    */             }
/* 480:    */             break;
/* 481:    */           case 'P': 
/* 482:563 */             if (word.charAt(length - el - 1) != 'c') {
/* 483:564 */               return word.substring(0, length - el);
/* 484:    */             }
/* 485:    */             break;
/* 486:    */           case 'Q': 
/* 487:568 */             if ((length - el > 2) && (word.charAt(length - el - 1) != 'l') && (word.charAt(length - el - 1) != 'n')) {
/* 488:571 */               return word.substring(0, length - el);
/* 489:    */             }
/* 490:    */             break;
/* 491:    */           case 'R': 
/* 492:575 */             if ((word.charAt(length - el - 1) == 'n') || (word.charAt(length - el - 1) == 'r')) {
/* 493:577 */               return word.substring(0, length - el);
/* 494:    */             }
/* 495:    */             break;
/* 496:    */           case 'S': 
/* 497:581 */             if (((word.charAt(length - el - 1) == 'r') && (word.charAt(length - el - 2) == 'd')) || ((word.charAt(length - el - 1) == 't') && (word.charAt(length - el - 2) != 't'))) {
/* 498:585 */               return word.substring(0, length - el);
/* 499:    */             }
/* 500:    */             break;
/* 501:    */           case 'T': 
/* 502:589 */             if ((word.charAt(length - el - 1) == 's') || ((word.charAt(length - el - 1) == 't') && (word.charAt(length - el - 2) != 'o'))) {
/* 503:592 */               return word.substring(0, length - el);
/* 504:    */             }
/* 505:    */             break;
/* 506:    */           case 'U': 
/* 507:596 */             if ((word.charAt(length - el - 1) == 'l') || (word.charAt(length - el - 1) == 'm') || (word.charAt(length - el - 1) == 'n') || (word.charAt(length - el - 1) == 'r')) {
/* 508:600 */               return word.substring(0, length - el);
/* 509:    */             }
/* 510:    */             break;
/* 511:    */           case 'V': 
/* 512:604 */             if (word.charAt(length - el - 1) == 'c') {
/* 513:605 */               return word.substring(0, length - el);
/* 514:    */             }
/* 515:    */             break;
/* 516:    */           case 'W': 
/* 517:609 */             if ((word.charAt(length - el - 1) != 's') && (word.charAt(length - el - 1) != 'u')) {
/* 518:611 */               return word.substring(0, length - el);
/* 519:    */             }
/* 520:    */             break;
/* 521:    */           case 'X': 
/* 522:615 */             if ((word.charAt(length - el - 1) == 'l') || (word.charAt(length - el - 1) == 'i') || ((length - el > 2) && (word.charAt(length - el - 1) == 'e') && (word.charAt(length - el - 3) == 'u'))) {
/* 523:620 */               return word.substring(0, length - el);
/* 524:    */             }
/* 525:    */             break;
/* 526:    */           case 'Y': 
/* 527:624 */             if ((word.charAt(length - el - 1) == 'n') && (word.charAt(length - el - 2) == 'i')) {
/* 528:626 */               return word.substring(0, length - el);
/* 529:    */             }
/* 530:    */             break;
/* 531:    */           case 'Z': 
/* 532:630 */             if (word.charAt(length - el - 1) != 'f') {
/* 533:631 */               return word.substring(0, length - el);
/* 534:    */             }
/* 535:    */             break;
/* 536:    */           case 'a': 
/* 537:635 */             if ((word.charAt(length - el - 1) == 'd') || (word.charAt(length - el - 1) == 'f') || ((word.charAt(length - el - 1) == 'h') && (word.charAt(length - el - 2) == 'p')) || ((word.charAt(length - el - 1) == 'h') && (word.charAt(length - el - 2) == 't')) || (word.charAt(length - el - 1) == 'l') || ((word.charAt(length - el - 1) == 'r') && (word.charAt(length - el - 2) == 'e')) || ((word.charAt(length - el - 1) == 'r') && (word.charAt(length - el - 2) == 'o')) || ((word.charAt(length - el - 1) == 's') && (word.charAt(length - el - 2) == 'e')) || (word.charAt(length - el - 1) == 't')) {
/* 538:649 */               return word.substring(0, length - el);
/* 539:    */             }
/* 540:    */             break;
/* 541:    */           case 'b': 
/* 542:653 */             if (m_CompMode)
/* 543:    */             {
/* 544:654 */               if (((length - el == 3) && ((word.charAt(length - el - 1) != 't') || (word.charAt(length - el - 2) != 'e') || (word.charAt(length - el - 3) != 'm'))) || ((length - el > 3) && ((word.charAt(length - el - 1) != 't') || (word.charAt(length - el - 2) != 's') || (word.charAt(length - el - 3) != 'y') || (word.charAt(length - el - 4) != 'r')))) {
/* 545:663 */                 return word.substring(0, length - el);
/* 546:    */               }
/* 547:    */             }
/* 548:666 */             else if ((length - el > 2) && ((word.charAt(length - el - 1) != 't') || (word.charAt(length - el - 2) != 'e') || (word.charAt(length - el - 3) != 'm')) && ((length - el < 4) || (word.charAt(length - el - 1) != 't') || (word.charAt(length - el - 2) != 's') || (word.charAt(length - el - 3) != 'y') || (word.charAt(length - el - 4) != 'r'))) {
/* 549:675 */               return word.substring(0, length - el);
/* 550:    */             }
/* 551:    */             break;
/* 552:    */           case 'c': 
/* 553:680 */             if (word.charAt(length - el - 1) == 'l') {
/* 554:681 */               return word.substring(0, length - el);
/* 555:    */             }
/* 556:    */             break;
/* 557:    */           case '[': 
/* 558:    */           case '\\': 
/* 559:    */           case ']': 
/* 560:    */           case '^': 
/* 561:    */           case '_': 
/* 562:    */           case '`': 
/* 563:    */           default: 
/* 564:685 */             throw new IllegalArgumentException("Fatal error.");
/* 565:    */           }
/* 566:    */         }
/* 567:    */       }
/* 568:689 */       el--;
/* 569:    */     }
/* 570:691 */     return word;
/* 571:    */   }
/* 572:    */   
/* 573:    */   private String recodeEnding(String word)
/* 574:    */   {
/* 575:702 */     int lastPos = word.length() - 1;
/* 576:705 */     if ((word.endsWith("bb")) || (word.endsWith("dd")) || (word.endsWith("gg")) || (word.endsWith("ll")) || (word.endsWith("mm")) || (word.endsWith("nn")) || (word.endsWith("pp")) || (word.endsWith("rr")) || (word.endsWith("ss")) || (word.endsWith("tt")))
/* 577:    */     {
/* 578:715 */       word = word.substring(0, lastPos);
/* 579:716 */       lastPos--;
/* 580:    */     }
/* 581:720 */     if (word.endsWith("iev")) {
/* 582:721 */       word = word.substring(0, lastPos - 2).concat("ief");
/* 583:    */     }
/* 584:725 */     if (word.endsWith("uct"))
/* 585:    */     {
/* 586:726 */       word = word.substring(0, lastPos - 2).concat("uc");
/* 587:727 */       lastPos--;
/* 588:    */     }
/* 589:731 */     if (word.endsWith("umpt"))
/* 590:    */     {
/* 591:732 */       word = word.substring(0, lastPos - 3).concat("um");
/* 592:733 */       lastPos -= 2;
/* 593:    */     }
/* 594:737 */     if (word.endsWith("rpt"))
/* 595:    */     {
/* 596:738 */       word = word.substring(0, lastPos - 2).concat("rb");
/* 597:739 */       lastPos--;
/* 598:    */     }
/* 599:743 */     if (word.endsWith("urs"))
/* 600:    */     {
/* 601:744 */       word = word.substring(0, lastPos - 2).concat("ur");
/* 602:745 */       lastPos--;
/* 603:    */     }
/* 604:749 */     if (word.endsWith("istr"))
/* 605:    */     {
/* 606:750 */       word = word.substring(0, lastPos - 3).concat("ister");
/* 607:751 */       lastPos++;
/* 608:    */     }
/* 609:755 */     if (word.endsWith("metr"))
/* 610:    */     {
/* 611:756 */       word = word.substring(0, lastPos - 3).concat("meter");
/* 612:757 */       lastPos++;
/* 613:    */     }
/* 614:761 */     if (word.endsWith("olv"))
/* 615:    */     {
/* 616:762 */       word = word.substring(0, lastPos - 2).concat("olut");
/* 617:763 */       lastPos++;
/* 618:    */     }
/* 619:767 */     if ((word.endsWith("ul")) && (
/* 620:768 */       (lastPos - 2 < 0) || ((word.charAt(lastPos - 2) != 'a') && (word.charAt(lastPos - 2) != 'i') && (word.charAt(lastPos - 2) != 'o'))))
/* 621:    */     {
/* 622:772 */       word = word.substring(0, lastPos - 1).concat("l");
/* 623:773 */       lastPos--;
/* 624:    */     }
/* 625:778 */     if (word.endsWith("bex")) {
/* 626:779 */       word = word.substring(0, lastPos - 2).concat("bic");
/* 627:    */     }
/* 628:783 */     if (word.endsWith("dex")) {
/* 629:784 */       word = word.substring(0, lastPos - 2).concat("dic");
/* 630:    */     }
/* 631:788 */     if (word.endsWith("pex")) {
/* 632:789 */       word = word.substring(0, lastPos - 2).concat("pic");
/* 633:    */     }
/* 634:793 */     if (word.endsWith("tex")) {
/* 635:794 */       word = word.substring(0, lastPos - 2).concat("tic");
/* 636:    */     }
/* 637:798 */     if (word.endsWith("ax")) {
/* 638:799 */       word = word.substring(0, lastPos - 1).concat("ac");
/* 639:    */     }
/* 640:803 */     if (word.endsWith("ex")) {
/* 641:804 */       word = word.substring(0, lastPos - 1).concat("ec");
/* 642:    */     }
/* 643:808 */     if (word.endsWith("ix")) {
/* 644:809 */       word = word.substring(0, lastPos - 1).concat("ic");
/* 645:    */     }
/* 646:813 */     if (word.endsWith("lux")) {
/* 647:814 */       word = word.substring(0, lastPos - 2).concat("luc");
/* 648:    */     }
/* 649:818 */     if (word.endsWith("uad")) {
/* 650:819 */       word = word.substring(0, lastPos - 2).concat("uas");
/* 651:    */     }
/* 652:823 */     if (word.endsWith("vad")) {
/* 653:824 */       word = word.substring(0, lastPos - 2).concat("vas");
/* 654:    */     }
/* 655:828 */     if (word.endsWith("cid")) {
/* 656:829 */       word = word.substring(0, lastPos - 2).concat("cis");
/* 657:    */     }
/* 658:833 */     if (word.endsWith("lid")) {
/* 659:834 */       word = word.substring(0, lastPos - 2).concat("lis");
/* 660:    */     }
/* 661:838 */     if (word.endsWith("erid")) {
/* 662:839 */       word = word.substring(0, lastPos - 3).concat("eris");
/* 663:    */     }
/* 664:843 */     if (word.endsWith("pand")) {
/* 665:844 */       word = word.substring(0, lastPos - 3).concat("pans");
/* 666:    */     }
/* 667:848 */     if ((word.endsWith("end")) && (
/* 668:849 */       (lastPos - 3 < 0) || (word.charAt(lastPos - 3) != 's'))) {
/* 669:851 */       word = word.substring(0, lastPos - 2).concat("ens");
/* 670:    */     }
/* 671:856 */     if (word.endsWith("ond")) {
/* 672:857 */       word = word.substring(0, lastPos - 2).concat("ons");
/* 673:    */     }
/* 674:861 */     if (word.endsWith("lud")) {
/* 675:862 */       word = word.substring(0, lastPos - 2).concat("lus");
/* 676:    */     }
/* 677:866 */     if (word.endsWith("rud")) {
/* 678:867 */       word = word.substring(0, lastPos - 2).concat("rus");
/* 679:    */     }
/* 680:871 */     if ((word.endsWith("her")) && (
/* 681:872 */       (lastPos - 3 < 0) || ((word.charAt(lastPos - 3) != 'p') && (word.charAt(lastPos - 3) != 't')))) {
/* 682:875 */       word = word.substring(0, lastPos - 2).concat("hes");
/* 683:    */     }
/* 684:880 */     if (word.endsWith("mit")) {
/* 685:881 */       word = word.substring(0, lastPos - 2).concat("mis");
/* 686:    */     }
/* 687:885 */     if ((word.endsWith("end")) && (
/* 688:886 */       (lastPos - 3 < 0) || (word.charAt(lastPos - 3) != 'm'))) {
/* 689:888 */       word = word.substring(0, lastPos - 2).concat("ens");
/* 690:    */     }
/* 691:893 */     if (word.endsWith("ert")) {
/* 692:894 */       word = word.substring(0, lastPos - 2).concat("ers");
/* 693:    */     }
/* 694:898 */     if ((word.endsWith("et")) && (
/* 695:899 */       (lastPos - 2 < 0) || (word.charAt(lastPos - 2) != 'n'))) {
/* 696:901 */       word = word.substring(0, lastPos - 1).concat("es");
/* 697:    */     }
/* 698:906 */     if (word.endsWith("yt")) {
/* 699:907 */       word = word.substring(0, lastPos - 1).concat("ys");
/* 700:    */     }
/* 701:911 */     if (word.endsWith("yz")) {
/* 702:912 */       word = word.substring(0, lastPos - 1).concat("ys");
/* 703:    */     }
/* 704:915 */     return word;
/* 705:    */   }
/* 706:    */   
/* 707:    */   public String stem(String word)
/* 708:    */   {
/* 709:927 */     if (word.length() > 2) {
/* 710:928 */       return recodeEnding(removeEnding(word.toLowerCase()));
/* 711:    */     }
/* 712:930 */     return word.toLowerCase();
/* 713:    */   }
/* 714:    */   
/* 715:    */   public String stemString(String str)
/* 716:    */   {
/* 717:943 */     StringBuffer result = new StringBuffer();
/* 718:944 */     int start = -1;
/* 719:945 */     for (int j = 0; j < str.length(); j++)
/* 720:    */     {
/* 721:946 */       char c = str.charAt(j);
/* 722:947 */       if (Character.isLetterOrDigit(c))
/* 723:    */       {
/* 724:948 */         if (start == -1) {
/* 725:949 */           start = j;
/* 726:    */         }
/* 727:    */       }
/* 728:951 */       else if (c == '\'')
/* 729:    */       {
/* 730:952 */         if (start == -1) {
/* 731:953 */           result.append(c);
/* 732:    */         }
/* 733:    */       }
/* 734:    */       else
/* 735:    */       {
/* 736:956 */         if (start != -1)
/* 737:    */         {
/* 738:957 */           result.append(stem(str.substring(start, j)));
/* 739:958 */           start = -1;
/* 740:    */         }
/* 741:960 */         result.append(c);
/* 742:    */       }
/* 743:    */     }
/* 744:963 */     if (start != -1) {
/* 745:964 */       result.append(stem(str.substring(start, str.length())));
/* 746:    */     }
/* 747:966 */     return result.toString();
/* 748:    */   }
/* 749:    */   
/* 750:    */   public String toString()
/* 751:    */   {
/* 752:975 */     return getClass().getName();
/* 753:    */   }
/* 754:    */   
/* 755:    */   public String getRevision()
/* 756:    */   {
/* 757:984 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 758:    */   }
/* 759:    */   
/* 760:    */   public static void main(String[] args)
/* 761:    */   {
/* 762:    */     try
/* 763:    */     {
/* 764:994 */       Stemming.useStemmer(new LovinsStemmer(), args);
/* 765:    */     }
/* 766:    */     catch (Exception e)
/* 767:    */     {
/* 768:997 */       e.printStackTrace();
/* 769:    */     }
/* 770:    */   }
/* 771:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.stemmers.LovinsStemmer
 * JD-Core Version:    0.7.0.1
 */