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
/*  14:    */ @XmlType(name="", propOrder={"extension", "instanceFields", "tableLocator", "inlineTable"})
/*  15:    */ @XmlRootElement(name="TrainingInstances")
/*  16:    */ public class TrainingInstances
/*  17:    */ {
/*  18:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  19:    */   protected List<Extension> extension;
/*  20:    */   @XmlElement(name="InstanceFields", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  21:    */   protected InstanceFields instanceFields;
/*  22:    */   @XmlElement(name="TableLocator", namespace="http://www.dmg.org/PMML-4_1")
/*  23:    */   protected TableLocator tableLocator;
/*  24:    */   @XmlElement(name="InlineTable", namespace="http://www.dmg.org/PMML-4_1")
/*  25:    */   protected InlineTable inlineTable;
/*  26:    */   @XmlAttribute
/*  27:    */   protected BigInteger fieldCount;
/*  28:    */   @XmlAttribute
/*  29:    */   protected Boolean isTransformed;
/*  30:    */   @XmlAttribute
/*  31:    */   protected BigInteger recordCount;
/*  32:    */   
/*  33:    */   public List<Extension> getExtension()
/*  34:    */   {
/*  35: 99 */     if (this.extension == null) {
/*  36:100 */       this.extension = new ArrayList();
/*  37:    */     }
/*  38:102 */     return this.extension;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public InstanceFields getInstanceFields()
/*  42:    */   {
/*  43:114 */     return this.instanceFields;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public void setInstanceFields(InstanceFields value)
/*  47:    */   {
/*  48:126 */     this.instanceFields = value;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public TableLocator getTableLocator()
/*  52:    */   {
/*  53:138 */     return this.tableLocator;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public void setTableLocator(TableLocator value)
/*  57:    */   {
/*  58:150 */     this.tableLocator = value;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public InlineTable getInlineTable()
/*  62:    */   {
/*  63:162 */     return this.inlineTable;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public void setInlineTable(InlineTable value)
/*  67:    */   {
/*  68:174 */     this.inlineTable = value;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public BigInteger getFieldCount()
/*  72:    */   {
/*  73:186 */     return this.fieldCount;
/*  74:    */   }
/*  75:    */   
/*  76:    */   public void setFieldCount(BigInteger value)
/*  77:    */   {
/*  78:198 */     this.fieldCount = value;
/*  79:    */   }
/*  80:    */   
/*  81:    */   public boolean isIsTransformed()
/*  82:    */   {
/*  83:210 */     if (this.isTransformed == null) {
/*  84:211 */       return false;
/*  85:    */     }
/*  86:213 */     return this.isTransformed.booleanValue();
/*  87:    */   }
/*  88:    */   
/*  89:    */   public void setIsTransformed(Boolean value)
/*  90:    */   {
/*  91:226 */     this.isTransformed = value;
/*  92:    */   }
/*  93:    */   
/*  94:    */   public BigInteger getRecordCount()
/*  95:    */   {
/*  96:238 */     return this.recordCount;
/*  97:    */   }
/*  98:    */   
/*  99:    */   public void setRecordCount(BigInteger value)
/* 100:    */   {
/* 101:250 */     this.recordCount = value;
/* 102:    */   }
/* 103:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.TrainingInstances
 * JD-Core Version:    0.7.0.1
 */