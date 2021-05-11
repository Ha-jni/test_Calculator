package com.hajni.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import javax.script.ScriptEngine;

public class MainActivity extends AppCompatActivity {
    private int openParenthesis = 0;

    TextView inputNumbers;
    private List<Integer> checkList;
    private Stack<String> operatorStack;
    private List<String> infixList;
    private List<String> postfixList;

    private boolean equalClicked = false;
    private String lastExpression = "";

    private boolean dotUsed = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.initView();
    }

    private void initView() {
        inputNumbers = findViewById(R.id.inputNumbers);
        checkList = new ArrayList<>();
        operatorStack = new Stack<>();
        infixList = new ArrayList<>();
        postfixList = new ArrayList<>();
    }

    // 숫자, 연산자 버튼 이벤트 처리
    public void btnClick(View v) {
        if (!checkList.isEmpty() && checkList.get(checkList.size() - 1) == -1) {
            inputNumbers.setText(inputNumbers.getText().toString());
            checkList.clear();
            checkList.add(1); // 정수
            checkList.add(2); // .
            checkList.add(1); // 소수점
            inputNumbers.setText("");
        }
        switch (v.getId()) {
            case R.id.btnOne:
                addNumber("1");
                break;
            case R.id.btnTwo:
                addNumber("2");
                break;
            case R.id.btnThree:
                addNumber("3");
                break;
            case R.id.btnFour:
                addNumber("4");
                break;
            case R.id.btnFive:
                addNumber("5");
                break;
            case R.id.btnSix:
                addNumber("6");
                break;
            case R.id.btnSeven:
                addNumber("7");
                break;
            case R.id.btnEight:
                addNumber("8");
                break;
            case R.id.btnNine:
                addNumber("9");
                break;
            case R.id.btnZero:
                addNumber("0");
                break;
            case R.id.btnDot:
                addDot(".");
                break;
            case R.id.btnDivision:
                addOperator("/");
                break;
            case R.id.btnPercent:
                addOperator("%");
                break;
            case R.id.btnMultiplication:
                addOperator("X");
                break;
            case R.id.btnPlus:
                addOperator("+");
                break;
            case R.id.btnMinus:
                addOperator("-");
                break;
//            case R.id.btnParentheses:
//                if (addParenthesis()) equalClicked = false;
//                break;
            case R.id.btnClear:
                inputNumbers.setText("");
                checkList.clear();
                openParenthesis = 0;
                dotUsed = false;
                equalClicked = false;
                break;
        }
    }




    void addDot(String str) {
        if (checkList.isEmpty()) {
            Toast.makeText(getApplicationContext(), ". 을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        } else if (checkList.get(checkList.size() - 1) != 1) {
            Toast.makeText(getApplicationContext(), ". 을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        for (int i = checkList.size() - 2; i >= 0; i--) {
            int check = checkList.get(i);
            if (check == 2) {
                Toast.makeText(getApplicationContext(), ". 을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (check == 0) break;
            if (check == 1) continue;
        }
        checkList.add(2);
        inputNumbers.append(str); // UI
    }


    void addNumber(String str) {
        checkList.add(1);
        inputNumbers.append(str);
    }

    void addOperator(String str) {
        if (checkList.isEmpty()) {
            Toast.makeText(getApplicationContext(), "연산자가 올 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        } else if (checkList.get(checkList.size() - 1) == 0 || checkList.get(checkList.size() - 1) == 2) {
            Toast.makeText(getApplicationContext(), "연산자가 올 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        checkList.add(0);
        inputNumbers.append(" " + str + " ");
    }

    public void equalClick(View v) {
        if (inputNumbers.length() == 0) return;
        if (checkList.get(checkList.size() - 1) != 1) {
            Toast.makeText(getApplicationContext(), "숫자를 제대로 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        Collections.addAll(infixList, inputNumbers.getText().toString().split(" "));
        checkList.add(-1);
        result();
    }

    ///이름수정
    int getPriorityt(String operator) {
        int priority = 0;
        switch (operator) {
            case "X":
            case "/":
                priority = 5;
                break;
            case "%":
                priority = 3;
                break;
            case "+":
            case "-":
                priority = 1;
                break;
        }
        return priority;
    }

    boolean isNumber(String str) {
        boolean result = true;
        try {
            Double.parseDouble(str);
        } catch (NumberFormatException e) {
            result = false;
        }
        return result;
    }

    void infixToPostfix() {
        for (String item : infixList) {
            // 피연산자
            if (isNumber(item)) postfixList.add(String.valueOf(Double.parseDouble(item)));
                // 연산자
            else {
                if (operatorStack.isEmpty()) operatorStack.push(item);
                else {
                    if (getPriorityt(operatorStack.peek()) >= getPriorityt(item))
                        postfixList.add(operatorStack.pop());
                    operatorStack.push(item);
                }
            }
        }
        while (!operatorStack.isEmpty()) postfixList.add(operatorStack.pop());
    }

    String calculate(String num1, String num2, String op) {
        double first = Double.parseDouble(num1);
        double second = Double.parseDouble(num2);
        double result = 0.0;
        switch (op) {
            case "X":
                result = first * second;
                break;
            case "/":
                result = first / second;
                break;
            case "%":
                result = first % second;
                break;
            case "+":
                result = first + second;
                break;
            case "-":
                result = first - second;
                break;
        }
        return String.valueOf(result);
    }

    // 최종 결과
    void result() {
        int i = 0;
        infixToPostfix();
        while (postfixList.size() != 1) {
            if (!isNumber(postfixList.get(i))) {
                postfixList.add(i - 2, calculate(postfixList.remove(i - 2), postfixList.remove(i - 2), postfixList.remove(i - 2)));
                i = -1;
            }
            i++;
        }
        inputNumbers.setText(postfixList.remove(0));
        infixList.clear();
    }
}