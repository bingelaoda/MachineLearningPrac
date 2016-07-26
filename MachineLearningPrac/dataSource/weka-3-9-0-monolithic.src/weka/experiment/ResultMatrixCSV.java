/*   1:    */ package weka.experiment;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import weka.core.RevisionUtils;
/*   5:    */ import weka.core.Utils;
/*   6:    */ 
/*   7:    */ public class ResultMatrixCSV
/*   8:    */   extends ResultMatrix
/*   9:    */ {
/*  10:    */   private static final long serialVersionUID = -171838863135042743L;
/*  11:    */   
/*  12:    */   public ResultMatrixCSV()
/*  13:    */   {
/*  14:114 */     this(1, 1);
/*  15:    */   }
/*  16:    */   
/*  17:    */   public ResultMatrixCSV(int cols, int rows)
/*  18:    */   {
/*  19:124 */     super(cols, rows);
/*  20:    */   }
/*  21:    */   
/*  22:    */   public ResultMatrixCSV(ResultMatrix matrix)
/*  23:    */   {
/*  24:133 */     super(matrix);
/*  25:    */   }
/*  26:    */   
/*  27:    */   public String globalInfo()
/*  28:    */   {
/*  29:143 */     return "Generates the matrix in CSV ('comma-separated values') format.";
/*  30:    */   }
/*  31:    */   
/*  32:    */   public String getDisplayName()
/*  33:    */   {
/*  34:152 */     return "CSV";
/*  35:    */   }
/*  36:    */   
/*  37:    */   public void clear()
/*  38:    */   {
/*  39:159 */     super.clear();
/*  40:160 */     this.LEFT_PARENTHESES = "[";
/*  41:161 */     this.RIGHT_PARENTHESES = "]";
/*  42:    */   }
/*  43:    */   
/*  44:    */   public int getDefaultRowNameWidth()
/*  45:    */   {
/*  46:170 */     return 25;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public boolean getDefaultPrintColNames()
/*  50:    */   {
/*  51:179 */     return false;
/*  52:    */   }
/*  53:    */   
/*  54:    */   public boolean getDefaultEnumerateColNames()
/*  55:    */   {
/*  56:188 */     return true;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public String toStringHeader()
/*  60:    */   {
/*  61:199 */     return new ResultMatrixPlainText(this).toStringHeader();
/*  62:    */   }
/*  63:    */   
/*  64:    */   public String toStringMatrix()
/*  65:    */   {
/*  66:213 */     StringBuffer result = new StringBuffer();
/*  67:214 */     String[][] cells = toArray();
/*  68:216 */     for (int i = 0; i < cells.length; i++)
/*  69:    */     {
/*  70:217 */       for (int n = 0; n < cells[i].length; n++)
/*  71:    */       {
/*  72:218 */         if (n > 0) {
/*  73:219 */           result.append(",");
/*  74:    */         }
/*  75:220 */         result.append(Utils.quote(cells[i][n]));
/*  76:    */       }
/*  77:222 */       result.append("\n");
/*  78:    */     }
/*  79:225 */     return result.toString();
/*  80:    */   }
/*  81:    */   
/*  82:    */   public String toStringKey()
/*  83:    */   {
/*  84:238 */     String result = "Key,\n";
/*  85:239 */     for (int i = 0; i < getColCount(); i++) {
/*  86:240 */       if (!getColHidden(i)) {
/*  87:243 */         result = result + this.LEFT_PARENTHESES + (i + 1) + this.RIGHT_PARENTHESES + "," + Utils.quote(removeFilterName(this.m_ColNames[i])) + "\n";
/*  88:    */       }
/*  89:    */     }
/*  90:247 */     return result;
/*  91:    */   }
/*  92:    */   
/*  93:    */   public String toStringSummary()
/*  94:    */   {
/*  95:262 */     if (this.m_NonSigWins == null) {
/*  96:263 */       return "-summary data not set-";
/*  97:    */     }
/*  98:265 */     String result = "";
/*  99:266 */     String titles = "";
/* 100:268 */     for (int i = 0; i < getColCount(); i++) {
/* 101:269 */       if (!getColHidden(i))
/* 102:    */       {
/* 103:271 */         if (!titles.equals("")) {
/* 104:272 */           titles = titles + ",";
/* 105:    */         }
/* 106:273 */         titles = titles + getSummaryTitle(i);
/* 107:    */       }
/* 108:    */     }
/* 109:275 */     result = result + titles + ",'(No. of datasets where [col] >> [row])'\n";
/* 110:277 */     for (i = 0; i < getColCount(); i++) {
/* 111:278 */       if (!getColHidden(i))
/* 112:    */       {
/* 113:281 */         String line = "";
/* 114:282 */         for (int j = 0; j < getColCount(); j++) {
/* 115:283 */           if (!getColHidden(j))
/* 116:    */           {
/* 117:286 */             if (!line.equals("")) {
/* 118:287 */               line = line + ",";
/* 119:    */             }
/* 120:289 */             if (j == i) {
/* 121:290 */               line = line + "-";
/* 122:    */             } else {
/* 123:292 */               line = line + this.m_NonSigWins[i][j] + " (" + this.m_Wins[i][j] + ")";
/* 124:    */             }
/* 125:    */           }
/* 126:    */         }
/* 127:296 */         result = result + line + "," + getSummaryTitle(i) + " = " + removeFilterName(this.m_ColNames[i]) + '\n';
/* 128:    */       }
/* 129:    */     }
/* 130:299 */     return result;
/* 131:    */   }
/* 132:    */   
/* 133:    */   public String toStringRanking()
/* 134:    */   {
/* 135:313 */     if (this.m_RankingWins == null) {
/* 136:314 */       return "-ranking data not set-";
/* 137:    */     }
/* 138:316 */     String result = ">-<,>,<,Resultset\n";
/* 139:    */     
/* 140:318 */     int[] ranking = Utils.sort(this.m_RankingDiff);
/* 141:320 */     for (int i = getColCount() - 1; i >= 0; i--)
/* 142:    */     {
/* 143:321 */       int curr = ranking[i];
/* 144:323 */       if (!getColHidden(curr)) {
/* 145:326 */         result = result + this.m_RankingDiff[curr] + "," + this.m_RankingWins[curr] + "," + this.m_RankingLosses[curr] + "," + removeFilterName(this.m_ColNames[curr]) + "\n";
/* 146:    */       }
/* 147:    */     }
/* 148:332 */     return result;
/* 149:    */   }
/* 150:    */   
/* 151:    */   public String getRevision()
/* 152:    */   {
/* 153:341 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 154:    */   }
/* 155:    */   
/* 156:    */   public static void main(String[] args)
/* 157:    */   {
/* 158:354 */     ResultMatrix matrix = new ResultMatrixCSV(3, 3);
/* 159:    */     
/* 160:    */ 
/* 161:357 */     matrix.addHeader("header1", "value1");
/* 162:358 */     matrix.addHeader("header2", "value2");
/* 163:359 */     matrix.addHeader("header2", "value3");
/* 164:362 */     for (int i = 0; i < matrix.getRowCount(); i++) {
/* 165:363 */       for (int n = 0; n < matrix.getColCount(); n++)
/* 166:    */       {
/* 167:364 */         matrix.setMean(n, i, (i + 1) * n);
/* 168:365 */         matrix.setStdDev(n, i, (i + 1) * n / 100.0D);
/* 169:366 */         if (i == n) {
/* 170:367 */           if (i % 2 == 1) {
/* 171:368 */             matrix.setSignificance(n, i, 1);
/* 172:    */           } else {
/* 173:370 */             matrix.setSignificance(n, i, 2);
/* 174:    */           }
/* 175:    */         }
/* 176:    */       }
/* 177:    */     }
/* 178:375 */     System.out.println("\n\n--> " + matrix.getDisplayName());
/* 179:    */     
/* 180:377 */     System.out.println("\n1. complete\n");
/* 181:378 */     System.out.println(matrix.toStringHeader() + "\n");
/* 182:379 */     System.out.println(matrix.toStringMatrix() + "\n");
/* 183:380 */     System.out.println(matrix.toStringKey());
/* 184:    */     
/* 185:382 */     System.out.println("\n2. complete with std deviations\n");
/* 186:383 */     matrix.setShowStdDev(true);
/* 187:384 */     System.out.println(matrix.toStringMatrix());
/* 188:    */     
/* 189:386 */     System.out.println("\n3. cols numbered\n");
/* 190:387 */     matrix.setPrintColNames(false);
/* 191:388 */     System.out.println(matrix.toStringMatrix());
/* 192:    */     
/* 193:390 */     System.out.println("\n4. second col missing\n");
/* 194:391 */     matrix.setColHidden(1, true);
/* 195:392 */     System.out.println(matrix.toStringMatrix());
/* 196:    */     
/* 197:394 */     System.out.println("\n5. last row missing, rows numbered too\n");
/* 198:395 */     matrix.setRowHidden(2, true);
/* 199:396 */     matrix.setPrintRowNames(false);
/* 200:397 */     System.out.println(matrix.toStringMatrix());
/* 201:    */     
/* 202:399 */     System.out.println("\n6. mean prec to 3\n");
/* 203:400 */     matrix.setMeanPrec(3);
/* 204:401 */     matrix.setPrintRowNames(false);
/* 205:402 */     System.out.println(matrix.toStringMatrix());
/* 206:    */   }
/* 207:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.experiment.ResultMatrixCSV
 * JD-Core Version:    0.7.0.1
 */