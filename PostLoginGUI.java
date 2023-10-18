import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PostLoginGUI extends JFrame {
    public PostLoginGUI() {
        setTitle("Welcome to the Currency Conversion Dashboard");
        setSize(500, 100); // Reduce the height to make the buttons smaller
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create GUI components
        JPanel buttonPanel = new JPanel(new FlowLayout()); // Use FlowLayout for side-by-side buttons
        JButton currencyConversionButton = new JButton("Currency Conversion");
        JButton historicalButton = new JButton("Historical Rates");

        buttonPanel.add(currencyConversionButton);
        buttonPanel.add(historicalButton);

        add(buttonPanel, BorderLayout.CENTER);
        
        currencyConversionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                // Open the CurrencyConversionGUI when the button is clicked
                CurrencyConversionGUI currencyConversionGUI = new CurrencyConversionGUI();
                currencyConversionGUI.setVisible(true);
            }
        });

        historicalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open the CurrencyConversionGUI when the button is clicked
                HistoricalConversionsGUI historicalConversionsGUI = new HistoricalConversionsGUI();
                historicalConversionsGUI.setVisible(true);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                PostLoginGUI postLoginGUI = new PostLoginGUI();
                postLoginGUI.setVisible(true);
            }
        });
    }
}
