/*   1:    */ package com.github.fommil.netlib;
/*   2:    */ 
/*   3:    */ import org.netlib.arpack.Dgetv0;
/*   4:    */ import org.netlib.arpack.Dlaqrb;
/*   5:    */ import org.netlib.arpack.Dmout;
/*   6:    */ import org.netlib.arpack.Dnaitr;
/*   7:    */ import org.netlib.arpack.Dnapps;
/*   8:    */ import org.netlib.arpack.Dnaup2;
/*   9:    */ import org.netlib.arpack.Dnaupd;
/*  10:    */ import org.netlib.arpack.Dnconv;
/*  11:    */ import org.netlib.arpack.Dneigh;
/*  12:    */ import org.netlib.arpack.Dneupd;
/*  13:    */ import org.netlib.arpack.Dngets;
/*  14:    */ import org.netlib.arpack.Dsaitr;
/*  15:    */ import org.netlib.arpack.Dsapps;
/*  16:    */ import org.netlib.arpack.Dsaup2;
/*  17:    */ import org.netlib.arpack.Dsaupd;
/*  18:    */ import org.netlib.arpack.Dsconv;
/*  19:    */ import org.netlib.arpack.Dseigt;
/*  20:    */ import org.netlib.arpack.Dsesrt;
/*  21:    */ import org.netlib.arpack.Dseupd;
/*  22:    */ import org.netlib.arpack.Dsgets;
/*  23:    */ import org.netlib.arpack.Dsortc;
/*  24:    */ import org.netlib.arpack.Dsortr;
/*  25:    */ import org.netlib.arpack.Dstatn;
/*  26:    */ import org.netlib.arpack.Dstats;
/*  27:    */ import org.netlib.arpack.Dstqrb;
/*  28:    */ import org.netlib.arpack.Dvout;
/*  29:    */ import org.netlib.arpack.Icnteq;
/*  30:    */ import org.netlib.arpack.Icopy;
/*  31:    */ import org.netlib.arpack.Iset;
/*  32:    */ import org.netlib.arpack.Iswap;
/*  33:    */ import org.netlib.arpack.Ivout;
/*  34:    */ import org.netlib.arpack.Second;
/*  35:    */ import org.netlib.arpack.Sgetv0;
/*  36:    */ import org.netlib.arpack.Slaqrb;
/*  37:    */ import org.netlib.arpack.Smout;
/*  38:    */ import org.netlib.arpack.Snaitr;
/*  39:    */ import org.netlib.arpack.Snapps;
/*  40:    */ import org.netlib.arpack.Snaup2;
/*  41:    */ import org.netlib.arpack.Snaupd;
/*  42:    */ import org.netlib.arpack.Snconv;
/*  43:    */ import org.netlib.arpack.Sneigh;
/*  44:    */ import org.netlib.arpack.Sneupd;
/*  45:    */ import org.netlib.arpack.Sngets;
/*  46:    */ import org.netlib.arpack.Ssaitr;
/*  47:    */ import org.netlib.arpack.Ssapps;
/*  48:    */ import org.netlib.arpack.Ssaup2;
/*  49:    */ import org.netlib.arpack.Ssaupd;
/*  50:    */ import org.netlib.arpack.Ssconv;
/*  51:    */ import org.netlib.arpack.Sseigt;
/*  52:    */ import org.netlib.arpack.Ssesrt;
/*  53:    */ import org.netlib.arpack.Sseupd;
/*  54:    */ import org.netlib.arpack.Ssgets;
/*  55:    */ import org.netlib.arpack.Ssortc;
/*  56:    */ import org.netlib.arpack.Ssortr;
/*  57:    */ import org.netlib.arpack.Sstatn;
/*  58:    */ import org.netlib.arpack.Sstats;
/*  59:    */ import org.netlib.arpack.Sstqrb;
/*  60:    */ import org.netlib.arpack.Svout;
/*  61:    */ import org.netlib.util.doubleW;
/*  62:    */ import org.netlib.util.floatW;
/*  63:    */ import org.netlib.util.intW;
/*  64:    */ 
/*  65:    */ public class F2jARPACK
/*  66:    */   extends ARPACK
/*  67:    */ {
/*  68:    */   public void dmout(int lout, int m, int n, double[] a, int lda, int idigit, String ifmt)
/*  69:    */   {
/*  70: 41 */     Dmout.dmout(lout, m, n, a, 0, lda, idigit, ifmt);
/*  71:    */   }
/*  72:    */   
/*  73:    */   public void dmout(int lout, int m, int n, double[] a, int _a_offset, int lda, int idigit, String ifmt)
/*  74:    */   {
/*  75: 46 */     Dmout.dmout(lout, m, n, a, _a_offset, lda, idigit, ifmt);
/*  76:    */   }
/*  77:    */   
/*  78:    */   public void dvout(int lout, int n, double[] sx, int idigit, String ifmt)
/*  79:    */   {
/*  80: 51 */     Dvout.dvout(lout, n, sx, 0, idigit, ifmt);
/*  81:    */   }
/*  82:    */   
/*  83:    */   public void dvout(int lout, int n, double[] sx, int _sx_offset, int idigit, String ifmt)
/*  84:    */   {
/*  85: 56 */     Dvout.dvout(lout, n, sx, _sx_offset, idigit, ifmt);
/*  86:    */   }
/*  87:    */   
/*  88:    */   public int icnteq(int n, int[] array, int value)
/*  89:    */   {
/*  90: 61 */     return Icnteq.icnteq(n, array, 0, value);
/*  91:    */   }
/*  92:    */   
/*  93:    */   public int icnteq(int n, int[] array, int _array_offset, int value)
/*  94:    */   {
/*  95: 66 */     return Icnteq.icnteq(n, array, _array_offset, value);
/*  96:    */   }
/*  97:    */   
/*  98:    */   public void icopy(int n, int[] lx, int incx, int[] ly, int incy)
/*  99:    */   {
/* 100: 71 */     Icopy.icopy(n, lx, 0, incx, ly, 0, incy);
/* 101:    */   }
/* 102:    */   
/* 103:    */   public void icopy(int n, int[] lx, int _lx_offset, int incx, int[] ly, int _ly_offset, int incy)
/* 104:    */   {
/* 105: 76 */     Icopy.icopy(n, lx, _lx_offset, incx, ly, _ly_offset, incy);
/* 106:    */   }
/* 107:    */   
/* 108:    */   public void iset(int n, int value, int[] array, int inc)
/* 109:    */   {
/* 110: 81 */     Iset.iset(n, value, array, 0, inc);
/* 111:    */   }
/* 112:    */   
/* 113:    */   public void iset(int n, int value, int[] array, int _array_offset, int inc)
/* 114:    */   {
/* 115: 86 */     Iset.iset(n, value, array, _array_offset, inc);
/* 116:    */   }
/* 117:    */   
/* 118:    */   public void iswap(int n, int[] sx, int incx, int[] sy, int incy)
/* 119:    */   {
/* 120: 91 */     Iswap.iswap(n, sx, 0, incx, sy, 0, incy);
/* 121:    */   }
/* 122:    */   
/* 123:    */   public void iswap(int n, int[] sx, int _sx_offset, int incx, int[] sy, int _sy_offset, int incy)
/* 124:    */   {
/* 125: 96 */     Iswap.iswap(n, sx, _sx_offset, incx, sy, _sy_offset, incy);
/* 126:    */   }
/* 127:    */   
/* 128:    */   public void ivout(int lout, int n, int[] ix, int idigit, String ifmt)
/* 129:    */   {
/* 130:101 */     Ivout.ivout(lout, n, ix, 0, idigit, ifmt);
/* 131:    */   }
/* 132:    */   
/* 133:    */   public void ivout(int lout, int n, int[] ix, int _ix_offset, int idigit, String ifmt)
/* 134:    */   {
/* 135:106 */     Ivout.ivout(lout, n, ix, _ix_offset, idigit, ifmt);
/* 136:    */   }
/* 137:    */   
/* 138:    */   public void second(floatW t)
/* 139:    */   {
/* 140:111 */     Second.second(t);
/* 141:    */   }
/* 142:    */   
/* 143:    */   public void smout(int lout, int m, int n, float[] a, int lda, int idigit, String ifmt)
/* 144:    */   {
/* 145:116 */     Smout.smout(lout, m, n, a, 0, lda, idigit, ifmt);
/* 146:    */   }
/* 147:    */   
/* 148:    */   public void smout(int lout, int m, int n, float[] a, int _a_offset, int lda, int idigit, String ifmt)
/* 149:    */   {
/* 150:121 */     Smout.smout(lout, m, n, a, _a_offset, lda, idigit, ifmt);
/* 151:    */   }
/* 152:    */   
/* 153:    */   public void svout(int lout, int n, float[] sx, int idigit, String ifmt)
/* 154:    */   {
/* 155:126 */     Svout.svout(lout, n, sx, 0, idigit, ifmt);
/* 156:    */   }
/* 157:    */   
/* 158:    */   public void svout(int lout, int n, float[] sx, int _sx_offset, int idigit, String ifmt)
/* 159:    */   {
/* 160:131 */     Svout.svout(lout, n, sx, _sx_offset, idigit, ifmt);
/* 161:    */   }
/* 162:    */   
/* 163:    */   public void dgetv0(intW ido, String bmat, int itry, boolean initv, int n, int j, double[] v, int ldv, double[] resid, doubleW rnorm, int[] ipntr, double[] workd, intW ierr)
/* 164:    */   {
/* 165:136 */     Dgetv0.dgetv0(ido, bmat, itry, initv, n, j, v, 0, ldv, resid, 0, rnorm, ipntr, 0, workd, 0, ierr);
/* 166:    */   }
/* 167:    */   
/* 168:    */   public void dgetv0(intW ido, String bmat, int itry, boolean initv, int n, int j, double[] v, int _v_offset, int ldv, double[] resid, int _resid_offset, doubleW rnorm, int[] ipntr, int _ipntr_offset, double[] workd, int _workd_offset, intW ierr)
/* 169:    */   {
/* 170:141 */     Dgetv0.dgetv0(ido, bmat, itry, initv, n, j, v, _v_offset, ldv, resid, _resid_offset, rnorm, ipntr, _ipntr_offset, workd, _workd_offset, ierr);
/* 171:    */   }
/* 172:    */   
/* 173:    */   public void dlaqrb(boolean wantt, int n, int ilo, int ihi, double[] h, int ldh, double[] wr, double[] wi, double[] z, intW info)
/* 174:    */   {
/* 175:146 */     Dlaqrb.dlaqrb(wantt, n, ilo, ihi, h, 0, ldh, wr, 0, wi, 0, z, 0, info);
/* 176:    */   }
/* 177:    */   
/* 178:    */   public void dlaqrb(boolean wantt, int n, int ilo, int ihi, double[] h, int _h_offset, int ldh, double[] wr, int _wr_offset, double[] wi, int _wi_offset, double[] z, int _z_offset, intW info)
/* 179:    */   {
/* 180:151 */     Dlaqrb.dlaqrb(wantt, n, ilo, ihi, h, _h_offset, ldh, wr, _wr_offset, wi, _wi_offset, z, _z_offset, info);
/* 181:    */   }
/* 182:    */   
/* 183:    */   public void dnaitr(intW ido, String bmat, int n, int k, int np, int nb, double[] resid, doubleW rnorm, double[] v, int ldv, double[] h, int ldh, int[] ipntr, double[] workd, intW info)
/* 184:    */   {
/* 185:156 */     Dnaitr.dnaitr(ido, bmat, n, k, np, nb, resid, 0, rnorm, v, 0, ldv, h, 0, ldh, ipntr, 0, workd, 0, info);
/* 186:    */   }
/* 187:    */   
/* 188:    */   public void dnaitr(intW ido, String bmat, int n, int k, int np, int nb, double[] resid, int _resid_offset, doubleW rnorm, double[] v, int _v_offset, int ldv, double[] h, int _h_offset, int ldh, int[] ipntr, int _ipntr_offset, double[] workd, int _workd_offset, intW info)
/* 189:    */   {
/* 190:161 */     Dnaitr.dnaitr(ido, bmat, n, k, np, nb, resid, _resid_offset, rnorm, v, _v_offset, ldv, h, _h_offset, ldh, ipntr, _ipntr_offset, workd, _workd_offset, info);
/* 191:    */   }
/* 192:    */   
/* 193:    */   public void dnapps(int n, intW kev, int np, double[] shiftr, double[] shifti, double[] v, int ldv, double[] h, int ldh, double[] resid, double[] q, int ldq, double[] workl, double[] workd)
/* 194:    */   {
/* 195:166 */     Dnapps.dnapps(n, kev, np, shiftr, 0, shifti, 0, v, 0, ldv, h, 0, ldh, resid, 0, q, 0, ldq, workl, 0, workd, 0);
/* 196:    */   }
/* 197:    */   
/* 198:    */   public void dnapps(int n, intW kev, int np, double[] shiftr, int _shiftr_offset, double[] shifti, int _shifti_offset, double[] v, int _v_offset, int ldv, double[] h, int _h_offset, int ldh, double[] resid, int _resid_offset, double[] q, int _q_offset, int ldq, double[] workl, int _workl_offset, double[] workd, int _workd_offset)
/* 199:    */   {
/* 200:171 */     Dnapps.dnapps(n, kev, np, shiftr, _shiftr_offset, shifti, _shifti_offset, v, _v_offset, ldv, h, _h_offset, ldh, resid, _resid_offset, q, _q_offset, ldq, workl, _workl_offset, workd, _workd_offset);
/* 201:    */   }
/* 202:    */   
/* 203:    */   public void dnaup2(intW ido, String bmat, int n, String which, intW nev, intW np, double tol, double[] resid, int mode, int iupd, int ishift, intW mxiter, double[] v, int ldv, double[] h, int ldh, double[] ritzr, double[] ritzi, double[] bounds, double[] q, int ldq, double[] workl, int[] ipntr, double[] workd, intW info)
/* 204:    */   {
/* 205:176 */     Dnaup2.dnaup2(ido, bmat, n, which, nev, np, tol, resid, 0, mode, iupd, ishift, mxiter, v, 0, ldv, h, 0, ldh, ritzr, 0, ritzi, 0, bounds, 0, q, 0, ldq, workl, 0, ipntr, 0, workd, 0, info);
/* 206:    */   }
/* 207:    */   
/* 208:    */   public void dnaup2(intW ido, String bmat, int n, String which, intW nev, intW np, double tol, double[] resid, int _resid_offset, int mode, int iupd, int ishift, intW mxiter, double[] v, int _v_offset, int ldv, double[] h, int _h_offset, int ldh, double[] ritzr, int _ritzr_offset, double[] ritzi, int _ritzi_offset, double[] bounds, int _bounds_offset, double[] q, int _q_offset, int ldq, double[] workl, int _workl_offset, int[] ipntr, int _ipntr_offset, double[] workd, int _workd_offset, intW info)
/* 209:    */   {
/* 210:181 */     Dnaup2.dnaup2(ido, bmat, n, which, nev, np, tol, resid, _resid_offset, mode, iupd, ishift, mxiter, v, _v_offset, ldv, h, _h_offset, ldh, ritzr, _ritzr_offset, ritzi, _ritzi_offset, bounds, _bounds_offset, q, _q_offset, ldq, workl, _workl_offset, ipntr, _ipntr_offset, workd, _workd_offset, info);
/* 211:    */   }
/* 212:    */   
/* 213:    */   public void dnaupd(intW ido, String bmat, int n, String which, int nev, doubleW tol, double[] resid, int ncv, double[] v, int ldv, int[] iparam, int[] ipntr, double[] workd, double[] workl, int lworkl, intW info)
/* 214:    */   {
/* 215:186 */     Dnaupd.dnaupd(ido, bmat, n, which, nev, tol, resid, 0, ncv, v, 0, ldv, iparam, 0, ipntr, 0, workd, 0, workl, 0, lworkl, info);
/* 216:    */   }
/* 217:    */   
/* 218:    */   public void dnaupd(intW ido, String bmat, int n, String which, int nev, doubleW tol, double[] resid, int _resid_offset, int ncv, double[] v, int _v_offset, int ldv, int[] iparam, int _iparam_offset, int[] ipntr, int _ipntr_offset, double[] workd, int _workd_offset, double[] workl, int _workl_offset, int lworkl, intW info)
/* 219:    */   {
/* 220:191 */     Dnaupd.dnaupd(ido, bmat, n, which, nev, tol, resid, _resid_offset, ncv, v, _v_offset, ldv, iparam, _iparam_offset, ipntr, _ipntr_offset, workd, _workd_offset, workl, _workl_offset, lworkl, info);
/* 221:    */   }
/* 222:    */   
/* 223:    */   public void dnconv(int n, double[] ritzr, double[] ritzi, double[] bounds, double tol, intW nconv)
/* 224:    */   {
/* 225:196 */     Dnconv.dnconv(n, ritzr, 0, ritzi, 0, bounds, 0, tol, nconv);
/* 226:    */   }
/* 227:    */   
/* 228:    */   public void dnconv(int n, double[] ritzr, int _ritzr_offset, double[] ritzi, int _ritzi_offset, double[] bounds, int _bounds_offset, double tol, intW nconv)
/* 229:    */   {
/* 230:201 */     Dnconv.dnconv(n, ritzr, _ritzr_offset, ritzi, _ritzi_offset, bounds, _bounds_offset, tol, nconv);
/* 231:    */   }
/* 232:    */   
/* 233:    */   public void dneigh(double rnorm, intW n, double[] h, int ldh, double[] ritzr, double[] ritzi, double[] bounds, double[] q, int ldq, double[] workl, intW ierr)
/* 234:    */   {
/* 235:206 */     Dneigh.dneigh(rnorm, n, h, 0, ldh, ritzr, 0, ritzi, 0, bounds, 0, q, 0, ldq, workl, 0, ierr);
/* 236:    */   }
/* 237:    */   
/* 238:    */   public void dneigh(double rnorm, intW n, double[] h, int _h_offset, int ldh, double[] ritzr, int _ritzr_offset, double[] ritzi, int _ritzi_offset, double[] bounds, int _bounds_offset, double[] q, int _q_offset, int ldq, double[] workl, int _workl_offset, intW ierr)
/* 239:    */   {
/* 240:211 */     Dneigh.dneigh(rnorm, n, h, _h_offset, ldh, ritzr, _ritzr_offset, ritzi, _ritzi_offset, bounds, _bounds_offset, q, _q_offset, ldq, workl, _workl_offset, ierr);
/* 241:    */   }
/* 242:    */   
/* 243:    */   public void dneupd(boolean rvec, String howmny, boolean[] select, double[] dr, double[] di, double[] z, int ldz, double sigmar, double sigmai, double[] workev, String bmat, int n, String which, intW nev, double tol, double[] resid, int ncv, double[] v, int ldv, int[] iparam, int[] ipntr, double[] workd, double[] workl, int lworkl, intW info)
/* 244:    */   {
/* 245:216 */     Dneupd.dneupd(rvec, howmny, select, 0, dr, 0, di, 0, z, 0, ldz, sigmar, sigmai, workev, 0, bmat, n, which, nev, tol, resid, 0, ncv, v, 0, ldv, iparam, 0, ipntr, 0, workd, 0, workl, 0, lworkl, info);
/* 246:    */   }
/* 247:    */   
/* 248:    */   public void dneupd(boolean rvec, String howmny, boolean[] select, int _select_offset, double[] dr, int _dr_offset, double[] di, int _di_offset, double[] z, int _z_offset, int ldz, double sigmar, double sigmai, double[] workev, int _workev_offset, String bmat, int n, String which, intW nev, double tol, double[] resid, int _resid_offset, int ncv, double[] v, int _v_offset, int ldv, int[] iparam, int _iparam_offset, int[] ipntr, int _ipntr_offset, double[] workd, int _workd_offset, double[] workl, int _workl_offset, int lworkl, intW info)
/* 249:    */   {
/* 250:221 */     Dneupd.dneupd(rvec, howmny, select, _select_offset, dr, _dr_offset, di, _di_offset, z, _z_offset, ldz, sigmar, sigmai, workev, _workev_offset, bmat, n, which, nev, tol, resid, _resid_offset, ncv, v, _v_offset, ldv, iparam, _iparam_offset, ipntr, _ipntr_offset, workd, _workd_offset, workl, _workl_offset, lworkl, info);
/* 251:    */   }
/* 252:    */   
/* 253:    */   public void dngets(int ishift, String which, intW kev, intW np, double[] ritzr, double[] ritzi, double[] bounds, double[] shiftr, double[] shifti)
/* 254:    */   {
/* 255:226 */     Dngets.dngets(ishift, which, kev, np, ritzr, 0, ritzi, 0, bounds, 0, shiftr, 0, shifti, 0);
/* 256:    */   }
/* 257:    */   
/* 258:    */   public void dngets(int ishift, String which, intW kev, intW np, double[] ritzr, int _ritzr_offset, double[] ritzi, int _ritzi_offset, double[] bounds, int _bounds_offset, double[] shiftr, int _shiftr_offset, double[] shifti, int _shifti_offset)
/* 259:    */   {
/* 260:231 */     Dngets.dngets(ishift, which, kev, np, ritzr, _ritzr_offset, ritzi, _ritzi_offset, bounds, _bounds_offset, shiftr, _shiftr_offset, shifti, _shifti_offset);
/* 261:    */   }
/* 262:    */   
/* 263:    */   public void dsaitr(intW ido, String bmat, int n, int k, int np, int mode, double[] resid, doubleW rnorm, double[] v, int ldv, double[] h, int ldh, int[] ipntr, double[] workd, intW info)
/* 264:    */   {
/* 265:236 */     Dsaitr.dsaitr(ido, bmat, n, k, np, mode, resid, 0, rnorm, v, 0, ldv, h, 0, ldh, ipntr, 0, workd, 0, info);
/* 266:    */   }
/* 267:    */   
/* 268:    */   public void dsaitr(intW ido, String bmat, int n, int k, int np, int mode, double[] resid, int _resid_offset, doubleW rnorm, double[] v, int _v_offset, int ldv, double[] h, int _h_offset, int ldh, int[] ipntr, int _ipntr_offset, double[] workd, int _workd_offset, intW info)
/* 269:    */   {
/* 270:241 */     Dsaitr.dsaitr(ido, bmat, n, k, np, mode, resid, _resid_offset, rnorm, v, _v_offset, ldv, h, _h_offset, ldh, ipntr, _ipntr_offset, workd, _workd_offset, info);
/* 271:    */   }
/* 272:    */   
/* 273:    */   public void dsapps(int n, int kev, int np, double[] shift, double[] v, int ldv, double[] h, int ldh, double[] resid, double[] q, int ldq, double[] workd)
/* 274:    */   {
/* 275:246 */     Dsapps.dsapps(n, kev, np, shift, 0, v, 0, ldv, h, 0, ldh, resid, 0, q, 0, ldq, workd, 0);
/* 276:    */   }
/* 277:    */   
/* 278:    */   public void dsapps(int n, int kev, int np, double[] shift, int _shift_offset, double[] v, int _v_offset, int ldv, double[] h, int _h_offset, int ldh, double[] resid, int _resid_offset, double[] q, int _q_offset, int ldq, double[] workd, int _workd_offset)
/* 279:    */   {
/* 280:251 */     Dsapps.dsapps(n, kev, np, shift, _shift_offset, v, _v_offset, ldv, h, _h_offset, ldh, resid, _resid_offset, q, _q_offset, ldq, workd, _workd_offset);
/* 281:    */   }
/* 282:    */   
/* 283:    */   public void dsaup2(intW ido, String bmat, int n, String which, intW nev, intW np, double tol, double[] resid, int mode, int iupd, int ishift, intW mxiter, double[] v, int ldv, double[] h, int ldh, double[] ritz, double[] bounds, double[] q, int ldq, double[] workl, int[] ipntr, double[] workd, intW info)
/* 284:    */   {
/* 285:256 */     Dsaup2.dsaup2(ido, bmat, n, which, nev, np, tol, resid, 0, mode, iupd, ishift, mxiter, v, 0, ldv, h, 0, ldh, ritz, 0, bounds, 0, q, 0, ldq, workl, 0, ipntr, 0, workd, 0, info);
/* 286:    */   }
/* 287:    */   
/* 288:    */   public void dsaup2(intW ido, String bmat, int n, String which, intW nev, intW np, double tol, double[] resid, int _resid_offset, int mode, int iupd, int ishift, intW mxiter, double[] v, int _v_offset, int ldv, double[] h, int _h_offset, int ldh, double[] ritz, int _ritz_offset, double[] bounds, int _bounds_offset, double[] q, int _q_offset, int ldq, double[] workl, int _workl_offset, int[] ipntr, int _ipntr_offset, double[] workd, int _workd_offset, intW info)
/* 289:    */   {
/* 290:261 */     Dsaup2.dsaup2(ido, bmat, n, which, nev, np, tol, resid, _resid_offset, mode, iupd, ishift, mxiter, v, _v_offset, ldv, h, _h_offset, ldh, ritz, _ritz_offset, bounds, _bounds_offset, q, _q_offset, ldq, workl, _workl_offset, ipntr, _ipntr_offset, workd, _workd_offset, info);
/* 291:    */   }
/* 292:    */   
/* 293:    */   public void dsaupd(intW ido, String bmat, int n, String which, int nev, doubleW tol, double[] resid, int ncv, double[] v, int ldv, int[] iparam, int[] ipntr, double[] workd, double[] workl, int lworkl, intW info)
/* 294:    */   {
/* 295:266 */     Dsaupd.dsaupd(ido, bmat, n, which, nev, tol, resid, 0, ncv, v, 0, ldv, iparam, 0, ipntr, 0, workd, 0, workl, 0, lworkl, info);
/* 296:    */   }
/* 297:    */   
/* 298:    */   public void dsaupd(intW ido, String bmat, int n, String which, int nev, doubleW tol, double[] resid, int _resid_offset, int ncv, double[] v, int _v_offset, int ldv, int[] iparam, int _iparam_offset, int[] ipntr, int _ipntr_offset, double[] workd, int _workd_offset, double[] workl, int _workl_offset, int lworkl, intW info)
/* 299:    */   {
/* 300:271 */     Dsaupd.dsaupd(ido, bmat, n, which, nev, tol, resid, _resid_offset, ncv, v, _v_offset, ldv, iparam, _iparam_offset, ipntr, _ipntr_offset, workd, _workd_offset, workl, _workl_offset, lworkl, info);
/* 301:    */   }
/* 302:    */   
/* 303:    */   public void dsconv(int n, double[] ritz, double[] bounds, double tol, intW nconv)
/* 304:    */   {
/* 305:276 */     Dsconv.dsconv(n, ritz, 0, bounds, 0, tol, nconv);
/* 306:    */   }
/* 307:    */   
/* 308:    */   public void dsconv(int n, double[] ritz, int _ritz_offset, double[] bounds, int _bounds_offset, double tol, intW nconv)
/* 309:    */   {
/* 310:281 */     Dsconv.dsconv(n, ritz, _ritz_offset, bounds, _bounds_offset, tol, nconv);
/* 311:    */   }
/* 312:    */   
/* 313:    */   public void dseigt(double rnorm, int n, double[] h, int ldh, double[] eig, double[] bounds, double[] workl, intW ierr)
/* 314:    */   {
/* 315:286 */     Dseigt.dseigt(rnorm, n, h, 0, ldh, eig, 0, bounds, 0, workl, 0, ierr);
/* 316:    */   }
/* 317:    */   
/* 318:    */   public void dseigt(double rnorm, int n, double[] h, int _h_offset, int ldh, double[] eig, int _eig_offset, double[] bounds, int _bounds_offset, double[] workl, int _workl_offset, intW ierr)
/* 319:    */   {
/* 320:291 */     Dseigt.dseigt(rnorm, n, h, _h_offset, ldh, eig, _eig_offset, bounds, _bounds_offset, workl, _workl_offset, ierr);
/* 321:    */   }
/* 322:    */   
/* 323:    */   public void dsesrt(String which, boolean apply, int n, double[] x, int na, double[] a, int lda)
/* 324:    */   {
/* 325:296 */     Dsesrt.dsesrt(which, apply, n, x, 0, na, a, 0, lda);
/* 326:    */   }
/* 327:    */   
/* 328:    */   public void dsesrt(String which, boolean apply, int n, double[] x, int _x_offset, int na, double[] a, int _a_offset, int lda)
/* 329:    */   {
/* 330:301 */     Dsesrt.dsesrt(which, apply, n, x, _x_offset, na, a, _a_offset, lda);
/* 331:    */   }
/* 332:    */   
/* 333:    */   public void dseupd(boolean rvec, String howmny, boolean[] select, double[] d, double[] z, int ldz, double sigma, String bmat, int n, String which, intW nev, double tol, double[] resid, int ncv, double[] v, int ldv, int[] iparam, int[] ipntr, double[] workd, double[] workl, int lworkl, intW info)
/* 334:    */   {
/* 335:306 */     Dseupd.dseupd(rvec, howmny, select, 0, d, 0, z, 0, ldz, sigma, bmat, n, which, nev, tol, resid, 0, ncv, v, 0, ldv, iparam, 0, ipntr, 0, workd, 0, workl, 0, lworkl, info);
/* 336:    */   }
/* 337:    */   
/* 338:    */   public void dseupd(boolean rvec, String howmny, boolean[] select, int _select_offset, double[] d, int _d_offset, double[] z, int _z_offset, int ldz, double sigma, String bmat, int n, String which, intW nev, double tol, double[] resid, int _resid_offset, int ncv, double[] v, int _v_offset, int ldv, int[] iparam, int _iparam_offset, int[] ipntr, int _ipntr_offset, double[] workd, int _workd_offset, double[] workl, int _workl_offset, int lworkl, intW info)
/* 339:    */   {
/* 340:311 */     Dseupd.dseupd(rvec, howmny, select, _select_offset, d, _d_offset, z, _z_offset, ldz, sigma, bmat, n, which, nev, tol, resid, _resid_offset, ncv, v, _v_offset, ldv, iparam, _iparam_offset, ipntr, _ipntr_offset, workd, _workd_offset, workl, _workl_offset, lworkl, info);
/* 341:    */   }
/* 342:    */   
/* 343:    */   public void dsgets(int ishift, String which, intW kev, intW np, double[] ritz, double[] bounds, double[] shifts)
/* 344:    */   {
/* 345:316 */     Dsgets.dsgets(ishift, which, kev, np, ritz, 0, bounds, 0, shifts, 0);
/* 346:    */   }
/* 347:    */   
/* 348:    */   public void dsgets(int ishift, String which, intW kev, intW np, double[] ritz, int _ritz_offset, double[] bounds, int _bounds_offset, double[] shifts, int _shifts_offset)
/* 349:    */   {
/* 350:321 */     Dsgets.dsgets(ishift, which, kev, np, ritz, _ritz_offset, bounds, _bounds_offset, shifts, _shifts_offset);
/* 351:    */   }
/* 352:    */   
/* 353:    */   public void dsortc(String which, boolean apply, int n, double[] xreal, double[] ximag, double[] y)
/* 354:    */   {
/* 355:326 */     Dsortc.dsortc(which, apply, n, xreal, 0, ximag, 0, y, 0);
/* 356:    */   }
/* 357:    */   
/* 358:    */   public void dsortc(String which, boolean apply, int n, double[] xreal, int _xreal_offset, double[] ximag, int _ximag_offset, double[] y, int _y_offset)
/* 359:    */   {
/* 360:331 */     Dsortc.dsortc(which, apply, n, xreal, _xreal_offset, ximag, _ximag_offset, y, _y_offset);
/* 361:    */   }
/* 362:    */   
/* 363:    */   public void dsortr(String which, boolean apply, int n, double[] x1, double[] x2)
/* 364:    */   {
/* 365:336 */     Dsortr.dsortr(which, apply, n, x1, 0, x2, 0);
/* 366:    */   }
/* 367:    */   
/* 368:    */   public void dsortr(String which, boolean apply, int n, double[] x1, int _x1_offset, double[] x2, int _x2_offset)
/* 369:    */   {
/* 370:341 */     Dsortr.dsortr(which, apply, n, x1, _x1_offset, x2, _x2_offset);
/* 371:    */   }
/* 372:    */   
/* 373:    */   public void dstatn() {}
/* 374:    */   
/* 375:    */   public void dstats() {}
/* 376:    */   
/* 377:    */   public void dstqrb(int n, double[] d, double[] e, double[] z, double[] work, intW info)
/* 378:    */   {
/* 379:356 */     Dstqrb.dstqrb(n, d, 0, e, 0, z, 0, work, 0, info);
/* 380:    */   }
/* 381:    */   
/* 382:    */   public void dstqrb(int n, double[] d, int _d_offset, double[] e, int _e_offset, double[] z, int _z_offset, double[] work, int _work_offset, intW info)
/* 383:    */   {
/* 384:361 */     Dstqrb.dstqrb(n, d, _d_offset, e, _e_offset, z, _z_offset, work, _work_offset, info);
/* 385:    */   }
/* 386:    */   
/* 387:    */   public void sgetv0(intW ido, String bmat, int itry, boolean initv, int n, int j, float[] v, int ldv, float[] resid, floatW rnorm, int[] ipntr, float[] workd, intW ierr)
/* 388:    */   {
/* 389:366 */     Sgetv0.sgetv0(ido, bmat, itry, initv, n, j, v, 0, ldv, resid, 0, rnorm, ipntr, 0, workd, 0, ierr);
/* 390:    */   }
/* 391:    */   
/* 392:    */   public void sgetv0(intW ido, String bmat, int itry, boolean initv, int n, int j, float[] v, int _v_offset, int ldv, float[] resid, int _resid_offset, floatW rnorm, int[] ipntr, int _ipntr_offset, float[] workd, int _workd_offset, intW ierr)
/* 393:    */   {
/* 394:371 */     Sgetv0.sgetv0(ido, bmat, itry, initv, n, j, v, _v_offset, ldv, resid, _resid_offset, rnorm, ipntr, _ipntr_offset, workd, _workd_offset, ierr);
/* 395:    */   }
/* 396:    */   
/* 397:    */   public void slaqrb(boolean wantt, int n, int ilo, int ihi, float[] h, int ldh, float[] wr, float[] wi, float[] z, intW info)
/* 398:    */   {
/* 399:376 */     Slaqrb.slaqrb(wantt, n, ilo, ihi, h, 0, ldh, wr, 0, wi, 0, z, 0, info);
/* 400:    */   }
/* 401:    */   
/* 402:    */   public void slaqrb(boolean wantt, int n, int ilo, int ihi, float[] h, int _h_offset, int ldh, float[] wr, int _wr_offset, float[] wi, int _wi_offset, float[] z, int _z_offset, intW info)
/* 403:    */   {
/* 404:381 */     Slaqrb.slaqrb(wantt, n, ilo, ihi, h, _h_offset, ldh, wr, _wr_offset, wi, _wi_offset, z, _z_offset, info);
/* 405:    */   }
/* 406:    */   
/* 407:    */   public void snaitr(intW ido, String bmat, int n, int k, int np, int nb, float[] resid, floatW rnorm, float[] v, int ldv, float[] h, int ldh, int[] ipntr, float[] workd, intW info)
/* 408:    */   {
/* 409:386 */     Snaitr.snaitr(ido, bmat, n, k, np, nb, resid, 0, rnorm, v, 0, ldv, h, 0, ldh, ipntr, 0, workd, 0, info);
/* 410:    */   }
/* 411:    */   
/* 412:    */   public void snaitr(intW ido, String bmat, int n, int k, int np, int nb, float[] resid, int _resid_offset, floatW rnorm, float[] v, int _v_offset, int ldv, float[] h, int _h_offset, int ldh, int[] ipntr, int _ipntr_offset, float[] workd, int _workd_offset, intW info)
/* 413:    */   {
/* 414:391 */     Snaitr.snaitr(ido, bmat, n, k, np, nb, resid, _resid_offset, rnorm, v, _v_offset, ldv, h, _h_offset, ldh, ipntr, _ipntr_offset, workd, _workd_offset, info);
/* 415:    */   }
/* 416:    */   
/* 417:    */   public void snapps(int n, intW kev, int np, float[] shiftr, float[] shifti, float[] v, int ldv, float[] h, int ldh, float[] resid, float[] q, int ldq, float[] workl, float[] workd)
/* 418:    */   {
/* 419:396 */     Snapps.snapps(n, kev, np, shiftr, 0, shifti, 0, v, 0, ldv, h, 0, ldh, resid, 0, q, 0, ldq, workl, 0, workd, 0);
/* 420:    */   }
/* 421:    */   
/* 422:    */   public void snapps(int n, intW kev, int np, float[] shiftr, int _shiftr_offset, float[] shifti, int _shifti_offset, float[] v, int _v_offset, int ldv, float[] h, int _h_offset, int ldh, float[] resid, int _resid_offset, float[] q, int _q_offset, int ldq, float[] workl, int _workl_offset, float[] workd, int _workd_offset)
/* 423:    */   {
/* 424:401 */     Snapps.snapps(n, kev, np, shiftr, _shiftr_offset, shifti, _shifti_offset, v, _v_offset, ldv, h, _h_offset, ldh, resid, _resid_offset, q, _q_offset, ldq, workl, _workl_offset, workd, _workd_offset);
/* 425:    */   }
/* 426:    */   
/* 427:    */   public void snaup2(intW ido, String bmat, int n, String which, intW nev, intW np, float tol, float[] resid, int mode, int iupd, int ishift, intW mxiter, float[] v, int ldv, float[] h, int ldh, float[] ritzr, float[] ritzi, float[] bounds, float[] q, int ldq, float[] workl, int[] ipntr, float[] workd, intW info)
/* 428:    */   {
/* 429:406 */     Snaup2.snaup2(ido, bmat, n, which, nev, np, tol, resid, 0, mode, iupd, ishift, mxiter, v, 0, ldv, h, 0, ldh, ritzr, 0, ritzi, 0, bounds, 0, q, 0, ldq, workl, 0, ipntr, 0, workd, 0, info);
/* 430:    */   }
/* 431:    */   
/* 432:    */   public void snaup2(intW ido, String bmat, int n, String which, intW nev, intW np, float tol, float[] resid, int _resid_offset, int mode, int iupd, int ishift, intW mxiter, float[] v, int _v_offset, int ldv, float[] h, int _h_offset, int ldh, float[] ritzr, int _ritzr_offset, float[] ritzi, int _ritzi_offset, float[] bounds, int _bounds_offset, float[] q, int _q_offset, int ldq, float[] workl, int _workl_offset, int[] ipntr, int _ipntr_offset, float[] workd, int _workd_offset, intW info)
/* 433:    */   {
/* 434:411 */     Snaup2.snaup2(ido, bmat, n, which, nev, np, tol, resid, _resid_offset, mode, iupd, ishift, mxiter, v, _v_offset, ldv, h, _h_offset, ldh, ritzr, _ritzr_offset, ritzi, _ritzi_offset, bounds, _bounds_offset, q, _q_offset, ldq, workl, _workl_offset, ipntr, _ipntr_offset, workd, _workd_offset, info);
/* 435:    */   }
/* 436:    */   
/* 437:    */   public void snaupd(intW ido, String bmat, int n, String which, int nev, floatW tol, float[] resid, int ncv, float[] v, int ldv, int[] iparam, int[] ipntr, float[] workd, float[] workl, int lworkl, intW info)
/* 438:    */   {
/* 439:416 */     Snaupd.snaupd(ido, bmat, n, which, nev, tol, resid, 0, ncv, v, 0, ldv, iparam, 0, ipntr, 0, workd, 0, workl, 0, lworkl, info);
/* 440:    */   }
/* 441:    */   
/* 442:    */   public void snaupd(intW ido, String bmat, int n, String which, int nev, floatW tol, float[] resid, int _resid_offset, int ncv, float[] v, int _v_offset, int ldv, int[] iparam, int _iparam_offset, int[] ipntr, int _ipntr_offset, float[] workd, int _workd_offset, float[] workl, int _workl_offset, int lworkl, intW info)
/* 443:    */   {
/* 444:421 */     Snaupd.snaupd(ido, bmat, n, which, nev, tol, resid, _resid_offset, ncv, v, _v_offset, ldv, iparam, _iparam_offset, ipntr, _ipntr_offset, workd, _workd_offset, workl, _workl_offset, lworkl, info);
/* 445:    */   }
/* 446:    */   
/* 447:    */   public void snconv(int n, float[] ritzr, float[] ritzi, float[] bounds, float tol, intW nconv)
/* 448:    */   {
/* 449:426 */     Snconv.snconv(n, ritzr, 0, ritzi, 0, bounds, 0, tol, nconv);
/* 450:    */   }
/* 451:    */   
/* 452:    */   public void snconv(int n, float[] ritzr, int _ritzr_offset, float[] ritzi, int _ritzi_offset, float[] bounds, int _bounds_offset, float tol, intW nconv)
/* 453:    */   {
/* 454:431 */     Snconv.snconv(n, ritzr, _ritzr_offset, ritzi, _ritzi_offset, bounds, _bounds_offset, tol, nconv);
/* 455:    */   }
/* 456:    */   
/* 457:    */   public void sneigh(float rnorm, intW n, float[] h, int ldh, float[] ritzr, float[] ritzi, float[] bounds, float[] q, int ldq, float[] workl, intW ierr)
/* 458:    */   {
/* 459:436 */     Sneigh.sneigh(rnorm, n, h, 0, ldh, ritzr, 0, ritzi, 0, bounds, 0, q, 0, ldq, workl, 0, ierr);
/* 460:    */   }
/* 461:    */   
/* 462:    */   public void sneigh(float rnorm, intW n, float[] h, int _h_offset, int ldh, float[] ritzr, int _ritzr_offset, float[] ritzi, int _ritzi_offset, float[] bounds, int _bounds_offset, float[] q, int _q_offset, int ldq, float[] workl, int _workl_offset, intW ierr)
/* 463:    */   {
/* 464:441 */     Sneigh.sneigh(rnorm, n, h, _h_offset, ldh, ritzr, _ritzr_offset, ritzi, _ritzi_offset, bounds, _bounds_offset, q, _q_offset, ldq, workl, _workl_offset, ierr);
/* 465:    */   }
/* 466:    */   
/* 467:    */   public void sneupd(boolean rvec, String howmny, boolean[] select, float[] dr, float[] di, float[] z, int ldz, float sigmar, float sigmai, float[] workev, String bmat, int n, String which, intW nev, float tol, float[] resid, int ncv, float[] v, int ldv, int[] iparam, int[] ipntr, float[] workd, float[] workl, int lworkl, intW info)
/* 468:    */   {
/* 469:446 */     Sneupd.sneupd(rvec, howmny, select, 0, dr, 0, di, 0, z, 0, ldz, sigmar, sigmai, workev, 0, bmat, n, which, nev, tol, resid, 0, ncv, v, 0, ldv, iparam, 0, ipntr, 0, workd, 0, workl, 0, lworkl, info);
/* 470:    */   }
/* 471:    */   
/* 472:    */   public void sneupd(boolean rvec, String howmny, boolean[] select, int _select_offset, float[] dr, int _dr_offset, float[] di, int _di_offset, float[] z, int _z_offset, int ldz, float sigmar, float sigmai, float[] workev, int _workev_offset, String bmat, int n, String which, intW nev, float tol, float[] resid, int _resid_offset, int ncv, float[] v, int _v_offset, int ldv, int[] iparam, int _iparam_offset, int[] ipntr, int _ipntr_offset, float[] workd, int _workd_offset, float[] workl, int _workl_offset, int lworkl, intW info)
/* 473:    */   {
/* 474:451 */     Sneupd.sneupd(rvec, howmny, select, _select_offset, dr, _dr_offset, di, _di_offset, z, _z_offset, ldz, sigmar, sigmai, workev, _workev_offset, bmat, n, which, nev, tol, resid, _resid_offset, ncv, v, _v_offset, ldv, iparam, _iparam_offset, ipntr, _ipntr_offset, workd, _workd_offset, workl, _workl_offset, lworkl, info);
/* 475:    */   }
/* 476:    */   
/* 477:    */   public void sngets(int ishift, String which, intW kev, intW np, float[] ritzr, float[] ritzi, float[] bounds, float[] shiftr, float[] shifti)
/* 478:    */   {
/* 479:456 */     Sngets.sngets(ishift, which, kev, np, ritzr, 0, ritzi, 0, bounds, 0, shiftr, 0, shifti, 0);
/* 480:    */   }
/* 481:    */   
/* 482:    */   public void sngets(int ishift, String which, intW kev, intW np, float[] ritzr, int _ritzr_offset, float[] ritzi, int _ritzi_offset, float[] bounds, int _bounds_offset, float[] shiftr, int _shiftr_offset, float[] shifti, int _shifti_offset)
/* 483:    */   {
/* 484:461 */     Sngets.sngets(ishift, which, kev, np, ritzr, _ritzr_offset, ritzi, _ritzi_offset, bounds, _bounds_offset, shiftr, _shiftr_offset, shifti, _shifti_offset);
/* 485:    */   }
/* 486:    */   
/* 487:    */   public void ssaitr(intW ido, String bmat, int n, int k, int np, int mode, float[] resid, floatW rnorm, float[] v, int ldv, float[] h, int ldh, int[] ipntr, float[] workd, intW info)
/* 488:    */   {
/* 489:466 */     Ssaitr.ssaitr(ido, bmat, n, k, np, mode, resid, 0, rnorm, v, 0, ldv, h, 0, ldh, ipntr, 0, workd, 0, info);
/* 490:    */   }
/* 491:    */   
/* 492:    */   public void ssaitr(intW ido, String bmat, int n, int k, int np, int mode, float[] resid, int _resid_offset, floatW rnorm, float[] v, int _v_offset, int ldv, float[] h, int _h_offset, int ldh, int[] ipntr, int _ipntr_offset, float[] workd, int _workd_offset, intW info)
/* 493:    */   {
/* 494:471 */     Ssaitr.ssaitr(ido, bmat, n, k, np, mode, resid, _resid_offset, rnorm, v, _v_offset, ldv, h, _h_offset, ldh, ipntr, _ipntr_offset, workd, _workd_offset, info);
/* 495:    */   }
/* 496:    */   
/* 497:    */   public void ssapps(int n, int kev, int np, float[] shift, float[] v, int ldv, float[] h, int ldh, float[] resid, float[] q, int ldq, float[] workd)
/* 498:    */   {
/* 499:476 */     Ssapps.ssapps(n, kev, np, shift, 0, v, 0, ldv, h, 0, ldh, resid, 0, q, 0, ldq, workd, 0);
/* 500:    */   }
/* 501:    */   
/* 502:    */   public void ssapps(int n, int kev, int np, float[] shift, int _shift_offset, float[] v, int _v_offset, int ldv, float[] h, int _h_offset, int ldh, float[] resid, int _resid_offset, float[] q, int _q_offset, int ldq, float[] workd, int _workd_offset)
/* 503:    */   {
/* 504:481 */     Ssapps.ssapps(n, kev, np, shift, _shift_offset, v, _v_offset, ldv, h, _h_offset, ldh, resid, _resid_offset, q, _q_offset, ldq, workd, _workd_offset);
/* 505:    */   }
/* 506:    */   
/* 507:    */   public void ssaup2(intW ido, String bmat, int n, String which, intW nev, intW np, float tol, float[] resid, int mode, int iupd, int ishift, intW mxiter, float[] v, int ldv, float[] h, int ldh, float[] ritz, float[] bounds, float[] q, int ldq, float[] workl, int[] ipntr, float[] workd, intW info)
/* 508:    */   {
/* 509:486 */     Ssaup2.ssaup2(ido, bmat, n, which, nev, np, tol, resid, 0, mode, iupd, ishift, mxiter, v, 0, ldv, h, 0, ldh, ritz, 0, bounds, 0, q, 0, ldq, workl, 0, ipntr, 0, workd, 0, info);
/* 510:    */   }
/* 511:    */   
/* 512:    */   public void ssaup2(intW ido, String bmat, int n, String which, intW nev, intW np, float tol, float[] resid, int _resid_offset, int mode, int iupd, int ishift, intW mxiter, float[] v, int _v_offset, int ldv, float[] h, int _h_offset, int ldh, float[] ritz, int _ritz_offset, float[] bounds, int _bounds_offset, float[] q, int _q_offset, int ldq, float[] workl, int _workl_offset, int[] ipntr, int _ipntr_offset, float[] workd, int _workd_offset, intW info)
/* 513:    */   {
/* 514:491 */     Ssaup2.ssaup2(ido, bmat, n, which, nev, np, tol, resid, _resid_offset, mode, iupd, ishift, mxiter, v, _v_offset, ldv, h, _h_offset, ldh, ritz, _ritz_offset, bounds, _bounds_offset, q, _q_offset, ldq, workl, _workl_offset, ipntr, _ipntr_offset, workd, _workd_offset, info);
/* 515:    */   }
/* 516:    */   
/* 517:    */   public void ssaupd(intW ido, String bmat, int n, String which, int nev, floatW tol, float[] resid, int ncv, float[] v, int ldv, int[] iparam, int[] ipntr, float[] workd, float[] workl, int lworkl, intW info)
/* 518:    */   {
/* 519:496 */     Ssaupd.ssaupd(ido, bmat, n, which, nev, tol, resid, 0, ncv, v, 0, ldv, iparam, 0, ipntr, 0, workd, 0, workl, 0, lworkl, info);
/* 520:    */   }
/* 521:    */   
/* 522:    */   public void ssaupd(intW ido, String bmat, int n, String which, int nev, floatW tol, float[] resid, int _resid_offset, int ncv, float[] v, int _v_offset, int ldv, int[] iparam, int _iparam_offset, int[] ipntr, int _ipntr_offset, float[] workd, int _workd_offset, float[] workl, int _workl_offset, int lworkl, intW info)
/* 523:    */   {
/* 524:501 */     Ssaupd.ssaupd(ido, bmat, n, which, nev, tol, resid, _resid_offset, ncv, v, _v_offset, ldv, iparam, _iparam_offset, ipntr, _ipntr_offset, workd, _workd_offset, workl, _workl_offset, lworkl, info);
/* 525:    */   }
/* 526:    */   
/* 527:    */   public void ssconv(int n, float[] ritz, float[] bounds, float tol, intW nconv)
/* 528:    */   {
/* 529:506 */     Ssconv.ssconv(n, ritz, 0, bounds, 0, tol, nconv);
/* 530:    */   }
/* 531:    */   
/* 532:    */   public void ssconv(int n, float[] ritz, int _ritz_offset, float[] bounds, int _bounds_offset, float tol, intW nconv)
/* 533:    */   {
/* 534:511 */     Ssconv.ssconv(n, ritz, _ritz_offset, bounds, _bounds_offset, tol, nconv);
/* 535:    */   }
/* 536:    */   
/* 537:    */   public void sseigt(float rnorm, int n, float[] h, int ldh, float[] eig, float[] bounds, float[] workl, intW ierr)
/* 538:    */   {
/* 539:516 */     Sseigt.sseigt(rnorm, n, h, 0, ldh, eig, 0, bounds, 0, workl, 0, ierr);
/* 540:    */   }
/* 541:    */   
/* 542:    */   public void sseigt(float rnorm, int n, float[] h, int _h_offset, int ldh, float[] eig, int _eig_offset, float[] bounds, int _bounds_offset, float[] workl, int _workl_offset, intW ierr)
/* 543:    */   {
/* 544:521 */     Sseigt.sseigt(rnorm, n, h, _h_offset, ldh, eig, _eig_offset, bounds, _bounds_offset, workl, _workl_offset, ierr);
/* 545:    */   }
/* 546:    */   
/* 547:    */   public void ssesrt(String which, boolean apply, int n, float[] x, int na, float[] a, int lda)
/* 548:    */   {
/* 549:526 */     Ssesrt.ssesrt(which, apply, n, x, 0, na, a, 0, lda);
/* 550:    */   }
/* 551:    */   
/* 552:    */   public void ssesrt(String which, boolean apply, int n, float[] x, int _x_offset, int na, float[] a, int _a_offset, int lda)
/* 553:    */   {
/* 554:531 */     Ssesrt.ssesrt(which, apply, n, x, _x_offset, na, a, _a_offset, lda);
/* 555:    */   }
/* 556:    */   
/* 557:    */   public void sseupd(boolean rvec, String howmny, boolean[] select, float[] d, float[] z, int ldz, float sigma, String bmat, int n, String which, intW nev, float tol, float[] resid, int ncv, float[] v, int ldv, int[] iparam, int[] ipntr, float[] workd, float[] workl, int lworkl, intW info)
/* 558:    */   {
/* 559:536 */     Sseupd.sseupd(rvec, howmny, select, 0, d, 0, z, 0, ldz, sigma, bmat, n, which, nev, tol, resid, 0, ncv, v, 0, ldv, iparam, 0, ipntr, 0, workd, 0, workl, 0, lworkl, info);
/* 560:    */   }
/* 561:    */   
/* 562:    */   public void sseupd(boolean rvec, String howmny, boolean[] select, int _select_offset, float[] d, int _d_offset, float[] z, int _z_offset, int ldz, float sigma, String bmat, int n, String which, intW nev, float tol, float[] resid, int _resid_offset, int ncv, float[] v, int _v_offset, int ldv, int[] iparam, int _iparam_offset, int[] ipntr, int _ipntr_offset, float[] workd, int _workd_offset, float[] workl, int _workl_offset, int lworkl, intW info)
/* 563:    */   {
/* 564:541 */     Sseupd.sseupd(rvec, howmny, select, _select_offset, d, _d_offset, z, _z_offset, ldz, sigma, bmat, n, which, nev, tol, resid, _resid_offset, ncv, v, _v_offset, ldv, iparam, _iparam_offset, ipntr, _ipntr_offset, workd, _workd_offset, workl, _workl_offset, lworkl, info);
/* 565:    */   }
/* 566:    */   
/* 567:    */   public void ssgets(int ishift, String which, intW kev, intW np, float[] ritz, float[] bounds, float[] shifts)
/* 568:    */   {
/* 569:546 */     Ssgets.ssgets(ishift, which, kev, np, ritz, 0, bounds, 0, shifts, 0);
/* 570:    */   }
/* 571:    */   
/* 572:    */   public void ssgets(int ishift, String which, intW kev, intW np, float[] ritz, int _ritz_offset, float[] bounds, int _bounds_offset, float[] shifts, int _shifts_offset)
/* 573:    */   {
/* 574:551 */     Ssgets.ssgets(ishift, which, kev, np, ritz, _ritz_offset, bounds, _bounds_offset, shifts, _shifts_offset);
/* 575:    */   }
/* 576:    */   
/* 577:    */   public void ssortc(String which, boolean apply, int n, float[] xreal, float[] ximag, float[] y)
/* 578:    */   {
/* 579:556 */     Ssortc.ssortc(which, apply, n, xreal, 0, ximag, 0, y, 0);
/* 580:    */   }
/* 581:    */   
/* 582:    */   public void ssortc(String which, boolean apply, int n, float[] xreal, int _xreal_offset, float[] ximag, int _ximag_offset, float[] y, int _y_offset)
/* 583:    */   {
/* 584:561 */     Ssortc.ssortc(which, apply, n, xreal, _xreal_offset, ximag, _ximag_offset, y, _y_offset);
/* 585:    */   }
/* 586:    */   
/* 587:    */   public void ssortr(String which, boolean apply, int n, float[] x1, float[] x2)
/* 588:    */   {
/* 589:566 */     Ssortr.ssortr(which, apply, n, x1, 0, x2, 0);
/* 590:    */   }
/* 591:    */   
/* 592:    */   public void ssortr(String which, boolean apply, int n, float[] x1, int _x1_offset, float[] x2, int _x2_offset)
/* 593:    */   {
/* 594:571 */     Ssortr.ssortr(which, apply, n, x1, _x1_offset, x2, _x2_offset);
/* 595:    */   }
/* 596:    */   
/* 597:    */   public void sstatn() {}
/* 598:    */   
/* 599:    */   public void sstats() {}
/* 600:    */   
/* 601:    */   public void sstqrb(int n, float[] d, float[] e, float[] z, float[] work, intW info)
/* 602:    */   {
/* 603:586 */     Sstqrb.sstqrb(n, d, 0, e, 0, z, 0, work, 0, info);
/* 604:    */   }
/* 605:    */   
/* 606:    */   public void sstqrb(int n, float[] d, int _d_offset, float[] e, int _e_offset, float[] z, int _z_offset, float[] work, int _work_offset, intW info)
/* 607:    */   {
/* 608:591 */     Sstqrb.sstqrb(n, d, _d_offset, e, _e_offset, z, _z_offset, work, _work_offset, info);
/* 609:    */   }
/* 610:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     com.github.fommil.netlib.F2jARPACK
 * JD-Core Version:    0.7.0.1
 */