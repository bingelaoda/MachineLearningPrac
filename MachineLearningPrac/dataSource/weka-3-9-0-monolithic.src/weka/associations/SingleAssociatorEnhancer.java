/*   1:    */ package weka.associations;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.core.Capabilities;
/*   7:    */ import weka.core.Capabilities.Capability;
/*   8:    */ import weka.core.Option;
/*   9:    */ import weka.core.OptionHandler;
/*  10:    */ import weka.core.Utils;
/*  11:    */ 
/*  12:    */ public abstract class SingleAssociatorEnhancer
/*  13:    */   extends AbstractAssociator
/*  14:    */   implements OptionHandler
/*  15:    */ {
/*  16:    */   private static final long serialVersionUID = -3665885256363525164L;
/*  17: 49 */   protected Associator m_Associator = new Apriori();
/*  18:    */   
/*  19:    */   protected String defaultAssociatorString()
/*  20:    */   {
/*  21: 57 */     return Apriori.class.getName();
/*  22:    */   }
/*  23:    */   
/*  24:    */   public Enumeration<Option> listOptions()
/*  25:    */   {
/*  26: 67 */     Vector<Option> result = new Vector();
/*  27:    */     
/*  28: 69 */     result.addElement(new Option("\tFull name of base associator.\n\t(default: " + defaultAssociatorString() + ")", "W", 1, "-W"));
/*  29: 72 */     if ((this.m_Associator instanceof OptionHandler))
/*  30:    */     {
/*  31: 73 */       result.addElement(new Option("", "", 0, "\nOptions specific to associator " + this.m_Associator.getClass().getName() + ":"));
/*  32:    */       
/*  33:    */ 
/*  34:    */ 
/*  35: 77 */       result.addAll(Collections.list(((OptionHandler)this.m_Associator).listOptions()));
/*  36:    */     }
/*  37: 81 */     return result.elements();
/*  38:    */   }
/*  39:    */   
/*  40:    */   public void setOptions(String[] options)
/*  41:    */     throws Exception
/*  42:    */   {
/*  43:102 */     String tmpStr = Utils.getOption('W', options);
/*  44:103 */     if (tmpStr.length() > 0)
/*  45:    */     {
/*  46:106 */       setAssociator(AbstractAssociator.forName(tmpStr, null));
/*  47:107 */       setAssociator(AbstractAssociator.forName(tmpStr, Utils.partitionOptions(options)));
/*  48:    */     }
/*  49:    */     else
/*  50:    */     {
/*  51:112 */       setAssociator(AbstractAssociator.forName(defaultAssociatorString(), null));
/*  52:113 */       setAssociator(AbstractAssociator.forName(defaultAssociatorString(), Utils.partitionOptions(options)));
/*  53:    */     }
/*  54:    */   }
/*  55:    */   
/*  56:    */   public String[] getOptions()
/*  57:    */   {
/*  58:129 */     Vector<String> result = new Vector();
/*  59:    */     
/*  60:131 */     result.add("-W");
/*  61:132 */     result.add(getAssociator().getClass().getName());
/*  62:134 */     if ((getAssociator() instanceof OptionHandler))
/*  63:    */     {
/*  64:135 */       String[] options = ((OptionHandler)getAssociator()).getOptions();
/*  65:136 */       result.add("--");
/*  66:137 */       for (int i = 0; i < options.length; i++) {
/*  67:138 */         result.add(options[i]);
/*  68:    */       }
/*  69:    */     }
/*  70:142 */     return (String[])result.toArray(new String[result.size()]);
/*  71:    */   }
/*  72:    */   
/*  73:    */   public String associatorTipText()
/*  74:    */   {
/*  75:152 */     return "The base associator to be used.";
/*  76:    */   }
/*  77:    */   
/*  78:    */   public void setAssociator(Associator value)
/*  79:    */   {
/*  80:161 */     this.m_Associator = value;
/*  81:    */   }
/*  82:    */   
/*  83:    */   public Associator getAssociator()
/*  84:    */   {
/*  85:170 */     return this.m_Associator;
/*  86:    */   }
/*  87:    */   
/*  88:    */   protected String getAssociatorSpec()
/*  89:    */   {
/*  90:180 */     Associator c = getAssociator();
/*  91:181 */     return c.getClass().getName() + " " + Utils.joinOptions(((OptionHandler)c).getOptions());
/*  92:    */   }
/*  93:    */   
/*  94:    */   public Capabilities getCapabilities()
/*  95:    */   {
/*  96:    */     Capabilities result;
/*  97:    */     Capabilities result;
/*  98:194 */     if (getAssociator() != null) {
/*  99:195 */       result = getAssociator().getCapabilities();
/* 100:    */     } else {
/* 101:197 */       result = new Capabilities(this);
/* 102:    */     }
/* 103:201 */     for (Capabilities.Capability cap : Capabilities.Capability.values()) {
/* 104:202 */       result.enableDependency(cap);
/* 105:    */     }
/* 106:205 */     result.setOwner(this);
/* 107:    */     
/* 108:207 */     return result;
/* 109:    */   }
/* 110:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.associations.SingleAssociatorEnhancer
 * JD-Core Version:    0.7.0.1
 */