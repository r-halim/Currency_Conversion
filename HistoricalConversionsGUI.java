import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.*;
import java.net.http.*;
import org.json.*;

public class HistoricalConversionsGUI extends JFrame {
    private JTextField fromCurrencyField, toCurrencyField, amountField, yearField, monthField, dayField;
    private JButton backButton, convertButton;

    public HistoricalConversionsGUI() {
        setTitle("Historical Currency Conversion");
        setSize(600, 250); // Increased the height for additional fields
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(8, 2)); // Adjusted rows for new fields

        JLabel fromCurrencyLabel = new JLabel("Currency converting from:");
        fromCurrencyField = new JTextField(10);

        JLabel toCurrencyLabel = new JLabel("Currency converting to:");
        toCurrencyField = new JTextField(10);

        JLabel amountLabel = new JLabel("Amount:");
        amountField = new JTextField(10);

        JLabel yearLabel = new JLabel("Historical Date (YYYY):");
        yearField = new JTextField(4);

        JLabel monthLabel = new JLabel("Historical Date (MM):");
        monthField = new JTextField(2);

        JLabel dayLabel = new JLabel("Historical Date (DD):");
        dayField = new JTextField(2);


        JLabel resultLabel = new JLabel();
        resultLabel.setHorizontalAlignment(JLabel.CENTER);

        backButton = new JButton("Back");
        convertButton = new JButton("Convert");

        addInputRow(panel, fromCurrencyLabel, fromCurrencyField);
        addInputRow(panel, toCurrencyLabel, toCurrencyField);
        addInputRow(panel, amountLabel, amountField);
        addInputRow(panel, yearLabel, yearField);
        addInputRow(panel, monthLabel, monthField);
        addInputRow(panel, dayLabel, dayField);
        panel.add(backButton);
        panel.add(convertButton);
        panel.add(resultLabel);
        add(panel);

        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fromCurrency = fromCurrencyField.getText().toUpperCase();
                String toCurrency = toCurrencyField.getText().toUpperCase();
                String amountStr = amountField.getText();
                String yearStr = yearField.getText();
                String monthStr = monthField.getText();
                String dayStr = dayField.getText();

                if (fromCurrency.isEmpty() || toCurrency.isEmpty() || amountStr.isEmpty() || yearStr.isEmpty()
                        || monthStr.isEmpty() || dayStr.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please ensure all fields are filled.");
                    return;
                }

                try {
                    double amount = Double.parseDouble(amountStr);

                    // Build the URI with user input
                    String apiURL = "https://currency-converter5.p.rapidapi.com/currency/historical/"
                            + yearStr + "-" + monthStr + "-" + dayStr + "?from=" + fromCurrency + "&to=" + toCurrency
                            + "&amount=" + amount + "&format=json";
                    URI uri = new URI(apiURL);

                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(uri)
                            .header("X-RapidAPI-Key", "API_KEY") // Add your API key here
                            .header("X-RapidAPI-Host", "currency-converter5.p.rapidapi.com")
                            .method("GET", HttpRequest.BodyPublishers.noBody())
                            .build();

                    HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                    System.out.println(response.body());

                    try {
                        // Parsing the JSON response
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
                                resultLabel.setText("Converted amount would have been:  " + rateForAmount);
                            }  else {
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
                // Closes the HistoricalConversionsGUI and opens the PostLoginGUI
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
                HistoricalConversionsGUI historicalConversionsGUI = new HistoricalConversionsGUI();
                historicalConversionsGUI.setVisible(true);
            }
        });
    }
}
