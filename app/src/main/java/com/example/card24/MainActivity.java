package com.example.card24;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.singularsys.jep.EvaluationException;
import com.singularsys.jep.Jep;
import com.singularsys.jep.ParseException;

import java.util.Arrays;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    Button rePick;
    Button checkInput;
    Button clear;
    Button left;
    Button right;
    Button plus;
    Button minus;
    Button multiply;
    Button divide;
    TextView expression;
    ImageButton[] cards;


    int[] data;
    int[] card;
    int[] imageCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cards = new ImageButton[4];
        cards[0] = findViewById(R.id.card1);
        cards[0].setOnClickListener((view) -> {
            clickCard(0);
            this.enableCorrectButtons();
        });
        cards[1] = findViewById(R.id.card2);
        cards[1].setOnClickListener((view) -> {
            clickCard(1);
            this.enableCorrectButtons();

        });
        cards[2] = findViewById(R.id.card3);
        cards[2].setOnClickListener((view) -> {
            clickCard(2);
            this.enableCorrectButtons();
        });

        cards[3] = findViewById(R.id.card4);
        cards[3].setOnClickListener((view) -> {
            clickCard(3);
            this.enableCorrectButtons();
        });

        imageCount = new int[4];
        rePick = (Button)findViewById(R.id.repick);
        rePick.setOnClickListener(view -> pickCard());
        checkInput = (Button)findViewById(R.id.checkinput);
        checkInput.setOnClickListener(view -> {
            if (Arrays.binarySearch(imageCount, 0) == -1) {
                this.evaluateInput();
            } else {
                Toast.makeText(MainActivity.this, "Select all 4 cards", Toast.LENGTH_SHORT).show();
            }
        });
        left = (Button)findViewById(R.id.left);
        left.setOnClickListener(view -> {
            expression.append("(");
            this.enableCorrectButtons();
        });
        right = (Button)findViewById(R.id.right);
        right.setOnClickListener(view -> {
            expression.append(")");
            this.enableCorrectButtons();
        });
        plus = (Button)findViewById(R.id.plus);
        plus.setOnClickListener(view -> {
            expression.append("+");
            this.enableCorrectButtons();
        });
        minus = (Button)findViewById(R.id.minus);
        minus.setOnClickListener(view -> expression.append("-"));
        multiply = (Button)findViewById(R.id.multiply);
        multiply.setOnClickListener(view -> expression.append("*"));
        divide = (Button)findViewById(R.id.divide);
        divide.setOnClickListener(view -> expression.append("/"));
        clear = (Button)findViewById(R.id.clear);
        clear.setOnClickListener(view -> {
            setClear();
            this.enableCorrectButtons();
        });
        expression = (TextView)findViewById(R.id.input);

        pickCard();

    }

    private void clickCard(int i) {
        int resID;
        String num;
        int value;
        if (imageCount[i] == 0) {
            resID = getResources().getIdentifier("back_0", "drawable", "com.example.card24");
            cards[i].setImageResource(resID);
            cards[i].setClickable(false);
            value = data[i];
            num = String.valueOf(value);
            expression.append(num);
            imageCount[i]++;
            for (int j = 0; j < 4; j++) {
                //cards are disabled after card is selected
                cards[j].setEnabled(false);
            }
        }
    }


    private void pickCard() {
        Random random = new Random();
        data = new int[4];
        card = new int[4];
        for (int i = 0; i < 4; i++) {
            int selectedCard = random.nextInt(52 - 1) + 1;
            card[i] = selectedCard;
            selectedCard = selectedCard % 13;
            if (selectedCard == 0) selectedCard = 13;
            data[i] = selectedCard;
        }
        setClear();
    }

    private void setClear() {
        int resID;
        expression.setText("");
        for (int i = 0; i < 4; i++) {
            imageCount[i] = 0;
            resID = getResources().getIdentifier("card" + card[i], "drawable", "com.example.card24");
            cards[i].setImageResource(resID);
            this.enableCorrectButtons();

            cards[i].setClickable(true);
        }
    }

    private boolean checkInput(String input) {
        Jep jep = new Jep();
        Object res;
        try {
            jep.parse(input);
            res = jep.evaluate();
        } catch (ParseException | EvaluationException ex) {
            ex.printStackTrace();
            Toast.makeText(MainActivity.this, "Wrong Expression", Toast.LENGTH_SHORT).show();
            return false;
        }
        Double ca = (Double) res;
        return Math.abs(ca - 24) < 1e-6;
    }

    private void enableCorrectButtons() {
        boolean canSelectCards = true;
        boolean canSelectSymbols = false;
        if (expression.length() > 0) {
            char lastChar = expression.getText().charAt(expression.length() - 1);
            boolean lastCharIsSymbol = lastChar == '+' || lastChar == '-' || lastChar == '/' || lastChar == '*';
            canSelectCards = (expression.length() == 0) || lastCharIsSymbol || lastChar == '(';
            canSelectSymbols = (expression.length() != 0) && (Character.isDigit(lastChar) || lastChar == ')');
        }
        if (canSelectCards) {
            this.enableCardsAndOpen(true);
            this.enableSymbolsAndClose(false);
        }
        if (canSelectSymbols) {
            this.enableCardsAndOpen(false);
            this.enableSymbolsAndClose(true);
        }

    }

    private void enableCardsAndOpen(boolean enabled) {
        for (int i = 0; i < 4; i++) {
            cards[i].setEnabled(enabled);
        }
        left.setEnabled(enabled);
    }

    private void enableSymbolsAndClose(boolean enabled) {
        right.setEnabled(enabled);
        plus.setEnabled(enabled);
        minus.setEnabled(enabled);
        multiply.setEnabled(enabled);
        divide.setEnabled(enabled);
    }

    private void evaluateInput() {
        String inputString = expression.getText().toString();;
        if (checkInput(inputString)) {
            Toast.makeText(MainActivity.this, "Correct Answer", Toast.LENGTH_SHORT).show();
            pickCard();
        } else {
            Toast.makeText(MainActivity.this, "Wrong Answer", Toast.LENGTH_SHORT).show();
            setClear();
        }
    }


}