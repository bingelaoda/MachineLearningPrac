/*   1:    */ package weka.core.neighboursearch.balltrees;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.core.DistanceFunction;
/*   7:    */ import weka.core.EuclideanDistance;
/*   8:    */ import weka.core.Instance;
/*   9:    */ import weka.core.Instances;
/*  10:    */ import weka.core.Option;
/*  11:    */ import weka.core.OptionHandler;
/*  12:    */ import weka.core.RevisionHandler;
/*  13:    */ import weka.core.RevisionUtils;
/*  14:    */ import weka.core.Utils;
/*  15:    */ 
/*  16:    */ public abstract class BallTreeConstructor
/*  17:    */   implements OptionHandler, Serializable, RevisionHandler
/*  18:    */ {
/*  19:    */   private static final long serialVersionUID = 982315539809240771L;
/*  20: 50 */   protected int m_MaxInstancesInLeaf = 40;
/*  21: 56 */   protected double m_MaxRelLeafRadius = 0.001D;
/*  22: 62 */   protected boolean m_FullyContainChildBalls = false;
/*  23:    */   protected Instances m_Instances;
/*  24:    */   protected DistanceFunction m_DistanceFunction;
/*  25:    */   protected int m_NumNodes;
/*  26:    */   protected int m_NumLeaves;
/*  27:    */   protected int m_MaxDepth;
/*  28:    */   protected int[] m_InstList;
/*  29:    */   
/*  30:    */   public abstract BallNode buildTree()
/*  31:    */     throws Exception;
/*  32:    */   
/*  33:    */   public abstract int[] addInstance(BallNode paramBallNode, Instance paramInstance)
/*  34:    */     throws Exception;
/*  35:    */   
/*  36:    */   public String maxInstancesInLeafTipText()
/*  37:    */   {
/*  38:117 */     return "The maximum number of instances allowed in a leaf.";
/*  39:    */   }
/*  40:    */   
/*  41:    */   public int getMaxInstancesInLeaf()
/*  42:    */   {
/*  43:126 */     return this.m_MaxInstancesInLeaf;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public void setMaxInstancesInLeaf(int num)
/*  47:    */     throws Exception
/*  48:    */   {
/*  49:136 */     if (num < 1) {
/*  50:137 */       throw new Exception("The maximum number of instances in a leaf must be >=1.");
/*  51:    */     }
/*  52:140 */     this.m_MaxInstancesInLeaf = num;
/*  53:    */   }
/*  54:    */   
/*  55:    */   public String maxRelativeLeafRadiusTipText()
/*  56:    */   {
/*  57:150 */     return "The maximum relative radius allowed for a leaf node. Itis relative to the radius of the smallest ball enclosing all the data points (that were used to build the tree). This smallest ball would be the same as the root node's ball, if ContainChildBalls property is set to false (default).";
/*  58:    */   }
/*  59:    */   
/*  60:    */   public double getMaxRelativeLeafRadius()
/*  61:    */   {
/*  62:167 */     return this.m_MaxRelLeafRadius;
/*  63:    */   }
/*  64:    */   
/*  65:    */   public void setMaxRelativeLeafRadius(double radius)
/*  66:    */     throws Exception
/*  67:    */   {
/*  68:181 */     if (radius < 0.0D) {
/*  69:182 */       throw new Exception("The radius for the leaves should be >= 0.0");
/*  70:    */     }
/*  71:184 */     this.m_MaxRelLeafRadius = radius;
/*  72:    */   }
/*  73:    */   
/*  74:    */   public String containChildBallsTipText()
/*  75:    */   {
/*  76:194 */     return "Whether to contain fully the child balls.";
/*  77:    */   }
/*  78:    */   
/*  79:    */   public boolean getContainChildBalls()
/*  80:    */   {
/*  81:204 */     return this.m_FullyContainChildBalls;
/*  82:    */   }
/*  83:    */   
/*  84:    */   public void setContainChildBalls(boolean containChildBalls)
/*  85:    */   {
/*  86:215 */     this.m_FullyContainChildBalls = containChildBalls;
/*  87:    */   }
/*  88:    */   
/*  89:    */   public void setInstances(Instances inst)
/*  90:    */   {
/*  91:224 */     this.m_Instances = inst;
/*  92:    */   }
/*  93:    */   
/*  94:    */   public void setInstanceList(int[] instList)
/*  95:    */   {
/*  96:234 */     this.m_InstList = instList;
/*  97:    */   }
/*  98:    */   
/*  99:    */   public void setEuclideanDistanceFunction(EuclideanDistance func)
/* 100:    */   {
/* 101:243 */     this.m_DistanceFunction = func;
/* 102:    */   }
/* 103:    */   
/* 104:    */   public int getNumNodes()
/* 105:    */   {
/* 106:252 */     return this.m_NumNodes;
/* 107:    */   }
/* 108:    */   
/* 109:    */   public int getNumLeaves()
/* 110:    */   {
/* 111:261 */     return this.m_NumLeaves;
/* 112:    */   }
/* 113:    */   
/* 114:    */   public int getMaxDepth()
/* 115:    */   {
/* 116:270 */     return this.m_MaxDepth;
/* 117:    */   }
/* 118:    */   
/* 119:    */   public Enumeration<Option> listOptions()
/* 120:    */   {
/* 121:280 */     Vector<Option> newVector = new Vector();
/* 122:    */     
/* 123:282 */     newVector.addElement(new Option("\tSet maximum number of instances in a leaf node\n\t(default: 40)", "N", 0, "-N <value>"));
/* 124:    */     
/* 125:    */ 
/* 126:    */ 
/* 127:286 */     newVector.addElement(new Option("\tSet internal nodes' radius to the sum \n\tof the child balls radii. So that it \ncontains the child balls.", "R", 0, "-R"));
/* 128:    */     
/* 129:    */ 
/* 130:    */ 
/* 131:    */ 
/* 132:291 */     return newVector.elements();
/* 133:    */   }
/* 134:    */   
/* 135:    */   public void setOptions(String[] options)
/* 136:    */     throws Exception
/* 137:    */   {
/* 138:303 */     String optionString = Utils.getOption('N', options);
/* 139:304 */     if (optionString.length() != 0) {
/* 140:305 */       setMaxInstancesInLeaf(Integer.parseInt(optionString));
/* 141:    */     } else {
/* 142:307 */       setMaxInstancesInLeaf(40);
/* 143:    */     }
/* 144:310 */     setContainChildBalls(Utils.getFlag('R', options));
/* 145:    */   }
/* 146:    */   
/* 147:    */   public String[] getOptions()
/* 148:    */   {
/* 149:320 */     Vector<String> result = new Vector();
/* 150:    */     
/* 151:322 */     result.add("-N");
/* 152:323 */     result.add("" + getMaxInstancesInLeaf());
/* 153:325 */     if (getContainChildBalls()) {
/* 154:326 */       result.add("-R");
/* 155:    */     }
/* 156:329 */     return (String[])result.toArray(new String[result.size()]);
/* 157:    */   }
/* 158:    */   
/* 159:    */   public String getRevision()
/* 160:    */   {
/* 161:339 */     return RevisionUtils.extract("$Revision: 10203 $");
/* 162:    */   }
/* 163:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.neighboursearch.balltrees.BallTreeConstructor
 * JD-Core Version:    0.7.0.1
 */