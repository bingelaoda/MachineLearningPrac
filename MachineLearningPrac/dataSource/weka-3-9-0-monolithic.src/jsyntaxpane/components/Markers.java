/*   1:    */ package jsyntaxpane.components;
/*   2:    */ 
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.util.logging.Level;
/*   5:    */ import java.util.logging.Logger;
/*   6:    */ import java.util.regex.Matcher;
/*   7:    */ import java.util.regex.Pattern;
/*   8:    */ import javax.swing.text.BadLocationException;
/*   9:    */ import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
/*  10:    */ import javax.swing.text.Highlighter;
/*  11:    */ import javax.swing.text.Highlighter.Highlight;
/*  12:    */ import javax.swing.text.JTextComponent;
/*  13:    */ import jsyntaxpane.SyntaxDocument;
/*  14:    */ import jsyntaxpane.Token;
/*  15:    */ import jsyntaxpane.actions.ActionUtils;
/*  16:    */ 
/*  17:    */ public class Markers
/*  18:    */ {
/*  19:    */   public static class SimpleMarker
/*  20:    */     extends DefaultHighlighter.DefaultHighlightPainter
/*  21:    */   {
/*  22:    */     public SimpleMarker(Color color)
/*  23:    */     {
/*  24: 41 */       super();
/*  25:    */     }
/*  26:    */   }
/*  27:    */   
/*  28:    */   public static void removeMarkers(JTextComponent component, SimpleMarker marker)
/*  29:    */   {
/*  30: 54 */     Highlighter hilite = component.getHighlighter();
/*  31: 55 */     Highlighter.Highlight[] hilites = hilite.getHighlights();
/*  32: 57 */     for (int i = 0; i < hilites.length; i++) {
/*  33: 58 */       if ((hilites[i].getPainter() instanceof SimpleMarker))
/*  34:    */       {
/*  35: 59 */         SimpleMarker hMarker = (SimpleMarker)hilites[i].getPainter();
/*  36: 60 */         if ((marker == null) || (hMarker.equals(marker))) {
/*  37: 61 */           hilite.removeHighlight(hilites[i]);
/*  38:    */         }
/*  39:    */       }
/*  40:    */     }
/*  41:    */   }
/*  42:    */   
/*  43:    */   public static void removeMarkers(JTextComponent editorPane)
/*  44:    */   {
/*  45: 72 */     removeMarkers(editorPane, null);
/*  46:    */   }
/*  47:    */   
/*  48:    */   public static void markToken(JTextComponent pane, Token token, SimpleMarker marker)
/*  49:    */   {
/*  50: 82 */     markText(pane, token.start, token.end(), marker);
/*  51:    */   }
/*  52:    */   
/*  53:    */   public static void markText(JTextComponent pane, int start, int end, SimpleMarker marker)
/*  54:    */   {
/*  55:    */     try
/*  56:    */     {
/*  57: 94 */       Highlighter hiliter = pane.getHighlighter();
/*  58: 95 */       int selStart = pane.getSelectionStart();
/*  59: 96 */       int selEnd = pane.getSelectionEnd();
/*  60: 98 */       if ((selStart == selEnd) || (end < selStart) || (start > selStart))
/*  61:    */       {
/*  62: 99 */         hiliter.addHighlight(start, end, marker);
/*  63:100 */         return;
/*  64:    */       }
/*  65:103 */       if ((selStart > start) && (selStart < end)) {
/*  66:104 */         hiliter.addHighlight(start, selStart, marker);
/*  67:    */       }
/*  68:107 */       if ((selEnd > start) && (selEnd < end)) {
/*  69:108 */         hiliter.addHighlight(selEnd, end, marker);
/*  70:    */       }
/*  71:    */     }
/*  72:    */     catch (BadLocationException ex)
/*  73:    */     {
/*  74:113 */       LOG.log(Level.SEVERE, null, ex);
/*  75:    */     }
/*  76:    */   }
/*  77:    */   
/*  78:    */   public static void markAll(JTextComponent pane, Pattern pattern, SimpleMarker marker)
/*  79:    */   {
/*  80:124 */     SyntaxDocument sDoc = ActionUtils.getSyntaxDocument(pane);
/*  81:125 */     if ((sDoc == null) || (pattern == null)) {
/*  82:126 */       return;
/*  83:    */     }
/*  84:128 */     Matcher matcher = sDoc.getMatcher(pattern);
/*  85:129 */     while (matcher.find()) {
/*  86:130 */       markText(pane, matcher.start(), matcher.end(), marker);
/*  87:    */     }
/*  88:    */   }
/*  89:    */   
/*  90:134 */   private static final Logger LOG = Logger.getLogger(Markers.class.getName());
/*  91:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.components.Markers
 * JD-Core Version:    0.7.0.1
 */