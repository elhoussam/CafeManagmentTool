package me.elhoussam.window;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import me.elhoussam.interfaces.infoInterface;
import me.elhoussam.util.log.Tracking;

public class FC extends JDialog {
  private static infoInterface currentRemoteObj = null;


  private final JPanel contentPanel = new JPanel();
  static ArrayList<String> filePathsBase = new ArrayList<String>();
  static ArrayList<String> fileNamesBase = new ArrayList<String>();
  static String currentPath = "";
  static Boolean mainList = false ;
  static byte level =0;

  JList<String> listView  ;
  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    try {
      FC dialog = new FC(null);
      dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
      dialog.setVisible(true);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Create the dialog.
   */
  public FC(infoInterface selectedObj ) throws RemoteException{
    setBounds(100, 100, 214, 300);
    this.currentRemoteObj = selectedObj ;
    this.currentRemoteObj.getRootDir(false);
    ArrayList<String> rootNames = this.currentRemoteObj.getRootDir(false);

    ArrayList<String> rootPaths = this.currentRemoteObj.getRootDir(true);
    filePathsBase = (ArrayList<String>) rootPaths.clone();

    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    contentPanel.setLayout(new BorderLayout(0, 0));

    {
      listView =  new JList<String>( new DefaultListModel<String>() );
      listView.addMouseListener(
          new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
              JList<String> theList = (JList) mouseEvent.getSource();
              if (mouseEvent.getClickCount() == 2) {
                int index = theList.locationToIndex(mouseEvent.getPoint());
                if (index >= 0) {
                  /*if ( level == 0 ) {

                  }else {

                  }*/


                  String selectedElement = theList.getModel().getElementAt(index);
                  String firstHalf = selectedElement.split("|")[0].trim();
                  String secondHalf = selectedElement.split("|")[1].trim();

                  if ( firstHalf.contains("D") ) {
                    // change path
                    currentPath = fixEndingOf(currentPath) + secondHalf;
                    //change JList Element (model)
                    try {
                      Tracking.echo("list#Current Path `"+currentPath+"`" );
                      String [] newItems = FC.currentRemoteObj.changeDirAndListContent(currentPath);

                      DefaultListModel<String> model = getNewListModel( newItems ) ;
                      filePathsBase = new ArrayList<String>( Arrays.asList( newItems) );
                      listView.setModel(  model  );
                    } catch (RemoteException e1) {
                      e1.printStackTrace();
                    }
                  }else {
                    // change path

                    Tracking.echo("selected file " +   fixEndingOf(currentPath) + secondHalf   );
                  }

                  //String a [] = selectedElement.split("|")[1];
                  //mainList = false ;
                  //Tracking.echo("try to reach " +   currentPath   );
                  //theList.setModel ( getNewListModel (changeDirAndListContent( filePathsBase.get(index) )) );
                }
              }
            }
          });
      contentPanel.add(new JScrollPane(listView), BorderLayout.CENTER);
    }
    {
      JComboBox comboBox =  new JComboBox<String>(  );
      for (String a : rootNames) {
        comboBox.addItem(a);
      }
      comboBox.addActionListener( e -> {
        JComboBox cb = (JComboBox)e.getSource();
        int selectedIndex = cb.getSelectedIndex();
        cb.getSelectedItem();
        String fullPath = fixEndingOf(rootPaths.get(selectedIndex) ) ;
        try {
          currentPath = fullPath ;
          Tracking.echo("Current Path `"+currentPath+"`" );
          String [] newItems = this.currentRemoteObj.changeDirAndListContent(fullPath);

          DefaultListModel<String> model = getNewListModel( newItems ) ;
          filePathsBase = new ArrayList<String>( Arrays.asList( newItems) );
          listView.setModel(  model  );
        } catch (RemoteException e1) {
          e1.printStackTrace();
        }
      } );
      contentPanel.add(comboBox, BorderLayout.NORTH);
    }

    {
      JPanel panel = new JPanel();
      contentPanel.add(panel, BorderLayout.SOUTH);
      panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
      {
        JButton button = new JButton("UP");
        button.setActionCommand("OK");
        panel.add(button);
      }
      {
        JButton button = new JButton("Copy");
        button.setActionCommand("Cancel");
        panel.add(button);
      }
    }

    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    this.setAlwaysOnTop(true);
    this.requestFocus();
    setVisible(true);
  }
  private static String fixEndingOf( String str ) {
    if ( str.endsWith( File.separator ) ) {
      return str ;
    }else {
      return str.concat( File.separator);
    }
  }
  private static DefaultListModel<String> getNewListModel(String[] arr) {
    if( arr ==  null ) return new DefaultListModel<String>();
    ArrayList<String>  a = new ArrayList<String>( Arrays.asList(arr) );
    return getNewListModel( a  );
  }
  private static DefaultListModel<String> getNewListModel(ArrayList<String> arr)  {
    DefaultListModel<String> demoList = new DefaultListModel<String>();
    for( String a : arr ) {
      String st = "*|";

      // Tracking.echo(a);
      if(  a.endsWith("/") || a.endsWith("\\") ) st="D|";
      else   st ="-|";
      demoList.addElement(st+""+a);
    }
    return demoList ;
  }
}
