import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.*;
import com.intellij.ui.content.*;

import javax.swing.*;
import java.awt.event.*;
import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 * User: Alexey.Chursin
 * Date: Aug 25, 2010
 * Time: 2:09:00 PM
 */
public class InputValidation implements ToolWindowFactory {

  private JButton refreshToolWindowButton;
  private JButton hideToolWindowButton;
  private JLabel CodeCheck;
  private JPanel myToolWindowContent;
  private JComboBox ValidationRule;
  private JButton settingsButton;
  private JButton helpButton1;
  private JTable jTable;
  private JScrollPane validationSP;
  private ToolWindow myToolWindow;


  public InputValidation() {
    hideToolWindowButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        myToolWindow.hide(null);
      }
    });
    refreshToolWindowButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        InputValidation.this.currentDateTime();
      }
    });
  }

  // Create the tool window content.
  public void createToolWindowContent(Project project, ToolWindow toolWindow) {
    myToolWindow = toolWindow;
    this.currentDateTime();


    ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
    Content content = contentFactory.createContent(myToolWindowContent, "", false);
    toolWindow.getContentManager().addContent(content);

  }

  public void currentDateTime() {
    // Get current date and time
    Calendar instance = Calendar.getInstance();
    CodeCheck.setText("CodeCheck 1.0 || Date:" + String.valueOf(instance.get(Calendar.DAY_OF_MONTH)) + "/"
                        + String.valueOf(instance.get(Calendar.MONTH) + 1) + "/" +
                        String.valueOf(instance.get(Calendar.YEAR)));
    CodeCheck.setIcon(new ImageIcon(getClass().getResource("/myToolWindow/CodeCheck.png")));
    int min = instance.get(Calendar.MINUTE);
    String strMin;
    if (min < 10) {
      strMin = "0" + String.valueOf(min);
    } else {
      strMin = String.valueOf(min);
    }

  }


}