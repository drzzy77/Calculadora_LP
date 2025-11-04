import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class Calculadora {
    int boardwith = 360;
    int boardheight = 540;

    //PALETA
    Color customWhite = new Color(255, 255, 255); // Fundo do display e Números
    Color customPinkLight = new Color(255, 192, 203); 
    Color customPinkDark = new Color(255, 105, 180); 
    Color customBlack = new Color(0, 0, 0);

    // Config da Fonte 
    String FONT_NAME = "Verdana"; 
    int DISPLAY_FONT_SIZE = 70;
    int BUTTON_FONT_SIZE = 28;  
    String[] buttonValues = {
        "AC", "+/-", "%", "/", 
        "7", "8", "9", "x",
        "4", "5", "6", "-",
        "1", "2", "3", "+",
        "0", ".", "√", "=",
    };

    String[] operatorSymbols = {"/", "x", "-", "+", "="}; 
    String[] topSymbols = {"AC", "+/-", "%", "√"}; 


    JFrame frame = new JFrame("Calculadora Alex tema barbie girl");
    JLabel displayJLabel = new JLabel();
    JPanel displayJPanel = new JPanel();
    JPanel buttonsPanel = new JPanel();

 
    String currentNumber = "0"; 
    String A = null; 
    String operator = null; 
    boolean waitingForB = false; 

    public Calculadora() {
        frame.setSize(boardwith, boardheight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

       
        displayJLabel.setBackground(customWhite); 
        displayJLabel.setForeground(customBlack); 
   
        displayJLabel.setFont(new Font(FONT_NAME, Font.BOLD, DISPLAY_FONT_SIZE)); 
        displayJLabel.setHorizontalAlignment(JLabel.RIGHT);
        displayJLabel.setText(currentNumber);
        displayJLabel.setOpaque(true);

        displayJPanel.setLayout(new BorderLayout());
        displayJPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); 
        displayJPanel.add(displayJLabel);
        frame.add(displayJPanel, BorderLayout.NORTH);


        buttonsPanel.setLayout(new GridLayout(5, 4, 3, 3)); 
   
        buttonsPanel.setBackground(customPinkDark); 
        frame.add(buttonsPanel);

      
        for (String buttonValue : buttonValues) {
            JButton button = new JButton(buttonValue); 
  
            button.setFont(new Font(FONT_NAME, Font.BOLD, BUTTON_FONT_SIZE)); 
            button.setFocusable(false);
   
            button.setBorder(new LineBorder(customPinkDark, 1)); 

            if (Arrays.asList(topSymbols).contains(buttonValue)) {
                // AC, +/-, %: Rosa Claro
                button.setBackground(customPinkLight);
                button.setForeground(customBlack); 
            } else if (Arrays.asList(operatorSymbols).contains(buttonValue)) {
                // Operadores e Igual (=): Rosa Choque
                button.setBackground(customPinkDark);
                button.setForeground(customWhite); 
            } else {
                // Números e Ponto (.): Branco
                button.setBackground(customWhite);
                button.setForeground(customBlack); 
            }

            buttonsPanel.add(button);

            // Anexando o Action Listener
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    buttonClicked(buttonValue); 
                }
            });
        }
        
        frame.setVisible(true); 
    }

    private void buttonClicked(String buttonValue) {
        if ("0123456789".contains(buttonValue) || buttonValue.equals(".")) {
            handleNumberAndDecimal(buttonValue);
            return;
        }

        if (Arrays.asList(topSymbols).contains(buttonValue)) {
            handleSpecialFunction(buttonValue);
            return;
        }

        if (Arrays.asList(operatorSymbols).contains(buttonValue)) {
            handleOperator(buttonValue);
            return;
        }
    }

    private void handleNumberAndDecimal(String value) {
        if (waitingForB) {
            currentNumber = value.equals(".") ? "0." : value;
            waitingForB = false;
        } else {
            if (value.equals(".")) {
                if (!currentNumber.contains(".")) {
                    currentNumber += value;
                }
            } else {
                if (currentNumber.equals("0")) {
                    currentNumber = value;
                } else {
                    currentNumber += value;
                }
            }
        }
        displayJLabel.setText(currentNumber);
    }
    
    private void handleSpecialFunction(String value) {
        double numDisplay;
        
        try {
            numDisplay = Double.parseDouble(currentNumber);
        } catch (NumberFormatException e) {
            clearAll(); 
            return;
        }

        switch (value) {
            case "AC":
                clearAll();
                return;
            case "+/-":
                numDisplay *= -1;
                break;
            case "%":
                numDisplay /= 100;
                break;
            case "√":
                if (numDisplay >= 0) {
                    numDisplay = Math.sqrt(numDisplay);
                } else {
                    displayJLabel.setText("Error"); 
                    clearState();
                    return;
                }
                break;
            default:
                return;
        }
        
        currentNumber = removeZeroDecimal(numDisplay);
        displayJLabel.setText(currentNumber);
    }

    private void handleOperator(String newOperator) {
        if (newOperator.equals("=")) {
            if (A != null && operator != null && !waitingForB) {
                double result = 0;
                try {
                    result = performCalculation(Double.parseDouble(A), operator, Double.parseDouble(currentNumber));
                } catch (NumberFormatException e) {
                    result = Double.NaN;
                }
                
                A = null; 
                operator = null; 
                currentNumber = removeZeroDecimal(result); 
                waitingForB = true; 
            }
        } else { 
            if (A != null && operator != null && !waitingForB) {
                double result = 0;
                 try {
                    result = performCalculation(Double.parseDouble(A), operator, Double.parseDouble(currentNumber));
                } catch (NumberFormatException e) {
                    result = Double.NaN;
                }
                
                A = removeZeroDecimal(result);
                operator = newOperator;
                currentNumber = A;
            } else {
                A = currentNumber;
                operator = newOperator;
            }
            waitingForB = true; 
        }
        displayJLabel.setText(currentNumber);
    }
    
    private double performCalculation(double numA, String op, double numB) {
        switch (op) {
            case "+": return numA + numB;
            case "-": return numA - numB;
            case "x": return numA * numB;
            case "/": 
                if (numB == 0) {
                    displayJLabel.setText("Div/0");
                    clearState();
                    return Double.NaN;
                }
                return numA / numB;
            default: return numB;
        }
    }

    void clearAll() {
        currentNumber = "0";
        A = null;
        operator = null;
        waitingForB = false;
        displayJLabel.setText("0");
    }
    
    void clearState() {
        A = null;
        operator = null;
        waitingForB = false;
        currentNumber = "0";
    }

    String removeZeroDecimal(double numDisplay) {
        if (Double.isNaN(numDisplay) || Double.isInfinite(numDisplay)) {
            clearState();
            return "Error";
        }
        
        if (numDisplay % 1 == 0) {
            String s = String.format("%.0f", numDisplay);
            if (s.length() > 9) return s.substring(0, 9);
            return s;
        }
        
        String s = String.valueOf(numDisplay);
        if (s.length() > 10) {
            return s.substring(0, 10);
        }
        return s;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Calculadora::new);
    }
}