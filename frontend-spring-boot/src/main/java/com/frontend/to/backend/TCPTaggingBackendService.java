package com.frontend.to.backend;

import com.dynatrace.adk.DynaTraceADKFactory;
import com.dynatrace.adk.Tagging;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.Socket;

@Component
public class TCPTaggingBackendService extends TCPBackendService {

    @Override
    public String chatSync(final String name, final String message) throws IOException {
        try {
            DynaTraceADKFactory.initialize();
            Tagging tagging = DynaTraceADKFactory.createTagging();
            String requestTag = tagging.getTagAsString();
            tagging.linkClientPurePath(false, requestTag);

            try (Socket socket = connect()) {
                send(name + "|" + message + "|" + requestTag, socket);
                return receive(socket);
            }
        } finally {
            DynaTraceADKFactory.uninitialize();
        }
    }
}