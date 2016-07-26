/*   1:    */ package weka.core.pmml;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import org.w3c.dom.Element;
/*   5:    */ import weka.core.Attribute;
/*   6:    */ import weka.core.Utils;
/*   7:    */ 
/*   8:    */ public class NormDiscrete
/*   9:    */   extends Expression
/*  10:    */ {
/*  11:    */   private static final long serialVersionUID = -8854409417983908220L;
/*  12:    */   protected String m_fieldName;
/*  13:    */   protected Attribute m_field;
/*  14: 52 */   protected int m_fieldIndex = -1;
/*  15:    */   protected String m_fieldValue;
/*  16: 58 */   protected boolean m_mapMissingDefined = false;
/*  17:    */   protected double m_mapMissingTo;
/*  18: 68 */   protected int m_fieldValueIndex = -1;
/*  19:    */   
/*  20:    */   public NormDiscrete(Element normDisc, FieldMetaInfo.Optype opType, ArrayList<Attribute> fieldDefs)
/*  21:    */     throws Exception
/*  22:    */   {
/*  23: 84 */     super(opType, fieldDefs);
/*  24: 86 */     if (opType != FieldMetaInfo.Optype.CONTINUOUS) {
/*  25: 87 */       throw new Exception("[NormDiscrete] can only have a continuous optype");
/*  26:    */     }
/*  27: 90 */     this.m_fieldName = normDisc.getAttribute("field");
/*  28: 91 */     this.m_fieldValue = normDisc.getAttribute("value");
/*  29:    */     
/*  30: 93 */     String mapMissing = normDisc.getAttribute("mapMissingTo");
/*  31: 94 */     if ((mapMissing != null) && (mapMissing.length() > 0))
/*  32:    */     {
/*  33: 95 */       this.m_mapMissingTo = Double.parseDouble(mapMissing);
/*  34: 96 */       this.m_mapMissingDefined = true;
/*  35:    */     }
/*  36: 99 */     if (fieldDefs != null) {
/*  37:100 */       setUpField();
/*  38:    */     }
/*  39:    */   }
/*  40:    */   
/*  41:    */   public void setFieldDefs(ArrayList<Attribute> fieldDefs)
/*  42:    */     throws Exception
/*  43:    */   {
/*  44:111 */     super.setFieldDefs(fieldDefs);
/*  45:112 */     setUpField();
/*  46:    */   }
/*  47:    */   
/*  48:    */   private void setUpField()
/*  49:    */     throws Exception
/*  50:    */   {
/*  51:121 */     this.m_fieldIndex = -1;
/*  52:122 */     this.m_fieldValueIndex = -1;
/*  53:123 */     this.m_field = null;
/*  54:125 */     if (this.m_fieldDefs != null)
/*  55:    */     {
/*  56:126 */       this.m_fieldIndex = getFieldDefIndex(this.m_fieldName);
/*  57:128 */       if (this.m_fieldIndex < 0) {
/*  58:129 */         throw new Exception("[NormDiscrete] Can't find field " + this.m_fieldName + " in the supplied field definitions.");
/*  59:    */       }
/*  60:132 */       this.m_field = ((Attribute)this.m_fieldDefs.get(this.m_fieldIndex));
/*  61:134 */       if ((!this.m_field.isString()) && (!this.m_field.isNominal())) {
/*  62:135 */         throw new Exception("[NormDiscrete] reference field " + this.m_fieldName + " must be categorical");
/*  63:    */       }
/*  64:139 */       if (this.m_field.isNominal())
/*  65:    */       {
/*  66:141 */         this.m_fieldValueIndex = this.m_field.indexOfValue(this.m_fieldValue);
/*  67:142 */         if (this.m_fieldValueIndex < 0) {
/*  68:143 */           throw new Exception("[NormDiscrete] Unable to find value " + this.m_fieldValue + " in nominal attribute " + this.m_field.name());
/*  69:    */         }
/*  70:    */       }
/*  71:146 */       else if (this.m_field.isString())
/*  72:    */       {
/*  73:149 */         this.m_fieldValueIndex = this.m_field.addStringValue(this.m_fieldValue);
/*  74:    */       }
/*  75:    */     }
/*  76:    */   }
/*  77:    */   
/*  78:    */   protected Attribute getOutputDef()
/*  79:    */   {
/*  80:162 */     return new Attribute(this.m_fieldName + "=" + this.m_fieldValue);
/*  81:    */   }
/*  82:    */   
/*  83:    */   public double getResult(double[] incoming)
/*  84:    */     throws Exception
/*  85:    */   {
/*  86:177 */     double result = 0.0D;
/*  87:178 */     if (Utils.isMissingValue(incoming[this.m_fieldIndex]))
/*  88:    */     {
/*  89:179 */       if (this.m_mapMissingDefined) {
/*  90:180 */         result = this.m_mapMissingTo;
/*  91:    */       } else {
/*  92:182 */         result = incoming[this.m_fieldIndex];
/*  93:    */       }
/*  94:    */     }
/*  95:185 */     else if (this.m_fieldValueIndex == (int)incoming[this.m_fieldIndex]) {
/*  96:186 */       result = 1.0D;
/*  97:    */     }
/*  98:190 */     return result;
/*  99:    */   }
/* 100:    */   
/* 101:    */   public String getResultCategorical(double[] incoming)
/* 102:    */     throws Exception
/* 103:    */   {
/* 104:201 */     throw new Exception("[NormDiscrete] Can't return the result as a categorical value!");
/* 105:    */   }
/* 106:    */   
/* 107:    */   public String toString(String pad)
/* 108:    */   {
/* 109:205 */     StringBuffer buff = new StringBuffer();
/* 110:206 */     buff.append("NormDiscrete: " + this.m_fieldName + "=" + this.m_fieldValue);
/* 111:207 */     if (this.m_mapMissingDefined) {
/* 112:208 */       buff.append("\n" + pad + "map missing values to: " + this.m_mapMissingTo);
/* 113:    */     }
/* 114:211 */     return buff.toString();
/* 115:    */   }
/* 116:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.NormDiscrete
 * JD-Core Version:    0.7.0.1
 */