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
/*  14:    */ public abstract class MultipleClassifiersCombiner
/*  15:    */   extends AbstractClassifier
/*  16:    */ {
/*  17:    */   private static final long serialVersionUID = 2776436621129422119L;
/*  18: 44 */   protected Classifier[] m_Classifiers = { new ZeroR() };
/*  19:    */   
/*  20:    */   public Enumeration<Option> listOptions()
/*  21:    */   {
/*  22: 55 */     Vector<Option> newVector = new Vector(1);
/*  23:    */     
/*  24: 57 */     newVector.addElement(new Option("\tFull class name of classifier to include, followed\n\tby scheme options. May be specified multiple times.\n\t(default: \"weka.classifiers.rules.ZeroR\")", "B", 1, "-B <classifier specification>"));
/*  25:    */     
/*  26:    */ 
/*  27:    */ 
/*  28:    */ 
/*  29:    */ 
/*  30: 63 */     newVector.addAll(Collections.list(super.listOptions()));
/*  31: 65 */     for (Classifier classifier : getClassifiers()) {
/*  32: 66 */       if ((classifier instanceof OptionHandler))
/*  33:    */       {
/*  34: 67 */         newVector.addElement(new Option("", "", 0, "\nOptions specific to classifier " + classifier.getClass().getName() + ":"));
/*  35:    */         
/*  36:    */ 
/*  37:    */ 
/*  38: 71 */         newVector.addAll(Collections.list(((OptionHandler)classifier).listOptions()));
/*  39:    */       }
/*  40:    */     }
/*  41: 75 */     return newVector.elements();
/*  42:    */   }
/*  43:    */   
/*  44:    */   public void setOptions(String[] options)
/*  45:    */     throws Exception
/*  46:    */   {
/*  47: 92 */     Vector<Classifier> classifiers = new Vector();
/*  48:    */     for (;;)
/*  49:    */     {
/*  50: 94 */       String classifierString = Utils.getOption('B', options);
/*  51: 95 */       if (classifierString.length() == 0) {
/*  52:    */         break;
/*  53:    */       }
/*  54: 98 */       String[] classifierSpec = Utils.splitOptions(classifierString);
/*  55: 99 */       if (classifierSpec.length == 0) {
/*  56:100 */         throw new IllegalArgumentException("Invalid classifier specification string");
/*  57:    */       }
/*  58:102 */       String classifierName = classifierSpec[0];
/*  59:103 */       classifierSpec[0] = "";
/*  60:104 */       classifiers.addElement(AbstractClassifier.forName(classifierName, classifierSpec));
/*  61:    */     }
/*  62:107 */     if (classifiers.size() == 0) {
/*  63:108 */       classifiers.addElement(new ZeroR());
/*  64:    */     }
/*  65:110 */     Classifier[] classifiersArray = new Classifier[classifiers.size()];
/*  66:111 */     for (int i = 0; i < classifiersArray.length; i++) {
/*  67:112 */       classifiersArray[i] = ((Classifier)classifiers.elementAt(i));
/*  68:    */     }
/*  69:114 */     setClassifiers(classifiersArray);
/*  70:    */     
/*  71:116 */     super.setOptions(options);
/*  72:    */   }
/*  73:    */   
/*  74:    */   public String[] getOptions()
/*  75:    */   {
/*  76:126 */     Vector<String> options = new Vector();
/*  77:127 */     for (int i = 0; i < this.m_Classifiers.length; i++)
/*  78:    */     {
/*  79:128 */       options.add("-B");
/*  80:129 */       options.add("" + getClassifierSpec(i));
/*  81:    */     }
/*  82:131 */     Collections.addAll(options, super.getOptions());
/*  83:    */     
/*  84:133 */     return (String[])options.toArray(new String[0]);
/*  85:    */   }
/*  86:    */   
/*  87:    */   public String classifiersTipText()
/*  88:    */   {
/*  89:142 */     return "The base classifiers to be used.";
/*  90:    */   }
/*  91:    */   
/*  92:    */   public void setClassifiers(Classifier[] classifiers)
/*  93:    */   {
/*  94:152 */     this.m_Classifiers = classifiers;
/*  95:    */   }
/*  96:    */   
/*  97:    */   public Classifier[] getClassifiers()
/*  98:    */   {
/*  99:162 */     return this.m_Classifiers;
/* 100:    */   }
/* 101:    */   
/* 102:    */   public Classifier getClassifier(int index)
/* 103:    */   {
/* 104:173 */     return this.m_Classifiers[index];
/* 105:    */   }
/* 106:    */   
/* 107:    */   protected String getClassifierSpec(int index)
/* 108:    */   {
/* 109:187 */     if (this.m_Classifiers.length < index) {
/* 110:188 */       return "";
/* 111:    */     }
/* 112:190 */     Classifier c = getClassifier(index);
/* 113:191 */     return c.getClass().getName() + " " + Utils.joinOptions(((OptionHandler)c).getOptions());
/* 114:    */   }
/* 115:    */   
/* 116:    */   public Capabilities getCapabilities()
/* 117:    */   {
/* 118:    */     Capabilities result;
/* 119:205 */     if (getClassifiers().length == 0)
/* 120:    */     {
/* 121:206 */       Capabilities result = new Capabilities(this);
/* 122:207 */       result.disableAll();
/* 123:    */     }
/* 124:    */     else
/* 125:    */     {
/* 126:210 */       result = (Capabilities)getClassifier(0).getCapabilities().clone();
/* 127:211 */       for (int i = 1; i < getClassifiers().length; i++) {
/* 128:212 */         result.and(getClassifier(i).getCapabilities());
/* 129:    */       }
/* 130:    */     }
/* 131:216 */     for (Capabilities.Capability cap : Capabilities.Capability.values()) {
/* 132:217 */       result.enableDependency(cap);
/* 133:    */     }
/* 134:219 */     result.setOwner(this);
/* 135:    */     
/* 136:221 */     return result;
/* 137:    */   }
/* 138:    */   
/* 139:    */   public void preExecution()
/* 140:    */     throws Exception
/* 141:    */   {
/* 142:226 */     for (Classifier classifier : getClassifiers()) {
/* 143:227 */       if ((classifier instanceof CommandlineRunnable)) {
/* 144:228 */         ((CommandlineRunnable)classifier).preExecution();
/* 145:    */       }
/* 146:    */     }
/* 147:    */   }
/* 148:    */   
/* 149:    */   public void postExecution()
/* 150:    */     throws Exception
/* 151:    */   {
/* 152:235 */     for (Classifier classifier : getClassifiers()) {
/* 153:236 */       if ((classifier instanceof CommandlineRunnable)) {
/* 154:237 */         ((CommandlineRunnable)classifier).postExecution();
/* 155:    */       }
/* 156:    */     }
/* 157:    */   }
/* 158:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.MultipleClassifiersCombiner
 * JD-Core Version:    0.7.0.1
 */