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
/*  13:    */ @XmlType(name="", propOrder={"extension", "realSparseArray", "array"})
/*  14:    */ @XmlRootElement(name="VectorInstance")
/*  15:    */ public class VectorInstance
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlElement(name="REAL-SparseArray", namespace="http://www.dmg.org/PMML-4_1")
/*  20:    */   protected REALSparseArray realSparseArray;
/*  21:    */   @XmlElement(name="Array", namespace="http://www.dmg.org/PMML-4_1")
/*  22:    */   protected ArrayType array;
/*  23:    */   @XmlAttribute(required=true)
/*  24:    */   protected String id;
/*  25:    */   
/*  26:    */   public List<Extension> getExtension()
/*  27:    */   {
/*  28: 88 */     if (this.extension == null) {
/*  29: 89 */       this.extension = new ArrayList();
/*  30:    */     }
/*  31: 91 */     return this.extension;
/*  32:    */   }
/*  33:    */   
/*  34:    */   public REALSparseArray getREALSparseArray()
/*  35:    */   {
/*  36:103 */     return this.realSparseArray;
/*  37:    */   }
/*  38:    */   
/*  39:    */   public void setREALSparseArray(REALSparseArray value)
/*  40:    */   {
/*  41:115 */     this.realSparseArray = value;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public ArrayType getArray()
/*  45:    */   {
/*  46:127 */     return this.array;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public void setArray(ArrayType value)
/*  50:    */   {
/*  51:139 */     this.array = value;
/*  52:    */   }
/*  53:    */   
/*  54:    */   public String getId()
/*  55:    */   {
/*  56:151 */     return this.id;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public void setId(String value)
/*  60:    */   {
/*  61:163 */     this.id = value;
/*  62:    */   }
/*  63:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.VectorInstance
 * JD-Core Version:    0.7.0.1
 */