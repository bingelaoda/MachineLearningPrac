/*   1:    */ package weka.filters.unsupervised.timeseries;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Arrays;
/*   5:    */ import java.util.Collections;
/*   6:    */ import java.util.Enumeration;
/*   7:    */ import java.util.List;
/*   8:    */ import weka.core.Attribute;
/*   9:    */ import weka.core.Capabilities;
/*  10:    */ import weka.core.Capabilities.Capability;
/*  11:    */ import weka.core.DenseInstance;
/*  12:    */ import weka.core.Instance;
/*  13:    */ import weka.core.Instances;
/*  14:    */ import weka.core.Option;
/*  15:    */ import weka.core.Range;
/*  16:    */ import weka.core.RelationalLocator;
/*  17:    */ import weka.core.RevisionUtils;
/*  18:    */ import weka.core.SparseInstance;
/*  19:    */ import weka.core.StringLocator;
/*  20:    */ import weka.core.TechnicalInformation;
/*  21:    */ import weka.core.TechnicalInformation.Field;
/*  22:    */ import weka.core.TechnicalInformation.Type;
/*  23:    */ import weka.core.TechnicalInformationHandler;
/*  24:    */ import weka.core.Utils;
/*  25:    */ import weka.filters.SimpleStreamFilter;
/*  26:    */ import weka.filters.UnsupervisedFilter;
/*  27:    */ 
/*  28:    */ public class PAATransformer
/*  29:    */   extends SimpleStreamFilter
/*  30:    */   implements UnsupervisedFilter, TechnicalInformationHandler
/*  31:    */ {
/*  32:    */   private static final long serialVersionUID = 3384394202360169084L;
/*  33:    */   protected static final int DEFAULT_W = 100;
/*  34:    */   
/*  35:    */   protected static final Range getDefaultAttributes()
/*  36:    */   {
/*  37: 99 */     return new Range();
/*  38:    */   }
/*  39:    */   
/*  40:103 */   protected Range m_TimeSeriesAttributes = new Range(getDefaultAttributes().getRanges());
/*  41:107 */   protected int m_W = 100;
/*  42:110 */   private StringLocator m_StringAttributes = null;
/*  43:113 */   private RelationalLocator m_RelationalAttributes = null;
/*  44:    */   
/*  45:    */   public Capabilities getCapabilities()
/*  46:    */   {
/*  47:121 */     Capabilities result = super.getCapabilities();
/*  48:122 */     result.disableAll();
/*  49:    */     
/*  50:    */ 
/*  51:125 */     result.enableAllAttributes();
/*  52:126 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  53:    */     
/*  54:    */ 
/*  55:129 */     result.enableAllClasses();
/*  56:130 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  57:131 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  58:    */     
/*  59:    */ 
/*  60:    */ 
/*  61:    */ 
/*  62:136 */     return result;
/*  63:    */   }
/*  64:    */   
/*  65:    */   public Capabilities getMultiInstanceCapabilities()
/*  66:    */   {
/*  67:147 */     Capabilities result = new Capabilities(this);
/*  68:    */     
/*  69:    */ 
/*  70:150 */     result.disableAll();
/*  71:151 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  72:152 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  73:    */     
/*  74:154 */     return result;
/*  75:    */   }
/*  76:    */   
/*  77:    */   public Enumeration<Option> listOptions()
/*  78:    */   {
/*  79:161 */     List<Option> options = new ArrayList(2);
/*  80:    */     
/*  81:163 */     options.add(new Option("\tSpecifies the attributes that should be transformed.\n\tThe attributes must be relational attributes and must contain only\n\tnumeric attributes which are each transformed separately.\n\tFirst and last are valid indices. (default " + getDefaultAttributes() + ")", "R", 1, "-R <index1,index2-index3,...>"));
/*  82:    */     
/*  83:    */ 
/*  84:    */ 
/*  85:    */ 
/*  86:    */ 
/*  87:    */ 
/*  88:    */ 
/*  89:171 */     options.add(new Option("\tInverts the specified attribute range (default don't invert)", "V", 0, "-V"));
/*  90:    */     
/*  91:    */ 
/*  92:    */ 
/*  93:175 */     options.add(new Option("\tSpecifies the compression factor w for the PAA transformation.\n\tA time series of length n will be compressed to one of length w.\n\t(default 100).", "W", 1, "-W <CompressionFactor>"));
/*  94:    */     
/*  95:    */ 
/*  96:    */ 
/*  97:    */ 
/*  98:    */ 
/*  99:181 */     return Collections.enumeration(options);
/* 100:    */   }
/* 101:    */   
/* 102:    */   public String[] getOptions()
/* 103:    */   {
/* 104:190 */     List<String> options = new ArrayList();
/* 105:192 */     if (this.m_W != 100)
/* 106:    */     {
/* 107:193 */       options.add("-W");
/* 108:194 */       options.add("" + this.m_W);
/* 109:    */     }
/* 110:197 */     if (!this.m_TimeSeriesAttributes.equals(getDefaultAttributes()))
/* 111:    */     {
/* 112:198 */       options.add("-R");
/* 113:199 */       options.add(this.m_TimeSeriesAttributes.getRanges());
/* 114:    */     }
/* 115:202 */     if (this.m_TimeSeriesAttributes.getInvert()) {
/* 116:203 */       options.add("-V");
/* 117:    */     }
/* 118:206 */     return (String[])options.toArray(new String[0]);
/* 119:    */   }
/* 120:    */   
/* 121:    */   public void setOptions(String[] options)
/* 122:    */     throws Exception
/* 123:    */   {
/* 124:237 */     String w = Utils.getOption('W', options);
/* 125:238 */     if (w.length() != 0) {
/* 126:239 */       this.m_W = Integer.parseInt(w);
/* 127:    */     }
/* 128:241 */     if (this.m_W <= 0) {
/* 129:242 */       throw new Exception("Parameter W must be bigger than 0!");
/* 130:    */     }
/* 131:244 */     String timeSeriesAttributes = Utils.getOption('R', options);
/* 132:245 */     if (timeSeriesAttributes.length() != 0) {
/* 133:246 */       this.m_TimeSeriesAttributes = new Range(timeSeriesAttributes);
/* 134:    */     }
/* 135:248 */     if (Utils.getFlag('V', options)) {
/* 136:249 */       this.m_TimeSeriesAttributes.setInvert(true);
/* 137:    */     }
/* 138:251 */     if (getInputFormat() != null) {
/* 139:252 */       setInputFormat(getInputFormat());
/* 140:    */     }
/* 141:254 */     Utils.checkForRemainingOptions(options);
/* 142:    */   }
/* 143:    */   
/* 144:    */   public int getW()
/* 145:    */   {
/* 146:263 */     return this.m_W;
/* 147:    */   }
/* 148:    */   
/* 149:    */   public void setW(int W)
/* 150:    */   {
/* 151:272 */     this.m_W = W;
/* 152:    */   }
/* 153:    */   
/* 154:    */   public String wTipText()
/* 155:    */   {
/* 156:281 */     return "The compression factor w";
/* 157:    */   }
/* 158:    */   
/* 159:    */   public String getRange()
/* 160:    */   {
/* 161:290 */     return this.m_TimeSeriesAttributes.getRanges();
/* 162:    */   }
/* 163:    */   
/* 164:    */   public void setRange(String range)
/* 165:    */   {
/* 166:299 */     this.m_TimeSeriesAttributes.setRanges(range);
/* 167:    */   }
/* 168:    */   
/* 169:    */   public String rangeTipText()
/* 170:    */   {
/* 171:308 */     return "The attribute ranges to which the filter should be applied to";
/* 172:    */   }
/* 173:    */   
/* 174:    */   public boolean getInvertRange()
/* 175:    */   {
/* 176:317 */     return this.m_TimeSeriesAttributes.getInvert();
/* 177:    */   }
/* 178:    */   
/* 179:    */   public void setInvertRange(boolean inversion)
/* 180:    */   {
/* 181:326 */     this.m_TimeSeriesAttributes.setInvert(inversion);
/* 182:    */   }
/* 183:    */   
/* 184:    */   public String invertRangeTipText()
/* 185:    */   {
/* 186:335 */     return "Whether the specified attribute range should be inverted";
/* 187:    */   }
/* 188:    */   
/* 189:    */   public String globalInfo()
/* 190:    */   {
/* 191:343 */     return "A filter to perform the Piecewise Aggregate Approximation transformation to time series.\n\nIt uses the \"N/n not equal an integer\" generalization. Furthermore, it's extended to handle weights for data points in time series.\n\nWarning: The lower bounding property may not hold for the distance measure when weights are used or N/n isn't equal to an integer!\n\nFor more information see:\n" + getTechnicalInformation().toString();
/* 192:    */   }
/* 193:    */   
/* 194:    */   public String getRevision()
/* 195:    */   {
/* 196:360 */     return RevisionUtils.extract("$Revision: 1000 $");
/* 197:    */   }
/* 198:    */   
/* 199:    */   public TechnicalInformation getTechnicalInformation()
/* 200:    */   {
/* 201:366 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/* 202:367 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Byoung-Kee Yi and Christos Faloutsos");
/* 203:368 */     result.setValue(TechnicalInformation.Field.TITLE, "Fast Time Sequence Indexing for Arbitrary L_p Norms");
/* 204:369 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "Proceedings of the 26th VLDB Conference");
/* 205:370 */     result.setValue(TechnicalInformation.Field.YEAR, "2000");
/* 206:371 */     result.setValue(TechnicalInformation.Field.PAGES, "385-394");
/* 207:372 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "Morgan Kaufmann Publishers");
/* 208:    */     
/* 209:374 */     TechnicalInformation additional = result.add(TechnicalInformation.Type.ARTICLE);
/* 210:375 */     additional.setValue(TechnicalInformation.Field.AUTHOR, "Eamonn Keogh and Kaushik Chakrabarti and Michael Pazzani and Sharad Mehrotra");
/* 211:    */     
/* 212:377 */     additional.setValue(TechnicalInformation.Field.TITLE, "Dimensionality Reduction for Fast Similarity Search in Large Time Series Databases");
/* 213:    */     
/* 214:379 */     additional.setValue(TechnicalInformation.Field.JOURNAL, "Knowledge and information Systems");
/* 215:380 */     additional.setValue(TechnicalInformation.Field.YEAR, "2001");
/* 216:381 */     additional.setValue(TechnicalInformation.Field.PAGES, "263-286");
/* 217:382 */     additional.setValue(TechnicalInformation.Field.NUMBER, "3");
/* 218:383 */     additional.setValue(TechnicalInformation.Field.VOLUME, "3");
/* 219:    */     
/* 220:385 */     TechnicalInformation nNnonEqual = result.add(TechnicalInformation.Type.MISC);
/* 221:386 */     nNnonEqual.setValue(TechnicalInformation.Field.AUTHOR, "Li Wei");
/* 222:387 */     nNnonEqual.setValue(TechnicalInformation.Field.TITLE, "Code for \"N/n not equal an integer'");
/* 223:388 */     nNnonEqual.setValue(TechnicalInformation.Field.URL, "http://www.cs.ucr.edu/~eamonn/SAX_2006_ver.zip");
/* 224:    */     
/* 225:390 */     return result;
/* 226:    */   }
/* 227:    */   
/* 228:    */   protected boolean hasImmediateOutputFormat()
/* 229:    */   {
/* 230:402 */     return true;
/* 231:    */   }
/* 232:    */   
/* 233:    */   protected Instances determineOutputFormat(Instances inputFormat)
/* 234:    */     throws Exception
/* 235:    */   {
/* 236:412 */     this.m_TimeSeriesAttributes.setUpper(inputFormat.numAttributes() - 1);
/* 237:414 */     for (int index : this.m_TimeSeriesAttributes.getSelection())
/* 238:    */     {
/* 239:415 */       Attribute attribute = getInputFormat().attribute(index);
/* 240:418 */       if (!attribute.isRelationValued()) {
/* 241:419 */         throw new Exception(String.format("Attribute '%s' isn't relational!", new Object[] { attribute.name() }));
/* 242:    */       }
/* 243:425 */       Instances timeSeries = attribute.relation();
/* 244:426 */       for (int i = 0; i < timeSeries.numAttributes(); i++) {
/* 245:427 */         if (!timeSeries.attribute(i).isNumeric()) {
/* 246:428 */           throw new Exception(String.format("Attribute '%s' inside relational attribute '%s' isn't numeric!", new Object[] { timeSeries.attribute(i).name(), attribute.name() }));
/* 247:    */         }
/* 248:    */       }
/* 249:    */     }
/* 250:436 */     this.m_TimeSeriesAttributes.setInvert(!this.m_TimeSeriesAttributes.getInvert());
/* 251:437 */     this.m_StringAttributes = new StringLocator(inputFormat, this.m_TimeSeriesAttributes.getSelection());
/* 252:    */     
/* 253:439 */     this.m_RelationalAttributes = new RelationalLocator(inputFormat, this.m_TimeSeriesAttributes.getSelection());
/* 254:    */     
/* 255:441 */     this.m_TimeSeriesAttributes.setInvert(!this.m_TimeSeriesAttributes.getInvert());
/* 256:    */     
/* 257:443 */     return new Instances(inputFormat, 0);
/* 258:    */   }
/* 259:    */   
/* 260:    */   protected Instance process(Instance inputInstance)
/* 261:    */     throws Exception
/* 262:    */   {
/* 263:457 */     assert (inputInstance.dataset().equalHeaders(getInputFormat()));
/* 264:    */     
/* 265:459 */     StringLocator.copyStringValues(inputInstance, getOutputFormat(), this.m_StringAttributes);
/* 266:    */     
/* 267:461 */     RelationalLocator.copyRelationalValues(inputInstance, getOutputFormat(), this.m_RelationalAttributes);
/* 268:    */     
/* 269:    */ 
/* 270:464 */     Instance outputInstance = null;
/* 271:465 */     if ((inputInstance instanceof DenseInstance)) {
/* 272:466 */       outputInstance = new DenseInstance(inputInstance.weight(), Arrays.copyOf(inputInstance.toDoubleArray(), inputInstance.numAttributes()));
/* 273:468 */     } else if ((inputInstance instanceof SparseInstance)) {
/* 274:469 */       outputInstance = new SparseInstance(inputInstance.weight(), Arrays.copyOf(inputInstance.toDoubleArray(), inputInstance.numAttributes()));
/* 275:    */     } else {
/* 276:472 */       throw new Exception("Input instance is neither sparse nor dense!");
/* 277:    */     }
/* 278:475 */     outputInstance.setDataset(getOutputFormat());
/* 279:477 */     for (int index : this.m_TimeSeriesAttributes.getSelection()) {
/* 280:479 */       if (!inputInstance.isMissing(index))
/* 281:    */       {
/* 282:482 */         Instances timeSeries = inputInstance.relationalValue(inputInstance.attribute(index));
/* 283:    */         
/* 284:    */ 
/* 285:485 */         Instances transformed = transform(timeSeries);
/* 286:    */         
/* 287:487 */         int reference = getOutputFormat().attribute(index).addRelation(transformed);
/* 288:    */         
/* 289:489 */         outputInstance.setValue(index, reference);
/* 290:    */       }
/* 291:    */     }
/* 292:493 */     return outputInstance;
/* 293:    */   }
/* 294:    */   
/* 295:    */   protected Instances transform(Instances input)
/* 296:    */     throws Exception
/* 297:    */   {
/* 298:564 */     Instances output = new Instances(input, this.m_W);
/* 299:    */     
/* 300:566 */     assert (this.m_W >= 1);
/* 301:    */     
/* 302:568 */     double segmentSize = input.sumOfWeights() / this.m_W;
/* 303:569 */     int segmentIndex = 1;
/* 304:570 */     double segmentLowerBoundary = 0.0D;
/* 305:571 */     double segmentUpperBoundary = segmentSize;
/* 306:    */     
/* 307:573 */     double instanceLowerBoundary = 0.0D;
/* 308:574 */     double instanceUpperBoundary = 0.0D;
/* 309:    */     
/* 310:576 */     double[] sumValues = new double[input.numAttributes()];
/* 311:578 */     for (int i = 0; i < input.numInstances(); i++)
/* 312:    */     {
/* 313:581 */       assert (instanceLowerBoundary < segmentUpperBoundary);
/* 314:582 */       assert (segmentLowerBoundary <= instanceLowerBoundary);
/* 315:    */       
/* 316:    */ 
/* 317:585 */       instanceUpperBoundary = instanceLowerBoundary + input.get(i).weight();
/* 318:587 */       if ((instanceUpperBoundary >= segmentUpperBoundary) && (segmentUpperBoundary - instanceLowerBoundary < segmentSize))
/* 319:    */       {
/* 320:591 */         double weight = segmentUpperBoundary - instanceLowerBoundary;
/* 321:592 */         for (int att = 0; att < input.numAttributes(); att++)
/* 322:    */         {
/* 323:593 */           sumValues[att] += input.get(i).value(att) * weight;
/* 324:594 */           sumValues[att] /= segmentSize;
/* 325:    */         }
/* 326:597 */         output.add(new DenseInstance(segmentSize, sumValues));
/* 327:598 */         segmentLowerBoundary = segmentUpperBoundary;
/* 328:599 */         segmentIndex++;segmentUpperBoundary = segmentIndex * segmentSize;
/* 329:600 */         sumValues = new double[input.numAttributes()];
/* 330:    */       }
/* 331:604 */       while (instanceUpperBoundary >= segmentUpperBoundary)
/* 332:    */       {
/* 333:607 */         output.add(new DenseInstance(segmentSize, Arrays.copyOf(input.get(i).toDoubleArray(), input.numAttributes())));
/* 334:    */         
/* 335:609 */         segmentLowerBoundary = segmentUpperBoundary;
/* 336:610 */         segmentIndex++;segmentUpperBoundary = segmentIndex * segmentSize;
/* 337:611 */         sumValues = new double[input.numAttributes()];
/* 338:    */       }
/* 339:615 */       assert (instanceUpperBoundary < segmentUpperBoundary);
/* 340:    */       
/* 341:    */ 
/* 342:618 */       double weight = Math.min(input.get(i).weight(), instanceUpperBoundary - segmentLowerBoundary);
/* 343:623 */       for (int att = 0; att < input.numAttributes(); att++) {
/* 344:624 */         sumValues[att] += input.get(i).value(att) * weight;
/* 345:    */       }
/* 346:626 */       instanceLowerBoundary = instanceUpperBoundary;
/* 347:    */     }
/* 348:630 */     return output;
/* 349:    */   }
/* 350:    */   
/* 351:    */   public static void main(String[] args)
/* 352:    */   {
/* 353:639 */     runFilter(new PAATransformer(), args);
/* 354:    */   }
/* 355:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.timeseries.PAATransformer
 * JD-Core Version:    0.7.0.1
 */