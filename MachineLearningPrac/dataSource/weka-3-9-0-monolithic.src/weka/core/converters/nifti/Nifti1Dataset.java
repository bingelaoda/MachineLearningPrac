/*    1:     */ package weka.core.converters.nifti;
/*    2:     */ 
/*    3:     */ import java.io.BufferedInputStream;
/*    4:     */ import java.io.ByteArrayInputStream;
/*    5:     */ import java.io.ByteArrayOutputStream;
/*    6:     */ import java.io.DataInputStream;
/*    7:     */ import java.io.EOFException;
/*    8:     */ import java.io.File;
/*    9:     */ import java.io.FileInputStream;
/*   10:     */ import java.io.FileNotFoundException;
/*   11:     */ import java.io.FileOutputStream;
/*   12:     */ import java.io.IOException;
/*   13:     */ import java.io.PrintStream;
/*   14:     */ import java.io.RandomAccessFile;
/*   15:     */ import java.nio.ByteOrder;
/*   16:     */ import java.text.NumberFormat;
/*   17:     */ import java.util.Vector;
/*   18:     */ import java.util.zip.GZIPInputStream;
/*   19:     */ 
/*   20:     */ public class Nifti1Dataset
/*   21:     */ {
/*   22:     */   public static final String ANZ_HDR_EXT = ".hdr";
/*   23:     */   public static final String ANZ_DAT_EXT = ".img";
/*   24:     */   public static final String NI1_EXT = ".nii";
/*   25:     */   public static final String GZIP_EXT = ".gz";
/*   26:     */   public static final int ANZ_HDR_SIZE = 348;
/*   27:     */   public static final long NII_HDR_SIZE = 352L;
/*   28:     */   public static final int EXT_KEY_SIZE = 8;
/*   29:     */   public static final String NII_MAGIC_STRING = "n+1";
/*   30:     */   public static final String ANZ_MAGIC_STRING = "ni1";
/*   31:     */   public static final short NIFTI_INTENT_NONE = 0;
/*   32:     */   public static final short NIFTI_INTENT_CORREL = 2;
/*   33:     */   public static final short NIFTI_INTENT_TTEST = 3;
/*   34:     */   public static final short NIFTI_INTENT_FTEST = 4;
/*   35:     */   public static final short NIFTI_INTENT_ZSCORE = 5;
/*   36:     */   public static final short NIFTI_INTENT_CHISQ = 6;
/*   37:     */   public static final short NIFTI_INTENT_BETA = 7;
/*   38:     */   public static final short NIFTI_INTENT_BINOM = 8;
/*   39:     */   public static final short NIFTI_INTENT_GAMMA = 9;
/*   40:     */   public static final short NIFTI_INTENT_POISSON = 10;
/*   41:     */   public static final short NIFTI_INTENT_NORMAL = 11;
/*   42:     */   public static final short NIFTI_INTENT_FTEST_NONC = 12;
/*   43:     */   public static final short NIFTI_INTENT_CHISQ_NONC = 13;
/*   44:     */   public static final short NIFTI_INTENT_LOGISTIC = 14;
/*   45:     */   public static final short NIFTI_INTENT_LAPLACE = 15;
/*   46:     */   public static final short NIFTI_INTENT_UNIFORM = 16;
/*   47:     */   public static final short NIFTI_INTENT_TTEST_NONC = 17;
/*   48:     */   public static final short NIFTI_INTENT_WEIBULL = 18;
/*   49:     */   public static final short NIFTI_INTENT_CHI = 19;
/*   50:     */   public static final short NIFTI_INTENT_INVGAUSS = 20;
/*   51:     */   public static final short NIFTI_INTENT_EXTVAL = 21;
/*   52:     */   public static final short NIFTI_INTENT_PVAL = 22;
/*   53:     */   public static final short NIFTI_INTENT_ESTIMATE = 1001;
/*   54:     */   public static final short NIFTI_INTENT_LABEL = 1002;
/*   55:     */   public static final short NIFTI_INTENT_NEURONAME = 1003;
/*   56:     */   public static final short NIFTI_INTENT_GENMATRIX = 1004;
/*   57:     */   public static final short NIFTI_INTENT_SYMMATRIX = 1005;
/*   58:     */   public static final short NIFTI_INTENT_DISPVECT = 1006;
/*   59:     */   public static final short NIFTI_INTENT_VECTOR = 1007;
/*   60:     */   public static final short NIFTI_INTENT_POINTSET = 1008;
/*   61:     */   public static final short NIFTI_INTENT_TRIANGLE = 1009;
/*   62:     */   public static final short NIFTI_INTENT_QUATERNION = 1010;
/*   63:     */   public static final short NIFTI_FIRST_STATCODE = 2;
/*   64:     */   public static final short NIFTI_LAST_STATCODE = 22;
/*   65:     */   public static final short DT_NONE = 0;
/*   66:     */   public static final short DT_BINARY = 1;
/*   67:     */   public static final short NIFTI_TYPE_UINT8 = 2;
/*   68:     */   public static final short NIFTI_TYPE_INT16 = 4;
/*   69:     */   public static final short NIFTI_TYPE_INT32 = 8;
/*   70:     */   public static final short NIFTI_TYPE_FLOAT32 = 16;
/*   71:     */   public static final short NIFTI_TYPE_COMPLEX64 = 32;
/*   72:     */   public static final short NIFTI_TYPE_FLOAT64 = 64;
/*   73:     */   public static final short NIFTI_TYPE_RGB24 = 128;
/*   74:     */   public static final short DT_ALL = 255;
/*   75:     */   public static final short NIFTI_TYPE_INT8 = 256;
/*   76:     */   public static final short NIFTI_TYPE_UINT16 = 512;
/*   77:     */   public static final short NIFTI_TYPE_UINT32 = 768;
/*   78:     */   public static final short NIFTI_TYPE_INT64 = 1024;
/*   79:     */   public static final short NIFTI_TYPE_UINT64 = 1280;
/*   80:     */   public static final short NIFTI_TYPE_FLOAT128 = 1536;
/*   81:     */   public static final short NIFTI_TYPE_COMPLEX128 = 1792;
/*   82:     */   public static final short NIFTI_TYPE_COMPLEX256 = 2048;
/*   83:     */   public static final short NIFTI_UNITS_UNKNOWN = 0;
/*   84:     */   public static final short NIFTI_UNITS_METER = 1;
/*   85:     */   public static final short NIFTI_UNITS_MM = 2;
/*   86:     */   public static final short NIFTI_UNITS_MICRON = 3;
/*   87:     */   public static final short NIFTI_UNITS_SEC = 8;
/*   88:     */   public static final short NIFTI_UNITS_MSEC = 16;
/*   89:     */   public static final short NIFTI_UNITS_USEC = 24;
/*   90:     */   public static final short NIFTI_UNITS_HZ = 32;
/*   91:     */   public static final short NIFTI_UNITS_PPM = 40;
/*   92:     */   public static final short NIFTI_SLICE_SEQ_INC = 1;
/*   93:     */   public static final short NIFTI_SLICE_SEQ_DEC = 2;
/*   94:     */   public static final short NIFTI_SLICE_ALT_INC = 3;
/*   95:     */   public static final short NIFTI_SLICE_ALT_DEC = 4;
/*   96:     */   public static final short NIFTI_XFORM_UNKNOWN = 0;
/*   97:     */   public static final short NIFTI_XFORM_SCANNER_ANAT = 1;
/*   98:     */   public static final short NIFTI_XFORM_ALIGNED_ANAT = 2;
/*   99:     */   public static final short NIFTI_XFORM_TALAIRACH = 3;
/*  100:     */   public static final short NIFTI_XFORM_MNI_152 = 4;
/*  101:     */   String ds_hdrname;
/*  102:     */   String ds_datname;
/*  103:     */   public boolean ds_is_nii;
/*  104:     */   public boolean big_endian;
/*  105:     */   public short XDIM;
/*  106:     */   public short YDIM;
/*  107:     */   public short ZDIM;
/*  108:     */   public short TDIM;
/*  109:     */   public short DIM5;
/*  110:     */   public short DIM6;
/*  111:     */   public short DIM7;
/*  112:     */   public short freq_dim;
/*  113:     */   public short phase_dim;
/*  114:     */   public short slice_dim;
/*  115:     */   public short xyz_unit_code;
/*  116:     */   public short t_unit_code;
/*  117:     */   public short qfac;
/*  118:     */   Vector extensions_list;
/*  119:     */   Vector extension_blobs;
/*  120:     */   public int sizeof_hdr;
/*  121:     */   public StringBuffer data_type_string;
/*  122:     */   public StringBuffer db_name;
/*  123:     */   public int extents;
/*  124:     */   public short session_error;
/*  125:     */   public StringBuffer regular;
/*  126:     */   public StringBuffer dim_info;
/*  127:     */   short[] dim;
/*  128:     */   public float[] intent;
/*  129:     */   public short intent_code;
/*  130:     */   short datatype;
/*  131:     */   short bitpix;
/*  132:     */   public short slice_start;
/*  133:     */   public float[] pixdim;
/*  134:     */   public float vox_offset;
/*  135:     */   public float scl_slope;
/*  136:     */   public float scl_inter;
/*  137:     */   public short slice_end;
/*  138:     */   public byte slice_code;
/*  139:     */   public byte xyzt_units;
/*  140:     */   public float cal_max;
/*  141:     */   public float cal_min;
/*  142:     */   public float slice_duration;
/*  143:     */   public float toffset;
/*  144:     */   public int glmax;
/*  145:     */   public int glmin;
/*  146:     */   public StringBuffer descrip;
/*  147:     */   public StringBuffer aux_file;
/*  148:     */   public short qform_code;
/*  149:     */   public short sform_code;
/*  150:     */   public float[] quatern;
/*  151:     */   public float[] qoffset;
/*  152:     */   public float[] srow_x;
/*  153:     */   public float[] srow_y;
/*  154:     */   public float[] srow_z;
/*  155:     */   public StringBuffer intent_name;
/*  156:     */   public StringBuffer magic;
/*  157:     */   public byte[] extension;
/*  158:     */   
/*  159:     */   public Nifti1Dataset(String name)
/*  160:     */   {
/*  161: 273 */     setDefaults();
/*  162: 274 */     checkName(name);
/*  163:     */   }
/*  164:     */   
/*  165:     */   public Nifti1Dataset()
/*  166:     */   {
/*  167: 289 */     setDefaults();
/*  168:     */   }
/*  169:     */   
/*  170:     */   public void readHeader()
/*  171:     */     throws IOException, FileNotFoundException
/*  172:     */   {
/*  173:     */     DataInputStream dis;
/*  174:     */     DataInputStream dis;
/*  175: 309 */     if (this.ds_hdrname.endsWith(".gz")) {
/*  176: 310 */       dis = new DataInputStream(new GZIPInputStream(new FileInputStream(this.ds_hdrname)));
/*  177:     */     } else {
/*  178: 312 */       dis = new DataInputStream(new FileInputStream(this.ds_hdrname));
/*  179:     */     }
/*  180:     */     EndianCorrectInputStream ecs;
/*  181:     */     try
/*  182:     */     {
/*  183: 316 */       dis.skipBytes(40);
/*  184: 317 */       short s = dis.readShort();
/*  185: 318 */       dis.close();
/*  186: 319 */       if ((s < 1) || (s > 7)) {
/*  187: 320 */         this.big_endian = false;
/*  188:     */       } else {
/*  189: 322 */         this.big_endian = true;
/*  190:     */       }
/*  191:     */       EndianCorrectInputStream ecs;
/*  192: 326 */       if (this.ds_hdrname.endsWith(".gz")) {
/*  193: 327 */         ecs = new EndianCorrectInputStream(new GZIPInputStream(new FileInputStream(this.ds_hdrname)), this.big_endian);
/*  194:     */       } else {
/*  195: 329 */         ecs = new EndianCorrectInputStream(this.ds_hdrname, this.big_endian);
/*  196:     */       }
/*  197: 331 */       this.sizeof_hdr = ecs.readIntCorrect();
/*  198:     */       
/*  199: 333 */       byte[] bb = new byte[10];
/*  200: 334 */       ecs.readFully(bb, 0, 10);
/*  201: 335 */       this.data_type_string = new StringBuffer(new String(bb));
/*  202:     */       
/*  203: 337 */       bb = new byte[18];
/*  204: 338 */       ecs.readFully(bb, 0, 18);
/*  205: 339 */       this.db_name = new StringBuffer(new String(bb));
/*  206:     */       
/*  207: 341 */       this.extents = ecs.readIntCorrect();
/*  208:     */       
/*  209: 343 */       this.session_error = ecs.readShortCorrect();
/*  210:     */       
/*  211: 345 */       this.regular = new StringBuffer();
/*  212: 346 */       this.regular.append((char)ecs.readUnsignedByte());
/*  213:     */       
/*  214: 348 */       this.dim_info = new StringBuffer();
/*  215: 349 */       this.dim_info.append((char)ecs.readUnsignedByte());
/*  216: 350 */       short[] ss = unpackDimInfo(this.dim_info.charAt(0));
/*  217: 351 */       this.freq_dim = ss[0];
/*  218: 352 */       this.phase_dim = ss[1];
/*  219: 353 */       this.slice_dim = ss[2];
/*  220: 355 */       for (int i = 0; i < 8; i++) {
/*  221: 356 */         this.dim[i] = ecs.readShortCorrect();
/*  222:     */       }
/*  223: 357 */       if (this.dim[0] > 0) {
/*  224: 358 */         this.XDIM = this.dim[1];
/*  225:     */       }
/*  226: 359 */       if (this.dim[0] > 1) {
/*  227: 360 */         this.YDIM = this.dim[2];
/*  228:     */       }
/*  229: 361 */       if (this.dim[0] > 2) {
/*  230: 362 */         this.ZDIM = this.dim[3];
/*  231:     */       }
/*  232: 363 */       if (this.dim[0] > 3) {
/*  233: 364 */         this.TDIM = this.dim[4];
/*  234:     */       }
/*  235: 366 */       for (i = 0; i < 3; i++) {
/*  236: 367 */         this.intent[i] = ecs.readFloatCorrect();
/*  237:     */       }
/*  238: 369 */       this.intent_code = ecs.readShortCorrect();
/*  239:     */       
/*  240: 371 */       this.datatype = ecs.readShortCorrect();
/*  241:     */       
/*  242: 373 */       this.bitpix = ecs.readShortCorrect();
/*  243:     */       
/*  244: 375 */       this.slice_start = ecs.readShortCorrect();
/*  245: 377 */       for (i = 0; i < 8; i++) {
/*  246: 378 */         this.pixdim[i] = ecs.readFloatCorrect();
/*  247:     */       }
/*  248: 379 */       this.qfac = ((short)(int)Math.floor(this.pixdim[0]));
/*  249:     */       
/*  250: 381 */       this.vox_offset = ecs.readFloatCorrect();
/*  251:     */       
/*  252: 383 */       this.scl_slope = ecs.readFloatCorrect();
/*  253: 384 */       this.scl_inter = ecs.readFloatCorrect();
/*  254:     */       
/*  255: 386 */       this.slice_end = ecs.readShortCorrect();
/*  256:     */       
/*  257: 388 */       this.slice_code = ((byte)ecs.readUnsignedByte());
/*  258:     */       
/*  259: 390 */       this.xyzt_units = ((byte)ecs.readUnsignedByte());
/*  260: 391 */       ss = unpackUnits(this.xyzt_units);
/*  261: 392 */       this.xyz_unit_code = ss[0];
/*  262: 393 */       this.t_unit_code = ss[1];
/*  263:     */       
/*  264: 395 */       this.cal_max = ecs.readFloatCorrect();
/*  265: 396 */       this.cal_min = ecs.readFloatCorrect();
/*  266:     */       
/*  267: 398 */       this.slice_duration = ecs.readFloatCorrect();
/*  268:     */       
/*  269: 400 */       this.toffset = ecs.readFloatCorrect();
/*  270:     */       
/*  271: 402 */       this.glmax = ecs.readIntCorrect();
/*  272: 403 */       this.glmin = ecs.readIntCorrect();
/*  273:     */       
/*  274: 405 */       bb = new byte[80];
/*  275: 406 */       ecs.readFully(bb, 0, 80);
/*  276: 407 */       this.descrip = new StringBuffer(new String(bb));
/*  277:     */       
/*  278: 409 */       bb = new byte[24];
/*  279: 410 */       ecs.readFully(bb, 0, 24);
/*  280: 411 */       this.aux_file = new StringBuffer(new String(bb));
/*  281:     */       
/*  282: 413 */       this.qform_code = ecs.readShortCorrect();
/*  283: 414 */       this.sform_code = ecs.readShortCorrect();
/*  284: 416 */       for (i = 0; i < 3; i++) {
/*  285: 417 */         this.quatern[i] = ecs.readFloatCorrect();
/*  286:     */       }
/*  287: 418 */       for (i = 0; i < 3; i++) {
/*  288: 419 */         this.qoffset[i] = ecs.readFloatCorrect();
/*  289:     */       }
/*  290: 421 */       for (i = 0; i < 4; i++) {
/*  291: 422 */         this.srow_x[i] = ecs.readFloatCorrect();
/*  292:     */       }
/*  293: 423 */       for (i = 0; i < 4; i++) {
/*  294: 424 */         this.srow_y[i] = ecs.readFloatCorrect();
/*  295:     */       }
/*  296: 425 */       for (i = 0; i < 4; i++) {
/*  297: 426 */         this.srow_z[i] = ecs.readFloatCorrect();
/*  298:     */       }
/*  299: 429 */       bb = new byte[16];
/*  300: 430 */       ecs.readFully(bb, 0, 16);
/*  301: 431 */       this.intent_name = new StringBuffer(new String(bb));
/*  302:     */       
/*  303: 433 */       bb = new byte[4];
/*  304: 434 */       ecs.readFully(bb, 0, 4);
/*  305: 435 */       this.magic = new StringBuffer(new String(bb));
/*  306:     */     }
/*  307:     */     catch (IOException ex)
/*  308:     */     {
/*  309: 439 */       throw new IOException("Error: unable to read header file " + this.ds_hdrname + ": " + ex.getMessage());
/*  310:     */     }
/*  311: 444 */     if (this.ds_is_nii) {
/*  312: 445 */       readNiiExt(ecs);
/*  313:     */     } else {
/*  314: 447 */       readNp1Ext(ecs);
/*  315:     */     }
/*  316: 450 */     ecs.close();
/*  317:     */   }
/*  318:     */   
/*  319:     */   public void copyHeader(Nifti1Dataset A)
/*  320:     */   {
/*  321: 465 */     this.ds_hdrname = new String(A.ds_hdrname);
/*  322: 466 */     this.ds_datname = new String(A.ds_datname);
/*  323: 467 */     this.ds_is_nii = A.ds_is_nii;
/*  324: 468 */     this.big_endian = A.big_endian;
/*  325: 469 */     this.sizeof_hdr = A.sizeof_hdr;
/*  326: 470 */     this.data_type_string = new StringBuffer(A.data_type_string.toString());
/*  327: 471 */     this.db_name = new StringBuffer(A.db_name.toString());
/*  328: 472 */     this.extents = A.extents;
/*  329: 473 */     this.session_error = A.session_error;
/*  330: 474 */     this.regular = new StringBuffer(A.regular.toString());
/*  331: 475 */     this.dim_info = new StringBuffer(A.dim_info.toString());
/*  332: 476 */     this.freq_dim = A.freq_dim;
/*  333: 477 */     this.phase_dim = A.phase_dim;
/*  334: 478 */     this.slice_dim = A.slice_dim;
/*  335: 479 */     for (int i = 0; i < 8; i++) {
/*  336: 480 */       this.dim[i] = A.dim[i];
/*  337:     */     }
/*  338: 481 */     this.XDIM = A.XDIM;this.YDIM = A.YDIM;this.ZDIM = A.ZDIM;this.TDIM = A.TDIM;
/*  339: 482 */     this.DIM5 = A.DIM5;this.DIM6 = A.DIM6;this.DIM7 = A.DIM7;
/*  340: 483 */     for (i = 0; i < 3; i++) {
/*  341: 484 */       this.intent[i] = A.intent[i];
/*  342:     */     }
/*  343: 485 */     this.intent_code = A.intent_code;
/*  344: 486 */     this.datatype = A.datatype;
/*  345: 487 */     this.bitpix = A.bitpix;
/*  346: 488 */     this.slice_start = A.slice_start;
/*  347: 489 */     this.qfac = 1;
/*  348: 490 */     for (i = 0; i < 8; i++) {
/*  349: 491 */       this.pixdim[i] = A.pixdim[i];
/*  350:     */     }
/*  351: 493 */     this.vox_offset = A.vox_offset;
/*  352: 494 */     this.scl_slope = A.scl_slope;
/*  353: 495 */     this.scl_inter = A.scl_inter;
/*  354: 496 */     this.slice_end = A.slice_end;
/*  355: 497 */     this.slice_code = A.slice_code;
/*  356: 498 */     this.xyzt_units = A.xyzt_units;
/*  357: 499 */     this.xyz_unit_code = A.xyz_unit_code;
/*  358: 500 */     this.t_unit_code = A.t_unit_code;
/*  359:     */     
/*  360: 502 */     this.cal_max = A.cal_max;
/*  361: 503 */     this.cal_min = A.cal_min;
/*  362: 504 */     this.slice_duration = A.slice_duration;
/*  363: 505 */     this.toffset = A.toffset;
/*  364: 506 */     this.glmax = A.glmax;
/*  365: 507 */     this.glmin = A.glmin;
/*  366:     */     
/*  367: 509 */     this.descrip = new StringBuffer(A.descrip.toString());
/*  368: 510 */     this.aux_file = new StringBuffer(A.aux_file.toString());
/*  369:     */     
/*  370: 512 */     this.qform_code = A.qform_code;
/*  371: 513 */     this.sform_code = A.sform_code;
/*  372: 515 */     for (i = 0; i < 3; i++)
/*  373:     */     {
/*  374: 516 */       this.quatern[i] = A.quatern[i];
/*  375: 517 */       this.qoffset[i] = A.qoffset[i];
/*  376:     */     }
/*  377: 520 */     for (i = 0; i < 4; i++)
/*  378:     */     {
/*  379: 521 */       this.srow_x[i] = A.srow_x[i];
/*  380: 522 */       this.srow_y[i] = A.srow_y[i];
/*  381: 523 */       this.srow_z[i] = A.srow_z[i];
/*  382:     */     }
/*  383: 526 */     this.intent_name = new StringBuffer(A.intent_name.toString());
/*  384:     */     
/*  385: 528 */     this.magic = new StringBuffer(A.magic.toString());
/*  386: 530 */     for (i = 0; i < 4; i++) {
/*  387: 531 */       this.extension[i] = 0;
/*  388:     */     }
/*  389:     */   }
/*  390:     */   
/*  391:     */   private void readNiiExt(EndianCorrectInputStream ecs)
/*  392:     */     throws IOException
/*  393:     */   {
/*  394:     */     try
/*  395:     */     {
/*  396: 552 */       ecs.readFully(this.extension, 0, 4);
/*  397:     */     }
/*  398:     */     catch (IOException ex)
/*  399:     */     {
/*  400: 555 */       throw new IOException("Error: i/o error reading extension bytes on header file " + this.ds_hdrname + ": " + ex.getMessage());
/*  401:     */     }
/*  402: 560 */     int[] size_code = new int[2];
/*  403: 561 */     if (this.extension[0] != 0)
/*  404:     */     {
/*  405: 562 */       int start_addr = 352;
/*  406:     */       
/*  407: 564 */       size_code[0] = 0;
/*  408: 565 */       size_code[1] = 0;
/*  409: 569 */       while (start_addr < (int)this.vox_offset)
/*  410:     */       {
/*  411:     */         try
/*  412:     */         {
/*  413: 571 */           size_code = new int[2];
/*  414: 572 */           size_code[0] = ecs.readIntCorrect();
/*  415: 573 */           size_code[1] = ecs.readIntCorrect();
/*  416: 574 */           byte[] eblob = new byte[size_code[0] - 8];
/*  417: 575 */           ecs.readFully(eblob, 0, size_code[0] - 8);
/*  418: 576 */           this.extension_blobs.add(eblob);
/*  419:     */         }
/*  420:     */         catch (IOException ex)
/*  421:     */         {
/*  422: 579 */           printHeader();
/*  423: 580 */           throw new EOFException("Error: i/o error reading extension data for extension " + (this.extensions_list.size() + 1) + " on header file " + this.ds_hdrname + ": " + ex.getMessage());
/*  424:     */         }
/*  425: 583 */         this.extensions_list.add(size_code);
/*  426: 584 */         start_addr += size_code[0];
/*  427: 588 */         if (start_addr > (int)this.vox_offset)
/*  428:     */         {
/*  429: 589 */           printHeader();
/*  430: 590 */           throw new IOException("Error: Data  for extension " + this.extensions_list.size() + " on header file " + this.ds_hdrname + " appears to overrun start of image data.");
/*  431:     */         }
/*  432:     */       }
/*  433:     */     }
/*  434:     */   }
/*  435:     */   
/*  436:     */   private void readNp1Ext(EndianCorrectInputStream ecs)
/*  437:     */     throws IOException, EOFException
/*  438:     */   {
/*  439:     */     try
/*  440:     */     {
/*  441: 617 */       ecs.readFully(this.extension, 0, 4);
/*  442:     */     }
/*  443:     */     catch (EOFException ex)
/*  444:     */     {
/*  445: 620 */       return;
/*  446:     */     }
/*  447:     */     catch (IOException ex)
/*  448:     */     {
/*  449: 623 */       throw new IOException("Error: i/o error reading extension bytes on header file " + this.ds_hdrname + ": " + ex.getMessage());
/*  450:     */     }
/*  451: 628 */     int[] size_code = new int[2];
/*  452: 629 */     if (this.extension[0] != 0)
/*  453:     */     {
/*  454: 631 */       size_code[0] = 0;
/*  455: 632 */       size_code[1] = 0;
/*  456:     */       for (;;)
/*  457:     */       {
/*  458:     */         try
/*  459:     */         {
/*  460: 638 */           size_code = new int[2];
/*  461: 639 */           size_code[0] = ecs.readIntCorrect();
/*  462: 640 */           size_code[1] = ecs.readIntCorrect();
/*  463: 641 */           byte[] eblob = new byte[size_code[0] - 8];
/*  464: 642 */           ecs.readFully(eblob, 0, size_code[0] - 8);
/*  465: 643 */           this.extension_blobs.add(eblob);
/*  466:     */         }
/*  467:     */         catch (EOFException ex)
/*  468:     */         {
/*  469: 646 */           return;
/*  470:     */         }
/*  471:     */         catch (IOException ex)
/*  472:     */         {
/*  473: 649 */           throw new EOFException("Error: i/o error reading extension data for extension " + (this.extensions_list.size() + 1) + " on header file " + this.ds_hdrname + ": " + ex.getMessage());
/*  474:     */         }
/*  475: 652 */         this.extensions_list.add(size_code);
/*  476:     */       }
/*  477:     */     }
/*  478:     */   }
/*  479:     */   
/*  480:     */   public int[][] getExtensionsList()
/*  481:     */   {
/*  482: 675 */     int[] size_code = new int[2];
/*  483: 676 */     int n = this.extensions_list.size();
/*  484: 677 */     int[][] extlist = new int[n][2];
/*  485: 680 */     for (int i = 0; i < n; i++)
/*  486:     */     {
/*  487: 681 */       size_code = (int[])this.extensions_list.get(i);
/*  488: 682 */       extlist[i][0] = size_code[0];
/*  489: 683 */       extlist[i][1] = size_code[1];
/*  490:     */     }
/*  491: 686 */     return extlist;
/*  492:     */   }
/*  493:     */   
/*  494:     */   public void removeExtension(int index)
/*  495:     */   {
/*  496: 698 */     int[] size_code = new int[2];
/*  497:     */     
/*  498: 700 */     int n = this.extensions_list.size();
/*  499: 702 */     if (index >= n)
/*  500:     */     {
/*  501: 703 */       System.out.println("\nERROR: could not remove extension " + index + 1 + " from " + this.ds_hdrname + ". It only has " + n + " extensions.");
/*  502: 704 */       return;
/*  503:     */     }
/*  504: 708 */     size_code = (int[])this.extensions_list.get(index);
/*  505: 709 */     this.extensions_list.remove(index);
/*  506: 710 */     this.extension_blobs.remove(index);
/*  507: 713 */     if (this.ds_is_nii) {
/*  508: 714 */       this.vox_offset -= size_code[0];
/*  509:     */     }
/*  510:     */   }
/*  511:     */   
/*  512:     */   public void addExtension(int code, String filename)
/*  513:     */     throws IOException
/*  514:     */   {
/*  515: 730 */     int[] size_code = new int[2];
/*  516:     */     
/*  517:     */ 
/*  518:     */ 
/*  519:     */ 
/*  520: 735 */     File f = new File(filename);
/*  521: 736 */     long l = f.length();
/*  522: 740 */     if (l > 2147483647L) {
/*  523: 741 */       throw new IOException("Error: maximum extension size is 2147483647bytes. " + filename + " is " + l + " bytes.");
/*  524:     */     }
/*  525: 743 */     int il = (int)l;
/*  526: 744 */     int pad = (il + 8) % 16;
/*  527: 745 */     if (pad != 0) {
/*  528: 746 */       pad = 16 - pad;
/*  529:     */     }
/*  530: 750 */     byte[] b = new byte[il + pad];
/*  531:     */     try
/*  532:     */     {
/*  533: 752 */       DataInputStream dis = new DataInputStream(new FileInputStream(filename));
/*  534: 753 */       dis.readFully(b, 0, il);
/*  535: 754 */       dis.close();
/*  536:     */     }
/*  537:     */     catch (IOException ex)
/*  538:     */     {
/*  539: 757 */       throw new IOException("Error reading extension data for " + this.ds_hdrname + " from file " + filename + ". :" + ex.getMessage());
/*  540:     */     }
/*  541: 759 */     for (int i = il; i < il + pad; i++) {
/*  542: 760 */       b[i] = 0;
/*  543:     */     }
/*  544: 764 */     size_code[0] = (il + pad + 8);
/*  545: 765 */     size_code[1] = code;
/*  546: 766 */     this.extensions_list.add(size_code);
/*  547: 767 */     this.extension_blobs.add(b);
/*  548: 768 */     this.extension[0] = 1;
/*  549: 771 */     if (this.ds_is_nii) {
/*  550: 772 */       this.vox_offset += size_code[0];
/*  551:     */     }
/*  552:     */   }
/*  553:     */   
/*  554:     */   public void writeHeader()
/*  555:     */     throws IOException, FileNotFoundException
/*  556:     */   {
/*  557: 797 */     int hsize = 348;
/*  558: 798 */     if ((this.ds_is_nii) || (this.extension[0] != 0)) {
/*  559: 799 */       hsize += 4;
/*  560:     */     }
/*  561:     */     ByteArrayOutputStream baos;
/*  562:     */     FileOutputStream fos;
/*  563:     */     EndianCorrectOutputStream ecs;
/*  564:     */     int i;
/*  565:     */     try
/*  566:     */     {
/*  567: 803 */       baos = new ByteArrayOutputStream(hsize);
/*  568: 804 */       fos = new FileOutputStream(this.ds_hdrname);
/*  569:     */       
/*  570: 806 */       ecs = new EndianCorrectOutputStream(baos, this.big_endian);
/*  571:     */       
/*  572:     */ 
/*  573: 809 */       ecs.writeIntCorrect(this.sizeof_hdr);
/*  574: 811 */       if (this.data_type_string.length() >= 10)
/*  575:     */       {
/*  576: 812 */         ecs.writeBytes(this.data_type_string.substring(0, 10));
/*  577:     */       }
/*  578:     */       else
/*  579:     */       {
/*  580: 815 */         ecs.writeBytes(this.data_type_string.toString());
/*  581: 816 */         for (int i = 0; i < 10 - this.data_type_string.length(); i++) {
/*  582: 817 */           ecs.writeByte(0);
/*  583:     */         }
/*  584:     */       }
/*  585: 820 */       if (this.db_name.length() >= 18)
/*  586:     */       {
/*  587: 821 */         ecs.writeBytes(this.db_name.substring(0, 18));
/*  588:     */       }
/*  589:     */       else
/*  590:     */       {
/*  591: 824 */         ecs.writeBytes(this.db_name.toString());
/*  592: 825 */         for (int i = 0; i < 18 - this.db_name.length(); i++) {
/*  593: 826 */           ecs.writeByte(0);
/*  594:     */         }
/*  595:     */       }
/*  596: 829 */       ecs.writeIntCorrect(this.extents);
/*  597:     */       
/*  598: 831 */       ecs.writeShortCorrect(this.session_error);
/*  599:     */       
/*  600: 833 */       ecs.writeByte(this.regular.charAt(0));
/*  601:     */       
/*  602: 835 */       byte b = packDimInfo(this.freq_dim, this.phase_dim, this.slice_dim);
/*  603: 836 */       ecs.writeByte(b);
/*  604: 838 */       for (i = 0; i < 8; i++) {
/*  605: 839 */         ecs.writeShortCorrect(this.dim[i]);
/*  606:     */       }
/*  607: 841 */       for (i = 0; i < 3; i++) {
/*  608: 842 */         ecs.writeFloatCorrect(this.intent[i]);
/*  609:     */       }
/*  610: 844 */       ecs.writeShortCorrect(this.intent_code);
/*  611:     */       
/*  612: 846 */       ecs.writeShortCorrect(this.datatype);
/*  613:     */       
/*  614: 848 */       ecs.writeShortCorrect(this.bitpix);
/*  615:     */       
/*  616: 850 */       ecs.writeShortCorrect(this.slice_start);
/*  617: 852 */       for (i = 0; i < 8; i++) {
/*  618: 853 */         ecs.writeFloatCorrect(this.pixdim[i]);
/*  619:     */       }
/*  620: 856 */       ecs.writeFloatCorrect(this.vox_offset);
/*  621:     */       
/*  622: 858 */       ecs.writeFloatCorrect(this.scl_slope);
/*  623: 859 */       ecs.writeFloatCorrect(this.scl_inter);
/*  624:     */       
/*  625: 861 */       ecs.writeShortCorrect(this.slice_end);
/*  626:     */       
/*  627: 863 */       ecs.writeByte(this.slice_code);
/*  628:     */       
/*  629: 865 */       ecs.writeByte(packUnits(this.xyz_unit_code, this.t_unit_code));
/*  630:     */       
/*  631:     */ 
/*  632: 868 */       ecs.writeFloatCorrect(this.cal_max);
/*  633: 869 */       ecs.writeFloatCorrect(this.cal_min);
/*  634:     */       
/*  635: 871 */       ecs.writeFloatCorrect(this.slice_duration);
/*  636:     */       
/*  637: 873 */       ecs.writeFloatCorrect(this.toffset);
/*  638:     */       
/*  639: 875 */       ecs.writeIntCorrect(this.glmax);
/*  640: 876 */       ecs.writeIntCorrect(this.glmin);
/*  641:     */       
/*  642: 878 */       ecs.write(setStringSize(this.descrip, 80), 0, 80);
/*  643: 879 */       ecs.write(setStringSize(this.aux_file, 24), 0, 24);
/*  644:     */       
/*  645:     */ 
/*  646: 882 */       ecs.writeShortCorrect(this.qform_code);
/*  647: 883 */       ecs.writeShortCorrect(this.sform_code);
/*  648: 885 */       for (i = 0; i < 3; i++) {
/*  649: 886 */         ecs.writeFloatCorrect(this.quatern[i]);
/*  650:     */       }
/*  651: 887 */       for (i = 0; i < 3; i++) {
/*  652: 888 */         ecs.writeFloatCorrect(this.qoffset[i]);
/*  653:     */       }
/*  654: 890 */       for (i = 0; i < 4; i++) {
/*  655: 891 */         ecs.writeFloatCorrect(this.srow_x[i]);
/*  656:     */       }
/*  657: 892 */       for (i = 0; i < 4; i++) {
/*  658: 893 */         ecs.writeFloatCorrect(this.srow_y[i]);
/*  659:     */       }
/*  660: 894 */       for (i = 0; i < 4; i++) {
/*  661: 895 */         ecs.writeFloatCorrect(this.srow_z[i]);
/*  662:     */       }
/*  663: 898 */       ecs.write(setStringSize(this.intent_name, 16), 0, 16);
/*  664: 899 */       ecs.write(setStringSize(this.magic, 4), 0, 4);
/*  665: 903 */       if ((this.ds_is_nii) || (this.extension[0] != 0)) {
/*  666: 904 */         for (i = 0; i < 4; i++) {
/*  667: 905 */           ecs.writeByte(this.extension[i]);
/*  668:     */         }
/*  669:     */       }
/*  670: 909 */       baos.writeTo(fos);
/*  671:     */     }
/*  672:     */     catch (IOException ex)
/*  673:     */     {
/*  674: 913 */       throw new IOException("Error: unable to write header file " + this.ds_hdrname + ": " + ex.getMessage());
/*  675:     */     }
/*  676:     */     try
/*  677:     */     {
/*  678: 922 */       if (this.extension[0] != 0)
/*  679:     */       {
/*  680: 924 */         baos = new ByteArrayOutputStream(8);
/*  681: 925 */         ecs = new EndianCorrectOutputStream(baos, this.big_endian);
/*  682: 926 */         int[][] extlist = getExtensionsList();
/*  683: 927 */         int n = extlist.length;
/*  684: 928 */         for (i = 0; i < n; i++)
/*  685:     */         {
/*  686: 930 */           ecs.writeIntCorrect(extlist[i][0]);
/*  687: 931 */           ecs.writeIntCorrect(extlist[i][1]);
/*  688: 932 */           baos.writeTo(fos);
/*  689: 933 */           baos.reset();
/*  690:     */           
/*  691:     */ 
/*  692: 936 */           byte[] ext_blob = (byte[])this.extension_blobs.get(i);
/*  693: 937 */           fos.write(ext_blob, 0, extlist[i][0] - 8);
/*  694:     */         }
/*  695:     */       }
/*  696: 941 */       fos.close();
/*  697:     */     }
/*  698:     */     catch (IOException ex)
/*  699:     */     {
/*  700: 945 */       throw new IOException("Error: unable to write header extensions for file " + this.ds_hdrname + ": " + ex.getMessage());
/*  701:     */     }
/*  702:     */   }
/*  703:     */   
/*  704:     */   public void printHeader()
/*  705:     */   {
/*  706: 961 */     System.out.println("\n");
/*  707: 962 */     System.out.println("Dataset header file:\t\t\t\t" + this.ds_hdrname);
/*  708: 963 */     System.out.println("Dataset data file:\t\t\t\t" + this.ds_datname);
/*  709: 964 */     System.out.println("Size of header:\t\t\t\t\t" + this.sizeof_hdr);
/*  710: 965 */     System.out.println("File offset to data blob:\t\t\t" + this.vox_offset);
/*  711:     */     
/*  712: 967 */     System.out.print("Endianness:\t\t\t\t\t");
/*  713: 968 */     if (this.big_endian) {
/*  714: 969 */       System.out.println("big");
/*  715:     */     } else {
/*  716: 971 */       System.out.println("little");
/*  717:     */     }
/*  718: 973 */     System.out.println("Magic filetype string:\t\t\t\t" + this.magic);
/*  719:     */     
/*  720:     */ 
/*  721:     */ 
/*  722:     */ 
/*  723: 978 */     System.out.println("Datatype:\t\t\t\t\t" + this.datatype + " (" + decodeDatatype(this.datatype) + ")");
/*  724: 979 */     System.out.println("Bits per voxel:\t\t\t\t\t" + this.bitpix);
/*  725: 980 */     System.out.println("Scaling slope and intercept:\t\t\t" + this.scl_slope + " " + this.scl_inter);
/*  726:     */     
/*  727:     */ 
/*  728: 983 */     System.out.print("Dataset dimensions (Count, X,Y,Z,T...):\t\t");
/*  729: 984 */     for (int i = 0; i <= this.dim[0]; i++) {
/*  730: 985 */       System.out.print(this.dim[i] + " ");
/*  731:     */     }
/*  732: 986 */     System.out.println("");
/*  733:     */     
/*  734: 988 */     System.out.print("Grid spacings (X,Y,Z,T,...):\t\t\t");
/*  735: 989 */     for (i = 1; i <= this.dim[0]; i++) {
/*  736: 990 */       System.out.print(this.pixdim[i] + " ");
/*  737:     */     }
/*  738: 991 */     System.out.println("");
/*  739:     */     
/*  740: 993 */     System.out.println("XYZ  units:\t\t\t\t\t" + this.xyz_unit_code + " (" + decodeUnits(this.xyz_unit_code) + ")");
/*  741: 994 */     System.out.println("T units:\t\t\t\t\t" + this.t_unit_code + " (" + decodeUnits(this.t_unit_code) + ")");
/*  742: 995 */     System.out.println("T offset:\t\t\t\t\t" + this.toffset);
/*  743:     */     
/*  744:     */ 
/*  745: 998 */     System.out.print("Intent parameters:\t\t\t\t");
/*  746: 999 */     for (i = 0; i < 3; i++) {
/*  747:1000 */       System.out.print(this.intent[i] + " ");
/*  748:     */     }
/*  749:1001 */     System.out.println("");
/*  750:1002 */     System.out.println("Intent code:\t\t\t\t\t" + this.intent_code + " (" + decodeIntent(this.intent_code) + ")");
/*  751:     */     
/*  752:1004 */     System.out.println("Cal. (display) max/min:\t\t\t\t" + this.cal_max + " " + this.cal_min);
/*  753:     */     
/*  754:     */ 
/*  755:     */ 
/*  756:1008 */     System.out.println("Slice timing code:\t\t\t\t" + this.slice_code + " (" + decodeSliceOrder((short)this.slice_code) + ")");
/*  757:1009 */     System.out.println("MRI slice ordering (freq, phase, slice index):\t" + this.freq_dim + " " + this.phase_dim + " " + this.slice_dim);
/*  758:     */     
/*  759:1011 */     System.out.println("Start/end slice:\t\t\t\t" + this.slice_start + " " + this.slice_end);
/*  760:1012 */     System.out.println("Slice duration:\t\t\t\t\t" + this.slice_duration);
/*  761:     */     
/*  762:     */ 
/*  763:     */ 
/*  764:1016 */     System.out.println("Q factor:\t\t\t\t\t" + this.qfac);
/*  765:1017 */     System.out.println("Qform transform code:\t\t\t\t" + this.qform_code + " (" + decodeXform(this.qform_code) + ")");
/*  766:1018 */     System.out.println("Quaternion b,c,d params:\t\t\t" + this.quatern[0] + " " + this.quatern[1] + " " + this.quatern[2]);
/*  767:1019 */     System.out.println("Quaternion x,y,z shifts:\t\t\t" + this.qoffset[0] + " " + this.qoffset[1] + " " + this.qoffset[2]);
/*  768:     */     
/*  769:1021 */     System.out.println("Affine transform code:\t\t\t\t" + this.sform_code + " (" + decodeXform(this.sform_code) + ")");
/*  770:1022 */     System.out.print("1st row affine transform:\t\t\t");
/*  771:1023 */     for (i = 0; i < 4; i++) {
/*  772:1024 */       System.out.print(this.srow_x[i] + " ");
/*  773:     */     }
/*  774:1025 */     System.out.println("");
/*  775:1026 */     System.out.print("2nd row affine transform:\t\t\t");
/*  776:1027 */     for (i = 0; i < 4; i++) {
/*  777:1028 */       System.out.print(this.srow_y[i] + " ");
/*  778:     */     }
/*  779:1029 */     System.out.println("");
/*  780:1030 */     System.out.print("3rd row affine transform:\t\t\t");
/*  781:1031 */     for (i = 0; i < 4; i++) {
/*  782:1032 */       System.out.print(this.srow_z[i] + " ");
/*  783:     */     }
/*  784:1033 */     System.out.println("");
/*  785:     */     
/*  786:     */ 
/*  787:     */ 
/*  788:1037 */     System.out.println("Description:\t\t\t\t\t" + this.descrip);
/*  789:1038 */     System.out.println("Intent name:\t\t\t\t\t" + this.intent_name);
/*  790:1039 */     System.out.println("Auxiliary file:\t\t\t\t\t" + this.aux_file);
/*  791:1040 */     System.out.println("Extension byte 1:\t\t\t\t\t" + this.extension[0]);
/*  792:     */     
/*  793:     */ 
/*  794:     */ 
/*  795:1044 */     System.out.println("\n\nUnused Fields");
/*  796:1045 */     System.out.println("----------------------------------------------------------------------");
/*  797:1046 */     System.out.println("Data type string:\t\t\t" + this.data_type_string);
/*  798:1047 */     System.out.println("db_name:\t\t\t\t\t" + this.db_name);
/*  799:1048 */     System.out.println("extents:\t\t\t\t\t" + this.extents);
/*  800:1049 */     System.out.println("session_error:\t\t\t\t\t" + this.session_error);
/*  801:1050 */     System.out.println("regular:\t\t\t\t\t" + this.regular);
/*  802:1051 */     System.out.println("glmax/glmin:\t\t\t\t\t" + this.glmax + " " + this.glmin);
/*  803:1052 */     System.out.println("Extension bytes 2-4:\t\t\t\t" + this.extension[1] + " " + this.extension[2] + " " + this.extension[3]);
/*  804:1056 */     if (this.extension[0] != 0)
/*  805:     */     {
/*  806:1057 */       int[][] extlist = getExtensionsList();
/*  807:1058 */       int n = extlist.length;
/*  808:1059 */       System.out.println("\n\nExtensions");
/*  809:1060 */       System.out.println("----------------------------------------------------------------------");
/*  810:1061 */       System.out.println("#\tCode\tSize");
/*  811:1062 */       for (i = 0; i < n; i++) {
/*  812:1063 */         System.out.println(i + 1 + "\t" + extlist[i][1] + "\t" + extlist[i][0]);
/*  813:     */       }
/*  814:1064 */       System.out.println("\n");
/*  815:     */     }
/*  816:     */   }
/*  817:     */   
/*  818:     */   public void printDoubleTmcrs(double[] d)
/*  819:     */   {
/*  820:1082 */     NumberFormat nf = NumberFormat.getInstance();
/*  821:1083 */     nf.setMaximumFractionDigits(6);
/*  822:1084 */     nf.setGroupingUsed(false);
/*  823:1086 */     for (short i = 0; i < this.TDIM; i = (short)(i + 1)) {
/*  824:1087 */       System.out.println(nf.format(d[i]));
/*  825:     */     }
/*  826:     */   }
/*  827:     */   
/*  828:     */   public boolean exists()
/*  829:     */   {
/*  830:1102 */     return (existsHdr()) && (existsDat());
/*  831:     */   }
/*  832:     */   
/*  833:     */   public boolean existsHdr()
/*  834:     */   {
/*  835:1111 */     File f = new File(this.ds_hdrname);
/*  836:1112 */     if (f.exists()) {
/*  837:1113 */       return true;
/*  838:     */     }
/*  839:1114 */     f = new File(this.ds_hdrname + ".gz");
/*  840:1115 */     if (f.exists()) {
/*  841:1116 */       return true;
/*  842:     */     }
/*  843:1117 */     return false;
/*  844:     */   }
/*  845:     */   
/*  846:     */   public boolean existsDat()
/*  847:     */   {
/*  848:1126 */     File f = new File(this.ds_datname);
/*  849:1127 */     if (f.exists()) {
/*  850:1128 */       return true;
/*  851:     */     }
/*  852:1129 */     f = new File(this.ds_datname + ".gz");
/*  853:1130 */     if (f.exists()) {
/*  854:1131 */       return true;
/*  855:     */     }
/*  856:1132 */     return false;
/*  857:     */   }
/*  858:     */   
/*  859:     */   public void setHeaderFilename(String s)
/*  860:     */   {
/*  861:1142 */     if (s.endsWith(".nii")) {
/*  862:1143 */       setToNii();
/*  863:     */     } else {
/*  864:1145 */       setToNi1();
/*  865:     */     }
/*  866:1147 */     this.ds_hdrname = s;
/*  867:1148 */     if (this.ds_is_nii)
/*  868:     */     {
/*  869:1149 */       if (!this.ds_hdrname.endsWith(".nii")) {
/*  870:1150 */         this.ds_hdrname += ".nii";
/*  871:     */       }
/*  872:     */     }
/*  873:1153 */     else if (!this.ds_hdrname.endsWith(".hdr")) {
/*  874:1154 */       this.ds_hdrname += ".hdr";
/*  875:     */     }
/*  876:     */   }
/*  877:     */   
/*  878:     */   public String getHeaderFilename()
/*  879:     */   {
/*  880:1165 */     return this.ds_hdrname;
/*  881:     */   }
/*  882:     */   
/*  883:     */   public void setDataFilename(String s)
/*  884:     */   {
/*  885:1176 */     this.ds_datname = s;
/*  886:1177 */     if (this.ds_is_nii)
/*  887:     */     {
/*  888:1178 */       if (!this.ds_datname.endsWith(".nii")) {
/*  889:1179 */         this.ds_datname += ".nii";
/*  890:     */       }
/*  891:     */     }
/*  892:1182 */     else if (!this.ds_datname.endsWith(".img")) {
/*  893:1183 */       this.ds_datname += ".img";
/*  894:     */     }
/*  895:     */   }
/*  896:     */   
/*  897:     */   public String getDataFilename()
/*  898:     */   {
/*  899:1195 */     return this.ds_datname;
/*  900:     */   }
/*  901:     */   
/*  902:     */   private void setToNii()
/*  903:     */   {
/*  904:1216 */     this.ds_is_nii = true;
/*  905:1217 */     this.magic = new StringBuffer("n+1");
/*  906:     */     
/*  907:1219 */     this.vox_offset = 352.0F;
/*  908:1220 */     if (this.extension[0] != 0)
/*  909:     */     {
/*  910:1221 */       int[][] extlist = getExtensionsList();
/*  911:1222 */       int n = extlist.length;
/*  912:1223 */       for (int i = 0; i < n; i++) {
/*  913:1224 */         this.vox_offset += extlist[i][0];
/*  914:     */       }
/*  915:     */     }
/*  916:     */   }
/*  917:     */   
/*  918:     */   private void setToNi1()
/*  919:     */   {
/*  920:1247 */     this.ds_is_nii = false;
/*  921:1248 */     this.magic = new StringBuffer("ni1");
/*  922:     */     
/*  923:     */ 
/*  924:     */ 
/*  925:1252 */     this.vox_offset = 0.0F;
/*  926:     */   }
/*  927:     */   
/*  928:     */   public void setDims(short a, short x, short y, short z, short t, short d5, short d6, short d7)
/*  929:     */   {
/*  930:1265 */     this.dim[0] = a;
/*  931:1266 */     this.dim[1] = x;
/*  932:1267 */     this.dim[2] = y;
/*  933:1268 */     this.dim[3] = z;
/*  934:1269 */     this.dim[4] = t;
/*  935:1270 */     this.dim[5] = d5;
/*  936:1271 */     this.dim[6] = d6;
/*  937:1272 */     this.dim[7] = d7;
/*  938:     */     
/*  939:1274 */     this.XDIM = x;
/*  940:1275 */     this.YDIM = y;
/*  941:1276 */     this.ZDIM = z;
/*  942:1277 */     this.TDIM = t;
/*  943:     */   }
/*  944:     */   
/*  945:     */   public void setDatatype(short code)
/*  946:     */   {
/*  947:1289 */     this.datatype = code;
/*  948:1290 */     this.bitpix = ((short)(bytesPerVoxel(code) * 8));
/*  949:     */   }
/*  950:     */   
/*  951:     */   public short getDatatype()
/*  952:     */   {
/*  953:1301 */     return this.datatype;
/*  954:     */   }
/*  955:     */   
/*  956:     */   public short getBitpix()
/*  957:     */   {
/*  958:1310 */     return this.bitpix;
/*  959:     */   }
/*  960:     */   
/*  961:     */   public String decodeIntent(short icode)
/*  962:     */   {
/*  963:1322 */     switch (icode)
/*  964:     */     {
/*  965:     */     case 0: 
/*  966:1325 */       return "NIFTI_INTENT_NONE";
/*  967:     */     case 2: 
/*  968:1327 */       return "NIFTI_INTENT_CORREL";
/*  969:     */     case 3: 
/*  970:1329 */       return "NIFTI_INTENT_TTEST";
/*  971:     */     case 4: 
/*  972:1331 */       return "NIFTI_INTENT_FTEST";
/*  973:     */     case 5: 
/*  974:1333 */       return "NIFTI_INTENT_ZSCORE";
/*  975:     */     case 6: 
/*  976:1335 */       return "NIFTI_INTENT_CHISQ";
/*  977:     */     case 7: 
/*  978:1337 */       return "NIFTI_INTENT_BETA";
/*  979:     */     case 8: 
/*  980:1339 */       return "NIFTI_INTENT_BINOM";
/*  981:     */     case 9: 
/*  982:1341 */       return "NIFTI_INTENT_GAMMA";
/*  983:     */     case 10: 
/*  984:1343 */       return "NIFTI_INTENT_POISSON";
/*  985:     */     case 11: 
/*  986:1345 */       return "NIFTI_INTENT_NORMAL";
/*  987:     */     case 12: 
/*  988:1347 */       return "NIFTI_INTENT_FTEST_NONC";
/*  989:     */     case 13: 
/*  990:1349 */       return "NIFTI_INTENT_CHISQ_NONC";
/*  991:     */     case 14: 
/*  992:1351 */       return "NIFTI_INTENT_LOGISTIC";
/*  993:     */     case 15: 
/*  994:1353 */       return "NIFTI_INTENT_LAPLACE";
/*  995:     */     case 16: 
/*  996:1355 */       return "NIFTI_INTENT_UNIFORM";
/*  997:     */     case 17: 
/*  998:1357 */       return "NIFTI_INTENT_TTEST_NONC";
/*  999:     */     case 18: 
/* 1000:1359 */       return "NIFTI_INTENT_WEIBULL";
/* 1001:     */     case 19: 
/* 1002:1361 */       return "NIFTI_INTENT_CHI";
/* 1003:     */     case 20: 
/* 1004:1363 */       return "NIFTI_INTENT_INVGAUSS";
/* 1005:     */     case 21: 
/* 1006:1365 */       return "NIFTI_INTENT_EXTVAL";
/* 1007:     */     case 22: 
/* 1008:1367 */       return "NIFTI_INTENT_PVAL";
/* 1009:     */     case 1001: 
/* 1010:1369 */       return "NIFTI_INTENT_ESTIMATE";
/* 1011:     */     case 1002: 
/* 1012:1371 */       return "NIFTI_INTENT_LABEL";
/* 1013:     */     case 1003: 
/* 1014:1373 */       return "NIFTI_INTENT_NEURONAME";
/* 1015:     */     case 1004: 
/* 1016:1375 */       return "NIFTI_INTENT_GENMATRIX";
/* 1017:     */     case 1005: 
/* 1018:1377 */       return "NIFTI_INTENT_SYMMATRIX";
/* 1019:     */     case 1006: 
/* 1020:1379 */       return "NIFTI_INTENT_DISPVECT";
/* 1021:     */     case 1007: 
/* 1022:1381 */       return "NIFTI_INTENT_VECTOR";
/* 1023:     */     case 1008: 
/* 1024:1383 */       return "NIFTI_INTENT_POINTSET";
/* 1025:     */     case 1009: 
/* 1026:1385 */       return "NIFTI_INTENT_TRIANGLE";
/* 1027:     */     case 1010: 
/* 1028:1387 */       return "NIFTI_INTENT_QUATERNION";
/* 1029:     */     }
/* 1030:1389 */     return "INVALID_NIFTI_INTENT_CODE";
/* 1031:     */   }
/* 1032:     */   
/* 1033:     */   public String decodeDatatype(short dcode)
/* 1034:     */   {
/* 1035:1402 */     switch (dcode)
/* 1036:     */     {
/* 1037:     */     case 0: 
/* 1038:1405 */       return "DT_NONE";
/* 1039:     */     case 1: 
/* 1040:1407 */       return "DT_BINARY";
/* 1041:     */     case 2: 
/* 1042:1409 */       return "NIFTI_TYPE_UINT8";
/* 1043:     */     case 4: 
/* 1044:1411 */       return "NIFTI_TYPE_INT16";
/* 1045:     */     case 8: 
/* 1046:1413 */       return "NIFTI_TYPE_INT32";
/* 1047:     */     case 16: 
/* 1048:1415 */       return "NIFTI_TYPE_FLOAT32";
/* 1049:     */     case 32: 
/* 1050:1417 */       return "NIFTI_TYPE_COMPLEX64";
/* 1051:     */     case 64: 
/* 1052:1419 */       return "NIFTI_TYPE_FLOAT64";
/* 1053:     */     case 128: 
/* 1054:1421 */       return "NIFTI_TYPE_RGB24";
/* 1055:     */     case 255: 
/* 1056:1423 */       return "DT_ALL";
/* 1057:     */     case 256: 
/* 1058:1425 */       return "NIFTI_TYPE_INT8";
/* 1059:     */     case 512: 
/* 1060:1427 */       return "NIFTI_TYPE_UINT16";
/* 1061:     */     case 768: 
/* 1062:1429 */       return "NIFTI_TYPE_UINT32";
/* 1063:     */     case 1024: 
/* 1064:1431 */       return "NIFTI_TYPE_INT64";
/* 1065:     */     case 1280: 
/* 1066:1433 */       return "NIFTI_TYPE_UINT64";
/* 1067:     */     case 1536: 
/* 1068:1435 */       return "NIFTI_TYPE_FLOAT128";
/* 1069:     */     case 1792: 
/* 1070:1437 */       return "NIFTI_TYPE_COMPLEX128";
/* 1071:     */     case 2048: 
/* 1072:1439 */       return "NIFTI_TYPE_COMPLEX256";
/* 1073:     */     }
/* 1074:1441 */     return "INVALID_NIFTI_DATATYPE_CODE";
/* 1075:     */   }
/* 1076:     */   
/* 1077:     */   public short bytesPerVoxel(short dcode)
/* 1078:     */   {
/* 1079:1455 */     switch (dcode)
/* 1080:     */     {
/* 1081:     */     case 0: 
/* 1082:1458 */       return 0;
/* 1083:     */     case 1: 
/* 1084:1460 */       return -1;
/* 1085:     */     case 2: 
/* 1086:1462 */       return 1;
/* 1087:     */     case 4: 
/* 1088:1464 */       return 2;
/* 1089:     */     case 8: 
/* 1090:1466 */       return 4;
/* 1091:     */     case 16: 
/* 1092:1468 */       return 4;
/* 1093:     */     case 32: 
/* 1094:1470 */       return 8;
/* 1095:     */     case 64: 
/* 1096:1472 */       return 8;
/* 1097:     */     case 128: 
/* 1098:1474 */       return 3;
/* 1099:     */     case 255: 
/* 1100:1476 */       return 0;
/* 1101:     */     case 256: 
/* 1102:1478 */       return 1;
/* 1103:     */     case 512: 
/* 1104:1480 */       return 2;
/* 1105:     */     case 768: 
/* 1106:1482 */       return 4;
/* 1107:     */     case 1024: 
/* 1108:1484 */       return 8;
/* 1109:     */     case 1280: 
/* 1110:1486 */       return 8;
/* 1111:     */     case 1536: 
/* 1112:1488 */       return 16;
/* 1113:     */     case 1792: 
/* 1114:1490 */       return 16;
/* 1115:     */     case 2048: 
/* 1116:1492 */       return 32;
/* 1117:     */     }
/* 1118:1494 */     return 0;
/* 1119:     */   }
/* 1120:     */   
/* 1121:     */   public String decodeSliceOrder(short code)
/* 1122:     */   {
/* 1123:1507 */     switch (code)
/* 1124:     */     {
/* 1125:     */     case 1: 
/* 1126:1510 */       return "NIFTI_SLICE_SEQ_INC";
/* 1127:     */     case 2: 
/* 1128:1512 */       return "NIFTI_SLICE_SEQ_DEC";
/* 1129:     */     case 3: 
/* 1130:1514 */       return "NIFTI_SLICE_ALT_INC";
/* 1131:     */     case 4: 
/* 1132:1516 */       return "NIFTI_SLICE_ALT_DEC";
/* 1133:     */     }
/* 1134:1518 */     return "INVALID_NIFTI_SLICE_SEQ_CODE";
/* 1135:     */   }
/* 1136:     */   
/* 1137:     */   public String decodeXform(short code)
/* 1138:     */   {
/* 1139:1531 */     switch (code)
/* 1140:     */     {
/* 1141:     */     case 0: 
/* 1142:1533 */       return "NIFTI_XFORM_UNKNOWN";
/* 1143:     */     case 1: 
/* 1144:1535 */       return "NIFTI_XFORM_SCANNER_ANAT";
/* 1145:     */     case 2: 
/* 1146:1537 */       return "NIFTI_XFORM_ALIGNED_ANAT";
/* 1147:     */     case 3: 
/* 1148:1539 */       return "NIFTI_XFORM_TALAIRACH";
/* 1149:     */     case 4: 
/* 1150:1541 */       return "NIFTI_XFORM_MNI_152";
/* 1151:     */     }
/* 1152:1543 */     return "INVALID_NIFTI_XFORM_CODE";
/* 1153:     */   }
/* 1154:     */   
/* 1155:     */   public String decodeUnits(short code)
/* 1156:     */   {
/* 1157:1556 */     switch (code)
/* 1158:     */     {
/* 1159:     */     case 0: 
/* 1160:1558 */       return "NIFTI_UNITS_UNKNOWN";
/* 1161:     */     case 1: 
/* 1162:1560 */       return "NIFTI_UNITS_METER";
/* 1163:     */     case 2: 
/* 1164:1562 */       return "NIFTI_UNITS_MM";
/* 1165:     */     case 3: 
/* 1166:1564 */       return "NIFTI_UNITS_MICRON";
/* 1167:     */     case 8: 
/* 1168:1566 */       return "NIFTI_UNITS_SEC";
/* 1169:     */     case 16: 
/* 1170:1568 */       return "NIFTI_UNITS_MSEC";
/* 1171:     */     case 24: 
/* 1172:1570 */       return "NIFTI_UNITS_USEC";
/* 1173:     */     case 32: 
/* 1174:1572 */       return "NIFTI_UNITS_HZ";
/* 1175:     */     case 40: 
/* 1176:1574 */       return "NIFTI_UNITS_PPM";
/* 1177:     */     }
/* 1178:1576 */     return "INVALID_NIFTI_UNITS_CODE";
/* 1179:     */   }
/* 1180:     */   
/* 1181:     */   public short checkHeader()
/* 1182:     */   {
/* 1183:1595 */     return 0;
/* 1184:     */   }
/* 1185:     */   
/* 1186:     */   private void checkName(String name)
/* 1187:     */   {
/* 1188:1617 */     String wname = new String(name);
/* 1189:1621 */     if (wname.endsWith(".gz")) {
/* 1190:1622 */       wname = wname.substring(0, wname.length() - ".gz".length());
/* 1191:     */     }
/* 1192:1629 */     if ((!wname.endsWith(".hdr")) && (!wname.endsWith(".img")) && (!wname.endsWith(".nii")))
/* 1193:     */     {
/* 1194:1631 */       File nii_file = new File(wname + ".nii");
/* 1195:1632 */       File niig_file = new File(wname + ".nii" + ".gz");
/* 1196:1633 */       File anzh_file = new File(wname + ".hdr");
/* 1197:1634 */       File anzhg_file = new File(wname + ".hdr" + ".gz");
/* 1198:1635 */       File anzd_file = new File(wname + ".img");
/* 1199:1636 */       File anzdg_file = new File(wname + ".img" + ".gz");
/* 1200:1638 */       if (nii_file.exists()) {
/* 1201:1639 */         wname = wname + ".nii";
/* 1202:1640 */       } else if (niig_file.exists()) {
/* 1203:1641 */         wname = wname + ".nii";
/* 1204:1642 */       } else if (anzh_file.exists()) {
/* 1205:1643 */         wname = wname + ".hdr";
/* 1206:1644 */       } else if (anzhg_file.exists()) {
/* 1207:1645 */         wname = wname + ".hdr";
/* 1208:1646 */       } else if (anzd_file.exists()) {
/* 1209:1647 */         wname = wname + ".hdr";
/* 1210:1648 */       } else if (anzdg_file.exists()) {
/* 1211:1649 */         wname = wname + ".hdr";
/* 1212:     */       }
/* 1213:     */     }
/* 1214:1653 */     if (wname.endsWith(".hdr"))
/* 1215:     */     {
/* 1216:1654 */       this.ds_hdrname = new String(wname);
/* 1217:1655 */       this.ds_datname = (wname.substring(0, wname.length() - ".hdr".length()) + ".img");
/* 1218:     */     }
/* 1219:1658 */     else if (wname.endsWith(".img"))
/* 1220:     */     {
/* 1221:1659 */       this.ds_datname = new String(wname);
/* 1222:1660 */       this.ds_hdrname = (wname.substring(0, wname.length() - ".img".length()) + ".hdr");
/* 1223:     */     }
/* 1224:1663 */     else if (wname.endsWith(".nii"))
/* 1225:     */     {
/* 1226:1664 */       this.ds_datname = new String(wname);
/* 1227:1665 */       this.ds_hdrname = new String(wname);
/* 1228:1666 */       this.ds_is_nii = true;
/* 1229:     */     }
/* 1230:1671 */     File f = new File(this.ds_hdrname + ".gz");
/* 1231:1672 */     if (f.exists()) {
/* 1232:1673 */       this.ds_hdrname += ".gz";
/* 1233:     */     }
/* 1234:1674 */     f = new File(this.ds_datname + ".gz");
/* 1235:1675 */     if (f.exists()) {
/* 1236:1676 */       this.ds_datname += ".gz";
/* 1237:     */     }
/* 1238:     */   }
/* 1239:     */   
/* 1240:     */   private void setDefaults()
/* 1241:     */   {
/* 1242:1693 */     this.ds_hdrname = new String("");
/* 1243:1694 */     this.ds_datname = new String("");
/* 1244:1695 */     this.ds_is_nii = false;
/* 1245:1696 */     ByteOrder bo = ByteOrder.nativeOrder();
/* 1246:1697 */     if (bo == ByteOrder.BIG_ENDIAN) {
/* 1247:1698 */       this.big_endian = true;
/* 1248:     */     } else {
/* 1249:1700 */       this.big_endian = false;
/* 1250:     */     }
/* 1251:1701 */     this.sizeof_hdr = 348;
/* 1252:1702 */     this.data_type_string = new StringBuffer();
/* 1253:1703 */     for (int i = 0; i < 10; i++) {
/* 1254:1704 */       this.data_type_string.append("");
/* 1255:     */     }
/* 1256:1705 */     this.db_name = new StringBuffer();
/* 1257:1706 */     for (i = 0; i < 18; i++) {
/* 1258:1707 */       this.db_name.append("");
/* 1259:     */     }
/* 1260:1708 */     this.extents = 0;
/* 1261:1709 */     this.session_error = 0;
/* 1262:1710 */     this.regular = new StringBuffer("");
/* 1263:1711 */     this.dim_info = new StringBuffer("");
/* 1264:1712 */     this.freq_dim = 0;this.phase_dim = 0;this.slice_dim = 0;
/* 1265:1713 */     this.dim = new short[8];
/* 1266:1714 */     for (i = 0; i < 8; i++) {
/* 1267:1715 */       this.dim[i] = 0;
/* 1268:     */     }
/* 1269:1716 */     this.XDIM = 0;this.YDIM = 0;this.ZDIM = 0;this.TDIM = 0;
/* 1270:1717 */     this.intent = new float[3];
/* 1271:1718 */     for (i = 0; i < 3; i++) {
/* 1272:1719 */       this.intent[i] = 0.0F;
/* 1273:     */     }
/* 1274:1720 */     this.intent_code = 0;
/* 1275:1721 */     this.datatype = 0;
/* 1276:1722 */     this.bitpix = 0;
/* 1277:1723 */     this.slice_start = 0;
/* 1278:1724 */     this.pixdim = new float[8];
/* 1279:1725 */     this.pixdim[0] = 1.0F;this.qfac = 1;
/* 1280:1726 */     for (i = 1; i < 8; i++) {
/* 1281:1727 */       this.pixdim[i] = 0.0F;
/* 1282:     */     }
/* 1283:1729 */     this.vox_offset = 0.0F;
/* 1284:     */     
/* 1285:     */ 
/* 1286:1732 */     this.scl_slope = 0.0F;
/* 1287:1733 */     this.scl_inter = 0.0F;
/* 1288:1734 */     this.slice_end = 0;
/* 1289:1735 */     this.slice_code = 0;
/* 1290:1736 */     this.xyzt_units = 0;
/* 1291:1737 */     this.xyz_unit_code = 0;
/* 1292:1738 */     this.t_unit_code = 0;
/* 1293:     */     
/* 1294:1740 */     this.cal_max = 0.0F;
/* 1295:1741 */     this.cal_min = 0.0F;
/* 1296:1742 */     this.slice_duration = 0.0F;
/* 1297:1743 */     this.toffset = 0.0F;
/* 1298:1744 */     this.glmax = 0;
/* 1299:1745 */     this.glmin = 0;
/* 1300:     */     
/* 1301:1747 */     this.descrip = new StringBuffer();
/* 1302:1748 */     for (i = 0; i < 80; i++) {
/* 1303:1749 */       this.descrip.append("");
/* 1304:     */     }
/* 1305:1750 */     this.aux_file = new StringBuffer();
/* 1306:1751 */     for (i = 0; i < 24; i++) {
/* 1307:1752 */       this.aux_file.append("");
/* 1308:     */     }
/* 1309:1755 */     this.qform_code = 0;
/* 1310:1756 */     this.sform_code = 0;
/* 1311:     */     
/* 1312:1758 */     this.quatern = new float[3];
/* 1313:1759 */     this.qoffset = new float[3];
/* 1314:1760 */     for (i = 0; i < 3; i++)
/* 1315:     */     {
/* 1316:1761 */       this.quatern[i] = 0.0F;
/* 1317:1762 */       this.qoffset[i] = 0.0F;
/* 1318:     */     }
/* 1319:1765 */     this.srow_x = new float[4];
/* 1320:1766 */     this.srow_y = new float[4];
/* 1321:1767 */     this.srow_z = new float[4];
/* 1322:1768 */     for (i = 0; i < 4; i++)
/* 1323:     */     {
/* 1324:1769 */       this.srow_x[i] = 0.0F;
/* 1325:1770 */       this.srow_y[i] = 0.0F;
/* 1326:1771 */       this.srow_z[i] = 0.0F;
/* 1327:     */     }
/* 1328:1774 */     this.intent_name = new StringBuffer();
/* 1329:1775 */     for (i = 0; i < 16; i++) {
/* 1330:1776 */       this.intent_name.append("");
/* 1331:     */     }
/* 1332:1779 */     this.magic = new StringBuffer("n+1");
/* 1333:     */     
/* 1334:1781 */     this.extension = new byte[4];
/* 1335:1782 */     for (i = 0; i < 4; i++) {
/* 1336:1783 */       this.extension[i] = 0;
/* 1337:     */     }
/* 1338:1785 */     this.extensions_list = new Vector(5);
/* 1339:1786 */     this.extension_blobs = new Vector(5);
/* 1340:     */   }
/* 1341:     */   
/* 1342:     */   private short[] unpackDimInfo(int b)
/* 1343:     */   {
/* 1344:1802 */     short[] s = new short[3];
/* 1345:1803 */     s[0] = ((short)(b & 0x3));
/* 1346:1804 */     s[1] = ((short)(b >>> 2 & 0x3));
/* 1347:1805 */     s[2] = ((short)(b >>> 4 & 0x3));
/* 1348:1806 */     return s;
/* 1349:     */   }
/* 1350:     */   
/* 1351:     */   private byte packDimInfo(short freq, short phase, short slice)
/* 1352:     */   {
/* 1353:1810 */     int i = 0;
/* 1354:     */     
/* 1355:1812 */     i = (i & slice & 0x3) << 2;
/* 1356:1813 */     i = (i & phase & 0x3) << 2;
/* 1357:1814 */     i &= freq & 0x3;
/* 1358:1815 */     return (byte)i;
/* 1359:     */   }
/* 1360:     */   
/* 1361:     */   private short[] unpackUnits(int b)
/* 1362:     */   {
/* 1363:1830 */     short[] s = new short[2];
/* 1364:1831 */     s[0] = ((short)(b & 0x7));
/* 1365:1832 */     s[1] = ((short)(b & 0x38));
/* 1366:1833 */     return s;
/* 1367:     */   }
/* 1368:     */   
/* 1369:     */   private byte packUnits(short space, short time)
/* 1370:     */   {
/* 1371:1839 */     return (byte)(space & 0x7 | time & 0x38);
/* 1372:     */   }
/* 1373:     */   
/* 1374:     */   public double[][][] readDoubleVol(short ttt)
/* 1375:     */     throws IOException
/* 1376:     */   {
/* 1377:1863 */     short ZZZ = this.ZDIM;
/* 1378:1864 */     if (this.dim[0] == 2) {
/* 1379:1865 */       ZZZ = 1;
/* 1380:     */     }
/* 1381:1869 */     double[][][] data = new double[ZZZ][this.YDIM][this.XDIM];
/* 1382:     */     
/* 1383:     */ 
/* 1384:1872 */     byte[] b = readVolBlob(ttt);
/* 1385:     */     
/* 1386:     */ 
/* 1387:     */ 
/* 1388:1876 */     EndianCorrectInputStream ecs = new EndianCorrectInputStream(new ByteArrayInputStream(b), this.big_endian);
/* 1389:     */     int k;
/* 1390:1877 */     switch (this.datatype)
/* 1391:     */     {
/* 1392:     */     case 2: 
/* 1393:     */     case 256: 
/* 1394:1881 */       for (k = 0; k < ZZZ;)
/* 1395:     */       {
/* 1396:1882 */         for (j = 0; j < this.YDIM; j++) {
/* 1397:1883 */           for (int i = 0; i < this.XDIM; i++)
/* 1398:     */           {
/* 1399:1884 */             data[k][j][i] = ecs.readByte();
/* 1400:1885 */             if ((this.datatype == 2) && (data[k][j][i] < 0.0D)) {
/* 1401:1886 */               data[k][j][i] = (Math.abs(data[k][j][i]) + 128.0D);
/* 1402:     */             }
/* 1403:1887 */             if (this.scl_slope != 0.0F) {
/* 1404:1888 */               data[k][j][i] = (data[k][j][i] * this.scl_slope + this.scl_inter);
/* 1405:     */             }
/* 1406:     */           }
/* 1407:     */         }
/* 1408:1881 */         k++; continue;
/* 1409:1894 */         for (k = 0; k < ZZZ;)
/* 1410:     */         {
/* 1411:1895 */           for (j = 0; j < this.YDIM; j++) {
/* 1412:1896 */             for (int i = 0; i < this.XDIM; i++)
/* 1413:     */             {
/* 1414:1897 */               data[k][j][i] = ecs.readShortCorrect();
/* 1415:1898 */               if ((this.datatype == 512) && (data[k][j][i] < 0.0D)) {
/* 1416:1899 */                 data[k][j][i] = (Math.abs(data[k][j][i]) + 32768.0D);
/* 1417:     */               }
/* 1418:1900 */               if (this.scl_slope != 0.0F) {
/* 1419:1901 */                 data[k][j][i] = (data[k][j][i] * this.scl_slope + this.scl_inter);
/* 1420:     */               }
/* 1421:     */             }
/* 1422:     */           }
/* 1423:1894 */           k++; continue;
/* 1424:1907 */           for (k = 0; k < ZZZ;)
/* 1425:     */           {
/* 1426:1908 */             for (j = 0; j < this.YDIM; j++) {
/* 1427:1909 */               for (int i = 0; i < this.XDIM; i++)
/* 1428:     */               {
/* 1429:1910 */                 data[k][j][i] = ecs.readIntCorrect();
/* 1430:1911 */                 if ((this.datatype == 768) && (data[k][j][i] < 0.0D)) {
/* 1431:1912 */                   data[k][j][i] = (Math.abs(data[k][j][i]) + -2147483648.0D);
/* 1432:     */                 }
/* 1433:1913 */                 if (this.scl_slope != 0.0F) {
/* 1434:1914 */                   data[k][j][i] = (data[k][j][i] * this.scl_slope + this.scl_inter);
/* 1435:     */                 }
/* 1436:     */               }
/* 1437:     */             }
/* 1438:1907 */             k++; continue;
/* 1439:1921 */             for (k = 0; k < ZZZ;)
/* 1440:     */             {
/* 1441:1922 */               for (j = 0; j < this.YDIM; j++) {
/* 1442:1923 */                 for (int i = 0; i < this.XDIM; i++)
/* 1443:     */                 {
/* 1444:1924 */                   data[k][j][i] = ecs.readLongCorrect();
/* 1445:1925 */                   if ((this.datatype == 1280) && (data[k][j][i] < 0.0D)) {
/* 1446:1926 */                     data[k][j][i] = (Math.abs(data[k][j][i]) + -2147483648.0D);
/* 1447:     */                   }
/* 1448:1927 */                   if (this.scl_slope != 0.0F) {
/* 1449:1928 */                     data[k][j][i] = (data[k][j][i] * this.scl_slope + this.scl_inter);
/* 1450:     */                   }
/* 1451:     */                 }
/* 1452:     */               }
/* 1453:1921 */               k++; continue;
/* 1454:1934 */               for (k = 0; k < ZZZ;)
/* 1455:     */               {
/* 1456:1935 */                 for (j = 0; j < this.YDIM; j++) {
/* 1457:1936 */                   for (int i = 0; i < this.XDIM; i++)
/* 1458:     */                   {
/* 1459:1937 */                     data[k][j][i] = ecs.readFloatCorrect();
/* 1460:1938 */                     if (this.scl_slope != 0.0F) {
/* 1461:1939 */                       data[k][j][i] = (data[k][j][i] * this.scl_slope + this.scl_inter);
/* 1462:     */                     }
/* 1463:     */                   }
/* 1464:     */                 }
/* 1465:1934 */                 k++; continue;
/* 1466:1945 */                 for (k = 0; k < ZZZ;)
/* 1467:     */                 {
/* 1468:1946 */                   for (j = 0; j < this.YDIM; j++) {
/* 1469:1947 */                     for (int i = 0; i < this.XDIM; i++)
/* 1470:     */                     {
/* 1471:1948 */                       data[k][j][i] = ecs.readDoubleCorrect();
/* 1472:1949 */                       if (this.scl_slope != 0.0F) {
/* 1473:1950 */                         data[k][j][i] = (data[k][j][i] * this.scl_slope + this.scl_inter);
/* 1474:     */                       }
/* 1475:     */                     }
/* 1476:     */                   }
/* 1477:1945 */                   k++; continue;
/* 1478:     */                   
/* 1479:     */ 
/* 1480:     */ 
/* 1481:     */ 
/* 1482:     */ 
/* 1483:     */ 
/* 1484:     */ 
/* 1485:     */ 
/* 1486:     */ 
/* 1487:     */ 
/* 1488:     */ 
/* 1489:     */ 
/* 1490:     */ 
/* 1491:     */ 
/* 1492:     */ 
/* 1493:     */ 
/* 1494:     */ 
/* 1495:     */ 
/* 1496:1964 */                   throw new IOException("Sorry, cannot yet read nifti-1 datatype " + decodeDatatype(this.datatype));
/* 1497:     */                 }
/* 1498:     */               }
/* 1499:     */             }
/* 1500:     */           }
/* 1501:     */         }
/* 1502:     */       }
/* 1503:     */     }
/* 1504:     */     int j;
/* 1505:1967 */     ecs.close();
/* 1506:1968 */     b = null;
/* 1507:     */     
/* 1508:     */ 
/* 1509:1971 */     return data;
/* 1510:     */   }
/* 1511:     */   
/* 1512:     */   public void writeVol(double[][][] data, short ttt)
/* 1513:     */     throws IOException
/* 1514:     */   {
/* 1515:1997 */     short ZZZ = this.ZDIM;
/* 1516:1998 */     if (this.dim[0] == 2) {
/* 1517:1999 */       ZZZ = 1;
/* 1518:     */     }
/* 1519:2001 */     int blob_size = this.XDIM * this.YDIM * ZZZ * bytesPerVoxel(this.datatype);
/* 1520:2002 */     ByteArrayOutputStream baos = new ByteArrayOutputStream(blob_size);
/* 1521:2003 */     EndianCorrectOutputStream ecs = new EndianCorrectOutputStream(baos, this.big_endian);
/* 1522:     */     short k;
/* 1523:2006 */     switch (this.datatype)
/* 1524:     */     {
/* 1525:     */     case 2: 
/* 1526:     */     case 4: 
/* 1527:     */     case 256: 
/* 1528:     */     case 512: 
/* 1529:2012 */       for (k = 0; k < ZZZ;)
/* 1530:     */       {
/* 1531:2013 */         for (j = 0; j < this.YDIM; j = (short)(j + 1)) {
/* 1532:2014 */           for (short i = 0; i < this.XDIM; i = (short)(i + 1)) {
/* 1533:2015 */             if (this.scl_slope == 0.0F) {
/* 1534:2016 */               ecs.writeShortCorrect((short)(int)data[k][j][i]);
/* 1535:     */             } else {
/* 1536:2018 */               ecs.writeShortCorrect((short)(int)((data[k][j][i] - this.scl_inter) / this.scl_slope));
/* 1537:     */             }
/* 1538:     */           }
/* 1539:     */         }
/* 1540:2012 */         k = (short)(k + 1); continue;
/* 1541:2025 */         for (k = 0; k < ZZZ;)
/* 1542:     */         {
/* 1543:2026 */           for (j = 0; j < this.YDIM; j = (short)(j + 1)) {
/* 1544:2027 */             for (short i = 0; i < this.XDIM; i = (short)(i + 1)) {
/* 1545:2028 */               if (this.scl_slope == 0.0F) {
/* 1546:2029 */                 ecs.writeIntCorrect((int)data[k][j][i]);
/* 1547:     */               } else {
/* 1548:2031 */                 ecs.writeIntCorrect((int)((data[k][j][i] - this.scl_inter) / this.scl_slope));
/* 1549:     */               }
/* 1550:     */             }
/* 1551:     */           }
/* 1552:2025 */           k = (short)(k + 1); continue;
/* 1553:2037 */           for (k = 0; k < ZZZ;)
/* 1554:     */           {
/* 1555:2038 */             for (j = 0; j < this.YDIM; j = (short)(j + 1)) {
/* 1556:2039 */               for (short i = 0; i < this.XDIM; i = (short)(i + 1)) {
/* 1557:2040 */                 if (this.scl_slope == 0.0F) {
/* 1558:2041 */                   ecs.writeLongCorrect(Math.rint(data[k][j][i]));
/* 1559:     */                 } else {
/* 1560:2043 */                   ecs.writeLongCorrect(Math.rint((data[k][j][i] - this.scl_inter) / this.scl_slope));
/* 1561:     */                 }
/* 1562:     */               }
/* 1563:     */             }
/* 1564:2037 */             k = (short)(k + 1); continue;
/* 1565:2047 */             for (k = 0; k < ZZZ;)
/* 1566:     */             {
/* 1567:2048 */               for (j = 0; j < this.YDIM; j = (short)(j + 1)) {
/* 1568:2049 */                 for (short i = 0; i < this.XDIM; i = (short)(i + 1)) {
/* 1569:2050 */                   if (this.scl_slope == 0.0F) {
/* 1570:2051 */                     ecs.writeFloatCorrect((float)data[k][j][i]);
/* 1571:     */                   } else {
/* 1572:2053 */                     ecs.writeFloatCorrect((float)((data[k][j][i] - this.scl_inter) / this.scl_slope));
/* 1573:     */                   }
/* 1574:     */                 }
/* 1575:     */               }
/* 1576:2047 */               k = (short)(k + 1); continue;
/* 1577:2057 */               for (k = 0; k < ZZZ;)
/* 1578:     */               {
/* 1579:2058 */                 for (j = 0; j < this.YDIM; j = (short)(j + 1)) {
/* 1580:2059 */                   for (short i = 0; i < this.XDIM; i = (short)(i + 1)) {
/* 1581:2060 */                     if (this.scl_slope == 0.0F) {
/* 1582:2061 */                       ecs.writeDoubleCorrect(data[k][j][i]);
/* 1583:     */                     } else {
/* 1584:2063 */                       ecs.writeDoubleCorrect((data[k][j][i] - this.scl_inter) / this.scl_slope);
/* 1585:     */                     }
/* 1586:     */                   }
/* 1587:     */                 }
/* 1588:2057 */                 k = (short)(k + 1); continue;
/* 1589:     */                 
/* 1590:     */ 
/* 1591:     */ 
/* 1592:     */ 
/* 1593:     */ 
/* 1594:     */ 
/* 1595:     */ 
/* 1596:     */ 
/* 1597:     */ 
/* 1598:     */ 
/* 1599:     */ 
/* 1600:     */ 
/* 1601:     */ 
/* 1602:     */ 
/* 1603:     */ 
/* 1604:     */ 
/* 1605:     */ 
/* 1606:     */ 
/* 1607:     */ 
/* 1608:     */ 
/* 1609:     */ 
/* 1610:2079 */                 throw new IOException("Sorry, cannot yet write nifti-1 datatype " + decodeDatatype(this.datatype));
/* 1611:     */               }
/* 1612:     */             }
/* 1613:     */           }
/* 1614:     */         }
/* 1615:     */       }
/* 1616:     */     }
/* 1617:     */     short j;
/* 1618:2084 */     writeVolBlob(baos, ttt);
/* 1619:2085 */     ecs.close();
/* 1620:     */   }
/* 1621:     */   
/* 1622:2091 */   private BufferedInputStream bis = null;
/* 1623:2092 */   private long currentPosition = 0L;
/* 1624:     */   
/* 1625:     */   private byte[] readVolBlob(short ttt)
/* 1626:     */     throws IOException
/* 1627:     */   {
/* 1628:2110 */     short ZZZ = this.ZDIM;
/* 1629:2111 */     if (this.dim[0] == 2) {
/* 1630:2112 */       ZZZ = 1;
/* 1631:     */     }
/* 1632:2114 */     int blob_size = this.XDIM * this.YDIM * ZZZ * bytesPerVoxel(this.datatype);
/* 1633:2115 */     byte[] b = new byte[blob_size];
/* 1634:     */     
/* 1635:2117 */     long skip_head = this.vox_offset;
/* 1636:2118 */     long skip_data = ttt * blob_size;
/* 1637:2126 */     if (this.ds_datname.endsWith(".gz"))
/* 1638:     */     {
/* 1639:2129 */       if ((this.bis == null) || (this.currentPosition != skip_head + skip_data))
/* 1640:     */       {
/* 1641:2130 */         this.bis = new BufferedInputStream(new GZIPInputStream(new FileInputStream(this.ds_datname)));
/* 1642:2131 */         this.currentPosition = 0L;
/* 1643:     */       }
/* 1644:2133 */       if (this.currentPosition != skip_head + skip_data)
/* 1645:     */       {
/* 1646:2134 */         this.bis.skip(skip_head + skip_data);
/* 1647:2135 */         this.currentPosition = (skip_head + skip_data);
/* 1648:     */       }
/* 1649:2139 */       this.bis.read(b, 0, blob_size);
/* 1650:     */       
/* 1651:     */ 
/* 1652:2142 */       this.bis.mark(blob_size);
/* 1653:2143 */       long skipped = 0L;long toSkip = blob_size;
/* 1654:2144 */       while ((toSkip > 0L) && ((skipped = this.bis.skip(toSkip)) != 0L)) {
/* 1655:2145 */         toSkip -= skipped;
/* 1656:     */       }
/* 1657:2149 */       if (toSkip > 0L)
/* 1658:     */       {
/* 1659:2150 */         this.bis.close();
/* 1660:2151 */         this.bis = null;
/* 1661:2152 */         this.currentPosition = 0L;
/* 1662:     */       }
/* 1663:     */       else
/* 1664:     */       {
/* 1665:2154 */         this.bis.reset();
/* 1666:2155 */         this.currentPosition += blob_size;
/* 1667:     */       }
/* 1668:     */     }
/* 1669:     */     else
/* 1670:     */     {
/* 1671:2160 */       RandomAccessFile raf = new RandomAccessFile(this.ds_datname, "r");
/* 1672:2161 */       raf.seek(skip_head + skip_data);
/* 1673:2162 */       raf.readFully(b, 0, blob_size);
/* 1674:2163 */       raf.close();
/* 1675:     */     }
/* 1676:2166 */     return b;
/* 1677:     */   }
/* 1678:     */   
/* 1679:     */   private void writeVolBlob(ByteArrayOutputStream baos, short ttt)
/* 1680:     */     throws IOException
/* 1681:     */   {
/* 1682:2184 */     short ZZZ = this.ZDIM;
/* 1683:2185 */     if (this.dim[0] == 2) {
/* 1684:2186 */       ZZZ = 1;
/* 1685:     */     }
/* 1686:2188 */     long skip_head = this.vox_offset;
/* 1687:2189 */     long skip_data = ttt * this.XDIM * this.YDIM * ZZZ * bytesPerVoxel(this.datatype);
/* 1688:2196 */     if (this.ds_datname.endsWith(".gz")) {
/* 1689:2197 */       throw new IOException("Sorry, can't write to compressed image data file: " + this.ds_datname);
/* 1690:     */     }
/* 1691:2201 */     RandomAccessFile raf = new RandomAccessFile(this.ds_datname, "rwd");
/* 1692:2202 */     raf.seek(skip_head + skip_data);
/* 1693:2203 */     raf.write(baos.toByteArray());
/* 1694:2204 */     raf.close();
/* 1695:     */   }
/* 1696:     */   
/* 1697:     */   public byte[] readData()
/* 1698:     */     throws IOException
/* 1699:     */   {
/* 1700:2231 */     long blob_size = 1L;
/* 1701:2232 */     for (int i = 1; i <= this.dim[0]; i++) {
/* 1702:2233 */       blob_size *= this.dim[i];
/* 1703:     */     }
/* 1704:2234 */     blob_size *= bytesPerVoxel(this.datatype);
/* 1705:2236 */     if (blob_size > 2147483647L) {
/* 1706:2237 */       throw new IOException("\nSorry, cannot yet handle data arrays bigger than 2147483647 bytes.  " + this.ds_datname + " has " + blob_size + " bytes.");
/* 1707:     */     }
/* 1708:2239 */     byte[] b = new byte[(int)blob_size];
/* 1709:     */     
/* 1710:2241 */     long skip_head = this.vox_offset;
/* 1711:     */     DataInputStream dis;
/* 1712:     */     DataInputStream dis;
/* 1713:2247 */     if (this.ds_datname.endsWith(".gz")) {
/* 1714:2248 */       dis = new DataInputStream(new GZIPInputStream(new FileInputStream(this.ds_datname)));
/* 1715:     */     } else {
/* 1716:2250 */       dis = new DataInputStream(new FileInputStream(this.ds_datname));
/* 1717:     */     }
/* 1718:2252 */     dis.skipBytes((int)skip_head);
/* 1719:2253 */     dis.readFully(b, 0, (int)blob_size);
/* 1720:2254 */     dis.close();
/* 1721:     */     
/* 1722:2256 */     return b;
/* 1723:     */   }
/* 1724:     */   
/* 1725:     */   public void writeData(byte[] b)
/* 1726:     */     throws IOException
/* 1727:     */   {
/* 1728:2278 */     int skip_head = (int)this.vox_offset;
/* 1729:2282 */     if (this.ds_datname.endsWith(".gz")) {
/* 1730:2283 */       throw new IOException("Sorry, can't write to compressed image data file: " + this.ds_datname);
/* 1731:     */     }
/* 1732:2287 */     RandomAccessFile raf = new RandomAccessFile(this.ds_datname, "rwd");
/* 1733:2288 */     raf.seek(skip_head);
/* 1734:2289 */     raf.write(b, 0, b.length);
/* 1735:2290 */     raf.close();
/* 1736:     */   }
/* 1737:     */   
/* 1738:     */   public double[] readDoubleTmcrs(short x, short y, short z)
/* 1739:     */     throws IOException
/* 1740:     */   {
/* 1741:2314 */     short ZZZ = this.ZDIM;
/* 1742:2315 */     if (this.dim[0] == 2) {
/* 1743:2316 */       ZZZ = 1;
/* 1744:     */     }
/* 1745:2319 */     double[] data = new double[this.TDIM];
/* 1746:     */     
/* 1747:2321 */     long skip_head = this.vox_offset;
/* 1748:2322 */     long skip_data = (z * this.XDIM * this.YDIM + y * this.XDIM + x) * bytesPerVoxel(this.datatype);
/* 1749:2323 */     long skip_vol = this.XDIM * this.YDIM * ZZZ * bytesPerVoxel(this.datatype) - bytesPerVoxel(this.datatype);
/* 1750:2324 */     long skip_vol2 = 0L;
/* 1751:     */     EndianCorrectInputStream ecs;
/* 1752:     */     EndianCorrectInputStream ecs;
/* 1753:2331 */     if (this.ds_datname.endsWith(".gz")) {
/* 1754:2332 */       ecs = new EndianCorrectInputStream(new GZIPInputStream(new FileInputStream(this.ds_datname)), this.big_endian);
/* 1755:     */     } else {
/* 1756:2334 */       ecs = new EndianCorrectInputStream(new FileInputStream(this.ds_datname), this.big_endian);
/* 1757:     */     }
/* 1758:2338 */     ecs.skip((int)(skip_head + skip_data));
/* 1759:2342 */     for (short i = 0; i < this.TDIM; i = (short)(i + 1))
/* 1760:     */     {
/* 1761:2345 */       ecs.skip(skip_vol2);
/* 1762:2346 */       skip_vol2 = skip_vol;
/* 1763:2349 */       switch (this.datatype)
/* 1764:     */       {
/* 1765:     */       case 2: 
/* 1766:     */       case 256: 
/* 1767:2353 */         data[i] = ecs.readByte();
/* 1768:2354 */         if ((this.datatype == 2) && (data[i] < 0.0D)) {
/* 1769:2355 */           data[i] = (Math.abs(data[i]) + 128.0D);
/* 1770:     */         }
/* 1771:2356 */         if (this.scl_slope != 0.0F) {
/* 1772:2357 */           data[i] = (data[i] * this.scl_slope + this.scl_inter);
/* 1773:     */         }
/* 1774:     */         break;
/* 1775:     */       case 4: 
/* 1776:     */       case 512: 
/* 1777:2362 */         data[i] = ecs.readShortCorrect();
/* 1778:2363 */         if ((this.datatype == 512) && (data[i] < 0.0D)) {
/* 1779:2364 */           data[i] = (Math.abs(data[i]) + 32768.0D);
/* 1780:     */         }
/* 1781:2365 */         if (this.scl_slope != 0.0F) {
/* 1782:2366 */           data[i] = (data[i] * this.scl_slope + this.scl_inter);
/* 1783:     */         }
/* 1784:     */         break;
/* 1785:     */       case 8: 
/* 1786:     */       case 768: 
/* 1787:2371 */         data[i] = ecs.readIntCorrect();
/* 1788:2372 */         if ((this.datatype == 768) && (data[i] < 0.0D)) {
/* 1789:2373 */           data[i] = (Math.abs(data[i]) + -2147483648.0D);
/* 1790:     */         }
/* 1791:2374 */         if (this.scl_slope != 0.0F) {
/* 1792:2375 */           data[i] = (data[i] * this.scl_slope + this.scl_inter);
/* 1793:     */         }
/* 1794:     */         break;
/* 1795:     */       case 1024: 
/* 1796:     */       case 1280: 
/* 1797:2381 */         data[i] = ecs.readLongCorrect();
/* 1798:2382 */         if ((this.datatype == 1280) && (data[i] < 0.0D)) {
/* 1799:2383 */           data[i] = (Math.abs(data[i]) + -2147483648.0D);
/* 1800:     */         }
/* 1801:2384 */         if (this.scl_slope != 0.0F) {
/* 1802:2385 */           data[i] = (data[i] * this.scl_slope + this.scl_inter);
/* 1803:     */         }
/* 1804:     */         break;
/* 1805:     */       case 16: 
/* 1806:2390 */         data[i] = ecs.readFloatCorrect();
/* 1807:2391 */         if (this.scl_slope != 0.0F) {
/* 1808:2392 */           data[i] = (data[i] * this.scl_slope + this.scl_inter);
/* 1809:     */         }
/* 1810:     */         break;
/* 1811:     */       case 64: 
/* 1812:2397 */         data[i] = ecs.readDoubleCorrect();
/* 1813:2398 */         if (this.scl_slope != 0.0F) {
/* 1814:2399 */           data[i] = (data[i] * this.scl_slope + this.scl_inter);
/* 1815:     */         }
/* 1816:     */         break;
/* 1817:     */       case 0: 
/* 1818:     */       case 1: 
/* 1819:     */       case 32: 
/* 1820:     */       case 128: 
/* 1821:     */       case 255: 
/* 1822:     */       case 1536: 
/* 1823:     */       case 1792: 
/* 1824:     */       case 2048: 
/* 1825:     */       default: 
/* 1826:2412 */         throw new IOException("Sorry, cannot yet read nifti-1 datatype " + decodeDatatype(this.datatype));
/* 1827:     */       }
/* 1828:     */     }
/* 1829:2419 */     ecs.close();
/* 1830:     */     
/* 1831:     */ 
/* 1832:2422 */     return data;
/* 1833:     */   }
/* 1834:     */   
/* 1835:     */   private byte[] setStringSize(StringBuffer s, int n)
/* 1836:     */   {
/* 1837:2439 */     int slen = s.length();
/* 1838:2441 */     if (slen >= n) {
/* 1839:2442 */       return s.toString().substring(0, n).getBytes();
/* 1840:     */     }
/* 1841:2444 */     byte[] b = new byte[n];
/* 1842:2445 */     for (int i = 0; i < slen; i++) {
/* 1843:2446 */       b[i] = ((byte)s.charAt(i));
/* 1844:     */     }
/* 1845:2447 */     for (i = slen; i < n; i++) {
/* 1846:2448 */       b[i] = 0;
/* 1847:     */     }
/* 1848:2450 */     return b;
/* 1849:     */   }
/* 1850:     */   
/* 1851:     */   public static void main(String[] args)
/* 1852:     */   {
/* 1853:2462 */     Nifti1Dataset nds = new Nifti1Dataset(args[0]);
/* 1854:     */     try
/* 1855:     */     {
/* 1856:2464 */       nds.readHeader();
/* 1857:2465 */       nds.printHeader();
/* 1858:     */     }
/* 1859:     */     catch (IOException ex)
/* 1860:     */     {
/* 1861:2468 */       System.out.println("\nCould not read header file for " + args[0] + ": " + ex.getMessage());
/* 1862:     */     }
/* 1863:     */   }
/* 1864:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.converters.nifti.Nifti1Dataset
 * JD-Core Version:    0.7.0.1
 */