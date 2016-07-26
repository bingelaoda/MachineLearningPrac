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
/*  14:    */ @XmlRootElement(name="PCovCell")
/*  15:    */ public class PCovCell
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlAttribute(required=true)
/*  20:    */   protected String pCol;
/*  21:    */   @XmlAttribute(required=true)
/*  22:    */   protected String pRow;
/*  23:    */   @XmlAttribute
/*  24:    */   protected String tCol;
/*  25:    */   @XmlAttribute
/*  26:    */   protected String tRow;
/*  27:    */   @XmlAttribute
/*  28:    */   protected String targetCategory;
/*  29:    */   @XmlAttribute(required=true)
/*  30:    */   protected double value;
/*  31:    */   
/*  32:    */   public List<Extension> getExtension()
/*  33:    */   {
/*  34: 93 */     if (this.extension == null) {
/*  35: 94 */       this.extension = new ArrayList();
/*  36:    */     }
/*  37: 96 */     return this.extension;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public String getPCol()
/*  41:    */   {
/*  42:108 */     return this.pCol;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public void setPCol(String value)
/*  46:    */   {
/*  47:120 */     this.pCol = value;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public String getPRow()
/*  51:    */   {
/*  52:132 */     return this.pRow;
/*  53:    */   }
/*  54:    */   
/*  55:    */   public void setPRow(String value)
/*  56:    */   {
/*  57:144 */     this.pRow = value;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public String getTCol()
/*  61:    */   {
/*  62:156 */     return this.tCol;
/*  63:    */   }
/*  64:    */   
/*  65:    */   public void setTCol(String value)
/*  66:    */   {
/*  67:168 */     this.tCol = value;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public String getTRow()
/*  71:    */   {
/*  72:180 */     return this.tRow;
/*  73:    */   }
/*  74:    */   
/*  75:    */   public void setTRow(String value)
/*  76:    */   {
/*  77:192 */     this.tRow = value;
/*  78:    */   }
/*  79:    */   
/*  80:    */   public String getTargetCategory()
/*  81:    */   {
/*  82:204 */     return this.targetCategory;
/*  83:    */   }
/*  84:    */   
/*  85:    */   public void setTargetCategory(String value)
/*  86:    */   {
/*  87:216 */     this.targetCategory = value;
/*  88:    */   }
/*  89:    */   
/*  90:    */   public double getValue()
/*  91:    */   {
/*  92:224 */     return this.value;
/*  93:    */   }
/*  94:    */   
/*  95:    */   public void setValue(double value)
/*  96:    */   {
/*  97:232 */     this.value = value;
/*  98:    */   }
/*  99:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.PCovCell
 * JD-Core Version:    0.7.0.1
 */