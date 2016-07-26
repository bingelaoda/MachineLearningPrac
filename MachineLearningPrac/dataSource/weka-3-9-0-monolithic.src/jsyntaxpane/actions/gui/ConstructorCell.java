/*   1:    */ package jsyntaxpane.actions.gui;
/*   2:    */ 
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.awt.Image;
/*   5:    */ import java.io.PrintStream;
/*   6:    */ import java.lang.reflect.Constructor;
/*   7:    */ import java.util.Map;
/*   8:    */ import javax.swing.JList;
/*   9:    */ import jsyntaxpane.util.ReflectUtils;
/*  10:    */ 
/*  11:    */ class ConstructorCell
/*  12:    */   extends MemberCell
/*  13:    */ {
/*  14:    */   private final Constructor cons;
/*  15:    */   
/*  16:    */   public ConstructorCell(JList list, boolean isSelected, Color backColor, Constructor cons, Class clazz)
/*  17:    */   {
/*  18:225 */     super(list, isSelected, backColor, cons, clazz);
/*  19:226 */     this.cons = cons;
/*  20:    */   }
/*  21:    */   
/*  22:    */   protected String getMemberName()
/*  23:    */   {
/*  24:231 */     return this.cons.getDeclaringClass().getSimpleName();
/*  25:    */   }
/*  26:    */   
/*  27:    */   protected String getArguments()
/*  28:    */   {
/*  29:236 */     return ReflectUtils.getParamsString(this.cons.getParameterTypes());
/*  30:    */   }
/*  31:    */   
/*  32:    */   protected String getReturnType()
/*  33:    */   {
/*  34:241 */     return this.cons.getDeclaringClass().getSimpleName();
/*  35:    */   }
/*  36:    */   
/*  37:    */   protected Image getIcon()
/*  38:    */   {
/*  39:246 */     int type = this.cons.getModifiers() & 0x7;
/*  40:247 */     if (icons == null) {
/*  41:248 */       icons = readIcons("/META-INF/images/completions/constructor");
/*  42:    */     }
/*  43:250 */     if (icons.get(Integer.valueOf(type)) == null) {
/*  44:251 */       System.out.println("Unable to get icon for type: " + this.cons.getModifiers());
/*  45:    */     }
/*  46:253 */     return (Image)icons.get(Integer.valueOf(type));
/*  47:    */   }
/*  48:    */   
/*  49:256 */   private static Map<Integer, Image> icons = null;
/*  50:    */   public static final String FIELD_ICON_LOC = "/META-INF/images/completions/constructor";
/*  51:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.actions.gui.ConstructorCell
 * JD-Core Version:    0.7.0.1
 */