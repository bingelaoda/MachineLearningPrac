/*   1:    */ package weka.filters.unsupervised.attribute;
/*   2:    */ 
/*   3:    */ import java.util.Enumeration;
/*   4:    */ import java.util.Vector;
/*   5:    */ import weka.core.Instance;
/*   6:    */ import weka.core.Instances;
/*   7:    */ import weka.core.Option;
/*   8:    */ import weka.core.RevisionUtils;
/*   9:    */ import weka.core.TechnicalInformation;
/*  10:    */ import weka.core.TechnicalInformation.Field;
/*  11:    */ import weka.core.TechnicalInformation.Type;
/*  12:    */ import weka.core.TechnicalInformationHandler;
/*  13:    */ import weka.core.Utils;
/*  14:    */ 
/*  15:    */ public class PKIDiscretize
/*  16:    */   extends Discretize
/*  17:    */   implements TechnicalInformationHandler
/*  18:    */ {
/*  19:    */   static final long serialVersionUID = 6153101248977702675L;
/*  20:    */   
/*  21:    */   public boolean setInputFormat(Instances instanceInfo)
/*  22:    */     throws Exception
/*  23:    */   {
/*  24:116 */     this.m_FindNumBins = true;
/*  25:117 */     return super.setInputFormat(instanceInfo);
/*  26:    */   }
/*  27:    */   
/*  28:    */   protected void findNumBins(int index)
/*  29:    */   {
/*  30:128 */     Instances toFilter = getInputFormat();
/*  31:    */     
/*  32:    */ 
/*  33:131 */     int numOfInstances = toFilter.numInstances();
/*  34:132 */     for (int i = 0; i < toFilter.numInstances(); i++) {
/*  35:133 */       if (toFilter.instance(i).isMissing(index)) {
/*  36:134 */         numOfInstances--;
/*  37:    */       }
/*  38:    */     }
/*  39:138 */     this.m_NumBins = ((int)Math.sqrt(numOfInstances));
/*  40:140 */     if (this.m_NumBins > 0) {
/*  41:141 */       calculateCutPointsByEqualFrequencyBinning(index);
/*  42:    */     }
/*  43:    */   }
/*  44:    */   
/*  45:    */   public Enumeration<Option> listOptions()
/*  46:    */   {
/*  47:153 */     Vector<Option> result = new Vector();
/*  48:    */     
/*  49:155 */     result.addElement(new Option("\tUnsets the class index temporarily before the filter is\n\tapplied to the data.\n\t(default: no)", "unset-class-temporarily", 1, "-unset-class-temporarily"));
/*  50:    */     
/*  51:    */ 
/*  52:    */ 
/*  53:    */ 
/*  54:160 */     result.addElement(new Option("\tSpecifies list of columns to Discretize. First and last are valid indexes.\n\t(default: first-last)", "R", 1, "-R <col1,col2-col4,...>"));
/*  55:    */     
/*  56:    */ 
/*  57:    */ 
/*  58:    */ 
/*  59:165 */     result.addElement(new Option("\tInvert matching sense of column indexes.", "V", 0, "-V"));
/*  60:    */     
/*  61:    */ 
/*  62:168 */     result.addElement(new Option("\tOutput binary attributes for discretized attributes.", "D", 0, "-D"));
/*  63:    */     
/*  64:    */ 
/*  65:171 */     return result.elements();
/*  66:    */   }
/*  67:    */   
/*  68:    */   public void setOptions(String[] options)
/*  69:    */     throws Exception
/*  70:    */   {
/*  71:212 */     setIgnoreClass(Utils.getFlag("unset-class-temporarily", options));
/*  72:213 */     setMakeBinary(Utils.getFlag('D', options));
/*  73:214 */     setInvertSelection(Utils.getFlag('V', options));
/*  74:    */     
/*  75:216 */     String convertList = Utils.getOption('R', options);
/*  76:217 */     if (convertList.length() != 0) {
/*  77:218 */       setAttributeIndices(convertList);
/*  78:    */     } else {
/*  79:220 */       setAttributeIndices("first-last");
/*  80:    */     }
/*  81:223 */     if (getInputFormat() != null) {
/*  82:224 */       setInputFormat(getInputFormat());
/*  83:    */     }
/*  84:227 */     Utils.checkForRemainingOptions(options);
/*  85:    */   }
/*  86:    */   
/*  87:    */   public String[] getOptions()
/*  88:    */   {
/*  89:238 */     Vector<String> result = new Vector();
/*  90:240 */     if (getMakeBinary()) {
/*  91:241 */       result.add("-D");
/*  92:    */     }
/*  93:244 */     if (getInvertSelection()) {
/*  94:245 */       result.add("-V");
/*  95:    */     }
/*  96:248 */     if (!getAttributeIndices().equals(""))
/*  97:    */     {
/*  98:249 */       result.add("-R");
/*  99:250 */       result.add(getAttributeIndices());
/* 100:    */     }
/* 101:253 */     return (String[])result.toArray(new String[result.size()]);
/* 102:    */   }
/* 103:    */   
/* 104:    */   public String globalInfo()
/* 105:    */   {
/* 106:265 */     return "Discretizes numeric attributes using equal frequency binning, where the number of bins is equal to the square root of the number of non-missing values.\n\nFor more information, see:\n\n" + getTechnicalInformation().toString();
/* 107:    */   }
/* 108:    */   
/* 109:    */   public TechnicalInformation getTechnicalInformation()
/* 110:    */   {
/* 111:282 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/* 112:283 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Ying Yang and Geoffrey I. Webb");
/* 113:284 */     result.setValue(TechnicalInformation.Field.TITLE, "Proportional k-Interval Discretization for Naive-Bayes Classifiers");
/* 114:    */     
/* 115:286 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "12th European Conference on Machine Learning");
/* 116:    */     
/* 117:288 */     result.setValue(TechnicalInformation.Field.YEAR, "2001");
/* 118:289 */     result.setValue(TechnicalInformation.Field.PAGES, "564-575");
/* 119:290 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "Springer");
/* 120:291 */     result.setValue(TechnicalInformation.Field.SERIES, "LNCS");
/* 121:292 */     result.setValue(TechnicalInformation.Field.VOLUME, "2167");
/* 122:    */     
/* 123:294 */     return result;
/* 124:    */   }
/* 125:    */   
/* 126:    */   public String findNumBinsTipText()
/* 127:    */   {
/* 128:306 */     return "Ignored.";
/* 129:    */   }
/* 130:    */   
/* 131:    */   public boolean getFindNumBins()
/* 132:    */   {
/* 133:317 */     return false;
/* 134:    */   }
/* 135:    */   
/* 136:    */   public void setFindNumBins(boolean newFindNumBins) {}
/* 137:    */   
/* 138:    */   public String useEqualFrequencyTipText()
/* 139:    */   {
/* 140:339 */     return "Always true.";
/* 141:    */   }
/* 142:    */   
/* 143:    */   public boolean getUseEqualFrequency()
/* 144:    */   {
/* 145:350 */     return true;
/* 146:    */   }
/* 147:    */   
/* 148:    */   public void setUseEqualFrequency(boolean newUseEqualFrequency) {}
/* 149:    */   
/* 150:    */   public String binsTipText()
/* 151:    */   {
/* 152:372 */     return "Ignored.";
/* 153:    */   }
/* 154:    */   
/* 155:    */   public int getBins()
/* 156:    */   {
/* 157:383 */     return 0;
/* 158:    */   }
/* 159:    */   
/* 160:    */   public void setBins(int numBins) {}
/* 161:    */   
/* 162:    */   public String getRevision()
/* 163:    */   {
/* 164:403 */     return RevisionUtils.extract("$Revision: 10215 $");
/* 165:    */   }
/* 166:    */   
/* 167:    */   public static void main(String[] argv)
/* 168:    */   {
/* 169:412 */     runFilter(new PKIDiscretize(), argv);
/* 170:    */   }
/* 171:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.PKIDiscretize
 * JD-Core Version:    0.7.0.1
 */