/*   1:    */ package jsyntaxpane.actions;
/*   2:    */ 
/*   3:    */ import java.awt.Component;
/*   4:    */ import java.awt.event.ActionEvent;
/*   5:    */ import java.io.IOException;
/*   6:    */ import java.io.StringReader;
/*   7:    */ import java.io.StringWriter;
/*   8:    */ import javax.swing.JOptionPane;
/*   9:    */ import javax.swing.SwingUtilities;
/*  10:    */ import javax.swing.text.JTextComponent;
/*  11:    */ import javax.xml.parsers.DocumentBuilder;
/*  12:    */ import javax.xml.parsers.DocumentBuilderFactory;
/*  13:    */ import javax.xml.parsers.ParserConfigurationException;
/*  14:    */ import javax.xml.transform.Transformer;
/*  15:    */ import javax.xml.transform.TransformerConfigurationException;
/*  16:    */ import javax.xml.transform.TransformerException;
/*  17:    */ import javax.xml.transform.TransformerFactory;
/*  18:    */ import javax.xml.transform.dom.DOMSource;
/*  19:    */ import javax.xml.transform.stream.StreamResult;
/*  20:    */ import jsyntaxpane.SyntaxDocument;
/*  21:    */ import org.w3c.dom.Document;
/*  22:    */ import org.xml.sax.InputSource;
/*  23:    */ import org.xml.sax.SAXException;
/*  24:    */ import org.xml.sax.SAXParseException;
/*  25:    */ 
/*  26:    */ public class XmlPrettifyAction
/*  27:    */   extends DefaultSyntaxAction
/*  28:    */ {
/*  29:    */   static Transformer transformer;
/*  30:    */   static DocumentBuilderFactory docBuilderFactory;
/*  31:    */   static DocumentBuilder docBuilder;
/*  32:    */   
/*  33:    */   public XmlPrettifyAction()
/*  34:    */   {
/*  35: 47 */     super("XML_PRETTIFY");
/*  36:    */   }
/*  37:    */   
/*  38:    */   public void actionPerformed(ActionEvent e)
/*  39:    */   {
/*  40: 52 */     if (transformer == null) {
/*  41: 53 */       return;
/*  42:    */     }
/*  43: 55 */     JTextComponent target = getTextComponent(e);
/*  44:    */     try
/*  45:    */     {
/*  46: 57 */       SyntaxDocument sdoc = ActionUtils.getSyntaxDocument(target);
/*  47: 58 */       StringWriter out = new StringWriter(sdoc.getLength());
/*  48: 59 */       StringReader reader = new StringReader(target.getText());
/*  49: 60 */       InputSource src = new InputSource(reader);
/*  50: 61 */       Document doc = getDocBuilder().parse(src);
/*  51:    */       
/*  52: 63 */       getTransformer().transform(new DOMSource(doc), new StreamResult(out));
/*  53: 64 */       target.setText(out.toString());
/*  54:    */     }
/*  55:    */     catch (SAXParseException ex)
/*  56:    */     {
/*  57: 66 */       showErrorMessage(target, String.format("XML error: %s\nat(%d, %d)", new Object[] { ex.getMessage(), Integer.valueOf(ex.getLineNumber()), Integer.valueOf(ex.getColumnNumber()) }));
/*  58:    */       
/*  59:    */ 
/*  60: 69 */       ActionUtils.setCaretPosition(target, ex.getLineNumber(), ex.getColumnNumber() - 1);
/*  61:    */     }
/*  62:    */     catch (TransformerException ex)
/*  63:    */     {
/*  64: 71 */       showErrorMessage(target, ex.getMessageAndLocation());
/*  65:    */     }
/*  66:    */     catch (SAXException ex)
/*  67:    */     {
/*  68: 73 */       showErrorMessage(target, ex.getLocalizedMessage());
/*  69:    */     }
/*  70:    */     catch (IOException ex)
/*  71:    */     {
/*  72: 75 */       showErrorMessage(target, ex.getLocalizedMessage());
/*  73:    */     }
/*  74:    */   }
/*  75:    */   
/*  76:    */   private static void showErrorMessage(JTextComponent text, String msg)
/*  77:    */   {
/*  78: 83 */     Component parent = SwingUtilities.getWindowAncestor(text);
/*  79: 84 */     JOptionPane.showMessageDialog(parent, msg, "JsyntaxPAne XML", 0);
/*  80:    */   }
/*  81:    */   
/*  82:    */   public static Transformer getTransformer()
/*  83:    */   {
/*  84: 88 */     if (transformer == null)
/*  85:    */     {
/*  86: 89 */       TransformerFactory tfactory = TransformerFactory.newInstance();
/*  87:    */       try
/*  88:    */       {
/*  89: 91 */         transformer = tfactory.newTransformer();
/*  90:    */       }
/*  91:    */       catch (TransformerConfigurationException ex)
/*  92:    */       {
/*  93: 93 */         throw new IllegalArgumentException("Unable to create transformer. ", ex);
/*  94:    */       }
/*  95:    */     }
/*  96: 96 */     return transformer;
/*  97:    */   }
/*  98:    */   
/*  99:    */   public void setIndent(String text)
/* 100:    */   {
/* 101:100 */     getTransformer().setOutputProperty("indent", text);
/* 102:    */   }
/* 103:    */   
/* 104:    */   public void setStandAlone(String text)
/* 105:    */   {
/* 106:104 */     getTransformer().setOutputProperty("standalone", text);
/* 107:    */   }
/* 108:    */   
/* 109:    */   public void setSOmitDeclaration(String text)
/* 110:    */   {
/* 111:108 */     getTransformer().setOutputProperty("omit-xml-declaration", text);
/* 112:    */   }
/* 113:    */   
/* 114:    */   public void setIndentAmount(String text)
/* 115:    */   {
/* 116:112 */     getTransformer().setOutputProperty("{http://xml.apache.org/xslt}indent-amount", text);
/* 117:    */   }
/* 118:    */   
/* 119:    */   public void setIgnoreComments(String ic)
/* 120:    */   {
/* 121:116 */     getDocBuilderFactory().setIgnoringComments(Boolean.parseBoolean(ic));
/* 122:    */   }
/* 123:    */   
/* 124:    */   public void setIgnoreWhiteSpace(String value)
/* 125:    */   {
/* 126:120 */     getDocBuilderFactory().setIgnoringElementContentWhitespace(Boolean.parseBoolean(value));
/* 127:    */   }
/* 128:    */   
/* 129:    */   public static DocumentBuilderFactory getDocBuilderFactory()
/* 130:    */   {
/* 131:124 */     if (docBuilderFactory == null) {
/* 132:125 */       docBuilderFactory = DocumentBuilderFactory.newInstance();
/* 133:    */     }
/* 134:127 */     return docBuilderFactory;
/* 135:    */   }
/* 136:    */   
/* 137:    */   public DocumentBuilder getDocBuilder()
/* 138:    */   {
/* 139:131 */     if (docBuilder == null) {
/* 140:    */       try
/* 141:    */       {
/* 142:133 */         docBuilder = getDocBuilderFactory().newDocumentBuilder();
/* 143:    */       }
/* 144:    */       catch (ParserConfigurationException ex)
/* 145:    */       {
/* 146:135 */         throw new IllegalArgumentException("Unable to create document builder", ex);
/* 147:    */       }
/* 148:    */     }
/* 149:138 */     return docBuilder;
/* 150:    */   }
/* 151:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.actions.XmlPrettifyAction
 * JD-Core Version:    0.7.0.1
 */