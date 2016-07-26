/*   1:    */ package weka.datagenerators;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.core.Option;
/*   7:    */ import weka.core.Range;
/*   8:    */ import weka.core.Utils;
/*   9:    */ 
/*  10:    */ public abstract class ClusterGenerator
/*  11:    */   extends DataGenerator
/*  12:    */ {
/*  13:    */   private static final long serialVersionUID = 6131722618472046365L;
/*  14:    */   protected int m_NumAttributes;
/*  15: 63 */   protected boolean m_ClassFlag = false;
/*  16:    */   protected Range m_booleanCols;
/*  17:    */   protected Range m_nominalCols;
/*  18:    */   
/*  19:    */   public ClusterGenerator()
/*  20:    */   {
/*  21: 77 */     setNumAttributes(defaultNumAttributes());
/*  22:    */   }
/*  23:    */   
/*  24:    */   public Enumeration<Option> listOptions()
/*  25:    */   {
/*  26: 87 */     Vector<Option> result = enumToVector(super.listOptions());
/*  27:    */     
/*  28: 89 */     result.addElement(new Option("\tThe number of attributes (default " + defaultNumAttributes() + ").", "a", 1, "-a <num>"));
/*  29:    */     
/*  30:    */ 
/*  31: 92 */     result.addElement(new Option("\tClass Flag, if set, the cluster is listed in extra attribute.", "c", 0, "-c"));
/*  32:    */     
/*  33:    */ 
/*  34:    */ 
/*  35: 96 */     result.addElement(new Option("\tThe indices for boolean attributes.", "b", 1, "-b <range>"));
/*  36:    */     
/*  37:    */ 
/*  38: 99 */     result.addElement(new Option("\tThe indices for nominal attributes.", "m", 1, "-m <range>"));
/*  39:    */     
/*  40:    */ 
/*  41:102 */     return result.elements();
/*  42:    */   }
/*  43:    */   
/*  44:    */   public void setOptions(String[] options)
/*  45:    */     throws Exception
/*  46:    */   {
/*  47:115 */     super.setOptions(options);
/*  48:    */     
/*  49:117 */     String tmpStr = Utils.getOption('a', options);
/*  50:118 */     if (tmpStr.length() != 0) {
/*  51:119 */       setNumAttributes(Integer.parseInt(tmpStr));
/*  52:    */     } else {
/*  53:121 */       setNumAttributes(defaultNumAttributes());
/*  54:    */     }
/*  55:124 */     setClassFlag(Utils.getFlag('c', options));
/*  56:    */     
/*  57:126 */     tmpStr = Utils.getOption('b', options);
/*  58:127 */     setBooleanIndices(tmpStr);
/*  59:128 */     this.m_booleanCols.setUpper(getNumAttributes() - 1);
/*  60:    */     
/*  61:130 */     tmpStr = Utils.getOption('m', options);
/*  62:131 */     setNominalIndices(tmpStr);
/*  63:132 */     this.m_nominalCols.setUpper(getNumAttributes() - 1);
/*  64:    */     
/*  65:    */ 
/*  66:135 */     tmpStr = checkIndices();
/*  67:136 */     if (tmpStr.length() > 0) {
/*  68:137 */       throw new IllegalArgumentException(tmpStr);
/*  69:    */     }
/*  70:    */   }
/*  71:    */   
/*  72:    */   public String[] getOptions()
/*  73:    */   {
/*  74:149 */     Vector<String> result = new Vector();
/*  75:    */     
/*  76:151 */     Collections.addAll(result, super.getOptions());
/*  77:    */     
/*  78:153 */     result.add("-a");
/*  79:154 */     result.add("" + getNumAttributes());
/*  80:156 */     if (getClassFlag()) {
/*  81:157 */       result.add("-c");
/*  82:    */     }
/*  83:160 */     if (!getBooleanCols().toString().equalsIgnoreCase("empty"))
/*  84:    */     {
/*  85:161 */       result.add("-b");
/*  86:162 */       result.add("" + getBooleanCols().getRanges());
/*  87:    */     }
/*  88:165 */     if (!getNominalCols().toString().equalsIgnoreCase("empty"))
/*  89:    */     {
/*  90:166 */       result.add("-m");
/*  91:167 */       result.add("" + getNominalCols().getRanges());
/*  92:    */     }
/*  93:170 */     return (String[])result.toArray(new String[result.size()]);
/*  94:    */   }
/*  95:    */   
/*  96:    */   protected int defaultNumAttributes()
/*  97:    */   {
/*  98:179 */     return 10;
/*  99:    */   }
/* 100:    */   
/* 101:    */   public void setNumAttributes(int numAttributes)
/* 102:    */   {
/* 103:188 */     this.m_NumAttributes = numAttributes;
/* 104:189 */     getBooleanCols().setUpper(getNumAttributes());
/* 105:190 */     getNominalCols().setUpper(getNumAttributes());
/* 106:    */   }
/* 107:    */   
/* 108:    */   public int getNumAttributes()
/* 109:    */   {
/* 110:199 */     return this.m_NumAttributes;
/* 111:    */   }
/* 112:    */   
/* 113:    */   public String numAttributesTipText()
/* 114:    */   {
/* 115:209 */     return "The number of attributes the generated data will contain.";
/* 116:    */   }
/* 117:    */   
/* 118:    */   public void setClassFlag(boolean classFlag)
/* 119:    */   {
/* 120:219 */     this.m_ClassFlag = classFlag;
/* 121:    */   }
/* 122:    */   
/* 123:    */   public boolean getClassFlag()
/* 124:    */   {
/* 125:228 */     return this.m_ClassFlag;
/* 126:    */   }
/* 127:    */   
/* 128:    */   public String classFlagTipText()
/* 129:    */   {
/* 130:238 */     return "If set to TRUE, lists the cluster as an extra attribute.";
/* 131:    */   }
/* 132:    */   
/* 133:    */   public void setBooleanIndices(String rangeList)
/* 134:    */   {
/* 135:251 */     this.m_booleanCols.setRanges(rangeList);
/* 136:    */   }
/* 137:    */   
/* 138:    */   public void setBooleanCols(Range value)
/* 139:    */   {
/* 140:260 */     this.m_booleanCols.setRanges(value.getRanges());
/* 141:    */   }
/* 142:    */   
/* 143:    */   public Range getBooleanCols()
/* 144:    */   {
/* 145:269 */     if (this.m_booleanCols == null) {
/* 146:270 */       this.m_booleanCols = new Range();
/* 147:    */     }
/* 148:273 */     return this.m_booleanCols;
/* 149:    */   }
/* 150:    */   
/* 151:    */   public String booleanColsTipText()
/* 152:    */   {
/* 153:283 */     return "The range of attributes that are generated as boolean ones.";
/* 154:    */   }
/* 155:    */   
/* 156:    */   public void setNominalIndices(String rangeList)
/* 157:    */   {
/* 158:296 */     this.m_nominalCols.setRanges(rangeList);
/* 159:    */   }
/* 160:    */   
/* 161:    */   public void setNominalCols(Range value)
/* 162:    */   {
/* 163:305 */     this.m_nominalCols.setRanges(value.getRanges());
/* 164:    */   }
/* 165:    */   
/* 166:    */   public Range getNominalCols()
/* 167:    */   {
/* 168:314 */     if (this.m_nominalCols == null) {
/* 169:315 */       this.m_nominalCols = new Range();
/* 170:    */     }
/* 171:318 */     return this.m_nominalCols;
/* 172:    */   }
/* 173:    */   
/* 174:    */   public String nominalColsTipText()
/* 175:    */   {
/* 176:328 */     return "The range of attributes to generate as nominal ones.";
/* 177:    */   }
/* 178:    */   
/* 179:    */   protected String checkIndices()
/* 180:    */   {
/* 181:337 */     for (int i = 0; i < getNumAttributes(); i++) {
/* 182:338 */       if ((this.m_booleanCols.isInRange(i)) && (this.m_nominalCols.isInRange(i))) {
/* 183:339 */         return "Error in attribute type: Attribute " + i + " is set boolean and nominal.";
/* 184:    */       }
/* 185:    */     }
/* 186:343 */     return "";
/* 187:    */   }
/* 188:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.datagenerators.ClusterGenerator
 * JD-Core Version:    0.7.0.1
 */