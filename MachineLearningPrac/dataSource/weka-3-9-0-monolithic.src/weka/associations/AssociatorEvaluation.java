/*   1:    */ package weka.associations;
/*   2:    */ 
/*   3:    */ import java.io.BufferedWriter;
/*   4:    */ import java.io.FileWriter;
/*   5:    */ import java.io.PrintStream;
/*   6:    */ import java.util.Enumeration;
/*   7:    */ import weka.core.Drawable;
/*   8:    */ import weka.core.Instances;
/*   9:    */ import weka.core.Option;
/*  10:    */ import weka.core.OptionHandler;
/*  11:    */ import weka.core.RevisionHandler;
/*  12:    */ import weka.core.RevisionUtils;
/*  13:    */ import weka.core.Utils;
/*  14:    */ import weka.core.converters.ConverterUtils.DataSource;
/*  15:    */ 
/*  16:    */ public class AssociatorEvaluation
/*  17:    */   implements RevisionHandler
/*  18:    */ {
/*  19:    */   protected StringBuffer m_Result;
/*  20:    */   
/*  21:    */   public AssociatorEvaluation()
/*  22:    */   {
/*  23: 53 */     this.m_Result = new StringBuffer();
/*  24:    */   }
/*  25:    */   
/*  26:    */   protected static String makeOptionString(Associator associator)
/*  27:    */   {
/*  28: 65 */     StringBuffer text = new StringBuffer();
/*  29:    */     
/*  30:    */ 
/*  31: 68 */     text.append("\nGeneral options:\n\n");
/*  32: 69 */     text.append("-t <training file>\n");
/*  33: 70 */     text.append("\tThe name of the training file.\n");
/*  34: 71 */     text.append("-g <name of graph file>\n");
/*  35: 72 */     text.append("\tOutputs the graph representation (if supported) of the associator to a file.\n");
/*  36: 76 */     if ((associator instanceof OptionHandler))
/*  37:    */     {
/*  38: 77 */       text.append("\nOptions specific to " + associator.getClass().getName().replaceAll(".*\\.", "") + ":\n\n");
/*  39:    */       
/*  40:    */ 
/*  41: 80 */       Enumeration<Option> enm = ((OptionHandler)associator).listOptions();
/*  42: 81 */       while (enm.hasMoreElements())
/*  43:    */       {
/*  44: 82 */         Option option = (Option)enm.nextElement();
/*  45: 83 */         text.append(option.synopsis() + "\n");
/*  46: 84 */         text.append(option.description() + "\n");
/*  47:    */       }
/*  48:    */     }
/*  49: 88 */     return text.toString();
/*  50:    */   }
/*  51:    */   
/*  52:    */   public static String evaluate(String associatorString, String[] options)
/*  53:    */     throws Exception
/*  54:    */   {
/*  55:    */     Associator associator;
/*  56:    */     try
/*  57:    */     {
/*  58:105 */       associator = (Associator)Class.forName(associatorString).newInstance();
/*  59:    */     }
/*  60:    */     catch (Exception e)
/*  61:    */     {
/*  62:107 */       throw new Exception("Can't find class with name " + associatorString + '.');
/*  63:    */     }
/*  64:111 */     return evaluate(associator, options);
/*  65:    */   }
/*  66:    */   
/*  67:    */   public static String evaluate(Associator associator, String[] options)
/*  68:    */     throws Exception
/*  69:    */   {
/*  70:126 */     String trainFileString = "";
/*  71:127 */     String graphFileName = "";
/*  72:132 */     if (Utils.getFlag('h', options)) {
/*  73:133 */       throw new Exception("\nHelp requested.\n" + makeOptionString(associator));
/*  74:    */     }
/*  75:    */     ConverterUtils.DataSource loader;
/*  76:    */     try
/*  77:    */     {
/*  78:138 */       trainFileString = Utils.getOption('t', options);
/*  79:139 */       if (trainFileString.length() == 0) {
/*  80:140 */         throw new Exception("No training file given!");
/*  81:    */       }
/*  82:142 */       loader = new ConverterUtils.DataSource(trainFileString);
/*  83:    */       
/*  84:144 */       graphFileName = Utils.getOption('g', options);
/*  85:147 */       if ((associator instanceof OptionHandler)) {
/*  86:148 */         ((OptionHandler)associator).setOptions(options);
/*  87:    */       }
/*  88:152 */       Utils.checkForRemainingOptions(options);
/*  89:    */     }
/*  90:    */     catch (Exception e)
/*  91:    */     {
/*  92:154 */       throw new Exception("\nWeka exception: " + e.getMessage() + "\n" + makeOptionString(associator));
/*  93:    */     }
/*  94:159 */     AssociatorEvaluation eval = new AssociatorEvaluation();
/*  95:160 */     String results = eval.evaluate(associator, new Instances(loader.getDataSet()));
/*  96:164 */     if (((associator instanceof Drawable)) && (graphFileName.length() != 0))
/*  97:    */     {
/*  98:165 */       BufferedWriter writer = new BufferedWriter(new FileWriter(graphFileName));
/*  99:166 */       writer.write(((Drawable)associator).graph());
/* 100:167 */       writer.newLine();
/* 101:168 */       writer.flush();
/* 102:169 */       writer.close();
/* 103:    */     }
/* 104:172 */     return results;
/* 105:    */   }
/* 106:    */   
/* 107:    */   public String evaluate(Associator associator, Instances data)
/* 108:    */     throws Exception
/* 109:    */   {
/* 110:191 */     long startTime = System.currentTimeMillis();
/* 111:192 */     associator.buildAssociations(data);
/* 112:193 */     long endTime = System.currentTimeMillis();
/* 113:    */     
/* 114:195 */     this.m_Result = new StringBuffer(associator.toString());
/* 115:196 */     this.m_Result.append("\n=== Evaluation ===\n\n");
/* 116:197 */     this.m_Result.append("Elapsed time: " + (endTime - startTime) / 1000.0D + "s");
/* 117:    */     
/* 118:199 */     this.m_Result.append("\n");
/* 119:    */     
/* 120:201 */     return this.m_Result.toString();
/* 121:    */   }
/* 122:    */   
/* 123:    */   public boolean equals(Object obj)
/* 124:    */   {
/* 125:213 */     if ((obj == null) || (!obj.getClass().equals(getClass()))) {
/* 126:214 */       return false;
/* 127:    */     }
/* 128:217 */     AssociatorEvaluation cmp = (AssociatorEvaluation)obj;
/* 129:    */     
/* 130:    */ 
/* 131:220 */     String associatingResults1 = this.m_Result.toString().replaceAll("Elapsed time.*", "");
/* 132:    */     
/* 133:222 */     String associatingResults2 = cmp.m_Result.toString().replaceAll("Elapsed time.*", "");
/* 134:224 */     if (!associatingResults1.equals(associatingResults2)) {
/* 135:225 */       return false;
/* 136:    */     }
/* 137:228 */     return true;
/* 138:    */   }
/* 139:    */   
/* 140:    */   public String toSummaryString()
/* 141:    */   {
/* 142:237 */     return toSummaryString("");
/* 143:    */   }
/* 144:    */   
/* 145:    */   public String toSummaryString(String title)
/* 146:    */   {
/* 147:249 */     StringBuffer result = new StringBuffer(title);
/* 148:250 */     if (title.length() != 0) {
/* 149:251 */       result.append("\n");
/* 150:    */     }
/* 151:253 */     result.append(this.m_Result);
/* 152:    */     
/* 153:255 */     return result.toString();
/* 154:    */   }
/* 155:    */   
/* 156:    */   public String toString()
/* 157:    */   {
/* 158:266 */     return toSummaryString();
/* 159:    */   }
/* 160:    */   
/* 161:    */   public String getRevision()
/* 162:    */   {
/* 163:276 */     return RevisionUtils.extract("$Revision: 10172 $");
/* 164:    */   }
/* 165:    */   
/* 166:    */   public static void main(String[] args)
/* 167:    */   {
/* 168:    */     try
/* 169:    */     {
/* 170:288 */       if (args.length == 0) {
/* 171:289 */         throw new Exception("The first argument must be the class name of a kernel");
/* 172:    */       }
/* 173:292 */       String associator = args[0];
/* 174:293 */       args[0] = "";
/* 175:294 */       System.out.println(evaluate(associator, args));
/* 176:    */     }
/* 177:    */     catch (Exception ex)
/* 178:    */     {
/* 179:296 */       ex.printStackTrace();
/* 180:297 */       System.err.println(ex.getMessage());
/* 181:    */     }
/* 182:    */   }
/* 183:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.associations.AssociatorEvaluation
 * JD-Core Version:    0.7.0.1
 */