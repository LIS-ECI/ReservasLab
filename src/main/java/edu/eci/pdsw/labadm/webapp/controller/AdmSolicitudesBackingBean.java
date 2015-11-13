/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.pdsw.labadm.webapp.controller;

import edu.eci.pdsw.labadm.entities.Solicitud;
import edu.eci.pdsw.labadm.services.ServicesFacade;
import edu.eci.pdsw.labadm.services.ServicesFacadeException;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author tatoo
 */
@ManagedBean (name = "beanAdmin")
@ViewScoped
public class AdmSolicitudesBackingBean{    
    private Solicitud solselc ; 
    private boolean resp;
    private Date fechaRealiz;
    private String justificacion;
    //Este atributo es para avisar al usuario que no es posible dar respuesta a la solicitud con los datos dados
    private String popUp;

    public AdmSolicitudesBackingBean() {
        justificacion="";
        fechaRealiz=new Date();
        resp= false;
        solselc = new Solicitud();
        popUp = "";
    }

 

    
    //trae todas las solicitudes que no han sido atendidas
    public List<Solicitud> getSolicitudes() throws ServicesFacadeException{
        return ServicesFacade.getInstance("config.properties").loadSolicitudSinResp();
    }
    
    public void nuevaRespuesta(){
        
        try {
            System.out.println("Guardoooo");
            if(resp){
                solselc.setEstado("aprobada");
            }else{
                solselc.setEstado("negada");
            }
            solselc.setJustificacion(justificacion);
            solselc.setFecha_posible(fechaRealiz);
            solselc.setFecha_resp(new Date());
            ServicesFacade.getInstance("config.properties").deleteSolicitud(solselc.getId());
            ServicesFacade.getInstance("config.properties").saveSolicitud(solselc);
        } catch (ServicesFacadeException ex) {
            ex.printStackTrace();
            popUp=ex.getMessage();
        }
    }

    public String getPopUp() {
        return popUp;
    }

    public void setPopUp(String popUp) {
        this.popUp = popUp;
    }
    
    public Solicitud getSolselc() {
        return solselc;
    }
    public void setSolselc(Solicitud solselc) {
        this.solselc = solselc;
    }
    
    public Date getFechaRealiz() {
        return fechaRealiz;
    }

    public void setFechaRealiz(Date fechaRealiz) {
        this.fechaRealiz = fechaRealiz;
    }

    public boolean isResp() {
        return resp;
    }

    public void setResp(boolean resp) {
        this.resp = resp;
    }
    
    public String getJustificacion() {
        return justificacion;
    }

    public void setJustificacion(String justificacion) {
        this.justificacion = justificacion;
    }
    
    public void addMessage() {
        String summary = resp ? "Solicitud Aprobada" : "Solicitud Negada";
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(summary));
        this.justificacion="";
        this.fechaRealiz=null;
    }
}
