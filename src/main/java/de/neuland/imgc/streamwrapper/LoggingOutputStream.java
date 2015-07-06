package de.neuland.imgc.streamwrapper;

import hudson.model.BuildListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LoggingOutputStream extends Thread{

    private final InputStream is;
    private final BuildListener listener;

    public LoggingOutputStream(final InputStream is, final BuildListener listener) {
        this.is = is;
        this.listener = listener;
    }

    public void run() {
        try {
            final BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ( (line = br.readLine()) != null) {
                listener.getLogger().println(line);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

}
