import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Arrays;

public class AdminUi {
    private TextLog textLog;
    private Admin admin;
    private JTextField textField;

    public AdminUi(Admin admin) {
        this.admin = admin;
    }

    public void start(){
        JFrame frame = new JFrame("App");
        JPanel panel = new JPanel(new GridLayout(1,2));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        JPanel leftPanel = new JPanel(new GridLayout(4,1));
        JPanel rightPanel = new JPanel();

        textLog = new TextLog();
        textLog.setForeground(Color.WHITE);
        textLog.setSize(rightPanel.getSize());
        rightPanel.setBackground(Color.darkGray);

        rightPanel.add(textLog);

        MyButton button1 = new MyButton("create", textLog);
        MyButton button2 = new MyButton("replace", textLog);
        MyButton button3 = new MyButton("sendToAll", textLog);
        textField = new JTextField(30);

        leftPanel.add(button1);
        leftPanel.add(button2);
        leftPanel.add(button3);
        leftPanel.add(textField);
        panel.add(leftPanel);
        panel.add(rightPanel);


        frame.setContentPane(panel);
        frame.setVisible(true);
    }

    private class MyButton extends Button
    {

        public MyButton(String name, TextLog textLog) throws HeadlessException {
            super(name);
            this.setBackground(Color.gray);

            this.addActionListener(e -> {

            textLog.write(name+" button pressed");
            try {
                send(name+" "+textField.getText());
                textField.setText("");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            });
        }
    }

    private class TextLog extends JLabel{
        private String[] lines = new String[27];

        public TextLog() {
            this.setForeground(Color.WHITE);
            Arrays.fill(lines, " ");
            this.lines[0] = "<html><pre>";
            this.lines[lines.length-1] = "</pre></html>";
            this.setText(String.join("\n", lines));
        }

        public void write(String string){
            for (int i = lines.length-2; i > 1; i--) {
                this.lines[i] = lines[i-1];
            }
            this.lines[1] = string;
            this.setText(String.join("\n", lines));
        }
    }

    public void printToTextLog(String s){
        textLog.write(s);
    }

    public void send(String s) throws IOException {
        admin.printToServer(s);
    }
}
