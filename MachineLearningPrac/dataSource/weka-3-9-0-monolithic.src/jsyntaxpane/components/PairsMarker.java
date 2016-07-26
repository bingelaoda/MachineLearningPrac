/*  1:   */ package jsyntaxpane.components;
/*  2:   */ 
/*  3:   */ import java.awt.Color;
/*  4:   */ import java.beans.PropertyChangeEvent;
/*  5:   */ import java.beans.PropertyChangeListener;
/*  6:   */ import javax.swing.JEditorPane;
/*  7:   */ import javax.swing.event.CaretEvent;
/*  8:   */ import javax.swing.event.CaretListener;
/*  9:   */ import javax.swing.text.JTextComponent;
/* 10:   */ import jsyntaxpane.SyntaxDocument;
/* 11:   */ import jsyntaxpane.Token;
/* 12:   */ import jsyntaxpane.actions.ActionUtils;
/* 13:   */ import jsyntaxpane.util.Configuration;
/* 14:   */ 
/* 15:   */ public class PairsMarker
/* 16:   */   implements CaretListener, SyntaxComponent, PropertyChangeListener
/* 17:   */ {
/* 18:   */   public static final String PROPERTY_COLOR = "PairMarker.Color";
/* 19:   */   private JTextComponent pane;
/* 20:   */   private Markers.SimpleMarker marker;
/* 21:   */   private SyntaxComponent.Status status;
/* 22:   */   
/* 23:   */   public void caretUpdate(CaretEvent e)
/* 24:   */   {
/* 25:46 */     removeMarkers();
/* 26:47 */     int pos = e.getDot();
/* 27:48 */     SyntaxDocument doc = ActionUtils.getSyntaxDocument(this.pane);
/* 28:49 */     Token token = doc.getTokenAt(pos);
/* 29:50 */     if ((token != null) && (token.pairValue != 0))
/* 30:   */     {
/* 31:51 */       Markers.markToken(this.pane, token, this.marker);
/* 32:52 */       Token other = doc.getPairFor(token);
/* 33:53 */       if (other != null) {
/* 34:54 */         Markers.markToken(this.pane, other, this.marker);
/* 35:   */       }
/* 36:   */     }
/* 37:   */   }
/* 38:   */   
/* 39:   */   public void removeMarkers()
/* 40:   */   {
/* 41:64 */     Markers.removeMarkers(this.pane, this.marker);
/* 42:   */   }
/* 43:   */   
/* 44:   */   public void config(Configuration config)
/* 45:   */   {
/* 46:69 */     Color markerColor = config.getColor("PairMarker.Color", new Color(15658547));
/* 47:70 */     this.marker = new Markers.SimpleMarker(markerColor);
/* 48:   */   }
/* 49:   */   
/* 50:   */   public void install(JEditorPane editor)
/* 51:   */   {
/* 52:75 */     this.pane = editor;
/* 53:76 */     this.pane.addCaretListener(this);
/* 54:77 */     this.status = SyntaxComponent.Status.INSTALLING;
/* 55:   */   }
/* 56:   */   
/* 57:   */   public void deinstall(JEditorPane editor)
/* 58:   */   {
/* 59:82 */     this.status = SyntaxComponent.Status.DEINSTALLING;
/* 60:83 */     this.pane.removeCaretListener(this);
/* 61:84 */     removeMarkers();
/* 62:   */   }
/* 63:   */   
/* 64:   */   public void propertyChange(PropertyChangeEvent evt)
/* 65:   */   {
/* 66:89 */     if (evt.getPropertyName().equals("document"))
/* 67:   */     {
/* 68:90 */       this.pane.removeCaretListener(this);
/* 69:91 */       if (this.status.equals(SyntaxComponent.Status.INSTALLING))
/* 70:   */       {
/* 71:92 */         this.pane.addCaretListener(this);
/* 72:93 */         removeMarkers();
/* 73:   */       }
/* 74:   */     }
/* 75:   */   }
/* 76:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.components.PairsMarker
 * JD-Core Version:    0.7.0.1
 */