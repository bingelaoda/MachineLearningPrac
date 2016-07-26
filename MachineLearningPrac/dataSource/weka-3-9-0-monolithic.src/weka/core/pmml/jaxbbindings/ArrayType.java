/*   1:    */ package weka.core.pmml.jaxbbindings;
/*   2:    */ 
/*   3:    */ import java.math.BigInteger;
/*   4:    */ import javax.xml.bind.annotation.XmlAccessType;
/*   5:    */ import javax.xml.bind.annotation.XmlAccessorType;
/*   6:    */ import javax.xml.bind.annotation.XmlAttribute;
/*   7:    */ import javax.xml.bind.annotation.XmlType;
/*   8:    */ import javax.xml.bind.annotation.XmlValue;
/*   9:    */ 
/*  10:    */ @XmlAccessorType(XmlAccessType.FIELD)
/*  11:    */ @XmlType(name="ArrayType", propOrder={"content"})
/*  12:    */ public class ArrayType
/*  13:    */ {
/*  14:    */   @XmlValue
/*  15:    */   protected String content;
/*  16:    */   @XmlAttribute
/*  17:    */   protected BigInteger n;
/*  18:    */   @XmlAttribute(required=true)
/*  19:    */   protected String type;
/*  20:    */   
/*  21:    */   public String getContent()
/*  22:    */   {
/*  23: 67 */     return this.content;
/*  24:    */   }
/*  25:    */   
/*  26:    */   public void setContent(String value)
/*  27:    */   {
/*  28: 79 */     this.content = value;
/*  29:    */   }
/*  30:    */   
/*  31:    */   public BigInteger getN()
/*  32:    */   {
/*  33: 91 */     return this.n;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public void setN(BigInteger value)
/*  37:    */   {
/*  38:103 */     this.n = value;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public String getType()
/*  42:    */   {
/*  43:115 */     return this.type;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public void setType(String value)
/*  47:    */   {
/*  48:127 */     this.type = value;
/*  49:    */   }
/*  50:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.ArrayType
 * JD-Core Version:    0.7.0.1
 */