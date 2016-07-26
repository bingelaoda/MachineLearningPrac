/*  1:   */ package weka.gui;
/*  2:   */ 
/*  3:   */ import java.beans.PropertyEditor;
/*  4:   */ import javax.swing.ComboBoxModel;
/*  5:   */ import javax.swing.DefaultComboBoxModel;
/*  6:   */ import javax.swing.JComboBox;
/*  7:   */ 
/*  8:   */ class PropertyValueSelector
/*  9:   */   extends JComboBox
/* 10:   */ {
/* 11:   */   private static final long serialVersionUID = 128041237745933212L;
/* 12:   */   PropertyEditor m_Editor;
/* 13:   */   
/* 14:   */   public PropertyValueSelector(PropertyEditor pe)
/* 15:   */   {
/* 16:52 */     this.m_Editor = pe;
/* 17:53 */     Object value = this.m_Editor.getAsText();
/* 18:54 */     String[] tags = this.m_Editor.getTags();
/* 19:55 */     ComboBoxModel model = new DefaultComboBoxModel(tags)
/* 20:   */     {
/* 21:   */       private static final long serialVersionUID = 7942587653040180213L;
/* 22:   */       
/* 23:   */       public Object getSelectedItem()
/* 24:   */       {
/* 25:59 */         return PropertyValueSelector.this.m_Editor.getAsText();
/* 26:   */       }
/* 27:   */       
/* 28:   */       public void setSelectedItem(Object o)
/* 29:   */       {
/* 30:62 */         PropertyValueSelector.this.m_Editor.setAsText((String)o);
/* 31:   */       }
/* 32:64 */     };
/* 33:65 */     setModel(model);
/* 34:66 */     setSelectedItem(value);
/* 35:   */   }
/* 36:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.PropertyValueSelector
 * JD-Core Version:    0.7.0.1
 */