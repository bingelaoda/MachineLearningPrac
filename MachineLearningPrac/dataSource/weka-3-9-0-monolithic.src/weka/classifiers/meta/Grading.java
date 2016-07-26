/*   1:    */ package weka.classifiers.meta;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Random;
/*   5:    */ import weka.classifiers.AbstractClassifier;
/*   6:    */ import weka.classifiers.Classifier;
/*   7:    */ import weka.core.Attribute;
/*   8:    */ import weka.core.DenseInstance;
/*   9:    */ import weka.core.Instance;
/*  10:    */ import weka.core.Instances;
/*  11:    */ import weka.core.RevisionUtils;
/*  12:    */ import weka.core.TechnicalInformation;
/*  13:    */ import weka.core.TechnicalInformation.Field;
/*  14:    */ import weka.core.TechnicalInformation.Type;
/*  15:    */ import weka.core.TechnicalInformationHandler;
/*  16:    */ import weka.core.Utils;
/*  17:    */ 
/*  18:    */ public class Grading
/*  19:    */   extends Stacking
/*  20:    */   implements TechnicalInformationHandler
/*  21:    */ {
/*  22:    */   static final long serialVersionUID = 5207837947890081170L;
/*  23:114 */   protected Classifier[] m_MetaClassifiers = new Classifier[0];
/*  24:117 */   protected double[] m_InstPerClass = null;
/*  25:    */   
/*  26:    */   public String globalInfo()
/*  27:    */   {
/*  28:128 */     return "Implements Grading. The base classifiers are \"graded\".\n\nFor more information, see\n\n" + getTechnicalInformation().toString();
/*  29:    */   }
/*  30:    */   
/*  31:    */   public TechnicalInformation getTechnicalInformation()
/*  32:    */   {
/*  33:143 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*  34:144 */     result.setValue(TechnicalInformation.Field.AUTHOR, "A.K. Seewald and J. Fuernkranz");
/*  35:145 */     result.setValue(TechnicalInformation.Field.TITLE, "An Evaluation of Grading Classifiers");
/*  36:146 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "Advances in Intelligent Data Analysis: 4th International Conference");
/*  37:    */     
/*  38:148 */     result.setValue(TechnicalInformation.Field.EDITOR, "F. Hoffmann et al.");
/*  39:149 */     result.setValue(TechnicalInformation.Field.YEAR, "2001");
/*  40:150 */     result.setValue(TechnicalInformation.Field.PAGES, "115-124");
/*  41:151 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "Springer");
/*  42:152 */     result.setValue(TechnicalInformation.Field.ADDRESS, "Berlin/Heidelberg/New York/Tokyo");
/*  43:    */     
/*  44:154 */     return result;
/*  45:    */   }
/*  46:    */   
/*  47:    */   protected void generateMetaLevel(Instances newData, Random random)
/*  48:    */     throws Exception
/*  49:    */   {
/*  50:168 */     this.m_MetaFormat = metaFormat(newData);
/*  51:169 */     Instances[] metaData = new Instances[this.m_Classifiers.length];
/*  52:170 */     for (int i = 0; i < this.m_Classifiers.length; i++) {
/*  53:171 */       metaData[i] = metaFormat(newData);
/*  54:    */     }
/*  55:173 */     for (int j = 0; j < this.m_NumFolds; j++)
/*  56:    */     {
/*  57:175 */       Instances train = newData.trainCV(this.m_NumFolds, j, random);
/*  58:176 */       Instances test = newData.testCV(this.m_NumFolds, j);
/*  59:179 */       for (int i = 0; i < this.m_Classifiers.length; i++)
/*  60:    */       {
/*  61:180 */         getClassifier(i).buildClassifier(train);
/*  62:181 */         for (int k = 0; k < test.numInstances(); k++) {
/*  63:182 */           metaData[i].add(metaInstance(test.instance(k), i));
/*  64:    */         }
/*  65:    */       }
/*  66:    */     }
/*  67:188 */     this.m_InstPerClass = new double[newData.numClasses()];
/*  68:189 */     for (int i = 0; i < newData.numClasses(); i++) {
/*  69:190 */       this.m_InstPerClass[i] = 0.0D;
/*  70:    */     }
/*  71:192 */     for (int i = 0; i < newData.numInstances(); i++) {
/*  72:193 */       this.m_InstPerClass[((int)newData.instance(i).classValue())] += 1.0D;
/*  73:    */     }
/*  74:196 */     this.m_MetaClassifiers = AbstractClassifier.makeCopies(this.m_MetaClassifier, this.m_Classifiers.length);
/*  75:199 */     for (int i = 0; i < this.m_Classifiers.length; i++) {
/*  76:200 */       this.m_MetaClassifiers[i].buildClassifier(metaData[i]);
/*  77:    */     }
/*  78:    */   }
/*  79:    */   
/*  80:    */   public double[] distributionForInstance(Instance instance)
/*  81:    */     throws Exception
/*  82:    */   {
/*  83:217 */     int numPreds = 0;
/*  84:218 */     int numClassifiers = this.m_Classifiers.length;
/*  85:    */     
/*  86:220 */     double[] predConfs = new double[numClassifiers];
/*  87:223 */     for (int i = 0; i < numClassifiers; i++)
/*  88:    */     {
/*  89:224 */       double[] preds = this.m_MetaClassifiers[i].distributionForInstance(metaInstance(instance, i));
/*  90:226 */       if (this.m_MetaClassifiers[i].classifyInstance(metaInstance(instance, i)) == 1.0D) {
/*  91:227 */         predConfs[i] = preds[1];
/*  92:    */       } else {
/*  93:229 */         predConfs[i] = (-preds[0]);
/*  94:    */       }
/*  95:    */     }
/*  96:232 */     if (predConfs[Utils.maxIndex(predConfs)] < 0.0D) {
/*  97:233 */       for (int i = 0; i < numClassifiers; i++) {
/*  98:234 */         predConfs[i] = (1.0D + predConfs[i]);
/*  99:    */       }
/* 100:    */     } else {
/* 101:237 */       for (int i = 0; i < numClassifiers; i++) {
/* 102:238 */         if (predConfs[i] < 0.0D) {
/* 103:239 */           predConfs[i] = 0.0D;
/* 104:    */         }
/* 105:    */       }
/* 106:    */     }
/* 107:249 */     double[] preds = new double[instance.numClasses()];
/* 108:250 */     for (int i = 0; i < instance.numClasses(); i++) {
/* 109:251 */       preds[i] = 0.0D;
/* 110:    */     }
/* 111:253 */     for (int i = 0; i < numClassifiers; i++)
/* 112:    */     {
/* 113:254 */       int idxPreds = (int)this.m_Classifiers[i].classifyInstance(instance);
/* 114:255 */       preds[idxPreds] += predConfs[i];
/* 115:    */     }
/* 116:258 */     double maxPreds = preds[Utils.maxIndex(preds)];
/* 117:259 */     int MaxInstPerClass = -100;
/* 118:260 */     int MaxClass = -1;
/* 119:261 */     for (int i = 0; i < instance.numClasses(); i++) {
/* 120:262 */       if (preds[i] == maxPreds)
/* 121:    */       {
/* 122:263 */         numPreds++;
/* 123:264 */         if (this.m_InstPerClass[i] > MaxInstPerClass)
/* 124:    */         {
/* 125:265 */           MaxInstPerClass = (int)this.m_InstPerClass[i];
/* 126:266 */           MaxClass = i;
/* 127:    */         }
/* 128:    */       }
/* 129:    */     }
/* 130:    */     int predictedIndex;
/* 131:    */     int predictedIndex;
/* 132:272 */     if (numPreds == 1) {
/* 133:273 */       predictedIndex = Utils.maxIndex(preds);
/* 134:    */     } else {
/* 135:282 */       predictedIndex = MaxClass;
/* 136:    */     }
/* 137:284 */     double[] classProbs = new double[instance.numClasses()];
/* 138:285 */     classProbs[predictedIndex] = 1.0D;
/* 139:286 */     return classProbs;
/* 140:    */   }
/* 141:    */   
/* 142:    */   public String toString()
/* 143:    */   {
/* 144:297 */     if (this.m_Classifiers.length == 0) {
/* 145:298 */       return "Grading: No base schemes entered.";
/* 146:    */     }
/* 147:300 */     if (this.m_MetaClassifiers.length == 0) {
/* 148:301 */       return "Grading: No meta scheme selected.";
/* 149:    */     }
/* 150:303 */     if (this.m_MetaFormat == null) {
/* 151:304 */       return "Grading: No model built yet.";
/* 152:    */     }
/* 153:306 */     String result = "Grading\n\nBase classifiers\n\n";
/* 154:307 */     for (int i = 0; i < this.m_Classifiers.length; i++) {
/* 155:308 */       result = result + getClassifier(i).toString() + "\n\n";
/* 156:    */     }
/* 157:311 */     result = result + "\n\nMeta classifiers\n\n";
/* 158:312 */     for (int i = 0; i < this.m_Classifiers.length; i++) {
/* 159:313 */       result = result + this.m_MetaClassifiers[i].toString() + "\n\n";
/* 160:    */     }
/* 161:316 */     return result;
/* 162:    */   }
/* 163:    */   
/* 164:    */   protected Instances metaFormat(Instances instances)
/* 165:    */     throws Exception
/* 166:    */   {
/* 167:329 */     ArrayList<Attribute> attributes = new ArrayList();
/* 168:332 */     for (int i = 0; i < instances.numAttributes(); i++) {
/* 169:333 */       if (i != instances.classIndex()) {
/* 170:334 */         attributes.add(instances.attribute(i));
/* 171:    */       }
/* 172:    */     }
/* 173:338 */     ArrayList<String> nomElements = new ArrayList(2);
/* 174:339 */     nomElements.add("0");
/* 175:340 */     nomElements.add("1");
/* 176:341 */     attributes.add(new Attribute("PredConf", nomElements));
/* 177:    */     
/* 178:343 */     Instances metaFormat = new Instances("Meta format", attributes, 0);
/* 179:344 */     metaFormat.setClassIndex(metaFormat.numAttributes() - 1);
/* 180:345 */     return metaFormat;
/* 181:    */   }
/* 182:    */   
/* 183:    */   protected Instance metaInstance(Instance instance, int k)
/* 184:    */     throws Exception
/* 185:    */   {
/* 186:358 */     double[] values = new double[this.m_MetaFormat.numAttributes()];
/* 187:    */     
/* 188:    */ 
/* 189:    */ 
/* 190:    */ 
/* 191:    */ 
/* 192:    */ 
/* 193:365 */     int idx = 0;
/* 194:366 */     for (int i = 0; i < instance.numAttributes(); i++) {
/* 195:367 */       if (i != instance.classIndex())
/* 196:    */       {
/* 197:368 */         values[idx] = instance.value(i);
/* 198:369 */         idx++;
/* 199:    */       }
/* 200:    */     }
/* 201:373 */     Classifier classifier = getClassifier(k);
/* 202:375 */     if (this.m_BaseFormat.classAttribute().isNumeric()) {
/* 203:376 */       throw new Exception("Class Attribute must not be numeric!");
/* 204:    */     }
/* 205:378 */     double[] dist = classifier.distributionForInstance(instance);
/* 206:    */     
/* 207:380 */     int maxIdx = 0;
/* 208:381 */     double maxVal = dist[0];
/* 209:382 */     for (int j = 1; j < dist.length; j++) {
/* 210:383 */       if (dist[j] > maxVal)
/* 211:    */       {
/* 212:384 */         maxVal = dist[j];
/* 213:385 */         maxIdx = j;
/* 214:    */       }
/* 215:    */     }
/* 216:388 */     double predConf = instance.classValue() == maxIdx ? 1.0D : 0.0D;
/* 217:    */     
/* 218:    */ 
/* 219:391 */     values[idx] = predConf;
/* 220:392 */     Instance metaInstance = new DenseInstance(1.0D, values);
/* 221:393 */     metaInstance.setDataset(this.m_MetaFormat);
/* 222:394 */     return metaInstance;
/* 223:    */   }
/* 224:    */   
/* 225:    */   public String getRevision()
/* 226:    */   {
/* 227:404 */     return RevisionUtils.extract("$Revision: 10362 $");
/* 228:    */   }
/* 229:    */   
/* 230:    */   public static void main(String[] argv)
/* 231:    */   {
/* 232:414 */     runClassifier(new Grading(), argv);
/* 233:    */   }
/* 234:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.meta.Grading
 * JD-Core Version:    0.7.0.1
 */