package model;

public class ModeloGenerico<E, N, M, O, P> {

    private E objectoE;
    private N objectoN;
    private M objectoM;
    private O objectoO;
    private P objectoP;

    public ModeloGenerico() {
    }

    public E getObjectoE() {
        return objectoE;
    }

    public void setObjectoE(E objectoE) {
        this.objectoE = objectoE;
    }

    public N getObjectoN() {
        return objectoN;
    }

    public void setObjectoN(N objectoN) {
        this.objectoN = objectoN;
    }

    public M getObjectoM() {
        return objectoM;
    }

    public void setObjectoM(M objectoM) {
        this.objectoM = objectoM;
    }

    public O getObjectoO() {
        return objectoO;
    }

    public void setObjectoO(O objectoO) {
        this.objectoO = objectoO;
    }

    public P getObjectoP() {
        return objectoP;
    }

    public void setObjectoP(P objectoP) {
        this.objectoP = objectoP;
    }

    public void toMostrarInformacion() {
        System.out.println("E: " + objectoE);
        System.out.println("N: " + objectoN);
        System.out.println("M: " + objectoM);
        System.out.println("O: " + objectoO);
        System.out.println("P: " + objectoP);
    }

}
