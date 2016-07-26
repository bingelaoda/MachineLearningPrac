/*   1:    */ package weka.filters.supervised.attribute;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.classifiers.functions.NonNegativeLogisticRegression;
/*   8:    */ import weka.core.Attribute;
/*   9:    */ import weka.core.Capabilities;
/*  10:    */ import weka.core.Capabilities.Capability;
/*  11:    */ import weka.core.DenseInstance;
/*  12:    */ import weka.core.DistanceFunction;
/*  13:    */ import weka.core.EuclideanDistance;
/*  14:    */ import weka.core.Instance;
/*  15:    */ import weka.core.Instances;
/*  16:    */ import weka.core.ManhattanDistance;
/*  17:    */ import weka.core.Option;
/*  18:    */ import weka.core.RevisionUtils;
/*  19:    */ import weka.core.TechnicalInformation;
/*  20:    */ import weka.core.TechnicalInformation.Field;
/*  21:    */ import weka.core.TechnicalInformation.Type;
/*  22:    */ import weka.core.TechnicalInformationHandler;
/*  23:    */ import weka.core.Utils;
/*  24:    */ import weka.core.neighboursearch.LinearNNSearch;
/*  25:    */ import weka.core.neighboursearch.NearestNeighbourSearch;
/*  26:    */ import weka.filters.SimpleBatchFilter;
/*  27:    */ import weka.filters.SupervisedFilter;
/*  28:    */ 
/*  29:    */ public class SupervisedAttributeScaler
/*  30:    */   extends SimpleBatchFilter
/*  31:    */   implements SupervisedFilter, TechnicalInformationHandler
/*  32:    */ {
/*  33:    */   static final long serialVersionUID = -4448107323933117974L;
/*  34:131 */   protected double[] m_weights = null;
/*  35:134 */   protected boolean m_AssumeEuclideanDistance = false;
/*  36:137 */   protected int m_numNeighbours = 30;
/*  37:    */   
/*  38:    */   public String globalInfo()
/*  39:    */   {
/*  40:147 */     return "Rescales the attributes in a classification problem based on their discriminative power. This is useful as a pre-processing step for learning algorithms such as the k-nearest-neighbour method, toreplace simple normalization. Each attribute is rescaled by multiplying it with a learned weight.All attributes excluding the class are assumed to be numeric and missing values are not permitted.\nThe attribute weights are learned by taking the original labeled dataset with N instances and creating a new dataset with N*K instances, where K is the number of neighbours selected. Each instance in the original dataset is paired with its K nearest neighbours, creating K pairs, and an instance in the new dataset is created for each pair, with the same number of attributes as in the original data. For all attributes excluding the class, in each new instance, the attribute's value is set to the absolute difference between the corresponding attribute values in the pair of original instances. The new instance is then labeled based on whether the two instances in the pair have the same class label in the original data or a different one, yielding a two-class classification problem. A logistic regression model with non-negative coefficients is learned from this data and the resulting coefficients are used as weights to rescale the original data.\nThis process assumes that distance in the original space is measured using Manhattan distance because the absolute difference is taken between attribute values. The method can optionally be used to learn weights for a Euclidean distance. In this case, squared differences are taken rather than absolute differences, and the square root of the learned coefficients is used to rescale the attributes in the original data.\n\nThe approach is based on the Probabilistic Global Distance Metric Learning method included in the experimental comparison in\n\n" + getTechnicalInformation().toString();
/*  41:    */   }
/*  42:    */   
/*  43:    */   public TechnicalInformation getTechnicalInformation()
/*  44:    */   {
/*  45:182 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*  46:183 */     result.setValue(TechnicalInformation.Field.AUTHOR, "L. Yang and R. Jin and R. Sukthankar and Y. Liu");
/*  47:    */     
/*  48:185 */     result.setValue(TechnicalInformation.Field.TITLE, "An efficient algorithm for local distance metric learning");
/*  49:    */     
/*  50:187 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "Proceedings of the National Conference on Artificial Intelligence");
/*  51:    */     
/*  52:189 */     result.setValue(TechnicalInformation.Field.YEAR, "2006");
/*  53:190 */     result.setValue(TechnicalInformation.Field.PAGES, "543-548");
/*  54:191 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "AAAI Press");
/*  55:    */     
/*  56:193 */     return result;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public Enumeration<Option> listOptions()
/*  60:    */   {
/*  61:204 */     Vector<Option> result = new Vector();
/*  62:    */     
/*  63:206 */     result.addElement(new Option("\tIf set, weights are learned for Euclidean distance.\n", "-assume-Euclidean-distance", 0, "-assume-Euclidean-distance"));
/*  64:    */     
/*  65:    */ 
/*  66:    */ 
/*  67:210 */     result.addElement(new Option("\tThe number of neighbours to use (default: 30).\n", "-K", 1, "-K"));
/*  68:    */     
/*  69:    */ 
/*  70:213 */     result.addAll(Collections.list(super.listOptions()));
/*  71:    */     
/*  72:215 */     return result.elements();
/*  73:    */   }
/*  74:    */   
/*  75:    */   public String[] getOptions()
/*  76:    */   {
/*  77:226 */     Vector<String> result = new Vector();
/*  78:228 */     if (getAssumeEuclideanDistance()) {
/*  79:229 */       result.add("-assume-Euclidean-distance");
/*  80:    */     }
/*  81:232 */     result.add("-K");
/*  82:233 */     result.add("" + getNumNeighbours());
/*  83:    */     
/*  84:235 */     Collections.addAll(result, super.getOptions());
/*  85:    */     
/*  86:237 */     return (String[])result.toArray(new String[result.size()]);
/*  87:    */   }
/*  88:    */   
/*  89:    */   public void setOptions(String[] options)
/*  90:    */     throws Exception
/*  91:    */   {
/*  92:265 */     setAssumeEuclideanDistance(Utils.getFlag("assume-Euclidean-distance", options));
/*  93:    */     
/*  94:    */ 
/*  95:268 */     String knnString = Utils.getOption('K', options);
/*  96:269 */     if (knnString.length() != 0) {
/*  97:270 */       setNumNeighbours(Integer.parseInt(knnString));
/*  98:    */     } else {
/*  99:272 */       setNumNeighbours(30);
/* 100:    */     }
/* 101:275 */     super.setOptions(options);
/* 102:    */     
/* 103:277 */     Utils.checkForRemainingOptions(options);
/* 104:    */   }
/* 105:    */   
/* 106:    */   public String assumeEuclideanDistanceTipText()
/* 107:    */   {
/* 108:287 */     return "Whether to assume Euclidean distance rather than Manhattan distance.";
/* 109:    */   }
/* 110:    */   
/* 111:    */   public boolean getAssumeEuclideanDistance()
/* 112:    */   {
/* 113:296 */     return this.m_AssumeEuclideanDistance;
/* 114:    */   }
/* 115:    */   
/* 116:    */   public void setAssumeEuclideanDistance(boolean value)
/* 117:    */   {
/* 118:305 */     this.m_AssumeEuclideanDistance = value;
/* 119:    */   }
/* 120:    */   
/* 121:    */   public String numNeighboursTipText()
/* 122:    */   {
/* 123:315 */     return "The number of neighbours to use.";
/* 124:    */   }
/* 125:    */   
/* 126:    */   public int getNumNeighbours()
/* 127:    */   {
/* 128:324 */     return this.m_numNeighbours;
/* 129:    */   }
/* 130:    */   
/* 131:    */   public void setNumNeighbours(int value)
/* 132:    */   {
/* 133:333 */     this.m_numNeighbours = value;
/* 134:    */   }
/* 135:    */   
/* 136:    */   protected Instances determineOutputFormat(Instances inputFormat)
/* 137:    */   {
/* 138:344 */     return new Instances(inputFormat);
/* 139:    */   }
/* 140:    */   
/* 141:    */   public void initFilter(Instances instances)
/* 142:    */     throws Exception
/* 143:    */   {
/* 144:356 */     int N = instances.numInstances();
/* 145:357 */     int M = instances.numAttributes();
/* 146:    */     
/* 147:    */ 
/* 148:360 */     this.m_weights = new double[M];
/* 149:    */     
/* 150:    */ 
/* 151:363 */     ArrayList<Attribute> atts = new ArrayList(M + 1);
/* 152:364 */     atts.add(new Attribute("-1"));
/* 153:365 */     for (int i = 0; i < M; i++) {
/* 154:366 */       if (i == instances.classIndex())
/* 155:    */       {
/* 156:367 */         ArrayList<String> classValues = new ArrayList(2);
/* 157:368 */         classValues.add("different_class_values");
/* 158:369 */         classValues.add("same_class_values");
/* 159:370 */         atts.add(new Attribute("Class", classValues));
/* 160:    */       }
/* 161:    */       else
/* 162:    */       {
/* 163:372 */         atts.add((Attribute)instances.attribute(i).copy());
/* 164:    */       }
/* 165:    */     }
/* 166:377 */     DistanceFunction ed = null;
/* 167:378 */     if (getAssumeEuclideanDistance())
/* 168:    */     {
/* 169:379 */       EuclideanDistance d = new EuclideanDistance();
/* 170:380 */       d.setDontNormalize(true);
/* 171:381 */       ed = d;
/* 172:    */     }
/* 173:    */     else
/* 174:    */     {
/* 175:383 */       ManhattanDistance d = new ManhattanDistance();
/* 176:384 */       d.setDontNormalize(true);
/* 177:385 */       ed = d;
/* 178:    */     }
/* 179:387 */     NearestNeighbourSearch nnSearch = new LinearNNSearch();
/* 180:388 */     nnSearch.setDistanceFunction(ed);
/* 181:389 */     nnSearch.setInstances(instances);
/* 182:    */     
/* 183:    */ 
/* 184:392 */     int numNeighbours = this.m_numNeighbours;
/* 185:393 */     if (this.m_numNeighbours >= instances.numInstances()) {
/* 186:394 */       numNeighbours = instances.numInstances() - 1;
/* 187:    */     }
/* 188:396 */     Instances pairwiseData = new Instances("pairwise_data", atts, N * numNeighbours);
/* 189:398 */     for (int i = 0; i < N; i++)
/* 190:    */     {
/* 191:399 */       Instance inst1 = instances.instance(i);
/* 192:400 */       nnSearch.addInstanceInfo(inst1);
/* 193:401 */       Instances neighbours = nnSearch.kNearestNeighbours(inst1, numNeighbours);
/* 194:402 */       for (int j = 0; j < numNeighbours; j++)
/* 195:    */       {
/* 196:403 */         Instance inst2 = neighbours.instance(j);
/* 197:404 */         double[] diffInst = new double[M + 1];
/* 198:405 */         diffInst[0] = -1.0D;
/* 199:406 */         for (int k = 0; k < M; k++) {
/* 200:407 */           if (k != instances.classIndex())
/* 201:    */           {
/* 202:408 */             double diff = inst1.value(k) - inst2.value(k);
/* 203:409 */             if (getAssumeEuclideanDistance()) {
/* 204:410 */               diffInst[(k + 1)] = (diff * diff);
/* 205:    */             } else {
/* 206:412 */               diffInst[(k + 1)] = Math.abs(diff);
/* 207:    */             }
/* 208:    */           }
/* 209:    */           else
/* 210:    */           {
/* 211:415 */             diffInst[(k + 1)] = (inst1.classValue() == inst2.classValue() ? 1.0D : 0.0D);
/* 212:    */           }
/* 213:    */         }
/* 214:419 */         pairwiseData.add(new DenseInstance(1.0D, diffInst));
/* 215:    */       }
/* 216:    */     }
/* 217:422 */     pairwiseData.setClassIndex(instances.classIndex() + 1);
/* 218:    */     
/* 219:    */ 
/* 220:425 */     NonNegativeLogisticRegression nnlr = new NonNegativeLogisticRegression();
/* 221:426 */     nnlr.buildClassifier(pairwiseData);
/* 222:    */     
/* 223:428 */     double[] coefficients = nnlr.getCoefficients();
/* 224:429 */     for (int i = 1; i < coefficients.length; i++)
/* 225:    */     {
/* 226:430 */       this.m_weights[(i - 1)] = coefficients[i];
/* 227:431 */       if (getAssumeEuclideanDistance()) {
/* 228:432 */         this.m_weights[(i - 1)] = Math.sqrt(this.m_weights[(i - 1)]);
/* 229:    */       }
/* 230:    */     }
/* 231:    */   }
/* 232:    */   
/* 233:    */   public Capabilities getCapabilities()
/* 234:    */   {
/* 235:447 */     Capabilities result = super.getCapabilities();
/* 236:448 */     result.disableAll();
/* 237:    */     
/* 238:450 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/* 239:451 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/* 240:    */     
/* 241:453 */     return result;
/* 242:    */   }
/* 243:    */   
/* 244:    */   protected Instances process(Instances instances)
/* 245:    */     throws Exception
/* 246:    */   {
/* 247:467 */     if (!isFirstBatchDone()) {
/* 248:468 */       initFilter(instances);
/* 249:    */     }
/* 250:472 */     Instances result = new Instances(instances, instances.numInstances());
/* 251:473 */     for (int i = 0; i < instances.numInstances(); i++)
/* 252:    */     {
/* 253:474 */       Instance inst = instances.instance(i);
/* 254:475 */       double[] newData = new double[instances.numAttributes()];
/* 255:476 */       for (int j = 0; j < instances.numAttributes(); j++)
/* 256:    */       {
/* 257:477 */         newData[j] = inst.value(j);
/* 258:478 */         if (j != instances.classIndex()) {
/* 259:479 */           newData[j] *= this.m_weights[j];
/* 260:    */         }
/* 261:    */       }
/* 262:482 */       result.add(new DenseInstance(1.0D, newData));
/* 263:    */     }
/* 264:484 */     return result;
/* 265:    */   }
/* 266:    */   
/* 267:    */   public String getRevision()
/* 268:    */   {
/* 269:494 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 270:    */   }
/* 271:    */   
/* 272:    */   public static void main(String[] args)
/* 273:    */   {
/* 274:503 */     runFilter(new SupervisedAttributeScaler(), args);
/* 275:    */   }
/* 276:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.supervised.attribute.SupervisedAttributeScaler
 * JD-Core Version:    0.7.0.1
 */