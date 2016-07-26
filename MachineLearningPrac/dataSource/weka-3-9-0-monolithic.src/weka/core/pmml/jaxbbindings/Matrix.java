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
/*  14:    */ @XmlType(name="", propOrder={"numarray", "matCell"})
/*  15:    */ @XmlRootElement(name="Matrix")
/*  16:    */ public class Matrix
/*  17:    */ {
/*  18:    */   @XmlElement(name="Array", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  19:    */   protected List<ArrayType> numarray;
/*  20:    */   @XmlElement(name="MatCell", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  21:    */   protected List<MatCell> matCell;
/*  22:    */   @XmlAttribute
/*  23:    */   protected Double diagDefault;
/*  24:    */   @XmlAttribute
/*  25:    */   protected String kind;
/*  26:    */   @XmlAttribute
/*  27:    */   protected BigInteger nbCols;
/*  28:    */   @XmlAttribute
/*  29:    */   protected BigInteger nbRows;
/*  30:    */   @XmlAttribute
/*  31:    */   protected Double offDiagDefault;
/*  32:    */   
/*  33:    */   public List<ArrayType> getNUMARRAY()
/*  34:    */   {
/*  35:103 */     if (this.numarray == null) {
/*  36:104 */       this.numarray = new ArrayList();
/*  37:    */     }
/*  38:106 */     return this.numarray;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public List<MatCell> getMatCell()
/*  42:    */   {
/*  43:132 */     if (this.matCell == null) {
/*  44:133 */       this.matCell = new ArrayList();
/*  45:    */     }
/*  46:135 */     return this.matCell;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public Double getDiagDefault()
/*  50:    */   {
/*  51:147 */     return this.diagDefault;
/*  52:    */   }
/*  53:    */   
/*  54:    */   public void setDiagDefault(Double value)
/*  55:    */   {
/*  56:159 */     this.diagDefault = value;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public String getKind()
/*  60:    */   {
/*  61:171 */     if (this.kind == null) {
/*  62:172 */       return "any";
/*  63:    */     }
/*  64:174 */     return this.kind;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public void setKind(String value)
/*  68:    */   {
/*  69:187 */     this.kind = value;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public BigInteger getNbCols()
/*  73:    */   {
/*  74:199 */     return this.nbCols;
/*  75:    */   }
/*  76:    */   
/*  77:    */   public void setNbCols(BigInteger value)
/*  78:    */   {
/*  79:211 */     this.nbCols = value;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public BigInteger getNbRows()
/*  83:    */   {
/*  84:223 */     return this.nbRows;
/*  85:    */   }
/*  86:    */   
/*  87:    */   public void setNbRows(BigInteger value)
/*  88:    */   {
/*  89:235 */     this.nbRows = value;
/*  90:    */   }
/*  91:    */   
/*  92:    */   public Double getOffDiagDefault()
/*  93:    */   {
/*  94:247 */     return this.offDiagDefault;
/*  95:    */   }
/*  96:    */   
/*  97:    */   public void setOffDiagDefault(Double value)
/*  98:    */   {
/*  99:259 */     this.offDiagDefault = value;
/* 100:    */   }
/* 101:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.Matrix
 * JD-Core Version:    0.7.0.1
 */