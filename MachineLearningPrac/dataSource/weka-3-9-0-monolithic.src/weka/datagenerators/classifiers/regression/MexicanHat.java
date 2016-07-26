/*   1:    */ package weka.datagenerators.classifiers.regression;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Random;
/*   7:    */ import java.util.Vector;
/*   8:    */ import weka.core.Attribute;
/*   9:    */ import weka.core.DenseInstance;
/*  10:    */ import weka.core.Instance;
/*  11:    */ import weka.core.Instances;
/*  12:    */ import weka.core.Option;
/*  13:    */ import weka.core.RevisionUtils;
/*  14:    */ import weka.core.Utils;
/*  15:    */ import weka.datagenerators.RegressionGenerator;
/*  16:    */ 
/*  17:    */ public class MexicanHat
/*  18:    */   extends RegressionGenerator
/*  19:    */ {
/*  20:    */   static final long serialVersionUID = 4577016375261512975L;
/*  21:    */   protected double m_Amplitude;
/*  22:    */   protected double m_MinRange;
/*  23:    */   protected double m_MaxRange;
/*  24:    */   protected double m_NoiseRate;
/*  25:    */   protected double m_NoiseVariance;
/*  26:129 */   protected Random m_NoiseRandom = null;
/*  27:    */   
/*  28:    */   public MexicanHat()
/*  29:    */   {
/*  30:137 */     setAmplitude(defaultAmplitude());
/*  31:138 */     setMinRange(defaultMinRange());
/*  32:139 */     setMaxRange(defaultMaxRange());
/*  33:140 */     setNoiseRate(defaultNoiseRate());
/*  34:141 */     setNoiseVariance(defaultNoiseVariance());
/*  35:    */   }
/*  36:    */   
/*  37:    */   public String globalInfo()
/*  38:    */   {
/*  39:151 */     return "A data generator for the simple 'Mexian Hat' function:\n   y = sin|x| / |x|\nIn addition to this simple function, the amplitude can be changed and gaussian noise can be added.";
/*  40:    */   }
/*  41:    */   
/*  42:    */   public Enumeration<Option> listOptions()
/*  43:    */   {
/*  44:164 */     Vector<Option> result = enumToVector(super.listOptions());
/*  45:    */     
/*  46:166 */     result.addElement(new Option("\tThe amplitude multiplier (default " + defaultAmplitude() + ").", "A", 1, "-A <num>"));
/*  47:    */     
/*  48:    */ 
/*  49:169 */     result.addElement(new Option("\tThe range x is randomly drawn from (default " + defaultMinRange() + ".." + defaultMaxRange() + ").", "R", 1, "-R <num>..<num>"));
/*  50:    */     
/*  51:    */ 
/*  52:    */ 
/*  53:173 */     result.addElement(new Option("\tThe noise rate (default " + defaultNoiseRate() + ").", "N", 1, "-N <num>"));
/*  54:    */     
/*  55:    */ 
/*  56:176 */     result.addElement(new Option("\tThe noise variance (default " + defaultNoiseVariance() + ").", "V", 1, "-V <num>"));
/*  57:    */     
/*  58:    */ 
/*  59:179 */     return result.elements();
/*  60:    */   }
/*  61:    */   
/*  62:    */   public void setOptions(String[] options)
/*  63:    */     throws Exception
/*  64:    */   {
/*  65:249 */     super.setOptions(options);
/*  66:    */     
/*  67:251 */     String tmpStr = Utils.getOption('A', options);
/*  68:252 */     if (tmpStr.length() != 0) {
/*  69:253 */       setAmplitude(Double.parseDouble(tmpStr));
/*  70:    */     } else {
/*  71:255 */       setAmplitude(defaultAmplitude());
/*  72:    */     }
/*  73:258 */     tmpStr = Utils.getOption('R', options);
/*  74:259 */     if (tmpStr.length() != 0) {
/*  75:260 */       setRange(tmpStr);
/*  76:    */     } else {
/*  77:262 */       setRange(defaultMinRange() + ".." + defaultMaxRange());
/*  78:    */     }
/*  79:265 */     tmpStr = Utils.getOption('N', options);
/*  80:266 */     if (tmpStr.length() != 0) {
/*  81:267 */       setNoiseRate(Double.parseDouble(tmpStr));
/*  82:    */     } else {
/*  83:269 */       setNoiseRate(defaultNoiseRate());
/*  84:    */     }
/*  85:272 */     tmpStr = Utils.getOption('V', options);
/*  86:273 */     if (tmpStr.length() != 0) {
/*  87:274 */       setNoiseVariance(Double.parseDouble(tmpStr));
/*  88:    */     } else {
/*  89:276 */       setNoiseVariance(defaultNoiseVariance());
/*  90:    */     }
/*  91:    */   }
/*  92:    */   
/*  93:    */   public String[] getOptions()
/*  94:    */   {
/*  95:290 */     Vector<String> result = new Vector();
/*  96:291 */     String[] options = removeBlacklist(super.getOptions());
/*  97:292 */     Collections.addAll(result, options);
/*  98:    */     
/*  99:294 */     result.add("-A");
/* 100:295 */     result.add("" + getAmplitude());
/* 101:    */     
/* 102:297 */     result.add("-R");
/* 103:298 */     result.add("" + getRange());
/* 104:    */     
/* 105:300 */     result.add("-N");
/* 106:301 */     result.add("" + getNoiseRate());
/* 107:    */     
/* 108:303 */     result.add("-V");
/* 109:304 */     result.add("" + getNoiseVariance());
/* 110:    */     
/* 111:306 */     return (String[])result.toArray(new String[result.size()]);
/* 112:    */   }
/* 113:    */   
/* 114:    */   protected double defaultAmplitude()
/* 115:    */   {
/* 116:315 */     return 1.0D;
/* 117:    */   }
/* 118:    */   
/* 119:    */   public double getAmplitude()
/* 120:    */   {
/* 121:324 */     return this.m_Amplitude;
/* 122:    */   }
/* 123:    */   
/* 124:    */   public void setAmplitude(double value)
/* 125:    */   {
/* 126:333 */     this.m_Amplitude = value;
/* 127:    */   }
/* 128:    */   
/* 129:    */   public String amplitudeTipText()
/* 130:    */   {
/* 131:343 */     return "The amplitude of the mexican hat.";
/* 132:    */   }
/* 133:    */   
/* 134:    */   protected void setRange(String fromTo)
/* 135:    */   {
/* 136:353 */     int i = fromTo.indexOf("..");
/* 137:354 */     String from = fromTo.substring(0, i);
/* 138:355 */     setMinRange(Double.valueOf(from).doubleValue());
/* 139:356 */     String to = fromTo.substring(i + 2, fromTo.length());
/* 140:357 */     setMaxRange(Double.valueOf(to).doubleValue());
/* 141:    */   }
/* 142:    */   
/* 143:    */   protected String getRange()
/* 144:    */   {
/* 145:367 */     String fromTo = "" + Utils.doubleToString(getMinRange(), 2) + ".." + Utils.doubleToString(getMaxRange(), 2);
/* 146:    */     
/* 147:369 */     return fromTo;
/* 148:    */   }
/* 149:    */   
/* 150:    */   protected String rangeTipText()
/* 151:    */   {
/* 152:379 */     return "The upper and lower boundary for the range x is drawn from randomly.";
/* 153:    */   }
/* 154:    */   
/* 155:    */   protected double defaultMinRange()
/* 156:    */   {
/* 157:388 */     return -10.0D;
/* 158:    */   }
/* 159:    */   
/* 160:    */   public void setMinRange(double value)
/* 161:    */   {
/* 162:397 */     this.m_MinRange = value;
/* 163:    */   }
/* 164:    */   
/* 165:    */   public double getMinRange()
/* 166:    */   {
/* 167:406 */     return this.m_MinRange;
/* 168:    */   }
/* 169:    */   
/* 170:    */   public String minRangeTipText()
/* 171:    */   {
/* 172:416 */     return "The lower boundary for the range x is drawn from randomly.";
/* 173:    */   }
/* 174:    */   
/* 175:    */   protected double defaultMaxRange()
/* 176:    */   {
/* 177:425 */     return 10.0D;
/* 178:    */   }
/* 179:    */   
/* 180:    */   public void setMaxRange(double value)
/* 181:    */   {
/* 182:434 */     this.m_MaxRange = value;
/* 183:    */   }
/* 184:    */   
/* 185:    */   public double getMaxRange()
/* 186:    */   {
/* 187:443 */     return this.m_MaxRange;
/* 188:    */   }
/* 189:    */   
/* 190:    */   public String maxRangeTipText()
/* 191:    */   {
/* 192:453 */     return "The upper boundary for the range x is drawn from randomly.";
/* 193:    */   }
/* 194:    */   
/* 195:    */   protected double defaultNoiseRate()
/* 196:    */   {
/* 197:462 */     return 0.0D;
/* 198:    */   }
/* 199:    */   
/* 200:    */   public double getNoiseRate()
/* 201:    */   {
/* 202:471 */     return this.m_NoiseRate;
/* 203:    */   }
/* 204:    */   
/* 205:    */   public void setNoiseRate(double value)
/* 206:    */   {
/* 207:480 */     this.m_NoiseRate = value;
/* 208:    */   }
/* 209:    */   
/* 210:    */   public String noiseRateTipText()
/* 211:    */   {
/* 212:490 */     return "The gaussian noise rate to use.";
/* 213:    */   }
/* 214:    */   
/* 215:    */   protected double defaultNoiseVariance()
/* 216:    */   {
/* 217:499 */     return 1.0D;
/* 218:    */   }
/* 219:    */   
/* 220:    */   public double getNoiseVariance()
/* 221:    */   {
/* 222:508 */     return this.m_NoiseVariance;
/* 223:    */   }
/* 224:    */   
/* 225:    */   public void setNoiseVariance(double value)
/* 226:    */   {
/* 227:517 */     if (value > 0.0D) {
/* 228:518 */       this.m_NoiseVariance = value;
/* 229:    */     } else {
/* 230:520 */       throw new IllegalArgumentException("Noise variance needs to be > 0 (provided: " + value + ")!");
/* 231:    */     }
/* 232:    */   }
/* 233:    */   
/* 234:    */   public String noiseVarianceTipText()
/* 235:    */   {
/* 236:532 */     return "The noise variance to use.";
/* 237:    */   }
/* 238:    */   
/* 239:    */   public boolean getSingleModeFlag()
/* 240:    */     throws Exception
/* 241:    */   {
/* 242:544 */     return true;
/* 243:    */   }
/* 244:    */   
/* 245:    */   public Instances defineDataFormat()
/* 246:    */     throws Exception
/* 247:    */   {
/* 248:560 */     this.m_Random = new Random(getSeed());
/* 249:561 */     this.m_NoiseRandom = new Random(getSeed());
/* 250:    */     
/* 251:    */ 
/* 252:564 */     setNumExamplesAct(getNumExamples());
/* 253:    */     
/* 254:    */ 
/* 255:567 */     ArrayList<Attribute> atts = new ArrayList();
/* 256:568 */     atts.add(new Attribute("x"));
/* 257:569 */     atts.add(new Attribute("y"));
/* 258:    */     
/* 259:571 */     this.m_DatasetFormat = new Instances(getRelationNameToUse(), atts, 0);
/* 260:    */     
/* 261:573 */     return this.m_DatasetFormat;
/* 262:    */   }
/* 263:    */   
/* 264:    */   public Instance generateExample()
/* 265:    */     throws Exception
/* 266:    */   {
/* 267:592 */     Instance result = null;
/* 268:593 */     Random rand = getRandom();
/* 269:595 */     if (this.m_DatasetFormat == null) {
/* 270:596 */       throw new Exception("Dataset format not defined.");
/* 271:    */     }
/* 272:600 */     double[] atts = new double[this.m_DatasetFormat.numAttributes()];
/* 273:    */     
/* 274:    */ 
/* 275:603 */     double x = rand.nextDouble();
/* 276:    */     
/* 277:605 */     x = x * (getMaxRange() - getMinRange()) + getMinRange();
/* 278:    */     double y;
/* 279:    */     double y;
/* 280:608 */     if (Utils.eq(x, 0.0D)) {
/* 281:609 */       y = getAmplitude();
/* 282:    */     } else {
/* 283:611 */       y = getAmplitude() * StrictMath.sin(StrictMath.abs(x)) / StrictMath.abs(x);
/* 284:    */     }
/* 285:615 */     y += getAmplitude() * this.m_NoiseRandom.nextGaussian() * getNoiseRate() * getNoiseVariance();
/* 286:    */     
/* 287:    */ 
/* 288:618 */     atts[0] = x;
/* 289:619 */     atts[1] = y;
/* 290:620 */     result = new DenseInstance(1.0D, atts);
/* 291:    */     
/* 292:    */ 
/* 293:623 */     result.setDataset(this.m_DatasetFormat);
/* 294:    */     
/* 295:625 */     return result;
/* 296:    */   }
/* 297:    */   
/* 298:    */   public Instances generateExamples()
/* 299:    */     throws Exception
/* 300:    */   {
/* 301:643 */     Instances result = new Instances(this.m_DatasetFormat, 0);
/* 302:644 */     this.m_Random = new Random(getSeed());
/* 303:646 */     for (int i = 0; i < getNumExamplesAct(); i++) {
/* 304:647 */       result.add(generateExample());
/* 305:    */     }
/* 306:650 */     return result;
/* 307:    */   }
/* 308:    */   
/* 309:    */   public String generateStart()
/* 310:    */   {
/* 311:662 */     return "";
/* 312:    */   }
/* 313:    */   
/* 314:    */   public String generateFinished()
/* 315:    */     throws Exception
/* 316:    */   {
/* 317:674 */     return "";
/* 318:    */   }
/* 319:    */   
/* 320:    */   public String getRevision()
/* 321:    */   {
/* 322:684 */     return RevisionUtils.extract("$Revision: 10203 $");
/* 323:    */   }
/* 324:    */   
/* 325:    */   public static void main(String[] args)
/* 326:    */   {
/* 327:693 */     runDataGenerator(new MexicanHat(), args);
/* 328:    */   }
/* 329:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.datagenerators.classifiers.regression.MexicanHat
 * JD-Core Version:    0.7.0.1
 */