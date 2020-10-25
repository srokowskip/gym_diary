package com.example.przemek.gymdiary.Interfaces;

public interface ObserveCollectionInterface<Tmodel> {

    public void onAdd(Tmodel object);

    public void onDelete(Tmodel object);

    public void onModify(Tmodel object);
}
