/*   1:    */ package weka.filters.unsupervised.timeseries;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Arrays;
/*   5:    */ import java.util.Collections;
/*   6:    */ import java.util.Enumeration;
/*   7:    */ import java.util.List;
/*   8:    */ import weka.core.Attribute;
/*   9:    */ import weka.core.Instance;
/*  10:    */ import weka.core.Instances;
/*  11:    */ import weka.core.Option;
/*  12:    */ import weka.core.Range;
/*  13:    */ import weka.core.RevisionUtils;
/*  14:    */ import weka.core.Statistics;
/*  15:    */ import weka.core.TechnicalInformation;
/*  16:    */ import weka.core.TechnicalInformation.Field;
/*  17:    */ import weka.core.TechnicalInformation.Type;
/*  18:    */ import weka.core.Utils;
/*  19:    */ import weka.filters.Filter;
/*  20:    */ import weka.filters.unsupervised.attribute.Standardize;
/*  21:    */ 
/*  22:    */ public class SAXTransformer
/*  23:    */   extends PAATransformer
/*  24:    */ {
/*  25:    */   private static final long serialVersionUID = 8349903480332417357L;
/*  26:    */   private static final int DEFAULT_ALPHABET_SIZE = 10;
/*  27: 88 */   private int m_AlphabetSize = 10;
/*  28: 91 */   private double[] m_Betas = null;
/*  29:    */   
/*  30:    */   public Enumeration<Option> listOptions()
/*  31:    */   {
/*  32: 98 */     List<Option> options = Collections.list(super.listOptions());
/*  33:    */     
/*  34:100 */     options.add(new Option("\tSpecifies the alphabet size a for the SAX transformation.\n\tThe transformed data points will have discrete values.\n\t(default 10)", "A", 1, "-A <AlphabetSize>"));
/*  35:    */     
/*  36:    */ 
/*  37:    */ 
/*  38:    */ 
/*  39:    */ 
/*  40:106 */     return Collections.enumeration(options);
/*  41:    */   }
/*  42:    */   
/*  43:    */   public String[] getOptions()
/*  44:    */   {
/*  45:115 */     if (this.m_AlphabetSize == 10) {
/*  46:116 */       return super.getOptions();
/*  47:    */     }
/*  48:118 */     List<String> options = new ArrayList(Arrays.asList(super.getOptions()));
/*  49:    */     
/*  50:    */ 
/*  51:121 */     options.add("-A");
/*  52:122 */     options.add("" + this.m_AlphabetSize);
/*  53:    */     
/*  54:124 */     return (String[])options.toArray(new String[0]);
/*  55:    */   }
/*  56:    */   
/*  57:    */   public void setOptions(String[] options)
/*  58:    */     throws Exception
/*  59:    */   {
/*  60:161 */     String a = Utils.getOption('A', options);
/*  61:162 */     if (a.length() != 0) {
/*  62:163 */       this.m_AlphabetSize = Integer.parseInt(a);
/*  63:    */     }
/*  64:165 */     if (this.m_AlphabetSize <= 0) {
/*  65:166 */       throw new Exception("Parameter M must be bigger than 0!");
/*  66:    */     }
/*  67:168 */     super.setOptions(options);
/*  68:170 */     if (getInputFormat() != null) {
/*  69:171 */       setInputFormat(getInputFormat());
/*  70:    */     }
/*  71:173 */     Utils.checkForRemainingOptions(options);
/*  72:    */   }
/*  73:    */   
/*  74:    */   public int getAlphabetSize()
/*  75:    */   {
/*  76:183 */     return this.m_AlphabetSize;
/*  77:    */   }
/*  78:    */   
/*  79:    */   public void setAlphabetSize(int alphabetSize)
/*  80:    */   {
/*  81:192 */     this.m_AlphabetSize = alphabetSize;
/*  82:    */   }
/*  83:    */   
/*  84:    */   public String alphabetSizeTipText()
/*  85:    */   {
/*  86:201 */     return "The alphabet size of the SAX representation";
/*  87:    */   }
/*  88:    */   
/*  89:    */   public String globalInfo()
/*  90:    */   {
/*  91:209 */     return "A filter to perform the Symbolic Aggregate Approximation transformation to time series.\n\nThe filter can handle arbitrarily big alphabet sizes.\n\nFor more information see:\n" + getTechnicalInformation().toString() + "\n\n" + "It makes use of the PAATransformer filter";
/*  92:    */   }
/*  93:    */   
/*  94:    */   public String getRevision()
/*  95:    */   {
/*  96:225 */     return RevisionUtils.extract("$Revision: 1000 $");
/*  97:    */   }
/*  98:    */   
/*  99:    */   public TechnicalInformation getTechnicalInformation()
/* 100:    */   {
/* 101:231 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/* 102:232 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Jessica Lin and Eamonn Keogh and Stefano Lonardi and Bill Chiu");
/* 103:233 */     result.setValue(TechnicalInformation.Field.TITLE, "A Symbolic Representation of Time Series, with Implications for Streaming Algorithms");
/* 104:    */     
/* 105:235 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "Proceedings 8th ACM SIGMOD Workshop on Research issues in Data Mining and Knowledge Discovery ");
/* 106:    */     
/* 107:237 */     result.setValue(TechnicalInformation.Field.YEAR, "2003");
/* 108:238 */     result.setValue(TechnicalInformation.Field.PAGES, "2-11");
/* 109:239 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "ACM");
/* 110:    */     
/* 111:241 */     return result;
/* 112:    */   }
/* 113:    */   
/* 114:    */   protected Instances determineOutputFormat(Instances inputFormat)
/* 115:    */     throws Exception
/* 116:    */   {
/* 117:251 */     super.determineOutputFormat(inputFormat);
/* 118:    */     
/* 119:253 */     List<String> alphabet = generateAlphabet(this.m_AlphabetSize);
/* 120:254 */     this.m_Betas = generateBetas(this.m_AlphabetSize);
/* 121:    */     
/* 122:256 */     ArrayList<Attribute> attributes = new ArrayList(inputFormat.numAttributes());
/* 123:259 */     for (int att = 0; att < inputFormat.numAttributes(); att++) {
/* 124:260 */       if (!this.m_TimeSeriesAttributes.isInRange(att))
/* 125:    */       {
/* 126:261 */         attributes.add((Attribute)inputFormat.attribute(att).copy());
/* 127:    */       }
/* 128:    */       else
/* 129:    */       {
/* 130:265 */         Instances timeSeries = inputFormat.attribute(att).relation();
/* 131:    */         
/* 132:267 */         ArrayList<Attribute> timeSeriesAttributes = new ArrayList(timeSeries.numAttributes());
/* 133:270 */         for (int i = 0; i < timeSeries.numAttributes(); i++) {
/* 134:271 */           timeSeriesAttributes.add(new Attribute(timeSeries.attribute(i).name(), alphabet));
/* 135:    */         }
/* 136:275 */         Instances newTimeSeries = new Instances(timeSeries.relationName(), timeSeriesAttributes, 0);
/* 137:    */         
/* 138:277 */         newTimeSeries.setClassIndex(timeSeries.classIndex());
/* 139:    */         
/* 140:279 */         attributes.add(new Attribute(inputFormat.attribute(att).name(), newTimeSeries));
/* 141:    */       }
/* 142:    */     }
/* 143:285 */     Instances outputFormat = new Instances(inputFormat.relationName(), attributes, 0);
/* 144:    */     
/* 145:287 */     outputFormat.setClassIndex(inputFormat.classIndex());
/* 146:    */     
/* 147:289 */     return outputFormat;
/* 148:    */   }
/* 149:    */   
/* 150:    */   protected Instances transform(Instances inputInstances)
/* 151:    */     throws Exception
/* 152:    */   {
/* 153:305 */     Instances outputInstances = new Instances(getOutputFormat().attribute(inputInstances.relationName()).relation(), inputInstances.numInstances());
/* 154:    */     
/* 155:    */ 
/* 156:    */ 
/* 157:    */ 
/* 158:    */ 
/* 159:311 */     Standardize standardize = new Standardize();
/* 160:312 */     standardize.setInputFormat(inputInstances);
/* 161:313 */     inputInstances = Filter.useFilter(inputInstances, standardize);
/* 162:    */     
/* 163:    */ 
/* 164:316 */     inputInstances = super.transform(inputInstances);
/* 165:319 */     for (int i = 0; i < inputInstances.numInstances(); i++)
/* 166:    */     {
/* 167:321 */       Instance instance = inputInstances.get(i);
/* 168:323 */       for (int att = 0; att < inputInstances.numAttributes(); att++)
/* 169:    */       {
/* 170:325 */         double discretized = binarySearchIntoBetas(this.m_Betas, instance.value(att));
/* 171:    */         
/* 172:    */ 
/* 173:    */ 
/* 174:    */ 
/* 175:330 */         instance.setValue(att, discretized);
/* 176:    */       }
/* 177:334 */       outputInstances.add(instance);
/* 178:    */     }
/* 179:338 */     return outputInstances;
/* 180:    */   }
/* 181:    */   
/* 182:    */   private static final List<String> generateAlphabet(int size)
/* 183:    */   {
/* 184:349 */     List<String> alphabet = new ArrayList(size);
/* 185:    */     
/* 186:351 */     int width = (int)Math.ceil(Math.log(size) / Math.log(26.0D));
/* 187:353 */     for (int i = 0; i < size; i++)
/* 188:    */     {
/* 189:354 */       int integer = i;
/* 190:355 */       String letter = "";
/* 191:356 */       for (int k = 1; k <= width; k++)
/* 192:    */       {
/* 193:357 */         int digit = integer % 26;
/* 194:358 */         integer /= 26;
/* 195:    */         
/* 196:360 */         letter = letter + (char)(97 + digit);
/* 197:    */       }
/* 198:363 */       alphabet.add(new StringBuilder(letter).reverse().toString());
/* 199:    */     }
/* 200:366 */     return alphabet;
/* 201:    */   }
/* 202:    */   
/* 203:    */   private static final double[] generateBetas(int num)
/* 204:    */   {
/* 205:377 */     double[] betas = new double[num - 1];
/* 206:    */     
/* 207:379 */     double area = 1.0D / num;
/* 208:381 */     for (int i = 1; i < num; i++) {
/* 209:382 */       betas[(i - 1)] = Statistics.normalInverse(area * i);
/* 210:    */     }
/* 211:385 */     return betas;
/* 212:    */   }
/* 213:    */   
/* 214:    */   private static final int binarySearchIntoBetas(double[] betas, double real)
/* 215:    */   {
/* 216:406 */     if (real <= betas[0]) {
/* 217:407 */       return 0;
/* 218:    */     }
/* 219:409 */     if (real > betas[(betas.length - 1)]) {
/* 220:410 */       return betas.length;
/* 221:    */     }
/* 222:412 */     int lower = 0;
/* 223:413 */     int upper = betas.length - 1;
/* 224:415 */     while (upper - lower > 1)
/* 225:    */     {
/* 226:416 */       int mid = (lower + upper) / 2;
/* 227:417 */       if (betas[mid] <= real) {
/* 228:418 */         lower = mid;
/* 229:    */       } else {
/* 230:420 */         upper = mid;
/* 231:    */       }
/* 232:    */     }
/* 233:424 */     return upper;
/* 234:    */   }
/* 235:    */   
/* 236:    */   public static void main(String[] args)
/* 237:    */   {
/* 238:433 */     runFilter(new SAXTransformer(), args);
/* 239:    */   }
/* 240:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.timeseries.SAXTransformer
 * JD-Core Version:    0.7.0.1
 */