/*   1:    */ package weka.clusterers;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.core.Attribute;
/*   7:    */ import weka.core.AttributeStats;
/*   8:    */ import weka.core.Capabilities;
/*   9:    */ import weka.core.Capabilities.Capability;
/*  10:    */ import weka.core.Instance;
/*  11:    */ import weka.core.Instances;
/*  12:    */ import weka.core.Option;
/*  13:    */ import weka.core.OptionHandler;
/*  14:    */ import weka.core.RevisionUtils;
/*  15:    */ import weka.core.Utils;
/*  16:    */ import weka.core.WeightedInstancesHandler;
/*  17:    */ import weka.estimators.DiscreteEstimator;
/*  18:    */ import weka.experiment.Stats;
/*  19:    */ import weka.filters.Filter;
/*  20:    */ import weka.filters.unsupervised.attribute.ReplaceMissingValues;
/*  21:    */ 
/*  22:    */ public class MakeDensityBasedClusterer
/*  23:    */   extends AbstractDensityBasedClusterer
/*  24:    */   implements NumberOfClustersRequestable, OptionHandler, WeightedInstancesHandler
/*  25:    */ {
/*  26:    */   static final long serialVersionUID = -5643302427972186631L;
/*  27:    */   private Instances m_theInstances;
/*  28:    */   private double[] m_priors;
/*  29:    */   private double[][][] m_modelNormal;
/*  30:    */   private DiscreteEstimator[][] m_model;
/*  31:114 */   private double m_minStdDev = 1.0E-006D;
/*  32:116 */   private Clusterer m_wrappedClusterer = new SimpleKMeans();
/*  33:    */   private ReplaceMissingValues m_replaceMissing;
/*  34:    */   
/*  35:    */   public MakeDensityBasedClusterer() {}
/*  36:    */   
/*  37:    */   public MakeDensityBasedClusterer(Clusterer toWrap)
/*  38:    */   {
/*  39:135 */     setClusterer(toWrap);
/*  40:    */   }
/*  41:    */   
/*  42:    */   public String globalInfo()
/*  43:    */   {
/*  44:145 */     return "Class for wrapping a Clusterer to make it return a distribution and density. Fits normal distributions and discrete distributions within each cluster produced by the wrapped clusterer. Supports the NumberOfClustersRequestable interface only if the wrapped Clusterer does.";
/*  45:    */   }
/*  46:    */   
/*  47:    */   protected String defaultClustererString()
/*  48:    */   {
/*  49:158 */     return SimpleKMeans.class.getName();
/*  50:    */   }
/*  51:    */   
/*  52:    */   public void setNumClusters(int n)
/*  53:    */     throws Exception
/*  54:    */   {
/*  55:170 */     if (this.m_wrappedClusterer == null) {
/*  56:171 */       throw new Exception("Can't set the number of clusters to generate - no clusterer has been set yet.");
/*  57:    */     }
/*  58:174 */     if (!(this.m_wrappedClusterer instanceof NumberOfClustersRequestable)) {
/*  59:175 */       throw new Exception("Can't set the number of clusters to generate - wrapped clusterer does not support this facility.");
/*  60:    */     }
/*  61:179 */     ((NumberOfClustersRequestable)this.m_wrappedClusterer).setNumClusters(n);
/*  62:    */   }
/*  63:    */   
/*  64:    */   public Capabilities getCapabilities()
/*  65:    */   {
/*  66:190 */     if (this.m_wrappedClusterer != null) {
/*  67:191 */       return this.m_wrappedClusterer.getCapabilities();
/*  68:    */     }
/*  69:193 */     Capabilities result = super.getCapabilities();
/*  70:194 */     result.disableAll();
/*  71:195 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  72:    */     
/*  73:197 */     return result;
/*  74:    */   }
/*  75:    */   
/*  76:    */   public void buildClusterer(Instances data)
/*  77:    */     throws Exception
/*  78:    */   {
/*  79:209 */     getCapabilities().testWithFail(data);
/*  80:    */     
/*  81:211 */     this.m_replaceMissing = new ReplaceMissingValues();
/*  82:212 */     this.m_replaceMissing.setInputFormat(data);
/*  83:213 */     data = Filter.useFilter(data, this.m_replaceMissing);
/*  84:    */     
/*  85:215 */     this.m_theInstances = new Instances(data, 0);
/*  86:216 */     if (this.m_wrappedClusterer == null) {
/*  87:217 */       throw new Exception("No clusterer has been set");
/*  88:    */     }
/*  89:219 */     this.m_wrappedClusterer.buildClusterer(data);
/*  90:220 */     this.m_model = new DiscreteEstimator[this.m_wrappedClusterer.numberOfClusters()][data.numAttributes()];
/*  91:    */     
/*  92:222 */     this.m_modelNormal = new double[this.m_wrappedClusterer.numberOfClusters()][data.numAttributes()][2];
/*  93:    */     
/*  94:224 */     double[][] weights = new double[this.m_wrappedClusterer.numberOfClusters()][data.numAttributes()];
/*  95:    */     
/*  96:226 */     this.m_priors = new double[this.m_wrappedClusterer.numberOfClusters()];
/*  97:227 */     for (int i = 0; i < this.m_wrappedClusterer.numberOfClusters(); i++)
/*  98:    */     {
/*  99:228 */       this.m_priors[i] = 1.0D;
/* 100:229 */       for (int j = 0; j < data.numAttributes(); j++) {
/* 101:230 */         if (data.attribute(j).isNominal()) {
/* 102:231 */           this.m_model[i][j] = new DiscreteEstimator(data.attribute(j).numValues(), true);
/* 103:    */         }
/* 104:    */       }
/* 105:    */     }
/* 106:237 */     Instance inst = null;
/* 107:    */     
/* 108:    */ 
/* 109:240 */     int[] clusterIndex = new int[data.numInstances()];
/* 110:241 */     for (int i = 0; i < data.numInstances(); i++)
/* 111:    */     {
/* 112:242 */       inst = data.instance(i);
/* 113:243 */       int cluster = this.m_wrappedClusterer.clusterInstance(inst);
/* 114:244 */       this.m_priors[cluster] += inst.weight();
/* 115:245 */       for (int j = 0; j < data.numAttributes(); j++) {
/* 116:246 */         if (!inst.isMissing(j)) {
/* 117:247 */           if (data.attribute(j).isNominal())
/* 118:    */           {
/* 119:248 */             this.m_model[cluster][j].addValue(inst.value(j), inst.weight());
/* 120:    */           }
/* 121:    */           else
/* 122:    */           {
/* 123:250 */             this.m_modelNormal[cluster][j][0] += inst.weight() * inst.value(j);
/* 124:251 */             weights[cluster][j] += inst.weight();
/* 125:    */           }
/* 126:    */         }
/* 127:    */       }
/* 128:255 */       clusterIndex[i] = cluster;
/* 129:    */     }
/* 130:258 */     for (int j = 0; j < data.numAttributes(); j++) {
/* 131:259 */       if (data.attribute(j).isNumeric()) {
/* 132:260 */         for (int i = 0; i < this.m_wrappedClusterer.numberOfClusters(); i++) {
/* 133:261 */           if (weights[i][j] > 0.0D) {
/* 134:262 */             this.m_modelNormal[i][j][0] /= weights[i][j];
/* 135:    */           }
/* 136:    */         }
/* 137:    */       }
/* 138:    */     }
/* 139:269 */     for (int i = 0; i < data.numInstances(); i++)
/* 140:    */     {
/* 141:270 */       inst = data.instance(i);
/* 142:271 */       for (int j = 0; j < data.numAttributes(); j++) {
/* 143:272 */         if ((!inst.isMissing(j)) && 
/* 144:273 */           (data.attribute(j).isNumeric()))
/* 145:    */         {
/* 146:274 */           double diff = this.m_modelNormal[clusterIndex[i]][j][0] - inst.value(j);
/* 147:275 */           this.m_modelNormal[clusterIndex[i]][j][1] += inst.weight() * diff * diff;
/* 148:    */         }
/* 149:    */       }
/* 150:    */     }
/* 151:281 */     for (int j = 0; j < data.numAttributes(); j++) {
/* 152:282 */       if (data.attribute(j).isNumeric()) {
/* 153:283 */         for (int i = 0; i < this.m_wrappedClusterer.numberOfClusters(); i++)
/* 154:    */         {
/* 155:284 */           if (weights[i][j] > 0.0D) {
/* 156:285 */             this.m_modelNormal[i][j][1] = Math.sqrt(this.m_modelNormal[i][j][1] / weights[i][j]);
/* 157:287 */           } else if (weights[i][j] <= 0.0D) {
/* 158:288 */             this.m_modelNormal[i][j][1] = 1.7976931348623157E+308D;
/* 159:    */           }
/* 160:290 */           if (this.m_modelNormal[i][j][1] <= this.m_minStdDev)
/* 161:    */           {
/* 162:291 */             this.m_modelNormal[i][j][1] = data.attributeStats(j).numericStats.stdDev;
/* 163:292 */             if (this.m_modelNormal[i][j][1] <= this.m_minStdDev) {
/* 164:293 */               this.m_modelNormal[i][j][1] = this.m_minStdDev;
/* 165:    */             }
/* 166:    */           }
/* 167:    */         }
/* 168:    */       }
/* 169:    */     }
/* 170:300 */     Utils.normalize(this.m_priors);
/* 171:    */   }
/* 172:    */   
/* 173:    */   public double[] clusterPriors()
/* 174:    */   {
/* 175:311 */     double[] n = new double[this.m_priors.length];
/* 176:    */     
/* 177:313 */     System.arraycopy(this.m_priors, 0, n, 0, n.length);
/* 178:314 */     return n;
/* 179:    */   }
/* 180:    */   
/* 181:    */   public double[] logDensityPerClusterForInstance(Instance inst)
/* 182:    */     throws Exception
/* 183:    */   {
/* 184:331 */     double[] wghts = new double[this.m_wrappedClusterer.numberOfClusters()];
/* 185:    */     
/* 186:333 */     this.m_replaceMissing.input(inst);
/* 187:334 */     inst = this.m_replaceMissing.output();
/* 188:336 */     for (int i = 0; i < this.m_wrappedClusterer.numberOfClusters(); i++)
/* 189:    */     {
/* 190:337 */       double logprob = 0.0D;
/* 191:338 */       for (int j = 0; j < inst.numAttributes(); j++) {
/* 192:339 */         if (!inst.isMissing(j)) {
/* 193:340 */           if (inst.attribute(j).isNominal()) {
/* 194:341 */             logprob += Math.log(this.m_model[i][j].getProbability(inst.value(j)));
/* 195:    */           } else {
/* 196:343 */             logprob += logNormalDens(inst.value(j), this.m_modelNormal[i][j][0], this.m_modelNormal[i][j][1]);
/* 197:    */           }
/* 198:    */         }
/* 199:    */       }
/* 200:348 */       wghts[i] = logprob;
/* 201:    */     }
/* 202:350 */     return wghts;
/* 203:    */   }
/* 204:    */   
/* 205:354 */   private static double m_normConst = 0.5D * Math.log(6.283185307179586D);
/* 206:    */   
/* 207:    */   private double logNormalDens(double x, double mean, double stdDev)
/* 208:    */   {
/* 209:366 */     double diff = x - mean;
/* 210:    */     
/* 211:368 */     return -(diff * diff / (2.0D * stdDev * stdDev)) - m_normConst - Math.log(stdDev);
/* 212:    */   }
/* 213:    */   
/* 214:    */   public int numberOfClusters()
/* 215:    */     throws Exception
/* 216:    */   {
/* 217:381 */     return this.m_wrappedClusterer.numberOfClusters();
/* 218:    */   }
/* 219:    */   
/* 220:    */   public String toString()
/* 221:    */   {
/* 222:391 */     if (this.m_priors == null) {
/* 223:392 */       return "No clusterer built yet!";
/* 224:    */     }
/* 225:395 */     StringBuffer text = new StringBuffer();
/* 226:396 */     text.append("MakeDensityBasedClusterer: \n\nWrapped clusterer: " + this.m_wrappedClusterer.toString());
/* 227:    */     
/* 228:    */ 
/* 229:399 */     text.append("\nFitted estimators (with ML estimates of variance):\n");
/* 230:401 */     for (int j = 0; j < this.m_priors.length; j++)
/* 231:    */     {
/* 232:402 */       text.append("\nCluster: " + j + " Prior probability: " + Utils.doubleToString(this.m_priors[j], 4) + "\n\n");
/* 233:405 */       for (int i = 0; i < this.m_model[0].length; i++)
/* 234:    */       {
/* 235:406 */         text.append("Attribute: " + this.m_theInstances.attribute(i).name() + "\n");
/* 236:408 */         if (this.m_theInstances.attribute(i).isNominal())
/* 237:    */         {
/* 238:409 */           if (this.m_model[j][i] != null) {
/* 239:410 */             text.append(this.m_model[j][i].toString());
/* 240:    */           }
/* 241:    */         }
/* 242:    */         else {
/* 243:413 */           text.append("Normal Distribution. Mean = " + Utils.doubleToString(this.m_modelNormal[j][i][0], 4) + " StdDev = " + Utils.doubleToString(this.m_modelNormal[j][i][1], 4) + "\n");
/* 244:    */         }
/* 245:    */       }
/* 246:    */     }
/* 247:420 */     return text.toString();
/* 248:    */   }
/* 249:    */   
/* 250:    */   public String clustererTipText()
/* 251:    */   {
/* 252:430 */     return "the clusterer to wrap";
/* 253:    */   }
/* 254:    */   
/* 255:    */   public void setClusterer(Clusterer toWrap)
/* 256:    */   {
/* 257:440 */     this.m_wrappedClusterer = toWrap;
/* 258:    */   }
/* 259:    */   
/* 260:    */   public Clusterer getClusterer()
/* 261:    */   {
/* 262:450 */     return this.m_wrappedClusterer;
/* 263:    */   }
/* 264:    */   
/* 265:    */   public String minStdDevTipText()
/* 266:    */   {
/* 267:460 */     return "set minimum allowable standard deviation";
/* 268:    */   }
/* 269:    */   
/* 270:    */   public void setMinStdDev(double m)
/* 271:    */   {
/* 272:472 */     this.m_minStdDev = m;
/* 273:    */   }
/* 274:    */   
/* 275:    */   public double getMinStdDev()
/* 276:    */   {
/* 277:481 */     return this.m_minStdDev;
/* 278:    */   }
/* 279:    */   
/* 280:    */   public Enumeration<Option> listOptions()
/* 281:    */   {
/* 282:491 */     Vector<Option> result = new Vector();
/* 283:    */     
/* 284:493 */     result.addElement(new Option("\tminimum allowable standard deviation for normal density computation \n\t(default 1e-6)", "M", 1, "-M <num>"));
/* 285:    */     
/* 286:    */ 
/* 287:    */ 
/* 288:497 */     result.addElement(new Option("\tClusterer to wrap.\n\t(default " + defaultClustererString() + ")", "W", 1, "-W <clusterer name>"));
/* 289:    */     
/* 290:    */ 
/* 291:500 */     result.addAll(Collections.list(super.listOptions()));
/* 292:502 */     if ((this.m_wrappedClusterer != null) && ((this.m_wrappedClusterer instanceof OptionHandler)))
/* 293:    */     {
/* 294:504 */       result.addElement(new Option("", "", 0, "\nOptions specific to clusterer " + this.m_wrappedClusterer.getClass().getName() + ":"));
/* 295:    */       
/* 296:    */ 
/* 297:507 */       result.addAll(Collections.list(((OptionHandler)this.m_wrappedClusterer).listOptions()));
/* 298:    */     }
/* 299:511 */     return result.elements();
/* 300:    */   }
/* 301:    */   
/* 302:    */   public void setOptions(String[] options)
/* 303:    */     throws Exception
/* 304:    */   {
/* 305:567 */     String optionString = Utils.getOption('M', options);
/* 306:568 */     if (optionString.length() != 0) {
/* 307:569 */       setMinStdDev(new Double(optionString).doubleValue());
/* 308:    */     } else {
/* 309:571 */       setMinStdDev(1.0E-006D);
/* 310:    */     }
/* 311:574 */     String wString = Utils.getOption('W', options);
/* 312:575 */     if (wString.length() == 0) {
/* 313:576 */       wString = defaultClustererString();
/* 314:    */     }
/* 315:578 */     setClusterer(AbstractClusterer.forName(wString, Utils.partitionOptions(options)));
/* 316:    */     
/* 317:    */ 
/* 318:581 */     super.setOptions(options);
/* 319:    */     
/* 320:583 */     Utils.checkForRemainingOptions(options);
/* 321:    */   }
/* 322:    */   
/* 323:    */   public String[] getOptions()
/* 324:    */   {
/* 325:594 */     Vector<String> options = new Vector();
/* 326:    */     
/* 327:596 */     options.add("-M");
/* 328:597 */     options.add("" + getMinStdDev());
/* 329:599 */     if (getClusterer() != null)
/* 330:    */     {
/* 331:600 */       options.add("-W");
/* 332:601 */       options.add(getClusterer().getClass().getName());
/* 333:602 */       if ((this.m_wrappedClusterer instanceof OptionHandler))
/* 334:    */       {
/* 335:603 */         String[] clustererOptions = ((OptionHandler)this.m_wrappedClusterer).getOptions();
/* 336:605 */         if (clustererOptions.length > 0)
/* 337:    */         {
/* 338:606 */           options.add("--");
/* 339:607 */           Collections.addAll(options, clustererOptions);
/* 340:    */         }
/* 341:    */       }
/* 342:    */     }
/* 343:612 */     Collections.addAll(options, super.getOptions());
/* 344:    */     
/* 345:614 */     return (String[])options.toArray(new String[0]);
/* 346:    */   }
/* 347:    */   
/* 348:    */   public String getRevision()
/* 349:    */   {
/* 350:624 */     return RevisionUtils.extract("$Revision: 10203 $");
/* 351:    */   }
/* 352:    */   
/* 353:    */   public static void main(String[] argv)
/* 354:    */   {
/* 355:633 */     runClusterer(new MakeDensityBasedClusterer(), argv);
/* 356:    */   }
/* 357:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.clusterers.MakeDensityBasedClusterer
 * JD-Core Version:    0.7.0.1
 */