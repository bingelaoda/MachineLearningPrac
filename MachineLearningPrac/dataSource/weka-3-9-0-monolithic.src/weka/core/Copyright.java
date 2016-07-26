/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Calendar;
/*   5:    */ import java.util.Properties;
/*   6:    */ 
/*   7:    */ public class Copyright
/*   8:    */ {
/*   9:    */   public static final String PROPERTY_FILE = "weka/core/Copyright.props";
/*  10: 41 */   protected static Properties PROPERTIES = new Properties();
/*  11:    */   
/*  12:    */   static
/*  13:    */   {
/*  14:    */     try
/*  15:    */     {
/*  16: 45 */       PROPERTIES.load(new Copyright().getClass().getClassLoader().getResourceAsStream("weka/core/Copyright.props"));
/*  17:    */     }
/*  18:    */     catch (Exception e)
/*  19:    */     {
/*  20: 49 */       System.err.println("Could not read configuration file for the copyright information - using default.");
/*  21:    */     }
/*  22:    */   }
/*  23:    */   
/*  24:    */   public static String getFromYear()
/*  25:    */   {
/*  26: 61 */     return PROPERTIES.getProperty("FromYear", "1999");
/*  27:    */   }
/*  28:    */   
/*  29:    */   public static String getToYear()
/*  30:    */   {
/*  31: 70 */     return PROPERTIES.getProperty("ToYear", "" + Calendar.getInstance().get(1));
/*  32:    */   }
/*  33:    */   
/*  34:    */   public static String getOwner()
/*  35:    */   {
/*  36: 79 */     return PROPERTIES.getProperty("Owner", "The University of Waikato");
/*  37:    */   }
/*  38:    */   
/*  39:    */   public static String getAddress()
/*  40:    */   {
/*  41: 88 */     return PROPERTIES.getProperty("Address", "Hamilton, New Zealand");
/*  42:    */   }
/*  43:    */   
/*  44:    */   public static String getURL()
/*  45:    */   {
/*  46: 97 */     return PROPERTIES.getProperty("URL", "http://www.cs.waikato.ac.nz/~ml/");
/*  47:    */   }
/*  48:    */   
/*  49:    */   public static void main(String[] args)
/*  50:    */   {
/*  51:106 */     System.out.println(PROPERTIES);
/*  52:    */   }
/*  53:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.Copyright
 * JD-Core Version:    0.7.0.1
 */