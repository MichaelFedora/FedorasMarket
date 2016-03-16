package io.github.michaelfedora.fedorasmarket.database;

import java.util.Optional;

/**
 * Created by Michael on 2/27/2016.
 */
public interface FmSerializedData<S> extends java.io.Serializable {

    Optional<S> safeDeserialize();

    S deserialize() throws BadDataException;
}
