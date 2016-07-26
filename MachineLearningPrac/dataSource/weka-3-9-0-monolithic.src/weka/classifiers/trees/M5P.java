/*   1:    */ package weka.classifiers.trees;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.classifiers.trees.m5.M5Base;
/*   7:    */ import weka.classifiers.trees.m5.Rule;
/*   8:    */ import weka.classifiers.trees.m5.RuleNode;
/*   9:    */ import weka.core.Drawable;
/*  10:    */ import weka.core.Option;
/*  11:    */ import weka.core.RevisionUtils;
/*  12:    */ import weka.core.Utils;
/*  13:    */ 
/*  14:    */ public class M5P
/*  15:    */   extends M5Base
/*  16:    */   implements Drawable
/*  17:    */ {
/*  18:    */   static final long serialVersionUID = -6118439039768244417L;
/*  19:    */   
/*  20:    */   public M5P()
/*  21:    */   {
/*  22:120 */     setGenerateRules(false);
/*  23:    */   }
/*  24:    */   
/*  25:    */   public int graphType()
/*  26:    */   {
/*  27:130 */     return 1;
/*  28:    */   }
/*  29:    */   
/*  30:    */   public String graph()
/*  31:    */     throws Exception
/*  32:    */   {
/*  33:141 */     StringBuffer text = new StringBuffer();
/*  34:    */     
/*  35:143 */     text.append("digraph M5Tree {\n");
/*  36:144 */     Rule temp = (Rule)this.m_ruleSet.get(0);
/*  37:145 */     temp.topOfTree().graph(text);
/*  38:146 */     text.append("}\n");
/*  39:147 */     return text.toString();
/*  40:    */   }
/*  41:    */   
/*  42:    */   public String saveInstancesTipText()
/*  43:    */   {
/*  44:157 */     return "Whether to save instance data at each node in the tree for visualization purposes.";
/*  45:    */   }
/*  46:    */   
/*  47:    */   public void setSaveInstances(boolean save)
/*  48:    */   {
/*  49:168 */     this.m_saveInstances = save;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public boolean getSaveInstances()
/*  53:    */   {
/*  54:177 */     return this.m_saveInstances;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public Enumeration<Option> listOptions()
/*  58:    */   {
/*  59:187 */     Enumeration<Option> superOpts = super.listOptions();
/*  60:    */     
/*  61:189 */     Vector<Option> newVector = new Vector();
/*  62:190 */     while (superOpts.hasMoreElements()) {
/*  63:191 */       newVector.addElement(superOpts.nextElement());
/*  64:    */     }
/*  65:194 */     newVector.addElement(new Option("\tSave instances at the nodes in\n\tthe tree (for visualization purposes)", "L", 0, "-L"));
/*  66:    */     
/*  67:196 */     return newVector.elements();
/*  68:    */   }
/*  69:    */   
/*  70:    */   public void setOptions(String[] options)
/*  71:    */     throws Exception
/*  72:    */   {
/*  73:240 */     setSaveInstances(Utils.getFlag('L', options));
/*  74:241 */     super.setOptions(options);
/*  75:    */   }
/*  76:    */   
/*  77:    */   public String[] getOptions()
/*  78:    */   {
/*  79:251 */     String[] superOpts = super.getOptions();
/*  80:252 */     String[] options = new String[superOpts.length + 1];
/*  81:253 */     int current = superOpts.length;
/*  82:254 */     for (int i = 0; i < current; i++) {
/*  83:255 */       options[i] = superOpts[i];
/*  84:    */     }
/*  85:258 */     if (getSaveInstances()) {
/*  86:259 */       options[(current++)] = "-L";
/*  87:    */     }
/*  88:262 */     while (current < options.length) {
/*  89:263 */       options[(current++)] = "";
/*  90:    */     }
/*  91:266 */     return options;
/*  92:    */   }
/*  93:    */   
/*  94:    */   public String getRevision()
/*  95:    */   {
/*  96:276 */     return RevisionUtils.extract("$Revision: 10153 $");
/*  97:    */   }
/*  98:    */   
/*  99:    */   public static void main(String[] args)
/* 100:    */   {
/* 101:285 */     runClassifier(new M5P(), args);
/* 102:    */   }
/* 103:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.M5P
 * JD-Core Version:    0.7.0.1
 */