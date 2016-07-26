/*   1:    */ package weka.classifiers.functions.supportVector;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.core.Capabilities;
/*   7:    */ import weka.core.Capabilities.Capability;
/*   8:    */ import weka.core.Instance;
/*   9:    */ import weka.core.Instances;
/*  10:    */ import weka.core.Option;
/*  11:    */ import weka.core.RevisionUtils;
/*  12:    */ import weka.core.Utils;
/*  13:    */ 
/*  14:    */ public class PolyKernel
/*  15:    */   extends CachedKernel
/*  16:    */ {
/*  17:    */   static final long serialVersionUID = -321831645846363201L;
/*  18: 89 */   protected boolean m_lowerOrder = false;
/*  19: 92 */   protected double m_exponent = 1.0D;
/*  20:    */   
/*  21:    */   public PolyKernel() {}
/*  22:    */   
/*  23:    */   public PolyKernel(Instances data, int cacheSize, double exponent, boolean lowerOrder)
/*  24:    */     throws Exception
/*  25:    */   {
/*  26:115 */     setCacheSize(cacheSize);
/*  27:116 */     setExponent(exponent);
/*  28:117 */     setUseLowerOrder(lowerOrder);
/*  29:    */     
/*  30:119 */     buildKernel(data);
/*  31:    */   }
/*  32:    */   
/*  33:    */   public String globalInfo()
/*  34:    */   {
/*  35:130 */     return "The polynomial kernel : K(x, y) = <x, y>^p or K(x, y) = (<x, y>+1)^p";
/*  36:    */   }
/*  37:    */   
/*  38:    */   public Enumeration<Option> listOptions()
/*  39:    */   {
/*  40:140 */     Vector<Option> result = new Vector();
/*  41:    */     
/*  42:142 */     result.addElement(new Option("\tThe Exponent to use.\n\t(default: 1.0)", "E", 1, "-E <num>"));
/*  43:    */     
/*  44:    */ 
/*  45:145 */     result.addElement(new Option("\tUse lower-order terms.\n\t(default: no)", "L", 0, "-L"));
/*  46:    */     
/*  47:    */ 
/*  48:148 */     result.addAll(Collections.list(super.listOptions()));
/*  49:    */     
/*  50:150 */     return result.elements();
/*  51:    */   }
/*  52:    */   
/*  53:    */   public void setOptions(String[] options)
/*  54:    */     throws Exception
/*  55:    */   {
/*  56:200 */     String tmpStr = Utils.getOption('E', options);
/*  57:201 */     if (tmpStr.length() != 0) {
/*  58:202 */       setExponent(Double.parseDouble(tmpStr));
/*  59:    */     } else {
/*  60:204 */       setExponent(1.0D);
/*  61:    */     }
/*  62:207 */     setUseLowerOrder(Utils.getFlag('L', options));
/*  63:    */     
/*  64:209 */     super.setOptions(options);
/*  65:    */   }
/*  66:    */   
/*  67:    */   public String[] getOptions()
/*  68:    */   {
/*  69:220 */     Vector<String> result = new Vector();
/*  70:    */     
/*  71:222 */     result.add("-E");
/*  72:223 */     result.add("" + getExponent());
/*  73:225 */     if (getUseLowerOrder()) {
/*  74:226 */       result.add("-L");
/*  75:    */     }
/*  76:229 */     Collections.addAll(result, super.getOptions());
/*  77:    */     
/*  78:231 */     return (String[])result.toArray(new String[result.size()]);
/*  79:    */   }
/*  80:    */   
/*  81:    */   protected double evaluate(int id1, int id2, Instance inst1)
/*  82:    */     throws Exception
/*  83:    */   {
/*  84:    */     double result;
/*  85:    */     double result;
/*  86:246 */     if (id1 == id2) {
/*  87:247 */       result = dotProd(inst1, inst1);
/*  88:    */     } else {
/*  89:249 */       result = dotProd(inst1, this.m_data.instance(id2));
/*  90:    */     }
/*  91:252 */     if (this.m_lowerOrder) {
/*  92:253 */       result += 1.0D;
/*  93:    */     }
/*  94:255 */     if (this.m_exponent != 1.0D) {
/*  95:256 */       result = Math.pow(result, this.m_exponent);
/*  96:    */     }
/*  97:258 */     return result;
/*  98:    */   }
/*  99:    */   
/* 100:    */   public Capabilities getCapabilities()
/* 101:    */   {
/* 102:269 */     Capabilities result = super.getCapabilities();
/* 103:270 */     result.disableAll();
/* 104:    */     
/* 105:272 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/* 106:273 */     result.enableAllClasses();
/* 107:274 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 108:275 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 109:    */     
/* 110:277 */     return result;
/* 111:    */   }
/* 112:    */   
/* 113:    */   public void setExponent(double value)
/* 114:    */   {
/* 115:286 */     this.m_exponent = value;
/* 116:    */   }
/* 117:    */   
/* 118:    */   public double getExponent()
/* 119:    */   {
/* 120:295 */     return this.m_exponent;
/* 121:    */   }
/* 122:    */   
/* 123:    */   public String exponentTipText()
/* 124:    */   {
/* 125:305 */     return "The exponent value.";
/* 126:    */   }
/* 127:    */   
/* 128:    */   public void setUseLowerOrder(boolean value)
/* 129:    */   {
/* 130:314 */     this.m_lowerOrder = value;
/* 131:    */   }
/* 132:    */   
/* 133:    */   public boolean getUseLowerOrder()
/* 134:    */   {
/* 135:323 */     return this.m_lowerOrder;
/* 136:    */   }
/* 137:    */   
/* 138:    */   public String useLowerOrderTipText()
/* 139:    */   {
/* 140:333 */     return "Whether to use lower-order terms.";
/* 141:    */   }
/* 142:    */   
/* 143:    */   public String toString()
/* 144:    */   {
/* 145:    */     String result;
/* 146:    */     String result;
/* 147:345 */     if (getExponent() == 1.0D)
/* 148:    */     {
/* 149:    */       String result;
/* 150:346 */       if (getUseLowerOrder()) {
/* 151:347 */         result = "Linear Kernel with lower order: K(x,y) = <x,y> + 1";
/* 152:    */       } else {
/* 153:349 */         result = "Linear Kernel: K(x,y) = <x,y>";
/* 154:    */       }
/* 155:    */     }
/* 156:    */     else
/* 157:    */     {
/* 158:    */       String result;
/* 159:352 */       if (getUseLowerOrder()) {
/* 160:353 */         result = "Poly Kernel with lower order: K(x,y) = (<x,y> + 1)^" + getExponent();
/* 161:    */       } else {
/* 162:356 */         result = "Poly Kernel: K(x,y) = <x,y>^" + getExponent();
/* 163:    */       }
/* 164:    */     }
/* 165:360 */     return result;
/* 166:    */   }
/* 167:    */   
/* 168:    */   public String getRevision()
/* 169:    */   {
/* 170:370 */     return RevisionUtils.extract("$Revision: 12533 $");
/* 171:    */   }
/* 172:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.supportVector.PolyKernel
 * JD-Core Version:    0.7.0.1
 */