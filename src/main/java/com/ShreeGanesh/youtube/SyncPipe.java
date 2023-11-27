package com.ShreeGanesh.youtube;

import java.io.InputStream;
import java.io.OutputStream;

class SyncPipe implements Runnable {
    SyncPipe(OutputStream ostrm, InputStream istrm) {
        ostrm_ = ostrm;
        istrm_ = istrm;
    }

    @Override
    public void run() {
        try {
            final byte[] buffer = new byte[1024];
            for (int length = 0; (length = istrm_.read(buffer)) != -1; ) {
                ostrm_.write(buffer, 0, length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final OutputStream ostrm_;
    private final InputStream istrm_;
}
