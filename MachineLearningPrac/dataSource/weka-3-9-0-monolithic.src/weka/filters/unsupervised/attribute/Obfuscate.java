/*   1:    */ package weka.filters.unsupervised.attribute;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import weka.core.Attribute;
/*   6:    */ import weka.core.Capabilities;
/*   7:    */ import weka.core.Capabilities.Capability;
/*   8:    */ import weka.core.Instance;
/*   9:    */ import weka.core.Instances;
/*  10:    */ import weka.core.RevisionUtils;
/*  11:    */ import weka.filters.Filter;
/*  12:    */ import weka.filters.StreamableFilter;
/*  13:    */ import weka.filters.UnsupervisedFilter;
/*  14:    */ 
/*  15:    */ public class Obfuscate
/*  16:    */   extends Filter
/*  17:    */   implements UnsupervisedFilter, StreamableFilter
/*  18:    */ {
/*  19:    */   static final long serialVersionUID = -343922772462971561L;
/*  20:    */   
/*  21:    */   public String globalInfo()
/*  22:    */   {
/*  23: 60 */     return "A simple instance filter that renames the relation, all attribute names and all nominal (and string) attribute values. For exchanging sensitive datasets. Currently doesn't like string or relational attributes.";
/*  24:    */   }
/*  25:    */   
/*  26:    */   public Capabilities getCapabilities()
/*  27:    */   {
/*  28: 73 */     Capabilities result = super.getCapabilities();
/*  29: 74 */     result.disableAll();
/*  30:    */     
/*  31:    */ 
/*  32: 77 */     result.enableAllAttributes();
/*  33: 78 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  34:    */     
/*  35:    */ 
/*  36: 81 */     result.enableAllClasses();
/*  37: 82 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  38: 83 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  39:    */     
/*  40: 85 */     return result;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public boolean setInputFormat(Instances instanceInfo)
/*  44:    */     throws Exception
/*  45:    */   {
/*  46:100 */     super.setInputFormat(instanceInfo);
/*  47:    */     
/*  48:    */ 
/*  49:103 */     ArrayList<Attribute> v = new ArrayList();
/*  50:104 */     for (int i = 0; i < instanceInfo.numAttributes(); i++)
/*  51:    */     {
/*  52:105 */       Attribute oldAtt = instanceInfo.attribute(i);
/*  53:106 */       Attribute newAtt = null;
/*  54:107 */       switch (oldAtt.type())
/*  55:    */       {
/*  56:    */       case 0: 
/*  57:109 */         newAtt = new Attribute("A" + (i + 1));
/*  58:110 */         break;
/*  59:    */       case 3: 
/*  60:112 */         String format = oldAtt.getDateFormat();
/*  61:113 */         newAtt = new Attribute("A" + (i + 1), format);
/*  62:114 */         break;
/*  63:    */       case 1: 
/*  64:116 */         ArrayList<String> vals = new ArrayList();
/*  65:117 */         for (int j = 0; j < oldAtt.numValues(); j++) {
/*  66:118 */           vals.add("V" + (j + 1));
/*  67:    */         }
/*  68:120 */         newAtt = new Attribute("A" + (i + 1), vals);
/*  69:121 */         break;
/*  70:    */       case 2: 
/*  71:    */       case 4: 
/*  72:    */       default: 
/*  73:125 */         newAtt = (Attribute)oldAtt.copy();
/*  74:126 */         System.err.println("Not converting attribute: " + oldAtt.name());
/*  75:    */       }
/*  76:129 */       newAtt.setWeight(oldAtt.weight());
/*  77:130 */       v.add(newAtt);
/*  78:    */     }
/*  79:132 */     Instances newHeader = new Instances("R", v, 10);
/*  80:133 */     newHeader.setClassIndex(instanceInfo.classIndex());
/*  81:134 */     setOutputFormat(newHeader);
/*  82:135 */     return true;
/*  83:    */   }
/*  84:    */   
/*  85:    */   public boolean input(Instance instance)
/*  86:    */   {
/*  87:150 */     if (getInputFormat() == null) {
/*  88:151 */       throw new IllegalStateException("No input instance format defined");
/*  89:    */     }
/*  90:153 */     if (this.m_NewBatch)
/*  91:    */     {
/*  92:154 */       resetQueue();
/*  93:155 */       this.m_NewBatch = false;
/*  94:    */     }
/*  95:157 */     push((Instance)instance.copy(), false);
/*  96:158 */     return true;
/*  97:    */   }
/*  98:    */   
/*  99:    */   public String getRevision()
/* 100:    */   {
/* 101:168 */     return RevisionUtils.extract("$Revision: 12037 $");
/* 102:    */   }
/* 103:    */   
/* 104:    */   public static void main(String[] argv)
/* 105:    */   {
/* 106:177 */     runFilter(new Obfuscate(), argv);
/* 107:    */   }
/* 108:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.Obfuscate
 * JD-Core Version:    0.7.0.1
 */