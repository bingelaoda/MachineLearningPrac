/*   1:    */ package weka.gui.sql;
/*   2:    */ 
/*   3:    */ import java.sql.Date;
/*   4:    */ import java.sql.ResultSet;
/*   5:    */ import java.sql.ResultSetMetaData;
/*   6:    */ import java.sql.Time;
/*   7:    */ import java.sql.Timestamp;
/*   8:    */ import java.util.Vector;
/*   9:    */ 
/*  10:    */ public class ResultSetHelper
/*  11:    */ {
/*  12:    */   protected ResultSet m_ResultSet;
/*  13: 42 */   protected boolean m_Initialized = false;
/*  14: 45 */   protected int m_MaxRows = 0;
/*  15: 48 */   protected int m_ColumnCount = 0;
/*  16: 51 */   protected int m_RowCount = 0;
/*  17: 54 */   protected String[] m_ColumnNames = null;
/*  18: 57 */   protected boolean[] m_NumericColumns = null;
/*  19: 60 */   protected Class<?>[] m_ColumnClasses = null;
/*  20:    */   
/*  21:    */   public ResultSetHelper(ResultSet rs)
/*  22:    */   {
/*  23: 68 */     this(rs, 0);
/*  24:    */   }
/*  25:    */   
/*  26:    */   public ResultSetHelper(ResultSet rs, int max)
/*  27:    */   {
/*  28: 81 */     this.m_ResultSet = rs;
/*  29: 82 */     this.m_MaxRows = max;
/*  30:    */   }
/*  31:    */   
/*  32:    */   protected void initialize()
/*  33:    */   {
/*  34: 92 */     if (this.m_Initialized) {
/*  35: 93 */       return;
/*  36:    */     }
/*  37:    */     try
/*  38:    */     {
/*  39: 97 */       ResultSetMetaData meta = this.m_ResultSet.getMetaData();
/*  40:    */       
/*  41:    */ 
/*  42:100 */       this.m_ColumnNames = new String[meta.getColumnCount()];
/*  43:101 */       for (int i = 1; i <= meta.getColumnCount(); i++) {
/*  44:102 */         this.m_ColumnNames[(i - 1)] = meta.getColumnLabel(i);
/*  45:    */       }
/*  46:106 */       this.m_NumericColumns = new boolean[meta.getColumnCount()];
/*  47:107 */       for (i = 1; i <= meta.getColumnCount(); i++) {
/*  48:108 */         this.m_NumericColumns[(i - 1)] = typeIsNumeric(meta.getColumnType(i));
/*  49:    */       }
/*  50:112 */       this.m_ColumnClasses = new Class[meta.getColumnCount()];
/*  51:113 */       for (i = 1; i <= meta.getColumnCount(); i++) {
/*  52:    */         try
/*  53:    */         {
/*  54:115 */           this.m_ColumnClasses[(i - 1)] = typeToClass(meta.getColumnType(i));
/*  55:    */         }
/*  56:    */         catch (Exception ex)
/*  57:    */         {
/*  58:117 */           this.m_ColumnClasses[(i - 1)] = String.class;
/*  59:    */         }
/*  60:    */       }
/*  61:122 */       this.m_ColumnCount = meta.getColumnCount();
/*  62:126 */       if (this.m_ResultSet.getType() == 1003)
/*  63:    */       {
/*  64:127 */         this.m_RowCount = -1;
/*  65:    */       }
/*  66:    */       else
/*  67:    */       {
/*  68:129 */         this.m_RowCount = 0;
/*  69:130 */         this.m_ResultSet.first();
/*  70:131 */         if (this.m_MaxRows > 0)
/*  71:    */         {
/*  72:    */           try
/*  73:    */           {
/*  74:133 */             this.m_ResultSet.absolute(this.m_MaxRows);
/*  75:134 */             this.m_RowCount = this.m_ResultSet.getRow();
/*  76:    */           }
/*  77:    */           catch (Exception ex) {}
/*  78:    */         }
/*  79:    */         else
/*  80:    */         {
/*  81:139 */           this.m_ResultSet.last();
/*  82:140 */           this.m_RowCount = this.m_ResultSet.getRow();
/*  83:    */         }
/*  84:    */         try
/*  85:    */         {
/*  86:146 */           if ((this.m_RowCount == 0) && (this.m_ResultSet.first()))
/*  87:    */           {
/*  88:147 */             this.m_RowCount = 1;
/*  89:148 */             while (this.m_ResultSet.next())
/*  90:    */             {
/*  91:149 */               this.m_RowCount += 1;
/*  92:150 */               if (this.m_ResultSet.getRow() == this.m_MaxRows) {
/*  93:    */                 break;
/*  94:    */               }
/*  95:    */             }
/*  96:    */           }
/*  97:    */         }
/*  98:    */         catch (Exception e) {}
/*  99:    */       }
/* 100:161 */       this.m_Initialized = true;
/* 101:    */     }
/* 102:    */     catch (Exception ex) {}
/* 103:    */   }
/* 104:    */   
/* 105:    */   public ResultSet getResultSet()
/* 106:    */   {
/* 107:173 */     return this.m_ResultSet;
/* 108:    */   }
/* 109:    */   
/* 110:    */   public int getColumnCount()
/* 111:    */   {
/* 112:182 */     initialize();
/* 113:    */     
/* 114:184 */     return this.m_ColumnCount;
/* 115:    */   }
/* 116:    */   
/* 117:    */   public int getRowCount()
/* 118:    */   {
/* 119:194 */     initialize();
/* 120:    */     
/* 121:196 */     return this.m_RowCount;
/* 122:    */   }
/* 123:    */   
/* 124:    */   public String[] getColumnNames()
/* 125:    */   {
/* 126:205 */     initialize();
/* 127:    */     
/* 128:207 */     return this.m_ColumnNames;
/* 129:    */   }
/* 130:    */   
/* 131:    */   public boolean[] getNumericColumns()
/* 132:    */   {
/* 133:216 */     initialize();
/* 134:    */     
/* 135:218 */     return this.m_NumericColumns;
/* 136:    */   }
/* 137:    */   
/* 138:    */   public Class<?>[] getColumnClasses()
/* 139:    */   {
/* 140:227 */     initialize();
/* 141:    */     
/* 142:229 */     return this.m_ColumnClasses;
/* 143:    */   }
/* 144:    */   
/* 145:    */   public boolean hasMaxRows()
/* 146:    */   {
/* 147:238 */     return this.m_MaxRows > 0;
/* 148:    */   }
/* 149:    */   
/* 150:    */   public int getMaxRows()
/* 151:    */   {
/* 152:247 */     return this.m_MaxRows;
/* 153:    */   }
/* 154:    */   
/* 155:    */   public Object[][] getCells()
/* 156:    */   {
/* 157:265 */     initialize();
/* 158:    */     
/* 159:267 */     Vector<Object[]> result = new Vector();
/* 160:    */     try
/* 161:    */     {
/* 162:272 */       int rowCount = getRowCount();
/* 163:    */       boolean proceed;
/* 164:    */       boolean proceed;
/* 165:273 */       if (rowCount == -1)
/* 166:    */       {
/* 167:274 */         rowCount = getMaxRows();
/* 168:275 */         proceed = this.m_ResultSet.next();
/* 169:    */       }
/* 170:    */       else
/* 171:    */       {
/* 172:277 */         proceed = this.m_ResultSet.first();
/* 173:    */       }
/* 174:280 */       if (proceed)
/* 175:    */       {
/* 176:281 */         int i = 0;
/* 177:    */         for (;;)
/* 178:    */         {
/* 179:283 */           Object[] row = new Object[getColumnCount()];
/* 180:284 */           result.add(row);
/* 181:286 */           for (int n = 0; n < getColumnCount(); n++) {
/* 182:    */             try
/* 183:    */             {
/* 184:289 */               if (getColumnClasses()[n] == String.class) {
/* 185:290 */                 row[n] = this.m_ResultSet.getString(n + 1);
/* 186:    */               } else {
/* 187:292 */                 row[n] = this.m_ResultSet.getObject(n + 1);
/* 188:    */               }
/* 189:    */             }
/* 190:    */             catch (Exception e)
/* 191:    */             {
/* 192:295 */               row[n] = null;
/* 193:    */             }
/* 194:    */           }
/* 195:300 */           if (i == rowCount - 1) {
/* 196:    */             break;
/* 197:    */           }
/* 198:304 */           if (!this.m_ResultSet.next()) {
/* 199:    */             break;
/* 200:    */           }
/* 201:309 */           i++;
/* 202:    */         }
/* 203:    */       }
/* 204:    */     }
/* 205:    */     catch (Exception e)
/* 206:    */     {
/* 207:313 */       e.printStackTrace();
/* 208:    */     }
/* 209:316 */     return (Object[][])result.toArray(new Object[result.size()][getColumnCount()]);
/* 210:    */   }
/* 211:    */   
/* 212:    */   public static Class<?> typeToClass(int type)
/* 213:    */   {
/* 214:    */     Class<?> result;
/* 215:328 */     switch (type)
/* 216:    */     {
/* 217:    */     case -5: 
/* 218:330 */       result = Long.class;
/* 219:331 */       break;
/* 220:    */     case -2: 
/* 221:333 */       result = String.class;
/* 222:334 */       break;
/* 223:    */     case -7: 
/* 224:336 */       result = Boolean.class;
/* 225:337 */       break;
/* 226:    */     case 1: 
/* 227:339 */       result = Character.class;
/* 228:340 */       break;
/* 229:    */     case 91: 
/* 230:342 */       result = Date.class;
/* 231:343 */       break;
/* 232:    */     case 3: 
/* 233:345 */       result = Double.class;
/* 234:346 */       break;
/* 235:    */     case 8: 
/* 236:348 */       result = Double.class;
/* 237:349 */       break;
/* 238:    */     case 6: 
/* 239:351 */       result = Float.class;
/* 240:352 */       break;
/* 241:    */     case 4: 
/* 242:354 */       result = Integer.class;
/* 243:355 */       break;
/* 244:    */     case -4: 
/* 245:357 */       result = String.class;
/* 246:358 */       break;
/* 247:    */     case -1: 
/* 248:360 */       result = String.class;
/* 249:361 */       break;
/* 250:    */     case 0: 
/* 251:363 */       result = String.class;
/* 252:364 */       break;
/* 253:    */     case 2: 
/* 254:366 */       result = Double.class;
/* 255:367 */       break;
/* 256:    */     case 1111: 
/* 257:369 */       result = String.class;
/* 258:370 */       break;
/* 259:    */     case 7: 
/* 260:372 */       result = Double.class;
/* 261:373 */       break;
/* 262:    */     case 5: 
/* 263:375 */       result = Short.class;
/* 264:376 */       break;
/* 265:    */     case 92: 
/* 266:378 */       result = Time.class;
/* 267:379 */       break;
/* 268:    */     case 93: 
/* 269:381 */       result = Timestamp.class;
/* 270:382 */       break;
/* 271:    */     case -6: 
/* 272:384 */       result = Short.class;
/* 273:385 */       break;
/* 274:    */     case -3: 
/* 275:387 */       result = String.class;
/* 276:388 */       break;
/* 277:    */     case 12: 
/* 278:390 */       result = String.class;
/* 279:391 */       break;
/* 280:    */     default: 
/* 281:393 */       result = null;
/* 282:    */     }
/* 283:396 */     return result;
/* 284:    */   }
/* 285:    */   
/* 286:    */   public static boolean typeIsNumeric(int type)
/* 287:    */   {
/* 288:    */     boolean result;
/* 289:409 */     switch (type)
/* 290:    */     {
/* 291:    */     case -5: 
/* 292:411 */       result = true;
/* 293:412 */       break;
/* 294:    */     case -2: 
/* 295:414 */       result = false;
/* 296:415 */       break;
/* 297:    */     case -7: 
/* 298:417 */       result = false;
/* 299:418 */       break;
/* 300:    */     case 1: 
/* 301:420 */       result = false;
/* 302:421 */       break;
/* 303:    */     case 91: 
/* 304:423 */       result = false;
/* 305:424 */       break;
/* 306:    */     case 3: 
/* 307:426 */       result = true;
/* 308:427 */       break;
/* 309:    */     case 8: 
/* 310:429 */       result = true;
/* 311:430 */       break;
/* 312:    */     case 6: 
/* 313:432 */       result = true;
/* 314:433 */       break;
/* 315:    */     case 4: 
/* 316:435 */       result = true;
/* 317:436 */       break;
/* 318:    */     case -4: 
/* 319:438 */       result = false;
/* 320:439 */       break;
/* 321:    */     case -1: 
/* 322:441 */       result = false;
/* 323:442 */       break;
/* 324:    */     case 0: 
/* 325:444 */       result = false;
/* 326:445 */       break;
/* 327:    */     case 2: 
/* 328:447 */       result = true;
/* 329:448 */       break;
/* 330:    */     case 1111: 
/* 331:450 */       result = false;
/* 332:451 */       break;
/* 333:    */     case 7: 
/* 334:453 */       result = true;
/* 335:454 */       break;
/* 336:    */     case 5: 
/* 337:456 */       result = true;
/* 338:457 */       break;
/* 339:    */     case 92: 
/* 340:459 */       result = false;
/* 341:460 */       break;
/* 342:    */     case 93: 
/* 343:462 */       result = true;
/* 344:463 */       break;
/* 345:    */     case -6: 
/* 346:465 */       result = true;
/* 347:466 */       break;
/* 348:    */     case -3: 
/* 349:468 */       result = false;
/* 350:469 */       break;
/* 351:    */     case 12: 
/* 352:471 */       result = false;
/* 353:472 */       break;
/* 354:    */     default: 
/* 355:474 */       result = false;
/* 356:    */     }
/* 357:477 */     return result;
/* 358:    */   }
/* 359:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.sql.ResultSetHelper
 * JD-Core Version:    0.7.0.1
 */