/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.util.EventObject;
/*   4:    */ import weka.core.Instances;
/*   5:    */ 
/*   6:    */ public class TestSetEvent
/*   7:    */   extends EventObject
/*   8:    */ {
/*   9:    */   private static final long serialVersionUID = 8780718708498854231L;
/*  10:    */   protected Instances m_testSet;
/*  11:    */   private boolean m_structureOnly;
/*  12: 49 */   protected int m_runNumber = 1;
/*  13: 55 */   protected int m_maxRunNumber = 1;
/*  14:    */   protected int m_setNumber;
/*  15:    */   protected int m_maxSetNumber;
/*  16:    */   
/*  17:    */   public TestSetEvent(Object source, Instances testSet)
/*  18:    */   {
/*  19: 74 */     super(source);
/*  20: 75 */     this.m_testSet = testSet;
/*  21: 76 */     if ((this.m_testSet != null) && (this.m_testSet.numInstances() == 0)) {
/*  22: 77 */       this.m_structureOnly = true;
/*  23:    */     }
/*  24:    */   }
/*  25:    */   
/*  26:    */   public TestSetEvent(Object source, Instances testSet, int setNum, int maxSetNum)
/*  27:    */   {
/*  28: 91 */     this(source, testSet);
/*  29: 92 */     this.m_setNumber = setNum;
/*  30: 93 */     this.m_maxSetNumber = maxSetNum;
/*  31:    */   }
/*  32:    */   
/*  33:    */   public TestSetEvent(Object source, Instances testSet, int runNum, int maxRunNum, int setNum, int maxSetNum)
/*  34:    */   {
/*  35:108 */     this(source, testSet, setNum, maxSetNum);
/*  36:    */     
/*  37:110 */     this.m_runNumber = runNum;
/*  38:111 */     this.m_maxRunNumber = maxRunNum;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public Instances getTestSet()
/*  42:    */   {
/*  43:120 */     return this.m_testSet;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public int getRunNumber()
/*  47:    */   {
/*  48:129 */     return this.m_runNumber;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public int getMaxRunNumber()
/*  52:    */   {
/*  53:138 */     return this.m_maxRunNumber;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public int getSetNumber()
/*  57:    */   {
/*  58:147 */     return this.m_setNumber;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public int getMaxSetNumber()
/*  62:    */   {
/*  63:156 */     return this.m_maxSetNumber;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public boolean isStructureOnly()
/*  67:    */   {
/*  68:167 */     return this.m_structureOnly;
/*  69:    */   }
/*  70:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.TestSetEvent
 * JD-Core Version:    0.7.0.1
 */