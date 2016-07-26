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
/*  12:    */ @XmlType(name="", propOrder={"array"})
/*  13:    */ @XmlRootElement(name="TimeException")
/*  14:    */ public class TimeException
/*  15:    */ {
/*  16:    */   @XmlElement(name="Array", namespace="http://www.dmg.org/PMML-4_1")
/*  17:    */   protected ArrayType array;
/*  18:    */   @XmlAttribute
/*  19:    */   protected BigInteger count;
/*  20:    */   @XmlAttribute
/*  21:    */   protected TIMEEXCEPTIONTYPE type;
/*  22:    */   
/*  23:    */   public ArrayType getArray()
/*  24:    */   {
/*  25: 66 */     return this.array;
/*  26:    */   }
/*  27:    */   
/*  28:    */   public void setArray(ArrayType value)
/*  29:    */   {
/*  30: 78 */     this.array = value;
/*  31:    */   }
/*  32:    */   
/*  33:    */   public BigInteger getCount()
/*  34:    */   {
/*  35: 90 */     return this.count;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public void setCount(BigInteger value)
/*  39:    */   {
/*  40:102 */     this.count = value;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public TIMEEXCEPTIONTYPE getType()
/*  44:    */   {
/*  45:114 */     return this.type;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public void setType(TIMEEXCEPTIONTYPE value)
/*  49:    */   {
/*  50:126 */     this.type = value;
/*  51:    */   }
/*  52:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.TimeException
 * JD-Core Version:    0.7.0.1
 */