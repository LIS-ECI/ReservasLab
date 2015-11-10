package edu.eci.pdsw.labadm.test;

import edu.eci.pdsw.labadm.entities.SistemaOperativo;
import edu.eci.pdsw.labadm.entities.Solicitud;
import edu.eci.pdsw.labadm.persistence.PersistenceException;
import edu.eci.pdsw.labadm.services.ServicesFacade;
import edu.eci.pdsw.labadm.services.ServicesFacadeException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Assert;
import static org.junit.Assert.fail;
import org.junit.Before;

import org.junit.Test;

/**
 * CLASES DE EQUIVALENCIA:
 * Todas las solicitudes realicidas deben tener como respuesta null.
 * Todas las solicitudes deben contener el email y las dos paginas web con un formato correcto.
 * Las solicitudes deben hacerse a un laboratorio con el sistema operativo indicado.
 * Todas las solicitudes deben tener fecha de radicación.
 **/

public class AppTest {

@Before
    public void setUp() {
    }

    @After
    public void clearDB() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:file:./target/db/testdb;MODE=MYSQL", "sa", "");
        Statement stmt = conn.createStatement();
        stmt.execute("delete from SOLICITUD");
        stmt.execute("delete from SISTEMA_OPERATIVO");
        stmt.execute("delete from LABORATORIO");
        stmt.execute("delete from USUARIO");
        stmt.execute("delete from SOFTWARE");
        stmt.execute("delete from Laboratorio_sistema_operativo");
        stmt.execute("delete from software_laboratorio");
        conn.commit();
        conn.close();
    }
  
  @Test
  public void emailOPaginaIncorrectosTest() {
      
      Connection conn;
    try {
        conn = DriverManager.getConnection("jdbc:h2:file:./target/db/testdb;MODE=MYSQL", "sa", "");
        Statement stmt = conn.createStatement();
        stmt.execute("INSERT INTO LABORATORIO (ID_laboratorio, nombre, cantidad_equipos, videobeam) values(1,'Ingenieria De Software',20, true)");
        stmt.execute("INSERT INTO SISTEMA_OPERATIVO (ID_sistema_operativo, nombre, version, Solicitud_id) values(1,'Windows','8.1', 1)");
        conn.commit();
    } catch (SQLException ex) {
        Logger.getLogger(AppTest.class.getName()).log(Level.SEVERE, null, ex);
    }
      
      ServicesFacade sf = ServicesFacade.getInstance("h2-applicationconfig.properties");
      boolean posible=true;
      Solicitud s = new Solicitud();
      s.setSoftware("DevC++");
      s.setLink_descarga("http://dev-c.softonic.com/");
      s.setLink_licencia("licencia");
      SistemaOperativo so = new SistemaOperativo("Windows", "8.1");
      s.setSo(so);
      try{
        sf.saveSolicitud(s);
      }catch(ServicesFacadeException bs){
         if(bs.getCause().equals("PROBLEMA_BASE_DATOS")){
            posible =false;
         }
       }
      Assert.assertFalse(posible);
  }
  
  @Test
  public void autoGeneraFechaRadicacionTest(){
       Connection conn;
    try {
        conn = DriverManager.getConnection("jdbc:h2:file:./target/db/testdb;MODE=MYSQL", "sa", "");
        Statement stmt = conn.createStatement();
        stmt.execute("INSERT INTO LABORATORIO (ID_laboratorio, nombre, cantidad_equipos, videobeam) values(1,'Ingenieria De Software',20, true)");
        stmt.execute("INSERT INTO USUARIO (ID_usuario, nombre, email, tipo_usuario) values (1,'camilo', 'camilo@hotmail.com', 1)");
        stmt.execute("INSERT INTO SISTEMA_OPERATIVO (ID_sistema_operativo, nombre, version) values(1,'Windows','8.1')");
        stmt.execute("INSERT INTO SOLICITUD (ID_solicitud, Laboratorio_id, Software, Link_licencia, Link_descarga, Estado, Fecha_posible_instalacion, Fecha_respuesta, Justificacion, Usuario_id, SISTEMA_OPERATIVO_ID_sistema_operativo) values (1,1,'openmaint','http//:www.openmaint.com','http//:www.openmaint.com/descargas',null,null,null,null,1,1)");
        conn.commit();
    } catch (SQLException ex) {
        Logger.getLogger(AppTest.class.getName()).log(Level.SEVERE, null, ex);
    }
        ServicesFacade sf = ServicesFacade.getInstance("h2-applicationconfig.properties");
        Date d = new Date();
        boolean fine= true;
        ArrayList<Solicitud> solicitudes = sf.loadAllSolicitud();
        for (Solicitud s : solicitudes) {
          if(!s.getFecha_rad().equals(d) && fine){
              fine= false;
          }
      }
        Assert.assertTrue(fine);
        
  }
  
  @Test
  public void laboratoriosConSistemaOperativoTest(){
      Connection conn;
      boolean fine = true;
        try {
            conn = DriverManager.getConnection("jdbc:h2:file:./target/db/testdb;MODE=MYSQL", "sa", "");
            Statement stmt = conn.createStatement();
            stmt.execute("INSERT INTO LABORATORIO (ID_laboratorio, nombre, cantidad_equipos, videobeam) values(1,'Ingenieria De Software',20, true)");
            stmt.execute("INSERT INTO USUARIO (ID_usuario, nombre, email, tipo_usuario) values(1,'Tatiana','tatiana@mail.com', 1)");      
            stmt.execute("INSERT INTO SISTEMA_OPERATIVO (ID_sistema_operativo, nombre, version, Solicitud_id) values(2,'Windows','8.1', 1)");
            stmt.execute("INSERT INTO SOLICITUD (ID_solicitud, Laboratorio_id, Software, Link_licencia, Link_descarga, Estado, Fecha_posible_instalacion, Fecha_respuesta, Justificacion, Usuario_id, SISTEMA_OPERATIVO_ID_sistema_operativo)"
                    + " values(1,1,'Dulces', 'http//:www.Dulces.co', 'http//:www.Dulces.co/download', null, null, null, null, 1,1)");
      
            conn.commit();
      
       } catch (SQLException ex) {
           fine=false;
        }
      
      Assert.assertFalse(fine);
  }
  @Test
  public void solicitudesSinRespuestaTest(){
      Connection conn;
    try {
      conn = DriverManager.getConnection("jdbc:h2:file:./target/db/testdb;MODE=MYSQL", "sa", "");
      Statement stmt = conn.createStatement();
      stmt.execute("INSERT INTO LABORATORIO (ID_laboratorio, nombre, cantidad_equipos, videobeam) values(1,'Ingenieria De Software',20, true)");
      stmt.execute("INSERT INTO USUARIO (ID_usuario, nombre, email, tipo_usuario) values(1,'Tatiana','tatiana@mail.com', 1)");      
      stmt.execute("INSERT INTO SISTEMA_OPERATIVO (ID_sistema_operativo, nombre, version, Solicitud_id) values(1,'Windows','8.1', 1)");
      stmt.execute("INSERT INTO SOLICITUD (ID_solicitud, Laboratorio_id, Software, Link_licencia, Link_descarga, Estado, Fecha_posible_instalacion, Fecha_respuesta, Justificacion, Usuario_id, SISTEMA_OPERATIVO_ID_sistema_operativo)"
              + " values(1,1,'Dulces', 'http//:www.Dulces.co', 'http//:www.Dulces.co/download', null, null, null, null, 1,1)");
      stmt.execute("INSERT INTO SOFTWARE (ID_software, nombre, version) values(1,'glass','1')");
      
      conn.commit();
      } catch (SQLException ex) {
        Logger.getLogger(AppTest.class.getName()).log(Level.SEVERE, null, ex);
    }
     
      ServicesFacade sf = ServicesFacade.getInstance("h2-applicationconfig.properties");
      ArrayList<Solicitud> s =sf.loadAllSolicitud();
      boolean fine = true;
      for (Solicitud s1 : s) {
              if(!s1.getJustificacion().contentEquals(null) && fine){
                  fine = false;
              }
          }
      Assert.assertTrue(fine);
  }
} 
