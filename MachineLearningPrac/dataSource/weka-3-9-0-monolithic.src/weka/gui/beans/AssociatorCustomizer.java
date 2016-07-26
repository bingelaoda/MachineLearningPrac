/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.GridLayout;
/*   5:    */ import java.awt.Window;
/*   6:    */ import java.awt.event.ActionEvent;
/*   7:    */ import java.awt.event.ActionListener;
/*   8:    */ import java.beans.PropertyChangeListener;
/*   9:    */ import java.beans.PropertyChangeSupport;
/*  10:    */ import javax.swing.JButton;
/*  11:    */ import javax.swing.JPanel;
/*  12:    */ import weka.gui.GenericObjectEditor;
/*  13:    */ import weka.gui.PropertySheetPanel;
/*  14:    */ 
/*  15:    */ public class AssociatorCustomizer
/*  16:    */   extends JPanel
/*  17:    */   implements BeanCustomizer, CustomizerCloseRequester
/*  18:    */ {
/*  19:    */   private static final long serialVersionUID = 5767664969353495974L;
/*  20: 55 */   private PropertyChangeSupport m_pcSupport = new PropertyChangeSupport(this);
/*  21:    */   private Associator m_dsAssociator;
/*  22: 61 */   private PropertySheetPanel m_AssociatorEditor = new PropertySheetPanel();
/*  23:    */   protected Window m_parentWindow;
/*  24:    */   private weka.associations.Associator m_backup;
/*  25:    */   private BeanCustomizer.ModifyListener m_modifyListener;
/*  26:    */   
/*  27:    */   public AssociatorCustomizer()
/*  28:    */   {
/*  29: 72 */     setLayout(new BorderLayout());
/*  30: 73 */     add(this.m_AssociatorEditor, "Center");
/*  31:    */     
/*  32: 75 */     JPanel butHolder = new JPanel();
/*  33: 76 */     butHolder.setLayout(new GridLayout(1, 2));
/*  34: 77 */     JButton OKBut = new JButton("OK");
/*  35: 78 */     OKBut.addActionListener(new ActionListener()
/*  36:    */     {
/*  37:    */       public void actionPerformed(ActionEvent e)
/*  38:    */       {
/*  39: 81 */         if (AssociatorCustomizer.this.m_modifyListener != null) {
/*  40: 82 */           AssociatorCustomizer.this.m_modifyListener.setModifiedStatus(AssociatorCustomizer.this, true);
/*  41:    */         }
/*  42: 85 */         AssociatorCustomizer.this.m_parentWindow.dispose();
/*  43:    */       }
/*  44: 88 */     });
/*  45: 89 */     JButton CancelBut = new JButton("Cancel");
/*  46: 90 */     CancelBut.addActionListener(new ActionListener()
/*  47:    */     {
/*  48:    */       public void actionPerformed(ActionEvent e)
/*  49:    */       {
/*  50: 94 */         if (AssociatorCustomizer.this.m_backup != null) {
/*  51: 95 */           AssociatorCustomizer.this.m_dsAssociator.setAssociator(AssociatorCustomizer.this.m_backup);
/*  52:    */         }
/*  53: 98 */         if (AssociatorCustomizer.this.m_modifyListener != null) {
/*  54: 99 */           AssociatorCustomizer.this.m_modifyListener.setModifiedStatus(AssociatorCustomizer.this, false);
/*  55:    */         }
/*  56:102 */         AssociatorCustomizer.this.m_parentWindow.dispose();
/*  57:    */       }
/*  58:105 */     });
/*  59:106 */     butHolder.add(OKBut);
/*  60:107 */     butHolder.add(CancelBut);
/*  61:108 */     add(butHolder, "South");
/*  62:    */   }
/*  63:    */   
/*  64:    */   public void setObject(Object object)
/*  65:    */   {
/*  66:117 */     this.m_dsAssociator = ((Associator)object);
/*  67:    */     try
/*  68:    */     {
/*  69:120 */       this.m_backup = ((weka.associations.Associator)GenericObjectEditor.makeCopy(this.m_dsAssociator.getAssociator()));
/*  70:    */     }
/*  71:    */     catch (Exception ex) {}
/*  72:126 */     this.m_AssociatorEditor.setTarget(this.m_dsAssociator.getAssociator());
/*  73:    */   }
/*  74:    */   
/*  75:    */   public void addPropertyChangeListener(PropertyChangeListener pcl)
/*  76:    */   {
/*  77:135 */     this.m_pcSupport.addPropertyChangeListener(pcl);
/*  78:    */   }
/*  79:    */   
/*  80:    */   public void removePropertyChangeListener(PropertyChangeListener pcl)
/*  81:    */   {
/*  82:144 */     this.m_pcSupport.removePropertyChangeListener(pcl);
/*  83:    */   }
/*  84:    */   
/*  85:    */   public void setParentWindow(Window parent)
/*  86:    */   {
/*  87:148 */     this.m_parentWindow = parent;
/*  88:    */   }
/*  89:    */   
/*  90:    */   public void setModifiedListener(BeanCustomizer.ModifyListener l)
/*  91:    */   {
/*  92:153 */     this.m_modifyListener = l;
/*  93:    */   }
/*  94:    */   
/*  95:    */   static {}
/*  96:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.AssociatorCustomizer
 * JD-Core Version:    0.7.0.1
 */