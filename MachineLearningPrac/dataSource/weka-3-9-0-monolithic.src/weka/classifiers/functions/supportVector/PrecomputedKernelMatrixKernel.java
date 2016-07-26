/*   1:    */ package weka.classifiers.functions.supportVector;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.FileReader;
/*   5:    */ import java.io.PrintStream;
/*   6:    */ import java.util.Collections;
/*   7:    */ import java.util.Enumeration;
/*   8:    */ import java.util.Vector;
/*   9:    */ import weka.core.Capabilities;
/*  10:    */ import weka.core.Capabilities.Capability;
/*  11:    */ import weka.core.Copyable;
/*  12:    */ import weka.core.Instance;
/*  13:    */ import weka.core.Instances;
/*  14:    */ import weka.core.Option;
/*  15:    */ import weka.core.RevisionUtils;
/*  16:    */ import weka.core.Utils;
/*  17:    */ import weka.core.matrix.Matrix;
/*  18:    */ 
/*  19:    */ public class PrecomputedKernelMatrixKernel
/*  20:    */   extends Kernel
/*  21:    */   implements Copyable
/*  22:    */ {
/*  23:    */   static final long serialVersionUID = -321831645846363333L;
/*  24: 81 */   protected File m_KernelMatrixFile = new File("kernelMatrix.matrix");
/*  25:    */   protected Matrix m_KernelMatrix;
/*  26:    */   protected int m_Counter;
/*  27:    */   
/*  28:    */   public Object copy()
/*  29:    */   {
/*  30: 96 */     PrecomputedKernelMatrixKernel newK = new PrecomputedKernelMatrixKernel();
/*  31:    */     
/*  32: 98 */     newK.setKernelMatrix(this.m_KernelMatrix);
/*  33: 99 */     newK.setKernelMatrixFile(this.m_KernelMatrixFile);
/*  34:100 */     newK.m_Counter = this.m_Counter;
/*  35:    */     
/*  36:102 */     return newK;
/*  37:    */   }
/*  38:    */   
/*  39:    */   public String globalInfo()
/*  40:    */   {
/*  41:113 */     return "This kernel is based on a static kernel matrix that is read from a file. Instances must have a single nominal attribute (excluding the class). This attribute must be the first attribute in the file and its values are used to reference rows/columns in the kernel matrix. The second attribute must be the class attribute.";
/*  42:    */   }
/*  43:    */   
/*  44:    */   public Enumeration<Option> listOptions()
/*  45:    */   {
/*  46:127 */     Vector<Option> result = new Vector();
/*  47:    */     
/*  48:129 */     result.addElement(new Option("\tThe file name of the file that holds the kernel matrix.\n\t(default: kernelMatrix.matrix)", "M", 1, "-M <file name>"));
/*  49:    */     
/*  50:    */ 
/*  51:    */ 
/*  52:133 */     result.addAll(Collections.list(super.listOptions()));
/*  53:    */     
/*  54:135 */     return result.elements();
/*  55:    */   }
/*  56:    */   
/*  57:    */   public void setOptions(String[] options)
/*  58:    */     throws Exception
/*  59:    */   {
/*  60:172 */     String tmpStr = Utils.getOption('M', options);
/*  61:173 */     if (tmpStr.length() != 0) {
/*  62:174 */       setKernelMatrixFile(new File(tmpStr));
/*  63:    */     } else {
/*  64:176 */       setKernelMatrixFile(new File("kernelMatrix.matrix"));
/*  65:    */     }
/*  66:179 */     super.setOptions(options);
/*  67:    */   }
/*  68:    */   
/*  69:    */   public String[] getOptions()
/*  70:    */   {
/*  71:190 */     Vector<String> result = new Vector();
/*  72:    */     
/*  73:192 */     result.add("-M");
/*  74:193 */     result.add("" + getKernelMatrixFile());
/*  75:    */     
/*  76:195 */     Collections.addAll(result, super.getOptions());
/*  77:    */     
/*  78:197 */     return (String[])result.toArray(new String[result.size()]);
/*  79:    */   }
/*  80:    */   
/*  81:    */   public double eval(int id1, int id2, Instance inst1)
/*  82:    */     throws Exception
/*  83:    */   {
/*  84:211 */     if (this.m_KernelMatrix == null) {
/*  85:212 */       throw new IllegalArgumentException("Kernel matrix has not been loaded successfully.");
/*  86:    */     }
/*  87:215 */     int index1 = -1;
/*  88:216 */     if (id1 > -1) {
/*  89:217 */       index1 = (int)this.m_data.instance(id1).value(0);
/*  90:    */     } else {
/*  91:219 */       index1 = (int)inst1.value(0);
/*  92:    */     }
/*  93:221 */     int index2 = (int)this.m_data.instance(id2).value(0);
/*  94:222 */     return this.m_KernelMatrix.get(index1, index2);
/*  95:    */   }
/*  96:    */   
/*  97:    */   protected void initVars(Instances data)
/*  98:    */   {
/*  99:232 */     super.initVars(data);
/* 100:    */     try
/* 101:    */     {
/* 102:235 */       if (this.m_KernelMatrix == null) {
/* 103:236 */         this.m_KernelMatrix = new Matrix(new FileReader(this.m_KernelMatrixFile));
/* 104:    */       }
/* 105:    */     }
/* 106:    */     catch (Exception e)
/* 107:    */     {
/* 108:240 */       System.err.println("Problem reading matrix from " + this.m_KernelMatrixFile);
/* 109:    */     }
/* 110:242 */     this.m_Counter += 1;
/* 111:    */   }
/* 112:    */   
/* 113:    */   public Capabilities getCapabilities()
/* 114:    */   {
/* 115:254 */     Capabilities result = super.getCapabilities();
/* 116:255 */     result.disableAll();
/* 117:    */     
/* 118:257 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/* 119:258 */     result.enableAllClasses();
/* 120:259 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 121:260 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 122:    */     
/* 123:262 */     return result;
/* 124:    */   }
/* 125:    */   
/* 126:    */   public void setKernelMatrixFile(File f)
/* 127:    */   {
/* 128:271 */     this.m_KernelMatrixFile = f;
/* 129:    */   }
/* 130:    */   
/* 131:    */   public File getKernelMatrixFile()
/* 132:    */   {
/* 133:280 */     return this.m_KernelMatrixFile;
/* 134:    */   }
/* 135:    */   
/* 136:    */   public String kernelMatrixFileTipText()
/* 137:    */   {
/* 138:290 */     return "The file holding the kernel matrix.";
/* 139:    */   }
/* 140:    */   
/* 141:    */   protected void setKernelMatrix(Matrix km)
/* 142:    */   {
/* 143:300 */     this.m_KernelMatrix = km;
/* 144:    */   }
/* 145:    */   
/* 146:    */   public String toString()
/* 147:    */   {
/* 148:310 */     return "Using kernel matrix from file with name: " + getKernelMatrixFile();
/* 149:    */   }
/* 150:    */   
/* 151:    */   public void clean() {}
/* 152:    */   
/* 153:    */   public int numEvals()
/* 154:    */   {
/* 155:330 */     return 0;
/* 156:    */   }
/* 157:    */   
/* 158:    */   public int numCacheHits()
/* 159:    */   {
/* 160:341 */     return 0;
/* 161:    */   }
/* 162:    */   
/* 163:    */   public String getRevision()
/* 164:    */   {
/* 165:351 */     return RevisionUtils.extract("$Revision: 12518 $");
/* 166:    */   }
/* 167:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.supportVector.PrecomputedKernelMatrixKernel
 * JD-Core Version:    0.7.0.1
 */