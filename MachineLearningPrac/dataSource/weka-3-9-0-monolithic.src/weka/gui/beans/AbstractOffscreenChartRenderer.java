/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.List;
/*   5:    */ import weka.core.Attribute;
/*   6:    */ import weka.core.Instance;
/*   7:    */ import weka.core.Instances;
/*   8:    */ 
/*   9:    */ public abstract class AbstractOffscreenChartRenderer
/*  10:    */   implements OffscreenChartRenderer
/*  11:    */ {
/*  12:    */   protected List<Instances> splitToClasses(Instances insts, int classIndex)
/*  13:    */   {
/*  14: 52 */     List<Instances> newSeries = new ArrayList();
/*  15:    */     
/*  16: 54 */     Instances[] classes = new Instances[insts.attribute(classIndex).numValues()];
/*  17: 55 */     for (int i = 0; i < classes.length; i++)
/*  18:    */     {
/*  19: 56 */       classes[i] = new Instances(insts, 0);
/*  20: 57 */       classes[i].setRelationName(insts.attribute(classIndex).value(i));
/*  21:    */     }
/*  22: 60 */     for (int i = 0; i < insts.numInstances(); i++)
/*  23:    */     {
/*  24: 61 */       Instance current = insts.instance(i);
/*  25: 62 */       classes[((int)current.value(classIndex))].add((Instance)current.copy());
/*  26:    */     }
/*  27: 65 */     for (int i = 0; i < classes.length; i++) {
/*  28: 66 */       newSeries.add(classes[i]);
/*  29:    */     }
/*  30: 69 */     return newSeries;
/*  31:    */   }
/*  32:    */   
/*  33:    */   public String optionsTipTextHTML()
/*  34:    */   {
/*  35: 81 */     return "<html><ul><li>No options for this renderer</li></ul></html>";
/*  36:    */   }
/*  37:    */   
/*  38:    */   protected String getOption(List<String> options, String toGet)
/*  39:    */   {
/*  40: 99 */     String value = null;
/*  41:101 */     if (options == null) {
/*  42:102 */       return null;
/*  43:    */     }
/*  44:105 */     for (String option : options) {
/*  45:106 */       if (option.startsWith(toGet))
/*  46:    */       {
/*  47:107 */         String[] parts = option.split("=");
/*  48:108 */         if (parts.length != 2) {
/*  49:109 */           return "";
/*  50:    */         }
/*  51:111 */         value = parts[1];
/*  52:112 */         break;
/*  53:    */       }
/*  54:    */     }
/*  55:116 */     return value;
/*  56:    */   }
/*  57:    */   
/*  58:    */   protected int getIndexOfAttribute(Instances insts, String attName)
/*  59:    */   {
/*  60:130 */     if (attName == null) {
/*  61:131 */       return -1;
/*  62:    */     }
/*  63:135 */     if (attName.equalsIgnoreCase("/last")) {
/*  64:136 */       return insts.numAttributes() - 1;
/*  65:    */     }
/*  66:138 */     if (attName.equalsIgnoreCase("/first")) {
/*  67:139 */       return 0;
/*  68:    */     }
/*  69:141 */     if (attName.startsWith("/"))
/*  70:    */     {
/*  71:143 */       String numS = attName.replace("/", "");
/*  72:    */       try
/*  73:    */       {
/*  74:145 */         int index = Integer.parseInt(numS);
/*  75:146 */         index--;
/*  76:147 */         if ((index >= 0) && (index < insts.numAttributes())) {
/*  77:148 */           return index;
/*  78:    */         }
/*  79:    */       }
/*  80:    */       catch (NumberFormatException e) {}
/*  81:    */     }
/*  82:154 */     Attribute att = insts.attribute(attName);
/*  83:155 */     if (att != null) {
/*  84:156 */       return att.index();
/*  85:    */     }
/*  86:159 */     return -1;
/*  87:    */   }
/*  88:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.AbstractOffscreenChartRenderer
 * JD-Core Version:    0.7.0.1
 */