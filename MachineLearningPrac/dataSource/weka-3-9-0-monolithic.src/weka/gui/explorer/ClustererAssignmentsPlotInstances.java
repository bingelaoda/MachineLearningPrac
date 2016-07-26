/*   1:    */ package weka.gui.explorer;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import weka.clusterers.ClusterEvaluation;
/*   5:    */ import weka.clusterers.Clusterer;
/*   6:    */ import weka.core.Attribute;
/*   7:    */ import weka.core.DenseInstance;
/*   8:    */ import weka.core.Instance;
/*   9:    */ import weka.core.Instances;
/*  10:    */ import weka.core.Utils;
/*  11:    */ import weka.gui.visualize.PlotData2D;
/*  12:    */ 
/*  13:    */ public class ClustererAssignmentsPlotInstances
/*  14:    */   extends AbstractPlotInstances
/*  15:    */ {
/*  16:    */   private static final long serialVersionUID = -4748134272046520423L;
/*  17:    */   protected int[] m_PlotShapes;
/*  18:    */   protected Clusterer m_Clusterer;
/*  19:    */   protected ClusterEvaluation m_Evaluation;
/*  20:    */   
/*  21:    */   protected void initialize()
/*  22:    */   {
/*  23: 84 */     super.initialize();
/*  24:    */     
/*  25: 86 */     this.m_PlotShapes = null;
/*  26: 87 */     this.m_Clusterer = null;
/*  27: 88 */     this.m_Evaluation = null;
/*  28:    */   }
/*  29:    */   
/*  30:    */   public void setClusterer(Clusterer value)
/*  31:    */   {
/*  32: 97 */     this.m_Clusterer = value;
/*  33:    */   }
/*  34:    */   
/*  35:    */   public Clusterer getClusterer()
/*  36:    */   {
/*  37:106 */     return this.m_Clusterer;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public void setClusterEvaluation(ClusterEvaluation value)
/*  41:    */   {
/*  42:115 */     this.m_Evaluation = value;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public ClusterEvaluation getClusterEvaluation()
/*  46:    */   {
/*  47:124 */     return this.m_Evaluation;
/*  48:    */   }
/*  49:    */   
/*  50:    */   protected void check()
/*  51:    */   {
/*  52:132 */     super.check();
/*  53:134 */     if (this.m_Clusterer == null) {
/*  54:135 */       throw new IllegalStateException("No clusterer set!");
/*  55:    */     }
/*  56:138 */     if (this.m_Evaluation == null) {
/*  57:139 */       throw new IllegalStateException("No cluster evaluation set!");
/*  58:    */     }
/*  59:    */   }
/*  60:    */   
/*  61:    */   protected void determineFormat()
/*  62:    */   {
/*  63:154 */     int numClusters = this.m_Evaluation.getNumClusters();
/*  64:155 */     ArrayList<Attribute> hv = new ArrayList();
/*  65:156 */     ArrayList<String> clustVals = new ArrayList();
/*  66:158 */     for (int i = 0; i < numClusters; i++) {
/*  67:159 */       clustVals.add("cluster" + i);
/*  68:    */     }
/*  69:161 */     Attribute predictedCluster = new Attribute("Cluster", clustVals);
/*  70:162 */     for (i = 0; i < this.m_Instances.numAttributes(); i++) {
/*  71:163 */       hv.add((Attribute)this.m_Instances.attribute(i).copy());
/*  72:    */     }
/*  73:165 */     hv.add(predictedCluster);
/*  74:    */     
/*  75:167 */     this.m_PlotInstances = new Instances(this.m_Instances.relationName() + "_clustered", hv, this.m_Instances.numInstances());
/*  76:    */   }
/*  77:    */   
/*  78:    */   protected void process()
/*  79:    */   {
/*  80:185 */     double[] clusterAssignments = this.m_Evaluation.getClusterAssignments();
/*  81:    */     
/*  82:187 */     int[] classAssignments = null;
/*  83:188 */     if (this.m_Instances.classIndex() >= 0)
/*  84:    */     {
/*  85:189 */       classAssignments = this.m_Evaluation.getClassesToClusters();
/*  86:190 */       this.m_PlotShapes = new int[this.m_Instances.numInstances()];
/*  87:191 */       for (int i = 0; i < this.m_Instances.numInstances(); i++) {
/*  88:192 */         this.m_PlotShapes[i] = -1;
/*  89:    */       }
/*  90:    */     }
/*  91:196 */     for (int i = 0; i < this.m_Instances.numInstances(); i++)
/*  92:    */     {
/*  93:197 */       double[] values = new double[this.m_PlotInstances.numAttributes()];
/*  94:198 */       for (int j = 0; j < this.m_Instances.numAttributes(); j++) {
/*  95:199 */         values[j] = this.m_Instances.instance(i).value(j);
/*  96:    */       }
/*  97:201 */       if (clusterAssignments[i] < 0.0D) {
/*  98:202 */         values[j] = Utils.missingValue();
/*  99:    */       } else {
/* 100:204 */         values[j] = clusterAssignments[i];
/* 101:    */       }
/* 102:206 */       this.m_PlotInstances.add(new DenseInstance(1.0D, values));
/* 103:207 */       if (this.m_PlotShapes != null) {
/* 104:208 */         if (clusterAssignments[i] >= 0.0D)
/* 105:    */         {
/* 106:209 */           if ((int)this.m_Instances.instance(i).classValue() != classAssignments[((int)clusterAssignments[i])]) {
/* 107:210 */             this.m_PlotShapes[i] = 1000;
/* 108:    */           }
/* 109:    */         }
/* 110:    */         else {
/* 111:213 */           this.m_PlotShapes[i] = 2000;
/* 112:    */         }
/* 113:    */       }
/* 114:    */     }
/* 115:    */   }
/* 116:    */   
/* 117:    */   protected void finishUp()
/* 118:    */   {
/* 119:224 */     super.finishUp();
/* 120:    */     
/* 121:226 */     process();
/* 122:    */   }
/* 123:    */   
/* 124:    */   protected PlotData2D createPlotData(String name)
/* 125:    */     throws Exception
/* 126:    */   {
/* 127:241 */     PlotData2D result = new PlotData2D(this.m_PlotInstances);
/* 128:242 */     if (this.m_PlotShapes != null) {
/* 129:243 */       result.setShapeType(this.m_PlotShapes);
/* 130:    */     }
/* 131:245 */     result.addInstanceNumberAttribute();
/* 132:246 */     result.setPlotName(name + " (" + this.m_Instances.relationName() + ")");
/* 133:    */     
/* 134:248 */     return result;
/* 135:    */   }
/* 136:    */   
/* 137:    */   public void cleanUp()
/* 138:    */   {
/* 139:256 */     super.cleanUp();
/* 140:    */     
/* 141:258 */     this.m_Clusterer = null;
/* 142:259 */     this.m_Evaluation = null;
/* 143:260 */     this.m_PlotShapes = null;
/* 144:    */   }
/* 145:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.explorer.ClustererAssignmentsPlotInstances
 * JD-Core Version:    0.7.0.1
 */