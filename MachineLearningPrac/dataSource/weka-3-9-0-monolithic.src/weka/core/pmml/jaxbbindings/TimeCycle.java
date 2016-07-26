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
/*  13:    */ @XmlRootElement(name="TimeCycle")
/*  14:    */ public class TimeCycle
/*  15:    */ {
/*  16:    */   @XmlElement(name="Array", namespace="http://www.dmg.org/PMML-4_1")
/*  17:    */   protected ArrayType array;
/*  18:    */   @XmlAttribute
/*  19:    */   protected String displayName;
/*  20:    */   @XmlAttribute
/*  21:    */   protected BigInteger length;
/*  22:    */   @XmlAttribute
/*  23:    */   protected VALIDTIMESPEC type;
/*  24:    */   
/*  25:    */   public ArrayType getArray()
/*  26:    */   {
/*  27: 69 */     return this.array;
/*  28:    */   }
/*  29:    */   
/*  30:    */   public void setArray(ArrayType value)
/*  31:    */   {
/*  32: 81 */     this.array = value;
/*  33:    */   }
/*  34:    */   
/*  35:    */   public String getDisplayName()
/*  36:    */   {
/*  37: 93 */     return this.displayName;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public void setDisplayName(String value)
/*  41:    */   {
/*  42:105 */     this.displayName = value;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public BigInteger getLength()
/*  46:    */   {
/*  47:117 */     return this.length;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public void setLength(BigInteger value)
/*  51:    */   {
/*  52:129 */     this.length = value;
/*  53:    */   }
/*  54:    */   
/*  55:    */   public VALIDTIMESPEC getType()
/*  56:    */   {
/*  57:141 */     return this.type;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public void setType(VALIDTIMESPEC value)
/*  61:    */   {
/*  62:153 */     this.type = value;
/*  63:    */   }
/*  64:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.TimeCycle
 * JD-Core Version:    0.7.0.1
 */