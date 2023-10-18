import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.*;
import java.net.http.*;

import org.json.*;

public class CurrencyConversionGUI extends JFrame {
    private JTextField fromCurrencyField, toCurrencyField, amountField;
    private JButton backButton, convertButton;

    public CurrencyConversionGUI() {
        //GUI Setup
        setTitle("Currency Conversion");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create GUI components
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2)); // Increase the rows to make space for the buttons

        JLabel fromCurrencyLabel = new JLabel("Currency converting from:");
        fromCurrencyField = new JTextField(10);

        JLabel toCurrencyLabel = new JLabel("Currency converting to:");
        toCurrencyField = new JTextField(10);

        JLabel amountLabel = new JLabel("Amount:");
        amountField = new JTextField(10);
        
        JLabel resultLabel = new JLabel();
        resultLabel.setHorizontalAlignment(JLabel.CENTER);

        backButton = new JButton("Back");
        convertButton = new JButton("Convert");

        addInputRow(panel, fromCurrencyLabel, fromCurrencyField);
        addInputRow(panel, toCurrencyLabel, toCurrencyField);
        addInputRow(panel, amountLabel, amountField);
        panel.add(backButton);
        panel.add(convertButton);
        panel.add(resultLabel);
        add(panel);

        // Actions that run when the convert button is clicked
        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Collect user input
                String fromCurrency = fromCurrencyField.getText().toUpperCase(); //sets to uppercase to later pass into api
                String toCurrency = toCurrencyField.getText().toUpperCase(); //sets to uppercase to later pass into api
                String amountStr = amountField.getText();
        
                // Ensure that the user has provided valid input
                if (fromCurrency.isEmpty() || toCurrency.isEmpty() || amountStr.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please ensure all fields are filled.");
                    return;
                }
        
                try {
                    // Parse the amount input by the user as a double
                    double amount = Double.parseDouble(amountStr);
        
                    // Build the URI with user input
                    String apiURL = "https://currency-converter5.p.rapidapi.com/currency/convert";
                    URI uri = new URI(apiURL + "?format=json&from=" + fromCurrency + "&to=" + toCurrency + "&amount=" + amount + "&language=en");
        
                    // Create the HTTP request
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(uri)
                            .header("X-RapidAPI-Key", "API_KEY") // Replace with API key
                            .header("X-RapidAPI-Host", "currency-converter5.p.rapidapi.com")
                            .method("GET", HttpRequest.BodyPublishers.noBody())
                            .build();
        
                    // Send the request
                    HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

                    // Print the API response
                    System.out.println(response.body());

                    try {
                        // Parse the JSON response
                        JSONObject jsonObject = new JSONObject(response.body());

                        // Check if the status is "success". If successful then proceeds with parsing the JSON string received
                        String status = jsonObject.getString("status");
                        if ("success".equals(status)) {

                            // Get the rates object
                            JSONObject rates = jsonObject.getJSONObject("rates");

                            // Extract the rate_for_amount for the target currency from the JSON string
                            if (rates.has(toCurrency)) {
                                JSONObject targetCurrency = rates.getJSONObject(toCurrency);
                                String rateForAmount = targetCurrency.getString("rate_for_amount");
                                resultLabel.setText("Converted Amount: " + rateForAmount);
                            } else {
                                resultLabel.setText("Target currency not found in rates.");
                            }
                        }else if ("failed".equals(status)) {
                            resultLabel.setText("Conversion failed. Please check your input.");
                        }
                        else {
                            resultLabel.setText("Conversion failed. Please check your input.");
                        }
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "An error occurred while parsing the response.");
                    }

                } catch (NumberFormatException | URISyntaxException | IOException | InterruptedException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "An error occurred. Please verify your inputs");
                }
            }
        });

        // Action that runs when the back button is pressed
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Closes the CurrencyConversionGUI and return to the PostLoginGUI
                dispose();
                new PostLoginGUI().setVisible(true);
            }
        });
    }

    private void addInputRow(JPanel panel, JLabel label, JTextField textField) {
        panel.add(label);
        panel.add(textField);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                CurrencyConversionGUI currencyConversionGUI = new CurrencyConversionGUI();
                currencyConversionGUI.setVisible(true);
            }
        });
    }
}
