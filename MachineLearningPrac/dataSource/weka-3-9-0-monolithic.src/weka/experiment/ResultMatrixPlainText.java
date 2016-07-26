/*   1:    */ package weka.experiment;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Vector;
/*   5:    */ import weka.core.RevisionUtils;
/*   6:    */ import weka.core.Utils;
/*   7:    */ 
/*   8:    */ public class ResultMatrixPlainText
/*   9:    */   extends ResultMatrix
/*  10:    */ {
/*  11:    */   private static final long serialVersionUID = 1502934525382357937L;
/*  12:    */   
/*  13:    */   public ResultMatrixPlainText()
/*  14:    */   {
/*  15:143 */     this(1, 1);
/*  16:    */   }
/*  17:    */   
/*  18:    */   public ResultMatrixPlainText(int cols, int rows)
/*  19:    */   {
/*  20:153 */     super(cols, rows);
/*  21:    */   }
/*  22:    */   
/*  23:    */   public ResultMatrixPlainText(ResultMatrix matrix)
/*  24:    */   {
/*  25:162 */     super(matrix);
/*  26:    */   }
/*  27:    */   
/*  28:    */   public String globalInfo()
/*  29:    */   {
/*  30:172 */     return "Generates the output as plain text (for fixed width fonts).";
/*  31:    */   }
/*  32:    */   
/*  33:    */   public String getDisplayName()
/*  34:    */   {
/*  35:182 */     return "Plain Text";
/*  36:    */   }
/*  37:    */   
/*  38:    */   public int getDefaultRowNameWidth()
/*  39:    */   {
/*  40:192 */     return 25;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public int getDefaultCountWidth()
/*  44:    */   {
/*  45:202 */     return 5;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public String toStringHeader()
/*  49:    */   {
/*  50:219 */     String result = "";
/*  51:    */     
/*  52:    */ 
/*  53:222 */     String[][] data = new String[this.m_HeaderKeys.size()][2];
/*  54:223 */     for (int i = 0; i < this.m_HeaderKeys.size(); i++)
/*  55:    */     {
/*  56:224 */       data[i][0] = (((String)this.m_HeaderKeys.get(i)).toString() + ":");
/*  57:225 */       data[i][1] = ((String)this.m_HeaderValues.get(i)).toString();
/*  58:    */     }
/*  59:229 */     int size = getColSize(data, 0);
/*  60:230 */     for (i = 0; i < data.length; i++) {
/*  61:231 */       data[i][0] = padString(data[i][0], size);
/*  62:    */     }
/*  63:235 */     for (i = 0; i < data.length; i++) {
/*  64:236 */       result = result + data[i][0] + " " + data[i][1] + "\n";
/*  65:    */     }
/*  66:239 */     return result;
/*  67:    */   }
/*  68:    */   
/*  69:    */   public String toStringMatrix()
/*  70:    */   {
/*  71:265 */     StringBuffer result = new StringBuffer();
/*  72:266 */     StringBuffer head = new StringBuffer();
/*  73:267 */     StringBuffer body = new StringBuffer();
/*  74:268 */     StringBuffer foot = new StringBuffer();
/*  75:269 */     String[][] cells = toArray();
/*  76:270 */     int[] startMeans = new int[getColCount()];
/*  77:271 */     int[] startSigs = new int[getColCount() - 1];
/*  78:272 */     int maxLength = 0;
/*  79:275 */     for (int n = 1; n < cells[0].length; n++)
/*  80:    */     {
/*  81:276 */       int size = getColSize(cells, n, true, true);
/*  82:277 */       for (int i = 1; i < cells.length - 1; i++) {
/*  83:278 */         cells[i][n] = padString(cells[i][n], size, true);
/*  84:    */       }
/*  85:    */     }
/*  86:283 */     int indexBase = 1;
/*  87:284 */     if (getShowStdDev()) {
/*  88:285 */       indexBase++;
/*  89:    */     }
/*  90:288 */     if (getShowStdDev()) {}
/*  91:292 */     int j = 0;
/*  92:293 */     int k = 0;
/*  93:294 */     for (int i = 1; i < cells.length - 1; i++)
/*  94:    */     {
/*  95:295 */       if (isAverage(i)) {
/*  96:296 */         body.append(padString("", maxLength).replaceAll(".", "-") + "\n");
/*  97:    */       }
/*  98:298 */       String line = "";
/*  99:300 */       for (n = 0; n < cells[0].length; n++)
/* 100:    */       {
/* 101:302 */         if (i == 1)
/* 102:    */         {
/* 103:303 */           if (isMean(n))
/* 104:    */           {
/* 105:304 */             startMeans[j] = line.length();
/* 106:305 */             j++;
/* 107:    */           }
/* 108:308 */           if (isSignificance(n))
/* 109:    */           {
/* 110:309 */             startSigs[k] = line.length();
/* 111:310 */             k++;
/* 112:    */           }
/* 113:    */         }
/* 114:314 */         if (n == 0)
/* 115:    */         {
/* 116:315 */           line = line + padString(cells[i][n], getRowNameWidth());
/* 117:316 */           if (!isAverage(i)) {
/* 118:317 */             line = line + padString(new StringBuilder().append("(").append(Utils.doubleToString(getCount(getDisplayRow(i - 1)), 0)).append(")").toString(), getCountWidth(), true);
/* 119:    */           } else {
/* 120:321 */             line = line + padString("", getCountWidth(), true);
/* 121:    */           }
/* 122:    */         }
/* 123:    */         else
/* 124:    */         {
/* 125:325 */           if (isMean(n)) {
/* 126:326 */             line = line + "  ";
/* 127:    */           }
/* 128:330 */           if (getShowStdDev())
/* 129:    */           {
/* 130:331 */             if (isMean(n - 1))
/* 131:    */             {
/* 132:332 */               if (!cells[i][n].trim().equals("")) {
/* 133:333 */                 line = line + "(" + cells[i][n] + ")";
/* 134:    */               } else {
/* 135:335 */                 line = line + " " + cells[i][n] + " ";
/* 136:    */               }
/* 137:    */             }
/* 138:    */             else {
/* 139:338 */               line = line + " " + cells[i][n];
/* 140:    */             }
/* 141:    */           }
/* 142:    */           else {
/* 143:341 */             line = line + " " + cells[i][n];
/* 144:    */           }
/* 145:    */         }
/* 146:346 */         if (n == indexBase) {
/* 147:347 */           line = line + " |";
/* 148:    */         }
/* 149:    */       }
/* 150:352 */       if (i == 1) {
/* 151:353 */         maxLength = line.length();
/* 152:    */       }
/* 153:356 */       body.append(line + "\n");
/* 154:    */     }
/* 155:360 */     String line = padString(cells[0][0], startMeans[0]);
/* 156:361 */     i = -1;
/* 157:362 */     for (n = 1; n < cells[0].length; n++) {
/* 158:363 */       if (isMean(n))
/* 159:    */       {
/* 160:364 */         i++;
/* 161:366 */         if (i == 0) {
/* 162:367 */           line = padString(line, startMeans[i] - getCountWidth());
/* 163:368 */         } else if (i == 1) {
/* 164:369 */           line = padString(line, startMeans[i] - " |".length());
/* 165:370 */         } else if (i > 1) {
/* 166:371 */           line = padString(line, startMeans[i]);
/* 167:    */         }
/* 168:374 */         if (i == 1) {
/* 169:375 */           line = line + " |";
/* 170:    */         }
/* 171:378 */         line = line + " " + cells[0][n];
/* 172:    */       }
/* 173:    */     }
/* 174:381 */     line = padString(line, maxLength);
/* 175:382 */     head.append(line + "\n");
/* 176:383 */     head.append(line.replaceAll(".", "-") + "\n");
/* 177:384 */     body.append(line.replaceAll(".", "-") + "\n");
/* 178:387 */     if (getColCount() > 1)
/* 179:    */     {
/* 180:388 */       line = padString(cells[(cells.length - 1)][0], startMeans[1] - 2, true) + " |";
/* 181:    */       
/* 182:390 */       i = 0;
/* 183:391 */       for (n = 1; n < cells[(cells.length - 1)].length; n++) {
/* 184:392 */         if (isSignificance(n))
/* 185:    */         {
/* 186:393 */           line = padString(line, startSigs[i] + 1 - cells[(cells.length - 1)][n].length());
/* 187:    */           
/* 188:395 */           line = line + " " + cells[(cells.length - 1)][n];
/* 189:396 */           i++;
/* 190:    */         }
/* 191:    */       }
/* 192:399 */       line = padString(line, maxLength);
/* 193:    */     }
/* 194:    */     else
/* 195:    */     {
/* 196:401 */       line = padString(cells[(cells.length - 1)][0], line.length() - 2) + " |";
/* 197:    */     }
/* 198:403 */     foot.append(line + "\n");
/* 199:    */     
/* 200:    */ 
/* 201:406 */     result.append(head.toString());
/* 202:407 */     result.append(body.toString());
/* 203:408 */     result.append(foot.toString());
/* 204:    */     
/* 205:410 */     return result.toString();
/* 206:    */   }
/* 207:    */   
/* 208:    */   public String toStringKey()
/* 209:    */   {
/* 210:424 */     String result = "Key:\n";
/* 211:425 */     for (int i = 0; i < getColCount(); i++) {
/* 212:426 */       if (!getColHidden(i)) {
/* 213:430 */         result = result + this.LEFT_PARENTHESES + (i + 1) + this.RIGHT_PARENTHESES + " " + removeFilterName(this.m_ColNames[i]) + "\n";
/* 214:    */       }
/* 215:    */     }
/* 216:434 */     return result;
/* 217:    */   }
/* 218:    */   
/* 219:    */   public String toStringSummary()
/* 220:    */   {
/* 221:450 */     if (this.m_NonSigWins == null) {
/* 222:451 */       return "-summary data not set-";
/* 223:    */     }
/* 224:454 */     String result = "";
/* 225:455 */     String titles = "";
/* 226:456 */     int resultsetLength = 1 + Math.max((int)(Math.log(getColCount()) / Math.log(10.0D)), (int)(Math.log(getRowCount()) / Math.log(10.0D)));
/* 227:460 */     for (int i = 0; i < getColCount(); i++) {
/* 228:461 */       if (!getColHidden(i)) {
/* 229:464 */         titles = titles + " " + Utils.padLeft(new StringBuilder().append("").append(getSummaryTitle(i)).toString(), resultsetLength * 2 + 3);
/* 230:    */       }
/* 231:    */     }
/* 232:467 */     result = result + titles + "  (No. of datasets where [col] >> [row])\n";
/* 233:469 */     for (i = 0; i < getColCount(); i++) {
/* 234:470 */       if (!getColHidden(i))
/* 235:    */       {
/* 236:474 */         for (int j = 0; j < getColCount(); j++) {
/* 237:475 */           if (!getColHidden(j))
/* 238:    */           {
/* 239:479 */             result = result + " ";
/* 240:480 */             if (j == i) {
/* 241:481 */               result = result + Utils.padLeft("-", resultsetLength * 2 + 3);
/* 242:    */             } else {
/* 243:483 */               result = result + Utils.padLeft(new StringBuilder().append("").append(this.m_NonSigWins[i][j]).append(" (").append(this.m_Wins[i][j]).append(")").toString(), resultsetLength * 2 + 3);
/* 244:    */             }
/* 245:    */           }
/* 246:    */         }
/* 247:488 */         result = result + " | " + getSummaryTitle(i) + " = " + getColName(i) + '\n';
/* 248:    */       }
/* 249:    */     }
/* 250:491 */     return result;
/* 251:    */   }
/* 252:    */   
/* 253:    */   public String toStringRanking()
/* 254:    */   {
/* 255:508 */     if (this.m_RankingWins == null) {
/* 256:509 */       return "-ranking data not set-";
/* 257:    */     }
/* 258:512 */     int biggest = Math.max(this.m_RankingWins[Utils.maxIndex(this.m_RankingWins)], this.m_RankingLosses[Utils.maxIndex(this.m_RankingLosses)]);
/* 259:    */     
/* 260:514 */     int width = Math.max(2 + (int)(Math.log(biggest) / Math.log(10.0D)), ">-<".length());
/* 261:    */     
/* 262:516 */     String result = Utils.padLeft(">-<", width) + ' ' + Utils.padLeft(">", width) + ' ' + Utils.padLeft("<", width) + " Resultset\n";
/* 263:    */     
/* 264:    */ 
/* 265:519 */     int[] ranking = Utils.sort(this.m_RankingDiff);
/* 266:521 */     for (int i = getColCount() - 1; i >= 0; i--)
/* 267:    */     {
/* 268:522 */       int curr = ranking[i];
/* 269:524 */       if (!getColHidden(curr)) {
/* 270:528 */         result = result + Utils.padLeft(new StringBuilder().append("").append(this.m_RankingDiff[curr]).toString(), width) + ' ' + Utils.padLeft(new StringBuilder().append("").append(this.m_RankingWins[curr]).toString(), width) + ' ' + Utils.padLeft(new StringBuilder().append("").append(this.m_RankingLosses[curr]).toString(), width) + ' ' + removeFilterName(this.m_ColNames[curr]) + '\n';
/* 271:    */       }
/* 272:    */     }
/* 273:534 */     return result;
/* 274:    */   }
/* 275:    */   
/* 276:    */   public String getRevision()
/* 277:    */   {
/* 278:544 */     return RevisionUtils.extract("$Revision: 10204 $");
/* 279:    */   }
/* 280:    */   
/* 281:    */   public static void main(String[] args)
/* 282:    */   {
/* 283:557 */     ResultMatrix matrix = new ResultMatrixPlainText(3, 3);
/* 284:    */     
/* 285:    */ 
/* 286:560 */     matrix.addHeader("header1", "value1");
/* 287:561 */     matrix.addHeader("header2", "value2");
/* 288:562 */     matrix.addHeader("header2", "value3");
/* 289:565 */     for (int i = 0; i < matrix.getRowCount(); i++) {
/* 290:566 */       for (int n = 0; n < matrix.getColCount(); n++)
/* 291:    */       {
/* 292:567 */         matrix.setMean(n, i, (i + 1) * n);
/* 293:568 */         matrix.setStdDev(n, i, (i + 1) * n / 100.0D);
/* 294:569 */         if (i == n) {
/* 295:570 */           if (i % 2 == 1) {
/* 296:571 */             matrix.setSignificance(n, i, 1);
/* 297:    */           } else {
/* 298:573 */             matrix.setSignificance(n, i, 2);
/* 299:    */           }
/* 300:    */         }
/* 301:    */       }
/* 302:    */     }
/* 303:579 */     System.out.println("\n\n--> " + matrix.getDisplayName());
/* 304:    */     
/* 305:581 */     System.out.println("\n1. complete\n");
/* 306:582 */     System.out.println(matrix.toStringHeader() + "\n");
/* 307:583 */     System.out.println(matrix.toStringMatrix() + "\n");
/* 308:584 */     System.out.println(matrix.toStringKey());
/* 309:    */     
/* 310:586 */     System.out.println("\n2. complete with std deviations\n");
/* 311:587 */     matrix.setShowStdDev(true);
/* 312:588 */     System.out.println(matrix.toStringMatrix());
/* 313:    */     
/* 314:590 */     System.out.println("\n3. cols numbered\n");
/* 315:591 */     matrix.setPrintColNames(false);
/* 316:592 */     System.out.println(matrix.toStringMatrix());
/* 317:    */     
/* 318:594 */     System.out.println("\n4. second col missing\n");
/* 319:595 */     matrix.setColHidden(1, true);
/* 320:596 */     System.out.println(matrix.toStringMatrix());
/* 321:    */     
/* 322:598 */     System.out.println("\n5. last row missing, rows numbered too\n");
/* 323:599 */     matrix.setRowHidden(2, true);
/* 324:600 */     matrix.setPrintRowNames(false);
/* 325:601 */     System.out.println(matrix.toStringMatrix());
/* 326:    */     
/* 327:603 */     System.out.println("\n6. mean prec to 3\n");
/* 328:604 */     matrix.setMeanPrec(3);
/* 329:605 */     matrix.setPrintRowNames(false);
/* 330:606 */     System.out.println(matrix.toStringMatrix());
/* 331:    */   }
/* 332:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.experiment.ResultMatrixPlainText
 * JD-Core Version:    0.7.0.1
 */