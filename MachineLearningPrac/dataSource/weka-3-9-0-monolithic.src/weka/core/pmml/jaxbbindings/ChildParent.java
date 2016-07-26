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
/*  13:    */ @XmlType(name="", propOrder={"extension", "tableLocator", "inlineTable"})
/*  14:    */ @XmlRootElement(name="ChildParent")
/*  15:    */ public class ChildParent
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlElement(name="TableLocator", namespace="http://www.dmg.org/PMML-4_1")
/*  20:    */   protected TableLocator tableLocator;
/*  21:    */   @XmlElement(name="InlineTable", namespace="http://www.dmg.org/PMML-4_1")
/*  22:    */   protected InlineTable inlineTable;
/*  23:    */   @XmlAttribute(required=true)
/*  24:    */   protected String childField;
/*  25:    */   @XmlAttribute
/*  26:    */   protected String isRecursive;
/*  27:    */   @XmlAttribute(required=true)
/*  28:    */   protected String parentField;
/*  29:    */   @XmlAttribute
/*  30:    */   protected String parentLevelField;
/*  31:    */   
/*  32:    */   public List<Extension> getExtension()
/*  33:    */   {
/*  34:104 */     if (this.extension == null) {
/*  35:105 */       this.extension = new ArrayList();
/*  36:    */     }
/*  37:107 */     return this.extension;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public TableLocator getTableLocator()
/*  41:    */   {
/*  42:119 */     return this.tableLocator;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public void setTableLocator(TableLocator value)
/*  46:    */   {
/*  47:131 */     this.tableLocator = value;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public InlineTable getInlineTable()
/*  51:    */   {
/*  52:143 */     return this.inlineTable;
/*  53:    */   }
/*  54:    */   
/*  55:    */   public void setInlineTable(InlineTable value)
/*  56:    */   {
/*  57:155 */     this.inlineTable = value;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public String getChildField()
/*  61:    */   {
/*  62:167 */     return this.childField;
/*  63:    */   }
/*  64:    */   
/*  65:    */   public void setChildField(String value)
/*  66:    */   {
/*  67:179 */     this.childField = value;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public String getIsRecursive()
/*  71:    */   {
/*  72:191 */     if (this.isRecursive == null) {
/*  73:192 */       return "no";
/*  74:    */     }
/*  75:194 */     return this.isRecursive;
/*  76:    */   }
/*  77:    */   
/*  78:    */   public void setIsRecursive(String value)
/*  79:    */   {
/*  80:207 */     this.isRecursive = value;
/*  81:    */   }
/*  82:    */   
/*  83:    */   public String getParentField()
/*  84:    */   {
/*  85:219 */     return this.parentField;
/*  86:    */   }
/*  87:    */   
/*  88:    */   public void setParentField(String value)
/*  89:    */   {
/*  90:231 */     this.parentField = value;
/*  91:    */   }
/*  92:    */   
/*  93:    */   public String getParentLevelField()
/*  94:    */   {
/*  95:243 */     return this.parentLevelField;
/*  96:    */   }
/*  97:    */   
/*  98:    */   public void setParentLevelField(String value)
/*  99:    */   {
/* 100:255 */     this.parentLevelField = value;
/* 101:    */   }
/* 102:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.ChildParent
 * JD-Core Version:    0.7.0.1
 */