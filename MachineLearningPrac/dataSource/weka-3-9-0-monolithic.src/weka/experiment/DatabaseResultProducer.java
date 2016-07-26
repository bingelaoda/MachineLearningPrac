/*   1:    */ package weka.experiment;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.core.AdditionalMeasureProducer;
/*   8:    */ import weka.core.Instances;
/*   9:    */ import weka.core.Option;
/*  10:    */ import weka.core.OptionHandler;
/*  11:    */ import weka.core.RevisionUtils;
/*  12:    */ import weka.core.Utils;
/*  13:    */ 
/*  14:    */ public class DatabaseResultProducer
/*  15:    */   extends DatabaseResultListener
/*  16:    */   implements ResultProducer, OptionHandler, AdditionalMeasureProducer
/*  17:    */ {
/*  18:    */   static final long serialVersionUID = -5620660780203158666L;
/*  19:    */   protected Instances m_Instances;
/*  20:143 */   protected ResultListener m_ResultListener = new CSVResultListener();
/*  21:146 */   protected String[] m_AdditionalMeasures = null;
/*  22:    */   
/*  23:    */   public String globalInfo()
/*  24:    */   {
/*  25:156 */     return "Examines a database and extracts out the results produced by the specified ResultProducer and submits them to the specified ResultListener. If a result needs to be generated, the ResultProducer is used to obtain the result.";
/*  26:    */   }
/*  27:    */   
/*  28:    */   public DatabaseResultProducer()
/*  29:    */     throws Exception
/*  30:    */   {
/*  31:171 */     this.m_ResultProducer = new CrossValidationResultProducer();
/*  32:    */   }
/*  33:    */   
/*  34:    */   public void doRunKeys(int run)
/*  35:    */     throws Exception
/*  36:    */   {
/*  37:185 */     if (this.m_ResultProducer == null) {
/*  38:186 */       throw new Exception("No ResultProducer set");
/*  39:    */     }
/*  40:188 */     if (this.m_ResultListener == null) {
/*  41:189 */       throw new Exception("No ResultListener set");
/*  42:    */     }
/*  43:191 */     if (this.m_Instances == null) {
/*  44:192 */       throw new Exception("No Instances set");
/*  45:    */     }
/*  46:196 */     this.m_ResultProducer.setResultListener(this);
/*  47:197 */     this.m_ResultProducer.setInstances(this.m_Instances);
/*  48:198 */     this.m_ResultProducer.doRunKeys(run);
/*  49:    */   }
/*  50:    */   
/*  51:    */   public void doRun(int run)
/*  52:    */     throws Exception
/*  53:    */   {
/*  54:212 */     if (this.m_ResultProducer == null) {
/*  55:213 */       throw new Exception("No ResultProducer set");
/*  56:    */     }
/*  57:215 */     if (this.m_ResultListener == null) {
/*  58:216 */       throw new Exception("No ResultListener set");
/*  59:    */     }
/*  60:218 */     if (this.m_Instances == null) {
/*  61:219 */       throw new Exception("No Instances set");
/*  62:    */     }
/*  63:223 */     this.m_ResultProducer.setResultListener(this);
/*  64:224 */     this.m_ResultProducer.setInstances(this.m_Instances);
/*  65:225 */     this.m_ResultProducer.doRun(run);
/*  66:    */   }
/*  67:    */   
/*  68:    */   public void preProcess(ResultProducer rp)
/*  69:    */     throws Exception
/*  70:    */   {
/*  71:238 */     super.preProcess(rp);
/*  72:239 */     if (this.m_ResultListener == null) {
/*  73:240 */       throw new Exception("No ResultListener set");
/*  74:    */     }
/*  75:242 */     this.m_ResultListener.preProcess(this);
/*  76:    */   }
/*  77:    */   
/*  78:    */   public void postProcess(ResultProducer rp)
/*  79:    */     throws Exception
/*  80:    */   {
/*  81:255 */     super.postProcess(rp);
/*  82:256 */     this.m_ResultListener.postProcess(this);
/*  83:    */   }
/*  84:    */   
/*  85:    */   public void preProcess()
/*  86:    */     throws Exception
/*  87:    */   {
/*  88:268 */     if (this.m_ResultProducer == null) {
/*  89:269 */       throw new Exception("No ResultProducer set");
/*  90:    */     }
/*  91:271 */     this.m_ResultProducer.setResultListener(this);
/*  92:272 */     this.m_ResultProducer.preProcess();
/*  93:    */   }
/*  94:    */   
/*  95:    */   public void postProcess()
/*  96:    */     throws Exception
/*  97:    */   {
/*  98:285 */     this.m_ResultProducer.postProcess();
/*  99:    */   }
/* 100:    */   
/* 101:    */   public void acceptResult(ResultProducer rp, Object[] key, Object[] result)
/* 102:    */     throws Exception
/* 103:    */   {
/* 104:302 */     if (this.m_ResultProducer != rp) {
/* 105:303 */       throw new Error("Unrecognized ResultProducer sending results!!");
/* 106:    */     }
/* 107:308 */     boolean isRequiredByListener = this.m_ResultListener.isResultRequired(this, key);
/* 108:    */     
/* 109:310 */     boolean isRequiredByDatabase = super.isResultRequired(rp, key);
/* 110:313 */     if (isRequiredByDatabase) {
/* 111:316 */       if (result != null) {
/* 112:319 */         super.acceptResult(rp, key, result);
/* 113:    */       }
/* 114:    */     }
/* 115:324 */     if (isRequiredByListener) {
/* 116:325 */       this.m_ResultListener.acceptResult(this, key, result);
/* 117:    */     }
/* 118:    */   }
/* 119:    */   
/* 120:    */   public boolean isResultRequired(ResultProducer rp, Object[] key)
/* 121:    */     throws Exception
/* 122:    */   {
/* 123:342 */     if (this.m_ResultProducer != rp) {
/* 124:343 */       throw new Error("Unrecognized ResultProducer sending results!!");
/* 125:    */     }
/* 126:348 */     boolean isRequiredByListener = this.m_ResultListener.isResultRequired(this, key);
/* 127:    */     
/* 128:350 */     boolean isRequiredByDatabase = super.isResultRequired(rp, key);
/* 129:352 */     if ((!isRequiredByDatabase) && (isRequiredByListener))
/* 130:    */     {
/* 131:354 */       Object[] result = getResultFromTable(this.m_ResultsTableName, rp, key);
/* 132:355 */       System.err.println("Got result from database: " + DatabaseUtils.arrayToString(result));
/* 133:    */       
/* 134:357 */       this.m_ResultListener.acceptResult(this, key, result);
/* 135:358 */       return false;
/* 136:    */     }
/* 137:361 */     return (isRequiredByListener) || (isRequiredByDatabase);
/* 138:    */   }
/* 139:    */   
/* 140:    */   public String[] getKeyNames()
/* 141:    */     throws Exception
/* 142:    */   {
/* 143:373 */     return this.m_ResultProducer.getKeyNames();
/* 144:    */   }
/* 145:    */   
/* 146:    */   public Object[] getKeyTypes()
/* 147:    */     throws Exception
/* 148:    */   {
/* 149:387 */     return this.m_ResultProducer.getKeyTypes();
/* 150:    */   }
/* 151:    */   
/* 152:    */   public String[] getResultNames()
/* 153:    */     throws Exception
/* 154:    */   {
/* 155:403 */     return this.m_ResultProducer.getResultNames();
/* 156:    */   }
/* 157:    */   
/* 158:    */   public Object[] getResultTypes()
/* 159:    */     throws Exception
/* 160:    */   {
/* 161:416 */     return this.m_ResultProducer.getResultTypes();
/* 162:    */   }
/* 163:    */   
/* 164:    */   public String getCompatibilityState()
/* 165:    */   {
/* 166:435 */     String result = "";
/* 167:436 */     if (this.m_ResultProducer == null)
/* 168:    */     {
/* 169:437 */       result = result + "<null ResultProducer>";
/* 170:    */     }
/* 171:    */     else
/* 172:    */     {
/* 173:439 */       result = result + "-W " + this.m_ResultProducer.getClass().getName();
/* 174:440 */       result = result + " -- " + this.m_ResultProducer.getCompatibilityState();
/* 175:    */     }
/* 176:443 */     return result.trim();
/* 177:    */   }
/* 178:    */   
/* 179:    */   public Enumeration<Option> listOptions()
/* 180:    */   {
/* 181:454 */     Vector<Option> newVector = new Vector(2);
/* 182:    */     
/* 183:456 */     newVector.addElement(new Option("\tThe name of the database field to cache over.\n\teg: \"Fold\" (default none)", "F", 1, "-F <field name>"));
/* 184:    */     
/* 185:    */ 
/* 186:459 */     newVector.addElement(new Option("\tThe full class name of a ResultProducer.\n\teg: weka.experiment.CrossValidationResultProducer", "W", 1, "-W <class name>"));
/* 187:464 */     if ((this.m_ResultProducer != null) && ((this.m_ResultProducer instanceof OptionHandler)))
/* 188:    */     {
/* 189:466 */       newVector.addElement(new Option("", "", 0, "\nOptions specific to result producer " + this.m_ResultProducer.getClass().getName() + ":"));
/* 190:    */       
/* 191:    */ 
/* 192:469 */       newVector.addAll(Collections.list(((OptionHandler)this.m_ResultProducer).listOptions()));
/* 193:    */     }
/* 194:472 */     return newVector.elements();
/* 195:    */   }
/* 196:    */   
/* 197:    */   public void setOptions(String[] options)
/* 198:    */     throws Exception
/* 199:    */   {
/* 200:574 */     setCacheKeyName(Utils.getOption('F', options));
/* 201:    */     
/* 202:576 */     String rpName = Utils.getOption('W', options);
/* 203:577 */     if (rpName.length() == 0) {
/* 204:578 */       throw new Exception("A ResultProducer must be specified with the -W option.");
/* 205:    */     }
/* 206:584 */     setResultProducer((ResultProducer)Utils.forName(ResultProducer.class, rpName, null));
/* 207:586 */     if ((getResultProducer() instanceof OptionHandler)) {
/* 208:587 */       ((OptionHandler)getResultProducer()).setOptions(Utils.partitionOptions(options));
/* 209:    */     }
/* 210:    */   }
/* 211:    */   
/* 212:    */   public String[] getOptions()
/* 213:    */   {
/* 214:600 */     String[] seOptions = new String[0];
/* 215:601 */     if ((this.m_ResultProducer != null) && ((this.m_ResultProducer instanceof OptionHandler))) {
/* 216:603 */       seOptions = ((OptionHandler)this.m_ResultProducer).getOptions();
/* 217:    */     }
/* 218:606 */     String[] options = new String[seOptions.length + 8];
/* 219:607 */     int current = 0;
/* 220:609 */     if (!getCacheKeyName().equals(""))
/* 221:    */     {
/* 222:610 */       options[(current++)] = "-F";
/* 223:611 */       options[(current++)] = getCacheKeyName();
/* 224:    */     }
/* 225:613 */     if (getResultProducer() != null)
/* 226:    */     {
/* 227:614 */       options[(current++)] = "-W";
/* 228:615 */       options[(current++)] = getResultProducer().getClass().getName();
/* 229:    */     }
/* 230:617 */     options[(current++)] = "--";
/* 231:    */     
/* 232:619 */     System.arraycopy(seOptions, 0, options, current, seOptions.length);
/* 233:620 */     current += seOptions.length;
/* 234:621 */     while (current < options.length) {
/* 235:622 */       options[(current++)] = "";
/* 236:    */     }
/* 237:624 */     return options;
/* 238:    */   }
/* 239:    */   
/* 240:    */   public void setAdditionalMeasures(String[] additionalMeasures)
/* 241:    */   {
/* 242:637 */     this.m_AdditionalMeasures = additionalMeasures;
/* 243:639 */     if (this.m_ResultProducer != null)
/* 244:    */     {
/* 245:640 */       System.err.println("DatabaseResultProducer: setting additional measures for ResultProducer");
/* 246:    */       
/* 247:642 */       this.m_ResultProducer.setAdditionalMeasures(this.m_AdditionalMeasures);
/* 248:    */     }
/* 249:    */   }
/* 250:    */   
/* 251:    */   public Enumeration<String> enumerateMeasures()
/* 252:    */   {
/* 253:654 */     Vector<String> newVector = new Vector();
/* 254:655 */     if ((this.m_ResultProducer instanceof AdditionalMeasureProducer))
/* 255:    */     {
/* 256:656 */       Enumeration<String> en = ((AdditionalMeasureProducer)this.m_ResultProducer).enumerateMeasures();
/* 257:658 */       while (en.hasMoreElements())
/* 258:    */       {
/* 259:659 */         String mname = (String)en.nextElement();
/* 260:660 */         newVector.addElement(mname);
/* 261:    */       }
/* 262:    */     }
/* 263:663 */     return newVector.elements();
/* 264:    */   }
/* 265:    */   
/* 266:    */   public double getMeasure(String additionalMeasureName)
/* 267:    */   {
/* 268:675 */     if ((this.m_ResultProducer instanceof AdditionalMeasureProducer)) {
/* 269:676 */       return ((AdditionalMeasureProducer)this.m_ResultProducer).getMeasure(additionalMeasureName);
/* 270:    */     }
/* 271:679 */     throw new IllegalArgumentException("DatabaseResultProducer: Can't return value for : " + additionalMeasureName + ". " + this.m_ResultProducer.getClass().getName() + " " + "is not an AdditionalMeasureProducer");
/* 272:    */   }
/* 273:    */   
/* 274:    */   public void setInstances(Instances instances)
/* 275:    */   {
/* 276:694 */     this.m_Instances = instances;
/* 277:    */   }
/* 278:    */   
/* 279:    */   public void setResultListener(ResultListener listener)
/* 280:    */   {
/* 281:705 */     this.m_ResultListener = listener;
/* 282:    */   }
/* 283:    */   
/* 284:    */   public String resultProducerTipText()
/* 285:    */   {
/* 286:715 */     return "Set the result producer to use. If some results are not found in the source database then this result producer is used to generate them.";
/* 287:    */   }
/* 288:    */   
/* 289:    */   public ResultProducer getResultProducer()
/* 290:    */   {
/* 291:727 */     return this.m_ResultProducer;
/* 292:    */   }
/* 293:    */   
/* 294:    */   public void setResultProducer(ResultProducer newResultProducer)
/* 295:    */   {
/* 296:737 */     this.m_ResultProducer = newResultProducer;
/* 297:    */   }
/* 298:    */   
/* 299:    */   public String toString()
/* 300:    */   {
/* 301:748 */     String result = "DatabaseResultProducer: ";
/* 302:749 */     result = result + getCompatibilityState();
/* 303:750 */     if (this.m_Instances == null) {
/* 304:751 */       result = result + ": <null Instances>";
/* 305:    */     } else {
/* 306:753 */       result = result + ": " + Utils.backQuoteChars(this.m_Instances.relationName());
/* 307:    */     }
/* 308:755 */     return result;
/* 309:    */   }
/* 310:    */   
/* 311:    */   public String getRevision()
/* 312:    */   {
/* 313:765 */     return RevisionUtils.extract("$Revision: 11247 $");
/* 314:    */   }
/* 315:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.experiment.DatabaseResultProducer
 * JD-Core Version:    0.7.0.1
 */