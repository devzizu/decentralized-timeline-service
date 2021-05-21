package app.central;

import java.util.*;

import app.central.usernode.*;

import app.central.service.*;

public class Central {

    CentralService services;


    public Central() {
        this.services = new CentralService(); 
    }

}
