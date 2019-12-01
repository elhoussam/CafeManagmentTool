package me.elhoussam.window;

import java.awt.BorderLayout;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import me.elhoussam.core.Manager;
import me.elhoussam.interfaces.infoInterface;
import me.elhoussam.util.log.Tracking;

public class Popupwindows {



  public static void viewScreenshot(String args) {
    Thread scThread = new Thread("scThread") {
      @Override
      public void run() {
        JFrame frame = new JFrame("main");

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel pnl = new JPanel();
        //JLabel img = new JLabel("");
        //img.setIcon(new ImageIcon( getClass().getClassLoader().getResource("icons/sqlserver.png") ) );
        ImageIcon ii = new ImageIcon(args);
        // try to resize image
        Image image = ii.getImage(); // transform it
        int width = ii.getIconWidth(), height = ii.getIconHeight();
        Tracking.echo("W="+width+" H="+height);
        Image newimg = image.getScaledInstance(
            (int)(ii.getIconWidth()*0.7),
            (int)(ii.getIconHeight()*0.7),  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way


        JSlider b = new JSlider(0, 99, 70);

        // paint the ticks and tarcks
        b.setPaintTrack(true);
        b.setPaintTicks(true);
        b.setPaintLabels(true);

        // set spacing
        b.setMajorTickSpacing(50);
        b.setMinorTickSpacing(5);

        // setChangeListener
        b.addChangeListener(event -> {
          int percentage = b.getValue() ;
          Image newimgg = ii.getImage();
          newimgg = image.getScaledInstance(
              (ii.getIconWidth()*percentage/100),
              (ii.getIconHeight()*percentage/100),  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
          ImageIcon iii = new ImageIcon(newimgg);
          ((JLabel)pnl.getComponent(0)).setIcon(iii);
          Tracking.echo("Update  W="+iii.getIconWidth()+" H="+iii.getIconHeight());
        });
        JLabel lable = new JLabel(new ImageIcon(newimg));

        pnl.setLayout( new BorderLayout() );
        pnl.add(lable , BorderLayout.CENTER );
        pnl.add(b , BorderLayout.NORTH );

        JScrollPane jsp = new JScrollPane(    pnl);
        frame.getContentPane().add(jsp);
        frame. setSize(1000, 700);

        frame.setVisible(true);
      }
    };
    scThread.start();
  }

  public static String[] selectFilesOnPc(int pcNumber){
    infoInterface infOBJ = Manager.get().get(pcNumber).getRef();
    try {
      FileChooser2 a = new FileChooser2(infOBJ);
      int val = a.showOpenDialog();
      if( val != -1) {
        Tracking.echo("choosen file "+a.getSelectedPaths() );
      }
      //return FileChooser.getSelectedPaths();

      //new FC( infOBJ );
      Tracking.echo("------------ selectFileEnd");

    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
