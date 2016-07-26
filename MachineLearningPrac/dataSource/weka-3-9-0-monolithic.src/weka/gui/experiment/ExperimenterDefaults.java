/*   1:    */ package weka.gui.experiment;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.io.Serializable;
/*   6:    */ import java.util.Collections;
/*   7:    */ import java.util.Enumeration;
/*   8:    */ import java.util.Properties;
/*   9:    */ import java.util.Vector;
/*  10:    */ import weka.core.Utils;
/*  11:    */ import weka.experiment.PairedCorrectedTTester;
/*  12:    */ import weka.experiment.ResultMatrix;
/*  13:    */ import weka.experiment.ResultMatrixPlainText;
/*  14:    */ 
/*  15:    */ public class ExperimenterDefaults
/*  16:    */   implements Serializable
/*  17:    */ {
/*  18:    */   static final long serialVersionUID = -2835933184632147981L;
/*  19:    */   public static final String PROPERTY_FILE = "weka/gui/experiment/Experimenter.props";
/*  20:    */   protected static Properties PROPERTIES;
/*  21:    */   
/*  22:    */   static
/*  23:    */   {
/*  24:    */     try
/*  25:    */     {
/*  26: 57 */       PROPERTIES = Utils.readProperties("weka/gui/experiment/Experimenter.props");
/*  27:    */     }
/*  28:    */     catch (Exception e)
/*  29:    */     {
/*  30: 59 */       System.err.println("Problem reading properties. Fix before continuing.");
/*  31: 60 */       e.printStackTrace();
/*  32: 61 */       PROPERTIES = new Properties();
/*  33:    */     }
/*  34:    */   }
/*  35:    */   
/*  36:    */   public static String get(String property, String defaultValue)
/*  37:    */   {
/*  38: 74 */     return PROPERTIES.getProperty(property, defaultValue);
/*  39:    */   }
/*  40:    */   
/*  41:    */   public static final Properties getProperties()
/*  42:    */   {
/*  43: 83 */     return PROPERTIES;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public static final String getSetupPanel()
/*  47:    */   {
/*  48: 92 */     return get("SetupPanel", SimpleSetupPanel.class.getName());
/*  49:    */   }
/*  50:    */   
/*  51:    */   public static final String getExtension()
/*  52:    */   {
/*  53:101 */     return get("Extension", ".exp");
/*  54:    */   }
/*  55:    */   
/*  56:    */   public static final String getDestination()
/*  57:    */   {
/*  58:110 */     return get("Destination", "ARFF file");
/*  59:    */   }
/*  60:    */   
/*  61:    */   public static final String getExperimentType()
/*  62:    */   {
/*  63:119 */     return get("ExperimentType", "Cross-validation");
/*  64:    */   }
/*  65:    */   
/*  66:    */   public static final boolean getUseClassification()
/*  67:    */   {
/*  68:128 */     return Boolean.valueOf(get("UseClassification", "true")).booleanValue();
/*  69:    */   }
/*  70:    */   
/*  71:    */   public static final int getFolds()
/*  72:    */   {
/*  73:137 */     return Integer.parseInt(get("Folds", "10"));
/*  74:    */   }
/*  75:    */   
/*  76:    */   public static final double getTrainPercentage()
/*  77:    */   {
/*  78:146 */     return Integer.parseInt(get("TrainPercentage", "66"));
/*  79:    */   }
/*  80:    */   
/*  81:    */   public static final int getRepetitions()
/*  82:    */   {
/*  83:155 */     return Integer.parseInt(get("Repetitions", "10"));
/*  84:    */   }
/*  85:    */   
/*  86:    */   public static final boolean getDatasetsFirst()
/*  87:    */   {
/*  88:164 */     return Boolean.valueOf(get("DatasetsFirst", "true")).booleanValue();
/*  89:    */   }
/*  90:    */   
/*  91:    */   public static final File getInitialDatasetsDirectory()
/*  92:    */   {
/*  93:176 */     String dir = get("InitialDatasetsDirectory", "");
/*  94:177 */     if (dir.equals("")) {
/*  95:178 */       dir = System.getProperty("user.dir");
/*  96:    */     }
/*  97:181 */     return new File(dir);
/*  98:    */   }
/*  99:    */   
/* 100:    */   public static final boolean getUseRelativePaths()
/* 101:    */   {
/* 102:190 */     return Boolean.valueOf(get("UseRelativePaths", "false")).booleanValue();
/* 103:    */   }
/* 104:    */   
/* 105:    */   public static final String getTester()
/* 106:    */   {
/* 107:201 */     return get("Tester", new PairedCorrectedTTester().getDisplayName());
/* 108:    */   }
/* 109:    */   
/* 110:    */   public static final String getRow()
/* 111:    */   {
/* 112:210 */     return get("Row", "Key_Dataset");
/* 113:    */   }
/* 114:    */   
/* 115:    */   public static final String getColumn()
/* 116:    */   {
/* 117:219 */     return get("Column", "Key_Scheme,Key_Scheme_options,Key_Scheme_version_ID");
/* 118:    */   }
/* 119:    */   
/* 120:    */   public static final String getComparisonField()
/* 121:    */   {
/* 122:228 */     return get("ComparisonField", "percent_correct");
/* 123:    */   }
/* 124:    */   
/* 125:    */   public static final double getSignificance()
/* 126:    */   {
/* 127:237 */     return Double.parseDouble(get("Significance", "0.05"));
/* 128:    */   }
/* 129:    */   
/* 130:    */   public static final String getSorting()
/* 131:    */   {
/* 132:246 */     return get("Sorting", "");
/* 133:    */   }
/* 134:    */   
/* 135:    */   public static final boolean getShowStdDevs()
/* 136:    */   {
/* 137:255 */     return Boolean.valueOf(get("ShowStdDev", "false")).booleanValue();
/* 138:    */   }
/* 139:    */   
/* 140:    */   public static final boolean getShowAverage()
/* 141:    */   {
/* 142:264 */     return Boolean.valueOf(get("ShowAverage", "false")).booleanValue();
/* 143:    */   }
/* 144:    */   
/* 145:    */   public static final int getMeanPrecision()
/* 146:    */   {
/* 147:273 */     return Integer.parseInt(get("MeanPrecision", "2"));
/* 148:    */   }
/* 149:    */   
/* 150:    */   public static final int getStdDevPrecision()
/* 151:    */   {
/* 152:282 */     return Integer.parseInt(get("StdDevPrecision", "2"));
/* 153:    */   }
/* 154:    */   
/* 155:    */   public static final ResultMatrix getOutputFormat()
/* 156:    */   {
/* 157:    */     ResultMatrix result;
/* 158:    */     try
/* 159:    */     {
/* 160:297 */       String[] options = Utils.splitOptions(get("OutputFormat", ResultMatrix.class.getName() + " -col-name-width 0 -row-name-width 25 -mean-width 0 -stddev-width 0 -sig-width 0 -count-width 5 -print-col-names -print-row-names -enum-col-names"));
/* 161:    */       
/* 162:    */ 
/* 163:    */ 
/* 164:    */ 
/* 165:302 */       String classname = options[0];
/* 166:303 */       options[0] = "";
/* 167:304 */       result = (ResultMatrix)Utils.forName(ResultMatrix.class, classname, options);
/* 168:    */     }
/* 169:    */     catch (Exception e)
/* 170:    */     {
/* 171:307 */       e.printStackTrace();
/* 172:308 */       result = new ResultMatrixPlainText();
/* 173:    */     }
/* 174:312 */     result.setMeanPrec(getMeanPrecision());
/* 175:313 */     result.setStdDevPrec(getStdDevPrecision());
/* 176:314 */     result.setShowAverage(getShowAverage());
/* 177:315 */     result.setShowStdDev(getShowStdDevs());
/* 178:316 */     result.setRemoveFilterName(getRemoveFilterClassnames());
/* 179:    */     
/* 180:318 */     return result;
/* 181:    */   }
/* 182:    */   
/* 183:    */   public static final boolean getRemoveFilterClassnames()
/* 184:    */   {
/* 185:327 */     return Boolean.valueOf(get("RemoveFilterClassnames", "false")).booleanValue();
/* 186:    */   }
/* 187:    */   
/* 188:    */   public static void main(String[] args)
/* 189:    */   {
/* 190:341 */     System.out.println("\nExperimenter defaults:");
/* 191:342 */     Enumeration<?> names = PROPERTIES.propertyNames();
/* 192:    */     
/* 193:    */ 
/* 194:345 */     Vector<String> sorted = new Vector();
/* 195:346 */     while (names.hasMoreElements()) {
/* 196:347 */       sorted.add(names.nextElement().toString());
/* 197:    */     }
/* 198:349 */     Collections.sort(sorted);
/* 199:350 */     names = sorted.elements();
/* 200:353 */     while (names.hasMoreElements())
/* 201:    */     {
/* 202:354 */       String name = names.nextElement().toString();
/* 203:355 */       System.out.println("- " + name + ": " + PROPERTIES.getProperty(name, ""));
/* 204:    */     }
/* 205:357 */     System.out.println();
/* 206:    */   }
/* 207:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.experiment.ExperimenterDefaults
 * JD-Core Version:    0.7.0.1
 */