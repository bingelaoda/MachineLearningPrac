/*   1:    */ package weka.filters;
/*   2:    */ 
/*   3:    */ import weka.core.Capabilities;
/*   4:    */ import weka.core.Capabilities.Capability;
/*   5:    */ import weka.core.Instance;
/*   6:    */ import weka.core.Instances;
/*   7:    */ import weka.core.RevisionUtils;
/*   8:    */ 
/*   9:    */ public class AllFilter
/*  10:    */   extends Filter
/*  11:    */   implements Sourcable
/*  12:    */ {
/*  13:    */   static final long serialVersionUID = 5022109283147503266L;
/*  14:    */   
/*  15:    */   public String globalInfo()
/*  16:    */   {
/*  17: 52 */     return "An instance filter that passes all instances through unmodified. Primarily for testing purposes.";
/*  18:    */   }
/*  19:    */   
/*  20:    */   public Capabilities getCapabilities()
/*  21:    */   {
/*  22: 63 */     Capabilities result = super.getCapabilities();
/*  23: 64 */     result.disableAll();
/*  24:    */     
/*  25:    */ 
/*  26: 67 */     result.enableAllAttributes();
/*  27: 68 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  28:    */     
/*  29:    */ 
/*  30: 71 */     result.enableAllClasses();
/*  31: 72 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  32: 73 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  33:    */     
/*  34: 75 */     return result;
/*  35:    */   }
/*  36:    */   
/*  37:    */   public boolean setInputFormat(Instances instanceInfo)
/*  38:    */     throws Exception
/*  39:    */   {
/*  40: 90 */     super.setInputFormat(instanceInfo);
/*  41: 91 */     setOutputFormat(instanceInfo);
/*  42: 92 */     return true;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public boolean input(Instance instance)
/*  46:    */   {
/*  47:108 */     if (getInputFormat() == null) {
/*  48:109 */       throw new IllegalStateException("No input instance format defined");
/*  49:    */     }
/*  50:111 */     if (this.m_NewBatch)
/*  51:    */     {
/*  52:112 */       resetQueue();
/*  53:113 */       this.m_NewBatch = false;
/*  54:    */     }
/*  55:115 */     if (instance.dataset() == null) {
/*  56:116 */       push((Instance)instance.copy());
/*  57:    */     } else {
/*  58:118 */       push(instance);
/*  59:    */     }
/*  60:120 */     return true;
/*  61:    */   }
/*  62:    */   
/*  63:    */   public String toSource(String className, Instances data)
/*  64:    */     throws Exception
/*  65:    */   {
/*  66:146 */     StringBuffer result = new StringBuffer();
/*  67:    */     
/*  68:148 */     result.append("class " + className + " {\n");
/*  69:149 */     result.append("\n");
/*  70:150 */     result.append("  /**\n");
/*  71:151 */     result.append("   * filters a single row\n");
/*  72:152 */     result.append("   * \n");
/*  73:153 */     result.append("   * @param i the row to process\n");
/*  74:154 */     result.append("   * @return the processed row\n");
/*  75:155 */     result.append("   */\n");
/*  76:156 */     result.append("  public static Object[] filter(Object[] i) {\n");
/*  77:157 */     result.append("    return i;\n");
/*  78:158 */     result.append("  }\n");
/*  79:159 */     result.append("\n");
/*  80:160 */     result.append("  /**\n");
/*  81:161 */     result.append("   * filters multiple rows\n");
/*  82:162 */     result.append("   * \n");
/*  83:163 */     result.append("   * @param i the rows to process\n");
/*  84:164 */     result.append("   * @return the processed rows\n");
/*  85:165 */     result.append("   */\n");
/*  86:166 */     result.append("  public static Object[][] filter(Object[][] i) {\n");
/*  87:167 */     result.append("    return i;\n");
/*  88:168 */     result.append("  }\n");
/*  89:169 */     result.append("}\n");
/*  90:    */     
/*  91:171 */     return result.toString();
/*  92:    */   }
/*  93:    */   
/*  94:    */   public String getRevision()
/*  95:    */   {
/*  96:180 */     return RevisionUtils.extract("$Revision: 12037 $");
/*  97:    */   }
/*  98:    */   
/*  99:    */   public static void main(String[] argv)
/* 100:    */   {
/* 101:189 */     runFilter(new AllFilter(), argv);
/* 102:    */   }
/* 103:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.AllFilter
 * JD-Core Version:    0.7.0.1
 */