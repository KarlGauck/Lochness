package me.karl.lochness;

public abstract class Haustier {

    public abstract void essen();

}


class Katze extends Haustier {

    Haustier haustier = new Katze();
    Katze katze = (Katze) haustier;

    @Override
    public void essen() {

    }

}
