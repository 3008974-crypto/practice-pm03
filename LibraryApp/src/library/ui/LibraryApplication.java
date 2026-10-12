package library.ui;

import java.awt.*;
import java.util.Random;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class LibraryApplication extends JFrame {

    private final JTextField searchField = new JTextField(25);
    private final JLabel statusLabel = new JLabel(" ");
    private final JTable booksTable = new JTable();
    private final DefaultTableModel catalogModel;
    private final TableRowSorter<DefaultTableModel> catalogSorter;

    public LibraryApplication() {
        super("Зимняя библиотека — электронный каталог");

        String[] columns = {"Название", "Автор", "Год", "Статус"};
        Object[][] books = {
            {"Капитанская дочка", "А. С. Пушкин", 1836, "Доступна"},
            {"Мертвые души", "Н. В. Гоголь", 1842, "Доступна"},
            {"Преступление и наказание", "Ф. М. Достоевский", 1866, "Доступна"},
            {"Война и мир", "Л. Н. Толстой", 1869, "Доступна"},
            {"Вишневый сад", "А. П. Чехов", 1904, "Доступна"},
            {"Мастер и Маргарита", "М. А. Булгаков", 1966, "Доступна"}
        };

        catalogModel = new DefaultTableModel(books, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                return column == 2 ? Integer.class : String.class;
            }
        };

        catalogSorter = new TableRowSorter<>(catalogModel);
        booksTable.setModel(catalogModel);
        booksTable.setRowSorter(catalogSorter);
        booksTable.setRowHeight(32);
        booksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        booksTable.getTableHeader().setReorderingAllowed(false);
        booksTable.getColumnModel().getColumn(0).setPreferredWidth(300);
        booksTable.getColumnModel().getColumn(1).setPreferredWidth(220);
        booksTable.getColumnModel().getColumn(2).setPreferredWidth(70);
        booksTable.getColumnModel().getColumn(3).setPreferredWidth(100);

        JButton searchButton = new JButton("Найти");
        JButton showAllButton = new JButton("Показать все");
        JButton borrowButton = new JButton("Выдать книгу");
        JButton returnButton = new JButton("Вернуть книгу");

        searchButton.addActionListener(event -> searchBooks());
        searchField.addActionListener(event -> searchBooks());
        showAllButton.addActionListener(event -> showAllBooks());
        borrowButton.addActionListener(event -> changeBookStatus(true));
        returnButton.addActionListener(event -> changeBookStatus(false));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Название или автор:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(showAllButton);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionsPanel.add(borrowButton);
        actionsPanel.add(returnButton);

        JPanel bottomPanel = new JPanel(new BorderLayout(0, 8));
        bottomPanel.add(actionsPanel, BorderLayout.NORTH);
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);

        JPanel catalogPanel = new JPanel(new BorderLayout(0, 12));
        catalogPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        catalogPanel.add(searchPanel, BorderLayout.NORTH);
        catalogPanel.add(new JScrollPane(booksTable), BorderLayout.CENTER);
        catalogPanel.add(bottomPanel, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(new WinterHeader(), BorderLayout.NORTH);
        add(catalogPanel, BorderLayout.CENTER);

        statusLabel.setText("В каталоге книг: " + catalogModel.getRowCount()
                + ". Выберите книгу для выдачи или возврата.");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(920, 580);
        setMinimumSize(new Dimension(850, 480));
        setLocationRelativeTo(null);
    }

    private void searchBooks() {
        String query = searchField.getText().trim();

        if (query.isEmpty()) {
            catalogSorter.setRowFilter(null);
        } else {
            String pattern = "(?iu)" + java.util.regex.Pattern.quote(query);
            catalogSorter.setRowFilter(RowFilter.regexFilter(pattern, 0, 1));
        }

        statusLabel.setText("Найдено книг: " + booksTable.getRowCount());
    }

    private void showAllBooks() {
        searchField.setText("");
        catalogSorter.setRowFilter(null);
        statusLabel.setText("В каталоге книг: " + catalogModel.getRowCount());
    }

    private void changeBookStatus(boolean borrow) {
        int selectedRow = booksTable.getSelectedRow();

        if (selectedRow < 0) {
            statusLabel.setText("Сначала выберите книгу в таблице.");
            return;
        }

        int modelRow = booksTable.convertRowIndexToModel(selectedRow);
        String title = catalogModel.getValueAt(modelRow, 0).toString();
        String currentStatus = catalogModel.getValueAt(modelRow, 3).toString();

        if (borrow && currentStatus.equals("Выдана")) {
            statusLabel.setText("Книга «" + title + "» уже выдана.");
            return;
        }

        if (!borrow && currentStatus.equals("Доступна")) {
            statusLabel.setText("Книга «" + title + "» уже находится в библиотеке.");
            return;
        }

        catalogModel.setValueAt(borrow ? "Выдана" : "Доступна", modelRow, 3);
        statusLabel.setText("Книга «" + title + "» "
                + (borrow ? "выдана читателю." : "возвращена в библиотеку."));
    }

    private static class WinterHeader extends JPanel {

        private final int[] snowX = new int[45];
        private final int[] snowY = new int[45];
        private final int[] snowSpeed = new int[45];
        private final Random random = new Random();
        private final Timer animationTimer;

        WinterHeader() {
            setPreferredSize(new Dimension(900, 130));

            for (int index = 0; index < snowX.length; index++) {
                snowX[index] = random.nextInt(900);
                snowY[index] = random.nextInt(130);
                snowSpeed[index] = 1 + random.nextInt(3);
            }

            animationTimer = new Timer(35, event -> {
                int width = Math.max(1, getWidth());
                int height = Math.max(1, getHeight());

                for (int index = 0; index < snowX.length; index++) {
                    snowY[index] += snowSpeed[index];

                    if (snowY[index] > height) {
                        snowY[index] = 0;
                        snowX[index] = random.nextInt(width);
                    }

                    if (snowX[index] >= width) {
                        snowX[index] = random.nextInt(width);
                    }
                }

                repaint();
            });
        }

        @Override
        public void addNotify() {
            super.addNotify();
            animationTimer.start();
        }

        @Override
        public void removeNotify() {
            animationTimer.stop();
            super.removeNotify();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D canvas = (Graphics2D) graphics.create();
            canvas.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            canvas.setPaint(new GradientPaint(
                    0, 0, new Color(20, 50, 95),
                    getWidth(), getHeight(), new Color(45, 110, 155)));
            canvas.fillRect(0, 0, getWidth(), getHeight());

            canvas.setColor(new Color(255, 255, 255, 180));
            for (int index = 0; index < snowX.length; index++) {
                int x = snowX[index];
                int y = snowY[index];
                canvas.drawLine(x - 3, y, x + 3, y);
                canvas.drawLine(x, y - 3, x, y + 3);
                canvas.drawLine(x - 2, y - 2, x + 2, y + 2);
                canvas.drawLine(x - 2, y + 2, x + 2, y - 2);
            }

            canvas.setColor(new Color(245, 250, 255));
            canvas.fillRoundRect(25, 35, 88, 62, 10, 10);
            canvas.setColor(new Color(35, 85, 130));
            canvas.setStroke(new BasicStroke(2));
            canvas.drawLine(69, 40, 69, 92);
            for (int y = 49; y <= 79; y += 10) {
                canvas.drawLine(35, y, 60, y);
                canvas.drawLine(78, y, 103, y);
            }

            canvas.setColor(Color.WHITE);
            canvas.setFont(new Font("SansSerif", Font.BOLD, 28));
            canvas.drawString("Зимняя библиотека", 140, 57);
            canvas.setFont(new Font("SansSerif", Font.PLAIN, 17));
            canvas.drawString("Электронный каталог • Поиск • Выдача • Возврат",
                    140, 88);
            canvas.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibraryApplication().setVisible(true));
    }
}

