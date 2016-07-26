/*   1:    */ package weka.associations;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.io.Serializable;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.core.Capabilities;
/*   8:    */ import weka.core.CapabilitiesHandler;
/*   9:    */ import weka.core.CapabilitiesIgnorer;
/*  10:    */ import weka.core.CommandlineRunnable;
/*  11:    */ import weka.core.Option;
/*  12:    */ import weka.core.OptionHandler;
/*  13:    */ import weka.core.RevisionHandler;
/*  14:    */ import weka.core.RevisionUtils;
/*  15:    */ import weka.core.SerializedObject;
/*  16:    */ import weka.core.Utils;
/*  17:    */ 
/*  18:    */ public abstract class AbstractAssociator
/*  19:    */   implements Cloneable, Associator, Serializable, CapabilitiesHandler, CapabilitiesIgnorer, RevisionHandler, OptionHandler, CommandlineRunnable
/*  20:    */ {
/*  21:    */   private static final long serialVersionUID = -3017644543382432070L;
/*  22: 54 */   protected boolean m_DoNotCheckCapabilities = false;
/*  23:    */   
/*  24:    */   public Enumeration<Option> listOptions()
/*  25:    */   {
/*  26: 63 */     Vector<Option> newVector = Option.listOptionsForClassHierarchy(getClass(), AbstractAssociator.class);
/*  27:    */     
/*  28:    */ 
/*  29: 66 */     return newVector.elements();
/*  30:    */   }
/*  31:    */   
/*  32:    */   public void setOptions(String[] options)
/*  33:    */     throws Exception
/*  34:    */   {
/*  35: 77 */     Option.setOptionsForHierarchy(options, this, AbstractAssociator.class);
/*  36:    */   }
/*  37:    */   
/*  38:    */   public String[] getOptions()
/*  39:    */   {
/*  40: 86 */     Vector<String> options = new Vector();
/*  41: 87 */     for (String s : Option.getOptionsForHierarchy(this, AbstractAssociator.class)) {
/*  42: 89 */       options.add(s);
/*  43:    */     }
/*  44: 91 */     return (String[])options.toArray(new String[0]);
/*  45:    */   }
/*  46:    */   
/*  47:    */   public String doNotCheckCapabilitiesTipText()
/*  48:    */   {
/*  49:101 */     return "If set, associator capabilities are not checked before associator is built (Use with caution to reduce runtime).";
/*  50:    */   }
/*  51:    */   
/*  52:    */   public void setDoNotCheckCapabilities(boolean doNotCheckCapabilities)
/*  53:    */   {
/*  54:112 */     this.m_DoNotCheckCapabilities = doNotCheckCapabilities;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public boolean getDoNotCheckCapabilities()
/*  58:    */   {
/*  59:122 */     return this.m_DoNotCheckCapabilities;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public static Associator forName(String associatorName, String[] options)
/*  63:    */     throws Exception
/*  64:    */   {
/*  65:141 */     return (Associator)Utils.forName(Associator.class, associatorName, options);
/*  66:    */   }
/*  67:    */   
/*  68:    */   public static Associator makeCopy(Associator model)
/*  69:    */     throws Exception
/*  70:    */   {
/*  71:153 */     return (Associator)new SerializedObject(model).getObject();
/*  72:    */   }
/*  73:    */   
/*  74:    */   public static Associator[] makeCopies(Associator model, int num)
/*  75:    */     throws Exception
/*  76:    */   {
/*  77:169 */     if (model == null) {
/*  78:170 */       throw new Exception("No model associator set");
/*  79:    */     }
/*  80:172 */     Associator[] associators = new Associator[num];
/*  81:173 */     SerializedObject so = new SerializedObject(model);
/*  82:174 */     for (int i = 0; i < associators.length; i++) {
/*  83:175 */       associators[i] = ((Associator)so.getObject());
/*  84:    */     }
/*  85:177 */     return associators;
/*  86:    */   }
/*  87:    */   
/*  88:    */   public Capabilities getCapabilities()
/*  89:    */   {
/*  90:190 */     Capabilities defaultC = new Capabilities(this);
/*  91:191 */     defaultC.enableAll();
/*  92:    */     
/*  93:193 */     return defaultC;
/*  94:    */   }
/*  95:    */   
/*  96:    */   public String getRevision()
/*  97:    */   {
/*  98:202 */     return RevisionUtils.extract("$Revision: 12201 $");
/*  99:    */   }
/* 100:    */   
/* 101:    */   public static void runAssociator(Associator associator, String[] options)
/* 102:    */   {
/* 103:    */     try
/* 104:    */     {
/* 105:213 */       if ((associator instanceof CommandlineRunnable)) {
/* 106:214 */         ((CommandlineRunnable)associator).preExecution();
/* 107:    */       }
/* 108:216 */       System.out.println(AssociatorEvaluation.evaluate(associator, options));
/* 109:    */     }
/* 110:    */     catch (Exception e)
/* 111:    */     {
/* 112:218 */       if ((e.getMessage() != null) && (e.getMessage().indexOf("General options") == -1)) {
/* 113:220 */         e.printStackTrace();
/* 114:    */       } else {
/* 115:222 */         System.err.println(e.getMessage());
/* 116:    */       }
/* 117:    */     }
/* 118:    */     try
/* 119:    */     {
/* 120:225 */       if ((associator instanceof CommandlineRunnable)) {
/* 121:226 */         ((CommandlineRunnable)associator).postExecution();
/* 122:    */       }
/* 123:    */     }
/* 124:    */     catch (Exception ex)
/* 125:    */     {
/* 126:229 */       ex.printStackTrace();
/* 127:    */     }
/* 128:    */   }
/* 129:    */   
/* 130:    */   public void preExecution()
/* 131:    */     throws Exception
/* 132:    */   {}
/* 133:    */   
/* 134:    */   public void run(Object toRun, String[] options)
/* 135:    */     throws Exception
/* 136:    */   {
/* 137:252 */     if (!(toRun instanceof Associator)) {
/* 138:253 */       throw new IllegalArgumentException("Object to run is not an instance of Associator!");
/* 139:    */     }
/* 140:257 */     preExecution();
/* 141:258 */     runAssociator((Associator)toRun, options);
/* 142:259 */     postExecution();
/* 143:    */   }
/* 144:    */   
/* 145:    */   public void postExecution()
/* 146:    */     throws Exception
/* 147:    */   {}
/* 148:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.associations.AbstractAssociator
 * JD-Core Version:    0.7.0.1
 */