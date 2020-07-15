package cn.nihility.jdk8;

import javax.swing.*;

/**
 * SwingTest
 *
 * @author clover
 * @date 2020-01-04 10:34
 *
 */
public class SwingTest {

    public static void main(String[] args) {

        JFrame jFrame = new JFrame("My Frame");
        JButton myButton = new JButton("My Button");

        /*myButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Button Pressed!");
            }
        });*/

        myButton.addActionListener( event -> {
            System.out.println("Button pressed!");
            System.out.println("Button pressed!");
            System.out.println("Button pressed!");
        });

        jFrame.add(myButton);
        jFrame.pack();
        jFrame.setVisible(true);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }


}
