/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Teste;


import java.sql.*;
import DataBase.Oracle.*;

/**
 *
 * @author victor
 */
public class teste {

   public static void main(String[] args)
   {
       try
       {
         new BDConnection("10.221.0.15","ELET","o_ept","naosei_own");     
         Connection conn = OracleConnection.obtemConexao();
           
         CallableStatement callStatement = conn.prepareCall(
             "{call O_EPT.PKG_REL.PR_REL_HIS( ?,?,?,?,?,?,? ) }"
          );
        callStatement.setString(1, "044784");
          callStatement.setString(2, "EF");
          callStatement.setString(3, null);
          callStatement.setString(4, "17/10/2008");
          callStatement.setString(5, "134321");
          callStatement.setString(6, "2004//00731");
          callStatement.setString(7, "O_WEB");    
          
          ResultSet callResult = callStatement.executeQuery();
          System.out.println("Passou "+callResult); 
          
          callResult.close();
          callStatement.close();
   
       }
       catch(Exception e)
       {
           e.printStackTrace();
       }
   }
   
}
