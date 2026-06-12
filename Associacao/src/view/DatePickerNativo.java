package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DatePickerNativo {
    private int month = Calendar.getInstance().get(Calendar.MONTH);
    private int year = Calendar.getInstance().get(Calendar.YEAR);
    private JLabel label = new JLabel("", JLabel.CENTER);
    private String day = "";
    private JDialog dialog;
    private JButton[] button = new JButton[49];

    public String exibirCalendario(JFrame parent, Component invoker) {
        dialog = new JDialog(parent, "Selecione a Data", true);
        dialog.setUndecorated(true);
        dialog.setSize(300, 240);

        Point p = invoker.getLocationOnScreen();
        dialog.setLocation(p.x, p.y + invoker.getHeight());

        JPanel p1 = new JPanel(new GridLayout(7, 7));
        p1.setPreferredSize(new Dimension(300, 180));
        p1.setBackground(Color.WHITE);

        String[] header = { "Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb" };
        for (int x = 0; x < button.length; x++) {
            final int selection = x;
            button[x] = new JButton();
            button[x].setFocusPainted(false);
            button[x].setBackground(Color.WHITE);
            button[x].setBorder(BorderFactory.createLineBorder(new Color(240, 240, 240)));

            if (x < 7) {
                button[x].setText(header[x]);
                button[x].setFont(new Font("Segoe UI", Font.BOLD, 12));
                button[x].setForeground(new Color(185, 120, 30));
            } else {
                button[x].addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        day = button[selection].getText();
                        dialog.dispose();
                    }
                });
            }
            p1.add(button[x]);
        }

        JPanel p2 = new JPanel(new BorderLayout());
        p2.setBackground(new Color(205, 145, 55));

        JButton previous = new JButton("<<");
        previous.setForeground(Color.WHITE);
        previous.setBackground(new Color(185, 120, 30));
        previous.setFocusPainted(false);
        previous.setBorderPainted(false);
        previous.addActionListener(e -> {
            month--;
            exibirMes();
        });

        JButton next = new JButton(">>");
        next.setForeground(Color.WHITE);
        next.setBackground(new Color(185, 120, 30));
        next.setFocusPainted(false);
        next.setBorderPainted(false);
        next.addActionListener(e -> {
            month++;
            exibirMes();
        });

        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));

        p2.add(previous, BorderLayout.WEST);
        p2.add(label, BorderLayout.CENTER);
        p2.add(next, BorderLayout.EAST);

        dialog.add(p1, BorderLayout.CENTER);
        dialog.add(p2, BorderLayout.NORTH);

        if (dialog.getContentPane() instanceof JPanel) {
            ((JPanel) dialog.getContentPane()).setBorder(BorderFactory.createLineBorder(new Color(185, 120, 30), 1));
        }

        exibirMes();
        dialog.setVisible(true);

        if (!day.equals("")) {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, Integer.parseInt(day));
            return new SimpleDateFormat("dd/MM/yyyy").format(cal.getTime());
        }
        return "";
    }

    private void exibirMes() {
        for (int x = 7; x < button.length; x++) {
            button[x].setText("");
            button[x].setEnabled(false);
            button[x].setBackground(Color.WHITE);
        }

        Calendar cal = Calendar.getInstance();
        cal.set(year, month, 1);

        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);

        String[] months = { "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro" };
        label.setText(months[month] + " " + year);

        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 1, index = 6 + dayOfWeek; i <= daysInMonth; i++, index++) {
            button[index].setText(String.valueOf(i));
            button[index].setEnabled(true);
            button[index].setFont(new Font("Segoe UI", Font.PLAIN, 12));

            Calendar hoje = Calendar.getInstance();
            if (i == hoje.get(Calendar.DAY_OF_MONTH) && month == hoje.get(Calendar.MONTH) && year == hoje.get(Calendar.YEAR)) {
                button[index].setBackground(new Color(248, 245, 240));
                button[index].setFont(new Font("Segoe UI", Font.BOLD, 12));
            }
        }
    }
}