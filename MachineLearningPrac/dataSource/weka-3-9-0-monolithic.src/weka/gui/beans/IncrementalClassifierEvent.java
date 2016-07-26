/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.util.EventObject;
/*   4:    */ import weka.classifiers.Classifier;
/*   5:    */ import weka.core.Instance;
/*   6:    */ import weka.core.Instances;
/*   7:    */ 
/*   8:    */ public class IncrementalClassifierEvent
/*   9:    */   extends EventObject
/*  10:    */ {
/*  11:    */   private static final long serialVersionUID = 28979464317643232L;
/*  12:    */   public static final int NEW_BATCH = 0;
/*  13:    */   public static final int WITHIN_BATCH = 1;
/*  14:    */   public static final int BATCH_FINISHED = 2;
/*  15:    */   private Instances m_structure;
/*  16:    */   private int m_status;
/*  17:    */   protected Classifier m_classifier;
/*  18:    */   protected Instance m_currentInstance;
/*  19:    */   
/*  20:    */   public IncrementalClassifierEvent(Object source, Classifier scheme, Instance currentI, int status)
/*  21:    */   {
/*  22: 63 */     super(source);
/*  23:    */     
/*  24: 65 */     this.m_classifier = scheme;
/*  25: 66 */     this.m_currentInstance = currentI;
/*  26: 67 */     this.m_status = status;
/*  27:    */   }
/*  28:    */   
/*  29:    */   public IncrementalClassifierEvent(Object source, Classifier scheme, Instances structure)
/*  30:    */   {
/*  31: 80 */     super(source);
/*  32: 81 */     this.m_structure = structure;
/*  33: 82 */     this.m_status = 0;
/*  34: 83 */     this.m_classifier = scheme;
/*  35:    */   }
/*  36:    */   
/*  37:    */   public IncrementalClassifierEvent(Object source)
/*  38:    */   {
/*  39: 87 */     super(source);
/*  40:    */   }
/*  41:    */   
/*  42:    */   public Classifier getClassifier()
/*  43:    */   {
/*  44: 96 */     return this.m_classifier;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public void setClassifier(Classifier c)
/*  48:    */   {
/*  49:100 */     this.m_classifier = c;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public Instance getCurrentInstance()
/*  53:    */   {
/*  54:109 */     return this.m_currentInstance;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public void setCurrentInstance(Instance i)
/*  58:    */   {
/*  59:118 */     this.m_currentInstance = i;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public int getStatus()
/*  63:    */   {
/*  64:127 */     return this.m_status;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public void setStatus(int s)
/*  68:    */   {
/*  69:136 */     this.m_status = s;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public void setStructure(Instances structure)
/*  73:    */   {
/*  74:145 */     this.m_structure = structure;
/*  75:146 */     this.m_currentInstance = null;
/*  76:147 */     this.m_status = 0;
/*  77:    */   }
/*  78:    */   
/*  79:    */   public Instances getStructure()
/*  80:    */   {
/*  81:157 */     return this.m_structure;
/*  82:    */   }
/*  83:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.IncrementalClassifierEvent
 * JD-Core Version:    0.7.0.1
 */