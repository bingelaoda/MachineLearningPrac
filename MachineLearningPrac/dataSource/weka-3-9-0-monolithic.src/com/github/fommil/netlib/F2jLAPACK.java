/*    1:     */ package com.github.fommil.netlib;
/*    2:     */ 
/*    3:     */ import org.netlib.lapack.Dbdsdc;
/*    4:     */ import org.netlib.lapack.Dbdsqr;
/*    5:     */ import org.netlib.lapack.Ddisna;
/*    6:     */ import org.netlib.lapack.Dgbbrd;
/*    7:     */ import org.netlib.lapack.Dgbcon;
/*    8:     */ import org.netlib.lapack.Dgbequ;
/*    9:     */ import org.netlib.lapack.Dgbrfs;
/*   10:     */ import org.netlib.lapack.Dgbsv;
/*   11:     */ import org.netlib.lapack.Dgbsvx;
/*   12:     */ import org.netlib.lapack.Dgbtf2;
/*   13:     */ import org.netlib.lapack.Dgbtrf;
/*   14:     */ import org.netlib.lapack.Dgbtrs;
/*   15:     */ import org.netlib.lapack.Dgebak;
/*   16:     */ import org.netlib.lapack.Dgebal;
/*   17:     */ import org.netlib.lapack.Dgebd2;
/*   18:     */ import org.netlib.lapack.Dgebrd;
/*   19:     */ import org.netlib.lapack.Dgecon;
/*   20:     */ import org.netlib.lapack.Dgeequ;
/*   21:     */ import org.netlib.lapack.Dgees;
/*   22:     */ import org.netlib.lapack.Dgeesx;
/*   23:     */ import org.netlib.lapack.Dgeev;
/*   24:     */ import org.netlib.lapack.Dgeevx;
/*   25:     */ import org.netlib.lapack.Dgegs;
/*   26:     */ import org.netlib.lapack.Dgegv;
/*   27:     */ import org.netlib.lapack.Dgehd2;
/*   28:     */ import org.netlib.lapack.Dgehrd;
/*   29:     */ import org.netlib.lapack.Dgelq2;
/*   30:     */ import org.netlib.lapack.Dgelqf;
/*   31:     */ import org.netlib.lapack.Dgels;
/*   32:     */ import org.netlib.lapack.Dgelsd;
/*   33:     */ import org.netlib.lapack.Dgelss;
/*   34:     */ import org.netlib.lapack.Dgelsx;
/*   35:     */ import org.netlib.lapack.Dgelsy;
/*   36:     */ import org.netlib.lapack.Dgeql2;
/*   37:     */ import org.netlib.lapack.Dgeqlf;
/*   38:     */ import org.netlib.lapack.Dgeqp3;
/*   39:     */ import org.netlib.lapack.Dgeqpf;
/*   40:     */ import org.netlib.lapack.Dgeqr2;
/*   41:     */ import org.netlib.lapack.Dgeqrf;
/*   42:     */ import org.netlib.lapack.Dgerfs;
/*   43:     */ import org.netlib.lapack.Dgerq2;
/*   44:     */ import org.netlib.lapack.Dgerqf;
/*   45:     */ import org.netlib.lapack.Dgesc2;
/*   46:     */ import org.netlib.lapack.Dgesdd;
/*   47:     */ import org.netlib.lapack.Dgesv;
/*   48:     */ import org.netlib.lapack.Dgesvd;
/*   49:     */ import org.netlib.lapack.Dgesvx;
/*   50:     */ import org.netlib.lapack.Dgetc2;
/*   51:     */ import org.netlib.lapack.Dgetf2;
/*   52:     */ import org.netlib.lapack.Dgetrf;
/*   53:     */ import org.netlib.lapack.Dgetri;
/*   54:     */ import org.netlib.lapack.Dgetrs;
/*   55:     */ import org.netlib.lapack.Dggbak;
/*   56:     */ import org.netlib.lapack.Dggbal;
/*   57:     */ import org.netlib.lapack.Dgges;
/*   58:     */ import org.netlib.lapack.Dggesx;
/*   59:     */ import org.netlib.lapack.Dggev;
/*   60:     */ import org.netlib.lapack.Dggevx;
/*   61:     */ import org.netlib.lapack.Dggglm;
/*   62:     */ import org.netlib.lapack.Dgghrd;
/*   63:     */ import org.netlib.lapack.Dgglse;
/*   64:     */ import org.netlib.lapack.Dggqrf;
/*   65:     */ import org.netlib.lapack.Dggrqf;
/*   66:     */ import org.netlib.lapack.Dggsvd;
/*   67:     */ import org.netlib.lapack.Dggsvp;
/*   68:     */ import org.netlib.lapack.Dgtcon;
/*   69:     */ import org.netlib.lapack.Dgtrfs;
/*   70:     */ import org.netlib.lapack.Dgtsv;
/*   71:     */ import org.netlib.lapack.Dgtsvx;
/*   72:     */ import org.netlib.lapack.Dgttrf;
/*   73:     */ import org.netlib.lapack.Dgttrs;
/*   74:     */ import org.netlib.lapack.Dgtts2;
/*   75:     */ import org.netlib.lapack.Dhgeqz;
/*   76:     */ import org.netlib.lapack.Dhsein;
/*   77:     */ import org.netlib.lapack.Dhseqr;
/*   78:     */ import org.netlib.lapack.Disnan;
/*   79:     */ import org.netlib.lapack.Dlabad;
/*   80:     */ import org.netlib.lapack.Dlabrd;
/*   81:     */ import org.netlib.lapack.Dlacn2;
/*   82:     */ import org.netlib.lapack.Dlacon;
/*   83:     */ import org.netlib.lapack.Dlacpy;
/*   84:     */ import org.netlib.lapack.Dladiv;
/*   85:     */ import org.netlib.lapack.Dlae2;
/*   86:     */ import org.netlib.lapack.Dlaebz;
/*   87:     */ import org.netlib.lapack.Dlaed0;
/*   88:     */ import org.netlib.lapack.Dlaed1;
/*   89:     */ import org.netlib.lapack.Dlaed2;
/*   90:     */ import org.netlib.lapack.Dlaed3;
/*   91:     */ import org.netlib.lapack.Dlaed4;
/*   92:     */ import org.netlib.lapack.Dlaed5;
/*   93:     */ import org.netlib.lapack.Dlaed6;
/*   94:     */ import org.netlib.lapack.Dlaed7;
/*   95:     */ import org.netlib.lapack.Dlaed8;
/*   96:     */ import org.netlib.lapack.Dlaed9;
/*   97:     */ import org.netlib.lapack.Dlaeda;
/*   98:     */ import org.netlib.lapack.Dlaein;
/*   99:     */ import org.netlib.lapack.Dlaev2;
/*  100:     */ import org.netlib.lapack.Dlaexc;
/*  101:     */ import org.netlib.lapack.Dlag2;
/*  102:     */ import org.netlib.lapack.Dlag2s;
/*  103:     */ import org.netlib.lapack.Dlags2;
/*  104:     */ import org.netlib.lapack.Dlagtf;
/*  105:     */ import org.netlib.lapack.Dlagtm;
/*  106:     */ import org.netlib.lapack.Dlagts;
/*  107:     */ import org.netlib.lapack.Dlagv2;
/*  108:     */ import org.netlib.lapack.Dlahqr;
/*  109:     */ import org.netlib.lapack.Dlahr2;
/*  110:     */ import org.netlib.lapack.Dlahrd;
/*  111:     */ import org.netlib.lapack.Dlaic1;
/*  112:     */ import org.netlib.lapack.Dlaisnan;
/*  113:     */ import org.netlib.lapack.Dlaln2;
/*  114:     */ import org.netlib.lapack.Dlals0;
/*  115:     */ import org.netlib.lapack.Dlalsa;
/*  116:     */ import org.netlib.lapack.Dlalsd;
/*  117:     */ import org.netlib.lapack.Dlamc1;
/*  118:     */ import org.netlib.lapack.Dlamc2;
/*  119:     */ import org.netlib.lapack.Dlamc3;
/*  120:     */ import org.netlib.lapack.Dlamc4;
/*  121:     */ import org.netlib.lapack.Dlamc5;
/*  122:     */ import org.netlib.lapack.Dlamch;
/*  123:     */ import org.netlib.lapack.Dlamrg;
/*  124:     */ import org.netlib.lapack.Dlaneg;
/*  125:     */ import org.netlib.lapack.Dlangb;
/*  126:     */ import org.netlib.lapack.Dlange;
/*  127:     */ import org.netlib.lapack.Dlangt;
/*  128:     */ import org.netlib.lapack.Dlanhs;
/*  129:     */ import org.netlib.lapack.Dlansb;
/*  130:     */ import org.netlib.lapack.Dlansp;
/*  131:     */ import org.netlib.lapack.Dlanst;
/*  132:     */ import org.netlib.lapack.Dlansy;
/*  133:     */ import org.netlib.lapack.Dlantb;
/*  134:     */ import org.netlib.lapack.Dlantp;
/*  135:     */ import org.netlib.lapack.Dlantr;
/*  136:     */ import org.netlib.lapack.Dlanv2;
/*  137:     */ import org.netlib.lapack.Dlapll;
/*  138:     */ import org.netlib.lapack.Dlapmt;
/*  139:     */ import org.netlib.lapack.Dlapy2;
/*  140:     */ import org.netlib.lapack.Dlapy3;
/*  141:     */ import org.netlib.lapack.Dlaqgb;
/*  142:     */ import org.netlib.lapack.Dlaqge;
/*  143:     */ import org.netlib.lapack.Dlaqp2;
/*  144:     */ import org.netlib.lapack.Dlaqps;
/*  145:     */ import org.netlib.lapack.Dlaqr0;
/*  146:     */ import org.netlib.lapack.Dlaqr1;
/*  147:     */ import org.netlib.lapack.Dlaqr2;
/*  148:     */ import org.netlib.lapack.Dlaqr3;
/*  149:     */ import org.netlib.lapack.Dlaqr4;
/*  150:     */ import org.netlib.lapack.Dlaqr5;
/*  151:     */ import org.netlib.lapack.Dlaqsb;
/*  152:     */ import org.netlib.lapack.Dlaqsp;
/*  153:     */ import org.netlib.lapack.Dlaqsy;
/*  154:     */ import org.netlib.lapack.Dlaqtr;
/*  155:     */ import org.netlib.lapack.Dlar1v;
/*  156:     */ import org.netlib.lapack.Dlar2v;
/*  157:     */ import org.netlib.lapack.Dlarf;
/*  158:     */ import org.netlib.lapack.Dlarfb;
/*  159:     */ import org.netlib.lapack.Dlarfg;
/*  160:     */ import org.netlib.lapack.Dlarft;
/*  161:     */ import org.netlib.lapack.Dlarfx;
/*  162:     */ import org.netlib.lapack.Dlargv;
/*  163:     */ import org.netlib.lapack.Dlarnv;
/*  164:     */ import org.netlib.lapack.Dlarra;
/*  165:     */ import org.netlib.lapack.Dlarrb;
/*  166:     */ import org.netlib.lapack.Dlarrc;
/*  167:     */ import org.netlib.lapack.Dlarrd;
/*  168:     */ import org.netlib.lapack.Dlarre;
/*  169:     */ import org.netlib.lapack.Dlarrf;
/*  170:     */ import org.netlib.lapack.Dlarrj;
/*  171:     */ import org.netlib.lapack.Dlarrk;
/*  172:     */ import org.netlib.lapack.Dlarrr;
/*  173:     */ import org.netlib.lapack.Dlarrv;
/*  174:     */ import org.netlib.lapack.Dlartg;
/*  175:     */ import org.netlib.lapack.Dlartv;
/*  176:     */ import org.netlib.lapack.Dlaruv;
/*  177:     */ import org.netlib.lapack.Dlarz;
/*  178:     */ import org.netlib.lapack.Dlarzb;
/*  179:     */ import org.netlib.lapack.Dlarzt;
/*  180:     */ import org.netlib.lapack.Dlas2;
/*  181:     */ import org.netlib.lapack.Dlascl;
/*  182:     */ import org.netlib.lapack.Dlasd0;
/*  183:     */ import org.netlib.lapack.Dlasd1;
/*  184:     */ import org.netlib.lapack.Dlasd2;
/*  185:     */ import org.netlib.lapack.Dlasd3;
/*  186:     */ import org.netlib.lapack.Dlasd4;
/*  187:     */ import org.netlib.lapack.Dlasd5;
/*  188:     */ import org.netlib.lapack.Dlasd6;
/*  189:     */ import org.netlib.lapack.Dlasd7;
/*  190:     */ import org.netlib.lapack.Dlasd8;
/*  191:     */ import org.netlib.lapack.Dlasda;
/*  192:     */ import org.netlib.lapack.Dlasdq;
/*  193:     */ import org.netlib.lapack.Dlasdt;
/*  194:     */ import org.netlib.lapack.Dlaset;
/*  195:     */ import org.netlib.lapack.Dlasq1;
/*  196:     */ import org.netlib.lapack.Dlasq2;
/*  197:     */ import org.netlib.lapack.Dlasq3;
/*  198:     */ import org.netlib.lapack.Dlasq4;
/*  199:     */ import org.netlib.lapack.Dlasq5;
/*  200:     */ import org.netlib.lapack.Dlasq6;
/*  201:     */ import org.netlib.lapack.Dlasr;
/*  202:     */ import org.netlib.lapack.Dlasrt;
/*  203:     */ import org.netlib.lapack.Dlassq;
/*  204:     */ import org.netlib.lapack.Dlasv2;
/*  205:     */ import org.netlib.lapack.Dlaswp;
/*  206:     */ import org.netlib.lapack.Dlasy2;
/*  207:     */ import org.netlib.lapack.Dlasyf;
/*  208:     */ import org.netlib.lapack.Dlatbs;
/*  209:     */ import org.netlib.lapack.Dlatdf;
/*  210:     */ import org.netlib.lapack.Dlatps;
/*  211:     */ import org.netlib.lapack.Dlatrd;
/*  212:     */ import org.netlib.lapack.Dlatrs;
/*  213:     */ import org.netlib.lapack.Dlatrz;
/*  214:     */ import org.netlib.lapack.Dlatzm;
/*  215:     */ import org.netlib.lapack.Dlauu2;
/*  216:     */ import org.netlib.lapack.Dlauum;
/*  217:     */ import org.netlib.lapack.Dlazq3;
/*  218:     */ import org.netlib.lapack.Dlazq4;
/*  219:     */ import org.netlib.lapack.Dopgtr;
/*  220:     */ import org.netlib.lapack.Dopmtr;
/*  221:     */ import org.netlib.lapack.Dorg2l;
/*  222:     */ import org.netlib.lapack.Dorg2r;
/*  223:     */ import org.netlib.lapack.Dorgbr;
/*  224:     */ import org.netlib.lapack.Dorghr;
/*  225:     */ import org.netlib.lapack.Dorgl2;
/*  226:     */ import org.netlib.lapack.Dorglq;
/*  227:     */ import org.netlib.lapack.Dorgql;
/*  228:     */ import org.netlib.lapack.Dorgqr;
/*  229:     */ import org.netlib.lapack.Dorgr2;
/*  230:     */ import org.netlib.lapack.Dorgrq;
/*  231:     */ import org.netlib.lapack.Dorgtr;
/*  232:     */ import org.netlib.lapack.Dorm2l;
/*  233:     */ import org.netlib.lapack.Dorm2r;
/*  234:     */ import org.netlib.lapack.Dormbr;
/*  235:     */ import org.netlib.lapack.Dormhr;
/*  236:     */ import org.netlib.lapack.Dorml2;
/*  237:     */ import org.netlib.lapack.Dormlq;
/*  238:     */ import org.netlib.lapack.Dormql;
/*  239:     */ import org.netlib.lapack.Dormqr;
/*  240:     */ import org.netlib.lapack.Dormr2;
/*  241:     */ import org.netlib.lapack.Dormr3;
/*  242:     */ import org.netlib.lapack.Dormrq;
/*  243:     */ import org.netlib.lapack.Dormrz;
/*  244:     */ import org.netlib.lapack.Dormtr;
/*  245:     */ import org.netlib.lapack.Dpbcon;
/*  246:     */ import org.netlib.lapack.Dpbequ;
/*  247:     */ import org.netlib.lapack.Dpbrfs;
/*  248:     */ import org.netlib.lapack.Dpbstf;
/*  249:     */ import org.netlib.lapack.Dpbsv;
/*  250:     */ import org.netlib.lapack.Dpbsvx;
/*  251:     */ import org.netlib.lapack.Dpbtf2;
/*  252:     */ import org.netlib.lapack.Dpbtrf;
/*  253:     */ import org.netlib.lapack.Dpbtrs;
/*  254:     */ import org.netlib.lapack.Dpocon;
/*  255:     */ import org.netlib.lapack.Dpoequ;
/*  256:     */ import org.netlib.lapack.Dporfs;
/*  257:     */ import org.netlib.lapack.Dposv;
/*  258:     */ import org.netlib.lapack.Dposvx;
/*  259:     */ import org.netlib.lapack.Dpotf2;
/*  260:     */ import org.netlib.lapack.Dpotrf;
/*  261:     */ import org.netlib.lapack.Dpotri;
/*  262:     */ import org.netlib.lapack.Dpotrs;
/*  263:     */ import org.netlib.lapack.Dppcon;
/*  264:     */ import org.netlib.lapack.Dppequ;
/*  265:     */ import org.netlib.lapack.Dpprfs;
/*  266:     */ import org.netlib.lapack.Dppsv;
/*  267:     */ import org.netlib.lapack.Dppsvx;
/*  268:     */ import org.netlib.lapack.Dpptrf;
/*  269:     */ import org.netlib.lapack.Dpptri;
/*  270:     */ import org.netlib.lapack.Dpptrs;
/*  271:     */ import org.netlib.lapack.Dptcon;
/*  272:     */ import org.netlib.lapack.Dpteqr;
/*  273:     */ import org.netlib.lapack.Dptrfs;
/*  274:     */ import org.netlib.lapack.Dptsv;
/*  275:     */ import org.netlib.lapack.Dptsvx;
/*  276:     */ import org.netlib.lapack.Dpttrf;
/*  277:     */ import org.netlib.lapack.Dpttrs;
/*  278:     */ import org.netlib.lapack.Dptts2;
/*  279:     */ import org.netlib.lapack.Drscl;
/*  280:     */ import org.netlib.lapack.Dsbev;
/*  281:     */ import org.netlib.lapack.Dsbevd;
/*  282:     */ import org.netlib.lapack.Dsbevx;
/*  283:     */ import org.netlib.lapack.Dsbgst;
/*  284:     */ import org.netlib.lapack.Dsbgv;
/*  285:     */ import org.netlib.lapack.Dsbgvd;
/*  286:     */ import org.netlib.lapack.Dsbgvx;
/*  287:     */ import org.netlib.lapack.Dsbtrd;
/*  288:     */ import org.netlib.lapack.Dsecnd;
/*  289:     */ import org.netlib.lapack.Dsgesv;
/*  290:     */ import org.netlib.lapack.Dspcon;
/*  291:     */ import org.netlib.lapack.Dspev;
/*  292:     */ import org.netlib.lapack.Dspevd;
/*  293:     */ import org.netlib.lapack.Dspevx;
/*  294:     */ import org.netlib.lapack.Dspgst;
/*  295:     */ import org.netlib.lapack.Dspgv;
/*  296:     */ import org.netlib.lapack.Dspgvd;
/*  297:     */ import org.netlib.lapack.Dspgvx;
/*  298:     */ import org.netlib.lapack.Dsprfs;
/*  299:     */ import org.netlib.lapack.Dspsv;
/*  300:     */ import org.netlib.lapack.Dspsvx;
/*  301:     */ import org.netlib.lapack.Dsptrd;
/*  302:     */ import org.netlib.lapack.Dsptrf;
/*  303:     */ import org.netlib.lapack.Dsptri;
/*  304:     */ import org.netlib.lapack.Dsptrs;
/*  305:     */ import org.netlib.lapack.Dstebz;
/*  306:     */ import org.netlib.lapack.Dstedc;
/*  307:     */ import org.netlib.lapack.Dstegr;
/*  308:     */ import org.netlib.lapack.Dstein;
/*  309:     */ import org.netlib.lapack.Dstemr;
/*  310:     */ import org.netlib.lapack.Dsteqr;
/*  311:     */ import org.netlib.lapack.Dsterf;
/*  312:     */ import org.netlib.lapack.Dstev;
/*  313:     */ import org.netlib.lapack.Dstevd;
/*  314:     */ import org.netlib.lapack.Dstevr;
/*  315:     */ import org.netlib.lapack.Dstevx;
/*  316:     */ import org.netlib.lapack.Dsycon;
/*  317:     */ import org.netlib.lapack.Dsyev;
/*  318:     */ import org.netlib.lapack.Dsyevd;
/*  319:     */ import org.netlib.lapack.Dsyevr;
/*  320:     */ import org.netlib.lapack.Dsyevx;
/*  321:     */ import org.netlib.lapack.Dsygs2;
/*  322:     */ import org.netlib.lapack.Dsygst;
/*  323:     */ import org.netlib.lapack.Dsygv;
/*  324:     */ import org.netlib.lapack.Dsygvd;
/*  325:     */ import org.netlib.lapack.Dsygvx;
/*  326:     */ import org.netlib.lapack.Dsyrfs;
/*  327:     */ import org.netlib.lapack.Dsysv;
/*  328:     */ import org.netlib.lapack.Dsysvx;
/*  329:     */ import org.netlib.lapack.Dsytd2;
/*  330:     */ import org.netlib.lapack.Dsytf2;
/*  331:     */ import org.netlib.lapack.Dsytrd;
/*  332:     */ import org.netlib.lapack.Dsytrf;
/*  333:     */ import org.netlib.lapack.Dsytri;
/*  334:     */ import org.netlib.lapack.Dsytrs;
/*  335:     */ import org.netlib.lapack.Dtbcon;
/*  336:     */ import org.netlib.lapack.Dtbrfs;
/*  337:     */ import org.netlib.lapack.Dtbtrs;
/*  338:     */ import org.netlib.lapack.Dtgevc;
/*  339:     */ import org.netlib.lapack.Dtgex2;
/*  340:     */ import org.netlib.lapack.Dtgexc;
/*  341:     */ import org.netlib.lapack.Dtgsen;
/*  342:     */ import org.netlib.lapack.Dtgsja;
/*  343:     */ import org.netlib.lapack.Dtgsna;
/*  344:     */ import org.netlib.lapack.Dtgsy2;
/*  345:     */ import org.netlib.lapack.Dtgsyl;
/*  346:     */ import org.netlib.lapack.Dtpcon;
/*  347:     */ import org.netlib.lapack.Dtprfs;
/*  348:     */ import org.netlib.lapack.Dtptri;
/*  349:     */ import org.netlib.lapack.Dtptrs;
/*  350:     */ import org.netlib.lapack.Dtrcon;
/*  351:     */ import org.netlib.lapack.Dtrevc;
/*  352:     */ import org.netlib.lapack.Dtrexc;
/*  353:     */ import org.netlib.lapack.Dtrrfs;
/*  354:     */ import org.netlib.lapack.Dtrsen;
/*  355:     */ import org.netlib.lapack.Dtrsna;
/*  356:     */ import org.netlib.lapack.Dtrsyl;
/*  357:     */ import org.netlib.lapack.Dtrti2;
/*  358:     */ import org.netlib.lapack.Dtrtri;
/*  359:     */ import org.netlib.lapack.Dtrtrs;
/*  360:     */ import org.netlib.lapack.Dtzrqf;
/*  361:     */ import org.netlib.lapack.Dtzrzf;
/*  362:     */ import org.netlib.lapack.Ieeeck;
/*  363:     */ import org.netlib.lapack.Ilaenv;
/*  364:     */ import org.netlib.lapack.Ilaver;
/*  365:     */ import org.netlib.lapack.Iparmq;
/*  366:     */ import org.netlib.lapack.Lsame;
/*  367:     */ import org.netlib.lapack.Lsamen;
/*  368:     */ import org.netlib.lapack.Sbdsdc;
/*  369:     */ import org.netlib.lapack.Sbdsqr;
/*  370:     */ import org.netlib.lapack.Sdisna;
/*  371:     */ import org.netlib.lapack.Second;
/*  372:     */ import org.netlib.lapack.Sgbbrd;
/*  373:     */ import org.netlib.lapack.Sgbcon;
/*  374:     */ import org.netlib.lapack.Sgbequ;
/*  375:     */ import org.netlib.lapack.Sgbrfs;
/*  376:     */ import org.netlib.lapack.Sgbsv;
/*  377:     */ import org.netlib.lapack.Sgbsvx;
/*  378:     */ import org.netlib.lapack.Sgbtf2;
/*  379:     */ import org.netlib.lapack.Sgbtrf;
/*  380:     */ import org.netlib.lapack.Sgbtrs;
/*  381:     */ import org.netlib.lapack.Sgebak;
/*  382:     */ import org.netlib.lapack.Sgebal;
/*  383:     */ import org.netlib.lapack.Sgebd2;
/*  384:     */ import org.netlib.lapack.Sgebrd;
/*  385:     */ import org.netlib.lapack.Sgecon;
/*  386:     */ import org.netlib.lapack.Sgeequ;
/*  387:     */ import org.netlib.lapack.Sgees;
/*  388:     */ import org.netlib.lapack.Sgeesx;
/*  389:     */ import org.netlib.lapack.Sgeev;
/*  390:     */ import org.netlib.lapack.Sgeevx;
/*  391:     */ import org.netlib.lapack.Sgegs;
/*  392:     */ import org.netlib.lapack.Sgegv;
/*  393:     */ import org.netlib.lapack.Sgehd2;
/*  394:     */ import org.netlib.lapack.Sgehrd;
/*  395:     */ import org.netlib.lapack.Sgelq2;
/*  396:     */ import org.netlib.lapack.Sgelqf;
/*  397:     */ import org.netlib.lapack.Sgels;
/*  398:     */ import org.netlib.lapack.Sgelsd;
/*  399:     */ import org.netlib.lapack.Sgelss;
/*  400:     */ import org.netlib.lapack.Sgelsx;
/*  401:     */ import org.netlib.lapack.Sgelsy;
/*  402:     */ import org.netlib.lapack.Sgeql2;
/*  403:     */ import org.netlib.lapack.Sgeqlf;
/*  404:     */ import org.netlib.lapack.Sgeqp3;
/*  405:     */ import org.netlib.lapack.Sgeqpf;
/*  406:     */ import org.netlib.lapack.Sgeqr2;
/*  407:     */ import org.netlib.lapack.Sgeqrf;
/*  408:     */ import org.netlib.lapack.Sgerfs;
/*  409:     */ import org.netlib.lapack.Sgerq2;
/*  410:     */ import org.netlib.lapack.Sgerqf;
/*  411:     */ import org.netlib.lapack.Sgesc2;
/*  412:     */ import org.netlib.lapack.Sgesdd;
/*  413:     */ import org.netlib.lapack.Sgesv;
/*  414:     */ import org.netlib.lapack.Sgesvd;
/*  415:     */ import org.netlib.lapack.Sgesvx;
/*  416:     */ import org.netlib.lapack.Sgetc2;
/*  417:     */ import org.netlib.lapack.Sgetf2;
/*  418:     */ import org.netlib.lapack.Sgetrf;
/*  419:     */ import org.netlib.lapack.Sgetri;
/*  420:     */ import org.netlib.lapack.Sgetrs;
/*  421:     */ import org.netlib.lapack.Sggbak;
/*  422:     */ import org.netlib.lapack.Sggbal;
/*  423:     */ import org.netlib.lapack.Sgges;
/*  424:     */ import org.netlib.lapack.Sggesx;
/*  425:     */ import org.netlib.lapack.Sggev;
/*  426:     */ import org.netlib.lapack.Sggevx;
/*  427:     */ import org.netlib.lapack.Sggglm;
/*  428:     */ import org.netlib.lapack.Sgghrd;
/*  429:     */ import org.netlib.lapack.Sgglse;
/*  430:     */ import org.netlib.lapack.Sggqrf;
/*  431:     */ import org.netlib.lapack.Sggrqf;
/*  432:     */ import org.netlib.lapack.Sggsvd;
/*  433:     */ import org.netlib.lapack.Sggsvp;
/*  434:     */ import org.netlib.lapack.Sgtcon;
/*  435:     */ import org.netlib.lapack.Sgtrfs;
/*  436:     */ import org.netlib.lapack.Sgtsv;
/*  437:     */ import org.netlib.lapack.Sgtsvx;
/*  438:     */ import org.netlib.lapack.Sgttrf;
/*  439:     */ import org.netlib.lapack.Sgttrs;
/*  440:     */ import org.netlib.lapack.Sgtts2;
/*  441:     */ import org.netlib.lapack.Shgeqz;
/*  442:     */ import org.netlib.lapack.Shsein;
/*  443:     */ import org.netlib.lapack.Shseqr;
/*  444:     */ import org.netlib.lapack.Sisnan;
/*  445:     */ import org.netlib.lapack.Slabad;
/*  446:     */ import org.netlib.lapack.Slabrd;
/*  447:     */ import org.netlib.lapack.Slacn2;
/*  448:     */ import org.netlib.lapack.Slacon;
/*  449:     */ import org.netlib.lapack.Slacpy;
/*  450:     */ import org.netlib.lapack.Sladiv;
/*  451:     */ import org.netlib.lapack.Slae2;
/*  452:     */ import org.netlib.lapack.Slaebz;
/*  453:     */ import org.netlib.lapack.Slaed0;
/*  454:     */ import org.netlib.lapack.Slaed1;
/*  455:     */ import org.netlib.lapack.Slaed2;
/*  456:     */ import org.netlib.lapack.Slaed3;
/*  457:     */ import org.netlib.lapack.Slaed4;
/*  458:     */ import org.netlib.lapack.Slaed5;
/*  459:     */ import org.netlib.lapack.Slaed6;
/*  460:     */ import org.netlib.lapack.Slaed7;
/*  461:     */ import org.netlib.lapack.Slaed8;
/*  462:     */ import org.netlib.lapack.Slaed9;
/*  463:     */ import org.netlib.lapack.Slaeda;
/*  464:     */ import org.netlib.lapack.Slaein;
/*  465:     */ import org.netlib.lapack.Slaev2;
/*  466:     */ import org.netlib.lapack.Slaexc;
/*  467:     */ import org.netlib.lapack.Slag2;
/*  468:     */ import org.netlib.lapack.Slag2d;
/*  469:     */ import org.netlib.lapack.Slags2;
/*  470:     */ import org.netlib.lapack.Slagtf;
/*  471:     */ import org.netlib.lapack.Slagtm;
/*  472:     */ import org.netlib.lapack.Slagts;
/*  473:     */ import org.netlib.lapack.Slagv2;
/*  474:     */ import org.netlib.lapack.Slahqr;
/*  475:     */ import org.netlib.lapack.Slahr2;
/*  476:     */ import org.netlib.lapack.Slahrd;
/*  477:     */ import org.netlib.lapack.Slaic1;
/*  478:     */ import org.netlib.lapack.Slaisnan;
/*  479:     */ import org.netlib.lapack.Slaln2;
/*  480:     */ import org.netlib.lapack.Slals0;
/*  481:     */ import org.netlib.lapack.Slalsa;
/*  482:     */ import org.netlib.lapack.Slalsd;
/*  483:     */ import org.netlib.lapack.Slamc1;
/*  484:     */ import org.netlib.lapack.Slamc2;
/*  485:     */ import org.netlib.lapack.Slamc3;
/*  486:     */ import org.netlib.lapack.Slamc4;
/*  487:     */ import org.netlib.lapack.Slamc5;
/*  488:     */ import org.netlib.lapack.Slamch;
/*  489:     */ import org.netlib.lapack.Slamrg;
/*  490:     */ import org.netlib.lapack.Slaneg;
/*  491:     */ import org.netlib.lapack.Slangb;
/*  492:     */ import org.netlib.lapack.Slange;
/*  493:     */ import org.netlib.lapack.Slangt;
/*  494:     */ import org.netlib.lapack.Slanhs;
/*  495:     */ import org.netlib.lapack.Slansb;
/*  496:     */ import org.netlib.lapack.Slansp;
/*  497:     */ import org.netlib.lapack.Slanst;
/*  498:     */ import org.netlib.lapack.Slansy;
/*  499:     */ import org.netlib.lapack.Slantb;
/*  500:     */ import org.netlib.lapack.Slantp;
/*  501:     */ import org.netlib.lapack.Slantr;
/*  502:     */ import org.netlib.lapack.Slanv2;
/*  503:     */ import org.netlib.lapack.Slapll;
/*  504:     */ import org.netlib.lapack.Slapmt;
/*  505:     */ import org.netlib.lapack.Slapy2;
/*  506:     */ import org.netlib.lapack.Slapy3;
/*  507:     */ import org.netlib.lapack.Slaqgb;
/*  508:     */ import org.netlib.lapack.Slaqge;
/*  509:     */ import org.netlib.lapack.Slaqp2;
/*  510:     */ import org.netlib.lapack.Slaqps;
/*  511:     */ import org.netlib.lapack.Slaqr0;
/*  512:     */ import org.netlib.lapack.Slaqr1;
/*  513:     */ import org.netlib.lapack.Slaqr2;
/*  514:     */ import org.netlib.lapack.Slaqr3;
/*  515:     */ import org.netlib.lapack.Slaqr4;
/*  516:     */ import org.netlib.lapack.Slaqr5;
/*  517:     */ import org.netlib.lapack.Slaqsb;
/*  518:     */ import org.netlib.lapack.Slaqsp;
/*  519:     */ import org.netlib.lapack.Slaqsy;
/*  520:     */ import org.netlib.lapack.Slaqtr;
/*  521:     */ import org.netlib.lapack.Slar1v;
/*  522:     */ import org.netlib.lapack.Slar2v;
/*  523:     */ import org.netlib.lapack.Slarf;
/*  524:     */ import org.netlib.lapack.Slarfb;
/*  525:     */ import org.netlib.lapack.Slarfg;
/*  526:     */ import org.netlib.lapack.Slarft;
/*  527:     */ import org.netlib.lapack.Slarfx;
/*  528:     */ import org.netlib.lapack.Slargv;
/*  529:     */ import org.netlib.lapack.Slarnv;
/*  530:     */ import org.netlib.lapack.Slarra;
/*  531:     */ import org.netlib.lapack.Slarrb;
/*  532:     */ import org.netlib.lapack.Slarrc;
/*  533:     */ import org.netlib.lapack.Slarrd;
/*  534:     */ import org.netlib.lapack.Slarre;
/*  535:     */ import org.netlib.lapack.Slarrf;
/*  536:     */ import org.netlib.lapack.Slarrj;
/*  537:     */ import org.netlib.lapack.Slarrk;
/*  538:     */ import org.netlib.lapack.Slarrr;
/*  539:     */ import org.netlib.lapack.Slarrv;
/*  540:     */ import org.netlib.lapack.Slartg;
/*  541:     */ import org.netlib.lapack.Slartv;
/*  542:     */ import org.netlib.lapack.Slaruv;
/*  543:     */ import org.netlib.lapack.Slarz;
/*  544:     */ import org.netlib.lapack.Slarzb;
/*  545:     */ import org.netlib.lapack.Slarzt;
/*  546:     */ import org.netlib.lapack.Slas2;
/*  547:     */ import org.netlib.lapack.Slascl;
/*  548:     */ import org.netlib.lapack.Slasd0;
/*  549:     */ import org.netlib.lapack.Slasd1;
/*  550:     */ import org.netlib.lapack.Slasd2;
/*  551:     */ import org.netlib.lapack.Slasd3;
/*  552:     */ import org.netlib.lapack.Slasd4;
/*  553:     */ import org.netlib.lapack.Slasd5;
/*  554:     */ import org.netlib.lapack.Slasd6;
/*  555:     */ import org.netlib.lapack.Slasd7;
/*  556:     */ import org.netlib.lapack.Slasd8;
/*  557:     */ import org.netlib.lapack.Slasda;
/*  558:     */ import org.netlib.lapack.Slasdq;
/*  559:     */ import org.netlib.lapack.Slasdt;
/*  560:     */ import org.netlib.lapack.Slaset;
/*  561:     */ import org.netlib.lapack.Slasq1;
/*  562:     */ import org.netlib.lapack.Slasq2;
/*  563:     */ import org.netlib.lapack.Slasq3;
/*  564:     */ import org.netlib.lapack.Slasq4;
/*  565:     */ import org.netlib.lapack.Slasq5;
/*  566:     */ import org.netlib.lapack.Slasq6;
/*  567:     */ import org.netlib.lapack.Slasr;
/*  568:     */ import org.netlib.lapack.Slasrt;
/*  569:     */ import org.netlib.lapack.Slassq;
/*  570:     */ import org.netlib.lapack.Slasv2;
/*  571:     */ import org.netlib.lapack.Slaswp;
/*  572:     */ import org.netlib.lapack.Slasy2;
/*  573:     */ import org.netlib.lapack.Slasyf;
/*  574:     */ import org.netlib.lapack.Slatbs;
/*  575:     */ import org.netlib.lapack.Slatdf;
/*  576:     */ import org.netlib.lapack.Slatps;
/*  577:     */ import org.netlib.lapack.Slatrd;
/*  578:     */ import org.netlib.lapack.Slatrs;
/*  579:     */ import org.netlib.lapack.Slatrz;
/*  580:     */ import org.netlib.lapack.Slatzm;
/*  581:     */ import org.netlib.lapack.Slauu2;
/*  582:     */ import org.netlib.lapack.Slauum;
/*  583:     */ import org.netlib.lapack.Slazq3;
/*  584:     */ import org.netlib.lapack.Slazq4;
/*  585:     */ import org.netlib.lapack.Sopgtr;
/*  586:     */ import org.netlib.lapack.Sopmtr;
/*  587:     */ import org.netlib.lapack.Sorg2l;
/*  588:     */ import org.netlib.lapack.Sorg2r;
/*  589:     */ import org.netlib.lapack.Sorgbr;
/*  590:     */ import org.netlib.lapack.Sorghr;
/*  591:     */ import org.netlib.lapack.Sorgl2;
/*  592:     */ import org.netlib.lapack.Sorglq;
/*  593:     */ import org.netlib.lapack.Sorgql;
/*  594:     */ import org.netlib.lapack.Sorgqr;
/*  595:     */ import org.netlib.lapack.Sorgr2;
/*  596:     */ import org.netlib.lapack.Sorgrq;
/*  597:     */ import org.netlib.lapack.Sorgtr;
/*  598:     */ import org.netlib.lapack.Sorm2l;
/*  599:     */ import org.netlib.lapack.Sorm2r;
/*  600:     */ import org.netlib.lapack.Sormbr;
/*  601:     */ import org.netlib.lapack.Sormhr;
/*  602:     */ import org.netlib.lapack.Sorml2;
/*  603:     */ import org.netlib.lapack.Sormlq;
/*  604:     */ import org.netlib.lapack.Sormql;
/*  605:     */ import org.netlib.lapack.Sormqr;
/*  606:     */ import org.netlib.lapack.Sormr2;
/*  607:     */ import org.netlib.lapack.Sormr3;
/*  608:     */ import org.netlib.lapack.Sormrq;
/*  609:     */ import org.netlib.lapack.Sormrz;
/*  610:     */ import org.netlib.lapack.Sormtr;
/*  611:     */ import org.netlib.lapack.Spbcon;
/*  612:     */ import org.netlib.lapack.Spbequ;
/*  613:     */ import org.netlib.lapack.Spbrfs;
/*  614:     */ import org.netlib.lapack.Spbstf;
/*  615:     */ import org.netlib.lapack.Spbsv;
/*  616:     */ import org.netlib.lapack.Spbsvx;
/*  617:     */ import org.netlib.lapack.Spbtf2;
/*  618:     */ import org.netlib.lapack.Spbtrf;
/*  619:     */ import org.netlib.lapack.Spbtrs;
/*  620:     */ import org.netlib.lapack.Spocon;
/*  621:     */ import org.netlib.lapack.Spoequ;
/*  622:     */ import org.netlib.lapack.Sporfs;
/*  623:     */ import org.netlib.lapack.Sposv;
/*  624:     */ import org.netlib.lapack.Sposvx;
/*  625:     */ import org.netlib.lapack.Spotf2;
/*  626:     */ import org.netlib.lapack.Spotrf;
/*  627:     */ import org.netlib.lapack.Spotri;
/*  628:     */ import org.netlib.lapack.Spotrs;
/*  629:     */ import org.netlib.lapack.Sppcon;
/*  630:     */ import org.netlib.lapack.Sppequ;
/*  631:     */ import org.netlib.lapack.Spprfs;
/*  632:     */ import org.netlib.lapack.Sppsv;
/*  633:     */ import org.netlib.lapack.Sppsvx;
/*  634:     */ import org.netlib.lapack.Spptrf;
/*  635:     */ import org.netlib.lapack.Spptri;
/*  636:     */ import org.netlib.lapack.Spptrs;
/*  637:     */ import org.netlib.lapack.Sptcon;
/*  638:     */ import org.netlib.lapack.Spteqr;
/*  639:     */ import org.netlib.lapack.Sptrfs;
/*  640:     */ import org.netlib.lapack.Sptsv;
/*  641:     */ import org.netlib.lapack.Sptsvx;
/*  642:     */ import org.netlib.lapack.Spttrf;
/*  643:     */ import org.netlib.lapack.Spttrs;
/*  644:     */ import org.netlib.lapack.Sptts2;
/*  645:     */ import org.netlib.lapack.Srscl;
/*  646:     */ import org.netlib.lapack.Ssbev;
/*  647:     */ import org.netlib.lapack.Ssbevd;
/*  648:     */ import org.netlib.lapack.Ssbevx;
/*  649:     */ import org.netlib.lapack.Ssbgst;
/*  650:     */ import org.netlib.lapack.Ssbgv;
/*  651:     */ import org.netlib.lapack.Ssbgvd;
/*  652:     */ import org.netlib.lapack.Ssbgvx;
/*  653:     */ import org.netlib.lapack.Ssbtrd;
/*  654:     */ import org.netlib.lapack.Sspcon;
/*  655:     */ import org.netlib.lapack.Sspev;
/*  656:     */ import org.netlib.lapack.Sspevd;
/*  657:     */ import org.netlib.lapack.Sspevx;
/*  658:     */ import org.netlib.lapack.Sspgst;
/*  659:     */ import org.netlib.lapack.Sspgv;
/*  660:     */ import org.netlib.lapack.Sspgvd;
/*  661:     */ import org.netlib.lapack.Sspgvx;
/*  662:     */ import org.netlib.lapack.Ssprfs;
/*  663:     */ import org.netlib.lapack.Sspsv;
/*  664:     */ import org.netlib.lapack.Sspsvx;
/*  665:     */ import org.netlib.lapack.Ssptrd;
/*  666:     */ import org.netlib.lapack.Ssptrf;
/*  667:     */ import org.netlib.lapack.Ssptri;
/*  668:     */ import org.netlib.lapack.Ssptrs;
/*  669:     */ import org.netlib.lapack.Sstebz;
/*  670:     */ import org.netlib.lapack.Sstedc;
/*  671:     */ import org.netlib.lapack.Sstegr;
/*  672:     */ import org.netlib.lapack.Sstein;
/*  673:     */ import org.netlib.lapack.Sstemr;
/*  674:     */ import org.netlib.lapack.Ssteqr;
/*  675:     */ import org.netlib.lapack.Ssterf;
/*  676:     */ import org.netlib.lapack.Sstev;
/*  677:     */ import org.netlib.lapack.Sstevd;
/*  678:     */ import org.netlib.lapack.Sstevr;
/*  679:     */ import org.netlib.lapack.Sstevx;
/*  680:     */ import org.netlib.lapack.Ssycon;
/*  681:     */ import org.netlib.lapack.Ssyev;
/*  682:     */ import org.netlib.lapack.Ssyevd;
/*  683:     */ import org.netlib.lapack.Ssyevr;
/*  684:     */ import org.netlib.lapack.Ssyevx;
/*  685:     */ import org.netlib.lapack.Ssygs2;
/*  686:     */ import org.netlib.lapack.Ssygst;
/*  687:     */ import org.netlib.lapack.Ssygv;
/*  688:     */ import org.netlib.lapack.Ssygvd;
/*  689:     */ import org.netlib.lapack.Ssygvx;
/*  690:     */ import org.netlib.lapack.Ssyrfs;
/*  691:     */ import org.netlib.lapack.Ssysv;
/*  692:     */ import org.netlib.lapack.Ssysvx;
/*  693:     */ import org.netlib.lapack.Ssytd2;
/*  694:     */ import org.netlib.lapack.Ssytf2;
/*  695:     */ import org.netlib.lapack.Ssytrd;
/*  696:     */ import org.netlib.lapack.Ssytrf;
/*  697:     */ import org.netlib.lapack.Ssytri;
/*  698:     */ import org.netlib.lapack.Ssytrs;
/*  699:     */ import org.netlib.lapack.Stbcon;
/*  700:     */ import org.netlib.lapack.Stbrfs;
/*  701:     */ import org.netlib.lapack.Stbtrs;
/*  702:     */ import org.netlib.lapack.Stgevc;
/*  703:     */ import org.netlib.lapack.Stgex2;
/*  704:     */ import org.netlib.lapack.Stgexc;
/*  705:     */ import org.netlib.lapack.Stgsen;
/*  706:     */ import org.netlib.lapack.Stgsja;
/*  707:     */ import org.netlib.lapack.Stgsna;
/*  708:     */ import org.netlib.lapack.Stgsy2;
/*  709:     */ import org.netlib.lapack.Stgsyl;
/*  710:     */ import org.netlib.lapack.Stpcon;
/*  711:     */ import org.netlib.lapack.Stprfs;
/*  712:     */ import org.netlib.lapack.Stptri;
/*  713:     */ import org.netlib.lapack.Stptrs;
/*  714:     */ import org.netlib.lapack.Strcon;
/*  715:     */ import org.netlib.lapack.Strevc;
/*  716:     */ import org.netlib.lapack.Strexc;
/*  717:     */ import org.netlib.lapack.Strrfs;
/*  718:     */ import org.netlib.lapack.Strsen;
/*  719:     */ import org.netlib.lapack.Strsna;
/*  720:     */ import org.netlib.lapack.Strsyl;
/*  721:     */ import org.netlib.lapack.Strti2;
/*  722:     */ import org.netlib.lapack.Strtri;
/*  723:     */ import org.netlib.lapack.Strtrs;
/*  724:     */ import org.netlib.lapack.Stzrqf;
/*  725:     */ import org.netlib.lapack.Stzrzf;
/*  726:     */ import org.netlib.util.StringW;
/*  727:     */ import org.netlib.util.booleanW;
/*  728:     */ import org.netlib.util.doubleW;
/*  729:     */ import org.netlib.util.floatW;
/*  730:     */ import org.netlib.util.intW;
/*  731:     */ 
/*  732:     */ public class F2jLAPACK
/*  733:     */   extends LAPACK
/*  734:     */ {
/*  735:     */   public void dbdsdc(String uplo, String compq, int n, double[] d, double[] e, double[] u, int ldu, double[] vt, int ldvt, double[] q, int[] iq, double[] work, int[] iwork, intW info)
/*  736:     */   {
/*  737:  41 */     Dbdsdc.dbdsdc(uplo, compq, n, d, 0, e, 0, u, 0, ldu, vt, 0, ldvt, q, 0, iq, 0, work, 0, iwork, 0, info);
/*  738:     */   }
/*  739:     */   
/*  740:     */   public void dbdsdc(String uplo, String compq, int n, double[] d, int _d_offset, double[] e, int _e_offset, double[] u, int _u_offset, int ldu, double[] vt, int _vt_offset, int ldvt, double[] q, int _q_offset, int[] iq, int _iq_offset, double[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/*  741:     */   {
/*  742:  46 */     Dbdsdc.dbdsdc(uplo, compq, n, d, _d_offset, e, _e_offset, u, _u_offset, ldu, vt, _vt_offset, ldvt, q, _q_offset, iq, _iq_offset, work, _work_offset, iwork, _iwork_offset, info);
/*  743:     */   }
/*  744:     */   
/*  745:     */   public void dbdsqr(String uplo, int n, int ncvt, int nru, int ncc, double[] d, double[] e, double[] vt, int ldvt, double[] u, int ldu, double[] c, int Ldc, double[] work, intW info)
/*  746:     */   {
/*  747:  51 */     Dbdsqr.dbdsqr(uplo, n, ncvt, nru, ncc, d, 0, e, 0, vt, 0, ldvt, u, 0, ldu, c, 0, Ldc, work, 0, info);
/*  748:     */   }
/*  749:     */   
/*  750:     */   public void dbdsqr(String uplo, int n, int ncvt, int nru, int ncc, double[] d, int _d_offset, double[] e, int _e_offset, double[] vt, int _vt_offset, int ldvt, double[] u, int _u_offset, int ldu, double[] c, int _c_offset, int Ldc, double[] work, int _work_offset, intW info)
/*  751:     */   {
/*  752:  56 */     Dbdsqr.dbdsqr(uplo, n, ncvt, nru, ncc, d, _d_offset, e, _e_offset, vt, _vt_offset, ldvt, u, _u_offset, ldu, c, _c_offset, Ldc, work, _work_offset, info);
/*  753:     */   }
/*  754:     */   
/*  755:     */   public void ddisna(String job, int m, int n, double[] d, double[] sep, intW info)
/*  756:     */   {
/*  757:  61 */     Ddisna.ddisna(job, m, n, d, 0, sep, 0, info);
/*  758:     */   }
/*  759:     */   
/*  760:     */   public void ddisna(String job, int m, int n, double[] d, int _d_offset, double[] sep, int _sep_offset, intW info)
/*  761:     */   {
/*  762:  66 */     Ddisna.ddisna(job, m, n, d, _d_offset, sep, _sep_offset, info);
/*  763:     */   }
/*  764:     */   
/*  765:     */   public void dgbbrd(String vect, int m, int n, int ncc, int kl, int ku, double[] ab, int ldab, double[] d, double[] e, double[] q, int ldq, double[] pt, int ldpt, double[] c, int Ldc, double[] work, intW info)
/*  766:     */   {
/*  767:  71 */     Dgbbrd.dgbbrd(vect, m, n, ncc, kl, ku, ab, 0, ldab, d, 0, e, 0, q, 0, ldq, pt, 0, ldpt, c, 0, Ldc, work, 0, info);
/*  768:     */   }
/*  769:     */   
/*  770:     */   public void dgbbrd(String vect, int m, int n, int ncc, int kl, int ku, double[] ab, int _ab_offset, int ldab, double[] d, int _d_offset, double[] e, int _e_offset, double[] q, int _q_offset, int ldq, double[] pt, int _pt_offset, int ldpt, double[] c, int _c_offset, int Ldc, double[] work, int _work_offset, intW info)
/*  771:     */   {
/*  772:  76 */     Dgbbrd.dgbbrd(vect, m, n, ncc, kl, ku, ab, _ab_offset, ldab, d, _d_offset, e, _e_offset, q, _q_offset, ldq, pt, _pt_offset, ldpt, c, _c_offset, Ldc, work, _work_offset, info);
/*  773:     */   }
/*  774:     */   
/*  775:     */   public void dgbcon(String norm, int n, int kl, int ku, double[] ab, int ldab, int[] ipiv, double anorm, doubleW rcond, double[] work, int[] iwork, intW info)
/*  776:     */   {
/*  777:  81 */     Dgbcon.dgbcon(norm, n, kl, ku, ab, 0, ldab, ipiv, 0, anorm, rcond, work, 0, iwork, 0, info);
/*  778:     */   }
/*  779:     */   
/*  780:     */   public void dgbcon(String norm, int n, int kl, int ku, double[] ab, int _ab_offset, int ldab, int[] ipiv, int _ipiv_offset, double anorm, doubleW rcond, double[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/*  781:     */   {
/*  782:  86 */     Dgbcon.dgbcon(norm, n, kl, ku, ab, _ab_offset, ldab, ipiv, _ipiv_offset, anorm, rcond, work, _work_offset, iwork, _iwork_offset, info);
/*  783:     */   }
/*  784:     */   
/*  785:     */   public void dgbequ(int m, int n, int kl, int ku, double[] ab, int ldab, double[] r, double[] c, doubleW rowcnd, doubleW colcnd, doubleW amax, intW info)
/*  786:     */   {
/*  787:  91 */     Dgbequ.dgbequ(m, n, kl, ku, ab, 0, ldab, r, 0, c, 0, rowcnd, colcnd, amax, info);
/*  788:     */   }
/*  789:     */   
/*  790:     */   public void dgbequ(int m, int n, int kl, int ku, double[] ab, int _ab_offset, int ldab, double[] r, int _r_offset, double[] c, int _c_offset, doubleW rowcnd, doubleW colcnd, doubleW amax, intW info)
/*  791:     */   {
/*  792:  96 */     Dgbequ.dgbequ(m, n, kl, ku, ab, _ab_offset, ldab, r, _r_offset, c, _c_offset, rowcnd, colcnd, amax, info);
/*  793:     */   }
/*  794:     */   
/*  795:     */   public void dgbrfs(String trans, int n, int kl, int ku, int nrhs, double[] ab, int ldab, double[] afb, int ldafb, int[] ipiv, double[] b, int ldb, double[] x, int ldx, double[] ferr, double[] berr, double[] work, int[] iwork, intW info)
/*  796:     */   {
/*  797: 101 */     Dgbrfs.dgbrfs(trans, n, kl, ku, nrhs, ab, 0, ldab, afb, 0, ldafb, ipiv, 0, b, 0, ldb, x, 0, ldx, ferr, 0, berr, 0, work, 0, iwork, 0, info);
/*  798:     */   }
/*  799:     */   
/*  800:     */   public void dgbrfs(String trans, int n, int kl, int ku, int nrhs, double[] ab, int _ab_offset, int ldab, double[] afb, int _afb_offset, int ldafb, int[] ipiv, int _ipiv_offset, double[] b, int _b_offset, int ldb, double[] x, int _x_offset, int ldx, double[] ferr, int _ferr_offset, double[] berr, int _berr_offset, double[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/*  801:     */   {
/*  802: 106 */     Dgbrfs.dgbrfs(trans, n, kl, ku, nrhs, ab, _ab_offset, ldab, afb, _afb_offset, ldafb, ipiv, _ipiv_offset, b, _b_offset, ldb, x, _x_offset, ldx, ferr, _ferr_offset, berr, _berr_offset, work, _work_offset, iwork, _iwork_offset, info);
/*  803:     */   }
/*  804:     */   
/*  805:     */   public void dgbsv(int n, int kl, int ku, int nrhs, double[] ab, int ldab, int[] ipiv, double[] b, int ldb, intW info)
/*  806:     */   {
/*  807: 111 */     Dgbsv.dgbsv(n, kl, ku, nrhs, ab, 0, ldab, ipiv, 0, b, 0, ldb, info);
/*  808:     */   }
/*  809:     */   
/*  810:     */   public void dgbsv(int n, int kl, int ku, int nrhs, double[] ab, int _ab_offset, int ldab, int[] ipiv, int _ipiv_offset, double[] b, int _b_offset, int ldb, intW info)
/*  811:     */   {
/*  812: 116 */     Dgbsv.dgbsv(n, kl, ku, nrhs, ab, _ab_offset, ldab, ipiv, _ipiv_offset, b, _b_offset, ldb, info);
/*  813:     */   }
/*  814:     */   
/*  815:     */   public void dgbsvx(String fact, String trans, int n, int kl, int ku, int nrhs, double[] ab, int ldab, double[] afb, int ldafb, int[] ipiv, StringW equed, double[] r, double[] c, double[] b, int ldb, double[] x, int ldx, doubleW rcond, double[] ferr, double[] berr, double[] work, int[] iwork, intW info)
/*  816:     */   {
/*  817: 121 */     Dgbsvx.dgbsvx(fact, trans, n, kl, ku, nrhs, ab, 0, ldab, afb, 0, ldafb, ipiv, 0, equed, r, 0, c, 0, b, 0, ldb, x, 0, ldx, rcond, ferr, 0, berr, 0, work, 0, iwork, 0, info);
/*  818:     */   }
/*  819:     */   
/*  820:     */   public void dgbsvx(String fact, String trans, int n, int kl, int ku, int nrhs, double[] ab, int _ab_offset, int ldab, double[] afb, int _afb_offset, int ldafb, int[] ipiv, int _ipiv_offset, StringW equed, double[] r, int _r_offset, double[] c, int _c_offset, double[] b, int _b_offset, int ldb, double[] x, int _x_offset, int ldx, doubleW rcond, double[] ferr, int _ferr_offset, double[] berr, int _berr_offset, double[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/*  821:     */   {
/*  822: 126 */     Dgbsvx.dgbsvx(fact, trans, n, kl, ku, nrhs, ab, _ab_offset, ldab, afb, _afb_offset, ldafb, ipiv, _ipiv_offset, equed, r, _r_offset, c, _c_offset, b, _b_offset, ldb, x, _x_offset, ldx, rcond, ferr, _ferr_offset, berr, _berr_offset, work, _work_offset, iwork, _iwork_offset, info);
/*  823:     */   }
/*  824:     */   
/*  825:     */   public void dgbtf2(int m, int n, int kl, int ku, double[] ab, int ldab, int[] ipiv, intW info)
/*  826:     */   {
/*  827: 131 */     Dgbtf2.dgbtf2(m, n, kl, ku, ab, 0, ldab, ipiv, 0, info);
/*  828:     */   }
/*  829:     */   
/*  830:     */   public void dgbtf2(int m, int n, int kl, int ku, double[] ab, int _ab_offset, int ldab, int[] ipiv, int _ipiv_offset, intW info)
/*  831:     */   {
/*  832: 136 */     Dgbtf2.dgbtf2(m, n, kl, ku, ab, _ab_offset, ldab, ipiv, _ipiv_offset, info);
/*  833:     */   }
/*  834:     */   
/*  835:     */   public void dgbtrf(int m, int n, int kl, int ku, double[] ab, int ldab, int[] ipiv, intW info)
/*  836:     */   {
/*  837: 141 */     Dgbtrf.dgbtrf(m, n, kl, ku, ab, 0, ldab, ipiv, 0, info);
/*  838:     */   }
/*  839:     */   
/*  840:     */   public void dgbtrf(int m, int n, int kl, int ku, double[] ab, int _ab_offset, int ldab, int[] ipiv, int _ipiv_offset, intW info)
/*  841:     */   {
/*  842: 146 */     Dgbtrf.dgbtrf(m, n, kl, ku, ab, _ab_offset, ldab, ipiv, _ipiv_offset, info);
/*  843:     */   }
/*  844:     */   
/*  845:     */   public void dgbtrs(String trans, int n, int kl, int ku, int nrhs, double[] ab, int ldab, int[] ipiv, double[] b, int ldb, intW info)
/*  846:     */   {
/*  847: 151 */     Dgbtrs.dgbtrs(trans, n, kl, ku, nrhs, ab, 0, ldab, ipiv, 0, b, 0, ldb, info);
/*  848:     */   }
/*  849:     */   
/*  850:     */   public void dgbtrs(String trans, int n, int kl, int ku, int nrhs, double[] ab, int _ab_offset, int ldab, int[] ipiv, int _ipiv_offset, double[] b, int _b_offset, int ldb, intW info)
/*  851:     */   {
/*  852: 156 */     Dgbtrs.dgbtrs(trans, n, kl, ku, nrhs, ab, _ab_offset, ldab, ipiv, _ipiv_offset, b, _b_offset, ldb, info);
/*  853:     */   }
/*  854:     */   
/*  855:     */   public void dgebak(String job, String side, int n, int ilo, int ihi, double[] scale, int m, double[] v, int ldv, intW info)
/*  856:     */   {
/*  857: 161 */     Dgebak.dgebak(job, side, n, ilo, ihi, scale, 0, m, v, 0, ldv, info);
/*  858:     */   }
/*  859:     */   
/*  860:     */   public void dgebak(String job, String side, int n, int ilo, int ihi, double[] scale, int _scale_offset, int m, double[] v, int _v_offset, int ldv, intW info)
/*  861:     */   {
/*  862: 166 */     Dgebak.dgebak(job, side, n, ilo, ihi, scale, _scale_offset, m, v, _v_offset, ldv, info);
/*  863:     */   }
/*  864:     */   
/*  865:     */   public void dgebal(String job, int n, double[] a, int lda, intW ilo, intW ihi, double[] scale, intW info)
/*  866:     */   {
/*  867: 171 */     Dgebal.dgebal(job, n, a, 0, lda, ilo, ihi, scale, 0, info);
/*  868:     */   }
/*  869:     */   
/*  870:     */   public void dgebal(String job, int n, double[] a, int _a_offset, int lda, intW ilo, intW ihi, double[] scale, int _scale_offset, intW info)
/*  871:     */   {
/*  872: 176 */     Dgebal.dgebal(job, n, a, _a_offset, lda, ilo, ihi, scale, _scale_offset, info);
/*  873:     */   }
/*  874:     */   
/*  875:     */   public void dgebd2(int m, int n, double[] a, int lda, double[] d, double[] e, double[] tauq, double[] taup, double[] work, intW info)
/*  876:     */   {
/*  877: 181 */     Dgebd2.dgebd2(m, n, a, 0, lda, d, 0, e, 0, tauq, 0, taup, 0, work, 0, info);
/*  878:     */   }
/*  879:     */   
/*  880:     */   public void dgebd2(int m, int n, double[] a, int _a_offset, int lda, double[] d, int _d_offset, double[] e, int _e_offset, double[] tauq, int _tauq_offset, double[] taup, int _taup_offset, double[] work, int _work_offset, intW info)
/*  881:     */   {
/*  882: 186 */     Dgebd2.dgebd2(m, n, a, _a_offset, lda, d, _d_offset, e, _e_offset, tauq, _tauq_offset, taup, _taup_offset, work, _work_offset, info);
/*  883:     */   }
/*  884:     */   
/*  885:     */   public void dgebrd(int m, int n, double[] a, int lda, double[] d, double[] e, double[] tauq, double[] taup, double[] work, int lwork, intW info)
/*  886:     */   {
/*  887: 191 */     Dgebrd.dgebrd(m, n, a, 0, lda, d, 0, e, 0, tauq, 0, taup, 0, work, 0, lwork, info);
/*  888:     */   }
/*  889:     */   
/*  890:     */   public void dgebrd(int m, int n, double[] a, int _a_offset, int lda, double[] d, int _d_offset, double[] e, int _e_offset, double[] tauq, int _tauq_offset, double[] taup, int _taup_offset, double[] work, int _work_offset, int lwork, intW info)
/*  891:     */   {
/*  892: 196 */     Dgebrd.dgebrd(m, n, a, _a_offset, lda, d, _d_offset, e, _e_offset, tauq, _tauq_offset, taup, _taup_offset, work, _work_offset, lwork, info);
/*  893:     */   }
/*  894:     */   
/*  895:     */   public void dgecon(String norm, int n, double[] a, int lda, double anorm, doubleW rcond, double[] work, int[] iwork, intW info)
/*  896:     */   {
/*  897: 201 */     Dgecon.dgecon(norm, n, a, 0, lda, anorm, rcond, work, 0, iwork, 0, info);
/*  898:     */   }
/*  899:     */   
/*  900:     */   public void dgecon(String norm, int n, double[] a, int _a_offset, int lda, double anorm, doubleW rcond, double[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/*  901:     */   {
/*  902: 206 */     Dgecon.dgecon(norm, n, a, _a_offset, lda, anorm, rcond, work, _work_offset, iwork, _iwork_offset, info);
/*  903:     */   }
/*  904:     */   
/*  905:     */   public void dgeequ(int m, int n, double[] a, int lda, double[] r, double[] c, doubleW rowcnd, doubleW colcnd, doubleW amax, intW info)
/*  906:     */   {
/*  907: 211 */     Dgeequ.dgeequ(m, n, a, 0, lda, r, 0, c, 0, rowcnd, colcnd, amax, info);
/*  908:     */   }
/*  909:     */   
/*  910:     */   public void dgeequ(int m, int n, double[] a, int _a_offset, int lda, double[] r, int _r_offset, double[] c, int _c_offset, doubleW rowcnd, doubleW colcnd, doubleW amax, intW info)
/*  911:     */   {
/*  912: 216 */     Dgeequ.dgeequ(m, n, a, _a_offset, lda, r, _r_offset, c, _c_offset, rowcnd, colcnd, amax, info);
/*  913:     */   }
/*  914:     */   
/*  915:     */   public void dgees(String jobvs, String sort, Object select, int n, double[] a, int lda, intW sdim, double[] wr, double[] wi, double[] vs, int ldvs, double[] work, int lwork, boolean[] bwork, intW info)
/*  916:     */   {
/*  917: 221 */     Dgees.dgees(jobvs, sort, select, n, a, 0, lda, sdim, wr, 0, wi, 0, vs, 0, ldvs, work, 0, lwork, bwork, 0, info);
/*  918:     */   }
/*  919:     */   
/*  920:     */   public void dgees(String jobvs, String sort, Object select, int n, double[] a, int _a_offset, int lda, intW sdim, double[] wr, int _wr_offset, double[] wi, int _wi_offset, double[] vs, int _vs_offset, int ldvs, double[] work, int _work_offset, int lwork, boolean[] bwork, int _bwork_offset, intW info)
/*  921:     */   {
/*  922: 226 */     Dgees.dgees(jobvs, sort, select, n, a, _a_offset, lda, sdim, wr, _wr_offset, wi, _wi_offset, vs, _vs_offset, ldvs, work, _work_offset, lwork, bwork, _bwork_offset, info);
/*  923:     */   }
/*  924:     */   
/*  925:     */   public void dgeesx(String jobvs, String sort, Object select, String sense, int n, double[] a, int lda, intW sdim, double[] wr, double[] wi, double[] vs, int ldvs, doubleW rconde, doubleW rcondv, double[] work, int lwork, int[] iwork, int liwork, boolean[] bwork, intW info)
/*  926:     */   {
/*  927: 231 */     Dgeesx.dgeesx(jobvs, sort, select, sense, n, a, 0, lda, sdim, wr, 0, wi, 0, vs, 0, ldvs, rconde, rcondv, work, 0, lwork, iwork, 0, liwork, bwork, 0, info);
/*  928:     */   }
/*  929:     */   
/*  930:     */   public void dgeesx(String jobvs, String sort, Object select, String sense, int n, double[] a, int _a_offset, int lda, intW sdim, double[] wr, int _wr_offset, double[] wi, int _wi_offset, double[] vs, int _vs_offset, int ldvs, doubleW rconde, doubleW rcondv, double[] work, int _work_offset, int lwork, int[] iwork, int _iwork_offset, int liwork, boolean[] bwork, int _bwork_offset, intW info)
/*  931:     */   {
/*  932: 236 */     Dgeesx.dgeesx(jobvs, sort, select, sense, n, a, _a_offset, lda, sdim, wr, _wr_offset, wi, _wi_offset, vs, _vs_offset, ldvs, rconde, rcondv, work, _work_offset, lwork, iwork, _iwork_offset, liwork, bwork, _bwork_offset, info);
/*  933:     */   }
/*  934:     */   
/*  935:     */   public void dgeev(String jobvl, String jobvr, int n, double[] a, int lda, double[] wr, double[] wi, double[] vl, int ldvl, double[] vr, int ldvr, double[] work, int lwork, intW info)
/*  936:     */   {
/*  937: 241 */     Dgeev.dgeev(jobvl, jobvr, n, a, 0, lda, wr, 0, wi, 0, vl, 0, ldvl, vr, 0, ldvr, work, 0, lwork, info);
/*  938:     */   }
/*  939:     */   
/*  940:     */   public void dgeev(String jobvl, String jobvr, int n, double[] a, int _a_offset, int lda, double[] wr, int _wr_offset, double[] wi, int _wi_offset, double[] vl, int _vl_offset, int ldvl, double[] vr, int _vr_offset, int ldvr, double[] work, int _work_offset, int lwork, intW info)
/*  941:     */   {
/*  942: 246 */     Dgeev.dgeev(jobvl, jobvr, n, a, _a_offset, lda, wr, _wr_offset, wi, _wi_offset, vl, _vl_offset, ldvl, vr, _vr_offset, ldvr, work, _work_offset, lwork, info);
/*  943:     */   }
/*  944:     */   
/*  945:     */   public void dgeevx(String balanc, String jobvl, String jobvr, String sense, int n, double[] a, int lda, double[] wr, double[] wi, double[] vl, int ldvl, double[] vr, int ldvr, intW ilo, intW ihi, double[] scale, doubleW abnrm, double[] rconde, double[] rcondv, double[] work, int lwork, int[] iwork, intW info)
/*  946:     */   {
/*  947: 251 */     Dgeevx.dgeevx(balanc, jobvl, jobvr, sense, n, a, 0, lda, wr, 0, wi, 0, vl, 0, ldvl, vr, 0, ldvr, ilo, ihi, scale, 0, abnrm, rconde, 0, rcondv, 0, work, 0, lwork, iwork, 0, info);
/*  948:     */   }
/*  949:     */   
/*  950:     */   public void dgeevx(String balanc, String jobvl, String jobvr, String sense, int n, double[] a, int _a_offset, int lda, double[] wr, int _wr_offset, double[] wi, int _wi_offset, double[] vl, int _vl_offset, int ldvl, double[] vr, int _vr_offset, int ldvr, intW ilo, intW ihi, double[] scale, int _scale_offset, doubleW abnrm, double[] rconde, int _rconde_offset, double[] rcondv, int _rcondv_offset, double[] work, int _work_offset, int lwork, int[] iwork, int _iwork_offset, intW info)
/*  951:     */   {
/*  952: 256 */     Dgeevx.dgeevx(balanc, jobvl, jobvr, sense, n, a, _a_offset, lda, wr, _wr_offset, wi, _wi_offset, vl, _vl_offset, ldvl, vr, _vr_offset, ldvr, ilo, ihi, scale, _scale_offset, abnrm, rconde, _rconde_offset, rcondv, _rcondv_offset, work, _work_offset, lwork, iwork, _iwork_offset, info);
/*  953:     */   }
/*  954:     */   
/*  955:     */   public void dgegs(String jobvsl, String jobvsr, int n, double[] a, int lda, double[] b, int ldb, double[] alphar, double[] alphai, double[] beta, double[] vsl, int ldvsl, double[] vsr, int ldvsr, double[] work, int lwork, intW info)
/*  956:     */   {
/*  957: 261 */     Dgegs.dgegs(jobvsl, jobvsr, n, a, 0, lda, b, 0, ldb, alphar, 0, alphai, 0, beta, 0, vsl, 0, ldvsl, vsr, 0, ldvsr, work, 0, lwork, info);
/*  958:     */   }
/*  959:     */   
/*  960:     */   public void dgegs(String jobvsl, String jobvsr, int n, double[] a, int _a_offset, int lda, double[] b, int _b_offset, int ldb, double[] alphar, int _alphar_offset, double[] alphai, int _alphai_offset, double[] beta, int _beta_offset, double[] vsl, int _vsl_offset, int ldvsl, double[] vsr, int _vsr_offset, int ldvsr, double[] work, int _work_offset, int lwork, intW info)
/*  961:     */   {
/*  962: 266 */     Dgegs.dgegs(jobvsl, jobvsr, n, a, _a_offset, lda, b, _b_offset, ldb, alphar, _alphar_offset, alphai, _alphai_offset, beta, _beta_offset, vsl, _vsl_offset, ldvsl, vsr, _vsr_offset, ldvsr, work, _work_offset, lwork, info);
/*  963:     */   }
/*  964:     */   
/*  965:     */   public void dgegv(String jobvl, String jobvr, int n, double[] a, int lda, double[] b, int ldb, double[] alphar, double[] alphai, double[] beta, double[] vl, int ldvl, double[] vr, int ldvr, double[] work, int lwork, intW info)
/*  966:     */   {
/*  967: 271 */     Dgegv.dgegv(jobvl, jobvr, n, a, 0, lda, b, 0, ldb, alphar, 0, alphai, 0, beta, 0, vl, 0, ldvl, vr, 0, ldvr, work, 0, lwork, info);
/*  968:     */   }
/*  969:     */   
/*  970:     */   public void dgegv(String jobvl, String jobvr, int n, double[] a, int _a_offset, int lda, double[] b, int _b_offset, int ldb, double[] alphar, int _alphar_offset, double[] alphai, int _alphai_offset, double[] beta, int _beta_offset, double[] vl, int _vl_offset, int ldvl, double[] vr, int _vr_offset, int ldvr, double[] work, int _work_offset, int lwork, intW info)
/*  971:     */   {
/*  972: 276 */     Dgegv.dgegv(jobvl, jobvr, n, a, _a_offset, lda, b, _b_offset, ldb, alphar, _alphar_offset, alphai, _alphai_offset, beta, _beta_offset, vl, _vl_offset, ldvl, vr, _vr_offset, ldvr, work, _work_offset, lwork, info);
/*  973:     */   }
/*  974:     */   
/*  975:     */   public void dgehd2(int n, int ilo, int ihi, double[] a, int lda, double[] tau, double[] work, intW info)
/*  976:     */   {
/*  977: 281 */     Dgehd2.dgehd2(n, ilo, ihi, a, 0, lda, tau, 0, work, 0, info);
/*  978:     */   }
/*  979:     */   
/*  980:     */   public void dgehd2(int n, int ilo, int ihi, double[] a, int _a_offset, int lda, double[] tau, int _tau_offset, double[] work, int _work_offset, intW info)
/*  981:     */   {
/*  982: 286 */     Dgehd2.dgehd2(n, ilo, ihi, a, _a_offset, lda, tau, _tau_offset, work, _work_offset, info);
/*  983:     */   }
/*  984:     */   
/*  985:     */   public void dgehrd(int n, int ilo, int ihi, double[] a, int lda, double[] tau, double[] work, int lwork, intW info)
/*  986:     */   {
/*  987: 291 */     Dgehrd.dgehrd(n, ilo, ihi, a, 0, lda, tau, 0, work, 0, lwork, info);
/*  988:     */   }
/*  989:     */   
/*  990:     */   public void dgehrd(int n, int ilo, int ihi, double[] a, int _a_offset, int lda, double[] tau, int _tau_offset, double[] work, int _work_offset, int lwork, intW info)
/*  991:     */   {
/*  992: 296 */     Dgehrd.dgehrd(n, ilo, ihi, a, _a_offset, lda, tau, _tau_offset, work, _work_offset, lwork, info);
/*  993:     */   }
/*  994:     */   
/*  995:     */   public void dgelq2(int m, int n, double[] a, int lda, double[] tau, double[] work, intW info)
/*  996:     */   {
/*  997: 301 */     Dgelq2.dgelq2(m, n, a, 0, lda, tau, 0, work, 0, info);
/*  998:     */   }
/*  999:     */   
/* 1000:     */   public void dgelq2(int m, int n, double[] a, int _a_offset, int lda, double[] tau, int _tau_offset, double[] work, int _work_offset, intW info)
/* 1001:     */   {
/* 1002: 306 */     Dgelq2.dgelq2(m, n, a, _a_offset, lda, tau, _tau_offset, work, _work_offset, info);
/* 1003:     */   }
/* 1004:     */   
/* 1005:     */   public void dgelqf(int m, int n, double[] a, int lda, double[] tau, double[] work, int lwork, intW info)
/* 1006:     */   {
/* 1007: 311 */     Dgelqf.dgelqf(m, n, a, 0, lda, tau, 0, work, 0, lwork, info);
/* 1008:     */   }
/* 1009:     */   
/* 1010:     */   public void dgelqf(int m, int n, double[] a, int _a_offset, int lda, double[] tau, int _tau_offset, double[] work, int _work_offset, int lwork, intW info)
/* 1011:     */   {
/* 1012: 316 */     Dgelqf.dgelqf(m, n, a, _a_offset, lda, tau, _tau_offset, work, _work_offset, lwork, info);
/* 1013:     */   }
/* 1014:     */   
/* 1015:     */   public void dgels(String trans, int m, int n, int nrhs, double[] a, int lda, double[] b, int ldb, double[] work, int lwork, intW info)
/* 1016:     */   {
/* 1017: 321 */     Dgels.dgels(trans, m, n, nrhs, a, 0, lda, b, 0, ldb, work, 0, lwork, info);
/* 1018:     */   }
/* 1019:     */   
/* 1020:     */   public void dgels(String trans, int m, int n, int nrhs, double[] a, int _a_offset, int lda, double[] b, int _b_offset, int ldb, double[] work, int _work_offset, int lwork, intW info)
/* 1021:     */   {
/* 1022: 326 */     Dgels.dgels(trans, m, n, nrhs, a, _a_offset, lda, b, _b_offset, ldb, work, _work_offset, lwork, info);
/* 1023:     */   }
/* 1024:     */   
/* 1025:     */   public void dgelsd(int m, int n, int nrhs, double[] a, int lda, double[] b, int ldb, double[] s, double rcond, intW rank, double[] work, int lwork, int[] iwork, intW info)
/* 1026:     */   {
/* 1027: 331 */     Dgelsd.dgelsd(m, n, nrhs, a, 0, lda, b, 0, ldb, s, 0, rcond, rank, work, 0, lwork, iwork, 0, info);
/* 1028:     */   }
/* 1029:     */   
/* 1030:     */   public void dgelsd(int m, int n, int nrhs, double[] a, int _a_offset, int lda, double[] b, int _b_offset, int ldb, double[] s, int _s_offset, double rcond, intW rank, double[] work, int _work_offset, int lwork, int[] iwork, int _iwork_offset, intW info)
/* 1031:     */   {
/* 1032: 336 */     Dgelsd.dgelsd(m, n, nrhs, a, _a_offset, lda, b, _b_offset, ldb, s, _s_offset, rcond, rank, work, _work_offset, lwork, iwork, _iwork_offset, info);
/* 1033:     */   }
/* 1034:     */   
/* 1035:     */   public void dgelss(int m, int n, int nrhs, double[] a, int lda, double[] b, int ldb, double[] s, double rcond, intW rank, double[] work, int lwork, intW info)
/* 1036:     */   {
/* 1037: 341 */     Dgelss.dgelss(m, n, nrhs, a, 0, lda, b, 0, ldb, s, 0, rcond, rank, work, 0, lwork, info);
/* 1038:     */   }
/* 1039:     */   
/* 1040:     */   public void dgelss(int m, int n, int nrhs, double[] a, int _a_offset, int lda, double[] b, int _b_offset, int ldb, double[] s, int _s_offset, double rcond, intW rank, double[] work, int _work_offset, int lwork, intW info)
/* 1041:     */   {
/* 1042: 346 */     Dgelss.dgelss(m, n, nrhs, a, _a_offset, lda, b, _b_offset, ldb, s, _s_offset, rcond, rank, work, _work_offset, lwork, info);
/* 1043:     */   }
/* 1044:     */   
/* 1045:     */   public void dgelsx(int m, int n, int nrhs, double[] a, int lda, double[] b, int ldb, int[] jpvt, double rcond, intW rank, double[] work, intW info)
/* 1046:     */   {
/* 1047: 351 */     Dgelsx.dgelsx(m, n, nrhs, a, 0, lda, b, 0, ldb, jpvt, 0, rcond, rank, work, 0, info);
/* 1048:     */   }
/* 1049:     */   
/* 1050:     */   public void dgelsx(int m, int n, int nrhs, double[] a, int _a_offset, int lda, double[] b, int _b_offset, int ldb, int[] jpvt, int _jpvt_offset, double rcond, intW rank, double[] work, int _work_offset, intW info)
/* 1051:     */   {
/* 1052: 356 */     Dgelsx.dgelsx(m, n, nrhs, a, _a_offset, lda, b, _b_offset, ldb, jpvt, _jpvt_offset, rcond, rank, work, _work_offset, info);
/* 1053:     */   }
/* 1054:     */   
/* 1055:     */   public void dgelsy(int m, int n, int nrhs, double[] a, int lda, double[] b, int ldb, int[] jpvt, double rcond, intW rank, double[] work, int lwork, intW info)
/* 1056:     */   {
/* 1057: 361 */     Dgelsy.dgelsy(m, n, nrhs, a, 0, lda, b, 0, ldb, jpvt, 0, rcond, rank, work, 0, lwork, info);
/* 1058:     */   }
/* 1059:     */   
/* 1060:     */   public void dgelsy(int m, int n, int nrhs, double[] a, int _a_offset, int lda, double[] b, int _b_offset, int ldb, int[] jpvt, int _jpvt_offset, double rcond, intW rank, double[] work, int _work_offset, int lwork, intW info)
/* 1061:     */   {
/* 1062: 366 */     Dgelsy.dgelsy(m, n, nrhs, a, _a_offset, lda, b, _b_offset, ldb, jpvt, _jpvt_offset, rcond, rank, work, _work_offset, lwork, info);
/* 1063:     */   }
/* 1064:     */   
/* 1065:     */   public void dgeql2(int m, int n, double[] a, int lda, double[] tau, double[] work, intW info)
/* 1066:     */   {
/* 1067: 371 */     Dgeql2.dgeql2(m, n, a, 0, lda, tau, 0, work, 0, info);
/* 1068:     */   }
/* 1069:     */   
/* 1070:     */   public void dgeql2(int m, int n, double[] a, int _a_offset, int lda, double[] tau, int _tau_offset, double[] work, int _work_offset, intW info)
/* 1071:     */   {
/* 1072: 376 */     Dgeql2.dgeql2(m, n, a, _a_offset, lda, tau, _tau_offset, work, _work_offset, info);
/* 1073:     */   }
/* 1074:     */   
/* 1075:     */   public void dgeqlf(int m, int n, double[] a, int lda, double[] tau, double[] work, int lwork, intW info)
/* 1076:     */   {
/* 1077: 381 */     Dgeqlf.dgeqlf(m, n, a, 0, lda, tau, 0, work, 0, lwork, info);
/* 1078:     */   }
/* 1079:     */   
/* 1080:     */   public void dgeqlf(int m, int n, double[] a, int _a_offset, int lda, double[] tau, int _tau_offset, double[] work, int _work_offset, int lwork, intW info)
/* 1081:     */   {
/* 1082: 386 */     Dgeqlf.dgeqlf(m, n, a, _a_offset, lda, tau, _tau_offset, work, _work_offset, lwork, info);
/* 1083:     */   }
/* 1084:     */   
/* 1085:     */   public void dgeqp3(int m, int n, double[] a, int lda, int[] jpvt, double[] tau, double[] work, int lwork, intW info)
/* 1086:     */   {
/* 1087: 391 */     Dgeqp3.dgeqp3(m, n, a, 0, lda, jpvt, 0, tau, 0, work, 0, lwork, info);
/* 1088:     */   }
/* 1089:     */   
/* 1090:     */   public void dgeqp3(int m, int n, double[] a, int _a_offset, int lda, int[] jpvt, int _jpvt_offset, double[] tau, int _tau_offset, double[] work, int _work_offset, int lwork, intW info)
/* 1091:     */   {
/* 1092: 396 */     Dgeqp3.dgeqp3(m, n, a, _a_offset, lda, jpvt, _jpvt_offset, tau, _tau_offset, work, _work_offset, lwork, info);
/* 1093:     */   }
/* 1094:     */   
/* 1095:     */   public void dgeqpf(int m, int n, double[] a, int lda, int[] jpvt, double[] tau, double[] work, intW info)
/* 1096:     */   {
/* 1097: 401 */     Dgeqpf.dgeqpf(m, n, a, 0, lda, jpvt, 0, tau, 0, work, 0, info);
/* 1098:     */   }
/* 1099:     */   
/* 1100:     */   public void dgeqpf(int m, int n, double[] a, int _a_offset, int lda, int[] jpvt, int _jpvt_offset, double[] tau, int _tau_offset, double[] work, int _work_offset, intW info)
/* 1101:     */   {
/* 1102: 406 */     Dgeqpf.dgeqpf(m, n, a, _a_offset, lda, jpvt, _jpvt_offset, tau, _tau_offset, work, _work_offset, info);
/* 1103:     */   }
/* 1104:     */   
/* 1105:     */   public void dgeqr2(int m, int n, double[] a, int lda, double[] tau, double[] work, intW info)
/* 1106:     */   {
/* 1107: 411 */     Dgeqr2.dgeqr2(m, n, a, 0, lda, tau, 0, work, 0, info);
/* 1108:     */   }
/* 1109:     */   
/* 1110:     */   public void dgeqr2(int m, int n, double[] a, int _a_offset, int lda, double[] tau, int _tau_offset, double[] work, int _work_offset, intW info)
/* 1111:     */   {
/* 1112: 416 */     Dgeqr2.dgeqr2(m, n, a, _a_offset, lda, tau, _tau_offset, work, _work_offset, info);
/* 1113:     */   }
/* 1114:     */   
/* 1115:     */   public void dgeqrf(int m, int n, double[] a, int lda, double[] tau, double[] work, int lwork, intW info)
/* 1116:     */   {
/* 1117: 421 */     Dgeqrf.dgeqrf(m, n, a, 0, lda, tau, 0, work, 0, lwork, info);
/* 1118:     */   }
/* 1119:     */   
/* 1120:     */   public void dgeqrf(int m, int n, double[] a, int _a_offset, int lda, double[] tau, int _tau_offset, double[] work, int _work_offset, int lwork, intW info)
/* 1121:     */   {
/* 1122: 426 */     Dgeqrf.dgeqrf(m, n, a, _a_offset, lda, tau, _tau_offset, work, _work_offset, lwork, info);
/* 1123:     */   }
/* 1124:     */   
/* 1125:     */   public void dgerfs(String trans, int n, int nrhs, double[] a, int lda, double[] af, int ldaf, int[] ipiv, double[] b, int ldb, double[] x, int ldx, double[] ferr, double[] berr, double[] work, int[] iwork, intW info)
/* 1126:     */   {
/* 1127: 431 */     Dgerfs.dgerfs(trans, n, nrhs, a, 0, lda, af, 0, ldaf, ipiv, 0, b, 0, ldb, x, 0, ldx, ferr, 0, berr, 0, work, 0, iwork, 0, info);
/* 1128:     */   }
/* 1129:     */   
/* 1130:     */   public void dgerfs(String trans, int n, int nrhs, double[] a, int _a_offset, int lda, double[] af, int _af_offset, int ldaf, int[] ipiv, int _ipiv_offset, double[] b, int _b_offset, int ldb, double[] x, int _x_offset, int ldx, double[] ferr, int _ferr_offset, double[] berr, int _berr_offset, double[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 1131:     */   {
/* 1132: 436 */     Dgerfs.dgerfs(trans, n, nrhs, a, _a_offset, lda, af, _af_offset, ldaf, ipiv, _ipiv_offset, b, _b_offset, ldb, x, _x_offset, ldx, ferr, _ferr_offset, berr, _berr_offset, work, _work_offset, iwork, _iwork_offset, info);
/* 1133:     */   }
/* 1134:     */   
/* 1135:     */   public void dgerq2(int m, int n, double[] a, int lda, double[] tau, double[] work, intW info)
/* 1136:     */   {
/* 1137: 441 */     Dgerq2.dgerq2(m, n, a, 0, lda, tau, 0, work, 0, info);
/* 1138:     */   }
/* 1139:     */   
/* 1140:     */   public void dgerq2(int m, int n, double[] a, int _a_offset, int lda, double[] tau, int _tau_offset, double[] work, int _work_offset, intW info)
/* 1141:     */   {
/* 1142: 446 */     Dgerq2.dgerq2(m, n, a, _a_offset, lda, tau, _tau_offset, work, _work_offset, info);
/* 1143:     */   }
/* 1144:     */   
/* 1145:     */   public void dgerqf(int m, int n, double[] a, int lda, double[] tau, double[] work, int lwork, intW info)
/* 1146:     */   {
/* 1147: 451 */     Dgerqf.dgerqf(m, n, a, 0, lda, tau, 0, work, 0, lwork, info);
/* 1148:     */   }
/* 1149:     */   
/* 1150:     */   public void dgerqf(int m, int n, double[] a, int _a_offset, int lda, double[] tau, int _tau_offset, double[] work, int _work_offset, int lwork, intW info)
/* 1151:     */   {
/* 1152: 456 */     Dgerqf.dgerqf(m, n, a, _a_offset, lda, tau, _tau_offset, work, _work_offset, lwork, info);
/* 1153:     */   }
/* 1154:     */   
/* 1155:     */   public void dgesc2(int n, double[] a, int lda, double[] rhs, int[] ipiv, int[] jpiv, doubleW scale)
/* 1156:     */   {
/* 1157: 461 */     Dgesc2.dgesc2(n, a, 0, lda, rhs, 0, ipiv, 0, jpiv, 0, scale);
/* 1158:     */   }
/* 1159:     */   
/* 1160:     */   public void dgesc2(int n, double[] a, int _a_offset, int lda, double[] rhs, int _rhs_offset, int[] ipiv, int _ipiv_offset, int[] jpiv, int _jpiv_offset, doubleW scale)
/* 1161:     */   {
/* 1162: 466 */     Dgesc2.dgesc2(n, a, _a_offset, lda, rhs, _rhs_offset, ipiv, _ipiv_offset, jpiv, _jpiv_offset, scale);
/* 1163:     */   }
/* 1164:     */   
/* 1165:     */   public void dgesdd(String jobz, int m, int n, double[] a, int lda, double[] s, double[] u, int ldu, double[] vt, int ldvt, double[] work, int lwork, int[] iwork, intW info)
/* 1166:     */   {
/* 1167: 471 */     Dgesdd.dgesdd(jobz, m, n, a, 0, lda, s, 0, u, 0, ldu, vt, 0, ldvt, work, 0, lwork, iwork, 0, info);
/* 1168:     */   }
/* 1169:     */   
/* 1170:     */   public void dgesdd(String jobz, int m, int n, double[] a, int _a_offset, int lda, double[] s, int _s_offset, double[] u, int _u_offset, int ldu, double[] vt, int _vt_offset, int ldvt, double[] work, int _work_offset, int lwork, int[] iwork, int _iwork_offset, intW info)
/* 1171:     */   {
/* 1172: 476 */     Dgesdd.dgesdd(jobz, m, n, a, _a_offset, lda, s, _s_offset, u, _u_offset, ldu, vt, _vt_offset, ldvt, work, _work_offset, lwork, iwork, _iwork_offset, info);
/* 1173:     */   }
/* 1174:     */   
/* 1175:     */   public void dgesv(int n, int nrhs, double[] a, int lda, int[] ipiv, double[] b, int ldb, intW info)
/* 1176:     */   {
/* 1177: 481 */     Dgesv.dgesv(n, nrhs, a, 0, lda, ipiv, 0, b, 0, ldb, info);
/* 1178:     */   }
/* 1179:     */   
/* 1180:     */   public void dgesv(int n, int nrhs, double[] a, int _a_offset, int lda, int[] ipiv, int _ipiv_offset, double[] b, int _b_offset, int ldb, intW info)
/* 1181:     */   {
/* 1182: 486 */     Dgesv.dgesv(n, nrhs, a, _a_offset, lda, ipiv, _ipiv_offset, b, _b_offset, ldb, info);
/* 1183:     */   }
/* 1184:     */   
/* 1185:     */   public void dgesvd(String jobu, String jobvt, int m, int n, double[] a, int lda, double[] s, double[] u, int ldu, double[] vt, int ldvt, double[] work, int lwork, intW info)
/* 1186:     */   {
/* 1187: 491 */     Dgesvd.dgesvd(jobu, jobvt, m, n, a, 0, lda, s, 0, u, 0, ldu, vt, 0, ldvt, work, 0, lwork, info);
/* 1188:     */   }
/* 1189:     */   
/* 1190:     */   public void dgesvd(String jobu, String jobvt, int m, int n, double[] a, int _a_offset, int lda, double[] s, int _s_offset, double[] u, int _u_offset, int ldu, double[] vt, int _vt_offset, int ldvt, double[] work, int _work_offset, int lwork, intW info)
/* 1191:     */   {
/* 1192: 496 */     Dgesvd.dgesvd(jobu, jobvt, m, n, a, _a_offset, lda, s, _s_offset, u, _u_offset, ldu, vt, _vt_offset, ldvt, work, _work_offset, lwork, info);
/* 1193:     */   }
/* 1194:     */   
/* 1195:     */   public void dgesvx(String fact, String trans, int n, int nrhs, double[] a, int lda, double[] af, int ldaf, int[] ipiv, StringW equed, double[] r, double[] c, double[] b, int ldb, double[] x, int ldx, doubleW rcond, double[] ferr, double[] berr, double[] work, int[] iwork, intW info)
/* 1196:     */   {
/* 1197: 501 */     Dgesvx.dgesvx(fact, trans, n, nrhs, a, 0, lda, af, 0, ldaf, ipiv, 0, equed, r, 0, c, 0, b, 0, ldb, x, 0, ldx, rcond, ferr, 0, berr, 0, work, 0, iwork, 0, info);
/* 1198:     */   }
/* 1199:     */   
/* 1200:     */   public void dgesvx(String fact, String trans, int n, int nrhs, double[] a, int _a_offset, int lda, double[] af, int _af_offset, int ldaf, int[] ipiv, int _ipiv_offset, StringW equed, double[] r, int _r_offset, double[] c, int _c_offset, double[] b, int _b_offset, int ldb, double[] x, int _x_offset, int ldx, doubleW rcond, double[] ferr, int _ferr_offset, double[] berr, int _berr_offset, double[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 1201:     */   {
/* 1202: 506 */     Dgesvx.dgesvx(fact, trans, n, nrhs, a, _a_offset, lda, af, _af_offset, ldaf, ipiv, _ipiv_offset, equed, r, _r_offset, c, _c_offset, b, _b_offset, ldb, x, _x_offset, ldx, rcond, ferr, _ferr_offset, berr, _berr_offset, work, _work_offset, iwork, _iwork_offset, info);
/* 1203:     */   }
/* 1204:     */   
/* 1205:     */   public void dgetc2(int n, double[] a, int lda, int[] ipiv, int[] jpiv, intW info)
/* 1206:     */   {
/* 1207: 511 */     Dgetc2.dgetc2(n, a, 0, lda, ipiv, 0, jpiv, 0, info);
/* 1208:     */   }
/* 1209:     */   
/* 1210:     */   public void dgetc2(int n, double[] a, int _a_offset, int lda, int[] ipiv, int _ipiv_offset, int[] jpiv, int _jpiv_offset, intW info)
/* 1211:     */   {
/* 1212: 516 */     Dgetc2.dgetc2(n, a, _a_offset, lda, ipiv, _ipiv_offset, jpiv, _jpiv_offset, info);
/* 1213:     */   }
/* 1214:     */   
/* 1215:     */   public void dgetf2(int m, int n, double[] a, int lda, int[] ipiv, intW info)
/* 1216:     */   {
/* 1217: 521 */     Dgetf2.dgetf2(m, n, a, 0, lda, ipiv, 0, info);
/* 1218:     */   }
/* 1219:     */   
/* 1220:     */   public void dgetf2(int m, int n, double[] a, int _a_offset, int lda, int[] ipiv, int _ipiv_offset, intW info)
/* 1221:     */   {
/* 1222: 526 */     Dgetf2.dgetf2(m, n, a, _a_offset, lda, ipiv, _ipiv_offset, info);
/* 1223:     */   }
/* 1224:     */   
/* 1225:     */   public void dgetrf(int m, int n, double[] a, int lda, int[] ipiv, intW info)
/* 1226:     */   {
/* 1227: 531 */     Dgetrf.dgetrf(m, n, a, 0, lda, ipiv, 0, info);
/* 1228:     */   }
/* 1229:     */   
/* 1230:     */   public void dgetrf(int m, int n, double[] a, int _a_offset, int lda, int[] ipiv, int _ipiv_offset, intW info)
/* 1231:     */   {
/* 1232: 536 */     Dgetrf.dgetrf(m, n, a, _a_offset, lda, ipiv, _ipiv_offset, info);
/* 1233:     */   }
/* 1234:     */   
/* 1235:     */   public void dgetri(int n, double[] a, int lda, int[] ipiv, double[] work, int lwork, intW info)
/* 1236:     */   {
/* 1237: 541 */     Dgetri.dgetri(n, a, 0, lda, ipiv, 0, work, 0, lwork, info);
/* 1238:     */   }
/* 1239:     */   
/* 1240:     */   public void dgetri(int n, double[] a, int _a_offset, int lda, int[] ipiv, int _ipiv_offset, double[] work, int _work_offset, int lwork, intW info)
/* 1241:     */   {
/* 1242: 546 */     Dgetri.dgetri(n, a, _a_offset, lda, ipiv, _ipiv_offset, work, _work_offset, lwork, info);
/* 1243:     */   }
/* 1244:     */   
/* 1245:     */   public void dgetrs(String trans, int n, int nrhs, double[] a, int lda, int[] ipiv, double[] b, int ldb, intW info)
/* 1246:     */   {
/* 1247: 551 */     Dgetrs.dgetrs(trans, n, nrhs, a, 0, lda, ipiv, 0, b, 0, ldb, info);
/* 1248:     */   }
/* 1249:     */   
/* 1250:     */   public void dgetrs(String trans, int n, int nrhs, double[] a, int _a_offset, int lda, int[] ipiv, int _ipiv_offset, double[] b, int _b_offset, int ldb, intW info)
/* 1251:     */   {
/* 1252: 556 */     Dgetrs.dgetrs(trans, n, nrhs, a, _a_offset, lda, ipiv, _ipiv_offset, b, _b_offset, ldb, info);
/* 1253:     */   }
/* 1254:     */   
/* 1255:     */   public void dggbak(String job, String side, int n, int ilo, int ihi, double[] lscale, double[] rscale, int m, double[] v, int ldv, intW info)
/* 1256:     */   {
/* 1257: 561 */     Dggbak.dggbak(job, side, n, ilo, ihi, lscale, 0, rscale, 0, m, v, 0, ldv, info);
/* 1258:     */   }
/* 1259:     */   
/* 1260:     */   public void dggbak(String job, String side, int n, int ilo, int ihi, double[] lscale, int _lscale_offset, double[] rscale, int _rscale_offset, int m, double[] v, int _v_offset, int ldv, intW info)
/* 1261:     */   {
/* 1262: 566 */     Dggbak.dggbak(job, side, n, ilo, ihi, lscale, _lscale_offset, rscale, _rscale_offset, m, v, _v_offset, ldv, info);
/* 1263:     */   }
/* 1264:     */   
/* 1265:     */   public void dggbal(String job, int n, double[] a, int lda, double[] b, int ldb, intW ilo, intW ihi, double[] lscale, double[] rscale, double[] work, intW info)
/* 1266:     */   {
/* 1267: 571 */     Dggbal.dggbal(job, n, a, 0, lda, b, 0, ldb, ilo, ihi, lscale, 0, rscale, 0, work, 0, info);
/* 1268:     */   }
/* 1269:     */   
/* 1270:     */   public void dggbal(String job, int n, double[] a, int _a_offset, int lda, double[] b, int _b_offset, int ldb, intW ilo, intW ihi, double[] lscale, int _lscale_offset, double[] rscale, int _rscale_offset, double[] work, int _work_offset, intW info)
/* 1271:     */   {
/* 1272: 576 */     Dggbal.dggbal(job, n, a, _a_offset, lda, b, _b_offset, ldb, ilo, ihi, lscale, _lscale_offset, rscale, _rscale_offset, work, _work_offset, info);
/* 1273:     */   }
/* 1274:     */   
/* 1275:     */   public void dgges(String jobvsl, String jobvsr, String sort, Object selctg, int n, double[] a, int lda, double[] b, int ldb, intW sdim, double[] alphar, double[] alphai, double[] beta, double[] vsl, int ldvsl, double[] vsr, int ldvsr, double[] work, int lwork, boolean[] bwork, intW info)
/* 1276:     */   {
/* 1277: 581 */     Dgges.dgges(jobvsl, jobvsr, sort, selctg, n, a, 0, lda, b, 0, ldb, sdim, alphar, 0, alphai, 0, beta, 0, vsl, 0, ldvsl, vsr, 0, ldvsr, work, 0, lwork, bwork, 0, info);
/* 1278:     */   }
/* 1279:     */   
/* 1280:     */   public void dgges(String jobvsl, String jobvsr, String sort, Object selctg, int n, double[] a, int _a_offset, int lda, double[] b, int _b_offset, int ldb, intW sdim, double[] alphar, int _alphar_offset, double[] alphai, int _alphai_offset, double[] beta, int _beta_offset, double[] vsl, int _vsl_offset, int ldvsl, double[] vsr, int _vsr_offset, int ldvsr, double[] work, int _work_offset, int lwork, boolean[] bwork, int _bwork_offset, intW info)
/* 1281:     */   {
/* 1282: 586 */     Dgges.dgges(jobvsl, jobvsr, sort, selctg, n, a, _a_offset, lda, b, _b_offset, ldb, sdim, alphar, _alphar_offset, alphai, _alphai_offset, beta, _beta_offset, vsl, _vsl_offset, ldvsl, vsr, _vsr_offset, ldvsr, work, _work_offset, lwork, bwork, _bwork_offset, info);
/* 1283:     */   }
/* 1284:     */   
/* 1285:     */   public void dggesx(String jobvsl, String jobvsr, String sort, Object selctg, String sense, int n, double[] a, int lda, double[] b, int ldb, intW sdim, double[] alphar, double[] alphai, double[] beta, double[] vsl, int ldvsl, double[] vsr, int ldvsr, double[] rconde, double[] rcondv, double[] work, int lwork, int[] iwork, int liwork, boolean[] bwork, intW info)
/* 1286:     */   {
/* 1287: 591 */     Dggesx.dggesx(jobvsl, jobvsr, sort, selctg, sense, n, a, 0, lda, b, 0, ldb, sdim, alphar, 0, alphai, 0, beta, 0, vsl, 0, ldvsl, vsr, 0, ldvsr, rconde, 0, rcondv, 0, work, 0, lwork, iwork, 0, liwork, bwork, 0, info);
/* 1288:     */   }
/* 1289:     */   
/* 1290:     */   public void dggesx(String jobvsl, String jobvsr, String sort, Object selctg, String sense, int n, double[] a, int _a_offset, int lda, double[] b, int _b_offset, int ldb, intW sdim, double[] alphar, int _alphar_offset, double[] alphai, int _alphai_offset, double[] beta, int _beta_offset, double[] vsl, int _vsl_offset, int ldvsl, double[] vsr, int _vsr_offset, int ldvsr, double[] rconde, int _rconde_offset, double[] rcondv, int _rcondv_offset, double[] work, int _work_offset, int lwork, int[] iwork, int _iwork_offset, int liwork, boolean[] bwork, int _bwork_offset, intW info)
/* 1291:     */   {
/* 1292: 596 */     Dggesx.dggesx(jobvsl, jobvsr, sort, selctg, sense, n, a, _a_offset, lda, b, _b_offset, ldb, sdim, alphar, _alphar_offset, alphai, _alphai_offset, beta, _beta_offset, vsl, _vsl_offset, ldvsl, vsr, _vsr_offset, ldvsr, rconde, _rconde_offset, rcondv, _rcondv_offset, work, _work_offset, lwork, iwork, _iwork_offset, liwork, bwork, _bwork_offset, info);
/* 1293:     */   }
/* 1294:     */   
/* 1295:     */   public void dggev(String jobvl, String jobvr, int n, double[] a, int lda, double[] b, int ldb, double[] alphar, double[] alphai, double[] beta, double[] vl, int ldvl, double[] vr, int ldvr, double[] work, int lwork, intW info)
/* 1296:     */   {
/* 1297: 601 */     Dggev.dggev(jobvl, jobvr, n, a, 0, lda, b, 0, ldb, alphar, 0, alphai, 0, beta, 0, vl, 0, ldvl, vr, 0, ldvr, work, 0, lwork, info);
/* 1298:     */   }
/* 1299:     */   
/* 1300:     */   public void dggev(String jobvl, String jobvr, int n, double[] a, int _a_offset, int lda, double[] b, int _b_offset, int ldb, double[] alphar, int _alphar_offset, double[] alphai, int _alphai_offset, double[] beta, int _beta_offset, double[] vl, int _vl_offset, int ldvl, double[] vr, int _vr_offset, int ldvr, double[] work, int _work_offset, int lwork, intW info)
/* 1301:     */   {
/* 1302: 606 */     Dggev.dggev(jobvl, jobvr, n, a, _a_offset, lda, b, _b_offset, ldb, alphar, _alphar_offset, alphai, _alphai_offset, beta, _beta_offset, vl, _vl_offset, ldvl, vr, _vr_offset, ldvr, work, _work_offset, lwork, info);
/* 1303:     */   }
/* 1304:     */   
/* 1305:     */   public void dggevx(String balanc, String jobvl, String jobvr, String sense, int n, double[] a, int lda, double[] b, int ldb, double[] alphar, double[] alphai, double[] beta, double[] vl, int ldvl, double[] vr, int ldvr, intW ilo, intW ihi, double[] lscale, double[] rscale, doubleW abnrm, doubleW bbnrm, double[] rconde, double[] rcondv, double[] work, int lwork, int[] iwork, boolean[] bwork, intW info)
/* 1306:     */   {
/* 1307: 611 */     Dggevx.dggevx(balanc, jobvl, jobvr, sense, n, a, 0, lda, b, 0, ldb, alphar, 0, alphai, 0, beta, 0, vl, 0, ldvl, vr, 0, ldvr, ilo, ihi, lscale, 0, rscale, 0, abnrm, bbnrm, rconde, 0, rcondv, 0, work, 0, lwork, iwork, 0, bwork, 0, info);
/* 1308:     */   }
/* 1309:     */   
/* 1310:     */   public void dggevx(String balanc, String jobvl, String jobvr, String sense, int n, double[] a, int _a_offset, int lda, double[] b, int _b_offset, int ldb, double[] alphar, int _alphar_offset, double[] alphai, int _alphai_offset, double[] beta, int _beta_offset, double[] vl, int _vl_offset, int ldvl, double[] vr, int _vr_offset, int ldvr, intW ilo, intW ihi, double[] lscale, int _lscale_offset, double[] rscale, int _rscale_offset, doubleW abnrm, doubleW bbnrm, double[] rconde, int _rconde_offset, double[] rcondv, int _rcondv_offset, double[] work, int _work_offset, int lwork, int[] iwork, int _iwork_offset, boolean[] bwork, int _bwork_offset, intW info)
/* 1311:     */   {
/* 1312: 616 */     Dggevx.dggevx(balanc, jobvl, jobvr, sense, n, a, _a_offset, lda, b, _b_offset, ldb, alphar, _alphar_offset, alphai, _alphai_offset, beta, _beta_offset, vl, _vl_offset, ldvl, vr, _vr_offset, ldvr, ilo, ihi, lscale, _lscale_offset, rscale, _rscale_offset, abnrm, bbnrm, rconde, _rconde_offset, rcondv, _rcondv_offset, work, _work_offset, lwork, iwork, _iwork_offset, bwork, _bwork_offset, info);
/* 1313:     */   }
/* 1314:     */   
/* 1315:     */   public void dggglm(int n, int m, int p, double[] a, int lda, double[] b, int ldb, double[] d, double[] x, double[] y, double[] work, int lwork, intW info)
/* 1316:     */   {
/* 1317: 621 */     Dggglm.dggglm(n, m, p, a, 0, lda, b, 0, ldb, d, 0, x, 0, y, 0, work, 0, lwork, info);
/* 1318:     */   }
/* 1319:     */   
/* 1320:     */   public void dggglm(int n, int m, int p, double[] a, int _a_offset, int lda, double[] b, int _b_offset, int ldb, double[] d, int _d_offset, double[] x, int _x_offset, double[] y, int _y_offset, double[] work, int _work_offset, int lwork, intW info)
/* 1321:     */   {
/* 1322: 626 */     Dggglm.dggglm(n, m, p, a, _a_offset, lda, b, _b_offset, ldb, d, _d_offset, x, _x_offset, y, _y_offset, work, _work_offset, lwork, info);
/* 1323:     */   }
/* 1324:     */   
/* 1325:     */   public void dgghrd(String compq, String compz, int n, int ilo, int ihi, double[] a, int lda, double[] b, int ldb, double[] q, int ldq, double[] z, int ldz, intW info)
/* 1326:     */   {
/* 1327: 631 */     Dgghrd.dgghrd(compq, compz, n, ilo, ihi, a, 0, lda, b, 0, ldb, q, 0, ldq, z, 0, ldz, info);
/* 1328:     */   }
/* 1329:     */   
/* 1330:     */   public void dgghrd(String compq, String compz, int n, int ilo, int ihi, double[] a, int _a_offset, int lda, double[] b, int _b_offset, int ldb, double[] q, int _q_offset, int ldq, double[] z, int _z_offset, int ldz, intW info)
/* 1331:     */   {
/* 1332: 636 */     Dgghrd.dgghrd(compq, compz, n, ilo, ihi, a, _a_offset, lda, b, _b_offset, ldb, q, _q_offset, ldq, z, _z_offset, ldz, info);
/* 1333:     */   }
/* 1334:     */   
/* 1335:     */   public void dgglse(int m, int n, int p, double[] a, int lda, double[] b, int ldb, double[] c, double[] d, double[] x, double[] work, int lwork, intW info)
/* 1336:     */   {
/* 1337: 641 */     Dgglse.dgglse(m, n, p, a, 0, lda, b, 0, ldb, c, 0, d, 0, x, 0, work, 0, lwork, info);
/* 1338:     */   }
/* 1339:     */   
/* 1340:     */   public void dgglse(int m, int n, int p, double[] a, int _a_offset, int lda, double[] b, int _b_offset, int ldb, double[] c, int _c_offset, double[] d, int _d_offset, double[] x, int _x_offset, double[] work, int _work_offset, int lwork, intW info)
/* 1341:     */   {
/* 1342: 646 */     Dgglse.dgglse(m, n, p, a, _a_offset, lda, b, _b_offset, ldb, c, _c_offset, d, _d_offset, x, _x_offset, work, _work_offset, lwork, info);
/* 1343:     */   }
/* 1344:     */   
/* 1345:     */   public void dggqrf(int n, int m, int p, double[] a, int lda, double[] taua, double[] b, int ldb, double[] taub, double[] work, int lwork, intW info)
/* 1346:     */   {
/* 1347: 651 */     Dggqrf.dggqrf(n, m, p, a, 0, lda, taua, 0, b, 0, ldb, taub, 0, work, 0, lwork, info);
/* 1348:     */   }
/* 1349:     */   
/* 1350:     */   public void dggqrf(int n, int m, int p, double[] a, int _a_offset, int lda, double[] taua, int _taua_offset, double[] b, int _b_offset, int ldb, double[] taub, int _taub_offset, double[] work, int _work_offset, int lwork, intW info)
/* 1351:     */   {
/* 1352: 656 */     Dggqrf.dggqrf(n, m, p, a, _a_offset, lda, taua, _taua_offset, b, _b_offset, ldb, taub, _taub_offset, work, _work_offset, lwork, info);
/* 1353:     */   }
/* 1354:     */   
/* 1355:     */   public void dggrqf(int m, int p, int n, double[] a, int lda, double[] taua, double[] b, int ldb, double[] taub, double[] work, int lwork, intW info)
/* 1356:     */   {
/* 1357: 661 */     Dggrqf.dggrqf(m, p, n, a, 0, lda, taua, 0, b, 0, ldb, taub, 0, work, 0, lwork, info);
/* 1358:     */   }
/* 1359:     */   
/* 1360:     */   public void dggrqf(int m, int p, int n, double[] a, int _a_offset, int lda, double[] taua, int _taua_offset, double[] b, int _b_offset, int ldb, double[] taub, int _taub_offset, double[] work, int _work_offset, int lwork, intW info)
/* 1361:     */   {
/* 1362: 666 */     Dggrqf.dggrqf(m, p, n, a, _a_offset, lda, taua, _taua_offset, b, _b_offset, ldb, taub, _taub_offset, work, _work_offset, lwork, info);
/* 1363:     */   }
/* 1364:     */   
/* 1365:     */   public void dggsvd(String jobu, String jobv, String jobq, int m, int n, int p, intW k, intW l, double[] a, int lda, double[] b, int ldb, double[] alpha, double[] beta, double[] u, int ldu, double[] v, int ldv, double[] q, int ldq, double[] work, int[] iwork, intW info)
/* 1366:     */   {
/* 1367: 671 */     Dggsvd.dggsvd(jobu, jobv, jobq, m, n, p, k, l, a, 0, lda, b, 0, ldb, alpha, 0, beta, 0, u, 0, ldu, v, 0, ldv, q, 0, ldq, work, 0, iwork, 0, info);
/* 1368:     */   }
/* 1369:     */   
/* 1370:     */   public void dggsvd(String jobu, String jobv, String jobq, int m, int n, int p, intW k, intW l, double[] a, int _a_offset, int lda, double[] b, int _b_offset, int ldb, double[] alpha, int _alpha_offset, double[] beta, int _beta_offset, double[] u, int _u_offset, int ldu, double[] v, int _v_offset, int ldv, double[] q, int _q_offset, int ldq, double[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 1371:     */   {
/* 1372: 676 */     Dggsvd.dggsvd(jobu, jobv, jobq, m, n, p, k, l, a, _a_offset, lda, b, _b_offset, ldb, alpha, _alpha_offset, beta, _beta_offset, u, _u_offset, ldu, v, _v_offset, ldv, q, _q_offset, ldq, work, _work_offset, iwork, _iwork_offset, info);
/* 1373:     */   }
/* 1374:     */   
/* 1375:     */   public void dggsvp(String jobu, String jobv, String jobq, int m, int p, int n, double[] a, int lda, double[] b, int ldb, double tola, double tolb, intW k, intW l, double[] u, int ldu, double[] v, int ldv, double[] q, int ldq, int[] iwork, double[] tau, double[] work, intW info)
/* 1376:     */   {
/* 1377: 681 */     Dggsvp.dggsvp(jobu, jobv, jobq, m, p, n, a, 0, lda, b, 0, ldb, tola, tolb, k, l, u, 0, ldu, v, 0, ldv, q, 0, ldq, iwork, 0, tau, 0, work, 0, info);
/* 1378:     */   }
/* 1379:     */   
/* 1380:     */   public void dggsvp(String jobu, String jobv, String jobq, int m, int p, int n, double[] a, int _a_offset, int lda, double[] b, int _b_offset, int ldb, double tola, double tolb, intW k, intW l, double[] u, int _u_offset, int ldu, double[] v, int _v_offset, int ldv, double[] q, int _q_offset, int ldq, int[] iwork, int _iwork_offset, double[] tau, int _tau_offset, double[] work, int _work_offset, intW info)
/* 1381:     */   {
/* 1382: 686 */     Dggsvp.dggsvp(jobu, jobv, jobq, m, p, n, a, _a_offset, lda, b, _b_offset, ldb, tola, tolb, k, l, u, _u_offset, ldu, v, _v_offset, ldv, q, _q_offset, ldq, iwork, _iwork_offset, tau, _tau_offset, work, _work_offset, info);
/* 1383:     */   }
/* 1384:     */   
/* 1385:     */   public void dgtcon(String norm, int n, double[] dl, double[] d, double[] du, double[] du2, int[] ipiv, double anorm, doubleW rcond, double[] work, int[] iwork, intW info)
/* 1386:     */   {
/* 1387: 691 */     Dgtcon.dgtcon(norm, n, dl, 0, d, 0, du, 0, du2, 0, ipiv, 0, anorm, rcond, work, 0, iwork, 0, info);
/* 1388:     */   }
/* 1389:     */   
/* 1390:     */   public void dgtcon(String norm, int n, double[] dl, int _dl_offset, double[] d, int _d_offset, double[] du, int _du_offset, double[] du2, int _du2_offset, int[] ipiv, int _ipiv_offset, double anorm, doubleW rcond, double[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 1391:     */   {
/* 1392: 696 */     Dgtcon.dgtcon(norm, n, dl, _dl_offset, d, _d_offset, du, _du_offset, du2, _du2_offset, ipiv, _ipiv_offset, anorm, rcond, work, _work_offset, iwork, _iwork_offset, info);
/* 1393:     */   }
/* 1394:     */   
/* 1395:     */   public void dgtrfs(String trans, int n, int nrhs, double[] dl, double[] d, double[] du, double[] dlf, double[] df, double[] duf, double[] du2, int[] ipiv, double[] b, int ldb, double[] x, int ldx, double[] ferr, double[] berr, double[] work, int[] iwork, intW info)
/* 1396:     */   {
/* 1397: 701 */     Dgtrfs.dgtrfs(trans, n, nrhs, dl, 0, d, 0, du, 0, dlf, 0, df, 0, duf, 0, du2, 0, ipiv, 0, b, 0, ldb, x, 0, ldx, ferr, 0, berr, 0, work, 0, iwork, 0, info);
/* 1398:     */   }
/* 1399:     */   
/* 1400:     */   public void dgtrfs(String trans, int n, int nrhs, double[] dl, int _dl_offset, double[] d, int _d_offset, double[] du, int _du_offset, double[] dlf, int _dlf_offset, double[] df, int _df_offset, double[] duf, int _duf_offset, double[] du2, int _du2_offset, int[] ipiv, int _ipiv_offset, double[] b, int _b_offset, int ldb, double[] x, int _x_offset, int ldx, double[] ferr, int _ferr_offset, double[] berr, int _berr_offset, double[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 1401:     */   {
/* 1402: 706 */     Dgtrfs.dgtrfs(trans, n, nrhs, dl, _dl_offset, d, _d_offset, du, _du_offset, dlf, _dlf_offset, df, _df_offset, duf, _duf_offset, du2, _du2_offset, ipiv, _ipiv_offset, b, _b_offset, ldb, x, _x_offset, ldx, ferr, _ferr_offset, berr, _berr_offset, work, _work_offset, iwork, _iwork_offset, info);
/* 1403:     */   }
/* 1404:     */   
/* 1405:     */   public void dgtsv(int n, int nrhs, double[] dl, double[] d, double[] du, double[] b, int ldb, intW info)
/* 1406:     */   {
/* 1407: 711 */     Dgtsv.dgtsv(n, nrhs, dl, 0, d, 0, du, 0, b, 0, ldb, info);
/* 1408:     */   }
/* 1409:     */   
/* 1410:     */   public void dgtsv(int n, int nrhs, double[] dl, int _dl_offset, double[] d, int _d_offset, double[] du, int _du_offset, double[] b, int _b_offset, int ldb, intW info)
/* 1411:     */   {
/* 1412: 716 */     Dgtsv.dgtsv(n, nrhs, dl, _dl_offset, d, _d_offset, du, _du_offset, b, _b_offset, ldb, info);
/* 1413:     */   }
/* 1414:     */   
/* 1415:     */   public void dgtsvx(String fact, String trans, int n, int nrhs, double[] dl, double[] d, double[] du, double[] dlf, double[] df, double[] duf, double[] du2, int[] ipiv, double[] b, int ldb, double[] x, int ldx, doubleW rcond, double[] ferr, double[] berr, double[] work, int[] iwork, intW info)
/* 1416:     */   {
/* 1417: 721 */     Dgtsvx.dgtsvx(fact, trans, n, nrhs, dl, 0, d, 0, du, 0, dlf, 0, df, 0, duf, 0, du2, 0, ipiv, 0, b, 0, ldb, x, 0, ldx, rcond, ferr, 0, berr, 0, work, 0, iwork, 0, info);
/* 1418:     */   }
/* 1419:     */   
/* 1420:     */   public void dgtsvx(String fact, String trans, int n, int nrhs, double[] dl, int _dl_offset, double[] d, int _d_offset, double[] du, int _du_offset, double[] dlf, int _dlf_offset, double[] df, int _df_offset, double[] duf, int _duf_offset, double[] du2, int _du2_offset, int[] ipiv, int _ipiv_offset, double[] b, int _b_offset, int ldb, double[] x, int _x_offset, int ldx, doubleW rcond, double[] ferr, int _ferr_offset, double[] berr, int _berr_offset, double[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 1421:     */   {
/* 1422: 726 */     Dgtsvx.dgtsvx(fact, trans, n, nrhs, dl, _dl_offset, d, _d_offset, du, _du_offset, dlf, _dlf_offset, df, _df_offset, duf, _duf_offset, du2, _du2_offset, ipiv, _ipiv_offset, b, _b_offset, ldb, x, _x_offset, ldx, rcond, ferr, _ferr_offset, berr, _berr_offset, work, _work_offset, iwork, _iwork_offset, info);
/* 1423:     */   }
/* 1424:     */   
/* 1425:     */   public void dgttrf(int n, double[] dl, double[] d, double[] du, double[] du2, int[] ipiv, intW info)
/* 1426:     */   {
/* 1427: 731 */     Dgttrf.dgttrf(n, dl, 0, d, 0, du, 0, du2, 0, ipiv, 0, info);
/* 1428:     */   }
/* 1429:     */   
/* 1430:     */   public void dgttrf(int n, double[] dl, int _dl_offset, double[] d, int _d_offset, double[] du, int _du_offset, double[] du2, int _du2_offset, int[] ipiv, int _ipiv_offset, intW info)
/* 1431:     */   {
/* 1432: 736 */     Dgttrf.dgttrf(n, dl, _dl_offset, d, _d_offset, du, _du_offset, du2, _du2_offset, ipiv, _ipiv_offset, info);
/* 1433:     */   }
/* 1434:     */   
/* 1435:     */   public void dgttrs(String trans, int n, int nrhs, double[] dl, double[] d, double[] du, double[] du2, int[] ipiv, double[] b, int ldb, intW info)
/* 1436:     */   {
/* 1437: 741 */     Dgttrs.dgttrs(trans, n, nrhs, dl, 0, d, 0, du, 0, du2, 0, ipiv, 0, b, 0, ldb, info);
/* 1438:     */   }
/* 1439:     */   
/* 1440:     */   public void dgttrs(String trans, int n, int nrhs, double[] dl, int _dl_offset, double[] d, int _d_offset, double[] du, int _du_offset, double[] du2, int _du2_offset, int[] ipiv, int _ipiv_offset, double[] b, int _b_offset, int ldb, intW info)
/* 1441:     */   {
/* 1442: 746 */     Dgttrs.dgttrs(trans, n, nrhs, dl, _dl_offset, d, _d_offset, du, _du_offset, du2, _du2_offset, ipiv, _ipiv_offset, b, _b_offset, ldb, info);
/* 1443:     */   }
/* 1444:     */   
/* 1445:     */   public void dgtts2(int itrans, int n, int nrhs, double[] dl, double[] d, double[] du, double[] du2, int[] ipiv, double[] b, int ldb)
/* 1446:     */   {
/* 1447: 751 */     Dgtts2.dgtts2(itrans, n, nrhs, dl, 0, d, 0, du, 0, du2, 0, ipiv, 0, b, 0, ldb);
/* 1448:     */   }
/* 1449:     */   
/* 1450:     */   public void dgtts2(int itrans, int n, int nrhs, double[] dl, int _dl_offset, double[] d, int _d_offset, double[] du, int _du_offset, double[] du2, int _du2_offset, int[] ipiv, int _ipiv_offset, double[] b, int _b_offset, int ldb)
/* 1451:     */   {
/* 1452: 756 */     Dgtts2.dgtts2(itrans, n, nrhs, dl, _dl_offset, d, _d_offset, du, _du_offset, du2, _du2_offset, ipiv, _ipiv_offset, b, _b_offset, ldb);
/* 1453:     */   }
/* 1454:     */   
/* 1455:     */   public void dhgeqz(String job, String compq, String compz, int n, int ilo, int ihi, double[] h, int ldh, double[] t, int ldt, double[] alphar, double[] alphai, double[] beta, double[] q, int ldq, double[] z, int ldz, double[] work, int lwork, intW info)
/* 1456:     */   {
/* 1457: 761 */     Dhgeqz.dhgeqz(job, compq, compz, n, ilo, ihi, h, 0, ldh, t, 0, ldt, alphar, 0, alphai, 0, beta, 0, q, 0, ldq, z, 0, ldz, work, 0, lwork, info);
/* 1458:     */   }
/* 1459:     */   
/* 1460:     */   public void dhgeqz(String job, String compq, String compz, int n, int ilo, int ihi, double[] h, int _h_offset, int ldh, double[] t, int _t_offset, int ldt, double[] alphar, int _alphar_offset, double[] alphai, int _alphai_offset, double[] beta, int _beta_offset, double[] q, int _q_offset, int ldq, double[] z, int _z_offset, int ldz, double[] work, int _work_offset, int lwork, intW info)
/* 1461:     */   {
/* 1462: 766 */     Dhgeqz.dhgeqz(job, compq, compz, n, ilo, ihi, h, _h_offset, ldh, t, _t_offset, ldt, alphar, _alphar_offset, alphai, _alphai_offset, beta, _beta_offset, q, _q_offset, ldq, z, _z_offset, ldz, work, _work_offset, lwork, info);
/* 1463:     */   }
/* 1464:     */   
/* 1465:     */   public void dhsein(String side, String eigsrc, String initv, boolean[] select, int n, double[] h, int ldh, double[] wr, double[] wi, double[] vl, int ldvl, double[] vr, int ldvr, int mm, intW m, double[] work, int[] ifaill, int[] ifailr, intW info)
/* 1466:     */   {
/* 1467: 771 */     Dhsein.dhsein(side, eigsrc, initv, select, 0, n, h, 0, ldh, wr, 0, wi, 0, vl, 0, ldvl, vr, 0, ldvr, mm, m, work, 0, ifaill, 0, ifailr, 0, info);
/* 1468:     */   }
/* 1469:     */   
/* 1470:     */   public void dhsein(String side, String eigsrc, String initv, boolean[] select, int _select_offset, int n, double[] h, int _h_offset, int ldh, double[] wr, int _wr_offset, double[] wi, int _wi_offset, double[] vl, int _vl_offset, int ldvl, double[] vr, int _vr_offset, int ldvr, int mm, intW m, double[] work, int _work_offset, int[] ifaill, int _ifaill_offset, int[] ifailr, int _ifailr_offset, intW info)
/* 1471:     */   {
/* 1472: 776 */     Dhsein.dhsein(side, eigsrc, initv, select, _select_offset, n, h, _h_offset, ldh, wr, _wr_offset, wi, _wi_offset, vl, _vl_offset, ldvl, vr, _vr_offset, ldvr, mm, m, work, _work_offset, ifaill, _ifaill_offset, ifailr, _ifailr_offset, info);
/* 1473:     */   }
/* 1474:     */   
/* 1475:     */   public void dhseqr(String job, String compz, int n, int ilo, int ihi, double[] h, int ldh, double[] wr, double[] wi, double[] z, int ldz, double[] work, int lwork, intW info)
/* 1476:     */   {
/* 1477: 781 */     Dhseqr.dhseqr(job, compz, n, ilo, ihi, h, 0, ldh, wr, 0, wi, 0, z, 0, ldz, work, 0, lwork, info);
/* 1478:     */   }
/* 1479:     */   
/* 1480:     */   public void dhseqr(String job, String compz, int n, int ilo, int ihi, double[] h, int _h_offset, int ldh, double[] wr, int _wr_offset, double[] wi, int _wi_offset, double[] z, int _z_offset, int ldz, double[] work, int _work_offset, int lwork, intW info)
/* 1481:     */   {
/* 1482: 786 */     Dhseqr.dhseqr(job, compz, n, ilo, ihi, h, _h_offset, ldh, wr, _wr_offset, wi, _wi_offset, z, _z_offset, ldz, work, _work_offset, lwork, info);
/* 1483:     */   }
/* 1484:     */   
/* 1485:     */   public boolean disnan(double din)
/* 1486:     */   {
/* 1487: 791 */     return Disnan.disnan(din);
/* 1488:     */   }
/* 1489:     */   
/* 1490:     */   public void dlabad(doubleW small, doubleW large)
/* 1491:     */   {
/* 1492: 796 */     Dlabad.dlabad(small, large);
/* 1493:     */   }
/* 1494:     */   
/* 1495:     */   public void dlabrd(int m, int n, int nb, double[] a, int lda, double[] d, double[] e, double[] tauq, double[] taup, double[] x, int ldx, double[] y, int ldy)
/* 1496:     */   {
/* 1497: 801 */     Dlabrd.dlabrd(m, n, nb, a, 0, lda, d, 0, e, 0, tauq, 0, taup, 0, x, 0, ldx, y, 0, ldy);
/* 1498:     */   }
/* 1499:     */   
/* 1500:     */   public void dlabrd(int m, int n, int nb, double[] a, int _a_offset, int lda, double[] d, int _d_offset, double[] e, int _e_offset, double[] tauq, int _tauq_offset, double[] taup, int _taup_offset, double[] x, int _x_offset, int ldx, double[] y, int _y_offset, int ldy)
/* 1501:     */   {
/* 1502: 806 */     Dlabrd.dlabrd(m, n, nb, a, _a_offset, lda, d, _d_offset, e, _e_offset, tauq, _tauq_offset, taup, _taup_offset, x, _x_offset, ldx, y, _y_offset, ldy);
/* 1503:     */   }
/* 1504:     */   
/* 1505:     */   public void dlacn2(int n, double[] v, double[] x, int[] isgn, doubleW est, intW kase, int[] isave)
/* 1506:     */   {
/* 1507: 811 */     Dlacn2.dlacn2(n, v, 0, x, 0, isgn, 0, est, kase, isave, 0);
/* 1508:     */   }
/* 1509:     */   
/* 1510:     */   public void dlacn2(int n, double[] v, int _v_offset, double[] x, int _x_offset, int[] isgn, int _isgn_offset, doubleW est, intW kase, int[] isave, int _isave_offset)
/* 1511:     */   {
/* 1512: 816 */     Dlacn2.dlacn2(n, v, _v_offset, x, _x_offset, isgn, _isgn_offset, est, kase, isave, _isave_offset);
/* 1513:     */   }
/* 1514:     */   
/* 1515:     */   public void dlacon(int n, double[] v, double[] x, int[] isgn, doubleW est, intW kase)
/* 1516:     */   {
/* 1517: 821 */     Dlacon.dlacon(n, v, 0, x, 0, isgn, 0, est, kase);
/* 1518:     */   }
/* 1519:     */   
/* 1520:     */   public void dlacon(int n, double[] v, int _v_offset, double[] x, int _x_offset, int[] isgn, int _isgn_offset, doubleW est, intW kase)
/* 1521:     */   {
/* 1522: 826 */     Dlacon.dlacon(n, v, _v_offset, x, _x_offset, isgn, _isgn_offset, est, kase);
/* 1523:     */   }
/* 1524:     */   
/* 1525:     */   public void dlacpy(String uplo, int m, int n, double[] a, int lda, double[] b, int ldb)
/* 1526:     */   {
/* 1527: 831 */     Dlacpy.dlacpy(uplo, m, n, a, 0, lda, b, 0, ldb);
/* 1528:     */   }
/* 1529:     */   
/* 1530:     */   public void dlacpy(String uplo, int m, int n, double[] a, int _a_offset, int lda, double[] b, int _b_offset, int ldb)
/* 1531:     */   {
/* 1532: 836 */     Dlacpy.dlacpy(uplo, m, n, a, _a_offset, lda, b, _b_offset, ldb);
/* 1533:     */   }
/* 1534:     */   
/* 1535:     */   public void dladiv(double a, double b, double c, double d, doubleW p, doubleW q)
/* 1536:     */   {
/* 1537: 841 */     Dladiv.dladiv(a, b, c, d, p, q);
/* 1538:     */   }
/* 1539:     */   
/* 1540:     */   public void dlae2(double a, double b, double c, doubleW rt1, doubleW rt2)
/* 1541:     */   {
/* 1542: 846 */     Dlae2.dlae2(a, b, c, rt1, rt2);
/* 1543:     */   }
/* 1544:     */   
/* 1545:     */   public void dlaebz(int ijob, int nitmax, int n, int mmax, int minp, int nbmin, double abstol, double reltol, double pivmin, double[] d, double[] e, double[] e2, int[] nval, double[] ab, double[] c, intW mout, int[] nab, double[] work, int[] iwork, intW info)
/* 1546:     */   {
/* 1547: 851 */     Dlaebz.dlaebz(ijob, nitmax, n, mmax, minp, nbmin, abstol, reltol, pivmin, d, 0, e, 0, e2, 0, nval, 0, ab, 0, c, 0, mout, nab, 0, work, 0, iwork, 0, info);
/* 1548:     */   }
/* 1549:     */   
/* 1550:     */   public void dlaebz(int ijob, int nitmax, int n, int mmax, int minp, int nbmin, double abstol, double reltol, double pivmin, double[] d, int _d_offset, double[] e, int _e_offset, double[] e2, int _e2_offset, int[] nval, int _nval_offset, double[] ab, int _ab_offset, double[] c, int _c_offset, intW mout, int[] nab, int _nab_offset, double[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 1551:     */   {
/* 1552: 856 */     Dlaebz.dlaebz(ijob, nitmax, n, mmax, minp, nbmin, abstol, reltol, pivmin, d, _d_offset, e, _e_offset, e2, _e2_offset, nval, _nval_offset, ab, _ab_offset, c, _c_offset, mout, nab, _nab_offset, work, _work_offset, iwork, _iwork_offset, info);
/* 1553:     */   }
/* 1554:     */   
/* 1555:     */   public void dlaed0(int icompq, int qsiz, int n, double[] d, double[] e, double[] q, int ldq, double[] qstore, int ldqs, double[] work, int[] iwork, intW info)
/* 1556:     */   {
/* 1557: 861 */     Dlaed0.dlaed0(icompq, qsiz, n, d, 0, e, 0, q, 0, ldq, qstore, 0, ldqs, work, 0, iwork, 0, info);
/* 1558:     */   }
/* 1559:     */   
/* 1560:     */   public void dlaed0(int icompq, int qsiz, int n, double[] d, int _d_offset, double[] e, int _e_offset, double[] q, int _q_offset, int ldq, double[] qstore, int _qstore_offset, int ldqs, double[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 1561:     */   {
/* 1562: 866 */     Dlaed0.dlaed0(icompq, qsiz, n, d, _d_offset, e, _e_offset, q, _q_offset, ldq, qstore, _qstore_offset, ldqs, work, _work_offset, iwork, _iwork_offset, info);
/* 1563:     */   }
/* 1564:     */   
/* 1565:     */   public void dlaed1(int n, double[] d, double[] q, int ldq, int[] indxq, doubleW rho, int cutpnt, double[] work, int[] iwork, intW info)
/* 1566:     */   {
/* 1567: 871 */     Dlaed1.dlaed1(n, d, 0, q, 0, ldq, indxq, 0, rho, cutpnt, work, 0, iwork, 0, info);
/* 1568:     */   }
/* 1569:     */   
/* 1570:     */   public void dlaed1(int n, double[] d, int _d_offset, double[] q, int _q_offset, int ldq, int[] indxq, int _indxq_offset, doubleW rho, int cutpnt, double[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 1571:     */   {
/* 1572: 876 */     Dlaed1.dlaed1(n, d, _d_offset, q, _q_offset, ldq, indxq, _indxq_offset, rho, cutpnt, work, _work_offset, iwork, _iwork_offset, info);
/* 1573:     */   }
/* 1574:     */   
/* 1575:     */   public void dlaed2(intW k, int n, int n1, double[] d, double[] q, int ldq, int[] indxq, doubleW rho, double[] z, double[] dlamda, double[] w, double[] q2, int[] indx, int[] indxc, int[] indxp, int[] coltyp, intW info)
/* 1576:     */   {
/* 1577: 881 */     Dlaed2.dlaed2(k, n, n1, d, 0, q, 0, ldq, indxq, 0, rho, z, 0, dlamda, 0, w, 0, q2, 0, indx, 0, indxc, 0, indxp, 0, coltyp, 0, info);
/* 1578:     */   }
/* 1579:     */   
/* 1580:     */   public void dlaed2(intW k, int n, int n1, double[] d, int _d_offset, double[] q, int _q_offset, int ldq, int[] indxq, int _indxq_offset, doubleW rho, double[] z, int _z_offset, double[] dlamda, int _dlamda_offset, double[] w, int _w_offset, double[] q2, int _q2_offset, int[] indx, int _indx_offset, int[] indxc, int _indxc_offset, int[] indxp, int _indxp_offset, int[] coltyp, int _coltyp_offset, intW info)
/* 1581:     */   {
/* 1582: 886 */     Dlaed2.dlaed2(k, n, n1, d, _d_offset, q, _q_offset, ldq, indxq, _indxq_offset, rho, z, _z_offset, dlamda, _dlamda_offset, w, _w_offset, q2, _q2_offset, indx, _indx_offset, indxc, _indxc_offset, indxp, _indxp_offset, coltyp, _coltyp_offset, info);
/* 1583:     */   }
/* 1584:     */   
/* 1585:     */   public void dlaed3(int k, int n, int n1, double[] d, double[] q, int ldq, double rho, double[] dlamda, double[] q2, int[] indx, int[] ctot, double[] w, double[] s, intW info)
/* 1586:     */   {
/* 1587: 891 */     Dlaed3.dlaed3(k, n, n1, d, 0, q, 0, ldq, rho, dlamda, 0, q2, 0, indx, 0, ctot, 0, w, 0, s, 0, info);
/* 1588:     */   }
/* 1589:     */   
/* 1590:     */   public void dlaed3(int k, int n, int n1, double[] d, int _d_offset, double[] q, int _q_offset, int ldq, double rho, double[] dlamda, int _dlamda_offset, double[] q2, int _q2_offset, int[] indx, int _indx_offset, int[] ctot, int _ctot_offset, double[] w, int _w_offset, double[] s, int _s_offset, intW info)
/* 1591:     */   {
/* 1592: 896 */     Dlaed3.dlaed3(k, n, n1, d, _d_offset, q, _q_offset, ldq, rho, dlamda, _dlamda_offset, q2, _q2_offset, indx, _indx_offset, ctot, _ctot_offset, w, _w_offset, s, _s_offset, info);
/* 1593:     */   }
/* 1594:     */   
/* 1595:     */   public void dlaed4(int n, int i, double[] d, double[] z, double[] delta, double rho, doubleW dlam, intW info)
/* 1596:     */   {
/* 1597: 901 */     Dlaed4.dlaed4(n, i, d, 0, z, 0, delta, 0, rho, dlam, info);
/* 1598:     */   }
/* 1599:     */   
/* 1600:     */   public void dlaed4(int n, int i, double[] d, int _d_offset, double[] z, int _z_offset, double[] delta, int _delta_offset, double rho, doubleW dlam, intW info)
/* 1601:     */   {
/* 1602: 906 */     Dlaed4.dlaed4(n, i, d, _d_offset, z, _z_offset, delta, _delta_offset, rho, dlam, info);
/* 1603:     */   }
/* 1604:     */   
/* 1605:     */   public void dlaed5(int i, double[] d, double[] z, double[] delta, double rho, doubleW dlam)
/* 1606:     */   {
/* 1607: 911 */     Dlaed5.dlaed5(i, d, 0, z, 0, delta, 0, rho, dlam);
/* 1608:     */   }
/* 1609:     */   
/* 1610:     */   public void dlaed5(int i, double[] d, int _d_offset, double[] z, int _z_offset, double[] delta, int _delta_offset, double rho, doubleW dlam)
/* 1611:     */   {
/* 1612: 916 */     Dlaed5.dlaed5(i, d, _d_offset, z, _z_offset, delta, _delta_offset, rho, dlam);
/* 1613:     */   }
/* 1614:     */   
/* 1615:     */   public void dlaed6(int kniter, boolean orgati, double rho, double[] d, double[] z, double finit, doubleW tau, intW info)
/* 1616:     */   {
/* 1617: 921 */     Dlaed6.dlaed6(kniter, orgati, rho, d, 0, z, 0, finit, tau, info);
/* 1618:     */   }
/* 1619:     */   
/* 1620:     */   public void dlaed6(int kniter, boolean orgati, double rho, double[] d, int _d_offset, double[] z, int _z_offset, double finit, doubleW tau, intW info)
/* 1621:     */   {
/* 1622: 926 */     Dlaed6.dlaed6(kniter, orgati, rho, d, _d_offset, z, _z_offset, finit, tau, info);
/* 1623:     */   }
/* 1624:     */   
/* 1625:     */   public void dlaed7(int icompq, int n, int qsiz, int tlvls, int curlvl, int curpbm, double[] d, double[] q, int ldq, int[] indxq, doubleW rho, int cutpnt, double[] qstore, int[] qptr, int[] prmptr, int[] perm, int[] givptr, int[] givcol, double[] givnum, double[] work, int[] iwork, intW info)
/* 1626:     */   {
/* 1627: 931 */     Dlaed7.dlaed7(icompq, n, qsiz, tlvls, curlvl, curpbm, d, 0, q, 0, ldq, indxq, 0, rho, cutpnt, qstore, 0, qptr, 0, prmptr, 0, perm, 0, givptr, 0, givcol, 0, givnum, 0, work, 0, iwork, 0, info);
/* 1628:     */   }
/* 1629:     */   
/* 1630:     */   public void dlaed7(int icompq, int n, int qsiz, int tlvls, int curlvl, int curpbm, double[] d, int _d_offset, double[] q, int _q_offset, int ldq, int[] indxq, int _indxq_offset, doubleW rho, int cutpnt, double[] qstore, int _qstore_offset, int[] qptr, int _qptr_offset, int[] prmptr, int _prmptr_offset, int[] perm, int _perm_offset, int[] givptr, int _givptr_offset, int[] givcol, int _givcol_offset, double[] givnum, int _givnum_offset, double[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 1631:     */   {
/* 1632: 936 */     Dlaed7.dlaed7(icompq, n, qsiz, tlvls, curlvl, curpbm, d, _d_offset, q, _q_offset, ldq, indxq, _indxq_offset, rho, cutpnt, qstore, _qstore_offset, qptr, _qptr_offset, prmptr, _prmptr_offset, perm, _perm_offset, givptr, _givptr_offset, givcol, _givcol_offset, givnum, _givnum_offset, work, _work_offset, iwork, _iwork_offset, info);
/* 1633:     */   }
/* 1634:     */   
/* 1635:     */   public void dlaed8(int icompq, intW k, int n, int qsiz, double[] d, double[] q, int ldq, int[] indxq, doubleW rho, int cutpnt, double[] z, double[] dlamda, double[] q2, int ldq2, double[] w, int[] perm, intW givptr, int[] givcol, double[] givnum, int[] indxp, int[] indx, intW info)
/* 1636:     */   {
/* 1637: 941 */     Dlaed8.dlaed8(icompq, k, n, qsiz, d, 0, q, 0, ldq, indxq, 0, rho, cutpnt, z, 0, dlamda, 0, q2, 0, ldq2, w, 0, perm, 0, givptr, givcol, 0, givnum, 0, indxp, 0, indx, 0, info);
/* 1638:     */   }
/* 1639:     */   
/* 1640:     */   public void dlaed8(int icompq, intW k, int n, int qsiz, double[] d, int _d_offset, double[] q, int _q_offset, int ldq, int[] indxq, int _indxq_offset, doubleW rho, int cutpnt, double[] z, int _z_offset, double[] dlamda, int _dlamda_offset, double[] q2, int _q2_offset, int ldq2, double[] w, int _w_offset, int[] perm, int _perm_offset, intW givptr, int[] givcol, int _givcol_offset, double[] givnum, int _givnum_offset, int[] indxp, int _indxp_offset, int[] indx, int _indx_offset, intW info)
/* 1641:     */   {
/* 1642: 946 */     Dlaed8.dlaed8(icompq, k, n, qsiz, d, _d_offset, q, _q_offset, ldq, indxq, _indxq_offset, rho, cutpnt, z, _z_offset, dlamda, _dlamda_offset, q2, _q2_offset, ldq2, w, _w_offset, perm, _perm_offset, givptr, givcol, _givcol_offset, givnum, _givnum_offset, indxp, _indxp_offset, indx, _indx_offset, info);
/* 1643:     */   }
/* 1644:     */   
/* 1645:     */   public void dlaed9(int k, int kstart, int kstop, int n, double[] d, double[] q, int ldq, double rho, double[] dlamda, double[] w, double[] s, int lds, intW info)
/* 1646:     */   {
/* 1647: 951 */     Dlaed9.dlaed9(k, kstart, kstop, n, d, 0, q, 0, ldq, rho, dlamda, 0, w, 0, s, 0, lds, info);
/* 1648:     */   }
/* 1649:     */   
/* 1650:     */   public void dlaed9(int k, int kstart, int kstop, int n, double[] d, int _d_offset, double[] q, int _q_offset, int ldq, double rho, double[] dlamda, int _dlamda_offset, double[] w, int _w_offset, double[] s, int _s_offset, int lds, intW info)
/* 1651:     */   {
/* 1652: 956 */     Dlaed9.dlaed9(k, kstart, kstop, n, d, _d_offset, q, _q_offset, ldq, rho, dlamda, _dlamda_offset, w, _w_offset, s, _s_offset, lds, info);
/* 1653:     */   }
/* 1654:     */   
/* 1655:     */   public void dlaeda(int n, int tlvls, int curlvl, int curpbm, int[] prmptr, int[] perm, int[] givptr, int[] givcol, double[] givnum, double[] q, int[] qptr, double[] z, double[] ztemp, intW info)
/* 1656:     */   {
/* 1657: 961 */     Dlaeda.dlaeda(n, tlvls, curlvl, curpbm, prmptr, 0, perm, 0, givptr, 0, givcol, 0, givnum, 0, q, 0, qptr, 0, z, 0, ztemp, 0, info);
/* 1658:     */   }
/* 1659:     */   
/* 1660:     */   public void dlaeda(int n, int tlvls, int curlvl, int curpbm, int[] prmptr, int _prmptr_offset, int[] perm, int _perm_offset, int[] givptr, int _givptr_offset, int[] givcol, int _givcol_offset, double[] givnum, int _givnum_offset, double[] q, int _q_offset, int[] qptr, int _qptr_offset, double[] z, int _z_offset, double[] ztemp, int _ztemp_offset, intW info)
/* 1661:     */   {
/* 1662: 966 */     Dlaeda.dlaeda(n, tlvls, curlvl, curpbm, prmptr, _prmptr_offset, perm, _perm_offset, givptr, _givptr_offset, givcol, _givcol_offset, givnum, _givnum_offset, q, _q_offset, qptr, _qptr_offset, z, _z_offset, ztemp, _ztemp_offset, info);
/* 1663:     */   }
/* 1664:     */   
/* 1665:     */   public void dlaein(boolean rightv, boolean noinit, int n, double[] h, int ldh, double wr, double wi, double[] vr, double[] vi, double[] b, int ldb, double[] work, double eps3, double smlnum, double bignum, intW info)
/* 1666:     */   {
/* 1667: 971 */     Dlaein.dlaein(rightv, noinit, n, h, 0, ldh, wr, wi, vr, 0, vi, 0, b, 0, ldb, work, 0, eps3, smlnum, bignum, info);
/* 1668:     */   }
/* 1669:     */   
/* 1670:     */   public void dlaein(boolean rightv, boolean noinit, int n, double[] h, int _h_offset, int ldh, double wr, double wi, double[] vr, int _vr_offset, double[] vi, int _vi_offset, double[] b, int _b_offset, int ldb, double[] work, int _work_offset, double eps3, double smlnum, double bignum, intW info)
/* 1671:     */   {
/* 1672: 976 */     Dlaein.dlaein(rightv, noinit, n, h, _h_offset, ldh, wr, wi, vr, _vr_offset, vi, _vi_offset, b, _b_offset, ldb, work, _work_offset, eps3, smlnum, bignum, info);
/* 1673:     */   }
/* 1674:     */   
/* 1675:     */   public void dlaev2(double a, double b, double c, doubleW rt1, doubleW rt2, doubleW cs1, doubleW sn1)
/* 1676:     */   {
/* 1677: 981 */     Dlaev2.dlaev2(a, b, c, rt1, rt2, cs1, sn1);
/* 1678:     */   }
/* 1679:     */   
/* 1680:     */   public void dlaexc(boolean wantq, int n, double[] t, int ldt, double[] q, int ldq, int j1, int n1, int n2, double[] work, intW info)
/* 1681:     */   {
/* 1682: 986 */     Dlaexc.dlaexc(wantq, n, t, 0, ldt, q, 0, ldq, j1, n1, n2, work, 0, info);
/* 1683:     */   }
/* 1684:     */   
/* 1685:     */   public void dlaexc(boolean wantq, int n, double[] t, int _t_offset, int ldt, double[] q, int _q_offset, int ldq, int j1, int n1, int n2, double[] work, int _work_offset, intW info)
/* 1686:     */   {
/* 1687: 991 */     Dlaexc.dlaexc(wantq, n, t, _t_offset, ldt, q, _q_offset, ldq, j1, n1, n2, work, _work_offset, info);
/* 1688:     */   }
/* 1689:     */   
/* 1690:     */   public void dlag2(double[] a, int lda, double[] b, int ldb, double safmin, doubleW scale1, doubleW scale2, doubleW wr1, doubleW wr2, doubleW wi)
/* 1691:     */   {
/* 1692: 996 */     Dlag2.dlag2(a, 0, lda, b, 0, ldb, safmin, scale1, scale2, wr1, wr2, wi);
/* 1693:     */   }
/* 1694:     */   
/* 1695:     */   public void dlag2(double[] a, int _a_offset, int lda, double[] b, int _b_offset, int ldb, double safmin, doubleW scale1, doubleW scale2, doubleW wr1, doubleW wr2, doubleW wi)
/* 1696:     */   {
/* 1697:1001 */     Dlag2.dlag2(a, _a_offset, lda, b, _b_offset, ldb, safmin, scale1, scale2, wr1, wr2, wi);
/* 1698:     */   }
/* 1699:     */   
/* 1700:     */   public void dlag2s(int m, int n, double[] a, int lda, float[] sa, int ldsa, intW info)
/* 1701:     */   {
/* 1702:1006 */     Dlag2s.dlag2s(m, n, a, 0, lda, sa, 0, ldsa, info);
/* 1703:     */   }
/* 1704:     */   
/* 1705:     */   public void dlag2s(int m, int n, double[] a, int _a_offset, int lda, float[] sa, int _sa_offset, int ldsa, intW info)
/* 1706:     */   {
/* 1707:1011 */     Dlag2s.dlag2s(m, n, a, _a_offset, lda, sa, _sa_offset, ldsa, info);
/* 1708:     */   }
/* 1709:     */   
/* 1710:     */   public void dlags2(boolean upper, double a1, double a2, double a3, double b1, double b2, double b3, doubleW csu, doubleW snu, doubleW csv, doubleW snv, doubleW csq, doubleW snq)
/* 1711:     */   {
/* 1712:1016 */     Dlags2.dlags2(upper, a1, a2, a3, b1, b2, b3, csu, snu, csv, snv, csq, snq);
/* 1713:     */   }
/* 1714:     */   
/* 1715:     */   public void dlagtf(int n, double[] a, double lambda, double[] b, double[] c, double tol, double[] d, int[] in, intW info)
/* 1716:     */   {
/* 1717:1021 */     Dlagtf.dlagtf(n, a, 0, lambda, b, 0, c, 0, tol, d, 0, in, 0, info);
/* 1718:     */   }
/* 1719:     */   
/* 1720:     */   public void dlagtf(int n, double[] a, int _a_offset, double lambda, double[] b, int _b_offset, double[] c, int _c_offset, double tol, double[] d, int _d_offset, int[] in, int _in_offset, intW info)
/* 1721:     */   {
/* 1722:1026 */     Dlagtf.dlagtf(n, a, _a_offset, lambda, b, _b_offset, c, _c_offset, tol, d, _d_offset, in, _in_offset, info);
/* 1723:     */   }
/* 1724:     */   
/* 1725:     */   public void dlagtm(String trans, int n, int nrhs, double alpha, double[] dl, double[] d, double[] du, double[] x, int ldx, double beta, double[] b, int ldb)
/* 1726:     */   {
/* 1727:1031 */     Dlagtm.dlagtm(trans, n, nrhs, alpha, dl, 0, d, 0, du, 0, x, 0, ldx, beta, b, 0, ldb);
/* 1728:     */   }
/* 1729:     */   
/* 1730:     */   public void dlagtm(String trans, int n, int nrhs, double alpha, double[] dl, int _dl_offset, double[] d, int _d_offset, double[] du, int _du_offset, double[] x, int _x_offset, int ldx, double beta, double[] b, int _b_offset, int ldb)
/* 1731:     */   {
/* 1732:1036 */     Dlagtm.dlagtm(trans, n, nrhs, alpha, dl, _dl_offset, d, _d_offset, du, _du_offset, x, _x_offset, ldx, beta, b, _b_offset, ldb);
/* 1733:     */   }
/* 1734:     */   
/* 1735:     */   public void dlagts(int job, int n, double[] a, double[] b, double[] c, double[] d, int[] in, double[] y, doubleW tol, intW info)
/* 1736:     */   {
/* 1737:1041 */     Dlagts.dlagts(job, n, a, 0, b, 0, c, 0, d, 0, in, 0, y, 0, tol, info);
/* 1738:     */   }
/* 1739:     */   
/* 1740:     */   public void dlagts(int job, int n, double[] a, int _a_offset, double[] b, int _b_offset, double[] c, int _c_offset, double[] d, int _d_offset, int[] in, int _in_offset, double[] y, int _y_offset, doubleW tol, intW info)
/* 1741:     */   {
/* 1742:1046 */     Dlagts.dlagts(job, n, a, _a_offset, b, _b_offset, c, _c_offset, d, _d_offset, in, _in_offset, y, _y_offset, tol, info);
/* 1743:     */   }
/* 1744:     */   
/* 1745:     */   public void dlagv2(double[] a, int lda, double[] b, int ldb, double[] alphar, double[] alphai, double[] beta, doubleW csl, doubleW snl, doubleW csr, doubleW snr)
/* 1746:     */   {
/* 1747:1051 */     Dlagv2.dlagv2(a, 0, lda, b, 0, ldb, alphar, 0, alphai, 0, beta, 0, csl, snl, csr, snr);
/* 1748:     */   }
/* 1749:     */   
/* 1750:     */   public void dlagv2(double[] a, int _a_offset, int lda, double[] b, int _b_offset, int ldb, double[] alphar, int _alphar_offset, double[] alphai, int _alphai_offset, double[] beta, int _beta_offset, doubleW csl, doubleW snl, doubleW csr, doubleW snr)
/* 1751:     */   {
/* 1752:1056 */     Dlagv2.dlagv2(a, _a_offset, lda, b, _b_offset, ldb, alphar, _alphar_offset, alphai, _alphai_offset, beta, _beta_offset, csl, snl, csr, snr);
/* 1753:     */   }
/* 1754:     */   
/* 1755:     */   public void dlahqr(boolean wantt, boolean wantz, int n, int ilo, int ihi, double[] h, int ldh, double[] wr, double[] wi, int iloz, int ihiz, double[] z, int ldz, intW info)
/* 1756:     */   {
/* 1757:1061 */     Dlahqr.dlahqr(wantt, wantz, n, ilo, ihi, h, 0, ldh, wr, 0, wi, 0, iloz, ihiz, z, 0, ldz, info);
/* 1758:     */   }
/* 1759:     */   
/* 1760:     */   public void dlahqr(boolean wantt, boolean wantz, int n, int ilo, int ihi, double[] h, int _h_offset, int ldh, double[] wr, int _wr_offset, double[] wi, int _wi_offset, int iloz, int ihiz, double[] z, int _z_offset, int ldz, intW info)
/* 1761:     */   {
/* 1762:1066 */     Dlahqr.dlahqr(wantt, wantz, n, ilo, ihi, h, _h_offset, ldh, wr, _wr_offset, wi, _wi_offset, iloz, ihiz, z, _z_offset, ldz, info);
/* 1763:     */   }
/* 1764:     */   
/* 1765:     */   public void dlahr2(int n, int k, int nb, double[] a, int lda, double[] tau, double[] t, int ldt, double[] y, int ldy)
/* 1766:     */   {
/* 1767:1071 */     Dlahr2.dlahr2(n, k, nb, a, 0, lda, tau, 0, t, 0, ldt, y, 0, ldy);
/* 1768:     */   }
/* 1769:     */   
/* 1770:     */   public void dlahr2(int n, int k, int nb, double[] a, int _a_offset, int lda, double[] tau, int _tau_offset, double[] t, int _t_offset, int ldt, double[] y, int _y_offset, int ldy)
/* 1771:     */   {
/* 1772:1076 */     Dlahr2.dlahr2(n, k, nb, a, _a_offset, lda, tau, _tau_offset, t, _t_offset, ldt, y, _y_offset, ldy);
/* 1773:     */   }
/* 1774:     */   
/* 1775:     */   public void dlahrd(int n, int k, int nb, double[] a, int lda, double[] tau, double[] t, int ldt, double[] y, int ldy)
/* 1776:     */   {
/* 1777:1081 */     Dlahrd.dlahrd(n, k, nb, a, 0, lda, tau, 0, t, 0, ldt, y, 0, ldy);
/* 1778:     */   }
/* 1779:     */   
/* 1780:     */   public void dlahrd(int n, int k, int nb, double[] a, int _a_offset, int lda, double[] tau, int _tau_offset, double[] t, int _t_offset, int ldt, double[] y, int _y_offset, int ldy)
/* 1781:     */   {
/* 1782:1086 */     Dlahrd.dlahrd(n, k, nb, a, _a_offset, lda, tau, _tau_offset, t, _t_offset, ldt, y, _y_offset, ldy);
/* 1783:     */   }
/* 1784:     */   
/* 1785:     */   public void dlaic1(int job, int j, double[] x, double sest, double[] w, double gamma, doubleW sestpr, doubleW s, doubleW c)
/* 1786:     */   {
/* 1787:1091 */     Dlaic1.dlaic1(job, j, x, 0, sest, w, 0, gamma, sestpr, s, c);
/* 1788:     */   }
/* 1789:     */   
/* 1790:     */   public void dlaic1(int job, int j, double[] x, int _x_offset, double sest, double[] w, int _w_offset, double gamma, doubleW sestpr, doubleW s, doubleW c)
/* 1791:     */   {
/* 1792:1096 */     Dlaic1.dlaic1(job, j, x, _x_offset, sest, w, _w_offset, gamma, sestpr, s, c);
/* 1793:     */   }
/* 1794:     */   
/* 1795:     */   public boolean dlaisnan(double din1, double din2)
/* 1796:     */   {
/* 1797:1101 */     return Dlaisnan.dlaisnan(din1, din2);
/* 1798:     */   }
/* 1799:     */   
/* 1800:     */   public void dlaln2(boolean ltrans, int na, int nw, double smin, double ca, double[] a, int lda, double d1, double d2, double[] b, int ldb, double wr, double wi, double[] x, int ldx, doubleW scale, doubleW xnorm, intW info)
/* 1801:     */   {
/* 1802:1106 */     Dlaln2.dlaln2(ltrans, na, nw, smin, ca, a, 0, lda, d1, d2, b, 0, ldb, wr, wi, x, 0, ldx, scale, xnorm, info);
/* 1803:     */   }
/* 1804:     */   
/* 1805:     */   public void dlaln2(boolean ltrans, int na, int nw, double smin, double ca, double[] a, int _a_offset, int lda, double d1, double d2, double[] b, int _b_offset, int ldb, double wr, double wi, double[] x, int _x_offset, int ldx, doubleW scale, doubleW xnorm, intW info)
/* 1806:     */   {
/* 1807:1111 */     Dlaln2.dlaln2(ltrans, na, nw, smin, ca, a, _a_offset, lda, d1, d2, b, _b_offset, ldb, wr, wi, x, _x_offset, ldx, scale, xnorm, info);
/* 1808:     */   }
/* 1809:     */   
/* 1810:     */   public void dlals0(int icompq, int nl, int nr, int sqre, int nrhs, double[] b, int ldb, double[] bx, int ldbx, int[] perm, int givptr, int[] givcol, int ldgcol, double[] givnum, int ldgnum, double[] poles, double[] difl, double[] difr, double[] z, int k, double c, double s, double[] work, intW info)
/* 1811:     */   {
/* 1812:1116 */     Dlals0.dlals0(icompq, nl, nr, sqre, nrhs, b, 0, ldb, bx, 0, ldbx, perm, 0, givptr, givcol, 0, ldgcol, givnum, 0, ldgnum, poles, 0, difl, 0, difr, 0, z, 0, k, c, s, work, 0, info);
/* 1813:     */   }
/* 1814:     */   
/* 1815:     */   public void dlals0(int icompq, int nl, int nr, int sqre, int nrhs, double[] b, int _b_offset, int ldb, double[] bx, int _bx_offset, int ldbx, int[] perm, int _perm_offset, int givptr, int[] givcol, int _givcol_offset, int ldgcol, double[] givnum, int _givnum_offset, int ldgnum, double[] poles, int _poles_offset, double[] difl, int _difl_offset, double[] difr, int _difr_offset, double[] z, int _z_offset, int k, double c, double s, double[] work, int _work_offset, intW info)
/* 1816:     */   {
/* 1817:1121 */     Dlals0.dlals0(icompq, nl, nr, sqre, nrhs, b, _b_offset, ldb, bx, _bx_offset, ldbx, perm, _perm_offset, givptr, givcol, _givcol_offset, ldgcol, givnum, _givnum_offset, ldgnum, poles, _poles_offset, difl, _difl_offset, difr, _difr_offset, z, _z_offset, k, c, s, work, _work_offset, info);
/* 1818:     */   }
/* 1819:     */   
/* 1820:     */   public void dlalsa(int icompq, int smlsiz, int n, int nrhs, double[] b, int ldb, double[] bx, int ldbx, double[] u, int ldu, double[] vt, int[] k, double[] difl, double[] difr, double[] z, double[] poles, int[] givptr, int[] givcol, int ldgcol, int[] perm, double[] givnum, double[] c, double[] s, double[] work, int[] iwork, intW info)
/* 1821:     */   {
/* 1822:1126 */     Dlalsa.dlalsa(icompq, smlsiz, n, nrhs, b, 0, ldb, bx, 0, ldbx, u, 0, ldu, vt, 0, k, 0, difl, 0, difr, 0, z, 0, poles, 0, givptr, 0, givcol, 0, ldgcol, perm, 0, givnum, 0, c, 0, s, 0, work, 0, iwork, 0, info);
/* 1823:     */   }
/* 1824:     */   
/* 1825:     */   public void dlalsa(int icompq, int smlsiz, int n, int nrhs, double[] b, int _b_offset, int ldb, double[] bx, int _bx_offset, int ldbx, double[] u, int _u_offset, int ldu, double[] vt, int _vt_offset, int[] k, int _k_offset, double[] difl, int _difl_offset, double[] difr, int _difr_offset, double[] z, int _z_offset, double[] poles, int _poles_offset, int[] givptr, int _givptr_offset, int[] givcol, int _givcol_offset, int ldgcol, int[] perm, int _perm_offset, double[] givnum, int _givnum_offset, double[] c, int _c_offset, double[] s, int _s_offset, double[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 1826:     */   {
/* 1827:1131 */     Dlalsa.dlalsa(icompq, smlsiz, n, nrhs, b, _b_offset, ldb, bx, _bx_offset, ldbx, u, _u_offset, ldu, vt, _vt_offset, k, _k_offset, difl, _difl_offset, difr, _difr_offset, z, _z_offset, poles, _poles_offset, givptr, _givptr_offset, givcol, _givcol_offset, ldgcol, perm, _perm_offset, givnum, _givnum_offset, c, _c_offset, s, _s_offset, work, _work_offset, iwork, _iwork_offset, info);
/* 1828:     */   }
/* 1829:     */   
/* 1830:     */   public void dlalsd(String uplo, int smlsiz, int n, int nrhs, double[] d, double[] e, double[] b, int ldb, double rcond, intW rank, double[] work, int[] iwork, intW info)
/* 1831:     */   {
/* 1832:1136 */     Dlalsd.dlalsd(uplo, smlsiz, n, nrhs, d, 0, e, 0, b, 0, ldb, rcond, rank, work, 0, iwork, 0, info);
/* 1833:     */   }
/* 1834:     */   
/* 1835:     */   public void dlalsd(String uplo, int smlsiz, int n, int nrhs, double[] d, int _d_offset, double[] e, int _e_offset, double[] b, int _b_offset, int ldb, double rcond, intW rank, double[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 1836:     */   {
/* 1837:1141 */     Dlalsd.dlalsd(uplo, smlsiz, n, nrhs, d, _d_offset, e, _e_offset, b, _b_offset, ldb, rcond, rank, work, _work_offset, iwork, _iwork_offset, info);
/* 1838:     */   }
/* 1839:     */   
/* 1840:     */   public void dlamrg(int n1, int n2, double[] a, int dtrd1, int dtrd2, int[] index)
/* 1841:     */   {
/* 1842:1146 */     Dlamrg.dlamrg(n1, n2, a, 0, dtrd1, dtrd2, index, 0);
/* 1843:     */   }
/* 1844:     */   
/* 1845:     */   public void dlamrg(int n1, int n2, double[] a, int _a_offset, int dtrd1, int dtrd2, int[] index, int _index_offset)
/* 1846:     */   {
/* 1847:1151 */     Dlamrg.dlamrg(n1, n2, a, _a_offset, dtrd1, dtrd2, index, _index_offset);
/* 1848:     */   }
/* 1849:     */   
/* 1850:     */   public int dlaneg(int n, double[] d, double[] lld, double sigma, double pivmin, int r)
/* 1851:     */   {
/* 1852:1156 */     return Dlaneg.dlaneg(n, d, 0, lld, 0, sigma, pivmin, r);
/* 1853:     */   }
/* 1854:     */   
/* 1855:     */   public int dlaneg(int n, double[] d, int _d_offset, double[] lld, int _lld_offset, double sigma, double pivmin, int r)
/* 1856:     */   {
/* 1857:1161 */     return Dlaneg.dlaneg(n, d, _d_offset, lld, _lld_offset, sigma, pivmin, r);
/* 1858:     */   }
/* 1859:     */   
/* 1860:     */   public double dlangb(String norm, int n, int kl, int ku, double[] ab, int ldab, double[] work)
/* 1861:     */   {
/* 1862:1166 */     return Dlangb.dlangb(norm, n, kl, ku, ab, 0, ldab, work, 0);
/* 1863:     */   }
/* 1864:     */   
/* 1865:     */   public double dlangb(String norm, int n, int kl, int ku, double[] ab, int _ab_offset, int ldab, double[] work, int _work_offset)
/* 1866:     */   {
/* 1867:1171 */     return Dlangb.dlangb(norm, n, kl, ku, ab, _ab_offset, ldab, work, _work_offset);
/* 1868:     */   }
/* 1869:     */   
/* 1870:     */   public double dlange(String norm, int m, int n, double[] a, int lda, double[] work)
/* 1871:     */   {
/* 1872:1176 */     return Dlange.dlange(norm, m, n, a, 0, lda, work, 0);
/* 1873:     */   }
/* 1874:     */   
/* 1875:     */   public double dlange(String norm, int m, int n, double[] a, int _a_offset, int lda, double[] work, int _work_offset)
/* 1876:     */   {
/* 1877:1181 */     return Dlange.dlange(norm, m, n, a, _a_offset, lda, work, _work_offset);
/* 1878:     */   }
/* 1879:     */   
/* 1880:     */   public double dlangt(String norm, int n, double[] dl, double[] d, double[] du)
/* 1881:     */   {
/* 1882:1186 */     return Dlangt.dlangt(norm, n, dl, 0, d, 0, du, 0);
/* 1883:     */   }
/* 1884:     */   
/* 1885:     */   public double dlangt(String norm, int n, double[] dl, int _dl_offset, double[] d, int _d_offset, double[] du, int _du_offset)
/* 1886:     */   {
/* 1887:1191 */     return Dlangt.dlangt(norm, n, dl, _dl_offset, d, _d_offset, du, _du_offset);
/* 1888:     */   }
/* 1889:     */   
/* 1890:     */   public double dlanhs(String norm, int n, double[] a, int lda, double[] work)
/* 1891:     */   {
/* 1892:1196 */     return Dlanhs.dlanhs(norm, n, a, 0, lda, work, 0);
/* 1893:     */   }
/* 1894:     */   
/* 1895:     */   public double dlanhs(String norm, int n, double[] a, int _a_offset, int lda, double[] work, int _work_offset)
/* 1896:     */   {
/* 1897:1201 */     return Dlanhs.dlanhs(norm, n, a, _a_offset, lda, work, _work_offset);
/* 1898:     */   }
/* 1899:     */   
/* 1900:     */   public double dlansb(String norm, String uplo, int n, int k, double[] ab, int ldab, double[] work)
/* 1901:     */   {
/* 1902:1206 */     return Dlansb.dlansb(norm, uplo, n, k, ab, 0, ldab, work, 0);
/* 1903:     */   }
/* 1904:     */   
/* 1905:     */   public double dlansb(String norm, String uplo, int n, int k, double[] ab, int _ab_offset, int ldab, double[] work, int _work_offset)
/* 1906:     */   {
/* 1907:1211 */     return Dlansb.dlansb(norm, uplo, n, k, ab, _ab_offset, ldab, work, _work_offset);
/* 1908:     */   }
/* 1909:     */   
/* 1910:     */   public double dlansp(String norm, String uplo, int n, double[] ap, double[] work)
/* 1911:     */   {
/* 1912:1216 */     return Dlansp.dlansp(norm, uplo, n, ap, 0, work, 0);
/* 1913:     */   }
/* 1914:     */   
/* 1915:     */   public double dlansp(String norm, String uplo, int n, double[] ap, int _ap_offset, double[] work, int _work_offset)
/* 1916:     */   {
/* 1917:1221 */     return Dlansp.dlansp(norm, uplo, n, ap, _ap_offset, work, _work_offset);
/* 1918:     */   }
/* 1919:     */   
/* 1920:     */   public double dlanst(String norm, int n, double[] d, double[] e)
/* 1921:     */   {
/* 1922:1226 */     return Dlanst.dlanst(norm, n, d, 0, e, 0);
/* 1923:     */   }
/* 1924:     */   
/* 1925:     */   public double dlanst(String norm, int n, double[] d, int _d_offset, double[] e, int _e_offset)
/* 1926:     */   {
/* 1927:1231 */     return Dlanst.dlanst(norm, n, d, _d_offset, e, _e_offset);
/* 1928:     */   }
/* 1929:     */   
/* 1930:     */   public double dlansy(String norm, String uplo, int n, double[] a, int lda, double[] work)
/* 1931:     */   {
/* 1932:1236 */     return Dlansy.dlansy(norm, uplo, n, a, 0, lda, work, 0);
/* 1933:     */   }
/* 1934:     */   
/* 1935:     */   public double dlansy(String norm, String uplo, int n, double[] a, int _a_offset, int lda, double[] work, int _work_offset)
/* 1936:     */   {
/* 1937:1241 */     return Dlansy.dlansy(norm, uplo, n, a, _a_offset, lda, work, _work_offset);
/* 1938:     */   }
/* 1939:     */   
/* 1940:     */   public double dlantb(String norm, String uplo, String diag, int n, int k, double[] ab, int ldab, double[] work)
/* 1941:     */   {
/* 1942:1246 */     return Dlantb.dlantb(norm, uplo, diag, n, k, ab, 0, ldab, work, 0);
/* 1943:     */   }
/* 1944:     */   
/* 1945:     */   public double dlantb(String norm, String uplo, String diag, int n, int k, double[] ab, int _ab_offset, int ldab, double[] work, int _work_offset)
/* 1946:     */   {
/* 1947:1251 */     return Dlantb.dlantb(norm, uplo, diag, n, k, ab, _ab_offset, ldab, work, _work_offset);
/* 1948:     */   }
/* 1949:     */   
/* 1950:     */   public double dlantp(String norm, String uplo, String diag, int n, double[] ap, double[] work)
/* 1951:     */   {
/* 1952:1256 */     return Dlantp.dlantp(norm, uplo, diag, n, ap, 0, work, 0);
/* 1953:     */   }
/* 1954:     */   
/* 1955:     */   public double dlantp(String norm, String uplo, String diag, int n, double[] ap, int _ap_offset, double[] work, int _work_offset)
/* 1956:     */   {
/* 1957:1261 */     return Dlantp.dlantp(norm, uplo, diag, n, ap, _ap_offset, work, _work_offset);
/* 1958:     */   }
/* 1959:     */   
/* 1960:     */   public double dlantr(String norm, String uplo, String diag, int m, int n, double[] a, int lda, double[] work)
/* 1961:     */   {
/* 1962:1266 */     return Dlantr.dlantr(norm, uplo, diag, m, n, a, 0, lda, work, 0);
/* 1963:     */   }
/* 1964:     */   
/* 1965:     */   public double dlantr(String norm, String uplo, String diag, int m, int n, double[] a, int _a_offset, int lda, double[] work, int _work_offset)
/* 1966:     */   {
/* 1967:1271 */     return Dlantr.dlantr(norm, uplo, diag, m, n, a, _a_offset, lda, work, _work_offset);
/* 1968:     */   }
/* 1969:     */   
/* 1970:     */   public void dlanv2(doubleW a, doubleW b, doubleW c, doubleW d, doubleW rt1r, doubleW rt1i, doubleW rt2r, doubleW rt2i, doubleW cs, doubleW sn)
/* 1971:     */   {
/* 1972:1276 */     Dlanv2.dlanv2(a, b, c, d, rt1r, rt1i, rt2r, rt2i, cs, sn);
/* 1973:     */   }
/* 1974:     */   
/* 1975:     */   public void dlapll(int n, double[] x, int incx, double[] y, int incy, doubleW ssmin)
/* 1976:     */   {
/* 1977:1281 */     Dlapll.dlapll(n, x, 0, incx, y, 0, incy, ssmin);
/* 1978:     */   }
/* 1979:     */   
/* 1980:     */   public void dlapll(int n, double[] x, int _x_offset, int incx, double[] y, int _y_offset, int incy, doubleW ssmin)
/* 1981:     */   {
/* 1982:1286 */     Dlapll.dlapll(n, x, _x_offset, incx, y, _y_offset, incy, ssmin);
/* 1983:     */   }
/* 1984:     */   
/* 1985:     */   public void dlapmt(boolean forwrd, int m, int n, double[] x, int ldx, int[] k)
/* 1986:     */   {
/* 1987:1291 */     Dlapmt.dlapmt(forwrd, m, n, x, 0, ldx, k, 0);
/* 1988:     */   }
/* 1989:     */   
/* 1990:     */   public void dlapmt(boolean forwrd, int m, int n, double[] x, int _x_offset, int ldx, int[] k, int _k_offset)
/* 1991:     */   {
/* 1992:1296 */     Dlapmt.dlapmt(forwrd, m, n, x, _x_offset, ldx, k, _k_offset);
/* 1993:     */   }
/* 1994:     */   
/* 1995:     */   public double dlapy2(double x, double y)
/* 1996:     */   {
/* 1997:1301 */     return Dlapy2.dlapy2(x, y);
/* 1998:     */   }
/* 1999:     */   
/* 2000:     */   public double dlapy3(double x, double y, double z)
/* 2001:     */   {
/* 2002:1306 */     return Dlapy3.dlapy3(x, y, z);
/* 2003:     */   }
/* 2004:     */   
/* 2005:     */   public void dlaqgb(int m, int n, int kl, int ku, double[] ab, int ldab, double[] r, double[] c, double rowcnd, double colcnd, double amax, StringW equed)
/* 2006:     */   {
/* 2007:1311 */     Dlaqgb.dlaqgb(m, n, kl, ku, ab, 0, ldab, r, 0, c, 0, rowcnd, colcnd, amax, equed);
/* 2008:     */   }
/* 2009:     */   
/* 2010:     */   public void dlaqgb(int m, int n, int kl, int ku, double[] ab, int _ab_offset, int ldab, double[] r, int _r_offset, double[] c, int _c_offset, double rowcnd, double colcnd, double amax, StringW equed)
/* 2011:     */   {
/* 2012:1316 */     Dlaqgb.dlaqgb(m, n, kl, ku, ab, _ab_offset, ldab, r, _r_offset, c, _c_offset, rowcnd, colcnd, amax, equed);
/* 2013:     */   }
/* 2014:     */   
/* 2015:     */   public void dlaqge(int m, int n, double[] a, int lda, double[] r, double[] c, double rowcnd, double colcnd, double amax, StringW equed)
/* 2016:     */   {
/* 2017:1321 */     Dlaqge.dlaqge(m, n, a, 0, lda, r, 0, c, 0, rowcnd, colcnd, amax, equed);
/* 2018:     */   }
/* 2019:     */   
/* 2020:     */   public void dlaqge(int m, int n, double[] a, int _a_offset, int lda, double[] r, int _r_offset, double[] c, int _c_offset, double rowcnd, double colcnd, double amax, StringW equed)
/* 2021:     */   {
/* 2022:1326 */     Dlaqge.dlaqge(m, n, a, _a_offset, lda, r, _r_offset, c, _c_offset, rowcnd, colcnd, amax, equed);
/* 2023:     */   }
/* 2024:     */   
/* 2025:     */   public void dlaqp2(int m, int n, int offset, double[] a, int lda, int[] jpvt, double[] tau, double[] vn1, double[] vn2, double[] work)
/* 2026:     */   {
/* 2027:1331 */     Dlaqp2.dlaqp2(m, n, offset, a, 0, lda, jpvt, 0, tau, 0, vn1, 0, vn2, 0, work, 0);
/* 2028:     */   }
/* 2029:     */   
/* 2030:     */   public void dlaqp2(int m, int n, int offset, double[] a, int _a_offset, int lda, int[] jpvt, int _jpvt_offset, double[] tau, int _tau_offset, double[] vn1, int _vn1_offset, double[] vn2, int _vn2_offset, double[] work, int _work_offset)
/* 2031:     */   {
/* 2032:1336 */     Dlaqp2.dlaqp2(m, n, offset, a, _a_offset, lda, jpvt, _jpvt_offset, tau, _tau_offset, vn1, _vn1_offset, vn2, _vn2_offset, work, _work_offset);
/* 2033:     */   }
/* 2034:     */   
/* 2035:     */   public void dlaqps(int m, int n, int offset, int nb, intW kb, double[] a, int lda, int[] jpvt, double[] tau, double[] vn1, double[] vn2, double[] auxv, double[] f, int ldf)
/* 2036:     */   {
/* 2037:1341 */     Dlaqps.dlaqps(m, n, offset, nb, kb, a, 0, lda, jpvt, 0, tau, 0, vn1, 0, vn2, 0, auxv, 0, f, 0, ldf);
/* 2038:     */   }
/* 2039:     */   
/* 2040:     */   public void dlaqps(int m, int n, int offset, int nb, intW kb, double[] a, int _a_offset, int lda, int[] jpvt, int _jpvt_offset, double[] tau, int _tau_offset, double[] vn1, int _vn1_offset, double[] vn2, int _vn2_offset, double[] auxv, int _auxv_offset, double[] f, int _f_offset, int ldf)
/* 2041:     */   {
/* 2042:1346 */     Dlaqps.dlaqps(m, n, offset, nb, kb, a, _a_offset, lda, jpvt, _jpvt_offset, tau, _tau_offset, vn1, _vn1_offset, vn2, _vn2_offset, auxv, _auxv_offset, f, _f_offset, ldf);
/* 2043:     */   }
/* 2044:     */   
/* 2045:     */   public void dlaqr0(boolean wantt, boolean wantz, int n, int ilo, int ihi, double[] h, int ldh, double[] wr, double[] wi, int iloz, int ihiz, double[] z, int ldz, double[] work, int lwork, intW info)
/* 2046:     */   {
/* 2047:1351 */     Dlaqr0.dlaqr0(wantt, wantz, n, ilo, ihi, h, 0, ldh, wr, 0, wi, 0, iloz, ihiz, z, 0, ldz, work, 0, lwork, info);
/* 2048:     */   }
/* 2049:     */   
/* 2050:     */   public void dlaqr0(boolean wantt, boolean wantz, int n, int ilo, int ihi, double[] h, int _h_offset, int ldh, double[] wr, int _wr_offset, double[] wi, int _wi_offset, int iloz, int ihiz, double[] z, int _z_offset, int ldz, double[] work, int _work_offset, int lwork, intW info)
/* 2051:     */   {
/* 2052:1356 */     Dlaqr0.dlaqr0(wantt, wantz, n, ilo, ihi, h, _h_offset, ldh, wr, _wr_offset, wi, _wi_offset, iloz, ihiz, z, _z_offset, ldz, work, _work_offset, lwork, info);
/* 2053:     */   }
/* 2054:     */   
/* 2055:     */   public void dlaqr1(int n, double[] h, int ldh, double sr1, double si1, double sr2, double si2, double[] v)
/* 2056:     */   {
/* 2057:1361 */     Dlaqr1.dlaqr1(n, h, 0, ldh, sr1, si1, sr2, si2, v, 0);
/* 2058:     */   }
/* 2059:     */   
/* 2060:     */   public void dlaqr1(int n, double[] h, int _h_offset, int ldh, double sr1, double si1, double sr2, double si2, double[] v, int _v_offset)
/* 2061:     */   {
/* 2062:1366 */     Dlaqr1.dlaqr1(n, h, _h_offset, ldh, sr1, si1, sr2, si2, v, _v_offset);
/* 2063:     */   }
/* 2064:     */   
/* 2065:     */   public void dlaqr2(boolean wantt, boolean wantz, int n, int ktop, int kbot, int nw, double[] h, int ldh, int iloz, int ihiz, double[] z, int ldz, intW ns, intW nd, double[] sr, double[] si, double[] v, int ldv, int nh, double[] t, int ldt, int nv, double[] wv, int ldwv, double[] work, int lwork)
/* 2066:     */   {
/* 2067:1371 */     Dlaqr2.dlaqr2(wantt, wantz, n, ktop, kbot, nw, h, 0, ldh, iloz, ihiz, z, 0, ldz, ns, nd, sr, 0, si, 0, v, 0, ldv, nh, t, 0, ldt, nv, wv, 0, ldwv, work, 0, lwork);
/* 2068:     */   }
/* 2069:     */   
/* 2070:     */   public void dlaqr2(boolean wantt, boolean wantz, int n, int ktop, int kbot, int nw, double[] h, int _h_offset, int ldh, int iloz, int ihiz, double[] z, int _z_offset, int ldz, intW ns, intW nd, double[] sr, int _sr_offset, double[] si, int _si_offset, double[] v, int _v_offset, int ldv, int nh, double[] t, int _t_offset, int ldt, int nv, double[] wv, int _wv_offset, int ldwv, double[] work, int _work_offset, int lwork)
/* 2071:     */   {
/* 2072:1376 */     Dlaqr2.dlaqr2(wantt, wantz, n, ktop, kbot, nw, h, _h_offset, ldh, iloz, ihiz, z, _z_offset, ldz, ns, nd, sr, _sr_offset, si, _si_offset, v, _v_offset, ldv, nh, t, _t_offset, ldt, nv, wv, _wv_offset, ldwv, work, _work_offset, lwork);
/* 2073:     */   }
/* 2074:     */   
/* 2075:     */   public void dlaqr3(boolean wantt, boolean wantz, int n, int ktop, int kbot, int nw, double[] h, int ldh, int iloz, int ihiz, double[] z, int ldz, intW ns, intW nd, double[] sr, double[] si, double[] v, int ldv, int nh, double[] t, int ldt, int nv, double[] wv, int ldwv, double[] work, int lwork)
/* 2076:     */   {
/* 2077:1381 */     Dlaqr3.dlaqr3(wantt, wantz, n, ktop, kbot, nw, h, 0, ldh, iloz, ihiz, z, 0, ldz, ns, nd, sr, 0, si, 0, v, 0, ldv, nh, t, 0, ldt, nv, wv, 0, ldwv, work, 0, lwork);
/* 2078:     */   }
/* 2079:     */   
/* 2080:     */   public void dlaqr3(boolean wantt, boolean wantz, int n, int ktop, int kbot, int nw, double[] h, int _h_offset, int ldh, int iloz, int ihiz, double[] z, int _z_offset, int ldz, intW ns, intW nd, double[] sr, int _sr_offset, double[] si, int _si_offset, double[] v, int _v_offset, int ldv, int nh, double[] t, int _t_offset, int ldt, int nv, double[] wv, int _wv_offset, int ldwv, double[] work, int _work_offset, int lwork)
/* 2081:     */   {
/* 2082:1386 */     Dlaqr3.dlaqr3(wantt, wantz, n, ktop, kbot, nw, h, _h_offset, ldh, iloz, ihiz, z, _z_offset, ldz, ns, nd, sr, _sr_offset, si, _si_offset, v, _v_offset, ldv, nh, t, _t_offset, ldt, nv, wv, _wv_offset, ldwv, work, _work_offset, lwork);
/* 2083:     */   }
/* 2084:     */   
/* 2085:     */   public void dlaqr4(boolean wantt, boolean wantz, int n, int ilo, int ihi, double[] h, int ldh, double[] wr, double[] wi, int iloz, int ihiz, double[] z, int ldz, double[] work, int lwork, intW info)
/* 2086:     */   {
/* 2087:1391 */     Dlaqr4.dlaqr4(wantt, wantz, n, ilo, ihi, h, 0, ldh, wr, 0, wi, 0, iloz, ihiz, z, 0, ldz, work, 0, lwork, info);
/* 2088:     */   }
/* 2089:     */   
/* 2090:     */   public void dlaqr4(boolean wantt, boolean wantz, int n, int ilo, int ihi, double[] h, int _h_offset, int ldh, double[] wr, int _wr_offset, double[] wi, int _wi_offset, int iloz, int ihiz, double[] z, int _z_offset, int ldz, double[] work, int _work_offset, int lwork, intW info)
/* 2091:     */   {
/* 2092:1396 */     Dlaqr4.dlaqr4(wantt, wantz, n, ilo, ihi, h, _h_offset, ldh, wr, _wr_offset, wi, _wi_offset, iloz, ihiz, z, _z_offset, ldz, work, _work_offset, lwork, info);
/* 2093:     */   }
/* 2094:     */   
/* 2095:     */   public void dlaqr5(boolean wantt, boolean wantz, int kacc22, int n, int ktop, int kbot, int nshfts, double[] sr, double[] si, double[] h, int ldh, int iloz, int ihiz, double[] z, int ldz, double[] v, int ldv, double[] u, int ldu, int nv, double[] wv, int ldwv, int nh, double[] wh, int ldwh)
/* 2096:     */   {
/* 2097:1401 */     Dlaqr5.dlaqr5(wantt, wantz, kacc22, n, ktop, kbot, nshfts, sr, 0, si, 0, h, 0, ldh, iloz, ihiz, z, 0, ldz, v, 0, ldv, u, 0, ldu, nv, wv, 0, ldwv, nh, wh, 0, ldwh);
/* 2098:     */   }
/* 2099:     */   
/* 2100:     */   public void dlaqr5(boolean wantt, boolean wantz, int kacc22, int n, int ktop, int kbot, int nshfts, double[] sr, int _sr_offset, double[] si, int _si_offset, double[] h, int _h_offset, int ldh, int iloz, int ihiz, double[] z, int _z_offset, int ldz, double[] v, int _v_offset, int ldv, double[] u, int _u_offset, int ldu, int nv, double[] wv, int _wv_offset, int ldwv, int nh, double[] wh, int _wh_offset, int ldwh)
/* 2101:     */   {
/* 2102:1406 */     Dlaqr5.dlaqr5(wantt, wantz, kacc22, n, ktop, kbot, nshfts, sr, _sr_offset, si, _si_offset, h, _h_offset, ldh, iloz, ihiz, z, _z_offset, ldz, v, _v_offset, ldv, u, _u_offset, ldu, nv, wv, _wv_offset, ldwv, nh, wh, _wh_offset, ldwh);
/* 2103:     */   }
/* 2104:     */   
/* 2105:     */   public void dlaqsb(String uplo, int n, int kd, double[] ab, int ldab, double[] s, double scond, double amax, StringW equed)
/* 2106:     */   {
/* 2107:1411 */     Dlaqsb.dlaqsb(uplo, n, kd, ab, 0, ldab, s, 0, scond, amax, equed);
/* 2108:     */   }
/* 2109:     */   
/* 2110:     */   public void dlaqsb(String uplo, int n, int kd, double[] ab, int _ab_offset, int ldab, double[] s, int _s_offset, double scond, double amax, StringW equed)
/* 2111:     */   {
/* 2112:1416 */     Dlaqsb.dlaqsb(uplo, n, kd, ab, _ab_offset, ldab, s, _s_offset, scond, amax, equed);
/* 2113:     */   }
/* 2114:     */   
/* 2115:     */   public void dlaqsp(String uplo, int n, double[] ap, double[] s, double scond, double amax, StringW equed)
/* 2116:     */   {
/* 2117:1421 */     Dlaqsp.dlaqsp(uplo, n, ap, 0, s, 0, scond, amax, equed);
/* 2118:     */   }
/* 2119:     */   
/* 2120:     */   public void dlaqsp(String uplo, int n, double[] ap, int _ap_offset, double[] s, int _s_offset, double scond, double amax, StringW equed)
/* 2121:     */   {
/* 2122:1426 */     Dlaqsp.dlaqsp(uplo, n, ap, _ap_offset, s, _s_offset, scond, amax, equed);
/* 2123:     */   }
/* 2124:     */   
/* 2125:     */   public void dlaqsy(String uplo, int n, double[] a, int lda, double[] s, double scond, double amax, StringW equed)
/* 2126:     */   {
/* 2127:1431 */     Dlaqsy.dlaqsy(uplo, n, a, 0, lda, s, 0, scond, amax, equed);
/* 2128:     */   }
/* 2129:     */   
/* 2130:     */   public void dlaqsy(String uplo, int n, double[] a, int _a_offset, int lda, double[] s, int _s_offset, double scond, double amax, StringW equed)
/* 2131:     */   {
/* 2132:1436 */     Dlaqsy.dlaqsy(uplo, n, a, _a_offset, lda, s, _s_offset, scond, amax, equed);
/* 2133:     */   }
/* 2134:     */   
/* 2135:     */   public void dlaqtr(boolean ltran, boolean lreal, int n, double[] t, int ldt, double[] b, double w, doubleW scale, double[] x, double[] work, intW info)
/* 2136:     */   {
/* 2137:1441 */     Dlaqtr.dlaqtr(ltran, lreal, n, t, 0, ldt, b, 0, w, scale, x, 0, work, 0, info);
/* 2138:     */   }
/* 2139:     */   
/* 2140:     */   public void dlaqtr(boolean ltran, boolean lreal, int n, double[] t, int _t_offset, int ldt, double[] b, int _b_offset, double w, doubleW scale, double[] x, int _x_offset, double[] work, int _work_offset, intW info)
/* 2141:     */   {
/* 2142:1446 */     Dlaqtr.dlaqtr(ltran, lreal, n, t, _t_offset, ldt, b, _b_offset, w, scale, x, _x_offset, work, _work_offset, info);
/* 2143:     */   }
/* 2144:     */   
/* 2145:     */   public void dlar1v(int n, int b1, int bn, double lambda, double[] d, double[] l, double[] ld, double[] lld, double pivmin, double gaptol, double[] z, boolean wantnc, intW negcnt, doubleW ztz, doubleW mingma, intW r, int[] isuppz, doubleW nrminv, doubleW resid, doubleW rqcorr, double[] work)
/* 2146:     */   {
/* 2147:1451 */     Dlar1v.dlar1v(n, b1, bn, lambda, d, 0, l, 0, ld, 0, lld, 0, pivmin, gaptol, z, 0, wantnc, negcnt, ztz, mingma, r, isuppz, 0, nrminv, resid, rqcorr, work, 0);
/* 2148:     */   }
/* 2149:     */   
/* 2150:     */   public void dlar1v(int n, int b1, int bn, double lambda, double[] d, int _d_offset, double[] l, int _l_offset, double[] ld, int _ld_offset, double[] lld, int _lld_offset, double pivmin, double gaptol, double[] z, int _z_offset, boolean wantnc, intW negcnt, doubleW ztz, doubleW mingma, intW r, int[] isuppz, int _isuppz_offset, doubleW nrminv, doubleW resid, doubleW rqcorr, double[] work, int _work_offset)
/* 2151:     */   {
/* 2152:1456 */     Dlar1v.dlar1v(n, b1, bn, lambda, d, _d_offset, l, _l_offset, ld, _ld_offset, lld, _lld_offset, pivmin, gaptol, z, _z_offset, wantnc, negcnt, ztz, mingma, r, isuppz, _isuppz_offset, nrminv, resid, rqcorr, work, _work_offset);
/* 2153:     */   }
/* 2154:     */   
/* 2155:     */   public void dlar2v(int n, double[] x, double[] y, double[] z, int incx, double[] c, double[] s, int incc)
/* 2156:     */   {
/* 2157:1461 */     Dlar2v.dlar2v(n, x, 0, y, 0, z, 0, incx, c, 0, s, 0, incc);
/* 2158:     */   }
/* 2159:     */   
/* 2160:     */   public void dlar2v(int n, double[] x, int _x_offset, double[] y, int _y_offset, double[] z, int _z_offset, int incx, double[] c, int _c_offset, double[] s, int _s_offset, int incc)
/* 2161:     */   {
/* 2162:1466 */     Dlar2v.dlar2v(n, x, _x_offset, y, _y_offset, z, _z_offset, incx, c, _c_offset, s, _s_offset, incc);
/* 2163:     */   }
/* 2164:     */   
/* 2165:     */   public void dlarf(String side, int m, int n, double[] v, int incv, double tau, double[] c, int Ldc, double[] work)
/* 2166:     */   {
/* 2167:1471 */     Dlarf.dlarf(side, m, n, v, 0, incv, tau, c, 0, Ldc, work, 0);
/* 2168:     */   }
/* 2169:     */   
/* 2170:     */   public void dlarf(String side, int m, int n, double[] v, int _v_offset, int incv, double tau, double[] c, int _c_offset, int Ldc, double[] work, int _work_offset)
/* 2171:     */   {
/* 2172:1476 */     Dlarf.dlarf(side, m, n, v, _v_offset, incv, tau, c, _c_offset, Ldc, work, _work_offset);
/* 2173:     */   }
/* 2174:     */   
/* 2175:     */   public void dlarfb(String side, String trans, String direct, String storev, int m, int n, int k, double[] v, int ldv, double[] t, int ldt, double[] c, int Ldc, double[] work, int ldwork)
/* 2176:     */   {
/* 2177:1481 */     Dlarfb.dlarfb(side, trans, direct, storev, m, n, k, v, 0, ldv, t, 0, ldt, c, 0, Ldc, work, 0, ldwork);
/* 2178:     */   }
/* 2179:     */   
/* 2180:     */   public void dlarfb(String side, String trans, String direct, String storev, int m, int n, int k, double[] v, int _v_offset, int ldv, double[] t, int _t_offset, int ldt, double[] c, int _c_offset, int Ldc, double[] work, int _work_offset, int ldwork)
/* 2181:     */   {
/* 2182:1486 */     Dlarfb.dlarfb(side, trans, direct, storev, m, n, k, v, _v_offset, ldv, t, _t_offset, ldt, c, _c_offset, Ldc, work, _work_offset, ldwork);
/* 2183:     */   }
/* 2184:     */   
/* 2185:     */   public void dlarfg(int n, doubleW alpha, double[] x, int incx, doubleW tau)
/* 2186:     */   {
/* 2187:1491 */     Dlarfg.dlarfg(n, alpha, x, 0, incx, tau);
/* 2188:     */   }
/* 2189:     */   
/* 2190:     */   public void dlarfg(int n, doubleW alpha, double[] x, int _x_offset, int incx, doubleW tau)
/* 2191:     */   {
/* 2192:1496 */     Dlarfg.dlarfg(n, alpha, x, _x_offset, incx, tau);
/* 2193:     */   }
/* 2194:     */   
/* 2195:     */   public void dlarft(String direct, String storev, int n, int k, double[] v, int ldv, double[] tau, double[] t, int ldt)
/* 2196:     */   {
/* 2197:1501 */     Dlarft.dlarft(direct, storev, n, k, v, 0, ldv, tau, 0, t, 0, ldt);
/* 2198:     */   }
/* 2199:     */   
/* 2200:     */   public void dlarft(String direct, String storev, int n, int k, double[] v, int _v_offset, int ldv, double[] tau, int _tau_offset, double[] t, int _t_offset, int ldt)
/* 2201:     */   {
/* 2202:1506 */     Dlarft.dlarft(direct, storev, n, k, v, _v_offset, ldv, tau, _tau_offset, t, _t_offset, ldt);
/* 2203:     */   }
/* 2204:     */   
/* 2205:     */   public void dlarfx(String side, int m, int n, double[] v, double tau, double[] c, int Ldc, double[] work)
/* 2206:     */   {
/* 2207:1511 */     Dlarfx.dlarfx(side, m, n, v, 0, tau, c, 0, Ldc, work, 0);
/* 2208:     */   }
/* 2209:     */   
/* 2210:     */   public void dlarfx(String side, int m, int n, double[] v, int _v_offset, double tau, double[] c, int _c_offset, int Ldc, double[] work, int _work_offset)
/* 2211:     */   {
/* 2212:1516 */     Dlarfx.dlarfx(side, m, n, v, _v_offset, tau, c, _c_offset, Ldc, work, _work_offset);
/* 2213:     */   }
/* 2214:     */   
/* 2215:     */   public void dlargv(int n, double[] x, int incx, double[] y, int incy, double[] c, int incc)
/* 2216:     */   {
/* 2217:1521 */     Dlargv.dlargv(n, x, 0, incx, y, 0, incy, c, 0, incc);
/* 2218:     */   }
/* 2219:     */   
/* 2220:     */   public void dlargv(int n, double[] x, int _x_offset, int incx, double[] y, int _y_offset, int incy, double[] c, int _c_offset, int incc)
/* 2221:     */   {
/* 2222:1526 */     Dlargv.dlargv(n, x, _x_offset, incx, y, _y_offset, incy, c, _c_offset, incc);
/* 2223:     */   }
/* 2224:     */   
/* 2225:     */   public void dlarnv(int idist, int[] iseed, int n, double[] x)
/* 2226:     */   {
/* 2227:1531 */     Dlarnv.dlarnv(idist, iseed, 0, n, x, 0);
/* 2228:     */   }
/* 2229:     */   
/* 2230:     */   public void dlarnv(int idist, int[] iseed, int _iseed_offset, int n, double[] x, int _x_offset)
/* 2231:     */   {
/* 2232:1536 */     Dlarnv.dlarnv(idist, iseed, _iseed_offset, n, x, _x_offset);
/* 2233:     */   }
/* 2234:     */   
/* 2235:     */   public void dlarra(int n, double[] d, double[] e, double[] e2, double spltol, double tnrm, intW nsplit, int[] isplit, intW info)
/* 2236:     */   {
/* 2237:1541 */     Dlarra.dlarra(n, d, 0, e, 0, e2, 0, spltol, tnrm, nsplit, isplit, 0, info);
/* 2238:     */   }
/* 2239:     */   
/* 2240:     */   public void dlarra(int n, double[] d, int _d_offset, double[] e, int _e_offset, double[] e2, int _e2_offset, double spltol, double tnrm, intW nsplit, int[] isplit, int _isplit_offset, intW info)
/* 2241:     */   {
/* 2242:1546 */     Dlarra.dlarra(n, d, _d_offset, e, _e_offset, e2, _e2_offset, spltol, tnrm, nsplit, isplit, _isplit_offset, info);
/* 2243:     */   }
/* 2244:     */   
/* 2245:     */   public void dlarrb(int n, double[] d, double[] lld, int ifirst, int ilast, double rtol1, double rtol2, int offset, double[] w, double[] wgap, double[] werr, double[] work, int[] iwork, double pivmin, double spdiam, int twist, intW info)
/* 2246:     */   {
/* 2247:1551 */     Dlarrb.dlarrb(n, d, 0, lld, 0, ifirst, ilast, rtol1, rtol2, offset, w, 0, wgap, 0, werr, 0, work, 0, iwork, 0, pivmin, spdiam, twist, info);
/* 2248:     */   }
/* 2249:     */   
/* 2250:     */   public void dlarrb(int n, double[] d, int _d_offset, double[] lld, int _lld_offset, int ifirst, int ilast, double rtol1, double rtol2, int offset, double[] w, int _w_offset, double[] wgap, int _wgap_offset, double[] werr, int _werr_offset, double[] work, int _work_offset, int[] iwork, int _iwork_offset, double pivmin, double spdiam, int twist, intW info)
/* 2251:     */   {
/* 2252:1556 */     Dlarrb.dlarrb(n, d, _d_offset, lld, _lld_offset, ifirst, ilast, rtol1, rtol2, offset, w, _w_offset, wgap, _wgap_offset, werr, _werr_offset, work, _work_offset, iwork, _iwork_offset, pivmin, spdiam, twist, info);
/* 2253:     */   }
/* 2254:     */   
/* 2255:     */   public void dlarrc(String jobt, int n, double vl, double vu, double[] d, double[] e, double pivmin, intW eigcnt, intW lcnt, intW rcnt, intW info)
/* 2256:     */   {
/* 2257:1561 */     Dlarrc.dlarrc(jobt, n, vl, vu, d, 0, e, 0, pivmin, eigcnt, lcnt, rcnt, info);
/* 2258:     */   }
/* 2259:     */   
/* 2260:     */   public void dlarrc(String jobt, int n, double vl, double vu, double[] d, int _d_offset, double[] e, int _e_offset, double pivmin, intW eigcnt, intW lcnt, intW rcnt, intW info)
/* 2261:     */   {
/* 2262:1566 */     Dlarrc.dlarrc(jobt, n, vl, vu, d, _d_offset, e, _e_offset, pivmin, eigcnt, lcnt, rcnt, info);
/* 2263:     */   }
/* 2264:     */   
/* 2265:     */   public void dlarrd(String range, String order, int n, double vl, double vu, int il, int iu, double[] gers, double reltol, double[] d, double[] e, double[] e2, double pivmin, int nsplit, int[] isplit, intW m, double[] w, double[] werr, doubleW wl, doubleW wu, int[] iblock, int[] indexw, double[] work, int[] iwork, intW info)
/* 2266:     */   {
/* 2267:1571 */     Dlarrd.dlarrd(range, order, n, vl, vu, il, iu, gers, 0, reltol, d, 0, e, 0, e2, 0, pivmin, nsplit, isplit, 0, m, w, 0, werr, 0, wl, wu, iblock, 0, indexw, 0, work, 0, iwork, 0, info);
/* 2268:     */   }
/* 2269:     */   
/* 2270:     */   public void dlarrd(String range, String order, int n, double vl, double vu, int il, int iu, double[] gers, int _gers_offset, double reltol, double[] d, int _d_offset, double[] e, int _e_offset, double[] e2, int _e2_offset, double pivmin, int nsplit, int[] isplit, int _isplit_offset, intW m, double[] w, int _w_offset, double[] werr, int _werr_offset, doubleW wl, doubleW wu, int[] iblock, int _iblock_offset, int[] indexw, int _indexw_offset, double[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 2271:     */   {
/* 2272:1576 */     Dlarrd.dlarrd(range, order, n, vl, vu, il, iu, gers, _gers_offset, reltol, d, _d_offset, e, _e_offset, e2, _e2_offset, pivmin, nsplit, isplit, _isplit_offset, m, w, _w_offset, werr, _werr_offset, wl, wu, iblock, _iblock_offset, indexw, _indexw_offset, work, _work_offset, iwork, _iwork_offset, info);
/* 2273:     */   }
/* 2274:     */   
/* 2275:     */   public void dlarre(String range, int n, doubleW vl, doubleW vu, int il, int iu, double[] d, double[] e, double[] e2, double rtol1, double rtol2, double spltol, intW nsplit, int[] isplit, intW m, double[] w, double[] werr, double[] wgap, int[] iblock, int[] indexw, double[] gers, doubleW pivmin, double[] work, int[] iwork, intW info)
/* 2276:     */   {
/* 2277:1581 */     Dlarre.dlarre(range, n, vl, vu, il, iu, d, 0, e, 0, e2, 0, rtol1, rtol2, spltol, nsplit, isplit, 0, m, w, 0, werr, 0, wgap, 0, iblock, 0, indexw, 0, gers, 0, pivmin, work, 0, iwork, 0, info);
/* 2278:     */   }
/* 2279:     */   
/* 2280:     */   public void dlarre(String range, int n, doubleW vl, doubleW vu, int il, int iu, double[] d, int _d_offset, double[] e, int _e_offset, double[] e2, int _e2_offset, double rtol1, double rtol2, double spltol, intW nsplit, int[] isplit, int _isplit_offset, intW m, double[] w, int _w_offset, double[] werr, int _werr_offset, double[] wgap, int _wgap_offset, int[] iblock, int _iblock_offset, int[] indexw, int _indexw_offset, double[] gers, int _gers_offset, doubleW pivmin, double[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 2281:     */   {
/* 2282:1586 */     Dlarre.dlarre(range, n, vl, vu, il, iu, d, _d_offset, e, _e_offset, e2, _e2_offset, rtol1, rtol2, spltol, nsplit, isplit, _isplit_offset, m, w, _w_offset, werr, _werr_offset, wgap, _wgap_offset, iblock, _iblock_offset, indexw, _indexw_offset, gers, _gers_offset, pivmin, work, _work_offset, iwork, _iwork_offset, info);
/* 2283:     */   }
/* 2284:     */   
/* 2285:     */   public void dlarrf(int n, double[] d, double[] l, double[] ld, int clstrt, int clend, double[] w, double[] wgap, double[] werr, double spdiam, double clgapl, double clgapr, double pivmin, doubleW sigma, double[] dplus, double[] lplus, double[] work, intW info)
/* 2286:     */   {
/* 2287:1591 */     Dlarrf.dlarrf(n, d, 0, l, 0, ld, 0, clstrt, clend, w, 0, wgap, 0, werr, 0, spdiam, clgapl, clgapr, pivmin, sigma, dplus, 0, lplus, 0, work, 0, info);
/* 2288:     */   }
/* 2289:     */   
/* 2290:     */   public void dlarrf(int n, double[] d, int _d_offset, double[] l, int _l_offset, double[] ld, int _ld_offset, int clstrt, int clend, double[] w, int _w_offset, double[] wgap, int _wgap_offset, double[] werr, int _werr_offset, double spdiam, double clgapl, double clgapr, double pivmin, doubleW sigma, double[] dplus, int _dplus_offset, double[] lplus, int _lplus_offset, double[] work, int _work_offset, intW info)
/* 2291:     */   {
/* 2292:1596 */     Dlarrf.dlarrf(n, d, _d_offset, l, _l_offset, ld, _ld_offset, clstrt, clend, w, _w_offset, wgap, _wgap_offset, werr, _werr_offset, spdiam, clgapl, clgapr, pivmin, sigma, dplus, _dplus_offset, lplus, _lplus_offset, work, _work_offset, info);
/* 2293:     */   }
/* 2294:     */   
/* 2295:     */   public void dlarrj(int n, double[] d, double[] e2, int ifirst, int ilast, double rtol, int offset, double[] w, double[] werr, double[] work, int[] iwork, double pivmin, double spdiam, intW info)
/* 2296:     */   {
/* 2297:1601 */     Dlarrj.dlarrj(n, d, 0, e2, 0, ifirst, ilast, rtol, offset, w, 0, werr, 0, work, 0, iwork, 0, pivmin, spdiam, info);
/* 2298:     */   }
/* 2299:     */   
/* 2300:     */   public void dlarrj(int n, double[] d, int _d_offset, double[] e2, int _e2_offset, int ifirst, int ilast, double rtol, int offset, double[] w, int _w_offset, double[] werr, int _werr_offset, double[] work, int _work_offset, int[] iwork, int _iwork_offset, double pivmin, double spdiam, intW info)
/* 2301:     */   {
/* 2302:1606 */     Dlarrj.dlarrj(n, d, _d_offset, e2, _e2_offset, ifirst, ilast, rtol, offset, w, _w_offset, werr, _werr_offset, work, _work_offset, iwork, _iwork_offset, pivmin, spdiam, info);
/* 2303:     */   }
/* 2304:     */   
/* 2305:     */   public void dlarrk(int n, int iw, double gl, double gu, double[] d, double[] e2, double pivmin, double reltol, doubleW w, doubleW werr, intW info)
/* 2306:     */   {
/* 2307:1611 */     Dlarrk.dlarrk(n, iw, gl, gu, d, 0, e2, 0, pivmin, reltol, w, werr, info);
/* 2308:     */   }
/* 2309:     */   
/* 2310:     */   public void dlarrk(int n, int iw, double gl, double gu, double[] d, int _d_offset, double[] e2, int _e2_offset, double pivmin, double reltol, doubleW w, doubleW werr, intW info)
/* 2311:     */   {
/* 2312:1616 */     Dlarrk.dlarrk(n, iw, gl, gu, d, _d_offset, e2, _e2_offset, pivmin, reltol, w, werr, info);
/* 2313:     */   }
/* 2314:     */   
/* 2315:     */   public void dlarrr(int n, double[] d, double[] e, intW info)
/* 2316:     */   {
/* 2317:1621 */     Dlarrr.dlarrr(n, d, 0, e, 0, info);
/* 2318:     */   }
/* 2319:     */   
/* 2320:     */   public void dlarrr(int n, double[] d, int _d_offset, double[] e, int _e_offset, intW info)
/* 2321:     */   {
/* 2322:1626 */     Dlarrr.dlarrr(n, d, _d_offset, e, _e_offset, info);
/* 2323:     */   }
/* 2324:     */   
/* 2325:     */   public void dlarrv(int n, double vl, double vu, double[] d, double[] l, double pivmin, int[] isplit, int m, int dol, int dou, double minrgp, doubleW rtol1, doubleW rtol2, double[] w, double[] werr, double[] wgap, int[] iblock, int[] indexw, double[] gers, double[] z, int ldz, int[] isuppz, double[] work, int[] iwork, intW info)
/* 2326:     */   {
/* 2327:1631 */     Dlarrv.dlarrv(n, vl, vu, d, 0, l, 0, pivmin, isplit, 0, m, dol, dou, minrgp, rtol1, rtol2, w, 0, werr, 0, wgap, 0, iblock, 0, indexw, 0, gers, 0, z, 0, ldz, isuppz, 0, work, 0, iwork, 0, info);
/* 2328:     */   }
/* 2329:     */   
/* 2330:     */   public void dlarrv(int n, double vl, double vu, double[] d, int _d_offset, double[] l, int _l_offset, double pivmin, int[] isplit, int _isplit_offset, int m, int dol, int dou, double minrgp, doubleW rtol1, doubleW rtol2, double[] w, int _w_offset, double[] werr, int _werr_offset, double[] wgap, int _wgap_offset, int[] iblock, int _iblock_offset, int[] indexw, int _indexw_offset, double[] gers, int _gers_offset, double[] z, int _z_offset, int ldz, int[] isuppz, int _isuppz_offset, double[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 2331:     */   {
/* 2332:1636 */     Dlarrv.dlarrv(n, vl, vu, d, _d_offset, l, _l_offset, pivmin, isplit, _isplit_offset, m, dol, dou, minrgp, rtol1, rtol2, w, _w_offset, werr, _werr_offset, wgap, _wgap_offset, iblock, _iblock_offset, indexw, _indexw_offset, gers, _gers_offset, z, _z_offset, ldz, isuppz, _isuppz_offset, work, _work_offset, iwork, _iwork_offset, info);
/* 2333:     */   }
/* 2334:     */   
/* 2335:     */   public void dlartg(double f, double g, doubleW cs, doubleW sn, doubleW r)
/* 2336:     */   {
/* 2337:1641 */     Dlartg.dlartg(f, g, cs, sn, r);
/* 2338:     */   }
/* 2339:     */   
/* 2340:     */   public void dlartv(int n, double[] x, int incx, double[] y, int incy, double[] c, double[] s, int incc)
/* 2341:     */   {
/* 2342:1646 */     Dlartv.dlartv(n, x, 0, incx, y, 0, incy, c, 0, s, 0, incc);
/* 2343:     */   }
/* 2344:     */   
/* 2345:     */   public void dlartv(int n, double[] x, int _x_offset, int incx, double[] y, int _y_offset, int incy, double[] c, int _c_offset, double[] s, int _s_offset, int incc)
/* 2346:     */   {
/* 2347:1651 */     Dlartv.dlartv(n, x, _x_offset, incx, y, _y_offset, incy, c, _c_offset, s, _s_offset, incc);
/* 2348:     */   }
/* 2349:     */   
/* 2350:     */   public void dlaruv(int[] iseed, int n, double[] x)
/* 2351:     */   {
/* 2352:1656 */     Dlaruv.dlaruv(iseed, 0, n, x, 0);
/* 2353:     */   }
/* 2354:     */   
/* 2355:     */   public void dlaruv(int[] iseed, int _iseed_offset, int n, double[] x, int _x_offset)
/* 2356:     */   {
/* 2357:1661 */     Dlaruv.dlaruv(iseed, _iseed_offset, n, x, _x_offset);
/* 2358:     */   }
/* 2359:     */   
/* 2360:     */   public void dlarz(String side, int m, int n, int l, double[] v, int incv, double tau, double[] c, int Ldc, double[] work)
/* 2361:     */   {
/* 2362:1666 */     Dlarz.dlarz(side, m, n, l, v, 0, incv, tau, c, 0, Ldc, work, 0);
/* 2363:     */   }
/* 2364:     */   
/* 2365:     */   public void dlarz(String side, int m, int n, int l, double[] v, int _v_offset, int incv, double tau, double[] c, int _c_offset, int Ldc, double[] work, int _work_offset)
/* 2366:     */   {
/* 2367:1671 */     Dlarz.dlarz(side, m, n, l, v, _v_offset, incv, tau, c, _c_offset, Ldc, work, _work_offset);
/* 2368:     */   }
/* 2369:     */   
/* 2370:     */   public void dlarzb(String side, String trans, String direct, String storev, int m, int n, int k, int l, double[] v, int ldv, double[] t, int ldt, double[] c, int Ldc, double[] work, int ldwork)
/* 2371:     */   {
/* 2372:1676 */     Dlarzb.dlarzb(side, trans, direct, storev, m, n, k, l, v, 0, ldv, t, 0, ldt, c, 0, Ldc, work, 0, ldwork);
/* 2373:     */   }
/* 2374:     */   
/* 2375:     */   public void dlarzb(String side, String trans, String direct, String storev, int m, int n, int k, int l, double[] v, int _v_offset, int ldv, double[] t, int _t_offset, int ldt, double[] c, int _c_offset, int Ldc, double[] work, int _work_offset, int ldwork)
/* 2376:     */   {
/* 2377:1681 */     Dlarzb.dlarzb(side, trans, direct, storev, m, n, k, l, v, _v_offset, ldv, t, _t_offset, ldt, c, _c_offset, Ldc, work, _work_offset, ldwork);
/* 2378:     */   }
/* 2379:     */   
/* 2380:     */   public void dlarzt(String direct, String storev, int n, int k, double[] v, int ldv, double[] tau, double[] t, int ldt)
/* 2381:     */   {
/* 2382:1686 */     Dlarzt.dlarzt(direct, storev, n, k, v, 0, ldv, tau, 0, t, 0, ldt);
/* 2383:     */   }
/* 2384:     */   
/* 2385:     */   public void dlarzt(String direct, String storev, int n, int k, double[] v, int _v_offset, int ldv, double[] tau, int _tau_offset, double[] t, int _t_offset, int ldt)
/* 2386:     */   {
/* 2387:1691 */     Dlarzt.dlarzt(direct, storev, n, k, v, _v_offset, ldv, tau, _tau_offset, t, _t_offset, ldt);
/* 2388:     */   }
/* 2389:     */   
/* 2390:     */   public void dlas2(double f, double g, double h, doubleW ssmin, doubleW ssmax)
/* 2391:     */   {
/* 2392:1696 */     Dlas2.dlas2(f, g, h, ssmin, ssmax);
/* 2393:     */   }
/* 2394:     */   
/* 2395:     */   public void dlascl(String type, int kl, int ku, double cfrom, double cto, int m, int n, double[] a, int lda, intW info)
/* 2396:     */   {
/* 2397:1701 */     Dlascl.dlascl(type, kl, ku, cfrom, cto, m, n, a, 0, lda, info);
/* 2398:     */   }
/* 2399:     */   
/* 2400:     */   public void dlascl(String type, int kl, int ku, double cfrom, double cto, int m, int n, double[] a, int _a_offset, int lda, intW info)
/* 2401:     */   {
/* 2402:1706 */     Dlascl.dlascl(type, kl, ku, cfrom, cto, m, n, a, _a_offset, lda, info);
/* 2403:     */   }
/* 2404:     */   
/* 2405:     */   public void dlasd0(int n, int sqre, double[] d, double[] e, double[] u, int ldu, double[] vt, int ldvt, int smlsiz, int[] iwork, double[] work, intW info)
/* 2406:     */   {
/* 2407:1711 */     Dlasd0.dlasd0(n, sqre, d, 0, e, 0, u, 0, ldu, vt, 0, ldvt, smlsiz, iwork, 0, work, 0, info);
/* 2408:     */   }
/* 2409:     */   
/* 2410:     */   public void dlasd0(int n, int sqre, double[] d, int _d_offset, double[] e, int _e_offset, double[] u, int _u_offset, int ldu, double[] vt, int _vt_offset, int ldvt, int smlsiz, int[] iwork, int _iwork_offset, double[] work, int _work_offset, intW info)
/* 2411:     */   {
/* 2412:1716 */     Dlasd0.dlasd0(n, sqre, d, _d_offset, e, _e_offset, u, _u_offset, ldu, vt, _vt_offset, ldvt, smlsiz, iwork, _iwork_offset, work, _work_offset, info);
/* 2413:     */   }
/* 2414:     */   
/* 2415:     */   public void dlasd1(int nl, int nr, int sqre, double[] d, doubleW alpha, doubleW beta, double[] u, int ldu, double[] vt, int ldvt, int[] idxq, int[] iwork, double[] work, intW info)
/* 2416:     */   {
/* 2417:1721 */     Dlasd1.dlasd1(nl, nr, sqre, d, 0, alpha, beta, u, 0, ldu, vt, 0, ldvt, idxq, 0, iwork, 0, work, 0, info);
/* 2418:     */   }
/* 2419:     */   
/* 2420:     */   public void dlasd1(int nl, int nr, int sqre, double[] d, int _d_offset, doubleW alpha, doubleW beta, double[] u, int _u_offset, int ldu, double[] vt, int _vt_offset, int ldvt, int[] idxq, int _idxq_offset, int[] iwork, int _iwork_offset, double[] work, int _work_offset, intW info)
/* 2421:     */   {
/* 2422:1726 */     Dlasd1.dlasd1(nl, nr, sqre, d, _d_offset, alpha, beta, u, _u_offset, ldu, vt, _vt_offset, ldvt, idxq, _idxq_offset, iwork, _iwork_offset, work, _work_offset, info);
/* 2423:     */   }
/* 2424:     */   
/* 2425:     */   public void dlasd2(int nl, int nr, int sqre, intW k, double[] d, double[] z, double alpha, double beta, double[] u, int ldu, double[] vt, int ldvt, double[] dsigma, double[] u2, int ldu2, double[] vt2, int ldvt2, int[] idxp, int[] idx, int[] idxc, int[] idxq, int[] coltyp, intW info)
/* 2426:     */   {
/* 2427:1731 */     Dlasd2.dlasd2(nl, nr, sqre, k, d, 0, z, 0, alpha, beta, u, 0, ldu, vt, 0, ldvt, dsigma, 0, u2, 0, ldu2, vt2, 0, ldvt2, idxp, 0, idx, 0, idxc, 0, idxq, 0, coltyp, 0, info);
/* 2428:     */   }
/* 2429:     */   
/* 2430:     */   public void dlasd2(int nl, int nr, int sqre, intW k, double[] d, int _d_offset, double[] z, int _z_offset, double alpha, double beta, double[] u, int _u_offset, int ldu, double[] vt, int _vt_offset, int ldvt, double[] dsigma, int _dsigma_offset, double[] u2, int _u2_offset, int ldu2, double[] vt2, int _vt2_offset, int ldvt2, int[] idxp, int _idxp_offset, int[] idx, int _idx_offset, int[] idxc, int _idxc_offset, int[] idxq, int _idxq_offset, int[] coltyp, int _coltyp_offset, intW info)
/* 2431:     */   {
/* 2432:1736 */     Dlasd2.dlasd2(nl, nr, sqre, k, d, _d_offset, z, _z_offset, alpha, beta, u, _u_offset, ldu, vt, _vt_offset, ldvt, dsigma, _dsigma_offset, u2, _u2_offset, ldu2, vt2, _vt2_offset, ldvt2, idxp, _idxp_offset, idx, _idx_offset, idxc, _idxc_offset, idxq, _idxq_offset, coltyp, _coltyp_offset, info);
/* 2433:     */   }
/* 2434:     */   
/* 2435:     */   public void dlasd3(int nl, int nr, int sqre, int k, double[] d, double[] q, int ldq, double[] dsigma, double[] u, int ldu, double[] u2, int ldu2, double[] vt, int ldvt, double[] vt2, int ldvt2, int[] idxc, int[] ctot, double[] z, intW info)
/* 2436:     */   {
/* 2437:1741 */     Dlasd3.dlasd3(nl, nr, sqre, k, d, 0, q, 0, ldq, dsigma, 0, u, 0, ldu, u2, 0, ldu2, vt, 0, ldvt, vt2, 0, ldvt2, idxc, 0, ctot, 0, z, 0, info);
/* 2438:     */   }
/* 2439:     */   
/* 2440:     */   public void dlasd3(int nl, int nr, int sqre, int k, double[] d, int _d_offset, double[] q, int _q_offset, int ldq, double[] dsigma, int _dsigma_offset, double[] u, int _u_offset, int ldu, double[] u2, int _u2_offset, int ldu2, double[] vt, int _vt_offset, int ldvt, double[] vt2, int _vt2_offset, int ldvt2, int[] idxc, int _idxc_offset, int[] ctot, int _ctot_offset, double[] z, int _z_offset, intW info)
/* 2441:     */   {
/* 2442:1746 */     Dlasd3.dlasd3(nl, nr, sqre, k, d, _d_offset, q, _q_offset, ldq, dsigma, _dsigma_offset, u, _u_offset, ldu, u2, _u2_offset, ldu2, vt, _vt_offset, ldvt, vt2, _vt2_offset, ldvt2, idxc, _idxc_offset, ctot, _ctot_offset, z, _z_offset, info);
/* 2443:     */   }
/* 2444:     */   
/* 2445:     */   public void dlasd4(int n, int i, double[] d, double[] z, double[] delta, double rho, doubleW sigma, double[] work, intW info)
/* 2446:     */   {
/* 2447:1751 */     Dlasd4.dlasd4(n, i, d, 0, z, 0, delta, 0, rho, sigma, work, 0, info);
/* 2448:     */   }
/* 2449:     */   
/* 2450:     */   public void dlasd4(int n, int i, double[] d, int _d_offset, double[] z, int _z_offset, double[] delta, int _delta_offset, double rho, doubleW sigma, double[] work, int _work_offset, intW info)
/* 2451:     */   {
/* 2452:1756 */     Dlasd4.dlasd4(n, i, d, _d_offset, z, _z_offset, delta, _delta_offset, rho, sigma, work, _work_offset, info);
/* 2453:     */   }
/* 2454:     */   
/* 2455:     */   public void dlasd5(int i, double[] d, double[] z, double[] delta, double rho, doubleW dsigma, double[] work)
/* 2456:     */   {
/* 2457:1761 */     Dlasd5.dlasd5(i, d, 0, z, 0, delta, 0, rho, dsigma, work, 0);
/* 2458:     */   }
/* 2459:     */   
/* 2460:     */   public void dlasd5(int i, double[] d, int _d_offset, double[] z, int _z_offset, double[] delta, int _delta_offset, double rho, doubleW dsigma, double[] work, int _work_offset)
/* 2461:     */   {
/* 2462:1766 */     Dlasd5.dlasd5(i, d, _d_offset, z, _z_offset, delta, _delta_offset, rho, dsigma, work, _work_offset);
/* 2463:     */   }
/* 2464:     */   
/* 2465:     */   public void dlasd6(int icompq, int nl, int nr, int sqre, double[] d, double[] vf, double[] vl, doubleW alpha, doubleW beta, int[] idxq, int[] perm, intW givptr, int[] givcol, int ldgcol, double[] givnum, int ldgnum, double[] poles, double[] difl, double[] difr, double[] z, intW k, doubleW c, doubleW s, double[] work, int[] iwork, intW info)
/* 2466:     */   {
/* 2467:1771 */     Dlasd6.dlasd6(icompq, nl, nr, sqre, d, 0, vf, 0, vl, 0, alpha, beta, idxq, 0, perm, 0, givptr, givcol, 0, ldgcol, givnum, 0, ldgnum, poles, 0, difl, 0, difr, 0, z, 0, k, c, s, work, 0, iwork, 0, info);
/* 2468:     */   }
/* 2469:     */   
/* 2470:     */   public void dlasd6(int icompq, int nl, int nr, int sqre, double[] d, int _d_offset, double[] vf, int _vf_offset, double[] vl, int _vl_offset, doubleW alpha, doubleW beta, int[] idxq, int _idxq_offset, int[] perm, int _perm_offset, intW givptr, int[] givcol, int _givcol_offset, int ldgcol, double[] givnum, int _givnum_offset, int ldgnum, double[] poles, int _poles_offset, double[] difl, int _difl_offset, double[] difr, int _difr_offset, double[] z, int _z_offset, intW k, doubleW c, doubleW s, double[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 2471:     */   {
/* 2472:1776 */     Dlasd6.dlasd6(icompq, nl, nr, sqre, d, _d_offset, vf, _vf_offset, vl, _vl_offset, alpha, beta, idxq, _idxq_offset, perm, _perm_offset, givptr, givcol, _givcol_offset, ldgcol, givnum, _givnum_offset, ldgnum, poles, _poles_offset, difl, _difl_offset, difr, _difr_offset, z, _z_offset, k, c, s, work, _work_offset, iwork, _iwork_offset, info);
/* 2473:     */   }
/* 2474:     */   
/* 2475:     */   public void dlasd7(int icompq, int nl, int nr, int sqre, intW k, double[] d, double[] z, double[] zw, double[] vf, double[] vfw, double[] vl, double[] vlw, double alpha, double beta, double[] dsigma, int[] idx, int[] idxp, int[] idxq, int[] perm, intW givptr, int[] givcol, int ldgcol, double[] givnum, int ldgnum, doubleW c, doubleW s, intW info)
/* 2476:     */   {
/* 2477:1781 */     Dlasd7.dlasd7(icompq, nl, nr, sqre, k, d, 0, z, 0, zw, 0, vf, 0, vfw, 0, vl, 0, vlw, 0, alpha, beta, dsigma, 0, idx, 0, idxp, 0, idxq, 0, perm, 0, givptr, givcol, 0, ldgcol, givnum, 0, ldgnum, c, s, info);
/* 2478:     */   }
/* 2479:     */   
/* 2480:     */   public void dlasd7(int icompq, int nl, int nr, int sqre, intW k, double[] d, int _d_offset, double[] z, int _z_offset, double[] zw, int _zw_offset, double[] vf, int _vf_offset, double[] vfw, int _vfw_offset, double[] vl, int _vl_offset, double[] vlw, int _vlw_offset, double alpha, double beta, double[] dsigma, int _dsigma_offset, int[] idx, int _idx_offset, int[] idxp, int _idxp_offset, int[] idxq, int _idxq_offset, int[] perm, int _perm_offset, intW givptr, int[] givcol, int _givcol_offset, int ldgcol, double[] givnum, int _givnum_offset, int ldgnum, doubleW c, doubleW s, intW info)
/* 2481:     */   {
/* 2482:1786 */     Dlasd7.dlasd7(icompq, nl, nr, sqre, k, d, _d_offset, z, _z_offset, zw, _zw_offset, vf, _vf_offset, vfw, _vfw_offset, vl, _vl_offset, vlw, _vlw_offset, alpha, beta, dsigma, _dsigma_offset, idx, _idx_offset, idxp, _idxp_offset, idxq, _idxq_offset, perm, _perm_offset, givptr, givcol, _givcol_offset, ldgcol, givnum, _givnum_offset, ldgnum, c, s, info);
/* 2483:     */   }
/* 2484:     */   
/* 2485:     */   public void dlasd8(int icompq, int k, double[] d, double[] z, double[] vf, double[] vl, double[] difl, double[] difr, int lddifr, double[] dsigma, double[] work, intW info)
/* 2486:     */   {
/* 2487:1791 */     Dlasd8.dlasd8(icompq, k, d, 0, z, 0, vf, 0, vl, 0, difl, 0, difr, 0, lddifr, dsigma, 0, work, 0, info);
/* 2488:     */   }
/* 2489:     */   
/* 2490:     */   public void dlasd8(int icompq, int k, double[] d, int _d_offset, double[] z, int _z_offset, double[] vf, int _vf_offset, double[] vl, int _vl_offset, double[] difl, int _difl_offset, double[] difr, int _difr_offset, int lddifr, double[] dsigma, int _dsigma_offset, double[] work, int _work_offset, intW info)
/* 2491:     */   {
/* 2492:1796 */     Dlasd8.dlasd8(icompq, k, d, _d_offset, z, _z_offset, vf, _vf_offset, vl, _vl_offset, difl, _difl_offset, difr, _difr_offset, lddifr, dsigma, _dsigma_offset, work, _work_offset, info);
/* 2493:     */   }
/* 2494:     */   
/* 2495:     */   public void dlasda(int icompq, int smlsiz, int n, int sqre, double[] d, double[] e, double[] u, int ldu, double[] vt, int[] k, double[] difl, double[] difr, double[] z, double[] poles, int[] givptr, int[] givcol, int ldgcol, int[] perm, double[] givnum, double[] c, double[] s, double[] work, int[] iwork, intW info)
/* 2496:     */   {
/* 2497:1801 */     Dlasda.dlasda(icompq, smlsiz, n, sqre, d, 0, e, 0, u, 0, ldu, vt, 0, k, 0, difl, 0, difr, 0, z, 0, poles, 0, givptr, 0, givcol, 0, ldgcol, perm, 0, givnum, 0, c, 0, s, 0, work, 0, iwork, 0, info);
/* 2498:     */   }
/* 2499:     */   
/* 2500:     */   public void dlasda(int icompq, int smlsiz, int n, int sqre, double[] d, int _d_offset, double[] e, int _e_offset, double[] u, int _u_offset, int ldu, double[] vt, int _vt_offset, int[] k, int _k_offset, double[] difl, int _difl_offset, double[] difr, int _difr_offset, double[] z, int _z_offset, double[] poles, int _poles_offset, int[] givptr, int _givptr_offset, int[] givcol, int _givcol_offset, int ldgcol, int[] perm, int _perm_offset, double[] givnum, int _givnum_offset, double[] c, int _c_offset, double[] s, int _s_offset, double[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 2501:     */   {
/* 2502:1806 */     Dlasda.dlasda(icompq, smlsiz, n, sqre, d, _d_offset, e, _e_offset, u, _u_offset, ldu, vt, _vt_offset, k, _k_offset, difl, _difl_offset, difr, _difr_offset, z, _z_offset, poles, _poles_offset, givptr, _givptr_offset, givcol, _givcol_offset, ldgcol, perm, _perm_offset, givnum, _givnum_offset, c, _c_offset, s, _s_offset, work, _work_offset, iwork, _iwork_offset, info);
/* 2503:     */   }
/* 2504:     */   
/* 2505:     */   public void dlasdq(String uplo, int sqre, int n, int ncvt, int nru, int ncc, double[] d, double[] e, double[] vt, int ldvt, double[] u, int ldu, double[] c, int Ldc, double[] work, intW info)
/* 2506:     */   {
/* 2507:1811 */     Dlasdq.dlasdq(uplo, sqre, n, ncvt, nru, ncc, d, 0, e, 0, vt, 0, ldvt, u, 0, ldu, c, 0, Ldc, work, 0, info);
/* 2508:     */   }
/* 2509:     */   
/* 2510:     */   public void dlasdq(String uplo, int sqre, int n, int ncvt, int nru, int ncc, double[] d, int _d_offset, double[] e, int _e_offset, double[] vt, int _vt_offset, int ldvt, double[] u, int _u_offset, int ldu, double[] c, int _c_offset, int Ldc, double[] work, int _work_offset, intW info)
/* 2511:     */   {
/* 2512:1816 */     Dlasdq.dlasdq(uplo, sqre, n, ncvt, nru, ncc, d, _d_offset, e, _e_offset, vt, _vt_offset, ldvt, u, _u_offset, ldu, c, _c_offset, Ldc, work, _work_offset, info);
/* 2513:     */   }
/* 2514:     */   
/* 2515:     */   public void dlasdt(int n, intW lvl, intW nd, int[] inode, int[] ndiml, int[] ndimr, int msub)
/* 2516:     */   {
/* 2517:1821 */     Dlasdt.dlasdt(n, lvl, nd, inode, 0, ndiml, 0, ndimr, 0, msub);
/* 2518:     */   }
/* 2519:     */   
/* 2520:     */   public void dlasdt(int n, intW lvl, intW nd, int[] inode, int _inode_offset, int[] ndiml, int _ndiml_offset, int[] ndimr, int _ndimr_offset, int msub)
/* 2521:     */   {
/* 2522:1826 */     Dlasdt.dlasdt(n, lvl, nd, inode, _inode_offset, ndiml, _ndiml_offset, ndimr, _ndimr_offset, msub);
/* 2523:     */   }
/* 2524:     */   
/* 2525:     */   public void dlaset(String uplo, int m, int n, double alpha, double beta, double[] a, int lda)
/* 2526:     */   {
/* 2527:1831 */     Dlaset.dlaset(uplo, m, n, alpha, beta, a, 0, lda);
/* 2528:     */   }
/* 2529:     */   
/* 2530:     */   public void dlaset(String uplo, int m, int n, double alpha, double beta, double[] a, int _a_offset, int lda)
/* 2531:     */   {
/* 2532:1836 */     Dlaset.dlaset(uplo, m, n, alpha, beta, a, _a_offset, lda);
/* 2533:     */   }
/* 2534:     */   
/* 2535:     */   public void dlasq1(int n, double[] d, double[] e, double[] work, intW info)
/* 2536:     */   {
/* 2537:1841 */     Dlasq1.dlasq1(n, d, 0, e, 0, work, 0, info);
/* 2538:     */   }
/* 2539:     */   
/* 2540:     */   public void dlasq1(int n, double[] d, int _d_offset, double[] e, int _e_offset, double[] work, int _work_offset, intW info)
/* 2541:     */   {
/* 2542:1846 */     Dlasq1.dlasq1(n, d, _d_offset, e, _e_offset, work, _work_offset, info);
/* 2543:     */   }
/* 2544:     */   
/* 2545:     */   public void dlasq2(int n, double[] z, intW info)
/* 2546:     */   {
/* 2547:1851 */     Dlasq2.dlasq2(n, z, 0, info);
/* 2548:     */   }
/* 2549:     */   
/* 2550:     */   public void dlasq2(int n, double[] z, int _z_offset, intW info)
/* 2551:     */   {
/* 2552:1856 */     Dlasq2.dlasq2(n, z, _z_offset, info);
/* 2553:     */   }
/* 2554:     */   
/* 2555:     */   public void dlasq3(int i0, intW n0, double[] z, int pp, doubleW dmin, doubleW sigma, doubleW desig, doubleW qmax, intW nfail, intW iter, intW ndiv, boolean ieee)
/* 2556:     */   {
/* 2557:1861 */     Dlasq3.dlasq3(i0, n0, z, 0, pp, dmin, sigma, desig, qmax, nfail, iter, ndiv, ieee);
/* 2558:     */   }
/* 2559:     */   
/* 2560:     */   public void dlasq3(int i0, intW n0, double[] z, int _z_offset, int pp, doubleW dmin, doubleW sigma, doubleW desig, doubleW qmax, intW nfail, intW iter, intW ndiv, boolean ieee)
/* 2561:     */   {
/* 2562:1866 */     Dlasq3.dlasq3(i0, n0, z, _z_offset, pp, dmin, sigma, desig, qmax, nfail, iter, ndiv, ieee);
/* 2563:     */   }
/* 2564:     */   
/* 2565:     */   public void dlasq4(int i0, int n0, double[] z, int pp, int n0in, double dmin, double dmin1, double dmin2, double dn, double dn1, double dn2, doubleW tau, intW ttype)
/* 2566:     */   {
/* 2567:1871 */     Dlasq4.dlasq4(i0, n0, z, 0, pp, n0in, dmin, dmin1, dmin2, dn, dn1, dn2, tau, ttype);
/* 2568:     */   }
/* 2569:     */   
/* 2570:     */   public void dlasq4(int i0, int n0, double[] z, int _z_offset, int pp, int n0in, double dmin, double dmin1, double dmin2, double dn, double dn1, double dn2, doubleW tau, intW ttype)
/* 2571:     */   {
/* 2572:1876 */     Dlasq4.dlasq4(i0, n0, z, _z_offset, pp, n0in, dmin, dmin1, dmin2, dn, dn1, dn2, tau, ttype);
/* 2573:     */   }
/* 2574:     */   
/* 2575:     */   public void dlasq5(int i0, int n0, double[] z, int pp, double tau, doubleW dmin, doubleW dmin1, doubleW dmin2, doubleW dn, doubleW dnm1, doubleW dnm2, boolean ieee)
/* 2576:     */   {
/* 2577:1881 */     Dlasq5.dlasq5(i0, n0, z, 0, pp, tau, dmin, dmin1, dmin2, dn, dnm1, dnm2, ieee);
/* 2578:     */   }
/* 2579:     */   
/* 2580:     */   public void dlasq5(int i0, int n0, double[] z, int _z_offset, int pp, double tau, doubleW dmin, doubleW dmin1, doubleW dmin2, doubleW dn, doubleW dnm1, doubleW dnm2, boolean ieee)
/* 2581:     */   {
/* 2582:1886 */     Dlasq5.dlasq5(i0, n0, z, _z_offset, pp, tau, dmin, dmin1, dmin2, dn, dnm1, dnm2, ieee);
/* 2583:     */   }
/* 2584:     */   
/* 2585:     */   public void dlasq6(int i0, int n0, double[] z, int pp, doubleW dmin, doubleW dmin1, doubleW dmin2, doubleW dn, doubleW dnm1, doubleW dnm2)
/* 2586:     */   {
/* 2587:1891 */     Dlasq6.dlasq6(i0, n0, z, 0, pp, dmin, dmin1, dmin2, dn, dnm1, dnm2);
/* 2588:     */   }
/* 2589:     */   
/* 2590:     */   public void dlasq6(int i0, int n0, double[] z, int _z_offset, int pp, doubleW dmin, doubleW dmin1, doubleW dmin2, doubleW dn, doubleW dnm1, doubleW dnm2)
/* 2591:     */   {
/* 2592:1896 */     Dlasq6.dlasq6(i0, n0, z, _z_offset, pp, dmin, dmin1, dmin2, dn, dnm1, dnm2);
/* 2593:     */   }
/* 2594:     */   
/* 2595:     */   public void dlasr(String side, String pivot, String direct, int m, int n, double[] c, double[] s, double[] a, int lda)
/* 2596:     */   {
/* 2597:1901 */     Dlasr.dlasr(side, pivot, direct, m, n, c, 0, s, 0, a, 0, lda);
/* 2598:     */   }
/* 2599:     */   
/* 2600:     */   public void dlasr(String side, String pivot, String direct, int m, int n, double[] c, int _c_offset, double[] s, int _s_offset, double[] a, int _a_offset, int lda)
/* 2601:     */   {
/* 2602:1906 */     Dlasr.dlasr(side, pivot, direct, m, n, c, _c_offset, s, _s_offset, a, _a_offset, lda);
/* 2603:     */   }
/* 2604:     */   
/* 2605:     */   public void dlasrt(String id, int n, double[] d, intW info)
/* 2606:     */   {
/* 2607:1911 */     Dlasrt.dlasrt(id, n, d, 0, info);
/* 2608:     */   }
/* 2609:     */   
/* 2610:     */   public void dlasrt(String id, int n, double[] d, int _d_offset, intW info)
/* 2611:     */   {
/* 2612:1916 */     Dlasrt.dlasrt(id, n, d, _d_offset, info);
/* 2613:     */   }
/* 2614:     */   
/* 2615:     */   public void dlassq(int n, double[] x, int incx, doubleW scale, doubleW sumsq)
/* 2616:     */   {
/* 2617:1921 */     Dlassq.dlassq(n, x, 0, incx, scale, sumsq);
/* 2618:     */   }
/* 2619:     */   
/* 2620:     */   public void dlassq(int n, double[] x, int _x_offset, int incx, doubleW scale, doubleW sumsq)
/* 2621:     */   {
/* 2622:1926 */     Dlassq.dlassq(n, x, _x_offset, incx, scale, sumsq);
/* 2623:     */   }
/* 2624:     */   
/* 2625:     */   public void dlasv2(double f, double g, double h, doubleW ssmin, doubleW ssmax, doubleW snr, doubleW csr, doubleW snl, doubleW csl)
/* 2626:     */   {
/* 2627:1931 */     Dlasv2.dlasv2(f, g, h, ssmin, ssmax, snr, csr, snl, csl);
/* 2628:     */   }
/* 2629:     */   
/* 2630:     */   public void dlaswp(int n, double[] a, int lda, int k1, int k2, int[] ipiv, int incx)
/* 2631:     */   {
/* 2632:1936 */     Dlaswp.dlaswp(n, a, 0, lda, k1, k2, ipiv, 0, incx);
/* 2633:     */   }
/* 2634:     */   
/* 2635:     */   public void dlaswp(int n, double[] a, int _a_offset, int lda, int k1, int k2, int[] ipiv, int _ipiv_offset, int incx)
/* 2636:     */   {
/* 2637:1941 */     Dlaswp.dlaswp(n, a, _a_offset, lda, k1, k2, ipiv, _ipiv_offset, incx);
/* 2638:     */   }
/* 2639:     */   
/* 2640:     */   public void dlasy2(boolean ltranl, boolean ltranr, int isgn, int n1, int n2, double[] tl, int ldtl, double[] tr, int ldtr, double[] b, int ldb, doubleW scale, double[] x, int ldx, doubleW xnorm, intW info)
/* 2641:     */   {
/* 2642:1946 */     Dlasy2.dlasy2(ltranl, ltranr, isgn, n1, n2, tl, 0, ldtl, tr, 0, ldtr, b, 0, ldb, scale, x, 0, ldx, xnorm, info);
/* 2643:     */   }
/* 2644:     */   
/* 2645:     */   public void dlasy2(boolean ltranl, boolean ltranr, int isgn, int n1, int n2, double[] tl, int _tl_offset, int ldtl, double[] tr, int _tr_offset, int ldtr, double[] b, int _b_offset, int ldb, doubleW scale, double[] x, int _x_offset, int ldx, doubleW xnorm, intW info)
/* 2646:     */   {
/* 2647:1951 */     Dlasy2.dlasy2(ltranl, ltranr, isgn, n1, n2, tl, _tl_offset, ldtl, tr, _tr_offset, ldtr, b, _b_offset, ldb, scale, x, _x_offset, ldx, xnorm, info);
/* 2648:     */   }
/* 2649:     */   
/* 2650:     */   public void dlasyf(String uplo, int n, int nb, intW kb, double[] a, int lda, int[] ipiv, double[] w, int ldw, intW info)
/* 2651:     */   {
/* 2652:1956 */     Dlasyf.dlasyf(uplo, n, nb, kb, a, 0, lda, ipiv, 0, w, 0, ldw, info);
/* 2653:     */   }
/* 2654:     */   
/* 2655:     */   public void dlasyf(String uplo, int n, int nb, intW kb, double[] a, int _a_offset, int lda, int[] ipiv, int _ipiv_offset, double[] w, int _w_offset, int ldw, intW info)
/* 2656:     */   {
/* 2657:1961 */     Dlasyf.dlasyf(uplo, n, nb, kb, a, _a_offset, lda, ipiv, _ipiv_offset, w, _w_offset, ldw, info);
/* 2658:     */   }
/* 2659:     */   
/* 2660:     */   public void dlatbs(String uplo, String trans, String diag, String normin, int n, int kd, double[] ab, int ldab, double[] x, doubleW scale, double[] cnorm, intW info)
/* 2661:     */   {
/* 2662:1966 */     Dlatbs.dlatbs(uplo, trans, diag, normin, n, kd, ab, 0, ldab, x, 0, scale, cnorm, 0, info);
/* 2663:     */   }
/* 2664:     */   
/* 2665:     */   public void dlatbs(String uplo, String trans, String diag, String normin, int n, int kd, double[] ab, int _ab_offset, int ldab, double[] x, int _x_offset, doubleW scale, double[] cnorm, int _cnorm_offset, intW info)
/* 2666:     */   {
/* 2667:1971 */     Dlatbs.dlatbs(uplo, trans, diag, normin, n, kd, ab, _ab_offset, ldab, x, _x_offset, scale, cnorm, _cnorm_offset, info);
/* 2668:     */   }
/* 2669:     */   
/* 2670:     */   public void dlatdf(int ijob, int n, double[] z, int ldz, double[] rhs, doubleW rdsum, doubleW rdscal, int[] ipiv, int[] jpiv)
/* 2671:     */   {
/* 2672:1976 */     Dlatdf.dlatdf(ijob, n, z, 0, ldz, rhs, 0, rdsum, rdscal, ipiv, 0, jpiv, 0);
/* 2673:     */   }
/* 2674:     */   
/* 2675:     */   public void dlatdf(int ijob, int n, double[] z, int _z_offset, int ldz, double[] rhs, int _rhs_offset, doubleW rdsum, doubleW rdscal, int[] ipiv, int _ipiv_offset, int[] jpiv, int _jpiv_offset)
/* 2676:     */   {
/* 2677:1981 */     Dlatdf.dlatdf(ijob, n, z, _z_offset, ldz, rhs, _rhs_offset, rdsum, rdscal, ipiv, _ipiv_offset, jpiv, _jpiv_offset);
/* 2678:     */   }
/* 2679:     */   
/* 2680:     */   public void dlatps(String uplo, String trans, String diag, String normin, int n, double[] ap, double[] x, doubleW scale, double[] cnorm, intW info)
/* 2681:     */   {
/* 2682:1986 */     Dlatps.dlatps(uplo, trans, diag, normin, n, ap, 0, x, 0, scale, cnorm, 0, info);
/* 2683:     */   }
/* 2684:     */   
/* 2685:     */   public void dlatps(String uplo, String trans, String diag, String normin, int n, double[] ap, int _ap_offset, double[] x, int _x_offset, doubleW scale, double[] cnorm, int _cnorm_offset, intW info)
/* 2686:     */   {
/* 2687:1991 */     Dlatps.dlatps(uplo, trans, diag, normin, n, ap, _ap_offset, x, _x_offset, scale, cnorm, _cnorm_offset, info);
/* 2688:     */   }
/* 2689:     */   
/* 2690:     */   public void dlatrd(String uplo, int n, int nb, double[] a, int lda, double[] e, double[] tau, double[] w, int ldw)
/* 2691:     */   {
/* 2692:1996 */     Dlatrd.dlatrd(uplo, n, nb, a, 0, lda, e, 0, tau, 0, w, 0, ldw);
/* 2693:     */   }
/* 2694:     */   
/* 2695:     */   public void dlatrd(String uplo, int n, int nb, double[] a, int _a_offset, int lda, double[] e, int _e_offset, double[] tau, int _tau_offset, double[] w, int _w_offset, int ldw)
/* 2696:     */   {
/* 2697:2001 */     Dlatrd.dlatrd(uplo, n, nb, a, _a_offset, lda, e, _e_offset, tau, _tau_offset, w, _w_offset, ldw);
/* 2698:     */   }
/* 2699:     */   
/* 2700:     */   public void dlatrs(String uplo, String trans, String diag, String normin, int n, double[] a, int lda, double[] x, doubleW scale, double[] cnorm, intW info)
/* 2701:     */   {
/* 2702:2006 */     Dlatrs.dlatrs(uplo, trans, diag, normin, n, a, 0, lda, x, 0, scale, cnorm, 0, info);
/* 2703:     */   }
/* 2704:     */   
/* 2705:     */   public void dlatrs(String uplo, String trans, String diag, String normin, int n, double[] a, int _a_offset, int lda, double[] x, int _x_offset, doubleW scale, double[] cnorm, int _cnorm_offset, intW info)
/* 2706:     */   {
/* 2707:2011 */     Dlatrs.dlatrs(uplo, trans, diag, normin, n, a, _a_offset, lda, x, _x_offset, scale, cnorm, _cnorm_offset, info);
/* 2708:     */   }
/* 2709:     */   
/* 2710:     */   public void dlatrz(int m, int n, int l, double[] a, int lda, double[] tau, double[] work)
/* 2711:     */   {
/* 2712:2016 */     Dlatrz.dlatrz(m, n, l, a, 0, lda, tau, 0, work, 0);
/* 2713:     */   }
/* 2714:     */   
/* 2715:     */   public void dlatrz(int m, int n, int l, double[] a, int _a_offset, int lda, double[] tau, int _tau_offset, double[] work, int _work_offset)
/* 2716:     */   {
/* 2717:2021 */     Dlatrz.dlatrz(m, n, l, a, _a_offset, lda, tau, _tau_offset, work, _work_offset);
/* 2718:     */   }
/* 2719:     */   
/* 2720:     */   public void dlatzm(String side, int m, int n, double[] v, int incv, double tau, double[] c1, double[] c2, int Ldc, double[] work)
/* 2721:     */   {
/* 2722:2026 */     Dlatzm.dlatzm(side, m, n, v, 0, incv, tau, c1, 0, c2, 0, Ldc, work, 0);
/* 2723:     */   }
/* 2724:     */   
/* 2725:     */   public void dlatzm(String side, int m, int n, double[] v, int _v_offset, int incv, double tau, double[] c1, int _c1_offset, double[] c2, int _c2_offset, int Ldc, double[] work, int _work_offset)
/* 2726:     */   {
/* 2727:2031 */     Dlatzm.dlatzm(side, m, n, v, _v_offset, incv, tau, c1, _c1_offset, c2, _c2_offset, Ldc, work, _work_offset);
/* 2728:     */   }
/* 2729:     */   
/* 2730:     */   public void dlauu2(String uplo, int n, double[] a, int lda, intW info)
/* 2731:     */   {
/* 2732:2036 */     Dlauu2.dlauu2(uplo, n, a, 0, lda, info);
/* 2733:     */   }
/* 2734:     */   
/* 2735:     */   public void dlauu2(String uplo, int n, double[] a, int _a_offset, int lda, intW info)
/* 2736:     */   {
/* 2737:2041 */     Dlauu2.dlauu2(uplo, n, a, _a_offset, lda, info);
/* 2738:     */   }
/* 2739:     */   
/* 2740:     */   public void dlauum(String uplo, int n, double[] a, int lda, intW info)
/* 2741:     */   {
/* 2742:2046 */     Dlauum.dlauum(uplo, n, a, 0, lda, info);
/* 2743:     */   }
/* 2744:     */   
/* 2745:     */   public void dlauum(String uplo, int n, double[] a, int _a_offset, int lda, intW info)
/* 2746:     */   {
/* 2747:2051 */     Dlauum.dlauum(uplo, n, a, _a_offset, lda, info);
/* 2748:     */   }
/* 2749:     */   
/* 2750:     */   public void dlazq3(int i0, intW n0, double[] z, int pp, doubleW dmin, doubleW sigma, doubleW desig, doubleW qmax, intW nfail, intW iter, intW ndiv, boolean ieee, intW ttype, doubleW dmin1, doubleW dmin2, doubleW dn, doubleW dn1, doubleW dn2, doubleW tau)
/* 2751:     */   {
/* 2752:2056 */     Dlazq3.dlazq3(i0, n0, z, 0, pp, dmin, sigma, desig, qmax, nfail, iter, ndiv, ieee, ttype, dmin1, dmin2, dn, dn1, dn2, tau);
/* 2753:     */   }
/* 2754:     */   
/* 2755:     */   public void dlazq3(int i0, intW n0, double[] z, int _z_offset, int pp, doubleW dmin, doubleW sigma, doubleW desig, doubleW qmax, intW nfail, intW iter, intW ndiv, boolean ieee, intW ttype, doubleW dmin1, doubleW dmin2, doubleW dn, doubleW dn1, doubleW dn2, doubleW tau)
/* 2756:     */   {
/* 2757:2061 */     Dlazq3.dlazq3(i0, n0, z, _z_offset, pp, dmin, sigma, desig, qmax, nfail, iter, ndiv, ieee, ttype, dmin1, dmin2, dn, dn1, dn2, tau);
/* 2758:     */   }
/* 2759:     */   
/* 2760:     */   public void dlazq4(int i0, int n0, double[] z, int pp, int n0in, double dmin, double dmin1, double dmin2, double dn, double dn1, double dn2, doubleW tau, intW ttype, doubleW g)
/* 2761:     */   {
/* 2762:2066 */     Dlazq4.dlazq4(i0, n0, z, 0, pp, n0in, dmin, dmin1, dmin2, dn, dn1, dn2, tau, ttype, g);
/* 2763:     */   }
/* 2764:     */   
/* 2765:     */   public void dlazq4(int i0, int n0, double[] z, int _z_offset, int pp, int n0in, double dmin, double dmin1, double dmin2, double dn, double dn1, double dn2, doubleW tau, intW ttype, doubleW g)
/* 2766:     */   {
/* 2767:2071 */     Dlazq4.dlazq4(i0, n0, z, _z_offset, pp, n0in, dmin, dmin1, dmin2, dn, dn1, dn2, tau, ttype, g);
/* 2768:     */   }
/* 2769:     */   
/* 2770:     */   public void dopgtr(String uplo, int n, double[] ap, double[] tau, double[] q, int ldq, double[] work, intW info)
/* 2771:     */   {
/* 2772:2076 */     Dopgtr.dopgtr(uplo, n, ap, 0, tau, 0, q, 0, ldq, work, 0, info);
/* 2773:     */   }
/* 2774:     */   
/* 2775:     */   public void dopgtr(String uplo, int n, double[] ap, int _ap_offset, double[] tau, int _tau_offset, double[] q, int _q_offset, int ldq, double[] work, int _work_offset, intW info)
/* 2776:     */   {
/* 2777:2081 */     Dopgtr.dopgtr(uplo, n, ap, _ap_offset, tau, _tau_offset, q, _q_offset, ldq, work, _work_offset, info);
/* 2778:     */   }
/* 2779:     */   
/* 2780:     */   public void dopmtr(String side, String uplo, String trans, int m, int n, double[] ap, double[] tau, double[] c, int Ldc, double[] work, intW info)
/* 2781:     */   {
/* 2782:2086 */     Dopmtr.dopmtr(side, uplo, trans, m, n, ap, 0, tau, 0, c, 0, Ldc, work, 0, info);
/* 2783:     */   }
/* 2784:     */   
/* 2785:     */   public void dopmtr(String side, String uplo, String trans, int m, int n, double[] ap, int _ap_offset, double[] tau, int _tau_offset, double[] c, int _c_offset, int Ldc, double[] work, int _work_offset, intW info)
/* 2786:     */   {
/* 2787:2091 */     Dopmtr.dopmtr(side, uplo, trans, m, n, ap, _ap_offset, tau, _tau_offset, c, _c_offset, Ldc, work, _work_offset, info);
/* 2788:     */   }
/* 2789:     */   
/* 2790:     */   public void dorg2l(int m, int n, int k, double[] a, int lda, double[] tau, double[] work, intW info)
/* 2791:     */   {
/* 2792:2096 */     Dorg2l.dorg2l(m, n, k, a, 0, lda, tau, 0, work, 0, info);
/* 2793:     */   }
/* 2794:     */   
/* 2795:     */   public void dorg2l(int m, int n, int k, double[] a, int _a_offset, int lda, double[] tau, int _tau_offset, double[] work, int _work_offset, intW info)
/* 2796:     */   {
/* 2797:2101 */     Dorg2l.dorg2l(m, n, k, a, _a_offset, lda, tau, _tau_offset, work, _work_offset, info);
/* 2798:     */   }
/* 2799:     */   
/* 2800:     */   public void dorg2r(int m, int n, int k, double[] a, int lda, double[] tau, double[] work, intW info)
/* 2801:     */   {
/* 2802:2106 */     Dorg2r.dorg2r(m, n, k, a, 0, lda, tau, 0, work, 0, info);
/* 2803:     */   }
/* 2804:     */   
/* 2805:     */   public void dorg2r(int m, int n, int k, double[] a, int _a_offset, int lda, double[] tau, int _tau_offset, double[] work, int _work_offset, intW info)
/* 2806:     */   {
/* 2807:2111 */     Dorg2r.dorg2r(m, n, k, a, _a_offset, lda, tau, _tau_offset, work, _work_offset, info);
/* 2808:     */   }
/* 2809:     */   
/* 2810:     */   public void dorgbr(String vect, int m, int n, int k, double[] a, int lda, double[] tau, double[] work, int lwork, intW info)
/* 2811:     */   {
/* 2812:2116 */     Dorgbr.dorgbr(vect, m, n, k, a, 0, lda, tau, 0, work, 0, lwork, info);
/* 2813:     */   }
/* 2814:     */   
/* 2815:     */   public void dorgbr(String vect, int m, int n, int k, double[] a, int _a_offset, int lda, double[] tau, int _tau_offset, double[] work, int _work_offset, int lwork, intW info)
/* 2816:     */   {
/* 2817:2121 */     Dorgbr.dorgbr(vect, m, n, k, a, _a_offset, lda, tau, _tau_offset, work, _work_offset, lwork, info);
/* 2818:     */   }
/* 2819:     */   
/* 2820:     */   public void dorghr(int n, int ilo, int ihi, double[] a, int lda, double[] tau, double[] work, int lwork, intW info)
/* 2821:     */   {
/* 2822:2126 */     Dorghr.dorghr(n, ilo, ihi, a, 0, lda, tau, 0, work, 0, lwork, info);
/* 2823:     */   }
/* 2824:     */   
/* 2825:     */   public void dorghr(int n, int ilo, int ihi, double[] a, int _a_offset, int lda, double[] tau, int _tau_offset, double[] work, int _work_offset, int lwork, intW info)
/* 2826:     */   {
/* 2827:2131 */     Dorghr.dorghr(n, ilo, ihi, a, _a_offset, lda, tau, _tau_offset, work, _work_offset, lwork, info);
/* 2828:     */   }
/* 2829:     */   
/* 2830:     */   public void dorgl2(int m, int n, int k, double[] a, int lda, double[] tau, double[] work, intW info)
/* 2831:     */   {
/* 2832:2136 */     Dorgl2.dorgl2(m, n, k, a, 0, lda, tau, 0, work, 0, info);
/* 2833:     */   }
/* 2834:     */   
/* 2835:     */   public void dorgl2(int m, int n, int k, double[] a, int _a_offset, int lda, double[] tau, int _tau_offset, double[] work, int _work_offset, intW info)
/* 2836:     */   {
/* 2837:2141 */     Dorgl2.dorgl2(m, n, k, a, _a_offset, lda, tau, _tau_offset, work, _work_offset, info);
/* 2838:     */   }
/* 2839:     */   
/* 2840:     */   public void dorglq(int m, int n, int k, double[] a, int lda, double[] tau, double[] work, int lwork, intW info)
/* 2841:     */   {
/* 2842:2146 */     Dorglq.dorglq(m, n, k, a, 0, lda, tau, 0, work, 0, lwork, info);
/* 2843:     */   }
/* 2844:     */   
/* 2845:     */   public void dorglq(int m, int n, int k, double[] a, int _a_offset, int lda, double[] tau, int _tau_offset, double[] work, int _work_offset, int lwork, intW info)
/* 2846:     */   {
/* 2847:2151 */     Dorglq.dorglq(m, n, k, a, _a_offset, lda, tau, _tau_offset, work, _work_offset, lwork, info);
/* 2848:     */   }
/* 2849:     */   
/* 2850:     */   public void dorgql(int m, int n, int k, double[] a, int lda, double[] tau, double[] work, int lwork, intW info)
/* 2851:     */   {
/* 2852:2156 */     Dorgql.dorgql(m, n, k, a, 0, lda, tau, 0, work, 0, lwork, info);
/* 2853:     */   }
/* 2854:     */   
/* 2855:     */   public void dorgql(int m, int n, int k, double[] a, int _a_offset, int lda, double[] tau, int _tau_offset, double[] work, int _work_offset, int lwork, intW info)
/* 2856:     */   {
/* 2857:2161 */     Dorgql.dorgql(m, n, k, a, _a_offset, lda, tau, _tau_offset, work, _work_offset, lwork, info);
/* 2858:     */   }
/* 2859:     */   
/* 2860:     */   public void dorgqr(int m, int n, int k, double[] a, int lda, double[] tau, double[] work, int lwork, intW info)
/* 2861:     */   {
/* 2862:2166 */     Dorgqr.dorgqr(m, n, k, a, 0, lda, tau, 0, work, 0, lwork, info);
/* 2863:     */   }
/* 2864:     */   
/* 2865:     */   public void dorgqr(int m, int n, int k, double[] a, int _a_offset, int lda, double[] tau, int _tau_offset, double[] work, int _work_offset, int lwork, intW info)
/* 2866:     */   {
/* 2867:2171 */     Dorgqr.dorgqr(m, n, k, a, _a_offset, lda, tau, _tau_offset, work, _work_offset, lwork, info);
/* 2868:     */   }
/* 2869:     */   
/* 2870:     */   public void dorgr2(int m, int n, int k, double[] a, int lda, double[] tau, double[] work, intW info)
/* 2871:     */   {
/* 2872:2176 */     Dorgr2.dorgr2(m, n, k, a, 0, lda, tau, 0, work, 0, info);
/* 2873:     */   }
/* 2874:     */   
/* 2875:     */   public void dorgr2(int m, int n, int k, double[] a, int _a_offset, int lda, double[] tau, int _tau_offset, double[] work, int _work_offset, intW info)
/* 2876:     */   {
/* 2877:2181 */     Dorgr2.dorgr2(m, n, k, a, _a_offset, lda, tau, _tau_offset, work, _work_offset, info);
/* 2878:     */   }
/* 2879:     */   
/* 2880:     */   public void dorgrq(int m, int n, int k, double[] a, int lda, double[] tau, double[] work, int lwork, intW info)
/* 2881:     */   {
/* 2882:2186 */     Dorgrq.dorgrq(m, n, k, a, 0, lda, tau, 0, work, 0, lwork, info);
/* 2883:     */   }
/* 2884:     */   
/* 2885:     */   public void dorgrq(int m, int n, int k, double[] a, int _a_offset, int lda, double[] tau, int _tau_offset, double[] work, int _work_offset, int lwork, intW info)
/* 2886:     */   {
/* 2887:2191 */     Dorgrq.dorgrq(m, n, k, a, _a_offset, lda, tau, _tau_offset, work, _work_offset, lwork, info);
/* 2888:     */   }
/* 2889:     */   
/* 2890:     */   public void dorgtr(String uplo, int n, double[] a, int lda, double[] tau, double[] work, int lwork, intW info)
/* 2891:     */   {
/* 2892:2196 */     Dorgtr.dorgtr(uplo, n, a, 0, lda, tau, 0, work, 0, lwork, info);
/* 2893:     */   }
/* 2894:     */   
/* 2895:     */   public void dorgtr(String uplo, int n, double[] a, int _a_offset, int lda, double[] tau, int _tau_offset, double[] work, int _work_offset, int lwork, intW info)
/* 2896:     */   {
/* 2897:2201 */     Dorgtr.dorgtr(uplo, n, a, _a_offset, lda, tau, _tau_offset, work, _work_offset, lwork, info);
/* 2898:     */   }
/* 2899:     */   
/* 2900:     */   public void dorm2l(String side, String trans, int m, int n, int k, double[] a, int lda, double[] tau, double[] c, int Ldc, double[] work, intW info)
/* 2901:     */   {
/* 2902:2206 */     Dorm2l.dorm2l(side, trans, m, n, k, a, 0, lda, tau, 0, c, 0, Ldc, work, 0, info);
/* 2903:     */   }
/* 2904:     */   
/* 2905:     */   public void dorm2l(String side, String trans, int m, int n, int k, double[] a, int _a_offset, int lda, double[] tau, int _tau_offset, double[] c, int _c_offset, int Ldc, double[] work, int _work_offset, intW info)
/* 2906:     */   {
/* 2907:2211 */     Dorm2l.dorm2l(side, trans, m, n, k, a, _a_offset, lda, tau, _tau_offset, c, _c_offset, Ldc, work, _work_offset, info);
/* 2908:     */   }
/* 2909:     */   
/* 2910:     */   public void dorm2r(String side, String trans, int m, int n, int k, double[] a, int lda, double[] tau, double[] c, int Ldc, double[] work, intW info)
/* 2911:     */   {
/* 2912:2216 */     Dorm2r.dorm2r(side, trans, m, n, k, a, 0, lda, tau, 0, c, 0, Ldc, work, 0, info);
/* 2913:     */   }
/* 2914:     */   
/* 2915:     */   public void dorm2r(String side, String trans, int m, int n, int k, double[] a, int _a_offset, int lda, double[] tau, int _tau_offset, double[] c, int _c_offset, int Ldc, double[] work, int _work_offset, intW info)
/* 2916:     */   {
/* 2917:2221 */     Dorm2r.dorm2r(side, trans, m, n, k, a, _a_offset, lda, tau, _tau_offset, c, _c_offset, Ldc, work, _work_offset, info);
/* 2918:     */   }
/* 2919:     */   
/* 2920:     */   public void dormbr(String vect, String side, String trans, int m, int n, int k, double[] a, int lda, double[] tau, double[] c, int Ldc, double[] work, int lwork, intW info)
/* 2921:     */   {
/* 2922:2226 */     Dormbr.dormbr(vect, side, trans, m, n, k, a, 0, lda, tau, 0, c, 0, Ldc, work, 0, lwork, info);
/* 2923:     */   }
/* 2924:     */   
/* 2925:     */   public void dormbr(String vect, String side, String trans, int m, int n, int k, double[] a, int _a_offset, int lda, double[] tau, int _tau_offset, double[] c, int _c_offset, int Ldc, double[] work, int _work_offset, int lwork, intW info)
/* 2926:     */   {
/* 2927:2231 */     Dormbr.dormbr(vect, side, trans, m, n, k, a, _a_offset, lda, tau, _tau_offset, c, _c_offset, Ldc, work, _work_offset, lwork, info);
/* 2928:     */   }
/* 2929:     */   
/* 2930:     */   public void dormhr(String side, String trans, int m, int n, int ilo, int ihi, double[] a, int lda, double[] tau, double[] c, int Ldc, double[] work, int lwork, intW info)
/* 2931:     */   {
/* 2932:2236 */     Dormhr.dormhr(side, trans, m, n, ilo, ihi, a, 0, lda, tau, 0, c, 0, Ldc, work, 0, lwork, info);
/* 2933:     */   }
/* 2934:     */   
/* 2935:     */   public void dormhr(String side, String trans, int m, int n, int ilo, int ihi, double[] a, int _a_offset, int lda, double[] tau, int _tau_offset, double[] c, int _c_offset, int Ldc, double[] work, int _work_offset, int lwork, intW info)
/* 2936:     */   {
/* 2937:2241 */     Dormhr.dormhr(side, trans, m, n, ilo, ihi, a, _a_offset, lda, tau, _tau_offset, c, _c_offset, Ldc, work, _work_offset, lwork, info);
/* 2938:     */   }
/* 2939:     */   
/* 2940:     */   public void dorml2(String side, String trans, int m, int n, int k, double[] a, int lda, double[] tau, double[] c, int Ldc, double[] work, intW info)
/* 2941:     */   {
/* 2942:2246 */     Dorml2.dorml2(side, trans, m, n, k, a, 0, lda, tau, 0, c, 0, Ldc, work, 0, info);
/* 2943:     */   }
/* 2944:     */   
/* 2945:     */   public void dorml2(String side, String trans, int m, int n, int k, double[] a, int _a_offset, int lda, double[] tau, int _tau_offset, double[] c, int _c_offset, int Ldc, double[] work, int _work_offset, intW info)
/* 2946:     */   {
/* 2947:2251 */     Dorml2.dorml2(side, trans, m, n, k, a, _a_offset, lda, tau, _tau_offset, c, _c_offset, Ldc, work, _work_offset, info);
/* 2948:     */   }
/* 2949:     */   
/* 2950:     */   public void dormlq(String side, String trans, int m, int n, int k, double[] a, int lda, double[] tau, double[] c, int Ldc, double[] work, int lwork, intW info)
/* 2951:     */   {
/* 2952:2256 */     Dormlq.dormlq(side, trans, m, n, k, a, 0, lda, tau, 0, c, 0, Ldc, work, 0, lwork, info);
/* 2953:     */   }
/* 2954:     */   
/* 2955:     */   public void dormlq(String side, String trans, int m, int n, int k, double[] a, int _a_offset, int lda, double[] tau, int _tau_offset, double[] c, int _c_offset, int Ldc, double[] work, int _work_offset, int lwork, intW info)
/* 2956:     */   {
/* 2957:2261 */     Dormlq.dormlq(side, trans, m, n, k, a, _a_offset, lda, tau, _tau_offset, c, _c_offset, Ldc, work, _work_offset, lwork, info);
/* 2958:     */   }
/* 2959:     */   
/* 2960:     */   public void dormql(String side, String trans, int m, int n, int k, double[] a, int lda, double[] tau, double[] c, int Ldc, double[] work, int lwork, intW info)
/* 2961:     */   {
/* 2962:2266 */     Dormql.dormql(side, trans, m, n, k, a, 0, lda, tau, 0, c, 0, Ldc, work, 0, lwork, info);
/* 2963:     */   }
/* 2964:     */   
/* 2965:     */   public void dormql(String side, String trans, int m, int n, int k, double[] a, int _a_offset, int lda, double[] tau, int _tau_offset, double[] c, int _c_offset, int Ldc, double[] work, int _work_offset, int lwork, intW info)
/* 2966:     */   {
/* 2967:2271 */     Dormql.dormql(side, trans, m, n, k, a, _a_offset, lda, tau, _tau_offset, c, _c_offset, Ldc, work, _work_offset, lwork, info);
/* 2968:     */   }
/* 2969:     */   
/* 2970:     */   public void dormqr(String side, String trans, int m, int n, int k, double[] a, int lda, double[] tau, double[] c, int Ldc, double[] work, int lwork, intW info)
/* 2971:     */   {
/* 2972:2276 */     Dormqr.dormqr(side, trans, m, n, k, a, 0, lda, tau, 0, c, 0, Ldc, work, 0, lwork, info);
/* 2973:     */   }
/* 2974:     */   
/* 2975:     */   public void dormqr(String side, String trans, int m, int n, int k, double[] a, int _a_offset, int lda, double[] tau, int _tau_offset, double[] c, int _c_offset, int Ldc, double[] work, int _work_offset, int lwork, intW info)
/* 2976:     */   {
/* 2977:2281 */     Dormqr.dormqr(side, trans, m, n, k, a, _a_offset, lda, tau, _tau_offset, c, _c_offset, Ldc, work, _work_offset, lwork, info);
/* 2978:     */   }
/* 2979:     */   
/* 2980:     */   public void dormr2(String side, String trans, int m, int n, int k, double[] a, int lda, double[] tau, double[] c, int Ldc, double[] work, intW info)
/* 2981:     */   {
/* 2982:2286 */     Dormr2.dormr2(side, trans, m, n, k, a, 0, lda, tau, 0, c, 0, Ldc, work, 0, info);
/* 2983:     */   }
/* 2984:     */   
/* 2985:     */   public void dormr2(String side, String trans, int m, int n, int k, double[] a, int _a_offset, int lda, double[] tau, int _tau_offset, double[] c, int _c_offset, int Ldc, double[] work, int _work_offset, intW info)
/* 2986:     */   {
/* 2987:2291 */     Dormr2.dormr2(side, trans, m, n, k, a, _a_offset, lda, tau, _tau_offset, c, _c_offset, Ldc, work, _work_offset, info);
/* 2988:     */   }
/* 2989:     */   
/* 2990:     */   public void dormr3(String side, String trans, int m, int n, int k, int l, double[] a, int lda, double[] tau, double[] c, int Ldc, double[] work, intW info)
/* 2991:     */   {
/* 2992:2296 */     Dormr3.dormr3(side, trans, m, n, k, l, a, 0, lda, tau, 0, c, 0, Ldc, work, 0, info);
/* 2993:     */   }
/* 2994:     */   
/* 2995:     */   public void dormr3(String side, String trans, int m, int n, int k, int l, double[] a, int _a_offset, int lda, double[] tau, int _tau_offset, double[] c, int _c_offset, int Ldc, double[] work, int _work_offset, intW info)
/* 2996:     */   {
/* 2997:2301 */     Dormr3.dormr3(side, trans, m, n, k, l, a, _a_offset, lda, tau, _tau_offset, c, _c_offset, Ldc, work, _work_offset, info);
/* 2998:     */   }
/* 2999:     */   
/* 3000:     */   public void dormrq(String side, String trans, int m, int n, int k, double[] a, int lda, double[] tau, double[] c, int Ldc, double[] work, int lwork, intW info)
/* 3001:     */   {
/* 3002:2306 */     Dormrq.dormrq(side, trans, m, n, k, a, 0, lda, tau, 0, c, 0, Ldc, work, 0, lwork, info);
/* 3003:     */   }
/* 3004:     */   
/* 3005:     */   public void dormrq(String side, String trans, int m, int n, int k, double[] a, int _a_offset, int lda, double[] tau, int _tau_offset, double[] c, int _c_offset, int Ldc, double[] work, int _work_offset, int lwork, intW info)
/* 3006:     */   {
/* 3007:2311 */     Dormrq.dormrq(side, trans, m, n, k, a, _a_offset, lda, tau, _tau_offset, c, _c_offset, Ldc, work, _work_offset, lwork, info);
/* 3008:     */   }
/* 3009:     */   
/* 3010:     */   public void dormrz(String side, String trans, int m, int n, int k, int l, double[] a, int lda, double[] tau, double[] c, int Ldc, double[] work, int lwork, intW info)
/* 3011:     */   {
/* 3012:2316 */     Dormrz.dormrz(side, trans, m, n, k, l, a, 0, lda, tau, 0, c, 0, Ldc, work, 0, lwork, info);
/* 3013:     */   }
/* 3014:     */   
/* 3015:     */   public void dormrz(String side, String trans, int m, int n, int k, int l, double[] a, int _a_offset, int lda, double[] tau, int _tau_offset, double[] c, int _c_offset, int Ldc, double[] work, int _work_offset, int lwork, intW info)
/* 3016:     */   {
/* 3017:2321 */     Dormrz.dormrz(side, trans, m, n, k, l, a, _a_offset, lda, tau, _tau_offset, c, _c_offset, Ldc, work, _work_offset, lwork, info);
/* 3018:     */   }
/* 3019:     */   
/* 3020:     */   public void dormtr(String side, String uplo, String trans, int m, int n, double[] a, int lda, double[] tau, double[] c, int Ldc, double[] work, int lwork, intW info)
/* 3021:     */   {
/* 3022:2326 */     Dormtr.dormtr(side, uplo, trans, m, n, a, 0, lda, tau, 0, c, 0, Ldc, work, 0, lwork, info);
/* 3023:     */   }
/* 3024:     */   
/* 3025:     */   public void dormtr(String side, String uplo, String trans, int m, int n, double[] a, int _a_offset, int lda, double[] tau, int _tau_offset, double[] c, int _c_offset, int Ldc, double[] work, int _work_offset, int lwork, intW info)
/* 3026:     */   {
/* 3027:2331 */     Dormtr.dormtr(side, uplo, trans, m, n, a, _a_offset, lda, tau, _tau_offset, c, _c_offset, Ldc, work, _work_offset, lwork, info);
/* 3028:     */   }
/* 3029:     */   
/* 3030:     */   public void dpbcon(String uplo, int n, int kd, double[] ab, int ldab, double anorm, doubleW rcond, double[] work, int[] iwork, intW info)
/* 3031:     */   {
/* 3032:2336 */     Dpbcon.dpbcon(uplo, n, kd, ab, 0, ldab, anorm, rcond, work, 0, iwork, 0, info);
/* 3033:     */   }
/* 3034:     */   
/* 3035:     */   public void dpbcon(String uplo, int n, int kd, double[] ab, int _ab_offset, int ldab, double anorm, doubleW rcond, double[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 3036:     */   {
/* 3037:2341 */     Dpbcon.dpbcon(uplo, n, kd, ab, _ab_offset, ldab, anorm, rcond, work, _work_offset, iwork, _iwork_offset, info);
/* 3038:     */   }
/* 3039:     */   
/* 3040:     */   public void dpbequ(String uplo, int n, int kd, double[] ab, int ldab, double[] s, doubleW scond, doubleW amax, intW info)
/* 3041:     */   {
/* 3042:2346 */     Dpbequ.dpbequ(uplo, n, kd, ab, 0, ldab, s, 0, scond, amax, info);
/* 3043:     */   }
/* 3044:     */   
/* 3045:     */   public void dpbequ(String uplo, int n, int kd, double[] ab, int _ab_offset, int ldab, double[] s, int _s_offset, doubleW scond, doubleW amax, intW info)
/* 3046:     */   {
/* 3047:2351 */     Dpbequ.dpbequ(uplo, n, kd, ab, _ab_offset, ldab, s, _s_offset, scond, amax, info);
/* 3048:     */   }
/* 3049:     */   
/* 3050:     */   public void dpbrfs(String uplo, int n, int kd, int nrhs, double[] ab, int ldab, double[] afb, int ldafb, double[] b, int ldb, double[] x, int ldx, double[] ferr, double[] berr, double[] work, int[] iwork, intW info)
/* 3051:     */   {
/* 3052:2356 */     Dpbrfs.dpbrfs(uplo, n, kd, nrhs, ab, 0, ldab, afb, 0, ldafb, b, 0, ldb, x, 0, ldx, ferr, 0, berr, 0, work, 0, iwork, 0, info);
/* 3053:     */   }
/* 3054:     */   
/* 3055:     */   public void dpbrfs(String uplo, int n, int kd, int nrhs, double[] ab, int _ab_offset, int ldab, double[] afb, int _afb_offset, int ldafb, double[] b, int _b_offset, int ldb, double[] x, int _x_offset, int ldx, double[] ferr, int _ferr_offset, double[] berr, int _berr_offset, double[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 3056:     */   {
/* 3057:2361 */     Dpbrfs.dpbrfs(uplo, n, kd, nrhs, ab, _ab_offset, ldab, afb, _afb_offset, ldafb, b, _b_offset, ldb, x, _x_offset, ldx, ferr, _ferr_offset, berr, _berr_offset, work, _work_offset, iwork, _iwork_offset, info);
/* 3058:     */   }
/* 3059:     */   
/* 3060:     */   public void dpbstf(String uplo, int n, int kd, double[] ab, int ldab, intW info)
/* 3061:     */   {
/* 3062:2366 */     Dpbstf.dpbstf(uplo, n, kd, ab, 0, ldab, info);
/* 3063:     */   }
/* 3064:     */   
/* 3065:     */   public void dpbstf(String uplo, int n, int kd, double[] ab, int _ab_offset, int ldab, intW info)
/* 3066:     */   {
/* 3067:2371 */     Dpbstf.dpbstf(uplo, n, kd, ab, _ab_offset, ldab, info);
/* 3068:     */   }
/* 3069:     */   
/* 3070:     */   public void dpbsv(String uplo, int n, int kd, int nrhs, double[] ab, int ldab, double[] b, int ldb, intW info)
/* 3071:     */   {
/* 3072:2376 */     Dpbsv.dpbsv(uplo, n, kd, nrhs, ab, 0, ldab, b, 0, ldb, info);
/* 3073:     */   }
/* 3074:     */   
/* 3075:     */   public void dpbsv(String uplo, int n, int kd, int nrhs, double[] ab, int _ab_offset, int ldab, double[] b, int _b_offset, int ldb, intW info)
/* 3076:     */   {
/* 3077:2381 */     Dpbsv.dpbsv(uplo, n, kd, nrhs, ab, _ab_offset, ldab, b, _b_offset, ldb, info);
/* 3078:     */   }
/* 3079:     */   
/* 3080:     */   public void dpbsvx(String fact, String uplo, int n, int kd, int nrhs, double[] ab, int ldab, double[] afb, int ldafb, StringW equed, double[] s, double[] b, int ldb, double[] x, int ldx, doubleW rcond, double[] ferr, double[] berr, double[] work, int[] iwork, intW info)
/* 3081:     */   {
/* 3082:2386 */     Dpbsvx.dpbsvx(fact, uplo, n, kd, nrhs, ab, 0, ldab, afb, 0, ldafb, equed, s, 0, b, 0, ldb, x, 0, ldx, rcond, ferr, 0, berr, 0, work, 0, iwork, 0, info);
/* 3083:     */   }
/* 3084:     */   
/* 3085:     */   public void dpbsvx(String fact, String uplo, int n, int kd, int nrhs, double[] ab, int _ab_offset, int ldab, double[] afb, int _afb_offset, int ldafb, StringW equed, double[] s, int _s_offset, double[] b, int _b_offset, int ldb, double[] x, int _x_offset, int ldx, doubleW rcond, double[] ferr, int _ferr_offset, double[] berr, int _berr_offset, double[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 3086:     */   {
/* 3087:2391 */     Dpbsvx.dpbsvx(fact, uplo, n, kd, nrhs, ab, _ab_offset, ldab, afb, _afb_offset, ldafb, equed, s, _s_offset, b, _b_offset, ldb, x, _x_offset, ldx, rcond, ferr, _ferr_offset, berr, _berr_offset, work, _work_offset, iwork, _iwork_offset, info);
/* 3088:     */   }
/* 3089:     */   
/* 3090:     */   public void dpbtf2(String uplo, int n, int kd, double[] ab, int ldab, intW info)
/* 3091:     */   {
/* 3092:2396 */     Dpbtf2.dpbtf2(uplo, n, kd, ab, 0, ldab, info);
/* 3093:     */   }
/* 3094:     */   
/* 3095:     */   public void dpbtf2(String uplo, int n, int kd, double[] ab, int _ab_offset, int ldab, intW info)
/* 3096:     */   {
/* 3097:2401 */     Dpbtf2.dpbtf2(uplo, n, kd, ab, _ab_offset, ldab, info);
/* 3098:     */   }
/* 3099:     */   
/* 3100:     */   public void dpbtrf(String uplo, int n, int kd, double[] ab, int ldab, intW info)
/* 3101:     */   {
/* 3102:2406 */     Dpbtrf.dpbtrf(uplo, n, kd, ab, 0, ldab, info);
/* 3103:     */   }
/* 3104:     */   
/* 3105:     */   public void dpbtrf(String uplo, int n, int kd, double[] ab, int _ab_offset, int ldab, intW info)
/* 3106:     */   {
/* 3107:2411 */     Dpbtrf.dpbtrf(uplo, n, kd, ab, _ab_offset, ldab, info);
/* 3108:     */   }
/* 3109:     */   
/* 3110:     */   public void dpbtrs(String uplo, int n, int kd, int nrhs, double[] ab, int ldab, double[] b, int ldb, intW info)
/* 3111:     */   {
/* 3112:2416 */     Dpbtrs.dpbtrs(uplo, n, kd, nrhs, ab, 0, ldab, b, 0, ldb, info);
/* 3113:     */   }
/* 3114:     */   
/* 3115:     */   public void dpbtrs(String uplo, int n, int kd, int nrhs, double[] ab, int _ab_offset, int ldab, double[] b, int _b_offset, int ldb, intW info)
/* 3116:     */   {
/* 3117:2421 */     Dpbtrs.dpbtrs(uplo, n, kd, nrhs, ab, _ab_offset, ldab, b, _b_offset, ldb, info);
/* 3118:     */   }
/* 3119:     */   
/* 3120:     */   public void dpocon(String uplo, int n, double[] a, int lda, double anorm, doubleW rcond, double[] work, int[] iwork, intW info)
/* 3121:     */   {
/* 3122:2426 */     Dpocon.dpocon(uplo, n, a, 0, lda, anorm, rcond, work, 0, iwork, 0, info);
/* 3123:     */   }
/* 3124:     */   
/* 3125:     */   public void dpocon(String uplo, int n, double[] a, int _a_offset, int lda, double anorm, doubleW rcond, double[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 3126:     */   {
/* 3127:2431 */     Dpocon.dpocon(uplo, n, a, _a_offset, lda, anorm, rcond, work, _work_offset, iwork, _iwork_offset, info);
/* 3128:     */   }
/* 3129:     */   
/* 3130:     */   public void dpoequ(int n, double[] a, int lda, double[] s, doubleW scond, doubleW amax, intW info)
/* 3131:     */   {
/* 3132:2436 */     Dpoequ.dpoequ(n, a, 0, lda, s, 0, scond, amax, info);
/* 3133:     */   }
/* 3134:     */   
/* 3135:     */   public void dpoequ(int n, double[] a, int _a_offset, int lda, double[] s, int _s_offset, doubleW scond, doubleW amax, intW info)
/* 3136:     */   {
/* 3137:2441 */     Dpoequ.dpoequ(n, a, _a_offset, lda, s, _s_offset, scond, amax, info);
/* 3138:     */   }
/* 3139:     */   
/* 3140:     */   public void dporfs(String uplo, int n, int nrhs, double[] a, int lda, double[] af, int ldaf, double[] b, int ldb, double[] x, int ldx, double[] ferr, double[] berr, double[] work, int[] iwork, intW info)
/* 3141:     */   {
/* 3142:2446 */     Dporfs.dporfs(uplo, n, nrhs, a, 0, lda, af, 0, ldaf, b, 0, ldb, x, 0, ldx, ferr, 0, berr, 0, work, 0, iwork, 0, info);
/* 3143:     */   }
/* 3144:     */   
/* 3145:     */   public void dporfs(String uplo, int n, int nrhs, double[] a, int _a_offset, int lda, double[] af, int _af_offset, int ldaf, double[] b, int _b_offset, int ldb, double[] x, int _x_offset, int ldx, double[] ferr, int _ferr_offset, double[] berr, int _berr_offset, double[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 3146:     */   {
/* 3147:2451 */     Dporfs.dporfs(uplo, n, nrhs, a, _a_offset, lda, af, _af_offset, ldaf, b, _b_offset, ldb, x, _x_offset, ldx, ferr, _ferr_offset, berr, _berr_offset, work, _work_offset, iwork, _iwork_offset, info);
/* 3148:     */   }
/* 3149:     */   
/* 3150:     */   public void dposv(String uplo, int n, int nrhs, double[] a, int lda, double[] b, int ldb, intW info)
/* 3151:     */   {
/* 3152:2456 */     Dposv.dposv(uplo, n, nrhs, a, 0, lda, b, 0, ldb, info);
/* 3153:     */   }
/* 3154:     */   
/* 3155:     */   public void dposv(String uplo, int n, int nrhs, double[] a, int _a_offset, int lda, double[] b, int _b_offset, int ldb, intW info)
/* 3156:     */   {
/* 3157:2461 */     Dposv.dposv(uplo, n, nrhs, a, _a_offset, lda, b, _b_offset, ldb, info);
/* 3158:     */   }
/* 3159:     */   
/* 3160:     */   public void dposvx(String fact, String uplo, int n, int nrhs, double[] a, int lda, double[] af, int ldaf, StringW equed, double[] s, double[] b, int ldb, double[] x, int ldx, doubleW rcond, double[] ferr, double[] berr, double[] work, int[] iwork, intW info)
/* 3161:     */   {
/* 3162:2466 */     Dposvx.dposvx(fact, uplo, n, nrhs, a, 0, lda, af, 0, ldaf, equed, s, 0, b, 0, ldb, x, 0, ldx, rcond, ferr, 0, berr, 0, work, 0, iwork, 0, info);
/* 3163:     */   }
/* 3164:     */   
/* 3165:     */   public void dposvx(String fact, String uplo, int n, int nrhs, double[] a, int _a_offset, int lda, double[] af, int _af_offset, int ldaf, StringW equed, double[] s, int _s_offset, double[] b, int _b_offset, int ldb, double[] x, int _x_offset, int ldx, doubleW rcond, double[] ferr, int _ferr_offset, double[] berr, int _berr_offset, double[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 3166:     */   {
/* 3167:2471 */     Dposvx.dposvx(fact, uplo, n, nrhs, a, _a_offset, lda, af, _af_offset, ldaf, equed, s, _s_offset, b, _b_offset, ldb, x, _x_offset, ldx, rcond, ferr, _ferr_offset, berr, _berr_offset, work, _work_offset, iwork, _iwork_offset, info);
/* 3168:     */   }
/* 3169:     */   
/* 3170:     */   public void dpotf2(String uplo, int n, double[] a, int lda, intW info)
/* 3171:     */   {
/* 3172:2476 */     Dpotf2.dpotf2(uplo, n, a, 0, lda, info);
/* 3173:     */   }
/* 3174:     */   
/* 3175:     */   public void dpotf2(String uplo, int n, double[] a, int _a_offset, int lda, intW info)
/* 3176:     */   {
/* 3177:2481 */     Dpotf2.dpotf2(uplo, n, a, _a_offset, lda, info);
/* 3178:     */   }
/* 3179:     */   
/* 3180:     */   public void dpotrf(String uplo, int n, double[] a, int lda, intW info)
/* 3181:     */   {
/* 3182:2486 */     Dpotrf.dpotrf(uplo, n, a, 0, lda, info);
/* 3183:     */   }
/* 3184:     */   
/* 3185:     */   public void dpotrf(String uplo, int n, double[] a, int _a_offset, int lda, intW info)
/* 3186:     */   {
/* 3187:2491 */     Dpotrf.dpotrf(uplo, n, a, _a_offset, lda, info);
/* 3188:     */   }
/* 3189:     */   
/* 3190:     */   public void dpotri(String uplo, int n, double[] a, int lda, intW info)
/* 3191:     */   {
/* 3192:2496 */     Dpotri.dpotri(uplo, n, a, 0, lda, info);
/* 3193:     */   }
/* 3194:     */   
/* 3195:     */   public void dpotri(String uplo, int n, double[] a, int _a_offset, int lda, intW info)
/* 3196:     */   {
/* 3197:2501 */     Dpotri.dpotri(uplo, n, a, _a_offset, lda, info);
/* 3198:     */   }
/* 3199:     */   
/* 3200:     */   public void dpotrs(String uplo, int n, int nrhs, double[] a, int lda, double[] b, int ldb, intW info)
/* 3201:     */   {
/* 3202:2506 */     Dpotrs.dpotrs(uplo, n, nrhs, a, 0, lda, b, 0, ldb, info);
/* 3203:     */   }
/* 3204:     */   
/* 3205:     */   public void dpotrs(String uplo, int n, int nrhs, double[] a, int _a_offset, int lda, double[] b, int _b_offset, int ldb, intW info)
/* 3206:     */   {
/* 3207:2511 */     Dpotrs.dpotrs(uplo, n, nrhs, a, _a_offset, lda, b, _b_offset, ldb, info);
/* 3208:     */   }
/* 3209:     */   
/* 3210:     */   public void dppcon(String uplo, int n, double[] ap, double anorm, doubleW rcond, double[] work, int[] iwork, intW info)
/* 3211:     */   {
/* 3212:2516 */     Dppcon.dppcon(uplo, n, ap, 0, anorm, rcond, work, 0, iwork, 0, info);
/* 3213:     */   }
/* 3214:     */   
/* 3215:     */   public void dppcon(String uplo, int n, double[] ap, int _ap_offset, double anorm, doubleW rcond, double[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 3216:     */   {
/* 3217:2521 */     Dppcon.dppcon(uplo, n, ap, _ap_offset, anorm, rcond, work, _work_offset, iwork, _iwork_offset, info);
/* 3218:     */   }
/* 3219:     */   
/* 3220:     */   public void dppequ(String uplo, int n, double[] ap, double[] s, doubleW scond, doubleW amax, intW info)
/* 3221:     */   {
/* 3222:2526 */     Dppequ.dppequ(uplo, n, ap, 0, s, 0, scond, amax, info);
/* 3223:     */   }
/* 3224:     */   
/* 3225:     */   public void dppequ(String uplo, int n, double[] ap, int _ap_offset, double[] s, int _s_offset, doubleW scond, doubleW amax, intW info)
/* 3226:     */   {
/* 3227:2531 */     Dppequ.dppequ(uplo, n, ap, _ap_offset, s, _s_offset, scond, amax, info);
/* 3228:     */   }
/* 3229:     */   
/* 3230:     */   public void dpprfs(String uplo, int n, int nrhs, double[] ap, double[] afp, double[] b, int ldb, double[] x, int ldx, double[] ferr, double[] berr, double[] work, int[] iwork, intW info)
/* 3231:     */   {
/* 3232:2536 */     Dpprfs.dpprfs(uplo, n, nrhs, ap, 0, afp, 0, b, 0, ldb, x, 0, ldx, ferr, 0, berr, 0, work, 0, iwork, 0, info);
/* 3233:     */   }
/* 3234:     */   
/* 3235:     */   public void dpprfs(String uplo, int n, int nrhs, double[] ap, int _ap_offset, double[] afp, int _afp_offset, double[] b, int _b_offset, int ldb, double[] x, int _x_offset, int ldx, double[] ferr, int _ferr_offset, double[] berr, int _berr_offset, double[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 3236:     */   {
/* 3237:2541 */     Dpprfs.dpprfs(uplo, n, nrhs, ap, _ap_offset, afp, _afp_offset, b, _b_offset, ldb, x, _x_offset, ldx, ferr, _ferr_offset, berr, _berr_offset, work, _work_offset, iwork, _iwork_offset, info);
/* 3238:     */   }
/* 3239:     */   
/* 3240:     */   public void dppsv(String uplo, int n, int nrhs, double[] ap, double[] b, int ldb, intW info)
/* 3241:     */   {
/* 3242:2546 */     Dppsv.dppsv(uplo, n, nrhs, ap, 0, b, 0, ldb, info);
/* 3243:     */   }
/* 3244:     */   
/* 3245:     */   public void dppsv(String uplo, int n, int nrhs, double[] ap, int _ap_offset, double[] b, int _b_offset, int ldb, intW info)
/* 3246:     */   {
/* 3247:2551 */     Dppsv.dppsv(uplo, n, nrhs, ap, _ap_offset, b, _b_offset, ldb, info);
/* 3248:     */   }
/* 3249:     */   
/* 3250:     */   public void dppsvx(String fact, String uplo, int n, int nrhs, double[] ap, double[] afp, StringW equed, double[] s, double[] b, int ldb, double[] x, int ldx, doubleW rcond, double[] ferr, double[] berr, double[] work, int[] iwork, intW info)
/* 3251:     */   {
/* 3252:2556 */     Dppsvx.dppsvx(fact, uplo, n, nrhs, ap, 0, afp, 0, equed, s, 0, b, 0, ldb, x, 0, ldx, rcond, ferr, 0, berr, 0, work, 0, iwork, 0, info);
/* 3253:     */   }
/* 3254:     */   
/* 3255:     */   public void dppsvx(String fact, String uplo, int n, int nrhs, double[] ap, int _ap_offset, double[] afp, int _afp_offset, StringW equed, double[] s, int _s_offset, double[] b, int _b_offset, int ldb, double[] x, int _x_offset, int ldx, doubleW rcond, double[] ferr, int _ferr_offset, double[] berr, int _berr_offset, double[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 3256:     */   {
/* 3257:2561 */     Dppsvx.dppsvx(fact, uplo, n, nrhs, ap, _ap_offset, afp, _afp_offset, equed, s, _s_offset, b, _b_offset, ldb, x, _x_offset, ldx, rcond, ferr, _ferr_offset, berr, _berr_offset, work, _work_offset, iwork, _iwork_offset, info);
/* 3258:     */   }
/* 3259:     */   
/* 3260:     */   public void dpptrf(String uplo, int n, double[] ap, intW info)
/* 3261:     */   {
/* 3262:2566 */     Dpptrf.dpptrf(uplo, n, ap, 0, info);
/* 3263:     */   }
/* 3264:     */   
/* 3265:     */   public void dpptrf(String uplo, int n, double[] ap, int _ap_offset, intW info)
/* 3266:     */   {
/* 3267:2571 */     Dpptrf.dpptrf(uplo, n, ap, _ap_offset, info);
/* 3268:     */   }
/* 3269:     */   
/* 3270:     */   public void dpptri(String uplo, int n, double[] ap, intW info)
/* 3271:     */   {
/* 3272:2576 */     Dpptri.dpptri(uplo, n, ap, 0, info);
/* 3273:     */   }
/* 3274:     */   
/* 3275:     */   public void dpptri(String uplo, int n, double[] ap, int _ap_offset, intW info)
/* 3276:     */   {
/* 3277:2581 */     Dpptri.dpptri(uplo, n, ap, _ap_offset, info);
/* 3278:     */   }
/* 3279:     */   
/* 3280:     */   public void dpptrs(String uplo, int n, int nrhs, double[] ap, double[] b, int ldb, intW info)
/* 3281:     */   {
/* 3282:2586 */     Dpptrs.dpptrs(uplo, n, nrhs, ap, 0, b, 0, ldb, info);
/* 3283:     */   }
/* 3284:     */   
/* 3285:     */   public void dpptrs(String uplo, int n, int nrhs, double[] ap, int _ap_offset, double[] b, int _b_offset, int ldb, intW info)
/* 3286:     */   {
/* 3287:2591 */     Dpptrs.dpptrs(uplo, n, nrhs, ap, _ap_offset, b, _b_offset, ldb, info);
/* 3288:     */   }
/* 3289:     */   
/* 3290:     */   public void dptcon(int n, double[] d, double[] e, double anorm, doubleW rcond, double[] work, intW info)
/* 3291:     */   {
/* 3292:2596 */     Dptcon.dptcon(n, d, 0, e, 0, anorm, rcond, work, 0, info);
/* 3293:     */   }
/* 3294:     */   
/* 3295:     */   public void dptcon(int n, double[] d, int _d_offset, double[] e, int _e_offset, double anorm, doubleW rcond, double[] work, int _work_offset, intW info)
/* 3296:     */   {
/* 3297:2601 */     Dptcon.dptcon(n, d, _d_offset, e, _e_offset, anorm, rcond, work, _work_offset, info);
/* 3298:     */   }
/* 3299:     */   
/* 3300:     */   public void dpteqr(String compz, int n, double[] d, double[] e, double[] z, int ldz, double[] work, intW info)
/* 3301:     */   {
/* 3302:2606 */     Dpteqr.dpteqr(compz, n, d, 0, e, 0, z, 0, ldz, work, 0, info);
/* 3303:     */   }
/* 3304:     */   
/* 3305:     */   public void dpteqr(String compz, int n, double[] d, int _d_offset, double[] e, int _e_offset, double[] z, int _z_offset, int ldz, double[] work, int _work_offset, intW info)
/* 3306:     */   {
/* 3307:2611 */     Dpteqr.dpteqr(compz, n, d, _d_offset, e, _e_offset, z, _z_offset, ldz, work, _work_offset, info);
/* 3308:     */   }
/* 3309:     */   
/* 3310:     */   public void dptrfs(int n, int nrhs, double[] d, double[] e, double[] df, double[] ef, double[] b, int ldb, double[] x, int ldx, double[] ferr, double[] berr, double[] work, intW info)
/* 3311:     */   {
/* 3312:2616 */     Dptrfs.dptrfs(n, nrhs, d, 0, e, 0, df, 0, ef, 0, b, 0, ldb, x, 0, ldx, ferr, 0, berr, 0, work, 0, info);
/* 3313:     */   }
/* 3314:     */   
/* 3315:     */   public void dptrfs(int n, int nrhs, double[] d, int _d_offset, double[] e, int _e_offset, double[] df, int _df_offset, double[] ef, int _ef_offset, double[] b, int _b_offset, int ldb, double[] x, int _x_offset, int ldx, double[] ferr, int _ferr_offset, double[] berr, int _berr_offset, double[] work, int _work_offset, intW info)
/* 3316:     */   {
/* 3317:2621 */     Dptrfs.dptrfs(n, nrhs, d, _d_offset, e, _e_offset, df, _df_offset, ef, _ef_offset, b, _b_offset, ldb, x, _x_offset, ldx, ferr, _ferr_offset, berr, _berr_offset, work, _work_offset, info);
/* 3318:     */   }
/* 3319:     */   
/* 3320:     */   public void dptsv(int n, int nrhs, double[] d, double[] e, double[] b, int ldb, intW info)
/* 3321:     */   {
/* 3322:2626 */     Dptsv.dptsv(n, nrhs, d, 0, e, 0, b, 0, ldb, info);
/* 3323:     */   }
/* 3324:     */   
/* 3325:     */   public void dptsv(int n, int nrhs, double[] d, int _d_offset, double[] e, int _e_offset, double[] b, int _b_offset, int ldb, intW info)
/* 3326:     */   {
/* 3327:2631 */     Dptsv.dptsv(n, nrhs, d, _d_offset, e, _e_offset, b, _b_offset, ldb, info);
/* 3328:     */   }
/* 3329:     */   
/* 3330:     */   public void dptsvx(String fact, int n, int nrhs, double[] d, double[] e, double[] df, double[] ef, double[] b, int ldb, double[] x, int ldx, doubleW rcond, double[] ferr, double[] berr, double[] work, intW info)
/* 3331:     */   {
/* 3332:2636 */     Dptsvx.dptsvx(fact, n, nrhs, d, 0, e, 0, df, 0, ef, 0, b, 0, ldb, x, 0, ldx, rcond, ferr, 0, berr, 0, work, 0, info);
/* 3333:     */   }
/* 3334:     */   
/* 3335:     */   public void dptsvx(String fact, int n, int nrhs, double[] d, int _d_offset, double[] e, int _e_offset, double[] df, int _df_offset, double[] ef, int _ef_offset, double[] b, int _b_offset, int ldb, double[] x, int _x_offset, int ldx, doubleW rcond, double[] ferr, int _ferr_offset, double[] berr, int _berr_offset, double[] work, int _work_offset, intW info)
/* 3336:     */   {
/* 3337:2641 */     Dptsvx.dptsvx(fact, n, nrhs, d, _d_offset, e, _e_offset, df, _df_offset, ef, _ef_offset, b, _b_offset, ldb, x, _x_offset, ldx, rcond, ferr, _ferr_offset, berr, _berr_offset, work, _work_offset, info);
/* 3338:     */   }
/* 3339:     */   
/* 3340:     */   public void dpttrf(int n, double[] d, double[] e, intW info)
/* 3341:     */   {
/* 3342:2646 */     Dpttrf.dpttrf(n, d, 0, e, 0, info);
/* 3343:     */   }
/* 3344:     */   
/* 3345:     */   public void dpttrf(int n, double[] d, int _d_offset, double[] e, int _e_offset, intW info)
/* 3346:     */   {
/* 3347:2651 */     Dpttrf.dpttrf(n, d, _d_offset, e, _e_offset, info);
/* 3348:     */   }
/* 3349:     */   
/* 3350:     */   public void dpttrs(int n, int nrhs, double[] d, double[] e, double[] b, int ldb, intW info)
/* 3351:     */   {
/* 3352:2656 */     Dpttrs.dpttrs(n, nrhs, d, 0, e, 0, b, 0, ldb, info);
/* 3353:     */   }
/* 3354:     */   
/* 3355:     */   public void dpttrs(int n, int nrhs, double[] d, int _d_offset, double[] e, int _e_offset, double[] b, int _b_offset, int ldb, intW info)
/* 3356:     */   {
/* 3357:2661 */     Dpttrs.dpttrs(n, nrhs, d, _d_offset, e, _e_offset, b, _b_offset, ldb, info);
/* 3358:     */   }
/* 3359:     */   
/* 3360:     */   public void dptts2(int n, int nrhs, double[] d, double[] e, double[] b, int ldb)
/* 3361:     */   {
/* 3362:2666 */     Dptts2.dptts2(n, nrhs, d, 0, e, 0, b, 0, ldb);
/* 3363:     */   }
/* 3364:     */   
/* 3365:     */   public void dptts2(int n, int nrhs, double[] d, int _d_offset, double[] e, int _e_offset, double[] b, int _b_offset, int ldb)
/* 3366:     */   {
/* 3367:2671 */     Dptts2.dptts2(n, nrhs, d, _d_offset, e, _e_offset, b, _b_offset, ldb);
/* 3368:     */   }
/* 3369:     */   
/* 3370:     */   public void drscl(int n, double sa, double[] sx, int incx)
/* 3371:     */   {
/* 3372:2676 */     Drscl.drscl(n, sa, sx, 0, incx);
/* 3373:     */   }
/* 3374:     */   
/* 3375:     */   public void drscl(int n, double sa, double[] sx, int _sx_offset, int incx)
/* 3376:     */   {
/* 3377:2681 */     Drscl.drscl(n, sa, sx, _sx_offset, incx);
/* 3378:     */   }
/* 3379:     */   
/* 3380:     */   public void dsbev(String jobz, String uplo, int n, int kd, double[] ab, int ldab, double[] w, double[] z, int ldz, double[] work, intW info)
/* 3381:     */   {
/* 3382:2686 */     Dsbev.dsbev(jobz, uplo, n, kd, ab, 0, ldab, w, 0, z, 0, ldz, work, 0, info);
/* 3383:     */   }
/* 3384:     */   
/* 3385:     */   public void dsbev(String jobz, String uplo, int n, int kd, double[] ab, int _ab_offset, int ldab, double[] w, int _w_offset, double[] z, int _z_offset, int ldz, double[] work, int _work_offset, intW info)
/* 3386:     */   {
/* 3387:2691 */     Dsbev.dsbev(jobz, uplo, n, kd, ab, _ab_offset, ldab, w, _w_offset, z, _z_offset, ldz, work, _work_offset, info);
/* 3388:     */   }
/* 3389:     */   
/* 3390:     */   public void dsbevd(String jobz, String uplo, int n, int kd, double[] ab, int ldab, double[] w, double[] z, int ldz, double[] work, int lwork, int[] iwork, int liwork, intW info)
/* 3391:     */   {
/* 3392:2696 */     Dsbevd.dsbevd(jobz, uplo, n, kd, ab, 0, ldab, w, 0, z, 0, ldz, work, 0, lwork, iwork, 0, liwork, info);
/* 3393:     */   }
/* 3394:     */   
/* 3395:     */   public void dsbevd(String jobz, String uplo, int n, int kd, double[] ab, int _ab_offset, int ldab, double[] w, int _w_offset, double[] z, int _z_offset, int ldz, double[] work, int _work_offset, int lwork, int[] iwork, int _iwork_offset, int liwork, intW info)
/* 3396:     */   {
/* 3397:2701 */     Dsbevd.dsbevd(jobz, uplo, n, kd, ab, _ab_offset, ldab, w, _w_offset, z, _z_offset, ldz, work, _work_offset, lwork, iwork, _iwork_offset, liwork, info);
/* 3398:     */   }
/* 3399:     */   
/* 3400:     */   public void dsbevx(String jobz, String range, String uplo, int n, int kd, double[] ab, int ldab, double[] q, int ldq, double vl, double vu, int il, int iu, double abstol, intW m, double[] w, double[] z, int ldz, double[] work, int[] iwork, int[] ifail, intW info)
/* 3401:     */   {
/* 3402:2706 */     Dsbevx.dsbevx(jobz, range, uplo, n, kd, ab, 0, ldab, q, 0, ldq, vl, vu, il, iu, abstol, m, w, 0, z, 0, ldz, work, 0, iwork, 0, ifail, 0, info);
/* 3403:     */   }
/* 3404:     */   
/* 3405:     */   public void dsbevx(String jobz, String range, String uplo, int n, int kd, double[] ab, int _ab_offset, int ldab, double[] q, int _q_offset, int ldq, double vl, double vu, int il, int iu, double abstol, intW m, double[] w, int _w_offset, double[] z, int _z_offset, int ldz, double[] work, int _work_offset, int[] iwork, int _iwork_offset, int[] ifail, int _ifail_offset, intW info)
/* 3406:     */   {
/* 3407:2711 */     Dsbevx.dsbevx(jobz, range, uplo, n, kd, ab, _ab_offset, ldab, q, _q_offset, ldq, vl, vu, il, iu, abstol, m, w, _w_offset, z, _z_offset, ldz, work, _work_offset, iwork, _iwork_offset, ifail, _ifail_offset, info);
/* 3408:     */   }
/* 3409:     */   
/* 3410:     */   public void dsbgst(String vect, String uplo, int n, int ka, int kb, double[] ab, int ldab, double[] bb, int ldbb, double[] x, int ldx, double[] work, intW info)
/* 3411:     */   {
/* 3412:2716 */     Dsbgst.dsbgst(vect, uplo, n, ka, kb, ab, 0, ldab, bb, 0, ldbb, x, 0, ldx, work, 0, info);
/* 3413:     */   }
/* 3414:     */   
/* 3415:     */   public void dsbgst(String vect, String uplo, int n, int ka, int kb, double[] ab, int _ab_offset, int ldab, double[] bb, int _bb_offset, int ldbb, double[] x, int _x_offset, int ldx, double[] work, int _work_offset, intW info)
/* 3416:     */   {
/* 3417:2721 */     Dsbgst.dsbgst(vect, uplo, n, ka, kb, ab, _ab_offset, ldab, bb, _bb_offset, ldbb, x, _x_offset, ldx, work, _work_offset, info);
/* 3418:     */   }
/* 3419:     */   
/* 3420:     */   public void dsbgv(String jobz, String uplo, int n, int ka, int kb, double[] ab, int ldab, double[] bb, int ldbb, double[] w, double[] z, int ldz, double[] work, intW info)
/* 3421:     */   {
/* 3422:2726 */     Dsbgv.dsbgv(jobz, uplo, n, ka, kb, ab, 0, ldab, bb, 0, ldbb, w, 0, z, 0, ldz, work, 0, info);
/* 3423:     */   }
/* 3424:     */   
/* 3425:     */   public void dsbgv(String jobz, String uplo, int n, int ka, int kb, double[] ab, int _ab_offset, int ldab, double[] bb, int _bb_offset, int ldbb, double[] w, int _w_offset, double[] z, int _z_offset, int ldz, double[] work, int _work_offset, intW info)
/* 3426:     */   {
/* 3427:2731 */     Dsbgv.dsbgv(jobz, uplo, n, ka, kb, ab, _ab_offset, ldab, bb, _bb_offset, ldbb, w, _w_offset, z, _z_offset, ldz, work, _work_offset, info);
/* 3428:     */   }
/* 3429:     */   
/* 3430:     */   public void dsbgvd(String jobz, String uplo, int n, int ka, int kb, double[] ab, int ldab, double[] bb, int ldbb, double[] w, double[] z, int ldz, double[] work, int lwork, int[] iwork, int liwork, intW info)
/* 3431:     */   {
/* 3432:2736 */     Dsbgvd.dsbgvd(jobz, uplo, n, ka, kb, ab, 0, ldab, bb, 0, ldbb, w, 0, z, 0, ldz, work, 0, lwork, iwork, 0, liwork, info);
/* 3433:     */   }
/* 3434:     */   
/* 3435:     */   public void dsbgvd(String jobz, String uplo, int n, int ka, int kb, double[] ab, int _ab_offset, int ldab, double[] bb, int _bb_offset, int ldbb, double[] w, int _w_offset, double[] z, int _z_offset, int ldz, double[] work, int _work_offset, int lwork, int[] iwork, int _iwork_offset, int liwork, intW info)
/* 3436:     */   {
/* 3437:2741 */     Dsbgvd.dsbgvd(jobz, uplo, n, ka, kb, ab, _ab_offset, ldab, bb, _bb_offset, ldbb, w, _w_offset, z, _z_offset, ldz, work, _work_offset, lwork, iwork, _iwork_offset, liwork, info);
/* 3438:     */   }
/* 3439:     */   
/* 3440:     */   public void dsbgvx(String jobz, String range, String uplo, int n, int ka, int kb, double[] ab, int ldab, double[] bb, int ldbb, double[] q, int ldq, double vl, double vu, int il, int iu, double abstol, intW m, double[] w, double[] z, int ldz, double[] work, int[] iwork, int[] ifail, intW info)
/* 3441:     */   {
/* 3442:2746 */     Dsbgvx.dsbgvx(jobz, range, uplo, n, ka, kb, ab, 0, ldab, bb, 0, ldbb, q, 0, ldq, vl, vu, il, iu, abstol, m, w, 0, z, 0, ldz, work, 0, iwork, 0, ifail, 0, info);
/* 3443:     */   }
/* 3444:     */   
/* 3445:     */   public void dsbgvx(String jobz, String range, String uplo, int n, int ka, int kb, double[] ab, int _ab_offset, int ldab, double[] bb, int _bb_offset, int ldbb, double[] q, int _q_offset, int ldq, double vl, double vu, int il, int iu, double abstol, intW m, double[] w, int _w_offset, double[] z, int _z_offset, int ldz, double[] work, int _work_offset, int[] iwork, int _iwork_offset, int[] ifail, int _ifail_offset, intW info)
/* 3446:     */   {
/* 3447:2751 */     Dsbgvx.dsbgvx(jobz, range, uplo, n, ka, kb, ab, _ab_offset, ldab, bb, _bb_offset, ldbb, q, _q_offset, ldq, vl, vu, il, iu, abstol, m, w, _w_offset, z, _z_offset, ldz, work, _work_offset, iwork, _iwork_offset, ifail, _ifail_offset, info);
/* 3448:     */   }
/* 3449:     */   
/* 3450:     */   public void dsbtrd(String vect, String uplo, int n, int kd, double[] ab, int ldab, double[] d, double[] e, double[] q, int ldq, double[] work, intW info)
/* 3451:     */   {
/* 3452:2756 */     Dsbtrd.dsbtrd(vect, uplo, n, kd, ab, 0, ldab, d, 0, e, 0, q, 0, ldq, work, 0, info);
/* 3453:     */   }
/* 3454:     */   
/* 3455:     */   public void dsbtrd(String vect, String uplo, int n, int kd, double[] ab, int _ab_offset, int ldab, double[] d, int _d_offset, double[] e, int _e_offset, double[] q, int _q_offset, int ldq, double[] work, int _work_offset, intW info)
/* 3456:     */   {
/* 3457:2761 */     Dsbtrd.dsbtrd(vect, uplo, n, kd, ab, _ab_offset, ldab, d, _d_offset, e, _e_offset, q, _q_offset, ldq, work, _work_offset, info);
/* 3458:     */   }
/* 3459:     */   
/* 3460:     */   public void dsgesv(int n, int nrhs, double[] a, int lda, int[] ipiv, double[] b, int ldb, double[] x, int ldx, double[] work, float[] swork, intW iter, intW info)
/* 3461:     */   {
/* 3462:2766 */     Dsgesv.dsgesv(n, nrhs, a, 0, lda, ipiv, 0, b, 0, ldb, x, 0, ldx, work, 0, swork, 0, iter, info);
/* 3463:     */   }
/* 3464:     */   
/* 3465:     */   public void dsgesv(int n, int nrhs, double[] a, int _a_offset, int lda, int[] ipiv, int _ipiv_offset, double[] b, int _b_offset, int ldb, double[] x, int _x_offset, int ldx, double[] work, int _work_offset, float[] swork, int _swork_offset, intW iter, intW info)
/* 3466:     */   {
/* 3467:2771 */     Dsgesv.dsgesv(n, nrhs, a, _a_offset, lda, ipiv, _ipiv_offset, b, _b_offset, ldb, x, _x_offset, ldx, work, _work_offset, swork, _swork_offset, iter, info);
/* 3468:     */   }
/* 3469:     */   
/* 3470:     */   public void dspcon(String uplo, int n, double[] ap, int[] ipiv, double anorm, doubleW rcond, double[] work, int[] iwork, intW info)
/* 3471:     */   {
/* 3472:2776 */     Dspcon.dspcon(uplo, n, ap, 0, ipiv, 0, anorm, rcond, work, 0, iwork, 0, info);
/* 3473:     */   }
/* 3474:     */   
/* 3475:     */   public void dspcon(String uplo, int n, double[] ap, int _ap_offset, int[] ipiv, int _ipiv_offset, double anorm, doubleW rcond, double[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 3476:     */   {
/* 3477:2781 */     Dspcon.dspcon(uplo, n, ap, _ap_offset, ipiv, _ipiv_offset, anorm, rcond, work, _work_offset, iwork, _iwork_offset, info);
/* 3478:     */   }
/* 3479:     */   
/* 3480:     */   public void dspev(String jobz, String uplo, int n, double[] ap, double[] w, double[] z, int ldz, double[] work, intW info)
/* 3481:     */   {
/* 3482:2786 */     Dspev.dspev(jobz, uplo, n, ap, 0, w, 0, z, 0, ldz, work, 0, info);
/* 3483:     */   }
/* 3484:     */   
/* 3485:     */   public void dspev(String jobz, String uplo, int n, double[] ap, int _ap_offset, double[] w, int _w_offset, double[] z, int _z_offset, int ldz, double[] work, int _work_offset, intW info)
/* 3486:     */   {
/* 3487:2791 */     Dspev.dspev(jobz, uplo, n, ap, _ap_offset, w, _w_offset, z, _z_offset, ldz, work, _work_offset, info);
/* 3488:     */   }
/* 3489:     */   
/* 3490:     */   public void dspevd(String jobz, String uplo, int n, double[] ap, double[] w, double[] z, int ldz, double[] work, int lwork, int[] iwork, int liwork, intW info)
/* 3491:     */   {
/* 3492:2796 */     Dspevd.dspevd(jobz, uplo, n, ap, 0, w, 0, z, 0, ldz, work, 0, lwork, iwork, 0, liwork, info);
/* 3493:     */   }
/* 3494:     */   
/* 3495:     */   public void dspevd(String jobz, String uplo, int n, double[] ap, int _ap_offset, double[] w, int _w_offset, double[] z, int _z_offset, int ldz, double[] work, int _work_offset, int lwork, int[] iwork, int _iwork_offset, int liwork, intW info)
/* 3496:     */   {
/* 3497:2801 */     Dspevd.dspevd(jobz, uplo, n, ap, _ap_offset, w, _w_offset, z, _z_offset, ldz, work, _work_offset, lwork, iwork, _iwork_offset, liwork, info);
/* 3498:     */   }
/* 3499:     */   
/* 3500:     */   public void dspevx(String jobz, String range, String uplo, int n, double[] ap, double vl, double vu, int il, int iu, double abstol, intW m, double[] w, double[] z, int ldz, double[] work, int[] iwork, int[] ifail, intW info)
/* 3501:     */   {
/* 3502:2806 */     Dspevx.dspevx(jobz, range, uplo, n, ap, 0, vl, vu, il, iu, abstol, m, w, 0, z, 0, ldz, work, 0, iwork, 0, ifail, 0, info);
/* 3503:     */   }
/* 3504:     */   
/* 3505:     */   public void dspevx(String jobz, String range, String uplo, int n, double[] ap, int _ap_offset, double vl, double vu, int il, int iu, double abstol, intW m, double[] w, int _w_offset, double[] z, int _z_offset, int ldz, double[] work, int _work_offset, int[] iwork, int _iwork_offset, int[] ifail, int _ifail_offset, intW info)
/* 3506:     */   {
/* 3507:2811 */     Dspevx.dspevx(jobz, range, uplo, n, ap, _ap_offset, vl, vu, il, iu, abstol, m, w, _w_offset, z, _z_offset, ldz, work, _work_offset, iwork, _iwork_offset, ifail, _ifail_offset, info);
/* 3508:     */   }
/* 3509:     */   
/* 3510:     */   public void dspgst(int itype, String uplo, int n, double[] ap, double[] bp, intW info)
/* 3511:     */   {
/* 3512:2816 */     Dspgst.dspgst(itype, uplo, n, ap, 0, bp, 0, info);
/* 3513:     */   }
/* 3514:     */   
/* 3515:     */   public void dspgst(int itype, String uplo, int n, double[] ap, int _ap_offset, double[] bp, int _bp_offset, intW info)
/* 3516:     */   {
/* 3517:2821 */     Dspgst.dspgst(itype, uplo, n, ap, _ap_offset, bp, _bp_offset, info);
/* 3518:     */   }
/* 3519:     */   
/* 3520:     */   public void dspgv(int itype, String jobz, String uplo, int n, double[] ap, double[] bp, double[] w, double[] z, int ldz, double[] work, intW info)
/* 3521:     */   {
/* 3522:2826 */     Dspgv.dspgv(itype, jobz, uplo, n, ap, 0, bp, 0, w, 0, z, 0, ldz, work, 0, info);
/* 3523:     */   }
/* 3524:     */   
/* 3525:     */   public void dspgv(int itype, String jobz, String uplo, int n, double[] ap, int _ap_offset, double[] bp, int _bp_offset, double[] w, int _w_offset, double[] z, int _z_offset, int ldz, double[] work, int _work_offset, intW info)
/* 3526:     */   {
/* 3527:2831 */     Dspgv.dspgv(itype, jobz, uplo, n, ap, _ap_offset, bp, _bp_offset, w, _w_offset, z, _z_offset, ldz, work, _work_offset, info);
/* 3528:     */   }
/* 3529:     */   
/* 3530:     */   public void dspgvd(int itype, String jobz, String uplo, int n, double[] ap, double[] bp, double[] w, double[] z, int ldz, double[] work, int lwork, int[] iwork, int liwork, intW info)
/* 3531:     */   {
/* 3532:2836 */     Dspgvd.dspgvd(itype, jobz, uplo, n, ap, 0, bp, 0, w, 0, z, 0, ldz, work, 0, lwork, iwork, 0, liwork, info);
/* 3533:     */   }
/* 3534:     */   
/* 3535:     */   public void dspgvd(int itype, String jobz, String uplo, int n, double[] ap, int _ap_offset, double[] bp, int _bp_offset, double[] w, int _w_offset, double[] z, int _z_offset, int ldz, double[] work, int _work_offset, int lwork, int[] iwork, int _iwork_offset, int liwork, intW info)
/* 3536:     */   {
/* 3537:2841 */     Dspgvd.dspgvd(itype, jobz, uplo, n, ap, _ap_offset, bp, _bp_offset, w, _w_offset, z, _z_offset, ldz, work, _work_offset, lwork, iwork, _iwork_offset, liwork, info);
/* 3538:     */   }
/* 3539:     */   
/* 3540:     */   public void dspgvx(int itype, String jobz, String range, String uplo, int n, double[] ap, double[] bp, double vl, double vu, int il, int iu, double abstol, intW m, double[] w, double[] z, int ldz, double[] work, int[] iwork, int[] ifail, intW info)
/* 3541:     */   {
/* 3542:2846 */     Dspgvx.dspgvx(itype, jobz, range, uplo, n, ap, 0, bp, 0, vl, vu, il, iu, abstol, m, w, 0, z, 0, ldz, work, 0, iwork, 0, ifail, 0, info);
/* 3543:     */   }
/* 3544:     */   
/* 3545:     */   public void dspgvx(int itype, String jobz, String range, String uplo, int n, double[] ap, int _ap_offset, double[] bp, int _bp_offset, double vl, double vu, int il, int iu, double abstol, intW m, double[] w, int _w_offset, double[] z, int _z_offset, int ldz, double[] work, int _work_offset, int[] iwork, int _iwork_offset, int[] ifail, int _ifail_offset, intW info)
/* 3546:     */   {
/* 3547:2851 */     Dspgvx.dspgvx(itype, jobz, range, uplo, n, ap, _ap_offset, bp, _bp_offset, vl, vu, il, iu, abstol, m, w, _w_offset, z, _z_offset, ldz, work, _work_offset, iwork, _iwork_offset, ifail, _ifail_offset, info);
/* 3548:     */   }
/* 3549:     */   
/* 3550:     */   public void dsprfs(String uplo, int n, int nrhs, double[] ap, double[] afp, int[] ipiv, double[] b, int ldb, double[] x, int ldx, double[] ferr, double[] berr, double[] work, int[] iwork, intW info)
/* 3551:     */   {
/* 3552:2856 */     Dsprfs.dsprfs(uplo, n, nrhs, ap, 0, afp, 0, ipiv, 0, b, 0, ldb, x, 0, ldx, ferr, 0, berr, 0, work, 0, iwork, 0, info);
/* 3553:     */   }
/* 3554:     */   
/* 3555:     */   public void dsprfs(String uplo, int n, int nrhs, double[] ap, int _ap_offset, double[] afp, int _afp_offset, int[] ipiv, int _ipiv_offset, double[] b, int _b_offset, int ldb, double[] x, int _x_offset, int ldx, double[] ferr, int _ferr_offset, double[] berr, int _berr_offset, double[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 3556:     */   {
/* 3557:2861 */     Dsprfs.dsprfs(uplo, n, nrhs, ap, _ap_offset, afp, _afp_offset, ipiv, _ipiv_offset, b, _b_offset, ldb, x, _x_offset, ldx, ferr, _ferr_offset, berr, _berr_offset, work, _work_offset, iwork, _iwork_offset, info);
/* 3558:     */   }
/* 3559:     */   
/* 3560:     */   public void dspsv(String uplo, int n, int nrhs, double[] ap, int[] ipiv, double[] b, int ldb, intW info)
/* 3561:     */   {
/* 3562:2866 */     Dspsv.dspsv(uplo, n, nrhs, ap, 0, ipiv, 0, b, 0, ldb, info);
/* 3563:     */   }
/* 3564:     */   
/* 3565:     */   public void dspsv(String uplo, int n, int nrhs, double[] ap, int _ap_offset, int[] ipiv, int _ipiv_offset, double[] b, int _b_offset, int ldb, intW info)
/* 3566:     */   {
/* 3567:2871 */     Dspsv.dspsv(uplo, n, nrhs, ap, _ap_offset, ipiv, _ipiv_offset, b, _b_offset, ldb, info);
/* 3568:     */   }
/* 3569:     */   
/* 3570:     */   public void dspsvx(String fact, String uplo, int n, int nrhs, double[] ap, double[] afp, int[] ipiv, double[] b, int ldb, double[] x, int ldx, doubleW rcond, double[] ferr, double[] berr, double[] work, int[] iwork, intW info)
/* 3571:     */   {
/* 3572:2876 */     Dspsvx.dspsvx(fact, uplo, n, nrhs, ap, 0, afp, 0, ipiv, 0, b, 0, ldb, x, 0, ldx, rcond, ferr, 0, berr, 0, work, 0, iwork, 0, info);
/* 3573:     */   }
/* 3574:     */   
/* 3575:     */   public void dspsvx(String fact, String uplo, int n, int nrhs, double[] ap, int _ap_offset, double[] afp, int _afp_offset, int[] ipiv, int _ipiv_offset, double[] b, int _b_offset, int ldb, double[] x, int _x_offset, int ldx, doubleW rcond, double[] ferr, int _ferr_offset, double[] berr, int _berr_offset, double[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 3576:     */   {
/* 3577:2881 */     Dspsvx.dspsvx(fact, uplo, n, nrhs, ap, _ap_offset, afp, _afp_offset, ipiv, _ipiv_offset, b, _b_offset, ldb, x, _x_offset, ldx, rcond, ferr, _ferr_offset, berr, _berr_offset, work, _work_offset, iwork, _iwork_offset, info);
/* 3578:     */   }
/* 3579:     */   
/* 3580:     */   public void dsptrd(String uplo, int n, double[] ap, double[] d, double[] e, double[] tau, intW info)
/* 3581:     */   {
/* 3582:2886 */     Dsptrd.dsptrd(uplo, n, ap, 0, d, 0, e, 0, tau, 0, info);
/* 3583:     */   }
/* 3584:     */   
/* 3585:     */   public void dsptrd(String uplo, int n, double[] ap, int _ap_offset, double[] d, int _d_offset, double[] e, int _e_offset, double[] tau, int _tau_offset, intW info)
/* 3586:     */   {
/* 3587:2891 */     Dsptrd.dsptrd(uplo, n, ap, _ap_offset, d, _d_offset, e, _e_offset, tau, _tau_offset, info);
/* 3588:     */   }
/* 3589:     */   
/* 3590:     */   public void dsptrf(String uplo, int n, double[] ap, int[] ipiv, intW info)
/* 3591:     */   {
/* 3592:2896 */     Dsptrf.dsptrf(uplo, n, ap, 0, ipiv, 0, info);
/* 3593:     */   }
/* 3594:     */   
/* 3595:     */   public void dsptrf(String uplo, int n, double[] ap, int _ap_offset, int[] ipiv, int _ipiv_offset, intW info)
/* 3596:     */   {
/* 3597:2901 */     Dsptrf.dsptrf(uplo, n, ap, _ap_offset, ipiv, _ipiv_offset, info);
/* 3598:     */   }
/* 3599:     */   
/* 3600:     */   public void dsptri(String uplo, int n, double[] ap, int[] ipiv, double[] work, intW info)
/* 3601:     */   {
/* 3602:2906 */     Dsptri.dsptri(uplo, n, ap, 0, ipiv, 0, work, 0, info);
/* 3603:     */   }
/* 3604:     */   
/* 3605:     */   public void dsptri(String uplo, int n, double[] ap, int _ap_offset, int[] ipiv, int _ipiv_offset, double[] work, int _work_offset, intW info)
/* 3606:     */   {
/* 3607:2911 */     Dsptri.dsptri(uplo, n, ap, _ap_offset, ipiv, _ipiv_offset, work, _work_offset, info);
/* 3608:     */   }
/* 3609:     */   
/* 3610:     */   public void dsptrs(String uplo, int n, int nrhs, double[] ap, int[] ipiv, double[] b, int ldb, intW info)
/* 3611:     */   {
/* 3612:2916 */     Dsptrs.dsptrs(uplo, n, nrhs, ap, 0, ipiv, 0, b, 0, ldb, info);
/* 3613:     */   }
/* 3614:     */   
/* 3615:     */   public void dsptrs(String uplo, int n, int nrhs, double[] ap, int _ap_offset, int[] ipiv, int _ipiv_offset, double[] b, int _b_offset, int ldb, intW info)
/* 3616:     */   {
/* 3617:2921 */     Dsptrs.dsptrs(uplo, n, nrhs, ap, _ap_offset, ipiv, _ipiv_offset, b, _b_offset, ldb, info);
/* 3618:     */   }
/* 3619:     */   
/* 3620:     */   public void dstebz(String range, String order, int n, double vl, double vu, int il, int iu, double abstol, double[] d, double[] e, intW m, intW nsplit, double[] w, int[] iblock, int[] isplit, double[] work, int[] iwork, intW info)
/* 3621:     */   {
/* 3622:2926 */     Dstebz.dstebz(range, order, n, vl, vu, il, iu, abstol, d, 0, e, 0, m, nsplit, w, 0, iblock, 0, isplit, 0, work, 0, iwork, 0, info);
/* 3623:     */   }
/* 3624:     */   
/* 3625:     */   public void dstebz(String range, String order, int n, double vl, double vu, int il, int iu, double abstol, double[] d, int _d_offset, double[] e, int _e_offset, intW m, intW nsplit, double[] w, int _w_offset, int[] iblock, int _iblock_offset, int[] isplit, int _isplit_offset, double[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 3626:     */   {
/* 3627:2931 */     Dstebz.dstebz(range, order, n, vl, vu, il, iu, abstol, d, _d_offset, e, _e_offset, m, nsplit, w, _w_offset, iblock, _iblock_offset, isplit, _isplit_offset, work, _work_offset, iwork, _iwork_offset, info);
/* 3628:     */   }
/* 3629:     */   
/* 3630:     */   public void dstedc(String compz, int n, double[] d, double[] e, double[] z, int ldz, double[] work, int lwork, int[] iwork, int liwork, intW info)
/* 3631:     */   {
/* 3632:2936 */     Dstedc.dstedc(compz, n, d, 0, e, 0, z, 0, ldz, work, 0, lwork, iwork, 0, liwork, info);
/* 3633:     */   }
/* 3634:     */   
/* 3635:     */   public void dstedc(String compz, int n, double[] d, int _d_offset, double[] e, int _e_offset, double[] z, int _z_offset, int ldz, double[] work, int _work_offset, int lwork, int[] iwork, int _iwork_offset, int liwork, intW info)
/* 3636:     */   {
/* 3637:2941 */     Dstedc.dstedc(compz, n, d, _d_offset, e, _e_offset, z, _z_offset, ldz, work, _work_offset, lwork, iwork, _iwork_offset, liwork, info);
/* 3638:     */   }
/* 3639:     */   
/* 3640:     */   public void dstegr(String jobz, String range, int n, double[] d, double[] e, double vl, double vu, int il, int iu, double abstol, intW m, double[] w, double[] z, int ldz, int[] isuppz, double[] work, int lwork, int[] iwork, int liwork, intW info)
/* 3641:     */   {
/* 3642:2946 */     Dstegr.dstegr(jobz, range, n, d, 0, e, 0, vl, vu, il, iu, abstol, m, w, 0, z, 0, ldz, isuppz, 0, work, 0, lwork, iwork, 0, liwork, info);
/* 3643:     */   }
/* 3644:     */   
/* 3645:     */   public void dstegr(String jobz, String range, int n, double[] d, int _d_offset, double[] e, int _e_offset, double vl, double vu, int il, int iu, double abstol, intW m, double[] w, int _w_offset, double[] z, int _z_offset, int ldz, int[] isuppz, int _isuppz_offset, double[] work, int _work_offset, int lwork, int[] iwork, int _iwork_offset, int liwork, intW info)
/* 3646:     */   {
/* 3647:2951 */     Dstegr.dstegr(jobz, range, n, d, _d_offset, e, _e_offset, vl, vu, il, iu, abstol, m, w, _w_offset, z, _z_offset, ldz, isuppz, _isuppz_offset, work, _work_offset, lwork, iwork, _iwork_offset, liwork, info);
/* 3648:     */   }
/* 3649:     */   
/* 3650:     */   public void dstein(int n, double[] d, double[] e, int m, double[] w, int[] iblock, int[] isplit, double[] z, int ldz, double[] work, int[] iwork, int[] ifail, intW info)
/* 3651:     */   {
/* 3652:2956 */     Dstein.dstein(n, d, 0, e, 0, m, w, 0, iblock, 0, isplit, 0, z, 0, ldz, work, 0, iwork, 0, ifail, 0, info);
/* 3653:     */   }
/* 3654:     */   
/* 3655:     */   public void dstein(int n, double[] d, int _d_offset, double[] e, int _e_offset, int m, double[] w, int _w_offset, int[] iblock, int _iblock_offset, int[] isplit, int _isplit_offset, double[] z, int _z_offset, int ldz, double[] work, int _work_offset, int[] iwork, int _iwork_offset, int[] ifail, int _ifail_offset, intW info)
/* 3656:     */   {
/* 3657:2961 */     Dstein.dstein(n, d, _d_offset, e, _e_offset, m, w, _w_offset, iblock, _iblock_offset, isplit, _isplit_offset, z, _z_offset, ldz, work, _work_offset, iwork, _iwork_offset, ifail, _ifail_offset, info);
/* 3658:     */   }
/* 3659:     */   
/* 3660:     */   public void dstemr(String jobz, String range, int n, double[] d, double[] e, double vl, double vu, int il, int iu, intW m, double[] w, double[] z, int ldz, int nzc, int[] isuppz, booleanW tryrac, double[] work, int lwork, int[] iwork, int liwork, intW info)
/* 3661:     */   {
/* 3662:2966 */     Dstemr.dstemr(jobz, range, n, d, 0, e, 0, vl, vu, il, iu, m, w, 0, z, 0, ldz, nzc, isuppz, 0, tryrac, work, 0, lwork, iwork, 0, liwork, info);
/* 3663:     */   }
/* 3664:     */   
/* 3665:     */   public void dstemr(String jobz, String range, int n, double[] d, int _d_offset, double[] e, int _e_offset, double vl, double vu, int il, int iu, intW m, double[] w, int _w_offset, double[] z, int _z_offset, int ldz, int nzc, int[] isuppz, int _isuppz_offset, booleanW tryrac, double[] work, int _work_offset, int lwork, int[] iwork, int _iwork_offset, int liwork, intW info)
/* 3666:     */   {
/* 3667:2971 */     Dstemr.dstemr(jobz, range, n, d, _d_offset, e, _e_offset, vl, vu, il, iu, m, w, _w_offset, z, _z_offset, ldz, nzc, isuppz, _isuppz_offset, tryrac, work, _work_offset, lwork, iwork, _iwork_offset, liwork, info);
/* 3668:     */   }
/* 3669:     */   
/* 3670:     */   public void dsteqr(String compz, int n, double[] d, double[] e, double[] z, int ldz, double[] work, intW info)
/* 3671:     */   {
/* 3672:2976 */     Dsteqr.dsteqr(compz, n, d, 0, e, 0, z, 0, ldz, work, 0, info);
/* 3673:     */   }
/* 3674:     */   
/* 3675:     */   public void dsteqr(String compz, int n, double[] d, int _d_offset, double[] e, int _e_offset, double[] z, int _z_offset, int ldz, double[] work, int _work_offset, intW info)
/* 3676:     */   {
/* 3677:2981 */     Dsteqr.dsteqr(compz, n, d, _d_offset, e, _e_offset, z, _z_offset, ldz, work, _work_offset, info);
/* 3678:     */   }
/* 3679:     */   
/* 3680:     */   public void dsterf(int n, double[] d, double[] e, intW info)
/* 3681:     */   {
/* 3682:2986 */     Dsterf.dsterf(n, d, 0, e, 0, info);
/* 3683:     */   }
/* 3684:     */   
/* 3685:     */   public void dsterf(int n, double[] d, int _d_offset, double[] e, int _e_offset, intW info)
/* 3686:     */   {
/* 3687:2991 */     Dsterf.dsterf(n, d, _d_offset, e, _e_offset, info);
/* 3688:     */   }
/* 3689:     */   
/* 3690:     */   public void dstev(String jobz, int n, double[] d, double[] e, double[] z, int ldz, double[] work, intW info)
/* 3691:     */   {
/* 3692:2996 */     Dstev.dstev(jobz, n, d, 0, e, 0, z, 0, ldz, work, 0, info);
/* 3693:     */   }
/* 3694:     */   
/* 3695:     */   public void dstev(String jobz, int n, double[] d, int _d_offset, double[] e, int _e_offset, double[] z, int _z_offset, int ldz, double[] work, int _work_offset, intW info)
/* 3696:     */   {
/* 3697:3001 */     Dstev.dstev(jobz, n, d, _d_offset, e, _e_offset, z, _z_offset, ldz, work, _work_offset, info);
/* 3698:     */   }
/* 3699:     */   
/* 3700:     */   public void dstevd(String jobz, int n, double[] d, double[] e, double[] z, int ldz, double[] work, int lwork, int[] iwork, int liwork, intW info)
/* 3701:     */   {
/* 3702:3006 */     Dstevd.dstevd(jobz, n, d, 0, e, 0, z, 0, ldz, work, 0, lwork, iwork, 0, liwork, info);
/* 3703:     */   }
/* 3704:     */   
/* 3705:     */   public void dstevd(String jobz, int n, double[] d, int _d_offset, double[] e, int _e_offset, double[] z, int _z_offset, int ldz, double[] work, int _work_offset, int lwork, int[] iwork, int _iwork_offset, int liwork, intW info)
/* 3706:     */   {
/* 3707:3011 */     Dstevd.dstevd(jobz, n, d, _d_offset, e, _e_offset, z, _z_offset, ldz, work, _work_offset, lwork, iwork, _iwork_offset, liwork, info);
/* 3708:     */   }
/* 3709:     */   
/* 3710:     */   public void dstevr(String jobz, String range, int n, double[] d, double[] e, double vl, double vu, int il, int iu, double abstol, intW m, double[] w, double[] z, int ldz, int[] isuppz, double[] work, int lwork, int[] iwork, int liwork, intW info)
/* 3711:     */   {
/* 3712:3016 */     Dstevr.dstevr(jobz, range, n, d, 0, e, 0, vl, vu, il, iu, abstol, m, w, 0, z, 0, ldz, isuppz, 0, work, 0, lwork, iwork, 0, liwork, info);
/* 3713:     */   }
/* 3714:     */   
/* 3715:     */   public void dstevr(String jobz, String range, int n, double[] d, int _d_offset, double[] e, int _e_offset, double vl, double vu, int il, int iu, double abstol, intW m, double[] w, int _w_offset, double[] z, int _z_offset, int ldz, int[] isuppz, int _isuppz_offset, double[] work, int _work_offset, int lwork, int[] iwork, int _iwork_offset, int liwork, intW info)
/* 3716:     */   {
/* 3717:3021 */     Dstevr.dstevr(jobz, range, n, d, _d_offset, e, _e_offset, vl, vu, il, iu, abstol, m, w, _w_offset, z, _z_offset, ldz, isuppz, _isuppz_offset, work, _work_offset, lwork, iwork, _iwork_offset, liwork, info);
/* 3718:     */   }
/* 3719:     */   
/* 3720:     */   public void dstevx(String jobz, String range, int n, double[] d, double[] e, double vl, double vu, int il, int iu, double abstol, intW m, double[] w, double[] z, int ldz, double[] work, int[] iwork, int[] ifail, intW info)
/* 3721:     */   {
/* 3722:3026 */     Dstevx.dstevx(jobz, range, n, d, 0, e, 0, vl, vu, il, iu, abstol, m, w, 0, z, 0, ldz, work, 0, iwork, 0, ifail, 0, info);
/* 3723:     */   }
/* 3724:     */   
/* 3725:     */   public void dstevx(String jobz, String range, int n, double[] d, int _d_offset, double[] e, int _e_offset, double vl, double vu, int il, int iu, double abstol, intW m, double[] w, int _w_offset, double[] z, int _z_offset, int ldz, double[] work, int _work_offset, int[] iwork, int _iwork_offset, int[] ifail, int _ifail_offset, intW info)
/* 3726:     */   {
/* 3727:3031 */     Dstevx.dstevx(jobz, range, n, d, _d_offset, e, _e_offset, vl, vu, il, iu, abstol, m, w, _w_offset, z, _z_offset, ldz, work, _work_offset, iwork, _iwork_offset, ifail, _ifail_offset, info);
/* 3728:     */   }
/* 3729:     */   
/* 3730:     */   public void dsycon(String uplo, int n, double[] a, int lda, int[] ipiv, double anorm, doubleW rcond, double[] work, int[] iwork, intW info)
/* 3731:     */   {
/* 3732:3036 */     Dsycon.dsycon(uplo, n, a, 0, lda, ipiv, 0, anorm, rcond, work, 0, iwork, 0, info);
/* 3733:     */   }
/* 3734:     */   
/* 3735:     */   public void dsycon(String uplo, int n, double[] a, int _a_offset, int lda, int[] ipiv, int _ipiv_offset, double anorm, doubleW rcond, double[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 3736:     */   {
/* 3737:3041 */     Dsycon.dsycon(uplo, n, a, _a_offset, lda, ipiv, _ipiv_offset, anorm, rcond, work, _work_offset, iwork, _iwork_offset, info);
/* 3738:     */   }
/* 3739:     */   
/* 3740:     */   public void dsyev(String jobz, String uplo, int n, double[] a, int lda, double[] w, double[] work, int lwork, intW info)
/* 3741:     */   {
/* 3742:3046 */     Dsyev.dsyev(jobz, uplo, n, a, 0, lda, w, 0, work, 0, lwork, info);
/* 3743:     */   }
/* 3744:     */   
/* 3745:     */   public void dsyev(String jobz, String uplo, int n, double[] a, int _a_offset, int lda, double[] w, int _w_offset, double[] work, int _work_offset, int lwork, intW info)
/* 3746:     */   {
/* 3747:3051 */     Dsyev.dsyev(jobz, uplo, n, a, _a_offset, lda, w, _w_offset, work, _work_offset, lwork, info);
/* 3748:     */   }
/* 3749:     */   
/* 3750:     */   public void dsyevd(String jobz, String uplo, int n, double[] a, int lda, double[] w, double[] work, int lwork, int[] iwork, int liwork, intW info)
/* 3751:     */   {
/* 3752:3056 */     Dsyevd.dsyevd(jobz, uplo, n, a, 0, lda, w, 0, work, 0, lwork, iwork, 0, liwork, info);
/* 3753:     */   }
/* 3754:     */   
/* 3755:     */   public void dsyevd(String jobz, String uplo, int n, double[] a, int _a_offset, int lda, double[] w, int _w_offset, double[] work, int _work_offset, int lwork, int[] iwork, int _iwork_offset, int liwork, intW info)
/* 3756:     */   {
/* 3757:3061 */     Dsyevd.dsyevd(jobz, uplo, n, a, _a_offset, lda, w, _w_offset, work, _work_offset, lwork, iwork, _iwork_offset, liwork, info);
/* 3758:     */   }
/* 3759:     */   
/* 3760:     */   public void dsyevr(String jobz, String range, String uplo, int n, double[] a, int lda, double vl, double vu, int il, int iu, double abstol, intW m, double[] w, double[] z, int ldz, int[] isuppz, double[] work, int lwork, int[] iwork, int liwork, intW info)
/* 3761:     */   {
/* 3762:3066 */     Dsyevr.dsyevr(jobz, range, uplo, n, a, 0, lda, vl, vu, il, iu, abstol, m, w, 0, z, 0, ldz, isuppz, 0, work, 0, lwork, iwork, 0, liwork, info);
/* 3763:     */   }
/* 3764:     */   
/* 3765:     */   public void dsyevr(String jobz, String range, String uplo, int n, double[] a, int _a_offset, int lda, double vl, double vu, int il, int iu, double abstol, intW m, double[] w, int _w_offset, double[] z, int _z_offset, int ldz, int[] isuppz, int _isuppz_offset, double[] work, int _work_offset, int lwork, int[] iwork, int _iwork_offset, int liwork, intW info)
/* 3766:     */   {
/* 3767:3071 */     Dsyevr.dsyevr(jobz, range, uplo, n, a, _a_offset, lda, vl, vu, il, iu, abstol, m, w, _w_offset, z, _z_offset, ldz, isuppz, _isuppz_offset, work, _work_offset, lwork, iwork, _iwork_offset, liwork, info);
/* 3768:     */   }
/* 3769:     */   
/* 3770:     */   public void dsyevx(String jobz, String range, String uplo, int n, double[] a, int lda, double vl, double vu, int il, int iu, double abstol, intW m, double[] w, double[] z, int ldz, double[] work, int lwork, int[] iwork, int[] ifail, intW info)
/* 3771:     */   {
/* 3772:3076 */     Dsyevx.dsyevx(jobz, range, uplo, n, a, 0, lda, vl, vu, il, iu, abstol, m, w, 0, z, 0, ldz, work, 0, lwork, iwork, 0, ifail, 0, info);
/* 3773:     */   }
/* 3774:     */   
/* 3775:     */   public void dsyevx(String jobz, String range, String uplo, int n, double[] a, int _a_offset, int lda, double vl, double vu, int il, int iu, double abstol, intW m, double[] w, int _w_offset, double[] z, int _z_offset, int ldz, double[] work, int _work_offset, int lwork, int[] iwork, int _iwork_offset, int[] ifail, int _ifail_offset, intW info)
/* 3776:     */   {
/* 3777:3081 */     Dsyevx.dsyevx(jobz, range, uplo, n, a, _a_offset, lda, vl, vu, il, iu, abstol, m, w, _w_offset, z, _z_offset, ldz, work, _work_offset, lwork, iwork, _iwork_offset, ifail, _ifail_offset, info);
/* 3778:     */   }
/* 3779:     */   
/* 3780:     */   public void dsygs2(int itype, String uplo, int n, double[] a, int lda, double[] b, int ldb, intW info)
/* 3781:     */   {
/* 3782:3086 */     Dsygs2.dsygs2(itype, uplo, n, a, 0, lda, b, 0, ldb, info);
/* 3783:     */   }
/* 3784:     */   
/* 3785:     */   public void dsygs2(int itype, String uplo, int n, double[] a, int _a_offset, int lda, double[] b, int _b_offset, int ldb, intW info)
/* 3786:     */   {
/* 3787:3091 */     Dsygs2.dsygs2(itype, uplo, n, a, _a_offset, lda, b, _b_offset, ldb, info);
/* 3788:     */   }
/* 3789:     */   
/* 3790:     */   public void dsygst(int itype, String uplo, int n, double[] a, int lda, double[] b, int ldb, intW info)
/* 3791:     */   {
/* 3792:3096 */     Dsygst.dsygst(itype, uplo, n, a, 0, lda, b, 0, ldb, info);
/* 3793:     */   }
/* 3794:     */   
/* 3795:     */   public void dsygst(int itype, String uplo, int n, double[] a, int _a_offset, int lda, double[] b, int _b_offset, int ldb, intW info)
/* 3796:     */   {
/* 3797:3101 */     Dsygst.dsygst(itype, uplo, n, a, _a_offset, lda, b, _b_offset, ldb, info);
/* 3798:     */   }
/* 3799:     */   
/* 3800:     */   public void dsygv(int itype, String jobz, String uplo, int n, double[] a, int lda, double[] b, int ldb, double[] w, double[] work, int lwork, intW info)
/* 3801:     */   {
/* 3802:3106 */     Dsygv.dsygv(itype, jobz, uplo, n, a, 0, lda, b, 0, ldb, w, 0, work, 0, lwork, info);
/* 3803:     */   }
/* 3804:     */   
/* 3805:     */   public void dsygv(int itype, String jobz, String uplo, int n, double[] a, int _a_offset, int lda, double[] b, int _b_offset, int ldb, double[] w, int _w_offset, double[] work, int _work_offset, int lwork, intW info)
/* 3806:     */   {
/* 3807:3111 */     Dsygv.dsygv(itype, jobz, uplo, n, a, _a_offset, lda, b, _b_offset, ldb, w, _w_offset, work, _work_offset, lwork, info);
/* 3808:     */   }
/* 3809:     */   
/* 3810:     */   public void dsygvd(int itype, String jobz, String uplo, int n, double[] a, int lda, double[] b, int ldb, double[] w, double[] work, int lwork, int[] iwork, int liwork, intW info)
/* 3811:     */   {
/* 3812:3116 */     Dsygvd.dsygvd(itype, jobz, uplo, n, a, 0, lda, b, 0, ldb, w, 0, work, 0, lwork, iwork, 0, liwork, info);
/* 3813:     */   }
/* 3814:     */   
/* 3815:     */   public void dsygvd(int itype, String jobz, String uplo, int n, double[] a, int _a_offset, int lda, double[] b, int _b_offset, int ldb, double[] w, int _w_offset, double[] work, int _work_offset, int lwork, int[] iwork, int _iwork_offset, int liwork, intW info)
/* 3816:     */   {
/* 3817:3121 */     Dsygvd.dsygvd(itype, jobz, uplo, n, a, _a_offset, lda, b, _b_offset, ldb, w, _w_offset, work, _work_offset, lwork, iwork, _iwork_offset, liwork, info);
/* 3818:     */   }
/* 3819:     */   
/* 3820:     */   public void dsygvx(int itype, String jobz, String range, String uplo, int n, double[] a, int lda, double[] b, int ldb, double vl, double vu, int il, int iu, double abstol, intW m, double[] w, double[] z, int ldz, double[] work, int lwork, int[] iwork, int[] ifail, intW info)
/* 3821:     */   {
/* 3822:3126 */     Dsygvx.dsygvx(itype, jobz, range, uplo, n, a, 0, lda, b, 0, ldb, vl, vu, il, iu, abstol, m, w, 0, z, 0, ldz, work, 0, lwork, iwork, 0, ifail, 0, info);
/* 3823:     */   }
/* 3824:     */   
/* 3825:     */   public void dsygvx(int itype, String jobz, String range, String uplo, int n, double[] a, int _a_offset, int lda, double[] b, int _b_offset, int ldb, double vl, double vu, int il, int iu, double abstol, intW m, double[] w, int _w_offset, double[] z, int _z_offset, int ldz, double[] work, int _work_offset, int lwork, int[] iwork, int _iwork_offset, int[] ifail, int _ifail_offset, intW info)
/* 3826:     */   {
/* 3827:3131 */     Dsygvx.dsygvx(itype, jobz, range, uplo, n, a, _a_offset, lda, b, _b_offset, ldb, vl, vu, il, iu, abstol, m, w, _w_offset, z, _z_offset, ldz, work, _work_offset, lwork, iwork, _iwork_offset, ifail, _ifail_offset, info);
/* 3828:     */   }
/* 3829:     */   
/* 3830:     */   public void dsyrfs(String uplo, int n, int nrhs, double[] a, int lda, double[] af, int ldaf, int[] ipiv, double[] b, int ldb, double[] x, int ldx, double[] ferr, double[] berr, double[] work, int[] iwork, intW info)
/* 3831:     */   {
/* 3832:3136 */     Dsyrfs.dsyrfs(uplo, n, nrhs, a, 0, lda, af, 0, ldaf, ipiv, 0, b, 0, ldb, x, 0, ldx, ferr, 0, berr, 0, work, 0, iwork, 0, info);
/* 3833:     */   }
/* 3834:     */   
/* 3835:     */   public void dsyrfs(String uplo, int n, int nrhs, double[] a, int _a_offset, int lda, double[] af, int _af_offset, int ldaf, int[] ipiv, int _ipiv_offset, double[] b, int _b_offset, int ldb, double[] x, int _x_offset, int ldx, double[] ferr, int _ferr_offset, double[] berr, int _berr_offset, double[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 3836:     */   {
/* 3837:3141 */     Dsyrfs.dsyrfs(uplo, n, nrhs, a, _a_offset, lda, af, _af_offset, ldaf, ipiv, _ipiv_offset, b, _b_offset, ldb, x, _x_offset, ldx, ferr, _ferr_offset, berr, _berr_offset, work, _work_offset, iwork, _iwork_offset, info);
/* 3838:     */   }
/* 3839:     */   
/* 3840:     */   public void dsysv(String uplo, int n, int nrhs, double[] a, int lda, int[] ipiv, double[] b, int ldb, double[] work, int lwork, intW info)
/* 3841:     */   {
/* 3842:3146 */     Dsysv.dsysv(uplo, n, nrhs, a, 0, lda, ipiv, 0, b, 0, ldb, work, 0, lwork, info);
/* 3843:     */   }
/* 3844:     */   
/* 3845:     */   public void dsysv(String uplo, int n, int nrhs, double[] a, int _a_offset, int lda, int[] ipiv, int _ipiv_offset, double[] b, int _b_offset, int ldb, double[] work, int _work_offset, int lwork, intW info)
/* 3846:     */   {
/* 3847:3151 */     Dsysv.dsysv(uplo, n, nrhs, a, _a_offset, lda, ipiv, _ipiv_offset, b, _b_offset, ldb, work, _work_offset, lwork, info);
/* 3848:     */   }
/* 3849:     */   
/* 3850:     */   public void dsysvx(String fact, String uplo, int n, int nrhs, double[] a, int lda, double[] af, int ldaf, int[] ipiv, double[] b, int ldb, double[] x, int ldx, doubleW rcond, double[] ferr, double[] berr, double[] work, int lwork, int[] iwork, intW info)
/* 3851:     */   {
/* 3852:3156 */     Dsysvx.dsysvx(fact, uplo, n, nrhs, a, 0, lda, af, 0, ldaf, ipiv, 0, b, 0, ldb, x, 0, ldx, rcond, ferr, 0, berr, 0, work, 0, lwork, iwork, 0, info);
/* 3853:     */   }
/* 3854:     */   
/* 3855:     */   public void dsysvx(String fact, String uplo, int n, int nrhs, double[] a, int _a_offset, int lda, double[] af, int _af_offset, int ldaf, int[] ipiv, int _ipiv_offset, double[] b, int _b_offset, int ldb, double[] x, int _x_offset, int ldx, doubleW rcond, double[] ferr, int _ferr_offset, double[] berr, int _berr_offset, double[] work, int _work_offset, int lwork, int[] iwork, int _iwork_offset, intW info)
/* 3856:     */   {
/* 3857:3161 */     Dsysvx.dsysvx(fact, uplo, n, nrhs, a, _a_offset, lda, af, _af_offset, ldaf, ipiv, _ipiv_offset, b, _b_offset, ldb, x, _x_offset, ldx, rcond, ferr, _ferr_offset, berr, _berr_offset, work, _work_offset, lwork, iwork, _iwork_offset, info);
/* 3858:     */   }
/* 3859:     */   
/* 3860:     */   public void dsytd2(String uplo, int n, double[] a, int lda, double[] d, double[] e, double[] tau, intW info)
/* 3861:     */   {
/* 3862:3166 */     Dsytd2.dsytd2(uplo, n, a, 0, lda, d, 0, e, 0, tau, 0, info);
/* 3863:     */   }
/* 3864:     */   
/* 3865:     */   public void dsytd2(String uplo, int n, double[] a, int _a_offset, int lda, double[] d, int _d_offset, double[] e, int _e_offset, double[] tau, int _tau_offset, intW info)
/* 3866:     */   {
/* 3867:3171 */     Dsytd2.dsytd2(uplo, n, a, _a_offset, lda, d, _d_offset, e, _e_offset, tau, _tau_offset, info);
/* 3868:     */   }
/* 3869:     */   
/* 3870:     */   public void dsytf2(String uplo, int n, double[] a, int lda, int[] ipiv, intW info)
/* 3871:     */   {
/* 3872:3176 */     Dsytf2.dsytf2(uplo, n, a, 0, lda, ipiv, 0, info);
/* 3873:     */   }
/* 3874:     */   
/* 3875:     */   public void dsytf2(String uplo, int n, double[] a, int _a_offset, int lda, int[] ipiv, int _ipiv_offset, intW info)
/* 3876:     */   {
/* 3877:3181 */     Dsytf2.dsytf2(uplo, n, a, _a_offset, lda, ipiv, _ipiv_offset, info);
/* 3878:     */   }
/* 3879:     */   
/* 3880:     */   public void dsytrd(String uplo, int n, double[] a, int lda, double[] d, double[] e, double[] tau, double[] work, int lwork, intW info)
/* 3881:     */   {
/* 3882:3186 */     Dsytrd.dsytrd(uplo, n, a, 0, lda, d, 0, e, 0, tau, 0, work, 0, lwork, info);
/* 3883:     */   }
/* 3884:     */   
/* 3885:     */   public void dsytrd(String uplo, int n, double[] a, int _a_offset, int lda, double[] d, int _d_offset, double[] e, int _e_offset, double[] tau, int _tau_offset, double[] work, int _work_offset, int lwork, intW info)
/* 3886:     */   {
/* 3887:3191 */     Dsytrd.dsytrd(uplo, n, a, _a_offset, lda, d, _d_offset, e, _e_offset, tau, _tau_offset, work, _work_offset, lwork, info);
/* 3888:     */   }
/* 3889:     */   
/* 3890:     */   public void dsytrf(String uplo, int n, double[] a, int lda, int[] ipiv, double[] work, int lwork, intW info)
/* 3891:     */   {
/* 3892:3196 */     Dsytrf.dsytrf(uplo, n, a, 0, lda, ipiv, 0, work, 0, lwork, info);
/* 3893:     */   }
/* 3894:     */   
/* 3895:     */   public void dsytrf(String uplo, int n, double[] a, int _a_offset, int lda, int[] ipiv, int _ipiv_offset, double[] work, int _work_offset, int lwork, intW info)
/* 3896:     */   {
/* 3897:3201 */     Dsytrf.dsytrf(uplo, n, a, _a_offset, lda, ipiv, _ipiv_offset, work, _work_offset, lwork, info);
/* 3898:     */   }
/* 3899:     */   
/* 3900:     */   public void dsytri(String uplo, int n, double[] a, int lda, int[] ipiv, double[] work, intW info)
/* 3901:     */   {
/* 3902:3206 */     Dsytri.dsytri(uplo, n, a, 0, lda, ipiv, 0, work, 0, info);
/* 3903:     */   }
/* 3904:     */   
/* 3905:     */   public void dsytri(String uplo, int n, double[] a, int _a_offset, int lda, int[] ipiv, int _ipiv_offset, double[] work, int _work_offset, intW info)
/* 3906:     */   {
/* 3907:3211 */     Dsytri.dsytri(uplo, n, a, _a_offset, lda, ipiv, _ipiv_offset, work, _work_offset, info);
/* 3908:     */   }
/* 3909:     */   
/* 3910:     */   public void dsytrs(String uplo, int n, int nrhs, double[] a, int lda, int[] ipiv, double[] b, int ldb, intW info)
/* 3911:     */   {
/* 3912:3216 */     Dsytrs.dsytrs(uplo, n, nrhs, a, 0, lda, ipiv, 0, b, 0, ldb, info);
/* 3913:     */   }
/* 3914:     */   
/* 3915:     */   public void dsytrs(String uplo, int n, int nrhs, double[] a, int _a_offset, int lda, int[] ipiv, int _ipiv_offset, double[] b, int _b_offset, int ldb, intW info)
/* 3916:     */   {
/* 3917:3221 */     Dsytrs.dsytrs(uplo, n, nrhs, a, _a_offset, lda, ipiv, _ipiv_offset, b, _b_offset, ldb, info);
/* 3918:     */   }
/* 3919:     */   
/* 3920:     */   public void dtbcon(String norm, String uplo, String diag, int n, int kd, double[] ab, int ldab, doubleW rcond, double[] work, int[] iwork, intW info)
/* 3921:     */   {
/* 3922:3226 */     Dtbcon.dtbcon(norm, uplo, diag, n, kd, ab, 0, ldab, rcond, work, 0, iwork, 0, info);
/* 3923:     */   }
/* 3924:     */   
/* 3925:     */   public void dtbcon(String norm, String uplo, String diag, int n, int kd, double[] ab, int _ab_offset, int ldab, doubleW rcond, double[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 3926:     */   {
/* 3927:3231 */     Dtbcon.dtbcon(norm, uplo, diag, n, kd, ab, _ab_offset, ldab, rcond, work, _work_offset, iwork, _iwork_offset, info);
/* 3928:     */   }
/* 3929:     */   
/* 3930:     */   public void dtbrfs(String uplo, String trans, String diag, int n, int kd, int nrhs, double[] ab, int ldab, double[] b, int ldb, double[] x, int ldx, double[] ferr, double[] berr, double[] work, int[] iwork, intW info)
/* 3931:     */   {
/* 3932:3236 */     Dtbrfs.dtbrfs(uplo, trans, diag, n, kd, nrhs, ab, 0, ldab, b, 0, ldb, x, 0, ldx, ferr, 0, berr, 0, work, 0, iwork, 0, info);
/* 3933:     */   }
/* 3934:     */   
/* 3935:     */   public void dtbrfs(String uplo, String trans, String diag, int n, int kd, int nrhs, double[] ab, int _ab_offset, int ldab, double[] b, int _b_offset, int ldb, double[] x, int _x_offset, int ldx, double[] ferr, int _ferr_offset, double[] berr, int _berr_offset, double[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 3936:     */   {
/* 3937:3241 */     Dtbrfs.dtbrfs(uplo, trans, diag, n, kd, nrhs, ab, _ab_offset, ldab, b, _b_offset, ldb, x, _x_offset, ldx, ferr, _ferr_offset, berr, _berr_offset, work, _work_offset, iwork, _iwork_offset, info);
/* 3938:     */   }
/* 3939:     */   
/* 3940:     */   public void dtbtrs(String uplo, String trans, String diag, int n, int kd, int nrhs, double[] ab, int ldab, double[] b, int ldb, intW info)
/* 3941:     */   {
/* 3942:3246 */     Dtbtrs.dtbtrs(uplo, trans, diag, n, kd, nrhs, ab, 0, ldab, b, 0, ldb, info);
/* 3943:     */   }
/* 3944:     */   
/* 3945:     */   public void dtbtrs(String uplo, String trans, String diag, int n, int kd, int nrhs, double[] ab, int _ab_offset, int ldab, double[] b, int _b_offset, int ldb, intW info)
/* 3946:     */   {
/* 3947:3251 */     Dtbtrs.dtbtrs(uplo, trans, diag, n, kd, nrhs, ab, _ab_offset, ldab, b, _b_offset, ldb, info);
/* 3948:     */   }
/* 3949:     */   
/* 3950:     */   public void dtgevc(String side, String howmny, boolean[] select, int n, double[] s, int lds, double[] p, int ldp, double[] vl, int ldvl, double[] vr, int ldvr, int mm, intW m, double[] work, intW info)
/* 3951:     */   {
/* 3952:3256 */     Dtgevc.dtgevc(side, howmny, select, 0, n, s, 0, lds, p, 0, ldp, vl, 0, ldvl, vr, 0, ldvr, mm, m, work, 0, info);
/* 3953:     */   }
/* 3954:     */   
/* 3955:     */   public void dtgevc(String side, String howmny, boolean[] select, int _select_offset, int n, double[] s, int _s_offset, int lds, double[] p, int _p_offset, int ldp, double[] vl, int _vl_offset, int ldvl, double[] vr, int _vr_offset, int ldvr, int mm, intW m, double[] work, int _work_offset, intW info)
/* 3956:     */   {
/* 3957:3261 */     Dtgevc.dtgevc(side, howmny, select, _select_offset, n, s, _s_offset, lds, p, _p_offset, ldp, vl, _vl_offset, ldvl, vr, _vr_offset, ldvr, mm, m, work, _work_offset, info);
/* 3958:     */   }
/* 3959:     */   
/* 3960:     */   public void dtgex2(boolean wantq, boolean wantz, int n, double[] a, int lda, double[] b, int ldb, double[] q, int ldq, double[] z, int ldz, int j1, int n1, int n2, double[] work, int lwork, intW info)
/* 3961:     */   {
/* 3962:3266 */     Dtgex2.dtgex2(wantq, wantz, n, a, 0, lda, b, 0, ldb, q, 0, ldq, z, 0, ldz, j1, n1, n2, work, 0, lwork, info);
/* 3963:     */   }
/* 3964:     */   
/* 3965:     */   public void dtgex2(boolean wantq, boolean wantz, int n, double[] a, int _a_offset, int lda, double[] b, int _b_offset, int ldb, double[] q, int _q_offset, int ldq, double[] z, int _z_offset, int ldz, int j1, int n1, int n2, double[] work, int _work_offset, int lwork, intW info)
/* 3966:     */   {
/* 3967:3271 */     Dtgex2.dtgex2(wantq, wantz, n, a, _a_offset, lda, b, _b_offset, ldb, q, _q_offset, ldq, z, _z_offset, ldz, j1, n1, n2, work, _work_offset, lwork, info);
/* 3968:     */   }
/* 3969:     */   
/* 3970:     */   public void dtgexc(boolean wantq, boolean wantz, int n, double[] a, int lda, double[] b, int ldb, double[] q, int ldq, double[] z, int ldz, intW ifst, intW ilst, double[] work, int lwork, intW info)
/* 3971:     */   {
/* 3972:3276 */     Dtgexc.dtgexc(wantq, wantz, n, a, 0, lda, b, 0, ldb, q, 0, ldq, z, 0, ldz, ifst, ilst, work, 0, lwork, info);
/* 3973:     */   }
/* 3974:     */   
/* 3975:     */   public void dtgexc(boolean wantq, boolean wantz, int n, double[] a, int _a_offset, int lda, double[] b, int _b_offset, int ldb, double[] q, int _q_offset, int ldq, double[] z, int _z_offset, int ldz, intW ifst, intW ilst, double[] work, int _work_offset, int lwork, intW info)
/* 3976:     */   {
/* 3977:3281 */     Dtgexc.dtgexc(wantq, wantz, n, a, _a_offset, lda, b, _b_offset, ldb, q, _q_offset, ldq, z, _z_offset, ldz, ifst, ilst, work, _work_offset, lwork, info);
/* 3978:     */   }
/* 3979:     */   
/* 3980:     */   public void dtgsen(int ijob, boolean wantq, boolean wantz, boolean[] select, int n, double[] a, int lda, double[] b, int ldb, double[] alphar, double[] alphai, double[] beta, double[] q, int ldq, double[] z, int ldz, intW m, doubleW pl, doubleW pr, double[] dif, double[] work, int lwork, int[] iwork, int liwork, intW info)
/* 3981:     */   {
/* 3982:3286 */     Dtgsen.dtgsen(ijob, wantq, wantz, select, 0, n, a, 0, lda, b, 0, ldb, alphar, 0, alphai, 0, beta, 0, q, 0, ldq, z, 0, ldz, m, pl, pr, dif, 0, work, 0, lwork, iwork, 0, liwork, info);
/* 3983:     */   }
/* 3984:     */   
/* 3985:     */   public void dtgsen(int ijob, boolean wantq, boolean wantz, boolean[] select, int _select_offset, int n, double[] a, int _a_offset, int lda, double[] b, int _b_offset, int ldb, double[] alphar, int _alphar_offset, double[] alphai, int _alphai_offset, double[] beta, int _beta_offset, double[] q, int _q_offset, int ldq, double[] z, int _z_offset, int ldz, intW m, doubleW pl, doubleW pr, double[] dif, int _dif_offset, double[] work, int _work_offset, int lwork, int[] iwork, int _iwork_offset, int liwork, intW info)
/* 3986:     */   {
/* 3987:3291 */     Dtgsen.dtgsen(ijob, wantq, wantz, select, _select_offset, n, a, _a_offset, lda, b, _b_offset, ldb, alphar, _alphar_offset, alphai, _alphai_offset, beta, _beta_offset, q, _q_offset, ldq, z, _z_offset, ldz, m, pl, pr, dif, _dif_offset, work, _work_offset, lwork, iwork, _iwork_offset, liwork, info);
/* 3988:     */   }
/* 3989:     */   
/* 3990:     */   public void dtgsja(String jobu, String jobv, String jobq, int m, int p, int n, int k, int l, double[] a, int lda, double[] b, int ldb, double tola, double tolb, double[] alpha, double[] beta, double[] u, int ldu, double[] v, int ldv, double[] q, int ldq, double[] work, intW ncycle, intW info)
/* 3991:     */   {
/* 3992:3296 */     Dtgsja.dtgsja(jobu, jobv, jobq, m, p, n, k, l, a, 0, lda, b, 0, ldb, tola, tolb, alpha, 0, beta, 0, u, 0, ldu, v, 0, ldv, q, 0, ldq, work, 0, ncycle, info);
/* 3993:     */   }
/* 3994:     */   
/* 3995:     */   public void dtgsja(String jobu, String jobv, String jobq, int m, int p, int n, int k, int l, double[] a, int _a_offset, int lda, double[] b, int _b_offset, int ldb, double tola, double tolb, double[] alpha, int _alpha_offset, double[] beta, int _beta_offset, double[] u, int _u_offset, int ldu, double[] v, int _v_offset, int ldv, double[] q, int _q_offset, int ldq, double[] work, int _work_offset, intW ncycle, intW info)
/* 3996:     */   {
/* 3997:3301 */     Dtgsja.dtgsja(jobu, jobv, jobq, m, p, n, k, l, a, _a_offset, lda, b, _b_offset, ldb, tola, tolb, alpha, _alpha_offset, beta, _beta_offset, u, _u_offset, ldu, v, _v_offset, ldv, q, _q_offset, ldq, work, _work_offset, ncycle, info);
/* 3998:     */   }
/* 3999:     */   
/* 4000:     */   public void dtgsna(String job, String howmny, boolean[] select, int n, double[] a, int lda, double[] b, int ldb, double[] vl, int ldvl, double[] vr, int ldvr, double[] s, double[] dif, int mm, intW m, double[] work, int lwork, int[] iwork, intW info)
/* 4001:     */   {
/* 4002:3306 */     Dtgsna.dtgsna(job, howmny, select, 0, n, a, 0, lda, b, 0, ldb, vl, 0, ldvl, vr, 0, ldvr, s, 0, dif, 0, mm, m, work, 0, lwork, iwork, 0, info);
/* 4003:     */   }
/* 4004:     */   
/* 4005:     */   public void dtgsna(String job, String howmny, boolean[] select, int _select_offset, int n, double[] a, int _a_offset, int lda, double[] b, int _b_offset, int ldb, double[] vl, int _vl_offset, int ldvl, double[] vr, int _vr_offset, int ldvr, double[] s, int _s_offset, double[] dif, int _dif_offset, int mm, intW m, double[] work, int _work_offset, int lwork, int[] iwork, int _iwork_offset, intW info)
/* 4006:     */   {
/* 4007:3311 */     Dtgsna.dtgsna(job, howmny, select, _select_offset, n, a, _a_offset, lda, b, _b_offset, ldb, vl, _vl_offset, ldvl, vr, _vr_offset, ldvr, s, _s_offset, dif, _dif_offset, mm, m, work, _work_offset, lwork, iwork, _iwork_offset, info);
/* 4008:     */   }
/* 4009:     */   
/* 4010:     */   public void dtgsy2(String trans, int ijob, int m, int n, double[] a, int lda, double[] b, int ldb, double[] c, int Ldc, double[] d, int ldd, double[] e, int lde, double[] f, int ldf, doubleW scale, doubleW rdsum, doubleW rdscal, int[] iwork, intW pq, intW info)
/* 4011:     */   {
/* 4012:3316 */     Dtgsy2.dtgsy2(trans, ijob, m, n, a, 0, lda, b, 0, ldb, c, 0, Ldc, d, 0, ldd, e, 0, lde, f, 0, ldf, scale, rdsum, rdscal, iwork, 0, pq, info);
/* 4013:     */   }
/* 4014:     */   
/* 4015:     */   public void dtgsy2(String trans, int ijob, int m, int n, double[] a, int _a_offset, int lda, double[] b, int _b_offset, int ldb, double[] c, int _c_offset, int Ldc, double[] d, int _d_offset, int ldd, double[] e, int _e_offset, int lde, double[] f, int _f_offset, int ldf, doubleW scale, doubleW rdsum, doubleW rdscal, int[] iwork, int _iwork_offset, intW pq, intW info)
/* 4016:     */   {
/* 4017:3321 */     Dtgsy2.dtgsy2(trans, ijob, m, n, a, _a_offset, lda, b, _b_offset, ldb, c, _c_offset, Ldc, d, _d_offset, ldd, e, _e_offset, lde, f, _f_offset, ldf, scale, rdsum, rdscal, iwork, _iwork_offset, pq, info);
/* 4018:     */   }
/* 4019:     */   
/* 4020:     */   public void dtgsyl(String trans, int ijob, int m, int n, double[] a, int lda, double[] b, int ldb, double[] c, int Ldc, double[] d, int ldd, double[] e, int lde, double[] f, int ldf, doubleW scale, doubleW dif, double[] work, int lwork, int[] iwork, intW info)
/* 4021:     */   {
/* 4022:3326 */     Dtgsyl.dtgsyl(trans, ijob, m, n, a, 0, lda, b, 0, ldb, c, 0, Ldc, d, 0, ldd, e, 0, lde, f, 0, ldf, scale, dif, work, 0, lwork, iwork, 0, info);
/* 4023:     */   }
/* 4024:     */   
/* 4025:     */   public void dtgsyl(String trans, int ijob, int m, int n, double[] a, int _a_offset, int lda, double[] b, int _b_offset, int ldb, double[] c, int _c_offset, int Ldc, double[] d, int _d_offset, int ldd, double[] e, int _e_offset, int lde, double[] f, int _f_offset, int ldf, doubleW scale, doubleW dif, double[] work, int _work_offset, int lwork, int[] iwork, int _iwork_offset, intW info)
/* 4026:     */   {
/* 4027:3331 */     Dtgsyl.dtgsyl(trans, ijob, m, n, a, _a_offset, lda, b, _b_offset, ldb, c, _c_offset, Ldc, d, _d_offset, ldd, e, _e_offset, lde, f, _f_offset, ldf, scale, dif, work, _work_offset, lwork, iwork, _iwork_offset, info);
/* 4028:     */   }
/* 4029:     */   
/* 4030:     */   public void dtpcon(String norm, String uplo, String diag, int n, double[] ap, doubleW rcond, double[] work, int[] iwork, intW info)
/* 4031:     */   {
/* 4032:3336 */     Dtpcon.dtpcon(norm, uplo, diag, n, ap, 0, rcond, work, 0, iwork, 0, info);
/* 4033:     */   }
/* 4034:     */   
/* 4035:     */   public void dtpcon(String norm, String uplo, String diag, int n, double[] ap, int _ap_offset, doubleW rcond, double[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 4036:     */   {
/* 4037:3341 */     Dtpcon.dtpcon(norm, uplo, diag, n, ap, _ap_offset, rcond, work, _work_offset, iwork, _iwork_offset, info);
/* 4038:     */   }
/* 4039:     */   
/* 4040:     */   public void dtprfs(String uplo, String trans, String diag, int n, int nrhs, double[] ap, double[] b, int ldb, double[] x, int ldx, double[] ferr, double[] berr, double[] work, int[] iwork, intW info)
/* 4041:     */   {
/* 4042:3346 */     Dtprfs.dtprfs(uplo, trans, diag, n, nrhs, ap, 0, b, 0, ldb, x, 0, ldx, ferr, 0, berr, 0, work, 0, iwork, 0, info);
/* 4043:     */   }
/* 4044:     */   
/* 4045:     */   public void dtprfs(String uplo, String trans, String diag, int n, int nrhs, double[] ap, int _ap_offset, double[] b, int _b_offset, int ldb, double[] x, int _x_offset, int ldx, double[] ferr, int _ferr_offset, double[] berr, int _berr_offset, double[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 4046:     */   {
/* 4047:3351 */     Dtprfs.dtprfs(uplo, trans, diag, n, nrhs, ap, _ap_offset, b, _b_offset, ldb, x, _x_offset, ldx, ferr, _ferr_offset, berr, _berr_offset, work, _work_offset, iwork, _iwork_offset, info);
/* 4048:     */   }
/* 4049:     */   
/* 4050:     */   public void dtptri(String uplo, String diag, int n, double[] ap, intW info)
/* 4051:     */   {
/* 4052:3356 */     Dtptri.dtptri(uplo, diag, n, ap, 0, info);
/* 4053:     */   }
/* 4054:     */   
/* 4055:     */   public void dtptri(String uplo, String diag, int n, double[] ap, int _ap_offset, intW info)
/* 4056:     */   {
/* 4057:3361 */     Dtptri.dtptri(uplo, diag, n, ap, _ap_offset, info);
/* 4058:     */   }
/* 4059:     */   
/* 4060:     */   public void dtptrs(String uplo, String trans, String diag, int n, int nrhs, double[] ap, double[] b, int ldb, intW info)
/* 4061:     */   {
/* 4062:3366 */     Dtptrs.dtptrs(uplo, trans, diag, n, nrhs, ap, 0, b, 0, ldb, info);
/* 4063:     */   }
/* 4064:     */   
/* 4065:     */   public void dtptrs(String uplo, String trans, String diag, int n, int nrhs, double[] ap, int _ap_offset, double[] b, int _b_offset, int ldb, intW info)
/* 4066:     */   {
/* 4067:3371 */     Dtptrs.dtptrs(uplo, trans, diag, n, nrhs, ap, _ap_offset, b, _b_offset, ldb, info);
/* 4068:     */   }
/* 4069:     */   
/* 4070:     */   public void dtrcon(String norm, String uplo, String diag, int n, double[] a, int lda, doubleW rcond, double[] work, int[] iwork, intW info)
/* 4071:     */   {
/* 4072:3376 */     Dtrcon.dtrcon(norm, uplo, diag, n, a, 0, lda, rcond, work, 0, iwork, 0, info);
/* 4073:     */   }
/* 4074:     */   
/* 4075:     */   public void dtrcon(String norm, String uplo, String diag, int n, double[] a, int _a_offset, int lda, doubleW rcond, double[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 4076:     */   {
/* 4077:3381 */     Dtrcon.dtrcon(norm, uplo, diag, n, a, _a_offset, lda, rcond, work, _work_offset, iwork, _iwork_offset, info);
/* 4078:     */   }
/* 4079:     */   
/* 4080:     */   public void dtrevc(String side, String howmny, boolean[] select, int n, double[] t, int ldt, double[] vl, int ldvl, double[] vr, int ldvr, int mm, intW m, double[] work, intW info)
/* 4081:     */   {
/* 4082:3386 */     Dtrevc.dtrevc(side, howmny, select, 0, n, t, 0, ldt, vl, 0, ldvl, vr, 0, ldvr, mm, m, work, 0, info);
/* 4083:     */   }
/* 4084:     */   
/* 4085:     */   public void dtrevc(String side, String howmny, boolean[] select, int _select_offset, int n, double[] t, int _t_offset, int ldt, double[] vl, int _vl_offset, int ldvl, double[] vr, int _vr_offset, int ldvr, int mm, intW m, double[] work, int _work_offset, intW info)
/* 4086:     */   {
/* 4087:3391 */     Dtrevc.dtrevc(side, howmny, select, _select_offset, n, t, _t_offset, ldt, vl, _vl_offset, ldvl, vr, _vr_offset, ldvr, mm, m, work, _work_offset, info);
/* 4088:     */   }
/* 4089:     */   
/* 4090:     */   public void dtrexc(String compq, int n, double[] t, int ldt, double[] q, int ldq, intW ifst, intW ilst, double[] work, intW info)
/* 4091:     */   {
/* 4092:3396 */     Dtrexc.dtrexc(compq, n, t, 0, ldt, q, 0, ldq, ifst, ilst, work, 0, info);
/* 4093:     */   }
/* 4094:     */   
/* 4095:     */   public void dtrexc(String compq, int n, double[] t, int _t_offset, int ldt, double[] q, int _q_offset, int ldq, intW ifst, intW ilst, double[] work, int _work_offset, intW info)
/* 4096:     */   {
/* 4097:3401 */     Dtrexc.dtrexc(compq, n, t, _t_offset, ldt, q, _q_offset, ldq, ifst, ilst, work, _work_offset, info);
/* 4098:     */   }
/* 4099:     */   
/* 4100:     */   public void dtrrfs(String uplo, String trans, String diag, int n, int nrhs, double[] a, int lda, double[] b, int ldb, double[] x, int ldx, double[] ferr, double[] berr, double[] work, int[] iwork, intW info)
/* 4101:     */   {
/* 4102:3406 */     Dtrrfs.dtrrfs(uplo, trans, diag, n, nrhs, a, 0, lda, b, 0, ldb, x, 0, ldx, ferr, 0, berr, 0, work, 0, iwork, 0, info);
/* 4103:     */   }
/* 4104:     */   
/* 4105:     */   public void dtrrfs(String uplo, String trans, String diag, int n, int nrhs, double[] a, int _a_offset, int lda, double[] b, int _b_offset, int ldb, double[] x, int _x_offset, int ldx, double[] ferr, int _ferr_offset, double[] berr, int _berr_offset, double[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 4106:     */   {
/* 4107:3411 */     Dtrrfs.dtrrfs(uplo, trans, diag, n, nrhs, a, _a_offset, lda, b, _b_offset, ldb, x, _x_offset, ldx, ferr, _ferr_offset, berr, _berr_offset, work, _work_offset, iwork, _iwork_offset, info);
/* 4108:     */   }
/* 4109:     */   
/* 4110:     */   public void dtrsen(String job, String compq, boolean[] select, int n, double[] t, int ldt, double[] q, int ldq, double[] wr, double[] wi, intW m, doubleW s, doubleW sep, double[] work, int lwork, int[] iwork, int liwork, intW info)
/* 4111:     */   {
/* 4112:3416 */     Dtrsen.dtrsen(job, compq, select, 0, n, t, 0, ldt, q, 0, ldq, wr, 0, wi, 0, m, s, sep, work, 0, lwork, iwork, 0, liwork, info);
/* 4113:     */   }
/* 4114:     */   
/* 4115:     */   public void dtrsen(String job, String compq, boolean[] select, int _select_offset, int n, double[] t, int _t_offset, int ldt, double[] q, int _q_offset, int ldq, double[] wr, int _wr_offset, double[] wi, int _wi_offset, intW m, doubleW s, doubleW sep, double[] work, int _work_offset, int lwork, int[] iwork, int _iwork_offset, int liwork, intW info)
/* 4116:     */   {
/* 4117:3421 */     Dtrsen.dtrsen(job, compq, select, _select_offset, n, t, _t_offset, ldt, q, _q_offset, ldq, wr, _wr_offset, wi, _wi_offset, m, s, sep, work, _work_offset, lwork, iwork, _iwork_offset, liwork, info);
/* 4118:     */   }
/* 4119:     */   
/* 4120:     */   public void dtrsna(String job, String howmny, boolean[] select, int n, double[] t, int ldt, double[] vl, int ldvl, double[] vr, int ldvr, double[] s, double[] sep, int mm, intW m, double[] work, int ldwork, int[] iwork, intW info)
/* 4121:     */   {
/* 4122:3426 */     Dtrsna.dtrsna(job, howmny, select, 0, n, t, 0, ldt, vl, 0, ldvl, vr, 0, ldvr, s, 0, sep, 0, mm, m, work, 0, ldwork, iwork, 0, info);
/* 4123:     */   }
/* 4124:     */   
/* 4125:     */   public void dtrsna(String job, String howmny, boolean[] select, int _select_offset, int n, double[] t, int _t_offset, int ldt, double[] vl, int _vl_offset, int ldvl, double[] vr, int _vr_offset, int ldvr, double[] s, int _s_offset, double[] sep, int _sep_offset, int mm, intW m, double[] work, int _work_offset, int ldwork, int[] iwork, int _iwork_offset, intW info)
/* 4126:     */   {
/* 4127:3431 */     Dtrsna.dtrsna(job, howmny, select, _select_offset, n, t, _t_offset, ldt, vl, _vl_offset, ldvl, vr, _vr_offset, ldvr, s, _s_offset, sep, _sep_offset, mm, m, work, _work_offset, ldwork, iwork, _iwork_offset, info);
/* 4128:     */   }
/* 4129:     */   
/* 4130:     */   public void dtrsyl(String trana, String tranb, int isgn, int m, int n, double[] a, int lda, double[] b, int ldb, double[] c, int Ldc, doubleW scale, intW info)
/* 4131:     */   {
/* 4132:3436 */     Dtrsyl.dtrsyl(trana, tranb, isgn, m, n, a, 0, lda, b, 0, ldb, c, 0, Ldc, scale, info);
/* 4133:     */   }
/* 4134:     */   
/* 4135:     */   public void dtrsyl(String trana, String tranb, int isgn, int m, int n, double[] a, int _a_offset, int lda, double[] b, int _b_offset, int ldb, double[] c, int _c_offset, int Ldc, doubleW scale, intW info)
/* 4136:     */   {
/* 4137:3441 */     Dtrsyl.dtrsyl(trana, tranb, isgn, m, n, a, _a_offset, lda, b, _b_offset, ldb, c, _c_offset, Ldc, scale, info);
/* 4138:     */   }
/* 4139:     */   
/* 4140:     */   public void dtrti2(String uplo, String diag, int n, double[] a, int lda, intW info)
/* 4141:     */   {
/* 4142:3446 */     Dtrti2.dtrti2(uplo, diag, n, a, 0, lda, info);
/* 4143:     */   }
/* 4144:     */   
/* 4145:     */   public void dtrti2(String uplo, String diag, int n, double[] a, int _a_offset, int lda, intW info)
/* 4146:     */   {
/* 4147:3451 */     Dtrti2.dtrti2(uplo, diag, n, a, _a_offset, lda, info);
/* 4148:     */   }
/* 4149:     */   
/* 4150:     */   public void dtrtri(String uplo, String diag, int n, double[] a, int lda, intW info)
/* 4151:     */   {
/* 4152:3456 */     Dtrtri.dtrtri(uplo, diag, n, a, 0, lda, info);
/* 4153:     */   }
/* 4154:     */   
/* 4155:     */   public void dtrtri(String uplo, String diag, int n, double[] a, int _a_offset, int lda, intW info)
/* 4156:     */   {
/* 4157:3461 */     Dtrtri.dtrtri(uplo, diag, n, a, _a_offset, lda, info);
/* 4158:     */   }
/* 4159:     */   
/* 4160:     */   public void dtrtrs(String uplo, String trans, String diag, int n, int nrhs, double[] a, int lda, double[] b, int ldb, intW info)
/* 4161:     */   {
/* 4162:3466 */     Dtrtrs.dtrtrs(uplo, trans, diag, n, nrhs, a, 0, lda, b, 0, ldb, info);
/* 4163:     */   }
/* 4164:     */   
/* 4165:     */   public void dtrtrs(String uplo, String trans, String diag, int n, int nrhs, double[] a, int _a_offset, int lda, double[] b, int _b_offset, int ldb, intW info)
/* 4166:     */   {
/* 4167:3471 */     Dtrtrs.dtrtrs(uplo, trans, diag, n, nrhs, a, _a_offset, lda, b, _b_offset, ldb, info);
/* 4168:     */   }
/* 4169:     */   
/* 4170:     */   public void dtzrqf(int m, int n, double[] a, int lda, double[] tau, intW info)
/* 4171:     */   {
/* 4172:3476 */     Dtzrqf.dtzrqf(m, n, a, 0, lda, tau, 0, info);
/* 4173:     */   }
/* 4174:     */   
/* 4175:     */   public void dtzrqf(int m, int n, double[] a, int _a_offset, int lda, double[] tau, int _tau_offset, intW info)
/* 4176:     */   {
/* 4177:3481 */     Dtzrqf.dtzrqf(m, n, a, _a_offset, lda, tau, _tau_offset, info);
/* 4178:     */   }
/* 4179:     */   
/* 4180:     */   public void dtzrzf(int m, int n, double[] a, int lda, double[] tau, double[] work, int lwork, intW info)
/* 4181:     */   {
/* 4182:3486 */     Dtzrzf.dtzrzf(m, n, a, 0, lda, tau, 0, work, 0, lwork, info);
/* 4183:     */   }
/* 4184:     */   
/* 4185:     */   public void dtzrzf(int m, int n, double[] a, int _a_offset, int lda, double[] tau, int _tau_offset, double[] work, int _work_offset, int lwork, intW info)
/* 4186:     */   {
/* 4187:3491 */     Dtzrzf.dtzrzf(m, n, a, _a_offset, lda, tau, _tau_offset, work, _work_offset, lwork, info);
/* 4188:     */   }
/* 4189:     */   
/* 4190:     */   public int ieeeck(int ispec, float zero, float one)
/* 4191:     */   {
/* 4192:3496 */     return Ieeeck.ieeeck(ispec, zero, one);
/* 4193:     */   }
/* 4194:     */   
/* 4195:     */   public int ilaenv(int ispec, String name, String opts, int n1, int n2, int n3, int n4)
/* 4196:     */   {
/* 4197:3501 */     return Ilaenv.ilaenv(ispec, name, opts, n1, n2, n3, n4);
/* 4198:     */   }
/* 4199:     */   
/* 4200:     */   public void ilaver(intW vers_major, intW vers_minor, intW vers_patch)
/* 4201:     */   {
/* 4202:3506 */     Ilaver.ilaver(vers_major, vers_minor, vers_patch);
/* 4203:     */   }
/* 4204:     */   
/* 4205:     */   public int iparmq(int ispec, String name, String opts, int n, int ilo, int ihi, int lwork)
/* 4206:     */   {
/* 4207:3511 */     return Iparmq.iparmq(ispec, name, opts, n, ilo, ihi, lwork);
/* 4208:     */   }
/* 4209:     */   
/* 4210:     */   public boolean lsamen(int n, String ca, String cb)
/* 4211:     */   {
/* 4212:3516 */     return Lsamen.lsamen(n, ca, cb);
/* 4213:     */   }
/* 4214:     */   
/* 4215:     */   public void sbdsdc(String uplo, String compq, int n, float[] d, float[] e, float[] u, int ldu, float[] vt, int ldvt, float[] q, int[] iq, float[] work, int[] iwork, intW info)
/* 4216:     */   {
/* 4217:3521 */     Sbdsdc.sbdsdc(uplo, compq, n, d, 0, e, 0, u, 0, ldu, vt, 0, ldvt, q, 0, iq, 0, work, 0, iwork, 0, info);
/* 4218:     */   }
/* 4219:     */   
/* 4220:     */   public void sbdsdc(String uplo, String compq, int n, float[] d, int _d_offset, float[] e, int _e_offset, float[] u, int _u_offset, int ldu, float[] vt, int _vt_offset, int ldvt, float[] q, int _q_offset, int[] iq, int _iq_offset, float[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 4221:     */   {
/* 4222:3526 */     Sbdsdc.sbdsdc(uplo, compq, n, d, _d_offset, e, _e_offset, u, _u_offset, ldu, vt, _vt_offset, ldvt, q, _q_offset, iq, _iq_offset, work, _work_offset, iwork, _iwork_offset, info);
/* 4223:     */   }
/* 4224:     */   
/* 4225:     */   public void sbdsqr(String uplo, int n, int ncvt, int nru, int ncc, float[] d, float[] e, float[] vt, int ldvt, float[] u, int ldu, float[] c, int Ldc, float[] work, intW info)
/* 4226:     */   {
/* 4227:3531 */     Sbdsqr.sbdsqr(uplo, n, ncvt, nru, ncc, d, 0, e, 0, vt, 0, ldvt, u, 0, ldu, c, 0, Ldc, work, 0, info);
/* 4228:     */   }
/* 4229:     */   
/* 4230:     */   public void sbdsqr(String uplo, int n, int ncvt, int nru, int ncc, float[] d, int _d_offset, float[] e, int _e_offset, float[] vt, int _vt_offset, int ldvt, float[] u, int _u_offset, int ldu, float[] c, int _c_offset, int Ldc, float[] work, int _work_offset, intW info)
/* 4231:     */   {
/* 4232:3536 */     Sbdsqr.sbdsqr(uplo, n, ncvt, nru, ncc, d, _d_offset, e, _e_offset, vt, _vt_offset, ldvt, u, _u_offset, ldu, c, _c_offset, Ldc, work, _work_offset, info);
/* 4233:     */   }
/* 4234:     */   
/* 4235:     */   public void sdisna(String job, int m, int n, float[] d, float[] sep, intW info)
/* 4236:     */   {
/* 4237:3541 */     Sdisna.sdisna(job, m, n, d, 0, sep, 0, info);
/* 4238:     */   }
/* 4239:     */   
/* 4240:     */   public void sdisna(String job, int m, int n, float[] d, int _d_offset, float[] sep, int _sep_offset, intW info)
/* 4241:     */   {
/* 4242:3546 */     Sdisna.sdisna(job, m, n, d, _d_offset, sep, _sep_offset, info);
/* 4243:     */   }
/* 4244:     */   
/* 4245:     */   public void sgbbrd(String vect, int m, int n, int ncc, int kl, int ku, float[] ab, int ldab, float[] d, float[] e, float[] q, int ldq, float[] pt, int ldpt, float[] c, int Ldc, float[] work, intW info)
/* 4246:     */   {
/* 4247:3551 */     Sgbbrd.sgbbrd(vect, m, n, ncc, kl, ku, ab, 0, ldab, d, 0, e, 0, q, 0, ldq, pt, 0, ldpt, c, 0, Ldc, work, 0, info);
/* 4248:     */   }
/* 4249:     */   
/* 4250:     */   public void sgbbrd(String vect, int m, int n, int ncc, int kl, int ku, float[] ab, int _ab_offset, int ldab, float[] d, int _d_offset, float[] e, int _e_offset, float[] q, int _q_offset, int ldq, float[] pt, int _pt_offset, int ldpt, float[] c, int _c_offset, int Ldc, float[] work, int _work_offset, intW info)
/* 4251:     */   {
/* 4252:3556 */     Sgbbrd.sgbbrd(vect, m, n, ncc, kl, ku, ab, _ab_offset, ldab, d, _d_offset, e, _e_offset, q, _q_offset, ldq, pt, _pt_offset, ldpt, c, _c_offset, Ldc, work, _work_offset, info);
/* 4253:     */   }
/* 4254:     */   
/* 4255:     */   public void sgbcon(String norm, int n, int kl, int ku, float[] ab, int ldab, int[] ipiv, float anorm, floatW rcond, float[] work, int[] iwork, intW info)
/* 4256:     */   {
/* 4257:3561 */     Sgbcon.sgbcon(norm, n, kl, ku, ab, 0, ldab, ipiv, 0, anorm, rcond, work, 0, iwork, 0, info);
/* 4258:     */   }
/* 4259:     */   
/* 4260:     */   public void sgbcon(String norm, int n, int kl, int ku, float[] ab, int _ab_offset, int ldab, int[] ipiv, int _ipiv_offset, float anorm, floatW rcond, float[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 4261:     */   {
/* 4262:3566 */     Sgbcon.sgbcon(norm, n, kl, ku, ab, _ab_offset, ldab, ipiv, _ipiv_offset, anorm, rcond, work, _work_offset, iwork, _iwork_offset, info);
/* 4263:     */   }
/* 4264:     */   
/* 4265:     */   public void sgbequ(int m, int n, int kl, int ku, float[] ab, int ldab, float[] r, float[] c, floatW rowcnd, floatW colcnd, floatW amax, intW info)
/* 4266:     */   {
/* 4267:3571 */     Sgbequ.sgbequ(m, n, kl, ku, ab, 0, ldab, r, 0, c, 0, rowcnd, colcnd, amax, info);
/* 4268:     */   }
/* 4269:     */   
/* 4270:     */   public void sgbequ(int m, int n, int kl, int ku, float[] ab, int _ab_offset, int ldab, float[] r, int _r_offset, float[] c, int _c_offset, floatW rowcnd, floatW colcnd, floatW amax, intW info)
/* 4271:     */   {
/* 4272:3576 */     Sgbequ.sgbequ(m, n, kl, ku, ab, _ab_offset, ldab, r, _r_offset, c, _c_offset, rowcnd, colcnd, amax, info);
/* 4273:     */   }
/* 4274:     */   
/* 4275:     */   public void sgbrfs(String trans, int n, int kl, int ku, int nrhs, float[] ab, int ldab, float[] afb, int ldafb, int[] ipiv, float[] b, int ldb, float[] x, int ldx, float[] ferr, float[] berr, float[] work, int[] iwork, intW info)
/* 4276:     */   {
/* 4277:3581 */     Sgbrfs.sgbrfs(trans, n, kl, ku, nrhs, ab, 0, ldab, afb, 0, ldafb, ipiv, 0, b, 0, ldb, x, 0, ldx, ferr, 0, berr, 0, work, 0, iwork, 0, info);
/* 4278:     */   }
/* 4279:     */   
/* 4280:     */   public void sgbrfs(String trans, int n, int kl, int ku, int nrhs, float[] ab, int _ab_offset, int ldab, float[] afb, int _afb_offset, int ldafb, int[] ipiv, int _ipiv_offset, float[] b, int _b_offset, int ldb, float[] x, int _x_offset, int ldx, float[] ferr, int _ferr_offset, float[] berr, int _berr_offset, float[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 4281:     */   {
/* 4282:3586 */     Sgbrfs.sgbrfs(trans, n, kl, ku, nrhs, ab, _ab_offset, ldab, afb, _afb_offset, ldafb, ipiv, _ipiv_offset, b, _b_offset, ldb, x, _x_offset, ldx, ferr, _ferr_offset, berr, _berr_offset, work, _work_offset, iwork, _iwork_offset, info);
/* 4283:     */   }
/* 4284:     */   
/* 4285:     */   public void sgbsv(int n, int kl, int ku, int nrhs, float[] ab, int ldab, int[] ipiv, float[] b, int ldb, intW info)
/* 4286:     */   {
/* 4287:3591 */     Sgbsv.sgbsv(n, kl, ku, nrhs, ab, 0, ldab, ipiv, 0, b, 0, ldb, info);
/* 4288:     */   }
/* 4289:     */   
/* 4290:     */   public void sgbsv(int n, int kl, int ku, int nrhs, float[] ab, int _ab_offset, int ldab, int[] ipiv, int _ipiv_offset, float[] b, int _b_offset, int ldb, intW info)
/* 4291:     */   {
/* 4292:3596 */     Sgbsv.sgbsv(n, kl, ku, nrhs, ab, _ab_offset, ldab, ipiv, _ipiv_offset, b, _b_offset, ldb, info);
/* 4293:     */   }
/* 4294:     */   
/* 4295:     */   public void sgbsvx(String fact, String trans, int n, int kl, int ku, int nrhs, float[] ab, int ldab, float[] afb, int ldafb, int[] ipiv, StringW equed, float[] r, float[] c, float[] b, int ldb, float[] x, int ldx, floatW rcond, float[] ferr, float[] berr, float[] work, int[] iwork, intW info)
/* 4296:     */   {
/* 4297:3601 */     Sgbsvx.sgbsvx(fact, trans, n, kl, ku, nrhs, ab, 0, ldab, afb, 0, ldafb, ipiv, 0, equed, r, 0, c, 0, b, 0, ldb, x, 0, ldx, rcond, ferr, 0, berr, 0, work, 0, iwork, 0, info);
/* 4298:     */   }
/* 4299:     */   
/* 4300:     */   public void sgbsvx(String fact, String trans, int n, int kl, int ku, int nrhs, float[] ab, int _ab_offset, int ldab, float[] afb, int _afb_offset, int ldafb, int[] ipiv, int _ipiv_offset, StringW equed, float[] r, int _r_offset, float[] c, int _c_offset, float[] b, int _b_offset, int ldb, float[] x, int _x_offset, int ldx, floatW rcond, float[] ferr, int _ferr_offset, float[] berr, int _berr_offset, float[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 4301:     */   {
/* 4302:3606 */     Sgbsvx.sgbsvx(fact, trans, n, kl, ku, nrhs, ab, _ab_offset, ldab, afb, _afb_offset, ldafb, ipiv, _ipiv_offset, equed, r, _r_offset, c, _c_offset, b, _b_offset, ldb, x, _x_offset, ldx, rcond, ferr, _ferr_offset, berr, _berr_offset, work, _work_offset, iwork, _iwork_offset, info);
/* 4303:     */   }
/* 4304:     */   
/* 4305:     */   public void sgbtf2(int m, int n, int kl, int ku, float[] ab, int ldab, int[] ipiv, intW info)
/* 4306:     */   {
/* 4307:3611 */     Sgbtf2.sgbtf2(m, n, kl, ku, ab, 0, ldab, ipiv, 0, info);
/* 4308:     */   }
/* 4309:     */   
/* 4310:     */   public void sgbtf2(int m, int n, int kl, int ku, float[] ab, int _ab_offset, int ldab, int[] ipiv, int _ipiv_offset, intW info)
/* 4311:     */   {
/* 4312:3616 */     Sgbtf2.sgbtf2(m, n, kl, ku, ab, _ab_offset, ldab, ipiv, _ipiv_offset, info);
/* 4313:     */   }
/* 4314:     */   
/* 4315:     */   public void sgbtrf(int m, int n, int kl, int ku, float[] ab, int ldab, int[] ipiv, intW info)
/* 4316:     */   {
/* 4317:3621 */     Sgbtrf.sgbtrf(m, n, kl, ku, ab, 0, ldab, ipiv, 0, info);
/* 4318:     */   }
/* 4319:     */   
/* 4320:     */   public void sgbtrf(int m, int n, int kl, int ku, float[] ab, int _ab_offset, int ldab, int[] ipiv, int _ipiv_offset, intW info)
/* 4321:     */   {
/* 4322:3626 */     Sgbtrf.sgbtrf(m, n, kl, ku, ab, _ab_offset, ldab, ipiv, _ipiv_offset, info);
/* 4323:     */   }
/* 4324:     */   
/* 4325:     */   public void sgbtrs(String trans, int n, int kl, int ku, int nrhs, float[] ab, int ldab, int[] ipiv, float[] b, int ldb, intW info)
/* 4326:     */   {
/* 4327:3631 */     Sgbtrs.sgbtrs(trans, n, kl, ku, nrhs, ab, 0, ldab, ipiv, 0, b, 0, ldb, info);
/* 4328:     */   }
/* 4329:     */   
/* 4330:     */   public void sgbtrs(String trans, int n, int kl, int ku, int nrhs, float[] ab, int _ab_offset, int ldab, int[] ipiv, int _ipiv_offset, float[] b, int _b_offset, int ldb, intW info)
/* 4331:     */   {
/* 4332:3636 */     Sgbtrs.sgbtrs(trans, n, kl, ku, nrhs, ab, _ab_offset, ldab, ipiv, _ipiv_offset, b, _b_offset, ldb, info);
/* 4333:     */   }
/* 4334:     */   
/* 4335:     */   public void sgebak(String job, String side, int n, int ilo, int ihi, float[] scale, int m, float[] v, int ldv, intW info)
/* 4336:     */   {
/* 4337:3641 */     Sgebak.sgebak(job, side, n, ilo, ihi, scale, 0, m, v, 0, ldv, info);
/* 4338:     */   }
/* 4339:     */   
/* 4340:     */   public void sgebak(String job, String side, int n, int ilo, int ihi, float[] scale, int _scale_offset, int m, float[] v, int _v_offset, int ldv, intW info)
/* 4341:     */   {
/* 4342:3646 */     Sgebak.sgebak(job, side, n, ilo, ihi, scale, _scale_offset, m, v, _v_offset, ldv, info);
/* 4343:     */   }
/* 4344:     */   
/* 4345:     */   public void sgebal(String job, int n, float[] a, int lda, intW ilo, intW ihi, float[] scale, intW info)
/* 4346:     */   {
/* 4347:3651 */     Sgebal.sgebal(job, n, a, 0, lda, ilo, ihi, scale, 0, info);
/* 4348:     */   }
/* 4349:     */   
/* 4350:     */   public void sgebal(String job, int n, float[] a, int _a_offset, int lda, intW ilo, intW ihi, float[] scale, int _scale_offset, intW info)
/* 4351:     */   {
/* 4352:3656 */     Sgebal.sgebal(job, n, a, _a_offset, lda, ilo, ihi, scale, _scale_offset, info);
/* 4353:     */   }
/* 4354:     */   
/* 4355:     */   public void sgebd2(int m, int n, float[] a, int lda, float[] d, float[] e, float[] tauq, float[] taup, float[] work, intW info)
/* 4356:     */   {
/* 4357:3661 */     Sgebd2.sgebd2(m, n, a, 0, lda, d, 0, e, 0, tauq, 0, taup, 0, work, 0, info);
/* 4358:     */   }
/* 4359:     */   
/* 4360:     */   public void sgebd2(int m, int n, float[] a, int _a_offset, int lda, float[] d, int _d_offset, float[] e, int _e_offset, float[] tauq, int _tauq_offset, float[] taup, int _taup_offset, float[] work, int _work_offset, intW info)
/* 4361:     */   {
/* 4362:3666 */     Sgebd2.sgebd2(m, n, a, _a_offset, lda, d, _d_offset, e, _e_offset, tauq, _tauq_offset, taup, _taup_offset, work, _work_offset, info);
/* 4363:     */   }
/* 4364:     */   
/* 4365:     */   public void sgebrd(int m, int n, float[] a, int lda, float[] d, float[] e, float[] tauq, float[] taup, float[] work, int lwork, intW info)
/* 4366:     */   {
/* 4367:3671 */     Sgebrd.sgebrd(m, n, a, 0, lda, d, 0, e, 0, tauq, 0, taup, 0, work, 0, lwork, info);
/* 4368:     */   }
/* 4369:     */   
/* 4370:     */   public void sgebrd(int m, int n, float[] a, int _a_offset, int lda, float[] d, int _d_offset, float[] e, int _e_offset, float[] tauq, int _tauq_offset, float[] taup, int _taup_offset, float[] work, int _work_offset, int lwork, intW info)
/* 4371:     */   {
/* 4372:3676 */     Sgebrd.sgebrd(m, n, a, _a_offset, lda, d, _d_offset, e, _e_offset, tauq, _tauq_offset, taup, _taup_offset, work, _work_offset, lwork, info);
/* 4373:     */   }
/* 4374:     */   
/* 4375:     */   public void sgecon(String norm, int n, float[] a, int lda, float anorm, floatW rcond, float[] work, int[] iwork, intW info)
/* 4376:     */   {
/* 4377:3681 */     Sgecon.sgecon(norm, n, a, 0, lda, anorm, rcond, work, 0, iwork, 0, info);
/* 4378:     */   }
/* 4379:     */   
/* 4380:     */   public void sgecon(String norm, int n, float[] a, int _a_offset, int lda, float anorm, floatW rcond, float[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 4381:     */   {
/* 4382:3686 */     Sgecon.sgecon(norm, n, a, _a_offset, lda, anorm, rcond, work, _work_offset, iwork, _iwork_offset, info);
/* 4383:     */   }
/* 4384:     */   
/* 4385:     */   public void sgeequ(int m, int n, float[] a, int lda, float[] r, float[] c, floatW rowcnd, floatW colcnd, floatW amax, intW info)
/* 4386:     */   {
/* 4387:3691 */     Sgeequ.sgeequ(m, n, a, 0, lda, r, 0, c, 0, rowcnd, colcnd, amax, info);
/* 4388:     */   }
/* 4389:     */   
/* 4390:     */   public void sgeequ(int m, int n, float[] a, int _a_offset, int lda, float[] r, int _r_offset, float[] c, int _c_offset, floatW rowcnd, floatW colcnd, floatW amax, intW info)
/* 4391:     */   {
/* 4392:3696 */     Sgeequ.sgeequ(m, n, a, _a_offset, lda, r, _r_offset, c, _c_offset, rowcnd, colcnd, amax, info);
/* 4393:     */   }
/* 4394:     */   
/* 4395:     */   public void sgees(String jobvs, String sort, Object select, int n, float[] a, int lda, intW sdim, float[] wr, float[] wi, float[] vs, int ldvs, float[] work, int lwork, boolean[] bwork, intW info)
/* 4396:     */   {
/* 4397:3701 */     Sgees.sgees(jobvs, sort, select, n, a, 0, lda, sdim, wr, 0, wi, 0, vs, 0, ldvs, work, 0, lwork, bwork, 0, info);
/* 4398:     */   }
/* 4399:     */   
/* 4400:     */   public void sgees(String jobvs, String sort, Object select, int n, float[] a, int _a_offset, int lda, intW sdim, float[] wr, int _wr_offset, float[] wi, int _wi_offset, float[] vs, int _vs_offset, int ldvs, float[] work, int _work_offset, int lwork, boolean[] bwork, int _bwork_offset, intW info)
/* 4401:     */   {
/* 4402:3706 */     Sgees.sgees(jobvs, sort, select, n, a, _a_offset, lda, sdim, wr, _wr_offset, wi, _wi_offset, vs, _vs_offset, ldvs, work, _work_offset, lwork, bwork, _bwork_offset, info);
/* 4403:     */   }
/* 4404:     */   
/* 4405:     */   public void sgeesx(String jobvs, String sort, Object select, String sense, int n, float[] a, int lda, intW sdim, float[] wr, float[] wi, float[] vs, int ldvs, floatW rconde, floatW rcondv, float[] work, int lwork, int[] iwork, int liwork, boolean[] bwork, intW info)
/* 4406:     */   {
/* 4407:3711 */     Sgeesx.sgeesx(jobvs, sort, select, sense, n, a, 0, lda, sdim, wr, 0, wi, 0, vs, 0, ldvs, rconde, rcondv, work, 0, lwork, iwork, 0, liwork, bwork, 0, info);
/* 4408:     */   }
/* 4409:     */   
/* 4410:     */   public void sgeesx(String jobvs, String sort, Object select, String sense, int n, float[] a, int _a_offset, int lda, intW sdim, float[] wr, int _wr_offset, float[] wi, int _wi_offset, float[] vs, int _vs_offset, int ldvs, floatW rconde, floatW rcondv, float[] work, int _work_offset, int lwork, int[] iwork, int _iwork_offset, int liwork, boolean[] bwork, int _bwork_offset, intW info)
/* 4411:     */   {
/* 4412:3716 */     Sgeesx.sgeesx(jobvs, sort, select, sense, n, a, _a_offset, lda, sdim, wr, _wr_offset, wi, _wi_offset, vs, _vs_offset, ldvs, rconde, rcondv, work, _work_offset, lwork, iwork, _iwork_offset, liwork, bwork, _bwork_offset, info);
/* 4413:     */   }
/* 4414:     */   
/* 4415:     */   public void sgeev(String jobvl, String jobvr, int n, float[] a, int lda, float[] wr, float[] wi, float[] vl, int ldvl, float[] vr, int ldvr, float[] work, int lwork, intW info)
/* 4416:     */   {
/* 4417:3721 */     Sgeev.sgeev(jobvl, jobvr, n, a, 0, lda, wr, 0, wi, 0, vl, 0, ldvl, vr, 0, ldvr, work, 0, lwork, info);
/* 4418:     */   }
/* 4419:     */   
/* 4420:     */   public void sgeev(String jobvl, String jobvr, int n, float[] a, int _a_offset, int lda, float[] wr, int _wr_offset, float[] wi, int _wi_offset, float[] vl, int _vl_offset, int ldvl, float[] vr, int _vr_offset, int ldvr, float[] work, int _work_offset, int lwork, intW info)
/* 4421:     */   {
/* 4422:3726 */     Sgeev.sgeev(jobvl, jobvr, n, a, _a_offset, lda, wr, _wr_offset, wi, _wi_offset, vl, _vl_offset, ldvl, vr, _vr_offset, ldvr, work, _work_offset, lwork, info);
/* 4423:     */   }
/* 4424:     */   
/* 4425:     */   public void sgeevx(String balanc, String jobvl, String jobvr, String sense, int n, float[] a, int lda, float[] wr, float[] wi, float[] vl, int ldvl, float[] vr, int ldvr, intW ilo, intW ihi, float[] scale, floatW abnrm, float[] rconde, float[] rcondv, float[] work, int lwork, int[] iwork, intW info)
/* 4426:     */   {
/* 4427:3731 */     Sgeevx.sgeevx(balanc, jobvl, jobvr, sense, n, a, 0, lda, wr, 0, wi, 0, vl, 0, ldvl, vr, 0, ldvr, ilo, ihi, scale, 0, abnrm, rconde, 0, rcondv, 0, work, 0, lwork, iwork, 0, info);
/* 4428:     */   }
/* 4429:     */   
/* 4430:     */   public void sgeevx(String balanc, String jobvl, String jobvr, String sense, int n, float[] a, int _a_offset, int lda, float[] wr, int _wr_offset, float[] wi, int _wi_offset, float[] vl, int _vl_offset, int ldvl, float[] vr, int _vr_offset, int ldvr, intW ilo, intW ihi, float[] scale, int _scale_offset, floatW abnrm, float[] rconde, int _rconde_offset, float[] rcondv, int _rcondv_offset, float[] work, int _work_offset, int lwork, int[] iwork, int _iwork_offset, intW info)
/* 4431:     */   {
/* 4432:3736 */     Sgeevx.sgeevx(balanc, jobvl, jobvr, sense, n, a, _a_offset, lda, wr, _wr_offset, wi, _wi_offset, vl, _vl_offset, ldvl, vr, _vr_offset, ldvr, ilo, ihi, scale, _scale_offset, abnrm, rconde, _rconde_offset, rcondv, _rcondv_offset, work, _work_offset, lwork, iwork, _iwork_offset, info);
/* 4433:     */   }
/* 4434:     */   
/* 4435:     */   public void sgegs(String jobvsl, String jobvsr, int n, float[] a, int lda, float[] b, int ldb, float[] alphar, float[] alphai, float[] beta, float[] vsl, int ldvsl, float[] vsr, int ldvsr, float[] work, int lwork, intW info)
/* 4436:     */   {
/* 4437:3741 */     Sgegs.sgegs(jobvsl, jobvsr, n, a, 0, lda, b, 0, ldb, alphar, 0, alphai, 0, beta, 0, vsl, 0, ldvsl, vsr, 0, ldvsr, work, 0, lwork, info);
/* 4438:     */   }
/* 4439:     */   
/* 4440:     */   public void sgegs(String jobvsl, String jobvsr, int n, float[] a, int _a_offset, int lda, float[] b, int _b_offset, int ldb, float[] alphar, int _alphar_offset, float[] alphai, int _alphai_offset, float[] beta, int _beta_offset, float[] vsl, int _vsl_offset, int ldvsl, float[] vsr, int _vsr_offset, int ldvsr, float[] work, int _work_offset, int lwork, intW info)
/* 4441:     */   {
/* 4442:3746 */     Sgegs.sgegs(jobvsl, jobvsr, n, a, _a_offset, lda, b, _b_offset, ldb, alphar, _alphar_offset, alphai, _alphai_offset, beta, _beta_offset, vsl, _vsl_offset, ldvsl, vsr, _vsr_offset, ldvsr, work, _work_offset, lwork, info);
/* 4443:     */   }
/* 4444:     */   
/* 4445:     */   public void sgegv(String jobvl, String jobvr, int n, float[] a, int lda, float[] b, int ldb, float[] alphar, float[] alphai, float[] beta, float[] vl, int ldvl, float[] vr, int ldvr, float[] work, int lwork, intW info)
/* 4446:     */   {
/* 4447:3751 */     Sgegv.sgegv(jobvl, jobvr, n, a, 0, lda, b, 0, ldb, alphar, 0, alphai, 0, beta, 0, vl, 0, ldvl, vr, 0, ldvr, work, 0, lwork, info);
/* 4448:     */   }
/* 4449:     */   
/* 4450:     */   public void sgegv(String jobvl, String jobvr, int n, float[] a, int _a_offset, int lda, float[] b, int _b_offset, int ldb, float[] alphar, int _alphar_offset, float[] alphai, int _alphai_offset, float[] beta, int _beta_offset, float[] vl, int _vl_offset, int ldvl, float[] vr, int _vr_offset, int ldvr, float[] work, int _work_offset, int lwork, intW info)
/* 4451:     */   {
/* 4452:3756 */     Sgegv.sgegv(jobvl, jobvr, n, a, _a_offset, lda, b, _b_offset, ldb, alphar, _alphar_offset, alphai, _alphai_offset, beta, _beta_offset, vl, _vl_offset, ldvl, vr, _vr_offset, ldvr, work, _work_offset, lwork, info);
/* 4453:     */   }
/* 4454:     */   
/* 4455:     */   public void sgehd2(int n, int ilo, int ihi, float[] a, int lda, float[] tau, float[] work, intW info)
/* 4456:     */   {
/* 4457:3761 */     Sgehd2.sgehd2(n, ilo, ihi, a, 0, lda, tau, 0, work, 0, info);
/* 4458:     */   }
/* 4459:     */   
/* 4460:     */   public void sgehd2(int n, int ilo, int ihi, float[] a, int _a_offset, int lda, float[] tau, int _tau_offset, float[] work, int _work_offset, intW info)
/* 4461:     */   {
/* 4462:3766 */     Sgehd2.sgehd2(n, ilo, ihi, a, _a_offset, lda, tau, _tau_offset, work, _work_offset, info);
/* 4463:     */   }
/* 4464:     */   
/* 4465:     */   public void sgehrd(int n, int ilo, int ihi, float[] a, int lda, float[] tau, float[] work, int lwork, intW info)
/* 4466:     */   {
/* 4467:3771 */     Sgehrd.sgehrd(n, ilo, ihi, a, 0, lda, tau, 0, work, 0, lwork, info);
/* 4468:     */   }
/* 4469:     */   
/* 4470:     */   public void sgehrd(int n, int ilo, int ihi, float[] a, int _a_offset, int lda, float[] tau, int _tau_offset, float[] work, int _work_offset, int lwork, intW info)
/* 4471:     */   {
/* 4472:3776 */     Sgehrd.sgehrd(n, ilo, ihi, a, _a_offset, lda, tau, _tau_offset, work, _work_offset, lwork, info);
/* 4473:     */   }
/* 4474:     */   
/* 4475:     */   public void sgelq2(int m, int n, float[] a, int lda, float[] tau, float[] work, intW info)
/* 4476:     */   {
/* 4477:3781 */     Sgelq2.sgelq2(m, n, a, 0, lda, tau, 0, work, 0, info);
/* 4478:     */   }
/* 4479:     */   
/* 4480:     */   public void sgelq2(int m, int n, float[] a, int _a_offset, int lda, float[] tau, int _tau_offset, float[] work, int _work_offset, intW info)
/* 4481:     */   {
/* 4482:3786 */     Sgelq2.sgelq2(m, n, a, _a_offset, lda, tau, _tau_offset, work, _work_offset, info);
/* 4483:     */   }
/* 4484:     */   
/* 4485:     */   public void sgelqf(int m, int n, float[] a, int lda, float[] tau, float[] work, int lwork, intW info)
/* 4486:     */   {
/* 4487:3791 */     Sgelqf.sgelqf(m, n, a, 0, lda, tau, 0, work, 0, lwork, info);
/* 4488:     */   }
/* 4489:     */   
/* 4490:     */   public void sgelqf(int m, int n, float[] a, int _a_offset, int lda, float[] tau, int _tau_offset, float[] work, int _work_offset, int lwork, intW info)
/* 4491:     */   {
/* 4492:3796 */     Sgelqf.sgelqf(m, n, a, _a_offset, lda, tau, _tau_offset, work, _work_offset, lwork, info);
/* 4493:     */   }
/* 4494:     */   
/* 4495:     */   public void sgels(String trans, int m, int n, int nrhs, float[] a, int lda, float[] b, int ldb, float[] work, int lwork, intW info)
/* 4496:     */   {
/* 4497:3801 */     Sgels.sgels(trans, m, n, nrhs, a, 0, lda, b, 0, ldb, work, 0, lwork, info);
/* 4498:     */   }
/* 4499:     */   
/* 4500:     */   public void sgels(String trans, int m, int n, int nrhs, float[] a, int _a_offset, int lda, float[] b, int _b_offset, int ldb, float[] work, int _work_offset, int lwork, intW info)
/* 4501:     */   {
/* 4502:3806 */     Sgels.sgels(trans, m, n, nrhs, a, _a_offset, lda, b, _b_offset, ldb, work, _work_offset, lwork, info);
/* 4503:     */   }
/* 4504:     */   
/* 4505:     */   public void sgelsd(int m, int n, int nrhs, float[] a, int lda, float[] b, int ldb, float[] s, float rcond, intW rank, float[] work, int lwork, int[] iwork, intW info)
/* 4506:     */   {
/* 4507:3811 */     Sgelsd.sgelsd(m, n, nrhs, a, 0, lda, b, 0, ldb, s, 0, rcond, rank, work, 0, lwork, iwork, 0, info);
/* 4508:     */   }
/* 4509:     */   
/* 4510:     */   public void sgelsd(int m, int n, int nrhs, float[] a, int _a_offset, int lda, float[] b, int _b_offset, int ldb, float[] s, int _s_offset, float rcond, intW rank, float[] work, int _work_offset, int lwork, int[] iwork, int _iwork_offset, intW info)
/* 4511:     */   {
/* 4512:3816 */     Sgelsd.sgelsd(m, n, nrhs, a, _a_offset, lda, b, _b_offset, ldb, s, _s_offset, rcond, rank, work, _work_offset, lwork, iwork, _iwork_offset, info);
/* 4513:     */   }
/* 4514:     */   
/* 4515:     */   public void sgelss(int m, int n, int nrhs, float[] a, int lda, float[] b, int ldb, float[] s, float rcond, intW rank, float[] work, int lwork, intW info)
/* 4516:     */   {
/* 4517:3821 */     Sgelss.sgelss(m, n, nrhs, a, 0, lda, b, 0, ldb, s, 0, rcond, rank, work, 0, lwork, info);
/* 4518:     */   }
/* 4519:     */   
/* 4520:     */   public void sgelss(int m, int n, int nrhs, float[] a, int _a_offset, int lda, float[] b, int _b_offset, int ldb, float[] s, int _s_offset, float rcond, intW rank, float[] work, int _work_offset, int lwork, intW info)
/* 4521:     */   {
/* 4522:3826 */     Sgelss.sgelss(m, n, nrhs, a, _a_offset, lda, b, _b_offset, ldb, s, _s_offset, rcond, rank, work, _work_offset, lwork, info);
/* 4523:     */   }
/* 4524:     */   
/* 4525:     */   public void sgelsx(int m, int n, int nrhs, float[] a, int lda, float[] b, int ldb, int[] jpvt, float rcond, intW rank, float[] work, intW info)
/* 4526:     */   {
/* 4527:3831 */     Sgelsx.sgelsx(m, n, nrhs, a, 0, lda, b, 0, ldb, jpvt, 0, rcond, rank, work, 0, info);
/* 4528:     */   }
/* 4529:     */   
/* 4530:     */   public void sgelsx(int m, int n, int nrhs, float[] a, int _a_offset, int lda, float[] b, int _b_offset, int ldb, int[] jpvt, int _jpvt_offset, float rcond, intW rank, float[] work, int _work_offset, intW info)
/* 4531:     */   {
/* 4532:3836 */     Sgelsx.sgelsx(m, n, nrhs, a, _a_offset, lda, b, _b_offset, ldb, jpvt, _jpvt_offset, rcond, rank, work, _work_offset, info);
/* 4533:     */   }
/* 4534:     */   
/* 4535:     */   public void sgelsy(int m, int n, int nrhs, float[] a, int lda, float[] b, int ldb, int[] jpvt, float rcond, intW rank, float[] work, int lwork, intW info)
/* 4536:     */   {
/* 4537:3841 */     Sgelsy.sgelsy(m, n, nrhs, a, 0, lda, b, 0, ldb, jpvt, 0, rcond, rank, work, 0, lwork, info);
/* 4538:     */   }
/* 4539:     */   
/* 4540:     */   public void sgelsy(int m, int n, int nrhs, float[] a, int _a_offset, int lda, float[] b, int _b_offset, int ldb, int[] jpvt, int _jpvt_offset, float rcond, intW rank, float[] work, int _work_offset, int lwork, intW info)
/* 4541:     */   {
/* 4542:3846 */     Sgelsy.sgelsy(m, n, nrhs, a, _a_offset, lda, b, _b_offset, ldb, jpvt, _jpvt_offset, rcond, rank, work, _work_offset, lwork, info);
/* 4543:     */   }
/* 4544:     */   
/* 4545:     */   public void sgeql2(int m, int n, float[] a, int lda, float[] tau, float[] work, intW info)
/* 4546:     */   {
/* 4547:3851 */     Sgeql2.sgeql2(m, n, a, 0, lda, tau, 0, work, 0, info);
/* 4548:     */   }
/* 4549:     */   
/* 4550:     */   public void sgeql2(int m, int n, float[] a, int _a_offset, int lda, float[] tau, int _tau_offset, float[] work, int _work_offset, intW info)
/* 4551:     */   {
/* 4552:3856 */     Sgeql2.sgeql2(m, n, a, _a_offset, lda, tau, _tau_offset, work, _work_offset, info);
/* 4553:     */   }
/* 4554:     */   
/* 4555:     */   public void sgeqlf(int m, int n, float[] a, int lda, float[] tau, float[] work, int lwork, intW info)
/* 4556:     */   {
/* 4557:3861 */     Sgeqlf.sgeqlf(m, n, a, 0, lda, tau, 0, work, 0, lwork, info);
/* 4558:     */   }
/* 4559:     */   
/* 4560:     */   public void sgeqlf(int m, int n, float[] a, int _a_offset, int lda, float[] tau, int _tau_offset, float[] work, int _work_offset, int lwork, intW info)
/* 4561:     */   {
/* 4562:3866 */     Sgeqlf.sgeqlf(m, n, a, _a_offset, lda, tau, _tau_offset, work, _work_offset, lwork, info);
/* 4563:     */   }
/* 4564:     */   
/* 4565:     */   public void sgeqp3(int m, int n, float[] a, int lda, int[] jpvt, float[] tau, float[] work, int lwork, intW info)
/* 4566:     */   {
/* 4567:3871 */     Sgeqp3.sgeqp3(m, n, a, 0, lda, jpvt, 0, tau, 0, work, 0, lwork, info);
/* 4568:     */   }
/* 4569:     */   
/* 4570:     */   public void sgeqp3(int m, int n, float[] a, int _a_offset, int lda, int[] jpvt, int _jpvt_offset, float[] tau, int _tau_offset, float[] work, int _work_offset, int lwork, intW info)
/* 4571:     */   {
/* 4572:3876 */     Sgeqp3.sgeqp3(m, n, a, _a_offset, lda, jpvt, _jpvt_offset, tau, _tau_offset, work, _work_offset, lwork, info);
/* 4573:     */   }
/* 4574:     */   
/* 4575:     */   public void sgeqpf(int m, int n, float[] a, int lda, int[] jpvt, float[] tau, float[] work, intW info)
/* 4576:     */   {
/* 4577:3881 */     Sgeqpf.sgeqpf(m, n, a, 0, lda, jpvt, 0, tau, 0, work, 0, info);
/* 4578:     */   }
/* 4579:     */   
/* 4580:     */   public void sgeqpf(int m, int n, float[] a, int _a_offset, int lda, int[] jpvt, int _jpvt_offset, float[] tau, int _tau_offset, float[] work, int _work_offset, intW info)
/* 4581:     */   {
/* 4582:3886 */     Sgeqpf.sgeqpf(m, n, a, _a_offset, lda, jpvt, _jpvt_offset, tau, _tau_offset, work, _work_offset, info);
/* 4583:     */   }
/* 4584:     */   
/* 4585:     */   public void sgeqr2(int m, int n, float[] a, int lda, float[] tau, float[] work, intW info)
/* 4586:     */   {
/* 4587:3891 */     Sgeqr2.sgeqr2(m, n, a, 0, lda, tau, 0, work, 0, info);
/* 4588:     */   }
/* 4589:     */   
/* 4590:     */   public void sgeqr2(int m, int n, float[] a, int _a_offset, int lda, float[] tau, int _tau_offset, float[] work, int _work_offset, intW info)
/* 4591:     */   {
/* 4592:3896 */     Sgeqr2.sgeqr2(m, n, a, _a_offset, lda, tau, _tau_offset, work, _work_offset, info);
/* 4593:     */   }
/* 4594:     */   
/* 4595:     */   public void sgeqrf(int m, int n, float[] a, int lda, float[] tau, float[] work, int lwork, intW info)
/* 4596:     */   {
/* 4597:3901 */     Sgeqrf.sgeqrf(m, n, a, 0, lda, tau, 0, work, 0, lwork, info);
/* 4598:     */   }
/* 4599:     */   
/* 4600:     */   public void sgeqrf(int m, int n, float[] a, int _a_offset, int lda, float[] tau, int _tau_offset, float[] work, int _work_offset, int lwork, intW info)
/* 4601:     */   {
/* 4602:3906 */     Sgeqrf.sgeqrf(m, n, a, _a_offset, lda, tau, _tau_offset, work, _work_offset, lwork, info);
/* 4603:     */   }
/* 4604:     */   
/* 4605:     */   public void sgerfs(String trans, int n, int nrhs, float[] a, int lda, float[] af, int ldaf, int[] ipiv, float[] b, int ldb, float[] x, int ldx, float[] ferr, float[] berr, float[] work, int[] iwork, intW info)
/* 4606:     */   {
/* 4607:3911 */     Sgerfs.sgerfs(trans, n, nrhs, a, 0, lda, af, 0, ldaf, ipiv, 0, b, 0, ldb, x, 0, ldx, ferr, 0, berr, 0, work, 0, iwork, 0, info);
/* 4608:     */   }
/* 4609:     */   
/* 4610:     */   public void sgerfs(String trans, int n, int nrhs, float[] a, int _a_offset, int lda, float[] af, int _af_offset, int ldaf, int[] ipiv, int _ipiv_offset, float[] b, int _b_offset, int ldb, float[] x, int _x_offset, int ldx, float[] ferr, int _ferr_offset, float[] berr, int _berr_offset, float[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 4611:     */   {
/* 4612:3916 */     Sgerfs.sgerfs(trans, n, nrhs, a, _a_offset, lda, af, _af_offset, ldaf, ipiv, _ipiv_offset, b, _b_offset, ldb, x, _x_offset, ldx, ferr, _ferr_offset, berr, _berr_offset, work, _work_offset, iwork, _iwork_offset, info);
/* 4613:     */   }
/* 4614:     */   
/* 4615:     */   public void sgerq2(int m, int n, float[] a, int lda, float[] tau, float[] work, intW info)
/* 4616:     */   {
/* 4617:3921 */     Sgerq2.sgerq2(m, n, a, 0, lda, tau, 0, work, 0, info);
/* 4618:     */   }
/* 4619:     */   
/* 4620:     */   public void sgerq2(int m, int n, float[] a, int _a_offset, int lda, float[] tau, int _tau_offset, float[] work, int _work_offset, intW info)
/* 4621:     */   {
/* 4622:3926 */     Sgerq2.sgerq2(m, n, a, _a_offset, lda, tau, _tau_offset, work, _work_offset, info);
/* 4623:     */   }
/* 4624:     */   
/* 4625:     */   public void sgerqf(int m, int n, float[] a, int lda, float[] tau, float[] work, int lwork, intW info)
/* 4626:     */   {
/* 4627:3931 */     Sgerqf.sgerqf(m, n, a, 0, lda, tau, 0, work, 0, lwork, info);
/* 4628:     */   }
/* 4629:     */   
/* 4630:     */   public void sgerqf(int m, int n, float[] a, int _a_offset, int lda, float[] tau, int _tau_offset, float[] work, int _work_offset, int lwork, intW info)
/* 4631:     */   {
/* 4632:3936 */     Sgerqf.sgerqf(m, n, a, _a_offset, lda, tau, _tau_offset, work, _work_offset, lwork, info);
/* 4633:     */   }
/* 4634:     */   
/* 4635:     */   public void sgesc2(int n, float[] a, int lda, float[] rhs, int[] ipiv, int[] jpiv, floatW scale)
/* 4636:     */   {
/* 4637:3941 */     Sgesc2.sgesc2(n, a, 0, lda, rhs, 0, ipiv, 0, jpiv, 0, scale);
/* 4638:     */   }
/* 4639:     */   
/* 4640:     */   public void sgesc2(int n, float[] a, int _a_offset, int lda, float[] rhs, int _rhs_offset, int[] ipiv, int _ipiv_offset, int[] jpiv, int _jpiv_offset, floatW scale)
/* 4641:     */   {
/* 4642:3946 */     Sgesc2.sgesc2(n, a, _a_offset, lda, rhs, _rhs_offset, ipiv, _ipiv_offset, jpiv, _jpiv_offset, scale);
/* 4643:     */   }
/* 4644:     */   
/* 4645:     */   public void sgesdd(String jobz, int m, int n, float[] a, int lda, float[] s, float[] u, int ldu, float[] vt, int ldvt, float[] work, int lwork, int[] iwork, intW info)
/* 4646:     */   {
/* 4647:3951 */     Sgesdd.sgesdd(jobz, m, n, a, 0, lda, s, 0, u, 0, ldu, vt, 0, ldvt, work, 0, lwork, iwork, 0, info);
/* 4648:     */   }
/* 4649:     */   
/* 4650:     */   public void sgesdd(String jobz, int m, int n, float[] a, int _a_offset, int lda, float[] s, int _s_offset, float[] u, int _u_offset, int ldu, float[] vt, int _vt_offset, int ldvt, float[] work, int _work_offset, int lwork, int[] iwork, int _iwork_offset, intW info)
/* 4651:     */   {
/* 4652:3956 */     Sgesdd.sgesdd(jobz, m, n, a, _a_offset, lda, s, _s_offset, u, _u_offset, ldu, vt, _vt_offset, ldvt, work, _work_offset, lwork, iwork, _iwork_offset, info);
/* 4653:     */   }
/* 4654:     */   
/* 4655:     */   public void sgesv(int n, int nrhs, float[] a, int lda, int[] ipiv, float[] b, int ldb, intW info)
/* 4656:     */   {
/* 4657:3961 */     Sgesv.sgesv(n, nrhs, a, 0, lda, ipiv, 0, b, 0, ldb, info);
/* 4658:     */   }
/* 4659:     */   
/* 4660:     */   public void sgesv(int n, int nrhs, float[] a, int _a_offset, int lda, int[] ipiv, int _ipiv_offset, float[] b, int _b_offset, int ldb, intW info)
/* 4661:     */   {
/* 4662:3966 */     Sgesv.sgesv(n, nrhs, a, _a_offset, lda, ipiv, _ipiv_offset, b, _b_offset, ldb, info);
/* 4663:     */   }
/* 4664:     */   
/* 4665:     */   public void sgesvd(String jobu, String jobvt, int m, int n, float[] a, int lda, float[] s, float[] u, int ldu, float[] vt, int ldvt, float[] work, int lwork, intW info)
/* 4666:     */   {
/* 4667:3971 */     Sgesvd.sgesvd(jobu, jobvt, m, n, a, 0, lda, s, 0, u, 0, ldu, vt, 0, ldvt, work, 0, lwork, info);
/* 4668:     */   }
/* 4669:     */   
/* 4670:     */   public void sgesvd(String jobu, String jobvt, int m, int n, float[] a, int _a_offset, int lda, float[] s, int _s_offset, float[] u, int _u_offset, int ldu, float[] vt, int _vt_offset, int ldvt, float[] work, int _work_offset, int lwork, intW info)
/* 4671:     */   {
/* 4672:3976 */     Sgesvd.sgesvd(jobu, jobvt, m, n, a, _a_offset, lda, s, _s_offset, u, _u_offset, ldu, vt, _vt_offset, ldvt, work, _work_offset, lwork, info);
/* 4673:     */   }
/* 4674:     */   
/* 4675:     */   public void sgesvx(String fact, String trans, int n, int nrhs, float[] a, int lda, float[] af, int ldaf, int[] ipiv, StringW equed, float[] r, float[] c, float[] b, int ldb, float[] x, int ldx, floatW rcond, float[] ferr, float[] berr, float[] work, int[] iwork, intW info)
/* 4676:     */   {
/* 4677:3981 */     Sgesvx.sgesvx(fact, trans, n, nrhs, a, 0, lda, af, 0, ldaf, ipiv, 0, equed, r, 0, c, 0, b, 0, ldb, x, 0, ldx, rcond, ferr, 0, berr, 0, work, 0, iwork, 0, info);
/* 4678:     */   }
/* 4679:     */   
/* 4680:     */   public void sgesvx(String fact, String trans, int n, int nrhs, float[] a, int _a_offset, int lda, float[] af, int _af_offset, int ldaf, int[] ipiv, int _ipiv_offset, StringW equed, float[] r, int _r_offset, float[] c, int _c_offset, float[] b, int _b_offset, int ldb, float[] x, int _x_offset, int ldx, floatW rcond, float[] ferr, int _ferr_offset, float[] berr, int _berr_offset, float[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 4681:     */   {
/* 4682:3986 */     Sgesvx.sgesvx(fact, trans, n, nrhs, a, _a_offset, lda, af, _af_offset, ldaf, ipiv, _ipiv_offset, equed, r, _r_offset, c, _c_offset, b, _b_offset, ldb, x, _x_offset, ldx, rcond, ferr, _ferr_offset, berr, _berr_offset, work, _work_offset, iwork, _iwork_offset, info);
/* 4683:     */   }
/* 4684:     */   
/* 4685:     */   public void sgetc2(int n, float[] a, int lda, int[] ipiv, int[] jpiv, intW info)
/* 4686:     */   {
/* 4687:3991 */     Sgetc2.sgetc2(n, a, 0, lda, ipiv, 0, jpiv, 0, info);
/* 4688:     */   }
/* 4689:     */   
/* 4690:     */   public void sgetc2(int n, float[] a, int _a_offset, int lda, int[] ipiv, int _ipiv_offset, int[] jpiv, int _jpiv_offset, intW info)
/* 4691:     */   {
/* 4692:3996 */     Sgetc2.sgetc2(n, a, _a_offset, lda, ipiv, _ipiv_offset, jpiv, _jpiv_offset, info);
/* 4693:     */   }
/* 4694:     */   
/* 4695:     */   public void sgetf2(int m, int n, float[] a, int lda, int[] ipiv, intW info)
/* 4696:     */   {
/* 4697:4001 */     Sgetf2.sgetf2(m, n, a, 0, lda, ipiv, 0, info);
/* 4698:     */   }
/* 4699:     */   
/* 4700:     */   public void sgetf2(int m, int n, float[] a, int _a_offset, int lda, int[] ipiv, int _ipiv_offset, intW info)
/* 4701:     */   {
/* 4702:4006 */     Sgetf2.sgetf2(m, n, a, _a_offset, lda, ipiv, _ipiv_offset, info);
/* 4703:     */   }
/* 4704:     */   
/* 4705:     */   public void sgetrf(int m, int n, float[] a, int lda, int[] ipiv, intW info)
/* 4706:     */   {
/* 4707:4011 */     Sgetrf.sgetrf(m, n, a, 0, lda, ipiv, 0, info);
/* 4708:     */   }
/* 4709:     */   
/* 4710:     */   public void sgetrf(int m, int n, float[] a, int _a_offset, int lda, int[] ipiv, int _ipiv_offset, intW info)
/* 4711:     */   {
/* 4712:4016 */     Sgetrf.sgetrf(m, n, a, _a_offset, lda, ipiv, _ipiv_offset, info);
/* 4713:     */   }
/* 4714:     */   
/* 4715:     */   public void sgetri(int n, float[] a, int lda, int[] ipiv, float[] work, int lwork, intW info)
/* 4716:     */   {
/* 4717:4021 */     Sgetri.sgetri(n, a, 0, lda, ipiv, 0, work, 0, lwork, info);
/* 4718:     */   }
/* 4719:     */   
/* 4720:     */   public void sgetri(int n, float[] a, int _a_offset, int lda, int[] ipiv, int _ipiv_offset, float[] work, int _work_offset, int lwork, intW info)
/* 4721:     */   {
/* 4722:4026 */     Sgetri.sgetri(n, a, _a_offset, lda, ipiv, _ipiv_offset, work, _work_offset, lwork, info);
/* 4723:     */   }
/* 4724:     */   
/* 4725:     */   public void sgetrs(String trans, int n, int nrhs, float[] a, int lda, int[] ipiv, float[] b, int ldb, intW info)
/* 4726:     */   {
/* 4727:4031 */     Sgetrs.sgetrs(trans, n, nrhs, a, 0, lda, ipiv, 0, b, 0, ldb, info);
/* 4728:     */   }
/* 4729:     */   
/* 4730:     */   public void sgetrs(String trans, int n, int nrhs, float[] a, int _a_offset, int lda, int[] ipiv, int _ipiv_offset, float[] b, int _b_offset, int ldb, intW info)
/* 4731:     */   {
/* 4732:4036 */     Sgetrs.sgetrs(trans, n, nrhs, a, _a_offset, lda, ipiv, _ipiv_offset, b, _b_offset, ldb, info);
/* 4733:     */   }
/* 4734:     */   
/* 4735:     */   public void sggbak(String job, String side, int n, int ilo, int ihi, float[] lscale, float[] rscale, int m, float[] v, int ldv, intW info)
/* 4736:     */   {
/* 4737:4041 */     Sggbak.sggbak(job, side, n, ilo, ihi, lscale, 0, rscale, 0, m, v, 0, ldv, info);
/* 4738:     */   }
/* 4739:     */   
/* 4740:     */   public void sggbak(String job, String side, int n, int ilo, int ihi, float[] lscale, int _lscale_offset, float[] rscale, int _rscale_offset, int m, float[] v, int _v_offset, int ldv, intW info)
/* 4741:     */   {
/* 4742:4046 */     Sggbak.sggbak(job, side, n, ilo, ihi, lscale, _lscale_offset, rscale, _rscale_offset, m, v, _v_offset, ldv, info);
/* 4743:     */   }
/* 4744:     */   
/* 4745:     */   public void sggbal(String job, int n, float[] a, int lda, float[] b, int ldb, intW ilo, intW ihi, float[] lscale, float[] rscale, float[] work, intW info)
/* 4746:     */   {
/* 4747:4051 */     Sggbal.sggbal(job, n, a, 0, lda, b, 0, ldb, ilo, ihi, lscale, 0, rscale, 0, work, 0, info);
/* 4748:     */   }
/* 4749:     */   
/* 4750:     */   public void sggbal(String job, int n, float[] a, int _a_offset, int lda, float[] b, int _b_offset, int ldb, intW ilo, intW ihi, float[] lscale, int _lscale_offset, float[] rscale, int _rscale_offset, float[] work, int _work_offset, intW info)
/* 4751:     */   {
/* 4752:4056 */     Sggbal.sggbal(job, n, a, _a_offset, lda, b, _b_offset, ldb, ilo, ihi, lscale, _lscale_offset, rscale, _rscale_offset, work, _work_offset, info);
/* 4753:     */   }
/* 4754:     */   
/* 4755:     */   public void sgges(String jobvsl, String jobvsr, String sort, Object selctg, int n, float[] a, int lda, float[] b, int ldb, intW sdim, float[] alphar, float[] alphai, float[] beta, float[] vsl, int ldvsl, float[] vsr, int ldvsr, float[] work, int lwork, boolean[] bwork, intW info)
/* 4756:     */   {
/* 4757:4061 */     Sgges.sgges(jobvsl, jobvsr, sort, selctg, n, a, 0, lda, b, 0, ldb, sdim, alphar, 0, alphai, 0, beta, 0, vsl, 0, ldvsl, vsr, 0, ldvsr, work, 0, lwork, bwork, 0, info);
/* 4758:     */   }
/* 4759:     */   
/* 4760:     */   public void sgges(String jobvsl, String jobvsr, String sort, Object selctg, int n, float[] a, int _a_offset, int lda, float[] b, int _b_offset, int ldb, intW sdim, float[] alphar, int _alphar_offset, float[] alphai, int _alphai_offset, float[] beta, int _beta_offset, float[] vsl, int _vsl_offset, int ldvsl, float[] vsr, int _vsr_offset, int ldvsr, float[] work, int _work_offset, int lwork, boolean[] bwork, int _bwork_offset, intW info)
/* 4761:     */   {
/* 4762:4066 */     Sgges.sgges(jobvsl, jobvsr, sort, selctg, n, a, _a_offset, lda, b, _b_offset, ldb, sdim, alphar, _alphar_offset, alphai, _alphai_offset, beta, _beta_offset, vsl, _vsl_offset, ldvsl, vsr, _vsr_offset, ldvsr, work, _work_offset, lwork, bwork, _bwork_offset, info);
/* 4763:     */   }
/* 4764:     */   
/* 4765:     */   public void sggesx(String jobvsl, String jobvsr, String sort, Object selctg, String sense, int n, float[] a, int lda, float[] b, int ldb, intW sdim, float[] alphar, float[] alphai, float[] beta, float[] vsl, int ldvsl, float[] vsr, int ldvsr, float[] rconde, float[] rcondv, float[] work, int lwork, int[] iwork, int liwork, boolean[] bwork, intW info)
/* 4766:     */   {
/* 4767:4071 */     Sggesx.sggesx(jobvsl, jobvsr, sort, selctg, sense, n, a, 0, lda, b, 0, ldb, sdim, alphar, 0, alphai, 0, beta, 0, vsl, 0, ldvsl, vsr, 0, ldvsr, rconde, 0, rcondv, 0, work, 0, lwork, iwork, 0, liwork, bwork, 0, info);
/* 4768:     */   }
/* 4769:     */   
/* 4770:     */   public void sggesx(String jobvsl, String jobvsr, String sort, Object selctg, String sense, int n, float[] a, int _a_offset, int lda, float[] b, int _b_offset, int ldb, intW sdim, float[] alphar, int _alphar_offset, float[] alphai, int _alphai_offset, float[] beta, int _beta_offset, float[] vsl, int _vsl_offset, int ldvsl, float[] vsr, int _vsr_offset, int ldvsr, float[] rconde, int _rconde_offset, float[] rcondv, int _rcondv_offset, float[] work, int _work_offset, int lwork, int[] iwork, int _iwork_offset, int liwork, boolean[] bwork, int _bwork_offset, intW info)
/* 4771:     */   {
/* 4772:4076 */     Sggesx.sggesx(jobvsl, jobvsr, sort, selctg, sense, n, a, _a_offset, lda, b, _b_offset, ldb, sdim, alphar, _alphar_offset, alphai, _alphai_offset, beta, _beta_offset, vsl, _vsl_offset, ldvsl, vsr, _vsr_offset, ldvsr, rconde, _rconde_offset, rcondv, _rcondv_offset, work, _work_offset, lwork, iwork, _iwork_offset, liwork, bwork, _bwork_offset, info);
/* 4773:     */   }
/* 4774:     */   
/* 4775:     */   public void sggev(String jobvl, String jobvr, int n, float[] a, int lda, float[] b, int ldb, float[] alphar, float[] alphai, float[] beta, float[] vl, int ldvl, float[] vr, int ldvr, float[] work, int lwork, intW info)
/* 4776:     */   {
/* 4777:4081 */     Sggev.sggev(jobvl, jobvr, n, a, 0, lda, b, 0, ldb, alphar, 0, alphai, 0, beta, 0, vl, 0, ldvl, vr, 0, ldvr, work, 0, lwork, info);
/* 4778:     */   }
/* 4779:     */   
/* 4780:     */   public void sggev(String jobvl, String jobvr, int n, float[] a, int _a_offset, int lda, float[] b, int _b_offset, int ldb, float[] alphar, int _alphar_offset, float[] alphai, int _alphai_offset, float[] beta, int _beta_offset, float[] vl, int _vl_offset, int ldvl, float[] vr, int _vr_offset, int ldvr, float[] work, int _work_offset, int lwork, intW info)
/* 4781:     */   {
/* 4782:4086 */     Sggev.sggev(jobvl, jobvr, n, a, _a_offset, lda, b, _b_offset, ldb, alphar, _alphar_offset, alphai, _alphai_offset, beta, _beta_offset, vl, _vl_offset, ldvl, vr, _vr_offset, ldvr, work, _work_offset, lwork, info);
/* 4783:     */   }
/* 4784:     */   
/* 4785:     */   public void sggevx(String balanc, String jobvl, String jobvr, String sense, int n, float[] a, int lda, float[] b, int ldb, float[] alphar, float[] alphai, float[] beta, float[] vl, int ldvl, float[] vr, int ldvr, intW ilo, intW ihi, float[] lscale, float[] rscale, floatW abnrm, floatW bbnrm, float[] rconde, float[] rcondv, float[] work, int lwork, int[] iwork, boolean[] bwork, intW info)
/* 4786:     */   {
/* 4787:4091 */     Sggevx.sggevx(balanc, jobvl, jobvr, sense, n, a, 0, lda, b, 0, ldb, alphar, 0, alphai, 0, beta, 0, vl, 0, ldvl, vr, 0, ldvr, ilo, ihi, lscale, 0, rscale, 0, abnrm, bbnrm, rconde, 0, rcondv, 0, work, 0, lwork, iwork, 0, bwork, 0, info);
/* 4788:     */   }
/* 4789:     */   
/* 4790:     */   public void sggevx(String balanc, String jobvl, String jobvr, String sense, int n, float[] a, int _a_offset, int lda, float[] b, int _b_offset, int ldb, float[] alphar, int _alphar_offset, float[] alphai, int _alphai_offset, float[] beta, int _beta_offset, float[] vl, int _vl_offset, int ldvl, float[] vr, int _vr_offset, int ldvr, intW ilo, intW ihi, float[] lscale, int _lscale_offset, float[] rscale, int _rscale_offset, floatW abnrm, floatW bbnrm, float[] rconde, int _rconde_offset, float[] rcondv, int _rcondv_offset, float[] work, int _work_offset, int lwork, int[] iwork, int _iwork_offset, boolean[] bwork, int _bwork_offset, intW info)
/* 4791:     */   {
/* 4792:4096 */     Sggevx.sggevx(balanc, jobvl, jobvr, sense, n, a, _a_offset, lda, b, _b_offset, ldb, alphar, _alphar_offset, alphai, _alphai_offset, beta, _beta_offset, vl, _vl_offset, ldvl, vr, _vr_offset, ldvr, ilo, ihi, lscale, _lscale_offset, rscale, _rscale_offset, abnrm, bbnrm, rconde, _rconde_offset, rcondv, _rcondv_offset, work, _work_offset, lwork, iwork, _iwork_offset, bwork, _bwork_offset, info);
/* 4793:     */   }
/* 4794:     */   
/* 4795:     */   public void sggglm(int n, int m, int p, float[] a, int lda, float[] b, int ldb, float[] d, float[] x, float[] y, float[] work, int lwork, intW info)
/* 4796:     */   {
/* 4797:4101 */     Sggglm.sggglm(n, m, p, a, 0, lda, b, 0, ldb, d, 0, x, 0, y, 0, work, 0, lwork, info);
/* 4798:     */   }
/* 4799:     */   
/* 4800:     */   public void sggglm(int n, int m, int p, float[] a, int _a_offset, int lda, float[] b, int _b_offset, int ldb, float[] d, int _d_offset, float[] x, int _x_offset, float[] y, int _y_offset, float[] work, int _work_offset, int lwork, intW info)
/* 4801:     */   {
/* 4802:4106 */     Sggglm.sggglm(n, m, p, a, _a_offset, lda, b, _b_offset, ldb, d, _d_offset, x, _x_offset, y, _y_offset, work, _work_offset, lwork, info);
/* 4803:     */   }
/* 4804:     */   
/* 4805:     */   public void sgghrd(String compq, String compz, int n, int ilo, int ihi, float[] a, int lda, float[] b, int ldb, float[] q, int ldq, float[] z, int ldz, intW info)
/* 4806:     */   {
/* 4807:4111 */     Sgghrd.sgghrd(compq, compz, n, ilo, ihi, a, 0, lda, b, 0, ldb, q, 0, ldq, z, 0, ldz, info);
/* 4808:     */   }
/* 4809:     */   
/* 4810:     */   public void sgghrd(String compq, String compz, int n, int ilo, int ihi, float[] a, int _a_offset, int lda, float[] b, int _b_offset, int ldb, float[] q, int _q_offset, int ldq, float[] z, int _z_offset, int ldz, intW info)
/* 4811:     */   {
/* 4812:4116 */     Sgghrd.sgghrd(compq, compz, n, ilo, ihi, a, _a_offset, lda, b, _b_offset, ldb, q, _q_offset, ldq, z, _z_offset, ldz, info);
/* 4813:     */   }
/* 4814:     */   
/* 4815:     */   public void sgglse(int m, int n, int p, float[] a, int lda, float[] b, int ldb, float[] c, float[] d, float[] x, float[] work, int lwork, intW info)
/* 4816:     */   {
/* 4817:4121 */     Sgglse.sgglse(m, n, p, a, 0, lda, b, 0, ldb, c, 0, d, 0, x, 0, work, 0, lwork, info);
/* 4818:     */   }
/* 4819:     */   
/* 4820:     */   public void sgglse(int m, int n, int p, float[] a, int _a_offset, int lda, float[] b, int _b_offset, int ldb, float[] c, int _c_offset, float[] d, int _d_offset, float[] x, int _x_offset, float[] work, int _work_offset, int lwork, intW info)
/* 4821:     */   {
/* 4822:4126 */     Sgglse.sgglse(m, n, p, a, _a_offset, lda, b, _b_offset, ldb, c, _c_offset, d, _d_offset, x, _x_offset, work, _work_offset, lwork, info);
/* 4823:     */   }
/* 4824:     */   
/* 4825:     */   public void sggqrf(int n, int m, int p, float[] a, int lda, float[] taua, float[] b, int ldb, float[] taub, float[] work, int lwork, intW info)
/* 4826:     */   {
/* 4827:4131 */     Sggqrf.sggqrf(n, m, p, a, 0, lda, taua, 0, b, 0, ldb, taub, 0, work, 0, lwork, info);
/* 4828:     */   }
/* 4829:     */   
/* 4830:     */   public void sggqrf(int n, int m, int p, float[] a, int _a_offset, int lda, float[] taua, int _taua_offset, float[] b, int _b_offset, int ldb, float[] taub, int _taub_offset, float[] work, int _work_offset, int lwork, intW info)
/* 4831:     */   {
/* 4832:4136 */     Sggqrf.sggqrf(n, m, p, a, _a_offset, lda, taua, _taua_offset, b, _b_offset, ldb, taub, _taub_offset, work, _work_offset, lwork, info);
/* 4833:     */   }
/* 4834:     */   
/* 4835:     */   public void sggrqf(int m, int p, int n, float[] a, int lda, float[] taua, float[] b, int ldb, float[] taub, float[] work, int lwork, intW info)
/* 4836:     */   {
/* 4837:4141 */     Sggrqf.sggrqf(m, p, n, a, 0, lda, taua, 0, b, 0, ldb, taub, 0, work, 0, lwork, info);
/* 4838:     */   }
/* 4839:     */   
/* 4840:     */   public void sggrqf(int m, int p, int n, float[] a, int _a_offset, int lda, float[] taua, int _taua_offset, float[] b, int _b_offset, int ldb, float[] taub, int _taub_offset, float[] work, int _work_offset, int lwork, intW info)
/* 4841:     */   {
/* 4842:4146 */     Sggrqf.sggrqf(m, p, n, a, _a_offset, lda, taua, _taua_offset, b, _b_offset, ldb, taub, _taub_offset, work, _work_offset, lwork, info);
/* 4843:     */   }
/* 4844:     */   
/* 4845:     */   public void sggsvd(String jobu, String jobv, String jobq, int m, int n, int p, intW k, intW l, float[] a, int lda, float[] b, int ldb, float[] alpha, float[] beta, float[] u, int ldu, float[] v, int ldv, float[] q, int ldq, float[] work, int[] iwork, intW info)
/* 4846:     */   {
/* 4847:4151 */     Sggsvd.sggsvd(jobu, jobv, jobq, m, n, p, k, l, a, 0, lda, b, 0, ldb, alpha, 0, beta, 0, u, 0, ldu, v, 0, ldv, q, 0, ldq, work, 0, iwork, 0, info);
/* 4848:     */   }
/* 4849:     */   
/* 4850:     */   public void sggsvd(String jobu, String jobv, String jobq, int m, int n, int p, intW k, intW l, float[] a, int _a_offset, int lda, float[] b, int _b_offset, int ldb, float[] alpha, int _alpha_offset, float[] beta, int _beta_offset, float[] u, int _u_offset, int ldu, float[] v, int _v_offset, int ldv, float[] q, int _q_offset, int ldq, float[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 4851:     */   {
/* 4852:4156 */     Sggsvd.sggsvd(jobu, jobv, jobq, m, n, p, k, l, a, _a_offset, lda, b, _b_offset, ldb, alpha, _alpha_offset, beta, _beta_offset, u, _u_offset, ldu, v, _v_offset, ldv, q, _q_offset, ldq, work, _work_offset, iwork, _iwork_offset, info);
/* 4853:     */   }
/* 4854:     */   
/* 4855:     */   public void sggsvp(String jobu, String jobv, String jobq, int m, int p, int n, float[] a, int lda, float[] b, int ldb, float tola, float tolb, intW k, intW l, float[] u, int ldu, float[] v, int ldv, float[] q, int ldq, int[] iwork, float[] tau, float[] work, intW info)
/* 4856:     */   {
/* 4857:4161 */     Sggsvp.sggsvp(jobu, jobv, jobq, m, p, n, a, 0, lda, b, 0, ldb, tola, tolb, k, l, u, 0, ldu, v, 0, ldv, q, 0, ldq, iwork, 0, tau, 0, work, 0, info);
/* 4858:     */   }
/* 4859:     */   
/* 4860:     */   public void sggsvp(String jobu, String jobv, String jobq, int m, int p, int n, float[] a, int _a_offset, int lda, float[] b, int _b_offset, int ldb, float tola, float tolb, intW k, intW l, float[] u, int _u_offset, int ldu, float[] v, int _v_offset, int ldv, float[] q, int _q_offset, int ldq, int[] iwork, int _iwork_offset, float[] tau, int _tau_offset, float[] work, int _work_offset, intW info)
/* 4861:     */   {
/* 4862:4166 */     Sggsvp.sggsvp(jobu, jobv, jobq, m, p, n, a, _a_offset, lda, b, _b_offset, ldb, tola, tolb, k, l, u, _u_offset, ldu, v, _v_offset, ldv, q, _q_offset, ldq, iwork, _iwork_offset, tau, _tau_offset, work, _work_offset, info);
/* 4863:     */   }
/* 4864:     */   
/* 4865:     */   public void sgtcon(String norm, int n, float[] dl, float[] d, float[] du, float[] du2, int[] ipiv, float anorm, floatW rcond, float[] work, int[] iwork, intW info)
/* 4866:     */   {
/* 4867:4171 */     Sgtcon.sgtcon(norm, n, dl, 0, d, 0, du, 0, du2, 0, ipiv, 0, anorm, rcond, work, 0, iwork, 0, info);
/* 4868:     */   }
/* 4869:     */   
/* 4870:     */   public void sgtcon(String norm, int n, float[] dl, int _dl_offset, float[] d, int _d_offset, float[] du, int _du_offset, float[] du2, int _du2_offset, int[] ipiv, int _ipiv_offset, float anorm, floatW rcond, float[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 4871:     */   {
/* 4872:4176 */     Sgtcon.sgtcon(norm, n, dl, _dl_offset, d, _d_offset, du, _du_offset, du2, _du2_offset, ipiv, _ipiv_offset, anorm, rcond, work, _work_offset, iwork, _iwork_offset, info);
/* 4873:     */   }
/* 4874:     */   
/* 4875:     */   public void sgtrfs(String trans, int n, int nrhs, float[] dl, float[] d, float[] du, float[] dlf, float[] df, float[] duf, float[] du2, int[] ipiv, float[] b, int ldb, float[] x, int ldx, float[] ferr, float[] berr, float[] work, int[] iwork, intW info)
/* 4876:     */   {
/* 4877:4181 */     Sgtrfs.sgtrfs(trans, n, nrhs, dl, 0, d, 0, du, 0, dlf, 0, df, 0, duf, 0, du2, 0, ipiv, 0, b, 0, ldb, x, 0, ldx, ferr, 0, berr, 0, work, 0, iwork, 0, info);
/* 4878:     */   }
/* 4879:     */   
/* 4880:     */   public void sgtrfs(String trans, int n, int nrhs, float[] dl, int _dl_offset, float[] d, int _d_offset, float[] du, int _du_offset, float[] dlf, int _dlf_offset, float[] df, int _df_offset, float[] duf, int _duf_offset, float[] du2, int _du2_offset, int[] ipiv, int _ipiv_offset, float[] b, int _b_offset, int ldb, float[] x, int _x_offset, int ldx, float[] ferr, int _ferr_offset, float[] berr, int _berr_offset, float[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 4881:     */   {
/* 4882:4186 */     Sgtrfs.sgtrfs(trans, n, nrhs, dl, _dl_offset, d, _d_offset, du, _du_offset, dlf, _dlf_offset, df, _df_offset, duf, _duf_offset, du2, _du2_offset, ipiv, _ipiv_offset, b, _b_offset, ldb, x, _x_offset, ldx, ferr, _ferr_offset, berr, _berr_offset, work, _work_offset, iwork, _iwork_offset, info);
/* 4883:     */   }
/* 4884:     */   
/* 4885:     */   public void sgtsv(int n, int nrhs, float[] dl, float[] d, float[] du, float[] b, int ldb, intW info)
/* 4886:     */   {
/* 4887:4191 */     Sgtsv.sgtsv(n, nrhs, dl, 0, d, 0, du, 0, b, 0, ldb, info);
/* 4888:     */   }
/* 4889:     */   
/* 4890:     */   public void sgtsv(int n, int nrhs, float[] dl, int _dl_offset, float[] d, int _d_offset, float[] du, int _du_offset, float[] b, int _b_offset, int ldb, intW info)
/* 4891:     */   {
/* 4892:4196 */     Sgtsv.sgtsv(n, nrhs, dl, _dl_offset, d, _d_offset, du, _du_offset, b, _b_offset, ldb, info);
/* 4893:     */   }
/* 4894:     */   
/* 4895:     */   public void sgtsvx(String fact, String trans, int n, int nrhs, float[] dl, float[] d, float[] du, float[] dlf, float[] df, float[] duf, float[] du2, int[] ipiv, float[] b, int ldb, float[] x, int ldx, floatW rcond, float[] ferr, float[] berr, float[] work, int[] iwork, intW info)
/* 4896:     */   {
/* 4897:4201 */     Sgtsvx.sgtsvx(fact, trans, n, nrhs, dl, 0, d, 0, du, 0, dlf, 0, df, 0, duf, 0, du2, 0, ipiv, 0, b, 0, ldb, x, 0, ldx, rcond, ferr, 0, berr, 0, work, 0, iwork, 0, info);
/* 4898:     */   }
/* 4899:     */   
/* 4900:     */   public void sgtsvx(String fact, String trans, int n, int nrhs, float[] dl, int _dl_offset, float[] d, int _d_offset, float[] du, int _du_offset, float[] dlf, int _dlf_offset, float[] df, int _df_offset, float[] duf, int _duf_offset, float[] du2, int _du2_offset, int[] ipiv, int _ipiv_offset, float[] b, int _b_offset, int ldb, float[] x, int _x_offset, int ldx, floatW rcond, float[] ferr, int _ferr_offset, float[] berr, int _berr_offset, float[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 4901:     */   {
/* 4902:4206 */     Sgtsvx.sgtsvx(fact, trans, n, nrhs, dl, _dl_offset, d, _d_offset, du, _du_offset, dlf, _dlf_offset, df, _df_offset, duf, _duf_offset, du2, _du2_offset, ipiv, _ipiv_offset, b, _b_offset, ldb, x, _x_offset, ldx, rcond, ferr, _ferr_offset, berr, _berr_offset, work, _work_offset, iwork, _iwork_offset, info);
/* 4903:     */   }
/* 4904:     */   
/* 4905:     */   public void sgttrf(int n, float[] dl, float[] d, float[] du, float[] du2, int[] ipiv, intW info)
/* 4906:     */   {
/* 4907:4211 */     Sgttrf.sgttrf(n, dl, 0, d, 0, du, 0, du2, 0, ipiv, 0, info);
/* 4908:     */   }
/* 4909:     */   
/* 4910:     */   public void sgttrf(int n, float[] dl, int _dl_offset, float[] d, int _d_offset, float[] du, int _du_offset, float[] du2, int _du2_offset, int[] ipiv, int _ipiv_offset, intW info)
/* 4911:     */   {
/* 4912:4216 */     Sgttrf.sgttrf(n, dl, _dl_offset, d, _d_offset, du, _du_offset, du2, _du2_offset, ipiv, _ipiv_offset, info);
/* 4913:     */   }
/* 4914:     */   
/* 4915:     */   public void sgttrs(String trans, int n, int nrhs, float[] dl, float[] d, float[] du, float[] du2, int[] ipiv, float[] b, int ldb, intW info)
/* 4916:     */   {
/* 4917:4221 */     Sgttrs.sgttrs(trans, n, nrhs, dl, 0, d, 0, du, 0, du2, 0, ipiv, 0, b, 0, ldb, info);
/* 4918:     */   }
/* 4919:     */   
/* 4920:     */   public void sgttrs(String trans, int n, int nrhs, float[] dl, int _dl_offset, float[] d, int _d_offset, float[] du, int _du_offset, float[] du2, int _du2_offset, int[] ipiv, int _ipiv_offset, float[] b, int _b_offset, int ldb, intW info)
/* 4921:     */   {
/* 4922:4226 */     Sgttrs.sgttrs(trans, n, nrhs, dl, _dl_offset, d, _d_offset, du, _du_offset, du2, _du2_offset, ipiv, _ipiv_offset, b, _b_offset, ldb, info);
/* 4923:     */   }
/* 4924:     */   
/* 4925:     */   public void sgtts2(int itrans, int n, int nrhs, float[] dl, float[] d, float[] du, float[] du2, int[] ipiv, float[] b, int ldb)
/* 4926:     */   {
/* 4927:4231 */     Sgtts2.sgtts2(itrans, n, nrhs, dl, 0, d, 0, du, 0, du2, 0, ipiv, 0, b, 0, ldb);
/* 4928:     */   }
/* 4929:     */   
/* 4930:     */   public void sgtts2(int itrans, int n, int nrhs, float[] dl, int _dl_offset, float[] d, int _d_offset, float[] du, int _du_offset, float[] du2, int _du2_offset, int[] ipiv, int _ipiv_offset, float[] b, int _b_offset, int ldb)
/* 4931:     */   {
/* 4932:4236 */     Sgtts2.sgtts2(itrans, n, nrhs, dl, _dl_offset, d, _d_offset, du, _du_offset, du2, _du2_offset, ipiv, _ipiv_offset, b, _b_offset, ldb);
/* 4933:     */   }
/* 4934:     */   
/* 4935:     */   public void shgeqz(String job, String compq, String compz, int n, int ilo, int ihi, float[] h, int ldh, float[] t, int ldt, float[] alphar, float[] alphai, float[] beta, float[] q, int ldq, float[] z, int ldz, float[] work, int lwork, intW info)
/* 4936:     */   {
/* 4937:4241 */     Shgeqz.shgeqz(job, compq, compz, n, ilo, ihi, h, 0, ldh, t, 0, ldt, alphar, 0, alphai, 0, beta, 0, q, 0, ldq, z, 0, ldz, work, 0, lwork, info);
/* 4938:     */   }
/* 4939:     */   
/* 4940:     */   public void shgeqz(String job, String compq, String compz, int n, int ilo, int ihi, float[] h, int _h_offset, int ldh, float[] t, int _t_offset, int ldt, float[] alphar, int _alphar_offset, float[] alphai, int _alphai_offset, float[] beta, int _beta_offset, float[] q, int _q_offset, int ldq, float[] z, int _z_offset, int ldz, float[] work, int _work_offset, int lwork, intW info)
/* 4941:     */   {
/* 4942:4246 */     Shgeqz.shgeqz(job, compq, compz, n, ilo, ihi, h, _h_offset, ldh, t, _t_offset, ldt, alphar, _alphar_offset, alphai, _alphai_offset, beta, _beta_offset, q, _q_offset, ldq, z, _z_offset, ldz, work, _work_offset, lwork, info);
/* 4943:     */   }
/* 4944:     */   
/* 4945:     */   public void shsein(String side, String eigsrc, String initv, boolean[] select, int n, float[] h, int ldh, float[] wr, float[] wi, float[] vl, int ldvl, float[] vr, int ldvr, int mm, intW m, float[] work, int[] ifaill, int[] ifailr, intW info)
/* 4946:     */   {
/* 4947:4251 */     Shsein.shsein(side, eigsrc, initv, select, 0, n, h, 0, ldh, wr, 0, wi, 0, vl, 0, ldvl, vr, 0, ldvr, mm, m, work, 0, ifaill, 0, ifailr, 0, info);
/* 4948:     */   }
/* 4949:     */   
/* 4950:     */   public void shsein(String side, String eigsrc, String initv, boolean[] select, int _select_offset, int n, float[] h, int _h_offset, int ldh, float[] wr, int _wr_offset, float[] wi, int _wi_offset, float[] vl, int _vl_offset, int ldvl, float[] vr, int _vr_offset, int ldvr, int mm, intW m, float[] work, int _work_offset, int[] ifaill, int _ifaill_offset, int[] ifailr, int _ifailr_offset, intW info)
/* 4951:     */   {
/* 4952:4256 */     Shsein.shsein(side, eigsrc, initv, select, _select_offset, n, h, _h_offset, ldh, wr, _wr_offset, wi, _wi_offset, vl, _vl_offset, ldvl, vr, _vr_offset, ldvr, mm, m, work, _work_offset, ifaill, _ifaill_offset, ifailr, _ifailr_offset, info);
/* 4953:     */   }
/* 4954:     */   
/* 4955:     */   public void shseqr(String job, String compz, int n, int ilo, int ihi, float[] h, int ldh, float[] wr, float[] wi, float[] z, int ldz, float[] work, int lwork, intW info)
/* 4956:     */   {
/* 4957:4261 */     Shseqr.shseqr(job, compz, n, ilo, ihi, h, 0, ldh, wr, 0, wi, 0, z, 0, ldz, work, 0, lwork, info);
/* 4958:     */   }
/* 4959:     */   
/* 4960:     */   public void shseqr(String job, String compz, int n, int ilo, int ihi, float[] h, int _h_offset, int ldh, float[] wr, int _wr_offset, float[] wi, int _wi_offset, float[] z, int _z_offset, int ldz, float[] work, int _work_offset, int lwork, intW info)
/* 4961:     */   {
/* 4962:4266 */     Shseqr.shseqr(job, compz, n, ilo, ihi, h, _h_offset, ldh, wr, _wr_offset, wi, _wi_offset, z, _z_offset, ldz, work, _work_offset, lwork, info);
/* 4963:     */   }
/* 4964:     */   
/* 4965:     */   public boolean sisnan(float sin)
/* 4966:     */   {
/* 4967:4271 */     return Sisnan.sisnan(sin);
/* 4968:     */   }
/* 4969:     */   
/* 4970:     */   public void slabad(floatW small, floatW large)
/* 4971:     */   {
/* 4972:4276 */     Slabad.slabad(small, large);
/* 4973:     */   }
/* 4974:     */   
/* 4975:     */   public void slabrd(int m, int n, int nb, float[] a, int lda, float[] d, float[] e, float[] tauq, float[] taup, float[] x, int ldx, float[] y, int ldy)
/* 4976:     */   {
/* 4977:4281 */     Slabrd.slabrd(m, n, nb, a, 0, lda, d, 0, e, 0, tauq, 0, taup, 0, x, 0, ldx, y, 0, ldy);
/* 4978:     */   }
/* 4979:     */   
/* 4980:     */   public void slabrd(int m, int n, int nb, float[] a, int _a_offset, int lda, float[] d, int _d_offset, float[] e, int _e_offset, float[] tauq, int _tauq_offset, float[] taup, int _taup_offset, float[] x, int _x_offset, int ldx, float[] y, int _y_offset, int ldy)
/* 4981:     */   {
/* 4982:4286 */     Slabrd.slabrd(m, n, nb, a, _a_offset, lda, d, _d_offset, e, _e_offset, tauq, _tauq_offset, taup, _taup_offset, x, _x_offset, ldx, y, _y_offset, ldy);
/* 4983:     */   }
/* 4984:     */   
/* 4985:     */   public void slacn2(int n, float[] v, float[] x, int[] isgn, floatW est, intW kase, int[] isave)
/* 4986:     */   {
/* 4987:4291 */     Slacn2.slacn2(n, v, 0, x, 0, isgn, 0, est, kase, isave, 0);
/* 4988:     */   }
/* 4989:     */   
/* 4990:     */   public void slacn2(int n, float[] v, int _v_offset, float[] x, int _x_offset, int[] isgn, int _isgn_offset, floatW est, intW kase, int[] isave, int _isave_offset)
/* 4991:     */   {
/* 4992:4296 */     Slacn2.slacn2(n, v, _v_offset, x, _x_offset, isgn, _isgn_offset, est, kase, isave, _isave_offset);
/* 4993:     */   }
/* 4994:     */   
/* 4995:     */   public void slacon(int n, float[] v, float[] x, int[] isgn, floatW est, intW kase)
/* 4996:     */   {
/* 4997:4301 */     Slacon.slacon(n, v, 0, x, 0, isgn, 0, est, kase);
/* 4998:     */   }
/* 4999:     */   
/* 5000:     */   public void slacon(int n, float[] v, int _v_offset, float[] x, int _x_offset, int[] isgn, int _isgn_offset, floatW est, intW kase)
/* 5001:     */   {
/* 5002:4306 */     Slacon.slacon(n, v, _v_offset, x, _x_offset, isgn, _isgn_offset, est, kase);
/* 5003:     */   }
/* 5004:     */   
/* 5005:     */   public void slacpy(String uplo, int m, int n, float[] a, int lda, float[] b, int ldb)
/* 5006:     */   {
/* 5007:4311 */     Slacpy.slacpy(uplo, m, n, a, 0, lda, b, 0, ldb);
/* 5008:     */   }
/* 5009:     */   
/* 5010:     */   public void slacpy(String uplo, int m, int n, float[] a, int _a_offset, int lda, float[] b, int _b_offset, int ldb)
/* 5011:     */   {
/* 5012:4316 */     Slacpy.slacpy(uplo, m, n, a, _a_offset, lda, b, _b_offset, ldb);
/* 5013:     */   }
/* 5014:     */   
/* 5015:     */   public void sladiv(float a, float b, float c, float d, floatW p, floatW q)
/* 5016:     */   {
/* 5017:4321 */     Sladiv.sladiv(a, b, c, d, p, q);
/* 5018:     */   }
/* 5019:     */   
/* 5020:     */   public void slae2(float a, float b, float c, floatW rt1, floatW rt2)
/* 5021:     */   {
/* 5022:4326 */     Slae2.slae2(a, b, c, rt1, rt2);
/* 5023:     */   }
/* 5024:     */   
/* 5025:     */   public void slaebz(int ijob, int nitmax, int n, int mmax, int minp, int nbmin, float abstol, float reltol, float pivmin, float[] d, float[] e, float[] e2, int[] nval, float[] ab, float[] c, intW mout, int[] nab, float[] work, int[] iwork, intW info)
/* 5026:     */   {
/* 5027:4331 */     Slaebz.slaebz(ijob, nitmax, n, mmax, minp, nbmin, abstol, reltol, pivmin, d, 0, e, 0, e2, 0, nval, 0, ab, 0, c, 0, mout, nab, 0, work, 0, iwork, 0, info);
/* 5028:     */   }
/* 5029:     */   
/* 5030:     */   public void slaebz(int ijob, int nitmax, int n, int mmax, int minp, int nbmin, float abstol, float reltol, float pivmin, float[] d, int _d_offset, float[] e, int _e_offset, float[] e2, int _e2_offset, int[] nval, int _nval_offset, float[] ab, int _ab_offset, float[] c, int _c_offset, intW mout, int[] nab, int _nab_offset, float[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 5031:     */   {
/* 5032:4336 */     Slaebz.slaebz(ijob, nitmax, n, mmax, minp, nbmin, abstol, reltol, pivmin, d, _d_offset, e, _e_offset, e2, _e2_offset, nval, _nval_offset, ab, _ab_offset, c, _c_offset, mout, nab, _nab_offset, work, _work_offset, iwork, _iwork_offset, info);
/* 5033:     */   }
/* 5034:     */   
/* 5035:     */   public void slaed0(int icompq, int qsiz, int n, float[] d, float[] e, float[] q, int ldq, float[] qstore, int ldqs, float[] work, int[] iwork, intW info)
/* 5036:     */   {
/* 5037:4341 */     Slaed0.slaed0(icompq, qsiz, n, d, 0, e, 0, q, 0, ldq, qstore, 0, ldqs, work, 0, iwork, 0, info);
/* 5038:     */   }
/* 5039:     */   
/* 5040:     */   public void slaed0(int icompq, int qsiz, int n, float[] d, int _d_offset, float[] e, int _e_offset, float[] q, int _q_offset, int ldq, float[] qstore, int _qstore_offset, int ldqs, float[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 5041:     */   {
/* 5042:4346 */     Slaed0.slaed0(icompq, qsiz, n, d, _d_offset, e, _e_offset, q, _q_offset, ldq, qstore, _qstore_offset, ldqs, work, _work_offset, iwork, _iwork_offset, info);
/* 5043:     */   }
/* 5044:     */   
/* 5045:     */   public void slaed1(int n, float[] d, float[] q, int ldq, int[] indxq, floatW rho, int cutpnt, float[] work, int[] iwork, intW info)
/* 5046:     */   {
/* 5047:4351 */     Slaed1.slaed1(n, d, 0, q, 0, ldq, indxq, 0, rho, cutpnt, work, 0, iwork, 0, info);
/* 5048:     */   }
/* 5049:     */   
/* 5050:     */   public void slaed1(int n, float[] d, int _d_offset, float[] q, int _q_offset, int ldq, int[] indxq, int _indxq_offset, floatW rho, int cutpnt, float[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 5051:     */   {
/* 5052:4356 */     Slaed1.slaed1(n, d, _d_offset, q, _q_offset, ldq, indxq, _indxq_offset, rho, cutpnt, work, _work_offset, iwork, _iwork_offset, info);
/* 5053:     */   }
/* 5054:     */   
/* 5055:     */   public void slaed2(intW k, int n, int n1, float[] d, float[] q, int ldq, int[] indxq, floatW rho, float[] z, float[] dlamda, float[] w, float[] q2, int[] indx, int[] indxc, int[] indxp, int[] coltyp, intW info)
/* 5056:     */   {
/* 5057:4361 */     Slaed2.slaed2(k, n, n1, d, 0, q, 0, ldq, indxq, 0, rho, z, 0, dlamda, 0, w, 0, q2, 0, indx, 0, indxc, 0, indxp, 0, coltyp, 0, info);
/* 5058:     */   }
/* 5059:     */   
/* 5060:     */   public void slaed2(intW k, int n, int n1, float[] d, int _d_offset, float[] q, int _q_offset, int ldq, int[] indxq, int _indxq_offset, floatW rho, float[] z, int _z_offset, float[] dlamda, int _dlamda_offset, float[] w, int _w_offset, float[] q2, int _q2_offset, int[] indx, int _indx_offset, int[] indxc, int _indxc_offset, int[] indxp, int _indxp_offset, int[] coltyp, int _coltyp_offset, intW info)
/* 5061:     */   {
/* 5062:4366 */     Slaed2.slaed2(k, n, n1, d, _d_offset, q, _q_offset, ldq, indxq, _indxq_offset, rho, z, _z_offset, dlamda, _dlamda_offset, w, _w_offset, q2, _q2_offset, indx, _indx_offset, indxc, _indxc_offset, indxp, _indxp_offset, coltyp, _coltyp_offset, info);
/* 5063:     */   }
/* 5064:     */   
/* 5065:     */   public void slaed3(int k, int n, int n1, float[] d, float[] q, int ldq, float rho, float[] dlamda, float[] q2, int[] indx, int[] ctot, float[] w, float[] s, intW info)
/* 5066:     */   {
/* 5067:4371 */     Slaed3.slaed3(k, n, n1, d, 0, q, 0, ldq, rho, dlamda, 0, q2, 0, indx, 0, ctot, 0, w, 0, s, 0, info);
/* 5068:     */   }
/* 5069:     */   
/* 5070:     */   public void slaed3(int k, int n, int n1, float[] d, int _d_offset, float[] q, int _q_offset, int ldq, float rho, float[] dlamda, int _dlamda_offset, float[] q2, int _q2_offset, int[] indx, int _indx_offset, int[] ctot, int _ctot_offset, float[] w, int _w_offset, float[] s, int _s_offset, intW info)
/* 5071:     */   {
/* 5072:4376 */     Slaed3.slaed3(k, n, n1, d, _d_offset, q, _q_offset, ldq, rho, dlamda, _dlamda_offset, q2, _q2_offset, indx, _indx_offset, ctot, _ctot_offset, w, _w_offset, s, _s_offset, info);
/* 5073:     */   }
/* 5074:     */   
/* 5075:     */   public void slaed4(int n, int i, float[] d, float[] z, float[] delta, float rho, floatW dlam, intW info)
/* 5076:     */   {
/* 5077:4381 */     Slaed4.slaed4(n, i, d, 0, z, 0, delta, 0, rho, dlam, info);
/* 5078:     */   }
/* 5079:     */   
/* 5080:     */   public void slaed4(int n, int i, float[] d, int _d_offset, float[] z, int _z_offset, float[] delta, int _delta_offset, float rho, floatW dlam, intW info)
/* 5081:     */   {
/* 5082:4386 */     Slaed4.slaed4(n, i, d, _d_offset, z, _z_offset, delta, _delta_offset, rho, dlam, info);
/* 5083:     */   }
/* 5084:     */   
/* 5085:     */   public void slaed5(int i, float[] d, float[] z, float[] delta, float rho, floatW dlam)
/* 5086:     */   {
/* 5087:4391 */     Slaed5.slaed5(i, d, 0, z, 0, delta, 0, rho, dlam);
/* 5088:     */   }
/* 5089:     */   
/* 5090:     */   public void slaed5(int i, float[] d, int _d_offset, float[] z, int _z_offset, float[] delta, int _delta_offset, float rho, floatW dlam)
/* 5091:     */   {
/* 5092:4396 */     Slaed5.slaed5(i, d, _d_offset, z, _z_offset, delta, _delta_offset, rho, dlam);
/* 5093:     */   }
/* 5094:     */   
/* 5095:     */   public void slaed6(int kniter, boolean orgati, float rho, float[] d, float[] z, float finit, floatW tau, intW info)
/* 5096:     */   {
/* 5097:4401 */     Slaed6.slaed6(kniter, orgati, rho, d, 0, z, 0, finit, tau, info);
/* 5098:     */   }
/* 5099:     */   
/* 5100:     */   public void slaed6(int kniter, boolean orgati, float rho, float[] d, int _d_offset, float[] z, int _z_offset, float finit, floatW tau, intW info)
/* 5101:     */   {
/* 5102:4406 */     Slaed6.slaed6(kniter, orgati, rho, d, _d_offset, z, _z_offset, finit, tau, info);
/* 5103:     */   }
/* 5104:     */   
/* 5105:     */   public void slaed7(int icompq, int n, int qsiz, int tlvls, int curlvl, int curpbm, float[] d, float[] q, int ldq, int[] indxq, floatW rho, int cutpnt, float[] qstore, int[] qptr, int[] prmptr, int[] perm, int[] givptr, int[] givcol, float[] givnum, float[] work, int[] iwork, intW info)
/* 5106:     */   {
/* 5107:4411 */     Slaed7.slaed7(icompq, n, qsiz, tlvls, curlvl, curpbm, d, 0, q, 0, ldq, indxq, 0, rho, cutpnt, qstore, 0, qptr, 0, prmptr, 0, perm, 0, givptr, 0, givcol, 0, givnum, 0, work, 0, iwork, 0, info);
/* 5108:     */   }
/* 5109:     */   
/* 5110:     */   public void slaed7(int icompq, int n, int qsiz, int tlvls, int curlvl, int curpbm, float[] d, int _d_offset, float[] q, int _q_offset, int ldq, int[] indxq, int _indxq_offset, floatW rho, int cutpnt, float[] qstore, int _qstore_offset, int[] qptr, int _qptr_offset, int[] prmptr, int _prmptr_offset, int[] perm, int _perm_offset, int[] givptr, int _givptr_offset, int[] givcol, int _givcol_offset, float[] givnum, int _givnum_offset, float[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 5111:     */   {
/* 5112:4416 */     Slaed7.slaed7(icompq, n, qsiz, tlvls, curlvl, curpbm, d, _d_offset, q, _q_offset, ldq, indxq, _indxq_offset, rho, cutpnt, qstore, _qstore_offset, qptr, _qptr_offset, prmptr, _prmptr_offset, perm, _perm_offset, givptr, _givptr_offset, givcol, _givcol_offset, givnum, _givnum_offset, work, _work_offset, iwork, _iwork_offset, info);
/* 5113:     */   }
/* 5114:     */   
/* 5115:     */   public void slaed8(int icompq, intW k, int n, int qsiz, float[] d, float[] q, int ldq, int[] indxq, floatW rho, int cutpnt, float[] z, float[] dlamda, float[] q2, int ldq2, float[] w, int[] perm, intW givptr, int[] givcol, float[] givnum, int[] indxp, int[] indx, intW info)
/* 5116:     */   {
/* 5117:4421 */     Slaed8.slaed8(icompq, k, n, qsiz, d, 0, q, 0, ldq, indxq, 0, rho, cutpnt, z, 0, dlamda, 0, q2, 0, ldq2, w, 0, perm, 0, givptr, givcol, 0, givnum, 0, indxp, 0, indx, 0, info);
/* 5118:     */   }
/* 5119:     */   
/* 5120:     */   public void slaed8(int icompq, intW k, int n, int qsiz, float[] d, int _d_offset, float[] q, int _q_offset, int ldq, int[] indxq, int _indxq_offset, floatW rho, int cutpnt, float[] z, int _z_offset, float[] dlamda, int _dlamda_offset, float[] q2, int _q2_offset, int ldq2, float[] w, int _w_offset, int[] perm, int _perm_offset, intW givptr, int[] givcol, int _givcol_offset, float[] givnum, int _givnum_offset, int[] indxp, int _indxp_offset, int[] indx, int _indx_offset, intW info)
/* 5121:     */   {
/* 5122:4426 */     Slaed8.slaed8(icompq, k, n, qsiz, d, _d_offset, q, _q_offset, ldq, indxq, _indxq_offset, rho, cutpnt, z, _z_offset, dlamda, _dlamda_offset, q2, _q2_offset, ldq2, w, _w_offset, perm, _perm_offset, givptr, givcol, _givcol_offset, givnum, _givnum_offset, indxp, _indxp_offset, indx, _indx_offset, info);
/* 5123:     */   }
/* 5124:     */   
/* 5125:     */   public void slaed9(int k, int kstart, int kstop, int n, float[] d, float[] q, int ldq, float rho, float[] dlamda, float[] w, float[] s, int lds, intW info)
/* 5126:     */   {
/* 5127:4431 */     Slaed9.slaed9(k, kstart, kstop, n, d, 0, q, 0, ldq, rho, dlamda, 0, w, 0, s, 0, lds, info);
/* 5128:     */   }
/* 5129:     */   
/* 5130:     */   public void slaed9(int k, int kstart, int kstop, int n, float[] d, int _d_offset, float[] q, int _q_offset, int ldq, float rho, float[] dlamda, int _dlamda_offset, float[] w, int _w_offset, float[] s, int _s_offset, int lds, intW info)
/* 5131:     */   {
/* 5132:4436 */     Slaed9.slaed9(k, kstart, kstop, n, d, _d_offset, q, _q_offset, ldq, rho, dlamda, _dlamda_offset, w, _w_offset, s, _s_offset, lds, info);
/* 5133:     */   }
/* 5134:     */   
/* 5135:     */   public void slaeda(int n, int tlvls, int curlvl, int curpbm, int[] prmptr, int[] perm, int[] givptr, int[] givcol, float[] givnum, float[] q, int[] qptr, float[] z, float[] ztemp, intW info)
/* 5136:     */   {
/* 5137:4441 */     Slaeda.slaeda(n, tlvls, curlvl, curpbm, prmptr, 0, perm, 0, givptr, 0, givcol, 0, givnum, 0, q, 0, qptr, 0, z, 0, ztemp, 0, info);
/* 5138:     */   }
/* 5139:     */   
/* 5140:     */   public void slaeda(int n, int tlvls, int curlvl, int curpbm, int[] prmptr, int _prmptr_offset, int[] perm, int _perm_offset, int[] givptr, int _givptr_offset, int[] givcol, int _givcol_offset, float[] givnum, int _givnum_offset, float[] q, int _q_offset, int[] qptr, int _qptr_offset, float[] z, int _z_offset, float[] ztemp, int _ztemp_offset, intW info)
/* 5141:     */   {
/* 5142:4446 */     Slaeda.slaeda(n, tlvls, curlvl, curpbm, prmptr, _prmptr_offset, perm, _perm_offset, givptr, _givptr_offset, givcol, _givcol_offset, givnum, _givnum_offset, q, _q_offset, qptr, _qptr_offset, z, _z_offset, ztemp, _ztemp_offset, info);
/* 5143:     */   }
/* 5144:     */   
/* 5145:     */   public void slaein(boolean rightv, boolean noinit, int n, float[] h, int ldh, float wr, float wi, float[] vr, float[] vi, float[] b, int ldb, float[] work, float eps3, float smlnum, float bignum, intW info)
/* 5146:     */   {
/* 5147:4451 */     Slaein.slaein(rightv, noinit, n, h, 0, ldh, wr, wi, vr, 0, vi, 0, b, 0, ldb, work, 0, eps3, smlnum, bignum, info);
/* 5148:     */   }
/* 5149:     */   
/* 5150:     */   public void slaein(boolean rightv, boolean noinit, int n, float[] h, int _h_offset, int ldh, float wr, float wi, float[] vr, int _vr_offset, float[] vi, int _vi_offset, float[] b, int _b_offset, int ldb, float[] work, int _work_offset, float eps3, float smlnum, float bignum, intW info)
/* 5151:     */   {
/* 5152:4456 */     Slaein.slaein(rightv, noinit, n, h, _h_offset, ldh, wr, wi, vr, _vr_offset, vi, _vi_offset, b, _b_offset, ldb, work, _work_offset, eps3, smlnum, bignum, info);
/* 5153:     */   }
/* 5154:     */   
/* 5155:     */   public void slaev2(float a, float b, float c, floatW rt1, floatW rt2, floatW cs1, floatW sn1)
/* 5156:     */   {
/* 5157:4461 */     Slaev2.slaev2(a, b, c, rt1, rt2, cs1, sn1);
/* 5158:     */   }
/* 5159:     */   
/* 5160:     */   public void slaexc(boolean wantq, int n, float[] t, int ldt, float[] q, int ldq, int j1, int n1, int n2, float[] work, intW info)
/* 5161:     */   {
/* 5162:4466 */     Slaexc.slaexc(wantq, n, t, 0, ldt, q, 0, ldq, j1, n1, n2, work, 0, info);
/* 5163:     */   }
/* 5164:     */   
/* 5165:     */   public void slaexc(boolean wantq, int n, float[] t, int _t_offset, int ldt, float[] q, int _q_offset, int ldq, int j1, int n1, int n2, float[] work, int _work_offset, intW info)
/* 5166:     */   {
/* 5167:4471 */     Slaexc.slaexc(wantq, n, t, _t_offset, ldt, q, _q_offset, ldq, j1, n1, n2, work, _work_offset, info);
/* 5168:     */   }
/* 5169:     */   
/* 5170:     */   public void slag2(float[] a, int lda, float[] b, int ldb, float safmin, floatW scale1, floatW scale2, floatW wr1, floatW wr2, floatW wi)
/* 5171:     */   {
/* 5172:4476 */     Slag2.slag2(a, 0, lda, b, 0, ldb, safmin, scale1, scale2, wr1, wr2, wi);
/* 5173:     */   }
/* 5174:     */   
/* 5175:     */   public void slag2(float[] a, int _a_offset, int lda, float[] b, int _b_offset, int ldb, float safmin, floatW scale1, floatW scale2, floatW wr1, floatW wr2, floatW wi)
/* 5176:     */   {
/* 5177:4481 */     Slag2.slag2(a, _a_offset, lda, b, _b_offset, ldb, safmin, scale1, scale2, wr1, wr2, wi);
/* 5178:     */   }
/* 5179:     */   
/* 5180:     */   public void slag2d(int m, int n, float[] sa, int ldsa, double[] a, int lda, intW info)
/* 5181:     */   {
/* 5182:4486 */     Slag2d.slag2d(m, n, sa, 0, ldsa, a, 0, lda, info);
/* 5183:     */   }
/* 5184:     */   
/* 5185:     */   public void slag2d(int m, int n, float[] sa, int _sa_offset, int ldsa, double[] a, int _a_offset, int lda, intW info)
/* 5186:     */   {
/* 5187:4491 */     Slag2d.slag2d(m, n, sa, _sa_offset, ldsa, a, _a_offset, lda, info);
/* 5188:     */   }
/* 5189:     */   
/* 5190:     */   public void slags2(boolean upper, float a1, float a2, float a3, float b1, float b2, float b3, floatW csu, floatW snu, floatW csv, floatW snv, floatW csq, floatW snq)
/* 5191:     */   {
/* 5192:4496 */     Slags2.slags2(upper, a1, a2, a3, b1, b2, b3, csu, snu, csv, snv, csq, snq);
/* 5193:     */   }
/* 5194:     */   
/* 5195:     */   public void slagtf(int n, float[] a, float lambda, float[] b, float[] c, float tol, float[] d, int[] in, intW info)
/* 5196:     */   {
/* 5197:4501 */     Slagtf.slagtf(n, a, 0, lambda, b, 0, c, 0, tol, d, 0, in, 0, info);
/* 5198:     */   }
/* 5199:     */   
/* 5200:     */   public void slagtf(int n, float[] a, int _a_offset, float lambda, float[] b, int _b_offset, float[] c, int _c_offset, float tol, float[] d, int _d_offset, int[] in, int _in_offset, intW info)
/* 5201:     */   {
/* 5202:4506 */     Slagtf.slagtf(n, a, _a_offset, lambda, b, _b_offset, c, _c_offset, tol, d, _d_offset, in, _in_offset, info);
/* 5203:     */   }
/* 5204:     */   
/* 5205:     */   public void slagtm(String trans, int n, int nrhs, float alpha, float[] dl, float[] d, float[] du, float[] x, int ldx, float beta, float[] b, int ldb)
/* 5206:     */   {
/* 5207:4511 */     Slagtm.slagtm(trans, n, nrhs, alpha, dl, 0, d, 0, du, 0, x, 0, ldx, beta, b, 0, ldb);
/* 5208:     */   }
/* 5209:     */   
/* 5210:     */   public void slagtm(String trans, int n, int nrhs, float alpha, float[] dl, int _dl_offset, float[] d, int _d_offset, float[] du, int _du_offset, float[] x, int _x_offset, int ldx, float beta, float[] b, int _b_offset, int ldb)
/* 5211:     */   {
/* 5212:4516 */     Slagtm.slagtm(trans, n, nrhs, alpha, dl, _dl_offset, d, _d_offset, du, _du_offset, x, _x_offset, ldx, beta, b, _b_offset, ldb);
/* 5213:     */   }
/* 5214:     */   
/* 5215:     */   public void slagts(int job, int n, float[] a, float[] b, float[] c, float[] d, int[] in, float[] y, floatW tol, intW info)
/* 5216:     */   {
/* 5217:4521 */     Slagts.slagts(job, n, a, 0, b, 0, c, 0, d, 0, in, 0, y, 0, tol, info);
/* 5218:     */   }
/* 5219:     */   
/* 5220:     */   public void slagts(int job, int n, float[] a, int _a_offset, float[] b, int _b_offset, float[] c, int _c_offset, float[] d, int _d_offset, int[] in, int _in_offset, float[] y, int _y_offset, floatW tol, intW info)
/* 5221:     */   {
/* 5222:4526 */     Slagts.slagts(job, n, a, _a_offset, b, _b_offset, c, _c_offset, d, _d_offset, in, _in_offset, y, _y_offset, tol, info);
/* 5223:     */   }
/* 5224:     */   
/* 5225:     */   public void slagv2(float[] a, int lda, float[] b, int ldb, float[] alphar, float[] alphai, float[] beta, floatW csl, floatW snl, floatW csr, floatW snr)
/* 5226:     */   {
/* 5227:4531 */     Slagv2.slagv2(a, 0, lda, b, 0, ldb, alphar, 0, alphai, 0, beta, 0, csl, snl, csr, snr);
/* 5228:     */   }
/* 5229:     */   
/* 5230:     */   public void slagv2(float[] a, int _a_offset, int lda, float[] b, int _b_offset, int ldb, float[] alphar, int _alphar_offset, float[] alphai, int _alphai_offset, float[] beta, int _beta_offset, floatW csl, floatW snl, floatW csr, floatW snr)
/* 5231:     */   {
/* 5232:4536 */     Slagv2.slagv2(a, _a_offset, lda, b, _b_offset, ldb, alphar, _alphar_offset, alphai, _alphai_offset, beta, _beta_offset, csl, snl, csr, snr);
/* 5233:     */   }
/* 5234:     */   
/* 5235:     */   public void slahqr(boolean wantt, boolean wantz, int n, int ilo, int ihi, float[] h, int ldh, float[] wr, float[] wi, int iloz, int ihiz, float[] z, int ldz, intW info)
/* 5236:     */   {
/* 5237:4541 */     Slahqr.slahqr(wantt, wantz, n, ilo, ihi, h, 0, ldh, wr, 0, wi, 0, iloz, ihiz, z, 0, ldz, info);
/* 5238:     */   }
/* 5239:     */   
/* 5240:     */   public void slahqr(boolean wantt, boolean wantz, int n, int ilo, int ihi, float[] h, int _h_offset, int ldh, float[] wr, int _wr_offset, float[] wi, int _wi_offset, int iloz, int ihiz, float[] z, int _z_offset, int ldz, intW info)
/* 5241:     */   {
/* 5242:4546 */     Slahqr.slahqr(wantt, wantz, n, ilo, ihi, h, _h_offset, ldh, wr, _wr_offset, wi, _wi_offset, iloz, ihiz, z, _z_offset, ldz, info);
/* 5243:     */   }
/* 5244:     */   
/* 5245:     */   public void slahr2(int n, int k, int nb, float[] a, int lda, float[] tau, float[] t, int ldt, float[] y, int ldy)
/* 5246:     */   {
/* 5247:4551 */     Slahr2.slahr2(n, k, nb, a, 0, lda, tau, 0, t, 0, ldt, y, 0, ldy);
/* 5248:     */   }
/* 5249:     */   
/* 5250:     */   public void slahr2(int n, int k, int nb, float[] a, int _a_offset, int lda, float[] tau, int _tau_offset, float[] t, int _t_offset, int ldt, float[] y, int _y_offset, int ldy)
/* 5251:     */   {
/* 5252:4556 */     Slahr2.slahr2(n, k, nb, a, _a_offset, lda, tau, _tau_offset, t, _t_offset, ldt, y, _y_offset, ldy);
/* 5253:     */   }
/* 5254:     */   
/* 5255:     */   public void slahrd(int n, int k, int nb, float[] a, int lda, float[] tau, float[] t, int ldt, float[] y, int ldy)
/* 5256:     */   {
/* 5257:4561 */     Slahrd.slahrd(n, k, nb, a, 0, lda, tau, 0, t, 0, ldt, y, 0, ldy);
/* 5258:     */   }
/* 5259:     */   
/* 5260:     */   public void slahrd(int n, int k, int nb, float[] a, int _a_offset, int lda, float[] tau, int _tau_offset, float[] t, int _t_offset, int ldt, float[] y, int _y_offset, int ldy)
/* 5261:     */   {
/* 5262:4566 */     Slahrd.slahrd(n, k, nb, a, _a_offset, lda, tau, _tau_offset, t, _t_offset, ldt, y, _y_offset, ldy);
/* 5263:     */   }
/* 5264:     */   
/* 5265:     */   public void slaic1(int job, int j, float[] x, float sest, float[] w, float gamma, floatW sestpr, floatW s, floatW c)
/* 5266:     */   {
/* 5267:4571 */     Slaic1.slaic1(job, j, x, 0, sest, w, 0, gamma, sestpr, s, c);
/* 5268:     */   }
/* 5269:     */   
/* 5270:     */   public void slaic1(int job, int j, float[] x, int _x_offset, float sest, float[] w, int _w_offset, float gamma, floatW sestpr, floatW s, floatW c)
/* 5271:     */   {
/* 5272:4576 */     Slaic1.slaic1(job, j, x, _x_offset, sest, w, _w_offset, gamma, sestpr, s, c);
/* 5273:     */   }
/* 5274:     */   
/* 5275:     */   public boolean slaisnan(float sin1, float sin2)
/* 5276:     */   {
/* 5277:4581 */     return Slaisnan.slaisnan(sin1, sin2);
/* 5278:     */   }
/* 5279:     */   
/* 5280:     */   public void slaln2(boolean ltrans, int na, int nw, float smin, float ca, float[] a, int lda, float d1, float d2, float[] b, int ldb, float wr, float wi, float[] x, int ldx, floatW scale, floatW xnorm, intW info)
/* 5281:     */   {
/* 5282:4586 */     Slaln2.slaln2(ltrans, na, nw, smin, ca, a, 0, lda, d1, d2, b, 0, ldb, wr, wi, x, 0, ldx, scale, xnorm, info);
/* 5283:     */   }
/* 5284:     */   
/* 5285:     */   public void slaln2(boolean ltrans, int na, int nw, float smin, float ca, float[] a, int _a_offset, int lda, float d1, float d2, float[] b, int _b_offset, int ldb, float wr, float wi, float[] x, int _x_offset, int ldx, floatW scale, floatW xnorm, intW info)
/* 5286:     */   {
/* 5287:4591 */     Slaln2.slaln2(ltrans, na, nw, smin, ca, a, _a_offset, lda, d1, d2, b, _b_offset, ldb, wr, wi, x, _x_offset, ldx, scale, xnorm, info);
/* 5288:     */   }
/* 5289:     */   
/* 5290:     */   public void slals0(int icompq, int nl, int nr, int sqre, int nrhs, float[] b, int ldb, float[] bx, int ldbx, int[] perm, int givptr, int[] givcol, int ldgcol, float[] givnum, int ldgnum, float[] poles, float[] difl, float[] difr, float[] z, int k, float c, float s, float[] work, intW info)
/* 5291:     */   {
/* 5292:4596 */     Slals0.slals0(icompq, nl, nr, sqre, nrhs, b, 0, ldb, bx, 0, ldbx, perm, 0, givptr, givcol, 0, ldgcol, givnum, 0, ldgnum, poles, 0, difl, 0, difr, 0, z, 0, k, c, s, work, 0, info);
/* 5293:     */   }
/* 5294:     */   
/* 5295:     */   public void slals0(int icompq, int nl, int nr, int sqre, int nrhs, float[] b, int _b_offset, int ldb, float[] bx, int _bx_offset, int ldbx, int[] perm, int _perm_offset, int givptr, int[] givcol, int _givcol_offset, int ldgcol, float[] givnum, int _givnum_offset, int ldgnum, float[] poles, int _poles_offset, float[] difl, int _difl_offset, float[] difr, int _difr_offset, float[] z, int _z_offset, int k, float c, float s, float[] work, int _work_offset, intW info)
/* 5296:     */   {
/* 5297:4601 */     Slals0.slals0(icompq, nl, nr, sqre, nrhs, b, _b_offset, ldb, bx, _bx_offset, ldbx, perm, _perm_offset, givptr, givcol, _givcol_offset, ldgcol, givnum, _givnum_offset, ldgnum, poles, _poles_offset, difl, _difl_offset, difr, _difr_offset, z, _z_offset, k, c, s, work, _work_offset, info);
/* 5298:     */   }
/* 5299:     */   
/* 5300:     */   public void slalsa(int icompq, int smlsiz, int n, int nrhs, float[] b, int ldb, float[] bx, int ldbx, float[] u, int ldu, float[] vt, int[] k, float[] difl, float[] difr, float[] z, float[] poles, int[] givptr, int[] givcol, int ldgcol, int[] perm, float[] givnum, float[] c, float[] s, float[] work, int[] iwork, intW info)
/* 5301:     */   {
/* 5302:4606 */     Slalsa.slalsa(icompq, smlsiz, n, nrhs, b, 0, ldb, bx, 0, ldbx, u, 0, ldu, vt, 0, k, 0, difl, 0, difr, 0, z, 0, poles, 0, givptr, 0, givcol, 0, ldgcol, perm, 0, givnum, 0, c, 0, s, 0, work, 0, iwork, 0, info);
/* 5303:     */   }
/* 5304:     */   
/* 5305:     */   public void slalsa(int icompq, int smlsiz, int n, int nrhs, float[] b, int _b_offset, int ldb, float[] bx, int _bx_offset, int ldbx, float[] u, int _u_offset, int ldu, float[] vt, int _vt_offset, int[] k, int _k_offset, float[] difl, int _difl_offset, float[] difr, int _difr_offset, float[] z, int _z_offset, float[] poles, int _poles_offset, int[] givptr, int _givptr_offset, int[] givcol, int _givcol_offset, int ldgcol, int[] perm, int _perm_offset, float[] givnum, int _givnum_offset, float[] c, int _c_offset, float[] s, int _s_offset, float[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 5306:     */   {
/* 5307:4611 */     Slalsa.slalsa(icompq, smlsiz, n, nrhs, b, _b_offset, ldb, bx, _bx_offset, ldbx, u, _u_offset, ldu, vt, _vt_offset, k, _k_offset, difl, _difl_offset, difr, _difr_offset, z, _z_offset, poles, _poles_offset, givptr, _givptr_offset, givcol, _givcol_offset, ldgcol, perm, _perm_offset, givnum, _givnum_offset, c, _c_offset, s, _s_offset, work, _work_offset, iwork, _iwork_offset, info);
/* 5308:     */   }
/* 5309:     */   
/* 5310:     */   public void slalsd(String uplo, int smlsiz, int n, int nrhs, float[] d, float[] e, float[] b, int ldb, float rcond, intW rank, float[] work, int[] iwork, intW info)
/* 5311:     */   {
/* 5312:4616 */     Slalsd.slalsd(uplo, smlsiz, n, nrhs, d, 0, e, 0, b, 0, ldb, rcond, rank, work, 0, iwork, 0, info);
/* 5313:     */   }
/* 5314:     */   
/* 5315:     */   public void slalsd(String uplo, int smlsiz, int n, int nrhs, float[] d, int _d_offset, float[] e, int _e_offset, float[] b, int _b_offset, int ldb, float rcond, intW rank, float[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 5316:     */   {
/* 5317:4621 */     Slalsd.slalsd(uplo, smlsiz, n, nrhs, d, _d_offset, e, _e_offset, b, _b_offset, ldb, rcond, rank, work, _work_offset, iwork, _iwork_offset, info);
/* 5318:     */   }
/* 5319:     */   
/* 5320:     */   public void slamrg(int n1, int n2, float[] a, int strd1, int strd2, int[] index)
/* 5321:     */   {
/* 5322:4626 */     Slamrg.slamrg(n1, n2, a, 0, strd1, strd2, index, 0);
/* 5323:     */   }
/* 5324:     */   
/* 5325:     */   public void slamrg(int n1, int n2, float[] a, int _a_offset, int strd1, int strd2, int[] index, int _index_offset)
/* 5326:     */   {
/* 5327:4631 */     Slamrg.slamrg(n1, n2, a, _a_offset, strd1, strd2, index, _index_offset);
/* 5328:     */   }
/* 5329:     */   
/* 5330:     */   public int slaneg(int n, float[] d, float[] lld, float sigma, float pivmin, int r)
/* 5331:     */   {
/* 5332:4636 */     return Slaneg.slaneg(n, d, 0, lld, 0, sigma, pivmin, r);
/* 5333:     */   }
/* 5334:     */   
/* 5335:     */   public int slaneg(int n, float[] d, int _d_offset, float[] lld, int _lld_offset, float sigma, float pivmin, int r)
/* 5336:     */   {
/* 5337:4641 */     return Slaneg.slaneg(n, d, _d_offset, lld, _lld_offset, sigma, pivmin, r);
/* 5338:     */   }
/* 5339:     */   
/* 5340:     */   public float slangb(String norm, int n, int kl, int ku, float[] ab, int ldab, float[] work)
/* 5341:     */   {
/* 5342:4646 */     return Slangb.slangb(norm, n, kl, ku, ab, 0, ldab, work, 0);
/* 5343:     */   }
/* 5344:     */   
/* 5345:     */   public float slangb(String norm, int n, int kl, int ku, float[] ab, int _ab_offset, int ldab, float[] work, int _work_offset)
/* 5346:     */   {
/* 5347:4651 */     return Slangb.slangb(norm, n, kl, ku, ab, _ab_offset, ldab, work, _work_offset);
/* 5348:     */   }
/* 5349:     */   
/* 5350:     */   public float slange(String norm, int m, int n, float[] a, int lda, float[] work)
/* 5351:     */   {
/* 5352:4656 */     return Slange.slange(norm, m, n, a, 0, lda, work, 0);
/* 5353:     */   }
/* 5354:     */   
/* 5355:     */   public float slange(String norm, int m, int n, float[] a, int _a_offset, int lda, float[] work, int _work_offset)
/* 5356:     */   {
/* 5357:4661 */     return Slange.slange(norm, m, n, a, _a_offset, lda, work, _work_offset);
/* 5358:     */   }
/* 5359:     */   
/* 5360:     */   public float slangt(String norm, int n, float[] dl, float[] d, float[] du)
/* 5361:     */   {
/* 5362:4666 */     return Slangt.slangt(norm, n, dl, 0, d, 0, du, 0);
/* 5363:     */   }
/* 5364:     */   
/* 5365:     */   public float slangt(String norm, int n, float[] dl, int _dl_offset, float[] d, int _d_offset, float[] du, int _du_offset)
/* 5366:     */   {
/* 5367:4671 */     return Slangt.slangt(norm, n, dl, _dl_offset, d, _d_offset, du, _du_offset);
/* 5368:     */   }
/* 5369:     */   
/* 5370:     */   public float slanhs(String norm, int n, float[] a, int lda, float[] work)
/* 5371:     */   {
/* 5372:4676 */     return Slanhs.slanhs(norm, n, a, 0, lda, work, 0);
/* 5373:     */   }
/* 5374:     */   
/* 5375:     */   public float slanhs(String norm, int n, float[] a, int _a_offset, int lda, float[] work, int _work_offset)
/* 5376:     */   {
/* 5377:4681 */     return Slanhs.slanhs(norm, n, a, _a_offset, lda, work, _work_offset);
/* 5378:     */   }
/* 5379:     */   
/* 5380:     */   public float slansb(String norm, String uplo, int n, int k, float[] ab, int ldab, float[] work)
/* 5381:     */   {
/* 5382:4686 */     return Slansb.slansb(norm, uplo, n, k, ab, 0, ldab, work, 0);
/* 5383:     */   }
/* 5384:     */   
/* 5385:     */   public float slansb(String norm, String uplo, int n, int k, float[] ab, int _ab_offset, int ldab, float[] work, int _work_offset)
/* 5386:     */   {
/* 5387:4691 */     return Slansb.slansb(norm, uplo, n, k, ab, _ab_offset, ldab, work, _work_offset);
/* 5388:     */   }
/* 5389:     */   
/* 5390:     */   public float slansp(String norm, String uplo, int n, float[] ap, float[] work)
/* 5391:     */   {
/* 5392:4696 */     return Slansp.slansp(norm, uplo, n, ap, 0, work, 0);
/* 5393:     */   }
/* 5394:     */   
/* 5395:     */   public float slansp(String norm, String uplo, int n, float[] ap, int _ap_offset, float[] work, int _work_offset)
/* 5396:     */   {
/* 5397:4701 */     return Slansp.slansp(norm, uplo, n, ap, _ap_offset, work, _work_offset);
/* 5398:     */   }
/* 5399:     */   
/* 5400:     */   public float slanst(String norm, int n, float[] d, float[] e)
/* 5401:     */   {
/* 5402:4706 */     return Slanst.slanst(norm, n, d, 0, e, 0);
/* 5403:     */   }
/* 5404:     */   
/* 5405:     */   public float slanst(String norm, int n, float[] d, int _d_offset, float[] e, int _e_offset)
/* 5406:     */   {
/* 5407:4711 */     return Slanst.slanst(norm, n, d, _d_offset, e, _e_offset);
/* 5408:     */   }
/* 5409:     */   
/* 5410:     */   public float slansy(String norm, String uplo, int n, float[] a, int lda, float[] work)
/* 5411:     */   {
/* 5412:4716 */     return Slansy.slansy(norm, uplo, n, a, 0, lda, work, 0);
/* 5413:     */   }
/* 5414:     */   
/* 5415:     */   public float slansy(String norm, String uplo, int n, float[] a, int _a_offset, int lda, float[] work, int _work_offset)
/* 5416:     */   {
/* 5417:4721 */     return Slansy.slansy(norm, uplo, n, a, _a_offset, lda, work, _work_offset);
/* 5418:     */   }
/* 5419:     */   
/* 5420:     */   public float slantb(String norm, String uplo, String diag, int n, int k, float[] ab, int ldab, float[] work)
/* 5421:     */   {
/* 5422:4726 */     return Slantb.slantb(norm, uplo, diag, n, k, ab, 0, ldab, work, 0);
/* 5423:     */   }
/* 5424:     */   
/* 5425:     */   public float slantb(String norm, String uplo, String diag, int n, int k, float[] ab, int _ab_offset, int ldab, float[] work, int _work_offset)
/* 5426:     */   {
/* 5427:4731 */     return Slantb.slantb(norm, uplo, diag, n, k, ab, _ab_offset, ldab, work, _work_offset);
/* 5428:     */   }
/* 5429:     */   
/* 5430:     */   public float slantp(String norm, String uplo, String diag, int n, float[] ap, float[] work)
/* 5431:     */   {
/* 5432:4736 */     return Slantp.slantp(norm, uplo, diag, n, ap, 0, work, 0);
/* 5433:     */   }
/* 5434:     */   
/* 5435:     */   public float slantp(String norm, String uplo, String diag, int n, float[] ap, int _ap_offset, float[] work, int _work_offset)
/* 5436:     */   {
/* 5437:4741 */     return Slantp.slantp(norm, uplo, diag, n, ap, _ap_offset, work, _work_offset);
/* 5438:     */   }
/* 5439:     */   
/* 5440:     */   public float slantr(String norm, String uplo, String diag, int m, int n, float[] a, int lda, float[] work)
/* 5441:     */   {
/* 5442:4746 */     return Slantr.slantr(norm, uplo, diag, m, n, a, 0, lda, work, 0);
/* 5443:     */   }
/* 5444:     */   
/* 5445:     */   public float slantr(String norm, String uplo, String diag, int m, int n, float[] a, int _a_offset, int lda, float[] work, int _work_offset)
/* 5446:     */   {
/* 5447:4751 */     return Slantr.slantr(norm, uplo, diag, m, n, a, _a_offset, lda, work, _work_offset);
/* 5448:     */   }
/* 5449:     */   
/* 5450:     */   public void slanv2(floatW a, floatW b, floatW c, floatW d, floatW rt1r, floatW rt1i, floatW rt2r, floatW rt2i, floatW cs, floatW sn)
/* 5451:     */   {
/* 5452:4756 */     Slanv2.slanv2(a, b, c, d, rt1r, rt1i, rt2r, rt2i, cs, sn);
/* 5453:     */   }
/* 5454:     */   
/* 5455:     */   public void slapll(int n, float[] x, int incx, float[] y, int incy, floatW ssmin)
/* 5456:     */   {
/* 5457:4761 */     Slapll.slapll(n, x, 0, incx, y, 0, incy, ssmin);
/* 5458:     */   }
/* 5459:     */   
/* 5460:     */   public void slapll(int n, float[] x, int _x_offset, int incx, float[] y, int _y_offset, int incy, floatW ssmin)
/* 5461:     */   {
/* 5462:4766 */     Slapll.slapll(n, x, _x_offset, incx, y, _y_offset, incy, ssmin);
/* 5463:     */   }
/* 5464:     */   
/* 5465:     */   public void slapmt(boolean forwrd, int m, int n, float[] x, int ldx, int[] k)
/* 5466:     */   {
/* 5467:4771 */     Slapmt.slapmt(forwrd, m, n, x, 0, ldx, k, 0);
/* 5468:     */   }
/* 5469:     */   
/* 5470:     */   public void slapmt(boolean forwrd, int m, int n, float[] x, int _x_offset, int ldx, int[] k, int _k_offset)
/* 5471:     */   {
/* 5472:4776 */     Slapmt.slapmt(forwrd, m, n, x, _x_offset, ldx, k, _k_offset);
/* 5473:     */   }
/* 5474:     */   
/* 5475:     */   public float slapy2(float x, float y)
/* 5476:     */   {
/* 5477:4781 */     return Slapy2.slapy2(x, y);
/* 5478:     */   }
/* 5479:     */   
/* 5480:     */   public float slapy3(float x, float y, float z)
/* 5481:     */   {
/* 5482:4786 */     return Slapy3.slapy3(x, y, z);
/* 5483:     */   }
/* 5484:     */   
/* 5485:     */   public void slaqgb(int m, int n, int kl, int ku, float[] ab, int ldab, float[] r, float[] c, float rowcnd, float colcnd, float amax, StringW equed)
/* 5486:     */   {
/* 5487:4791 */     Slaqgb.slaqgb(m, n, kl, ku, ab, 0, ldab, r, 0, c, 0, rowcnd, colcnd, amax, equed);
/* 5488:     */   }
/* 5489:     */   
/* 5490:     */   public void slaqgb(int m, int n, int kl, int ku, float[] ab, int _ab_offset, int ldab, float[] r, int _r_offset, float[] c, int _c_offset, float rowcnd, float colcnd, float amax, StringW equed)
/* 5491:     */   {
/* 5492:4796 */     Slaqgb.slaqgb(m, n, kl, ku, ab, _ab_offset, ldab, r, _r_offset, c, _c_offset, rowcnd, colcnd, amax, equed);
/* 5493:     */   }
/* 5494:     */   
/* 5495:     */   public void slaqge(int m, int n, float[] a, int lda, float[] r, float[] c, float rowcnd, float colcnd, float amax, StringW equed)
/* 5496:     */   {
/* 5497:4801 */     Slaqge.slaqge(m, n, a, 0, lda, r, 0, c, 0, rowcnd, colcnd, amax, equed);
/* 5498:     */   }
/* 5499:     */   
/* 5500:     */   public void slaqge(int m, int n, float[] a, int _a_offset, int lda, float[] r, int _r_offset, float[] c, int _c_offset, float rowcnd, float colcnd, float amax, StringW equed)
/* 5501:     */   {
/* 5502:4806 */     Slaqge.slaqge(m, n, a, _a_offset, lda, r, _r_offset, c, _c_offset, rowcnd, colcnd, amax, equed);
/* 5503:     */   }
/* 5504:     */   
/* 5505:     */   public void slaqp2(int m, int n, int offset, float[] a, int lda, int[] jpvt, float[] tau, float[] vn1, float[] vn2, float[] work)
/* 5506:     */   {
/* 5507:4811 */     Slaqp2.slaqp2(m, n, offset, a, 0, lda, jpvt, 0, tau, 0, vn1, 0, vn2, 0, work, 0);
/* 5508:     */   }
/* 5509:     */   
/* 5510:     */   public void slaqp2(int m, int n, int offset, float[] a, int _a_offset, int lda, int[] jpvt, int _jpvt_offset, float[] tau, int _tau_offset, float[] vn1, int _vn1_offset, float[] vn2, int _vn2_offset, float[] work, int _work_offset)
/* 5511:     */   {
/* 5512:4816 */     Slaqp2.slaqp2(m, n, offset, a, _a_offset, lda, jpvt, _jpvt_offset, tau, _tau_offset, vn1, _vn1_offset, vn2, _vn2_offset, work, _work_offset);
/* 5513:     */   }
/* 5514:     */   
/* 5515:     */   public void slaqps(int m, int n, int offset, int nb, intW kb, float[] a, int lda, int[] jpvt, float[] tau, float[] vn1, float[] vn2, float[] auxv, float[] f, int ldf)
/* 5516:     */   {
/* 5517:4821 */     Slaqps.slaqps(m, n, offset, nb, kb, a, 0, lda, jpvt, 0, tau, 0, vn1, 0, vn2, 0, auxv, 0, f, 0, ldf);
/* 5518:     */   }
/* 5519:     */   
/* 5520:     */   public void slaqps(int m, int n, int offset, int nb, intW kb, float[] a, int _a_offset, int lda, int[] jpvt, int _jpvt_offset, float[] tau, int _tau_offset, float[] vn1, int _vn1_offset, float[] vn2, int _vn2_offset, float[] auxv, int _auxv_offset, float[] f, int _f_offset, int ldf)
/* 5521:     */   {
/* 5522:4826 */     Slaqps.slaqps(m, n, offset, nb, kb, a, _a_offset, lda, jpvt, _jpvt_offset, tau, _tau_offset, vn1, _vn1_offset, vn2, _vn2_offset, auxv, _auxv_offset, f, _f_offset, ldf);
/* 5523:     */   }
/* 5524:     */   
/* 5525:     */   public void slaqr0(boolean wantt, boolean wantz, int n, int ilo, int ihi, float[] h, int ldh, float[] wr, float[] wi, int iloz, int ihiz, float[] z, int ldz, float[] work, int lwork, intW info)
/* 5526:     */   {
/* 5527:4831 */     Slaqr0.slaqr0(wantt, wantz, n, ilo, ihi, h, 0, ldh, wr, 0, wi, 0, iloz, ihiz, z, 0, ldz, work, 0, lwork, info);
/* 5528:     */   }
/* 5529:     */   
/* 5530:     */   public void slaqr0(boolean wantt, boolean wantz, int n, int ilo, int ihi, float[] h, int _h_offset, int ldh, float[] wr, int _wr_offset, float[] wi, int _wi_offset, int iloz, int ihiz, float[] z, int _z_offset, int ldz, float[] work, int _work_offset, int lwork, intW info)
/* 5531:     */   {
/* 5532:4836 */     Slaqr0.slaqr0(wantt, wantz, n, ilo, ihi, h, _h_offset, ldh, wr, _wr_offset, wi, _wi_offset, iloz, ihiz, z, _z_offset, ldz, work, _work_offset, lwork, info);
/* 5533:     */   }
/* 5534:     */   
/* 5535:     */   public void slaqr1(int n, float[] h, int ldh, float sr1, float si1, float sr2, float si2, float[] v)
/* 5536:     */   {
/* 5537:4841 */     Slaqr1.slaqr1(n, h, 0, ldh, sr1, si1, sr2, si2, v, 0);
/* 5538:     */   }
/* 5539:     */   
/* 5540:     */   public void slaqr1(int n, float[] h, int _h_offset, int ldh, float sr1, float si1, float sr2, float si2, float[] v, int _v_offset)
/* 5541:     */   {
/* 5542:4846 */     Slaqr1.slaqr1(n, h, _h_offset, ldh, sr1, si1, sr2, si2, v, _v_offset);
/* 5543:     */   }
/* 5544:     */   
/* 5545:     */   public void slaqr2(boolean wantt, boolean wantz, int n, int ktop, int kbot, int nw, float[] h, int ldh, int iloz, int ihiz, float[] z, int ldz, intW ns, intW nd, float[] sr, float[] si, float[] v, int ldv, int nh, float[] t, int ldt, int nv, float[] wv, int ldwv, float[] work, int lwork)
/* 5546:     */   {
/* 5547:4851 */     Slaqr2.slaqr2(wantt, wantz, n, ktop, kbot, nw, h, 0, ldh, iloz, ihiz, z, 0, ldz, ns, nd, sr, 0, si, 0, v, 0, ldv, nh, t, 0, ldt, nv, wv, 0, ldwv, work, 0, lwork);
/* 5548:     */   }
/* 5549:     */   
/* 5550:     */   public void slaqr2(boolean wantt, boolean wantz, int n, int ktop, int kbot, int nw, float[] h, int _h_offset, int ldh, int iloz, int ihiz, float[] z, int _z_offset, int ldz, intW ns, intW nd, float[] sr, int _sr_offset, float[] si, int _si_offset, float[] v, int _v_offset, int ldv, int nh, float[] t, int _t_offset, int ldt, int nv, float[] wv, int _wv_offset, int ldwv, float[] work, int _work_offset, int lwork)
/* 5551:     */   {
/* 5552:4856 */     Slaqr2.slaqr2(wantt, wantz, n, ktop, kbot, nw, h, _h_offset, ldh, iloz, ihiz, z, _z_offset, ldz, ns, nd, sr, _sr_offset, si, _si_offset, v, _v_offset, ldv, nh, t, _t_offset, ldt, nv, wv, _wv_offset, ldwv, work, _work_offset, lwork);
/* 5553:     */   }
/* 5554:     */   
/* 5555:     */   public void slaqr3(boolean wantt, boolean wantz, int n, int ktop, int kbot, int nw, float[] h, int ldh, int iloz, int ihiz, float[] z, int ldz, intW ns, intW nd, float[] sr, float[] si, float[] v, int ldv, int nh, float[] t, int ldt, int nv, float[] wv, int ldwv, float[] work, int lwork)
/* 5556:     */   {
/* 5557:4861 */     Slaqr3.slaqr3(wantt, wantz, n, ktop, kbot, nw, h, 0, ldh, iloz, ihiz, z, 0, ldz, ns, nd, sr, 0, si, 0, v, 0, ldv, nh, t, 0, ldt, nv, wv, 0, ldwv, work, 0, lwork);
/* 5558:     */   }
/* 5559:     */   
/* 5560:     */   public void slaqr3(boolean wantt, boolean wantz, int n, int ktop, int kbot, int nw, float[] h, int _h_offset, int ldh, int iloz, int ihiz, float[] z, int _z_offset, int ldz, intW ns, intW nd, float[] sr, int _sr_offset, float[] si, int _si_offset, float[] v, int _v_offset, int ldv, int nh, float[] t, int _t_offset, int ldt, int nv, float[] wv, int _wv_offset, int ldwv, float[] work, int _work_offset, int lwork)
/* 5561:     */   {
/* 5562:4866 */     Slaqr3.slaqr3(wantt, wantz, n, ktop, kbot, nw, h, _h_offset, ldh, iloz, ihiz, z, _z_offset, ldz, ns, nd, sr, _sr_offset, si, _si_offset, v, _v_offset, ldv, nh, t, _t_offset, ldt, nv, wv, _wv_offset, ldwv, work, _work_offset, lwork);
/* 5563:     */   }
/* 5564:     */   
/* 5565:     */   public void slaqr4(boolean wantt, boolean wantz, int n, int ilo, int ihi, float[] h, int ldh, float[] wr, float[] wi, int iloz, int ihiz, float[] z, int ldz, float[] work, int lwork, intW info)
/* 5566:     */   {
/* 5567:4871 */     Slaqr4.slaqr4(wantt, wantz, n, ilo, ihi, h, 0, ldh, wr, 0, wi, 0, iloz, ihiz, z, 0, ldz, work, 0, lwork, info);
/* 5568:     */   }
/* 5569:     */   
/* 5570:     */   public void slaqr4(boolean wantt, boolean wantz, int n, int ilo, int ihi, float[] h, int _h_offset, int ldh, float[] wr, int _wr_offset, float[] wi, int _wi_offset, int iloz, int ihiz, float[] z, int _z_offset, int ldz, float[] work, int _work_offset, int lwork, intW info)
/* 5571:     */   {
/* 5572:4876 */     Slaqr4.slaqr4(wantt, wantz, n, ilo, ihi, h, _h_offset, ldh, wr, _wr_offset, wi, _wi_offset, iloz, ihiz, z, _z_offset, ldz, work, _work_offset, lwork, info);
/* 5573:     */   }
/* 5574:     */   
/* 5575:     */   public void slaqr5(boolean wantt, boolean wantz, int kacc22, int n, int ktop, int kbot, int nshfts, float[] sr, float[] si, float[] h, int ldh, int iloz, int ihiz, float[] z, int ldz, float[] v, int ldv, float[] u, int ldu, int nv, float[] wv, int ldwv, int nh, float[] wh, int ldwh)
/* 5576:     */   {
/* 5577:4881 */     Slaqr5.slaqr5(wantt, wantz, kacc22, n, ktop, kbot, nshfts, sr, 0, si, 0, h, 0, ldh, iloz, ihiz, z, 0, ldz, v, 0, ldv, u, 0, ldu, nv, wv, 0, ldwv, nh, wh, 0, ldwh);
/* 5578:     */   }
/* 5579:     */   
/* 5580:     */   public void slaqr5(boolean wantt, boolean wantz, int kacc22, int n, int ktop, int kbot, int nshfts, float[] sr, int _sr_offset, float[] si, int _si_offset, float[] h, int _h_offset, int ldh, int iloz, int ihiz, float[] z, int _z_offset, int ldz, float[] v, int _v_offset, int ldv, float[] u, int _u_offset, int ldu, int nv, float[] wv, int _wv_offset, int ldwv, int nh, float[] wh, int _wh_offset, int ldwh)
/* 5581:     */   {
/* 5582:4886 */     Slaqr5.slaqr5(wantt, wantz, kacc22, n, ktop, kbot, nshfts, sr, _sr_offset, si, _si_offset, h, _h_offset, ldh, iloz, ihiz, z, _z_offset, ldz, v, _v_offset, ldv, u, _u_offset, ldu, nv, wv, _wv_offset, ldwv, nh, wh, _wh_offset, ldwh);
/* 5583:     */   }
/* 5584:     */   
/* 5585:     */   public void slaqsb(String uplo, int n, int kd, float[] ab, int ldab, float[] s, float scond, float amax, StringW equed)
/* 5586:     */   {
/* 5587:4891 */     Slaqsb.slaqsb(uplo, n, kd, ab, 0, ldab, s, 0, scond, amax, equed);
/* 5588:     */   }
/* 5589:     */   
/* 5590:     */   public void slaqsb(String uplo, int n, int kd, float[] ab, int _ab_offset, int ldab, float[] s, int _s_offset, float scond, float amax, StringW equed)
/* 5591:     */   {
/* 5592:4896 */     Slaqsb.slaqsb(uplo, n, kd, ab, _ab_offset, ldab, s, _s_offset, scond, amax, equed);
/* 5593:     */   }
/* 5594:     */   
/* 5595:     */   public void slaqsp(String uplo, int n, float[] ap, float[] s, float scond, float amax, StringW equed)
/* 5596:     */   {
/* 5597:4901 */     Slaqsp.slaqsp(uplo, n, ap, 0, s, 0, scond, amax, equed);
/* 5598:     */   }
/* 5599:     */   
/* 5600:     */   public void slaqsp(String uplo, int n, float[] ap, int _ap_offset, float[] s, int _s_offset, float scond, float amax, StringW equed)
/* 5601:     */   {
/* 5602:4906 */     Slaqsp.slaqsp(uplo, n, ap, _ap_offset, s, _s_offset, scond, amax, equed);
/* 5603:     */   }
/* 5604:     */   
/* 5605:     */   public void slaqsy(String uplo, int n, float[] a, int lda, float[] s, float scond, float amax, StringW equed)
/* 5606:     */   {
/* 5607:4911 */     Slaqsy.slaqsy(uplo, n, a, 0, lda, s, 0, scond, amax, equed);
/* 5608:     */   }
/* 5609:     */   
/* 5610:     */   public void slaqsy(String uplo, int n, float[] a, int _a_offset, int lda, float[] s, int _s_offset, float scond, float amax, StringW equed)
/* 5611:     */   {
/* 5612:4916 */     Slaqsy.slaqsy(uplo, n, a, _a_offset, lda, s, _s_offset, scond, amax, equed);
/* 5613:     */   }
/* 5614:     */   
/* 5615:     */   public void slaqtr(boolean ltran, boolean lreal, int n, float[] t, int ldt, float[] b, float w, floatW scale, float[] x, float[] work, intW info)
/* 5616:     */   {
/* 5617:4921 */     Slaqtr.slaqtr(ltran, lreal, n, t, 0, ldt, b, 0, w, scale, x, 0, work, 0, info);
/* 5618:     */   }
/* 5619:     */   
/* 5620:     */   public void slaqtr(boolean ltran, boolean lreal, int n, float[] t, int _t_offset, int ldt, float[] b, int _b_offset, float w, floatW scale, float[] x, int _x_offset, float[] work, int _work_offset, intW info)
/* 5621:     */   {
/* 5622:4926 */     Slaqtr.slaqtr(ltran, lreal, n, t, _t_offset, ldt, b, _b_offset, w, scale, x, _x_offset, work, _work_offset, info);
/* 5623:     */   }
/* 5624:     */   
/* 5625:     */   public void slar1v(int n, int b1, int bn, float lambda, float[] d, float[] l, float[] ld, float[] lld, float pivmin, float gaptol, float[] z, boolean wantnc, intW negcnt, floatW ztz, floatW mingma, intW r, int[] isuppz, floatW nrminv, floatW resid, floatW rqcorr, float[] work)
/* 5626:     */   {
/* 5627:4931 */     Slar1v.slar1v(n, b1, bn, lambda, d, 0, l, 0, ld, 0, lld, 0, pivmin, gaptol, z, 0, wantnc, negcnt, ztz, mingma, r, isuppz, 0, nrminv, resid, rqcorr, work, 0);
/* 5628:     */   }
/* 5629:     */   
/* 5630:     */   public void slar1v(int n, int b1, int bn, float lambda, float[] d, int _d_offset, float[] l, int _l_offset, float[] ld, int _ld_offset, float[] lld, int _lld_offset, float pivmin, float gaptol, float[] z, int _z_offset, boolean wantnc, intW negcnt, floatW ztz, floatW mingma, intW r, int[] isuppz, int _isuppz_offset, floatW nrminv, floatW resid, floatW rqcorr, float[] work, int _work_offset)
/* 5631:     */   {
/* 5632:4936 */     Slar1v.slar1v(n, b1, bn, lambda, d, _d_offset, l, _l_offset, ld, _ld_offset, lld, _lld_offset, pivmin, gaptol, z, _z_offset, wantnc, negcnt, ztz, mingma, r, isuppz, _isuppz_offset, nrminv, resid, rqcorr, work, _work_offset);
/* 5633:     */   }
/* 5634:     */   
/* 5635:     */   public void slar2v(int n, float[] x, float[] y, float[] z, int incx, float[] c, float[] s, int incc)
/* 5636:     */   {
/* 5637:4941 */     Slar2v.slar2v(n, x, 0, y, 0, z, 0, incx, c, 0, s, 0, incc);
/* 5638:     */   }
/* 5639:     */   
/* 5640:     */   public void slar2v(int n, float[] x, int _x_offset, float[] y, int _y_offset, float[] z, int _z_offset, int incx, float[] c, int _c_offset, float[] s, int _s_offset, int incc)
/* 5641:     */   {
/* 5642:4946 */     Slar2v.slar2v(n, x, _x_offset, y, _y_offset, z, _z_offset, incx, c, _c_offset, s, _s_offset, incc);
/* 5643:     */   }
/* 5644:     */   
/* 5645:     */   public void slarf(String side, int m, int n, float[] v, int incv, float tau, float[] c, int Ldc, float[] work)
/* 5646:     */   {
/* 5647:4951 */     Slarf.slarf(side, m, n, v, 0, incv, tau, c, 0, Ldc, work, 0);
/* 5648:     */   }
/* 5649:     */   
/* 5650:     */   public void slarf(String side, int m, int n, float[] v, int _v_offset, int incv, float tau, float[] c, int _c_offset, int Ldc, float[] work, int _work_offset)
/* 5651:     */   {
/* 5652:4956 */     Slarf.slarf(side, m, n, v, _v_offset, incv, tau, c, _c_offset, Ldc, work, _work_offset);
/* 5653:     */   }
/* 5654:     */   
/* 5655:     */   public void slarfb(String side, String trans, String direct, String storev, int m, int n, int k, float[] v, int ldv, float[] t, int ldt, float[] c, int Ldc, float[] work, int ldwork)
/* 5656:     */   {
/* 5657:4961 */     Slarfb.slarfb(side, trans, direct, storev, m, n, k, v, 0, ldv, t, 0, ldt, c, 0, Ldc, work, 0, ldwork);
/* 5658:     */   }
/* 5659:     */   
/* 5660:     */   public void slarfb(String side, String trans, String direct, String storev, int m, int n, int k, float[] v, int _v_offset, int ldv, float[] t, int _t_offset, int ldt, float[] c, int _c_offset, int Ldc, float[] work, int _work_offset, int ldwork)
/* 5661:     */   {
/* 5662:4966 */     Slarfb.slarfb(side, trans, direct, storev, m, n, k, v, _v_offset, ldv, t, _t_offset, ldt, c, _c_offset, Ldc, work, _work_offset, ldwork);
/* 5663:     */   }
/* 5664:     */   
/* 5665:     */   public void slarfg(int n, floatW alpha, float[] x, int incx, floatW tau)
/* 5666:     */   {
/* 5667:4971 */     Slarfg.slarfg(n, alpha, x, 0, incx, tau);
/* 5668:     */   }
/* 5669:     */   
/* 5670:     */   public void slarfg(int n, floatW alpha, float[] x, int _x_offset, int incx, floatW tau)
/* 5671:     */   {
/* 5672:4976 */     Slarfg.slarfg(n, alpha, x, _x_offset, incx, tau);
/* 5673:     */   }
/* 5674:     */   
/* 5675:     */   public void slarft(String direct, String storev, int n, int k, float[] v, int ldv, float[] tau, float[] t, int ldt)
/* 5676:     */   {
/* 5677:4981 */     Slarft.slarft(direct, storev, n, k, v, 0, ldv, tau, 0, t, 0, ldt);
/* 5678:     */   }
/* 5679:     */   
/* 5680:     */   public void slarft(String direct, String storev, int n, int k, float[] v, int _v_offset, int ldv, float[] tau, int _tau_offset, float[] t, int _t_offset, int ldt)
/* 5681:     */   {
/* 5682:4986 */     Slarft.slarft(direct, storev, n, k, v, _v_offset, ldv, tau, _tau_offset, t, _t_offset, ldt);
/* 5683:     */   }
/* 5684:     */   
/* 5685:     */   public void slarfx(String side, int m, int n, float[] v, float tau, float[] c, int Ldc, float[] work)
/* 5686:     */   {
/* 5687:4991 */     Slarfx.slarfx(side, m, n, v, 0, tau, c, 0, Ldc, work, 0);
/* 5688:     */   }
/* 5689:     */   
/* 5690:     */   public void slarfx(String side, int m, int n, float[] v, int _v_offset, float tau, float[] c, int _c_offset, int Ldc, float[] work, int _work_offset)
/* 5691:     */   {
/* 5692:4996 */     Slarfx.slarfx(side, m, n, v, _v_offset, tau, c, _c_offset, Ldc, work, _work_offset);
/* 5693:     */   }
/* 5694:     */   
/* 5695:     */   public void slargv(int n, float[] x, int incx, float[] y, int incy, float[] c, int incc)
/* 5696:     */   {
/* 5697:5001 */     Slargv.slargv(n, x, 0, incx, y, 0, incy, c, 0, incc);
/* 5698:     */   }
/* 5699:     */   
/* 5700:     */   public void slargv(int n, float[] x, int _x_offset, int incx, float[] y, int _y_offset, int incy, float[] c, int _c_offset, int incc)
/* 5701:     */   {
/* 5702:5006 */     Slargv.slargv(n, x, _x_offset, incx, y, _y_offset, incy, c, _c_offset, incc);
/* 5703:     */   }
/* 5704:     */   
/* 5705:     */   public void slarnv(int idist, int[] iseed, int n, float[] x)
/* 5706:     */   {
/* 5707:5011 */     Slarnv.slarnv(idist, iseed, 0, n, x, 0);
/* 5708:     */   }
/* 5709:     */   
/* 5710:     */   public void slarnv(int idist, int[] iseed, int _iseed_offset, int n, float[] x, int _x_offset)
/* 5711:     */   {
/* 5712:5016 */     Slarnv.slarnv(idist, iseed, _iseed_offset, n, x, _x_offset);
/* 5713:     */   }
/* 5714:     */   
/* 5715:     */   public void slarra(int n, float[] d, float[] e, float[] e2, float spltol, float tnrm, intW nsplit, int[] isplit, intW info)
/* 5716:     */   {
/* 5717:5021 */     Slarra.slarra(n, d, 0, e, 0, e2, 0, spltol, tnrm, nsplit, isplit, 0, info);
/* 5718:     */   }
/* 5719:     */   
/* 5720:     */   public void slarra(int n, float[] d, int _d_offset, float[] e, int _e_offset, float[] e2, int _e2_offset, float spltol, float tnrm, intW nsplit, int[] isplit, int _isplit_offset, intW info)
/* 5721:     */   {
/* 5722:5026 */     Slarra.slarra(n, d, _d_offset, e, _e_offset, e2, _e2_offset, spltol, tnrm, nsplit, isplit, _isplit_offset, info);
/* 5723:     */   }
/* 5724:     */   
/* 5725:     */   public void slarrb(int n, float[] d, float[] lld, int ifirst, int ilast, float rtol1, float rtol2, int offset, float[] w, float[] wgap, float[] werr, float[] work, int[] iwork, float pivmin, float spdiam, int twist, intW info)
/* 5726:     */   {
/* 5727:5031 */     Slarrb.slarrb(n, d, 0, lld, 0, ifirst, ilast, rtol1, rtol2, offset, w, 0, wgap, 0, werr, 0, work, 0, iwork, 0, pivmin, spdiam, twist, info);
/* 5728:     */   }
/* 5729:     */   
/* 5730:     */   public void slarrb(int n, float[] d, int _d_offset, float[] lld, int _lld_offset, int ifirst, int ilast, float rtol1, float rtol2, int offset, float[] w, int _w_offset, float[] wgap, int _wgap_offset, float[] werr, int _werr_offset, float[] work, int _work_offset, int[] iwork, int _iwork_offset, float pivmin, float spdiam, int twist, intW info)
/* 5731:     */   {
/* 5732:5036 */     Slarrb.slarrb(n, d, _d_offset, lld, _lld_offset, ifirst, ilast, rtol1, rtol2, offset, w, _w_offset, wgap, _wgap_offset, werr, _werr_offset, work, _work_offset, iwork, _iwork_offset, pivmin, spdiam, twist, info);
/* 5733:     */   }
/* 5734:     */   
/* 5735:     */   public void slarrc(String jobt, int n, float vl, float vu, float[] d, float[] e, float pivmin, intW eigcnt, intW lcnt, intW rcnt, intW info)
/* 5736:     */   {
/* 5737:5041 */     Slarrc.slarrc(jobt, n, vl, vu, d, 0, e, 0, pivmin, eigcnt, lcnt, rcnt, info);
/* 5738:     */   }
/* 5739:     */   
/* 5740:     */   public void slarrc(String jobt, int n, float vl, float vu, float[] d, int _d_offset, float[] e, int _e_offset, float pivmin, intW eigcnt, intW lcnt, intW rcnt, intW info)
/* 5741:     */   {
/* 5742:5046 */     Slarrc.slarrc(jobt, n, vl, vu, d, _d_offset, e, _e_offset, pivmin, eigcnt, lcnt, rcnt, info);
/* 5743:     */   }
/* 5744:     */   
/* 5745:     */   public void slarrd(String range, String order, int n, float vl, float vu, int il, int iu, float[] gers, float reltol, float[] d, float[] e, float[] e2, float pivmin, int nsplit, int[] isplit, intW m, float[] w, float[] werr, floatW wl, floatW wu, int[] iblock, int[] indexw, float[] work, int[] iwork, intW info)
/* 5746:     */   {
/* 5747:5051 */     Slarrd.slarrd(range, order, n, vl, vu, il, iu, gers, 0, reltol, d, 0, e, 0, e2, 0, pivmin, nsplit, isplit, 0, m, w, 0, werr, 0, wl, wu, iblock, 0, indexw, 0, work, 0, iwork, 0, info);
/* 5748:     */   }
/* 5749:     */   
/* 5750:     */   public void slarrd(String range, String order, int n, float vl, float vu, int il, int iu, float[] gers, int _gers_offset, float reltol, float[] d, int _d_offset, float[] e, int _e_offset, float[] e2, int _e2_offset, float pivmin, int nsplit, int[] isplit, int _isplit_offset, intW m, float[] w, int _w_offset, float[] werr, int _werr_offset, floatW wl, floatW wu, int[] iblock, int _iblock_offset, int[] indexw, int _indexw_offset, float[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 5751:     */   {
/* 5752:5056 */     Slarrd.slarrd(range, order, n, vl, vu, il, iu, gers, _gers_offset, reltol, d, _d_offset, e, _e_offset, e2, _e2_offset, pivmin, nsplit, isplit, _isplit_offset, m, w, _w_offset, werr, _werr_offset, wl, wu, iblock, _iblock_offset, indexw, _indexw_offset, work, _work_offset, iwork, _iwork_offset, info);
/* 5753:     */   }
/* 5754:     */   
/* 5755:     */   public void slarre(String range, int n, floatW vl, floatW vu, int il, int iu, float[] d, float[] e, float[] e2, float rtol1, float rtol2, float spltol, intW nsplit, int[] isplit, intW m, float[] w, float[] werr, float[] wgap, int[] iblock, int[] indexw, float[] gers, floatW pivmin, float[] work, int[] iwork, intW info)
/* 5756:     */   {
/* 5757:5061 */     Slarre.slarre(range, n, vl, vu, il, iu, d, 0, e, 0, e2, 0, rtol1, rtol2, spltol, nsplit, isplit, 0, m, w, 0, werr, 0, wgap, 0, iblock, 0, indexw, 0, gers, 0, pivmin, work, 0, iwork, 0, info);
/* 5758:     */   }
/* 5759:     */   
/* 5760:     */   public void slarre(String range, int n, floatW vl, floatW vu, int il, int iu, float[] d, int _d_offset, float[] e, int _e_offset, float[] e2, int _e2_offset, float rtol1, float rtol2, float spltol, intW nsplit, int[] isplit, int _isplit_offset, intW m, float[] w, int _w_offset, float[] werr, int _werr_offset, float[] wgap, int _wgap_offset, int[] iblock, int _iblock_offset, int[] indexw, int _indexw_offset, float[] gers, int _gers_offset, floatW pivmin, float[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 5761:     */   {
/* 5762:5066 */     Slarre.slarre(range, n, vl, vu, il, iu, d, _d_offset, e, _e_offset, e2, _e2_offset, rtol1, rtol2, spltol, nsplit, isplit, _isplit_offset, m, w, _w_offset, werr, _werr_offset, wgap, _wgap_offset, iblock, _iblock_offset, indexw, _indexw_offset, gers, _gers_offset, pivmin, work, _work_offset, iwork, _iwork_offset, info);
/* 5763:     */   }
/* 5764:     */   
/* 5765:     */   public void slarrf(int n, float[] d, float[] l, float[] ld, int clstrt, int clend, float[] w, float[] wgap, float[] werr, float spdiam, float clgapl, float clgapr, float pivmin, floatW sigma, float[] dplus, float[] lplus, float[] work, intW info)
/* 5766:     */   {
/* 5767:5071 */     Slarrf.slarrf(n, d, 0, l, 0, ld, 0, clstrt, clend, w, 0, wgap, 0, werr, 0, spdiam, clgapl, clgapr, pivmin, sigma, dplus, 0, lplus, 0, work, 0, info);
/* 5768:     */   }
/* 5769:     */   
/* 5770:     */   public void slarrf(int n, float[] d, int _d_offset, float[] l, int _l_offset, float[] ld, int _ld_offset, int clstrt, int clend, float[] w, int _w_offset, float[] wgap, int _wgap_offset, float[] werr, int _werr_offset, float spdiam, float clgapl, float clgapr, float pivmin, floatW sigma, float[] dplus, int _dplus_offset, float[] lplus, int _lplus_offset, float[] work, int _work_offset, intW info)
/* 5771:     */   {
/* 5772:5076 */     Slarrf.slarrf(n, d, _d_offset, l, _l_offset, ld, _ld_offset, clstrt, clend, w, _w_offset, wgap, _wgap_offset, werr, _werr_offset, spdiam, clgapl, clgapr, pivmin, sigma, dplus, _dplus_offset, lplus, _lplus_offset, work, _work_offset, info);
/* 5773:     */   }
/* 5774:     */   
/* 5775:     */   public void slarrj(int n, float[] d, float[] e2, int ifirst, int ilast, float rtol, int offset, float[] w, float[] werr, float[] work, int[] iwork, float pivmin, float spdiam, intW info)
/* 5776:     */   {
/* 5777:5081 */     Slarrj.slarrj(n, d, 0, e2, 0, ifirst, ilast, rtol, offset, w, 0, werr, 0, work, 0, iwork, 0, pivmin, spdiam, info);
/* 5778:     */   }
/* 5779:     */   
/* 5780:     */   public void slarrj(int n, float[] d, int _d_offset, float[] e2, int _e2_offset, int ifirst, int ilast, float rtol, int offset, float[] w, int _w_offset, float[] werr, int _werr_offset, float[] work, int _work_offset, int[] iwork, int _iwork_offset, float pivmin, float spdiam, intW info)
/* 5781:     */   {
/* 5782:5086 */     Slarrj.slarrj(n, d, _d_offset, e2, _e2_offset, ifirst, ilast, rtol, offset, w, _w_offset, werr, _werr_offset, work, _work_offset, iwork, _iwork_offset, pivmin, spdiam, info);
/* 5783:     */   }
/* 5784:     */   
/* 5785:     */   public void slarrk(int n, int iw, float gl, float gu, float[] d, float[] e2, float pivmin, float reltol, floatW w, floatW werr, intW info)
/* 5786:     */   {
/* 5787:5091 */     Slarrk.slarrk(n, iw, gl, gu, d, 0, e2, 0, pivmin, reltol, w, werr, info);
/* 5788:     */   }
/* 5789:     */   
/* 5790:     */   public void slarrk(int n, int iw, float gl, float gu, float[] d, int _d_offset, float[] e2, int _e2_offset, float pivmin, float reltol, floatW w, floatW werr, intW info)
/* 5791:     */   {
/* 5792:5096 */     Slarrk.slarrk(n, iw, gl, gu, d, _d_offset, e2, _e2_offset, pivmin, reltol, w, werr, info);
/* 5793:     */   }
/* 5794:     */   
/* 5795:     */   public void slarrr(int n, float[] d, float[] e, intW info)
/* 5796:     */   {
/* 5797:5101 */     Slarrr.slarrr(n, d, 0, e, 0, info);
/* 5798:     */   }
/* 5799:     */   
/* 5800:     */   public void slarrr(int n, float[] d, int _d_offset, float[] e, int _e_offset, intW info)
/* 5801:     */   {
/* 5802:5106 */     Slarrr.slarrr(n, d, _d_offset, e, _e_offset, info);
/* 5803:     */   }
/* 5804:     */   
/* 5805:     */   public void slarrv(int n, float vl, float vu, float[] d, float[] l, float pivmin, int[] isplit, int m, int dol, int dou, float minrgp, floatW rtol1, floatW rtol2, float[] w, float[] werr, float[] wgap, int[] iblock, int[] indexw, float[] gers, float[] z, int ldz, int[] isuppz, float[] work, int[] iwork, intW info)
/* 5806:     */   {
/* 5807:5111 */     Slarrv.slarrv(n, vl, vu, d, 0, l, 0, pivmin, isplit, 0, m, dol, dou, minrgp, rtol1, rtol2, w, 0, werr, 0, wgap, 0, iblock, 0, indexw, 0, gers, 0, z, 0, ldz, isuppz, 0, work, 0, iwork, 0, info);
/* 5808:     */   }
/* 5809:     */   
/* 5810:     */   public void slarrv(int n, float vl, float vu, float[] d, int _d_offset, float[] l, int _l_offset, float pivmin, int[] isplit, int _isplit_offset, int m, int dol, int dou, float minrgp, floatW rtol1, floatW rtol2, float[] w, int _w_offset, float[] werr, int _werr_offset, float[] wgap, int _wgap_offset, int[] iblock, int _iblock_offset, int[] indexw, int _indexw_offset, float[] gers, int _gers_offset, float[] z, int _z_offset, int ldz, int[] isuppz, int _isuppz_offset, float[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 5811:     */   {
/* 5812:5116 */     Slarrv.slarrv(n, vl, vu, d, _d_offset, l, _l_offset, pivmin, isplit, _isplit_offset, m, dol, dou, minrgp, rtol1, rtol2, w, _w_offset, werr, _werr_offset, wgap, _wgap_offset, iblock, _iblock_offset, indexw, _indexw_offset, gers, _gers_offset, z, _z_offset, ldz, isuppz, _isuppz_offset, work, _work_offset, iwork, _iwork_offset, info);
/* 5813:     */   }
/* 5814:     */   
/* 5815:     */   public void slartg(float f, float g, floatW cs, floatW sn, floatW r)
/* 5816:     */   {
/* 5817:5121 */     Slartg.slartg(f, g, cs, sn, r);
/* 5818:     */   }
/* 5819:     */   
/* 5820:     */   public void slartv(int n, float[] x, int incx, float[] y, int incy, float[] c, float[] s, int incc)
/* 5821:     */   {
/* 5822:5126 */     Slartv.slartv(n, x, 0, incx, y, 0, incy, c, 0, s, 0, incc);
/* 5823:     */   }
/* 5824:     */   
/* 5825:     */   public void slartv(int n, float[] x, int _x_offset, int incx, float[] y, int _y_offset, int incy, float[] c, int _c_offset, float[] s, int _s_offset, int incc)
/* 5826:     */   {
/* 5827:5131 */     Slartv.slartv(n, x, _x_offset, incx, y, _y_offset, incy, c, _c_offset, s, _s_offset, incc);
/* 5828:     */   }
/* 5829:     */   
/* 5830:     */   public void slaruv(int[] iseed, int n, float[] x)
/* 5831:     */   {
/* 5832:5136 */     Slaruv.slaruv(iseed, 0, n, x, 0);
/* 5833:     */   }
/* 5834:     */   
/* 5835:     */   public void slaruv(int[] iseed, int _iseed_offset, int n, float[] x, int _x_offset)
/* 5836:     */   {
/* 5837:5141 */     Slaruv.slaruv(iseed, _iseed_offset, n, x, _x_offset);
/* 5838:     */   }
/* 5839:     */   
/* 5840:     */   public void slarz(String side, int m, int n, int l, float[] v, int incv, float tau, float[] c, int Ldc, float[] work)
/* 5841:     */   {
/* 5842:5146 */     Slarz.slarz(side, m, n, l, v, 0, incv, tau, c, 0, Ldc, work, 0);
/* 5843:     */   }
/* 5844:     */   
/* 5845:     */   public void slarz(String side, int m, int n, int l, float[] v, int _v_offset, int incv, float tau, float[] c, int _c_offset, int Ldc, float[] work, int _work_offset)
/* 5846:     */   {
/* 5847:5151 */     Slarz.slarz(side, m, n, l, v, _v_offset, incv, tau, c, _c_offset, Ldc, work, _work_offset);
/* 5848:     */   }
/* 5849:     */   
/* 5850:     */   public void slarzb(String side, String trans, String direct, String storev, int m, int n, int k, int l, float[] v, int ldv, float[] t, int ldt, float[] c, int Ldc, float[] work, int ldwork)
/* 5851:     */   {
/* 5852:5156 */     Slarzb.slarzb(side, trans, direct, storev, m, n, k, l, v, 0, ldv, t, 0, ldt, c, 0, Ldc, work, 0, ldwork);
/* 5853:     */   }
/* 5854:     */   
/* 5855:     */   public void slarzb(String side, String trans, String direct, String storev, int m, int n, int k, int l, float[] v, int _v_offset, int ldv, float[] t, int _t_offset, int ldt, float[] c, int _c_offset, int Ldc, float[] work, int _work_offset, int ldwork)
/* 5856:     */   {
/* 5857:5161 */     Slarzb.slarzb(side, trans, direct, storev, m, n, k, l, v, _v_offset, ldv, t, _t_offset, ldt, c, _c_offset, Ldc, work, _work_offset, ldwork);
/* 5858:     */   }
/* 5859:     */   
/* 5860:     */   public void slarzt(String direct, String storev, int n, int k, float[] v, int ldv, float[] tau, float[] t, int ldt)
/* 5861:     */   {
/* 5862:5166 */     Slarzt.slarzt(direct, storev, n, k, v, 0, ldv, tau, 0, t, 0, ldt);
/* 5863:     */   }
/* 5864:     */   
/* 5865:     */   public void slarzt(String direct, String storev, int n, int k, float[] v, int _v_offset, int ldv, float[] tau, int _tau_offset, float[] t, int _t_offset, int ldt)
/* 5866:     */   {
/* 5867:5171 */     Slarzt.slarzt(direct, storev, n, k, v, _v_offset, ldv, tau, _tau_offset, t, _t_offset, ldt);
/* 5868:     */   }
/* 5869:     */   
/* 5870:     */   public void slas2(float f, float g, float h, floatW ssmin, floatW ssmax)
/* 5871:     */   {
/* 5872:5176 */     Slas2.slas2(f, g, h, ssmin, ssmax);
/* 5873:     */   }
/* 5874:     */   
/* 5875:     */   public void slascl(String type, int kl, int ku, float cfrom, float cto, int m, int n, float[] a, int lda, intW info)
/* 5876:     */   {
/* 5877:5181 */     Slascl.slascl(type, kl, ku, cfrom, cto, m, n, a, 0, lda, info);
/* 5878:     */   }
/* 5879:     */   
/* 5880:     */   public void slascl(String type, int kl, int ku, float cfrom, float cto, int m, int n, float[] a, int _a_offset, int lda, intW info)
/* 5881:     */   {
/* 5882:5186 */     Slascl.slascl(type, kl, ku, cfrom, cto, m, n, a, _a_offset, lda, info);
/* 5883:     */   }
/* 5884:     */   
/* 5885:     */   public void slasd0(int n, int sqre, float[] d, float[] e, float[] u, int ldu, float[] vt, int ldvt, int smlsiz, int[] iwork, float[] work, intW info)
/* 5886:     */   {
/* 5887:5191 */     Slasd0.slasd0(n, sqre, d, 0, e, 0, u, 0, ldu, vt, 0, ldvt, smlsiz, iwork, 0, work, 0, info);
/* 5888:     */   }
/* 5889:     */   
/* 5890:     */   public void slasd0(int n, int sqre, float[] d, int _d_offset, float[] e, int _e_offset, float[] u, int _u_offset, int ldu, float[] vt, int _vt_offset, int ldvt, int smlsiz, int[] iwork, int _iwork_offset, float[] work, int _work_offset, intW info)
/* 5891:     */   {
/* 5892:5196 */     Slasd0.slasd0(n, sqre, d, _d_offset, e, _e_offset, u, _u_offset, ldu, vt, _vt_offset, ldvt, smlsiz, iwork, _iwork_offset, work, _work_offset, info);
/* 5893:     */   }
/* 5894:     */   
/* 5895:     */   public void slasd1(int nl, int nr, int sqre, float[] d, floatW alpha, floatW beta, float[] u, int ldu, float[] vt, int ldvt, int[] idxq, int[] iwork, float[] work, intW info)
/* 5896:     */   {
/* 5897:5201 */     Slasd1.slasd1(nl, nr, sqre, d, 0, alpha, beta, u, 0, ldu, vt, 0, ldvt, idxq, 0, iwork, 0, work, 0, info);
/* 5898:     */   }
/* 5899:     */   
/* 5900:     */   public void slasd1(int nl, int nr, int sqre, float[] d, int _d_offset, floatW alpha, floatW beta, float[] u, int _u_offset, int ldu, float[] vt, int _vt_offset, int ldvt, int[] idxq, int _idxq_offset, int[] iwork, int _iwork_offset, float[] work, int _work_offset, intW info)
/* 5901:     */   {
/* 5902:5206 */     Slasd1.slasd1(nl, nr, sqre, d, _d_offset, alpha, beta, u, _u_offset, ldu, vt, _vt_offset, ldvt, idxq, _idxq_offset, iwork, _iwork_offset, work, _work_offset, info);
/* 5903:     */   }
/* 5904:     */   
/* 5905:     */   public void slasd2(int nl, int nr, int sqre, intW k, float[] d, float[] z, float alpha, float beta, float[] u, int ldu, float[] vt, int ldvt, float[] dsigma, float[] u2, int ldu2, float[] vt2, int ldvt2, int[] idxp, int[] idx, int[] idxc, int[] idxq, int[] coltyp, intW info)
/* 5906:     */   {
/* 5907:5211 */     Slasd2.slasd2(nl, nr, sqre, k, d, 0, z, 0, alpha, beta, u, 0, ldu, vt, 0, ldvt, dsigma, 0, u2, 0, ldu2, vt2, 0, ldvt2, idxp, 0, idx, 0, idxc, 0, idxq, 0, coltyp, 0, info);
/* 5908:     */   }
/* 5909:     */   
/* 5910:     */   public void slasd2(int nl, int nr, int sqre, intW k, float[] d, int _d_offset, float[] z, int _z_offset, float alpha, float beta, float[] u, int _u_offset, int ldu, float[] vt, int _vt_offset, int ldvt, float[] dsigma, int _dsigma_offset, float[] u2, int _u2_offset, int ldu2, float[] vt2, int _vt2_offset, int ldvt2, int[] idxp, int _idxp_offset, int[] idx, int _idx_offset, int[] idxc, int _idxc_offset, int[] idxq, int _idxq_offset, int[] coltyp, int _coltyp_offset, intW info)
/* 5911:     */   {
/* 5912:5216 */     Slasd2.slasd2(nl, nr, sqre, k, d, _d_offset, z, _z_offset, alpha, beta, u, _u_offset, ldu, vt, _vt_offset, ldvt, dsigma, _dsigma_offset, u2, _u2_offset, ldu2, vt2, _vt2_offset, ldvt2, idxp, _idxp_offset, idx, _idx_offset, idxc, _idxc_offset, idxq, _idxq_offset, coltyp, _coltyp_offset, info);
/* 5913:     */   }
/* 5914:     */   
/* 5915:     */   public void slasd3(int nl, int nr, int sqre, int k, float[] d, float[] q, int ldq, float[] dsigma, float[] u, int ldu, float[] u2, int ldu2, float[] vt, int ldvt, float[] vt2, int ldvt2, int[] idxc, int[] ctot, float[] z, intW info)
/* 5916:     */   {
/* 5917:5221 */     Slasd3.slasd3(nl, nr, sqre, k, d, 0, q, 0, ldq, dsigma, 0, u, 0, ldu, u2, 0, ldu2, vt, 0, ldvt, vt2, 0, ldvt2, idxc, 0, ctot, 0, z, 0, info);
/* 5918:     */   }
/* 5919:     */   
/* 5920:     */   public void slasd3(int nl, int nr, int sqre, int k, float[] d, int _d_offset, float[] q, int _q_offset, int ldq, float[] dsigma, int _dsigma_offset, float[] u, int _u_offset, int ldu, float[] u2, int _u2_offset, int ldu2, float[] vt, int _vt_offset, int ldvt, float[] vt2, int _vt2_offset, int ldvt2, int[] idxc, int _idxc_offset, int[] ctot, int _ctot_offset, float[] z, int _z_offset, intW info)
/* 5921:     */   {
/* 5922:5226 */     Slasd3.slasd3(nl, nr, sqre, k, d, _d_offset, q, _q_offset, ldq, dsigma, _dsigma_offset, u, _u_offset, ldu, u2, _u2_offset, ldu2, vt, _vt_offset, ldvt, vt2, _vt2_offset, ldvt2, idxc, _idxc_offset, ctot, _ctot_offset, z, _z_offset, info);
/* 5923:     */   }
/* 5924:     */   
/* 5925:     */   public void slasd4(int n, int i, float[] d, float[] z, float[] delta, float rho, floatW sigma, float[] work, intW info)
/* 5926:     */   {
/* 5927:5231 */     Slasd4.slasd4(n, i, d, 0, z, 0, delta, 0, rho, sigma, work, 0, info);
/* 5928:     */   }
/* 5929:     */   
/* 5930:     */   public void slasd4(int n, int i, float[] d, int _d_offset, float[] z, int _z_offset, float[] delta, int _delta_offset, float rho, floatW sigma, float[] work, int _work_offset, intW info)
/* 5931:     */   {
/* 5932:5236 */     Slasd4.slasd4(n, i, d, _d_offset, z, _z_offset, delta, _delta_offset, rho, sigma, work, _work_offset, info);
/* 5933:     */   }
/* 5934:     */   
/* 5935:     */   public void slasd5(int i, float[] d, float[] z, float[] delta, float rho, floatW dsigma, float[] work)
/* 5936:     */   {
/* 5937:5241 */     Slasd5.slasd5(i, d, 0, z, 0, delta, 0, rho, dsigma, work, 0);
/* 5938:     */   }
/* 5939:     */   
/* 5940:     */   public void slasd5(int i, float[] d, int _d_offset, float[] z, int _z_offset, float[] delta, int _delta_offset, float rho, floatW dsigma, float[] work, int _work_offset)
/* 5941:     */   {
/* 5942:5246 */     Slasd5.slasd5(i, d, _d_offset, z, _z_offset, delta, _delta_offset, rho, dsigma, work, _work_offset);
/* 5943:     */   }
/* 5944:     */   
/* 5945:     */   public void slasd6(int icompq, int nl, int nr, int sqre, float[] d, float[] vf, float[] vl, floatW alpha, floatW beta, int[] idxq, int[] perm, intW givptr, int[] givcol, int ldgcol, float[] givnum, int ldgnum, float[] poles, float[] difl, float[] difr, float[] z, intW k, floatW c, floatW s, float[] work, int[] iwork, intW info)
/* 5946:     */   {
/* 5947:5251 */     Slasd6.slasd6(icompq, nl, nr, sqre, d, 0, vf, 0, vl, 0, alpha, beta, idxq, 0, perm, 0, givptr, givcol, 0, ldgcol, givnum, 0, ldgnum, poles, 0, difl, 0, difr, 0, z, 0, k, c, s, work, 0, iwork, 0, info);
/* 5948:     */   }
/* 5949:     */   
/* 5950:     */   public void slasd6(int icompq, int nl, int nr, int sqre, float[] d, int _d_offset, float[] vf, int _vf_offset, float[] vl, int _vl_offset, floatW alpha, floatW beta, int[] idxq, int _idxq_offset, int[] perm, int _perm_offset, intW givptr, int[] givcol, int _givcol_offset, int ldgcol, float[] givnum, int _givnum_offset, int ldgnum, float[] poles, int _poles_offset, float[] difl, int _difl_offset, float[] difr, int _difr_offset, float[] z, int _z_offset, intW k, floatW c, floatW s, float[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 5951:     */   {
/* 5952:5256 */     Slasd6.slasd6(icompq, nl, nr, sqre, d, _d_offset, vf, _vf_offset, vl, _vl_offset, alpha, beta, idxq, _idxq_offset, perm, _perm_offset, givptr, givcol, _givcol_offset, ldgcol, givnum, _givnum_offset, ldgnum, poles, _poles_offset, difl, _difl_offset, difr, _difr_offset, z, _z_offset, k, c, s, work, _work_offset, iwork, _iwork_offset, info);
/* 5953:     */   }
/* 5954:     */   
/* 5955:     */   public void slasd7(int icompq, int nl, int nr, int sqre, intW k, float[] d, float[] z, float[] zw, float[] vf, float[] vfw, float[] vl, float[] vlw, float alpha, float beta, float[] dsigma, int[] idx, int[] idxp, int[] idxq, int[] perm, intW givptr, int[] givcol, int ldgcol, float[] givnum, int ldgnum, floatW c, floatW s, intW info)
/* 5956:     */   {
/* 5957:5261 */     Slasd7.slasd7(icompq, nl, nr, sqre, k, d, 0, z, 0, zw, 0, vf, 0, vfw, 0, vl, 0, vlw, 0, alpha, beta, dsigma, 0, idx, 0, idxp, 0, idxq, 0, perm, 0, givptr, givcol, 0, ldgcol, givnum, 0, ldgnum, c, s, info);
/* 5958:     */   }
/* 5959:     */   
/* 5960:     */   public void slasd7(int icompq, int nl, int nr, int sqre, intW k, float[] d, int _d_offset, float[] z, int _z_offset, float[] zw, int _zw_offset, float[] vf, int _vf_offset, float[] vfw, int _vfw_offset, float[] vl, int _vl_offset, float[] vlw, int _vlw_offset, float alpha, float beta, float[] dsigma, int _dsigma_offset, int[] idx, int _idx_offset, int[] idxp, int _idxp_offset, int[] idxq, int _idxq_offset, int[] perm, int _perm_offset, intW givptr, int[] givcol, int _givcol_offset, int ldgcol, float[] givnum, int _givnum_offset, int ldgnum, floatW c, floatW s, intW info)
/* 5961:     */   {
/* 5962:5266 */     Slasd7.slasd7(icompq, nl, nr, sqre, k, d, _d_offset, z, _z_offset, zw, _zw_offset, vf, _vf_offset, vfw, _vfw_offset, vl, _vl_offset, vlw, _vlw_offset, alpha, beta, dsigma, _dsigma_offset, idx, _idx_offset, idxp, _idxp_offset, idxq, _idxq_offset, perm, _perm_offset, givptr, givcol, _givcol_offset, ldgcol, givnum, _givnum_offset, ldgnum, c, s, info);
/* 5963:     */   }
/* 5964:     */   
/* 5965:     */   public void slasd8(int icompq, int k, float[] d, float[] z, float[] vf, float[] vl, float[] difl, float[] difr, int lddifr, float[] dsigma, float[] work, intW info)
/* 5966:     */   {
/* 5967:5271 */     Slasd8.slasd8(icompq, k, d, 0, z, 0, vf, 0, vl, 0, difl, 0, difr, 0, lddifr, dsigma, 0, work, 0, info);
/* 5968:     */   }
/* 5969:     */   
/* 5970:     */   public void slasd8(int icompq, int k, float[] d, int _d_offset, float[] z, int _z_offset, float[] vf, int _vf_offset, float[] vl, int _vl_offset, float[] difl, int _difl_offset, float[] difr, int _difr_offset, int lddifr, float[] dsigma, int _dsigma_offset, float[] work, int _work_offset, intW info)
/* 5971:     */   {
/* 5972:5276 */     Slasd8.slasd8(icompq, k, d, _d_offset, z, _z_offset, vf, _vf_offset, vl, _vl_offset, difl, _difl_offset, difr, _difr_offset, lddifr, dsigma, _dsigma_offset, work, _work_offset, info);
/* 5973:     */   }
/* 5974:     */   
/* 5975:     */   public void slasda(int icompq, int smlsiz, int n, int sqre, float[] d, float[] e, float[] u, int ldu, float[] vt, int[] k, float[] difl, float[] difr, float[] z, float[] poles, int[] givptr, int[] givcol, int ldgcol, int[] perm, float[] givnum, float[] c, float[] s, float[] work, int[] iwork, intW info)
/* 5976:     */   {
/* 5977:5281 */     Slasda.slasda(icompq, smlsiz, n, sqre, d, 0, e, 0, u, 0, ldu, vt, 0, k, 0, difl, 0, difr, 0, z, 0, poles, 0, givptr, 0, givcol, 0, ldgcol, perm, 0, givnum, 0, c, 0, s, 0, work, 0, iwork, 0, info);
/* 5978:     */   }
/* 5979:     */   
/* 5980:     */   public void slasda(int icompq, int smlsiz, int n, int sqre, float[] d, int _d_offset, float[] e, int _e_offset, float[] u, int _u_offset, int ldu, float[] vt, int _vt_offset, int[] k, int _k_offset, float[] difl, int _difl_offset, float[] difr, int _difr_offset, float[] z, int _z_offset, float[] poles, int _poles_offset, int[] givptr, int _givptr_offset, int[] givcol, int _givcol_offset, int ldgcol, int[] perm, int _perm_offset, float[] givnum, int _givnum_offset, float[] c, int _c_offset, float[] s, int _s_offset, float[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 5981:     */   {
/* 5982:5286 */     Slasda.slasda(icompq, smlsiz, n, sqre, d, _d_offset, e, _e_offset, u, _u_offset, ldu, vt, _vt_offset, k, _k_offset, difl, _difl_offset, difr, _difr_offset, z, _z_offset, poles, _poles_offset, givptr, _givptr_offset, givcol, _givcol_offset, ldgcol, perm, _perm_offset, givnum, _givnum_offset, c, _c_offset, s, _s_offset, work, _work_offset, iwork, _iwork_offset, info);
/* 5983:     */   }
/* 5984:     */   
/* 5985:     */   public void slasdq(String uplo, int sqre, int n, int ncvt, int nru, int ncc, float[] d, float[] e, float[] vt, int ldvt, float[] u, int ldu, float[] c, int Ldc, float[] work, intW info)
/* 5986:     */   {
/* 5987:5291 */     Slasdq.slasdq(uplo, sqre, n, ncvt, nru, ncc, d, 0, e, 0, vt, 0, ldvt, u, 0, ldu, c, 0, Ldc, work, 0, info);
/* 5988:     */   }
/* 5989:     */   
/* 5990:     */   public void slasdq(String uplo, int sqre, int n, int ncvt, int nru, int ncc, float[] d, int _d_offset, float[] e, int _e_offset, float[] vt, int _vt_offset, int ldvt, float[] u, int _u_offset, int ldu, float[] c, int _c_offset, int Ldc, float[] work, int _work_offset, intW info)
/* 5991:     */   {
/* 5992:5296 */     Slasdq.slasdq(uplo, sqre, n, ncvt, nru, ncc, d, _d_offset, e, _e_offset, vt, _vt_offset, ldvt, u, _u_offset, ldu, c, _c_offset, Ldc, work, _work_offset, info);
/* 5993:     */   }
/* 5994:     */   
/* 5995:     */   public void slasdt(int n, intW lvl, intW nd, int[] inode, int[] ndiml, int[] ndimr, int msub)
/* 5996:     */   {
/* 5997:5301 */     Slasdt.slasdt(n, lvl, nd, inode, 0, ndiml, 0, ndimr, 0, msub);
/* 5998:     */   }
/* 5999:     */   
/* 6000:     */   public void slasdt(int n, intW lvl, intW nd, int[] inode, int _inode_offset, int[] ndiml, int _ndiml_offset, int[] ndimr, int _ndimr_offset, int msub)
/* 6001:     */   {
/* 6002:5306 */     Slasdt.slasdt(n, lvl, nd, inode, _inode_offset, ndiml, _ndiml_offset, ndimr, _ndimr_offset, msub);
/* 6003:     */   }
/* 6004:     */   
/* 6005:     */   public void slaset(String uplo, int m, int n, float alpha, float beta, float[] a, int lda)
/* 6006:     */   {
/* 6007:5311 */     Slaset.slaset(uplo, m, n, alpha, beta, a, 0, lda);
/* 6008:     */   }
/* 6009:     */   
/* 6010:     */   public void slaset(String uplo, int m, int n, float alpha, float beta, float[] a, int _a_offset, int lda)
/* 6011:     */   {
/* 6012:5316 */     Slaset.slaset(uplo, m, n, alpha, beta, a, _a_offset, lda);
/* 6013:     */   }
/* 6014:     */   
/* 6015:     */   public void slasq1(int n, float[] d, float[] e, float[] work, intW info)
/* 6016:     */   {
/* 6017:5321 */     Slasq1.slasq1(n, d, 0, e, 0, work, 0, info);
/* 6018:     */   }
/* 6019:     */   
/* 6020:     */   public void slasq1(int n, float[] d, int _d_offset, float[] e, int _e_offset, float[] work, int _work_offset, intW info)
/* 6021:     */   {
/* 6022:5326 */     Slasq1.slasq1(n, d, _d_offset, e, _e_offset, work, _work_offset, info);
/* 6023:     */   }
/* 6024:     */   
/* 6025:     */   public void slasq2(int n, float[] z, intW info)
/* 6026:     */   {
/* 6027:5331 */     Slasq2.slasq2(n, z, 0, info);
/* 6028:     */   }
/* 6029:     */   
/* 6030:     */   public void slasq2(int n, float[] z, int _z_offset, intW info)
/* 6031:     */   {
/* 6032:5336 */     Slasq2.slasq2(n, z, _z_offset, info);
/* 6033:     */   }
/* 6034:     */   
/* 6035:     */   public void slasq3(int i0, intW n0, float[] z, int pp, floatW dmin, floatW sigma, floatW desig, floatW qmax, intW nfail, intW iter, intW ndiv, boolean ieee)
/* 6036:     */   {
/* 6037:5341 */     Slasq3.slasq3(i0, n0, z, 0, pp, dmin, sigma, desig, qmax, nfail, iter, ndiv, ieee);
/* 6038:     */   }
/* 6039:     */   
/* 6040:     */   public void slasq3(int i0, intW n0, float[] z, int _z_offset, int pp, floatW dmin, floatW sigma, floatW desig, floatW qmax, intW nfail, intW iter, intW ndiv, boolean ieee)
/* 6041:     */   {
/* 6042:5346 */     Slasq3.slasq3(i0, n0, z, _z_offset, pp, dmin, sigma, desig, qmax, nfail, iter, ndiv, ieee);
/* 6043:     */   }
/* 6044:     */   
/* 6045:     */   public void slasq4(int i0, int n0, float[] z, int pp, int n0in, float dmin, float dmin1, float dmin2, float dn, float dn1, float dn2, floatW tau, intW ttype)
/* 6046:     */   {
/* 6047:5351 */     Slasq4.slasq4(i0, n0, z, 0, pp, n0in, dmin, dmin1, dmin2, dn, dn1, dn2, tau, ttype);
/* 6048:     */   }
/* 6049:     */   
/* 6050:     */   public void slasq4(int i0, int n0, float[] z, int _z_offset, int pp, int n0in, float dmin, float dmin1, float dmin2, float dn, float dn1, float dn2, floatW tau, intW ttype)
/* 6051:     */   {
/* 6052:5356 */     Slasq4.slasq4(i0, n0, z, _z_offset, pp, n0in, dmin, dmin1, dmin2, dn, dn1, dn2, tau, ttype);
/* 6053:     */   }
/* 6054:     */   
/* 6055:     */   public void slasq5(int i0, int n0, float[] z, int pp, float tau, floatW dmin, floatW dmin1, floatW dmin2, floatW dn, floatW dnm1, floatW dnm2, boolean ieee)
/* 6056:     */   {
/* 6057:5361 */     Slasq5.slasq5(i0, n0, z, 0, pp, tau, dmin, dmin1, dmin2, dn, dnm1, dnm2, ieee);
/* 6058:     */   }
/* 6059:     */   
/* 6060:     */   public void slasq5(int i0, int n0, float[] z, int _z_offset, int pp, float tau, floatW dmin, floatW dmin1, floatW dmin2, floatW dn, floatW dnm1, floatW dnm2, boolean ieee)
/* 6061:     */   {
/* 6062:5366 */     Slasq5.slasq5(i0, n0, z, _z_offset, pp, tau, dmin, dmin1, dmin2, dn, dnm1, dnm2, ieee);
/* 6063:     */   }
/* 6064:     */   
/* 6065:     */   public void slasq6(int i0, int n0, float[] z, int pp, floatW dmin, floatW dmin1, floatW dmin2, floatW dn, floatW dnm1, floatW dnm2)
/* 6066:     */   {
/* 6067:5371 */     Slasq6.slasq6(i0, n0, z, 0, pp, dmin, dmin1, dmin2, dn, dnm1, dnm2);
/* 6068:     */   }
/* 6069:     */   
/* 6070:     */   public void slasq6(int i0, int n0, float[] z, int _z_offset, int pp, floatW dmin, floatW dmin1, floatW dmin2, floatW dn, floatW dnm1, floatW dnm2)
/* 6071:     */   {
/* 6072:5376 */     Slasq6.slasq6(i0, n0, z, _z_offset, pp, dmin, dmin1, dmin2, dn, dnm1, dnm2);
/* 6073:     */   }
/* 6074:     */   
/* 6075:     */   public void slasr(String side, String pivot, String direct, int m, int n, float[] c, float[] s, float[] a, int lda)
/* 6076:     */   {
/* 6077:5381 */     Slasr.slasr(side, pivot, direct, m, n, c, 0, s, 0, a, 0, lda);
/* 6078:     */   }
/* 6079:     */   
/* 6080:     */   public void slasr(String side, String pivot, String direct, int m, int n, float[] c, int _c_offset, float[] s, int _s_offset, float[] a, int _a_offset, int lda)
/* 6081:     */   {
/* 6082:5386 */     Slasr.slasr(side, pivot, direct, m, n, c, _c_offset, s, _s_offset, a, _a_offset, lda);
/* 6083:     */   }
/* 6084:     */   
/* 6085:     */   public void slasrt(String id, int n, float[] d, intW info)
/* 6086:     */   {
/* 6087:5391 */     Slasrt.slasrt(id, n, d, 0, info);
/* 6088:     */   }
/* 6089:     */   
/* 6090:     */   public void slasrt(String id, int n, float[] d, int _d_offset, intW info)
/* 6091:     */   {
/* 6092:5396 */     Slasrt.slasrt(id, n, d, _d_offset, info);
/* 6093:     */   }
/* 6094:     */   
/* 6095:     */   public void slassq(int n, float[] x, int incx, floatW scale, floatW sumsq)
/* 6096:     */   {
/* 6097:5401 */     Slassq.slassq(n, x, 0, incx, scale, sumsq);
/* 6098:     */   }
/* 6099:     */   
/* 6100:     */   public void slassq(int n, float[] x, int _x_offset, int incx, floatW scale, floatW sumsq)
/* 6101:     */   {
/* 6102:5406 */     Slassq.slassq(n, x, _x_offset, incx, scale, sumsq);
/* 6103:     */   }
/* 6104:     */   
/* 6105:     */   public void slasv2(float f, float g, float h, floatW ssmin, floatW ssmax, floatW snr, floatW csr, floatW snl, floatW csl)
/* 6106:     */   {
/* 6107:5411 */     Slasv2.slasv2(f, g, h, ssmin, ssmax, snr, csr, snl, csl);
/* 6108:     */   }
/* 6109:     */   
/* 6110:     */   public void slaswp(int n, float[] a, int lda, int k1, int k2, int[] ipiv, int incx)
/* 6111:     */   {
/* 6112:5416 */     Slaswp.slaswp(n, a, 0, lda, k1, k2, ipiv, 0, incx);
/* 6113:     */   }
/* 6114:     */   
/* 6115:     */   public void slaswp(int n, float[] a, int _a_offset, int lda, int k1, int k2, int[] ipiv, int _ipiv_offset, int incx)
/* 6116:     */   {
/* 6117:5421 */     Slaswp.slaswp(n, a, _a_offset, lda, k1, k2, ipiv, _ipiv_offset, incx);
/* 6118:     */   }
/* 6119:     */   
/* 6120:     */   public void slasy2(boolean ltranl, boolean ltranr, int isgn, int n1, int n2, float[] tl, int ldtl, float[] tr, int ldtr, float[] b, int ldb, floatW scale, float[] x, int ldx, floatW xnorm, intW info)
/* 6121:     */   {
/* 6122:5426 */     Slasy2.slasy2(ltranl, ltranr, isgn, n1, n2, tl, 0, ldtl, tr, 0, ldtr, b, 0, ldb, scale, x, 0, ldx, xnorm, info);
/* 6123:     */   }
/* 6124:     */   
/* 6125:     */   public void slasy2(boolean ltranl, boolean ltranr, int isgn, int n1, int n2, float[] tl, int _tl_offset, int ldtl, float[] tr, int _tr_offset, int ldtr, float[] b, int _b_offset, int ldb, floatW scale, float[] x, int _x_offset, int ldx, floatW xnorm, intW info)
/* 6126:     */   {
/* 6127:5431 */     Slasy2.slasy2(ltranl, ltranr, isgn, n1, n2, tl, _tl_offset, ldtl, tr, _tr_offset, ldtr, b, _b_offset, ldb, scale, x, _x_offset, ldx, xnorm, info);
/* 6128:     */   }
/* 6129:     */   
/* 6130:     */   public void slasyf(String uplo, int n, int nb, intW kb, float[] a, int lda, int[] ipiv, float[] w, int ldw, intW info)
/* 6131:     */   {
/* 6132:5436 */     Slasyf.slasyf(uplo, n, nb, kb, a, 0, lda, ipiv, 0, w, 0, ldw, info);
/* 6133:     */   }
/* 6134:     */   
/* 6135:     */   public void slasyf(String uplo, int n, int nb, intW kb, float[] a, int _a_offset, int lda, int[] ipiv, int _ipiv_offset, float[] w, int _w_offset, int ldw, intW info)
/* 6136:     */   {
/* 6137:5441 */     Slasyf.slasyf(uplo, n, nb, kb, a, _a_offset, lda, ipiv, _ipiv_offset, w, _w_offset, ldw, info);
/* 6138:     */   }
/* 6139:     */   
/* 6140:     */   public void slatbs(String uplo, String trans, String diag, String normin, int n, int kd, float[] ab, int ldab, float[] x, floatW scale, float[] cnorm, intW info)
/* 6141:     */   {
/* 6142:5446 */     Slatbs.slatbs(uplo, trans, diag, normin, n, kd, ab, 0, ldab, x, 0, scale, cnorm, 0, info);
/* 6143:     */   }
/* 6144:     */   
/* 6145:     */   public void slatbs(String uplo, String trans, String diag, String normin, int n, int kd, float[] ab, int _ab_offset, int ldab, float[] x, int _x_offset, floatW scale, float[] cnorm, int _cnorm_offset, intW info)
/* 6146:     */   {
/* 6147:5451 */     Slatbs.slatbs(uplo, trans, diag, normin, n, kd, ab, _ab_offset, ldab, x, _x_offset, scale, cnorm, _cnorm_offset, info);
/* 6148:     */   }
/* 6149:     */   
/* 6150:     */   public void slatdf(int ijob, int n, float[] z, int ldz, float[] rhs, floatW rdsum, floatW rdscal, int[] ipiv, int[] jpiv)
/* 6151:     */   {
/* 6152:5456 */     Slatdf.slatdf(ijob, n, z, 0, ldz, rhs, 0, rdsum, rdscal, ipiv, 0, jpiv, 0);
/* 6153:     */   }
/* 6154:     */   
/* 6155:     */   public void slatdf(int ijob, int n, float[] z, int _z_offset, int ldz, float[] rhs, int _rhs_offset, floatW rdsum, floatW rdscal, int[] ipiv, int _ipiv_offset, int[] jpiv, int _jpiv_offset)
/* 6156:     */   {
/* 6157:5461 */     Slatdf.slatdf(ijob, n, z, _z_offset, ldz, rhs, _rhs_offset, rdsum, rdscal, ipiv, _ipiv_offset, jpiv, _jpiv_offset);
/* 6158:     */   }
/* 6159:     */   
/* 6160:     */   public void slatps(String uplo, String trans, String diag, String normin, int n, float[] ap, float[] x, floatW scale, float[] cnorm, intW info)
/* 6161:     */   {
/* 6162:5466 */     Slatps.slatps(uplo, trans, diag, normin, n, ap, 0, x, 0, scale, cnorm, 0, info);
/* 6163:     */   }
/* 6164:     */   
/* 6165:     */   public void slatps(String uplo, String trans, String diag, String normin, int n, float[] ap, int _ap_offset, float[] x, int _x_offset, floatW scale, float[] cnorm, int _cnorm_offset, intW info)
/* 6166:     */   {
/* 6167:5471 */     Slatps.slatps(uplo, trans, diag, normin, n, ap, _ap_offset, x, _x_offset, scale, cnorm, _cnorm_offset, info);
/* 6168:     */   }
/* 6169:     */   
/* 6170:     */   public void slatrd(String uplo, int n, int nb, float[] a, int lda, float[] e, float[] tau, float[] w, int ldw)
/* 6171:     */   {
/* 6172:5476 */     Slatrd.slatrd(uplo, n, nb, a, 0, lda, e, 0, tau, 0, w, 0, ldw);
/* 6173:     */   }
/* 6174:     */   
/* 6175:     */   public void slatrd(String uplo, int n, int nb, float[] a, int _a_offset, int lda, float[] e, int _e_offset, float[] tau, int _tau_offset, float[] w, int _w_offset, int ldw)
/* 6176:     */   {
/* 6177:5481 */     Slatrd.slatrd(uplo, n, nb, a, _a_offset, lda, e, _e_offset, tau, _tau_offset, w, _w_offset, ldw);
/* 6178:     */   }
/* 6179:     */   
/* 6180:     */   public void slatrs(String uplo, String trans, String diag, String normin, int n, float[] a, int lda, float[] x, floatW scale, float[] cnorm, intW info)
/* 6181:     */   {
/* 6182:5486 */     Slatrs.slatrs(uplo, trans, diag, normin, n, a, 0, lda, x, 0, scale, cnorm, 0, info);
/* 6183:     */   }
/* 6184:     */   
/* 6185:     */   public void slatrs(String uplo, String trans, String diag, String normin, int n, float[] a, int _a_offset, int lda, float[] x, int _x_offset, floatW scale, float[] cnorm, int _cnorm_offset, intW info)
/* 6186:     */   {
/* 6187:5491 */     Slatrs.slatrs(uplo, trans, diag, normin, n, a, _a_offset, lda, x, _x_offset, scale, cnorm, _cnorm_offset, info);
/* 6188:     */   }
/* 6189:     */   
/* 6190:     */   public void slatrz(int m, int n, int l, float[] a, int lda, float[] tau, float[] work)
/* 6191:     */   {
/* 6192:5496 */     Slatrz.slatrz(m, n, l, a, 0, lda, tau, 0, work, 0);
/* 6193:     */   }
/* 6194:     */   
/* 6195:     */   public void slatrz(int m, int n, int l, float[] a, int _a_offset, int lda, float[] tau, int _tau_offset, float[] work, int _work_offset)
/* 6196:     */   {
/* 6197:5501 */     Slatrz.slatrz(m, n, l, a, _a_offset, lda, tau, _tau_offset, work, _work_offset);
/* 6198:     */   }
/* 6199:     */   
/* 6200:     */   public void slatzm(String side, int m, int n, float[] v, int incv, float tau, float[] c1, float[] c2, int Ldc, float[] work)
/* 6201:     */   {
/* 6202:5506 */     Slatzm.slatzm(side, m, n, v, 0, incv, tau, c1, 0, c2, 0, Ldc, work, 0);
/* 6203:     */   }
/* 6204:     */   
/* 6205:     */   public void slatzm(String side, int m, int n, float[] v, int _v_offset, int incv, float tau, float[] c1, int _c1_offset, float[] c2, int _c2_offset, int Ldc, float[] work, int _work_offset)
/* 6206:     */   {
/* 6207:5511 */     Slatzm.slatzm(side, m, n, v, _v_offset, incv, tau, c1, _c1_offset, c2, _c2_offset, Ldc, work, _work_offset);
/* 6208:     */   }
/* 6209:     */   
/* 6210:     */   public void slauu2(String uplo, int n, float[] a, int lda, intW info)
/* 6211:     */   {
/* 6212:5516 */     Slauu2.slauu2(uplo, n, a, 0, lda, info);
/* 6213:     */   }
/* 6214:     */   
/* 6215:     */   public void slauu2(String uplo, int n, float[] a, int _a_offset, int lda, intW info)
/* 6216:     */   {
/* 6217:5521 */     Slauu2.slauu2(uplo, n, a, _a_offset, lda, info);
/* 6218:     */   }
/* 6219:     */   
/* 6220:     */   public void slauum(String uplo, int n, float[] a, int lda, intW info)
/* 6221:     */   {
/* 6222:5526 */     Slauum.slauum(uplo, n, a, 0, lda, info);
/* 6223:     */   }
/* 6224:     */   
/* 6225:     */   public void slauum(String uplo, int n, float[] a, int _a_offset, int lda, intW info)
/* 6226:     */   {
/* 6227:5531 */     Slauum.slauum(uplo, n, a, _a_offset, lda, info);
/* 6228:     */   }
/* 6229:     */   
/* 6230:     */   public void slazq3(int i0, intW n0, float[] z, int pp, floatW dmin, floatW sigma, floatW desig, floatW qmax, intW nfail, intW iter, intW ndiv, boolean ieee, intW ttype, floatW dmin1, floatW dmin2, floatW dn, floatW dn1, floatW dn2, floatW tau)
/* 6231:     */   {
/* 6232:5536 */     Slazq3.slazq3(i0, n0, z, 0, pp, dmin, sigma, desig, qmax, nfail, iter, ndiv, ieee, ttype, dmin1, dmin2, dn, dn1, dn2, tau);
/* 6233:     */   }
/* 6234:     */   
/* 6235:     */   public void slazq3(int i0, intW n0, float[] z, int _z_offset, int pp, floatW dmin, floatW sigma, floatW desig, floatW qmax, intW nfail, intW iter, intW ndiv, boolean ieee, intW ttype, floatW dmin1, floatW dmin2, floatW dn, floatW dn1, floatW dn2, floatW tau)
/* 6236:     */   {
/* 6237:5541 */     Slazq3.slazq3(i0, n0, z, _z_offset, pp, dmin, sigma, desig, qmax, nfail, iter, ndiv, ieee, ttype, dmin1, dmin2, dn, dn1, dn2, tau);
/* 6238:     */   }
/* 6239:     */   
/* 6240:     */   public void slazq4(int i0, int n0, float[] z, int pp, int n0in, float dmin, float dmin1, float dmin2, float dn, float dn1, float dn2, floatW tau, intW ttype, floatW g)
/* 6241:     */   {
/* 6242:5546 */     Slazq4.slazq4(i0, n0, z, 0, pp, n0in, dmin, dmin1, dmin2, dn, dn1, dn2, tau, ttype, g);
/* 6243:     */   }
/* 6244:     */   
/* 6245:     */   public void slazq4(int i0, int n0, float[] z, int _z_offset, int pp, int n0in, float dmin, float dmin1, float dmin2, float dn, float dn1, float dn2, floatW tau, intW ttype, floatW g)
/* 6246:     */   {
/* 6247:5551 */     Slazq4.slazq4(i0, n0, z, _z_offset, pp, n0in, dmin, dmin1, dmin2, dn, dn1, dn2, tau, ttype, g);
/* 6248:     */   }
/* 6249:     */   
/* 6250:     */   public void sopgtr(String uplo, int n, float[] ap, float[] tau, float[] q, int ldq, float[] work, intW info)
/* 6251:     */   {
/* 6252:5556 */     Sopgtr.sopgtr(uplo, n, ap, 0, tau, 0, q, 0, ldq, work, 0, info);
/* 6253:     */   }
/* 6254:     */   
/* 6255:     */   public void sopgtr(String uplo, int n, float[] ap, int _ap_offset, float[] tau, int _tau_offset, float[] q, int _q_offset, int ldq, float[] work, int _work_offset, intW info)
/* 6256:     */   {
/* 6257:5561 */     Sopgtr.sopgtr(uplo, n, ap, _ap_offset, tau, _tau_offset, q, _q_offset, ldq, work, _work_offset, info);
/* 6258:     */   }
/* 6259:     */   
/* 6260:     */   public void sopmtr(String side, String uplo, String trans, int m, int n, float[] ap, float[] tau, float[] c, int Ldc, float[] work, intW info)
/* 6261:     */   {
/* 6262:5566 */     Sopmtr.sopmtr(side, uplo, trans, m, n, ap, 0, tau, 0, c, 0, Ldc, work, 0, info);
/* 6263:     */   }
/* 6264:     */   
/* 6265:     */   public void sopmtr(String side, String uplo, String trans, int m, int n, float[] ap, int _ap_offset, float[] tau, int _tau_offset, float[] c, int _c_offset, int Ldc, float[] work, int _work_offset, intW info)
/* 6266:     */   {
/* 6267:5571 */     Sopmtr.sopmtr(side, uplo, trans, m, n, ap, _ap_offset, tau, _tau_offset, c, _c_offset, Ldc, work, _work_offset, info);
/* 6268:     */   }
/* 6269:     */   
/* 6270:     */   public void sorg2l(int m, int n, int k, float[] a, int lda, float[] tau, float[] work, intW info)
/* 6271:     */   {
/* 6272:5576 */     Sorg2l.sorg2l(m, n, k, a, 0, lda, tau, 0, work, 0, info);
/* 6273:     */   }
/* 6274:     */   
/* 6275:     */   public void sorg2l(int m, int n, int k, float[] a, int _a_offset, int lda, float[] tau, int _tau_offset, float[] work, int _work_offset, intW info)
/* 6276:     */   {
/* 6277:5581 */     Sorg2l.sorg2l(m, n, k, a, _a_offset, lda, tau, _tau_offset, work, _work_offset, info);
/* 6278:     */   }
/* 6279:     */   
/* 6280:     */   public void sorg2r(int m, int n, int k, float[] a, int lda, float[] tau, float[] work, intW info)
/* 6281:     */   {
/* 6282:5586 */     Sorg2r.sorg2r(m, n, k, a, 0, lda, tau, 0, work, 0, info);
/* 6283:     */   }
/* 6284:     */   
/* 6285:     */   public void sorg2r(int m, int n, int k, float[] a, int _a_offset, int lda, float[] tau, int _tau_offset, float[] work, int _work_offset, intW info)
/* 6286:     */   {
/* 6287:5591 */     Sorg2r.sorg2r(m, n, k, a, _a_offset, lda, tau, _tau_offset, work, _work_offset, info);
/* 6288:     */   }
/* 6289:     */   
/* 6290:     */   public void sorgbr(String vect, int m, int n, int k, float[] a, int lda, float[] tau, float[] work, int lwork, intW info)
/* 6291:     */   {
/* 6292:5596 */     Sorgbr.sorgbr(vect, m, n, k, a, 0, lda, tau, 0, work, 0, lwork, info);
/* 6293:     */   }
/* 6294:     */   
/* 6295:     */   public void sorgbr(String vect, int m, int n, int k, float[] a, int _a_offset, int lda, float[] tau, int _tau_offset, float[] work, int _work_offset, int lwork, intW info)
/* 6296:     */   {
/* 6297:5601 */     Sorgbr.sorgbr(vect, m, n, k, a, _a_offset, lda, tau, _tau_offset, work, _work_offset, lwork, info);
/* 6298:     */   }
/* 6299:     */   
/* 6300:     */   public void sorghr(int n, int ilo, int ihi, float[] a, int lda, float[] tau, float[] work, int lwork, intW info)
/* 6301:     */   {
/* 6302:5606 */     Sorghr.sorghr(n, ilo, ihi, a, 0, lda, tau, 0, work, 0, lwork, info);
/* 6303:     */   }
/* 6304:     */   
/* 6305:     */   public void sorghr(int n, int ilo, int ihi, float[] a, int _a_offset, int lda, float[] tau, int _tau_offset, float[] work, int _work_offset, int lwork, intW info)
/* 6306:     */   {
/* 6307:5611 */     Sorghr.sorghr(n, ilo, ihi, a, _a_offset, lda, tau, _tau_offset, work, _work_offset, lwork, info);
/* 6308:     */   }
/* 6309:     */   
/* 6310:     */   public void sorgl2(int m, int n, int k, float[] a, int lda, float[] tau, float[] work, intW info)
/* 6311:     */   {
/* 6312:5616 */     Sorgl2.sorgl2(m, n, k, a, 0, lda, tau, 0, work, 0, info);
/* 6313:     */   }
/* 6314:     */   
/* 6315:     */   public void sorgl2(int m, int n, int k, float[] a, int _a_offset, int lda, float[] tau, int _tau_offset, float[] work, int _work_offset, intW info)
/* 6316:     */   {
/* 6317:5621 */     Sorgl2.sorgl2(m, n, k, a, _a_offset, lda, tau, _tau_offset, work, _work_offset, info);
/* 6318:     */   }
/* 6319:     */   
/* 6320:     */   public void sorglq(int m, int n, int k, float[] a, int lda, float[] tau, float[] work, int lwork, intW info)
/* 6321:     */   {
/* 6322:5626 */     Sorglq.sorglq(m, n, k, a, 0, lda, tau, 0, work, 0, lwork, info);
/* 6323:     */   }
/* 6324:     */   
/* 6325:     */   public void sorglq(int m, int n, int k, float[] a, int _a_offset, int lda, float[] tau, int _tau_offset, float[] work, int _work_offset, int lwork, intW info)
/* 6326:     */   {
/* 6327:5631 */     Sorglq.sorglq(m, n, k, a, _a_offset, lda, tau, _tau_offset, work, _work_offset, lwork, info);
/* 6328:     */   }
/* 6329:     */   
/* 6330:     */   public void sorgql(int m, int n, int k, float[] a, int lda, float[] tau, float[] work, int lwork, intW info)
/* 6331:     */   {
/* 6332:5636 */     Sorgql.sorgql(m, n, k, a, 0, lda, tau, 0, work, 0, lwork, info);
/* 6333:     */   }
/* 6334:     */   
/* 6335:     */   public void sorgql(int m, int n, int k, float[] a, int _a_offset, int lda, float[] tau, int _tau_offset, float[] work, int _work_offset, int lwork, intW info)
/* 6336:     */   {
/* 6337:5641 */     Sorgql.sorgql(m, n, k, a, _a_offset, lda, tau, _tau_offset, work, _work_offset, lwork, info);
/* 6338:     */   }
/* 6339:     */   
/* 6340:     */   public void sorgqr(int m, int n, int k, float[] a, int lda, float[] tau, float[] work, int lwork, intW info)
/* 6341:     */   {
/* 6342:5646 */     Sorgqr.sorgqr(m, n, k, a, 0, lda, tau, 0, work, 0, lwork, info);
/* 6343:     */   }
/* 6344:     */   
/* 6345:     */   public void sorgqr(int m, int n, int k, float[] a, int _a_offset, int lda, float[] tau, int _tau_offset, float[] work, int _work_offset, int lwork, intW info)
/* 6346:     */   {
/* 6347:5651 */     Sorgqr.sorgqr(m, n, k, a, _a_offset, lda, tau, _tau_offset, work, _work_offset, lwork, info);
/* 6348:     */   }
/* 6349:     */   
/* 6350:     */   public void sorgr2(int m, int n, int k, float[] a, int lda, float[] tau, float[] work, intW info)
/* 6351:     */   {
/* 6352:5656 */     Sorgr2.sorgr2(m, n, k, a, 0, lda, tau, 0, work, 0, info);
/* 6353:     */   }
/* 6354:     */   
/* 6355:     */   public void sorgr2(int m, int n, int k, float[] a, int _a_offset, int lda, float[] tau, int _tau_offset, float[] work, int _work_offset, intW info)
/* 6356:     */   {
/* 6357:5661 */     Sorgr2.sorgr2(m, n, k, a, _a_offset, lda, tau, _tau_offset, work, _work_offset, info);
/* 6358:     */   }
/* 6359:     */   
/* 6360:     */   public void sorgrq(int m, int n, int k, float[] a, int lda, float[] tau, float[] work, int lwork, intW info)
/* 6361:     */   {
/* 6362:5666 */     Sorgrq.sorgrq(m, n, k, a, 0, lda, tau, 0, work, 0, lwork, info);
/* 6363:     */   }
/* 6364:     */   
/* 6365:     */   public void sorgrq(int m, int n, int k, float[] a, int _a_offset, int lda, float[] tau, int _tau_offset, float[] work, int _work_offset, int lwork, intW info)
/* 6366:     */   {
/* 6367:5671 */     Sorgrq.sorgrq(m, n, k, a, _a_offset, lda, tau, _tau_offset, work, _work_offset, lwork, info);
/* 6368:     */   }
/* 6369:     */   
/* 6370:     */   public void sorgtr(String uplo, int n, float[] a, int lda, float[] tau, float[] work, int lwork, intW info)
/* 6371:     */   {
/* 6372:5676 */     Sorgtr.sorgtr(uplo, n, a, 0, lda, tau, 0, work, 0, lwork, info);
/* 6373:     */   }
/* 6374:     */   
/* 6375:     */   public void sorgtr(String uplo, int n, float[] a, int _a_offset, int lda, float[] tau, int _tau_offset, float[] work, int _work_offset, int lwork, intW info)
/* 6376:     */   {
/* 6377:5681 */     Sorgtr.sorgtr(uplo, n, a, _a_offset, lda, tau, _tau_offset, work, _work_offset, lwork, info);
/* 6378:     */   }
/* 6379:     */   
/* 6380:     */   public void sorm2l(String side, String trans, int m, int n, int k, float[] a, int lda, float[] tau, float[] c, int Ldc, float[] work, intW info)
/* 6381:     */   {
/* 6382:5686 */     Sorm2l.sorm2l(side, trans, m, n, k, a, 0, lda, tau, 0, c, 0, Ldc, work, 0, info);
/* 6383:     */   }
/* 6384:     */   
/* 6385:     */   public void sorm2l(String side, String trans, int m, int n, int k, float[] a, int _a_offset, int lda, float[] tau, int _tau_offset, float[] c, int _c_offset, int Ldc, float[] work, int _work_offset, intW info)
/* 6386:     */   {
/* 6387:5691 */     Sorm2l.sorm2l(side, trans, m, n, k, a, _a_offset, lda, tau, _tau_offset, c, _c_offset, Ldc, work, _work_offset, info);
/* 6388:     */   }
/* 6389:     */   
/* 6390:     */   public void sorm2r(String side, String trans, int m, int n, int k, float[] a, int lda, float[] tau, float[] c, int Ldc, float[] work, intW info)
/* 6391:     */   {
/* 6392:5696 */     Sorm2r.sorm2r(side, trans, m, n, k, a, 0, lda, tau, 0, c, 0, Ldc, work, 0, info);
/* 6393:     */   }
/* 6394:     */   
/* 6395:     */   public void sorm2r(String side, String trans, int m, int n, int k, float[] a, int _a_offset, int lda, float[] tau, int _tau_offset, float[] c, int _c_offset, int Ldc, float[] work, int _work_offset, intW info)
/* 6396:     */   {
/* 6397:5701 */     Sorm2r.sorm2r(side, trans, m, n, k, a, _a_offset, lda, tau, _tau_offset, c, _c_offset, Ldc, work, _work_offset, info);
/* 6398:     */   }
/* 6399:     */   
/* 6400:     */   public void sormbr(String vect, String side, String trans, int m, int n, int k, float[] a, int lda, float[] tau, float[] c, int Ldc, float[] work, int lwork, intW info)
/* 6401:     */   {
/* 6402:5706 */     Sormbr.sormbr(vect, side, trans, m, n, k, a, 0, lda, tau, 0, c, 0, Ldc, work, 0, lwork, info);
/* 6403:     */   }
/* 6404:     */   
/* 6405:     */   public void sormbr(String vect, String side, String trans, int m, int n, int k, float[] a, int _a_offset, int lda, float[] tau, int _tau_offset, float[] c, int _c_offset, int Ldc, float[] work, int _work_offset, int lwork, intW info)
/* 6406:     */   {
/* 6407:5711 */     Sormbr.sormbr(vect, side, trans, m, n, k, a, _a_offset, lda, tau, _tau_offset, c, _c_offset, Ldc, work, _work_offset, lwork, info);
/* 6408:     */   }
/* 6409:     */   
/* 6410:     */   public void sormhr(String side, String trans, int m, int n, int ilo, int ihi, float[] a, int lda, float[] tau, float[] c, int Ldc, float[] work, int lwork, intW info)
/* 6411:     */   {
/* 6412:5716 */     Sormhr.sormhr(side, trans, m, n, ilo, ihi, a, 0, lda, tau, 0, c, 0, Ldc, work, 0, lwork, info);
/* 6413:     */   }
/* 6414:     */   
/* 6415:     */   public void sormhr(String side, String trans, int m, int n, int ilo, int ihi, float[] a, int _a_offset, int lda, float[] tau, int _tau_offset, float[] c, int _c_offset, int Ldc, float[] work, int _work_offset, int lwork, intW info)
/* 6416:     */   {
/* 6417:5721 */     Sormhr.sormhr(side, trans, m, n, ilo, ihi, a, _a_offset, lda, tau, _tau_offset, c, _c_offset, Ldc, work, _work_offset, lwork, info);
/* 6418:     */   }
/* 6419:     */   
/* 6420:     */   public void sorml2(String side, String trans, int m, int n, int k, float[] a, int lda, float[] tau, float[] c, int Ldc, float[] work, intW info)
/* 6421:     */   {
/* 6422:5726 */     Sorml2.sorml2(side, trans, m, n, k, a, 0, lda, tau, 0, c, 0, Ldc, work, 0, info);
/* 6423:     */   }
/* 6424:     */   
/* 6425:     */   public void sorml2(String side, String trans, int m, int n, int k, float[] a, int _a_offset, int lda, float[] tau, int _tau_offset, float[] c, int _c_offset, int Ldc, float[] work, int _work_offset, intW info)
/* 6426:     */   {
/* 6427:5731 */     Sorml2.sorml2(side, trans, m, n, k, a, _a_offset, lda, tau, _tau_offset, c, _c_offset, Ldc, work, _work_offset, info);
/* 6428:     */   }
/* 6429:     */   
/* 6430:     */   public void sormlq(String side, String trans, int m, int n, int k, float[] a, int lda, float[] tau, float[] c, int Ldc, float[] work, int lwork, intW info)
/* 6431:     */   {
/* 6432:5736 */     Sormlq.sormlq(side, trans, m, n, k, a, 0, lda, tau, 0, c, 0, Ldc, work, 0, lwork, info);
/* 6433:     */   }
/* 6434:     */   
/* 6435:     */   public void sormlq(String side, String trans, int m, int n, int k, float[] a, int _a_offset, int lda, float[] tau, int _tau_offset, float[] c, int _c_offset, int Ldc, float[] work, int _work_offset, int lwork, intW info)
/* 6436:     */   {
/* 6437:5741 */     Sormlq.sormlq(side, trans, m, n, k, a, _a_offset, lda, tau, _tau_offset, c, _c_offset, Ldc, work, _work_offset, lwork, info);
/* 6438:     */   }
/* 6439:     */   
/* 6440:     */   public void sormql(String side, String trans, int m, int n, int k, float[] a, int lda, float[] tau, float[] c, int Ldc, float[] work, int lwork, intW info)
/* 6441:     */   {
/* 6442:5746 */     Sormql.sormql(side, trans, m, n, k, a, 0, lda, tau, 0, c, 0, Ldc, work, 0, lwork, info);
/* 6443:     */   }
/* 6444:     */   
/* 6445:     */   public void sormql(String side, String trans, int m, int n, int k, float[] a, int _a_offset, int lda, float[] tau, int _tau_offset, float[] c, int _c_offset, int Ldc, float[] work, int _work_offset, int lwork, intW info)
/* 6446:     */   {
/* 6447:5751 */     Sormql.sormql(side, trans, m, n, k, a, _a_offset, lda, tau, _tau_offset, c, _c_offset, Ldc, work, _work_offset, lwork, info);
/* 6448:     */   }
/* 6449:     */   
/* 6450:     */   public void sormqr(String side, String trans, int m, int n, int k, float[] a, int lda, float[] tau, float[] c, int Ldc, float[] work, int lwork, intW info)
/* 6451:     */   {
/* 6452:5756 */     Sormqr.sormqr(side, trans, m, n, k, a, 0, lda, tau, 0, c, 0, Ldc, work, 0, lwork, info);
/* 6453:     */   }
/* 6454:     */   
/* 6455:     */   public void sormqr(String side, String trans, int m, int n, int k, float[] a, int _a_offset, int lda, float[] tau, int _tau_offset, float[] c, int _c_offset, int Ldc, float[] work, int _work_offset, int lwork, intW info)
/* 6456:     */   {
/* 6457:5761 */     Sormqr.sormqr(side, trans, m, n, k, a, _a_offset, lda, tau, _tau_offset, c, _c_offset, Ldc, work, _work_offset, lwork, info);
/* 6458:     */   }
/* 6459:     */   
/* 6460:     */   public void sormr2(String side, String trans, int m, int n, int k, float[] a, int lda, float[] tau, float[] c, int Ldc, float[] work, intW info)
/* 6461:     */   {
/* 6462:5766 */     Sormr2.sormr2(side, trans, m, n, k, a, 0, lda, tau, 0, c, 0, Ldc, work, 0, info);
/* 6463:     */   }
/* 6464:     */   
/* 6465:     */   public void sormr2(String side, String trans, int m, int n, int k, float[] a, int _a_offset, int lda, float[] tau, int _tau_offset, float[] c, int _c_offset, int Ldc, float[] work, int _work_offset, intW info)
/* 6466:     */   {
/* 6467:5771 */     Sormr2.sormr2(side, trans, m, n, k, a, _a_offset, lda, tau, _tau_offset, c, _c_offset, Ldc, work, _work_offset, info);
/* 6468:     */   }
/* 6469:     */   
/* 6470:     */   public void sormr3(String side, String trans, int m, int n, int k, int l, float[] a, int lda, float[] tau, float[] c, int Ldc, float[] work, intW info)
/* 6471:     */   {
/* 6472:5776 */     Sormr3.sormr3(side, trans, m, n, k, l, a, 0, lda, tau, 0, c, 0, Ldc, work, 0, info);
/* 6473:     */   }
/* 6474:     */   
/* 6475:     */   public void sormr3(String side, String trans, int m, int n, int k, int l, float[] a, int _a_offset, int lda, float[] tau, int _tau_offset, float[] c, int _c_offset, int Ldc, float[] work, int _work_offset, intW info)
/* 6476:     */   {
/* 6477:5781 */     Sormr3.sormr3(side, trans, m, n, k, l, a, _a_offset, lda, tau, _tau_offset, c, _c_offset, Ldc, work, _work_offset, info);
/* 6478:     */   }
/* 6479:     */   
/* 6480:     */   public void sormrq(String side, String trans, int m, int n, int k, float[] a, int lda, float[] tau, float[] c, int Ldc, float[] work, int lwork, intW info)
/* 6481:     */   {
/* 6482:5786 */     Sormrq.sormrq(side, trans, m, n, k, a, 0, lda, tau, 0, c, 0, Ldc, work, 0, lwork, info);
/* 6483:     */   }
/* 6484:     */   
/* 6485:     */   public void sormrq(String side, String trans, int m, int n, int k, float[] a, int _a_offset, int lda, float[] tau, int _tau_offset, float[] c, int _c_offset, int Ldc, float[] work, int _work_offset, int lwork, intW info)
/* 6486:     */   {
/* 6487:5791 */     Sormrq.sormrq(side, trans, m, n, k, a, _a_offset, lda, tau, _tau_offset, c, _c_offset, Ldc, work, _work_offset, lwork, info);
/* 6488:     */   }
/* 6489:     */   
/* 6490:     */   public void sormrz(String side, String trans, int m, int n, int k, int l, float[] a, int lda, float[] tau, float[] c, int Ldc, float[] work, int lwork, intW info)
/* 6491:     */   {
/* 6492:5796 */     Sormrz.sormrz(side, trans, m, n, k, l, a, 0, lda, tau, 0, c, 0, Ldc, work, 0, lwork, info);
/* 6493:     */   }
/* 6494:     */   
/* 6495:     */   public void sormrz(String side, String trans, int m, int n, int k, int l, float[] a, int _a_offset, int lda, float[] tau, int _tau_offset, float[] c, int _c_offset, int Ldc, float[] work, int _work_offset, int lwork, intW info)
/* 6496:     */   {
/* 6497:5801 */     Sormrz.sormrz(side, trans, m, n, k, l, a, _a_offset, lda, tau, _tau_offset, c, _c_offset, Ldc, work, _work_offset, lwork, info);
/* 6498:     */   }
/* 6499:     */   
/* 6500:     */   public void sormtr(String side, String uplo, String trans, int m, int n, float[] a, int lda, float[] tau, float[] c, int Ldc, float[] work, int lwork, intW info)
/* 6501:     */   {
/* 6502:5806 */     Sormtr.sormtr(side, uplo, trans, m, n, a, 0, lda, tau, 0, c, 0, Ldc, work, 0, lwork, info);
/* 6503:     */   }
/* 6504:     */   
/* 6505:     */   public void sormtr(String side, String uplo, String trans, int m, int n, float[] a, int _a_offset, int lda, float[] tau, int _tau_offset, float[] c, int _c_offset, int Ldc, float[] work, int _work_offset, int lwork, intW info)
/* 6506:     */   {
/* 6507:5811 */     Sormtr.sormtr(side, uplo, trans, m, n, a, _a_offset, lda, tau, _tau_offset, c, _c_offset, Ldc, work, _work_offset, lwork, info);
/* 6508:     */   }
/* 6509:     */   
/* 6510:     */   public void spbcon(String uplo, int n, int kd, float[] ab, int ldab, float anorm, floatW rcond, float[] work, int[] iwork, intW info)
/* 6511:     */   {
/* 6512:5816 */     Spbcon.spbcon(uplo, n, kd, ab, 0, ldab, anorm, rcond, work, 0, iwork, 0, info);
/* 6513:     */   }
/* 6514:     */   
/* 6515:     */   public void spbcon(String uplo, int n, int kd, float[] ab, int _ab_offset, int ldab, float anorm, floatW rcond, float[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 6516:     */   {
/* 6517:5821 */     Spbcon.spbcon(uplo, n, kd, ab, _ab_offset, ldab, anorm, rcond, work, _work_offset, iwork, _iwork_offset, info);
/* 6518:     */   }
/* 6519:     */   
/* 6520:     */   public void spbequ(String uplo, int n, int kd, float[] ab, int ldab, float[] s, floatW scond, floatW amax, intW info)
/* 6521:     */   {
/* 6522:5826 */     Spbequ.spbequ(uplo, n, kd, ab, 0, ldab, s, 0, scond, amax, info);
/* 6523:     */   }
/* 6524:     */   
/* 6525:     */   public void spbequ(String uplo, int n, int kd, float[] ab, int _ab_offset, int ldab, float[] s, int _s_offset, floatW scond, floatW amax, intW info)
/* 6526:     */   {
/* 6527:5831 */     Spbequ.spbequ(uplo, n, kd, ab, _ab_offset, ldab, s, _s_offset, scond, amax, info);
/* 6528:     */   }
/* 6529:     */   
/* 6530:     */   public void spbrfs(String uplo, int n, int kd, int nrhs, float[] ab, int ldab, float[] afb, int ldafb, float[] b, int ldb, float[] x, int ldx, float[] ferr, float[] berr, float[] work, int[] iwork, intW info)
/* 6531:     */   {
/* 6532:5836 */     Spbrfs.spbrfs(uplo, n, kd, nrhs, ab, 0, ldab, afb, 0, ldafb, b, 0, ldb, x, 0, ldx, ferr, 0, berr, 0, work, 0, iwork, 0, info);
/* 6533:     */   }
/* 6534:     */   
/* 6535:     */   public void spbrfs(String uplo, int n, int kd, int nrhs, float[] ab, int _ab_offset, int ldab, float[] afb, int _afb_offset, int ldafb, float[] b, int _b_offset, int ldb, float[] x, int _x_offset, int ldx, float[] ferr, int _ferr_offset, float[] berr, int _berr_offset, float[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 6536:     */   {
/* 6537:5841 */     Spbrfs.spbrfs(uplo, n, kd, nrhs, ab, _ab_offset, ldab, afb, _afb_offset, ldafb, b, _b_offset, ldb, x, _x_offset, ldx, ferr, _ferr_offset, berr, _berr_offset, work, _work_offset, iwork, _iwork_offset, info);
/* 6538:     */   }
/* 6539:     */   
/* 6540:     */   public void spbstf(String uplo, int n, int kd, float[] ab, int ldab, intW info)
/* 6541:     */   {
/* 6542:5846 */     Spbstf.spbstf(uplo, n, kd, ab, 0, ldab, info);
/* 6543:     */   }
/* 6544:     */   
/* 6545:     */   public void spbstf(String uplo, int n, int kd, float[] ab, int _ab_offset, int ldab, intW info)
/* 6546:     */   {
/* 6547:5851 */     Spbstf.spbstf(uplo, n, kd, ab, _ab_offset, ldab, info);
/* 6548:     */   }
/* 6549:     */   
/* 6550:     */   public void spbsv(String uplo, int n, int kd, int nrhs, float[] ab, int ldab, float[] b, int ldb, intW info)
/* 6551:     */   {
/* 6552:5856 */     Spbsv.spbsv(uplo, n, kd, nrhs, ab, 0, ldab, b, 0, ldb, info);
/* 6553:     */   }
/* 6554:     */   
/* 6555:     */   public void spbsv(String uplo, int n, int kd, int nrhs, float[] ab, int _ab_offset, int ldab, float[] b, int _b_offset, int ldb, intW info)
/* 6556:     */   {
/* 6557:5861 */     Spbsv.spbsv(uplo, n, kd, nrhs, ab, _ab_offset, ldab, b, _b_offset, ldb, info);
/* 6558:     */   }
/* 6559:     */   
/* 6560:     */   public void spbsvx(String fact, String uplo, int n, int kd, int nrhs, float[] ab, int ldab, float[] afb, int ldafb, StringW equed, float[] s, float[] b, int ldb, float[] x, int ldx, floatW rcond, float[] ferr, float[] berr, float[] work, int[] iwork, intW info)
/* 6561:     */   {
/* 6562:5866 */     Spbsvx.spbsvx(fact, uplo, n, kd, nrhs, ab, 0, ldab, afb, 0, ldafb, equed, s, 0, b, 0, ldb, x, 0, ldx, rcond, ferr, 0, berr, 0, work, 0, iwork, 0, info);
/* 6563:     */   }
/* 6564:     */   
/* 6565:     */   public void spbsvx(String fact, String uplo, int n, int kd, int nrhs, float[] ab, int _ab_offset, int ldab, float[] afb, int _afb_offset, int ldafb, StringW equed, float[] s, int _s_offset, float[] b, int _b_offset, int ldb, float[] x, int _x_offset, int ldx, floatW rcond, float[] ferr, int _ferr_offset, float[] berr, int _berr_offset, float[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 6566:     */   {
/* 6567:5871 */     Spbsvx.spbsvx(fact, uplo, n, kd, nrhs, ab, _ab_offset, ldab, afb, _afb_offset, ldafb, equed, s, _s_offset, b, _b_offset, ldb, x, _x_offset, ldx, rcond, ferr, _ferr_offset, berr, _berr_offset, work, _work_offset, iwork, _iwork_offset, info);
/* 6568:     */   }
/* 6569:     */   
/* 6570:     */   public void spbtf2(String uplo, int n, int kd, float[] ab, int ldab, intW info)
/* 6571:     */   {
/* 6572:5876 */     Spbtf2.spbtf2(uplo, n, kd, ab, 0, ldab, info);
/* 6573:     */   }
/* 6574:     */   
/* 6575:     */   public void spbtf2(String uplo, int n, int kd, float[] ab, int _ab_offset, int ldab, intW info)
/* 6576:     */   {
/* 6577:5881 */     Spbtf2.spbtf2(uplo, n, kd, ab, _ab_offset, ldab, info);
/* 6578:     */   }
/* 6579:     */   
/* 6580:     */   public void spbtrf(String uplo, int n, int kd, float[] ab, int ldab, intW info)
/* 6581:     */   {
/* 6582:5886 */     Spbtrf.spbtrf(uplo, n, kd, ab, 0, ldab, info);
/* 6583:     */   }
/* 6584:     */   
/* 6585:     */   public void spbtrf(String uplo, int n, int kd, float[] ab, int _ab_offset, int ldab, intW info)
/* 6586:     */   {
/* 6587:5891 */     Spbtrf.spbtrf(uplo, n, kd, ab, _ab_offset, ldab, info);
/* 6588:     */   }
/* 6589:     */   
/* 6590:     */   public void spbtrs(String uplo, int n, int kd, int nrhs, float[] ab, int ldab, float[] b, int ldb, intW info)
/* 6591:     */   {
/* 6592:5896 */     Spbtrs.spbtrs(uplo, n, kd, nrhs, ab, 0, ldab, b, 0, ldb, info);
/* 6593:     */   }
/* 6594:     */   
/* 6595:     */   public void spbtrs(String uplo, int n, int kd, int nrhs, float[] ab, int _ab_offset, int ldab, float[] b, int _b_offset, int ldb, intW info)
/* 6596:     */   {
/* 6597:5901 */     Spbtrs.spbtrs(uplo, n, kd, nrhs, ab, _ab_offset, ldab, b, _b_offset, ldb, info);
/* 6598:     */   }
/* 6599:     */   
/* 6600:     */   public void spocon(String uplo, int n, float[] a, int lda, float anorm, floatW rcond, float[] work, int[] iwork, intW info)
/* 6601:     */   {
/* 6602:5906 */     Spocon.spocon(uplo, n, a, 0, lda, anorm, rcond, work, 0, iwork, 0, info);
/* 6603:     */   }
/* 6604:     */   
/* 6605:     */   public void spocon(String uplo, int n, float[] a, int _a_offset, int lda, float anorm, floatW rcond, float[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 6606:     */   {
/* 6607:5911 */     Spocon.spocon(uplo, n, a, _a_offset, lda, anorm, rcond, work, _work_offset, iwork, _iwork_offset, info);
/* 6608:     */   }
/* 6609:     */   
/* 6610:     */   public void spoequ(int n, float[] a, int lda, float[] s, floatW scond, floatW amax, intW info)
/* 6611:     */   {
/* 6612:5916 */     Spoequ.spoequ(n, a, 0, lda, s, 0, scond, amax, info);
/* 6613:     */   }
/* 6614:     */   
/* 6615:     */   public void spoequ(int n, float[] a, int _a_offset, int lda, float[] s, int _s_offset, floatW scond, floatW amax, intW info)
/* 6616:     */   {
/* 6617:5921 */     Spoequ.spoequ(n, a, _a_offset, lda, s, _s_offset, scond, amax, info);
/* 6618:     */   }
/* 6619:     */   
/* 6620:     */   public void sporfs(String uplo, int n, int nrhs, float[] a, int lda, float[] af, int ldaf, float[] b, int ldb, float[] x, int ldx, float[] ferr, float[] berr, float[] work, int[] iwork, intW info)
/* 6621:     */   {
/* 6622:5926 */     Sporfs.sporfs(uplo, n, nrhs, a, 0, lda, af, 0, ldaf, b, 0, ldb, x, 0, ldx, ferr, 0, berr, 0, work, 0, iwork, 0, info);
/* 6623:     */   }
/* 6624:     */   
/* 6625:     */   public void sporfs(String uplo, int n, int nrhs, float[] a, int _a_offset, int lda, float[] af, int _af_offset, int ldaf, float[] b, int _b_offset, int ldb, float[] x, int _x_offset, int ldx, float[] ferr, int _ferr_offset, float[] berr, int _berr_offset, float[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 6626:     */   {
/* 6627:5931 */     Sporfs.sporfs(uplo, n, nrhs, a, _a_offset, lda, af, _af_offset, ldaf, b, _b_offset, ldb, x, _x_offset, ldx, ferr, _ferr_offset, berr, _berr_offset, work, _work_offset, iwork, _iwork_offset, info);
/* 6628:     */   }
/* 6629:     */   
/* 6630:     */   public void sposv(String uplo, int n, int nrhs, float[] a, int lda, float[] b, int ldb, intW info)
/* 6631:     */   {
/* 6632:5936 */     Sposv.sposv(uplo, n, nrhs, a, 0, lda, b, 0, ldb, info);
/* 6633:     */   }
/* 6634:     */   
/* 6635:     */   public void sposv(String uplo, int n, int nrhs, float[] a, int _a_offset, int lda, float[] b, int _b_offset, int ldb, intW info)
/* 6636:     */   {
/* 6637:5941 */     Sposv.sposv(uplo, n, nrhs, a, _a_offset, lda, b, _b_offset, ldb, info);
/* 6638:     */   }
/* 6639:     */   
/* 6640:     */   public void sposvx(String fact, String uplo, int n, int nrhs, float[] a, int lda, float[] af, int ldaf, StringW equed, float[] s, float[] b, int ldb, float[] x, int ldx, floatW rcond, float[] ferr, float[] berr, float[] work, int[] iwork, intW info)
/* 6641:     */   {
/* 6642:5946 */     Sposvx.sposvx(fact, uplo, n, nrhs, a, 0, lda, af, 0, ldaf, equed, s, 0, b, 0, ldb, x, 0, ldx, rcond, ferr, 0, berr, 0, work, 0, iwork, 0, info);
/* 6643:     */   }
/* 6644:     */   
/* 6645:     */   public void sposvx(String fact, String uplo, int n, int nrhs, float[] a, int _a_offset, int lda, float[] af, int _af_offset, int ldaf, StringW equed, float[] s, int _s_offset, float[] b, int _b_offset, int ldb, float[] x, int _x_offset, int ldx, floatW rcond, float[] ferr, int _ferr_offset, float[] berr, int _berr_offset, float[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 6646:     */   {
/* 6647:5951 */     Sposvx.sposvx(fact, uplo, n, nrhs, a, _a_offset, lda, af, _af_offset, ldaf, equed, s, _s_offset, b, _b_offset, ldb, x, _x_offset, ldx, rcond, ferr, _ferr_offset, berr, _berr_offset, work, _work_offset, iwork, _iwork_offset, info);
/* 6648:     */   }
/* 6649:     */   
/* 6650:     */   public void spotf2(String uplo, int n, float[] a, int lda, intW info)
/* 6651:     */   {
/* 6652:5956 */     Spotf2.spotf2(uplo, n, a, 0, lda, info);
/* 6653:     */   }
/* 6654:     */   
/* 6655:     */   public void spotf2(String uplo, int n, float[] a, int _a_offset, int lda, intW info)
/* 6656:     */   {
/* 6657:5961 */     Spotf2.spotf2(uplo, n, a, _a_offset, lda, info);
/* 6658:     */   }
/* 6659:     */   
/* 6660:     */   public void spotrf(String uplo, int n, float[] a, int lda, intW info)
/* 6661:     */   {
/* 6662:5966 */     Spotrf.spotrf(uplo, n, a, 0, lda, info);
/* 6663:     */   }
/* 6664:     */   
/* 6665:     */   public void spotrf(String uplo, int n, float[] a, int _a_offset, int lda, intW info)
/* 6666:     */   {
/* 6667:5971 */     Spotrf.spotrf(uplo, n, a, _a_offset, lda, info);
/* 6668:     */   }
/* 6669:     */   
/* 6670:     */   public void spotri(String uplo, int n, float[] a, int lda, intW info)
/* 6671:     */   {
/* 6672:5976 */     Spotri.spotri(uplo, n, a, 0, lda, info);
/* 6673:     */   }
/* 6674:     */   
/* 6675:     */   public void spotri(String uplo, int n, float[] a, int _a_offset, int lda, intW info)
/* 6676:     */   {
/* 6677:5981 */     Spotri.spotri(uplo, n, a, _a_offset, lda, info);
/* 6678:     */   }
/* 6679:     */   
/* 6680:     */   public void spotrs(String uplo, int n, int nrhs, float[] a, int lda, float[] b, int ldb, intW info)
/* 6681:     */   {
/* 6682:5986 */     Spotrs.spotrs(uplo, n, nrhs, a, 0, lda, b, 0, ldb, info);
/* 6683:     */   }
/* 6684:     */   
/* 6685:     */   public void spotrs(String uplo, int n, int nrhs, float[] a, int _a_offset, int lda, float[] b, int _b_offset, int ldb, intW info)
/* 6686:     */   {
/* 6687:5991 */     Spotrs.spotrs(uplo, n, nrhs, a, _a_offset, lda, b, _b_offset, ldb, info);
/* 6688:     */   }
/* 6689:     */   
/* 6690:     */   public void sppcon(String uplo, int n, float[] ap, float anorm, floatW rcond, float[] work, int[] iwork, intW info)
/* 6691:     */   {
/* 6692:5996 */     Sppcon.sppcon(uplo, n, ap, 0, anorm, rcond, work, 0, iwork, 0, info);
/* 6693:     */   }
/* 6694:     */   
/* 6695:     */   public void sppcon(String uplo, int n, float[] ap, int _ap_offset, float anorm, floatW rcond, float[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 6696:     */   {
/* 6697:6001 */     Sppcon.sppcon(uplo, n, ap, _ap_offset, anorm, rcond, work, _work_offset, iwork, _iwork_offset, info);
/* 6698:     */   }
/* 6699:     */   
/* 6700:     */   public void sppequ(String uplo, int n, float[] ap, float[] s, floatW scond, floatW amax, intW info)
/* 6701:     */   {
/* 6702:6006 */     Sppequ.sppequ(uplo, n, ap, 0, s, 0, scond, amax, info);
/* 6703:     */   }
/* 6704:     */   
/* 6705:     */   public void sppequ(String uplo, int n, float[] ap, int _ap_offset, float[] s, int _s_offset, floatW scond, floatW amax, intW info)
/* 6706:     */   {
/* 6707:6011 */     Sppequ.sppequ(uplo, n, ap, _ap_offset, s, _s_offset, scond, amax, info);
/* 6708:     */   }
/* 6709:     */   
/* 6710:     */   public void spprfs(String uplo, int n, int nrhs, float[] ap, float[] afp, float[] b, int ldb, float[] x, int ldx, float[] ferr, float[] berr, float[] work, int[] iwork, intW info)
/* 6711:     */   {
/* 6712:6016 */     Spprfs.spprfs(uplo, n, nrhs, ap, 0, afp, 0, b, 0, ldb, x, 0, ldx, ferr, 0, berr, 0, work, 0, iwork, 0, info);
/* 6713:     */   }
/* 6714:     */   
/* 6715:     */   public void spprfs(String uplo, int n, int nrhs, float[] ap, int _ap_offset, float[] afp, int _afp_offset, float[] b, int _b_offset, int ldb, float[] x, int _x_offset, int ldx, float[] ferr, int _ferr_offset, float[] berr, int _berr_offset, float[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 6716:     */   {
/* 6717:6021 */     Spprfs.spprfs(uplo, n, nrhs, ap, _ap_offset, afp, _afp_offset, b, _b_offset, ldb, x, _x_offset, ldx, ferr, _ferr_offset, berr, _berr_offset, work, _work_offset, iwork, _iwork_offset, info);
/* 6718:     */   }
/* 6719:     */   
/* 6720:     */   public void sppsv(String uplo, int n, int nrhs, float[] ap, float[] b, int ldb, intW info)
/* 6721:     */   {
/* 6722:6026 */     Sppsv.sppsv(uplo, n, nrhs, ap, 0, b, 0, ldb, info);
/* 6723:     */   }
/* 6724:     */   
/* 6725:     */   public void sppsv(String uplo, int n, int nrhs, float[] ap, int _ap_offset, float[] b, int _b_offset, int ldb, intW info)
/* 6726:     */   {
/* 6727:6031 */     Sppsv.sppsv(uplo, n, nrhs, ap, _ap_offset, b, _b_offset, ldb, info);
/* 6728:     */   }
/* 6729:     */   
/* 6730:     */   public void sppsvx(String fact, String uplo, int n, int nrhs, float[] ap, float[] afp, StringW equed, float[] s, float[] b, int ldb, float[] x, int ldx, floatW rcond, float[] ferr, float[] berr, float[] work, int[] iwork, intW info)
/* 6731:     */   {
/* 6732:6036 */     Sppsvx.sppsvx(fact, uplo, n, nrhs, ap, 0, afp, 0, equed, s, 0, b, 0, ldb, x, 0, ldx, rcond, ferr, 0, berr, 0, work, 0, iwork, 0, info);
/* 6733:     */   }
/* 6734:     */   
/* 6735:     */   public void sppsvx(String fact, String uplo, int n, int nrhs, float[] ap, int _ap_offset, float[] afp, int _afp_offset, StringW equed, float[] s, int _s_offset, float[] b, int _b_offset, int ldb, float[] x, int _x_offset, int ldx, floatW rcond, float[] ferr, int _ferr_offset, float[] berr, int _berr_offset, float[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 6736:     */   {
/* 6737:6041 */     Sppsvx.sppsvx(fact, uplo, n, nrhs, ap, _ap_offset, afp, _afp_offset, equed, s, _s_offset, b, _b_offset, ldb, x, _x_offset, ldx, rcond, ferr, _ferr_offset, berr, _berr_offset, work, _work_offset, iwork, _iwork_offset, info);
/* 6738:     */   }
/* 6739:     */   
/* 6740:     */   public void spptrf(String uplo, int n, float[] ap, intW info)
/* 6741:     */   {
/* 6742:6046 */     Spptrf.spptrf(uplo, n, ap, 0, info);
/* 6743:     */   }
/* 6744:     */   
/* 6745:     */   public void spptrf(String uplo, int n, float[] ap, int _ap_offset, intW info)
/* 6746:     */   {
/* 6747:6051 */     Spptrf.spptrf(uplo, n, ap, _ap_offset, info);
/* 6748:     */   }
/* 6749:     */   
/* 6750:     */   public void spptri(String uplo, int n, float[] ap, intW info)
/* 6751:     */   {
/* 6752:6056 */     Spptri.spptri(uplo, n, ap, 0, info);
/* 6753:     */   }
/* 6754:     */   
/* 6755:     */   public void spptri(String uplo, int n, float[] ap, int _ap_offset, intW info)
/* 6756:     */   {
/* 6757:6061 */     Spptri.spptri(uplo, n, ap, _ap_offset, info);
/* 6758:     */   }
/* 6759:     */   
/* 6760:     */   public void spptrs(String uplo, int n, int nrhs, float[] ap, float[] b, int ldb, intW info)
/* 6761:     */   {
/* 6762:6066 */     Spptrs.spptrs(uplo, n, nrhs, ap, 0, b, 0, ldb, info);
/* 6763:     */   }
/* 6764:     */   
/* 6765:     */   public void spptrs(String uplo, int n, int nrhs, float[] ap, int _ap_offset, float[] b, int _b_offset, int ldb, intW info)
/* 6766:     */   {
/* 6767:6071 */     Spptrs.spptrs(uplo, n, nrhs, ap, _ap_offset, b, _b_offset, ldb, info);
/* 6768:     */   }
/* 6769:     */   
/* 6770:     */   public void sptcon(int n, float[] d, float[] e, float anorm, floatW rcond, float[] work, intW info)
/* 6771:     */   {
/* 6772:6076 */     Sptcon.sptcon(n, d, 0, e, 0, anorm, rcond, work, 0, info);
/* 6773:     */   }
/* 6774:     */   
/* 6775:     */   public void sptcon(int n, float[] d, int _d_offset, float[] e, int _e_offset, float anorm, floatW rcond, float[] work, int _work_offset, intW info)
/* 6776:     */   {
/* 6777:6081 */     Sptcon.sptcon(n, d, _d_offset, e, _e_offset, anorm, rcond, work, _work_offset, info);
/* 6778:     */   }
/* 6779:     */   
/* 6780:     */   public void spteqr(String compz, int n, float[] d, float[] e, float[] z, int ldz, float[] work, intW info)
/* 6781:     */   {
/* 6782:6086 */     Spteqr.spteqr(compz, n, d, 0, e, 0, z, 0, ldz, work, 0, info);
/* 6783:     */   }
/* 6784:     */   
/* 6785:     */   public void spteqr(String compz, int n, float[] d, int _d_offset, float[] e, int _e_offset, float[] z, int _z_offset, int ldz, float[] work, int _work_offset, intW info)
/* 6786:     */   {
/* 6787:6091 */     Spteqr.spteqr(compz, n, d, _d_offset, e, _e_offset, z, _z_offset, ldz, work, _work_offset, info);
/* 6788:     */   }
/* 6789:     */   
/* 6790:     */   public void sptrfs(int n, int nrhs, float[] d, float[] e, float[] df, float[] ef, float[] b, int ldb, float[] x, int ldx, float[] ferr, float[] berr, float[] work, intW info)
/* 6791:     */   {
/* 6792:6096 */     Sptrfs.sptrfs(n, nrhs, d, 0, e, 0, df, 0, ef, 0, b, 0, ldb, x, 0, ldx, ferr, 0, berr, 0, work, 0, info);
/* 6793:     */   }
/* 6794:     */   
/* 6795:     */   public void sptrfs(int n, int nrhs, float[] d, int _d_offset, float[] e, int _e_offset, float[] df, int _df_offset, float[] ef, int _ef_offset, float[] b, int _b_offset, int ldb, float[] x, int _x_offset, int ldx, float[] ferr, int _ferr_offset, float[] berr, int _berr_offset, float[] work, int _work_offset, intW info)
/* 6796:     */   {
/* 6797:6101 */     Sptrfs.sptrfs(n, nrhs, d, _d_offset, e, _e_offset, df, _df_offset, ef, _ef_offset, b, _b_offset, ldb, x, _x_offset, ldx, ferr, _ferr_offset, berr, _berr_offset, work, _work_offset, info);
/* 6798:     */   }
/* 6799:     */   
/* 6800:     */   public void sptsv(int n, int nrhs, float[] d, float[] e, float[] b, int ldb, intW info)
/* 6801:     */   {
/* 6802:6106 */     Sptsv.sptsv(n, nrhs, d, 0, e, 0, b, 0, ldb, info);
/* 6803:     */   }
/* 6804:     */   
/* 6805:     */   public void sptsv(int n, int nrhs, float[] d, int _d_offset, float[] e, int _e_offset, float[] b, int _b_offset, int ldb, intW info)
/* 6806:     */   {
/* 6807:6111 */     Sptsv.sptsv(n, nrhs, d, _d_offset, e, _e_offset, b, _b_offset, ldb, info);
/* 6808:     */   }
/* 6809:     */   
/* 6810:     */   public void sptsvx(String fact, int n, int nrhs, float[] d, float[] e, float[] df, float[] ef, float[] b, int ldb, float[] x, int ldx, floatW rcond, float[] ferr, float[] berr, float[] work, intW info)
/* 6811:     */   {
/* 6812:6116 */     Sptsvx.sptsvx(fact, n, nrhs, d, 0, e, 0, df, 0, ef, 0, b, 0, ldb, x, 0, ldx, rcond, ferr, 0, berr, 0, work, 0, info);
/* 6813:     */   }
/* 6814:     */   
/* 6815:     */   public void sptsvx(String fact, int n, int nrhs, float[] d, int _d_offset, float[] e, int _e_offset, float[] df, int _df_offset, float[] ef, int _ef_offset, float[] b, int _b_offset, int ldb, float[] x, int _x_offset, int ldx, floatW rcond, float[] ferr, int _ferr_offset, float[] berr, int _berr_offset, float[] work, int _work_offset, intW info)
/* 6816:     */   {
/* 6817:6121 */     Sptsvx.sptsvx(fact, n, nrhs, d, _d_offset, e, _e_offset, df, _df_offset, ef, _ef_offset, b, _b_offset, ldb, x, _x_offset, ldx, rcond, ferr, _ferr_offset, berr, _berr_offset, work, _work_offset, info);
/* 6818:     */   }
/* 6819:     */   
/* 6820:     */   public void spttrf(int n, float[] d, float[] e, intW info)
/* 6821:     */   {
/* 6822:6126 */     Spttrf.spttrf(n, d, 0, e, 0, info);
/* 6823:     */   }
/* 6824:     */   
/* 6825:     */   public void spttrf(int n, float[] d, int _d_offset, float[] e, int _e_offset, intW info)
/* 6826:     */   {
/* 6827:6131 */     Spttrf.spttrf(n, d, _d_offset, e, _e_offset, info);
/* 6828:     */   }
/* 6829:     */   
/* 6830:     */   public void spttrs(int n, int nrhs, float[] d, float[] e, float[] b, int ldb, intW info)
/* 6831:     */   {
/* 6832:6136 */     Spttrs.spttrs(n, nrhs, d, 0, e, 0, b, 0, ldb, info);
/* 6833:     */   }
/* 6834:     */   
/* 6835:     */   public void spttrs(int n, int nrhs, float[] d, int _d_offset, float[] e, int _e_offset, float[] b, int _b_offset, int ldb, intW info)
/* 6836:     */   {
/* 6837:6141 */     Spttrs.spttrs(n, nrhs, d, _d_offset, e, _e_offset, b, _b_offset, ldb, info);
/* 6838:     */   }
/* 6839:     */   
/* 6840:     */   public void sptts2(int n, int nrhs, float[] d, float[] e, float[] b, int ldb)
/* 6841:     */   {
/* 6842:6146 */     Sptts2.sptts2(n, nrhs, d, 0, e, 0, b, 0, ldb);
/* 6843:     */   }
/* 6844:     */   
/* 6845:     */   public void sptts2(int n, int nrhs, float[] d, int _d_offset, float[] e, int _e_offset, float[] b, int _b_offset, int ldb)
/* 6846:     */   {
/* 6847:6151 */     Sptts2.sptts2(n, nrhs, d, _d_offset, e, _e_offset, b, _b_offset, ldb);
/* 6848:     */   }
/* 6849:     */   
/* 6850:     */   public void srscl(int n, float sa, float[] sx, int incx)
/* 6851:     */   {
/* 6852:6156 */     Srscl.srscl(n, sa, sx, 0, incx);
/* 6853:     */   }
/* 6854:     */   
/* 6855:     */   public void srscl(int n, float sa, float[] sx, int _sx_offset, int incx)
/* 6856:     */   {
/* 6857:6161 */     Srscl.srscl(n, sa, sx, _sx_offset, incx);
/* 6858:     */   }
/* 6859:     */   
/* 6860:     */   public void ssbev(String jobz, String uplo, int n, int kd, float[] ab, int ldab, float[] w, float[] z, int ldz, float[] work, intW info)
/* 6861:     */   {
/* 6862:6166 */     Ssbev.ssbev(jobz, uplo, n, kd, ab, 0, ldab, w, 0, z, 0, ldz, work, 0, info);
/* 6863:     */   }
/* 6864:     */   
/* 6865:     */   public void ssbev(String jobz, String uplo, int n, int kd, float[] ab, int _ab_offset, int ldab, float[] w, int _w_offset, float[] z, int _z_offset, int ldz, float[] work, int _work_offset, intW info)
/* 6866:     */   {
/* 6867:6171 */     Ssbev.ssbev(jobz, uplo, n, kd, ab, _ab_offset, ldab, w, _w_offset, z, _z_offset, ldz, work, _work_offset, info);
/* 6868:     */   }
/* 6869:     */   
/* 6870:     */   public void ssbevd(String jobz, String uplo, int n, int kd, float[] ab, int ldab, float[] w, float[] z, int ldz, float[] work, int lwork, int[] iwork, int liwork, intW info)
/* 6871:     */   {
/* 6872:6176 */     Ssbevd.ssbevd(jobz, uplo, n, kd, ab, 0, ldab, w, 0, z, 0, ldz, work, 0, lwork, iwork, 0, liwork, info);
/* 6873:     */   }
/* 6874:     */   
/* 6875:     */   public void ssbevd(String jobz, String uplo, int n, int kd, float[] ab, int _ab_offset, int ldab, float[] w, int _w_offset, float[] z, int _z_offset, int ldz, float[] work, int _work_offset, int lwork, int[] iwork, int _iwork_offset, int liwork, intW info)
/* 6876:     */   {
/* 6877:6181 */     Ssbevd.ssbevd(jobz, uplo, n, kd, ab, _ab_offset, ldab, w, _w_offset, z, _z_offset, ldz, work, _work_offset, lwork, iwork, _iwork_offset, liwork, info);
/* 6878:     */   }
/* 6879:     */   
/* 6880:     */   public void ssbevx(String jobz, String range, String uplo, int n, int kd, float[] ab, int ldab, float[] q, int ldq, float vl, float vu, int il, int iu, float abstol, intW m, float[] w, float[] z, int ldz, float[] work, int[] iwork, int[] ifail, intW info)
/* 6881:     */   {
/* 6882:6186 */     Ssbevx.ssbevx(jobz, range, uplo, n, kd, ab, 0, ldab, q, 0, ldq, vl, vu, il, iu, abstol, m, w, 0, z, 0, ldz, work, 0, iwork, 0, ifail, 0, info);
/* 6883:     */   }
/* 6884:     */   
/* 6885:     */   public void ssbevx(String jobz, String range, String uplo, int n, int kd, float[] ab, int _ab_offset, int ldab, float[] q, int _q_offset, int ldq, float vl, float vu, int il, int iu, float abstol, intW m, float[] w, int _w_offset, float[] z, int _z_offset, int ldz, float[] work, int _work_offset, int[] iwork, int _iwork_offset, int[] ifail, int _ifail_offset, intW info)
/* 6886:     */   {
/* 6887:6191 */     Ssbevx.ssbevx(jobz, range, uplo, n, kd, ab, _ab_offset, ldab, q, _q_offset, ldq, vl, vu, il, iu, abstol, m, w, _w_offset, z, _z_offset, ldz, work, _work_offset, iwork, _iwork_offset, ifail, _ifail_offset, info);
/* 6888:     */   }
/* 6889:     */   
/* 6890:     */   public void ssbgst(String vect, String uplo, int n, int ka, int kb, float[] ab, int ldab, float[] bb, int ldbb, float[] x, int ldx, float[] work, intW info)
/* 6891:     */   {
/* 6892:6196 */     Ssbgst.ssbgst(vect, uplo, n, ka, kb, ab, 0, ldab, bb, 0, ldbb, x, 0, ldx, work, 0, info);
/* 6893:     */   }
/* 6894:     */   
/* 6895:     */   public void ssbgst(String vect, String uplo, int n, int ka, int kb, float[] ab, int _ab_offset, int ldab, float[] bb, int _bb_offset, int ldbb, float[] x, int _x_offset, int ldx, float[] work, int _work_offset, intW info)
/* 6896:     */   {
/* 6897:6201 */     Ssbgst.ssbgst(vect, uplo, n, ka, kb, ab, _ab_offset, ldab, bb, _bb_offset, ldbb, x, _x_offset, ldx, work, _work_offset, info);
/* 6898:     */   }
/* 6899:     */   
/* 6900:     */   public void ssbgv(String jobz, String uplo, int n, int ka, int kb, float[] ab, int ldab, float[] bb, int ldbb, float[] w, float[] z, int ldz, float[] work, intW info)
/* 6901:     */   {
/* 6902:6206 */     Ssbgv.ssbgv(jobz, uplo, n, ka, kb, ab, 0, ldab, bb, 0, ldbb, w, 0, z, 0, ldz, work, 0, info);
/* 6903:     */   }
/* 6904:     */   
/* 6905:     */   public void ssbgv(String jobz, String uplo, int n, int ka, int kb, float[] ab, int _ab_offset, int ldab, float[] bb, int _bb_offset, int ldbb, float[] w, int _w_offset, float[] z, int _z_offset, int ldz, float[] work, int _work_offset, intW info)
/* 6906:     */   {
/* 6907:6211 */     Ssbgv.ssbgv(jobz, uplo, n, ka, kb, ab, _ab_offset, ldab, bb, _bb_offset, ldbb, w, _w_offset, z, _z_offset, ldz, work, _work_offset, info);
/* 6908:     */   }
/* 6909:     */   
/* 6910:     */   public void ssbgvd(String jobz, String uplo, int n, int ka, int kb, float[] ab, int ldab, float[] bb, int ldbb, float[] w, float[] z, int ldz, float[] work, int lwork, int[] iwork, int liwork, intW info)
/* 6911:     */   {
/* 6912:6216 */     Ssbgvd.ssbgvd(jobz, uplo, n, ka, kb, ab, 0, ldab, bb, 0, ldbb, w, 0, z, 0, ldz, work, 0, lwork, iwork, 0, liwork, info);
/* 6913:     */   }
/* 6914:     */   
/* 6915:     */   public void ssbgvd(String jobz, String uplo, int n, int ka, int kb, float[] ab, int _ab_offset, int ldab, float[] bb, int _bb_offset, int ldbb, float[] w, int _w_offset, float[] z, int _z_offset, int ldz, float[] work, int _work_offset, int lwork, int[] iwork, int _iwork_offset, int liwork, intW info)
/* 6916:     */   {
/* 6917:6221 */     Ssbgvd.ssbgvd(jobz, uplo, n, ka, kb, ab, _ab_offset, ldab, bb, _bb_offset, ldbb, w, _w_offset, z, _z_offset, ldz, work, _work_offset, lwork, iwork, _iwork_offset, liwork, info);
/* 6918:     */   }
/* 6919:     */   
/* 6920:     */   public void ssbgvx(String jobz, String range, String uplo, int n, int ka, int kb, float[] ab, int ldab, float[] bb, int ldbb, float[] q, int ldq, float vl, float vu, int il, int iu, float abstol, intW m, float[] w, float[] z, int ldz, float[] work, int[] iwork, int[] ifail, intW info)
/* 6921:     */   {
/* 6922:6226 */     Ssbgvx.ssbgvx(jobz, range, uplo, n, ka, kb, ab, 0, ldab, bb, 0, ldbb, q, 0, ldq, vl, vu, il, iu, abstol, m, w, 0, z, 0, ldz, work, 0, iwork, 0, ifail, 0, info);
/* 6923:     */   }
/* 6924:     */   
/* 6925:     */   public void ssbgvx(String jobz, String range, String uplo, int n, int ka, int kb, float[] ab, int _ab_offset, int ldab, float[] bb, int _bb_offset, int ldbb, float[] q, int _q_offset, int ldq, float vl, float vu, int il, int iu, float abstol, intW m, float[] w, int _w_offset, float[] z, int _z_offset, int ldz, float[] work, int _work_offset, int[] iwork, int _iwork_offset, int[] ifail, int _ifail_offset, intW info)
/* 6926:     */   {
/* 6927:6231 */     Ssbgvx.ssbgvx(jobz, range, uplo, n, ka, kb, ab, _ab_offset, ldab, bb, _bb_offset, ldbb, q, _q_offset, ldq, vl, vu, il, iu, abstol, m, w, _w_offset, z, _z_offset, ldz, work, _work_offset, iwork, _iwork_offset, ifail, _ifail_offset, info);
/* 6928:     */   }
/* 6929:     */   
/* 6930:     */   public void ssbtrd(String vect, String uplo, int n, int kd, float[] ab, int ldab, float[] d, float[] e, float[] q, int ldq, float[] work, intW info)
/* 6931:     */   {
/* 6932:6236 */     Ssbtrd.ssbtrd(vect, uplo, n, kd, ab, 0, ldab, d, 0, e, 0, q, 0, ldq, work, 0, info);
/* 6933:     */   }
/* 6934:     */   
/* 6935:     */   public void ssbtrd(String vect, String uplo, int n, int kd, float[] ab, int _ab_offset, int ldab, float[] d, int _d_offset, float[] e, int _e_offset, float[] q, int _q_offset, int ldq, float[] work, int _work_offset, intW info)
/* 6936:     */   {
/* 6937:6241 */     Ssbtrd.ssbtrd(vect, uplo, n, kd, ab, _ab_offset, ldab, d, _d_offset, e, _e_offset, q, _q_offset, ldq, work, _work_offset, info);
/* 6938:     */   }
/* 6939:     */   
/* 6940:     */   public void sspcon(String uplo, int n, float[] ap, int[] ipiv, float anorm, floatW rcond, float[] work, int[] iwork, intW info)
/* 6941:     */   {
/* 6942:6246 */     Sspcon.sspcon(uplo, n, ap, 0, ipiv, 0, anorm, rcond, work, 0, iwork, 0, info);
/* 6943:     */   }
/* 6944:     */   
/* 6945:     */   public void sspcon(String uplo, int n, float[] ap, int _ap_offset, int[] ipiv, int _ipiv_offset, float anorm, floatW rcond, float[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 6946:     */   {
/* 6947:6251 */     Sspcon.sspcon(uplo, n, ap, _ap_offset, ipiv, _ipiv_offset, anorm, rcond, work, _work_offset, iwork, _iwork_offset, info);
/* 6948:     */   }
/* 6949:     */   
/* 6950:     */   public void sspev(String jobz, String uplo, int n, float[] ap, float[] w, float[] z, int ldz, float[] work, intW info)
/* 6951:     */   {
/* 6952:6256 */     Sspev.sspev(jobz, uplo, n, ap, 0, w, 0, z, 0, ldz, work, 0, info);
/* 6953:     */   }
/* 6954:     */   
/* 6955:     */   public void sspev(String jobz, String uplo, int n, float[] ap, int _ap_offset, float[] w, int _w_offset, float[] z, int _z_offset, int ldz, float[] work, int _work_offset, intW info)
/* 6956:     */   {
/* 6957:6261 */     Sspev.sspev(jobz, uplo, n, ap, _ap_offset, w, _w_offset, z, _z_offset, ldz, work, _work_offset, info);
/* 6958:     */   }
/* 6959:     */   
/* 6960:     */   public void sspevd(String jobz, String uplo, int n, float[] ap, float[] w, float[] z, int ldz, float[] work, int lwork, int[] iwork, int liwork, intW info)
/* 6961:     */   {
/* 6962:6266 */     Sspevd.sspevd(jobz, uplo, n, ap, 0, w, 0, z, 0, ldz, work, 0, lwork, iwork, 0, liwork, info);
/* 6963:     */   }
/* 6964:     */   
/* 6965:     */   public void sspevd(String jobz, String uplo, int n, float[] ap, int _ap_offset, float[] w, int _w_offset, float[] z, int _z_offset, int ldz, float[] work, int _work_offset, int lwork, int[] iwork, int _iwork_offset, int liwork, intW info)
/* 6966:     */   {
/* 6967:6271 */     Sspevd.sspevd(jobz, uplo, n, ap, _ap_offset, w, _w_offset, z, _z_offset, ldz, work, _work_offset, lwork, iwork, _iwork_offset, liwork, info);
/* 6968:     */   }
/* 6969:     */   
/* 6970:     */   public void sspevx(String jobz, String range, String uplo, int n, float[] ap, float vl, float vu, int il, int iu, float abstol, intW m, float[] w, float[] z, int ldz, float[] work, int[] iwork, int[] ifail, intW info)
/* 6971:     */   {
/* 6972:6276 */     Sspevx.sspevx(jobz, range, uplo, n, ap, 0, vl, vu, il, iu, abstol, m, w, 0, z, 0, ldz, work, 0, iwork, 0, ifail, 0, info);
/* 6973:     */   }
/* 6974:     */   
/* 6975:     */   public void sspevx(String jobz, String range, String uplo, int n, float[] ap, int _ap_offset, float vl, float vu, int il, int iu, float abstol, intW m, float[] w, int _w_offset, float[] z, int _z_offset, int ldz, float[] work, int _work_offset, int[] iwork, int _iwork_offset, int[] ifail, int _ifail_offset, intW info)
/* 6976:     */   {
/* 6977:6281 */     Sspevx.sspevx(jobz, range, uplo, n, ap, _ap_offset, vl, vu, il, iu, abstol, m, w, _w_offset, z, _z_offset, ldz, work, _work_offset, iwork, _iwork_offset, ifail, _ifail_offset, info);
/* 6978:     */   }
/* 6979:     */   
/* 6980:     */   public void sspgst(int itype, String uplo, int n, float[] ap, float[] bp, intW info)
/* 6981:     */   {
/* 6982:6286 */     Sspgst.sspgst(itype, uplo, n, ap, 0, bp, 0, info);
/* 6983:     */   }
/* 6984:     */   
/* 6985:     */   public void sspgst(int itype, String uplo, int n, float[] ap, int _ap_offset, float[] bp, int _bp_offset, intW info)
/* 6986:     */   {
/* 6987:6291 */     Sspgst.sspgst(itype, uplo, n, ap, _ap_offset, bp, _bp_offset, info);
/* 6988:     */   }
/* 6989:     */   
/* 6990:     */   public void sspgv(int itype, String jobz, String uplo, int n, float[] ap, float[] bp, float[] w, float[] z, int ldz, float[] work, intW info)
/* 6991:     */   {
/* 6992:6296 */     Sspgv.sspgv(itype, jobz, uplo, n, ap, 0, bp, 0, w, 0, z, 0, ldz, work, 0, info);
/* 6993:     */   }
/* 6994:     */   
/* 6995:     */   public void sspgv(int itype, String jobz, String uplo, int n, float[] ap, int _ap_offset, float[] bp, int _bp_offset, float[] w, int _w_offset, float[] z, int _z_offset, int ldz, float[] work, int _work_offset, intW info)
/* 6996:     */   {
/* 6997:6301 */     Sspgv.sspgv(itype, jobz, uplo, n, ap, _ap_offset, bp, _bp_offset, w, _w_offset, z, _z_offset, ldz, work, _work_offset, info);
/* 6998:     */   }
/* 6999:     */   
/* 7000:     */   public void sspgvd(int itype, String jobz, String uplo, int n, float[] ap, float[] bp, float[] w, float[] z, int ldz, float[] work, int lwork, int[] iwork, int liwork, intW info)
/* 7001:     */   {
/* 7002:6306 */     Sspgvd.sspgvd(itype, jobz, uplo, n, ap, 0, bp, 0, w, 0, z, 0, ldz, work, 0, lwork, iwork, 0, liwork, info);
/* 7003:     */   }
/* 7004:     */   
/* 7005:     */   public void sspgvd(int itype, String jobz, String uplo, int n, float[] ap, int _ap_offset, float[] bp, int _bp_offset, float[] w, int _w_offset, float[] z, int _z_offset, int ldz, float[] work, int _work_offset, int lwork, int[] iwork, int _iwork_offset, int liwork, intW info)
/* 7006:     */   {
/* 7007:6311 */     Sspgvd.sspgvd(itype, jobz, uplo, n, ap, _ap_offset, bp, _bp_offset, w, _w_offset, z, _z_offset, ldz, work, _work_offset, lwork, iwork, _iwork_offset, liwork, info);
/* 7008:     */   }
/* 7009:     */   
/* 7010:     */   public void sspgvx(int itype, String jobz, String range, String uplo, int n, float[] ap, float[] bp, float vl, float vu, int il, int iu, float abstol, intW m, float[] w, float[] z, int ldz, float[] work, int[] iwork, int[] ifail, intW info)
/* 7011:     */   {
/* 7012:6316 */     Sspgvx.sspgvx(itype, jobz, range, uplo, n, ap, 0, bp, 0, vl, vu, il, iu, abstol, m, w, 0, z, 0, ldz, work, 0, iwork, 0, ifail, 0, info);
/* 7013:     */   }
/* 7014:     */   
/* 7015:     */   public void sspgvx(int itype, String jobz, String range, String uplo, int n, float[] ap, int _ap_offset, float[] bp, int _bp_offset, float vl, float vu, int il, int iu, float abstol, intW m, float[] w, int _w_offset, float[] z, int _z_offset, int ldz, float[] work, int _work_offset, int[] iwork, int _iwork_offset, int[] ifail, int _ifail_offset, intW info)
/* 7016:     */   {
/* 7017:6321 */     Sspgvx.sspgvx(itype, jobz, range, uplo, n, ap, _ap_offset, bp, _bp_offset, vl, vu, il, iu, abstol, m, w, _w_offset, z, _z_offset, ldz, work, _work_offset, iwork, _iwork_offset, ifail, _ifail_offset, info);
/* 7018:     */   }
/* 7019:     */   
/* 7020:     */   public void ssprfs(String uplo, int n, int nrhs, float[] ap, float[] afp, int[] ipiv, float[] b, int ldb, float[] x, int ldx, float[] ferr, float[] berr, float[] work, int[] iwork, intW info)
/* 7021:     */   {
/* 7022:6326 */     Ssprfs.ssprfs(uplo, n, nrhs, ap, 0, afp, 0, ipiv, 0, b, 0, ldb, x, 0, ldx, ferr, 0, berr, 0, work, 0, iwork, 0, info);
/* 7023:     */   }
/* 7024:     */   
/* 7025:     */   public void ssprfs(String uplo, int n, int nrhs, float[] ap, int _ap_offset, float[] afp, int _afp_offset, int[] ipiv, int _ipiv_offset, float[] b, int _b_offset, int ldb, float[] x, int _x_offset, int ldx, float[] ferr, int _ferr_offset, float[] berr, int _berr_offset, float[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 7026:     */   {
/* 7027:6331 */     Ssprfs.ssprfs(uplo, n, nrhs, ap, _ap_offset, afp, _afp_offset, ipiv, _ipiv_offset, b, _b_offset, ldb, x, _x_offset, ldx, ferr, _ferr_offset, berr, _berr_offset, work, _work_offset, iwork, _iwork_offset, info);
/* 7028:     */   }
/* 7029:     */   
/* 7030:     */   public void sspsv(String uplo, int n, int nrhs, float[] ap, int[] ipiv, float[] b, int ldb, intW info)
/* 7031:     */   {
/* 7032:6336 */     Sspsv.sspsv(uplo, n, nrhs, ap, 0, ipiv, 0, b, 0, ldb, info);
/* 7033:     */   }
/* 7034:     */   
/* 7035:     */   public void sspsv(String uplo, int n, int nrhs, float[] ap, int _ap_offset, int[] ipiv, int _ipiv_offset, float[] b, int _b_offset, int ldb, intW info)
/* 7036:     */   {
/* 7037:6341 */     Sspsv.sspsv(uplo, n, nrhs, ap, _ap_offset, ipiv, _ipiv_offset, b, _b_offset, ldb, info);
/* 7038:     */   }
/* 7039:     */   
/* 7040:     */   public void sspsvx(String fact, String uplo, int n, int nrhs, float[] ap, float[] afp, int[] ipiv, float[] b, int ldb, float[] x, int ldx, floatW rcond, float[] ferr, float[] berr, float[] work, int[] iwork, intW info)
/* 7041:     */   {
/* 7042:6346 */     Sspsvx.sspsvx(fact, uplo, n, nrhs, ap, 0, afp, 0, ipiv, 0, b, 0, ldb, x, 0, ldx, rcond, ferr, 0, berr, 0, work, 0, iwork, 0, info);
/* 7043:     */   }
/* 7044:     */   
/* 7045:     */   public void sspsvx(String fact, String uplo, int n, int nrhs, float[] ap, int _ap_offset, float[] afp, int _afp_offset, int[] ipiv, int _ipiv_offset, float[] b, int _b_offset, int ldb, float[] x, int _x_offset, int ldx, floatW rcond, float[] ferr, int _ferr_offset, float[] berr, int _berr_offset, float[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 7046:     */   {
/* 7047:6351 */     Sspsvx.sspsvx(fact, uplo, n, nrhs, ap, _ap_offset, afp, _afp_offset, ipiv, _ipiv_offset, b, _b_offset, ldb, x, _x_offset, ldx, rcond, ferr, _ferr_offset, berr, _berr_offset, work, _work_offset, iwork, _iwork_offset, info);
/* 7048:     */   }
/* 7049:     */   
/* 7050:     */   public void ssptrd(String uplo, int n, float[] ap, float[] d, float[] e, float[] tau, intW info)
/* 7051:     */   {
/* 7052:6356 */     Ssptrd.ssptrd(uplo, n, ap, 0, d, 0, e, 0, tau, 0, info);
/* 7053:     */   }
/* 7054:     */   
/* 7055:     */   public void ssptrd(String uplo, int n, float[] ap, int _ap_offset, float[] d, int _d_offset, float[] e, int _e_offset, float[] tau, int _tau_offset, intW info)
/* 7056:     */   {
/* 7057:6361 */     Ssptrd.ssptrd(uplo, n, ap, _ap_offset, d, _d_offset, e, _e_offset, tau, _tau_offset, info);
/* 7058:     */   }
/* 7059:     */   
/* 7060:     */   public void ssptrf(String uplo, int n, float[] ap, int[] ipiv, intW info)
/* 7061:     */   {
/* 7062:6366 */     Ssptrf.ssptrf(uplo, n, ap, 0, ipiv, 0, info);
/* 7063:     */   }
/* 7064:     */   
/* 7065:     */   public void ssptrf(String uplo, int n, float[] ap, int _ap_offset, int[] ipiv, int _ipiv_offset, intW info)
/* 7066:     */   {
/* 7067:6371 */     Ssptrf.ssptrf(uplo, n, ap, _ap_offset, ipiv, _ipiv_offset, info);
/* 7068:     */   }
/* 7069:     */   
/* 7070:     */   public void ssptri(String uplo, int n, float[] ap, int[] ipiv, float[] work, intW info)
/* 7071:     */   {
/* 7072:6376 */     Ssptri.ssptri(uplo, n, ap, 0, ipiv, 0, work, 0, info);
/* 7073:     */   }
/* 7074:     */   
/* 7075:     */   public void ssptri(String uplo, int n, float[] ap, int _ap_offset, int[] ipiv, int _ipiv_offset, float[] work, int _work_offset, intW info)
/* 7076:     */   {
/* 7077:6381 */     Ssptri.ssptri(uplo, n, ap, _ap_offset, ipiv, _ipiv_offset, work, _work_offset, info);
/* 7078:     */   }
/* 7079:     */   
/* 7080:     */   public void ssptrs(String uplo, int n, int nrhs, float[] ap, int[] ipiv, float[] b, int ldb, intW info)
/* 7081:     */   {
/* 7082:6386 */     Ssptrs.ssptrs(uplo, n, nrhs, ap, 0, ipiv, 0, b, 0, ldb, info);
/* 7083:     */   }
/* 7084:     */   
/* 7085:     */   public void ssptrs(String uplo, int n, int nrhs, float[] ap, int _ap_offset, int[] ipiv, int _ipiv_offset, float[] b, int _b_offset, int ldb, intW info)
/* 7086:     */   {
/* 7087:6391 */     Ssptrs.ssptrs(uplo, n, nrhs, ap, _ap_offset, ipiv, _ipiv_offset, b, _b_offset, ldb, info);
/* 7088:     */   }
/* 7089:     */   
/* 7090:     */   public void sstebz(String range, String order, int n, float vl, float vu, int il, int iu, float abstol, float[] d, float[] e, intW m, intW nsplit, float[] w, int[] iblock, int[] isplit, float[] work, int[] iwork, intW info)
/* 7091:     */   {
/* 7092:6396 */     Sstebz.sstebz(range, order, n, vl, vu, il, iu, abstol, d, 0, e, 0, m, nsplit, w, 0, iblock, 0, isplit, 0, work, 0, iwork, 0, info);
/* 7093:     */   }
/* 7094:     */   
/* 7095:     */   public void sstebz(String range, String order, int n, float vl, float vu, int il, int iu, float abstol, float[] d, int _d_offset, float[] e, int _e_offset, intW m, intW nsplit, float[] w, int _w_offset, int[] iblock, int _iblock_offset, int[] isplit, int _isplit_offset, float[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 7096:     */   {
/* 7097:6401 */     Sstebz.sstebz(range, order, n, vl, vu, il, iu, abstol, d, _d_offset, e, _e_offset, m, nsplit, w, _w_offset, iblock, _iblock_offset, isplit, _isplit_offset, work, _work_offset, iwork, _iwork_offset, info);
/* 7098:     */   }
/* 7099:     */   
/* 7100:     */   public void sstedc(String compz, int n, float[] d, float[] e, float[] z, int ldz, float[] work, int lwork, int[] iwork, int liwork, intW info)
/* 7101:     */   {
/* 7102:6406 */     Sstedc.sstedc(compz, n, d, 0, e, 0, z, 0, ldz, work, 0, lwork, iwork, 0, liwork, info);
/* 7103:     */   }
/* 7104:     */   
/* 7105:     */   public void sstedc(String compz, int n, float[] d, int _d_offset, float[] e, int _e_offset, float[] z, int _z_offset, int ldz, float[] work, int _work_offset, int lwork, int[] iwork, int _iwork_offset, int liwork, intW info)
/* 7106:     */   {
/* 7107:6411 */     Sstedc.sstedc(compz, n, d, _d_offset, e, _e_offset, z, _z_offset, ldz, work, _work_offset, lwork, iwork, _iwork_offset, liwork, info);
/* 7108:     */   }
/* 7109:     */   
/* 7110:     */   public void sstegr(String jobz, String range, int n, float[] d, float[] e, float vl, float vu, int il, int iu, float abstol, intW m, float[] w, float[] z, int ldz, int[] isuppz, float[] work, int lwork, int[] iwork, int liwork, intW info)
/* 7111:     */   {
/* 7112:6416 */     Sstegr.sstegr(jobz, range, n, d, 0, e, 0, vl, vu, il, iu, abstol, m, w, 0, z, 0, ldz, isuppz, 0, work, 0, lwork, iwork, 0, liwork, info);
/* 7113:     */   }
/* 7114:     */   
/* 7115:     */   public void sstegr(String jobz, String range, int n, float[] d, int _d_offset, float[] e, int _e_offset, float vl, float vu, int il, int iu, float abstol, intW m, float[] w, int _w_offset, float[] z, int _z_offset, int ldz, int[] isuppz, int _isuppz_offset, float[] work, int _work_offset, int lwork, int[] iwork, int _iwork_offset, int liwork, intW info)
/* 7116:     */   {
/* 7117:6421 */     Sstegr.sstegr(jobz, range, n, d, _d_offset, e, _e_offset, vl, vu, il, iu, abstol, m, w, _w_offset, z, _z_offset, ldz, isuppz, _isuppz_offset, work, _work_offset, lwork, iwork, _iwork_offset, liwork, info);
/* 7118:     */   }
/* 7119:     */   
/* 7120:     */   public void sstein(int n, float[] d, float[] e, int m, float[] w, int[] iblock, int[] isplit, float[] z, int ldz, float[] work, int[] iwork, int[] ifail, intW info)
/* 7121:     */   {
/* 7122:6426 */     Sstein.sstein(n, d, 0, e, 0, m, w, 0, iblock, 0, isplit, 0, z, 0, ldz, work, 0, iwork, 0, ifail, 0, info);
/* 7123:     */   }
/* 7124:     */   
/* 7125:     */   public void sstein(int n, float[] d, int _d_offset, float[] e, int _e_offset, int m, float[] w, int _w_offset, int[] iblock, int _iblock_offset, int[] isplit, int _isplit_offset, float[] z, int _z_offset, int ldz, float[] work, int _work_offset, int[] iwork, int _iwork_offset, int[] ifail, int _ifail_offset, intW info)
/* 7126:     */   {
/* 7127:6431 */     Sstein.sstein(n, d, _d_offset, e, _e_offset, m, w, _w_offset, iblock, _iblock_offset, isplit, _isplit_offset, z, _z_offset, ldz, work, _work_offset, iwork, _iwork_offset, ifail, _ifail_offset, info);
/* 7128:     */   }
/* 7129:     */   
/* 7130:     */   public void sstemr(String jobz, String range, int n, float[] d, float[] e, float vl, float vu, int il, int iu, intW m, float[] w, float[] z, int ldz, int nzc, int[] isuppz, booleanW tryrac, float[] work, int lwork, int[] iwork, int liwork, intW info)
/* 7131:     */   {
/* 7132:6436 */     Sstemr.sstemr(jobz, range, n, d, 0, e, 0, vl, vu, il, iu, m, w, 0, z, 0, ldz, nzc, isuppz, 0, tryrac, work, 0, lwork, iwork, 0, liwork, info);
/* 7133:     */   }
/* 7134:     */   
/* 7135:     */   public void sstemr(String jobz, String range, int n, float[] d, int _d_offset, float[] e, int _e_offset, float vl, float vu, int il, int iu, intW m, float[] w, int _w_offset, float[] z, int _z_offset, int ldz, int nzc, int[] isuppz, int _isuppz_offset, booleanW tryrac, float[] work, int _work_offset, int lwork, int[] iwork, int _iwork_offset, int liwork, intW info)
/* 7136:     */   {
/* 7137:6441 */     Sstemr.sstemr(jobz, range, n, d, _d_offset, e, _e_offset, vl, vu, il, iu, m, w, _w_offset, z, _z_offset, ldz, nzc, isuppz, _isuppz_offset, tryrac, work, _work_offset, lwork, iwork, _iwork_offset, liwork, info);
/* 7138:     */   }
/* 7139:     */   
/* 7140:     */   public void ssteqr(String compz, int n, float[] d, float[] e, float[] z, int ldz, float[] work, intW info)
/* 7141:     */   {
/* 7142:6446 */     Ssteqr.ssteqr(compz, n, d, 0, e, 0, z, 0, ldz, work, 0, info);
/* 7143:     */   }
/* 7144:     */   
/* 7145:     */   public void ssteqr(String compz, int n, float[] d, int _d_offset, float[] e, int _e_offset, float[] z, int _z_offset, int ldz, float[] work, int _work_offset, intW info)
/* 7146:     */   {
/* 7147:6451 */     Ssteqr.ssteqr(compz, n, d, _d_offset, e, _e_offset, z, _z_offset, ldz, work, _work_offset, info);
/* 7148:     */   }
/* 7149:     */   
/* 7150:     */   public void ssterf(int n, float[] d, float[] e, intW info)
/* 7151:     */   {
/* 7152:6456 */     Ssterf.ssterf(n, d, 0, e, 0, info);
/* 7153:     */   }
/* 7154:     */   
/* 7155:     */   public void ssterf(int n, float[] d, int _d_offset, float[] e, int _e_offset, intW info)
/* 7156:     */   {
/* 7157:6461 */     Ssterf.ssterf(n, d, _d_offset, e, _e_offset, info);
/* 7158:     */   }
/* 7159:     */   
/* 7160:     */   public void sstev(String jobz, int n, float[] d, float[] e, float[] z, int ldz, float[] work, intW info)
/* 7161:     */   {
/* 7162:6466 */     Sstev.sstev(jobz, n, d, 0, e, 0, z, 0, ldz, work, 0, info);
/* 7163:     */   }
/* 7164:     */   
/* 7165:     */   public void sstev(String jobz, int n, float[] d, int _d_offset, float[] e, int _e_offset, float[] z, int _z_offset, int ldz, float[] work, int _work_offset, intW info)
/* 7166:     */   {
/* 7167:6471 */     Sstev.sstev(jobz, n, d, _d_offset, e, _e_offset, z, _z_offset, ldz, work, _work_offset, info);
/* 7168:     */   }
/* 7169:     */   
/* 7170:     */   public void sstevd(String jobz, int n, float[] d, float[] e, float[] z, int ldz, float[] work, int lwork, int[] iwork, int liwork, intW info)
/* 7171:     */   {
/* 7172:6476 */     Sstevd.sstevd(jobz, n, d, 0, e, 0, z, 0, ldz, work, 0, lwork, iwork, 0, liwork, info);
/* 7173:     */   }
/* 7174:     */   
/* 7175:     */   public void sstevd(String jobz, int n, float[] d, int _d_offset, float[] e, int _e_offset, float[] z, int _z_offset, int ldz, float[] work, int _work_offset, int lwork, int[] iwork, int _iwork_offset, int liwork, intW info)
/* 7176:     */   {
/* 7177:6481 */     Sstevd.sstevd(jobz, n, d, _d_offset, e, _e_offset, z, _z_offset, ldz, work, _work_offset, lwork, iwork, _iwork_offset, liwork, info);
/* 7178:     */   }
/* 7179:     */   
/* 7180:     */   public void sstevr(String jobz, String range, int n, float[] d, float[] e, float vl, float vu, int il, int iu, float abstol, intW m, float[] w, float[] z, int ldz, int[] isuppz, float[] work, int lwork, int[] iwork, int liwork, intW info)
/* 7181:     */   {
/* 7182:6486 */     Sstevr.sstevr(jobz, range, n, d, 0, e, 0, vl, vu, il, iu, abstol, m, w, 0, z, 0, ldz, isuppz, 0, work, 0, lwork, iwork, 0, liwork, info);
/* 7183:     */   }
/* 7184:     */   
/* 7185:     */   public void sstevr(String jobz, String range, int n, float[] d, int _d_offset, float[] e, int _e_offset, float vl, float vu, int il, int iu, float abstol, intW m, float[] w, int _w_offset, float[] z, int _z_offset, int ldz, int[] isuppz, int _isuppz_offset, float[] work, int _work_offset, int lwork, int[] iwork, int _iwork_offset, int liwork, intW info)
/* 7186:     */   {
/* 7187:6491 */     Sstevr.sstevr(jobz, range, n, d, _d_offset, e, _e_offset, vl, vu, il, iu, abstol, m, w, _w_offset, z, _z_offset, ldz, isuppz, _isuppz_offset, work, _work_offset, lwork, iwork, _iwork_offset, liwork, info);
/* 7188:     */   }
/* 7189:     */   
/* 7190:     */   public void sstevx(String jobz, String range, int n, float[] d, float[] e, float vl, float vu, int il, int iu, float abstol, intW m, float[] w, float[] z, int ldz, float[] work, int[] iwork, int[] ifail, intW info)
/* 7191:     */   {
/* 7192:6496 */     Sstevx.sstevx(jobz, range, n, d, 0, e, 0, vl, vu, il, iu, abstol, m, w, 0, z, 0, ldz, work, 0, iwork, 0, ifail, 0, info);
/* 7193:     */   }
/* 7194:     */   
/* 7195:     */   public void sstevx(String jobz, String range, int n, float[] d, int _d_offset, float[] e, int _e_offset, float vl, float vu, int il, int iu, float abstol, intW m, float[] w, int _w_offset, float[] z, int _z_offset, int ldz, float[] work, int _work_offset, int[] iwork, int _iwork_offset, int[] ifail, int _ifail_offset, intW info)
/* 7196:     */   {
/* 7197:6501 */     Sstevx.sstevx(jobz, range, n, d, _d_offset, e, _e_offset, vl, vu, il, iu, abstol, m, w, _w_offset, z, _z_offset, ldz, work, _work_offset, iwork, _iwork_offset, ifail, _ifail_offset, info);
/* 7198:     */   }
/* 7199:     */   
/* 7200:     */   public void ssycon(String uplo, int n, float[] a, int lda, int[] ipiv, float anorm, floatW rcond, float[] work, int[] iwork, intW info)
/* 7201:     */   {
/* 7202:6506 */     Ssycon.ssycon(uplo, n, a, 0, lda, ipiv, 0, anorm, rcond, work, 0, iwork, 0, info);
/* 7203:     */   }
/* 7204:     */   
/* 7205:     */   public void ssycon(String uplo, int n, float[] a, int _a_offset, int lda, int[] ipiv, int _ipiv_offset, float anorm, floatW rcond, float[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 7206:     */   {
/* 7207:6511 */     Ssycon.ssycon(uplo, n, a, _a_offset, lda, ipiv, _ipiv_offset, anorm, rcond, work, _work_offset, iwork, _iwork_offset, info);
/* 7208:     */   }
/* 7209:     */   
/* 7210:     */   public void ssyev(String jobz, String uplo, int n, float[] a, int lda, float[] w, float[] work, int lwork, intW info)
/* 7211:     */   {
/* 7212:6516 */     Ssyev.ssyev(jobz, uplo, n, a, 0, lda, w, 0, work, 0, lwork, info);
/* 7213:     */   }
/* 7214:     */   
/* 7215:     */   public void ssyev(String jobz, String uplo, int n, float[] a, int _a_offset, int lda, float[] w, int _w_offset, float[] work, int _work_offset, int lwork, intW info)
/* 7216:     */   {
/* 7217:6521 */     Ssyev.ssyev(jobz, uplo, n, a, _a_offset, lda, w, _w_offset, work, _work_offset, lwork, info);
/* 7218:     */   }
/* 7219:     */   
/* 7220:     */   public void ssyevd(String jobz, String uplo, int n, float[] a, int lda, float[] w, float[] work, int lwork, int[] iwork, int liwork, intW info)
/* 7221:     */   {
/* 7222:6526 */     Ssyevd.ssyevd(jobz, uplo, n, a, 0, lda, w, 0, work, 0, lwork, iwork, 0, liwork, info);
/* 7223:     */   }
/* 7224:     */   
/* 7225:     */   public void ssyevd(String jobz, String uplo, int n, float[] a, int _a_offset, int lda, float[] w, int _w_offset, float[] work, int _work_offset, int lwork, int[] iwork, int _iwork_offset, int liwork, intW info)
/* 7226:     */   {
/* 7227:6531 */     Ssyevd.ssyevd(jobz, uplo, n, a, _a_offset, lda, w, _w_offset, work, _work_offset, lwork, iwork, _iwork_offset, liwork, info);
/* 7228:     */   }
/* 7229:     */   
/* 7230:     */   public void ssyevr(String jobz, String range, String uplo, int n, float[] a, int lda, float vl, float vu, int il, int iu, float abstol, intW m, float[] w, float[] z, int ldz, int[] isuppz, float[] work, int lwork, int[] iwork, int liwork, intW info)
/* 7231:     */   {
/* 7232:6536 */     Ssyevr.ssyevr(jobz, range, uplo, n, a, 0, lda, vl, vu, il, iu, abstol, m, w, 0, z, 0, ldz, isuppz, 0, work, 0, lwork, iwork, 0, liwork, info);
/* 7233:     */   }
/* 7234:     */   
/* 7235:     */   public void ssyevr(String jobz, String range, String uplo, int n, float[] a, int _a_offset, int lda, float vl, float vu, int il, int iu, float abstol, intW m, float[] w, int _w_offset, float[] z, int _z_offset, int ldz, int[] isuppz, int _isuppz_offset, float[] work, int _work_offset, int lwork, int[] iwork, int _iwork_offset, int liwork, intW info)
/* 7236:     */   {
/* 7237:6541 */     Ssyevr.ssyevr(jobz, range, uplo, n, a, _a_offset, lda, vl, vu, il, iu, abstol, m, w, _w_offset, z, _z_offset, ldz, isuppz, _isuppz_offset, work, _work_offset, lwork, iwork, _iwork_offset, liwork, info);
/* 7238:     */   }
/* 7239:     */   
/* 7240:     */   public void ssyevx(String jobz, String range, String uplo, int n, float[] a, int lda, float vl, float vu, int il, int iu, float abstol, intW m, float[] w, float[] z, int ldz, float[] work, int lwork, int[] iwork, int[] ifail, intW info)
/* 7241:     */   {
/* 7242:6546 */     Ssyevx.ssyevx(jobz, range, uplo, n, a, 0, lda, vl, vu, il, iu, abstol, m, w, 0, z, 0, ldz, work, 0, lwork, iwork, 0, ifail, 0, info);
/* 7243:     */   }
/* 7244:     */   
/* 7245:     */   public void ssyevx(String jobz, String range, String uplo, int n, float[] a, int _a_offset, int lda, float vl, float vu, int il, int iu, float abstol, intW m, float[] w, int _w_offset, float[] z, int _z_offset, int ldz, float[] work, int _work_offset, int lwork, int[] iwork, int _iwork_offset, int[] ifail, int _ifail_offset, intW info)
/* 7246:     */   {
/* 7247:6551 */     Ssyevx.ssyevx(jobz, range, uplo, n, a, _a_offset, lda, vl, vu, il, iu, abstol, m, w, _w_offset, z, _z_offset, ldz, work, _work_offset, lwork, iwork, _iwork_offset, ifail, _ifail_offset, info);
/* 7248:     */   }
/* 7249:     */   
/* 7250:     */   public void ssygs2(int itype, String uplo, int n, float[] a, int lda, float[] b, int ldb, intW info)
/* 7251:     */   {
/* 7252:6556 */     Ssygs2.ssygs2(itype, uplo, n, a, 0, lda, b, 0, ldb, info);
/* 7253:     */   }
/* 7254:     */   
/* 7255:     */   public void ssygs2(int itype, String uplo, int n, float[] a, int _a_offset, int lda, float[] b, int _b_offset, int ldb, intW info)
/* 7256:     */   {
/* 7257:6561 */     Ssygs2.ssygs2(itype, uplo, n, a, _a_offset, lda, b, _b_offset, ldb, info);
/* 7258:     */   }
/* 7259:     */   
/* 7260:     */   public void ssygst(int itype, String uplo, int n, float[] a, int lda, float[] b, int ldb, intW info)
/* 7261:     */   {
/* 7262:6566 */     Ssygst.ssygst(itype, uplo, n, a, 0, lda, b, 0, ldb, info);
/* 7263:     */   }
/* 7264:     */   
/* 7265:     */   public void ssygst(int itype, String uplo, int n, float[] a, int _a_offset, int lda, float[] b, int _b_offset, int ldb, intW info)
/* 7266:     */   {
/* 7267:6571 */     Ssygst.ssygst(itype, uplo, n, a, _a_offset, lda, b, _b_offset, ldb, info);
/* 7268:     */   }
/* 7269:     */   
/* 7270:     */   public void ssygv(int itype, String jobz, String uplo, int n, float[] a, int lda, float[] b, int ldb, float[] w, float[] work, int lwork, intW info)
/* 7271:     */   {
/* 7272:6576 */     Ssygv.ssygv(itype, jobz, uplo, n, a, 0, lda, b, 0, ldb, w, 0, work, 0, lwork, info);
/* 7273:     */   }
/* 7274:     */   
/* 7275:     */   public void ssygv(int itype, String jobz, String uplo, int n, float[] a, int _a_offset, int lda, float[] b, int _b_offset, int ldb, float[] w, int _w_offset, float[] work, int _work_offset, int lwork, intW info)
/* 7276:     */   {
/* 7277:6581 */     Ssygv.ssygv(itype, jobz, uplo, n, a, _a_offset, lda, b, _b_offset, ldb, w, _w_offset, work, _work_offset, lwork, info);
/* 7278:     */   }
/* 7279:     */   
/* 7280:     */   public void ssygvd(int itype, String jobz, String uplo, int n, float[] a, int lda, float[] b, int ldb, float[] w, float[] work, int lwork, int[] iwork, int liwork, intW info)
/* 7281:     */   {
/* 7282:6586 */     Ssygvd.ssygvd(itype, jobz, uplo, n, a, 0, lda, b, 0, ldb, w, 0, work, 0, lwork, iwork, 0, liwork, info);
/* 7283:     */   }
/* 7284:     */   
/* 7285:     */   public void ssygvd(int itype, String jobz, String uplo, int n, float[] a, int _a_offset, int lda, float[] b, int _b_offset, int ldb, float[] w, int _w_offset, float[] work, int _work_offset, int lwork, int[] iwork, int _iwork_offset, int liwork, intW info)
/* 7286:     */   {
/* 7287:6591 */     Ssygvd.ssygvd(itype, jobz, uplo, n, a, _a_offset, lda, b, _b_offset, ldb, w, _w_offset, work, _work_offset, lwork, iwork, _iwork_offset, liwork, info);
/* 7288:     */   }
/* 7289:     */   
/* 7290:     */   public void ssygvx(int itype, String jobz, String range, String uplo, int n, float[] a, int lda, float[] b, int ldb, float vl, float vu, int il, int iu, float abstol, intW m, float[] w, float[] z, int ldz, float[] work, int lwork, int[] iwork, int[] ifail, intW info)
/* 7291:     */   {
/* 7292:6596 */     Ssygvx.ssygvx(itype, jobz, range, uplo, n, a, 0, lda, b, 0, ldb, vl, vu, il, iu, abstol, m, w, 0, z, 0, ldz, work, 0, lwork, iwork, 0, ifail, 0, info);
/* 7293:     */   }
/* 7294:     */   
/* 7295:     */   public void ssygvx(int itype, String jobz, String range, String uplo, int n, float[] a, int _a_offset, int lda, float[] b, int _b_offset, int ldb, float vl, float vu, int il, int iu, float abstol, intW m, float[] w, int _w_offset, float[] z, int _z_offset, int ldz, float[] work, int _work_offset, int lwork, int[] iwork, int _iwork_offset, int[] ifail, int _ifail_offset, intW info)
/* 7296:     */   {
/* 7297:6601 */     Ssygvx.ssygvx(itype, jobz, range, uplo, n, a, _a_offset, lda, b, _b_offset, ldb, vl, vu, il, iu, abstol, m, w, _w_offset, z, _z_offset, ldz, work, _work_offset, lwork, iwork, _iwork_offset, ifail, _ifail_offset, info);
/* 7298:     */   }
/* 7299:     */   
/* 7300:     */   public void ssyrfs(String uplo, int n, int nrhs, float[] a, int lda, float[] af, int ldaf, int[] ipiv, float[] b, int ldb, float[] x, int ldx, float[] ferr, float[] berr, float[] work, int[] iwork, intW info)
/* 7301:     */   {
/* 7302:6606 */     Ssyrfs.ssyrfs(uplo, n, nrhs, a, 0, lda, af, 0, ldaf, ipiv, 0, b, 0, ldb, x, 0, ldx, ferr, 0, berr, 0, work, 0, iwork, 0, info);
/* 7303:     */   }
/* 7304:     */   
/* 7305:     */   public void ssyrfs(String uplo, int n, int nrhs, float[] a, int _a_offset, int lda, float[] af, int _af_offset, int ldaf, int[] ipiv, int _ipiv_offset, float[] b, int _b_offset, int ldb, float[] x, int _x_offset, int ldx, float[] ferr, int _ferr_offset, float[] berr, int _berr_offset, float[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 7306:     */   {
/* 7307:6611 */     Ssyrfs.ssyrfs(uplo, n, nrhs, a, _a_offset, lda, af, _af_offset, ldaf, ipiv, _ipiv_offset, b, _b_offset, ldb, x, _x_offset, ldx, ferr, _ferr_offset, berr, _berr_offset, work, _work_offset, iwork, _iwork_offset, info);
/* 7308:     */   }
/* 7309:     */   
/* 7310:     */   public void ssysv(String uplo, int n, int nrhs, float[] a, int lda, int[] ipiv, float[] b, int ldb, float[] work, int lwork, intW info)
/* 7311:     */   {
/* 7312:6616 */     Ssysv.ssysv(uplo, n, nrhs, a, 0, lda, ipiv, 0, b, 0, ldb, work, 0, lwork, info);
/* 7313:     */   }
/* 7314:     */   
/* 7315:     */   public void ssysv(String uplo, int n, int nrhs, float[] a, int _a_offset, int lda, int[] ipiv, int _ipiv_offset, float[] b, int _b_offset, int ldb, float[] work, int _work_offset, int lwork, intW info)
/* 7316:     */   {
/* 7317:6621 */     Ssysv.ssysv(uplo, n, nrhs, a, _a_offset, lda, ipiv, _ipiv_offset, b, _b_offset, ldb, work, _work_offset, lwork, info);
/* 7318:     */   }
/* 7319:     */   
/* 7320:     */   public void ssysvx(String fact, String uplo, int n, int nrhs, float[] a, int lda, float[] af, int ldaf, int[] ipiv, float[] b, int ldb, float[] x, int ldx, floatW rcond, float[] ferr, float[] berr, float[] work, int lwork, int[] iwork, intW info)
/* 7321:     */   {
/* 7322:6626 */     Ssysvx.ssysvx(fact, uplo, n, nrhs, a, 0, lda, af, 0, ldaf, ipiv, 0, b, 0, ldb, x, 0, ldx, rcond, ferr, 0, berr, 0, work, 0, lwork, iwork, 0, info);
/* 7323:     */   }
/* 7324:     */   
/* 7325:     */   public void ssysvx(String fact, String uplo, int n, int nrhs, float[] a, int _a_offset, int lda, float[] af, int _af_offset, int ldaf, int[] ipiv, int _ipiv_offset, float[] b, int _b_offset, int ldb, float[] x, int _x_offset, int ldx, floatW rcond, float[] ferr, int _ferr_offset, float[] berr, int _berr_offset, float[] work, int _work_offset, int lwork, int[] iwork, int _iwork_offset, intW info)
/* 7326:     */   {
/* 7327:6631 */     Ssysvx.ssysvx(fact, uplo, n, nrhs, a, _a_offset, lda, af, _af_offset, ldaf, ipiv, _ipiv_offset, b, _b_offset, ldb, x, _x_offset, ldx, rcond, ferr, _ferr_offset, berr, _berr_offset, work, _work_offset, lwork, iwork, _iwork_offset, info);
/* 7328:     */   }
/* 7329:     */   
/* 7330:     */   public void ssytd2(String uplo, int n, float[] a, int lda, float[] d, float[] e, float[] tau, intW info)
/* 7331:     */   {
/* 7332:6636 */     Ssytd2.ssytd2(uplo, n, a, 0, lda, d, 0, e, 0, tau, 0, info);
/* 7333:     */   }
/* 7334:     */   
/* 7335:     */   public void ssytd2(String uplo, int n, float[] a, int _a_offset, int lda, float[] d, int _d_offset, float[] e, int _e_offset, float[] tau, int _tau_offset, intW info)
/* 7336:     */   {
/* 7337:6641 */     Ssytd2.ssytd2(uplo, n, a, _a_offset, lda, d, _d_offset, e, _e_offset, tau, _tau_offset, info);
/* 7338:     */   }
/* 7339:     */   
/* 7340:     */   public void ssytf2(String uplo, int n, float[] a, int lda, int[] ipiv, intW info)
/* 7341:     */   {
/* 7342:6646 */     Ssytf2.ssytf2(uplo, n, a, 0, lda, ipiv, 0, info);
/* 7343:     */   }
/* 7344:     */   
/* 7345:     */   public void ssytf2(String uplo, int n, float[] a, int _a_offset, int lda, int[] ipiv, int _ipiv_offset, intW info)
/* 7346:     */   {
/* 7347:6651 */     Ssytf2.ssytf2(uplo, n, a, _a_offset, lda, ipiv, _ipiv_offset, info);
/* 7348:     */   }
/* 7349:     */   
/* 7350:     */   public void ssytrd(String uplo, int n, float[] a, int lda, float[] d, float[] e, float[] tau, float[] work, int lwork, intW info)
/* 7351:     */   {
/* 7352:6656 */     Ssytrd.ssytrd(uplo, n, a, 0, lda, d, 0, e, 0, tau, 0, work, 0, lwork, info);
/* 7353:     */   }
/* 7354:     */   
/* 7355:     */   public void ssytrd(String uplo, int n, float[] a, int _a_offset, int lda, float[] d, int _d_offset, float[] e, int _e_offset, float[] tau, int _tau_offset, float[] work, int _work_offset, int lwork, intW info)
/* 7356:     */   {
/* 7357:6661 */     Ssytrd.ssytrd(uplo, n, a, _a_offset, lda, d, _d_offset, e, _e_offset, tau, _tau_offset, work, _work_offset, lwork, info);
/* 7358:     */   }
/* 7359:     */   
/* 7360:     */   public void ssytrf(String uplo, int n, float[] a, int lda, int[] ipiv, float[] work, int lwork, intW info)
/* 7361:     */   {
/* 7362:6666 */     Ssytrf.ssytrf(uplo, n, a, 0, lda, ipiv, 0, work, 0, lwork, info);
/* 7363:     */   }
/* 7364:     */   
/* 7365:     */   public void ssytrf(String uplo, int n, float[] a, int _a_offset, int lda, int[] ipiv, int _ipiv_offset, float[] work, int _work_offset, int lwork, intW info)
/* 7366:     */   {
/* 7367:6671 */     Ssytrf.ssytrf(uplo, n, a, _a_offset, lda, ipiv, _ipiv_offset, work, _work_offset, lwork, info);
/* 7368:     */   }
/* 7369:     */   
/* 7370:     */   public void ssytri(String uplo, int n, float[] a, int lda, int[] ipiv, float[] work, intW info)
/* 7371:     */   {
/* 7372:6676 */     Ssytri.ssytri(uplo, n, a, 0, lda, ipiv, 0, work, 0, info);
/* 7373:     */   }
/* 7374:     */   
/* 7375:     */   public void ssytri(String uplo, int n, float[] a, int _a_offset, int lda, int[] ipiv, int _ipiv_offset, float[] work, int _work_offset, intW info)
/* 7376:     */   {
/* 7377:6681 */     Ssytri.ssytri(uplo, n, a, _a_offset, lda, ipiv, _ipiv_offset, work, _work_offset, info);
/* 7378:     */   }
/* 7379:     */   
/* 7380:     */   public void ssytrs(String uplo, int n, int nrhs, float[] a, int lda, int[] ipiv, float[] b, int ldb, intW info)
/* 7381:     */   {
/* 7382:6686 */     Ssytrs.ssytrs(uplo, n, nrhs, a, 0, lda, ipiv, 0, b, 0, ldb, info);
/* 7383:     */   }
/* 7384:     */   
/* 7385:     */   public void ssytrs(String uplo, int n, int nrhs, float[] a, int _a_offset, int lda, int[] ipiv, int _ipiv_offset, float[] b, int _b_offset, int ldb, intW info)
/* 7386:     */   {
/* 7387:6691 */     Ssytrs.ssytrs(uplo, n, nrhs, a, _a_offset, lda, ipiv, _ipiv_offset, b, _b_offset, ldb, info);
/* 7388:     */   }
/* 7389:     */   
/* 7390:     */   public void stbcon(String norm, String uplo, String diag, int n, int kd, float[] ab, int ldab, floatW rcond, float[] work, int[] iwork, intW info)
/* 7391:     */   {
/* 7392:6696 */     Stbcon.stbcon(norm, uplo, diag, n, kd, ab, 0, ldab, rcond, work, 0, iwork, 0, info);
/* 7393:     */   }
/* 7394:     */   
/* 7395:     */   public void stbcon(String norm, String uplo, String diag, int n, int kd, float[] ab, int _ab_offset, int ldab, floatW rcond, float[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 7396:     */   {
/* 7397:6701 */     Stbcon.stbcon(norm, uplo, diag, n, kd, ab, _ab_offset, ldab, rcond, work, _work_offset, iwork, _iwork_offset, info);
/* 7398:     */   }
/* 7399:     */   
/* 7400:     */   public void stbrfs(String uplo, String trans, String diag, int n, int kd, int nrhs, float[] ab, int ldab, float[] b, int ldb, float[] x, int ldx, float[] ferr, float[] berr, float[] work, int[] iwork, intW info)
/* 7401:     */   {
/* 7402:6706 */     Stbrfs.stbrfs(uplo, trans, diag, n, kd, nrhs, ab, 0, ldab, b, 0, ldb, x, 0, ldx, ferr, 0, berr, 0, work, 0, iwork, 0, info);
/* 7403:     */   }
/* 7404:     */   
/* 7405:     */   public void stbrfs(String uplo, String trans, String diag, int n, int kd, int nrhs, float[] ab, int _ab_offset, int ldab, float[] b, int _b_offset, int ldb, float[] x, int _x_offset, int ldx, float[] ferr, int _ferr_offset, float[] berr, int _berr_offset, float[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 7406:     */   {
/* 7407:6711 */     Stbrfs.stbrfs(uplo, trans, diag, n, kd, nrhs, ab, _ab_offset, ldab, b, _b_offset, ldb, x, _x_offset, ldx, ferr, _ferr_offset, berr, _berr_offset, work, _work_offset, iwork, _iwork_offset, info);
/* 7408:     */   }
/* 7409:     */   
/* 7410:     */   public void stbtrs(String uplo, String trans, String diag, int n, int kd, int nrhs, float[] ab, int ldab, float[] b, int ldb, intW info)
/* 7411:     */   {
/* 7412:6716 */     Stbtrs.stbtrs(uplo, trans, diag, n, kd, nrhs, ab, 0, ldab, b, 0, ldb, info);
/* 7413:     */   }
/* 7414:     */   
/* 7415:     */   public void stbtrs(String uplo, String trans, String diag, int n, int kd, int nrhs, float[] ab, int _ab_offset, int ldab, float[] b, int _b_offset, int ldb, intW info)
/* 7416:     */   {
/* 7417:6721 */     Stbtrs.stbtrs(uplo, trans, diag, n, kd, nrhs, ab, _ab_offset, ldab, b, _b_offset, ldb, info);
/* 7418:     */   }
/* 7419:     */   
/* 7420:     */   public void stgevc(String side, String howmny, boolean[] select, int n, float[] s, int lds, float[] p, int ldp, float[] vl, int ldvl, float[] vr, int ldvr, int mm, intW m, float[] work, intW info)
/* 7421:     */   {
/* 7422:6726 */     Stgevc.stgevc(side, howmny, select, 0, n, s, 0, lds, p, 0, ldp, vl, 0, ldvl, vr, 0, ldvr, mm, m, work, 0, info);
/* 7423:     */   }
/* 7424:     */   
/* 7425:     */   public void stgevc(String side, String howmny, boolean[] select, int _select_offset, int n, float[] s, int _s_offset, int lds, float[] p, int _p_offset, int ldp, float[] vl, int _vl_offset, int ldvl, float[] vr, int _vr_offset, int ldvr, int mm, intW m, float[] work, int _work_offset, intW info)
/* 7426:     */   {
/* 7427:6731 */     Stgevc.stgevc(side, howmny, select, _select_offset, n, s, _s_offset, lds, p, _p_offset, ldp, vl, _vl_offset, ldvl, vr, _vr_offset, ldvr, mm, m, work, _work_offset, info);
/* 7428:     */   }
/* 7429:     */   
/* 7430:     */   public void stgex2(boolean wantq, boolean wantz, int n, float[] a, int lda, float[] b, int ldb, float[] q, int ldq, float[] z, int ldz, int j1, int n1, int n2, float[] work, int lwork, intW info)
/* 7431:     */   {
/* 7432:6736 */     Stgex2.stgex2(wantq, wantz, n, a, 0, lda, b, 0, ldb, q, 0, ldq, z, 0, ldz, j1, n1, n2, work, 0, lwork, info);
/* 7433:     */   }
/* 7434:     */   
/* 7435:     */   public void stgex2(boolean wantq, boolean wantz, int n, float[] a, int _a_offset, int lda, float[] b, int _b_offset, int ldb, float[] q, int _q_offset, int ldq, float[] z, int _z_offset, int ldz, int j1, int n1, int n2, float[] work, int _work_offset, int lwork, intW info)
/* 7436:     */   {
/* 7437:6741 */     Stgex2.stgex2(wantq, wantz, n, a, _a_offset, lda, b, _b_offset, ldb, q, _q_offset, ldq, z, _z_offset, ldz, j1, n1, n2, work, _work_offset, lwork, info);
/* 7438:     */   }
/* 7439:     */   
/* 7440:     */   public void stgexc(boolean wantq, boolean wantz, int n, float[] a, int lda, float[] b, int ldb, float[] q, int ldq, float[] z, int ldz, intW ifst, intW ilst, float[] work, int lwork, intW info)
/* 7441:     */   {
/* 7442:6746 */     Stgexc.stgexc(wantq, wantz, n, a, 0, lda, b, 0, ldb, q, 0, ldq, z, 0, ldz, ifst, ilst, work, 0, lwork, info);
/* 7443:     */   }
/* 7444:     */   
/* 7445:     */   public void stgexc(boolean wantq, boolean wantz, int n, float[] a, int _a_offset, int lda, float[] b, int _b_offset, int ldb, float[] q, int _q_offset, int ldq, float[] z, int _z_offset, int ldz, intW ifst, intW ilst, float[] work, int _work_offset, int lwork, intW info)
/* 7446:     */   {
/* 7447:6751 */     Stgexc.stgexc(wantq, wantz, n, a, _a_offset, lda, b, _b_offset, ldb, q, _q_offset, ldq, z, _z_offset, ldz, ifst, ilst, work, _work_offset, lwork, info);
/* 7448:     */   }
/* 7449:     */   
/* 7450:     */   public void stgsen(int ijob, boolean wantq, boolean wantz, boolean[] select, int n, float[] a, int lda, float[] b, int ldb, float[] alphar, float[] alphai, float[] beta, float[] q, int ldq, float[] z, int ldz, intW m, floatW pl, floatW pr, float[] dif, float[] work, int lwork, int[] iwork, int liwork, intW info)
/* 7451:     */   {
/* 7452:6756 */     Stgsen.stgsen(ijob, wantq, wantz, select, 0, n, a, 0, lda, b, 0, ldb, alphar, 0, alphai, 0, beta, 0, q, 0, ldq, z, 0, ldz, m, pl, pr, dif, 0, work, 0, lwork, iwork, 0, liwork, info);
/* 7453:     */   }
/* 7454:     */   
/* 7455:     */   public void stgsen(int ijob, boolean wantq, boolean wantz, boolean[] select, int _select_offset, int n, float[] a, int _a_offset, int lda, float[] b, int _b_offset, int ldb, float[] alphar, int _alphar_offset, float[] alphai, int _alphai_offset, float[] beta, int _beta_offset, float[] q, int _q_offset, int ldq, float[] z, int _z_offset, int ldz, intW m, floatW pl, floatW pr, float[] dif, int _dif_offset, float[] work, int _work_offset, int lwork, int[] iwork, int _iwork_offset, int liwork, intW info)
/* 7456:     */   {
/* 7457:6761 */     Stgsen.stgsen(ijob, wantq, wantz, select, _select_offset, n, a, _a_offset, lda, b, _b_offset, ldb, alphar, _alphar_offset, alphai, _alphai_offset, beta, _beta_offset, q, _q_offset, ldq, z, _z_offset, ldz, m, pl, pr, dif, _dif_offset, work, _work_offset, lwork, iwork, _iwork_offset, liwork, info);
/* 7458:     */   }
/* 7459:     */   
/* 7460:     */   public void stgsja(String jobu, String jobv, String jobq, int m, int p, int n, int k, int l, float[] a, int lda, float[] b, int ldb, float tola, float tolb, float[] alpha, float[] beta, float[] u, int ldu, float[] v, int ldv, float[] q, int ldq, float[] work, intW ncycle, intW info)
/* 7461:     */   {
/* 7462:6766 */     Stgsja.stgsja(jobu, jobv, jobq, m, p, n, k, l, a, 0, lda, b, 0, ldb, tola, tolb, alpha, 0, beta, 0, u, 0, ldu, v, 0, ldv, q, 0, ldq, work, 0, ncycle, info);
/* 7463:     */   }
/* 7464:     */   
/* 7465:     */   public void stgsja(String jobu, String jobv, String jobq, int m, int p, int n, int k, int l, float[] a, int _a_offset, int lda, float[] b, int _b_offset, int ldb, float tola, float tolb, float[] alpha, int _alpha_offset, float[] beta, int _beta_offset, float[] u, int _u_offset, int ldu, float[] v, int _v_offset, int ldv, float[] q, int _q_offset, int ldq, float[] work, int _work_offset, intW ncycle, intW info)
/* 7466:     */   {
/* 7467:6771 */     Stgsja.stgsja(jobu, jobv, jobq, m, p, n, k, l, a, _a_offset, lda, b, _b_offset, ldb, tola, tolb, alpha, _alpha_offset, beta, _beta_offset, u, _u_offset, ldu, v, _v_offset, ldv, q, _q_offset, ldq, work, _work_offset, ncycle, info);
/* 7468:     */   }
/* 7469:     */   
/* 7470:     */   public void stgsna(String job, String howmny, boolean[] select, int n, float[] a, int lda, float[] b, int ldb, float[] vl, int ldvl, float[] vr, int ldvr, float[] s, float[] dif, int mm, intW m, float[] work, int lwork, int[] iwork, intW info)
/* 7471:     */   {
/* 7472:6776 */     Stgsna.stgsna(job, howmny, select, 0, n, a, 0, lda, b, 0, ldb, vl, 0, ldvl, vr, 0, ldvr, s, 0, dif, 0, mm, m, work, 0, lwork, iwork, 0, info);
/* 7473:     */   }
/* 7474:     */   
/* 7475:     */   public void stgsna(String job, String howmny, boolean[] select, int _select_offset, int n, float[] a, int _a_offset, int lda, float[] b, int _b_offset, int ldb, float[] vl, int _vl_offset, int ldvl, float[] vr, int _vr_offset, int ldvr, float[] s, int _s_offset, float[] dif, int _dif_offset, int mm, intW m, float[] work, int _work_offset, int lwork, int[] iwork, int _iwork_offset, intW info)
/* 7476:     */   {
/* 7477:6781 */     Stgsna.stgsna(job, howmny, select, _select_offset, n, a, _a_offset, lda, b, _b_offset, ldb, vl, _vl_offset, ldvl, vr, _vr_offset, ldvr, s, _s_offset, dif, _dif_offset, mm, m, work, _work_offset, lwork, iwork, _iwork_offset, info);
/* 7478:     */   }
/* 7479:     */   
/* 7480:     */   public void stgsy2(String trans, int ijob, int m, int n, float[] a, int lda, float[] b, int ldb, float[] c, int Ldc, float[] d, int ldd, float[] e, int lde, float[] f, int ldf, floatW scale, floatW rdsum, floatW rdscal, int[] iwork, intW pq, intW info)
/* 7481:     */   {
/* 7482:6786 */     Stgsy2.stgsy2(trans, ijob, m, n, a, 0, lda, b, 0, ldb, c, 0, Ldc, d, 0, ldd, e, 0, lde, f, 0, ldf, scale, rdsum, rdscal, iwork, 0, pq, info);
/* 7483:     */   }
/* 7484:     */   
/* 7485:     */   public void stgsy2(String trans, int ijob, int m, int n, float[] a, int _a_offset, int lda, float[] b, int _b_offset, int ldb, float[] c, int _c_offset, int Ldc, float[] d, int _d_offset, int ldd, float[] e, int _e_offset, int lde, float[] f, int _f_offset, int ldf, floatW scale, floatW rdsum, floatW rdscal, int[] iwork, int _iwork_offset, intW pq, intW info)
/* 7486:     */   {
/* 7487:6791 */     Stgsy2.stgsy2(trans, ijob, m, n, a, _a_offset, lda, b, _b_offset, ldb, c, _c_offset, Ldc, d, _d_offset, ldd, e, _e_offset, lde, f, _f_offset, ldf, scale, rdsum, rdscal, iwork, _iwork_offset, pq, info);
/* 7488:     */   }
/* 7489:     */   
/* 7490:     */   public void stgsyl(String trans, int ijob, int m, int n, float[] a, int lda, float[] b, int ldb, float[] c, int Ldc, float[] d, int ldd, float[] e, int lde, float[] f, int ldf, floatW scale, floatW dif, float[] work, int lwork, int[] iwork, intW info)
/* 7491:     */   {
/* 7492:6796 */     Stgsyl.stgsyl(trans, ijob, m, n, a, 0, lda, b, 0, ldb, c, 0, Ldc, d, 0, ldd, e, 0, lde, f, 0, ldf, scale, dif, work, 0, lwork, iwork, 0, info);
/* 7493:     */   }
/* 7494:     */   
/* 7495:     */   public void stgsyl(String trans, int ijob, int m, int n, float[] a, int _a_offset, int lda, float[] b, int _b_offset, int ldb, float[] c, int _c_offset, int Ldc, float[] d, int _d_offset, int ldd, float[] e, int _e_offset, int lde, float[] f, int _f_offset, int ldf, floatW scale, floatW dif, float[] work, int _work_offset, int lwork, int[] iwork, int _iwork_offset, intW info)
/* 7496:     */   {
/* 7497:6801 */     Stgsyl.stgsyl(trans, ijob, m, n, a, _a_offset, lda, b, _b_offset, ldb, c, _c_offset, Ldc, d, _d_offset, ldd, e, _e_offset, lde, f, _f_offset, ldf, scale, dif, work, _work_offset, lwork, iwork, _iwork_offset, info);
/* 7498:     */   }
/* 7499:     */   
/* 7500:     */   public void stpcon(String norm, String uplo, String diag, int n, float[] ap, floatW rcond, float[] work, int[] iwork, intW info)
/* 7501:     */   {
/* 7502:6806 */     Stpcon.stpcon(norm, uplo, diag, n, ap, 0, rcond, work, 0, iwork, 0, info);
/* 7503:     */   }
/* 7504:     */   
/* 7505:     */   public void stpcon(String norm, String uplo, String diag, int n, float[] ap, int _ap_offset, floatW rcond, float[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 7506:     */   {
/* 7507:6811 */     Stpcon.stpcon(norm, uplo, diag, n, ap, _ap_offset, rcond, work, _work_offset, iwork, _iwork_offset, info);
/* 7508:     */   }
/* 7509:     */   
/* 7510:     */   public void stprfs(String uplo, String trans, String diag, int n, int nrhs, float[] ap, float[] b, int ldb, float[] x, int ldx, float[] ferr, float[] berr, float[] work, int[] iwork, intW info)
/* 7511:     */   {
/* 7512:6816 */     Stprfs.stprfs(uplo, trans, diag, n, nrhs, ap, 0, b, 0, ldb, x, 0, ldx, ferr, 0, berr, 0, work, 0, iwork, 0, info);
/* 7513:     */   }
/* 7514:     */   
/* 7515:     */   public void stprfs(String uplo, String trans, String diag, int n, int nrhs, float[] ap, int _ap_offset, float[] b, int _b_offset, int ldb, float[] x, int _x_offset, int ldx, float[] ferr, int _ferr_offset, float[] berr, int _berr_offset, float[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 7516:     */   {
/* 7517:6821 */     Stprfs.stprfs(uplo, trans, diag, n, nrhs, ap, _ap_offset, b, _b_offset, ldb, x, _x_offset, ldx, ferr, _ferr_offset, berr, _berr_offset, work, _work_offset, iwork, _iwork_offset, info);
/* 7518:     */   }
/* 7519:     */   
/* 7520:     */   public void stptri(String uplo, String diag, int n, float[] ap, intW info)
/* 7521:     */   {
/* 7522:6826 */     Stptri.stptri(uplo, diag, n, ap, 0, info);
/* 7523:     */   }
/* 7524:     */   
/* 7525:     */   public void stptri(String uplo, String diag, int n, float[] ap, int _ap_offset, intW info)
/* 7526:     */   {
/* 7527:6831 */     Stptri.stptri(uplo, diag, n, ap, _ap_offset, info);
/* 7528:     */   }
/* 7529:     */   
/* 7530:     */   public void stptrs(String uplo, String trans, String diag, int n, int nrhs, float[] ap, float[] b, int ldb, intW info)
/* 7531:     */   {
/* 7532:6836 */     Stptrs.stptrs(uplo, trans, diag, n, nrhs, ap, 0, b, 0, ldb, info);
/* 7533:     */   }
/* 7534:     */   
/* 7535:     */   public void stptrs(String uplo, String trans, String diag, int n, int nrhs, float[] ap, int _ap_offset, float[] b, int _b_offset, int ldb, intW info)
/* 7536:     */   {
/* 7537:6841 */     Stptrs.stptrs(uplo, trans, diag, n, nrhs, ap, _ap_offset, b, _b_offset, ldb, info);
/* 7538:     */   }
/* 7539:     */   
/* 7540:     */   public void strcon(String norm, String uplo, String diag, int n, float[] a, int lda, floatW rcond, float[] work, int[] iwork, intW info)
/* 7541:     */   {
/* 7542:6846 */     Strcon.strcon(norm, uplo, diag, n, a, 0, lda, rcond, work, 0, iwork, 0, info);
/* 7543:     */   }
/* 7544:     */   
/* 7545:     */   public void strcon(String norm, String uplo, String diag, int n, float[] a, int _a_offset, int lda, floatW rcond, float[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 7546:     */   {
/* 7547:6851 */     Strcon.strcon(norm, uplo, diag, n, a, _a_offset, lda, rcond, work, _work_offset, iwork, _iwork_offset, info);
/* 7548:     */   }
/* 7549:     */   
/* 7550:     */   public void strevc(String side, String howmny, boolean[] select, int n, float[] t, int ldt, float[] vl, int ldvl, float[] vr, int ldvr, int mm, intW m, float[] work, intW info)
/* 7551:     */   {
/* 7552:6856 */     Strevc.strevc(side, howmny, select, 0, n, t, 0, ldt, vl, 0, ldvl, vr, 0, ldvr, mm, m, work, 0, info);
/* 7553:     */   }
/* 7554:     */   
/* 7555:     */   public void strevc(String side, String howmny, boolean[] select, int _select_offset, int n, float[] t, int _t_offset, int ldt, float[] vl, int _vl_offset, int ldvl, float[] vr, int _vr_offset, int ldvr, int mm, intW m, float[] work, int _work_offset, intW info)
/* 7556:     */   {
/* 7557:6861 */     Strevc.strevc(side, howmny, select, _select_offset, n, t, _t_offset, ldt, vl, _vl_offset, ldvl, vr, _vr_offset, ldvr, mm, m, work, _work_offset, info);
/* 7558:     */   }
/* 7559:     */   
/* 7560:     */   public void strexc(String compq, int n, float[] t, int ldt, float[] q, int ldq, intW ifst, intW ilst, float[] work, intW info)
/* 7561:     */   {
/* 7562:6866 */     Strexc.strexc(compq, n, t, 0, ldt, q, 0, ldq, ifst, ilst, work, 0, info);
/* 7563:     */   }
/* 7564:     */   
/* 7565:     */   public void strexc(String compq, int n, float[] t, int _t_offset, int ldt, float[] q, int _q_offset, int ldq, intW ifst, intW ilst, float[] work, int _work_offset, intW info)
/* 7566:     */   {
/* 7567:6871 */     Strexc.strexc(compq, n, t, _t_offset, ldt, q, _q_offset, ldq, ifst, ilst, work, _work_offset, info);
/* 7568:     */   }
/* 7569:     */   
/* 7570:     */   public void strrfs(String uplo, String trans, String diag, int n, int nrhs, float[] a, int lda, float[] b, int ldb, float[] x, int ldx, float[] ferr, float[] berr, float[] work, int[] iwork, intW info)
/* 7571:     */   {
/* 7572:6876 */     Strrfs.strrfs(uplo, trans, diag, n, nrhs, a, 0, lda, b, 0, ldb, x, 0, ldx, ferr, 0, berr, 0, work, 0, iwork, 0, info);
/* 7573:     */   }
/* 7574:     */   
/* 7575:     */   public void strrfs(String uplo, String trans, String diag, int n, int nrhs, float[] a, int _a_offset, int lda, float[] b, int _b_offset, int ldb, float[] x, int _x_offset, int ldx, float[] ferr, int _ferr_offset, float[] berr, int _berr_offset, float[] work, int _work_offset, int[] iwork, int _iwork_offset, intW info)
/* 7576:     */   {
/* 7577:6881 */     Strrfs.strrfs(uplo, trans, diag, n, nrhs, a, _a_offset, lda, b, _b_offset, ldb, x, _x_offset, ldx, ferr, _ferr_offset, berr, _berr_offset, work, _work_offset, iwork, _iwork_offset, info);
/* 7578:     */   }
/* 7579:     */   
/* 7580:     */   public void strsen(String job, String compq, boolean[] select, int n, float[] t, int ldt, float[] q, int ldq, float[] wr, float[] wi, intW m, floatW s, floatW sep, float[] work, int lwork, int[] iwork, int liwork, intW info)
/* 7581:     */   {
/* 7582:6886 */     Strsen.strsen(job, compq, select, 0, n, t, 0, ldt, q, 0, ldq, wr, 0, wi, 0, m, s, sep, work, 0, lwork, iwork, 0, liwork, info);
/* 7583:     */   }
/* 7584:     */   
/* 7585:     */   public void strsen(String job, String compq, boolean[] select, int _select_offset, int n, float[] t, int _t_offset, int ldt, float[] q, int _q_offset, int ldq, float[] wr, int _wr_offset, float[] wi, int _wi_offset, intW m, floatW s, floatW sep, float[] work, int _work_offset, int lwork, int[] iwork, int _iwork_offset, int liwork, intW info)
/* 7586:     */   {
/* 7587:6891 */     Strsen.strsen(job, compq, select, _select_offset, n, t, _t_offset, ldt, q, _q_offset, ldq, wr, _wr_offset, wi, _wi_offset, m, s, sep, work, _work_offset, lwork, iwork, _iwork_offset, liwork, info);
/* 7588:     */   }
/* 7589:     */   
/* 7590:     */   public void strsna(String job, String howmny, boolean[] select, int n, float[] t, int ldt, float[] vl, int ldvl, float[] vr, int ldvr, float[] s, float[] sep, int mm, intW m, float[] work, int ldwork, int[] iwork, intW info)
/* 7591:     */   {
/* 7592:6896 */     Strsna.strsna(job, howmny, select, 0, n, t, 0, ldt, vl, 0, ldvl, vr, 0, ldvr, s, 0, sep, 0, mm, m, work, 0, ldwork, iwork, 0, info);
/* 7593:     */   }
/* 7594:     */   
/* 7595:     */   public void strsna(String job, String howmny, boolean[] select, int _select_offset, int n, float[] t, int _t_offset, int ldt, float[] vl, int _vl_offset, int ldvl, float[] vr, int _vr_offset, int ldvr, float[] s, int _s_offset, float[] sep, int _sep_offset, int mm, intW m, float[] work, int _work_offset, int ldwork, int[] iwork, int _iwork_offset, intW info)
/* 7596:     */   {
/* 7597:6901 */     Strsna.strsna(job, howmny, select, _select_offset, n, t, _t_offset, ldt, vl, _vl_offset, ldvl, vr, _vr_offset, ldvr, s, _s_offset, sep, _sep_offset, mm, m, work, _work_offset, ldwork, iwork, _iwork_offset, info);
/* 7598:     */   }
/* 7599:     */   
/* 7600:     */   public void strsyl(String trana, String tranb, int isgn, int m, int n, float[] a, int lda, float[] b, int ldb, float[] c, int Ldc, floatW scale, intW info)
/* 7601:     */   {
/* 7602:6906 */     Strsyl.strsyl(trana, tranb, isgn, m, n, a, 0, lda, b, 0, ldb, c, 0, Ldc, scale, info);
/* 7603:     */   }
/* 7604:     */   
/* 7605:     */   public void strsyl(String trana, String tranb, int isgn, int m, int n, float[] a, int _a_offset, int lda, float[] b, int _b_offset, int ldb, float[] c, int _c_offset, int Ldc, floatW scale, intW info)
/* 7606:     */   {
/* 7607:6911 */     Strsyl.strsyl(trana, tranb, isgn, m, n, a, _a_offset, lda, b, _b_offset, ldb, c, _c_offset, Ldc, scale, info);
/* 7608:     */   }
/* 7609:     */   
/* 7610:     */   public void strti2(String uplo, String diag, int n, float[] a, int lda, intW info)
/* 7611:     */   {
/* 7612:6916 */     Strti2.strti2(uplo, diag, n, a, 0, lda, info);
/* 7613:     */   }
/* 7614:     */   
/* 7615:     */   public void strti2(String uplo, String diag, int n, float[] a, int _a_offset, int lda, intW info)
/* 7616:     */   {
/* 7617:6921 */     Strti2.strti2(uplo, diag, n, a, _a_offset, lda, info);
/* 7618:     */   }
/* 7619:     */   
/* 7620:     */   public void strtri(String uplo, String diag, int n, float[] a, int lda, intW info)
/* 7621:     */   {
/* 7622:6926 */     Strtri.strtri(uplo, diag, n, a, 0, lda, info);
/* 7623:     */   }
/* 7624:     */   
/* 7625:     */   public void strtri(String uplo, String diag, int n, float[] a, int _a_offset, int lda, intW info)
/* 7626:     */   {
/* 7627:6931 */     Strtri.strtri(uplo, diag, n, a, _a_offset, lda, info);
/* 7628:     */   }
/* 7629:     */   
/* 7630:     */   public void strtrs(String uplo, String trans, String diag, int n, int nrhs, float[] a, int lda, float[] b, int ldb, intW info)
/* 7631:     */   {
/* 7632:6936 */     Strtrs.strtrs(uplo, trans, diag, n, nrhs, a, 0, lda, b, 0, ldb, info);
/* 7633:     */   }
/* 7634:     */   
/* 7635:     */   public void strtrs(String uplo, String trans, String diag, int n, int nrhs, float[] a, int _a_offset, int lda, float[] b, int _b_offset, int ldb, intW info)
/* 7636:     */   {
/* 7637:6941 */     Strtrs.strtrs(uplo, trans, diag, n, nrhs, a, _a_offset, lda, b, _b_offset, ldb, info);
/* 7638:     */   }
/* 7639:     */   
/* 7640:     */   public void stzrqf(int m, int n, float[] a, int lda, float[] tau, intW info)
/* 7641:     */   {
/* 7642:6946 */     Stzrqf.stzrqf(m, n, a, 0, lda, tau, 0, info);
/* 7643:     */   }
/* 7644:     */   
/* 7645:     */   public void stzrqf(int m, int n, float[] a, int _a_offset, int lda, float[] tau, int _tau_offset, intW info)
/* 7646:     */   {
/* 7647:6951 */     Stzrqf.stzrqf(m, n, a, _a_offset, lda, tau, _tau_offset, info);
/* 7648:     */   }
/* 7649:     */   
/* 7650:     */   public void stzrzf(int m, int n, float[] a, int lda, float[] tau, float[] work, int lwork, intW info)
/* 7651:     */   {
/* 7652:6956 */     Stzrzf.stzrzf(m, n, a, 0, lda, tau, 0, work, 0, lwork, info);
/* 7653:     */   }
/* 7654:     */   
/* 7655:     */   public void stzrzf(int m, int n, float[] a, int _a_offset, int lda, float[] tau, int _tau_offset, float[] work, int _work_offset, int lwork, intW info)
/* 7656:     */   {
/* 7657:6961 */     Stzrzf.stzrzf(m, n, a, _a_offset, lda, tau, _tau_offset, work, _work_offset, lwork, info);
/* 7658:     */   }
/* 7659:     */   
/* 7660:     */   public double dlamch(String cmach)
/* 7661:     */   {
/* 7662:6966 */     return Dlamch.dlamch(cmach);
/* 7663:     */   }
/* 7664:     */   
/* 7665:     */   public void dlamc1(intW beta, intW t, booleanW rnd, booleanW ieee1)
/* 7666:     */   {
/* 7667:6971 */     Dlamc1.dlamc1(beta, t, rnd, ieee1);
/* 7668:     */   }
/* 7669:     */   
/* 7670:     */   public void dlamc2(intW beta, intW t, booleanW rnd, doubleW eps, intW emin, doubleW rmin, intW emax, doubleW rmax)
/* 7671:     */   {
/* 7672:6976 */     Dlamc2.dlamc2(beta, t, rnd, eps, emin, rmin, emax, rmax);
/* 7673:     */   }
/* 7674:     */   
/* 7675:     */   public double dlamc3(double a, double b)
/* 7676:     */   {
/* 7677:6981 */     return Dlamc3.dlamc3(a, b);
/* 7678:     */   }
/* 7679:     */   
/* 7680:     */   public void dlamc4(intW emin, double start, int base)
/* 7681:     */   {
/* 7682:6986 */     Dlamc4.dlamc4(emin, start, base);
/* 7683:     */   }
/* 7684:     */   
/* 7685:     */   public void dlamc5(int beta, int p, int emin, boolean ieee, intW emax, doubleW rmax)
/* 7686:     */   {
/* 7687:6991 */     Dlamc5.dlamc5(beta, p, emin, ieee, emax, rmax);
/* 7688:     */   }
/* 7689:     */   
/* 7690:     */   public double dsecnd()
/* 7691:     */   {
/* 7692:6996 */     return Dsecnd.dsecnd();
/* 7693:     */   }
/* 7694:     */   
/* 7695:     */   public boolean lsame(String ca, String cb)
/* 7696:     */   {
/* 7697:7001 */     return Lsame.lsame(ca, cb);
/* 7698:     */   }
/* 7699:     */   
/* 7700:     */   public float second()
/* 7701:     */   {
/* 7702:7006 */     return Second.second();
/* 7703:     */   }
/* 7704:     */   
/* 7705:     */   public float slamch(String cmach)
/* 7706:     */   {
/* 7707:7011 */     return Slamch.slamch(cmach);
/* 7708:     */   }
/* 7709:     */   
/* 7710:     */   public void slamc1(intW beta, intW t, booleanW rnd, booleanW ieee1)
/* 7711:     */   {
/* 7712:7016 */     Slamc1.slamc1(beta, t, rnd, ieee1);
/* 7713:     */   }
/* 7714:     */   
/* 7715:     */   public void slamc2(intW beta, intW t, booleanW rnd, floatW eps, intW emin, floatW rmin, intW emax, floatW rmax)
/* 7716:     */   {
/* 7717:7021 */     Slamc2.slamc2(beta, t, rnd, eps, emin, rmin, emax, rmax);
/* 7718:     */   }
/* 7719:     */   
/* 7720:     */   public float slamc3(float a, float b)
/* 7721:     */   {
/* 7722:7026 */     return Slamc3.slamc3(a, b);
/* 7723:     */   }
/* 7724:     */   
/* 7725:     */   public void slamc4(intW emin, float start, int base)
/* 7726:     */   {
/* 7727:7031 */     Slamc4.slamc4(emin, start, base);
/* 7728:     */   }
/* 7729:     */   
/* 7730:     */   public void slamc5(int beta, int p, int emin, boolean ieee, intW emax, floatW rmax)
/* 7731:     */   {
/* 7732:7036 */     Slamc5.slamc5(beta, p, emin, ieee, emax, rmax);
/* 7733:     */   }
/* 7734:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     com.github.fommil.netlib.F2jLAPACK
 * JD-Core Version:    0.7.0.1
 */