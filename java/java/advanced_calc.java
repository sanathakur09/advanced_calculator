import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

class AdvancedCalculatorSwingExample extends JFrame implements ActionListener {

    // Text field to display input and results
    JTextField textField;

    // Variables to store the first number, second number, and the result of operations
    double num1, num2, result;

    // Stores the current operator (+, -, *, /)
    String operator;

    // Stack to store the history of calculations
    private Stack<Double> history = new Stack<>();

    // Swing components for displaying the history
    private JList<String> historyList;
    private DefaultListModel<String> historyModel;
    private JScrollPane historyScrollPane;
    private JLabel historyLabel;

    // Flag to indicate if the next digit entered should clear the text field
    private boolean isNewInput = true;

    // Variables to store memory values
    private double memoryValue = 0;        // Memory used by MC, MR, M+
    private double anotherMemoryValue = 0; // Separate memory used by M-

    // Custom font and colors
    private static final Font DISPLAY_FONT = new Font("Roboto", Font.PLAIN, 30);
    private static final Font BUTTON_FONT = new Font("Montserrat", Font.PLAIN, 20);
    private static final Color BACKGROUND_COLOR = new Color(235, 235, 235);
    private static final Color BUTTON_COLOR = new Color(255, 255, 255);
    private static final Color OPERATOR_COLOR = new Color(255, 165, 0); // Gold
    private static final Color EQUALS_COLOR = new Color(65, 105, 225);   // Royal Blue
    private static final Color HISTORY_BACKGROUND = new Color(245, 245, 245);
    private static final Color HISTORY_FOREGROUND = new Color(50, 50, 50);
    private static final Color MEMORY_BUTTON_COLOR = new Color(240, 240, 240);
    private static final Color TEXT_COLOR = new Color(30, 30, 30);

    // Constructor for the AdvancedCalculatorSwingExample class
    AdvancedCalculatorSwingExample() {
        // 1. Frame Setup
        setTitle("Elegant Calculator");
        setSize(480, 600); // Adjusted height
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(BACKGROUND_COLOR);

        // 2. Text Field
        textField = new JTextField();
        textField.setBounds(30, 30, 300, 60); // Increased height
        textField.setEditable(false);
        textField.setHorizontalAlignment(JTextField.RIGHT);
        textField.setFont(DISPLAY_FONT);
        textField.setForeground(TEXT_COLOR);
        textField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200)),
                new EmptyBorder(15, 15, 15, 15)));
        add(textField);

        // 3. Number and Basic Operation Buttons
        String[] buttonLabels = {
                "7", "8", "9", "/",
                "4", "5", "6", "*",
                "1", "2", "3", "-",
                "0", ".", "=", "+"
        };

        JButton[] buttons = new JButton[16];
        int x = 30, y = 110; // Adjusted y position

        for (int i = 0; i < 16; i++) {
            buttons[i] = new JButton(buttonLabels[i]);
            buttons[i].setBounds(x, y, 70, 60); // Increased height
            buttons[i].addActionListener(this);
            buttons[i].setFont(BUTTON_FONT);
            buttons[i].setBackground(BUTTON_COLOR);
            buttons[i].setForeground(TEXT_COLOR);
            buttons[i].setFocusPainted(false);
            buttons[i].setBorder(new LineBorder(new Color(220, 220, 220)));

            if (buttonLabels[i].matches("[\\+\\-\\*/]")) {
                buttons[i].setForeground(Color.WHITE);
                buttons[i].setBackground(OPERATOR_COLOR);
                buttons[i].setBorder(new LineBorder(OPERATOR_COLOR.darker()));
            } else if (buttonLabels[i].equals("=")) {
                buttons[i].setForeground(Color.WHITE);
                buttons[i].setBackground(EQUALS_COLOR);
                buttons[i].setBorder(new LineBorder(EQUALS_COLOR.darker()));
            }

            add(buttons[i]);

            x += 80;
            if ((i + 1) % 4 == 0) {
                x = 30;
                y += 70; // Adjusted y increment
            }
        }

        // 4. Clear Button ("C")
        JButton clearBtn = new JButton("C");
        clearBtn.setBounds(30, 390, 70, 60); // Adjusted y position and height
        clearBtn.addActionListener(e -> {
            textField.setText("");
            num1 = num2 = result = 0;
            operator = null;
            isNewInput = true;
        });
        clearBtn.setFont(BUTTON_FONT);
        clearBtn.setBackground(BUTTON_COLOR);
        clearBtn.setForeground(new Color(178, 34, 34)); // Firebrick for clear
        clearBtn.setFocusPainted(false);
        clearBtn.setBorder(new LineBorder(new Color(220, 220, 220)));
        add(clearBtn);

        // 5. Square Root Button ("√")
        JButton sqrtBtn = new JButton("√");
        sqrtBtn.setBounds(120, 390, 70, 60); // Adjusted y position and height
        sqrtBtn.addActionListener(e -> {
            if (!textField.getText().isEmpty()) {
                try {
                    double num = Double.parseDouble(textField.getText());
                    if (num >= 0) {
                        textField.setText(String.valueOf(Math.sqrt(num)));
                        history.push(Math.sqrt(num));
                        updateHistoryList();
                        isNewInput = true;
                    } else {
                        textField.setText("Error: Invalid input");
                    }
                } catch (NumberFormatException ex) {
                    textField.setText("Error");
                }
            }
        });
        sqrtBtn.setFont(BUTTON_FONT);
        sqrtBtn.setBackground(BUTTON_COLOR);
        sqrtBtn.setForeground(TEXT_COLOR);
        sqrtBtn.setFocusPainted(false);
        sqrtBtn.setBorder(new LineBorder(new Color(220, 220, 220)));
        add(sqrtBtn);

        // 6. Power Button ("x²")
        JButton powerBtn = new JButton("x²");
        powerBtn.setBounds(210, 390, 70, 60); // Adjusted y position and height
        powerBtn.addActionListener(e -> {
            if (!textField.getText().isEmpty()) {
                try {
                    double num = Double.parseDouble(textField.getText());
                    textField.setText(String.valueOf(Math.pow(num, 2)));
                    history.push(Math.pow(num, 2));
                    updateHistoryList();
                    isNewInput = true;
                } catch (NumberFormatException ex) {
                    textField.setText("Error");
                }
            }
        });
        powerBtn.setFont(BUTTON_FONT);
        powerBtn.setBackground(BUTTON_COLOR);
        powerBtn.setForeground(TEXT_COLOR);
        powerBtn.setFocusPainted(false);
        powerBtn.setBorder(new LineBorder(new Color(220, 220, 220)));
        add(powerBtn);

        // 7. History Display
        historyModel = new DefaultListModel<>();
        historyList = new JList<>(historyModel);
        historyList.setFont(new Font("JetBrains Mono", Font.PLAIN, 16)); // Modern monospaced font
        historyList.setBackground(HISTORY_BACKGROUND);
        historyList.setForeground(HISTORY_FOREGROUND);
        historyScrollPane = new JScrollPane(historyList);
        historyScrollPane.setBounds(350, 30, 110, 430); // Adjusted position and height
        historyScrollPane.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200)),
                new EmptyBorder(10, 10, 10, 10)));
        add(historyScrollPane);

        historyLabel = new JLabel("History");
        historyLabel.setBounds(350, 10, 80, 20);
        historyLabel.setFont(new Font("Montserrat", Font.BOLD, 16));
        historyLabel.setForeground(new Color(80, 80, 80));
        add(historyLabel);

        // 8. Backspace Button ("←")
        JButton backspaceBtn = new JButton("←");
        backspaceBtn.setBounds(300, 390, 70, 60); // Adjusted y position and height
        backspaceBtn.addActionListener(e -> {
            String currentText = textField.getText();
            if (!currentText.isEmpty()) {
                textField.setText(currentText.substring(0, currentText.length() - 1));
            }
        });
        backspaceBtn.setFont(BUTTON_FONT);
        backspaceBtn.setBackground(BUTTON_COLOR);
        backspaceBtn.setForeground(TEXT_COLOR);
        backspaceBtn.setFocusPainted(false);
        backspaceBtn.setBorder(new LineBorder(new Color(220, 220, 220)));
        add(backspaceBtn);

        // 9. Memory Functions
        JButton memoryClearBtn = new JButton("MC");
        memoryClearBtn.setBounds(30, 460, 65, 45); // Adjusted y position and height
        memoryClearBtn.addActionListener(e -> memoryValue = 0);
        memoryClearBtn.setFont(new Font("Montserrat", Font.PLAIN, 14));
        memoryClearBtn.setBackground(MEMORY_BUTTON_COLOR);
        memoryClearBtn.setForeground(TEXT_COLOR);
        memoryClearBtn.setFocusPainted(false);
        memoryClearBtn.setBorder(new LineBorder(new Color(220, 220, 220)));
        add(memoryClearBtn);

        JButton memoryRecallBtn = new JButton("MR");
        memoryRecallBtn.setBounds(105, 460, 65, 45); // Adjusted y position and height
        memoryRecallBtn.addActionListener(e -> textField.setText(String.valueOf(memoryValue)));
        memoryRecallBtn.setFont(new Font("Montserrat", Font.PLAIN, 14));
        memoryRecallBtn.setBackground(MEMORY_BUTTON_COLOR);
        memoryRecallBtn.setForeground(TEXT_COLOR);
        memoryRecallBtn.setFocusPainted(false);
        memoryRecallBtn.setBorder(new LineBorder(new Color(220, 220, 220)));
        add(memoryRecallBtn);

        JButton memoryAddBtn = new JButton("M+");
        memoryAddBtn.setBounds(180, 460, 65, 45); // Adjusted y position and height
        memoryAddBtn.addActionListener(e -> {
            try {
                memoryValue += Double.parseDouble(textField.getText());
            } catch (NumberFormatException ex) {
                textField.setText("Error");
            }
        });
        memoryAddBtn.setFont(new Font("Montserrat", Font.PLAIN, 14));
        memoryAddBtn.setBackground(MEMORY_BUTTON_COLOR);
        memoryAddBtn.setForeground(TEXT_COLOR);
        memoryAddBtn.setFocusPainted(false);
        memoryAddBtn.setBorder(new LineBorder(new Color(220, 220, 220)));
        add(memoryAddBtn);

        JButton memorySubtractBtn = new JButton("M-");
        memorySubtractBtn.setBounds(255, 460, 65, 45); // Adjusted y position and height
        memorySubtractBtn.addActionListener(e -> {
            try {
                anotherMemoryValue -= Double.parseDouble(textField.getText());
            } catch (NumberFormatException ex) {
                textField.setText("Error");
            }
        });
        memorySubtractBtn.setFont(new Font("Montserrat", Font.PLAIN, 14));
        memorySubtractBtn.setBackground(MEMORY_BUTTON_COLOR);
        memorySubtractBtn.setForeground(TEXT_COLOR);
        memorySubtractBtn.setFocusPainted(false);
        memorySubtractBtn.setBorder(new LineBorder(new Color(220, 220, 220)));
        add(memorySubtractBtn);

        // 10. Memory Recall 2 Button (for the 'anotherMemoryValue')
        JButton memoryRecall2Btn = new JButton("MR2");
        memoryRecall2Btn.setBounds(330, 460, 65, 45); // Adjusted y position and height
        memoryRecall2Btn.addActionListener(e -> textField.setText(String.valueOf(anotherMemoryValue)));
        memoryRecall2Btn.setFont(new Font("Montserrat", Font.PLAIN, 14));
        memoryRecall2Btn.setBackground(MEMORY_BUTTON_COLOR);
        memoryRecall2Btn.setForeground(TEXT_COLOR);
        memoryRecall2Btn.setFocusPainted(false);
        memoryRecall2Btn.setBorder(new LineBorder(new Color(220, 220, 220)));
        add(memoryRecall2Btn);

        // 11. Make the frame visible
        setVisible(true);
    }

    // Method to update the history JList
    private void updateHistoryList() {
        historyModel.clear();
        List<Double> tempHistory = new ArrayList<>(history);
        for (int i = tempHistory.size() - 1; i >= 0; i--) {
            historyModel.addElement(String.valueOf(tempHistory.get(i)));
        }
    }

    // ActionListener implementation for button clicks
    @Override
    public void actionPerformed(ActionEvent e) {
        String input = ((JButton) e.getSource()).getText();

        if (input.matches("[0-9\\.]")) {
            if (isNewInput) {
                textField.setText("");
                isNewInput = false;
            }
            textField.setText(textField.getText() + input);
        } else if (input.matches("[\\+\\-\\*/]")) {
            if (!textField.getText().isEmpty()) {
                try {
                    num1 = Double.parseDouble(textField.getText());
                    operator = input;
                    isNewInput = true;
                } catch (NumberFormatException ex) {
                    textField.setText("Error");
                }
            }
        } else if (input.equals("=")) {
            try {
                num2 = Double.parseDouble(textField.getText());

                switch (operator) {
                    case "+":
                        result = num1 + num2;
                        break;
                    case "-":
                        result = num1 - num2;
                        break;
                    case "*":
                        result = num1 * num2;
                        break;
                    case "/":
                        if (num2 == 0) {
                            textField.setText("Error: Div by 0");
                            return;
                        }
                        result = num1 / num2;
                        break;
                    default:
                        return; // No operator selected
                }

                textField.setText(String.valueOf(result));
                history.push(result);
                updateHistoryList();
                isNewInput = true;
            } catch (NumberFormatException ex) {
                textField.setText("Error");
            }
        }
    }

    // Main method to create and run the calculator
    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdvancedCalculatorSwingExample::new);
    }
}          more attractive code