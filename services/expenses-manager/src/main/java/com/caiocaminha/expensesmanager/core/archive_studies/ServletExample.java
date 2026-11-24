package com.caiocaminha.expensesmanager.core.archive_studies;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "exampleServlet", urlPatterns = "/exampleServlet")
public class ServletExample extends HttpServlet {


    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String height = req.getParameter("height");
        res.setHeader("height", height);
        req.getRequestDispatcher("").forward(req, res);
    }

}
