/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.awt.Component;
/*   5:    */ import java.awt.Desktop;
/*   6:    */ import java.awt.Desktop.Action;
/*   7:    */ import java.awt.event.MouseAdapter;
/*   8:    */ import java.awt.event.MouseEvent;
/*   9:    */ import java.io.PrintStream;
/*  10:    */ import java.lang.reflect.Method;
/*  11:    */ import java.net.URI;
/*  12:    */ import javax.swing.JLabel;
/*  13:    */ import javax.swing.JOptionPane;
/*  14:    */ 
/*  15:    */ public class BrowserHelper
/*  16:    */ {
/*  17: 49 */   public static final String[] LINUX_BROWSERS = { "firefox", "google-chrome", "opera", "konqueror", "epiphany", "mozilla", "netscape" };
/*  18:    */   
/*  19:    */   public static void openURL(String url)
/*  20:    */   {
/*  21: 58 */     openURL(null, url);
/*  22:    */   }
/*  23:    */   
/*  24:    */   public static void openURL(Component parent, String url)
/*  25:    */   {
/*  26: 68 */     openURL(parent, url, true);
/*  27:    */   }
/*  28:    */   
/*  29:    */   public static void openURL(Component parent, String url, boolean showDialog)
/*  30:    */   {
/*  31: 80 */     String osName = System.getProperty("os.name");
/*  32:    */     try
/*  33:    */     {
/*  34: 82 */       if ((Desktop.isDesktopSupported()) && (Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)))
/*  35:    */       {
/*  36: 83 */         Desktop.getDesktop().browse(new URI(url));
/*  37:    */       }
/*  38:    */       else
/*  39:    */       {
/*  40: 86 */         System.err.println("Desktop or browse action not supported, using fallback to determine browser.");
/*  41: 89 */         if (osName.startsWith("Mac OS"))
/*  42:    */         {
/*  43: 90 */           Class<?> fileMgr = Class.forName("com.apple.eio.FileManager");
/*  44: 91 */           Method openURL = fileMgr.getDeclaredMethod("openURL", new Class[] { String.class });
/*  45:    */           
/*  46: 93 */           openURL.invoke(null, new Object[] { url });
/*  47:    */         }
/*  48: 96 */         else if (osName.startsWith("Windows"))
/*  49:    */         {
/*  50: 97 */           Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
/*  51:    */         }
/*  52:    */         else
/*  53:    */         {
/*  54:102 */           String browser = null;
/*  55:103 */           for (int count = 0; (count < LINUX_BROWSERS.length) && (browser == null); count++) {
/*  56:105 */             if (Runtime.getRuntime().exec(new String[] { "which", LINUX_BROWSERS[count] }).waitFor() == 0)
/*  57:    */             {
/*  58:107 */               browser = LINUX_BROWSERS[count];
/*  59:108 */               break;
/*  60:    */             }
/*  61:    */           }
/*  62:111 */           if (browser == null) {
/*  63:112 */             throw new Exception("Could not find web browser");
/*  64:    */           }
/*  65:114 */           Runtime.getRuntime().exec(new String[] { browser, url });
/*  66:    */         }
/*  67:    */       }
/*  68:    */     }
/*  69:    */     catch (Exception e)
/*  70:    */     {
/*  71:119 */       String errMsg = "Error attempting to launch web browser:\n" + e.getMessage();
/*  72:122 */       if (showDialog) {
/*  73:123 */         JOptionPane.showMessageDialog(parent, errMsg);
/*  74:    */       } else {
/*  75:125 */         System.err.println(errMsg);
/*  76:    */       }
/*  77:    */     }
/*  78:    */   }
/*  79:    */   
/*  80:    */   public static JLabel createLink(String url, String text)
/*  81:    */   {
/*  82:139 */     String urlF = url;
/*  83:140 */     final JLabel result = new JLabel();
/*  84:141 */     result.setText((text == null) || (text.length() == 0) ? url : text);
/*  85:142 */     result.setToolTipText("Click to open link in browser");
/*  86:143 */     result.setForeground(Color.BLUE);
/*  87:144 */     result.addMouseListener(new MouseAdapter()
/*  88:    */     {
/*  89:    */       public void mouseClicked(MouseEvent e)
/*  90:    */       {
/*  91:147 */         if (e.getButton() == 1) {
/*  92:148 */           BrowserHelper.openURL(this.val$urlF);
/*  93:    */         } else {
/*  94:150 */           super.mouseClicked(e);
/*  95:    */         }
/*  96:    */       }
/*  97:    */       
/*  98:    */       public void mouseEntered(MouseEvent e)
/*  99:    */       {
/* 100:156 */         result.setForeground(Color.RED);
/* 101:    */       }
/* 102:    */       
/* 103:    */       public void mouseExited(MouseEvent e)
/* 104:    */       {
/* 105:161 */         result.setForeground(Color.BLUE);
/* 106:    */       }
/* 107:164 */     });
/* 108:165 */     return result;
/* 109:    */   }
/* 110:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.BrowserHelper
 * JD-Core Version:    0.7.0.1
 */