package io.github.michaelfedora.fedorasmarket.database;

/**
 * Created by Michael on 2/27/2016.
 */
public interface FmSerializable<D extends FmSerializedData> {

    D toData();
}