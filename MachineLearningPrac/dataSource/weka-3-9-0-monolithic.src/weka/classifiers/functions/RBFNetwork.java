/*   1:    */ package weka.classifiers.functions;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.classifiers.AbstractClassifier;
/*   8:    */ import weka.classifiers.Classifier;
/*   9:    */ import weka.classifiers.rules.ZeroR;
/*  10:    */ import weka.clusterers.MakeDensityBasedClusterer;
/*  11:    */ import weka.clusterers.SimpleKMeans;
/*  12:    */ import weka.core.Attribute;
/*  13:    */ import weka.core.Capabilities;
/*  14:    */ import weka.core.Instance;
/*  15:    */ import weka.core.Instances;
/*  16:    */ import weka.core.Option;
/*  17:    */ import weka.core.OptionHandler;
/*  18:    */ import weka.core.RevisionUtils;
/*  19:    */ import weka.core.SelectedTag;
/*  20:    */ import weka.core.Utils;
/*  21:    */ import weka.core.WeightedInstancesHandler;
/*  22:    */ import weka.filters.Filter;
/*  23:    */ import weka.filters.unsupervised.attribute.ClusterMembership;
/*  24:    */ import weka.filters.unsupervised.attribute.Standardize;
/*  25:    */ 
/*  26:    */ public class RBFNetwork
/*  27:    */   extends AbstractClassifier
/*  28:    */   implements OptionHandler, WeightedInstancesHandler
/*  29:    */ {
/*  30:    */   static final long serialVersionUID = -3669814959712675720L;
/*  31:    */   private Logistic m_logistic;
/*  32:    */   private LinearRegression m_linear;
/*  33:    */   private ClusterMembership m_basisFilter;
/*  34:    */   private Standardize m_standardize;
/*  35:108 */   private int m_numClusters = 2;
/*  36:111 */   protected double m_ridge = 1.0E-008D;
/*  37:114 */   private int m_maxIts = -1;
/*  38:117 */   private int m_clusteringSeed = 1;
/*  39:120 */   private double m_minStdDev = 0.1D;
/*  40:    */   private Classifier m_ZeroR;
/*  41:    */   
/*  42:    */   public String globalInfo()
/*  43:    */   {
/*  44:132 */     return "Class that implements a normalized Gaussian radial basisbasis function network.\nIt uses the k-means clustering algorithm to provide the basis functions and learns either a logistic regression (discrete class problems) or linear regression (numeric class problems) on top of that. Symmetric multivariate Gaussians are fit to the data from each cluster. If the class is nominal it uses the given number of clusters per class.It standardizes all numeric attributes to zero mean and unit variance.";
/*  45:    */   }
/*  46:    */   
/*  47:    */   public Capabilities getCapabilities()
/*  48:    */   {
/*  49:154 */     Capabilities result = new Logistic().getCapabilities();
/*  50:155 */     result.or(new LinearRegression().getCapabilities());
/*  51:156 */     Capabilities classes = result.getClassCapabilities();
/*  52:157 */     result.and(new SimpleKMeans().getCapabilities());
/*  53:158 */     result.or(classes);
/*  54:159 */     return result;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public void buildClassifier(Instances instances)
/*  58:    */     throws Exception
/*  59:    */   {
/*  60:172 */     getCapabilities().testWithFail(instances);
/*  61:    */     
/*  62:    */ 
/*  63:175 */     instances = new Instances(instances);
/*  64:176 */     instances.deleteWithMissingClass();
/*  65:179 */     if (instances.numAttributes() == 1)
/*  66:    */     {
/*  67:180 */       System.err.println("Cannot build model (only class attribute present in data!), using ZeroR model instead!");
/*  68:    */       
/*  69:    */ 
/*  70:183 */       this.m_ZeroR = new ZeroR();
/*  71:184 */       this.m_ZeroR.buildClassifier(instances);
/*  72:185 */       return;
/*  73:    */     }
/*  74:187 */     this.m_ZeroR = null;
/*  75:    */     
/*  76:    */ 
/*  77:190 */     this.m_standardize = new Standardize();
/*  78:191 */     this.m_standardize.setInputFormat(instances);
/*  79:192 */     instances = Filter.useFilter(instances, this.m_standardize);
/*  80:    */     
/*  81:194 */     SimpleKMeans sk = new SimpleKMeans();
/*  82:195 */     sk.setNumClusters(this.m_numClusters);
/*  83:196 */     sk.setSeed(this.m_clusteringSeed);
/*  84:197 */     MakeDensityBasedClusterer dc = new MakeDensityBasedClusterer();
/*  85:198 */     dc.setClusterer(sk);
/*  86:199 */     dc.setMinStdDev(this.m_minStdDev);
/*  87:200 */     this.m_basisFilter = new ClusterMembership();
/*  88:201 */     this.m_basisFilter.setDensityBasedClusterer(dc);
/*  89:202 */     this.m_basisFilter.setInputFormat(instances);
/*  90:203 */     Instances transformed = Filter.useFilter(instances, this.m_basisFilter);
/*  91:205 */     if (instances.classAttribute().isNominal())
/*  92:    */     {
/*  93:206 */       this.m_linear = null;
/*  94:207 */       this.m_logistic = new Logistic();
/*  95:208 */       this.m_logistic.setRidge(this.m_ridge);
/*  96:209 */       this.m_logistic.setMaxIts(this.m_maxIts);
/*  97:210 */       this.m_logistic.buildClassifier(transformed);
/*  98:    */     }
/*  99:    */     else
/* 100:    */     {
/* 101:212 */       this.m_logistic = null;
/* 102:213 */       this.m_linear = new LinearRegression();
/* 103:214 */       this.m_linear.setAttributeSelectionMethod(new SelectedTag(1, LinearRegression.TAGS_SELECTION));
/* 104:    */       
/* 105:216 */       this.m_linear.setRidge(this.m_ridge);
/* 106:217 */       this.m_linear.buildClassifier(transformed);
/* 107:    */     }
/* 108:    */   }
/* 109:    */   
/* 110:    */   public double[] distributionForInstance(Instance instance)
/* 111:    */     throws Exception
/* 112:    */   {
/* 113:232 */     if (this.m_ZeroR != null) {
/* 114:233 */       return this.m_ZeroR.distributionForInstance(instance);
/* 115:    */     }
/* 116:236 */     this.m_standardize.input(instance);
/* 117:237 */     this.m_basisFilter.input(this.m_standardize.output());
/* 118:238 */     Instance transformed = this.m_basisFilter.output();
/* 119:    */     
/* 120:240 */     return instance.classAttribute().isNominal() ? this.m_logistic.distributionForInstance(transformed) : this.m_linear.distributionForInstance(transformed);
/* 121:    */   }
/* 122:    */   
/* 123:    */   public String toString()
/* 124:    */   {
/* 125:254 */     if (this.m_ZeroR != null)
/* 126:    */     {
/* 127:255 */       StringBuffer buf = new StringBuffer();
/* 128:256 */       buf.append(getClass().getName().replaceAll(".*\\.", "") + "\n");
/* 129:257 */       buf.append(getClass().getName().replaceAll(".*\\.", "").replaceAll(".", "=") + "\n\n");
/* 130:    */       
/* 131:    */ 
/* 132:260 */       buf.append("Warning: No model could be built, hence ZeroR model is used:\n\n");
/* 133:    */       
/* 134:262 */       buf.append(this.m_ZeroR.toString());
/* 135:263 */       return buf.toString();
/* 136:    */     }
/* 137:266 */     if (this.m_basisFilter == null) {
/* 138:267 */       return "No classifier built yet!";
/* 139:    */     }
/* 140:270 */     StringBuffer sb = new StringBuffer();
/* 141:271 */     sb.append("Radial basis function network\n");
/* 142:272 */     sb.append(this.m_linear == null ? "(Logistic regression " : "(Linear regression ");
/* 143:    */     
/* 144:274 */     sb.append("applied to K-means clusters as basis functions):\n\n");
/* 145:275 */     sb.append(this.m_linear == null ? this.m_logistic.toString() : this.m_linear.toString());
/* 146:276 */     return sb.toString();
/* 147:    */   }
/* 148:    */   
/* 149:    */   public String maxItsTipText()
/* 150:    */   {
/* 151:286 */     return "Maximum number of iterations for the logistic regression to perform. Only applied to discrete class problems.";
/* 152:    */   }
/* 153:    */   
/* 154:    */   public int getMaxIts()
/* 155:    */   {
/* 156:297 */     return this.m_maxIts;
/* 157:    */   }
/* 158:    */   
/* 159:    */   public void setMaxIts(int newMaxIts)
/* 160:    */   {
/* 161:307 */     this.m_maxIts = newMaxIts;
/* 162:    */   }
/* 163:    */   
/* 164:    */   public String ridgeTipText()
/* 165:    */   {
/* 166:317 */     return "Set the Ridge value for the logistic or linear regression.";
/* 167:    */   }
/* 168:    */   
/* 169:    */   public void setRidge(double ridge)
/* 170:    */   {
/* 171:326 */     this.m_ridge = ridge;
/* 172:    */   }
/* 173:    */   
/* 174:    */   public double getRidge()
/* 175:    */   {
/* 176:335 */     return this.m_ridge;
/* 177:    */   }
/* 178:    */   
/* 179:    */   public String numClustersTipText()
/* 180:    */   {
/* 181:345 */     return "The number of clusters for K-Means to generate.";
/* 182:    */   }
/* 183:    */   
/* 184:    */   public void setNumClusters(int numClusters)
/* 185:    */   {
/* 186:354 */     if (numClusters > 0) {
/* 187:355 */       this.m_numClusters = numClusters;
/* 188:    */     }
/* 189:    */   }
/* 190:    */   
/* 191:    */   public int getNumClusters()
/* 192:    */   {
/* 193:365 */     return this.m_numClusters;
/* 194:    */   }
/* 195:    */   
/* 196:    */   public String clusteringSeedTipText()
/* 197:    */   {
/* 198:375 */     return "The random seed to pass on to K-means.";
/* 199:    */   }
/* 200:    */   
/* 201:    */   public void setClusteringSeed(int seed)
/* 202:    */   {
/* 203:384 */     this.m_clusteringSeed = seed;
/* 204:    */   }
/* 205:    */   
/* 206:    */   public int getClusteringSeed()
/* 207:    */   {
/* 208:393 */     return this.m_clusteringSeed;
/* 209:    */   }
/* 210:    */   
/* 211:    */   public String minStdDevTipText()
/* 212:    */   {
/* 213:403 */     return "Sets the minimum standard deviation for the clusters.";
/* 214:    */   }
/* 215:    */   
/* 216:    */   public double getMinStdDev()
/* 217:    */   {
/* 218:412 */     return this.m_minStdDev;
/* 219:    */   }
/* 220:    */   
/* 221:    */   public void setMinStdDev(double newMinStdDev)
/* 222:    */   {
/* 223:421 */     this.m_minStdDev = newMinStdDev;
/* 224:    */   }
/* 225:    */   
/* 226:    */   public Enumeration<Option> listOptions()
/* 227:    */   {
/* 228:431 */     Vector<Option> newVector = new Vector(5);
/* 229:    */     
/* 230:433 */     newVector.addElement(new Option("\tSet the number of clusters (basis functions) to generate. (default = 2).", "B", 1, "-B <number>"));
/* 231:    */     
/* 232:    */ 
/* 233:436 */     newVector.addElement(new Option("\tSet the random seed to be used by K-means. (default = 1).", "S", 1, "-S <seed>"));
/* 234:    */     
/* 235:    */ 
/* 236:439 */     newVector.addElement(new Option("\tSet the ridge value for the logistic or linear regression.", "R", 1, "-R <ridge>"));
/* 237:    */     
/* 238:    */ 
/* 239:442 */     newVector.addElement(new Option("\tSet the maximum number of iterations for the logistic regression. (default -1, until convergence).", "M", 1, "-M <number>"));
/* 240:    */     
/* 241:    */ 
/* 242:445 */     newVector.addElement(new Option("\tSet the minimum standard deviation for the clusters. (default 0.1).", "W", 1, "-W <number>"));
/* 243:    */     
/* 244:    */ 
/* 245:    */ 
/* 246:449 */     newVector.addAll(Collections.list(super.listOptions()));
/* 247:    */     
/* 248:451 */     return newVector.elements();
/* 249:    */   }
/* 250:    */   
/* 251:    */   public void setOptions(String[] options)
/* 252:    */     throws Exception
/* 253:    */   {
/* 254:493 */     setDebug(Utils.getFlag('D', options));
/* 255:    */     
/* 256:495 */     String ridgeString = Utils.getOption('R', options);
/* 257:496 */     if (ridgeString.length() != 0) {
/* 258:497 */       this.m_ridge = Double.parseDouble(ridgeString);
/* 259:    */     } else {
/* 260:499 */       this.m_ridge = 1.0E-008D;
/* 261:    */     }
/* 262:502 */     String maxItsString = Utils.getOption('M', options);
/* 263:503 */     if (maxItsString.length() != 0) {
/* 264:504 */       this.m_maxIts = Integer.parseInt(maxItsString);
/* 265:    */     } else {
/* 266:506 */       this.m_maxIts = -1;
/* 267:    */     }
/* 268:509 */     String numClustersString = Utils.getOption('B', options);
/* 269:510 */     if (numClustersString.length() != 0) {
/* 270:511 */       setNumClusters(Integer.parseInt(numClustersString));
/* 271:    */     }
/* 272:514 */     String seedString = Utils.getOption('S', options);
/* 273:515 */     if (seedString.length() != 0) {
/* 274:516 */       setClusteringSeed(Integer.parseInt(seedString));
/* 275:    */     }
/* 276:518 */     String stdString = Utils.getOption('W', options);
/* 277:519 */     if (stdString.length() != 0) {
/* 278:520 */       setMinStdDev(Double.parseDouble(stdString));
/* 279:    */     }
/* 280:523 */     super.setOptions(options);
/* 281:    */     
/* 282:525 */     Utils.checkForRemainingOptions(options);
/* 283:    */   }
/* 284:    */   
/* 285:    */   public String[] getOptions()
/* 286:    */   {
/* 287:536 */     Vector<String> options = new Vector();
/* 288:    */     
/* 289:538 */     options.add("-B");
/* 290:539 */     options.add("" + this.m_numClusters);
/* 291:540 */     options.add("-S");
/* 292:541 */     options.add("" + this.m_clusteringSeed);
/* 293:542 */     options.add("-R");
/* 294:543 */     options.add("" + this.m_ridge);
/* 295:544 */     options.add("-M");
/* 296:545 */     options.add("" + this.m_maxIts);
/* 297:546 */     options.add("-W");
/* 298:547 */     options.add("" + this.m_minStdDev);
/* 299:    */     
/* 300:549 */     Collections.addAll(options, super.getOptions());
/* 301:    */     
/* 302:551 */     return (String[])options.toArray(new String[0]);
/* 303:    */   }
/* 304:    */   
/* 305:    */   public String getRevision()
/* 306:    */   {
/* 307:561 */     return RevisionUtils.extract("$Revision: 10951 $");
/* 308:    */   }
/* 309:    */   
/* 310:    */   public static void main(String[] argv)
/* 311:    */   {
/* 312:571 */     runClassifier(new RBFNetwork(), argv);
/* 313:    */   }
/* 314:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.RBFNetwork
 * JD-Core Version:    0.7.0.1
 */