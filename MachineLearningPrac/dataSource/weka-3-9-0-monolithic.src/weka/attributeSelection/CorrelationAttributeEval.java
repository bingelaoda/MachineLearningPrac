/*   1:    */ package weka.attributeSelection;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Arrays;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.List;
/*   7:    */ import java.util.Vector;
/*   8:    */ import weka.core.Attribute;
/*   9:    */ import weka.core.Capabilities;
/*  10:    */ import weka.core.Capabilities.Capability;
/*  11:    */ import weka.core.Instance;
/*  12:    */ import weka.core.Instances;
/*  13:    */ import weka.core.Option;
/*  14:    */ import weka.core.OptionHandler;
/*  15:    */ import weka.core.RevisionUtils;
/*  16:    */ import weka.core.Utils;
/*  17:    */ import weka.filters.Filter;
/*  18:    */ import weka.filters.unsupervised.attribute.ReplaceMissingValues;
/*  19:    */ 
/*  20:    */ public class CorrelationAttributeEval
/*  21:    */   extends ASEvaluation
/*  22:    */   implements AttributeEvaluator, OptionHandler
/*  23:    */ {
/*  24:    */   private static final long serialVersionUID = -4931946995055872438L;
/*  25:    */   protected double[] m_correlations;
/*  26: 76 */   protected boolean m_detailedOutput = false;
/*  27:    */   protected StringBuffer m_detailedOutputBuff;
/*  28:    */   
/*  29:    */   public String globalInfo()
/*  30:    */   {
/*  31: 88 */     return "CorrelationAttributeEval :\n\nEvaluates the worth of an attribute by measuring the correlation (Pearson's) between it and the class.\n\nNominal attributes are considered on a value by value basis by treating each value as an indicator. An overall correlation for a nominal attribute is arrived at via a weighted average.\n";
/*  32:    */   }
/*  33:    */   
/*  34:    */   public Capabilities getCapabilities()
/*  35:    */   {
/*  36:103 */     Capabilities result = super.getCapabilities();
/*  37:104 */     result.disableAll();
/*  38:    */     
/*  39:    */ 
/*  40:107 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  41:108 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  42:109 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*  43:110 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  44:    */     
/*  45:    */ 
/*  46:113 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  47:114 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  48:    */     
/*  49:116 */     return result;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public Enumeration<Option> listOptions()
/*  53:    */   {
/*  54:127 */     Vector<Option> newVector = new Vector();
/*  55:    */     
/*  56:129 */     newVector.addElement(new Option("\tOutput detailed info for nominal attributes", "D", 0, "-D"));
/*  57:    */     
/*  58:    */ 
/*  59:132 */     return newVector.elements();
/*  60:    */   }
/*  61:    */   
/*  62:    */   public void setOptions(String[] options)
/*  63:    */     throws Exception
/*  64:    */   {
/*  65:155 */     setOutputDetailedInfo(Utils.getFlag('D', options));
/*  66:    */   }
/*  67:    */   
/*  68:    */   public String[] getOptions()
/*  69:    */   {
/*  70:165 */     String[] options = new String[1];
/*  71:167 */     if (getOutputDetailedInfo()) {
/*  72:168 */       options[0] = "-D";
/*  73:    */     } else {
/*  74:170 */       options[0] = "";
/*  75:    */     }
/*  76:173 */     return options;
/*  77:    */   }
/*  78:    */   
/*  79:    */   public String outputDetailedInfoTipText()
/*  80:    */   {
/*  81:183 */     return "Output per value correlation for nominal attributes";
/*  82:    */   }
/*  83:    */   
/*  84:    */   public void setOutputDetailedInfo(boolean d)
/*  85:    */   {
/*  86:193 */     this.m_detailedOutput = d;
/*  87:    */   }
/*  88:    */   
/*  89:    */   public boolean getOutputDetailedInfo()
/*  90:    */   {
/*  91:203 */     return this.m_detailedOutput;
/*  92:    */   }
/*  93:    */   
/*  94:    */   public double evaluateAttribute(int attribute)
/*  95:    */     throws Exception
/*  96:    */   {
/*  97:218 */     return this.m_correlations[attribute];
/*  98:    */   }
/*  99:    */   
/* 100:    */   public String toString()
/* 101:    */   {
/* 102:228 */     StringBuffer buff = new StringBuffer();
/* 103:230 */     if (this.m_correlations == null)
/* 104:    */     {
/* 105:231 */       buff.append("Correlation attribute evaluator has not been built yet.");
/* 106:    */     }
/* 107:    */     else
/* 108:    */     {
/* 109:233 */       buff.append("\tCorrelation Ranking Filter");
/* 110:235 */       if ((this.m_detailedOutput) && (this.m_detailedOutputBuff.length() > 0))
/* 111:    */       {
/* 112:236 */         buff.append("\n\tDetailed output for nominal attributes");
/* 113:237 */         buff.append(this.m_detailedOutputBuff);
/* 114:    */       }
/* 115:    */     }
/* 116:241 */     return buff.toString();
/* 117:    */   }
/* 118:    */   
/* 119:    */   public void buildEvaluator(Instances data)
/* 120:    */     throws Exception
/* 121:    */   {
/* 122:253 */     data = new Instances(data);
/* 123:254 */     data.deleteWithMissingClass();
/* 124:    */     
/* 125:256 */     ReplaceMissingValues rmv = new ReplaceMissingValues();
/* 126:257 */     rmv.setInputFormat(data);
/* 127:258 */     data = Filter.useFilter(data, rmv);
/* 128:    */     
/* 129:260 */     int numClasses = data.classAttribute().numValues();
/* 130:261 */     int classIndex = data.classIndex();
/* 131:262 */     int numInstances = data.numInstances();
/* 132:263 */     this.m_correlations = new double[data.numAttributes()];
/* 133:    */     
/* 134:    */ 
/* 135:    */ 
/* 136:267 */     List<Integer> numericIndexes = new ArrayList();
/* 137:268 */     List<Integer> nominalIndexes = new ArrayList();
/* 138:269 */     if (this.m_detailedOutput) {
/* 139:270 */       this.m_detailedOutputBuff = new StringBuffer();
/* 140:    */     }
/* 141:277 */     double[][][] nomAtts = new double[data.numAttributes()][][];
/* 142:278 */     for (int i = 0; i < data.numAttributes(); i++) {
/* 143:279 */       if ((data.attribute(i).isNominal()) && (i != classIndex))
/* 144:    */       {
/* 145:280 */         nomAtts[i] = new double[data.attribute(i).numValues()][data.numInstances()];
/* 146:    */         
/* 147:282 */         Arrays.fill(nomAtts[i][0], 1.0D);
/* 148:    */         
/* 149:284 */         nominalIndexes.add(Integer.valueOf(i));
/* 150:    */       }
/* 151:285 */       else if ((data.attribute(i).isNumeric()) && (i != classIndex))
/* 152:    */       {
/* 153:286 */         numericIndexes.add(Integer.valueOf(i));
/* 154:    */       }
/* 155:    */     }
/* 156:291 */     if (nominalIndexes.size() > 0) {
/* 157:292 */       for (int i = 0; i < data.numInstances(); i++)
/* 158:    */       {
/* 159:293 */         Instance current = data.instance(i);
/* 160:294 */         for (int j = 0; j < current.numValues(); j++) {
/* 161:295 */           if ((current.attribute(current.index(j)).isNominal()) && (current.index(j) != classIndex))
/* 162:    */           {
/* 163:299 */             nomAtts[current.index(j)][((int)current.valueSparse(j))][i] += 1.0D;
/* 164:300 */             nomAtts[current.index(j)][0][i] -= 1.0D;
/* 165:    */           }
/* 166:    */         }
/* 167:    */       }
/* 168:    */     }
/* 169:    */     double[] classVals;
/* 170:    */     double[][] binarizedClasses;
/* 171:    */     double[] classValCounts;
/* 172:    */     double sumClass;
/* 173:306 */     if (data.classAttribute().isNumeric())
/* 174:    */     {
/* 175:307 */       classVals = data.attributeToDoubleArray(classIndex);
/* 176:310 */       for (Integer i : numericIndexes)
/* 177:    */       {
/* 178:311 */         double[] numAttVals = data.attributeToDoubleArray(i.intValue());
/* 179:312 */         this.m_correlations[i.intValue()] = Utils.correlation(numAttVals, classVals, numAttVals.length);
/* 180:315 */         if (this.m_correlations[i.intValue()] == 1.0D) {
/* 181:317 */           if (Utils.variance(numAttVals) == 0.0D) {
/* 182:318 */             this.m_correlations[i.intValue()] = 0.0D;
/* 183:    */           }
/* 184:    */         }
/* 185:    */       }
/* 186:324 */       if (nominalIndexes.size() > 0) {
/* 187:327 */         for (Integer i : nominalIndexes)
/* 188:    */         {
/* 189:328 */           double sum = 0.0D;
/* 190:329 */           double corr = 0.0D;
/* 191:330 */           double sumCorr = 0.0D;
/* 192:331 */           double sumForValue = 0.0D;
/* 193:333 */           if (this.m_detailedOutput) {
/* 194:334 */             this.m_detailedOutputBuff.append("\n\n").append(data.attribute(i.intValue()).name());
/* 195:    */           }
/* 196:338 */           for (int j = 0; j < data.attribute(i.intValue()).numValues(); j++)
/* 197:    */           {
/* 198:339 */             sumForValue = Utils.sum(nomAtts[i.intValue()][j]);
/* 199:340 */             corr = Utils.correlation(nomAtts[i.intValue()][j], classVals, classVals.length);
/* 200:344 */             if ((sumForValue == numInstances) || (sumForValue == 0.0D)) {
/* 201:345 */               corr = 0.0D;
/* 202:    */             }
/* 203:347 */             if (corr < 0.0D) {
/* 204:348 */               corr = -corr;
/* 205:    */             }
/* 206:350 */             sumCorr += sumForValue * corr;
/* 207:351 */             sum += sumForValue;
/* 208:353 */             if (this.m_detailedOutput)
/* 209:    */             {
/* 210:354 */               this.m_detailedOutputBuff.append("\n\t").append(data.attribute(i.intValue()).value(j)).append(": ");
/* 211:    */               
/* 212:356 */               this.m_detailedOutputBuff.append(Utils.doubleToString(corr, 6));
/* 213:    */             }
/* 214:    */           }
/* 215:359 */           this.m_correlations[i.intValue()] = (sum > 0.0D ? sumCorr / sum : 0.0D);
/* 216:    */         }
/* 217:    */       }
/* 218:    */     }
/* 219:    */     else
/* 220:    */     {
/* 221:365 */       binarizedClasses = new double[data.classAttribute().numValues()][data.numInstances()];
/* 222:    */       
/* 223:    */ 
/* 224:    */ 
/* 225:369 */       classValCounts = new double[data.classAttribute().numValues()];
/* 226:371 */       for (int i = 0; i < data.numInstances(); i++)
/* 227:    */       {
/* 228:372 */         Instance current = data.instance(i);
/* 229:373 */         binarizedClasses[((int)current.classValue())][i] = 1.0D;
/* 230:    */       }
/* 231:375 */       for (int i = 0; i < data.classAttribute().numValues(); i++) {
/* 232:376 */         classValCounts[i] = Utils.sum(binarizedClasses[i]);
/* 233:    */       }
/* 234:379 */       sumClass = Utils.sum(classValCounts);
/* 235:382 */       if (numericIndexes.size() > 0) {
/* 236:383 */         for (Integer i : numericIndexes)
/* 237:    */         {
/* 238:384 */           double[] numAttVals = data.attributeToDoubleArray(i.intValue());
/* 239:385 */           double corr = 0.0D;
/* 240:386 */           double sumCorr = 0.0D;
/* 241:388 */           for (int j = 0; j < data.classAttribute().numValues(); j++)
/* 242:    */           {
/* 243:389 */             corr = Utils.correlation(numAttVals, binarizedClasses[j], numAttVals.length);
/* 244:391 */             if (corr < 0.0D) {
/* 245:392 */               corr = -corr;
/* 246:    */             }
/* 247:395 */             if (corr == 1.0D) {
/* 248:397 */               if (Utils.variance(numAttVals) == 0.0D) {
/* 249:398 */                 corr = 0.0D;
/* 250:    */               }
/* 251:    */             }
/* 252:402 */             sumCorr += classValCounts[j] * corr;
/* 253:    */           }
/* 254:404 */           this.m_correlations[i.intValue()] = (sumCorr / sumClass);
/* 255:    */         }
/* 256:    */       }
/* 257:408 */       if (nominalIndexes.size() > 0) {
/* 258:409 */         for (Integer i : nominalIndexes)
/* 259:    */         {
/* 260:410 */           if (this.m_detailedOutput) {
/* 261:411 */             this.m_detailedOutputBuff.append("\n\n").append(data.attribute(i.intValue()).name());
/* 262:    */           }
/* 263:415 */           double sumForAtt = 0.0D;
/* 264:416 */           double corrForAtt = 0.0D;
/* 265:417 */           for (int j = 0; j < data.attribute(i.intValue()).numValues(); j++)
/* 266:    */           {
/* 267:418 */             double sumForValue = Utils.sum(nomAtts[i.intValue()][j]);
/* 268:419 */             double corr = 0.0D;
/* 269:420 */             double sumCorr = 0.0D;
/* 270:421 */             double avgCorrForValue = 0.0D;
/* 271:    */             
/* 272:423 */             sumForAtt += sumForValue;
/* 273:424 */             for (int k = 0; k < numClasses; k++)
/* 274:    */             {
/* 275:427 */               corr = Utils.correlation(nomAtts[i.intValue()][j], binarizedClasses[k], binarizedClasses[k].length);
/* 276:431 */               if ((sumForValue == numInstances) || (sumForValue == 0.0D)) {
/* 277:432 */                 corr = 0.0D;
/* 278:    */               }
/* 279:434 */               if (corr < 0.0D) {
/* 280:435 */                 corr = -corr;
/* 281:    */               }
/* 282:437 */               sumCorr += classValCounts[k] * corr;
/* 283:    */             }
/* 284:439 */             avgCorrForValue = sumCorr / sumClass;
/* 285:440 */             corrForAtt += sumForValue * avgCorrForValue;
/* 286:442 */             if (this.m_detailedOutput)
/* 287:    */             {
/* 288:443 */               this.m_detailedOutputBuff.append("\n\t").append(data.attribute(i.intValue()).value(j)).append(": ");
/* 289:    */               
/* 290:445 */               this.m_detailedOutputBuff.append(Utils.doubleToString(avgCorrForValue, 6));
/* 291:    */             }
/* 292:    */           }
/* 293:452 */           this.m_correlations[i.intValue()] = (sumForAtt > 0.0D ? corrForAtt / sumForAtt : 0.0D);
/* 294:    */         }
/* 295:    */       }
/* 296:    */     }
/* 297:457 */     if ((this.m_detailedOutputBuff != null) && (this.m_detailedOutputBuff.length() > 0)) {
/* 298:458 */       this.m_detailedOutputBuff.append("\n");
/* 299:    */     }
/* 300:    */   }
/* 301:    */   
/* 302:    */   public String getRevision()
/* 303:    */   {
/* 304:469 */     return RevisionUtils.extract("$Revision: 10172 $");
/* 305:    */   }
/* 306:    */   
/* 307:    */   public static void main(String[] args)
/* 308:    */   {
/* 309:478 */     runEvaluator(new CorrelationAttributeEval(), args);
/* 310:    */   }
/* 311:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.attributeSelection.CorrelationAttributeEval
 * JD-Core Version:    0.7.0.1
 */