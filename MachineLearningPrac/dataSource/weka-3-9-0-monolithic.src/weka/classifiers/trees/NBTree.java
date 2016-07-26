/*   1:    */ package weka.classifiers.trees;
/*   2:    */ 
/*   3:    */ import java.util.Enumeration;
/*   4:    */ import java.util.Vector;
/*   5:    */ import weka.classifiers.AbstractClassifier;
/*   6:    */ import weka.classifiers.trees.j48.NBTreeClassifierTree;
/*   7:    */ import weka.classifiers.trees.j48.NBTreeModelSelection;
/*   8:    */ import weka.core.AdditionalMeasureProducer;
/*   9:    */ import weka.core.Capabilities;
/*  10:    */ import weka.core.Drawable;
/*  11:    */ import weka.core.Instance;
/*  12:    */ import weka.core.Instances;
/*  13:    */ import weka.core.RevisionUtils;
/*  14:    */ import weka.core.Summarizable;
/*  15:    */ import weka.core.TechnicalInformation;
/*  16:    */ import weka.core.TechnicalInformation.Field;
/*  17:    */ import weka.core.TechnicalInformation.Type;
/*  18:    */ import weka.core.TechnicalInformationHandler;
/*  19:    */ import weka.core.WeightedInstancesHandler;
/*  20:    */ 
/*  21:    */ public class NBTree
/*  22:    */   extends AbstractClassifier
/*  23:    */   implements WeightedInstancesHandler, Drawable, Summarizable, AdditionalMeasureProducer, TechnicalInformationHandler
/*  24:    */ {
/*  25:    */   static final long serialVersionUID = -4716005707058256086L;
/*  26: 91 */   private final int m_minNumObj = 30;
/*  27:    */   private NBTreeClassifierTree m_root;
/*  28:    */   
/*  29:    */   public String globalInfo()
/*  30:    */   {
/*  31:103 */     return "Class for generating a decision tree with naive Bayes classifiers at the leaves.\n\nFor more information, see\n\n" + getTechnicalInformation().toString();
/*  32:    */   }
/*  33:    */   
/*  34:    */   public TechnicalInformation getTechnicalInformation()
/*  35:    */   {
/*  36:120 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*  37:121 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Ron Kohavi");
/*  38:122 */     result.setValue(TechnicalInformation.Field.TITLE, "Scaling Up the Accuracy of Naive-Bayes Classifiers: A Decision-Tree Hybrid");
/*  39:    */     
/*  40:    */ 
/*  41:125 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "Second International Conference on Knoledge Discovery and Data Mining");
/*  42:    */     
/*  43:127 */     result.setValue(TechnicalInformation.Field.YEAR, "1996");
/*  44:128 */     result.setValue(TechnicalInformation.Field.PAGES, "202-207");
/*  45:    */     
/*  46:130 */     return result;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public Capabilities getCapabilities()
/*  50:    */   {
/*  51:140 */     return new NBTreeClassifierTree(null).getCapabilities();
/*  52:    */   }
/*  53:    */   
/*  54:    */   public void buildClassifier(Instances instances)
/*  55:    */     throws Exception
/*  56:    */   {
/*  57:152 */     NBTreeModelSelection modSelection = new NBTreeModelSelection(30, instances);
/*  58:    */     
/*  59:    */ 
/*  60:155 */     this.m_root = new NBTreeClassifierTree(modSelection);
/*  61:156 */     this.m_root.buildClassifier(instances);
/*  62:    */   }
/*  63:    */   
/*  64:    */   public double classifyInstance(Instance instance)
/*  65:    */     throws Exception
/*  66:    */   {
/*  67:169 */     return this.m_root.classifyInstance(instance);
/*  68:    */   }
/*  69:    */   
/*  70:    */   public final double[] distributionForInstance(Instance instance)
/*  71:    */     throws Exception
/*  72:    */   {
/*  73:183 */     return this.m_root.distributionForInstance(instance, false);
/*  74:    */   }
/*  75:    */   
/*  76:    */   public String toString()
/*  77:    */   {
/*  78:194 */     if (this.m_root == null) {
/*  79:195 */       return "No classifier built";
/*  80:    */     }
/*  81:197 */     return "NBTree\n------------------\n" + this.m_root.toString();
/*  82:    */   }
/*  83:    */   
/*  84:    */   public int graphType()
/*  85:    */   {
/*  86:207 */     return 1;
/*  87:    */   }
/*  88:    */   
/*  89:    */   public String graph()
/*  90:    */     throws Exception
/*  91:    */   {
/*  92:219 */     return this.m_root.graph();
/*  93:    */   }
/*  94:    */   
/*  95:    */   public String toSummaryString()
/*  96:    */   {
/*  97:230 */     return "Number of leaves: " + this.m_root.numLeaves() + "\n" + "Size of the tree: " + this.m_root.numNodes() + "\n";
/*  98:    */   }
/*  99:    */   
/* 100:    */   public double measureTreeSize()
/* 101:    */   {
/* 102:240 */     return this.m_root.numNodes();
/* 103:    */   }
/* 104:    */   
/* 105:    */   public double measureNumLeaves()
/* 106:    */   {
/* 107:249 */     return this.m_root.numLeaves();
/* 108:    */   }
/* 109:    */   
/* 110:    */   public double measureNumRules()
/* 111:    */   {
/* 112:258 */     return this.m_root.numLeaves();
/* 113:    */   }
/* 114:    */   
/* 115:    */   public double getMeasure(String additionalMeasureName)
/* 116:    */   {
/* 117:270 */     if (additionalMeasureName.compareToIgnoreCase("measureNumRules") == 0) {
/* 118:271 */       return measureNumRules();
/* 119:    */     }
/* 120:272 */     if (additionalMeasureName.compareToIgnoreCase("measureTreeSize") == 0) {
/* 121:273 */       return measureTreeSize();
/* 122:    */     }
/* 123:274 */     if (additionalMeasureName.compareToIgnoreCase("measureNumLeaves") == 0) {
/* 124:275 */       return measureNumLeaves();
/* 125:    */     }
/* 126:277 */     throw new IllegalArgumentException(additionalMeasureName + " not supported (j48)");
/* 127:    */   }
/* 128:    */   
/* 129:    */   public Enumeration<String> enumerateMeasures()
/* 130:    */   {
/* 131:289 */     Vector<String> newVector = new Vector(3);
/* 132:290 */     newVector.addElement("measureTreeSize");
/* 133:291 */     newVector.addElement("measureNumLeaves");
/* 134:292 */     newVector.addElement("measureNumRules");
/* 135:293 */     return newVector.elements();
/* 136:    */   }
/* 137:    */   
/* 138:    */   public String getRevision()
/* 139:    */   {
/* 140:303 */     return RevisionUtils.extract("$Revision: 10374 $");
/* 141:    */   }
/* 142:    */   
/* 143:    */   public static void main(String[] argv)
/* 144:    */   {
/* 145:312 */     runClassifier(new NBTree(), argv);
/* 146:    */   }
/* 147:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.NBTree
 * JD-Core Version:    0.7.0.1
 */