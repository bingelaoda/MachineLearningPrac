/*   1:    */ package weka.core.pmml;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import org.w3c.dom.Element;
/*   5:    */ import weka.core.Attribute;
/*   6:    */ 
/*   7:    */ public class FieldRef
/*   8:    */   extends Expression
/*   9:    */ {
/*  10:    */   private static final long serialVersionUID = -8009605897876168409L;
/*  11: 45 */   protected String m_fieldName = null;
/*  12:    */   
/*  13:    */   public FieldRef(Element fieldRef, FieldMetaInfo.Optype opType, ArrayList<Attribute> fieldDefs)
/*  14:    */     throws Exception
/*  15:    */   {
/*  16: 49 */     super(opType, fieldDefs);
/*  17:    */     
/*  18: 51 */     this.m_fieldName = fieldRef.getAttribute("field");
/*  19:    */   }
/*  20:    */   
/*  21:    */   public void setFieldDefs(ArrayList<Attribute> fieldDefs)
/*  22:    */     throws Exception
/*  23:    */   {
/*  24: 55 */     super.setFieldDefs(fieldDefs);
/*  25: 56 */     validateField();
/*  26:    */   }
/*  27:    */   
/*  28:    */   protected void validateField()
/*  29:    */     throws Exception
/*  30:    */   {
/*  31: 61 */     if (this.m_fieldDefs != null)
/*  32:    */     {
/*  33: 62 */       Attribute a = getFieldDef(this.m_fieldName);
/*  34: 63 */       if (a == null) {
/*  35: 64 */         throw new Exception("[FieldRef] Can't find field " + this.m_fieldName + " in the supplied field definitions");
/*  36:    */       }
/*  37: 67 */       if (((this.m_opType == FieldMetaInfo.Optype.CATEGORICAL) || (this.m_opType == FieldMetaInfo.Optype.ORDINAL)) && (a.isNumeric())) {
/*  38: 69 */         throw new IllegalArgumentException("[FieldRef] Optype is categorical/ordinal but matching parameter in the field definitions is not!");
/*  39:    */       }
/*  40: 73 */       if ((this.m_opType == FieldMetaInfo.Optype.CONTINUOUS) && (a.isNominal())) {
/*  41: 74 */         throw new IllegalArgumentException("[FieldRef] Optype is continuous but matching parameter in the field definitions is not!");
/*  42:    */       }
/*  43:    */     }
/*  44:    */   }
/*  45:    */   
/*  46:    */   public double getResult(double[] incoming)
/*  47:    */     throws Exception
/*  48:    */   {
/*  49: 83 */     double result = (0.0D / 0.0D);
/*  50: 84 */     boolean found = false;
/*  51: 86 */     for (int i = 0; i < this.m_fieldDefs.size(); i++)
/*  52:    */     {
/*  53: 87 */       Attribute a = (Attribute)this.m_fieldDefs.get(i);
/*  54: 88 */       if (a.name().equals(this.m_fieldName))
/*  55:    */       {
/*  56: 89 */         if (a.isNumeric())
/*  57:    */         {
/*  58: 90 */           if ((this.m_opType == FieldMetaInfo.Optype.CATEGORICAL) || (this.m_opType == FieldMetaInfo.Optype.ORDINAL)) {
/*  59: 92 */             throw new IllegalArgumentException("[FieldRef] Optype is categorical/ordinal but matching parameter is not!");
/*  60:    */           }
/*  61:    */         }
/*  62: 95 */         else if (a.isNominal())
/*  63:    */         {
/*  64: 96 */           if (this.m_opType == FieldMetaInfo.Optype.CONTINUOUS) {
/*  65: 97 */             throw new IllegalArgumentException("[FieldRef] Optype is continuous but matching parameter is not!");
/*  66:    */           }
/*  67:    */         }
/*  68:    */         else {
/*  69:101 */           throw new IllegalArgumentException("[FieldRef] Unhandled attribute type");
/*  70:    */         }
/*  71:103 */         result = incoming[i];
/*  72:104 */         found = true;
/*  73:105 */         break;
/*  74:    */       }
/*  75:    */     }
/*  76:109 */     if (!found) {
/*  77:110 */       throw new Exception("[FieldRef] this field: " + this.m_fieldName + " is not in the supplied " + "list of parameters!");
/*  78:    */     }
/*  79:113 */     return result;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public String getResultCategorical(double[] incoming)
/*  83:    */     throws Exception
/*  84:    */   {
/*  85:120 */     if (this.m_opType == FieldMetaInfo.Optype.CONTINUOUS) {
/*  86:121 */       throw new IllegalArgumentException("[FieldRef] Can't return result as categorical/ordinal because optype is continuous!");
/*  87:    */     }
/*  88:125 */     boolean found = false;
/*  89:126 */     String result = null;
/*  90:128 */     for (int i = 0; i < this.m_fieldDefs.size(); i++)
/*  91:    */     {
/*  92:129 */       Attribute a = (Attribute)this.m_fieldDefs.get(i);
/*  93:130 */       if (a.name().equals(this.m_fieldName))
/*  94:    */       {
/*  95:131 */         found = true;
/*  96:132 */         result = a.value((int)incoming[i]);
/*  97:133 */         break;
/*  98:    */       }
/*  99:    */     }
/* 100:137 */     if (!found) {
/* 101:138 */       throw new Exception("[FieldRef] this field: " + this.m_fieldName + " is not in the supplied " + "list of parameters!");
/* 102:    */     }
/* 103:141 */     return result;
/* 104:    */   }
/* 105:    */   
/* 106:    */   public Attribute getOutputDef()
/* 107:    */   {
/* 108:153 */     Attribute a = getFieldDef(this.m_fieldName);
/* 109:154 */     if (a != null) {
/* 110:155 */       return a;
/* 111:    */     }
/* 112:162 */     return null;
/* 113:    */   }
/* 114:    */   
/* 115:    */   public String toString(String pad)
/* 116:    */   {
/* 117:166 */     return pad + "FieldRef: " + this.m_fieldName;
/* 118:    */   }
/* 119:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.FieldRef
 * JD-Core Version:    0.7.0.1
 */