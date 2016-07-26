/*   1:    */ package no.uib.cipr.matrix.io;
/*   2:    */ 
/*   3:    */ import java.io.OutputStream;
/*   4:    */ import java.io.PrintWriter;
/*   5:    */ import java.io.Writer;
/*   6:    */ import java.util.Locale;
/*   7:    */ 
/*   8:    */ public class MatrixVectorWriter
/*   9:    */   extends PrintWriter
/*  10:    */ {
/*  11:    */   public MatrixVectorWriter(OutputStream out)
/*  12:    */   {
/*  13: 39 */     super(out);
/*  14:    */   }
/*  15:    */   
/*  16:    */   public MatrixVectorWriter(OutputStream out, boolean autoFlush)
/*  17:    */   {
/*  18: 49 */     super(out, autoFlush);
/*  19:    */   }
/*  20:    */   
/*  21:    */   public MatrixVectorWriter(Writer out)
/*  22:    */   {
/*  23: 58 */     super(out);
/*  24:    */   }
/*  25:    */   
/*  26:    */   public MatrixVectorWriter(Writer out, boolean autoFlush)
/*  27:    */   {
/*  28: 68 */     super(out, autoFlush);
/*  29:    */   }
/*  30:    */   
/*  31:    */   public void add(int num, int[] indices)
/*  32:    */   {
/*  33: 81 */     for (int i = 0; i < indices.length; i++) {
/*  34: 82 */       indices[i] += num;
/*  35:    */     }
/*  36:    */   }
/*  37:    */   
/*  38:    */   public void printMatrixInfo(MatrixInfo info)
/*  39:    */   {
/*  40: 89 */     print(info.toString());
/*  41:    */   }
/*  42:    */   
/*  43:    */   public void printVectorInfo(VectorInfo info)
/*  44:    */   {
/*  45: 96 */     print(info.toString());
/*  46:    */   }
/*  47:    */   
/*  48:    */   public void printMatrixSize(MatrixSize size, MatrixInfo info)
/*  49:    */   {
/*  50:103 */     format(Locale.ENGLISH, "%10d %10d", new Object[] { Integer.valueOf(size.numRows()), Integer.valueOf(size.numColumns()) });
/*  51:104 */     if (info.isCoordinate()) {
/*  52:105 */       format(Locale.ENGLISH, " %19d", new Object[] { Integer.valueOf(size.numEntries()) });
/*  53:    */     }
/*  54:106 */     println();
/*  55:    */   }
/*  56:    */   
/*  57:    */   public void printMatrixSize(MatrixSize size)
/*  58:    */   {
/*  59:113 */     format(Locale.ENGLISH, "%10d %10d %19d\n", new Object[] { Integer.valueOf(size.numRows()), 
/*  60:114 */       Integer.valueOf(size.numColumns()), Integer.valueOf(size.numEntries()) });
/*  61:    */   }
/*  62:    */   
/*  63:    */   public void printVectorSize(VectorSize size, VectorInfo info)
/*  64:    */   {
/*  65:121 */     format(Locale.ENGLISH, "%10d", new Object[] { Integer.valueOf(size.size()) });
/*  66:122 */     if (info.isCoordinate()) {
/*  67:123 */       format(Locale.ENGLISH, " %19d", new Object[] { Integer.valueOf(size.numEntries()) });
/*  68:    */     }
/*  69:124 */     println();
/*  70:    */   }
/*  71:    */   
/*  72:    */   public void printVectorSize(VectorSize size)
/*  73:    */   {
/*  74:131 */     format(Locale.ENGLISH, "%10d %19d\n", new Object[] { Integer.valueOf(size.size()), Integer.valueOf(size.numEntries()) });
/*  75:    */   }
/*  76:    */   
/*  77:    */   public void printArray(float[] data)
/*  78:    */   {
/*  79:138 */     for (int i = 0; i < data.length; i++) {
/*  80:139 */       format(Locale.ENGLISH, "% .12e\n", new Object[] { Float.valueOf(data[i]) });
/*  81:    */     }
/*  82:    */   }
/*  83:    */   
/*  84:    */   public void printArray(double[] data)
/*  85:    */   {
/*  86:146 */     for (int i = 0; i < data.length; i++) {
/*  87:147 */       format(Locale.ENGLISH, "% .12e\n", new Object[] { Double.valueOf(data[i]) });
/*  88:    */     }
/*  89:    */   }
/*  90:    */   
/*  91:    */   public void printArray(float[] dataR, float[] dataI)
/*  92:    */   {
/*  93:155 */     int size = dataR.length;
/*  94:156 */     if (size != dataI.length) {
/*  95:157 */       throw new IllegalArgumentException("All arrays must be of the same size");
/*  96:    */     }
/*  97:159 */     for (int i = 0; i < size; i++) {
/*  98:160 */       format(Locale.ENGLISH, "% .12e % .12e\n", new Object[] { Float.valueOf(dataR[i]), Float.valueOf(dataI[i]) });
/*  99:    */     }
/* 100:    */   }
/* 101:    */   
/* 102:    */   public void printArray(double[] dataR, double[] dataI)
/* 103:    */   {
/* 104:168 */     int size = dataR.length;
/* 105:169 */     if (size != dataI.length) {
/* 106:170 */       throw new IllegalArgumentException("All arrays must be of the same size");
/* 107:    */     }
/* 108:172 */     for (int i = 0; i < size; i++) {
/* 109:173 */       format(Locale.ENGLISH, "% .12e % .12e\n", new Object[] { Double.valueOf(dataR[i]), Double.valueOf(dataI[i]) });
/* 110:    */     }
/* 111:    */   }
/* 112:    */   
/* 113:    */   public void printArray(int[] data)
/* 114:    */   {
/* 115:180 */     for (int i = 0; i < data.length; i++) {
/* 116:181 */       format(Locale.ENGLISH, "%10d\n", new Object[] { Integer.valueOf(data[i]) });
/* 117:    */     }
/* 118:    */   }
/* 119:    */   
/* 120:    */   public void printArray(long[] data)
/* 121:    */   {
/* 122:188 */     for (int i = 0; i < data.length; i++) {
/* 123:189 */       format(Locale.ENGLISH, "%10d\n", new Object[] { Long.valueOf(data[i]) });
/* 124:    */     }
/* 125:    */   }
/* 126:    */   
/* 127:    */   public void printCoordinate(int[] index, float[] data, int offset)
/* 128:    */   {
/* 129:198 */     int size = index.length;
/* 130:199 */     if (size != data.length) {
/* 131:200 */       throw new IllegalArgumentException("All arrays must be of the same size");
/* 132:    */     }
/* 133:202 */     for (int i = 0; i < size; i++) {
/* 134:203 */       format(Locale.ENGLISH, "%10d % .12e\n", new Object[] { Integer.valueOf(index[i] + offset), Float.valueOf(data[i]) });
/* 135:    */     }
/* 136:    */   }
/* 137:    */   
/* 138:    */   public void printCoordinate(int[] index, double[] data, int offset)
/* 139:    */   {
/* 140:212 */     int size = index.length;
/* 141:213 */     if (size != data.length) {
/* 142:214 */       throw new IllegalArgumentException("All arrays must be of the same size");
/* 143:    */     }
/* 144:216 */     for (int i = 0; i < size; i++) {
/* 145:217 */       format(Locale.ENGLISH, "%10d % .12e\n", new Object[] { Integer.valueOf(index[i] + offset), Double.valueOf(data[i]) });
/* 146:    */     }
/* 147:    */   }
/* 148:    */   
/* 149:    */   public void printCoordinate(int[] index, int[] data, int offset)
/* 150:    */   {
/* 151:226 */     int size = index.length;
/* 152:227 */     if (size != data.length) {
/* 153:228 */       throw new IllegalArgumentException("All arrays must be of the same size");
/* 154:    */     }
/* 155:230 */     for (int i = 0; i < size; i++) {
/* 156:231 */       format(Locale.ENGLISH, "%10d %10d\n", new Object[] { Integer.valueOf(index[i] + offset), Integer.valueOf(data[i]) });
/* 157:    */     }
/* 158:    */   }
/* 159:    */   
/* 160:    */   public void printCoordinate(int[] index, long[] data, int offset)
/* 161:    */   {
/* 162:240 */     int size = index.length;
/* 163:241 */     if (size != data.length) {
/* 164:242 */       throw new IllegalArgumentException("All arrays must be of the same size");
/* 165:    */     }
/* 166:244 */     for (int i = 0; i < size; i++) {
/* 167:245 */       format(Locale.ENGLISH, "%10d %10d\n", new Object[] { Integer.valueOf(index[i] + offset), Long.valueOf(data[i]) });
/* 168:    */     }
/* 169:    */   }
/* 170:    */   
/* 171:    */   public void printCoordinate(int[] row, int[] column, float[] data, int offset)
/* 172:    */   {
/* 173:255 */     int size = row.length;
/* 174:256 */     if ((size != column.length) || (size != data.length)) {
/* 175:257 */       throw new IllegalArgumentException("All arrays must be of the same size");
/* 176:    */     }
/* 177:259 */     for (int i = 0; i < size; i++) {
/* 178:260 */       format(Locale.ENGLISH, "%10d %10d % .12e\n", new Object[] { Integer.valueOf(row[i] + offset), 
/* 179:261 */         Integer.valueOf(column[i] + offset), Float.valueOf(data[i]) });
/* 180:    */     }
/* 181:    */   }
/* 182:    */   
/* 183:    */   public void printCoordinate(int[] row, int[] column, double[] data, int offset)
/* 184:    */   {
/* 185:271 */     int size = row.length;
/* 186:272 */     if ((size != column.length) || (size != data.length)) {
/* 187:273 */       throw new IllegalArgumentException("All arrays must be of the same size");
/* 188:    */     }
/* 189:275 */     for (int i = 0; i < size; i++) {
/* 190:276 */       format(Locale.ENGLISH, "%10d %10d % .12e\n", new Object[] { Integer.valueOf(row[i] + offset), 
/* 191:277 */         Integer.valueOf(column[i] + offset), Double.valueOf(data[i]) });
/* 192:    */     }
/* 193:    */   }
/* 194:    */   
/* 195:    */   public void printCoordinate(int[] index, float[] dataR, float[] dataI, int offset)
/* 196:    */   {
/* 197:288 */     int size = index.length;
/* 198:289 */     if ((size != dataR.length) || (size != dataI.length)) {
/* 199:290 */       throw new IllegalArgumentException("All arrays must be of the same size");
/* 200:    */     }
/* 201:292 */     for (int i = 0; i < size; i++) {
/* 202:293 */       format(Locale.ENGLISH, "%10d % .12e % .12e\n", new Object[] { Integer.valueOf(index[i] + offset), 
/* 203:294 */         Float.valueOf(dataR[i]), Float.valueOf(dataI[i]) });
/* 204:    */     }
/* 205:    */   }
/* 206:    */   
/* 207:    */   public void printCoordinate(int[] index, double[] dataR, double[] dataI, int offset)
/* 208:    */   {
/* 209:305 */     int size = index.length;
/* 210:306 */     if ((size != dataR.length) || (size != dataI.length)) {
/* 211:307 */       throw new IllegalArgumentException("All arrays must be of the same size");
/* 212:    */     }
/* 213:309 */     for (int i = 0; i < size; i++) {
/* 214:310 */       format(Locale.ENGLISH, "%10d % .12e % .12e\n", new Object[] { Integer.valueOf(index[i] + offset), 
/* 215:311 */         Double.valueOf(dataR[i]), Double.valueOf(dataI[i]) });
/* 216:    */     }
/* 217:    */   }
/* 218:    */   
/* 219:    */   public void printCoordinate(int[] row, int[] column, float[] dataR, float[] dataI, int offset)
/* 220:    */   {
/* 221:322 */     int size = row.length;
/* 222:323 */     if ((size != column.length) || (size != dataR.length) || (size != dataI.length)) {
/* 223:325 */       throw new IllegalArgumentException("All arrays must be of the same size");
/* 224:    */     }
/* 225:327 */     for (int i = 0; i < size; i++) {
/* 226:328 */       format(Locale.ENGLISH, "%10d %10d % .12e % .12e\n", new Object[] {
/* 227:329 */         Integer.valueOf(row[i] + offset), Integer.valueOf(column[i] + offset), Float.valueOf(dataR[i]), Float.valueOf(dataI[i]) });
/* 228:    */     }
/* 229:    */   }
/* 230:    */   
/* 231:    */   public void printCoordinate(int[] row, int[] column, double[] dataR, double[] dataI, int offset)
/* 232:    */   {
/* 233:340 */     int size = row.length;
/* 234:341 */     if ((size != column.length) || (size != dataR.length) || (size != dataI.length)) {
/* 235:343 */       throw new IllegalArgumentException("All arrays must be of the same size");
/* 236:    */     }
/* 237:345 */     for (int i = 0; i < size; i++) {
/* 238:346 */       format(Locale.ENGLISH, "%10d %10d % .12e % .12e\n", new Object[] {
/* 239:347 */         Integer.valueOf(row[i] + offset), Integer.valueOf(column[i] + offset), Double.valueOf(dataR[i]), Double.valueOf(dataI[i]) });
/* 240:    */     }
/* 241:    */   }
/* 242:    */   
/* 243:    */   public void printCoordinate(int[] row, int[] column, int[] data, int offset)
/* 244:    */   {
/* 245:356 */     int size = row.length;
/* 246:357 */     if ((size != column.length) || (size != data.length)) {
/* 247:358 */       throw new IllegalArgumentException("All arrays must be of the same size");
/* 248:    */     }
/* 249:360 */     for (int i = 0; i < size; i++) {
/* 250:361 */       format(Locale.ENGLISH, "%10d %10d %19d\n", new Object[] { Integer.valueOf(row[i] + offset), 
/* 251:362 */         Integer.valueOf(column[i] + offset), Integer.valueOf(data[i]) });
/* 252:    */     }
/* 253:    */   }
/* 254:    */   
/* 255:    */   public void printCoordinate(int[] row, int[] column, long[] data, int offset)
/* 256:    */   {
/* 257:371 */     int size = row.length;
/* 258:372 */     if ((size != column.length) || (size != data.length)) {
/* 259:373 */       throw new IllegalArgumentException("All arrays must be of the same size");
/* 260:    */     }
/* 261:375 */     for (int i = 0; i < size; i++) {
/* 262:376 */       format(Locale.ENGLISH, "%10d %10d %19d\n", new Object[] { Integer.valueOf(row[i] + offset), 
/* 263:377 */         Integer.valueOf(column[i] + offset), Long.valueOf(data[i]) });
/* 264:    */     }
/* 265:    */   }
/* 266:    */   
/* 267:    */   public void printPattern(int[] row, int[] column, int offset)
/* 268:    */   {
/* 269:386 */     int size = row.length;
/* 270:387 */     if (size != column.length) {
/* 271:388 */       throw new IllegalArgumentException("All arrays must be of the same size");
/* 272:    */     }
/* 273:390 */     for (int i = 0; i < size; i++) {
/* 274:391 */       format(Locale.ENGLISH, "%10d %10d\n", new Object[] { Integer.valueOf(row[i] + offset), Integer.valueOf(column[i] + offset) });
/* 275:    */     }
/* 276:    */   }
/* 277:    */   
/* 278:    */   public void printPattern(int[] index, int offset)
/* 279:    */   {
/* 280:401 */     int size = index.length;
/* 281:402 */     for (int i = 0; i < size; i++) {
/* 282:403 */       format(Locale.ENGLISH, "%10d\n", new Object[] { Integer.valueOf(index[i] + offset) });
/* 283:    */     }
/* 284:    */   }
/* 285:    */   
/* 286:    */   public void printCoordinate(int[] row, int[] column, float[] data)
/* 287:    */   {
/* 288:411 */     printCoordinate(row, column, data, 0);
/* 289:    */   }
/* 290:    */   
/* 291:    */   public void printCoordinate(int[] row, int[] column, double[] data)
/* 292:    */   {
/* 293:419 */     printCoordinate(row, column, data, 0);
/* 294:    */   }
/* 295:    */   
/* 296:    */   public void printCoordinate(int[] row, int[] column, float[] dataR, float[] dataI)
/* 297:    */   {
/* 298:429 */     printCoordinate(row, column, dataR, dataI, 0);
/* 299:    */   }
/* 300:    */   
/* 301:    */   public void printCoordinate(int[] row, int[] column, double[] dataR, double[] dataI)
/* 302:    */   {
/* 303:439 */     printCoordinate(row, column, dataR, dataI, 0);
/* 304:    */   }
/* 305:    */   
/* 306:    */   public void printCoordinate(int[] row, int[] column, int[] data)
/* 307:    */   {
/* 308:447 */     printCoordinate(row, column, data, 0);
/* 309:    */   }
/* 310:    */   
/* 311:    */   public void printCoordinate(int[] row, int[] column, long[] data)
/* 312:    */   {
/* 313:455 */     printCoordinate(row, column, data, 0);
/* 314:    */   }
/* 315:    */   
/* 316:    */   public void printPattern(int[] row, int[] column)
/* 317:    */   {
/* 318:463 */     printPattern(row, column, 0);
/* 319:    */   }
/* 320:    */   
/* 321:    */   public void printCoordinate(int[] index, float[] data)
/* 322:    */   {
/* 323:471 */     printCoordinate(index, data, 0);
/* 324:    */   }
/* 325:    */   
/* 326:    */   public void printCoordinate(int[] index, double[] data)
/* 327:    */   {
/* 328:479 */     printCoordinate(index, data, 0);
/* 329:    */   }
/* 330:    */   
/* 331:    */   public void printCoordinate(int[] index, float[] dataR, float[] dataI)
/* 332:    */   {
/* 333:488 */     printCoordinate(index, dataR, dataI, 0);
/* 334:    */   }
/* 335:    */   
/* 336:    */   public void printCoordinate(int[] index, double[] dataR, double[] dataI)
/* 337:    */   {
/* 338:497 */     printCoordinate(index, dataR, dataI, 0);
/* 339:    */   }
/* 340:    */   
/* 341:    */   public void printCoordinate(int[] index, int[] data)
/* 342:    */   {
/* 343:505 */     printCoordinate(index, data, 0);
/* 344:    */   }
/* 345:    */   
/* 346:    */   public void printCoordinate(int[] index, long[] data)
/* 347:    */   {
/* 348:513 */     printCoordinate(index, data, 0);
/* 349:    */   }
/* 350:    */   
/* 351:    */   public void printPattern(int[] index)
/* 352:    */   {
/* 353:520 */     printPattern(index, 0);
/* 354:    */   }
/* 355:    */   
/* 356:    */   public void printComments(String[] comments)
/* 357:    */   {
/* 358:528 */     for (String comment : comments) {
/* 359:529 */       println("%" + comment);
/* 360:    */     }
/* 361:    */   }
/* 362:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.io.MatrixVectorWriter
 * JD-Core Version:    0.7.0.1
 */