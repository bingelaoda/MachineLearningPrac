/*   1:    */ package weka.classifiers.meta.generators;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.core.Option;
/*   7:    */ import weka.core.OptionHandler;
/*   8:    */ import weka.core.Utils;
/*   9:    */ 
/*  10:    */ public abstract class Generator
/*  11:    */   implements Serializable, OptionHandler
/*  12:    */ {
/*  13:    */   private static final long serialVersionUID = 2412127331483792089L;
/*  14: 43 */   protected boolean m_Debug = false;
/*  15:    */   
/*  16:    */   public abstract double getProbabilityOf(double paramDouble);
/*  17:    */   
/*  18:    */   public abstract double getLogProbabilityOf(double paramDouble);
/*  19:    */   
/*  20:    */   public abstract double generate();
/*  21:    */   
/*  22:    */   public Generator copy()
/*  23:    */   {
/*  24:    */     Generator result;
/*  25:    */     try
/*  26:    */     {
/*  27: 79 */       result = (Generator)getClass().newInstance();
/*  28: 80 */       result.setOptions(getOptions());
/*  29:    */     }
/*  30:    */     catch (Exception e)
/*  31:    */     {
/*  32: 82 */       e.printStackTrace();
/*  33: 83 */       result = null;
/*  34:    */     }
/*  35: 86 */     return result;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public static Generator forName(String generatorName, String[] options)
/*  39:    */     throws Exception
/*  40:    */   {
/*  41:105 */     return (Generator)Utils.forName(Generator.class, generatorName, options);
/*  42:    */   }
/*  43:    */   
/*  44:    */   public abstract String globalInfo();
/*  45:    */   
/*  46:    */   public Enumeration<Option> listOptions()
/*  47:    */   {
/*  48:123 */     Vector<Option> result = new Vector();
/*  49:    */     
/*  50:125 */     result.addElement(new Option("\tIf set, generator is run in debug mode and\n\tmay output additional info to the console", "output-debug-info", 0, "-output-debug-info"));
/*  51:    */     
/*  52:    */ 
/*  53:    */ 
/*  54:    */ 
/*  55:130 */     return result.elements();
/*  56:    */   }
/*  57:    */   
/*  58:    */   public void setOptions(String[] options)
/*  59:    */     throws Exception
/*  60:    */   {
/*  61:142 */     setDebug(Utils.getFlag("output-debug-info", options));
/*  62:    */   }
/*  63:    */   
/*  64:    */   public String[] getOptions()
/*  65:    */   {
/*  66:153 */     Vector<String> result = new Vector();
/*  67:155 */     if (getDebug()) {
/*  68:156 */       result.add("-output-debug-info");
/*  69:    */     }
/*  70:159 */     return (String[])result.toArray(new String[result.size()]);
/*  71:    */   }
/*  72:    */   
/*  73:    */   public void setDebug(boolean debug)
/*  74:    */   {
/*  75:168 */     this.m_Debug = debug;
/*  76:    */   }
/*  77:    */   
/*  78:    */   public boolean getDebug()
/*  79:    */   {
/*  80:177 */     return this.m_Debug;
/*  81:    */   }
/*  82:    */   
/*  83:    */   public String debugTipText()
/*  84:    */   {
/*  85:187 */     return "If set to true, the generator might output debugging information in the console.";
/*  86:    */   }
/*  87:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.meta.generators.Generator
 * JD-Core Version:    0.7.0.1
 */