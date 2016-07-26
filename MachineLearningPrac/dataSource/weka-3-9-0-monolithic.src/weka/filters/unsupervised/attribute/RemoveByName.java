/*   1:    */ package weka.filters.unsupervised.attribute;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.core.Attribute;
/*   7:    */ import weka.core.Capabilities;
/*   8:    */ import weka.core.Instance;
/*   9:    */ import weka.core.Instances;
/*  10:    */ import weka.core.Option;
/*  11:    */ import weka.core.RevisionUtils;
/*  12:    */ import weka.core.Utils;
/*  13:    */ import weka.filters.SimpleStreamFilter;
/*  14:    */ 
/*  15:    */ public class RemoveByName
/*  16:    */   extends SimpleStreamFilter
/*  17:    */ {
/*  18:    */   private static final long serialVersionUID = -3335106965521265631L;
/*  19:    */   public static final String DEFAULT_EXPRESSION = "^.*id$";
/*  20: 77 */   protected String m_Expression = "^.*id$";
/*  21:    */   protected boolean m_InvertSelection;
/*  22:    */   protected Remove m_Remove;
/*  23:    */   
/*  24:    */   public String globalInfo()
/*  25:    */   {
/*  26: 93 */     return "Removes attributes based on a regular expression matched against their names but will not remove the class attribute.";
/*  27:    */   }
/*  28:    */   
/*  29:    */   public Enumeration<Option> listOptions()
/*  30:    */   {
/*  31:105 */     Vector<Option> result = new Vector(2);
/*  32:    */     
/*  33:107 */     result.addElement(new Option("\tThe regular expression to match the attribute names against.\n\t(default: ^.*id$)", "E", 1, "-E <regular expression>"));
/*  34:    */     
/*  35:    */ 
/*  36:    */ 
/*  37:    */ 
/*  38:112 */     result.addElement(new Option("\tFlag for inverting the matching sense. If set, attributes are kept\n\tinstead of deleted.\n\t(default: off)", "V", 0, "-V"));
/*  39:    */     
/*  40:    */ 
/*  41:    */ 
/*  42:116 */     result.addAll(Collections.list(super.listOptions()));
/*  43:    */     
/*  44:118 */     return result.elements();
/*  45:    */   }
/*  46:    */   
/*  47:    */   public String[] getOptions()
/*  48:    */   {
/*  49:129 */     Vector<String> result = new Vector();
/*  50:    */     
/*  51:131 */     result.add("-E");
/*  52:132 */     result.add("" + getExpression());
/*  53:134 */     if (getInvertSelection()) {
/*  54:135 */       result.add("-V");
/*  55:    */     }
/*  56:138 */     Collections.addAll(result, super.getOptions());
/*  57:    */     
/*  58:140 */     return (String[])result.toArray(new String[result.size()]);
/*  59:    */   }
/*  60:    */   
/*  61:    */   public void setOptions(String[] options)
/*  62:    */     throws Exception
/*  63:    */   {
/*  64:176 */     String tmpStr = Utils.getOption("E", options);
/*  65:177 */     if (tmpStr.length() != 0) {
/*  66:178 */       setExpression(tmpStr);
/*  67:    */     } else {
/*  68:180 */       setExpression("^.*id$");
/*  69:    */     }
/*  70:183 */     setInvertSelection(Utils.getFlag("V", options));
/*  71:    */     
/*  72:185 */     super.setOptions(options);
/*  73:    */     
/*  74:187 */     Utils.checkForRemainingOptions(options);
/*  75:    */   }
/*  76:    */   
/*  77:    */   public void setExpression(String value)
/*  78:    */   {
/*  79:196 */     this.m_Expression = value;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public String getExpression()
/*  83:    */   {
/*  84:205 */     return this.m_Expression;
/*  85:    */   }
/*  86:    */   
/*  87:    */   public String expressionTipText()
/*  88:    */   {
/*  89:215 */     return "The regular expression to match the attribute names against.";
/*  90:    */   }
/*  91:    */   
/*  92:    */   public void setInvertSelection(boolean value)
/*  93:    */   {
/*  94:226 */     this.m_InvertSelection = value;
/*  95:    */   }
/*  96:    */   
/*  97:    */   public boolean getInvertSelection()
/*  98:    */   {
/*  99:235 */     return this.m_InvertSelection;
/* 100:    */   }
/* 101:    */   
/* 102:    */   public String invertSelectionTipText()
/* 103:    */   {
/* 104:245 */     return "Determines whether action is to select or delete. If set to true, only the specified attributes will be kept; If set to false, specified attributes will be deleted.";
/* 105:    */   }
/* 106:    */   
/* 107:    */   protected Instances determineOutputFormat(Instances inputFormat)
/* 108:    */     throws Exception
/* 109:    */   {
/* 110:265 */     Vector<Integer> indices = new Vector();
/* 111:266 */     for (int i = 0; i < inputFormat.numAttributes(); i++) {
/* 112:268 */       if (i == inputFormat.classIndex())
/* 113:    */       {
/* 114:269 */         if (getInvertSelection()) {
/* 115:270 */           indices.add(Integer.valueOf(i));
/* 116:    */         }
/* 117:    */       }
/* 118:274 */       else if (inputFormat.attribute(i).name().matches(this.m_Expression)) {
/* 119:275 */         indices.add(Integer.valueOf(i));
/* 120:    */       }
/* 121:    */     }
/* 122:278 */     int[] attributes = new int[indices.size()];
/* 123:279 */     for (int i = 0; i < indices.size(); i++) {
/* 124:280 */       attributes[i] = ((Integer)indices.get(i)).intValue();
/* 125:    */     }
/* 126:283 */     this.m_Remove = new Remove();
/* 127:284 */     this.m_Remove.setAttributeIndicesArray(attributes);
/* 128:285 */     this.m_Remove.setInvertSelection(getInvertSelection());
/* 129:286 */     this.m_Remove.setInputFormat(inputFormat);
/* 130:    */     
/* 131:288 */     return this.m_Remove.getOutputFormat();
/* 132:    */   }
/* 133:    */   
/* 134:    */   public Capabilities getCapabilities()
/* 135:    */   {
/* 136:301 */     Capabilities result = new Remove().getCapabilities();
/* 137:302 */     result.setOwner(this);
/* 138:    */     
/* 139:304 */     return result;
/* 140:    */   }
/* 141:    */   
/* 142:    */   protected Instance process(Instance instance)
/* 143:    */     throws Exception
/* 144:    */   {
/* 145:317 */     this.m_Remove.input(instance);
/* 146:318 */     return this.m_Remove.output();
/* 147:    */   }
/* 148:    */   
/* 149:    */   public String getRevision()
/* 150:    */   {
/* 151:328 */     return RevisionUtils.extract("$Revision: 10394 $");
/* 152:    */   }
/* 153:    */   
/* 154:    */   public static void main(String[] args)
/* 155:    */   {
/* 156:337 */     runFilter(new RemoveByName(), args);
/* 157:    */   }
/* 158:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.RemoveByName
 * JD-Core Version:    0.7.0.1
 */