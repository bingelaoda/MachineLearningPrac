/*   1:    */ package weka.datagenerators.classifiers.classification;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Random;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.core.Attribute;
/*   8:    */ import weka.core.DenseInstance;
/*   9:    */ import weka.core.Instance;
/*  10:    */ import weka.core.Instances;
/*  11:    */ import weka.core.Option;
/*  12:    */ import weka.core.RevisionUtils;
/*  13:    */ import weka.core.TechnicalInformation;
/*  14:    */ import weka.core.TechnicalInformation.Field;
/*  15:    */ import weka.core.TechnicalInformation.Type;
/*  16:    */ import weka.core.TechnicalInformationHandler;
/*  17:    */ import weka.core.Utils;
/*  18:    */ import weka.datagenerators.ClassificationGenerator;
/*  19:    */ 
/*  20:    */ public class LED24
/*  21:    */   extends ClassificationGenerator
/*  22:    */   implements TechnicalInformationHandler
/*  23:    */ {
/*  24:    */   static final long serialVersionUID = -7880209100415868737L;
/*  25:    */   protected double m_NoisePercent;
/*  26:135 */   protected static final int[][] m_originalInstances = { { 1, 1, 1, 0, 1, 1, 1 }, { 0, 0, 1, 0, 0, 1, 0 }, { 1, 0, 1, 1, 1, 0, 1 }, { 1, 0, 1, 1, 0, 1, 1 }, { 0, 1, 1, 1, 0, 1, 0 }, { 1, 1, 0, 1, 0, 1, 1 }, { 1, 1, 0, 1, 1, 1, 1 }, { 1, 0, 1, 0, 0, 1, 0 }, { 1, 1, 1, 1, 1, 1, 1 }, { 1, 1, 1, 1, 0, 1, 1 } };
/*  27:142 */   protected int m_numIrrelevantAttributes = 17;
/*  28:    */   
/*  29:    */   public LED24()
/*  30:    */   {
/*  31:150 */     setNoisePercent(defaultNoisePercent());
/*  32:    */   }
/*  33:    */   
/*  34:    */   public String globalInfo()
/*  35:    */   {
/*  36:160 */     return "This generator produces data for a display with 7 LEDs. The original output consists of 10 concepts and 7 boolean attributes. Here, in addition to the 7 necessary boolean attributes, 17 other, irrelevant boolean attributes with random values are added to make it harder. By default 10 percent of noise are added to the data.\n\nMore information can be found here:\n" + getTechnicalInformation().toString();
/*  37:    */   }
/*  38:    */   
/*  39:    */   public TechnicalInformation getTechnicalInformation()
/*  40:    */   {
/*  41:181 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INBOOK);
/*  42:182 */     result.setValue(TechnicalInformation.Field.AUTHOR, "L. Breiman J.H. Friedman R.A. Olshen and C.J. Stone");
/*  43:    */     
/*  44:184 */     result.setValue(TechnicalInformation.Field.YEAR, "1984");
/*  45:185 */     result.setValue(TechnicalInformation.Field.TITLE, "Classification and Regression Trees");
/*  46:186 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "Wadsworth International Group");
/*  47:187 */     result.setValue(TechnicalInformation.Field.ADDRESS, "Belmont, California");
/*  48:188 */     result.setValue(TechnicalInformation.Field.PAGES, "43-49");
/*  49:189 */     result.setValue(TechnicalInformation.Field.ISBN, "0412048418");
/*  50:190 */     result.setValue(TechnicalInformation.Field.URL, "http://www.ics.uci.edu/~mlearn/databases/led-display-creator/");
/*  51:    */     
/*  52:    */ 
/*  53:193 */     return result;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public Enumeration<Option> listOptions()
/*  57:    */   {
/*  58:203 */     Vector<Option> result = enumToVector(super.listOptions());
/*  59:    */     
/*  60:205 */     result.add(new Option("\tThe noise percentage. (default " + defaultNoisePercent() + ")", "N", 1, "-N <num>"));
/*  61:    */     
/*  62:    */ 
/*  63:208 */     return result.elements();
/*  64:    */   }
/*  65:    */   
/*  66:    */   public void setOptions(String[] options)
/*  67:    */     throws Exception
/*  68:    */   {
/*  69:263 */     super.setOptions(options);
/*  70:    */     
/*  71:265 */     String tmpStr = Utils.getOption('N', options);
/*  72:266 */     if (tmpStr.length() != 0) {
/*  73:267 */       setNoisePercent(Double.parseDouble(tmpStr));
/*  74:    */     } else {
/*  75:269 */       setNoisePercent(defaultNoisePercent());
/*  76:    */     }
/*  77:    */   }
/*  78:    */   
/*  79:    */   public String[] getOptions()
/*  80:    */   {
/*  81:284 */     Vector<String> result = new Vector();
/*  82:285 */     String[] options = super.getOptions();
/*  83:286 */     for (int i = 0; i < options.length; i++) {
/*  84:287 */       result.add(options[i]);
/*  85:    */     }
/*  86:290 */     result.add("-N");
/*  87:291 */     result.add("" + getNoisePercent());
/*  88:    */     
/*  89:293 */     return (String[])result.toArray(new String[result.size()]);
/*  90:    */   }
/*  91:    */   
/*  92:    */   protected double defaultNoisePercent()
/*  93:    */   {
/*  94:302 */     return 10.0D;
/*  95:    */   }
/*  96:    */   
/*  97:    */   public double getNoisePercent()
/*  98:    */   {
/*  99:311 */     return this.m_NoisePercent;
/* 100:    */   }
/* 101:    */   
/* 102:    */   public void setNoisePercent(double value)
/* 103:    */   {
/* 104:320 */     if ((value >= 0.0D) && (value <= 100.0D)) {
/* 105:321 */       this.m_NoisePercent = value;
/* 106:    */     } else {
/* 107:323 */       throw new IllegalArgumentException("Noise percent must be in [0,100] (provided: " + value + ")!");
/* 108:    */     }
/* 109:    */   }
/* 110:    */   
/* 111:    */   public String noisePercentTipText()
/* 112:    */   {
/* 113:335 */     return "The noise percent: 0 <= perc <= 100.";
/* 114:    */   }
/* 115:    */   
/* 116:    */   public boolean getSingleModeFlag()
/* 117:    */     throws Exception
/* 118:    */   {
/* 119:347 */     return true;
/* 120:    */   }
/* 121:    */   
/* 122:    */   public Instances defineDataFormat()
/* 123:    */     throws Exception
/* 124:    */   {
/* 125:366 */     this.m_Random = new Random(getSeed());
/* 126:    */     
/* 127:    */ 
/* 128:369 */     setNumExamplesAct(getNumExamples());
/* 129:    */     
/* 130:    */ 
/* 131:372 */     ArrayList<Attribute> atts = new ArrayList();
/* 132:374 */     for (int n = 1; n <= 24; n++)
/* 133:    */     {
/* 134:375 */       ArrayList<String> attValues = new ArrayList();
/* 135:376 */       for (int i = 0; i < 2; i++) {
/* 136:377 */         attValues.add("" + i);
/* 137:    */       }
/* 138:379 */       atts.add(new Attribute("att" + n, attValues));
/* 139:    */     }
/* 140:382 */     ArrayList<String> attValues = new ArrayList();
/* 141:383 */     for (int i = 0; i < 10; i++) {
/* 142:384 */       attValues.add("" + i);
/* 143:    */     }
/* 144:386 */     atts.add(new Attribute("class", attValues));
/* 145:    */     
/* 146:    */ 
/* 147:389 */     this.m_DatasetFormat = new Instances(getRelationNameToUse(), atts, 0);
/* 148:    */     
/* 149:391 */     return this.m_DatasetFormat;
/* 150:    */   }
/* 151:    */   
/* 152:    */   public Instance generateExample()
/* 153:    */     throws Exception
/* 154:    */   {
/* 155:410 */     Instance result = null;
/* 156:411 */     Random random = getRandom();
/* 157:413 */     if (this.m_DatasetFormat == null) {
/* 158:414 */       throw new Exception("Dataset format not defined.");
/* 159:    */     }
/* 160:417 */     double[] atts = new double[this.m_DatasetFormat.numAttributes()];
/* 161:418 */     int selected = random.nextInt(10);
/* 162:419 */     for (int i = 0; i < 7; i++) {
/* 163:420 */       if (1 + random.nextInt(100) <= getNoisePercent()) {
/* 164:421 */         atts[i] = (m_originalInstances[selected][i] == 0 ? 1.0D : 0.0D);
/* 165:    */       } else {
/* 166:423 */         atts[i] = m_originalInstances[selected][i];
/* 167:    */       }
/* 168:    */     }
/* 169:427 */     for (i = 0; i < this.m_numIrrelevantAttributes; i++) {
/* 170:428 */       atts[(i + 7)] = random.nextInt(2);
/* 171:    */     }
/* 172:431 */     atts[(atts.length - 1)] = selected;
/* 173:    */     
/* 174:    */ 
/* 175:434 */     result = new DenseInstance(1.0D, atts);
/* 176:435 */     result.setDataset(this.m_DatasetFormat);
/* 177:    */     
/* 178:437 */     return result;
/* 179:    */   }
/* 180:    */   
/* 181:    */   public Instances generateExamples()
/* 182:    */     throws Exception
/* 183:    */   {
/* 184:455 */     Instances result = new Instances(this.m_DatasetFormat, 0);
/* 185:456 */     this.m_Random = new Random(getSeed());
/* 186:458 */     for (int i = 0; i < getNumExamplesAct(); i++) {
/* 187:459 */       result.add(generateExample());
/* 188:    */     }
/* 189:462 */     return result;
/* 190:    */   }
/* 191:    */   
/* 192:    */   public String generateStart()
/* 193:    */   {
/* 194:474 */     return "";
/* 195:    */   }
/* 196:    */   
/* 197:    */   public String generateFinished()
/* 198:    */     throws Exception
/* 199:    */   {
/* 200:486 */     return "";
/* 201:    */   }
/* 202:    */   
/* 203:    */   public String getRevision()
/* 204:    */   {
/* 205:496 */     return RevisionUtils.extract("$Revision: 10203 $");
/* 206:    */   }
/* 207:    */   
/* 208:    */   public static void main(String[] args)
/* 209:    */   {
/* 210:505 */     runDataGenerator(new LED24(), args);
/* 211:    */   }
/* 212:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.datagenerators.classifiers.classification.LED24
 * JD-Core Version:    0.7.0.1
 */