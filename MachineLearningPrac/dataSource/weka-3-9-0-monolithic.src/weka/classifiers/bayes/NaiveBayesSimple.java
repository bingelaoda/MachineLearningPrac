/*   1:    */ package weka.classifiers.bayes;
/*   2:    */ 
/*   3:    */ import java.util.Enumeration;
/*   4:    */ import weka.classifiers.AbstractClassifier;
/*   5:    */ import weka.core.Attribute;
/*   6:    */ import weka.core.Capabilities;
/*   7:    */ import weka.core.Capabilities.Capability;
/*   8:    */ import weka.core.Instance;
/*   9:    */ import weka.core.Instances;
/*  10:    */ import weka.core.RevisionUtils;
/*  11:    */ import weka.core.TechnicalInformation;
/*  12:    */ import weka.core.TechnicalInformation.Field;
/*  13:    */ import weka.core.TechnicalInformation.Type;
/*  14:    */ import weka.core.TechnicalInformationHandler;
/*  15:    */ import weka.core.Utils;
/*  16:    */ 
/*  17:    */ public class NaiveBayesSimple
/*  18:    */   extends AbstractClassifier
/*  19:    */   implements TechnicalInformationHandler
/*  20:    */ {
/*  21:    */   static final long serialVersionUID = -1478242251770381214L;
/*  22:    */   protected double[][][] m_Counts;
/*  23:    */   protected double[][] m_Means;
/*  24:    */   protected double[][] m_Devs;
/*  25:    */   protected double[] m_Priors;
/*  26:    */   protected Instances m_Instances;
/*  27:100 */   protected static double NORM_CONST = Math.sqrt(6.283185307179586D);
/*  28:    */   
/*  29:    */   public String globalInfo()
/*  30:    */   {
/*  31:109 */     return "Class for building and using a simple Naive Bayes classifier.Numeric attributes are modelled by a normal distribution.\n\nFor more information, see\n\n" + getTechnicalInformation().toString();
/*  32:    */   }
/*  33:    */   
/*  34:    */   public TechnicalInformation getTechnicalInformation()
/*  35:    */   {
/*  36:125 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.BOOK);
/*  37:126 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Richard Duda and Peter Hart");
/*  38:127 */     result.setValue(TechnicalInformation.Field.YEAR, "1973");
/*  39:128 */     result.setValue(TechnicalInformation.Field.TITLE, "Pattern Classification and Scene Analysis");
/*  40:129 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "Wiley");
/*  41:130 */     result.setValue(TechnicalInformation.Field.ADDRESS, "New York");
/*  42:    */     
/*  43:132 */     return result;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public Capabilities getCapabilities()
/*  47:    */   {
/*  48:142 */     Capabilities result = super.getCapabilities();
/*  49:143 */     result.disableAll();
/*  50:    */     
/*  51:    */ 
/*  52:146 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  53:147 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  54:148 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*  55:149 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  56:    */     
/*  57:    */ 
/*  58:152 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  59:153 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  60:    */     
/*  61:155 */     return result;
/*  62:    */   }
/*  63:    */   
/*  64:    */   public void buildClassifier(Instances instances)
/*  65:    */     throws Exception
/*  66:    */   {
/*  67:167 */     int attIndex = 0;
/*  68:    */     
/*  69:    */ 
/*  70:    */ 
/*  71:171 */     getCapabilities().testWithFail(instances);
/*  72:    */     
/*  73:    */ 
/*  74:174 */     instances = new Instances(instances);
/*  75:175 */     instances.deleteWithMissingClass();
/*  76:    */     
/*  77:177 */     this.m_Instances = new Instances(instances, 0);
/*  78:    */     
/*  79:    */ 
/*  80:180 */     this.m_Counts = new double[instances.numClasses()][instances.numAttributes() - 1][0];
/*  81:181 */     this.m_Means = new double[instances.numClasses()][instances.numAttributes() - 1];
/*  82:182 */     this.m_Devs = new double[instances.numClasses()][instances.numAttributes() - 1];
/*  83:183 */     this.m_Priors = new double[instances.numClasses()];
/*  84:184 */     Enumeration<Attribute> enu = instances.enumerateAttributes();
/*  85:185 */     while (enu.hasMoreElements())
/*  86:    */     {
/*  87:186 */       Attribute attribute = (Attribute)enu.nextElement();
/*  88:187 */       if (attribute.isNominal()) {
/*  89:188 */         for (int j = 0; j < instances.numClasses(); j++) {
/*  90:189 */           this.m_Counts[j][attIndex] = new double[attribute.numValues()];
/*  91:    */         }
/*  92:    */       } else {
/*  93:192 */         for (int j = 0; j < instances.numClasses(); j++) {
/*  94:193 */           this.m_Counts[j][attIndex] = new double[1];
/*  95:    */         }
/*  96:    */       }
/*  97:196 */       attIndex++;
/*  98:    */     }
/*  99:200 */     Enumeration<Instance> enumInsts = instances.enumerateInstances();
/* 100:201 */     while (enumInsts.hasMoreElements())
/* 101:    */     {
/* 102:202 */       Instance instance = (Instance)enumInsts.nextElement();
/* 103:203 */       if (!instance.classIsMissing())
/* 104:    */       {
/* 105:204 */         Enumeration<Attribute> enumAtts = instances.enumerateAttributes();
/* 106:205 */         attIndex = 0;
/* 107:206 */         while (enumAtts.hasMoreElements())
/* 108:    */         {
/* 109:207 */           Attribute attribute = (Attribute)enumAtts.nextElement();
/* 110:208 */           if (!instance.isMissing(attribute)) {
/* 111:209 */             if (attribute.isNominal())
/* 112:    */             {
/* 113:210 */               this.m_Counts[((int)instance.classValue())][attIndex][((int)instance.value(attribute))] += 1.0D;
/* 114:    */             }
/* 115:    */             else
/* 116:    */             {
/* 117:213 */               this.m_Means[((int)instance.classValue())][attIndex] += instance.value(attribute);
/* 118:    */               
/* 119:215 */               this.m_Counts[((int)instance.classValue())][attIndex][0] += 1.0D;
/* 120:    */             }
/* 121:    */           }
/* 122:218 */           attIndex++;
/* 123:    */         }
/* 124:220 */         this.m_Priors[((int)instance.classValue())] += 1.0D;
/* 125:    */       }
/* 126:    */     }
/* 127:225 */     Enumeration<Attribute> enumAtts = instances.enumerateAttributes();
/* 128:226 */     attIndex = 0;
/* 129:227 */     while (enumAtts.hasMoreElements())
/* 130:    */     {
/* 131:228 */       Attribute attribute = (Attribute)enumAtts.nextElement();
/* 132:229 */       if (attribute.isNumeric()) {
/* 133:230 */         for (int j = 0; j < instances.numClasses(); j++)
/* 134:    */         {
/* 135:231 */           if (this.m_Counts[j][attIndex][0] < 2.0D) {
/* 136:232 */             throw new Exception("attribute " + attribute.name() + ": less than two values for class " + instances.classAttribute().value(j));
/* 137:    */           }
/* 138:236 */           this.m_Means[j][attIndex] /= this.m_Counts[j][attIndex][0];
/* 139:    */         }
/* 140:    */       }
/* 141:239 */       attIndex++;
/* 142:    */     }
/* 143:243 */     enumInsts = instances.enumerateInstances();
/* 144:244 */     while (enumInsts.hasMoreElements())
/* 145:    */     {
/* 146:245 */       Instance instance = (Instance)enumInsts.nextElement();
/* 147:246 */       if (!instance.classIsMissing())
/* 148:    */       {
/* 149:247 */         enumAtts = instances.enumerateAttributes();
/* 150:248 */         attIndex = 0;
/* 151:249 */         while (enumAtts.hasMoreElements())
/* 152:    */         {
/* 153:250 */           Attribute attribute = (Attribute)enumAtts.nextElement();
/* 154:251 */           if ((!instance.isMissing(attribute)) && 
/* 155:252 */             (attribute.isNumeric())) {
/* 156:253 */             this.m_Devs[((int)instance.classValue())][attIndex] += (this.m_Means[((int)instance.classValue())][attIndex] - instance.value(attribute)) * (this.m_Means[((int)instance.classValue())][attIndex] - instance.value(attribute));
/* 157:    */           }
/* 158:259 */           attIndex++;
/* 159:    */         }
/* 160:    */       }
/* 161:    */     }
/* 162:263 */     enumAtts = instances.enumerateAttributes();
/* 163:264 */     attIndex = 0;
/* 164:265 */     while (enumAtts.hasMoreElements())
/* 165:    */     {
/* 166:266 */       Attribute attribute = (Attribute)enumAtts.nextElement();
/* 167:267 */       if (attribute.isNumeric()) {
/* 168:268 */         for (int j = 0; j < instances.numClasses(); j++)
/* 169:    */         {
/* 170:269 */           if (this.m_Devs[j][attIndex] <= 0.0D) {
/* 171:270 */             throw new Exception("attribute " + attribute.name() + ": standard deviation is 0 for class " + instances.classAttribute().value(j));
/* 172:    */           }
/* 173:274 */           this.m_Devs[j][attIndex] /= (this.m_Counts[j][attIndex][0] - 1.0D);
/* 174:275 */           this.m_Devs[j][attIndex] = Math.sqrt(this.m_Devs[j][attIndex]);
/* 175:    */         }
/* 176:    */       }
/* 177:279 */       attIndex++;
/* 178:    */     }
/* 179:283 */     enumAtts = instances.enumerateAttributes();
/* 180:284 */     attIndex = 0;
/* 181:285 */     while (enumAtts.hasMoreElements())
/* 182:    */     {
/* 183:286 */       Attribute attribute = (Attribute)enumAtts.nextElement();
/* 184:287 */       if (attribute.isNominal()) {
/* 185:288 */         for (int j = 0; j < instances.numClasses(); j++)
/* 186:    */         {
/* 187:289 */           double sum = Utils.sum(this.m_Counts[j][attIndex]);
/* 188:290 */           for (int i = 0; i < attribute.numValues(); i++) {
/* 189:291 */             this.m_Counts[j][attIndex][i] = ((this.m_Counts[j][attIndex][i] + 1.0D) / (sum + attribute.numValues()));
/* 190:    */           }
/* 191:    */         }
/* 192:    */       }
/* 193:296 */       attIndex++;
/* 194:    */     }
/* 195:300 */     double sum = Utils.sum(this.m_Priors);
/* 196:301 */     for (int j = 0; j < instances.numClasses(); j++) {
/* 197:302 */       this.m_Priors[j] = ((this.m_Priors[j] + 1.0D) / (sum + instances.numClasses()));
/* 198:    */     }
/* 199:    */   }
/* 200:    */   
/* 201:    */   public double[] distributionForInstance(Instance instance)
/* 202:    */     throws Exception
/* 203:    */   {
/* 204:316 */     double[] probs = new double[instance.numClasses()];
/* 205:319 */     for (int j = 0; j < instance.numClasses(); j++)
/* 206:    */     {
/* 207:320 */       probs[j] = 1.0D;
/* 208:321 */       Enumeration<Attribute> enumAtts = instance.enumerateAttributes();
/* 209:322 */       int attIndex = 0;
/* 210:323 */       while (enumAtts.hasMoreElements())
/* 211:    */       {
/* 212:324 */         Attribute attribute = (Attribute)enumAtts.nextElement();
/* 213:325 */         if (!instance.isMissing(attribute)) {
/* 214:326 */           if (attribute.isNominal()) {
/* 215:327 */             probs[j] *= this.m_Counts[j][attIndex][((int)instance.value(attribute))];
/* 216:    */           } else {
/* 217:329 */             probs[j] *= normalDens(instance.value(attribute), this.m_Means[j][attIndex], this.m_Devs[j][attIndex]);
/* 218:    */           }
/* 219:    */         }
/* 220:333 */         attIndex++;
/* 221:    */       }
/* 222:335 */       probs[j] *= this.m_Priors[j];
/* 223:    */     }
/* 224:339 */     Utils.normalize(probs);
/* 225:    */     
/* 226:341 */     return probs;
/* 227:    */   }
/* 228:    */   
/* 229:    */   public String toString()
/* 230:    */   {
/* 231:352 */     if (this.m_Instances == null) {
/* 232:353 */       return "Naive Bayes (simple): No model built yet.";
/* 233:    */     }
/* 234:    */     try
/* 235:    */     {
/* 236:356 */       StringBuffer text = new StringBuffer("Naive Bayes (simple)");
/* 237:359 */       for (int i = 0; i < this.m_Instances.numClasses(); i++)
/* 238:    */       {
/* 239:360 */         text.append("\n\nClass " + this.m_Instances.classAttribute().value(i) + ": P(C) = " + Utils.doubleToString(this.m_Priors[i], 10, 8) + "\n\n");
/* 240:    */         
/* 241:362 */         Enumeration<Attribute> enumAtts = this.m_Instances.enumerateAttributes();
/* 242:363 */         int attIndex = 0;
/* 243:364 */         while (enumAtts.hasMoreElements())
/* 244:    */         {
/* 245:365 */           Attribute attribute = (Attribute)enumAtts.nextElement();
/* 246:366 */           text.append("Attribute " + attribute.name() + "\n");
/* 247:367 */           if (attribute.isNominal())
/* 248:    */           {
/* 249:368 */             for (int j = 0; j < attribute.numValues(); j++) {
/* 250:369 */               text.append(attribute.value(j) + "\t");
/* 251:    */             }
/* 252:371 */             text.append("\n");
/* 253:372 */             for (int j = 0; j < attribute.numValues(); j++) {
/* 254:373 */               text.append(Utils.doubleToString(this.m_Counts[i][attIndex][j], 10, 8) + "\t");
/* 255:    */             }
/* 256:    */           }
/* 257:    */           else
/* 258:    */           {
/* 259:377 */             text.append("Mean: " + Utils.doubleToString(this.m_Means[i][attIndex], 10, 8) + "\t");
/* 260:    */             
/* 261:379 */             text.append("Standard Deviation: " + Utils.doubleToString(this.m_Devs[i][attIndex], 10, 8));
/* 262:    */           }
/* 263:382 */           text.append("\n\n");
/* 264:383 */           attIndex++;
/* 265:    */         }
/* 266:    */       }
/* 267:387 */       return text.toString();
/* 268:    */     }
/* 269:    */     catch (Exception e) {}
/* 270:389 */     return "Can't print Naive Bayes classifier!";
/* 271:    */   }
/* 272:    */   
/* 273:    */   protected double normalDens(double x, double mean, double stdDev)
/* 274:    */   {
/* 275:403 */     double diff = x - mean;
/* 276:    */     
/* 277:405 */     return 1.0D / (NORM_CONST * stdDev) * Math.exp(-(diff * diff / (2.0D * stdDev * stdDev)));
/* 278:    */   }
/* 279:    */   
/* 280:    */   public String getRevision()
/* 281:    */   {
/* 282:416 */     return RevisionUtils.extract("$Revision: 10390 $");
/* 283:    */   }
/* 284:    */   
/* 285:    */   public static void main(String[] argv)
/* 286:    */   {
/* 287:425 */     runClassifier(new NaiveBayesSimple(), argv);
/* 288:    */   }
/* 289:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.bayes.NaiveBayesSimple
 * JD-Core Version:    0.7.0.1
 */