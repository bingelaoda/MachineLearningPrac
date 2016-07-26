/*   1:    */ package weka.classifiers.lazy;
/*   2:    */ 
/*   3:    */ import java.util.Enumeration;
/*   4:    */ import weka.classifiers.AbstractClassifier;
/*   5:    */ import weka.classifiers.UpdateableClassifier;
/*   6:    */ import weka.core.Attribute;
/*   7:    */ import weka.core.Capabilities;
/*   8:    */ import weka.core.Capabilities.Capability;
/*   9:    */ import weka.core.Instance;
/*  10:    */ import weka.core.Instances;
/*  11:    */ import weka.core.RevisionUtils;
/*  12:    */ import weka.core.TechnicalInformation;
/*  13:    */ import weka.core.TechnicalInformation.Field;
/*  14:    */ import weka.core.TechnicalInformation.Type;
/*  15:    */ import weka.core.TechnicalInformationHandler;
/*  16:    */ import weka.core.Utils;
/*  17:    */ 
/*  18:    */ public class IB1
/*  19:    */   extends AbstractClassifier
/*  20:    */   implements UpdateableClassifier, TechnicalInformationHandler
/*  21:    */ {
/*  22:    */   static final long serialVersionUID = -6152184127304895851L;
/*  23:    */   private Instances m_Train;
/*  24:    */   private double[] m_MinArray;
/*  25:    */   private double[] m_MaxArray;
/*  26:    */   
/*  27:    */   public String globalInfo()
/*  28:    */   {
/*  29:107 */     return "Nearest-neighbour classifier. Uses normalized Euclidean distance to find the training instance closest to the given test instance, and predicts the same class as this training instance. If multiple instances have the same (smallest) distance to the test instance, the first one found is used.\n\nFor more information, see \n\n" + getTechnicalInformation().toString();
/*  30:    */   }
/*  31:    */   
/*  32:    */   public TechnicalInformation getTechnicalInformation()
/*  33:    */   {
/*  34:127 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.ARTICLE);
/*  35:128 */     result.setValue(TechnicalInformation.Field.AUTHOR, "D. Aha and D. Kibler");
/*  36:129 */     result.setValue(TechnicalInformation.Field.YEAR, "1991");
/*  37:130 */     result.setValue(TechnicalInformation.Field.TITLE, "Instance-based learning algorithms");
/*  38:131 */     result.setValue(TechnicalInformation.Field.JOURNAL, "Machine Learning");
/*  39:132 */     result.setValue(TechnicalInformation.Field.VOLUME, "6");
/*  40:133 */     result.setValue(TechnicalInformation.Field.PAGES, "37-66");
/*  41:    */     
/*  42:135 */     return result;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public Capabilities getCapabilities()
/*  46:    */   {
/*  47:145 */     Capabilities result = super.getCapabilities();
/*  48:146 */     result.disableAll();
/*  49:    */     
/*  50:    */ 
/*  51:149 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  52:150 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  53:151 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*  54:152 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  55:    */     
/*  56:    */ 
/*  57:155 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  58:156 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  59:    */     
/*  60:    */ 
/*  61:159 */     result.setMinimumNumberInstances(0);
/*  62:    */     
/*  63:161 */     return result;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public void buildClassifier(Instances instances)
/*  67:    */     throws Exception
/*  68:    */   {
/*  69:174 */     getCapabilities().testWithFail(instances);
/*  70:    */     
/*  71:    */ 
/*  72:177 */     instances = new Instances(instances);
/*  73:178 */     instances.deleteWithMissingClass();
/*  74:    */     
/*  75:180 */     this.m_Train = new Instances(instances, 0, instances.numInstances());
/*  76:    */     
/*  77:182 */     this.m_MinArray = new double[this.m_Train.numAttributes()];
/*  78:183 */     this.m_MaxArray = new double[this.m_Train.numAttributes()];
/*  79:184 */     for (int i = 0; i < this.m_Train.numAttributes(); i++)
/*  80:    */     {
/*  81:185 */       double tmp90_87 = (0.0D / 0.0D);this.m_MaxArray[i] = tmp90_87;this.m_MinArray[i] = tmp90_87;
/*  82:    */     }
/*  83:187 */     Enumeration<Instance> enu = this.m_Train.enumerateInstances();
/*  84:188 */     while (enu.hasMoreElements()) {
/*  85:189 */       updateMinMax((Instance)enu.nextElement());
/*  86:    */     }
/*  87:    */   }
/*  88:    */   
/*  89:    */   public void updateClassifier(Instance instance)
/*  90:    */     throws Exception
/*  91:    */   {
/*  92:202 */     if (!this.m_Train.equalHeaders(instance.dataset())) {
/*  93:203 */       throw new Exception("Incompatible instance types\n" + this.m_Train.equalHeadersMsg(instance.dataset()));
/*  94:    */     }
/*  95:206 */     if (instance.classIsMissing()) {
/*  96:207 */       return;
/*  97:    */     }
/*  98:209 */     this.m_Train.add(instance);
/*  99:210 */     updateMinMax(instance);
/* 100:    */   }
/* 101:    */   
/* 102:    */   public double classifyInstance(Instance instance)
/* 103:    */     throws Exception
/* 104:    */   {
/* 105:223 */     if (this.m_Train.numInstances() == 0) {
/* 106:224 */       throw new Exception("No training instances!");
/* 107:    */     }
/* 108:227 */     double minDistance = 1.7976931348623157E+308D;double classValue = 0.0D;
/* 109:228 */     updateMinMax(instance);
/* 110:229 */     Enumeration<Instance> enu = this.m_Train.enumerateInstances();
/* 111:230 */     while (enu.hasMoreElements())
/* 112:    */     {
/* 113:231 */       Instance trainInstance = (Instance)enu.nextElement();
/* 114:232 */       if (!trainInstance.classIsMissing())
/* 115:    */       {
/* 116:233 */         double distance = distance(instance, trainInstance);
/* 117:234 */         if (distance < minDistance)
/* 118:    */         {
/* 119:235 */           minDistance = distance;
/* 120:236 */           classValue = trainInstance.classValue();
/* 121:    */         }
/* 122:    */       }
/* 123:    */     }
/* 124:241 */     return classValue;
/* 125:    */   }
/* 126:    */   
/* 127:    */   public String toString()
/* 128:    */   {
/* 129:252 */     return "IB1 classifier";
/* 130:    */   }
/* 131:    */   
/* 132:    */   private double distance(Instance first, Instance second)
/* 133:    */   {
/* 134:264 */     double distance = 0.0D;
/* 135:266 */     for (int i = 0; i < this.m_Train.numAttributes(); i++) {
/* 136:267 */       if (i != this.m_Train.classIndex()) {
/* 137:270 */         if (this.m_Train.attribute(i).isNominal())
/* 138:    */         {
/* 139:273 */           if ((first.isMissing(i)) || (second.isMissing(i)) || ((int)first.value(i) != (int)second.value(i))) {
/* 140:275 */             distance += 1.0D;
/* 141:    */           }
/* 142:    */         }
/* 143:    */         else
/* 144:    */         {
/* 145:    */           double diff;
/* 146:280 */           if ((first.isMissing(i)) || (second.isMissing(i)))
/* 147:    */           {
/* 148:    */             double diff;
/* 149:281 */             if ((first.isMissing(i)) && (second.isMissing(i)))
/* 150:    */             {
/* 151:282 */               diff = 1.0D;
/* 152:    */             }
/* 153:    */             else
/* 154:    */             {
/* 155:    */               double diff;
/* 156:    */               double diff;
/* 157:284 */               if (second.isMissing(i)) {
/* 158:285 */                 diff = norm(first.value(i), i);
/* 159:    */               } else {
/* 160:287 */                 diff = norm(second.value(i), i);
/* 161:    */               }
/* 162:289 */               if (diff < 0.5D) {
/* 163:290 */                 diff = 1.0D - diff;
/* 164:    */               }
/* 165:    */             }
/* 166:    */           }
/* 167:    */           else
/* 168:    */           {
/* 169:294 */             diff = norm(first.value(i), i) - norm(second.value(i), i);
/* 170:    */           }
/* 171:296 */           distance += diff * diff;
/* 172:    */         }
/* 173:    */       }
/* 174:    */     }
/* 175:300 */     return distance;
/* 176:    */   }
/* 177:    */   
/* 178:    */   private double norm(double x, int i)
/* 179:    */   {
/* 180:312 */     if ((Double.isNaN(this.m_MinArray[i])) || (Utils.eq(this.m_MaxArray[i], this.m_MinArray[i]))) {
/* 181:313 */       return 0.0D;
/* 182:    */     }
/* 183:315 */     return (x - this.m_MinArray[i]) / (this.m_MaxArray[i] - this.m_MinArray[i]);
/* 184:    */   }
/* 185:    */   
/* 186:    */   private void updateMinMax(Instance instance)
/* 187:    */   {
/* 188:327 */     for (int j = 0; j < this.m_Train.numAttributes(); j++) {
/* 189:328 */       if ((this.m_Train.attribute(j).isNumeric()) && (!instance.isMissing(j))) {
/* 190:329 */         if (Double.isNaN(this.m_MinArray[j]))
/* 191:    */         {
/* 192:330 */           this.m_MinArray[j] = instance.value(j);
/* 193:331 */           this.m_MaxArray[j] = instance.value(j);
/* 194:    */         }
/* 195:333 */         else if (instance.value(j) < this.m_MinArray[j])
/* 196:    */         {
/* 197:334 */           this.m_MinArray[j] = instance.value(j);
/* 198:    */         }
/* 199:336 */         else if (instance.value(j) > this.m_MaxArray[j])
/* 200:    */         {
/* 201:337 */           this.m_MaxArray[j] = instance.value(j);
/* 202:    */         }
/* 203:    */       }
/* 204:    */     }
/* 205:    */   }
/* 206:    */   
/* 207:    */   public String getRevision()
/* 208:    */   {
/* 209:352 */     return RevisionUtils.extract("$Revision: 10390 $");
/* 210:    */   }
/* 211:    */   
/* 212:    */   public static void main(String[] argv)
/* 213:    */   {
/* 214:362 */     runClassifier(new IB1(), argv);
/* 215:    */   }
/* 216:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.lazy.IB1
 * JD-Core Version:    0.7.0.1
 */