package Util;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JPanel;

public class SelfAdaption extends JPanel {

    private static final long serialVersionUID = 1L;

    protected GridBagLayout layout;
    protected GridBagConstraints constraints;

    public SelfAdaption() {

        layout = new GridBagLayout();
        constraints = new GridBagConstraints();
        this.setLayout(layout);
        constraints.fill = constraints.BOTH;
    }

    protected void addComponent(Component componentToAdd, GridBagLayout layout,
                                GridBagConstraints constraints,  int row,
                                int column, int height, int width, int x, int y) {
        constraints.gridx = column;
        constraints.gridy = row;
        constraints.gridwidth = width;
        constraints.gridheight = height;
        constraints.weightx = x;
        constraints.weighty = y;
        layout.setConstraints(componentToAdd, constraints);
        add(componentToAdd);
    }

    protected void addComponent(JPanel componentToAdd, int row, int column,
                                int height, int width, int xAblility, int yAblity) {
        addComponent(componentToAdd,layout, constraints, row,
                column, height, width, xAblility, yAblity);
    }
}