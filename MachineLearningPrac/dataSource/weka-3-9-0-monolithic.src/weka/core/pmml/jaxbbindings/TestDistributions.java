/*   1:    */ package weka.core.pmml.jaxbbindings;
/*   2:    */ 
/*   3:    */ import java.math.BigInteger;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.List;
/*   6:    */ import javax.xml.bind.annotation.XmlAccessType;
/*   7:    */ import javax.xml.bind.annotation.XmlAccessorType;
/*   8:    */ import javax.xml.bind.annotation.XmlAttribute;
/*   9:    */ import javax.xml.bind.annotation.XmlElement;
/*  10:    */ import javax.xml.bind.annotation.XmlRootElement;
/*  11:    */ import javax.xml.bind.annotation.XmlType;
/*  12:    */ 
/*  13:    */ @XmlAccessorType(XmlAccessType.FIELD)
/*  14:    */ @XmlType(name="", propOrder={"baseline", "alternate", "extension"})
/*  15:    */ @XmlRootElement(name="TestDistributions")
/*  16:    */ public class TestDistributions
/*  17:    */ {
/*  18:    */   @XmlElement(name="Baseline", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  19:    */   protected Baseline baseline;
/*  20:    */   @XmlElement(name="Alternate", namespace="http://www.dmg.org/PMML-4_1")
/*  21:    */   protected Alternate alternate;
/*  22:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  23:    */   protected List<Extension> extension;
/*  24:    */   @XmlAttribute(required=true)
/*  25:    */   protected String field;
/*  26:    */   @XmlAttribute
/*  27:    */   protected String normalizationScheme;
/*  28:    */   @XmlAttribute
/*  29:    */   protected Double resetValue;
/*  30:    */   @XmlAttribute(required=true)
/*  31:    */   protected BASELINETESTSTATISTIC testStatistic;
/*  32:    */   @XmlAttribute
/*  33:    */   protected String weightField;
/*  34:    */   @XmlAttribute
/*  35:    */   protected BigInteger windowSize;
/*  36:    */   
/*  37:    */   public Baseline getBaseline()
/*  38:    */   {
/*  39: 88 */     return this.baseline;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public void setBaseline(Baseline value)
/*  43:    */   {
/*  44:100 */     this.baseline = value;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public Alternate getAlternate()
/*  48:    */   {
/*  49:112 */     return this.alternate;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public void setAlternate(Alternate value)
/*  53:    */   {
/*  54:124 */     this.alternate = value;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public List<Extension> getExtension()
/*  58:    */   {
/*  59:150 */     if (this.extension == null) {
/*  60:151 */       this.extension = new ArrayList();
/*  61:    */     }
/*  62:153 */     return this.extension;
/*  63:    */   }
/*  64:    */   
/*  65:    */   public String getField()
/*  66:    */   {
/*  67:165 */     return this.field;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public void setField(String value)
/*  71:    */   {
/*  72:177 */     this.field = value;
/*  73:    */   }
/*  74:    */   
/*  75:    */   public String getNormalizationScheme()
/*  76:    */   {
/*  77:189 */     return this.normalizationScheme;
/*  78:    */   }
/*  79:    */   
/*  80:    */   public void setNormalizationScheme(String value)
/*  81:    */   {
/*  82:201 */     this.normalizationScheme = value;
/*  83:    */   }
/*  84:    */   
/*  85:    */   public double getResetValue()
/*  86:    */   {
/*  87:213 */     if (this.resetValue == null) {
/*  88:214 */       return 0.0D;
/*  89:    */     }
/*  90:216 */     return this.resetValue.doubleValue();
/*  91:    */   }
/*  92:    */   
/*  93:    */   public void setResetValue(Double value)
/*  94:    */   {
/*  95:229 */     this.resetValue = value;
/*  96:    */   }
/*  97:    */   
/*  98:    */   public BASELINETESTSTATISTIC getTestStatistic()
/*  99:    */   {
/* 100:241 */     return this.testStatistic;
/* 101:    */   }
/* 102:    */   
/* 103:    */   public void setTestStatistic(BASELINETESTSTATISTIC value)
/* 104:    */   {
/* 105:253 */     this.testStatistic = value;
/* 106:    */   }
/* 107:    */   
/* 108:    */   public String getWeightField()
/* 109:    */   {
/* 110:265 */     return this.weightField;
/* 111:    */   }
/* 112:    */   
/* 113:    */   public void setWeightField(String value)
/* 114:    */   {
/* 115:277 */     this.weightField = value;
/* 116:    */   }
/* 117:    */   
/* 118:    */   public BigInteger getWindowSize()
/* 119:    */   {
/* 120:289 */     if (this.windowSize == null) {
/* 121:290 */       return new BigInteger("0");
/* 122:    */     }
/* 123:292 */     return this.windowSize;
/* 124:    */   }
/* 125:    */   
/* 126:    */   public void setWindowSize(BigInteger value)
/* 127:    */   {
/* 128:305 */     this.windowSize = value;
/* 129:    */   }
/* 130:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.TestDistributions
 * JD-Core Version:    0.7.0.1
 */