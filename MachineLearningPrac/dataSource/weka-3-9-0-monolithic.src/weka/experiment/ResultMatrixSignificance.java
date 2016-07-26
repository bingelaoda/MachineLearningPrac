/*   1:    */ package weka.experiment;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import weka.core.RevisionUtils;
/*   5:    */ 
/*   6:    */ public class ResultMatrixSignificance
/*   7:    */   extends ResultMatrix
/*   8:    */ {
/*   9:    */   private static final long serialVersionUID = -1280545644109764206L;
/*  10:    */   
/*  11:    */   public ResultMatrixSignificance()
/*  12:    */   {
/*  13:113 */     this(1, 1);
/*  14:    */   }
/*  15:    */   
/*  16:    */   public ResultMatrixSignificance(int cols, int rows)
/*  17:    */   {
/*  18:123 */     super(cols, rows);
/*  19:    */   }
/*  20:    */   
/*  21:    */   public ResultMatrixSignificance(ResultMatrix matrix)
/*  22:    */   {
/*  23:132 */     super(matrix);
/*  24:    */   }
/*  25:    */   
/*  26:    */   public String globalInfo()
/*  27:    */   {
/*  28:142 */     return "Only outputs the significance indicators. Can be used for spotting patterns.";
/*  29:    */   }
/*  30:    */   
/*  31:    */   public String getDisplayName()
/*  32:    */   {
/*  33:151 */     return "Significance only";
/*  34:    */   }
/*  35:    */   
/*  36:    */   public boolean getDefaultPrintColNames()
/*  37:    */   {
/*  38:160 */     return false;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public int getDefaultRowNameWidth()
/*  42:    */   {
/*  43:169 */     return 40;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public boolean getDefaultShowStdDev()
/*  47:    */   {
/*  48:178 */     return false;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public void setShowStdDev(boolean show) {}
/*  52:    */   
/*  53:    */   public String toStringMatrix()
/*  54:    */   {
/*  55:205 */     StringBuffer result = new StringBuffer();
/*  56:206 */     String[][] cells = toArray();
/*  57:    */     
/*  58:    */ 
/*  59:209 */     int nameWidth = getColSize(cells, 0);
/*  60:210 */     for (int i = 0; i < cells.length - 1; i++) {
/*  61:211 */       cells[i][0] = padString(cells[i][0], nameWidth);
/*  62:    */     }
/*  63:214 */     int rows = cells.length - 1;
/*  64:215 */     if (getShowAverage()) {
/*  65:216 */       rows--;
/*  66:    */     }
/*  67:218 */     for (i = 0; i < rows; i++)
/*  68:    */     {
/*  69:219 */       String line = "";
/*  70:220 */       String colStr = "";
/*  71:222 */       for (int n = 0; n < cells[i].length; n++)
/*  72:    */       {
/*  73:224 */         if ((isMean(n)) || (isRowName(n))) {
/*  74:225 */           colStr = cells[0][n];
/*  75:    */         }
/*  76:227 */         if ((n <= 1) || (isSignificance(n)))
/*  77:    */         {
/*  78:231 */           if (n > 0) {
/*  79:232 */             line = line + " ";
/*  80:    */           }
/*  81:234 */           if ((i > 0) && (n > 1)) {
/*  82:235 */             line = line + " ";
/*  83:    */           }
/*  84:237 */           if (i == 0)
/*  85:    */           {
/*  86:238 */             line = line + colStr;
/*  87:    */           }
/*  88:241 */           else if (n == 0)
/*  89:    */           {
/*  90:242 */             line = line + cells[i][n];
/*  91:    */           }
/*  92:244 */           else if (n == 1)
/*  93:    */           {
/*  94:245 */             line = line + colStr.replaceAll(".", " ");
/*  95:    */           }
/*  96:    */           else
/*  97:    */           {
/*  98:248 */             line = line + cells[i][n];
/*  99:    */             
/* 100:250 */             line = line + colStr.replaceAll(".", " ").substring(2);
/* 101:    */           }
/* 102:    */         }
/* 103:    */       }
/* 104:254 */       result.append(line + "\n");
/* 105:257 */       if (i == 0) {
/* 106:258 */         result.append(line.replaceAll(".", "-") + "\n");
/* 107:    */       }
/* 108:    */     }
/* 109:261 */     return result.toString();
/* 110:    */   }
/* 111:    */   
/* 112:    */   public String toStringHeader()
/* 113:    */   {
/* 114:272 */     return new ResultMatrixPlainText(this).toStringHeader();
/* 115:    */   }
/* 116:    */   
/* 117:    */   public String toStringKey()
/* 118:    */   {
/* 119:282 */     return new ResultMatrixPlainText(this).toStringKey();
/* 120:    */   }
/* 121:    */   
/* 122:    */   public String toStringSummary()
/* 123:    */   {
/* 124:291 */     return new ResultMatrixPlainText(this).toStringSummary();
/* 125:    */   }
/* 126:    */   
/* 127:    */   public String toStringRanking()
/* 128:    */   {
/* 129:300 */     return new ResultMatrixPlainText(this).toStringRanking();
/* 130:    */   }
/* 131:    */   
/* 132:    */   public String getRevision()
/* 133:    */   {
/* 134:309 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 135:    */   }
/* 136:    */   
/* 137:    */   public static void main(String[] args)
/* 138:    */   {
/* 139:322 */     ResultMatrix matrix = new ResultMatrixSignificance(3, 3);
/* 140:    */     
/* 141:    */ 
/* 142:325 */     matrix.addHeader("header1", "value1");
/* 143:326 */     matrix.addHeader("header2", "value2");
/* 144:327 */     matrix.addHeader("header2", "value3");
/* 145:330 */     for (int i = 0; i < matrix.getRowCount(); i++) {
/* 146:331 */       for (int n = 0; n < matrix.getColCount(); n++)
/* 147:    */       {
/* 148:332 */         matrix.setMean(n, i, (i + 1) * n);
/* 149:333 */         matrix.setStdDev(n, i, (i + 1) * n / 100.0D);
/* 150:334 */         if (i == n) {
/* 151:335 */           if (i % 2 == 1) {
/* 152:336 */             matrix.setSignificance(n, i, 1);
/* 153:    */           } else {
/* 154:338 */             matrix.setSignificance(n, i, 2);
/* 155:    */           }
/* 156:    */         }
/* 157:    */       }
/* 158:    */     }
/* 159:343 */     System.out.println("\n\n--> " + matrix.getDisplayName());
/* 160:    */     
/* 161:345 */     System.out.println("\n1. complete\n");
/* 162:346 */     System.out.println(matrix.toStringHeader() + "\n");
/* 163:347 */     System.out.println(matrix.toStringMatrix() + "\n");
/* 164:348 */     System.out.println(matrix.toStringKey());
/* 165:    */     
/* 166:350 */     System.out.println("\n2. complete with std deviations\n");
/* 167:351 */     matrix.setShowStdDev(true);
/* 168:352 */     System.out.println(matrix.toStringMatrix());
/* 169:    */     
/* 170:354 */     System.out.println("\n3. cols numbered\n");
/* 171:355 */     matrix.setPrintColNames(false);
/* 172:356 */     System.out.println(matrix.toStringMatrix());
/* 173:    */     
/* 174:358 */     System.out.println("\n4. second col missing\n");
/* 175:359 */     matrix.setColHidden(1, true);
/* 176:360 */     System.out.println(matrix.toStringMatrix());
/* 177:    */     
/* 178:362 */     System.out.println("\n5. last row missing, rows numbered too\n");
/* 179:363 */     matrix.setRowHidden(2, true);
/* 180:364 */     matrix.setPrintRowNames(false);
/* 181:365 */     System.out.println(matrix.toStringMatrix());
/* 182:    */     
/* 183:367 */     System.out.println("\n6. mean prec to 3\n");
/* 184:368 */     matrix.setMeanPrec(3);
/* 185:369 */     matrix.setPrintRowNames(false);
/* 186:370 */     System.out.println(matrix.toStringMatrix());
/* 187:    */   }
/* 188:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.experiment.ResultMatrixSignificance
 * JD-Core Version:    0.7.0.1
 */