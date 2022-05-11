import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class AdminUi {
    AdminUi.MyButton[] buttons = new MyButton[8];
    JLabel response;
    public AdminUi() {
    }

    public void start(NewsServer newsServer){
        JFrame frame = new JFrame("App");
        JPanel panel = new JPanel(new GridLayout(1,2));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        JPanel leftPanel = new JPanel(new GridLayout(4,2));
        JPanel rightPanel = new JPanel();

        JPanel innerRightButtonPanel = new JPanel();
        JButton irbutton1 = new JButton("Add");
        irbutton1.addActionListener(e -> {
            System.out.println("Add");
            //todo logic
        });

        JButton irbutton2 = new JButton("Delete");
        irbutton2.addActionListener(e -> {
            System.out.println("Delete");
            //todo logic
        });
        this.response = new JLabel("");
        response.setForeground(Color.white);
        innerRightButtonPanel.add(irbutton1);
        innerRightButtonPanel.add(irbutton2);
        rightPanel.add(innerRightButtonPanel);

        JTextField textField = new JTextField(20);
        rightPanel.add(textField);
        rightPanel.add(response);

        rightPanel.setBackground(Color.darkGray);



        this.buttons[0] = new AdminUi.MyButton("Politics", buttons);
        this.buttons[1] = new AdminUi.MyButton("Sports", buttons);
        this.buttons[2] = new AdminUi.MyButton("Music", buttons);
        this.buttons[3] = new AdminUi.MyButton("Nature", buttons);
        this.buttons[4] = new AdminUi.MyButton("Games", buttons);
        this.buttons[5] = new AdminUi.MyButton("Romance", buttons);
        this.buttons[6] = new AdminUi.MyButton("Movies", buttons);
        this.buttons[7] = new AdminUi.MyButton("Motorisation", buttons);

        leftPanel.add(buttons[0]);
        leftPanel.add(buttons[1]);
        leftPanel.add(buttons[2]);
        leftPanel.add(buttons[3]);
        leftPanel.add(buttons[4]);
        leftPanel.add(buttons[5]);
        leftPanel.add(buttons[6]);
        leftPanel.add(buttons[7]);

        panel.add(leftPanel);
        panel.add(rightPanel);


        frame.setContentPane(panel);
        frame.setVisible(true);
    }

    private class MyButton extends Button
    {
        boolean active = false;

        public MyButton(String name, MyButton[] buttons) throws HeadlessException {
            super(name);
            this.setBackground(Color.red);
            this.addActionListener(e -> {
                for (MyButton m: buttons) {
                    m.active = false;
                    m.setBackground(Color.red);
                }
                this.setBackground(Color.green);
                //todo button pressed logic
                this.active = true;
            });
        }
    }

    public static void main(String[] args) throws IOException {
        AdminUi adminUi = new AdminUi();
        NewsServer newsServer = new NewsServer();
        adminUi.start(newsServer);
    }
}
