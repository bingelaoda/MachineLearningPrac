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
/*  14:    */ @XmlRootElement(name="RuleSelectionMethod")
/*  15:    */ public class RuleSelectionMethod
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlAttribute(required=true)
/*  20:    */   protected String criterion;
/*  21:    */   
/*  22:    */   public List<Extension> getExtension()
/*  23:    */   {
/*  24: 86 */     if (this.extension == null) {
/*  25: 87 */       this.extension = new ArrayList();
/*  26:    */     }
/*  27: 89 */     return this.extension;
/*  28:    */   }
/*  29:    */   
/*  30:    */   public String getCriterion()
/*  31:    */   {
/*  32:101 */     return this.criterion;
/*  33:    */   }
/*  34:    */   
/*  35:    */   public void setCriterion(String value)
/*  36:    */   {
/*  37:113 */     this.criterion = value;
/*  38:    */   }
/*  39:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.RuleSelectionMethod
 * JD-Core Version:    0.7.0.1
 */