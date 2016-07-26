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
/*  16:    */ public class IncrementalClassifierEvaluatorCustomizer
/*  17:    */   extends JPanel
/*  18:    */   implements BeanCustomizer, CustomizerCloseRequester, CustomizerClosingListener
/*  19:    */ {
/*  20:    */   private static final long serialVersionUID = 443506897387629418L;
/*  21: 52 */   private final PropertyChangeSupport m_pcSupport = new PropertyChangeSupport(this);
/*  22: 55 */   private final PropertySheetPanel m_ieEditor = new PropertySheetPanel();
/*  23:    */   private IncrementalClassifierEvaluator m_evaluator;
/*  24:    */   private BeanCustomizer.ModifyListener m_modifyListener;
/*  25:    */   private Window m_parent;
/*  26:    */   private int m_freqBackup;
/*  27:    */   private boolean m_perClassBackup;
/*  28:    */   
/*  29:    */   public IncrementalClassifierEvaluatorCustomizer()
/*  30:    */   {
/*  31: 66 */     setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
/*  32:    */     
/*  33: 68 */     setLayout(new BorderLayout());
/*  34: 69 */     add(this.m_ieEditor, "Center");
/*  35: 70 */     add(new JLabel("IncrementalClassifierEvaluatorCustomizer"), "North");
/*  36:    */     
/*  37: 72 */     addButtons();
/*  38:    */   }
/*  39:    */   
/*  40:    */   private void addButtons()
/*  41:    */   {
/*  42: 76 */     JButton okBut = new JButton("OK");
/*  43: 77 */     JButton cancelBut = new JButton("Cancel");
/*  44:    */     
/*  45: 79 */     JPanel butHolder = new JPanel();
/*  46: 80 */     butHolder.setLayout(new GridLayout(1, 2));
/*  47: 81 */     butHolder.add(okBut);
/*  48: 82 */     butHolder.add(cancelBut);
/*  49: 83 */     add(butHolder, "South");
/*  50:    */     
/*  51: 85 */     okBut.addActionListener(new ActionListener()
/*  52:    */     {
/*  53:    */       public void actionPerformed(ActionEvent e)
/*  54:    */       {
/*  55: 88 */         IncrementalClassifierEvaluatorCustomizer.this.m_modifyListener.setModifiedStatus(IncrementalClassifierEvaluatorCustomizer.this, true);
/*  56: 90 */         if (IncrementalClassifierEvaluatorCustomizer.this.m_parent != null) {
/*  57: 91 */           IncrementalClassifierEvaluatorCustomizer.this.m_parent.dispose();
/*  58:    */         }
/*  59:    */       }
/*  60: 95 */     });
/*  61: 96 */     cancelBut.addActionListener(new ActionListener()
/*  62:    */     {
/*  63:    */       public void actionPerformed(ActionEvent e)
/*  64:    */       {
/*  65: 99 */         IncrementalClassifierEvaluatorCustomizer.this.customizerClosing();
/*  66:100 */         if (IncrementalClassifierEvaluatorCustomizer.this.m_parent != null) {
/*  67:101 */           IncrementalClassifierEvaluatorCustomizer.this.m_parent.dispose();
/*  68:    */         }
/*  69:    */       }
/*  70:    */     });
/*  71:    */   }
/*  72:    */   
/*  73:    */   public void setObject(Object object)
/*  74:    */   {
/*  75:114 */     this.m_evaluator = ((IncrementalClassifierEvaluator)object);
/*  76:115 */     this.m_ieEditor.setTarget(this.m_evaluator);
/*  77:116 */     this.m_freqBackup = this.m_evaluator.getStatusFrequency();
/*  78:117 */     this.m_perClassBackup = this.m_evaluator.getOutputPerClassInfoRetrievalStats();
/*  79:    */   }
/*  80:    */   
/*  81:    */   public void addPropertyChangeListener(PropertyChangeListener pcl)
/*  82:    */   {
/*  83:127 */     this.m_pcSupport.addPropertyChangeListener(pcl);
/*  84:    */   }
/*  85:    */   
/*  86:    */   public void removePropertyChangeListener(PropertyChangeListener pcl)
/*  87:    */   {
/*  88:137 */     this.m_pcSupport.removePropertyChangeListener(pcl);
/*  89:    */   }
/*  90:    */   
/*  91:    */   public void setModifiedListener(BeanCustomizer.ModifyListener l)
/*  92:    */   {
/*  93:142 */     this.m_modifyListener = l;
/*  94:    */   }
/*  95:    */   
/*  96:    */   public void setParentWindow(Window parent)
/*  97:    */   {
/*  98:147 */     this.m_parent = parent;
/*  99:    */   }
/* 100:    */   
/* 101:    */   public void customizerClosing()
/* 102:    */   {
/* 103:153 */     this.m_evaluator.setStatusFrequency(this.m_freqBackup);
/* 104:154 */     this.m_evaluator.setOutputPerClassInfoRetrievalStats(this.m_perClassBackup);
/* 105:    */   }
/* 106:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.IncrementalClassifierEvaluatorCustomizer
 * JD-Core Version:    0.7.0.1
 */