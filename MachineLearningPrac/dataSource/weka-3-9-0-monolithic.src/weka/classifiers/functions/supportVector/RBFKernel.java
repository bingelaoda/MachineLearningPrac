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
/*  14:    */ public class RBFKernel
/*  15:    */   extends CachedKernel
/*  16:    */ {
/*  17:    */   static final long serialVersionUID = 5247117544316387852L;
/*  18:    */   protected double[] m_kernelPrecalc;
/*  19: 88 */   protected double m_gamma = 0.01D;
/*  20:    */   
/*  21:    */   public RBFKernel() {}
/*  22:    */   
/*  23:    */   public RBFKernel(Instances data, int cacheSize, double gamma)
/*  24:    */     throws Exception
/*  25:    */   {
/*  26:109 */     setCacheSize(cacheSize);
/*  27:110 */     setGamma(gamma);
/*  28:    */     
/*  29:112 */     buildKernel(data);
/*  30:    */   }
/*  31:    */   
/*  32:    */   public String globalInfo()
/*  33:    */   {
/*  34:123 */     return "The RBF kernel. K(x, y) = e^-(gamma * <x-y, x-y>)";
/*  35:    */   }
/*  36:    */   
/*  37:    */   public Enumeration<Option> listOptions()
/*  38:    */   {
/*  39:133 */     Vector<Option> result = new Vector();
/*  40:    */     
/*  41:135 */     result.addElement(new Option("\tThe Gamma parameter.\n\t(default: 0.01)", "G", 1, "-G <num>"));
/*  42:    */     
/*  43:    */ 
/*  44:138 */     result.addAll(Collections.list(super.listOptions()));
/*  45:    */     
/*  46:140 */     return result.elements();
/*  47:    */   }
/*  48:    */   
/*  49:    */   public void setOptions(String[] options)
/*  50:    */     throws Exception
/*  51:    */   {
/*  52:184 */     String tmpStr = Utils.getOption('G', options);
/*  53:185 */     if (tmpStr.length() != 0) {
/*  54:186 */       setGamma(Double.parseDouble(tmpStr));
/*  55:    */     } else {
/*  56:188 */       setGamma(0.01D);
/*  57:    */     }
/*  58:191 */     super.setOptions(options);
/*  59:    */   }
/*  60:    */   
/*  61:    */   public String[] getOptions()
/*  62:    */   {
/*  63:202 */     Vector<String> result = new Vector();
/*  64:    */     
/*  65:204 */     result.add("-G");
/*  66:205 */     result.add("" + getGamma());
/*  67:    */     
/*  68:207 */     Collections.addAll(result, super.getOptions());
/*  69:    */     
/*  70:209 */     return (String[])result.toArray(new String[result.size()]);
/*  71:    */   }
/*  72:    */   
/*  73:    */   protected double evaluate(int id1, int id2, Instance inst1)
/*  74:    */     throws Exception
/*  75:    */   {
/*  76:223 */     if (id1 == id2) {
/*  77:224 */       return 1.0D;
/*  78:    */     }
/*  79:    */     double precalc1;
/*  80:    */     double precalc1;
/*  81:227 */     if (id1 == -1) {
/*  82:228 */       precalc1 = dotProd(inst1, inst1);
/*  83:    */     } else {
/*  84:230 */       precalc1 = this.m_kernelPrecalc[id1];
/*  85:    */     }
/*  86:232 */     Instance inst2 = this.m_data.instance(id2);
/*  87:233 */     double result = Math.exp(this.m_gamma * (2.0D * dotProd(inst1, inst2) - precalc1 - this.m_kernelPrecalc[id2]));
/*  88:    */     
/*  89:    */ 
/*  90:236 */     return result;
/*  91:    */   }
/*  92:    */   
/*  93:    */   public void setGamma(double value)
/*  94:    */   {
/*  95:246 */     this.m_gamma = value;
/*  96:    */   }
/*  97:    */   
/*  98:    */   public double getGamma()
/*  99:    */   {
/* 100:255 */     return this.m_gamma;
/* 101:    */   }
/* 102:    */   
/* 103:    */   public String gammaTipText()
/* 104:    */   {
/* 105:265 */     return "The Gamma value.";
/* 106:    */   }
/* 107:    */   
/* 108:    */   protected void initVars(Instances data)
/* 109:    */   {
/* 110:275 */     super.initVars(data);
/* 111:    */     
/* 112:277 */     this.m_kernelPrecalc = new double[data.numInstances()];
/* 113:    */   }
/* 114:    */   
/* 115:    */   public Capabilities getCapabilities()
/* 116:    */   {
/* 117:288 */     Capabilities result = super.getCapabilities();
/* 118:289 */     result.disableAll();
/* 119:    */     
/* 120:291 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/* 121:292 */     result.enableAllClasses();
/* 122:293 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 123:294 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 124:    */     
/* 125:296 */     return result;
/* 126:    */   }
/* 127:    */   
/* 128:    */   public void buildKernel(Instances data)
/* 129:    */     throws Exception
/* 130:    */   {
/* 131:309 */     if (!getChecksTurnedOff()) {
/* 132:310 */       getCapabilities().testWithFail(data);
/* 133:    */     }
/* 134:313 */     initVars(data);
/* 135:315 */     for (int i = 0; i < data.numInstances(); i++) {
/* 136:316 */       this.m_kernelPrecalc[i] = dotProd(data.instance(i), data.instance(i));
/* 137:    */     }
/* 138:    */   }
/* 139:    */   
/* 140:    */   public String toString()
/* 141:    */   {
/* 142:327 */     return "RBF kernel: K(x,y) = e^-(" + getGamma() + "* <x-y,x-y>^2)";
/* 143:    */   }
/* 144:    */   
/* 145:    */   public String getRevision()
/* 146:    */   {
/* 147:337 */     return RevisionUtils.extract("$Revision: 12518 $");
/* 148:    */   }
/* 149:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.supportVector.RBFKernel
 * JD-Core Version:    0.7.0.1
 */