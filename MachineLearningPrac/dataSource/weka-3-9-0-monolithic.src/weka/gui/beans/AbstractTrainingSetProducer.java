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
/*  11:    */ public abstract class AbstractTrainingSetProducer
/*  12:    */   extends JPanel
/*  13:    */   implements TrainingSetProducer, Visible, BeanCommon, Serializable
/*  14:    */ {
/*  15:    */   private static final long serialVersionUID = -7842746199524591125L;
/*  16: 50 */   protected Vector<EventListener> m_listeners = new Vector();
/*  17: 52 */   protected BeanVisual m_visual = new BeanVisual("AbstractTraingSetProducer", "weka/gui/beans/icons/DefaultTrainTest.gif", "weka/gui/beans/icons/DefaultTrainTest_animated.gif");
/*  18: 59 */   protected Object m_listenee = null;
/*  19: 61 */   protected transient Logger m_logger = null;
/*  20:    */   
/*  21:    */   public AbstractTrainingSetProducer()
/*  22:    */   {
/*  23: 67 */     setLayout(new BorderLayout());
/*  24: 68 */     add(this.m_visual, "Center");
/*  25:    */   }
/*  26:    */   
/*  27:    */   public synchronized void addTrainingSetListener(TrainingSetListener tsl)
/*  28:    */   {
/*  29: 78 */     this.m_listeners.addElement(tsl);
/*  30:    */   }
/*  31:    */   
/*  32:    */   public synchronized void removeTrainingSetListener(TrainingSetListener tsl)
/*  33:    */   {
/*  34: 88 */     this.m_listeners.removeElement(tsl);
/*  35:    */   }
/*  36:    */   
/*  37:    */   public void setVisual(BeanVisual newVisual)
/*  38:    */   {
/*  39: 98 */     this.m_visual = newVisual;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public BeanVisual getVisual()
/*  43:    */   {
/*  44:108 */     return this.m_visual;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public void useDefaultVisual()
/*  48:    */   {
/*  49:116 */     this.m_visual.loadIcons("weka/gui/beans/icons/DefaultTrainTest.gif", "weka/gui/beans/icons/DefaultTrainTest_animated.gif");
/*  50:    */   }
/*  51:    */   
/*  52:    */   public boolean connectionAllowed(String eventName)
/*  53:    */   {
/*  54:129 */     return this.m_listenee == null;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public boolean connectionAllowed(EventSetDescriptor esd)
/*  58:    */   {
/*  59:141 */     return connectionAllowed(esd.getName());
/*  60:    */   }
/*  61:    */   
/*  62:    */   public synchronized void connectionNotification(String eventName, Object source)
/*  63:    */   {
/*  64:155 */     if (connectionAllowed(eventName)) {
/*  65:156 */       this.m_listenee = source;
/*  66:    */     }
/*  67:    */   }
/*  68:    */   
/*  69:    */   public synchronized void disconnectionNotification(String eventName, Object source)
/*  70:    */   {
/*  71:171 */     if (this.m_listenee == source) {
/*  72:172 */       this.m_listenee = null;
/*  73:    */     }
/*  74:    */   }
/*  75:    */   
/*  76:    */   public void setLog(Logger logger)
/*  77:    */   {
/*  78:183 */     this.m_logger = logger;
/*  79:    */   }
/*  80:    */   
/*  81:    */   public abstract void stop();
/*  82:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.AbstractTrainingSetProducer
 * JD-Core Version:    0.7.0.1
 */