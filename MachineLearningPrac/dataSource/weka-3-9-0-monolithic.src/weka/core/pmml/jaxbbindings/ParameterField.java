/*   1:    */ package weka.core.pmml.jaxbbindings;
/*   2:    */ 
/*   3:    */ import javax.xml.bind.annotation.XmlAccessType;
/*   4:    */ import javax.xml.bind.annotation.XmlAccessorType;
/*   5:    */ import javax.xml.bind.annotation.XmlAttribute;
/*   6:    */ import javax.xml.bind.annotation.XmlRootElement;
/*   7:    */ import javax.xml.bind.annotation.XmlType;
/*   8:    */ 
/*   9:    */ @XmlAccessorType(XmlAccessType.FIELD)
/*  10:    */ @XmlType(name="")
/*  11:    */ @XmlRootElement(name="ParameterField")
/*  12:    */ public class ParameterField
/*  13:    */ {
/*  14:    */   @XmlAttribute
/*  15:    */   protected DATATYPE dataType;
/*  16:    */   @XmlAttribute(required=true)
/*  17:    */   protected String name;
/*  18:    */   @XmlAttribute
/*  19:    */   protected OPTYPE optype;
/*  20:    */   
/*  21:    */   public DATATYPE getDataType()
/*  22:    */   {
/*  23: 60 */     return this.dataType;
/*  24:    */   }
/*  25:    */   
/*  26:    */   public void setDataType(DATATYPE value)
/*  27:    */   {
/*  28: 72 */     this.dataType = value;
/*  29:    */   }
/*  30:    */   
/*  31:    */   public String getName()
/*  32:    */   {
/*  33: 84 */     return this.name;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public void setName(String value)
/*  37:    */   {
/*  38: 96 */     this.name = value;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public OPTYPE getOptype()
/*  42:    */   {
/*  43:108 */     return this.optype;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public void setOptype(OPTYPE value)
/*  47:    */   {
/*  48:120 */     this.optype = value;
/*  49:    */   }
/*  50:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.ParameterField
 * JD-Core Version:    0.7.0.1
 */