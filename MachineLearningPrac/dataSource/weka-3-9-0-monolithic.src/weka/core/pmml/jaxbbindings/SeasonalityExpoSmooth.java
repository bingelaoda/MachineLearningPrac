/*   1:    */ package weka.core.pmml.jaxbbindings;
/*   2:    */ 
/*   3:    */ import java.math.BigInteger;
/*   4:    */ import javax.xml.bind.annotation.XmlAccessType;
/*   5:    */ import javax.xml.bind.annotation.XmlAccessorType;
/*   6:    */ import javax.xml.bind.annotation.XmlAttribute;
/*   7:    */ import javax.xml.bind.annotation.XmlElement;
/*   8:    */ import javax.xml.bind.annotation.XmlRootElement;
/*   9:    */ import javax.xml.bind.annotation.XmlType;
/*  10:    */ import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
/*  11:    */ import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
/*  12:    */ 
/*  13:    */ @XmlAccessorType(XmlAccessType.FIELD)
/*  14:    */ @XmlType(name="", propOrder={"array"})
/*  15:    */ @XmlRootElement(name="Seasonality_ExpoSmooth")
/*  16:    */ public class SeasonalityExpoSmooth
/*  17:    */ {
/*  18:    */   @XmlElement(name="Array", namespace="http://www.dmg.org/PMML-4_1")
/*  19:    */   protected ArrayType array;
/*  20:    */   @XmlAttribute
/*  21:    */   protected Double delta;
/*  22:    */   @XmlAttribute(required=true)
/*  23:    */   protected BigInteger period;
/*  24:    */   @XmlAttribute
/*  25:    */   protected BigInteger phase;
/*  26:    */   @XmlAttribute(required=true)
/*  27:    */   @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
/*  28:    */   protected String type;
/*  29:    */   @XmlAttribute
/*  30:    */   protected String unit;
/*  31:    */   
/*  32:    */   public ArrayType getArray()
/*  33:    */   {
/*  34: 85 */     return this.array;
/*  35:    */   }
/*  36:    */   
/*  37:    */   public void setArray(ArrayType value)
/*  38:    */   {
/*  39: 97 */     this.array = value;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public Double getDelta()
/*  43:    */   {
/*  44:109 */     return this.delta;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public void setDelta(Double value)
/*  48:    */   {
/*  49:121 */     this.delta = value;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public BigInteger getPeriod()
/*  53:    */   {
/*  54:133 */     return this.period;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public void setPeriod(BigInteger value)
/*  58:    */   {
/*  59:145 */     this.period = value;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public BigInteger getPhase()
/*  63:    */   {
/*  64:157 */     return this.phase;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public void setPhase(BigInteger value)
/*  68:    */   {
/*  69:169 */     this.phase = value;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public String getType()
/*  73:    */   {
/*  74:181 */     return this.type;
/*  75:    */   }
/*  76:    */   
/*  77:    */   public void setType(String value)
/*  78:    */   {
/*  79:193 */     this.type = value;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public String getUnit()
/*  83:    */   {
/*  84:205 */     return this.unit;
/*  85:    */   }
/*  86:    */   
/*  87:    */   public void setUnit(String value)
/*  88:    */   {
/*  89:217 */     this.unit = value;
/*  90:    */   }
/*  91:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.SeasonalityExpoSmooth
 * JD-Core Version:    0.7.0.1
 */