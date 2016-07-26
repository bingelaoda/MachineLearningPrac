/*   1:    */ package weka.filters.supervised.attribute;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.HashMap;
/*   5:    */ import java.util.List;
/*   6:    */ import java.util.Map;
/*   7:    */ import weka.classifiers.bayes.NaiveBayes;
/*   8:    */ import weka.core.Attribute;
/*   9:    */ import weka.core.Capabilities;
/*  10:    */ import weka.core.DenseInstance;
/*  11:    */ import weka.core.Instance;
/*  12:    */ import weka.core.Instances;
/*  13:    */ import weka.core.OptionMetadata;
/*  14:    */ import weka.core.RevisionUtils;
/*  15:    */ import weka.core.Utils;
/*  16:    */ import weka.estimators.Estimator;
/*  17:    */ import weka.filters.Filter;
/*  18:    */ import weka.filters.SimpleBatchFilter;
/*  19:    */ import weka.filters.unsupervised.attribute.Remove;
/*  20:    */ import weka.gui.ProgrammaticProperty;
/*  21:    */ 
/*  22:    */ public class ClassConditionalProbabilities
/*  23:    */   extends SimpleBatchFilter
/*  24:    */ {
/*  25:    */   private static final long serialVersionUID = 1684310720200284263L;
/*  26:    */   protected boolean m_excludeNumericAttributes;
/*  27:    */   protected boolean m_excludeNominalAttributes;
/*  28: 93 */   protected int m_nominalConversionThreshold = -1;
/*  29:    */   protected NaiveBayes m_estimator;
/*  30:    */   protected Remove m_remove;
/*  31:    */   protected Instances m_unchanged;
/*  32:    */   protected Map<String, Estimator[]> m_estimatorLookup;
/*  33:    */   
/*  34:    */   public static void main(String[] args)
/*  35:    */   {
/*  36:116 */     runFilter(new ClassConditionalProbabilities(), args);
/*  37:    */   }
/*  38:    */   
/*  39:    */   public String globalInfo()
/*  40:    */   {
/*  41:126 */     return "Converts the values of nominal and/or numeric attributes into class conditional probabilities. If there are k classes, then k new attributes are created for each of the original ones, giving pr(att val | class k).\n\nCan be useful for converting nominal attributes with a lot of distinct values into something more manageable for learning schemes that can't handle nominal attributes (as opposed to creating binary indicator attributes). For nominal attributes, the user can specify the number values above which an attribute will be converted by this method.";
/*  42:    */   }
/*  43:    */   
/*  44:    */   @OptionMetadata(displayName="Exclude numeric attributes", description="Don't apply this transformation to numeric attributes", commandLineParamName="N", commandLineParamIsFlag=true, commandLineParamSynopsis="-N", displayOrder=1)
/*  45:    */   public boolean getExcludeNumericAttributes()
/*  46:    */   {
/*  47:147 */     return this.m_excludeNumericAttributes;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public void setExcludeNumericAttributes(boolean e)
/*  51:    */   {
/*  52:156 */     this.m_excludeNumericAttributes = e;
/*  53:    */   }
/*  54:    */   
/*  55:    */   @OptionMetadata(displayName="Exclude nominal attributes", description="Don't apply this transformation to nominal attributes", commandLineParamName="C", commandLineParamIsFlag=true, commandLineParamSynopsis="-C", displayOrder=2)
/*  56:    */   public boolean getExcludeNominalAttributes()
/*  57:    */   {
/*  58:169 */     return this.m_excludeNominalAttributes;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public void setExcludeNominalAttributes(boolean e)
/*  62:    */   {
/*  63:178 */     this.m_excludeNominalAttributes = e;
/*  64:    */   }
/*  65:    */   
/*  66:    */   @OptionMetadata(displayName="Nominal conversion threshold", description="Transform nominal attributes with at least this many values.\n-1 means always transform.", commandLineParamName="min-values", commandLineParamSynopsis="-min-values <integer>", displayOrder=3)
/*  67:    */   public int getNominalConversionThreshold()
/*  68:    */   {
/*  69:195 */     return this.m_nominalConversionThreshold;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public void setNominalConversionThreshold(int n)
/*  73:    */   {
/*  74:207 */     this.m_nominalConversionThreshold = n;
/*  75:    */   }
/*  76:    */   
/*  77:    */   protected Instances determineOutputFormat(Instances inputFormat)
/*  78:    */     throws Exception
/*  79:    */   {
/*  80:214 */     if ((this.m_excludeNominalAttributes) && (this.m_excludeNumericAttributes)) {
/*  81:215 */       throw new Exception("No transformation will be done if both nominal and numeric attributes are excluded!");
/*  82:    */     }
/*  83:219 */     if (this.m_remove == null)
/*  84:    */     {
/*  85:220 */       List<Integer> attsToExclude = new ArrayList();
/*  86:221 */       if (this.m_excludeNumericAttributes) {
/*  87:222 */         for (int i = 0; i < inputFormat.numAttributes(); i++) {
/*  88:223 */           if ((inputFormat.attribute(i).isNumeric()) && (i != inputFormat.classIndex())) {
/*  89:225 */             attsToExclude.add(Integer.valueOf(i));
/*  90:    */           }
/*  91:    */         }
/*  92:    */       }
/*  93:230 */       if ((this.m_excludeNominalAttributes) || (this.m_nominalConversionThreshold > 1)) {
/*  94:231 */         for (int i = 0; i < inputFormat.numAttributes(); i++) {
/*  95:232 */           if ((inputFormat.attribute(i).isNominal()) && (i != inputFormat.classIndex())) {
/*  96:234 */             if ((this.m_excludeNominalAttributes) || (inputFormat.attribute(i).numValues() < this.m_nominalConversionThreshold)) {
/*  97:236 */               attsToExclude.add(Integer.valueOf(i));
/*  98:    */             }
/*  99:    */           }
/* 100:    */         }
/* 101:    */       }
/* 102:242 */       if (attsToExclude.size() > 0)
/* 103:    */       {
/* 104:243 */         int[] r = new int[attsToExclude.size()];
/* 105:244 */         for (int i = 0; i < attsToExclude.size(); i++) {
/* 106:245 */           r[i] = ((Integer)attsToExclude.get(i)).intValue();
/* 107:    */         }
/* 108:247 */         this.m_remove = new Remove();
/* 109:248 */         this.m_remove.setAttributeIndicesArray(r);
/* 110:249 */         this.m_remove.setInputFormat(inputFormat);
/* 111:    */         
/* 112:251 */         Remove forRetaining = new Remove();
/* 113:252 */         forRetaining.setAttributeIndicesArray(r);
/* 114:253 */         forRetaining.setInvertSelection(true);
/* 115:254 */         forRetaining.setInputFormat(inputFormat);
/* 116:255 */         this.m_unchanged = Filter.useFilter(inputFormat, forRetaining);
/* 117:    */       }
/* 118:    */     }
/* 119:259 */     ArrayList<Attribute> atts = new ArrayList();
/* 120:260 */     for (int i = 0; i < inputFormat.numAttributes(); i++) {
/* 121:261 */       if (i != inputFormat.classIndex()) {
/* 122:262 */         if ((this.m_unchanged != null) && (this.m_unchanged.attribute(inputFormat.attribute(i).name()) != null)) {
/* 123:264 */           atts.add((Attribute)this.m_unchanged.attribute(inputFormat.attribute(i).name()).copy());
/* 124:    */         } else {
/* 125:269 */           for (int j = 0; j < inputFormat.classAttribute().numValues(); j++)
/* 126:    */           {
/* 127:270 */             String name = "pr_" + inputFormat.attribute(i).name() + "|" + inputFormat.classAttribute().value(j);
/* 128:    */             
/* 129:    */ 
/* 130:273 */             atts.add(new Attribute(name));
/* 131:    */           }
/* 132:    */         }
/* 133:    */       }
/* 134:    */     }
/* 135:278 */     atts.add((Attribute)inputFormat.classAttribute().copy());
/* 136:279 */     Instances data = new Instances(inputFormat.relationName(), atts, 0);
/* 137:280 */     data.setClassIndex(data.numAttributes() - 1);
/* 138:    */     
/* 139:282 */     return data;
/* 140:    */   }
/* 141:    */   
/* 142:    */   protected Instances process(Instances instances)
/* 143:    */     throws Exception
/* 144:    */   {
/* 145:287 */     if (this.m_estimator == null)
/* 146:    */     {
/* 147:288 */       this.m_estimator = new NaiveBayes();
/* 148:    */       
/* 149:290 */       Instances trainingData = new Instances(instances);
/* 150:291 */       if (this.m_remove != null) {
/* 151:292 */         trainingData = Filter.useFilter(instances, this.m_remove);
/* 152:    */       }
/* 153:294 */       this.m_estimator.buildClassifier(trainingData);
/* 154:    */     }
/* 155:297 */     if (this.m_estimatorLookup == null)
/* 156:    */     {
/* 157:298 */       this.m_estimatorLookup = new HashMap();
/* 158:299 */       Estimator[][] estimators = this.m_estimator.getConditionalEstimators();
/* 159:300 */       Instances header = this.m_estimator.getHeader();
/* 160:301 */       int index = 0;
/* 161:302 */       for (int i = 0; i < header.numAttributes(); i++) {
/* 162:303 */         if (i != header.classIndex())
/* 163:    */         {
/* 164:304 */           this.m_estimatorLookup.put(header.attribute(i).name(), estimators[index]);
/* 165:305 */           index++;
/* 166:    */         }
/* 167:    */       }
/* 168:    */     }
/* 169:310 */     Instances result = new Instances(getOutputFormat(), instances.numInstances());
/* 170:312 */     for (int i = 0; i < instances.numInstances(); i++)
/* 171:    */     {
/* 172:314 */       Instance current = instances.instance(i);
/* 173:315 */       Instance instNew = convertInstance(current);
/* 174:    */       
/* 175:    */ 
/* 176:318 */       result.add(instNew);
/* 177:    */     }
/* 178:321 */     return result;
/* 179:    */   }
/* 180:    */   
/* 181:    */   protected Instance convertInstance(Instance current)
/* 182:    */     throws Exception
/* 183:    */   {
/* 184:332 */     double[] vals = new double[getOutputFormat().numAttributes()];
/* 185:333 */     int index = 0;
/* 186:334 */     for (int j = 0; j < current.numAttributes(); j++) {
/* 187:335 */       if (j != current.classIndex()) {
/* 188:336 */         if ((this.m_unchanged != null) && (this.m_unchanged.attribute(current.attribute(j).name()) != null))
/* 189:    */         {
/* 190:338 */           vals[(index++)] = current.value(j);
/* 191:    */         }
/* 192:    */         else
/* 193:    */         {
/* 194:340 */           Estimator[] estForAtt = (Estimator[])this.m_estimatorLookup.get(current.attribute(j).name());
/* 195:342 */           for (int k = 0; k < current.classAttribute().numValues(); k++) {
/* 196:343 */             if (current.isMissing(j))
/* 197:    */             {
/* 198:344 */               vals[(index++)] = Utils.missingValue();
/* 199:    */             }
/* 200:    */             else
/* 201:    */             {
/* 202:346 */               double e = estForAtt[k].getProbability(current.value(j));
/* 203:347 */               vals[(index++)] = e;
/* 204:    */             }
/* 205:    */           }
/* 206:    */         }
/* 207:    */       }
/* 208:    */     }
/* 209:354 */     vals[(vals.length - 1)] = current.classValue();
/* 210:355 */     DenseInstance instNew = new DenseInstance(current.weight(), vals);
/* 211:    */     
/* 212:357 */     return instNew;
/* 213:    */   }
/* 214:    */   
/* 215:    */   public boolean input(Instance inst)
/* 216:    */     throws Exception
/* 217:    */   {
/* 218:361 */     if (!isFirstBatchDone()) {
/* 219:362 */       return super.input(inst);
/* 220:    */     }
/* 221:365 */     Instance converted = convertInstance(inst);
/* 222:366 */     push(converted);
/* 223:    */     
/* 224:368 */     return true;
/* 225:    */   }
/* 226:    */   
/* 227:    */   public Capabilities getCapabilities()
/* 228:    */   {
/* 229:379 */     return new NaiveBayes().getCapabilities();
/* 230:    */   }
/* 231:    */   
/* 232:    */   public String getRevision()
/* 233:    */   {
/* 234:389 */     return RevisionUtils.extract("$Revision: $");
/* 235:    */   }
/* 236:    */   
/* 237:    */   @ProgrammaticProperty
/* 238:    */   public NaiveBayes getEstimator()
/* 239:    */   {
/* 240:399 */     return this.m_estimator;
/* 241:    */   }
/* 242:    */   
/* 243:    */   public void setEstimator(NaiveBayes nb)
/* 244:    */   {
/* 245:408 */     this.m_estimator = nb;
/* 246:    */   }
/* 247:    */   
/* 248:    */   @ProgrammaticProperty
/* 249:    */   public Remove getRemoveFilter()
/* 250:    */   {
/* 251:418 */     return this.m_remove;
/* 252:    */   }
/* 253:    */   
/* 254:    */   public void setRemoveFilter(Remove r)
/* 255:    */   {
/* 256:422 */     this.m_remove = r;
/* 257:423 */     this.m_unchanged = r.getOutputFormat();
/* 258:    */   }
/* 259:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.supervised.attribute.ClassConditionalProbabilities
 * JD-Core Version:    0.7.0.1
 */