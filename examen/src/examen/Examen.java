package examen;

import com.sun.org.apache.xml.internal.security.utils.Base64;
import java.security.MessageDigest;
import java.sql.*;
import java.util.Arrays;
import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


/**
 *
 * @author Joe Pinargote && Andry Cedeño
 */

public class Examen {

    String KEY = "seguridaddelainformacion";
    Connection db = null;
    String sql;
    Statement st = null;
    ResultSet rs = null;
    PreparedStatement pst=null;

    public static void main(String[] args) throws SQLException {
        String encriptada = "";
        String aEncriptar = "";
        String encriptado = "";
        int id = 0;
        Scanner entrada= new Scanner (System.in);
        Examen examen = new Examen();
        //aEnccriptar = JOptionPane.showInputDialog("Ingresa la cadena a encriptar: ");
        System.out.println("ingrese el texto a encriptar");
        aEncriptar = entrada.nextLine();
        encriptada = examen.Encriptar(aEncriptar);
        examen.mostrar_todos();
        System.out.println("ingrese el id de la cadena que quiera desencriptar");
        id = entrada.nextInt();
        encriptado = examen.buscar_uno(id);
        //System.out.println(examen.Desencriptar(encriptado));
        System.out.println("el dato "+encriptado+" desencriptado es: "+ examen.Desencriptar(encriptado));
        
        //JOptionPane.showMessageDialog(null, encriptada);
        //JOptionPane.showMessageDialog(null, examen.Desencriptar(encriptada));
        
    }

    // Clave de encriptación / desencriptación
    public SecretKeySpec CrearClave(String llave) {
        try {
            byte[] cadena = llave.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            cadena = md.digest(cadena);
            cadena = Arrays.copyOf(cadena, 16);
            SecretKeySpec secretKeySpec = new SecretKeySpec(cadena, "AES");
            return secretKeySpec;
        } catch (Exception e) {
            return null;
        }

    }

    // Encriptar
    public String Encriptar(String encriptar) {
     conecciondb();
        try {
            SecretKeySpec secretKeySpec = CrearClave(KEY);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            
            byte [] cadena = encriptar.getBytes("UTF-8");
            byte [] encriptada = cipher.doFinal(cadena);
            String cadena_encriptada = Base64.encode(encriptada);
            
            sql = "insert into dato(dato_encriptado) values (?)";
            pst = db.prepareStatement(sql);
            pst.setString(1,cadena_encriptada);
            pst.executeUpdate();
            return cadena_encriptada;
            
            
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "";
        }
    }

    // Des-encriptación
     public String Desencriptar(String desencriptar) {
     
        try {
            SecretKeySpec secretKeySpec = CrearClave(KEY);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            
            byte [] cadena = Base64.decode(desencriptar);
            byte [] desencriptacioon = cipher.doFinal(cadena);
            String cadena_desencriptada = new String(desencriptacioon);
            return cadena_desencriptada;
            
        } catch (Exception e) {
            return "";
        }
    }
     public String buscar_uno (int dato) throws SQLException{
         st = db.createStatement();
         rs = st.executeQuery("select * from dato where id = '"+dato+"'");
         
         if(rs.next()){             
             return rs.getString(2);
         }else{
             System.out.println("no existe dato");
             return "";
         }
     }
     
     public void mostrar_todos () throws SQLException{
         st = db.createStatement();
         rs = st.executeQuery("select * from dato");
         System.out.print("id");
         System.out.println(" dato");
         
             while(rs.next()){
             System.out.print(rs.getString(1)+"  ");
             System.out.println(rs.getString(2));
         }
         
         
     }
     
public void conecciondb() {
  try {
      db=DriverManager.getConnection("jdbc:postgresql://localhost:1024/examen", "postgres", "joenopuede");
      System.out.println("base de datos conectada exitosamente");
      } catch (SQLException e) {System.out.println("Ocurrio un error : "+e.getMessage());} }

}
