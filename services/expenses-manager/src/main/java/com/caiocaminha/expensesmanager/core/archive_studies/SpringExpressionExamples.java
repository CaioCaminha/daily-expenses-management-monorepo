package com.caiocaminha.expensesmanager.core.archive_studies;

import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;

public class SpringExpressionExamples {
    public static void main(String[] args) throws ParseException, EvaluationException {
        ExpressionParser parser = new SpelExpressionParser();
        Expression exp = parser.parseExpression("'Hello World'.concat('!')");
        String message = (String) exp.getValue();
        System.out.println(message); // will print out Hello World!
    }
}
