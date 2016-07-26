/*   1:    */ package com.github.fommil.netlib;
/*   2:    */ 
/*   3:    */ import org.netlib.blas.Dasum;
/*   4:    */ import org.netlib.blas.Daxpy;
/*   5:    */ import org.netlib.blas.Dcopy;
/*   6:    */ import org.netlib.blas.Ddot;
/*   7:    */ import org.netlib.blas.Dgbmv;
/*   8:    */ import org.netlib.blas.Dgemm;
/*   9:    */ import org.netlib.blas.Dgemv;
/*  10:    */ import org.netlib.blas.Dger;
/*  11:    */ import org.netlib.blas.Dnrm2;
/*  12:    */ import org.netlib.blas.Drot;
/*  13:    */ import org.netlib.blas.Drotg;
/*  14:    */ import org.netlib.blas.Drotm;
/*  15:    */ import org.netlib.blas.Drotmg;
/*  16:    */ import org.netlib.blas.Dsbmv;
/*  17:    */ import org.netlib.blas.Dscal;
/*  18:    */ import org.netlib.blas.Dspmv;
/*  19:    */ import org.netlib.blas.Dspr;
/*  20:    */ import org.netlib.blas.Dspr2;
/*  21:    */ import org.netlib.blas.Dswap;
/*  22:    */ import org.netlib.blas.Dsymm;
/*  23:    */ import org.netlib.blas.Dsymv;
/*  24:    */ import org.netlib.blas.Dsyr;
/*  25:    */ import org.netlib.blas.Dsyr2;
/*  26:    */ import org.netlib.blas.Dsyr2k;
/*  27:    */ import org.netlib.blas.Dsyrk;
/*  28:    */ import org.netlib.blas.Dtbmv;
/*  29:    */ import org.netlib.blas.Dtbsv;
/*  30:    */ import org.netlib.blas.Dtpmv;
/*  31:    */ import org.netlib.blas.Dtpsv;
/*  32:    */ import org.netlib.blas.Dtrmm;
/*  33:    */ import org.netlib.blas.Dtrmv;
/*  34:    */ import org.netlib.blas.Dtrsm;
/*  35:    */ import org.netlib.blas.Dtrsv;
/*  36:    */ import org.netlib.blas.Idamax;
/*  37:    */ import org.netlib.blas.Isamax;
/*  38:    */ import org.netlib.blas.Lsame;
/*  39:    */ import org.netlib.blas.Sasum;
/*  40:    */ import org.netlib.blas.Saxpy;
/*  41:    */ import org.netlib.blas.Scopy;
/*  42:    */ import org.netlib.blas.Sdot;
/*  43:    */ import org.netlib.blas.Sdsdot;
/*  44:    */ import org.netlib.blas.Sgbmv;
/*  45:    */ import org.netlib.blas.Sgemm;
/*  46:    */ import org.netlib.blas.Sgemv;
/*  47:    */ import org.netlib.blas.Sger;
/*  48:    */ import org.netlib.blas.Snrm2;
/*  49:    */ import org.netlib.blas.Srot;
/*  50:    */ import org.netlib.blas.Srotg;
/*  51:    */ import org.netlib.blas.Srotm;
/*  52:    */ import org.netlib.blas.Srotmg;
/*  53:    */ import org.netlib.blas.Ssbmv;
/*  54:    */ import org.netlib.blas.Sscal;
/*  55:    */ import org.netlib.blas.Sspmv;
/*  56:    */ import org.netlib.blas.Sspr;
/*  57:    */ import org.netlib.blas.Sspr2;
/*  58:    */ import org.netlib.blas.Sswap;
/*  59:    */ import org.netlib.blas.Ssymm;
/*  60:    */ import org.netlib.blas.Ssymv;
/*  61:    */ import org.netlib.blas.Ssyr;
/*  62:    */ import org.netlib.blas.Ssyr2;
/*  63:    */ import org.netlib.blas.Ssyr2k;
/*  64:    */ import org.netlib.blas.Ssyrk;
/*  65:    */ import org.netlib.blas.Stbmv;
/*  66:    */ import org.netlib.blas.Stbsv;
/*  67:    */ import org.netlib.blas.Stpmv;
/*  68:    */ import org.netlib.blas.Stpsv;
/*  69:    */ import org.netlib.blas.Strmm;
/*  70:    */ import org.netlib.blas.Strmv;
/*  71:    */ import org.netlib.blas.Strsm;
/*  72:    */ import org.netlib.blas.Strsv;
/*  73:    */ import org.netlib.util.doubleW;
/*  74:    */ import org.netlib.util.floatW;
/*  75:    */ 
/*  76:    */ public class F2jBLAS
/*  77:    */   extends BLAS
/*  78:    */ {
/*  79:    */   public double dasum(int n, double[] dx, int incx)
/*  80:    */   {
/*  81: 41 */     return Dasum.dasum(n, dx, 0, incx);
/*  82:    */   }
/*  83:    */   
/*  84:    */   public double dasum(int n, double[] dx, int _dx_offset, int incx)
/*  85:    */   {
/*  86: 46 */     return Dasum.dasum(n, dx, _dx_offset, incx);
/*  87:    */   }
/*  88:    */   
/*  89:    */   public void daxpy(int n, double da, double[] dx, int incx, double[] dy, int incy)
/*  90:    */   {
/*  91: 51 */     Daxpy.daxpy(n, da, dx, 0, incx, dy, 0, incy);
/*  92:    */   }
/*  93:    */   
/*  94:    */   public void daxpy(int n, double da, double[] dx, int _dx_offset, int incx, double[] dy, int _dy_offset, int incy)
/*  95:    */   {
/*  96: 56 */     Daxpy.daxpy(n, da, dx, _dx_offset, incx, dy, _dy_offset, incy);
/*  97:    */   }
/*  98:    */   
/*  99:    */   public void dcopy(int n, double[] dx, int incx, double[] dy, int incy)
/* 100:    */   {
/* 101: 61 */     Dcopy.dcopy(n, dx, 0, incx, dy, 0, incy);
/* 102:    */   }
/* 103:    */   
/* 104:    */   public void dcopy(int n, double[] dx, int _dx_offset, int incx, double[] dy, int _dy_offset, int incy)
/* 105:    */   {
/* 106: 66 */     Dcopy.dcopy(n, dx, _dx_offset, incx, dy, _dy_offset, incy);
/* 107:    */   }
/* 108:    */   
/* 109:    */   public double ddot(int n, double[] dx, int incx, double[] dy, int incy)
/* 110:    */   {
/* 111: 71 */     return Ddot.ddot(n, dx, 0, incx, dy, 0, incy);
/* 112:    */   }
/* 113:    */   
/* 114:    */   public double ddot(int n, double[] dx, int _dx_offset, int incx, double[] dy, int _dy_offset, int incy)
/* 115:    */   {
/* 116: 76 */     return Ddot.ddot(n, dx, _dx_offset, incx, dy, _dy_offset, incy);
/* 117:    */   }
/* 118:    */   
/* 119:    */   public void dgbmv(String trans, int m, int n, int kl, int ku, double alpha, double[] a, int lda, double[] x, int incx, double beta, double[] y, int incy)
/* 120:    */   {
/* 121: 81 */     Dgbmv.dgbmv(trans, m, n, kl, ku, alpha, a, 0, lda, x, 0, incx, beta, y, 0, incy);
/* 122:    */   }
/* 123:    */   
/* 124:    */   public void dgbmv(String trans, int m, int n, int kl, int ku, double alpha, double[] a, int _a_offset, int lda, double[] x, int _x_offset, int incx, double beta, double[] y, int _y_offset, int incy)
/* 125:    */   {
/* 126: 86 */     Dgbmv.dgbmv(trans, m, n, kl, ku, alpha, a, _a_offset, lda, x, _x_offset, incx, beta, y, _y_offset, incy);
/* 127:    */   }
/* 128:    */   
/* 129:    */   public void dgemm(String transa, String transb, int m, int n, int k, double alpha, double[] a, int lda, double[] b, int ldb, double beta, double[] c, int Ldc)
/* 130:    */   {
/* 131: 91 */     Dgemm.dgemm(transa, transb, m, n, k, alpha, a, 0, lda, b, 0, ldb, beta, c, 0, Ldc);
/* 132:    */   }
/* 133:    */   
/* 134:    */   public void dgemm(String transa, String transb, int m, int n, int k, double alpha, double[] a, int _a_offset, int lda, double[] b, int _b_offset, int ldb, double beta, double[] c, int _c_offset, int Ldc)
/* 135:    */   {
/* 136: 96 */     Dgemm.dgemm(transa, transb, m, n, k, alpha, a, _a_offset, lda, b, _b_offset, ldb, beta, c, _c_offset, Ldc);
/* 137:    */   }
/* 138:    */   
/* 139:    */   public void dgemv(String trans, int m, int n, double alpha, double[] a, int lda, double[] x, int incx, double beta, double[] y, int incy)
/* 140:    */   {
/* 141:101 */     Dgemv.dgemv(trans, m, n, alpha, a, 0, lda, x, 0, incx, beta, y, 0, incy);
/* 142:    */   }
/* 143:    */   
/* 144:    */   public void dgemv(String trans, int m, int n, double alpha, double[] a, int _a_offset, int lda, double[] x, int _x_offset, int incx, double beta, double[] y, int _y_offset, int incy)
/* 145:    */   {
/* 146:106 */     Dgemv.dgemv(trans, m, n, alpha, a, _a_offset, lda, x, _x_offset, incx, beta, y, _y_offset, incy);
/* 147:    */   }
/* 148:    */   
/* 149:    */   public void dger(int m, int n, double alpha, double[] x, int incx, double[] y, int incy, double[] a, int lda)
/* 150:    */   {
/* 151:111 */     Dger.dger(m, n, alpha, x, 0, incx, y, 0, incy, a, 0, lda);
/* 152:    */   }
/* 153:    */   
/* 154:    */   public void dger(int m, int n, double alpha, double[] x, int _x_offset, int incx, double[] y, int _y_offset, int incy, double[] a, int _a_offset, int lda)
/* 155:    */   {
/* 156:116 */     Dger.dger(m, n, alpha, x, _x_offset, incx, y, _y_offset, incy, a, _a_offset, lda);
/* 157:    */   }
/* 158:    */   
/* 159:    */   public double dnrm2(int n, double[] x, int incx)
/* 160:    */   {
/* 161:121 */     return Dnrm2.dnrm2(n, x, 0, incx);
/* 162:    */   }
/* 163:    */   
/* 164:    */   public double dnrm2(int n, double[] x, int _x_offset, int incx)
/* 165:    */   {
/* 166:126 */     return Dnrm2.dnrm2(n, x, _x_offset, incx);
/* 167:    */   }
/* 168:    */   
/* 169:    */   public void drot(int n, double[] dx, int incx, double[] dy, int incy, double c, double s)
/* 170:    */   {
/* 171:131 */     Drot.drot(n, dx, 0, incx, dy, 0, incy, c, s);
/* 172:    */   }
/* 173:    */   
/* 174:    */   public void drot(int n, double[] dx, int _dx_offset, int incx, double[] dy, int _dy_offset, int incy, double c, double s)
/* 175:    */   {
/* 176:136 */     Drot.drot(n, dx, _dx_offset, incx, dy, _dy_offset, incy, c, s);
/* 177:    */   }
/* 178:    */   
/* 179:    */   public void drotg(doubleW da, doubleW db, doubleW c, doubleW s)
/* 180:    */   {
/* 181:141 */     Drotg.drotg(da, db, c, s);
/* 182:    */   }
/* 183:    */   
/* 184:    */   public void drotm(int n, double[] dx, int incx, double[] dy, int incy, double[] dparam)
/* 185:    */   {
/* 186:146 */     Drotm.drotm(n, dx, 0, incx, dy, 0, incy, dparam, 0);
/* 187:    */   }
/* 188:    */   
/* 189:    */   public void drotm(int n, double[] dx, int _dx_offset, int incx, double[] dy, int _dy_offset, int incy, double[] dparam, int _dparam_offset)
/* 190:    */   {
/* 191:151 */     Drotm.drotm(n, dx, _dx_offset, incx, dy, _dy_offset, incy, dparam, _dparam_offset);
/* 192:    */   }
/* 193:    */   
/* 194:    */   public void drotmg(doubleW dd1, doubleW dd2, doubleW dx1, double dy1, double[] dparam)
/* 195:    */   {
/* 196:156 */     Drotmg.drotmg(dd1, dd2, dx1, dy1, dparam, 0);
/* 197:    */   }
/* 198:    */   
/* 199:    */   public void drotmg(doubleW dd1, doubleW dd2, doubleW dx1, double dy1, double[] dparam, int _dparam_offset)
/* 200:    */   {
/* 201:161 */     Drotmg.drotmg(dd1, dd2, dx1, dy1, dparam, _dparam_offset);
/* 202:    */   }
/* 203:    */   
/* 204:    */   public void dsbmv(String uplo, int n, int k, double alpha, double[] a, int lda, double[] x, int incx, double beta, double[] y, int incy)
/* 205:    */   {
/* 206:166 */     Dsbmv.dsbmv(uplo, n, k, alpha, a, 0, lda, x, 0, incx, beta, y, 0, incy);
/* 207:    */   }
/* 208:    */   
/* 209:    */   public void dsbmv(String uplo, int n, int k, double alpha, double[] a, int _a_offset, int lda, double[] x, int _x_offset, int incx, double beta, double[] y, int _y_offset, int incy)
/* 210:    */   {
/* 211:171 */     Dsbmv.dsbmv(uplo, n, k, alpha, a, _a_offset, lda, x, _x_offset, incx, beta, y, _y_offset, incy);
/* 212:    */   }
/* 213:    */   
/* 214:    */   public void dscal(int n, double da, double[] dx, int incx)
/* 215:    */   {
/* 216:176 */     Dscal.dscal(n, da, dx, 0, incx);
/* 217:    */   }
/* 218:    */   
/* 219:    */   public void dscal(int n, double da, double[] dx, int _dx_offset, int incx)
/* 220:    */   {
/* 221:181 */     Dscal.dscal(n, da, dx, _dx_offset, incx);
/* 222:    */   }
/* 223:    */   
/* 224:    */   public void dspmv(String uplo, int n, double alpha, double[] ap, double[] x, int incx, double beta, double[] y, int incy)
/* 225:    */   {
/* 226:186 */     Dspmv.dspmv(uplo, n, alpha, ap, 0, x, 0, incx, beta, y, 0, incy);
/* 227:    */   }
/* 228:    */   
/* 229:    */   public void dspmv(String uplo, int n, double alpha, double[] ap, int _ap_offset, double[] x, int _x_offset, int incx, double beta, double[] y, int _y_offset, int incy)
/* 230:    */   {
/* 231:191 */     Dspmv.dspmv(uplo, n, alpha, ap, _ap_offset, x, _x_offset, incx, beta, y, _y_offset, incy);
/* 232:    */   }
/* 233:    */   
/* 234:    */   public void dspr(String uplo, int n, double alpha, double[] x, int incx, double[] ap)
/* 235:    */   {
/* 236:196 */     Dspr.dspr(uplo, n, alpha, x, 0, incx, ap, 0);
/* 237:    */   }
/* 238:    */   
/* 239:    */   public void dspr(String uplo, int n, double alpha, double[] x, int _x_offset, int incx, double[] ap, int _ap_offset)
/* 240:    */   {
/* 241:201 */     Dspr.dspr(uplo, n, alpha, x, _x_offset, incx, ap, _ap_offset);
/* 242:    */   }
/* 243:    */   
/* 244:    */   public void dspr2(String uplo, int n, double alpha, double[] x, int incx, double[] y, int incy, double[] ap)
/* 245:    */   {
/* 246:206 */     Dspr2.dspr2(uplo, n, alpha, x, 0, incx, y, 0, incy, ap, 0);
/* 247:    */   }
/* 248:    */   
/* 249:    */   public void dspr2(String uplo, int n, double alpha, double[] x, int _x_offset, int incx, double[] y, int _y_offset, int incy, double[] ap, int _ap_offset)
/* 250:    */   {
/* 251:211 */     Dspr2.dspr2(uplo, n, alpha, x, _x_offset, incx, y, _y_offset, incy, ap, _ap_offset);
/* 252:    */   }
/* 253:    */   
/* 254:    */   public void dswap(int n, double[] dx, int incx, double[] dy, int incy)
/* 255:    */   {
/* 256:216 */     Dswap.dswap(n, dx, 0, incx, dy, 0, incy);
/* 257:    */   }
/* 258:    */   
/* 259:    */   public void dswap(int n, double[] dx, int _dx_offset, int incx, double[] dy, int _dy_offset, int incy)
/* 260:    */   {
/* 261:221 */     Dswap.dswap(n, dx, _dx_offset, incx, dy, _dy_offset, incy);
/* 262:    */   }
/* 263:    */   
/* 264:    */   public void dsymm(String side, String uplo, int m, int n, double alpha, double[] a, int lda, double[] b, int ldb, double beta, double[] c, int Ldc)
/* 265:    */   {
/* 266:226 */     Dsymm.dsymm(side, uplo, m, n, alpha, a, 0, lda, b, 0, ldb, beta, c, 0, Ldc);
/* 267:    */   }
/* 268:    */   
/* 269:    */   public void dsymm(String side, String uplo, int m, int n, double alpha, double[] a, int _a_offset, int lda, double[] b, int _b_offset, int ldb, double beta, double[] c, int _c_offset, int Ldc)
/* 270:    */   {
/* 271:231 */     Dsymm.dsymm(side, uplo, m, n, alpha, a, _a_offset, lda, b, _b_offset, ldb, beta, c, _c_offset, Ldc);
/* 272:    */   }
/* 273:    */   
/* 274:    */   public void dsymv(String uplo, int n, double alpha, double[] a, int lda, double[] x, int incx, double beta, double[] y, int incy)
/* 275:    */   {
/* 276:236 */     Dsymv.dsymv(uplo, n, alpha, a, 0, lda, x, 0, incx, beta, y, 0, incy);
/* 277:    */   }
/* 278:    */   
/* 279:    */   public void dsymv(String uplo, int n, double alpha, double[] a, int _a_offset, int lda, double[] x, int _x_offset, int incx, double beta, double[] y, int _y_offset, int incy)
/* 280:    */   {
/* 281:241 */     Dsymv.dsymv(uplo, n, alpha, a, _a_offset, lda, x, _x_offset, incx, beta, y, _y_offset, incy);
/* 282:    */   }
/* 283:    */   
/* 284:    */   public void dsyr(String uplo, int n, double alpha, double[] x, int incx, double[] a, int lda)
/* 285:    */   {
/* 286:246 */     Dsyr.dsyr(uplo, n, alpha, x, 0, incx, a, 0, lda);
/* 287:    */   }
/* 288:    */   
/* 289:    */   public void dsyr(String uplo, int n, double alpha, double[] x, int _x_offset, int incx, double[] a, int _a_offset, int lda)
/* 290:    */   {
/* 291:251 */     Dsyr.dsyr(uplo, n, alpha, x, _x_offset, incx, a, _a_offset, lda);
/* 292:    */   }
/* 293:    */   
/* 294:    */   public void dsyr2(String uplo, int n, double alpha, double[] x, int incx, double[] y, int incy, double[] a, int lda)
/* 295:    */   {
/* 296:256 */     Dsyr2.dsyr2(uplo, n, alpha, x, 0, incx, y, 0, incy, a, 0, lda);
/* 297:    */   }
/* 298:    */   
/* 299:    */   public void dsyr2(String uplo, int n, double alpha, double[] x, int _x_offset, int incx, double[] y, int _y_offset, int incy, double[] a, int _a_offset, int lda)
/* 300:    */   {
/* 301:261 */     Dsyr2.dsyr2(uplo, n, alpha, x, _x_offset, incx, y, _y_offset, incy, a, _a_offset, lda);
/* 302:    */   }
/* 303:    */   
/* 304:    */   public void dsyr2k(String uplo, String trans, int n, int k, double alpha, double[] a, int lda, double[] b, int ldb, double beta, double[] c, int Ldc)
/* 305:    */   {
/* 306:266 */     Dsyr2k.dsyr2k(uplo, trans, n, k, alpha, a, 0, lda, b, 0, ldb, beta, c, 0, Ldc);
/* 307:    */   }
/* 308:    */   
/* 309:    */   public void dsyr2k(String uplo, String trans, int n, int k, double alpha, double[] a, int _a_offset, int lda, double[] b, int _b_offset, int ldb, double beta, double[] c, int _c_offset, int Ldc)
/* 310:    */   {
/* 311:271 */     Dsyr2k.dsyr2k(uplo, trans, n, k, alpha, a, _a_offset, lda, b, _b_offset, ldb, beta, c, _c_offset, Ldc);
/* 312:    */   }
/* 313:    */   
/* 314:    */   public void dsyrk(String uplo, String trans, int n, int k, double alpha, double[] a, int lda, double beta, double[] c, int Ldc)
/* 315:    */   {
/* 316:276 */     Dsyrk.dsyrk(uplo, trans, n, k, alpha, a, 0, lda, beta, c, 0, Ldc);
/* 317:    */   }
/* 318:    */   
/* 319:    */   public void dsyrk(String uplo, String trans, int n, int k, double alpha, double[] a, int _a_offset, int lda, double beta, double[] c, int _c_offset, int Ldc)
/* 320:    */   {
/* 321:281 */     Dsyrk.dsyrk(uplo, trans, n, k, alpha, a, _a_offset, lda, beta, c, _c_offset, Ldc);
/* 322:    */   }
/* 323:    */   
/* 324:    */   public void dtbmv(String uplo, String trans, String diag, int n, int k, double[] a, int lda, double[] x, int incx)
/* 325:    */   {
/* 326:286 */     Dtbmv.dtbmv(uplo, trans, diag, n, k, a, 0, lda, x, 0, incx);
/* 327:    */   }
/* 328:    */   
/* 329:    */   public void dtbmv(String uplo, String trans, String diag, int n, int k, double[] a, int _a_offset, int lda, double[] x, int _x_offset, int incx)
/* 330:    */   {
/* 331:291 */     Dtbmv.dtbmv(uplo, trans, diag, n, k, a, _a_offset, lda, x, _x_offset, incx);
/* 332:    */   }
/* 333:    */   
/* 334:    */   public void dtbsv(String uplo, String trans, String diag, int n, int k, double[] a, int lda, double[] x, int incx)
/* 335:    */   {
/* 336:296 */     Dtbsv.dtbsv(uplo, trans, diag, n, k, a, 0, lda, x, 0, incx);
/* 337:    */   }
/* 338:    */   
/* 339:    */   public void dtbsv(String uplo, String trans, String diag, int n, int k, double[] a, int _a_offset, int lda, double[] x, int _x_offset, int incx)
/* 340:    */   {
/* 341:301 */     Dtbsv.dtbsv(uplo, trans, diag, n, k, a, _a_offset, lda, x, _x_offset, incx);
/* 342:    */   }
/* 343:    */   
/* 344:    */   public void dtpmv(String uplo, String trans, String diag, int n, double[] ap, double[] x, int incx)
/* 345:    */   {
/* 346:306 */     Dtpmv.dtpmv(uplo, trans, diag, n, ap, 0, x, 0, incx);
/* 347:    */   }
/* 348:    */   
/* 349:    */   public void dtpmv(String uplo, String trans, String diag, int n, double[] ap, int _ap_offset, double[] x, int _x_offset, int incx)
/* 350:    */   {
/* 351:311 */     Dtpmv.dtpmv(uplo, trans, diag, n, ap, _ap_offset, x, _x_offset, incx);
/* 352:    */   }
/* 353:    */   
/* 354:    */   public void dtpsv(String uplo, String trans, String diag, int n, double[] ap, double[] x, int incx)
/* 355:    */   {
/* 356:316 */     Dtpsv.dtpsv(uplo, trans, diag, n, ap, 0, x, 0, incx);
/* 357:    */   }
/* 358:    */   
/* 359:    */   public void dtpsv(String uplo, String trans, String diag, int n, double[] ap, int _ap_offset, double[] x, int _x_offset, int incx)
/* 360:    */   {
/* 361:321 */     Dtpsv.dtpsv(uplo, trans, diag, n, ap, _ap_offset, x, _x_offset, incx);
/* 362:    */   }
/* 363:    */   
/* 364:    */   public void dtrmm(String side, String uplo, String transa, String diag, int m, int n, double alpha, double[] a, int lda, double[] b, int ldb)
/* 365:    */   {
/* 366:326 */     Dtrmm.dtrmm(side, uplo, transa, diag, m, n, alpha, a, 0, lda, b, 0, ldb);
/* 367:    */   }
/* 368:    */   
/* 369:    */   public void dtrmm(String side, String uplo, String transa, String diag, int m, int n, double alpha, double[] a, int _a_offset, int lda, double[] b, int _b_offset, int ldb)
/* 370:    */   {
/* 371:331 */     Dtrmm.dtrmm(side, uplo, transa, diag, m, n, alpha, a, _a_offset, lda, b, _b_offset, ldb);
/* 372:    */   }
/* 373:    */   
/* 374:    */   public void dtrmv(String uplo, String trans, String diag, int n, double[] a, int lda, double[] x, int incx)
/* 375:    */   {
/* 376:336 */     Dtrmv.dtrmv(uplo, trans, diag, n, a, 0, lda, x, 0, incx);
/* 377:    */   }
/* 378:    */   
/* 379:    */   public void dtrmv(String uplo, String trans, String diag, int n, double[] a, int _a_offset, int lda, double[] x, int _x_offset, int incx)
/* 380:    */   {
/* 381:341 */     Dtrmv.dtrmv(uplo, trans, diag, n, a, _a_offset, lda, x, _x_offset, incx);
/* 382:    */   }
/* 383:    */   
/* 384:    */   public void dtrsm(String side, String uplo, String transa, String diag, int m, int n, double alpha, double[] a, int lda, double[] b, int ldb)
/* 385:    */   {
/* 386:346 */     Dtrsm.dtrsm(side, uplo, transa, diag, m, n, alpha, a, 0, lda, b, 0, ldb);
/* 387:    */   }
/* 388:    */   
/* 389:    */   public void dtrsm(String side, String uplo, String transa, String diag, int m, int n, double alpha, double[] a, int _a_offset, int lda, double[] b, int _b_offset, int ldb)
/* 390:    */   {
/* 391:351 */     Dtrsm.dtrsm(side, uplo, transa, diag, m, n, alpha, a, _a_offset, lda, b, _b_offset, ldb);
/* 392:    */   }
/* 393:    */   
/* 394:    */   public void dtrsv(String uplo, String trans, String diag, int n, double[] a, int lda, double[] x, int incx)
/* 395:    */   {
/* 396:356 */     Dtrsv.dtrsv(uplo, trans, diag, n, a, 0, lda, x, 0, incx);
/* 397:    */   }
/* 398:    */   
/* 399:    */   public void dtrsv(String uplo, String trans, String diag, int n, double[] a, int _a_offset, int lda, double[] x, int _x_offset, int incx)
/* 400:    */   {
/* 401:361 */     Dtrsv.dtrsv(uplo, trans, diag, n, a, _a_offset, lda, x, _x_offset, incx);
/* 402:    */   }
/* 403:    */   
/* 404:    */   public int idamax(int n, double[] dx, int incx)
/* 405:    */   {
/* 406:366 */     return Idamax.idamax(n, dx, 0, incx);
/* 407:    */   }
/* 408:    */   
/* 409:    */   public int idamax(int n, double[] dx, int _dx_offset, int incx)
/* 410:    */   {
/* 411:371 */     return Idamax.idamax(n, dx, _dx_offset, incx);
/* 412:    */   }
/* 413:    */   
/* 414:    */   public int isamax(int n, float[] sx, int incx)
/* 415:    */   {
/* 416:376 */     return Isamax.isamax(n, sx, 0, incx);
/* 417:    */   }
/* 418:    */   
/* 419:    */   public int isamax(int n, float[] sx, int _sx_offset, int incx)
/* 420:    */   {
/* 421:381 */     return Isamax.isamax(n, sx, _sx_offset, incx);
/* 422:    */   }
/* 423:    */   
/* 424:    */   public boolean lsame(String ca, String cb)
/* 425:    */   {
/* 426:386 */     return Lsame.lsame(ca, cb);
/* 427:    */   }
/* 428:    */   
/* 429:    */   public float sasum(int n, float[] sx, int incx)
/* 430:    */   {
/* 431:391 */     return Sasum.sasum(n, sx, 0, incx);
/* 432:    */   }
/* 433:    */   
/* 434:    */   public float sasum(int n, float[] sx, int _sx_offset, int incx)
/* 435:    */   {
/* 436:396 */     return Sasum.sasum(n, sx, _sx_offset, incx);
/* 437:    */   }
/* 438:    */   
/* 439:    */   public void saxpy(int n, float sa, float[] sx, int incx, float[] sy, int incy)
/* 440:    */   {
/* 441:401 */     Saxpy.saxpy(n, sa, sx, 0, incx, sy, 0, incy);
/* 442:    */   }
/* 443:    */   
/* 444:    */   public void saxpy(int n, float sa, float[] sx, int _sx_offset, int incx, float[] sy, int _sy_offset, int incy)
/* 445:    */   {
/* 446:406 */     Saxpy.saxpy(n, sa, sx, _sx_offset, incx, sy, _sy_offset, incy);
/* 447:    */   }
/* 448:    */   
/* 449:    */   public void scopy(int n, float[] sx, int incx, float[] sy, int incy)
/* 450:    */   {
/* 451:411 */     Scopy.scopy(n, sx, 0, incx, sy, 0, incy);
/* 452:    */   }
/* 453:    */   
/* 454:    */   public void scopy(int n, float[] sx, int _sx_offset, int incx, float[] sy, int _sy_offset, int incy)
/* 455:    */   {
/* 456:416 */     Scopy.scopy(n, sx, _sx_offset, incx, sy, _sy_offset, incy);
/* 457:    */   }
/* 458:    */   
/* 459:    */   public float sdot(int n, float[] sx, int incx, float[] sy, int incy)
/* 460:    */   {
/* 461:421 */     return Sdot.sdot(n, sx, 0, incx, sy, 0, incy);
/* 462:    */   }
/* 463:    */   
/* 464:    */   public float sdot(int n, float[] sx, int _sx_offset, int incx, float[] sy, int _sy_offset, int incy)
/* 465:    */   {
/* 466:426 */     return Sdot.sdot(n, sx, _sx_offset, incx, sy, _sy_offset, incy);
/* 467:    */   }
/* 468:    */   
/* 469:    */   public float sdsdot(int n, float sb, float[] sx, int incx, float[] sy, int incy)
/* 470:    */   {
/* 471:431 */     return Sdsdot.sdsdot(n, sb, sx, 0, incx, sy, 0, incy);
/* 472:    */   }
/* 473:    */   
/* 474:    */   public float sdsdot(int n, float sb, float[] sx, int _sx_offset, int incx, float[] sy, int _sy_offset, int incy)
/* 475:    */   {
/* 476:436 */     return Sdsdot.sdsdot(n, sb, sx, _sx_offset, incx, sy, _sy_offset, incy);
/* 477:    */   }
/* 478:    */   
/* 479:    */   public void sgbmv(String trans, int m, int n, int kl, int ku, float alpha, float[] a, int lda, float[] x, int incx, float beta, float[] y, int incy)
/* 480:    */   {
/* 481:441 */     Sgbmv.sgbmv(trans, m, n, kl, ku, alpha, a, 0, lda, x, 0, incx, beta, y, 0, incy);
/* 482:    */   }
/* 483:    */   
/* 484:    */   public void sgbmv(String trans, int m, int n, int kl, int ku, float alpha, float[] a, int _a_offset, int lda, float[] x, int _x_offset, int incx, float beta, float[] y, int _y_offset, int incy)
/* 485:    */   {
/* 486:446 */     Sgbmv.sgbmv(trans, m, n, kl, ku, alpha, a, _a_offset, lda, x, _x_offset, incx, beta, y, _y_offset, incy);
/* 487:    */   }
/* 488:    */   
/* 489:    */   public void sgemm(String transa, String transb, int m, int n, int k, float alpha, float[] a, int lda, float[] b, int ldb, float beta, float[] c, int Ldc)
/* 490:    */   {
/* 491:451 */     Sgemm.sgemm(transa, transb, m, n, k, alpha, a, 0, lda, b, 0, ldb, beta, c, 0, Ldc);
/* 492:    */   }
/* 493:    */   
/* 494:    */   public void sgemm(String transa, String transb, int m, int n, int k, float alpha, float[] a, int _a_offset, int lda, float[] b, int _b_offset, int ldb, float beta, float[] c, int _c_offset, int Ldc)
/* 495:    */   {
/* 496:456 */     Sgemm.sgemm(transa, transb, m, n, k, alpha, a, _a_offset, lda, b, _b_offset, ldb, beta, c, _c_offset, Ldc);
/* 497:    */   }
/* 498:    */   
/* 499:    */   public void sgemv(String trans, int m, int n, float alpha, float[] a, int lda, float[] x, int incx, float beta, float[] y, int incy)
/* 500:    */   {
/* 501:461 */     Sgemv.sgemv(trans, m, n, alpha, a, 0, lda, x, 0, incx, beta, y, 0, incy);
/* 502:    */   }
/* 503:    */   
/* 504:    */   public void sgemv(String trans, int m, int n, float alpha, float[] a, int _a_offset, int lda, float[] x, int _x_offset, int incx, float beta, float[] y, int _y_offset, int incy)
/* 505:    */   {
/* 506:466 */     Sgemv.sgemv(trans, m, n, alpha, a, _a_offset, lda, x, _x_offset, incx, beta, y, _y_offset, incy);
/* 507:    */   }
/* 508:    */   
/* 509:    */   public void sger(int m, int n, float alpha, float[] x, int incx, float[] y, int incy, float[] a, int lda)
/* 510:    */   {
/* 511:471 */     Sger.sger(m, n, alpha, x, 0, incx, y, 0, incy, a, 0, lda);
/* 512:    */   }
/* 513:    */   
/* 514:    */   public void sger(int m, int n, float alpha, float[] x, int _x_offset, int incx, float[] y, int _y_offset, int incy, float[] a, int _a_offset, int lda)
/* 515:    */   {
/* 516:476 */     Sger.sger(m, n, alpha, x, _x_offset, incx, y, _y_offset, incy, a, _a_offset, lda);
/* 517:    */   }
/* 518:    */   
/* 519:    */   public float snrm2(int n, float[] x, int incx)
/* 520:    */   {
/* 521:481 */     return Snrm2.snrm2(n, x, 0, incx);
/* 522:    */   }
/* 523:    */   
/* 524:    */   public float snrm2(int n, float[] x, int _x_offset, int incx)
/* 525:    */   {
/* 526:486 */     return Snrm2.snrm2(n, x, _x_offset, incx);
/* 527:    */   }
/* 528:    */   
/* 529:    */   public void srot(int n, float[] sx, int incx, float[] sy, int incy, float c, float s)
/* 530:    */   {
/* 531:491 */     Srot.srot(n, sx, 0, incx, sy, 0, incy, c, s);
/* 532:    */   }
/* 533:    */   
/* 534:    */   public void srot(int n, float[] sx, int _sx_offset, int incx, float[] sy, int _sy_offset, int incy, float c, float s)
/* 535:    */   {
/* 536:496 */     Srot.srot(n, sx, _sx_offset, incx, sy, _sy_offset, incy, c, s);
/* 537:    */   }
/* 538:    */   
/* 539:    */   public void srotg(floatW sa, floatW sb, floatW c, floatW s)
/* 540:    */   {
/* 541:501 */     Srotg.srotg(sa, sb, c, s);
/* 542:    */   }
/* 543:    */   
/* 544:    */   public void srotm(int n, float[] sx, int incx, float[] sy, int incy, float[] sparam)
/* 545:    */   {
/* 546:506 */     Srotm.srotm(n, sx, 0, incx, sy, 0, incy, sparam, 0);
/* 547:    */   }
/* 548:    */   
/* 549:    */   public void srotm(int n, float[] sx, int _sx_offset, int incx, float[] sy, int _sy_offset, int incy, float[] sparam, int _sparam_offset)
/* 550:    */   {
/* 551:511 */     Srotm.srotm(n, sx, _sx_offset, incx, sy, _sy_offset, incy, sparam, _sparam_offset);
/* 552:    */   }
/* 553:    */   
/* 554:    */   public void srotmg(floatW sd1, floatW sd2, floatW sx1, float sy1, float[] sparam)
/* 555:    */   {
/* 556:516 */     Srotmg.srotmg(sd1, sd2, sx1, sy1, sparam, 0);
/* 557:    */   }
/* 558:    */   
/* 559:    */   public void srotmg(floatW sd1, floatW sd2, floatW sx1, float sy1, float[] sparam, int _sparam_offset)
/* 560:    */   {
/* 561:521 */     Srotmg.srotmg(sd1, sd2, sx1, sy1, sparam, _sparam_offset);
/* 562:    */   }
/* 563:    */   
/* 564:    */   public void ssbmv(String uplo, int n, int k, float alpha, float[] a, int lda, float[] x, int incx, float beta, float[] y, int incy)
/* 565:    */   {
/* 566:526 */     Ssbmv.ssbmv(uplo, n, k, alpha, a, 0, lda, x, 0, incx, beta, y, 0, incy);
/* 567:    */   }
/* 568:    */   
/* 569:    */   public void ssbmv(String uplo, int n, int k, float alpha, float[] a, int _a_offset, int lda, float[] x, int _x_offset, int incx, float beta, float[] y, int _y_offset, int incy)
/* 570:    */   {
/* 571:531 */     Ssbmv.ssbmv(uplo, n, k, alpha, a, _a_offset, lda, x, _x_offset, incx, beta, y, _y_offset, incy);
/* 572:    */   }
/* 573:    */   
/* 574:    */   public void sscal(int n, float sa, float[] sx, int incx)
/* 575:    */   {
/* 576:536 */     Sscal.sscal(n, sa, sx, 0, incx);
/* 577:    */   }
/* 578:    */   
/* 579:    */   public void sscal(int n, float sa, float[] sx, int _sx_offset, int incx)
/* 580:    */   {
/* 581:541 */     Sscal.sscal(n, sa, sx, _sx_offset, incx);
/* 582:    */   }
/* 583:    */   
/* 584:    */   public void sspmv(String uplo, int n, float alpha, float[] ap, float[] x, int incx, float beta, float[] y, int incy)
/* 585:    */   {
/* 586:546 */     Sspmv.sspmv(uplo, n, alpha, ap, 0, x, 0, incx, beta, y, 0, incy);
/* 587:    */   }
/* 588:    */   
/* 589:    */   public void sspmv(String uplo, int n, float alpha, float[] ap, int _ap_offset, float[] x, int _x_offset, int incx, float beta, float[] y, int _y_offset, int incy)
/* 590:    */   {
/* 591:551 */     Sspmv.sspmv(uplo, n, alpha, ap, _ap_offset, x, _x_offset, incx, beta, y, _y_offset, incy);
/* 592:    */   }
/* 593:    */   
/* 594:    */   public void sspr(String uplo, int n, float alpha, float[] x, int incx, float[] ap)
/* 595:    */   {
/* 596:556 */     Sspr.sspr(uplo, n, alpha, x, 0, incx, ap, 0);
/* 597:    */   }
/* 598:    */   
/* 599:    */   public void sspr(String uplo, int n, float alpha, float[] x, int _x_offset, int incx, float[] ap, int _ap_offset)
/* 600:    */   {
/* 601:561 */     Sspr.sspr(uplo, n, alpha, x, _x_offset, incx, ap, _ap_offset);
/* 602:    */   }
/* 603:    */   
/* 604:    */   public void sspr2(String uplo, int n, float alpha, float[] x, int incx, float[] y, int incy, float[] ap)
/* 605:    */   {
/* 606:566 */     Sspr2.sspr2(uplo, n, alpha, x, 0, incx, y, 0, incy, ap, 0);
/* 607:    */   }
/* 608:    */   
/* 609:    */   public void sspr2(String uplo, int n, float alpha, float[] x, int _x_offset, int incx, float[] y, int _y_offset, int incy, float[] ap, int _ap_offset)
/* 610:    */   {
/* 611:571 */     Sspr2.sspr2(uplo, n, alpha, x, _x_offset, incx, y, _y_offset, incy, ap, _ap_offset);
/* 612:    */   }
/* 613:    */   
/* 614:    */   public void sswap(int n, float[] sx, int incx, float[] sy, int incy)
/* 615:    */   {
/* 616:576 */     Sswap.sswap(n, sx, 0, incx, sy, 0, incy);
/* 617:    */   }
/* 618:    */   
/* 619:    */   public void sswap(int n, float[] sx, int _sx_offset, int incx, float[] sy, int _sy_offset, int incy)
/* 620:    */   {
/* 621:581 */     Sswap.sswap(n, sx, _sx_offset, incx, sy, _sy_offset, incy);
/* 622:    */   }
/* 623:    */   
/* 624:    */   public void ssymm(String side, String uplo, int m, int n, float alpha, float[] a, int lda, float[] b, int ldb, float beta, float[] c, int Ldc)
/* 625:    */   {
/* 626:586 */     Ssymm.ssymm(side, uplo, m, n, alpha, a, 0, lda, b, 0, ldb, beta, c, 0, Ldc);
/* 627:    */   }
/* 628:    */   
/* 629:    */   public void ssymm(String side, String uplo, int m, int n, float alpha, float[] a, int _a_offset, int lda, float[] b, int _b_offset, int ldb, float beta, float[] c, int _c_offset, int Ldc)
/* 630:    */   {
/* 631:591 */     Ssymm.ssymm(side, uplo, m, n, alpha, a, _a_offset, lda, b, _b_offset, ldb, beta, c, _c_offset, Ldc);
/* 632:    */   }
/* 633:    */   
/* 634:    */   public void ssymv(String uplo, int n, float alpha, float[] a, int lda, float[] x, int incx, float beta, float[] y, int incy)
/* 635:    */   {
/* 636:596 */     Ssymv.ssymv(uplo, n, alpha, a, 0, lda, x, 0, incx, beta, y, 0, incy);
/* 637:    */   }
/* 638:    */   
/* 639:    */   public void ssymv(String uplo, int n, float alpha, float[] a, int _a_offset, int lda, float[] x, int _x_offset, int incx, float beta, float[] y, int _y_offset, int incy)
/* 640:    */   {
/* 641:601 */     Ssymv.ssymv(uplo, n, alpha, a, _a_offset, lda, x, _x_offset, incx, beta, y, _y_offset, incy);
/* 642:    */   }
/* 643:    */   
/* 644:    */   public void ssyr(String uplo, int n, float alpha, float[] x, int incx, float[] a, int lda)
/* 645:    */   {
/* 646:606 */     Ssyr.ssyr(uplo, n, alpha, x, 0, incx, a, 0, lda);
/* 647:    */   }
/* 648:    */   
/* 649:    */   public void ssyr(String uplo, int n, float alpha, float[] x, int _x_offset, int incx, float[] a, int _a_offset, int lda)
/* 650:    */   {
/* 651:611 */     Ssyr.ssyr(uplo, n, alpha, x, _x_offset, incx, a, _a_offset, lda);
/* 652:    */   }
/* 653:    */   
/* 654:    */   public void ssyr2(String uplo, int n, float alpha, float[] x, int incx, float[] y, int incy, float[] a, int lda)
/* 655:    */   {
/* 656:616 */     Ssyr2.ssyr2(uplo, n, alpha, x, 0, incx, y, 0, incy, a, 0, lda);
/* 657:    */   }
/* 658:    */   
/* 659:    */   public void ssyr2(String uplo, int n, float alpha, float[] x, int _x_offset, int incx, float[] y, int _y_offset, int incy, float[] a, int _a_offset, int lda)
/* 660:    */   {
/* 661:621 */     Ssyr2.ssyr2(uplo, n, alpha, x, _x_offset, incx, y, _y_offset, incy, a, _a_offset, lda);
/* 662:    */   }
/* 663:    */   
/* 664:    */   public void ssyr2k(String uplo, String trans, int n, int k, float alpha, float[] a, int lda, float[] b, int ldb, float beta, float[] c, int Ldc)
/* 665:    */   {
/* 666:626 */     Ssyr2k.ssyr2k(uplo, trans, n, k, alpha, a, 0, lda, b, 0, ldb, beta, c, 0, Ldc);
/* 667:    */   }
/* 668:    */   
/* 669:    */   public void ssyr2k(String uplo, String trans, int n, int k, float alpha, float[] a, int _a_offset, int lda, float[] b, int _b_offset, int ldb, float beta, float[] c, int _c_offset, int Ldc)
/* 670:    */   {
/* 671:631 */     Ssyr2k.ssyr2k(uplo, trans, n, k, alpha, a, _a_offset, lda, b, _b_offset, ldb, beta, c, _c_offset, Ldc);
/* 672:    */   }
/* 673:    */   
/* 674:    */   public void ssyrk(String uplo, String trans, int n, int k, float alpha, float[] a, int lda, float beta, float[] c, int Ldc)
/* 675:    */   {
/* 676:636 */     Ssyrk.ssyrk(uplo, trans, n, k, alpha, a, 0, lda, beta, c, 0, Ldc);
/* 677:    */   }
/* 678:    */   
/* 679:    */   public void ssyrk(String uplo, String trans, int n, int k, float alpha, float[] a, int _a_offset, int lda, float beta, float[] c, int _c_offset, int Ldc)
/* 680:    */   {
/* 681:641 */     Ssyrk.ssyrk(uplo, trans, n, k, alpha, a, _a_offset, lda, beta, c, _c_offset, Ldc);
/* 682:    */   }
/* 683:    */   
/* 684:    */   public void stbmv(String uplo, String trans, String diag, int n, int k, float[] a, int lda, float[] x, int incx)
/* 685:    */   {
/* 686:646 */     Stbmv.stbmv(uplo, trans, diag, n, k, a, 0, lda, x, 0, incx);
/* 687:    */   }
/* 688:    */   
/* 689:    */   public void stbmv(String uplo, String trans, String diag, int n, int k, float[] a, int _a_offset, int lda, float[] x, int _x_offset, int incx)
/* 690:    */   {
/* 691:651 */     Stbmv.stbmv(uplo, trans, diag, n, k, a, _a_offset, lda, x, _x_offset, incx);
/* 692:    */   }
/* 693:    */   
/* 694:    */   public void stbsv(String uplo, String trans, String diag, int n, int k, float[] a, int lda, float[] x, int incx)
/* 695:    */   {
/* 696:656 */     Stbsv.stbsv(uplo, trans, diag, n, k, a, 0, lda, x, 0, incx);
/* 697:    */   }
/* 698:    */   
/* 699:    */   public void stbsv(String uplo, String trans, String diag, int n, int k, float[] a, int _a_offset, int lda, float[] x, int _x_offset, int incx)
/* 700:    */   {
/* 701:661 */     Stbsv.stbsv(uplo, trans, diag, n, k, a, _a_offset, lda, x, _x_offset, incx);
/* 702:    */   }
/* 703:    */   
/* 704:    */   public void stpmv(String uplo, String trans, String diag, int n, float[] ap, float[] x, int incx)
/* 705:    */   {
/* 706:666 */     Stpmv.stpmv(uplo, trans, diag, n, ap, 0, x, 0, incx);
/* 707:    */   }
/* 708:    */   
/* 709:    */   public void stpmv(String uplo, String trans, String diag, int n, float[] ap, int _ap_offset, float[] x, int _x_offset, int incx)
/* 710:    */   {
/* 711:671 */     Stpmv.stpmv(uplo, trans, diag, n, ap, _ap_offset, x, _x_offset, incx);
/* 712:    */   }
/* 713:    */   
/* 714:    */   public void stpsv(String uplo, String trans, String diag, int n, float[] ap, float[] x, int incx)
/* 715:    */   {
/* 716:676 */     Stpsv.stpsv(uplo, trans, diag, n, ap, 0, x, 0, incx);
/* 717:    */   }
/* 718:    */   
/* 719:    */   public void stpsv(String uplo, String trans, String diag, int n, float[] ap, int _ap_offset, float[] x, int _x_offset, int incx)
/* 720:    */   {
/* 721:681 */     Stpsv.stpsv(uplo, trans, diag, n, ap, _ap_offset, x, _x_offset, incx);
/* 722:    */   }
/* 723:    */   
/* 724:    */   public void strmm(String side, String uplo, String transa, String diag, int m, int n, float alpha, float[] a, int lda, float[] b, int ldb)
/* 725:    */   {
/* 726:686 */     Strmm.strmm(side, uplo, transa, diag, m, n, alpha, a, 0, lda, b, 0, ldb);
/* 727:    */   }
/* 728:    */   
/* 729:    */   public void strmm(String side, String uplo, String transa, String diag, int m, int n, float alpha, float[] a, int _a_offset, int lda, float[] b, int _b_offset, int ldb)
/* 730:    */   {
/* 731:691 */     Strmm.strmm(side, uplo, transa, diag, m, n, alpha, a, _a_offset, lda, b, _b_offset, ldb);
/* 732:    */   }
/* 733:    */   
/* 734:    */   public void strmv(String uplo, String trans, String diag, int n, float[] a, int lda, float[] x, int incx)
/* 735:    */   {
/* 736:696 */     Strmv.strmv(uplo, trans, diag, n, a, 0, lda, x, 0, incx);
/* 737:    */   }
/* 738:    */   
/* 739:    */   public void strmv(String uplo, String trans, String diag, int n, float[] a, int _a_offset, int lda, float[] x, int _x_offset, int incx)
/* 740:    */   {
/* 741:701 */     Strmv.strmv(uplo, trans, diag, n, a, _a_offset, lda, x, _x_offset, incx);
/* 742:    */   }
/* 743:    */   
/* 744:    */   public void strsm(String side, String uplo, String transa, String diag, int m, int n, float alpha, float[] a, int lda, float[] b, int ldb)
/* 745:    */   {
/* 746:706 */     Strsm.strsm(side, uplo, transa, diag, m, n, alpha, a, 0, lda, b, 0, ldb);
/* 747:    */   }
/* 748:    */   
/* 749:    */   public void strsm(String side, String uplo, String transa, String diag, int m, int n, float alpha, float[] a, int _a_offset, int lda, float[] b, int _b_offset, int ldb)
/* 750:    */   {
/* 751:711 */     Strsm.strsm(side, uplo, transa, diag, m, n, alpha, a, _a_offset, lda, b, _b_offset, ldb);
/* 752:    */   }
/* 753:    */   
/* 754:    */   public void strsv(String uplo, String trans, String diag, int n, float[] a, int lda, float[] x, int incx)
/* 755:    */   {
/* 756:716 */     Strsv.strsv(uplo, trans, diag, n, a, 0, lda, x, 0, incx);
/* 757:    */   }
/* 758:    */   
/* 759:    */   public void strsv(String uplo, String trans, String diag, int n, float[] a, int _a_offset, int lda, float[] x, int _x_offset, int incx)
/* 760:    */   {
/* 761:721 */     Strsv.strsv(uplo, trans, diag, n, a, _a_offset, lda, x, _x_offset, incx);
/* 762:    */   }
/* 763:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     com.github.fommil.netlib.F2jBLAS
 * JD-Core Version:    0.7.0.1
 */