package br.com.aula.cadeopovo.activity.entity;

/**
 * Created by jsoft-ti on 02/02/18.
 */

public class RegistroMovimentacao {

    public String latitude,longitude,timestamp;
    private String uid;

    public void setUid(String uid){
        this.uid = uid;
    }
    public RegistroMovimentacao(){

    }

    public RegistroMovimentacao(double latitude, double longitude, long timestamp) {
        this.latitude = String.valueOf(latitude);
        this.longitude = String.valueOf(longitude);
        this.timestamp = String.valueOf(timestamp);
    }
}
