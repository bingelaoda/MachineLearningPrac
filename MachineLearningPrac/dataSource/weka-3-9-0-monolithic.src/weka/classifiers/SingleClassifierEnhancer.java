/*   1:    */ package weka.classifiers;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.classifiers.rules.ZeroR;
/*   7:    */ import weka.core.Capabilities;
/*   8:    */ import weka.core.Capabilities.Capability;
/*   9:    */ import weka.core.CommandlineRunnable;
/*  10:    */ import weka.core.Option;
/*  11:    */ import weka.core.OptionHandler;
/*  12:    */ import weka.core.Utils;
/*  13:    */ 
/*  14:    */ public abstract class SingleClassifierEnhancer
/*  15:    */   extends AbstractClassifier
/*  16:    */ {
/*  17:    */   private static final long serialVersionUID = -3665885256363525164L;
/*  18: 45 */   protected Classifier m_Classifier = new ZeroR();
/*  19:    */   
/*  20:    */   protected String defaultClassifierString()
/*  21:    */   {
/*  22: 52 */     return "weka.classifiers.rules.ZeroR";
/*  23:    */   }
/*  24:    */   
/*  25:    */   protected String[] defaultClassifierOptions()
/*  26:    */   {
/*  27: 60 */     return new String[0];
/*  28:    */   }
/*  29:    */   
/*  30:    */   public Enumeration<Option> listOptions()
/*  31:    */   {
/*  32: 70 */     Vector<Option> newVector = new Vector(3);
/*  33:    */     
/*  34: 72 */     newVector.addElement(new Option("\tFull name of base classifier.\n\t(default: " + defaultClassifierString() + (defaultClassifierOptions().length > 0 ? " with options " + Utils.joinOptions(defaultClassifierOptions()) + ")" : ")"), "W", 1, "-W"));
/*  35:    */     
/*  36:    */ 
/*  37:    */ 
/*  38:    */ 
/*  39:    */ 
/*  40:    */ 
/*  41: 79 */     newVector.addAll(Collections.list(super.listOptions()));
/*  42:    */     
/*  43: 81 */     newVector.addElement(new Option("", "", 0, "\nOptions specific to classifier " + this.m_Classifier.getClass().getName() + ":"));
/*  44:    */     
/*  45:    */ 
/*  46:    */ 
/*  47: 85 */     newVector.addAll(Collections.list(((OptionHandler)this.m_Classifier).listOptions()));
/*  48:    */     
/*  49: 87 */     return newVector.elements();
/*  50:    */   }
/*  51:    */   
/*  52:    */   public void setOptions(String[] options)
/*  53:    */     throws Exception
/*  54:    */   {
/*  55:103 */     super.setOptions(options);
/*  56:    */     
/*  57:105 */     String classifierName = Utils.getOption('W', options);
/*  58:107 */     if (classifierName.length() > 0)
/*  59:    */     {
/*  60:108 */       setClassifier(AbstractClassifier.forName(classifierName, null));
/*  61:109 */       setClassifier(AbstractClassifier.forName(classifierName, Utils.partitionOptions(options)));
/*  62:    */     }
/*  63:    */     else
/*  64:    */     {
/*  65:112 */       setClassifier(AbstractClassifier.forName(defaultClassifierString(), null));
/*  66:113 */       String[] classifierOptions = Utils.partitionOptions(options);
/*  67:114 */       if (classifierOptions.length > 0) {
/*  68:115 */         setClassifier(AbstractClassifier.forName(defaultClassifierString(), classifierOptions));
/*  69:    */       } else {
/*  70:118 */         setClassifier(AbstractClassifier.forName(defaultClassifierString(), defaultClassifierOptions()));
/*  71:    */       }
/*  72:    */     }
/*  73:    */   }
/*  74:    */   
/*  75:    */   public String[] getOptions()
/*  76:    */   {
/*  77:131 */     Vector<String> options = new Vector();
/*  78:    */     
/*  79:133 */     options.add("-W");
/*  80:134 */     options.add(getClassifier().getClass().getName());
/*  81:    */     
/*  82:136 */     Collections.addAll(options, super.getOptions());
/*  83:    */     
/*  84:138 */     String[] classifierOptions = ((OptionHandler)this.m_Classifier).getOptions();
/*  85:139 */     if (classifierOptions.length > 0)
/*  86:    */     {
/*  87:140 */       options.add("--");
/*  88:141 */       Collections.addAll(options, classifierOptions);
/*  89:    */     }
/*  90:144 */     return (String[])options.toArray(new String[0]);
/*  91:    */   }
/*  92:    */   
/*  93:    */   public String classifierTipText()
/*  94:    */   {
/*  95:153 */     return "The base classifier to be used.";
/*  96:    */   }
/*  97:    */   
/*  98:    */   public Capabilities getCapabilities()
/*  99:    */   {
/* 100:    */     Capabilities result;
/* 101:    */     Capabilities result;
/* 102:164 */     if (getClassifier() != null)
/* 103:    */     {
/* 104:165 */       result = getClassifier().getCapabilities();
/* 105:    */     }
/* 106:    */     else
/* 107:    */     {
/* 108:167 */       result = new Capabilities(this);
/* 109:168 */       result.disableAll();
/* 110:    */     }
/* 111:172 */     for (Capabilities.Capability cap : Capabilities.Capability.values()) {
/* 112:173 */       result.enableDependency(cap);
/* 113:    */     }
/* 114:175 */     result.setOwner(this);
/* 115:    */     
/* 116:177 */     return result;
/* 117:    */   }
/* 118:    */   
/* 119:    */   public void setClassifier(Classifier newClassifier)
/* 120:    */   {
/* 121:187 */     this.m_Classifier = newClassifier;
/* 122:    */   }
/* 123:    */   
/* 124:    */   public Classifier getClassifier()
/* 125:    */   {
/* 126:197 */     return this.m_Classifier;
/* 127:    */   }
/* 128:    */   
/* 129:    */   protected String getClassifierSpec()
/* 130:    */   {
/* 131:208 */     Classifier c = getClassifier();
/* 132:209 */     return c.getClass().getName() + " " + Utils.joinOptions(((OptionHandler)c).getOptions());
/* 133:    */   }
/* 134:    */   
/* 135:    */   public void preExecution()
/* 136:    */     throws Exception
/* 137:    */   {
/* 138:215 */     if ((getClassifier() instanceof CommandlineRunnable)) {
/* 139:216 */       ((CommandlineRunnable)getClassifier()).preExecution();
/* 140:    */     }
/* 141:    */   }
/* 142:    */   
/* 143:    */   public void postExecution()
/* 144:    */     throws Exception
/* 145:    */   {
/* 146:222 */     if ((getClassifier() instanceof CommandlineRunnable)) {
/* 147:223 */       ((CommandlineRunnable)getClassifier()).postExecution();
/* 148:    */     }
/* 149:    */   }
/* 150:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.SingleClassifierEnhancer
 * JD-Core Version:    0.7.0.1
 */