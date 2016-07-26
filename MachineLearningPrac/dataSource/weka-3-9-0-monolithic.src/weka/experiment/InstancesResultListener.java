/*   1:    */ package weka.experiment;
/*   2:    */ 
/*   3:    */ import java.io.BufferedOutputStream;
/*   4:    */ import java.io.File;
/*   5:    */ import java.io.FileOutputStream;
/*   6:    */ import java.io.PrintStream;
/*   7:    */ import java.io.PrintWriter;
/*   8:    */ import java.util.ArrayList;
/*   9:    */ import java.util.Hashtable;
/*  10:    */ import weka.core.Attribute;
/*  11:    */ import weka.core.DenseInstance;
/*  12:    */ import weka.core.Instance;
/*  13:    */ import weka.core.Instances;
/*  14:    */ import weka.core.RevisionUtils;
/*  15:    */ import weka.core.Utils;
/*  16:    */ 
/*  17:    */ public class InstancesResultListener
/*  18:    */   extends CSVResultListener
/*  19:    */ {
/*  20:    */   static final long serialVersionUID = -2203808461809311178L;
/*  21:    */   protected transient ArrayList<Instance> m_Instances;
/*  22:    */   protected transient int[] m_AttributeTypes;
/*  23:    */   protected transient Hashtable<String, Double>[] m_NominalIndexes;
/*  24:    */   protected transient ArrayList<String>[] m_NominalStrings;
/*  25:    */   
/*  26:    */   public InstancesResultListener()
/*  27:    */   {
/*  28:    */     File resultsFile;
/*  29:    */     try
/*  30:    */     {
/*  31: 82 */       resultsFile = File.createTempFile("weka_experiment", ".arff");
/*  32: 83 */       resultsFile.deleteOnExit();
/*  33:    */     }
/*  34:    */     catch (Exception e)
/*  35:    */     {
/*  36: 85 */       System.err.println("Cannot create temp file, writing to standard out.");
/*  37: 86 */       resultsFile = new File("-");
/*  38:    */     }
/*  39: 88 */     setOutputFile(resultsFile);
/*  40: 89 */     setOutputFileName("");
/*  41:    */   }
/*  42:    */   
/*  43:    */   public String globalInfo()
/*  44:    */   {
/*  45:100 */     return "Outputs the received results in arff format to a Writer. All results must be received before the instances can be written out.";
/*  46:    */   }
/*  47:    */   
/*  48:    */   public void preProcess(ResultProducer rp)
/*  49:    */     throws Exception
/*  50:    */   {
/*  51:115 */     this.m_RP = rp;
/*  52:116 */     if ((this.m_OutputFile == null) || (this.m_OutputFile.getName().equals("-"))) {
/*  53:117 */       this.m_Out = new PrintWriter(System.out, true);
/*  54:    */     } else {
/*  55:119 */       this.m_Out = new PrintWriter(new BufferedOutputStream(new FileOutputStream(this.m_OutputFile)), true);
/*  56:    */     }
/*  57:123 */     Object[] keyTypes = this.m_RP.getKeyTypes();
/*  58:124 */     Object[] resultTypes = this.m_RP.getResultTypes();
/*  59:    */     
/*  60:126 */     this.m_AttributeTypes = new int[keyTypes.length + resultTypes.length];
/*  61:127 */     this.m_NominalIndexes = new Hashtable[this.m_AttributeTypes.length];
/*  62:128 */     this.m_NominalStrings = new ArrayList[this.m_AttributeTypes.length];
/*  63:129 */     this.m_Instances = new ArrayList();
/*  64:131 */     for (int i = 0; i < this.m_AttributeTypes.length; i++)
/*  65:    */     {
/*  66:132 */       Object attribute = null;
/*  67:133 */       if (i < keyTypes.length) {
/*  68:134 */         attribute = keyTypes[i];
/*  69:    */       } else {
/*  70:136 */         attribute = resultTypes[(i - keyTypes.length)];
/*  71:    */       }
/*  72:138 */       if ((attribute instanceof String))
/*  73:    */       {
/*  74:139 */         this.m_AttributeTypes[i] = 1;
/*  75:140 */         this.m_NominalIndexes[i] = new Hashtable();
/*  76:141 */         this.m_NominalStrings[i] = new ArrayList();
/*  77:    */       }
/*  78:142 */       else if ((attribute instanceof Double))
/*  79:    */       {
/*  80:143 */         this.m_AttributeTypes[i] = 0;
/*  81:    */       }
/*  82:    */       else
/*  83:    */       {
/*  84:145 */         throw new Exception("Unknown attribute type in column " + (i + 1));
/*  85:    */       }
/*  86:    */     }
/*  87:    */   }
/*  88:    */   
/*  89:    */   public void postProcess(ResultProducer rp)
/*  90:    */     throws Exception
/*  91:    */   {
/*  92:160 */     if (this.m_RP != rp) {
/*  93:161 */       throw new Error("Unrecognized ResultProducer sending results!!");
/*  94:    */     }
/*  95:163 */     String[] keyNames = this.m_RP.getKeyNames();
/*  96:164 */     String[] resultNames = this.m_RP.getResultNames();
/*  97:165 */     ArrayList<Attribute> attribInfo = new ArrayList();
/*  98:166 */     for (int i = 0; i < this.m_AttributeTypes.length; i++)
/*  99:    */     {
/* 100:167 */       String attribName = "Unknown";
/* 101:168 */       if (i < keyNames.length) {
/* 102:169 */         attribName = "Key_" + keyNames[i];
/* 103:    */       } else {
/* 104:171 */         attribName = resultNames[(i - keyNames.length)];
/* 105:    */       }
/* 106:174 */       switch (this.m_AttributeTypes[i])
/* 107:    */       {
/* 108:    */       case 1: 
/* 109:176 */         if (this.m_NominalStrings[i].size() > 0) {
/* 110:177 */           attribInfo.add(new Attribute(attribName, this.m_NominalStrings[i]));
/* 111:    */         } else {
/* 112:179 */           attribInfo.add(new Attribute(attribName, (ArrayList)null));
/* 113:    */         }
/* 114:181 */         break;
/* 115:    */       case 0: 
/* 116:183 */         attribInfo.add(new Attribute(attribName));
/* 117:184 */         break;
/* 118:    */       case 2: 
/* 119:186 */         attribInfo.add(new Attribute(attribName, (ArrayList)null));
/* 120:187 */         break;
/* 121:    */       default: 
/* 122:189 */         throw new Exception("Unknown attribute type");
/* 123:    */       }
/* 124:    */     }
/* 125:193 */     Instances result = new Instances("InstanceResultListener", attribInfo, this.m_Instances.size());
/* 126:195 */     for (int i = 0; i < this.m_Instances.size(); i++) {
/* 127:196 */       result.add((Instance)this.m_Instances.get(i));
/* 128:    */     }
/* 129:199 */     this.m_Out.println(new Instances(result, 0));
/* 130:200 */     for (int i = 0; i < result.numInstances(); i++) {
/* 131:201 */       this.m_Out.println(result.instance(i));
/* 132:    */     }
/* 133:204 */     if ((this.m_OutputFile != null) && (!this.m_OutputFile.getName().equals("-"))) {
/* 134:205 */       this.m_Out.close();
/* 135:    */     }
/* 136:    */   }
/* 137:    */   
/* 138:    */   public void acceptResult(ResultProducer rp, Object[] key, Object[] result)
/* 139:    */     throws Exception
/* 140:    */   {
/* 141:221 */     if (this.m_RP != rp) {
/* 142:222 */       throw new Error("Unrecognized ResultProducer sending results!!");
/* 143:    */     }
/* 144:225 */     Instance newInst = new DenseInstance(this.m_AttributeTypes.length);
/* 145:226 */     for (int i = 0; i < this.m_AttributeTypes.length; i++)
/* 146:    */     {
/* 147:227 */       Object val = null;
/* 148:228 */       if (i < key.length) {
/* 149:229 */         val = key[i];
/* 150:    */       } else {
/* 151:231 */         val = result[(i - key.length)];
/* 152:    */       }
/* 153:233 */       if (val == null) {
/* 154:234 */         newInst.setValue(i, Utils.missingValue());
/* 155:    */       } else {
/* 156:236 */         switch (this.m_AttributeTypes[i])
/* 157:    */         {
/* 158:    */         case 1: 
/* 159:238 */           String str = (String)val;
/* 160:239 */           Double index = (Double)this.m_NominalIndexes[i].get(str);
/* 161:240 */           if (index == null)
/* 162:    */           {
/* 163:241 */             index = new Double(this.m_NominalStrings[i].size());
/* 164:242 */             this.m_NominalIndexes[i].put(str, index);
/* 165:243 */             this.m_NominalStrings[i].add(str);
/* 166:    */           }
/* 167:245 */           newInst.setValue(i, index.doubleValue());
/* 168:246 */           break;
/* 169:    */         case 0: 
/* 170:248 */           double dou = ((Double)val).doubleValue();
/* 171:249 */           newInst.setValue(i, dou);
/* 172:250 */           break;
/* 173:    */         default: 
/* 174:252 */           newInst.setValue(i, Utils.missingValue());
/* 175:    */         }
/* 176:    */       }
/* 177:    */     }
/* 178:256 */     this.m_Instances.add(newInst);
/* 179:    */   }
/* 180:    */   
/* 181:    */   public String getRevision()
/* 182:    */   {
/* 183:266 */     return RevisionUtils.extract("$Revision: 10203 $");
/* 184:    */   }
/* 185:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.experiment.InstancesResultListener
 * JD-Core Version:    0.7.0.1
 */