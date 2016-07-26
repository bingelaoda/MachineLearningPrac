/*   1:    */ package weka.core.pmml.jaxbbindings;
/*   2:    */ 
/*   3:    */ import java.math.BigInteger;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.List;
/*   6:    */ import javax.xml.bind.annotation.XmlAccessType;
/*   7:    */ import javax.xml.bind.annotation.XmlAccessorType;
/*   8:    */ import javax.xml.bind.annotation.XmlAttribute;
/*   9:    */ import javax.xml.bind.annotation.XmlElement;
/*  10:    */ import javax.xml.bind.annotation.XmlList;
/*  11:    */ import javax.xml.bind.annotation.XmlRootElement;
/*  12:    */ import javax.xml.bind.annotation.XmlType;
/*  13:    */ 
/*  14:    */ @XmlAccessorType(XmlAccessType.FIELD)
/*  15:    */ @XmlType(name="", propOrder={"indices", "intEntries"})
/*  16:    */ @XmlRootElement(name="INT-SparseArray")
/*  17:    */ public class INTSparseArray
/*  18:    */ {
/*  19:    */   @XmlList
/*  20:    */   @XmlElement(name="Indices", namespace="http://www.dmg.org/PMML-4_1", type=Integer.class)
/*  21:    */   protected List<Integer> indices;
/*  22:    */   @XmlList
/*  23:    */   @XmlElement(name="INT-Entries", namespace="http://www.dmg.org/PMML-4_1", type=Integer.class)
/*  24:    */   protected List<Integer> intEntries;
/*  25:    */   @XmlAttribute
/*  26:    */   protected BigInteger defaultValue;
/*  27:    */   @XmlAttribute
/*  28:    */   protected BigInteger n;
/*  29:    */   
/*  30:    */   public List<Integer> getIndices()
/*  31:    */   {
/*  32: 89 */     if (this.indices == null) {
/*  33: 90 */       this.indices = new ArrayList();
/*  34:    */     }
/*  35: 92 */     return this.indices;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public List<Integer> getINTEntries()
/*  39:    */   {
/*  40:118 */     if (this.intEntries == null) {
/*  41:119 */       this.intEntries = new ArrayList();
/*  42:    */     }
/*  43:121 */     return this.intEntries;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public BigInteger getDefaultValue()
/*  47:    */   {
/*  48:133 */     if (this.defaultValue == null) {
/*  49:134 */       return new BigInteger("0");
/*  50:    */     }
/*  51:136 */     return this.defaultValue;
/*  52:    */   }
/*  53:    */   
/*  54:    */   public void setDefaultValue(BigInteger value)
/*  55:    */   {
/*  56:149 */     this.defaultValue = value;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public BigInteger getN()
/*  60:    */   {
/*  61:161 */     return this.n;
/*  62:    */   }
/*  63:    */   
/*  64:    */   public void setN(BigInteger value)
/*  65:    */   {
/*  66:173 */     this.n = value;
/*  67:    */   }
/*  68:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.INTSparseArray
 * JD-Core Version:    0.7.0.1
 */