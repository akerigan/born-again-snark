package org.klomp.snark.gui;

import javax.swing.*;

/**
 * Date: 10.09.2009
 * Time: 0:21:51 (Moscow Standard Time)
 *
 * @author Vlad Vinichenko (akerigan@gmail.com)
 */
public abstract class EmptyFrame extends JFrame {

    protected abstract void initComponents();

    protected abstract JPanel getMainPanel();

    protected EmptyFrame(String title, int width, int height) {
        super(title);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        initComponents();
        getContentPane().add(getMainPanel());

        setSize(width, height);
        setVisible(true);
    }
}
