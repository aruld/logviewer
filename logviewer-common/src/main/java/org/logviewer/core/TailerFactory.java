package org.logviewer.core;

import java.io.IOException;
import java.net.URI;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.logviewer.tailer.LogTailerAbstract;
import org.logviewer.tailer.LogTailerCommons;
import org.logviewer.tailer.LogTailerSsh;
import org.logviewer.tailer.TailerCallback;
import org.logviewer.tailer.TailerSsh;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.JSch;

public class TailerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(TailerFactory.class);

    private final LogConfig logConfig;

    public TailerFactory(LogConfig logConfig) {
        this.logConfig = logConfig;
    }

    public LogTailerAbstract logLocalTailer(URI uri, TailerCallback listener) throws IOException {
        LOGGER.debug("uri: {}", uri);

        return new LogTailerCommons(listener, uri, logConfig.getExecutor());
    }

    public LogTailerAbstract logRemoteTailer(URI uri, String password, String passphrase, TailerCallback listener) throws IOException {
        LOGGER.debug("uri: {}", uri);

        Properties properties = new Properties(logConfig.getProperties());
        if (StringUtils.isNotEmpty(password)) {
            properties.put(LogTailerSsh.LOG_TAILER_SSH_PASSWORD_KEY, password);
        }
        if (StringUtils.isNotEmpty(passphrase)) {
            properties.put(LogTailerSsh.LOG_TAILER_SSH_PASSPHRASE_KEY, passphrase);
        }

        JSch jsch = new JSch();
        TailerSsh tailer = new TailerSsh(jsch, uri, listener, logConfig.getExecutor());
        return new LogTailerSsh(listener, uri, logConfig.getExecutor(), properties, tailer);
    }

}
