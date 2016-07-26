/*   1:    */ package weka.filters.unsupervised.instance;
/*   2:    */ 
/*   3:    */ import java.util.Enumeration;
/*   4:    */ import java.util.Vector;
/*   5:    */ import weka.core.Capabilities;
/*   6:    */ import weka.core.Capabilities.Capability;
/*   7:    */ import weka.core.Instance;
/*   8:    */ import weka.core.Instances;
/*   9:    */ import weka.core.Option;
/*  10:    */ import weka.core.OptionHandler;
/*  11:    */ import weka.core.RevisionUtils;
/*  12:    */ import weka.core.SparseInstance;
/*  13:    */ import weka.core.Utils;
/*  14:    */ import weka.filters.Filter;
/*  15:    */ import weka.filters.StreamableFilter;
/*  16:    */ import weka.filters.UnsupervisedFilter;
/*  17:    */ 
/*  18:    */ public class NonSparseToSparse
/*  19:    */   extends Filter
/*  20:    */   implements UnsupervisedFilter, StreamableFilter, OptionHandler
/*  21:    */ {
/*  22:    */   static final long serialVersionUID = 4694489111366063852L;
/*  23: 57 */   protected boolean m_encodeMissingAsZero = false;
/*  24:    */   
/*  25:    */   public String globalInfo()
/*  26:    */   {
/*  27: 66 */     return "An instance filter that converts all incoming instances into sparse format.";
/*  28:    */   }
/*  29:    */   
/*  30:    */   public Capabilities getCapabilities()
/*  31:    */   {
/*  32: 78 */     Capabilities result = super.getCapabilities();
/*  33: 79 */     result.disableAll();
/*  34:    */     
/*  35:    */ 
/*  36: 82 */     result.enableAllAttributes();
/*  37: 83 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  38:    */     
/*  39:    */ 
/*  40: 86 */     result.enableAllClasses();
/*  41: 87 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  42: 88 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  43:    */     
/*  44: 90 */     return result;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public Enumeration<Option> listOptions()
/*  48:    */   {
/*  49:101 */     Vector<Option> result = new Vector();
/*  50:102 */     result.add(new Option("\tTreat missing values as zero.", "M", 0, "-M"));
/*  51:    */     
/*  52:104 */     return result.elements();
/*  53:    */   }
/*  54:    */   
/*  55:    */   public void setOptions(String[] options)
/*  56:    */     throws Exception
/*  57:    */   {
/*  58:110 */     this.m_encodeMissingAsZero = Utils.getFlag('M', options);
/*  59:    */     
/*  60:112 */     Utils.checkForRemainingOptions(options);
/*  61:    */   }
/*  62:    */   
/*  63:    */   public String[] getOptions()
/*  64:    */   {
/*  65:118 */     Vector<String> result = new Vector();
/*  66:120 */     if (this.m_encodeMissingAsZero) {
/*  67:121 */       result.add("-M");
/*  68:    */     }
/*  69:124 */     return (String[])result.toArray(new String[result.size()]);
/*  70:    */   }
/*  71:    */   
/*  72:    */   public void setTreatMissingValuesAsZero(boolean m)
/*  73:    */   {
/*  74:133 */     this.m_encodeMissingAsZero = m;
/*  75:    */   }
/*  76:    */   
/*  77:    */   public boolean getTreatMissingValuesAsZero()
/*  78:    */   {
/*  79:142 */     return this.m_encodeMissingAsZero;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public String treatMissingValuesAsZeroTipText()
/*  83:    */   {
/*  84:152 */     return "Treat missing values in the same way as zeros.";
/*  85:    */   }
/*  86:    */   
/*  87:    */   public boolean setInputFormat(Instances instanceInfo)
/*  88:    */     throws Exception
/*  89:    */   {
/*  90:167 */     super.setInputFormat(instanceInfo);
/*  91:168 */     Instances instNew = instanceInfo;
/*  92:    */     
/*  93:170 */     setOutputFormat(instNew);
/*  94:171 */     return true;
/*  95:    */   }
/*  96:    */   
/*  97:    */   public boolean input(Instance instance)
/*  98:    */   {
/*  99:186 */     Instance newInstance = null;
/* 100:188 */     if (getInputFormat() == null) {
/* 101:189 */       throw new IllegalStateException("No input instance format defined");
/* 102:    */     }
/* 103:191 */     if (this.m_NewBatch)
/* 104:    */     {
/* 105:192 */       resetQueue();
/* 106:193 */       this.m_NewBatch = false;
/* 107:    */     }
/* 108:196 */     if (this.m_encodeMissingAsZero)
/* 109:    */     {
/* 110:197 */       Instance tempInst = (Instance)instance.copy();
/* 111:198 */       tempInst.setDataset(getInputFormat());
/* 112:200 */       for (int i = 0; i < tempInst.numAttributes(); i++) {
/* 113:201 */         if (tempInst.isMissing(i)) {
/* 114:202 */           tempInst.setValue(i, 0.0D);
/* 115:    */         }
/* 116:    */       }
/* 117:205 */       instance = tempInst;
/* 118:    */     }
/* 119:208 */     newInstance = new SparseInstance(instance);
/* 120:209 */     newInstance.setDataset(instance.dataset());
/* 121:210 */     push(newInstance, false);
/* 122:    */     
/* 123:    */ 
/* 124:    */ 
/* 125:    */ 
/* 126:    */ 
/* 127:    */ 
/* 128:217 */     return true;
/* 129:    */   }
/* 130:    */   
/* 131:    */   public String getRevision()
/* 132:    */   {
/* 133:227 */     return RevisionUtils.extract("$Revision: 12477 $");
/* 134:    */   }
/* 135:    */   
/* 136:    */   public static void main(String[] argv)
/* 137:    */   {
/* 138:236 */     runFilter(new NonSparseToSparse(), argv);
/* 139:    */   }
/* 140:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.instance.NonSparseToSparse
 * JD-Core Version:    0.7.0.1
 */