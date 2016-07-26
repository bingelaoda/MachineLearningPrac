/*   1:    */ package weka.core.pmml.jaxbbindings;
/*   2:    */ 
/*   3:    */ import java.math.BigDecimal;
/*   4:    */ import java.math.BigInteger;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import java.util.List;
/*   7:    */ import javax.xml.bind.annotation.XmlAccessType;
/*   8:    */ import javax.xml.bind.annotation.XmlAccessorType;
/*   9:    */ import javax.xml.bind.annotation.XmlAttribute;
/*  10:    */ import javax.xml.bind.annotation.XmlElement;
/*  11:    */ import javax.xml.bind.annotation.XmlRootElement;
/*  12:    */ import javax.xml.bind.annotation.XmlType;
/*  13:    */ 
/*  14:    */ @XmlAccessorType(XmlAccessType.FIELD)
/*  15:    */ @XmlType(name="", propOrder={"extension", "itemRef"})
/*  16:    */ @XmlRootElement(name="Itemset")
/*  17:    */ public class Itemset
/*  18:    */ {
/*  19:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  20:    */   protected List<Extension> extension;
/*  21:    */   @XmlElement(name="ItemRef", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  22:    */   protected List<ItemRef> itemRef;
/*  23:    */   @XmlAttribute(required=true)
/*  24:    */   protected String id;
/*  25:    */   @XmlAttribute
/*  26:    */   protected BigInteger numberOfItems;
/*  27:    */   @XmlAttribute
/*  28:    */   protected BigDecimal support;
/*  29:    */   
/*  30:    */   public List<Extension> getExtension()
/*  31:    */   {
/*  32: 90 */     if (this.extension == null) {
/*  33: 91 */       this.extension = new ArrayList();
/*  34:    */     }
/*  35: 93 */     return this.extension;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public List<ItemRef> getItemRef()
/*  39:    */   {
/*  40:119 */     if (this.itemRef == null) {
/*  41:120 */       this.itemRef = new ArrayList();
/*  42:    */     }
/*  43:122 */     return this.itemRef;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public String getId()
/*  47:    */   {
/*  48:134 */     return this.id;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public void setId(String value)
/*  52:    */   {
/*  53:146 */     this.id = value;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public BigInteger getNumberOfItems()
/*  57:    */   {
/*  58:158 */     return this.numberOfItems;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public void setNumberOfItems(BigInteger value)
/*  62:    */   {
/*  63:170 */     this.numberOfItems = value;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public BigDecimal getSupport()
/*  67:    */   {
/*  68:182 */     return this.support;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public void setSupport(BigDecimal value)
/*  72:    */   {
/*  73:194 */     this.support = value;
/*  74:    */   }
/*  75:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.Itemset
 * JD-Core Version:    0.7.0.1
 */