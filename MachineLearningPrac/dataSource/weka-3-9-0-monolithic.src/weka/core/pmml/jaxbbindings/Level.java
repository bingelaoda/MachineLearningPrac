/*  1:   */ package weka.core.pmml.jaxbbindings;
/*  2:   */ 
/*  3:   */ import javax.xml.bind.annotation.XmlAccessType;
/*  4:   */ import javax.xml.bind.annotation.XmlAccessorType;
/*  5:   */ import javax.xml.bind.annotation.XmlAttribute;
/*  6:   */ import javax.xml.bind.annotation.XmlRootElement;
/*  7:   */ import javax.xml.bind.annotation.XmlType;
/*  8:   */ 
/*  9:   */ @XmlAccessorType(XmlAccessType.FIELD)
/* 10:   */ @XmlType(name="")
/* 11:   */ @XmlRootElement(name="Level")
/* 12:   */ public class Level
/* 13:   */ {
/* 14:   */   @XmlAttribute
/* 15:   */   protected Double alpha;
/* 16:   */   @XmlAttribute
/* 17:   */   protected Double smoothedValue;
/* 18:   */   
/* 19:   */   public Double getAlpha()
/* 20:   */   {
/* 21:57 */     return this.alpha;
/* 22:   */   }
/* 23:   */   
/* 24:   */   public void setAlpha(Double value)
/* 25:   */   {
/* 26:69 */     this.alpha = value;
/* 27:   */   }
/* 28:   */   
/* 29:   */   public Double getSmoothedValue()
/* 30:   */   {
/* 31:81 */     return this.smoothedValue;
/* 32:   */   }
/* 33:   */   
/* 34:   */   public void setSmoothedValue(Double value)
/* 35:   */   {
/* 36:93 */     this.smoothedValue = value;
/* 37:   */   }
/* 38:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.Level
 * JD-Core Version:    0.7.0.1
 */