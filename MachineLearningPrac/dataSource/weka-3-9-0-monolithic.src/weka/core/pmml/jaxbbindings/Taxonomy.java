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
/*  13:    */ @XmlType(name="", propOrder={"extension", "childParent"})
/*  14:    */ @XmlRootElement(name="Taxonomy")
/*  15:    */ public class Taxonomy
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlElement(name="ChildParent", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  20:    */   protected List<ChildParent> childParent;
/*  21:    */   @XmlAttribute(required=true)
/*  22:    */   protected String name;
/*  23:    */   
/*  24:    */   public List<Extension> getExtension()
/*  25:    */   {
/*  26: 82 */     if (this.extension == null) {
/*  27: 83 */       this.extension = new ArrayList();
/*  28:    */     }
/*  29: 85 */     return this.extension;
/*  30:    */   }
/*  31:    */   
/*  32:    */   public List<ChildParent> getChildParent()
/*  33:    */   {
/*  34:111 */     if (this.childParent == null) {
/*  35:112 */       this.childParent = new ArrayList();
/*  36:    */     }
/*  37:114 */     return this.childParent;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public String getName()
/*  41:    */   {
/*  42:126 */     return this.name;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public void setName(String value)
/*  46:    */   {
/*  47:138 */     this.name = value;
/*  48:    */   }
/*  49:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.Taxonomy
 * JD-Core Version:    0.7.0.1
 */