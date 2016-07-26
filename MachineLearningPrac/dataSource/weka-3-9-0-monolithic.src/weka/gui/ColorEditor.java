/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.awt.Component;
/*   5:    */ import java.awt.Graphics;
/*   6:    */ import java.awt.Rectangle;
/*   7:    */ import java.beans.PropertyChangeListener;
/*   8:    */ import java.beans.PropertyEditor;
/*   9:    */ import javax.swing.JColorChooser;
/*  10:    */ 
/*  11:    */ public class ColorEditor
/*  12:    */   implements PropertyEditor
/*  13:    */ {
/*  14: 22 */   protected JColorChooser m_editor = new JColorChooser();
/*  15:    */   
/*  16:    */   public void setValue(Object value)
/*  17:    */   {
/*  18: 35 */     this.m_editor.setColor((Color)value);
/*  19:    */   }
/*  20:    */   
/*  21:    */   public Object getValue()
/*  22:    */   {
/*  23: 45 */     return this.m_editor.getColor();
/*  24:    */   }
/*  25:    */   
/*  26:    */   public boolean isPaintable()
/*  27:    */   {
/*  28: 55 */     return true;
/*  29:    */   }
/*  30:    */   
/*  31:    */   public void paintValue(Graphics gfx, Rectangle box)
/*  32:    */   {
/*  33: 66 */     Color c = this.m_editor.getColor();
/*  34: 67 */     gfx.setColor(c);
/*  35: 68 */     gfx.fillRect(box.x, box.y, box.width, box.height);
/*  36:    */   }
/*  37:    */   
/*  38:    */   public String getJavaInitializationString()
/*  39:    */   {
/*  40: 78 */     return "null";
/*  41:    */   }
/*  42:    */   
/*  43:    */   public String getAsText()
/*  44:    */   {
/*  45: 88 */     return null;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public void setAsText(String text)
/*  49:    */     throws IllegalArgumentException
/*  50:    */   {
/*  51: 99 */     throw new IllegalArgumentException(text);
/*  52:    */   }
/*  53:    */   
/*  54:    */   public String[] getTags()
/*  55:    */   {
/*  56:109 */     return null;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public Component getCustomEditor()
/*  60:    */   {
/*  61:119 */     return this.m_editor;
/*  62:    */   }
/*  63:    */   
/*  64:    */   public boolean supportsCustomEditor()
/*  65:    */   {
/*  66:129 */     return true;
/*  67:    */   }
/*  68:    */   
/*  69:    */   public void addPropertyChangeListener(PropertyChangeListener listener)
/*  70:    */   {
/*  71:134 */     this.m_editor.addPropertyChangeListener(listener);
/*  72:    */   }
/*  73:    */   
/*  74:    */   public void removePropertyChangeListener(PropertyChangeListener listener)
/*  75:    */   {
/*  76:139 */     this.m_editor.removePropertyChangeListener(listener);
/*  77:    */   }
/*  78:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.ColorEditor
 * JD-Core Version:    0.7.0.1
 */