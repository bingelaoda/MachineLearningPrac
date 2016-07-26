/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.io.Serializable;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import java.util.Iterator;
/*   7:    */ 
/*   8:    */ public class Range
/*   9:    */   implements Serializable, RevisionHandler, CustomDisplayStringProvider
/*  10:    */ {
/*  11:    */   static final long serialVersionUID = 3667337062176835900L;
/*  12: 52 */   ArrayList<String> m_RangeStrings = new ArrayList();
/*  13:    */   boolean m_Invert;
/*  14:    */   boolean[] m_SelectFlags;
/*  15: 64 */   int m_Upper = -1;
/*  16:    */   
/*  17:    */   public Range() {}
/*  18:    */   
/*  19:    */   public Range(String rangeList)
/*  20:    */   {
/*  21: 79 */     setRanges(rangeList);
/*  22:    */   }
/*  23:    */   
/*  24:    */   public void setUpper(int newUpper)
/*  25:    */   {
/*  26: 89 */     if (newUpper >= 0)
/*  27:    */     {
/*  28: 90 */       this.m_Upper = newUpper;
/*  29: 91 */       setFlags();
/*  30:    */     }
/*  31:    */   }
/*  32:    */   
/*  33:    */   public boolean getInvert()
/*  34:    */   {
/*  35:104 */     return this.m_Invert;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public void setInvert(boolean newSetting)
/*  39:    */   {
/*  40:115 */     this.m_Invert = newSetting;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public String getRanges()
/*  44:    */   {
/*  45:125 */     StringBuffer result = new StringBuffer(this.m_RangeStrings.size() * 4);
/*  46:126 */     boolean first = true;
/*  47:127 */     char sep = ',';
/*  48:128 */     for (int i = 0; i < this.m_RangeStrings.size(); i++) {
/*  49:129 */       if (first)
/*  50:    */       {
/*  51:130 */         result.append((String)this.m_RangeStrings.get(i));
/*  52:131 */         first = false;
/*  53:    */       }
/*  54:    */       else
/*  55:    */       {
/*  56:133 */         result.append(sep + (String)this.m_RangeStrings.get(i));
/*  57:    */       }
/*  58:    */     }
/*  59:136 */     return result.toString();
/*  60:    */   }
/*  61:    */   
/*  62:    */   public void setRanges(String rangeList)
/*  63:    */   {
/*  64:151 */     ArrayList<String> ranges = new ArrayList(10);
/*  65:154 */     while (!rangeList.equals(""))
/*  66:    */     {
/*  67:155 */       String range = rangeList.trim();
/*  68:156 */       int commaLoc = rangeList.indexOf(',');
/*  69:157 */       if (commaLoc != -1)
/*  70:    */       {
/*  71:158 */         range = rangeList.substring(0, commaLoc).trim();
/*  72:159 */         rangeList = rangeList.substring(commaLoc + 1).trim();
/*  73:    */       }
/*  74:    */       else
/*  75:    */       {
/*  76:161 */         rangeList = "";
/*  77:    */       }
/*  78:163 */       if (!range.equals("")) {
/*  79:164 */         ranges.add(range);
/*  80:    */       }
/*  81:    */     }
/*  82:167 */     this.m_RangeStrings = ranges;
/*  83:168 */     this.m_SelectFlags = null;
/*  84:    */   }
/*  85:    */   
/*  86:    */   public boolean isInRange(int index)
/*  87:    */   {
/*  88:183 */     if (this.m_Upper == -1) {
/*  89:184 */       throw new RuntimeException("No upper limit has been specified for range");
/*  90:    */     }
/*  91:186 */     if (this.m_Invert) {
/*  92:187 */       return this.m_SelectFlags[index] == 0;
/*  93:    */     }
/*  94:189 */     return this.m_SelectFlags[index];
/*  95:    */   }
/*  96:    */   
/*  97:    */   public String toString()
/*  98:    */   {
/*  99:202 */     if (this.m_RangeStrings.size() == 0) {
/* 100:203 */       return "Empty";
/* 101:    */     }
/* 102:205 */     String result = "Strings: ";
/* 103:206 */     Iterator<String> enu = this.m_RangeStrings.iterator();
/* 104:207 */     while (enu.hasNext()) {
/* 105:208 */       result = result + (String)enu.next() + " ";
/* 106:    */     }
/* 107:210 */     result = result + "\n";
/* 108:    */     
/* 109:212 */     result = result + "Invert: " + this.m_Invert + "\n";
/* 110:    */     try
/* 111:    */     {
/* 112:215 */       if (this.m_Upper == -1) {
/* 113:216 */         throw new RuntimeException("Upper limit has not been specified");
/* 114:    */       }
/* 115:218 */       String cols = null;
/* 116:219 */       for (int i = 0; i < this.m_SelectFlags.length; i++) {
/* 117:220 */         if (isInRange(i)) {
/* 118:221 */           if (cols == null) {
/* 119:222 */             cols = "Cols: " + (i + 1);
/* 120:    */           } else {
/* 121:224 */             cols = cols + "," + (i + 1);
/* 122:    */           }
/* 123:    */         }
/* 124:    */       }
/* 125:228 */       if (cols != null) {
/* 126:229 */         result = result + cols + "\n";
/* 127:    */       }
/* 128:    */     }
/* 129:    */     catch (Exception ex)
/* 130:    */     {
/* 131:232 */       result = result + ex.getMessage();
/* 132:    */     }
/* 133:234 */     return result;
/* 134:    */   }
/* 135:    */   
/* 136:    */   public int[] getSelection()
/* 137:    */   {
/* 138:248 */     if (this.m_Upper == -1) {
/* 139:249 */       throw new RuntimeException("No upper limit has been specified for range");
/* 140:    */     }
/* 141:251 */     int[] selectIndices = new int[this.m_Upper + 1];
/* 142:252 */     int numSelected = 0;
/* 143:253 */     if (this.m_Invert)
/* 144:    */     {
/* 145:254 */       for (int i = 0; i <= this.m_Upper; i++) {
/* 146:255 */         if (this.m_SelectFlags[i] == 0) {
/* 147:256 */           selectIndices[(numSelected++)] = i;
/* 148:    */         }
/* 149:    */       }
/* 150:    */     }
/* 151:    */     else
/* 152:    */     {
/* 153:260 */       Iterator<String> enu = this.m_RangeStrings.iterator();
/* 154:261 */       for (; enu.hasNext(); goto 119)
/* 155:    */       {
/* 156:262 */         String currentRange = (String)enu.next();
/* 157:263 */         int start = rangeLower(currentRange);
/* 158:264 */         int end = rangeUpper(currentRange);
/* 159:265 */         int i = start;
/* 160:265 */         if ((i <= this.m_Upper) && (i <= end))
/* 161:    */         {
/* 162:266 */           if (this.m_SelectFlags[i] != 0) {
/* 163:267 */             selectIndices[(numSelected++)] = i;
/* 164:    */           }
/* 165:265 */           i++;
/* 166:    */         }
/* 167:    */       }
/* 168:    */     }
/* 169:272 */     int[] result = new int[numSelected];
/* 170:273 */     System.arraycopy(selectIndices, 0, result, 0, numSelected);
/* 171:274 */     return result;
/* 172:    */   }
/* 173:    */   
/* 174:    */   public static String indicesToRangeList(int[] indices)
/* 175:    */   {
/* 176:288 */     StringBuffer rl = new StringBuffer();
/* 177:289 */     int last = -2;
/* 178:290 */     boolean range = false;
/* 179:291 */     for (int i = 0; i < indices.length; i++)
/* 180:    */     {
/* 181:292 */       if (i == 0)
/* 182:    */       {
/* 183:293 */         rl.append(indices[i] + 1);
/* 184:    */       }
/* 185:294 */       else if (indices[i] == last)
/* 186:    */       {
/* 187:295 */         range = true;
/* 188:    */       }
/* 189:    */       else
/* 190:    */       {
/* 191:297 */         if (range)
/* 192:    */         {
/* 193:298 */           rl.append('-').append(last);
/* 194:299 */           range = false;
/* 195:    */         }
/* 196:301 */         rl.append(',').append(indices[i] + 1);
/* 197:    */       }
/* 198:303 */       last = indices[i] + 1;
/* 199:    */     }
/* 200:305 */     if (range) {
/* 201:306 */       rl.append('-').append(last);
/* 202:    */     }
/* 203:308 */     return rl.toString();
/* 204:    */   }
/* 205:    */   
/* 206:    */   protected void setFlags()
/* 207:    */   {
/* 208:314 */     this.m_SelectFlags = new boolean[this.m_Upper + 1];
/* 209:315 */     Iterator<String> enu = this.m_RangeStrings.iterator();
/* 210:316 */     for (; enu.hasNext(); goto 90)
/* 211:    */     {
/* 212:317 */       String currentRange = (String)enu.next();
/* 213:318 */       if (!isValidRange(currentRange)) {
/* 214:319 */         throw new IllegalArgumentException("Invalid range list at " + currentRange);
/* 215:    */       }
/* 216:322 */       int start = rangeLower(currentRange);
/* 217:323 */       int end = rangeUpper(currentRange);
/* 218:324 */       int i = start;
/* 219:324 */       if ((i <= this.m_Upper) && (i <= end))
/* 220:    */       {
/* 221:325 */         this.m_SelectFlags[i] = true;i++;
/* 222:    */       }
/* 223:    */     }
/* 224:    */   }
/* 225:    */   
/* 226:    */   protected int rangeSingle(String single)
/* 227:    */   {
/* 228:338 */     if (single.toLowerCase().equals("first")) {
/* 229:339 */       return 0;
/* 230:    */     }
/* 231:341 */     if (single.toLowerCase().equals("last")) {
/* 232:342 */       return this.m_Upper;
/* 233:    */     }
/* 234:344 */     int index = Integer.parseInt(single) - 1;
/* 235:345 */     if (index < 0) {
/* 236:346 */       index = 0;
/* 237:    */     }
/* 238:348 */     if (index > this.m_Upper) {
/* 239:349 */       index = this.m_Upper;
/* 240:    */     }
/* 241:351 */     return index;
/* 242:    */   }
/* 243:    */   
/* 244:    */   protected int rangeLower(String range)
/* 245:    */   {
/* 246:    */     int hyphenIndex;
/* 247:363 */     if ((hyphenIndex = range.indexOf('-')) >= 0) {
/* 248:364 */       return Math.min(rangeLower(range.substring(0, hyphenIndex)), rangeLower(range.substring(hyphenIndex + 1)));
/* 249:    */     }
/* 250:367 */     return rangeSingle(range);
/* 251:    */   }
/* 252:    */   
/* 253:    */   protected int rangeUpper(String range)
/* 254:    */   {
/* 255:    */     int hyphenIndex;
/* 256:380 */     if ((hyphenIndex = range.indexOf('-')) >= 0) {
/* 257:381 */       return Math.max(rangeUpper(range.substring(0, hyphenIndex)), rangeUpper(range.substring(hyphenIndex + 1)));
/* 258:    */     }
/* 259:384 */     return rangeSingle(range);
/* 260:    */   }
/* 261:    */   
/* 262:    */   protected boolean isValidRange(String range)
/* 263:    */   {
/* 264:397 */     if (range == null) {
/* 265:398 */       return false;
/* 266:    */     }
/* 267:    */     int hyphenIndex;
/* 268:401 */     if ((hyphenIndex = range.indexOf('-')) >= 0)
/* 269:    */     {
/* 270:402 */       if ((isValidRange(range.substring(0, hyphenIndex))) && (isValidRange(range.substring(hyphenIndex + 1)))) {
/* 271:404 */         return true;
/* 272:    */       }
/* 273:406 */       return false;
/* 274:    */     }
/* 275:408 */     if (range.toLowerCase().equals("first")) {
/* 276:409 */       return true;
/* 277:    */     }
/* 278:411 */     if (range.toLowerCase().equals("last")) {
/* 279:412 */       return true;
/* 280:    */     }
/* 281:    */     try
/* 282:    */     {
/* 283:415 */       int index = Integer.parseInt(range);
/* 284:416 */       if ((index > 0) && (index <= this.m_Upper + 1)) {
/* 285:417 */         return true;
/* 286:    */       }
/* 287:419 */       return false;
/* 288:    */     }
/* 289:    */     catch (NumberFormatException ex) {}
/* 290:421 */     return false;
/* 291:    */   }
/* 292:    */   
/* 293:    */   public String getRevision()
/* 294:    */   {
/* 295:432 */     return RevisionUtils.extract("$Revision: 10203 $");
/* 296:    */   }
/* 297:    */   
/* 298:    */   public String toDisplay()
/* 299:    */   {
/* 300:442 */     if (getInvert()) {
/* 301:443 */       return "inv(" + getRanges() + ")";
/* 302:    */     }
/* 303:445 */     return getRanges();
/* 304:    */   }
/* 305:    */   
/* 306:    */   public static void main(String[] argv)
/* 307:    */   {
/* 308:    */     try
/* 309:    */     {
/* 310:457 */       if (argv.length == 0) {
/* 311:458 */         throw new Exception("Usage: Range <rangespec>");
/* 312:    */       }
/* 313:460 */       Range range = new Range();
/* 314:461 */       range.setRanges(argv[0]);
/* 315:462 */       range.setUpper(9);
/* 316:463 */       range.setInvert(false);
/* 317:464 */       System.out.println("Input: " + argv[0] + "\n" + range.toString());
/* 318:465 */       int[] rangeIndices = range.getSelection();
/* 319:466 */       for (int rangeIndice : rangeIndices) {
/* 320:467 */         System.out.print(" " + (rangeIndice + 1));
/* 321:    */       }
/* 322:469 */       System.out.println("");
/* 323:    */     }
/* 324:    */     catch (Exception ex)
/* 325:    */     {
/* 326:471 */       System.out.println(ex.getMessage());
/* 327:    */     }
/* 328:    */   }
/* 329:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.Range
 * JD-Core Version:    0.7.0.1
 */