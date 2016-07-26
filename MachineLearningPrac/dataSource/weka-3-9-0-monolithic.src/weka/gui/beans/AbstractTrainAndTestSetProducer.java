/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.beans.EventSetDescriptor;
/*   5:    */ import java.io.Serializable;
/*   6:    */ import java.util.EventListener;
/*   7:    */ import java.util.Vector;
/*   8:    */ import javax.swing.JPanel;
/*   9:    */ import weka.gui.Logger;
/*  10:    */ 
/*  11:    */ public abstract class AbstractTrainAndTestSetProducer
/*  12:    */   extends JPanel
/*  13:    */   implements Visible, TrainingSetProducer, TestSetProducer, BeanCommon, Serializable, DataSourceListener
/*  14:    */ {
/*  15:    */   private static final long serialVersionUID = -1809339823613492037L;
/*  16: 50 */   protected Vector<EventListener> m_trainingListeners = new Vector();
/*  17: 55 */   protected Vector<EventListener> m_testListeners = new Vector();
/*  18: 57 */   protected BeanVisual m_visual = new BeanVisual("AbstractTrainingSetProducer", "weka/gui/beans/icons/DefaultTrainTest.gif", "weka/gui/beans/icons/DefaultTrainTest_animated.gif");
/*  19: 64 */   protected Object m_listenee = null;
/*  20: 66 */   protected transient Logger m_logger = null;
/*  21:    */   
/*  22:    */   public AbstractTrainAndTestSetProducer()
/*  23:    */   {
/*  24: 72 */     setLayout(new BorderLayout());
/*  25: 73 */     add(this.m_visual, "Center");
/*  26:    */   }
/*  27:    */   
/*  28:    */   public abstract void acceptDataSet(DataSetEvent paramDataSetEvent);
/*  29:    */   
/*  30:    */   public synchronized void addTrainingSetListener(TrainingSetListener tsl)
/*  31:    */   {
/*  32: 91 */     this.m_trainingListeners.addElement(tsl);
/*  33:    */   }
/*  34:    */   
/*  35:    */   public synchronized void removeTrainingSetListener(TrainingSetListener tsl)
/*  36:    */   {
/*  37:101 */     this.m_trainingListeners.removeElement(tsl);
/*  38:    */   }
/*  39:    */   
/*  40:    */   public synchronized void addTestSetListener(TestSetListener tsl)
/*  41:    */   {
/*  42:111 */     this.m_testListeners.addElement(tsl);
/*  43:    */   }
/*  44:    */   
/*  45:    */   public synchronized void removeTestSetListener(TestSetListener tsl)
/*  46:    */   {
/*  47:121 */     this.m_testListeners.removeElement(tsl);
/*  48:    */   }
/*  49:    */   
/*  50:    */   public void setVisual(BeanVisual newVisual)
/*  51:    */   {
/*  52:131 */     this.m_visual = newVisual;
/*  53:    */   }
/*  54:    */   
/*  55:    */   public BeanVisual getVisual()
/*  56:    */   {
/*  57:141 */     return this.m_visual;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public void useDefaultVisual()
/*  61:    */   {
/*  62:149 */     this.m_visual.loadIcons("weka/gui/beans/icons/DefaultTrainTest.gif", "weka/gui/beans/icons/DefaultTrainTest_animated.gif");
/*  63:    */   }
/*  64:    */   
/*  65:    */   public boolean connectionAllowed(String eventName)
/*  66:    */   {
/*  67:162 */     return this.m_listenee == null;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public boolean connectionAllowed(EventSetDescriptor esd)
/*  71:    */   {
/*  72:174 */     return connectionAllowed(esd.getName());
/*  73:    */   }
/*  74:    */   
/*  75:    */   public synchronized void connectionNotification(String eventName, Object source)
/*  76:    */   {
/*  77:188 */     if (connectionAllowed(eventName)) {
/*  78:189 */       this.m_listenee = source;
/*  79:    */     }
/*  80:    */   }
/*  81:    */   
/*  82:    */   public synchronized void disconnectionNotification(String eventName, Object source)
/*  83:    */   {
/*  84:204 */     if (this.m_listenee == source) {
/*  85:205 */       this.m_listenee = null;
/*  86:    */     }
/*  87:    */   }
/*  88:    */   
/*  89:    */   public void setLog(Logger logger)
/*  90:    */   {
/*  91:216 */     this.m_logger = logger;
/*  92:    */   }
/*  93:    */   
/*  94:    */   public abstract void stop();
/*  95:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.AbstractTrainAndTestSetProducer
 * JD-Core Version:    0.7.0.1
 */