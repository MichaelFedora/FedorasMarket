package io.github.michaelfedora.fedorasmarket.database;

/**
 * Created by Michael on 2/27/2016.
 */
public interface FmSerializedData<S extends FmSerializable> extends java.io.Serializable {

    S deserialize();
}
