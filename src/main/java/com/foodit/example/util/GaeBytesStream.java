package com.foodit.example.util;

import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.io.BytesStream;
import org.elasticsearch.common.io.stream.StreamOutput;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * An implementation of {@link BytesStream} that works for Elastic Search. The default version that ships with Elastic Search doesn't work on Google App Engine.
 */
public class GaeBytesStream extends StreamOutput implements BytesStream {

    private ByteArrayOutputStream delegate;

    public GaeBytesStream(int expectedSize) {
        delegate = new ByteArrayOutputStream(expectedSize);
    }

    @Override
    public void writeByte(byte b) throws IOException {
        delegate.write(b);
    }

    @Override
    public void writeBytes(byte[] b, int offset, int length) throws IOException {
        delegate.write(b, offset, length);
    }

    public void reset() {
        delegate.reset();
    }

    @Override
    public void flush() throws IOException {
        delegate.flush();
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }

    public int size() {
        return delegate.size();
    }

    @Override
    public BytesReference bytes() {
        return new BytesArray(delegate.toByteArray());
    }
}
