/*   1:    */ package weka.experiment;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import weka.core.RevisionUtils;
/*   5:    */ import weka.core.Utils;
/*   6:    */ 
/*   7:    */ public class ResultMatrixHTML
/*   8:    */   extends ResultMatrix
/*   9:    */ {
/*  10:    */   private static final long serialVersionUID = 6672380422544799990L;
/*  11:    */   
/*  12:    */   public ResultMatrixHTML()
/*  13:    */   {
/*  14:114 */     this(1, 1);
/*  15:    */   }
/*  16:    */   
/*  17:    */   public ResultMatrixHTML(int cols, int rows)
/*  18:    */   {
/*  19:124 */     super(cols, rows);
/*  20:    */   }
/*  21:    */   
/*  22:    */   public ResultMatrixHTML(ResultMatrix matrix)
/*  23:    */   {
/*  24:133 */     super(matrix);
/*  25:    */   }
/*  26:    */   
/*  27:    */   public String globalInfo()
/*  28:    */   {
/*  29:143 */     return "Generates the matrix output as HTML.";
/*  30:    */   }
/*  31:    */   
/*  32:    */   public String getDisplayName()
/*  33:    */   {
/*  34:152 */     return "HTML";
/*  35:    */   }
/*  36:    */   
/*  37:    */   public int getDefaultRowNameWidth()
/*  38:    */   {
/*  39:161 */     return 25;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public boolean getDefaultPrintColNames()
/*  43:    */   {
/*  44:170 */     return false;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public boolean getDefaultEnumerateColNames()
/*  48:    */   {
/*  49:179 */     return true;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public String toStringHeader()
/*  53:    */   {
/*  54:190 */     return new ResultMatrixPlainText(this).toStringHeader();
/*  55:    */   }
/*  56:    */   
/*  57:    */   public String toStringMatrix()
/*  58:    */   {
/*  59:205 */     StringBuffer result = new StringBuffer();
/*  60:206 */     String[][] cells = toArray();
/*  61:    */     
/*  62:208 */     result.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\">\n");
/*  63:    */     
/*  64:    */ 
/*  65:211 */     result.append("   <tr>");
/*  66:212 */     for (int n = 0; n < cells[0].length; n++) {
/*  67:213 */       if (isRowName(n))
/*  68:    */       {
/*  69:214 */         result.append("<td><b>" + cells[0][n] + "</b></td>");
/*  70:    */       }
/*  71:216 */       else if (isMean(n))
/*  72:    */       {
/*  73:    */         int cols;
/*  74:    */         int cols;
/*  75:217 */         if (n == 1) {
/*  76:218 */           cols = 1;
/*  77:    */         } else {
/*  78:220 */           cols = 2;
/*  79:    */         }
/*  80:221 */         if (getShowStdDev()) {
/*  81:222 */           cols++;
/*  82:    */         }
/*  83:223 */         result.append("<td align=\"center\" colspan=\"" + cols + "\">");
/*  84:224 */         result.append("<b>" + cells[0][n] + "</b>");
/*  85:225 */         result.append("</td>");
/*  86:    */       }
/*  87:    */     }
/*  88:228 */     result.append("</tr>\n");
/*  89:231 */     for (int i = 1; i < cells.length; i++)
/*  90:    */     {
/*  91:232 */       result.append("   <tr>");
/*  92:233 */       for (n = 0; n < cells[i].length; n++)
/*  93:    */       {
/*  94:234 */         if (isRowName(n)) {
/*  95:235 */           result.append("<td>");
/*  96:236 */         } else if ((isMean(n)) || (isStdDev(n))) {
/*  97:237 */           result.append("<td align=\"right\">");
/*  98:238 */         } else if (isSignificance(n)) {
/*  99:239 */           result.append("<td align=\"center\">");
/* 100:    */         } else {
/* 101:241 */           result.append("<td>");
/* 102:    */         }
/* 103:244 */         if (cells[i][n].trim().equals("")) {
/* 104:245 */           result.append("&nbsp;");
/* 105:246 */         } else if (isStdDev(n)) {
/* 106:247 */           result.append("&plusmn;&nbsp;" + cells[i][n]);
/* 107:    */         } else {
/* 108:249 */           result.append(cells[i][n]);
/* 109:    */         }
/* 110:251 */         result.append("</td>");
/* 111:    */       }
/* 112:253 */       result.append("</tr>\n");
/* 113:    */     }
/* 114:255 */     result.append("</table>\n");
/* 115:    */     
/* 116:257 */     return result.toString();
/* 117:    */   }
/* 118:    */   
/* 119:    */   public String toStringKey()
/* 120:    */   {
/* 121:270 */     String result = "<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\">\n   <tr><td colspan=\"2\"><b>Key</b></td></tr>\n";
/* 122:272 */     for (int i = 0; i < getColCount(); i++) {
/* 123:273 */       if (!getColHidden(i)) {
/* 124:276 */         result = result + "   <tr><td><b>(" + (i + 1) + ")</b></td>" + "<td>" + removeFilterName(this.m_ColNames[i]) + "</td>" + "</tr>\n";
/* 125:    */       }
/* 126:    */     }
/* 127:282 */     result = result + "</table>\n";
/* 128:    */     
/* 129:284 */     return result;
/* 130:    */   }
/* 131:    */   
/* 132:    */   public String toStringSummary()
/* 133:    */   {
/* 134:300 */     if (this.m_NonSigWins == null) {
/* 135:301 */       return "-summary data not set-";
/* 136:    */     }
/* 137:303 */     String result = "<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\">\n";
/* 138:304 */     String titles = "   <tr>";
/* 139:305 */     int resultsetLength = 1 + Math.max((int)(Math.log(getColCount()) / Math.log(10.0D)), (int)(Math.log(getRowCount()) / Math.log(10.0D)));
/* 140:308 */     for (int i = 0; i < getColCount(); i++) {
/* 141:309 */       if (!getColHidden(i)) {
/* 142:311 */         titles = titles + "<td align=\"center\"><b>" + getSummaryTitle(i) + "</b></td>";
/* 143:    */       }
/* 144:    */     }
/* 145:313 */     result = result + titles + "<td><b>(No. of datasets where [col] &gt;&gt; [row])</b></td></tr>\n";
/* 146:316 */     for (i = 0; i < getColCount(); i++) {
/* 147:317 */       if (!getColHidden(i))
/* 148:    */       {
/* 149:320 */         result = result + "   <tr>";
/* 150:322 */         for (int j = 0; j < getColCount(); j++) {
/* 151:323 */           if (!getColHidden(j))
/* 152:    */           {
/* 153:    */             String content;
/* 154:    */             String content;
/* 155:326 */             if (j == i) {
/* 156:327 */               content = Utils.padLeft("-", resultsetLength * 2 + 3);
/* 157:    */             } else {
/* 158:329 */               content = Utils.padLeft("" + this.m_NonSigWins[i][j] + " (" + this.m_Wins[i][j] + ")", resultsetLength * 2 + 3);
/* 159:    */             }
/* 160:332 */             result = result + "<td>" + content.replaceAll(" ", "&nbsp;") + "</td>";
/* 161:    */           }
/* 162:    */         }
/* 163:335 */         result = result + "<td><b>" + getSummaryTitle(i) + "</b> = " + removeFilterName(this.m_ColNames[i]) + "</td></tr>\n";
/* 164:    */       }
/* 165:    */     }
/* 166:338 */     result = result + "</table>\n";
/* 167:    */     
/* 168:340 */     return result;
/* 169:    */   }
/* 170:    */   
/* 171:    */   public String toStringRanking()
/* 172:    */   {
/* 173:354 */     if (this.m_RankingWins == null) {
/* 174:355 */       return "-ranking data not set-";
/* 175:    */     }
/* 176:357 */     String result = "<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\">\n";
/* 177:358 */     result = result + "   <tr><td align=\"center\"><b>&gt;-&lt;</b></td><td align=\"center\"><b>&gt;</b></td><td align=\"center\"><b>&lt;</b></td><td><b>Resultset</b></td></tr>\n";
/* 178:    */     
/* 179:    */ 
/* 180:    */ 
/* 181:    */ 
/* 182:    */ 
/* 183:    */ 
/* 184:365 */     int[] ranking = Utils.sort(this.m_RankingDiff);
/* 185:367 */     for (int i = getColCount() - 1; i >= 0; i--)
/* 186:    */     {
/* 187:368 */       int curr = ranking[i];
/* 188:370 */       if (!getColHidden(curr)) {
/* 189:373 */         result = result + "   <tr><td align=\"right\">" + this.m_RankingDiff[curr] + "</td>" + "<td align=\"right\">" + this.m_RankingWins[curr] + "</td>" + "<td align=\"right\">" + this.m_RankingLosses[curr] + "</td>" + "<td>" + removeFilterName(this.m_ColNames[curr]) + "</td>" + "<tr>\n";
/* 190:    */       }
/* 191:    */     }
/* 192:381 */     result = result + "</table>\n";
/* 193:    */     
/* 194:383 */     return result;
/* 195:    */   }
/* 196:    */   
/* 197:    */   public String getRevision()
/* 198:    */   {
/* 199:392 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 200:    */   }
/* 201:    */   
/* 202:    */   public static void main(String[] args)
/* 203:    */   {
/* 204:405 */     ResultMatrix matrix = new ResultMatrixHTML(3, 3);
/* 205:    */     
/* 206:    */ 
/* 207:408 */     matrix.addHeader("header1", "value1");
/* 208:409 */     matrix.addHeader("header2", "value2");
/* 209:410 */     matrix.addHeader("header2", "value3");
/* 210:413 */     for (int i = 0; i < matrix.getRowCount(); i++) {
/* 211:414 */       for (int n = 0; n < matrix.getColCount(); n++)
/* 212:    */       {
/* 213:415 */         matrix.setMean(n, i, (i + 1) * n);
/* 214:416 */         matrix.setStdDev(n, i, (i + 1) * n / 100.0D);
/* 215:417 */         if (i == n) {
/* 216:418 */           if (i % 2 == 1) {
/* 217:419 */             matrix.setSignificance(n, i, 1);
/* 218:    */           } else {
/* 219:421 */             matrix.setSignificance(n, i, 2);
/* 220:    */           }
/* 221:    */         }
/* 222:    */       }
/* 223:    */     }
/* 224:426 */     System.out.println("\n\n--> " + matrix.getDisplayName());
/* 225:    */     
/* 226:428 */     System.out.println("\n1. complete\n");
/* 227:429 */     System.out.println(matrix.toStringHeader() + "\n");
/* 228:430 */     System.out.println(matrix.toStringMatrix() + "\n");
/* 229:431 */     System.out.println(matrix.toStringKey());
/* 230:    */     
/* 231:433 */     System.out.println("\n2. complete with std deviations\n");
/* 232:434 */     matrix.setShowStdDev(true);
/* 233:435 */     System.out.println(matrix.toStringMatrix());
/* 234:    */     
/* 235:437 */     System.out.println("\n3. cols numbered\n");
/* 236:438 */     matrix.setPrintColNames(false);
/* 237:439 */     System.out.println(matrix.toStringMatrix());
/* 238:    */     
/* 239:441 */     System.out.println("\n4. second col missing\n");
/* 240:442 */     matrix.setColHidden(1, true);
/* 241:443 */     System.out.println(matrix.toStringMatrix());
/* 242:    */     
/* 243:445 */     System.out.println("\n5. last row missing, rows numbered too\n");
/* 244:446 */     matrix.setRowHidden(2, true);
/* 245:447 */     matrix.setPrintRowNames(false);
/* 246:448 */     System.out.println(matrix.toStringMatrix());
/* 247:    */     
/* 248:450 */     System.out.println("\n6. mean prec to 3\n");
/* 249:451 */     matrix.setMeanPrec(3);
/* 250:452 */     matrix.setPrintRowNames(false);
/* 251:453 */     System.out.println(matrix.toStringMatrix());
/* 252:    */   }
/* 253:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.experiment.ResultMatrixHTML
 * JD-Core Version:    0.7.0.1
 */