/*   1:    */ package weka.core.pmml.jaxbbindings;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.List;
/*   5:    */ import javax.xml.bind.annotation.XmlAccessType;
/*   6:    */ import javax.xml.bind.annotation.XmlAccessorType;
/*   7:    */ import javax.xml.bind.annotation.XmlAttribute;
/*   8:    */ import javax.xml.bind.annotation.XmlElement;
/*   9:    */ import javax.xml.bind.annotation.XmlRootElement;
/*  10:    */ import javax.xml.bind.annotation.XmlType;
/*  11:    */ 
/*  12:    */ @XmlAccessorType(XmlAccessType.FIELD)
/*  13:    */ @XmlType(name="", propOrder={"extension"})
/*  14:    */ @XmlRootElement(name="Con")
/*  15:    */ public class Con1
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlAttribute(required=true)
/*  20:    */   protected String from;
/*  21:    */   @XmlAttribute(required=true)
/*  22:    */   protected double weight;
/*  23:    */   
/*  24:    */   public List<Extension> getExtension()
/*  25:    */   {
/*  26: 83 */     if (this.extension == null) {
/*  27: 84 */       this.extension = new ArrayList();
/*  28:    */     }
/*  29: 86 */     return this.extension;
/*  30:    */   }
/*  31:    */   
/*  32:    */   public String getFrom()
/*  33:    */   {
/*  34: 96 */     return this.from;
/*  35:    */   }
/*  36:    */   
/*  37:    */   public void setFrom(String value)
/*  38:    */   {
/*  39:106 */     this.from = value;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public double getWeight()
/*  43:    */   {
/*  44:114 */     return this.weight;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public void setWeight(double value)
/*  48:    */   {
/*  49:122 */     this.weight = value;
/*  50:    */   }
/*  51:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.Con1
 * JD-Core Version:    0.7.0.1
 */