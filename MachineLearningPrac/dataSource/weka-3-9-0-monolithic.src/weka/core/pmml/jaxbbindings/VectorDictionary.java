/*   1:    */ package weka.core.pmml.jaxbbindings;
/*   2:    */ 
/*   3:    */ import java.math.BigInteger;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.List;
/*   6:    */ import javax.xml.bind.annotation.XmlAccessType;
/*   7:    */ import javax.xml.bind.annotation.XmlAccessorType;
/*   8:    */ import javax.xml.bind.annotation.XmlAttribute;
/*   9:    */ import javax.xml.bind.annotation.XmlElement;
/*  10:    */ import javax.xml.bind.annotation.XmlRootElement;
/*  11:    */ import javax.xml.bind.annotation.XmlType;
/*  12:    */ 
/*  13:    */ @XmlAccessorType(XmlAccessType.FIELD)
/*  14:    */ @XmlType(name="", propOrder={"extension", "vectorFields", "vectorInstance"})
/*  15:    */ @XmlRootElement(name="VectorDictionary")
/*  16:    */ public class VectorDictionary
/*  17:    */ {
/*  18:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  19:    */   protected List<Extension> extension;
/*  20:    */   @XmlElement(name="VectorFields", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  21:    */   protected VectorFields vectorFields;
/*  22:    */   @XmlElement(name="VectorInstance", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  23:    */   protected List<VectorInstance> vectorInstance;
/*  24:    */   @XmlAttribute
/*  25:    */   protected BigInteger numberOfVectors;
/*  26:    */   
/*  27:    */   public List<Extension> getExtension()
/*  28:    */   {
/*  29: 87 */     if (this.extension == null) {
/*  30: 88 */       this.extension = new ArrayList();
/*  31:    */     }
/*  32: 90 */     return this.extension;
/*  33:    */   }
/*  34:    */   
/*  35:    */   public VectorFields getVectorFields()
/*  36:    */   {
/*  37:102 */     return this.vectorFields;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public void setVectorFields(VectorFields value)
/*  41:    */   {
/*  42:114 */     this.vectorFields = value;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public List<VectorInstance> getVectorInstance()
/*  46:    */   {
/*  47:140 */     if (this.vectorInstance == null) {
/*  48:141 */       this.vectorInstance = new ArrayList();
/*  49:    */     }
/*  50:143 */     return this.vectorInstance;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public BigInteger getNumberOfVectors()
/*  54:    */   {
/*  55:155 */     return this.numberOfVectors;
/*  56:    */   }
/*  57:    */   
/*  58:    */   public void setNumberOfVectors(BigInteger value)
/*  59:    */   {
/*  60:167 */     this.numberOfVectors = value;
/*  61:    */   }
/*  62:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.VectorDictionary
 * JD-Core Version:    0.7.0.1
 */