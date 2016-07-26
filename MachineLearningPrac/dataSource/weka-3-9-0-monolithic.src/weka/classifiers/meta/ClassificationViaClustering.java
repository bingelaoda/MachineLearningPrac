/*   1:    */ package weka.classifiers.meta;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.classifiers.AbstractClassifier;
/*   8:    */ import weka.classifiers.Classifier;
/*   9:    */ import weka.classifiers.rules.ZeroR;
/*  10:    */ import weka.clusterers.AbstractClusterer;
/*  11:    */ import weka.clusterers.ClusterEvaluation;
/*  12:    */ import weka.clusterers.Clusterer;
/*  13:    */ import weka.clusterers.SimpleKMeans;
/*  14:    */ import weka.core.Attribute;
/*  15:    */ import weka.core.Capabilities;
/*  16:    */ import weka.core.Capabilities.Capability;
/*  17:    */ import weka.core.DenseInstance;
/*  18:    */ import weka.core.Instance;
/*  19:    */ import weka.core.Instances;
/*  20:    */ import weka.core.Option;
/*  21:    */ import weka.core.OptionHandler;
/*  22:    */ import weka.core.RevisionUtils;
/*  23:    */ import weka.core.Utils;
/*  24:    */ 
/*  25:    */ public class ClassificationViaClustering
/*  26:    */   extends AbstractClassifier
/*  27:    */ {
/*  28:    */   private static final long serialVersionUID = -5687069451420259135L;
/*  29:    */   protected Clusterer m_Clusterer;
/*  30:    */   protected Clusterer m_ActualClusterer;
/*  31:    */   protected Instances m_OriginalHeader;
/*  32:    */   protected Instances m_ClusteringHeader;
/*  33:    */   protected double[] m_ClustersToClasses;
/*  34:    */   protected Classifier m_ZeroR;
/*  35:    */   
/*  36:    */   public ClassificationViaClustering()
/*  37:    */   {
/*  38:134 */     this.m_Clusterer = new SimpleKMeans();
/*  39:    */   }
/*  40:    */   
/*  41:    */   public String globalInfo()
/*  42:    */   {
/*  43:144 */     return "A simple meta-classifier that uses a clusterer for classification. For cluster algorithms that use a fixed number of clusterers, like SimpleKMeans, the user has to make sure that the number of clusters to generate are the same as the number of class labels in the dataset in order to obtain a useful model.\n\nNote: at prediction time, a missing value is returned if no cluster is found for the instance.\n\nThe code is based on the 'clusters to classes' functionality of the weka.clusterers.ClusterEvaluation class by Mark Hall.";
/*  44:    */   }
/*  45:    */   
/*  46:    */   public Enumeration<Option> listOptions()
/*  47:    */   {
/*  48:165 */     Vector<Option> result = new Vector();
/*  49:    */     
/*  50:167 */     result.addElement(new Option("\tFull name of clusterer.\n\t(default: " + defaultClustererString() + ")", "W", 1, "-W"));
/*  51:    */     
/*  52:    */ 
/*  53:170 */     result.addAll(Collections.list(super.listOptions()));
/*  54:    */     
/*  55:172 */     result.addElement(new Option("", "", 0, "\nOptions specific to clusterer " + this.m_Clusterer.getClass().getName() + ":"));
/*  56:    */     
/*  57:    */ 
/*  58:175 */     result.addAll(Collections.list(((OptionHandler)this.m_Clusterer).listOptions()));
/*  59:    */     
/*  60:    */ 
/*  61:178 */     return result.elements();
/*  62:    */   }
/*  63:    */   
/*  64:    */   public String[] getOptions()
/*  65:    */   {
/*  66:189 */     Vector<String> result = new Vector();
/*  67:    */     
/*  68:191 */     result.add("-W");
/*  69:192 */     result.add("" + getClusterer().getClass().getName());
/*  70:    */     
/*  71:194 */     Collections.addAll(result, super.getOptions());
/*  72:196 */     if ((getClusterer() instanceof OptionHandler))
/*  73:    */     {
/*  74:197 */       String[] options = ((OptionHandler)getClusterer()).getOptions();
/*  75:198 */       if (options.length > 0)
/*  76:    */       {
/*  77:199 */         result.add("--");
/*  78:200 */         Collections.addAll(result, options);
/*  79:    */       }
/*  80:    */     }
/*  81:204 */     return (String[])result.toArray(new String[result.size()]);
/*  82:    */   }
/*  83:    */   
/*  84:    */   public void setOptions(String[] options)
/*  85:    */     throws Exception
/*  86:    */   {
/*  87:261 */     String tmpStr = Utils.getOption('W', options);
/*  88:262 */     if (tmpStr.length() > 0)
/*  89:    */     {
/*  90:263 */       setClusterer(AbstractClusterer.forName(tmpStr, null));
/*  91:264 */       setClusterer(AbstractClusterer.forName(tmpStr, Utils.partitionOptions(options)));
/*  92:    */     }
/*  93:    */     else
/*  94:    */     {
/*  95:267 */       setClusterer(AbstractClusterer.forName(defaultClustererString(), null));
/*  96:268 */       setClusterer(AbstractClusterer.forName(defaultClustererString(), Utils.partitionOptions(options)));
/*  97:    */     }
/*  98:272 */     super.setOptions(options);
/*  99:    */     
/* 100:274 */     Utils.checkForRemainingOptions(options);
/* 101:    */   }
/* 102:    */   
/* 103:    */   protected String defaultClustererString()
/* 104:    */   {
/* 105:283 */     return SimpleKMeans.class.getName();
/* 106:    */   }
/* 107:    */   
/* 108:    */   public String clustererTipText()
/* 109:    */   {
/* 110:293 */     return "The clusterer to be used.";
/* 111:    */   }
/* 112:    */   
/* 113:    */   public void setClusterer(Clusterer value)
/* 114:    */   {
/* 115:302 */     this.m_Clusterer = value;
/* 116:    */   }
/* 117:    */   
/* 118:    */   public Clusterer getClusterer()
/* 119:    */   {
/* 120:311 */     return this.m_Clusterer;
/* 121:    */   }
/* 122:    */   
/* 123:    */   public double classifyInstance(Instance instance)
/* 124:    */     throws Exception
/* 125:    */   {
/* 126:    */     double result;
/* 127:    */     double result;
/* 128:330 */     if (this.m_ZeroR != null)
/* 129:    */     {
/* 130:331 */       result = this.m_ZeroR.classifyInstance(instance);
/* 131:    */     }
/* 132:333 */     else if (this.m_ActualClusterer != null)
/* 133:    */     {
/* 134:335 */       double[] values = new double[this.m_ClusteringHeader.numAttributes()];
/* 135:336 */       int n = 0;
/* 136:337 */       for (int i = 0; i < instance.numAttributes(); i++) {
/* 137:338 */         if (i != instance.classIndex())
/* 138:    */         {
/* 139:341 */           values[n] = instance.value(i);
/* 140:342 */           n++;
/* 141:    */         }
/* 142:    */       }
/* 143:344 */       Instance newInst = new DenseInstance(instance.weight(), values);
/* 144:345 */       newInst.setDataset(this.m_ClusteringHeader);
/* 145:    */       
/* 146:    */ 
/* 147:348 */       double result = this.m_ClustersToClasses[this.m_ActualClusterer.clusterInstance(newInst)];
/* 148:349 */       if (result == -1.0D) {
/* 149:350 */         result = Utils.missingValue();
/* 150:    */       }
/* 151:    */     }
/* 152:    */     else
/* 153:    */     {
/* 154:353 */       result = Utils.missingValue();
/* 155:    */     }
/* 156:357 */     return result;
/* 157:    */   }
/* 158:    */   
/* 159:    */   public Capabilities getCapabilities()
/* 160:    */   {
/* 161:369 */     Capabilities result = this.m_Clusterer.getCapabilities();
/* 162:    */     
/* 163:    */ 
/* 164:372 */     result.disableAllClasses();
/* 165:373 */     result.disable(Capabilities.Capability.NO_CLASS);
/* 166:374 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/* 167:    */     
/* 168:376 */     return result;
/* 169:    */   }
/* 170:    */   
/* 171:    */   public void buildClassifier(Instances data)
/* 172:    */     throws Exception
/* 173:    */   {
/* 174:398 */     getCapabilities().testWithFail(data);
/* 175:    */     
/* 176:    */ 
/* 177:401 */     data = new Instances(data);
/* 178:402 */     data.deleteWithMissingClass();
/* 179:    */     
/* 180:    */ 
/* 181:405 */     this.m_OriginalHeader = new Instances(data, 0);
/* 182:    */     
/* 183:    */ 
/* 184:408 */     Instances clusterData = new Instances(data);
/* 185:409 */     clusterData.setClassIndex(-1);
/* 186:410 */     clusterData.deleteAttributeAt(this.m_OriginalHeader.classIndex());
/* 187:411 */     this.m_ClusteringHeader = new Instances(clusterData, 0);
/* 188:413 */     if (this.m_ClusteringHeader.numAttributes() == 0)
/* 189:    */     {
/* 190:414 */       System.err.println("Data contains only class attribute, defaulting to ZeroR model.");
/* 191:    */       
/* 192:416 */       this.m_ZeroR = new ZeroR();
/* 193:417 */       this.m_ZeroR.buildClassifier(data);
/* 194:    */     }
/* 195:    */     else
/* 196:    */     {
/* 197:419 */       this.m_ZeroR = null;
/* 198:    */       
/* 199:    */ 
/* 200:422 */       this.m_ActualClusterer = AbstractClusterer.makeCopy(this.m_Clusterer);
/* 201:423 */       this.m_ActualClusterer.buildClusterer(clusterData);
/* 202:    */       
/* 203:    */ 
/* 204:426 */       ClusterEvaluation eval = new ClusterEvaluation();
/* 205:427 */       eval.setClusterer(this.m_ActualClusterer);
/* 206:428 */       eval.evaluateClusterer(clusterData);
/* 207:429 */       double[] clusterAssignments = eval.getClusterAssignments();
/* 208:    */       
/* 209:    */ 
/* 210:432 */       int[][] counts = new int[eval.getNumClusters()][this.m_OriginalHeader.numClasses()];
/* 211:433 */       int[] clusterTotals = new int[eval.getNumClusters()];
/* 212:434 */       double[] best = new double[eval.getNumClusters() + 1];
/* 213:435 */       double[] current = new double[eval.getNumClusters() + 1];
/* 214:436 */       for (int i = 0; i < data.numInstances(); i++)
/* 215:    */       {
/* 216:437 */         Instance instance = data.instance(i);
/* 217:438 */         counts[((int)clusterAssignments[i])][((int)instance.classValue())] += 1;
/* 218:439 */         clusterTotals[((int)clusterAssignments[i])] += 1;
/* 219:440 */         i++;
/* 220:    */       }
/* 221:442 */       best[eval.getNumClusters()] = 1.7976931348623157E+308D;
/* 222:443 */       ClusterEvaluation.mapClasses(eval.getNumClusters(), 0, counts, clusterTotals, current, best, 0);
/* 223:    */       
/* 224:445 */       this.m_ClustersToClasses = new double[best.length];
/* 225:446 */       System.arraycopy(best, 0, this.m_ClustersToClasses, 0, best.length);
/* 226:    */     }
/* 227:    */   }
/* 228:    */   
/* 229:    */   public String toString()
/* 230:    */   {
/* 231:462 */     StringBuffer result = new StringBuffer();
/* 232:    */     
/* 233:    */ 
/* 234:465 */     result.append(getClass().getName().replaceAll(".*\\.", "") + "\n");
/* 235:466 */     result.append(getClass().getName().replaceAll(".*\\.", "").replaceAll(".", "=") + "\n");
/* 236:471 */     if (this.m_ActualClusterer != null)
/* 237:    */     {
/* 238:473 */       result.append(this.m_ActualClusterer + "\n");
/* 239:    */       
/* 240:    */ 
/* 241:476 */       result.append("Clusters to classes mapping:\n");
/* 242:477 */       for (int i = 0; i < this.m_ClustersToClasses.length - 1; i++)
/* 243:    */       {
/* 244:478 */         result.append("  " + (i + 1) + ". Cluster: ");
/* 245:479 */         if (this.m_ClustersToClasses[i] < 0.0D) {
/* 246:480 */           result.append("no class");
/* 247:    */         } else {
/* 248:482 */           result.append(this.m_OriginalHeader.classAttribute().value((int)this.m_ClustersToClasses[i]) + " (" + ((int)this.m_ClustersToClasses[i] + 1) + ")");
/* 249:    */         }
/* 250:486 */         result.append("\n");
/* 251:    */       }
/* 252:488 */       result.append("\n");
/* 253:    */       
/* 254:    */ 
/* 255:491 */       result.append("Classes to clusters mapping:\n");
/* 256:492 */       for (i = 0; i < this.m_OriginalHeader.numClasses(); i++)
/* 257:    */       {
/* 258:493 */         result.append("  " + (i + 1) + ". Class (" + this.m_OriginalHeader.classAttribute().value(i) + "): ");
/* 259:    */         
/* 260:    */ 
/* 261:496 */         boolean found = false;
/* 262:497 */         for (int n = 0; n < this.m_ClustersToClasses.length - 1; n++) {
/* 263:498 */           if ((int)this.m_ClustersToClasses[n] == i)
/* 264:    */           {
/* 265:499 */             found = true;
/* 266:500 */             result.append(n + 1 + ". Cluster");
/* 267:501 */             break;
/* 268:    */           }
/* 269:    */         }
/* 270:505 */         if (!found) {
/* 271:506 */           result.append("no cluster");
/* 272:    */         }
/* 273:509 */         result.append("\n");
/* 274:    */       }
/* 275:512 */       result.append("\n");
/* 276:    */     }
/* 277:    */     else
/* 278:    */     {
/* 279:514 */       result.append("no model built yet\n");
/* 280:    */     }
/* 281:517 */     return result.toString();
/* 282:    */   }
/* 283:    */   
/* 284:    */   public String getRevision()
/* 285:    */   {
/* 286:527 */     return RevisionUtils.extract("$Revision: 10331 $");
/* 287:    */   }
/* 288:    */   
/* 289:    */   public static void main(String[] args)
/* 290:    */   {
/* 291:536 */     runClassifier(new ClassificationViaClustering(), args);
/* 292:    */   }
/* 293:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.meta.ClassificationViaClustering
 * JD-Core Version:    0.7.0.1
 */