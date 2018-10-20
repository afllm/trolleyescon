/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alejandrollamas.control;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.beans.PropertyVetoException;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.dbcp2.BasicDataSource;

/**
 *
 * @author Alejandro
 */
public class json extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, ClassNotFoundException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String strJson = "{\"status\":200,\"msg\":\"antes del tray\"}";
        try {
            HttpSession sesionObj = request.getSession();
            strJson = "{\"status\":200,\"msg\":\"dentro del tray\"}";
            String strOp = request.getParameter("op");
            String usuario = "alejandro";
            String clave = "indescifrable";
            String strSecreto = "01234567899876543210";
            if (strOp != null) {
                if (!strOp.equalsIgnoreCase("")) {

                    /**
                     * 
                     * ********************INICIO HIKARI*************************
                     *
                     */
                    if (strOp.equalsIgnoreCase("connecthik")) {
                        //probando...
                        //strJson = "{\"status\":200,\"msg\":\"dentro del if de connect\"}";

                        //Conexión a la base de datos
                        try {
                            Class.forName("com.mysql.jdbc.Driver");
                        } catch (Exception ex) {
                            response.setStatus(500);
                            strJson = "{\"status\":500,\"msg\":\"jdbc driver not found\"}";
                        }
                        HikariConfig config = new HikariConfig();
                        config.setJdbcUrl("jdbc:mysql://localhost:3306/trolleyes");
                        config.setUsername("root");
                        config.setPassword("bitnami");

                        config.addDataSourceProperty("cachePrepStmts", "true");
                        config.addDataSourceProperty("prepStmtCacheSize", "250");
                        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

                        config.setMaximumPoolSize(10);
                        config.setMinimumIdle(5);
                        config.setLeakDetectionThreshold(15000);
                        config.setConnectionTestQuery("SELECT 1");
                        config.setConnectionTimeout(2000);

                        try {
                            HikariDataSource oConnectionPool = new HikariDataSource(config);
                            Connection oConnection = (Connection) oConnectionPool.getConnection();
                            strJson = "{\"status\":200,\"msg\":\"Hikari Connection OK\"}";
                            oConnection.close();
                            oConnectionPool.close();
                        } catch (Exception ex) {
                            response.setStatus(500);
                            strJson = "{\"status\":500,\"msg\":\"Bad Hikari Connection\"}";
                        }

                    }

                    /**
                     * ********************FIN HIKARI*************************
                     */
                    /**
                     * ********************INICIO C3P0*************************
                     */
                    if (strOp.equalsIgnoreCase("connectc3p")) {

                        ComboPooledDataSource cpds = new ComboPooledDataSource();
                        try {
                            cpds.setDriverClass("com.mysql.jdbc.Driver");
                        } catch (PropertyVetoException ex) {
                            Logger.getLogger(json.class.getName()).log(Level.SEVERE, null, ex);
                            response.setStatus(500);
                            strJson = "{\"status\":500,\"msg\":\"jdbc driver not found\"}";
                        }
                        cpds.setJdbcUrl("jdbc:mysql://localhost:3306/trolleyes");
                        cpds.setUser("root");
                        cpds.setPassword("bitnami");

                        cpds.setInitialPoolSize(5);
                        cpds.setMinPoolSize(5);
                        cpds.setAcquireIncrement(5);
                        cpds.setMaxPoolSize(20);
                        cpds.setMaxStatements(100);

                        Connection conn = null;

                        try {
                            conn = cpds.getConnection();
                            strJson = "{\"status\":200,\"msg\":\"c3p0 Connection OK\"}";
                            conn.close();
                            cpds.close();
                        } catch (SQLException ex) {
                            Logger.getLogger(json.class.getName()).log(Level.SEVERE, null, ex);
                            response.setStatus(500);
                            strJson = "{\"status\":500,\"msg\":\"Bad c3p0 Connection\"}";
                        }
                    }

                    /**
                     * ********************FIN C3P0*************************
                     */
                    /**
                     * ********************INICIO DBCP*************************
                     */
                    if (strOp.equalsIgnoreCase("connectdbc")) {
                        BasicDataSource bds = new BasicDataSource();
                        bds.setDriverClassName("com.mysql.jdbc.Driver");
                        bds.setUrl("jdbc:mysql://localhost:3306/trolleyes");
                        bds.setUsername("root");
                        bds.setPassword("bitnami");
                        
                        bds.setMinIdle(5);
                        bds.setMaxIdle(10);
                        bds.setMaxOpenPreparedStatements(100);
                        
                        try {
                            Connection conn = bds.getConnection();
                            strJson = "{\"status\":200,\"msg\":\"DBCP Connection OK\"}";
                            conn.close();
                            bds.close();
                        } catch (Exception ex) {
                            Logger.getLogger(json.class.getName()).log(Level.SEVERE, null, ex);
                            response.setStatus(500);
                            strJson = "{\"status\":500,\"msg\":\"Bad DBCP Connection\"}";
                        }
                    }

                    /**
                     * ********************FIN DBCP*************************
                     */
                    if (strOp.equalsIgnoreCase("login")) {
                        String strUser = request.getParameter("user");
                        String strPsw = request.getParameter("pass");
                        if (strUser != null && strPsw != null) {
                            if (strUser.equals(usuario) && strPsw.equals(clave)) {
                                sesionObj.setAttribute("sesionVar", strUser);
                                response.setStatus(200);
                                strJson = "{\"status\":200,\"msg\":\"Bienvenido " + strUser + "\"}";
                            } else {
                                response.setStatus(401);
                                strJson = "{\"status\":401,\"msg\":\"Error de autenticación\"}";
                            }
                        } else {
                            response.setStatus(500);
                            strJson = "{\"status\":500,\"msg\":\"Ingrese con su usuario y contraseña, por favor\"}";
                        }

                        if (strUser.equals(usuario) && strPsw.equals(clave)) {
                            sesionObj.setAttribute("sesionVar", strUser);
                            response.setStatus(200);
                            strJson = "{\"status\":200,\"msg\":\"Bienvenido " + strUser + "\"}";
                        } else {
                            response.setStatus(401);
                            strJson = "{\"status\":401,\"msg\":\"Error de autenticación\"}";
                        }
                    }

                    if (strOp.equalsIgnoreCase("logout")) {
                        String strNombre = (String) sesionObj.getAttribute("sesionVar");
                        if (strNombre != null) {
                            sesionObj.invalidate();
                            response.setStatus(200);
                            strJson = "{\"status\":200,\"msg\":\"Se ha cerrado la sesión\"}";
                        } else {
                            response.setStatus(401);
                            strJson = "{\"status\":401,\"msg\":\"No está logeado; no se puede hacer logout\"}";
                        }
                    }

                    if (strOp.equalsIgnoreCase("check")) {
                        String strNombre = (String) sesionObj.getAttribute("sesionVar");
                        if (strNombre != null) {
                            response.setStatus(200);
                            strJson = "{\"status\":200,\"msg\":\"Esta logeado como: " + strNombre + "\"}";
                        } else {
                            response.setStatus(401);
                            strJson = "{\"status\":401,\"msg\":\"No está logeado\"}";
                        }
                    }

                    if (strOp.equalsIgnoreCase("secret")) {
                        String strNombre = (String) sesionObj.getAttribute("sesionVar");
                        if (strNombre != null) {
                            response.setStatus(200);
                            strJson = "{\"status\":200,\"msg\":\"Solo para tus ojos: " + strSecreto + "\"}";
                        } else {
                            response.setStatus(401);
                            strJson = "{\"status\":401,\"msg\":\"No está logeado; no hay nada que mostrar\"}";
                        }
                    }

                    if (strOp.equalsIgnoreCase("reset")) {
                        String strNombre = (String) sesionObj.getAttribute("sesionVar");
                        if (strNombre != null) {
                            response.setStatus(200);
                            strJson = "{\"status\":200,\"msg\":\"Formulario limpio. Esta logeado como: " + strNombre + "\"}";
                        } else {
                            response.setStatus(401);
                            strJson = "{\"status\":401,\"msg\":\"Formulario limpio. No está logeado\"}";
                        }

                    }
                }
            } else {
                response.setStatus(500);
                strJson = "{\"status\":500,\"msg\":\"Introduzca sus datos y/o pulse una opción\"}";
            }

            response.getWriter().append(strJson);

        } finally {
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(json.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(json.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
