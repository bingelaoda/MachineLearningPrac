/*   1:    */ package weka.classifiers.misc;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.io.Serializable;
/*   5:    */ import weka.classifiers.AbstractClassifier;
/*   6:    */ import weka.classifiers.Classifier;
/*   7:    */ import weka.classifiers.rules.ZeroR;
/*   8:    */ import weka.core.Attribute;
/*   9:    */ import weka.core.Capabilities;
/*  10:    */ import weka.core.Capabilities.Capability;
/*  11:    */ import weka.core.Instance;
/*  12:    */ import weka.core.Instances;
/*  13:    */ import weka.core.RevisionHandler;
/*  14:    */ import weka.core.RevisionUtils;
/*  15:    */ import weka.core.UnsupportedAttributeTypeException;
/*  16:    */ import weka.core.Utils;
/*  17:    */ 
/*  18:    */ public class HyperPipes
/*  19:    */   extends AbstractClassifier
/*  20:    */ {
/*  21:    */   static final long serialVersionUID = -7527596632268975274L;
/*  22:    */   protected int m_ClassIndex;
/*  23:    */   protected Instances m_Instances;
/*  24:    */   protected HyperPipe[] m_HyperPipes;
/*  25:    */   protected Classifier m_ZeroR;
/*  26:    */   
/*  27:    */   public String globalInfo()
/*  28:    */   {
/*  29: 83 */     return "Class implementing a HyperPipe classifier. For each category a HyperPipe is constructed that contains all points of that category (essentially records the attribute bounds observed for each category). Test instances are classified according to the category that \"most contains the instance\".\nDoes not handle numeric class, or missing values in test cases. Extremely simple algorithm, but has the advantage of being extremely fast, and works quite well when you have \"smegloads\" of attributes.";
/*  30:    */   }
/*  31:    */   
/*  32:    */   class HyperPipe
/*  33:    */     implements Serializable, RevisionHandler
/*  34:    */   {
/*  35:    */     static final long serialVersionUID = 3972254260367902025L;
/*  36:    */     protected double[][] m_NumericBounds;
/*  37:    */     protected boolean[][] m_NominalBounds;
/*  38:    */     
/*  39:    */     public HyperPipe(Instances instances)
/*  40:    */       throws Exception
/*  41:    */     {
/*  42:119 */       this.m_NumericBounds = new double[instances.numAttributes()][];
/*  43:120 */       this.m_NominalBounds = new boolean[instances.numAttributes()][];
/*  44:122 */       for (int i = 0; i < instances.numAttributes(); i++) {
/*  45:123 */         switch (instances.attribute(i).type())
/*  46:    */         {
/*  47:    */         case 0: 
/*  48:125 */           this.m_NumericBounds[i] = new double[2];
/*  49:126 */           this.m_NumericBounds[i][0] = (1.0D / 0.0D);
/*  50:127 */           this.m_NumericBounds[i][1] = (-1.0D / 0.0D);
/*  51:128 */           break;
/*  52:    */         case 1: 
/*  53:130 */           this.m_NominalBounds[i] = new boolean[instances.attribute(i).numValues()];
/*  54:131 */           break;
/*  55:    */         default: 
/*  56:133 */           throw new UnsupportedAttributeTypeException("Cannot process string attributes!");
/*  57:    */         }
/*  58:    */       }
/*  59:137 */       for (int i = 0; i < instances.numInstances(); i++) {
/*  60:138 */         addInstance(instances.instance(i));
/*  61:    */       }
/*  62:    */     }
/*  63:    */     
/*  64:    */     public void addInstance(Instance instance)
/*  65:    */       throws Exception
/*  66:    */     {
/*  67:152 */       for (int j = 0; j < instance.numAttributes(); j++) {
/*  68:153 */         if ((j != HyperPipes.this.m_ClassIndex) && (!instance.isMissing(j)))
/*  69:    */         {
/*  70:155 */           double current = instance.value(j);
/*  71:157 */           if (this.m_NumericBounds[j] != null)
/*  72:    */           {
/*  73:158 */             if (current < this.m_NumericBounds[j][0]) {
/*  74:159 */               this.m_NumericBounds[j][0] = current;
/*  75:    */             }
/*  76:160 */             if (current > this.m_NumericBounds[j][1]) {
/*  77:161 */               this.m_NumericBounds[j][1] = current;
/*  78:    */             }
/*  79:    */           }
/*  80:    */           else
/*  81:    */           {
/*  82:164 */             this.m_NominalBounds[j][((int)current)] = 1;
/*  83:    */           }
/*  84:    */         }
/*  85:    */       }
/*  86:    */     }
/*  87:    */     
/*  88:    */     public double partialContains(Instance instance)
/*  89:    */       throws Exception
/*  90:    */     {
/*  91:181 */       int count = 0;
/*  92:182 */       for (int i = 0; i < instance.numAttributes(); i++) {
/*  93:184 */         if (i != HyperPipes.this.m_ClassIndex) {
/*  94:187 */           if (!instance.isMissing(i))
/*  95:    */           {
/*  96:191 */             double current = instance.value(i);
/*  97:193 */             if (this.m_NumericBounds[i] != null)
/*  98:    */             {
/*  99:194 */               if ((current >= this.m_NumericBounds[i][0]) && (current <= this.m_NumericBounds[i][1])) {
/* 100:196 */                 count++;
/* 101:    */               }
/* 102:    */             }
/* 103:199 */             else if (this.m_NominalBounds[i][((int)current)] != 0) {
/* 104:200 */               count++;
/* 105:    */             }
/* 106:    */           }
/* 107:    */         }
/* 108:    */       }
/* 109:205 */       return count / (instance.numAttributes() - 1);
/* 110:    */     }
/* 111:    */     
/* 112:    */     public String getRevision()
/* 113:    */     {
/* 114:214 */       return RevisionUtils.extract("$Revision: 8109 $");
/* 115:    */     }
/* 116:    */   }
/* 117:    */   
/* 118:    */   public Capabilities getCapabilities()
/* 119:    */   {
/* 120:225 */     Capabilities result = super.getCapabilities();
/* 121:226 */     result.disableAll();
/* 122:    */     
/* 123:    */ 
/* 124:229 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/* 125:230 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/* 126:231 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/* 127:232 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 128:    */     
/* 129:    */ 
/* 130:235 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/* 131:236 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 132:    */     
/* 133:    */ 
/* 134:239 */     result.setMinimumNumberInstances(0);
/* 135:    */     
/* 136:241 */     return result;
/* 137:    */   }
/* 138:    */   
/* 139:    */   public void buildClassifier(Instances instances)
/* 140:    */     throws Exception
/* 141:    */   {
/* 142:253 */     getCapabilities().testWithFail(instances);
/* 143:    */     
/* 144:    */ 
/* 145:256 */     instances = new Instances(instances);
/* 146:257 */     instances.deleteWithMissingClass();
/* 147:260 */     if (instances.numAttributes() == 1)
/* 148:    */     {
/* 149:261 */       System.err.println("Cannot build model (only class attribute present in data!), using ZeroR model instead!");
/* 150:    */       
/* 151:    */ 
/* 152:264 */       this.m_ZeroR = new ZeroR();
/* 153:265 */       this.m_ZeroR.buildClassifier(instances);
/* 154:266 */       return;
/* 155:    */     }
/* 156:269 */     this.m_ZeroR = null;
/* 157:    */     
/* 158:    */ 
/* 159:272 */     this.m_ClassIndex = instances.classIndex();
/* 160:273 */     this.m_Instances = new Instances(instances, 0);
/* 161:    */     
/* 162:    */ 
/* 163:276 */     this.m_HyperPipes = new HyperPipe[instances.numClasses()];
/* 164:277 */     for (int i = 0; i < this.m_HyperPipes.length; i++) {
/* 165:278 */       this.m_HyperPipes[i] = new HyperPipe(new Instances(instances, 0));
/* 166:    */     }
/* 167:282 */     for (int i = 0; i < instances.numInstances(); i++) {
/* 168:283 */       updateClassifier(instances.instance(i));
/* 169:    */     }
/* 170:    */   }
/* 171:    */   
/* 172:    */   public void updateClassifier(Instance instance)
/* 173:    */     throws Exception
/* 174:    */   {
/* 175:296 */     if (instance.classIsMissing()) {
/* 176:297 */       return;
/* 177:    */     }
/* 178:299 */     this.m_HyperPipes[((int)instance.classValue())].addInstance(instance);
/* 179:    */   }
/* 180:    */   
/* 181:    */   public double[] distributionForInstance(Instance instance)
/* 182:    */     throws Exception
/* 183:    */   {
/* 184:313 */     if (this.m_ZeroR != null) {
/* 185:314 */       return this.m_ZeroR.distributionForInstance(instance);
/* 186:    */     }
/* 187:317 */     double[] dist = new double[this.m_HyperPipes.length];
/* 188:319 */     for (int j = 0; j < this.m_HyperPipes.length; j++) {
/* 189:320 */       dist[j] = this.m_HyperPipes[j].partialContains(instance);
/* 190:    */     }
/* 191:323 */     double sum = Utils.sum(dist);
/* 192:324 */     if (sum <= 0.0D)
/* 193:    */     {
/* 194:325 */       for (int j = 0; j < dist.length; j++) {
/* 195:326 */         dist[j] = (1.0D / dist.length);
/* 196:    */       }
/* 197:328 */       return dist;
/* 198:    */     }
/* 199:330 */     Utils.normalize(dist, sum);
/* 200:331 */     return dist;
/* 201:    */   }
/* 202:    */   
/* 203:    */   public String toString()
/* 204:    */   {
/* 205:344 */     if (this.m_ZeroR != null)
/* 206:    */     {
/* 207:345 */       StringBuffer buf = new StringBuffer();
/* 208:346 */       buf.append(getClass().getName().replaceAll(".*\\.", "") + "\n");
/* 209:347 */       buf.append(getClass().getName().replaceAll(".*\\.", "").replaceAll(".", "=") + "\n\n");
/* 210:348 */       buf.append("Warning: No model could be built, hence ZeroR model is used:\n\n");
/* 211:349 */       buf.append(this.m_ZeroR.toString());
/* 212:350 */       return buf.toString();
/* 213:    */     }
/* 214:353 */     if (this.m_HyperPipes == null) {
/* 215:354 */       return "HyperPipes classifier";
/* 216:    */     }
/* 217:357 */     StringBuffer text = new StringBuffer("HyperPipes classifier\n");
/* 218:    */     
/* 219:    */ 
/* 220:    */ 
/* 221:    */ 
/* 222:    */ 
/* 223:    */ 
/* 224:    */ 
/* 225:    */ 
/* 226:    */ 
/* 227:367 */     return text.toString();
/* 228:    */   }
/* 229:    */   
/* 230:    */   public String getRevision()
/* 231:    */   {
/* 232:376 */     return RevisionUtils.extract("$Revision: 8109 $");
/* 233:    */   }
/* 234:    */   
/* 235:    */   public static void main(String[] argv)
/* 236:    */   {
/* 237:386 */     runClassifier(new HyperPipes(), argv);
/* 238:    */   }
/* 239:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.misc.HyperPipes
 * JD-Core Version:    0.7.0.1
 */