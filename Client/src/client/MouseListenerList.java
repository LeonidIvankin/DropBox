package client;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MouseListenerList implements MouseListener {
	private JList list;
	private Control control;

	public MouseListenerList(JList list, Control control) {
		this.list = list;
		this.control = control;
		list.addMouseListener(this);

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		list = (JList) e.getSource();
		if (e.getClickCount() == 2) {
			int index = list.locationToIndex(e.getPoint());
			if (index >= 0) {
				Object o = list.getModel().getElementAt(index);
				control.move(o.toString());
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}
}
