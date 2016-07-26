/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.util.EventObject;
/*   4:    */ import weka.core.Instances;
/*   5:    */ 
/*   6:    */ public class TrainingSetEvent
/*   7:    */   extends EventObject
/*   8:    */ {
/*   9:    */   private static final long serialVersionUID = 5872343811810872662L;
/*  10:    */   protected Instances m_trainingSet;
/*  11:    */   private boolean m_structureOnly;
/*  12: 49 */   protected int m_runNumber = 1;
/*  13: 55 */   protected int m_maxRunNumber = 1;
/*  14:    */   protected int m_setNumber;
/*  15:    */   protected int m_maxSetNumber;
/*  16:    */   
/*  17:    */   public TrainingSetEvent(Object source, Instances trainSet)
/*  18:    */   {
/*  19: 74 */     super(source);
/*  20: 75 */     this.m_trainingSet = trainSet;
/*  21: 76 */     if ((this.m_trainingSet != null) && (this.m_trainingSet.numInstances() == 0)) {
/*  22: 77 */       this.m_structureOnly = true;
/*  23:    */     }
/*  24:    */   }
/*  25:    */   
/*  26:    */   public TrainingSetEvent(Object source, Instances trainSet, int setNum, int maxSetNum)
/*  27:    */   {
/*  28: 90 */     this(source, trainSet);
/*  29: 91 */     this.m_setNumber = setNum;
/*  30: 92 */     this.m_maxSetNumber = maxSetNum;
/*  31:    */   }
/*  32:    */   
/*  33:    */   public TrainingSetEvent(Object source, Instances trainSet, int runNum, int maxRunNum, int setNum, int maxSetNum)
/*  34:    */   {
/*  35:107 */     this(source, trainSet, setNum, maxSetNum);
/*  36:    */     
/*  37:109 */     this.m_runNumber = runNum;
/*  38:110 */     this.m_maxRunNumber = maxRunNum;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public Instances getTrainingSet()
/*  42:    */   {
/*  43:119 */     return this.m_trainingSet;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public int getRunNumber()
/*  47:    */   {
/*  48:128 */     return this.m_runNumber;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public int getMaxRunNumber()
/*  52:    */   {
/*  53:137 */     return this.m_maxRunNumber;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public int getSetNumber()
/*  57:    */   {
/*  58:146 */     return this.m_setNumber;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public int getMaxSetNumber()
/*  62:    */   {
/*  63:155 */     return this.m_maxSetNumber;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public boolean isStructureOnly()
/*  67:    */   {
/*  68:166 */     return this.m_structureOnly;
/*  69:    */   }
/*  70:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.TrainingSetEvent
 * JD-Core Version:    0.7.0.1
 */