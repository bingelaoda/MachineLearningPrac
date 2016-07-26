/*  1:   */ package jsyntaxpane.actions.gui;
/*  2:   */ 
/*  3:   */ import java.awt.Color;
/*  4:   */ import java.awt.Component;
/*  5:   */ import java.lang.reflect.Constructor;
/*  6:   */ import java.lang.reflect.Field;
/*  7:   */ import java.lang.reflect.Method;
/*  8:   */ import javax.swing.DefaultListCellRenderer;
/*  9:   */ import javax.swing.JList;
/* 10:   */ 
/* 11:   */ class MembersListRenderer
/* 12:   */   extends DefaultListCellRenderer
/* 13:   */ {
/* 14:26 */   static final Color evensColor = new Color(15663086);
/* 15:   */   private ReflectCompletionDialog dlg;
/* 16:   */   
/* 17:   */   public MembersListRenderer(ReflectCompletionDialog dlg)
/* 18:   */   {
/* 19:30 */     this.dlg = dlg;
/* 20:   */   }
/* 21:   */   
/* 22:   */   public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
/* 23:   */   {
/* 24:36 */     Color back = index % 2 == 1 ? list.getBackground() : evensColor;
/* 25:37 */     if ((value instanceof Method))
/* 26:   */     {
/* 27:38 */       Method method = (Method)value;
/* 28:39 */       return new MethodCell(list, isSelected, back, method, this.dlg.getTheClass());
/* 29:   */     }
/* 30:40 */     if ((value instanceof Field))
/* 31:   */     {
/* 32:41 */       Field field = (Field)value;
/* 33:42 */       return new FieldCell(list, isSelected, back, field, this.dlg.getTheClass());
/* 34:   */     }
/* 35:43 */     if ((value instanceof Constructor))
/* 36:   */     {
/* 37:44 */       Constructor cons = (Constructor)value;
/* 38:45 */       return new ConstructorCell(list, isSelected, back, cons, this.dlg.getTheClass());
/* 39:   */     }
/* 40:47 */     Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
/* 41:48 */     comp.setBackground(back);
/* 42:49 */     return comp;
/* 43:   */   }
/* 44:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.actions.gui.MembersListRenderer
 * JD-Core Version:    0.7.0.1
 */