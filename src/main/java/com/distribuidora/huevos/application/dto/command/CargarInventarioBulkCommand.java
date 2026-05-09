package com.distribuidora.huevos.application.dto.command;

public class CargarInventarioBulkCommand {

    private int extra;   // canastas EXTRA a agregar (0 = no agregar)
    private int aa;      // canastas AA a agregar
    private int a;       // canastas A a agregar
    private int b;       // canastas B a agregar

    public CargarInventarioBulkCommand() {}

    public int getExtra() { return extra; }
    public void setExtra(int extra) { this.extra = extra; }

    public int getAa() { return aa; }
    public void setAa(int aa) { this.aa = aa; }

    public int getA() { return a; }
    public void setA(int a) { this.a = a; }

    public int getB() { return b; }
    public void setB(int b) { this.b = b; }
}
