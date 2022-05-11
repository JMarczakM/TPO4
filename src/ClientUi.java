import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class ClientUi {
    public ClientUi() {}

    public void start(){
        JFrame frame = new JFrame("App");
        JPanel panel = new JPanel(new GridLayout(1,2));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        JPanel leftPanel = new JPanel(new GridLayout(4,2));
        JPanel rightPanel = new JPanel();

        TextLog textLog = new TextLog();
        textLog.setForeground(Color.WHITE);
        textLog.setSize(rightPanel.getSize());
        rightPanel.setBackground(Color.darkGray);

        rightPanel.add(textLog);

        MyButton button1 = new MyButton("Politics", textLog);
        MyButton button2 = new MyButton("Sports", textLog);
        MyButton button3 = new MyButton("Music", textLog);
        MyButton button4 = new MyButton("Nature", textLog);
        MyButton button5 = new MyButton("Games", textLog);
        MyButton button6 = new MyButton("Romance", textLog);
        MyButton button7 = new MyButton("Movies", textLog);
        MyButton button8 = new MyButton("Motorisation", textLog);

        leftPanel.add(button1);
        leftPanel.add(button2);
        leftPanel.add(button3);
        leftPanel.add(button4);
        leftPanel.add(button5);
        leftPanel.add(button6);
        leftPanel.add(button7);
        leftPanel.add(button8);

        panel.add(leftPanel);
        panel.add(rightPanel);


        frame.setContentPane(panel);
        frame.setVisible(true);
    }

    private class MyButton extends Button
    {
        boolean active = false;

        public MyButton(String name, TextLog textLog) throws HeadlessException {
            super(name);
            this.setBackground(Color.red);

            this.addActionListener(e -> {
                this.active = !active;
                if(active){
                    this.setBackground(Color.green);
                    textLog.write(name+" button pressed");
                    //todo button pressed logic
                } else {
                    this.setBackground(Color.red);
                    textLog.write(name+" button unpressed");
                    //todo button unpressed logic
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

    public static void main(String[] args){
        ClientUi clientUi = new ClientUi();
        clientUi.start();
    }

}
