/*  1:   */ package weka.core.pmml.jaxbbindings;
/*  2:   */ 
/*  3:   */ import javax.xml.bind.annotation.XmlAccessType;
/*  4:   */ import javax.xml.bind.annotation.XmlAccessorType;
/*  5:   */ import javax.xml.bind.annotation.XmlAttribute;
/*  6:   */ import javax.xml.bind.annotation.XmlRootElement;
/*  7:   */ import javax.xml.bind.annotation.XmlType;
/*  8:   */ import javax.xml.bind.annotation.XmlValue;
/*  9:   */ 
/* 10:   */ @XmlAccessorType(XmlAccessType.FIELD)
/* 11:   */ @XmlType(name="", propOrder={"value"})
/* 12:   */ @XmlRootElement(name="Constant")
/* 13:   */ public class Constant
/* 14:   */ {
/* 15:   */   @XmlValue
/* 16:   */   protected String value;
/* 17:   */   @XmlAttribute
/* 18:   */   protected DATATYPE dataType;
/* 19:   */   
/* 20:   */   public String getValue()
/* 21:   */   {
/* 22:59 */     return this.value;
/* 23:   */   }
/* 24:   */   
/* 25:   */   public void setValue(String value)
/* 26:   */   {
/* 27:71 */     this.value = value;
/* 28:   */   }
/* 29:   */   
/* 30:   */   public DATATYPE getDataType()
/* 31:   */   {
/* 32:83 */     return this.dataType;
/* 33:   */   }
/* 34:   */   
/* 35:   */   public void setDataType(DATATYPE value)
/* 36:   */   {
/* 37:95 */     this.dataType = value;
/* 38:   */   }
/* 39:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.Constant
 * JD-Core Version:    0.7.0.1
 */