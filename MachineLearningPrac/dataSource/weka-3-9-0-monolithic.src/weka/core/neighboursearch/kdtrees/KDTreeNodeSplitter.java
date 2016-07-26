/*   1:    */ package weka.core.neighboursearch.kdtrees;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.core.EuclideanDistance;
/*   7:    */ import weka.core.Instances;
/*   8:    */ import weka.core.Option;
/*   9:    */ import weka.core.OptionHandler;
/*  10:    */ import weka.core.RevisionHandler;
/*  11:    */ import weka.core.RevisionUtils;
/*  12:    */ 
/*  13:    */ public abstract class KDTreeNodeSplitter
/*  14:    */   implements Serializable, OptionHandler, RevisionHandler
/*  15:    */ {
/*  16:    */   private static final long serialVersionUID = 7222420817095067166L;
/*  17:    */   protected Instances m_Instances;
/*  18:    */   protected EuclideanDistance m_EuclideanDistance;
/*  19:    */   protected int[] m_InstList;
/*  20:    */   protected boolean m_NormalizeNodeWidth;
/*  21:    */   public static final int MIN = 0;
/*  22:    */   public static final int MAX = 1;
/*  23:    */   public static final int WIDTH = 2;
/*  24:    */   
/*  25:    */   public KDTreeNodeSplitter() {}
/*  26:    */   
/*  27:    */   public KDTreeNodeSplitter(int[] instList, Instances insts, EuclideanDistance e)
/*  28:    */   {
/*  29: 87 */     this.m_InstList = instList;
/*  30: 88 */     this.m_Instances = insts;
/*  31: 89 */     this.m_EuclideanDistance = e;
/*  32:    */   }
/*  33:    */   
/*  34:    */   public Enumeration<Option> listOptions()
/*  35:    */   {
/*  36: 99 */     return new Vector().elements();
/*  37:    */   }
/*  38:    */   
/*  39:    */   public void setOptions(String[] options)
/*  40:    */     throws Exception
/*  41:    */   {}
/*  42:    */   
/*  43:    */   public String[] getOptions()
/*  44:    */   {
/*  45:119 */     return new String[0];
/*  46:    */   }
/*  47:    */   
/*  48:    */   protected void correctlyInitialized()
/*  49:    */     throws Exception
/*  50:    */   {
/*  51:130 */     if (this.m_Instances == null) {
/*  52:131 */       throw new Exception("No instances supplied.");
/*  53:    */     }
/*  54:132 */     if (this.m_InstList == null) {
/*  55:133 */       throw new Exception("No instance list supplied.");
/*  56:    */     }
/*  57:134 */     if (this.m_EuclideanDistance == null) {
/*  58:135 */       throw new Exception("No Euclidean distance function supplied.");
/*  59:    */     }
/*  60:136 */     if (this.m_Instances.numInstances() != this.m_InstList.length) {
/*  61:137 */       throw new Exception("The supplied instance list doesn't seem to match the supplied instances");
/*  62:    */     }
/*  63:    */   }
/*  64:    */   
/*  65:    */   public abstract void splitNode(KDTreeNode paramKDTreeNode, int paramInt, double[][] paramArrayOfDouble1, double[][] paramArrayOfDouble2)
/*  66:    */     throws Exception;
/*  67:    */   
/*  68:    */   public void setInstances(Instances inst)
/*  69:    */   {
/*  70:164 */     this.m_Instances = inst;
/*  71:    */   }
/*  72:    */   
/*  73:    */   public void setInstanceList(int[] instList)
/*  74:    */   {
/*  75:176 */     this.m_InstList = instList;
/*  76:    */   }
/*  77:    */   
/*  78:    */   public void setEuclideanDistanceFunction(EuclideanDistance func)
/*  79:    */   {
/*  80:185 */     this.m_EuclideanDistance = func;
/*  81:    */   }
/*  82:    */   
/*  83:    */   public void setNodeWidthNormalization(boolean normalize)
/*  84:    */   {
/*  85:198 */     this.m_NormalizeNodeWidth = normalize;
/*  86:    */   }
/*  87:    */   
/*  88:    */   protected int widestDim(double[][] nodeRanges, double[][] universe)
/*  89:    */   {
/*  90:212 */     int classIdx = this.m_Instances.classIndex();
/*  91:213 */     double widest = 0.0D;
/*  92:214 */     int w = -1;
/*  93:215 */     if (this.m_NormalizeNodeWidth) {
/*  94:216 */       for (int i = 0; i < nodeRanges.length; i++)
/*  95:    */       {
/*  96:217 */         double newWidest = nodeRanges[i][2] / universe[i][2];
/*  97:218 */         if ((newWidest > widest) && 
/*  98:219 */           (i != classIdx))
/*  99:    */         {
/* 100:222 */           widest = newWidest;
/* 101:223 */           w = i;
/* 102:    */         }
/* 103:    */       }
/* 104:    */     } else {
/* 105:227 */       for (int i = 0; i < nodeRanges.length; i++) {
/* 106:228 */         if ((nodeRanges[i][2] > widest) && 
/* 107:229 */           (i != classIdx))
/* 108:    */         {
/* 109:232 */           widest = nodeRanges[i][2];
/* 110:233 */           w = i;
/* 111:    */         }
/* 112:    */       }
/* 113:    */     }
/* 114:237 */     return w;
/* 115:    */   }
/* 116:    */   
/* 117:    */   public String getRevision()
/* 118:    */   {
/* 119:247 */     return RevisionUtils.extract("$Revision: 10203 $");
/* 120:    */   }
/* 121:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.neighboursearch.kdtrees.KDTreeNodeSplitter
 * JD-Core Version:    0.7.0.1
 */