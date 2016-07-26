/*   1:    */ package weka.gui.visualize;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Container;
/*   5:    */ import java.awt.event.ActionEvent;
/*   6:    */ import java.awt.event.ActionListener;
/*   7:    */ import java.awt.event.WindowAdapter;
/*   8:    */ import java.awt.event.WindowEvent;
/*   9:    */ import java.io.BufferedReader;
/*  10:    */ import java.io.FileReader;
/*  11:    */ import java.io.PrintStream;
/*  12:    */ import java.util.ArrayList;
/*  13:    */ import javax.swing.BorderFactory;
/*  14:    */ import javax.swing.JComboBox;
/*  15:    */ import javax.swing.JFrame;
/*  16:    */ import javax.swing.JPanel;
/*  17:    */ import javax.swing.border.TitledBorder;
/*  18:    */ import weka.classifiers.AbstractClassifier;
/*  19:    */ import weka.classifiers.Classifier;
/*  20:    */ import weka.classifiers.evaluation.EvaluationUtils;
/*  21:    */ import weka.classifiers.evaluation.Prediction;
/*  22:    */ import weka.classifiers.evaluation.ThresholdCurve;
/*  23:    */ import weka.classifiers.functions.Logistic;
/*  24:    */ import weka.core.Attribute;
/*  25:    */ import weka.core.Instances;
/*  26:    */ import weka.core.SingleIndex;
/*  27:    */ import weka.core.Utils;
/*  28:    */ 
/*  29:    */ public class ThresholdVisualizePanel
/*  30:    */   extends VisualizePanel
/*  31:    */ {
/*  32:    */   private static final long serialVersionUID = 3070002211779443890L;
/*  33: 60 */   private String m_ROCString = "";
/*  34:    */   private final String m_savePanelBorderText;
/*  35:    */   
/*  36:    */   public ThresholdVisualizePanel()
/*  37:    */   {
/*  38: 72 */     TitledBorder tb = (TitledBorder)this.m_plotSurround.getBorder();
/*  39: 73 */     this.m_savePanelBorderText = tb.getTitle();
/*  40:    */   }
/*  41:    */   
/*  42:    */   public void setROCString(String str)
/*  43:    */   {
/*  44: 82 */     this.m_ROCString = str;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public String getROCString()
/*  48:    */   {
/*  49: 91 */     return this.m_ROCString;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public void setUpComboBoxes(Instances inst)
/*  53:    */   {
/*  54:102 */     super.setUpComboBoxes(inst);
/*  55:    */     
/*  56:104 */     this.m_XCombo.addActionListener(new ActionListener()
/*  57:    */     {
/*  58:    */       public void actionPerformed(ActionEvent e)
/*  59:    */       {
/*  60:107 */         ThresholdVisualizePanel.this.setBorderText();
/*  61:    */       }
/*  62:109 */     });
/*  63:110 */     this.m_YCombo.addActionListener(new ActionListener()
/*  64:    */     {
/*  65:    */       public void actionPerformed(ActionEvent e)
/*  66:    */       {
/*  67:113 */         ThresholdVisualizePanel.this.setBorderText();
/*  68:    */       }
/*  69:117 */     });
/*  70:118 */     setBorderText();
/*  71:    */   }
/*  72:    */   
/*  73:    */   private void setBorderText()
/*  74:    */   {
/*  75:128 */     String xs = this.m_XCombo.getSelectedItem().toString();
/*  76:129 */     String ys = this.m_YCombo.getSelectedItem().toString();
/*  77:131 */     if ((xs.equals("X: False Positive Rate (Num)")) && (ys.equals("Y: True Positive Rate (Num)"))) {
/*  78:133 */       this.m_plotSurround.setBorder(BorderFactory.createTitledBorder(this.m_savePanelBorderText + " " + this.m_ROCString));
/*  79:    */     } else {
/*  80:136 */       this.m_plotSurround.setBorder(BorderFactory.createTitledBorder(this.m_savePanelBorderText));
/*  81:    */     }
/*  82:    */   }
/*  83:    */   
/*  84:    */   protected void openVisibleInstances(Instances insts)
/*  85:    */     throws Exception
/*  86:    */   {
/*  87:149 */     super.openVisibleInstances(insts);
/*  88:    */     
/*  89:151 */     setROCString("(Area under ROC = " + Utils.doubleToString(ThresholdCurve.getROCArea(insts), 4) + ")");
/*  90:    */     
/*  91:    */ 
/*  92:154 */     setBorderText();
/*  93:    */   }
/*  94:    */   
/*  95:    */   public static void main(String[] args)
/*  96:    */   {
/*  97:205 */     Instances inst = null;
/*  98:206 */     Classifier classifier = null;
/*  99:207 */     int runs = 2;
/* 100:208 */     int folds = 10;
/* 101:209 */     boolean compute = true;
/* 102:210 */     Instances result = null;
/* 103:211 */     SingleIndex classIndex = null;
/* 104:212 */     SingleIndex valueIndex = null;
/* 105:213 */     int seed = 1;
/* 106:    */     try
/* 107:    */     {
/* 108:217 */       if (Utils.getFlag('h', args))
/* 109:    */       {
/* 110:218 */         System.out.println("\nOptions for " + ThresholdVisualizePanel.class.getName() + ":\n");
/* 111:    */         
/* 112:220 */         System.out.println("-h\n\tThis help.");
/* 113:221 */         System.out.println("-t <file>\n\tDataset to process with given classifier.");
/* 114:    */         
/* 115:223 */         System.out.println("-c <num>\n\tThe class index. first and last are valid, too (default: last).");
/* 116:    */         
/* 117:225 */         System.out.println("-C <num>\n\tThe index of the class value to get the the curve for (default: first).");
/* 118:    */         
/* 119:227 */         System.out.println("-W <classname>\n\tFull classname of classifier to run.\n\tOptions after '--' are passed to the classifier.\n\t(default: weka.classifiers.functions.Logistic)");
/* 120:    */         
/* 121:229 */         System.out.println("-r <number>\n\tThe number of runs to perform (default: 1).");
/* 122:    */         
/* 123:231 */         System.out.println("-x <number>\n\tThe number of Cross-validation folds (default: 10).");
/* 124:    */         
/* 125:233 */         System.out.println("-S <number>\n\tThe seed value for randomizing the data (default: 1).");
/* 126:    */         
/* 127:235 */         System.out.println("-l <file>\n\tPreviously saved threshold curve ARFF file.");
/* 128:    */         
/* 129:237 */         return;
/* 130:    */       }
/* 131:241 */       String tmpStr = Utils.getOption('l', args);
/* 132:242 */       if (tmpStr.length() != 0)
/* 133:    */       {
/* 134:243 */         result = new Instances(new BufferedReader(new FileReader(tmpStr)));
/* 135:244 */         compute = false;
/* 136:    */       }
/* 137:247 */       if (compute)
/* 138:    */       {
/* 139:248 */         tmpStr = Utils.getOption('r', args);
/* 140:249 */         if (tmpStr.length() != 0) {
/* 141:250 */           runs = Integer.parseInt(tmpStr);
/* 142:    */         } else {
/* 143:252 */           runs = 1;
/* 144:    */         }
/* 145:255 */         tmpStr = Utils.getOption('x', args);
/* 146:256 */         if (tmpStr.length() != 0) {
/* 147:257 */           folds = Integer.parseInt(tmpStr);
/* 148:    */         } else {
/* 149:259 */           folds = 10;
/* 150:    */         }
/* 151:262 */         tmpStr = Utils.getOption('S', args);
/* 152:263 */         if (tmpStr.length() != 0) {
/* 153:264 */           seed = Integer.parseInt(tmpStr);
/* 154:    */         } else {
/* 155:266 */           seed = 1;
/* 156:    */         }
/* 157:269 */         tmpStr = Utils.getOption('t', args);
/* 158:270 */         if (tmpStr.length() != 0)
/* 159:    */         {
/* 160:271 */           inst = new Instances(new BufferedReader(new FileReader(tmpStr)));
/* 161:272 */           inst.setClassIndex(inst.numAttributes() - 1);
/* 162:    */         }
/* 163:275 */         tmpStr = Utils.getOption('W', args);
/* 164:    */         String[] options;
/* 165:    */         String[] options;
/* 166:276 */         if (tmpStr.length() != 0)
/* 167:    */         {
/* 168:277 */           options = Utils.partitionOptions(args);
/* 169:    */         }
/* 170:    */         else
/* 171:    */         {
/* 172:279 */           tmpStr = Logistic.class.getName();
/* 173:280 */           options = new String[0];
/* 174:    */         }
/* 175:282 */         classifier = AbstractClassifier.forName(tmpStr, options);
/* 176:    */         
/* 177:284 */         tmpStr = Utils.getOption('c', args);
/* 178:285 */         if (tmpStr.length() != 0) {
/* 179:286 */           classIndex = new SingleIndex(tmpStr);
/* 180:    */         } else {
/* 181:288 */           classIndex = new SingleIndex("last");
/* 182:    */         }
/* 183:291 */         tmpStr = Utils.getOption('C', args);
/* 184:292 */         if (tmpStr.length() != 0) {
/* 185:293 */           valueIndex = new SingleIndex(tmpStr);
/* 186:    */         } else {
/* 187:295 */           valueIndex = new SingleIndex("first");
/* 188:    */         }
/* 189:    */       }
/* 190:300 */       if (compute)
/* 191:    */       {
/* 192:301 */         if (classIndex != null)
/* 193:    */         {
/* 194:302 */           classIndex.setUpper(inst.numAttributes() - 1);
/* 195:303 */           inst.setClassIndex(classIndex.getIndex());
/* 196:    */         }
/* 197:    */         else
/* 198:    */         {
/* 199:305 */           inst.setClassIndex(inst.numAttributes() - 1);
/* 200:    */         }
/* 201:308 */         if (valueIndex != null) {
/* 202:309 */           valueIndex.setUpper(inst.classAttribute().numValues() - 1);
/* 203:    */         }
/* 204:312 */         ThresholdCurve tc = new ThresholdCurve();
/* 205:313 */         EvaluationUtils eu = new EvaluationUtils();
/* 206:314 */         ArrayList<Prediction> predictions = new ArrayList();
/* 207:315 */         for (int i = 0; i < runs; i++)
/* 208:    */         {
/* 209:316 */           eu.setSeed(seed + i);
/* 210:317 */           predictions.addAll(eu.getCVPredictions(classifier, inst, folds));
/* 211:    */         }
/* 212:320 */         if (valueIndex != null) {
/* 213:321 */           result = tc.getCurve(predictions, valueIndex.getIndex());
/* 214:    */         } else {
/* 215:323 */           result = tc.getCurve(predictions);
/* 216:    */         }
/* 217:    */       }
/* 218:328 */       ThresholdVisualizePanel vmc = new ThresholdVisualizePanel();
/* 219:329 */       vmc.setROCString("(Area under ROC = " + Utils.doubleToString(ThresholdCurve.getROCArea(result), 4) + ")");
/* 220:331 */       if (compute) {
/* 221:332 */         vmc.setName(result.relationName() + ". (Class value " + inst.classAttribute().value(valueIndex.getIndex()) + ")");
/* 222:    */       } else {
/* 223:335 */         vmc.setName(result.relationName() + " (display only)");
/* 224:    */       }
/* 225:337 */       PlotData2D tempd = new PlotData2D(result);
/* 226:338 */       tempd.setPlotName(result.relationName());
/* 227:339 */       tempd.addInstanceNumberAttribute();
/* 228:340 */       vmc.addPlot(tempd);
/* 229:    */       
/* 230:342 */       String plotName = vmc.getName();
/* 231:343 */       JFrame jf = new JFrame("Weka Classifier Visualize: " + plotName);
/* 232:344 */       jf.setSize(500, 400);
/* 233:345 */       jf.getContentPane().setLayout(new BorderLayout());
/* 234:    */       
/* 235:347 */       jf.getContentPane().add(vmc, "Center");
/* 236:348 */       jf.addWindowListener(new WindowAdapter()
/* 237:    */       {
/* 238:    */         public void windowClosing(WindowEvent e)
/* 239:    */         {
/* 240:351 */           this.val$jf.dispose();
/* 241:    */         }
/* 242:354 */       });
/* 243:355 */       jf.setVisible(true);
/* 244:    */     }
/* 245:    */     catch (Exception e)
/* 246:    */     {
/* 247:357 */       e.printStackTrace();
/* 248:    */     }
/* 249:    */   }
/* 250:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.visualize.ThresholdVisualizePanel
 * JD-Core Version:    0.7.0.1
 */