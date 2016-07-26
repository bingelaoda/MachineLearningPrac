/*   1:    */ package weka.experiment;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import weka.core.RevisionUtils;
/*   5:    */ import weka.core.Utils;
/*   6:    */ import weka.core.Version;
/*   7:    */ 
/*   8:    */ public class ResultMatrixGnuPlot
/*   9:    */   extends ResultMatrix
/*  10:    */ {
/*  11:    */   private static final long serialVersionUID = -234648254944790097L;
/*  12:    */   
/*  13:    */   public ResultMatrixGnuPlot()
/*  14:    */   {
/*  15:115 */     this(1, 1);
/*  16:    */   }
/*  17:    */   
/*  18:    */   public ResultMatrixGnuPlot(int cols, int rows)
/*  19:    */   {
/*  20:125 */     super(cols, rows);
/*  21:    */   }
/*  22:    */   
/*  23:    */   public ResultMatrixGnuPlot(ResultMatrix matrix)
/*  24:    */   {
/*  25:134 */     super(matrix);
/*  26:    */   }
/*  27:    */   
/*  28:    */   public String globalInfo()
/*  29:    */   {
/*  30:144 */     return "Generates output for a data and script file for GnuPlot.";
/*  31:    */   }
/*  32:    */   
/*  33:    */   public String getDisplayName()
/*  34:    */   {
/*  35:153 */     return "GNUPlot";
/*  36:    */   }
/*  37:    */   
/*  38:    */   public void clear()
/*  39:    */   {
/*  40:160 */     super.clear();
/*  41:161 */     this.LEFT_PARENTHESES = "";
/*  42:162 */     this.RIGHT_PARENTHESES = "";
/*  43:    */   }
/*  44:    */   
/*  45:    */   public int getDefaultRowNameWidth()
/*  46:    */   {
/*  47:171 */     return 50;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public int getDefaultColNameWidth()
/*  51:    */   {
/*  52:180 */     return 50;
/*  53:    */   }
/*  54:    */   
/*  55:    */   public boolean getDefaultEnumerateColNames()
/*  56:    */   {
/*  57:189 */     return false;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public boolean getDefaultEnumerateRowNames()
/*  61:    */   {
/*  62:198 */     return false;
/*  63:    */   }
/*  64:    */   
/*  65:    */   public String toStringHeader()
/*  66:    */   {
/*  67:209 */     return new ResultMatrixPlainText(this).toStringHeader();
/*  68:    */   }
/*  69:    */   
/*  70:    */   public String toStringMatrix()
/*  71:    */   {
/*  72:226 */     StringBuffer result = new StringBuffer();
/*  73:227 */     String[][] cells = toArray();
/*  74:    */     
/*  75:    */ 
/*  76:230 */     String generated = "# generated by WEKA " + Version.VERSION + "\n";
/*  77:    */     
/*  78:    */ 
/*  79:233 */     result.append("\n");
/*  80:234 */     result.append("##################\n");
/*  81:235 */     result.append("# file: plot.dat #\n");
/*  82:236 */     result.append("##################\n");
/*  83:237 */     result.append(generated);
/*  84:238 */     result.append("# contains the data for the plot\n");
/*  85:    */     
/*  86:240 */     result.append("\n");
/*  87:241 */     result.append("# key for the x-axis\n");
/*  88:242 */     for (int i = 1; i < cells.length - 1; i++) {
/*  89:243 */       result.append("# " + i + " - " + cells[i][0] + "\n");
/*  90:    */     }
/*  91:245 */     result.append("\n");
/*  92:246 */     result.append("# data for the plot\n");
/*  93:247 */     for (i = 1; i < cells.length - 1; i++)
/*  94:    */     {
/*  95:248 */       result.append(Integer.toString(i));
/*  96:249 */       for (int n = 1; n < cells[i].length; n++) {
/*  97:250 */         if (!isSignificance(n))
/*  98:    */         {
/*  99:252 */           result.append(" ");
/* 100:253 */           result.append(Utils.quote(cells[i][n]));
/* 101:    */         }
/* 102:    */       }
/* 103:255 */       result.append("\n");
/* 104:    */     }
/* 105:257 */     result.append("#######\n");
/* 106:258 */     result.append("# end #\n");
/* 107:259 */     result.append("#######\n");
/* 108:    */     
/* 109:    */ 
/* 110:262 */     result.append("\n");
/* 111:263 */     result.append("##################\n");
/* 112:264 */     result.append("# file: plot.scr #\n");
/* 113:265 */     result.append("##################\n");
/* 114:266 */     result.append(generated);
/* 115:267 */     result.append("# script to plot the data\n");
/* 116:268 */     result.append("\n");
/* 117:269 */     result.append("# display it in a window:\n");
/* 118:270 */     result.append("set terminal x11\n");
/* 119:271 */     result.append("set output\n");
/* 120:272 */     result.append("\n");
/* 121:273 */     result.append("# to display all data rows:\n");
/* 122:274 */     result.append("set xrange [0:" + (cells.length - 2 + 1) + "]\n");
/* 123:275 */     result.append("\n");
/* 124:276 */     result.append("# axis labels, e.g.:\n");
/* 125:277 */     result.append("#set xlabel \"Datasets\"\n");
/* 126:278 */     result.append("#set ylabel \"Accuracy in %\"\n");
/* 127:279 */     result.append("\n");
/* 128:280 */     result.append("# the plot commands\n");
/* 129:281 */     int n = 1;
/* 130:282 */     i = 0;
/* 131:283 */     while (i < cells[0].length - 1)
/* 132:    */     {
/* 133:284 */       i++;
/* 134:286 */       if (!isSignificance(i))
/* 135:    */       {
/* 136:289 */         n++;
/* 137:    */         String line;
/* 138:292 */         if (i == 1) {
/* 139:293 */           line = "plot";
/* 140:    */         } else {
/* 141:295 */           line = "replot";
/* 142:    */         }
/* 143:296 */         String line = line + " \"plot.dat\"";
/* 144:    */         
/* 145:    */ 
/* 146:299 */         String title = "title \"" + cells[0][i] + "\"";
/* 147:    */         
/* 148:    */ 
/* 149:302 */         line = line + " using 1:" + n;
/* 150:303 */         if (getShowStdDev())
/* 151:    */         {
/* 152:304 */           n++;
/* 153:305 */           i++;
/* 154:    */           
/* 155:307 */           line = line + ":" + n;
/* 156:    */         }
/* 157:311 */         line = line + " with";
/* 158:312 */         if (getShowStdDev()) {
/* 159:313 */           line = line + " yerrorbars";
/* 160:    */         } else {
/* 161:315 */           line = line + " lines";
/* 162:    */         }
/* 163:316 */         line = line + " " + title;
/* 164:    */         
/* 165:318 */         result.append(line + "\n");
/* 166:    */       }
/* 167:    */     }
/* 168:320 */     result.append("\n");
/* 169:321 */     result.append("# generate ps:\n");
/* 170:322 */     result.append("#set terminal postscript\n");
/* 171:323 */     result.append("#set output \"plot.ps\"\n");
/* 172:324 */     result.append("#replot\n");
/* 173:325 */     result.append("\n");
/* 174:326 */     result.append("# generate png:\n");
/* 175:327 */     result.append("#set terminal png size 800,600\n");
/* 176:328 */     result.append("#set output \"plot.png\"\n");
/* 177:329 */     result.append("#replot\n");
/* 178:330 */     result.append("\n");
/* 179:331 */     result.append("# wait for user to hit <Return>\n");
/* 180:332 */     result.append("pause -1\n");
/* 181:333 */     result.append("#######\n");
/* 182:334 */     result.append("# end #\n");
/* 183:335 */     result.append("#######\n");
/* 184:    */     
/* 185:337 */     return result.toString();
/* 186:    */   }
/* 187:    */   
/* 188:    */   public String toStringKey()
/* 189:    */   {
/* 190:347 */     return new ResultMatrixPlainText(this).toStringKey();
/* 191:    */   }
/* 192:    */   
/* 193:    */   public String toStringSummary()
/* 194:    */   {
/* 195:356 */     return new ResultMatrixPlainText(this).toStringSummary();
/* 196:    */   }
/* 197:    */   
/* 198:    */   public String toStringRanking()
/* 199:    */   {
/* 200:365 */     return new ResultMatrixPlainText(this).toStringRanking();
/* 201:    */   }
/* 202:    */   
/* 203:    */   public String getRevision()
/* 204:    */   {
/* 205:374 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 206:    */   }
/* 207:    */   
/* 208:    */   public static void main(String[] args)
/* 209:    */   {
/* 210:387 */     ResultMatrix matrix = new ResultMatrixGnuPlot(3, 3);
/* 211:    */     
/* 212:    */ 
/* 213:390 */     matrix.addHeader("header1", "value1");
/* 214:391 */     matrix.addHeader("header2", "value2");
/* 215:392 */     matrix.addHeader("header2", "value3");
/* 216:395 */     for (int i = 0; i < matrix.getRowCount(); i++) {
/* 217:396 */       for (int n = 0; n < matrix.getColCount(); n++)
/* 218:    */       {
/* 219:397 */         matrix.setMean(n, i, (i + 1) * n);
/* 220:398 */         matrix.setStdDev(n, i, (i + 1) * n / 100.0D);
/* 221:399 */         if (i == n) {
/* 222:400 */           if (i % 2 == 1) {
/* 223:401 */             matrix.setSignificance(n, i, 1);
/* 224:    */           } else {
/* 225:403 */             matrix.setSignificance(n, i, 2);
/* 226:    */           }
/* 227:    */         }
/* 228:    */       }
/* 229:    */     }
/* 230:408 */     System.out.println("\n\n--> " + matrix.getDisplayName());
/* 231:    */     
/* 232:410 */     System.out.println("\n1. complete\n");
/* 233:411 */     System.out.println(matrix.toStringHeader() + "\n");
/* 234:412 */     System.out.println(matrix.toStringMatrix() + "\n");
/* 235:413 */     System.out.println(matrix.toStringKey());
/* 236:    */     
/* 237:415 */     System.out.println("\n2. complete with std deviations\n");
/* 238:416 */     matrix.setShowStdDev(true);
/* 239:417 */     System.out.println(matrix.toStringMatrix());
/* 240:    */     
/* 241:419 */     System.out.println("\n3. cols numbered\n");
/* 242:420 */     matrix.setPrintColNames(false);
/* 243:421 */     System.out.println(matrix.toStringMatrix());
/* 244:    */     
/* 245:423 */     System.out.println("\n4. second col missing\n");
/* 246:424 */     matrix.setColHidden(1, true);
/* 247:425 */     System.out.println(matrix.toStringMatrix());
/* 248:    */     
/* 249:427 */     System.out.println("\n5. last row missing, rows numbered too\n");
/* 250:428 */     matrix.setRowHidden(2, true);
/* 251:429 */     matrix.setPrintRowNames(false);
/* 252:430 */     System.out.println(matrix.toStringMatrix());
/* 253:    */     
/* 254:432 */     System.out.println("\n6. mean prec to 3\n");
/* 255:433 */     matrix.setMeanPrec(3);
/* 256:434 */     matrix.setPrintRowNames(false);
/* 257:435 */     System.out.println(matrix.toStringMatrix());
/* 258:    */   }
/* 259:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.experiment.ResultMatrixGnuPlot
 * JD-Core Version:    0.7.0.1
 */