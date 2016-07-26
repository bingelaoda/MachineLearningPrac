/*   1:    */ package jsyntaxpane.actions.gui;
/*   2:    */ 
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.awt.Image;
/*   5:    */ import java.io.PrintStream;
/*   6:    */ import java.lang.reflect.Field;
/*   7:    */ import java.util.Map;
/*   8:    */ import javax.swing.JList;
/*   9:    */ 
/*  10:    */ class FieldCell
/*  11:    */   extends MemberCell
/*  12:    */ {
/*  13:    */   private final Field field;
/*  14:    */   
/*  15:    */   public FieldCell(JList list, boolean isSelected, Color backColor, Field field, Class clazz)
/*  16:    */   {
/*  17:187 */     super(list, isSelected, backColor, field, clazz);
/*  18:188 */     this.field = field;
/*  19:    */   }
/*  20:    */   
/*  21:    */   protected String getArguments()
/*  22:    */   {
/*  23:193 */     return "";
/*  24:    */   }
/*  25:    */   
/*  26:    */   protected String getReturnType()
/*  27:    */   {
/*  28:198 */     return this.field.getType().getSimpleName();
/*  29:    */   }
/*  30:    */   
/*  31:    */   protected Image getIcon()
/*  32:    */   {
/*  33:203 */     int type = this.field.getModifiers() & 0xF;
/*  34:204 */     if (icons == null) {
/*  35:205 */       icons = readIcons("/META-INF/images/completions/field");
/*  36:    */     }
/*  37:207 */     if (icons.get(Integer.valueOf(type)) == null) {
/*  38:208 */       System.err.println("Unable to get icon for type: " + this.field.getModifiers());
/*  39:    */     }
/*  40:210 */     return (Image)icons.get(Integer.valueOf(type));
/*  41:    */   }
/*  42:    */   
/*  43:212 */   private static Map<Integer, Image> icons = null;
/*  44:    */   public static final String FIELD_ICON_LOC = "/META-INF/images/completions/field";
/*  45:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.actions.gui.FieldCell
 * JD-Core Version:    0.7.0.1
 */