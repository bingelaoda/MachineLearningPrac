/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.util.EventObject;
/*   4:    */ import weka.classifiers.Classifier;
/*   5:    */ 
/*   6:    */ public class BatchClassifierEvent
/*   7:    */   extends EventObject
/*   8:    */ {
/*   9:    */   private static final long serialVersionUID = 878097199815991084L;
/*  10:    */   protected Classifier m_classifier;
/*  11:    */   protected DataSetEvent m_testSet;
/*  12:    */   protected DataSetEvent m_trainSet;
/*  13: 62 */   protected int m_runNumber = 1;
/*  14: 67 */   protected int m_maxRunNumber = 1;
/*  15:    */   protected int m_setNumber;
/*  16:    */   protected int m_maxSetNumber;
/*  17: 82 */   protected long m_groupIdentifier = 9223372036854775807L;
/*  18: 87 */   protected String m_eventLabel = "";
/*  19:    */   
/*  20:    */   public BatchClassifierEvent(Object source, Classifier scheme, DataSetEvent trsI, DataSetEvent tstI, int setNum, int maxSetNum)
/*  21:    */   {
/*  22:101 */     super(source);
/*  23:    */     
/*  24:103 */     this.m_classifier = scheme;
/*  25:104 */     this.m_testSet = tstI;
/*  26:105 */     this.m_trainSet = trsI;
/*  27:106 */     this.m_setNumber = setNum;
/*  28:107 */     this.m_maxSetNumber = maxSetNum;
/*  29:    */   }
/*  30:    */   
/*  31:    */   public BatchClassifierEvent(Object source, Classifier scheme, DataSetEvent trsI, DataSetEvent tstI, int runNum, int maxRunNum, int setNum, int maxSetNum)
/*  32:    */   {
/*  33:126 */     this(source, scheme, trsI, tstI, setNum, maxSetNum);
/*  34:    */     
/*  35:128 */     this.m_runNumber = runNum;
/*  36:129 */     this.m_maxRunNumber = maxRunNum;
/*  37:    */   }
/*  38:    */   
/*  39:    */   public void setLabel(String lab)
/*  40:    */   {
/*  41:138 */     this.m_eventLabel = lab;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public String getLabel()
/*  45:    */   {
/*  46:147 */     return this.m_eventLabel;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public Classifier getClassifier()
/*  50:    */   {
/*  51:156 */     return this.m_classifier;
/*  52:    */   }
/*  53:    */   
/*  54:    */   public void setClassifier(Classifier classifier)
/*  55:    */   {
/*  56:165 */     this.m_classifier = classifier;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public void setTestSet(DataSetEvent tse)
/*  60:    */   {
/*  61:174 */     this.m_testSet = tse;
/*  62:    */   }
/*  63:    */   
/*  64:    */   public DataSetEvent getTestSet()
/*  65:    */   {
/*  66:183 */     return this.m_testSet;
/*  67:    */   }
/*  68:    */   
/*  69:    */   public void setTrainSet(DataSetEvent tse)
/*  70:    */   {
/*  71:192 */     this.m_trainSet = tse;
/*  72:    */   }
/*  73:    */   
/*  74:    */   public DataSetEvent getTrainSet()
/*  75:    */   {
/*  76:201 */     return this.m_trainSet;
/*  77:    */   }
/*  78:    */   
/*  79:    */   public int getRunNumber()
/*  80:    */   {
/*  81:210 */     return this.m_runNumber;
/*  82:    */   }
/*  83:    */   
/*  84:    */   public int getMaxRunNumber()
/*  85:    */   {
/*  86:219 */     return this.m_maxRunNumber;
/*  87:    */   }
/*  88:    */   
/*  89:    */   public int getSetNumber()
/*  90:    */   {
/*  91:229 */     return this.m_setNumber;
/*  92:    */   }
/*  93:    */   
/*  94:    */   public int getMaxSetNumber()
/*  95:    */   {
/*  96:239 */     return this.m_maxSetNumber;
/*  97:    */   }
/*  98:    */   
/*  99:    */   public void setGroupIdentifier(long identifier)
/* 100:    */   {
/* 101:243 */     this.m_groupIdentifier = identifier;
/* 102:    */   }
/* 103:    */   
/* 104:    */   public long getGroupIdentifier()
/* 105:    */   {
/* 106:247 */     return this.m_groupIdentifier;
/* 107:    */   }
/* 108:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.BatchClassifierEvent
 * JD-Core Version:    0.7.0.1
 */