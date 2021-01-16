package com.dicewars.Controllers;

import com.dicewars.Models.MapModel;
import com.dicewars.Views.CarteView;

public class CarteController {
    private MapModel mapModel;
    private CarteView carteView;

    public CarteController(MapModel mapModel, CarteView carteView) {
        this.mapModel = mapModel;
        this.carteView = carteView;
    }

    public void refresh() {
        carteView.repaint();
        carteView.revalidate();
    }
}
