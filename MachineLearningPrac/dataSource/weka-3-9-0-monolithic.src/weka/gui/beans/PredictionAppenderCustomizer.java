/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.GridLayout;
/*   5:    */ import java.awt.Window;
/*   6:    */ import java.awt.event.ActionEvent;
/*   7:    */ import java.awt.event.ActionListener;
/*   8:    */ import java.beans.PropertyChangeListener;
/*   9:    */ import java.beans.PropertyChangeSupport;
/*  10:    */ import javax.swing.BorderFactory;
/*  11:    */ import javax.swing.JButton;
/*  12:    */ import javax.swing.JLabel;
/*  13:    */ import javax.swing.JPanel;
/*  14:    */ import weka.gui.PropertySheetPanel;
/*  15:    */ 
/*  16:    */ public class PredictionAppenderCustomizer
/*  17:    */   extends JPanel
/*  18:    */   implements BeanCustomizer, CustomizerCloseRequester, CustomizerClosingListener
/*  19:    */ {
/*  20:    */   private static final long serialVersionUID = 6884933202506331888L;
/*  21: 51 */   private PropertyChangeSupport m_pcSupport = new PropertyChangeSupport(this);
/*  22: 54 */   private PropertySheetPanel m_paEditor = new PropertySheetPanel();
/*  23:    */   private PredictionAppender m_appender;
/*  24:    */   private boolean m_appendProbsBackup;
/*  25:    */   private BeanCustomizer.ModifyListener m_modifyListener;
/*  26:    */   private Window m_parent;
/*  27:    */   
/*  28:    */   public PredictionAppenderCustomizer()
/*  29:    */   {
/*  30: 64 */     setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
/*  31:    */     
/*  32: 66 */     setLayout(new BorderLayout());
/*  33: 67 */     add(this.m_paEditor, "Center");
/*  34: 68 */     add(new JLabel("PredcitionAppenderCustomizer"), "North");
/*  35:    */     
/*  36: 70 */     addButtons();
/*  37:    */   }
/*  38:    */   
/*  39:    */   private void addButtons()
/*  40:    */   {
/*  41: 74 */     JButton okBut = new JButton("OK");
/*  42: 75 */     JButton cancelBut = new JButton("Cancel");
/*  43:    */     
/*  44: 77 */     JPanel butHolder = new JPanel();
/*  45: 78 */     butHolder.setLayout(new GridLayout(1, 2));
/*  46: 79 */     butHolder.add(okBut);butHolder.add(cancelBut);
/*  47: 80 */     add(butHolder, "South");
/*  48:    */     
/*  49: 82 */     okBut.addActionListener(new ActionListener()
/*  50:    */     {
/*  51:    */       public void actionPerformed(ActionEvent e)
/*  52:    */       {
/*  53: 84 */         PredictionAppenderCustomizer.this.m_modifyListener.setModifiedStatus(PredictionAppenderCustomizer.this, true);
/*  54: 85 */         if (PredictionAppenderCustomizer.this.m_parent != null) {
/*  55: 86 */           PredictionAppenderCustomizer.this.m_parent.dispose();
/*  56:    */         }
/*  57:    */       }
/*  58: 90 */     });
/*  59: 91 */     cancelBut.addActionListener(new ActionListener()
/*  60:    */     {
/*  61:    */       public void actionPerformed(ActionEvent e)
/*  62:    */       {
/*  63: 93 */         PredictionAppenderCustomizer.this.customizerClosing();
/*  64: 94 */         if (PredictionAppenderCustomizer.this.m_parent != null) {
/*  65: 95 */           PredictionAppenderCustomizer.this.m_parent.dispose();
/*  66:    */         }
/*  67:    */       }
/*  68:    */     });
/*  69:    */   }
/*  70:    */   
/*  71:    */   public void setObject(Object object)
/*  72:    */   {
/*  73:107 */     this.m_appender = ((PredictionAppender)object);
/*  74:108 */     this.m_paEditor.setTarget(this.m_appender);
/*  75:109 */     this.m_appendProbsBackup = this.m_appender.getAppendPredictedProbabilities();
/*  76:    */   }
/*  77:    */   
/*  78:    */   public void addPropertyChangeListener(PropertyChangeListener pcl)
/*  79:    */   {
/*  80:118 */     this.m_pcSupport.addPropertyChangeListener(pcl);
/*  81:    */   }
/*  82:    */   
/*  83:    */   public void removePropertyChangeListener(PropertyChangeListener pcl)
/*  84:    */   {
/*  85:127 */     this.m_pcSupport.removePropertyChangeListener(pcl);
/*  86:    */   }
/*  87:    */   
/*  88:    */   public void setModifiedListener(BeanCustomizer.ModifyListener l)
/*  89:    */   {
/*  90:132 */     this.m_modifyListener = l;
/*  91:    */   }
/*  92:    */   
/*  93:    */   public void setParentWindow(Window parent)
/*  94:    */   {
/*  95:137 */     this.m_parent = parent;
/*  96:    */   }
/*  97:    */   
/*  98:    */   public void customizerClosing()
/*  99:    */   {
/* 100:143 */     this.m_appender.setAppendPredictedProbabilities(this.m_appendProbsBackup);
/* 101:    */   }
/* 102:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.PredictionAppenderCustomizer
 * JD-Core Version:    0.7.0.1
 */