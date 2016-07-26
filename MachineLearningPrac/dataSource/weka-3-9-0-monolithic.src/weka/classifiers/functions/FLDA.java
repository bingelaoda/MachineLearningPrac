/*   1:    */ package weka.classifiers.functions;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import no.uib.cipr.matrix.DenseMatrix;
/*   7:    */ import no.uib.cipr.matrix.DenseVector;
/*   8:    */ import no.uib.cipr.matrix.Matrix;
/*   9:    */ import no.uib.cipr.matrix.UpperSymmDenseMatrix;
/*  10:    */ import no.uib.cipr.matrix.Vector.Norm;
/*  11:    */ import weka.classifiers.AbstractClassifier;
/*  12:    */ import weka.core.Attribute;
/*  13:    */ import weka.core.Capabilities;
/*  14:    */ import weka.core.Capabilities.Capability;
/*  15:    */ import weka.core.Instance;
/*  16:    */ import weka.core.Instances;
/*  17:    */ import weka.core.Option;
/*  18:    */ import weka.core.RevisionUtils;
/*  19:    */ import weka.core.Utils;
/*  20:    */ import weka.filters.Filter;
/*  21:    */ import weka.filters.unsupervised.attribute.RemoveUseless;
/*  22:    */ 
/*  23:    */ public class FLDA
/*  24:    */   extends AbstractClassifier
/*  25:    */ {
/*  26:    */   static final long serialVersionUID = -9212385698193681291L;
/*  27:    */   protected Instances m_Data;
/*  28:    */   protected no.uib.cipr.matrix.Vector m_Weights;
/*  29:    */   protected double m_Threshold;
/*  30: 83 */   protected double m_Ridge = 1.0E-006D;
/*  31:    */   protected RemoveUseless m_RemoveUseless;
/*  32:    */   
/*  33:    */   public String globalInfo()
/*  34:    */   {
/*  35: 92 */     return "Builds Fisher's Linear Discriminant function. The threshold is selected so that the separator is half-way between centroids. The class must be binary and all other attributes must be numeric. Missing values are not permitted. Constant attributes are removed using RemoveUseless. No standardization or normalization of attributes is performed.";
/*  36:    */   }
/*  37:    */   
/*  38:    */   public Capabilities getCapabilities()
/*  39:    */   {
/*  40:105 */     Capabilities result = super.getCapabilities();
/*  41:106 */     result.disableAll();
/*  42:    */     
/*  43:    */ 
/*  44:109 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  45:    */     
/*  46:    */ 
/*  47:112 */     result.enable(Capabilities.Capability.BINARY_CLASS);
/*  48:113 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  49:    */     
/*  50:    */ 
/*  51:116 */     result.setMinimumNumberInstances(0);
/*  52:    */     
/*  53:118 */     return result;
/*  54:    */   }
/*  55:    */   
/*  56:    */   protected no.uib.cipr.matrix.Vector[] getClassMeans(Instances data, int[] counts)
/*  57:    */   {
/*  58:126 */     double[][] centroids = new double[2][data.numAttributes() - 1];
/*  59:127 */     for (int i = 0; i < data.numInstances(); i++)
/*  60:    */     {
/*  61:128 */       Instance inst = data.instance(i);
/*  62:129 */       int index = 0;
/*  63:130 */       for (int j = 0; j < data.numAttributes(); j++) {
/*  64:131 */         if (j != data.classIndex()) {
/*  65:132 */           centroids[((int)inst.classValue())][(index++)] += inst.value(j);
/*  66:    */         }
/*  67:    */       }
/*  68:135 */       counts[((int)inst.classValue())] += 1;
/*  69:    */     }
/*  70:137 */     no.uib.cipr.matrix.Vector[] centroidVectors = new DenseVector[2];
/*  71:138 */     for (int i = 0; i < 2; i++)
/*  72:    */     {
/*  73:139 */       centroidVectors[i] = new DenseVector(centroids[i]);
/*  74:140 */       centroidVectors[i].scale(1.0D / counts[i]);
/*  75:    */     }
/*  76:142 */     if (this.m_Debug)
/*  77:    */     {
/*  78:143 */       System.out.println("Count for class 0: " + counts[0]);
/*  79:144 */       System.out.println("Centroid 0:" + centroidVectors[0]);
/*  80:145 */       System.out.println("Count for class 11: " + counts[1]);
/*  81:146 */       System.out.println("Centroid 1:" + centroidVectors[1]);
/*  82:    */     }
/*  83:149 */     return centroidVectors;
/*  84:    */   }
/*  85:    */   
/*  86:    */   protected Matrix[] getCenteredData(Instances data, int[] counts, no.uib.cipr.matrix.Vector[] centroids)
/*  87:    */   {
/*  88:157 */     Matrix[] centeredData = new Matrix[2];
/*  89:158 */     for (int i = 0; i < 2; i++) {
/*  90:159 */       centeredData[i] = new DenseMatrix(data.numAttributes() - 1, counts[i]);
/*  91:    */     }
/*  92:161 */     int[] indexC = new int[2];
/*  93:162 */     for (int i = 0; i < data.numInstances(); i++)
/*  94:    */     {
/*  95:163 */       Instance inst = data.instance(i);
/*  96:164 */       int classIndex = (int)inst.classValue();
/*  97:165 */       int index = 0;
/*  98:166 */       for (int j = 0; j < data.numAttributes(); j++) {
/*  99:167 */         if (j != data.classIndex())
/* 100:    */         {
/* 101:168 */           centeredData[classIndex].set(index, indexC[classIndex], inst.value(j) - centroids[classIndex].get(index));
/* 102:    */           
/* 103:170 */           index++;
/* 104:    */         }
/* 105:    */       }
/* 106:173 */       indexC[classIndex] += 1;
/* 107:    */     }
/* 108:175 */     if (this.m_Debug)
/* 109:    */     {
/* 110:176 */       System.out.println("Centered data for class 0:\n" + centeredData[0]);
/* 111:177 */       System.out.println("Centered data for class 1:\n" + centeredData[1]);
/* 112:    */     }
/* 113:179 */     return centeredData;
/* 114:    */   }
/* 115:    */   
/* 116:    */   public void buildClassifier(Instances insts)
/* 117:    */     throws Exception
/* 118:    */   {
/* 119:188 */     getCapabilities().testWithFail(insts);
/* 120:    */     
/* 121:    */ 
/* 122:191 */     this.m_RemoveUseless = new RemoveUseless();
/* 123:192 */     this.m_RemoveUseless.setInputFormat(insts);
/* 124:193 */     insts = Filter.useFilter(insts, this.m_RemoveUseless);
/* 125:194 */     insts.deleteWithMissingClass();
/* 126:    */     
/* 127:    */ 
/* 128:197 */     int[] classCounts = new int[2];
/* 129:198 */     no.uib.cipr.matrix.Vector[] centroids = getClassMeans(insts, classCounts);
/* 130:    */     
/* 131:    */ 
/* 132:201 */     no.uib.cipr.matrix.Vector diff = centroids[0].copy().add(-1.0D, centroids[1]);
/* 133:    */     
/* 134:    */ 
/* 135:204 */     Matrix[] data = getCenteredData(insts, classCounts, centroids);
/* 136:    */     
/* 137:    */ 
/* 138:207 */     Matrix scatter = new UpperSymmDenseMatrix(data[0].numRows()).rank1(data[0]).add(new UpperSymmDenseMatrix(data[1].numRows()).rank1(data[1]));
/* 139:209 */     for (int i = 0; i < scatter.numColumns(); i++) {
/* 140:210 */       scatter.add(i, i, this.m_Ridge);
/* 141:    */     }
/* 142:212 */     if (this.m_Debug) {
/* 143:213 */       System.out.println("Scatter:\n" + scatter);
/* 144:    */     }
/* 145:217 */     this.m_Weights = scatter.solve(diff, new DenseVector(scatter.numColumns()));
/* 146:218 */     this.m_Weights.scale(1.0D / this.m_Weights.norm(Vector.Norm.Two));
/* 147:    */     
/* 148:    */ 
/* 149:221 */     this.m_Threshold = (0.5D * this.m_Weights.dot(centroids[0].copy().add(centroids[1])));
/* 150:    */     
/* 151:    */ 
/* 152:224 */     this.m_Data = new Instances(insts, 0);
/* 153:    */   }
/* 154:    */   
/* 155:    */   public double[] distributionForInstance(Instance inst)
/* 156:    */     throws Exception
/* 157:    */   {
/* 158:233 */     this.m_RemoveUseless.input(inst);
/* 159:234 */     inst = this.m_RemoveUseless.output();
/* 160:    */     
/* 161:    */ 
/* 162:237 */     no.uib.cipr.matrix.Vector instM = new DenseVector(inst.numAttributes() - 1);
/* 163:238 */     int index = 0;
/* 164:239 */     for (int i = 0; i < inst.numAttributes(); i++) {
/* 165:240 */       if (i != this.m_Data.classIndex()) {
/* 166:241 */         instM.set(index++, inst.value(i));
/* 167:    */       }
/* 168:    */     }
/* 169:246 */     double[] dist = new double[2];
/* 170:247 */     dist[1] = (1.0D / (1.0D + Math.exp(instM.dot(this.m_Weights) - this.m_Threshold)));
/* 171:248 */     dist[0] = (1.0D - dist[1]);
/* 172:249 */     return dist;
/* 173:    */   }
/* 174:    */   
/* 175:    */   public String toString()
/* 176:    */   {
/* 177:258 */     if (this.m_Weights == null) {
/* 178:259 */       return "No model has been built yet.";
/* 179:    */     }
/* 180:261 */     StringBuffer result = new StringBuffer();
/* 181:262 */     result.append("Fisher's Linear Discriminant Analysis\n\n");
/* 182:263 */     result.append("Threshold: " + this.m_Threshold + "\n\n");
/* 183:264 */     result.append("Weights:\n\n");
/* 184:265 */     int index = 0;
/* 185:266 */     for (int i = 0; i < this.m_Data.numAttributes(); i++) {
/* 186:267 */       if (i != this.m_Data.classIndex())
/* 187:    */       {
/* 188:268 */         result.append(this.m_Data.attribute(i).name() + ": \t");
/* 189:269 */         double weight = this.m_Weights.get(index++);
/* 190:270 */         if (weight >= 0.0D) {
/* 191:271 */           result.append(" ");
/* 192:    */         }
/* 193:273 */         result.append(weight + "\n");
/* 194:    */       }
/* 195:    */     }
/* 196:276 */     return result.toString();
/* 197:    */   }
/* 198:    */   
/* 199:    */   public String ridgeTipText()
/* 200:    */   {
/* 201:286 */     return "The value of the ridge parameter.";
/* 202:    */   }
/* 203:    */   
/* 204:    */   public double getRidge()
/* 205:    */   {
/* 206:296 */     return this.m_Ridge;
/* 207:    */   }
/* 208:    */   
/* 209:    */   public void setRidge(double newRidge)
/* 210:    */   {
/* 211:306 */     this.m_Ridge = newRidge;
/* 212:    */   }
/* 213:    */   
/* 214:    */   public Enumeration<Option> listOptions()
/* 215:    */   {
/* 216:316 */     java.util.Vector<Option> newVector = new java.util.Vector(7);
/* 217:    */     
/* 218:318 */     newVector.addElement(new Option("\tThe ridge parameter.\n\t(default is 1e-6)", "R", 0, "-R"));
/* 219:    */     
/* 220:    */ 
/* 221:    */ 
/* 222:    */ 
/* 223:323 */     newVector.addAll(Collections.list(super.listOptions()));
/* 224:    */     
/* 225:325 */     return newVector.elements();
/* 226:    */   }
/* 227:    */   
/* 228:    */   public void setOptions(String[] options)
/* 229:    */     throws Exception
/* 230:    */   {
/* 231:353 */     String ridgeString = Utils.getOption('R', options);
/* 232:354 */     if (ridgeString.length() != 0) {
/* 233:355 */       setRidge(Double.parseDouble(ridgeString));
/* 234:    */     } else {
/* 235:357 */       setRidge(1.0E-006D);
/* 236:    */     }
/* 237:360 */     super.setOptions(options);
/* 238:    */     
/* 239:362 */     Utils.checkForRemainingOptions(options);
/* 240:    */   }
/* 241:    */   
/* 242:    */   public String[] getOptions()
/* 243:    */   {
/* 244:372 */     java.util.Vector<String> options = new java.util.Vector();
/* 245:373 */     options.add("-R");options.add("" + getRidge());
/* 246:    */     
/* 247:375 */     Collections.addAll(options, super.getOptions());
/* 248:    */     
/* 249:377 */     return (String[])options.toArray(new String[0]);
/* 250:    */   }
/* 251:    */   
/* 252:    */   public String getRevision()
/* 253:    */   {
/* 254:387 */     return RevisionUtils.extract("$Revision: 10382 $");
/* 255:    */   }
/* 256:    */   
/* 257:    */   public static void main(String[] argv)
/* 258:    */   {
/* 259:396 */     runClassifier(new FLDA(), argv);
/* 260:    */   }
/* 261:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.FLDA
 * JD-Core Version:    0.7.0.1
 */