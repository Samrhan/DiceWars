package com.dicewars.Controllers;

import com.dicewars.Models.CarteModel;
import com.dicewars.Views.CarteView;

public class CarteController {
    private CarteModel carteModel;
    private CarteView carteView;

    public CarteController(CarteModel carteModel, CarteView carteView) {
        this.carteModel = carteModel;
        this.carteView = carteView;
    }

    public void refresh() {
        carteView.repaint();
        carteView.revalidate();
    }
}
