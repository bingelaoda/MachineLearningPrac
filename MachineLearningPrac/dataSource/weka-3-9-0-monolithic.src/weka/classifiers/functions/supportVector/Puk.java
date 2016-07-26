/*   1:    */ package weka.classifiers.functions.supportVector;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.core.Capabilities;
/*   7:    */ import weka.core.Capabilities.Capability;
/*   8:    */ import weka.core.Instance;
/*   9:    */ import weka.core.Instances;
/*  10:    */ import weka.core.Option;
/*  11:    */ import weka.core.RevisionUtils;
/*  12:    */ import weka.core.TechnicalInformation;
/*  13:    */ import weka.core.TechnicalInformation.Field;
/*  14:    */ import weka.core.TechnicalInformation.Type;
/*  15:    */ import weka.core.TechnicalInformationHandler;
/*  16:    */ import weka.core.Utils;
/*  17:    */ 
/*  18:    */ public class Puk
/*  19:    */   extends CachedKernel
/*  20:    */   implements TechnicalInformationHandler
/*  21:    */ {
/*  22:    */   private static final long serialVersionUID = 1682161522559978851L;
/*  23:    */   protected double[] m_kernelPrecalc;
/*  24: 99 */   protected double m_omega = 1.0D;
/*  25:102 */   protected double m_sigma = 1.0D;
/*  26:105 */   protected double m_factor = 1.0D;
/*  27:    */   
/*  28:    */   public Puk() {}
/*  29:    */   
/*  30:    */   public Puk(Instances data, int cacheSize, double omega, double sigma)
/*  31:    */     throws Exception
/*  32:    */   {
/*  33:127 */     setCacheSize(cacheSize);
/*  34:128 */     setOmega(omega);
/*  35:129 */     setSigma(sigma);
/*  36:    */     
/*  37:131 */     buildKernel(data);
/*  38:    */   }
/*  39:    */   
/*  40:    */   public String globalInfo()
/*  41:    */   {
/*  42:142 */     return "The Pearson VII function-based universal kernel.\n\nFor more information see:\n\n" + getTechnicalInformation().toString();
/*  43:    */   }
/*  44:    */   
/*  45:    */   public TechnicalInformation getTechnicalInformation()
/*  46:    */   {
/*  47:157 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.ARTICLE);
/*  48:158 */     result.setValue(TechnicalInformation.Field.AUTHOR, "B. Uestuen and W.J. Melssen and L.M.C. Buydens");
/*  49:    */     
/*  50:160 */     result.setValue(TechnicalInformation.Field.YEAR, "2006");
/*  51:161 */     result.setValue(TechnicalInformation.Field.TITLE, "Facilitating the application of Support Vector Regression by using a universal Pearson VII function based kernel");
/*  52:    */     
/*  53:    */ 
/*  54:    */ 
/*  55:165 */     result.setValue(TechnicalInformation.Field.JOURNAL, "Chemometrics and Intelligent Laboratory Systems");
/*  56:    */     
/*  57:167 */     result.setValue(TechnicalInformation.Field.VOLUME, "81");
/*  58:168 */     result.setValue(TechnicalInformation.Field.PAGES, "29-40");
/*  59:169 */     result.setValue(TechnicalInformation.Field.PDF, "http://www.cac.science.ru.nl/research/publications/PDFs/ustun2006.pdf");
/*  60:    */     
/*  61:    */ 
/*  62:172 */     return result;
/*  63:    */   }
/*  64:    */   
/*  65:    */   public Enumeration<Option> listOptions()
/*  66:    */   {
/*  67:182 */     Vector<Option> result = new Vector();
/*  68:    */     
/*  69:184 */     result.addElement(new Option("\tThe Omega parameter.\n\t(default: 1.0)", "O", 1, "-O <num>"));
/*  70:    */     
/*  71:    */ 
/*  72:187 */     result.addElement(new Option("\tThe Sigma parameter.\n\t(default: 1.0)", "S", 1, "-S <num>"));
/*  73:    */     
/*  74:    */ 
/*  75:190 */     result.addAll(Collections.list(super.listOptions()));
/*  76:    */     
/*  77:192 */     return result.elements();
/*  78:    */   }
/*  79:    */   
/*  80:    */   public void setOptions(String[] options)
/*  81:    */     throws Exception
/*  82:    */   {
/*  83:242 */     String tmpStr = Utils.getOption('O', options);
/*  84:243 */     if (tmpStr.length() != 0) {
/*  85:244 */       setOmega(Double.parseDouble(tmpStr));
/*  86:    */     } else {
/*  87:246 */       setOmega(1.0D);
/*  88:    */     }
/*  89:249 */     tmpStr = Utils.getOption('S', options);
/*  90:250 */     if (tmpStr.length() != 0) {
/*  91:251 */       setSigma(Double.parseDouble(tmpStr));
/*  92:    */     } else {
/*  93:253 */       setSigma(1.0D);
/*  94:    */     }
/*  95:256 */     super.setOptions(options);
/*  96:    */   }
/*  97:    */   
/*  98:    */   public String[] getOptions()
/*  99:    */   {
/* 100:267 */     Vector<String> result = new Vector();
/* 101:    */     
/* 102:269 */     result.add("-O");
/* 103:270 */     result.add("" + getOmega());
/* 104:    */     
/* 105:272 */     result.add("-S");
/* 106:273 */     result.add("" + getSigma());
/* 107:    */     
/* 108:275 */     Collections.addAll(result, super.getOptions());
/* 109:    */     
/* 110:277 */     return (String[])result.toArray(new String[result.size()]);
/* 111:    */   }
/* 112:    */   
/* 113:    */   protected double evaluate(int id1, int id2, Instance inst1)
/* 114:    */     throws Exception
/* 115:    */   {
/* 116:292 */     if (id1 == id2) {
/* 117:293 */       return 1.0D;
/* 118:    */     }
/* 119:    */     double precalc1;
/* 120:    */     double precalc1;
/* 121:296 */     if (id1 == -1) {
/* 122:297 */       precalc1 = dotProd(inst1, inst1);
/* 123:    */     } else {
/* 124:299 */       precalc1 = this.m_kernelPrecalc[id1];
/* 125:    */     }
/* 126:301 */     Instance inst2 = this.m_data.instance(id2);
/* 127:302 */     double squaredDifference = -2.0D * dotProd(inst1, inst2) + precalc1 + this.m_kernelPrecalc[id2];
/* 128:    */     
/* 129:304 */     double intermediate = this.m_factor * Math.sqrt(squaredDifference);
/* 130:305 */     double result = 1.0D / Math.pow(1.0D + intermediate * intermediate, getOmega());
/* 131:    */     
/* 132:307 */     return result;
/* 133:    */   }
/* 134:    */   
/* 135:    */   public void setOmega(double value)
/* 136:    */   {
/* 137:317 */     this.m_omega = value;
/* 138:318 */     this.m_factor = computeFactor(this.m_omega, this.m_sigma);
/* 139:    */   }
/* 140:    */   
/* 141:    */   public double getOmega()
/* 142:    */   {
/* 143:327 */     return this.m_omega;
/* 144:    */   }
/* 145:    */   
/* 146:    */   public String omegaTipText()
/* 147:    */   {
/* 148:337 */     return "The Omega value.";
/* 149:    */   }
/* 150:    */   
/* 151:    */   public void setSigma(double value)
/* 152:    */   {
/* 153:346 */     this.m_sigma = value;
/* 154:347 */     this.m_factor = computeFactor(this.m_omega, this.m_sigma);
/* 155:    */   }
/* 156:    */   
/* 157:    */   public double getSigma()
/* 158:    */   {
/* 159:356 */     return this.m_sigma;
/* 160:    */   }
/* 161:    */   
/* 162:    */   public String sigmaTipText()
/* 163:    */   {
/* 164:366 */     return "The Sigma value.";
/* 165:    */   }
/* 166:    */   
/* 167:    */   protected double computeFactor(double omega, double sigma)
/* 168:    */   {
/* 169:377 */     double root = Math.sqrt(Math.pow(2.0D, 1.0D / omega) - 1.0D);
/* 170:378 */     return 2.0D * root / sigma;
/* 171:    */   }
/* 172:    */   
/* 173:    */   protected void initVars(Instances data)
/* 174:    */   {
/* 175:388 */     super.initVars(data);
/* 176:    */     
/* 177:390 */     this.m_factor = computeFactor(this.m_omega, this.m_sigma);
/* 178:391 */     this.m_kernelPrecalc = new double[data.numInstances()];
/* 179:    */   }
/* 180:    */   
/* 181:    */   public Capabilities getCapabilities()
/* 182:    */   {
/* 183:402 */     Capabilities result = super.getCapabilities();
/* 184:403 */     result.disableAll();
/* 185:    */     
/* 186:405 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/* 187:406 */     result.enableAllClasses();
/* 188:407 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 189:408 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 190:    */     
/* 191:410 */     return result;
/* 192:    */   }
/* 193:    */   
/* 194:    */   public void buildKernel(Instances data)
/* 195:    */     throws Exception
/* 196:    */   {
/* 197:423 */     if (!getChecksTurnedOff()) {
/* 198:424 */       getCapabilities().testWithFail(data);
/* 199:    */     }
/* 200:427 */     initVars(data);
/* 201:429 */     for (int i = 0; i < data.numInstances(); i++) {
/* 202:430 */       this.m_kernelPrecalc[i] = dotProd(data.instance(i), data.instance(i));
/* 203:    */     }
/* 204:    */   }
/* 205:    */   
/* 206:    */   public String toString()
/* 207:    */   {
/* 208:441 */     return "Puk kernel";
/* 209:    */   }
/* 210:    */   
/* 211:    */   public String getRevision()
/* 212:    */   {
/* 213:451 */     return RevisionUtils.extract("$Revision: 12518 $");
/* 214:    */   }
/* 215:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.supportVector.Puk
 * JD-Core Version:    0.7.0.1
 */