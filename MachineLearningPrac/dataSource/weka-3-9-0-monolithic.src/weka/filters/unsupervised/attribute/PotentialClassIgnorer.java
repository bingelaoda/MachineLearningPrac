/*   1:    */ package weka.filters.unsupervised.attribute;
/*   2:    */ 
/*   3:    */ import java.util.Enumeration;
/*   4:    */ import java.util.Vector;
/*   5:    */ import weka.core.Instances;
/*   6:    */ import weka.core.Option;
/*   7:    */ import weka.core.OptionHandler;
/*   8:    */ import weka.core.Utils;
/*   9:    */ import weka.filters.Filter;
/*  10:    */ 
/*  11:    */ public abstract class PotentialClassIgnorer
/*  12:    */   extends Filter
/*  13:    */   implements OptionHandler
/*  14:    */ {
/*  15:    */   private static final long serialVersionUID = 8625371119276845454L;
/*  16: 51 */   protected boolean m_IgnoreClass = false;
/*  17: 54 */   protected int m_ClassIndex = -1;
/*  18:    */   
/*  19:    */   public Enumeration<Option> listOptions()
/*  20:    */   {
/*  21: 64 */     Vector<Option> result = new Vector();
/*  22:    */     
/*  23: 66 */     result.addElement(new Option("\tUnsets the class index temporarily before the filter is\n\tapplied to the data.\n\t(default: no)", "unset-class-temporarily", 1, "-unset-class-temporarily"));
/*  24:    */     
/*  25:    */ 
/*  26:    */ 
/*  27:    */ 
/*  28: 71 */     return result.elements();
/*  29:    */   }
/*  30:    */   
/*  31:    */   public void setOptions(String[] options)
/*  32:    */     throws Exception
/*  33:    */   {
/*  34: 82 */     setIgnoreClass(Utils.getFlag("unset-class-temporarily", options));
/*  35:    */   }
/*  36:    */   
/*  37:    */   public String[] getOptions()
/*  38:    */   {
/*  39: 93 */     Vector<String> result = new Vector();
/*  40: 95 */     if (getIgnoreClass()) {
/*  41: 96 */       result.add("-unset-class-temporarily");
/*  42:    */     }
/*  43: 99 */     return (String[])result.toArray(new String[result.size()]);
/*  44:    */   }
/*  45:    */   
/*  46:    */   public boolean setInputFormat(Instances instanceInfo)
/*  47:    */     throws Exception
/*  48:    */   {
/*  49:118 */     boolean result = super.setInputFormat(instanceInfo);
/*  50:119 */     if (this.m_IgnoreClass)
/*  51:    */     {
/*  52:120 */       this.m_ClassIndex = inputFormatPeek().classIndex();
/*  53:121 */       inputFormatPeek().setClassIndex(-1);
/*  54:    */     }
/*  55:123 */     return result;
/*  56:    */   }
/*  57:    */   
/*  58:    */   public Instances getOutputFormat()
/*  59:    */   {
/*  60:139 */     if (this.m_IgnoreClass) {
/*  61:140 */       outputFormatPeek().setClassIndex(this.m_ClassIndex);
/*  62:    */     }
/*  63:142 */     return super.getOutputFormat();
/*  64:    */   }
/*  65:    */   
/*  66:    */   public String ignoreClassTipText()
/*  67:    */   {
/*  68:152 */     return "The class index will be unset temporarily before the filter is applied.";
/*  69:    */   }
/*  70:    */   
/*  71:    */   public void setIgnoreClass(boolean newIgnoreClass)
/*  72:    */   {
/*  73:162 */     this.m_IgnoreClass = newIgnoreClass;
/*  74:    */   }
/*  75:    */   
/*  76:    */   public boolean getIgnoreClass()
/*  77:    */   {
/*  78:172 */     return this.m_IgnoreClass;
/*  79:    */   }
/*  80:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.PotentialClassIgnorer
 * JD-Core Version:    0.7.0.1
 */