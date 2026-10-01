import javax.swing.*;import javax.swing.table.DefaultTableModel;import java.awt.*;import java.io.*;import java.nio.charset.StandardCharsets;import java.util.Random;
/* <ars> */
public class ApertureSafe extends JFrame {
    private static final String FILE_NAME = "aperture_vault.db";
    private DefaultTableModel model;private JTextField siteF, logF, passF;private String masterKey;
    public ApertureSafe() {
        masterKey = JOptionPane.showInputDialog(this, "Введите Мастер-Пароль для расшифровки базы:", "Aperture Safe Core", JOptionPane.QUESTION_MESSAGE);
        if (masterKey == null || masterKey.isEmpty()) System.exit(0);
        setTitle("Aperture Crypto-Safe v1.0");setSize(650, 400);setDefaultCloseOperation(EXIT_ON_CLOSE);setLocationRelativeTo(null);
        JPanel p = new JPanel(new BorderLayout(5, 5));p.setBackground(new Color(30, 35, 40));
        String[] cols = {"Сайт / Сервис", "Логин", "Пароль"};model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);table.setBackground(new Color(45, 50, 55));table.setForeground(Color.WHITE);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel inputP = new JPanel(new GridLayout(4, 2, 5, 5));inputP.setBackground(new Color(45, 50, 55));
        inputP.add(lbl(" Сайт:")); siteF = txt(); inputP.add(siteF);
        inputP.add(lbl(" Логин:")); logF = txt(); inputP.add(logF);
        inputP.add(lbl(" Пароль:")); passF = txt(); inputP.add(passF);
        JButton genB = new JButton("Сгенерировать пароль"); genB.addActionListener(e -> passF.setText(genPass())); inputP.add(genB);
        JButton addB = new JButton("СОХРАНИТЬ ЗАПИСЬ"); addB.setBackground(new Color(255, 127, 39)); addB.setForeground(Color.WHITE);
        addB.addActionListener(e -> addRecord()); inputP.add(addB);p.add(inputP, BorderLayout.SOUTH);add(p);loadData();
    }
    private JLabel lbl(String t) { JLabel l = new JLabel(t); l.setForeground(Color.WHITE); return l; }
    private JTextField txt() { JTextField t = new JTextField(); t.setBackground(Color.BLACK); t.setForeground(Color.GREEN); return t; }
    private String genPass() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        StringBuilder sb = new StringBuilder(); Random r = new Random();
        for (int i = 0; i < 12; i++) sb.append(chars.charAt(r.nextInt(chars.length()))); return sb.toString();
    }
    private void addRecord() {
        String s = siteF.getText().trim(), l = logF.getText().trim(), pr = passF.getText().trim();
        if (s.isEmpty() || l.isEmpty() || pr.isEmpty()) return;
        model.addRow(new Object[]{s, l, pr}); saveData(); siteF.setText(""); logF.setText(""); passF.setText("");
    }
    private void saveData() {
        try {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < model.getRowCount(); i++) {
                sb.append(model.getValueAt(i, 0)).append("|||").append(model.getValueAt(i, 1)).append("|||").append(model.getValueAt(i, 2)).append("\n");
            }
            byte[] enc = crypt(sb.toString().getBytes(StandardCharsets.UTF_8));
            try (FileOutputStream fos = new FileOutputStream(FILE_NAME)) { fos.write(enc); }
        } catch (Exception ignored) {}
    }
    private void loadData() {
        File f = new File(FILE_NAME); if (!f.exists()) return;
        try {
            byte[] data = new byte[(int) f.length()]; // ИСПРАВЛЕНО НА ОДНОМЕРНЫЙ МАССИВ
            try (FileInputStream fis = new FileInputStream(f)) { fis.read(data); }
            String res = new String(crypt(data), StandardCharsets.UTF_8);
            for (String line : res.split("\n")) {
                String[] parts = line.split("\\|\\|\\|"); if (parts.length == 3) model.addRow(parts);
            }
        } catch (Exception ignored) {}
    }
    private byte[] crypt(byte[] in) {
        byte[] key = masterKey.getBytes(StandardCharsets.UTF_8); byte[] out = new byte[in.length];
        for (int i = 0; i < in.length; i++) out[i] = (byte) (in[i] ^ key[i % key.length]); return out;
    }
    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new ApertureSafe().setVisible(true)); }
}
/* </ars> */
