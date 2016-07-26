/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.lang.reflect.Method;
/*   5:    */ 
/*   6:    */ public class EnumHelper
/*   7:    */ {
/*   8:    */   protected String m_enumClass;
/*   9:    */   protected String m_selectedEnumValue;
/*  10:    */   
/*  11:    */   public EnumHelper(Enum e)
/*  12:    */   {
/*  13: 53 */     this.m_selectedEnumValue = e.toString();
/*  14: 54 */     this.m_enumClass = e.getClass().getName();
/*  15:    */   }
/*  16:    */   
/*  17:    */   public EnumHelper() {}
/*  18:    */   
/*  19:    */   public void setEnumClass(String enumClass)
/*  20:    */   {
/*  21: 69 */     this.m_enumClass = enumClass;
/*  22:    */   }
/*  23:    */   
/*  24:    */   public String getEnumClass()
/*  25:    */   {
/*  26: 78 */     return this.m_enumClass;
/*  27:    */   }
/*  28:    */   
/*  29:    */   public void setSelectedEnumValue(String selectedEnumValue)
/*  30:    */   {
/*  31: 88 */     this.m_selectedEnumValue = selectedEnumValue;
/*  32:    */   }
/*  33:    */   
/*  34:    */   public String getSelectedEnumValue()
/*  35:    */   {
/*  36: 98 */     return this.m_selectedEnumValue;
/*  37:    */   }
/*  38:    */   
/*  39:    */   public static Object valueFromString(String enmumClass, String enumValue)
/*  40:    */     throws Exception
/*  41:    */   {
/*  42:113 */     Class<?> eClazz = Class.forName(enmumClass);
/*  43:114 */     Method valuesM = eClazz.getMethod("values", new Class[0]);
/*  44:    */     
/*  45:116 */     Enum[] values = (Enum[])valuesM.invoke(null, new Object[0]);
/*  46:117 */     for (Enum e : values) {
/*  47:118 */       if (e.toString().equals(enumValue)) {
/*  48:119 */         return e;
/*  49:    */       }
/*  50:    */     }
/*  51:123 */     return null;
/*  52:    */   }
/*  53:    */   
/*  54:    */   public static void main(String[] args)
/*  55:    */   {
/*  56:    */     try
/*  57:    */     {
/*  58:133 */       if (args.length != 2) {
/*  59:134 */         System.err.println("usage: weka.core.EnumHelper <enum class> <enum value>");
/*  60:    */       }
/*  61:138 */       Object eVal = valueFromString(args[0], args[1]);
/*  62:139 */       System.out.println("The enum's value is: " + eVal.toString());
/*  63:140 */       System.out.println("The enum's class is: " + eVal.getClass().toString());
/*  64:141 */       if ((eVal instanceof Enum)) {
/*  65:142 */         System.out.println("The value is an instance of Enum superclass");
/*  66:    */       }
/*  67:    */     }
/*  68:    */     catch (Exception ex)
/*  69:    */     {
/*  70:145 */       ex.printStackTrace();
/*  71:    */     }
/*  72:    */   }
/*  73:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.EnumHelper
 * JD-Core Version:    0.7.0.1
 */