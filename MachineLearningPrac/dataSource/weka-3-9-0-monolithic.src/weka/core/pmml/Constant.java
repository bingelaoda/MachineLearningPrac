/*   1:    */ package weka.core.pmml;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import javax.xml.parsers.DocumentBuilder;
/*   7:    */ import javax.xml.parsers.DocumentBuilderFactory;
/*   8:    */ import org.w3c.dom.Document;
/*   9:    */ import org.w3c.dom.Element;
/*  10:    */ import org.w3c.dom.Node;
/*  11:    */ import org.w3c.dom.NodeList;
/*  12:    */ import weka.core.Attribute;
/*  13:    */ 
/*  14:    */ public class Constant
/*  15:    */   extends Expression
/*  16:    */ {
/*  17:    */   private static final long serialVersionUID = -304829687822452424L;
/*  18: 45 */   protected String m_categoricalConst = null;
/*  19: 46 */   protected double m_continuousConst = (0.0D / 0.0D);
/*  20:    */   
/*  21:    */   public Constant(Element constant, FieldMetaInfo.Optype opType, ArrayList<Attribute> fieldDefs)
/*  22:    */     throws Exception
/*  23:    */   {
/*  24: 60 */     super(opType, fieldDefs);
/*  25:    */     
/*  26: 62 */     NodeList constL = constant.getChildNodes();
/*  27: 63 */     String c = constL.item(0).getNodeValue();
/*  28: 65 */     if ((this.m_opType == FieldMetaInfo.Optype.CATEGORICAL) || (this.m_opType == FieldMetaInfo.Optype.ORDINAL)) {
/*  29: 67 */       this.m_categoricalConst = c;
/*  30:    */     } else {
/*  31:    */       try
/*  32:    */       {
/*  33: 70 */         this.m_continuousConst = Double.parseDouble(c);
/*  34:    */       }
/*  35:    */       catch (IllegalArgumentException ex)
/*  36:    */       {
/*  37: 72 */         throw new Exception("[Constant] Unable to parse continuous constant: " + c);
/*  38:    */       }
/*  39:    */     }
/*  40:    */   }
/*  41:    */   
/*  42:    */   protected Attribute getOutputDef()
/*  43:    */   {
/*  44: 87 */     if (this.m_opType == FieldMetaInfo.Optype.CONTINUOUS) {
/*  45: 88 */       return new Attribute("Constant: " + this.m_continuousConst);
/*  46:    */     }
/*  47: 91 */     ArrayList<String> nom = new ArrayList();
/*  48: 92 */     nom.add(this.m_categoricalConst);
/*  49: 93 */     return new Attribute("Constant: " + this.m_categoricalConst, nom);
/*  50:    */   }
/*  51:    */   
/*  52:    */   public double getResult(double[] incoming)
/*  53:    */   {
/*  54:106 */     if (this.m_opType == FieldMetaInfo.Optype.CONTINUOUS) {
/*  55:107 */       return this.m_continuousConst;
/*  56:    */     }
/*  57:109 */     return 0.0D;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public String getResultCategorical(double[] incoming)
/*  61:    */     throws Exception
/*  62:    */   {
/*  63:123 */     if (this.m_opType == FieldMetaInfo.Optype.CONTINUOUS) {
/*  64:124 */       throw new IllegalArgumentException("[Constant] Cant't return result as categorical/ordinal as optype is continuous!");
/*  65:    */     }
/*  66:127 */     return this.m_categoricalConst;
/*  67:    */   }
/*  68:    */   
/*  69:    */   public static void main(String[] args)
/*  70:    */   {
/*  71:    */     try
/*  72:    */     {
/*  73:132 */       File f = new File(args[0]);
/*  74:133 */       DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
/*  75:134 */       DocumentBuilder db = dbf.newDocumentBuilder();
/*  76:135 */       Document doc = db.parse(f);
/*  77:136 */       doc.getDocumentElement().normalize();
/*  78:137 */       NodeList constL = doc.getElementsByTagName("Constant");
/*  79:138 */       Node c = constL.item(0);
/*  80:140 */       if (c.getNodeType() == 1)
/*  81:    */       {
/*  82:141 */         Constant constC = new Constant((Element)c, FieldMetaInfo.Optype.CONTINUOUS, null);
/*  83:142 */         System.err.println("Value of first constant: " + constC.getResult(null));
/*  84:    */       }
/*  85:    */     }
/*  86:    */     catch (Exception ex)
/*  87:    */     {
/*  88:145 */       ex.printStackTrace();
/*  89:    */     }
/*  90:    */   }
/*  91:    */   
/*  92:    */   public String toString(String pad)
/*  93:    */   {
/*  94:150 */     return pad + "Constant: " + (this.m_categoricalConst != null ? this.m_categoricalConst : new StringBuilder().append("").append(this.m_continuousConst).toString());
/*  95:    */   }
/*  96:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.Constant
 * JD-Core Version:    0.7.0.1
 */