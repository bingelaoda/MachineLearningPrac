/*   1:    */ package jsyntaxpane.actions.gui;
/*   2:    */ 
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.awt.Image;
/*   5:    */ import java.lang.reflect.Method;
/*   6:    */ import java.util.Map;
/*   7:    */ import javax.swing.JList;
/*   8:    */ import jsyntaxpane.util.ReflectUtils;
/*   9:    */ 
/*  10:    */ class MethodCell
/*  11:    */   extends MemberCell
/*  12:    */ {
/*  13:    */   private final Method method;
/*  14:    */   
/*  15:    */   public MethodCell(JList list, boolean isSelected, Color backColor, Method method, Class clazz)
/*  16:    */   {
/*  17:152 */     super(list, isSelected, backColor, method, clazz);
/*  18:153 */     this.method = method;
/*  19:    */   }
/*  20:    */   
/*  21:    */   protected String getArguments()
/*  22:    */   {
/*  23:158 */     return ReflectUtils.getParamsString(this.method.getParameterTypes());
/*  24:    */   }
/*  25:    */   
/*  26:    */   protected String getReturnType()
/*  27:    */   {
/*  28:163 */     return this.method.getReturnType().getSimpleName();
/*  29:    */   }
/*  30:    */   
/*  31:    */   protected Image getIcon()
/*  32:    */   {
/*  33:168 */     int type = this.method.getModifiers() & 0xF;
/*  34:169 */     if (icons == null) {
/*  35:170 */       icons = readIcons("/META-INF/images/completions/method");
/*  36:    */     }
/*  37:172 */     return (Image)icons.get(Integer.valueOf(type));
/*  38:    */   }
/*  39:    */   
/*  40:174 */   private static Map<Integer, Image> icons = null;
/*  41:    */   public static final String METHOD_ICON_LOC = "/META-INF/images/completions/method";
/*  42:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.actions.gui.MethodCell
 * JD-Core Version:    0.7.0.1
 */