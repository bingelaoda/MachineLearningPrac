/*   1:    */ package weka.classifiers.misc;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.util.Collections;
/*   6:    */ import java.util.Enumeration;
/*   7:    */ import java.util.Vector;
/*   8:    */ import weka.classifiers.AbstractClassifier;
/*   9:    */ import weka.classifiers.Classifier;
/*  10:    */ import weka.core.Capabilities;
/*  11:    */ import weka.core.Capabilities.Capability;
/*  12:    */ import weka.core.Instance;
/*  13:    */ import weka.core.Instances;
/*  14:    */ import weka.core.Option;
/*  15:    */ import weka.core.RevisionUtils;
/*  16:    */ import weka.core.SerializationHelper;
/*  17:    */ import weka.core.Utils;
/*  18:    */ 
/*  19:    */ public class SerializedClassifier
/*  20:    */   extends AbstractClassifier
/*  21:    */ {
/*  22:    */   private static final long serialVersionUID = 4599593909947628642L;
/*  23: 74 */   protected transient Classifier m_Model = null;
/*  24: 77 */   protected File m_ModelFile = new File(System.getProperty("user.dir"));
/*  25:    */   
/*  26:    */   public String globalInfo()
/*  27:    */   {
/*  28: 86 */     return "A wrapper around a serialized classifier model. This classifier loads a serialized models and uses it to make predictions.\n\nWarning: since the serialized model doesn't get changed, cross-validation cannot bet used with this classifier.";
/*  29:    */   }
/*  30:    */   
/*  31:    */   public Enumeration<Option> listOptions()
/*  32:    */   {
/*  33:100 */     Vector<Option> result = new Vector();
/*  34:    */     
/*  35:102 */     result.addElement(new Option("\tThe file containing the serialized model.\n\t(required)", "model", 1, "-model <filename>"));
/*  36:    */     
/*  37:    */ 
/*  38:    */ 
/*  39:106 */     result.addAll(Collections.list(super.listOptions()));
/*  40:    */     
/*  41:108 */     return result.elements();
/*  42:    */   }
/*  43:    */   
/*  44:    */   public String[] getOptions()
/*  45:    */   {
/*  46:119 */     Vector<String> result = new Vector();
/*  47:    */     
/*  48:121 */     result.add("-model");
/*  49:122 */     result.add("" + getModelFile());
/*  50:    */     
/*  51:124 */     Collections.addAll(result, super.getOptions());
/*  52:    */     
/*  53:126 */     return (String[])result.toArray(new String[result.size()]);
/*  54:    */   }
/*  55:    */   
/*  56:    */   public void setOptions(String[] options)
/*  57:    */     throws Exception
/*  58:    */   {
/*  59:157 */     super.setOptions(options);
/*  60:    */     
/*  61:159 */     String tmpStr = Utils.getOption("model", options);
/*  62:160 */     if (tmpStr.length() != 0) {
/*  63:161 */       setModelFile(new File(tmpStr));
/*  64:    */     } else {
/*  65:163 */       setModelFile(new File(System.getProperty("user.dir")));
/*  66:    */     }
/*  67:    */   }
/*  68:    */   
/*  69:    */   public String modelFileTipText()
/*  70:    */   {
/*  71:174 */     return "The serialized classifier model to use for predictions.";
/*  72:    */   }
/*  73:    */   
/*  74:    */   public File getModelFile()
/*  75:    */   {
/*  76:183 */     return this.m_ModelFile;
/*  77:    */   }
/*  78:    */   
/*  79:    */   public void setModelFile(File value)
/*  80:    */   {
/*  81:192 */     this.m_ModelFile = value;
/*  82:194 */     if ((value.exists()) && (value.isFile())) {
/*  83:    */       try
/*  84:    */       {
/*  85:196 */         initModel();
/*  86:    */       }
/*  87:    */       catch (Exception e)
/*  88:    */       {
/*  89:198 */         throw new IllegalArgumentException("Cannot load model from file '" + value + "': " + e);
/*  90:    */       }
/*  91:    */     }
/*  92:    */   }
/*  93:    */   
/*  94:    */   public void setModel(Classifier value)
/*  95:    */   {
/*  96:212 */     this.m_Model = value;
/*  97:    */   }
/*  98:    */   
/*  99:    */   public Classifier getCurrentModel()
/* 100:    */   {
/* 101:223 */     return this.m_Model;
/* 102:    */   }
/* 103:    */   
/* 104:    */   protected void initModel()
/* 105:    */     throws Exception
/* 106:    */   {
/* 107:233 */     if (this.m_Model == null) {
/* 108:234 */       this.m_Model = ((Classifier)SerializationHelper.read(this.m_ModelFile.getAbsolutePath()));
/* 109:    */     }
/* 110:    */   }
/* 111:    */   
/* 112:    */   public Capabilities getCapabilities()
/* 113:    */   {
/* 114:249 */     if ((this.m_ModelFile != null) && (this.m_ModelFile.exists()) && (this.m_ModelFile.isFile())) {
/* 115:    */       try
/* 116:    */       {
/* 117:251 */         initModel();
/* 118:    */       }
/* 119:    */       catch (Exception e)
/* 120:    */       {
/* 121:253 */         System.err.println(e);
/* 122:    */       }
/* 123:    */     }
/* 124:    */     Capabilities result;
/* 125:    */     Capabilities result;
/* 126:257 */     if (this.m_Model != null)
/* 127:    */     {
/* 128:258 */       result = this.m_Model.getCapabilities();
/* 129:    */     }
/* 130:    */     else
/* 131:    */     {
/* 132:260 */       result = new Capabilities(this);
/* 133:261 */       result.disableAll();
/* 134:    */     }
/* 135:265 */     for (Capabilities.Capability cap : Capabilities.Capability.values()) {
/* 136:266 */       result.enableDependency(cap);
/* 137:    */     }
/* 138:269 */     result.setOwner(this);
/* 139:    */     
/* 140:271 */     return result;
/* 141:    */   }
/* 142:    */   
/* 143:    */   public double[] distributionForInstance(Instance instance)
/* 144:    */     throws Exception
/* 145:    */   {
/* 146:286 */     initModel();
/* 147:    */     
/* 148:288 */     double[] result = this.m_Model.distributionForInstance(instance);
/* 149:    */     
/* 150:290 */     return result;
/* 151:    */   }
/* 152:    */   
/* 153:    */   public void buildClassifier(Instances data)
/* 154:    */     throws Exception
/* 155:    */   {
/* 156:302 */     initModel();
/* 157:    */     
/* 158:    */ 
/* 159:305 */     getCapabilities().testWithFail(data);
/* 160:    */   }
/* 161:    */   
/* 162:    */   public String toString()
/* 163:    */   {
/* 164:    */     StringBuffer result;
/* 165:    */     StringBuffer result;
/* 166:317 */     if (this.m_Model == null)
/* 167:    */     {
/* 168:318 */       result = new StringBuffer("No model loaded yet.");
/* 169:    */     }
/* 170:    */     else
/* 171:    */     {
/* 172:320 */       result = new StringBuffer();
/* 173:321 */       result.append("SerializedClassifier\n");
/* 174:322 */       result.append("====================\n\n");
/* 175:323 */       result.append("File: " + getModelFile() + "\n\n");
/* 176:324 */       result.append(this.m_Model.toString());
/* 177:    */     }
/* 178:327 */     return result.toString();
/* 179:    */   }
/* 180:    */   
/* 181:    */   public String getRevision()
/* 182:    */   {
/* 183:337 */     return RevisionUtils.extract("$Revision: 10153 $");
/* 184:    */   }
/* 185:    */   
/* 186:    */   public static void main(String[] args)
/* 187:    */   {
/* 188:346 */     runClassifier(new SerializedClassifier(), args);
/* 189:    */   }
/* 190:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.misc.SerializedClassifier
 * JD-Core Version:    0.7.0.1
 */