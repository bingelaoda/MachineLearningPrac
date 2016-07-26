/*   1:    */ package jsyntaxpane.components;
/*   2:    */ 
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.beans.PropertyChangeEvent;
/*   5:    */ import java.beans.PropertyChangeListener;
/*   6:    */ import java.util.HashSet;
/*   7:    */ import java.util.Iterator;
/*   8:    */ import java.util.Set;
/*   9:    */ import java.util.logging.Logger;
/*  10:    */ import javax.swing.JEditorPane;
/*  11:    */ import javax.swing.event.CaretEvent;
/*  12:    */ import javax.swing.event.CaretListener;
/*  13:    */ import jsyntaxpane.SyntaxDocument;
/*  14:    */ import jsyntaxpane.Token;
/*  15:    */ import jsyntaxpane.TokenType;
/*  16:    */ import jsyntaxpane.actions.ActionUtils;
/*  17:    */ import jsyntaxpane.util.Configuration;
/*  18:    */ 
/*  19:    */ public class TokenMarker
/*  20:    */   implements SyntaxComponent, CaretListener, PropertyChangeListener
/*  21:    */ {
/*  22:    */   public static final String DEFAULT_TOKENTYPES = "IDENTIFIER, TYPE, TYPE2, TYPE3";
/*  23:    */   public static final String PROPERTY_COLOR = "TokenMarker.Color";
/*  24:    */   public static final String PROPERTY_TOKENTYPES = "TokenMarker.TokenTypes";
/*  25: 43 */   private static final Color DEFAULT_COLOR = new Color(16772710);
/*  26:    */   private JEditorPane pane;
/*  27: 45 */   private Set<TokenType> tokenTypes = new HashSet();
/*  28:    */   private Markers.SimpleMarker marker;
/*  29:    */   private SyntaxComponent.Status status;
/*  30:    */   
/*  31:    */   public void caretUpdate(CaretEvent e)
/*  32:    */   {
/*  33: 57 */     markTokenAt(e.getDot());
/*  34:    */   }
/*  35:    */   
/*  36:    */   public void markTokenAt(int pos)
/*  37:    */   {
/*  38: 61 */     SyntaxDocument doc = ActionUtils.getSyntaxDocument(this.pane);
/*  39: 62 */     if (doc != null)
/*  40:    */     {
/*  41: 63 */       Token token = doc.getTokenAt(pos);
/*  42: 64 */       removeMarkers();
/*  43: 65 */       if ((token != null) && (this.tokenTypes.contains(token.type))) {
/*  44: 66 */         addMarkers(token);
/*  45:    */       }
/*  46:    */     }
/*  47:    */   }
/*  48:    */   
/*  49:    */   public void removeMarkers()
/*  50:    */   {
/*  51: 75 */     Markers.removeMarkers(this.pane, this.marker);
/*  52:    */   }
/*  53:    */   
/*  54:    */   void addMarkers(Token tok)
/*  55:    */   {
/*  56: 83 */     SyntaxDocument sDoc = (SyntaxDocument)this.pane.getDocument();
/*  57: 84 */     sDoc.readLock();
/*  58:    */     
/*  59:    */ 
/*  60: 87 */     String text = tok.getText(sDoc).toString();
/*  61: 88 */     Iterator<Token> it = sDoc.getTokens(0, sDoc.getLength());
/*  62: 89 */     while (it.hasNext())
/*  63:    */     {
/*  64: 90 */       Token nextToken = (Token)it.next();
/*  65: 91 */       String nextText = nextToken.getText(sDoc).toString();
/*  66: 92 */       if (text.equals(nextText)) {
/*  67: 93 */         Markers.markToken(this.pane, nextToken, this.marker);
/*  68:    */       }
/*  69:    */     }
/*  70: 97 */     sDoc.readUnlock();
/*  71:    */   }
/*  72:    */   
/*  73:    */   public void config(Configuration config)
/*  74:    */   {
/*  75:102 */     Color markerColor = config.getColor("TokenMarker.Color", DEFAULT_COLOR);
/*  76:    */     
/*  77:104 */     this.marker = new Markers.SimpleMarker(markerColor);
/*  78:105 */     String types = config.getString("TokenMarker.TokenTypes", "IDENTIFIER, TYPE, TYPE2, TYPE3");
/*  79:108 */     for (String type : types.split("\\s*,\\s*")) {
/*  80:    */       try
/*  81:    */       {
/*  82:110 */         TokenType tt = TokenType.valueOf(type);
/*  83:111 */         this.tokenTypes.add(tt);
/*  84:    */       }
/*  85:    */       catch (IllegalArgumentException e)
/*  86:    */       {
/*  87:113 */         LOG.warning("Error in setting up TokenMarker  - Invalid TokenType: " + type);
/*  88:    */       }
/*  89:    */     }
/*  90:    */   }
/*  91:    */   
/*  92:    */   public void install(JEditorPane editor)
/*  93:    */   {
/*  94:122 */     this.pane = editor;
/*  95:123 */     this.pane.addCaretListener(this);
/*  96:124 */     markTokenAt(editor.getCaretPosition());
/*  97:125 */     this.status = SyntaxComponent.Status.INSTALLING;
/*  98:    */   }
/*  99:    */   
/* 100:    */   public void deinstall(JEditorPane editor)
/* 101:    */   {
/* 102:130 */     this.status = SyntaxComponent.Status.DEINSTALLING;
/* 103:131 */     removeMarkers();
/* 104:132 */     this.pane.removeCaretListener(this);
/* 105:    */   }
/* 106:    */   
/* 107:134 */   private static final Logger LOG = Logger.getLogger(TokenMarker.class.getName());
/* 108:    */   
/* 109:    */   public void propertyChange(PropertyChangeEvent evt)
/* 110:    */   {
/* 111:138 */     if (evt.getPropertyName().equals("document"))
/* 112:    */     {
/* 113:139 */       this.pane.removeCaretListener(this);
/* 114:140 */       if (this.status.equals(SyntaxComponent.Status.INSTALLING))
/* 115:    */       {
/* 116:141 */         this.pane.addCaretListener(this);
/* 117:142 */         removeMarkers();
/* 118:    */       }
/* 119:    */     }
/* 120:    */   }
/* 121:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.components.TokenMarker
 * JD-Core Version:    0.7.0.1
 */