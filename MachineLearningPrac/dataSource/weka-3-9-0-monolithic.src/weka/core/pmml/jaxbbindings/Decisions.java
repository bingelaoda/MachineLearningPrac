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
/*  13:    */ @XmlType(name="", propOrder={"extension", "decision"})
/*  14:    */ @XmlRootElement(name="Decisions")
/*  15:    */ public class Decisions
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlElement(name="Decision", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  20:    */   protected List<Decision> decision;
/*  21:    */   @XmlAttribute
/*  22:    */   protected String businessProblem;
/*  23:    */   @XmlAttribute
/*  24:    */   protected String description;
/*  25:    */   
/*  26:    */   public List<Extension> getExtension()
/*  27:    */   {
/*  28: 85 */     if (this.extension == null) {
/*  29: 86 */       this.extension = new ArrayList();
/*  30:    */     }
/*  31: 88 */     return this.extension;
/*  32:    */   }
/*  33:    */   
/*  34:    */   public List<Decision> getDecision()
/*  35:    */   {
/*  36:114 */     if (this.decision == null) {
/*  37:115 */       this.decision = new ArrayList();
/*  38:    */     }
/*  39:117 */     return this.decision;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public String getBusinessProblem()
/*  43:    */   {
/*  44:129 */     return this.businessProblem;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public void setBusinessProblem(String value)
/*  48:    */   {
/*  49:141 */     this.businessProblem = value;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public String getDescription()
/*  53:    */   {
/*  54:153 */     return this.description;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public void setDescription(String value)
/*  58:    */   {
/*  59:165 */     this.description = value;
/*  60:    */   }
/*  61:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.Decisions
 * JD-Core Version:    0.7.0.1
 */