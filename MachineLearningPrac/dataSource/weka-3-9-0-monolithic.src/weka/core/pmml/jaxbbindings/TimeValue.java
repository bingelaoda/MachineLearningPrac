/*   1:    */ package weka.core.pmml.jaxbbindings;
/*   2:    */ 
/*   3:    */ import java.math.BigInteger;
/*   4:    */ import javax.xml.bind.annotation.XmlAccessType;
/*   5:    */ import javax.xml.bind.annotation.XmlAccessorType;
/*   6:    */ import javax.xml.bind.annotation.XmlAttribute;
/*   7:    */ import javax.xml.bind.annotation.XmlElement;
/*   8:    */ import javax.xml.bind.annotation.XmlRootElement;
/*   9:    */ import javax.xml.bind.annotation.XmlType;
/*  10:    */ 
/*  11:    */ @XmlAccessorType(XmlAccessType.FIELD)
/*  12:    */ @XmlType(name="", propOrder={"timestamp"})
/*  13:    */ @XmlRootElement(name="TimeValue")
/*  14:    */ public class TimeValue
/*  15:    */ {
/*  16:    */   @XmlElement(name="Timestamp", namespace="http://www.dmg.org/PMML-4_1")
/*  17:    */   protected Timestamp timestamp;
/*  18:    */   @XmlAttribute
/*  19:    */   protected BigInteger index;
/*  20:    */   @XmlAttribute
/*  21:    */   protected Double standardError;
/*  22:    */   @XmlAttribute
/*  23:    */   protected Double time;
/*  24:    */   @XmlAttribute(required=true)
/*  25:    */   protected double value;
/*  26:    */   
/*  27:    */   public Timestamp getTimestamp()
/*  28:    */   {
/*  29: 72 */     return this.timestamp;
/*  30:    */   }
/*  31:    */   
/*  32:    */   public void setTimestamp(Timestamp value)
/*  33:    */   {
/*  34: 84 */     this.timestamp = value;
/*  35:    */   }
/*  36:    */   
/*  37:    */   public BigInteger getIndex()
/*  38:    */   {
/*  39: 96 */     return this.index;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public void setIndex(BigInteger value)
/*  43:    */   {
/*  44:108 */     this.index = value;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public Double getStandardError()
/*  48:    */   {
/*  49:120 */     return this.standardError;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public void setStandardError(Double value)
/*  53:    */   {
/*  54:132 */     this.standardError = value;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public Double getTime()
/*  58:    */   {
/*  59:144 */     return this.time;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public void setTime(Double value)
/*  63:    */   {
/*  64:156 */     this.time = value;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public double getValue()
/*  68:    */   {
/*  69:164 */     return this.value;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public void setValue(double value)
/*  73:    */   {
/*  74:172 */     this.value = value;
/*  75:    */   }
/*  76:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.TimeValue
 * JD-Core Version:    0.7.0.1
 */