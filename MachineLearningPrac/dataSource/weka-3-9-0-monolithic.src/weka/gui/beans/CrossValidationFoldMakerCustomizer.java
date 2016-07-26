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
/*  16:    */ public class CrossValidationFoldMakerCustomizer
/*  17:    */   extends JPanel
/*  18:    */   implements BeanCustomizer, CustomizerCloseRequester, CustomizerClosingListener
/*  19:    */ {
/*  20:    */   private static final long serialVersionUID = 1229878140258668581L;
/*  21: 51 */   private PropertyChangeSupport m_pcSupport = new PropertyChangeSupport(this);
/*  22: 54 */   private PropertySheetPanel m_cvEditor = new PropertySheetPanel();
/*  23:    */   private CrossValidationFoldMaker m_cvMaker;
/*  24:    */   private BeanCustomizer.ModifyListener m_modifyListener;
/*  25:    */   private int m_foldsBackup;
/*  26:    */   private boolean m_orderBackup;
/*  27:    */   private int m_seedBackup;
/*  28:    */   private Window m_parent;
/*  29:    */   
/*  30:    */   public CrossValidationFoldMakerCustomizer()
/*  31:    */   {
/*  32: 66 */     setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
/*  33:    */     
/*  34: 68 */     setLayout(new BorderLayout());
/*  35: 69 */     add(this.m_cvEditor, "Center");
/*  36: 70 */     add(new JLabel("CrossValidationFoldMakerCustomizer"), "North");
/*  37:    */     
/*  38: 72 */     addButtons();
/*  39:    */   }
/*  40:    */   
/*  41:    */   private void addButtons()
/*  42:    */   {
/*  43: 76 */     JButton okBut = new JButton("OK");
/*  44: 77 */     JButton cancelBut = new JButton("Cancel");
/*  45:    */     
/*  46: 79 */     JPanel butHolder = new JPanel();
/*  47: 80 */     butHolder.setLayout(new GridLayout(1, 2));
/*  48: 81 */     butHolder.add(okBut);butHolder.add(cancelBut);
/*  49: 82 */     add(butHolder, "South");
/*  50:    */     
/*  51: 84 */     okBut.addActionListener(new ActionListener()
/*  52:    */     {
/*  53:    */       public void actionPerformed(ActionEvent e)
/*  54:    */       {
/*  55: 86 */         if (CrossValidationFoldMakerCustomizer.this.m_modifyListener != null) {
/*  56: 87 */           CrossValidationFoldMakerCustomizer.this.m_modifyListener.setModifiedStatus(CrossValidationFoldMakerCustomizer.this, true);
/*  57:    */         }
/*  58: 90 */         if (CrossValidationFoldMakerCustomizer.this.m_parent != null) {
/*  59: 91 */           CrossValidationFoldMakerCustomizer.this.m_parent.dispose();
/*  60:    */         }
/*  61:    */       }
/*  62: 95 */     });
/*  63: 96 */     cancelBut.addActionListener(new ActionListener()
/*  64:    */     {
/*  65:    */       public void actionPerformed(ActionEvent e)
/*  66:    */       {
/*  67: 99 */         CrossValidationFoldMakerCustomizer.this.customizerClosing();
/*  68:100 */         if (CrossValidationFoldMakerCustomizer.this.m_parent != null) {
/*  69:101 */           CrossValidationFoldMakerCustomizer.this.m_parent.dispose();
/*  70:    */         }
/*  71:    */       }
/*  72:    */     });
/*  73:    */   }
/*  74:    */   
/*  75:    */   public void setObject(Object object)
/*  76:    */   {
/*  77:113 */     this.m_cvMaker = ((CrossValidationFoldMaker)object);
/*  78:114 */     this.m_foldsBackup = this.m_cvMaker.getFolds();
/*  79:115 */     this.m_orderBackup = this.m_cvMaker.getPreserveOrder();
/*  80:116 */     this.m_seedBackup = this.m_cvMaker.getSeed();
/*  81:    */     
/*  82:118 */     this.m_cvEditor.setTarget(this.m_cvMaker);
/*  83:    */   }
/*  84:    */   
/*  85:    */   public void addPropertyChangeListener(PropertyChangeListener pcl)
/*  86:    */   {
/*  87:127 */     this.m_pcSupport.addPropertyChangeListener(pcl);
/*  88:    */   }
/*  89:    */   
/*  90:    */   public void removePropertyChangeListener(PropertyChangeListener pcl)
/*  91:    */   {
/*  92:136 */     this.m_pcSupport.removePropertyChangeListener(pcl);
/*  93:    */   }
/*  94:    */   
/*  95:    */   public void setModifiedListener(BeanCustomizer.ModifyListener l)
/*  96:    */   {
/*  97:141 */     this.m_modifyListener = l;
/*  98:    */   }
/*  99:    */   
/* 100:    */   public void setParentWindow(Window parent)
/* 101:    */   {
/* 102:146 */     this.m_parent = parent;
/* 103:    */   }
/* 104:    */   
/* 105:    */   public void customizerClosing()
/* 106:    */   {
/* 107:153 */     this.m_cvMaker.setSeed(this.m_seedBackup);
/* 108:154 */     this.m_cvMaker.setFolds(this.m_foldsBackup);
/* 109:155 */     this.m_cvMaker.setPreserveOrder(this.m_orderBackup);
/* 110:    */   }
/* 111:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.CrossValidationFoldMakerCustomizer
 * JD-Core Version:    0.7.0.1
 */