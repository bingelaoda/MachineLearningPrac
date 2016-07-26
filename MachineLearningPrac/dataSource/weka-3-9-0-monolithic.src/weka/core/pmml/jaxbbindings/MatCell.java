/*   1:    */ package weka.core.pmml.jaxbbindings;
/*   2:    */ 
/*   3:    */ import java.math.BigInteger;
/*   4:    */ import javax.xml.bind.annotation.XmlAccessType;
/*   5:    */ import javax.xml.bind.annotation.XmlAccessorType;
/*   6:    */ import javax.xml.bind.annotation.XmlAttribute;
/*   7:    */ import javax.xml.bind.annotation.XmlRootElement;
/*   8:    */ import javax.xml.bind.annotation.XmlType;
/*   9:    */ import javax.xml.bind.annotation.XmlValue;
/*  10:    */ 
/*  11:    */ @XmlAccessorType(XmlAccessType.FIELD)
/*  12:    */ @XmlType(name="", propOrder={"value"})
/*  13:    */ @XmlRootElement(name="MatCell")
/*  14:    */ public class MatCell
/*  15:    */ {
/*  16:    */   @XmlValue
/*  17:    */   protected String value;
/*  18:    */   @XmlAttribute(required=true)
/*  19:    */   protected BigInteger col;
/*  20:    */   @XmlAttribute(required=true)
/*  21:    */   protected BigInteger row;
/*  22:    */   
/*  23:    */   public String getValue()
/*  24:    */   {
/*  25: 63 */     return this.value;
/*  26:    */   }
/*  27:    */   
/*  28:    */   public void setValue(String value)
/*  29:    */   {
/*  30: 75 */     this.value = value;
/*  31:    */   }
/*  32:    */   
/*  33:    */   public BigInteger getCol()
/*  34:    */   {
/*  35: 87 */     return this.col;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public void setCol(BigInteger value)
/*  39:    */   {
/*  40: 99 */     this.col = value;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public BigInteger getRow()
/*  44:    */   {
/*  45:111 */     return this.row;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public void setRow(BigInteger value)
/*  49:    */   {
/*  50:123 */     this.row = value;
/*  51:    */   }
/*  52:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.MatCell
 * JD-Core Version:    0.7.0.1
 */