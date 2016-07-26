/*   1:    */ package weka.core.neighboursearch.balltrees;
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
/*  13:    */ public abstract class BallSplitter
/*  14:    */   implements Serializable, OptionHandler, RevisionHandler
/*  15:    */ {
/*  16:    */   private static final long serialVersionUID = -2233739562654159948L;
/*  17:    */   protected Instances m_Instances;
/*  18:    */   protected EuclideanDistance m_DistanceFunction;
/*  19:    */   protected int[] m_Instlist;
/*  20:    */   
/*  21:    */   public BallSplitter() {}
/*  22:    */   
/*  23:    */   public BallSplitter(int[] instList, Instances insts, EuclideanDistance e)
/*  24:    */   {
/*  25: 74 */     this.m_Instlist = instList;
/*  26: 75 */     this.m_Instances = insts;
/*  27: 76 */     this.m_DistanceFunction = e;
/*  28:    */   }
/*  29:    */   
/*  30:    */   protected void correctlyInitialized()
/*  31:    */     throws Exception
/*  32:    */   {
/*  33: 86 */     if (this.m_Instances == null) {
/*  34: 87 */       throw new Exception("No instances supplied.");
/*  35:    */     }
/*  36: 88 */     if (this.m_Instlist == null) {
/*  37: 89 */       throw new Exception("No instance list supplied.");
/*  38:    */     }
/*  39: 90 */     if (this.m_DistanceFunction == null) {
/*  40: 91 */       throw new Exception("No Euclidean distance function supplied.");
/*  41:    */     }
/*  42: 92 */     if (this.m_Instances.numInstances() != this.m_Instlist.length) {
/*  43: 93 */       throw new Exception("The supplied instance list doesn't seem to match the supplied instances");
/*  44:    */     }
/*  45:    */   }
/*  46:    */   
/*  47:    */   public Enumeration<Option> listOptions()
/*  48:    */   {
/*  49:105 */     return new Vector().elements();
/*  50:    */   }
/*  51:    */   
/*  52:    */   public void setOptions(String[] options)
/*  53:    */     throws Exception
/*  54:    */   {}
/*  55:    */   
/*  56:    */   public String[] getOptions()
/*  57:    */   {
/*  58:125 */     return new String[0];
/*  59:    */   }
/*  60:    */   
/*  61:    */   public abstract void splitNode(BallNode paramBallNode, int paramInt)
/*  62:    */     throws Exception;
/*  63:    */   
/*  64:    */   public void setInstances(Instances inst)
/*  65:    */   {
/*  66:146 */     this.m_Instances = inst;
/*  67:    */   }
/*  68:    */   
/*  69:    */   public void setInstanceList(int[] instList)
/*  70:    */   {
/*  71:158 */     this.m_Instlist = instList;
/*  72:    */   }
/*  73:    */   
/*  74:    */   public void setEuclideanDistanceFunction(EuclideanDistance func)
/*  75:    */   {
/*  76:167 */     this.m_DistanceFunction = func;
/*  77:    */   }
/*  78:    */   
/*  79:    */   public String getRevision()
/*  80:    */   {
/*  81:177 */     return RevisionUtils.extract("$Revision: 10203 $");
/*  82:    */   }
/*  83:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.neighboursearch.balltrees.BallSplitter
 * JD-Core Version:    0.7.0.1
 */