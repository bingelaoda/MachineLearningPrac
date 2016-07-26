/*   1:    */ package weka.core.pmml.jaxbbindings;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.List;
/*   5:    */ import javax.xml.bind.annotation.XmlAccessType;
/*   6:    */ import javax.xml.bind.annotation.XmlAccessorType;
/*   7:    */ import javax.xml.bind.annotation.XmlAttribute;
/*   8:    */ import javax.xml.bind.annotation.XmlElement;
/*   9:    */ import javax.xml.bind.annotation.XmlRootElement;
/*  10:    */ import javax.xml.bind.annotation.XmlType;
/*  11:    */ 
/*  12:    */ @XmlAccessorType(XmlAccessType.FIELD)
/*  13:    */ @XmlType(name="", propOrder={"extension", "targetValue"})
/*  14:    */ @XmlRootElement(name="Target")
/*  15:    */ public class Target
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlElement(name="TargetValue", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  20:    */   protected List<TargetValue> targetValue;
/*  21:    */   @XmlAttribute
/*  22:    */   protected String castInteger;
/*  23:    */   @XmlAttribute(required=true)
/*  24:    */   protected String field;
/*  25:    */   @XmlAttribute
/*  26:    */   protected Double max;
/*  27:    */   @XmlAttribute
/*  28:    */   protected Double min;
/*  29:    */   @XmlAttribute
/*  30:    */   protected OPTYPE optype;
/*  31:    */   @XmlAttribute
/*  32:    */   protected Double rescaleConstant;
/*  33:    */   @XmlAttribute
/*  34:    */   protected Double rescaleFactor;
/*  35:    */   
/*  36:    */   public List<Extension> getExtension()
/*  37:    */   {
/*  38:108 */     if (this.extension == null) {
/*  39:109 */       this.extension = new ArrayList();
/*  40:    */     }
/*  41:111 */     return this.extension;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public List<TargetValue> getTargetValue()
/*  45:    */   {
/*  46:137 */     if (this.targetValue == null) {
/*  47:138 */       this.targetValue = new ArrayList();
/*  48:    */     }
/*  49:140 */     return this.targetValue;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public String getCastInteger()
/*  53:    */   {
/*  54:152 */     return this.castInteger;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public void setCastInteger(String value)
/*  58:    */   {
/*  59:164 */     this.castInteger = value;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public String getField()
/*  63:    */   {
/*  64:176 */     return this.field;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public void setField(String value)
/*  68:    */   {
/*  69:188 */     this.field = value;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public Double getMax()
/*  73:    */   {
/*  74:200 */     return this.max;
/*  75:    */   }
/*  76:    */   
/*  77:    */   public void setMax(Double value)
/*  78:    */   {
/*  79:212 */     this.max = value;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public Double getMin()
/*  83:    */   {
/*  84:224 */     return this.min;
/*  85:    */   }
/*  86:    */   
/*  87:    */   public void setMin(Double value)
/*  88:    */   {
/*  89:236 */     this.min = value;
/*  90:    */   }
/*  91:    */   
/*  92:    */   public OPTYPE getOptype()
/*  93:    */   {
/*  94:248 */     return this.optype;
/*  95:    */   }
/*  96:    */   
/*  97:    */   public void setOptype(OPTYPE value)
/*  98:    */   {
/*  99:260 */     this.optype = value;
/* 100:    */   }
/* 101:    */   
/* 102:    */   public double getRescaleConstant()
/* 103:    */   {
/* 104:272 */     if (this.rescaleConstant == null) {
/* 105:273 */       return 0.0D;
/* 106:    */     }
/* 107:275 */     return this.rescaleConstant.doubleValue();
/* 108:    */   }
/* 109:    */   
/* 110:    */   public void setRescaleConstant(Double value)
/* 111:    */   {
/* 112:288 */     this.rescaleConstant = value;
/* 113:    */   }
/* 114:    */   
/* 115:    */   public double getRescaleFactor()
/* 116:    */   {
/* 117:300 */     if (this.rescaleFactor == null) {
/* 118:301 */       return 1.0D;
/* 119:    */     }
/* 120:303 */     return this.rescaleFactor.doubleValue();
/* 121:    */   }
/* 122:    */   
/* 123:    */   public void setRescaleFactor(Double value)
/* 124:    */   {
/* 125:316 */     this.rescaleFactor = value;
/* 126:    */   }
/* 127:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.Target
 * JD-Core Version:    0.7.0.1
 */