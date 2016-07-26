/*   1:    */ package weka.experiment;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import weka.core.RevisionUtils;
/*   5:    */ import weka.core.Utils;
/*   6:    */ 
/*   7:    */ public class ResultMatrixLatex
/*   8:    */   extends ResultMatrix
/*   9:    */ {
/*  10:    */   private static final long serialVersionUID = 777690788447600978L;
/*  11:    */   
/*  12:    */   public ResultMatrixLatex()
/*  13:    */   {
/*  14:114 */     this(1, 1);
/*  15:    */   }
/*  16:    */   
/*  17:    */   public ResultMatrixLatex(int cols, int rows)
/*  18:    */   {
/*  19:124 */     super(cols, rows);
/*  20:    */   }
/*  21:    */   
/*  22:    */   public ResultMatrixLatex(ResultMatrix matrix)
/*  23:    */   {
/*  24:133 */     super(matrix);
/*  25:    */   }
/*  26:    */   
/*  27:    */   public String globalInfo()
/*  28:    */   {
/*  29:143 */     return "Generates the matrix output in LaTeX-syntax.";
/*  30:    */   }
/*  31:    */   
/*  32:    */   public String getDisplayName()
/*  33:    */   {
/*  34:152 */     return "LaTeX";
/*  35:    */   }
/*  36:    */   
/*  37:    */   public void clear()
/*  38:    */   {
/*  39:159 */     super.clear();
/*  40:160 */     this.TIE_STRING = " ";
/*  41:161 */     this.WIN_STRING = "$\\circ$";
/*  42:162 */     this.LOSS_STRING = "$\\bullet$";
/*  43:    */   }
/*  44:    */   
/*  45:    */   public boolean getDefaultPrintColNames()
/*  46:    */   {
/*  47:171 */     return false;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public boolean getDefaultEnumerateColNames()
/*  51:    */   {
/*  52:180 */     return true;
/*  53:    */   }
/*  54:    */   
/*  55:    */   public String toStringHeader()
/*  56:    */   {
/*  57:191 */     return new ResultMatrixPlainText(this).toStringHeader();
/*  58:    */   }
/*  59:    */   
/*  60:    */   public String toStringMatrix()
/*  61:    */   {
/*  62:207 */     StringBuffer result = new StringBuffer();
/*  63:208 */     String[][] cells = toArray();
/*  64:    */     
/*  65:210 */     result.append("\\begin{table}[thb]\n\\caption{\\label{labelname}Table Caption}\n");
/*  66:212 */     if (!getShowStdDev()) {
/*  67:213 */       result.append("\\footnotesize\n");
/*  68:    */     } else {
/*  69:215 */       result.append("\\scriptsize\n");
/*  70:    */     }
/*  71:219 */     if (!getShowStdDev()) {
/*  72:220 */       result.append("{\\centering \\begin{tabular}{lr");
/*  73:    */     } else {
/*  74:227 */       result.append("{\\centering \\begin{tabular}{lr@{\\hspace{0cm}}c@{\\hspace{0cm}}r");
/*  75:    */     }
/*  76:238 */     for (int j = 1; j < getColCount(); j++) {
/*  77:239 */       if (!getColHidden(j)) {
/*  78:241 */         if (!getShowStdDev()) {
/*  79:242 */           result.append("r@{\\hspace{0.1cm}}c");
/*  80:    */         } else {
/*  81:247 */           result.append("r@{\\hspace{0cm}}c@{\\hspace{0cm}}r@{\\hspace{0.1cm}}c");
/*  82:    */         }
/*  83:    */       }
/*  84:    */     }
/*  85:256 */     result.append("}\n\\\\\n\\hline\n");
/*  86:257 */     if (!getShowStdDev()) {
/*  87:258 */       result.append("Dataset & " + cells[0][1]);
/*  88:    */     } else {
/*  89:260 */       result.append("Dataset & \\multicolumn{3}{c}{" + cells[0][1] + "}");
/*  90:    */     }
/*  91:263 */     for (j = 2; j < cells[0].length; j++) {
/*  92:264 */       if (isMean(j)) {
/*  93:266 */         if (!getShowStdDev()) {
/*  94:267 */           result.append("& " + cells[0][j] + " & ");
/*  95:    */         } else {
/*  96:269 */           result.append("& \\multicolumn{4}{c}{" + cells[0][j] + "} ");
/*  97:    */         }
/*  98:    */       }
/*  99:    */     }
/* 100:271 */     result.append("\\\\\n\\hline\n");
/* 101:274 */     for (int i = 1; i < cells.length; i++) {
/* 102:275 */       cells[i][0] = cells[i][0].replace('_', '-');
/* 103:    */     }
/* 104:278 */     for (int n = 1; n < cells[0].length; n++)
/* 105:    */     {
/* 106:279 */       int size = getColSize(cells, n);
/* 107:280 */       for (i = 1; i < cells.length; i++) {
/* 108:281 */         cells[i][n] = padString(cells[i][n], size, true);
/* 109:    */       }
/* 110:    */     }
/* 111:285 */     for (i = 1; i < cells.length - 1; i++)
/* 112:    */     {
/* 113:286 */       if (isAverage(i)) {
/* 114:287 */         result.append("\\hline\n");
/* 115:    */       }
/* 116:288 */       for (n = 0; n < cells[0].length; n++) {
/* 117:289 */         if (n == 0)
/* 118:    */         {
/* 119:290 */           result.append(padString(cells[i][n], getRowNameWidth()));
/* 120:    */         }
/* 121:    */         else
/* 122:    */         {
/* 123:293 */           if (getShowStdDev())
/* 124:    */           {
/* 125:294 */             if (isMean(n - 1))
/* 126:    */             {
/* 127:295 */               if (!cells[i][n].trim().equals("")) {
/* 128:296 */                 result.append(" & $\\pm$ & ");
/* 129:    */               } else {
/* 130:298 */                 result.append(" &       & ");
/* 131:    */               }
/* 132:    */             }
/* 133:    */             else {
/* 134:301 */               result.append(" & ");
/* 135:    */             }
/* 136:    */           }
/* 137:    */           else {
/* 138:304 */             result.append(" & ");
/* 139:    */           }
/* 140:306 */           result.append(cells[i][n]);
/* 141:    */         }
/* 142:    */       }
/* 143:310 */       result.append("\\\\\n");
/* 144:    */     }
/* 145:313 */     result.append("\\hline\n\\multicolumn{" + cells[0].length + "}{c}{$\\circ$, $\\bullet$" + " statistically significant improvement or degradation}" + "\\\\\n\\end{tabular} ");
/* 146:316 */     if (!getShowStdDev()) {
/* 147:317 */       result.append("\\footnotesize ");
/* 148:    */     } else {
/* 149:319 */       result.append("\\scriptsize ");
/* 150:    */     }
/* 151:321 */     result.append("\\par}\n\\end{table}\n");
/* 152:    */     
/* 153:    */ 
/* 154:324 */     return result.toString();
/* 155:    */   }
/* 156:    */   
/* 157:    */   public String toStringKey()
/* 158:    */   {
/* 159:337 */     String result = "\\begin{table}[thb]\n\\caption{\\label{labelname}Table Caption (Key)}\n";
/* 160:    */     
/* 161:339 */     result = result + "\\scriptsize\n";
/* 162:340 */     result = result + "{\\centering\n";
/* 163:341 */     result = result + "\\begin{tabular}{cl}\\\\\n";
/* 164:342 */     for (int i = 0; i < getColCount(); i++) {
/* 165:343 */       if (!getColHidden(i)) {
/* 166:346 */         result = result + this.LEFT_PARENTHESES + (i + 1) + this.RIGHT_PARENTHESES + " & " + removeFilterName(this.m_ColNames[i]).replace('_', '-').replaceAll("\\\\", "\\\\textbackslash") + " \\\\\n";
/* 167:    */       }
/* 168:    */     }
/* 169:351 */     result = result + "\\end{tabular}\n";
/* 170:352 */     result = result + "}\n";
/* 171:353 */     result = result + "\\end{table}\n";
/* 172:    */     
/* 173:355 */     return result;
/* 174:    */   }
/* 175:    */   
/* 176:    */   public String toStringSummary()
/* 177:    */   {
/* 178:370 */     if (this.m_NonSigWins == null) {
/* 179:371 */       return "-summary data not set-";
/* 180:    */     }
/* 181:373 */     int resultsetLength = 1 + Math.max((int)(Math.log(getColCount()) / Math.log(10.0D)), (int)(Math.log(getRowCount()) / Math.log(10.0D)));
/* 182:    */     
/* 183:375 */     String result = "";
/* 184:376 */     String titles = "";
/* 185:    */     
/* 186:378 */     result = result + "{\\centering\n";
/* 187:379 */     result = result + "\\begin{table}[thb]\n\\caption{\\label{labelname}Table Caption}\n";
/* 188:    */     
/* 189:381 */     result = result + "\\footnotesize\n";
/* 190:382 */     result = result + "\\begin{tabular}{l";
/* 191:384 */     for (int i = 0; i < getColCount(); i++) {
/* 192:385 */       if (!getColHidden(i))
/* 193:    */       {
/* 194:388 */         titles = titles + " &";
/* 195:389 */         result = result + "c";
/* 196:390 */         titles = titles + ' ' + Utils.padLeft(new StringBuilder().append("").append(getSummaryTitle(i)).toString(), resultsetLength * 2 + 3);
/* 197:    */       }
/* 198:    */     }
/* 199:393 */     result = result + "}\\\\\n\\hline\n";
/* 200:394 */     result = result + titles + " \\\\\n\\hline\n";
/* 201:396 */     for (i = 0; i < getColCount(); i++) {
/* 202:397 */       if (!getColHidden(i))
/* 203:    */       {
/* 204:400 */         for (int j = 0; j < getColCount(); j++) {
/* 205:401 */           if (!getColHidden(j))
/* 206:    */           {
/* 207:404 */             if (j == 0) {
/* 208:405 */               result = result + (char)(97 + i % 26);
/* 209:    */             }
/* 210:407 */             if (j == i) {
/* 211:408 */               result = result + " & - ";
/* 212:    */             } else {
/* 213:410 */               result = result + "& " + this.m_NonSigWins[i][j] + " (" + this.m_Wins[i][j] + ") ";
/* 214:    */             }
/* 215:    */           }
/* 216:    */         }
/* 217:412 */         result = result + "\\\\\n";
/* 218:    */       }
/* 219:    */     }
/* 220:415 */     result = result + "\\hline\n\\end{tabular} \\footnotesize \\par\n\\end{table}}";
/* 221:    */     
/* 222:417 */     return result;
/* 223:    */   }
/* 224:    */   
/* 225:    */   public String toStringRanking()
/* 226:    */   {
/* 227:433 */     if (this.m_RankingWins == null) {
/* 228:434 */       return "-ranking data not set-";
/* 229:    */     }
/* 230:436 */     int biggest = Math.max(this.m_RankingWins[Utils.maxIndex(this.m_RankingWins)], this.m_RankingLosses[Utils.maxIndex(this.m_RankingLosses)]);
/* 231:    */     
/* 232:438 */     int width = Math.max(2 + (int)(Math.log(biggest) / Math.log(10.0D)), ">-<".length());
/* 233:    */     
/* 234:440 */     String result = "\\begin{table}[thb]\n\\caption{\\label{labelname}Table Caption}\n\\footnotesize\n{\\centering \\begin{tabular}{rlll}\\\\\n\\hline\n";
/* 235:    */     
/* 236:442 */     result = result + "Resultset & Wins$-$ & Wins & Losses \\\\\n& Losses & & \\\\\n\\hline\n";
/* 237:    */     
/* 238:    */ 
/* 239:445 */     int[] ranking = Utils.sort(this.m_RankingDiff);
/* 240:446 */     for (int i = getColCount() - 1; i >= 0; i--)
/* 241:    */     {
/* 242:447 */       int curr = ranking[i];
/* 243:449 */       if (!getColHidden(curr)) {
/* 244:452 */         result = result + "(" + (curr + 1) + ") & " + Utils.padLeft(new StringBuilder().append("").append(this.m_RankingDiff[curr]).toString(), width) + " & " + Utils.padLeft(new StringBuilder().append("").append(this.m_RankingWins[curr]).toString(), width) + " & " + Utils.padLeft(new StringBuilder().append("").append(this.m_RankingLosses[curr]).toString(), width) + "\\\\\n";
/* 245:    */       }
/* 246:    */     }
/* 247:459 */     result = result + "\\hline\n\\end{tabular} \\footnotesize \\par}\n\\end{table}";
/* 248:    */     
/* 249:461 */     return result;
/* 250:    */   }
/* 251:    */   
/* 252:    */   public String getRevision()
/* 253:    */   {
/* 254:470 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 255:    */   }
/* 256:    */   
/* 257:    */   public static void main(String[] args)
/* 258:    */   {
/* 259:483 */     ResultMatrix matrix = new ResultMatrixLatex(3, 3);
/* 260:    */     
/* 261:    */ 
/* 262:486 */     matrix.addHeader("header1", "value1");
/* 263:487 */     matrix.addHeader("header2", "value2");
/* 264:488 */     matrix.addHeader("header2", "value3");
/* 265:491 */     for (int i = 0; i < matrix.getRowCount(); i++) {
/* 266:492 */       for (int n = 0; n < matrix.getColCount(); n++)
/* 267:    */       {
/* 268:493 */         matrix.setMean(n, i, (i + 1) * n);
/* 269:494 */         matrix.setStdDev(n, i, (i + 1) * n / 100.0D);
/* 270:495 */         if (i == n) {
/* 271:496 */           if (i % 2 == 1) {
/* 272:497 */             matrix.setSignificance(n, i, 1);
/* 273:    */           } else {
/* 274:499 */             matrix.setSignificance(n, i, 2);
/* 275:    */           }
/* 276:    */         }
/* 277:    */       }
/* 278:    */     }
/* 279:504 */     System.out.println("\n\n--> " + matrix.getDisplayName());
/* 280:    */     
/* 281:506 */     System.out.println("\n1. complete\n");
/* 282:507 */     System.out.println(matrix.toStringHeader() + "\n");
/* 283:508 */     System.out.println(matrix.toStringMatrix() + "\n");
/* 284:509 */     System.out.println(matrix.toStringKey());
/* 285:    */     
/* 286:511 */     System.out.println("\n2. complete with std deviations\n");
/* 287:512 */     matrix.setShowStdDev(true);
/* 288:513 */     System.out.println(matrix.toStringMatrix());
/* 289:    */     
/* 290:515 */     System.out.println("\n3. cols numbered\n");
/* 291:516 */     matrix.setPrintColNames(false);
/* 292:517 */     System.out.println(matrix.toStringMatrix());
/* 293:    */     
/* 294:519 */     System.out.println("\n4. second col missing\n");
/* 295:520 */     matrix.setColHidden(1, true);
/* 296:521 */     System.out.println(matrix.toStringMatrix());
/* 297:    */     
/* 298:523 */     System.out.println("\n5. last row missing, rows numbered too\n");
/* 299:524 */     matrix.setRowHidden(2, true);
/* 300:525 */     matrix.setPrintRowNames(false);
/* 301:526 */     System.out.println(matrix.toStringMatrix());
/* 302:    */     
/* 303:528 */     System.out.println("\n6. mean prec to 3\n");
/* 304:529 */     matrix.setMeanPrec(3);
/* 305:530 */     matrix.setPrintRowNames(false);
/* 306:531 */     System.out.println(matrix.toStringMatrix());
/* 307:    */   }
/* 308:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.experiment.ResultMatrixLatex
 * JD-Core Version:    0.7.0.1
 */