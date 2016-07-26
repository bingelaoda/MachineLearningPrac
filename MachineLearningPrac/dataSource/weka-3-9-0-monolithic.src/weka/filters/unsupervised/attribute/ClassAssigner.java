/*   1:    */ package weka.filters.unsupervised.attribute;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.core.Capabilities;
/*   8:    */ import weka.core.Capabilities.Capability;
/*   9:    */ import weka.core.Instance;
/*  10:    */ import weka.core.Instances;
/*  11:    */ import weka.core.Option;
/*  12:    */ import weka.core.RevisionUtils;
/*  13:    */ import weka.core.Utils;
/*  14:    */ import weka.filters.SimpleStreamFilter;
/*  15:    */ 
/*  16:    */ public class ClassAssigner
/*  17:    */   extends SimpleStreamFilter
/*  18:    */ {
/*  19:    */   private static final long serialVersionUID = 1775780193887394115L;
/*  20:    */   public static final int FIRST = 0;
/*  21:    */   public static final int LAST = -2;
/*  22:    */   public static final int UNSET = -1;
/*  23: 76 */   protected int m_ClassIndex = -2;
/*  24:    */   
/*  25:    */   public String globalInfo()
/*  26:    */   {
/*  27: 86 */     return "Filter that can set and unset the class index.";
/*  28:    */   }
/*  29:    */   
/*  30:    */   public Enumeration<Option> listOptions()
/*  31:    */   {
/*  32: 97 */     Vector<Option> result = new Vector(1);
/*  33:    */     
/*  34: 99 */     result.addElement(new Option("\tThe index of the class attribute. Index starts with 1, 'first'\n\tand 'last' are accepted, '0' unsets the class index.\n\t(default: last)", "C", 1, "-C <num|first|last|0>"));
/*  35:    */     
/*  36:    */ 
/*  37:    */ 
/*  38:    */ 
/*  39:104 */     result.addAll(Collections.list(super.listOptions()));
/*  40:    */     
/*  41:106 */     return result.elements();
/*  42:    */   }
/*  43:    */   
/*  44:    */   public void setOptions(String[] options)
/*  45:    */     throws Exception
/*  46:    */   {
/*  47:136 */     String tmpStr = Utils.getOption("C", options);
/*  48:138 */     if (tmpStr.length() != 0) {
/*  49:139 */       setClassIndex(tmpStr);
/*  50:    */     } else {
/*  51:141 */       setClassIndex("last");
/*  52:    */     }
/*  53:144 */     super.setOptions(options);
/*  54:    */     
/*  55:146 */     Utils.checkForRemainingOptions(options);
/*  56:    */   }
/*  57:    */   
/*  58:    */   public String[] getOptions()
/*  59:    */   {
/*  60:157 */     Vector<String> result = new Vector();
/*  61:    */     
/*  62:159 */     result.add("-C");
/*  63:160 */     result.add(getClassIndex());
/*  64:    */     
/*  65:162 */     Collections.addAll(result, super.getOptions());
/*  66:    */     
/*  67:164 */     return (String[])result.toArray(new String[result.size()]);
/*  68:    */   }
/*  69:    */   
/*  70:    */   public String classIndexTipText()
/*  71:    */   {
/*  72:174 */     return "The index of the class attribute, starts with 1, 'first' and 'last' are accepted as well, '0' unsets the class index.";
/*  73:    */   }
/*  74:    */   
/*  75:    */   public void setClassIndex(String value)
/*  76:    */   {
/*  77:184 */     if (value.equalsIgnoreCase("first")) {
/*  78:185 */       this.m_ClassIndex = 0;
/*  79:186 */     } else if (value.equalsIgnoreCase("last")) {
/*  80:187 */       this.m_ClassIndex = -2;
/*  81:188 */     } else if (value.equalsIgnoreCase("0")) {
/*  82:189 */       this.m_ClassIndex = -1;
/*  83:    */     } else {
/*  84:    */       try
/*  85:    */       {
/*  86:192 */         this.m_ClassIndex = (Integer.parseInt(value) - 1);
/*  87:    */       }
/*  88:    */       catch (Exception e)
/*  89:    */       {
/*  90:194 */         System.err.println("Error parsing '" + value + "'!");
/*  91:    */       }
/*  92:    */     }
/*  93:    */   }
/*  94:    */   
/*  95:    */   public String getClassIndex()
/*  96:    */   {
/*  97:205 */     if (this.m_ClassIndex == 0) {
/*  98:206 */       return "first";
/*  99:    */     }
/* 100:207 */     if (this.m_ClassIndex == -2) {
/* 101:208 */       return "last";
/* 102:    */     }
/* 103:209 */     if (this.m_ClassIndex == -1) {
/* 104:210 */       return "0";
/* 105:    */     }
/* 106:212 */     return "" + (this.m_ClassIndex + 1);
/* 107:    */   }
/* 108:    */   
/* 109:    */   public Capabilities getCapabilities()
/* 110:    */   {
/* 111:224 */     Capabilities result = super.getCapabilities();
/* 112:225 */     result.disableAll();
/* 113:    */     
/* 114:    */ 
/* 115:228 */     result.enableAllAttributes();
/* 116:229 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 117:    */     
/* 118:    */ 
/* 119:232 */     result.enableAllClasses();
/* 120:233 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 121:234 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 122:    */     
/* 123:236 */     return result;
/* 124:    */   }
/* 125:    */   
/* 126:    */   protected Instances determineOutputFormat(Instances inputFormat)
/* 127:    */     throws Exception
/* 128:    */   {
/* 129:250 */     Instances result = new Instances(inputFormat, 0);
/* 130:252 */     if (this.m_ClassIndex == 0) {
/* 131:253 */       result.setClassIndex(0);
/* 132:254 */     } else if (this.m_ClassIndex == -2) {
/* 133:255 */       result.setClassIndex(result.numAttributes() - 1);
/* 134:256 */     } else if (this.m_ClassIndex == -1) {
/* 135:257 */       result.setClassIndex(-1);
/* 136:    */     } else {
/* 137:259 */       result.setClassIndex(this.m_ClassIndex);
/* 138:    */     }
/* 139:262 */     return result;
/* 140:    */   }
/* 141:    */   
/* 142:    */   protected Instance process(Instance instance)
/* 143:    */     throws Exception
/* 144:    */   {
/* 145:275 */     return instance;
/* 146:    */   }
/* 147:    */   
/* 148:    */   public String getRevision()
/* 149:    */   {
/* 150:285 */     return RevisionUtils.extract("$Revision: 10215 $");
/* 151:    */   }
/* 152:    */   
/* 153:    */   public static void main(String[] args)
/* 154:    */   {
/* 155:294 */     runFilter(new ClassAssigner(), args);
/* 156:    */   }
/* 157:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.ClassAssigner
 * JD-Core Version:    0.7.0.1
 */