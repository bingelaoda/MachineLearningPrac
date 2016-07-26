/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.util.EventObject;
/*   4:    */ import weka.clusterers.Clusterer;
/*   5:    */ 
/*   6:    */ public class BatchClustererEvent
/*   7:    */   extends EventObject
/*   8:    */ {
/*   9:    */   private static final long serialVersionUID = 7268777944939129714L;
/*  10:    */   protected Clusterer m_clusterer;
/*  11:    */   protected DataSetEvent m_testSet;
/*  12:    */   protected int m_setNumber;
/*  13:    */   protected int m_testOrTrain;
/*  14:    */   protected int m_maxSetNumber;
/*  15: 70 */   public static int TEST = 0;
/*  16: 71 */   public static int TRAINING = 1;
/*  17:    */   
/*  18:    */   public BatchClustererEvent(Object source, Clusterer scheme, DataSetEvent tstI, int setNum, int maxSetNum, int testOrTrain)
/*  19:    */   {
/*  20: 84 */     super(source);
/*  21:    */     
/*  22: 86 */     this.m_clusterer = scheme;
/*  23: 87 */     this.m_testSet = tstI;
/*  24: 88 */     this.m_setNumber = setNum;
/*  25: 89 */     this.m_maxSetNumber = maxSetNum;
/*  26: 90 */     if (testOrTrain == 0) {
/*  27: 91 */       this.m_testOrTrain = TEST;
/*  28:    */     } else {
/*  29: 93 */       this.m_testOrTrain = TRAINING;
/*  30:    */     }
/*  31:    */   }
/*  32:    */   
/*  33:    */   public Clusterer getClusterer()
/*  34:    */   {
/*  35:103 */     return this.m_clusterer;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public DataSetEvent getTestSet()
/*  39:    */   {
/*  40:112 */     return this.m_testSet;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public int getSetNumber()
/*  44:    */   {
/*  45:122 */     return this.m_setNumber;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public int getMaxSetNumber()
/*  49:    */   {
/*  50:132 */     return this.m_maxSetNumber;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public int getTestOrTrain()
/*  54:    */   {
/*  55:141 */     return this.m_testOrTrain;
/*  56:    */   }
/*  57:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.BatchClustererEvent
 * JD-Core Version:    0.7.0.1
 */